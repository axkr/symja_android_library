package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.TimeLimits;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Indefinite integrals of <code>x^alpha * P(x, kernels)</code>, where the kernels are Bessel
 * functions <code>BesselJ, BesselY, BesselI, BesselK</code> of the argument <code>b*x</code>, Airy
 * functions <code>AiryAi, AiryBi</code> and their derivatives of <code>b*x</code>, and
 * <code>Sin, Cos</code> of <code>b*x</code>.
 *
 * <p>
 * <b>Module ansatz.</b> Each kernel family spans a two dimensional space <code>{p, q}</code> which
 * is closed under <code>d/dx</code>, with coefficients that are Laurent polynomials in
 * <code>x</code>:
 *
 * <pre>
 * BesselJ, BesselY:  p = Z(nu, b*x), q = Z(nu+1, b*x),  p' = nu/x*p - b*q,  q' = b*p - (nu+1)/x*q
 * BesselI:           p' = nu/x*p + b*q,  q' = b*p - (nu+1)/x*q
 * BesselK:           p' = nu/x*p - b*q,  q' = -b*p - (nu+1)/x*q
 * Sin, Cos:          p = Sin(b*x), q = Cos(b*x),  p' = b*q,  q' = -b*p
 * Airy:              p = AiryAi(b*x), q = AiryAiPrime(b*x),  p' = b*q,  q' = b^2*x*p
 * </pre>
 *
 * The orders <code>nu0+k</code> of one family are written in the basis of the least order by the
 * three term recurrence. A product of kernels is then a monomial in the family symbols, and the
 * derivative maps the monomials of one multidegree into themselves. The antiderivative is sought as
 * <code>x^alpha * Sum(c(m,k)*x^k*M_m)</code>: comparing the coefficients of <code>x^j*M</code> in
 * its derivative with the integrand gives a linear system for the <code>c(m,k)</code>. Its lower
 * bound for <code>k</code> is exact (the indicial exponents of the regular singular point
 * <code>x=0</code>, which are diagonal in the monomial basis); the upper bound is a heuristic,
 * which can only miss an antiderivative, never return a wrong one. An integrand whose
 * antiderivative is not in the module, like <code>x*BesselJ(1,x)</code> (Struve functions), has no
 * solution and is declined.
 *
 * <p>
 * <b>Wronskian route.</b> For two solutions <code>y1, y2</code> of one second order equation with
 * the Wronskian <code>W</code>, <code>d/dx(y2/y1) = W/y1^2</code>. So an integrand
 * <code>W*g(y1,y2)</code> with <code>g</code> homogeneous of degree <code>-2</code> is integrated
 * as <code>Integrate(g(1,t), t)</code> with <code>t = y2/y1</code>. The pairs are
 * <code>AiryAi/AiryBi</code> (<code>W = b/Pi</code>), <code>BesselJ/BesselY</code> (<code>W =
 * 2/(Pi*x)</code>) and <code>BesselI/BesselK</code> (<code>W = -1/x</code>) of one order and
 * argument.
 *
 * <p>
 * Every result is verified by differentiating it back at sample points, the parameters set to
 * generic rational values.
 */
public final class HolonomicIntegration {

  /** the largest integer power of a kernel factor which is multiplied out */
  private static final int MAX_POWER = 12;

  /** the largest order difference within one family */
  private static final int MAX_ORDER_SHIFT = 24;

  /** the largest number of unknowns of one linear system */
  private static final int MAX_UNKNOWNS = 700;

  /** the generic values of the parameters in the numeric check */
  private static final IExpr[] PARAMETER_VALUES = {F.QQ(23, 29), F.QQ(31, 37), F.QQ(13, 17),
      F.QQ(41, 43), F.QQ(7, 11), F.QQ(19, 23), F.QQ(29, 31), F.QQ(3, 7)};

  /** the sample points of the numeric check */
  private static final IExpr[] SAMPLE_POINTS = {F.QQ(37, 100), F.QQ(71, 100), F.QQ(143, 100)};

  private enum Kind {
    BESSEL_J, BESSEL_Y, BESSEL_I, BESSEL_K, TRIG, AIRY_AI, AIRY_BI;

    boolean isBessel() {
      return this == BESSEL_J || this == BESSEL_Y || this == BESSEL_I || this == BESSEL_K;
    }
  }

  /** a kernel occurrence: kind, order (0 for trig and Airy), scale and whether it is q */
  private static final class Kernel {
    final Kind kind;
    final IExpr order;
    final IExpr scale;
    /** Cos for trig, the derivative for Airy */
    final boolean second;

    Kernel(Kind kind, IExpr order, IExpr scale, boolean second) {
      this.kind = kind;
      this.order = order;
      this.scale = scale;
      this.second = second;
    }
  }

  /** a family of kernels of one kind and scale whose orders differ by integers */
  private static final class Family {
    final Kind kind;
    final IExpr scale;
    IExpr order;
    /** the kernels of order nu0+k in the basis {p, q}, built on demand */
    List<Poly> shifted;

    Family(Kind kind, IExpr order, IExpr scale) {
      this.kind = kind;
      this.order = order;
      this.scale = scale;
    }
  }

  /** an exponent vector of the family symbols */
  private static final class Mono {
    final int[] exps;
    final int hash;

    Mono(int[] exps) {
      this.exps = exps;
      this.hash = Arrays.hashCode(exps);
    }

    int degree() {
      int d = 0;
      for (int e : exps) {
        d += e;
      }
      return d;
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof Mono && Arrays.equals(exps, ((Mono) obj).exps);
    }

    @Override
    public int hashCode() {
      return hash;
    }
  }

  /** a monomial times a power of x */
  private static final class Key {
    final Mono mono;
    final IExpr xExponent;

