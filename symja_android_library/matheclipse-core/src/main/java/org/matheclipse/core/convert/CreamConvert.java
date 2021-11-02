package org.matheclipse.core.convert;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import jp.ac.kobe_u.cs.cream.DefaultSolver;
import jp.ac.kobe_u.cs.cream.IntVariable;
import jp.ac.kobe_u.cs.cream.Network;
import jp.ac.kobe_u.cs.cream.Solution;
import jp.ac.kobe_u.cs.cream.SolutionHandler;
import jp.ac.kobe_u.cs.cream.Solver;

/**
 * Convert <code>IExpr</code> expressions from and to <a
 * href="http://bach.istc.kobe-u.ac.jp/cream/">Cream: Class Library for Constraint Programming in
 * Java</a>
 */
public class CreamConvert {

  private final class CreamSolutionHandler implements SolutionHandler {
    private final IAST userDefinedVariables;
    private final IASTAppendable result;
    private final int maximumNumberOfResults;
    private final IAST equationVariables;
    private final EvalEngine engine;

    private CreamSolutionHandler(
        IASTAppendable result,
        IAST equationVariables,
        IAST userDefinedVariables,
        int maximumNumberOfResults,
        EvalEngine engine) {
      this.userDefinedVariables = userDefinedVariables;
      this.result = result;
      this.maximumNumberOfResults = maximumNumberOfResults;
      this.equationVariables = equationVariables;
      this.engine = engine;
    }

    @Override
    public boolean solved(Solver solver, Solution solution) {
      if (solution != null) {
        IExpr listOfZZVariables = F.NIL;
        IExpr complement = S.Complement.of(engine, userDefinedVariables, equationVariables);
        if (complement.size() > 1 && complement.isList()) {
          listOfZZVariables =
              S.Apply.of(
                  engine, S.And, ((IAST) complement).mapThread(F.Element(F.Slot1, S.Integers), 1));
        }

        Set<Entry<ISymbol, IntVariable>> set = map.entrySet();
        IASTAppendable temp = F.ListAlloc(set.size());
        for (Entry<ISymbol, IntVariable> entry : set) {
          ISymbol variable = entry.getKey();
          if (listOfZZVariables.isPresent()) {
            temp.append(
                F.Rule(
                    variable,
                    F.ConditionalExpression(
                        F.ZZ(solution.getIntValue(entry.getValue())), listOfZZVariables)));
          } else {
            temp.append(F.Rule(variable, F.ZZ(solution.getIntValue(entry.getValue()))));
          }
        }
        result.append(temp);
        if (result.size() - 1 >= maximumNumberOfResults) {
          return false;
        }
      }
      return true;
    }
  }

  TreeMap<ISymbol, IntVariable> map = new TreeMap<ISymbol, IntVariable>();

  public CreamConvert() {}

  public Network expr2Cream(final IAST list, final IAST variables) throws ClassCastException {
    // Create a constraint network
    Network net = new Network();
    for (int i = 1; i < variables.size(); i++) {
      if (variables.get(i) instanceof ISymbol) {
        map.put((ISymbol) variables.get(i), new IntVariable(net));
      }
    }
    IAST temp;
    IntVariable lhs;
    IntVariable rhs;
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i) instanceof IAST) {
        temp = (IAST) list.get(i);
        if (temp.isAST2()) {
          lhs = integerVariable(net, temp.arg1());
          rhs = integerVariable(net, temp.arg2());
          if (temp.isEqual()) {
            lhs.equals(rhs);
          } else if (temp.isAST(S.Unequal, 3)) {
            lhs.notEquals(rhs);
          } else if (temp.isAST(S.Greater, 3)) {
            lhs.gt(rhs);
          } else if (temp.isAST(S.GreaterEqual, 3)) {
            lhs.ge(rhs);
          } else if (temp.isAST(S.LessEqual, 3)) {
            lhs.le(rhs);
          } else if (temp.isAST(S.Less, 3)) {
            lhs.lt(rhs);
          } else {
            return null;
          }
        }
      }
    }
    return net;
  }

  private IntVariable integerVariable(Network net, IExpr expr) throws ArithmeticException {
    if (expr instanceof ISymbol) {
      IntVariable temp = map.get(expr);
      if (temp == null) {
        temp = new IntVariable(net);
        map.put((ISymbol) expr, temp);
      }
      return temp;
    }
    if (expr instanceof IInteger) {
      int value = ((IInteger) expr).toInt(); // throws ArithmeticException
      return new IntVariable(net, value);
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (ast.isPlus()) {
        IntVariable result = integerVariable(net, ast.arg1());
        for (int i = 2; i < ast.size(); i++) {
          result = result.add(integerVariable(net, ast.get(i)));
        }
        return result;
      } else if (ast.isTimes()) {
        IntVariable result = integerVariable(net, ast.arg1());
        for (int i = 2; i < ast.size(); i++) {
          result = result.multiply(integerVariable(net, ast.get(i)));
        }
        return result;
      } else if (ast.isPower()) {
        IExpr exponent = ast.exponent();
        if (exponent.isInteger()) {
          int value = ((IInteger) exponent).toInt();
          if (value > 0) {
            IExpr base = ast.base();
            IntVariable result = integerVariable(net, base);
            for (int i = 1; i < value; i++) {
              result = result.multiply(integerVariable(net, base));
            }
            return result;
          }
        }
      } else if (ast.isSameHeadSizeGE(S.Max, 3)) {
        IntVariable result = integerVariable(net, ast.arg1());
        for (int i = 2; i < ast.size(); i++) {
          result = result.max(integerVariable(net, ast.get(i)));
        }
        return result;
      } else if (ast.isSameHeadSizeGE(S.Min, 3)) {
        IntVariable result = integerVariable(net, ast.arg1());
        for (int i = 2; i < ast.size(); i++) {
          result = result.min(integerVariable(net, ast.get(i)));
        }
        return result;
      } else if (ast.isAbs()) {
        return integerVariable(net, ast.arg1()).abs();
      } else if (ast.isAST(S.Sign, 2)) {
        return integerVariable(net, ast.arg1()).sign();
      }
    }
    throw new ArgumentTypeException(
        expr.toString() + " is no int variable found for Solve(..., Integers)");
  }

  public TreeMap<ISymbol, IntVariable> variableMap() {
    return map;
  }

  /**
   * Create a cream integer solver.
   *
   * @param list
   * @param equationVariables all variables which are defined in the equations
   * @param userDefinedVariables all variables which are defined by the user. May contain additional
   *     variables which aren't available in <code>equationVariables</code>
   * @param engine
   * @return a list of rules with the integer solutions
   */
  public IAST integerSolve(
      final IAST list,
      final IAST equationVariables,
      final IAST userDefinedVariables,
      int maximumNumberOfResults,
      final EvalEngine engine) {
    IASTAppendable result = F.ListAlloc();

    Solver solver = new DefaultSolver(expr2Cream(list, equationVariables), Solver.DEFAULT);

    // call with timeout
    solver.findAll(
        new CreamSolutionHandler(
            result, equationVariables, userDefinedVariables, maximumNumberOfResults, engine),
        10000);
    return result;
  }
}
