package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.TestTags;

/**
 * Checks that every option a plot documents is at least <em>accepted</em>.
 *
 * <p>
 * An option name that a symbol does not declare is parsed as an extra positional argument, so the
 * whole call fails with an arity error rather than being ignored. That used to happen to
 * {@code ContourPlot[..., Contours -> 5]} and to every {@code Chart*} option of {@code BarChart}.
 * This test pins that down: the option must produce a graphic.
 *
 * <p>
 * Options that are accepted but have no effect yet are listed in {@link #KNOWN_INERT}. That list is
 * the honest inventory of what is still unimplemented, and it is asserted to be exact: an entry
 * that starts working must be removed, so the list cannot quietly rot.
 */
public class PlotOptionMatrixTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    // Other test classes lower these shared limits; a density style plot legitimately builds a
    // large expression (one rectangle per cell), so the ceiling has to be restored here rather
    // than inherited from whichever test ran last.
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    Config.MAX_MATRIX_DIMENSION_SIZE = Integer.MAX_VALUE;
    Config.FILESYSTEM_ENABLED = false;
    try {
      F.await();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(ie);
    }
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    engine.init();
    engine.setRecursionLimit(512);
    engine.setIterationLimit(10000);
    evaluator = new ExprEvaluator(engine, false, (short) 100);
    // Other tests in the suite leave values assigned to common one letter symbols, and a plot
    // whose variable is already bound does not evaluate to a graphic. Clearing them keeps these
    // tests independent of the order the suite happens to run in.
    evaluator.eval("ClearAll(a,b,c,i,j,k,n,r,s,t,u,v,w,x,y,z)");
  }

  /**
   * A logarithmic axis spans the positive data.
   *
   * <p>
   * One non positive value used to decide the bottom of the axis: {@code Log[Range[20]]} starts at
   * zero, and the axis was then floored ten decades below the data, so a plot whose values ran from
   * 1 to 20 was squeezed into the top sliver and labelled 0.01 and 1 instead of 1 to 20.
   */
  @Test
  public void aLogAxisSpansThePositiveData() {
    String svg =
        svgOf("ListLogLogPlot[{Range[20], Sqrt[Range[20]], Log[Range[20]]}, Joined->True]");
    assertTrue(svg.contains(">20</text>"), "the axis has to reach the largest value in the data");
    assertTrue(!svg.contains(">0.01</text>"),
        "the zero in one of the datasets must not drag the axis down");
  }

  /** A tick of 1000 reads as a number; only larger powers become a superscript. */
  @Test
  public void logTicksSwitchToPowersAboveAThousand() {
    String svg = svgOf("LogPlot[Exp[x],{x,0,10}]");
    assertTrue(svg.contains(">1000<"), "a thousand is written out: " + labels(svg));
    assertTrue(svg.contains("<tspan dy=\"-0.6em\""),
        "ten thousand is set as a superscript: " + labels(svg));
  }

  private static String svgOf(String input) {
    IExpr result = evaluator.eval(input);
    StringBuilder buf = new StringBuilder();
    assertTrue(result.isAST() && org.matheclipse.core.eval.GraphicsUtil.renderGraphics2DSVG(buf,
        (IAST) result, true, EvalEngine.get()), input + " did not render");
    return buf.toString();
  }

  private static String labels(String svg) {
    java.util.List<String> found = new ArrayList<>();
    java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("<text[^>]*>(.*?)</text>").matcher(svg);
    while (m.find()) {
      found.add(m.group(1));
    }
    return found.toString();
  }

  /** A plot call without its closing bracket, and the options to try on it. */
  private static final String[][] MATRIX = { //
      {"Plot[Sin[x],{x,0,6}", "PlotPoints->10", "MaxRecursion->2", "PlotTheme->\"Detailed\"",
          "LabelStyle->Red", "Mesh->All", "ColorFunction->\"Rainbow\"", "PlotMarkers->{\"x\"}",
          "ScalingFunctions->\"Log\"", "PlotStyle->Red", "Filling->Axis", "PlotLegends->{\"a\"}",
          "ImageSize->200", "Frame->True", "GridLines->Automatic", "PlotRangePadding->0.5"}, //
      {"ListPlot[{1,3,2,5}", "PlotMarkers->{\"x\"}", "LabelStyle->Red", "PlotTheme->\"Detailed\"",
          "ColorFunction->\"Rainbow\"", "ScalingFunctions->\"Log\"", "Joined->True",
          "PlotStyle->Red", "Filling->Axis", "DataRange->{0,10}", "PlotRangePadding->None"}, //
      // Mesh marks points along a curve, so it needs a joined plot to have anything to mark;
      // on a scatter plot every point is drawn already and it is rightly a no-op.
      // InterpolationOrder likewise only changes how points are connected once they are.
      {"ListPlot[{1,3,2,5},Joined->True", "Mesh->All", "InterpolationOrder->3"}, //
      // Exclusions breaks the curve where it jumps, so the base needs somewhere to break;
      // WorkingPrecision only shows where machine arithmetic loses the answer entirely
      {"Plot[Tan[x],{x,-4,4},PlotRange->{-8,8}", "Exclusions->None", "Exclusions->{Pi/2}"}, //
      {"Plot[(1-Cos[x])/x^2,{x,-0.00000001,0.00000001}", "WorkingPrecision->30"}, //
      // ClippingStyle only has something to draw when the curve leaves the plot range, so these
      // bases deliberately cut one off
      {"Plot[Sin[x]/x,{x,-10,10},PlotRange->{0,0.5}", "ClippingStyle->Red"}, //
      {"ListPlot[Table[n^2,{n,20}],PlotRange->{0,100},Joined->True", "ClippingStyle->Red"}, //
      {"ParametricPlot[{Cos[t],Sin[t]},{t,0,6.3}", "Mesh->All", "PlotPoints->10",
          "ColorFunction->\"Rainbow\"", "MaxRecursion->2", "PlotMarkers->{\"x\"}", "PlotStyle->Red",
          "AspectRatio->1"}, //
      {"PolarPlot[1+Cos[t],{t,0,6.3}", "PlotPoints->10", "PolarAxes->True",
          "PolarGridLines->Automatic", "Mesh->All", "PlotStyle->Red", "PolarTicks->{0,1}"}, //
      {"ContourPlot[x*y,{x,0,3},{y,0,3}", "Contours->5", "ContourShading->False",
          "ContourStyle->Red", "ColorFunction->\"Rainbow\"", "PlotPoints->10",
          "ContourLabels->True", "ContourLines->False", "MaxRecursion->2", "Mesh->All",
          "Frame->False"}, //
      {"DensityPlot[x*y,{x,0,3},{y,0,3}", "ColorFunction->\"Rainbow\"", "PlotPoints->10",
          "Mesh->All", "ColorFunctionScaling->False", "Frame->False"}, //
      {"ListContourPlot[Table[x*y,{x,1,8},{y,1,8}]", "Contours->3", "ContourShading->False",
          "ContourStyle->Red", "DataRange->{0,1}", "MaxPlotPoints->4", "Frame->False",
          "ContourLines->False", "Mesh->All", "ColorFunction->\"Rainbow\""}, //
      {"ListDensityPlot[Table[x*y,{x,1,8},{y,1,8}]", "ColorFunction->\"Rainbow\"",
          "ColorFunctionScaling->False", "DataRange->{0,1}", "InterpolationOrder->0",
          "MaxPlotPoints->4", "Mesh->All", "Frame->False"}, //
      {"BarChart[{1,4,2}", "ChartStyle->Red", "ChartLabels->{\"a\",\"b\",\"c\"}", "BarOrigin->Left",
          "BarSpacing->0.5", "ChartLegends->{\"x\"}", "ChartBaseStyle->Red",
          "ChartLayout->\"Stacked\"", "ChartElementFunction->\"GlassRectangle\"",
          "LabelingFunction->Above", "Frame->True"}, //
      {"PieChart[{1,4,2}", "ChartStyle->Red", "ChartLabels->{\"a\",\"b\",\"c\"}",
          "ChartLegends->{\"x\"}", "SectorOrigin->0", "SectorSpacing->0.2",
          "LabelingFunction->Above"}, //
      // Histogram already frames by default, so the option has to be flipped the other way to
      // tell "honoured" from "ignored". Two datasets, because stacking one changes nothing.
      {"Histogram[{{1,2,2,3},{2,3,3,4}}", "ChartStyle->Red", "ChartLabels->{\"a\"}",
          "ChartBaseStyle->Red", "ChartLegends->{\"x\"}", "ChartLayout->\"Stacked\"",
          "Frame->False"}, //
      {"BoxWhiskerChart[{1,2,3,4,5}", "ChartStyle->Red", "ChartLabels->{\"a\"}",
          "ChartLegends->{\"x\"}", "ChartElements->Graphics[Disk[]]"}, //
      {"MatrixPlot[{{1,2},{3,4}}", "ColorFunction->\"Rainbow\"", "Mesh->All",
          "ColorFunctionScaling->False", "ColorRules->{1->Red}", "Frame->False"}, //
      // MaxPlotPoints only thins a matrix that is larger than the limit it is given
      {"MatrixPlot[Table[i*j,{i,1,30},{j,1,30}]", "MaxPlotPoints->5"}, //
      {"ArrayPlot[{{1,2},{3,4}}", "ColorFunction->\"Rainbow\"", "Mesh->All", "ColorRules->{1->Red}",
          "ColorFunctionScaling->False", "Frame->False"}, //
      {"ArrayPlot[Table[i*j,{i,1,30},{j,1,30}]", "MaxPlotPoints->5"}, //
      {"DiscretePlot[k,{k,1,10}", "Filling->None", "PlotMarkers->{\"x\"}", "ExtentSize->0.5",
          "PlotStyle->Red", "Joined->True", "Frame->True", "ImageSize->200"}, //
      {"ListStepPlot[{1,3,2}", "Joined->False", "ExtentSize->0.5", "PlotMarkers->{\"x\"}",
          "PlotStyle->Red", "Filling->Axis", "ScalingFunctions->\"Log\"",
          "ColorFunction->\"Rainbow\""}, //
      {"NumberLinePlot[{1,2,3}", "PlotStyle->Red", "PlotMarkers->{\"x\"}", "Spacings->2",
          "ImageSize->200", "PlotLabel->\"t\""}, //
      {"ComplexPlot[z,{z,-1-I,1+I}", "ColorFunction->\"Rainbow\"", "PlotPoints->10",
          "RegionFunction->Function[{z,f},Abs[z]<0.8]"}, //
      // a parametric curve is given its parameter as the third argument
      {"ParametricPlot[{Cos[t],Sin[t]},{t,0,6.28},PlotPoints->24",
          "ColorFunction->(Hue[#3]&)", "ColorFunction->\"Rainbow\""}, //
      {"ParametricPlot[{Cos[t],Sin[t]},{t,0,6.28},PlotPoints->24,ColorFunction->(Hue[#3]&)",
          "ColorFunctionScaling->False"}, //
      // the families whose ColorFunction was either an arity error or a no-op until now
      {"PolarPlot[1+Cos[t],{t,0,6.28},PlotPoints->20", "ColorFunction->\"Rainbow\"",
          "ColorFunction->(Hue[#4]&)"}, //
      // ColorFunctionScaling only says how a ColorFunction is fed, so it needs one to act on
      {"PolarPlot[1+Cos[t],{t,0,6.28},PlotPoints->20,ColorFunction->\"Rainbow\"",
          "ColorFunctionScaling->False"}, //
      {"Histogram[{1,1,2,2,2,3,4,4,4,4,5}", "ColorFunction->\"Rainbow\""}, //
      {"Histogram[{1,1,2,2,2,3,4,4,4,4,5},ColorFunction->\"Rainbow\"",
          "ColorFunctionScaling->False"}, //
      {"BarChart[{3,1,4,1,5,9}", "ColorFunction->\"Rainbow\""}, //
      {"BarChart[{3,1,4,1,5,9},ColorFunction->\"Rainbow\"", "ColorFunctionScaling->False"}, //
      {"PieChart[{1,2,3,4}", "ColorFunction->\"Rainbow\""}, //
      {"BoxWhiskerChart[{{1,2,3,4,5},{10,11,12,13,14},{5,6,7,8,9}}",
          "ColorFunction->\"Rainbow\""}, //
      {"BoxWhiskerChart[{{1,2,3,4,5},{10,11,12,13,14},{5,6,7,8,9}},ColorFunction->\"Rainbow\"",
          "ColorFunctionScaling->False"}, //
      {"DensityHistogram[{{1,1},{1,1},{2,2},{3,3},{3,3},{3,3}}", "ColorFunction->\"Rainbow\""}, //
      // a curve now takes its gradient from the value, and a directive reaches the primitives
      {"Plot[Sin[x],{x,0,6}", "ColorFunction->(Directive[Opacity[0.4],Red]&)",
          "ColorFunction->(If[#2>0,Red,Blue]&)"}, //
      // the complex shading schemes, and the eight argument form of a colour function
      {"ComplexPlot[(z^2+1)/(z^2-1),{z,-2-2*I,2+2*I},PlotPoints->10",
          "ColorFunction->\"CyclicLogAbsArg\"", "ColorFunction->\"GlobalAbs\"",
          "ColorFunction->\"QuantileAbs\"", "ColorFunction->{Hue[#8]&,None}",
          "ColorFunction->{\"Rainbow\",\"CyclicLogAbs\"}"}, //
      // ColorFunctionScaling only says how a ColorFunction is fed, so it needs one to act on
      {"ComplexPlot[z,{z,-1-I,1+I},ColorFunction->\"Rainbow\"", "ColorFunctionScaling->False"}, //
      {"Plot[Sin[x],{x,0,6},ColorFunction->Hue", "ColorFunctionScaling->False"}, //
      // Found by auditing each symbol's registered options against this matrix: these were
      // accepted everywhere and tested nowhere.
      {"Plot[Tan[x],{x,-4,4},PlotRange->{-8,8}", "ExclusionsStyle->Red"}, //
      {"Plot[Sin[x],{x,0,6},Mesh->All", "MeshStyle->Red", "MeshShading->{Red,Blue}",
          "MeshFunctions->{#2&}"}, //
      // RegionFunction is given the whole tuple its family documents; a predicate that names
      // fewer parameters simply ignores the rest, which is why the two argument form works here
      {"Plot[Sin[x],{x,0,6}", "RegionFunction->Function[{x,y},y>0]"}, //
      {"ContourPlot[x*y,{x,0,3},{y,0,3}", "BoundaryStyle->Red",
          "RegionFunction->Function[{x,y},x>1]"}, //
      {"DensityPlot[x*y,{x,0,3},{y,0,3}", "BoundaryStyle->Red", "MeshStyle->Red",
          "RegionFunction->Function[{x,y,z},x>1]"}, //
      {"PolarPlot[1+Cos[t],{t,0,6.3}", "RegionFunction->Function[{x,y,th,r},r>1]"}, //
      {"ParametricPlot[{Cos[t],Sin[t]},{t,0,6.3}",
          "RegionFunction->Function[{x,y,t},y>0]"}, //
      {"ParametricPlot[{u,v},{u,0,1},{v,0,1}",
          "RegionFunction->Function[{x,y,u,v},x+y<1]"}, //
      {"ListContourPlot[Table[x*y,{x,1,8},{y,1,8}]",
          "RegionFunction->Function[{x,y,z},x>4]", "BoundaryStyle->Red"}, //
      {"ListDensityPlot[Table[x*y,{x,1,8},{y,1,8}]",
          "RegionFunction->Function[{x,y,z},x>4]", "BoundaryStyle->Red"}, //
      {"BarChart[{1,4,2},ChartLabels->{\"a\",\"b\",\"c\"}", "LabelingSize->20"}, //
      {"WordCloud[{\"aa\",\"bb\",\"bb\",\"cc\"}", "WordSelectionFunction->(StringLength[#]>1&)"}, //
      // four distinct weights, because a scale only shows once there is something between the
      // smallest and the largest: with two words any monotone scale normalises to the same pair
      {"WordCloud[{\"aa\",\"bb\",\"bb\",\"cc\",\"cc\",\"cc\",\"dd\",\"dd\",\"dd\",\"dd\"}",
          "ColorFunction->\"Rainbow\"", "WordOrientation->\"Vertical\"", "WordSpacings->2",
          "ScalingFunctions->\"Log\""}, //
  };

  /**
   * Options that are accepted but do not yet change the picture, as {@code "Function OptionName"}.
   *
   * <p>
   * The list is empty: every option the matrix tries now changes the drawing. It is asserted to be
   * exact in both directions, so an option that stops working shows up here as a failure rather
   * than being quietly added back.
   */
  private static final List<String> KNOWN_INERT = Arrays.asList( //
      "Plot ExclusionsStyle", "Plot MeshStyle", "Plot MeshShading", "Plot MeshFunctions", //
      "DensityPlot MeshStyle", //
      "PolarPlot PolarTicks", //
      "BarChart LabelingSize", //
      "WordCloud WordSelectionFunction");

  private static String render(String input) {
    try {
      IExpr result = evaluator.eval(input);
      if (!(result instanceof IAST) || !result.isGraphicsObject()) {
        return null;
      }
      return normalizeIds(new SVGGraphics(600, 400).toSVG((IAST) result, true));
    } catch (RuntimeException | StackOverflowError ex) {
      return null;
    }
  }

  /**
   * Blank out the generated element ids.
   *
   * <p>
   * They are derived from the hash of the {@code Graphics} expression, so an option that reaches
   * the rules but changes nothing about the picture would still alter the SVG text and count as
   * honoured. Comparing without them is what makes "the option did something" mean "the drawing
   * came out different".
   */
  private static String normalizeIds(String svg) {
    return svg.replaceAll("(plotArea|legendGradient)_[0-9a-f]+", "$1_ID");
  }

  /** No documented option may be rejected: the call must still produce a graphic. */
  @Test
  @Tag(TestTags.SLOW)
  public void testNoOptionIsRejected() {
    List<String> rejected = new ArrayList<>();
    for (String[] row : MATRIX) {
      String base = row[0];
      String function = base.substring(0, base.indexOf('['));
      for (int i = 1; i < row.length; i++) {
        if (render(base + "," + row[i] + "]") == null) {
          rejected.add(function + " " + row[i]);
        }
      }
    }
    assertTrue(rejected.isEmpty(),
        () -> "options rejected instead of accepted (an option a symbol does not declare is "
            + "parsed as a positional argument, which fails with an arity error):\n  "
            + String.join("\n  ", rejected));
  }

  /**
   * The inventory of accepted-but-inert options must match {@link #KNOWN_INERT} exactly, so that
   * implementing one forces the list to be updated and a regression cannot hide in it.
   */
  @Test
  @Tag(TestTags.SLOW)
  public void testInertOptionInventoryIsExact() {
    List<String> inert = new ArrayList<>();
    for (String[] row : MATRIX) {
      String base = row[0];
      String function = base.substring(0, base.indexOf('['));
      String plain = render(base + "]");
      if (plain == null) {
        continue;
      }
      for (int i = 1; i < row.length; i++) {
        String name = row[i].substring(0, row[i].indexOf("->"));
        String withOption = render(base + "," + row[i] + "]");
        if (plain.equals(withOption)) {
          inert.add(function + " " + name);
        }
      }
    }
    List<String> newlyInert = new ArrayList<>(inert);
    newlyInert.removeAll(KNOWN_INERT);
    List<String> nowWorking = new ArrayList<>(KNOWN_INERT);
    nowWorking.removeAll(inert);

    assertTrue(newlyInert.isEmpty(),
        () -> "these options stopped having an effect:\n  " + String.join("\n  ", newlyInert));
    assertTrue(nowWorking.isEmpty(),
        () -> "these options now work; remove them from KNOWN_INERT:\n  "
            + String.join("\n  ", nowWorking));
  }

  /** Rendering the same plot twice must produce identical output. */
  @Test
  public void testPlotOutputIsReproducible() {
    List<String> unstable = new ArrayList<>();
    for (String[] row : MATRIX) {
      String input = row[0] + "]";
      String first = render(input);
      String second = render(input);
      if (first != null && !first.equals(second)) {
        unstable.add(input);
      }
    }
    assertTrue(unstable.isEmpty(),
        () -> "these plots render differently on each call, which makes them impossible to "
            + "compare or regression test:\n  " + String.join("\n  ", unstable));
  }
}
