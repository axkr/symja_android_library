package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
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
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class LinearOptimization extends LinearProgramming {
  private static final Logger LOGGER = LogManager.getLogger();

  public LinearOptimization() {
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
    IAST variables = F.NIL;
    try {
      if (ast.arg2().isList() && ast.arg3().isList()) {
        if (ast.arg1().isList()) {
          // fall back to LinearProgramming
          return super.numericEval(ast, engine);
        }
        IExpr arg1 = F.evalExpand(ast.arg1());
        if (!arg1.isPlus()) {
          return Errors.printMessage(S.LinearOptimization, "error",
              F.List(F.stringx("Plus expression expected at position 1.")), engine);
        }
        IAST plusAST = (IAST) arg1;

        variables = (IAST) ast.arg3();
        Map<IExpr, Integer> variablesMap = new HashMap<IExpr, Integer>();
        if (createVariablesMap(variables, variablesMap, engine).isNIL()) {
          return F.NIL;
        }

        IExpr objectiveFunction = createObjectiveFunction(plusAST, variables, variablesMap, engine);
        if (objectiveFunction.isNIL()) {
          return F.NIL;
        }

        IAST constraintsList = (IAST) ast.arg2();
        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        for (int i = 1; i < constraintsList.size(); i++) {
          IExpr temp = constraintsList.get(i);
          int headID = temp.headID();
          if (headID < 0) {
            return messageConstraintIsNotConvex(temp, engine);
          }
          if (temp.isAST2()) {
            IAST relation = (IAST) temp;
            IExpr lhs = relation.arg1();
            IExpr lhsList = createObjectiveFunction(lhs, variables, variablesMap, engine);
            if (lhsList.isNIL()) {
              return F.NIL;
            }
            double rhs = relation.arg2().evalf();
            switch (headID) {
              case ID.Equal:
                constraints
                    .add(new LinearConstraint(lhsList.toDoubleVector(), Relationship.EQ, rhs));
                continue;
              case ID.Greater:
              case ID.GreaterEqual:
                constraints
                    .add(new LinearConstraint(lhsList.toDoubleVector(), Relationship.GEQ, rhs));
                continue;
              case ID.Less:
              case ID.LessEqual:
                constraints
                    .add(new LinearConstraint(lhsList.toDoubleVector(), Relationship.LEQ, rhs));
                continue;
              default:
                break;
            }

          }
          return messageConstraintIsNotConvex(temp, engine);
        }

        LinearObjectiveFunction f =
            new LinearObjectiveFunction(objectiveFunction.toDoubleVector(), 0);

        SimplexSolver solver = new SimplexSolver();
        // PointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true);
        PointValuePair solution = solver.optimize(f, new LinearConstraintSet(constraints),
            GoalType.MINIMIZE, new NonNegativeConstraint(true), PivotSelectionRule.BLAND);
        double[] values = solution.getPointRef();
        if (values != null) {
          IASTAppendable result = F.ListAlloc(values.length);
          for (int i = 0; i < values.length; i++) {
            IExpr variable = variables.get(i + 1);
            result.append(F.Rule(variable, F.num(values[i])));
          }
          return result;
        }
      }
    } catch (MathIllegalStateException mise) {
      if (mise.getMessage().equals("no feasible solution")) {
        // There are no points which satisfy the constraints
        Errors.printMessage(S.LinearOptimization, "nsolc", F.CEmptyList, engine);
        if (variables.isPresent()) {
          IASTAppendable result = F.ListAlloc(variables.argSize());
          for (int i = 1; i < variables.size(); i++) {
            IExpr variable = variables.get(i);
            result.append(F.Rule(variable, S.Indeterminate));
          }
          return result;
        }
      }
    } catch (MathIllegalArgumentException miae) {
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(miae.getMessage())), engine);
    } catch (MathRuntimeException mre) {
      LOGGER.log(engine.getLogLevel(), ast.topHead(), mre);
    }
    return F.NIL;
  }

  private static IExpr messageConstraintIsNotConvex(IExpr temp, EvalEngine engine) {
    // The constraint `1` is not convex.
    return Errors.printMessage(S.LinearOptimization, "ctnc", F.List(temp), engine);
  }

  private static IExpr createObjectiveFunction(IExpr expr, IAST variables,
      Map<IExpr, Integer> variablesMap, EvalEngine engine) {
    Integer index = variablesMap.get(expr);
    if (index != null) {
      IASTAppendable functionList = F.ListAlloc(variables.argSize());
      for (int i = 1; i < variables.size(); i++) {
        functionList.append(F.C0);
      }
      functionList.set(index, F.C1);
      return functionList;
    }
    if (expr.isPlus()) {
      IAST plusAST = (IAST) expr;
      IASTAppendable functionList = F.ListAlloc(variables.argSize());
      for (int i = 1; i < variables.size(); i++) {
        functionList.append(F.C0);
      }
      for (int i = 1; i < plusAST.size(); i++) {
        IExpr temp = plusAST.get(i);
        if (temp.isTimes()) {
          IAST timesAST = (IAST) temp;
          if (timesAST.arg1().isReal()) {
            index = variablesMap.get(timesAST.arg2());
            if (index == null) {
              // The objective function `1` is not a numeric valued linear function of the
              // variables
              // `2`.
              return Errors.printMessage(S.LinearOptimization, "linobj", F.List(plusAST, variables),
                  engine);
            }
            functionList.set(index, timesAST.arg1());
            continue;
          }

        } else {
          index = variablesMap.get(temp);
          if (index == null) {
            // The objective function `1` is not a numeric valued linear function of the variables
            // `2`.
            return Errors.printMessage(S.LinearOptimization, "linobj", F.List(plusAST, variables),
                engine);
          }
          functionList.set(index, F.C1);
          continue;
        }
        // The objective function `1` is not a numeric valued linear function of the variables
        // `2`.
        return Errors.printMessage(S.LinearOptimization, "linobj", F.List(plusAST, variables),
            engine);
      }
      return functionList;
    }
    // The objective function `1` is not a numeric valued linear function of the variables
    // `2`.
    return Errors.printMessage(S.LinearOptimization, "linobj", F.List(expr, variables), engine);
  }

  private static IExpr createVariablesMap(IAST variables, Map<IExpr, Integer> variablesMap,
      EvalEngine engine) {
    int counter = 1;
    for (int i = 1; i < variables.size(); i++) {
      IExpr variable = variables.get(i);
      if (!variable.isVariable()) {
        return Errors.printMessage(S.LinearOptimization, "ivar", F.List(variable), engine);
      }
      Integer index = variablesMap.get(variable);
      if (index != null) {
        // The variable `1` has been specified more than once.
        return Errors.printMessage(S.LinearOptimization, "dpvar", F.List(variable), engine);
      }
      variablesMap.put(variable, counter++);
    }
    return S.True;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }
}
