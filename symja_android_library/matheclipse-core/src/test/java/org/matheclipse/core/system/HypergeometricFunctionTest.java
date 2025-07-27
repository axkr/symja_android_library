package org.matheclipse.core.system;

import org.junit.Test;

public class HypergeometricFunctionTest extends ExprEvaluatorTestCase {

  @Test
  public void testHypergeometric0F1() {
    check("N(Hypergeometric0F1(1, -2), 50)", //
        "-0.19654809527046820004079337208793223132588978731089");
    check("Hypergeometric0F1(1, -2.00000000000000000000000000000)", //
        "-0.1965480952704682000407933720879");

    check("Hypergeometric0F1(b, 0)", //
        "1");
    check("Hypergeometric0F1(b, Infinity)", //
        "ComplexInfinity");

    check("Hypergeometric0F1(1/2, z)", //
        "Cosh(2*Sqrt(z))");
    check("Hypergeometric0F1(1/2, -a)", //
        "Cos(2*Sqrt(a))");
    check("Hypergeometric0F1(3/2, z)", //
        "Sinh(2*Sqrt(z))/(2*Sqrt(z))");
    check("Hypergeometric0F1(3/2, -a)", //
        "Sin(2*Sqrt(a))/(2*Sqrt(a))");

    check("Hypergeometric0F1({1, 2, 3}, 1.5)", //
        "{3.16559,1.96279,1.60374}");
    check("Hypergeometric0F1(1,-2.0)", //
        "-0.196548");
    checkNumeric("Hypergeometric0F1(1,1.5)", //
        "3.1655890675997247");
  }

  @Test
  public void testHypergeometric0F1Regularized() {
    checkNumeric("Hypergeometric0F1Regularized(0., E)", //
        "8.522277545659726");
    check("Hypergeometric0F1Regularized(b, 0)", //
        "1/Gamma(b)");
    check("N(Hypergeometric0F1Regularized(0, -48), 50)", //
        "-0.75356407978144308380327864211537660841009027837077");
  }

