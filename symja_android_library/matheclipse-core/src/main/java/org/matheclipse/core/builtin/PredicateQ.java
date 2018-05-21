package org.matheclipse.core.builtin;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherEvalEngine;

public class PredicateQ {

	/**
	 * Constructor for the unary predicate
	 */
	public final static AtomQ ATOMQ = new AtomQ();

	static {
		F.AntisymmetricMatrixQ.setEvaluator(new AntisymmetricMatrixQ());
		F.AntihermitianMatrixQ.setEvaluator(new AntihermitianMatrixQ());
		F.ArrayQ.setEvaluator(new ArrayQ());
		F.AtomQ.setEvaluator(ATOMQ);
		F.BooleanQ.setEvaluator(new BooleanQ());
		F.DigitQ.setEvaluator(new DigitQ());
		F.EvenQ.setEvaluator(new EvenQ());
		F.ExactNumberQ.setEvaluator(new ExactNumberQ());
		F.FreeQ.setEvaluator(new FreeQ());
		F.HermitianMatrixQ.setEvaluator(new HermitianMatrixQ());
		F.InexactNumberQ.setEvaluator(new InexactNumberQ());
		F.IntegerQ.setEvaluator(new IntegerQ());
		F.ListQ.setEvaluator(new ListQ());
		F.MachineNumberQ.setEvaluator(new MachineNumberQ());
		F.MatchQ.setEvaluator(new MatchQ());
		F.MatrixQ.setEvaluator(new MatrixQ());
		F.MemberQ.setEvaluator(new MemberQ());
		F.MissingQ.setEvaluator(new MissingQ());
		F.NotListQ.setEvaluator(new NotListQ());
		F.NumberQ.setEvaluator(new NumberQ());
		F.NumericQ.setEvaluator(new NumericQ());
		F.OddQ.setEvaluator(new OddQ());
		F.PossibleZeroQ.setEvaluator(new PossibleZeroQ());
		F.PrimeQ.setEvaluator(new PrimeQ());
		F.RealNumberQ.setEvaluator(new RealNumberQ());
		F.SquareMatrixQ.setEvaluator(new SquareMatrixQ());
		F.SymbolQ.setEvaluator(new SymbolQ());
		F.SymmetricMatrixQ.setEvaluator(new SymmetricMatrixQ());
		F.SyntaxQ.setEvaluator(new SyntaxQ());
		F.UpperCaseQ.setEvaluator(new UpperCaseQ());
		F.ValueQ.setEvaluator(new ValueQ());
		F.VectorQ.setEvaluator(new VectorQ());
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
	private static class AntihermitianMatrixQ extends SymmetricMatrixQ {

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

		@Override
		public void setUp(final ISymbol newSymbol) {
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
	private static class AntisymmetricMatrixQ extends SymmetricMatrixQ {

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

		@Override
		public void setUp(final ISymbol newSymbol) {
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
	private static class ArrayQ extends AbstractCoreFunctionEvaluator {

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
			Validate.checkRange(ast, 2, 4);

			final IExpr arg1 = engine.evaluate(ast.arg1());
			Predicate<IExpr> pred = null;
			if ((ast.size() >= 4)) {
				final IExpr arg3 = engine.evaluate(ast.arg3());
				pred = x -> engine.evalTrue(F.unaryAST1(arg3, x));
			}
			int depth = determineDepth(arg1, 0, pred);
			if (depth >= 0) {
				if ((ast.size() >= 3)) {
					// Match the depth with the second argumnt
					final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
					if (!matcher.test(F.ZZ(depth), engine)) {
						return F.False;
					}
				}
				return F.True;
			}
			return F.False;

		}

	}

	/**
	 * <pre>
	 * AtomQ(x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is true if <code>x</code> is an atom (an object such as a number or string, which cannot be divided into
	 * subexpressions using 'Part').
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; AtomQ(x)
	 * True
	 * 
	 * &gt;&gt; AtomQ(1.2)
	 * True
	 * 
	 * &gt;&gt; AtomQ(2 + I)
	 * True
	 * 
	 * &gt;&gt; AtomQ(2 / 3)
	 * True
	 * 
	 * &gt;&gt; AtomQ(x + y)
	 * False
	 * </pre>
	 */
	private static class AtomQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return arg1.isAtom();
		}

		@Override
		public boolean test(final IExpr obj) {
			return obj.isAtom();
		}

	}

	/**
	 * <pre>
	 * BooleanQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>expr</code> is either <code>True</code> or <code>False</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; BooleanQ(True)
	 * True
	 * &gt;&gt; BooleanQ(False)
	 * True
	 * &gt;&gt; BooleanQ(a)
	 * False
	 * &gt;&gt; BooleanQ(1 &lt; 2)
	 * True
	 * &gt;&gt; BooleanQ("string")
	 * False
	 * &gt;&gt; BooleanQ(Together(x/y + y/x))
	 * False
	 * </pre>
	 */
	private static class BooleanQ extends AbstractCorePredicateEvaluator {

		public BooleanQ() {
			super(F.BooleanQ);
		}

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return arg1.isTrue() || arg1.isFalse();
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
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
	private static class DigitQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			if (arg1 instanceof IStringX) {
				return test(arg1);
			}
			return false;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
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
	private static class EvenQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {
		public EvenQ() {
			super(F.EvenQ);
		}

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return arg1.isInteger() && ((IInteger) arg1).isEven();
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
	 * ExactNumberQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>expr</code> is an exact number, and <code>False</code> otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ExactNumberQ(10)
	 * True
	 * 
	 * &gt;&gt; ExactNumberQ(4.0)
	 * False
	 * 
	 * &gt;&gt; ExactNumberQ(n)
	 * False
	 * 
	 * &gt;&gt; ExactNumberQ(1+I)    
	 * True
	 * 
	 * &gt;&gt; ExactNumberQ(1 + 1. * I)
	 * False
	 * </pre>
	 */

	private static class ExactNumberQ extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return F.bool(arg1.isExactNumber());
			}
			Validate.checkSize(ast, 2);
			return F.NIL;
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
	private static class FreeQ extends AbstractCoreFunctionEvaluator {

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
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				return F.operatorFormAST1(ast);
			}
			Validate.checkSize(ast, 3);
			final IExpr arg1 = engine.evaluate(ast.arg1());
			final IExpr arg2 = engine.evalPattern(ast.arg2());
			final IPatternMatcher matcher = new PatternMatcherEvalEngine(arg2, engine);
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
	private static class HermitianMatrixQ extends SymmetricMatrixQ {
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

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * InexactNumberQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>expr</code> is not an exact number, and <code>False</code> otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; InexactNumberQ(a)
	 * False
	 * 
	 * &gt;&gt; InexactNumberQ(3.0)
	 * True
	 * 
	 * &gt;&gt; InexactNumberQ(2/3)
	 * False
	 * </pre>
	 * <p>
	 * <code>InexactNumberQ</code> can be applied to complex numbers:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; InexactNumberQ(4.0+I)    
	 * True
	 * </pre>
	 */
	private static class InexactNumberQ extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return F.bool(arg1.isInexactNumber());
			}
			Validate.checkSize(ast, 2);
			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * IntegerQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>expr</code> is an integer, and <code>False</code> otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; IntegerQ(3)
	 * 4
	 * 
	 * &gt;&gt; IntegerQ(Pi)
	 * False
	 * </pre>
	 */
	private static class IntegerQ extends AbstractCorePredicateEvaluator {

		public IntegerQ() {
			super(F.IntegerQ);
		}

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return arg1.isInteger();
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * ListQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * tests whether <code>expr</code> is a <code>List</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; ListQ({1, 2, 3})
	 * True
	 * 
	 * &gt;&gt; ListQ({{1, 2}, {3, 4}})
	 * True
	 * 
	 * &gt;&gt; ListQ(x)
	 * False
	 * </pre>
	 */
	private static class ListQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return arg1.isList();
		}

		@Override
		public boolean test(final IExpr expr) {
			return expr.isList();
		}
	}

	/**
	 * <pre>
	 * MachineNumberQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>expr</code> is a machine-precision real or complex number.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; MachineNumberQ(3.14159265358979324)
	 * False
	 * 
	 * &gt;&gt; MachineNumberQ(1.5 + 2.3*I)
	 * True
	 * 
	 * &gt;&gt; MachineNumberQ(2.71828182845904524 + 3.14159265358979324*I)
	 * False
	 * 
	 * &gt;&gt; MachineNumberQ(1.5 + 3.14159265358979324*I)    
	 * True
	 * 
	 * &gt;&gt; MachineNumberQ(1.5 + 5 *I)
	 * True
	 * </pre>
	 */
	private static class MachineNumberQ extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return F.bool(arg1.isMachineNumber());
			}
			Validate.checkSize(ast, 2);
			return F.NIL;
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
	private static class MatchQ extends AbstractCoreFunctionEvaluator {

		public MatchQ() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				return F.operatorFormAST1(ast);
			}
			if ((ast.isAST2())) {
				final IExpr arg1 = engine.evaluate(ast.arg1());
				IExpr arg2 = ast.arg2();
				if (!arg2.isCondition()) {
					try {
						arg2 = engine.evaluate(arg2);
					} catch (RuntimeException rte) {

					}
				}
				return F.bool(engine.evalPatternMatcher(arg2).test(arg1, engine));
			}
			return F.False;
		}

