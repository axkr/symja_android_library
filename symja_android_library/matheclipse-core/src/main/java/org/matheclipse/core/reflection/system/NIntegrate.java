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
import org.matheclipse.core.eval.util.Options;
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

	public static final int DEFAULT_MAX_POINTS = 100;
	public static final int DEFAULT_MAX_ITERATIONS = 10000;
	public NIntegrate() {
	}

	public final static ISymbol LegendreGauss = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "legendregauss"
			: "LegendreGauss");

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);

		ISymbol method = LegendreGauss;
		int maxPoints = DEFAULT_MAX_POINTS;
		int maxIterations = DEFAULT_MAX_ITERATIONS;
		if (ast.size() >= 4) { // && ast.arg3().isSymbol()) {
			// method = (ISymbol) ast.arg3();
			final Options options = new Options(ast.topHead(), ast, 3, engine);
			IExpr option = options.getOption("Method");
			if (option.isSymbol()) {
				method = (ISymbol) option;
			}
			option = options.getOption("MaxPoints");
			if (option.isSignedNumber()) {
				try {
					maxPoints = ((ISignedNumber) option).toInt();
				} catch (ArithmeticException ae) {
					engine.printMessage("Error in option MaxPoints. Using default value: " + maxPoints);
				}
			}
			option = options.getOption("MaxIterations");
			if (option.isSignedNumber()) {
				try {
					maxIterations = ((ISignedNumber) option).toInt();
				} catch (ArithmeticException ae) {
					engine.printMessage("Error in option MaxIterations. Using default value: " + maxIterations);
				}
			}
		}

		if ((ast.arg2().isList())) {
			IAST list = (IAST) ast.arg2();
			IExpr function = ast.arg1();
			if (list.isAST3() && list.arg1().isSymbol()) {
				ISignedNumber min = list.arg2().evalSignedNumber();
				ISignedNumber max = list.arg3().evalSignedNumber();
				if (min != null && max != null) {
					if (function.isAST(F.Equal, 3)) {
						function = F.Plus(((IAST) function).arg1(), F.Negate(((IAST) function).arg2()));
					}
					try {
						return Num.valueOf(integrate(method.getSymbolName(), list, min.doubleValue(), max.doubleValue(), function,
								maxPoints, maxIterations));
					} catch (ConvergenceException e) {
						throw new WrappedException(e);
					} catch (Exception e) {
						throw new WrappedException(e);
					}
				}
			}

		}
		return F.NIL;
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
	 * @param maxPoints
	 *            maximum number of points
	 * @param maxIterations
	 *            maximum number of iterations
	 * @return
	 * @throws ConvergenceException
	 */
	public static double integrate(String method, IAST list, double min, double max, IExpr function, int maxPoints,
			int maxIterations) throws ConvergenceException {
		GaussIntegratorFactory factory = new GaussIntegratorFactory();

		ISymbol xVar = (ISymbol) list.arg1(); 
		final EvalEngine engine = EvalEngine.get();
		function = F.eval(function);
		UnivariateFunction f = new UnaryNumerical(function, xVar, engine); 

		UnivariateIntegrator integrator = null;
		if (method.equalsIgnoreCase("Simpson")) {
			integrator = new SimpsonIntegrator();
		} else if (method.equalsIgnoreCase("Romberg")) {
			integrator = new RombergIntegrator();
		} else if (method.equalsIgnoreCase("Trapezoid")) {
			integrator = new TrapezoidIntegrator();
		} else {
			// default: LegendreGauss
			GaussIntegrator integ = factory.legendre(maxPoints, min, max);
			return integ.integrate(f);
		}
		return integrator.integrate(maxIterations, f, min, max);

	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDFIRST);
	}
}