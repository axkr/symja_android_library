package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 * Edge cases of arithmetic and number theory functions: indeterminate and infinite arguments,
 * complex quotients, machine zeros, negative moduli, huge indices and machine iterations.
 */
public class NumberEdgeCasesTest extends ExprEvaluatorTestCase {

  /** Evaluate <code>input</code> and return what was printed as messages. */
  private String messagesOf(String input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream old = evaluator.getEvalEngine().getErrorPrintStream();
    evaluator.getEvalEngine().setErrorPrintStream(new PrintStream(buffer, true));
    try {
      evaluator.eval(input);
    } finally {
      evaluator.getEvalEngine().setErrorPrintStream(old);
    }
    return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
  }

  @Test
  public void testInfiniteAndIndeterminateArguments() {
    check("RealSign(Indeterminate)", //
        "Indeterminate");
    // the parts of an infinity follow the parts of its direction
    check("{Re((3-4*I)*Infinity), Im((3-4*I)*Infinity), Re((-1+2*I)*Infinity), Im((-1-2*I)*Infinity)}", //
        "{Infinity,-Infinity,-Infinity,-Infinity}");
    check("Im(DirectedInfinity(w))", //
        "Im(DirectedInfinity(w))");
    // a rational power of an infinity points in the direction of the power of its direction
    check("{Sqrt(I*Infinity), (-I*Infinity)^(1/3)}", //
        "{DirectedInfinity((-1)^(1/4)),DirectedInfinity(-(-1)^(5/6))}");
    check("{(I*Infinity)^3, ((1-I)*Infinity)^2, (I*Infinity)^(-1/2)}", //
        "{-I*Infinity,-I*Infinity,0}");
  }

  @Test
  public void testMachineZeroTimesSymbol() {
    check("{0.0*w, 0.0*w + 3, 0.0*w + v} // InputForm", //
        "{0.0`,3.0`,v}");
  }

  @Test
  public void testComplexQuotientByNonNumber() {
    // an explicit number rounds the parts of the quotient, a value like Sqrt(3) floors them
    check("{Quotient(2 + 3*I, Sqrt(3)), Quotient(5 + 7*I, 3), Quotient(5 + 7*I, 3.), Quotient(7*I, Pi)}", //
        "{1+I,2+I*2,2+I*2,I*2}");
    check("QuotientRemainder(2 + 3*I, Sqrt(3))", //
        "{1+I,2+I*3+(-1-I)*Sqrt(3)}");
  }

  @Test
  public void testQuotientRemainderByZero() {
    check("{QuotientRemainder(7, 0.), QuotientRemainder(w, 0)}", //
        "{QuotientRemainder(7,0.0),QuotientRemainder(w,0)}");
    assertEquals(
        "QuotientRemainder: The argument 0.0 in QuotientRemainder(7,0.0) should be nonzero.\n",
        messagesOf("QuotientRemainder(7, 0.)"));
    assertEquals("", messagesOf("QuotientRemainder(w, 0)"));
  }

  @Test
  public void testPowerModNegativeModulus() {
    // only the empty product has a residue for a negative modulus
    check("{PowerMod(5, 0, -9), PowerMod(5, 0, -1), PowerMod(5, 3, -9), PowerMod(5, -1, -9)}", //
        "{-8,0,PowerMod(5,3,-9),PowerMod(5,-1,-9)}");
    check("PowerMod(3, 1/2, -11)", //
        "PowerMod(3,1/2,-11)");
    assertEquals("PowerMod: The equation x^2==Mod(3,-11) has no integer solutions.\n",
        messagesOf("PowerMod(3, 1/2, -11)"));
    // the message names the residue of the base
    assertEquals("PowerMod: 7 is not invertible modulo 14.\n", messagesOf("PowerMod(21, -1, 14)"));
    assertEquals("PowerMod: 3 is not invertible modulo 12.\n", messagesOf("PowerMod(-9, -1, 12)"));
  }

  @Test
  public void testBinomialZeroAndNegativeIndex() {
    // a non-real n and a negative integer k give the exact zero, a symbolic n stays
    check("{Binomial(2 - 3*I, -1), Binomial(1.5 - I, -3), Binomial(2*I, -2.), Binomial(w, -1), Binomial(w, -3), Binomial(2 - 3*I, 2)}", //
        "{0,0,0,Binomial(w,-1),Binomial(w,-3),-7/2-I*9/2}");
    // k = 0 gives 1, inexact if an argument is
    check("{Binomial(-1.5, 0), Binomial(1.5 - 2*I, 0), Binomial(w, 0), Binomial(3/4, 0), Binomial(6, 0.), Binomial(w, 0.)}", //
        "{1.0,1.0,1,1,1.0,1.0}");
  }

  @Test
  public void testIntegerLengthOfLargeNumbers() {
    check("{IntegerLength(7^4000), IntegerLength(10^75), IntegerLength(10^75 - 1), IntegerLength(-10^75), "
        + "IntegerLength(2^90, 2), IntegerLength(2^90 - 1, 2), IntegerLength(3^100, 9), IntegerLength(3^100 - 1, 9), IntegerLength(5^60, 7)}", //
        "{3381,76,75,76,91,90,51,50,50}");
  }

  @Test
  public void testFibonacciIndexOverflow() {
    check("{Fibonacci(10^15), LucasL(-10^15 - 1)}", //
        "{Overflow(),Overflow()}");
    check("Fibonacci(10^15, w)", //
        "Fibonacci(1000000000000000,w)");
  }

  @Test
  public void testMachineArithmeticGeometricMean() {
    // the double iteration; a product beyond the range is scaled, one which underflows gives 0
    check("{ArithmeticGeometricMean(2.2, 1.6), ArithmeticGeometricMean(5.0, 9.0), ArithmeticGeometricMean(7, 2.3), "
        + "ArithmeticGeometricMean(-1.5, -6.25)} // InputForm", //
        "{1.8880643481007156`,6.853325451283464`,4.325365488884322`,-3.4564650545081372`}");
    check("{ArithmeticGeometricMean(3.*^-190, 4.*^-190), ArithmeticGeometricMean(2.*^180, 5.*^180), "
        + "ArithmeticGeometricMean(3.*^-90, 7.*^-90)} // InputForm", //
        "{0.0`,3.328997143273107`*^180,4.789013583140953`*^-90}");
  }

  @Test
  public void testBinomialOfNegativeIntegerOrder() {
    // an exact negative integer k and an n which is no integer give the exact zero
    check("{Binomial(3.75, -2), Binomial(E, -3), Binomial(2/7, -1), Binomial(-4.0, -6)}", //
        "{0,0,0,10.0}");
  }

  @Test
  public void testMaxMinNeutralElementBesideNonReal() {
    // -Infinity (Infinity for Min) is a value to compare the non-real argument with
    check("Max(-Infinity, 5*I)", //
        "Max(I*5,-Infinity)");
    check("Min(Infinity, 2*I)", //
        "Min(I*2,Infinity)");
    check("Min(4*I, 4*I)", //
        "I*4");
  }

  @Test
  public void testBinomialNearIntegerExpression() {
    // an exact integer whose machine value carries a rounding error is no reason for the zero
    check("Binomial(-Log(8)/Log(2), -5) === 0", //
        "False");
  }
}
