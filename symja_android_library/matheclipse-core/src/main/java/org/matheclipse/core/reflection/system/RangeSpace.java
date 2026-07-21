package org.matheclipse.core.reflection.system;

import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class RangeSpace extends AbstractFunctionOptionEvaluator {

  public RangeSpace() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr arg1 = engine.evaluate(ast.arg1());
    int[] dims = arg1.isMatrix(false);

    if (dims != null) {
      // SVD should be used for approximate numeric matrices (containing floating-point numbers)
      boolean useSVD = engine.isNumericMode() || arg1.isNumericArray()
          || arg1.has(x -> x.isInexactNumber(), false);

      IExpr method = options[0];
      IExpr modulus = options[1];
      IExpr tolerance = options[2];
      IExpr zeroTest = options[3];
      Predicate<IExpr> zeroChecker =
          AbstractMatrix1Expr.optionZeroTest(ast, engine, zeroTest, tolerance);

      if (useSVD) {
        IExpr svdResult = engine.evaluate(F.SingularValueDecomposition(arg1));
        if (svdResult.isList() && svdResult.argSize() >= 2) {
          IAST uMatrix = (IAST) ((IAST) svdResult).arg1();
          IAST sMatrix = (IAST) ((IAST) svdResult).arg2();
          IAST uTransposed = (IAST) engine.evaluate(F.Transpose(uMatrix));
          IASTAppendable result = F.ListAlloc(sMatrix.argSize());

          int minDim = Math.min(sMatrix.argSize(), ((IAST) sMatrix.get(1)).argSize());

          for (int i = 1; i <= minDim; i++) {
            IExpr singularValue = ((IAST) sMatrix.get(i)).get(i);
            if (!zeroChecker.test(singularValue)) {
              if (i <= uTransposed.argSize()) {
                IExpr uRow = uTransposed.get(i);
                // The valid non-zero left singular vectors are scaled by the singular value
                IExpr scaledRow = engine.evaluate(F.Expand(F.Times(singularValue, uRow)));
                result.append(scaledRow);
              }
            }
          }
          return result;
        }
      }

      // Exact or symbolic matrices fallback
      IASTAppendable rowReduceAST = F.ast(S.RowReduce);
      rowReduceAST.append(F.Transpose(arg1));

      // Propagate options to RowReduce
      if (method.isPresent() && !method.equals(S.Automatic)) {
        rowReduceAST.append(F.Rule(S.Method, method));
      }
      if (modulus.isPresent() && !modulus.equals(F.C0)) {
        rowReduceAST.append(F.Rule(S.Modulus, modulus));
      }
      if (tolerance.isPresent() && !tolerance.equals(S.Automatic)) {
        rowReduceAST.append(F.Rule(S.Tolerance, tolerance));
      }
      if (zeroTest.isPresent() && !zeroTest.equals(S.Automatic)) {
        rowReduceAST.append(F.Rule(S.ZeroTest, zeroTest));
      }

      IExpr rowReduced = engine.evaluate(rowReduceAST);
      if (rowReduced.isList()) {
        IAST list = (IAST) rowReduced;
        IASTAppendable result = F.ListAlloc(list.argSize());

        for (int i = 1; i <= list.argSize(); i++) {
          IExpr row = list.get(i);
          if (row.isList()) {
            boolean allZero = true;
            for (int j = 1; j <= ((IAST) row).argSize(); j++) {
              if (!zeroChecker.test(((IAST) row).get(j))) {
                allZero = false;
                break;
              }
            }
            if (!allZero) {
              result.append(row);
            }
          }
        }
        return result;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, new IBuiltInSymbol[] {S.Method, S.Modulus, S.Tolerance, S.ZeroTest},
        new IExpr[] {S.Automatic, F.C0, S.Automatic, S.Automatic});
  }
}
