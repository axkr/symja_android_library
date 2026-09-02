package org.matheclipse.core.reflection.system;

import java.util.Map;
import java.util.function.Supplier;
import org.hipparchus.analysis.UnivariateFunction;
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
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import com.google.common.collect.ImmutableMap;

/**
 * <pre>
 * <code>FindRoot(f, {x, xmin, xmax})
 * </code>
 * </pre>
 * 
 * <p>
 * searches for a numerical root of <code>f</code> for the variable <code>x</code>, in the range
 * <code>xmin</code> to <code>xmax</code>.
 * </p>
 * 
 * <pre>
 * <code>FindRoot(f, {x, xmin, xmax}, MaxIterations-&gt;maxiter)
 * </code>
 * </pre>
 * 
 * <p>
 * searches for a numerical root of <code>f</code> for the variable <code>x</code>, with
 * <code>maxiter</code> iterations. The default maximum iteraton is <code>100</code>.
 * </p>
 * 
 * <pre>
 * <code>FindRoot(f, {x, xmin, xmax}, Method-&gt;method_name)
 * </code>
 * </pre>
 * 
 * <p>
 * searches for a numerical root of <code>f</code> for the variable <code>x</code>, with one of the
 * method names listed below.
 * </p>
 * 
 * <pre>
 * <code>FindRoot({f(x1,x2,...), g(x1,x2,...), ...}, {{x1, initialValue1}, {x2, initialValue2}, ...})
 * </code>
 * </pre>
 * 
 * <p>
 * searches a multivariate root with Newton's iteration method for a differentiable, multivariate,
 * vector-valued function.
 * </p>
 * 
 * <pre>
 * <code>FindRoot({f(x1,x2,...), g(x1,x2,...), ...}, {x1, initialValue1}, {x2, initialValue2}, ...)
 * </code>
 * </pre>
 * 
 * <p>
 * the same search, with each start specification written as its own argument.
 * </p>
 * 
 * <p>
 * A start specification may name a second value, <code>{x, initialValue, secondValue}</code>. For
 * the bracketing methods listed below it is the other end of the interval to search; otherwise it
 * sets the width of the first step of the difference quotient described below, and the later steps
 * determine their own width.
 * </p>
 * 
 * <p>
 * The default method differentiates the equations. A function which is only defined for numeric
 * arguments - one guarded by <code>NumericQ</code>, or one which wraps a numerical solver like
 * <code>NDSolve</code> - has no derivative to differentiate, so the difference quotient is used
 * instead: for one variable that is the secant method, and for several it is a jacobian matrix
 * built by finite differences once and then kept up to date with the rank one updates of Broyden's
 * method - one evaluation of the equations per step, where a fresh finite difference matrix would
 * cost one per entry. This matters when evaluating the equations is expensive. Steps are halved
 * until they make the residual smaller. Complex start values keep the differentiated jacobian
 * matrix. The same difference quotient iteration takes over for one variable when the derivative
 * is not a finite number at the start value, where Newton's method could not take a step.
 * </p>
 * 
 * <p>
 * If <code>MaxIterations</code> is used up before the search converges, <code>FindRoot</code>
 * reports it and returns the best point it reached.
 * </p>
 * 
 * <p>
 * See
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Root-finding_algorithm">Wikipedia - Root-finding
 * algorithm</a></li>
 * <li><a href="https://en.wikipedia.org/wiki/Newton%27s_method#k_variables,_k_functions">Wikipedia
 * - Newton's method - k_variables, _k_functions</a></li>
 * <li><a href="https://en.wikipedia.org/wiki/Broyden%27s_method">Wikipedia - Broyden's
 * method</a></li>
 * </ul>
 * <h4>Brent</h4>
 * <p>
 * Implements the Brent algorithm for finding zeros of real univariate functions
 * (<code>BracketingNthOrderBrentSolver</code>). The function should be continuous but not
 * necessarily smooth. The solve method returns a zero <code>x</code> of the function <code>f</code>
 * in the given interval <code>[xmin, xmax]</code>.
 * </p>
 * <p>
 * This is the default method, if no <code>method_name</code> is given.
 * </p>
 * <h4>Newton</h4>
 * <p>
 * Implements Newton's method for finding zeros of real univariate functions. The function should be
 * continuous but not necessarily smooth.
 * </p>
 * <h4>Bisection</h4>
 * <p>
 * Implements the bisection algorithm for finding zeros of univariate real functions. The function
 * should be continuous but not necessarily smooth.
 * </p>
 * <h4>Muller</h4>
 * <p>
 * Implements the Muller's Method for root finding of real univariate functions. For reference, see
 * Elementary Numerical Analysis, ISBN 0070124477, chapter 3. Muller's method applies to both real
 * and complex functions, but here we restrict ourselves to real functions. Muller's original method
 * would have function evaluation at complex point. Since our <code>f(x)</code> is real, we have to
 * find ways to avoid that. Bracketing condition is one way to go: by requiring bracketing in every
 * iteration, the newly computed approximation is guaranteed to be real. Normally Muller's method
 * converges quadratically in the vicinity of a zero, however it may be very slow in regions far
 * away from zeros. For example,
 * <code>FindRoot(Exp(x)-1 == 0,{x,-50,100}, Method-&gt;Muller)</code>. In such case we use
 * bisection as a safety backup if it performs very poorly. The formulas here use divided
 * differences directly.
 * </p>
 * <h4>Ridders</h4>
 * <p>
 * Implements the Ridders' Method for root finding of real univariate functions. For reference, see
 * C. Ridders, A new algorithm for computing a single root of a real continuous function, IEEE
 * Transactions on Circuits and Systems, 26 (1979), 979 - 980. The function should be continuous but
 * not necessarily smooth.
 * </p>
 * <h4>Secant</h4>
 * <p>
 * Implements the Secant method for root-finding (approximating a zero of a univariate real
 * function). The solution that is maintained is not bracketed, and as such convergence is not
 * guaranteed.
 * </p>
 * <h4>RegulaFalsi</h4>
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
 * </p>
 * <h4>Illinois</h4>
 * <p>
 * Implements the Illinois method for root-finding (approximating a zero of a univariate real
 * function). It is a modified Regula Falsi method. Like the Regula Falsi method, convergence is
 * guaranteed by maintaining a bracketed solution. The Illinois method however, should converge much
 * faster than the original Regula Falsi method. Furthermore, this implementation of the Illinois
 * method should not suffer from the same implementation issues as the Regula Falsi method, which
 * may fail to convergence in certain cases. The Illinois method assumes that the function is
 * continuous, but not necessarily smooth.
 * </p>
 * <h4>Pegasus</h4>
 * <p>
 * Implements the Pegasus method for root-finding (approximating a zero of a univariate real
 * function). It is a modified Regula Falsi method. Like the Regula Falsi method, convergence is
 * guaranteed by maintaining a bracketed solution. The Pegasus method however, should converge much
 * faster than the original Regula Falsi method. The Pegasus method should converge faster than the
 * Illinois method, another Regula Falsi-based method. The Pegasus method assumes that the function
 * is continuous, but not necessarily smooth.
 * </p>
 * <h3>Examples</h3>
 * 
 * <pre>
 * <code>&gt;&gt; FindRoot(Exp(x)==Pi^3,{x,-1,10}, Method-&gt;Bisection)
 * {x-&gt;3.434189647436142}
 * 
 * &gt;&gt; FindRoot(Sin(x), {x, -0.5, 0.5})
 * {x-&gt;0.0} 
 * </code>
 * </pre>
 * <p>
 * Using Newton's method for finding the root of a differentiable, multivariate, vector-valued
 * function.
 * </p>
 * 
 * <pre>
 * <code>&gt;&gt; FindRoot({2*x1+x2==E^(-x1), -x1+2*x2==E^(-x2)},{{x1, 0.0},{x2, 1.0}})
 * {x1-&gt;0.197594,x2-&gt;0.425514}
 * 
 * &gt;&gt; FindRoot({Exp(-Exp(-(x1+x2)))-x2*(1+x1^2), x1*Cos(x2)+x2*Sin(x1)-0.5},{x1,x2})
 * {x1-&gt;0.353247,x2-&gt;0.606082}
 * </code>
 * </pre>
 * 
 * <h3>Related terms</h3>
 * <p>
 * <a href="Factor.md">Factor</a>, <a href="Eliminate.md">Eliminate</a>,
 * <a href="NRoots.md">NRoots</a>, <a href="Solve.md">Solve</a>
 * </p>
 */
