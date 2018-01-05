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
		F.SymbolQ.setEvaluator(new SymbolQ());
		F.SymmetricMatrixQ.setEvaluator(new SymmetricMatrixQ());
		F.SyntaxQ.setEvaluator(new SyntaxQ());
		F.UpperCaseQ.setEvaluator(new UpperCaseQ());
		F.ValueQ.setEvaluator(new ValueQ());
		F.VectorQ.setEvaluator(new VectorQ());
	}

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
	 * ArrayQ tests whether an expression is a full array.
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/ArrayQ"> ArrayQ</a>
	 * </p>
	 *
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
	 * Returns <code>True</code>, if the given expression is an atomic object (i.e. no AST instance)
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/AtomQ"> AtomQ</a>
	 * </p>
	 *
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
	 * Predicate function
	 * 
	 * Returns <code>True</code> if the first argument is an integer; <code>False</code> otherwise
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
	 * Returns <code>True</code>, if the given expression is a string which only contains digits.
	 * 
	 */
	private static class DigitQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {

		public DigitQ() {
			super(F.DigitQ);
		}

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
	}

	/**
	 * Predicate function
	 * 
	 * Returns <code>True</code> if the 1st argument is an even integer number; <code>False</code> otherwise
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

	private static class HermitianMatrixQ extends SymmetricMatrixQ {
		@Override
		protected boolean compareElements(IExpr expr1, IExpr expr2, EvalEngine engine) {
			if (expr1.isSignedNumber() && expr2.isSignedNumber()) {
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
	 * Predicate function
	 * 
	 * Returns <code>True</code> if the first argument is an integer; <code>False</code> otherwise
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
	 * Predicate function
	 * 
	 * Returns <code>True</code> if the 1st argument is a list expression; <code>False</code> otherwise
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
	 * Match an expression against a given pattern.
	 * 
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
				return F.bool(engine.evalPatternMatcher(ast.arg2()).test(arg1, engine));
			}
			return F.False;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDREST);
		}

	}

	/**
	 * Predicate function
	 * 
	 * Returns <code>True</code> if the 1st argument is a matrix; <code>False</code> otherwise
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

	private static class MemberQ extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				return F.operatorFormAST1(ast);
			}
			Validate.checkRange(ast, 3, 5);

			boolean heads = false;
			if (ast.size() > 3) {
				final Options options = new Options(ast.topHead(), ast, ast.size() - 1, engine);
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
	 * Predicate function
	 * 
	 * Returns <code>True</code> if the 1st argument is a <code>Missing()</code> expression; <code>False</code>
	 * otherwise
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
	 * Returns <code>True</code>, if the given expression is an number object
	 * 
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
	 * Returns <code>True</code>, if the given expression is a numeric function or value.
	 * 
	 */
	private static class NumericQ extends AbstractCoreFunctionEvaluator implements Predicate<IExpr> {

		/**
		 * Constructor for the unary predicate
		 */
		// public final static NumericQ CONST = new NumericQ();

		public NumericQ() {
		}

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
	 * Predicate function
	 * 
	 * Returns <code>True</code> if the 1st argument is an odd integer number; <code>False</code> otherwise
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
	 * Predicate function
	 * 
	 * Returns <code>True</code> if the 1st argument is <code>0</code>; <code>False</code> otherwise
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
				if (expr.isPlus() || expr.isPower() || expr.isTimes()) {
					expr = engine.evaluate(expr);
					if (expr.isZero()) {
						return true;
					}
					if (expr.isPlus() || expr.isPower() || expr.isTimes()) {
						expr = F.Together.of(engine, expr);
						if (expr.isZero()) {
							return true;
						}
					}
				}
			}
			if (expr.isNumericFunction()) {
				IExpr temp = engine.evalN(expr);
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
	 * Test if a number is prime. See: <a href="http://en.wikipedia.org/wiki/Prime_number">Wikipedia:Prime number</a>
	 * 
	 * @see org.matheclipse.core.reflection.system.NextPrime
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

	private static class RealNumberQ extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = ast.arg1();
				if (arg1.isNumber()) {
					return F.bool(arg1.isRealNumber());
				}
				IExpr temp = engine.evaluate(arg1);
				if (temp.isSignedNumber()) {
					return F.True;
				}
				if (temp.isNumericFunction()) {
					temp = engine.evalN(arg1);
					if (temp.isSignedNumber()) {
						return F.True;
					}
				}
				return F.False;
			}
			Validate.checkSize(ast, 2);
			return F.NIL;
		}
	}

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
	 * Returns <code>True</code>, if the given expression is a string which has the correct syntax
	 * 
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
	 * Returns <code>True</code>, if the given expression is a string which only contains upper case characters
	 *
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
	 * Returns <code>True</code>, if the given expression is bound to a value.
	 * 
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
	 * Predicate function
	 *
	 * Returns <code>True</code> if the 1st argument is a vector; <code>False</code> otherwise
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
