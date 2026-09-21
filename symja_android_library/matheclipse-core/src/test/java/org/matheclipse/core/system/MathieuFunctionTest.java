package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * <code>MathieuC</code>, <code>MathieuS</code> and their derivatives.
 *
 * <p>
 * The numeric values are those of the Floquet solution with <code>Sum(Abs(c(k))^2) == 1</code>,
 * checked against an independent 70 digit mpmath computation. Everything else - the Wronskian, the
 * differential equation, the symmetries - holds for any normalization.
 */
public class MathieuFunctionTest extends ExprEvaluatorTestCase {

  @Test
  public void testMathieuC() {
    checkNumeric("MathieuC(2, 1, 0.)", //
        "1.1824427065730964");
    checkNumeric("MathieuC(2, 1, 3.2)", //
        "-0.8995311634414614");
    checkNumeric("N(MathieuC(2, 1, 37/10), 50)", //
        "-0.70674734130376727046760504036431438649802416523");
    // a negative characteristic value
    checkNumeric("MathieuC(-1, 1, 1.5)", //
        "2.4577439674998856");
    // inside an instability gap
    checkNumeric("MathieuC(10, 50, 1.3)", //
        "-22.18179551098821");
    checkNumeric("MathieuC(I, -2, 2.5 + I)", //
        "5.398058588653733+I*(-36.3139589176273)");
    // the Fourier series is periodic up to the Floquet factor, a large argument costs nothing
    checkNumeric("MathieuC(2., 1., 100.)", //
        "-1.0645307480060517");
    check("MathieuC(2, 1, {0.5,1.5})", //
        "{1.15872,0.0359219}");
  }

  @Test
  public void testMathieuS() {
    checkNumeric("MathieuS(2, 1, 3.2)", //
        "-0.7680982888758983");
    checkNumeric("N(MathieuS(2, 1, 1/5), 30)", //
        "0.106689298010009061172431792922");
    checkNumeric("MathieuS(10, 1, 1.)", //
        "0.1372608567882371");
    // as Sin(Sqrt(a)*z) is for a negative a
    checkNumeric("MathieuS(-1, 1, 1.5)", //
        "I*2.1151354501167512");
    // next to the origin
    checkNumeric("MathieuS(2, 1, 1.0*^-12)/1.0*^-12", //
        "0.5336161514292");
  }

  @Test
  public void testPrime() {
    checkNumeric("MathieuCPrime(2, 1, 3.2)", //
        "0.33593784114337855");
    checkNumeric("MathieuSPrime(2, 1, 0.)", //
        "0.5336161514292013");
    // the Wronskian is constant
    check(
        "Chop(MathieuC(2.0,1.0,0.7)*MathieuSPrime(2.0,1.0,0.7)-MathieuCPrime(2.0,1.0,0.7)*MathieuS(2.0,1.0,0.7)"
            + "-MathieuC(2.0,1.0,0.0)*MathieuSPrime(2.0,1.0,0.0))", //
        "0");
    // the derivative, by a central difference
    check(
        "Chop((MathieuC(3.0,2.0,0.9+1.0*^-5)-MathieuC(3.0,2.0,0.9-1.0*^-5))/2.0*^-5-MathieuCPrime(3.0,2.0,0.9), 1.0*^-8)", //
        "0");
    check(
        "Chop((MathieuSPrime(3.0,2.0,0.9+1.0*^-5)-MathieuSPrime(3.0,2.0,0.9-1.0*^-5))/2.0*^-5"
            + "+(3.0-2*2.0*Cos(2*0.9))*MathieuS(3.0,2.0,0.9), 1.0*^-8)", //
        "0");
  }

  @Test
  public void testExact() {
    check("MathieuC(a,0,z)", //
        "Cos(Sqrt(a)*z)");
    check("MathieuS(a,0,z)", //
        "Sin(Sqrt(a)*z)");
    check("MathieuCPrime(a,0,z)", //
        "-Sqrt(a)*Sin(Sqrt(a)*z)");
    check("MathieuSPrime(a,0,z)", //
        "Sqrt(a)*Cos(Sqrt(a)*z)");
    check("MathieuC(2., 0., 0.5)-Cos(Sqrt(2.)*0.5)", //
        "0.0");

    check("{MathieuC(a,q,-z),MathieuS(a,q,-z),MathieuCPrime(a,q,-z),MathieuSPrime(a,q,-z)}", //
        "{MathieuC(a,q,z),-MathieuS(a,q,z),-MathieuCPrime(a,q,z),MathieuSPrime(a,q,z)}");
    check("{MathieuC(a,q,0),MathieuS(a,q,0),MathieuCPrime(a,q,0),MathieuSPrime(a,q,0)}", //
        "{MathieuC(a,q,0),0,0,MathieuSPrime(a,q,0)}");
  }

  @Test
  public void testDerivative() {
    check("D(MathieuC(a,q,z),z)", //
        "MathieuCPrime(a,q,z)");
    check("D(MathieuS(a,q,z),z)", //
        "MathieuSPrime(a,q,z)");
    check("D(MathieuC(a,q,z),{z,2})", //
        "(-a+2*q*Cos(2*z))*MathieuC(a,q,z)");
    check("D(MathieuS(a,q,z),{z,2})", //
        "(-a+2*q*Cos(2*z))*MathieuS(a,q,z)");
    check("Series(MathieuC(3,2,z),{z,0,4})", //
        "MathieuC(3,2,0)+1/2*MathieuC(3,2,0)*z^2-5/8*MathieuC(3,2,0)*z^4+O(z)^5");
  }

