package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The raster values and the explicit frame ticks of MatrixPlot and ArrayPlot. */
public class MatrixPlotRasterTest extends ExprEvaluatorTestCase {

  @Test
  public void testMatrixPlotColorFunctionScale() {
    // half by rank and half by size, 0 at 1/2; the raster rows are bottom up
    check("Round(Cases(MatrixPlot({{2, -4}, {0, 8}}, ColorFunction -> (GrayLevel(#)&)), "
        + "Raster(r_, ___) :> r[[All, All, 1]], Infinity), 0.01)", //
        "{{{0.5,1.0},{0.69,0.25}}}");
    // equal values share their rank
    check("Round(Cases(MatrixPlot({{3, 3, -1, 6}}, ColorFunction -> (GrayLevel(#)&)), "
        + "Raster(r_, ___) :> r[[All, All, 1]], Infinity), 0.001)", //
        "{{{0.75,0.75,0.344,1.0}}}");
    // nothing above 0: the scale ends at 1/2
    check("Round(Cases(MatrixPlot({{-3, -1, 0}}, ColorFunction -> (GrayLevel(#)&)), "
        + "Raster(r_, ___) :> r[[All, All, 1]], Infinity), 0.001)", //
        "{{{0.0,0.292,0.5}}}");
    // nothing but zeros: the middle of the scale
    check("Cases(MatrixPlot({{0, 0, 0}}, ColorFunction -> (GrayLevel(#)&)), "
        + "Raster(r_, ___) :> r[[All, All, 1]], Infinity)", //
        "{{{0.5,0.5,0.5}}}");
  }

  @Test
  public void testMatrixPlotDefaultColours() {
    // the same scale: blue at 1/4, white for 0, light orange at 0.6875 and {1, 0.42, 0} at the top
    check("Round(Cases(MatrixPlot({{2, -4}, {0, 8}}), Raster(r_, ___) :> r, Infinity), 0.001)", //
        "{{{{1.0,1.0,1.0},{1.0,0.42,0.0}},{{0.907,0.762,0.406},{0.311,0.579,0.823}}}}");
    // the blue half: -3 at 0, -1 at 0.2917, 0 at 1/2
    check("Round(Cases(MatrixPlot({{-3, -1, 0}}), Raster(r_, ___) :> r, Infinity), 0.001)", //
        "{{{{0.26,0.356,0.892},{0.352,0.619,0.81},{1.0,1.0,1.0}}}}");
  }

  @Test
  public void testArrayPlotGreyLevels() {
    // the largest magnitude less the magnitude of each cell, over the range {0, largest}
    check("Cases(ArrayPlot({{3, -6}, {1/2, 0}}), _Raster, Infinity)", //
        "{Raster({{11/2,6},{3,0}},{{0.0,0.0},{2.0,2.0}},{0,6})}");
    check("Cases(ArrayPlot({{1.5, -0.5}}), _Raster, Infinity)", //
        "{Raster({{0.0,1.0}},{{0.0,0.0},{2.0,1.0}},{0,1.5})}");
    check("Cases(ArrayPlot({{0, 0}}), _Raster, Infinity)", //
        "{Raster({{1,1}},{{0.0,0.0},{2.0,1.0}},{0,1})}");
    // a colour among the cells keeps the colours
    check("Cases(ArrayPlot({{1, Red}}), Raster(r_, ___) :> r, Infinity)", //
        "{{{{0.0,0.0,0.0},{1.0,0.0,0.0}}}}");
  }

  @Test
  public void testExplicitFrameTicksAtCellCentres() {
    // row and column indices move to the centres of their cells, the labels are kept
    check("Options(ArrayPlot({{1, 2}, {3, 4}, {5, 6}}, "
        + "FrameTicks -> {{{1, {3, \"c\"}}, None}, {{{2, xx}}, None}}), FrameTicks)", //
        "{FrameTicks->{{{{2.5,1},{0.5,c}},None},{{{1.5,xx}},None}}}");
    check("Options(ArrayPlot({{1, 2}, {3, 4}, {5, 6}}, DataReversed -> True, "
        + "FrameTicks -> {{{1, {3, \"c\"}}, None}, {None, None}}), FrameTicks)", //
        "{FrameTicks->{{{{0.5,1},{2.5,c}},None},{None,None}}}");
    check("Union(Cases(FrameTicks /. Options(MatrixPlot({{1, 2}, {3, 4}, {5, 6}}, "
        + "FrameTicks -> {{{{2, \"b\"}, {3, 7/2}}, None}, {None, None}})), "
        + "{_Real, lbl_} :> Head(lbl), Infinity))", //
        "{Rational,String}");
  }
}
