package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * <code>IntegerReverse(n)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the integer whose digits are reversed with respect to the integer <code>n</code>.
 * </p>
 * </blockquote>
 *
 * <pre>
 * <code>IntegerReverse(n, b)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the integer whose digits in base <code>b</code> are reversed.
 * </p>
 * </blockquote>
 *
 * <pre>
 * <code>IntegerReverse(n, b, len)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * pads the digits of <code>n</code> with zeros on the left to the length <code>len</code>.
 * </p>
 * </blockquote>
 */
public class IntegerReverse extends AbstractFunctionEvaluator {

  public IntegerReverse() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr n = ast.arg1();
    if (!n.isInteger()) {
      return F.NIL;
    }
    IExpr base = ast.argSize() >= 2 ? ast.arg2() : F.C10;
    if (!base.isInteger() || base.toIntDefault() < 2) {
      if (base.isNumber()) {
        // Base `1` is not an integer greater than 1.
        return Errors.printMessage(S.IntegerReverse, "ibase", F.List(base, F.C1), engine);
      }
      return F.NIL;
    }
    IExpr digits;
    if (ast.isAST3()) {
      if (!ast.arg3().isInteger() || ast.arg3().isNegative()) {
        return F.NIL;
      }
      digits = engine.evaluate(F.ternaryAST3(S.IntegerDigits, n, base, ast.arg3()));
    } else {
      digits = engine.evaluate(F.binaryAST2(S.IntegerDigits, n, base));
    }
    if (!digits.isList()) {
      return F.NIL;
    }
    return engine.evaluate(F.binaryAST2(S.FromDigits, F.Reverse(digits), base));
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.LISTABLE);
  }
}
