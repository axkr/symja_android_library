package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Numerical functions with a <code>WorkingPrecision</code> option, and arithmetic with their
 * arbitrary-precision results.
 */
public class WorkingPrecisionTest extends ExprEvaluatorTestCase {

  @Test
  public void testNSumEulerMaclaurin() {
    check("Abs(NSum(1/(n^2+1), {n,1,Infinity}, WorkingPrecision->40) - (Pi*Coth(Pi)-1)/2) < 10^-38", //
        "True");
    check("Precision(NSum(1/(n^2+1), {n,1,Infinity}, WorkingPrecision->40))", //
        "40");
    check("Abs(NSum(1/k^(3/2), {k,1,Infinity}, WorkingPrecision->30) - Zeta(3/2)) < 10^-28", //
        "True");
    // machine precision: no longer the 8 digits of an extrapolation of the partial sums
    check("Abs(NSum(1/(n^2+4), {n,1,Infinity}) - N((2*Pi*Coth(2*Pi)-1)/8, 30)) < 10^-14", //
        "True");
  }

  @Test
  public void testNSumAlternatingAndFast() {
    check("Abs(NSum((-1)^k/(3*k+1), {k,0,Infinity}, WorkingPrecision->30) - (Pi/(3*Sqrt(3))+Log(2)/3)) < 10^-28", //
        "True");
    check("Abs(NSum(2^k/k!, {k,0,Infinity}, WorkingPrecision->50) - E^2) < 10^-48", //
        "True");
    check("Abs(NSum((-1)^k*Sech(k+1/2), {k,0,Infinity}, WorkingPrecision->40) - "
        + "NSum((-1)^k*Sech(k+1/2), {k,0,200}, WorkingPrecision->40)) < 10^-38", //
        "True");
    // two iterators: the sum over the first of the sums over the second
    check("NSum(1/(2^j*3^k), {j,0,Infinity}, {k,0,Infinity})", //
        "3.0");
  }

  @Test
  public void testNSumNoValue() {
    // no numeric summand
    check("NSum(z^k/k!, {k,0,Infinity})", //
        "NSum(z^k/k!,{k,0,Infinity})");
    // a divergent series
    check("NSum(1/k, {k,1,Infinity}, WorkingPrecision->30)", //
        "NSum(1/k,{k,1,Infinity},WorkingPrecision->30)");
  }

  @Test
  public void testArithmeticKeepsPrecision() {
    // an exact number next to a list of arbitrary-precision numbers
    check("Floor(Precision({N(1/7,50), N(2/7,50)} - 1))", //
        "50");
    // an exact number next to the untracked result of a numerical function
    check("Precision(3*NSum(1/(n^3+1), {n,1,Infinity}, WorkingPrecision->35))", //
        "35");
  }

  @Test
  public void testNMinimizeWorkingPrecision() {
    check("Precision(First(NMinimize({(x-2/7)^2+Cos(y)^2, -1<=x<=1, 0<=y<=3}, {x,y}, WorkingPrecision->30)))", //
        "30");
    check("Abs((x/.Last(NMaximize({-(x-2/7)^2+5, -1<=x<=1}, x, WorkingPrecision->30))) - 2/7) < 10^-28", //
        "True");
  }

  @Test
  public void testNMinimizeAtBound() {
    // the optimum lies on the bound 2/9 of x: the exact bound with the working precision
    check("NMinimize({x+(y-1/5)^2, 2/9<=x<=1, -1<=y<=1}, {x,y}, WorkingPrecision->30)", //
        "{0.222222222222222222222222222222,{x->0.222222222222222222222222222222,y->0.2}}");
    // a zero coordinate is a zero of the working precision
    check("FindRoot(Sin(x) == 0, {x, 0.2}, WorkingPrecision->30)", //
        "{x->0.0*10^-30}");
  }

  @Test
  public void testNSumOfNumericFunction() {
    // no symbolic derivative: the Levin transform of the partial sums
    check("r(x_?NumericQ) := 1/(x^2+2); "
        + "Abs(NSum(r(k), {k,1,Infinity}, WorkingPrecision->30) - (Sqrt(2)*Pi*Coth(Sqrt(2)*Pi)-1)/4) < 10^-28", //
        "True");
  }

  @Test
  public void testNSolveInexactCoefficients() {
    check("Precision(t/.First(NSolve((N(1/3,50)+t)^2 == N(2,50), t, Reals, WorkingPrecision->50)))", //
        "50");
  }

  @Test
  public void testFindRootWithoutDerivative() {
    check("g(s_?NumericQ) := N(Zeta(s), 40);"
        + "Abs((s/.FindRoot(g(s) == 3, {s, 1.5}, WorkingPrecision->35)) - 1.41784593578735729296211253832) < 10^-30", //
        "True");
    check("g(s_?NumericQ) := N(Zeta(s), 40);"
        + "Precision(s/.FindRoot(g(s) == 3, {s, 1.5}, WorkingPrecision->35))", //
        "35");
    // a list valued function: one equation per component
    check("w(x_?NumericQ, y_?NumericQ) := {x^3 + y, x*y - 2};"
        + "FindRoot(w(x, y) == {5, 0}, {{x, 1}, {y, 1}})", //
        "{x->1.54777,y->1.29218}");
    check("w(x_?NumericQ, y_?NumericQ) := {x^3 + y, x*y - 2};"
        + "Precision(x/.FindRoot(w(x, y) == {5, 0}, {{x, 1}, {y, 1}}, WorkingPrecision->30))", //
        "30");
  }

  @Test
  public void testIndexedOfNonList() {
    check("Indexed(f(a,b), {1})", //
        "Indexed(f(a,b),{1})");
    check("Indexed({a,b}, {2})", //
        "b");
    check("D(Indexed(v(x,y), {1}), x)", //
        "Indexed(Derivative(1,0)[v][x,y],{1})");
  }

  @Test
  public void testArbitraryPrecisionMatrices() {
    check("Precision(Eigenvalues(N({{3,1},{1,5}}, 40)))", //
        "40");
    check("Abs(First(Eigenvalues(N({{3,1},{1,5}}, 40))) - (4+Sqrt(2))) < 10^-38", //
        "True");
    check("Abs(Norm(N({{2,1},{5,3}}, 30)) - Sqrt(Max(Eigenvalues({{29,17},{17,10}})))) < 10^-28", //
        "True");
    check("Precision(Norm(N({{2,1},{5,3}}, 30)))", //
        "30");
    check("Precision(Norm(N({{2,1},{5,3}}, 30), 1))", //
        "30");
    check("Length(SingularValueList(N({{1,3},{2,6}}, 30)))", //
        "1");
    check("Precision(SingularValueList(N({{1,3},{2,5},{4,1}}, 30)))", //
        "30");
  }

  @Test
  public void testFoldLongList() {
    check("Length(FoldList(#1+1/#2&, 0, Range(5000)))", //
        "5001");
    check("Fold(#1+#2&, 0, Range(5000))", //
        "12502500");
    check("Last(Accumulate(Range(5000)))", //
        "12502500");
  }
}
