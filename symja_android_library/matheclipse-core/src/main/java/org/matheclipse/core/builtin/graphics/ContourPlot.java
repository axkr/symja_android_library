package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.RegionClip;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating 2D contour plots with ContourShading, ContourStyle, and Scaling options.
 * 
 */
public class ContourPlot extends ListPlot {

  /**
   * The data is drawn as one field rather than point by point, so no wrapper is read here; the
   * label goes over the whole picture instead.
   */
  @Override
  protected boolean readsArgumentWrapper() {
    return false;
  }

  public ContourPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // the shape tests below read the data through any display wrapper; `wrappedAST` keeps the
    // wrapper, so the label can still be put over the finished picture
    final IAST wrappedAST = ast;
    if (ast.size() > 1) {
      IExpr unwrapped = PlotWrapper.strip(ast.arg1());
      if (unwrapped != ast.arg1()) {
        ast = ast.setAtCopy(1, unwrapped);
      }
    }
    if (argSize < 3) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    // ContourLines draws the level curves themselves; without them only the shading is left.
    // ContourLabels writes each level's value onto its curve.
    boolean contourLines =
        !GraphicsOptions.optionValue(originalAST, S.ContourLines, S.True).isFalse();
    IExpr contourLabelsOption = GraphicsOptions.optionValue(originalAST, S.ContourLabels, S.None);
    boolean contourLabels = contourLabelsOption.isTrue() || contourLabelsOption.isAutomatic();

    int plotPoints = 25;
    int maxRecursion = -1;
    int numberOfContours = 10;
    IExpr contourStyle = S.Automatic;
    IExpr contourShading = S.Automatic;
    boolean colorFunctionScaling = true;
    IExpr colorFunctionOpt = S.Automatic;
    IExpr meshOpt = S.None;

    // the options array holds resolved values, not the rules the caller wrote,
    // so the option rules are read back off the original call
    for (IExpr opt : originalAST) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();