public class FindRoot extends AbstractFunctionOptionEvaluator {

  private static class UnivariateSolverSupplier implements Supplier<IExpr> {
    final IExpr originalFunction;
    final IAST variableList;
    /**
     * Starting point or minimum of a user defined interval; <code>min</code> is not allowed to be
     * <code>null</code>
     */
    final IReal min;
    /**
     * Maximum of a user defined interval; the maximum can be <code>null</code>, if no interval was
     * defined
     */
    final IReal maxMaybeNull;
    final int maxIterations;
    final String method;
    final double accuracy;
    final EvalEngine engine;

    /**
     * 
     * @param function
     * @param variableList
     * @param min starting point or minimum of a given interval; <code>min</code> is not allowed to
     *        be <code>null</code>
     * @param max maximum of a given interval; <code>max</code> can be <code>null</code>, if no
     *        interval was defined
     * @param maxIterations
     * @param method
     * @param accuracyGoal TODO
     * @param engine
     */
    public UnivariateSolverSupplier(IExpr function, IAST variableList, IReal min, IReal max,
        int maxIterations, String method, int accuracyGoal, EvalEngine engine) {
      this.originalFunction = function;
      this.variableList = variableList;
      this.min = min;
      this.maxMaybeNull = max;
      this.maxIterations = maxIterations;
      this.method = method;
      this.accuracy = accuracy(accuracyGoal);
      this.engine = engine;
    }

