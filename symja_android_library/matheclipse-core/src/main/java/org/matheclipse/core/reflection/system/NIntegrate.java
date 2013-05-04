package org.matheclipse.core.reflection.system;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.analysis.integration.LegendreGaussIntegrator;
import org.apache.commons.math3.analysis.integration.RombergIntegrator;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.exception.ConvergenceException;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IConstantHeaders;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Function for <a
 * href="http://en.wikipedia.org/wiki/Numerical_integration">numerical
 * integration</a> of univariate real functions.
 * 
 * Uses the <a href="http://commons.apache.org/math/apidocs/org/apache/commons/math/analysis/integration/UnivariateRealIntegratorImpl.html"
 * >Commons math LegendreGaussIntegrator, RombergIntegrator, SimpsonIntegrator,
 * TrapezoidIntegrator</a> implementations.
 */
public class NIntegrate extends AbstractFunctionEvaluator implements IConstantHeaders {

	public NIntegrate() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);

		String method = "Trapezoid";
		if (ast.size() == 4 && ast.get(3).isSymbol()) {
			method = ast.get(3).toString();
		}
		if ((ast.get(2).isList())) {
			IAST list = (IAST) ast.get(2);
			IExpr function = ast.get(1);
			if (list.size() == 4 && list.get(1).isSymbol() && list.get(2).isSignedNumber() && list.get(3).isSignedNumber()) {
				if (function.isAST(F.Equal, 3)) {
					function = F.Plus(((IAST) function).get(1), F.Times(F.CN1, ((IAST) function).get(2)));
				}
				try {
					return Num.valueOf(integrate(method, list, function));
				} catch (ConvergenceException e) {
					throw new WrappedException(e);
				} catch (Exception e) {
					throw new WrappedException(e);
					// if (Config.SHOW_STACKTRACE) {
					// e.printStackTrace();
					// }
				}
			}

		}
		return null;
	}

	private double integrate(String method, IAST list, IExpr function) throws ConvergenceException {
		ISymbol xVar = (ISymbol) list.get(1);
		ISignedNumber min = (ISignedNumber) list.get(2);
		ISignedNumber max = (ISignedNumber) list.get(3);
		final EvalEngine engine = EvalEngine.get();
		function = F.eval(function);
		DifferentiableUnivariateFunction f = new UnaryNumerical(function, xVar, engine);
		UnivariateIntegrator integrator = new TrapezoidIntegrator();
		if (method.equals("Simpson")) {
			integrator = new SimpsonIntegrator();
		} else if (method.equals("LegendreGauss")) {
			integrator = new LegendreGaussIntegrator(3, BaseAbstractUnivariateIntegrator.DEFAULT_RELATIVE_ACCURACY,
					BaseAbstractUnivariateIntegrator.DEFAULT_ABSOLUTE_ACCURACY,
					BaseAbstractUnivariateIntegrator.DEFAULT_MIN_ITERATIONS_COUNT, 64);
		} else if (method.equals("Romberg")) {
			integrator = new RombergIntegrator();
		} else {
			// default: TrapezoidIntegrator
		}
		return integrator.integrate(10000, f, min.doubleValue(), max.doubleValue());

	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDFIRST);
	}
}