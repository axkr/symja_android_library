package org.matheclipse.core.patternmatching;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class PatternMatcherAndEvaluator extends PatternMatcher implements Externalizable {
  private static final Logger LOGGER = LogManager.getLogger();

  /** */
  private static final long serialVersionUID = 2241135467123931061L;

  protected IExpr fRightHandSide;

  protected transient IExpr fReturnResult = F.NIL;

  private int fSetFlags;

  /** Public constructor for serialization. */
  public PatternMatcherAndEvaluator() {}

  /**
   * Define a pattern-matching rule.
   *
   * @param leftHandSide could contain pattern expressions for "pattern-matching"
   * @param rightHandSide the result which should be evaluated if the "pattern-matching" succeeds
   */
  public PatternMatcherAndEvaluator(final IExpr leftHandSide, final IExpr rightHandSide) {
    this(SET_DELAYED, leftHandSide, rightHandSide);
  }

  /**
   * Define a pattern-matching rule.
   *
   * @param setSymbol the flags for the symbol which defines this pattern-matching rule (i.e. Set,
   *     SetDelayed,...)
   * @param leftHandSide could contain pattern expressions for "pattern-matching"
   * @param rightHandSide the result which should be evaluated if the "pattern-matching" succeeds
   */
  public PatternMatcherAndEvaluator(
      final int setSymbol, final IExpr leftHandSide, final IExpr rightHandSide) {
    this(setSymbol, leftHandSide, rightHandSide, true, 0);
  }

  public PatternMatcherAndEvaluator(
      final int setSymbol,
      final IExpr leftHandSide,
      final IExpr rightHandSide,
      boolean initAll,
      int patternHash) {
    super(leftHandSide, initAll);
    fSetFlags = setSymbol;
    fRightHandSide = rightHandSide;
    fPatterHash = patternHash;
    //    if (initAll) {
    //      initRHSleafCountSimplify();
    //    }
  }

  /**
   * Are the given flags disabled ?
   *
   * @param flags
   * @return
   * @see IAST#NO_FLAG
   */
  public final boolean isFlagOff(final int flags) {
    return (fSetFlags & flags) == 0;
  }

  /**
   * Are the given flags enabled ?
   *
   * @param flags
   * @return
   * @see IAST#NO_FLAG
   */
  public final boolean isFlagOn(int flags) {
    return (fSetFlags & flags) == flags;
  }

  /**
   * Check if <code>fPatterHash == 0 || fPatterHash == patternHash;</code>.
   *
   * @param patternHash
   * @return
   */
  @Override
  public boolean isPatternHashAllowed(int patternHash) {
    return fPatterHash == 0 || fPatterHash == patternHash;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    PatternMatcherAndEvaluator v = (PatternMatcherAndEvaluator) super.clone();
    v.fRightHandSide = fRightHandSide;
    v.fSetFlags = fSetFlags;
    v.fReturnResult = F.NIL;
    return v;
  }

  /**
   * Check if the two expressions are equivalent. (i.e. <code>f[x_,y_]</code> is equivalent to
   * <code>f[a_,b_]</code> )
   *
   * @param patternExpr1
   * @param patternExpr2
   * @param pm1
   * @param pm2
   * @return
   */
  private static int equivalentRHS(
      final IExpr patternExpr1,
      final IExpr patternExpr2,
      final IPatternMap pm1,
      final IPatternMap pm2) {
    IExpr p1, p2;
    if (patternExpr1.isCondition()) {
      p1 = patternExpr1.second();
      if (patternExpr2.isCondition()) {
        p2 = patternExpr2.second();
        if (equivalent(p1, p2, pm1, pm2)) {
          return 0;
        }
        return p1.compareTo(p2);
      } else if (patternExpr2.isModuleOrWithCondition()) {
        p2 = patternExpr2.last().second();
        if (equivalent(p1, p2, pm1, pm2)) {
          return 0;
        }
        return p1.compareTo(p2);
      }
    } else if (patternExpr1.isModuleOrWithCondition()) {
      p1 = patternExpr1.last().second();
      if (patternExpr2.isCondition()) {
        p2 = patternExpr2.second();
        if (equivalent(p1, p2, pm1, pm2)) {
          return 0;
        }
        return p1.compareTo(p2);
      } else if (patternExpr2.isModuleOrWithCondition()) {
        p2 = patternExpr2.last().second();
        if (equivalent(p1, p2, pm1, pm2)) {
          return 0;
        }
        return p1.compareTo(p2);
      }
    }
    return 0;
  }

  /**
   * Add a flag to the existing ones.
   *
   * @param i
   */
  public final void addFlags(final int i) {
    fSetFlags |= i;
  }

  /**
   * Check if the condition for the right-hand-sides <code>Module[], With[] or Condition[]</code>
   * expressions evaluates to <code>true</code>.
   *
   * @return <code>true</code> if the right-hand-sides condition is fulfilled or not all patterns
   *     are assigned.
   */
  @Override
  public boolean checkRHSCondition(EvalEngine engine) {
    IPatternMap patternMap = createPatternMap();
    if (patternMap.getRHSEvaluated()) {
      return true;
    }

    if (!(fRightHandSide.isCondition() || fRightHandSide.isModuleOrWithCondition())) {
      return true;
    } else {
      if (!patternMap.isAllPatternsAssigned()) {
        return true;
      } else {
        boolean matched = false;
        IExpr rhs = patternMap.substituteSymbols(fRightHandSide, F.CEmptySequence);

        engine.pushOptionsStack();
        IEvalStepListener stepListener = engine.getStepListener();
        try {
          engine.setOptionsPattern(fLhsPatternExpr.topHead(), patternMap);
          if (Config.TRACE_REWRITE_RULE && stepListener != null) {
            IExpr lhs = getLHSExprToMatch();
            if (lhs.isPresent()) {
              stepListener.setUp(lhs, 0);
              fReturnResult =
                  engine.addEvaluatedTraceStep(lhs, rhs, lhs.topHead(), F.$str("RewriteRule"));
            } else {
              fReturnResult = engine.evaluate(rhs);
            }
          } else {
            fReturnResult = engine.evaluate(rhs);
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
        if (Config.TRACE_REWRITE_RULE && stepListener != null) {
          stepListener.tearDown(0, matched);
        }
        patternMap.setRHSEvaluated(matched);
        return matched;
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public IExpr eval(final IExpr leftHandSide, EvalEngine engine) {
    return replace(leftHandSide, engine, true);
  }

  public IExpr replace(final IExpr leftHandSide, EvalEngine engine, boolean evaluate) {
    IPatternMap patternMap = null;
    if (isRuleWithoutPatterns()) {
      // no patterns found match equally:
      if (fLhsPatternExpr.equals(leftHandSide)) {
        return replaceEqualMatch(leftHandSide, engine, evaluate);
      }
      if (!(fLhsPatternExpr.isOrderlessAST() && leftHandSide.isOrderlessAST())) {
        if (!(fLhsPatternExpr.isFlatAST() && leftHandSide.isFlatAST())) {
          return F.NIL;
        }
        // replaceSubExpressionOrderlessFlat() below implements equals matching for
        // special cases, if the AST is Orderless or Flat
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
      return replaceSubExpressionOrderlessFlat(
          (IAST) fLhsPatternExpr, (IAST) leftHandSide, fRightHandSide, engine);
    }
    return F.NIL;
  }

  /**
   * A match which contains a pattern was found.
   *
   * <p>Assumption <code>
   * matchExpr(fLhsPatternExpr, leftHandSide, engine, new StackMatcher(engine)) == true</code>.
   *
   * @param leftHandSide
   * @param patternMap
   * @param engine
   * @param evaluate
   * @return
   */
  private IExpr replacePatternMatch(
      final IExpr leftHandSide, IPatternMap patternMap, EvalEngine engine, boolean evaluate) {
    if (RulesData.showSteps) {
      if (fLhsPatternExpr.head().equals(S.Integrate)) {
        IExpr rhs = fRightHandSide.orElse(S.Null);
        LOGGER.info("COMPLEX: {} := {}", fLhsPatternExpr, rhs);
        LOGGER.info(">>>>> {}", this);
      }
    }

    if (fReturnResult.isPresent()) {
      return fReturnResult;
    }

    engine.pushOptionsStack();
    try {
      engine.setOptionsPattern(fLhsPatternExpr.topHead(), patternMap);
      IExpr result = patternMap.substituteSymbols(fRightHandSide, F.CEmptySequence);
      if (evaluate) {
        if (Config.TRACE_REWRITE_RULE) {
          return engine.addEvaluatedTraceStep(
              leftHandSide, result, leftHandSide.topHead(), F.$str("RewriteRule"));
        }
        return engine.evaluate(result);
      } else {
        return result;
      }
    } catch (final ConditionException e) {
      if (LOGGER.isDebugEnabled()) {
        logConditionFalse(leftHandSide, fLhsPatternExpr, fRightHandSide);
      }
      return F.NIL;
    } catch (final ReturnException e) {
      IExpr result = e.getValue();
      if (evaluate) {
        if (Config.TRACE_REWRITE_RULE) {
          return engine.addEvaluatedTraceStep(
              leftHandSide, result, leftHandSide.topHead(), F.$str("RewriteRule"));
        }
        return engine.evaluate(result);
      }
      return result;
    } finally {
      engine.popOptionsStack();
    }
  }

  /**
   * A match which contains no pattern was found.
   *
   * <p>Assumption <code>fLhsPatternExpr.equals(leftHandSide) == true</code>
   *
   * @param leftHandSide
   * @param engine
   * @param evaluate
   * @return
   */
  private IExpr replaceEqualMatch(final IExpr leftHandSide, EvalEngine engine, boolean evaluate) {
    IExpr result = fRightHandSide;
    try {
      if (evaluate) {
        if (Config.TRACE_REWRITE_RULE) {
          return engine.addEvaluatedTraceStep(
              leftHandSide, result, leftHandSide.topHead(), F.$str("RewriteRule"));
        }
        return engine.evaluate(result);
      }
      return result;
    } catch (final ConditionException e) {
      if (LOGGER.isDebugEnabled()) {
        logConditionFalse(leftHandSide, fLhsPatternExpr, fRightHandSide);
      }
      return F.NIL;
    } catch (final ReturnException e) {
      result = e.getValue();
      if (evaluate) {
        if (Config.TRACE_REWRITE_RULE) {
          return engine.addEvaluatedTraceStep(
              leftHandSide, result, leftHandSide.topHead(), F.$str("RewriteRule"));
        }
        return engine.evaluate(result);
      }
      return result;
    }
  }

  /**
   * Get the flags for this matcher.
   *
   * @return
   */
  public int getFlags() {
    return fSetFlags;
  }

  @Override
  public IExpr getRHS() {
    return IExpr.ofNullable(fRightHandSide);
  }

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

  /**
   * Return <code>Set</code> or <code>SetDelayed</code> symbol.
   *
   * @return <code>null</code> if no symbol was defined
   */
  public ISymbol getSetSymbol() {
    if (isFlagOn(SET_DELAYED)) {
      return S.SetDelayed;
    }
    if (isFlagOn(SET)) {
      return S.Set;
    }
    if (isFlagOn(UPSET_DELAYED)) {
      return S.UpSetDelayed;
    }
    if (isFlagOn(UPSET)) {
      return S.UpSet;
    }
    if (isFlagOn(TAGSET_DELAYED)) {
      return S.TagSetDelayed;
    }
    if (isFlagOn(TAGSET)) {
      return S.TagSet;
    }
    return null;
  }

  /**
   * Set the evaluation flags for this list (i.e. replace all existing flags).
   *
   * @param i
   */
  public void setFlags(int i) {
    fSetFlags = i;
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
    result = prime * result + fSetFlags;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PatternMatcherAndEvaluator other = (PatternMatcherAndEvaluator) obj;
    if (fRightHandSide == null) {
      if (other.fRightHandSide != null) {
        return false;
      }
    } else if (!fRightHandSide.equals(other.fRightHandSide)) {
      return false;
    }
    return fSetFlags == other.fSetFlags;
  }
}
