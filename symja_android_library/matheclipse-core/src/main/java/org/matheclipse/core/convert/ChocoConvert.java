package org.matheclipse.core.convert;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.expression.continuous.arithmetic.CArExpression;
import org.chocosolver.solver.expression.continuous.relational.CReExpression;
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression;
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
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.external.fastutil.ints.Int2IntOpenHashMap;

/**
 * Convert <code>IExpr</code> expressions from and to
 * <a href="https://github.com/chocoteam/choco-solver">Choco solver</a>
 */
public class ChocoConvert {

  /**
   * Call the <code>unaryFunction::applyAsInt</code> and cache the results for each input value.
   */
  private static class CachedFunctionPropagator extends Propagator<IntVar> {
    private final IntVar x;
    private final IntVar y;
    private final IntUnaryOperator unaryFunction;
    private final Int2IntOpenHashMap cache;

    public CachedFunctionPropagator(IntVar x, IntVar y, IntUnaryOperator unaryFunction) {
      super(new IntVar[] {x, y}, PropagatorPriority.VERY_SLOW, false);
      this.x = x;
      this.y = y;
      this.unaryFunction = unaryFunction;
      this.cache = new Int2IntOpenHashMap();
    }

    private int evaluateCached(int val) {
      return cache.computeIfAbsent(val, unaryFunction::applyAsInt);
    }

    @Override
    public void propagate(int evtmask) throws ContradictionException {
      int ubX = x.getUB();
      int minY = Integer.MAX_VALUE;
      int maxY = Integer.MIN_VALUE;

      // 1. Forward Check: Prune X based on the domain of Y
      for (int valX = x.getLB(); valX <= ubX; valX = x.nextValue(valX)) {
        try {
          int fVal = evaluateCached(valX);
          if (!y.contains(fVal)) {
            x.removeValue(valX, this);
          } else {
            if (fVal < minY)
              minY = fVal;
            if (fVal > maxY)
              maxY = fVal;
          }
        } catch (Exception e) {
          // Function is undefined for this input, prune it immediately
          x.removeValue(valX, this);
        }
      }

      // 2. Update Y's bounds
      if (minY <= maxY) {
        y.updateLowerBound(minY, this);
        y.updateUpperBound(maxY, this);
      }

      // 3. Arc Consistency: Prune Y values that have no supporting X value
      int ubY = y.getUB();
      for (int valY = y.getLB(); valY <= ubY; valY = y.nextValue(valY)) {
        boolean supported = false;
        ubX = x.getUB();
        for (int valX = x.getLB(); valX <= ubX; valX = x.nextValue(valX)) {
          try {
            if (evaluateCached(valX) == valY) {
              supported = true;
              break;
            }
          } catch (Exception e) {
            // Undefined inputs cannot support this Y value, skip
          }
        }
        if (!supported) {
          y.removeValue(valY, this);
        }
      }
    }

    @Override
    public ESat isEntailed() {
      boolean allValid = true;
      boolean anyValid = false;
      int ubX = x.getUB();

      for (int valX = x.getLB(); valX <= ubX; valX = x.nextValue(valX)) {
        try {
          int fVal = evaluateCached(valX);
          if (y.contains(fVal)) {
            anyValid = true;
          } else {
            allValid = false;
          }
        } catch (Exception e) {
          allValid = false;
        }
      }

      if (!anyValid)
        return ESat.FALSE;
      if (allValid && x.isInstantiated() && y.isInstantiated())
        return ESat.TRUE;
      return ESat.UNDEFINED;
    }
  }

  /**
   * Call the <code>binaryFunction::applyAsInt</code> and cache the results for each pair of input
   * values.
   */
  private static class CachedBinaryFunctionPropagator extends Propagator<IntVar> {

    private final IntVar x1;
    private final IntVar x2;
    private final IntVar y;
    private final IntBinaryOperator binaryFunction;

    // Using Long as the key to pack two 32-bit ints, avoiding object creation overhead
    private final Map<Long, Integer> cache;

    public CachedBinaryFunctionPropagator(IntVar x1, IntVar x2, IntVar y,
        IntBinaryOperator binaryFunction) {
      // QUADRATIC priority is suitable since we iterate over D * D (Domain 1 * Domain 2)
      super(new IntVar[] {x1, x2, y}, PropagatorPriority.QUADRATIC, false);
      this.x1 = x1;
      this.x2 = x2;
      this.y = y;
      this.binaryFunction = binaryFunction;
      this.cache = new HashMap<>();
    }