    @Override
    public IExpr get() {
      ISymbol xVar = (ISymbol) variableList.arg1();
      IAssumptions oldAssumptions = engine.getAssumptions();
      try {
        IAssumptions assum = Assumptions.getInstance(F.Element(xVar, S.Reals));
        engine.setAssumptions(assum);
        IExpr function = engine.evaluate(originalFunction);
        if (function.isEqual()) {
          IAST equalAST = (IAST) function;
          function = F.Plus(equalAST.arg1(), F.Negate(equalAST.arg2()));
        }
        UnaryNumerical f = new UnaryNumerical(function, xVar, true, true, Double.NaN, engine);
        BaseAbstractUnivariateSolver<UnivariateFunction> solver = null;
        if (method.equalsIgnoreCase("Brent")) {
          solver = new BracketingNthOrderBrentSolver(accuracy, 5);
        } else if (method.equalsIgnoreCase("Bisection")) {
          solver = new BisectionSolver(accuracy);
          // } else if (method.isSymbolName("Laguerre")) {
          // solver = new LaguerreSolver();
        } else if (method.equalsIgnoreCase("Muller")) {
          solver = new MullerSolver(accuracy);
        } else if (method.equalsIgnoreCase("Ridders")) {
          solver = new RiddersSolver(accuracy);
        } else if (method.equalsIgnoreCase("Secant")) {
          solver = new SecantSolver(accuracy);
        } else if (method.equalsIgnoreCase("RegulaFalsi")) {
          solver = new RegulaFalsiSolver(accuracy);
        } else if (method.equalsIgnoreCase("Illinois")) {
          solver = new IllinoisSolver(accuracy);
        } else if (method.equalsIgnoreCase("Pegasus")) {
          solver = new PegasusSolver(accuracy);
        } else {
          // Newton's method is the only one here which needs a derivative, and a function which is
          // only defined for numeric arguments does not have one which can be evaluated -
          // D(f(x),x) stays f'(x). NewtonRaphsonSolver then spends its whole evaluation budget on a
          // derivative which is never a number, without ever sampling the function itself, and
          // reports that the count was exceeded. Iterate with the difference quotient instead,
          // which is what the multivariate path does for the same reason - Broyden's method for one
          // variable is the secant method, and a second start value sets the width of its first
          // step exactly as it does there.
          double startValue = min.doubleValue();
          if (!Double.isFinite(f.derivative().value(startValue))) {
            double stepWidth = (maxMaybeNull == null) //
                ? 0.0 //
                : Math.abs(maxMaybeNull.doubleValue() - startValue);
            double[] root = new BroydenSolver(F.list(function), F.list(xVar), //
                new double[] {startValue}, //
                new double[] {stepWidth > 1e-14 ? stepWidth : 0.0}, //
                accuracy, maxIterations, engine).solve();
            if (root != null) {
              return F.num(root[0]);
            }
          }
          // try {
          NewtonRaphsonSolver nrs = new NewtonRaphsonSolver(accuracy);
          if (maxMaybeNull == null) {
            return F.num(nrs.solve(maxIterations, f, min.doubleValue()));
          }
          return F.num(nrs.solve(maxIterations, f, min.doubleValue(), maxMaybeNull.doubleValue()));
          // } catch (MathRuntimeException mex) {
          // // switch to BracketingNthOrderBrentSolver
          // solver = new BracketingNthOrderBrentSolver(accuracy, 5);
          // }
          // } else {
          // // default: NewtonRaphsonSolver
          // try {
          // NewtonRaphsonSolver nrs = new NewtonRaphsonSolver(accuracy);
          // return F
          // .num(nrs.solve(maxIterations, f, min.doubleValue(), maxMaybeNull.doubleValue()));
          // } catch (MathRuntimeException mex) {
          // // org.hipparchus.exception.MathIllegalArgumentException: interval does not bracket a
          // // root
          //
          // if (mex instanceof org.hipparchus.exception.MathIllegalArgumentException) {
          // MathIllegalArgumentException mie = (MathIllegalArgumentException) mex;
          // // try {
          // // NewtonRaphsonSolver nrs = new NewtonRaphsonSolver(accuracy);
          // // return F.num(
          // // nrs.solve(maxIterations, f, min.doubleValue(), maxMaybeNull.doubleValue()));
          // // } catch (MathRuntimeException mre) {
          // // }
          // return F.NIL;
          // }
          //
          // // solver = new BisectionSolver(accuracy);
          // }
        }

        if (maxMaybeNull == null) {
          return F.num(solver.solve(maxIterations, f, min.doubleValue()));
        }
        return F.num(solver.solve(maxIterations, f, min.doubleValue(), maxMaybeNull.doubleValue()));
      } finally {
        engine.setAssumptions(oldAssumptions);
      }
    }
  }

  /** The number of iterations a search takes when <code>MaxIterations</code> is not given. */
  private static final int DEFAULT_MAX_ITERATIONS = 100;

  /**
   * How often one step of the quasi Newton iteration may be halved before the step is given up on.
   */
  private static final int MAX_BACKTRACK_STEPS = 60;

  public FindRoot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    // default: BracketingNthOrderBrentSolver
    String method = "Newton";
    int accuracyGoal = 6;
    // S.MaxIterations
    int maxIterations = optionMaxIterations(options[0], engine);
    if (maxIterations < 0) {
      return F.NIL;
    }
    if (options[2] != S.Automatic) {
      if (options[2].isInteger()) {
        // S.AccuracyGoal
        accuracyGoal = options[2].toIntDefault();
      } else {
        // Value of option `1` is not Automatic or a machine-sized integer.
        return Errors.printMessage(S.FindRoot, "accg", F.List(F.Rule(S.AccuracyGoal, options[2])),
            engine);
      }
    }

    // The search specifications are the leading run of list arguments behind the equations. One
    // list is the single variable form FindRoot(f, {x,x0}) or the nested multivariate form
    // FindRoot({f1,f2}, {{x,x0},{y,y0}}); several are the multivariate form Mathematica documents,
    // FindRoot({f1,f2}, {x,x0}, {y,y0}), which is collected into the same nested shape here so
    // that only one of them has to be understood further down.
    int lastSpecPosition = 1;
    for (int i = 2; i < ast.size(); i++) {
      if (!ast.get(i).isList()) {
        break;
      }
      lastSpecPosition = i;
    }
    if (lastSpecPosition < 2) {
      // Search specification `1` should be a list with 1 to 3 elements.
      return Errors.printMessage(S.FindRoot, "fdss", F.List(ast.arg2()), engine);
    }

