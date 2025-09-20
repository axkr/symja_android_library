package org.matheclipse.core.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.util.OpenFixedSizeMap;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherAndEvaluator;
import org.matheclipse.core.patternmatching.PatternMatcherList;
import com.google.common.collect.ImmutableMap;

public class Functors {

  private static class SingleRuleFunctor implements Function<IExpr, IExpr> {
    private final IExpr lhs;
    private final IExpr rhs;

    /**
     * @param equalRule the left- and right-hand-side (i.e. arg1() and arg2()) which should be
     *        tested for equality
     */
    public SingleRuleFunctor(IAST equalRule) {
      lhs = equalRule.arg1();
      rhs = equalRule.arg2();
    }

    @Override
    public IExpr apply(final IExpr arg) {
      return lhs.equals(arg) ? rhs : F.NIL;
    }
  }

  private static class RulesFunctor implements Function<IExpr, IExpr> {
    private final Map<? extends IExpr, ? extends IExpr> fEqualRules;

    public RulesFunctor(Map<? extends IExpr, ? extends IExpr> rulesMap) {
      fEqualRules = rulesMap;
    }

    @Override
    public IExpr apply(final IExpr arg) {
      IExpr temp = fEqualRules.get(arg);
      return temp != null ? temp : F.NIL;
    }
  }

  private static class RulesPatternFunctor implements Function<IExpr, IExpr> {
    private final Map<IExpr, IExpr> fEqualRules;
    private final List<PatternMatcherAndEvaluator> fMatchers;
    private final EvalEngine fEngine;

    public RulesPatternFunctor(Map<IExpr, IExpr> equalRules,
        List<PatternMatcherAndEvaluator> matchers, EvalEngine engine) {
      fEqualRules = equalRules;
      fMatchers = matchers;
      fEngine = engine;
    }

    @Override
    public IExpr apply(final IExpr arg) {
      IExpr temp = fEqualRules.get(arg);
      if (temp != null) {
        return temp;
      }
      for (int i = 0; i < fMatchers.size(); i++) {
        temp = fMatchers.get(i).replace(arg, fEngine);
        if (temp.isPresent()) {
          return temp;
        }
      }
      return F.NIL;
    }
  }

  private static class ListRulesPatternFunctor implements Function<IExpr, IExpr> {
    private final Map<IExpr, IExpr> fEqualRules;
    private final List<PatternMatcherList> fMatchers;
    private final EvalEngine fEngine;
    private IASTAppendable fResult;

    /**
     * @param equalRules
     * @param matchers
     * @param result
     * @param engine
     */
    public ListRulesPatternFunctor(Map<IExpr, IExpr> equalRules, List<PatternMatcherList> matchers,
        IASTAppendable result, EvalEngine engine) {
      fEqualRules = equalRules;
      fMatchers = matchers;
      fResult = result;
      fEngine = engine;
    }

    public ListRulesPatternFunctor(Map<IExpr, IExpr> rulesMap, IASTAppendable result) {
      fEqualRules = rulesMap;
      fResult = result;
      fMatchers = null;
      fEngine = null;
    }

    @Override
    public IExpr apply(final IExpr arg) {
      IExpr temp = fEqualRules.get(arg);
      if (temp != null) {
        fResult.append(temp);
        return temp;
      }
      if (fMatchers != null) {
        for (int i = 0; i < fMatchers.size(); i++) {
          PatternMatcherList matcher = fMatchers.get(i);
          if (matcher != null) {
            matcher.replaceEvaled(arg, fEngine);
            IAST list = matcher.getReplaceList();
            if (list.size() > 1) {
              for (int j = 1; j < list.size(); j++) {
                fResult.append(list.get(j));
              }
              return list;
            }
          }
        }
      }
      return F.NIL;
    }
  }

  private static class SubsFunctor implements Function<IExpr, IExpr> {
    private final IAST listOfRules;

    public SubsFunctor(IAST rulesList) {
      this.listOfRules = rulesList;
    }

