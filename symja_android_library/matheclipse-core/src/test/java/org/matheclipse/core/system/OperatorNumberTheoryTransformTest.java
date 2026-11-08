package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Inverse functions, operator forms, number theory, distances and numeric transforms. */
public class OperatorNumberTheoryTransformTest extends ExprEvaluatorTestCase {

  @Test
  public void testInverseFunctionsInsertLU() {
    check("ArcTanh(ComplexInfinity)", //
        "Indeterminate");
    checkNumeric("InverseGudermannian(0.25)", //
        "0.2526456103578675");
    check("Insert(<|x -> 1, y -> 2, z -> 3|>, y -> 7, Key(z))", //
        "<|x->1,y->7,z->3|>");
    check("Insert(<|x -> 1, y -> 2|>, {x -> 5, w -> 6}, 2)", //
        "<|x->5,w->6,y->2|>");
    check("Insert({a, <|k -> {b, c}|>}, n, {{1}, {2, Key(k), 2}})", //
        "{n,a,<|k->{b,n,c}|>}");
    check("Insert({a, b, c}, x, {{2}, {3}})", //
        "{a,x,b,x,c}");
    check("LUDecomposition({{2, 4, 1}, {1, 3, 5}})", //
        "{{{1,0},{1/2,1}},{{2,4,1},{0,1,9/2}},{{1,0},{0,1}},0}");
    check("LUDecomposition({{0, 2}, {1, 1}, {3, 4}})", //
        "{{{1,0,0},{0,1,0},{3,1/2,1}},{{1,1},{0,2},{0,0}},{{0,1,0},{1,0,0},{0,0,1}},0}");
    check("Reduce(x^300 - 3 x^200 + 2 == 0, x, Integers)", //
        "x==-1||x==1");
    check("Reduce(x^4 - 5 x^2 + 4 == 0, x, Primes)", //
        "x==2");
    check("Solve((x + 3)^2 (x - 2)^3 == 0, x, Integers)", //
        "{{x->-3},{x->-3},{x->2},{x->2},{x->2}}");
    check("Solve(x^3 (x - 4) == 0 && x >= 0, x, Integers)", //
        "{{x->0},{x->0},{x->0},{x->4}}");
  }

  @Test
  public void testFoldAndSequenceOperators() {
    check("FoldWhile(Times, 1, {2, 3, 4, 5}, # < 20 &)", //
        "24");
    check("FoldWhile(Plus, {5, 1, 2, 3}, # < 8 &)", //
        "8");
    check("FoldWhile(Plus, # < 8 &)[{5, 1, 2, 3}]", //
        "8");
    check("FoldPair({#2, #1 + #2} &, 0, {a, b, c})", //
        "c");
    check("FoldPair({#1, #1 * #2} &, {2, 3, 4})", //
        "6");
    check("FoldPair({#1, #1 + #2} &, 0, {1, 2, 3}, Last)", //
        "6");
    check("ReverseApplied(g)[1, 2, 3, 4]", //
        "g(4,3,2,1)");
    check("ReverseApplied(g, 3)[1, 2, 3, 4]", //
        "g(3,2,1,4)");
    check("ReverseApplied(g, -3)[1, 2, 3, 4]", //
        "g(1,4,3,2)");
    check("LexicographicSort({{3, 1}, {1, 2, 3}, {1, 2}, {2}})", //
        "{{1,2},{1,2,3},{2},{3,1}}");
    checkNumeric("LexicographicSort({{2., 1}, {1, 5}, {-3, 0}}, NumericalOrder)", //
        "{{-3,0},{1,5},{2.0,1}}");
    check("CenterArray({a, b}, 6)", //
        "{0,0,a,b,0,0}");
    check("CenterArray({a, b, c}, 4, x)", //
        "{a,b,c,x}");
    check("CenterArray({{1, 2}, {3, 4}}, {4, 4})", //
        "{{0,0,0,0},{0,1,2,0},{0,3,4,0},{0,0,0,0}}");
    check("CenterArray(2)", //
        "{1,0}");
    check("DeleteAdjacentDuplicates({1, 1, 2, 2, 2, 1, 3, 3})", //
        "{1,2,1,3}");
    check("DeleteAdjacentDuplicates({1, 3, 5, 2, 4}, OddQ(#1) == OddQ(#2) &)", //
        "{1,2}");
  }