    /**
     * Packs two integers into a single long for fast cache lookups.
     */
    private int evaluateCached(int val1, int val2) {
      long key = (((long) val1) << 32) | (val2 & 0xffffffffL);
      return cache.computeIfAbsent(key, k -> binaryFunction.applyAsInt(val1, val2));
    }

    @Override
    public void propagate(int evtmask) throws ContradictionException {
      Set<Integer> supportedX1 = new HashSet<>();
      Set<Integer> supportedX2 = new HashSet<>();
      Set<Integer> supportedY = new HashSet<>();

      // 1. Explore Cartesian product (x1 * x2)
      int ub1 = x1.getUB();
      for (int val1 = x1.getLB(); val1 <= ub1; val1 = x1.nextValue(val1)) {
        int ub2 = x2.getUB();
        for (int val2 = x2.getLB(); val2 <= ub2; val2 = x2.nextValue(val2)) {
          try {
            int fVal = evaluateCached(val1, val2);

            // If the calculated value is a valid assignment for Y, the tuple is supported
            if (y.contains(fVal)) {
              supportedX1.add(val1);
              supportedX2.add(val2);
              supportedY.add(fVal);
            }
          } catch (RuntimeException e) {
            // Function is undefined for this pair, skip it
          }
        }
      }

      // 2. If no valid tuples were found, the constraints cannot be satisfied
      if (supportedY.isEmpty()) {
        fails(); // throws ContradictionException
      }

      // 3. Prune the Output Variable (Y)
      int ubY = y.getUB();
      for (int valY = y.getLB(); valY <= ubY; valY = y.nextValue(valY)) {
        if (!supportedY.contains(valY)) {
          y.removeValue(valY, this);
        }
      }

      // 4. Prune Input Variable 1 (x1)
      ub1 = x1.getUB();
      for (int val1 = x1.getLB(); val1 <= ub1; val1 = x1.nextValue(val1)) {
        if (!supportedX1.contains(val1)) {
          x1.removeValue(val1, this);
        }
      }

      // 5. Prune Input Variable 2 (x2)
      int ub2 = x2.getUB();
      for (int val2 = x2.getLB(); val2 <= ub2; val2 = x2.nextValue(val2)) {
        if (!supportedX2.contains(val2)) {
          x2.removeValue(val2, this);
        }
      }
    }

