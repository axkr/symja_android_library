package org.matheclipse.core.expression;



import java.util.Collection;
import java.util.Map;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * Abstract base class for patters with sequence handling.
 *
 */
public abstract class AbstractPatternSequence implements IPatternSequence {

  private static final long serialVersionUID = -1095810420975971421L;

  /** The associated symbol for this pattern sequence */
  protected ISymbol fSymbol;
  /** Use default value, if no matching was found */
  protected boolean fDefault = false;
  protected boolean fZeroArgsAllowed = false;

  public AbstractPatternSequence() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr variables2Slots(final Map<IExpr, IExpr> map,
      final Collection<IExpr> variableCollector) {
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public boolean accept(IVisitorBoolean visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public int accept(IVisitorInt visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public long accept(IVisitorLong visitor) {
    return visitor.visit(this);
  }

  /**
   * Check if the two left-hand-side pattern expressions are equivalent. (i.e. <code>f[x_,y_]</code>
   * is equivalent to <code>f[a_,b_]</code> )
   *
   * @param patternExpr2
   * @param pm1
   * @param pm2
   * @return
   */
  @Override
  public boolean equivalent(final IPatternObject patternExpr2, final IPatternMap pm1,
      IPatternMap pm2) {
    if (this == patternExpr2) {
      return true;
    }
    if (patternExpr2 instanceof PatternSequence) {
      // test if the pattern indices are equal
      final IPatternSequence p2 = (IPatternSequence) patternExpr2;
      if (getIndex(pm1) != p2.getIndex(pm2)) {
        return false;
      }
      // test if the "check" expressions are equal
      final Object o1 = getHeadTest();
      final Object o2 = p2.getHeadTest();
      if ((o1 == null) || (o2 == null)) {
        return o1 == o2;
      }
      return o1.equals(o2);
    }
    return false;
  }


  /** @return */
  @Override
  public int getIndex(IPatternMap pm) {
    if (pm != null) {
      return pm.get(fSymbol);
    }
    return -1;
  }


  @Override
  public int getEvalFlags() {
    // the ast contains a pattern sequence (i.e. "x__")
    return IAST.CONTAINS_PATTERN_SEQUENCE;
  }

  /** @return */
  @Override
  public ISymbol getSymbol() {
    return fSymbol;
  }

  @Override
  public ISymbol head() {
    return S.Pattern;
  }

  /** {@inheritDoc} */
  @Override
  public int hierarchy() {
    return PATTERNID;
  }

  @Override
  public boolean matchPattern(final IExpr expr, IPatternMap patternMap) {
    IAST sequence = F.Sequence(expr);
    return matchPatternSequence(sequence, patternMap, S.Missing);
  }

  @Override
  public boolean matchPatternSequence(final IAST sequence, IPatternMap patternMap,
      ISymbol optionsPatternHead) {
    if (!isConditionMatchedSequence(sequence, patternMap)) {
      return false;
    }
    if (sequence.size() == 1 && !isNullSequence()) {
      return false;
    }

    IExpr value = patternMap.getValue(this);
    if (value != null) {
      return sequence.equals(value);
    }
    return patternMap.setValue(this, sequence);
    // return true;
  }

  /**
   * Use default value, if no matching was found.
   *
   * @return
   */
  @Override
  public boolean isDefault() {
    return fDefault;
  }

  @Override
  public boolean isNullSequence() {
    return fZeroArgsAllowed;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFreeOfPatterns() {
    return false;
  }

  /** {@inheritDoc} */
  // public boolean isFreeOfDefaultPatterns() {
  // return true;
  // }

  /** {@inheritDoc} */
  @Override
  public final boolean isPatternExpr() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPatternSequence(boolean testNullSequence) {
    if (testNullSequence) {
      return fZeroArgsAllowed;
    }
    return true;
  }

}
