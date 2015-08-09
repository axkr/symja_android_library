package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.analysis.UnivariateFunction;
import org.apache.commons.math4.analysis.integration.RombergIntegrator;
import org.apache.commons.math4.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math4.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math4.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math4.analysis.integration.gauss.GaussIntegrator;
import org.apache.commons.math4.analysis.integration.gauss.GaussIntegratorFactory;
import org.apache.commons.math4.exception.ConvergenceException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Function for <a href="http://en.wikipedia.org/wiki/Numerical_integration">numerical integration</a> of univariate real functions.
 * 
 * Uses the <a href=
 * "http://commons.apache.org/math/apidocs/org/apache/commons/math/analysis/integration/UnivariateRealIntegratorImpl.html" >Commons
 * math LegendreGaussIntegrator, RombergIntegrator, SimpsonIntegrator, TrapezoidIntegrator</a> implementations.
 */
public class NIntegrate extends AbstractFunctionEvaluator {

	public NIntegrate() {
	}

	public final static ISymbol LegendreGauss = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "legendregauss"
			: "LegendreGauss");

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);

		ISymbol method = LegendreGauss;
		if (ast.size() == 4 && ast.arg3().isSymbol()) {
			method = (ISymbol) ast.arg3();
		}
		if ((ast.arg2().isList())) {
			IAST list = (IAST) ast.arg2();
			IExpr function = ast.arg1();
			if (list.size() == 4 && list.arg1().isSymbol()) {
				ISignedNumber min = list.arg2().evalSignedNumber();
				ISignedNumber max = list.arg3().evalSignedNumber();
				if (min != null && max != null) {
					if (function.isAST(F.Equal, 3)) {
						function = F.Plus(((IAST) function).arg1(), F.Negate(((IAST) function).arg2()));
					}
					try {
						return Num.valueOf(integrate(method.getSymbolName(), list, min.doubleValue(), max.doubleValue(), function));
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

		}
		return null;
	}

	/**
	 * Integrate a function numerically.
	 * 
	 * @param method
	 *            the following methods are possible: LegendreGauss, Simpson, Romberg, Trapezoid
	 * @param list
	 *            a list of the form <code>{x, lowerBound, upperBound}</code>, where <code>lowerBound</code> and
	 *            <code>upperBound</code> are numbers which could be converted to a Java double value.
	 * @param function
	 *            the function which should be integrated.
	 * @return
	 * @throws ConvergenceException
	 */
	public static double integrate(String method, IAST list, double min, double max, IExpr function) throws ConvergenceException {
		GaussIntegratorFactory factory = new GaussIntegratorFactory();

		ISymbol xVar = (ISymbol) list.arg1();
		// ISignedNumber min = (ISignedNumber) list.arg2();
		// ISignedNumber max = (ISignedNumber) list.arg3();
		final EvalEngine engine = EvalEngine.get();
		function = F.eval(function);
		UnivariateFunction f = new UnaryNumerical(function, xVar, engine);
		// if (method.equalsIgnoreCase("legendregauss")) {
		// GaussIntegrator integ = factory.legendre(7, min.doubleValue(), max.doubleValue());
		// return integ.integrate(f);
		// }

		UnivariateIntegrator integrator = null;
		if (method.equalsIgnoreCase("Simpson")) {
			integrator = new SimpsonIntegrator();
		} else if (method.equalsIgnoreCase("Romberg")) {
			integrator = new RombergIntegrator();
		} else if (method.equalsIgnoreCase("Trapezoid")) {
			integrator = new TrapezoidIntegrator();
		} else {
			// default: LegendreGauss
			GaussIntegrator integ = factory.legendre(7, min, max);
			return integ.integrate(f);
		}
		return integrator.integrate(10000, f, min, max);

	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDFIRST);
	}
}