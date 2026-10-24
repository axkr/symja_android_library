package org.matheclipse.ortools.constraintsolver.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.matheclipse.ortools.testutil.OutputCapture.assertContains;
import static org.matheclipse.ortools.testutil.OutputCapture.count;
import static org.matheclipse.ortools.testutil.OutputCapture.numberAfter;

import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.testutil.OutputCapture;

/**
 * Runs the samples of the original OR-Tools constraint solver (ported from
 * <code>ortools/constraint_solver/samples/java</code> of OR-Tools 10.0) and checks their output.
 */
public class CpSamplesTest {

  /** The number of solutions of CP + IS + FUN = TRUE in base 10, counted by brute force. */
  private static int bruteForceCpIsFun() {
    int[] digit = new int[10];
    boolean[] used = new boolean[10];
    return assign(0, digit, used);
  }

  // letters: c p i s f u n t r e
  private static int assign(int letter, int[] d, boolean[] used) {
    if (letter == 10) {
      int lhs = (10 * d[0] + d[1]) + (10 * d[2] + d[3]) + (100 * d[4] + 10 * d[5] + d[6]);
      int rhs = 1000 * d[7] + 100 * d[8] + 10 * d[5] + d[9];
      return lhs == rhs ? 1 : 0;
    }
    boolean nonZero = letter == 0 || letter == 2 || letter == 4 || letter == 7;
    int total = 0;
    for (int v = nonZero ? 1 : 0; v < 10; v++) {
      if (!used[v]) {
        used[v] = true;
        d[letter] = v;
        total += assign(letter + 1, d, used);
        used[v] = false;
      }
    }
    return total;
  }

  @Test
  public void testCpIsFunCp() {
    String out = OutputCapture.run(() -> CpIsFunCp.main(new String[0]));
    int expected = bruteForceCpIsFun();
    assertEquals(72, expected);
    assertEquals(expected, (int) numberAfter(out, "Number of solutions found:"));
    assertEquals(expected, count(out, "^C="));
  }

  @Test
  public void testNQueensCp() {
    String out = OutputCapture.run(() -> NQueensCp.main(new String[0]));
    assertEquals(92, (int) numberAfter(out, "Solutions found:"));
    assertEquals(92, count(out, "^Solution \\d+"));
    // every printed board has exactly one queen per row
    assertEquals(92 * 8, count(out, "^[_ ]*Q[_ ]*$"));
  }

  @Test
  public void testSimpleCpProgram() {
    String out = OutputCapture.run(() -> SimpleCpProgram.main(new String[0]));
    assertEquals(1, (int) numberAfter(out, "Number of constraints:"));
    // x != y over {0, 1, 2} with z free: 3 * 2 * 3 solutions
    assertEquals(18, (int) numberAfter(out, "Number of solutions found:"));
    assertEquals(18, count(out, "^Solution: \\d+"));
    assertContains(out, "x=0 y=1 z=0");
    assertContains(out, "Problem solved in");
  }

  @Test
  public void testRabbitsPheasants() {
    String out = OutputCapture.run(() -> RabbitsPheasants.main(new String[0]));
    assertContains(out, "rabbits(8)");
    assertContains(out, "pheasants(12)");
  }
}
