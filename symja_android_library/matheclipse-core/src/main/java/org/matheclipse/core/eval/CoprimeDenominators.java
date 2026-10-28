package org.matheclipse.core.eval;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;

/**
 * <code>Together</code> of a sum of univariate rational functions with rational coefficients, in
 * the form Mathematica returns: the denominator is a product of the terms' denominators, split
 * only as far as two of them share a factor, and never factored into irreducibles.
 *
 * <pre>
 * Together(1/(x^2-1)+1/(x^2-4))  ==  (-5+2*x^2)/((-4+x^2)*(-1+x^2))
 * Together(1/(x^2-1)+1/(x^3-1))  ==  (2+2*x+x^2)/((-1+x)*(1+x)*(1+x+x^2))
 * Together(x/(x^2-1)+1/(x^2-1))  ==  1/(-1+x)
 * </pre>
 *
 * <p>
 * The factors of the denominators are refined to a coprime (gcd-free) basis, each element a
 * primitive polynomial with a positive leading coefficient. The common denominator is the product
 * of the basis elements to their highest multiplicity; a common factor of the numerator is then
 * cancelled from the basis element it divides, splitting the element if it divides only a part of
 * it.
 */
final class CoprimeDenominators {

  private final IExpr x;
  private final EvalEngine engine;
  /** pairwise coprime, primitive, positive leading coefficient, degree at least 1 */
  private final List<IExpr> basis = new ArrayList<>();
  /** the degrees of the polynomials seen so far */
  private final Map<IExpr, Integer> degrees = new HashMap<>();

  private CoprimeDenominators(IExpr x, EvalEngine engine) {
    this.x = x;
    this.engine = engine;
  }

  /**
   * The sum of <code>numerators[[i]]/denominators[[i]]</code> over a common denominator, or
   * {@link F#NIL} if a coefficient isn't rational.
   */
  static IExpr together(IAST numerators, IAST denominators, IExpr x, EvalEngine engine) {
    return new CoprimeDenominators(x, engine).together(numerators, denominators);
  }

  private IExpr together(IAST numerators, IAST denominators) {
    final int terms = numerators.argSize();
    for (IExpr numerator : numerators) {
      if (!isPolynomial(numerator)) {
        return F.NIL;
      }
    }
    final List<List<IExpr[]>> pieces = new ArrayList<>(terms);
    final INumber[] constants = new INumber[terms];
    for (int t = 0; t < terms; t++) {
      final List<IExpr[]> termPieces = new ArrayList<>();
      final INumber constant = pieces(denominators.get(t + 1), termPieces);
      if (constant == null) {
        return F.NIL;
      }
      constants[t] = constant;
      pieces.add(termPieces);
      for (IExpr[] piece : termPieces) {
        insert(piece[0]);
      }
    }

    // the multiplicity of every basis element in every denominator
    final Map<IExpr, Integer> index = new HashMap<>();
    for (int i = 0; i < basis.size(); i++) {
      index.put(basis.get(i), i);
    }
    final int[][] valuations = new int[terms][basis.size()];
    final int[] exponents = new int[basis.size()];
    for (int t = 0; t < terms; t++) {
      for (IExpr[] piece : pieces.get(t)) {
        final int e = piece[1].toIntDefault();
        if (!valuate(piece[0], e, valuations[t], index)) {
          return F.NIL;
        }
      }
      for (int i = 0; i < exponents.length; i++) {
        exponents[i] = Math.max(exponents[i], valuations[t][i]);
      }
    }

    final IExpr[] cancelled = cancelOverRationals(numerators, constants, valuations, exponents);
    if (cancelled != null) {
      if (cancelled[0].isZero()) {
        return F.C0;
      }
      return withDenominator(cancelled[0], (IAST) cancelled[1]);
    }

    final IASTAppendable sum = F.PlusAlloc(terms);
    for (int t = 0; t < terms; t++) {
      final IASTAppendable product = F.TimesAlloc(basis.size() + 2);
      product.append(numerators.get(t + 1));
      product.append(constants[t].inverse());
      for (int i = 0; i < exponents.length; i++) {
        final int e = exponents[i] - valuations[t][i];
        if (e > 0) {
          product.append(F.Power(basis.get(i), F.ZZ(e)));
        }
      }
      sum.append(product);
    }
    IExpr numerator = engine.evaluate(F.Expand(sum));
    if (numerator.isZero()) {
      return F.C0;
    }

    // cancel the common factors of the numerator and the denominator
    final List<IExpr> factors = new ArrayList<>(basis);
    final List<Integer> multiplicities = new ArrayList<>();
    for (int e : exponents) {
      multiplicities.add(e);
    }
    for (int i = 0; i < factors.size(); i++) {
      while (multiplicities.get(i) > 0) {
        final IExpr b = factors.get(i);
        final IExpr g = gcd(numerator, b);
        if (g == null) {
          break;
        }
        if (g.equals(b)) {
          numerator = engine.evaluate(F.PolynomialQuotient(numerator, b, x));
          multiplicities.set(i, multiplicities.get(i) - 1);
        } else {
          final IExpr[] rest = primitive(engine.evaluate(F.PolynomialQuotient(b, g, x)));
          if (rest == null) {
            return F.NIL;
          }
          factors.set(i, g);
          factors.add(rest[1]);
          multiplicities.add(multiplicities.get(i));
        }
      }
    }

    final IASTAppendable denominatorFactors = F.ListAlloc(factors.size());
    for (int i = 0; i < factors.size(); i++) {
      final int e = multiplicities.get(i);
      if (e > 0) {
        denominatorFactors.append(e == 1 ? factors.get(i) : F.Power(factors.get(i), F.ZZ(e)));
      }
    }
    return withDenominator(numerator, denominatorFactors);
  }

