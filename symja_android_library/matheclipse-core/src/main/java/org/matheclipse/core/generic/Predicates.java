package org.matheclipse.core.generic;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;

public class Predicates {
	private static class InASTPredicate implements Predicate<IExpr>, Serializable {
		private static final long serialVersionUID = 0;

		private final IAST target;

		private InASTPredicate(IAST target) {
			this.target = target;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof InASTPredicate) {
				InASTPredicate that = (InASTPredicate) obj;
				return target.equals(that.target);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return target.hashCode();
		}

		@Override
		public boolean test(IExpr t) {
			for (IExpr expr : target) {
				if (expr.equals(t)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return "In(" + target + ")";
		}
	}

	/**
	 * Check if the evaluation of a binary AST object gives <code>False</code>
	 * 
	 */
	public static class IsBinaryFalse implements BiPredicate<IExpr, IExpr>, Comparator<IExpr> {
		protected final EvalEngine fEngine;

		protected final IAST fAST;

		/**
		 * Define a binary AST with the header <code>head</code>.
		 * 
		 * @param head
		 *            the AST's head expression
		 */
		public IsBinaryFalse(final IExpr head) {
			this(head, EvalEngine.get());
		}

		public IsBinaryFalse(final IExpr head, EvalEngine engine) {
			fEngine = engine;
			fAST = F.ast(head, 2, true);
		}

		@Override
		public int compare(final IExpr firstArg, final IExpr secondArg) {
			fAST.set(1, firstArg);
			fAST.set(2, secondArg);
			IExpr temp = fEngine.evaluate(fAST);
			if (temp.isFalse()) {
				return 1;
			}
			if (temp.isTrue()) {
				return -1;
			}
			return 0;
		}

		/**
		 * Check if the evaluation of a binary AST object gives
		 * <code>True</code> by settings it's first argument to
		 * <code>firstArg</code> and settings it's second argument to
		 * <code>secondArg</code>
		 * 
		 */
		@Override
		public boolean test(final IExpr firstArg, final IExpr secondArg) {
			fAST.set(1, firstArg);
			fAST.set(2, secondArg);
			if (fEngine.evaluate(fAST).isFalse()) {
				return true;
			}
			return false;
		}

	}

	/**
	 * Check if the evaluation of a binary AST object gives <code>True</code>
	 * 
	 */
	public static class IsBinaryTrue implements BiPredicate<IExpr, IExpr>, Comparator<IExpr> {
		protected final EvalEngine fEngine;

		protected final IAST fAST;

		/**
		 * Define a binary AST with the header <code>head</code>.
		 * 
		 * @param head
		 *            the AST's head expression
		 */
		public IsBinaryTrue(final IExpr head) {
			this(head, EvalEngine.get());
		}

		public IsBinaryTrue(final IExpr head, EvalEngine engine) {
			fEngine = engine;
			fAST = F.ast(head, 2, true);
		}

		@Override
		public int compare(final IExpr firstArg, final IExpr secondArg) {
			fAST.set(1, firstArg);
			fAST.set(2, secondArg);
			IExpr temp = fEngine.evaluate(fAST);
			if (temp.isTrue()) {
				return 1;
			}
			if (temp.isFalse()) {
				return -1;
			}
			return 0;
		}

		/**
		 * Check if the evaluation of a binary AST object gives
		 * <code>True</code> by settings it's first argument to
		 * <code>firstArg</code> and settings it's second argument to
		 * <code>secondArg</code>
		 * 
		 */
		@Override
		public boolean test(final IExpr firstArg, final IExpr secondArg) {
			fAST.set(1, firstArg);
			fAST.set(2, secondArg);
			if (fEngine.evaluate(fAST).isTrue()) {
				return true;
			}
			return false;
		}

	}

	/**
	 * Check if the evaluation of an unary AST object gives <code>True</code>.
	 * 
	 */
	private static class IsUnaryTrue<E extends IExpr> implements Predicate<E> {
		protected final EvalEngine fEngine;

		protected final IAST fAST;

		/**
		 * Define an unary AST with the header <code>head</code>. The
		 * <code>apply()</code> method evaluates the created AST with the given
		 * expression and checks if the result equals <code>True</code>.
		 * 
		 * @param engine
		 *            the evaluation engine
		 * @param head
		 *            the AST's head expresion
		 */
		public IsUnaryTrue(final EvalEngine engine, final IExpr head) {
			fEngine = engine;
			fAST = F.ast(head, 1, false);
		}

		/**
		 * Check if the evaluation of an unary AST object gives
		 * <code>True</code>, by setting the first argument of the AST to
		 * <code>arg</code>.
		 * 
		 */
		@Override
		public boolean test(final IExpr arg) {
			final IAST ast = fAST.clone();
			ast.add(arg);
			return fEngine.evalTrue(ast);
		}

	}

	/**
	 * Returns a predicate that evaluates to {@code true} if the object
	 * reference being tested is one of the arguments of the given
	 * <code>ast</code>. It does not defensively copy the collection passed in,
	 * so future changes to it will alter the behavior of the predicate.
	 * 
	 * @param ast
	 *            the AST those arguments may contain the function input
	 */
	public static Predicate<IExpr> in(IAST ast) {
		return new InASTPredicate(ast);
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if the object
	 * reference being tested is one of the arguments of the given
	 * <code>ast</code>. It does not defensively copy the collection passed in,
	 * so future changes to it will alter the behavior of the predicate.
	 * 
	 * @param expr
	 *            the expr which may match the function input
	 */
	public static Predicate<IExpr> in(IExpr expr) {
		return new InASTPredicate(F.List(expr));
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if the
	 * <code>input</code> is an AST list, which contains one of the given
	 * <b>header elements</b> at index position <code>0</code>.
	 * 
	 */
	public static Predicate<IExpr> isAST(final ISymbol[] heads) {
		return new Predicate<IExpr>() {
			@Override
			public boolean test(IExpr input) {
				for (int i = 0; i < heads.length; i++) {
					if (input.isAST(heads[i])) {
						return true;
					}
				}
				return false;
			}
		};
	}

	/**
	 * Check if the evaluation of the <code>expr</code> object gives
	 * <code>False</code>. A <code>IsBinaryFalse</code> predicate will be
	 * returned.
	 * 
	 * @param expr
	 * @return
	 * @see IsUnaryTrue
	 */
	public static BiPredicate<IExpr, IExpr> isBinaryFalse(IExpr expr) {
		return new IsBinaryFalse(expr, EvalEngine.get());
	}

	/**
	 * Check if the evaluation of the <code>expr</code> object gives
	 * <code>True</code>. A <code>IsUnaryTrue</code> predicate will be returned.
	 * 
	 * @param expr
	 * @return
	 * @see IsUnaryTrue
	 */
	public static BiPredicate<IExpr, IExpr> isBinaryTrue(IExpr expr) {
		return new IsBinaryTrue(expr, EvalEngine.get());
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if the object
	 * reference being tested is free of the arguments of the given
	 * <code>ast</code>. Calls <code>IExpr#isFree(expr, true)</code>.
	 * 
	 * @param expr
	 *            the expr which may match the function input
	 */
	public static Predicate<IExpr> isFree(final IExpr expr) {
		return new Predicate<IExpr>() {
			@Override
			public boolean test(IExpr input) {
				return input.isFree(expr, true);
			}
		};
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if
	 * <code>input.isNumber()</code> gives {@code true}.
	 * 
	 * @return
	 */
	public static Predicate<IExpr> isNumber() {
		return new Predicate<IExpr>() {
			@Override
			public boolean test(IExpr input) {
				return input.isNumber();
			}
		};
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if
	 * <code>input.isNumeric()</code> gives {@code true}. The predicates test if
	 * an input expression is a numeric number (i.e. an instance of type INum or
	 * type IComplexNum
	 * 
	 * @return
	 */
	public static Predicate<IExpr> isNumeric() {
		return new Predicate<IExpr>() {
			@Override
			public boolean test(IExpr input) {
				return input.isNumeric();
			}
		};
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if
	 * <code>input.isNumericFunction()</code> gives {@code true}. The predicates
	 * test if an input expression is a numeric function (i.e. a number, a
	 * symbolic constant or a function (with attribute NumericFunction) where
	 * all arguments are also "numeric functions")
	 * 
	 * @return
	 */
	public static Predicate<IExpr> isNumericFunction() {
		return new Predicate<IExpr>() {
			@Override
			public boolean test(IExpr input) {
				return input.isNumericFunction();
			}
		};
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if the input is an
	 * <code>instanceof IPattern</code>.
	 * 
	 * @return
	 */
	public static Predicate<IExpr> isPattern() {
		return new Predicate<IExpr>() {
			@Override
			public boolean test(IExpr input) {
				return (input instanceof IPattern);
			}
		};
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if
	 * <code>input.isSignedNumber()</code> gives {@code true}.
	 * 
	 * @return
	 */
	public static Predicate<IExpr> isSignedNumber() {
		return new Predicate<IExpr>() {
			@Override
			public boolean test(IExpr input) {
				return input.isSignedNumber();
			}
		};
	}

	/**
	 * Check if the evaluation of the <code>expr</code> object gives
	 * <code>True</code>. If <code>expr</code> is a symbol, which has an
	 * assigned <code>Predicate</code> evaluator object, the predicate will be
	 * returned. Otherwise a <code>IsUnaryTrue</code> predicate will be
	 * returned.
	 * 
	 * @param engine
	 * @param head
	 * @return
	 * @see IsUnaryTrue
	 */
	public static Predicate<IExpr> isTrue(final EvalEngine engine, final IExpr head) {
		if (head.isSymbol()) {
			IEvaluator eval = ((ISymbol) head).getEvaluator();
			if (eval != null && (eval instanceof Predicate<?>)) {
				return (Predicate<IExpr>) eval;
			}
		}
		return new IsUnaryTrue<IExpr>(engine, head);
	}

	/**
	 * Check if the evaluation of the <code>expr</code> object gives
	 * <code>True</code>. If <code>expr</code> is a symbol, which has an
	 * assigned <code>Predicate</code> evaluator object, the predicate will be
	 * returned. Otherwise a <code>IsUnaryTrue</code> predicate will be
	 * returned.
	 * 
	 * @param expr
	 * @return
	 * @see IsUnaryTrue
	 */
	public static Predicate<IExpr> isTrue(IExpr expr) {
		return new IsUnaryTrue<IExpr>(EvalEngine.get(), expr);
	}

	/**
	 *
	 */
	public static Predicate<IExpr> isUnaryVariableOrPattern() {
		return new Predicate<IExpr>() {
			@Override
			public boolean test(final IExpr firstArg) {
				if (firstArg instanceof ISymbol) {
					return true;
				}
				if (firstArg instanceof IPattern) {
					return true;
				}
				return false;
			}
		};
	}

	private Predicates() {

	}
}
