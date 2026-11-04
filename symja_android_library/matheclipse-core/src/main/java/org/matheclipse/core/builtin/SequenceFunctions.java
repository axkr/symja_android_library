package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class SequenceFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AllSameBy.setEvaluator(new AllSameBy());
      S.ComapApply.setEvaluator(new ComapApply());
      S.FindRepeat.setEvaluator(new FindRepeat());
      S.FindTransientRepeat.setEvaluator(new FindTransientRepeat());
      S.PositionLargest.setEvaluator(new PositionExtreme(true));
      S.PositionSmallest.setEvaluator(new PositionExtreme(false));
      S.SequenceCases.setEvaluator(new SequenceCases());
      S.SequenceCount.setEvaluator(new SequenceCount());
      S.SequencePosition.setEvaluator(new SequencePosition());
      S.SequenceReplace.setEvaluator(new SequenceReplace());
      S.SequenceSplit.setEvaluator(new SequenceSplit());
      S.Subsequences.setEvaluator(new Subsequences());
      S.SymmetricDifference.setEvaluator(new SymmetricDifference());
      S.Upsample.setEvaluator(new Upsample());
    }
  }

  /** Anything that can match more than one element of a sequence. */
  private static final Predicate<IExpr> SPANS_ELEMENTS = x -> x.isPatternSequence(false)
      || x.isPatternSequence(true) || x == S.Repeated || x == S.RepeatedNull
      || x == S.PatternSequence || x == S.OptionsPattern || x == S.Sequence;

  /**
   * The longest sequence a pattern, or the left-hand side of a rule, can match, or
   * {@link Integer#MAX_VALUE} when it has no bound.
   *
   * <p>
   * A pattern is bounded when it is a list, possibly under {@code HoldPattern}, {@code Condition},
   * {@code PatternTest} or a named {@code s:{...}}, none of whose elements can match more than one
   * element. An element that can match none, such as {@code a_:0}, only makes the match shorter, so
   * it does not matter for the bound.
   *
   * <p>
   * The scans below try every sequence that starts at a position; a sequence longer than this can
   * never match, so it is not tried. Without the bound every position copied every tail of the
   * list, which is cubic: the WLJS notebook loader, one rule of 8 and one of 6 elements over the
   * character codes of a whole file, never finished.
   */
  private static int maxSequenceLength(IExpr pattern) {
    IExpr lhs = pattern.isRuleAST() ? pattern.first() : pattern;
    while (true) {
      if (lhs.isAST(S.HoldPattern, 2) || lhs.isAST(S.Condition, 3)
          || lhs.isAST(S.PatternTest, 3)) {
        lhs = lhs.first();
      } else if (lhs.isAST(S.Pattern, 3)) {
        lhs = lhs.second();
      } else {
        break;
      }
    }
    if (!lhs.isList() || !lhs.isFree(SPANS_ELEMENTS, true)) {
      return Integer.MAX_VALUE;
    }
    return lhs.argSize();
  }

  /** The longest sequence any of the patterns or rules can match; see {@link #maxSequenceLength}. */
  private static int maxSequenceLengthOfAll(IAST patterns) {
    int max = 0;
    for (int i = 1; i < patterns.size(); i++) {
      int length = maxSequenceLength(patterns.get(i));
      if (length == Integer.MAX_VALUE) {
        return Integer.MAX_VALUE;
      }
      max = Math.max(max, length);
    }
    return max;
  }

  /**
   * The last end index (exclusive) worth trying for a sequence starting at {@code i}: the end of
   * the list, or sooner when the patterns cannot match anything longer than {@code maxLength}.
   */
  private static int scanLimit(int size, int i, int maxLength) {
    return maxLength == Integer.MAX_VALUE ? size : (int) Math.min(size, (long) i + maxLength);
  }



  /**
   * <code>f(list, pattern)</code> with the option <code>Overlaps</code>: checks the option and the
   * arguments, then calls {@link #apply} with the evaluated list and pattern.
   */
  private abstract static class AbstractSequenceOverlaps extends AbstractFunctionOptionEvaluator {

    /**
     * Evaluate for a valid call.
     *
     * @param ast the call
     * @param list the evaluated first argument
     * @param pattern the second argument, evaluated as a pattern
     * @param overlapsOption <code>True</code>, <code>False</code> or <code>All</code>
     */
    protected abstract IExpr apply(IAST ast, int argSize, IAST list, IExpr pattern,
        IExpr overlapsOption, EvalEngine engine);

    @Override
    public IExpr evaluate(final IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      try {
        if (ast.arg2().isAST(S.Rule, 3, S.Overlaps)) {
          // List or pattern matching a list expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "lstpat", F.List(F.C2, ast), engine);
        }
        if (!(options[0] == S.All || options[0].isFalse() || options[0].isTrue())) {
          // Value of option `1` must be True, False or All.
          return Errors.printMessage(ast.topHead(), "ovls", F.List(F.Rule(S.Overlaps, options[0])),
              engine);
        }
        final IExpr overlapsOption = options[0];

        final IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          final IExpr arg2 = engine.evalPattern(ast.arg2());
          return apply(ast, argSize, (IAST) arg1, arg2, overlapsOption, engine);
        }
        // List expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "list", F.List(F.C1, ast), engine);
      } catch (final ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.Overlaps, S.False);
    }
  }

  private static final class SequenceCases extends AbstractSequenceOverlaps {

    @Override
    protected IExpr apply(IAST ast, int argSize, IAST list, IExpr pattern, IExpr overlapsOption,
        EvalEngine engine) {
      return sequenceCases(list, pattern, overlapsOption, engine);
    }

    public static IAST sequenceCases(final IAST ast, final IExpr pattern, IExpr overlapsOption,
        EvalEngine engine) {
      IASTAppendable resultAST = F.ListAlloc();
      if (pattern.isRuleAST()) {
        return sequenceCasesWithReplacement(ast, (IAST) pattern, overlapsOption, resultAST, engine);
      }
      return sequenceCasesWithoutReplacement(ast, pattern, overlapsOption, resultAST, engine);
    }

    private static IAST sequenceCasesWithoutReplacement(final IAST ast, final IExpr pattern,
        IExpr overlapsOption, IASTAppendable resultAST, EvalEngine engine) {
      final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
      // CPD-OFF matcher and rule-function scan loops kept parallel
      final int maxLength = maxSequenceLength(pattern);
      int i = 1;
      while (i < ast.size()) {
        if (overlapsOption == S.All) {
          IASTAppendable allResults = F.ListAlloc();
          for (int k = i + 1; k <= scanLimit(ast.size(), i, maxLength); k++) {
            // every length is tried: a miss at one length says nothing about a longer one, so
            // {a, b} still has to be tried after {a} failed
            IASTAppendable subSequence = ast.copyFrom(i, k);
            // CPD-ON
            if (matcher.test(subSequence)) {
              allResults.append(subSequence);
            }
          }
          if (allResults.argSize() > 0) {
            allResults.reverse(resultAST);
          }
        } else {
          for (int j = i + 1; j <= scanLimit(ast.size(), i, maxLength); j++) {
            IExpr subResult = F.NIL;
            for (int k = j; k <= scanLimit(ast.size(), i, maxLength); k++) {
              // if (i >= k) {
              // break;
              // }
              // TODO optimize by classifying pattern matchers
              // use greedy search because of possible pattern sequences
              IASTAppendable subSequence = ast.copyFrom(i, k);
              if (matcher.test(subSequence)) {
                subResult = subSequence;
              } else {
                break;
              }
            }
            if (subResult.isPresent()) {
              resultAST.append(subResult);
              if (overlapsOption.isFalse()) {
                i += subResult.argSize() - 1;
                break;
              }
              if (overlapsOption.isTrue()) {
                break;
              }
            }
          }
        }
        i++;
      }
      return resultAST;
    }

    private static IAST sequenceCasesWithReplacement(final IAST ast, final IAST patternRule,
        IExpr overlapsOption, IASTAppendable resultAST, EvalEngine engine) {
      Function<IExpr, IExpr> function = Functors.rules(patternRule, engine);
      // CPD-OFF matcher and rule-function scan loops kept parallel
      final int maxLength = maxSequenceLength(patternRule);
      int i = 1;
      while (i < ast.size()) {
        if (overlapsOption == S.All) {
          IASTAppendable allResults = F.ListAlloc();
          for (int k = i + 1; k <= scanLimit(ast.size(), i, maxLength); k++) {
            // every length is tried: a miss at one length says nothing about a longer one, so
            // {a, b} still has to be tried after {a} failed
            IASTAppendable subSequence = ast.copyFrom(i, k);
            // CPD-ON
            IExpr temp = function.apply(subSequence);
            if (temp.isPresent()) {
              allResults.append(temp);
            }
          }
          if (allResults.argSize() > 0) {
            allResults.reverse(resultAST);
          }
        } else {
          for (int j = i + 1; j <= scanLimit(ast.size(), i, maxLength); j++) {
            IExpr subResult = F.NIL;
            IExpr result = F.NIL;
            for (int k = j; k <= scanLimit(ast.size(), i, maxLength); k++) {
              // if (i >= k) {
              // break;
              // }
              // TODO optimize by classifying pattern matchers
              // use greedy search because of possible pattern sequences
              IASTAppendable subSequence = ast.copyFrom(i, k);
              IExpr temp = function.apply(subSequence);
              if (temp.isPresent()) {
                subResult = subSequence;
                result = temp;
              } else {
                break;
              }
            }
            if (subResult.isPresent()) {
              resultAST.append(result);
              if (overlapsOption.isFalse()) {
                i += subResult.argSize() - 1;
                if (i <= 0) {
                  return F.NIL;
                }
                break;
              }
              if (overlapsOption.isTrue()) {
                break;
              }
            }
          }
        }
        i++;
      }
      return resultAST;
    }
  }

  private static final class SequenceCount extends AbstractSequenceOverlaps {

    @Override
    protected IExpr apply(IAST ast, int argSize, IAST list, IExpr pattern, IExpr overlapsOption,
        EvalEngine engine) {
      return sequenceCount(list, pattern, overlapsOption, engine);
    }

    public static IExpr sequenceCount(final IAST ast, final IExpr pattern, IExpr overlapsOption,
        EvalEngine engine) {
      int count = 0;
      final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
      final int maxLength = maxSequenceLength(pattern);
      int i = 1;

      while (i < ast.size()) {
        // Greedy match: iterate k downwards to match the longest possible sequences first
        for (int k = scanLimit(ast.size(), i, maxLength); k >= i + 1; k--) {
          IASTAppendable subSequence = ast.subList(i, k);
          if (matcher.test(subSequence)) {
            count++;

            if (overlapsOption != S.All) {
              if (overlapsOption.isFalse()) {
                // If no overlaps allowed, jump index to the end of the matching sequence
                i = k - 1;
              }
              break;
            }
          }
        }
        i++;
      }
      return F.ZZ(count);
    }
  }

  private static final class SequencePosition extends AbstractSequenceOverlaps {

    @Override
    protected IExpr apply(IAST ast, int argSize, IAST list, IExpr pattern, IExpr overlapsOption,
        EvalEngine engine) {
      int maxMatches = Integer.MAX_VALUE;
      if (argSize == 3) {
        final IExpr arg3 = engine.evaluate(ast.arg3());
        if (!arg3.isInfinity()) {
          int maxN = arg3.toMachineInt();
          if (maxN < 0 || !arg3.isInteger()) {
            // Non-negative integer or Infinity expected at position `3` in `ast`.
            return Errors.printMessage(ast.topHead(), "innf", F.List(F.C3, ast), engine);
          }
          maxMatches = maxN;
        }
      }
      return sequencePosition(list, pattern, overlapsOption, maxMatches, engine);
    }

    public static IAST sequencePosition(final IAST ast, final IExpr pattern, IExpr overlapsOption,
        int maxMatches, EvalEngine engine) {
      IASTAppendable resultAST = F.ListAlloc();
      if (maxMatches <= 0) {
        return resultAST;
      }

      final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
      final int maxLength = maxSequenceLength(pattern);
      int i = 1;
      while (i < ast.size()) {
        for (int k = scanLimit(ast.size(), i, maxLength); k >= i + 1; k--) {
          IASTAppendable subSequence = ast.copyFrom(i, k);
          if (matcher.test(subSequence)) {
            resultAST.append(F.List(F.ZZ(i), F.ZZ(k - 1)));
            if (resultAST.argSize() >= maxMatches) {
              return resultAST;
            }
            if (overlapsOption != S.All) {
              if (overlapsOption.isFalse()) {
                i = k - 1;
              }
              break;
            }
          }
        }
        i++;
      }
      return resultAST;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.Overlaps, S.True);
    }


  }


  /** Appends the elements <code>[from, to)</code> between two matched subsequences. */
  @FunctionalInterface
  private interface GapAppender {
    void append(IASTAppendable result, int from, int to);
  }

  /**
   * Scan <code>list</code> from left to right for the longest subsequences which a rule of
   * <code>functions</code> rewrites or a pattern of <code>matchers</code> matches, and append the
   * gaps between them and the rewritten subsequences to <code>resultAST</code>.
   *
   * @param functions the rules as functions; an entry is used where <code>matchers</code> has none
   * @param matchers the patterns, or <code>null</code> if there are rules only
   * @param maxLength the longest subsequence a pattern can match
   * @param maxMatches the number of matches after which the rest of the list is a gap
   * @param gap appends a gap to the result
   */
  @SuppressWarnings("unchecked")
  private static IAST scanSubsequences(final IAST list, Object[] functions,
      IPatternMatcher[] matchers, int maxLength, int maxMatches, IASTAppendable resultAST,
      GapAppender gap) {
    int i = 1;
    int lastI = i;
    while (i < list.size()) {
      // i moves forward inside this loop after a match and the scan goes on from there, so the
      // bound is read again on every step rather than fixed for the i the loop started with
      for (int j = i + 1; j <= scanLimit(list.size(), i, maxLength); j++) {
        IExpr subResult = F.NIL;
        int lastK = -1;

        boolean matched = false;
        for (int k = j; k <= scanLimit(list.size(), i, maxLength); k++) {
          if (i >= k) {
            break;
          }
          // greedy search
          IASTAppendable subSequence = list.copyFrom(i, k);
          for (int l = 0; l < functions.length; l++) {
            if (matchers != null && matchers[l] != null) {
              if (matchers[l].test(subSequence)) {
                subResult = F.NIL;
                lastK = k;
                matched = true;
                break;
              }
            } else {
              IExpr temp = ((Function<IExpr, IExpr>) functions[l]).apply(subSequence);
              if (temp.isPresent()) {
                subResult = temp;
                lastK = k;
                matched = true;
                break;
              }
            }
          }
          if (!matched) {
            break;
          }
        }
        if (lastK > i) {
          if (lastI < i) {
            gap.append(resultAST, lastI, i);
          }
          if (subResult.isPresent()) {
            resultAST.append(subResult);
          }
          i = lastK;
          lastI = i;
          if (--maxMatches <= 0) {
            if (lastI < list.size()) {
              gap.append(resultAST, lastI, list.size());
            }
            return resultAST;
          }
        }

      }
      i++;
    }
    if (lastI < list.size()) {
      gap.append(resultAST, lastI, list.size());
    }
    return resultAST;
  }

  private static final class SequenceReplace extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      try {
        final IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          int maxReplacements = Integer.MAX_VALUE;
          if (ast.isAST3()) {
            final IExpr arg3 = engine.evaluate(ast.arg3());
            if (!arg3.isInfinity()) {
              int maxN = arg3.toMachineInt();
              if (maxN < 0) {
                // Non-negative integer or Infinity expected at position `1` in `2`.
                return Errors.printMessage(ast.topHead(), "innf", F.List(F.C3, ast), engine);
              }
              maxReplacements = maxN;
            }
          }
          final IExpr arg2 = engine.evalPattern(ast.arg2());
          IASTAppendable resultAST = F.ListAlloc();
          IAST list = arg2.isListOfRules() ? (IAST) arg2 : F.List(arg2);
          return sequenceReplace((IAST) arg1, list, maxReplacements, resultAST, engine);
        }
        // List expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "list", F.List(F.C1, ast), engine);
      } catch (final ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_1;
    }

    private static IAST sequenceReplace(final IAST list, final IAST listOfRules,
        int maxReplacements, IASTAppendable resultAST, EvalEngine engine) {
      if (maxReplacements <= 0) {
        return F.List(list);
      }
      Object[] functions = new Object[listOfRules.argSize()];
      for (int i = 1; i < listOfRules.size(); i++) {
        IExpr pattern = listOfRules.get(i);
        if (pattern.isRuleAST()) {
          functions[i - 1] = Functors.rules(pattern, engine);
        } else {
          // (`1`) is neither a list of replacement rules nor a valid dispatch table and cannot be
          // used for replacing.
          return Errors.printMessage(S.SequenceReplace, "reps", F.List(listOfRules), engine);
        }
      }
      return scanSubsequences(list, functions, null, maxSequenceLengthOfAll(listOfRules),
          maxReplacements, resultAST, (result, from, to) -> result.appendArgs(from, to,
              n -> list.get(n)));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

  }

  private static final class SequenceSplit extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      try {
        final IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          int maxSplits = Integer.MAX_VALUE;
          if (ast.isAST3()) {
            final IExpr arg3 = engine.evaluate(ast.arg3());
            if (!arg3.isInfinity()) {
              int maxParts = arg3.toMachineInt();
              if (maxParts <= 0) {
                // Positive integer or Infinity expected at position `1` in `2`.
                return Errors.printMessage(ast.topHead(), "ipnf", F.List(F.C3, ast), engine);
              }
              maxSplits = maxParts - 1;
            }
          }
          final IExpr arg2 = engine.evalPattern(ast.arg2());
          IASTAppendable resultAST = F.ListAlloc();
          IAST list = arg2.isListOfLists() ? (IAST) arg2 : F.List(arg2);
          return sequenceSplitList((IAST) arg1, list, maxSplits, resultAST, engine);
        }
        // List expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "list", F.List(F.C1, ast), engine);
      } catch (final ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    private static IAST sequenceSplitList(final IAST list, final IAST listOfPattern, int maxSplits,
        IASTAppendable resultAST, EvalEngine engine) {
      if (maxSplits <= 0) {
        return F.List(list);
      }
      Object[] functions = new Object[listOfPattern.argSize()];
      IPatternMatcher[] matchers = new IPatternMatcher[listOfPattern.argSize()];
      for (int i = 1; i < listOfPattern.size(); i++) {
        IExpr pattern = listOfPattern.get(i);
        if (pattern.isRuleAST()) {
          functions[i - 1] = Functors.rules(pattern, engine);
        } else {
          matchers[i - 1] = engine.evalPatternMatcher(pattern);
        }
      }
      return scanSubsequences(list, functions, matchers, maxSequenceLengthOfAll(listOfPattern),
          maxSplits, resultAST, (result, from, to) -> result.append(list.copyFrom(from, to)));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

  }

  private static class Subsequences extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() <= 3) {
        IExpr arg1 = ast.arg1();
        if (arg1.isAST()) {
          IAST list = (IAST) arg1;
          int size = list.argSize();
          int min = 0;
          int max = size;
          int step = 1;

          int limit = Integer.MAX_VALUE;
          if (ast.isAST3() && ast.arg3() != S.All) {
            limit = ast.arg3().toIntDefault();
            if (limit < 0) {
              // Non-negative machine-sized integer expected at position `2` in `1`.
              return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.C3), engine);
            }
          }

          if (ast.argSize() >= 2) {
            IExpr arg2 = ast.arg2();
            if (arg2.isList()) {
              IAST list2 = (IAST) arg2;
              if (list2.argSize() == 1) {
                int val = list2.arg1().toMachineInt();
                if (val < 0)
                  return F.NIL;
                min = val;
                max = val;
              } else if (list2.argSize() == 2) {
                int val1 = list2.arg1().toMachineInt();
                int val2 = list2.arg2().toMachineInt();
                if (val1 < 0 || val2 < 0)
                  return F.NIL;
                min = val1;
                max = val2;
              } else if (list2.argSize() == 3) {
                int val1 = list2.arg1().toMachineInt();
                int val2 = list2.arg2().toMachineInt();
                int val3 = list2.arg3().toMachineInt();
                if (val1 < 0 || val2 < 0 || val3 < 1)
                  return F.NIL;
                min = val1;
                max = val2;
                step = val3;
              } else {
                return F.NIL;
              }
            } else if (arg2.isInteger()) {
              int val = arg2.toIntDefault();
              if (val < 0)
                return F.NIL;
              min = 0;
              max = val;
            } else if (arg2 == S.All) {
              min = 0;
              max = size;
            } else {
              return F.NIL;
            }
          }

          if (max > size) {
            max = size;
          }

          IASTAppendable result = F.ListAlloc();
          for (int len = min; len <= max && result.argSize() < limit; len += step) {
            if (len == 0) {
              // exactly one empty subsequence (retaining the input's head)
              result.append(list.copyFrom(1, 1));
            } else {
              // all contiguous windows of this length, left to right
              for (int i = 1; i <= size - len + 1 && result.argSize() < limit; i++) {
                result.append(list.copyFrom(i, i + len));
              }
            }
          }
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>AllSameBy(list, f)</code> - test if <code>f</code> gives the same value for all elements
   * of <code>list</code>. <code>AllSameBy(f)</code> is the operator form.
   */
  private static final class AllSameBy extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isListOrAssociation()) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      IExpr function = ast.arg2();
      IExpr first = F.NIL;
      for (int i = 1; i < list.size(); i++) {
        IExpr value = engine.evaluate(F.unaryAST1(function, list.get(i)));
        if (first.isNIL()) {
          first = value;
        } else if (!Predicates.isSameQ(first, value)) {
          return S.False;
        }
      }
      return S.True;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }
  }

  /**
   * <code>ComapApply(f(g, h), {x, y})</code> - apply each element of the first argument to the
   * sequence of the elements of the second argument. With <code>Heads -&gt; True</code> the head of
   * the first argument is applied too.
   */
  private static final class ComapApply extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] option, EvalEngine engine,
        IAST originalAST) {
      if (argSize != 2) {
        return F.NIL;
      }
      IExpr functions = ast.arg1();
      if (!functions.isASTOrAssociation()) {
        return functions;
      }
      IExpr arguments = ast.arg2();
      IAST result = ((IAST) functions).map(x -> F.Apply(x, arguments));
      if (option[0].isTrue() && !functions.isAssociation()) {
        return result.setAtCopy(0, F.Apply(functions.head(), arguments));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.Heads, S.False);
    }
  }

  /**
   * The elements of the first argument of <code>FindRepeat, FindTransientRepeat</code>: a list, an
   * association or a string.
   */
  private static final class RepeatElements {
    private final IExpr sequence;
    private final IExpr[] items;
    private final BiPredicate<IExpr, IExpr> sameTest;

    private RepeatElements(IExpr sequence, IExpr[] items, BiPredicate<IExpr, IExpr> sameTest) {
      this.sequence = sequence;
      this.items = items;
      this.sameTest = sameTest;
    }

    /**
     * @return <code>null</code> if <code>sequence</code> is no list, association or string
     */
    static RepeatElements of(IExpr sequence, IExpr sameTest, EvalEngine engine) {
      IExpr[] items;
      if (sequence.isString()) {
        int[] codePoints = sequence.toString().codePoints().toArray();
        items = new IExpr[codePoints.length];
        for (int i = 0; i < codePoints.length; i++) {
          items[i] = F.stringx(new String(Character.toChars(codePoints[i])));
        }
      } else if (sequence.isListOrAssociation()) {
        items = new IExpr[sequence.argSize()];
        for (int i = 0; i < items.length; i++) {
          items[i] = ((IAST) sequence).get(i + 1);
        }
      } else {
        return null;
      }
      return new RepeatElements(sequence, items, Predicates.identicalTest(sameTest, engine));
    }

    int length() {
      return items.length;
    }

    boolean same(int i, int j) {
      return sameTest.test(items[i], items[j]);
    }

    /** The elements <code>from &lt;= i &lt; to</code> (counted from 0) with the input's type. */
    IExpr slice(int from, int to) {
      if (sequence.isString()) {
        StringBuilder buf = new StringBuilder();
        for (int i = from; i < to; i++) {
          buf.append(items[i].toString());
        }
        return F.stringx(buf.toString());
      }
      if (sequence.isAssociation()) {
        IAssociation assoc = F.assoc();
        for (int i = from; i < to; i++) {
          assoc.appendRule(((IAssociation) sequence).getRule(i + 1));
        }
        return assoc;
      }
      return ((IAST) sequence).copyFrom(from + 1, to + 1);
    }
  }

  /**
   * <code>FindRepeat(seq)</code> - the shortest sequence which, repeated, reproduces
   * <code>seq</code>; the last repetition may be incomplete. <code>FindRepeat(seq, n)</code> needs
   * at least <code>n</code> whole repetitions and gives the empty sequence if there is no such
   * period; <code>{n}</code> is the same for the only level of the sequence. The option
   * <code>SameTest</code> decides which elements are the same.
   */
  private static final class FindRepeat extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] option, EvalEngine engine,
        IAST originalAST) {
      int minRepeats = 1;
      if (argSize == 2) {
        minRepeats = (ast.arg2().isList1() ? ast.arg2().first() : ast.arg2()).toIntDefault();
        if (minRepeats < 1) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
        }
      }
      RepeatElements elements = RepeatElements.of(ast.arg1(), option[0], engine);
      if (elements == null) {
        // The first argument `1` to `2` is expected to be a list, an association or a string.
        return Errors.printMessage(ast.topHead(), "arg1", F.list(ast.arg1(), ast.topHead()),
            engine);
      }
      int length = elements.length();
      for (int period = 1; period <= length / minRepeats; period++) {
        engine.checkTimeLimit();
        boolean repeats = true;
        for (int i = period; repeats && i < length; i++) {
          repeats = elements.same(i, i % period);
        }
        if (repeats) {
          return elements.slice(0, period);
        }
      }
      return elements.slice(0, 0);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.putMessage(IPatternMatcher.SET, "arg1",
          F.stringx("The first argument `1` to `2` is expected to be a list, an association "
              + "or a string."));
      setOptions(newSymbol, S.SameTest, S.Automatic);
    }
  }

  /**
   * <code>FindTransientRepeat(seq, n)</code> - split <code>seq</code> into
   * <code>{transient, repeat}</code>, where <code>repeat</code> occurs at least <code>n</code>
   * times at the end of <code>seq</code>. Among all such splits the shortest transient is chosen,
   * and among those the shortest repeat. The option <code>SameTest</code> decides which elements
   * are the same.
   */
  private static final class FindTransientRepeat extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] option, EvalEngine engine,
        IAST originalAST) {
      int minRepeats = ast.arg2().toIntDefault();
      if (minRepeats < 1) {
        // Positive machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
      }
      RepeatElements elements = RepeatElements.of(ast.arg1(), option[0], engine);
      if (elements == null) {
        return F.NIL;
      }
      int length = elements.length();
      int bestTransient = length;
      int bestPeriod = 0;
      for (int period = 1; period <= length; period++) {
        engine.checkTimeLimit();
        int repeats = 1;
        while ((repeats + 1) * period <= length
            && sameBlock(elements, length - (repeats + 1) * period, length - period, period)) {
          repeats++;
        }
        int transientLength = length - repeats * period;
        if (repeats >= minRepeats && transientLength < bestTransient) {
          bestTransient = transientLength;
          bestPeriod = period;
        }
      }
      return F.list(elements.slice(0, bestTransient),
          elements.slice(bestTransient, bestTransient + bestPeriod));
    }

    private static boolean sameBlock(RepeatElements elements, int start1, int start2, int length) {
      for (int i = 0; i < length; i++) {
        if (!elements.same(start1 + i, start2 + i)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.SameTest, S.Automatic);
    }
  }

  /**
   * <code>PositionLargest(list)</code>, <code>PositionSmallest(list)</code> - the positions of the
   * extreme value of <code>list</code>; with a number <code>n</code> as second argument one list of
   * positions for each of the <code>n</code> extreme values; a third argument is an ordering
   * function, which gives <code>1</code> if its arguments are in order, <code>-1</code> if they are
   * not and <code>0</code> if they tie. Without it the elements must be numeric.
   */
  private static final class PositionExtreme extends AbstractFunctionEvaluator {
    /** The result of a comparison which gave no order. */
    private static final int NO_ORDER = Integer.MIN_VALUE;

    private final boolean largest;

    PositionExtreme(boolean largest) {
      this.largest = largest;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.arg1().isAssociation() && ast.arg1().argSize() > 0) {
        // the positions of the values of an association
        IAssociation association = (IAssociation) ast.arg1();
        return ast.setAtCopy(1,
            F.mapRange(1, association.size(), i -> association.getValue(i)));
      }
      if (!ast.arg1().isList() || ast.arg1().isEmptyList()) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      int count = -1;
      if (ast.argSize() >= 2 && ast.arg2() != S.Automatic) {
        IExpr countSpec = ast.arg2().isAST(S.UpTo, 2) ? ast.arg2().first() : ast.arg2();
        count = countSpec.toIntDefault();
        if (count < 0) {
          // Positive machine-sized integer or Automatic expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intpma", F.list(ast, F.C2), engine);
        }
      }
      IExpr orderFunction = ast.isAST3() ? ast.arg3() : F.NIL;
      if (orderFunction.isNIL() && !list.forAll(x -> !Double.isNaN(x.evalfNaN()))) {
        // Input `1` is not a real-valued vector.
        return Errors.printMessage(ast.topHead(), "rvec2", F.list(list), engine);
      }

      // the distinct values in ascending order, with the ascending positions of each one
      List<IExpr> values = new ArrayList<>();
      List<IASTAppendable> groups = new ArrayList<>();
      for (int i = 1; i < list.size(); i++) {
        engine.checkTimeLimit();
        IExpr element = list.get(i);
        int low = 0;
        int high = values.size();
        int group = -1;
        while (group < 0 && low < high) {
          int middle = (low + high) >>> 1;
          int order = compare(orderFunction, values.get(middle), element, engine);
          if (order == NO_ORDER) {
            // Input `1` is not a real-valued vector.
            return Errors.printMessage(ast.topHead(), "rvec2", F.list(list), engine);
          }
          if (order == 0) {
            group = middle;
          } else if (order > 0) {
            low = middle + 1;
          } else {
            high = middle;
          }
        }
        if (group < 0) {
          values.add(low, element);
          groups.add(low, F.ListAlloc());
          group = low;
        }
        groups.get(group).append(F.ZZ(i));
      }
      return extremeGroups(groups, count);
    }

    /** The result of the ordering function, or the numeric order if there is none. */
    private static int compare(IExpr orderFunction, IExpr x, IExpr y, EvalEngine engine) {
      if (orderFunction.isNIL()) {
        if (x.isRational() && y.isRational()) {
          return -Integer.signum(x.compareTo(y));
        }
        return -Double.compare(x.evalfNaN(), y.evalfNaN());
      }
      int order = engine.evaluate(F.binaryAST2(orderFunction, x, y)).toIntDefault();
      return order >= -1 && order <= 1 ? order : NO_ORDER;
    }

    /**
     * Take the <code>count</code> extreme groups from the groups of positions, which are given in
     * ascending order of their values. The largest values are built as the reversal of the
     * ascending structure, so the positions of a group run downwards if more than one value was
     * asked for.
     *
     * @param count the number of values, or <code>-1</code> for the positions of the extreme value
     */
    private IExpr extremeGroups(List<IASTAppendable> groups, int count) {
      int size = groups.size();
      if (count < 0) {
        return groups.get(largest ? size - 1 : 0);
      }
      int kept = Math.min(count, size);
      IASTAppendable result = F.ListAlloc(kept);
      for (int i = 0; i < kept; i++) {
        if (!largest) {
          result.append(groups.get(i));
        } else {
          IASTAppendable group = groups.get(size - 1 - i);
          result.append(count == 1 ? group : group.reverse(F.ListAlloc(group.size())));
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.putMessage(IPatternMatcher.SET, "intpma", F.stringx(
          "Positive machine-sized integer or Automatic expected at position `2` in `1`."));
      newSymbol.putMessage(IPatternMatcher.SET, "rvec2",
          F.stringx("Input `1` is not a real-valued vector."));
    }
  }

  /**
   * <code>SymmetricDifference(list1, list2, ...)</code> - the sorted elements which are in an odd
   * number of the lists. The option <code>SameTest</code> decides which elements are the same.
   */
  private static final class SymmetricDifference extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] option, EvalEngine engine,
        IAST originalAST) {
      for (int i = 1; i <= argSize; i++) {
        if (!ast.get(i).isAST()) {
          return F.NIL;
        }
        if (!ast.get(i).head().equals(ast.arg1().head())) {
          // Heads `1` and `2` at positions `3` and `4` are expected to be the same.
          return Errors.printMessage(ast.topHead(), "heads2",
              F.List(ast.get(i).head(), ast.arg1().head(), F.ZZ(i), F.C1), engine);
        }
      }
      IASTAppendable result = option[0] == S.Automatic ? identicalElements(ast, argSize)
          : elementsBySameTest(ast, argSize, option[0], engine);
      EvalAttributes.sort(result);
      if (option[0] == S.Automatic) {
        return result;
      }
      // one representative of the elements which the test takes to be the same
      BiPredicate<IExpr, IExpr> test = Predicates.identicalTest(option[0], engine);
      IASTAppendable representatives = ((IAST) ast.arg1()).copyHead();
      for (int i = 1; i < result.size(); i++) {
        IExpr element = result.get(i);
        if (!representatives.exists(x -> test.test(element, x))) {
          representatives.append(element);
        }
      }
      return representatives;
    }

    private static IASTAppendable identicalElements(IAST ast, int argSize) {
      Map<IExpr, int[]> counts = new HashMap<>();
      for (int i = 1; i <= argSize; i++) {
        IAST list = (IAST) ast.get(i);
        LinkedHashSet<IExpr> distinct = new LinkedHashSet<>();
        for (int j = 1; j < list.size(); j++) {
          distinct.add(list.get(j));
        }
        for (IExpr element : distinct) {
          counts.computeIfAbsent(element, x -> new int[1])[0]++;
        }
      }
      IASTAppendable result = ((IAST) ast.arg1()).copyHead();
      counts.forEach((element, count) -> {
        if ((count[0] & 1) == 1) {
          result.append(element);
        }
      });
      return result;
    }

    private static IASTAppendable elementsBySameTest(IAST ast, int argSize, IExpr sameTest,
        EvalEngine engine) {
      BiPredicate<IExpr, IExpr> test = Predicates.identicalTest(sameTest, engine);
      IASTAppendable result = ((IAST) ast.arg1()).copyHead();
      for (int k = 1; k <= argSize; k++) {
        IAST list = (IAST) ast.get(k);
        for (int i = 1; i < list.size(); i++) {
          engine.checkTimeLimit();
          IExpr element = list.get(i);
          int count = 1;
          for (int m = 1; m <= argSize; m++) {
            if (m != k && ((IAST) ast.get(m)).exists(x -> test.test(element, x))) {
              count++;
            }
          }
          if ((count & 1) == 1) {
            result.append(element);
          }
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.SameTest, S.Automatic);
    }
  }

  /**
   * <code>Upsample(list, n)</code> - insert <code>n-1</code> zeros behind each element of
   * <code>list</code>. <code>Upsample(list, n, offset)</code> puts each element at position
   * <code>offset</code> of its block of <code>n</code> elements.
   */
  private static final class Upsample extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList() || ((IAST) ast.arg1()).exists(x -> x.isList())) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      int n = ast.arg2().toIntDefault();
      if (n <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
      }
      int offset = ast.argSize() >= 3 ? ast.arg3().toIntDefault() : 1;
      // the value of the inserted elements
      final IExpr padding = ast.argSize() == 4 ? ast.arg4() : F.C0;
      if (offset <= 0 || offset > n) {
        return F.NIL;
      }
      long size = (long) n * list.argSize();
      if (size > Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt(size);
      }
      IASTAppendable result = F.ListAlloc((int) size);
      for (int i = 1; i < list.size(); i++) {
        for (int j = 1; j <= n; j++) {
          result.append(j == offset ? list.get(i) : padding);
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private SequenceFunctions() {}
}