    @Override
    public IExpr apply(final IExpr arg) {
      IExpr[] resultArg = new IExpr[] {arg};

      for (int i = 1; i < listOfRules.size(); i++) {
        IAST rule = (IAST) listOfRules.get(i);
        if (rule.arg1().equals(resultArg[0])) {
          return rule.arg2();
        } else if (rule.arg1().isPlus()) {
          IAST plusLHS = (IAST) rule.arg1();
          if (resultArg[0].isPlus()) {
            IAST plus = (IAST) resultArg[0];
            if (subsPlus(plus, plusLHS, rule.arg2(), resultArg)) {
              break;
            }
            IExpr negPlus = plusLHS.negate();
            if (negPlus.isPlus()) {
              if (subsPlus(plus, (IAST) negPlus, rule.arg2().negate(), resultArg)) {
                break;
              }
            }
          }
        } else if (rule.arg1().isTimes()) {
          IAST timesLHS = (IAST) rule.arg1();
          if (resultArg[0].isTimes()) {
            IAST times = (IAST) resultArg[0];
            if (subsTimes(times, timesLHS, rule.arg2(), resultArg)) {
              break;
            }
          } else if (timesLHS.isAST2() && timesLHS.first().isMinusOne()
              && timesLHS.second().equals(resultArg[0])) {
            return F.Times(F.CN1, rule.arg2());
          }
        } else if (rule.arg1().isPower()) {
          IAST powerLHS = (IAST) rule.arg1();
          if (resultArg[0].isTimes() && powerLHS.exponent().isRational()) {
            IAST times = (IAST) resultArg[0];
            // boolean stop = false;
            IASTAppendable result = times.copyAppendable();
            int j = 1;
            IRational exponentCounter = F.C0;
            while (j < result.size()) {
              IExpr expr = result.get(j);
              final IRational ratExp;
              if (expr.equals(powerLHS.base())) {
                ratExp = F.C1;
              } else if (expr.isPower() && expr.exponent().isRational()
                  && expr.base().equals(powerLHS.base())) {
                ratExp = (IRational) expr.exponent();
              } else {
                ratExp = null;
              }
              if (ratExp != null) {
                IRational ratArg = (IRational) powerLHS.exponent();
                IRational div = ratExp.divideBy(ratArg);
                if (div.denominator().isOne()) {
                  IInteger numerator = div.numerator();
                  result.remove(j);
                  exponentCounter = exponentCounter.add(numerator);
                  continue;
                }
              }
              j++;
            }
            if (!exponentCounter.isZero()) {
              if (exponentCounter.isOne()) {
                result.append(rule.arg2());
              } else {
                result.append(F.Power(rule.arg2(), exponentCounter));
              }
              resultArg[0] = result;
            }

          } else if (resultArg[0].isPower()) {
            IAST power = (IAST) resultArg[0];
            if (powerLHS.base().equals(resultArg[0].base())) {
              // if (power.exponent().isInteger()) {
              // IInteger exponent1 = (IInteger) power.exponent();
              // if (powerLHS.exponent().isInteger()) {
              // IInteger exponent2 = (IInteger) powerLHS.exponent();
              // if (exponent2.isLT(exponent1) && exponent2.isPositive()) {
              // IInteger mod = exponent1.mod(exponent2);
              // if (mod.isZero()) {
              // IInteger quotient = exponent1.quotient(exponent2);
              // resultArg[0] = F.Power(rule.arg2(), quotient);
              // break;
              // }
              // }
              // }
              // }

              IAST lhsRule = powerLHS;
              // if (powerLHS.exponent().isTimes()) {
              // lhsRule = nestedPower(powerLHS);
              // }
              IExpr lhsBase = lhsRule;
              IRational lhsExponent = F.C1;
              if (lhsRule.isPower() && lhsRule.exponent().isRational()) {
                lhsBase = lhsRule.base();
                lhsExponent = (IRational) lhsRule.exponent();
              }
              IASTAppendable result = F.unary(S.List, resultArg[0]);
              IExpr spp =
                  subsPowerPowers(result, power, lhsRule, lhsBase, lhsExponent, rule.arg2());
              if (spp.isPresent()) {
                resultArg[0] = spp;
              }

            }
          }
        }
      }
      return mapArgumentsToRule(arg, resultArg);
    }

