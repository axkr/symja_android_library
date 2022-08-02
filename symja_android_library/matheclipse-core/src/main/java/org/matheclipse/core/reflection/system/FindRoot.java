package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
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
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

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
 * <p>
 * See
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Root-finding_algorithm">Wikipedia - Root-finding
 * algorithm</a></li>
 * <li><a href="https://en.wikipedia.org/wiki/Newton%27s_method#k_variables,_k_functions">Wikipedia
 * - Newton's method - k_variables, _k_functions</a></li>
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
public class FindRoot extends AbstractFunctionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  private static class UnivariateSolverSupplier implements Supplier<IExpr> {
    final IExpr originalFunction;
    final IAST variableList;
    /**
     * Starting point or minimum of a user defined interval; <code>min</code> is not allowed to be
     * <code>null</code>
     */
    final ISignedNumber min;
    /**
     * Maximum of a user defined interval; the maximum can be <code>null</code>, if no interval was
     * defined
     */
    final ISignedNumber maxMaybeNull;
    final int maxIterations;
    final String method;
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
     * @param engine
     */
    public UnivariateSolverSupplier(IExpr function, IAST variableList, ISignedNumber min,
        ISignedNumber max, int maxIterations, String method, EvalEngine engine) {
      this.originalFunction = function;
      this.variableList = variableList;
      this.min = min;
      this.maxMaybeNull = max;
      this.maxIterations = maxIterations;
      this.method = method;
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
        } else if (maxMaybeNull == null || method.equalsIgnoreCase("Newton")) {
          try {
            NewtonRaphsonSolver nrs = new NewtonRaphsonSolver();
            if (maxMaybeNull == null) {
              return F.num(nrs.solve(maxIterations, f, min.doubleValue()));
            }
            return F
                .num(nrs.solve(maxIterations, f, min.doubleValue(), maxMaybeNull.doubleValue()));
          } catch (MathRuntimeException mex) {
            // switch to BracketingNthOrderBrentSolver
            solver = new BracketingNthOrderBrentSolver();
          }
        } else {
          // default: BracketingNthOrderBrentSolver
          try {
            solver = new BracketingNthOrderBrentSolver();
            return F
                .num(solver.solve(maxIterations, f, min.doubleValue(), maxMaybeNull.doubleValue()));
          } catch (MathRuntimeException mex) {
            // org.hipparchus.exception.MathIllegalArgumentException: interval does not bracket a
            // root

            if (mex instanceof org.hipparchus.exception.MathIllegalArgumentException) {

              try {
                NewtonRaphsonSolver nrs = new NewtonRaphsonSolver();
                return F.num(
                    nrs.solve(maxIterations, f, min.doubleValue(), maxMaybeNull.doubleValue()));
              } catch (MathRuntimeException mre) {
              }

            }

            // switch to BisectionSolver
            solver = new BisectionSolver();
          }
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
      if (optionMethod.isSymbol() || optionMethod.isString()) {
        method = optionMethod.toString();
      } else {
        if (ast.arg3().isSymbol()) {
          method = ast.arg3().toString();
        }
      }
    }

    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();
    if (!arg2.isList()) {
      arg2 = engine.evaluate(arg2);
    }
    int l1 = arg1.isVector();
    int l2 = arg2.argSize();
    if (l1 > 0 && l1 == l2 && arg1.isList() && arg2.isList()) {
      return multivariateFindRoot((IAST) arg1, (IAST) arg2, Config.SPECIAL_FUNCTIONS_TOLERANCE, 200,
          engine);
    } else if (arg2.isList()) {
      IAST list = (IAST) arg2;
      if (list.size() >= 3 && list.arg1().isSymbol()) {
        ISignedNumber min = list.arg2().evalReal();
        if (min != null) {
          ISignedNumber max = null;
          if (list.size() > 3) {
            max = list.arg3().evalReal();
          }
          try {
            UnivariateSolverSupplier optimizeSupplier = new UnivariateSolverSupplier(ast.arg1(),
                list, min, max, maxIterations, method, engine);
            IExpr result = engine.evalBlock(optimizeSupplier, list);
            return F.list(F.Rule(list.arg1(), result));
          } catch (MathIllegalStateException miae) {
            // `1`.
            return IOFunctions.printMessage(ast.topHead(), "error",
                F.list(F.$str(miae.getMessage())), engine);
          } catch (MathRuntimeException mre) {
            IOFunctions.printMessage(ast.topHead(), "error", F.list(F.$str(mre.getMessage())),
                engine);
            return F.CEmptyList;
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Call Newton's method for finding the root of a differentiable, multivariate, vector-valued
   * function.
   * 
   * @param listOfEquations a list of equations
   * @param matrixOfVarValuePairs a matrix of variables and their initial values
   * @param tolerance the tolerance where the the iteration should stop
   * @param iterationLimit maximum iterations
   * @param engine
   * @return
   */
  private static IExpr multivariateFindRoot(IAST listOfEquations, IAST matrixOfVarValuePairs,
      double tolerance, int iterationLimit, EvalEngine engine) {
    // convert parameters from FindRoot to be suitable for Newtons method
    IASTAppendable vectorValuedFunction = F.ListAlloc(matrixOfVarValuePairs.argSize());
    IASTAppendable vectorOfVariables = F.ListAlloc(matrixOfVarValuePairs.argSize());
    IASTAppendable initialGuess = F.ListAlloc(matrixOfVarValuePairs.argSize());
    for (int i = 1; i < matrixOfVarValuePairs.size(); i++) {
      IExpr element = matrixOfVarValuePairs.get(i);
      if (element.isList2()) {
        vectorOfVariables.append(element.first());
        initialGuess.append(engine.evalDouble(element.second()));
      } else {
        return F.NIL;
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

    return multivariateNewton(vectorValuedFunction, vectorOfVariables, initialGuess, tolerance,
        iterationLimit, engine);
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
   * @param iterationLimit maximum iterations
   * @param engine
   * @return
   */
  private static IExpr multivariateNewton(IAST vectorValuedFunction, IAST vectorOfVariables,
      IAST initialGuessVector, double tolerance,
      int iterationLimit, EvalEngine engine) {

    IExpr jacobianMatrix = S.Grad.ofNIL(engine, vectorValuedFunction, vectorOfVariables);
    if (jacobianMatrix.isMatrix(false) != null) {
      final int argSize = vectorOfVariables.argSize();
      IAST xNext = F.constantArray(F.CD0, argSize);
      IAST xCurr = initialGuessVector.copy();
      for (int k = 0; k < iterationLimit; k++) {
        Map<IExpr, IExpr> map = createSubsMap(vectorOfVariables, xCurr);
        IExpr fValue = engine.evalN(F.Negate(F.subsList(vectorValuedFunction, map)));
        IExpr jValue = engine.evalN(F.subsList(jacobianMatrix, map));
        if (fValue.argSize() == argSize && jValue.argSize() == argSize) {
          IExpr y = S.LinearSolve.ofNIL(engine, jValue, fValue);
          if (y.argSize() != argSize) {
            return F.NIL;
          }
          IExpr temp = engine.evaluate(F.Plus(xCurr, y));
          if (temp.argSize() != argSize) {
            return F.NIL;
          }
          xNext = (IAST) temp;
          double norm = engine.evalDouble(F.Norm(y));
          if (norm < tolerance) {
            break;
          }
          xCurr = xNext;
        } else {
          return F.NIL;
        }
      }
      // convert result vector to list of rules
      return vectorOfVariables.mapThread(xNext, (x, y) -> F.Rule(x, y));
    }
    return F.NIL;
  }

  /**
   * Create the substitution map.
   * 
   * @param variables
   * @param xCurr
   * @return
   */
  private static Map<IExpr, IExpr> createSubsMap(IAST variables, IAST xCurr) {
    Map<IExpr, IExpr> map = new HashMap<IExpr, IExpr>();
    for (int i = 1; i < variables.size(); i++) {
      map.put(variables.get(i), xCurr.get(i));
    }
    return map;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
