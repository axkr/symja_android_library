package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The integer points of a conjunction of polynomial equations and inequalities whose variables
 * the relations confine to a finite box.
 *
 * <p>
 * Every variable gets an interval by propagation: the term <code>c*x^e*q</code> of a relation
 * <code>p == 0</code> or <code>p &lt;= 0</code> lies in the interval which the other terms leave
 * for it, so <code>x^e</code> and <code>x</code> are bounded; an even power is not negative. The
 * propagation runs over all terms until no interval shrinks. The intervals are computed with
 * machine numbers and widened by a margin before they are rounded to integers, so that they are
 * never too narrow; the points of the box are tested with the exact condition one by one.
 */
public final class PolynomialBox {

  /** The number of points which may be tested. */
  public static final long MAX_POINTS = 100000L;

  private static final int MAX_ROUNDS = 64;

  private static final double MARGIN = 1.0e-7;

  private PolynomialBox() {}

  /** A polynomial <code>sum(coefficient[k] * prod(x_j^exponents[k][j])) rel 0</code>. */
  private static final class Relation {
    final double[] coefficients;
    final int[][] exponents;
    /** <code>true</code> for <code>p == 0</code>, <code>false</code> for <code>p &lt;= 0</code> */
    final boolean equation;

    Relation(double[] coefficients, int[][] exponents, boolean equation) {
      this.coefficients = coefficients;
      this.exponents = exponents;
      this.equation = equation;
    }
  }