    private IExpr mapArgumentsToRule(final IExpr arg, IExpr[] resultArg) {
      if (resultArg[0].isAST()) {
        boolean evaled = true;
        while (evaled && resultArg[0].isAST()) {
          IAST list = (IAST) resultArg[0];
          evaled = false;
          for (int j = 1; j < listOfRules.size(); j++) {
            IAST rule = (IAST) listOfRules.get(j);
            SubsFunctor functor = new SubsFunctor(rule.makeList());
            for (int i = 1; i < list.size(); i++) {
              IExpr temp = functor.apply(list.get(i));
              if (temp.isPresent() && !temp.equals(list.get(i))) {
                evaled = true;
                IASTMutable copy = list.setAtCopy(i, temp);
                resultArg[0] = EvalEngine.get().evaluate(copy);
                break;
              }
            }
            if (evaled) {
              break;
            }
          }
        }
      }
      return (resultArg[0] != arg) ? resultArg[0] : F.NIL;
    }

    private boolean subsTimes(IAST times, IAST timesLHS, IExpr rhs, IExpr[] resultArg) {
      if (times.size() >= timesLHS.size() && timesLHS.size() > 1) {
        int replaceCounter = 0;
        IASTAppendable result = times.copyAppendable();

        IRational factor = F.C1;
        boolean stop = false;
        IRational ratNum = F.C1;
        IRational ratNumLHS = F.C1;
        int maxLimit = 1;
        boolean isRatNumNegative = false;
        boolean isRatNumLHSNegative = false;
        if (timesLHS.arg1().isRational()) {
          ratNumLHS = (IRational) timesLHS.arg1();
          timesLHS = timesLHS.rest();
          if (times.arg1().isRational()) {
            ratNum = (IRational) times.arg1();
            factor = ratNum;
            result = times.removeAtClone(1);
          } else {
            result = times.copyAppendable();
          }

          isRatNumNegative = ratNum.isNegative();
          isRatNumLHSNegative = ratNumLHS.isNegative();
          if (isRatNumNegative != isRatNumLHSNegative && isRatNumLHSNegative) {
            return false;
          }
          IRational ratNumAbs = isRatNumNegative ? ratNum.abs() : ratNum;
          IRational ratNumLHSAbs = isRatNumLHSNegative ? ratNumLHS.abs() : ratNumLHS;
          maxLimit =
              (int) (Math.log(ratNumAbs.doubleValue()) / Math.log(ratNumLHSAbs.doubleValue()));
          if (!ratNumLHSAbs.pow(maxLimit).equals(ratNumAbs)) {
            maxLimit = 1;
          }
          factor = ratNum.divideBy(ratNumLHS);
          if (maxLimit == 1 && !factor.denominator().isOne()) {
            return false;
          }
        } else {
          result = times.copyAppendable();
        }
        if (maxLimit < 1) {
          return false;
        }
        int limit = maxLimit;
        IASTAppendable resultCopy = result.copyAppendable();
        while (true) {
          int j = 1;
          while (!timesLHS.isEmpty() && j < timesLHS.size()) {
            int k = removeTimesArg(result, timesLHS.get(j));
            if (k < 0) {
              result = resultCopy;
              stop = true;
              break;
            }
            if (k > result.size()) {
              break;
            }
            j++;
          }
          if (stop) {
            break;
          }
          resultCopy = result.copyAppendable();
          replaceCounter++;
          limit--;
          if (limit == 0 && !factor.isOne()) {
            break;
          }
        }
        if (replaceCounter > 0) {
          if (replaceCounter == 1) {
            if (!factor.isOne()) {
              result.append(factor);
            }
            result.append(rhs);
          } else {
            if (isRatNumNegative) {
              result.append(F.CN1);
            }
            if (limit > 0) {
              result.append(F.Power(ratNumLHS, limit));
            }
            result.append(F.Power(rhs, replaceCounter));
          }
          resultArg[0] = result;
          return true;
        }
      }
      return false;
    }

    private boolean subsPlus(IAST plus, IAST plusLHS, IExpr rhs, IExpr[] resultArg) {
      if (plus.size() >= plusLHS.size() && plusLHS.size() > 1) {
        int replaceCounter = 0;
        IASTAppendable result = plus.copyAppendable();

        boolean stop = false;
        IASTAppendable resultCopy = plus.copyAppendable();
        while (true) {
          int j = 1;
          while (j < plusLHS.size()) {
            int k = removePlusArg(result, plusLHS.get(j));
            if (k < 0) {
              result = resultCopy;
              stop = true;
              break;
            }
            if (k > result.size()) {
              break;
            }
            j++;
          }
          if (stop) {
            break;
          }
          resultCopy = result.copyAppendable();
          replaceCounter++;
        }
        if (replaceCounter > 0) {
          if (replaceCounter == 1) {
            result.append(rhs);
          } else {
            result.append(F.Times(replaceCounter, rhs));
          }
          resultArg[0] = result;
          return true;
        }
      }
      return false;
    }

