package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <p>
 * Implement the Partition function.
 * </p>
 * * *
 * 
 * <pre>
 * Partition(list, n)
 * Partition(list, n, d)
 * Partition(list, n, d, {kL, kR})
 * Partition(list, n, d, {kL, kR}, padding)
 * </pre>
 * 
 * * *
 * <p>
 * Supports <code>UpTo[n]</code> and multi-dimensional partitioning.
 * </p>
 */
public class Partition extends AbstractFunctionEvaluator {

  public Partition() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    // Arguments: list, n, d, {kL, kR}, pad
    IExpr listExpr = ast.arg1();
    // Use isAST() to allow f(x), not just List
    if (!listExpr.isAST()) {
      return F.NIL;
    }
    IAST list = (IAST) listExpr;

    // --- 1. Parse 'n' (Partition Sizes) ---
    IExpr nExpr = ast.arg2();
    IAST nList = null;
    boolean isUpTo = false;

    // Handle UpTo[n]
    if (nExpr.isAST(F.UpTo, 2)) {
      nExpr = nExpr.first();
      isUpTo = true;
    }

    if (nExpr.isList()) {
      nList = (IAST) nExpr;
    } else if (nExpr.isInteger()) {
      nList = F.List(nExpr);
    } else {
      return F.NIL;
    }

    // Validate n > 0
    for (IExpr nVal : nList) {
      if (nVal.toIntDefault() < 1) {
        return F.NIL;
      }
    }

    // --- 2. Parse 'd' (Offsets) ---
    IAST dList = null;
    if (ast.argSize() >= 3) {
      IExpr dExpr = ast.arg3();
      if (dExpr.isList()) {
        dList = (IAST) dExpr;
      } else if (dExpr.isInteger()) {
        dList = F.List(dExpr);
      } else {
        return F.NIL; // Invalid d
      }

      // Validate d > 0
      for (IExpr dVal : dList) {
        if (dVal.toIntDefault() < 1) {
          return F.NIL;
        }
      }
    }

    // --- 3. Parse Alignment {kL, kR} ---
    IAST kList = null;
    if (ast.argSize() >= 4) {
      IExpr kExpr = ast.arg4();
      if (kExpr.isList() || kExpr.isInteger()) {
        // Normalize to a list for consistency passing
        if (kExpr.isInteger()) {
          // k -> {k, k}
          kList = F.List(F.List(kExpr, kExpr));
        } else {
          // List
          kList = (IAST) kExpr;
        }
      } else {
        return F.NIL;
      }
    } else if (isUpTo) {
      // UpTo implies {1, 1} alignment if not specified
      // Partition[list, UpTo[n]] == Partition[list, n, n, {1, 1}, {}]
      kList = F.List(F.List(F.C1, F.C1));
    }

    // --- 4. Parse Padding ---
    IExpr pad = F.NIL; // NIL implies cyclic list padding (default)
    if (ast.argSize() >= 5) {
      pad = ast.arg5();
    } else if (isUpTo) {
      // UpTo implies padding -> {} (no padding, allow short lists)
      pad = F.List();
    }