		// @Override
		// public void setUp(final ISymbol newSymbol) {
		// newSymbol.setAttributes(ISymbol.HOLDREST);
		// }

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
	private static class MatrixQ extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

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
	private static class MemberQ extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				return F.operatorFormAST1(ast);
			}
			Validate.checkRange(ast, 3, 5);

			boolean heads = false;
			if (ast.size() > 3) {
				final Options options = new Options(ast.topHead(), ast, ast.argSize(), engine);
				// IExpr option = options.getOption("Heads");
				if (options.isOption("Heads")) {
					heads = true;
				}
			}
			final IExpr arg1 = engine.evaluate(ast.arg1());
			final IExpr arg2 = engine.evaluate(ast.arg2());
			if (arg1.isAST()) {
				return F.bool(arg1.isMember(arg2, heads));
			}
			return F.False;
		}

	}

	/**
	 * <pre>
	 * MissingQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>expr</code> is a <code>Missing()</code> expression.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; MissingQ(Missing("Test message"))
	 * True
	 * </pre>
	 */
	private static class MissingQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return arg1.isAST(F.Missing, 2);
		}

		@Override
		public boolean test(final IExpr expr) {
			return expr.isAST(F.Missing, 2);
		}
	}

	/**
	 * Predicate function
	 * 
	 * Returns <code>True</code> if the 1st argument is a list expression; <code>False</code> otherwise
	 */
	private static class NotListQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return !arg1.isList();
		}

		@Override
		public boolean test(final IExpr expr) {
			return !expr.isList();
		}
	}

	/**
	 * <pre>
	 * NumberQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>expr</code> is an explicit number, and <code>False</code> otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; NumberQ[3+I]
	 *  = True
	 * 
	 * &gt;&gt; NumberQ[5!]
	 *  = True
	 * 
	 * &gt;&gt; NumberQ[Pi]
	 *  = False
	 * </pre>
	 */
	private static class NumberQ extends AbstractCoreFunctionEvaluator {
		/**
		 * Returns <code>True</code> if the 1st argument is a number; <code>False</code> otherwise
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return F.bool(arg1.isNumber());
			}
			Validate.checkSize(ast, 2);
			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * NumericQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>expr</code> is an explicit numeric expression, and <code>False</code>
	 * otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; NumericQ(E+Pi)
	 * True
	 * 
	 * &gt;&gt; NumericQ(Sqrt(3))
	 * True
	 * </pre>
	 */
	private static class NumericQ extends AbstractCoreFunctionEvaluator implements Predicate<IExpr> {

		/**
		 * Returns <code>True</code> if the first argument is a numeric object; <code>False</code> otherwise
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			IExpr arg1 = engine.evaluate(ast.arg1());
			return F.bool(arg1.isNumericFunction());
		}

		@Override
		public void setUp(ISymbol newSymbol) {
		}

		@Override
		public boolean test(IExpr arg) {
			return arg.isNumericFunction();
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
	private static class OddQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {
		public OddQ() {
			super(F.OddQ);
		}

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			return arg1.isInteger() && ((IInteger) arg1).isOdd();
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
	private static class PossibleZeroQ extends AbstractCorePredicateEvaluator {

		public PossibleZeroQ() {
			super(F.PossibleZeroQ);
		}

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			IExpr expr = arg1;
			if (expr.isNumber()) {
				return expr.isZero();
			}
			if (expr.isAST()) {
				expr = F.expandAll(expr, true, true);
				if (expr.isZero()) {
					return true;
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
			}
			IExpr temp = arg1.evalNumber();
			if (temp != null) {
				return temp.isZero();
			}

			return expr.isZero();
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
	 * returns <code>True</code> if <code>n</code> is a prime number.<br />
	 * </p>
	 * </blockquote>
	 * <p>
	 * For very large numbers, <code>PrimeQ</code> uses probabilistic prime testing, so it might be wrong
	 * sometimes<br />
	 * (a number might be composite even though <code>PrimeQ</code> says it is prime).
	 * </p>
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
	 * All prime numbers between 1 and 100:<br />
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
	 */
	private static class PrimeQ extends AbstractCorePredicateEvaluator implements Predicate<IInteger> {

		public PrimeQ() {
			super(F.PrimeQ);
		}

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			if (!arg1.isInteger()) {
				return false;
			}
			return ((IInteger) arg1).isProbablePrime();
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
	private static class RealNumberQ extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = ast.arg1();
				if (arg1.isNumber()) {
					return F.bool(arg1.isReal());
				}
				IExpr temp = engine.evaluate(arg1);
				if (temp.isReal()) {
					return F.True;
				}
				if (temp.isNumericFunction()) {
					temp = engine.evalN(arg1);
					if (temp.isReal()) {
						return F.True;
					}
				}
				return F.False;
			}
			Validate.checkSize(ast, 2);
			return F.NIL;
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
	private static class SquareMatrixQ extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			final IExpr arg1 = engine.evaluate(ast.arg1());
			int[] dims = arg1.isMatrix();
			if (dims == null || dims[0] != dims[1]) {
				// no square matrix
				return F.False;
			}

			return F.True;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * SymbolQ(x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is <code>True</code> if <code>x</code> is a symbol, or <code>False</code> otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; SymbolQ(a)
	 * True
	 * &gt;&gt; SymbolQ(1)
	 * False
	 * &gt;&gt; SymbolQ(a + b)
	 * False
	 * </pre>
	 */
	private static class SymbolQ extends AbstractCoreFunctionEvaluator implements Predicate<IExpr> {
		/**
		 * Returns <code>True</code> if the 1st argument is a symbol; <code>False</code> otherwise
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			IExpr arg1 = engine.evaluate(ast.arg1());
			return F.bool(arg1.isSymbol());
		}

		@Override
		public boolean test(final IExpr expr) {
			return expr.isSymbol();
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
	private static class SymmetricMatrixQ extends AbstractCoreFunctionEvaluator {

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
			Validate.checkRange(ast, 2, 3);

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

		@Override
		public void setUp(final ISymbol newSymbol) {
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
	private static class SyntaxQ extends AbstractCorePredicateEvaluator {

		public SyntaxQ() {
			super(F.SyntaxQ);
		}

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			if (!(arg1 instanceof IStringX)) {
				return false;
			}
			return ExprParser.test(arg1.toString(), engine);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * UpperCaseQ(str)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is <code>True</code> if the given <code>str</code> is a string which only contains upper case characters.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; UpperCaseQ("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
	 * True
	 * 
	 * &gt;&gt; UpperCaseQ("ABCDEFGHIJKLMNopqRSTUVWXYZ")
	 * False
	 * </pre>
	 */
	private static class UpperCaseQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {

		public UpperCaseQ() {
			super(F.UpperCaseQ);
		}

		@Override
		public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
			if (!(arg1 instanceof IStringX)) {
				throw new WrongArgumentType(null, arg1, 1);
			}
			return test(arg1);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

		@Override
		public boolean test(final IExpr obj) {
			final String str = obj.toString();
			char ch;
			for (int i = 0; i < str.length(); i++) {
				ch = str.charAt(i);
				if (!(Character.isUpperCase(ch))) {
					return false;
				}
			}
			return true;
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
	private static class ValueQ extends AbstractCoreFunctionEvaluator implements Predicate<IExpr> {

		/**
		 * Returns <code>True</code> if the 1st argument is an atomic object; <code>False</code> otherwise
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			return F.bool(ast.arg1().isValue());
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
	private static class VectorQ extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

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

	}

	private final static PredicateQ CONST = new PredicateQ();

	public static PredicateQ initialize() {
		return CONST;
	}

	private PredicateQ() {

	}
}
