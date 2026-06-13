package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;

/**
 * Native integration of rational functions <code>P(x)/Q(x)</code> with rational number
 * coefficients.
 *
 * <p>
 * The algorithm performs:
 * <ol>
 * <li>polynomial division for the polynomial part,</li>
 * <li>Horowitz-Ostrogradsky reduction for the rational part of the antiderivative (avoiding any
 * polynomial factorization),</li>
 * <li>a Trager-style logarithmic part on the remaining square-free denominator: full partial
 * fractions over the irreducible factors of the denominator, with closed real
 * <code>Log/ArcTan/ArcTanh</code> forms for linear and quadratic factors and a <code>RootSum</code>
 * antiderivative for irreducible factors of degree &gt;= 3.</li>
 * </ol>
 *
 * <p>
 * Integrands with symbolic (non-rational) coefficients are rejected, so that the caller can fall
 * back to the Rubi rules.
 */
public class RationalIntegration {

  /** Maximum degree of the denominator polynomial. */
  private static final int MAX_DEGREE = 60;

  private RationalIntegration() {}

  /**
   * Integrate a rational function in <code>x</code>.
   *
   * @param integrand the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHM_RATIONAL) {
      return F.NIL;
    }
    if (!isRationalFunction(integrand, x)) {
      return F.NIL;
    }
    try {
      IExpr together = engine.evaluate(F.Together(integrand));
      IExpr numerExpr = engine.evaluate(F.Numerator(together));
      IExpr denomExpr = engine.evaluate(F.Denominator(together));

      JASConvert<BigRational> jas = new JASConvert<BigRational>(F.list(x), BigRational.ZERO);
      GenPolynomial<BigRational> numer = jas.expr2JAS(numerExpr, false);
      GenPolynomial<BigRational> denom = jas.expr2JAS(denomExpr, false);
      if (denom.isZERO() || denom.degree() > MAX_DEGREE) {
        return F.NIL;
      }
      if (denom.isConstant()) {
        // pure polynomial - integrate directly
        GenPolynomial<BigRational> p = numer.multiply(denom.leadingBaseCoefficient().inverse());
        return engine.evaluate(integratePolynomial(p, x));
      }

      GenPolynomialRing<BigRational> ring = denom.ring;
      GreatestCommonDivisor<BigRational> ufd = GCDFactory.getImplementation(BigRational.ZERO);

      // cancel common factors
      GenPolynomial<BigRational> g = ufd.gcd(numer, denom);
      if (!g.isConstant()) {
        g = g.monic();
        numer = PolyUtil.<BigRational>basePseudoDivide(numer, g);
        denom = PolyUtil.<BigRational>basePseudoDivide(denom, g);
      }
      // normalize: make the denominator monic
      BigRational lc = denom.leadingBaseCoefficient();
      if (!lc.isONE()) {
        denom = denom.monic();
        numer = numer.multiply(lc.inverse());
      }

      IASTAppendable result = F.PlusAlloc(8);

      // polynomial part
      GenPolynomial<BigRational>[] qr =
          PolyUtil.<BigRational>basePseudoQuotientRemainder(numer, denom);
      GenPolynomial<BigRational> quotient = qr[0];
      GenPolynomial<BigRational> remainder = qr[1];
      if (!quotient.isZERO()) {
        result.append(integratePolynomial(quotient, x));
      }
      if (remainder.isZERO()) {
        return engine.evaluate(result.oneIdentity0());
      }

      // Horowitz-Ostrogradsky reduction
      GenPolynomial<BigRational> dDeriv = PolyUtil.<BigRational>baseDerivative(denom);
      GenPolynomial<BigRational> dMinus = ufd.gcd(denom, dDeriv).monic();
      GenPolynomial<BigRational> a2; // numerator of the logarithmic part
      GenPolynomial<BigRational> d2; // square-free denominator of the logarithmic part
      if (dMinus.degree() > 0) {
        GenPolynomial<BigRational> dStar = PolyUtil.<BigRational>basePseudoDivide(denom, dMinus);
        GenPolynomial<BigRational>[] horowitz =
            horowitzOstrogradsky(remainder, denom, dMinus, dStar, ring);
        if (horowitz == null) {
          return F.NIL;
        }
        GenPolynomial<BigRational> aMinus = horowitz[0];
        a2 = horowitz[1];
        d2 = dStar;
        if (!aMinus.isZERO()) {
          // rational part Aminus/Dminus
          result.append(F.Divide(polyToExpr(aMinus, x), polyToExpr(dMinus, x)));
        }
      } else {
        a2 = remainder;
        d2 = denom;
      }

      // logarithmic part with square-free denominator d2
      if (!a2.isZERO() && d2.degree() > 0) {
        IExpr logPart = logarithmicPart(a2, d2, x, ring, ufd);
        if (logPart.isNIL()) {
          return F.NIL;
        }
        result.append(logPart);
      }
      return engine.evaluate(result.oneIdentity0());
    } catch (JASConversionException jce) {
      // symbolic coefficients - fall through to Rubi
      return F.NIL;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * Quick structural test whether <code>expr</code> is a rational function in <code>x</code> with
   * rational number coefficients.
   */
  public static boolean isRationalFunction(IExpr expr, IExpr x) {
    if (expr.isFree(x, true)) {
      return expr.isRational();
    }
    if (expr.equals(x)) {
      return true;
    }
    if (expr.isPlus() || expr.isTimes()) {
      IAST ast = (IAST) expr;
      for (int i = 1; i < ast.size(); i++) {
        if (!isRationalFunction(ast.get(i), x)) {
          return false;
        }
      }
      return true;
    }
    if (expr.isPower()) {
      IExpr exponent = expr.exponent();
      return exponent.isInteger() && isRationalFunction(expr.base(), x);
    }
    return false;
  }

