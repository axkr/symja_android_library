package org.matheclipse.core.sympy.simplify;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Expansion of the Meijer G-function into generalized hypergeometric functions by Slater's
 * theorem. Ported in part from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/simplify/hyperexpand.py">sympy/simplify/hyperexpand.py</a>
 * (<code>_meijergexpand</code>, <code>reduce_order_meijer</code>).
 *
 * <p>
 * <code>MeijerG({{a1,...,an},{a(n+1),...,ap}}, {{b1,...,bm},{b(m+1),...,bq}}, z, r)</code> is the
 * integral of <code>Gamma(b1+s)...Gamma(bm+s)*Gamma(1-a1-s)...Gamma(1-an-s) /
 * (Gamma(a(n+1)+s)...Gamma(ap+s)*Gamma(1-b(m+1)-s)...Gamma(1-bq-s)) * z^(-s/r)</code>. The sum of
 * its residues at the poles of <code>Gamma(bj+s)</code> is a sum of functions
 * <code>pF(q-1)</code> in <code>z^(1/r)</code>, the sum of the residues at the poles of
 * <code>Gamma(1-aj-s)</code> a sum of functions <code>qF(p-1)</code> in <code>z^(-1/r)</code>.
 * Where two of the parameters <code>bj</code> differ by an integer, their poles coincide and the
 * residues contain logarithms; finitely many of them are summed one by one.
 */
public final class HyperExpand {

  /** Most residues of poles of higher order which are summed one by one. */
  private static final int MAX_RESIDUES = 12;

  /** Largest integer distance of two parameters which are cancelled against each other. */
  private static final int MAX_REDUCTION = 12;

  /** An operator <code>constant + sign*r*z*d/dz</code>, applied to the expansion of the reduced function. */
  private static final class Operator {
    final IExpr constant;
    final int sign;

    Operator(IExpr constant, int sign) {
      this.constant = constant;
      this.sign = sign;
    }
  }

  /** The expansion of one side, with the facts that decide whether it is shown automatically. */
  private static final class Expansion {
    final IExpr value;
    final int terms;
    final boolean multiplePoles;

    Expansion(IExpr value, int terms, boolean multiplePoles) {
      this.value = value;
      this.terms = terms;
      this.multiplePoles = multiplePoles;
    }
  }

  private HyperExpand() {}

