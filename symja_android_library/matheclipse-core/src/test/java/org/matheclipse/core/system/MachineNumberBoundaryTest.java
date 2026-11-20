package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Machine numbers at the edges of the double range: tiny or huge parts of a complex modulus, sums
 * and products of statistics and linear algebra which overflow a double, the precision of a power
 * with a machine operand, base notation with a fraction point, small arguments of Sinh and Tanh,
 * and exact powers too large to compute.
 */
public class MachineNumberBoundaryTest extends ExprEvaluatorTestCase {

  @Test
  public void testComplexModulusOfTinyAndHugeParts() {
    // a part which is small in absolute terms isn't zero
    // scaled for the output, which prints a tiny machine number as 0.0
    check("10^209*Abs(6.0*10^-210+I*8.0*10^-210)", //
        "1.0");
    check("Abs(5.0*10^250-I*1.2*10^251)", //
        "1.3*10^251");
    check("10^189*Abs(-7.0*10^-190*I+2.4*10^-189)", //
        "2.5");
  }

  @Test
  public void testReciprocalOfTinyAndHugeComplex() {
    // a tiny complex number isn't zero
    check("1/(2.0*10^-250+I*2.0*10^-250)", //
        "2.5*10^249+I*(-2.5*10^249)");
    check("1/(1/(4.0*10^290-I*3.0*10^290))", //
        "4.*10^290+I*(-3.*10^290)");
  }

  @Test
  public void testSinhAndTanhOfSmallArguments() {
    // exp(x)-exp(-x) cancels the leading digits; sinh(x)-x = x^3/6+x^5/120+...
    check("N(Sinh(3*10^-20)-3*10^-20,40)", //
        "4.500000000000000000000000000000000000000*10^-60");
    check("N(Tanh(2*10^-12)-2*10^-12,20)", //
        "-2.6666666666666666667*10^-36");
  }

  @Test
  public void testStatisticsBeyondTheDoubleRange() {
    check("Mean({1.5*10^308,1.7*10^308})", //
        "1.600000000000000*10^308");
    check("Variance({2.0*10^210,6.0*10^210})", //
        "7.999999999999998*10^420");
    // the deviations -3,0,3 and -1,-1,2 times 10^200: (3+0+6)*10^400/(3-1)
    check("Covariance({1.0*10^200,4.0*10^200,7.0*10^200},{2.0*10^200,2.0*10^200,5.0*10^200})", //
        "4.500000000000000*10^400");
    check("RootMeanSquare({3.0*10^250,4.0*10^250})", //
        "3.535533905932737*10^250");
    check("StandardDeviation({2.0*10^220,6.0*10^220})", //
        "2.828427124746189*10^220");
  }

  @Test
  public void testLinearAlgebraBeyondTheDoubleRange() {
    check("{2.0*10^180,3.0*10^180}.{2.0*10^180,1.0*10^180}", //
        "7.000000000000000*10^360");
    check("SparseArray({{1,1}->2.0*10^200,{2,2}->1.0}).{3.0*10^200,1.0}", //
        "{6.000000000000000*10^400,1.0}");
    check("10^229*Norm({6.0*10^-230,8.0*10^-230})", //
        "1.0");
    check("Norm({5.0*10^250,1.2*10^251})", //
        "1.3*10^251");
    check("10^200*Norm({3.0*10^-200*I,4.0*10^-200})", //
        "5.0");
  }

  @Test
  public void testPowerWithMachineOperand() {
    // a machine number in a power gives the machine precision, as in a product
    check("a=N(E,40);", //
        "");
    check("Precision(a^2.5)", //
        "16");
    check("Precision(2.5^a)", //
        "16");
    check("Precision(a^(5/2))", //
        "39.60206");
  }

  @Test
  public void testBaseNotationWithFraction() {
    check("{8^^7.4, 2^^101.01, -16^^a.c, 36^^z.i}", //
        "{7.5,5.25,-10.75,35.5}");
    // *^ is a decimal exponent of the base
    check("{16^^1.8*^2, 16^^ff*^2, 2^^1.1*^-3, 16^^.8}", //
        "{384.0,65280,0.1875,0.5}");
    // the precision of the integer of the digits: the leading 1 of 1.0101... counts as a 1
    check("Precision(2^^1.0101010101010101010101010101010101010101010101010101010101010101)", //
        "19.39086");
    // a point before a letter which is no digit of the base is the operator Dot
    check("Hold(16^^ff.x)", //
        "Hold(255.x)");
  }

  @Test
  public void testHugeExactPowerRefused() {
    // the result would have about 10^13 digits: refused at once, the power stays
    check("(1+10^-12)^(10^12)", //
        "(1+1/10^12)^10^12");
    check("7^(10^14)", //
        "7^10^14");
  }
}
