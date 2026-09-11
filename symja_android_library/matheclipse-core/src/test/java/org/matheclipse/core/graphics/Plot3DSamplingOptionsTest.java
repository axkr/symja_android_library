package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The options that decide <em>where</em> a 3D plot samples and <em>what</em> it keeps.
 *
 * <p>
 * {@link Graphics3DOptionMatrixTest} asserts that an option changes the graphic at all. These are
 * the options where the change has to be a particular one - a grid that got finer, a hole where a
 * region ends, a seam along an excluded curve - and where being merely different is not enough.
 * They are counted rather than compared, because the numbers say what the picture should look like
 * and a string comparison would not.
 */
public class Plot3DSamplingOptionsTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    try {
      F.await();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(ie);
    }
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    engine.init();
    evaluator = new ExprEvaluator(engine, false, (short) 100);
    evaluator.eval("ClearAll(x,y,z,i,j,k,n,t,u,v)");
  }

  /** How many vertices the surface was built from. */
  private static int vertices(String plot) {
    IExpr count = evaluator.eval("Length(Part(" + plot + ",1,1,1))");
    assertTrue(count.isInteger(), plot + " did not produce a GraphicsComplex: " + count);
    return count.toIntDefault(-1);
  }

  /** How many separate lines the graphic carries, mesh and boundary included. */
  private static int lines(String plot) {
    return evaluator.eval("Length(Cases(" + plot + ",_Line,Infinity))").toIntDefault(-1);
  }

  /** How many times the plotted function was evaluated. */
  private static int samples(String plot) {
    String monitored = plot.substring(0, plot.length() - 1) + ",EvaluationMonitor:>n++]";
    return evaluator.eval("Module({n=0}," + monitored + ";n)").toIntDefault(-1);
  }

  @Test
  public void maxRecursionRefinesTheWholeGrid() {
    String base = "Plot3D[Sin[x*y],{x,-2,2},{y,-2,2},PlotPoints->10";
    assertEquals(100, vertices(base + "]"), "ten by ten to start with");
    assertEquals(19 * 19, vertices(base + ",MaxRecursion->1]"), "one level doubles each side");
    assertEquals(37 * 37, vertices(base + ",MaxRecursion->2]"), "and again");
    assertEquals(100, vertices(base + ",MaxRecursion->0]"), "no refinement was asked for");
  }

  /**
   * Refinement stops once it stops learning anything.
   *
   * <p>
   * A plane is described exactly by its corners, so the first refinement already agrees with what
   * the coarse grid predicted and the remaining levels are not worth paying for. Without that test,
   * {@code MaxRecursion -> 4} on a plane would sample a hundred thousand points to draw something
   * two triangles can express.
   */
  @Test
  public void refinementStopsWhenTheSurfaceIsResolved() {
    int flat = samples("Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->10,MaxRecursion->4]");
    assertEquals(100 + 19 * 19, flat, "a plane needs one refinement to establish that it is flat");

    int curved = samples("Plot3D[Sin[10*x*y],{x,0,1},{y,0,1},PlotPoints->10,MaxRecursion->4]");
    assertTrue(curved > flat, "a surface that keeps bending keeps refining, got " + curved);
  }

  @Test
  public void aRegionFunctionLeavesTheSurfaceOpenWhereItEnds() {
    String base = "Plot3D[x+y,{x,-2,2},{y,-2,2},PlotPoints->20";
    int whole = vertices(base + "]");
    int disc = vertices(base + ",RegionFunction->Function({x,y,z},x^2+y^2<1)]");
    assertTrue(disc > 0 && disc < whole / 2,
        "a unit disc covers a small part of a four by four square, got " + disc + " of " + whole);
    assertEquals(whole, vertices(base + ",RegionFunction->Automatic]"),
        "Automatic keeps the whole rectangle");
  }

  @Test
  public void exclusionsCutAlongTheCurveTheyName() {
    String base = "Plot3D[x*y,{x,-2,2},{y,-2,2},PlotPoints->21";
    int whole = vertices(base + "]");
    int oneCut = vertices(base + ",Exclusions->{x==0}]");
    int twoCuts = vertices(base + ",Exclusions->{x==0,y==0}]");
    assertTrue(oneCut < whole, "the seam removes a column of samples");
    assertTrue(twoCuts < oneCut, "a second curve removes a row as well");
    assertEquals(whole, vertices(base + ",Exclusions->None]"), "None cuts nothing");
    assertTrue(vertices(base + ",Exclusions->{x-y}]") < whole,
        "an expression that is zero on the curve works as well as an equation");
  }

  /** A pole is capped at the box by default, and left open when the call asks for that. */
  @Test
  public void clippingStyleDecidesWhatHappensAtTheBox() {
    String base = "Plot3D[1/(x*y),{x,-2,2},{y,-2,2},PlotPoints->21";
    int capped = vertices(base + "]");
    assertTrue(vertices(base + ",ClippingStyle->None]") < capped,
        "the samples beyond the box are dropped rather than pinned to it");
    assertEquals(capped, vertices(base + ",ClippingStyle->Automatic]"));
  }

  @Test
  public void meshFunctionsDrawLevelsOfTheirOwn() {
    String base = "Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->15";
    int grid = lines(base + "]");
    int oneFunction = lines(base + ",MeshFunctions->{Function({x,y,z},x)}]");
    int twoFunctions = lines(base + ",MeshFunctions->{Function({x,y,z},x),Function({x,y,z},y)}]");
    assertTrue(oneFunction > grid, "a mesh function adds lines of its own");
    assertTrue(twoFunctions > oneFunction, "a second one adds more");
    assertEquals(grid, lines(base + ",MeshFunctions->Automatic]"),
        "Automatic leaves the mesh on the sampling grid");
    assertTrue(lines(base + ",MeshFunctions->{Function({x,y,z},x)},Mesh->3]") < lines(
        base + ",MeshFunctions->{Function({x,y,z},x)},Mesh->12]"), "Mesh sets the level count");
  }

  /**
   * {@code Plot3D} outlines its surface by default, as Mathematica does - its output of
   * {@code Plot3D[..., Mesh -> None]} still carries {@code {GrayLevel[0], Line[...]}}.
   * {@code None} takes the outline away and a style recolours it.
   */
  @Test
  public void boundaryStyleOutlinesWhatWasDrawn() {
    String base = "Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->10";
    int plain = lines(base + "]");
    assertEquals(plain - 1, lines(base + ",BoundaryStyle->None]"),
        "the default outline is one line, and None removes it");
    assertEquals(plain, lines(base + ",BoundaryStyle->Automatic]"), "Automatic is the default");
    assertEquals(plain, lines(base + ",BoundaryStyle->Red]"), "a style recolours the same outline");
    assertTrue(evaluator.eval("MemberQ[" + base + "],{GrayLevel[0],_Line},Infinity]").isTrue(),
        "the default outline is black, as Mathematica writes it");
    assertTrue(evaluator.eval("MemberQ[" + base + ",BoundaryStyle->Red],{Red,_Line},Infinity]")
        .isTrue(), "the outline takes the style it was given");

    String region = "Plot3D[x+y,{x,-2,2},{y,-2,2},PlotPoints->20,"
        + "RegionFunction->Function({x,y,z},x^2+y^2<1)";
    assertEquals(lines(region + ",BoundaryStyle->None]") + 1, lines(region + "]"),
        "the edge of a region is outlined too");
  }

  /**
   * The monitor runs once per sample, which is what makes it usable for counting.
   *
   * <p>
   * It is written with {@code :>} so the expression arrives unevaluated. That form used not to be
   * recognised as an option at all, and because an unrecognised option stops the backwards scan it
   * took every option written before it down with it.
   */
  @Test
  public void anEvaluationMonitorRunsOncePerSample() {
    assertEquals(25, samples("Plot3D[x*y,{x,0,1},{y,0,1},PlotPoints->5]"));
    assertEquals(12, samples("ParametricPlot3D[{Cos[t],Sin[t],t},{t,0,6},PlotPoints->12]"));
    assertEquals(6 * 6 * 6,
        samples("ContourPlot3D[x^2+y^2+z^2==1,{x,-2,2},{y,-2,2},{z,-2,2},PlotPoints->6]"));
    assertEquals(36, samples("ComplexPlot3D[Sqrt[z],{z,-2-2*I,2+2*I},PlotPoints->6]"));
    assertEquals(16, samples("DiscretePlot3D[i+j,{i,1,4},{j,1,4}]"));
  }

  /** An option written with {@code :>} must not swallow the ones written before it. */
  @Test
  public void aDelayedOptionDoesNotSwallowTheOneBeforeIt() {
    assertEquals(25, vertices("Plot3D[x*y,{x,0,1},{y,0,1},PlotPoints->5,EvaluationMonitor:>Null]"),
        "PlotPoints still reached the sampler");
  }

  /** A delayed rule that is not an option name is still an ordinary argument. */
  @Test
  public void anOrdinaryDelayedRuleIsNotReadAsAnOption() {
    assertEquals(F.C2, evaluator.eval("Replace(f(1),f(x_):>x+1)"));
    assertEquals(F.ZZ(100), evaluator.eval("f(1)/.f(x_):>x+99"));
  }
}
