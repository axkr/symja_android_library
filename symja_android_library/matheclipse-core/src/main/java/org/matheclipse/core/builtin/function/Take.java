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

public class Take extends AbstractCoreFunctionEvaluator {

	public Take() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);

		IAST evaledAST = (IAST) engine.evalAttributes(F.Drop, ast);
		if (!evaledAST.isPresent()) {
			evaledAST = ast;
		}
		try {
			if (evaledAST.arg1().isAST()) {
				final ISequence[] sequ = Sequence.createSequences(evaledAST, 2);
				final IAST arg1 = (IAST) evaledAST.arg1();
				if (sequ != null) {
					return take(arg1, 0, sequ);
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

	/**
	 * Take the list elements according to the
	 * <code>sequenceSpecifications</code> for the list indexes.
	 * 
	 * @param list
	 * @param level
	 *            recursion level
	 * @param sequenceSpecifications
	 *            one or more ISequence specifications
	 * @return
	 */
	public IAST take(final IAST list, final int level, final ISequence[] sequenceSpecifications) {
		sequenceSpecifications[level].setListSize(list.size());
		final IAST resultList = list.copyHead();
		final int newLevel = level + 1;
		for (int i = sequenceSpecifications[level].getStart(); i < sequenceSpecifications[level]
				.getEnd(); i += sequenceSpecifications[level].getStep()) {
			if (sequenceSpecifications.length > newLevel) {
				if (list.get(i).isAST()) {
					resultList.add(take((IAST) list.get(i), newLevel, sequenceSpecifications));
				} else {
					throw new IllegalArgument("Cannot execute take for argument: " + list.get(i).toString());
				}
			} else {
				resultList.add(list.get(i));
			}
		}
		return resultList;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.NHOLDREST);
	}
}
