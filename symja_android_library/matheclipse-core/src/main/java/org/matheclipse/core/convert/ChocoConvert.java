package org.matheclipse.core.convert;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.constraints.extension.hybrid.HybridTuples;
import org.chocosolver.solver.constraints.extension.hybrid.ISupportable;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.expression.continuous.arithmetic.CArExpression;
import org.chocosolver.solver.expression.continuous.relational.CReExpression;
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression;
import org.chocosolver.solver.expression.discrete.logical.LoExpression;
import org.chocosolver.solver.expression.discrete.logical.NaLoExpression;
import org.chocosolver.solver.expression.discrete.relational.ReExpression;
import org.chocosolver.solver.search.limits.SolutionCounter;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainClosest;
import org.chocosolver.solver.search.strategy.selectors.variables.InputOrder;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.util.ESat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Convert <code>IExpr</code> expressions from and to
 * <a href="https://github.com/chocoteam/choco-solver">Choco solver</a>
 */
public class ChocoConvert {
  private static class IExprPropagator extends Propagator<IntVar> {
    private final IntVar[] vars;
    private final Predicate<IExpr[]> predicate;

    public IExprPropagator(IntVar[] vars, Predicate<IExpr[]> predicate) {
      super(vars, PropagatorPriority.VERY_SLOW, false);
      this.vars = vars;
      this.predicate = predicate;
    }

    @Override
    public void propagate(int evtmask) throws ContradictionException {
      for (int i = 0; i < vars.length; i++) {
        for (int value = vars[i].getLB(); value <= vars[i].getUB(); value =
            vars[i].nextValue(value)) {
          IExpr[] exprs = new IExpr[vars.length];
          for (int j = 0; j < vars.length; j++) {
            exprs[j] = F.ZZ(vars[j].getValue());
          }
          if (!predicate.test(exprs)) {
            vars[i].removeValue(value, this);
          }
        }
      }
    }

    @Override
    public ESat isEntailed() {
      IExpr[] exprs = new IExpr[vars.length];
      for (int i = 0; i < vars.length; i++) {
        exprs[i] = F.ZZ(vars[i].getValue());
      }
      return predicate.test(exprs) ? ESat.TRUE : ESat.FALSE;
    }

    @Override
    public void propagate(int idxVarInProp, int mask) throws ContradictionException {
      propagate(mask);
    }
  }
  private static class PredicatePropagator extends Propagator<IntVar> {
    private final IntVar var;
    private final Predicate<Integer> predicate;

    public PredicatePropagator(IntVar var, Predicate<Integer> predicate) {
      super(new IntVar[] {var}, PropagatorPriority.LINEAR, false);
      this.var = var;
      this.predicate = predicate;
    }

    @Override
    public void propagate(int evtmask) throws ContradictionException {
      for (int value = var.getLB(); value <= var.getUB(); value = var.nextValue(value)) {
        if (!predicate.test(value)) {
          var.removeValue(value, this);
        }
      }
    }

    @Override
    public ESat isEntailed() {
      for (int value = var.getLB(); value <= var.getUB(); value = var.nextValue(value)) {
        if (predicate.test(value)) {
          return ESat.TRUE;
        }
      }
      return ESat.FALSE;
    }

    @Override
    public void propagate(int idxVarInProp, int mask) throws ContradictionException {
      propagate(mask);
    }
  }

  /**
   * Default minimum lower bound for <code>int</code> variables.
   */
  final public static short CHOCO_MIN_VALUE = Short.MIN_VALUE / 2;

  /**
   * Default minimum lower bound for <code>int</code> variables in the {@link S#Primes} domain.
   */
  final public static short CHOCO_MIN_PRIME = 2;

  /**
   * Default maximum upper bound for <code>int</code> variables.
   */
  final public static short CHOCO_MAX_VALUE = Short.MAX_VALUE / 2;

  /**
   * Default maximum upper bound for <code>int</code> variables in the {@link S#Primes} domain.
   */
  final public static short CHOCO_MAX_PRIME = 32749;