    // --- EXECUTE ---
    try {
      return partitionRec(list, nList, 1, dList, kList, pad, engine);
    } catch (RuntimeException e) {
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
      return F.NIL;
    }
  }

  /**
   * Recursive partition implementation for multi-dimensional support.
   * 
   * @param list The current level list.
   * @param nList The full list of partition sizes {n1, n2...}.
   * @param level The current depth level (1-based).
   * @param dList The full specification of offsets.
   * @param kList The full specification of alignments.
   * @param pad The padding specification.
   */
  private IExpr partitionRec(IAST list, IAST nList, int level, IAST dList, IAST kList, IExpr pad,
      EvalEngine engine) {
    if (level > nList.argSize()) {
      return list;
    }

    // Get parameters for this level
    int n = nList.get(level).toIntDefault(); // The partition size for this level

    // Resolve d (Offset)
    int d = n; // Default
    if (dList != null) {
      if (dList.argSize() >= level) {
        d = dList.get(level).toIntDefault();
      } else if (dList.argSize() == 1) {
        d = dList.get(1).toIntDefault();
      }
    }

    // Resolve k (Alignment {kL, kR})
    int kL = 1;
    int kR = -1;

    if (kList != null) {
      if (nList.argSize() == 1) {
        // Simple 1D Case
        if (kList.isAST() && kList.arg1().isList()) {
          // Handle {{kL, kR}}
          IAST sub = (IAST) kList.arg1();
          if (sub.argSize() >= 1)
            kL = sub.get(1).toIntDefault();
          if (sub.argSize() >= 2)
            kR = sub.get(2).toIntDefault();
          else
            kR = kL;
        } else if (kList.size() > 1 && kList.arg1().isInteger()) {
          kL = kList.get(1).toIntDefault();
          kR = (kList.size() > 2) ? kList.get(2).toIntDefault() : kL;
        }
      }
    }

    // Perform 1D Partition on the current list
    IAST partitions = partition1D(list, n, d, kL, kR, pad);

    // Recursion for Nested Dimensions
    if (level < nList.argSize()) {
      // Use F.ast(list.head()) to maintain the head (e.g., List or f)
      IASTAppendable nextLevelResult = F.ast(list.head(), partitions.size());
      for (IExpr sub : partitions) {
        if (sub.isList()) {
          nextLevelResult
              .append(partitionRec((IAST) sub, nList, level + 1, dList, kList, pad, engine));
        } else {
          nextLevelResult.append(sub);
        }
      }

      try {
        if (level == 1 && nList.argSize() == 2) {
          return engine.evaluate(F.Transpose(nextLevelResult, F.List(F.C1, F.C3, F.C2, F.C4)));
        }
      } catch (Exception e) {
        // Fallback
      }
      return nextLevelResult;
    }

    return partitions;
  }

  /**
   * Core 1D Partition Logic.
   */
  private IAST partition1D(IAST list, int n, int d, int kL, int kR, IExpr pad) {
    // Safety check
    if (n <= 0 || d <= 0) {
      return F.NIL;
    }

    int size = list.argSize();

    // Canonicalize kL, kR (Handling negative indices)
    // k > 0: k
    // k < 0: n + k + 1
    int c_kL = (kL > 0) ? kL : n + kL + 1;
    int c_kR = (kR > 0) ? kR : n + kR + 1;

    // Calculate Start Index (0-based relative to list)
    long start = -(c_kL - 1);

    // Calculate Stopping Condition
    // The last partition is valid if its start 's' satisfies:
    // s <= size - c_kR
    long endLimit = size - c_kR;

    boolean defaultPad = pad.isNIL();
    boolean noPad = pad.isList() && pad.isEmpty(); // pad -> {}

    // Allocate result using the same head as the input list
    IASTAppendable result = F.ast(list.head());

    for (long s = start; s <= endLimit; s += d) {
      IASTAppendable sublist = F.ast(list.head(), n);

      // Optimization for no-padding case fully inside bounds
      if (noPad && s >= 0 && s + n <= size) {
        for (int i = 0; i < n; i++) {
          sublist.append(list.get((int) (s + i + 1)));
        }
        result.append(sublist);
        continue;
      }

      // General case
      for (int i = 0; i < n; i++) {
        long idx = s + i; // 0-based index in list

        if (idx >= 0 && idx < size) {
          sublist.append(list.get((int) (idx + 1)));
        } else {
          if (noPad) {
            // Partition[..., {}] allows ragged lists. Don't append.
            continue;
          }

          // Apply Padding
          if (defaultPad) {
            // Cyclic list padding
            int cIdx = (int) (idx % size);
            if (cIdx < 0) {
              cIdx += size;
            }
            sublist.append(list.get(cIdx + 1));
          } else if (pad.isList()) {
            // Cyclic padding from provided list
            IAST pList = (IAST) pad;
            if (pList.size() > 1) {
              int pLen = pList.argSize();
              int pIdx = (int) (idx % pLen);
              if (pIdx < 0) {
                pIdx += pLen;
              }
              sublist.append(pList.get(pIdx + 1));
            }
          } else {
            // Single element padding
            sublist.append(pad);
          }
        }
      }

      // Determine if we keep this sublist
      // We keep it if it contains at least one element (size > 1 due to head)
      if (sublist.size() > 1) {
        result.append(sublist);
      }
    }

    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_5;
  }
}