package org.matheclipse.graphtheory.alg;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.clique.PivotBronKerboschCliqueFinder;
import org.jgrapht.generate.ComplementGraphGenerator;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.ExprEdge;

/**
 * The maximum clique search behind {@code FindClique} and {@code FindIndependentVertexSet}, and the
 * size specification both of them accept.
 *
 * <p>
 * The two are the same question asked of a graph and of its complement: an independent set of
 * <code>g</code> is a clique of the complement of <code>g</code>. Both are NP-hard, and both are
 * answered here by Bron-Kerbosch with pivoting.
 */
public final class VertexSetSearch {

  /** A parsed <code>n</code> / <code>{n}</code> / <code>{min, max}</code> size specification. */
  public static final class SizeSpec {
    public final int min;
    public final int max;

    /** <code>true</code> when the specification could not be read. */
    public final boolean invalid;

    private SizeSpec(int min, int max, boolean invalid) {
      this.min = min;
      this.max = max;
      this.invalid = invalid;
    }

    private static final SizeSpec INVALID = new SizeSpec(0, 0, true);
  }

  private VertexSetSearch() {}

  /**
   * Read the second argument of {@code FindClique} / {@code FindIndependentVertexSet}:
   * <code>n</code> means at most n, <code>{n}</code> exactly n, <code>{min, max}</code> a range, and
   * <code>Infinity</code> no upper bound at all.
   */
  public static SizeSpec sizeSpec(IExpr arg) {
    int min = 0;
    int max = Integer.MAX_VALUE;
    if (arg.isList1()) {
      min = max = arg.first().toIntDefault();
    } else if (arg.isList2()) {
      min = arg.first().toIntDefault();
      max = arg.second().toIntDefault();
    } else if (!arg.isInfinity()) {
      max = arg.toIntDefault();
    }
    if (min < 0 || max < 0 || min > max) {
      return SizeSpec.INVALID;
    }
    return new SizeSpec(min, max, false);
  }

  /** A maximum clique of the graph, or the empty set when it has no vertices. */
  public static Set<IExpr> maximumClique(Graph<IExpr, ExprEdge> graph) {
    PivotBronKerboschCliqueFinder<IExpr, ExprEdge> finder =
        new PivotBronKerboschCliqueFinder<IExpr, ExprEdge>(graph);
    Iterator<Set<IExpr>> iterator = finder.maximumIterator();
    return iterator.hasNext() ? iterator.next() : Collections.<IExpr>emptySet();
  }

  /**
   * The <code>k</code>-th distance power of the graph: the same vertices, joined whenever the
   * shortest path between them is at most <code>k</code> edges long. A maximal set of vertices all
   * within distance <code>k</code> of each other - Luce's k-clique - is exactly a maximal clique
   * here, so <code>k == 1</code> reproduces the graph itself and with it the ordinary cliques.
   */
  public static Graph<IExpr, ExprEdge> distancePower(Graph<IExpr, ExprEdge> graph, int k) {
    Graph<IExpr, ExprEdge> power = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
    Graphs.addAllVertices(power, graph.vertexSet());
    for (IExpr source : graph.vertexSet()) {
      // breadth first out of `source`, stopping at depth k
      Map<IExpr, Integer> depth = new HashMap<IExpr, Integer>();
      Deque<IExpr> queue = new ArrayDeque<IExpr>();
      depth.put(source, Integer.valueOf(0));
      queue.add(source);
      while (!queue.isEmpty()) {
        IExpr current = queue.poll();
        int next = depth.get(current).intValue() + 1;
        if (next > k) {
          continue;
        }
        for (ExprEdge edge : graph.edgesOf(current)) {
          IExpr neighbor = Graphs.getOppositeVertex(graph, edge, current);
          if (depth.containsKey(neighbor)) {
            continue;
          }
          depth.put(neighbor, Integer.valueOf(next));
          queue.add(neighbor);
          power.addEdge(source, neighbor);
        }
      }
    }
    return power;
  }

  /**
   * A maximum independent set of the graph: a maximum clique of its complement.
   */
  public static Set<IExpr> maximumIndependentSet(Graph<IExpr, ExprEdge> graph) {
    return maximumClique(complement(graph));
  }

  /**
   * The independent sets of the graph, which are the cliques of its complement, cut down to the
   * requested size and reported largest first.
   */
  public static List<Set<IExpr>> independentSets(Graph<IExpr, ExprEdge> graph, SizeSpec spec,
      int limit) {
    return kCliques(complement(graph), 1, spec, limit);
  }

