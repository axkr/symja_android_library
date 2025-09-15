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
    // check("ChebyshevT(1317624576693539401,3.14159)", //
    // "ChebyshevT(1.31762*10^18,3.14159)");

    // https://github.com/mtommila/apfloat/issues/65
    check("N(ChebyshevT(Pi/2,-9223372036854775808/11),30)", //
        "4672660418534308965963828022.7+I*(-20661346583240968698114847518.9)");
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
        "2.5811662779830957+I*(-0.15456328352770213)");
    check("N(GegenbauerC(1/6, 1/8, 7), 50)", //
        "0.74986248230619982209196563118394711854677907245854");
    check("GegenbauerC(0.333333333333333333, 7, 1)", //
        "2.67757341042435161");


    check("GegenbauerC({0,0,2/(1+Sqrt(5)),0},-0.8+I*1.2)", //
        "{ComplexInfinity,ComplexInfinity,1.17445+I*2.30854,ComplexInfinity}");
    check("GegenbauerC(2, 0.5)", //
        "-0.5");
    checkNumeric("GegenbauerC(5,1/8,7) //N", //
        "16839.531372070312");
    checkNumeric("N(GegenbauerC(2/7, 5 - I, 2))", //
        "2.5811662779830957+I*(-0.15456328352770213)");
    checkNumeric("Table(GegenbauerC(10, x), {x, 1, 5})", //
        "{1/5,262087/5,22619537/5,457470751/5,4517251249/5}");
    check("GegenbauerC({1/3, 1/2}, 1/6, 0)", //
        "{(2^(1/3)*Sqrt(Pi))/(Gamma(1/6)*Gamma(4/3)),(2*Sqrt(2)*Gamma(5/12))/(Gamma(1/6)*Gamma(\n" //
            + "1/4))}");
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
        "1/2*l*(1+l)-2*l*(1+l)*(2+l)*z^2+2/3*l*(1+l)*(2+l)*(3+l)*z^4");
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
    // TODO interrupt long running apfloat calculations
    // checkNumeric("HermiteH(2.718281828459045,10007)", //
    // "");
    // TODO
    checkNumeric("HermiteH({-1,2.987,0,1},-1009)", //
        // "{HermiteH(-1,-1009),-1.3103460085601044*10^442134,1,-2018}");
        "{HermiteH(-1,-1009),-Infinity,1,-2018}");
    checkNumeric("HermiteH( 1 ,-1009)", //
        "-2018");
    checkNumeric("HermiteH(3.1, 5)", //
        "1177.0141932572576");
    checkNumeric("HermiteH(.71, .87)", //
        "1.558715413902994");
    checkNumeric("N(HermiteH(2/3, 8/7),50)", //
        "1.7959788632385866394352905812486095222798196025734");
    checkNumeric("HermiteH(1.30000000000000000000000000, 3)", //
        "10.1610479715174332429613241");
    checkNumeric("HermiteH(5.3 + I, .8 + I)", //
        "4.818411899082359+I*157.49044439917662");

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
    check("JacobiP(-1, 42, b, z)", //
        "0");

    // TODO https://github.com/mtommila/apfloat/issues/31
    // check("JacobiP(-3, -0.5,-9223372036854775808/11,0.0)", //
    // "(-1)^n*JacobiP(n,b,a,z)");

    checkNumeric("N(JacobiP(3, 1/2, 1, 7))", //
        "1844.0");
    check("N(JacobiP(2.3, 1/2, 1/8, 12))", //
        "752.0365");
    check("N(JacobiP(5, 1/2, 1, 3))", //
        "3846.375");
    check("N(JacobiP(2/3, 1/2, 9, 7), 50)", //
        "11.670621287206595077877422103270342556210833297972");
    check("JacobiP(2, 2, 9, .33333333333333333333333333333)", //
        "-0.999999999999999999999999999976");
    checkNumeric("JacobiP(2 + 3*I, 1.2, .8, 7)", //
        "32.01331219559314+I*179.3594719971409");

    // JacobiP[] == (-1)^n JacobiP[n, b, a, z]
    check("JacobiP(n, a, b, -z)", //
        "(-1)^n*JacobiP(n,b,a,z)");

    check("JacobiP(2, a, b, z)", //
        "1/4*(1/2*(1+b)*(2+b)*(1-z)^2+(2+a)*(2+b)*(-1+z)*(1+z)+1/2*(1+a)*(2+a)*(1+z)^2)");
    check("Table(JacobiP(3, 1, 2, z), {z, -1, 5})", //
        "{-10,1/2,4,127/2,242,1205/2,1208}");

  }

  @Test
  public void testLaguerreL() {
    check("LaguerreL(-3,z)", //
        "E^z*(1+2*z+z^2/2)");
    check("LaguerreL(-10,z)", //
        "E^z*(1+9*z+18*z^2+14*z^3+21/4*z^4+21/20*z^5+7/60*z^6+z^7/140+z^8/4480+z^9/362880)");
    check("LaguerreL(2,-1+b,z)", //
        "1/2*(b+b^2-2*z-2*b*z+z^2)");

    // TODO
    // check("LaguerreL(-9223372036854775808/11,-3.141592653589793)", //
    // " ");
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
        "0.68821856392835285644553764124515707164455717484011");

    checkNumeric("LaguerreL(6.1, 5)", //
        "-1.8746624910048282");
    checkNumeric("LaguerreL(2.5,8+I)", //
        "3.290735694157826+I*(-5.88415063864657)");
    check("LaguerreL(100, 0, N(5, 20))", //
        "1.455527163410546");


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
  }

  @Test
  public void testLegendreP() {
    check("LegendreP(10,2,z)", //
        "(1-z^2)*(3465/128-45045/32*z^2+675675/64*z^4-765765/32*z^6+2078505/128*z^8)");
    check("LegendreP(10,-2,z)", //
        "1/11880*(1-z^2)*(3465/128-45045/32*z^2+675675/64*z^4-765765/32*z^6+2078505/128*z^\n"
            + "8)");

    check("LegendreP(3,1,z)", //
        "(3/2-15/2*z^2)*Sqrt(1-z^2)");
    check("LegendreP(3,1,2,z)", //
        "(3/2-15/2*z^2)*Sqrt(1-z^2)");
    check("LegendreP(6,3,2,z)", //
        "(945/2*z-3465/2*z^3)*(1-z^2)^(3/2)");
    check("LegendreP(-7,4,2,z)", //
        "(1-z)^2*(-945/2+10395/2*z^2)*(1+z)^2");
    // TODO type 3 formula
    // check("LegendreP(3,1,3,z)", //
    // "Sqrt(-1+z)*(-3/2+15/2*z^2)*Sqrt(1+z)");
    // check("LegendreP(6,3,3,z)", //
    // "Sqrt(-1+z)*(-3/2+15/2*z^2)*Sqrt(1+z)");

    check("LegendreP(-3,1,z)", //
        "-3*z*Sqrt(1-z^2)");

    check("LegendreP(2,2,2)", //
        "-9");

    check("LegendreP(n,0,z)", //
        "LegendreP(n,z)");
    // message LegendreP: Not allowed to use file storage
    check("LegendreP(1317624576693539401,0.0)", //
        "LegendreP(1.31762*10^18,0.0)");
    // TODO support negative values
    // check(
    // "LegendreP(-3,x)", //
    // "LegendreP(-3,x)");
    // check(
    // "Sqrt(Pi)/(Gamma((1 - Pi)/2) * Gamma(1 + Pi/2))", //
    // "Sqrt(Pi)/(Gamma(1/2*(1-Pi))*Gamma(1+Pi/2))");

    check("{LegendreP(2, 2), LegendreP(2, 2, 2), LegendreP(1.5, 2)}//N", //
        "{5.5,-9.0,3.24394}");
    check("N(LegendreP(3/2, 2), 50)", //
        "3.2439396660408049154502287929704557672075154110176");
    check("LegendreP(3/2, 2.000000000000000000000000000)", //
        "3.243939666040804915450228792");
    checkNumeric("LegendreP(3/2 + I, 1.5 - I)", //
        "5.204659495086081+I*0.2994794549763052");


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
    check("LegendreQ(n,0,z)", //
        "LegendreQ(n,z)");
    checkNumeric("LegendreQ({x,3,3},(Modulus->10)[[2]])", //
        "{LegendreQ(x,10),-748/3+2485/2*(-I*Pi-Log(9)+Log(11)),-748/3+2485/2*(-I*Pi-Log(9)+Log(\n" //
            + "11))}");
    checkNumeric("LegendreQ(1/3, 0.5)", //
        "-0.03995329475988969");
    checkNumeric("{LegendreQ(2, 0.5), LegendreQ(2, 2, 0.5), LegendreQ(1/2, 0.5)}", //
        "{-0.8186632680417568,4.0692721580849565,-0.2655964076372758}");

    checkNumeric("N(LegendreQ(3/2, 1/2), 50)", //
        "-0.89590282092473162125852553313186422570428299415015");
    checkNumeric("LegendreQ(1 - I, 2 + I, 0.5)", //
        "9.486593283969226+I*5.441832617637285");


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
    check("SphericalHarmonicY(-6,5,a,b)", //
        "-3/32*(Sqrt(77)*E^(I*5*b)*Sin(a)^5)/Sqrt(Pi)");

    check("SphericalHarmonicY(-6,-5,a,b)", //
        "-3/32*(Sqrt(77)*Sin(a)^5)/(E^(I*5*b)*Sqrt(Pi))");
    check("SphericalHarmonicY(-6,-6,a,b)", //
        "0");

    check("SphericalHarmonicY(6,5,a,b)", //
        "-3/32*(Sqrt(1001)*E^(I*5*b)*Cos(a)*Sin(a)^5)/Sqrt(Pi)");
    check("SphericalHarmonicY(6,5,a,b)", //
        "-3/32*(Sqrt(1001)*E^(I*5*b)*Cos(a)*Sin(a)^5)/Sqrt(Pi)");
    check("SphericalHarmonicY(5,6,a,b)", //
        "0");
    check("SphericalHarmonicY(27, 5, Pi/4, 0)", //
        "-44580915/549755813888*Sqrt(62093031)/Sqrt(2*Pi)");
    check("N(SphericalHarmonicY(27, 5, Pi/4, 0),50)", //
        "-0.25492395213084562609690191673291502381506002910716");
    check("N(-44580915/549755813888*Sqrt(62093031)/Sqrt(2*Pi),50)", //
        "-0.25492395213084562609690191673291502381506002910716");

    // TODO
    // check("SphericalHarmonicY(1317624576693539401,{x,-3,-1/2},10007,0.5)", //
    // "");
    check("SphericalHarmonicY(3/4, 0.5, Pi/5, Pi/3)", //
        "0.254247+I*0.14679");
    checkNumeric("SphericalHarmonicY(3/4, 0.5, Pi/5, Pi/3)", //
        "0.2542473403526675+I*0.14678977039335897");
    check("SphericalHarmonicY(3,1,t,p)", //
        "1/8*E^(I*p)*Sqrt(21/Pi)*(1-5*Cos(t)^2)*Sin(t)");

    check("SphericalHarmonicY(5, 1, 0, .3)", //
        "0.0");
    check("SphericalHarmonicY(1, 0.5, -5, Pi/3)", //
        "-0.0975801+I*(-0.0563379)");

    check("N(SphericalHarmonicY(2, 1, Pi/3, Pi),20 )", //
        "0.33452327177864458397");
    check("N(SphericalHarmonicY(2, 2, Pi/6, Pi/4.211111111111111111) )", //
        "0.00759663110648051733+I*0.09626928971703694314");
    check("N(SphericalHarmonicY(23, 5 - I, Pi/3, I + Pi) )", //
        "0.00760864+I*0.149675");
    check("SphericalHarmonicY(0,0,t,p)", //
        "1/(2*Sqrt(Pi))");
    check("SphericalHarmonicY(a,0,0,p)", //
        "Sqrt(1+2*a)/(2*Sqrt(Pi))");
    check("SphericalHarmonicY(1,2,t,p)", //
        "0");
    check("SphericalHarmonicY(1,1,t,p)", //
        "-1/2*E^(I*p)*Sqrt(3/2*1/Pi)*Sin(t)");
    check("SphericalHarmonicY(n,-n-1,t,p)", //
        "SphericalHarmonicY(n,-1-n,t,p)");
  }

  @Test
  public void testZernikeR() {
    check("ZernikeR(1,2147483647,Sqrt(2))", //
        "ZernikeR(1,2147483647,Sqrt(2))");

    check("ZernikeR(x,<|a->0,b:>1|>,{-I})", //
        "{<|a->ZernikeR(x,0,-I),b:>ZernikeR(x,1,-I)|>}");

    // https://en.wikipedia.org/wiki/Zernike_polynomials#Radial_polynomials
    check("ZernikeR(0,0,p)", //
        "1");
    check("ZernikeR(1,1,p)", //
        "p");
    check("ZernikeR(2,0,p)", //
        "-1+2*p^2");
    check("ZernikeR(2,2,p)", //
        "p^2");
    check("ZernikeR(3,1,p)", //
        "-2*p+3*p^3");
    check("ZernikeR(3,3,p)", //
        "p^3");
    check("ZernikeR(6,4,p)", //
        "-5*p^4+6*p^6");

    checkNumeric("ZernikeR(3, 1, 0.5)", //
        "-0.625");
    check("ZernikeR(5,3,r)", //
        "-4*r^3+5*r^5");
    check("ZernikeR(10,7,r)", //
        "0");
    check("ZernikeR(7,10,r)", //
        "0");
    check("N(ZernikeR(1/5, 2/3, 1/7), 50)", //
        "0.16624107895274857519231711412376921850667019765573");

    checkNumeric("N(ZernikeR(2/5, 2-I, 2))", //
        "0.008507312038070331+I*0.8156212465816385");
  }

}
