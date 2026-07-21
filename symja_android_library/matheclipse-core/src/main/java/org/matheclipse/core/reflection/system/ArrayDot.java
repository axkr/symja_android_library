package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ArrayDot extends AbstractFunctionEvaluator {

  public ArrayDot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr a = ast.arg1();
    IExpr b = ast.arg2();

    // ArrayDot[a, b] defaults to k=1, which is mathematically equivalent to Dot[a, b]
    if (ast.isAST2()) {
      return engine.evaluate(F.Dot(a, b));

    } else if (ast.isAST3()) {
      IExpr arg3 = ast.arg3();

      if (arg3.isInteger()) {
        int k = arg3.toIntDefault();
        if (k < 0) {
          return F.NIL;
        }
        if (k == 0) {
          return engine.evaluate(F.TensorProduct(a, b));
        }
        if (k == 1) {
          return engine.evaluate(F.Dot(a, b));
        }

        // Evaluate ArrayDepth and cleanly resolve indices for TensorContract
        IExpr pExpr = engine.evaluate(F.ArrayDepth(a));
        IASTAppendable pairs = F.ListAlloc(k);
        for (int i = 1; i <= k; i++) {
          IExpr dimA = engine.evaluate(F.Plus(pExpr, F.ZZ(i - k)));
          IExpr dimB = engine.evaluate(F.Plus(pExpr, F.ZZ(i)));
          pairs.append(F.List(dimA, dimB));
        }
        return tensorContract(a, b, pairs, engine);
      } else if (arg3.isListOfLists()) {
        IExpr pExpr = engine.evaluate(F.ArrayDepth(a));
        IAST list = (IAST) arg3;
        IASTAppendable pairs = F.ListAlloc(list.argSize());

        for (int i = 1; i <= list.argSize(); i++) {
          IExpr item = list.get(i);
          if (item.isList2()) {
            IAST pair = (IAST) item;
            // Carefully evaluate the dimension index arithmetic
            IExpr dimB = engine.evaluate(F.Plus(pExpr, pair.arg2()));
            pairs.append(F.List(pair.arg1(), dimB));
          } else {
            return F.NIL;
          }
        }
        return tensorContract(a, b, pairs, engine);
      }
    }

    return F.NIL;
  }

  private IExpr tensorContract(IExpr a, IExpr b, IASTAppendable pairs, EvalEngine engine) {
    IExpr tensorProduct = S.TensorProduct.ofNIL(engine, a, b);
    if (tensorProduct.isPresent()) {
      return S.TensorContract.ofNIL(engine, tensorProduct, pairs);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
