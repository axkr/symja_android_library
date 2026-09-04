package org.matheclipse.core.builtin;

import java.util.function.DoubleFunction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

public class PiecewiseFunctions {

  private static class BernsteinBasis extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr dArg1 = ast.arg1();
      IExpr nArg2 = ast.arg2();
      IExpr x = ast.arg3();
      if (dArg1.isReal() && nArg2.isReal() && x.isReal()) {
        int d = dArg1.toIntDefault();
        if (d < 0) {
          // Non-negative machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.C1), engine);
        }
        IInteger di = F.ZZ(d);
        int n = nArg2.toIntDefault();
        if (n < 0) {
          // Non-negative machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.C1), engine);
        }
        if (n > d) {
          // Index `1` should be a machine sized integer between `2` and `3`.
          return Errors.printMessage(ast.topHead(), "invidx2", F.list(nArg2, F.C0, di), engine);
        }
        if (engine.evalLess(F.C0, x, F.C1)) {
          IInteger ni = F.ZZ(n);
          // Binomial(d, ni) * x^ni * (1 - x)^(di - ni)
          return F.Times(F.Binomial(di, ni), F.Power(x, ni),
              F.Power(F.Subtract(F.C1, x), F.Subtract(di, ni)));
        } else {
          return F.C0;
        }
        // return F.Piecewise(F.list1(F.list2(piece, condition)), F.C0);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NHOLDALL, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * Clip(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> in the range <code>-1</code> to <code>1</code>. Returns <code>-1
   * </code> if <code>expr</code> is less than <code>-1</code>. Returns <code>1</code> if <code>expr
   * </code> is greater than <code>1</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Clip(expr, {min, max})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> in the range <code>min</code> to <code>max</code>. Returns <code>
   * min</code> if <code>expr</code> is less than <code>min</code>. Returns <code>max</code> if
   * <code>expr</code> is greater than <code>max</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Clip(expr, {min, max}, {vMin, vMax})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>expr</code> in the range <code>min</code> to <code>max</code>. Returns <code>
   * vMin</code> if <code>expr</code> is less than <code>min</code>. Returns <code>vMax</code> if
   * <code>expr</code> is greater than <code>max</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Clip(Sin(Pi/7))
   * Sin(Pi/7)
   *
   * &gt;&gt; Clip(Tan(E))
   * Tan(E)
   *
   * &gt;&gt; Clip(Tan(2*E)
   * -1
   *
   * &gt;&gt; Clip(Tan(-2*E))
   * 1
   *
   * &gt;&gt; Clip(x)
   * Clip(x)
   *
   * &gt;&gt; Clip(Tan(2*E), {-1/2,1/2})
   * -1/2
   *
   * &gt;&gt; Clip(Tan(-2*E), {-1/2,1/2})
   * 1/2
   *
   * &gt;&gt; Clip(Tan(E), {-1/2,1/2}, {a,b})
   * Tan(E)
   *
   * &gt;&gt; Clip(Tan(2*E), {-1/2,1/2}, {a,b})
   * a
   *
   * &gt;&gt; Clip(Tan(-2*E), {-1/2,1/2}, {a,b})
   * b
   * </pre>
   *
   * <p>
   * A bound may be infinite, which is how a one sided range is written:
   *
   * <pre>
   * &gt;&gt; Clip(-5, {0, Infinity})
   * 0
   *
   * &gt;&gt; Clip({-5, 1, 3.5}, {0, Infinity})
   * {0,1,3.5}
   * </pre>
   *
   * <p>
   * <code>Clip</code> maps over a list in its first argument, and over the bounds of an
   * <code>Interval</code> or <code>IntervalData</code>:
   *
   * <pre>
   * &gt;&gt; Clip({-2, 0, 2})
   * {-1,0,1}
   *
   * &gt;&gt; Clip(Interval({-3, 5}))
   * Interval({-1,1})
   *
   * &gt;&gt; Clip(IntervalData({-3, Less, Less, 5}))
   * IntervalData({-1,LessEqual,LessEqual,1})
   * </pre>
   */
  private static final class Clip extends AbstractFunctionEvaluator {

    /**
     * Map <code>Clip</code> over an {@link S#Interval}. <code>Clip</code> is continuous and non
     * decreasing, so the image of an interval is the interval between the images of its bounds.
     *
     * @return {@link F#NIL} if a bound cannot be clipped
     */
    private static IExpr clipInterval(IAST interval, IExpr min, IExpr max) {
      IAST normalized = IntervalSym.normalize(interval);
      if (normalized.isNIL()) {
        return F.NIL;
      }
      IASTAppendable result = F.IntervalAlloc(normalized.argSize());
      for (int i = 1; i < normalized.size(); i++) {
        IExpr sub = normalized.get(i);
        if (!sub.isList2()) {
          return F.NIL;
        }
        IAST bounds = (IAST) sub;
        IExpr lower = Arithmetic.clip(bounds.arg1(), min, max);
        IExpr upper = Arithmetic.clip(bounds.arg2(), min, max);
        if (lower.isNIL() || upper.isNIL()) {
          return F.NIL;
        }
        result.append(F.list(lower, upper));
      }
      return result;
    }

    /**
     * Map <code>Clip</code> over an {@link S#IntervalData}, whose bounds can be open.
     *
     * <p>
     * The bound relations do not simply carry across, because <code>Clip</code> saturates: every
     * point of <code>(-5,-1)</code> is clipped onto <code>-1</code>, so <code>-1</code> belongs to
     * the image even though neither bound of the domain does. A mapped bound is therefore closed
     * if the original bound was closed <i>or</i> the bound was saturated. When both mapped bounds
     * land on the same value the two flags describe one point and have to agree - otherwise
     * <code>Clip(IntervalData({-5,Less,Less,-1}))</code> would produce
     * <code>IntervalData({-1,LessEqual,Less,-1})</code>, which normalizes to the empty set and
     * loses the value.
     *
     * @return {@link F#NIL} if a bound cannot be clipped
     */
    private static IExpr clipIntervalData(IAST intervalData, IExpr min, IExpr max) {
      if (intervalData.isAST0()) {
        // the empty interval set
        return F.NIL;
      }
      IAST normalized;
      try {
        normalized = IntervalDataSym.normalize(intervalData);
      } catch (ArgumentTypeException atex) {
        return F.NIL;
      }
      if (normalized.isNIL()) {
        return F.NIL;
      }
      IASTAppendable result = F.IntervalDataAlloc(normalized.argSize());
      for (int i = 1; i < normalized.size(); i++) {
        IExpr sub = normalized.get(i);
        if (!sub.isList4()) {
          return F.NIL;
        }
        IAST bounds = (IAST) sub;
        IExpr lower = bounds.arg1();
        IExpr upper = bounds.arg4();
        IExpr clippedLower = Arithmetic.clip(lower, min, max);
        IExpr clippedUpper = Arithmetic.clip(upper, min, max);
        if (clippedLower.isNIL() || clippedUpper.isNIL()) {
          // a symbolic bound - which side of the range it falls on must not be guessed
          return F.NIL;
        }
        boolean lowerClosed = bounds.arg2() == S.LessEqual || !clippedLower.equals(lower);
        boolean upperClosed = bounds.arg3() == S.LessEqual || !clippedUpper.equals(upper);
        if (clippedLower.equals(clippedUpper)) {
          lowerClosed = lowerClosed || upperClosed;
          upperClosed = lowerClosed;
        }
        result.append(F.List(clippedLower, //
            lowerClosed ? S.LessEqual : S.Less, //
            upperClosed ? S.LessEqual : S.Less, //
            clippedUpper));
      }
      return result;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr min = F.CN1;
      IExpr max = F.C1;
      IExpr vMin = F.CN1;
      IExpr vMax = F.C1;
      if (ast.size() >= 3) {
        IExpr arg2 = ast.arg2();
        if (!arg2.isList2()) {
          // { min, max } as 2nd argument expected
          return F.NIL;
        }
        min = arg2.first();
        max = arg2.second();
        vMin = min;
        vMax = max;
        if (ast.size() == 4) {
          IExpr arg3 = ast.arg3();
          if (!arg3.isList2()) {
            // { vMin, vMax } as 3rd argument expected
            return F.NIL;
          }
          vMin = arg3.first();
          vMax = arg3.second();
        }
      }

      IExpr x = ast.arg1();
      if (x.isSparseArray()) {
        x = x.normal(false);
      }
      if (x.isList()) {
        // Clip maps over a list in its first argument, but only when every element can be
        // clipped - a list holding a symbolic element stays unevaluated as a whole. This is not
        // the LISTABLE attribute, which would thread the { min, max } bounds list as well.
        IAST list = (IAST) x;
        IASTAppendable result = F.ListAlloc(list.argSize());
        for (int i = 1; i < list.size(); i++) {
          IExpr temp = evaluate(ast.setAtCopy(1, list.get(i)), engine);
          if (temp.isNIL()) {
            return F.NIL;
          }
          result.append(temp);
        }
        return result;
      }

      if (vMin.equals(min) && vMax.equals(max)) {
        // mapping the bounds of an interval is only sound while Clip is non decreasing, i.e.
        // while it saturates onto its own range and not onto arbitrary vMin/vMax values
        if (x.isInterval()) {
          IExpr mapped = clipInterval((IAST) x, min, max);
          if (mapped.isPresent()) {
            return mapped;
          }
        } else if (x.isIntervalData()) {
          IExpr mapped = clipIntervalData((IAST) x, min, max);
          if (mapped.isPresent()) {
            return mapped;
          }
        }
      }

      return Arithmetic.clip(x, min, max, vMin, vMax);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }



    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * DiscreteDelta(n1, n2, n3, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * <code>DiscreteDelta</code> function returns <code>1</code> if all the <code>ni</code> are
   * <code>0</code>. Returns <code>0</code> otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; DiscreteDelta(0, 0, 0.0)
   * 1
   * </pre>
   */
  private static class DiscreteDelta extends AbstractFunctionEvaluator {

    private static IExpr removeEval(final IAST ast, EvalEngine engine) {
      IASTAppendable result = F.NIL;
      int size = ast.size();
      int j = 1;
      for (int i = 1; i < size; i++) {
        IExpr expr = engine.evaluate(ast.get(i));
        INumber temp = expr.evalNumber();
        if (temp != null) {
          if (temp.isZero()) {
            if (result.isNIL()) {
              result = ast.removeAtClone(i);
            } else {
              result.remove(j);
            }
            continue;
          }
          if (temp.isNumber()) {
            return F.C0;
          }
        }
        if (expr.isNonZeroComplexResult()) {
          return F.C0;
        }
        j++;
      }
      return result;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int size = ast.size();
      if (size == 1) {
        return F.C1;
      }
      if (size > 1) {
        IExpr arg1 = engine.evaluate(ast.arg1());

        if (size == 2) {
          INumber temp = arg1.evalNumber();
          if (temp != null) {
            if (temp.isZero()) {
              return F.C1;
            }
            return F.C0;
          }
          if (arg1.isNonZeroComplexResult()) {
            return F.C0;
          }
          return F.NIL;
        }

        IExpr result = removeEval(ast, engine);
        if (result.isPresent()) {
          if (result.isAST()) {
            if (result.isAST() && ((IAST) result).size() > 1) {
              return result;
            }
            return F.C1;
          }
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL, Attribute.ORDERLESS, Attribute.NUMERICFUNCTION);
    }
  }


  private static class Initializer {

    private static void init() {
      S.BernsteinBasis.setEvaluator(new BernsteinBasis());
      S.Clip.setEvaluator(new Clip());
      S.DiscreteDelta.setEvaluator(new DiscreteDelta());
      S.KroneckerDelta.setEvaluator(new KroneckerDelta());
      S.Piecewise.setEvaluator(new Piecewise());
      S.Ramp.setEvaluator(new Ramp());
      S.RealAbs.setEvaluator(new RealAbs());
      S.RealSign.setEvaluator(new RealSign());
      S.SawtoothWave.setEvaluator(new SawtoothWave());
      S.UnitBox.setEvaluator(new UnitBox());
      S.Unitize.setEvaluator(new Unitize());
      S.UnitStep.setEvaluator(new UnitStep());
      S.UnitTriangle.setEvaluator(new UnitTriangle());
    }
  }


  /**
   *
   *
   * <pre>
   * KroneckerDelta(arg1, arg2, ... argN)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * if all arguments <code>arg1</code> to <code>argN</code> are equal return <code>1</code>,
   * otherwise return <code>0</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; KroneckerDelta(42)
   * 0
   *
   * &gt;&gt; KroneckerDelta(42, 42.0, 42)
   * 1
   * </pre>
   */
  private static class KroneckerDelta extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int size = ast.size();
      if (ast.head() != S.KroneckerDelta) {
        // KroneckerDelta has no operator form in Mathematica, which leaves KroneckerDelta(x)[1,2]
        // alone. Evaluation arrives here because AbstractAST#evaluate() dispatches on topHead(),
        // which looks through nested heads; without this the outer arguments are compared as
        // though the head were the plain symbol, answering 0.
        return F.NIL;
      }
      if (size == 1) {
        return F.C1;
      }
      if (size > 1) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        IExpr temp = arg1.evalNumber();
        if (temp == null) {
          temp = arg1;
        }
        if (size == 2) {
          if (temp.isZero()) {
            return F.C1;
          }
          if (temp.isNonZeroComplexResult()) {
            return F.C0;
          }
          return F.NIL;
        }
        arg1 = temp;
        for (int i = 2; i < size; i++) {
          IExpr expr = engine.evaluate(ast.get(i));
          if (expr.equals(arg1)) {
            continue;
          }
          temp = expr.evalNumber();
          if (temp == null) {
            return F.NIL;
          } else {
            if (temp.equals(arg1)) {
              continue;
            }
          }
          return F.C0;
        }
        return F.C1;
      }
      return F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL, Attribute.ORDERLESS, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * Piecewise({{expr1, cond1}, ...})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents a piecewise function.
   *
   * </blockquote>
   *
   * <pre>
   * Piecewise({{expr1, cond1}, ...}, expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents a piecewise function with default <code>expr</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Piecewise">Wikipedia - Piecewise</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Piecewise({{-x, x&lt;0}, {x, x&gt;=0}})/.{{x-&gt;-3}, {x-&gt;-1/3}, {x-&gt;0}, {x-&gt;1/2}, {x-&gt;5}}
   * {3,1/3,0,1/2,5}
   * </pre>
   *
   * <p>
   * Heaviside function
   *
   * <pre>
   * &gt;&gt; Piecewise({{0, x &lt;= 0}}, 1)
   * Piecewise({{0, x &lt;= 0}}, 1)
   * </pre>
   *
   * <p>
   * Piecewise defaults to <code>0</code>, if no other case is matching.<br>
   *
   * <pre>
   * &gt;&gt; Piecewise({{1, False}})
   * 0
   *
   * &gt;&gt; Piecewise({{0 ^ 0, False}}, -1)
   * -1
   * </pre>
   */
  private static final class Piecewise extends AbstractFunctionEvaluator {

    private static boolean appendPiecewise(IASTAppendable list, IExpr function, IExpr predicate) {
      if (list.argSize() > 0 && list.get(list.argSize()).first().equals(function)) {
        IExpr previousPredicate = list.get(list.argSize()).second();
        list.set(list.argSize(), F.list(function, F.Or(previousPredicate, predicate)));
        return false;
      }
      list.append(F.list(function, predicate));
      return true;
    }

    private static IASTAppendable createPiecewise(IASTAppendable piecewiseAST, IAST resultList) {
      if (piecewiseAST.isNIL()) {
        piecewiseAST = F.ast(S.Piecewise);
        piecewiseAST.append(resultList);
      }
      return piecewiseAST;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isList()) {
        // Piecewise is HoldAll, so that the value of a branch is only evaluated once its condition
        // holds - Piecewise({{0^0, False}}, -1) must not complain about 0^0. The list of branches
        // itself still has to be evaluated, otherwise a symbol holding the branches
        // (Piecewise(x) with x = {{1, True}}) would never be recognized as a list of pairs.
        IExpr evaledArg1 = engine.evaluateNIL(arg1);
        if (evaledArg1.isPresent() && evaledArg1.isList()) {
          return ast.setAtCopy(1, evaledArg1);
        }
      }
      if (!arg1.isList(x -> x.isList2())) {
        if (arg1.isEmptyList()) {
          if (ast.isAST2()) {
            return ast.arg2();
          }
          return F.C0;
        }
        // The first argument `1` of `2` is not a list of pairs.
        return Errors.printMessage(ast.topHead(), "pairs", F.list(arg1, ast.topHead()), engine);
      }
      IAST matrix = (IAST) arg1;
      IExpr defaultValue = F.C0;
      if (ast.isAST1()) {
        return ast.appendClone(F.C0);
      }
      if (ast.isAST2()) {
        defaultValue = ast.arg2();
      }
      IExpr condition;
      int matrixSize = matrix.size();
      IASTAppendable result = F.NIL;
      IASTAppendable piecewiseAST = F.NIL;
      boolean evaluated = false;
      boolean noBoolean = false;
      for (int i = 1; i < matrixSize; i++) {
        final IAST row = matrix.getAST(i);
        condition = row.arg2();
        if (condition.isTrue()) {
          if (!evaluated && i == matrixSize - 1) {
            if (!row.arg1().isSymbol()) {
              return row.arg1();
            }
            return F.NIL;
          }
          if (noBoolean) {
            // result = appendPiecewise(result, row.arg1(), S.True, matrixSize);
            piecewiseAST = createPiecewise(piecewiseAST, result);
            piecewiseAST.append(row.arg1());
            return piecewiseAST;
          }
          return row.arg1();
        } else if (condition.isFalse()) {
          evaluated = true;
          continue;
        }
        condition = engine.evaluateNIL(condition);
        if (condition.isPresent()) {
          evaluated = true;
          if (condition.isTrue()) {
            if (noBoolean) {
              if (result.isNIL()) {
                result = F.ListAlloc(matrixSize);
              }
              appendPiecewise(result, row.arg1(), S.True);
              return createPiecewise(piecewiseAST, result);
            }
            return row.arg1();
          } else if (condition.isFalse()) {
            continue;
          }
        }
        IExpr rowArg1 = engine.evaluateNIL(row.arg1());
        if (rowArg1.isPresent()) {
          evaluated = true;
        } else {
          rowArg1 = row.arg1();
        }
        if (i == matrixSize - 1 && rowArg1.equals(defaultValue)) {
          evaluated = true;
          continue;
        }

        if (result.isNIL()) {
          result = F.ListAlloc(matrixSize);
        }
        if (!appendPiecewise(result, rowArg1, condition.orElse(row.arg2()))) {
          evaluated = true;
        }
        piecewiseAST = createPiecewise(piecewiseAST, result);
        noBoolean = true;
        continue;
      }
      if (!noBoolean) {
        return defaultValue;
      } else {
        if (evaluated) {
          piecewiseAST = createPiecewise(piecewiseAST, F.List());
          piecewiseAST.append(engine.evaluate(defaultValue));
          return piecewiseAST;
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set NUMERICFUNCTION
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }
  private static final class Ramp extends AbstractEvaluator {

    public Ramp() {}

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr expr = ast.arg1();
      if (expr.isPositiveResult() || expr.isInfinity()) {
        return expr;
      }
      if (expr.isNegativeResult() || expr.isNegativeInfinity() || expr.isZero()) {
        return F.C0;
      }
      // Ramp(0) == 0, so the non-strict bounds determine the result as well
      if (expr.isNonNegativeResult()) {
        return expr;
      }
      if (expr.isNonPositiveResult()) {
        return F.C0;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }


  private static final class RealAbs extends AbstractEvaluator {
    @Override
    public boolean evalIsReal(IAST ast) {
      if (ast.argSize() == 1) {
        return ast.arg1().isRealResult();
      }
      return false;
    }

    private static final class AbsNumericFunction implements DoubleFunction<IExpr> {
      final ISymbol symbol;

      public AbsNumericFunction(ISymbol symbol) {
        this.symbol = symbol;
      }

      @Override
      public IExpr apply(double value) {
        if (value < Integer.MAX_VALUE && value > Integer.MIN_VALUE) {
          double result = Math.abs(value);
          if (result > 0.0) {
            return symbol;
          }
        }
        return F.NIL;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInfinity() || arg1.isNegativeInfinity()) {
        return F.CInfinity;
      }
      if (arg1.isNumber()) {
        if (arg1.isReal()) {
          return arg1.abs();
        }
        return F.NIL;
      }
      if (engine.isNumericMode() && arg1.isNumericFunction(true)) {
        IExpr temp = engine.evalN(arg1);
        if (temp.isReal()) {
          return temp.abs();
        }
        if (temp.isNumber()) {
          return F.NIL;
        }
      }
      if (arg1.isNegativeResult()) {
        return F.Negate(arg1);
      }
      if (arg1.isNonNegativeResult()) {
        return arg1;
      }
      if (arg1.isSymbol()) {
        ISymbol sym = (ISymbol) arg1;
        return sym.mapConstantDouble(new AbsNumericFunction(sym));
      }

      if (arg1.isInterval()) {
        return IntervalSym.abs((IAST) arg1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  private static final class RealSign extends AbstractCoreFunctionEvaluator {

    @Override
    public boolean evalIsReal(IAST ast) {
      return (ast.argSize() == 1);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr result = F.NIL;
      IExpr arg1 = engine.evaluateNIL(ast.arg1());
      if (arg1.isPresent()) {
        result = F.RealSign(arg1);
        arg1 = result;
      } else {
        arg1 = ast.arg1();
      }
      if (arg1.isList()) {
        return ((IAST) arg1).mapThread(F.RealSign(F.Slot1), 1);
      }
      if (arg1.isReal()) {
        if (arg1.isPositiveResult()) {
          return F.C1;
        }
        if (arg1.isNegativeResult()) {
          return F.CN1;
        }
        return F.C0;
      }
      if (arg1.isAST(S.RealSign, 2)) {
        return arg1;
      }
      if (arg1.isNumericFunction(true)) {
        IExpr temp = engine.evalN(arg1);
        if (temp.isReal()) {
          return temp.sign();
        }
        if (temp.isNumber()) {
          return F.NIL;
        }
      }
      if (arg1.isInfinity()) {
        return F.C1;
      }
      if (arg1.isNegativeInfinity()) {
        return F.CN1;
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }


  private static class SawtoothWave extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST2()) {
        if (arg1.isList2()) {
          IExpr min = arg1.first();
          IExpr max = arg1.second();
          IExpr x = ast.arg2();
          if (x.isList()) {
            return x.mapThread(ast.setAtCopy(2, F.Slot1), 2);
          }
          if (min.isReal() && max.isReal()) {
            IReal minNumber = (IReal) min;
            IReal maxNumber = (IReal) max;
            if (x.isNumericFunction() && x.isRealResult()) {
              // min + (max - min) * (x - Floor(x))
              return engine.evaluate(
                  F.Plus(min, F.Times(maxNumber.subtract(minNumber), F.Subtract(x, F.Floor(x)))));
            }
            if (x.isPossibleZero(true, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
              return min;
            }
          }

        }
      } else {
        IExpr x = arg1;
        if (x.isList()) {
          return x.mapThread(ast.setAtCopy(1, F.Slot1), 1);
        }
        if (x.isNumericFunction() && x.isRealResult()) {
          return engine.evaluate(F.Subtract(x, F.Floor(x)));
        }
        if (x.isPossibleZero(true, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          return F.C0;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

  }


  /**
   *
   *
   * <pre>
   * Unitize(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * maps a non-zero <code>expr</code> to <code>1</code>, and a zero <code>expr</code> to <code>0
   * </code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Unitize((E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi)
   * 0
   * </pre>
   */
  private static class Unitize extends AbstractEvaluator {

    @Override
    public boolean evalIsReal(IAST ast) {
      return ast.argSize() == 1 || (ast.argSize() == 2 && ast.arg2().isPositiveResult());
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      if (ast.isAST2()) {
        IExpr dx = ast.arg2();
        if (dx.isNegativeResult()) {
          // The threshold `1` should be positive.
          return Errors.printMessage(ast.topHead(), "post", F.List(dx), engine);
        }
        return unitize(x, dx, engine);
      }
      return unitize(x, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }

    private IExpr unitize(IExpr x, EvalEngine engine) {
      if (x.isNumber()) {
        return x.isZero() ? F.C0 : F.C1;
      }
      if (S.PossibleZeroQ.ofQ(engine, x)) {
        return F.C0;
      }
      if (x.isNegativeResult() || x.isPositiveResult()) {
        return F.C1;
      }
      IExpr temp = x.evalNumber();
      if (temp != null) {
        if (temp.isNegative()) {
          return F.C1;
        }
        if (S.PossibleZeroQ.ofQ(engine, temp)) {
          return F.C1;
        }
        return F.C0;
      }
      return F.NIL;
    }

    private IExpr unitize(IExpr x, IExpr dx, EvalEngine engine) {
      // Piecewise({{1, dx-Abs(x)<= 0}}, 0)
      IExpr temp = engine.evaluate(F.Subtract(dx, F.Abs(x)));
      if (temp.isNegativeResult()) {
        return F.C1;
      }
      if (temp.isPositiveResult()) {
        return F.C0;
      }
      if (S.PossibleZeroQ.ofQ(engine, temp)) {
        return F.C1;
      }
      temp = temp.evalNumber();
      if (temp != null) {
        if (temp.isNegative()) {
          return F.C1;
        }
        if (S.PossibleZeroQ.ofQ(engine, temp)) {
          return F.C1;
        }
        return F.C0;
      }
      return F.NIL;
    }
  }


  /**
   *
   *
   * <pre>
   * UnitStep(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>0</code>, if <code>expr</code> is less than <code>0</code> and returns <code>1
   * </code>, if <code>expr</code> is greater equal than <code>0</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; UnitStep(-42)
   * 0
   * </pre>
   */
  private static class UnitStep extends AbstractEvaluator implements INumeric {

    @Override
    public double evalReal(double[] stack, int top, int size) {
      for (int i = top - size + 1; i < top + 1; i++) {
        if (stack[i] < 0.0) {
          return 0.0;
        }
      }
      return 1.0;
    }

    /**
     * Unit step <code>1</code> for all x greater equal <code>0</code>. <code>0</code> in all other
     * cases,
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int size = ast.size();
      if (size > 1) {
        for (int i = 1; i < size; i++) {
          IExpr expr = ast.get(i);
          IReal temp = expr.evalReal();
          if (temp != null) {
            if (temp.complexSign() < 0) {
              return F.C0;
            } else {
              continue;
            }
          } else {
            expr = engine.evaluate(expr);
            if (expr.isNegativeInfinity()) {
              return F.C0;
            }
            if (expr.isInfinity()) {
              continue;
            }
            if (expr.isNegativeResult()) {
              return F.C0;
            }
            if (expr.isNonNegativeResult()) {
              continue;
            }
            if (expr.isInterval1()) {
              IExpr l = expr.lower();
              IExpr u = expr.upper();
              if (l.isReal() && u.isReal()) {
                IReal min = (IReal) l;
                IReal max = (IReal) u;
                if (min.complexSign() < 0) {
                  if (max.complexSign() < 0) {
                    return F.Interval(F.list(F.C0, F.C0));
                  } else {
                    if (size == 2) {
                      return F.Interval(F.list(F.C0, F.C1));
                    }
                  }
                } else {
                  if (max.complexSign() < 0) {
                    if (size == 2) {
                      return F.Interval(F.list(F.C1, F.C0));
                    }
                  } else {
                    if (size == 2) {
                      return F.Interval(F.list(F.C1, F.C1));
                    }
                    continue;
                  }
                }
              }
            }
          }
          return F.NIL;
        }
      }
      return F.C1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.ORDERLESS, Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  private static class UnitBox extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      // multiple arguments are treated as a Product
      // UnitBox(x, y) becomes UnitBox(x) * UnitBox(y)
      if (ast.size() > 2) {
        IAST result = ast.mapThread(x -> F.UnitBox(x));
        return result.apply(S.Times);
      }

      // single argument UnitBox(x)
      IExpr arg1 = ast.arg1();

      // Numeric Evaluation for Reals (Doubles, Rationals, Integers)
      // Definition: 1 for Abs(x) <= 1/2, 0 for Abs(x) > 1/2
      if (arg1.isReal()) {
        IExpr absArg = arg1.abs();

        // If |x| > 1/2, the result is 0
        if (absArg.greater(F.C1D2).isTrue()) {
          return F.C0;
        }

        // If |x| <= 1/2, the result is 1
        if (absArg.lessEqual(F.C1D2).isTrue()) {
          return F.C1;
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.ORDERLESS, Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  private static class UnitTriangle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      // multiple arguments are treated as a Product
      // UnitTriangle(x, y) becomes UnitTriangle(x) * UnitTriangle(y)
      if (ast.size() > 2) {
        IAST result = ast.mapThread(x -> F.UnitTriangle(x));
        return result.apply(S.Times);
      }

      // single argument UnitTriangle(x)
      IExpr arg1 = ast.arg1();

      // Numeric Evaluation for Reals (Doubles, Rationals, Integers)
      // Definition: Max(0, 1 - Abs(x))
      if (arg1.isReal()) {
        IExpr absArg = arg1.abs();

        // If |x| >= 1, the result is 0
        if (absArg.greaterEqual(F.C1).isTrue()) {
          return F.C0;
        }

        // If |x| < 1, the result is 1 - |x|
        if (absArg.less(F.C1).isTrue()) {
          return F.C1.subtract(absArg);
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.ORDERLESS, Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }


  public static void initialize() {
    Initializer.init();
  }

  private PiecewiseFunctions() {}
}
