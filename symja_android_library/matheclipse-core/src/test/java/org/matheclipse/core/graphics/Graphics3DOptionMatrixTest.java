package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Checks that every option a 3D plot documents is at least <em>accepted</em>, and that the ones
 * which are implemented actually change the graphic.
 *
 * <p>
 * The engine strips options by scanning backwards from the last argument and stopping at the first
 * rule the symbol does not declare. An undeclared name therefore does not merely get ignored: it
 * swallows every option written before it, so {@code Plot3D[f, .., PlotPoints -> 3, Axes -> False]}
 * sampled at the default 40. This test pins that down for the whole family.
 *
 * <p>
 * Options that are accepted but have no effect yet are listed in {@link #KNOWN_INERT}, which is
 * asserted to be exact: an entry that starts working has to be removed, so the list cannot quietly
 * rot. It is the honest inventory of what is still unimplemented in 3D.
 */
public class Graphics3DOptionMatrixTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
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
    evaluator.eval("ClearAll(a,b,c,i,j,k,n,r,s,t,u,v,w,x,y,z,p)");
  }

  /** A plot call without its closing bracket, and the options to try on it. */
  private static final String[][] MATRIX = { //
      {"Plot3D[Sin[x y],{x,-1,1},{y,-1,1},PlotPoints->30", "Mesh->None", "Mesh->All", "Mesh->5",
          "MeshStyle->Red", "PlotStyle->Red", "ColorFunction->\"Rainbow\"",
          "BoxRatios->{1,1,1}", "PlotRange->{-1,1}", "Axes->False",
          "AxesLabel->{\"x\",\"y\",\"z\"}", "Boxed->False", "ViewPoint->{0,0,3}",
          "ViewVertical->{0,1,0}", "ViewProjection->\"Orthographic\"", "ImageSize->200",
          "Background->White", "PlotLabel->\"t\"", "Ticks->None", "Lighting->\"Neutral\"",
          "FaceGrids->All", "ScalingFunctions->{\"Identity\",\"Identity\",\"Log\"}",
          "AxesEdge->{{-1,-1},{-1,-1},{-1,-1}}", "BoxStyle->Red", "AxesStyle->Blue",
          "LabelStyle->Red", "TicksStyle->Red", "SphericalRegion->True", "ViewAngle->0.5",
          "ViewCenter->{0,0,0}", "ViewRange->All", "PlotTheme->\"Detailed\"",
          "PerformanceGoal->\"Speed\"", "WorkingPrecision->MachinePrecision", "MaxRecursion->1",
          "RegionFunction->Function[{x,y,z},x^2+y^2<0.5]", "BoundaryStyle->Red",
          "NormalsFunction->Automatic", "PlotLegends->{\"a\"}", "Exclusions->{x==0}",
          "MeshFunctions->{#3&}", "MeshShading->{Red,Blue}", "Filling->Bottom",
          "FillingStyle->Red", "PlotRangePadding->0.5"}, //
      // a function with a pole, so that there is something outside the plot range for
      // ClippingStyle to decide about; the surface above never leaves its box
      {"Plot3D[1/(x y),{x,-1,1},{y,-1,1},PlotPoints->15", "ClippingStyle->None"}, //
      {"ParametricPlot3D[{Cos[t],Sin[t],t},{t,0,6},PlotPoints->8", "PlotStyle->Red",
          "BoxRatios->{1,1,1}", "Axes->False", "Boxed->False", "ViewPoint->{0,0,3}",
          "RegionFunction->Function[{x,y,z,u},z<3]"}, //
      {"ParametricPlot3D[{Cos[u]Cos[v],Sin[u]Cos[v],Sin[v]},{u,0,6},{v,-1,1},PlotPoints->30",
          "Mesh->None", "Mesh->4", "PlotStyle->Red", "ColorFunction->\"Rainbow\"",
          "BoxRatios->{1,1,1}", "MeshStyle->Red",
          "RegionFunction->Function[{x,y,z,u,v},z>0]", "BoundaryStyle->Red"}, //
      {"SphericalPlot3D[1,{t,0,3},{p,0,6},PlotPoints->6", "Mesh->None", "PlotStyle->Red",
          "ColorFunction->\"Rainbow\"", "Axes->False", "ViewPoint->{0,0,3}", "BoxRatios->{1,1,1}",
          "RegionFunction->Function[{x,y,z,t,p,r},z>0]", "BoundaryStyle->Red"}, //
      {"RevolutionPlot3D[Sqrt[t],{t,0,4},PlotPoints->6", "Mesh->None", "PlotStyle->Red",
          "RevolutionAxis->{0,1,0}", "ColorFunction->\"Rainbow\"", "Axes->False",
          "RegionFunction->Function[{x,y,z,t,th,r},x>0]", "BoundaryStyle->Red"}, //
      {"ContourPlot3D[x^2+y^2+z^2==1,{x,-2,2},{y,-2,2},{z,-2,2},PlotPoints->8", "Mesh->None",
          "PlotStyle->Red", "Axes->False", "BoxRatios->{1,1,1}", "Boxed->False",
          "RegionFunction->Function[{x,y,z,f},z>0]"}, //
      {"ContourPlot3D[x^2+y^2+z^2,{x,-2,2},{y,-2,2},{z,-2,2},PlotPoints->8", "Contours->{1}",
          "Contours->2"}, //
      // ColorFunctionScaling only says how a ColorFunction is fed, so it needs one to act on -
      // and one whose answer depends on the scale, which a gradient clipped to 0..1 does
      {"Plot3D[Sin[x y],{x,-1,1},{y,-1,1},PlotPoints->30,ColorFunction->(Hue[#3]&)",
          "ColorFunctionScaling->False"}, //
      // the surface plots whose ColorFunction was declared and then ignored
      {"ListPlot3D[Table[i*j,{i,1,6},{j,1,6}]", "ColorFunction->\"Rainbow\"",
          "ColorFunction->Function[{x,y,z},Hue[z]]"}, //
      {"ListPlot3D[Table[i*j,{i,1,6},{j,1,6}],ColorFunction->(Hue[#3]&)",
          "ColorFunctionScaling->False"}, //
      {"ContourPlot3D[x^2+y^2+z^2,{x,-2,2},{y,-2,2},{z,-2,2},PlotPoints->8,Contours->3",
          "ColorFunction->\"Rainbow\"", "ColorFunction->(Hue[#4]&)"}, //
      // the two that were passing the same value in every slot, or mixing raw with scaled
      {"SphericalPlot3D[1+0.3*Sin[4*p],{t,0,3.14},{p,0,6.28},PlotPoints->10",
          "ColorFunction->\"Rainbow\"", "ColorFunction->(Hue[#6]&)"}, //
      {"RevolutionPlot3D[Sqrt[t],{t,0,4},PlotPoints->10",
          "ColorFunction->(RGBColor[#1,#2,#3]&)", "ColorFunction->(Hue[#4]&)"}, //
      {"ParametricPlot3D[{Cos[u]*Cos[v],Sin[u]*Cos[v],Sin[v]},{u,0,6.28},{v,-1.5,1.5},"
          + "PlotPoints->10", "ColorFunction->(Hue[#4]&)", "ColorFunction->(Hue[#5]&)"}, //
      // the point, line and bar plots, which declared the option and then ignored it
      {"ListPointPlot3D[Table[{i,j,i*j},{i,1,5},{j,1,5}]//Flatten[#,1]&",
          "ColorFunction->\"Rainbow\"", "ColorFunction->Function[{x,y,z},Hue[z]]"}, //
      {"ListPointPlot3D[Table[{i,j,i*j},{i,1,5},{j,1,5}]//Flatten[#,1]&,"
          + "ColorFunction->(Hue[#3]&)", "ColorFunctionScaling->False"}, //
      {"ListLinePlot3D[{{1,2,3,4},{-1,-2,-3,-4}}", "ColorFunction->\"Rainbow\"",
          "ColorFunction->(Hue[#3]&)"}, //
      {"ListLinePlot3D[{{1,2,3,4},{-1,-2,-3,-4}},ColorFunction->(Hue[#3]&)",
          "ColorFunctionScaling->False"}, //
      {"DiscretePlot3D[i*j,{i,1,5},{j,1,5}", "ColorFunction->\"Rainbow\"",
          "ColorFunction->(Hue[#3]&)"}, //
      {"DiscretePlot3D[i*j,{i,1,5},{j,1,5},ColorFunction->(Hue[#3]&)",
          "ColorFunctionScaling->False"}, //
      // Mesh->All rather than None: this plot leaves the mesh off by default, so asking for None
      // would be asking for what it already draws
      {"ComplexPlot3D[z^2,{z,-1-I,1+I},PlotPoints->6", "Mesh->All", "PlotStyle->Red", "Axes->False",
          "BoxRatios->{1,1,1}", "RegionFunction->Function[{z,f},Abs[z]<0.8]",
          "ColorFunction->\"CyclicLogAbsArg\"", "ColorFunction->\"Rainbow\"",
          "ColorFunction->{Hue[#8]&,None}"}, //
      // ColorFunctionScaling only says how a ColorFunction is fed, so it needs one to act on
      {"ComplexPlot3D[z^2,{z,-1-I,1+I},PlotPoints->6,ColorFunction->{Hue[#8]&,None}",
          "ColorFunctionScaling->False"}, //
      {"ListPlot3D[{{1,2},{3,4}}", "Mesh->None", "BoxRatios->{1,1,1}", "DataRange->{{0,1},{0,1}}",
          "PlotStyle->Red", "Axes->False"}, //
      {"ListPlot3D[Table[i*j,{i,1,8},{j,1,8}]", "RegionFunction->Function[{x,y,z},x>4]",
          "BoundaryStyle->Red"}, //
      {"ListPointPlot3D[{{1,1,1},{2,2,2},{3,1,2}}", "PlotStyle->Red", "BoxRatios->{1,1,1}",
          "Axes->False", "PlotRange->All", "RegionFunction->Function[{x,y,z},x>1]"}, //
      {"ListLinePlot3D[Table[i*j,{i,1,5},{j,1,5}]",
          "RegionFunction->Function[{x,y,z},y>2]"}, //
      {"DiscretePlot3D[i+j,{i,1,4},{j,1,4}", "RegionFunction->Function[{x,y,z},x>2]"}, //
      {"ListPointPlot3D[{{1,2},{3,4}}", "DataRange->{{0,1},{0,1}}"}, //
  };

  /**
   * Options that are accepted and do not yet change anything.
   *
   * <p>
   * Asserted to be exact, so an entry that starts working must be removed here. Entries are written
   * as {@code "Head:Option"}.
   */
  private static final List<String> KNOWN_INERT = Arrays.asList( //
      // normals always come from the sampled surface, which is what makes it look smooth; a
      // NormalsFunction would have to replace them per vertex and the builder has no way in
      "Plot3D:NormalsFunction", //
      // MeshShading colours the patches between mesh lines individually. The surface is one
      // GraphicsComplex with one style, so this needs the surface split per band first.
      "Plot3D:MeshShading", //
      // Filling would drop a skirt from the surface to a level. ListPointPlot3D fills, because
      // its points are separate; a surface would need a second closed shell built under it.
      "Plot3D:Filling", //
      "Plot3D:FillingStyle", //
      // the sampler is double precision throughout, so there is no higher precision to ask for
      "Plot3D:WorkingPrecision", //
      // both choose between speed and quality, and there is only one sampler to choose from
      "Plot3D:PerformanceGoal", //
      "Plot3D:PlotTheme", //
      // marching cubes produces a triangle soup with no grid to draw mesh lines along
      "ContourPlot3D:Mesh" //
  );

  /** An option must not turn a graphic into something that is no longer a graphic. */
  @Test
  public void everyOptionIsAccepted() {
    List<String> failures = new ArrayList<>();
    for (String[] row : MATRIX) {
      String base = row[0];
      for (int i = 1; i < row.length; i++) {
        String call = base + "," + row[i] + "]";
        IExpr result;
        try {
          result = evaluator.eval(call);
        } catch (RuntimeException rex) {
          failures.add(call + " threw " + rex);
          continue;
        }
        if (!isGraphics3D(result)) {
          failures.add(call + " -> " + head(result));
        }
      }
    }
    assertTrue(failures.isEmpty(),
        "these calls did not produce a Graphics3D:\n" + String.join("\n", failures));
  }

  /**
   * The option must reach the graphic. An option the plot consumes shows up as a change in the
   * sampled geometry; one it passes through shows up as a rule on the {@code Graphics3D}.
   *
   * <p>
   * Reaching the graphic is not the same as being honoured by the renderer. {@code ViewRange} and
   * {@code SphericalRegion} are forwarded and then ignored, so they pass here; what the renderer
   * does with a forwarded option is asserted in {@link WebGLGraphics3DTest} instead.
   */
  @Test
  public void implementedOptionsChangeTheGraphic() {
    List<String> inert = new ArrayList<>();
    for (String[] row : MATRIX) {
      String base = row[0];
      String plain = evaluator.eval(base + "]").toString();
      String symbol = base.substring(0, base.indexOf('['));
      for (int i = 1; i < row.length; i++) {
        String option = row[i];
        String name = option.substring(0, option.indexOf("->"));
        String changed = evaluator.eval(base + "," + option + "]").toString();
        if (plain.equals(changed)) {
          inert.add(symbol + ":" + name);
        }
      }
    }
    List<String> unexpected = new ArrayList<>(inert);
    unexpected.removeAll(KNOWN_INERT);
    List<String> fixed = new ArrayList<>(KNOWN_INERT);
    fixed.removeAll(inert);

    assertTrue(unexpected.isEmpty(),
        "these options are accepted but change nothing, and are not listed in KNOWN_INERT:\n"
            + String.join("\n", unexpected));
    assertTrue(fixed.isEmpty(),
        "these options are listed in KNOWN_INERT but now work; remove them from the list:\n"
            + String.join("\n", fixed));
  }

  /**
   * {@code PlotPoints} has to survive an option written after it.
   *
   * <p>
   * This is the failure the whole option block exists to prevent, so it is asserted directly rather
   * than only implied by the matrix.
   */
  @Test
  public void anOptionDoesNotSwallowTheOneBeforeIt() {
    String coarse = evaluator.eval("Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->3]").toString();
    String withAxes =
        evaluator.eval("Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->3,Axes->False]").toString();
    String withView =
        evaluator.eval("Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->3,ViewPoint->{0,0,3}]").toString();
    // the same nine sample points in all three
    assertTrue(withAxes.contains("{1.0,1.0,2.0}"),
        "PlotPoints was lost when Axes followed it: " + withAxes);
    assertTrue(withView.contains("{1.0,1.0,2.0}"),
        "PlotPoints was lost when ViewPoint followed it: " + withView);
    assertTrue(coarse.contains("{1.0,1.0,2.0}"), coarse);
  }

  private static boolean isGraphics3D(IExpr expr) {
    // PlotLegends returns Legended[Graphics3D[...], legend], which is the shape both renderers
    // read, so the graphic is the argument underneath the wrapper
    if (expr.isAST(S.Legended, 3)) {
      expr = ((IAST) expr).arg1();
    }
    return expr.isAST() && ((IAST) expr).topHead().toString().equals("Graphics3D");
  }

  private static String head(IExpr expr) {
    String s = expr.toString();
    return s.length() > 120 ? s.substring(0, 120) + "..." : s;
  }
}
