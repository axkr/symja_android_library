package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Sequence;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.generic.Algorithms;
import org.matheclipse.generic.interfaces.ISequence;

public class Drop extends AbstractFunctionEvaluator {

	public Drop() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		try {
			if (ast.get(1).isAST()) {
				final ISequence sequ = Sequence.createSequence(ast.get(2));
				final IAST arg1 = (IAST) ast.get(1);
				if (sequ != null) {
					final IAST resultList = arg1.clone();
					Algorithms.drop(resultList, sequ);
					return resultList;
				}
			}
		} catch (final Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.NHOLDREST);
	}
}
