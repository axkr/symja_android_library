package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for the forms of {@code TrigExpand}, {@code TrigReduce} and {@code TrigToExp}. */
public class TrigTransformationTest extends ExprEvaluatorTestCase {

  @Test
  public void testTrigExpandReducesPowersFirst() {
    // a power of a sum or multiple angle is reduced to multiple angles first
    check("TrigExpand(Sin(u+v)^2)", //
        "1/2-1/2*Cos(u)^2*Cos(v)^2+1/2*Cos(v)^2*Sin(u)^2+2*Cos(u)*Cos(v)*Sin(u)*Sin(v)+1/\n" //
            + "2*Cos(u)^2*Sin(v)^2-1/2*Sin(u)^2*Sin(v)^2");
    check("TrigExpand(Cosh(2*u)^2)", //
        "1/2+Cosh(u)^4/2+3*Cosh(u)^2*Sinh(u)^2+Sinh(u)^4/2");
    // several trigonometric functions
    check("TrigExpand(Sin(u)*Sin(2*u))", //
        "Cos(u)/2-Cos(u)^3/2+3/2*Cos(u)*Sin(u)^2");
    // the angle variable also occurs outside of the trigonometric functions
    check("TrigExpand(v+Cos(v)^3)", //
        "v+3/4*Cos(v)+Cos(v)^3/4-3/4*Cos(v)*Sin(v)^2");
    // a single power of a simple angle is kept
    check("TrigExpand(w+Cos(v)^3)", //
        "w+Cos(v)^3");
    check("TrigExpand(Cos(u*v)^2)", //
        "Cos(u*v)^2");
  }

  @Test
  public void testTrigExpandReducedDenominators() {
    check("TrigExpand(1/(1-Cos(2*v)))", //
        "Csc(v)^2/2");
    check("TrigExpand(3/(1+Cosh(2*v)))", //
        "3/2*Sech(v)^2");
    check("TrigExpand(Cot(3*v)^2)", //
        "1/(2*(3*Cos(v)^2*Sin(v)-Sin(v)^3)^2)+Cos(v)^6/(2*(3*Cos(v)^2*Sin(v)-Sin(v)^3)^2)-\n" //
            + "15/2*(Cos(v)^4*Sin(v)^2)/(3*Cos(v)^2*Sin(v)-Sin(v)^3)^2+15/2*(Cos(v)^2*Sin(v)^4)/(\n" //
            + "3*Cos(v)^2*Sin(v)-Sin(v)^3)^2-Sin(v)^6/(2*(3*Cos(v)^2*Sin(v)-Sin(v)^3)^2)");
    // the power of an expanded reciprocal function is expanded
    check("TrigExpand(Sec(3*v)^3)", //
        "1/(Cos(v)^9-9*Cos(v)^7*Sin(v)^2+27*Cos(v)^5*Sin(v)^4-27*Cos(v)^3*Sin(v)^6)");
    // no reduced power
    check("TrigExpand(1/(2+Cos(2*v)))", //
        "1/(2+Cos(v)^2-Sin(v)^2)");
  }

  @Test
  public void testTrigReduceQuotientProducts() {
    check("TrigReduce(Cot(u)*Tan(v))", //
        "(-Sin(u-v)+Sin(u+v))/(Sin(u-v)+Sin(u+v))");
    check("TrigReduce(Coth(u)*Coth(v))", //
        "(Cosh(u-v)+Cosh(u+v))/(-Cosh(u-v)+Cosh(u+v))");
    check("TrigReduce(Sin(u)^3*Cot(v))", //
        "1/8*Csc(v)*(3*Sin(u-v)-Sin(3*u-v)+3*Sin(u+v)-Sin(3*u+v))");
    check("TrigReduce(Cos(u*v)*Cot(u)^2)", //
        "(2*Cos(u*v)+Cos(2*u+u*v)+Cos(2*u-u*v))/(2*(1-Cos(2*u)))");
  }

  @Test
  public void testTrigReduceDistributesArguments() {
    check("TrigReduce(Cos(3*(u-v))^2)", //
        "1/2*(1+Cos(6*u-6*v))");
    check("TrigReduce(Sinh((u+v)/2))", //
        "Sinh(u/2+v/2)");
    // symbolic multiples of sums are kept
    check("TrigReduce(Cos(w*(u+v)))", //
        "Cos((u+v)*w)");
  }

  @Test
  public void testTrigToExpGroupedPowers() {
    check("TrigToExp(Cosh(u)^5)", //
        "(E^(-u)+E^u)^5/32");
    check("TrigToExp(Sin(2*u)^3)", //
        "-I*1/8*(E^(-I*2*u)-E^(I*2*u))^3");
    check("TrigToExp(Sinh(u)*Cos(v))", //
        "1/4*(-1/E^u+E^u)*(E^(-I*v)+E^(I*v))");
    check("TrigToExp(Cos(u)^2+Sin(v)^3)", //
        "(E^(-I*u)+E^(I*u))^2/4-I*1/8*(E^(-I*v)-E^(I*v))^3");
    // the harmonics of the cube combine with the other summand
    check("TrigToExp(Sin(u)+Sin(u)^3)", //
        "(I*7/8)/E^(I*u)-I*7/8*E^(I*u)+(-I*1/8)/E^(I*3*u)+I*1/8*E^(I*3*u)");
  }

  @Test
  public void testTrigToExpExpanded() {
    check("TrigToExp(v*Cos(u)^2)", //
        "v/2+v/(4*E^(I*2*u))+1/4*E^(I*2*u)*v");
    check("TrigToExp(Cos(u-v))", //
        "E^(I*u-I*v)/2+1/(2*E^(I*u-I*v))");
    check("TrigToExp(Sinh(u-v)^2)", //
        "-1/2+E^(2*u-2*v)/4+1/(4*E^(2*u-2*v))");
    check("TrigToExp(v*(w+Cosh(u)))", //
        "v/(2*E^u)+1/2*E^u*v+v*w");
    // arguments of undefined functions are kept
    check("TrigToExp(f(Cos(u))+Sinh(v))", //
        "-1/(2*E^v)+E^v/2+f(Cos(u))");
  }

  @Test
  public void testTrigToExpProductOfOneAngle() {
    // a product of degree 2 with one angle is expanded like a square
    check("TrigToExp(Sin(y)*Cos(y))", //
        "(I*1/4)/E^(I*2*y)-I*1/4*E^(I*2*y)");
    check("TrigToExp(Cosh(u)*Sinh(u))", //
        "-1/(4*E^(2*u))+E^(2*u)/4");
    // different angles stay grouped
    check("TrigToExp(Sin(x)*Cos(z))", //
        "I*1/4*(E^(-I*x)-E^(I*x))*(E^(-I*z)+E^(I*z))");
  }

  @Test
  public void testTrigExpandSimpleAngles() {
    // no angle to expand: a product with a quotient function is kept
    check("TrigExpand(Cos(y)*Cot(y))", //
        "Cos(y)*Cot(y)");
    // the power of an expanded reciprocal function is expanded
    check("TrigExpand(Sec(2*y)^2)", //
        "1/(Cos(y)^4-2*Cos(y)^2*Sin(y)^2+Sin(y)^4)");
  }

  @Test
  public void testTrigExpandKeepsOnlyQuotientSummands() {
    // the summand with a quotient function is kept, the others are reduced together
    check("TrigExpand(Sin(y)^2+Cos(y)^2+Tan(y))", //
        "1+Tan(y)");
  }
}
