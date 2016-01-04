package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.visit.AbstractVisitorLong;

/**
 * Count the number of leaves of an expression.
 * 
 */
public class LeafCount extends AbstractCoreFunctionEvaluator {

	/**
	 * Calculate the number of leaves in an AST
	 */
	public static class LeafCountVisitor extends AbstractVisitorLong {
		int fHeadOffset;

		public LeafCountVisitor() {
			this(1);
		}

		public LeafCountVisitor(int hOffset) {
			fHeadOffset = hOffset;
		}

		@Override
		public long visit(IFraction element) {
			return 3;
		}

		@Override
		public long visit(IComplex element) {
			return 3;
		}

		@Override
		public long visit(IComplexNum element) {
			return 3;
		}

		@Override
		public long visit(IAST list) {
			long sum = 0;
			for (int i = fHeadOffset; i < list.size(); i++) {
				sum += list.get(i).accept(this);
			}
			return sum;
		}
	}

	public LeafCount() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		return F.integer(engine.evaluate(ast.arg1()).leafCount());
	}

}
