package org.matheclipse.core.patternmatching;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.ruleindex.SubstitutionPlanStats;

/**
 * A specialized <b>Pattern Matcher</b> that holds a Right-Hand Side (RHS) expression for immediate
 * substitution.
 * <p>
 * {@code PatternMatcherAndEvaluator} extends the standard {@link PatternMatcher} to represent a
 * complete transformation rule (e.g., {@code f[x_] := x^2} or {@code x_ -> 0}). While the parent
 * class is responsible for verifying if an input matches the <i>structure</i> of the Left-Hand Side
 * (LHS) and binding variables, this class stores the <b>Right-Hand Side (RHS)</b> to be evaluated
 * if that match succeeds.
 * </p>
 *
 * <h3>1. The Transformation Process</h3>
 * <p>
 * This class encapsulates the logic for the "Match & Replace" cycle:
 * </p>
 * <ol>
 * <li><b>Match (Inherited):</b> The input is tested against the LHS pattern using
 * {@link PatternMatcher#test(IExpr)}. If successful, variables are bound in the {@link IPatternMap}
 * (e.g., {@code x -> 5}).</li>
 * <li><b>Substitute:</b> The bound variables are substituted into the stored {@code fRightHandSide}
 * expression.</li>
 * <li><b>Evaluate:</b> The resulting expression is evaluated (if the rule is immediate) or returned
 * (if delayed) to produce the final result.</li>
 * </ol>
 *
 * <h3>2. Usage in Symja</h3>
 * <p>
 * This is the underlying engine for:
 * </p>
 * <ul>
 * <li><b>Function Definitions:</b> {@code SetDelayed[f[x_], body]} creates a
 * {@code PatternMatcherAndEvaluator} attached to the symbol {@code f}.</li>
 * <li><b>Replacement Rules:</b> {@code ReplaceAll[expr, lhs :> rhs]} uses this class to scan and
 * transform parts of {@code expr}.</li>
 * </ul>
 *
 * <h3>3. Usage Examples</h3>
 *
 * <h4>Defining a Transformation Rule</h4>
 * 
 * <pre>
 * // Represents the rule: f[x_] :> x^2
 * ISymbol f = F.Dummy("f");
 * ISymbol x = F.Dummy("x");
 *
 * // 1. LHS Pattern: f[x_]
 * IAST lhs = F.unary(f, F.$p(x, null));
 *
 * // 2. RHS Expression: x^2
 * IExpr rhs = F.Sqr(x);
 *
 * // 3. Create the evaluator
 * PatternMatcherAndEvaluator rule = new PatternMatcherAndEvaluator(lhs, rhs);
 *
 * // 4. Apply to input: f[4]
 * IExpr input = F.unary(f, F.C4);
 * if (rule.test(input)) {
 *   // The rule internally calculates the result during the test/replace phase
 *   // In a real scenario, you would use rule.replaceAll(input) or similar methods.
 * }
 * </pre>
 *
 * @see org.matheclipse.core.patternmatching.PatternMatcher
 * @see org.matheclipse.core.patternmatching.IPatternMap
 * @see org.matheclipse.core.expression.Pattern
 */
public class PatternMatcherAndEvaluator extends PatternMatcher implements Externalizable {

  private static final long serialVersionUID = 2241135467123931061L;

  /**
   * The right-hand-side expression of the pattern-matching rule.
   */
  protected IExpr fRightHandSide;

  /**
   * 
   */
  protected transient IExpr fReturnResult = F.NIL;

  /**
   * The substituted match after applying the pattern-matching rule.
   */
  protected transient IExpr fSubstitutedMatch = F.NIL;

  /**
   * Precompiled substitution of {@link #fRightHandSide}, built on the first substitution. All its
   * fields are final, so the benign race of two threads building it publishes safely.
   *
   * @see SubstitutionPlan
   */
  private transient SubstitutionPlan fRightHandSidePlan;

  /**
   * <code>true</code> if {@link SubstitutionPlan#build(IExpr, IPatternMap)} declined this
   * right-hand-side, so it is not attempted again.
   */
  private transient boolean fRightHandSidePlanRefused;

