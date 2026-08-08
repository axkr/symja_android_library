package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class ExpTrigsFunctionsTest extends ExprEvaluatorTestCase {

  /**
   * <code>ArcTan(x,y)</code> is the polar angle of the point <code>(x,y)</code> and that angle is
   * scale invariant: <code>(3*10^-20, 4*10^-20)</code> is the very same 3-4-5 direction as
   * <code>(3,4)</code>, so the answer is <code>ArcTan(4/3) = 0.927295...</code> no matter how short
   * the vector is.
   *
   * <p>
   * This used to answer <code>Indeterminate</code> resp. <code>3/16*Pi</code>, because the zero
   * test was tolerance based (<code>Config#DOUBLE_TOLERANCE</code>, about <code>1.11*10^-15</code>)
   * and every point closer to the origin than that collapsed onto the undefined
   * <code>ArcTan(0,0)</code>.
   * </p>
   */
  @Test
  public void testArcTanTwoArgumentsTinyValues() {
    // all four quadrants far below the zero tolerance ...
    check("ArcTan(3.0*10^-20,4.0*10^-20)", //
        "0.927295");
    check("ArcTan(-3.0*10^-20,4.0*10^-20)", //
        "2.2143");
    check("ArcTan(-3.0*10^-20,-4.0*10^-20)", //
        "-2.2143");
    check("ArcTan(3.0*10^-20,-4.0*10^-20)", //
        "-0.927295");

    // ... are the same angles as at ordinary magnitude
    check("ArcTan(3.0,4.0)", //
        "0.927295");
    check("ArcTan(-3.0,4.0)", //
        "2.2143");
    check("ArcTan(-3.0,-4.0)", //
        "-2.2143");
    check("ArcTan(3.0,-4.0)", //
        "-0.927295");
  }

  /** The angle does not depend on the length of the vector, for any number type. */
  @Test
  public void testArcTanTwoArgumentsScaleInvariance() {
    // exact rationals
    check("ArcTan(3/10^20,4/10^20)", //
        "ArcTan(4/3)");
    check("N(ArcTan(3/10^20,4/10^20),50)", //
        "0.92729521800161223242851246292242880405707410857224");

    // machine precision doubles, above and below the zero tolerance
    check("ArcTan(3.0*10^-13,4.0*10^-13)", //
        "0.927295");
    check("ArcTan(3.0*10^-16,4.0*10^-16)", //
        "0.927295");
    check("ArcTan(3.0*10^-300,4.0*10^-300)", //
        "0.927295");
    check("ArcTan(-3.0*10^-300,4.0*10^-300)", //
        "2.2143");

    // arbitrary precision reals
    check("ArcTan(3`20*10^-20,4`20*10^-20)", //
        "0.92729521800161223242");

    // the same direction reached through Arg() of a tiny complex number
    check("Arg(3.0*10^-20+I*4.0*10^-20)", //
        "0.927295");
  }

  /** Only the true origin has no angle. */
  @Test
  public void testArcTanTwoArgumentsOrigin() {
    check("ArcTan(0,0)", //
        "Indeterminate");
    check("ArcTan(0.0,0.0)", //
        "Indeterminate");

    // a point on the y axis keeps its right angle, however tiny it is
    check("ArcTan(0,1)", //
        "Pi/2");
    check("ArcTan(0,-1)", //
        "-Pi/2");
    check("ArcTan(0,4.0*10^-20)", //
        "Pi/2");
    check("ArcTan(0,-4.0*10^-20)", //
        "-Pi/2");

    // a point on the x axis: 0 for a positive and Pi for a negative x
    check("ArcTan(1.0*10^-20,0)", //
        "0");
    check("ArcTan(-1.0*10^-20,0)", //
        "Pi");
  }

  /** A tiny coordinate combined with an ordinary one must not collapse either. */
  @Test
  public void testArcTanTwoArgumentsMixedMagnitudes() {
    check("ArcTan(1.0*10^-20,1.0)", //
        "1.5708");
    check("ArcTan(-1.0*10^-20,1.0)", //
        "1.5708");
    check("ArcTan(1.0,1.0*10^-20)", //
        "1.*10^-20");
    check("ArcTan(1.0,-1.0*10^-20)", //
        "-1.*10^-20");

    // a symbolic numerator over a tiny denominator still has to divide
    check("ArcTan(3.0*10^-20,Pi)", //
        "1.5708");
  }

  /** The established values of the two argument form must not regress. */
  @Test
  public void testArcTanTwoArgumentsOrdinaryValues() {
    check("ArcTan(1,1)", //
        "Pi/4");
    check("ArcTan(-1,-1)", //
        "-3/4*Pi");
    check("ArcTan(-1,1)", //
        "3/4*Pi");
    check("ArcTan(1,-1)", //
        "-Pi/4");
    check("ArcTan(1,0)", //
        "0");
    check("ArcTan(-1,0)", //
        "Pi");
    check("ArcTan(1,Sqrt(3))", //
        "Pi/3");
    check("ArcTan(Infinity,1)", //
        "0");
    check("N(ArcTan(2, 1), 50)", //
        "0.4636476090008061162142562314612144020285370542861");

    // the diagonals at inexact magnitude are numeric, in every quadrant
    check("ArcTan(2.0,2.0)", //
        "0.785398");
    check("ArcTan(-2.0,-2.0)", //
        "-2.35619");
    check("ArcTan(2.0*10^-20,2.0*10^-20)", //
        "0.785398");
    check("ArcTan(-2.0*10^-20,-2.0*10^-20)", //
        "-2.35619");

    // symbolic arguments stay unevaluated
    check("ArcTan(x,y)", //
        "ArcTan(x,y)");
  }
}
