package org.matheclipse.core.polynomials;

import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;

/** Generator to integrate a rational polynomial by partial fraction */
public class PartialFractionIntegrateGenerator implements IPartialFractionGenerator {
  IASTAppendable result;
  JASConvert<BigRational> jas;
  IExpr x;

  public PartialFractionIntegrateGenerator(IExpr x) {
    this.result = null;
    this.x = x;
  }

  /** {@inheritDoc} */
  @Override
  public void allocPlus(int size) {
    this.result = F.PlusAlloc(size);
  }

  @Override
  public void setJAS(JASConvert<BigRational> jas) {
    this.jas = jas;
  }

  @Override
  public IExpr getResult() {
    IExpr temp = result.oneIdentity0();
    if (temp.head().equals(S.Integrate)) {
      return F.NIL;
    }
    return temp;
  }

  @Override
  public void addNonFractionalPart(GenPolynomial<BigRational> genPolynomial) {
    IExpr temp = F.eval(jas.rationalPoly2Expr(genPolynomial, false));
    if (temp.isAST()) {
      ((IAST) temp).addEvalFlags(IAST.IS_DECOMPOSED_PARTIAL_FRACTION);
    }
    result.append(F.Integrate(temp, x));
  }

  @Override
  public void addSinglePartialFraction(
      GenPolynomial<BigRational> genPolynomial, GenPolynomial<BigRational> Di_1, int j) {
    if (!genPolynomial.isZERO()) {
      BigRational[] numer = new BigRational[3];
      BigRational[] denom = new BigRational[3];
      IExpr temp;
      boolean isDegreeLE2 = Di_1.degree() <= 2;
      if (isDegreeLE2 && j == 1L) {
        Object[] objects = jas.factorTerms(genPolynomial);
        java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
        java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
        GenPolynomial<edu.jas.arith.BigInteger> genPolynomial2 =
            ((GenPolynomial<edu.jas.arith.BigInteger>) objects[2])
                .multiply(edu.jas.arith.BigInteger.valueOf(gcd));
        if (genPolynomial2.isONE()) {
          GenPolynomial<BigRational> newDi_1 = Di_1.multiply(BigRational.valueOf(lcm));
          isQuadratic(newDi_1, denom);
          IFraction a = F.fraction(denom[2].numerator(), denom[2].denominator());
          IFraction b = F.fraction(denom[1].numerator(), denom[1].denominator());
          IFraction c = F.fraction(denom[0].numerator(), denom[0].denominator());
          if (a.isZero()) {
            // JavaForm[Log[b*x+c]/b]
            result.append(Times(Log(Plus(c, Times(b, x))), Power(b, CN1)));
          } else {
            // compute b^2-4*a*c from
            // (a*x^2+b*x+c)
            BigRational cmp =
                denom[1]
                    .multiply(denom[1])
                    .subtract(BigRational.valueOf(4L).multiply(denom[2]).multiply(denom[0]));
            int cmpTo = cmp.compareTo(BigRational.ZERO);
            // (2*a*x+b)
            IExpr ax2Plusb = F.Plus(F.Times(F.C2, a, x), b);
            if (cmpTo == 0) {
              // (-2) / (2*a*x+b)
              result.append(F.Times(F.CN2, F.Power(ax2Plusb, F.CN1)));
            } else if (cmpTo > 0) {
              // (b^2-4ac)^(1/2)
              temp = F.eval(F.Power(F.Subtract(F.Sqr(b), F.Times(F.C4, a, c)), F.C1D2));
              result.append(
                  F.Times(
                      F.Power(temp, F.CN1),
                      F.Log(
                          F.Times(
                              F.Subtract(ax2Plusb, temp), Power(F.Plus(ax2Plusb, temp), F.CN1)))));
            } else {
              // (4ac-b^2)^(1/2)
              temp = F.eval(F.Power(F.Subtract(F.Times(F.C4, a, c), F.Sqr(b)), F.CN1D2));
              result.append(F.Times(F.C2, temp, F.ArcTan(Times(ax2Plusb, temp))));
            }
          }
        } else {
          // (B*A*x) / (q*p*x)
          isQuadratic(genPolynomial, numer);
          IFraction A = F.fraction(numer[1].numerator(), numer[1].denominator());
          IFraction B = F.fraction(numer[0].numerator(), numer[0].denominator());
          isQuadratic(Di_1, denom);
          IFraction p = F.fraction(denom[1].numerator(), denom[1].denominator());
          IFraction q = F.fraction(denom[0].numerator(), denom[0].denominator());
          if (A.isZero() && !p.isZero()) {
            // JavaForm[B*Log[p*x+q]/p]
            if (q.isNegative()) {
              temp = Times(B, Log(Plus(q.negate(), Times(p.negate(), x))), Power(p, CN1));
            } else {
              temp = Times(B, Log(Plus(q, Times(p, x))), Power(p, CN1));
            }
          } else {
            // JavaForm[A/2*Log[x^2+p*x+q]+(2*B-A*p)/(4*q-p^2)^(1/2)*ArcTan[(2*x+p)/(4*q-p^2)^(1/2)]]
            temp =
                Plus(
                    Times(C1D2, A, Log(Plus(q, Times(p, x), Power(x, C2)))),
                    Times(
                        ArcTan(
                            Times(
                                Plus(p, Times(C2, x)),
                                Power(Plus(Times(CN1, Power(p, C2)), Times(C4, q)), CN1D2))),
                        Plus(Times(C2, B), Times(CN1, A, p)),
                        Power(Plus(Times(CN1, Power(p, C2)), Times(C4, q)), CN1D2)));
          }
          result.append(F.eval(temp));
        }
      } else if (isDegreeLE2 && j > 1L) {
        isQuadratic(genPolynomial, numer);
        IFraction A = F.fraction(numer[1].numerator(), numer[1].denominator());
        IFraction B = F.fraction(numer[0].numerator(), numer[0].denominator());
        isQuadratic(Di_1, denom);
        IFraction a = F.fraction(denom[2].numerator(), denom[2].denominator());
        IFraction b = F.fraction(denom[1].numerator(), denom[1].denominator());
        IFraction c = F.fraction(denom[0].numerator(), denom[0].denominator());
        IInteger k = F.ZZ(j);
        if (A.isZero()) {
          // JavaForm[B*((2*a*x+b)/((k-1)*(4*a*c-b^2)*(a*x^2+b*x+c)^(k-1))+
          // (4*k*a-6*a)/((k-1)*(4*a*c-b^2))*Integrate[(a*x^2+b*x+c)^(-k+1),x])]
          temp =
              Times(
                  B,
                  Plus(
                      Times(
                          Integrate(
                              Power(
                                  Plus(c, Times(b, x), Times(a, Power(x, C2))),
                                  Plus(C1, Times(CN1, k))),
                              x),
                          Plus(Times(F.ZZ(-6L), a), Times(C4, a, k)),
                          Power(Plus(CN1, k), CN1),
                          Power(Plus(Times(CN1, Power(b, C2)), Times(C4, a, c)), CN1)),
                      Times(
                          Plus(b, Times(C2, a, x)),
                          Power(Plus(CN1, k), CN1),
                          Power(Plus(Times(CN1, Power(b, C2)), Times(C4, a, c)), CN1),
                          Power(
                              Plus(c, Times(b, x), Times(a, Power(x, C2))),
                              Times(CN1, Plus(CN1, k))))));
        } else {
          // JavaForm[(-A)/(2*a*(k-1)*(a*x^2+b*x+c)^(k-1))+(B-A*b/(2*a))*Integrate[(a*x^2+b*x+c)^(-k),x]]
          temp =
              Plus(
                  Times(
                      Integrate(
                          Power(Plus(c, Times(b, x), Times(a, Power(x, C2))), Times(CN1, k)), x),
                      Plus(B, Times(CN1D2, A, Power(a, CN1), b))),
                  Times(
                      CN1D2,
                      A,
                      Power(a, CN1),
                      Power(Plus(CN1, k), CN1),
                      Power(
                          Plus(c, Times(b, x), Times(a, Power(x, C2))), Times(CN1, Plus(CN1, k)))));
        }
        result.append(F.eval(temp));
      } else {
        // ElementaryIntegration<BigRational> ei = new
        // ElementaryIntegration<BigRational>(BigRational.ZERO);
        // Integral<BigRational> integral= ei.integrate(genPolynomial,
        // Di_1);
        temp =
            F.eval(
                F.Times(
                    jas.rationalPoly2Expr(genPolynomial, false),
                    F.Power(jas.rationalPoly2Expr(Di_1, false), F.ZZ(j * (-1L)))));
        if (!temp.isZero()) {
          if (temp.isAST()) {
            ((IAST) temp).addEvalFlags(IAST.IS_DECOMPOSED_PARTIAL_FRACTION);
          }
          result.append(F.Integrate(temp, x));
        }
      }
    }
  }