  /**
   * Substitute the pattern values into the right-hand-side of this rule.
   *
   * <p>
   * Uses a {@link SubstitutionPlan} compiled from the right-hand-side, which rebuilds only the
   * nodes on a path to a replaced symbol instead of walking the whole expression. Falls back to
   * {@link IPatternMap#substituteSymbols(IExpr, IExpr)} for a right-hand-side the plan builder does
   * not model.
   *
   * @param patternMap the values to substitute
   * @param nilOrEmptySequence value of an unassigned pattern, see
   *        {@link SubstitutionPlan#substitute(IPatternMap, IExpr)}
   */
  private IExpr substituteRightHandSide(IPatternMap patternMap, IExpr nilOrEmptySequence) {
    if (Config.SUBSTITUTION_PLAN && !fRightHandSidePlanRefused) {
      SubstitutionPlan plan = fRightHandSidePlan;
      if (plan == null) {
        // a benign race can compile this twice; SubstitutionPlan is deeply final, so either
        // instance publishes safely and both describe the same right-hand-side
        plan = SubstitutionPlan.build(fRightHandSide, patternMap);
        if (plan == null) {
          fRightHandSidePlanRefused = true;
        } else {
          fRightHandSidePlan = plan;
        }
      }
      if (plan != null) {
        IExpr planned = plan.substitute(patternMap, nilOrEmptySequence).orElse(fRightHandSide);
        if (Config.SUBSTITUTION_PLAN_VALIDATE) {
          // the generic result is the one which is returned, so an incorrect plan cannot change a
          // result while this mode is on
          IExpr generic = patternMap.substituteSymbols(fRightHandSide, nilOrEmptySequence);
          SubstitutionPlanStats.checked(SubstitutionPlan.equalWithFlags(planned, generic), planned,
              generic);
          return generic;
        }
        SubstitutionPlanStats.plannedSubstitution();
        return planned;
      }
    }
    SubstitutionPlanStats.genericSubstitution();
    return patternMap.substituteSymbols(fRightHandSide, nilOrEmptySequence);
  }

  public PatternMatcherAndEvaluator() {
    fRightHandSide = F.NIL;
    fSubstitutedMatch = F.NIL;
  }

  public PatternMatcherAndEvaluator(final IExpr leftHandSide, final IExpr rightHandSide) {
    this(SET_DELAYED, leftHandSide, rightHandSide);
  }

  public PatternMatcherAndEvaluator(final int setSymbol, final IExpr leftHandSide,
      final IExpr rightHandSide) {
    this(setSymbol, leftHandSide, rightHandSide, true, 0);
  }

  public PatternMatcherAndEvaluator(final int setSymbol, final IExpr leftHandSide,
      final IExpr rightHandSide, boolean initAll, int patternHash) {
    super(setSymbol, leftHandSide, initAll);
    fRightHandSide = rightHandSide;
    fPatterHash = patternHash;
    fPatternMap = createPatternMap();
  }

  @Override
  public final boolean isPatternHashAllowed(int patternHash) {
    return fPatterHash == 0 || fPatterHash == patternHash;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    PatternMatcherAndEvaluator v = (PatternMatcherAndEvaluator) super.clone();
    v.fRightHandSide = fRightHandSide;
    v.fReturnResult = F.NIL;
    if (fPatternMap != null) {
      v.fPatternMap = fPatternMap.copy();
    }
    return v;
  }

  @Override
  public IPatternMatcher copy() {
    PatternMatcherAndEvaluator v = new PatternMatcherAndEvaluator();
    copyEvaluatorFieldsTo(v);
    return v;
  }

  /**
   * Copy all fields except the per-match state (<code>fReturnResult</code>,
   * <code>fSubstitutedMatch</code>) into <code>v</code>.
   */
  protected final void copyEvaluatorFieldsTo(PatternMatcherAndEvaluator v) {
    copyBaseFieldsTo(v);
    v.fRightHandSide = fRightHandSide;
    // the plan depends only on the right-hand-side and the slot layout, and IPatternMap#copy()
    // preserves that layout - so the copy can use the same plan
    v.fRightHandSidePlan = fRightHandSidePlan;
    v.fRightHandSidePlanRefused = fRightHandSidePlanRefused;
    // like in clone() - fReturnResult is per-match state and must not leak into the copy
    v.fReturnResult = F.NIL;
  }