  @Test
  public void testNumberTheoryFunctions() {
    check("DiracComb(-2 y)", //
        "DiracComb(2*y)");
    check("DiracComb(0.5)", //
        "0");
    check("NumberDecompose(E, {1, 1/100})", //
        "{2,100*(-2+E)}");
    check("NumberDecompose(23/4, {2, 1, 1/4})", //
        "{2,1,3}");
    check("ModularInverse(2 + 3 I, 7 + 2 I)", //
        "3");
    check("Mod((2 + 3 I) ModularInverse(2 + 3 I, 7 + 2 I), 7 + 2 I)", //
        "1");
    check("SquareFreeQ(x^2 y - y^3)", //
        "True");
    check("SquareFreeQ(x^2 y^2 - 2 x y + 1)", //
        "False");
    check("SquareFreeQ(x + y + z)", //
        "True");
    check("PermutationCycles(Cycles({{2, 4}}), Identity)", //
        "{{1},{2,4},{3}}");
    check("PositionLargest(<|p -> 4, q -> 9, r -> 9, s -> 1|>)", //
        "{2,3}");
    check("PermutationSupport(PermutationGroup({Cycles({{2, 6}}), Cycles({{3, 7, 9}})}))", //
        "{2,3,6,7,9}");
    check("PermutationMax(PermutationGroup({Cycles({{2, 6}}), Cycles({{3, 7, 9}})}))", //
        "9");
    check("LiouvilleLambda(2 + I)", //
        "-1");
    check("LiouvilleLambda(5 + 12 I)", //
        "1");
    check("DigitCount(100200, 10, 0, 8)", //
        "6");
    check("DigitCount(987654, 10, {9, 4}, 3)", //
        "{0,1}");
    check("IntegerReverse(2000, MixedRadix({12, 5}))", //
        "53");
    check("MemberQ(Attributes(MaxMemoryUsed), HoldAll)", //
        "True");
  }

  @Test
  public void testDistanceFourierBiweight() {
    check("DistanceMatrix({0, 3, 7})", //
        "{{0,3,7},{3,0,4},{7,4,0}}");
    check("DistanceMatrix({{0, 0}, {3, 4}}, {{1, 1}})", //
        "{{Sqrt(2)},{Sqrt(13)}}");
    check("DistanceMatrix({\"cat\", \"cart\", \"dog\"})", //
        "{{0,1,3},{1,0,4},{3,4,0}}");
    check("DistanceMatrix({{x, y}, {x, z, y}})", //
        "{{0,1},{1,0}}");
    check("DistanceMatrix({{1, 2, 3}, {2, 3}})", //
        "{{0,1},{1,0}}");
    check("DistanceMatrix({{0, 0}, {3, 4}}, DistanceFunction -> ChessboardDistance)", //
        "{{0,4},{4,0}}");
    checkNumeric("FourierDCT({1, 2, 3}, 3)", //
        "{4.309401076758504,-2.886751345948129,0.3094010767585034}");
    checkNumeric("FourierDST({1, 2, 3})", //
        "{2.309401076758503,-1.0,1.154700538379252}");
    checkNumeric("FourierDST({1, 2, 3}, 1)", //
        "{3.414213562373095,-1.414213562373095,0.5857864376269045}");
    checkNumeric("FourierDST({1, 2, 3}, \"III\")", //
        "{4.309401076758503,-0.5773502691896255,0.3094010767585032}");
    check("BiweightMidvariance({2, 4, 4, 5, 9})", //
        "142478188/30630615");
    checkNumeric("BiweightMidvariance({2., 4., 4., 5., 9.})", //
        "4.65149615833701");
    check("BiweightMidvariance({2, 4, 4, 5, 9}, 6)", //
        "1010159/343470");
    check("BiweightMidvariance({1, 1, 1, 1})", //
        "Indeterminate");
  }

