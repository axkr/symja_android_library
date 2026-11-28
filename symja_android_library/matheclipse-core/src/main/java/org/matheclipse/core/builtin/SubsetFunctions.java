package org.matheclipse.core.builtin;

import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class SubsetFunctions {

  private static class Initializer {

    private static void init() {
      S.SubsetCases.setEvaluator(new SubsetCases());
      S.SubsetPosition.setEvaluator(new SubsetPosition());
      S.SubsetReplace.setEvaluator(new SubsetReplace());
    }
  }

  /**
   * <pre>
   * <code>SubsetCases(list, sublist -&gt; rhs)
   * </code>
   * </pre>
   * <p>
   * or
   * </p>
   * 
   * <pre>
   * <code>SubsetCases(list, sublist :&gt; rhs)
   * </code>
   * </pre>
   * 
   * <p>
   * returns a list of the right-hand-side <code>rhs</code> for the matching sublist pattern
   * expression <code>sublist</code> in <code>list</code>.
   * </p>
   * 
   * <pre>
   * <code>SubsetCases(list, sublist)
   * </code>
   * </pre>
   * 
   * <p>
   * returns a list of the matching sublist pattern expressions <code>sublist</code> in
   * <code>list</code>.
   * </p>
   * 
   * <p>
   * <strong>Note:</strong> this function doesn't support pattern sequences at the moment.
   * </p>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; SubsetCases({a,b,c,d},{x_,y_} :&gt; f(x,y))
   * {f(a,b),f(c,d)}
   *         
   * &gt;&gt; SubsetCases({a,b,c,d},{x_,y_}) 
   * {{a,b},{c,d}}
   * </code>
   * </pre>
   * 
   * <h3>Related terms</h3>
   * <p>
   * <a href="SubsetReplace.md">SubsetReplace</a>, <a href="SubsetQ.md">SubsetQ</a>
   * </p>
   */
  private static final class SubsetCases extends AbstractEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 2 && ast.argSize() <= 3 && ast.arg1().isAST()) {
        try {
          int maxNumberOfResults = Integer.MAX_VALUE;
          IAST arg1 = (IAST) ast.arg1();
          if (arg1.argSize() == 0) {
            return F.NIL;
          }
          IExpr rules = ast.arg2();
          if (ast.isAST3()) {
            IExpr arg3 = engine.evaluate(ast.arg3());
            if (arg3.isReal()) {
              maxNumberOfResults = ((IReal) arg3).toInt();
            }
          }
          IExpr pattern = subsetPattern(rules);
          if (pattern.isPresent()) {
            // a subset is found by trying the combinations, so a condition which depends on more
            // than one element of the subset is tested on each of them
            final int k = pattern.argSize();
            if (k > arg1.argSize() || maxNumberOfResults <= 0) {
              return F.CEmptyList;
            }
            final java.util.function.Function<IExpr, IExpr> rule =
                rules.isRuleAST() ? Functors.rules(rules, engine) : null;
            final IPatternMatcher matcher = rule == null ? engine.evalPatternMatcher(rules) : null;
            SubsetSearch search = new SubsetSearch(arg1, k, (candidate, order) -> {
              if (rule != null) {
                return rule.apply(candidate);
              }
              return matcher.test(candidate, engine) ? candidate : F.NIL;
            }, false, maxNumberOfResults, engine);
            search.subsets(1, 0);
            return search.results;
          }
          IASTAppendable resultList = F.ListAlloc(8);
          SubsetMatchManager subsetCounter = new SubsetMatchManager(maxNumberOfResults, resultList);
          subsetCounter.subsetCases(arg1, rules, engine);
          return resultList;
        } catch (ArithmeticException ae) {
          return Errors.printMessage(S.SubsetCases, ae, engine);
        }
      }
      return F.NIL;
    }

    /**
     * The list pattern of the second argument of <code>SubsetCases</code>: a list pattern, a list
     * pattern with a condition, or the left-hand side of a single rule. <code>F.NIL</code> for a
     * list of rules or a pattern with pattern sequences, which the subset matcher handles.
     */
    private static @AllowNIL IExpr subsetPattern(IExpr rules) {
      IExpr pattern = rules.isRuleAST() ? rules.first() : rules;
      if (pattern.isAST(S.Condition, 3)) {
        pattern = pattern.first();
      }
      if (!pattern.isList() || pattern.argSize() == 0) {
        return F.NIL;
      }
      IAST list = (IAST) pattern;
      if (!rules.isRuleAST() && list.forAll(x -> x.isRuleAST() || x.isList())) {
        // a list of rules
        return F.NIL;
      }
      if (list.exists(x -> x.isPatternSequence(false))) {
        return F.NIL;
      }
      return list;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

  }

  /**
   * <code>SubsetPosition(list, sublist)</code> - the positions of the subsets of
   * <code>list</code> which match the pattern <code>sublist</code> in any order, each in the order
   * of <code>sublist</code>; <code>SubsetPosition(list, sublist, n)</code> gives the first
   * <code>n</code> of them. With <code>Overlaps -&gt; False</code> a subset shares no position with
   * those found before.
   */
  private static final class SubsetPosition extends AbstractFunctionOptionEvaluator {
    /** The largest number of tested arrangements. */
    private static final long MAX_ARRANGEMENTS = 20_000_000L;

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, int argSize, IExpr[] option, EvalEngine engine,
        IAST originalAST) {
      // a list pattern, also with a condition: {x_, y_} /; x + y == 7
      final IExpr sublist = ast.arg2().isAST(S.Condition, 3) ? ast.arg2().first() : ast.arg2();
      if (!ast.arg1().isList() || !sublist.isList()) {
        return F.NIL;
      }
      int maxResults = Integer.MAX_VALUE;
      if (argSize == 3 && !ast.arg3().isInfinity()) {
        maxResults = ast.arg3().toIntDefault(-1);
        if (maxResults < 0) {
          // Non-negative integer or Infinity expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "innf", F.List(F.C3, ast), engine);
        }
      }
      IAST list = (IAST) ast.arg1();
      int k = sublist.argSize();
      long arrangements = 1L;
      for (int i = 0; i < k; i++) {
        arrangements *= list.argSize() - i;
        if (arrangements > MAX_ARRANGEMENTS) {
          ASTElementLimitExceeded.throwIt(arrangements);
        }
      }
      if (k > list.argSize() || maxResults == 0) {
        return F.CEmptyList;
      }
      final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
      SubsetSearch search = new SubsetSearch(list, k,
          (candidate, order) -> matcher.test(candidate, engine)
              ? F.mapRange(0, order.length, i -> F.ZZ(order[i]))
              : F.NIL,
          !option[0].isFalse(), maxResults, engine);
      search.subsets(1, 0);
      return search.results;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.Overlaps, S.True);
    }
  }

  /**
   * The search for the subsets of <code>k</code> elements of a list which match, in the
   * lexicographic order of their positions. For each subset the arrangements of its elements are
   * tried in turn; the first one for which <code>match</code> gives a result is taken.
   */
  private static final class SubsetSearch {
    final IAST list;
    final int[] subset;
    /** The result for a candidate list and the positions of its elements, or <code>F.NIL</code>. */
    final java.util.function.BiFunction<IAST, int[], IExpr> match;
    final boolean overlaps;
    final int maxResults;
    final EvalEngine engine;
    final boolean[] used;
    final IASTAppendable results = F.ListAlloc();

    SubsetSearch(IAST list, int k, java.util.function.BiFunction<IAST, int[], IExpr> match,
        boolean overlaps, int maxResults, EvalEngine engine) {
      this.list = list;
      this.subset = new int[k];
      this.match = match;
      this.overlaps = overlaps;
      this.maxResults = maxResults;
      this.engine = engine;
      this.used = new boolean[list.size()];
    }

    void subsets(int from, int count) {
      if (count == subset.length) {
        engine.checkTimeLimit();
        if (!overlaps && hasUsedPosition()) {
          return;
        }
        int[] order = new int[count];
        IExpr found = arrangement(order, new boolean[count], 0);
        if (found.isPresent()) {
          results.append(found);
          for (int position : order) {
            used[position] = true;
          }
        }
        return;
      }
      for (int i = from; i < list.size() && results.argSize() < maxResults; i++) {
        if (!overlaps && used[i]) {
          // a position of a subset found before; no subset with it can be taken
          continue;
        }
        subset[count] = i;
        subsets(i + 1, count + 1);
      }
    }

    private boolean hasUsedPosition() {
      for (int position : subset) {
        if (used[position]) {
          return true;
        }
      }
      return false;
    }

    /**
     * The result of the first arrangement of the subset which matches, or <code>F.NIL</code>. The
     * positions of that arrangement are left in <code>order</code>.
     */
    private @AllowNIL IExpr arrangement(int[] order, boolean[] taken, int count) {
      if (count == order.length) {
        IASTAppendable candidate = F.ListAlloc(count);
        for (int position : order) {
          candidate.append(list.get(position));
        }
        return match.apply(candidate, order);
      }
      for (int i = 0; i < order.length; i++) {
        if (!taken[i]) {
          taken[i] = true;
          order[count] = subset[i];
          IExpr found = arrangement(order, taken, count + 1);
          if (found.isPresent()) {
            return found;
          }
          taken[i] = false;
        }
      }
      return F.NIL;
    }
  }

  private static final class SubsetMatchManager {
    int maxNumberOfResult;
    IASTAppendable casesList;

    public SubsetMatchManager(int maxNumberOfResult, IASTAppendable casesList) {
      if (maxNumberOfResult > 0) {
        this.maxNumberOfResult = maxNumberOfResult;
      } else {
        this.maxNumberOfResult = Integer.MAX_VALUE;
      }
      this.casesList = casesList;
    }

    private IExpr subsetCases(IAST arg1, IExpr rules, final EvalEngine engine) {
      if (rules.forAll(x -> x.isRuleAST() || x.isList())) {
        return applyRules((IAST) rules, arg1, true, engine);
      }

      if (rules.isRuleAST()) {
        return applyRule((IAST) rules, arg1, engine);
      }
      if (rules.isList()) {
        return applyRule(F.Rule(rules, IPatternMatcher.DUMMY_SUBSET_CASES), arg1, engine);
      }
      throw new ArgumentTypeException(
          "rule expressions (x->y) expected instead of " + rules.toString());
    }

    private IExpr subsetReplace(IAST arg1, IExpr rules, final EvalEngine engine) {
      if (rules.isList()) {
        return applyRules((IAST) rules, arg1, false, engine);
      }

      if (rules.isRuleAST()) {
        return applyRule((IAST) rules, arg1, engine);
      }
      throw new ArgumentTypeException(
          "rule expressions (x->y) expected instead of " + rules.toString());
    }

    /**
     * Apply the rules of <code>rulesList</code> one after the other, each to the result of the
     * previous one, until <code>maxNumberOfResult</code> results are collected.
     *
     * @param patternLists if <code>true</code> a non-empty list in <code>rulesList</code> is a
     *        pattern whose matches are collected; otherwise only rules are allowed
     */
    private IAST applyRules(IAST rulesList, IAST arg1, boolean patternLists,
        final EvalEngine engine) {
      IAST result = arg1;
      for (IExpr rule : rulesList) {
        IExpr temp;
        if (rule.isRuleAST()) {
          temp = applyRule((IAST) rule, result, engine);
        } else if (patternLists && rule.isList() && rule.size() > 1) {
          temp = applyRule(F.Rule(rule, IPatternMatcher.DUMMY_SUBSET_CASES), result, engine);
        } else {
          throw new ArgumentTypeException(
              "rule expressions (x->y) expected instead of " + rule.toString());
        }
        if (temp.isAST()) {
          result = (IAST) temp;
        }
        if (maxNumberOfResult == 0) {
          return result;
        }
      }
      return result;
    }

    private @AllowNIL IExpr applyRule(IAST rule, IAST result, final EvalEngine engine) {
      if (!rule.arg1().isAST()) {
        return F.NIL;
      }
      Functors.SubsetPatternFunctor function = Functors.subsetRules(rule, engine);
      function.setMaxMatches(maxNumberOfResult);
      IExpr temp = function.apply(result);
      maxNumberOfResult = function.getMaxMatches();
      if (temp.isPresent()) {
        result = (IAST) temp;

        if (casesList.isPresent()) {
          // for SubsetCases
          IExpr[] substitutedMatches = function.getSubstitutedMatches();
          if (substitutedMatches != null) {
            for (int i = 0; i < substitutedMatches.length; i++) {
              if (substitutedMatches[i] != null) {
                casesList.append(substitutedMatches[i]);
              }
            }
          }
        }

        if (maxNumberOfResult == 0) {
          return result;
        }
      }
      return engine.evaluate(result);
    }

  }


  /**
   * <pre>
   * <code>SubsetReplace(list, sublist -&gt; rhs)
   * </code>
   * </pre>
   * <p>
   * or
   * </p>
   * 
   * <pre>
   * <code>SubsetReplace(list, sublist :&gt; rhs)
   * </code>
   * </pre>
   * 
   * <p>
   * replaces the sublist pattern expression <code>sublist</code> in <code>list</code> with the
   * right-hand-side <code>rhs</code>.
   * </p>
   * 
   * <p>
   * <strong>Note:</strong> this function doesn't support pattern sequences at the moment.
   * </p>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; SubsetReplace({a,b,c,d},{x_,y_} :&gt; f(x,y))
   * {f(a,b),f(c,d)} 
   * </code>
   * </pre>
   * 
   * <h3>Related terms</h3>
   * <p>
   * <a href="SubsetCases.md">SubsetCases</a>, <a href="SubsetQ.md">SubsetQ</a>
   * </p>
   */
  private static final class SubsetReplace extends AbstractEvaluator {


    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 2 && ast.argSize() <= 3 && ast.arg1().isAST()) {
        if (!ast.arg1().isList()) {
          // List expected at position `1` in `2`.
          return Errors.printMessage(S.SubsetReplace, "list", F.List(F.C1, ast));
        }
        try {
          int maxNumberOfResults = Integer.MAX_VALUE;
          IAST arg1 = (IAST) ast.arg1();
          IExpr rules = ast.arg2();
          if (ast.isAST3()) {
            IExpr arg3 = engine.evaluate(ast.arg3());
            if (arg3.isZero()) {
              return arg1;
            }
            if (arg3.isReal()) {
              maxNumberOfResults = ((IReal) arg3).toInt();
            }
          }
          SubsetMatchManager subsetCounter = new SubsetMatchManager(maxNumberOfResults, F.NIL);
          return subsetCounter.subsetReplace(arg1, rules, engine);
        } catch (ArithmeticException ae) {
          return Errors.printMessage(S.SubsetReplace, ae, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_1;
    }

  }

  public static void initialize() {
    Initializer.init();
  }

  private SubsetFunctions() {}
}
