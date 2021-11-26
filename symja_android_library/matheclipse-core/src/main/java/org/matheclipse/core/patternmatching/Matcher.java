package org.matheclipse.core.patternmatching;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.AbstractVisitor;

/**
 * The Matcher defines a pattern matching rule set. The matcher doesn't try to match numbers or
 * strings.
 */
public class Matcher implements Function<IExpr, IExpr> {
  private static class MatcherVisitor extends AbstractVisitor {
    final Matcher matcher;
    final Function<IAST, IExpr> function;

    public MatcherVisitor(Matcher matcher, Function<IAST, IExpr> function) {
      this.matcher = matcher;
      this.function = function;
    }

    @Override
    public IExpr visit(IASTMutable ast) {
      IAST list = ast;
      if (function != null) {
        IExpr temp = function.apply(list);
        if (temp.isPresent()) {
          return temp;
        }
      }
      boolean evaled = false;
      IExpr temp = matcher.apply(list);
      if (temp.isPresent()) {
        if (temp.isASTOrAssociation()) {
          list = (IAST) temp;
          evaled = true;
        } else {
          return temp;
        }
      }
      IASTAppendable result = F.NIL;
      int i = 1;
      while (i < list.size()) {
        temp = list.get(i).accept(this);
        if (temp.isPresent()) {
          // something was evaluated - return a new IAST:
          result = list.copyAppendable();
          for (int j = 1; j < i; j++) {
            result.set(j, list.get(j));
          }
          result.set(i++, temp);
          break;
        }
        i++;
      }
      if (result.isPresent()) {
        while (i < list.size()) {
          temp = list.get(i).accept(this);
          if (temp.isPresent()) {
            result.set(i, temp);
          } else {
            result.set(i, list.get(i));
          }
          i++;
        }
      }
      if (result.isPresent()) {
        return result;
      }
      if (evaled) {
        return list;
      }
      return F.NIL;
    }

    @Override
    public IExpr visit(IPattern element) {
      return matcher.apply(element);
    }

    @Override
    public IExpr visit(IPatternSequence element) {
      return matcher.apply(element);
    }

    @Override
    public IExpr visit(ISymbol element) {
      return matcher.apply(element);
    }
  }

  private static class PatternMatcherBiFunctionMethod extends AbstractPatternMatcherMethod {
    BiFunction<IExpr, IExpr, IExpr> fRightHandSide;

    public PatternMatcherBiFunctionMethod(
        final IExpr leftHandSide, final BiFunction<IExpr, IExpr, IExpr> rightHandSide) {
      super(leftHandSide);
      fRightHandSide = rightHandSide;
    }

    @Override
    IExpr evalMethod() {
      IPatternMap pm = createPatternMap();
      IExpr arg1 = pm.getValue(0);
      IExpr arg2 = pm.getValue(1);
      return fRightHandSide.apply(arg1, arg2);
    }
  }

  private static class PatternMatcherBiPredicateMethod extends AbstractPatternMatcherMethod {
    BiPredicate<IExpr, IExpr> fRightHandSide;

    public PatternMatcherBiPredicateMethod(
        final IExpr leftHandSide, final BiPredicate<IExpr, IExpr> rightHandSide) {
      super(leftHandSide);
      fRightHandSide = rightHandSide;
    }

    @Override
    IExpr evalMethod() {
      IPatternMap pm = createPatternMap();
      IExpr arg1 = pm.getValue(0);
      IExpr arg2 = pm.getValue(1);
      return fRightHandSide.test(arg1, arg2) ? S.True : S.False;
    }
  }

  private static class PatternMatcherFunctionMethod extends AbstractPatternMatcherMethod {
    Function<IExpr, IExpr> fRightHandSide;

    public PatternMatcherFunctionMethod(
        final IExpr leftHandSide, final Function<IExpr, IExpr> rightHandSide) {
      super(leftHandSide);
      fRightHandSide = rightHandSide;
    }

    @Override
    IExpr evalMethod() {
      IPatternMap pm = createPatternMap();
      IExpr arg1 = pm.getValue(0);
      return fRightHandSide.apply(arg1);
    }
  }

  private static class PatternMatcherMapMethod extends AbstractPatternMatcherMethod {
    final IPatternMethod fRightHandSide;

    public PatternMatcherMapMethod(final IExpr leftHandSide, final IPatternMethod rightHandSide) {
      super(leftHandSide);
      fRightHandSide = rightHandSide;
    }

    @Override
    IExpr evalMethod() {
      IPatternMap pm = createPatternMap();
      return fRightHandSide.eval(pm);
    }
  }