  /**
   * <code>numerator</code> over the product of <code>factors</code>, the denominators of the
   * numerator's (Gaussian) rational coefficients moved to the denominator.
   */
  private IExpr withDenominator(IExpr numerator, IAST factors) {
    // the denominators of the numerator's (Gaussian) rational coefficients go to the denominator
    BigInteger scale = BigInteger.ONE;
    final List<IExpr> coefficients = coefficients(numerator, new long[1]);
    if (coefficients != null) {
      for (IExpr c : coefficients) {
        if (c.isRational()) {
          scale = lcm(scale, ((IRational) c).toBigDenominator());
        } else if (c instanceof IComplex) {
          scale = lcm(scale, ((IComplex) c).re().toBigDenominator());
          scale = lcm(scale, ((IComplex) c).im().toBigDenominator());
        } else {
          scale = BigInteger.ONE;
          break;
        }
      }
    }
    final IASTAppendable denominator = F.TimesAlloc(factors.size() + 1);
    if (!scale.equals(BigInteger.ONE)) {
      numerator = engine.evaluate(F.Expand(F.Times(F.ZZ(scale), numerator)));
      denominator.append(F.ZZ(scale));
    }
    denominator.appendArgs(factors);
    if (denominator.isAST0()) {
      return numerator;
    }
    if (numerator.isNegative()) {
      // Symja's convention for a negative numerator, as togetherPlus has it: 1/(1-x), not
      // -1/(-1+x)
      return F.Divide(numerator.negate(), denominator.oneIdentity1().negate());
    }
    return F.Divide(numerator, denominator.oneIdentity1());
  }

  /**
   * Split <code>denominator</code> into its polynomial pieces <code>{primitive part,
   * exponent}</code>.
   *
   * @return the constant factor, or <code>null</code> if a coefficient isn't rational
   */
  private INumber pieces(IExpr denominator, List<IExpr[]> result) {
    INumber constant = F.C1;
    final IAST factors = denominator.isTimes() ? (IAST) denominator : F.Times(denominator);
    for (IExpr factor : factors) {
      IExpr base = factor;
      int e = 1;
      if (factor.isPower() && factor.exponent().isInteger()) {
        e = factor.exponent().toIntDefault();
        if (e < 1) {
          return null;
        }
        base = factor.base();
      }
      if (base.isFree(x, true)) {
        final IExpr value = engine.evaluate(F.Power(base, F.ZZ(e)));
        if (!value.isRational() && !(value instanceof IComplex)) {
          return null;
        }
        constant = (INumber) constant.times(value);
        continue;
      }
      if (!isPolynomial(base)) {
        return null;
      }
      final IExpr[] primitive = primitive(engine.evaluate(F.Expand(base)));
      if (primitive == null) {
        return null;
      }
      constant = (INumber) constant.times(engine.evaluate(F.Power(primitive[0], F.ZZ(e))));
      result.add(new IExpr[] {primitive[1], F.ZZ(e)});
    }
    return constant;
  }