  @Test
  public void testGeometryHornerRegionBounds() {
    check("PlanarAngle({{0, 1}, {0, 0}, {1, 1}}, \"FullExterior\")", //
        "7/4*Pi");
    check("AngleBisector({{4, 0}, {0, 0}, {0, 2}}, \"Exterior\")", //
        "InfiniteLine({0,0},{-1,1})");
    check("DesignMatrix({4, 5, 7}, {1, x, x^2}, x)", //
        "{{1,1,1},{1,2,4},{1,3,9}}");
    checkNumeric("DesignMatrix({0.5, 1.5}, {1, x}, x)", //
        "{{1.0,1.0},{1.0,2.0}}");
    check("HornerForm(1 + x y + x^2 y^2 + x^2 y^3, {x, y})", //
        "1+x*(y+x*y^2*(1+y))");
    check("HornerForm(a x + a^2 x^2 + b x^2, {x, a})", //
        "x*(a+(a^2+b)*x)");
    check("RegionBounds(Tetrahedron({{1, 0, 2}, {3, 1, 0}, {0, 4, 1}, {2, 2, 5}}))", //
        "{{0,3},{0,4},{0,5}}");
    check("RegionBounds(InfinitePlane({{0, 0, 1}, {1, 0, 1}, {0, 0, 2}}))", //
        "{{-Infinity,Infinity},{0,0},{-Infinity,Infinity}}");
    check("RegionBounds(InfiniteLine({1, 2}, {0, 3}))", //
        "{{1,1},{-Infinity,Infinity}}");
    check("RegionBounds(InfiniteLine({1, 2}, {2, 3}), \"Finite\")", //
        "{{1,3},{2,5}}");
    check("RegionBounds(HalfLine({1, 2}, {-1, 1}), \"Minimal\")", //
        "{{-Infinity,1},{2,Infinity}}");
  }

  @Test
  public void testDistributionFormulas() {
    check("PDF(InverseChiSquareDistribution(n, s), y)", //
        "Piecewise({{((n*s)/y)^(n/2)/(2^(n/2)*E^((n*s)/(2*y))*y*Gamma(n/2)),y>0}},0)");
    check("CDF(InverseChiSquareDistribution(n), y)", //
        "Piecewise({{GammaRegularized(n/2,1/(2*y)),y>0}},0)");
    check("Mean(InverseChiSquareDistribution(7, 3))", //
        "21/5");
    check("Variance(InverseChiSquareDistribution(9, 2))", //
        "648/245");
    check("Variance(InverseChiSquareDistribution(3, 2))", //
        "Indeterminate");
    check("Median(InverseChiSquareDistribution(n))", //
        "1/(2*InverseGammaRegularized(n/2,1/2))");
    check("Quantile(InverseChiSquareDistribution(n), 1/3)", //
        "1/(2*InverseGammaRegularized(n/2,1/3))");
    checkNumeric("Mean(InverseChiSquareDistribution(20.5, 0.4))", //
        "0.4432432432432433");
    check("Variance(GammaDistribution(a, b, 3, m))", //
        "(b^2*(-Gamma(1/3+a)^2+Gamma(2/3+a)*Gamma(a)))/Gamma(a)^2");
    check("Variance(GammaDistribution(1, b, 1, m))", //
        "b^2");
    check("Variance(InverseGaussianDistribution(2, 3, t))", //
        "(4*(-BesselK(1+t,3/2)^2+BesselK(t,3/2)*BesselK(2+t,3/2)))/BesselK(t,3/2)^2");
    check("PDF(InverseGaussianDistribution(m, l, t), y)", //
        "Piecewise({{E^((l*(-1-y^2/m^2))/(2*y))/(2*m^t*y^(1-t)*BesselK(t,l/m)),y>0}},0)");
    check("PDF(GompertzMakehamDistribution(a, b, c, d), y)", //
        "Piecewise({{(a*b*(c+E^(a*y)+2*a*d*y))/E^(b*(-1+E^(a*y)+a*c*y+a^2*d*y^2)),y>=0}},\n0)");
    check("CDF(GompertzMakehamDistribution(a, b, c, d), y)", //
        "Piecewise({{1-1/E^(b*(-1+E^(a*y)+a*c*y+a^2*d*y^2)),y>=0}},0)");
    check("CDF(GompertzMakehamDistribution(1, 2, 0, 0), y)", //
        "Piecewise({{1-1/E^(2*(-1+E^y)),y>=0}},0)");
  }

  @Test
  public void testDMSString() {
    check("DMSString({12, 30, 1.5, \"W\"})", //
        "12°30'1.500\"");
    check("DMSString({12, 30, 1.5, \"W\"}, \"EW\")", //
        "12°30'1.500\"W");
    check("DMSString(-45.25, {\"NS\", 2})", //
        "45°15'0.00\"S");
    check("DMSString(45.123456, 5)", //
        "45°7'24.44160\"");
    check("DMSString(\"10d30m S\", \"NS\")", //
        "10°30'0\"S");
    check("DMSString(91/4)", //
        "22°45'0\"");
    check("DMSString({51.5, -0.125})", //
        "51°30'0.000\"N  0°7'30.000\"W");
    check("DMSString(Quantity(1, \"Radians\"))", //
        "57°17'44.806\"");
  }