  private static Graph<IExpr, ExprEdge> complement(Graph<IExpr, ExprEdge> graph) {
    Graph<IExpr, ExprEdge> complement = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
    new ComplementGraphGenerator<IExpr, ExprEdge>(graph).generateGraph(complement);
    return complement;
  }

  /**
   * The k-cliques of the graph - the maximal cliques of its k-th distance power - cut down to the
   * requested size and reported largest first.
   *
   * <p>
   * Trimming is sound because being within distance <code>k</code> of each other is inherited by
   * every subset. Different k-cliques can trim to the same set, so the results are deduplicated;
   * with no size limit they are the k-cliques themselves.
   *
   * @param limit how many to return at most; {@link Integer#MAX_VALUE} for all of them
   */
  public static List<Set<IExpr>> kCliques(Graph<IExpr, ExprEdge> graph, int k, SizeSpec spec,
      int limit) {
    Graph<IExpr, ExprEdge> power = distancePower(graph, k);
    PivotBronKerboschCliqueFinder<IExpr, ExprEdge> finder =
        new PivotBronKerboschCliqueFinder<IExpr, ExprEdge>(power);

    Set<Set<IExpr>> found = new LinkedHashSet<Set<IExpr>>();
    int examined = 0;
    for (Set<IExpr> candidate : finder) {
      if (++examined > MAX_KCLAN_CANDIDATES) {
        break;
      }
      if ((examined & 0xFF) == 0 && Thread.currentThread().isInterrupted()) {
        throw TimeoutException.TIMED_OUT;
      }
      if (candidate.size() < spec.min) {
        continue;
      }
      found.add(trim(graph, candidate, Math.min(candidate.size(), spec.max)));
    }

    return order(graph, found, limit);
  }

  /** Keep the first {@code wanted} members in the graph's own vertex order. */
  private static Set<IExpr> trim(Graph<IExpr, ExprEdge> graph, Set<IExpr> set, int wanted) {
    if (set.size() == wanted) {
      Set<IExpr> ordered = new LinkedHashSet<IExpr>();
      for (IExpr vertex : graph.vertexSet()) {
        if (set.contains(vertex)) {
          ordered.add(vertex);
        }
      }
      return ordered;
    }
    Set<IExpr> ordered = new LinkedHashSet<IExpr>();
    for (IExpr vertex : graph.vertexSet()) {
      if (ordered.size() == wanted) {
        break;
      }
      if (set.contains(vertex)) {
        ordered.add(vertex);
      }
    }
    return ordered;
  }

  /**
   * Largest set first, and among sets of one size the one whose vertices come earliest in the
   * graph's own order. Comparing the whole position sequence rather than only its first entry is
   * what makes the answer independent of the order the sets happened to be enumerated in.
   */
  private static List<Set<IExpr>> order(Graph<IExpr, ExprEdge> graph, Set<Set<IExpr>> sets,
      int limit) {
    Map<IExpr, Integer> position = new HashMap<IExpr, Integer>();
    for (IExpr vertex : graph.vertexSet()) {
      position.put(vertex, Integer.valueOf(position.size()));
    }
    List<Set<IExpr>> result = new ArrayList<Set<IExpr>>(sets);
    result.sort(Comparator.<Set<IExpr>>comparingInt(Set::size).reversed()
        .thenComparing(set -> positions(position, set), VertexSetSearch::compareLexicographically));
    return result.size() > limit ? result.subList(0, limit) : result;
  }

  private static int[] positions(Map<IExpr, Integer> position, Set<IExpr> set) {
    int[] indices = new int[set.size()];
    int i = 0;
    for (IExpr vertex : set) {
      Integer index = position.get(vertex);
      indices[i++] = index == null ? Integer.MAX_VALUE : index.intValue();
    }
    java.util.Arrays.sort(indices);
    return indices;
  }

  private static int compareLexicographically(int[] left, int[] right) {
    int n = Math.min(left.length, right.length);
    for (int i = 0; i < n; i++) {
      if (left[i] != right[i]) {
        return left[i] - right[i];
      }
    }
    return left.length - right.length;
  }

  /** How many maximal k-cliques {@link VertexSetSearch#largestKClan} will look at. */
  private static final int MAX_KCLAN_CANDIDATES = 200_000;