  /**
   * Solve the Horowitz-Ostrogradsky linear system:
   *
   * <pre>
   * A = Aminus' * Dstar - Aminus * H + Astar * Dminus
   * </pre>
   *
   * with <code>H = (Dminus' * Dstar) / Dminus</code> (exact division), <code>deg(Aminus) &lt;
   * deg(Dminus)</code> and <code>deg(Astar) &lt; deg(Dstar)</code>.
   *
   * @return <code>[Aminus, Astar]</code> or <code>null</code> if the system could not be solved
   */
  private static GenPolynomial<BigRational>[] horowitzOstrogradsky(GenPolynomial<BigRational> a,
      GenPolynomial<BigRational> d, GenPolynomial<BigRational> dMinus,
      GenPolynomial<BigRational> dStar, GenPolynomialRing<BigRational> ring) {
    int m = (int) dMinus.degree();
    int s = (int) dStar.degree();
    int n = m + s; // == deg(d)
    if (n != (int) d.degree()) {
      return null;
    }
    GenPolynomial<BigRational> dMinusDeriv = PolyUtil.<BigRational>baseDerivative(dMinus);
    // H = (Dminus' * Dstar) / Dminus - exact division
    GenPolynomial<BigRational> hNumer = dMinusDeriv.multiply(dStar);
    GenPolynomial<BigRational>[] hqr =
        PolyUtil.<BigRational>basePseudoQuotientRemainder(hNumer, dMinus);
    if (!hqr[1].isZERO()) {
      return null;
    }
    GenPolynomial<BigRational> h = hqr[0];

    // build the linear system column by column
    BigRational[][] matrix = new BigRational[n][n + 1];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j <= n; j++) {
        matrix[i][j] = BigRational.ZERO;
      }
    }
    // columns 0..m-1 : coefficients a_j of Aminus
    for (int j = 0; j < m; j++) {
      GenPolynomial<BigRational> col = ring.getZERO();
      if (j > 0) {
        // j * x^(j-1) * Dstar
        col = dStar.multiply(ring.univariate(0, j - 1)).multiply(new BigRational(j));
      }
      // - x^j * H
      col = col.subtract(h.multiply(ring.univariate(0, j)));
      fillColumn(matrix, col, j, n);
    }
    // columns m..m+s-1 : coefficients b_j of Astar
    for (int j = 0; j < s; j++) {
      GenPolynomial<BigRational> col = dMinus.multiply(ring.univariate(0, j));
      fillColumn(matrix, col, m + j, n);
    }
    // right hand side: coefficients of A
    for (Monomial<BigRational> monomial : a) {
      int e = (int) monomial.exponent().getVal(0);
      if (e >= n) {
        return null;
      }
      matrix[e][n] = monomial.coefficient();
    }

    BigRational[] solution = solveLinearSystem(matrix, n);
    if (solution == null) {
      return null;
    }
    GenPolynomial<BigRational> aMinus = ring.getZERO();
    for (int j = 0; j < m; j++) {
      if (!solution[j].isZERO()) {
        aMinus = aMinus.sum(ring.univariate(0, j).multiply(solution[j]));
      }
    }
    GenPolynomial<BigRational> aStar = ring.getZERO();
    for (int j = 0; j < s; j++) {
      if (!solution[m + j].isZERO()) {
        aStar = aStar.sum(ring.univariate(0, j).multiply(solution[m + j]));
      }
    }
    @SuppressWarnings("unchecked")
    GenPolynomial<BigRational>[] result = new GenPolynomial[] {aMinus, aStar};
    return result;
  }

  private static void fillColumn(BigRational[][] matrix, GenPolynomial<BigRational> col, int column,
      int n) {
    for (Monomial<BigRational> monomial : col) {
      int e = (int) monomial.exponent().getVal(0);
      if (e < n) {
        matrix[e][column] = matrix[e][column].sum(monomial.coefficient());
      }
    }
  }

  /**
   * Gaussian elimination for an <code>n x (n+1)</code> augmented matrix over the rationals.
   *
   * @return the solution vector or <code>null</code> if the matrix is singular
   */
  private static BigRational[] solveLinearSystem(BigRational[][] matrix, int n) {
    for (int col = 0; col < n; col++) {
      // find pivot
      int pivot = -1;
      for (int row = col; row < n; row++) {
        if (!matrix[row][col].isZERO()) {
          pivot = row;
          break;
        }
      }
      if (pivot < 0) {
        return null;
      }
      if (pivot != col) {
        BigRational[] tmp = matrix[pivot];
        matrix[pivot] = matrix[col];
        matrix[col] = tmp;
      }
      BigRational inv = matrix[col][col].inverse();
      for (int j = col; j <= n; j++) {
        matrix[col][j] = matrix[col][j].multiply(inv);
      }
      for (int row = 0; row < n; row++) {
        if (row != col && !matrix[row][col].isZERO()) {
          BigRational factor = matrix[row][col];
          for (int j = col; j <= n; j++) {
            matrix[row][j] = matrix[row][j].subtract(factor.multiply(matrix[col][j]));
          }
        }
      }
    }
    BigRational[] solution = new BigRational[n];
    for (int i = 0; i < n; i++) {
      solution[i] = matrix[i][n];
    }
    return solution;
  }

  /**
   * Compute the logarithmic part <code>Integrate(a2/d2, x)</code> where <code>d2</code> is
   * square-free and <code>deg(a2) &lt; deg(d2)</code>.
   */
  private static IExpr logarithmicPart(GenPolynomial<BigRational> a2, GenPolynomial<BigRational> d2,
      IExpr x, GenPolynomialRing<BigRational> ring, GreatestCommonDivisor<BigRational> ufd) {
    // make monic
    BigRational lc = d2.leadingBaseCoefficient();
    if (!lc.isONE()) {
      d2 = d2.monic();
      a2 = a2.multiply(lc.inverse());
    }
    FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ZERO);
    SortedMap<GenPolynomial<BigRational>, Long> factorMap = factorAbstract.baseFactors(d2);
    List<GenPolynomial<BigRational>> factors = new ArrayList<>();
    for (SortedMap.Entry<GenPolynomial<BigRational>, Long> entry : factorMap.entrySet()) {
      GenPolynomial<BigRational> factor = entry.getKey();
      if (factor.isConstant()) {
        continue;
      }
      if (entry.getValue() != 1L) {
        // d2 should be square-free
        return F.NIL;
      }
      factors.add(factor.monic());
    }
    if (factors.isEmpty()) {
      return F.NIL;
    }
    IASTAppendable plus = F.PlusAlloc(factors.size() * 2);
    // collect `coeff * Log(poly)` terms; terms with equal coefficients are combined into
    // `coeff * Log(poly1*poly2*...)`, e.g. Log(1+x)/3+Log(1-x+x^2)/3 -> Log(1+x^3)/3
    Map<BigRational, GenPolynomial<BigRational>> logTerms = new TreeMap<>();
    for (GenPolynomial<BigRational> fi : factors) {
      // Gi = d2 / fi
      GenPolynomial<BigRational> gi = PolyUtil.<BigRational>basePseudoDivide(d2, fi);
      GenPolynomial<BigRational> ai = a2.remainder(fi);

      if (ai.isZERO()) {
        continue;
      }

      int degF = (int) fi.degree();

      if (degF == 1 || degF == 2) {
        // Explicit solutions need the fully evaluated Ni polynomial
        GenPolynomial<BigRational> giInverse = modInverse(gi.remainder(fi), fi);
        if (giInverse == null) {
          return F.NIL;
        }
        GenPolynomial<BigRational> ni = ai.multiply(giInverse).remainder(fi);

        if (ni.isZERO()) {
          continue;
        }

        if (degF == 1) {
          // fi = x + c0, Ni constant
          BigRational c = coefficient(ni, 0);
          logTerms.merge(c, fi, GenPolynomial::multiply);
        } else {
          // fi = x^2 + p*x + q ; Ni = B*x + C
          BigRational p = coefficient(fi, 1);
          BigRational q = coefficient(fi, 0);
          BigRational bCoeff = coefficient(ni, 1);
          BigRational cCoeff = coefficient(ni, 0);
          if (!bCoeff.isZERO()) {
            // (B/2)*Log(fi)
            logTerms.merge(bCoeff.divide(new BigRational(2)), fi, GenPolynomial::multiply);
          }
          // rem = C - B*p/2
          BigRational rem = cCoeff.subtract(bCoeff.multiply(p).divide(new BigRational(2)));
          if (!rem.isZERO()) {
            // d = q - p^2/4
            BigRational d = q.subtract(p.multiply(p).divide(new BigRational(4)));
            IExpr v = F.Plus(toFraction(p.divide(new BigRational(2))), x);
            if (d.signum() > 0) {
              // rem * 1/Sqrt(d) * ArcTan(v / Sqrt(d))
              IExpr sqrt = F.Sqrt(toFraction(d));
              plus.append(F.Times(toFraction(rem), F.Power(sqrt, F.CN1),
                  F.ArcTan(F.Times(v, F.Power(sqrt, F.CN1)))));
            } else {
              // d < 0 -> rem * (-1)/Sqrt(-d) * ArcTanh(v / Sqrt(-d))
              IExpr sqrt = F.Sqrt(toFraction(d.negate()));
              plus.append(F.Times(toFraction(rem), F.CN1, F.Power(sqrt, F.CN1),
                  F.ArcTanh(F.Times(v, F.Power(sqrt, F.CN1)))));
            }
          }
        }
      } else if (degF >= 5) {
        // RootSum for irreducible factors of degree >= 3:
        // Evaluate w(alpha) = ai(alpha) / (Gi(alpha) * fi'(alpha)) without expanding modInverse
        GenPolynomial<BigRational> fiDeriv = PolyUtil.<BigRational>baseDerivative(fi);

        IExpr aiInSlot = polyToExpr(ai, F.Slot1);
        IExpr fiDerivInSlot = polyToExpr(fiDeriv, F.Slot1);
        IExpr wInSlot;

        if (gi.isONE()) {
          wInSlot = F.Divide(aiInSlot, fiDerivInSlot);
        } else {
          IExpr giInSlot = polyToExpr(gi, F.Slot1);
          wInSlot = F.Divide(aiInSlot, F.Times(giInSlot, fiDerivInSlot));
        }

        IExpr polyInSlot = polyToExpr(fi, F.Slot1);
        IExpr body = F.Times(wInSlot, F.Log(F.Subtract(x, F.Slot1)));
        plus.append(F.RootSum(F.Function(polyInSlot), F.Function(body)));
      } else {
        // degF == 3 || degF == 4
        // The factor is irreducible over the rationals, but can be factored over the reals
        // using radicals (e.g. x^4+1). Return NIL to fall back to the Rubi rules engine,
        // which contains elegant closed-form solutions for degree 3 and 4 denominators.
        return F.NIL;
      }
    }
    for (Map.Entry<BigRational, GenPolynomial<BigRational>> entry : logTerms.entrySet()) {
      GenPolynomial<BigRational> p = normalizeLogArgument(entry.getValue());
      plus.append(F.Times(toFraction(entry.getKey()), F.Log(polyToExpr(p, x))));
    }
    return plus.oneIdentity0();
  }

  /**
   * Negate the polynomial if its trailing (lowest degree) coefficient is negative, so that
   * arguments of <code>Log</code> are normalized like <code>Log(3-x)</code> instead of
   * <code>Log(-3+x)</code>. The two antiderivative forms differ only by an integration constant
   * <code>Log(-1)</code>.
   */
  private static GenPolynomial<BigRational> normalizeLogArgument(GenPolynomial<BigRational> p) {
    BigRational trailing = p.trailingBaseCoefficient();
    return trailing.signum() < 0 ? p.negate() : p;
  }

  /**
   * Compute the inverse of <code>u</code> modulo <code>f</code> via the extended Euclidean
   * algorithm.
   *
   * @return the inverse or <code>null</code> if <code>u</code> is not invertible
   */
  private static GenPolynomial<BigRational> modInverse(GenPolynomial<BigRational> u,
      GenPolynomial<BigRational> f) {
    if (u.isZERO()) {
      return null;
    }
    GenPolynomial<BigRational>[] egcd = u.egcd(f);
    GenPolynomial<BigRational> g = egcd[0];
    if (!g.isConstant() || g.isZERO()) {
      return null;
    }
    // s*u + t*f = g => (s/g)*u == 1 (mod f)
    return egcd[1].multiply(g.leadingBaseCoefficient().inverse());
  }

  /** Coefficient of <code>x^exponent</code> in the univariate polynomial <code>p</code>. */
  private static BigRational coefficient(GenPolynomial<BigRational> p, int exponent) {
    for (Monomial<BigRational> monomial : p) {
      if ((int) monomial.exponent().getVal(0) == exponent) {
        return monomial.coefficient();
      }
    }
    return BigRational.ZERO;
  }

  /** Integrate a polynomial monomial-wise. */
  private static IExpr integratePolynomial(GenPolynomial<BigRational> p, IExpr x) {
    if (p.isZERO()) {
      return F.C0;
    }
    IASTAppendable plus = F.PlusAlloc(p.length());
    for (Monomial<BigRational> monomial : p) {
      int e = (int) monomial.exponent().getVal(0);
      BigRational coeff = monomial.coefficient().divide(new BigRational(e + 1));
      plus.append(F.Times(toFraction(coeff), F.Power(x, F.ZZ(e + 1))));
    }
    return plus.oneIdentity0();
  }

  /** Convert a univariate JAS polynomial to a Symja expression in the variable <code>var</code>. */
  private static IExpr polyToExpr(GenPolynomial<BigRational> p, IExpr var) {
    if (p.isZERO()) {
      return F.C0;
    }
    IASTAppendable plus = F.PlusAlloc(p.length());
    for (Monomial<BigRational> monomial : p) {
      BigRational coeff = monomial.coefficient();
      ExpVector exp = monomial.exponent();
      int e = (int) exp.getVal(0);
      if (e == 0) {
        plus.append(toFraction(coeff));
      } else if (e == 1) {
        if (toFraction(coeff).isOne()) {
          plus.append(var);
        } else {
          plus.append(F.Times(toFraction(coeff), var));
        }
      } else {
        if (toFraction(coeff).isOne()) {
          plus.append(F.Power(var, F.ZZ(e)));
        } else {
          plus.append(F.Times(toFraction(coeff), F.Power(var, F.ZZ(e))));
        }
      }
    }
    return plus.oneIdentity0();
  }

  private static IExpr toFraction(BigRational rational) {
    return F.QQ(rational.numerator(), rational.denominator());
  }
}