    private static int removePlusArg(IASTAppendable result, IExpr arg) {
      int k = 1;
      while (k < result.size()) {
        final IExpr expr = result.get(k);
        if (expr.equals(arg)) {
          result.remove(k);
          return k;
        } else if (expr.isTimes2() && expr.second().equals(arg) && expr.first().isInteger()) {
          final IInteger arg1 = (IInteger) expr.first();
          if (arg1.isPositive()) {
            IInteger arg1New = arg1.dec();
            result.set(k, arg1New.isOne() ? arg : F.Times(arg1New, arg));
            return k;
          }
        } else if (arg.isRational() && expr.isRational()) {
          IRational ratExp = (IRational) expr;
          IRational ratArg = (IRational) arg;
          IRational sub = ratExp.subtract(ratArg);
          result.set(k, sub);
          return k;
        }
        k++;
      }
      return -1;
    }

    // private static int removeTimesArg(int start, int startLHS, IASTAppendable result,
    // IExpr lhsRule) {
    // int k = start;
    // if (lhsRule.isPower() && lhsRule.exponent().isTimes()) {
    // lhsRule = nestedPower(lhsRule);
    // }
    // IExpr lhsBase = lhsRule;
    // IRational lhsExponent = F.C1;
    // if (lhsRule.isPower() && lhsRule.exponent().isRational()) {
    // lhsBase = lhsRule.base();
    // lhsExponent = (IRational) lhsRule.exponent();
    // }
    // while (k < result.size()) {
    // IExpr expr = result.get(k);
    // if (subsPowers(result, expr, lhsRule, k, lhsBase, null, lhsExponent)) {
    // return k;
    // }
    // k++;
    // }
    // return -1;
    // }

    private static int removeTimesArg(IASTAppendable result, IExpr lhsRule) {
      int k = 1;
      // if (lhsRule.isPower() && lhsRule.exponent().isTimes()) {
      // lhsRule = nestedPower(lhsRule);
      // }
      IExpr lhsBase = lhsRule;
      IExpr lhsExponent = F.C1;
      IRational lhsExponentFactor = F.C1;
      if (lhsRule.isPower()) {
        lhsBase = lhsRule.base();
        lhsExponent = lhsRule.exponent();
        if (lhsRule.exponent().isRational()) {
          lhsExponent = F.C1;
          lhsExponentFactor = (IRational) lhsRule.exponent();
        } else if (lhsExponent.isTimes() && lhsExponent.first().isRational()) {
          lhsExponentFactor = (IRational) lhsExponent.first();
          lhsExponent = lhsExponent.rest().oneIdentity1();
        }
      }
      while (k < result.size()) {
        IExpr expr = result.get(k);
        if (subsPowers(result, expr, lhsRule, k, lhsBase, lhsExponent, lhsExponentFactor)) {
          return k;
        }
        k++;
      }
      return -1;
    }

    protected static boolean subsPowers(IASTAppendable result, IExpr expr, IExpr lhsRule, int index,
        IExpr lhsBase, IExpr lhsExponent, IRational lhsExponentFactor) {
      if (expr.equals(lhsRule)) {
        result.remove(index);
        return true;
      } else if (expr.isPower() && expr.base().equals(lhsBase) && expr.exponent().isRational()) {
        final IRational exp = (IRational) expr.exponent();
        if (exp.isPositive()) {
          final IRational expNew = exp.subtract(lhsExponentFactor);
          result.set(index, expNew.isOne() ? lhsRule : F.Power(expr.base(), expNew));
          return true;
        }
      } else if (expr.isPower() && expr.base().equals(lhsBase)) {
        IExpr exprExponent = expr.exponent();
        IRational exprExponentFactor = F.C1;
        if (exprExponent.isRational()) {
          exprExponentFactor = (IRational) exprExponent;
          exprExponent = F.C1;
        } else if (exprExponent.isTimes() && exprExponent.first().isRational()) {
          exprExponentFactor = (IRational) exprExponent.first();
          exprExponent = exprExponent.rest().oneIdentity1();
        }

        if (exprExponentFactor.isPositive()) {
          final IRational expNew = exprExponentFactor.subtract(lhsExponentFactor);
          result.set(index,
              F.Power(expr.base(), expNew.isOne() ? exprExponent : F.Times(expNew, exprExponent)));
          return true;
        }

        //
        // final IInteger exp = (IInteger) expr.exponent();
        // if (exp.isPositive()) {
        // final IInteger expNew = exp.dec();
        // result.set(k, expNew.isOne() ? arg : F.Power(expr.base(), expNew));
        // return k;
        // }
      }
      return false;
    }

