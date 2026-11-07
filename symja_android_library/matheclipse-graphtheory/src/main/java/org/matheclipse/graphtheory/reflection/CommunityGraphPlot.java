package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
import org.matheclipse.graphtheory.graphics.GraphGraphics;

/**
 * <code>CommunityGraphPlot(g)</code> and <code>CommunityGraphPlot(g, {c1, c2, ...})</code> - the
 * graph drawn community by community: each community is laid out on its own, its vertices are
 * coloured alike and a grey outline is drawn round it. Without a list of communities the ones
 * <code>FindGraphCommunities(g)</code> gives are drawn.
 *
 * <p>
 * The options <code>CommunityBoundaryStyle</code> (the outline, a grey line by default) and
 * <code>CommunityRegionStyle</code> (the inside of the outline, not filled by default) style the
 * outlines; every other option is passed on to <code>GraphPlot</code>.
 */
public class CommunityGraphPlot extends AbstractEvaluator {

  /** The hue, saturation and brightness of the first communities. */
  private static final double[][] COMMUNITY_COLORS = {{0.0, 1.0, 0.8}, {0.14, 1.0, 0.9},
      {0.8, 0.6, 0.8}, {0.07, 1.0, 1.0}, {0.2, 1.0, 0.6}, {0.1, 0.6, 0.7}, {0.5, 1.0, 0.7}};

  /** The mean length of an edge inside a community. */
  private static final double EDGE_LENGTH = 0.7;

  /** How far the outline of a community keeps from its vertices. */
  private static final double OUTLINE_DISTANCE = 0.35;

  /** How many points of the circle round a vertex the outline is drawn through. */
  private static final int OUTLINE_SAMPLES = 24;