  /**
   * The expansion of <code>MeijerG({an, ap}, {bm, bq}, z, r)</code>.
   *
   * <p>
   * With <code>automatic == true</code> only the expansions which are the value of the function
   * itself are returned: those whose poles are all simple, and which are one term or, for
   * <code>p, q &lt;= 2</code>, a sum of functions <code>0F1, 1F1, 2F1</code>. Otherwise also sums of
   * general hypergeometric functions, residues with logarithms, and for <code>p == q</code> a
   * <code>Piecewise</code> of the expansions inside and outside of the unit circle.
   *
   * @param an the parameters <code>a1,...,an</code>
   * @param ap the parameters <code>a(n+1),...,ap</code>
   * @param bm the parameters <code>b1,...,bm</code>
   * @param bq the parameters <code>b(m+1),...,bq</code>
   * @param z the argument
   * @param r the positive scale of the exponent, <code>1</code> for the ordinary G-function
   * @param automatic whether only the value of the function itself is asked for
   * @return the expansion or {@link F#NIL}
   */
  public static IExpr meijerGExpand(IAST an, IAST ap, IAST bm, IAST bq, IExpr z, IExpr r,
      boolean automatic, EvalEngine engine) {
    List<IExpr> nan = toList(an);
    List<IExpr> nap = toList(ap);
    List<IExpr> nbm = toList(bm);
    List<IExpr> nbq = toList(bq);
    if (isUndefined(nan, nbm, engine)) {
      return F.NIL;
    }
    List<Operator> operators = new ArrayList<Operator>();
    if (!reduceOrder(nan, nbq, nbm, nap, automatic, operators, engine)) {
      return F.NIL;
    }
    final int n = nan.size();
    final int p = n + nap.size();
    final int m = nbm.size();
    final int q = m + nbq.size();
    if (m + n == 0) {
      return F.NIL;
    }

    Boolean inZ = null;
    if (p < q) {
      inZ = Boolean.TRUE;
    } else if (p > q) {
      inZ = Boolean.FALSE;
    } else {
      if (z.isNumber()) {
        IExpr inside = engine.evaluate(F.LessEqual(F.Abs(z), F.C1));
        if (inside.isTrue()) {
          inZ = Boolean.TRUE;
        } else if (inside.isFalse()) {
          inZ = Boolean.FALSE;
        }
      }
      if (inZ == null) {
        if (m + n > p) {
          // the two expansions are continuations of each other
          inZ = Boolean.TRUE;
        } else if (automatic) {
          return F.NIL;
        }
      }
    }

    // the expansion is built in a variable of its own, which becomes z at the end
    final ISymbol x = F.Dummy("z");
    IExpr result;
    if (inZ == null) {
      Expansion inside = slater(nan, nap, nbm, nbq, x, r, true, automatic, engine);
      if (inside == null) {
        return F.NIL;
      }
      Expansion outside = slater(nan, nap, nbm, nbq, x, r, false, automatic, engine);
      if (outside == null) {
        return F.NIL;
      }
      // Piecewise holds its arguments: each piece is evaluated in z before
      IExpr insideValue = engine.evaluate(
          F.subs(engine.evaluate(applyOperators(inside.value, operators, x, r, engine)), x, z));
      IExpr outsideValue = engine.evaluate(
          F.subs(engine.evaluate(applyOperators(outside.value, operators, x, r, engine)), x, z));
      result = F.Piecewise(F.list(F.list(insideValue, F.LessEqual(F.Abs(z), F.C1))),
          outsideValue);
    } else {
      Expansion expansion = slater(nan, nap, nbm, nbq, x, r, inZ, automatic, engine);
      if (expansion == null) {
        return F.NIL;
      }
      if (automatic) {
        if (expansion.multiplePoles || expansion.terms == 0) {
          return F.NIL;
        }
        if (expansion.terms > 1 && !isNamed(expansion.value)) {
          // a sum of general hypergeometric functions
          return F.NIL;
        }
      }
      result = applyOperators(expansion.value, operators, x, r, engine);
    }
    result = engine.evaluate(F.subs(engine.evaluate(result), x, z));
    if (result.has(y -> y.isIndeterminate() || y.isComplexInfinity() || y.isDirectedInfinity(),
        true)) {
      return F.NIL;
    }
    return result;
  }

  /**
   * Whether the hypergeometric functions of the expansion are all functions with names of their
   * own: <code>0F1, 1F1, 2F1</code> and <code>0F0, 1F0</code>.
   */
  private static boolean isNamed(IExpr expansion) {
    return expansion.isFree(x -> x.isAST(S.HypergeometricPFQRegularized)
        || (x.isAST(S.HypergeometricPFQ, 4) && x.first().argSize() > 1), true);
  }

  /**
   * Whether the poles of <code>Gamma(1-aj-s)</code> and <code>Gamma(bk+s)</code> coincide for some
   * <code>aj</code> of the first and <code>bk</code> of the third list, which leaves no path for the
   * integral: <code>aj-bk</code> is a positive integer.
   */
  public static boolean isUndefined(IAST an, IAST bm, EvalEngine engine) {
    return isUndefined(toList(an), toList(bm), engine);
  }

