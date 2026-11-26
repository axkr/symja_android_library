package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Decisions which a machine value cannot make: identities of roots which hold only on a part of
 * the complex plane, signs of exact numbers which are zero or nearly cancel, and the scoping and
 * control flow of evaluation which must not repeat or lose a step.
 */
public class ExactDecisionTest extends ExprEvaluatorTestCase {

  @Test
  public void testProductsOfSquareRoots() {
    // Sqrt(x-3)*Sqrt(x+3) is -Sqrt(x^2-9) for x<-3
    check("FullSimplify(Sqrt(x-3)*Sqrt(x+3))", //
        "Sqrt(-3+x)*Sqrt(3+x)");
    check("Simplify(Sqrt(-3-x)*Sqrt(-3+x))", //
        "Sqrt(-3-x)*Sqrt(-3+x)");
    // the sum of the factors is positive: the identity holds everywhere
    check("FullSimplify(Sqrt(4-x)*Sqrt(4+x))", //
        "Sqrt(16-x^2)");
    check("FunctionExpand(Sqrt(x^2-9))", //
        "Sqrt(-9+x^2)");
    check("FunctionExpand(Sqrt(9-x^2))", //
        "Sqrt(3-x)*Sqrt(3+x)");
    check("Refine(Sqrt(u)*Sqrt(v), u<0 && v<0)", //
        "-Sqrt(u*v)");
    check("Refine(Sqrt(u*v), u<0 && v>0)", //
        "I*Sqrt(-u*v)");
  }

  @Test
  public void testHiddenZero() {
    // (1+Sqrt(10))^2 == 11+2*Sqrt(10); the machine value of w is a rounding error below 0
    check("w=Sqrt(11+2*Sqrt(10))-1-Sqrt(10);{Sign(w),UnitStep(w),KroneckerDelta(w),DiscreteDelta(w)}", //
        "{0,1,1,1}");
    check("z=Sqrt(19+8*Sqrt(3))-4-Sqrt(3);{Sign(z),(1+z)^Infinity}", //
        "{0,Indeterminate}");
  }

  @Test
  public void testRoundingAfterCancellation() {
    // the machine values are off by more than 1 after the cancellation of 22 and 20 digits
    check("Floor(10^22*(E-2718281828459045/10^15))", //
        "2353602");
    check("Round(10^20*(Pi-355/113))", //
        "-26676418906242");
  }

  @Test
  public void testModularRoots() {
    check("PowerMod(4, 1/2, 32)", //
        "2");
  }

  @Test
  public void testAbsoluteIrreducibility() {
    check("IrreduciblePolynomialQ(x^2-3, Extension->All)", //
        "False");
    check("IrreduciblePolynomialQ(u^2-5*v^2, Extension->All)", //
        "False");
    check("IrreduciblePolynomialQ(x^2+y^2+z^2-1, Extension->All)", //
        "True");
  }

  @Test
  public void testSolveAlwaysTranscendental() {
    check("SolveAlways(a*Cos(x)+b*x==0, x)", //
        "{{a->0,b->0}}");
    check("SolveAlways(p*Exp(2*x)+q==5*Exp(2*x)-1, x)", //
        "{{p->5,q->-1}}");
  }

  @Test
  public void testPartTargetEvaluatedTwice() {
    // s[[i]] op= x is s[[i]] = s[[i]] op x: the part is read with one index and written with the
    // next one
    check("j=1;v={5,6,7};{v[[j++]]*=3,j,v}", //
        "{15,3,{5,15,7}}");
    check("j=1;v={10,20};{v[[j++]]++,j,v}", //
        "{10,3,{10,11}}");
  }

  @Test
  public void testBreakInPredicate() {
    check("n=0;Do(Select({1,2,3},(If(#==2,Break());True)&);n++,{4});n", //
        "0");
  }

  @Test
  public void testScopedSubstitution() {
    // the initializer belongs to the outer scope, the body to the inner one
    check("Function(y, Module({y=2*y}, y+1))[4]", //
        "9");
    check("Function({u,w}, With({u=w}, u*w))[3,5]", //
        "25");
    // Block localizes values: its local list receives the parameter, also next to a Module
    check("Function(y, {Module({y=1}, y), Block({y=2}, y)})[7]", //
        "{1,Block({7=2},7)}");
    check("c=0;ComposeList({(c++;#)&,(c++;#)&},t);c", //
        "2");
  }

  @Test
  public void testIntervalsAndSameTest() {
    check("Max(Interval({0,4}), Interval({1,9}))", //
        "9");
    check("Union({1,2,3,4,5,6,7}, SameTest->(Mod(#1-#2,4)==0&))", //
        "{1,2,3,4}");
  }

  @Test
  public void testSingularArguments() {
    // Beta(m,n) == (n-1)!/Pochhammer(m,n) on the lattice of the poles of Gamma
    check("{Beta(-4,2),Beta(3,-6)}", //
        "{1/12,-1/60}");
    check("Log(1,1)", //
        "Indeterminate");
    check("EvenQ(4,5)", //
        "EvenQ(4,5)");
    check("D(x^3,{3,1})", //
        "D(x^3,{3,1})");
    check("FlattenAt(HoldComplete({p,q},r),1)", //
        "HoldComplete(p,q,r)");
    check("Head(KeyUnion({<|a->1|>,<|b->2|>})[[2,Key(a),1]])", //
        "String");
  }
}
