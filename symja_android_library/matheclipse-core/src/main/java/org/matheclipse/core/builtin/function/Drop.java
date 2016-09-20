package org.matheclipse.core.builtin.function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IllegalArgument;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.ISequence;
import org.matheclipse.core.eval.util.Sequence;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Drop(list,n) - delete the first n arguments from the list. Negative n counts
 * from the end.
 * 
 */
public class Drop extends AbstractCoreFunctionEvaluator {

	public Drop() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);

		IAST evaledAST = (IAST) engine.evalAttributes(F.Drop, ast);
		if (!evaledAST.isPresent()) {
			evaledAST = ast;
		}
		final IExpr arg1 = evaledAST.arg1();
		try {
			if (arg1.isAST()) {
				final ISequence[] sequ = Sequence.createSequences(evaledAST, 2);
				final IAST list = (IAST) arg1;
				if (sequ != null) {
					final IAST resultList = list.clone();
					drop(resultList, 0, sequ);
					return resultList;
				}
			}
		} catch (final IllegalArgument e) {
			engine.printMessage(e.getMessage());
			return F.NIL;
		} catch (final Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.NHOLDREST);
	}

	/**
	 * Drop (remove) the list elements according to the
	 * <code>sequenceSpecifications</code> for the list indexes.
	 * 
	 * @param list
	 * @param level
	 *            recursion level
	 * @param sequenceSpecifications
	 *            one or more ISequence specifications
	 * @return
	 */
	private IAST drop(final IAST list, final int level, final ISequence[] sequenceSpecifications) {
		sequenceSpecifications[level].setListSize(list.size());
		final int newLevel = level + 1;
		int j = sequenceSpecifications[level].getStart();
		int step = sequenceSpecifications[level].getStep();
		for (int i = j; i < sequenceSpecifications[level].getEnd(); i += step) {
			list.remove(j);
			j += step - 1;
		}
		for (int j2 = 1; j2 < list.size(); j2++) {
			if (sequenceSpecifications.length > newLevel) {
				if (list.get(j2).isAST()) {
					final IAST tempList = ((IAST) list.get(j2)).clone();
					list.set(j2, drop(tempList, newLevel, sequenceSpecifications));
				} else {
					throw new IllegalArgument("Cannot execute drop for argument: " + list.get(j2).toString());
				}
			}
		}
		return list;
	}
}