    if (!options[1].isAutomatic()) {
      if (options[1].isSymbol() || options[1].isString()) {
        // S.Method
        method = options[1].toString();
      }
    } else if (lastSpecPosition < ast.argSize() && ast.last().isSymbol()) {
      // FindRoot(f, {x, xmin, xmax}, methodName) - the bare method name behind the specifications
      method = ast.last().toString();
    }

    IAST listOfEquations = ast.arg1().makeList();
    final IAST finalAST = ast;
    IAST arg2 = lastSpecPosition == 2 //
        ? (IAST) ast.arg2() //
        : F.mapRange(2, lastSpecPosition + 1, i -> finalAST.get(i));
    boolean needsComplexComputation = needsComplexComputation(listOfEquations, arg2);
    if (!arg2.isListOfLists()) {
      arg2 = F.List(arg2);
    }
    int l1 = listOfEquations.isVector();
    int l2 = arg2.argSize();
    if ((needsComplexComputation || l2 > 1) && l1 == l2 && listOfEquations.isList()
        && arg2.isList()) {
      double accuracy = accuracy(accuracyGoal);
      return multivariateFindRoot(listOfEquations, arg2, accuracy, maxIterations, engine);
    } else if ((arg2.isList2() || arg2.isList3()) && !arg2.isListOfLists()) {
      return univariateFindRoot(ast.arg1(), arg2, method, maxIterations, accuracyGoal, engine);
    } else if (arg2.isList1() && (arg2.first().isList2() || arg2.first().isList3())) {
      return univariateFindRoot(ast.arg1(), (IAST) arg2.first(), method, maxIterations,
          accuracyGoal, engine);
    }
    return F.NIL;
  }

  /**
   * Determine the value of the <code>MaxIterations</code> option.
   *
   * @param option the value of the {@link S#MaxIterations} option
   * @param engine the evaluation engine
   * @return the maximum number of iterations, {@link Integer#MAX_VALUE} for {@link S#Infinity}, or
   *         <code>-1</code> if the value is not a valid option value - the <code>ioppfa</code>
   *         message was printed in that case
   */
  private static int optionMaxIterations(IExpr option, EvalEngine engine) {
    if (option.isAutomatic()) {
      return DEFAULT_MAX_ITERATIONS;
    }
    if (option.isInfinity()) {
      return Integer.MAX_VALUE;
    }
    if (option.isInteger()) {
      int maxIterations = option.toIntDefault();
      if (maxIterations > 0) {
        return maxIterations;
      }
    }
    // The value of the option MaxIterations -> `1` should be a positive integer, Infinity or
    // Automatic.
    Errors.printMessage(S.FindRoot, "ioppfa", F.list(option), engine);
    return -1;
  }

  /**
   * Test if the computation requires complex number calculations.
   * 
   * @param listOfEquations
   * @param varValuePairs
   * @return
   */
  private static boolean needsComplexComputation(IAST listOfEquations, IAST varValuePairs) {
    boolean needsComplexComputation = false;
    for (int i = 1; i < listOfEquations.size(); i++) {
      if (listOfEquations.get(i).hasComplexNumber()) {
        needsComplexComputation = true;
      }
    }
    if (varValuePairs.isList2() && !varValuePairs.isListOfLists()) {
      if (!needsComplexComputation) {
        IExpr startValue = varValuePairs.second();
        if (Double.isNaN(startValue.evalfNaN())) {
          needsComplexComputation = true;
        }
      }
    }
    return needsComplexComputation;
  }

  private static IExpr univariateFindRoot(IExpr listOfEquations, IAST varValuePairs, String method,
      int maxIterations, int accuracyGoal, final EvalEngine engine) {
    IAST list = varValuePairs;
    if (list.size() >= 2 && list.arg1().isSymbol()) {
      IReal min = F.CD1;
      if (list.argSize() > 1) {
        min = list.arg2().evalReal();
      }
      if (min != null) {
        IReal max = null;
        if (list.size() > 3) {
          max = list.arg3().evalReal();
        }
        try {
          UnivariateSolverSupplier optimizeSupplier = new UnivariateSolverSupplier(listOfEquations,
              list, min, max, maxIterations, method, accuracyGoal, engine);
          IExpr result = optimizeSupplier.get();
          // engine.evalBlock(optimizeSupplier, list);
          return F.list(F.Rule(list.arg1(), result));
        } catch (MathIllegalStateException mise) {
          if (mise.getSpecifier() == LocalizedCoreFormats.CONVERGENCE_FAILED) {
            Object[] parts = mise.getParts();
            if (parts != null && parts.length >= 1) {
              // Failed to converge to the requested accuracy or precision within `1` iterations.
              return Errors.printMessage(S.FindRoot, "cvmit", F.list(F.$str(parts[0].toString())),
                  engine);
            }

          }
          // `1`.
          return Errors.printMessage(S.FindRoot, "error", F.list(F.$str(mise.getMessage())),
              engine);
        } catch (MathRuntimeException mre) {
          if (mre.getSpecifier() == LocalizedCoreFormats.NOT_BRACKETING_INTERVAL
              || mre.getSpecifier() == LocalizedCoreFormats.ENDPOINTS_NOT_AN_INTERVAL) {
            // `1` is only applicable for univariate real functions and requires two real starting
            // values that bracket the root.
            return Errors.printMessage(S.FindRoot, "bbrac", F.list(F.Rule(S.Method, method)),
                engine);
          }
          return Errors.printMessage(S.FindRoot, "error", F.list(F.$str(mre.getMessage())), engine);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Call Newton's method for finding the root of a differentiable, multivariate, vector-valued
   * function.
   * <p>
   * See:
   * <a href="https://en.wikipedia.org/wiki/Newton%27s_method#k_variables,_k_functions">Wikipedia -
   * Newton's method - k_variables, _k_functions</a>
   * 
   * @param listOfEquations a list of equations
   * @param matrixOfVarValuePairs a matrix of variables and their initial values
   * @param tolerance the tolerance where the iteration should stop
   * @param maxIterations maximum iterations
   * @param engine
   * @return
   */
  private static IExpr multivariateFindRoot(IAST listOfEquations, IAST matrixOfVarValuePairs,
      double tolerance, int maxIterations, EvalEngine engine) {
    // convert parameters from FindRoot to be suitable for Newtons method
    final int numberOfVariables = matrixOfVarValuePairs.argSize();
    IASTAppendable vectorValuedFunction = F.ListAlloc(numberOfVariables);
    IASTAppendable vectorOfVariables = F.ListAlloc(numberOfVariables);
    IASTAppendable initialGuess = F.ListAlloc(numberOfVariables);
    // the real start values, and the width of the first finite difference step for each of them,
    // for the numerical fallback below; `0.0` asks it to determine a width of its own
    double[] realInitialGuess = new double[numberOfVariables];
    double[] initialStepWidths = new double[numberOfVariables];
    boolean allStartValuesAreReal = true;
    for (int i = 1; i < matrixOfVarValuePairs.size(); i++) {
      IExpr variableInitialGuessPair = matrixOfVarValuePairs.get(i);
      if (variableInitialGuessPair.isList2() || variableInitialGuessPair.isList3()) {
        IExpr variable = variableInitialGuessPair.first();
        if (!variable.isVariable()) {
          // The variables become the keys of a substitution map, so two pairs naming the same
          // non-variable collided there: FindRoot({x,1+Sqrt(2)},{{1,1},{1,1}}) failed with
          // "Multiple entries with same key: 1=1.0 and 1=1.0" from the map builder rather than
          // saying what was wrong with the argument.
          // `1` is not a valid variable.
          return Errors.printMessage(S.FindRoot, "ivar", F.list(variable), engine);
        }
        vectorOfVariables.append(variable);
        IExpr guessedValue = variableInitialGuessPair.second();
        // a non-numeric or non-finite guess falls back to the complex computation
        double doubleValue = guessedValue.evalfNaN();
        if (Double.isFinite(doubleValue)) {
          initialGuess.append(doubleValue);
          realInitialGuess[i - 1] = doubleValue;
        } else {
          Complex complexValue = engine.evalComplex(guessedValue);
          initialGuess.append(complexValue);
          allStartValuesAreReal = false;
        }
        if (variableInitialGuessPair.isList3()) {
          // A second start value {x, x0, x1} only says how wide the first step of the numerical
          // jacobian matrix should be, the way it seeds the secant method in the single variable
          // form. It is deliberately not kept for the later steps: once the iteration has moved
          // away from x0 that width is both coarser than the one determined from the current point
          // and liable to probe outside the range the caller implied was a sensible one.
          double secondValue = variableInitialGuessPair.get(3).evalfNaN();
          if (Double.isFinite(secondValue) && Double.isFinite(doubleValue)) {
            double width = Math.abs(secondValue - doubleValue);
            if (width > 1e-14) {
              initialStepWidths[i - 1] = width;
            }
          }
        }
      } else {
        // Search specification `1` should be a list with 1 to 3 elements.
        return Errors.printMessage(S.FindRoot, "fdss", F.List(variableInitialGuessPair), engine);
      }
      IExpr equation = listOfEquations.get(i);
      if (equation.isEqual()) {
        vectorValuedFunction
            .append(engine.evaluate(F.Subtract(equation.first(), equation.second())));
      } else {
        // assume equation == 0
        vectorValuedFunction.append(equation);
      }
    }

    IExpr result = multivariateNewton(vectorValuedFunction, vectorOfVariables, initialGuess,
        tolerance, maxIterations, engine);
    if (result.isPresent()) {
      return result;
    }
    if (allStartValuesAreReal) {
      // The jacobian matrix could not be determined symbolically, or could not be evaluated to
      // numbers - a function which is only defined for numeric arguments has no derivative to
      // differentiate. Determine the jacobian matrix numerically instead and keep it up to date
      // with Broyden's rank one updates, which costs one function evaluation per step where a
      // fresh finite difference matrix would cost one per entry.
      return new BroydenSolver(vectorValuedFunction, vectorOfVariables, realInitialGuess,
          initialStepWidths, tolerance, maxIterations, engine).get();
    }
    return F.NIL;
  }

  /**
   * Newton's method for finding the root of a differentiable, multivariate, vector-valued function.
   * <p>
   * See:
   * <a href="https://en.wikipedia.org/wiki/Newton%27s_method#k_variables,_k_functions">Wikipedia -
   * Newton's method - k_variables, _k_functions</a>
   * 
   * @param vectorValuedFunction
   * @param vectorOfVariables
   * @param initialGuessVector
   * @param tolerance
   * @param maxIterations maximum iterations
   * @param engine
   * @return
   */
  private static IExpr multivariateNewton(IAST vectorValuedFunction, IAST vectorOfVariables,
      IAST initialGuessVector, double tolerance, int maxIterations, EvalEngine engine) {

    IExpr jacobianMatrix = S.Grad.ofNIL(engine, vectorValuedFunction, vectorOfVariables);
    if (jacobianMatrix.isMatrix(false) == null) {
      return F.NIL;
    }
    final int argSize = vectorOfVariables.argSize();
    IAST xCurr = initialGuessVector.copy();
    // the point with the smallest residual which was seen, and that residual. An iteration which
    // runs out of steps settles on it rather than on wherever it happened to stop.
    IAST bestPoint = F.NIL;
    double bestResidual = Double.MAX_VALUE;
    for (int k = 0; k < maxIterations; k++) {
      Map<IExpr, IExpr> map = createSubsMap(vectorOfVariables, xCurr);
      IExpr fValue = engine.evalN(F.Negate(F.subsList(vectorValuedFunction, map)));
      IExpr jValue = engine.evalN(F.subsList(jacobianMatrix, map));
      if (!isNumericVector(fValue, argSize) || !isNumericMatrix(jValue, argSize)) {
        // Grad builds the jacobian matrix as Outer(D, ...), so a function which cannot be
        // differentiated still gives a matrix - one of unevaluated Derivative(...) entries. Only
        // evaluating it at a point says whether it can be used, and handing a symbolic matrix to
        // LinearSolve below would produce a symbolic step whose Norm is not a number.
        return F.NIL;
      }
      double residual = normOf(fValue, engine);
      if (residual < bestResidual) {
        bestResidual = residual;
        bestPoint = xCurr;
      }
      IExpr y = S.LinearSolve.ofNIL(engine, jValue, fValue);
      if (!isNumericVector(y, argSize)) {
        break;
      }
      IExpr temp = engine.evaluate(F.Plus(xCurr, y));
      if (!isNumericVector(temp, argSize)) {
        break;
      }
      IAST xNext = (IAST) temp;
      double norm = engine.evalDouble(F.Norm(y));
      if (norm < tolerance) {
        // convert result vector to list of rules
        return vectorOfVariables.mapThread(xNext, (a, b) -> F.Rule(a, b));
      }
      xCurr = xNext;
    }
    // The residual of the point the last step reached has not been looked at yet - the loop only
    // sees the residual of a point before stepping away from it - and it is usually the best one.
    // FindRoot(..., MaxIterations->1) would otherwise report the untouched start value.
    Map<IExpr, IExpr> map = createSubsMap(vectorOfVariables, xCurr);
    IExpr fValue = engine.evalN(F.subsList(vectorValuedFunction, map));
    if (isNumericVector(fValue, argSize)) {
      double residual = normOf(fValue, engine);
      if (residual < bestResidual) {
        bestResidual = residual;
        bestPoint = xCurr;
      }
    }
    if (!bestPoint.isPresent()) {
      return F.NIL;
    }
    if (bestResidual > tolerance) {
      // Failed to converge to the requested accuracy or precision within `1` iterations.
      Errors.printMessage(S.FindRoot, "cvmit", F.list(F.ZZ(maxIterations)), engine);
    }
    return vectorOfVariables.mapThread(bestPoint, (a, b) -> F.Rule(a, b));
  }

  /**
   * The euclidean norm of a vector of numbers.
   *
   * @param vector a vector of numbers
   * @param engine the evaluation engine
   * @return the norm, or {@link Double#NaN} if it is not a real number
   */
  private static double normOf(IExpr vector, EvalEngine engine) {
    return engine.evalN(F.Norm(vector)).evalfNaN();
  }

  /**
   * Test if <code>expr</code> is a list of <code>size</code> numbers.
   *
   * @param expr the expression to test
   * @param size the expected number of elements
   */
  private static boolean isNumericVector(IExpr expr, int size) {
    return expr.isList() && expr.argSize() == size && ((IAST) expr).forAll(x -> x.isNumber());
  }

  /**
   * Test if <code>expr</code> is a <code>size</code> x <code>size</code> matrix of numbers.
   *
   * @param expr the expression to test
   * @param size the expected number of rows and columns
   */
  private static boolean isNumericMatrix(IExpr expr, int size) {
    return expr.isList() && expr.argSize() == size
        && ((IAST) expr).forAll(row -> isNumericVector(row, size));
  }

  /**
   * Damped Broyden's method for the root of a multivariate, vector-valued function whose jacobian
   * matrix cannot be determined symbolically - because a function is only defined for numeric
   * arguments, for instance, or because it wraps a numerical solver like <code>NDSolve</code>.
   *
   * <p>
   * The jacobian matrix is built by finite differences once and then kept up to date with rank one
   * updates from the step which was just taken, so that each further iteration costs only the
   * evaluations the residual itself needs rather than the two per entry a fresh finite difference
   * matrix would. A step is only taken when it makes the residual smaller, and is halved until it
   * does; when no step does, the estimate of the jacobian matrix has drifted too far from the
   * derivative and is seeded again from the current point.
   *
   * <p>
   * Real start values only. The finite differences and the residual comparison are real
   * arithmetic, and a complex problem stays with the symbolic jacobian matrix of
   * {@link #multivariateNewton(IAST, IAST, IAST, double, int, EvalEngine)}.
   *
   * <p>
   * See: <a href="https://en.wikipedia.org/wiki/Broyden%27s_method">Wikipedia - Broyden's
   * method</a>
   */
  private static class BroydenSolver implements Supplier<IExpr> {
    final IAST vectorValuedFunction;
    final IAST vectorOfVariables;
    final double[] initialGuess;
    final double[] initialStepWidths;
    final double tolerance;
    final int maxIterations;
    final EvalEngine engine;
    final int argSize;

    BroydenSolver(IAST vectorValuedFunction, IAST vectorOfVariables, double[] initialGuess,
        double[] initialStepWidths, double tolerance, int maxIterations, EvalEngine engine) {
      this.vectorValuedFunction = vectorValuedFunction;
      this.vectorOfVariables = vectorOfVariables;
      this.initialGuess = initialGuess;
      this.initialStepWidths = initialStepWidths;
      this.tolerance = tolerance;
      this.maxIterations = maxIterations;
      this.engine = engine;
      this.argSize = vectorOfVariables.argSize();
    }

    @Override
    public IExpr get() {
      double[] result = solve();
      if (result == null) {
        return F.NIL;
      }
      return F.mapRange(0, argSize, i -> F.Rule(vectorOfVariables.get(i + 1), F.num(result[i])));
    }

    /**
     * Iterate to the root.
     *
     * @return the point with the smallest residual which was reached, or <code>null</code> if the
     *         equations are not real valued at the start value
     */
    public double[] solve() {
      double[] x = initialGuess.clone();
      double[] f = evalVector(x);
      if (f == null) {
        // the equations are not real valued at the start value, so there is nothing to iterate on
        return null;
      }
      double[] bestPoint = x.clone();
      double bestResidual = maxAbs(f);
      double[][] jacobian = finiteDifferenceJacobian(x, f, initialStepWidths);
      boolean jacobianWasReseeded = false;
      int iteration = 0;
      for (; iteration < maxIterations && bestResidual > 0.0; iteration++) {
        double[] step = solveStep(jacobian, f);
        double[] xNext = null;
        double[] fNext = null;
        if (step != null) {
          double residual = maxAbs(f);
          // How far the full step moves the point, relative to where the point is. Halving a step
          // which is already this small cannot change the point at all, so the backtracking below
          // stops there rather than spending its whole budget on trials which all evaluate at the
          // same place - which is what an iteration does once it has converged, and which costs
          // real time when one evaluation of the equations runs a numerical solver of its own.
          double stepScale = 0.0;
          for (int i = 0; i < argSize; i++) {
            stepScale = Math.max(stepScale, Math.abs(step[i]) / Math.max(1.0, Math.abs(x[i])));
          }
          double shrink = 1.0;
          for (int backtrack = 0; backtrack < MAX_BACKTRACK_STEPS; backtrack++) {
            if (stepScale * shrink < Config.DOUBLE_EPSILON) {
              break;
            }
            double[] xTrial = new double[argSize];
            for (int i = 0; i < argSize; i++) {
              xTrial[i] = x[i] + step[i] * shrink;
            }
            // A residual which wraps a numerical solver can fail outright at a wild trial point
            // rather than only come back as a number which is not finite. Both are the same thing
            // here - the trial is rejected and the step halved - and neither gives up the search.
            double[] fTrial = evalVector(xTrial);
            // strictly smaller: a trial which is unchanged to the last bit is not progress, and
            // accepting it would keep the iteration going in place until it runs out of steps
            if (fTrial != null && maxAbs(fTrial) < residual) {
              xNext = xTrial;
              fNext = fTrial;
              step = scale(step, shrink);
              break;
            }
            shrink *= 0.5;
          }
        }
        if (xNext == null) {
          if (jacobianWasReseeded) {
            // the jacobian matrix was already built afresh at this point and still no step
            // improves on it, so this is as close as the iteration gets
            break;
          }
          jacobian = finiteDifferenceJacobian(x, f, null);
          jacobianWasReseeded = true;
          continue;
        }
        jacobianWasReseeded = false;
        broydenUpdate(jacobian, step, f, fNext);
        x = xNext;
        f = fNext;
        double residual = maxAbs(f);
        if (residual < bestResidual) {
          bestResidual = residual;
          bestPoint = x.clone();
        }
      }
      if (iteration >= maxIterations && bestResidual > tolerance) {
        // Failed to converge to the requested accuracy or precision within `1` iterations.
        Errors.printMessage(S.FindRoot, "cvmit", F.list(F.ZZ(maxIterations)), engine);
      }
      return bestPoint;
    }

    /**
     * Evaluate the vector valued function at <code>point</code>.
     *
     * @param point the values of the variables
     * @return the value of each equation, or <code>null</code> if one of them is not a finite real
     *         number there
     */
    private double[] evalVector(double[] point) {
      IAST values = F.mapRange(0, argSize, i -> F.num(point[i]));
      Map<IExpr, IExpr> map = createSubsMap(vectorOfVariables, values);
      IExpr values0;
      try {
        values0 = engine.evalQuiet(F.subsList(vectorValuedFunction, map));
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return null;
      }
      if (!values0.isList() || values0.argSize() != argSize) {
        return null;
      }
      double[] result = new double[argSize];
      for (int i = 0; i < argSize; i++) {
        result[i] = ((IAST) values0).get(i + 1).evalfNaN();
        if (!Double.isFinite(result[i])) {
          return null;
        }
      }
      return result;
    }

    /**
     * Build the jacobian matrix at <code>point</code> by forward differences.
     *
     * @param point the values of the variables
     * @param fPoint the value of the equations at <code>point</code>
     * @param stepWidths the width of the difference for each variable, where a width of
     *        <code>0.0</code> and a <code>null</code> array both ask for one determined from the
     *        point itself
     * @return the jacobian matrix
     */
    private double[][] finiteDifferenceJacobian(double[] point, double[] fPoint,
        double[] stepWidths) {
      double[][] jacobian = new double[argSize][argSize];
      for (int j = 0; j < argSize; j++) {
        double width = (stepWidths == null || stepWidths[j] <= 0.0) //
            ? 1.0e-7 * Math.max(1.0, Math.abs(point[j])) //
            : stepWidths[j];
        double[] shifted = point.clone();
        shifted[j] += width;
        double[] fShifted = evalVector(shifted);
        if (fShifted == null) {
          // step to the other side instead, in case the function is undefined beyond this one
          shifted[j] = point[j] - width;
          fShifted = evalVector(shifted);
          width = -width;
        }
        for (int i = 0; i < argSize; i++) {
          jacobian[i][j] = fShifted == null ? 0.0 : (fShifted[i] - fPoint[i]) / width;
        }
      }
      return jacobian;
    }

    /**
     * Broyden's rank one update <code>J += (df - J.dx) dx^T / (dx.dx)</code>, which is the change
     * of the jacobian matrix which makes it reproduce the step which was just taken.
     *
     * @param jacobian the jacobian matrix, updated in place
     * @param step the step which was taken
     * @param f the value of the equations before the step
     * @param fNext the value of the equations after the step
     */
    private void broydenUpdate(double[][] jacobian, double[] step, double[] f, double[] fNext) {
      double stepSquared = 0.0;
      for (int i = 0; i < argSize; i++) {
        stepSquared += step[i] * step[i];
      }
      if (stepSquared <= 1e-300) {
        return;
      }
      for (int i = 0; i < argSize; i++) {
        double jacobianTimesStep = 0.0;
        for (int k = 0; k < argSize; k++) {
          jacobianTimesStep += jacobian[i][k] * step[k];
        }
        double coefficient = (fNext[i] - f[i] - jacobianTimesStep) / stepSquared;
        for (int k = 0; k < argSize; k++) {
          jacobian[i][k] += coefficient * step[k];
        }
      }
    }

    /**
     * Solve <code>jacobian . step == -f</code> for the Newton step.
     *
     * @param jacobian the current estimate of the jacobian matrix
     * @param f the value of the equations at the current point
     * @return the step, or <code>null</code> if the matrix is singular. The solve is quiet because
     *         a singular estimate is answered by building the matrix again rather than by a
     *         message.
     */
    private double[] solveStep(double[][] jacobian, double[] f) {
      IAST matrix = F.matrix((i, j) -> F.num(jacobian[i][j]), argSize, argSize);
      IAST vector = F.vector(i -> F.num(-f[i]), argSize);
      IExpr solution = engine.evalQuiet(F.LinearSolve(matrix, vector));
      if (!isNumericVector(solution, argSize)) {
        return null;
      }
      double[] step = new double[argSize];
      for (int i = 0; i < argSize; i++) {
        step[i] = ((IAST) solution).get(i + 1).evalfNaN();
        if (!Double.isFinite(step[i])) {
          return null;
        }
      }
      return step;
    }

    /** Multiply every element of <code>vector</code> by <code>factor</code>. */
    private static double[] scale(double[] vector, double factor) {
      double[] result = new double[vector.length];
      for (int i = 0; i < vector.length; i++) {
        result[i] = vector[i] * factor;
      }
      return result;
    }

    /** The largest absolute value in <code>vector</code>, which is the residual of a point. */
    private static double maxAbs(double[] vector) {
      double result = 0.0;
      for (double value : vector) {
        result = Math.max(result, Math.abs(value));
      }
      return result;
    }
  }

  /**
   * Convert the accuracyGoal in to a <code>double</code> tolerance value.
   * 
   * @param accuracyGoal
   * @return <code>1e- accuracyGoal </code>
   */
  private static double accuracy(int accuracyGoal) {
    if (accuracyGoal > 0) {
      switch (accuracyGoal) {
        case 1:
          return 1e-1;
        case 2:
          return 1e-2;
        case 3:
          return 1e-4;
        case 4:
          return 1e-4;
        case 5:
          return 1e-5;
        case 6:
          return 1e-6;
        case 7:
          return 1e-7;
        case 8:
          return 1e-8;
        case 9:
          return 1e-9;
        case 10:
          return 1e-10;
        case 11:
          return 1e-11;
        case 12:
          return 1e-12;
        case 13:
          return 1e-13;
        case 14:
          return 1e-14;
        case 15:
          return 1e-15;
        case 16:
          return 1e-16;
        default:
          break;
      }
    }
    // default
    return 1e-6;
  }

  /**
   * Create the substitution map.
   * 
   * @param variables
   * @param xCurr
   * @return
   */
  private static Map<IExpr, IExpr> createSubsMap(IAST variables, IAST xCurr) {
    ImmutableMap.Builder<IExpr, IExpr> builder = ImmutableMap.builder();
    for (int i = 1; i < variables.size(); i++) {
      builder.put(variables.get(i), xCurr.get(i));
    }
    return builder.build();
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // newSymbol.setAttributes(ISymbol.HOLDALL);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {//
            S.MaxIterations, S.Method, S.AccuracyGoal}, //
        new IExpr[] {//
            F.C100, S.Automatic, S.Automatic});
  }
}
