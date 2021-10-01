package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.differentiation.UnivariateDifferentiableFunction;
import org.hipparchus.analysis.solvers.BaseAbstractUnivariateSolver;
import org.hipparchus.analysis.solvers.BisectionSolver;
import org.hipparchus.analysis.solvers.BracketingNthOrderBrentSolver;
import org.hipparchus.analysis.solvers.IllinoisSolver;
import org.hipparchus.analysis.solvers.MullerSolver;
import org.hipparchus.analysis.solvers.NewtonRaphsonSolver;
import org.hipparchus.analysis.solvers.PegasusSolver;
import org.hipparchus.analysis.solvers.RegulaFalsiSolver;
import org.hipparchus.analysis.solvers.RiddersSolver;
import org.hipparchus.analysis.solvers.SecantSolver;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * FindRoot(f, {x, xmin, xmax})
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * searches for a numerical root of <code>f</code> for the variable <code>x</code>, in the range
 * <code>xmin</code> to <code>xmax</code>.
 *
 * </blockquote>
 *
 * <pre>
 * FindRoot(f, {x, xmin, xmax}, MaxIterations-&gt;maxiter)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * searches for a numerical root of <code>f</code> for the variable <code>x</code>, with <code>
 * maxiter</code> iterations. The default maximum iteraton is <code>100</code>.
 *
 * </blockquote>
 *
 * <pre>
 * FindRoot(f, {x, xmin, xmax}, Method-&gt;method_name)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * searches for a numerical root of <code>f</code> for the variable <code>x</code>, with one of the
 * following method names:
 *
 * </blockquote>
 *
 * <h4>Brent</h4>
 *
 * <p>
 * Implements the Brent algorithm for finding zeros of real univariate functions (<code>
 * BracketingNthOrderBrentSolver</code>). The function should be continuous but not necessarily
 * smooth. The solve method returns a zero <code>x</code> of the function <code>f</code> in the
 * given interval <code>[xmin, xmax]</code>.
 *
 * <p>
 * This is the default method, if no <code>method_name</code> is given.
 *
 * <h4>Newton</h4>
 *
 * <p>
 * Implements Newton's method for finding zeros of real univariate functions. The function should be
 * continuous but not necessarily smooth.
 *
 * <h4>Bisection</h4>
 *
 * <p>
 * Implements the bisection algorithm for finding zeros of univariate real functions. The function
 * should be continuous but not necessarily smooth.
 *
 * <h4>Muller</h4>
 *
 * <p>
 * Implements the Muller's Method for root finding of real univariate functions. For reference, see
 * Elementary Numerical Analysis, ISBN 0070124477, chapter 3. Muller's method applies to both real
 * and complex functions, but here we restrict ourselves to real functions. Muller's original method
 * would have function evaluation at complex point. Since our <code>f(x)</code> is real, we have to
 * find ways to avoid that. Bracketing condition is one way to go: by requiring bracketing in every
 * iteration, the newly computed approximation is guaranteed to be real. Normally Muller's method
 * converges quadratically in the vicinity of a zero, however it may be very slow in regions far
 * away from zeros. For example, <code>FindRoot(Exp(x)-1 == 0,{x,-50,100}, Method-&gt;Muller)
 * </code>. In such case we use bisection as a safety backup if it performs very poorly. The
 * formulas here use divided differences directly.
 *
 * <h4>Ridders</h4>
 *
 * <p>
 * Implements the Ridders' Method for root finding of real univariate functions. For reference, see
 * C. Ridders, A new algorithm for computing a single root of a real continuous function, IEEE
 * Transactions on Circuits and Systems, 26 (1979), 979 - 980. The function should be continuous but
 * not necessarily smooth.
 *
 * <h4>Secant</h4>
 *
 * <p>
 * Implements the Secant method for root-finding (approximating a zero of a univariate real
 * function). The solution that is maintained is not bracketed, and as such convergence is not
 * guaranteed.
 *
 * <h4>RegulaFalsi</h4>
 *
 * <p>
 * Implements the Regula Falsi or False position method for root-finding (approximating a zero of a
 * univariate real function). It is a modified Secant method. The Regula Falsi method is included
 * for completeness, for testing purposes, for educational purposes, for comparison to other
 * algorithms, etc. It is however not intended to be used for actual problems, as one of the bounds
 * often remains fixed, resulting in very slow convergence. Instead, one of the well-known modified
 * Regula Falsi algorithms can be used (Illinois or Pegasus). These two algorithms solve the
 * fundamental issues of the original Regula Falsi algorithm, and greatly out-performs it for most,
 * if not all, (practical) functions. Unlike the Secant method, the Regula Falsi guarantees
 * convergence, by maintaining a bracketed solution. Note however, that due to the finite/limited
 * precision of Java's double type, which is used in this implementation, the algorithm may get
 * stuck in a situation where it no longer makes any progress. Such cases are detected and result in
 * a ConvergenceException exception being thrown. In other words, the algorithm theoretically
 * guarantees convergence, but the implementation does not. The Regula Falsi method assumes that the
 * function is continuous, but not necessarily smooth.
 *
 * <h4>Illinois</h4>
 *
 * <p>
 * Implements the Illinois method for root-finding (approximating a zero of a univariate real
 * function). It is a modified Regula Falsi method. Like the Regula Falsi method, convergence is
 * guaranteed by maintaining a bracketed solution. The Illinois method however, should converge much
 * faster than the original Regula Falsi method. Furthermore, this implementation of the Illinois
 * method should not suffer from the same implementation issues as the Regula Falsi method, which
 * may fail to convergence in certain cases. The Illinois method assumes that the function is
 * continuous, but not necessarily smooth.
 *
 * <h4>Pegasus</h4>
 *
 * <p>
 * Implements the Pegasus method for root-finding (approximating a zero of a univariate real
 * function). It is a modified Regula Falsi method. Like the Regula Falsi method, convergence is
 * guaranteed by maintaining a bracketed solution. The Pegasus method however, should converge much
 * faster than the original Regula Falsi method. The Pegasus method should converge faster than the
 * Illinois method, another Regula Falsi-based method. The Pegasus method assumes that the function
 * is continuous, but not necessarily smooth.
 *
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
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="Factor.md">Factor</a>, <a href="Eliminate.md">Eliminate</a>,
 * <a href="NRoots.md">NRoots</a>, <a href="Solve.md">Solve</a>
 */