  @Test
  public void testMathieuCharacteristicA() {
    // Abramowitz/Stegun table 20.1; the other values are checked against mpmath eigenvalues
    check("MathieuCharacteristicA({0,1,2}, 1.0)", //
        "{-0.455139,1.85911,4.3713}");
    checkNumeric("N(MathieuCharacteristicA(1, 1/2), 40)", //
        "1.466766842516055774308238490098876885672");
    // non-integer exponent, checked against a 60 digit mpmath eigenvalue
    checkNumeric("N(MathieuCharacteristicA(3/2, 1), 40)", //
        "2.537180087119901695599980222737527197543");
    // an odd order changes class with the sign of q
    checkNumeric("MathieuCharacteristicA(1, -1.0)-MathieuCharacteristicB(1, 1.0)", //
        "0.0");
    // continuous across an integer exponent from the band below to the band above
    checkNumeric("MathieuCharacteristicA(0.999999, 1.0)", //
        "-0.11024881699326623");
    checkNumeric("MathieuCharacteristicA(1.000001, 1.0)", //
        "1.8591080725174663");
    checkNumeric("N(MathieuCharacteristicA(3/2, 1+I), 30)", //
        "2.60155579759453011180705202703+I*0.52090726462656625634301545334");
    // a(0) ~ -2*q+2*Sqrt(q)-1/4 for a large q
    checkNumeric("MathieuCharacteristicA(0, 1000.0)", //
        "-1937.005446936397");

    check("MathieuCharacteristicA(r, 0)", //
        "r^2");
    check("MathieuCharacteristicA(-r, q)", //
        "MathieuCharacteristicA(r,q)");
    // the characteristic value makes MathieuC periodic
    checkNumeric(
        "Chop(MathieuC(MathieuCharacteristicA(1, 1.), 1., 0.3)-MathieuC(MathieuCharacteristicA(1, 1.), 1., 0.3+2*Pi))", //
        "0");
  }

  @Test
  public void testMathieuCharacteristicB() {
    check("MathieuCharacteristicB({1,2}, 1.0)", //
        "{-0.110249,3.91702}");
    checkNumeric("MathieuCharacteristicB(1, 0.5)", //
        "0.4706543549338391");
    // the same as MathieuCharacteristicA for a non-integer exponent
    checkNumeric("MathieuCharacteristicB(1.5, 1.0)-MathieuCharacteristicA(1.5, 1.0)", //
        "0.0");
    check("MathieuCharacteristicB(r, 0)", //
        "r^2");
    check("MathieuCharacteristicB(-r, q)", //
        "MathieuCharacteristicB(r,q)");
    checkNumeric(
        "Chop(MathieuS(MathieuCharacteristicB(2, 1.), 1., 0.3)-MathieuS(MathieuCharacteristicB(2, 1.), 1., 0.3+Pi))", //
        "0");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testMathieuCharacteristicExponent() {
    checkNumeric("MathieuCharacteristicExponent(2, 0.5)", //
        "1.3695085696605283");
    // the monodromy Cos(Pi*nu) checked against an mpmath integration
    checkNumeric("N(MathieuCharacteristicExponent(17/3, 1/2), 30)", //
        "2.37472448001814669297498099483");
    // band 3: a(3) < 10 < b(4)
    checkNumeric("MathieuCharacteristicExponent(10.0, 1.0)", //
        "3.153348522822422");
    // below a(0), and in the gap between b(1) and a(1)
    checkNumeric("MathieuCharacteristicExponent(-1, 1.0)", //
        "I*0.8442462228408524");
    checkNumeric("MathieuCharacteristicExponent(1.0, 1.0)", //
        "1.0+I*0.45345353434748305");
    check("MathieuCharacteristicExponent(a, 0)", //
        "Sqrt(a)");

    // inverse of the characteristic values
    checkNumeric("MathieuCharacteristicA(MathieuCharacteristicExponent(3.0, 1.0), 1.0)", //
        "3.0");
    checkNumeric("MathieuCharacteristicA(MathieuCharacteristicExponent(-5.0, 3.0), 3.0)", //
        "-5.0");
    // b(3) is a band edge, where the exponent moves like Sqrt(a-b(3)): a rounding error of 1E-16
    // in the characteristic value leaves only 8 digits of the exponent
    check("Chop(MathieuCharacteristicExponent(N(MathieuCharacteristicB(3, 5), 40), 5) - 3, 10^-15)", //
        "0");
  }

  /** Complex parameters are followed by continuation from q == 0, which takes a while. */
  @Test
  @Tag(TestTags.SLOW)
  public void testMathieuCharacteristicComplex() {
    check("Chop(MathieuCharacteristicA(MathieuCharacteristicExponent(2.0, 1.0+I), 1.0+I)-2.0, 1.0*^-10)", //
        "0");
    check("Chop(MathieuCharacteristicA(MathieuCharacteristicExponent(2.0+I, 1.0), 1.0)-(2.0+I), 1.0*^-10)", //
        "0");
  }
}
