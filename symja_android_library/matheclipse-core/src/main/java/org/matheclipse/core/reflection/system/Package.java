package org.matheclipse.core.reflection.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Package[{&lt;list of public package rule headers&gt;}, {&lt;list of rules in this package&gt;}}
 * 
 */
public class Package implements IFunctionEvaluator {

	public Package() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);

		if (!(ast.arg1() instanceof IStringX) || !ast.arg2().isList() || !ast.arg3().isList()) {
			throw new WrongNumberOfArguments(ast, 1, ast.size() - 1);
		}
		if (Config.SERVER_MODE) {
			throw new RuleCreationError(null);
		}
		IAST symbols = (IAST) ast.arg2();
		IAST list = (IAST) ast.arg3();
		evalPackage(symbols, list);
		// System.out.println(resultList);
		return F.Null;
	}

	public static void evalPackage(IAST publicSymbols, IAST list) {
		HashMap<ISymbol, ISymbol> convertedSymbolMap = new HashMap<ISymbol, ISymbol>();
		HashSet<ISymbol> publicSymbolSet = new HashSet<ISymbol>();

		ISymbol toSymbol;
		for (int i = 1; i < publicSymbols.size(); i++) {
			IExpr expr = publicSymbols.get(i);
			if (expr.isSymbol()) {
				publicSymbolSet.add((ISymbol) expr);
				toSymbol = F.predefinedSymbol(((ISymbol) expr).toString());
				convertedSymbolMap.put((ISymbol) expr, toSymbol);
			}
		}

		// determine "private package rule headers" in convertedSymbolMap
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i).isAST()) {
				determineRuleHead((IAST) list.get(i), publicSymbolSet, convertedSymbolMap);
			}
		}

		// convert the rules into a new list:
		IAST resultList = F.List();
		for (int i = 1; i < list.size(); i++) {
			resultList.add(convertSymbolsInExpr(list.get(i), convertedSymbolMap));
		}
		EvalEngine engine = EvalEngine.get();
		try {
			engine.setPackageMode(true);
			// evaluate the new converted rules
			for (int i = 1; i < resultList.size(); i++) {
				EvalEngine.eval(resultList.get(i));
			}
		} finally {
			engine.setPackageMode(false);
		}
	}

	/**
	 * Determine the head symbol of the given rule
	 * 
	 * @param rule
	 * @param unprotectedSymbolSet
	 * @param convertedSymbolMap
	 */
	private static void determineRuleHead(IAST rule, HashSet<ISymbol> unprotectedSymbolSet,
			HashMap<ISymbol, ISymbol> convertedSymbolMap) {
		ISymbol lhsHead;
		if (rule.size() > 1
				&& (rule.head().equals(F.Set) || rule.head().equals(F.SetDelayed) || rule.head().equals(F.UpSet) || rule.head()
						.equals(F.UpSetDelayed))) {
			// determine the head to which this rule is associated
			lhsHead = null;
			if (rule.arg1().isAST()) {
				lhsHead = ((IAST) rule.arg1()).topHead();
			} else if (rule.arg1().isSymbol()) {
				lhsHead = (ISymbol) rule.arg1();
			}

			if (lhsHead != null && !unprotectedSymbolSet.contains(lhsHead)) {
				ISymbol toSymbol = convertedSymbolMap.get(lhsHead);
				if (toSymbol == null) {
					// define a package private symbol
					toSymbol = F.predefinedSymbol("@" + EvalEngine.getNextCounter() + lhsHead.toString());
					convertedSymbolMap.put(lhsHead, toSymbol);
				}
			}

		}
	}

	/**
	 * Convert all symbols which are keys in <code>convertedSymbols</code> in the given <code>expr</code> and return the resulting
	 * expression.
	 * 
	 * @param expr
	 * @param convertedSymbols
	 * @return
	 */
	private static IExpr convertSymbolsInExpr(IExpr expr, HashMap<ISymbol, ISymbol> convertedSymbols) {
		IExpr result = expr;
		if (expr.isAST()) {
			result = convertSymbolsInList((IAST) expr, convertedSymbols);
		} else if (expr.isSymbol()) {
			ISymbol toSymbol = convertedSymbols.get((ISymbol) expr);
			if (toSymbol != null) {
				result = toSymbol;
			}
		}

		return result;
	}

	/**
	 * Convert all symbols which are keys in <code>convertedSymbols</code> in the given <code>ast</code> list and return the
	 * resulting list.
	 * 
	 * @param ast
	 * @param convertedSymbols
	 * @return
	 */
	private static IAST convertSymbolsInList(IAST ast, HashMap<ISymbol, ISymbol> convertedSymbols) {
		IAST result = ast.clone();
		for (int i = 0; i < result.size(); i++) {
			IExpr expr = result.get(i);
			if (expr.isAST()) {
				result.set(i, convertSymbolsInList((IAST) expr, convertedSymbols));
			} else if (expr.isSymbol()) {
				ISymbol toSymbol = convertedSymbols.get((ISymbol) expr);
				if (toSymbol != null) {
					result.set(i, toSymbol);
				}
			}
		}

		return result;
	}

	public IExpr numericEval(IAST functionList) {
		return null;
	}

	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

	/**
	 * Load a package from the given reader
	 * 
	 * @param is
	 */
	public static void loadPackage(final EvalEngine engine, final Reader is) {
		String record = null;
		final BufferedReader r = new BufferedReader(is);// new
		try {
			StringBuilder builder = new StringBuilder(2048);
			while ((record = r.readLine()) != null) {
				builder.append(record);
				builder.append('\n');
			}

			IExpr parsedExpression = engine.parse(builder.toString());
			if (parsedExpression != null && parsedExpression.isAST()) {
				IAST ast = (IAST) parsedExpression;
				if (ast.size() != 4 || !(ast.arg1() instanceof IStringX) || !ast.arg2().isList() || !ast.arg3().isList()) {
					throw new WrongNumberOfArguments(ast, 3, ast.size() - 1);
				}
				IAST symbols = (IAST) ast.arg2();
				IAST list = (IAST) ast.arg3();
				evalPackage(symbols, list);
			}

		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			try {
				r.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
