package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * Symmetrize(tensor)
 * </pre>
 * 
 * * <blockquote>
 * <p>
 * returns the totally symmetric part of a tensor by averaging over all index permutations.
 * </p>
 * </blockquote> *
 * <h3>Examples</h3>
 * 
 * <pre>
 * >> Symmetrize({{a, b}, {c, d}})
 * {{a, (b+c)/2}, {(b+c)/2, d}}
 * </pre>
 */
public class Symmetrize extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST1()) {
      IExpr tensor = ast.arg1();

      // Find the rank (depth) of the tensor
      IExpr dimensionsExpr = engine.evaluate(F.Dimensions(tensor));
      if (dimensionsExpr.isList()) {
        IAST dimensions = (IAST) dimensionsExpr;
        int rank = dimensions.argSize();

        // Scalars (rank 0) and vectors (rank 1) are natively symmetric
        if (rank <= 1) {
          return tensor;
        }

        // Generate all index permutations: Permutations(Range(rank))
        IExpr permutationsExpr = engine.evaluate(F.Permutations(F.Range(rank)));
        if (permutationsExpr.isList()) {
          IAST permutations = (IAST) permutationsExpr;
          int numPerms = permutations.argSize();

          // Accumulate the sum of transposed tensors
          IASTAppendable sum = F.PlusAlloc(numPerms);
          for (int i = 1; i <= numPerms; i++) {
            IAST perm = (IAST) permutations.get(i);

            // Transpose[tensor, perm] shifts the indices to match the given permutation
            sum.append(F.Transpose(tensor, perm));
          }

          // S(T) = Sum[Transpose[T, p]] / rank!
          IExpr factorial = engine.evaluate(F.Factorial(F.ZZ(rank)));

          // F.Divide inherently threads across the sum of nested lists
          return engine.evaluate(F.Divide(sum, factorial));
        }
      }
    }

    // Returns F.NIL if advanced Symmetry specifications like Symmetrize(tensor,
    // Antisymmetric({1,2})) are called.
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // Allows up to 2 arguments so the engine won't reject Symmetrize(tensor, symmetry)
    return ARGS_1_2;
  }
}
