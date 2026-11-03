package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The real roots of an exponential polynomial <code>Sum(c*x^k*E^(a*x))</code>, which
 * <code>Solve</code> finds over the reals: <code>3^x</code> is <code>E^(Log(3)*x)</code>. A root
 * which is a small rational number is given as itself, every other one as
 * <code>Root({f, x0})</code>.
 *
 * <p>
 * Multiplying <code>f</code> by <code>E^(-a*x)</code> for one of its rates <code>a</code> and
 * differentiating lowers the degree of that rate's polynomial or removes it, without changing the
 * rest of the shape. So the critical points are found recursively, and between two neighbouring
 * critical points <code>f*E^(-a*x)</code> is monotonic and has at most one root.
 */
public final class ExponentialPolynomialRoots {

  /** The largest number of terms and of derivative steps of a polynomial which is searched. */
  private static final int MAX_ORDER = 12;

  /** The largest numerator and denominator of a root which is tested for being rational. */
  private static final int MAX_DENOMINATOR = 6;

  private static final double ZERO_TOLERANCE = 1.0e-13;

  private static final int BISECTIONS = 400;

  /** The term <code>c*x^k*E^(a*x)</code>. */
  private static final class Term {
    final double c;
    final int k;
    final double a;

    Term(double c, int k, double a) {
      this.c = c;
      this.k = k;
      this.a = a;
    }

    /** The logarithm of the absolute value at <code>x</code>; infinite below for a zero. */
    double logAbs(double x) {
      return Math.log(Math.abs(c)) + (k == 0 ? 0.0 : k * Math.log(Math.abs(x))) + a * x;
    }

    double sign(double x) {
      return Math.signum(c) * (k % 2 == 1 && x < 0.0 ? -1.0 : 1.0);
    }
  }

  private ExponentialPolynomialRoots() {}

  /** <code>f(x)</code> divided by its largest term, which has no overflow. */
  private static double scaledValue(Term[] terms, double x) {
    double max = Double.NEGATIVE_INFINITY;
    for (Term t : terms) {
      max = Math.max(max, t.logAbs(x));
    }
    if (max == Double.NEGATIVE_INFINITY) {
      return 0.0;
    }
    double sum = 0.0;
    for (Term t : terms) {
      sum += t.sign(x) * Math.exp(t.logAbs(x) - max);
    }
    return sum;
  }

  /** Add a term to <code>terms</code>, collecting it with one of the same powers. */
  private static void addTerm(List<Term> terms, double c, int k, double a) {
    for (int i = 0; i < terms.size(); i++) {
      Term t = terms.get(i);
      if (t.k == k && t.a == a) {
        terms.remove(i);
        if (t.c + c != 0.0) {
          terms.add(i, new Term(t.c + c, k, a));
        }
        return;
      }
    }
    terms.add(new Term(c, k, a));
  }

  /** The derivative of <code>f*E^(-rate*x)</code> divided by <code>E^(-rate*x)</code>. */
  private static Term[] nextDerivative(Term[] terms, double rate) {
    List<Term> result = new ArrayList<Term>(terms.length + 2);
    for (Term t : terms) {
      if (t.k > 0) {
        addTerm(result, t.c * t.k, t.k - 1, t.a);
      }
      if (t.a != rate) {
        addTerm(result, t.c * (t.a - rate), t.k, t.a);
      }
    }
    return result.toArray(new Term[0]);
  }

  /** A bound which the absolute values of all roots are below, or <code>NaN</code>. */
  private static double rootBound(Term[] terms) {
    for (double bound = 1.0; bound <= 1.0e6; bound *= 2.0) {
      if (hasDominantTerm(terms, bound) && hasDominantTerm(terms, -bound)) {
        return bound;
      }
    }
    return Double.NaN;
  }

  /**
   * Whether the term which wins for large <code>|y|</code> on the side of <code>x</code> is larger
   * than all the others together at <code>x</code>, and each of its ratios to the others only grows
   * from there on, so that no root lies beyond <code>x</code>.
   */
  private static boolean hasDominantTerm(Term[] terms, double x) {
    double direction = Math.signum(x);
    Term dominant = terms[0];
    for (Term t : terms) {
      double growth = (t.a - dominant.a) * direction;
      if (growth > 0.0 || (growth == 0.0 && t.k > dominant.k)) {
        dominant = t;
      }
    }
    double others = 0.0;
    for (Term t : terms) {
      if (t != dominant) {
        // the slope of the logarithm of t/dominant in |y|, which falls with |y| where it is positive
        if ((t.k - dominant.k) / Math.abs(x) + (t.a - dominant.a) * direction > 0.0) {
          return false;
        }
        others += Math.exp(t.logAbs(x) - dominant.logAbs(x));
      }
    }
    return others < 0.5;
  }