  /** The outcome of {@link VertexSetSearch#largestKClan} and {@link VertexSetSearch#largestKClub}. */
  public static final class SetResult {
    /** The matching sets, largest first; empty when there are none. */
    public final List<Set<IExpr>> sets;

    /** <code>false</code> when the search budget stopped the enumeration. */
    public final boolean proven;

    private SetResult(List<Set<IExpr>> sets, boolean proven) {
      this.sets = sets;
      this.proven = proven;
    }
  }

  /**
   * A largest k-clan: a k-clique whose induced subgraph has diameter at most <code>k</code>.
   *
   * <p>
   * The k-cliques are the maximal cliques of the k-th distance power, so they are enumerated there
   * and then filtered by the diameter of what they induce in the original graph. The two conditions
   * differ because a k-clique only asks that its members be within <code>k</code> steps <i>in the
   * whole graph</i>, and those paths may run through vertices the set does not contain.
   *
   * <p>
   * The size specification selects among the k-clans rather than trimming one, because a k-clan is
   * maximal by definition and a subset of one need not be a k-clan at all - dropping a vertex can
   * lengthen or even break the paths inside the set.
   */
  public static SetResult largestKClan(Graph<IExpr, ExprEdge> graph, int k, SizeSpec spec) {
    return kClans(graph, k, spec, 1);
  }

  /**
   * The k-clans - the k-cliques whose induced subgraph has diameter at most <code>k</code> - largest
   * first.
   *
   * <p>
   * Nothing is trimmed. A k-clan is maximal by definition and a subset of one need not be a k-clan
   * at all, since dropping a vertex can lengthen or break the paths inside the set, so the size
   * specification selects among them instead.
   */
  public static SetResult kClans(Graph<IExpr, ExprEdge> graph, int k, SizeSpec spec, int limit) {
    Graph<IExpr, ExprEdge> power = distancePower(graph, k);
    PivotBronKerboschCliqueFinder<IExpr, ExprEdge> finder =
        new PivotBronKerboschCliqueFinder<IExpr, ExprEdge>(power);

    Set<Set<IExpr>> found = new LinkedHashSet<Set<IExpr>>();
    int examined = 0;
    boolean proven = true;
    for (Set<IExpr> candidate : finder) {
      if (++examined > MAX_KCLAN_CANDIDATES) {
        proven = false;
        break;
      }
      if ((examined & 0xFF) == 0 && Thread.currentThread().isInterrupted()) {
        throw TimeoutException.TIMED_OUT;
      }
      if (candidate.size() < spec.min || candidate.size() > spec.max) {
        continue;
      }
      if (inducedDiameterAtMost(graph, candidate, k)) {
        found.add(trim(graph, candidate, candidate.size()));
      }
    }
    return new SetResult(order(graph, found, limit), proven);
  }

  public static SetResult largestKClub(Graph<IExpr, ExprEdge> graph, int k, SizeSpec spec) {
    Graph<IExpr, ExprEdge> power = distancePower(graph, k);
    PivotBronKerboschCliqueFinder<IExpr, ExprEdge> finder =
        new PivotBronKerboschCliqueFinder<IExpr, ExprEdge>(power);
    KClubSearch search = new KClubSearch(graph, k, spec);
    for (Set<IExpr> candidate : finder) {
      if (search.aborted) {
        break;
      }
      search.explore(new ArrayList<IExpr>(candidate));
    }
    List<Set<IExpr>> sets = new ArrayList<Set<IExpr>>();
    if (!search.best.isEmpty()) {
      sets.add(search.best);
    }
    return new SetResult(sets, !search.aborted);
  }

  /**
   * The k-clubs, largest first. Where {@link #largestKClub} prunes away everything that cannot beat
   * the best answer so far, this keeps every qualifying set and therefore explores far more of each
   * k-clique - which is why the two are separate.
   *
   * <p>
   * A k-club is maximal by definition, so a qualifying subset counts only when no vertex of the
   * graph at all can join it without pushing the induced diameter past <code>k</code>.
   */
  public static SetResult kClubs(Graph<IExpr, ExprEdge> graph, int k, SizeSpec spec, int limit) {
    Graph<IExpr, ExprEdge> power = distancePower(graph, k);
    PivotBronKerboschCliqueFinder<IExpr, ExprEdge> finder =
        new PivotBronKerboschCliqueFinder<IExpr, ExprEdge>(power);
    KClubEnumeration enumeration = new KClubEnumeration(graph, k, spec);
    for (Set<IExpr> candidate : finder) {
      if (enumeration.aborted) {
        break;
      }
      enumeration.explore(new ArrayList<IExpr>(candidate));
    }
    return new SetResult(order(graph, enumeration.found, limit), !enumeration.aborted);
  }

