package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.external.fastutil.ints.IntArrayList;
import org.matheclipse.external.fastutil.longs.LongOpenHashSet;

/**
 * Functions for the mesh region objects <code>BoundaryMeshRegion</code> and
 * <code>MeshRegion</code>.
 *
 * <p>
 * A boundary mesh region is stored as the inert expression
 *
 * <pre>
 * BoundaryMeshRegion({p1, p2,...}, {cell1({i11,...}), cell2({i21,...}),...}, options...)
 * </pre>
 *
 * where each <code>celli</code> is one of {@link S#Point}, {@link S#Line} or {@link S#Polygon} and
 * the integers index into the coordinate list. A cell head may either contain a single index list
 * (one cell) or a list of index lists (several cells of the same kind), which is the compact form
 * <code>ConvexHullMesh</code> generates.
 */
public class MeshFunctions {

  /** Geometric dimension of a cell head which isn't a mesh cell at all. */
  public static final int NO_CELL = -1;

  private static class Initializer {

    private static void init() {
      S.BoundaryMeshRegion.setEvaluator(new BoundaryMeshRegion());
      S.BoundaryMeshRegionQ.setEvaluator(new BoundaryMeshRegionQ());
      S.MeshRegionQ.setEvaluator(new MeshRegionQ());
      S.MeshCellCount.setEvaluator(new MeshCellCount());
      S.MeshCells.setEvaluator(new MeshCells());
      S.MeshCoordinates.setEvaluator(new MeshCoordinates());
      S.MeshPrimitives.setEvaluator(new MeshPrimitives());
      S.ArrayMesh.setEvaluator(new ArrayMesh());
      S.CantorMesh.setEvaluator(new CantorMesh());
      S.DelaunayMesh.setEvaluator(new DelaunayMesh());
      S.VoronoiMesh.setEvaluator(new VoronoiMesh());
    }
  }

  /**
   *
   *
   * <pre>
   * <code>VoronoiMesh({p1, p2, ...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the Voronoi mesh of the 2D points <code>p1, p2,...</code> as a <code>MeshRegion</code>.
   * The cell of a point contains all locations which are closer to this point than to any other
   * one. The unbounded cells are clipped to a bounding box around the points.
   *
   * </blockquote>
   *
   * <pre>
   * <code>VoronoiMesh({p1, p2, ...}, {{xmin, xmax}, {ymin, ymax}})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * clip the cells to the given bounds.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Head(VoronoiMesh({{0, 0}, {1, 0}, {0, 1}}))
   * MeshRegion
   * </code>
   * </pre>
   */
  private static class VoronoiMesh extends AbstractEvaluator {

    /** Two coordinates closer than this distance are considered to be the same. */
    private static final double EPSILON = 1.0e-10;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isNonEmptyList()) {
        return F.NIL;
      }
      IAST points = (IAST) arg1;
      int n = points.argSize();
      double[] x = new double[n];
      double[] y = new double[n];
      for (int i = 1; i <= n; i++) {
        if (!points.get(i).isList2()) {
          // the points aren't 2D coordinates
          return F.EmptyRegion(F.C2);
        }
        x[i - 1] = points.get(i).first().evalfNaN();
        y[i - 1] = points.get(i).second().evalfNaN();
        if (Double.isNaN(x[i - 1]) || Double.isNaN(y[i - 1])) {
          // the coordinates aren't numeric
          return F.EmptyRegion(F.C2);
        }
      }

      double[] bounds = ast.isAST2() ? boundsOption(ast.arg2()) : boundingBox(x, y);
      if (bounds == null) {
        // a single point or a degenerated bounding box has no Voronoi mesh
        return F.EmptyRegion(F.C2);
      }

      // the coordinates of the cell corners, shared between the cells
      List<double[]> corners = new ArrayList<double[]>();
      IASTAppendable cells = F.ListAlloc(n);
      for (int i = 0; i < n; i++) {
        List<double[]> cell = voronoiCell(x, y, i, bounds);
        if (cell.size() < 3) {
          // the cell is empty, this happens for duplicated points
          continue;
        }
        IASTAppendable indices = F.ListAlloc(cell.size());
        for (double[] corner : cell) {
          indices.append(F.ZZ(cornerIndex(corners, corner) + 1));
        }
        cells.append(F.Polygon(indices));
      }
      if (cells.argSize() == 0) {
        return F.EmptyRegion(F.C2);
      }

