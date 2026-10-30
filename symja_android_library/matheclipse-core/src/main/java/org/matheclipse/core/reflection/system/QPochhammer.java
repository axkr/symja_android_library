package org.matheclipse.core.reflection.system;

import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.numerics.functions.WorkingPrecision;
import org.matheclipse.external.apfloat.ApcomplexThetaMath;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

public class QPochhammer extends AbstractFunctionEvaluator implements IFunctionExpand {
  @Override
  public IExpr functionExpand(IAST ast, EvalEngine engine) {
    if (ast.argSize() == 3 && ast.arg3().isInteger()) {
      int n = ast.arg3().toIntDefault();
      if (F.isPresent(n)) {
        return finiteProduct(ast.arg1(), ast.arg2(), n);
      }
    }
    return F.NIL;
  }

  /** From this <code>|n|</code> on the product <code>QPochhammer(a, q, n)</code> isn't expanded. */
  private static final int MAX_EXPANSION = 100000;

  /**
   * The finite product <code>Product(1 - a*q^k, {k, 0, n-1})</code> for <code>n &gt; 0</code>, its
   * reciprocal form <code>1 / Product(1 - a/q^k, {k, 1, -n})</code> for <code>n &lt; 0</code> and
   * <code>1</code> for <code>n == 0</code>.
   *
   * @return {@link F#NIL} if <code>|n|</code> is too large
   */
  private static IExpr finiteProduct(IExpr a, IExpr q, int n) {
    if (n > 0 && n < MAX_EXPANSION) {
      return factors(a, q, 0, n - 1, 1);
    } else if (n < 0 && n > -MAX_EXPANSION) {
      return F.Power(factors(a, q, 1, -n, -1), F.CN1);
    } else if (n == 0) {
      return F.C1;
    }
    return F.NIL;
  }

  /**
   * The product of <code>1 - a*q^(sign*k)</code> for <code>k = from..to</code>, multiplied
   * pairwise and exactly if <code>a</code> and <code>q</code> are rational numbers.
   */
  private static IExpr factors(IExpr a, IExpr q, int from, int to, int sign) {
    if (a.isRational() && q.isRational() && !q.isZero()) {
      IRational ra = (IRational) a;
      IRational rq = sign < 0 ? ((IRational) q).inverse() : (IRational) q;
      return F.productRational(k -> ra.multiply(rq.powerRational(k)).negate().add(F.C1), from, to,
          1);
    }
    return F.product(k -> F.Subtract(F.C1, F.Times(a, F.Power(q, sign < 0 ? k.negate() : k))),
        from, to);
  }

  public QPochhammer() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    int argSize = ast.argSize();

    IExpr a = ast.arg1();

    if (argSize == 1) {
      // QPochhammer(a) is equivalent to QPochhammer(a, a)
      return F.binaryAST2(S.QPochhammer, a, a);
    }

    IExpr q = ast.arg2();

    if (argSize == 2) {
      if (a.isZero()) {
        return F.C1;
      }
      if (q.isZero()) {
        return F.Subtract(F.C1, a);
      }
      if (a.isOne() && q.isOne()) {
        return F.C0;
      }
      if (a.isInexactNumber() && q.isInexactNumber()) {
        // the infinite product inside of the unit disk
        return WorkingPrecision.evaluate((IInexactNumber) a, (IInexactNumber) q, true,
            (x, y, precision) -> ApcomplexThetaMath.qPochhammer(x, y));
      }
      return F.NIL;
    }

    IExpr n = ast.arg3();

    if (n.isZero() || a.isZero()) {
      return F.C1;
    }

    if (n.isNumber() && a.isNumber() && q.isNumber()
        && (n.isInexactNumber() || a.isInexactNumber() || q.isInexactNumber())
        && !(n.isReal() && ((IReal) n).isNumIntValue())) {
      // (a;q)_n == (a;q)_Infinity/(a*q^n;q)_Infinity inside of the unit disk
      IExpr absQ = engine.evaluate(F.Abs(F.N(q)));
      if (absQ.isReal() && ((IReal) absQ).isLT(F.C1)) {
        return F.Divide(F.binaryAST2(S.QPochhammer, a, q),
            F.binaryAST2(S.QPochhammer, F.Times(a, F.Power(q, n)), q));
      }
      return F.NIL;
    }

    if (n.isInteger() || (n.isInexactNumber() && n.isReal() && ((IReal) n).isNumIntValue()
        && a.isNumber() && q.isNumber())) {
      // also the integer which a numeric evaluation has turned into an inexact number
      int nVal = n.toIntDefault();
      if (F.isPresent(nVal)) {
        return finiteProduct(a, q, nVal);
      }
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
  }

}