  @Override
  public boolean checkRHSCondition(EvalEngine engine) {
    IPatternMap patternMap = createPatternMap();
    if (patternMap.getRHSEvaluated()) {
      return true;
    }
    if (!(fRightHandSide.isCondition() || fRightHandSide.isBlockModuleOrWithCondition())) {
      return true;
    }
    if (!patternMap.isAllPatternsAssigned()) {
      return true;
    }

    // from here on the guard is really evaluated: the right-hand-side is substituted and run
    org.matheclipse.core.patternmatching.ruleindex.RuleDispatchStats.rhsConditionEvaluation();
    boolean matched = false;
    IEvalStepListener stepListener = engine.getStepListener();
    final boolean isTraceMode =
        Config.TRACE_REWRITE_RULE && engine.isTraceMode() && stepListener != null;

    // Note: substituting only the `test` of a `body /; test` right-hand-side first, and the body
    // only once the test holds, was implemented and measured here. 95% of the conditions of the
    // Rubi rules fail, so it does remove real work - but it changed the wall clock by less than the
    // run to run drift. The reason is that the time "inside checkRHSCondition" is dominated by the
    // *successful* conditions, whose body evaluation recursively integrates again; the substitution
    // of a body which is then thrown away is a small slice. Do not re-add it without a measurement
    // which separates the recursive evaluation from the local substitution.
    IExpr rhs = substituteRightHandSide(patternMap, F.CEmptySequence);
    engine.pushOptionsStack();
    try {
      engine.setOptionsPattern(fLhsPatternExpr.topHead(), patternMap);
      if (isTraceMode) {
        IExpr lhs = getLHSExprToMatch();
        if (lhs.isPresent()) {
          stepListener.setUp(lhs, 0, lhs);
          try {
            fReturnResult =
                engine.addEvaluatedTraceStep(lhs, rhs, lhs.topHead(), F.$str("RewriteRule"));
          } finally {
            stepListener.tearDown(F.NIL, 0, true, lhs);
          }
        } else {
          fReturnResult = rhs.eval(engine);
        }
      } else {
        fReturnResult = rhs.eval(engine);
      }
      matched = true;
    } catch (final ConditionException e) {
      matched = false;
    } catch (final ReturnException e) {
      fReturnResult = e.getValue();
      matched = true;
    } finally {
      engine.popOptionsStack();
    }
    patternMap.setRHSEvaluated(matched);
    if (matched) {
      org.matheclipse.core.patternmatching.ruleindex.RuleDispatchStats.rhsConditionHeld();
    }
    return matched;
  }

  @Override
  public final IExpr eval(final IExpr leftHandSide, EvalEngine engine) {
    return replaceEvaled(leftHandSide, engine);
  }

  public IExpr replace(final IExpr leftHandSide, EvalEngine engine) {
    return replaceInternal(leftHandSide, engine, false);
  }

  public IExpr replaceEvaled(final IExpr leftHandSide, EvalEngine engine) {
    return replaceInternal(leftHandSide, engine, true);
  }

  private IExpr replaceInternal(final IExpr leftHandSide, EvalEngine engine, boolean evaluate) {
    IPatternMap patternMap = null;

    // reset per-match state; this matcher instance may be reused for several expressions (for
    // example the PatternMatcherList instances in Functors are applied without copy()), and a
    // fReturnResult of a previous match would short-circuit replacePatternMatch()
    fReturnResult = F.NIL;

    if (isRuleWithoutPatterns()) {
      if (fLhsPatternExpr.equals(leftHandSide)) {
        return replaceEqualMatch(leftHandSide, engine, evaluate);
      }
      if (!(fLhsPatternExpr.isOrderlessAST() && leftHandSide.isOrderlessAST())
          && !(fLhsPatternExpr.isFlatAST() && leftHandSide.isFlatAST())) {
        return F.NIL;
      }
      if (fLhsPatternExpr.size() == leftHandSide.size()) {
        return F.NIL;
      }
    } else {
      patternMap = createPatternMap();
      patternMap.initPattern();
      setLHSExprToMatch(leftHandSide);
      if (matchExpr(fLhsPatternExpr, leftHandSide, engine, new StackMatcher(engine))) {
        return replacePatternMatch(leftHandSide, patternMap, engine, evaluate);
      }
    }

    if (fLhsPatternExpr.isASTOrAssociation() && leftHandSide.isASTOrAssociation()) {
      return replaceSubExpressionOrderlessFlat((IAST) fLhsPatternExpr, (IAST) leftHandSide,
          fRightHandSide, engine);
    }
    return F.NIL;
  }

