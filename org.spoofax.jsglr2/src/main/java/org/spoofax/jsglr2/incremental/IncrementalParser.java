package org.spoofax.jsglr2.incremental;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.metaborg.util.iterators.Iterables2.stream;
import static org.spoofax.jsglr2.incremental.IncrementalParse.NO_STATE;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.metaborg.parsetable.IParseTable;
import org.metaborg.parsetable.actions.ActionType;
import org.metaborg.parsetable.actions.IAction;
import org.metaborg.parsetable.actions.IReduce;
import org.spoofax.jsglr2.incremental.actions.GotoShift;
import org.spoofax.jsglr2.incremental.diff.IStringDiff;
import org.spoofax.jsglr2.incremental.diff.JGitHistogramDiff;
import org.spoofax.jsglr2.incremental.lookaheadstack.ILookaheadStack;
import org.spoofax.jsglr2.incremental.parseforest.IncrementalDerivation;
import org.spoofax.jsglr2.incremental.parseforest.IncrementalParseForest;
import org.spoofax.jsglr2.incremental.parseforest.IncrementalParseNode;
import org.spoofax.jsglr2.parseforest.ParseForestManager;
import org.spoofax.jsglr2.parser.ParseFactory;
import org.spoofax.jsglr2.parser.Parser;
import org.spoofax.jsglr2.parser.result.ParseSuccess;
import org.spoofax.jsglr2.reducing.ReduceManagerFactory;
import org.spoofax.jsglr2.stack.AbstractStackManager;
import org.spoofax.jsglr2.stack.IStackNode;

public class IncrementalParser
// @formatter:off
   <ParseNode     extends IncrementalParseNode,
    Derivation    extends IncrementalDerivation,
    StackNode     extends IStackNode,
    Parse         extends IncrementalParse<StackNode>,
    StackManager  extends AbstractStackManager<IncrementalParseForest, StackNode, Parse>,
    ReduceManager extends org.spoofax.jsglr2.reducing.ReduceManager<
                              IncrementalParseForest, ParseNode, Derivation, StackNode, Parse>>
