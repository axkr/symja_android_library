package org.matheclipse.core.builtin;

import java.util.function.Function;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
      S.SequenceCases.setEvaluator(new SequenceCases());
      S.SequenceCount.setEvaluator(new SequenceCount());
      S.SequencePosition.setEvaluator(new SequencePosition());
      S.SequenceReplace.setEvaluator(new SequenceReplace());
      S.SequenceSplit.setEvaluator(new SequenceSplit());
    }
  }



  private static final class SequenceCases extends AbstractFunctionOptionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      try {
        if (ast.arg2().isAST(S.Rule, 3, S.Overlaps)) {
          // List or pattern matching a list expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "lstpat", F.List(F.C2, ast), engine);
        }
        IExpr overlapsOption = S.False;
        if (options[0] == S.All || options[0].isFalse() || options[0].isTrue()) {
          overlapsOption = options[0];
        } else {
          // Value of option `1` must be True, False or All.
          return Errors.printMessage(ast.topHead(), "ovls", F.List(F.Rule(S.Overlaps, options[0])),
              engine);
        }

        final IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          final IExpr arg2 = engine.evalPattern(ast.arg2());
          return sequenceCases((IAST) arg1, arg2, overlapsOption, engine);
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
      int i = 1;
      while (i < ast.size()) {
        if (overlapsOption == S.All) {
          IASTAppendable allResults = F.ListAlloc();
          for (int k = i + 1; k <= ast.size(); k++) {
            // TODO optimize by classifying pattern matchers
            // use greedy search because of possible pattern sequences
            IASTAppendable subSequence = ast.copyFrom(i, k);
            if (matcher.test(subSequence)) {
              allResults.append(subSequence);
            } else {
              break;
            }
          }
          if (allResults.argSize() > 0) {
            allResults.reverse(resultAST);
          }
        } else {
          for (int j = i + 1; j <= ast.size(); j++) {
            IExpr subResult = F.NIL;
            for (int k = j; k <= ast.size(); k++) {
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
      int i = 1;
      while (i < ast.size()) {
        if (overlapsOption == S.All) {
          IASTAppendable allResults = F.ListAlloc();
          for (int k = i + 1; k <= ast.size(); k++) {
            // TODO optimize by classifying pattern matchers
            // use greedy search because of possible pattern sequences
            IASTAppendable subSequence = ast.copyFrom(i, k);
            IExpr temp = function.apply(subSequence);
            if (temp.isPresent()) {
              allResults.append(temp);
            } else {
              break;
            }
          }
          if (allResults.argSize() > 0) {
            allResults.reverse(resultAST);
          }
        } else {
          for (int j = i + 1; j <= ast.size(); j++) {
            IExpr subResult = F.NIL;
            IExpr result = F.NIL;
            for (int k = j; k <= ast.size(); k++) {
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

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.Overlaps, S.False);
    }

  }

  private static final class SequenceCount extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      try {
        if (ast.arg2().isAST(S.Rule, 3, S.Overlaps)) {
          // List or pattern matching a list expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "lstpat", F.List(F.C2, ast), engine);
        }
        IExpr overlapsOption = S.False;
        if (options[0] == S.All || options[0].isFalse() || options[0].isTrue()) {
          overlapsOption = options[0];
        } else {
          // Value of option `1` must be True, False or All.
          return Errors.printMessage(ast.topHead(), "ovls", F.List(F.Rule(S.Overlaps, options[0])),
              engine);
        }

        final IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          final IExpr arg2 = engine.evalPattern(ast.arg2());
          return sequenceCount((IAST) arg1, arg2, overlapsOption, engine);
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

    public static IExpr sequenceCount(final IAST ast, final IExpr pattern, IExpr overlapsOption,
        EvalEngine engine) {
      int count = 0;
      final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
      int i = 1;

      while (i < ast.size()) {
        // Greedy match: iterate k downwards to match the longest possible sequences first
        for (int k = ast.size(); k >= i + 1; k--) {
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

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.Overlaps, S.False);
    }
  }

  private static final class SequencePosition extends AbstractFunctionOptionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      try {
        IExpr overlapsOption = S.True;
        int maxMatches = Integer.MAX_VALUE;

        if (ast.arg2().isAST(S.Rule, 3, S.Overlaps)) {
          // List or pattern matching a list expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "lstpat", F.List(F.C2, ast), engine);
        }
        if (options[0] == S.All || options[0].isFalse() || options[0].isTrue()) {
          overlapsOption = options[0];
        } else {
          // Value of option `1` must be True, False or All.
          return Errors.printMessage(ast.topHead(), "ovls", F.List(F.Rule(S.Overlaps, options[0])),
              engine);
        }

        final IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          final IExpr arg2 = engine.evalPattern(ast.arg2());
          if (argSize == 3) {
            final IExpr arg3 = engine.evaluate(ast.arg3());
            if (!arg3.isInfinity()) {
              int maxN = arg3.toIntDefault();
              if (maxN < 0 || !arg3.isInteger()) {
                // Non-negative integer or Infinity expected at position `3` in `ast`.
                return Errors.printMessage(ast.topHead(), "innf", F.List(F.C3, ast), engine);
              }
              maxMatches = maxN;
            }
          }
          return sequencePosition((IAST) arg1, arg2, overlapsOption, maxMatches, engine);
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

    public static IAST sequencePosition(final IAST ast, final IExpr pattern, IExpr overlapsOption,
        int maxMatches, EvalEngine engine) {
      IASTAppendable resultAST = F.ListAlloc();
      if (maxMatches <= 0) {
        return resultAST;
      }

      final IPatternMatcher matcher = engine.evalPatternMatcher(pattern);
      int i = 1;
      while (i < ast.size()) {
        for (int k = ast.size(); k >= i + 1; k--) {
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
              int maxN = arg3.toIntDefault();
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
          return Errors.printMessage(S.SequenceSplit, "reps", F.List(listOfRules), engine);
        }
      }
      int i = 1;
      int lastI = i;
      while (i < list.size()) {
        for (int j = i + 1; j <= list.size(); j++) {
          IExpr subResult = F.NIL;
          int lastK = -1;

          boolean matched = false;
          for (int k = j; k <= list.size(); k++) {
            if (i >= k) {
              break;
            }
            IASTAppendable subSequence = list.copyFrom(i, k);
            for (int l = 0; l < functions.length; l++) {
              IExpr temp = ((Function<IExpr, IExpr>) functions[l]).apply(subSequence);
              if (temp.isPresent()) {
                subResult = temp;
                lastK = k;
                matched = true;
                break;
              }
            }
            if (!matched) {
              break;
            }
          }
          if (lastK > i) {
            if (lastI < i) {
              resultAST.appendArgs(lastI, i, n -> list.get(n));
            }
            if (subResult.isPresent()) {
              resultAST.append(subResult);
            }
            i = lastK;
            lastI = i;
            if (--maxReplacements <= 0) {
              if (lastI < list.size()) {
                resultAST.appendArgs(lastI, list.size(), n -> list.get(n));
              }
              return resultAST;
            }
          }

        }
        i++;
      }
      if (lastI < list.size()) {
        resultAST.appendArgs(lastI, list.size(), n -> list.get(n));
      }
      return resultAST;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
    }

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
              int maxParts = arg3.toIntDefault();
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
      int i = 1;
      int lastI = i;
      while (i < list.size()) {
        for (int j = i + 1; j <= list.size(); j++) {
          IExpr subResult = F.NIL;
          int lastK = -1;

          boolean matched = false;
          for (int k = j; k <= list.size(); k++) {
            if (i >= k) {
              break;
            }
            // greedy search
            IASTAppendable subSequence = list.copyFrom(i, k);
            for (int l = 0; l < matchers.length; l++) {
              if (matchers[l] != null) {
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
              resultAST.append(list.copyFrom(lastI, i));
            }
            if (subResult.isPresent()) {
              resultAST.append(subResult);
            }
            i = lastK;
            lastI = i;
            if (--maxSplits <= 0) {
              if (lastI < list.size()) {
                resultAST.append(list.copyFrom(lastI, list.size()));
              }
              return resultAST;
            }
          }

        }
        i++;
      }
      if (lastI < list.size()) {
        resultAST.append(list.copyFrom(lastI, list.size()));
      }
      return resultAST;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
    }

  }

  public static void initialize() {
    Initializer.init();
  }

  private SequenceFunctions() {}
}
