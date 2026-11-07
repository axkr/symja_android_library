package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>FindGraphCommunities(graph)</code> - the communities of <code>graph</code> of the largest
 * modularity: sets of vertices with many edges inside and few between them.
 *
 * <p>
 * The communities are found the Louvain way. Every vertex in the order of the vertex list moves to
 * the neighbouring community which raises the modularity most, until none moves; the communities
 * then become the vertices of a smaller weighted graph, and the moving starts over, until a round
 * moves nothing. The directions of the edges are ignored.
 *
 * <p>
 * Each community lists its vertices in the order of the vertex list; the communities come largest
 * first, and communities of the same size in the order of their first vertices.
 */
public class FindGraphCommunities extends AbstractFunctionEvaluator {

  /** Gains which differ by less than this are a tie, which keeps the vertex where it is. */
  private static final double TOLERANCE = 1.0e-12;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    @SuppressWarnings("unchecked")
    Graph<IExpr, Object> g = (Graph<IExpr, Object>) gex.toData();
    List<IExpr> vertices = new ArrayList<IExpr>(g.vertexSet());
    Map<IExpr, Integer> index = new HashMap<IExpr, Integer>();
    for (int i = 0; i < vertices.size(); i++) {
      index.put(vertices.get(i), i);
    }
    List<int[]> edges = new ArrayList<int[]>();
    List<Double> edgeWeights = new ArrayList<Double>();
    for (Object edge : g.edgeSet()) {
      // an unweighted graph gives every edge the weight 1
      double weight = g.getEdgeWeight(edge);
      if (weight > 0.0 && Double.isFinite(weight)) {
        edges.add(new int[] {index.get(g.getEdgeSource(edge)), index.get(g.getEdgeTarget(edge))});
        edgeWeights.add(weight);
      }
    }
    List<List<Integer>> communities = communities(vertices.size(), edges, edgeWeights, engine);
    IASTAppendable result = F.ListAlloc(communities.size());
    for (List<Integer> community : communities) {
      IASTAppendable members = F.ListAlloc(community.size());
      for (int v : community) {
        members.append(vertices.get(v));
      }
      result.append(members);
    }
    return result;
  }

  /**
   * The communities of the graph of <code>n</code> vertices and the <code>edges</code> of the
   * positive <code>weights</code>, each a sorted list of vertex indices, largest first and ties by
   * their first vertex. A loop adds twice its weight inside its vertex.
   */
  static List<List<Integer>> communities(int n, List<int[]> edges, List<Double> weightOf,
      EvalEngine engine) {
    // the weighted graph being clustered, and which vertices each of its nodes stands for
    List<List<Integer>> members = new ArrayList<List<Integer>>(n);
    List<TreeMap<Integer, Double>> weights = new ArrayList<TreeMap<Integer, Double>>(n);
    for (int v = 0; v < n; v++) {
      List<Integer> single = new ArrayList<Integer>(1);
      single.add(v);
      members.add(single);
      weights.add(new TreeMap<Integer, Double>());
    }
    double twoM = 0.0;
    // the weight of the edges inside each node: the loops, then those of the earlier rounds
    double[] selfWeight = new double[n];
    for (int k = 0; k < edges.size(); k++) {
      int[] e = edges.get(k);
      double w = weightOf.get(k);
      if (e[0] != e[1]) {
        weights.get(e[0]).merge(e[1], w, Double::sum);
        weights.get(e[1]).merge(e[0], w, Double::sum);
      } else {
        selfWeight[e[0]] += 2.0 * w;
      }
      twoM += 2.0 * w;
    }
    if (twoM > 0.0) {
      while (true) {
        int nodes = members.size();
        double[] degree = new double[nodes];
        for (int i = 0; i < nodes; i++) {
          degree[i] = selfWeight[i];
          for (double w : weights.get(i).values()) {
            degree[i] += w;
          }
        }
        int[] community = new int[nodes];
        double[] total = degree.clone();
        for (int i = 0; i < nodes; i++) {
          community[i] = i;
        }
        if (!moveNodes(weights, degree, community, total, twoM, engine)) {
          break;
        }
        // one node for every community, numbered in the order of their first nodes
        Map<Integer, Integer> renumbered = new LinkedHashMap<Integer, Integer>();
        for (int c : community) {
          renumbered.putIfAbsent(c, renumbered.size());
        }
        int k = renumbered.size();
        List<List<Integer>> newMembers = new ArrayList<List<Integer>>(k);
        List<TreeMap<Integer, Double>> newWeights = new ArrayList<TreeMap<Integer, Double>>(k);
        for (int c = 0; c < k; c++) {
          newMembers.add(new ArrayList<Integer>());
          newWeights.add(new TreeMap<Integer, Double>());
        }
        double[] newSelf = new double[k];
        for (int i = 0; i < nodes; i++) {
          int ci = renumbered.get(community[i]);
          newMembers.get(ci).addAll(members.get(i));
          newSelf[ci] += selfWeight[i];
          for (Map.Entry<Integer, Double> link : weights.get(i).entrySet()) {
            int cj = renumbered.get(community[link.getKey()]);
            if (ci == cj) {
              newSelf[ci] += link.getValue();
            } else {
              newWeights.get(ci).merge(cj, link.getValue(), Double::sum);
            }
          }
        }
        members = newMembers;
        weights = newWeights;
        selfWeight = newSelf;
      }
    }
    for (List<Integer> m : members) {
      m.sort(null);
    }
    members.sort((a, b) -> a.size() != b.size() ? Integer.compare(b.size(), a.size())
        : Integer.compare(a.get(0), b.get(0)));
    return members;
  }

  /**
   * Move every node to the neighbouring community which raises the modularity most, round after
   * round until none moves.
   *
   * @param total the summed degree of every community, kept up to date
   * @return whether any node moved
   */
  private static boolean moveNodes(List<TreeMap<Integer, Double>> weights, double[] degree,
      int[] community, double[] total, double twoM, EvalEngine engine) {
    int nodes = community.length;
    boolean movedAny = false;
    boolean moved = true;
    while (moved) {
      engine.checkTimeLimit();
      moved = false;
      for (int i = 0; i < nodes; i++) {
        int own = community[i];
        total[own] -= degree[i];
        // the weight of the links from i into each neighbouring community
        TreeMap<Integer, Double> links = new TreeMap<Integer, Double>();
        for (Map.Entry<Integer, Double> link : weights.get(i).entrySet()) {
          links.merge(community[link.getKey()], link.getValue(), Double::sum);
        }
        int best = own;
        double bestGain = links.getOrDefault(own, 0.0) - total[own] * degree[i] / twoM;
        for (Map.Entry<Integer, Double> link : links.entrySet()) {
          int c = link.getKey();
          double gain = link.getValue() - total[c] * degree[i] / twoM;
          if (gain > bestGain + TOLERANCE) {
            best = c;
            bestGain = gain;
          }
        }
        total[best] += degree[i];
        if (best != own) {
          community[i] = best;
          moved = true;
          movedAny = true;
        }
      }
    }
    return movedAny;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
