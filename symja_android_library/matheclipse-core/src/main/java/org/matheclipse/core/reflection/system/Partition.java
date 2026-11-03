package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.PerLevelIntSpec;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class Partition extends AbstractFunctionEvaluator {

  public Partition() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    // Arguments: list, n, d, {kL, kR}, pad
    IExpr listExpr = ast.arg1();
    if (listExpr.isSparseArray()) {
      return org.matheclipse.core.expression.data.SparseArrayExpr.evalNormal(ast, true, engine);
    }
    // Use isAST() to allow f(x), not just List
    if (!listExpr.isAST()) {
      return F.NIL;
    }
    IAST list = (IAST) listExpr;

    // Parse 'n' (Partition Sizes) ---
    IExpr nExpr = ast.arg2();
    boolean isUpTo = false;

    // Handle UpTo[n]
    if (nExpr.isAST(F.UpTo, 2)) {
      nExpr = nExpr.first();
      isUpTo = true;
    }

    // one partition size for each level; validate n > 0
    PerLevelIntSpec nSpec = PerLevelIntSpec.create(nExpr);
    if (nSpec == null || !nSpec.allMatch(n -> n >= 1)) {
      return F.NIL;
    }

    // Parse 'd' (Offsets) ---
    PerLevelIntSpec dSpec = null;
    if (ast.argSize() >= 3) {
      // one offset for each level; validate d > 0
      dSpec = PerLevelIntSpec.create(ast.arg3());
      if (dSpec == null || !dSpec.allMatch(d -> d >= 1)) {
        return F.NIL;
      }
    }

    // Parse Alignment {kL, kR} ---
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

    // Parse Padding ---
    IExpr pad = F.NIL; // NIL implies cyclic list padding (default)
    if (ast.argSize() >= 5) {
      pad = ast.arg5();
    } else if (isUpTo) {
      // UpTo implies padding -> {} (no padding, allow short lists)
      pad = F.List();
    }

    try {
      if (nSpec.levels() > 1 && ast.argSize() >= 4 && !isUpTo) {
        IExpr array = partitionArray(list, nSpec, dSpec, ast.arg4(), pad, engine);
        if (array.isPresent()) {
          return array;
        }
      }
      return partitionRec(list, nSpec, 1, dSpec, kList, pad, engine);
    } catch (RuntimeException e) {
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
      return F.NIL;
    }
  }

  /**
   * The alignments <code>{kL, kR}</code> of each level for a single <code>k</code>, for one pair
   * <code>{kL, kR}</code> and for a list with one pair for each level.
   *
   * @return <code>null</code> if the specification is invalid
   */
  private static int[][] alignments(IExpr spec, int levels) {
    int[][] result = new int[levels][];
    for (int i = 0; i < levels; i++) {
      IExpr levelSpec = spec;
      if (spec.isList() && spec.exists(x -> x.isList())) {
        if (spec.argSize() != levels) {
          return null;
        }
        levelSpec = ((IAST) spec).get(i + 1);
      }
      int left;
      int right;
      if (levelSpec.isList()) {
        if (levelSpec.argSize() < 1 || levelSpec.argSize() > 2) {
          return null;
        }
        left = levelSpec.first().toIntDefault();
        right = levelSpec.argSize() == 2 ? levelSpec.second().toIntDefault() : left;
      } else {
        left = levelSpec.toIntDefault();
        right = left;
      }
      if (F.isNotPresent(left) || F.isNotPresent(right)) {
        return null;
      }
      result[i] = new int[] {left, right};
    }
    return result;
  }

  /** The cyclic position of the 0-based <code>index</code> in a list of <code>size</code>. */
  private static int cyclicPosition(long index, int size) {
    return (int) Math.floorMod(index, (long) size) + 1;
  }

  /**
   * The partition of a full array at several levels, with the padding outside of the array. The
   * blocks start at the positions <code>start + i * offset</code> of each level.
   *
   * @return {@link F#NIL} if <code>list</code> isn't a full array at these levels or if the
   *         alignment is invalid
   */
  private static IExpr partitionArray(IAST list, PerLevelIntSpec nSpec, PerLevelIntSpec dSpec,
      IExpr alignment, IExpr pad, EvalEngine engine) {
    final int levels = nSpec.levels();
    int[][] alignments = alignments(alignment, levels);
    IExpr dimensionList = engine.evaluate(F.Dimensions(list));
    if (alignments == null || !dimensionList.isList() || dimensionList.argSize() < levels) {
      return F.NIL;
    }
    int[] dimensions = new int[levels];
    int[] sizes = new int[levels];
    int[] offsets = new int[levels];
    long[] first = new long[levels];
    long[] last = new long[levels];
    long elements = 1L;
    for (int i = 0; i < levels; i++) {
      dimensions[i] = dimensionList.getAt(i + 1).toIntDefault();
      if (dimensions[i] <= 0) {
        return F.NIL;
      }
      sizes[i] = nSpec.get(i + 1);
      offsets[i] = dSpec == null ? sizes[i]
          : dSpec.levels() == 1 ? dSpec.get(1) : dSpec.get(i + 1, sizes[i]);
      int left = alignments[i][0] > 0 ? alignments[i][0] : sizes[i] + alignments[i][0] + 1;
      int right = alignments[i][1] > 0 ? alignments[i][1] : sizes[i] + alignments[i][1] + 1;
      first[i] = 1L - left;
      last[i] = (long) dimensions[i] - right;
      elements *= sizes[i] * Math.max(1L, (last[i] - first[i]) / offsets[i] + 1L);
      if (elements > Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt(elements);
      }
    }
    return new ArrayPartition(list, dimensions, sizes, offsets, pad, engine).blocks(first, last);
  }

  /** The blocks of a multi-dimensional partition. */
  private static final class ArrayPartition {
    private final IAST array;
    private final int[] dimensions;
    private final int[] sizes;
    private final int[] offsets;
    private final IExpr pad;
    private final EvalEngine engine;
    private final long[] origin;
    private final long[] index;

    ArrayPartition(IAST array, int[] dimensions, int[] sizes, int[] offsets, IExpr pad,
        EvalEngine engine) {
      this.array = array;
      this.dimensions = dimensions;
      this.sizes = sizes;
      this.offsets = offsets;
      this.pad = pad;
      this.engine = engine;
      this.origin = new long[sizes.length];
      this.index = new long[sizes.length];
    }

    IExpr blocks(long[] first, long[] last) {
      return blocks(first, last, 0);
    }

    private IExpr blocks(long[] first, long[] last, int level) {
      IASTAppendable result = F.ast(array.head());
      for (long start = first[level]; start <= last[level]; start += offsets[level]) {
        engine.checkTimeLimit();
        origin[level] = start;
        result.append(level + 1 == sizes.length ? block(0) : blocks(first, last, level + 1));
      }
      return result;
    }

    private IExpr block(int level) {
      if (level == sizes.length) {
        return element();
      }
      IASTAppendable result = F.ast(array.head(), sizes[level]);
      for (int i = 0; i < sizes[level]; i++) {
        index[level] = origin[level] + i;
        IExpr element = block(level + 1);
        if (element.isPresent()) {
          result.append(element);
        }
      }
      return result.argSize() > 0 ? result : F.NIL;
    }

    /** The element at the positions in {@link #index}, {@link F#NIL} if it is left out. */
    private IExpr element() {
      boolean inside = true;
      for (int i = 0; i < index.length; i++) {
        inside &= index[i] >= 0 && index[i] < dimensions[i];
      }
      if (inside || pad.isNIL()) {
        // outside of the array the cyclic continuation of the array is the default
        IExpr element = array;
        for (int i = 0; i < index.length; i++) {
          element = element.getAt(cyclicPosition(index[i], dimensions[i]));
        }
        return element;
      }
      if (!pad.isList()) {
        return pad;
      }
      if (pad.isEmpty()) {
        return F.NIL;
      }
      // the innermost levels address a nested padding list
      IExpr element = pad;
      int depth = 0;
      for (IExpr level = pad; level.isList() && level.argSize() > 0; level = level.first()) {
        depth++;
      }
      for (int i = Math.max(0, index.length - depth); i < index.length; i++) {
        element = element.getAt(cyclicPosition(index[i], element.argSize()));
      }
      return element;
    }
  }

  /**
   * Recursive partition implementation for multi-dimensional support. * @param list The current
   * level list.
   * 
   * @param nSpec The partition size for each level {n1, n2...}.
   * @param level The current depth level (1-based).
   * @param dSpec The offset for each level.
   * @param kList The full specification of alignments.
   * @param pad The padding specification.
   */
  private IExpr partitionRec(IAST list, PerLevelIntSpec nSpec, int level, PerLevelIntSpec dSpec,
      IAST kList, IExpr pad, EvalEngine engine) {
    if (level > nSpec.levels()) {
      return list;
    }

    // Get parameters for this level
    int n = nSpec.get(level); // The partition size for this level

    // Resolve d (Offset); a single offset is used for every level
    int d = n; // Default
    if (dSpec != null) {
      d = dSpec.levels() == 1 ? dSpec.get(1) : dSpec.get(level, n);
    }

    // Resolve k (Alignment {kL, kR})
    int kL = 1;
    int kR = -1;

    if (kList != null) {
      if (nSpec.levels() == 1) {
        // Simple 1D Case
        if (kList.isAST() && kList.arg1().isList()) {
          // Handle {{kL, kR}}
          IAST sub = (IAST) kList.arg1();
          if (sub.argSize() >= 1)
            kL = sub.get(1).toMachineInt();
          if (sub.argSize() >= 2)
            kR = sub.get(2).toMachineInt();
          else
            kR = kL;
        } else if (kList.size() > 1 && kList.arg1().isInteger()) {
          kL = kList.get(1).toMachineInt();
          kR = (kList.size() > 2) ? kList.get(2).toMachineInt() : kL;
        }
      }
    }

    // Perform 1D Partition on the current list
    IAST partitions = partition1D(list, n, d, kL, kR, pad);

    // Recursion for Nested Dimensions
    if (level < nSpec.levels()) {
      // Use F.ast(list.head()) to maintain the head (e.g., List or f)
      IASTAppendable nextLevelResult = F.ast(list.head(), partitions.size());
      for (IExpr sub : partitions) {
        if (sub.isAST()) {
          // We must map partitionRec across the elements inside this block (the rows),
          // rather than trying to partition the block's array structure directly.
          IAST subAST = (IAST) sub;
          IASTAppendable mappedSub = F.ast(subAST.head(), subAST.size());
          for (int i = 1; i <= subAST.argSize(); i++) {
            IExpr row = subAST.get(i);
            if (row.isAST()) {
              mappedSub
                  .append(partitionRec((IAST) row, nSpec, level + 1, dSpec, kList, pad, engine));
            } else {
              mappedSub.append(row);
            }
          }
          nextLevelResult.append(mappedSub);
        } else {
          nextLevelResult.append(sub);
        }
      }

      try {
        if (level == 1 && nSpec.levels() == 2) {
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
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_5;
  }
}
