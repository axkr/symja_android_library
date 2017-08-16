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
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.ISymbol2IntMap;
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
		public long visit(IAST list) {
			long sum = 0;
			for (int i = fHeadOffset; i < list.size(); i++) {
				sum += list.get(i).accept(this);
			}
			return sum;
		}

		@Override
		public long visit(IComplex element) {
			return element.leafCount();
		}

		@Override
		public long visit(IComplexNum element) {
			return element.leafCount();
		}

		@Override
		public long visit(IFraction element) {
			return element.leafCount();
		}
	}

	/**
	 * Calculate the number of leaves in an AST
	 */
	public static class SimplifyLeafCountVisitor extends AbstractVisitorLong {
		int fHeadOffset;

		public SimplifyLeafCountVisitor() {
			this(1);
		}

		public SimplifyLeafCountVisitor(int hOffset) {
			fHeadOffset = hOffset;
		}

		@Override
		public long visit(IAST list) {
			long sum = 0;
			for (int i = fHeadOffset; i < list.size(); i++) {
				sum += list.get(i).accept(this);
			}
			return sum;
		}

		@Override
		public long visit(IComplex element) {
			return element.leafCountSimplify();
		}

		@Override
		public long visit(IComplexNum element) {
			return 3;
		}

		@Override
		public long visit(IFraction element) {
			return element.leafCountSimplify();
		}

		@Override
		public long visit(IInteger element) {
			return element.leafCountSimplify();
		}
	}

	public static class SimplifyLeafCountPatternMapVisitor extends AbstractVisitorLong {

		int fHeadOffset;

		ISymbol2IntMap fPatternMap;

		public SimplifyLeafCountPatternMapVisitor(ISymbol2IntMap patternMap, int hOffset) {
			fHeadOffset = hOffset;
			fPatternMap = patternMap;
		}

		@Override
		public long visit(IAST list) {
			long sum = 0L;
			// if (list.isAnd()) {
			// sum = 1L;
			// }
			for (int i = fHeadOffset; i < list.size(); i++) {
				sum += list.get(i).accept(this);
			}
			return sum;
		}

		@Override
		public long visit(IComplex element) {
			return element.leafCountSimplify();
		}

		@Override
		public long visit(IComplexNum element) {
			return 3;
		}

		@Override
		public long visit(IFraction element) {
			return element.leafCountSimplify();
		}

		@Override
		public long visit(IInteger element) {
			return element.leafCountSimplify();
		}

		@Override
		public long visit(ISymbol element) {
			return element.leafCountSimplify();
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
