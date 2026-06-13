package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class ShiftRegisterSequenceTest extends ExprEvaluatorTestCase {

  @Test
  public void testShiftRegisterSequence001() {
    // Integer spec uses all-ones default initial state and the primitive polynomial
    // x^3 + x + 1 -> internal taps {1, 2}.
    check("ShiftRegisterSequence(3)", //
        "{0,0,1,0,1,1,1}");

    // {n, T} spec converts WMA-style taps T_W = {1, 3} (polynomial 1 + x + x^3) to internal
    // T_my = {1, 2}, with default initial state {0, 0, 1} (single 1 at the last position).
    check("ShiftRegisterSequence({3, {1, 3}}, 5)", //
        "{0,1,1,1,0}");

    check("ShiftRegisterSequence({3, {1, 3}}, All)", //
        "{0,1,1,1,0,0,1}");
  }

  @Test
  public void testShiftRegisterSequence002() {
    // Explicit initial state with {n, T} spec dispatches to a Galois LFSR that matches WMA:
    // mask positions = T_W, shift right, XOR mask after non-zero output.
    check("ShiftRegisterSequence({3, {1, 3}}, {1, 1, 1}, 5)", //
        "{1,0,1,0,0}");

    check("ShiftRegisterSequence({4, {1, 4}}, {1, 0, 1, 0}, 6)", //
        "{0,1,1,0,0,1}");
  }

  @Test
  public void testShiftRegisterSequencePolynomial() {
    // Polynomial spec runs a Galois LFSR (shift-left, subtract output*mask) with
    // mask = polynomial coefficients (c_0, ..., c_{n-1}) and default init (1, 0, ..., 0).
    check("ShiftRegisterSequence(x^3 + x + 1, 5)", //
        "{0,0,1,0,1}");

    // Explicit initial-state polynomial x^2 + x + 1 -> state {1, 1, 1}.
    check("ShiftRegisterSequence(x^3 + x + 1, x^2 + x + 1, 5)", //
        "{1,1,0,0,1}");
  }

  @Test
  public void testShiftRegisterSequenceWithFunction() {
    // Pure feedback function equivalent to default Mod(Total(#), 2)&; matches {n,T} default output.
    check("ShiftRegisterSequence({3, {1, 3}, Mod(Total(#), 2) &}, 5)", //
        "{0,1,1,1,0}");
  }

  @Test
  public void testShiftRegisterSequenceOptions() {
    // Modulus -> 3 is read from options [1]; the polynomial Galois LFSR over GF(3)
    // reproduces full m-sequence (first 8 elements requested here).
    check("ShiftRegisterSequence(x^6 + x + 1, 8, Modulus -> 3)", //
        "{0,0,0,0,0,1,0,0}");
    check("ShiftRegisterSequence(x^6 + x + 1, Modulus -> 3)", //
        "{0,0,0,0,0,1,0,0,0,0,2,2,0,0,0,1,2,1,0,0,2,0,0,2,0,1,1,0,1,1,2,1,2,2,1,0,0,0,2,0,\n" //
            + "2,0,0,1,1,1,1,0,2,1,1,1,2,1,0,1,1,0,0,2,2,1,2,0,1,2,0,0,1,2,0,1,0,2,0,1,2,2,1,1,\n" //
            + "2,0,2,0,1,0,1,1,1,2,2,2,1,1,0,2,2,0,1,2,1,2,1,2,0,0,0,0,0,1,0,0,0,0,2,2,0,0,0,1,\n" //
            + "2,1,0,0,2,0,0,2,0,1,1,0,1,1,2,1,2,2,1,0,0,0,2,0,2,0,0,1,1,1,1,0,2,1,1,1,2,1,0,1,\n" //
            + "1,0,0,2,2,1,2,0,1,2,0,0,1,2,0,1,0,2,0,1,2,2,1,1,2,0,2,0,1,0,1,1,1,2,2,2,1,1,0,2,\n" //
            + "2,0,1,2,1,2,1,2,0,0,0,0,0,1,0,0,0,0,2,2,0,0,0,1,2,1,0,0,2,0,0,2,0,1,1,0,1,1,2,1,\n" //
            + "2,2,1,0,0,0,2,0,2,0,0,1,1,1,1,0,2,1,1,1,2,1,0,1,1,0,0,2,2,1,2,0,1,2,0,0,1,2,0,1,\n" //
            + "0,2,0,1,2,2,1,1,2,0,2,0,1,0,1,1,1,2,2,2,1,1,0,2,2,0,1,2,1,2,1,2,0,0,0,0,0,1,0,0,\n" //
            + "0,0,2,2,0,0,0,1,2,1,0,0,2,0,0,2,0,1,1,0,1,1,2,1,2,2,1,0,0,0,2,0,2,0,0,1,1,1,1,0,\n" //
            + "2,1,1,1,2,1,0,1,1,0,0,2,2,1,2,0,1,2,0,0,1,2,0,1,0,2,0,1,2,2,1,1,2,0,2,0,1,0,1,1,\n" //
            + "1,2,2,2,1,1,0,2,2,0,1,2,1,2,1,2,0,0,0,0,0,1,0,0,0,0,2,2,0,0,0,1,2,1,0,0,2,0,0,2,\n" //
            + "0,1,1,0,1,1,2,1,2,2,1,0,0,0,2,0,2,0,0,1,1,1,1,0,2,1,1,1,2,1,0,1,1,0,0,2,2,1,2,0,\n" //
            + "1,2,0,0,1,2,0,1,0,2,0,1,2,2,1,1,2,0,2,0,1,0,1,1,1,2,2,2,1,1,0,2,2,0,1,2,1,2,1,2,\n" //
            + "0,0,0,0,0,1,0,0,0,0,2,2,0,0,0,1,2,1,0,0,2,0,0,2,0,1,1,0,1,1,2,1,2,2,1,0,0,0,2,0,\n" //
            + "2,0,0,1,1,1,1,0,2,1,1,1,2,1,0,1,1,0,0,2,2,1,2,0,1,2,0,0,1,2,0,1,0,2,0,1,2,2,1,1,\n" //
            + "2,0,2,0,1,0,1,1,1,2,2,2,1,1,0,2,2,0,1,2,1,2,1,2,0,0,0,0,0,1,0,0,0,0,2,2,0,0,0,1,\n" //
            + "2,1,0,0,2,0,0,2,0,1,1,0,1,1,2,1,2,2,1,0,0,0,2,0,2,0,0,1,1,1,1,0,2,1,1,1,2,1,0,1,\n" //
            + "1,0,0,2,2,1,2,0,1,2,0,0,1,2,0,1,0,2,0,1,2,2,1,1,2,0,2,0,1,0,1,1,1,2,2,2,1,1,0,2,\n" //
            + "2,0,1,2,1,2,1,2}");
  }
}