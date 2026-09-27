package org.matheclipse.core.eval;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

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
    final IRational[] constants = new IRational[terms];
    for (int t = 0; t < terms; t++) {
      final List<IExpr[]> termPieces = new ArrayList<>();
      final IRational constant = pieces(denominators.get(t + 1), termPieces);
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

    // the denominators of the numerator's (Gaussian) rational coefficients go to the denominator
    BigInteger scale = BigInteger.ONE;
    final IExpr coefficients = engine.evaluate(F.CoefficientList(numerator, x));
    if (coefficients.isList()) {
      for (IExpr c : (IAST) coefficients) {
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
    for (int i = 0; i < factors.size(); i++) {
      final int e = multiplicities.get(i);
      if (e > 0) {
        denominator.append(e == 1 ? factors.get(i) : F.Power(factors.get(i), F.ZZ(e)));
      }
    }
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
  private IRational pieces(IExpr denominator, List<IExpr[]> result) {
    IRational constant = F.C1;
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
        if (!value.isRational()) {
          return null;
        }
        constant = constant.multiply((IRational) value);
        continue;
      }
      if (!isPolynomial(base)) {
        return null;
      }
      final IExpr[] primitive = primitive(engine.evaluate(F.Expand(base)));
      if (primitive == null) {
        return null;
      }
      constant = constant.multiply(((IRational) primitive[0]).powerRational(e));
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
        final IExpr g = b.equals(q) ? b : gcd(q, b);
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
    return engine.evaluate(F.Exponent(p, x)).toIntDefault();
  }

  /**
   * <code>{content, primitive part}</code> of a polynomial with rational coefficients, the
   * primitive part with integer coefficients and a positive leading coefficient, or
   * <code>null</code> if a coefficient isn't rational.
   */
  private IExpr[] primitive(IExpr p) {
    final IExpr coefficients = engine.evaluate(F.CoefficientList(p, x));
    if (!coefficients.isList() || coefficients.argSize() < 2) {
      return null;
    }
    BigInteger lcm = BigInteger.ONE;
    BigInteger gcd = BigInteger.ZERO;
    for (IExpr c : (IAST) coefficients) {
      if (!c.isRational()) {
        return null;
      }
      lcm = lcm(lcm, ((IRational) c).toBigDenominator());
      gcd = gcd.gcd(((IRational) c).toBigNumerator());
    }
    IRational content = F.QQ(gcd, lcm).normalize();
    if (((IAST) coefficients).last().isNegative()) {
      content = content.negate();
    }
    if (content.isOne()) {
      return new IExpr[] {content, p};
    }
    return new IExpr[] {content, engine.evaluate(F.Expand(F.Times(content.inverse(), p)))};
  }

  private static BigInteger lcm(BigInteger a, BigInteger b) {
    if (b.equals(BigInteger.ONE)) {
      return a;
    }
    return a.divide(a.gcd(b)).multiply(b);
  }
}
