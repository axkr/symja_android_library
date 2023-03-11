package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.BinaryBindIth1st;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.rules.MatrixDRules;

public class MatrixD extends AbstractFunctionEvaluator implements MatrixDRules {
  private static final Logger LOGGER = LogManager.getLogger();

  public MatrixD() {}

  @Override
  public IAST getRuleAST() {
    return RULES;
  }

  /**
   * For the referenced formula numbers (XX) see:
   * <a href="https://archive.org/details/imm3274/">Internet Archive - The Matrix Cookbook</a>
   */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IOFunctions.printExperimental(S.MatrixD);
    if (ast.size() < 3) {
      return F.NIL;
    }
    final IExpr fx = ast.arg1();
    if (fx.isIndeterminate()) {
      return S.Indeterminate;
    }
    if (ast.size() > 3) {
      // reduce arguments by folding MatrixD[fxy, x, y] to MatrixD[ MatrixD[fxy, x], y] ...
      return ast.foldLeft((x, y) -> engine.evaluateNIL(F.MatrixD(x, y)), fx, 2);
    }

    IExpr x = ast.arg2();
    if (!(x.isVariable() || x.isList())) {
      // `1` is not a valid variable.
      return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(x), engine);
    }

    if (fx.isList()) {
      IAST list = (IAST) fx;
      // thread over first list
      return list.mapThreadEvaled(engine, F.ListAlloc(list.size()), ast, 1);
    }

    if (x.isList()) {
      // MatrixD[fx_, {...}]
      IAST xList = (IAST) x;
      if (xList.isAST1() && xList.arg1().isListOfLists()) {
        IAST subList = (IAST) xList.arg1();
        IASTAppendable result = F.ListAlloc(subList.size());
        result.appendArgs(subList.size(), i -> F.MatrixD(fx, F.list(subList.get(i))));
        return result;
      } else if (xList.isAST1() && xList.arg1().isList()) {
        IAST subList = (IAST) xList.arg1();
        return subList.mapLeft(F.ListAlloc(), (a, b) -> engine.evaluateNIL(F.MatrixD(a, b)), fx);
      } else if (xList.isAST2()) {
        if (xList.arg1().isList()) {
          x = F.list(xList.arg1());
        } else {
          x = xList.arg1();
        }
        IExpr arg2 = xList.arg2();
        int n = arg2.toIntDefault();
        if (n >= 0) {
          IExpr temp = fx;
          for (int i = 0; i < n; i++) {
            temp = S.MatrixD.ofNIL(engine, temp, x);
            if (temp.isNIL()) {
              return F.NIL;
            }
          }
          return temp;
        }
        if (arg2.isFree(num -> num.isNumber(), false)) {
          if (fx.equals(x)) {
            return S.$SingleEntryMatrix;
          }
          if (fx.isAST()) {
            // MatrixD(a_+b_+c_,x_) -> MatrixD(a,x)+MatrixD(b,x)+MatrixD(c,x)
            return fx.mapThread(F.MatrixD(F.Slot1, xList), 1); // (35)
          }
          return F.NIL;
        }
        if (!x.isVariable()) {
          // `1` is not a valid variable.
          return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(x), engine);
        }
        if (arg2.isAST()) {
          return F.NIL;
        }
        // Multiple derivative specifier `1` does not have the form {variable, n} where n is a
        // symbolic expression or a non-negative integer.
        return IOFunctions.printMessage(ast.topHead(), "dvar", F.list(xList), engine);
      }
      return F.NIL;
    }

    if (!x.isVariable()) {
      // `1` is not a valid variable.
      return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(x), engine);
    }

    return binaryMatrixD(fx, x);
  }

  /**
   * Evaluate <code>MatrixD(functionO>fX, x)</code> for some general cases.
   *
   * <p>
   * Rule numbers are from <a href="https://archive.org/details/imm3274">Internet Archive - The
   * Matrix Cookbook</a>
   *
   * @param functionOfX the function of <code>x</code>
   * @param x derive w.r.t this variable
   * @return
   */
  private static IExpr binaryMatrixD(final IExpr functionOfX, IExpr x) {
    if (functionOfX.isFree(x, true)) {
      return F.C0; // (33)
    }

    if (functionOfX.equals(x)) {
      // MatrixD[x_,x_] -> $SingleEntryMatrix
      return S.$SingleEntryMatrix;
    }

    if (functionOfX.isAST() && functionOfX.size() >= 2) {
      final IAST function = (IAST) functionOfX;
      // final IExpr arg1 = function.arg1();
      if (function.isPlus()) {
        // MatrixD(a_+b_+c_,x_) -> MatrixD(a,x)+MatrixD(b,x)+MatrixD(c,x)
        return function.mapThread(F.MatrixD(F.Slot1, x), 1); // (35)
      } else if (function.isTimes() // (38)
          || function.isASTSizeGE(S.Dot, 3) // (37)
          || function.isASTSizeGE(S.KroneckerProduct, 3)) { // (39)
        return function.map(F.PlusAlloc(16), new BinaryBindIth1st(function, F.MatrixD(S.Null, x)));
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }
}
