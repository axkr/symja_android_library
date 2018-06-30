package org.matheclipse.core.reflection.system;

import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.solvers.BaseAbstractUnivariateSolver;
import org.hipparchus.analysis.solvers.BisectionSolver;
import org.hipparchus.analysis.solvers.BrentSolver;
import org.hipparchus.analysis.solvers.IllinoisSolver;
import org.hipparchus.analysis.solvers.MullerSolver;
import org.hipparchus.analysis.solvers.PegasusSolver;
import org.hipparchus.analysis.solvers.RegulaFalsiSolver;
import org.hipparchus.analysis.solvers.RiddersSolver;
import org.hipparchus.analysis.solvers.SecantSolver;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.matheclipse.commons.math.analysis.solvers.DifferentiableUnivariateFunction;
import org.matheclipse.commons.math.analysis.solvers.NewtonSolver;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * FindRoot(f, {x, xmin, xmax})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * searches for a numerical root of <code>f</code> for the variable <code>x</code>, in the range <code>xmin</code> to
 * <code>xmax</code>.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * FindRoot(f, {x, xmin, xmax}, MaxIterations-&gt;maxiter)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * searches for a numerical root of <code>f</code> for the variable <code>x</code>, with <code>maxiter</code>
 * iterations. The default maximum iteraton is <code>100</code>.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * FindRoot(f, {x, xmin, xmax}, Method-&gt;method_name)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * searches for a numerical root of <code>f</code> for the variable <code>x</code>, with one of the following method
 * names:
 * </p>
 * </blockquote>
 * <h4>Newton</h4>
 * <p>
 * Implements Newton's method for finding zeros of real univariate functions. The function should be continuous but not
 * necessarily smooth. This is the default method, if no method name is given
 * </p>
 * <h4>Bisection</h4>
 * <p>
 * Implements the bisection algorithm for finding zeros of univariate real functions. The function should be continuous
 * but not necessarily smooth.
 * </p>
 * <h4>Brent</h4>
 * <p>
 * Implements the Brent algorithm for finding zeros of real univariate functions. The function should be continuous but
 * not necessarily smooth. The solve method returns a zero <code>x</code> of the function <code>f</code> in the given
 * interval <code>[xmin, xmax]</code> to within a tolerance <code>6*eps*abs(x)+t</code> where <code>eps</code> is the
 * relative accuracy and <code>t</code> is the absolute accuracy. The given interval must bracket the root.
 * </p>
 * <h4>Muller</h4>
 * <p>
 * Implements the Muller's Method for root finding of real univariate functions. For reference, see Elementary Numerical
 * Analysis, ISBN 0070124477, chapter 3. Muller's method applies to both real and complex functions, but here we
 * restrict ourselves to real functions. Muller's original method would have function evaluation at complex point. Since
 * our <code>f(x)</code> is real, we have to find ways to avoid that. Bracketing condition is one way to go: by
 * requiring bracketing in every iteration, the newly computed approximation is guaranteed to be real. Normally Muller's
 * method converges quadratically in the vicinity of a zero, however it may be very slow in regions far away from zeros.
 * For example, <code>FindRoot(Exp(x)-1 == 0,{x,-50,100}, Method-&gt;Muller)</code>. In such case we use bisection as a
 * safety backup if it performs very poorly. The formulas here use divided differences directly.
 * </p>
 * <h4>Ridders</h4>
 * <p>
 * Implements the Ridders' Method for root finding of real univariate functions. For reference, see C. Ridders, A new
 * algorithm for computing a single root of a real continuous function, IEEE Transactions on Circuits and Systems, 26
 * (1979), 979 - 980. The function should be continuous but not necessarily smooth.
 * </p>
 * <h4>Secant</h4>
 * <p>
 * Implements the Secant method for root-finding (approximating a zero of a univariate real function). The solution that
 * is maintained is not bracketed, and as such convergence is not guaranteed.
 * </p>
 * <h4>RegulaFalsi</h4>
 * <p>
 * Implements the Regula Falsi or False position method for root-finding (approximating a zero of a univariate real
 * function). It is a modified Secant method. The Regula Falsi method is included for completeness, for testing
 * purposes, for educational purposes, for comparison to other algorithms, etc. It is however not intended to be used
 * for actual problems, as one of the bounds often remains fixed, resulting in very slow convergence. Instead, one of
 * the well-known modified Regula Falsi algorithms can be used (Illinois or Pegasus). These two algorithms solve the
 * fundamental issues of the original Regula Falsi algorithm, and greatly out-performs it for most, if not all,
 * (practical) functions. Unlike the Secant method, the Regula Falsi guarantees convergence, by maintaining a bracketed
 * solution. Note however, that due to the finite/limited precision of Java's double type, which is used in this
 * implementation, the algorithm may get stuck in a situation where it no longer makes any progress. Such cases are
 * detected and result in a ConvergenceException exception being thrown. In other words, the algorithm theoretically
 * guarantees convergence, but the implementation does not. The Regula Falsi method assumes that the function is
 * continuous, but not necessarily smooth.
 * </p>
 * <h4>Illinois</h4>
 * <p>
 * Implements the Illinois method for root-finding (approximating a zero of a univariate real function). It is a
 * modified Regula Falsi method. Like the Regula Falsi method, convergence is guaranteed by maintaining a bracketed
 * solution. The Illinois method however, should converge much faster than the original Regula Falsi method.
 * Furthermore, this implementation of the Illinois method should not suffer from the same implementation issues as the
 * Regula Falsi method, which may fail to convergence in certain cases. The Illinois method assumes that the function is
 * continuous, but not necessarily smooth.
 * </p>
 * <h4>Pegasus</h4>
 * <p>
 * Implements the Pegasus method for root-finding (approximating a zero of a univariate real function). It is a modified
 * Regula Falsi method. Like the Regula Falsi method, convergence is guaranteed by maintaining a bracketed solution. The
 * Pegasus method however, should converge much faster than the original Regula Falsi method. The Pegasus method should
 * converge faster than the Illinois method, another Regula Falsi-based method. The Pegasus method assumes that the
 * function is continuous, but not necessarily smooth.
 * </p>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; FindRoot(Exp(x)==Pi^3,{x,-1,10}, Method-&gt;Bisection)
 * {x-&gt;3.434189647436142}
 * 
 * &gt;&gt; FindRoot(Sin(x), {x, -0.5, 0.5})
 * {x-&gt;0.0}
 * </pre>
 * 
 */

