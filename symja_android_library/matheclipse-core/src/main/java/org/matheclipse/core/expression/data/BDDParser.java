package org.matheclipse.core.expression.data;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.logicng.knowledgecompilation.bdds.BDD;

/**
 * A parser for converting a flat integer array representation of a Binary Decision Diagram (BDD)
 * into a LogicNG {@link Formula} directed acyclic graph.
 * <p>
 * This parser specifically handles the internal BDD serialization format used by the Wolfram
 * Language in the form {@code BooleanFunction["BDD" -> {root, v1, t1, f1, v2, t2, f2, ...}]}.
 * </p>
 * <b>Format Specification</b> The input list is a flattened array encoding a Reduced Ordered BDD
 * (ROBDD) with complemented edges.
 * <ul>
 * <li><b>Root Indicator (Index 0):</b> An integer {@code N} where the absolute value {@code |N|}
 * represents the total number of internal nodes. If {@code N < 0}, the final evaluation of the root
 * node is logically negated (complemented).</li>
 * <li><b>Node Triplets (Indices 1 to 3*|N|):</b> The remainder of the array consists of {@code |N|}
 * consecutive triplets, each defining a node in a 1-based indexed order (Node 1, Node 2, ..., Node
 * |N|). Each triplet consists of:
 * <ol>
 * <li>{@code variable_index}: A 0-based integer representing the boolean variable (e.g., 0 maps to
 * #1).</li>
 * <li>{@code true_edge}: The edge to follow if the variable evaluates to True.</li>
 * <li>{@code false_edge}: The edge to follow if the variable evaluates to False.</li>
 * </ol>
 * </li>
 * </ul>
 * <b>Edge Pointer Resolution</b> Edge values (both {@code true_edge} and {@code false_edge}) are
 * 1-based pointers interpreted as follows:
 * <ul>
 * <li>{@code 1}: Points to the <b>True</b> terminal node ({@code verum}).</li>
 * <li>{@code -1}: Points to the <b>False</b> terminal node ({@code falsum}).</li>
 * <li>{@code E > 1}: Points to the internal node at index {@code E}.</li>
 * <li>{@code E < -1}: Points to the internal node at index {@code |E|}, but specifies a
 * <b>complemented edge</b>, meaning the boolean result of the target node is logically negated
 * (NOT).</li>
 * </ul>
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Binary_decision_diagram">Binary Decision Diagram</a>
 */
public class BDDParser {
  /**
   * Memoization cache for parsed nodes to preserve the shared DAG structure and prevent exponential
   * explosion during Formula construction. Indexed 1-based to match edge pointers.
   */
  private Formula[] cache;

  /** The flat array containing the ROBDD triplet structure. */
  private int[] robddArray;

  /** The LogicNG factory used to instantiate the Boolean formulas. */
  private FormulaFactory factory;

  /**
   * Constructs a new BDDParser.
   * 
   * @param robddArray array of integers representing the Reduced Ordered BDD (ROBDD)
   * @param numNodes the absolute number of nodes |N|
   * @param factory the formula factory for creating variables and logic gates
   */
  public BDDParser(int[] robddArray, int numNodes, FormulaFactory factory) {
    this.robddArray = robddArray;
    this.cache = new Formula[numNodes + 1];
    this.factory = factory;
  }

  /**
   * Resolves an edge pointer to its corresponding boolean Formula. Handles terminal resolution and
   * applies edge complementation (NOT) if the edge pointer is negative.
   * 
   * @param edge the integer edge pointer
   * @return the resolved (and potentially negated) boolean Formula
   */
  private Formula parseEdge(int edge) {
    if (edge == 1) {
      return factory.verum();
    }
    if (edge == -1) {
      return factory.falsum();
    }
    int nodeIdx = Math.abs(edge);
    Formula f = parseNode(nodeIdx);
    if (f == null) {
      return null;
    }
    // Negative edges denote complemented (inverted) logic
    return edge < 0 ? factory.not(f) : f;
  }

  /**
   * Recursively parses and constructs the boolean Formula for a specific node triplet. Utilizes the
   * cache to avoid re-evaluating shared sub-graphs.
   * 
   * @param nodeIdx the 1-based index of the node to parse
   * @return the Formula representing this node's decision sub-tree, or null if the index is invalid
   */
  private Formula parseNode(int nodeIdx) {
    if (nodeIdx > cache.length - 1 || nodeIdx < 1) {
      return null;
    }

    Formula f = cache[nodeIdx];
    if (f == null) {
      int base = (nodeIdx - 1) * 3;
      int varIdx = robddArray[base + 1];
      int trueEdge = robddArray[base + 2];
      int falseEdge = robddArray[base + 3];

      Variable v = factory.variable("#" + (varIdx + 1));

      Formula tForm = parseEdge(trueEdge);
      Formula fForm = parseEdge(falseEdge);
      if (tForm == null || fForm == null) {
        return null;
      }

      f = factory.or(factory.and(v, tForm), factory.and(factory.not(v), fForm));
      cache[nodeIdx] = f;
    }
    return f;
  }

  /**
   * A parser for converting a flat integer array representation of a Binary Decision Diagram (BDD)
   * into a LogicNG {@link Formula} directed acyclic graph.
   * <p>
   * This parser specifically handles the internal BDD serialization format used by the Wolfram
   * Language in the form {@code BooleanFunction["BDD" -> {arity, v1, t1, f1, v2, t2, f2, ...}]}.
   * </p>
   * <b>Format Specification</b> The input list is a flattened array encoding a Reduced Ordered BDD
   * (ROBDD) with complemented edges.
   * <ul>
   * <li><b>Root Indicator (Index 0):</b> An integer {@code A} where the absolute value {@code |A|}
   * represents the <b>arity (number of variables)</b> of the boolean function. If {@code A < 0},
   * the final evaluation of the root node is logically negated (complemented).</li>
   * <li><b>Node Triplets (Indices 1 to 3*|N|):</b> The remainder of the array consists of
   * {@code |N|} consecutive triplets (where |N| is inferred as `(length - 1) / 3`), each defining a
   * node in a 1-based indexed order (Node 1, Node 2, ..., Node |N|).
   * </ul>
   * 
   * @param list the flat integer array representing the ROBDD
   * @param factory the formula factory
   * @return the parsed BDD as a LogicNG {@link BDD}, or null if the input is invalid
   */
  public static BDD parseBDD(int[] list, FormulaFactory factory) {
    int first = list[0];
    if (first != Integer.MIN_VALUE) {
      // The number of nodes is inferred from the remaining array length
      int numNodes = (list.length - 1) / 3;

      // Validate if format represents valid node triplets correctly
      if (numNodes * 3 + 1 == list.length) {
        // Handle trivial true/false root BDDs mapping straight to terminals
        if (list.length == 1) {
          if (first >= 0) {
            return factory.verum().bdd();
          } else {
            return factory.falsum().bdd();
          }
        }

        BDDParser parser = new BDDParser(list, numNodes, factory);
        Formula rootFormula = parser.parseNode(1);
        if (rootFormula != null) {
          if (first < 0) {
            rootFormula = factory.not(rootFormula);
          }
          return rootFormula.bdd();
        }
      }
    }
    return null;
  }
}
