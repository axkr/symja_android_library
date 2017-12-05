package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.RuleDelayed;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.List;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ICreatePatternMatcher;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ISymbol.RuleType;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

public final class PatternMatching {

	static {

		F.Clear.setEvaluator(new Clear());
		F.ClearAll.setEvaluator(new ClearAll());
		F.Get.setEvaluator(new Get());
		F.Information.setEvaluator(new Information());
		F.MessageName.setEvaluator(new MessageName());
		F.Optional.setEvaluator(new Optional());
		F.Rule.setEvaluator(new Rule());
		F.RuleDelayed.setEvaluator(new RuleDelayed());
		F.Set.setEvaluator(new Set());
		F.SetDelayed.setEvaluator(new SetDelayed());
		F.Unset.setEvaluator(new Unset());
		F.UpSet.setEvaluator(new UpSet());
		F.UpSetDelayed.setEvaluator(new UpSetDelayed());
	}

	/**
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Clear">Clear</a>
	 * </p>
	 */
	private static class Clear extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2);
			Lambda.forEach(ast, x -> x.isSymbol(), x -> ((ISymbol) x).clear(engine));
			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/ClearAll">ClearAll</a>
	 * </p>
	 */
	private final static class ClearAll extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			ISymbol symbol = Validate.checkSymbolType(ast, 1);
			symbol.clearAll(engine);
			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * Get[{&lt;file name&gt;}}
	 * 
	 */
	private static class Get extends AbstractFunctionEvaluator {

		private static int addContextToPath(ContextPath contextPath, final List<ASTNode> node, int i,
				final EvalEngine engine, ISymbol endSymbol) {
			ContextPath path = engine.getContextPath();
			try {
				engine.setContextPath(contextPath);
				AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
				while (i < node.size()) {
					IExpr temp = ast2Expr.convert(node.get(i++));
					if (temp.isAST()) {
						IExpr head = temp.head();
						IAST ast = (IAST) temp;
						if (head.equals(endSymbol) && ast.isAST0()) {
							continue;
						} else if (head.equals(F.Begin) && ast.size() >= 2) {
							try {
								contextPath.add(engine.getContextPath().getContext(ast.arg1().toString()));
								i = addContextToPath(contextPath, node, i, engine, F.End);
							} finally {
								contextPath.remove(contextPath.size() - 1);
							}
							continue;
						}
					}
					engine.evaluate(temp);
				}
				// TODO add error message
			} finally {
				engine.setContextPath(path);
			}
			return i;
		}

		/**
		 * Load a package from the given reader
		 * 
		 * @param engine
		 * @param is
		 * @return the last evaluated expression result
		 */
		public static IExpr loadPackage(final EvalEngine engine, final Reader is) {
			final BufferedReader r = new BufferedReader(is);
			Context packageContext = null;
			try {
				final List<ASTNode> node = parseReader(r, engine);

				IExpr temp;
				int i = 0;
				AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
				IExpr result = F.Null;
				while (i < node.size()) {
					temp = ast2Expr.convert(node.get(i++));
					if (temp.isAST()) {
						IAST ast = (IAST) temp;
						IExpr head = temp.head();
						if (head.equals(F.BeginPackage) && ast.size() >= 2) {
							String contextName = Validate.checkContextName(ast, 1);
							packageContext = engine.getContextPath().getContext(contextName);
							ISymbol endSymbol = F.EndPackage;
							for (int j = 2; j < ast.size(); j++) {
								FileReader reader = new FileReader(ast.get(j).toString());
								Get.loadPackage(engine, reader);
								reader.close();
							}
							i = addContextToPath(new ContextPath(packageContext), node, i, engine, endSymbol);
							continue;
						} else if (head.equals(F.Begin) && ast.size() >= 2) {
							String contextName = Validate.checkContextName(ast, 1);
							ISymbol endSymbol = F.End;
							i = addContextToPath(new ContextPath(contextName), node, i, engine, endSymbol);
							continue;
						}
					}
					result = engine.evaluate(temp);
				}
				return result;
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				if (packageContext != null) {
					engine.getContextPath().add(packageContext);
				}
				try {
					r.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return F.Null;
		}

		/**
		 * <p>
		 * Parse the <code>reader</code> input.
		 * </p>
		 * <p>
		 * This method ignores the first line of the script if it starts with the <code>#!</code> characters (i.e. Unix
		 * Script Executables)
		 * </p>
		 * <p>
		 * <b>Note</b>: uses the <code>ASTNode</code> parser and not the <code>ExprParser</code>, because otherwise the
		 * symbols couldn't be assigned to the contexts.
		 * </p>
		 * 
		 * @param reader
		 * @param engine
		 * @return
		 * @throws IOException
		 */
		public static List<ASTNode> parseReader(final BufferedReader reader, final EvalEngine engine)
				throws IOException {
			String record;
			StringBuilder builder = new StringBuilder(2048);
			if ((record = reader.readLine()) != null) {
				// ignore the first line of the script if it starts with the #!
				// characters (i.e. Unix Script Executables)
				if (!record.startsWith("!#")) {
					builder.append(record);
					builder.append('\n');
				}
			}
			while ((record = reader.readLine()) != null) {
				builder.append(record);
				builder.append('\n');
			}
			final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
			final List<ASTNode> node = parser.parsePackage(builder.toString());
			return node;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (!(ast.arg1() instanceof IStringX)) {
				throw new WrongNumberOfArguments(ast, 1, ast.size() - 1);
			}
			IStringX arg1 = (IStringX) ast.arg1();
			FileReader reader;
			try {
				reader = new FileReader(arg1.toString());
				return loadPackage(engine, reader);
			} catch (FileNotFoundException e) {
				engine.printMessage("Get: file " + arg1.toString() + " not found!");
			}
			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private static class Information extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			ISymbol symbol = null;
			if (!ast.arg1().isSymbol()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (!arg1.isSymbol()) {
					throw new WrongArgumentType(ast, ast.arg1(), 1, "");
				}
				symbol = (ISymbol) arg1;
			} else {
				symbol = (ISymbol) ast.arg1();
			}
			final PrintStream s = engine.getOutPrintStream();
			final PrintStream stream;
			if (s == null) {
				stream = System.out;
			} else {
				stream = s;
			}
//			IExpr temp = engine.evaluate(F.MessageName(symbol, F.usage));
//			if (temp.isPresent()) {
//				stream.println(temp.toString());
//			}
			if (!Documentation.printDocumentation(stream, symbol.getSymbolName())) {

			}
			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * MessageName[{&lt;file name&gt;}}
	 * 
	 */
	private static class MessageName extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			// Here we only validate the arguments
			// Assignment of the message is handled in the Set() function
			if (!ast.arg1().isSymbol()) {
				throw new WrongArgumentType(ast, ast.arg1(), 1, "");
			}
			// if (!ast.arg2().isAST(F.Set, 3)) {
			// throw new WrongArgumentType(ast, ast.arg2(), 2, "");
			// }
			IExpr arg2 = engine.evaluate(ast.arg2());
			if (arg2 instanceof IStringX || arg2.isSymbol()) {
				return F.NIL;
			}

			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private static class Optional extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);
			if (ast.arg1().isPattern()) {
				IPattern patt = (IPattern) ast.arg1();
				return F.$p(patt.getSymbol(), patt.getCondition(), ast.arg2());
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Rule extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr leftHandSide = ast.arg1();
			if (leftHandSide.isAST()) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			} else {
				leftHandSide = engine.evaluate(leftHandSide);
			}
			IExpr arg2 = engine.evaluateNull(ast.arg2());
			if (!arg2.isPresent()) {
				if (leftHandSide.equals(ast.arg1())) {
					return F.NIL;
				}
				return Rule(leftHandSide, ast.arg2());
			}
			return Rule(leftHandSide, arg2);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class RuleDelayed extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr leftHandSide = ast.arg1();
			if (leftHandSide.isAST()) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			} else {
				leftHandSide = engine.evaluate(leftHandSide);
			}
			if (!leftHandSide.equals(ast.arg1())) {
				return RuleDelayed(leftHandSide, ast.arg2());
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Set extends AbstractCoreFunctionEvaluator implements ICreatePatternMatcher {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			final IExpr leftHandSide = ast.arg1();
			IExpr rightHandSide = ast.arg2();
			if (leftHandSide.isAST()) {
				IAST leftHandSideAST = (IAST) leftHandSide;
				if (leftHandSideAST.isAST(F.Part) && leftHandSideAST.size() > 1) {
					IAST part = leftHandSideAST;
					if (part.arg1().isSymbol()) {
						ISymbol symbol = (ISymbol) part.arg1();
						RulesData rd = symbol.getRulesData();
						if (rd == null) {
							engine.printMessage("Set: no value defined for symbol '" + symbol.toString()
									+ "' in Part() expression.");
						} else {
							try {
								IExpr temp = symbol.getRulesData().evalDownRule(symbol, engine);
								if (!temp.isPresent()) {
									engine.printMessage("Set: no value defined for symbol '" + symbol.toString()
											+ "' in Part() expression.");
								} else {
									if (rightHandSide.isList()) {
										IExpr res = Programming.assignPart(temp, part, 2, (IAST) rightHandSide, 1,
												engine);
										symbol.putDownRule(RuleType.SET, true, symbol, res, false);
										return rightHandSide;
									} else {
										IExpr res = Programming.assignPart(temp, part, 2, rightHandSide, engine);
										symbol.putDownRule(RuleType.SET, true, symbol, res, false);
										return rightHandSide;
									}
								}
							} catch (RuntimeException npe) {
								engine.printMessage("Set: wrong argument for Part[] function: " + part.toString()
										+ " selects no part expression.");
							}
						}
					}

				} else if (leftHandSideAST.isList()) {
					// thread over lists
					try {
						rightHandSide = engine.evaluate(rightHandSide);
					} catch (final ReturnException e) {
						rightHandSide = e.getValue();
					}
					IExpr temp = engine.threadASTListArgs((IASTMutable) F.Set(leftHandSideAST, rightHandSide));
					if (temp.isPresent()) {
						return engine.evaluate(temp);
					}
					return F.NIL;
				} else if (leftHandSideAST.isAST(F.Attributes, 2)) {
					IAST symbolList = Validate.checkSymbolOrSymbolList(leftHandSideAST, 1);
					symbolList.forEach(x -> ((ISymbol) x).setAttributes(ISymbol.NOATTRIBUTE));
					return AttributeFunctions.setSymbolsAttributes(ast, engine, symbolList);
				}
			}
			Object[] result;
			result = createPatternMatcher(leftHandSide, rightHandSide, engine.isPackageMode(), engine);
			return (IExpr) result[1];
		}

		@Override
		public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				final EvalEngine engine) throws RuleCreationError {

			if (leftHandSide.isAST()) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			}
			try {
				rightHandSide = engine.evaluate(rightHandSide);
			} catch (final ConditionException e) {
				// System.out.println("Condition[] in right-hand-side of Set[]");
			} catch (final ReturnException e) {
				rightHandSide = e.getValue();
			}

			return setDownRule(leftHandSide, rightHandSide, packageMode);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class SetDelayed extends AbstractCoreFunctionEvaluator implements ICreatePatternMatcher {

		// public final static SetDelayed CONST = new SetDelayed();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			final IExpr leftHandSide = ast.arg1();
			final IExpr rightHandSide = ast.arg2();

			createPatternMatcher(leftHandSide, rightHandSide, engine.isPackageMode(), engine);

			return F.Null;
		}

		@Override
		public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				final EvalEngine engine) throws RuleCreationError {
			if (leftHandSide.isAST()
					&& (((IAST) leftHandSide).getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) == IAST.NO_FLAG) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			}
			return setDelayedDownRule(leftHandSide, rightHandSide, packageMode);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	public static Object[] setDownRule(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) {
		final Object[] result = new Object[] { null, rightHandSide };
		if (leftHandSide.isAST()) {
			final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();
			result[0] = lhsSymbol.putDownRule(ISymbol.RuleType.SET, false, leftHandSide, rightHandSide, packageMode);
			return result;
		}
		if (leftHandSide.isSymbol()) {
			final ISymbol lhsSymbol = (ISymbol) leftHandSide;

			if (lhsSymbol.hasLocalVariableStack()) {
				lhsSymbol.set(rightHandSide);
				return result;
			}
			result[0] = lhsSymbol.putDownRule(ISymbol.RuleType.SET, true, leftHandSide, rightHandSide, packageMode);
			return result;
		}

		throw new RuleCreationError(leftHandSide);
	}

	public static Object[] setDelayedDownRule(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) {
		final Object[] result = new Object[] { null, rightHandSide };
		if (leftHandSide.isAST()) {
			final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();

			result[0] = lhsSymbol.putDownRule(ISymbol.RuleType.SET_DELAYED, false, leftHandSide, rightHandSide,
					packageMode);
			return result;
		}
		if (leftHandSide.isSymbol()) {
			final ISymbol lhsSymbol = (ISymbol) leftHandSide;
			if (lhsSymbol.hasLocalVariableStack()) {
				lhsSymbol.set(rightHandSide);
				return result;
			}
			result[0] = lhsSymbol.putDownRule(ISymbol.RuleType.SET_DELAYED, true, leftHandSide, rightHandSide,
					packageMode);
			return result;
		}
		throw new RuleCreationError(leftHandSide);
	}

	private final static class Unset extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			final IExpr leftHandSide = ast.arg1();
			if (leftHandSide.isList()) {
				// thread over lists
				IExpr temp = engine.threadASTListArgs((IASTMutable) F.Unset(leftHandSide));
				if (temp.isPresent()) {
					return engine.evaluate(temp);
				}
			}
			removePatternMatcher(leftHandSide, engine.isPackageMode(), engine);
			return F.Null;
		}

		public void removePatternMatcher(IExpr leftHandSide, boolean packageMode, EvalEngine engine)
				throws RuleCreationError {

			if (leftHandSide.isAST()) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			}
			removeRule(leftHandSide, packageMode);
		}

		public void removeRule(IExpr leftHandSide, boolean packageMode) {
			if (leftHandSide.isAST()) {
				final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();
				if (!lhsSymbol.removeRule(ISymbol.RuleType.SET, false, leftHandSide, packageMode)) {
					printAssignmentNotFound(leftHandSide);
				}
				return;
			}
			if (leftHandSide.isSymbol()) {
				final ISymbol lhsSymbol = (ISymbol) leftHandSide;

				if (!lhsSymbol.removeRule(ISymbol.RuleType.SET, true, leftHandSide, packageMode)) {
					printAssignmentNotFound(leftHandSide);
				}
				return;
			}

			throw new RuleCreationError(leftHandSide);
		}

		private void printAssignmentNotFound(final IExpr leftHandSide) {
			EvalEngine.get().printMessage("Assignment not found for: " + leftHandSide.toString());
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class UpSet extends AbstractCoreFunctionEvaluator implements ICreatePatternMatcher {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			final IExpr leftHandSide = ast.arg1();
			IExpr rightHandSide = ast.arg2();
			if (leftHandSide.isList()) {
				// thread over lists
				try {
					rightHandSide = engine.evaluate(rightHandSide);
				} catch (final ReturnException e) {
					rightHandSide = e.getValue();
				}
				IExpr temp = engine.threadASTListArgs((IASTMutable) F.UpSet(leftHandSide, rightHandSide));
				if (temp.isPresent()) {
					return engine.evaluate(temp);
				}
			}
			Object[] result = createPatternMatcher(leftHandSide, rightHandSide, false, engine);
			return (IExpr) result[1];
		}

		@Override
		public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				EvalEngine engine) throws RuleCreationError {
			final Object[] result = new Object[2];

			if (leftHandSide.isAST()) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			}
			try {
				rightHandSide = engine.evaluate(rightHandSide);
			} catch (final ConditionException e) {
				System.out.println("Condition[] in right-hand-side of UpSet[]");
			} catch (final ReturnException e) {
				rightHandSide = e.getValue();
			}

			result[0] = null; // IPatternMatcher
			result[1] = rightHandSide;

			IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);
			for (int i = 1; i < lhsAST.size(); i++) {
				IExpr temp = lhsAST.get(i);
				if (temp instanceof IPatternObject) {
					continue;
				}
				ISymbol lhsSymbol = null;
				if (temp.isSymbol()) {
					lhsSymbol = (ISymbol) temp;
				} else {
					lhsSymbol = lhsAST.get(i).topHead();
				}
				result[0] = lhsSymbol.putUpRule(ISymbol.RuleType.UPSET, false, lhsAST, rightHandSide);
			}
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class UpSetDelayed extends AbstractCoreFunctionEvaluator implements ICreatePatternMatcher {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			final IExpr leftHandSide = ast.arg1();
			final IExpr rightHandSide = ast.arg2();

			createPatternMatcher(leftHandSide, rightHandSide, false, engine);

			return F.Null;
		}

		@Override
		public Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				EvalEngine engine) throws RuleCreationError {
			final Object[] result = new Object[2];

			if (leftHandSide.isAST()
					&& (((IAST) leftHandSide).getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) == IAST.NO_FLAG) {
				leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, engine);
			}
			result[0] = null;
			result[1] = rightHandSide;

			IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);

			for (int i = 1; i < lhsAST.size(); i++) {
				IExpr temp = lhsAST.get(i);
				if (temp instanceof IPatternObject) {
					continue;
				}
				ISymbol lhsSymbol = null;
				if (temp.isSymbol()) {
					lhsSymbol = (ISymbol) temp;
				} else {
					lhsSymbol = lhsAST.get(i).topHead();
				}
				result[0] = lhsSymbol.putUpRule(ISymbol.RuleType.UPSET_DELAYED, false, lhsAST, rightHandSide);
			}
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static PatternMatching CONST = new PatternMatching();

	public static PatternMatching initialize() {
		return CONST;
	}

	private PatternMatching() {

	}
}