  /**
   * The real roots of the sum of <code>terms</code> in ascending order.
   *
   * @param order the number of derivative steps which remain
   * @return <code>null</code> if the search is given up
   */
  private static List<Double> realRoots(Term[] terms, int order, EvalEngine engine) {
    engine.checkTimeLimit();
    if (terms.length == 0 || order == 0) {
      return null;
    }
    if (terms.length == 1) {
      return terms[0].k > 0 ? new ArrayList<Double>(Collections.singletonList(0.0))
          : new ArrayList<Double>();
    }
    List<Double> critical = realRoots(nextDerivative(terms, terms[0].a), order - 1, engine);
    double bound = rootBound(terms);
    if (critical == null || Double.isNaN(bound)) {
      return null;
    }
    for (double point : critical) {
      bound = Math.max(bound, Math.abs(point) + 1.0);
    }
    List<Double> points = new ArrayList<Double>(critical);
    points.add(0, -bound);
    points.add(bound);
    List<Double> roots = new ArrayList<Double>();
    for (int i = 0; i + 1 < points.size(); i++) {
      double lo = points.get(i);
      double hi = points.get(i + 1);
      double valueLo = scaledValue(terms, lo);
      double valueHi = scaledValue(terms, hi);
      if (Math.abs(valueLo) < ZERO_TOLERANCE) {
        addRoot(roots, lo);
      } else if (Math.abs(valueHi) >= ZERO_TOLERANCE && (valueLo < 0.0) != (valueHi < 0.0)) {
        addRoot(roots, bisect(terms, lo, hi, valueLo < 0.0));
      }
    }
    if (Math.abs(scaledValue(terms, bound)) < ZERO_TOLERANCE) {
      addRoot(roots, bound);
    }
    return roots;
  }

  private static void addRoot(List<Double> roots, double root) {
    if (roots.isEmpty() || Math.abs(roots.get(roots.size() - 1) - root) > 1.0e-10) {
      roots.add(root);
    }
  }

  private static double bisect(Term[] terms, double lo, double hi, boolean loNegative) {
    for (int i = 0; i < BISECTIONS; i++) {
      double mid = lo / 2.0 + hi / 2.0;
      if (mid == lo || mid == hi) {
        break;
      }
      if ((scaledValue(terms, mid) < 0.0) == loNegative) {
        lo = mid;
      } else {
        hi = mid;
      }
    }
    return lo / 2.0 + hi / 2.0;
  }

  /**
   * The terms of the expanded <code>f</code>.
   *
   * @return <code>null</code> if <code>f</code> has another shape or no exponential
   */
  private static Term[] terms(IExpr f, IExpr x, EvalEngine engine) {
    IExpr expanded = engine.evaluate(F.Expand(f));
    IAST sum = expanded.isPlus() ? (IAST) expanded : F.List(expanded);
    List<Term> result = new ArrayList<Term>(sum.argSize());
    boolean exponential = false;
    for (int i = 1; i <= sum.argSize(); i++) {
      IAST product = sum.get(i).isTimes() ? (IAST) sum.get(i) : F.List(sum.get(i));
      double c = 1.0;
      int k = 0;
      double a = 0.0;
      for (IExpr factor : product) {
        if (factor.isFree(x)) {
          c *= factor.evalfNaN();
        } else if (factor.equals(x)) {
          k++;
        } else if (factor.isPower() && factor.base().equals(x)) {
          int exponent = factor.exponent().toIntDefault();
          if (exponent < 1 || exponent > MAX_ORDER) {
            return null;
          }
          k += exponent;
        } else if (factor.isPower() && factor.base().isFree(x)) {
          double base = factor.base().evalfNaN();
          double slope = engine.evalN(F.D(factor.exponent(), x)).evalfNaN();
          double offset = engine.evalN(F.subst(factor.exponent(), x, F.C0)).evalfNaN();
          if (!(base > 0.0) || !engine.evaluate(F.D(factor.exponent(), x)).isFree(x)) {
            return null;
          }
          c *= Math.pow(base, offset);
          a += slope * Math.log(base);
          exponential = true;
        } else {
          return null;
        }
      }
      if (Double.isNaN(c) || Double.isInfinite(c) || Double.isNaN(a) || c == 0.0) {
        return null;
      }
      addTerm(result, c, k, a);
    }
    return exponential ? result.toArray(new Term[0]) : null;
  }

