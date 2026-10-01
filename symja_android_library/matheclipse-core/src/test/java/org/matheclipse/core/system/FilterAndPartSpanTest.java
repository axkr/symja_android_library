package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The discrete Gaussian kernel, the filters of a matrix and a <code>Part</code> span which runs
 * past the end of an expression.
 */
public class FilterAndPartSpanTest extends ExprEvaluatorTestCase {

  @Test
  public void testGaussianMatrix() {
    // Default kernel Exp(-s^2)*BesselI(k, s^2) with s == r/2, scaled to the sum 1: the
    // centre of GaussianFilter(delta, 1) is 0.6419840455055237 (a Real32 sample), and not the
    // 0.619 of the sampled Gaussian
    check("Max(Abs(GaussianMatrix(1) - {{0.00987648032605648, 0.07962752133607864, "
        + "0.00987648032605648}, {0.07962752133607864, 0.6419840455055237, 0.07962752133607864}, "
        + "{0.00987648032605648, 0.07962752133607864, 0.00987648032605648}})) < 10^-7", //
        "True");
    check("{Dimensions(GaussianMatrix(2)), Abs(Total(GaussianMatrix(2), 2) - 1) < 10^-12}", //
        "{{5,5},True}");
    check("Abs(Total(GaussianMatrix({3, 1.5}), 2) - 1) < 10^-12", //
        "True");
  }

  @Test
  public void testGaussianFilterOfLists() {
    check("GaussianFilter({0., 0., 1., 0., 0.}, 1)", //
        "{0.0,0.0993805,0.801239,0.0993805,0.0}");
    check("GaussianFilter({{0., 0., 0.}, {0., 1., 0.}, {0., 0., 0.}}, 1) == GaussianMatrix(1)", //
        "True");
    // the values beyond the border are those of the border, so a constant stays constant
    check("Max(Abs(GaussianFilter({3., 3., 3., 3.}, 2) - 3.)) < 10^-12", //
        "True");
  }

  @Test
  public void testFiltersOfAMatrix() {
    check("MeanFilter({{1, 2}, {3, 4}}, 1)", //
        "{{5/2,5/2},{5/2,5/2}}");
    check("MaxFilter({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, 1)", //
        "{{5,6,6},{8,9,9},{8,9,9}}");
    check("MinFilter({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, 1)", //
        "{{1,1,2},{1,1,2},{4,4,5}}");
    check("MedianFilter({{1, 2, 9}, {3, 4, 5}, {6, 7, 8}}, 1)[[2, 2]]", //
        "5");
    // the list filters are unchanged
    check("MeanFilter({0., 0., 1., 0., 0.}, 1)", //
        "{0.0,0.333333,0.333333,0.333333,0.0}");
  }

  @Test
  public void testPartSpanPastTheEnd() {
    // Part::take: Cannot take positions 11 through 17 in f[a,b]. - an
    // IndexOutOfBoundsException was thrown here
    check("f(a, b)[[11 ;; 17, 2 ;; 3]]", //
        "f(a,b)[[11;;17,2;;3]]", //
        "Part: Cannot take positions 11 through 17 in f(a,b).");
    check("{{1,2,3},{4,5,6}}[[1 ;; 4, 2 ;; 3]]", //
        "{{1,2,3},{4,5,6}}[[1;;4,2;;3]]", //
        "Part: Cannot take positions 1 through 4 in {{1,2,3},{4,5,6}}.");
    check("{{1,2,3},{4,5,6}}[[1 ;; 2, 2 ;; 3]]", //
        "{{2,3},{5,6}}");
    // the same for an assignment to the parts
    check("data = f(a, b); data[[22 ;; 30, 1 ;; 9]] = 1.; data", //
        "f(a,b)", //
        "Part: Cannot take positions 22 through 30 in f(a,b).");
    check("m = {{1,2,3},{4,5,6}}; m[[1 ;; 2, 2 ;; 3]] = 0; m", //
        "{{1,0,0},{4,0,0}}");
  }
}
