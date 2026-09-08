package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.builtin.graphics3d.Plot3DTools;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IBuiltInSymbol;

/**
 * Pins each {@code X_*} constant to the option it is meant to name.
 *
 * <p>
 * The evaluator hands a plot its option <em>values</em> in a positional array, and the constants in
 * {@link Plot3DTools} are the only thing tying a position to a name. Inserting an option anywhere
 * but the end of a block shifts every position after it, and nothing about that fails: the plot
 * simply starts reading a different option, so {@code PlotPoints} might come back as whatever now
 * sits at 26. This test makes that shift a failure instead of a mystery.
 */
public class Plot3DOptionIndexTest {

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    try {
      F.await();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(ie);
    }
  }

  private static void assertAt(IBuiltInSymbol[] keys, int index, IBuiltInSymbol expected) {
    assertTrue(index < keys.length,
        expected + " is meant to sit at " + index + " but the block only has " + keys.length);
    assertEquals(expected, keys[index],
        "position " + index + " should be " + expected + " but is " + keys[index]);
  }

  @Test
  public void theSurfaceBlockMatchesItsConstants() {
    IBuiltInSymbol[] keys = Plot3DTools.surfacePlot().keys();

    assertAt(keys, Plot3DTools.X_AXES, S.Axes);
    assertAt(keys, Plot3DTools.X_AXES_LABEL, S.AxesLabel);
    assertAt(keys, Plot3DTools.X_AXES_EDGE, S.AxesEdge);
    assertAt(keys, Plot3DTools.X_AXES_STYLE, S.AxesStyle);
    assertAt(keys, Plot3DTools.X_BACKGROUND, S.Background);
    assertAt(keys, Plot3DTools.X_BOXED, S.Boxed);
    assertAt(keys, Plot3DTools.X_BOX_RATIOS, S.BoxRatios);
    assertAt(keys, Plot3DTools.X_BOX_STYLE, S.BoxStyle);
    assertAt(keys, Plot3DTools.X_FACE_GRIDS, S.FaceGrids);
    assertAt(keys, Plot3DTools.X_IMAGE_SIZE, S.ImageSize);
    assertAt(keys, Plot3DTools.X_LIGHTING, S.Lighting);
    assertAt(keys, Plot3DTools.X_PLOT_LABEL, S.PlotLabel);
    assertAt(keys, Plot3DTools.X_PLOT_RANGE, S.PlotRange);
    assertAt(keys, Plot3DTools.X_TICKS, S.Ticks);
    assertAt(keys, Plot3DTools.X_TICKS_STYLE, S.TicksStyle);
    assertAt(keys, Plot3DTools.X_LABEL_STYLE, S.LabelStyle);
    assertAt(keys, Plot3DTools.X_VIEW_POINT, S.ViewPoint);
    assertAt(keys, Plot3DTools.X_VIEW_VERTICAL, S.ViewVertical);
    assertAt(keys, Plot3DTools.X_VIEW_ANGLE, S.ViewAngle);
    assertAt(keys, Plot3DTools.X_VIEW_CENTER, S.ViewCenter);
    assertAt(keys, Plot3DTools.X_VIEW_PROJECTION, S.ViewProjection);
    assertAt(keys, Plot3DTools.X_VIEW_RANGE, S.ViewRange);
    assertAt(keys, Plot3DTools.X_SPHERICAL_REGION, S.SphericalRegion);
    assertAt(keys, Plot3DTools.X_SCALING_FUNCTIONS, S.ScalingFunctions);
    assertAt(keys, Plot3DTools.X_PLOT_THEME, S.PlotTheme);
    assertAt(keys, Plot3DTools.X_PERFORMANCE_GOAL, S.PerformanceGoal);

    assertAt(keys, Plot3DTools.X_PLOT_POINTS, S.PlotPoints);
    assertAt(keys, Plot3DTools.X_PLOT_STYLE, S.PlotStyle);
    assertAt(keys, Plot3DTools.X_COLOR_FUNCTION, S.ColorFunction);
    assertAt(keys, Plot3DTools.X_COLOR_FUNCTION_SCALING, S.ColorFunctionScaling);
    assertAt(keys, Plot3DTools.X_MESH, S.Mesh);
    assertAt(keys, Plot3DTools.X_MESH_STYLE, S.MeshStyle);
    assertAt(keys, Plot3DTools.X_MAX_RECURSION, S.MaxRecursion);
    assertAt(keys, Plot3DTools.X_REGION_FUNCTION, S.RegionFunction);
    assertAt(keys, Plot3DTools.X_BOUNDARY_STYLE, S.BoundaryStyle);
    assertAt(keys, Plot3DTools.X_NORMALS_FUNCTION, S.NormalsFunction);
    assertAt(keys, Plot3DTools.X_PLOT_LEGENDS, S.PlotLegends);
    assertAt(keys, Plot3DTools.X_WORKING_PRECISION, S.WorkingPrecision);
    assertAt(keys, Plot3DTools.X_EXCLUSIONS, S.Exclusions);
    assertAt(keys, Plot3DTools.X_MESH_FUNCTIONS, S.MeshFunctions);
    assertAt(keys, Plot3DTools.X_MESH_SHADING, S.MeshShading);
    assertAt(keys, Plot3DTools.X_FILLING, S.Filling);
    assertAt(keys, Plot3DTools.X_FILLING_STYLE, S.FillingStyle);
    assertAt(keys, Plot3DTools.X_CLIPPING_STYLE, S.ClippingStyle);
    assertAt(keys, Plot3DTools.X_EVALUATION_MONITOR, S.EvaluationMonitor);
    assertAt(keys, Plot3DTools.X_EXCLUSIONS_STYLE, S.ExclusionsStyle);
    assertAt(keys, Plot3DTools.X_PLOT_LABELS, S.PlotLabels);
    assertAt(keys, Plot3DTools.X_TEXTURE_COORDINATE_FUNCTION, S.TextureCoordinateFunction);
    assertAt(keys, Plot3DTools.X_TEXTURE_COORDINATE_SCALING, S.TextureCoordinateScaling);
  }

  /** The per plot extras sit immediately after the shared blocks, before the frame block. */
  @Test
  public void thePerPlotExtrasMatchTheirConstants() {
    IBuiltInSymbol[] revolution =
        Plot3DTools.frameExtras(Plot3DTools.surfaceExtras(Plot3DTools.base3D())
            .add(F.List(F.C0, F.C0, F.C1), S.RevolutionAxis)).keys();
    assertAt(revolution, Plot3DTools.X_REVOLUTION_AXIS, S.RevolutionAxis);

    IBuiltInSymbol[] contour =
        Plot3DTools.frameExtras(Plot3DTools.surfaceExtras(Plot3DTools.base3D()).add(S.Automatic,
            S.Contours, S.ContourStyle, S.RegionBoundaryStyle)).keys();
    assertAt(contour, Plot3DTools.X_CONTOURS, S.Contours);
    assertAt(contour, Plot3DTools.X_CONTOUR_STYLE, S.ContourStyle);
    assertAt(contour, Plot3DTools.X_REGION_BOUNDARY_STYLE, S.RegionBoundaryStyle);

    IBuiltInSymbol[] list = Plot3DTools.listPlot().keys();
    assertAt(list, Plot3DTools.X_DATA_RANGE, S.DataRange);
    assertAt(list, Plot3DTools.X_INTERPOLATION_ORDER, S.InterpolationOrder);
    assertAt(list, Plot3DTools.X_MAX_PLOT_POINTS, S.MaxPlotPoints);
  }

  /** The option table a symbol was actually registered with, in declaration order. */
  private static IBuiltInSymbol[] registered(IBuiltInSymbol symbol) {
    Object evaluator = symbol.getEvaluator();
    assertTrue(evaluator instanceof IFunctionEvaluator, symbol + " has no function evaluator");
    IBuiltInSymbol[] keys = ((IFunctionEvaluator) evaluator).getOptionSymbols();
    assertTrue(keys != null && keys.length > 0, symbol + " declares no options");
    return keys;
  }

  /**
   * What every plot was really registered with, which is what the evaluator indexes into.
   *
   * <p>
   * The block-builder tests above check the recipe; this one checks the result. A plot that
   * assembles its own block - to change a default, or to add an option of its own - can put a name
   * ahead of the shared ones and move every later position along without any of the recipes
   * changing. That is how {@code ComplexPlot3D} came to read {@code PlotPoints} out of a
   * neighbouring slot: it declared {@code Mesh} first, so the surface never got the sample count it
   * was asked for, and nothing failed.
   */
  @Test
  public void everyPlotIsRegisteredInTheSharedOrder() {
    for (IBuiltInSymbol symbol : new IBuiltInSymbol[] {S.Plot3D, S.ParametricPlot3D,
        S.SphericalPlot3D, S.RevolutionPlot3D, S.ContourPlot3D, S.ComplexPlot3D, S.ListPlot3D,
        S.ListPointPlot3D}) {
      IBuiltInSymbol[] keys = registered(symbol);
      // only as far as the shared block reaches: a plot is free to add options of its own after
      // it, which is where RevolutionAxis, Contours and DataRange live
      IBuiltInSymbol[] expected = Plot3DTools.surfaceExtras(Plot3DTools.base3D()).keys();
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], keys[i],
            symbol + " declares " + keys[i] + " where the shared block has " + expected[i]
                + " at position " + i + ", so every option from there on is read from the wrong"
                + " slot");
      }
    }

    IBuiltInSymbol[] listKeys = registered(S.ListPointPlot3D);
    assertAt(listKeys, Plot3DTools.X_DATA_RANGE, S.DataRange);
    assertAt(listKeys, Plot3DTools.X_INTERPOLATION_ORDER, S.InterpolationOrder);
    assertAt(listKeys, Plot3DTools.X_MAX_PLOT_POINTS, S.MaxPlotPoints);

    IBuiltInSymbol[] contourKeys = registered(S.ContourPlot3D);
    assertAt(contourKeys, Plot3DTools.X_CONTOURS, S.Contours);
    assertAt(contourKeys, Plot3DTools.X_CONTOUR_STYLE, S.ContourStyle);
    assertAt(contourKeys, Plot3DTools.X_REGION_BOUNDARY_STYLE, S.RegionBoundaryStyle);

    assertAt(registered(S.RevolutionPlot3D), Plot3DTools.X_REVOLUTION_AXIS, S.RevolutionAxis);
  }

  /** A block must not declare the same option twice, or one default silently wins. */
  @Test
  public void noOptionIsDeclaredTwice() {
    for (IBuiltInSymbol[] keys : new IBuiltInSymbol[][] {Plot3DTools.surfacePlot().keys(),
        Plot3DTools.listPlot().keys(),
        Plot3DTools.discreteExtras(new org.matheclipse.core.graphics.GraphicsOptions.OptionSet())
            .keys()}) {
      java.util.Set<IBuiltInSymbol> seen = new java.util.HashSet<>();
      for (IBuiltInSymbol key : keys) {
        assertTrue(seen.add(key), key + " is declared twice in the same block");
      }
    }
  }
}
