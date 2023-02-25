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
import org.matheclipse.core.reflection.system.rules.ToIntervalDataRules;

public class IntervalFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Interval.setEvaluator(new Interval());
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

  private static final class IntervalData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isEvalFlagOff(IAST.BUILT_IN_EVALED)) {
        IAST result = IntervalDataSym.normalize(ast, engine);
        if (result.isPresent()) {
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class ToIntervalData extends AbstractFunctionEvaluator
      implements ToIntervalDataRules {

    @Override
    public IAST getRuleAST() {
      return RULES;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isOr()) {
        IAST orAST = (IAST) ast.arg1();
        return engine.evaluate(orAST.mapThread(x -> F.ToIntervalData(x, ast.arg2())));
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
                  if (S.LessEqual.ofQ(engine, min1, min2) && //
                      S.GreaterEqual.ofQ(engine, max1, max2)) {
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
            if (S.LessEqual.ofQ(engine, min1, arg2) && //
                S.GreaterEqual.ofQ(engine, max1, arg2)) {
              return S.True;
            }
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
      IAST result = (IAST) ast.arg1();
      result = IntervalSym.normalize(result, engine).orElse(result);
      for (int i = 2; i < ast.size(); i++) {
        IAST interval = (IAST) ast.get(i);
        IAST normalizedArg = IntervalSym.normalize(interval, engine).orElse(interval);
        result = intersectionInterval(result, normalizedArg, engine);
        if (result.size() == 1) {
          return result;
        }
      }
      IAST normalized = IntervalSym.normalize(result, engine);
      return normalized.orElse(result);
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
      result = IntervalDataSym.normalize(result, engine).orElse(result);
      for (int i = 2; i < ast.size(); i++) {
        IAST interval = (IAST) ast.get(i);
        IAST normalizedArg = IntervalDataSym.normalize(interval, engine).orElse(interval);
        result = intersectionIntervalData(result, normalizedArg, engine);
        if (result.size() == 1) {
          return result;
        }
      }
      IAST normalized = IntervalDataSym.normalize(result, engine);
      return normalized.orElse(result);
    }

    private IAST intersectionInterval(final IAST interval1, final IAST interval2,
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

          if (S.LessEqual.ofQ(engine, min1, min2)) {
            min1 = min2;
          }
          if (S.GreaterEqual.ofQ(engine, max1, max2)) {
            max1 = max2;
          }
          result.append(F.list(min1, max1));
        }
      }
      return result;
    }

    private static IAST intersectionIntervalData(final IAST interval1, final IAST interval2,
        EvalEngine engine) {
      IASTAppendable result = F.ast(S.IntervalData, 3);

      for (int i = 1; i < interval1.size(); i++) {
        IAST list1 = (IAST) interval1.get(i);
        IExpr min1 = list1.arg1();
        IBuiltInSymbol left = (IBuiltInSymbol) list1.arg2();
        IBuiltInSymbol right = (IBuiltInSymbol) list1.arg3();
        IExpr max1 = list1.arg4();
        for (int j = 1; j < interval2.size(); j++) {
          IAST list2 = (IAST) interval2.get(j);
          IExpr min2 = list2.arg1();
          IBuiltInSymbol left2 = (IBuiltInSymbol) list2.arg2();
          IBuiltInSymbol right2 = (IBuiltInSymbol) list2.arg3();
          IExpr max2 = list2.arg4();
          if (S.Less.ofQ(engine, max1, min2) || S.Less.ofQ(engine, max2, min1)) {
            continue;
          }

          if (S.LessEqual.ofQ(engine, min1, min2)) {
            min1 = min2;
            left = left2;
          }
          if (S.GreaterEqual.ofQ(engine, max1, max2)) {
            max1 = max2;
            right = right2;
          }
          result.append(F.List(min1, left, right, max1));
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
      return normalized.orElse(result);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private IntervalFunctions() {}
}