  @Test
  public void testHypergeometric1F1() {
    checkNumeric("Hypergeometric1F1(a, a+2, z)", //
        "(Gamma(a)-Gamma(a,-z)+(Gamma(1+a)-Gamma(1+a,-z))/z)/((-z)^a*Beta(a,2))");
    checkNumeric("Hypergeometric1F1(1,1/2,z)", //
        "1+E^z*Sqrt(Pi)*Sqrt(z)*Erf(Sqrt(z))");
    checkNumeric("Hypergeometric1F1(1,{2,3,4},5.0)", //
        "{29.482631820515323,11.393052728206127,6.235831636923677}");
    check("Hypergeometric1F1(3,b,z)", //
        "1/2*(-1+b)*(2*(3-b)+2*z+E^z*z^(3-b)*((-2+b)/(E^z*z^(3-b))-1/(E^z*z^(2-b)))+(2-b)*(\n" //
            + "3-b)*E^z*z^(1-b)*(Gamma(-1+b)-Gamma(-1+b,z))+2*(3-b)*E^z*z^(2-b)*(Gamma(-1+b)-Gamma(-\n" //
            + "1+b,z))+E^z*z^(3-b)*(Gamma(-1+b)-Gamma(-1+b,z)))");
//        "1/2*(-1+b)*(4-b+z+(2-b)*(3-b)*E^z*z^(1-b)*(Gamma(-1+b)-Gamma(-1+b,z))+2*(3-b)*E^z*z^(\n"
//            + "2-b)*(Gamma(-1+b)-Gamma(-1+b,z))+E^z*z^(3-b)*(Gamma(-1+b)-Gamma(-1+b,z)))");
    check("Hypergeometric1F1(a,a+1,z)", //
        "(a*(Gamma(a,0)-Gamma(a,-z)))/(-z)^a");
    check("Hypergeometric1F1(1,a+1,z)", //
        "(a*E^z*(Gamma(a)-Gamma(a,z)))/z^a");
    check("Hypergeometric1F1(a-1, a, z)", //
        "(-1+a)*(-z)^(1-a)*(Gamma(-1+a,0)-Gamma(-1+a,-z))");
    check("Hypergeometric1F1(a, a - 1, z)", //
        "(E^z*(-1+a+z))/(-1+a)");
    check("Hypergeometric1F1({0,0,0},a,{{0,0},{0,0},0})", //
        "{{1,1},{1,1},1}");
    checkNumeric("Hypergeometric1F1(-0.5, 1.0 / 3.0, -1)", //
        "2.269314995817225");
    // assertThat(Maja.hypergeo1F1(-0.5, 1.0 / 3.0, -1)).isEqualTo(2.269314995817403);
    check("N(Hypergeometric1F1(10, 1/3, -1), 50)", //
        "1.0856469662771144181060999200053894821341819655655");
    check("Hypergeometric1F1(10, 1/3, -1.000000000000000000000000000000000000)", //
        "1.0856469662771144181060999200053894821");

    check("Hypergeometric1F1(-2,-1-a,-a)", //
        "1+(2*a)/(-1-a)+a/(1+a)");
    check("Hypergeometric1F1(-3,b,z)", //
        "1+(-3*z)/b+(3*z^2)/(b*(1+b))-z^3/(b*(1+b)*(2+b))");
    check("Hypergeometric1F1(-2,b,z)", //
        "1+(-2*z)/b+z^2/(b*(1+b))");
    // slow
    // check("Hypergeometric1F1(-9223372036854775808/11,{2,3,4},0.5)", //
    // "{Hypergeometric1F1(-8.38488*10^17,2.0,0.5),Hypergeometric1F1(-8.38488*10^17,3.0,0.5),Hypergeometric1F1(-8.38488*10^17,4.0,0.5)}");
    // TODO check wrong
    check("Hypergeometric1F1(3,Quantity(1.2,\"m\"),-1+I)", //
        "Hypergeometric1F1(3,1.2[m],-1+I)");
    check("Hypergeometric1F1(2 + I, {2,3,4}, 0.5)", //
        "{1.61833+I*0.379258,1.391+I*0.228543,1.28402+I*0.161061}");

    check("Hypergeometric1F1(1,b,z)", //
        "(-1+b)*E^z*z^(1-b)*(Gamma(-1+b)-Gamma(-1+b,z))");
    check("Hypergeometric1F1(2,b,z)", //
        "(-1+b)*(1+(2-b)*E^z*z^(1-b)*(Gamma(-1+b)-Gamma(-1+b,z))+E^z*z^(2-b)*(Gamma(-1+b)-Gamma(-\n"
            + "1+b,z)))");
    check("Hypergeometric1F1(-2,-1,0)", //
        "1");
    check("Hypergeometric1F1(-2,-1,z)", //
        "ComplexInfinity");
    check("Hypergeometric1F1(-2,-7,z)", //
        "1+2/7*z+z^2/42");
    check("Hypergeometric1F1(-2,0,z)", //
        "ComplexInfinity");
    check("Hypergeometric1F1(a,a,z)", //
        "E^z");
    check("Hypergeometric1F1(0,1,z)", //
        "1");
    check("Hypergeometric1F1(a,1,z)  // FunctionExpand", //
        "LaguerreL(-a,z)");
    check("Hypergeometric1F1(3,1,z)", //
        "Hypergeometric1F1(3,1,z)");
    check("Hypergeometric1F1(-1,b,z)", //
        "1-z/b");
    check("Hypergeometric1F1(-1,2,3.0)", //
        "-0.5");
    check("Hypergeometric1F1(1,2,3.0)", //
        "6.36185");

  }