public class FindRoot extends AbstractFunctionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public FindRoot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // default: BracketingNthOrderBrentSolver
    String method = "Brent";
    int maxIterations = 100;
    if (ast.size() >= 4) {
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
      maxIterations = options.getOptionMaxIterations(S.MaxIterations);
      if (maxIterations == Integer.MIN_VALUE) {
        return F.NIL;
      }
      if (maxIterations < 0) {
        maxIterations = 100;
      }

      IExpr optionMethod = options.getOption(S.Method);
      if (optionMethod.isSymbol()) {
        method = optionMethod.toString();
      } else {
        if (ast.arg3().isSymbol()) {
          method = ast.arg3().toString();
        }
      }
    }

    IExpr arg2 = engine.evaluate(ast.arg2());
    if ((arg2.isList())) {
      IAST list = (IAST) arg2;
      if (list.size() >= 3 && list.arg1().isSymbol()) {
        ISignedNumber min = list.arg2().evalReal();
        if (min != null) {
          ISignedNumber max = null;
          if (list.size() > 3) {
            max = list.arg3().evalReal();
          }
          IExpr function = engine.evaluate(ast.arg1());
          if (function.isEqual()) {
            IAST equalAST = (IAST) function;
            function = F.Plus(equalAST.arg1(), F.Negate(equalAST.arg2()));
          }
          try {
            return F.List(F.Rule(list.arg1(),
                Num.valueOf(findRoot(method, maxIterations, list, min, max, function, engine))));
          } catch (MathIllegalStateException miae) {
            // `1`.
            return IOFunctions.printMessage(
                ast.topHead(), "error", F.List(F.$str(miae.getMessage())), engine);
          } catch (MathIllegalArgumentException miae) {
            // `1`.
            IOFunctions.printMessage(
                ast.topHead(), "error", F.List(F.$str(miae.getMessage())), engine);
            return F.CEmptyList;
          } catch (ValidateException ve) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), ve);
          } catch (MathRuntimeException mre) {
            LOGGER.log(engine.getLogLevel(), "FindRoot", mre);
          }
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  private double findRoot(String method, int maxIterations, IAST list, ISignedNumber min,
      ISignedNumber max, IExpr function, EvalEngine engine) {
    ISymbol xVar = (ISymbol) list.arg1();
    IAssumptions oldAssumptions = engine.getAssumptions();
    try {
      IAssumptions assum = Assumptions.getInstance(F.Element(xVar, S.Reals));
      engine.setAssumptions(assum);
      function = engine.evaluate(function);
      UnivariateDifferentiableFunction f = new UnaryNumerical(function, xVar, engine, true);
      BaseAbstractUnivariateSolver<UnivariateFunction> solver = null;
      if (method.equalsIgnoreCase("Bisection")) {
        solver = new BisectionSolver();
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
      } else if (max == null || method.equalsIgnoreCase("Newton")) {
        try {
          NewtonRaphsonSolver nrs = new NewtonRaphsonSolver();
          if (max == null) {
            return nrs.solve(maxIterations, f, min.doubleValue());
          }
          return nrs.solve(maxIterations, f, min.doubleValue(), max.doubleValue());
        } catch (MathRuntimeException mex) {
          // switch to BracketingNthOrderBrentSolver
          solver = new BracketingNthOrderBrentSolver();
        }
      } else {
        // default: BracketingNthOrderBrentSolver
        try {
          solver = new BracketingNthOrderBrentSolver();
          return solver.solve(maxIterations, f, min.doubleValue(), max.doubleValue());
        } catch (MathRuntimeException mex) {
          // org.hipparchus.exception.MathIllegalArgumentException: interval does not bracket a root

          if (mex instanceof org.hipparchus.exception.MathIllegalArgumentException) {
            if (min != null) {
              try {
                NewtonRaphsonSolver nrs = new NewtonRaphsonSolver();
                return nrs.solve(maxIterations, f, min.doubleValue(), max.doubleValue());
              } catch (MathRuntimeException mre) {
              }
            }
          }

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
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