  /** Every maximal subset of a k-clique whose induced subgraph has diameter at most k. */
  private static final class KClubEnumeration {
    private final Graph<IExpr, ExprEdge> graph;
    private final int k;
    private final SizeSpec spec;
    private final List<IExpr> chosen = new ArrayList<IExpr>();
    final Set<Set<IExpr>> found = new LinkedHashSet<Set<IExpr>>();
    private long steps;
    boolean aborted;

    KClubEnumeration(Graph<IExpr, ExprEdge> graph, int k, SizeSpec spec) {
      this.graph = graph;
      this.k = k;
      this.spec = spec;
    }

    void explore(List<IExpr> candidate) {
      chosen.clear();
      expand(candidate, 0);
    }

    private void expand(List<IExpr> candidate, int index) {
      if (aborted) {
        return;
      }
      int size = chosen.size();
      if (size > 0 && size >= spec.min && size <= spec.max) {
        Set<IExpr> set = new LinkedHashSet<IExpr>(chosen);
        if (inducedDiameterAtMost(graph, set, k) && isMaximalClub(set)) {
          found.add(set);
        }
      }
      if (index == candidate.size() || size + candidate.size() - index < spec.min) {
        return;
      }
      if (++steps > ExactVertexColoring.MAX_SEARCH_NODES || found.size() > MAX_KCLAN_CANDIDATES) {
        aborted = true;
        return;
      }
      if ((steps & 0xFFF) == 0 && Thread.currentThread().isInterrupted()) {
        throw TimeoutException.TIMED_OUT;
      }

      chosen.add(candidate.get(index));
      expand(candidate, index + 1);
      chosen.remove(chosen.size() - 1);
      expand(candidate, index + 1);
    }

    /** No vertex of the graph can join the set and leave its diameter at most k. */
    private boolean isMaximalClub(Set<IExpr> set) {
      for (IExpr vertex : graph.vertexSet()) {
        if (set.contains(vertex)) {
          continue;
        }
        Set<IExpr> larger = new LinkedHashSet<IExpr>(set);
        larger.add(vertex);
        if (inducedDiameterAtMost(graph, larger, k)) {
          return false;
        }
      }
      return true;
    }
  }

  /**
   * Whether every two vertices of <code>set</code> are within <code>k</code> steps of each other
   * using only edges between members of <code>set</code>.
   */
  public static boolean inducedDiameterAtMost(Graph<IExpr, ExprEdge> graph, Set<IExpr> set, int k) {
    if (set.size() <= 1) {
      return true;
    }
    for (IExpr source : set) {
      Map<IExpr, Integer> depth = new HashMap<IExpr, Integer>();
      Deque<IExpr> queue = new ArrayDeque<IExpr>();
      depth.put(source, Integer.valueOf(0));
      queue.add(source);
      int reached = 1;
      while (!queue.isEmpty()) {
        IExpr current = queue.poll();
        int next = depth.get(current).intValue() + 1;
        if (next > k) {
          continue;
        }
        for (ExprEdge edge : graph.edgesOf(current)) {
          IExpr neighbor = Graphs.getOppositeVertex(graph, edge, current);
          // the walk may not leave the set - that is exactly what separates a clan from a clique
          if (!set.contains(neighbor) || depth.containsKey(neighbor)) {
            continue;
          }
          depth.put(neighbor, Integer.valueOf(next));
          queue.add(neighbor);
          reached++;
        }
      }
      if (reached < set.size()) {
        return false;
      }
    }
    return true;
  }

  /** Largest subset of a k-clique whose induced subgraph still has diameter at most k. */
  private static final class KClubSearch {
    private final Graph<IExpr, ExprEdge> graph;
    private final int k;
    private final SizeSpec spec;
    private final List<IExpr> chosen = new ArrayList<IExpr>();
    Set<IExpr> best = Collections.<IExpr>emptySet();
    private long steps;
    boolean aborted;

    KClubSearch(Graph<IExpr, ExprEdge> graph, int k, SizeSpec spec) {
      this.graph = graph;
      this.k = k;
      this.spec = spec;
    }

