package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for the tier 2.1 number theory functions: AlternatingFactorial, FareySequence, RootOfUnityQ, CantorStaircase, MinkowskiQuestionMark, the Mandelbrot functions, RiemannR and the Ramanujan tau L-function. */
public class NumberTheoryAdditionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testAlternatingFactorial() {
    check("AlternatingFactorial(6)", //
        "619");
    check("AlternatingFactorial({0, 1, 2, 3})", //
        "{0,1,1,5}");
    check("AlternatingFactorial(n)", //
        "AlternatingFactorial(n)");
    check("AlternatingFactorial({-3, -1})", //
        "{ComplexInfinity,ComplexInfinity}");
  }

  @Test
  public void testAlternatingHarmonicNumber() {
    check("AlternatingHarmonicNumber(5)", //
        "47/60");
    check("AlternatingHarmonicNumber(4, 2)", //
        "115/144");
    check("AlternatingHarmonicNumber(Infinity)", //
        "Log(2)");
    check("AlternatingHarmonicNumber(Infinity, 3)", //
        "3/4*Zeta(3)");
    check("AlternatingHarmonicNumber(3, 0.5)", //
        "0.870243");
  }

  @Test
  public void testFibonorial() {
    check("Fibonorial(7)", //
        "3120");
    check("Fibonorial(0)", //
        "1");
  }

  @Test
  public void testFareySequence() {
    // the list form is limited in size, a single term isn't
    check("FareySequence(1000)", //
        "FareySequence(1000)");
    check("FareySequence(1000, 3)", //
        "1/999");
    check("FareySequence(5)", //
        "{0,1/5,1/4,1/3,2/5,1/2,3/5,2/3,3/4,4/5,1}");
    check("FareySequence(6, 4)", //
        "1/4");
    check("FareySequence(0)", //
        "FareySequence(0)");
  }

  @Test
  public void testPascalBinomial() {
    check("PascalBinomial(5, 2)", //
        "10");
    check("PascalBinomial(-3, 2)", //
        "6");
    check("PascalBinomial(-3, -2)", //
        "0");
    check("PascalBinomial(4.5, -1)", //
        "0.0");
  }

  @Test
  public void testMixedFractionParts() {
    check("MixedFractionParts(17/5)", //
        "{3,2/5}");
    check("MixedFractionParts(-7/3)", //
        "{-2,-1/3}");
    check("MixedFractionParts(2.75)", //
        "{2,0.75}");
    check("MixedFractionParts(x)", //
        "MixedFractionParts(x)");
  }

  @Test
  public void testRealExponent() {
    check("RealExponent(1000)", //
        "3.0");
    check("RealExponent(-0.001)", //
        "-3.0");
    check("RealExponent(0)", //
        "-Infinity");
    check("RealExponent(8, 2)", //
        "3.0");
    check("RealExponent(3 + 4*I)", //
        "0.69897");
  }

  @Test
  public void testRootOfUnityQ() {
    check("RootOfUnityQ(Exp(2*Pi*I/7))", //
        "True");
    check("RootOfUnityQ((1 + I*Sqrt(3))/2)", //
        "True");
    check("RootOfUnityQ((3 + 4*I)/5)", //
        "False");
    check("RootOfUnityQ(I)", //
        "True");
    check("RootOfUnityQ(Sqrt(2))", //
        "False");
    check("RootOfUnityQ(0.5 + 0.866*I)", //
        "False");
  }

  @Test
  public void testContinuedFractionK() {
    check("ContinuedFractionK(1, 1, {k, -2000000000, 2000000000})", //
        "ContinuedFractionK(1,1,{k,-2000000000,2000000000})");
    check("ContinuedFractionK(k, {k, 1, 4})", //
        "30/43");
    check("ContinuedFractionK(k^2, 2*k + 1, {k, 1, 3})", //
        "11/40");
    check("ContinuedFractionK(1, {n, 1, Infinity})", //
        "1/2*(-1+Sqrt(5))");
    check("ContinuedFractionK(2, 1, {n, 1, Infinity})", //
        "1");
  }

  @Test
  public void testCantorStaircase() {
    check("CantorStaircase(1/4)", //
        "1/3");
    check("CantorStaircase(1/3)", //
        "1/2");
    check("CantorStaircase(2/3)", //
        "1/2");
    check("CantorStaircase(1/7)", //
        "1/4");
    check("CantorStaircase(0.25)", //
        "0.333333");
    check("CantorStaircase(-1)", //
        "0");
    check("CantorStaircase(5/4)", //
        "1");
  }

  @Test
  public void testMinkowskiQuestionMark() {
    check("MinkowskiQuestionMark(3/5)", //
        "5/8");
    check("MinkowskiQuestionMark(-1/2)", //
        "8");
    check("MinkowskiQuestionMark(GoldenRatio)", //
        "5/3");
    check("MinkowskiQuestionMark(Sqrt(2))", //
        "7/5");
    check("MinkowskiQuestionMark(-Sqrt(2))", //
        "3/5");
    check("MinkowskiQuestionMark(0.6)", //
        "0.625");
    check("MinkowskiQuestionMark(Pi)", //
        "MinkowskiQuestionMark(Pi)");
  }

  @Test
  public void testMandelbrotSetIterationCount() {
    check("MandelbrotSetIterationCount(1)", //
        "2");
    check("MandelbrotSetIterationCount(0.3 + 0.5*I)", //
        "1000");
    check("MandelbrotSetIterationCount(-0.1, 50)", //
        "51");
  }

  @Test
  public void testMandelbrotSetMemberQ() {
    check("MandelbrotSetMemberQ(-1)", //
        "True");
    check("MandelbrotSetMemberQ(1/4 + I)", //
        "False");
    check("MandelbrotSetMemberQ({0, I, 2})", //
        "{True,True,False}");
  }

  @Test
  public void testRiemannR() {
    check("RiemannR(1)", //
        "1");
    check("RiemannR(100.0)", //
        "25.66163");
    check("RiemannR(0.5)", //
        "0.663526");
  }

  @Test
  public void testRiemannRNumeric() {
    check("N(RiemannR(1000))", //
        "168.3594");
  }

  @Test
  public void testRamanujanTauTheta() {
    check("RamanujanTauTheta(10.0)", //
        "1.84545");
  }

  @Test
  public void testRamanujanTauL() {
    check("RamanujanTauL(6.0)", //
        "0.792123");
    check("RamanujanTauL(12.0)", //
        "0.994544");
    check("RamanujanTauL(6.0 + 2.0*I)", //
        "0.879016+I*0.197862");
    check("RamanujanTauL(6)", //
        "RamanujanTauL(6)");
  }

  @Test
  public void testRamanujanTauZ() {
    // the first zero on the critical line
    check("Chop(RamanujanTauZ(9.22237939992110252), 10^-8)", //
        "0");
    check("RamanujanTauZ(4.0) > 0", //
        "True");
  }

  @Test
  public void testMixedRadixQuantity() {
    check("MixedRadixQuantity({5, 3}, {\"Feet\", \"Inches\"})", //
        "Quantity(MixedMagnitude({5,3}),MixedUnit({\"Feet\",\"Inches\"}))");
    check("UnitConvert(MixedRadixQuantity({2, 7}, {\"Feet\", \"Inches\"}), \"Inches\")", //
        "Quantity(31,\"Inches\")");
    check("MixedRadixQuantity({2}, {\"Feet\", \"Inches\"})", //
        "MixedRadixQuantity({2},{Feet,Inches})");
  }
}