  /** Add <code>p</code> to the basis, refining it so that its elements stay pairwise coprime. */
  private void insert(IExpr p) {
    final List<IExpr> pending = new ArrayList<>();
    pending.add(p);
    while (!pending.isEmpty()) {
      final IExpr q = pending.remove(pending.size() - 1);
      boolean split = false;
      for (int i = 0; i < basis.size(); i++) {
        final IExpr b = basis.get(i);
        // two distinct primitive linear polynomials with positive leading coefficients are coprime
        final IExpr g =
            b.equals(q) ? b : (degree(q) == 1 && degree(b) == 1 ? null : gcd(q, b));
        if (g != null) {
          basis.remove(i);
          pending.add(g);
          addQuotient(pending, b, g);
          addQuotient(pending, q, g);
          split = true;
          break;
        }
      }
      if (!split) {
        basis.add(q);
      }
    }
  }

  private void addQuotient(List<IExpr> pending, IExpr p, IExpr g) {
    if (!p.equals(g)) {
      final IExpr[] quotient = primitive(engine.evaluate(F.PolynomialQuotient(p, g, x)));
      if (quotient != null) {
        pending.add(quotient[1]);
      }
    }
  }

  /**
   * Add the multiplicities of the basis elements in <code>piece^e</code> to
   * <code>valuation</code>.
   *
   * @return <code>false</code> if <code>piece</code> isn't a product of basis elements
   */
  private boolean valuate(IExpr piece, int e, int[] valuation, Map<IExpr, Integer> index) {
    final Integer i = index.get(piece);
    if (i != null) {
      valuation[i] += e;
      return true;
    }
    IExpr rest = piece;
    for (int k = 0; k < basis.size() && degree(rest) > 0; k++) {
      final IExpr b = basis.get(k);
      while (degree(rest) >= degree(b)
          && engine.evaluate(F.PolynomialRemainder(rest, b, x)).isZero()) {
        rest = engine.evaluate(F.PolynomialQuotient(rest, b, x));
        valuation[k] += e;
      }
    }
    return degree(rest) == 0;
  }

  /** The primitive nonconstant gcd of <code>a</code> and <code>b</code>, or <code>null</code>. */
  private IExpr gcd(IExpr a, IExpr b) {
    final IExpr g = engine.evaluate(F.PolynomialGCD(a, b));
    if (degree(g) < 1) {
      return null;
    }
    final IExpr[] primitive = primitive(g);
    return primitive == null ? null : primitive[1];
  }

  private boolean isPolynomial(IExpr p) {
    return engine.evaluate(F.PolynomialQ(p, x)).isTrue();
  }

  private int degree(IExpr p) {
    return degrees.computeIfAbsent(p, q -> engine.evaluate(F.Exponent(q, x)).toIntDefault());
  }