  @Test
  public void testHypergeometric1F1Regularized() {
    // TODO interrupt long running calculations
    // check("Hypergeometric1F1Regularized(-9223372036854775808/11,-0.8,-11)", //
    // "");

    check("N(Hypergeometric1F1Regularized(2,-4,2), 50)", //
        "1891.5983613262464581709894299072020001741607860612");
    check("N(Hypergeometric1F1Regularized(1, 0, 1), 30)", //
        "2.71828182845904523536028747135");

    checkNumeric("Hypergeometric1F1Regularized(7, 23, 0.5)", //
        "1.0370581075059291E-21");
    check("Hypergeometric1F1Regularized(a, b, 0)", //
        "1/Gamma(b)");
    check("Hypergeometric1F1Regularized(a, a, z)", //
        "E^z/Gamma(a)");
  }

  @Test
  public void testHypergeometric2F1() {
    check("Hypergeometric2F1(1,1/2,3/2,m*z^k)", //
        "ArcTanh(Sqrt(m)*z^(k/2))/(Sqrt(m)*z^(k/2))");


    // https://dlmf.nist.gov/15.4
    check("Hypergeometric2F1(a,b,1/2*a+1/2*b+1/2, 1/2)", //
        "(Sqrt(Pi)*Gamma(1/2+a/2+b/2))/(Gamma(1/2+a/2)*Gamma(1/2+b/2))");
    check("Hypergeometric2F1(1,a,a+1,-1)", //
        "1/2*a*(PolyGamma(0,1/2+a/2)-PolyGamma(0,a/2))");
    check("Hypergeometric2F1(a,b,a-b+1, -1)", //
        "(Sqrt(Pi)*Gamma(1+a-b))/(2^a*Gamma(1/2+a/2)*Gamma(1+a/2-b))");
    check("Hypergeometric2F1(a+1,b,a,z)", //
        "(-a+a*z-b*z)/(a*(1-z)^b*(-1+z))");
    check("Hypergeometric2F1(a,1-a,1/2,z)", //
        "Cos((-1+2*(1-a))*ArcSin(Sqrt(z)))/Sqrt(1-z)");
    check("Hypergeometric2F1(1-a,a,1/2,z)", //
        "Cos((-1+2*(1-a))*ArcSin(Sqrt(z)))/Sqrt(1-z)");
    check("Hypergeometric2F1(a,-a,1/2,z)", //
        "Cos(2*a*ArcSin(Sqrt(z)))");
    check("Hypergeometric2F1(1/2,1,3/2,3)", //
        "ArcTanh(3)/3");
    check("Hypergeometric2F1(1/2,1,3/2,t^2)", //
        "ArcTanh(t)/t");
    check("Hypergeometric2F1(a, a + 1/2, 2*a, z)", //
        "(1+Sqrt(1-z))^(1-2*a)/(2^(1-2*a)*Sqrt(1-z))");

    // https://github.com/mtommila/apfloat/issues/29
    checkNumeric("Hypergeometric2F1(-3.0, -1, -2, 1.0)", //
        "-0.5");
    // https://github.com/paulmasson/math/issues/10 - uses ThrowException
    check("Hypergeometric2F1(0.5,0.333,0.666,1)", //
        "Hypergeometric2F1(0.333,0.5,0.666,1.0)");
    checkNumeric("Hypergeometric2F1(-0.5, 1.0 / 3.0, 4.0 / 3.0, -1)", //
        "1.1114479705325755");
    checkNumeric("Hypergeometric2F1(-0.5, 1.0 / 3.0, -4.0 / 3.0, 0)", //
        "1.0");

    checkNumeric("Hypergeometric2F1(-0.5, 1.0 / 4.0, -4.0 / 3.0, -3)", //
        "0.6919237698061459");
    checkNumeric("Hypergeometric2F1(-0.5, 1.0 / 4.0, -4.0 / 3.0,-1.5)", //
        "0.8274559676019725");
    checkNumeric("Hypergeometric2F1(0.5, 1.0 / 3.0, 4.0 / 3.0, 1)", //
        "1.4021821053254544");
    checkNumeric("Hypergeometric2F1(-5, 1.0 / 3.0, 4.0 / 3.0, 1)", //
        "0.5006868131868132");
    checkNumeric("Hypergeometric2F1(-0.5, 3.0, 1.0, 0.2)", //
        "0.66383268082025");
    checkNumeric("Hypergeometric2F1(2, 2.0, 3.0, 0.95)", //
        "35.46652127744268");
    checkNumeric("Hypergeometric2F1(2, 2.0, 3.01, 0.95)", //
        "34.8101340764061");
    checkNumeric("Hypergeometric2F1(20, 2.0, 3.01, 0.95)", //
        "5.513090059384318E23");
    checkNumeric("Hypergeometric2F1(0.123, 2.0, 3.01, 0.95)", //
        "1.166719619920925");
    checkNumeric("Hypergeometric2F1(-1, 2.0, 3.01, 0.95)", //
        "0.3687707641196013");

    checkNumeric("N(Hypergeometric2F1(-23/10, 1/2 - 1/8 + 23/10 + 1, 1/8 + 1, (12 + 1)/2),50)", //
        "316.8942332151300302909232080800472001326090876729+I*436.16750112351808402055619284015549992820985322775");
    checkNumeric("Hypergeometric2F1(-2.3, 1/2 - 1/8 + 2.3 + 1, 1/8 + 1, (12 + 1)/2)", //
        "316.89423321513+I*436.1675011235181");

    check("D( Hypergeometric2F1(a,b,c,x), {x,-4})", //
        "D(Hypergeometric2F1(a,b,c,x),{x,-4})");

    check("N(Hypergeometric2F1(1/2, 1/3, 2, 1), 50)", //
        "1.1595952669639283657699920515700208819451652634397");
    check("Hypergeometric2F1(1/2, 1/3, 2, 1.000000000000000000000000000000000)", //
        "1.15959526696392836576999205157002");

    // iteration limit of exceeded.
    // https://github.com/paulmasson/math/issues/29#issuecomment-1120230707
    check("Hypergeometric2F1(1, 0.5, 1.5, -0.9999999999999976)", //
        "0.785398163397448652");


    check("Hypergeometric2F1(n,n,2*n+1,1)", //
        "Gamma(1+2*n)/Gamma(1+n)^2");
    check("Hypergeometric2F1(2/3,3/7,10, 1)", //
        "(362880*Gamma(187/21))/(Gamma(28/3)*Gamma(67/7))");

    // message Plus: m^2 and m are incompatible units
    check("Hypergeometric2F1(-5,Quantity(1.2,\"m\"),c,1)", //
        "(-28.8[m]+72.0[m^2]+-60.48[m^3]+20.736[m^4]+-2.48832[m^5]+24*c+-120.0[m]*c+151.2[m^2]*c+-69.12[m^3]*c+10.368[m^4]*c+\n" //
            + "50*c^2+-126.0[m]*c^2+86.4[m^2]*c^2+-17.28[m^3]*c^2+35*c^3+-48.0[m]*c^3+14.4[m^2]*c^\n" //
            + "3+10*c^4+-6.0[m]*c^4+c^5)/(c*(1+c)*(2+c)*(3+c)*(4+c))");

    // check("Hypergeometric2F1(1317624576693539401,0.333,-3/2,-0.5)", //
    // "Hypergeometric2F1(0.333,1.31762*10^18,-1.5,-0.5)");
    check("Hypergeometric2F1(-1,b,c,1)", //
        "(-b+c)/c");
    check("Hypergeometric2F1(-2,b,c,1)", //
        "(-b+b^2+c-2*b*c+c^2)/(c*(1+c))");
    check("Hypergeometric2F1(3,0,-1,x)", //
        "1");
    check("Hypergeometric2F1(a,3/2,1/2,x)", //
        "(1-x+2*a*x)/(1-x)^(1+a)");
    check("Hypergeometric2F1(3,1,-1,x)", //
        "ComplexInfinity");
    check("Hypergeometric2F1(1/2,I,-10,x)", //
        "ComplexInfinity");
    check("D( Hypergeometric2F1(a,b,c,x), {x,-4})", //
        "D(Hypergeometric2F1(a,b,c,x),{x,-4})");
    check("D( Hypergeometric2F1(a,b,c,x), {x,n})", //
        "(Hypergeometric2F1(a+n,b+n,c+n,x)*Pochhammer(a,n)*Pochhammer(b,n))/Pochhammer(c,n)");
    check("D( Hypergeometric2F1(a,b,c,x), {x,4})", //
        "(a*(1+a)*(2+a)*(3+a)*b*(1+b)*(2+b)*(3+b)*Hypergeometric2F1(4+a,4+b,4+c,x))/(c*(1+c)*(\n" + //
            "2+c)*(3+c))");
    check("D( Hypergeometric2F1(a,b,c,x), x)", //
        "(a*b*Hypergeometric2F1(1+a,1+b,1+c,x))/c");

    // check("Hypergeometric2F1(-3, 1, 2, z)", //
    // "(1-(1-z)^4)/(4*z)");

    check("Hypergeometric2F1(2,1-I,2-I,I*E^(I*x))", //
        "Hypergeometric2F1(1-I,2,2-I,I*E^(I*x))");
    check("Hypergeometric2F1(3/2, 2, 5/2, z^n) ", //
        "3/2*(-z^(n/2)+ArcTanh(z^(n/2))-z^n*ArcTanh(z^(n/2)))/(z^(3/2*n)*(-1+z^n))");
    check("Hypergeometric2F1(3/2, 2, 5/2, -z) ", //
        "(-I*3/2*(I*Sqrt(z)-I*ArcTan(Sqrt(z))-I*z*ArcTan(Sqrt(z))))/((-1-z)*z^(3/2))");
    check("Hypergeometric2F1(3/2, 2, 5/2, z) ", //
        "3/2*(-Sqrt(z)+ArcTanh(Sqrt(z))-z*ArcTanh(Sqrt(z)))/((-1+z)*z^(3/2))");

    // CatalanNumber:
    check("Hypergeometric2F1(-9, -10, 2, 1)", //
        "16796");

    // TODO currently unsupported
    check("Hypergeometric2F1(0.5,0.333,1,1.5708)", //
        "1.12923+I*(-0.568083)");

    check("Hypergeometric2F1(1, b, 2, z)", //
        "-(-1+(1-z)^b+z)/((-1+b)*(1-z)^b*z)");
    check("Hypergeometric2F1(a, b, a, z)", //
        "(1-z)^(-b)");
    check("Hypergeometric2F1(a, b, b-1, z)", //
        "Hypergeometric2F1(a,b,-1+b,z)");
    check("Hypergeometric2F1(a, b, b, z)", //
        "(1-z)^(-a)");
    // check("Hypergeometric2F1(a, b, b+1, z)", //
    // "(b*Beta(z,b,1-a))/z^b");

    check("Hypergeometric2F1(-5, b, c, 1)", //
        "(-24*b+50*b^2-35*b^3+10*b^4-b^5+24*c-100*b*c+105*b^2*c-40*b^3*c+5*b^4*c+50*c^2-\n"
            + "105*b*c^2+60*b^2*c^2-10*b^3*c^2+35*c^3-40*b*c^3+10*b^2*c^3+10*c^4-5*b*c^4+c^5)/(c*(\n"
            + "1+c)*(2+c)*(3+c)*(4+c))");
    check("Hypergeometric2F1(-n, b, c, 1)", //
        "Hypergeometric2F1(b,-n,c,1)");



    check("Hypergeometric2F1(2 + I, -I, 3/4, 0.5-0.5*I)", //
        "-0.972167+I*(-0.181659)");

    // Hypergeometric2F1(1 - n, -n, 2, 1) == CatalanNumber(n)
    check("Hypergeometric2F1(-3, -4, 2, 1)==CatalanNumber(4)", //
        "True");

    check("Hypergeometric2F1(1,2,3/2,x^2/9)", //
        "3/2*(1/3*Sqrt(1-x^2/9)*Sqrt(x^2)+ArcSin(Sqrt(x^2)/3))/((1-x^2/9)^(3/2)*Sqrt(x^2))");

    check("Hypergeometric2F1(-2,b,c,1)", //
        "(-b+b^2+c-2*b*c+c^2)/(c*(1+c))");

    check("Hypergeometric2F1(0.5,0.333,0.666,0.5)", //
        "1.18566");
    checkNumeric("Hypergeometric2F1(0.5,Sin(Pi),0.666,-0.5)", //
        "1.0");
    checkNumeric("Hypergeometric2F1(0.5,0.333,0.666,-0.5)", //
        "0.9026782488379916");
    checkNumeric("Hypergeometric2F1(0.5,0.333,0.666,0.75)", //
        "1.3975732184289733");
    checkNumeric("Hypergeometric2F1(0.5,0.333,0.666,-0.75)", //
        "0.8677508558430819");
  }