      IASTAppendable coordinates = F.ListAlloc(corners.size());
      for (double[] corner : corners) {
        coordinates.append(F.list(F.num(corner[0]), F.num(corner[1])));
      }
      return F.binaryAST2(S.MeshRegion, coordinates, cells);
    }

    /** The index of <code>corner</code> in <code>corners</code>, appending it if it's new. */
    private static int cornerIndex(List<double[]> corners, double[] corner) {
      for (int i = 0; i < corners.size(); i++) {
        double[] existing = corners.get(i);
        if (Math.abs(existing[0] - corner[0]) < EPSILON
            && Math.abs(existing[1] - corner[1]) < EPSILON) {
          return i;
        }
      }
      corners.add(corner);
      return corners.size() - 1;
    }

    /**
     * A bounding box around all points, enlarged so that the unbounded cells stay visible.
     *
     * @return <code>{xmin, xmax, ymin, ymax}</code> or <code>null</code> if all points are equal
     */
    private static double[] boundingBox(double[] x, double[] y) {
      double minX = x[0], maxX = x[0], minY = y[0], maxY = y[0];
      for (int i = 1; i < x.length; i++) {
        minX = Math.min(minX, x[i]);
        maxX = Math.max(maxX, x[i]);
        minY = Math.min(minY, y[i]);
        maxY = Math.max(maxY, y[i]);
      }
      double extent = Math.max(maxX - minX, maxY - minY);
      if (extent <= EPSILON) {
        // all points are equal, there is no Voronoi mesh
        return null;
      }
      double padding = extent / 2.0;
      return new double[] {minX - padding, maxX + padding, minY - padding, maxY + padding};
    }

    /** Read the explicit bounds <code>{{xmin, xmax}, {ymin, ymax}}</code>. */
    private static double[] boundsOption(IExpr arg2) {
      if (!arg2.isList2() || !arg2.first().isList2() || !arg2.second().isList2()) {
        return null;
      }
      double xmin = arg2.first().first().evalfNaN();
      double xmax = arg2.first().second().evalfNaN();
      double ymin = arg2.second().first().evalfNaN();
      double ymax = arg2.second().second().evalfNaN();
      if (Double.isNaN(xmin) || Double.isNaN(xmax) || Double.isNaN(ymin) || Double.isNaN(ymax)
          || xmax - xmin <= EPSILON || ymax - ymin <= EPSILON) {
        return null;
      }
      return new double[] {xmin, xmax, ymin, ymax};
    }

    /**
     * The Voronoi cell of the point with the index <code>site</code>: the bounding box clipped by
     * the half plane of every other point.
     *
     * @return the counterclockwise corners of the convex cell
     */
    private static List<double[]> voronoiCell(double[] x, double[] y, int site, double[] bounds) {
      List<double[]> cell = new ArrayList<double[]>(4);
      cell.add(new double[] {bounds[0], bounds[2]});
      cell.add(new double[] {bounds[1], bounds[2]});
      cell.add(new double[] {bounds[1], bounds[3]});
      cell.add(new double[] {bounds[0], bounds[3]});

      for (int other = 0; other < x.length && !cell.isEmpty(); other++) {
        if (other == site) {
          continue;
        }
        double dx = x[other] - x[site];
        double dy = y[other] - y[site];
        if (Math.abs(dx) < EPSILON && Math.abs(dy) < EPSILON) {
          // a duplicated point: only the first of them gets a cell
          if (other < site) {
            return new ArrayList<double[]>();
          }
          continue;
        }
        // the points of the cell are on the site's side of the perpendicular bisector:
        // dx*(px - mx) + dy*(py - my) <= 0 with the midpoint (mx, my)
        double offset = dx * (x[site] + x[other]) / 2.0 + dy * (y[site] + y[other]) / 2.0;
        cell = clipHalfPlane(cell, dx, dy, offset);
      }
      return cell;
    }

    /**
     * Sutherland-Hodgman: clip the convex polygon to the half plane
     * <code>a*px + b*py &lt;= offset</code>.
     */
    private static List<double[]> clipHalfPlane(List<double[]> polygon, double a, double b,
        double offset) {
      List<double[]> result = new ArrayList<double[]>(polygon.size() + 1);
      int size = polygon.size();
      for (int i = 0; i < size; i++) {
        double[] current = polygon.get(i);
        double[] next = polygon.get((i + 1) % size);
        double currentSide = a * current[0] + b * current[1] - offset;
        double nextSide = a * next[0] + b * next[1] - offset;
        if (currentSide <= EPSILON) {
          result.add(current);
        }
        if ((currentSide > EPSILON && nextSide < -EPSILON)
            || (currentSide < -EPSILON && nextSide > EPSILON)) {
          double t = currentSide / (currentSide - nextSide);
          result.add(new double[] {current[0] + t * (next[0] - current[0]),
              current[1] + t * (next[1] - current[1])});
        }
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * The geometric dimension of a mesh cell: <code>Point</code> is 0, <code>Line</code> is 1 and
   * <code>Polygon</code> is 2.
   *
   * @return {@link #NO_CELL} if <code>cellHead</code> isn't a mesh cell head
   */
  public static int cellDimension(IExpr cellHead) {
    if (cellHead.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) cellHead).ordinal()) {
        case ID.Point:
          return 0;
        case ID.Line:
          return 1;
        case ID.Polygon:
          return 2;
      }
    }
    return NO_CELL;
  }

  /**
   * Test if <code>expr</code> is a structurally valid <code>BoundaryMeshRegion</code> or
   * <code>MeshRegion</code> expression.
   */
  public static boolean isMeshRegion(IExpr expr) {
    if (expr.isAST(S.BoundaryMeshRegion) || expr.isAST(S.MeshRegion)) {
      return validate((IAST) expr, null, null);
    }
    return false;
  }

  /** Test if <code>expr</code> is a structurally valid <code>BoundaryMeshRegion</code>. */
  public static boolean isBoundaryMeshRegion(IExpr expr) {
    return expr.isAST(S.BoundaryMeshRegion) && validate((IAST) expr, null, null);
  }

  /** The coordinate list of a mesh region. */
  public static IAST meshCoordinates(IAST meshRegion) {
    return (IAST) meshRegion.arg1();
  }

  /** The number of coordinates of the space the mesh region is embedded in. */
  public static int embeddingDimension(IAST meshRegion) {
    IAST coordinates = meshCoordinates(meshRegion);
    return coordinates.argSize() > 0 ? coordinates.arg1().argSize() : 0;
  }

  /**
   * The number of cells of each dimension, <code>0</code> up to the embedding dimension.
   *
   * <p>
   * A full dimensional cell that is not stored is still one cell - the square the boundary of a
   * two dimensional mesh encloses, the solid a three dimensional one does - which is how the
   * reference implementation counts when it styles every cell of a mesh.
   */
  private static int[] cellCounts(IAST meshRegion) {
    int dimension = embeddingDimension(meshRegion);
    int[] counts = new int[dimension + 1];
    for (int d = 0; d <= dimension; d++) {
      IAST cells = meshCells(meshRegion, d);
      counts[d] = cells.isPresent() ? cells.argSize() : (d == dimension ? 1 : 0);
    }
    return counts;
  }

  /**
   * A <code>MeshCellStyle</code> setting, written out cell by cell as the <code>Properties</code> a
   * mesh region carries.
   *
   * <p>
   * The reference implementation does not keep the setting as it was given. A bare style applies
   * to every cell of every dimension, <code>{d, All} -&gt; s</code> to every cell of dimension
   * <code>d</code> and <code>{d, i} -&gt; s</code> to one cell; each cell gets its own
   * <code>{d, i} -&gt; MeshCellStyle -&gt; s</code>, ordered by dimension and then by index, and each
   * dimension that got any closes with <code>{d, Default} -&gt; MeshCellStyle -&gt; Automatic</code>.
   * A later rule wins over an earlier one for the same cell; one that names no cell of the mesh is
   * dropped.
   *
   * @param meshRegion the mesh the setting is for
   * @param spec the right hand side of <code>MeshCellStyle -&gt; spec</code>
   * @return the list for <code>Properties -&gt; list</code>, or {@link F#NIL} when it styles nothing
   */
  public static IAST meshCellStyleProperties(IAST meshRegion, IExpr spec) {
    int[] counts = cellCounts(meshRegion);
    java.util.TreeMap<Integer, java.util.TreeMap<Integer, IExpr>> styles =
        new java.util.TreeMap<Integer, java.util.TreeMap<Integer, IExpr>>();
    if (spec.isList() && spec.argSize() > 0 && ((IAST) spec).forAll(x -> x.isRuleAST())) {
      for (IExpr rule : (IAST) spec) {
        addCellStyle(styles, counts, rule.first(), rule.second());
      }
    } else if (!spec.isRuleAST()) {
      // one style for everything
      for (int d = 0; d < counts.length; d++) {
        for (int i = 1; i <= counts[d]; i++) {
          styles.computeIfAbsent(d, k -> new java.util.TreeMap<Integer, IExpr>()).put(i, spec);
        }
      }
    }
    if (styles.isEmpty()) {
      return F.NIL;
    }
    IASTAppendable properties = F.ListAlloc();
    for (java.util.Map.Entry<Integer, java.util.TreeMap<Integer, IExpr>> dimension : styles
        .entrySet()) {
      IExpr d = F.ZZ(dimension.getKey());
      for (java.util.Map.Entry<Integer, IExpr> cell : dimension.getValue().entrySet()) {
        properties.append(F.Rule(F.list(d, F.ZZ(cell.getKey())),
            F.Rule(S.MeshCellStyle, cell.getValue())));
      }
      properties.append(F.Rule(F.list(d, S.Default), F.Rule(S.MeshCellStyle, S.Automatic)));
    }
    return properties;
  }

  /** One <code>{d, cells} -&gt; style</code> rule of a <code>MeshCellStyle</code> list. */
  private static void addCellStyle(java.util.TreeMap<Integer, java.util.TreeMap<Integer, IExpr>> styles,
      int[] counts, IExpr key, IExpr style) {
    if (!key.isList2() || !key.first().isInteger()) {
      return;
    }
    int d = key.first().toIntDefault();
    if (d < 0 || d >= counts.length) {
      return;
    }
    java.util.TreeMap<Integer, IExpr> cells =
        styles.computeIfAbsent(d, k -> new java.util.TreeMap<Integer, IExpr>());
    IExpr which = key.second();
    if (which == S.All) {
      for (int i = 1; i <= counts[d]; i++) {
        cells.put(i, style);
      }
    } else if (which.isInteger()) {
      int i = which.toIntDefault();
      if (i >= 1 && i <= counts[d]) {
        cells.put(i, style);
      }
    } else if (which.isList()) {
      for (IExpr index : (IAST) which) {
        int i = index.toIntDefault();
        if (i >= 1 && i <= counts[d]) {
          cells.put(i, style);
        }
      }
    }
    if (cells.isEmpty()) {
      styles.remove(d);
    }
  }

  /**
   * The styles a mesh region's <code>Properties</code> give its cells, by dimension and then by
   * index - the setting {@link #meshCellStyleProperties(IAST, IExpr)} wrote, read back for drawing.
   * The closing <code>Default -&gt; Automatic</code> entries style nothing and are left out.
   */
  public static java.util.TreeMap<Integer, java.util.TreeMap<Integer, IExpr>> meshCellStyles(
      IAST meshRegion) {
    java.util.TreeMap<Integer, java.util.TreeMap<Integer, IExpr>> styles =
        new java.util.TreeMap<Integer, java.util.TreeMap<Integer, IExpr>>();
    for (int i = optionsStartIndex(meshRegion); i < meshRegion.size(); i++) {
      IExpr option = meshRegion.get(i);
      if (!option.isRuleAST() || option.first() != S.Properties || !option.second().isList()) {
        continue;
      }
      for (IExpr entry : (IAST) option.second()) {
        if (entry.isRuleAST() && entry.first().isList2() && entry.first().first().isInteger()
            && entry.first().second().isInteger() && entry.second().isRuleAST()
            && entry.second().first() == S.MeshCellStyle
            && entry.second().second() != S.Automatic) {
          styles
              .computeIfAbsent(entry.first().first().toIntDefault(),
                  k -> new java.util.TreeMap<Integer, IExpr>())
              .put(entry.first().second().toIntDefault(), entry.second().second());
        }
      }
    }
    return styles;
  }

  /** The face colour the reference implementation draws a two dimensional mesh region in. */
  private static final IAST FACE_2D = F.RGBColor(F.num(0.6260033081763745),
      F.num(0.8359330492764128), F.num(0.9185378316380052));

  /** The edge colour of a two dimensional mesh region. */
  private static final IAST EDGE_2D = F.RGBColor(F.num(0.372575209344428),
      F.num(0.6124949134587575), F.num(0.706900379014863));

  /** The face and edge colour of a three dimensional mesh region. */
  private static final IAST FACE_3D = F.RGBColor(F.num(0.465719011680535),
      F.num(0.7656186418234469), F.num(0.8836254737685788));

  /**
   * The picture of a boundary mesh region, as <code>Show</code> gives it and as the renderers draw
   * it - the form the reference implementation produces, measured in Mathematica on 2026-09-19:
   *
   * <pre>
   * Graphics[GraphicsComplex[N[coordinates], {Directive[{face, EdgeForm[{edge}]}],
   *     {Annotation[Polygon[faces], "Geometry"]}, styled}]]
   * </pre>
   *
   * <p>
   * A two dimensional region is drawn as the polygon its boundary encloses, a three dimensional
   * one as its faces, with <code>Boxed -&gt; False</code> and four lights. Cells that
   * <code>MeshCellStyle</code> styled follow the geometry as <code>{Directive[style], Line[...]}</code>
   * groups, one per style and dimension; a styled face is taken out of the unstyled polygon, so
   * that a translucent face is not drawn over an opaque copy of itself.
   *
   * @return the <code>Graphics</code> or <code>Graphics3D</code>, or {@link F#NIL} for a region
   *         that is not two or three dimensional
   */
  public static IAST meshToGraphics(IAST meshRegion, EvalEngine engine) {
    IAST complex = meshGraphicsComplex(meshRegion, engine);
    if (complex.isNIL()) {
      return F.NIL;
    }
    if (embeddingDimension(meshRegion) == 2) {
      return F.Graphics(complex);
    }
    IAST lighting = F.List( //
        F.list(F.stringx("Ambient"), F.GrayLevel(F.num(0.45))), //
        F.list(F.stringx("Directional"), F.GrayLevel(F.num(0.3)),
            F.unaryAST1(S.ImageScaled, F.list(F.C2, F.C0, F.C2))), //
        F.list(F.stringx("Directional"), F.GrayLevel(F.num(0.33)),
            F.unaryAST1(S.ImageScaled, F.list(F.C2, F.C2, F.C2))), //
        F.list(F.stringx("Directional"), F.GrayLevel(F.num(0.3)),
            F.unaryAST1(S.ImageScaled, F.list(F.C0, F.C2, F.C2))));
    return F.Graphics3D(complex,
        F.list(F.Rule(S.Boxed, S.False), F.Rule(S.Lighting, lighting)));
  }

  /**
   * The <code>GraphicsComplex</code> of {@link #meshToGraphics(IAST, EvalEngine)}, which is what the
   * renderers draw when a mesh region sits among other primitives.
   */
  public static IAST meshGraphicsComplex(IAST meshRegion, EvalEngine engine) {
    if (!isBoundaryMeshRegion(meshRegion)) {
      return F.NIL;
    }
    int dimension = embeddingDimension(meshRegion);
    if (dimension != 2 && dimension != 3) {
      return F.NIL;
    }
    IAST faces = meshCells(meshRegion, 2);
    if (faces.isNIL()) {
      return F.NIL;
    }
    java.util.TreeMap<Integer, java.util.TreeMap<Integer, IExpr>> styles =
        meshCellStyles(meshRegion);
    java.util.Map<Integer, IExpr> styledFaces =
        styles.getOrDefault(2, new java.util.TreeMap<Integer, IExpr>());

    IASTAppendable unstyledFaces = F.ListAlloc(faces.argSize());
    for (int i = 1; i <= faces.argSize(); i++) {
      if (!styledFaces.containsKey(i)) {
        unstyledFaces.append(faces.get(i).first());
      }
    }
    IAST face = dimension == 2 ? FACE_2D : FACE_3D;
    IAST edge = dimension == 2 ? EDGE_2D : FACE_3D;
    IASTAppendable primitives = F.ListAlloc(3);
    primitives.append(F.Directive(F.list(face, F.unaryAST1(S.EdgeForm, F.list(edge)))));
    if (unstyledFaces.argSize() > 0) {
      primitives.append(F.list(
          F.binaryAST2(S.Annotation, F.Polygon(unstyledFaces), F.stringx("Geometry"))));
    }
    IAST styled = styledGroups(meshRegion, styles);
    if (styled.argSize() > 0) {
      primitives.append(styled);
    }
    return F.binaryAST2(S.GraphicsComplex, engine.evaluate(F.N(meshCoordinates(meshRegion))),
        primitives);
  }

  /**
   * The styled cells as <code>{Directive[style], Head[cells]}</code> groups - one per dimension
   * and style, in the order the styles first appear, every cell of a group gathered into one
   * primitive as <code>Line[{{1,2},{2,3},...}]</code>.
   */
  private static IAST styledGroups(IAST meshRegion,
      java.util.TreeMap<Integer, java.util.TreeMap<Integer, IExpr>> styles) {
    IASTAppendable groups = F.ListAlloc();
    for (java.util.Map.Entry<Integer, java.util.TreeMap<Integer, IExpr>> dimension : styles
        .entrySet()) {
      int d = dimension.getKey();
      IAST cells = meshCells(meshRegion, d);
      if (cells.isNIL()) {
        // the solid of a three dimensional region has no primitive to draw it with
        continue;
      }
      java.util.LinkedHashMap<IExpr, IASTAppendable> byStyle =
          new java.util.LinkedHashMap<IExpr, IASTAppendable>();
      for (java.util.Map.Entry<Integer, IExpr> cell : dimension.getValue().entrySet()) {
        int i = cell.getKey();
        if (i < 1 || i > cells.argSize()) {
          continue;
        }
        IExpr indices = cells.get(i).first();
        IASTAppendable members = byStyle.computeIfAbsent(cell.getValue(), k -> F.ListAlloc());
        if (d == 0 && indices.isList()) {
          // a point cell holds one index; the group is one list of them
          members.appendArgs((IAST) indices);
        } else {
          members.append(indices);
        }
      }
      IBuiltInSymbol head = d == 0 ? S.Point : d == 1 ? S.Line : S.Polygon;
      for (java.util.Map.Entry<IExpr, IASTAppendable> group : byStyle.entrySet()) {
        IExpr style = group.getKey();
        groups.append(F.list(style.isAST(S.Directive) ? style : F.Directive(style),
            F.unaryAST1(head, group.getValue())));
      }
    }
    return groups;
  }

  /**
   * The index of the first argument which is an option; all arguments from index <code>2</code> up
   * to (but excluding) that index are boundary cell lists.
   */
  private static int optionsStartIndex(IAST meshRegion) {
    for (int i = 2; i < meshRegion.size(); i++) {
      if (isOptionArgument(meshRegion.get(i))) {
        return i;
      }
    }
    return meshRegion.size();
  }

  private static boolean isOptionArgument(IExpr arg) {
    if (arg.isRuleAST()) {
      return true;
    }
    if (arg.isList() && arg.argSize() > 0) {
      IAST list = (IAST) arg;
      for (int i = 1; i < list.size(); i++) {
        if (!list.get(i).isRuleAST()) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Walk all boundary cells of a mesh region. Cells given in the compact form
   * <code>Line({{1,2},{2,3}})</code> are reported one by one.
   *
   * @param meshRegion the mesh region expression
   * @param numberOfCoordinates number of available coordinates, or <code>-1</code> to skip the
   *        index range check
   * @param cellHeads receives the head of every cell, may be <code>null</code>
   * @param cellIndices receives the index list of every cell, may be <code>null</code>
   * @return <code>false</code> if a cell is malformed or an index is out of range
   */
  private static boolean walkCells(IAST meshRegion, int numberOfCoordinates,
      List<IBuiltInSymbol> cellHeads, List<IAST> cellIndices) {
    int optionsStart = optionsStartIndex(meshRegion);
    for (int i = 2; i < optionsStart; i++) {
      IExpr boundary = meshRegion.get(i);
      if (!boundary.isList()) {
        return false;
      }
      IAST boundaryList = (IAST) boundary;
      for (int j = 1; j < boundaryList.size(); j++) {
        IExpr cell = boundaryList.get(j);
        if (!cell.isAST1() || cellDimension(cell.head()) == NO_CELL) {
          return false;
        }
        IBuiltInSymbol head = (IBuiltInSymbol) cell.head();
        IExpr indices = cell.first();
        if (!indices.isList() || indices.argSize() == 0) {
          return false;
        }
        IAST indexList = (IAST) indices;
        if (indexList.arg1().isList()) {
          // compact form: a list of index lists
          for (int k = 1; k < indexList.size(); k++) {
            if (!indexList.get(k).isList()) {
              return false;
            }
            if (!addCell(head, (IAST) indexList.get(k), numberOfCoordinates, cellHeads,
                cellIndices)) {
              return false;
            }
          }
        } else {
          if (!addCell(head, indexList, numberOfCoordinates, cellHeads, cellIndices)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private static boolean addCell(IBuiltInSymbol head, IAST indexList, int numberOfCoordinates,
      List<IBuiltInSymbol> cellHeads, List<IAST> cellIndices) {
    for (int i = 1; i < indexList.size(); i++) {
      int index = indexList.get(i).toIntDefault();
      if (index < 1) {
        return false;
      }
      if (numberOfCoordinates >= 0 && index > numberOfCoordinates) {
        return false;
      }
    }
    if (cellHeads != null) {
      cellHeads.add(head);
    }
    if (cellIndices != null) {
      cellIndices.add(indexList);
    }
    return true;
  }

  /**
   * Validate the structure of a mesh region expression.
   *
   * @param cellHeads receives the head of every boundary cell, may be <code>null</code>
   * @param cellIndices receives the index list of every boundary cell, may be <code>null</code>
   */
  private static boolean validate(IAST meshRegion, List<IBuiltInSymbol> cellHeads,
      List<IAST> cellIndices) {
    if (meshRegion.argSize() < 2) {
      return false;
    }
    IExpr coordinates = meshRegion.arg1();
    if (!coordinates.isListOfLists() || coordinates.argSize() == 0) {
      return false;
    }
    IAST coordinateList = (IAST) coordinates;
    int embeddingDimension = coordinateList.arg1().argSize();
    if (embeddingDimension < 1 || embeddingDimension > 3) {
      return false;
    }
    for (int i = 2; i < coordinateList.size(); i++) {
      if (coordinateList.get(i).argSize() != embeddingDimension) {
        return false;
      }
    }
    return walkCells(meshRegion, coordinateList.argSize(), cellHeads, cellIndices);
  }

  /**
   * All boundary cells of the given geometric dimension, each one as a separate cell expression
   * like <code>Line({1,2})</code>.
   */
  public static IAST boundaryCells(IAST meshRegion, int dimension) {
    List<IBuiltInSymbol> heads = new ArrayList<IBuiltInSymbol>();
    List<IAST> indices = new ArrayList<IAST>();
    if (!validate(meshRegion, heads, indices)) {
      return F.NIL;
    }
    IASTAppendable result = F.ListAlloc(indices.size());
    for (int i = 0; i < heads.size(); i++) {
      if (cellDimension(heads.get(i)) == dimension) {
        result.append(F.unaryAST1(heads.get(i), indices.get(i)));
      }
    }
    return result;
  }

  /**
   * The boundary edges of a mesh region as index pairs. In 2D these are the stored
   * <code>Line</code> cells, in 3D they are derived from the polygon boundaries.
   */
  private static IAST edgeCells(IAST meshRegion) {
    IAST lines = boundaryCells(meshRegion, 1);
    if (lines.isPresent() && lines.argSize() > 0) {
      return lines;
    }
    IAST polygons = boundaryCells(meshRegion, 2);
    if (polygons.isNIL()) {
      return F.NIL;
    }
    IASTAppendable result = F.ListAlloc(polygons.argSize() * 3);
    LongOpenHashSet seen = new LongOpenHashSet();
    for (int i = 1; i < polygons.size(); i++) {
      IAST indices = (IAST) polygons.get(i).first();
      int size = indices.argSize();
      for (int j = 1; j <= size; j++) {
        int from = indices.get(j).toIntDefault();
        int to = indices.get(j == size ? 1 : j + 1).toIntDefault();
        if (from < 1 || to < 1) {
          return F.NIL;
        }
        long key = from < to ? (((long) from) << 32) | to : (((long) to) << 32) | from;
        if (seen.add(key)) {
          result.append(F.Line(F.list(F.ZZ(from), F.ZZ(to))));
        }
      }
    }
    return result;
  }

  /**
   * The cells of geometric dimension <code>dimension</code>, following the conventions: dimension
   * <code>0</code> are the coordinates as <code>Point</code> cells, the dimension of the region
   * itself is the filled cell.
   */
  public static IAST meshCells(IAST meshRegion, int dimension) {
    if (!validate(meshRegion, null, null)) {
      return F.NIL;
    }
    int embeddingDimension = embeddingDimension(meshRegion);
    if (dimension < 0 || dimension > embeddingDimension) {
      return F.NIL;
    }
    if (dimension == embeddingDimension) {
      IAST stored = boundaryCells(meshRegion, dimension);
      if (stored.isPresent() && stored.argSize() > 0) {
        // a full dimensional mesh region stores its cells explicitly
        return stored;
      }
      // otherwise the cell of full dimension is the region which the boundary encloses; in 3D it
      // isn't representable as an index based cell
      if (embeddingDimension == 1) {
        IAST points = boundaryCells(meshRegion, 0);
        if (points.isNIL() || points.argSize() == 0) {
          return F.NIL;
        }
        IASTAppendable indices = F.ListAlloc(points.argSize());
        for (int i = 1; i < points.size(); i++) {
          indices.appendArgs((IAST) points.get(i).first());
        }
        return F.list(F.Line(indices));
      }
      if (embeddingDimension == 2) {
        IAST cycle = boundaryCycle(meshRegion);
        return cycle.isPresent() ? F.list(F.Polygon(cycle)) : F.NIL;
      }
      return F.NIL;
    }
    if (dimension == 0) {
      IAST points = boundaryCells(meshRegion, 0);
      if (points.isPresent() && points.argSize() > 0) {
        return points;
      }
      int numberOfCoordinates = meshCoordinates(meshRegion).argSize();
      IASTAppendable result = F.ListAlloc(numberOfCoordinates);
      for (int i = 1; i <= numberOfCoordinates; i++) {
        result.append(F.Point(F.list(F.ZZ(i))));
      }
      return result;
    }
    if (dimension == 1) {
      return edgeCells(meshRegion);
    }
    return boundaryCells(meshRegion, dimension);
  }

  /**
   * The vertex indices of a two dimensional boundary mesh region, ordered along the closed boundary
   * curve.
   *
   * @return {@link F#NIL} if the boundary isn't a single closed curve
   */
  public static IAST boundaryCycle(IAST meshRegion) {
    IAST lines = boundaryCells(meshRegion, 1);
    if (lines.isNIL() || lines.argSize() == 0) {
      return F.NIL;
    }
    int numberOfEdges = lines.argSize();
    IntArrayList from = new IntArrayList(numberOfEdges);
    IntArrayList to = new IntArrayList(numberOfEdges);
    for (int i = 1; i < lines.size(); i++) {
      IAST indices = (IAST) lines.get(i).first();
      if (indices.argSize() != 2) {
        return F.NIL;
      }
      from.add(indices.arg1().toIntDefault());
      to.add(indices.arg2().toIntDefault());
    }
    boolean[] used = new boolean[numberOfEdges];
    int start = from.getInt(0);
    IASTAppendable result = F.ListAlloc(numberOfEdges);
    result.append(F.ZZ(start));
    int current = to.getInt(0);
    used[0] = true;
    for (int step = 1; step < numberOfEdges; step++) {
      if (current == start) {
        // closed too early - the boundary isn't a single cycle
        return F.NIL;
      }
      result.append(F.ZZ(current));
      int next = -1;
      for (int i = 0; i < numberOfEdges; i++) {
        if (!used[i] && from.getInt(i) == current) {
          used[i] = true;
          next = to.getInt(i);
          break;
        }
      }
      if (next < 0) {
        return F.NIL;
      }
      current = next;
    }
    return current == start ? result : F.NIL;
  }

  /**
   * The polygon which is bounded by a two dimensional boundary mesh region.
   *
   * @return {@link F#NIL} if the region isn't two dimensional or its boundary isn't a single closed
   *         curve
   */
  public static IExpr toPolygon(IAST meshRegion) {
    if (!isMeshRegion(meshRegion) || embeddingDimension(meshRegion) != 2) {
      return F.NIL;
    }
    IAST cycle = boundaryCycle(meshRegion);
    if (cycle.isNIL()) {
      return F.NIL;
    }
    IAST coordinates = meshCoordinates(meshRegion);
    IASTAppendable points = F.ListAlloc(cycle.argSize());
    for (int i = 1; i < cycle.size(); i++) {
      points.append(coordinates.get(cycle.get(i).toIntDefault()));
    }
    return F.Polygon(points);
  }

  /**
   * Replace a two dimensional mesh region by the polygon it bounds and an index based
   * <code>Polygon({p1,...}, {i1,...})</code> by the polygon through the indexed corners, so that
   * the region functions can treat both like any other polygon. Every other expression is returned
   * unchanged.
   */
  public static IExpr normalizeRegion(IExpr region) {
    if (region.isAST(S.BoundaryMeshRegion) || region.isAST(S.MeshRegion)) {
      IExpr polygon = toPolygon((IAST) region);
      if (polygon.isPresent()) {
        return polygon;
      }
      return region;
    }
    if (region.isAST(S.Polyhedron, 3)) {
      // a polyhedron is a boundary mesh region with polygonal boundary cells
      IAST polyhedron = (IAST) region;
      if (polyhedron.arg1().isListOfLists() && polyhedron.arg2().isListOfLists()) {
        return F.binaryAST2(S.BoundaryMeshRegion, polyhedron.arg1(),
            F.list(F.Polygon(polyhedron.arg2())));
      }
      return region;
    }
    if (region.isAST(S.Polygon, 3)) {
      IAST polygon = (IAST) region;
      if (polygon.arg1().isListOfLists() && polygon.arg2().isList()) {
        IAST coordinates = (IAST) polygon.arg1();
        IAST indices = (IAST) polygon.arg2();
        IASTAppendable points = F.ListAlloc(indices.argSize());
        for (int i = 1; i < indices.size(); i++) {
          int index = indices.get(i).toIntDefault();
          if (index < 1 || index > coordinates.argSize()) {
            return region;
          }
          points.append(coordinates.get(index));
        }
        return F.Polygon(points);
      }
    }
    return region;
  }

  /** The triangles of the boundary of a three dimensional mesh region, as coordinate triples. */
  private static List<IAST[]> boundaryTriangles(IAST meshRegion) {
    IAST polygons = boundaryCells(meshRegion, 2);
    if (polygons.isNIL() || polygons.argSize() == 0) {
      return null;
    }
    IAST coordinates = meshCoordinates(meshRegion);
    List<IAST[]> triangles = new ArrayList<IAST[]>();
    for (int i = 1; i < polygons.size(); i++) {
      IAST face = (IAST) polygons.get(i).first();
      if (face.argSize() < 3) {
        return null;
      }
      IExpr first = coordinates.get(face.arg1().toIntDefault());
      for (int j = 2; j < face.argSize(); j++) {
        IExpr second = coordinates.get(face.get(j).toIntDefault());
        IExpr third = coordinates.get(face.get(j + 1).toIntDefault());
        triangles.add(new IAST[] {(IAST) first, (IAST) second, (IAST) third});
      }
    }
    return triangles;
  }

  /** Six times the signed volume of the tetrahedron spanned by the origin and a triangle. */
  private static IExpr signedTetrahedronVolume(IAST a, IAST b, IAST c, EvalEngine engine) {
    // a . (b x c)
    IExpr crossX = F.Subtract(F.Times(b.arg2(), c.arg3()), F.Times(b.arg3(), c.arg2()));
    IExpr crossY = F.Subtract(F.Times(b.arg3(), c.arg1()), F.Times(b.arg1(), c.arg3()));
    IExpr crossZ = F.Subtract(F.Times(b.arg1(), c.arg2()), F.Times(b.arg2(), c.arg1()));
    return engine.evaluate(
        F.Plus(F.Times(a.arg1(), crossX), F.Times(a.arg2(), crossY), F.Times(a.arg3(), crossZ)));
  }

  /**
   * The volume of a three dimensional mesh region, computed with the divergence theorem over the
   * triangulated boundary.
   */
  /**
   * The two dimensional cells of a mesh region, as lists of explicit coordinates.
   *
   * @return <code>null</code> if the mesh region has no explicit two dimensional cells
   */
  public static List<IAST> faces2D(IAST meshRegion) {
    if (!isMeshRegion(meshRegion) || embeddingDimension(meshRegion) != 2) {
      return null;
    }
    IAST cells = boundaryCells(meshRegion, 2);
    if (cells.isNIL() || cells.argSize() == 0) {
      return null;
    }
    IAST coordinates = meshCoordinates(meshRegion);
    List<IAST> faces = new ArrayList<IAST>(cells.argSize());
    for (int i = 1; i < cells.size(); i++) {
      IAST indices = (IAST) cells.get(i).first();
      IASTAppendable points = F.ListAlloc(indices.argSize());
      for (int j = 1; j < indices.size(); j++) {
        int index = indices.get(j).toIntDefault();
        if (index < 1 || index > coordinates.argSize()) {
          return null;
        }
        points.append(coordinates.get(index));
      }
      faces.add(points);
    }
    return faces;
  }

  /** The sum of the areas of the two dimensional cells of a mesh region. */
  public static IExpr area2D(IAST meshRegion, EvalEngine engine) {
    List<IAST> faces = faces2D(meshRegion);
    if (faces == null) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(faces.size());
    for (IAST face : faces) {
      sum.append(F.Area(F.Polygon(face)));
    }
    return engine.evaluate(sum);
  }

  /** The area weighted centroid of the two dimensional cells of a mesh region. */
  public static IExpr centroid2D(IAST meshRegion, EvalEngine engine) {
    List<IAST> faces = faces2D(meshRegion);
    if (faces == null) {
      return F.NIL;
    }
    IASTAppendable areaSum = F.PlusAlloc(faces.size());
    IASTAppendable weighted = F.PlusAlloc(faces.size());
    for (IAST face : faces) {
      IExpr area = engine.evaluate(F.Area(F.Polygon(face)));
      IExpr centroid = engine.evaluate(F.RegionCentroid(F.Polygon(face)));
      if (!centroid.isList2()) {
        return F.NIL;
      }
      areaSum.append(area);
      weighted.append(F.Times(area, centroid));
    }
    IExpr total = engine.evaluate(areaSum);
    if (total.isZero()) {
      return F.NIL;
    }
    return engine.evaluate(F.Divide(weighted, total));
  }

  /** A point is a member of a mesh region if it lies in one of its two dimensional cells. */
  public static IExpr member2D(IAST meshRegion, IExpr point, EvalEngine engine) {
    List<IAST> faces = faces2D(meshRegion);
    if (faces == null) {
      return F.NIL;
    }
    boolean allFalse = true;
    for (IAST face : faces) {
      IExpr member = engine.evaluate(F.RegionMember(F.Polygon(face), point));
      if (member.isTrue()) {
        return S.True;
      }
      if (!member.isFalse()) {
        allFalse = false;
      }
    }
    return allFalse ? S.False : F.NIL;
  }

  public static IExpr volume3D(IAST meshRegion, EvalEngine engine) {
    List<IAST[]> triangles = boundaryTriangles(meshRegion);
    if (triangles == null) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(triangles.size());
    for (IAST[] triangle : triangles) {
      sum.append(signedTetrahedronVolume(triangle[0], triangle[1], triangle[2], engine));
    }
    return engine.evaluate(F.Divide(F.Abs(sum), F.ZZ(6)));
  }

  /** The centroid of a three dimensional mesh region. */
  public static IExpr centroid3D(IAST meshRegion, EvalEngine engine) {
    List<IAST[]> triangles = boundaryTriangles(meshRegion);
    if (triangles == null) {
      return F.NIL;
    }
    IASTAppendable volumeSum = F.PlusAlloc(triangles.size());
    IASTAppendable[] coordinateSums = new IASTAppendable[] {F.PlusAlloc(triangles.size()),
        F.PlusAlloc(triangles.size()), F.PlusAlloc(triangles.size())};
    for (IAST[] triangle : triangles) {
      IExpr volume = signedTetrahedronVolume(triangle[0], triangle[1], triangle[2], engine);
      volumeSum.append(volume);
      for (int j = 1; j <= 3; j++) {
        // the centroid of the tetrahedron spanned by the origin and the triangle
        IExpr center =
            F.Divide(F.Plus(triangle[0].get(j), triangle[1].get(j), triangle[2].get(j)), F.C4);
        coordinateSums[j - 1].append(F.Times(volume, center));
      }
    }
    IExpr totalVolume = engine.evaluate(volumeSum);
    if (totalVolume.isZero()) {
      return F.NIL;
    }
    IASTAppendable result = F.ListAlloc(3);
    for (int j = 0; j < 3; j++) {
      result.append(engine.evaluate(F.Divide(coordinateSums[j], totalVolume)));
    }
    return result;
  }

  /**
   * Test if a point lies inside a convex three dimensional mesh region, by testing it against the
   * half space of every boundary face.
   *
   * @return {@link F#NIL} if the test isn't decidable
   */
  public static IExpr member3D(IAST meshRegion, IExpr point, EvalEngine engine) {
    List<IAST[]> triangles = boundaryTriangles(meshRegion);
    if (triangles == null || !point.isList3()) {
      return F.NIL;
    }
    if (!convexQ(meshRegion, engine).isTrue()) {
      // the half space test is only valid for a convex solid
      return F.NIL;
    }
    IAST p = (IAST) point;
    for (IAST[] triangle : triangles) {
      IExpr side = engine.evaluate(outwardHalfSpace(triangle, p));
      if (side.isPositiveResult()) {
        return S.False;
      }
      if (!side.isNegativeResult() && !side.isZero()) {
        return F.NIL;
      }
    }
    return S.True;
  }

  /**
   * <code>(p - a) . ((b - a) x (c - a))</code>, which is positive if <code>p</code> is on the outer
   * side of the counter-clockwise oriented triangle <code>(a, b, c)</code>.
   */
  private static IExpr outwardHalfSpace(IAST[] triangle, IAST p) {
    IAST a = triangle[0];
    IExpr ux = F.Subtract(triangle[1].arg1(), a.arg1());
    IExpr uy = F.Subtract(triangle[1].arg2(), a.arg2());
    IExpr uz = F.Subtract(triangle[1].arg3(), a.arg3());
    IExpr vx = F.Subtract(triangle[2].arg1(), a.arg1());
    IExpr vy = F.Subtract(triangle[2].arg2(), a.arg2());
    IExpr vz = F.Subtract(triangle[2].arg3(), a.arg3());
    IExpr nx = F.Subtract(F.Times(uy, vz), F.Times(uz, vy));
    IExpr ny = F.Subtract(F.Times(uz, vx), F.Times(ux, vz));
    IExpr nz = F.Subtract(F.Times(ux, vy), F.Times(uy, vx));
    return F.Plus(F.Times(F.Subtract(p.arg1(), a.arg1()), nx), //
        F.Times(F.Subtract(p.arg2(), a.arg2()), ny), //
        F.Times(F.Subtract(p.arg3(), a.arg3()), nz));
  }

  /**
   * Test if a closed polygon is convex, that is if all turns along its boundary have the same
   * orientation.
   *
   * @param points the corners of the polygon in boundary order
   * @return {@link F#NIL} if the test isn't decidable
   */
  public static IExpr convexPointCycleQ(IAST points, EvalEngine engine) {
    int size = points.argSize();
    if (size < 3) {
      return F.NIL;
    }
    boolean positive = false;
    boolean negative = false;
    for (int i = 1; i <= size; i++) {
      IExpr a = points.get(i);
      IExpr b = points.get(i % size + 1);
      IExpr c = points.get((i + 1) % size + 1);
      if (!a.isList2() || !b.isList2() || !c.isList2()) {
        return F.NIL;
      }
      IExpr cross = engine.evaluate(F.Subtract(
          F.Times(F.Subtract(((IAST) b).arg1(), ((IAST) a).arg1()),
              F.Subtract(((IAST) c).arg2(), ((IAST) a).arg2())),
          F.Times(F.Subtract(((IAST) b).arg2(), ((IAST) a).arg2()),
              F.Subtract(((IAST) c).arg1(), ((IAST) a).arg1()))));
      if (cross.isPositiveResult()) {
        positive = true;
      } else if (cross.isNegativeResult()) {
        negative = true;
      } else if (!cross.isZero()) {
        return F.NIL;
      }
    }
    return F.booleSymbol(!(positive && negative));
  }

  /**
   * Test if a mesh region is convex: in 2D all turns along the boundary have the same orientation,
   * in 3D every vertex lies on the inner side of every boundary face.
   *
   * @return {@link F#NIL} if the test isn't decidable
   */
  public static IExpr convexQ(IAST meshRegion, EvalEngine engine) {
    int embeddingDimension = embeddingDimension(meshRegion);
    IAST coordinates = meshCoordinates(meshRegion);
    if (embeddingDimension == 2) {
      IAST cycle = boundaryCycle(meshRegion);
      if (cycle.isNIL()) {
        return F.NIL;
      }
      IASTAppendable points = F.ListAlloc(cycle.argSize());
      for (int i = 1; i < cycle.size(); i++) {
        points.append(coordinates.get(cycle.get(i).toIntDefault()));
      }
      return convexPointCycleQ(points, engine);
    }
    if (embeddingDimension == 3) {
      List<IAST[]> triangles = boundaryTriangles(meshRegion);
      if (triangles == null) {
        return F.NIL;
      }
      for (IAST[] triangle : triangles) {
        for (int i = 1; i < coordinates.size(); i++) {
          IExpr side = engine.evaluate(outwardHalfSpace(triangle, (IAST) coordinates.get(i)));
          if (side.isPositiveResult()) {
            return S.False;
          }
          if (!side.isNegativeResult() && !side.isZero()) {
            return F.NIL;
          }
        }
      }
      return S.True;
    }
    return F.NIL;
  }

  /** The coordinate wise minimum and maximum of the mesh coordinates. */
  public static IExpr bounds(IAST meshRegion, EvalEngine engine) {
    IAST coordinates = meshCoordinates(meshRegion);
    int embeddingDimension = embeddingDimension(meshRegion);
    IASTAppendable result = F.ListAlloc(embeddingDimension);
    for (int j = 1; j <= embeddingDimension; j++) {
      IASTAppendable minimum = F.ast(S.Min, coordinates.argSize());
      IASTAppendable maximum = F.ast(S.Max, coordinates.argSize());
      for (int i = 1; i < coordinates.size(); i++) {
        IExpr coordinate = ((IAST) coordinates.get(i)).get(j);
        minimum.append(coordinate);
        maximum.append(coordinate);
      }
      result.append(F.list(engine.evaluate(minimum), engine.evaluate(maximum)));
    }
    return result;
  }

  /** The boundary of a mesh region as a <code>MeshRegion</code> of its boundary cells. */
  public static IExpr regionBoundary(IAST meshRegion) {
    if (!isMeshRegion(meshRegion)) {
      return F.NIL;
    }
    int embeddingDimension = embeddingDimension(meshRegion);
    IAST cells = boundaryCells(meshRegion, embeddingDimension - 1);
    if (cells.isNIL()) {
      return F.NIL;
    }
    return F.ternaryAST3(S.MeshRegion, meshCoordinates(meshRegion), cells,
        F.Rule(S.Method, F.list(F.Rule(F.stringx("SeparateBoundaries"), S.False))));
  }

  /**
   * The graphics representation of a mesh region: a <code>GraphicsComplex</code> of the mesh
   * coordinates and its boundary cells.
   */
  public static IExpr toGraphicsComplex(IAST meshRegion) {
    if (!isMeshRegion(meshRegion)) {
      return F.NIL;
    }
    int embeddingDimension = embeddingDimension(meshRegion);
    IAST cells = boundaryCells(meshRegion, embeddingDimension - 1);
    if (cells.isNIL() || cells.argSize() == 0) {
      return F.NIL;
    }
    return F.GraphicsComplex(meshCoordinates(meshRegion), cells);
  }

  private static class BoundaryMeshRegion extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr coordinates = ast.arg1();
      if (!coordinates.isListOfLists() || coordinates.argSize() == 0) {
        // `1` should be a non-empty list of points.
        return Errors.printMessage(ast.topHead(), "pts", F.list(coordinates), engine);
      }
      if (!validate(ast, null, null)) {
        // `1` is not a valid mesh cell specification.
        return Errors.printMessage(ast.topHead(), "mcell", F.list(ast.arg2()), engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }
  }

  private static class BoundaryMeshRegionQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.booleSymbol(isBoundaryMeshRegion(ast.arg1()));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class MeshRegionQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.booleSymbol(isMeshRegion(ast.arg1()));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class MeshCoordinates extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (isMeshRegion(arg1)) {
        return meshCoordinates((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class MeshCells extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!isMeshRegion(arg1)) {
        return F.NIL;
      }
      IAST meshRegion = (IAST) arg1;
      IExpr spec = ast.arg2();
      if (spec == S.All) {
        IASTAppendable result = F.ListAlloc();
        for (int d = 0; d < embeddingDimension(meshRegion); d++) {
          IAST cells = meshCells(meshRegion, d);
          if (cells.isPresent()) {
            result.appendArgs(cells);
          }
        }
        return result;
      }
      int dimension = spec.toIntDefault();
      if (dimension >= 0) {
        return meshCells(meshRegion, dimension);
      }
      if (spec.isList2()) {
        dimension = ((IAST) spec).arg1().toIntDefault();
        if (dimension < 0) {
          return F.NIL;
        }
        IAST cells = meshCells(meshRegion, dimension);
        if (cells.isNIL()) {
          return F.NIL;
        }
        IExpr indexSpec = ((IAST) spec).arg2();
        if (indexSpec == S.All) {
          return cells;
        }
        int index = indexSpec.toIntDefault();
        if (index >= 1 && index <= cells.argSize()) {
          return cells.get(index);
        }
        return F.NIL;
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class MeshCellCount extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!isMeshRegion(arg1)) {
        return F.NIL;
      }
      IAST meshRegion = (IAST) arg1;
      if (ast.isAST2()) {
        int dimension = ast.arg2().toIntDefault();
        if (dimension < 0) {
          return F.NIL;
        }
        IAST cells = meshCells(meshRegion, dimension);
        return cells.isPresent() ? F.ZZ(cells.argSize()) : F.NIL;
      }
      int embeddingDimension = embeddingDimension(meshRegion);
      // the cells of full dimension are only counted if the region stores them explicitly; a
      // BoundaryMeshRegion only knows about its boundary cells
      IAST fullDimensional = boundaryCells(meshRegion, embeddingDimension);
      int maxDimension =
          fullDimensional.isPresent() && fullDimensional.argSize() > 0 ? embeddingDimension
              : embeddingDimension - 1;
      IASTAppendable result = F.ListAlloc(maxDimension + 1);
      for (int d = 0; d <= maxDimension; d++) {
        IAST cells = meshCells(meshRegion, d);
        result.append(F.ZZ(cells.isPresent() ? cells.argSize() : 0));
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * The cells of a mesh region with the coordinate indices replaced by the coordinates themselves.
   */
  private static class MeshPrimitives extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!isMeshRegion(arg1)) {
        return F.NIL;
      }
      IAST meshRegion = (IAST) arg1;
      int dimension = ast.arg2().toIntDefault();
      if (dimension < 0) {
        return F.NIL;
      }
      IAST cells = meshCells(meshRegion, dimension);
      if (cells.isNIL()) {
        return F.NIL;
      }
      IAST coordinates = meshCoordinates(meshRegion);
      IASTAppendable result = F.ListAlloc(cells.argSize());
      for (int i = 1; i < cells.size(); i++) {
        IAST cell = (IAST) cells.get(i);
        IAST indices = (IAST) cell.first();
        IASTAppendable points = F.ListAlloc(indices.argSize());
        for (int j = 1; j < indices.size(); j++) {
          int index = indices.get(j).toIntDefault();
          if (index < 1 || index > coordinates.argSize()) {
            return F.NIL;
          }
          points.append(coordinates.get(index));
        }
        // a Point primitive holds a single coordinate, not a list of coordinates
        result.append(F.unaryAST1(cell.head(),
            dimension == 0 && points.argSize() == 1 ? points.arg1() : points));
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>ArrayMesh(array)</code> builds a mesh region from the non-zero entries of an array of
   * <code>0</code>s and <code>1</code>s. Only the grid points which are used by a cell become
   * coordinates of the mesh.
   */
  private static class ArrayMesh extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isList() || arg1.argSize() == 0) {
        return F.NIL;
      }
      IAST array = (IAST) arg1;
      if (array.arg1().isList()) {
        return mesh2D(array);
      }
      return mesh1D(array);
    }

    private IExpr mesh1D(IAST array) {
      int n = array.argSize();
      // collect the grid points which are used by a cell
      boolean[] used = new boolean[n + 1];
      boolean any = false;
      for (int i = 1; i <= n; i++) {
        int value = array.get(i).toIntDefault();
        if (value == Integer.MIN_VALUE) {
          return F.NIL;
        }
        if (value != 0) {
          used[i - 1] = true;
          used[i] = true;
          any = true;
        }
      }
      if (!any) {
        return F.EmptyRegion(F.C1);
      }
      int[] indexOf = new int[n + 1];
      IASTAppendable coordinates = F.ListAlloc(n + 1);
      for (int i = 0; i <= n; i++) {
        if (used[i]) {
          coordinates.append(F.list(F.num(i)));
          indexOf[i] = coordinates.argSize();
        }
      }
      IASTAppendable lines = F.ListAlloc(n);
      for (int i = 1; i <= n; i++) {
        if (array.get(i).toIntDefault() != 0) {
          lines.append(F.list(F.ZZ(indexOf[i - 1]), F.ZZ(indexOf[i])));
        }
      }
      return F.binaryAST2(S.MeshRegion, coordinates, F.list(F.Line(lines)));
    }

    private IExpr mesh2D(IAST array) {
      int rows = array.argSize();
      int columns = array.arg1().argSize();
      for (int i = 1; i <= rows; i++) {
        if (!array.get(i).isList() || array.get(i).argSize() != columns) {
          return F.NIL;
        }
      }
      // the grid point (i,j) is the upper left corner of the cell (i,j)
      boolean[][] used = new boolean[rows + 1][columns + 1];
      boolean any = false;
      for (int i = 1; i <= rows; i++) {
        IAST row = (IAST) array.get(i);
        for (int j = 1; j <= columns; j++) {
          int value = row.get(j).toIntDefault();
          if (value == Integer.MIN_VALUE) {
            return F.NIL;
          }
          if (value != 0) {
            used[i - 1][j - 1] = true;
            used[i - 1][j] = true;
            used[i][j - 1] = true;
            used[i][j] = true;
            any = true;
          }
        }
      }
      if (!any) {
        return F.EmptyRegion(F.C2);
      }
      int[][] indexOf = new int[rows + 1][columns + 1];
      IASTAppendable coordinates = F.ListAlloc((rows + 1) * (columns + 1));
      for (int i = 0; i <= rows; i++) {
        for (int j = 0; j <= columns; j++) {
          if (used[i][j]) {
            // rows run from top to bottom, so the y coordinate is flipped
            coordinates.append(F.list(F.num(j), F.num(rows - i)));
            indexOf[i][j] = coordinates.argSize();
          }
        }
      }
      IASTAppendable polygons = F.ListAlloc(rows * columns);
      for (int i = 1; i <= rows; i++) {
        IAST row = (IAST) array.get(i);
        for (int j = 1; j <= columns; j++) {
          if (row.get(j).toIntDefault() != 0) {
            polygons.append(F.List(//
                F.ZZ(indexOf[i][j - 1]), F.ZZ(indexOf[i][j]), //
                F.ZZ(indexOf[i - 1][j]), F.ZZ(indexOf[i - 1][j - 1])));
          }
        }
      }
      return F.binaryAST2(S.MeshRegion, coordinates, F.list(F.Polygon(polygons)));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>CantorMesh(n)</code> is the <code>n</code>th step of the Cantor set construction, which
   * repeatedly removes the middle third of every remaining interval. <code>CantorMesh(n, d)</code>
   * is the <code>d</code>-dimensional cartesian product of it.
   */
  private static class CantorMesh extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int n = ast.arg1().toIntDefault();
      if (n < 0 || n > 15) {
        return F.NIL;
      }
      int d = ast.isAST2() ? ast.arg2().toIntDefault() : 1;
      if (d < 1 || d > 2) {
        return F.NIL;
      }
      double[] endPoints = cantorEndPoints(n);
      return d == 1 ? mesh1D(endPoints) : mesh2D(endPoints);
    }

    /**
     * The <code>2^(n+1)</code> interval end points of the <code>n</code>th Cantor step, in
     * increasing order.
     */
    private static double[] cantorEndPoints(int n) {
      double[] endPoints = new double[] {0.0, 1.0};
      for (int step = 0; step < n; step++) {
        double[] next = new double[endPoints.length * 2];
        for (int i = 0; i < endPoints.length; i += 2) {
          double lower = endPoints[i];
          double upper = endPoints[i + 1];
          double third = (upper - lower) / 3.0;
          next[2 * i] = lower;
          next[2 * i + 1] = lower + third;
          next[2 * i + 2] = upper - third;
          next[2 * i + 3] = upper;
        }
        endPoints = next;
      }
      return endPoints;
    }

    private static IExpr mesh1D(double[] endPoints) {
      IASTAppendable coordinates = F.ListAlloc(endPoints.length);
      for (double endPoint : endPoints) {
        coordinates.append(F.list(F.num(endPoint)));
      }
      IASTAppendable lines = F.ListAlloc(endPoints.length / 2);
      for (int i = 0; i < endPoints.length; i += 2) {
        lines.append(F.list(F.ZZ(i + 1), F.ZZ(i + 2)));
      }
      return F.binaryAST2(S.MeshRegion, coordinates, F.list(F.Line(lines)));
    }

    private static IExpr mesh2D(double[] endPoints) {
      int m = endPoints.length;
      IASTAppendable coordinates = F.ListAlloc(m * m);
      for (int i = 0; i < m; i++) {
        for (int j = 0; j < m; j++) {
          coordinates.append(F.list(F.num(endPoints[i]), F.num(endPoints[j])));
        }
      }
      IASTAppendable polygons = F.ListAlloc((m / 2) * (m / 2));
      for (int i = 0; i < m; i += 2) {
        for (int j = 0; j < m; j += 2) {
          // counterclockwise around the cell [x_i, x_i+1] x [y_j, y_j+1]
          polygons.append(F.List(//
              F.ZZ(i * m + j + 1), //
              F.ZZ((i + 1) * m + j + 1), //
              F.ZZ((i + 1) * m + j + 2), //
              F.ZZ(i * m + j + 2)));
        }
      }
      return F.binaryAST2(S.MeshRegion, coordinates, F.list(F.Polygon(polygons)));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * The Delaunay triangulation of a list of two dimensional points, computed with the Bowyer-Watson
   * algorithm. The coordinates of the result are the given points, so they keep their exact values
   * even though the triangulation itself is determined numerically.
   */
  private static class DelaunayMesh extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isListOfLists() || arg1.argSize() < 3) {
        return F.NIL;
      }
      IAST points = (IAST) arg1;
      int n = points.argSize();
      double[] x = new double[n];
      double[] y = new double[n];
      for (int i = 1; i <= n; i++) {
        if (!points.get(i).isList2()) {
          return F.NIL;
        }
        x[i - 1] = points.get(i).first().evalfNaN();
        y[i - 1] = points.get(i).second().evalfNaN();
        if (Double.isNaN(x[i - 1]) || Double.isNaN(y[i - 1])) {
          return F.NIL;
        }
      }

      List<int[]> triangles = triangulate(x, y);
      if (triangles == null || triangles.isEmpty()) {
        return F.NIL;
      }
      IASTAppendable cells = F.ListAlloc(triangles.size());
      for (int[] triangle : triangles) {
        cells.append(F.list(F.ZZ(triangle[0] + 1), F.ZZ(triangle[1] + 1), F.ZZ(triangle[2] + 1)));
      }
      IAST result = F.binaryAST2(S.MeshRegion, points, F.list(F.Polygon(cells)));
      if (exactCoordinates(points)) {
        return F.ast(
            new IExpr[] {points, F.list(F.Polygon(cells)), F.Rule(S.WorkingPrecision, F.CInfinity)},
            S.MeshRegion);
      }
      return result;
    }

    /** <code>True</code> if none of the coordinates is a machine or arbitrary precision number. */
    private static boolean exactCoordinates(IAST points) {
      for (int i = 1; i < points.size(); i++) {
        IAST point = (IAST) points.get(i);
        for (int j = 1; j < point.size(); j++) {
          if (point.get(j).isInexactNumber()) {
            return false;
          }
        }
      }
      return true;
    }

    /**
     * Bowyer-Watson: start from a triangle which contains all points, then insert the points one
     * after the other and re-triangulate the hole of all triangles whose circumcircle contains the
     * new point.
     *
     * @return the corner indices of the triangles, or <code>null</code> if all points are collinear
     */
    private static List<int[]> triangulate(double[] x, double[] y) {
      int n = x.length;
      double minX = x[0], maxX = x[0], minY = y[0], maxY = y[0];
      for (int i = 1; i < n; i++) {
        minX = Math.min(minX, x[i]);
        maxX = Math.max(maxX, x[i]);
        minY = Math.min(minY, y[i]);
        maxY = Math.max(maxY, y[i]);
      }
      double dx = maxX - minX;
      double dy = maxY - minY;
      double delta = Math.max(dx, dy);
      if (delta <= 0.0) {
        return null;
      }
      double midX = (minX + maxX) / 2.0;
      double midY = (minY + maxY) / 2.0;

      // three additional points forming a triangle around all input points
      double[] px = new double[n + 3];
      double[] py = new double[n + 3];
      System.arraycopy(x, 0, px, 0, n);
      System.arraycopy(y, 0, py, 0, n);
      px[n] = midX - 20.0 * delta;
      py[n] = midY - delta;
      px[n + 1] = midX;
      py[n + 1] = midY + 20.0 * delta;
      px[n + 2] = midX + 20.0 * delta;
      py[n + 2] = midY - delta;

      List<int[]> triangles = new ArrayList<int[]>();
      triangles.add(new int[] {n, n + 1, n + 2});

      for (int i = 0; i < n; i++) {
        List<int[]> bad = new ArrayList<int[]>();
        for (int t = triangles.size() - 1; t >= 0; t--) {
          int[] triangle = triangles.get(t);
          if (inCircumcircle(px, py, triangle, px[i], py[i])) {
            bad.add(triangle);
            triangles.remove(t);
          }
        }
        // the boundary of the hole are the edges which belong to exactly one bad triangle
        List<int[]> boundary = new ArrayList<int[]>();
        for (int[] triangle : bad) {
          for (int e = 0; e < 3; e++) {
            int from = triangle[e];
            int to = triangle[(e + 1) % 3];
            boolean shared = false;
            for (int[] other : bad) {
              if (other != triangle && hasEdge(other, from, to)) {
                shared = true;
                break;
              }
            }
            if (!shared) {
              boundary.add(new int[] {from, to});
            }
          }
        }
        for (int[] edge : boundary) {
          triangles.add(new int[] {edge[0], edge[1], i});
        }
      }

      List<int[]> result = new ArrayList<int[]>();
      for (int[] triangle : triangles) {
        if (triangle[0] < n && triangle[1] < n && triangle[2] < n) {
          // orient the triangle counterclockwise
          double area = (px[triangle[1]] - px[triangle[0]]) * (py[triangle[2]] - py[triangle[0]])
              - (px[triangle[2]] - px[triangle[0]]) * (py[triangle[1]] - py[triangle[0]]);
          if (area < 0.0) {
            int swap = triangle[1];
            triangle[1] = triangle[2];
            triangle[2] = swap;
          }
          // rotate the counterclockwise corners so that the smallest index comes first
          int smallest = 0;
          for (int e = 1; e < 3; e++) {
            if (triangle[e] < triangle[smallest]) {
              smallest = e;
            }
          }
          result.add(new int[] {triangle[smallest], triangle[(smallest + 1) % 3],
              triangle[(smallest + 2) % 3]});
        }
      }
      // a deterministic order, independent of the insertion sequence
      result.sort((a, b) -> {
        int[] sortedA = a.clone();
        int[] sortedB = b.clone();
        java.util.Arrays.sort(sortedA);
        java.util.Arrays.sort(sortedB);
        for (int i = 0; i < 3; i++) {
          if (sortedA[i] != sortedB[i]) {
            return Integer.compare(sortedA[i], sortedB[i]);
          }
        }
        return 0;
      });
      return result;
    }

    private static boolean hasEdge(int[] triangle, int from, int to) {
      for (int e = 0; e < 3; e++) {
        int a = triangle[e];
        int b = triangle[(e + 1) % 3];
        if ((a == from && b == to) || (a == to && b == from)) {
          return true;
        }
      }
      return false;
    }

    private static boolean inCircumcircle(double[] px, double[] py, int[] triangle, double x,
        double y) {
      double ax = px[triangle[0]] - x;
      double ay = py[triangle[0]] - y;
      double bx = px[triangle[1]] - x;
      double by = py[triangle[1]] - y;
      double cx = px[triangle[2]] - x;
      double cy = py[triangle[2]] - y;
      double determinant = (ax * ax + ay * ay) * (bx * cy - by * cx) //
          - (bx * bx + by * by) * (ax * cy - ay * cx) //
          + (cx * cx + cy * cy) * (ax * by - ay * bx);
      double orientation = (px[triangle[1]] - px[triangle[0]]) * (py[triangle[2]] - py[triangle[0]])
          - (px[triangle[2]] - px[triangle[0]]) * (py[triangle[1]] - py[triangle[0]]);
      return orientation > 0.0 ? determinant > 0.0 : determinant < 0.0;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private MeshFunctions() {}
}