  /**
   * The numerator over the common denominator and the cancellation of its common factors, with
   * polynomials over the rationals: the common denominator divided by each term's denominator
   * times the term's numerator, instead of an expansion of every product of all the other
   * denominators. A linear factor <code>a*x+b</code> divides the numerator if the numerator
   * vanishes at <code>-b/a</code>.
   *
   * @return <code>{numerator, list of the denominator's factors}</code>, or <code>null</code> if a
   *         polynomial has no rational coefficients
   */
  private IExpr[] cancelOverRationals(IAST numerators, INumber[] constants, int[][] valuations,
      int[] exponents) {
    try {
      final JASConvert<BigRational> jas = new JASConvert<BigRational>(F.list(x), BigRational.ZERO);
      final List<GenPolynomial<BigRational>> factors = new ArrayList<>(basis.size());
      for (IExpr b : basis) {
        final GenPolynomial<BigRational> p = jas.expr2JAS(b, false);
        if (p == null) {
          return null;
        }
        factors.add(p);
      }
      final GenPolynomial<BigRational> one = jas.expr2JAS(F.C1, false);
      GenPolynomial<BigRational> common = one;
      for (int i = 0; i < factors.size(); i++) {
        common = common.multiply(factors.get(i).power(exponents[i]));
      }
      GenPolynomial<BigRational> numerator = one.ring.getZERO();
      for (int t = 0; t < valuations.length; t++) {
        engine.checkTimeLimit();
        final GenPolynomial<BigRational> n = jas.expr2JAS(numerators.get(t + 1), false);
        if (n == null) {
          return null;
        }
        GenPolynomial<BigRational> d = one;
        for (int i = 0; i < factors.size(); i++) {
          if (valuations[t][i] > 0) {
            d = d.multiply(factors.get(i).power(valuations[t][i]));
          }
        }
        if (!(constants[t] instanceof IRational)) {
          return null;
        }
        final IRational c = (IRational) constants[t];
        final BigRational inverse = BigRational.reduction(c.toBigDenominator(), c.toBigNumerator());
        numerator = numerator.sum(n.multiply(common.divide(d)).multiply(inverse));
      }
      if (numerator.isZERO()) {
        return new IExpr[] {F.C0, F.CEmptyList};
      }

      final List<IExpr> forms = new ArrayList<>(basis);
      final List<Integer> multiplicities = new ArrayList<>();
      for (int e : exponents) {
        multiplicities.add(e);
      }
      final GreatestCommonDivisor<BigRational> gcdEngine =
          GCDFactory.getImplementation(BigRational.ONE);
      for (int i = 0; i < factors.size(); i++) {
        while (multiplicities.get(i) > 0) {
          final GenPolynomial<BigRational> b = factors.get(i);
          if (b.degree() == 1) {
            if (!PolyUtil.<BigRational>evaluateMain(BigRational.ZERO, numerator, linearRoot(b))
                .isZERO()) {
              break;
            }
            numerator = numerator.divide(b);
            multiplicities.set(i, multiplicities.get(i) - 1);
            continue;
          }
          final GenPolynomial<BigRational> g = gcdEngine.gcd(numerator, b);
          if (g.isConstant()) {
            break;
          }
          if (g.degree() == b.degree()) {
            numerator = numerator.divide(b);
            multiplicities.set(i, multiplicities.get(i) - 1);
          } else {
            // b^m = c^m*g^m*rest^m with primitive g and rest: the numerator takes 1/c^m, so that
            // the factors stay the polynomials their forms show
            final AlgebraUtil.FactorSplit split = new AlgebraUtil.FactorSplit(jas, b, g);
            numerator = numerator.multiply(split.content.inverse().power(multiplicities.get(i)));
            factors.set(i, split.divisor);
            forms.set(i, engine.evaluate(jas.rationalPoly2Expr(split.divisor, false)));
            factors.add(split.rest);
            forms.add(engine.evaluate(jas.rationalPoly2Expr(split.rest, false)));
            multiplicities.add(multiplicities.get(i));
          }
        }
      }
      final IASTAppendable denominator = F.ListAlloc(forms.size());
      for (int i = 0; i < forms.size(); i++) {
        final int e = multiplicities.get(i);
        if (e > 0) {
          denominator.append(e == 1 ? forms.get(i) : F.Power(forms.get(i), F.ZZ(e)));
        }
      }
      return new IExpr[] {engine.evaluate(jas.rationalPoly2Expr(numerator, false)), denominator};
    } catch (LimitException le) {
      throw le;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    }
  }

  /** The root <code>-b/a</code> of the linear polynomial <code>a*x+b</code>. */
  private static BigRational linearRoot(GenPolynomial<BigRational> p) {
    BigRational a = BigRational.ZERO;
    BigRational b = BigRational.ZERO;
    for (Monomial<BigRational> m : p) {
      if (m.exponent().getVal(0) == 1L) {
        a = m.coefficient();
      } else {
        b = m.coefficient();
      }
    }
    return b.negate().divide(a);
  }

  /**
   * <code>{content, primitive part}</code> of a polynomial with rational coefficients, the
   * primitive part with integer coefficients and a positive leading coefficient, or
   * <code>null</code> if a coefficient isn't rational. With Gaussian rational coefficients the
   * primitive part has Gaussian integer coefficients without a common divisor and a leading
   * coefficient in the first quadrant: <code>1-2*I*x</code> is <code>-I*(I+2*x)</code>.
   */
  private IExpr[] primitive(IExpr p) {
    final long[] degree = new long[1];
    final List<IExpr> coefficients = coefficients(p, degree);
    if (coefficients == null || degree[0] < 1) {
      return null;
    }
    for (IExpr c : coefficients) {
      if (c instanceof IComplex) {
        return gaussianPrimitive(p, coefficients);
      }
    }
    BigInteger lcm = BigInteger.ONE;
    BigInteger gcd = BigInteger.ZERO;
    for (IExpr c : coefficients) {
      if (!c.isRational()) {
        return null;
      }
      lcm = lcm(lcm, ((IRational) c).toBigDenominator());
      gcd = gcd.gcd(((IRational) c).toBigNumerator());
    }
    IRational content = F.QQ(gcd, lcm).normalize();
    if (coefficients.get(coefficients.size() - 1).isNegative()) {
      content = content.negate();
    }
    if (content.isOne()) {
      return new IExpr[] {content, p};
    }
    return new IExpr[] {content, engine.evaluate(F.Expand(F.Times(content.inverse(), p)))};
  }