  @Test
  public void testHypergeometric2F1Regularized() {
    check("Hypergeometric2F1Regularized(I, -I, .5 + I, 5)", //
        "-0.91139+I*(-1.76606)");

    check("Hypergeometric2F1Regularized(-1, -1, 0, .5)", //
        "0.5");
    check("N(Hypergeometric2F1Regularized(1/3, 1, 3, -7), 50)", //
        "0.35510204081632653061224489795918367346938775510204");
    check("N(Hypergeometric2F1Regularized(1/3, 1, 0, -7), 25)", //
        "-0.1458333333333333333333333");


    checkNumeric("Hypergeometric2F1Regularized(1, 2, -3, 4.5)", //
        "26.768438320767704");
    check("Hypergeometric2F1Regularized(1, 1/3, -1, -0.3000000025555555555522220000)", //
        "0.0216866352303372915924565340457");

    check("Hypergeometric2F1Regularized(7, 2, -0.3, .5)", //
        "17490.25");

    check("Hypergeometric2F1Regularized(a,b,b,x)", //
        "1/((1-x)^a*Gamma(a))");
    check("Hypergeometric2F1Regularized(a,b,c,0)", //
        "1/Gamma(c)");
  }

  @Test
  public void testHypergeometricPFQ() {
    check("HypergeometricPFQ({c1,c2},{c1,c2}, z)", //
        "E^z");
    check("N( HypergeometricPFQ({4},{},1) )", //
        "ComplexInfinity");
    check("HypergeometricPFQ({}, {}, z)", //
        "E^z");
    check("HypergeometricPFQ(ConstantArray(1,1), ConstantArray(2,0), z)", //
        "1/(1-z)");
    check("HypergeometricPFQ(ConstantArray(1,2), ConstantArray(2,1), z)", //
        "-Log(1-z)/z");
    check("HypergeometricPFQ(ConstantArray(1,3), ConstantArray(2,2), z)", //
        "PolyLog(2,z)/z");
    check("HypergeometricPFQ({1,1,1,1,1,1}, {2,2,2,2,2}, z)", //
        "PolyLog(5,z)/z");
    check("HypergeometricPFQ(ConstantArray(1,42), ConstantArray(2,41), z)", //
        "PolyLog(41,z)/z");

    check("HypergeometricPFQ({1, 1}, {1/2, 1}, z)", //
        "1+E^z*Sqrt(Pi)*Sqrt(z)*Erf(Sqrt(z))");
    check("HypergeometricPFQ({1, 1}, {1/2, 1}, z)", //
        "1+E^z*Sqrt(Pi)*Sqrt(z)*Erf(Sqrt(z))");
    check("HypergeometricPFQ({0,a1,a2,a3,a4},{b1,b2,b3},z)", //
        "1");

    // message HypergeometricPFQ: general hypergeometric argument currently restricted.
    check("HypergeometricPFQ({1, 1, 1}, {3/2, 3/2, 3/2}, 10.0)", //
        "HypergeometricPFQ({1,1,1},{3/2,3/2,3/2},10.0)");
    check("HypergeometricPFQ({1, 1}, {3, 3, 3}, 2.)", //
        "HypergeometricPFQ({1,1},{3,3,3},2.0)");

    check("HypergeometricPFQ({I, I, I}, {2, 2 , 2}, -1.0*I)", //
        "0.870032+I*(-0.00484538)");
    check("HypergeometricPFQ({1, 2, 3, 4}, {5, 6, 7}, {0.1, 0.3, 0.5})", //
        "{1.01164,1.03627,1.06296}");

    check("HypergeometricPFQ({1/2, b}, {3/2, b + 1}, z)", //
        "(b*(Sqrt(Pi)*Sqrt(1/z)*Erfi(Sqrt(z))+(-Gamma(b)+Gamma(b,-z))/(-z)^b))/(-1+2*b)");
    check("HypergeometricPFQ({a,b}, {c,d}, 0)", //
        "1");
    check("HypergeometricPFQ({a, b}, {c, b}, z)", //
        "HypergeometricPFQ({a},{c},z)");
  }

