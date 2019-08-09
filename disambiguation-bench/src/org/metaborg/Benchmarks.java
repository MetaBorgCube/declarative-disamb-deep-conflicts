package org.metaborg;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.metaborg.sdf2table.grammar.IProduction;
import org.metaborg.sdf2table.io.NormGrammarReader;
import org.metaborg.sdf2table.parsetable.ParseTable;
import org.metaborg.sdf2table.parsetable.ParseTable.DisambSemantics;
import org.metaborg.sdf2table.parsetable.ParseTableConfiguration;

import com.google.common.collect.Lists;

public class Benchmarks {

	private final static String[] languages = { "Tiger", "IceDust", "Jasmin", "Java", "OCaml" };

	public static void main(String[] args) throws Exception {

		PrintWriter writer = new PrintWriter("results.txt", "UTF-8");

		printMessage(writer, "################# BENCHMARK #################");

		for (String lang : languages) {
			List<String> paths = Lists.newLinkedList();
			List<String> origPaths = Lists.newLinkedList();
			Path currentPath = Paths.get("").toAbsolutePath();
			String pathToSyntax = currentPath.toString() + "/norm-grammars/" + lang + "/syntax";
			String pathToOrigSyntax = currentPath.toString() + "/orig-grammars/" + lang + "/syntax";
			paths.add(pathToSyntax);
			origPaths.add(pathToOrigSyntax);
			File input = new File(pathToSyntax + "/normalized/" + lang + "-norm.aterm");
			File origInput = new File(pathToOrigSyntax + "/normalized/" + lang + "-norm.aterm");

			ParseTableConfiguration configNoDeepConflicts = new ParseTableConfiguration(false, false, false, false,
					false);

			printMessage(writer, "\n\nGenerating parse table for language: " + lang);

			ParseTable ptOldSemantics = new ParseTable(new NormGrammarReader(paths).readGrammar(input),
					configNoDeepConflicts, DisambSemantics.SDF2_SEMANTICS);
			ParseTable ptNewSemantics = new ParseTable(new NormGrammarReader(paths).readGrammar(input),
					configNoDeepConflicts, DisambSemantics.SDF3_SEMANTICS);

			printMessage(writer, "Original Grammar");

			int numberOfProductions = +new NormGrammarReader(origPaths).readGrammar(origInput)
					.getUniqueProductionMapping().size();

			printMessage(writer, "Productions : " + numberOfProductions);

			printMessage(writer, "\nNormalized Grammar");
			printMessage(writer, "Productions     : " + ptOldSemantics.productionLabels().size());
			printMessage(writer, "States (unsafe) : " + ptOldSemantics.stateLabels().size());
			printMessage(writer, "States (safe)   : " + ptNewSemantics.stateLabels().size());

			// expression grammars in both safe and unsafe semantics should be the same
			assert (ptOldSemantics.normalizedGrammar().getExpressionGrammars()
					.equals(ptNewSemantics.normalizedGrammar().getExpressionGrammars()));

			printMessage(writer, "\nExpression Grammar");

			int numberOfExpressionGrammars = ptNewSemantics.normalizedGrammar().getCombinedExpressionGrammars().size();

			printMessage(writer, "Quantity     : " + numberOfExpressionGrammars);

			int largestSize = 0;

			for (Set<IProduction> prods : ptNewSemantics.normalizedGrammar().getCombinedExpressionGrammars()) {
				if (prods.size() > largestSize) {
					largestSize = prods.size();
				}
			}

			int numberOfInitialPriorities = new NormGrammarReader(paths).readGrammar(input).getInputPriorities();
			int numberOfFinalPriorities = (ptNewSemantics.normalizedGrammar().priorities().keySet().size() - 1);

			printMessage(writer, "Largest Size : " + largestSize);
			printMessage(writer, "\nDisambiguation Rules");
			printMessage(writer, "Input              : " + numberOfInitialPriorities);
			// -1 to not count the priority involving the non-terminal LAYOUT-CF generated
			// by SDF3
			printMessage(writer, "Transitive Closure : " + numberOfFinalPriorities);

			ParseTableConfiguration configDeepConflicts = new ParseTableConfiguration(false, false, true, false, false);
			ParseTableConfiguration configDataDependent = new ParseTableConfiguration(false, true, true, false, false);

			printMessage(writer, "\nContextual Grammar");

			ParseTable ptContextualGrammar = new ParseTable(new NormGrammarReader(paths).readGrammar(input),
					configDeepConflicts, DisambSemantics.SDF3_SEMANTICS);
			ParseTable ptDDContextualGrammar = new ParseTable(new NormGrammarReader(paths).readGrammar(input),
					configDataDependent, DisambSemantics.SDF3_SEMANTICS);

			// Number of productions and states of data-dependent contextual grammar should
			// be the same
			assert (ptDDContextualGrammar.productionLabels().size() == ptNewSemantics.productionLabels().size());
			assert (ptDDContextualGrammar.stateLabels().size() == ptNewSemantics.stateLabels().size());

			int productionsDeepConflicts = ptContextualGrammar.normalizedGrammar().getProdContextualProdMapping()
					.size();
			int productionsDeepConflictsClosure = ptContextualGrammar.normalizedGrammar().getDerivedContextualProds()
					.size();

			printMessage(writer, "Productions (with deep conflicts)             : " + productionsDeepConflicts);
			printMessage(writer, "Productions (generated by contextual closure) : " + productionsDeepConflictsClosure);
			printMessage(writer,
					"Productions (total)                           : " + ptContextualGrammar.productionLabels().size());
			printMessage(writer,
					"States                                        : " + ptContextualGrammar.stateLabels().size());

			printMessage(writer, "-----------------------------------------------------");
		}

		writer.close();
	}

	private static void printMessage(PrintWriter writer, String message) {
		System.out.println(message);
		writer.println(message);
	}

}