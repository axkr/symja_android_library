package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Lists;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.frobenius.FrobeniusSolver;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * FrobeniusSolve(listOfIntegers, nonNegativeInteger)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * get a list of solutions for the Frobenius equation given by the <code>listOfIntegers</code> and the
 * <code>nonNegativeInteger</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; FrobeniusSolve({2, 3, 4}, 29)
 * {{0,3,5},{0,7,2},{1,1,6},{1,5,3},{1,9,0},{2,3,4},{2,7,1},{3,1,5},{3,5,2},{4,3,3},{
 * 4,7,0},{5,1,4},{5,5,1},{6,3,2},{7,1,3},{7,5,0},{8,3,1},{9,1,2},{10,3,0},{11,1,1},{
 * 13,1,0}}
 * </pre>
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
				list.forEach((x, i) -> equations[0][i - 1] = (IInteger) x);
				equations[0][list.argSize()] = (IInteger) ast.arg2();
				int numberOfSolutions = -1; // all solutions
				if (ast.size() == 4) {
					numberOfSolutions = ((ISignedNumber) ast.arg3()).toInt();
				}

				FrobeniusSolver solver = new FrobeniusSolver(equations);
				IInteger[] solution;

				IASTAppendable result = F.ListAlloc(8);
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
