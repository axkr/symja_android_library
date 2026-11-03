package org.matheclipse.core.sympy.core.strategies;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import com.google.common.collect.Iterators;

/**
 * Port of <code>sympy.strategies.core</code>, the strategies which know nothing about the shape of
 * an expression.
 *
 * <p>
 * A rule is a <code>Function&lt;IExpr, IExpr&gt;</code> which transforms one expression; a strategy
 * makes a new rule from rules. This separates a mathematical transformation from the algorithmic
 * details of applying it.
 *
 * <h3>The "no effect" contract</h3>
 *
 * <p>
 * In sympy a rule without effect returns its argument, and in Symja it returns {@link F#NIL}.
 * Strategies accept both from the rules they are given. What they return is always a total rule,
 * which answers its argument and never {@link F#NIL} if there was no effect. That is the difference
 * to a chain of <code>temp = f(x); if (temp.isPresent())</code> calls: {@link #nullSafe(Function)}
 * is what such a chain writes out by hand, and it's needed here only to make a single rule total.
 *
 * <p>
 * The composed rules are stateless, except for {@link #memoize(Function)} and the rules made from
 * it, so they can be held in constants. The engine is taken from the current thread when a rule is
 * applied.
 */
public final class Core {

  private Core() {}

  /**
   * The rule without effect.
   *
   * @return
   */
  public static Function<IExpr, IExpr> identity() {
    return Function.identity();
  }

  /**
   * Apply a rule repeatedly until it has no effect.
   *
   * <p>
   * Different from sympy, which loops forever if a rule cycles between two expressions:
   * {@link EvalEngine#checkTimeLimit()} is checked in each round and the number of rounds is
   * limited by <code>$IterationLimit</code>. Comparing with <code>equals()</code> is what stops the
   * loop, and rounds with a {@link F#NIL} result or the same object don't pay for it. This is the
   * <code>do..while (temp.isPresent())</code> loop of the Symja evaluators, plus the
   * <code>equals()</code> test which such a loop is missing if its rule fires without changing
   * anything.
   *
   * <p>
   * The test recurses, so a rule which returns a new object for a very deep expression raises
   * <code>$RecursionLimit</code> here, even if the traversal itself is iterative. A rule which
   * answers {@link F#NIL} or its argument never gets there.
   *
   * @param rule
   * @return
   * @throws IterationLimitExceeded if the limit is exceeded
   */
  public static Function<IExpr, IExpr> exhaust(Function<IExpr, IExpr> rule) {
    return expr -> {
      final EvalEngine engine = EvalEngine.get();
      final int limit = engine.getIterationLimit();
      long iterations = 0;
      IExpr old = expr;
      while (true) {
        final IExpr next = rule.apply(old);
        if (next.isNIL() || next == old || next.equals(old)) {
          return old;
        }
        old = next;
        if (limit >= 0 && ++iterations > limit) {
          IterationLimitExceeded.throwIt(iterations, expr);
        }
        engine.checkTimeLimit();
      }
    };
  }

  /**
   * Memoized version of a rule. The cache is never trimmed, see {@link #memoize(Function, int)}.
   *
   * <p>
   * The returned rule holds its cache, which isn't thread safe. Make one for each call of an
   * algorithm and don't store it in a constant: what a rule returns usually depends on the
   * assumptions, the numeric mode and the other state of the engine.
   *
   * <p>
   * The keys are compared structurally, and a key's <code>hashCode()</code> is stored in the
   * expression. So a repeated subexpression hits even if it's another object, and the first lookup
   * pays once for the subtree.
   *
   * @param rule
   * @return
   */
  public static Function<IExpr, IExpr> memoize(Function<IExpr, IExpr> rule) {
    return memoized(rule, new HashMap<IExpr, IExpr>());
  }

  /**
   * Memoized version of a rule, which keeps the <code>maxSize</code> entries used last. The sympy
   * function warns that its cache grows infinitely; this variant is the bound it recommends
   * <code>functools.lru_cache</code> for.
   *
   * @param rule
   * @param maxSize
   * @return
   * @see #memoize(Function)
   */
  public static Function<IExpr, IExpr> memoize(Function<IExpr, IExpr> rule, int maxSize) {
    return memoized(rule, new LinkedHashMap<IExpr, IExpr>(16, 0.75f, true) {
      private static final long serialVersionUID = 1L;

      @Override
      protected boolean removeEldestEntry(Map.Entry<IExpr, IExpr> eldest) {
        return size() > maxSize;
      }
    });
  }

  private static Function<IExpr, IExpr> memoized(Function<IExpr, IExpr> rule,
      Map<IExpr, IExpr> cache) {
    return expr -> {
      IExpr result = cache.get(expr);
      if (result == null) {
        result = rule.apply(expr).orElse(expr);
        cache.put(expr, result);
      }
      return result;
    };
  }

  /**
   * Only apply rule if condition is true.
   *
   * @param cond
   * @param rule
   * @return
   */
  public static Function<IExpr, IExpr> condition(Predicate<IExpr> cond,
      Function<IExpr, IExpr> rule) {
    return expr -> cond.test(expr) ? rule.apply(expr).orElse(expr) : expr;
  }