  /** The small rational number which is a root of <code>f</code> near <code>root</code>. */
  private static IExpr rationalRoot(IExpr f, IExpr x, double root, EvalEngine engine) {
    for (int d = 1; d <= MAX_DENOMINATOR; d++) {
      double n = Math.rint(root * d);
      if (Math.abs(n / d - root) <= 1.0e-9 * Math.max(1.0, Math.abs(root))) {
        IExpr candidate = F.QQ((long) n, d);
        if (engine.evalQuiet(F.PossibleZeroQ(F.subst(f, x, candidate))).isTrue()) {
          return engine.evaluate(candidate);
        }
      }
    }
    return F.NIL;
  }

  /**
   * The solutions of <code>f == 0</code> over the reals if <code>f</code> is an exponential
   * polynomial with numeric coefficients.
   *
   * @return the list of the lists with the rule of the variable, or {@link F#NIL}
   */
  public static IExpr solve(IExpr f, IExpr x, EvalEngine engine) {
    if (!x.isSymbol()) {
      return F.NIL;
    }
    Term[] terms = terms(f, x, engine);
    if (terms == null || terms.length > MAX_ORDER) {
      return F.NIL;
    }
    int order = 2;
    for (Term t : terms) {
      order += t.k + 1;
    }
    List<Double> roots = order > MAX_ORDER + 8 ? null : realRoots(terms, order, engine);
    if (roots == null) {
      return F.NIL;
    }
    IExpr function = F.Function(F.subst(f, x, F.Slot1));
    IASTAppendable result = F.ListAlloc(roots.size());
    for (double root : roots) {
      IExpr value = rationalRoot(f, x, root, engine);
      if (value.isNIL()) {
        // the root as the correctly rounded number
        IExpr refined = Root.refineNumerically(function, F.num(root), 30L, engine);
        double start = refined.isNumber() ? refined.evalfNaN() : root;
        value = F.unaryAST1(S.Root, F.list(function, F.num(start)));
      }
      result.append(F.list(F.Rule(x, value)));
    }
    EvalAttributes.sort(result);
    return result;
  }

  /**
   * Each real solution with <code>ProductLog(z)</code> and <code>-1/E &lt; z &lt; 0</code> has a
   * second real solution with the branch <code>ProductLog(-1, z)</code>.
   *
   * @param solutions the list of solutions of <code>f == 0</code> in the variable <code>x</code>
   * @return the solutions with the additional ones
   */
  public static IExpr withLowerProductLogBranch(IExpr solutions, IExpr f, IExpr x,
      EvalEngine engine) {
    if (!solutions.isListOfLists() || solutions.isFree(S.ProductLog)) {
      return solutions;
    }
    IASTAppendable result = F.ListAlloc(solutions.argSize() + 1);
    for (IExpr solution : (IAST) solutions) {
      result.append(solution);
      if (solution.argSize() != 1 || !solution.first().isRuleAST()
          || !solution.first().first().equals(x)) {
        continue;
      }
      IExpr value = solution.first().second();
      IExpr lower = value.replaceAll(p -> {
        if (p.isAST(S.ProductLog, 2)) {
          double z = p.first().evalfNaN();
          if (z < 0.0 && z > -1.0 / Math.E) {
            return F.ProductLog(F.CN1, p.first());
          }
        }
        return F.NIL;
      });
      if (lower.isPresent()) {
        lower = engine.evaluate(lower);
        IAST candidate = F.list(F.Rule(x, lower));
        double residual = engine.evalN(F.subst(f, x, lower)).evalfNaN();
        if (Math.abs(residual) < 1.0e-9 && !((IAST) solutions).contains(candidate)) {
          result.append(candidate);
        }
      }
    }
    return result;
  }
}
