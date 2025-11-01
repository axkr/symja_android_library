package org.matheclipse.core.patternmatching;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Matches a given expression by simply comparing the left-hand-side expression of this pattern
 * matcher with the {@link IExpr#equals(Object)} method.
 */
public class PatternMatcherEquals extends IPatternMatcher implements Externalizable {
  /** */
  private static final long serialVersionUID = 3566534441225675728L;

  /**
   * The right-hand-side expression which should be evaluated if the "pattern-matching" succeeds
   */
  protected IExpr fRightHandSide;

  /** Contains the flag for the "set" symbol used to define this pattern matcher */
  private int fSetFlags;

  /** Public constructor for serialization. */
  public PatternMatcherEquals() {}

  /**
   * @param setSymbol the symbol which defines this pattern-matching rule (i.e. Set, SetDelayed,...)
   * @param leftHandSide could contain pattern expressions for "pattern-matching"
   * @param rightHandSide the result which should be evaluated if the "pattern-matching" succeeds
   */
  public PatternMatcherEquals(final int setSymbol, final IExpr leftHandSide,
      final IExpr rightHandSide) {
    super(leftHandSide);
    fSetFlags = setSymbol;
    fRightHandSide = rightHandSide;
  }

  @Override
  public boolean test(IExpr lhsEvalExpr) {
    return fLhsPatternExpr.equals(lhsEvalExpr);
  }

  @Override
  public boolean test(IExpr lhsEvalExpr, EvalEngine engine) {
    return fLhsPatternExpr.equals(lhsEvalExpr);
  }

  @Override
  public IPatternMatcher clone() {
    return new PatternMatcherEquals(fSetFlags, fLhsPatternExpr, fRightHandSide);
  }

  @Override
  public IPatternMatcher copy() {
    PatternMatcherEquals v = new PatternMatcherEquals();
    v.fLhsPatternExpr = fLhsPatternExpr;
    v.fLhsExprToMatch = fLhsExprToMatch;
    v.fSetFlags = fSetFlags;
    v.fRightHandSide = fRightHandSide;
    return v;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr eval(IExpr leftHandSide, EvalEngine engine) {
    if (test(leftHandSide)) {
      return fRightHandSide;
    }
    return F.NIL;
  }

  @Override
  public void getPatterns(List<IExpr> resultList, IExpr patternExpr) {}

  /** {@inheritDoc} */
  @Override
  public IExpr getRHS() {
    return IExpr.ofNullable(fRightHandSide);
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
   * Are the given flags disabled ?
   *
   * @param flags
   * @see IAST#NO_FLAG
   */
  public final boolean isFlagOff(final int flags) {
    return (fSetFlags & flags) == 0;
  }

  /**
   * Are the given flags enabled ?
   *
   * @param flags
   * @see IAST#NO_FLAG
   */
  public final boolean isFlagOn(int flags) {
    return (fSetFlags & flags) == flags;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPatternHashAllowed(int patternHash) {
    return true;
  }

  @Override
  public boolean isRuleWithoutPatterns() {
    return true;
  }

  public void setRHS(IExpr rightHandSide) {
    fRightHandSide = rightHandSide;
  }

  @Override
  public int equivalentTo(IPatternMatcher o) {
    if (getLHSPriority() < o.getLHSPriority()) {
      return -1;
    }
    if (getLHSPriority() > o.getLHSPriority()) {
      return 1;
    }
    return 0;
  }

  @Override
  public int getPatternHash() {
    return 0;
  }

  @Override
  public int getLHSPriority() {
    return 0;
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
  public String toString() {
    return getAsAST().toString();
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeShort(fSetFlags);
    objectOutput.writeObject(fLhsPatternExpr);
    objectOutput.writeObject(fRightHandSide);
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    fSetFlags = objectInput.readShort();
    fLhsPatternExpr = (IExpr) objectInput.readObject();
    fRightHandSide = (IExpr) objectInput.readObject();
  }

  @Override
  public int equivalentLHS(IPatternMatcher obj) {
    return equivalentTo(obj);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + fSetFlags;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PatternMatcherEquals) {
      if (!super.equals(obj)) {
        return false;
      }
      return fSetFlags == ((PatternMatcherEquals) obj).fSetFlags;
    }
    return false;
  }
}
