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
 * Abstract base class for patterns that match a variable number of expressions (sequences).
 * <p>
 * This class provides the common infrastructure for implementing {@link IPatternSequence}. It
 * manages the state shared by most sequence patterns, such as the bound pattern symbol (e.g., the
 * {@code x} in {@code x__}), default value flags, and whether the sequence is allowed to be empty
 * (zero-length).
 * </p>
 *
 * <h3>1. Key Responsibilities</h3>
 * <ul>
 * <li><b>Symbol Binding:</b> Stores the {@link ISymbol} ({@code fSymbol}) to which the matched
 * sequence will be bound in the {@link IPatternMap}.</li>
 * <li><b>Zero-Length Handling:</b> Manages the {@code fZeroArgsAllowed} flag.
 * <ul>
 * <li>If {@code true}, the pattern matches 0 or more arguments (e.g., {@code BlankNullSequence}
 * {@code ___}).</li>
 * <li>If {@code false}, the pattern requires at least 1 argument (e.g., {@code BlankSequence}
 * {@code __}).</li>
 * </ul>
 * </li>
 * <li><b>Default Values:</b> Manages the {@code fDefault} flag, allowing the pattern to adopt a
 * default value if missing (used in {@code Optional} patterns like {@code x___.}).</li>
 * </ul>
 *
 * <h3>2. Concrete Subclasses</h3>
 * <p>
 * This class serves as the parent for the core sequence pattern implementations:
 * </p>
 * <ul>
 * <li>{@link PatternSequence}: The standard implementation for {@code BlankSequence} ({@code __})
 * and {@code BlankNullSequence} ({@code ___}).</li>
 * <li>{@link RepeatedPattern}: Implementation for {@code Repeated} ({@code p..}) and
 * {@code RepeatedNull} ({@code p...}).</li>
 * <li>{@link OptionsPattern}: Specialized handling for sequences of rules/options at the end of
 * function calls.</li>
 * </ul>
 *
 * <h3>3. Implementation Details</h3>
 * <p>
 * Subclasses generally only need to implement the specific matching logic (e.g., checking if
 * specific elements match a head test or a repeated pattern). The {@link #matchPatternSequence}
 * method provides a standard skeleton that delegates condition checking to
 * {@link #isConditionMatchedSequence} and handles the final storage into the pattern map.
 * </p>
 *
 * @see org.matheclipse.core.interfaces.IPatternSequence
 * @see org.matheclipse.core.expression.PatternSequence
 * @see org.matheclipse.core.expression.RepeatedPattern
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
      return pm.indexOf(fSymbol);
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
    return PATTERNSEQUENCEID;
  }

  @Override
  public boolean matchPattern(final IExpr expr, IPatternMap patternMap) {
    IAST sequence = expr.makeAST(S.Sequence);
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
