package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The <code>DiracComb</code> function, the sum of <code>DiracDelta(x-n)</code> over all integers
 * <code>n</code>. It is <code>0</code> for a real number <code>x</code> which isn't an integer and
 * an even function. Several arguments represent the product of the one-argument combs.
 */
public class DiracComb extends AbstractEvaluator implements IFunctionExpand {

  public DiracComb() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IASTMutable result = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr x = ast.get(i);
      IReal real = x.evalReal();
      if (real != null && !real.isNumIntValue()) {
        return F.C0;
      }
      IExpr negated = AbstractFunctionEvaluator.getNormalizedNegativeExpression(x);
      if (negated.isPresent()) {
        if (result.isNIL()) {
          result = ast.copy();
        }
        result.set(i, negated);
      }
    }
    return result;
  }

  /** <code>DiracComb(x, y, ...)</code> is <code>DiracComb(x)*DiracComb(y)*...</code> */
  @Override
  public IExpr functionExpand(IAST ast, EvalEngine engine) {
    if (ast.argSize() < 2) {
      return F.NIL;
    }
    IASTAppendable result = F.TimesAlloc(ast.argSize());
    for (int i = 1; i < ast.size(); i++) {
      result.append(F.unaryAST1(ast.head(), ast.get(i)));
    }
    return engine.evaluate(result);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
  }
}