    protected static IExpr subsPowerPowers(IASTAppendable result, IExpr expr, IExpr lhsRule,
        IExpr lhsBase, IRational lhsExponent, IExpr rhs) {
      if (expr.equals(lhsRule)) {
        return rhs;
      } else if (expr.isPower() && expr.base().equals(lhsBase) && expr.exponent().isRational()) {
        final IRational exp = (IRational) expr.exponent();
        if (exp.isPositive()) {
          final IRational expNew = exp.divideBy(lhsExponent);
          if (expNew.denominator().isOne()) {
            return F.Power(rhs, expNew);
          }
        }
      } else if (expr.isPower()) {
        IExpr exprBase = expr;
        IExpr originalExponent = expr.exponent();

        IExpr exprExponent = F.C1;
        if (expr.exponent().isTimes()) {
          originalExponent = expr.exponent();
          if (originalExponent.first().isRational()) {
            originalExponent = originalExponent.rest().oneIdentity1();
          }
          expr = nestedPower(expr);
          exprBase = expr.base();
          exprExponent = expr.exponent();
        }
        if (exprBase.equals(lhsBase) && exprExponent.isRational()) {
          final IRational exp = (IRational) exprExponent;
          if (exp.isPositive()) {
            final IRational expNew = exp.divideBy(lhsExponent);
            return F.Power(rhs, expNew);
          }
        }
      }
      return F.NIL;
    }

    private static IASTMutable nestedPower(IExpr arg) {
      IAST expTimes = (IAST) arg.exponent();
      IExpr powerArg = arg.base();
      IASTMutable nestedPower = F.NIL;
      for (int i = expTimes.argSize(); i > 0; i--) {
        nestedPower = F.binaryAST2(S.Power, powerArg, expTimes.get(i));
        powerArg = nestedPower;
      }
      return nestedPower;
    }
  }


  public static class SubsetPatternFunctor implements Function<IAST, IExpr> {

    private final List<PatternMatcherAndEvaluator> fMatchers;
    private IExpr[] fSubstitutedMatches;
    private final EvalEngine fEngine;

    /**
     * @param matchers
     * @param engine
     */
    public SubsetPatternFunctor(List<PatternMatcherAndEvaluator> matchers, EvalEngine engine) {
      this.fMatchers = matchers;
      this.fEngine = engine;
      this.fSubstitutedMatches = null;
    }

    @Override
    public IExpr apply(final IAST arg) {
      if (fMatchers != null) {
        final int argSize = arg.argSize();

        int[] allReplaceIndex = new int[] {0};
        int[] allRemoveIndex = new int[] {0};
        int[] allReplacePositions = new int[argSize];
        IExpr[] allReplaceExprs = new IExpr[argSize];
        int[] allRemovePositions = new int[argSize];
        this.fSubstitutedMatches = new IExpr[argSize];
        boolean evaled = false;
        for (int i = 0; i < fMatchers.size(); i++) {
          PatternMatcherAndEvaluator matcher = fMatchers.get(i);
          if (matcher != null) {
            IExpr lhs = matcher.getLHS();
            if (lhs.isAST()) {
              IAST lhsPatternExpr = (IAST) lhs;
              boolean matched = matcher.matchASTSubset(lhsPatternExpr, arg, allReplacePositions,
                  allReplaceExprs, allReplaceIndex, allRemovePositions, allRemoveIndex, fEngine);

              while (matched) {
                IExpr rhs = matcher.replacePatternMatch(matcher.getLHS(), matcher.getPatternMap(),
                    fEngine, false);
                allReplaceExprs[allReplaceIndex[0]] = rhs;
                this.fSubstitutedMatches[allReplaceIndex[0]++] = matcher.getSubstitutedMatch();

                evaled = true;
                matched = matcher.matchASTSubset(lhsPatternExpr, arg, allReplacePositions,
                    allReplaceExprs, allReplaceIndex, allRemovePositions, allRemoveIndex, fEngine);
              }
            }
          }
        }
        if (evaled) {
          Arrays.sort(allRemovePositions);
          return arg.replaceSubset(allReplacePositions, allReplaceExprs, allRemovePositions);
        }
      }
      return F.NIL;
    }

