package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ArraySymbolExpr;
import org.matheclipse.core.expression.data.MatrixSymbolExpr;
import org.matheclipse.core.expression.data.VectorSymbolExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import it.unimi.dsi.fastutil.ints.IntList;

public class TensorProduct extends AbstractEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IBuiltInSymbol headSymbol = S.TensorProduct;
    int argSize = ast.argSize();
    if (argSize == 0) {
      // TensorProduct[] is the identity for the product (scalar 1)
      return F.C1;
    } else if (argSize == 1) {
      return ast.arg1();
    }

    // 1. Explicit List Evaluation (Matrices/Arrays)
    if (ast.arg1().isList() && ast.arg2().isList()) {
      IAST tensor1 = (IAST) ast.arg1();
      IntList dim1 = LinearAlgebraUtil.dimensions(tensor1, S.List);
      if (dim1.size() > 0) {
        for (int i = 2; i < ast.size(); i++) {
          IAST tensor2 = (IAST) ast.get(i);
          IntList dim2 = LinearAlgebraUtil.dimensions(tensor2, S.List);
          if (dim2.size() > 0) {
            IExpr temp = tensorProduct(tensor1, tensor2, dim1.size(), engine);
            if (temp.isPresent()) {
              if (temp.isList()) {
                tensor1 = (IAST) temp;
                dim1 = LinearAlgebraUtil.dimensions(tensor1, S.List);
                if (dim1.size() > 0) {
                  if (i < argSize) {
                    if (ast.get(i + 1).isList()) {
                      continue;
                    }
                  } else {
                    return tensor1;
                  }
                }
              }
              IASTAppendable result = F.ast(headSymbol);
              result.append(temp);
              result.appendAll(ast, i + 1, ast.size());
              return result;
            }
          }
          if (i == 2) {
            return F.NIL;
          }

          IASTAppendable result = F.ast(headSymbol);
          result.append(tensor1);
          result.appendAll(ast, i, ast.size());
          return result;
        }
        return tensor1;
      }
    } else {
      // Symbolic Evaluation

      // Handle Scalars (Times) and Flattening
      // Extract scalars from Times(...) and flatten nested TensorProducts
      IASTAppendable scalarParts = F.TimesAlloc(argSize);
      IASTAppendable tensorParts = F.ast(S.TensorProduct, argSize);

      boolean simplified = false;

      for (IExpr arg : ast) {
        simplified |= analyzeArgument(arg, scalarParts, tensorParts, engine);
      }

      // If no extraction or flattening happened, return F.NIL
      if (!simplified && scalarParts.isEmpty()) {
        return F.NIL;
      }

      // If everything ended up being scalar
      if (tensorParts.isEmpty()) {
        // Use oneIdentity1() (multiplicative identity 1) because scalarParts is Times
        return scalarParts.oneIdentity1();
      }

      // If we extracted scalars, return Times[scalars, TensorProduct[...]]
      IExpr tensorRest = (tensorParts.argSize() == 1) ? tensorParts.arg1() : tensorParts;

      if (scalarParts.isEmpty()) {
        return tensorRest;
      }

      return F.Times(scalarParts.oneIdentity1(), tensorRest);
    }
    return F.NIL;
  }

  /**
   * Analyzes an argument for TensorProduct. - Extracts scalars (Rank 0) from Times expressions. -
   * Flattens nested TensorProducts. - Adds tensors to tensorParts. * @return true if some
   * simplification (extraction/flattening) occurred.
   */
  private boolean analyzeArgument(IExpr arg, IASTAppendable scalarParts, IASTAppendable tensorParts,
      EvalEngine engine) {
    // Case A: Nested TensorProduct -> Flatten
    if (arg.isAST(S.TensorProduct)) {
      boolean changed = true;
      for (IExpr subArg : (IAST) arg) {
        analyzeArgument(subArg, scalarParts, tensorParts, engine);
      }
      return changed;
    }

    // Case B: Times -> Distribute (TensorProduct is linear)
    if (arg.isAST(S.Times)) {
      IAST times = (IAST) arg;
      IASTAppendable residueTimes = F.TimesAlloc(times.argSize());
      boolean foundScalarInTimes = false;

      for (IExpr factor : times) {
        if (getTensorRank(factor, engine) == 0) {
          scalarParts.append(factor);
          foundScalarInTimes = true;
        } else {
          residueTimes.append(factor);
        }
      }

      if (foundScalarInTimes) {
        IExpr residue = residueTimes.oneIdentity1();
        if (!residue.isOne()) {
          // Re-analyze the residue recursively
          if (residue.isAST(S.TensorProduct)) {
            analyzeArgument(residue, scalarParts, tensorParts, engine);
          } else {
            tensorParts.append(residue);
          }
        }
        return true;
      }
    }

    // Case C: Check Tensor Rank directly
    int rank = getTensorRank(arg, engine);
    if (rank == 0) {
      scalarParts.append(arg);
      return true; // Moved a scalar out
    } else {
      tensorParts.append(arg);
      return false;
    }
  }

  private int getTensorRank(IExpr arg, EvalEngine engine) {
    // 1. Fast path for Numbers (Rank 0)
    if (arg.isNumber()) {
      return 0;
    }

    // 2. Fast path for Explicit Lists
    if (arg.isList()) {
      return LinearAlgebraUtil.arrayDepth(arg);
    }

    // 3. Fast path for Known Symbolic Types (avoids evaluator overhead)
    if (arg instanceof VectorSymbolExpr) {
      return 1;
    }
    if (arg instanceof MatrixSymbolExpr) {
      return ((MatrixSymbolExpr) arg).getDimensions().argSize();
    }
    if (arg instanceof ArraySymbolExpr) {
      return ((ArraySymbolExpr) arg).getDimensions().argSize();
    }

    // 4. Fallback: Evaluate F.TensorRank
    IExpr rankExpr = engine.evaluate(F.TensorRank(arg));
    if (rankExpr.isInteger()) {
      return rankExpr.toIntDefault();
    }
    return -1; // Unknown rank -> treat as Tensor
  }

  /**
   * Evaluate expression: <code>Map((#1 * tensor2)&, tensor1, {tensor1Depth}) </code>, to get the
   * tensor product.
   *
   * @param tensor1 the first tensor
   * @param tensor2 the second tensor
   * @param tensor1Depth depth of the first tensor
   * @param engine
   * @return
   */
  protected static IExpr tensorProduct(final IAST tensor1, final IAST tensor2, int tensor1Depth,
      EvalEngine engine) {
    return engine
        .evaluate(F.Map(F.Function(F.Times(F.Slot1, tensor2)), tensor1, F.List(tensor1Depth)));
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
  }
}
