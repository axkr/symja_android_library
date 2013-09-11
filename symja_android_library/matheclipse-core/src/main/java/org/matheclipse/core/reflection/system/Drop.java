package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.ISequence;
import org.matheclipse.core.eval.util.Sequence;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

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
					drop(resultList, sequ);
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

	/**
	 * Drop (remove) the list elements according to the <code>sequenceSpecification</code> for the list indexes.
	 * 
	 * @param <IExpr>
	 * @param list
	 * @param resultList
	 * @param sequenceSpecification
	 */
	public static IAST drop(final IAST resultList, final ISequence sequenceSpecification) {
		sequenceSpecification.setListSize(resultList.size());
		int j = sequenceSpecification.getStart();
		for (int i = j; i < sequenceSpecification.getEnd(); i += sequenceSpecification.getStep()) {
			resultList.remove(j);
			j += sequenceSpecification.getStep() - 1;
		}
		return resultList;
	}
}
