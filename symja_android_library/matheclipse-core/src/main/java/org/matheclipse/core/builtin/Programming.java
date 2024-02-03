package org.matheclipse.core.builtin;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.stat.descriptive.DescriptiveStatistics;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.BreakException;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFastFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.visit.ModuleReplaceAll;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

public final class Programming {
  private static final Logger LOGGER2 = LogManager.getLogger();

  private static final AbstractFunctionEvaluator NestWhileListEvaluator = new NestWhileList();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Abort.setEvaluator(new Abort());
      S.AbsoluteTiming.setEvaluator(new AbsoluteTiming());
      S.Break.setEvaluator(new Break());
      S.Block.setEvaluator(new Block());
      S.Catch.setEvaluator(new Catch());
      S.Check.setEvaluator(new Check());
      S.CheckAbort.setEvaluator(new CheckAbort());
      S.CompoundExpression.setEvaluator(new CompoundExpression());
      S.Condition.setEvaluator(new Condition());
      S.Continue.setEvaluator(new Continue());
      S.Defer.setEvaluator(new Defer());
      S.Do.setEvaluator(new Do());
      S.FixedPoint.setEvaluator(new FixedPoint());
      S.FixedPointList.setEvaluator(new FixedPointList());
      S.For.setEvaluator(new For());
      S.If.setEvaluator(new If());
      S.Interrupt.setEvaluator(new Interrupt());
      S.List.setEvaluator(new ListFunction());
      S.Module.setEvaluator(new Module());
      S.Nest.setEvaluator(new Nest());
      S.NestList.setEvaluator(new NestList());
      S.NestWhile.setEvaluator(new NestWhile());
      S.NestWhileList.setEvaluator(NestWhileListEvaluator);
      S.Part.setEvaluator(new Part());
      S.Pause.setEvaluator(new Pause());
      S.Quiet.setEvaluator(new Quiet());
      S.Reap.setEvaluator(new Reap());
      S.RepeatedTiming.setEvaluator(new RepeatedTiming());
      S.Return.setEvaluator(new Return());
      S.Sow.setEvaluator(new Sow());
      S.Stack.setEvaluator(new Stack());
      S.StackBegin.setEvaluator(new StackBegin());
      S.Switch.setEvaluator(new Switch());
      S.TimeConstrained.setEvaluator(new TimeConstrained());
      S.TimeRemaining.setEvaluator(new TimeRemaining());
      S.Timing.setEvaluator(new Timing());
      S.Throw.setEvaluator(new Throw());
      S.Unevaluated.setEvaluator(new Unevaluated());
      S.Which.setEvaluator(new Which());
      S.While.setEvaluator(new While());
      S.With.setEvaluator(new With());

