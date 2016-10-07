package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class YuleDissimilarity extends AbstractEvaluator {

	public YuleDissimilarity() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		int dim1 = ast.arg1().isVector();
		int dim2 = ast.arg2().isVector();
		if (dim1 == dim2 && dim1 > 0) {
			IAST u = (IAST) ast.arg1();
			IAST v = (IAST) ast.arg2();
			int length = u.size();
			int n10 = 0;
			int n01 = 0;
			int n00 = 0;
			int n11 = 0;
			IExpr x, y;
			for (int i = 1; i < length; i++) {
				x = u.get(i);
				y = v.get(i);
				if ((x.isOne() || x.isTrue()) && (y.isZero() || y.isFalse())) {
					n10++;
					continue;
				}
				if ((x.isZero() || x.isFalse()) && (y.isOne() || y.isTrue())) {
					n01++;
					continue;
				}
				if ((x.isZero() || x.isFalse()) && (y.isZero() || y.isFalse())) {
					n00++;
					continue;
				}
				if ((x.isOne() || x.isTrue()) && (y.isOne() || y.isTrue())) {
					n11++;
					continue;
				}
				if ((x.isZero() || x.isFalse() || x.isOne() || x.isTrue())
						&& (y.isZero() || y.isFalse() || y.isOne() || y.isTrue())) {
					continue;
				}
				return F.NIL;
			}

			long r = 2L * (long) n10 * (long) n01;
			if (r==0L) {
				return F.C0;
			}
			return F.Divide(F.integer(r), F.integer((long) n11 * (long) n00 + (long) n10 * (long) n01));
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
