package org.matheclipse.core.eval;

import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.eval.interfaces.IRealConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Not supported any longer
 */
@Deprecated
public class DoubleStackEvaluator {
  @Deprecated
  public static double evalSymbol(final ISymbol symbol) {
    IExpr value = symbol.assignedValue();
    if (value != null) {
      return ((IReal) value).doubleValue();
    }
    if (symbol.isRealConstant()) {
      // fast evaluation path
      IEvaluator realConstant = ((IBuiltInSymbol) symbol).getEvaluator();
      if (realConstant instanceof IRealConstant) {
        return ((IRealConstant) realConstant).evalReal();
      }
    }
    // slow evaluation path
    final IExpr result = F.evaln(symbol);
    if (result.isReal()) {
      return ((IReal) result).doubleValue();
    }
    if (result.isInfinity()) {
      return Double.POSITIVE_INFINITY;
    }
    if (result.isNegativeInfinity()) {
      return Double.NEGATIVE_INFINITY;
    }
    if (result.isIndeterminate()) {
      return Double.NaN;
    }
    throw new UnsupportedOperationException(
        "EvalDouble#evalSymbol() - no value assigned for symbol: " + symbol);
  }

  @Deprecated
  public static double evalAST(double[] stack, final int top, final IAST ast) {
    if (ast.isBuiltInFunction()) {
      final IBuiltInSymbol symbol = (IBuiltInSymbol) ast.head();
      final IEvaluator module = symbol.getEvaluator();
      if (module instanceof INumeric) {
        int newTop = top;
        // fast evaluation path
        if (top + ast.size() >= stack.length) {
          stack = new double[ast.size() + 50];
        }
        for (int i = 1; i < ast.size(); i++) {
          ++newTop;
          stack[newTop] = DoubleStackEvaluator.eval(stack, newTop, ast.get(i));
        }
        return ((INumeric) module).evalReal(stack, newTop, ast.argSize());
      }
    }
    // slow evaluation path
    final IExpr result = F.evaln(ast);
    if (result.isReal()) {
      return ((IReal) result).doubleValue();
    }
    if (result instanceof IComplexNum && F.isZero(((IComplexNum) result).imDoubleValue())) {
      return ((IComplexNum) result).reDoubleValue();
    }
    throw new UnsupportedOperationException("EvalDouble#evalAST(): " + ast);
  }

  @Deprecated
  public static double eval(final double[] stack, final int top, final IExpr expr) {
    if (expr instanceof IAST) {
      return evalAST(stack, top, (IAST) expr);
    }
    if (expr instanceof IReal) {
      return ((IReal) expr).doubleValue();
    }
    if (expr instanceof IComplexNum && F.isZero(((IComplexNum) expr).imDoubleValue())) {
      return ((IComplexNum) expr).reDoubleValue();
    }
    if (expr instanceof ISymbol) {
      return evalSymbol(((ISymbol) expr));
    }
    throw new UnsupportedOperationException("EvalDouble#eval(): " + expr);
  }
}
