package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for the numerical equation solvers <code>NSolve</code> and <code>NSolveValues</code>.
 *
 * <p>
 * The expected values are written in Symja's own output syntax - a machine number prints as
 * <code>5.0</code>, a complex number as <code>I*(-1.0)</code> and not as <code>-1.*I</code>.
 * Everything which is compared digit for digit uses
 * {@link ExprEvaluatorTestCase#checkNumeric(String, String)}, because
 * {@link ExprEvaluatorTestCase#check(String, String)} prints only six significant digits and
 * because the last digits of a machine number differ between CPU architectures.
 */
public class NSolveTest extends ExprEvaluatorTestCase {

  @Test
  public void testNSolveLinearEquation() {
    check("NSolve(x-5==0,x)", //
        "{{x->5.0}}");
  }

  @Test
  public void testNSolveQuadraticIntegerRoots() {
    check("NSolve(x^2-4==0,x)", //
        "{{x->-2.0},{x->2.0}}");
  }

  @Test
  public void testNSolveQuadraticIrrationalRoots() {
    checkNumeric("NSolve(x^2+x-1==0,x)", //
        "{{x->-1.618033988749895},{x->0.6180339887498948}}");
  }

  @Test
  public void testNSolveSystemSolutionsLargerRootFirst() {
    checkNumeric(
        "NSolve(y==-0.8090169943749475&&(x-0.7694208842938133)^2+(y-(-0.25))^2==1.090330521158122^2,{x,y})", //
        "{{x->-0.16669911088252198,y->-0.8090169943749475},{x->1.7055408794701485,y->-0.8090169943749475}}");
  }

  @Test
  public void testNSolveQuadraticDoubleRoot() {
    // the discriminant is rounding noise: the double root isn't split
    check("NSolve(4.0*x^2 + 2.4*x + 0.36 == 0, x) // InputForm", //
        "{{x->-0.3`},{x->-0.3`}}");
    check("NSolve(x^2 - 1.414213562373095*x + 0.5 == 0, x) // InputForm", //
        "{{x->0.7071067811865475`},{x->0.7071067811865475`}}");
    check("Roots(3.0*x^2 - 0.738*x + 0.045387 == 0, x)", //
        "x==0.123");
    // two close roots stay apart
    check("NSolve(x^2 - 2.5*x + 1.5624 == 0, x) // InputForm", //
        "{{x->1.2400000000000007`},{x->1.2599999999999993`}}");
  }

  @Test
  public void testNSolveQuadraticComplexRoots() {
    check("NSolve(x^2+1==0,x)", //
        "{{x->I*(-1.0)},{x->I*1.0}}");
  }

  @Test
  public void testNSolveCubicRoots() {
    check("NSolve(x^3-3*x^2+2*x==0,x)", //
        "{{x->0.0},{x->1.0},{x->2.0}}");
  }

  @Test
  public void testNSolveCubicWithMachineRealCoefficients() {
    check("Round({Re(x),Im(x)}/.NSolve(x^3+1.5*x^2-3.2*x+4.7==0,x),1/10^6)", //
        "{{-19079/6250,0},{2426/3125,-120997/125000},{2426/3125,120997/125000}}");
    // the root 0 has multiplicity 2
    check("NSolve(x^3-4.*x^2==0,x)", //
        "{{x->0.0},{x->0.0},{x->4.0}}");
  }

  @Test
  public void testNSolveRootsOrderedByRealThenImaginaryPart() {
    checkNumeric("NSolve(x^3-2==0,x)", //
        "{{x->-0.6299605249474366+I*(-1.0911236359717214)},{x->-0.6299605249474366+I*1.0911236359717214},{x->1.2599210498948732}}");
    check("NSolve(x^4-1==0,x)", //
        "{{x->-1.0},{x->I*(-1.0)},{x->I*1.0},{x->1.0}}");
  }

  @Test
  public void testNSolveWithUserDefinedFunction() {
    checkNumeric("f(x_):=x^2+x+1;NSolve(f(b)-2==0,b)", //
        "{{b->-1.618033988749895},{b->0.6180339887498948}}");
  }

  @Test
  public void testNSolveRationalSolution() {
    check("NSolve(2*x-1==0,x)", //
        "{{x->0.5}}");
  }

  @Test
  public void testNSolveCubeRootsOfUnityFullyNumericized() {
    check("FreeQ(NSolve(x^3==1,x),Power)", //
        "True");
    check("Round(x/.NSolve(x^3==1,x),1/1000)", //
        "{-1/2-I*433/500,-1/2+I*433/500,1}");
  }

  @Test
  public void testNSolveDomainArgument() {
    checkNumeric("NSolve(x^2==2,x,Reals)", //
        "{{x->-1.4142135623730951},{x->1.4142135623730951}}");
    check("NSolve(x^2+1==0,x,Reals)", //
        "{}");
    check("NSolve(x^3-1==0,x,Reals)", //
        "{{x->1.0}}");
    check("NSolve(x^2==2,x,Integers)", //
        "{}");
    check("NSolve(x^2==2,x,Rationals)", //
        "{}");
    check("NSolve(x^2-4==0,x,Rationals)", //
        "{{x->-2.0},{x->2.0}}");
    check("NSolve({x+y==3,x-y==1},{x,y},Reals)", //
        "{{x->2.0,y->1.0}}");
  }

  @Test
  public void testNSolveDomainRealsNumericRoots() {
    checkNumeric("NSolve(x^5-x-1==0,x,Reals)", //
        "{{x->1.1673039782614187}}");
    check("NSolve(x^4-1==0,x,Reals)", //
        "{{x->-1.0},{x->1.0}}");
    check("Length(NSolve(x^5-x-1==0,x))", //
        "5");
  }

  @Test
  public void testNSolveBarePolynomialMeansEqualZero() {
    check("NSolve({x+y-3,x-y-1},{x,y})", //
        "{{x->2.0,y->1.0}}");
    check("NSolve({x+y-3,x-y==1},{x,y})", //
        "{{x->2.0,y->1.0}}");
  }

  @Test
  public void testNSolveWorkingPrecision() {
    // the fourth argument is the working precision of the numerical solution
    check("NSolve(x^2==2,x,Reals,30)", //
        "{{x->-1.41421356237309504880168872421},{x->1.41421356237309504880168872421}}");
    check("NSolve(x^2==2,x,Reals,WorkingPrecision->30)", //
        "{{x->-1.41421356237309504880168872421},{x->1.41421356237309504880168872421}}");
    check("NSolveValues(x^2==2,x,Reals,30)", //
        "{-1.41421356237309504880168872421,1.41421356237309504880168872421}");
    // MachinePrecision and Automatic are the machine precision solution
    check("NSolve(x^2==2,x,Reals,MachinePrecision)", //
        "{{x->-1.41421},{x->1.41421}}");
    // the solutions are ordered by real, then imaginary part at every working precision
    check("NSolve(x^3-2==0,x,Complexes,20)", //
        "{{x->-0.62996052494743658238+I*(-1.0911236359717214036)},{x->-0.62996052494743658238+I*1.0911236359717214036},{x->1.2599210498948731648}}");
    // Requested precision `1` is smaller than `2`.
    check("NSolve(x^2==2,x,Reals,-5)", //
        "NSolve(x^2==2,x,Reals,-5)");
  }

  @Test
  public void testNSolveValuesSingleVariable() {
    checkNumeric("NSolveValues(x^2==2,x)", //
        "{-1.4142135623730951,1.4142135623730951}");
    check("NSolveValues(2*x-1==0,x)", //
        "{0.5}");
  }

  @Test
  public void testNSolveValuesRealsDomain() {
    checkNumeric("NSolveValues(x^5-x-1==0,x,Reals)", //
        "{1.1673039782614187}");
    check("NSolveValues(x^4-1==0,x,Reals)", //
        "{-1.0,1.0}");
    check("NSolveValues(x^2+1==0,x,Reals)", //
        "{}");
    check("Length(NSolveValues(x^5-x-1==0,x))", //
        "5");
  }

  @Test
  public void testNSolveValuesMultipleVariables() {
    check("NSolveValues({x+y==3,x-y==1},{x,y})", //
        "{{2.0,1.0}}");
  }

  @Test
  public void testNSolveMachineNumbersWithParameters() {
    check("sol=NSolve({(x-a)^2+(y-b)^2==9,0.6*x-0.8*y==0.5},{x,y});"
        + "{Length(sol),Max(Abs(Sort(Chop({x,y}/.sol/.{a->1,b->-1}))-Sort({x,y}/.NSolve({(x-1)^2+(y+1)^2==9,0.6*x-0.8*y==0.5},{x,y}))))<10^-12}", //
        "{2,True}");
  }
}
