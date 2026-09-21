package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.DefinitionFunctions.SymbolDefinition;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * ParallelTable(expr, {i, imin, imax}, ...)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * generates the same list as <code>Table</code>, evaluating <code>expr</code> for different values
 * of the outermost iterator on different threads at the same time.
 *
 * </blockquote>
 *
 * <h3>How the threads are kept apart</h3>
 *
 * <p>
 * The value of a symbol is stored in the symbol, and a symbol is shared by all threads. Two threads
 * which iterate <code>{i, 1, 10}</code> would therefore overwrite each other's <code>i</code> - and
 * so would the <code>k</code> of a <code>Sum(..., {k, n})</code> in a function the expression
 * calls. So each thread - a "kernel", in the words of the Wolfram Language - works on a copy of the
 * expression in which every symbol of the user is replaced by a symbol of its own, and the
 * definitions of the user's symbols are copied onto them. This is the very model of the parallel
 * kernels of the Wolfram Language, where definitions are distributed to the kernels before the
 * computation and side effects do not come back, and it is what the <code>DistributedContexts</code>
 * option selects the symbols for.
 */
public class ParallelTable extends AbstractFunctionEvaluator {

  /** <code>true</code> on a kernel thread; a nested <code>ParallelTable</code> is a <code>Table</code> */
  private static final ThreadLocal<Boolean> IN_KERNEL = new ThreadLocal<Boolean>();

  public ParallelTable() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // Method -> ..., DistributedContexts -> ..., ProgressReporting -> ... behind the iterators
    int last = ast.argSize();
    IExpr method = S.Automatic;
    IExpr distributedContexts = F.NIL;
    while (last > 2 && ast.get(last).isRuleAST() && ast.get(last).first().isSymbol()) {
      String name = ((ISymbol) ast.get(last).first()).getSymbolName().toLowerCase(Locale.US);
      IExpr value = engine.evaluate(ast.get(last).second());
      if (name.equals("method")) {
        method = value;
      } else if (name.equals("distributedcontexts")) {
        distributedContexts = value;
      } else if (!name.equals("progressreporting")) {
        break;
      }
      last--;
    }
    if (last < 2) {
      return F.NIL;
    }
    final IAST table = ast.copyUntil(last + 1).setAtCopy(0, S.Table);

    // ParallelTable(..., {Subscript(a, 1), 3}): a subscript as the iterator variable, or a formal
    // symbol
    IExpr localized = Iterator.evaluateWithLocalizedVariables(ast, engine);
    if (localized != null) {
      return localized;
    }

    final int kernels = Config.TIMECONSTRAINED_NO_THREAD ? 1 : Config.MAX_PARALLEL_KERNELS;
    if (kernels < 2 || IN_KERNEL.get() != null) {
      return engine.evaluateNIL(table);
    }

    // the values of the outermost iterator; the form of the others is checked as well
    final List<IExpr> values = new ArrayList<IExpr>();
    final IIterator<IExpr> iterator = Iterator.createIterators(table, false, engine).get(0);
    ISymbol variable = null;
    try {
      if (iterator.setUp()) {
        variable = iterator.getVariable();
        while (iterator.hasNext()) {
          values.add(iterator.next());
          if (values.size() > Config.MAX_AST_SIZE) {
            ASTElementLimitExceeded.throwIt(values.size());
          }
        }
      }
    } catch (NoEvalException nee) {
      // the iterator does not determine the values it iterates over
      return F.NIL;
    } finally {
      iterator.tearDown();
    }
    if (values.size() < 2) {
      return engine.evaluateNIL(table);
    }

