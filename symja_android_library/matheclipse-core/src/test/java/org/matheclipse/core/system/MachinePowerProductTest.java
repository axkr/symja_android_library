package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Products of powers with machine number bases, and products of machine numbers with arbitrary
 * precision numbers of machine precision.
 */
public class MachinePowerProductTest extends ExprEvaluatorTestCase {

  @Test
  public void testInexactOneToASymbolicPower() {
    // an inexact one stays inexact
    check("{1.0^z, 1.0^Sqrt(w), 1.0^(2*z), 1^z}", //
        "{1.0,1.0,1.0,1}");
    // a sum in the exponent stays
    check("1.0^(z + w)", //
        "1.0^(w+z)");
    // the bases multiply to an inexact one
    check("{2.5^z*2.5^(-z), 0.25^z*4.0^z}", //
        "{1.0,1.0}");
    check("InputForm({2.5^z*2.5^(-z), 0.25^z*4.0^z})", //
        "{1.0`,1.0`}");
  }

  @Test
  public void testIntegerPowerBeyondTheMachineRange() {
    // binary powering: 52 bits after one squaring beyond the double range, 50 after three
    // (3.^5000 is the square of the square of the square of 3.^625)
    check("{Precision(3.^700), Precision(0.25^(-600)), Precision(3.^5000)}", //
        "{15.65356,15.65356,15.0515}");
    check("InputForm(3.^700)", //
        "9.657802140591758`15.6536*^333");
    check("3.^700 == 3^700", //
        "True");
  }

  @Test
  public void testInexactArgumentBeyondTheMachineRange() {
    // the 53 bits of the inexact exponent less Log10 of the condition number
    // Abs(exponent*Log(base)) = 769.6, and Log10(800) for Exp
    check("{Precision(3.^700.5), Precision(Exp(800.))}", //
        "{13.06834,13.0515}");
    // a machine factor makes a product beyond the double range one of the 53 bits of a machine
    // number
    check("{Precision(5.0*3.^700), Precision(3.^701)}", //
        "{15.95459,15.95459}");
    // the relative errors of two values of fewer bits add: 52 and 52 bits give 51
    check("Precision(3.^700*3.^800)", //
        "15.35253");
    // the exact zero times an arbitrary precision number is the exact zero, the machine zero
    // times it the machine zero
    check("InputForm({0*3.^700, 0*Exp(800.), 0.0*3.^700})", //
        "{0,0,0.0`}");
    // the difference of two equal numbers keeps an accuracy
    check("InputForm(3.^700 - 3.^700)", //
        "0``-318.632");
  }

  @Test
  public void testMachineTimesArbitraryPrecisionIsCommutative() {
    // 3.7*^-315 is the subnormal machine number 3.6999999988288...*^-315, 10.^305 is beyond the
    // machine range; the product is the machine number of the binary value in both orders
    check("a = 3.7*^-315; b = 10.^305; InputForm({a*b, b*a})", //
        "{3.6999999988288205`*^-10,3.6999999988288205`*^-10}");
    check("a = 3.7*^-315; b = 10.^305; {MachineNumberQ(a*b), MachineNumberQ(b*a)}", //
        "{True,True}");
  }
}
