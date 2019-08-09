package org.spoofax.jsglr2.parseforest;

import org.metaborg.parsetable.productions.IProduction;
import org.metaborg.parsetable.productions.ProductionType;
import org.spoofax.jsglr2.parser.AbstractParse;
import org.spoofax.jsglr2.stack.IStackNode;

public abstract class ParseForestManager
//@formatter:off
   <ParseForest extends IParseForest,
    ParseNode   extends ParseForest,
    Derivation  extends IDerivation<ParseForest>,
    Parse       extends AbstractParse<ParseForest, ?>>
//@formatter:on
{

    /**
     * @param stack
     *            The parse node will be added to the link _to_ this stack node.
     */
    abstract public ParseNode createParseNode(Parse parse, IStackNode stack, IProduction production,
        Derivation firstDerivation);

    /**
     * @param stack
     *            The derivation will be added to the parse node on the link _to_ this stack node.
     */
    abstract public Derivation createDerivation(Parse parse, IStackNode stack, IProduction production,
        ProductionType productionType, ParseForest[] parseForests);

    abstract public void addDerivation(Parse parse, ParseNode parseNode, Derivation derivation);

    abstract public ParseForest createCharacterNode(Parse parse);

    abstract public ParseForest[] parseForestsArray(int length);

    abstract public ParseForest filterStartSymbol(ParseForest parseForest, String startSymbol, Parse parse);

}