    final int chunkSize = chunkSize(method, values.size(), kernels, engine);
    if (chunkSize <= 0) {
      return F.NIL;
    }
    Computation computation =
        new Computation(table, variable, values, chunkSize, distributedContexts, engine);
    return computation.run(Math.min(kernels, computation.numberOfChunks));
  }

  /**
   * The number of values of the outermost iterator which are evaluated in one piece.
   *
   * @param method the value of the <code>Method</code> option
   * @param size the number of values
   * @param kernels the number of kernels
   * @return <code>0</code> if <code>method</code> is not a valid setting
   */
  private static int chunkSize(IExpr method, int size, int kernels, EvalEngine engine) {
    long pieces;
    if (method == S.Automatic) {
      // a compromise between overhead and load balancing
      pieces = 4L * kernels;
    } else if (method.isString("CoarsestGrained")) {
      pieces = kernels;
    } else if (method.isString("FinestGrained")) {
      return 1;
    } else if (method.isRuleAST() && method.first().isString("ItemsPerEvaluation")
        && method.second().toIntDefault() > 0) {
      return method.second().toIntDefault();
    } else if (method.isRuleAST() && method.first().isString("EvaluationsPerKernel")
        && method.second().toIntDefault() > 0) {
      pieces = (long) method.second().toIntDefault() * kernels;
    } else {
      // Value of option `1` -> `2` is not Automatic, "CoarsestGrained", "FinestGrained", ...
      org.matheclipse.core.eval.Errors.printMessage(S.ParallelTable, "parmthd",
          F.list(S.Method, method), engine);
      return 0;
    }
    pieces = Math.min(pieces, size);
    return (int) ((size + pieces - 1) / pieces);
  }

  /** One evaluation of a <code>ParallelTable</code>. */
  private static final class Computation {
    /** <code>Table(expr, outer, inner...)</code> */
    final IAST table;

    /** the variable of the outermost iterator or <code>null</code> */
    final ISymbol variable;

    final List<IExpr> values;

    final int chunkSize;

    final int numberOfChunks;

    final EvalEngine engine;

    /** the symbols each kernel replaces by symbols of its own */
    final List<ISymbol> symbols = new ArrayList<ISymbol>();

    /** the definitions which are copied onto the symbols of each kernel */
    final Map<ISymbol, SymbolDefinition> definitions =
        new LinkedHashMap<ISymbol, SymbolDefinition>();

    final IExpr[] results;

    final AtomicInteger nextChunk = new AtomicInteger();

    /** set if a kernel failed or if a piece did not evaluate to a list */
    final AtomicBoolean stop = new AtomicBoolean();

    volatile boolean unevaluated;

    Computation(IAST table, ISymbol variable, List<IExpr> values, int chunkSize,
        IExpr distributedContexts, EvalEngine engine) {
      this.table = table;
      this.variable = variable;
      this.values = values;
      this.chunkSize = chunkSize;
      this.numberOfChunks = (values.size() + chunkSize - 1) / chunkSize;
      this.results = new IExpr[numberOfChunks];
      this.engine = engine;
      collectSymbols(distributedContexts);
    }

    /**
     * Find the symbols of the user in the table, in the values of the outermost iterator and in the
     * definitions of those symbols which are distributed to the kernels.
     */
    private void collectSymbols(IExpr distributedContexts) {
      final IdentityHashMap<ISymbol, Boolean> seen = new IdentityHashMap<ISymbol, Boolean>();
      final ArrayList<IExpr> todo = new ArrayList<IExpr>();
      todo.add(table);
      todo.addAll(values);
      while (!todo.isEmpty()) {
        collect(todo.remove(todo.size() - 1), seen, todo, distributedContexts);
      }
    }

    private void collect(IExpr expr, IdentityHashMap<ISymbol, Boolean> seen, List<IExpr> todo,
        IExpr distributedContexts) {
      if (expr instanceof ISymbol) {
        ISymbol symbol = (ISymbol) expr;
        if (isUserSymbol(symbol) && seen.put(symbol, Boolean.TRUE) == null) {
          symbols.add(symbol);
          if (isDistributed(symbol, distributedContexts)) {
            SymbolDefinition definition = new SymbolDefinition(symbol, engine);
            definitions.put(symbol, definition);
            definition.forEachExpr(todo::add);
          }
        }
      } else if (expr instanceof IPatternObject) {
        IPatternObject pattern = (IPatternObject) expr;
        if (pattern.getSymbol() != null) {
          collect(pattern.getSymbol(), seen, todo, distributedContexts);
        }
        if (pattern.getHeadTest() != null) {
          collect(pattern.getHeadTest(), seen, todo, distributedContexts);
        }
      } else if (expr instanceof IAST) {
        IAST ast = (IAST) expr;
        collect(ast.head(), seen, todo, distributedContexts);
        for (int i = 1; i < ast.size(); i++) {
          // getRule(): the elements of an association are its rules
          collect(ast.getRule(i), seen, todo, distributedContexts);
        }
      }
    }

    /** A symbol which can be assigned to, so that two threads must not share it. */
    private static boolean isUserSymbol(ISymbol symbol) {
      return !(symbol instanceof IBuiltInSymbol) && symbol.getContext() != Context.SYSTEM
          && !symbol.hasProtectedAttribute();
    }

    /**
     * Are the definitions of <code>symbol</code> copied to the kernels? By default those of the
     * symbols of the current context are, as with <code>$DistributedContexts := $Context</code>. A
     * symbol whose definitions are not copied stays unevaluated on the kernels and is evaluated
     * when the results come back.
     */
    private boolean isDistributed(ISymbol symbol, IExpr distributedContexts) {
      Context context = symbol.getContext();
      if (context == Context.DUMMY) {
        // the local variables of a Module around the ParallelTable
        return true;
      }
      if (distributedContexts.isNIL() || distributedContexts == S.Automatic) {
        return context == engine.getContext();
      }
      if (distributedContexts == S.All) {
        return true;
      }
      if (distributedContexts == S.None) {
        return false;
      }
      String name = context.completeContextName();
      if (distributedContexts.isString()) {
        return distributedContexts.toString().equals(name);
      }
      return distributedContexts.isList()
          && ((IAST) distributedContexts).exists(x -> x.isString() && x.toString().equals(name));
    }

    IExpr run(int kernels) {
      final List<EvalEngine> kernelEngines = new ArrayList<EvalEngine>(kernels);
      for (int i = 0; i < kernels; i++) {
        kernelEngines.add(engine.copyParallel());
      }
      final ExecutorService pool = Executors.newFixedThreadPool(kernels, runnable -> {
        Thread thread = Config.THREAD_FACTORY.newThread(runnable);
        thread.setDaemon(true);
        return thread;
      });
      try {
        List<Future<?>> futures = new ArrayList<Future<?>>(kernels);
        for (int i = 0; i < kernels; i++) {
          final EvalEngine kernelEngine = kernelEngines.get(i);
          futures.add(pool.submit(() -> kernel(kernelEngine)));
        }
        Throwable failure = null;
        for (Future<?> future : futures) {
          try {
            future.get();
          } catch (ExecutionException ee) {
            if (failure == null) {
              failure = ee.getCause();
            }
          } catch (InterruptedException ie) {
            // TimeConstrained or an abort: the kernels are interrupted by shutdownNow() below
            stop.set(true);
            Thread.currentThread().interrupt();
            throw TimeoutException.TIMED_OUT;
          }
        }
        if (failure instanceof RuntimeException) {
          // Throw, Abort, a limit, a wrong iterator ... as Table would have thrown it
          throw (RuntimeException) failure;
        }
        if (failure instanceof Error) {
          throw (Error) failure;
        }
        if (failure != null || unevaluated) {
          return F.NIL;
        }
      } finally {
        pool.shutdownNow();
      }
      IASTAppendable result = F.ListAlloc(values.size());
      for (IExpr chunk : results) {
        result.appendArgs((IAST) chunk);
      }
      return result;
    }

    /**
     * Replace the symbols which are keys of <code>map</code> everywhere in <code>expr</code>: as
     * symbols, as heads and as the names and head tests of patterns. The replacement is one to one
     * and applies to the whole expression, so no scoping construct needs a look.
     *
     * @return <code>expr</code> itself if nothing was replaced
     */
    private static IExpr rename(IExpr expr, Map<ISymbol, ISymbol> map) {
      if (expr instanceof ISymbol) {
        ISymbol symbol = map.get(expr);
        return symbol == null ? expr : symbol;
      }
      if (expr instanceof IPattern || expr instanceof IPatternSequence) {
        IPatternObject pattern = (IPatternObject) expr;
        ISymbol symbol = pattern.getSymbol();
        ISymbol newSymbol = symbol == null ? null : (ISymbol) rename(symbol, map);
        IExpr headTest = pattern.getHeadTest();
        IExpr newHeadTest = headTest == null ? null : rename(headTest, map);
        if (newSymbol == symbol && newHeadTest == headTest) {
          return expr;
        }
        if (expr instanceof IPattern) {
          boolean isDefault = ((IPattern) expr).isPatternDefault();
          return symbol == null ? F.$b(newHeadTest, isDefault)
              : F.$p(newSymbol, newHeadTest, isDefault);
        }
        IPatternSequence sequence = (IPatternSequence) expr;
        IPatternSequence result =
            F.$ps(newSymbol, newHeadTest, sequence.isDefault(), sequence.isNullSequence());
        return sequence.isLongest() ? result.withLongest(true) : result;
      }
      if (expr.isAssociation()) {
        IAST association = (IAST) expr;
        IASTAppendable rules = F.ListAlloc(association.size());
        boolean changed = false;
        for (int i = 1; i < association.size(); i++) {
          IExpr rule = association.getRule(i);
          IExpr newRule = rename(rule, map);
          changed |= newRule != rule;
          rules.append(newRule);
        }
        return changed ? F.assoc(rules) : expr;
      }
      if (expr.isAST()) {
        IAST ast = (IAST) expr;
        IASTMutable result = F.NIL;
        for (int i = 0; i < ast.size(); i++) {
          IExpr arg = ast.get(i);
          IExpr newArg = rename(arg, map);
          if (newArg != arg) {
            if (result.isNIL()) {
              result = ast.copy();
            }
            result.set(i, newArg);
          }
        }
        return result.isPresent() ? result : expr;
      }
      return expr;
    }

    /** The work of one kernel thread: evaluate pieces until none is left. */
    private void kernel(EvalEngine kernelEngine) {
      final IdentityHashMap<ISymbol, ISymbol> forward = new IdentityHashMap<ISymbol, ISymbol>();
      final IdentityHashMap<ISymbol, ISymbol> back = new IdentityHashMap<ISymbol, ISymbol>();
      IN_KERNEL.set(Boolean.TRUE);
      EvalEngine.set(kernelEngine);
      try {
        // the symbols of this kernel, with the definitions of the user's symbols
        final String suffix = EvalEngine.uniqueName("$");
        for (ISymbol symbol : symbols) {
          ISymbol kernelSymbol = F.Dummy(symbol.getSymbolName() + suffix);
          forward.put(symbol, kernelSymbol);
          back.put(kernelSymbol, symbol);
        }
        for (Map.Entry<ISymbol, SymbolDefinition> entry : definitions.entrySet()) {
          entry.getValue().installOn(forward.get(entry.getKey()), x -> rename(x, forward),
              kernelEngine);
        }
        final IAST kernelTable = (IAST) rename(table, forward);
        final IExpr kernelVariable = variable == null ? null : forward.get(variable);

        int chunk;
        while (!stop.get() && (chunk = nextChunk.getAndIncrement()) < numberOfChunks) {
          final int from = chunk * chunkSize;
          final int to = Math.min(values.size(), from + chunkSize);
          IExpr outer;
          if (kernelVariable == null) {
            // {n}: only the number of iterations matters
            outer = F.list(F.ZZ(to - from));
          } else {
            IASTAppendable chunkValues = F.ListAlloc(to - from);
            for (int i = from; i < to; i++) {
              chunkValues.append(rename(values.get(i), forward));
            }
            outer = F.list(kernelVariable, chunkValues);
          }
          IExpr result = kernelEngine.evaluate(kernelTable.setAtCopy(2, outer));
          if (!result.isList() || result.argSize() != to - from) {
            // an inner iterator which does not determine its values
            unevaluated = true;
            stop.set(true);
            return;
          }
          results[chunk] = rename(result, back);
        }
      } catch (ThrowException te) {
        // the value is caught by a Catch() outside of the kernel
        stop.set(true);
        throw new ThrowException(rename(te.getValue(), back), rename(te.getTag(), back));
      } catch (RuntimeException | Error e) {
        stop.set(true);
        throw e;
      } finally {
        EvalEngine.remove();
        IN_KERNEL.remove();
      }
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
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