  /** {@link #primitive(IExpr)} of a polynomial with a non-real Gaussian rational coefficient. */
  private IExpr[] gaussianPrimitive(IExpr p, List<IExpr> coefficients) {
    BigInteger lcm = BigInteger.ONE;
    for (IExpr c : coefficients) {
      if (c.isRational()) {
        lcm = lcm(lcm, ((IRational) c).toBigDenominator());
      } else if (c instanceof IComplex) {
        lcm = lcm(lcm, ((IComplex) c).re().toBigDenominator());
        lcm = lcm(lcm, ((IComplex) c).im().toBigDenominator());
      } else {
        return null;
      }
    }
    final IASTAppendable scaled = F.ListAlloc(coefficients.size());
    for (IExpr c : coefficients) {
      scaled.append(engine.evaluate(F.Times(F.ZZ(lcm), c)));
    }
    final IExpr gcd = engine.evaluate(scaled.setAtCopy(0, S.GCD));
    if (!gcd.isInteger() && !(gcd instanceof IComplex) || gcd.isZero()) {
      return null;
    }
    // the unit which turns the leading coefficient into the first quadrant
    final IExpr lead = engine.evaluate(F.Divide(scaled.get(scaled.size() - 1), gcd));
    IExpr unit = F.C1;
    for (IExpr u : new IExpr[] {F.C1, F.CI, F.CN1, F.CNI}) {
      final IExpr rotated = engine.evaluate(F.Times(u, lead));
      final IExpr re = engine.evaluate(F.Re(rotated));
      final IExpr im = engine.evaluate(F.Im(rotated));
      if (re.isPositive() && !im.isNegative()) {
        unit = u;
        break;
      }
    }
    final IExpr content = engine.evaluate(F.Divide(gcd, F.Times(F.ZZ(lcm), unit)));
    if (!content.isNumber()) {
      return null;
    }
    if (content.isOne()) {
      return new IExpr[] {content, p};
    }
    return new IExpr[] {content, engine.evaluate(F.Expand(F.Divide(p, content)))};
  }

  /**
   * The coefficients of the terms of the polynomial <code>p</code>, the one of the highest power
   * of <code>x</code> in the last place. These are the entries of <code>CoefficientList</code>
   * without its zeros: the list of <code>1+x^100000000</code> has a hundred million entries for
   * two terms.
   *
   * @param degree receives the degree of <code>p</code> at index 0
   * @return <code>null</code> if <code>p</code> is no polynomial in <code>x</code>
   */
  private List<IExpr> coefficients(IExpr p, long[] degree) {
    try {
      final IExpr expanded = p.isAST() && !p.isFree(x, true) ? F.evalExpandAll(p, engine) : p;
      final ExprPolynomial polynomial =
          new ExprPolynomialRing(F.list(x)).create(expanded, true, false, true);
      final List<IExpr> result = new ArrayList<>(polynomial.length());
      long highest = -1L;
      IExpr leading = null;
      for (ExprMonomial monomial : polynomial) {
        final long exponent = monomial.exponent().getVal(0);
        if (exponent > highest) {
          if (leading != null) {
            result.add(leading);
          }
          highest = exponent;
          leading = monomial.coefficient();
        } else {
          result.add(monomial.coefficient());
        }
      }
      if (leading != null) {
        result.add(leading);
      }
      degree[0] = Math.max(highest, 0L);
      return result;
    } catch (LimitException le) {
      throw le;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    }
  }

  private static BigInteger lcm(BigInteger a, BigInteger b) {
    if (b.equals(BigInteger.ONE)) {
      return a;
    }
    return a.divide(a.gcd(b)).multiply(b);
  }
}
