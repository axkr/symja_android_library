package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.ISequence;
import org.matheclipse.core.eval.util.Sequence;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Take extends AbstractFunctionEvaluator {

	public Take() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);

		try {
			if (ast.arg1().isAST()) {
				final ISequence[] sequ = Sequence.createSequences(ast, 2);
				final IAST arg1 = (IAST) ast.arg1();
				if (sequ != null) {
					return take(arg1, 0, sequ);
				}
			}
		} catch (final Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public IAST take(final IAST list, final int level, final ISequence[] sequ) {
		sequ[level].setListSize(list.size());
		final IAST resultList = list.copyHead();
		final int newLevel = level + 1;
		for (int i = sequ[level].getStart(); i < sequ[level].getEnd(); i += sequ[level].getStep()) {
			if (sequ.length > newLevel) {
				if (list.get(i).isAST()) {
					resultList.add(take((IAST) list.get(i), newLevel, sequ));
				}
			} else {
				resultList.add(list.get(i));
			}
		}
		return resultList;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.NHOLDREST);
	}
}
