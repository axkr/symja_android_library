package org.matheclipse.core.reflection.system;

import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;

public class Out extends AbstractArg1 {

	@Override
	public IExpr e1DblArg(final INum iNum) {
		try {
			int i = iNum.toInt();
			final EvalEngine engine = EvalEngine.get();
			final List<IExpr> list = engine.getOutList();
			if ((i > 0) && (list.size() >= i)) {
				return list.get(i - 1);
			}
			if ((i < 0) && (list.size() >= (-i))) {
				return list.get(list.size() + i);
			}
		} catch (final Exception ae) {

		}
		return null;
	}

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
		} catch (final Exception ae) {

		}
		return null;
	}

}