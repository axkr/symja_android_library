package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.PiecewiseFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The <code>HeavisidePi</code> function returns <code>1</code> for all <code>Abs(x)</code> less
 * than <code>1/2</code> and <code>0</code> for all <code>Abs(x)</code> greater than
 * <code>1/2</code>,
 */
public class HeavisidePi extends AbstractEvaluator implements IFunctionExpand {

  public HeavisidePi() {}

  /**
   * The arguments of an even function without their minus signs, then the numeric arguments of
   * several split off as a factor.
   *
   * @return {@link F#NIL} if neither applies
   */
  static IExpr rewriteArguments(final IAST ast) {
    // an even function: f(-x) = f(x)
    IAST even = evenArguments(ast);
    if (even.isPresent()) {
      return even;
    }
    if (ast.size() > 2 && !ast.forAll(x -> x.isNumericFunction(true))) {
      // f(x, y, 1/4) becomes f(1/4) * f(x, y)
      return PiecewiseFunctions.splitNumericArguments(ast);
    }
    return F.NIL;
  }

  /**
   * The arguments of an even function without their minus signs: <code>f(-x)</code> is
   * <code>f(x)</code>.
   *
   * @return {@link F#NIL} if no argument has a minus sign
   */
  static IAST evenArguments(final IAST ast) {
    IASTMutable even = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr expr = ast.get(i);
      if (!expr.isNumber() && expr.isNegativeSigned()) {
        if (even.isNIL()) {
          even = ast.copy();
        }
        even.set(i, expr.negate());
      }
    }
    return even;
  }

  /**
   * <code>HeavisideTheta(x+1/2)-HeavisideTheta(x-1/2)</code> for every argument <code>x</code>,
   * multiplied together.
   */
  @Override
  public IExpr functionExpand(IAST ast, EvalEngine engine) {
    if (ast.argSize() < 1) {
      return F.NIL;
    }
    IASTAppendable result = F.TimesAlloc(ast.argSize());
    for (int i = 1; i < ast.size(); i++) {
      IExpr x = ast.get(i);
      result.append(F.Subtract(F.HeavisideTheta(F.Plus(F.C1D2, x)),
          F.HeavisideTheta(F.Plus(F.CN1D2, x))));
    }
    return engine.evaluate(result.oneIdentity1());
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int size = ast.size();
    IExpr rewritten = HeavisidePi.rewriteArguments(ast);
    if (rewritten.isPresent()) {
      return rewritten;
    }
    if (size > 1) {
      for (int i = 1; i < size; i++) {
        IExpr expr = ast.get(i);
        IReal temp = expr.abs().evalReal();
        if (temp != null) {
          if (temp.isGT(F.C1D2)) {
            return F.C0;
          } else if (temp.isLT(F.C1D2)) {
            continue;
          }
        }
        return F.NIL;
      }
    }
    return F.C1;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.ORDERLESS, Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
  }
}
