package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class VectorAnalysisFunctions {
	static {
		F.Curl.setEvaluator(new Curl());
		F.Div.setEvaluator(new Div());
		F.Grad.setEvaluator(new Grad());
	}

	/**
	 * <pre>
	 * Curl({f1, f2}, {x1, x2})
	 * 
	 * Curl({f1, f2, f3}, {x1, x2, x3})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the curl.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Curl_%28mathematics%29">Wikipedia - Curl (mathematics)</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Curl({f(u,v,w),f(v,w,u),f(w,u,v),f(x)}, {u,v,w})
	 * {-D(f(v,w,u),w)+D(f(w,u,v),v),-D(f(w,u,v),u)+D(f(u,v,w),w),-D(f(u,v,w),v)+D(f(v,w,u),u),f(x)}
	 * </pre>
	 */
	private static final class Curl extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			if (ast.arg1().isVector() >= 3) {
				if (ast.arg2().isVector() == 3) {
					IAST variables = (IAST) ast.arg2();
					IAST vector = (IAST) ast.arg1();
					IASTAppendable curlVector = F.ListAlloc(vector.size());
					curlVector.append(
							F.Subtract(F.D(vector.arg3(), variables.arg2()), F.D(vector.arg2(), variables.arg3())));
					curlVector.append(
							F.Subtract(F.D(vector.arg1(), variables.arg3()), F.D(vector.arg3(), variables.arg1())));
					curlVector.append(
							F.Subtract(F.D(vector.arg2(), variables.arg1()), F.D(vector.arg1(), variables.arg2())));
					for (int i = 4; i < vector.size(); i++) {
						curlVector.append(vector.get(i));
					}
					return curlVector;
				}
			}

			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Div({f1, f2, f3,...},{x1, x2, x3,...})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * compute the divergence.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:<br />
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Divergence">Wikipedia - Divergence</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Div({x^2, y^3},{x, y})
	 * 2*x+3*y^2
	 * </pre>
	 */
	private static final class Div extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			if ((ast.arg1().isVector() == ast.arg2().isVector()) && (ast.arg1().isVector() >= 0)) {
				IAST vector = (IAST) ast.arg1();
				IAST variables = (IAST) ast.arg2();
				int size = vector.size();
				IASTAppendable divergenceValue = F.PlusAlloc(size);
				return divergenceValue.appendArgs(size, i -> F.D(vector.get(i), variables.get(i)));
			}

			return F.NIL;
		}

	}

	private static final class Grad extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr function = ast.arg1();
			if (ast.arg2().isVector() > 0) {
				IAST variables = (IAST) ast.arg2();
				IASTAppendable dList = F.ListAlloc(variables.argSize());
				for (int i = 1; i < variables.size(); i++) {
					dList.append(engine.evaluate(F.D(function, variables.get(i))));
				}
				return dList;
			}

			return F.NIL;
		}

	}

	private final static VectorAnalysisFunctions CONST = new VectorAnalysisFunctions();

	public static VectorAnalysisFunctions initialize() {
		return CONST;
	}

	private VectorAnalysisFunctions() {

	}

}
