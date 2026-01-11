package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ArraySymbolExpr;
import org.matheclipse.core.expression.data.MatrixSymbolExpr;
import org.matheclipse.core.expression.data.VectorSymbolExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Implementation of the TensorTranspose function.
 * <p>
 * Permutes the slots of a tensor.
 * </p>
 * <p>
 * <b>Capabilities:</b>
 * <ul>
 * <li>Supports explicit lists: <code>TensorTranspose[{{1, 2}, {3, 4}}, {2, 1}]</code></li>
 * <li>Supports Cycles notation: <code>TensorTranspose[matrix, Cycles[{{1, 2}}]]</code></li>
 * <li>Collapses nested transpositions:
 * <code>TensorTranspose[TensorTranspose[T, p1], p2]</code></li>
 * <li>Distributes over TensorProduct if permutation respects factor boundaries.</li>
 * <li>Supports symbolic tensors (MatrixSymbol, ArraySymbol, VectorSymbol) and simplifies based on
 * symmetry.</li>
 * </ul>
 * </p>
 */
public class TensorTranspose extends AbstractEvaluator {

  public TensorTranspose() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    IExpr tensor = ast.arg1();
    IExpr permSpec = ast.arg2();

    // 1. Determine Tensor Rank
    int rank = getTensorRank(tensor, engine);
    if (rank < 0) {
      // Rank could not be determined
      return F.NIL;
    }

    // 2. Normalize Permutation (Cycles -> List)
    IExpr permListExpr = normalizePermutation(permSpec, rank);
    if (!permListExpr.isPresent()) {
      return F.NIL;
    }
    IAST permList = (IAST) permListExpr;

    // 3. Optimization: Identity Permutation
    if (isIdentityPermutation(permList)) {
      return tensor;
    }

    // 4. Handle Explicit Lists (Data)
    if (tensor.isList()) {
      return F.Transpose(tensor, permList);
    }

    // 5. Handle Nested TensorTranspose
    if (tensor.isAST(S.TensorTranspose)) {
      IAST innerTranspose = (IAST) tensor;
      if (innerTranspose.argSize() == 2) {
        IExpr innerPermSpec = innerTranspose.arg2();
        IExpr innerPermListExpr = normalizePermutation(innerPermSpec, rank);

        if (innerPermListExpr.isPresent()) {
          IExpr composedExpr = composePermutations((IAST) innerPermListExpr, permList);
          if (composedExpr.isPresent()) {
            return F.TensorTranspose(innerTranspose.arg1(), composedExpr);
          }
        }
      }
      return F.NIL;
    }

    // 6. Handle TensorProduct
    if (tensor.isAST(S.TensorProduct)) {
      IExpr productResult = transposeTensorProduct((IAST) tensor, permList, rank, engine);
      if (productResult.isPresent()) {
        return productResult;
      }
      return F.NIL;
    }

    // 7. Handle Symbolic Tensor Symmetry
    // If the tensor has defined symmetries (e.g., Symmetric[{1,2}]),
    // and the permutation respects them, simplify to the tensor itself (or -tensor).
    IExpr symmetrySimplified = checkSymbolicSymmetry(tensor, permList, rank);
    if (symmetrySimplified.isPresent()) {
      return symmetrySimplified;
    }

    // 8. Return canonical form if input was Cycles notation, otherwise NIL
    if (permSpec.isAST(S.Cycles)) {
      return F.TensorTranspose(tensor, permList);
    }