  /**
   * Compose a sequence of rules so that they apply to the expression sequentially.
   *
   * @param rules
   * @return
   */
  @SafeVarargs
  public static Function<IExpr, IExpr> chain(Function<IExpr, IExpr>... rules) {
    return expr -> {
      IExpr result = expr;
      for (Function<IExpr, IExpr> rule : rules) {
        result = rule.apply(result).orElse(result);
      }
      return result;
    };
  }

  /**
   * Print out before and after expressions each time rule is used. sympy uses the name of the rule
   * function; a Java lambda has none.
   *
   * @param rule
   * @param name the name which is printed for the rule
   * @param out the destination, or <code>null</code> for the output stream of the engine
   * @return
   */
  public static Function<IExpr, IExpr> debug(Function<IExpr, IExpr> rule, String name,
      Appendable out) {
    return expr -> {
      final IExpr result = rule.apply(expr).orElse(expr);
      if (!result.equals(expr)) {
        try {
          final Appendable writer = out == null ? EvalEngine.get().getOutPrintStream() : out;
          writer.append("Rule: " + name + "\nIn:   " + expr + "\nOut:  " + result + "\n\n");
        } catch (IOException ioex) {
          throw new UncheckedIOException(ioex);
        }
      }
      return result;
    };
  }

  /**
   * Return the original expression if rule returns {@link F#NIL}.
   *
   * @param rule
   * @return
   */
  public static Function<IExpr, IExpr> nullSafe(Function<IExpr, IExpr> rule) {
    return expr -> rule.apply(expr).orElse(expr);
  }

  /**
   * Return the original expression if rule throws an exception of the given type.
   *
   * <p>
   * The type should be a narrow one: time limits and the control flow of the evaluator are
   * exceptions too, and a class like <code>RuntimeException</code> would turn them into "no effect".
   * <code>StackOverflowError.class</code> is allowed.
   *
   * @param rule
   * @param exception
   * @return
   */
  public static Function<IExpr, IExpr> tryit(Function<IExpr, IExpr> rule,
      Class<? extends Throwable> exception) {
    return expr -> {
      try {
        return rule.apply(expr).orElse(expr);
      } catch (RuntimeException | Error ex) {
        if (exception.isInstance(ex)) {
          return expr;
        }
        throw ex;
      }
    };
  }

  /**
   * Try each of the rules until one works. Then stop.
   *
   * @param rules
   * @return
   */
  @SafeVarargs
  public static Function<IExpr, IExpr> doOne(Function<IExpr, IExpr>... rules) {
    return expr -> {
      for (Function<IExpr, IExpr> rule : rules) {
        final IExpr result = rule.apply(expr);
        if (result.isPresent() && result != expr && !result.equals(expr)) {
          return result;
        }
      }
      return expr;
    };
  }

  /**
   * Select a rule based on the result of <code>key</code> called on the expression, sympy's
   * <code>switch</code>. With the head as key this is the dispatch of
   * <code>evaluate(IAST, EvalEngine)</code> in a builtin.
   *
   * @param key
   * @param ruledict
   * @return
   */
  public static Function<IExpr, IExpr> switchRule(Function<IExpr, IExpr> key,
      Map<? extends IExpr, Function<IExpr, IExpr>> ruledict) {
    return expr -> {
      final Function<IExpr, IExpr> rule = ruledict.get(key.apply(expr));
      return rule == null ? expr : rule.apply(expr).orElse(expr);
    };
  }

  /**
   * Select the result of the rules which is first in the canonical order of expressions.
   *
   * @param rules
   * @return
   * @see #minimizeBy(Function, Function...)
   */
  @SafeVarargs
  public static Function<IExpr, IExpr> minimize(Function<IExpr, IExpr>... rules) {
    return minimizeBy(Function.identity(), rules);
  }

  /**
   * Select the result of the rules which minimizes objective. All rules are run. sympy's name is
   * <code>minimize(*rules, objective=...)</code>; in Java the varargs have to be the last parameter.
   *
   * <p>
   * The objective is called exactly once for each candidate. Hand-written comparisons like
   * <code>measure.apply(a) &lt; measure.apply(b)</code> in a sequence compute the measure of the
   * current best again in each comparison, and a measure is a full walk of the expression. If there
   * is a tie, the first rule wins, as in Python's <code>min</code>.
   *
   * @param <C> the type of the score
   * @param objective
   * @param rules at least one rule
   * @return
   */
  @SafeVarargs
  public static <C extends Comparable<? super C>> Function<IExpr, IExpr> minimizeBy(
      Function<? super IExpr, C> objective, Function<IExpr, IExpr>... rules) {
    if (rules.length == 0) {
      throw new IllegalArgumentException("minimize() needs at least one rule");
    }
    return expr -> minimum(
        Iterators.transform(Arrays.asList(rules).iterator(), rule -> rule.apply(expr).orElse(expr)),
        objective);
  }

  /**
   * The candidate with the lowest score, the first one in a tie. The objective is called once for
   * each candidate.
   *
   * @param <C> the type of the score
   * @param candidates at least one candidate
   * @param objective
   * @return
   */
  static <C extends Comparable<? super C>> IExpr minimum(Iterator<IExpr> candidates,
      Function<? super IExpr, C> objective) {
    IExpr best = candidates.next();
    C bestScore = objective.apply(best);
    while (candidates.hasNext()) {
      final IExpr candidate = candidates.next();
      final C score = objective.apply(candidate);
      if (score.compareTo(bestScore) < 0) {
        best = candidate;
        bestScore = score;
      }
    }
    return best;
  }
}
