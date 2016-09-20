package org.matheclipse.core.reflection.system;

import java.util.Comparator;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class Commonest extends AbstractEvaluator {

	public Commonest() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);
		IAST list = Validate.checkListType(ast, 1);

		int n = -1;
		if (ast.isAST2()) {
			n = Validate.checkIntType(ast.arg2());
		}

		IAST tallyResult = Tally.tally1Arg(list);
		EvalAttributes.sort(tallyResult, new Comparator<IExpr>() {
			@Override
			public int compare(IExpr o1, IExpr o2) {
				return ((IAST) o2).arg2().compareTo(((IAST) o1).arg2());
			}
		});

		IAST result = F.List();
		if (tallyResult.size() > 1) {
			if (n == -1) {
				IInteger max = (IInteger) ((IAST) tallyResult.arg1()).arg2();
				result.add(((IAST) tallyResult.arg1()).arg1());
				for (int i = 2; i < tallyResult.size(); i++) {
					if (max.equals(((IAST) tallyResult.get(i)).arg2())) {
						result.add(((IAST) tallyResult.get(i)).arg1());
					} else {
						break;
					}
				}
			} else {
				int counter = 0;
				for (int i = 1; i < tallyResult.size(); i++) {
					if (counter < n) {
						result.add(((IAST) tallyResult.get(i)).arg1());
						counter++;
					} else {
						break;
					}
				}
			}
		}
		return result;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