    public IExpr[] getSubstitutedMatches() {
      return fSubstitutedMatches;
    }

  }

  /**
   * Create a functor from the given map, which calls the <code>rulesMap.get()</code> in the
   * functors <code>apply</code>method.
   *
   * @param rulesMap
   * @return
   */
  public static Function<IExpr, IExpr> rules(Map<? extends IExpr, ? extends IExpr> rulesMap) {
    return new RulesFunctor(rulesMap);
  }

  /**
   * Create a functor from the given rule <code>lhs->rhs</code> or <code>lhs:>rhs</code> and match
   * the left-hand-side argument with the <code>#equals()</code> method. Therefore the
   * left-hand-side shouldn't contain any pattern or orderless expression.
   *
   * @param rule
   * @return
   */
  public static Function<IExpr, IExpr> equalRule(IAST rule) {
    if (rule.first().isAST(S.HoldPattern, 2)) {
      return new SingleRuleFunctor(rule.setAtCopy(1, rule.first().first()));
    }
    return new SingleRuleFunctor(rule);
  }

  /**
   * Create a functor from the given rules. All strings in <code>strRules</code> are parsed in
   * internal rules form.
   *
   * @param strRules array of rules of the form &quot;<code>x-&gt;y</code>&quot;
   * @return
   */
  public static Function<IExpr, IExpr> rules(String[] strRules) {
    final EvalEngine engine = EvalEngine.get();
    ExprParser parser = new ExprParser(engine);
    IASTAppendable astRules =
        F.mapRange(0, strRules.length, i -> engine.evaluate(parser.parse(strRules[i])));
    return rules(astRules, engine);
  }

  /**
   * Create a functor from the given rules. If <code>astRules</code> is a <code>List[]</code>
   * object, the elements of the list are taken as the rules of the form <code>Rule[lhs, rhs]</code>
   * , otherwise the <code>astRules</code> itself is taken as the <code>Rule[lhs, rhs]</code>.
   *
   * @param astRules a possibly nested list of rules of the form <code>x-&gt;y</code> or <code>
   *     x:&gt;y</code>
   * @param engine the evaluation engine
   */
  public static Function<IExpr, IExpr> rules(IExpr astRules, EvalEngine engine) {
    final Map<IExpr, IExpr> equalRules;
    IAST rule;
    List<PatternMatcherAndEvaluator> matchers = new ArrayList<PatternMatcherAndEvaluator>();
    if (astRules.isList()) {
      return rulesFromNestedList((IAST) astRules, engine, matchers);
    } else {
      if (astRules.isRuleAST()) {
        rule = (IAST) astRules;
        equalRules = new OpenFixedSizeMap<IExpr, IExpr>(3);
        addRuleToCollection(equalRules, matchers, rule);
      } else {
        throw new ArgumentTypeException(
            "rule expression (x->y or x:>y) expected instead of " + astRules.toString());
      }
      if (matchers.size() > 0) {
        return new RulesPatternFunctor(equalRules, matchers, engine);
      }
      return equalRule(rule);
    }
  }

  /**
   * Create a functor from the given left-hand-side and right-hand-side AST expressions.
   * left-hand-side and right-hand-side must have equal number of arguments, otherwise only an empty
   * rule map is returned.
   *
   * @param lhsAST a list of left-hand-side expressions of the rules
   * @param rhsAST a list of right-hand-side expressions of the rules
   * @return a mapping function from lhs arguments to rhs arguments
   */
  public static Function<IExpr, IExpr> equalRules(IAST lhsAST, IAST rhsAST) {
    // final Map<IExpr, IExpr> equalRules;
    ImmutableMap.Builder<IExpr, IExpr> equalRules = ImmutableMap.builder();
    if (lhsAST.size() > 1 && lhsAST.size() == rhsAST.size()) {
      // int argsSize = lhsAST.argSize();
      // if (argsSize <= 5) {
      // equalRules = new OpenFixedSizeMap<IExpr, IExpr>(argsSize * 3 - 1);
      // } else {
      // equalRules = new HashMap<IExpr, IExpr>();
      // }
      for (int i = 1; i < lhsAST.size(); i++) {
        equalRules.put(lhsAST.get(i), rhsAST.get(i));
        // equalRules.put(lhsAST.get(i), rhsAST.get(i));
      }
      return rules(equalRules.build());
    }
    // equalRules = new HashMap<IExpr, IExpr>();
    return rules(equalRules.build());
  }

