package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Lists;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.frobenius.FrobeniusSolver;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * 
 */
public class FrobeniusSolve extends AbstractEvaluator {

	public FrobeniusSolve() {
		// default ctor
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);
		if (ast.arg1().isList()) {
			IAST list = ast.getAST(1);
			try {
				IInteger[][] equations = new IInteger[1][list.size()];
				// format looks like: { { 12, 16, 20, 27, 123 } };
				for (int i = 1; i < list.size(); i++) {
					equations[0][i - 1] = (IInteger) list.get(i);
				}
				equations[0][list.size() - 1] = (IInteger) ast.arg2();
				int numberOfSolutions = -1; // all solutions
				if (ast.size() == 4) {
					numberOfSolutions = ((ISignedNumber) ast.arg3()).toInt();
				}

				FrobeniusSolver solver = new FrobeniusSolver(equations);
				IInteger[] solution;

				IAST result = F.List();
				if (numberOfSolutions < 0) {
					while ((solution = solver.take()) != null) {
						result.append(Lists.asList(solution));
					}
				} else {
					while ((solution = solver.take()) != null) {
						if (--numberOfSolutions < 0) {
							break;
						}
						result.append(Lists.asList(solution));
					}
				}

				return result;
			} catch (RuntimeException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void setUp(final ISymbol newSymbol) {

	}

}
