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
  public void testMatrixPlotContentIsTheRaster() {
    // without a mesh the raster is the content itself, not a list around it
    check("Head(First(MatrixPlot({{7, -2}, {3, 3}, {0, 5}})))", //
        "Raster");
    check("Dimensions(MatrixPlot({{7, -2, 1}, {3, 3, 3}})[[1, 1]])", //
        "{2,3,3}");
    check("MatrixPlot({{0, 0, 0}})[[1, 1]]", //
        "{{{1.0,1.0,1.0},{1.0,1.0,1.0},{1.0,1.0,1.0}}}");
    // a mesh adds its lines next to the raster
    check("Map(Head, First(MatrixPlot({{7, -2}, {3, 3}}, Mesh -> True)))", //
        "{Raster,List}");
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
        "{Raster({{11/2,6},{3,0}},{{0,0},{2,2}},{0,6})}");
    check("Cases(ArrayPlot({{1.5, -0.5}}), _Raster, Infinity)", //
        "{Raster({{0.0,1.0}},{{0,0},{2,1}},{0,1.5})}");
    check("Cases(ArrayPlot({{0, 0}}), _Raster, Infinity)", //
        "{Raster({{1,1}},{{0,0},{2,1}},{0,1})}");
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

  @Test
  public void testMeshPerDirection() {
    // {rows, columns}: the boundary below the second of three rows only
    check("Cases(ArrayPlot({{1, 0, 1, 0}, {0, 1, 0, 1}, {1, 1, 0, 0}}, Mesh -> {{2}, None}), _Line, Infinity)", //
        "{Line({{{0,1},{4,1}}})}");
    // the boundaries right of the first and the fourth of four columns
    check("Cases(ArrayPlot({{1, 0, 1, 0}, {0, 1, 1, 0}}, Mesh -> {False, {1, 4}}), _Line, Infinity)", //
        "{Line({{{1,0},{1,2}},{{4,0},{4,2}}})}");
    // a boundary outside of the array is skipped
    check("Cases(MatrixPlot({{1, 2, 3}}, Mesh -> {{1, 7}, None}), _Line, Infinity)", //
        "{Line({{{0,0},{3,0}}})}");
    // every boundary, the edges included: the horizontal lines from the top, then the vertical ones
    check("Cases(ArrayPlot({{1, 2}}, Mesh -> True), Line(p_) :> p[[All, 1]], Infinity)", //
        "{{{0,1},{0,0}},{{0,0},{1,0},{2,0}}}");
    check("Cases(ArrayPlot({{1, 2}}, Mesh -> True), _GrayLevel, Infinity)", //
        "{GrayLevel(0.5,0.4)}");
  }

  @Test
  public void testMeshCount() {
    // n lines divide the extent into n+1 equal parts, without the edges
    check("Cases(ArrayPlot({{1, 2, 3, 4}, {5, 6, 7, 8}}, Mesh -> 1), _Line, Infinity)", //
        "{Line({{{0,1},{4,1}}}),Line({{{2,0},{2,2}}})}");
    check("Cases(ArrayPlot({{1, 2, 3, 4}, {5, 6, 7, 8}}, Mesh -> {0, 3}), _Line, Infinity)", //
        "{Line({}),Line({{{1,0},{1,2}},{{2,0},{2,2}},{{3,0},{3,2}}})}");
    check("Cases(ArrayPlot({{1, 2, 3}, {4, 5, 6}}, Mesh -> {2, 1}), _Line, Infinity)", //
        "{Line({{{0,4/3},{3,4/3}},{{0,2/3},{3,2/3}}}),Line({{{3/2,0},{3/2,2}}})}");
    check("Length(Cases(DensityPlot(x*y, {x, 0, 1}, {y, 0, 1}, Mesh -> 3), _Line, Infinity))", //
        "6");
    // a pair of counts: rows, then columns
    check("Length(Cases(DensityPlot(x*y, {x, 0, 1}, {y, 0, 1}, Mesh -> {1, 2}), _Line, Infinity))", //
        "3");
    // the empty line of a count of 0 is drawn as nothing
    check("StringContainsQ(ExportString(ArrayPlot({{1, 2}, {3, 4}}, Mesh -> {0, 1}), \"SVG\"), \"<svg\")", //
        "True");
  }
}