    return F.NIL;
  }

  // --- Helper Methods ---

  private int getTensorRank(IExpr tensor, EvalEngine engine) {
    // 1. Symbolic Objects
    if (tensor instanceof VectorSymbolExpr) {
      return 1;
    }
    if (tensor instanceof MatrixSymbolExpr) {
      // MatrixSymbol always rank 2, or check dimensions size
      return ((MatrixSymbolExpr) tensor).getDimensions().argSize();
    }
    if (tensor instanceof ArraySymbolExpr) {
      return ((ArraySymbolExpr) tensor).getDimensions().argSize();
    }

    // 2. Explicit Lists
    if (tensor.isList()) {
      return LinearAlgebraUtil.arrayDepth(tensor);
    }

    // 3. Symbolic Expression (TensorRank)
    IExpr rankExpr = engine.evaluate(F.TensorRank(tensor));
    if (rankExpr.isInteger()) {
      return rankExpr.toIntDefault();
    }

    return -1;
  }

  /**
   * Checks if the permutation is invariant under the tensor's declared symmetry. Currently supports
   * Symmetric[{...}].
   */
  private IExpr checkSymbolicSymmetry(IExpr tensor, IAST permList, int rank) {
    IExpr symmetry = F.NIL;

    if (tensor instanceof MatrixSymbolExpr) {
      symmetry = ((MatrixSymbolExpr) tensor).getSymmetry();
    } else if (tensor instanceof ArraySymbolExpr) {
      symmetry = ((ArraySymbolExpr) tensor).getSymmetry();
    }

    if (!symmetry.isPresent() || symmetry.equals(S.None)) {
      return F.NIL;
    }

    // Handle Symmetric[{indices...}]
    // Definition: The tensor is invariant under any permutation of the specified indices.
    // We need to check if the current 'permList' is generated by permutations within that set.

    if (symmetry.isAST(S.Symmetric, 2)) {
      IAST symArgs = (IAST) symmetry;
      if (symArgs.argSize() >= 1 && symArgs.arg1().isList()) {
        IAST allowedIndices = (IAST) symArgs.arg1();
        Set<Integer> symmetricSet = new HashSet<>();
        for (IExpr idx : allowedIndices) {
          symmetricSet.add(idx.toIntDefault());
        }

        // Check: For every index i, if i != permList[i], then both i and permList[i] must be in the
        // symmetric set.
        // And generally, the permutation must act *inside* the set.

        // Let's identify which indices are moved.
        for (int i = 1; i <= rank; i++) {
          int target = permList.get(i).toIntDefault(); // The index that ends up at position i
          // Wait, permList definition in TensorTranspose:
          // {p1, p2} means the new 1st dim is the old p1-th dim.
          // effectively this maps index position k to position where p_k is?
          // Standard definition: T'_ i1..in = T_ ip1..ipn.
          // So if we swap 1 and 2 ({2, 1}), T'_ij = T_ji.
          // If T is symmetric (T_ij = T_ji), then T' = T.

          if (target != i) {
            // The dimension at position 'i' in the output comes from 'target' in input.
            // If the tensor is symmetric w.r.t {a, b}, swapping dimensions a and b is identity.

            // We simplify the check:
            // Any index position that is moved must be within the Symmetric set.
            if (!symmetricSet.contains(i) || !symmetricSet.contains(target)) {
              return F.NIL; // Permutation affects an index not covered by symmetry
            }
          }
        }
        // If we passed the loop, the permutation is confined to the symmetric indices.
        // For 'Symmetric', the value is unchanged.
        if (symmetry.first().isList2()) {
          return F.NIL;
        }
        return tensor;
      }
      // Handle Symmetric[All] or just Symmetric
      else if (symArgs.argSize() == 0 || symArgs.equals(S.All)) {
        return tensor;
      }
    }

    // Future: Handle Antisymmetric (return F.Times(F.CN1, tensor) if odd permutation)

    return F.NIL;
  }

  private IExpr normalizePermutation(IExpr spec, int rank) {
    if (spec.isList()) {
      IAST list = (IAST) spec;
      if (list.argSize() != rank) {
        return F.NIL;
      }
      return list;
    }
    if (spec.isAST(S.Cycles)) {
      return cyclesToPermutationList((IAST) spec, rank);
    }
    return F.NIL;
  }

  private IExpr cyclesToPermutationList(IAST cyclesAST, int rank) {
    if (cyclesAST.argSize() < 1 || !cyclesAST.arg1().isList()) {
      return F.NIL;
    }

    int[] p = new int[rank];
    for (int i = 0; i < rank; i++)
      p[i] = i + 1;

    IAST cycleData = (IAST) cyclesAST.arg1();

    for (IExpr cycleExpr : cycleData) {
      if (!cycleExpr.isList())
        continue;
      IAST cycle = (IAST) cycleExpr;
      int size = cycle.argSize();

      if (size < 2)
        continue;

      Map<Integer, Integer> map = new HashMap<>(size);
      for (int k = 1; k <= size; k++) {
        int from = cycle.get(k).toIntDefault();
        int to = (k == size) ? cycle.get(1).toIntDefault() : cycle.get(k + 1).toIntDefault();

        if (from < 1 || from > rank || to < 1 || to > rank) {
          return F.NIL;
        }
        map.put(from, to);
      }

      for (int i = 0; i < rank; i++) {
        if (map.containsKey(p[i])) {
          p[i] = map.get(p[i]);
        }
      }
    }

    return F.List(p);
  }

  private IExpr composePermutations(IAST inner, IAST outer) {
    int rank = inner.argSize();
    if (outer.argSize() != rank)
      return F.NIL;

    IASTAppendable result = F.ListAlloc(rank);
    for (int i = 1; i <= rank; i++) {
      int outerIdx = outer.get(i).toIntDefault();
      if (outerIdx < 1 || outerIdx > rank)
        return F.NIL;
      result.append(inner.get(outerIdx));
    }
    return result;
  }

  private boolean isIdentityPermutation(IAST perm) {
    for (int i = 1; i <= perm.argSize(); i++) {
      if (perm.get(i).toIntDefault() != i) {
        return false;
      }
    }
    return true;
  }

  private IExpr transposeTensorProduct(IAST product, IAST perm, int totalRank, EvalEngine engine) {
    int argSize = product.argSize();
    int[] ranks = new int[argSize];
    int[] offsets = new int[argSize];
    int currentOffset = 0;

    for (int i = 0; i < argSize; i++) {
      offsets[i] = currentOffset;
      int r = getTensorRank(product.get(i + 1), engine);
      if (r < 0)
        return F.NIL;
      ranks[i] = r;
      currentOffset += r;
    }

    if (currentOffset != totalRank)
      return F.NIL;

    List<Integer> newFactorOrder = new ArrayList<>(argSize);
    List<IASTAppendable> newFactorPerms = new ArrayList<>(argSize);

    int permIdx = 1;
    while (permIdx <= totalRank) {
      int sourceIdx = perm.get(permIdx).toIntDefault();

      int factorIdx = -1;
      int localIdx = -1;

      for (int i = 0; i < argSize; i++) {
        if (sourceIdx > offsets[i] && sourceIdx <= offsets[i] + ranks[i]) {
          factorIdx = i;
          localIdx = sourceIdx - offsets[i];
          break;
        }
      }

      if (factorIdx == -1)
        return F.NIL;

      IASTAppendable localPerm = F.ListAlloc(ranks[factorIdx]);

      localPerm.append(localIdx);
      permIdx++;

      for (int k = 1; k < ranks[factorIdx]; k++) {
        if (permIdx > totalRank)
          return F.NIL;

        int nextSourceIdx = perm.get(permIdx).toIntDefault();

        if (nextSourceIdx <= offsets[factorIdx]
            || nextSourceIdx > offsets[factorIdx] + ranks[factorIdx]) {
          return F.NIL;
        }

        localPerm.append(nextSourceIdx - offsets[factorIdx]);
        permIdx++;
      }

      newFactorOrder.add(factorIdx);
      newFactorPerms.add(localPerm);
    }

    IASTAppendable resultProduct = F.ast(S.TensorProduct, newFactorOrder.size());
    for (int i = 0; i < newFactorOrder.size(); i++) {
      int fIdx = newFactorOrder.get(i);
      IExpr factor = product.get(fIdx + 1);
      IASTAppendable localP = newFactorPerms.get(i);

      if (isIdentityPermutation(localP)) {
        resultProduct.append(factor);
      } else {
        resultProduct.append(F.TensorTranspose(factor, localP));
      }
    }

    return resultProduct;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}