    void explore(List<IExpr> candidate) {
      if (Math.min(candidate.size(), spec.max) <= best.size()) {
        return; // nothing inside this k-clique can beat the incumbent
      }
      chosen.clear();
      expand(candidate, 0);
    }

    private void expand(List<IExpr> candidate, int index) {
      if (aborted) {
        return;
      }
      int size = chosen.size();
      if (size > best.size() && size >= spec.min && size <= spec.max) {
        Set<IExpr> set = new LinkedHashSet<IExpr>(chosen);
        if (inducedDiameterAtMost(graph, set, k)) {
          best = set;
        }
      }
      if (index == candidate.size()) {
        return;
      }
      if (Math.min(size + candidate.size() - index, spec.max) <= best.size()) {
        return;
      }
      if (++steps > ExactVertexColoring.MAX_SEARCH_NODES) {
        aborted = true;
        return;
      }
      if ((steps & 0xFFF) == 0 && Thread.currentThread().isInterrupted()) {
        throw TimeoutException.TIMED_OUT;
      }

      chosen.add(candidate.get(index));
      expand(candidate, index + 1);
      chosen.remove(chosen.size() - 1);
      expand(candidate, index + 1);
    }
  }

  /** The outcome of {@link VertexSetSearch#maximumKPlex}. */
  public static final class KPlexResult {
    public final Set<IExpr> vertices;

    /** <code>false</code> when the node budget stopped the search before it was exhaustive. */
    public final boolean proven;

    private KPlexResult(Set<IExpr> vertices, boolean proven) {
      this.vertices = vertices;
      this.proven = proven;
    }
  }

  /**
   * A largest k-plex: a set <code>S</code> in which every vertex is adjacent to all but
   * <code>k</code> of the members, that is <code>deg_S(v) &gt;= |S| - k</code>. With
   * <code>k == 1</code> that says every vertex is adjacent to all the others, so a 1-plex is a
   * clique.
   *
   * <p>
   * Unlike a clique this is not a clique of any derived graph, so it gets its own branch and bound.
   * Two bounds do the pruning: the vertices still to be considered cannot lift the current set past
   * the incumbent, and no k-plex containing <code>v</code> is larger than
   * <code>deg(v) + k</code>. Finding a maximum k-plex is NP-hard, so the search also carries the
   * node budget {@link ExactVertexColoring#MAX_SEARCH_NODES} and polls for interruption.
   */
  public static KPlexResult maximumKPlex(Graph<IExpr, ExprEdge> graph, int k) {
    List<IExpr> vertices = new ArrayList<IExpr>(graph.vertexSet());
    int n = vertices.size();
    if (n == 0) {
      return new KPlexResult(Collections.<IExpr>emptySet(), true);
    }
    Map<IExpr, Integer> index = new HashMap<IExpr, Integer>(n * 2);
    for (int i = 0; i < n; i++) {
      index.put(vertices.get(i), Integer.valueOf(i));
    }
    int[][] adjacency = new int[n][];
    for (int i = 0; i < n; i++) {
      IExpr vertex = vertices.get(i);
      Set<ExprEdge> edges = graph.edgesOf(vertex);
      int[] row = new int[edges.size()];
      int j = 0;
      for (ExprEdge edge : edges) {
        Integer other = index.get(Graphs.getOppositeVertex(graph, edge, vertex));
        if (other != null && other.intValue() != i) {
          row[j++] = other.intValue();
        }
      }
      row = java.util.Arrays.copyOf(row, j);
      java.util.Arrays.sort(row);
      adjacency[i] = row;
    }

    KPlexSearch search = new KPlexSearch(n, k, adjacency);
    search.run();
    Set<IExpr> best = new LinkedHashSet<IExpr>();
    for (int i = 0; i < search.bestSize; i++) {
      best.add(vertices.get(search.best[i]));
    }
    return new KPlexResult(best, !search.aborted);
  }

