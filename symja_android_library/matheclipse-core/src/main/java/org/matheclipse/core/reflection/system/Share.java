package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

/**
 * Try to share common sub-<code>IASTs</code> expressions with the same
 * object-id internally to minimize memory consumption. Returns the number f
 * shared sub-expressions
 *
 */
public class Share extends AbstractFunctionEvaluator {
	static class ShareFunction implements Function<IExpr, IExpr> {
		java.util.Map<IExpr, IExpr> map;

		public ShareFunction() {
			map = new HashMap<IExpr, IExpr>();
		}

		@Override
		public IExpr apply(IExpr t) {
			IExpr value = map.get(t);
			if (value == null) {
				map.put(t, t);
			} else {
				if (value == t) {
					return null;
				}
			}
			return value;
		}
	}

	/**
	 * Replace all occurrences of expressions where the given
	 * <code>function.apply()</code> method returns a non <code>F.NIL</code>
	 * value. The visitors <code>visit()</code> methods return
	 * <code>F.NIL</code> if no substitution occurred.
	 */
	static class ShareReplaceAll extends VisitorExpr {
		final Function<IExpr, IExpr> fFunction;
		final int fOffset;
		public int fCounter;

		public ShareReplaceAll(Function<IExpr, IExpr> function) {
			this(function, 0);
		}

		public ShareReplaceAll(Function<IExpr, IExpr> function, int offset) {
			super();
			this.fFunction = function;
			this.fOffset = offset;
			fCounter = 0;
		}

		public IExpr visit(IInteger element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		public IExpr visit(IFraction element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		public IExpr visit(IComplex element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		public IExpr visit(INum element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		public IExpr visit(IComplexNum element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		public IExpr visit(ISymbol element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		public IExpr visit(IPattern element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		public IExpr visit(IPatternSequence element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		public IExpr visit(IStringX element) {
			return null;
		}

		@Override
		public IExpr visit(IAST ast) {
			IExpr temp = fFunction.apply(ast);
			if (temp != null) {
				return temp;
			}
			return visitAST(ast);
		}

		protected IExpr visitAST(IAST ast) {
			IExpr temp;
			boolean evaled = false;
			int i = fOffset;
			while (i < ast.size()) {
				temp = ast.get(i);
				if (temp.isAST()) {
					temp = temp.accept(this);
					if (temp != null) {
						// share the object with the same id:
						ast.set(i, temp);
						evaled = true;
						fCounter++;
					}
				}
				i++;
			}
			return evaled ? ast : null;
		}
	}

	public Share() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isAST()) {
			return F.integer(shareAST((IAST) ast.arg1()));
		}
		return F.C0;
	}

	/**
	 * Try to share common sub-<code>IASTs</code> expressions with the same
	 * object-id internally to minimize memory consumption and return the number
	 * of shared sub-expressions
	 *
	 * @param ast
	 *            the ast whose internal memory consumption should be minimized
	 * @return the number of shared sub-expressions
	 */
	public static int shareAST(final IAST ast) {
		ShareReplaceAll sra = new ShareReplaceAll(new ShareFunction(), 1);
		ast.accept(sra);
		return sra.fCounter;
	}

}
