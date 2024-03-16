package org.matheclipse.core.system;

import static org.junit.Assert.assertTrue;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Test;
import org.matheclipse.core.expression.F;
import com.google.common.math.DoubleMath;

public class PolynomialFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testChebyshevT() {
    check("ChebyshevT(-1/2, z)", //
        "Cos(ArcCos(z)/2)");
    check("ChebyshevT(1/2, z)", //
        "Cos(ArcCos(z)/2)");
    check("ChebyshevT(1.5, 2+3*I)", //
        "0.692609+I*9.74575");
    check("ChebyshevT(8, x)", //
        "1-32*x^2+160*x^4-256*x^6+128*x^8");
    // TODO add non-integer args implementation
    // check("ChebyshevT(1 - I, 0.5)", "0.800143 + 1.08198 I");

    check("ChebyshevT(n,0)", //
        "Cos(1/2*n*Pi)");
    check("ChebyshevT({0,1,2,3,4}, x)", //
        "{1,x,-1+2*x^2,-3*x+4*x^3,1-8*x^2+8*x^4}");
    check("ChebyshevT({0,-1,-2,-3,-4}, x)", //
        "{1,x,-1+2*x^2,-3*x+4*x^3,1-8*x^2+8*x^4}");
    check("ChebyshevT(10, x)", //
        "-1+50*x^2-400*x^4+1120*x^6-1280*x^8+512*x^10");
  }

  @Test
  public void testChebyshevU() {
    check("ChebyshevU(4, -42)", //
        "49765969");
    // http://oeis.org/A001906
    check("Table(ChebyshevU(n-1, 3/2), {n, 0, 30})", //
        "{0,1,3,8,21,55,144,377,987,2584,6765,17711,46368,121393,317811,832040,2178309,\n"
            + "5702887,14930352,39088169,102334155,267914296,701408733,1836311903,4807526976,\n"
            + "12586269025,32951280099,86267571272,225851433717,591286729879,1548008755920}");

    check("ChebyshevU(1.5, 2+3*I)", //
        "1.70238+I*19.36013");
    check("ChebyshevU(8, x)", //
        "1-40*x^2+240*x^4-448*x^6+256*x^8");
    // TODO add non-integer args implementation
    // check("ChebyshevU(1 - I, 0.5)", "1.60029 + 0.721322 I");
    check("ChebyshevU(n, 1)", //
        "1+n");
    check("ChebyshevU({0,1,2,3,4,5}, x)", //
        "{1,2*x,-1+4*x^2,-4*x+8*x^3,1-12*x^2+16*x^4,6*x-32*x^3+32*x^5}");
    check("ChebyshevU(0, x)", //
        "1");
    check("ChebyshevU(1, x)", //
        "2*x");
    check("ChebyshevU(10, x)", //
        "-1+60*x^2-560*x^4+1792*x^6-2304*x^8+1024*x^10");
  }

  @Test
  public void testGegenbauerC() {
    checkNumeric("GegenbauerC(2/7, 5 - I, 2) //N", //
        "2.5811662779830957+I*(-0.1545632835277021)");
    check("N(GegenbauerC(1/6, 1/8, 7), 50)", //
        "0.74986248230619982209196563118394711854677907245854");
    check("GegenbauerC(0.333333333333333333, 7, 1)", //
        "2.67757341042435157");


    check("GegenbauerC({0,0,2/(1+Sqrt(5)),0},-0.8+I*1.2)", //
        "{ComplexInfinity,ComplexInfinity,1.17445+I*2.30854,ComplexInfinity}");
    check("GegenbauerC(2, 0.5)", //
        "-0.5");
    // checkNumeric("GegenbauerC(5,1/8,7) //N", //
    // "16839.531372070312");
    checkNumeric("GegenbauerC(5,1/8,7) //N", //
        "16839.53137207032");
    checkNumeric("Table(GegenbauerC(10, x), {x, 1, 5})", //
        "{1/5,262087/5,22619537/5,457470751/5,4517251249/5}");
    check("GegenbauerC({1/3, 1/2}, 1/6, 0)", //
        "{(2^(1/3)*Sqrt(Pi))/(Gamma(1/6)*Gamma(4/3)),(2*Sqrt(2*Pi)*Gamma(5/12))/(Sqrt(Pi)*Gamma(\n"
            + "1/6)*Gamma(1/4))}");
    check("GegenbauerC({1/3, 1/2}, 1/6, 1)", //
        "{Gamma(2/3)/(Gamma(1/3)*Gamma(4/3)),(2*Gamma(5/6))/(Sqrt(Pi)*Gamma(1/3))}");
    check("GegenbauerC(7/4, 2/3, -1)", //
        "ComplexInfinity");
    check("GegenbauerC(7/4, 1/3, -1)", //
        "((1+Sqrt(3))*Gamma(29/12))/(Sqrt(2)*Gamma(2/3)*Gamma(11/4))");

    // message Polynomial degree 101 exceeded
    check(
        "GegenbauerC(151,-9223372036854775807/9223372036854775808-I*9223372036854775808/9223372036854775807,z)", //
        "GegenbauerC(151,-9223372036854775807/9223372036854775808-I*9223372036854775808/\n"
            + "9223372036854775807,z)");

    check("GegenbauerC(3, l, z)", //
        "-2*l*(1+l)*z+4/3*l*(1+l)*(2+l)*z^3");
    check("GegenbauerC(4, l, z)", //
        "1/2*l*(1+l)+2*(-1-l)*l*(2+l)*z^2+2/3*l*(1+l)*(2+l)*(3+l)*z^4");
    check("GegenbauerC(0, l, z)", //
        "1");
    check("GegenbauerC(0, 0, z)", //
        "0");
    check("GegenbauerC(n, 0, z)", //
        "0");
    check("GegenbauerC(n, 1, z)", //
        "ChebyshevU(n,z)");
    check("GegenbauerC(n, 2, z)", //
        "((-2-n)*ChebyshevU(n,z)+(1+n)*z*ChebyshevU(1+n,z))/(2*(-1+z^2))");

    check("GegenbauerC(5,z)", //
        "2*z-8*z^3+32/5*z^5");
    check("GegenbauerC(1/2,z)", //
        "2*Sqrt(2)*Sqrt(1+z)");
    check("GegenbauerC(-1/2,z)", //
        "-2*Sqrt(2)*Sqrt(1+z)");
    check("GegenbauerC(v,0)", //
        "(2*Cos(1/2*Pi*v))/v");
    check("GegenbauerC(v,1)", //
        "2/v");
    check("GegenbauerC(v,-1)", //
        "(2*Cos(Pi*v))/v");
    check("GegenbauerC(v,i)", //
        "GegenbauerC(v,i)");

    check("GegenbauerC(0,z)", //
        "ComplexInfinity");
    check("GegenbauerC(1,z)", //
        "2*z");
    check("GegenbauerC(2,z)", //
        "-1+2*z^2");
    check("GegenbauerC(-v,z)", //
        "-GegenbauerC(v,z)");
    check("GegenbauerC(10,-z)", //
        "-1/5+10*z^2-80*z^4+224*z^6-256*z^8+512/5*z^10");
    check("GegenbauerC(11,-z)", //
        "2*z-40*z^3+224*z^5-512*z^7+512*z^9-2048/11*z^11");
    check("GegenbauerC(8,z)", //
        "1/4-8*z^2+40*z^4-64*z^6+32*z^8");
    check("GegenbauerC(3, 1 + I)", //
        "-22/3+I*10/3");
    check("GegenbauerC(2,a,z)", //
        "-a+2*a*(1+a)*z^2");
  }

  @Test
  public void testHermiteH() {
    checkNumeric("HermiteH({-1,2.987,0,1},-1009)", //
        "{HermiteH(-1,-1009),Indeterminate,1,-2018}");
    checkNumeric("HermiteH( 1 ,-1009)", //
        "-2018");
    checkNumeric("HermiteH(3.1, 5)", //
        "1177.0141932545182");
    checkNumeric("HermiteH(.71, .87)", //
        "1.5587154138967738");
    checkNumeric("N(HermiteH(2/3, 8/7),50)", //
        "1.7959788632385866394352905812486095222798196025734");
    checkNumeric("HermiteH(1.30000000000000000000000000, 3)", //
        "10.1610479715174332429613241");
    checkNumeric("HermiteH(5.3 + I, .8 + I)", //
        "4.818411899472347+I*157.49044439894877");

    check("HermiteH(i, x)", //
        "HermiteH(i,x)");
    check("HermiteH(8, x)", //
        "1680-13440*x^2+13440*x^4-3584*x^6+256*x^8");
    check("HermiteH(3, 1 + I)", //
        "-28+I*4");
    // TODO add non integer arg implementation
    // check("HermiteH(4.2, 2)", "");
    check("HermiteH(10, x)", //
        "-30240+302400*x^2-403200*x^4+161280*x^6-23040*x^8+1024*x^10");
  }

  @Test
  public void testJacobiP() {
    // TODO https://github.com/mtommila/apfloat/issues/31
    // check("JacobiP(-3, -0.5,-9223372036854775808/11,0.0)", //
    // "(-1)^n*JacobiP(n,b,a,z)");


    // JacobiP[] == (-1)^n JacobiP[n, b, a, z]
    check("JacobiP(n, a, b, -z)", //
        "(-1)^n*JacobiP(n,b,a,z)");

    check("JacobiP(2, a, b, z)", //
        "1/4*(1/2*(1+b)*(2+b)*(1-z)^2+(2+a)*(2+b)*(-1+z)*(1+z)+1/2*(1+a)*(2+a)*(1+z)^2)");

    check("N(JacobiP(5, 1/2, 1, 3))", //
        "3846.375");

    check("N(JacobiP(3.0, 1/2, 1, 7))", //
        "1844.0");

    // TODO improve numeric calculation
    check("N(JacobiP(2.3, 1/2, 1/8, 12))", //
        "752.0365");

  }

  @Test
  public void testLaguerreL() {
    // test for java.util.TimSort: IllegalArgumentException - Comparison method violates its general
    // contract!
    check("LaguerreL(42,-1+I)", //
        "1702541892882333422180162984132589351201806337983/\n" //
            + "31902815449870879276437818578186752000000000+\n" //
            + "I*59637827489058046482179573168446612471309160814317/\n" //
            + "669959124447288464805194190141921792000000000");

    assertTrue(DoubleMath.isMathematicalInteger(-8.3848836698679782E17));
    BigInteger bigInteger = BigDecimal.valueOf(8.3848836698679782E17).toBigInteger();
    int intDefault = F.num(8.3848836698679782E17).toIntDefault();
    System.out.println(intDefault);
    System.out.println(bigInteger);

    check("LaguerreL(-9223372036854775808/11,0.5,1317624576693539401)", //
        "Indeterminate");
    checkNumeric("Table(LaguerreL(10, x), {x, 1, 5})", //
        "{168919/403200,-4381/14175,-31361/44800,931/675,254927/145152}");
    check("LaguerreL(0,0)", //
        "1");
    check("LaguerreL(0,0,0)", //
        "1");

    // TODO
    check("LaguerreL(2.3000000000000000000000000, 3, 3) ", //
        "-1.1129298068672651031450569");
    check("N(LaguerreL(2/5, 1/3, 8/7),50)", //
        "0.6882185639283528564455376412451570716445571748401");

    checkNumeric("LaguerreL(6.1, 5)", //
        "-1.8746624909969782");
    checkNumeric("LaguerreL(2.5,8+I)", //
        "3.2907356941472+I*(-5.884150638656437)");
    check("LaguerreL(100, 0, N(5, 20))", //
        "1.4555271634781800199");


    check("LaguerreL(3, .51, .87)", //
        "-0.396576");

    // TODO improve error messages
    check("LaguerreL(10,l,Infinity)", //
        "Indeterminate");
    check("LaguerreL(3,l,-Infinity)", //
        "1/3*(-(2+l)*(Infinity+l)+1/2*(Infinity+l)*(Infinity+3*l+Infinity*l+l^2))");
    // mesage Polynomial degree 101 exceeded
    check("LaguerreL(101,l,-Infinity)", //
        "LaguerreL(101,l,-Infinity)");
    // message Polynomial degree 101 exceeded
    check("LaguerreL(101,l,-I)", //
        "LaguerreL(101,l,-I)");
    // message Polynomial degree 1009 exceeded
    check("LaguerreL(1009, 7)", //
        "LaguerreL(1009,7)");

    check("LaguerreL(1, 1 - b, -z)", //
        "2-b+z");
    check("LaguerreL(1, a, z)", //
        "1+a-z");
    check("LaguerreL(0, l, z)", //
        "1");
    check("LaguerreL(2, l, z)", // 1/2*(-1-l)+1/2*(1+l-z)*(3+l-z)
        "1/2*(2+3*l+l^2-4*z-2*l*z+z^2)");
    check("LaguerreL(3, l, z)", //
        "1/3*(-(2+l)*(1+l-z)+1/2*(5+l-z)*(2+3*l+l^2-4*z-2*l*z+z^2))");
    check("LaguerreL(4, l, z)", //
        "1/4*(1/2*(-3-l)*(2+3*l+l^2-4*z-2*l*z+z^2)+1/3*(7+l-z)*(-(2+l)*(1+l-z)+1/2*(5+l-z)*(\n"
            + "2+3*l+l^2-4*z-2*l*z+z^2)))");
    check("LaguerreL(n, 0)", //
        "LaguerreL(n,0)");
    check("LaguerreL(8, x)", //
        "1-8*x+14*x^2-28/3*x^3+35/12*x^4-7/15*x^5+7/180*x^6-x^7/630+x^8/40320");
    // TODO add non-integer implementation
    // check("LaguerreL(3/2, 1.7)", "");
    check("LaguerreL(3, x)", //
        "1-3*x+3/2*x^2-x^3/6");
    check("LaguerreL(4, x)", //
        "1-4*x+3*x^2-2/3*x^3+x^4/24");
    check("LaguerreL(5, x)", //
        "1-5*x+5*x^2-5/3*x^3+5/24*x^4-x^5/120");
    check("LaguerreL(0,z)", //
        "1");
    check("LaguerreL(-3,z)", //
        "LaguerreL(-3,z)");
  }

  @Test
  public void testLegendreP() {
    // TODO support negative values
    // check(
    // "LegendreP(-3,x)", //
    // "LegendreP(-3,x)");
    // check(
    // "Sqrt(Pi)/(Gamma((1 - Pi)/2) * Gamma(1 + Pi/2))", //
    // "Sqrt(Pi)/(Gamma(1/2*(1-Pi))*Gamma(1+Pi/2))");

    check("LegendreP(-(1/2), 1 - 2*z)", //
        "(2*EllipticK(z))/Pi");
    check("LegendreP(Pi,0)", //
        "Sqrt(Pi)/(Gamma(1/2*(1-Pi))*Gamma(1+Pi/2))");
    check("LegendreP(111,1)", //
        "1");
    check("LegendreP(4,x)", //
        "3/8-15/4*x^2+35/8*x^4");
    // TODO implement non integer args
    // check("LegendreP(5/2, 1.5) ", "x");

    check("LegendreP(0,x)", //
        "1");
    check("LegendreP(1,x)", //
        "x");
    check("LegendreP(2,x)", //
        "-1/2+3/2*x^2");
    check("LegendreP(7,x)", //
        "-35/16*x+315/16*x^3-693/16*x^5+429/16*x^7");
    check("LegendreP(10,x)", //
        "-63/256+3465/256*x^2-15015/128*x^4+45045/128*x^6-109395/256*x^8+46189/256*x^10");
  }

  @Test
  public void testLegendreQ() {
    // TODO control number of error message output
    // check("LegendreQ(1009,z)", //
    // "");

    // SLOW
    // message Polynomial degree 10007 exceeded
    check("LegendreQ(10007,z)", //
        "LegendreQ(10007,z)");

    check("LegendreQ(-(1/2), 2*z - 1)", //
        "EllipticK(z)");
    check("LegendreQ(-3,z)", //
        "ComplexInfinity");
    check("LegendreQ(1,z)", //
        "-1+z*(-Log(1-z)/2+Log(1+z)/2)");
    check("LegendreQ(2,z)", //
        "-3/2*z+1/2*(-1/2+3/2*z^2)*(-Log(1-z)+Log(1+z))");
    check("LegendreQ(3,z)", //
        "-1/6+5/3*(1/2-3/2*z^2)+1/2*(-3/2*z+5/2*z^3)*(-Log(1-z)+Log(1+z))");
    check("Expand(LegendreQ(4,z))", //
        "55/24*z-35/8*z^3-3/16*Log(1-z)+15/8*z^2*Log(1-z)-35/16*z^4*Log(1-z)+3/16*Log(1+z)-\n"
            + "15/8*z^2*Log(1+z)+35/16*z^4*Log(1+z)");
  }

  @Test
  public void testSphericalHarmonicY() {
    // TODO
    // check("SphericalHarmonicY(1317624576693539401,{x,-3,-1/2},10007,0.5)", //
    // "");
    check("SphericalHarmonicY(3,1,t,p)", //
        "(2*Sqrt(7/3)*E^(I*p)*(3/16*Sqrt(Pi)*Cos(t)-15/16*Sqrt(Pi)*Cos(t)^3)*Sec(t)*Sqrt(Sin(t)^\n"
            + "2))/Pi");

    check("SphericalHarmonicY(5, 1, 0, .3)", //
        "0.0");
    check("SphericalHarmonicY(1, 0.5, -5, Pi/3)", //
        "-0.0975801+I*(-0.0563379)");
    // TODO improve apfloat results
    check("N(SphericalHarmonicY(27, 5, Pi/4, 0),50)", //
        "-0.25492395213084562609690191673291502381506003049233");
    check("N(SphericalHarmonicY(2, 1, Pi/3, Pi),20 )", //
        "0.33452327177864458397");
    check("N(SphericalHarmonicY(2, 2, Pi/6, Pi/4.211111111111111111) )", //
        "0.00759663110648051733+I*0.09626928971703694314");
    check("N(SphericalHarmonicY(23, 5 - I, Pi/3, I + Pi) )", //
        "0.00760864+I*0.149675");
  }

}
