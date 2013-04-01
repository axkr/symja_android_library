package org.matheclipse.core.reflection.system;

import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class Out extends AbstractArg1 {

	@Override
	public IExpr e1IntArg(final IInteger ii) {
		try {
			int i = ii.toInt();
			final EvalEngine engine = EvalEngine.get();
			final List<IExpr> list = engine.getOutList();
			if ((i > 0) && (list.size() >= i)) {
				return list.get(i - 1);
			}
			if ((i < 0) && (list.size() >= (-i))) {
				return list.get(list.size() + i);
			}
		} catch (final ArithmeticException ae) {

		}
		return null;
	}

}