  @Test
  public void testHypergeometricU() {
    check("HypergeometricU(-5,b,z)", //
        "-24*b-50*b^2-35*b^3-10*b^4-b^5+120*z+250*b*z+175*b^2*z+50*b^3*z+5*b^4*z-240*z^2-\n" //
            + "260*b*z^2-90*b^2*z^2-10*b^3*z^2+120*z^3+70*b*z^3+10*b^2*z^3-20*z^4-5*b*z^4+z^5");

    check("HypergeometricU(3,b,z)", //
        "(-3+b-z+1/2*(2-b+z)+1/2*E^z*(6-5*b+b^2+6*z-2*b*z+z^2)*z^(1-b)*Gamma(-1+b,z))/((2-b)*(\n" //
            + "3-b))");

    check("N(HypergeometricU({3,1},{2,4},{7,8}))", //
        "{0.00154364,0.160156}");

    // TODO throws hypergeometric function pole
    // https://github.com/mtommila/apfloat/issues/36
    check("HypergeometricU(3.0, 1.0, 0.0)", //
        "HypergeometricU(3.0,1.0,0.0)");
    check("HypergeometricU(3, 2, 1.0)", //
        "0.105479");
    check("N(HypergeometricU(3, 2, 1),50)", //
        "0.10547895651520888848838225094608093588873320977117");


    check("D(HypergeometricU(a,b,x), x)", //
        "-a*HypergeometricU(1+a,1+b,x)");
    check("HypergeometricU(2, b, z)", //
        "(-1+E^z*(2-b+z)*z^(1-b)*Gamma(-1+b,z))/(2-b)");
    check("HypergeometricU(a, a+3, z)", //
        "(1+(a*(1+a))/z^2+(2*a)/z)/z^a");
    check("HypergeometricU(3, 2.5, 1.0)", //
        "0.173724");
    check("HypergeometricU(3, 2.5, 0.0)", //
        "ComplexInfinity");
    check("Table( HypergeometricU(3, 2.5, x), {x,-2.0,2,0.25})", //
        "{0.19001+I*(-0.148415),0.27603+I*(-0.141362),0.39355+I*(-0.107638),0.553199+I*(-0.0227102)," //
            + "0.76904+I*0.163012,1.05965+I*0.56395,1.44956+I*1.52035,1.97105+I*4.83136,ComplexInfinity," //
            + "2.45436,0.688641,0.312167,0.173724,0.1086,0.0732253,0.0520871,0.0385635}");
    check("Table( HypergeometricU(3, 1.0, x), {x,-2.0,2,0.25})", //
        "{0.0852414+I*0.212584,0.0312283+I*0.264433,-0.0527303+I*0.306681,-0.171748+I*0.323467,-0.325706+I*0.288932,-0.500123+I*0.162311,-0.642219+I*(-0.119092),-0.575265+I*(-0.649898),HypergeometricU(3.0,1.0,0.0),0.214115,0.105593,0.0644474,0.0436079,0.0314298,0.0236577,0.0183874,0.0146502}");
  }



}
