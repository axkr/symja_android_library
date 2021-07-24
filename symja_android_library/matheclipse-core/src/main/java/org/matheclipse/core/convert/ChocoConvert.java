package org.matheclipse.core.convert;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression;
import org.chocosolver.solver.expression.discrete.logical.LoExpression;
import org.chocosolver.solver.expression.discrete.logical.NaLoExpression;
import org.chocosolver.solver.expression.discrete.relational.ReExpression;
import org.chocosolver.solver.search.limits.SolutionCounter;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.impl.IntervalIntVarImpl;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Convert <code>IExpr</code> expressions from and to <a
 * href="https://github.com/chocoteam/choco-solver">Choco solver</a>
 */
public class ChocoConvert {

  private ChocoConvert() {}

  private static Model expr2ChocoSolver(
      final IAST list, final IAST variables, Map<ISymbol, IntVar> map) throws ArgumentTypeException {

    // Create a constraint network
    Model net = new Model();
    Solver solver = net.getSolver();
    for (int i = 1; i < variables.size(); i++) {
      if (variables.get(i) instanceof ISymbol) {
        map.put(
            (ISymbol) variables.get(i),
            new IntervalIntVarImpl(
                variables.get(i).toString(), Short.MIN_VALUE, Short.MAX_VALUE, net));
      }
    }
    IntVar[] vars = new IntervalIntVarImpl[map.size()];
    int k = 0;
    for (Entry<ISymbol, IntVar> entry : map.entrySet()) {
      vars[k++] = entry.getValue();
    }
    solver.setSearch(Search.inputOrderLBSearch(vars));
    IAST temp;
    ReExpression[] array = new ReExpression[list.size() - 1];
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i) instanceof IAST) {
        temp = (IAST) list.get(i);
        ReExpression reLHS = relationalExpression(net, temp, map);
        if (reLHS == null) {
          return null;
        }
        array[i - 1] = reLHS;
      }
    }
    NaLoExpression nlExpr = new NaLoExpression(LoExpression.Operator.AND, array);
    nlExpr.post();
    return net;
  }

  private static ReExpression relationalExpression(Model net, IAST temp, Map<ISymbol, IntVar> map) {
    ArExpression lhs;
    ArExpression rhs;
    if (temp.isAST2()) {
      lhs = integerVariable(net, temp.arg1(), map);
      rhs = integerVariable(net, temp.arg2(), map);
      if (temp.isEqual()) {
        return lhs.eq(rhs);
      } else if (temp.isAST(S.Unequal, 3)) {
        return lhs.ne(rhs);
      } else if (temp.isAST(S.Greater, 3)) {
        return lhs.gt(rhs);
      } else if (temp.isAST(S.GreaterEqual, 3)) {
        return lhs.ge(rhs);
      } else if (temp.isAST(S.LessEqual, 3)) {
        return lhs.le(rhs);
      } else if (temp.isAST(S.Less, 3)) {
        return lhs.lt(rhs);
      }
    }
    throw new ArgumentTypeException(
        temp.toString() + " is no relational expression found for Solve(..., Integers)");
  }

  private static ArExpression integerVariable(Model net, IExpr expr, Map<ISymbol, IntVar> map)
      throws ArgumentTypeException {
    if (expr instanceof ISymbol) {
      IntVar temp = map.get(expr);
      if (temp == null) {
        temp = net.intVar(Short.MIN_VALUE, Short.MAX_VALUE);
        map.put((ISymbol) expr, temp);
      }
      return temp;
    }
    if (expr instanceof IInteger) {
      int value = ((IInteger) expr).toInt(); // throws ArithmeticException
      return net.intVar(value);
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (ast.isPlus()) {
        ArExpression result = integerVariable(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.add(integerVariable(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isTimes()) {
        ArExpression result = integerVariable(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.mul(integerVariable(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isPower()) {
        IExpr exponent = ast.exponent();
        if (exponent.isInteger()) {
          int value = ((IInteger) exponent).toIntDefault();
          if (value > 0) {
            IExpr base = ast.base();
            ArExpression result = integerVariable(net, base, map);
            result = result.pow(value);
            return result;
          }
        }
      } else if (ast.isSameHeadSizeGE(S.Max, 3)) {
        ArExpression result = integerVariable(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.max(integerVariable(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isSameHeadSizeGE(S.Min, 3)) {
        ArExpression result = integerVariable(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.min(integerVariable(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isAbs()) {
        return integerVariable(net, ast.arg1(), map).abs();
        // } else if (ast.isAST(F.Sign, 2)) {
        // return integerVariable(net, ast.arg1()).sign();
      }
    }
    throw new ArgumentTypeException(
        expr.toString() + " is no int variable found for Solve(..., Integers)");
  }

  /**
   * Create choco integer solver.
   *
   * @param list
   * @param equationVariables all variables which are defined in the equations
   * @param userDefinedVariables all variables which are defined by the user. May contain additional
   *     variables which aren't available in <code>equationVariables</code>
   * @param engine
   * @return a list of rules with the integer solutions; or if no solution exists return {@link
   *     F#NIL}
   */
  public static IAST integerSolve(
      final IAST list,
      final IAST equationVariables,
      final IAST userDefinedVariables,
      final EvalEngine engine) {
    TreeMap<ISymbol, IntVar> map = new TreeMap<ISymbol, IntVar>();
    Model model = expr2ChocoSolver(list, equationVariables, map);
    List<Solution> res =
        model.getSolver().findAllSolutions(new SolutionCounter(model, Short.MAX_VALUE));
    if (res.size() == 0) {
      return F.NIL;
    }
    IASTAppendable result = F.ListAlloc(res.size());
    for (int i = 0; i < res.size(); i++) {
      Solution solution = res.get(i);
      if (solution != null) {
        IExpr listOfZZVariables = F.NIL;
        IExpr complement = F.Complement.of(engine, userDefinedVariables, equationVariables);
        if (complement.size() > 1 && complement.isList()) {
          listOfZZVariables =
              F.Apply.of(
                  engine, F.And, ((IAST) complement).mapThread(F.Element(F.Slot1, F.Integers), 1));
        }

        Set<Entry<ISymbol, IntVar>> set = map.entrySet();
        IASTAppendable temp = F.ListAlloc(set.size());
        for (Entry<ISymbol, IntVar> entry : set) {
          ISymbol variable = entry.getKey();
          if (listOfZZVariables.isPresent()) {
            temp.append(
                F.Rule(
                    variable,
                    F.ConditionalExpression(
                        F.ZZ(solution.getIntVal(entry.getValue())), listOfZZVariables)));
          } else {
            temp.append(F.Rule(variable, F.ZZ(solution.getIntVal(entry.getValue()))));
          }
        }
        result.append(temp);
      }
    }

    return result;
  }
}
