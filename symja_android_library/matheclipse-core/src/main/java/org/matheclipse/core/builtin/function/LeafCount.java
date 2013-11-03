package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.visit.AbstractVisitorInt;

public class LeafCount extends AbstractCoreFunctionEvaluator {

	/**
	 * Calculate the number of leaves in an AST
	 */
	public static class LeafCountVisitor extends AbstractVisitorInt {
		int fHeadOffset;

		public LeafCountVisitor() {
			this(1);
		}

		public LeafCountVisitor(int hOffset) {
			fHeadOffset = hOffset;
		}

		@Override
		public int visit(IFraction element) {
			return 3;
		}

		@Override
		public int visit(IComplex element) {
			return 3;
		}

		@Override
		public int visit(IComplexNum element) {
			return 3;
		}

		public int visit(IAST list) {
			int sum = 0;
			for (int i = fHeadOffset; i < list.size(); i++) {
				sum += list.get(i).accept(this);
			}
			return sum;
		}
	}

	public LeafCount() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		return F.integer(leafCount(F.eval(ast.arg1())));
	}

	public static int leafCount(IExpr expr) {
		int leafCount = 0;
		if (expr.isAST()) {
			leafCount = expr.accept(new LeafCountVisitor(0));
		} else {
			leafCount = expr.isAtom() ? 1 : 0;
		}
		return leafCount;
	}
}
