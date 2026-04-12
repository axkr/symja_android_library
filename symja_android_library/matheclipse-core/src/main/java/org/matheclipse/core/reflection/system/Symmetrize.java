package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ITensorAccess;
import it.unimi.dsi.fastutil.ints.IntList;

public class Symmetrize extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST1() || ast.isAST2()) {
      IExpr tensor = ast.arg1();
      IExpr sym = ast.isAST2() ? ast.arg2() : S.Symmetric;

      // Fold over a list of symmetries: Symmetrize(tensor, {sym1, sym2})
      if (sym.isList()) {
        IExpr current = tensor;
        IAST symList = (IAST) sym;
        for (int i = 1; i <= symList.argSize(); i++) {
          current = engine.evaluate(F.Symmetrize(current, symList.get(i)));
        }
        if (current instanceof ITensorAccess && ((ITensorAccess) current).isZeroTensor()) {
          // Symmetry specification `1` is only compatible with the zero tensor.
          Errors.printMessage(S.Symmetrize, "symm0", F.List(sym), engine);
        }
        return current;
      }

      // Find the rank (depth) of the tensor
      final IntList dims = LinearAlgebraUtil.dimensions(tensor, S.List, Integer.MAX_VALUE, true);
      if (dims == null) {
        if (tensor.isList() || tensor.isSparseArray()) {
          return F.NIL;
        }
        // TODO add generic TensorTranspose formula
        return F.NIL;
      }
      IExpr dimensionsExpr = engine.evaluate(F.Dimensions(tensor));
      if (dimensionsExpr.isList()) {
        IAST dimensions = (IAST) dimensionsExpr;
        int rank = dimensions.argSize();

        // Scalars and vectors are totally symmetric by default
        if (rank <= 1 && ast.isAST1()) {
          return tensor;
        }
        if (rank == 0) {
          return tensor;
        }

        IAST indicesList = null;
        boolean isAnti = false;

        // Parse the symmetry specification
        if (sym.equals(S.Symmetric)) {
          indicesList = (IAST) engine.evaluate(F.Range(F.C1, F.ZZ(rank)));
        } else if (sym.equals(S.Antisymmetric)) {
          indicesList = (IAST) engine.evaluate(F.Range(F.C1, F.ZZ(rank)));
          isAnti = true;
        } else if (sym.isAST(S.Symmetric, 2) && ((IAST) sym).arg1().isList()) {
          indicesList = (IAST) ((IAST) sym).arg1();
        } else if (sym.isAST(S.Antisymmetric, 2) && ((IAST) sym).arg1().isList()) {
          indicesList = (IAST) ((IAST) sym).arg1();
          isAnti = true;
        } else {
          return F.NIL;
        }

        int numIndices = indicesList.argSize();
        if (numIndices <= 1) {
          return tensor; // Nothing to permute
        }

        // Generate permutations of the target subset of indices
        IExpr permutationsExpr = engine.evaluate(F.Permutations(indicesList));
        if (permutationsExpr.isList()) {
          IAST permutations = (IAST) permutationsExpr;
          int numPerms = permutations.argSize();

          // Accumulate the sum of transposed tensors
          IASTAppendable sum = F.PlusAlloc(numPerms);

          for (int i = 1; i <= numPerms; i++) {
            IAST perm = (IAST) permutations.get(i);

            // Build the full transposition list {1, 2, ... rank}
            IASTAppendable fullPerm = F.ListAlloc(rank);
            for (int j = 1; j <= rank; j++) {
              fullPerm.append(F.ZZ(j));
            }

            // Replace the targeted indices with the active permutation mapping
            for (int j = 1; j <= numIndices; j++) {
              int originalIndex = indicesList.get(j).toIntDefault();
              if (originalIndex < 1 || originalIndex > rank) {
                return F.NIL;
              }
              fullPerm.set(originalIndex, perm.get(j));
            }

            // Transpose(tensor, fullPerm) shifts the dimensions to match the given permutation
            IExpr transposed = F.Transpose(tensor, fullPerm);

            if (isAnti) {
              // For Antisymmetric, multiply by the Signature of the permutation
              IExpr sign = engine.evaluate(F.Signature(fullPerm));
              transposed = F.Times(sign, transposed);
            }

            sum.append(transposed);
          }

          // S(T) = Sum(Transpose(T, p)) / numIndices!
          IExpr factorial = engine.evaluate(F.Factorial(F.ZZ(numIndices)));

          // F.Divide inherently threads across the sum of nested lists
          return engine.evaluate(F.Simplify(F.Divide(sum, factorial)));
        }
      }
    }
    return F.NIL;

  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // Allows 1 or 2 arguments
    return ARGS_1_2;
  }
}
