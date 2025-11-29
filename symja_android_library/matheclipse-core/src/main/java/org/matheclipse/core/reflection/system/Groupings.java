package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <p>
 * Implements <code>Groupings[list, k]</code>.
 * </p>
 * <p>
 * This implementation creates the ordering by processing split-sizes in "Outside-In" pairs
 * (Largest/Smallest, then 2nd-Largest/2nd-Smallest...). Within each pair, the results are
 * interleaved. This ensures unbalanced trees appear before balanced trees.
 * </p>
 */
public class Groupings extends AbstractFunctionEvaluator {

  public Groupings() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();
    int k = arg2.toIntDefault();
    IExpr head = (ast.size() > 3) ? ast.arg3() : F.List;

    if (k < 2) {
      return F.NIL;
    }

    IASTAppendable leafList;
    if (arg1.isInteger()) {
      int n = arg1.toIntDefault(-1);
      if (n < 0)
        return F.NIL;
      if (n == 0)
        return F.List();
      leafList = F.ListAlloc(n);
      for (int i = 1; i <= n; i++) {
        leafList.append(F.ZZ(i));
      }
    } else if (arg1.isList()) {
      leafList = ((IAST) arg1).copyAppendable();
    } else {
      return F.NIL;
    }

    int n = leafList.argSize();

    // Condition: (n - 1) must be divisible by (k - 1)
    if (n > 1 && (n - 1) % (k - 1) != 0) {
      return F.List();
    }

    return generateGroupings(leafList, k, head);
  }

  private IAST generateGroupings(IAST elements, int k, IExpr head) {
    int n = elements.argSize();

    if (n == 1) {
      return F.List(elements.arg1());
    }

    // Determine all valid split sizes for the first child
    List<Integer> validSplits = new ArrayList<>();
    int maxFirstChildSize = n - (k - 1);
    // Standard ordered list: 1, 1+(k-1), ... max
    for (int s = 1; s <= maxFirstChildSize; s += (k - 1)) {
      validSplits.add(s);
    }

    IASTAppendable finalResults = F.ListAlloc();

    // Process splits using "Two-Pointer" approach (Outside-In)
    int leftIdx = 0;
    int rightIdx = validSplits.size() - 1;

    while (leftIdx <= rightIdx) {
      if (leftIdx == rightIdx) {
        // Center case (Single split size in the middle)
        // Just generate and append.
        int splitSize = validSplits.get(leftIdx);
        IAST singleResults = generateSplitResults(elements, splitSize, k, head);
        finalResults.appendArgs(singleResults);
      } else {
        // Pair case: Largest (Right) and Smallest (Left)
        // Prioritizes the "Big Left Child" (RightIdx) in the interleave
        int bigSplit = validSplits.get(rightIdx);
        int smallSplit = validSplits.get(leftIdx);

        IAST resultsBig = generateSplitResults(elements, bigSplit, k, head);
        IAST resultsSmall = generateSplitResults(elements, smallSplit, k, head);

        // Interleave these two lists
        interleaveAndAppend(finalResults, resultsBig, resultsSmall);
      }
      leftIdx++;
      rightIdx--;
    }

    return finalResults;
  }

  /**
   * Generates trees for a specific fixed size of the first child.
   */
  private IAST generateSplitResults(IAST elements, int splitSize, int k, IExpr head) {
    int n = elements.argSize();
    IASTAppendable results = F.ListAlloc();

    IAST firstChildOptions = generateGroupings(elements.subList(1, splitSize + 1), k, head);

    IAST remainingOptions;
    if (k == 2) {
      remainingOptions = generateGroupings(elements.subList(splitSize + 1, n + 1), 2, head);
    } else {
      remainingOptions = generateForest(elements.subList(splitSize + 1, n + 1), k, k - 1, head);
    }

    for (IExpr first : firstChildOptions) {
      for (IExpr rest : remainingOptions) {
        if (k == 2) {
          results.append(F.function(head, first, rest));
        } else {
          IASTAppendable tree = F.ast(head, k);
          tree.append(first);
          tree.appendArgs((IAST) rest);
          results.append(tree);
        }
      }
    }
    return results;
  }

  /**
   * Helper to interleave two lists A and B into destination. Order: A[0], B[0], A[1], B[1] ...
   */
  private void interleaveAndAppend(IASTAppendable dest, IAST listA, IAST listB) {
    int sizeA = listA.size();
    int sizeB = listB.size();
    int max = Math.max(sizeA, sizeB);

    // IAST is 1-based, but loop logic is simpler with 0-based offsets
    for (int i = 1; i <= max; i++) {
      if (i < sizeA)
        dest.append(listA.get(i));
      if (i < sizeB)
        dest.append(listB.get(i));
    }
  }

  private IAST generateForest(IAST elements, int k, int treesNeeded, IExpr head) {
    int n = elements.argSize();
    if (treesNeeded == 1) {
      IAST trees = generateGroupings(elements, k, head);
      IASTAppendable result = F.ListAlloc(trees.size());
      for (IExpr t : trees) {
        result.append(F.List(t));
      }
      return result;
    }

    // Apply the same "Outside-In" logic for Forests to maintain consistency
    List<Integer> validSplits = new ArrayList<>();
    int maxNextTreeSize = n - (treesNeeded - 1);
    for (int s = 1; s <= maxNextTreeSize; s += (k - 1)) {
      validSplits.add(s);
    }

    IASTAppendable finalResults = F.ListAlloc();
    int leftIdx = 0;
    int rightIdx = validSplits.size() - 1;

    while (leftIdx <= rightIdx) {
      if (leftIdx == rightIdx) {
        finalResults.appendArgs(
            generateForestSplit(elements, validSplits.get(leftIdx), k, treesNeeded, head));
      } else {
        IAST resBig =
            generateForestSplit(elements, validSplits.get(rightIdx), k, treesNeeded, head);
        IAST resSmall =
            generateForestSplit(elements, validSplits.get(leftIdx), k, treesNeeded, head);
        interleaveAndAppend(finalResults, resBig, resSmall);
      }
      leftIdx++;
      rightIdx--;
    }
    return finalResults;
  }

  private IAST generateForestSplit(IAST elements, int splitSize, int k, int treesNeeded,
      IExpr head) {
    int n = elements.argSize();
    IASTAppendable results = F.ListAlloc();

    IAST currentTreeOptions = generateGroupings(elements.subList(1, splitSize + 1), k, head);
    IAST tailForestOptions =
        generateForest(elements.subList(splitSize + 1, n + 1), k, treesNeeded - 1, head);

    for (IExpr tree : currentTreeOptions) {
      for (IExpr tail : tailForestOptions) {
        IASTAppendable forest = F.ListAlloc(treesNeeded);
        forest.append(tree);
        forest.appendArgs((IAST) tail);
        results.append(forest);
      }
    }
    return results;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }
}