      if (!Config.FUZZY_PARSER) {
        S.On.setEvaluator(new On());
        S.Off.setEvaluator(new Off());
        S.MaxMemoryUsed.setEvaluator(new MaxMemoryUsed());
        S.MemoryAvailable.setEvaluator(new MemoryAvailable());
        S.MemoryInUse.setEvaluator(new MemoryInUse());
        S.Trace.setEvaluator(new Trace());
        S.TraceForm.setEvaluator(new TraceForm());
      }
    }
  }

  /**
   *
   *
   * <pre>
   * Abort()
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * aborts an evaluation completely and returns <code>$Aborted</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Print("a"); Abort(); Print("b")
   * $Aborted
   * </pre>
   */
  private static final class Abort extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        throw AbortException.ABORTED;
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class AbsoluteTiming extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        final long begin = System.currentTimeMillis();
        final IExpr result = engine.evaluate(ast.arg1());
        double value = (System.currentTimeMillis() - begin) / 1000.0;
        return F.list(F.num(value), F.HoldForm(result));
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
    }
  }

  /**
   *
   *
   * <pre>
   * Break()
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * exits a <code>For</code>, <code>While</code>, or <code>Do</code> loop.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; n = 0
   * &gt;&gt; While(True, If(n&gt;10, Break()); n=n+1)
   * &gt;&gt; n
   * 11
   * </pre>
   */
  private static final class Break extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        throw BreakException.CONST;
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Block({list_of_local_variables}, expr )
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>expr</code> for the <code>list_of_local_variables</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; $blck=Block({$i=10}, $i=$i+1; Return($i))
   * 11
   * </pre>
   */
  private static final class Block extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IAST blockVariablesList = Validate.checkLocalVariableList(ast, 1, engine);
      if (blockVariablesList.isPresent()) {
        return engine.evalBlock(ast.arg2(), blockVariablesList);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class Catch extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int argSize = ast.argSize();
      if (argSize >= 1 && argSize <= 3) {
        try {
          return engine.evaluate(ast.arg1());
        } catch (final ThrowException e) {
          final int size = ast.size();
          switch (size) {
            case 2:
              return e.getValue();
            case 3: {
              final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
              IExpr tag = engine.evaluate(e.getTag());
              if (matcher.test(tag)) {
                return e.getValue();
              }
              throw e;
            }
            case 4: {
              final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
              IExpr tag = engine.evaluate(e.getTag());
              if (matcher.test(tag)) {
                IExpr head = engine.evaluate(ast.arg3());
                return F.binaryAST2(head, e.getValue(), tag);
              }
              throw e;
            }
            default:
              return e.getValue();
          }
        }
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDFIRST);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Check(expr, failure)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>expr</code>, and returns the result, unless messages were generated, in which
   * case <code>failure</code> will be returned.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Check(2^(3), err)
   * 8
   * </code>
   * </pre>
   *
   * <p>
   * <code>0^(-42)</code> prints message: &quot;Power: Infinite expression 1/0^42 encountered.&quot;
   *
   * <pre>
   * <code>&gt;&gt; Check(0^(-42), failure)
   * failure
   * </code>
   * </pre>
   */
  private static final class Check extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int argSize = ast.argSize();
      if (argSize >= 2 && argSize <= 3) {
        // messageShortcut may be null
        String messageShortcut = engine.getMessageShortcut();
        try {
          engine.setMessageShortcut(null);
          IExpr arg1 = engine.evaluate(ast.arg1());
          if (engine.getMessageShortcut() != null) {
            return ast.arg2();
          }
          return arg1;
        } finally {
          engine.setMessageShortcut(messageShortcut);
        }
      }
      return engine.checkBuiltinArgsSize(ast, this);
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
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>CheckAbort(expr, failure-expr)
   * </code>
   * </pre>
   *
   * <p>
   * evaluates <code>expr</code>, and returns the result, unless <code>Abort</code> was called
   * during the evaluation, in which case <code>failure-expr</code> will be returned.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; CheckAbort(Abort(); -1, 41) + 1
   * 42
   *
   * &gt;&gt; CheckAbort(abc; -1, 41) + 1
   * 0
   * </code>
   * </pre>
   */
  private static final class CheckAbort extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        try {
          return engine.evaluate(ast.arg1());
        } catch (AbortException aex) {
          return ast.arg2();
        }
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }
  /**
   *
   *
   * <pre>
   * CompoundExpression(expr1, expr2, ...)
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * expr1; expr2; ...
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates its arguments in turn, returning the last result.
   *
   * </blockquote>
   */
  private static final class CompoundExpression extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() > 1) {
        IExpr[] result = {S.Null};
        ast.forEach(x -> result[0] = engine.evaluate(x));
        return result[0];
      }
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Condition(pattern, expr)
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * pattern /; expr
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * places an additional constraint on <code>pattern</code> that only allows it to match if
   * <code>expr</code> evaluates to <code>True</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * The controlling expression of a <code>Condition</code> can use variables from the pattern:
   *
   * <pre>
   * &gt; f(3) /. f(x_) /; x&gt;0 -&gt; t
   * t
   *
   * &gt;&gt; f(-3) /. f(x_) /; x&gt;0 -&gt; t
   * f(-3)
   * </pre>
   *
   * <p>
   * <code>Condition</code> can be used in an assignment:
   *
   * <pre>
   * &gt;&gt; f(x_) := p(x) /; x&gt;0
   * &gt;&gt; f(3)
   * p(3)
   *
   * &gt;&gt; f(-3)
   * f(-3)
   * </pre>
   */
  private static final class Condition extends AbstractCoreFunctionEvaluator {

    @Override
    public final IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (engine.isEvalRHSMode()) {
        if (engine.evalTrue(ast.arg2())) {
          return ast.arg1();
        }
        throw ConditionException.CONDITION_NIL;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Continue()
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * continues with the next iteration in a <code>For</code>, <code>While</code>, or <code>Do
   * </code> loop.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; For(i=1, i&lt;=8, i=i+1, If(Mod(i,2) == 0, Continue()); Print(i))
   *  | 1
   *  | 3
   *  | 5
   *  | 7
   * </pre>
   */
  private static final class Continue extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        throw ContinueException.CONST;
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>Defer(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * <code>Defer</code> doesn't evaluate <code>expr</code> and didn't appear in the output
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Defer(3*2)
   * 3*2
   * </code>
   * </pre>
   */
  private static class Defer extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // if (!ToggleFeature.DEFER) {
      // return F.NIL;
      // }
      // IExpr arg1=ast.arg1();
      // return arg1;
      // if (arg1.isAST()){
      // IAST copy=(IAST)arg1.copy();
      // copy.addEvalFlags(IAST.DEFER_AST);
      // return copy;
      // }
      if (ast.isAST1()) {
        return F.NIL;
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Do(expr, {max})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>expr</code> <code>max</code> times.
   *
   * </blockquote>
   *
   * <pre>
   * Do(expr, {i, max})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>expr</code> <code>max</code> times, substituting <code>i</code> in <code>
   * expr</code> with values from <code>1</code> to <code>max</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Do(expr, {i, min, max})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * starts with <code>i = max</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Do(expr, {i, min, max, step})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * uses a step size of <code>step</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Do(expr, {i, {i1, i2, ...}})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * uses values <code>i1, i2, ... for i</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Do(expr, {i, imin, imax}, {j, jmin, jmax}, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates expr for each j from jmin to jmax, for each i from imin to imax, etc.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Do(Print(i), {i, 2, 4})
   *  | 2
   *  | 3
   *  | 4
   *
   * &gt;&gt; Do(Print({i, j}), {i,1,2}, {j,3,5})
   *  | {1, 3}
   *  | {1, 4}
   *  | {1, 5}
   *  | {2, 3}
   *  | {2, 4}
   *  | {2, 5}
   * </pre>
   *
   * <p>
   * You can use 'Break()' and 'Continue()' inside 'Do':
   *
   * <pre>
   * &gt;&gt; Do(If(i &gt; 10, Break(), If(Mod(i, 2) == 0, Continue()); Print(i)), {i, 5, 20})
   *  | 5
   *  | 7
   *  | 9
   *
   * &gt;&gt; Do(Print("hi"),{1+1})
   *  | hi
   *  | hi
   * </pre>
   */
  private static final class Do extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        final java.util.List<IIterator<IExpr>> iterList = new ArrayList<IIterator<IExpr>>();
        ast.forEach(2, ast.size(), (x, i) -> iterList.add(Iterator.create((IAST) x, i, engine)));
        final DoIterator generator = new DoIterator(iterList, engine);
        return generator.doIt(ast.arg1());
      } catch (final NoEvalException | ClassCastException e) {
        // ClassCastException: the iterators are generated only from IASTs
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  public static class DoIterator {

    final java.util.List<? extends IIterator<IExpr>> fIterList;
    final EvalEngine fEngine;
    int fIndex;

    public DoIterator(final java.util.List<? extends IIterator<IExpr>> iterList,
        EvalEngine engine) {
      fIterList = iterList;
      fEngine = engine;
      fIndex = 0;
    }

    public IExpr doIt(IExpr input) {
      if (fIndex < fIterList.size()) {
        final IIterator<IExpr> iter = fIterList.get(fIndex);
        if (iter.setUp()) {
          try {
            final int iterationLimit = fEngine.getIterationLimit();
            int iterationCounter = 1;
            fIndex++;
            while (iter.hasNext()) {
              try {
                iter.next();
                doIt(input);
                if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
                  IterationLimitExceeded.throwIt(iterationCounter, input);
                }
              } catch (final ReturnException e) {
                return e.getValue();
              } catch (final BreakException e) {
                return S.Null;
              } catch (final ContinueException e) {
              }
            }
          } finally {
            --fIndex;
            iter.tearDown();
          }
        }
        return S.Null;
      }
      fEngine.evaluateNIL(input);

      return F.NIL;
    }
  }
  /**
   *
   *
   * <pre>
   * FixedPoint(f, expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * starting with <code>expr</code>, iteratively applies <code>f</code> until the result no longer
   * changes.
   *
   * </blockquote>
   *
   * <pre>
   * FixedPoint(f, expr, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * performs at most <code>n</code> iterations.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; FixedPoint(Cos, 1.0)
   * 0.7390851332151607
   *
   * &gt;&gt; FixedPoint(#+1 &amp;, 1, 20)
   * 21
   *
   * &gt;&gt; FixedPoint(f, x, 0)
   * x
   * </pre>
   *
   * <p>
   * Non-negative integer expected.
   *
   * <pre>
   * &gt;&gt; FixedPoint(f, x, -1)
   * FixedPoint(f, x, -1)
   *
   * &gt;&gt; FixedPoint(Cos, 1.0, Infinity)
   * 0.739085
   * </pre>
   */
  private static final class FixedPoint extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      boolean numericMode = engine.isNumericMode();
      try {
        IExpr f = ast.arg1();
        if (f.isAtom() && !f.isSymbol()) {
          // Nonatomic expression expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
        }
        int maxIterations = Integer.MAX_VALUE;
        if (ast.isAST3()) {
          IExpr arg3 = ast.arg3();
          if (arg3.isInfinity()) {
            maxIterations = Integer.MAX_VALUE;
          } else if (arg3.isNegativeInfinity()) {
            maxIterations = Integer.MIN_VALUE;
          } else {
            maxIterations = Validate.checkNonNegativeIntType(ast, 3);
          }
        }
        if (maxIterations < 0) {
          // Non-negative machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.ZZ(3)),
              EvalEngine.get());
        }
        if (maxIterations == 0) {
          return ast.arg2();
        } else {
          IExpr current = ast.arg2();
          final int iterationLimit = engine.getIterationLimit();
          int iterationCounter = 1;
          IExpr last;
          do {
            last = current;
            current = engine.evaluate(F.Apply(f, F.list(current)));
            if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
              IterationLimitExceeded.throwIt(iterationCounter, ast);
            }
          } while ((!current.isSame(last)) && (--maxIterations > 0));
          return current;
        }
      } finally {
        engine.setNumericMode(numericMode);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    // @Override
    // public void setUp(final ISymbol newSymbol) {
    // newSymbol.setAttributes(ISymbol.HOLDALL);
    // }

  }

  /**
   *
   *
   * <pre>
   * FixedPointList(f, expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * starting with <code>expr</code>, iteratively applies <code>f</code> until the result no longer
   * changes, and returns a list of all intermediate results.
   *
   * </blockquote>
   *
   * <pre>
   * FixedPointList(f, expr, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * performs at most <code>n</code> iterations.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; FixedPointList(Cos, 1.0, 4)
   * {1.0,0.5403023058681398,0.8575532158463934,0.6542897904977791,0.7934803587425656}
   * </pre>
   *
   * <p>
   * Observe the convergence of Newton's method for approximating square roots:<br>
   *
   * <pre>
   * &gt;&gt; newton(n_) := FixedPointList(.5(# + n/#) &amp;, 1.);
   * &gt;&gt; newton(9)
   * {1.0,5.0,3.4,3.023529411764706,3.00009155413138,3.000000001396984,3.0,3.0}
   * </pre>
   *
   * <p>
   * Get the &ldquo;hailstone&rdquo; sequence of a number:<br>
   *
   * <pre>
   * &gt;&gt; collatz(1) := 1;
   * &gt;&gt; collatz(x_ ? EvenQ) := x / 2;
   * &gt;&gt; collatz(x_) := 3*x + 1;
   * &gt;&gt; FixedPointList(collatz, 14)
   * {14,7,22,11,34,17,52,26,13,40,20,10,5,16,8,4,2,1,1}
   * </pre>
   *
   * <pre>
   * ```
   * &gt;&gt; FixedPointList(f, x, 0)
   * {x}
   * </pre>
   *
   * <p>
   * Non-negative integer expected.
   *
   * <pre>
   * &gt;&gt; FixedPointList(f, x, -1)
   * FixedPointList(f,x,-1)
   *
   * &gt;&gt; Last(FixedPointList(Cos, 1.0, Infinity))
   * 0.7390851332151607
   * </pre>
   */
  private static final class FixedPointList extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      boolean numericMode = engine.isNumericMode();
      try {
        IExpr f = ast.arg1();
        if (f.isNumber()) {
          // Nonatomic expression expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "normal", F.list(F.C1, ast), engine);
        }
        IExpr current = ast.arg2();
        int iterations = Integer.MAX_VALUE;
        if (ast.isAST3()) {
          if (ast.arg3().isInfinity()) {
            iterations = Integer.MAX_VALUE;
          } else if (ast.arg3().isNegativeInfinity()) {
            iterations = Integer.MIN_VALUE;
          } else {
            iterations = Validate.checkNonNegativeIntType(ast, 3);
          }
        }
        if (iterations < 0) {
          // Non-negative machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.ZZ(3)),
              EvalEngine.get());
        }
        if (iterations == 0) {
          return F.list(ast.arg2());
        }
        IASTAppendable list = F.ListAlloc(iterations < 100 ? iterations : 32);
        list.append(current);

        final int iterationLimit = engine.getIterationLimit();
        int iterationCounter = 1;
        IExpr last;
        do {
          last = current;
          current = engine.evaluate(F.Apply(f, F.list(current)));
          list.append(current);

          if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
            IterationLimitExceeded.throwIt(iterationCounter, ast);
          }
        } while ((!current.isSame(last)) && (--iterations > 0));
        return list;
      } finally {
        engine.setNumericMode(numericMode);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   *
   *
   * <pre>
   * For(start, test, incr, body)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>start</code>, and then iteratively <code>body</code> and <code>incr</code> as
   * long as test evaluates to <code>True</code>.
   *
   * </blockquote>
   *
   * <pre>
   * For(start, test, incr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates only <code>incr</code> and no <code>body</code>.
   *
   * </blockquote>
   *
   * <pre>
   * For(start, test)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * runs the loop without any body.<br>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * Compute the factorial of 10 using 'For':
   *
   * <pre>
   * &gt;&gt; n := 1
   * &gt;&gt; For(i=1, i&lt;=10, i=i+1, n = n * i)
   * &gt;&gt; n
   * 3628800
   *
   * &gt;&gt; n == 10!
   * True
   *
   * &gt;&gt; n := 1
   * &gt;&gt; For(i=1, i&lt;=10, i=i+1, If(i &gt; 5, Return(i)); n = n * i)
   * 6
   *
   * &gt;&gt; n
   * 120
   * </pre>
   */
  private static final class For extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // use EvalEngine's iterationLimit only for evaluation control
      final int iterationLimit = engine.getIterationLimit();
      int iterationCounter = 1;

      // For(start, test, incr, body)
      engine.evaluateNIL(ast.arg1()); // start
      IExpr test = ast.arg2();
      IExpr incr = ast.arg3();
      IExpr body = S.Null;
      if (ast.size() == 5) {
        body = ast.arg4();
      }
      while (true) {
        try {
          if (!engine.evalTrue(test)) {
            return S.Null;
          }
          if (ast.size() == 5) {
            engine.evaluateNIL(body);
          }
        } catch (final BreakException e) {
          return S.Null;
        } catch (final ContinueException e) {
          if (iterationLimit > 0 && iterationLimit <= ++iterationCounter) {
            IterationLimitExceeded.throwIt(iterationCounter, ast);
          }
        } catch (final ReturnException e) {
          return e.getValue();
        }
        if (iterationLimit > 0 && iterationLimit <= ++iterationCounter) {
          IterationLimitExceeded.throwIt(iterationCounter, ast);
        }
        engine.evaluateNIL(incr);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * If(cond, pos, neg)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>pos</code> if <code>cond</code> evaluates to <code>True</code>, and <code>neg
   * </code> if it evaluates to <code>False</code>.
   *
   * </blockquote>
   *
   * <pre>
   * If(cond, pos, neg, other)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>other</code> if <code>cond</code> evaluates to neither <code>True</code> nor
   * <code>False</code>.
   *
   * </blockquote>
   *
   * <pre>
   * If(cond, pos)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>Null</code> if <code>cond</code> evaluates to <code>False</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; If(1&lt;2, a, b)
   * a
   * </pre>
   *
   * <p>
   * If the second branch is not specified, <code>Null</code> is taken:
   *
   * <blockquote>
   *
   * <blockquote>
   *
   * <p>
   * If(1&lt;2, a) a
   *
   * <p>
   * If(False, a) //FullForm Null
   *
   * </blockquote>
   *
   * </blockquote>
   *
   * <p>
   * You might use comments (inside <code>(*</code> and <code>*)</code>) to make the branches of
   * <code>If</code> more readable:
   *
   * <pre>
   * &gt;&gt; If(a, (*then*) b, (*else*) c);
   * </pre>
   */
  private static final class If extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr temp = engine.evaluate(ast.arg1());

      if (temp.isFalse()) {
        if (ast.size() >= 4) {
          return ast.arg3();
        }

        return S.Null;
      }

      if (temp.equals(S.True)) {
        return ast.arg2();
      }

      if (ast.size() == 5) {
        return ast.arg4();
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Interrupt( )
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Interrupt an evaluation and returns <code>$Aborted</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>Print(test1)</code> prints string: &quot;test1&quot;
   *
   * <pre>
   * <code>&gt;&gt; Print(test1); Interrupt(); Print(test2)
   * $Aborted
   * </code>
   * </pre>
   */
  private static final class Interrupt extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        throw new AbortException();
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>List(e1, e2, ..., ei)
   * </code>
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * <code>{e1, e2, ..., ei}
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents a list containing the elements <code>e1...ei</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * 'List' is the head of lists:
   *
   * <pre>
   * <code>&gt;&gt; Head({1, 2, 3})
   * List
   * </code>
   * </pre>
   *
   * <p>
   * Lists can be nested:
   *
   * <pre>
   * <code>&gt;&gt; {{a, b, {c, d}}}
   * {{a, b, {c, d}}}
   * </code>
   * </pre>
   */
  private static final class ListFunction extends AbstractFunctionEvaluator
      implements ISetEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      if (leftHandSide.isList()) {
        // thread over lists
        IExpr temp =
            engine.threadASTListArgs(F.Set(leftHandSide, rightHandSide), S.Thread, "tdlen");
        if (temp.isPresent()) {
          return engine.evaluate(temp);
        }
      }
      return F.NIL;
    }
  }

  private static class MaxMemoryUsed extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        long freeMemory = Runtime.getRuntime().totalMemory();
        return F.ZZ(freeMemory);
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }
  }

  private static class MemoryAvailable extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        long freeMemory = Runtime.getRuntime().freeMemory();
        return F.ZZ(freeMemory);
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  private static class MemoryInUse extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        long freeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return F.ZZ(freeMemory);
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   *
   *
   * <pre>
   * Module({list_of_local_variables}, expr )
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>expr</code> for the <code>list_of_local_variables</code> by renaming local
   * variables.
   *
   * </blockquote>
   */
  private static final class Module extends AbstractCoreFunctionEvaluator {
    /** */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IAST moduleVariablesList = Validate.checkLocalVariableList(ast, 1, engine);
      if (moduleVariablesList.isPresent()) {
        IExpr temp = moduleSubstVariables(moduleVariablesList, ast.arg2(), engine);
        if (temp.isPresent()) {
          return engine.evaluate(temp);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Nest(f, expr, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * starting with <code>expr</code>, iteratively applies <code>f</code> <code>n</code> times and
   * returns the final result.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Nest(f, x, 3)
   * f(f(f(x)))
   *
   * &gt;&gt; Nest((1+#) ^ 2 &amp;, x, 2)
   * (1+(1+x)^2)^2
   * </pre>
   */
  private static final class Nest extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg3 = engine.evaluate(ast.arg3());
      if (arg3.isInteger()) {
        int n = arg3.toIntDefault();
        if (n < 0) {
          // Positive integer (less equal 2147483647) expected at position `2` in `1`.
          return Errors.printMessage(S.Nest, "intpm", F.list(ast, F.C3), engine);
        }
        int iterationLimit = engine.getIterationLimit();
        if (iterationLimit >= 0 && iterationLimit <= n) {
          IterationLimitExceeded.throwIt(n, ast);
        }
        return nest(ast.arg2(), ast.arg1(), n, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * NestList(f, expr, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * starting with <code>expr</code>, iteratively applies <code>f</code> <code>n</code> times and
   * returns a list of all intermediate results.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; NestList(f, x, 3)
   * {x,f(x),f(f(x)),f(f(f(x)))}
   *
   * &gt;&gt; NestList(2 # &amp;, 1, 8)
   * {1,2,4,8,16,32,64,128,256}
   * </pre>
   */
  private static final class NestList extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int n = ast.arg3().toIntDefault();
      if (n < 0) {
        // Positive integer (less equal 2147483647) expected at position `2` in `1`.
        return Errors.printMessage(S.Nest, "intpm", F.list(ast, F.C3), engine);
      }
      int iterationLimit = engine.getIterationLimit();
      if (iterationLimit >= 0 && iterationLimit <= n) {
        IterationLimitExceeded.throwIt(n, ast);
      }
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      return nestList(arg2, n, x -> F.unaryAST1(arg1, x), S.List, engine);
    }

    public static IAST nestList(final IExpr expr, final int n, final Function<IExpr, IExpr> fn,
        final IExpr resultHead, EvalEngine engine) {
      IASTAppendable resultList = F.ast(resultHead, n + 1);
      IExpr temp = expr;
      resultList.append(temp);
      for (int i = 0; i < n; i++) {
        temp = engine.evaluate(fn.apply(temp));
        resultList.append(temp);
      }
      return resultList;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

  }

  /**
   *
   *
   * <pre>
   * NestWhile(f, expr, test)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * applies a function <code>f</code> repeatedly on an expression <code>expr</code>, until applying
   * <code>test</code> on the result no longer yields <code>True</code>.
   *
   * </blockquote>
   *
   * <pre>
   * NestWhile(f, expr, test, m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * supplies the last <code>m</code> results to <code>test</code> (default value: <code>1</code> ).
   *
   * </blockquote>
   *
   * <pre>
   * NestWhile(f, expr, test, All)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * supplies all results gained so far to <code>test</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * Divide by 2 until the result is no longer an integer:
   *
   * <pre>
   * &gt;&gt; NestWhile(#/2&amp;, 10000, IntegerQ)
   * 625/2
   * </pre>
   */
  private static final class NestWhile extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = F.C1;
      if (ast.argSize() > 4) {
        IExpr temp = NestWhileListEvaluator.evaluate(ast.setAtClone(0, S.NestWhileList), engine);
        if (temp.isList()) {
          return temp.last();
        }
        return F.NIL;
      }
      if (ast.argSize() >= 4) {
        n = ast.arg4();
      }
      return nestWhile(ast.arg2(), ast.arg3(), x -> F.unaryAST1(ast.arg1(), x), n, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_6;
    }

    public static IExpr nestWhile(final IExpr expr, final IExpr test,
        final Function<IExpr, IExpr> fn, final IExpr n, EvalEngine engine) {
      if (n == S.All) {
        IExpr temp = expr;
        IASTAppendable testFunction = F.ast(test);
        testFunction.append(temp);
        while (engine.evalTrue(testFunction)) {
          temp = engine.evaluate(fn.apply(temp));
          testFunction.append(temp);
        }
        return temp;
      }
      int extraTimes = n.toIntDefault();
      if (extraTimes <= 0) {
        return F.NIL;
      }
      IExpr temp = expr;
      if (Config.MAX_AST_SIZE < extraTimes) {
        // Maximum AST limit `1` exceeded.
        return Errors.printMessage(S.NestWhile, "zzmaxast", F.List(extraTimes), engine);
      }
      IExpr[] args = new IExpr[extraTimes];
      args[0] = temp;
      for (int i = 1; i < extraTimes; i++) {
        temp = engine.evaluate(fn.apply(temp));
        args[i] = temp;
      }
      while (engine.evalTrue(F.ast(args, test))) {
        temp = engine.evaluate(fn.apply(temp));

        IExpr[] argsTemp = new IExpr[extraTimes];
        System.arraycopy(args, 1, argsTemp, 0, extraTimes - 1);
        argsTemp[extraTimes - 1] = temp;
        args = argsTemp;
      }
      return temp;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>NestWhileList(f, expr, test)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * applies a function <code>f</code> repeatedly on an expression <code>expr</code>, until applying
   * <code>test</code> on the result no longer yields <code>True</code>. It returns a list of all
   * intermediate results.
   *
   * </blockquote>
   *
   * <pre>
   * <code>NestWhileList(f, expr, test, m)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * supplies the last <code>m</code> results to <code>test</code> (default value: <code>1</code> ).
   * It returns a list of all intermediate results.
   *
   * </blockquote>
   *
   * <pre>
   * <code>NestWhileList(f, expr, test, All)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * supplies all results gained so far to <code>test</code>. It returns a list of all intermediate
   * results.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   */
  private static class NestWhileList extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr f = ast.arg1();
      IExpr expr = ast.arg2();
      IExpr test = ast.arg3();
      int m = 1;
      if (ast.argSize() >= 4) {
        if (ast.arg4().equals(S.All)) {
          m = -1;
        } else {
          int tmpInt = ast.arg4().toIntDefault();
          if (tmpInt < 0) {
            // Argument `2` in `1` is not of the form i, {i,j}, {i,Infinity}, or All, where i and j
            // are non-negative machine-sized integer
            return Errors.printMessage(ast.topHead(), "nwargs", F.List(ast, F.C4), engine);
          }
          m = tmpInt;
        }
      }
      int max = -1;
      if (ast.argSize() >= 5) {
        if (ast.arg5().isInfinity()) {
          max = -1;
        } else {
          int tmpInt = ast.arg5().toIntDefault();
          if (tmpInt < 0) {
            // Machine-sized integer expected at position `2` in `1`.
            return Errors.printMessage(ast.topHead(), "intm", F.List(ast, F.C5), engine);
          }
          max = tmpInt;
        }
      }
      int n = 0;
      if (ast.argSize() == 6) {
        int tmpInt = ast.get(6).toIntDefault();
        if (tmpInt == Integer.MIN_VALUE) {
          return F.NIL;
        }
        n = tmpInt;
      }
      return nestWhileList(expr, engine.evaluate(test), x -> F.unaryAST1(f, x), m, max, n, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_6;
    }

    /**
     * 
     * @param expr
     * @param test
     * @param function
     * @param m
     * @param n apply <code>function</code> n extra times. If n is negative drop last n values.
     * @param engine
     * @return
     */
    private static IAST nestWhileList(final IExpr expr, final IExpr test,
        final Function<IExpr, IExpr> function, int m, int max, int n, EvalEngine engine) {
      IExpr temp = expr;
      IExpr[] args;
      int arrSize;
      if (max != -1 && m > max) {
        m = max + 1;
      }
      if (m <= 0) {
        arrSize = 1;
      } else {
        arrSize = m;
      }
      if (Config.MAX_AST_SIZE < arrSize) {
        // Maximum AST limit `1` exceeded.
        return Errors.printMessage(S.NestWhileList, "zzmaxast", F.List(m), engine);
      }

      args = new IExpr[arrSize];
      final IASTAppendable resultList = F.ListAlloc(15);
      args[0] = temp;

      int count = 1;
      for (int i = 1; i < m; i++) {
        if (count++ > max && max != -1) {
          break;
        }
        resultList.append(temp);
        temp = engine.evaluate(function.apply(temp));
        args[i] = temp;
      }


      while (engine.evalTrue(m == 1 ? F.unaryAST1(test, args[0]) : F.ast(args, test))) {
        if (count++ > max && max != -1) {
          break;
        }
        resultList.append(temp);
        temp = engine.evaluate(function.apply(temp));

        IExpr[] argsTemp;
        if (m == (-1)) {
          arrSize++;
        }
        if (Config.MAX_AST_SIZE < arrSize) {
          // Maximum AST limit `1` exceeded.
          return Errors.printMessage(S.NestWhileList, "zzmaxast", F.List(m), engine);
        }
        if (m == (-1)) {
          argsTemp = new IExpr[arrSize];
          System.arraycopy(args, 0, argsTemp, 0, arrSize - 1);
          argsTemp[arrSize - 1] = temp;
          args = argsTemp;
        } else {
          if (m > 0) {
            argsTemp = new IExpr[m];
            System.arraycopy(args, 1, argsTemp, 0, m - 1);
            argsTemp[m - 1] = temp;
            args = argsTemp;
          }
        }

      }
      resultList.append(temp);
      if (n > 0) {
        // append n values
        for (int i = 0; i < n; i++) {
          temp = engine.evaluate(function.apply(temp));
          resultList.append(temp);
        }
      }

      if (n < 0) {
        // drop last n values
        if (resultList.argSize() < -n) {
          return F.CEmptyList;
        }
        return resultList.copyUntil(resultList.size() + n);
      }
      return resultList;
    }

  }

  /**
   *
   *
   * <pre>
   * <code>Off( )
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * switch off the interactive trace.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>On()</code> enables the trace of the evaluation steps.
   *
   * <pre>
   * <code>&gt;&gt; On()
   *   On() --&gt; Null
   *
   * &gt;&gt; D(Sin(x)+Cos(x), x)
   *
   *   NotListQ(x) --&gt; True
   *
   *   D(x,x) --&gt; 1
   *
   *   D(x,x)*(-1)*Sin(x) --&gt; (-1)*1*Sin(x)
   *
   *   (-1)*1*Sin(x) --&gt; -Sin(x)
   *
   *   D(Cos(x),x) --&gt; -Sin(x)
   *
   *   NotListQ(x) --&gt; True
   *
   *   D(x,x) --&gt; 1
   *
   *   Cos(x)*D(x,x) --&gt; 1*Cos(x)
   *
   *   1*Cos(x) --&gt; Cos(x)
   *
   *   D(Sin(x),x) --&gt; Cos(x)
   *
   *   D(Cos(x)+Sin(x),x) --&gt; -Sin(x)+Cos(x)
   *
   * Cos(x)-Sin(x)
   * </code>
   * </pre>
   *
   * <p>
   * <code>Off()</code> disables the trace of the evaluation steps.
   *
   * <pre>
   * <code>&gt;&gt; Off()
   *
   * &gt;&gt; D(Sin(x)+Cos(x), x)
   * Cos(x)-Sin(x)
   *
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="On.md">On</a>
   */
  private static final class Off extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        engine.setOnOffMode(false, null, false);
        return S.Null;
      }

      if (ast.isAST1()) {
        // `1` currently not supported in `2`.
        Errors.printMessage(S.Off, "unsupported", F.List(F.stringx("disabling messages"), S.Off),
            engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>On( )
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * switch on the interactive trace. The output is printed in the defined <code>out</code> stream.
   *
   * </blockquote>
   *
   * <pre>
   * <code>On({head1, head2,... })
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * switch on the interactive trace only for the <code>head</code>s defined in the list. The output
   * is printed in the defined <code>out</code> stream.
   *
   * </blockquote>
   *
   * <pre>
   * <code>On({head1, head2,... }, Unique)
   * </code>
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * <code>On(All, Unique)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * switch on the interactive trace only for the defined <code>head</code> s. The output is printed
   * only once for a combination of <em>unevaluated</em> input expression and <em>evaluated</em>
   * output expression.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * <code>On()</code> enables the trace of the evaluation steps.
   *
   * <pre>
   * <code>&gt;&gt; On()
   *   On() --&gt; Null
   *
   * &gt;&gt; D(Sin(x)+Cos(x), x)
   *
   *   NotListQ(x) --&gt; True
   *
   *   D(x,x) --&gt; 1
   *
   *   D(x,x)*(-1)*Sin(x) --&gt; (-1)*1*Sin(x)
   *
   *   (-1)*1*Sin(x) --&gt; -Sin(x)
   *
   *   D(Cos(x),x) --&gt; -Sin(x)
   *
   *   NotListQ(x) --&gt; True
   *
   *   D(x,x) --&gt; 1
   *
   *   Cos(x)*D(x,x) --&gt; 1*Cos(x)
   *
   *   1*Cos(x) --&gt; Cos(x)
   *
   *   D(Sin(x),x) --&gt; Cos(x)
   *
   *   D(Cos(x)+Sin(x),x) --&gt; -Sin(x)+Cos(x)
   *
   * Cos(x)-Sin(x)
   * </code>
   * </pre>
   *
   * <p>
   * <code>Off()</code> disables the trace of the evaluation steps.
   *
   * <pre>
   * <code>&gt;&gt; Off()
   *
   * &gt;&gt; D(Sin(x)+Cos(x), x)
   * Cos(x)-Sin(x)
   *
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Off.md">Off</a>
   */
  private static final class On extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        engine.setOnOffMode(true, null, false);
        return S.Null;
      }

      IExpr arg1 = ast.first();
      if (ast.isAST2() && ast.second().equals(S.Unique)) {
        IdentityHashMap<ISymbol, ISymbol> map = null;
        enableOnOffTrace(arg1, map, engine);
        engine.setOnOffMode(true, map, true);
        return S.Null;
      }
      IdentityHashMap<ISymbol, ISymbol> map = null;
      ast.forEach(x -> enableOnOffTrace(x, map, engine));
      engine.setOnOffMode(true, map, false);
      return F.NIL;
    }

    private void enableOnOffTrace(IExpr arg1, IdentityHashMap<ISymbol, ISymbol> map,
        EvalEngine engine) {

      if (!arg1.equals(S.All)) {
        IAST list = F.list(arg1);
        if (arg1.isList()) {
          list = (IAST) arg1;
        }
        // map = new IdentityHashMap<ISymbol, ISymbol>();
        if (map != null) {
          for (int i = 1; i < list.size(); i++) {
            if (list.get(i).isSymbol()) {
              map.put((ISymbol) list.get(i), S.Null);
            } else {
              map.put(list.get(i).topHead(), S.Null);
            }
          }
        }
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Part(expr, i)
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * expr[[i]]
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns part <code>i</code> of <code>expr</code>.
   *
   * </blockquote>
   *
   * <p>
   * Extract an element from a list:
   *
   * <pre>
   * &gt;&gt; A = {a, b, c, d}
   * &gt;&gt; A[[3]]
   * c
   * </pre>
   *
   * <p>
   * Negative indices count from the end:
   *
   * <pre>
   * &gt;&gt; {a, b, c}[[-2]]
   * b
   * </pre>
   *
   * <p>
   * <code>Part</code> can be applied on any expression, not necessarily lists.
   *
   * <pre>
   * &gt;&gt; (a + b + c)[[2]]
   * b
   * </pre>
   *
   * <p>
   * <code>expr[[0]]</code> gives the head of <code>expr</code>:
   *
   * <pre>
   * &gt;&gt; (a + b + c)[[0]]
   * Plus
   * </pre>
   *
   * <p>
   * Parts of nested lists:
   *
   * <pre>
   * &gt;&gt; M = {{a, b}, {c, d}}
   * &gt;&gt; M[[1, 2]]
   * b
   * </pre>
   *
   * <p>
   * You can use <code>Span</code> to specify a range of parts:
   *
   * <pre>
   * &gt;&gt; {1, 2, 3, 4}[[2;;4]]
   * {2,3,4}
   *
   * &gt;&gt; {1, 2, 3, 4}[[2;;-1]]
   * {2,3,4}
   * </pre>
   *
   * <p>
   * A list of parts extracts elements at certain indices:
   *
   * <pre>
   * &gt;&gt; {a, b, c, d}[[{1, 3, 3}]]
   * {a,c,c}
   * </pre>
   *
   * <p>
   * Get a certain column of a matrix:
   *
   * <pre>
   * &gt;&gt; B = {{a, b, c}, {d, e, f}, {g, h, i}}
   * &gt;&gt; B[[;;, 2]]
   * {b, e, h}
   * </pre>
   *
   * <p>
   * Extract a submatrix of 1st and 3rd row and the two last columns:
   *
   * <pre>
   * &gt;&gt; B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}
   * &gt;&gt; B[[{1, 3}, -2;;-1]]
   * {{2,3},{8,9}}
   * </pre>
   *
   * <p>
   * Further examples:
   *
   * <pre>
   * &gt;&gt; (a+b+c+d)[[-1;;-2]]
   * 0
   * </pre>
   *
   * <p>
   * Part specification is longer than depth of object.
   *
   * <pre>
   * &gt;&gt; x[[2]]
   * x[[2]]
   * </pre>
   *
   * <p>
   * Assignments to parts are possible:
   *
   * <pre>
   * &gt;&gt; B[[;;, 2]] = {10, 11, 12}
   * {10, 11, 12}
   *
   * &gt;&gt; B
   * {{1, 10, 3}, {4, 11, 6}, {7, 12, 9}}
   *
   * &gt;&gt; B[[;;, 3]] = 13
   * 13
   *
   * &gt;&gt; B
   * {{1, 10, 13}, {4, 11, 13}, {7, 12, 13}}
   *
   * &gt;&gt; B[[1;;-2]] = t
   * &gt;&gt; B
   * {t,t,{7,12,13}}
   *
   * &gt;&gt; F = Table(i*j*k, {i, 1, 3}, {j, 1, 3}, {k, 1, 3})
   * &gt;&gt; F[[;; All, 2 ;; 3, 2]] = t
   * &gt;&gt; F
   * {{{1,2,3},{2,t,6},{3,t,9}},{{2,4,6},{4,t,12},{6,t,18}},{{3,6,9},{6,t,18},{9,t,27}}}
   *
   * &gt;&gt; F[[;; All, 1 ;; 2, 3 ;; 3]] = k
   * &gt;&gt; F
   * {{{1,2,k},{2,t,k},{3,t,9}},{{2,4,k},{4,t,k},{6,t,18}},{{3,6,k},{6,t,k},{9,t,27}}}
   * </pre>
   *
   * <p>
   * Of course, part specifications have precedence over most arithmetic operations:
   *
   * <pre>
   * &gt;&gt; A[[1]] + B[[2]] + C[[3]] // Hold // FullForm
   * "Hold(Plus(Plus(Part(A, 1), Part(B, 2)), Part(C, 3)))"
   *
   * &gt;&gt; a = {2,3,4}; i = 1; a[[i]] = 0; a
   * {0, 3, 4}
   * </pre>
   *
   * <p>
   * Negative step
   *
   * <pre>
   * &gt;&gt; {1,2,3,4,5}[[3;;1;;-1]]
   * {3,2,1}
   *
   * &gt;&gt; {1, 2, 3, 4, 5}[[;; ;; -1]]
   * {5, 4, 3, 2, 1}
   *
   * &gt;&gt; Range(11)[[-3 ;; 2 ;; -2]]
   * {9,7,5,3}
   *
   * &gt;&gt; Range(11)[[-3 ;; -7 ;; -3]]
   * {9,6}
   *
   * &gt;&gt; Range(11)[[7 ;; -7;; -2]]
   * {7,5}
   * </pre>
   *
   * <p>
   * Cannot take positions <code>1</code> through <code>3</code> in <code>{1, 2, 3, 4}</code>.
   *
   * <pre>
   * &gt;&gt; {1, 2, 3, 4}[[1;;3;;-1]]
   * {1,2,3,4}[[1;;3;;-1]]
   * </pre>
   *
   * <p>
   * Cannot take positions <code>3</code> through <code>1</code> in <code>{1, 2, 3, 4}</code>.
   *
   * <pre>
   * &gt;&gt; {1, 2, 3, 4}[[3;;1]]
   * {1,2,3,4}[[3;;1]]
   * </pre>
   */
  private static final class Part extends AbstractFunctionEvaluator implements ISetEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      if (ast.isAST1()) {
        return ast.arg1();
      }
      if (ast.size() >= 3) {
        if (ast.isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
          return F.NIL;
        }

        IASTMutable evaledAST = F.NIL;
        IExpr arg1 = engine.evaluateNIL(ast.arg1());
        if (arg1.isPresent()) {
          evaledAST = ast.setAtCopy(1, arg1);
          if (!arg1.isASTOrAssociation()) {
            if (arg1.isSparseArray()) {
              return sparseEvaluate(evaledAST, (ISparseArray) arg1, engine).orElse(evaledAST);
            }
            if (ast.size() == 3 && ast.arg2().isZero()) {
              return arg1.head();
            }
            // Part specification `1` is longer than depth of object.
            Errors.printMessage(S.Part, "partd", F.list(evaledAST), engine);
            // return the evaluated result:
            return evaledAST;
          }
        } else {
          arg1 = ast.arg1();
          if (!arg1.isASTOrAssociation()) {
            if (arg1.isSparseArray()) {
              return sparseEvaluate(ast, (ISparseArray) arg1, engine);
            }
            if (ast.size() == 3 && ast.arg2().isZero()) {
              return arg1.head();
            }
            // Part specification `1` is longer than depth of object.
            return Errors.printMessage(S.Part, "partd", F.list(ast), engine);
          }
        }

        IAST arg1AST = (IAST) arg1;
        IExpr temp;
        int astSize = ast.size();
        for (int i = 2; i < astSize; i++) {
          temp = engine.evaluateNIL(ast.get(i));
          if (temp.isPresent()) {
            if (evaledAST.isPresent()) {
              evaledAST.set(i, temp);
            } else {
              evaledAST = ast.setAtCopy(i, temp);
              evaledAST.addEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
            }
          }
        }

        if (evaledAST.isPresent()) {
          return part(arg1AST, evaledAST, 2, engine);
        }
        return part(arg1AST, ast, 2, engine);
      }

      return F.NIL;
    }

    public IExpr sparseEvaluate(final IAST ast, ISparseArray arg1, EvalEngine engine) {

      ast.addEvalFlags(IAST.BUILT_IN_EVALED);
      if (ast.size() >= 3) {
        IASTMutable evaledAST = F.NIL;

        IExpr temp;

        int astSize = ast.size();
        for (int i = 2; i < astSize; i++) {
          temp = engine.evaluateNIL(ast.get(i));
          if (temp.isPresent()) {
            if (evaledAST.isPresent()) {
              evaledAST.set(i, temp);
            } else {
              evaledAST = ast.setAtCopy(i, temp);
              evaledAST.addEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
            }
          }
        }

        if (evaledAST.isPresent()) {
          return sparsePart(arg1, evaledAST, 2, engine);
        }
        return sparsePart(arg1, ast, 2, engine);
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      if (leftHandSide.size() > 1) {
        IAST part = (IAST) leftHandSide;
        if (part.arg1().isSymbol()) {
          ISymbol symbol = (ISymbol) part.arg1();
          IExpr temp = symbol.assignedValue();
          // RulesData rd = symbol.getRulesData();
          if (temp == null) {
            // `1` is not a variable with a value, so its value cannot be changed.
            return Errors.printMessage(builtinSymbol, "rvalue", F.list(symbol), engine);
          } else {
            if (symbol.isProtected()) {
              return Errors.printMessage(builtinSymbol, "write", F.list(symbol), EvalEngine.get());
            }
            try {
              if (rightHandSide.isList()) {
                IExpr res = Programming.assignPart(temp, part, 2, (IAST) rightHandSide, 1, engine);
                if (res.isPresent()) {
                  // symbol.putDownRule(IPatternMatcher.SET, true, symbol, res, false);
                  symbol.assignValue(res, false);
                }
                return rightHandSide;
              } else {
                IExpr res = Programming.assignPart(temp, part, 2, rightHandSide, engine);
                if (res.isPresent()) {
                  // symbol.putDownRule(IPatternMatcher.SET, true, symbol, res, false);
                  symbol.assignValue(res, false);
                }
                return rightHandSide;
              }
            } catch (SymjaMathException sme) {
              return Errors.printMessage(S.Off, sme, engine);
            }
          }
        } else {
          Errors.printMessage(builtinSymbol, "setps", F.list(part), engine);
          return rightHandSide;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NHOLDREST);
    }
  }

  private static final class Pause extends AbstractFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        int pause = ast.arg1().toIntDefault();
        if (pause > 0) {
          try {
            TimeUnit.SECONDS.sleep(pause);
          } catch (InterruptedException e) {
          }
          return S.Null;
        }
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Quiet(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>expr</code> in &quot;quiet&quot; mode (i.e. no warning messages are shown
   * during evaluation).
   *
   * </blockquote>
   */
  private static class Quiet extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();
        IExpr evalQuiet = engine.evalQuietNIL(arg1);
        return evalQuiet.orElse(arg1);
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Reap(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the result of evaluating <code>expr</code>, together with all values sown during this
   * evaluation. Values sown with different tags are given in different lists.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Reap(Sow(3); Sow(1))
   * {1,{{3,1}}}
   * </pre>
   */
  private static final class Reap extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int argSize = ast.argSize();
      if (argSize >= 1 && argSize <= 3) {
        java.util.List<IExpr> oldList = engine.getReapList();
        try {
          java.util.List<IExpr> reapList = new ArrayList<IExpr>();
          engine.setReapList(reapList);
          if (ast.isAST1()) {
            IExpr expr = engine.evaluate(ast.arg1());
            if (reapList.isEmpty()) {
              return F.list(expr, F.CEmptyList);
            }
            IASTAppendable result = F.ListAlloc(reapList.size() / 2);
            for (int i = 1; i < reapList.size(); i += 2) {
              result.append(reapList.get(i));
            }
            return F.list(expr, result);
          } else if (ast.isAST2() || ast.isAST3()) {
            IExpr expr = engine.evaluate(ast.arg1());
            IExpr arg2 = ast.arg2();
            IExpr head = null;
            if (ast.isAST3()) {
              head = engine.evaluate(ast.arg3());
            }
            IPatternMatcher[] matcher;
            if (arg2.isList()) {
              IAST matcherAST = (IAST) arg2;

              matcher = new IPatternMatcher[matcherAST.size() - 1];
              for (int i = 1; i < matcherAST.size(); i++) {
                matcher[i - 1] = engine.evalPatternMatcher(matcherAST.get(i));
              }
            } else {
              matcher = new IPatternMatcher[] {engine.evalPatternMatcher(arg2)};
            }
            if (reapList.isEmpty()) {
              return F.list(expr, F.CEmptyList);
            }
            IASTAppendable result = F.ListAlloc(reapList.size() / 2);
            for (int i = 1; i < reapList.size(); i += 2) {
              for (int j = 0; j < matcher.length; j++) {
                if (matcher[j].test(reapList.get(i - 1))) {
                  if (head == null) {
                    result.append(reapList.get(i));
                  } else {
                    result.append(F.binaryAST2(head, reapList.get(i - 1), reapList.get(i)));
                  }
                  break;
                }
              }
            }
            return F.list(expr, result);
          }
        } finally {
          engine.setReapList(oldList);
        }
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDFIRST);
    }
  }

  private static class RepeatedTiming extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        int n = 10;
        IExpr result = F.Null;
        double[] r = new double[n];
        for (int i = 0; i < n; i++) {
          final long begin = System.currentTimeMillis();
          result = engine.evaluate(ast.arg1());
          r[i] = (System.currentTimeMillis() - begin) / 1000.0;
        }
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(r);
        return F.list(F.num(descriptiveStatistics.getMean()), F.HoldForm(result));
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
    }
  }
  /**
   *
   *
   * <pre>
   * Return(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * aborts a function call and returns <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; f(x_) := (If(x &lt; 0, Return(0)); x)
   * &gt;&gt; f(-1)
   * 0
   *
   * &gt;&gt; Do(If(i &gt; 3, Return()); Print(i), {i, 10})
   *  | 1
   *  | 2
   *  | 3
   * </pre>
   *
   * <p>
   * <code>Return</code> only exits from the innermost control flow construct.
   *
   * <pre>
   * &gt;&gt; g(x_) := (Do(If(x &lt; 0, Return(0)), {i, {2, 1, 0, -1}}); x)
   * &gt;&gt; g(-1)
   * -1
   *
   * &gt;&gt; h(x_) := (If(x &lt; 0, Return()); x)
   * &gt;&gt; h(1)
   * 1
   *
   * &gt;&gt; h(-1) // FullForm
   * "Null"
   *
   * &gt;&gt; f(x_) := Return(x)
   * &gt;&gt; g(y_) := Module({}, z = f(y); 2)
   * &gt;&gt; g(1)
   * 2
   * </pre>
   */
  private static final class Return extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast == F.CReturnFalse) {
        throw ReturnException.RETURN_FALSE;
      }
      if (ast == F.CReturnTrue) {
        throw ReturnException.RETURN_TRUE;
      }
      if (ast.isAST1()) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isFalse()) {
          throw ReturnException.RETURN_FALSE;
        }
        if (arg1.isTrue()) {
          throw ReturnException.RETURN_TRUE;
        }
        throw new ReturnException(arg1);
      }
      if (ast.isAST0()) {
        throw new ReturnException();
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Sow(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * sends the value <code>expr</code> to the innermost <code>Reap</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Reap(Sow(3); Sow(1))
   * {1,{{3,1}}}
   * </pre>
   */
  private static final class Sow extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      java.util.List<IExpr> reapList = engine.getReapList();
      if (reapList != null) {
        if (ast.isAST1()) {
          IExpr arg1 = engine.evaluate(ast.arg1());
          appendReapList(arg1, S.None, reapList);
          return arg1;
        } else if (ast.isAST2()) {
          IExpr arg1 = engine.evaluate(ast.arg1());
          IExpr tags = engine.evaluate(ast.arg2());
          if (tags.isList()) {
            ((IAST) tags).forEach(x -> appendReapList(arg1, x, reapList));
          } else {
            appendReapList(arg1, tags, reapList);
            return arg1;
          }
        }
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    private static void appendReapList(final IExpr expr, final IExpr tag,
        java.util.List<IExpr> reapList) {
      for (int i = 0; i < reapList.size(); i += 2) {
        IExpr currentTag = reapList.get(i);
        if (tag.equals(currentTag)) {
          IASTAppendable temp = (IASTAppendable) reapList.get(i + 1);
          temp.append(expr);
          return;
        }
      }
      IASTAppendable temp = F.ListAlloc(10);
      temp.append(expr);
      reapList.add(tag);
      reapList.add(temp);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Stack( )
   * </code>
   * </pre>
   *
   * <p>
   * return a list of the heads of the current stack wrapped by <code>HoldForm</code>.
   *
   * <pre>
   * <code>Stack(_)
   * </code>
   * </pre>
   *
   * <p>
   * return a list of the expressions of the current stack wrapped by <code>HoldForm</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>
   * </code>
   * </pre>
   */
  private static final class Stack extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Deque<IExpr> stack = engine.getStack();
      java.util.Iterator<IExpr> iter = stack.descendingIterator();
      IASTAppendable result = F.ListAlloc(stack.size());
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();
        if (arg1.isBlank()) {
          while (iter.hasNext()) {
            IExpr expr = iter.next();
            if (expr != ast) {
              result.append(F.HoldForm(expr));
            }
          }
        } else {
          IPatternMatcher matcher = engine.evalPatternMatcher(arg1);
          while (iter.hasNext()) {
            IExpr expr = iter.next();
            if (expr != ast && //
                matcher.test(expr, engine)) {
              result.append(F.HoldForm(expr));
            }
          }
        }
      } else {
        while (iter.hasNext()) {
          IExpr expr = iter.next();
          if (expr != ast) {
            result.append(F.HoldForm(expr.head()));
          }
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class StackBegin extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Deque<IExpr> stack = engine.getStack();
      try {
        engine.stackBegin();
        return engine.evaluate(ast.arg1());
      } finally {
        engine.setStack(stack);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Switch(expr, pattern1, value1, pattern2, value2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * yields the first <code>value</code> for which <code>expr</code> matches the corresponding
   * pattern.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Switch(2, 1, x, 2, y, 3, z)
   * y
   *
   * &gt;&gt; Switch(5, 1, x, 2, y)
   * Switch(5, 1, x, 2, y)
   *
   * &gt;&gt; Switch(5, 1, x, 2, y, _, z)
   * z
   * </pre>
   *
   * <p>
   * Switch called with 2 arguments. Switch must be called with an odd number of arguments.
   *
   * <pre>
   * &gt;&gt; Switch(2, 1)
   * Switch(2, 1)
   * </pre>
   *
   * <p>
   * Switch called with 2 arguments. Switch must be called with an odd number of arguments.
   *
   * <pre>
   * &gt;&gt; a; Switch(b, b)
   * Switch(b, b)
   * </pre>
   *
   * <p>
   * Switch called with 2 arguments. Switch must be called with an odd number of arguments.
   *
   * <pre>
   * &gt;&gt; z = Switch(b, b);
   * &gt;&gt; z
   *
   * Switch(b, b)
   * </pre>
   */
  private static final class Switch extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if ((ast.size() & 0x0001) != 0x0000) {
        // `1` called with `2` arguments. `3`
        return Errors.printMessage(S.Switch, "argct", F.List(S.Switch, F.ZZ(ast.argSize()),
            F.stringx("Switch must be called with an odd number of arguments.")), engine);
      }
      if (ast.size() > 3) {
        final IExpr arg1 = engine.evaluate(ast.arg1());
        IPatternMatcher matcher;
        for (int i = 2; i < ast.size(); i += 2) {
          matcher = engine.evalPatternMatcher(ast.get(i));
          if (matcher.test(arg1, engine) && (i + 1 < ast.size())) {
            return engine.evaluate(ast.get(i + 1));
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class TimeConstrained extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    static class EvalControlledCallable implements Callable<IExpr> {
      private final EvalEngine fEngine;
      private IExpr fExpr;
      private long fSeconds;

      /**
       * Copy the current threads engine state into a new <code>EvalEngine</code> and do the
       * calculation in this <code>Callable</code> with the new <code>EvalEngine</code>.
       *
       * @param engine
       */
      public EvalControlledCallable(EvalEngine engine) {
        fEngine = engine.copy();
      }

      @Override
      public IExpr call() {
        EvalEngine.set(fEngine);
        try {
          long timeConstrainedMillis = System.currentTimeMillis() + fSeconds * 1000L;
          fEngine.setTimeConstrainedMillis(timeConstrainedMillis);
          return fEngine.evaluate(fExpr);
        } catch (org.matheclipse.core.eval.exception.TimeoutException e) {
          if (Config.DEBUG) {
            System.out
                .println("TimeConstrained evaluation failed: " + fExpr + "\nseconds: " + fSeconds);
          }
          // Errors.printMessage(S.TimeConstrained, e, fEngine);
          return S.$Aborted;
        } catch (final RecursionLimitExceeded | ASTElementLimitExceeded re) {
          throw re;
        } catch (Exception | OutOfMemoryError | StackOverflowError e) {
          Errors.printMessage(S.TimeConstrained, e, EvalEngine.get());
        } finally {
          fEngine.setTimeConstrainedMillis(-1);
          EvalEngine.remove();
        }
        return S.$Aborted;
      }

      public void cancel() {
        fEngine.stopRequest();
      }

      public void setExpr(IExpr fExpr, long seconds) {
        this.fExpr = fExpr;
        this.fSeconds = seconds;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int argSize = ast.argSize();
      if (argSize >= 2 && argSize <= 3) {
        long s = engine.getSeconds();
        if (s > 0 || Config.TIMECONSTRAINED_NO_THREAD) {
          // no new thread should be spawned
          if (ast.isAST3()) {
            // `1`.
            // Errors.printMessage(S.TimeConstrained, "error",
            // F.List("Single thread mode: default expression " + ast.arg3() + " will be ignored"),
            // engine);
          }
          return engine.evaluate(ast.arg1());
        }

        IExpr arg2 = engine.evaluate(ast.arg2());
        // System.out.println(ast.setAtCopy(2, arg2));
        long seconds = 0L;
        try {
          if (arg2.isReal()) {
            arg2 = ((IReal) arg2).ceilFraction();
            seconds = ((IReal) arg2).toLong();
          } else {
            // Positive machine-sized integer expected at position `2` in `1`.
            return Errors.printMessage(ast.topHead(), "intpm", F.list(F.C2, ast), engine);
          }

        } catch (ArithmeticException ae) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intpm", F.list(F.C2, ast), engine);
        }
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        TimeLimiter timeLimiter = SimpleTimeLimiter.create(executorService); // Executors.newSingleThreadExecutor());
        EvalControlledCallable work = new EvalControlledCallable(engine);

        try {
          seconds = seconds > 1 ? seconds - 1 : seconds;
          work.setExpr(ast.arg1(), seconds);
          return timeLimiter.callWithTimeout(work, seconds, TimeUnit.SECONDS);
        } catch (org.matheclipse.core.eval.exception.TimeoutException
            | java.util.concurrent.TimeoutException
            | com.google.common.util.concurrent.UncheckedTimeoutException e) {
          Errors.printMessage(S.TimeConstrained, e, EvalEngine.get());
          if (ast.isAST3()) {
            return ast.arg3();
          }
          return S.$Aborted;
        } catch (Exception e) {
          // Appengine example: com.google.apphosting.api.DeadlineExceededException
          Errors.printMessage(S.TimeConstrained, e, EvalEngine.get());
          if (ast.isAST3()) {
            return ast.arg3();
          }
          return S.Null;
        } finally {
          work.cancel();
          MoreExecutors.shutdownAndAwaitTermination(executorService, 1, TimeUnit.SECONDS);
        }
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class TimeRemaining extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        double timeRemaining = engine.getRemainingSeconds();
        if (timeRemaining < 0.0) {
          return F.CInfinity;
        }
        return F.num(timeRemaining);
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /** Calculate the time needed for evaluating an expression */
  private static class Timing extends AbsoluteTiming implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        if (bean.isCurrentThreadCpuTimeSupported()) {
          final long begin = bean.getCurrentThreadCpuTime();
          final IExpr result = engine.evaluate(ast.arg1());
          final long end = bean.getCurrentThreadCpuTime();
          double value = (end - begin) / 1000000000.0;
          return F.list(F.num(value), F.HoldForm(result));
        }
        // fall back to AbsoluteTiming
        return super.evaluate(ast, engine);
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class Throw extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast == F.CThrowFalse) {
        throw ThrowException.THROW_FALSE;
      }
      if (ast == F.CThrowTrue) {
        throw ThrowException.THROW_TRUE;
      }
      if (ast.isAST1()) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isFalse()) {
          throw ThrowException.THROW_FALSE;
        }
        if (arg1.isTrue()) {
          throw ThrowException.THROW_TRUE;
        }
        throw new ThrowException(arg1);
      } else if (ast.isAST2()) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        throw new ThrowException(arg1, ast.arg2());
      }

      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * Trace(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the evaluation steps which are used to get the result.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Trace(D(Sin(x),x))
   * {{Cos(#1)&amp;[x],Cos(x)},D(x,x)*Cos(x),{D(x,x),1},1*Cos(x),Cos(x)}
   * </pre>
   */
  private static class Trace extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {
    /**
     * Trace the evaluation steps for a given expression. The resulting trace expression list is
     * wrapped by Hold (i.e. <code>Hold[{...}]</code>.
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int argSize = ast.argSize();
      if (argSize >= 1 && argSize <= 2) {
        try {
          final IExpr temp = ast.arg1();
          IPatternMatcher matcher = null;
          if (ast.isAST2()) {
            matcher = engine.evalPatternMatcher(ast.arg2());
          }

          return engine.evalTrace(temp, matcher);
        } catch (RuntimeException rex) {
          Errors.printMessage(S.Trace, rex, EvalEngine.get());
        }
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class TraceForm extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() == S.TraceForm) {
        final int argSize = ast.argSize();
        if (argSize >= 1 && argSize <= 2) {
          try {
            IASTMutable trace = ast.copy();
            trace.set(0, S.Trace);
            final IExpr temp = engine.evaluate(trace);
            StringBuilder jsControl = new StringBuilder();

            createTree(jsControl, temp);
            return F.JSFormData(jsControl.toString(), "traceform");
          } catch (RuntimeException rex) {
            Errors.printMessage(S.TraceForm, rex, EvalEngine.get());
          }
        }
        return engine.checkBuiltinArgsSize(ast, this);
      }
      return F.NIL;
    }

    private static IExpr createTree(StringBuilder jsControl, IExpr traceExpr) {
      if (traceExpr.isList()) {
        IExpr l = F.NIL;
        IAST list = (IAST) traceExpr;
        jsControl.append("<ul>");
        for (int i = 1; i < list.size(); i++) {
          IExpr arg = list.get(i);
          if (arg.isAST(S.HoldForm, 2)) {
            jsControl.append("<li>\n");
            String html = StringEscapeUtils.escapeHtml4(arg.first().toString());
            jsControl.append(html);
            jsControl.append("</li>\n");
          } else if (arg.isList()) {
            IExpr last = arg.last();
            if (last.isAST(S.HoldForm, 2)) {
              jsControl.append("<li>\n");
              l = last.first();
              String html = StringEscapeUtils.escapeHtml4(l.toString());
              jsControl.append(html);
              createTree(jsControl, arg);
              jsControl.append("</li>\n");
            } else {
              // StringBuilder subControl = new StringBuilder();
              // IExpr sub = createTree(subControl, arg);
              // jsControl.append("<li>{\n");
              // if (sub.isPresent()) {
              // String html = StringEscapeUtils.escapeHtml4(sub.toString());
              // jsControl.append(html);
              // }
              jsControl.append("<li>{\n");
              createTree(jsControl, arg);
              jsControl.append("}</li>\n");
            }
          } else {
            jsControl.append("<li>\n");
            String html = StringEscapeUtils.escapeHtml4(arg.toString());
            jsControl.append(html);
            jsControl.append("</li>\n");
          }
        }
        jsControl.append("</ul>");
        return l;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class Unevaluated extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return ast.arg1();
      }
      return engine.checkBuiltinArgsSize(ast, this);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALLCOMPLETE);
    }
  }

  /**
   *
   *
   * <pre>
   * Which(cond1, expr1, cond2, expr2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * yields <code>expr1</code> if <code>cond1</code> evaluates to <code>True</code>, <code>expr2
   * </code> if <code>cond2</code> evaluates to <code>True</code>, etc.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; n=5;
   * &gt;&gt; Which(n == 3, x, n == 5, y)
   * y
   *
   * &gt;&gt; f(x_) := Which(x &lt; 0, -x, x == 0, 0, x &gt; 0, x)
   * &gt;&gt; f(-3)
   * 3
   * </pre>
   *
   * <p>
   * If no test yields <code>True</code>, <code>Which</code> returns <code>Null</code>:
   *
   * <pre>
   * &gt;&gt; Which(False, a)
   * </pre>
   *
   * <p>
   * If a test does not evaluate to <code>True</code> or <code>False</code>, evaluation stops and a
   * <code>Which</code> expression containing the remaining cases is returned:
   *
   * <pre>
   * &gt;&gt; Which(False, a, x, b, True, c)
   * Which(x,b,True,c)
   * </pre>
   *
   * <p>
   * <code>Which</code> must be called with an even number of arguments:
   *
   * <pre>
   * &gt;&gt; Which(a, b, c)
   * Which(a, b, c)
   * </pre>
   */
  private static final class Which extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (((ast.argSize()) & 0x0001) == 0x0001) {
        // `1`.
        return Errors.printMessage(S.Which, "error", F.List("Number of arguments must be even"),
            engine);
      }
      for (int i = 1; i < ast.size(); i += 2) {
        IExpr temp = engine.evaluate(ast.get(i));
        if (temp.isFalse()) {
          continue;
        }
        if (temp.isTrue()) {
          if ((i + 1 < ast.size())) {
            return engine.evaluate(ast.get(i + 1));
          }
          continue;
        }
        if (i == 1) {
          return F.NIL;
        }
        return F.ast(ast, ast.head(), true, i, ast.size());
      }
      return S.Null;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * While(test, body)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>body</code> as long as test evaluates to <code>True</code>.
   *
   * </blockquote>
   *
   * <pre>
   * While(test)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * runs the loop without any body.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * Compute the GCD of two numbers:
   *
   * <pre>
   * &gt;&gt; {a, b} = {27, 6};
   * &gt;&gt; While(b != 0, {a, b} = {b, Mod(a, b)});
   * &gt;&gt; a
   * 3
   *
   * &gt;&gt; i = 1; While(True, If(i^2 &gt; 100, Return(i + 1), i++))
   * 12
   * </pre>
   */
  private static final class While extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // While(test, body)
      final IExpr test = ast.arg1();
      final IExpr body = ast.isAST2() ? ast.arg2() : F.NIL;
      long iterationCounter = 0;
      while (engine.evalTrue(test)) {
        try {
          engine.evaluateNIL(body);
          if (Config.MAX_LOOP_COUNT <= ++iterationCounter) {
            IterationLimitExceeded.throwIt(iterationCounter, ast);
          }
        } catch (final BreakException e) {
          return S.Null;
        } catch (final ContinueException e) {
        } catch (final ReturnException e) {
          return e.getValue();
        }
      }

      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * With({list_of_local_variables}, expr )
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * evaluates <code>expr</code> for the <code>list_of_local_variables</code> by replacing the local
   * variables in <code>expr</code>.
   *
   * </blockquote>
   */
  private static final class With extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IAST moduleVariablesList = Validate.checkLocalVariableList(ast, 1, engine);
      if (moduleVariablesList.isPresent()) {
        IExpr lastArg;
        if (ast.argSize() > 2) {
          lastArg = ast.rest();
        } else {
          lastArg = ast.arg2();
        }
        IExpr temp = withSubstVariables(moduleVariablesList, lastArg, engine);
        if (temp.isPresent()) {
          return engine.evaluate(temp);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // in contrast to `Module` (which only allows 2 arguments), `With` allows multiple initializer
      // blocks
      return ARGS_2_INFINITY;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   * Remember which local variable names (appended with the module counter) we use in the given
   * <code>variablesMap</code>.
   *
   * @param variablesList initializer variables list from the <code>Module</code> function
   * @param variablesMap the resulting module variables map
   * @return
   */
  private static boolean rememberWithVariables(IAST variablesList,
      final java.util.Map<ISymbol, IExpr> variablesMap, EvalEngine engine) {
    ISymbol oldSymbol;
    for (int i = 1; i < variablesList.size(); i++) {
      if (variablesList.get(i).isAST(S.Set, 3)) {
        final IAST setFun = (IAST) variablesList.get(i);
        if (!setFun.arg1().isSymbol()) {
          // Local variable specification `1` contains `2`, which is an assignment to `3`; only
          // assignments to symbols are allowed.
          Errors.printMessage(S.With, "lvset",
              F.list(variablesList, variablesList.get(i), setFun.arg1()), engine);
          return false;
        }
        oldSymbol = (ISymbol) setFun.arg1();
        IExpr rightHandSide = setFun.arg2();
        IExpr temp = engine.evaluate(rightHandSide);
        VariablesSet set = new VariablesSet(temp);
        set.putAllSymbols(variablesMap);
        variablesMap.put(oldSymbol, temp);
      } else if (variablesList.get(i).isAST(S.SetDelayed, 3)) {
        final IAST setFun = (IAST) variablesList.get(i);
        if (!setFun.arg1().isSymbol()) {
          // Local variable specification `1` contains `2`, which is an assignment to `3`; only
          // assignments to symbols are allowed.
          Errors.printMessage(S.With, "lvset",
              F.list(variablesList, variablesList.get(i), setFun.arg1()), engine);
          return false;
        }
        oldSymbol = (ISymbol) setFun.arg1();
        IExpr rightHandSide = setFun.arg2();
        VariablesSet set = new VariablesSet(rightHandSide);
        set.putAllSymbols(variablesMap);
        variablesMap.put(oldSymbol, rightHandSide);
      } else {
        // Variable `1` in local variable specification `2` requires assigning a value
        Errors.printMessage(S.With, "lvws", F.list(variablesList.get(i), variablesList), engine);
        return false;
      }
    }
    return true;
  }

  /**
   * Remember which local variable names (appended with the module counter) we use in the given
   * <code>variablesMap</code>.
   *
   * @param variablesList initializer variables list from the <code>Module</code> function
   * @param varAppend the module counter string which aer appended to the variable names.
   * @param variablesMap the resulting module variables map
   * @param engine the evaluation engine
   */
  public static boolean rememberModuleVariables(IAST variablesList, final String varAppend,
      final java.util.Map<ISymbol, IExpr> variablesMap, final EvalEngine engine) {
    ISymbol oldSymbol;
    ISymbol newSymbol;
    for (int i = 1; i < variablesList.size(); i++) {
      if (variablesList.get(i).isSymbol()) {
        oldSymbol = (ISymbol) variablesList.get(i);
        newSymbol = F.Dummy(oldSymbol.toString() + varAppend);
        variablesMap.put(oldSymbol, newSymbol);
      } else {
        if (variablesList.get(i).isAST(S.Set, 3)) {
          final IAST setFun = (IAST) variablesList.get(i);
          if (!setFun.arg1().isSymbol()) {
            // Local variable specification `1` contains `2`, which is an assignment to `3`; only
            // assignments to symbols are allowed.
            Errors.printMessage(S.Module, "lvset",
                F.List(variablesList, variablesList.get(i), setFun.arg1()), engine);
            return false;
          }
          oldSymbol = (ISymbol) setFun.arg1();
          newSymbol = F.Dummy(oldSymbol.toString() + varAppend);
          variablesMap.put(oldSymbol, newSymbol);
          newSymbol.assignValue(engine.evaluate(setFun.arg2()));
        } else {
          // Local variable specification `1` contains `2` which is not a symbol or an assignment to
          // a symbol.
          Errors.printMessage(S.Module, "lvsym", F.List(variablesList, variablesList.get(i)),
              engine);
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Remember which local variable names we use in the given <code>assignedValues</code> and <code>
   * assignedRules</code>.
   *
   * @param variablesList initializer variables list from the <code>Block</code> function
   * @param assignedValues the variables mapped to their values (IExpr) before evaluating the block
   * @param assignedRules the variables mapped to their rules (RulesData) before evaluating the
   *        block
   * @param engine the evaluation engine
   */
  public static void rememberBlockVariables(IAST variablesList, final ISymbol[] symbolList,
      final IExpr[] assignedValues, final RulesData[] assignedRules, final EvalEngine engine) {
    ISymbol variableSymbol;
    for (int i = 1; i < variablesList.size(); i++) {
      if (variablesList.get(i).isSymbol()) {
        variableSymbol = (ISymbol) variablesList.get(i);
        if (variableSymbol.isBuiltInSymbol()) {
          ISymbol substitute = ((IBuiltInSymbol) variableSymbol).mapToGlobal(engine);
          if (substitute != null) {
            variableSymbol = substitute;
          }
        }
        symbolList[i] = variableSymbol;
        assignedValues[i] = variableSymbol.assignedValue();
        assignedRules[i] = variableSymbol.getRulesData();
      } else if (variablesList.get(i).isAST(S.Set, 3)) {
        final IAST setFun = (IAST) variablesList.get(i);
        if (setFun.arg1().isSymbol()) {
          variableSymbol = (ISymbol) setFun.arg1();
          if (variableSymbol.isBuiltInSymbol()) {
            ISymbol substitute = ((IBuiltInSymbol) variableSymbol).mapToGlobal(engine);
            if (substitute != null) {
              variableSymbol = substitute;
            }
          }
          symbolList[i] = variableSymbol;
          assignedValues[i] = variableSymbol.assignedValue();
          assignedRules[i] = variableSymbol.getRulesData();
        }
      }
    }

    for (int i = 1; i < variablesList.size(); i++) {
      if (variablesList.get(i).isSymbol()) {
        variableSymbol = symbolList[i];
        variableSymbol.assignValue(null, false);
        variableSymbol.setRulesData(null);
      } else {
        if (variablesList.get(i).isAST(S.Set, 3)) {
          final IAST setFun = (IAST) variablesList.get(i);
          if (setFun.arg1().isSymbol()) {
            variableSymbol = symbolList[i];
            IExpr temp = engine.evaluate(setFun.arg2());
            variableSymbol.assignValue(temp, false);
            variableSymbol.setRulesData(null);
          }
        }
      }
    }
  }

  /**
   * Substitute the variable names from the list with temporary dummy variable names in the
   * &quot;module-block&quot;..
   *
   * @param intializerList list of variables which should be substituted by appending <code>
   *     $<number></code> to the variable names
   * @param moduleBlock the module block where the variables should be replaced with temporary
   *        variables
   * @param engine
   * @return
   */
  private static IExpr moduleSubstVariables(IAST intializerList, IExpr moduleBlock,
      final EvalEngine engine) {
    // final long moduleCounter = engine.incModuleCounter();
    final String varAppend = EvalEngine.uniqueName("$");
    final java.util.IdentityHashMap<ISymbol, IExpr> moduleVariables =
        new IdentityHashMap<ISymbol, IExpr>();
    if (rememberModuleVariables(intializerList, varAppend, moduleVariables, engine)) {
      IExpr result = moduleBlock.accept(new ModuleReplaceAll(moduleVariables, engine, varAppend));
      return result.orElse(moduleBlock);
    }
    return F.NIL;
  }

  /**
   * Nest <code>this</code> expression with <code>head</code> applied <code>n</code> times to <code>
   * this</code>.
   *
   * <pre>
   * this.nest(h, 4)
   * </pre>
   *
   * gives
   *
   * <pre>
   * h(h(h(h(this))))
   * </pre>
   *
   * @param head the head which should be applied to this n times
   * @param n a value > 0, otherwise <code>this</code> will be returned as default value
   * @return
   */
  public static IExpr nest(final IExpr expr, final IExpr head, final int n, EvalEngine engine) {
    IExpr temp = expr;
    final Function<IExpr, IExpr> function = x -> F.unaryAST1(head, x);
    for (int i = 0; i < n; i++) {
      temp = engine.evaluate(function.apply(temp));
    }
    return temp;
  }

  /**
   * Substitute the variable names from the list with temporary dummy variable names in the
   * &quot;with-block&quot;..
   *
   * @param intializerList list of variables which should be substituted by appending <code>
   *     $<number></code> to the variable names
   * @param withBlock the with block where the variables should be replaced with temporary variables
   * @param engine
   * @return
   */
  private static IExpr withSubstVariables(IAST intializerList, IExpr withBlock,
      final EvalEngine engine) {
    // final long moduleCounter = engine.incModuleCounter();
    // final String varAppend = "$" + moduleCounter;
    final java.util.IdentityHashMap<ISymbol, IExpr> moduleVariables =
        new IdentityHashMap<ISymbol, IExpr>();
    if (rememberWithVariables(intializerList, moduleVariables, engine)) {
      IExpr result = withBlock
          .accept(new ModuleReplaceAll(moduleVariables, engine, EvalEngine.uniqueName("$")));
      return result.orElse(withBlock);
    }
    return F.NIL;
  }

  /**
   * Get the element stored at the given <code>position</code>.
   *
   * @param ast
   * @param pos
   * @param engine
   * @return
   */
  private static IExpr getIndex(IAST ast, final int pos, EvalEngine engine) {
    int position = pos;
    if (position < 0) {
      position = ast.size() + position;
    }
    if ((position < 0) || (position >= ast.size())) {
      // Part `1` of `2` does not exist.
      return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(pos), ast), engine);
    }
    return ast.get(position);
  }

  /**
   * If <code>ast</code> is an instance of IAssociation return the rule defined at the given
   * position in the association. Otherwise return the element at that position.
   *
   * @param ast
   * @param pos
   * @param engine
   * @return
   */
  private static IExpr getIndexRule(IAST ast, final int pos, EvalEngine engine) {
    int position = pos;
    if (position < 0) {
      position = ast.size() + position;
    }
    if ((position < 0) || (position >= ast.size())) {
      // Part `1` of `2` does not exist.
      return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(pos), ast), engine);
    }
    return ast.getRule(position);
  }

  /**
   * Get the <code>Part[...]</code> of an expression. If the expression is no <code>IAST</code>
   * return the expression.
   *
   * @param arg1 the expression from which parts should be extracted
   * @param ast the <code>Part[...]</code> expression
   * @param pos the index position from which the sub-expressions should be extracted
   * @param engine the evaluation engine
   * @return
   */
  public static IExpr part(final IAST arg1, final IAST ast, int pos, EvalEngine engine) {
    final IExpr arg2 = engine.evaluate(ast.get(pos));
    int p1 = pos + 1;
    int[] span = arg2.isSpan(arg1.size());
    if (span != null) {
      int start = span[0];
      int last = span[1];
      int step = span[2];
      return spanPart(ast, pos, arg1, arg2, start, last, step, p1, engine);
    } else if (arg2.equals(S.All)) {
      return spanPart(ast, pos, arg1, arg2, 1, arg1.size() - 1, 1, p1, engine);
    } else if (arg2.isReal()) {
      final int indx = ast.get(pos).toIntDefault();
      if (indx == Integer.MIN_VALUE) {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Part, "partw", F.list(ast.get(pos), arg1), engine);
      }
      IExpr result = getIndex(arg1, indx, engine);
      if (result.isPresent()) {
        if (p1 < ast.size()) {
          if (result.isASTOrAssociation()) {
            return part((IAST) result, ast, p1, engine);
          } else {
            // Part specification `1` is longer than depth of object.
            return Errors.printMessage(S.Part, "partd", F.list(result), engine);
          }
        }
        return result;
      }
      return F.NIL;
    } else if (arg1.isAssociation()) {
      IAssociation assoc = (IAssociation) arg1;
      if (arg2.isList()) {
        IExpr temp = null;
        final IAST list = (IAST) arg2;
        final IAssociation result = F.assoc(); // list.size());

        for (int i = 1; i < list.size(); i++) {
          final IExpr listArg = list.get(i);
          if (listArg.isReal()) {
            final int indx = listArg.toIntDefault();
            if (indx == Integer.MIN_VALUE) {
              // Part `1` of `2` does not exist.
              return Errors.printMessage(S.Part, "partw", F.list(listArg, arg1), engine);
            }
            IExpr ires = getIndexRule(arg1, indx, engine);
            if (ires.isPresent()) {
              if (p1 < ast.size()) {
                if (ires.isASTOrAssociation()) {

                  temp = part((IAST) ires, ast, p1, engine);
                  if (temp.isPresent()) {
                    try {
                      result.appendRule(temp);
                    } catch (IndexOutOfBoundsException ioobex) {
                      return Errors.printMessage(S.Part, "pkspec1", F.list(temp), engine);
                    }
                  } else {
                    // an error occurred
                    return F.NIL;
                  }

                } else {
                  // Part specification `1` is longer than depth of object.
                  return Errors.printMessage(S.Part, "partd", F.list(ires), engine);
                }
              } else {
                try {
                  result.appendRule(ires);
                } catch (IndexOutOfBoundsException ioobex) {
                  return Errors.printMessage(S.Part, "pkspec1", F.list(ires), engine);
                }
              }
            } else {
              return F.NIL;
            }
          } else if (listArg.isAST(S.Key, 2)) {
            result.appendRule(assoc.getRule(listArg.first()));
          } else if (listArg.isString()) {
            result.appendRule(assoc.getRule(listArg));
          } else if (listArg.isNumber()) {
            // The expression `1` cannot be used as a part specification.
            return Errors.printMessage(S.Part, "pkspec1", F.list(list), engine);
          }
        }
        return result;
      }

      IExpr result = F.NIL;
      if (arg2.isAST(S.Key, 2)) {
        result = assoc.getValue(arg2.first());
      } else if (arg2.isString()) {
        result = assoc.getValue(arg2);
      }

      if (result.isPresent()) {
        if (p1 < ast.size()) {
          if (result.isASTOrAssociation()) {
            return part((IAST) result, ast, p1, engine);
          } else {
            // Part specification `1` is longer than depth of object.
            return Errors.printMessage(S.Part, "partd", F.list(result), engine);
          }
        }
        return result;
      }
    } else if (arg2.isList()) {
      IExpr temp = null;
      final IAST list = (IAST) arg2;
      final IASTAppendable result = F.ast(arg1.head(), list.size());

      for (int i = 1; i < list.size(); i++) {
        final IExpr listArg = list.get(i);
        if (listArg.isReal()) {
          final int indx = listArg.toIntDefault();
          if (indx == Integer.MIN_VALUE) {
            // Part `1` of `2` does not exist.
            return Errors.printMessage(S.Part, "partw", F.list(listArg, arg1), engine);
          }
          IExpr ires = getIndex(arg1, indx, engine);
          if (ires.isPresent()) {
            if (p1 < ast.size()) {
              if (ires.isASTOrAssociation()) {
                temp = part((IAST) ires, ast, p1, engine);
                if (temp.isPresent()) {
                  result.append(temp);
                } else {
                  // an error occurred
                  return F.NIL;
                }
              } else {
                // Part specification `1` is longer than depth of object.
                return Errors.printMessage(S.Part, "partd", F.list(ires), engine);
              }
            } else {
              result.append(ires);
            }
          } else {
            return F.NIL;
          }
        } else if (listArg.isNumber() || listArg.isString()) {
          // The expression `1` cannot be used as a part specification.
          return Errors.printMessage(S.Part, "pkspec1", F.list(list), engine);
        }
      }
      return result;
    }

    // The expression `1` cannot be used as a part specification.
    return Errors.printMessage(S.Part, "pkspec1", F.list(arg2), engine);
  }

  public static IExpr sparsePart(final ISparseArray arg1, final IAST ast, int pos,
      EvalEngine engine) {
    if (ast.forAll(x -> (x.isInteger() && x.isPositive()) || x.equals(S.All), 2)) {
      return arg1.getPart(ast, 2);
    }
    // TODO implement more combinations for SparseArray

    IExpr temp = arg1.normal(false);
    if (temp.isList()) {
      IExpr res = part((IAST) temp, ast, pos, engine);
      if (res.isList()) {
        ISparseArray sparseArray = SparseArrayExpr.newDenseList((IAST) res, arg1.getDefaultValue());
        if (sparseArray != null) {
          return sparseArray;
        }
      }
      // return temp;
    }

    // The expression `1` cannot be used as a part specification.
    return Errors.printMessage(S.Part, "pkspec1", F.list(ast), engine);
  }

  private static IExpr spanPart(final IAST ast, int pos, final IAST arg1, final IExpr arg2,
      int start, int last, int step, int p1, EvalEngine engine) {

    final int size = arg1.size();
    if (step < 0 && start >= last) {
      IASTAppendable result = arg1.copyHead((last - start) / step + 2);
      for (int i = start; i >= last; i += step) {
        if (p1 >= ast.size()) {
          IExpr temp = getIndexRule(arg1, i, engine);
          if (temp.isPresent()) {
            result.appendRule(temp);
            continue;
          }
          return F.NIL;
        }
        if (arg1.get(i).isASTOrAssociation()) {
          if (i >= size) {
            // Cannot take positions `1` through `2` in `3`.
            return Errors.printMessage(S.Part, "take", F.list(F.ZZ(start), F.ZZ(last), arg1),
                engine);
          }
          IExpr temp = part((IAST) arg1.get(i), ast, p1, engine);
          if (temp.isPresent()) {
            result.append(temp);
            continue;
          }
        }
        // Part specification `1` is longer than depth of object.
        return Errors.printMessage(S.Part, "partd", F.list(arg1.get(i)), engine);
      }
      return result;
    } else if (step > 0 && (last != 1 || start <= last)) {
      IASTAppendable result = arg1.copyHead((last - start) / step + 2);
      for (int i = start; i <= last; i += step) {
        if (p1 >= ast.size()) {
          IExpr temp = getIndexRule(arg1, i, engine);
          if (temp.isPresent()) {
            result.appendRule(temp);
            continue;
          }
          return F.NIL;
        }
        if (arg1.get(i).isASTOrAssociation()) {
          if (i >= size) {
            // Cannot take positions `1` through `2` in `3`.
            return Errors.printMessage(S.Part, "take", F.list(F.ZZ(start), F.ZZ(last), arg1),
                engine);
          }

          if (arg1.isAssociation()) {
            IAST rule = (IAST) arg1.getRule(i);
            IAST argAST = (IAST) rule.second();

            IExpr temp = part(argAST, ast, p1, engine);
            if (temp.isPresent()) {
              result.appendRule(rule.setAtCopy(2, temp));
              continue;
            }
          } else {
            IAST argAST = (IAST) arg1.get(i);
            IExpr temp = part(argAST, ast, p1, engine);
            if (temp.isPresent()) {
              result.append(temp);
              continue;
            }
          }
        }
        // Part specification `1` is longer than depth of object.
        return Errors.printMessage(S.Part, "partd", F.list(arg1.get(i)), engine);
      }
      return result;
    }
    // The expression `1` cannot be used as a part specification.
    return Errors.printMessage(S.Part, "pkspec1", F.list(arg2), engine);
  }

  private static IExpr assignPart(final IExpr assignedExpr, final IAST part, int partPosition,
      IExpr value, EvalEngine engine) {
    if (partPosition >= part.size()) {
      // stop recursion
      return value;
    }
    if (!assignedExpr.isASTOrAssociation()) {
      // Part specification `1` is longer than depth of object.
      return Errors.printMessage(S.Part, "partd", F.list(part), engine);
    }
    IAST assignedAST = (IAST) assignedExpr;
    final IExpr arg2 = engine.evaluate(part.get(partPosition));
    int partPositionPlus1 = partPosition + 1;
    int[] span = arg2.isSpan(assignedAST.size());
    if (span != null) {
      int start = span[0];
      int last = span[1];
      int step = span[2];
      IASTAppendable result = F.NIL;
      IExpr element;

      if (step < 0 && start >= last) {
        for (int i = start; i >= last; i += step) {
          element = assignedAST.get(i);
          result = assignPartSpanValue(assignedAST, element, part, partPositionPlus1, result, i,
              value, engine);
        }
      } else if (step > 0 && (last != 1 || start <= last)) {
        for (int i = start; i <= last; i += step) {
          element = assignedAST.get(i);
          result = assignPartSpanValue(assignedAST, element, part, partPositionPlus1, result, i,
              value, engine);
        }
      } else {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(partPosition), arg2), engine);
      }
      return result;
    } else if (arg2.isReal()) {
      int indx = Validate.throwIntType(arg2, Integer.MIN_VALUE, engine);
      if (indx < 0) {
        indx = assignedAST.size() + indx;
      }
      if ((indx < 0) || (indx >= assignedAST.size())) {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(indx), assignedAST), engine);
      }
      IASTAppendable result = F.NIL;
      IExpr temp = assignPart(assignedAST.get(indx), part, partPositionPlus1, value, engine);
      if (temp.isPresent()) {
        if (result.isNIL()) {
          result = assignedAST.copyAppendable();
        }
        result.set(indx, temp);
      }
      return result;
    } else if (arg2.isList()) {
      IExpr temp = null;
      final IAST list = (IAST) arg2;
      final IASTAppendable result = F.ListAlloc(list.size());

      for (int i = 1; i < list.size(); i++) {
        final IExpr listArg = list.get(i);
        if (listArg.isInteger()) {
          IExpr ires = null;

          final int indx = Validate.throwIntType(listArg, Integer.MIN_VALUE, engine);
          ires = assignPartValue(assignedAST, indx, value);
          if (ires == null) {
            return F.NIL;
          }
          if (partPositionPlus1 < part.size()) {
            if (ires.isASTOrAssociation()) {
              temp = assignPart(ires, part, partPositionPlus1, value, engine);
              result.append(temp);
            } else {
              // Part `1` of `2` does not exist.
              return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(partPosition), assignedAST),
                  engine);
            }
          } else {
            result.append(ires);
          }
        }
      }
      return result;
    }
    // Part `1` of `2` does not exist.
    return Errors.printMessage(S.Part, "partw", F.list(arg2, assignedAST), engine);
  }

  private static IExpr assignPart(final IExpr assignedExpr, final IAST part, int partPosition,
      IAST rhs, int rhsPos, EvalEngine engine) {
    if (!assignedExpr.isASTOrAssociation() || partPosition >= part.size()) {
      return assignedExpr;
    }
    IAST assignedAST = (IAST) assignedExpr;
    final IExpr arg2 = part.get(partPosition);
    int partPositionPlus1 = partPosition + 1;
    int[] span = arg2.isSpan(assignedAST.size());
    if (span != null) {
      int start = span[0];
      int last = span[1];
      int step = span[2];
      IASTAppendable result = F.NIL;

      if (step < 0 && start >= last) {
        int rhsIndx = 1;
        for (int i = start; i >= last; i += step) {
          IExpr temp = rhs.get(rhsIndx++);
          if (!temp.isList()) {
            temp = assignPart(assignedAST.get(i), part, partPositionPlus1, temp, engine);
          } else {
            temp = assignPart(assignedAST.get(i), part, partPositionPlus1, (IAST) temp, 1, engine);
          }

          if (temp.isPresent()) {
            if (result.isNIL()) {
              result = assignedAST.copyAppendable();
            }
            result.set(i, temp);
          }
        }
      } else if (step > 0 && (last != 1 || start <= last)) {
        int rhsIndx = 1;
        for (int i = start; i <= last; i += step) {
          IExpr temp = rhs.get(rhsIndx++);
          if (!temp.isList()) {
            temp = assignPart(assignedAST.get(i), part, partPositionPlus1, temp, engine);
          } else {
            temp = assignPart(assignedAST.get(i), part, partPositionPlus1, (IAST) temp, 1, engine);
          }

          if (temp.isPresent()) {
            if (result.isNIL()) {
              result = assignedAST.copyAppendable();
            }
            result.set(i, temp);
          }
        }
      } else {
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Part, "partw", F.list(arg2, assignedAST), engine);
      }
      return result;
    } else if (arg2.isReal()) {
      final int indx = Validate.checkIntType(part, partPosition, Integer.MIN_VALUE);
      IExpr ires = null;
      ires = assignPartValue(assignedAST, indx, rhs);
      if (partPositionPlus1 < part.size()) {
        if (ires.isASTOrAssociation()) {
          return assignPart(ires, part, partPositionPlus1, rhs, rhsPos++, engine);
        } else {
          // Part `1` of `2` does not exist.
          return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(partPosition), assignedAST),
              engine);
        }
      }
      return ires;
    } else if (arg2.isList()) {
      IExpr temp = null;
      final IAST list = (IAST) arg2;
      final IASTAppendable result = F.ListAlloc(list.size());

      for (int i = 1; i < list.size(); i++) {
        final IExpr listArg = list.get(i);
        if (listArg.isInteger()) {
          IExpr ires = null;

          final int indx = Validate.throwIntType(listArg, Integer.MIN_VALUE, engine);
          ires = assignPartValue(assignedAST, indx, list);
          if (ires == null) {
            return F.NIL;
          }
          if (partPositionPlus1 < part.size()) {
            if (ires.isASTOrAssociation()) {
              temp = assignPart(ires, part, partPositionPlus1, rhs, rhsPos++, engine);
              result.append(temp);
            } else {
              // Part `1` of `2` does not exist.
              return Errors.printMessage(S.Part, "partw", F.list(F.ZZ(partPosition), assignedAST),
                  engine);
            }
          } else {
            result.append(ires);
          }
        }
      }
      return result;
    }
    // Part `1` of `2` does not exist.
    return Errors.printMessage(S.Part, "partw", F.list(arg2, assignedAST), engine);
  }

  /**
   * Assign the <code>value</code> to the given position in the left-hand-side. <code>
   * lhs[[position]] = value</code>
   *
   * @param lhs left-hand-side
   * @param partPosition
   * @param value
   * @return
   */
  private static IExpr assignPartValue(IAST lhs, int partPosition, IExpr value) {
    if (partPosition < 0) {
      partPosition = lhs.size() + partPosition;
    }
    if ((partPosition < 0) || (partPosition >= lhs.size())) {
      throw new ArgumentTypeException(
          "Part: index " + partPosition + " of " + lhs.toString() + " is out of bounds.");
    }
    return lhs.setAtCopy(partPosition, value);
  }

  /**
   * Call <code>assignPart(element, ast, pos, value, engine)</code> recursively and assign the
   * result to the given position in the result. <code>result[[position]] = resultValue</code>
   *
   * @param expr
   * @param element
   * @param part
   * @param partPosition
   * @param result will be cloned if an assignment occurs and returned by this method
   * @param position
   * @param value
   * @param engine the evaluation engineF
   * @return the (cloned and value assigned) result AST from input
   */
  private static IASTAppendable assignPartSpanValue(IAST expr, IExpr element, final IAST part,
      int partPosition, IASTAppendable result, int position, IExpr value, EvalEngine engine) {
    IExpr resultValue = assignPart(element, part, partPosition, value, engine);
    if (resultValue.isPresent()) {
      if (result.isNIL()) {
        result = expr.copyAppendable();
      }
      result.set(position, resultValue);
    }
    return result;
  }

  public static void initialize() {
    Initializer.init();
  }

  private Programming() {}
}
