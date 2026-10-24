package org.matheclipse.ortools.modelbuilder.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.matheclipse.ortools.linearsolver.samples.LinearSolverSamplesTest.checkAssignment;
import static org.matheclipse.ortools.linearsolver.samples.LinearSolverSamplesTest.checkBinPacking;
import static org.matheclipse.ortools.linearsolver.samples.LinearSolverSamplesTest.checkSimpleLp;
import static org.matheclipse.ortools.linearsolver.samples.LinearSolverSamplesTest.checkSimpleMip;
import static org.matheclipse.ortools.linearsolver.samples.LinearSolverSamplesTest.maximizeInteger;

import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.testutil.OutputCapture;

/**
 * Runs the ported model builder samples and checks the printed optimal values with the
 * independent reference computations of the linear solver samples test (the model builder samples
 * solve the same models as the corresponding MPSolver samples).
 */
public class ModelBuilderSamplesTest {

  private static final double TOLERANCE = 1e-6;

  private static void assertContains(String out, String expected) {
    OutputCapture.assertContains(out, expected);
  }

  @Test
  public void simpleLpProgramMb() {
    checkSimpleLp(OutputCapture.run(() -> SimpleLpProgramMb.main(new String[0])));
  }

  @Test
  public void simpleMipProgramMb() {
    checkSimpleMip(OutputCapture.run(() -> SimpleMipProgramMb.main(new String[0])));
  }

  @Test
  public void assignmentMb() {
    checkAssignment(OutputCapture.run(() -> AssignmentMb.main(new String[0])));
  }

  @Test
  public void binPackingMb() {
    checkBinPacking(OutputCapture.run(() -> BinPackingMb.main(new String[0])));
  }

  @Test
  public void cloneModelMb() {
    final String out = OutputCapture.run(() -> CloneModelMb.main(new String[0]));
    assertContains(out, "Number of constraints in the original model = 2");
    assertContains(out, "Number of constraints in the cloned model = 3");
    // the clone: max x + 10y, x + 7y <= 17.5, x + 2z <= 3.5, x >= 1, x, y integer, z boolean
    final double[] expected = maximizeInteger(new double[][] {{1, 7, 0}, {1, 0, 2}, {1, 0, 0}},
        new double[] {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1},
        new double[] {17.5, 3.5, Double.POSITIVE_INFINITY}, new double[] {1, 10, 0},
        new int[] {17, 3, 1});
    assertEquals(23.0, expected[0], TOLERANCE);
    assertEquals(expected[0], OutputCapture.numberAfter(out, "Objective value = "), TOLERANCE);
    assertEquals(expected[1], OutputCapture.numberAfter(out, "\nx = "), TOLERANCE);
    assertEquals(expected[2], OutputCapture.numberAfter(out, "\ny = "), TOLERANCE);
    assertEquals(expected[3], OutputCapture.numberAfter(out, "\nz = "), TOLERANCE);
  }
}
