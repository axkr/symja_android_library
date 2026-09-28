package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.calculus.Singularities;
import org.matheclipse.core.sympy.calculus.Util;

/**
 * <pre>
 * <code>FunctionMonotonicity(f, x)
 * FunctionMonotonicity({f, cons}, x)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * finds the monotonicity of the function <code>f</code> with the real variable <code>x</code>:
 * <code>1</code> for nondecreasing, <code>-1</code> for nonincreasing, <code>0</code> for
 * constant and <code>Indeterminate</code> for a function which is neither nonincreasing nor
 * nondecreasing.
 * </p>
 * </blockquote>
 *
 * <pre>
 * <code>FunctionConvexity(f, x)
 * FunctionConvexity({f, cons}, x)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * finds the convexity of the function <code>f</code> with the real variable <code>x</code>:
 * <code>1</code> for convex, <code>-1</code> for concave, <code>0</code> for affine and
 * <code>Indeterminate</code> for a function which is neither convex nor concave.
 * </p>
 * </blockquote>
 *
 * <p>
 * With the option <code>StrictInequalities-&gt;True</code> the functions test for a strictly
 * monotonic or a strictly convex/concave function.
 */
public class FunctionMonotonicity extends AbstractFunctionOptionEvaluator {

  private final boolean convexity;

  public FunctionMonotonicity() {
    this(false);
  }

  public FunctionMonotonicity(boolean convexity) {
    this.convexity = convexity;
  }

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] option, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 2 || argSize > 3) {
      return F.NIL;
    }
    if (argSize == 3 && ast.arg3() != S.Reals) {
      return F.NIL;
    }
    IExpr function = ast.arg1();
    IExpr domain = S.True;
    if (function.isList2()) {
      domain = function.second();
      function = function.first();
    }
    IExpr variable = ast.arg2();
    if (variable.isList1()) {
      variable = variable.first();
    }
    if (!variable.isSymbol() || !variable.isVariable()) {
      // only univariate functions are supported
      return F.NIL;
    }
    final ISymbol x = (ISymbol) variable;
    final boolean strict = option[0].isTrue();
    try {
      // the function must be real valued on the domain
      IExpr functionDomain = engine.evaluate(F.binaryAST2(S.FunctionDomain, function, x));
      if (!functionDomain.isFree(S.FunctionDomain, true)) {
        return F.NIL;
      }
      if (!functionDomain.isTrue()) {
        IExpr outside = Singularities.reduce(F.Not(functionDomain), domain, x, engine);
        if (outside.isNIL()) {
          return F.NIL;
        }
        if (!outside.isFalse()) {
          return S.Indeterminate;
        }
      }
      return convexity ? convexity(function, domain, x, strict, engine)
          : monotonicity(function, domain, x, strict, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  private static IExpr monotonicity(IExpr function, IExpr domain, ISymbol x, boolean strict,
      EvalEngine engine) {
    IExpr derivative = engine.evaluate(F.D(function, x));
    if (derivative.isZero()) {
      return strict ? S.Indeterminate : F.C0;
    }
    IExpr increasing = strict ? Singularities.isStrictlyIncreasing(function, domain, x, engine)
        : Singularities.isIncreasing(function, domain, x, engine);
    IExpr decreasing = strict ? Singularities.isStrictlyDecreasing(function, domain, x, engine)
        : Singularities.isDecreasing(function, domain, x, engine);
    if (increasing.isTrue()) {
      return decreasing.isTrue() ? F.C0 : F.C1;
    }
    if (decreasing.isTrue()) {
      return F.CN1;
    }
    if (increasing.isFalse() && decreasing.isFalse()) {
      return S.Indeterminate;
    }
    return F.NIL;
  }

  private static IExpr convexity(IExpr function, IExpr domain, ISymbol x, boolean strict,
      EvalEngine engine) {
    IExpr derivative = engine.evaluate(F.D(function, F.List(x, F.C2)));
    if (derivative.isZero()) {
      return strict ? S.Indeterminate : F.C0;
    }
    IExpr convex = Util.isConvex(function, x, domain, engine);
    IExpr concave = Util.isConvex(engine.evaluate(F.Negate(function)), x, domain, engine);
    if (strict) {
      // the second derivative is only allowed to be zero at isolated points
      IExpr zeros = Singularities.reduce(F.Equal(derivative, F.C0), domain, x, engine);
      if (zeros.isNIL()) {
        return F.NIL;
      }
      if (!zeros.isFalse() && !zeros.isEqual() && !zeros.isOr()) {
        return S.Indeterminate;
      }
    }
    if (convex.isTrue()) {
      return concave.isTrue() ? F.C0 : F.C1;
    }
    if (concave.isTrue()) {
      return F.CN1;
    }
    if (convex.isFalse() && concave.isFalse()) {
      return S.Indeterminate;
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, new IBuiltInSymbol[] {S.StrictInequalities}, new IExpr[] {S.False});
  }
}