  /**
   * The maximal k-plexes of the graph, cut down to the requested size and reported largest first.
   *
   * <p>
   * Unlike {@link #maximumKPlex} this walks the whole take-or-skip tree rather than pruning to one
   * best answer, so it is far more expensive - a complete graph makes every subset a k-plex. It is
   * therefore used only when several sets are asked for, and it stops with
   * {@link SetResult#proven} <code>false</code> once the node budget runs out.
   *
   * <p>
   * Trimming is sound because k-plexes are hereditary: a subset of size <code>m</code> of a k-plex
   * still leaves every member with <code>m - k</code> neighbours or more.
   */
  public static List<Set<IExpr>> maximalKPlexes(Graph<IExpr, ExprEdge> graph, int k, SizeSpec spec,
      int limit, boolean[] aborted) {
    List<IExpr> vertices = new ArrayList<IExpr>(graph.vertexSet());
    int n = vertices.size();
    if (n == 0) {
      return new ArrayList<Set<IExpr>>();
    }
    int[][] adjacency = adjacencyLists(graph, vertices);
    KPlexEnumeration enumeration = new KPlexEnumeration(n, k, adjacency);
    enumeration.run();
    aborted[0] = enumeration.aborted;

    Set<Set<IExpr>> found = new LinkedHashSet<Set<IExpr>>();
    for (int[] plex : enumeration.results) {
      if (plex.length < spec.min) {
        continue;
      }
      Set<IExpr> members = new LinkedHashSet<IExpr>();
      for (int index : plex) {
        members.add(vertices.get(index));
      }
      found.add(trim(graph, members, Math.min(members.size(), spec.max)));
    }
    return order(graph, found, limit);
  }

  private static int[][] adjacencyLists(Graph<IExpr, ExprEdge> graph, List<IExpr> vertices) {
    int n = vertices.size();
    Map<IExpr, Integer> index = new HashMap<IExpr, Integer>(n * 2);
    for (int i = 0; i < n; i++) {
      index.put(vertices.get(i), Integer.valueOf(i));
    }
    int[][] adjacency = new int[n][];
    for (int i = 0; i < n; i++) {
      IExpr vertex = vertices.get(i);
      Set<ExprEdge> edges = graph.edgesOf(vertex);
      int[] row = new int[edges.size()];
      int j = 0;
      for (ExprEdge edge : edges) {
        Integer other = index.get(Graphs.getOppositeVertex(graph, edge, vertex));
        if (other != null && other.intValue() != i) {
          row[j++] = other.intValue();
        }
      }
      row = java.util.Arrays.copyOf(row, j);
      java.util.Arrays.sort(row);
      adjacency[i] = row;
    }
    return adjacency;
  }

  /** Walks every k-plex and keeps the ones no vertex can be added to. */
  private static final class KPlexEnumeration {
    private final int n;
    private final int k;
    private final int[][] adjacency;
    private final int[] degreeInSet;
    private final int[] stack;
    private int size;
    final List<int[]> results = new ArrayList<int[]>();
    private long steps;
    boolean aborted;

    KPlexEnumeration(int n, int k, int[][] adjacency) {
      this.n = n;
      this.k = k;
      this.adjacency = adjacency;
      this.degreeInSet = new int[n];
      this.stack = new int[n];
    }

    void run() {
      expand(0);
    }

    private void expand(int position) {
      if (aborted) {
        return;
      }
      if (position == n) {
        if (size > 0 && isMaximal()) {
          results.add(java.util.Arrays.copyOf(stack, size));
        }
        return;
      }
      if (++steps > ExactVertexColoring.MAX_SEARCH_NODES
          || results.size() > MAX_KCLAN_CANDIDATES) {
        aborted = true;
        return;
      }
      if ((steps & 0xFFF) == 0 && Thread.currentThread().isInterrupted()) {
        throw TimeoutException.TIMED_OUT;
      }

      if (canAdd(position)) {
        add(position);
        expand(position + 1);
        remove(position);
      }
      expand(position + 1);
    }

    /** No vertex outside the set can join it without breaking the k-plex condition. */
    private boolean isMaximal() {
      boolean[] inSet = new boolean[n];
      for (int i = 0; i < size; i++) {
        inSet[stack[i]] = true;
      }
      for (int v = 0; v < n; v++) {
        if (!inSet[v] && canAdd(v)) {
          return false;
        }
      }
      return true;
    }

    private boolean canAdd(int v) {
      int newSize = size + 1;
      if (degreeInSet[v] < newSize - k) {
        return false;
      }
      for (int i = 0; i < size; i++) {
        int u = stack[i];
        int degreeWithV = degreeInSet[u] + (isAdjacent(u, v) ? 1 : 0);
        if (degreeWithV < newSize - k) {
          return false;
        }
      }
      return true;
    }

    private boolean isAdjacent(int u, int v) {
      int[] row = adjacency[u].length <= adjacency[v].length ? adjacency[u] : adjacency[v];
      int target = row == adjacency[u] ? v : u;
      return java.util.Arrays.binarySearch(row, target) >= 0;
    }