  /** The most vertices drawn; a bigger graph is left unevaluated. */
  private static final int MAX_VERTICES = 2000;

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.argSize() < 1) {
      return F.NIL;
    }
    GraphExpr<?> graphExpr = GraphExpr.newInstance(ast.arg1());
    if (graphExpr == null || graphExpr.toData().vertexSet().size() > MAX_VERTICES) {
      return F.NIL;
    }
    int firstOption = 2;
    IExpr communities;
    if (ast.argSize() >= 2 && !ast.arg2().isRuleAST()) {
      communities = ast.arg2();
      firstOption = 3;
    } else {
      communities = engine.evaluate(F.unaryAST1(S.FindGraphCommunities, graphExpr));
    }
    if (!communities.isListOfLists() && !communities.isEmptyList()) {
      return F.NIL;
    }
    IExpr boundaryStyle = F.Directive(F.GrayLevel(0.5));
    IExpr regionStyle = S.None;
    IASTAppendable plotOptions = F.ListAlloc();
    for (int i = firstOption; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST()) {
        return F.NIL;
      }
      if (isOption(option, "CommunityBoundaryStyle")) {
        boundaryStyle = option.second();
      } else if (isOption(option, "CommunityRegionStyle")) {
        regionStyle = option.second();
      } else {
        plotOptions.append(option);
      }
    }
    return plot(graphExpr, graphExpr.toData(), (IAST) communities, boundaryStyle, regionStyle,
        plotOptions, engine);
  }

  /** Whether <code>option</code> is a rule for the option called <code>name</code>. */
  private static boolean isOption(IExpr option, String name) {
    // the relaxed syntax lowercases the name of a symbol it does not know
    return option.first().isSymbol()
        && ((ISymbol) option.first()).getSymbolName().equalsIgnoreCase(name);
  }

  private static <E> IExpr plot(GraphExpr<?> graphExpr, Graph<IExpr, E> graph, IAST given,
      IExpr boundaryStyle, IExpr regionStyle, IAST plotOptions, EvalEngine engine) {
    List<Set<IExpr>> communities = new ArrayList<Set<IExpr>>();
    Map<IExpr, Integer> communityOf = new HashMap<IExpr, Integer>();
    for (IExpr list : given) {
      Set<IExpr> community = new LinkedHashSet<IExpr>();
      for (IExpr vertex : (IAST) list) {
        if (!graph.containsVertex(vertex)) {
          return F.NIL;
        }
        // a vertex belongs to the first community naming it
        if (!communityOf.containsKey(vertex)) {
          communityOf.put(vertex, communities.size());
          community.add(vertex);
        }
      }
      if (!community.isEmpty()) {
        communities.add(community);
      }
    }
    // the vertices no community names stand on their own
    for (IExpr vertex : graph.vertexSet()) {
      if (!communityOf.containsKey(vertex)) {
        communityOf.put(vertex, communities.size());
        communities.add(new LinkedHashSet<IExpr>(Arrays.asList(vertex)));
      }
    }
    int k = communities.size();

    // each community laid out on its own, centred, with edges about EDGE_LENGTH long
    List<Map<IExpr, double[]>> locals = new ArrayList<Map<IExpr, double[]>>(k);
    double biggest = 0.0;
    for (Set<IExpr> community : communities) {
      engine.checkTimeLimit();
      Map<IExpr, double[]> local = layout(new AsSubgraph<IExpr, E>(graph, community));
      double sum = 0.0;
      int count = 0;
      for (E edge : graph.edgeSet()) {
        double[] p = local.get(graph.getEdgeSource(edge));
        double[] q = local.get(graph.getEdgeTarget(edge));
        if (p != null && q != null && p != q) {
          sum += Math.hypot(p[0] - q[0], p[1] - q[1]);
          count++;
        }
      }
      double scale = count == 0 || sum < 1.0e-12 ? 1.0 : EDGE_LENGTH * count / sum;
      double[] centre = centroid(local.values());
      for (double[] p : local.values()) {
        p[0] = (p[0] - centre[0]) * scale;
        p[1] = (p[1] - centre[1]) * scale;
        biggest = Math.max(biggest, Math.hypot(p[0], p[1]));
      }
      locals.add(local);
    }

    // the communities placed as the vertices of the graph of communities, which has an edge
    // wherever an edge of g joins two of them, far enough apart that the outlines never touch
    Graph<IExpr, DefaultEdge> quotient = new SimpleGraph<IExpr, DefaultEdge>(DefaultEdge.class);
    for (int c = 0; c < k; c++) {
      quotient.addVertex(F.ZZ(c));
    }
    for (E edge : graph.edgeSet()) {
      int a = communityOf.get(graph.getEdgeSource(edge));
      int b = communityOf.get(graph.getEdgeTarget(edge));
      if (a != b) {
        quotient.addEdge(F.ZZ(a), F.ZZ(b));
      }
    }
    Map<IExpr, double[]> centres = layout(quotient);
    double closest = Double.MAX_VALUE;
    for (int i = 0; i < k; i++) {
      double[] p = centres.get(F.ZZ(i));
      for (int j = i + 1; j < k; j++) {
        double[] q = centres.get(F.ZZ(j));
        double d = Math.hypot(p[0] - q[0], p[1] - q[1]);
        if (d > 1.0e-12) {
          closest = Math.min(closest, d);
        }
      }
    }
    double spacing = Math.max(2.6, 2.0 * (biggest + OUTLINE_DISTANCE) + 0.5);
    double spread = closest == Double.MAX_VALUE ? 1.0 : spacing / closest;

    // each community turned so the vertices with edges to another community face it
    Map<IExpr, double[]> positions = new HashMap<IExpr, double[]>();
    for (int c = 0; c < k; c++) {
      Map<IExpr, double[]> local = locals.get(c);
      double[] centre = centres.get(F.ZZ(c));
      double along = 0.0;
      double across = 0.0;
      for (E edge : graph.edgeSet()) {
        IExpr source = graph.getEdgeSource(edge);
        IExpr target = graph.getEdgeTarget(edge);
        for (int end = 0; end < 2; end++) {
          IExpr v = end == 0 ? source : target;
          int other = communityOf.get(end == 0 ? target : source);
          double[] p = local.get(v);
          if (p == null || other == c) {
            continue;
          }
          double[] towards = centres.get(F.ZZ(other));
          double dx = towards[0] - centre[0];
          double dy = towards[1] - centre[1];
          along += p[0] * dx + p[1] * dy;
          across += p[0] * dy - p[1] * dx;
        }
      }
      double angle = Math.abs(along) + Math.abs(across) > 1.0e-12 ? Math.atan2(across, along) : 0.0;
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      for (Map.Entry<IExpr, double[]> entry : local.entrySet()) {
        double x = entry.getValue()[0];
        double y = entry.getValue()[1];
        positions.put(entry.getKey(), new double[] {centre[0] * spread + cos * x - sin * y,
            centre[1] * spread + sin * x + cos * y});
      }
    }

    // the graph with these coordinates and its vertices in their community's colour, drawn by
    // GraphPlot
    IASTAppendable coordinates = F.ListAlloc(graph.vertexSet().size());
    IASTAppendable vertexStyles = F.ListAlloc(graph.vertexSet().size());
    for (IExpr vertex : graph.vertexSet()) {
      double[] p = positions.get(vertex);
      coordinates.append(F.List(F.num(p[0]), F.num(p[1])));
      IExpr color = communityColor(communityOf.get(vertex));
      vertexStyles.append(
          F.Rule(vertex, F.Directive(color, F.EdgeForm(F.List(color, F.Opacity(F.C1))))));
    }
    IASTAppendable options = F.ListAlloc();
    IAST own = graphExpr.options();
    if (own != null) {
      for (IExpr option : own) {
        if (!option.isRuleAST() || (option.first() != S.VertexCoordinates
            && option.first() != S.VertexStyle && option.first() != S.GraphLayout)) {
          options.append(option);
        }
      }
    }
    options.append(F.Rule(S.VertexCoordinates, coordinates));
    options.append(F.Rule(S.VertexStyle, vertexStyles));
    IASTAppendable graphPlot = F.ast(S.GraphPlot, plotOptions.argSize() + 1);
    graphPlot.append(GraphExpr.newInstance(graph, options));
    graphPlot.appendArgs(plotOptions);
    IExpr picture = engine.evaluate(graphPlot);
    if (!picture.isAST(S.Graphics) || !picture.first().isList()) {
      return F.NIL;
    }

    // the outlines, behind the edges and the vertices
    IASTAppendable primitives = F.ListAlloc(k + ((IAST) picture.first()).argSize());
    for (Set<IExpr> community : communities) {
      List<double[]> around = new ArrayList<double[]>(OUTLINE_SAMPLES * community.size());
      for (IExpr vertex : community) {
        double[] p = positions.get(vertex);
        for (int i = 0; i < OUTLINE_SAMPLES; i++) {
          double angle = 2.0 * Math.PI * i / OUTLINE_SAMPLES;
          around.add(new double[] {p[0] + OUTLINE_DISTANCE * Math.cos(angle),
              p[1] + OUTLINE_DISTANCE * Math.sin(angle)});
        }
      }
      IASTAppendable outline = F.ListAlloc(around.size());
      for (double[] p : convexHull(around)) {
        outline.append(F.List(F.num(p[0]), F.num(p[1])));
      }
      primitives.append(
          F.List(F.EdgeForm(boundaryStyle), F.FaceForm(regionStyle), F.Polygon(outline)));
    }
    primitives.appendArgs((IAST) picture.first());
    return ((IAST) picture).setAtCopy(1, primitives);
  }

  /** <code>Hue(h, s, b)</code> of the <code>c</code>-th community, counting from 0. */
  private static IExpr communityColor(int c) {
    if (c < COMMUNITY_COLORS.length) {
      double[] hsb = COMMUNITY_COLORS[c];
      return GraphGraphics.hue(hsb[0], hsb[1], hsb[2]);
    }
    // further hues a golden-ratio turn apart
    double hue = 0.2 + (c - COMMUNITY_COLORS.length) * (1.0 + Math.sqrt(5.0)) / 2.0;
    return GraphGraphics.hue(hue - Math.floor(hue), 1.0, 0.8);
  }

  /** The spring layout of <code>graph</code>; a single vertex at the origin. */
  private static Map<IExpr, double[]> layout(Graph<IExpr, ?> graph) {
    Map<IExpr, double[]> result = new HashMap<IExpr, double[]>();
    if (graph.vertexSet().size() == 1) {
      result.put(graph.vertexSet().iterator().next(), new double[] {0.0, 0.0});
      return result;
    }
    IAST points = GraphGraphics.vertexCoordinates(graph, "SpringEmbedding");
    int i = 1;
    for (IExpr vertex : graph.vertexSet()) {
      IExpr p = points.get(i++);
      result.put(vertex, new double[] {p.first().evalf(), p.second().evalf()});
    }
    return result;
  }

  private static double[] centroid(Iterable<double[]> points) {
    double x = 0.0;
    double y = 0.0;
    int n = 0;
    for (double[] p : points) {
      x += p[0];
      y += p[1];
      n++;
    }
    return n == 0 ? new double[] {0.0, 0.0} : new double[] {x / n, y / n};
  }

  /** The convex hull of <code>points</code>, counterclockwise (monotone chain). */
  private static List<double[]> convexHull(List<double[]> points) {
    List<double[]> sorted = new ArrayList<double[]>(points);
    sorted.sort(Comparator.<double[]>comparingDouble(p -> p[0]).thenComparingDouble(p -> p[1]));
    int n = sorted.size();
    double[][] hull = new double[2 * n][];
    int h = 0;
    for (int i = 0; i < n; i++) {
      while (h >= 2 && cross(hull[h - 2], hull[h - 1], sorted.get(i)) <= 0) {
        h--;
      }
      hull[h++] = sorted.get(i);
    }
    for (int i = n - 2, lower = h + 1; i >= 0; i--) {
      while (h >= lower && cross(hull[h - 2], hull[h - 1], sorted.get(i)) <= 0) {
        h--;
      }
      hull[h++] = sorted.get(i);
    }
    return Arrays.asList(hull).subList(0, Math.max(0, h - 1));
  }

  private static double cross(double[] o, double[] a, double[] b) {
    return (a[0] - o[0]) * (b[1] - o[1]) - (a[1] - o[1]) * (b[0] - o[0]);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
