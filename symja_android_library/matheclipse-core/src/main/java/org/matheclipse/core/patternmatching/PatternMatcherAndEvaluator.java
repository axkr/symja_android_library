package org.matheclipse.core.patternmatching;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.GenericPair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

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
    v.fLHSPriority = fLHSPriority;
    v.fThrowIfTrue = fThrowIfTrue;
    v.fLhsPatternExpr = fLhsPatternExpr;
    if (fPatternMap != null) {
      v.fPatternMap = fPatternMap.copy();
    }
    v.fLhsExprToMatch = fLhsExprToMatch;
    v.fSetFlags = fSetFlags;
    v.fRightHandSide = fRightHandSide;
    v.fReturnResult = fReturnResult;
    return v;
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

    boolean matched = false;
    IExpr rhs = patternMap.substituteSymbols(fRightHandSide, F.CEmptySequence);
    engine.pushOptionsStack();
    IEvalStepListener stepListener = engine.getStepListener();
    final boolean isTraceMode =
        Config.TRACE_REWRITE_RULE && engine.isTraceMode() && stepListener != null;
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
    return matched;
  }

  @Override
  public final IExpr eval(final IExpr leftHandSide, EvalEngine engine) {
    return replaceEvaled(leftHandSide, engine);
  }

  public static IExpr evalInternal(final IExpr leftHandSide, final IExpr rightHandSide,
      List<GenericPair<IExpr, ISymbol>> patternIndexMap) {
    PatternMatcherAndEvaluator pm = new PatternMatcherAndEvaluator();
    IPatternMap patternMap = IPatternMap.createSymbolToValueMap(patternIndexMap);
    pm.fPatternMap = patternMap;
    pm.fRightHandSide = rightHandSide;
    pm.setLHSExprToMatch(leftHandSide);
    return pm.replacePatternMatch(leftHandSide, patternMap, EvalEngine.get(), true);
  }

  public IExpr replace(final IExpr leftHandSide, EvalEngine engine) {
    return replaceInternal(leftHandSide, engine, false);
  }

  public IExpr replaceEvaled(final IExpr leftHandSide, EvalEngine engine) {
    return replaceInternal(leftHandSide, engine, true);
  }

  private IExpr replaceInternal(final IExpr leftHandSide, EvalEngine engine, boolean evaluate) {
    IPatternMap patternMap = null;

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
   *  Match the left-hand-side first argument with the pattern-matching rules first argument.
   * <p>
   * 
   * @param leftHandSide
   * @param patternMap
   * @param engine
   */
  IExpr matchIntegrateFunction(final IExpr leftHandSide, IPatternMap patternMap,
      EvalEngine engine) {
    IExpr LhsPatternFunction = fLhsPatternExpr.first();
    setLHSExprToMatch(LhsPatternFunction);
    if (matchExpr(LhsPatternFunction, leftHandSide.first(), engine, new StackMatcher(engine))) {
      return replacePatternMatch(leftHandSide, patternMap, engine, true);
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
        fSubstitutedMatch = patternMap.substituteSymbols(fRightHandSide, F.CEmptySequence);
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
      if (Config.TRACE_REWRITE_RULE) {
        return engine.addEvaluatedTraceStep(leftHandSide, result, leftHandSide.topHead(),
            F.$str("RewriteRule"));
      }
      return result.eval(engine);
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
  public IAST getAsAST() {
    ISymbol setSymbol = getSetSymbol();
    IAST temp = F.binaryAST2(setSymbol, getLHS(), getRHS());
    if (isFlagOn(HOLDPATTERN)) {
      return F.HoldPattern(temp);
    }
    if (isFlagOn(LITERAL)) {
      return F.Literal(temp);
    }
    return temp;
  }

  @Override
  public ISymbol getSetSymbol() {
    if (isFlagOn(SET_DELAYED))
      return S.SetDelayed;
    if (isFlagOn(SET))
      return S.Set;
    if (isFlagOn(UPSET_DELAYED))
      return S.UpSetDelayed;
    if (isFlagOn(UPSET))
      return S.UpSet;
    if (isFlagOn(TAGSET_DELAYED))
      return S.TagSetDelayed;
    if (isFlagOn(TAGSET))
      return S.TagSet;
    return null;
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
    fSetFlags = objectInput.readShort();
    fLhsPatternExpr = (IExpr) objectInput.readObject();
    fRightHandSide = (IExpr) objectInput.readObject();
    if (fLhsPatternExpr != null) {
      int[] priority = new int[] {IPatternMap.DEFAULT_RULE_PRIORITY};
      this.fPatternMap = IPatternMap.determinePatterns(fLhsPatternExpr, priority, null);
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
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    PatternMatcherAndEvaluator other = (PatternMatcherAndEvaluator) obj;
    if (fRightHandSide == null) {
      return other.fRightHandSide == null;
    } else {
      return fRightHandSide.equals(other.fRightHandSide);
    }
  }
}