    private void add(int v) {
      stack[size++] = v;
      for (int u : adjacency[v]) {
        degreeInSet[u]++;
      }
    }

    private void remove(int v) {
      size--;
      for (int u : adjacency[v]) {
        degreeInSet[u]--;
      }
    }
  }

  /** Branch and bound over the vertices in descending degree order. */
  private static final class KPlexSearch {
    private final int n;
    private final int k;
    private final int[][] adjacency;
    private final int[] degree;
    private final int[] order;
    private final int[] degreeInSet;
    private final int[] stack;
    private int size;
    int[] best;
    int bestSize;
    private long steps;
    boolean aborted;

    KPlexSearch(int n, int k, int[][] adjacency) {
      this.n = n;
      this.k = k;
      this.adjacency = adjacency;
      this.degree = new int[n];
      for (int i = 0; i < n; i++) {
        degree[i] = adjacency[i].length;
      }
      Integer[] boxed = new Integer[n];
      for (int i = 0; i < n; i++) {
        boxed[i] = Integer.valueOf(i);
      }
      // the densest vertices first, so a good incumbent turns up early and prunes the rest
      java.util.Arrays.sort(boxed,
          (x, y) -> degree[x] != degree[y] ? degree[y] - degree[x] : x - y);
      this.order = new int[n];
      for (int i = 0; i < n; i++) {
        order[i] = boxed[i].intValue();
      }
      this.degreeInSet = new int[n];
      this.stack = new int[n];
      this.best = new int[n];
      this.bestSize = 0;
    }

    void run() {
      expand(0);
    }

    private void expand(int position) {
      if (aborted) {
        return;
      }
      if (size > bestSize) {
        System.arraycopy(stack, 0, best, 0, size);
        bestSize = size;
      }
      if (position == n || size + (n - position) <= bestSize) {
        return;
      }
      if (++steps > ExactVertexColoring.MAX_SEARCH_NODES) {
        aborted = true;
        return;
      }
      if ((steps & 0xFFF) == 0 && Thread.currentThread().isInterrupted()) {
        throw TimeoutException.TIMED_OUT;
      }

      int v = order[position];
      // no k-plex holding v is bigger than deg(v) + k, so v is worth taking only if that could beat
      // the incumbent
      if (degree[v] + k > bestSize && canAdd(v)) {
        add(v);
        expand(position + 1);
        remove(v);
      }
      expand(position + 1);
    }

    /** Whether the set stays a k-plex when v joins it. */
    private boolean canAdd(int v) {
      int newSize = size + 1;
      if (degreeInSet[v] < newSize - k) {
        return false;
      }
      for (int i = 0; i < size; i++) {
        int u = stack[i];
        int degreeWithV = degreeInSet[u] + (isAdjacent(u, v) ? 1 : 0);
        if (degreeWithV < newSize - k) {
          return false;
        }
      }
      return true;
    }

    private boolean isAdjacent(int u, int v) {
      int[] row = adjacency[u].length <= adjacency[v].length ? adjacency[u] : adjacency[v];
      int target = row == adjacency[u] ? v : u;
      return java.util.Arrays.binarySearch(row, target) >= 0;
    }

    private void add(int v) {
      stack[size++] = v;
      for (int u : adjacency[v]) {
        degreeInSet[u]++;
      }
    }

    private void remove(int v) {
      size--;
      for (int u : adjacency[v]) {
        degreeInSet[u]--;
      }
    }
  }

  /**
   * Cut a maximum set down to the requested size and report it the way both heads do, as a list
   * holding the one set found. Every subset of a clique is a clique and every subset of an
   * independent set is independent, so trimming cannot break either property.
   *
   * @return <code>{{...}}</code>, or <code>{}</code> when no set of the requested size exists
   */
  public static IAST toResult(IAST vertexList, Set<IExpr> largest, SizeSpec spec) {
    if (largest.size() < spec.min) {
      return F.CEmptyList;
    }
    int wanted = Math.min(largest.size(), spec.max);
    IASTAppendable set = F.ListAlloc(wanted);
    // report the vertices in VertexList order rather than in the search's own order
    for (int i = 1; i <= vertexList.argSize() && set.argSize() < wanted; i++) {
      IExpr vertex = vertexList.get(i);
      if (largest.contains(vertex)) {
        set.append(vertex);
      }
    }
    return F.list(set);
  }
}