    Key(Mono mono, IExpr xExponent) {
      this.mono = mono;
      this.xExponent = xExponent;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Key)) {
        return false;
      }
      Key other = (Key) obj;
      return mono.equals(other.mono) && xExponent.equals(other.xExponent);
    }

    @Override
    public int hashCode() {
      return mono.hash * 31 + xExponent.hashCode();
    }
  }

  /** a polynomial in the family symbols with coefficients <code>c*x^e</code> */
  private static final class Poly {
    final Map<Key, IExpr> terms = new LinkedHashMap<>();
  }

  /** a term of the derivative of a family symbol: coefficient * x^xOffset * symbol */
  private static final class DerTerm {
    final int symbol;
    final int xOffset;
    final IExpr coefficient;

    DerTerm(int symbol, int xOffset, IExpr coefficient) {
      this.symbol = symbol;
      this.xOffset = xOffset;
      this.coefficient = coefficient;
    }
  }

  /** one term of an exponent class: monomial, integer offset to alpha and coefficient */
  private static final class ClassTerm {
    final Mono mono;
    final int offset;
    final IExpr coefficient;

    ClassTerm(Mono mono, int offset, IExpr coefficient) {
      this.mono = mono;
      this.offset = offset;
      this.coefficient = coefficient;
    }
  }

  /** a linear equation: Sum(coefficient*unknown) == rhs */
  private static final class Row {
    final Map<Integer, IExpr> coefficients = new LinkedHashMap<>();
    IExpr rhs = F.C0;
    int pivot = -1;
  }

  private final IExpr x;
  private final EvalEngine engine;
  private final List<Family> families = new ArrayList<>();
  private int symbolCount;
  private DerTerm[][] derivatives;

  private HolonomicIntegration(IExpr x, EvalEngine engine) {
    this.x = x;
    this.engine = engine;
  }

  /**
   * <code>true</code> if <code>f</code> contains a Bessel or Airy function of an argument which
   * depends on <code>x</code>, the gate of the stage.
   */
  public static boolean hasKernel(IExpr f, IExpr x) {
    return !f.isFree(e -> {
      if (e.isAST(S.BesselJ, 3) || e.isAST(S.BesselY, 3) || e.isAST(S.BesselI, 3)
          || e.isAST(S.BesselK, 3)) {
        return !e.second().isFree(x);
      }
      if (e.isAST(S.AiryAi, 2) || e.isAST(S.AiryBi, 2) || e.isAST(S.AiryAiPrime, 2)
          || e.isAST(S.AiryBiPrime, 2)) {
        return !e.first().isFree(x);
      }
      return false;
    }, true);
  }

  /**
   * <code>true</code> if the module ansatz is the natural first method for <code>f</code>: more
   * than one kernel factor (a product or a power), or a Wronskian shape. A single Bessel or Airy
   * function times a power of <code>x</code> is what the integration rules are written for.
   */
  public static boolean isProductOfKernels(IExpr f, IExpr x) {
    int[] count = new int[1];
    countKernels(f, x, count);
    return count[0] >= 2;
  }

  private static void countKernels(IExpr f, IExpr x, int[] count) {
    if (f.isFree(x)) {
      return;
    }
    if (f.isPower() && !f.base().isFree(x)) {
      int n = f.exponent().toIntDefault();
      if (n != 1 && hasKernel(f.base(), x)) {
        // a power other than 1 of a kernel: a product or a quotient
        count[0] += 2;
        return;
      }
      countKernels(f.base(), x, count);
      return;
    }
    if (f.isTimes() || f.isPlus()) {
      if (f.isPlus()) {
        // a sum counts as its largest term
        int max = 0;
        for (IExpr arg : (IAST) f) {
          int[] c = new int[1];
          countKernels(arg, x, c);
          max = Math.max(max, c[0]);
        }
        count[0] += max;
        return;
      }
      for (IExpr arg : (IAST) f) {
        countKernels(arg, x, count);
      }
      return;
    }
    if (hasKernel(f, x)) {
      count[0]++;
    }
  }

  /**
   * The antiderivative of <code>f</code> by the module ansatz or the Wronskian route, or
   * {@link F#NIL}.
   *
   * @param f the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   */
  public static IExpr integrate(IExpr f, IExpr x, EvalEngine engine) {
    if (!x.isSymbol() || !hasKernel(f, x)) {
      return F.NIL;
    }
    try {
      IExpr result = new HolonomicIntegration(x, engine).ansatz(f);
      if (result.isPresent() && verified(result, f, x, engine)) {
        return result;
      }
      result = new HolonomicIntegration(x, engine).wronskian(f);
      if (result.isPresent() && verified(result, f, x, engine)) {
        return result;
      }
      result = besselSquare(f, x, engine);
      if (result.isPresent()) {
        return result;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /**
   * The square of a Bessel function <code>c*BesselJ(n,u)^2</code> or <code>c*BesselI(n,u)^2</code>
   * with <code>u = a+b*x</code>. Its series is a <code>1F2</code> in <code>u^2</code>, so the
   * antiderivative is
   *
   * <pre>
   * c * 2^(-1-2*n) * u^(1+2*n) * HypergeometricPFQ({1/2+n,1/2+n},{1+n,3/2+n,1+2*n},-+u^2)
   *   / ((1/2+n) * Gamma(1+n)^2 * b)
   * </pre>
   *
   * with <code>-u^2</code> for <code>BesselJ</code> and <code>u^2</code> for <code>BesselI</code>.
   * Orders with <code>1+2*n</code> zero or a negative integer, where the formula has no value, are
   * left out.
   */
  private static IExpr besselSquare(IExpr f, IExpr x, EvalEngine engine) {
    IExpr coefficient = F.C1;
    IExpr square = f;
    if (f.isTimes()) {
      IASTAppendable free = F.TimesAlloc(((IAST) f).argSize());
      square = F.NIL;
      for (IExpr factor : (IAST) f) {
        if (factor.isFree(x)) {
          free.append(factor);
        } else if (square.isPresent()) {
          return F.NIL;
        } else {
          square = factor;
        }
      }
      if (square.isNIL()) {
        return F.NIL;
      }
      coefficient = free.oneIdentity1();
    }
    if (!square.isPower() || !square.exponent().isInteger() || !square.exponent().equals(F.C2)) {
      return F.NIL;
    }
    IExpr bessel = square.base();
    final boolean modified = bessel.isAST(S.BesselI, 3);
    if (!modified && !bessel.isAST(S.BesselJ, 3)) {
      return F.NIL;
    }
    IExpr n = bessel.first();
    IExpr u = bessel.second();
    if (!n.isFree(x)) {
      return F.NIL;
    }
    IExpr b = engine.evaluate(F.D(u, x));
    if (!b.isFree(x) || b.isZero()) {
      return F.NIL;
    }
    IExpr lowest = engine.evaluate(F.Plus(F.C1, F.Times(F.C2, n)));
    if (lowest.isInteger() && !lowest.isPositive()) {
      return F.NIL;
    }
    IExpr upper = F.Plus(F.C1D2, n);
    IExpr parameters1 = F.list(upper, upper);
    IExpr parameters2 = F.list(F.Plus(F.C1, n), F.Plus(F.C3D2, n), lowest);
    IExpr z = modified ? F.Sqr(u) : F.Negate(F.Sqr(u));
    return engine.evaluate(F.Times(coefficient, F.Power(F.C2, F.Plus(F.CN1, F.Times(F.CN2, n))),
        F.Power(u, lowest), F.HypergeometricPFQ(parameters1, parameters2, z),
        F.Power(F.Times(upper, F.Sqr(F.Gamma(F.Plus(F.C1, n))), b), F.CN1)));
  }

  // ---------------------------------------------------------------------------------------------
  // kernels and families

  /** the factor <code>b</code> of an argument <code>b*x</code>, or <code>null</code> */
  private IExpr linearScale(IExpr arg) {
    if (arg.equals(x)) {
      return F.C1;
    }
    if (!arg.isTimes()) {
      return null;
    }
    IExpr b = engine.evaluate(F.Divide(arg, x));
    if (!b.isFree(x) || b.isZero()) {
      return null;
    }
    return b;
  }

  /** the kernel <code>e</code> is, or <code>null</code> */
  private Kernel kernelOf(IExpr e) {
    if (e.isAST2()) {
      Kind kind = null;
      if (e.isAST(S.BesselJ, 3)) {
        kind = Kind.BESSEL_J;
      } else if (e.isAST(S.BesselY, 3)) {
        kind = Kind.BESSEL_Y;
      } else if (e.isAST(S.BesselI, 3)) {
        kind = Kind.BESSEL_I;
      } else if (e.isAST(S.BesselK, 3)) {
        kind = Kind.BESSEL_K;
      }
      if (kind == null || !e.first().isFree(x) || e.second().isFree(x)) {
        return null;
      }
      IExpr b = linearScale(e.second());
      return b == null ? null : new Kernel(kind, e.first(), b, false);
    }
    if (e.isAST1() && !e.first().isFree(x)) {
      IExpr head = e.head();
      Kind kind;
      boolean second;
      if (head == S.Sin || head == S.Cos) {
        kind = Kind.TRIG;
        second = head == S.Cos;
      } else if (head == S.AiryAi || head == S.AiryAiPrime) {
        kind = Kind.AIRY_AI;
        second = head == S.AiryAiPrime;
      } else if (head == S.AiryBi || head == S.AiryBiPrime) {
        kind = Kind.AIRY_BI;
        second = head == S.AiryBiPrime;
      } else {
        return null;
      }
      IExpr b = linearScale(e.first());
      return b == null ? null : new Kernel(kind, F.C0, b, second);
    }
    return null;
  }

  /** the family of the kernel, or <code>null</code> */
  private Family familyOf(Kernel k) {
    for (Family family : families) {
      if (family.kind == k.kind && engine.evaluate(F.Subtract(family.scale, k.scale)).isZero()
          && engine.evaluate(F.Subtract(k.order, family.order)).isInteger()) {
        return family;
      }
    }
    return null;
  }

  /** collect the kernel families of <code>f</code> */
  private void collectFamilies(IExpr f) {
    if (f.isFree(x) || !f.isAST()) {
      return;
    }
    Kernel k = kernelOf(f);
    if (k != null) {
      Family family = familyOf(k);
      if (family == null) {
        families.add(new Family(k.kind, k.order, k.scale));
      } else {
        IExpr d = engine.evaluate(F.Subtract(k.order, family.order));
        if (d.isNegative()) {
          family.order = k.order;
        }
      }
      return;
    }
    for (IExpr arg : (IAST) f) {
      collectFamilies(arg);
    }
  }

  /** the derivative tables of the family symbols */
  private void buildDerivatives() {
    symbolCount = 2 * families.size();
    derivatives = new DerTerm[symbolCount][];
    for (int i = 0; i < families.size(); i++) {
      Family family = families.get(i);
      int p = 2 * i;
      int q = 2 * i + 1;
      IExpr nu = family.order;
      IExpr b = family.scale;
      IExpr nu1 = engine.evaluate(F.Negate(F.Plus(nu, F.C1)));
      IExpr minusB = engine.evaluate(F.Negate(b));
      switch (family.kind) {
        case BESSEL_J:
        case BESSEL_Y:
          derivatives[p] = new DerTerm[] {new DerTerm(p, -1, nu), new DerTerm(q, 0, minusB)};
          derivatives[q] = new DerTerm[] {new DerTerm(p, 0, b), new DerTerm(q, -1, nu1)};
          break;
        case BESSEL_I:
          derivatives[p] = new DerTerm[] {new DerTerm(p, -1, nu), new DerTerm(q, 0, b)};
          derivatives[q] = new DerTerm[] {new DerTerm(p, 0, b), new DerTerm(q, -1, nu1)};
          break;
        case BESSEL_K:
          derivatives[p] = new DerTerm[] {new DerTerm(p, -1, nu), new DerTerm(q, 0, minusB)};
          derivatives[q] = new DerTerm[] {new DerTerm(p, 0, minusB), new DerTerm(q, -1, nu1)};
          break;
        case TRIG:
          derivatives[p] = new DerTerm[] {new DerTerm(q, 0, b)};
          derivatives[q] = new DerTerm[] {new DerTerm(p, 0, minusB)};
          break;
        default:
          // Airy: Ai'' = z*Ai
          derivatives[p] = new DerTerm[] {new DerTerm(q, 0, b)};
          derivatives[q] =
              new DerTerm[] {new DerTerm(p, 1, engine.evaluate(F.Power(b, F.C2)))};
          break;
      }
    }
  }

  /**
   * The residue at <code>x=0</code> of the derivative on the symbol: the order for <code>p</code>
   * and <code>-(order+1)</code> for <code>q</code> of a Bessel family, 0 otherwise.
   */
  private IExpr residueAtZero(int symbol) {
    for (DerTerm term : derivatives[symbol]) {
      if (term.symbol == symbol && term.xOffset == -1) {
        return term.coefficient;
      }
    }
    return F.C0;
  }

  // ---------------------------------------------------------------------------------------------
  // polynomial arithmetic

  private IExpr plus(IExpr a, IExpr b) {
    if (a.isZero()) {
      return b;
    }
    if (b.isZero()) {
      return a;
    }
    if (a.isNumber() && b.isNumber()) {
      return ((INumber) a).plus((INumber) b);
    }
    return engine.evaluate(F.Plus(a, b));
  }

  private IExpr times(IExpr a, IExpr b) {
    if (a.isOne()) {
      return b;
    }
    if (b.isOne()) {
      return a;
    }
    if (a.isNumber() && b.isNumber()) {
      return ((INumber) a).times((INumber) b);
    }
    return engine.evaluate(F.Times(a, b));
  }

  /** a normal form of a coefficient, in which a zero is recognized */
  private IExpr normal(IExpr a) {
    if (a.isNumber()) {
      return a;
    }
    return engine.evaluate(F.Together(a));
  }

  private static void addTerm(Poly poly, Key key, IExpr coefficient, HolonomicIntegration h) {
    if (coefficient.isZero()) {
      return;
    }
    IExpr old = poly.terms.get(key);
    poly.terms.put(key, old == null ? coefficient : h.plus(old, coefficient));
  }

  private Mono unitMono() {
    return new Mono(new int[symbolCount]);
  }

  private Poly constant(IExpr c) {
    Poly poly = new Poly();
    addTerm(poly, new Key(unitMono(), F.C0), c, this);
    return poly;
  }

  private Poly symbolPoly(int symbol, IExpr coefficient, IExpr xExponent) {
    int[] exps = new int[symbolCount];
    exps[symbol] = 1;
    Poly poly = new Poly();
    addTerm(poly, new Key(new Mono(exps), xExponent), coefficient, this);
    return poly;
  }

  private Poly add(Poly a, Poly b) {
    Poly sum = new Poly();
    for (Map.Entry<Key, IExpr> entry : a.terms.entrySet()) {
      addTerm(sum, entry.getKey(), entry.getValue(), this);
    }
    for (Map.Entry<Key, IExpr> entry : b.terms.entrySet()) {
      addTerm(sum, entry.getKey(), entry.getValue(), this);
    }
    return sum;
  }

  private Poly multiply(Poly a, Poly b) {
    Poly product = new Poly();
    for (Map.Entry<Key, IExpr> ea : a.terms.entrySet()) {
      TimeLimits.check();
      for (Map.Entry<Key, IExpr> eb : b.terms.entrySet()) {
        int[] exps = new int[symbolCount];
        int[] x1 = ea.getKey().mono.exps;
        int[] x2 = eb.getKey().mono.exps;
        for (int i = 0; i < symbolCount; i++) {
          exps[i] = x1[i] + x2[i];
        }
        IExpr xExponent = plus(ea.getKey().xExponent, eb.getKey().xExponent);
        addTerm(product, new Key(new Mono(exps), xExponent), times(ea.getValue(), eb.getValue()),
            this);
      }
    }
    return product;
  }

  /** the kernel of order <code>nu0+k</code> of the family in the basis <code>{p, q}</code> */
  private Poly shifted(int familyIndex, int k) {
    Family family = families.get(familyIndex);
    if (family.shifted == null) {
      family.shifted = new ArrayList<>();
      family.shifted.add(symbolPoly(2 * familyIndex, F.C1, F.C0));
      family.shifted.add(symbolPoly(2 * familyIndex + 1, F.C1, F.C0));
    }
    while (family.shifted.size() <= k) {
      int size = family.shifted.size();
      // z0 = Z(n), z1 = Z(n+1), n = nu0 + size - 2
      Poly z0 = family.shifted.get(size - 2);
      Poly z1 = family.shifted.get(size - 1);
      IExpr n = engine.evaluate(F.Plus(family.order, F.ZZ(size - 2)));
      // 2*(n+1)/(b*x)
      IExpr c = engine.evaluate(F.Times(F.C2, F.Plus(n, F.C1), F.Power(family.scale, F.CN1)));
      Poly cz1 = multiply(constantTimesX(c, F.CN1), z1);
      Poly next;
      switch (family.kind) {
        case BESSEL_I:
          // I(n+2) = I(n) - 2*(n+1)/(b*x)*I(n+1)
          next = add(z0, negate(cz1));
          break;
        case BESSEL_K:
          // K(n+2) = K(n) + 2*(n+1)/(b*x)*K(n+1)
          next = add(z0, cz1);
          break;
        default:
          // J(n+2) = 2*(n+1)/(b*x)*J(n+1) - J(n)
          next = add(cz1, negate(z0));
          break;
      }
      family.shifted.add(next);
    }
    return family.shifted.get(k);
  }

  private Poly constantTimesX(IExpr c, IExpr xExponent) {
    Poly poly = new Poly();
    addTerm(poly, new Key(unitMono(), xExponent), c, this);
    return poly;
  }

  private Poly negate(Poly a) {
    Poly result = new Poly();
    for (Map.Entry<Key, IExpr> entry : a.terms.entrySet()) {
      addTerm(result, entry.getKey(), times(F.CN1, entry.getValue()), this);
    }
    return result;
  }

  /** <code>f</code> as a polynomial in the family symbols, or <code>null</code> */
  private Poly toPoly(IExpr f) {
    TimeLimits.check();
    if (f.isFree(x)) {
      return constant(f);
    }
    if (f.equals(x)) {
      return constantTimesX(F.C1, F.C1);
    }
    if (f.isPlus()) {
      Poly sum = new Poly();
      for (IExpr arg : (IAST) f) {
        Poly p = toPoly(arg);
        if (p == null) {
          return null;
        }
        sum = add(sum, p);
      }
      return sum;
    }
    if (f.isTimes()) {
      Poly product = constant(F.C1);
      for (IExpr arg : (IAST) f) {
        Poly p = toPoly(arg);
        if (p == null) {
          return null;
        }
        product = multiply(product, p);
      }
      return product;
    }
    if (f.isPower()) {
      IExpr base = f.base();
      IExpr exponent = f.exponent();
      if (base.equals(x) && exponent.isFree(x)) {
        return constantTimesX(F.C1, exponent);
      }
      int n = exponent.toIntDefault();
      if (n >= 1 && n <= MAX_POWER) {
        Poly p = toPoly(base);
        if (p == null) {
          return null;
        }
        Poly power = p;
        for (int i = 1; i < n; i++) {
          power = multiply(power, p);
        }
        return power;
      }
      return null;
    }
    Kernel k = kernelOf(f);
    if (k == null) {
      return null;
    }
    Family family = familyOf(k);
    if (family == null) {
      return null;
    }
    int index = families.indexOf(family);
    if (!k.kind.isBessel()) {
      return symbolPoly(2 * index + (k.second ? 1 : 0), F.C1, F.C0);
    }
    int shift = engine.evaluate(F.Subtract(k.order, family.order)).toIntDefault();
    if (shift < 0 || shift > MAX_ORDER_SHIFT) {
      return null;
    }
    return shifted(index, shift);
  }

  // ---------------------------------------------------------------------------------------------
  // the module ansatz

  private IExpr ansatz(IExpr f) {
    collectFamilies(f);
    boolean special = false;
    for (Family family : families) {
      if (family.kind != Kind.TRIG) {
        special = true;
      }
    }
    if (!special) {
      return F.NIL;
    }
    buildDerivatives();
    Poly g = toPoly(f);
    if (g == null || g.terms.isEmpty()) {
      return F.NIL;
    }
    // multidegree -> exponent classes -> terms
    Map<String, List<List<Map.Entry<Key, IExpr>>>> groups = new LinkedHashMap<>();
    Map<String, int[]> degreeOf = new HashMap<>();
    for (Map.Entry<Key, IExpr> entry : g.terms.entrySet()) {
      IExpr coefficient = normal(entry.getValue());
      if (coefficient.isZero()) {
        continue;
      }
      int[] exps = entry.getKey().mono.exps;
      int[] degrees = new int[families.size()];
      int total = 0;
      for (int i = 0; i < families.size(); i++) {
        degrees[i] = exps[2 * i] + exps[2 * i + 1];
        total += degrees[i];
      }
      if (total == 0) {
        // a term without a kernel
        return F.NIL;
      }
      String degreeKey = Arrays.toString(degrees);
      degreeOf.put(degreeKey, degrees);
      List<List<Map.Entry<Key, IExpr>>> classes =
          groups.computeIfAbsent(degreeKey, s -> new ArrayList<>());
      Map.Entry<Key, IExpr> term = new java.util.AbstractMap.SimpleEntry<>(entry.getKey(),
          coefficient);
      boolean placed = false;
      for (List<Map.Entry<Key, IExpr>> cls : classes) {
        IExpr d = engine.evaluate(
            F.Subtract(entry.getKey().xExponent, cls.get(0).getKey().xExponent));
        if (d.isInteger()) {
          cls.add(term);
          placed = true;
          break;
        }
      }
      if (!placed) {
        List<Map.Entry<Key, IExpr>> cls = new ArrayList<>();
        cls.add(term);
        classes.add(cls);
      }
    }
    if (groups.isEmpty()) {
      return F.NIL;
    }
    Map<Key, IExpr> antiderivative = new LinkedHashMap<>();
    for (Map.Entry<String, List<List<Map.Entry<Key, IExpr>>>> group : groups.entrySet()) {
      int[] degrees = degreeOf.get(group.getKey());
      for (List<Map.Entry<Key, IExpr>> cls : group.getValue()) {
        IExpr first = cls.get(0).getKey().xExponent;
        boolean integerClass = first.isInteger();
        IExpr alpha = integerClass ? F.C0 : first;
        List<ClassTerm> terms = new ArrayList<>();
        for (Map.Entry<Key, IExpr> term : cls) {
          int offset = engine.evaluate(F.Subtract(term.getKey().xExponent, alpha)).toIntDefault();
          if (offset == Integer.MIN_VALUE) {
            return F.NIL;
          }
          terms.add(new ClassTerm(term.getKey().mono, offset, term.getValue()));
        }
        if (!solveClass(terms, alpha, integerClass, degrees, antiderivative)) {
          return F.NIL;
        }
      }
    }
    IExpr result = backSubstitute(antiderivative);
    return result.isPresent() ? withLowerOrders(result) : F.NIL;
  }

  /**
   * The antiderivative with the higher order of a Bessel family written by the two orders below
   * it, where that is smaller: <code>Integrate(BesselJ(1,x),x)</code> is
   * <code>-BesselJ(0,x)</code>, not <code>BesselJ(2,x)-2*BesselJ(1,x)/x</code>.
   */
  private IExpr withLowerOrders(IExpr result) {
    IExpr best = result;
    for (Family family : families) {
      if (!family.kind.isBessel() || best.leafCount() > 300) {
        continue;
      }
      IExpr head = family.kind == Kind.BESSEL_J ? S.BesselJ
          : family.kind == Kind.BESSEL_Y ? S.BesselY
              : family.kind == Kind.BESSEL_I ? S.BesselI : S.BesselK;
      IExpr arg = engine.evaluate(F.Times(family.scale, x));
      IExpr nu = family.order;
      IExpr high = engine.evaluate(F.binaryAST2(head, F.Plus(nu, F.C1), arg));
      IExpr middle = engine.evaluate(F.binaryAST2(head, nu, arg));
      IExpr low = engine.evaluate(F.binaryAST2(head, F.Subtract(nu, F.C1), arg));
      if (!high.isAST(head, 3) || best.isFree(high)) {
        continue;
      }
      IExpr c = F.Times(F.C2, nu, F.Power(arg, F.CN1));
      IExpr replacement;
      switch (family.kind) {
        case BESSEL_I:
          replacement = F.Subtract(low, F.Times(c, middle));
          break;
        case BESSEL_K:
          replacement = F.Plus(low, F.Times(c, middle));
          break;
        default:
          replacement = F.Subtract(F.Times(c, middle), low);
          break;
      }
      IExpr candidate = engine.evaluate(F.ternaryAST3(S.Collect,
          F.Expand(F.subst(best, high, replacement)), F.List(low, middle), S.Together));
      if (candidate.isFree(S.Collect) && candidate.leafCount() < best.leafCount()) {
        best = candidate;
      }
    }
    return best;
  }

  /** the monomials of the given degree per family */
  private List<Mono> monomials(int[] degrees) {
    List<int[]> list = new ArrayList<>();
    list.add(new int[symbolCount]);
    for (int i = 0; i < degrees.length; i++) {
      List<int[]> next = new ArrayList<>();
      for (int[] exps : list) {
        for (int a = degrees[i]; a >= 0; a--) {
          int[] e = exps.clone();
          e[2 * i] = a;
          e[2 * i + 1] = degrees[i] - a;
          next.add(e);
        }
      }
      list = next;
    }
    List<Mono> monos = new ArrayList<>(list.size());
    for (int[] exps : list) {
      monos.add(new Mono(exps));
    }
    return monos;
  }

  /** the derivative of a monomial: (monomial, x offset, coefficient) terms */
  private List<Object[]> derivative(Mono mono) {
    List<Object[]> result = new ArrayList<>();
    for (int s = 0; s < symbolCount; s++) {
      int e = mono.exps[s];
      if (e == 0) {
        continue;
      }
      for (DerTerm term : derivatives[s]) {
        int[] exps = mono.exps.clone();
        exps[s]--;
        exps[term.symbol]++;
        result.add(new Object[] {new Mono(exps), term.xOffset,
            times(F.ZZ(e), term.coefficient)});
      }
    }
    return result;
  }

  /**
   * Solve the linear system of one exponent class and add <code>x^alpha*F</code> to
   * <code>antiderivative</code> ((monomial, alpha) -> Laurent polynomial coefficient).
   */
  private boolean solveClass(List<ClassTerm> terms, IExpr alpha, boolean integerClass,
      int[] degrees, Map<Key, IExpr> antiderivative) {
    List<Mono> monos = monomials(degrees);
    int dim = monos.size();
    Map<Mono, Integer> index = new HashMap<>();
    for (int m = 0; m < dim; m++) {
      index.put(monos.get(m), m);
    }
    int jmin = Integer.MAX_VALUE;
    int jmax = Integer.MIN_VALUE;
    for (ClassTerm term : terms) {
      jmin = Math.min(jmin, term.offset);
      jmax = Math.max(jmax, term.offset);
    }
    // exact lower end: the indicial exponents -alpha-e(M) of the homogeneous solutions at x=0
    int lo = jmin + 1;
    for (Mono mono : monos) {
      IASTAppendable sum = F.PlusAlloc(symbolCount + 1);
      sum.append(F.Negate(alpha));
      for (int s = 0; s < symbolCount; s++) {
        if (mono.exps[s] != 0) {
          sum.append(F.Times(F.ZZ(-mono.exps[s]), residueAtZero(s)));
        }
      }
      IExpr exponent = engine.evaluate(sum);
      if (exponent.isInteger()) {
        int k = exponent.toIntDefault();
        if (k != Integer.MIN_VALUE) {
          lo = Math.min(lo, k);
        }
      }
    }
    // heuristic upper end: a combination of kernels can decay faster than its coefficients grow,
    // like BesselJ(nu,x)^2+BesselJ(nu+1,x)^2 ~ 2/(Pi*x), which needs powers up to the degree
    int totalDegree = 0;
    for (int d : degrees) {
      totalDegree += d;
    }
    int hi = (integerClass ? Math.max(jmax + 1, totalDegree) : jmax + 1) + dim;
    int width = hi - lo + 1;
    if (width <= 0 || (long) width * dim > MAX_UNKNOWNS) {
      return false;
    }
    // equations, keyed by (monomial, x power)
    Map<Long, Row> equations = new LinkedHashMap<>();
    List<List<Object[]>> monoDerivatives = new ArrayList<>(dim);
    for (Mono mono : monos) {
      monoDerivatives.add(derivative(mono));
    }
    for (int m = 0; m < dim; m++) {
      TimeLimits.check();
      for (int k = lo; k <= hi; k++) {
        int unknown = m * width + (k - lo);
        IExpr c = engine.evaluate(F.Plus(alpha, F.ZZ(k)));
        if (!c.isZero()) {
          addCoefficient(equations, m, k - 1, unknown, c);
        }
        for (Object[] term : monoDerivatives.get(m)) {
          Integer target = index.get(term[0]);
          if (target == null) {
            return false;
          }
          addCoefficient(equations, target, k + (Integer) term[1], unknown, (IExpr) term[2]);
        }
      }
    }
    for (ClassTerm term : terms) {
      Integer m = index.get(term.mono);
      if (m == null) {
        return false;
      }
      Row row = equations.computeIfAbsent(equationKey(m, term.offset), key -> new Row());
      row.rhs = plus(row.rhs, term.coefficient);
    }
    IExpr[] solution = solve(new ArrayList<>(equations.values()), width * dim);
    if (solution == null) {
      return false;
    }
    // F = Sum c(m,k)*x^(alpha+k)*M_m
    Map<Key, IASTAppendable> sums = new LinkedHashMap<>();
    for (int m = 0; m < dim; m++) {
      for (int k = lo; k <= hi; k++) {
        IExpr c = solution[m * width + (k - lo)];
        if (c.isZero()) {
          continue;
        }
        sums.computeIfAbsent(new Key(monos.get(m), alpha), key -> F.PlusAlloc(4))
            .append(F.Times(c, F.Power(x, F.ZZ(k))));
      }
    }
    for (Map.Entry<Key, IASTAppendable> entry : sums.entrySet()) {
      IExpr old = antiderivative.get(entry.getKey());
      IExpr value = engine.evaluate(entry.getValue().oneIdentity0());
      antiderivative.put(entry.getKey(), old == null ? value : engine.evaluate(F.Plus(old, value)));
    }
    return true;
  }

  private static long equationKey(int mono, int xPower) {
    return ((long) mono << 32) ^ (xPower & 0xffffffffL);
  }

  private void addCoefficient(Map<Long, Row> equations, int mono, int xPower, int unknown,
      IExpr c) {
    Row row = equations.computeIfAbsent(equationKey(mono, xPower), key -> new Row());
    IExpr old = row.coefficients.get(unknown);
    row.coefficients.put(unknown, old == null ? c : plus(old, c));
  }

  /**
   * A particular solution of the linear system by Gauss-Jordan elimination, the free unknowns set
   * to 0, or <code>null</code> if the system is inconsistent.
   */
  private IExpr[] solve(List<Row> rows, int unknowns) {
    List<Row> pivots = new ArrayList<>();
    for (Row row : rows) {
      TimeLimits.check();
      normalizeRow(row);
      for (Row pivot : pivots) {
        IExpr factor = row.coefficients.get(pivot.pivot);
        if (factor != null) {
          eliminate(row, pivot, factor);
        }
      }
      if (row.coefficients.isEmpty()) {
        if (!normal(row.rhs).isZero()) {
          return null;
        }
        continue;
      }
      // the simplest coefficient becomes the pivot
      int best = -1;
      long bestSize = Long.MAX_VALUE;
      for (Map.Entry<Integer, IExpr> entry : row.coefficients.entrySet()) {
        IExpr c = entry.getValue();
        long size = c.isNumber() ? 0 : c.leafCount();
        if (size < bestSize) {
          bestSize = size;
          best = entry.getKey();
        }
      }
      IExpr inverse = c(row.coefficients.get(best));
      for (Map.Entry<Integer, IExpr> entry : row.coefficients.entrySet()) {
        entry.setValue(normal(times(entry.getValue(), inverse)));
      }
      row.rhs = normal(times(row.rhs, inverse));
      row.coefficients.put(best, F.C1);
      row.pivot = best;
      for (Row pivot : pivots) {
        IExpr factor = pivot.coefficients.get(best);
        if (factor != null) {
          eliminate(pivot, row, factor);
        }
      }
      pivots.add(row);
    }
    IExpr[] solution = new IExpr[unknowns];
    Arrays.fill(solution, F.C0);
    for (Row pivot : pivots) {
      solution[pivot.pivot] = pivot.rhs;
    }
    return solution;
  }

  /** <code>1/c</code> */
  private IExpr c(IExpr c) {
    if (c.isNumber()) {
      return ((INumber) c).inverse();
    }
    return engine.evaluate(F.Power(c, F.CN1));
  }

  private void normalizeRow(Row row) {
    row.coefficients.replaceAll((k, v) -> normal(v));
    row.coefficients.values().removeIf(IExpr::isZero);
  }

  /** <code>row = row - factor*pivot</code>, removing the pivot unknown from the row */
  private void eliminate(Row row, Row pivot, IExpr factor) {
    for (Map.Entry<Integer, IExpr> entry : pivot.coefficients.entrySet()) {
      int unknown = entry.getKey();
      IExpr product = times(factor, entry.getValue());
      IExpr old = row.coefficients.get(unknown);
      IExpr value = normal(old == null ? times(F.CN1, product)
          : (old.isNumber() && product.isNumber() ? ((INumber) old).subtract((INumber) product)
              : engine.evaluate(F.Subtract(old, product))));
      if (value.isZero()) {
        row.coefficients.remove(unknown);
      } else {
        row.coefficients.put(unknown, value);
      }
    }
    row.coefficients.remove(pivot.pivot);
    IExpr product = times(factor, pivot.rhs);
    row.rhs = normal(row.rhs.isNumber() && product.isNumber()
        ? ((INumber) row.rhs).subtract((INumber) product)
        : engine.evaluate(F.Subtract(row.rhs, product)));
  }

  /** the antiderivative with the kernels in place of the family symbols */
  private IExpr backSubstitute(Map<Key, IExpr> antiderivative) {
    IExpr[] kernels = new IExpr[symbolCount];
    for (int i = 0; i < families.size(); i++) {
      Family family = families.get(i);
      IExpr arg = engine.evaluate(F.Times(family.scale, x));
      IExpr order1 = engine.evaluate(F.Plus(family.order, F.C1));
      switch (family.kind) {
        case BESSEL_J:
          kernels[2 * i] = F.BesselJ(family.order, arg);
          kernels[2 * i + 1] = F.BesselJ(order1, arg);
          break;
        case BESSEL_Y:
          kernels[2 * i] = F.BesselY(family.order, arg);
          kernels[2 * i + 1] = F.BesselY(order1, arg);
          break;
        case BESSEL_I:
          kernels[2 * i] = F.BesselI(family.order, arg);
          kernels[2 * i + 1] = F.BesselI(order1, arg);
          break;
        case BESSEL_K:
          kernels[2 * i] = F.BesselK(family.order, arg);
          kernels[2 * i + 1] = F.BesselK(order1, arg);
          break;
        case TRIG:
          kernels[2 * i] = F.Sin(arg);
          kernels[2 * i + 1] = F.Cos(arg);
          break;
        case AIRY_AI:
          kernels[2 * i] = F.unaryAST1(S.AiryAi, arg);
          kernels[2 * i + 1] = F.unaryAST1(S.AiryAiPrime, arg);
          break;
        default:
          kernels[2 * i] = F.unaryAST1(S.AiryBi, arg);
          kernels[2 * i + 1] = F.unaryAST1(S.AiryBiPrime, arg);
          break;
      }
    }
    IASTAppendable result = F.PlusAlloc(antiderivative.size());
    for (Map.Entry<Key, IExpr> entry : antiderivative.entrySet()) {
      IExpr coefficient = engine.evaluate(F.Together(entry.getValue()));
      if (coefficient.isZero()) {
        continue;
      }
      if (!coefficient.isNumber() && coefficient.leafCount() < 200) {
        // the denominator in factors: (2*nu-1)*(2*nu+1)*x rather than -x+4*nu^2*x
        IExpr numerator = engine.evaluate(F.Numerator(coefficient));
        IExpr denominator = engine.evaluate(F.Factor(F.Denominator(coefficient)));
        coefficient = engine.evaluate(F.Divide(numerator, denominator));
      }
      IASTAppendable term = F.TimesAlloc(symbolCount + 2);
      term.append(coefficient);
      if (!entry.getKey().xExponent.isZero()) {
        term.append(F.Power(x, entry.getKey().xExponent));
      }
      int[] exps = entry.getKey().mono.exps;
      for (int s = 0; s < symbolCount; s++) {
        if (exps[s] == 1) {
          term.append(kernels[s]);
        } else if (exps[s] > 1) {
          term.append(F.Power(kernels[s], F.ZZ(exps[s])));
        }
      }
      result.append(term);
    }
    if (result.argSize() == 0) {
      return F.NIL;
    }
    return engine.evaluate(result.oneIdentity0());
  }

  // ---------------------------------------------------------------------------------------------
  // the Wronskian route

  private IExpr wronskian(IExpr f) {
    Set<IAST> pairs = new LinkedHashSet<>();
    collectPairs(f, pairs);
    if (pairs.size() != 1) {
      return F.NIL;
    }
    IAST pair = pairs.iterator().next();
    IExpr y1 = pair.arg1();
    IExpr y2 = pair.arg2();
    IExpr w = pair.arg3();
    ISymbol u1 = F.Dummy("hol$y1");
    ISymbol u2 = F.Dummy("hol$y2");
    ISymbol t = F.Dummy("hol$t");
    Map<IExpr, IExpr> toSymbols = new HashMap<>();
    toSymbols.put(y1, u1);
    toSymbols.put(y2, u2);
    IExpr h = engine.evaluate(F.Divide(F.subst(f, toSymbols), w));
    if (!h.isFree(x) || h.isFree(u1) && h.isFree(u2)) {
      return F.NIL;
    }
    IExpr g = engine.evaluate(F.subst(F.subst(h, u1, F.C1), u2, t));
    if (!isHomogeneousOfDegreeMinus2(h, g, u1, u2, t)) {
      return F.NIL;
    }
    IExpr antiderivative = engine.evaluate(F.Integrate(g, t));
    if (!antiderivative.isFreeAST(S.Integrate)) {
      return F.NIL;
    }
    // t^e -> y2^e*y1^(-e) rather than (y2/y1)^e: that form differentiates back on every
    // interval, also for a non-integer e
    IExpr back = F.subst(antiderivative, e -> {
      if (e.isPower() && e.base().equals(t)) {
        return F.Times(F.Power(y2, e.exponent()), F.Power(y1, F.Negate(e.exponent())));
      }
      return F.NIL;
    });
    back = F.subst(back, t, F.Divide(y2, y1));
    return engine.evaluate(back);
  }

  /** the pairs {y1, y2, Wronskian} of solutions of one equation which occur in f */
  private void collectPairs(IExpr f, Set<IAST> pairs) {
    if (f.isFree(x) || !f.isAST()) {
      return;
    }
    if ((f.isAST(S.AiryAi, 2) || f.isAST(S.AiryBi, 2))) {
      IExpr b = linearScale(f.first());
      if (b != null) {
        pairs.add(F.List(F.unaryAST1(S.AiryAi, f.first()), F.unaryAST1(S.AiryBi, f.first()),
            engine.evaluate(F.Divide(b, S.Pi))));
        return;
      }
    }
    if ((f.isAST(S.BesselJ, 3) || f.isAST(S.BesselY, 3)) && f.first().isFree(x)
        && linearScale(f.second()) != null) {
      pairs.add(F.List(F.BesselJ(f.first(), f.second()), F.BesselY(f.first(), f.second()),
          engine.evaluate(F.Divide(F.C2, F.Times(S.Pi, x)))));
      return;
    }
    if ((f.isAST(S.BesselI, 3) || f.isAST(S.BesselK, 3)) && f.first().isFree(x)
        && linearScale(f.second()) != null) {
      pairs.add(F.List(F.BesselI(f.first(), f.second()), F.BesselK(f.first(), f.second()),
          engine.evaluate(F.Divide(F.CN1, x))));
      return;
    }
    for (IExpr arg : (IAST) f) {
      collectPairs(arg, pairs);
    }
  }

  /**
   * <code>u1^2*h(u1, u1*t) == g(t)</code> at positive sample values, the parameters set to generic
   * values: for positive arguments that is the formal identity.
   */
  private boolean isHomogeneousOfDegreeMinus2(IExpr h, IExpr g, ISymbol u1, ISymbol u2,
      ISymbol t) {
    IExpr scaled = F.Times(F.Sqr(u1), F.subst(h, u2, F.Times(u1, t)));
    Map<IExpr, IExpr> parameters = parameterValues(F.List(h), x);
    if (parameters == null) {
      return false;
    }
    IExpr[][] samples = {{F.QQ(7, 10), F.QQ(13, 10)}, {F.QQ(19, 10), F.QQ(3, 5)},
        {F.QQ(23, 10), F.QQ(29, 10)}};
    for (IExpr[] sample : samples) {
      Map<IExpr, IExpr> values = new HashMap<>(parameters);
      values.put(u1, sample[0]);
      values.put(t, sample[1]);
      IExpr left = engine.evaluate(F.N(F.subst(scaled, values), F.ZZ(30)));
      IExpr right = engine.evaluate(F.N(F.subst(g, values), F.ZZ(30)));
      if (!left.isNumber() || !right.isNumber()) {
        return false;
      }
      double d = engine.evaluate(F.Abs(F.Subtract(left, right))).evalfNaN();
      double scale = engine.evaluate(F.Abs(right)).evalfNaN();
      if (Double.isNaN(d) || Double.isNaN(scale) || d > 1e-20 * (1.0 + scale)) {
        return false;
      }
    }
    return true;
  }

  // ---------------------------------------------------------------------------------------------
  // verification

  /** generic values for the symbols other than x, or <code>null</code> for too many */
  private static Map<IExpr, IExpr> parameterValues(IExpr expr, IExpr x) {
    Set<IExpr> symbols = new LinkedHashSet<>();
    collectSymbols(expr, x, symbols);
    if (symbols.size() > PARAMETER_VALUES.length) {
      return null;
    }
    Map<IExpr, IExpr> values = new HashMap<>();
    int i = 0;
    for (IExpr symbol : symbols) {
      values.put(symbol, PARAMETER_VALUES[i++]);
    }
    return values;
  }

  private static void collectSymbols(IExpr expr, IExpr x, Set<IExpr> symbols) {
    if (expr.isSymbol()) {
      if (!expr.equals(x) && expr.isVariable() && !((ISymbol) expr).isBuiltInSymbol()) {
        symbols.add(expr);
      }
      return;
    }
    if (expr.isAST()) {
      for (IExpr arg : (IAST) expr) {
        collectSymbols(arg, x, symbols);
      }
    }
  }

  /**
   * <code>D(antiderivative, x) == f</code> at three positive sample points, the
   * parameters set to generic rational values.
   */
  static boolean verified(IExpr antiderivative, IExpr f, IExpr x, EvalEngine engine) {
    if (!antiderivative.isFreeAST(S.Integrate) || !antiderivative.isSpecialsFree()) {
      return false;
    }
    Map<IExpr, IExpr> parameters = parameterValues(F.List(f, antiderivative), x);
    if (parameters == null) {
      return false;
    }
    IExpr derivative = engine.evaluate(F.D(antiderivative, x));
    if (engine.evaluate(F.Subtract(derivative, f)).isZero()) {
      return true;
    }
    // the two sides are evaluated separately: the numeric value of a difference which is zero
    // would be refined up to the precision limit
    derivative = F.subst(derivative, parameters);
    IExpr integrand = F.subst(f, parameters);
    for (IExpr point : SAMPLE_POINTS) {
      // machine precision first, which is fast; 30 digits where it may have cancelled
      if (!agreesAt(derivative, integrand, x, point, 0, 1e-10, engine)
          && !agreesAt(derivative, integrand, x, point, 30, 1e-20, engine)) {
        return false;
      }
    }
    return true;
  }

  private static boolean agreesAt(IExpr derivative, IExpr integrand, IExpr x, IExpr point,
      int precision, double tolerance, EvalEngine engine) {
    IExpr d = F.subst(derivative, x, point);
    IExpr v = F.subst(integrand, x, point);
    d = engine.evaluate(precision > 0 ? F.N(d, F.ZZ(precision)) : F.N(d));
    v = engine.evaluate(precision > 0 ? F.N(v, F.ZZ(precision)) : F.N(v));
    if (!d.isNumber() || !v.isNumber()) {
      return false;
    }
    double deviation = engine.evaluate(F.Abs(F.Subtract(d, v))).evalfNaN();
    double scale = engine.evaluate(F.Abs(v)).evalfNaN();
    return !Double.isNaN(deviation) && !Double.isNaN(scale)
        && deviation <= tolerance * (1.0 + scale);
  }
}
