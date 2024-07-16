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
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherAndEvaluator;
import org.matheclipse.core.patternmatching.PatternMatcherList;

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
   * @return
   */
  public static Function<IExpr, IExpr> rules(IAST astRules, EvalEngine engine) {
    final Map<IExpr, IExpr> equalRules;
    IAST rule;
    List<PatternMatcherAndEvaluator> matchers = new ArrayList<PatternMatcherAndEvaluator>();
    if (astRules.isList()) {
      return rulesFromNestedList(astRules, engine, matchers);
    } else {
      if (astRules.isRuleAST()) {
        rule = astRules;
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
    final Map<IExpr, IExpr> equalRules;
    if (lhsAST.size() > 1 && lhsAST.size() == rhsAST.size()) {
      int argsSize = lhsAST.argSize();
      if (argsSize <= 5) {
        equalRules = new OpenFixedSizeMap<IExpr, IExpr>(argsSize * 3 - 1);
      } else {
        equalRules = new HashMap<IExpr, IExpr>();
      }
      for (int i = 1; i < lhsAST.size(); i++) {
        equalRules.put(lhsAST.get(i), rhsAST.get(i));
      }
      return rules(equalRules);
    }
    equalRules = new HashMap<IExpr, IExpr>();
    return rules(equalRules);
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