  private static class PatternMatcherPredicateMethod extends AbstractPatternMatcherMethod {
    Predicate<IExpr> fRightHandSide;

    public PatternMatcherPredicateMethod(
        final IExpr leftHandSide, final Predicate<IExpr> rightHandSide) {
      super(leftHandSide);
      fRightHandSide = rightHandSide;
    }

    @Override
    IExpr evalMethod() {
      IPatternMap pm = createPatternMap();
      IExpr arg1 = pm.getValue(0);
      return fRightHandSide.test(arg1) ? S.True : S.False;
    }
  }

  /** The rule set */
  private RulesData rules;

  /** The Matcher constructor */
  public Matcher() {
    this.rules = new RulesData();
  }

  /**
   * Main method performing the pattern matching.
   *
   * @param expression the object to be matched
   * @return a computation result done by an accepted rule during pattern matching process or <code>
   *     F.NIL</code> if no evaluation was possible
   */
  @Override
  public IExpr apply(IExpr expression) {
    return rules.evalDownRule(expression, EvalEngine.get());
  }

  /**
   * If this rule matches the evaluation will return <code>S.True</code> or <code>S.False</code>
   * depending on the <code>predicates</code> result.
   *
   * @param patternMatchingRule
   * @param predicate
   */
  public void caseBoole(
      final IExpr patternMatchingRule, final BiPredicate<IExpr, IExpr> predicate) {
    rules.insertMatcher(new PatternMatcherBiPredicateMethod(patternMatchingRule, predicate));
  }

  /**
   * If this rule matches the evaluation will return <code>S.True</code> or <code>S.False</code>
   * depending on the <code>predicates</code> result.
   *
   * @param patternMatchingRule
   * @param predicate
   */
  public void caseBoole(final IExpr patternMatchingRule, final Predicate<IExpr> predicate) {
    rules.insertMatcher(new PatternMatcherPredicateMethod(patternMatchingRule, predicate));
  }

  /**
   * If this rule matches the evaluation will return the result of the <code>method.eval()</code>
   * method.
   *
   * @param patternMatchingRule
   * @param method
   */
  public void caseMethod(final IExpr patternMatchingRule, final IPatternMethod method) {
    rules.insertMatcher(new PatternMatcherMapMethod(patternMatchingRule, method));
  }

  /**
   * If this rule matches the evaluation will return the result of the <code>function.apply()</code>
   * method.
   *
   * @param patternMatchingRule
   * @param function
   */
  public void caseOf(
      final IExpr patternMatchingRule, final BiFunction<IExpr, IExpr, IExpr> function) {
    rules.insertMatcher(new PatternMatcherBiFunctionMethod(patternMatchingRule, function));
  }

  /**
   * If this rule matches the evaluation will return the result of the <code>function.apply()</code>
   * method.
   *
   * @param patternMatchingRule
   * @param function
   */
  public void caseOf(final IExpr patternMatchingRule, final Function<IExpr, IExpr> function) {
    rules.insertMatcher(new PatternMatcherFunctionMethod(patternMatchingRule, function));
  }

  /**
   * Method called in order to add a new pattern-matching rule to this rule-set.
   *
   * @param patternMatchingRule the pattern-matching rule
   * @param resultExpr the result expression which should be returned if the pattern-matching rule
   *     matches an expression in the apply method.
   * @return a
   */
  public void caseOf(final IExpr patternMatchingRule, final IExpr resultExpr) {
    if (patternMatchingRule.isPresent()) {
      rules.putDownRule(patternMatchingRule, resultExpr);
      return;
    }
    // unexpected NIL expression encountered.
    String str = IOFunctions.getMessage("nil", F.CEmptyList, EvalEngine.get());
    throw new ArgumentTypeException(str);
  }

  /**
   * Replace all (sub-) expressions with the given rule set. If no substitution matches, the method
   * returns the given <code>expression</code>.
   *
   * @param expression
   * @return <code>F.NIL</code> if no rule of the rule set matched an expression.
   */
  public IExpr replaceAll(IExpr expression) {
    return replaceAll(expression, null);
  }

  /**
   * Replace all (sub-) expressions with the given rule set. If no substitution matches, the method
   * returns the given <code>expression</code>.
   *
   * @param expression
   * @param function if not <code>null</code> evaluate before the rules apply
   * @return <code>F.NIL</code> if no rule of the rule set matched an expression.
   */
  public IExpr replaceAll(IExpr expression, Function<IAST, IExpr> function) {
    return expression.accept(new MatcherVisitor(this, function));
  }
}
