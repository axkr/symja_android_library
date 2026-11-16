package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The property argument of Fit and fits of no data. */
public class FitPropertiesTest extends ExprEvaluatorTestCase {

  private static final String DATA = "{{0, 2}, {1, 1}, {2, 3}, {3, 5}}";

  @Test
  public void testExactProperties() {
    check("Fit(" + DATA + ", {1, x}, x, \"BestFit\", WorkingPrecision -> Infinity)", //
        "11/10+11/10*x");
    check("Fit(" + DATA + ", {1, x}, x, \"BestFitParameters\", WorkingPrecision -> Infinity)", //
        "{11/10,11/10}");
    check("Fit(" + DATA + ", {1, x}, x, \"BasisFunctions\")", //
        "{1,x}");
    // the value of the fit minus the data value at each point
    check("Fit(" + DATA + ", {1, x}, x, \"FitResiduals\", WorkingPrecision -> Infinity)", //
        "{-9/10,6/5,3/10,-3/5}");
    check("Fit(" + DATA + ", {1, x}, x, \"Function\", WorkingPrecision -> Infinity)[2]", //
        "33/10");
    check("Fit(" + DATA + ", {1, x}, x, {\"BasisFunctions\", \"BestFitParameters\"}, "
        + "WorkingPrecision -> Infinity)", //
        "{{1,x},{11/10,11/10}}");
  }

  @Test
  public void testMachineProperties() {
    check("Round(Fit({5, 3, 4}, {1, x}, x, \"BestFitParameters\"), 1/100)", //
        "{5,-1/2}");
    check("Round(Fit({5, 3, 4}, {1, x}, x, \"FitResiduals\"), 1/100)", //
        "{-1/2,1,-1/2}");
    check("Head(Fit({5, 3, 4}, {1, x}, x, \"Function\"))", //
        "Function");
    // a basis function which is zero at every point keeps its coefficient in the parameters
    check("Fit({{0, 1}, {0, 3}}, {1, x}, x, \"BestFitParameters\")", //
        "{2.0,0.0}");
  }

  @Test
  public void testUnknownProperty() {
    check("Fit({5, 3, 4}, {1, x}, x, \"Slope\")", //
        "Missing(NotApplicable)");
    check("Check(Fit({5, 3, 4}, {1, x}, x, \"Slope\"), failed, Fit::noprop)", //
        "failed");
  }

  @Test
  public void testNoData() {
    check("Fit({}, {1, x, x^2}, x)", //
        "0");
    check("FindFit({}, a*x^2 + b, {a, b}, x)", //
        "{a->0,b->0}");
    check("FindFit({}, a*Exp(b*x), {{a, 2}, {b, 3}}, x)", //
        "{a->0,b->0}");
  }
}
