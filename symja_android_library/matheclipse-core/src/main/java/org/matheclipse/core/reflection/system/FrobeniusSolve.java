package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.Lists;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

import cc.redberry.core.math.frobenius.FrobeniusSolver;

/**
 * 
 * 
 */
public class FrobeniusSolve implements IFunctionEvaluator {

	public FrobeniusSolve() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);

		if (ast.arg1().isList()) {
			IAST list = ast.getAST(1);
			try {
				int[][] equations = new int[1][list.size()];
				// { { 12, 16, 20, 27, 123 } };
				for (int i = 1; i < list.size(); i++) {
					equations[0][i - 1] = ((ISignedNumber) list.get(i)).toInt();
				}
				equations[0][list.size() - 1] = ((ISignedNumber) ast.get(2)).toInt();
				int numberOfSolutions = -1; // all solutions
				if (ast.size() == 4) {
					numberOfSolutions = ((ISignedNumber) ast.get(3)).toInt();
				}

				FrobeniusSolver solver = new FrobeniusSolver(equations);
				int[] solution;

				IAST result = F.List();
				if (numberOfSolutions < 0) {
					while ((solution = solver.take()) != null) {
						// System.out.println(Arrays.toString(solution));
						result.add(Lists.asList(solution));
					}
				} else {
					while ((solution = solver.take()) != null) {
						if (--numberOfSolutions < 0) {
							break;
						}
						// System.out.println(Arrays.toString(solution));
						result.add(Lists.asList(solution));
					}
				}

				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {

	}

}