// @formatter:on
    extends Parser<IncrementalParseForest, ParseNode, Derivation, StackNode, Parse, StackManager, ReduceManager> {

    private final IncrementalParseFactory<StackNode, Parse> incrementalParseFactory;
    private final HashMap<String, IncrementalParseForest> cache = new HashMap<>();
    private final HashMap<String, String> oldString = new HashMap<>();
    private final IStringDiff diff;

    public IncrementalParser(ParseFactory<IncrementalParseForest, StackNode, Parse> parseFactory,
        IncrementalParseFactory<StackNode, Parse> incrementalParseFactory, IParseTable parseTable,
        StackManager stackManager,
        ParseForestManager<IncrementalParseForest, ParseNode, Derivation, Parse> parseForestManager,
        ReduceManagerFactory<IncrementalParseForest, ParseNode, Derivation, StackNode, Parse, StackManager, ReduceManager> reduceManagerFactory) {

        super(parseFactory, parseTable, stackManager, parseForestManager, reduceManagerFactory);
        this.incrementalParseFactory = incrementalParseFactory;
        // TODO parametrize parser on diff algorithm for benchmarking
        this.diff = new JGitHistogramDiff();
    }

    @Override protected Parse getParse(String inputString, String filename) {
        if(!filename.equals("") && cache.containsKey(filename) && oldString.containsKey(filename)) {
            List<EditorUpdate> updates = diff.diff(oldString.get(filename), inputString);
            return incrementalParseFactory.get(updates, cache.get(filename), inputString, filename, observing);
        } else
            return super.getParse(inputString, filename);
    }

    @Override protected ParseSuccess<IncrementalParseForest> success(Parse parse, IncrementalParseForest parseForest) {
        // On success, save result (if filename != "")
        if(!parse.filename.equals("")) {
            oldString.put(parse.filename, parse.inputString);
            cache.put(parse.filename, parseForest);
        }
        return super.success(parse, parseForest);
    }

    @Override protected void actor(StackNode stack, Parse parse) {
        Iterable<IAction> actions = getActions(stack, parse);
        // Break down lookahead if it has no state or if there are no actions for it.
        // Only break down if the lookahead is not a terminal.
        // If there are no actions, do not break down if we already have something to shift.
        // This node that we can shift should not be broken down anymore:
        // - if we would, it would cause different shifts to be desynchronised;
        // - if a break-down of this node would cause different actions, it would already have been broken down because
        // that would mean that this node was created when the parser was in multiple states.
        while(!parse.lookahead.get().isTerminal()
            && (lookaheadHasNoState(parse.lookahead) || isEmpty(actions) && parse.forShifter.isEmpty())) {
            parse.lookahead.leftBreakdown();
            actions = getActions(stack, parse);
        }

        if(size(actions) > 1)
            parse.setMultipleStates(true);

        Iterable<IAction> finalActions = actions;
        observing.notify(observer -> observer.actor(stack, parse, finalActions));

        for(IAction action : actions)
            actor(stack, parse, action);
    }

    private boolean lookaheadHasNoState(ILookaheadStack lookahead) {
        return ((IncrementalParseNode) lookahead.get()).getFirstDerivation().state.equals(NO_STATE);
    }

    // Inside this method, we can assume that the lookahead is a valid and complete subtree of the previous parse.
    // Else, the loop in `actor` will have broken it down
    private Iterable<IAction> getActions(StackNode stack, Parse parse) {
        // Get actions based on the lookahead terminal that `parse` will calculate in actionQueryCharacter
        Iterable<IAction> actions = stack.state().getApplicableActions(parse);

        IncrementalParseForest lookahead = parse.lookahead.get();
        if(lookahead.isTerminal()) {
            return actions;
        } else {
            // Split in shift and reduce actions
            List<IAction> shiftActions =
                stream(actions).filter(a -> a.actionType() == ActionType.SHIFT).collect(Collectors.toList());

            // By default, only the reduce actions are returned
            List<IAction> result = stream(actions)
                .filter(a -> a.actionType() == ActionType.REDUCE || a.actionType() == ActionType.REDUCE_LOOKAHEAD)
                .collect(Collectors.toList());

            IncrementalParseNode lookaheadNode = (IncrementalParseNode) lookahead;

            // Only allow shifting the subtree if the saved state matches the current state
            boolean hasGotoShift = false;
            for(IncrementalDerivation derivation : lookaheadNode.getDerivations()) {
                if(stack.state().id() == derivation.state.id()) {
                    result.add(new GotoShift(stack.state().getGotoId(derivation.production().id())));
                    hasGotoShift = true;
                }
            }

            // If we don't have a GotoShift action, but do have regular shift actions, we should break down further
            if(!hasGotoShift && !shiftActions.isEmpty()) {
                return Collections.emptyList(); // Return no actions, to trigger breakdown
            }

            // If lookahead has null yield and the production of lookahead matches the state of the GotoShift,
            // there is a duplicate action that can be removed (this is an optimization to avoid multipleStates == true)
            if(!lookaheadNode.isAmbiguous() && lookaheadNode.width() == 0 && result.size() == 2 && hasGotoShift
                && nullReduceMatchesGotoShift(stack, (IReduce) result.get(0), (GotoShift) result.get(1))) {
                result.remove(0); // Removes the unnecessary reduce action
            }

            return result;
        }
    }

    // If the lookahead has null yield, there are always at least two valid actions:
    // Either reduce a production with arity 0, or shift the already-existing null-yield subtree.
    // This method returns whether the Goto state of the Reduce action matches the state of the GotoShift action.
    private boolean nullReduceMatchesGotoShift(StackNode stack, IReduce reduceAction, GotoShift gotoShiftAction) {
        return stack.state().getGotoId(reduceAction.production().id()) == gotoShiftAction.shiftStateId();
    }

    @Override protected IncrementalParseForest getNodeToShift(Parse parse) {
        parse.setMultipleStates(parse.forShifter.size() > 1);

        return parse.lookahead.get();
    }
}