  @Test
  public void testFindDivisions() {
    check("FindDivisions({0, 1}, 4, 2)", //
        "{0,1/4,1/2,3/4,1}");
    check("FindDivisions({0, 30}, 3)", //
        "{0,10,20,30}");
    check("FindDivisions({-1, 1}, 4)", //
        "{-1,-1/2,0,1/2,1}");
    check("FindDivisions({0, 9, E}, 3)", //
        "{0,2*E,4*E}");
    check("FindDivisions({0, 2, 1/4}, 4)", //
        "{0,1/2,1,3/2,2}");
    check("FindDivisions({0, 10}, {2, 2})", //
        "{{0,5,10},{{0,5/2,5},{5,15/2,10}}}");
  }

  @Test
  public void testPeaks() {
    check("PeakDetect({1, 4, 2, 5, 5, 3, 6, 1}, 0, 0)", //
        "{0,1,0,1,1,0,1,0}");
    check("PeakDetect({1, 4, 2, 5, 5, 3, 6, 1}, 0, 4)", //
        "{0,1,0,0,0,0,1,0}");
    check("PeakDetect({1, 4, 2, 5, 5, 3, 6, 1}, 0, 0, 5)", //
        "{0,0,0,1,1,0,1,0}");
    check("PeakDetect({3, 1, 4, 1, 3}, 0, Padding -> 5)", //
        "{0,0,1,0,0}");
    check("FindPeaks({1, 4, 2, 5, 5, 3, 6, 1})", //
        "{{2,4},{9/2,5},{7,6}}");
    check("FindPeaks({1, 4, 2, 5, 5, 3, 6, 1}, 0, 2)", //
        "{{2,4},{9/2,5},{7,6}}");
    check("FindPeaks({3, 1, 4, 1, 3}, 0, Padding -> 2)", //
        "{{1,3},{3,4},{5,3}}");
    check("FindPeaks({5, 5, 1, 2})", //
        "{{1,5},{4,2}}");
    check("Round(10^4 FindPeaks({1, 2, 0, 3, 1}, 0, InterpolationOrder -> 3))", //
        "{{18916,20504},{40394,30104}}");
    check("Round(10^4 FindPeaks({1, 2, 0, 3, 1}, 0, InterpolationOrder -> 2))", //
        "{{18765,20484},{40569,30156}}");
  }

  @Test
  public void testNMinimizeListInterpolation() {
    checkNumeric("NMinimize({2 x + y, {x - y, x + 2 y} == {1, 4}}, {x, y})", //
        "{5.0,{x->2.0,y->1.0}}");
  }

  @Test
  public void testNumericTransforms() {
    // the branch of Log(Tan(Pi/4 + z/2)) for a real z in (Pi/2, 3*Pi/2)
    checkNumeric("N(InverseGudermannian(3))", //
        "0.1420681583893966+I*3.141592653589793");
    check("Chop(FourierDCT({1, 2, 3}) - {2*Sqrt(3.0), -1.0, 0})", //
        "{0,0,0}");
    check("Chop(FourierDCT(FourierDCT({4, 1, 7, 2}), 3) - {4, 1, 7, 2})", //
        "{0,0,0,0}");
    check("Chop(FourierDST(FourierDST({4, 1, 7, 2}, 4), 4) - {4, 1, 7, 2})", //
        "{0,0,0,0}");
    check("Chop(FourierDCT({{1, 0}, {0, 1}}) - {{1, 0}, {0, 0.5}})", //
        "{{0,0},{0,0}}");
    check("f = ListInterpolation({2, 0, 1, 3}, {{1, 2, 4, 7}});" //
        + "{Chop(f(4) - 1), f(3) == Interpolation({{1, 2}, {2, 0}, {4, 1}, {7, 3}})[3]}", //
        "{0,True}");
    check("Abs(Mean(RandomVariate(InverseChiSquareDistribution(12, 1/2), 4000)) - 3/5) < 0.05", //
        "True");
  }
}
