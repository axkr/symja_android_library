package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The level specification <code>All</code>, arrays which aren't rectangular or are partitioned in
 * blocks of three levels, and values of special functions at the edge of their formulas: orders
 * beyond the degree, polynomial cases at a zero argument, a machine zero, large orders and
 * principal values.
 */
public class LevelAllAndSpecialValuesTest extends ExprEvaluatorTestCase {

  @Test
  public void testLevelAllIncludesTheWholeExpression() {
    check("Level({p,{q}},All)", //
        "{p,q,{q},{p,{q}}}");
    check("Map(g,{1,{2}},All)", //
        "g({g(1),g({g(2)})})");
    check("Position({a,{b,{c}}},_List,All)", //
        "{{2,2},{2},{}}");
    // the default level specification {0, Infinity} includes the whole expression
    check("Position(h(u),_h)", //
        "{{}}");
    check("Position(h(u),_h,1)", //
        "{}");
  }

  @Test
  public void testArrays() {
    check("Median({{3,4},5})", //
        "Median({{3,4},5})");
    // blocks of 2x2x2 elements
    check("Dimensions(Partition(Array(b,{4,2,2}),{2,2,2}))", //
        "{2,1,1,2,2,2}");
    check("Partition(Array(b,{4,2,2}),{2,2,2})[[2,1,1,2,1,2]]", //
        "b(4,1,2)");
  }

  @Test
  public void testPolynomialCases() {
    // an order -k with k > n: no identity with LegendreP(n, k, x) == 0
    check("LegendreP(0,-2,x)", //
        "(1-x)/(2*(1+x))");
    check("LegendreP(1,-4,x)", //
        "((1-x)^2*(4+x))/(120*(1+x)^2)");
    // HypergeometricU(-n, b, z) is a polynomial in z
    check("HypergeometricU(-3,2,0)", //
        "-24");
    check("HypergeometricU(-2,3,z)", //
        "12-8*z+z^2");
    check("HypergeometricU(-1,2,0.0)", //
        "-2.0");
    check("StruveL(-3/2,0.0)", //
        "0.0");
    // a machine number argument: the alternating sum of the polynomial loses all digits
    check("HypergeometricU(-50,3,21.5)", //
        "7.23221*10^67");
  }

  @Test
  public void testBesselJZeroOfALargeOrder() {
    check("N(BesselJZero(50,1))", //
        "57.1169");
    check("Chop(BesselJ(50, N(BesselJZero(50,1))), 10^-10)", //
        "0");
    // the sign changes are counted with the fast machine routine
    check("TimeConstrained(N(BesselJZero(300,25)),10)", //
        "442.7963");
  }

  @Test
  public void testPrincipalValue() {
    check("NIntegrate(1/(x-2),{x,0,2,5},Method->\"PrincipalValue\")", //
        "0.405465");
    // no interior point: message pvrng
    check("NIntegrate(Sin(x),{x,1,4},Method->\"PrincipalValue\")", //
        "NIntegrate(Sin(x),{x,1,4},Method->PrincipalValue)");
  }

  @Test
  public void testNoAnswer() {
    check("NRoots(2==3,x)", //
        "NRoots(False,x)");
    check("Divisible(5,-Infinity)", //
        "Divisible(5,-Infinity)");
    check("ArcCoth(-1.0+0.0*I)", //
        "Indeterminate");
    check("ArcCoth(-1.0)", //
        "Indeterminate");
  }

  @Test
  public void testExactDecisions() {
    check("Clip(E+10^-30,{0,E})", //
        "E");
    // above 2^24 the roots modulo 2 are lifted to 2^36
    check("PowerMod(49,1/2,2^36)", //
        "7");
    // 32760 roots modulo the prime, each lifted by one linear congruence
    check("TimeConstrained(PowerMod(1,1/32760,65521^2),10)", //
        "1");
    check("Mod(PowerMod(10,1/3,65519^3)^3-10,65519^3)", //
        "0");
    check("IrreduciblePolynomialQ(x^3+y^5, Extension->All)", //
        "True");
    check("IrreduciblePolynomialQ(x^6+y^6+1, Extension->All)", //
        "True");
  }
}
