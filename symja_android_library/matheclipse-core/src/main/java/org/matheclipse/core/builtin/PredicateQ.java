package org.matheclipse.core.builtin;

import java.util.function.Predicate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra.InternalFindCommonFactorPlus;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IPredicate;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.VisitorBooleanLevelSpecification;

public class PredicateQ {

	/**
	 * Constructor for the unary predicate
	 */
	// public final static AtomQ ATOMQ = new AtomQ();

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.AntisymmetricMatrixQ.setEvaluator(new AntisymmetricMatrixQ());
			F.AntihermitianMatrixQ.setEvaluator(new AntihermitianMatrixQ());
			F.ArrayQ.setEvaluator(new ArrayQ());
			F.AssociationQ.setPredicateQ(x -> x.isAssociation());
			F.AtomQ.setPredicateQ(x -> x.isAtom());
			F.BooleanQ.setPredicateQ(x -> x.isTrue() || x.isFalse());
			F.ByteArrayQ.setPredicateQ(WXFFunctions::isByteArray);
			F.DigitQ.setEvaluator(new DigitQ());
			F.EvenQ.setEvaluator(new EvenQ());
			F.ExactNumberQ.setPredicateQ(x -> x.isExactNumber());
			F.FreeQ.setEvaluator(new FreeQ());
			F.HermitianMatrixQ.setEvaluator(new HermitianMatrixQ());
			F.InexactNumberQ.setPredicateQ(x -> x.isInexactNumber());
			F.IntegerQ.setPredicateQ(x -> x.isInteger());
			F.ListQ.setPredicateQ(x -> x.isList());
			F.MachineNumberQ.setPredicateQ(x -> x.isMachineNumber());
			F.MatchQ.setEvaluator(new MatchQ());
			F.MatrixQ.setEvaluator(new MatrixQ());
			F.MemberQ.setEvaluator(new MemberQ());
			F.MissingQ.setPredicateQ(x -> x.isAST(F.Missing, 2));
			F.NotListQ.setPredicateQ(x -> !x.isList());
			F.NumberQ.setPredicateQ(x -> x.isNumber());
			F.NumericQ.setPredicateQ(x -> x.isNumericFunction());
			F.OddQ.setEvaluator(new OddQ());
			F.OrthogonalMatrixQ.setEvaluator(new OrthogonalMatrixQ());
			F.PossibleZeroQ.setEvaluator(new PossibleZeroQ());
			F.PrimeQ.setEvaluator(new PrimeQ());
			F.QuantityQ.setEvaluator(new QuantityQ());
			F.RealNumberQ.setEvaluator(new RealNumberQ());
			F.SquareMatrixQ.setEvaluator(new SquareMatrixQ());
			F.StringQ.setPredicateQ(x -> x.isString());
			F.SymbolQ.setPredicateQ(x -> x.isSymbol());
			F.SymmetricMatrixQ.setEvaluator(new SymmetricMatrixQ());
			F.SyntaxQ.setEvaluator(new SyntaxQ());
			F.ValueQ.setEvaluator(new ValueQ());
			F.VectorQ.setEvaluator(new VectorQ());
		}
	}

	/**
	 * <pre>
	 * AntihermitianMatrixQ(m)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>m</code> is a anti hermitian matrix.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Skew-Hermitian_matrix">Wikipedia - Skew-Hermitian matrix</a></li>
	 * </ul>
	 */
	private final static class AntihermitianMatrixQ extends SymmetricMatrixQ {

		@Override
		protected boolean compareElements(IExpr expr1, IExpr expr2, EvalEngine engine) {
			if (expr1.isNumber() && expr2.isNumber()) {
				if (expr1.conjugate().negate().equals(expr2)) {
					return true;
				}
				return false;
			}
			return F.Equal.ofQ(engine, F.Times(F.CN1, F.Conjugate(expr1)), expr2);
		}

	}

	/**
	 * <pre>
	 * AntisymmetricMatrixQ(m)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>m</code> is a anti symmetric matrix.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Skew-symmetric_matrix">Wikipedia - Skew-symmetric matrix</a></li>
	 * </ul>
	 */
	private final static class AntisymmetricMatrixQ extends SymmetricMatrixQ {

		@Override
		protected boolean compareElements(IExpr expr1, IExpr expr2, EvalEngine engine) {
			if (expr1.isNumber() && expr2.isNumber()) {
				if (expr1.negate().equals(expr2)) {
					return true;
				}
				return false;
			}
			return F.Equal.ofQ(engine, F.Times(F.CN1, expr1), expr2);
		}

	}

	/**
	 * <pre>
	 * 'ArrayQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * tests whether expr is a full array.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * 'ArrayQ(expr, pattern)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * also tests whether the array depth of expr matches pattern.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * 'ArrayQ(expr, pattern, test)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * furthermore tests whether <code>test</code> yields <code>True</code> for all elements of expr.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ArrayQ(a)
	 * False
	 * &gt;&gt; ArrayQ({a})
	 * True
	 * &gt;&gt; ArrayQ({{{a}},{{b,c}}})
	 * False
	 * &gt;&gt; ArrayQ({{a, b}, {c, d}}, 2, SymbolQ)
	 * True
	 * </pre>
	 */
	private final static class ArrayQ extends AbstractCoreFunctionEvaluator implements IPredicate {

		/**
		 * Determine the depth of the given expression <code>expr</code> which should be a full array of (possibly
		 * nested) lists. Return <code>-1</code> if the expression isn't a full array.
		 * 
		 * @param expr
		 * @param depth
		 *            start depth of the full array
		 * @param predicate
		 *            an optional <code>Predicate</code> which would be applied to all elements which aren't lists.
		 * @return <code>-1</code> if the expression isn't a full array.
		 */
		private static int determineDepth(final IExpr expr, int depth, Predicate<IExpr> predicate) {
			int resultDepth = depth;
			if (expr.isList()) {
				IAST ast = (IAST) expr;
				int size = ast.size();
				if (size == 1) {
					return depth;
				}
				IExpr arg1AST = ast.arg1();
				boolean isList = arg1AST.isList();
				int arg1Size = 0;
				if (isList) {
					arg1Size = ((IAST) ast.arg1()).size();
				}
				resultDepth = determineDepth(arg1AST, depth + 1, predicate);
				if (resultDepth < 0) {
					return -1;
				}
				int tempDepth;
				for (int i = 2; i < size; i++) {
					if (isList) {
						if (!ast.get(i).isList()) {
							return -1;
						}
						if (arg1Size != ((IAST) ast.get(i)).size()) {
							return -1;
						}
						tempDepth = determineDepth(ast.get(i), depth + 1, predicate);
						if (tempDepth < 0 || tempDepth != resultDepth) {
							return -1;
						}
					} else {
						if (ast.get(i).isList()) {
							return -1;
						}
						if (predicate != null) {
							if (!predicate.test(ast.get(i))) {
								return -1;
							}
						}
					}
				}
				return resultDepth;
			}
			if (predicate != null) {
				if (!predicate.test(expr)) {
					return -1;
				}
			}
			return resultDepth;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final IExpr arg1 = engine.evaluate(ast.arg1());
			Predicate<IExpr> test = null;
			if ((ast.size() >= 4)) {
				final IExpr testArg3 = engine.evaluate(ast.arg3());
				test = x -> engine.evalTrue(F.unaryAST1(testArg3, x));
			}
			int depth = determineDepth(arg1, 0, test);
			if (depth >= 0) {
				if ((ast.size() >= 3)) {
					// Match the depth with the second argument
					final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
					if (!matcher.test(F.ZZ(depth), engine)) {
						return F.False;
					}
				}
				return F.True;
			}
			return F.False;

		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_3;
		}
	}

	/**
	 * <pre>
	 * DigitQ(str)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>str</code> is a string which contains only digits.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; DigitQ("1234")
	 * True
	 * </pre>
	 */
	private final static class DigitQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr>, IPredicate {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			if (arg1 instanceof IStringX) {
				return test(arg1);
			}
			return false;
		}

		@Override
		public boolean test(final IExpr obj) {
			if (obj instanceof IStringX) {
				final String str = obj.toString();
				char ch;
				for (int i = 0; i < str.length(); i++) {
					ch = str.charAt(i);
					if (!((ch >= '0') && (ch <= '9'))) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
	}

	/**
	 * <pre>
	 * EvenQ(x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>x</code> is even, and <code>False</code> otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; EvenQ(4)
	 * True
	 * &gt;&gt; EvenQ(-3)
	 * False
	 * &gt;&gt; EvenQ(n)
	 * False
	 * </pre>
	 */
	private final static class EvenQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr>, IPredicate {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return arg1.isEvenResult();
		}

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine, OptionArgs options) {
			IExpr option = options.getOption(F.GaussianIntegers);
			if (!option.isTrue()) {
				return evalArg1Boole(arg1, engine);
			}
			IInteger[] reImParts = arg1.gaussianIntegers();
			if (reImParts == null) {
				return false;
			}
			if (reImParts[1].isZero()) {
				return reImParts[0].isEven();
			}
			if (reImParts[0].isZero()) {
				return reImParts[1].isEven();
			}
			return reImParts[0].isEven() && reImParts[1].isEven();
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

		@Override
		public boolean test(final IExpr expr) {
			return (expr.isInteger()) && ((IInteger) expr).isEven();
		}
	}

	/**
	 * <pre>
	 * FreeQ(`expr`, `x`)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns 'True' if <code>expr</code> does not contain the expression <code>x</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; FreeQ(y, x)
	 * True
	 * &gt;&gt; FreeQ(a+b+c, a+b)
	 * False
	 * &gt;&gt; FreeQ({1, 2, a^(a+b)}, Plus)
	 * False
	 * &gt;&gt; FreeQ(a+b, x_+y_+z_)
	 * True
	 * &gt;&gt; FreeQ(a+b+c, x_+y_+z_)
	 * False
	 * &gt;&gt; FreeQ(x_+y_+z_)(a+b)
	 * True
	 * </pre>
	 */
	private final static class FreeQ extends AbstractCoreFunctionEvaluator implements IPredicate {

		/**
		 * Checks if <code>orderless1.size()</code> is greater or equal <code>orderless2.size()</code> and returns
		 * <code>false</code>, if every argument in <code>orderless2</code> equals an argument in
		 * <code>orderless1</code>. I.e. <code>orderless1</code> doesn't contain every argument of
		 * <code>orderless2</code>.
		 * 
		 * @param orderless1
		 * @param orderless2
		 * @return <code>false</code> if <code>orderless1.size()</code> is greater or equal
		 *         <code>orderless2.size()</code> and if every argument in <code>orderless2</code> equals an argument in
		 *         <code>orderless1</code>
		 */
		private static boolean isFreeOrderless(IAST orderless1, IAST orderless2) {
			if (orderless1.size() >= orderless2.size()) {
				IExpr temp;
				boolean evaled = false;
				int[] array = new int[orderless1.size()];
				for (int i = 1; i < orderless2.size(); i++) {
					temp = orderless2.get(i);
					evaled = false;
					for (int j = 1; j < orderless1.size(); j++) {
						if (array[j] != (-1) && temp.equals(orderless1.get(j))) {
							array[j] = -1;
							evaled = true;
							break;
						}
					}
					if (!evaled) {
						break;
					}
				}
				if (evaled) {
					return false;
				}
			}
			return true;
		}

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				ast = F.operatorFormAppend(ast);
				if (!ast.isPresent()) {
					return F.NIL;
				}
			}
			if (ast.size() == 3) {
				final IExpr arg1 = engine.evaluate(ast.arg1());
				final IExpr arg2 = engine.evalPattern(ast.arg2());
				if (arg2.isSymbol() || arg2.isNumber() || arg2.isString()) {
					return F.bool(arg1.isFree(arg2, true));
				}

				// final IPatternMatcher matcher = new PatternMatcherEvalEngine(arg2, engine);
				final IPatternMatcher matcher = engine.evalPatternMatcher(arg2);
				if (matcher.isRuleWithoutPatterns()) {
					// special for FreeQ(), don't implemented in MemberQ()!
					if (arg1.isOrderlessAST() && arg2.isOrderlessAST() && arg1.head().equals(arg2.head())) {
						if (!isFreeOrderless((IAST) arg1, (IAST) arg1)) {
							return F.False;
						}
					}
				}
				return F.bool(arg1.isFree(matcher, true));
			}
			return F.NIL;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}
	}

	/**
	 * <pre>
	 * HermitianMatrixQ(m)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>m</code> is a hermitian matrix.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Hermitian_matrix">Wikipedia - Hermitian matrix</a></li>
	 * </ul>
	 */
	private final static class HermitianMatrixQ extends SymmetricMatrixQ {
		@Override
		protected boolean compareElements(IExpr expr1, IExpr expr2, EvalEngine engine) {
			if (expr1.isReal() && expr2.isReal()) {
				if (expr1.equals(expr2)) {
					return true;
				}
				return false;
			}
			if (expr1.isNumber() && expr2.isNumber()) {
				if (expr1.conjugate().equals(expr2)) {
					return true;
				}
				return false;
			}
			return F.Equal.ofQ(engine, F.Conjugate(expr1), expr2);
		}

	}

	/**
	 * <pre>
	 * MatchQ(expr, form)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * tests whether <code>expr</code> matches <code>form</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; MatchQ(123, _Integer)
	 * True
	 * 
	 * &gt;&gt; MatchQ(123, _Real)
	 * False
	 * 
	 * &gt;&gt; MatchQ(_Integer)[123]
	 * True
	 * </pre>
	 */
	private final static class MatchQ extends AbstractCoreFunctionEvaluator implements IPredicate {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				ast = F.operatorFormAppend(ast);
				if (!ast.isPresent()) {
					return F.NIL;
				}
			}
			if ((ast.isAST2())) {
				IExpr arg1 = ast.arg1();
				IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
				IExpr arg1Evaled = engine.evaluate(arg1);
				if (matcher.test(arg1Evaled, engine)) {
					return F.True;
				}
				if (arg1Evaled.isAST()) {
					return F.bool(matcher.test(arg1, engine));
				}
				// if (!arg2.isCondition()) {
				// try {
				// arg2 = engine.evaluate(arg2);
				// } catch (RuntimeException rte) {
				//
				// }
				// }
				// return F.bool(engine.evalPatternMatcher(arg2).test(arg1, engine));

			}
			return F.False;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}
	}

	/**
	 * <pre>
	 * MatrixQ(m)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>m</code> is a list of equal-length lists.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * MatrixQ[m, f]
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * only returns <code>True</code> if <code>f(x)</code> returns <code>True</code> for each element <code>x</code> of
	 * the matrix <code>m</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; MatrixQ({{1, 3}, {4.0, 3/2}}, NumberQ)
	 * True
	 * </pre>
	 */
	private final static class MatrixQ extends AbstractCoreFunctionEvaluator implements IPredicate {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final IExpr arg1 = engine.evaluate(ast.arg1());
			int[] dims = arg1.isMatrix();
			if (dims == null) {
				return F.False;
			}

			if (ast.isAST2()) {
				final IExpr arg2 = engine.evaluate(ast.arg2());
				IASTAppendable temp = F.ast(arg2);
				temp.append(F.Slot1);
				IAST matrix = (IAST) arg1;
				for (int i = 1; i < dims[0]; i++) {
					if (!((IAST) matrix.get(i)).forAll(x -> {
						temp.set(1, x);
						return engine.evalTrue(temp);
					})) {
						return F.False;
					}
				}
			}
			return F.True;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * MemberQ(list, pattern)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if pattern matches any element of <code>list</code>, or <code>False</code> otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; MemberQ({a, b, c}, b)
	 * True
	 * &gt;&gt; MemberQ({a, b, c}, d)
	 * False
	 * &gt;&gt; MemberQ({"a", b, f(x)}, _?NumericQ)
	 * False
	 * &gt;&gt; MemberQ(_List)({{}})
	 * True
	 * </pre>
	 */
	private final static class MemberQ extends AbstractCoreFunctionEvaluator implements IPredicate {

		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				ast = F.operatorFormAppend(ast);
				if (!ast.isPresent()) {
					return F.NIL;
				}
			}
			try {
				boolean heads = false;
				int size = ast.size();
				if (ast.size() > 3) {
					final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, size, engine);
					if (options.isTrue(F.Heads)) {
						heads = true;
					}
					int pos = options.getLastPosition();
					if (pos != -1) {
						size = pos;
					}
				}

				if (size >= 3) {
					final IExpr arg1 = engine.evaluate(ast.arg1());
					if (arg1.isAST()) {
						final IExpr arg2 = engine.evaluate(ast.arg2());
						if (size == 3) {
							return F.bool(arg1.isMember(arg2, heads, null));
						}

						Predicate<IExpr> predicate = memberPredicate(arg2);
						IVisitorBoolean level = new VisitorBooleanLevelSpecification(predicate, ast.arg3(), heads,
								engine);

						return F.bool(arg1.accept(level));
					}

					return F.False;
				}
			} catch (final ValidateException ve) {
				// see level specification
				return engine.printMessage(ve.getMessage(ast.topHead()));
			}
			return F.NIL;
		}

		private static Predicate<IExpr> memberPredicate(IExpr pattern) {
			if (pattern.isSymbol() || pattern.isNumber() || pattern.isString()) {
				return x -> x.equals(pattern);
			}
			return new PatternMatcher(pattern);
		}
	}

	/**
	 * <pre>
	 * OddQ(x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>x</code> is odd, and <code>False</code> otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; OddQ(-3)
	 * True
	 * 
	 * &gt;&gt; OddQ(0)
	 * False
	 * </pre>
	 */
	private final static class OddQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr>, IPredicate {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return arg1.isInteger() && ((IInteger) arg1).isOdd();
		}

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine, OptionArgs options) {
			IExpr option = options.getOption(F.GaussianIntegers);
			if (!option.isTrue()) {
				return evalArg1Boole(arg1, engine);
			}
			IInteger[] reImParts = arg1.gaussianIntegers();
			if (reImParts == null) {
				return false;
			}
			if (reImParts[1].isZero()) {
				return reImParts[0].isOdd();
			}
			if (reImParts[0].isZero()) {
				return reImParts[1].isOdd();
			}
			if (reImParts[0].isOdd() && reImParts[1].isOdd()) {
				return false;
			}
			return reImParts[0].isOdd() || reImParts[1].isOdd();
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

		@Override
		public boolean test(final IExpr expr) {
			return expr.isInteger() && ((IInteger) expr).isOdd();
		}

	}

	private final static class QuantityQ extends AbstractCorePredicateEvaluator
			implements Predicate<IExpr>, IPredicate {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return arg1.isQuantity();
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

		@Override
		public boolean test(final IExpr expr) {
			return expr.isQuantity();
		}

	}

	private final static class OrthogonalMatrixQ extends AbstractCoreFunctionEvaluator implements IPredicate {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {

			final IExpr arg1 = engine.evaluate(ast.arg1());
			int[] dims = arg1.isMatrix();
			if (dims == null) {
				// no square matrix
				return F.False;
			}
			IExpr identityMatrix = F.NIL;
			int[] identityMatrixDims = null;
			if (dims[0] >= dims[1]) {
				identityMatrix = F.Dot.of(engine, F.Transpose(arg1), arg1);
				identityMatrixDims = identityMatrix.isMatrix();
				if (identityMatrixDims == null || //
						identityMatrixDims[0] != dims[1] || //
						identityMatrixDims[1] != dims[1]) {
					return F.False;
				}
			} else {
				identityMatrix = F.Dot.of(engine, arg1, F.Transpose(arg1));
				identityMatrixDims = identityMatrix.isMatrix();
				if (identityMatrixDims == null || //
						identityMatrixDims[0] != dims[0] || //
						identityMatrixDims[1] != dims[0]) {
					return F.False;
				}
			}
			IAST matrix = (IAST) identityMatrix;
			for (int i = 1; i <= identityMatrixDims[0]; i++) {
				IAST row = (IAST) matrix.get(i);
				for (int j = 1; j <= identityMatrixDims[1]; j++) {
					if (i == j) {
						if (!F.PossibleZeroQ.ofQ(engine, F.Plus(F.CN1, row.get(j)))) {
							return F.False;
						}
					} else {
						if (!F.PossibleZeroQ.ofQ(engine, row.get(j))) {
							return F.False;
						}
					}
				}
			}
			return F.True;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	/**
	 * <pre>
	 * PossibleZeroQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * maps a (possible) zero <code>expr</code> to <code>True</code> and returns <code>False</code> otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; PossibleZeroQ((E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi)
	 * True
	 * 
	 * &gt;&gt; PossibleZeroQ(Sqrt(x^2) - x)
	 * False
	 * </pre>
	 */
	private final static class PossibleZeroQ extends AbstractCorePredicateEvaluator implements IPredicate {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			IExpr expr = arg1;
			if (expr.isNumber()) {
				return expr.isZero();
			}
			if (expr.isAST()) {
				IExpr temp = ((IAST) expr).replace( //
						x -> x.isNumericFunction(), //
						x -> x.evalNumber());
				if (temp != null) {
					temp = engine.evaluate(temp);
					if (temp.isZero()) {
						return true;
					}
				}

				if (expr.isPlus()) {
					IExpr[] commonFactors = InternalFindCommonFactorPlus.findCommonFactors((IAST) expr, true);
					if (commonFactors != null) {
						temp = engine.evaluate(F.Simplify(F.Times(commonFactors[0], commonFactors[1])));
						if (temp.isNumber()) {
							return temp.isZero();
						}
						temp = temp.evalNumber();
						if (temp != null) {
							if (temp.isZero()) {
								return true;
							}
						}
					}
				}

				return isZeroTogether(expr, engine);

			}
			return false;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * PrimeQ(n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>n</code> is a integer prime number.<br />
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * PrimeQ(n, GaussianIntegers -&gt; True)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>n</code> is a Gaussian prime number.<br />
	 * </p>
	 * </blockquote>
	 * <p>
	 * For very large numbers, <code>PrimeQ</code> uses
	 * <a href="https://en.wikipedia.org/wiki/Prime_number#Primality_testing_versus_primality_proving">probabilistic
	 * prime testing</a>, so it might be wrong sometimes<br />
	 * (a number might be composite even though <code>PrimeQ</code> says it is prime).
	 * </p>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Prime_number">Wikipedia - Prime number</a></li>
	 * <li><a href="https://en.wikipedia.org/wiki/Gaussian_integer#Gaussian_primes">Wikipedia - Gaussian primes</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; PrimeQ(2)   
	 * True   
	 * &gt;&gt; PrimeQ(-3)   
	 * True   
	 * &gt;&gt; PrimeQ(137)   
	 * True   
	 * &gt;&gt; PrimeQ(2 ^ 127 - 1)   
	 * True   
	 * &gt;&gt; PrimeQ(1)   
	 * False   
	 * &gt;&gt; PrimeQ(2 ^ 255 - 1)   
	 * False
	 * </pre>
	 * <p>
	 * All prime numbers between 1 and 100:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Select(Range(100), PrimeQ)   
	 *  = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97}
	 * </pre>
	 * <p>
	 * 'PrimeQ' has attribute 'Listable':
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; PrimeQ(Range(20))   
	 *  = {False, True, True, False, True, False, True, False, False, False, True, False, True, False, False, False, True, False, True, False}
	 * </pre>
	 * <p>
	 * The Gaussian integer <code>2 == (1 + i)*(1 − i)</code> isn't a Gaussian prime number:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; PrimeQ(2, GaussianIntegers-&gt;True)
	 * False
	 * 
	 * &gt;&gt; PrimeQ(5+2*I, GaussianIntegers-&gt;True)
	 * True
	 * </pre>
	 */
	private final static class PrimeQ extends AbstractCorePredicateEvaluator
			implements Predicate<IInteger>, IPredicate {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			if (!arg1.isInteger()) {
				return false;
			}
			return ((IInteger) arg1).isProbablePrime();
		}

		/**
		 * Eval <a href="https://en.wikipedia.org/wiki/Gaussian_integer#Gaussian_primes">Gaussian primes</a> if option
		 * <code>GaussianIntegers->True</code> is set.
		 */
		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine, OptionArgs options) {
			IExpr option = options.getOption(F.GaussianIntegers);
			if (!option.isTrue()) {
				return evalArg1Boole(arg1, engine);
			}
			IInteger[] reImParts = arg1.gaussianIntegers();
			if (reImParts == null) {
				return false;
			}
			if (reImParts[1].isZero()) {
				if (reImParts[0].isProbablePrime()) {
					return reImParts[0].abs().mod(F.C4).equals(F.C3);
				}
				return false;
			}
			if (reImParts[0].isZero()) {
				if (reImParts[1].isProbablePrime()) {
					return reImParts[1].abs().mod(F.C4).equals(F.C3);
				}
				return false;
			}
			// re^2 + im^2 is probable prime?
			return reImParts[0].pow(2L).add(reImParts[1].pow(2L)).isProbablePrime();
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

		@Override
		public boolean test(final IInteger obj) {
			return obj.isProbablePrime();
		}
	}

	/**
	 * <pre>
	 * RealNumberQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>expr</code> is an explicit number with no imaginary component.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; RealNumberQ[10]
	 *  = True
	 * 
	 * &gt;&gt; RealNumberQ[4.0]
	 *  = True
	 * 
	 * &gt;&gt; RealNumberQ[1+I]
	 *  = False
	 * 
	 * &gt;&gt; RealNumberQ[0 * I]
	 *  = True
	 * 
	 * &gt;&gt; RealNumberQ[0.0 * I]
	 *  = False
	 * </pre>
	 */
	private final static class RealNumberQ extends AbstractCoreFunctionEvaluator implements IPredicate {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {

			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isNumber()) {
				if (arg1.isComplex() || arg1.isComplexNumeric()) {
					return F.False;
				}
				return F.bool(arg1.isReal());
			}

			// CAUTION: the following can not be used because Rubi uses another definition
			// IExpr temp = engine.evaluate(arg1);
			// if (temp.isReal()) {
			// return F.True;
			// }
			// if (temp.isNumericFunction()) {
			// temp = engine.evalN(arg1);
			// if (temp.isReal()) {
			// return F.True;
			// }
			// }
			return F.False;

		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	/**
	 * <pre>
	 * SquareMatrixQ(m)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>m</code> is a square matrix.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; SquareMatrixQ({{1, 3 + 4*I}, {3 - 4*I, 2}})
	 * True
	 * 
	 * &gt;&gt; SquareMatrixQ({{}})
	 * False
	 * </pre>
	 */
	private final static class SquareMatrixQ extends AbstractCoreFunctionEvaluator implements IPredicate {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {

			final IExpr arg1 = engine.evaluate(ast.arg1());
			int[] dims = arg1.isMatrix();
			if (dims == null || dims[0] != dims[1]) {
				// no square matrix
				return F.False;
			}

			return F.True;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	/**
	 * <pre>
	 * SymmetricMatrixQ(m)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>m</code> is a symmetric matrix.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Symmetric_matrix">Wikipedia - Symmetric matrix</a></li>
	 * </ul>
	 */
	private static class SymmetricMatrixQ extends AbstractCoreFunctionEvaluator implements IPredicate {

		protected boolean compareElements(IExpr expr1, IExpr expr2, EvalEngine engine) {
			if (expr1.isNumber() && expr2.isNumber()) {
				if (expr1.equals(expr2)) {
					return true;
				}
				return false;
			}
			return F.Equal.ofQ(engine, expr1, expr2);
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final IExpr arg1 = engine.evaluate(ast.arg1());
			int[] dims = arg1.isMatrix();
			if (dims == null || dims[0] != dims[1]) {
				// no square matrix
				return F.False;
			}

			final IAST matrix = (IAST) arg1;
			for (int i = 1; i <= dims[0]; i++) {
				IAST row = matrix.getAST(i);
				for (int j = i + 1; j <= dims[1]; j++) {
					IExpr expr = row.get(j);
					IExpr symmetricExpr = matrix.getPart(j, i);
					if (!compareElements(expr, symmetricExpr, engine)) {
						return F.False;
					}
				}
			}
			return F.True;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

	}

	/**
	 * <pre>
	 * SyntaxQ(str)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is <code>True</code> if the given <code>str</code> is a string which has the correct syntax.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; SyntaxQ("Integrate(f(x),{x,0,10})")
	 * True
	 * </pre>
	 */
	private final static class SyntaxQ extends AbstractCorePredicateEvaluator implements IPredicate {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return arg1.isString() ? ExprParser.test(arg1.toString(), engine) : false;
		}

	}

	/**
	 * <pre>
	 * ValueQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if and only if <code>expr</code> is defined.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ValueQ(x)
	 * False
	 * 
	 * &gt;&gt; x=1;
	 * &gt;&gt; ValueQ(x)
	 * True
	 * 
	 * &gt;&gt; ValueQ(True)
	 * False
	 * </pre>
	 */
	private final static class ValueQ extends AbstractCoreFunctionEvaluator implements Predicate<IExpr>, IPredicate {

		/**
		 * Returns <code>True</code> if the 1st argument is an atomic object; <code>False</code> otherwise
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// don't eval first argument
			return F.bool(ast.arg1().isValue());
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public boolean test(final IExpr expr) {
			return expr.isValue();
		}

	}

	/**
	 * <pre>
	 * VectorQ(v)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>v</code> is a list of elements which are not themselves lists.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * VectorQ(v, f)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>v</code> is a vector and <code>f(x)</code> returns <code>True</code> for each
	 * element <code>x</code> of <code>v</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; VectorQ({a, b, c})
	 * True
	 * </pre>
	 */
	private final static class VectorQ extends AbstractCoreFunctionEvaluator implements IPredicate {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {

			final IExpr arg1 = engine.evaluate(ast.arg1());
			int dim = arg1.isVector();
			if (dim == (-1)) {
				return F.False;
			}

			if (ast.isAST2()) {
				final IExpr arg2 = engine.evaluate(ast.arg2());
				IASTAppendable temp = F.ast(arg2);
				temp.append(F.Slot1);

				IAST vector = (IAST) arg1;
				if (!vector.forAll(x -> {
					temp.set(1, x);
					return engine.evalTrue(temp);
				})) {
					return F.False;
				}
			}
			return F.True;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}
	}

	public static boolean isZeroTogether(IExpr expr, EvalEngine engine) {
		expr = F.expandAll(expr, true, true);
		expr = engine.evaluate(expr);
		if (expr.isZero()) {
			return true;
		}
		if (expr.leafCount() > Config.MAX_POSSIBLE_ZERO_LEAFCOUNT) {
			return false;
		}
		if (expr.isPlusTimesPower()) {
			expr = engine.evaluate(expr);
			if (expr.isNumber()) {
				return expr.isZero();
			}
			if (expr.isPlusTimesPower()) {
				expr = F.Together.of(engine, expr);
				if (expr.isNumber()) {
					return expr.isZero();
				}
			}
		}
		return false;
	}

	public static void initialize() {
		Initializer.init();
	}

	private PredicateQ() {

	}
}
