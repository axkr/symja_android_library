package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import org.logicng.formulas.Variable;
import org.logicng.knowledgecompilation.bdds.BDD;
import org.logicng.knowledgecompilation.bdds.jbuddy.BDDKernel;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * A wrapper expression for a LogicNG {@link BDD} used in the function {@link S#BooleanFunction}.
 * <p>
 * This class extends {@link DataExpr} parameterized with {@link BDD} to store a compiled boolean
 * function (BDD). It implements {@link Externalizable} to allow custom serialization of the
 * underlying BDD object.
 * </p>
 * Instances may represent either a "pure" boolean function or a generic boolean function indicated
 * by {@link #isPureBooleanFunction()}.
 */
public class BDDExpr extends DataExpr<BDD> implements Externalizable {
  /**
   * True when this BDD expression represents a pure boolean function.
   */
  boolean isPureFunction;

  IInteger cachedFunctionIndex = F.CN1;

  /**
   * Create a new {@code BDDExpr} instance wrapping the given {@link BDD}. This is used in the
   * function {@link S#BooleanFunction}.
   *
   * @param bdd the LogicNG BDD to wrap
   * @param isPureFunction if true, the wrapped boolean function is considered pure
   * @return a new {@code BDDExpr} wrapping {@link BDD}
   */
  public static BDDExpr newInstance(final BDD bdd, boolean isPureFunction) {
    return new BDDExpr(bdd, isPureFunction);
  }

  /**
   * No-arg constructor required for {@link Externalizable} deserialization. Initializes the object
   * with the standard boolean function head and a null data reference.
   */
  public BDDExpr() {
    super(S.BooleanFunction, null);
  }

  /**
   * Protected constructor used by factory method to create a BDD expression with the specified data
   * and purity flag.
   *
   * @param bdd the BDD to store in this expression
   * @param isPureFunction whether the BDD represents a pure boolean function
   */
  protected BDDExpr(final BDD bdd, boolean isPureFunction) {
    super(S.BooleanFunction, bdd);
    this.isPureFunction = isPureFunction;
  }

  /**
   * Equality is based on the wrapped BDD data. Two {@link BDDExpr} instances are equal if their
   * underlying {@link BDD} objects are equal.
   *
   * @param obj the other object to compare
   * @return true if equal, false otherwise
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof BDDExpr) {
      return fData.equals(((BDDExpr) obj).fData);
    }
    return false;
  }

  /**
   * Evaluate this boolean function expression against an AST of boolean arguments.
   * <p>
   * The incoming {@link IAST} is expected to have boolean arguments
   * ({@link S#True}/{@link S#False}). Each argument is evaluated by the provided
   * {@link EvalEngine}. If any argument is not a boolean constant after evaluation, this method
   * returns {@link F#NIL} to indicate an invalid call. Otherwise the boolean argument list is
   * applied to this BDD expression and the result is returned.
   * </p>
   *
   * @param ast the argument AST (first element usually head)
   * @param engine the evaluation engine used to evaluate arguments
   * @return the result of applying the boolean arguments to this function, or {@link F#NIL} if
   *         argument evaluation fails
   */
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IASTAppendable variablesList = F.ListAlloc(ast.argSize());
    for (int i = 1; i < ast.size(); i++) {
      IExpr expr = engine.evaluate(ast.get(i));
      if (expr.isTrue()) {
        variablesList.append(S.True);
      } else if (expr.isFalse()) {
        variablesList.append(S.False);
      } else {
        return F.NIL;
      }
    }
    return variablesList.apply(this);
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 353 : 353 + fData.hashCode();
  }

  /**
   * Return the internal hierarchy id for this expression type.
   *
   * @return the hierarchy id (BDDEXPRID)
   */
  @Override
  public int hierarchy() {
    return BDDEXPRID;
  }

  /**
   * Create a shallow copy of this {@link BDDExpr}. The underlying {@link BDD} reference is shared
   * (no deep clone).
   *
   * @return a new {@link BDDExpr} with the same BDD and purity flag
   */
  @Override
  public IExpr copy() {
    return new BDDExpr(fData, isPureFunction);
  }

  /**
   * Query whether this boolean function is marked as pure.
   *
   * @return true if pure, false otherwise
   */
  public boolean isPureBooleanFunction() {
    return isPureFunction;
  }

  /**
   * Provide a readable string representation for debugging. If the underlying data is a
   * {@link BDD}, the representation includes the BDD index and number of variables according to its
   * variable order.
   *
   * @return a string describing this boolean function expression
   */
  @Override
  public String toString() {
    if (fData instanceof BDD) {
      List<Variable> variableOrder = fData.getVariableOrder();
      return "BooleanFunction(Index: " + fData.index() + " Number of variables: "
          + variableOrder.size() + ")";
    }

    return fHead + "[" + fData.toString() + "]";
  }

  /**
   * Custom deserialization hook for {@link Externalizable}. Reads the wrapped {@link BDD} object
   * from the provided stream.
   *
   * @param in the input stream to read from
   * @throws IOException if an I/O error occurs
   * @throws ClassNotFoundException if the BDD class cannot be found during deserialization
   */
  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (BDD) in.readObject();
  }

  /**
   * Custom serialization hook for {@link Externalizable}. Writes the wrapped {@link BDD} object to
   * the provided stream.
   *
   * @param output the output stream to write to
   * @throws IOException if an I/O error occurs
   */
  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }

  @Override
  public IASTAppendable fullForm() {
    if (fData == null) {
      return F.NIL;
    }
    int arity = fData.getVariableOrder().size();
    // Fallback if arity is 0 (pure true/false with no variables)
    int arityVal = arity == 0 ? 1 : arity;

    if (fData.isTautology()) {
      return F.BooleanFunction(F.Rule(F.stringx("BDD"), F.List(F.ZZ(arityVal))));
    }
    if (fData.isContradiction()) {
      return F.BooleanFunction(F.Rule(F.stringx("BDD"), F.List(F.ZZ(-arityVal))));
    }

    try {
      // Access the native JBuddy kernel directly
      org.logicng.knowledgecompilation.bdds.jbuddy.BDDKernel kernel = fData.underlyingKernel();

      // Fetch the generated graph array, passing the arity
      int[] flatArray = toROBDD(kernel, fData.index(), arityVal);

      // Convert to Symja internal Integer expressions
      IASTAppendable bddList = F.ListAlloc(flatArray.length);
      for (int val : flatArray) {
        bddList.append(F.ZZ(val));
      }

      return F.BooleanFunction(F.Rule(F.stringx("BDD"), bddList));
    } catch (Exception e) {
      return F.NIL;
    }
  }

  /**
   * Generates a flat integer array representation of the BDD rooted at the given node. The format
   * encodes a Canonical Directed Acyclic Graph (DAG) with Complemented Edges.
   * 
   * @param kernel the JBuddy BDD kernel
   * @param root the root node index of the BDD
   * @param arity the number of variables (arity) of the boolean function
   * @return the flat integer array representing the BDD
   */
  public int[] toROBDD(BDDKernel kernel, int root, int arity) {
    if (root == BDDKernel.BDD_TRUE) {
      return new int[] {arity};
    }
    if (root == BDDKernel.BDD_FALSE) {
      return new int[] {-arity};
    }

    java.util.List<int[]> bottomUpNodes = new java.util.ArrayList<>();
    java.util.Map<Integer, Integer> bddToMath = new java.util.HashMap<>();
    java.util.Map<String, Integer> tripletMap = new java.util.HashMap<>();

    int rootEdge;
    try {
      // Set up reflection once to avoid massive overhead during recursive graph traversal
      java.lang.reflect.Method levelMethod = BDDKernel.class.getDeclaredMethod("level", int.class);
      levelMethod.setAccessible(true);

      java.lang.reflect.Method highMethod = BDDKernel.class.getDeclaredMethod("high", int.class);
      highMethod.setAccessible(true);

      java.lang.reflect.Method lowMethod = BDDKernel.class.getDeclaredMethod("low", int.class);
      lowMethod.setAccessible(true);

      java.lang.reflect.Field level2varField = BDDKernel.class.getDeclaredField("level2var");
      level2varField.setAccessible(true);

      int[] level2var = (int[]) level2varField.get(kernel);

      rootEdge = traverseBottomUp(kernel, root, bottomUpNodes, bddToMath, tripletMap, levelMethod,
          highMethod, lowMethod, level2var);
    } catch (Exception e) {
      throw new RuntimeException("Failed to access BDDKernel internals via reflection", e);
    }

    int numNodes = bottomUpNodes.size();
    int[] result = new int[1 + numNodes * 3];

    // The first element is the Arity, inverted if the root edge is complemented
    result[0] = rootEdge < 0 ? -arity : arity;

    int[] oldToNew = new int[numNodes + 2];
    reorderPreOrder(Math.abs(rootEdge), bottomUpNodes, oldToNew, result, 1);

    return result;
  }

  /**
   * Recursively traverses the internal node arrays to construct the 1-based triplet layout using
   * Java reflection to access protected Kernel members. It canonicalizes nodes by pushing
   * complements downwards to guarantee the True (High) edge is never complemented.
   */
  private int traverseBottomUp(BDDKernel kernel, int bddNode, java.util.List<int[]> nodesList,
      java.util.Map<Integer, Integer> map, java.util.Map<String, Integer> tripletMap,
      java.lang.reflect.Method levelMethod, java.lang.reflect.Method highMethod,
      java.lang.reflect.Method lowMethod, int[] level2var) throws Exception {

    if (bddNode == BDDKernel.BDD_TRUE) {
      return 1;
    }
    if (bddNode == BDDKernel.BDD_FALSE) {
      return -1;
    }

    Integer mapped = map.get(bddNode);
    if (mapped != null) {
      return mapped;
    }

    // Fetch values via reflection
    int level = (Integer) levelMethod.invoke(kernel, bddNode);
    int highNode = (Integer) highMethod.invoke(kernel, bddNode);
    int lowNode = (Integer) lowMethod.invoke(kernel, bddNode);

    int trueEdge = traverseBottomUp(kernel, highNode, nodesList, map, tripletMap, levelMethod,
        highMethod, lowMethod, level2var);
    int falseEdge = traverseBottomUp(kernel, lowNode, nodesList, map, tripletMap, levelMethod,
        highMethod, lowMethod, level2var);

    int varIdx = level2var[level];

    // Canonicalization Rule: The True (High) edge must NEVER be complemented (negative).
    boolean invert = (trueEdge < 0);
    if (invert) {
      trueEdge = -trueEdge;
      falseEdge = -falseEdge;
    }

    String key = varIdx + "," + trueEdge + "," + falseEdge;
    Integer existingId = tripletMap.get(key);
    int mathId;
    if (existingId != null) {
      mathId = existingId;
    } else {
      mathId = nodesList.size() + 2; // Offset by 2 to reserve 1 and -1 for terminals
      nodesList.add(new int[] {varIdx, trueEdge, falseEdge});
      tripletMap.put(key, mathId);
    }

    int resultEdge = invert ? -mathId : mathId;
    map.put(bddNode, resultEdge);
    return resultEdge;
  }

  /**
   * Reformats the populated canonical DAG into a strict Pre-Order (Top-Down) indexing so that the
   * root always occupies position 1, mapping directly to Mathematica's flat array layout.
   */
  private int reorderPreOrder(int oldId, java.util.List<int[]> bottomUpNodes, int[] oldToNew,
      int[] result, int currentNewId) {
    if (oldToNew[oldId] != 0) {
      return currentNewId;
    }

    int newId = currentNewId++;
    oldToNew[oldId] = newId;

    int[] triplet = bottomUpNodes.get(oldId - 2); // oldId starts at 2
    int base = 1 + (newId - 1) * 3;
    result[base] = triplet[0]; // var

    int high = triplet[1];
    int absHigh = Math.abs(high);
    if (absHigh > 1) {
      currentNewId = reorderPreOrder(absHigh, bottomUpNodes, oldToNew, result, currentNewId);
    }

    int low = triplet[2];
    int absLow = Math.abs(low);
    if (absLow > 1) {
      currentNewId = reorderPreOrder(absLow, bottomUpNodes, oldToNew, result, currentNewId);
    }

    int newHigh = absHigh > 1 ? oldToNew[absHigh] : absHigh;
    if (high < 0) {
      newHigh = -newHigh;
    }
    result[base + 1] = newHigh;

    int newLow = absLow > 1 ? oldToNew[absLow] : absLow;
    if (low < 0) {
      newLow = -newLow;
    }
    result[base + 2] = newLow;

    return currentNewId;
  }

  /**
   * Calculates the integer "Function index" of this boolean function. The function index is the
   * decimal value of the full truth table output. * @return the function index as an IInteger, or
   * F.NIL if calculation fails.
   */
  private IInteger functionIndex() {
    if (fData == null) {
      return F.CN1;
    }

    int totalVars = fData.getVariableOrder().size();
    if (totalVars == 0) {
      return fData.isTautology() ? F.C1 : F.C0;
    }
    if (cachedFunctionIndex.isGT(F.CN1)) {
      return cachedFunctionIndex;
    }
    try {
      // Access the native JBuddy kernel directly
      org.logicng.knowledgecompilation.bdds.jbuddy.BDDKernel kernel = fData.underlyingKernel();

      java.lang.reflect.Method levelMethod = BDDKernel.class.getDeclaredMethod("level", int.class);
      levelMethod.setAccessible(true);

      java.lang.reflect.Method highMethod = BDDKernel.class.getDeclaredMethod("high", int.class);
      highMethod.setAccessible(true);

      java.lang.reflect.Method lowMethod = BDDKernel.class.getDeclaredMethod("low", int.class);
      lowMethod.setAccessible(true);

      java.util.Map<Integer, java.math.BigInteger> memo = new java.util.HashMap<>();

      java.math.BigInteger index =
          computeIndex(kernel, fData.index(), memo, levelMethod, highMethod, lowMethod, totalVars);

      // Pad the root node up to level 0 (in case the root node skips the highest order variables)
      int rootLevel = (Integer) levelMethod.invoke(kernel, fData.index());
      index = padIndex(index, rootLevel, 0, totalVars);

      cachedFunctionIndex = F.ZZ(index);
      return cachedFunctionIndex;
    } catch (Exception e) {
      return F.CN1;
    }
  }

  /**
   * Recursively computes the BigInteger representation of the truth table for a specific BDD node.
   */
  private java.math.BigInteger computeIndex(
      org.logicng.knowledgecompilation.bdds.jbuddy.BDDKernel kernel, int node,
      java.util.Map<Integer, java.math.BigInteger> memo, java.lang.reflect.Method levelMethod,
      java.lang.reflect.Method highMethod, java.lang.reflect.Method lowMethod, int totalVars)
      throws Exception {

    if (node == BDDKernel.BDD_FALSE)
      return java.math.BigInteger.ZERO;
    if (node == BDDKernel.BDD_TRUE)
      return java.math.BigInteger.ONE;
    if (memo.containsKey(node))
      return memo.get(node);

    int currentLevel = (Integer) levelMethod.invoke(kernel, node);
    int highNode = (Integer) highMethod.invoke(kernel, node);
    int lowNode = (Integer) lowMethod.invoke(kernel, node);

    // Terminal nodes (0, 1) are treated as being at the absolute bottom (totalVars)
    int highLevel = (highNode <= 1) ? totalVars : (Integer) levelMethod.invoke(kernel, highNode);
    int lowLevel = (lowNode <= 1) ? totalVars : (Integer) levelMethod.invoke(kernel, lowNode);

    // Compute the high branch and pad any skipped variables between this level and the high
    // branch's level
    java.math.BigInteger highJ =
        computeIndex(kernel, highNode, memo, levelMethod, highMethod, lowMethod, totalVars);
    java.math.BigInteger paddedHigh = padIndex(highJ, highLevel, currentLevel + 1, totalVars);

    // Compute the low branch and pad any skipped variables between this level and the low branch's
    // level
    java.math.BigInteger lowJ =
        computeIndex(kernel, lowNode, memo, levelMethod, highMethod, lowMethod, totalVars);
    java.math.BigInteger paddedLow = padIndex(lowJ, lowLevel, currentLevel + 1, totalVars);

    // Calculate the bit-shift corresponding to the size of the lower half of the truth table
    int shiftBits = totalVars - 1 - currentLevel;
    if (shiftBits >= 31) {
      throw new ArithmeticException("Function index exceeds practical memory limits.");
    }
    int shift = 1 << shiftBits;

    // Merge High and Low branches
    java.math.BigInteger res = paddedHigh.shiftLeft(shift).or(paddedLow);
    memo.put(node, res);

    return res;
  }

  /**
   * Duplicates a truth table index upwards across skipped variable levels to maintain full
   * bit-width.
   */
  private java.math.BigInteger padIndex(java.math.BigInteger index, int currentLevel,
      int targetLevel, int totalVars) {
    java.math.BigInteger res = index;
    while (currentLevel > targetLevel) {
      currentLevel--;
      int shiftBits = totalVars - 1 - currentLevel;
      if (shiftBits >= 31) {
        throw new ArithmeticException("Function index exceeds practical memory limits.");
      }
      int shift = 1 << shiftBits;
      res = res.shiftLeft(shift).or(res);
    }
    return res;
  }
}
