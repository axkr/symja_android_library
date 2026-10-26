package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>BSplineBasis({d, {u1, u2, ..., um}}, n, x)</code> - the <code>n</code>-th B-spline basis
 * function (counted from <code>0</code>) of degree <code>d</code> on the knot vector
 * <code>{u1, ..., um}</code> at <code>x</code>;
 * <code>BSplineBasis(d, x)</code> - the uniform B-spline of degree <code>d</code> on the knots
 * <code>Range(0, d+1)/(d+1)</code>.
 *
 * <p>
 * The value is computed by the Cox-de Boor recursion, exactly for exact knots and an exact
 * <code>x</code>. A degree <code>0</code> basis function is <code>1</code> on its half open knot
 * interval, except that the last non-empty interval also contains the last knot, so the last basis
 * function of a clamped knot vector is <code>1</code> at the end of the knot range. A symbolic
 * <code>x</code> leaves the call unevaluated.
 */
public class BSplineBasis extends AbstractFunctionEvaluator {

  public BSplineBasis() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int degree;
    IReal[] knots;
    int index;
    IExpr x;
    if (ast.isAST2()) {
      // BSplineBasis(d, x)
      degree = ast.arg1().toIntDefault();
      if (degree < 0) {
        return F.NIL;
      }
      knots = new IReal[degree + 2];
      for (int i = 0; i <= degree + 1; i++) {
        knots[i] = F.QQ(i, degree + 1);
      }
      index = 0;
      x = ast.arg2();
    } else if (ast.isAST3() && ast.arg1().isList2() && ast.arg1().second().isList()) {
      IAST spec = (IAST) ast.arg1();
      degree = spec.arg1().toIntDefault();
      IAST knotList = (IAST) spec.arg2();
      if (degree < 0 || knotList.argSize() < degree + 2) {
        return F.NIL;
      }
      knots = new IReal[knotList.argSize()];
      for (int i = 1; i < knotList.size(); i++) {
        if (!knotList.get(i).isReal()) {
          return F.NIL;
        }
        knots[i - 1] = (IReal) knotList.get(i);
        if (i > 1 && knots[i - 1].isLT(knots[i - 2])) {
          // the knots must not decrease
          return F.NIL;
        }
      }
      index = ast.arg2().toIntDefault();
      // there are m-d-1 basis functions for m knots
      if (index < 0 || index > knots.length - degree - 2) {
        return F.NIL;
      }
      x = ast.arg3();
    } else {
      return F.NIL;
    }
    if (!x.isReal()) {
      return F.NIL;
    }
    if (x.isInexactNumber()) {
      IReal[] numericKnots = new IReal[knots.length];
      for (int i = 0; i < knots.length; i++) {
        numericKnots[i] = F.num(knots[i].evalf());
      }
      knots = numericKnots;
    }
    return basis(knots, degree, index, (IReal) x);
  }

  /**
   * The Cox-de Boor recursion for the basis function <code>index</code> of the given degree, with
   * a term whose knot difference vanishes taken as <code>0</code>.
   */
  private static IExpr basis(IReal[] knots, int degree, int index, IReal x) {
    IReal first = knots[0];
    IReal last = knots[knots.length - 1];
    IExpr zero = x.isInexactNumber() ? F.CD0 : F.C0;
    IExpr one = x.isInexactNumber() ? F.CD1 : F.C1;
    if (x.isLT(first) || x.isGT(last)) {
      return zero;
    }
    // the last non-empty knot interval is closed at the right end
    int lastInterval = -1;
    if (x.isGE(last)) {
      for (int i = knots.length - 2; i >= 0; i--) {
        if (knots[i].isLT(knots[i + 1])) {
          lastInterval = i;
          break;
        }
      }
    }
    IExpr[] values = new IExpr[degree + 1];
    for (int j = 0; j <= degree; j++) {
      int i = index + j;
      boolean inside = (knots[i].isLE(x) && x.isLT(knots[i + 1])) || i == lastInterval;
      values[j] = inside ? one : zero;
    }
    for (int k = 1; k <= degree; k++) {
      for (int j = 0; j <= degree - k; j++) {
        int i = index + j;
        IExpr value = zero;
        IExpr leftDenominator = knots[i + k].subtract(knots[i]);
        if (!values[j].isZero() && !leftDenominator.isZero()) {
          value = value.plus(x.subtract(knots[i]).divide(leftDenominator).times(values[j]));
        }
        IExpr rightDenominator = knots[i + k + 1].subtract(knots[i + 1]);
        if (!values[j + 1].isZero() && !rightDenominator.isZero()) {
          value = value.plus(
              knots[i + k + 1].subtract(x).divide(rightDenominator).times(values[j + 1]));
        }
        values[j] = value;
      }
    }
    return values[0];
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NUMERICFUNCTION);
  }
}
