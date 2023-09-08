package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class IntervalFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Interval.setEvaluator(new Interval());
      S.IntervalComplement.setEvaluator(new IntervalComplement());
      S.ToIntervalData.setEvaluator(new ToIntervalData());
      S.IntervalData.setEvaluator(new IntervalData());
      S.IntervalMemberQ.setEvaluator(new IntervalMemberQ());
      S.IntervalIntersection.setEvaluator(new IntervalIntersection());
      S.IntervalUnion.setEvaluator(new IntervalUnion());
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Interval({a, b})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents the interval from <code>a</code> to <code>b</code>.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Interval_arithmetic">Wikipedia - Interval
   * arithmetic</a>
   * <li><a href="https://en.wikipedia.org/wiki/Interval_(mathematics)">Wikipedia - Interval
   * (mathematics)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Interval({1, 6}) * Interval({0, 2})
   * Interval({0,12})
   *
   * &gt;&gt; Interval({1.5, 6}) * Interval({0.1, 2.7})
   * Interval({0.15,16.2})
   *
   * &gt;&gt; Sign(Interval({-43, -42}))
   * -1
   *
   * &gt;&gt; Im(Interval({-Infinity, Infinity}))
   * 0
   *
   * &gt;&gt; ArcCot(Interval({-1, Infinity}))
   * Interval({-Pi/2,-Pi/4},{0,Pi/2})
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="IntervalIntersection.md">IntervalIntersection</a>,
   * <a href="IntervalUnion.md">IntervalUnion</a>
   */
  private static final class Interval extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isEvalFlagOff(IAST.BUILT_IN_EVALED)) {
        IAST result = IntervalSym.normalize(ast, engine);
        if (result.isPresent()) {
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  private static final class IntervalComplement extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      for (int i = 1; i < ast.size(); i++) {
        if (!ast.get(i).isIntervalData()) {
          return F.NIL;
        }
        IAST interval = (IAST) ast.get(i);
        for (int j = 1; j < interval.size(); j++) {
          if (!interval.get(j).isList4()) {
            return F.NIL;
          }
          IAST list1 = (IAST) interval.get(j);
          IExpr min1 = list1.arg1();
          IExpr max1 = list1.arg4();
          if (!min1.isRealResult() || !max1.isRealResult()) {
            return F.NIL;
          }
        }
      }
      IAST interval1 = (IAST) ast.arg1();
      interval1 = IntervalDataSym.normalize(interval1, engine);
      if (interval1.isInvalid()) {
        return F.NIL;
      }
      if (interval1.isNIL()) {
        interval1 = (IAST) ast.arg1();
      }
      IAST interval2 = (IAST) ast.arg2();
      interval2 = IntervalDataSym.normalize(interval2, engine);
      if (interval2.isInvalid()) {
        return F.NIL;
      }
      if (interval2.isNIL()) {
        interval2 = (IAST) ast.arg2();
      }
      IAST result = complementIntervalData(interval1, interval2, engine);
      if (result.size() == 1) {
        return result;
      }

      IAST normalized = IntervalDataSym.normalize(result, engine);
      if (normalized.isInvalid()) {
        return F.NIL;
      }
      return normalized.orElse(result);
    }

    /**
     * <p>
     * See: <a href=
     * "https://en.wikipedia.org/wiki/Complement_(set_theory)#Relative_complement">Complement (set
     * theory) - Relative complement</a>
     * 
     * @param interval1
     * @param interval2
     * @param engine
     * @return
     */
    private static IAST complementIntervalData(final IAST interval1, final IAST interval2,
        EvalEngine engine) {
      if (interval1.isEmptyIntervalData()) {
        return F.CEmptyIntervalData;
      }
      if (interval2.isEmptyIntervalData()) {
        return interval1;
      }

      IASTAppendable intervalUnion = F.ast(S.IntervalUnion, 3);
      for (int i = 1; i < interval1.size(); i++) {
        final IAST list1 = (IAST) interval1.get(i);
        final IExpr min1 = list1.arg1();
        final IBuiltInSymbol minEnd1 = (IBuiltInSymbol) list1.arg2();
        final IBuiltInSymbol maxEnd1 = (IBuiltInSymbol) list1.arg3();
        final IExpr max1 = list1.arg4();
        // use rule: list1 \setminus (list2A \cup list2B)=(list1 \setminus list2A) \cap (list1
        // \setminus list2B)
        IASTAppendable intervalIntersection = F.ast(S.IntervalIntersection, interval2.size());
        for (int j = 1; j < interval2.size(); j++) {
          IASTAppendable segmentResult = F.ast(S.IntervalData, interval2.size());
          intervalIntersection.append(segmentResult);
          final IAST list2 = (IAST) interval2.get(j);
          final IExpr min2 = list2.arg1();
          final IBuiltInSymbol minEnd2 = (IBuiltInSymbol) list2.arg2();
          final IBuiltInSymbol maxEnd2 = (IBuiltInSymbol) list2.arg3();
          final IExpr max2 = list2.arg4();
          if (S.Less.ofQ(engine, max2, min1) || S.Less.ofQ(engine, max1, min2)) {
            segmentResult.append(F.List(min1, minEnd1, maxEnd1, max1));
            continue;
          }
          if (S.Less.ofQ(engine, min1, min2)) {
            // left side
            segmentResult.append(F.List(min1, minEnd1, toggle(min2, minEnd2), min2));
          } else if (S.Equal.ofQ(engine, min1, min2)) {
            // left side
            if (minEnd2 == S.Less && minEnd1 == S.LessEqual && !min1.isNegativeInfinity()) {
              segmentResult.append(F.List(min1, S.LessEqual, S.LessEqual, min1));
            }
          }

          if (S.Less.ofQ(engine, max2, max1)) {
            // right side
            segmentResult.append(F.List(max2, toggle(max2, maxEnd2), maxEnd1, max1));
          } else if (S.Equal.ofQ(engine, max2, max1)) {
            // right side
            if (maxEnd2 == S.Less && maxEnd1 == S.LessEqual && !max1.isInfinity()) {
              segmentResult.append(F.List(max1, S.LessEqual, S.LessEqual, max1));
            }
          }

        }
        if (intervalIntersection.argSize() > 1) {
          IExpr temp = engine.evaluate(intervalIntersection);
          if (temp.isIntervalData()) {
            intervalUnion.append(temp);
          } else {
            // TODO print error ?
            return F.NIL;
          }
        } else if (intervalIntersection.argSize() == 1) {
          intervalUnion.append(intervalIntersection.arg1());
        } else if (intervalIntersection.argSize() == 0) {
          intervalUnion.append(F.CEmptyIntervalData);
        }

      }
      if (intervalUnion.argSize() == 1) {
        return (IAST) intervalUnion.arg1();
      }
      IExpr eval = engine.evaluate(intervalUnion);
      if (eval.isIntervalData()) {
        return (IAST) eval;
      }
      // TODO print error ?
      return F.NIL;
    }

    private static IBuiltInSymbol toggle(IExpr value, IBuiltInSymbol symbol) {
      if (symbol == S.Less) {
        if (value.isInfinity() || value.isNegativeInfinity()) {
          return S.Less;
        }
        return S.LessEqual;
      }
      if (symbol == S.LessEqual) {
        return S.Less;
      }
      return symbol;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static final class IntervalData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isEvalFlagOff(IAST.BUILT_IN_EVALED)) {
        return IntervalDataSym.normalize(ast, engine);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class ToIntervalData extends AbstractFunctionEvaluator {


    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isOr()) {
        IAST orAST = (IAST) ast.arg1();
        return orAST.mapThread(x -> F.ToIntervalData(x, ast.arg2())).eval(engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>IntervalMemberQ(interval, interval-or-real-number)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code>, if <code>interval-or-real-number</code> is completly sourrounded by
   * <code>interval</code>
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Interval_arithmetic">Wikipedia - Interval
   * arithmetic</a>
   * <li><a href="https://en.wikipedia.org/wiki/Interval_(mathematics)">Wikipedia - Interval
   * (mathematics)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; IntervalMemberQ(Interval({4,10}), Interval({2*Pi, 3*Pi}))
   * True
   *
   * &gt;&gt; IntervalMemberQ(Interval({4,10}), Interval({2*Pi, 4*Pi}))
   * False
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Interval.md">Interval</a>, <a href="IntervalIntersection.md">IntervalIntersection</a>,
   * <a href="IntervalUnion.md">IntervalUnion</a>
   */
  private static final class IntervalMemberQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isIntervalData()) {
        return evaluateIntervalData(ast, engine);
      }
      if (ast.arg1().isInterval()) {
        IAST interval1 = IntervalSym.normalize((IAST) ast.arg1());
        if (ast.arg2().isInterval()) {
          if (interval1.isPresent()) {
            IAST interval2 = IntervalSym.normalize((IAST) ast.arg2());
            if (interval2.isPresent()) {
              IASTAppendable copyInterval2 = interval2.copyAppendable();

              for (int i = 1; i < interval1.size(); i++) {
                IAST list1 = (IAST) interval1.get(i);
                IExpr min1 = list1.arg1();
                IExpr max1 = list1.arg2();
                boolean included = false;
                for (int j = 1; j < interval2.size(); j++) {
                  IAST list2 = (IAST) interval2.get(j);
                  IExpr min2 = list2.arg1();
                  IExpr max2 = list2.arg2();
                  if (min1.lessEqual(min2).isTrue() && //
                      max1.greaterEqual(max2).isTrue()) {
                    copyInterval2.remove(j);
                    if (copyInterval2.size() <= 1) {
                      return S.True;
                    }
                    included = true;
                    break;
                  }
                }
                if (!included) {
                  return S.False;
                }
              }
              if (copyInterval2.size() <= 1) {
                return S.True;
              }
            }
          }
        } else {
          IExpr arg2 = ast.arg2();
          for (int i = 1; i < interval1.size(); i++) {
            IAST list1 = (IAST) interval1.get(i);
            IExpr min1 = list1.arg1();
            IExpr max1 = list1.arg2();
            if (min1.lessEqual(arg2).isTrue() && //
                max1.greaterEqual(arg2).isTrue()) {
              // if (S.LessEqual.ofQ(engine, min1, arg2) && //
              // S.GreaterEqual.ofQ(engine, max1, arg2)) {
              return S.True;
            }
          }
        }
      }
      return S.False;
    }

    private static IExpr evaluateIntervalData(final IAST ast, EvalEngine engine) {
      IAST interval1 = IntervalDataSym.normalize((IAST) ast.arg1());
      if (ast.arg2().isIntervalData()) {
        if (interval1.isPresent()) {
          IAST interval2 = IntervalDataSym.normalize((IAST) ast.arg2());
          if (interval2.isPresent()) {
            IASTAppendable copyInterval2 = interval2.copyAppendable();

            for (int i = 1; i < interval1.size(); i++) {
              IAST list1 = (IAST) interval1.get(i);
              IExpr min1 = list1.arg1();
              IBuiltInSymbol left = (IBuiltInSymbol) list1.arg2();
              IBuiltInSymbol right = (IBuiltInSymbol) list1.arg3();
              IExpr max1 = list1.arg4();
              boolean included = false;
              for (int j = 1; j < interval2.size(); j++) {
                IAST list2 = (IAST) interval2.get(j);
                IExpr min2 = list2.arg1();
                IExpr max2 = list2.arg4();

                if (left.ofQ(engine, min1, min2) && //
                    right.ofQ(engine, max2, max1)) {
                  copyInterval2.remove(j);
                  if (copyInterval2.size() <= 1) {
                    return S.True;
                  }
                  included = true;
                  break;
                }
              }
              if (!included) {
                return S.False;
              }
            }
            if (copyInterval2.size() <= 1) {
              return S.True;
            }
          }
        }
      } else {
        IExpr scalar = ast.arg2();
        for (int i = 1; i < interval1.size(); i++) {
          IAST list1 = (IAST) interval1.get(i);
          IExpr min1 = list1.arg1();
          IBuiltInSymbol left = (IBuiltInSymbol) list1.arg2();
          IBuiltInSymbol right = (IBuiltInSymbol) list1.arg3();
          IExpr max1 = list1.arg4();
          if (left.ofQ(engine, min1, scalar) && //
              right.ofQ(engine, scalar, max1)) {
            return S.True;
          }
        }
      }

      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>IntervalIntersection(interval_1, interval_2, ...)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * compute the intersection of the intervals <code>interval_1, interval_2, ...</code>
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Interval_arithmetic">Wikipedia - Interval
   * arithmetic</a>
   * <li><a href="https://en.wikipedia.org/wiki/Interval_(mathematics)">Wikipedia - Interval
   * (mathematics)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; IntervalIntersection(Interval({1, 2}, {3, 4}, {5, 7}, {8, 8.5}), Interval({1.5, 3.5}, {4.1, 6}, {9, 10}))
   * Interval({1.5,2},{3,3.5},{5,6})
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Interval.md">Interval</a>, <a href="IntervalUnion.md">IntervalUnion</a>
   */
  private static final class IntervalIntersection extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int indexOf = ast.indexOf(x -> x.isIntervalData());
      if (indexOf > 0) {
        return evaluateIntervalData(ast, engine);
      }
      for (int i = 1; i < ast.size(); i++) {
        if (!ast.get(i).isInterval()) {
          return F.NIL;
        }
        IAST interval = (IAST) ast.get(i);
        for (int j = 1; j < interval.size(); j++) {
          if (!interval.get(j).isList2()) {
            return F.NIL;
          }
          IAST list1 = (IAST) interval.get(j);
          IExpr min1 = list1.arg1();
          IExpr max1 = list1.arg2();
          if (!min1.isRealResult() || !max1.isRealResult()) {
            return F.NIL;
          }
        }
      }
      return intervalDataIntersection(ast, engine);
    }

    private static IExpr evaluateIntervalData(final IAST ast, EvalEngine engine) {
      for (int i = 1; i < ast.size(); i++) {
        if (!ast.get(i).isIntervalData()) {
          return F.NIL;
        }
        IAST interval = (IAST) ast.get(i);
        for (int j = 1; j < interval.size(); j++) {
          if (!interval.get(j).isList4()) {
            return F.NIL;
          }
          IAST list1 = (IAST) interval.get(j);
          IExpr min1 = list1.arg1();
          IExpr max1 = list1.arg4();
          if (!min1.isRealResult() || !max1.isRealResult()) {
            return F.NIL;
          }
        }
      }
      IAST result = (IAST) ast.arg1();
      result = IntervalDataSym.normalize(result, engine);
      if (result.isInvalid()) {
        return F.NIL;
      }
      if (result.isNIL()) {
        result = (IAST) ast.arg1();
      }
      for (int i = 2; i < ast.size(); i++) {
        IAST interval = (IAST) ast.get(i);
        IAST normalizedArg = IntervalDataSym.normalize(interval, engine);
        if (normalizedArg.isInvalid()) {
          return F.NIL;
        }
        if (normalizedArg.isNIL()) {
          normalizedArg = interval;
        }
        result = IntervalDataSym.intersectionIntervalData(result, normalizedArg, engine);
        if (result.size() == 1) {
          return result;
        }
      }
      IAST normalized = IntervalDataSym.normalize(result, engine);
      if (normalized.isInvalid()) {
        return F.NIL;
      }
      return normalized.orElse(result);
    }

    private static IAST intersectionInterval(final IAST interval1, final IAST interval2,
        EvalEngine engine) {
      IASTAppendable result = F.ast(S.Interval, 3);
      for (int i = 1; i < interval1.size(); i++) {
        IAST list1 = (IAST) interval1.get(i);
        IExpr min1 = list1.arg1();
        IExpr max1 = list1.arg2();
        for (int j = 1; j < interval2.size(); j++) {
          IAST list2 = (IAST) interval2.get(j);
          IExpr min2 = list2.arg1();
          IExpr max2 = list2.arg2();
          if (S.Less.ofQ(engine, max1, min2) || S.Less.ofQ(engine, max2, min1)) {
            continue;
          }

          if (min1.lessEqual(min2).isTrue()) {
            // if (S.LessEqual.ofQ(engine, min1, min2)) {
            min1 = min2;
          }
          if (max1.greaterEqual(max2).isTrue()) {
            // if (S.GreaterEqual.ofQ(engine, max1, max2)) {
            max1 = max2;
          }
          result.append(F.list(min1, max1));
        }
      }
      return result;
    }



    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>IntervalUnion(interval_1, interval_2, ...)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * compute the union of the intervals <code>interval_1, interval_2, ...</code>
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Interval_arithmetic">Wikipedia - Interval
   * arithmetic</a>
   * <li><a href="https://en.wikipedia.org/wiki/Interval_(mathematics)">Wikipedia - Interval
   * (mathematics)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; IntervalUnion(Interval({1, 2}, {3, 4}, {5, 7}, {8, 8.5}), Interval({1.5, 3.5}, {4.1, 6}, {9, 10}))
   * Interval({1,4},{4.1,7},{8,8.5},{9,10})
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Interval.md">Interval</a>, <a href="IntervalIntersection.md">IntervalIntersection</a>
   */
  private static final class IntervalUnion extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int indexOf = ast.indexOf(x -> x.isIntervalData());
      if (indexOf > 0) {
        return evaluateIntervalData(ast, engine);
      }
      int calculatedResultSize = 2;
      for (int i = 1; i < ast.size(); i++) {
        if (!ast.get(i).isInterval()) {
          return F.NIL;
        }
        IAST interval = (IAST) ast.get(i);
        calculatedResultSize += interval.argSize();
        for (int j = 1; j < interval.size(); j++) {
          if (!interval.get(j).isList2()) {
            return F.NIL;
          }
          IAST list1 = (IAST) interval.get(j);
          IExpr min1 = list1.arg1();
          IExpr max1 = list1.arg2();
          if (!min1.isRealResult() || !max1.isRealResult()) {
            return F.NIL;
          }
        }
      }
      IASTAppendable result = F.ast(S.Interval, calculatedResultSize);
      for (int i = 1; i < ast.size(); i++) {
        IAST interval = (IAST) ast.get(i);
        for (int j = 1; j < interval.size(); j++) {
          result.append(interval.get(j));
        }
      }
      IAST normalized = IntervalSym.normalize(result, engine);
      return normalized.orElse(result);
    }

    private static IExpr evaluateIntervalData(final IAST ast, EvalEngine engine) {
      int calculatedResultSize = 2;
      for (int i = 1; i < ast.size(); i++) {
        IAST interval;
        if (!ast.get(i).isIntervalData()) {
          return F.NIL;
        } else {
          interval = (IAST) ast.get(i);
        }
        calculatedResultSize += interval.argSize();
        for (int j = 1; j < interval.size(); j++) {
          if (!interval.get(j).isList4()) {
            return F.NIL;
          }
          IAST list1 = (IAST) interval.get(j);
          IExpr min1 = list1.arg1();
          IExpr max1 = list1.arg4();
          if (!min1.isRealResult() || !max1.isRealResult()) {
            return F.NIL;
          }
        }
      }
      IASTAppendable result = F.ast(S.IntervalData, calculatedResultSize);
      for (int i = 1; i < ast.size(); i++) {
        IAST interval = (IAST) ast.get(i);
        for (int j = 1; j < interval.size(); j++) {
          result.append(interval.get(j));
        }
      }
      IAST normalized = IntervalDataSym.normalize(result, engine);
      if (normalized.isInvalid()) {
        return F.NIL;
      }
      return normalized.orElse(result);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  private static IExpr intervalDataIntersection(final IAST ast, EvalEngine engine) {
    IAST result = (IAST) ast.arg1();
    result = IntervalSym.normalize(result, engine).orElse(result);
    for (int i = 2; i < ast.size(); i++) {
      IAST interval = (IAST) ast.get(i);
      IAST normalizedArg = IntervalSym.normalize(interval, engine).orElse(interval);
      result = IntervalIntersection.intersectionInterval(result, normalizedArg, engine);
      if (result.size() == 1) {
        return result;
      }
    }
    IAST normalized = IntervalSym.normalize(result, engine);
    return normalized.orElse(result);
  }

  public static void initialize() {
    Initializer.init();
  }

  private IntervalFunctions() {}
}