  /**
   * Check if the polynomial has maximum degree 2 in 1 variable and return the coefficients.
   *
   * @param poly
   * @return <code>null</code> if the polynomials degree > 2 and number of variables <> 1
   */
  public static boolean isQuadratic(GenPolynomial<BigRational> poly, BigRational[] result) {
    if (poly.degree() <= 2 && poly.numberOfVariables() == 1) {
      result[0] = BigRational.ZERO;
      result[1] = BigRational.ZERO;
      result[2] = BigRational.ZERO;
      for (Monomial<BigRational> monomial : poly) {
        BigRational coeff = monomial.coefficient();
        ExpVector exp = monomial.exponent();
        for (int i = 0; i < exp.length(); i++) {
          result[(int) exp.getVal(i)] = coeff;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Check if the polynomial has maximum degree 2 in 1 variable and return the coefficients.
   *
   * @param poly
   * @return <code>null</code> if the polynomials degree > 2 and number of variables <> 1
   */
  public static boolean isQuadratic(GenPolynomial<BigInteger> poly, BigInteger[] result) {
    if (poly.degree() <= 2 && poly.numberOfVariables() == 1) {
      result[0] = BigInteger.ZERO;
      result[1] = BigInteger.ZERO;
      result[2] = BigInteger.ZERO;
      for (Monomial<BigInteger> monomial : poly) {
        BigInteger coeff = monomial.coefficient();
        ExpVector exp = monomial.exponent();
        for (int i = 0; i < exp.length(); i++) {
          result[(int) exp.getVal(i)] = coeff;
        }
      }
      return true;
    }
    return false;
  }
}
