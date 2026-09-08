package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for <code>NResidue</code>, the numerical residue by contour integration.
 *
 * <p>
 * The results are wrapped in <code>Chop</code> throughout: a trapezoidal contour sum leaves a
 * rounding residual of a few ulps in the component that is mathematically zero, and the exact size
 * of that residual isn't reproducible across platforms.
 */
public class NResidueTest extends ExprEvaluatorTestCase {

  @Test
  public void testNResidueSimplePole() {
    check("Chop(NResidue(1/x, {x, 0}))", //
        "1.0");
    check("Chop(NResidue(1/(x-2), {x, 2}))", //
        "1.0");
    check("Chop(NResidue(3/(x+5), {x, -5}))", //
        "3.0");
    check("Chop(NResidue(1/(z^4-1), {z, 1}))", //
        "0.25");
    // 1/(1.7-2.7*z+z^2) has simple poles at 1 and 1.7, the residue at 1 is -10/7
    check("Chop(NResidue(1/(1.7-2.7*z+z^2), {z, 1.0}))", //
        "-1.42857");
  }

  @Test
  public void testNResidueComplexPole() {
    check("Chop(NResidue(1/(x-I), {x, I}))", //
        "1.0");
    check("Chop(NResidue(1/(z^2+1), {z, I}))", //
        "I*(-0.5)");
    check("Chop(NResidue(z/(z^2+1), {z, I}))", //
        "0.5");
  }

  @Test
  public void testNResidueNoPole() {
    // a residue of zero is reached as an absolute tolerance - no relative goal could ever be met
    check("Chop(NResidue(1/z^2, {z, 0}))", //
        "0");
    check("Chop(NResidue(z^2, {z, 0}))", //
        "0");
  }

  @Test
  public void testNResidueTranscendental() {
    check("Chop(NResidue(Cot(z), {z, 0}))", //
        "1.0");
    check("Chop(NResidue(1/Sin(z), {z, Pi}))", //
        "-1.0");
    check("Chop(NResidue(Tan(z), {z, Pi/2}))", //
        "-1.0");
    check("Chop(NResidue(Gamma(z), {z, -1}))", //
        "-1.0");
    check("Chop(NResidue(Zeta(z), {z, 1}))", //
        "1.0");
  }

  @Test
  public void testNResidueEssentialSingularity() {
    // Series() has no Laurent expansion here, so the symbolic Residue() cannot do these
    check("Chop(NResidue(Sin(1/(10*x)), {x, 0}))", //
        "0.1");
    check("Chop(NResidue(Exp(1/x), {x, 0}, Radius -> 1))", //
        "1.0");
    check("Chop(NResidue(Sin(1/x), {x, 0}, Radius -> 1))", //
        "1.0");
    check("Chop(NResidue(Cos(1/x), {x, 0}, Radius -> 1))", //
        "0");
  }

  @Test
  public void testNResidueRadius() {
    // 1/x + 1/(x+0.005) has two poles 0.005 apart. The default radius 1/100 encloses both and
    // sums their residues, a radius of 0.001 encloses only the one at 0.
    check("Chop(NResidue(1/x + 1/(x+0.005), {x, 0}))", //
        "2.0");
    check("Chop(NResidue(1/x + 1/(x+0.005), {x, 0}, Radius -> 0.001))", //
        "1.0");
  }

  @Test
  public void testNResidueAutomaticRadius() {
    // Exp(1/x) needs a radius of about 1 - Radius -> Automatic finds it, the default 1/100 fails
    check("Chop(NResidue(Exp(1/x), {x, 0}, Radius -> Automatic))", //
        "1.0");
    check("Chop(NResidue(1/x, {x, 0}, Radius -> Automatic))", //
        "1.0");
  }

  @Test
  public void testNResidueListThreading() {
    // NResidue threads element wise over the first argument only - the {x, x0} specification is
    // never split. Mathematica leaves a list argument unevaluated instead.
    check("Chop(NResidue({Exp(1/x), Sin(1/x), Cos(1/x)}, {x, 0}, Radius -> 1))", //
        "{1.0,1.0,0}");
    check("Chop(NResidue({{1/x, 2/x}, {3/x}}, {x, 0}))", //
        "{{1.0,2.0},{3.0}}");
  }

  @Test
  public void testNResidueWorkingPrecision() {
    check("NResidue(1/(x-2), {x, 2}, WorkingPrecision -> 30)", //
        "1");
    check("Precision(NResidue(1/(x-2), {x, 2}, WorkingPrecision -> 30))", //
        "30");
    // the residue of Gamma at the pole -n is (-1)^n/n!, here -1/6
    check("NResidue(Gamma(x), {x, -3}, WorkingPrecision -> 30)", //
        "-0.166666666666666666666666666666");
    // a working precision at or below machine precision computes in machine precision
    check("Chop(NResidue(1/x, {x, 0}, WorkingPrecision -> 5))", //
        "1.0");
  }

  @Test
  public void testNResidueOptions() {
    check("Chop(NResidue(1/x, {x, 0}, Method -> \"Trapezoidal\"))", //
        "1.0");
    check("Chop(NResidue(1/x, {x, 0}, Method -> Trapezoidal))", //
        "1.0");
    check("Chop(NResidue(1/x, {x, 0}, PrecisionGoal -> 4))", //
        "1.0");
    check("Chop(NResidue(1/x, {x, 0}, MaxRecursion -> 3))", //
        "1.0");
    // the trapezoidal rule is the only method on a circular contour, anything else is ignored
    check("Chop(NResidue(1/x, {x, 0}, Method -> Simpson))", //
        "1.0");
    // an unusable Radius falls back to the default 1/100
    check("Chop(NResidue(1/x, {x, 0}, Radius -> -1))", //
        "1.0");
    check("Radius /. Options(NResidue)", //
        "1/100");
    check("{MaxRecursion, WorkingPrecision, PrecisionGoal, AccuracyGoal, Method}"
        + " /. Options(NResidue)", //
        "{10,MachinePrecision,Automatic,MachinePrecision,Automatic}");
    check("Attributes(NResidue)", //
        "{HoldFirst,Protected}");
  }

  @Test
  public void testNResidueUnevaluated() {
    check("NResidue(1/x)", //
        "NResidue(1/x)");
    check("NResidue(1/x, x)", //
        "NResidue(1/x,x)");
    check("NResidue(1/x, {2, 0})", //
        "NResidue(1/x,{2,0})");
    // the integrand has no numerical value on the contour
    check("NResidue(f(z)/z^2, {z, 0})", //
        "NResidue(f(z)/z^2,{z,0})");
  }

  @Test
  public void testNResidueAgreesWithResidue() {
    // the rational cases the symbolic Residue() closes too
    check("Chop(NResidue(1/(z-2), {z, 2}) - Residue(1/(z-2), {z, 2}))", //
        "0");
    check("Chop(NResidue(1/(z^2-1), {z, 1}) - Residue(1/(z^2-1), {z, 1}))", //
        "0");
    check("Chop(NResidue(Exp(z)/z^3, {z, 0}) - Residue(Exp(z)/z^3, {z, 0}))", //
        "0");
    check("Chop(NResidue((z+1)/(z-1)^2, {z, 1}) - Residue((z+1)/(z-1)^2, {z, 1}))", //
        "0");
  }
}