  /**
   * A match which contains a pattern was found.
   *
   * <p>
   * Assumption <code>
   * matchExpr(fLhsPatternExpr, leftHandSide, engine, new StackMatcher(engine)) == true</code>.
   *
   * @param leftHandSide
   * @param patternMap
   * @param engine
   */
  public IExpr replacePatternMatch(final IExpr leftHandSide, IPatternMap patternMap,
      EvalEngine engine, boolean evaluate) {
    if (fReturnResult.isPresent()) {
      if (isFlagOn(IPatternMatcher.SET_DELAYED)) {
        boolean oldEvalRHSMode = engine.isEvalRHSMode();
        try {
          engine.setEvalRHSMode(true);
          IExpr temp = fReturnResult.eval(engine);
          return temp;
        } catch (ConditionException cex) {
          return F.NIL;
        } finally {
          engine.setEvalRHSMode(oldEvalRHSMode);
        }
      }
      return fReturnResult;
    }

    boolean oldEvalRHSMode = engine.isEvalRHSMode();
    engine.pushOptionsStack();
    try {
      engine.setEvalRHSMode(true);
      if (fLhsPatternExpr != null) {
        engine.setOptionsPattern(fLhsPatternExpr.topHead(), patternMap);
      }
      if (fRightHandSide == DUMMY_SUBSET_CASES) {
        fSubstitutedMatch = patternMap.substitutePatterns(fLhsPatternExpr, F.CEmptySequence);
      } else {
        fSubstitutedMatch = substituteRightHandSide(patternMap, F.CEmptySequence);
      }
      IExpr result = fSubstitutedMatch;
      if (evaluate) {
        if (Config.TRACE_REWRITE_RULE) {
          return engine.addEvaluatedTraceStep(leftHandSide, result, leftHandSide.topHead(),
              F.$str("RewriteRule"));
        }
        return result.eval(engine);
      } else {
        return result;
      }
    } catch (final ConditionException e) {
      return F.NIL;
    } catch (final ReturnException e) {
      IExpr result = e.getValue();
      if (evaluate) {
        if (Config.TRACE_REWRITE_RULE) {
          return engine.addEvaluatedTraceStep(leftHandSide, result, leftHandSide.topHead(),
              F.$str("RewriteRule"));
        }
        return result.eval(engine);
      }
      return result;
    } finally {
      engine.popOptionsStack();
      engine.setEvalRHSMode(oldEvalRHSMode);
    }
  }

  /**
   * A match which contains no pattern was found.
   *
   * <p>
   * Assumption <code>fLhsPatternExpr.equals(leftHandSide) == true</code>
   *
   * @param leftHandSide
   * @param engine
   * @param evaluate
   */
  private IExpr replaceEqualMatch(final IExpr leftHandSide, EvalEngine engine, boolean evaluate) {
    IExpr result = fRightHandSide;
    try {
      if (evaluate) {
        if (Config.TRACE_REWRITE_RULE) {
          return engine.addEvaluatedTraceStep(leftHandSide, result, leftHandSide.topHead(),
              F.$str("RewriteRule"));
        }
        return result.eval(engine);
      }
      return result;
    } catch (final ConditionException e) {
      return F.NIL;
    } catch (final ReturnException e) {
      result = e.getValue();
      if (evaluate) {
        if (Config.TRACE_REWRITE_RULE) {
          return engine.addEvaluatedTraceStep(leftHandSide, result, leftHandSide.topHead(),
              F.$str("RewriteRule"));
        }
        return result.eval(engine);
      }
      return result;
    }
  }

  @Override
  public final IExpr getRHS() {
    return IExpr.ofNullable(fRightHandSide);
  }

  public final IExpr getSubstitutedMatch() {
    return fSubstitutedMatch;
  }

  @Override
  public String toString() {
    if (fPatternMap == null) {
      return getAsAST().toString();
    }
    return fPatternMap.toString() + "\n" + getAsAST().toString();
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeShort((short) fSetFlags);
    objectOutput.writeObject(fLhsPatternExpr);
    objectOutput.writeObject(fRightHandSide);
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    // mask the sign extension - the flags were written with writeShort()
    fSetFlags = objectInput.readShort() & 0xFFFF;
    fLhsPatternExpr = (IExpr) objectInput.readObject();
    fRightHandSide = (IExpr) objectInput.readObject();
    if (fLhsPatternExpr != null) {
      // also restores fLHSPriority, which was formerly left at 0 - a deserialized rule would sort
      // in front of all other rules and compare wrongly in equivalentTo()
      initPriorityFromLhs();
      // restore the pattern hash pre-filter with the same rules as
      // RulesData#putDownRule(int, boolean, IExpr, IExpr, int)
      if (fLhsPatternExpr.isAST() && !RulesData.isComplicatedPatternRule(fLhsPatternExpr)
          && !fLhsPatternExpr.isCondition()) {
        fPatterHash = ((IAST) fLhsPatternExpr).patternHashCode();
      }
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((fRightHandSide == null) ? 0 : fRightHandSide.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    PatternMatcherAndEvaluator other = (PatternMatcherAndEvaluator) obj;
    if (fRightHandSide == null) {
      return other.fRightHandSide == null;
    } else {
      return fRightHandSide.equals(other.fRightHandSide);
    }
  }
}