  private ChocoConvert() {}

  /**
   * Convert a list of equations to a Choco solver model.
   * 
   * @param list
   * @param variables
   * @param map
   * @param hybridVars
   * @param hybridTuples
   * @param domain {@link S#Integers} or {@link S#Primes}
   * @return
   * @throws ArgumentTypeException
   */
  private static Model expr2IntegerSolver(final IAST list, final IAST variables,
      Map<ISymbol, IntVar> map, IExpr[] hybridVars, HybridTuples hybridTuples, ISymbol domain)
      throws ArgumentTypeException {
    final Predicate<IExpr> isPrime =
        (domain == S.Primes) ? Predicates.isTrue(EvalEngine.get(), S.PrimeQ) : null;
    // Create a constraint network
    Model model = new Model();
    for (int i = 1; i < variables.size(); i++) {
      IExpr expr = variables.get(i);
      if (expr instanceof ISymbol) {
        final IntVar intVar;
        if (domain == S.Primes) {
          intVar = model.intVar(//
              expr.toString(), //
              CHOCO_MIN_PRIME, //
              CHOCO_MAX_PRIME);
          model.post(new Constraint("PrimeConstraint",
              new PredicatePropagator(intVar, x -> isPrime.test(F.ZZ(x)))));
        } else {
          intVar = model.intVar(//
              expr.toString(), //
              CHOCO_MIN_VALUE, //
              CHOCO_MAX_VALUE);
        }
        map.put((ISymbol) expr, intVar);
      }
    }
    if (hybridTuples != null) {
      IntVar[] vars = new IntVar[hybridVars.length];
      for (int i = 0; i < hybridVars.length; i++) {
        IntVar intVar = map.get(hybridVars[i]);
        vars[i] = intVar;
      }
      model.table(vars, hybridTuples);
    }

    IntVar[] vars = new IntVar[map.size()];
    int k = 0;
    for (Entry<ISymbol, IntVar> entry : map.entrySet()) {
      vars[k++] = entry.getValue();
    }
    model.getSolver()
        .setSearch(new IntStrategy(vars, new InputOrder<>(model), new IntDomainClosest()));
    ReExpression[] array = new ReExpression[list.argSize()];
    for (int i = 1; i < list.size(); i++) {
      IExpr element = list.get(i);
      if (element instanceof IAST) {
        ReExpression reLHS = relationalIntegerExpression(model, (IAST) element, map);
        if (reLHS == null) {
          return null;
        }
        array[i - 1] = reLHS;
      }
    }
    NaLoExpression nlExpr = new NaLoExpression(LoExpression.Operator.AND, array);
    nlExpr.post();
    return model;
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
            if (isIntRange(lowerBound)) {
              lhsVar.updateLowerBound(lowerBound + 1, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (rhs instanceof IntVar) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int upperBound = temp.arg1().toIntDefault();
            if (isIntRange(upperBound)) {
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
            if (isIntRange(lowerBound)) {
              lhsVar.updateLowerBound(lowerBound, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (rhs instanceof IntVar) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int upperBound = temp.arg1().toIntDefault();
            if (isIntRange(upperBound)) {
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
            if (isIntRange(upperBound)) {
              lhsVar.updateUpperBound(upperBound, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (rhs instanceof IntVar) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int lowerBound = temp.arg1().toIntDefault();
            if (isIntRange(lowerBound)) {
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
            if (isIntRange(upperBound)) {
              lhsVar.updateUpperBound(upperBound - 1, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (rhs instanceof IntVar) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int lowerBound = temp.arg1().toIntDefault();
            if (isIntRange(lowerBound)) {
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

  /**
   * Return <code>true</code>, if the int <code>value</code> unequals {@link Integer#MIN_VALUE}.
   * 
   * @param value
   * @return
   */
  private static boolean isIntRange(int value) {
    return value != Integer.MIN_VALUE;
  }

  private static ArExpression integerExpression(Model net, IExpr expr, Map<ISymbol, IntVar> map)
      throws ArgumentTypeException {
    if (expr instanceof ISymbol) {
      IntVar temp = map.get(expr);
      if (temp == null) {
        temp = net.intVar(expr.toString(), CHOCO_MIN_VALUE, CHOCO_MAX_VALUE);
        map.put((ISymbol) expr, temp);
      }
      return temp;
    }
    if (expr instanceof IInteger) {
      int value = ((IInteger) expr).toInt(); // throws ArithmeticException
      return net.intVar(value);
    }
    if (expr instanceof IFraction) {
      IFraction fraction = (IFraction) expr;
      IExpr numerator = fraction.numerator();
      IExpr denominator = fraction.denominator();
      ArExpression result = integerExpression(net, numerator, map);
      result = result.div(integerExpression(net, denominator, map));
      return result;

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
        if (ast.isAST2() && ast.arg1().isMinusOne()) {
          return integerExpression(net, ast.arg2(), map).neg();
        }
        ArExpression result = integerExpression(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = result.mul(integerExpression(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isPower()) {
        IExpr exponent = ast.exponent();
        if (exponent.isInteger()) {
          int value = exponent.toIntDefault();
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
          // if (value == -1) {
          // IExpr base = ast.base();
          // ArExpression one = integerExpression(net, F.C1, map);
          // ArExpression result = integerExpression(net, base, map);
          // result = one.div(result);
          // return result;
          // }
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
   * @param maximumNumberOfResults the maximum number of results to return; if < 0 return all
   * @param hybridTuples TODO
   * @param domain {@link S#Integers} or {@link S#Primes}
   * @param engine
   * @return a list of rules with the integer solutions; or if no solution exists return
   *         {@link F#NIL}
   */
  public static IAST integerSolve(final IAST list, final IAST equationVariables,
      final IAST userDefinedVariables, final int maximumNumberOfResults, IExpr[] hybridVars,
      HybridTuples hybridTuples, ISymbol domain, final EvalEngine engine) {
    TreeMap<ISymbol, IntVar> map = new TreeMap<ISymbol, IntVar>();
    Model model =
        expr2IntegerSolver(list, equationVariables, map, hybridVars, hybridTuples, domain);
    List<Solution> res = model.getSolver().findAllSolutions(new SolutionCounter(model,
        maximumNumberOfResults < 0 ? Short.MAX_VALUE : maximumNumberOfResults));
    if (res.size() == 0) {
      return F.CEmptyList;
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
        temp = net.realVar(CHOCO_MIN_VALUE, CHOCO_MAX_VALUE);
        map.put((ISymbol) expr, temp);
      }
      return temp;
    }
    if (expr.isNumericFunction(true)) {
      try {
        double value = expr.evalf();
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
          int value = exponent.toIntDefault();
          if (value >= -3) {
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
    // RealVar[] vars = new RealVar[map.size()];
    // int k = 0;
    // for (Entry<ISymbol, RealVar> entry : map.entrySet()) {
    // vars[k++] = entry.getValue();
    // }
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

  public static HybridTuples listOfRulesToTuples(IAST diophantineResult, ISymbol head,
      IExpr[] hybridVars, EvalEngine engine) {
    HybridTuples tuples = new HybridTuples();
    for (int i = 1; i < diophantineResult.size(); i++) {
      IAST subList = (IAST) diophantineResult.get(i);
      ISupportable[] supp = new ISupportable[subList.argSize()];
      for (int j = 1; j < subList.size(); j++) {
        IExpr rule = subList.get(j);
        if (rule.isRuleAST()) {
          IExpr lhs = rule.first();
          IExpr rhs = rule.second();
          int value = rhs.toIntDefault();
          if (value != Integer.MIN_VALUE) {
            hybridVars[j - 1] = lhs;
            supp[j - 1] = HybridTuples.eq(value);
          } else {
            // Machine-sized integer expected at position `2` in `1`.
            Errors.printMessage(head, "intm", F.List(rule, F.C2), engine);
            return null;
          }
        }
      }
      tuples.add(supp);
    }
    return tuples;
  }
}
