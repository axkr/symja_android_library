package org.matheclipse.core.convert;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.expression.continuous.arithmetic.CArExpression;
import org.chocosolver.solver.expression.continuous.relational.CReExpression;
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression;
import org.chocosolver.solver.expression.discrete.logical.LoExpression;
import org.chocosolver.solver.expression.discrete.logical.NaLoExpression;
import org.chocosolver.solver.expression.discrete.relational.ReExpression;
import org.chocosolver.solver.search.limits.SolutionCounter;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.solver.variables.impl.IntervalIntVarImpl;
import org.matheclipse.core.basic.Config;
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
 * Convert <code>IExpr</code> expressions from and to
 * <a href="https://github.com/chocoteam/choco-solver">Choco solver</a>
 */
public class ChocoConvert {

  private ChocoConvert() {}

  private static Model expr2IntegerSolver(final IAST list, final IAST variables,
      Map<ISymbol, IntVar> map) throws ArgumentTypeException {

    // Create a constraint network
    Model net = new Model();
    Solver solver = net.getSolver();
    for (int i = 1; i < variables.size(); i++) {
      if (variables.get(i) instanceof ISymbol) {
        map.put((ISymbol) variables.get(i), new IntervalIntVarImpl(//
            variables.get(i).toString(), //
            Short.MIN_VALUE + 1, // +1 to avoid java.lang.IndexOutOfBoundsException in bitset range
            Short.MAX_VALUE, //
            net));
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
        ReExpression reLHS = relationalIntegerExpression(net, temp, map);
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

  private static ReExpression relationalIntegerExpression(Model net, IAST temp,
      Map<ISymbol, IntVar> map) {
    ArExpression lhs;
    ArExpression rhs;
    if (temp.isAST2()) {
      lhs = integerExpression(net, temp.arg1(), map);
      rhs = integerExpression(net, temp.arg2(), map);
      if (temp.isEqual()) {
        return lhs.eq(rhs);
      } else if (temp.isAST(S.Unequal, 3)) {
        return lhs.ne(rhs);
      } else if (temp.isAST(S.Greater, 3)) {
        if (lhs instanceof IntVar) {
          IntVar lhsVar = (IntVar) lhs;
          try {
            int lowerBound = temp.arg2().toIntDefault();
            if (lowerBound != Integer.MIN_VALUE) {
              lhsVar.updateLowerBound(lowerBound + 1, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (rhs instanceof IntVar) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int upperBound = temp.arg1().toIntDefault();
            if (upperBound != Integer.MIN_VALUE) {
              rhsVar.updateUpperBound(upperBound - 1, rhsVar);
            }
          } catch (ContradictionException e) {
          }
        }
        return lhs.gt(rhs);
      } else if (temp.isAST(S.GreaterEqual, 3)) {
        if (lhs instanceof IntVar) {
          IntVar lhsVar = (IntVar) lhs;
          try {
            int lowerBound = temp.arg2().toIntDefault();
            if (lowerBound != Integer.MIN_VALUE) {
              lhsVar.updateLowerBound(lowerBound, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (rhs instanceof IntVar) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int upperBound = temp.arg1().toIntDefault();
            if (upperBound != Integer.MIN_VALUE) {
              rhsVar.updateUpperBound(upperBound, rhsVar);
            }
          } catch (ContradictionException e) {
          }
        }
        return lhs.ge(rhs);
      } else if (temp.isAST(S.LessEqual, 3)) {
        if (lhs instanceof IntVar) {
          IntVar lhsVar = (IntVar) lhs;
          try {
            int upperBound = temp.arg2().toIntDefault();
            if (upperBound != Integer.MIN_VALUE) {
              lhsVar.updateUpperBound(upperBound, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (rhs instanceof IntVar) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int lowerBound = temp.arg1().toIntDefault();
            if (lowerBound != Integer.MIN_VALUE) {
              rhsVar.updateLowerBound(lowerBound, rhsVar);
            }
          } catch (ContradictionException e) {
          }
        }
        return lhs.le(rhs);
      } else if (temp.isAST(S.Less, 3)) {
        if (lhs instanceof IntVar) {
          IntVar lhsVar = (IntVar) lhs;
          try {
            int upperBound = temp.arg2().toIntDefault();
            if (upperBound != Integer.MIN_VALUE) {
              lhsVar.updateUpperBound(upperBound - 1, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (rhs instanceof IntVar) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int lowerBound = temp.arg1().toIntDefault();
            if (lowerBound != Integer.MIN_VALUE) {
              rhsVar.updateLowerBound(lowerBound + 1, rhsVar);
            }
          } catch (ContradictionException e) {
          }
        }
        return lhs.lt(rhs);
      }
    }
    throw new ArgumentTypeException(
        temp.toString() + " is no relational expression found for Solve(..., Integers)");
  }

  private static ArExpression integerExpression(Model net, IExpr expr, Map<ISymbol, IntVar> map)
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
        ArExpression result = integerExpression(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.add(integerExpression(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isTimes()) {
        ArExpression result = integerExpression(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.mul(integerExpression(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isPower()) {
        IExpr exponent = ast.exponent();
        if (exponent.isInteger()) {
          int value = ((IInteger) exponent).toIntDefault();
          if (value > 0) {
            IExpr base = ast.base();
            ArExpression result = integerExpression(net, base, map);
            if (value == 2) {
              result = result.sqr();
            } else {
              result = result.pow(value);
            }
            return result;
          }
        }
      } else if (ast.isSameHeadSizeGE(S.Max, 3)) {
        ArExpression result = integerExpression(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.max(integerExpression(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isSameHeadSizeGE(S.Min, 3)) {
        ArExpression result = integerExpression(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.min(integerExpression(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isAST(S.Mod, 3)) {
        ArExpression result = integerExpression(net, ast.arg1(), map);
        return result.mod(integerExpression(net, ast.arg2(), map));
      } else if (ast.isAbs()) {
        return integerExpression(net, ast.arg1(), map).abs();
        // } else if (ast.isAST(F.Sign, 2)) {
        // return integerVariable(net, ast.arg1()).sign();
      }
    }
    throw new ArgumentTypeException(
        expr.toString() + " is no int variable found for Solve(..., Integers)");
  }

  /**
   * Create choco integer solver solutions.
   *
   * @param list
   * @param equationVariables all variables which are defined in the equations
   * @param userDefinedVariables all variables which are defined by the user. May contain additional
   *        variables which aren't available in <code>equationVariables</code>
   * @param engine
   * @return a list of rules with the integer solutions; or if no solution exists return
   *         {@link F#NIL}
   */
  public static IAST integerSolve(final IAST list, final IAST equationVariables,
      final IAST userDefinedVariables, final int maximumNumberOfResults, final EvalEngine engine) {
    TreeMap<ISymbol, IntVar> map = new TreeMap<ISymbol, IntVar>();
    Model model = expr2IntegerSolver(list, equationVariables, map);
    List<Solution> res = model.getSolver().findAllSolutions(new SolutionCounter(model,
        maximumNumberOfResults < 0 ? Short.MAX_VALUE : maximumNumberOfResults));
    if (res.size() == 0) {
      return F.NIL;
    }
    IASTAppendable result = F.ListAlloc(res.size());
    for (int i = 0; i < res.size(); i++) {
      Solution solution = res.get(i);
      if (solution != null) {
        IExpr listOfZZVariables = F.NIL;
        IExpr complement = S.Complement.of(engine, userDefinedVariables, equationVariables);
        if (complement.size() > 1 && complement.isList()) {
          listOfZZVariables =
              S.Apply.of(engine, S.And, complement.mapThread(F.Element(F.Slot1, S.Integers), 1));
        }

        Set<Entry<ISymbol, IntVar>> set = map.entrySet();
        IASTAppendable temp = F.ListAlloc(set.size());
        for (Entry<ISymbol, IntVar> entry : set) {
          ISymbol variable = entry.getKey();
          if (listOfZZVariables.isPresent()) {
            temp.append(F.Rule(variable, F.ConditionalExpression(
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

  private static CArExpression realExpression(Model net, IExpr expr, Map<ISymbol, RealVar> map)
      throws ArgumentTypeException {
    if (expr instanceof ISymbol) {
      RealVar temp = map.get(expr);
      if (temp == null) {
        temp = net.realVar(Short.MIN_VALUE, Short.MAX_VALUE);
        map.put((ISymbol) expr, temp);
      }
      return temp;
    }
    if (expr.isNumericFunction(true)) {
      try {
        double value = expr.evalDouble();
        return net.realVar(value);
      } catch (ArgumentTypeException ate) {
        // fall through
      }
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (ast.size() == 2) {
        if (ast.isAbs()) {
          return realExpression(net, ast.arg1(), map).abs();
        }
        if (ast.isArcCos()) {
          return realExpression(net, ast.arg1(), map).acos();
        }
        if (ast.isArcSin()) {
          return realExpression(net, ast.arg1(), map).asin();
        }
        if (ast.isArcTan()) {
          return realExpression(net, ast.arg1(), map).atan();
        }
        if (ast.isCos()) {
          return realExpression(net, ast.arg1(), map).cos();
        }
        if (ast.isLog()) {
          return realExpression(net, ast.arg1(), map).ln();
        }
        if (ast.isSin()) {
          return realExpression(net, ast.arg1(), map).sin();
        }
        if (ast.isTan()) {
          return realExpression(net, ast.arg1(), map).tan();
        }
      }
      if (ast.isPlus()) {
        CArExpression result = realExpression(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.add(realExpression(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isTimes()) {
        CArExpression result = realExpression(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.mul(realExpression(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isPower()) {
        IExpr base = ast.base();
        IExpr exponent = ast.exponent();
        if (base.isE()) {
          return realExpression(net, exponent, map).exp();
        }
        if (exponent.isInteger()) {
          int value = ((IInteger) exponent).toIntDefault();
          if (value >= -1) {
            if (value == -1) {
              CArExpression result = realExpression(net, base, map);
              result = net.realVar(1.0).div(result);
              return result;
            } else if (value == -2) {
              CArExpression result = realExpression(net, base, map);
              result = result.mul(realExpression(net, base, map));
              result = net.realVar(1.0).div(result);
              return result;
            } else if (value == -3) {
              CArExpression result = realExpression(net, base, map);
              result = result.mul(realExpression(net, base, map));
              result = result.mul(realExpression(net, base, map));
              result = net.realVar(1.0).div(result);
              return result;
            } else if (value == 1) {
              return realExpression(net, base, map);
            } else if (value == 2) {
              CArExpression result = realExpression(net, base, map);
              result = result.mul(realExpression(net, base, map));
              return result;
            } else if (value == 3) {
              CArExpression result = realExpression(net, base, map);
              result = result.mul(realExpression(net, base, map));
              result = result.mul(realExpression(net, base, map));
              return result;
            } else {
              CArExpression result = realExpression(net, base, map);
              result = result.pow(value);
              return result;
            }
          }
        }
      } else if (ast.isSameHeadSizeGE(S.Max, 3)) {
        CArExpression result = realExpression(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.max(realExpression(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isSameHeadSizeGE(S.Min, 3)) {
        CArExpression result = realExpression(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.min(realExpression(net, ast.get(i), map));
        }
        return result;
      }
    }
    throw new ArgumentTypeException(
        expr.toString() + " is no int variable found for Solve(..., Integers)");
  }

  private static CReExpression relationalExpression(Model net, IAST temp,
      Map<ISymbol, RealVar> map) {
    CArExpression lhs;
    CArExpression rhs;
    if (temp.isAST2()) {
      lhs = realExpression(net, temp.arg1(), map);
      rhs = realExpression(net, temp.arg2(), map);
      if (temp.isEqual()) {
        return lhs.eq(rhs);
        // } else if (temp.isAST(S.Unequal, 3)) {
        // return lhs.ne(rhs);
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

  private static Model expr2RealSolver(final IAST list, final IAST variables,
      Map<ISymbol, RealVar> map) throws ArgumentTypeException {

    // Create a constraint network
    Model net = new Model();
    // Solver solver = net.getSolver();
    for (int i = 1; i < variables.size(); i++) {
      if (variables.get(i) instanceof ISymbol) {
        map.put((ISymbol) variables.get(i),
            net.realVar("x", Double.MIN_VALUE, Double.MAX_VALUE, 0.000001d));
      }
    }
    RealVar[] vars = new RealVar[map.size()];
    int k = 0;
    for (Entry<ISymbol, RealVar> entry : map.entrySet()) {
      vars[k++] = entry.getValue();
    }
    IAST temp;
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i) instanceof IAST) {
        temp = (IAST) list.get(i);
        CReExpression reLHS = relationalExpression(net, temp, map);
        if (reLHS == null) {
          return null;
        }
        reLHS.post();
      }
    }

    return net;
  }

  public static IAST realSolve(final IAST list, final IAST equationVariables,
      final IAST userDefinedVariables, final EvalEngine engine) {
    TreeMap<ISymbol, RealVar> map = new TreeMap<ISymbol, RealVar>();
    Model model = expr2RealSolver(list, equationVariables, map);
    Solution sol = model.getSolver().findSolution(new SolutionCounter(model, 1));
    IASTAppendable result = F.ListAlloc(1);
    if (sol != null) {
      IExpr listOfRRVariables = F.NIL;
      IExpr complement = F.Complement.of(engine, userDefinedVariables, equationVariables);
      if (complement.size() > 1 && complement.isList()) {
        listOfRRVariables =
            S.Apply.of(engine, S.And, complement.mapThread(F.Element(F.Slot1, S.Reals), 1));
      }

      Set<Entry<ISymbol, RealVar>> set = map.entrySet();
      IASTAppendable temp = F.ListAlloc(set.size());
      for (Entry<ISymbol, RealVar> entry : set) {
        ISymbol variable = entry.getKey();
        double[] realBounds = sol.getRealBounds(entry.getValue());
        IExpr resultValue;
        if (F.isFuzzyEquals(realBounds[0], realBounds[1], Config.DEFAULT_ROOTS_CHOP_DELTA)) {
          resultValue = F.num((realBounds[0] + realBounds[1]) / 2.0);
        } else {
          resultValue = F.Interval(F.List(realBounds[0], realBounds[1]));
        }
        if (listOfRRVariables.isPresent()) {
          temp.append(F.Rule(variable, F.ConditionalExpression(resultValue, listOfRRVariables)));
        } else {
          temp.append(F.Rule(variable, resultValue));
        }
      }
      result.append(temp);
    }

    return result;
  }
}