  /**
   * The integer points which satisfy <code>condition</code>.
   *
   * @param condition a relation or a conjunction of relations; relations which are no polynomial
   *        relations only test the points
   * @param vars the variables
   * @param domain {@link S#Integers} or {@link S#Primes}
   * @return the list of the points, each a list <code>{x==a, y==b, ...}</code>, or {@link F#NIL}
   *         if the variables are not confined to a small box or a point isn't decided
   */
  public static IAST integerPoints(IExpr condition, IAST vars, ISymbol domain,
      EvalEngine engine) {
    final int n = vars.argSize();
    if (n == 0 || (domain != S.Integers && domain != S.Primes)) {
      return F.NIL;
    }
    try {
      IAST conjuncts = condition.isAnd() ? (IAST) condition : F.And(condition);
      List<Relation> relations = new ArrayList<Relation>();
      for (IExpr conjunct : conjuncts) {
        addRelations(conjunct, vars, relations, engine);
      }
      if (relations.isEmpty()) {
        return F.NIL;
      }
      double[] lower = new double[n];
      double[] upper = new double[n];
      java.util.Arrays.fill(lower, Double.NEGATIVE_INFINITY);
      java.util.Arrays.fill(upper, Double.POSITIVE_INFINITY);
      if (domain == S.Primes) {
        java.util.Arrays.fill(lower, 2.0);
      }
      for (int round = 0; round < MAX_ROUNDS; round++) {
        boolean changed = false;
        for (Relation relation : relations) {
          int result = propagate(relation, lower, upper);
          if (result < 0) {
            // an empty interval: no integer point
            return F.CEmptyList;
          }
          changed |= result > 0;
        }
        if (!changed) {
          break;
        }
      }
      long points = 1;
      long[] from = new long[n];
      long[] to = new long[n];
      for (int j = 0; j < n; j++) {
        if (Double.isInfinite(lower[j]) || Double.isInfinite(upper[j])
            || upper[j] - lower[j] >= MAX_POINTS) {
          return F.NIL;
        }
        from[j] = (long) lower[j];
        to[j] = (long) upper[j];
        points *= to[j] - from[j] + 1;
        if (points > MAX_POINTS) {
          return F.NIL;
        }
      }
      IASTAppendable solutions = F.ListAlloc();
      long[] point = from.clone();
      while (true) {
        IASTAppendable rules = F.ListAlloc(n);
        boolean member = true;
        for (int j = 0; j < n; j++) {
          IInteger value = F.ZZ(point[j]);
          if (domain == S.Primes && !(value.isPositive() && value.isProbablePrime())) {
            member = false;
          }
          rules.append(F.Rule(vars.get(j + 1), value));
        }
        if (member) {
          IExpr value = engine.evalQuiet(F.subst(condition, rules));
          if (value.isTrue()) {
            solutions.append(rules.map(r -> F.Equal(r.first(), r.second())));
          } else if (!value.isFalse()) {
            return F.NIL;
          }
        }
        int j = n - 1;
        while (j >= 0 && point[j] == to[j]) {
          point[j] = from[j];
          j--;
        }
        if (j < 0) {
          break;
        }
        point[j]++;
      }
      return solutions;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * Add the polynomial relation <code>conjunct</code> as <code>p == 0</code> or <code>p &lt;= 0</code>
   * (the strict <code>p &lt; 0</code> with integer coefficients as <code>p+1 &lt;= 0</code>); a
   * chain <code>a &lt; x &lt; b</code> gives one relation for each pair.
   */
  private static void addRelations(IExpr conjunct, IAST vars, List<Relation> relations,
      EvalEngine engine) {
    final boolean less = conjunct.isAST(S.Less) || conjunct.isAST(S.LessEqual);
    final boolean greater = conjunct.isAST(S.Greater) || conjunct.isAST(S.GreaterEqual);
    final boolean equal = conjunct.isAST(S.Equal);
    if (!less && !greater && !equal || conjunct.argSize() < 2) {
      return;
    }
    final boolean strict = conjunct.isAST(S.Less) || conjunct.isAST(S.Greater);
    IAST chain = (IAST) conjunct;
    for (int i = 1; i < chain.argSize(); i++) {
      // p rel 0 with rel one of ==, <=, <
      IExpr p = greater ? F.Subtract(chain.get(i + 1), chain.get(i))
          : F.Subtract(chain.get(i), chain.get(i + 1));
      Relation relation = relation(engine.evaluate(F.Expand(p)), vars, equal, strict);
      if (relation != null) {
        relations.add(relation);
      }
    }
  }

  /** The terms of the expanded polynomial <code>p</code>, or <code>null</code> for another form. */
  private static Relation relation(IExpr p, IAST vars, boolean equation, boolean strict) {
    final int n = vars.argSize();
    IAST terms = p.isPlus() ? (IAST) p : F.Plus(p);
    IExpr[] coefficients = new IExpr[terms.argSize()];
    int[][] exponents = new int[terms.argSize()][n];
    boolean rational = true;
    for (int k = 0; k < terms.argSize(); k++) {
      IExpr term = terms.get(k + 1);
      IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
      IExpr coefficient = F.C1;
      for (IExpr factor : factors) {
        int j = vars.indexOf(factor) - 1;
        if (j >= 0) {
          exponents[k][j]++;
          continue;
        }
        if (factor.isPower() && factor.exponent().isInteger() && factor.exponent().isPositive()) {
          j = vars.indexOf(factor.base()) - 1;
          int e = factor.exponent().toIntDefault();
          if (j >= 0 && e > 0) {
            exponents[k][j] += e;
            continue;
          }
        }
        if (factor.isFree(x -> vars.contains(x), false) && factor.isRealResult()) {
          coefficient = coefficient.isOne() ? factor : F.Times(coefficient, factor);
          continue;
        }
        return null;
      }
      coefficients[k] = coefficient;
      rational &= coefficient.isRational();
    }
    double[] values = new double[coefficients.length];
    if (rational && strict) {
      // over the integers p < 0 with integer coefficients is p + 1 <= 0
      BigInteger scale = BigInteger.ONE;
      for (IExpr c : coefficients) {
        BigInteger d = ((IRational) c).denominator().toBigNumerator();
        scale = scale.divide(scale.gcd(d)).multiply(d);
      }
      boolean constant = false;
      for (int k = 0; k < coefficients.length; k++) {
        IRational c = ((IRational) coefficients[k]).multiply(F.ZZ(scale));
        boolean isConstant = true;
        for (int j = 0; j < n; j++) {
          isConstant &= exponents[k][j] == 0;
        }
        if (isConstant) {
          c = c.add(F.C1);
          constant = true;
        }
        values[k] = c.doubleValue();
      }
      if (!constant) {
        values = java.util.Arrays.copyOf(values, values.length + 1);
        values[values.length - 1] = 1.0;
        exponents = java.util.Arrays.copyOf(exponents, exponents.length + 1);
        exponents[exponents.length - 1] = new int[n];
      }
      return new Relation(values, exponents, false);
    }
    for (int k = 0; k < coefficients.length; k++) {
      values[k] = coefficients[k].evalf();
      if (!Double.isFinite(values[k])) {
        return null;
      }
    }
    return new Relation(values, exponents, equation);
  }

  /**
   * Narrow the intervals of the variables of the terms of <code>relation</code>.
   *
   * @return <code>1</code> if an interval was narrowed, <code>0</code> if not, <code>-1</code> if
   *         an interval is empty
   */
  private static int propagate(Relation relation, double[] lower, double[] upper) {
    final int terms = relation.coefficients.length;
    double[][] ranges = new double[terms][];
    for (int k = 0; k < terms; k++) {
      ranges[k] = termRange(relation, k, -1, lower, upper);
    }
    int result = 0;
    for (int k = 0; k < terms; k++) {
      // the range of the other terms
      double low = 0.0;
      double high = 0.0;
      double magnitude = 0.0;
      for (int i = 0; i < terms; i++) {
        if (i != k) {
          low += ranges[i][0];
          high += ranges[i][1];
          for (double end : ranges[i]) {
            if (Double.isFinite(end)) {
              magnitude = Math.max(magnitude, Math.abs(end));
            }
          }
        }
      }
      if (Double.isNaN(low) || Double.isNaN(high)) {
        continue;
      }
      // the term lies in [-high, -low] for p == 0 and in (-Infinity, -low] for p <= 0
      double termLow = relation.equation ? -high : Double.NEGATIVE_INFINITY;
      double termHigh = -low;
      double slack = MARGIN * (1.0 + magnitude);
      termLow -= slack;
      termHigh += slack;
      int[] exponents = relation.exponents[k];
      for (int j = 0; j < exponents.length; j++) {
        if (exponents[j] == 0) {
          continue;
        }
        double[] q = termRange(relation, k, j, lower, upper);
        if (q[0] <= 0.0 && q[1] >= 0.0) {
          // the term can be 0 whatever x is
          continue;
        }
        double[] u = divide(termLow, termHigh, q[0], q[1]);
        if (u == null) {
          continue;
        }
        int narrowed = narrow(j, exponents[j], u[0], u[1], lower, upper);
        if (narrowed < 0) {
          return -1;
        }
        if (narrowed > 0) {
          result = 1;
          ranges[k] = termRange(relation, k, -1, lower, upper);
        }
      }
    }
    return result;
  }

  /**
   * The range of the term <code>k</code> without the factor <code>x_skip^e</code> (all factors for
   * <code>skip == -1</code>).
   */
  private static double[] termRange(Relation relation, int k, int skip, double[] lower,
      double[] upper) {
    double c = relation.coefficients[k];
    double[] range = {c, c};
    int[] exponents = relation.exponents[k];
    for (int j = 0; j < exponents.length; j++) {
      if (j != skip && exponents[j] > 0) {
        range = multiply(range, power(lower[j], upper[j], exponents[j]));
      }
    }
    return range;
  }

  private static double[] power(double low, double high, int e) {
    double a = Math.pow(low, e);
    double b = Math.pow(high, e);
    if (e % 2 == 1) {
      return new double[] {a, b};
    }
    if (low >= 0.0) {
      return new double[] {a, b};
    }
    if (high <= 0.0) {
      return new double[] {b, a};
    }
    return new double[] {0.0, Math.max(a, b)};
  }

  private static double[] multiply(double[] x, double[] y) {
    double lo = Double.POSITIVE_INFINITY;
    double hi = Double.NEGATIVE_INFINITY;
    for (double a : x) {
      for (double b : y) {
        // 0 times an unbounded end is 0: the end is a limit of finite values
        double p = a == 0.0 || b == 0.0 ? 0.0 : a * b;
        lo = Math.min(lo, p);
        hi = Math.max(hi, p);
      }
    }
    return new double[] {lo, hi};
  }

  /** <code>[low, high] / [qLow, qHigh]</code> for an interval which doesn't contain 0. */
  private static double[] divide(double low, double high, double qLow, double qHigh) {
    double lo = Double.POSITIVE_INFINITY;
    double hi = Double.NEGATIVE_INFINITY;
    for (double a : new double[] {low, high}) {
      for (double b : new double[] {qLow, qHigh}) {
        double p = a / b;
        if (Double.isNaN(p)) {
          return null;
        }
        lo = Math.min(lo, p);
        hi = Math.max(hi, p);
      }
    }
    return new double[] {lo, hi};
  }

  /** Narrow the interval of <code>x_j</code> by <code>x_j^e</code> in <code>[low, high]</code>. */
  private static int narrow(int j, int e, double low, double high, double[] lower,
      double[] upper) {
    double newLower = lower[j];
    double newUpper = upper[j];
    if (e % 2 == 1) {
      newLower = Math.max(newLower, root(low, e, true));
      newUpper = Math.min(newUpper, root(high, e, false));
    } else {
      if (high < 0.0) {
        return -1;
      }
      double r = Math.floor(Math.pow(high, 1.0 / e) * (1.0 + MARGIN) + MARGIN);
      newLower = Math.max(newLower, -r);
      newUpper = Math.min(newUpper, r);
      if (low > 0.0) {
        double s = Math.ceil(Math.pow(low, 1.0 / e) * (1.0 - MARGIN) - MARGIN);
        if (newLower > -s) {
          newLower = Math.max(newLower, s);
        } else if (newUpper < s) {
          newUpper = Math.min(newUpper, -s);
        }
      }
    }
    if (newLower > newUpper) {
      return -1;
    }
    if (newLower > lower[j] || newUpper < upper[j]) {
      lower[j] = newLower;
      upper[j] = newUpper;
      return 1;
    }
    return 0;
  }

  /** The integer next to the real <code>e</code>-th root of <code>v</code>, widened. */
  private static double root(double v, int e, boolean isLower) {
    if (Double.isInfinite(v)) {
      return v;
    }
    double r = Math.signum(v) * Math.pow(Math.abs(v), 1.0 / e);
    double slack = MARGIN * (1.0 + Math.abs(r));
    return isLower ? Math.ceil(r - slack) : Math.floor(r + slack);
  }
}
