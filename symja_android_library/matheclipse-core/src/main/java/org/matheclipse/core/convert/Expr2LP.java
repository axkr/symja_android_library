package org.matheclipse.core.convert;

import java.util.List;
import org.hipparchus.optim.linear.LinearConstraint;
import org.hipparchus.optim.linear.LinearObjectiveFunction;
import org.hipparchus.optim.linear.Relationship;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Convert a given Symja expression into linear programming objects from the <code>
 * org.hipparchus.optim.linear</code> package.
 */
public class Expr2LP {

  private final IExpr fExpr;
  /** The variables used in the expression. */
  private final List<? extends IExpr> fVariables;

  private final VariablesSet fVariablesSet;

  public Expr2LP(IExpr expr) {
    this(expr, new VariablesSet(expr));
  }

  public Expr2LP(IExpr expr, VariablesSet variableSet) {
    fExpr = expr;
    fVariablesSet = variableSet;
    fVariables = fVariablesSet.getArrayList();
  }

  public LinearConstraint expr2Constraint() {
    double[] coefficients = new double[fVariables.size()];
    if (fExpr.isAST()) {
      IAST ast = (IAST) fExpr;
      if (ast.isEqual()) {
        IExpr expr = F.eval(F.Subtract(ast.arg1(), ast.arg2()));
        IReal num = expr2ObjectiveFunction(expr, coefficients);
        if (num == null) {
          return new LinearConstraint(coefficients, Relationship.EQ, 0);
        }
        return new LinearConstraint(coefficients, Relationship.EQ, -1 * num.doubleValue());
      }
      if (ast.isAST(S.GreaterEqual, 3)) {
        IExpr expr = F.eval(F.Subtract(ast.arg1(), ast.arg2()));
        IReal num = expr2ObjectiveFunction(expr, coefficients);
        if (num == null) {
          return new LinearConstraint(coefficients, Relationship.GEQ, 0);
        }
        return new LinearConstraint(coefficients, Relationship.GEQ, -1 * num.doubleValue());
      }
      if (ast.isAST(S.LessEqual, 3)) {
        IExpr expr = F.eval(F.Subtract(ast.arg1(), ast.arg2()));
        IReal num = expr2ObjectiveFunction(expr, coefficients);
        if (num == null) {
          return new LinearConstraint(coefficients, Relationship.LEQ, 0);
        }
        return new LinearConstraint(coefficients, Relationship.LEQ, -1 * num.doubleValue());
      }
    }
    throw new ArgumentTypeException(
        "conversion from expression to linear programming expression failed for "
            + fExpr.toString());
  }

  public LinearObjectiveFunction expr2ObjectiveFunction() {
    double[] coefficients = new double[fVariables.size()];
    IReal num = expr2ObjectiveFunction(fExpr, coefficients);
    if (num == null) {
      return new LinearObjectiveFunction(coefficients, 0);
    }
    return new LinearObjectiveFunction(coefficients, num.doubleValue());
  }

  private IReal expr2ObjectiveFunction(final IExpr expr, double[] coefficients)
      throws ArithmeticException, ClassCastException {
    if (expr instanceof IAST) {
      final IAST ast = (IAST) expr;
      if (ast.isPlus()) {
        double constantTerm = 0.0;
        for (int i = 1; i < ast.size(); i++) {
          IExpr temp = ast.get(i);
          IReal num = expr2ObjectiveFunction(temp, coefficients);
          if (num != null) {
            constantTerm += num.doubleValue();
          }
        }
        return F.num(constantTerm);
      } else if (ast.isTimes()) {
        ISymbol variable = null;
        double value = 1.0;
        for (int i = 1; i < ast.size(); i++) {
          IExpr temp = ast.get(i);
          if (temp.isVariable()) {
            if (variable != null) {
              throw new ArgumentTypeException(
                  "conversion from expression to linear programming expression failed for "
                      + temp.toString());
            }
            variable = (ISymbol) temp;
            continue;
          }
          IReal num = temp.evalReal();
          if (num != null) {
            value *= num.doubleValue();
            continue;
          }
          throw new ArgumentTypeException(
              "conversion from expression to linear programming expression failed for "
                  + temp.toString());
        }
        if (variable != null) {
          for (int i = 0; i < coefficients.length; i++) {
            if (variable.equals(fVariables.get(i))) {
              coefficients[i] += value;
              return null;
            }
          }
          throw new ArgumentTypeException(
              "conversion from expression to linear programming expression failed for "
                  + ast.toString());
        }
        return F.num(value);
      }
    } else if (expr.isVariable()) {
      ISymbol variable = (ISymbol) expr;
      for (int i = 0; i < coefficients.length; i++) {
        if (variable.equals(fVariables.get(i))) {
          coefficients[i] += 1.0d;
          return null;
        }
      }
      throw new ArgumentTypeException(
          "conversion from expression to linear programming expression failed for "
              + expr.toString());
    }

    IReal num = expr.evalReal();
    if (num != null) {
      return num;
    }
    throw new ArgumentTypeException(
        "conversion from expression to linear programming expression failed for "
            + expr.toString());
  }
}