    @Override
    public ESat isEntailed() {
      if (isCompletelyInstantiated()) {
        try {
          int fVal = evaluateCached(x1.getValue(), x2.getValue());
          return fVal == y.getValue() ? ESat.TRUE : ESat.FALSE;
        } catch (Exception e) {
          return ESat.FALSE;
        }
      }
      return ESat.UNDEFINED;
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
      boolean accepted = false;
      boolean rejected = false;
      for (int value = var.getLB(); value <= var.getUB(); value = var.nextValue(value)) {
        if (predicate.test(value)) {
          accepted = true;
        } else {
          rejected = true;
        }
        if (accepted && rejected) {
          return ESat.UNDEFINED;
        }
      }
      return accepted ? ESat.TRUE : ESat.FALSE;
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

  /**
   * Name of the {@link Model} hook which is set if an intermediate value range had to be restricted
   * to choco's safe <code>int</code> range.
   */
  private static final String RESTRICTED_RANGE = "symja.restrictedRange";

  private ChocoConvert() {}

  /**
   * Convert a list of equations to a Choco solver model.
   * 
   * @param list
   * @param variables
   * @param map
   * @param domain {@link S#Integers} or {@link S#Primes}
   * @return
   * @throws ArgumentTypeException
   */
  private static Model expr2IntegerSolver(final IAST list, final IAST variables,
      Map<ISymbol, IntVar> map, ISymbol domain) throws ArgumentTypeException {
    final Predicate<IExpr> isPrime =
        (domain == S.Primes) ? Predicates.isTrue(EvalEngine.get(), S.PrimeQ) : null;
    // Create a constraint network
    // lazy clause generation (SettingsBuilder#setLCG) is not an option: it rejects pow() and
    // products which leave the int range, and can't explain the propagators of this class
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
    IntVar[] vars = new IntVar[map.size()];
    int k = 0;
    for (Entry<ISymbol, IntVar> entry : map.entrySet()) {
      vars[k++] = entry.getValue();
    }
    model.getSolver()
        .setSearch(new IntStrategy(vars, new InputOrder<>(model), new IntDomainClosest()));
    List<ReExpression> constraints = new java.util.ArrayList<ReExpression>(list.argSize());
    // the constraints are a conjunction, so the bounds of single variables are translated first:
    // they narrow the domains from which the ranges of powers and products are computed
    IAST integral = list.map(ChocoConvert::integralRelation);
    IASTAppendable ordered = F.ListAlloc(integral.argSize());
    ordered.appendArgs(integral.select(ChocoConvert::isVariableBound));
    ordered.appendArgs(integral.select(x -> !isVariableBound(x)));
    for (int i = 1; i < ordered.size(); i++) {
      IExpr element = ordered.get(i);
      if (element.isTrue()) {
        // a constraint which always holds constrains nothing
        continue;
      }
      if (element.isFalse()) {
        // a constraint which never holds makes the whole model unsatisfiable
        return null;
      }
      if (!(element instanceof IAST)) {
        return null;
      }
      constraints.add(booleanExpression(model, (IAST) element, map, true));
    }
    if (constraints.isEmpty()) {
      // without a constraint every point of the search box is a solution, which is not an answer
      return null;
    }
    // each constraint of the conjunction is posted on its own. Posting them as one n-ary AND
    // would reify every single relation instead of propagating it directly.
    for (int i = 0; i < constraints.size(); i++) {
      constraints.get(i).post();
    }
    return model;
  }

  /**
   * Translate a relation or a combination of relations with <code>And, Or, Not, Xor, Implies</code>.
   *
   * @param topLevel <code>true</code> if <code>expr</code> is one of the constraints which all
   *        have to hold. Only such a constraint may narrow the domain of a variable.
   */
  private static ReExpression booleanExpression(Model net, IAST expr, Map<ISymbol, IntVar> map,
      boolean topLevel) {
    if (expr.isAnd() || expr.isOr() || expr.isAST(S.Xor)) {
      if (expr.argSize() == 0) {
        throw new ArgumentTypeException(
            expr.toString() + " is no relational expression found for Solve(..., Integers)");
      }
      // a conjunction below the top level sits inside a disjunction or negation
      boolean conjunct = topLevel && expr.isAnd();
      ReExpression result = booleanOperand(net, expr.arg1(), map, conjunct);
      for (int i = 2; i < expr.size(); i++) {
        ReExpression operand = booleanOperand(net, expr.get(i), map, conjunct);
        if (expr.isAnd()) {
          result = result.and(operand);
        } else if (expr.isOr()) {
          result = result.or(operand);
        } else {
          // folding pairwise gives "an odd number of operands holds"
          result = result.xor(operand);
        }
      }
      return result;
    }
    if (expr.isNot()) {
      return booleanOperand(net, expr.arg1(), map, false).not();
    }
    if (expr.isAST(S.Implies, 3)) {
      return booleanOperand(net, expr.arg1(), map, false)
          .imp(booleanOperand(net, expr.arg2(), map, false));
    }
    IExpr relation = topLevel ? expr : integralRelation(expr);
    if (relation.isTrue() || relation.isFalse()) {
      return net.boolVar(relation.isTrue());
    }
    return relationalIntegerExpression(net, (IAST) relation, map, topLevel);
  }

  private static ReExpression booleanOperand(Model net, IExpr expr, Map<ISymbol, IntVar> map,
      boolean topLevel) {
    if (expr.isTrue() || expr.isFalse()) {
      return net.boolVar(expr.isTrue());
    }
    if (expr.isAST()) {
      return booleanExpression(net, (IAST) expr, map, topLevel);
    }
    throw new ArgumentTypeException(
        expr.toString() + " is no relational expression found for Solve(..., Integers)");
  }

  /**
   * Rewrite a relation with rational numbers into one with integers only. The terms of a relation
   * have integer values, so <code>x &gt;= 3/2</code> is <code>x &gt;= 2</code> and
   * <code>x*y == 3/2</code> never holds. Other relations are multiplied by the common denominator.
   *
   * @return the rewritten relation, {@link S#True}, {@link S#False} or <code>expr</code> itself
   */
  private static IExpr integralRelation(IExpr expr) {
    if (!expr.isAST2() || !isRelation(expr) || expr.isFree(x -> x.isFraction(), false)) {
      return expr;
    }
    IAST relation = (IAST) expr;
    IExpr lhs = relation.arg1();
    IExpr rhs = relation.arg2();
    ISymbol head = (ISymbol) relation.head();
    if (lhs.isFraction() && rhs.isFree(x -> x.isFraction(), false)) {
      // mirror the relation, so that the rational number is on the right-hand side
      IExpr swap = lhs;
      lhs = rhs;
      rhs = swap;
      if (head == S.Greater) {
        head = S.Less;
      } else if (head == S.GreaterEqual) {
        head = S.LessEqual;
      } else if (head == S.Less) {
        head = S.Greater;
      } else if (head == S.LessEqual) {
        head = S.GreaterEqual;
      }
    }
    if (rhs.isFraction() && lhs.isFree(x -> x.isFraction(), false)) {
      IFraction fraction = (IFraction) rhs;
      if (head == S.Equal) {
        return S.False;
      }
      if (head == S.Unequal) {
        return S.True;
      }
      boolean lower = head == S.Greater || head == S.GreaterEqual;
      return F.binaryAST2(lower ? S.GreaterEqual : S.LessEqual, lhs,
          lower ? fraction.ceilFraction() : fraction.floorFraction());
    }
    IInteger[] denominator = new IInteger[] {F.C1};
    relation.forAllLeaves(x -> {
      if (x.isFraction()) {
        denominator[0] = denominator[0].lcm(((IFraction) x).denominator());
      }
      return true;
    }, 1);
    EvalEngine engine = EvalEngine.get();
    return F.binaryAST2(head, engine.evaluate(F.Expand(F.Times(denominator[0], lhs))),
        engine.evaluate(F.Expand(F.Times(denominator[0], rhs))));
  }

  private static boolean isRelation(IExpr expr) {
    return expr.isEqual() || expr.isAST(S.Unequal) || expr.isAST(S.Greater)
        || expr.isAST(S.GreaterEqual) || expr.isAST(S.Less) || expr.isAST(S.LessEqual);
  }

  private static ReExpression relationalIntegerExpression(Model net, IAST temp,
      Map<ISymbol, IntVar> map, boolean narrowDomains) {
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
        if (narrowDomains && lhs instanceof IntVar && temp.arg2().isInteger()) {
          IntVar lhsVar = (IntVar) lhs;
          try {
            int lowerBound = temp.arg2().toIntDefault();
            if (F.isPresent(lowerBound)) {
              lhsVar.updateLowerBound(lowerBound + 1, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (narrowDomains && rhs instanceof IntVar && temp.arg1().isInteger()) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int upperBound = temp.arg1().toIntDefault();
            if (F.isPresent(upperBound)) {
              rhsVar.updateUpperBound(upperBound - 1, rhsVar);
            }
          } catch (ContradictionException e) {
          }
        }
        return lhs.gt(rhs);
      } else if (temp.isAST(S.GreaterEqual, 3)) {
        if (narrowDomains && lhs instanceof IntVar && temp.arg2().isInteger()) {
          IntVar lhsVar = (IntVar) lhs;
          try {
            int lowerBound = temp.arg2().toIntDefault();
            if (F.isPresent(lowerBound)) {
              lhsVar.updateLowerBound(lowerBound, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (narrowDomains && rhs instanceof IntVar && temp.arg1().isInteger()) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int upperBound = temp.arg1().toIntDefault();
            if (F.isPresent(upperBound)) {
              rhsVar.updateUpperBound(upperBound, rhsVar);
            }
          } catch (ContradictionException e) {
          }
        }
        return lhs.ge(rhs);
      } else if (temp.isAST(S.LessEqual, 3)) {
        if (narrowDomains && lhs instanceof IntVar && temp.arg2().isInteger()) {
          IntVar lhsVar = (IntVar) lhs;
          try {
            int upperBound = temp.arg2().toIntDefault();
            if (F.isPresent(upperBound)) {
              lhsVar.updateUpperBound(upperBound, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (narrowDomains && rhs instanceof IntVar && temp.arg1().isInteger()) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int lowerBound = temp.arg1().toIntDefault();
            if (F.isPresent(lowerBound)) {
              rhsVar.updateLowerBound(lowerBound, rhsVar);
            }
          } catch (ContradictionException e) {
          }
        }
        return lhs.le(rhs);
      } else if (temp.isAST(S.Less, 3)) {
        if (narrowDomains && lhs instanceof IntVar && temp.arg2().isInteger()) {
          IntVar lhsVar = (IntVar) lhs;
          try {
            int upperBound = temp.arg2().toIntDefault();
            if (F.isPresent(upperBound)) {
              lhsVar.updateUpperBound(upperBound - 1, lhsVar);
            }
          } catch (ContradictionException e) {
          }
        } else if (narrowDomains && rhs instanceof IntVar && temp.arg1().isInteger()) {
          IntVar rhsVar = (IntVar) rhs;
          try {
            int lowerBound = temp.arg1().toIntDefault();
            if (F.isPresent(lowerBound)) {
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
   * The condition under which the truncated remainder <code>r</code> of a division by
   * <code>b</code> differs from the floored remainder: <code>r</code> is non-zero and its sign
   * differs from the sign of <code>b</code>.
   */
  private static ReExpression floorCorrection(ArExpression r, ArExpression b) {
    return r.lt(0).and(b.gt(0)).or(r.gt(0).and(b.lt(0)));
  }

  /**
   * An <code>int</code> variable for a value between <code>lb</code> and <code>ub</code>, restricted
   * to choco's safe range {@link IntVar#MIN_INT_BOUND}..{@link IntVar#MAX_INT_BOUND}.
   *
   * <p>
   * choco refuses a variable whose bounds reach <code>Integer.MIN_VALUE</code> or
   * <code>Integer.MAX_VALUE</code>, which is where its own bounds for <code>x^3</code> or
   * <code>x*y*z</code> over the default search box saturate. Restricting the range instead limits
   * the search to the solutions whose intermediate values fit, like the search box itself does. The
   * model is marked with {@link #RESTRICTED_RANGE}, because an empty result doesn't prove the
   * absence of a solution then.
   */
  private static IntVar boundedIntVar(Model net, String prefix, double lb, double ub) {
    if (lb < IntVar.MIN_INT_BOUND || ub > IntVar.MAX_INT_BOUND) {
      net.addHook(RESTRICTED_RANGE, Boolean.TRUE);
    }
    int min = (int) Math.max(lb, IntVar.MIN_INT_BOUND);
    int max = (int) Math.min(ub, IntVar.MAX_INT_BOUND);
    return net.intVar(net.generateName(prefix), min, max);
  }

  /**
   * Test if <code>expr</code> compares a symbol with an integer, like <code>x &gt;= -5</code>.
   */
  private static boolean isVariableBound(IExpr expr) {
    if (expr.isAST2() && (expr.isAST(S.Greater) || expr.isAST(S.GreaterEqual)
        || expr.isAST(S.Less) || expr.isAST(S.LessEqual))) {
      return (expr.first().isSymbol() && expr.second().isInteger())
          || (expr.first().isInteger() && expr.second().isSymbol());
    }
    return false;
  }

  /**
   * <code>a * b</code>; with a variable of restricted range if choco's own bounds of the product
   * don't fit into an <code>int</code>.
   */
  private static ArExpression multiply(Model net, ArExpression a, ArExpression b) {
    IntVar x = a.intVar();
    IntVar y = b.intVar();
    double p1 = (double) x.getLB() * y.getLB();
    double p2 = (double) x.getLB() * y.getUB();
    double p3 = (double) x.getUB() * y.getLB();
    double p4 = (double) x.getUB() * y.getUB();
    double lb = Math.min(Math.min(p1, p2), Math.min(p3, p4));
    double ub = Math.max(Math.max(p1, p2), Math.max(p3, p4));
    if (lb > Integer.MIN_VALUE && ub < Integer.MAX_VALUE && ub - lb < Integer.MAX_VALUE) {
      return x.mul(y);
    }
    IntVar product = boundedIntVar(net, "mul_", lb, ub);
    net.times(x, y, product).post();
    return product;
  }

  /**
   * <code>base ^ exponent</code> for an exponent <code>&gt;= 3</code>. choco's own
   * {@link ArExpression#pow(int)} computes the result bounds with a saturating cast and then
   * refuses them for the default search box.
   */
  private static ArExpression power(Model net, IntVar base, int exponent) {
    double lbPow = Math.pow(base.getLB(), exponent);
    double ubPow = Math.pow(base.getUB(), exponent);
    double lb = Math.min(lbPow, ubPow);
    double ub = Math.max(lbPow, ubPow);
    if ((exponent & 1) == 0 && base.getLB() <= 0 && base.getUB() >= 0) {
      lb = 0;
    }
    IntVar result = boundedIntVar(net, "pow_", lb, ub);
    net.pow(base, exponent, result).post();
    return result;
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
      int value = expr.toIntDefault();
      if (F.isNotPresent(value)) {
        throw new ArgumentTypeException(
            expr.toString() + " does not fit into an int variable for Solve(..., Integers)");
      }
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
        if (ast.isAST2() && ast.arg1().isMinusOne()) {
          return integerExpression(net, ast.arg2(), map).neg();
        }
        ArExpression result = integerExpression(net, ast.arg1(), map);
        for (int i = 2; i < ast.size(); i++) {
          result = multiply(net, result, integerExpression(net, ast.get(i), map));
        }
        return result;
      } else if (ast.isPower()) {
        IExpr exponent = ast.exponent();
        if (exponent.isInteger()) {
          int value = exponent.toIntDefault();
          if (value > 0) {
            IExpr base = ast.base();
            ArExpression result = integerExpression(net, base, map);
            if (value == 1) {
              return result;
            }
            if (value == 2) {
              return result.sqr();
            }
            return power(net, result.intVar(), value);
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
        ArExpression a = integerExpression(net, ast.arg1(), map);
        ArExpression b = integerExpression(net, ast.arg2(), map);
        // choco's mod truncates (sign of the dividend); Mod takes the sign of the divisor
        ArExpression r = a.mod(b);
        return floorCorrection(r, b).ift(r.add(b), r);
      } else if (ast.isAST(S.Quotient, 3)) {
        ArExpression a = integerExpression(net, ast.arg1(), map);
        ArExpression b = integerExpression(net, ast.arg2(), map);
        // choco's div truncates towards zero; Quotient rounds towards -Infinity
        ArExpression q = a.div(b);
        return floorCorrection(a.mod(b), b).ift(q.sub(1), q);
      } else if (ast.isAbs()) {
        return integerExpression(net, ast.arg1(), map).abs();
      } else if (ast.isAST(S.Sign, 2)) {
        ArExpression a = integerExpression(net, ast.arg1(), map);
        return a.gt(0).ift(1, a.lt(0).ift(-1, 0));
      } else if (ast.isAST1()) {
        IExpr head = ast.head();
        if (head instanceof IBuiltInSymbol) {
          Object evaluator = ((IBuiltInSymbol) head).getEvaluator();

          // Check for the standard IntUnaryOperator interface
          if (evaluator instanceof java.util.function.IntUnaryOperator) {
            java.util.function.IntUnaryOperator intFunction =
                (java.util.function.IntUnaryOperator) evaluator;

            ArExpression argExpr = integerExpression(net, ast.arg1(), map);
            IntVar x = argExpr.intVar();
            IntVar y =
                net.intVar(head.toString() + "_" + x.getName(), CHOCO_MIN_VALUE, CHOCO_MAX_VALUE);

            // Post our custom lazy-caching propagator
            net.post(new org.chocosolver.solver.constraints.Constraint("CachedFunctionConstraint",
                new CachedFunctionPropagator(x, y, intFunction)));

            return y;
          }
        }
      } else if (ast.isAST2()) {
        IExpr head = ast.head();
        if (head instanceof IBuiltInSymbol) {
          Object evaluator = ((IBuiltInSymbol) head).getEvaluator();

          // Check for the standard IntBinaryOperator interface
          if (evaluator instanceof java.util.function.IntBinaryOperator) {
            java.util.function.IntBinaryOperator intFunction =
                (java.util.function.IntBinaryOperator) evaluator;

            ArExpression argExpr1 = integerExpression(net, ast.arg1(), map);
            ArExpression argExpr2 = integerExpression(net, ast.arg2(), map);

            IntVar x1 = argExpr1.intVar();
            IntVar x2 = argExpr2.intVar();
            IntVar y = net.intVar(head.toString() + "_" + x1.getName() + "_" + x2.getName(),
                CHOCO_MIN_VALUE, CHOCO_MAX_VALUE);

            // Post the binary lazy-caching propagator
            net.post(
                new org.chocosolver.solver.constraints.Constraint("CachedBinaryFunctionConstraint",
                    new CachedBinaryFunctionPropagator(x1, x2, y, intFunction)));

            return y;
          }
        }
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
   * @param domain {@link S#Integers} or {@link S#Primes}
   * @param engine
   * @return a list of rules with the integer solutions; or if no solution exists return
   *         {@link F#NIL}
   */
  public static IAST integerSolve(final IAST list, final IAST equationVariables,
      final IAST userDefinedVariables, final int maximumNumberOfResults, ISymbol domain,
      final EvalEngine engine) {
    TreeMap<ISymbol, IntVar> map = new TreeMap<ISymbol, IntVar>();
    Model model = expr2IntegerSolver(list, equationVariables, map, domain);
    if (model == null) {
      return F.NIL;
    }
    // the search doesn't notice an interrupt or an abort request on its own
    final boolean[] stopped = new boolean[] {false};
    Solver solver = model.getSolver();
    solver.addStopCriterion(() -> {
      if (Thread.currentThread().isInterrupted() || engine.isStopRequested()) {
        stopped[0] = true;
      }
      return stopped[0];
    });
    List<Solution> res = solver.findAllSolutions(new SolutionCounter(model,
        maximumNumberOfResults < 0 ? Short.MAX_VALUE : maximumNumberOfResults));
    if (stopped[0]) {
      if (Thread.currentThread().isInterrupted()) {
        throw TimeoutException.TIMED_OUT;
      }
      // an incomplete enumeration is no answer
      return F.NIL;
    }
    if (res.size() == 0) {
      // a solution may lie outside a restricted intermediate range
      return model.getHook(RESTRICTED_RANGE) != null ? F.NIL : F.CEmptyList;
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
        // Emit the rules in the order in which the user listed the variables. Iterating the
        // TreeMap instead would order them alphabetically, so solving for {n, m} would answer
        // {m->.., n->..}.
        for (int j = 1; j < userDefinedVariables.size(); j++) {
          IExpr userVariable = userDefinedVariables.get(j);
          if (userVariable.isSymbol()) {
            IntVar intVar = map.get(userVariable);
            if (intVar != null) {
              appendIntegerRule(temp, (ISymbol) userVariable, solution.getIntVal(intVar),
                  listOfZZVariables);
            }
          }
        }
        // any remaining solved variables the user didn't list explicitly
        for (Entry<ISymbol, IntVar> entry : set) {
          if (userDefinedVariables.indexOf(entry.getKey()) <= 0) {
            appendIntegerRule(temp, entry.getKey(), solution.getIntVal(entry.getValue()),
                listOfZZVariables);
          }
        }
        result.append(temp);
      }
    }

    return result;
  }

  /**
   * Append the rule <code>variable -> value</code> to <code>result</code>, wrapping the value in a
   * {@link S#ConditionalExpression} if there are unsolved variables which only have to be integers.
   */
  private static void appendIntegerRule(IASTAppendable result, ISymbol variable, int value,
      IExpr listOfZZVariables) {
    if (listOfZZVariables.isPresent()) {
      result.append(F.Rule(variable, F.ConditionalExpression(F.ZZ(value), listOfZZVariables)));
    } else {
      result.append(F.Rule(variable, F.ZZ(value)));
    }
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
      double value = expr.evalfNaN();
      if (!Double.isNaN(value)) {
        return net.realVar(value);
      }
      // fall through
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

  private static CReExpression relationalExpression(Model net, IExpr temp,
      Map<ISymbol, RealVar> map) {
    if (!temp.isAST()) {
      throw new ArgumentTypeException(
          temp.toString() + " is no relational expression found for Solve(..., Integers)");
    }
    CArExpression lhs;
    CArExpression rhs;
    if (temp.isAST2()) {
      lhs = realExpression(net, temp.first(), map);
      rhs = realExpression(net, temp.second(), map);
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
}
