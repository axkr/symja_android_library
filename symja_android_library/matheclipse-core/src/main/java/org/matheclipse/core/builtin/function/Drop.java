package org.matheclipse.core.builtin.function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.ISequence;
import org.matheclipse.core.eval.util.Sequence;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Drop(list,n) - delete the first n arguments from the list. Negative n counts from the end.
 * 
 */
public class Drop extends AbstractCoreFunctionEvaluator {

	public Drop() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		final IExpr arg1 = F.eval(ast.arg1());
		try {
			if (arg1.isAST()) {
				final ISequence sequ = Sequence.createSequence(ast.arg2());
				final IAST list = (IAST) arg1;
				if (sequ != null) {
					final IAST resultList = list.clone();
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