        if (key.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) key).ordinal()) {
            case ID.Contours:
              if (val.isInteger())
                numberOfContours = val.toIntDefault(10);
              break;
            case ID.ContourStyle:
              contourStyle = val;
              break;
            case ID.ContourShading:
              contourShading = val;
              break;
            case ID.ColorFunctionScaling:
              if (val.isFalse())
                colorFunctionScaling = false;
              break;
            case ID.PlotPoints:
              plotPoints = val.toIntDefault(25);
              break;
            case ID.MaxRecursion:
              maxRecursion = val.toIntDefault(-1);
              break;
            case ID.ColorFunction:
              colorFunctionOpt = val;
              break;
            case ID.Mesh:
              meshOpt = val;
              break;
          }
        }
      }
    }

    // The contours are traced on a fixed grid rather than refined adaptively, so MaxRecursion is
    // honoured as what it is for: each level doubles the sampling resolution, which is what makes
    // the traced contours smoother.
    if (maxRecursion >= 0) {
      plotPoints = Math.min(400, plotPoints * (1 << Math.min(maxRecursion, 4)));
    }

    IExpr functionArg = ast.arg1();
    IExpr xIter = ast.arg2();
    IExpr yIter = ast.arg3();

    double[] xRange = parseRange(xIter, engine);
    if (xRange == null || !xIter.isList() || !xIter.first().isSymbol()) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.ContourPlot, "pllim", F.list(xIter), engine);
    }
    double[] yRange = parseRange(yIter, engine);
    if (yRange == null || !yIter.isList() || !yIter.first().isSymbol()) {
      // Range specification `1` is not of the form {y, ymin, ymax}.
      return Errors.printMessage(S.ContourPlot, "pllim", F.list(yIter), engine);
    }

    graphicsOptions.setBoundingBox(new double[] {xRange[0], xRange[1], yRange[0], yRange[1]});

    ISymbol xVar = (ISymbol) ((IAST) xIter).arg1();
    ISymbol yVar = (ISymbol) ((IAST) yIter).arg1();

    IASTAppendable primitives = F.ListAlloc();

    RegionFunctionFilter region = RegionFunctionFilter.of(
        GraphicsOptions.optionValue(originalAST, S.RegionFunction, S.Automatic), engine);
    IExpr boundaryStyle = GraphicsOptions.optionValue(originalAST, S.BoundaryStyle, S.Automatic);

    if (functionArg.isList()) {
      // Multiple functions/equations
      IAST list = (IAST) functionArg;
      int count = 0;
      for (int i = 1; i < list.size(); i++) {
        IExpr func = list.get(i);

        // Determine style for this specific curve
        IExpr style = contourStyle;
        if (style.isList()) {
          style = GraphicsOptions.getPlotStyle(style, count);
        } else if (style.isAutomatic()) {
          // If Automatic, cycle colors for multiple equations
          style = GraphicsOptions.plotStyleColorExpr(count, F.NIL);
        }

        generateContours(primitives, func, xRange, yRange, xVar, yVar, plotPoints, numberOfContours,
            style, contourShading, colorFunctionScaling, colorFunctionOpt, engine, true,
            contourLines, contourLabels, region, boundaryStyle);
        count++;
      }
    } else {
      // Single function/equation
      generateContours(primitives, functionArg, xRange, yRange, xVar, yVar, plotPoints,
          numberOfContours, contourStyle, contourShading, colorFunctionScaling, colorFunctionOpt,
          engine, false, contourLines, contourLabels, region, boundaryStyle);
    }

    IExpr meshLines = GraphicsOptions.meshGrid(meshOpt, xRange[0], yRange[0], xRange[1], yRange[1],
        plotPoints, plotPoints);
    if (meshLines.isPresent()) {
      primitives.append(meshLines);
    }

    return createGraphicsFunction(primitives, graphicsOptions, wrappedAST);
  }

  private void generateContours(IASTAppendable primitives, IExpr function, double[] xRange,
      double[] yRange, ISymbol xVar, ISymbol yVar, int plotPoints, int numberOfContours,
      IExpr contourStyle, IExpr contourShading, boolean colorFunctionScaling,
      IExpr colorFunctionOpt, EvalEngine engine, boolean isMulti, boolean contourLines,
      boolean contourLabels, RegionFunctionFilter region, IExpr boundaryStyle) {
    boolean isEquation = false;
    if (function.isAST(S.Equal, 3)) {
      isEquation = true;
      function = F.Subtract(function.first(), function.second());
    }

    // Default shading to None for equations unless specified
    if (isEquation && contourShading == S.Automatic) {
      contourShading = S.None;
    }

    int gridX = plotPoints;
    int gridY = plotPoints;
    double[][] zGrid = new double[gridX + 1][gridY + 1];
    double stepX = (xRange[1] - xRange[0]) / gridX;
    double stepY = (yRange[1] - yRange[0]) / gridY;

    double minZ = Double.MAX_VALUE;
    double maxZ = -Double.MAX_VALUE;

    // 1. Compute Grid
    boolean[][] defined = new boolean[gridX + 1][gridY + 1];
    boolean[][] inside = new boolean[gridX + 1][gridY + 1];
    for (int i = 0; i <= gridX; i++) {
      double xVal = xRange[0] + i * stepX;
      for (int j = 0; j <= gridY; j++) {
        double yVal = yRange[0] + j * stepY;
        IExpr valExpr =
            function.replaceAll(F.List(F.Rule(xVar, F.num(xVal)), F.Rule(yVar, F.num(yVal))));

        double z = Double.NaN;
        try {
          IExpr result = engine.evaluate(valExpr);
          z = result.evalDouble();
        } catch (Exception e) {
          z = Double.NaN;
        }

        zGrid[i][j] = z;
        // The value is kept even where the region rejects it: a cell the edge of the region runs
        // through is clipped rather than dropped, and clipping interpolates across the cell, so it
        // needs the value at every corner. Whether a corner is part of the picture is a separate
        // question, and the answer to it is what the levels and the outline are built from.
        inside[i][j] = Double.isFinite(z) && (region == null || region.accepts(xVal, yVal, z));
        defined[i][j] = inside[i][j];
        if (inside[i][j]) {
          if (z < minZ)
            minZ = z;
          if (z > maxZ)
            maxZ = z;
        }
      }
    }

    if (minZ == Double.MAX_VALUE)
      return;

    // one argument, the average of the two levels the band lies between, which is what the
    // reference means by "the average of the scaled values of f for each pair of successive
    // contour levels" - the scaling is a straight line, so averaging before it is the same thing
    PlotColorFunction colorMap = PlotColorFunction
        .of(PlotColorFunction.Family.FIELD_2D, colorFunctionOpt, F.bool(colorFunctionScaling),
            S.ContourPlot, engine)
        .range(1, minZ, maxZ).sink(PlotColorFunction.Sink.FLAT).fallback(this::getShadingColor)
        .build();

    // 2. Determine Levels
    double[] levels;
    if (isEquation) {
      levels = new double[] {0.0};
    } else {
      levels = new double[numberOfContours];
      double levelStep = (maxZ - minZ) / (numberOfContours + 1);
      for (int k = 0; k < numberOfContours; k++) {
        levels[k] = minZ + (k + 1) * levelStep;
      }
    }

    // The cells the edge of the region runs through, and the line it follows through each. A cell
    // that is wholly in or wholly out is not listed here: it is drawn, or not, exactly as before.
    double[][][][] cellClip = regionClips(inside, region, zGrid, xRange[0], yRange[0], stepX, stepY,
        gridX, gridY);

    // 3. Shading (Polygons)
    if (contourShading != S.None && !contourShading.isFalse()) {
      for (int k = -1; k < levels.length; k++) {
        double level = (k == -1) ? minZ - 1.0 : levels[k];

        IExpr color;
        if (contourShading.isList()) {
          color = GraphicsOptions.getPlotStyle(contourShading, k + 1);
        } else {
          double lower = (k == -1) ? minZ : levels[k];
          double upper = (k == levels.length - 1) ? maxZ : levels[k + 1];
          double bandZ = (lower + upper) * 0.5;
          // an equation has no range of values to place a band within, so it is left unscaled
          color = colorMap != null && !isEquation ? colorMap.color(bandZ)
              : getShadingColor((colorFunctionScaling && !isEquation)
                  ? (bandZ - minZ) / (maxZ - minZ)
                  : bandZ);
        }

        IASTAppendable polygons = F.ListAlloc();
        for (int i = 0; i < gridX; i++) {
          for (int j = 0; j < gridY; j++) {
            double x = xRange[0] + i * stepX;
            double y = yRange[0] + j * stepY;
            if (cellClip != null && !inside[i][j] && !inside[i + 1][j] && !inside[i + 1][j + 1]
                && !inside[i][j + 1]) {
              continue;
            }
            double[][] clip = cellClip == null ? null : cellClip[i][j];
            if (cellClip != null && clip == null && !(inside[i][j] && inside[i + 1][j]
                && inside[i + 1][j + 1] && inside[i][j + 1])) {
              // partly inside, but the region crosses the cell more than once: no single line
              // describes it, so it is left out as it was before there was any clipping at all
              continue;
            }
            final double c00 = zGrid[i][j];
            final double c10 = zGrid[i + 1][j];
            final double c11 = zGrid[i + 1][j + 1];
            final double c01 = zGrid[i][j + 1];
            final double cx = x;
            final double cy = y;
            final double cLevel = level;
            clipped(polygons, clip, S.Polygon,
                out -> processCellPolygon(out, cx, cy, stepX, stepY, c00, c10, c11, c01, cLevel));
          }
        }

        if (polygons.argSize() > 0) {
          IASTAppendable group = F.ListAlloc();
          group.append(F.EdgeForm(S.None));
          group.append(color);
          for (IExpr poly : polygons)
            group.append(poly);
          primitives.append(group);
        }
      }
    }

    // 4. Contour Lines
    if (contourLines && contourStyle != S.None && !contourStyle.isFalse()) {
      for (int k = 0; k < levels.length; k++) {
        double level = levels[k];
        IASTAppendable lineSegments = F.ListAlloc();

        IExpr currentStyle = contourStyle;
        if (currentStyle == S.Automatic) {
          // Single plot default: Gray
          // Multi plot default: Passed in via argument (handled in evaluate loop)
          currentStyle = isMulti ? contourStyle : (isEquation ? S.Black : F.GrayLevel(0.5));
        } else if (currentStyle.isList() && !isMulti) {
          // If it's a list of styles for LEVELS (not equations), pick per level
          currentStyle = GraphicsOptions.getPlotStyle(currentStyle, k);
        }
        // Note: If isMulti is true, contourStyle passed here is already the specific color for
        // this equation

        for (int i = 0; i < gridX; i++) {
          for (int j = 0; j < gridY; j++) {
            double x = xRange[0] + i * stepX;
            double y = yRange[0] + j * stepY;
            if (cellClip != null && !inside[i][j] && !inside[i + 1][j] && !inside[i + 1][j + 1]
                && !inside[i][j + 1]) {
              continue;
            }
            double[][] clip = cellClip == null ? null : cellClip[i][j];
            if (cellClip != null && clip == null && !(inside[i][j] && inside[i + 1][j]
                && inside[i + 1][j + 1] && inside[i][j + 1])) {
              continue;
            }
            final double c00 = zGrid[i][j];
            final double c10 = zGrid[i + 1][j];
            final double c11 = zGrid[i + 1][j + 1];
            final double c01 = zGrid[i][j + 1];
            final double cx = x;
            final double cy = y;
            final double cLevel = level;
            clipped(lineSegments, clip, S.Line,
                out -> processCellLine(out, cx, cy, stepX, stepY, c00, c10, c11, c01, cLevel));
          }
        }

        if (lineSegments.argSize() > 0) {
          IASTAppendable group = F.ListAlloc();
          if (currentStyle.isList())
            group.append(F.Directive(currentStyle));
          else
            group.append(currentStyle);

          for (IExpr line : lineSegments)
            group.append(line);
          primitives.append(group);
          if (contourLabels) {
            IExpr label = levelLabel(lineSegments, level);
            if (label.isPresent()) {
              primitives.append(label);
            }
          }
        }
      }
    }

    appendBoundary(primitives, defined, xRange[0], yRange[0], stepX, stepY, boundaryStyle);
  }

  /**
   * The line the edge of the region follows through each cell it runs through.
   *
   * <p>
   * Masking a grid can only drop whole cells, which turns a smooth region boundary into a staircase
   * with one step per sample. Clipping each straddled cell to the line between the two points where
   * the region crosses its edges follows the boundary instead. Inside one cell a smooth curve is
   * very nearly straight, so at any density worth plotting at the difference is invisible.
   *
   * <p>
   * The value at a crossing is interpolated from the four corners rather than evaluated again: the
   * boundary is already being approximated to first order inside the cell, and a region written on
   * the value rather than on the position would otherwise cost a fresh evaluation of the plotted
   * function at every step of every bisection.
   *
   * @return {@code null} when there is no region at all, otherwise an entry per cell which is
   *         {@code null} unless that cell needs clipping
   */
  static double[][][][] regionClips(boolean[][] inside, RegionFunctionFilter region,
      double[][] zGrid, double x0, double y0, double stepX, double stepY, int gridX, int gridY) {
    if (region == null) {
      return null;
    }
    double[][][][] clips = new double[gridX][gridY][][];
    for (int i = 0; i < gridX; i++) {
      double x = x0 + i * stepX;
      for (int j = 0; j < gridY; j++) {
        double y = y0 + j * stepY;
        boolean[] corners = {inside[i][j], inside[i + 1][j], inside[i + 1][j + 1],
            inside[i][j + 1]};
        if (corners[0] == corners[1] && corners[1] == corners[2] && corners[2] == corners[3]) {
          continue; // wholly in or wholly out; nothing to cut
        }
        final double v00 = zGrid[i][j];
        final double v10 = zGrid[i + 1][j];
        final double v11 = zGrid[i + 1][j + 1];
        final double v01 = zGrid[i][j + 1];
        if (!Double.isFinite(v00) || !Double.isFinite(v10) || !Double.isFinite(v11)
            || !Double.isFinite(v01)) {
          continue; // a corner without a value: the cell is dropped anyway
        }
        RegionClip.Membership member = (px, py) -> {
          double u = (px - x) / stepX;
          double v = (py - y) / stepY;
          double value = v00 * (1 - u) * (1 - v) + v10 * u * (1 - v) + v11 * u * v
              + v01 * (1 - u) * v;
          return region.accepts(px, py, value);
        };
        double[][] cellCorners = {{x, y}, {x + stepX, y}, {x + stepX, y + stepY}, {x, y + stepY}};
        clips[i][j] = RegionClip.cellBoundary(member, cellCorners, corners);
      }
    }
    return clips;
  }

  /**
   * Appends what one cell draws, cut to the region when its edge runs through that cell.
   *
   * <p>
   * The marching squares routines write straight into the output, so a clipped cell is drawn into a
   * scratch list first and each primitive is then cut. Both the shaded bands and the contour lines
   * are pieces of a cell that a straight line already cut off, so both stay convex and one pass of
   * the clipper is enough.
   *
   * @param clip {@code null} to append what the cell draws unchanged
   * @param head {@code Polygon} or {@code Line}, which is what the cell draws
   */
  static void clipped(IASTAppendable out, double[][] clip, ISymbol head,
      java.util.function.Consumer<IASTAppendable> draw) {
    if (clip == null) {
      draw.accept(out);
      return;
    }
    IASTAppendable raw = F.ListAlloc(4);
    draw.accept(raw);
    for (IExpr primitive : raw) {
      if (!primitive.isAST1() || !primitive.first().isList()) {
        continue;
      }
      IAST points = (IAST) primitive.first();
      double[][] corners = new double[points.argSize()][];
      boolean readable = true;
      for (int k = 1; k <= points.argSize(); k++) {
        IExpr point = points.get(k);
        if (!point.isList2()) {
          readable = false;
          break;
        }
        corners[k - 1] =
            new double[] {point.first().evalfNaN(), point.second().evalfNaN()};
      }
      if (!readable) {
        continue;
      }
      double[][] cut = head == S.Line && corners.length == 2
          ? RegionClip.clipSegment(corners[0], corners[1], clip[0], clip[1], clip[2][0], clip[2][1])
          : RegionClip.clipPolygon(corners, clip[0], clip[1], clip[2][0], clip[2][1]);
      if (cut == null) {
        continue;
      }
      IASTAppendable list = F.ListAlloc(cut.length);
      for (double[] point : cut) {
        list.append(F.List(F.num(point[0]), F.num(point[1])));
      }
      out.append(F.unaryAST1(head, list));
    }
  }

  /**
   * Draws the outline of what was plotted, when {@code BoundaryStyle} asks for one.
   *
   * <p>
   * That is the rim of the sampled rectangle, and the rim of every hole in it - so it is also the
   * edge a {@code RegionFunction} cuts, without the option having to know a region was given.
   */
  static void appendBoundary(IASTAppendable primitives, boolean[][] defined, double x0, double y0,
      double stepX, double stepY, IExpr boundaryStyle) {
    if (boundaryStyle == null || !boundaryStyle.isPresent() || boundaryStyle.isAutomatic()
        || boundaryStyle.isNone()) {
      return;
    }
    IAST boundary = GraphicsOptions.gridBoundary(defined, x0, y0, stepX, stepY);
    if (boundary.argSize() == 0) {
      return;
    }
    IASTAppendable group = F.ListAlloc(boundary.size() + 1);
    group.append(boundaryStyle);
    group.appendArgs(boundary);
    primitives.append(group);
  }

  /**
   * The value of a contour, written where that contour runs.
   *
   * <p>
   * The label goes on the middle segment of the level, which keeps it away from the edges of the
   * picture where a contour is most likely to be clipped.
   *
   * @param lineSegments the {@code Line} primitives making up one level
   * @param level the value the contour stands for
   * @return the label, or {@link F#NIL} when the level has no segment to put it on
   */
  private static IExpr levelLabel(IAST lineSegments, double level) {
    if (lineSegments.argSize() < 1) {
      return F.NIL;
    }
    IExpr segment = lineSegments.get(1 + lineSegments.argSize() / 2);
    if (!segment.isAST(S.Line, 2) || !segment.first().isList()) {
      return F.NIL;
    }
    IAST points = (IAST) segment.first();
    if (points.argSize() < 2 || !points.arg1().isList() || !points.arg2().isList()) {
      return F.NIL;
    }
    double x =
        (((IAST) points.arg1()).arg1().evalfNaN() + ((IAST) points.arg2()).arg1().evalfNaN()) / 2.0;
    double y =
        (((IAST) points.arg1()).arg2().evalfNaN() + ((IAST) points.arg2()).arg2().evalfNaN()) / 2.0;
    if (!Double.isFinite(x) || !Double.isFinite(y)) {
      return F.NIL;
    }
    return F.List(S.Black, F.Text(F.num(level), F.List(F.num(x), F.num(y))));
  }

  @Override
  protected IExpr createGraphicsFunction(IAST primitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    graphicsOptions.addPadding();
    IASTAppendable result = F.Graphics(labelledContent(primitives, plotAST));
    result.appendArgs(graphicsOptions.getListOfRules());
    return result;
  }

  private IExpr getShadingColor(double t) {
    double val = t;
    if (val < 0 || val > 1.0) {
      val = val - Math.floor(val);
    }
    return F.Hue(F.num(0.66 * (1.0 - val)));
  }

  // --- Marching Squares: Polygons ---

  private void processCellPolygon(IASTAppendable out, double x, double y, double dx, double dy,
      double v0, double v1, double v2, double v3, double level) {
    if (!Double.isFinite(v0) || !Double.isFinite(v1) || !Double.isFinite(v2)
        || !Double.isFinite(v3)) {
      // A corner without a value is not "below the level": comparing NaN with >= says false, which
      // would classify the cell as filled up to that corner and then interpolate across the edge,
      // producing a polygon with NaN coordinates. An incomplete cell has no interior to shade -
      // that is what leaves a hole where a RegionFunction cuts, or where the function has no value.
      return;
    }
    int index = 0;
    if (v0 >= level)
      index |= 1;
    if (v1 >= level)
      index |= 2;
    if (v2 >= level)
      index |= 4;
    if (v3 >= level)
      index |= 8;

    if (index == 0)
      return;
    if (index == 15) {
      out.append(F.Polygon(F.List(F.List(F.num(x), F.num(y)), F.List(F.num(x + dx), F.num(y)),
          F.List(F.num(x + dx), F.num(y + dy)), F.List(F.num(x), F.num(y + dy)))));
      return;
    }

    double[][] pts = new double[4][2];
    if ((index & 1) != ((index >> 1) & 1))
      pts[0] = interp(x, y, dx, dy, 0, v0, v1, level);
    if ((index & 2) != ((index >> 2) & 1))
      pts[1] = interp(x, y, dx, dy, 1, v1, v2, level);
    if ((index & 4) != ((index >> 3) & 1))
      pts[2] = interp(x, y, dx, dy, 2, v3, v2, level);
    if ((index & 8) != ((index) & 1))
      pts[3] = interp(x, y, dx, dy, 3, v0, v3, level);

    IASTAppendable polyPts = F.ListAlloc();

    if (index == 1) {
      add(polyPts, pts[3]);
      add(polyPts, x, y);
      add(polyPts, pts[0]);
    } else if (index == 2) {
      add(polyPts, pts[0]);
      add(polyPts, x + dx, y);
      add(polyPts, pts[1]);
    } else if (index == 4) {
      add(polyPts, pts[1]);
      add(polyPts, x + dx, y + dy);
      add(polyPts, pts[2]);
    } else if (index == 8) {
      add(polyPts, pts[2]);
      add(polyPts, x, y + dy);
      add(polyPts, pts[3]);
    } else if (index == 3) {
      add(polyPts, pts[3]);
      add(polyPts, x, y);
      add(polyPts, x + dx, y);
      add(polyPts, pts[1]);
    } else if (index == 6) {
      add(polyPts, pts[0]);
      add(polyPts, x + dx, y);
      add(polyPts, x + dx, y + dy);
      add(polyPts, pts[2]);
    } else if (index == 12) {
      add(polyPts, pts[1]);
      add(polyPts, x + dx, y + dy);
      add(polyPts, x, y + dy);
      add(polyPts, pts[3]);
    } else if (index == 9) {
      add(polyPts, pts[2]);
      add(polyPts, x, y + dy);
      add(polyPts, x, y);
      add(polyPts, pts[0]);
    } else if (index == 14) {
      add(polyPts, pts[0]);
      add(polyPts, pts[3]);
      add(polyPts, x, y + dy);
      add(polyPts, x + dx, y + dy);
      add(polyPts, x + dx, y);
    } else if (index == 13) {
      add(polyPts, pts[1]);
      add(polyPts, pts[0]);
      add(polyPts, x, y);
      add(polyPts, x, y + dy);
      add(polyPts, x + dx, y + dy);
    } else if (index == 11) {
      add(polyPts, pts[2]);
      add(polyPts, pts[1]);
      add(polyPts, x + dx, y);
      add(polyPts, x, y);
      add(polyPts, x, y + dy);
    } else if (index == 7) {
      add(polyPts, pts[3]);
      add(polyPts, pts[2]);
      add(polyPts, x + dx, y + dy);
      add(polyPts, x + dx, y);
      add(polyPts, x, y);
    } else if (index == 5) { // Saddle
      IASTAppendable t1 = F.ListAlloc();
      add(t1, pts[3]);
      add(t1, x, y);
      add(t1, pts[0]);
      out.append(F.Polygon(t1));
      IASTAppendable t2 = F.ListAlloc();
      add(t2, pts[1]);
      add(t2, x + dx, y + dy);
      add(t2, pts[2]);
      out.append(F.Polygon(t2));
      return;
    } else if (index == 10) { // Saddle
      IASTAppendable t1 = F.ListAlloc();
      add(t1, pts[0]);
      add(t1, x + dx, y);
      add(t1, pts[1]);
      out.append(F.Polygon(t1));
      IASTAppendable t2 = F.ListAlloc();
      add(t2, pts[2]);
      add(t2, x, y + dy);
      add(t2, pts[3]);
      out.append(F.Polygon(t2));
      return;
    }

    if (polyPts.argSize() > 0) {
      out.append(F.Polygon(polyPts));
    }
  }

  private void add(IASTAppendable list, double[] p) {
    list.append(F.List(F.num(p[0]), F.num(p[1])));
  }

  private void add(IASTAppendable list, double x, double y) {
    list.append(F.List(F.num(x), F.num(y)));
  }

  // --- Marching Squares: Lines ---

  private void processCellLine(IASTAppendable out, double x, double y, double dx, double dy,
      double v0, double v1, double v2, double v3, double level) {
    if (!Double.isFinite(v0) || !Double.isFinite(v1) || !Double.isFinite(v2)
        || !Double.isFinite(v3)) {
      // an incomplete cell has no contour to trace; see processCellPolygon
      return;
    }

    int index = 0;
    if (v0 >= level)
      index |= 1;
    if (v1 >= level)
      index |= 2;
    if (v2 >= level)
      index |= 4;
    if (v3 >= level)
      index |= 8;

    int[] edges = ISO_LINES[index];
    for (int i = 0; i < edges.length; i += 2) {
      int e1 = edges[i];
      int e2 = edges[i + 1];
      if (e1 == -1)
        break;
      double[] p1 = interp(x, y, dx, dy, e1, v0, v1, v2, v3, level);
      double[] p2 = interp(x, y, dx, dy, e2, v0, v1, v2, v3, level);
      out.append(
          F.Line(F.List(F.List(F.num(p1[0]), F.num(p1[1])), F.List(F.num(p2[0]), F.num(p2[1])))));
    }
  }

  private double[] interp(double x, double y, double dx, double dy, int edge, double v0, double v1,
      double v2, double v3, double level) {
    return interp(x, y, dx, dy, edge, getVal(edge, 0, v0, v1, v2, v3),
        getVal(edge, 1, v0, v1, v2, v3), level);
  }

  private double getVal(int edge, int endpoint, double v0, double v1, double v2, double v3) {
    if (edge == 0)
      return (endpoint == 0) ? v0 : v1;
    if (edge == 1)
      return (endpoint == 0) ? v1 : v2;
    if (edge == 2)
      return (endpoint == 0) ? v3 : v2;
    if (edge == 3)
      return (endpoint == 0) ? v0 : v3;
    return 0;
  }

  private double[] interp(double x, double y, double dx, double dy, int edge, double valA,
      double valB, double level) {
    double mu = (level - valA) / (valB - valA);
    if (edge == 0)
      return new double[] {x + mu * dx, y};
    if (edge == 1)
      return new double[] {x + dx, y + mu * dy};
    if (edge == 2)
      return new double[] {x + mu * dx, y + dy};
    if (edge == 3)
      return new double[] {x, y + mu * dy};
    return new double[] {x, y};
  }

  private static final int[][] ISO_LINES = {{-1, -1, -1, -1}, {3, 0, -1, -1}, {0, 1, -1, -1},
      {3, 1, -1, -1}, {1, 2, -1, -1}, {3, 0, 1, 2}, {0, 2, -1, -1}, {3, 2, -1, -1}, {2, 3, -1, -1},
      {2, 0, -1, -1}, {0, 1, 2, 3}, {2, 1, -1, -1}, {1, 3, -1, -1}, {1, 0, -1, -1}, {0, 3, -1, -1},
      {-1, -1, -1, -1}};

  private double[] parseRange(IExpr iter, EvalEngine engine) {
    if (iter.isList() && iter.argSize() >= 3) {
      try {
        double min = engine.evalDouble(((IAST) iter).arg2());
        double max = engine.evalDouble(((IAST) iter).arg3());
        return new double[] {min, max};
      } catch (Exception e) {
      }
    }
    return null;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] optionValues = GraphicsOptions.contourPlotDefaultOptionValues(false, false);
    optionValues[GraphicsOptions.X_AXES] = S.False;
    optionValues[GraphicsOptions.X_FRAME] = S.True;
    optionValues[GraphicsOptions.X_ASPECTRATIO] = F.C1;
    GraphicsOptions.OptionSet optionSet =
        GraphicsOptions.contourExtras(new GraphicsOptions.OptionSet()
            .add(GraphicsOptions.contourPlotDefaultOptionKeys(), optionValues));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
    // setOptions(newSymbol, //
    // new IBuiltInSymbol[] {S.ContourShading, S.ColorFunctionScaling, S.ContourStyle,
    // S.AspectRatio, S.Frame, S.Axes}, //
    // new IExpr[] {S.Automatic, S.True, S.Automatic, F.C1, S.True, S.False} //
    // );
  }
}
