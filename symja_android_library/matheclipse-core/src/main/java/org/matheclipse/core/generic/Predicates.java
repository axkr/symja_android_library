package org.matheclipse.core.generic;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Predicate;

public class Predicates {
	private Predicates() {

	}

	private static class InASTPredicate implements Predicate<IExpr>, Serializable {
		private final IAST target;

		private InASTPredicate(IAST target) {
			this.target = checkNotNull(target);
		}

		public boolean apply(IExpr t) {
			for (IExpr expr : target) {
				if (expr.equals(t)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean equals(@Nullable Object obj) {
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
		public String toString() {
			return "In(" + target + ")";
		}

		private static final long serialVersionUID = 0;
	}

	/**
	 * Check if the evaluation of an unary AST object gives <code>True</code>.
	 * 
	 */
	private static class IsUnaryTrue<E extends IExpr> implements Predicate<E> {
		protected final EvalEngine fEngine;

		protected final IAST fAST;

		/**
		 * Define an unary AST with the header <code>head</code>. The <code>apply()</code> method evaluates the created AST with the
		 * given expression and checks if the result equals <code>True</code>.
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
		 * Check if the evaluation of an unary AST object gives <code>True</code>, by setting the first argument of the AST to
		 * <code>arg</code>.
		 * 
		 */
		public boolean apply(final IExpr arg) {
			final IAST ast = fAST.clone();
			ast.add(arg);
			return fEngine.evalTrue(ast);
		}

	}

	/**
	 * Check if the evaluation of the <code>expr</code> object gives <code>True</code>. If <code>expr</code> is a symbol, which has
	 * an assigned <code>Predicate</code> evaluator object, the predicate will be returned. Otherwise a <code>IsUnaryTrue</code>
	 * predicate will be returned.
	 * 
	 * @param expr
	 * @return
	 * @see IsUnaryTrue
	 */
	public static Predicate<IExpr> isTrue(IExpr expr) {
		return new IsUnaryTrue<IExpr>(EvalEngine.get(), expr);
	}

	/**
	 * Check if the evaluation of the <code>expr</code> object gives <code>True</code>. If <code>expr</code> is a symbol, which has
	 * an assigned <code>Predicate</code> evaluator object, the predicate will be returned. Otherwise a <code>IsUnaryTrue</code>
	 * predicate will be returned.
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
	 * Returns a predicate that evaluates to {@code true} if the object reference being tested is one of the arguments of the given
	 * <code>ast</code>. It does not defensively copy the collection passed in, so future changes to it will alter the behavior of
	 * the predicate.
	 * 
	 * @param ast
	 *            the AST those arguments may contain the function input
	 */
	public static Predicate<IExpr> in(IAST ast) {
		return new InASTPredicate(ast);
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if the object reference being tested is one of the arguments of the given
	 * <code>ast</code>. It does not defensively copy the collection passed in, so future changes to it will alter the behavior of
	 * the predicate.
	 * 
	 * @param expr
	 *            the expr which may macth the function input
	 */
	public static Predicate<IExpr> in(IExpr expr) {
		return new InASTPredicate(F.List(expr));
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if the input is an <code>instanceof IPattern</code>.
	 * 
	 * @return
	 */
	public static Predicate<IExpr> isPattern() {
		return new Predicate<IExpr>() {
			@Override
			public boolean apply(IExpr input) {
				return (input instanceof IPattern);
			}
		};
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if <code>input.isNumber()</code> gives {@code true}.
	 * 
	 * @return
	 */
	public static Predicate<IExpr> isNumber() {
		return new Predicate<IExpr>() {
			@Override
			public boolean apply(IExpr input) {
				return input.isNumber();
			}
		};
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if <code>input.isNumeric()</code> gives {@code true}.
	 * 
	 * @return
	 */
	public static Predicate<IExpr> isNumeric() {
		return new Predicate<IExpr>() {
			@Override
			public boolean apply(IExpr input) {
				return input.isNumeric();
			}
		};
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if <code>input.isSignedNumber()</code> gives {@code true}.
	 * 
	 * @return
	 */
	public static Predicate<IExpr> isSignedNumber() {
		return new Predicate<IExpr>() {
			@Override
			public boolean apply(IExpr input) {
				return input.isSignedNumber();
			}
		};
	}

	/**
	 * Returns a predicate that evaluates to {@code true} if the <code>input</code> is an AST list, which contains one of the given
	 * <b>header elements</b> at index position <code>0</code>.
	 * 
	 */
	public static Predicate<IExpr> isAST(final ISymbol[] heads) {
		return new Predicate<IExpr>() {
			@Override
			public boolean apply(IExpr input) {
				for (int i = 0; i < heads.length; i++) {
					if (input.isAST(heads[i])) {
						return true;
					}
				}
				return false;
			}
		};
	}
}