  private static boolean isUndefined(List<IExpr> an, List<IExpr> bm, EvalEngine engine) {
    for (IExpr a : an) {
      for (IExpr b : bm) {
        IExpr difference = difference(a, b, engine);
        if (difference.isInteger() && difference.isPositive()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * The difference <code>x-y</code>, expanded: <code>1/2*(-1-n)-1/2*(-1-n)</code> doesn't evaluate
   * to <code>0</code> otherwise.
   */
  public static IExpr difference(IExpr x, IExpr y, EvalEngine engine) {
    IExpr difference = engine.evaluate(F.Subtract(x, y));
    if (difference.isNumber()) {
      return difference;
    }
    return engine.evaluate(F.Expand(difference));
  }

  private static List<IExpr> toList(IAST list) {
    List<IExpr> result = new ArrayList<IExpr>(list.argSize());
    for (int i = 1; i < list.size(); i++) {
      result.add(list.get(i));
    }
    return result;
  }

  /** The integer <code>b-a</code>, or <code>-1</code> if it isn't a non-negative integer. */
  private static int integerDistance(IExpr a, IExpr b, EvalEngine engine) {
    IExpr difference = difference(b, a, engine);
    if (difference.isInteger() && !difference.isNegative()) {
      int distance = difference.toIntDefault();
      return distance >= 0 ? distance : -1;
    }
    return -1;
  }

  /**
   * Cancel the parameters of <code>an</code> against those of <code>bq</code>, and of
   * <code>bm</code> against those of <code>ap</code>, whose gamma functions form a polynomial in
   * <code>s</code>: their quotient becomes an operator in <code>z*d/dz</code>. Equal parameters
   * cancel without one.
   * The reduction is done for the automatic evaluation too: the value is then the operator applied
   * to the expansion of the reduced function.
   *
   * @return <code>false</code> if a distance is too large
   */
  private static boolean reduceOrder(List<IExpr> an, List<IExpr> bq, List<IExpr> bm,
      List<IExpr> ap, boolean automatic, List<Operator> operators, EvalEngine engine) {
    // Gamma(1-a-s)/Gamma(1-b-s) with b = a+N is (1-b-s)*...*(1-b+N-1-s), and -s is r*z*d/dz
    for (int i = 0; i < an.size(); i++) {
      IExpr a = an.get(i);
      for (int j = 0; j < bq.size(); j++) {
        IExpr b = bq.get(j);
        int distance = integerDistance(a, b, engine);
        if (distance >= 0) {
          if (distance > MAX_REDUCTION) {
            return false;
          }
          for (int k = 0; k < distance; k++) {
            operators.add(new Operator(F.Plus(F.C1, F.Negate(b), F.ZZ(k)), 1));
          }
          an.remove(i--);
          bq.remove(j);
          break;
        }
      }
    }
    // Gamma(b+s)/Gamma(a+s) with b = a+N is (a+s)*...*(a+N-1+s), and s is -r*z*d/dz
    for (int i = 0; i < bm.size(); i++) {
      IExpr b = bm.get(i);
      for (int j = 0; j < ap.size(); j++) {
        IExpr a = ap.get(j);
        int distance = integerDistance(a, b, engine);
        if (distance >= 0) {
          if (distance > MAX_REDUCTION) {
            return false;
          }
          for (int k = 0; k < distance; k++) {
            operators.add(new Operator(F.Plus(a, F.ZZ(k)), -1));
          }
          bm.remove(i--);
          ap.remove(j);
          break;
        }
      }
    }
    return true;
  }

  /**
   * Apply <code>constant + sign*r*x*d/dx</code> for each operator; the expanded result, or the
   * result over a common denominator if that is smaller.
   */
  private static IExpr applyOperators(IExpr expr, List<Operator> operators, ISymbol x, IExpr r,
      EvalEngine engine) {
    if (operators.isEmpty()) {
      return expr;
    }
    IExpr result = expr;
    for (Operator operator : operators) {
      result = engine.evaluate(F.Expand(F.Plus(F.Times(operator.constant, result),
          F.Times(F.ZZ(operator.sign), r, x, F.D(result, x)))));
    }
    IExpr together = engine.evaluate(F.Together(result));
    return together.leafCount() < result.leafCount() ? together : result;
  }

  /**
   * Slater's theorem for the residues at the poles of <code>Gamma(bj+s)</code> (series in
   * <code>z</code>), or at the poles of <code>Gamma(1-aj-s)</code> (series in <code>1/z</code>)
   * through the parameters of <code>G(1/z)</code>.
   *
   * @return <code>null</code> if a group of poles of higher order has no finite expansion
   */
  private static Expansion slater(List<IExpr> an, List<IExpr> ap, List<IExpr> bm, List<IExpr> bq,
      IExpr z, IExpr r, boolean inZ, boolean automatic, EvalEngine engine) {
    if (!inZ) {
      // G(z | an, ap; bm, bq) = G(1/z | 1-bm, 1-bq; 1-an, 1-ap)
      List<IExpr> newAn = oneMinus(bm, engine);
      List<IExpr> newAp = oneMinus(bq, engine);
      List<IExpr> newBm = oneMinus(an, engine);
      List<IExpr> newBq = oneMinus(ap, engine);
      an = newAn;
      ap = newAp;
      bm = newBm;
      bq = newBq;
    }
    // log of the variable of the series, which is z^(1/r) or z^(-1/r)
    final IExpr logVariable = inZ ? F.Divide(F.Log(z), r) : F.Negate(F.Divide(F.Log(z), r));
    final IExpr variable = inZ ? F.Power(z, F.Power(r, F.CN1)) : F.Power(z, F.Negate(F.Power(r, F.CN1)));

    List<List<IExpr>> buckets = buckets(bm, engine);
    boolean multiplePoles = false;
    for (List<IExpr> bucket : buckets) {
      if (bucket.size() > 1) {
        multiplePoles = true;
      }
    }
    if (multiplePoles && automatic) {
      return new Expansion(F.NIL, 0, true);
    }
    IASTAppendable sum = F.PlusAlloc(bm.size());
    int terms = 0;
    for (List<IExpr> bucket : buckets) {
      if (bucket.size() == 1) {
        sum.append(simplePole(bucket.get(0), an, ap, bm, bq, z, r, inZ, variable));
        terms++;
      } else {
        IExpr poles = multiplePole(bucket, an, ap, bm, bq, z, r, inZ, variable, logVariable, engine);
        if (poles.isNIL()) {
          return null;
        }
        sum.append(poles);
        terms++;
      }
    }
    return new Expansion(sum.oneIdentity0(), terms, multiplePoles);
  }

  private static List<IExpr> oneMinus(List<IExpr> list, EvalEngine engine) {
    List<IExpr> result = new ArrayList<IExpr>(list.size());
    for (IExpr x : list) {
      result.add(engine.evaluate(F.Subtract(F.C1, x)));
    }
    return result;
  }

  /** A combination of parameters, expanded so that equal parameters cancel. */
  private static IExpr expand(IExpr parameters) {
    EvalEngine engine = EvalEngine.get();
    IExpr result = engine.evaluate(parameters);
    return result.isNumber() ? result : engine.evaluate(F.Expand(result));
  }

  /** The power <code>variable^c</code>, as <code>z^(c/r)</code> or <code>z^(-c/r)</code>. */
  private static IExpr power(IExpr z, IExpr r, boolean inZ, IExpr c) {
    IExpr exponent = r.isOne() ? c : F.Divide(c, r);
    return F.Power(z, inZ ? exponent : F.Negate(exponent));
  }

  /**
   * The parameters grouped by integer differences, each group in ascending order.
   */
  private static List<List<IExpr>> buckets(List<IExpr> parameters, EvalEngine engine) {
    List<List<IExpr>> buckets = new ArrayList<List<IExpr>>();
    for (IExpr x : parameters) {
      List<IExpr> bucket = null;
      for (List<IExpr> b : buckets) {
        if (isIntegerDifference(x, b.get(0), engine)) {
          bucket = b;
          break;
        }
      }
      if (bucket == null) {
        bucket = new ArrayList<IExpr>();
        buckets.add(bucket);
      }
      bucket.add(x);
    }
    for (List<IExpr> bucket : buckets) {
      final IExpr first = bucket.get(0);
      bucket.sort(Comparator.comparingInt(x -> offset(x, first, engine)));
    }
    return buckets;
  }

  private static boolean isIntegerDifference(IExpr x, IExpr y, EvalEngine engine) {
    IExpr difference = difference(x, y, engine);
    return difference.isInteger()
        || (difference.isInexactNumber() && difference.isReal() && difference.isNumIntValue());
  }

  private static int offset(IExpr x, IExpr first, EvalEngine engine) {
    IExpr difference = difference(x, first, engine);
    if (difference.isInteger()) {
      return difference.toIntDefault();
    }
    return (int) Math.round(difference.evalf());
  }

  /**
   * The hypergeometric function with the lower parameters regularized. An upper parameter which is
   * equal to a lower one cancels, and leaves the factor <code>1/Gamma(lower)</code>.
   */
  private static IExpr regularized(List<IExpr> upper, List<IExpr> lower, IExpr argument,
      IExpr root, EvalEngine engine) {
    upper = new ArrayList<IExpr>(upper);
    lower = new ArrayList<IExpr>(lower);
    IASTAppendable factor = F.TimesAlloc(2);
    for (int i = 0; i < upper.size(); i++) {
      for (int j = 0; j < lower.size(); j++) {
        IExpr b = lower.get(j);
        if (difference(upper.get(i), b, engine).isZero()) {
          IExpr evaluated = engine.evaluate(b);
          if (evaluated.isInteger() && !evaluated.isPositive()) {
            continue;
          }
          factor.append(F.Power(F.Gamma(b), F.CN1));
          upper.remove(i--);
          lower.remove(j);
          break;
        }
      }
    }
    if (factor.argSize() > 0) {
      factor.append(regularizedFunction(upper, lower, argument, root));
      return factor;
    }
    return regularizedFunction(upper, lower, argument, root);
  }

  /**
   * @param argument <code>w</code> or <code>-w</code>, with <code>w</code> the variable of the
   *        series
   * @param root the square root of <code>w</code> on the Riemann surface of the logarithm:
   *        <code>z^(1/(2*r))</code> for <code>w = z^(1/r)</code>
   */
  private static IExpr regularizedFunction(List<IExpr> upper, List<IExpr> lower,
      IExpr argument, IExpr root) {
    if (lower.isEmpty()) {
      return F.HypergeometricPFQ(F.List(upper.toArray(new IExpr[0])), F.CEmptyList, argument);
    }
    if (upper.isEmpty() && lower.size() == 1) {
      // s^(1-b)*BesselI(b-1,2*s) for the argument w = s^2, s^(1-b)*BesselJ(b-1,2*s) for -w: both
      // are entire in w, for each square root s of w
      IExpr b = lower.get(0);
      IExpr power = F.Power(root, F.Subtract(F.C1, b));
      IExpr bessel = argument.isNegativeSigned() ? F.BesselJ(F.Plus(F.CN1, b), F.Times(F.C2, root))
          : F.BesselI(F.Plus(F.CN1, b), F.Times(F.C2, root));
      return F.Times(power, bessel);
    }
    if (upper.size() == 1 && lower.size() == 1) {
      return F.Hypergeometric1F1Regularized(upper.get(0), lower.get(0), argument);
    }
    if (upper.size() == 2 && lower.size() == 1) {
      return F.Hypergeometric2F1Regularized(upper.get(0), upper.get(1), lower.get(0), argument);
    }
    return F.HypergeometricPFQRegularized(F.List(upper.toArray(new IExpr[0])),
        F.List(lower.toArray(new IExpr[0])), argument);
  }

  /**
   * The residues at the poles <code>s = -bh-k</code> of <code>Gamma(bh+s)</code>, which are simple:
   * <code>z^(bh/r) * Product(Pi*Csc(Pi*(bj-bh))) * Product(Gamma(1-aj+bh), j<=n) /
   * Product(Gamma(aj-bh), j>n) * Regularized pFq({1-aj+bh}, {1+bh-bj, j!=h}, (-1)^(p-m-n)*z^(1/r))</code>.
   */
  private static IExpr simplePole(IExpr bh, List<IExpr> an, List<IExpr> ap, List<IExpr> bm,
      List<IExpr> bq, IExpr z, IExpr r, boolean inZ, IExpr variable) {
    IASTAppendable factor = F.TimesAlloc(bm.size() + an.size() + ap.size() + 2);
    factor.append(power(z, r, inZ, bh));
    boolean skipped = false;
    for (IExpr bj : bm) {
      if (!skipped && bj == bh) {
        skipped = true;
        continue;
      }
      factor.append(F.Times(S.Pi, F.Csc(F.Times(S.Pi, expand(F.Subtract(bj, bh))))));
    }
    for (IExpr aj : an) {
      factor.append(F.Gamma(expand(F.Plus(F.C1, F.Negate(aj), bh))));
    }
    for (IExpr aj : ap) {
      factor.append(F.Power(F.Gamma(expand(F.Subtract(aj, bh))), F.CN1));
    }
    List<IExpr> upper = new ArrayList<IExpr>();
    for (IExpr aj : an) {
      upper.add(expand(F.Plus(F.C1, F.Negate(aj), bh)));
    }
    for (IExpr aj : ap) {
      upper.add(expand(F.Plus(F.C1, F.Negate(aj), bh)));
    }
    List<IExpr> lower = new ArrayList<IExpr>();
    skipped = false;
    for (IExpr bj : bm) {
      if (!skipped && bj == bh) {
        skipped = true;
        continue;
      }
      lower.add(expand(F.Plus(F.C1, bh, F.Negate(bj))));
    }
    for (IExpr bj : bq) {
      lower.add(expand(F.Plus(F.C1, bh, F.Negate(bj))));
    }
    IExpr argument = (ap.size() - bm.size()) % 2 == 0 ? variable : F.Negate(variable);
    // the square root of the variable z^(1/r) on the Riemann surface of the logarithm
    factor.append(regularized(upper, lower, argument, power(z, r, inZ, F.C1D2), EvalEngine.get()));
    return factor;
  }

  /**
   * The residues at the poles of a group <code>b, b+k1, ..., b+ku</code> of the parameters
   * <code>bm</code>, which are of higher order where the poles coincide. As many parameters of
   * <code>ap</code> in the same group end the poles of higher order; the residues at those are
   * summed one by one, the remaining simple poles give one hypergeometric function.
   */
  private static IExpr multiplePole(List<IExpr> bucket, List<IExpr> an, List<IExpr> ap,
      List<IExpr> bm, List<IExpr> bq, IExpr z, IExpr r, boolean inZ, IExpr variable,
      IExpr logVariable, EvalEngine engine) {
    final IExpr b = bucket.get(0);
    List<IExpr> apBucket = new ArrayList<IExpr>();
    for (IExpr a : ap) {
      if (isIntegerDifference(a, b, engine)) {
        apBucket.add(a);
      }
    }
    apBucket.sort(Comparator.comparingInt(x -> offset(x, b, engine)));
    final int u = bucket.size() - 1;
    if (apBucket.size() < u) {
      // infinitely many poles of higher order
      return F.NIL;
    }
    int[] ki = new int[u];
    for (int i = 0; i < u; i++) {
      ki[i] = offset(bucket.get(i + 1), b, engine);
    }
    int liSize = Math.min(u + 1, apBucket.size());
    int[] li = new int[liSize];
    for (int i = 0; i < liSize; i++) {
      li[i] = offset(apBucket.get(i), b, engine);
    }
    if (liSize == 0) {
      return F.NIL;
    }
    final int lu = li[liSize - 1];
    if (lu < 0 || lu > MAX_RESIDUES) {
      return F.NIL;
    }
    List<IExpr> bo = new ArrayList<IExpr>(bm);
    for (IExpr x : bucket) {
      bo.remove(x);
    }
    List<IExpr> ao = new ArrayList<IExpr>(ap);
    for (int i = 0; i < u; i++) {
      ao.remove(apBucket.get(i));
    }

    // the integrand in the orientation of the residues at s = b+k:
    // E^(s*log) * Gamma(bm-s) * Gamma(1-an+s) / (Gamma(1-bq+s) * Gamma(ap-s))
    List<IExpr[]> numerator = new ArrayList<IExpr[]>();
    List<IExpr[]> denominator = new ArrayList<IExpr[]>();
    for (IExpr x : bm) {
      numerator.add(new IExpr[] {x, F.CN1});
    }
    for (IExpr x : an) {
      numerator.add(new IExpr[] {F.Subtract(F.C1, x), F.C1});
    }
    for (IExpr x : bq) {
      denominator.add(new IExpr[] {F.Subtract(F.C1, x), F.C1});
    }
    for (IExpr x : ap) {
      denominator.add(new IExpr[] {x, F.CN1});
    }
    ISymbol log = F.Dummy("log");
    IASTAppendable result = F.PlusAlloc(lu + 1);
    for (int k = 0; k < lu; k++) {
      IExpr residue =
          residue(numerator, denominator, engine.evaluate(F.Plus(b, F.ZZ(k))), log, engine);
      if (residue.isNIL()) {
        return F.NIL;
      }
      result.append(F.Negate(engine.evaluate(F.Expand(residue))));
    }

    // the remaining simple poles from s = b+lu on
    IExpr au = F.Plus(b, F.ZZ(lu));
    List<IExpr> upper = new ArrayList<IExpr>();
    for (IExpr x : an) {
      upper.add(F.Plus(F.C1, au, F.Negate(x)));
    }
    for (IExpr x : ap) {
      upper.add(F.Plus(F.C1, au, F.Negate(x)));
    }
    upper.add(F.C1);
    List<IExpr> lower = new ArrayList<IExpr>();
    for (IExpr x : bm) {
      lower.add(F.Plus(F.C1, au, F.Negate(x)));
    }
    for (IExpr x : bq) {
      lower.add(F.Plus(F.C1, au, F.Negate(x)));
    }
    IASTAppendable c = F.TimesAlloc(8);
    c.append(F.Divide(F.Power(F.CN1, F.ZZ(lu)), F.Factorial(F.ZZ(lu))));
    for (int i = 0; i < Math.min(u, liSize); i++) {
      int di = li[i] - ki[i];
      c.append(F.Divide(F.Power(F.CN1, F.ZZ(di)), F.Pochhammer(F.ZZ(lu - li[i] + 1), F.ZZ(di))));
    }
    for (IExpr x : an) {
      c.append(F.Gamma(F.Plus(F.C1, F.Negate(x), au)));
    }
    for (IExpr x : bo) {
      c.append(F.Gamma(F.Subtract(x, au)));
    }
    for (IExpr x : ao) {
      c.append(F.Power(F.Gamma(F.Subtract(x, au)), F.CN1));
    }
    for (IExpr x : bq) {
      c.append(F.Power(F.Gamma(F.Plus(F.C1, F.Negate(x), au)), F.CN1));
    }
    IExpr argument = (ao.size() + bo.size() + 1) % 2 == 0 ? variable : F.Negate(variable);
    c.append(power(z, r, inZ, au));
    c.append(F.HypergeometricPFQ(F.List(upper.toArray(new IExpr[0])),
        F.List(lower.toArray(new IExpr[0])), argument));
    result.append(c);
    return F.subs(engine.evaluate(result), log, logVariable);
  }

  /**
   * The residue at <code>s0</code> of <code>E^(s*log) * Product(Gamma(alpha+sigma*s)) /
   * Product(Gamma(beta+tau*s))</code>, from the Laurent series: each gamma function at a pole is
   * <code>Gamma(1+x)/(x*(x-1)*...*(x-N))</code>.
   */
  private static IExpr residue(List<IExpr[]> numerator, List<IExpr[]> denominator, IExpr s0,
      ISymbol log, EvalEngine engine) {
    ISymbol eps = F.Dummy("eps");
    int order = 0;
    IASTAppendable regular = F.TimesAlloc(numerator.size() + denominator.size() + 1);
    regular.append(F.Exp(F.Times(F.Plus(s0, eps), log)));
    for (IExpr[] gamma : numerator) {
      IExpr c = engine.evaluate(F.Plus(gamma[0], F.Times(gamma[1], s0)));
      IExpr x = F.Times(gamma[1], eps);
      if (c.isInteger() && !c.isPositive()) {
        int pole = -c.toIntDefault();
        order++;
        regular.append(F.Divide(F.Gamma(F.Plus(F.C1, x)), gamma[1]));
        for (int j = 1; j <= pole; j++) {
          regular.append(F.Power(F.Subtract(x, F.ZZ(j)), F.CN1));
        }
      } else {
        regular.append(F.Gamma(F.Plus(c, x)));
      }
    }
    for (IExpr[] gamma : denominator) {
      IExpr c = engine.evaluate(F.Plus(gamma[0], F.Times(gamma[1], s0)));
      IExpr x = F.Times(gamma[1], eps);
      if (c.isInteger() && !c.isPositive()) {
        int pole = -c.toIntDefault();
        order--;
        regular.append(F.Divide(gamma[1], F.Gamma(F.Plus(F.C1, x))));
        for (int j = 1; j <= pole; j++) {
          regular.append(F.Subtract(x, F.ZZ(j)));
        }
      } else {
        regular.append(F.Power(F.Gamma(F.Plus(c, x)), F.CN1));
      }
    }
    if (order <= 0) {
      return F.C0;
    }
    IExpr derivative = order == 1 ? regular : engine.evaluate(F.D(regular, F.list(eps, F.ZZ(order - 1))));
    IExpr value = engine.evaluate(F.Divide(F.subs(derivative, eps, F.C0), F.Factorial(F.ZZ(order - 1))));
    return value;
  }
}