  private static Function<IExpr, IExpr> rulesFromNestedList(IAST astRules, EvalEngine engine,
      List<PatternMatcherAndEvaluator> matchers) {
    final Map<IExpr, IExpr> equalRules;
    IAST rule;
    if (astRules.size() > 1) {
      // assuming multiple rules in a list

      int argsSize = astRules.argSize();
      if (argsSize <= 5) {
        equalRules = new OpenFixedSizeMap<IExpr, IExpr>(argsSize * 3 - 1);
      } else {
        equalRules = new HashMap<IExpr, IExpr>();
      }

      for (final IExpr expr : astRules) {
        if (expr.isRuleAST()) {
          rule = (IAST) expr;
          addRuleToCollection(equalRules, matchers, rule);
        } else {
          if (astRules.isList()) {
            return rulesFromNestedList((IAST) expr, engine, matchers);
          } else {
            throw new ArgumentTypeException(
                "rule expression (x->y or x:>y) expected instead of " + expr.toString());
          }
        }
      }
      if (matchers.size() > 0) {
        return new RulesPatternFunctor(equalRules, matchers, engine);
      }
      if (argsSize == 1) {
        return equalRule((IAST) astRules.arg1());
      }
      return rules(equalRules);
    }
    equalRules = new HashMap<IExpr, IExpr>();
    return rules(equalRules);
  }

  public static Function<IExpr, IExpr> listRules(IAST astRules, IASTAppendable result,
      EvalEngine engine) {
    final Map<IExpr, IExpr> equalRules;
    List<PatternMatcherList> matchers = new ArrayList<PatternMatcherList>();
    if (astRules.isList()) {
      if (astRules.size() > 1) {
        // assuming multiple rules in a list
        IAST rule;
        int argsSize = astRules.argSize();
        if (argsSize <= 5) {
          equalRules = new OpenFixedSizeMap<IExpr, IExpr>(argsSize * 3 - 1);
        } else {
          equalRules = new HashMap<IExpr, IExpr>();
        }

        for (final IExpr expr : astRules) {
          if (expr.isRuleAST()) {
            rule = (IAST) expr;
            createPatternMatcherList(equalRules, matchers, rule);
          } else {
            throw new ArgumentTypeException(
                "rule expression (x->y or x:>y) expected instead of " + expr.toString());
          }
        }
      } else {
        equalRules = new HashMap<IExpr, IExpr>();
      }
    } else {
      if (astRules.isRuleAST()) {
        equalRules = new OpenFixedSizeMap<IExpr, IExpr>(3);
        createPatternMatcherList(equalRules, matchers, astRules);
      } else {
        throw new ArgumentTypeException(
            "rule expression (x->y or x:>y) expected instead of " + astRules.toString());
      }
    }
    if (matchers.size() > 0) {
      return new ListRulesPatternFunctor(equalRules, matchers, result, engine);
    }
    return listRules(equalRules, result);
  }

  public static Function<IExpr, IExpr> subsFunction(IAST rulesList) {
    return new SubsFunctor(rulesList);
  }

  public static SubsetPatternFunctor subsetRules(IAST astRules, EvalEngine engine) {
    List<PatternMatcherAndEvaluator> matchers = new ArrayList<PatternMatcherAndEvaluator>();
    if (astRules.isRuleAST()) {
      createPatternMatcherSubset(matchers, astRules);
    } else {
      throw new ArgumentTypeException(
          "rule expression (x->y or x:>y) expected instead of " + astRules.toString());
    }
    if (matchers.size() > 0) {
      return new SubsetPatternFunctor(matchers, engine);
    }
    return null;
  }