public class FindRoot extends AbstractFunctionEvaluator {

	// public final static ISymbol Newton =
	// F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "newton" :
	// "Newton");
	public FindRoot() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);

		String method = "Newton";
		int maxIterations = 100;
		if (ast.size() >= 4) {
			final Options options = new Options(ast.topHead(), ast, 3, engine);
			IExpr optionMaxIterations = options.getOption("MaxIterations");
			if (optionMaxIterations.isReal()) {
				maxIterations = ((ISignedNumber) optionMaxIterations).toInt();
			}
			IExpr optionMethod = options.getOption("Method");
			if (optionMethod.isSymbol()) {
				method = optionMethod.toString();
			} else {
				if (ast.arg3().isSymbol()) {
					method = ast.arg3().toString();
				}
			}
		}
		if ((ast.arg2().isList())) {
			IAST list = (IAST) ast.arg2();
			IExpr function = ast.arg1();
			if (list.size() >= 3 && list.arg1().isSymbol()) {
				if (function.isEqual()) {
					IAST equalAST = (IAST) function;
					function = F.Plus(equalAST.arg1(), F.Negate(equalAST.arg2()));
				}
				ISignedNumber min = list.arg2().evalReal();
				if (min != null) {
					ISignedNumber max = null;
					if (list.size() > 3) {
						max = list.arg3().evalReal();
					}

					try {
						return F.List(F.Rule(list.arg1(),
								Num.valueOf(findRoot(method, maxIterations, list, min, max, function, engine))));
					} catch (MathIllegalArgumentException miae) {
						if (Config.SHOW_STACKTRACE) {
							miae.printStackTrace();
						}
						engine.printMessage("FindRoot: " + miae.getMessage());
						return F.List();
					} catch (MathRuntimeException mre) {
						if (Config.SHOW_STACKTRACE) {
							mre.printStackTrace();
						}
						engine.printMessage("FindRoot: " + mre.getMessage());
					}

				}
			}
		}
		return F.NIL;
	}

	private double findRoot(String method, int maxIterations, IAST list, ISignedNumber min, ISignedNumber max,
			IExpr function, EvalEngine engine) {
		ISymbol xVar = (ISymbol) list.arg1();
		IAssumptions oldAssumptions = engine.getAssumptions();
		try {
			IAssumptions assum = Assumptions.getInstance(F.Element(xVar, F.Reals));
			engine.setAssumptions(assum);
			function = engine.evaluate(function);
			UnivariateFunction f = new UnaryNumerical(function, xVar, engine);
			BaseAbstractUnivariateSolver<UnivariateFunction> solver = null;
			if (method.equalsIgnoreCase("Bisection")) {
				solver = new BisectionSolver();
			} else if (method.equalsIgnoreCase("Brent")) {
				solver = new BrentSolver();
				// } else if (method.isSymbolName("Laguerre")) {
				// solver = new LaguerreSolver();
			} else if (method.equalsIgnoreCase("Muller")) {
				solver = new MullerSolver();
			} else if (method.equalsIgnoreCase("Ridders")) {
				solver = new RiddersSolver();
			} else if (method.equalsIgnoreCase("Secant")) {
				solver = new SecantSolver();
			} else if (method.equalsIgnoreCase("RegulaFalsi")) {
				solver = new RegulaFalsiSolver();
			} else if (method.equalsIgnoreCase("Illinois")) {
				solver = new IllinoisSolver();
			} else if (method.equalsIgnoreCase("Pegasus")) {
				solver = new PegasusSolver();
			} else {
				// default: NewtonSolver
				try {
					DifferentiableUnivariateFunction fNewton = new UnaryNumerical(function, xVar, engine);
					BaseAbstractUnivariateSolver<DifferentiableUnivariateFunction> solver2 = new NewtonSolver();
					if (max == null) {
						return solver2.solve(maxIterations, fNewton, min.doubleValue());
					}
					return solver2.solve(maxIterations, fNewton, min.doubleValue(), max.doubleValue());
				} catch (MathRuntimeException mex) {
					// switch to BisectionSolver
					solver = new BisectionSolver();
				}
			}
			if (max == null) {
				return solver.solve(maxIterations, f, min.doubleValue());
			}
			return solver.solve(maxIterations, f, min.doubleValue(), max.doubleValue());
		} finally {
			engine.setAssumptions(oldAssumptions);
		}
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDFIRST);
	}
}