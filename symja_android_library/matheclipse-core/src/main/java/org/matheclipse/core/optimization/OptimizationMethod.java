package org.matheclipse.core.optimization;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The option value <code>Method -&gt; "ORTools"</code>, which asks a function to solve its
 * problem with the optional <code>matheclipse-or-tools</code> module.
 */
public final class OptimizationMethod {

  /** The name of the method. */
  public static final String ORTOOLS = "ORTools";

  private OptimizationMethod() {}

  /**
   * @param method the value of the <code>Method</code> option
   * @return <code>true</code> if it is the string <code>"ORTools"</code> (in any letter case)
   */
  public static boolean isORTools(IExpr method) {
    return method != null && method.isString() && method.toString().equalsIgnoreCase(ORTOOLS);
  }

  /**
   * The value of a <code>Method -&gt; value</code> rule among the arguments
   * <code>ast.get(from) ... ast.last()</code>.
   *
   * @return the value or {@link F#NIL} if there is no such rule
   */
  public static IExpr methodOption(IAST ast, int from) {
    for (int i = ast.argSize(); i >= from; i--) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && arg.first() == S.Method) {
        return arg.second();
      }
    }
    return F.NIL;
  }

  /**
   * The value of a trailing <code>Method -&gt; value</code> rule of a function which takes at least
   * <code>minArgs</code> positional arguments.
   *
   * @return the value, or {@link F#NIL} if the last argument isn't such a rule or
   *         <code>ast</code> has no more than <code>minArgs</code> arguments
   */
  public static IExpr trailingMethod(IAST ast, int minArgs) {
    if (ast.argSize() > minArgs) {
      IExpr last = ast.last();
      if (last.isRuleAST() && last.first() == S.Method) {
        return last.second();
      }
    }
    return F.NIL;
  }

  /**
   * The linear program <code>objective . x + offset</code> subject to the hipparchus
   * <code>constraints</code>, in the form {@link ILinearOptimizer} solves.
   *
   * @param nonNegative whether every variable is <code>&gt;= 0</code>; else the variables are free
   *        and bounds are among the constraints
   * @param integer the variables which must be integers, <code>null</code> for none
   * @param timeLimit the time limit in seconds
   */
  public static ILinearOptimizer.Problem linearProblem(double[] objective, double offset,
      boolean maximize, java.util.Collection<org.hipparchus.optim.linear.LinearConstraint> constraints,
      boolean nonNegative, boolean[] integer, double timeLimit) {
    final int n = objective.length;
    final int m = constraints.size();
    double[][] rows = new double[m][];
    double[] rowLower = new double[m];
    double[] rowUpper = new double[m];
    int i = 0;
    for (org.hipparchus.optim.linear.LinearConstraint c : constraints) {
      rows[i] = c.getCoefficients().toArray();
      double value = c.getValue();
      switch (c.getRelationship()) {
        case EQ:
          rowLower[i] = value;
          rowUpper[i] = value;
          break;
        case LEQ:
          rowLower[i] = Double.NEGATIVE_INFINITY;
          rowUpper[i] = value;
          break;
        default:
          rowLower[i] = value;
          rowUpper[i] = Double.POSITIVE_INFINITY;
          break;
      }
      i++;
    }
    double[] varLower = new double[n];
    double[] varUpper = new double[n];
    java.util.Arrays.fill(varLower, nonNegative ? 0.0 : Double.NEGATIVE_INFINITY);
    java.util.Arrays.fill(varUpper, Double.POSITIVE_INFINITY);
    return new ILinearOptimizer.Problem(objective, offset, maximize, rows, rowLower, rowUpper,
        varLower, varUpper, integer, timeLimit);
  }

  /**
   * The time limit in seconds of a solve requested by a Symja function. An enclosing
   * <code>TimeConstrained</code> still interrupts the solver thread earlier.
   */
  public static final double DEFAULT_TIME_LIMIT = 60.0;

  /**
   * Whether <code>method</code> asks for OR-Tools and the solver is installed. If the method asks
   * for OR-Tools but the module is missing, a message is printed and <code>false</code> is
   * returned so that the caller uses its own algorithm.
   *
   * @param head the function which prints the message
   * @param method the value of the <code>Method</code> option
   * @param available whether the needed solver is installed
   * @param engine the evaluation engine
   */
  public static boolean useORTools(ISymbol head, IExpr method, boolean available,
      EvalEngine engine) {
    if (!isORTools(method)) {
      return false;
    }
    if (available) {
      return true;
    }
    // `1`.
    Errors.printMessage(head, "error",
        F.list(F.stringx(
            "Method -> \"ORTools\" needs the matheclipse-or-tools module; the default method is used")),
        engine);
    return false;
  }
}