  public static Function<IExpr, IExpr> listRules(Map<IExpr, IExpr> rulesMap,
      IASTAppendable result) {
    return new ListRulesPatternFunctor(rulesMap, result);
  }

  /**
   * A predicate to determine if an expression is an instance of <code>IPattern</code> or <code>
   * IPatternSequence</code>.
   */
  private static Predicate<IExpr> PATTERNQ_PREDICATE = new Predicate<IExpr>() {
    @Override
    public boolean test(IExpr input) {
      return input.isBlank() || input.isPattern() || input.isPatternSequence(false)
          || input.isAlternatives() || input.isExcept();
    }
  };

  private static void addRuleToCollection(Map<IExpr, IExpr> equalRules,
      List<PatternMatcherAndEvaluator> matchers, IAST rule) {
    IExpr lhs = rule.arg1();
    final IExpr rhs = rule.arg2();

    if (lhs.isAST(S.HoldPattern, 2)) {
      lhs = lhs.first();
    }

    if (lhs.isFree(PATTERNQ_PREDICATE, true)) {
      IExpr temp = equalRules.get(lhs);
      if (temp == null) {
        if (lhs.isOrderlessAST() || lhs.isFlatAST()) {
          if (rule.isRuleDelayed()) {
            matchers.add(new PatternMatcherAndEvaluator(IPatternMatcher.SET_DELAYED, lhs, rhs));
          } else {
            matchers.add(
                new PatternMatcherAndEvaluator(IPatternMatcher.SET, lhs, evalOneIdentity(rhs)));
          }
          return;
        }
        equalRules.put(lhs, rhs);
      }
    } else {
      if (rule.isRuleDelayed()) {
        matchers.add(new PatternMatcherAndEvaluator(IPatternMatcher.SET_DELAYED, lhs, rhs));
      } else {
        matchers
            .add(new PatternMatcherAndEvaluator(IPatternMatcher.SET, lhs, evalOneIdentity(rhs)));
      }
    }
  }

  private static void createPatternMatcherList(Map<IExpr, IExpr> equalRules,
      List<PatternMatcherList> matchers, IAST rule) {
    if (rule.arg1().isFree(PATTERNQ_PREDICATE, true)) {
      IExpr temp = equalRules.get(rule.arg1());
      if (temp == null) {
        if (rule.arg1().isOrderlessAST() || rule.arg1().isFlatAST()) {
          if (rule.isRuleDelayed()) {
            matchers
                .add(new PatternMatcherList(IPatternMatcher.SET_DELAYED, rule.arg1(), rule.arg2()));
          } else {
            matchers.add(new PatternMatcherList(IPatternMatcher.SET, rule.arg1(),
                evalOneIdentity(rule.arg2())));
          }
          return;
        }
        equalRules.put(rule.arg1(), rule.arg2());
      }
    } else {
      if (rule.isRuleDelayed()) {
        matchers.add(new PatternMatcherList(IPatternMatcher.SET_DELAYED, rule.arg1(), rule.arg2()));
      } else {
        matchers.add(
            new PatternMatcherList(IPatternMatcher.SET, rule.arg1(), evalOneIdentity(rule.arg2())));
      }
    }
  }

  private static void createPatternMatcherSubset(List<PatternMatcherAndEvaluator> matchers,
      IAST rule) {
    if (rule.isRuleDelayed()) {
      matchers.add(
          new PatternMatcherAndEvaluator(IPatternMatcher.SET_DELAYED, rule.arg1(), rule.arg2()));
    } else {
      matchers.add(new PatternMatcherAndEvaluator(IPatternMatcher.SET, rule.arg1(),
          evalOneIdentity(rule.arg2())));
    }
  }

  /**
   * Test if <code>expr</code> is an <code>IAST</code> with one argument and the head symbol
   * contains the <code>OneIdentity</code> attribute.
   *
   * @param expr
   * @return
   */
  private static IExpr evalOneIdentity(IExpr expr) {
    if (expr.isAST()) {
      IAST arg2AST = (IAST) expr;
      if (arg2AST.isAST1() && arg2AST.head().isSymbol()) {
        if (((ISymbol) arg2AST.head()).hasOneIdentityAttribute()) {
          expr = arg2AST.arg1();
        }
      }
    }
    return expr;
  }

  private Functors() {}
}
