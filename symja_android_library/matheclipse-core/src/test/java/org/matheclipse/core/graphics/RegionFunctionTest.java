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
 * {@code RegionFunction} across the plot families.
 *
 * <p>
 * {@link PlotOptionMatrixTest} and {@link Graphics3DOptionMatrixTest} assert that the option
 * changes the picture at all. These are the cases where the change has to be a particular one -
 * fewer samples, a curve broken into two, a raster cell that went transparent - so they are counted
 * rather than compared.
 *
 * <p>
 * The predicate is handed the whole tuple its family documents - real coordinates everywhere except
 * the two complex plots, which pass the sample point and the value - and that is more arguments
 * than most predicates name. That is deliberate and is pinned here: {@code Function} binds the
 * parameters it declares and discards the rest, so a caller may write {@code Function({x, y}, ...)}
 * for a plot that supplies three coordinates.
 */
public class RegionFunctionTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    Config.MAX_MATRIX_DIMENSION_SIZE = Integer.MAX_VALUE;
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
    evaluator.eval("ClearAll(a,b,c,f,i,j,k,n,p,r,s,t,u,v,w,x,y,z)");
  }

  private static int count(String expression) {
    IExpr result = evaluator.eval(expression);
    int value = result.toIntDefault(-1);
    assertTrue(value >= 0, expression + " did not count anything: " + result);
    return value;
  }

  /** How many separate polylines the graphic carries. */
  private static int lines(String plot) {
    return count("Length(Cases(" + plot + ",_Line,Infinity))");
  }

  /** How many points those polylines are drawn through, all of them together. */
  private static int linePoints(String plot) {
    return count("Total(Cases(" + plot + ",Line(l_):>Length(l),Infinity))");
  }

  /** How many vertices a {@code GraphicsComplex} was built from. */
  private static int vertices(String plot) {
    return count("Total(Cases(" + plot + ",GraphicsComplex(v_,__):>Length(v),Infinity))");
  }

  /** How many corners the drawn polygons have, all of them together. */
  private static int polygonPoints(String plot) {
    return count("Total(Cases(" + plot + ",Polygon(l_):>Length(l),Infinity))");
  }

  /**
   * How many raster cells were left unpainted: the cells of fully transparent colour. A raster's
   * cells are numbers, {r, g, b} or {r, g, b, a}, as in the Wolfram Language.
   */
  private static int transparentCells(String plot) {
    return count("Count(Flatten(Cases(" + plot + ",Raster(d_,___):>d,Infinity),2),"
        + "{_,_,_,a_/;a==0})");
  }

  // -----------------------------------------------------------------------------------------
  // 2D plots that sample a curve
  // -----------------------------------------------------------------------------------------

  /**
   * The curve stops where the region does, and starts again on the other side.
   *
   * <p>
   * Two humps of a sine are two separate polylines, not one polyline with a chord along the axis
   * between them. The region is tested inside the sampler, so the adaptive refinement finds the
   * crossing rather than stepping over it.
   */
  @Test
  public void plotBreaksTheCurveWhereTheRegionEnds() {
    String base = "Plot(Sin(x),{x,0,12},PlotPoints->60";
    assertEquals(1, lines(base + ")"), "the whole sine is one polyline");
    assertEquals(60, linePoints(base + ")"));

    String half = base + ",RegionFunction->Function({x,y},y>0))";
    assertEquals(2, lines(half), "the two humps above the axis are two polylines");
    assertTrue(linePoints(half) < 40, "only the samples above the axis are drawn");
  }

  /** The settings that mean "no region" leave the picture exactly as it was. */
  @Test
  public void theDefaultsKeepTheWholePicture() {
    String base = "Plot(Sin(x),{x,0,12},PlotPoints->60";
    int whole = linePoints(base + ")");
    assertEquals(whole, linePoints(base + ",RegionFunction->Automatic)"));
    assertEquals(whole, linePoints(base + ",RegionFunction->None)"));
    assertEquals(whole, linePoints(base + ",RegionFunction->(True&))"), "(True&) is the default");
  }

  @Test
  public void polarPlotIsClippedInPolarCoordinates() {
    String base = "PolarPlot(1+Cos(t),{t,0,6.28}";
    assertEquals(1, lines(base + ")"));
    // the cardioid's radius drops below one over the second half of its sweep
    String outer = base + ",RegionFunction->Function({x,y,th,r},r>1))";
    assertEquals(2, lines(outer), "the lobe is cut where the radius drops below one");
    assertTrue(linePoints(outer) < linePoints(base + ")"));
  }

  @Test
  public void parametricPlotBreaksTheCurve() {
    String base = "ParametricPlot({Cos(t),Sin(t)},{t,0,6.3}";
    assertEquals(1, lines(base + ")"), "a circle is one closed polyline");
    String upper = base + ",RegionFunction->Function({x,y,t},y>0))";
    assertEquals(2, lines(upper), "the two ends of the upper half arrive separately");
    assertTrue(linePoints(upper) < linePoints(base + ")") / 2 + 5,
        "about half the sweep is above the axis");
  }

  @Test
  public void aParametricRegionIsCutToShape() {
    String base = "ParametricPlot({u,v},{u,0,1},{v,0,1}";
    int square = vertices(base + ")");
    int triangle = vertices(base + ",RegionFunction->Function({x,y,u,v},x+y<1))");
    assertTrue(triangle > 0 && triangle < square,
        "half a square is fewer vertices than all of it, got " + triangle + " of " + square);
  }

  // -----------------------------------------------------------------------------------------
  // 2D plots that sample a grid
  // -----------------------------------------------------------------------------------------

  @Test
  public void contourPlotIsRestrictedToTheRegion() {
    String base = "ContourPlot(x*y,{x,0,3},{y,0,3},PlotPoints->10";
    int whole = polygonPoints(base + ")");
    int right = polygonPoints(base + ",RegionFunction->Function({x,y,z},x>1))");
    assertTrue(right > 0 && right < whole,
        "two thirds of the square is less than all of it, got " + right + " of " + whole);
    assertEquals(whole, polygonPoints(base + ",RegionFunction->Automatic)"));
  }

  /**
   * A contour plot restricted to an annulus.
   *
   * <p>
   * Before the region was honoured this drew a filled square, because the grid sampler ignored the
   * option entirely. The hole in the middle is what the option is for.
   */
  @Test
  public void anAnnulusIsHollow() {
    String base = "ContourPlot(x^2-y^2,{x,-3,3},{y,-3,3},PlotPoints->20";
    String annulus = base + ",RegionFunction->Function({x,y,z},2<x^2+y^2<9))";
    assertTrue(polygonPoints(annulus) < polygonPoints(base + ")"),
        "the disc in the middle and the corners outside are not drawn");
    // a point in the hole is not covered by any polygon
    IExpr covered = evaluator.eval(
        "MemberQ(Cases(" + annulus + ",Polygon(l_):>l,Infinity),{___,{x_,y_},___}/;x^2+y^2<1)");
    assertTrue(covered.isFalse(), "nothing is drawn inside the inner radius");
  }

  @Test
  public void densityPlotLeavesRejectedCellsTransparent() {
    String base = "DensityPlot(x*y,{x,0,3},{y,0,3},PlotPoints->10";
    assertEquals(0, transparentCells(base + ")"), "every cell is painted without a region");
    assertTrue(transparentCells(base + ",RegionFunction->Function({x,y,z},x>1))") > 0,
        "the cells outside the region are left unpainted");
  }

  @Test
  public void theListVariantsTakeTheSameRegion() {
    String contour = "ListContourPlot(Table(x*y,{x,1,8},{y,1,8})";
    assertTrue(polygonPoints(contour + ",RegionFunction->Function({x,y,z},x>4))") //
        < polygonPoints(contour + ")"));

    String density = "ListDensityPlot(Table(x*y,{x,1,8},{y,1,8})";
    assertEquals(0, transparentCells(density + ")"));
    assertTrue(transparentCells(density + ",RegionFunction->Function({x,y,z},x>4))") > 0);
  }

  /**
   * The complex plots select on the sample point and on the value, both complex.
   *
   * <p>
   * This is the one family that is not handed a tuple of real coordinates: {@code z} is the point
   * the function was sampled at and {@code f} is what it came back with, so a region may be written
   * either way round - {@code Abs(z) < 2} shapes the domain, {@code 1 <= Abs(f) <= 2} cuts the
   * zeros and the poles out of it.
   */
  @Test
  public void theComplexPlotsSelectOnThePointAndOnTheValue() {
    String plot = "ComplexPlot(z,{z,-2-2*I,2+2*I},PlotPoints->40";
    assertEquals(0, transparentCells(plot + ")"), "every cell is painted without a region");
    // the disc of radius two covers pi/4 of the square it is inscribed in
    int outsideDisc = transparentCells(plot + ",RegionFunction->Function({z,f},Abs(z)<2))");
    assertTrue(outsideDisc > 250 && outsideDisc < 450,
        "about a fifth of the square lies outside the disc, got " + outsideDisc + " of 1600");
    assertEquals(800,
        transparentCells(plot + ",RegionFunction->Function({z,f},-Pi/2<=Arg(z)<Pi/2))"),
        "half the plane by argument is exactly half the cells");
    assertEquals(0, transparentCells(plot + ",RegionFunction->Automatic)"));

    // a region on the value rather than on the point
    assertTrue(transparentCells("ComplexPlot((z^2-1)/(z^2+1),{z,-2-2*I,2+2*I},PlotPoints->40,"
        + "RegionFunction->Function({z,f},1<=Abs(f)<=2))") > 0);

    String surface = "ComplexPlot3D(z,{z,-2-2*I,2+2*I},PlotPoints->20";
    assertEquals(400, vertices(surface + ")"));
    assertTrue(vertices(surface + ",RegionFunction->Function({z,f},Abs(z)<2))") < 400);
    assertTrue(vertices("ComplexPlot3D(Sqrt(z),{z,-2-2*I,2+2*I},PlotPoints->20,"
        + "RegionFunction->Function({z,f},Abs(f)<1))") < 400);
    assertEquals(400, vertices(surface + ",RegionFunction->Automatic)"));
  }

  /**
   * Every 3D plot with a sampling grid outlines what it drew, when asked.
   *
   * <p>
   * Nothing is outlined unless a style is given: a surface is read by its shading, and a rim round
   * every plot that never asked for one would be noise. So {@code Automatic} and {@code None} must
   * leave the graphic byte for byte the same as no option at all - which is also what keeps the
   * shape these plots have always returned.
   */
  @Test
  public void theThreeDimensionalSurfacesOutlineWhatTheyDrew() {
    String[] bases = { //
        "ParametricPlot3D({Cos(u)*Cos(v),Sin(u)*Cos(v),Sin(v)},{u,0,6},{v,-1,1},PlotPoints->12",
        "SphericalPlot3D(1,{t,0,3},{p,0,6},PlotPoints->12",
        "RevolutionPlot3D(Sqrt(t),{t,0,4},PlotPoints->12", "ListPlot3D(Table(i*j,{i,1,8},{j,1,8})"};
    for (String base : bases) {
      int plain = lines(base + ")");
      assertTrue(lines(base + ",BoundaryStyle->Red)") > plain, base + " drew no rim");
      assertTrue(
          evaluator.eval("SameQ(" + base + ")," + base + ",BoundaryStyle->Automatic))").isTrue(),
          base + " changed without being asked to");
      assertTrue(evaluator.eval("SameQ(" + base + ")," + base + ",BoundaryStyle->None))").isTrue(),
          base + " outlined something for None");
      assertTrue(evaluator.eval("MemberQ(" + base + ",BoundaryStyle->Red),Red,Infinity)").isTrue(),
          base + " did not draw the rim in the style it was given");
    }
  }

  /** The rim follows the edge a region cut, not the edge of the sampled rectangle. */
  @Test
  public void theOutlineFollowsTheRegionEdge() {
    String[][] regions = { //
        {"ParametricPlot3D({Cos(u)*Cos(v),Sin(u)*Cos(v),Sin(v)},{u,0,6},{v,-1,1},PlotPoints->12",
            "RegionFunction->Function({x,y,z,u,v},z>0)"},
        {"SphericalPlot3D(1,{t,0,3},{p,0,6},PlotPoints->16",
            "RegionFunction->Function({x,y,z,t,p,r},z>0)"},
        {"RevolutionPlot3D(Sqrt(t),{t,0,4},PlotPoints->12",
            "RegionFunction->Function({x,y,z,t,th,r},x>0)"},
        {"ListPlot3D(Table(i*j,{i,1,8},{j,1,8})", "RegionFunction->Function({x,y,z},x>4)"},
        {"Plot3D(x+y,{x,-2,2},{y,-2,2},PlotPoints->20",
            "RegionFunction->Function({x,y,z},x^2+y^2<1)"}};
    for (String[] row : regions) {
      String cut = row[0] + "," + row[1];
      assertTrue(lines(cut + ")") < lines(cut + ",BoundaryStyle->Red)"),
          row[0] + " did not outline the edge its region cut");
    }
  }

  /**
   * {@code ComplexPlot3D} outlines the domain it drew.
   *
   * <p>
   * Without a region that is one closed line round the rectangle, which is what it always drew.
   * With one the rectangle has holes in it, so the outline has to follow the edge of what survived
   * instead - and a single closed line cannot describe that.
   */
  @Test
  public void theComplexSurfaceOutlinesWhatSurvived() {
    String base = "ComplexPlot3D(z,{z,-2-2*I,2+2*I},PlotPoints->20";
    assertEquals(1, lines(base + ")"), "the rim of the rectangle is one closed line");
    assertTrue(lines(base + ",RegionFunction->Function({z,f},Abs(z)<2))") > 1,
        "the edge of the region is followed segment by segment");
    assertEquals(0, lines(base + ",RegionFunction->Function({z,f},Abs(z)<2),BoundaryStyle->None)"),
        "None outlines nothing, region or no region");
  }

  /**
   * {@code BoundaryStyle} outlines what was drawn.
   *
   * <p>
   * With no region that is the rim of the plot rectangle; with one it is also the edge the region
   * cuts, which is why the outlined picture carries more segments than the plain rectangle does.
   */
  @Test
  public void boundaryStyleOutlinesTheRegionEdge() {
    String base = "ContourPlot(x*y,{x,0,3},{y,0,3},PlotPoints->10";
    int plain = lines(base + ")");
    int rim = lines(base + ",BoundaryStyle->Red)");
    assertTrue(rim > plain, "the rim of the rectangle is drawn");
    assertEquals(plain, lines(base + ",BoundaryStyle->Automatic)"), "no outline unless asked");
    assertEquals(plain, lines(base + ",BoundaryStyle->None)"));

    String region = base + ",RegionFunction->Function({x,y,z},x^2+y^2<4)";
    assertTrue(lines(region + ",BoundaryStyle->Red)") > lines(region + ")"),
        "the edge of the region is outlined too");

    String density = "DensityPlot(x*y,{x,0,3},{y,0,3},PlotPoints->10";
    assertTrue(lines(density + ",BoundaryStyle->Red)") > lines(density + ")"));
  }

  // -----------------------------------------------------------------------------------------
  // 3D plots
  // -----------------------------------------------------------------------------------------

  @Test
  public void theThreeDimensionalSurfacesAreCutToo() {
    String plot3D = "Plot3D(x+y,{x,-2,2},{y,-2,2},PlotPoints->20";
    assertTrue(vertices(plot3D + ",RegionFunction->Function({x,y,z},x^2+y^2<1))") //
        < vertices(plot3D + ")") / 2);

    String parametric = "ParametricPlot3D({Cos(u)*Cos(v),Sin(u)*Cos(v),Sin(v)},"
        + "{u,0,6},{v,-1,1},PlotPoints->20";
    assertTrue(vertices(parametric + ",RegionFunction->Function({x,y,z,u,v},z>0))") //
        < vertices(parametric + ")"));

    String spherical = "SphericalPlot3D(1,{t,0,3},{p,0,6},PlotPoints->12";
    assertTrue(vertices(spherical + ",RegionFunction->Function({x,y,z,t,p,r},z>0))") //
        < vertices(spherical + ")"));

    String revolution = "RevolutionPlot3D(Sqrt(t),{t,0,4},PlotPoints->12";
    assertTrue(vertices(revolution + ",RegionFunction->Function({x,y,z,t,th,r},x>0))") //
        < vertices(revolution + ")"));

    String contour3D = "ContourPlot3D(x^2+y^2+z^2==1,{x,-2,2},{y,-2,2},{z,-2,2},PlotPoints->12";
    assertTrue(polygonPoints(contour3D + ",RegionFunction->Function({x,y,z,f},z>0))") //
        < polygonPoints(contour3D + ")"));
  }

  /** A space curve is broken where the region ends, rather than jumped across. */
  @Test
  public void aSpaceCurveIsCutWhereTheRegionEnds() {
    String base = "ParametricPlot3D({Cos(t),Sin(t),t},{t,0,6},PlotPoints->30";
    assertEquals(30, linePoints(base + ")"));
    assertTrue(linePoints(base + ",RegionFunction->Function({x,y,z,u},z<3))") < 20,
        "only the part below z == 3 is drawn");
  }

  @Test
  public void theThreeDimensionalDataPlotsTakeTheSameRegion() {
    String surface = "ListPlot3D(Table(i*j,{i,1,8},{j,1,8})";
    assertEquals(64, vertices(surface + ")"));
    assertEquals(32, vertices(surface + ",RegionFunction->Function({x,y,z},x>4))"));

    String points = "ListPointPlot3D(Flatten(Table({i,j,i*j},{i,1,5},{j,1,5}),1)";
    assertEquals(25, vertices(points + ")"));
    assertEquals(15, vertices(points + ",RegionFunction->Function({x,y,z},x>2))"));

    String polylines = "ListLinePlot3D(Table(i*j,{i,1,5},{j,1,5})";
    assertEquals(25, linePoints(polylines + ")"));
    assertEquals(15, linePoints(polylines + ",RegionFunction->Function({x,y,z},y>2))"));

    String discrete = "DiscretePlot3D(i+j,{i,1,4},{j,1,4}";
    assertTrue(count("Length(Cases(" + discrete
        + ",RegionFunction->Function({x,y,z},x>2)),_Polygon|_Line|_Point,Infinity))") //
        < count("Length(Cases(" + discrete + "),_Polygon|_Line|_Point,Infinity))"));
  }

  // -----------------------------------------------------------------------------------------
  // Corner cases
  // -----------------------------------------------------------------------------------------

  /** The value of an expression that evaluates to one real number. */
  private static double number(String expression) {
    IExpr result = evaluator.eval(expression);
    double value = result.evalfNaN();
    assertTrue(Double.isFinite(value), expression + " is not a number: " + result);
    return value;
  }

  /**
   * The edge of a region is not quantised to the sampling grid.
   *
   * <p>
   * Masking a grid can only drop whole cells, which leaves a smooth region drawn as a staircase
   * with one step per sample. The cells the boundary crosses are cut along it instead, so the
   * drawing reaches the region's own edge rather than stopping at the last cell wholly inside it.
   * Both numbers below would be a whole cell out - and here that is a twentieth of the picture - if
   * the cells were being dropped rather than cut.
   */
  @Test
  public void theEdgeOfARegionIsNotQuantisedToTheSamplingGrid() {
    // an annulus with radii Sqrt(2) and 3, sampled every 0.3
    String annulus = "ContourPlot(x^2-y^2,{x,-3,3},{y,-3,3},PlotPoints->20,"
        + "RegionFunction->Function({x,y,z},2<x^2+y^2<9))";
    String corners = "Norm/@Flatten(Cases(" + annulus + ",Polygon(l_):>l,Infinity),1)";
    assertEquals(Math.sqrt(2.0), number("Min(" + corners + ")"), 0.02,
        "the shading reaches the inner edge of the annulus");
    assertEquals(3.0, number("Max(" + corners + ")"), 0.02, "and stops at the outer one");

    // the same for a surface, where nothing else can put a vertex between two samples
    String disc = "Plot3D(x+y,{x,-3,3},{y,-3,3},PlotPoints->21,"
        + "RegionFunction->Function({x,y,z},x^2+y^2<4))";
    String vertices = "Cases(" + disc + ",GraphicsComplex(vv_,__):>vv,Infinity)[[1]]";
    assertEquals(2.0, number("Max(Norm/@(Most/@" + vertices + "))"), 0.02,
        "the surface reaches the edge of the disc");
    assertTrue(
        number("Count(" + vertices
            + ",{q_,_,_}/;Min(Abs(Mod(q+3,0.3)),Abs(Mod(q+3,0.3)-0.3))>0.01)") > 0,
        "some vertices lie between two samples, which is where the region ends");
  }

  /**
   * A raster cannot be cut, so the cells the boundary crosses are drawn as polygons over it.
   *
   * <p>
   * A plot without a region draws none of them: every cell is a whole cell, and the raster paints
   * all of them.
   */
  @Test
  public void aDensityRasterGrowsPolygonsAlongTheRegionEdge() {
    String base = "DensityPlot(Sin(x)*Cos(y),{x,-3,3},{y,-3,3},PlotPoints->20";
    assertEquals(0, count("Length(Cases(" + base + "),_Polygon,Infinity))"));
    assertTrue(
        count("Length(Cases(" + base
            + ",RegionFunction->Function({x,y},x^2+y^2<6)),_Polygon,Infinity))") > 0,
        "the cells the edge runs through are drawn cut to it");
  }

  /**
   * A predicate may name fewer parameters than the plot supplies.
   *
   * <p>
   * This is what the whole design rests on: every family hands over the tuple documents for it, and
   * {@code Function} discards the arguments it did not declare. If that ever stopped being true,
   * every predicate written the short way would silently reject every point.
   */
  @Test
  public void aPredicateMayIgnoreTheArgumentsItDoesNotName() {
    String base = "ContourPlot(x*y,{x,0,3},{y,0,3},PlotPoints->10";
    int byThree = polygonPoints(base + ",RegionFunction->Function({x,y,z},x>1))");
    int byTwo = polygonPoints(base + ",RegionFunction->Function({x,y},x>1))");
    assertEquals(byThree, byTwo, "the third argument is simply not looked at");
    assertEquals(byThree, polygonPoints(base + ",RegionFunction->(#1>1&))"),
        "a slot function ignores the arguments its body does not mention");
  }

  /** A region that keeps nothing draws nothing, and does not fail. */
  @Test
  public void aRegionThatKeepsNothingIsNotAnError() {
    String[] plots = { //
        "Plot(Sin(x),{x,0,6},RegionFunction->Function({x,y},y>1000))",
        "PolarPlot(1+Cos(t),{t,0,6.28},RegionFunction->Function({x,y,th,r},r>1000))",
        "ParametricPlot({Cos(t),Sin(t)},{t,0,6.3},RegionFunction->Function({x,y,t},y>1000))",
        "ContourPlot(x*y,{x,0,3},{y,0,3},PlotPoints->6,"
            + "RegionFunction->Function({x,y,z},x>1000))",
        "DensityPlot(x*y,{x,0,3},{y,0,3},PlotPoints->6,"
            + "RegionFunction->Function({x,y,z},x>1000))",
        "ListContourPlot(Table(x*y,{x,1,6},{y,1,6}),RegionFunction->Function({x,y,z},x>1000))",
        "ListDensityPlot(Table(x*y,{x,1,6},{y,1,6}),RegionFunction->Function({x,y,z},x>1000))",
        "Plot3D(x+y,{x,0,1},{y,0,1},PlotPoints->6,RegionFunction->Function({x,y,z},x>1000))",
        "ListPlot3D(Table(i*j,{i,1,4},{j,1,4}),RegionFunction->Function({x,y,z},x>1000))",
        "ListLinePlot3D(Table(i*j,{i,1,4},{j,1,4}),RegionFunction->Function({x,y,z},x>1000))",
        "ComplexPlot(z,{z,-1-I,1+I},PlotPoints->8,RegionFunction->Function({z,f},Abs(z)>1000))",
        "ComplexPlot3D(z,{z,-1-I,1+I},PlotPoints->8,"
            + "RegionFunction->Function({z,f},Abs(z)>1000))"};
    for (String plot : plots) {
      IExpr result = evaluator.eval(plot);
      assertTrue(result.isPresent(), plot + " threw instead of drawing nothing");
    }
  }

  /** A predicate that does not answer with a boolean leaves the point out rather than failing. */
  @Test
  public void aPredicateThatDoesNotDecideIsNotAnError() {
    assertTrue(evaluator.eval("Head(Plot(Sin(x),{x,0,6},RegionFunction->Function({x,y},foo)))")
        .toString().equals("Graphics"));
    assertTrue(evaluator.eval("Head(Plot(Sin(x),{x,0,6},RegionFunction->Function({x,y},1/0>0)))") //
        .toString().equals("Graphics"));
  }

  /**
   * A grid cell with a corner that has no value is left out entirely.
   *
   * <p>
   * {@code NaN >= level} is false, so an undefined corner used to be classified as lying below the
   * level; the cell was then shaded up to that corner and the interpolation across the undefined
   * edge produced polygon vertices that were not numbers at all. That is the machinery a region
   * hole travels through, so it has to be right whether or not a region was asked for.
   */
  @Test
  public void anUndefinedCornerDoesNotProduceCoordinatesThatAreNotNumbers() {
    for (String plot : new String[] { //
        "ContourPlot(Sqrt(x*y),{x,-1,1},{y,-1,1},PlotPoints->6)",
        "ContourPlot(Log(x*y),{x,-1,1},{y,-1,1},PlotPoints->6)"}) {
      IExpr result = evaluator.eval(plot);
      assertTrue(!result.toString().contains("NaN"), plot + " drew a point that is not a number");
    }
  }
}
