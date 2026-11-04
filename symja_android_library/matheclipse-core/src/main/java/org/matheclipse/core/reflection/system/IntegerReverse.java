package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
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
    // a MixedRadix base reverses the mixed radix digits and reads them with the same radices
    if (!base.isAST(S.MixedRadix, 2) && (!base.isInteger() || base.toIntDefault() < 2)) {
      if (base.isNumber()) {
        // Base `1` is not an integer greater than 1.
        return Errors.printMessage(S.IntegerReverse, "ibase", F.List(base, F.C1), engine);
      }
      return F.NIL;
    }
    if (base.isAST(S.MixedRadix, 2)) {
      return ast.isAST2() ? mixedRadixReverse((IInteger) n, base.first()) : F.NIL;
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

  /**
   * Reverse the digits of <code>n</code> in the mixed radix with the radices <code>list</code>: the
   * last digit is <code>n</code> modulo the last radix, the first digit takes what remains. The
   * reversed digits are read with the same radices.
   */
  private static IExpr mixedRadixReverse(IInteger n, IExpr list) {
    if (!list.isList() || n.isNegative()) {
      return F.NIL;
    }
    IAST radixList = (IAST) list;
    BigInteger[] radices = new BigInteger[radixList.argSize()];
    for (int i = 0; i < radices.length; i++) {
      if (!radixList.get(i + 1).isInteger() || !radixList.get(i + 1).isPositive()) {
        return F.NIL;
      }
      radices[i] = ((IInteger) radixList.get(i + 1)).toBigNumerator();
    }
    // the digits from the last one to the first one, without leading zeros
    List<BigInteger> digits = new ArrayList<BigInteger>();
    BigInteger value = n.toBigNumerator();
    for (int i = radices.length - 1; i >= 0 && value.signum() > 0; i--) {
      BigInteger[] qr = value.divideAndRemainder(radices[i]);
      digits.add(qr[1]);
      value = qr[0];
    }
    if (value.signum() > 0 || digits.isEmpty()) {
      digits.add(value);
    }
    // the reversed number has the first digit last; read it with the trailing radices
    BigInteger result = BigInteger.ZERO;
    int m = digits.size();
    for (int i = 0; i < m; i++) {
      if (i > 0) {
        result = result.multiply(radices[radices.length - m + i]);
      }
      result = result.add(digits.get(i));
    }
    return F.ZZ(result);
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
