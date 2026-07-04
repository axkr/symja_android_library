package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class DigitSum extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1().isInteger()) {
      IInteger radix = F.C10;

      if (ast.isAST2()) {
        if (ast.arg2().isInteger()) {
          radix = (IInteger) ast.arg2();
        } else {
          return F.NIL;
        }
      }

      if (radix.isLT(F.C2)) {
        // Base `1` is not an integer greater than or equal to `2`.
        return Errors.printMessage(S.DigitSum, "ibase", F.List(radix, F.C1), engine);
      }

      IInteger iArg1 = ((IInteger) ast.arg1()).abs();
      if (iArg1.isZero()) {
        return F.C0;
      }

      BigInteger value = iArg1.toBigNumerator();
      BigInteger b = radix.toBigNumerator();
      BigInteger sum = BigInteger.ZERO;

      // Optimization for base 10
      if (b.equals(BigInteger.TEN)) {
        String str = value.toString();
        long s = 0;
        for (int i = 0; i < str.length(); i++) {
          s += str.charAt(i) - '0';
        }
        return F.ZZ(s);
      }

      // Optimization for bases up to 36
      if (b.compareTo(BigInteger.valueOf(36)) <= 0) {
        String str = value.toString(b.intValue());
        long s = 0;
        for (int i = 0; i < str.length(); i++) {
          char c = str.charAt(i);
          if (c >= '0' && c <= '9') {
            s += c - '0';
          } else if (c >= 'a' && c <= 'z') {
            s += c - 'a' + 10;
          }
        }
        return F.ZZ(s);
      }

      // General case for arbitrary large bases
      while (value.compareTo(BigInteger.ZERO) > 0) {
        BigInteger[] divmod = value.divideAndRemainder(b);
        sum = sum.add(divmod[1]);
        value = divmod[0];
      }

      return F.ZZ(sum);
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE);
  }
}