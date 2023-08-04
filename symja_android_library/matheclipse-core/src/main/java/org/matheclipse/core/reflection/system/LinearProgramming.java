package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.optim.PointValuePair;
import org.hipparchus.optim.linear.LinearConstraint;
import org.hipparchus.optim.linear.LinearConstraintSet;
import org.hipparchus.optim.linear.LinearObjectiveFunction;
import org.hipparchus.optim.linear.NonNegativeConstraint;
import org.hipparchus.optim.linear.PivotSelectionRule;
import org.hipparchus.optim.linear.Relationship;
import org.hipparchus.optim.linear.SimplexSolver;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;

/**
 *
 *
 * <pre>
 * LinearProgramming(coefficientsOfLinearObjectiveFunction, constraintList, constraintRelationList)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * the <code>LinearProgramming</code> function provides an implementation of
 * <a href="http://en.wikipedia.org/wiki/Simplex_algorithm">George Dantzig's simplex algorithm</a>
 * for solving linear optimization problems with linear equality and inequality constraints and
 * implicit non-negative variables.
 *
 * </blockquote>
 *
 * <p>
 * See:<br>
 *
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Linear_programming">Wikipedia - Linear programming</a>
 * </ul>
 *
 * <p>
 * See also: <a href="NMaximize.md">NMaximize</a>, <a href="NMinimize.md">NMinimize</a>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; LinearProgramming({-2, 1, -5}, {{1, 2, 0},{3, 2, 0},{0,1,0},{0,0,1}}, {{6,-1},{12,-1},{0,1},{1,0}})
 * {4.0,0.0,1.0}
 * </pre>
 *
 * <p>
 * solves the linear problem:
 *
 * <pre>
 * Minimize -2x + y - 5
 * </pre>
 *
 * <p>
 * with the constraints:
 *
 * <pre>
 *   x  + 2y &lt;=  6
 *   3x + 2y &lt;= 12
 *         x &gt;= 0
 *         y &gt;= 0
 * </pre>
 */
public class LinearProgramming extends AbstractFunctionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public LinearProgramming() {
    super();
  }

  /**
   * The LinearProgramming provides an implementation of
   * <a href="http://en.wikipedia.org/wiki/Simplex_algorithm">George Dantzig's simplex algorithm</a>
   * for solving linear optimization problems with linear equality and inequality constraints.
   */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // switch to numeric calculation
    return numericEval(ast, engine);
  }

  @Override
  public IExpr numericEval(final IAST ast, EvalEngine engine) {
    try {
      if (ast.arg1().isList() && ast.arg2().isList() && ast.arg3().isList()) {
        double[] arg1D = ast.arg1().toDoubleVector();
        if (arg1D != null) {
          LinearObjectiveFunction f = new LinearObjectiveFunction(arg1D, 0);
          Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

          IAST arg2 = (IAST) ast.arg2();
          IAST arg3 = (IAST) ast.arg3();
          if (arg2.size() != arg3.size()) {
            return F.NIL;
          }
          double[] arg2D;
          double[] arg3D;
          for (int i = 1; i < arg2.size(); i++) {
            if (arg2.get(i).isList()) {
              arg2D = arg2.get(i).toDoubleVector();
              if (arg2D == null) {
                return F.NIL;
              }
              if (arg3.get(i).isList()) {
                arg3D = arg3.get(i).toDoubleVector();
                if (arg3D == null) {
                  return F.NIL;
                }
                if (arg3D.length >= 2) {
                  double val = arg3D[1];
                  if (val == 0.0) {
                    constraints.add(new LinearConstraint(arg2D, Relationship.EQ, arg3D[0]));
                  } else if (val < 0.0) {
                    constraints.add(new LinearConstraint(arg2D, Relationship.LEQ, arg3D[0]));
                  } else if (val > 0.0) {
                    constraints.add(new LinearConstraint(arg2D, Relationship.GEQ, arg3D[0]));
                  }
                } else if (arg3D.length == 1) {
                  constraints.add(new LinearConstraint(arg2D, Relationship.GEQ, arg3D[0]));
                }
              } else {
                IReal sn = arg3.get(i).evalReal();
                if (sn != null) {
                  constraints.add(new LinearConstraint(arg2D, Relationship.GEQ, sn.doubleValue()));
                } else {
                  LOGGER.log(engine.getLogLevel(), "Numeric vector or number expected!");
                  return F.NIL;
                }
              }
            } else {
              LOGGER.log(engine.getLogLevel(), "Numeric vector expected!");
              return F.NIL;
            }
          }
          SimplexSolver solver = new SimplexSolver();
          // PointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true);
          PointValuePair solution = solver.optimize(f, new LinearConstraintSet(constraints),
              GoalType.MINIMIZE, new NonNegativeConstraint(true), PivotSelectionRule.BLAND);
          double[] values = solution.getPointRef();
          return F.List(values);
        }
      }
    } catch (MathIllegalArgumentException miae) {
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(miae.getMessage())),
          engine);
    } catch (MathRuntimeException mre) {
      LOGGER.log(engine.getLogLevel(), ast.topHead(), mre);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }
}
