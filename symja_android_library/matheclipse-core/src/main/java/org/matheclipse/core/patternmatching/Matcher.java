package org.matheclipse.core.patternmatching;

import java.util.function.Function;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
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
      final IExpr temp = matcher.apply(list);
      if (temp.isPresent()) {
        if (temp.isASTOrAssociation()) {
          list = (IAST) temp;
          evaled = true;
        } else {
          return temp;
        }
      }
      IASTMutable result = F.NIL;
      int i = 1;
      while (i < list.size()) {
        final IExpr childEval = list.get(i).accept(this);
        if (childEval.isPresent()) {
          // something was evaluated - return a new IAST:
          result = list.copy();
          result.set(i++, childEval);
          break;
        }
        i++;
      }
      if (result.isPresent()) {
        while (i < list.size()) {
          final IExpr childEval = list.get(i).accept(this);
          if (childEval.isPresent()) {
            result.set(i, childEval);
          }
          i++;
        }
        return result;
      }
      return evaled ? list : F.NIL;
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

  /**
   * A rule whose right-hand-side is a Java function of the single matched pattern value.
   */
  private static final class PatternMatcherFunctionMethod extends PatternMatcher {
    private static final long serialVersionUID = 3452587395234693418L;

    private final Function<IExpr, IExpr> fRightHandSide;

    PatternMatcherFunctionMethod(final IExpr leftHandSide,
        final Function<IExpr, IExpr> rightHandSide) {
      super(leftHandSide);
      fRightHandSide = rightHandSide;
    }

    @Override
    public IPatternMatcher copy() {
      PatternMatcherFunctionMethod v =
          new PatternMatcherFunctionMethod(fLhsPatternExpr, fRightHandSide);
      copyBaseFieldsTo(v);
      return v;
    }

    @Override
    public IExpr eval(final IExpr leftHandSide, EvalEngine engine) {
      if (isRuleWithoutPatterns()) {
        return fLhsPatternExpr.equals(leftHandSide) ? evalMethod() : F.NIL;
      }
      IPatternMap patternMap = createPatternMap();
      patternMap.initPattern();
      return matchExpr(fLhsPatternExpr, leftHandSide, engine) ? evalMethod() : F.NIL;
    }

    private IExpr evalMethod() {
      try {
        return fRightHandSide.apply(createPatternMap().getValue(0));
      } catch (final ConditionException e) {
        return F.NIL;
      } catch (final ReturnException e) {
        return e.getValue();
      }
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
   * If this rule matches the evaluation will return the result of the <code>function.apply()</code>
   * method, called with the value of the (single) pattern of the left-hand-side.
   *
   * @param patternMatchingRule the left-hand-side pattern with exactly one pattern object
   * @param function the function which computes the result from the matched value
   */
  public void caseOf(final IExpr patternMatchingRule, final Function<IExpr, IExpr> function) {
    rules.insertMatcher(new PatternMatcherFunctionMethod(patternMatchingRule, function));
  }

  /**
   * Method called in order to add a new pattern-matching rule to this rule-set.
   *
   * @param patternMatchingRule the pattern-matching rule
   * @param resultExpr the result expression which should be returned if the pattern-matching rule
   *        matches an expression in the apply method.
   * @return a
   */
  public void caseOf(final IExpr patternMatchingRule, final IExpr resultExpr) {
    if (patternMatchingRule.isPresent()) {
      rules.putDownRule(patternMatchingRule, resultExpr);
      return;
    }
    // unexpected NIL expression encountered.
    String str = Errors.getMessage("nil", F.CEmptyList, EvalEngine.get());
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
