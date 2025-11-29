package org.matheclipse.core.interfaces;

import java.util.Iterator;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;

/** Interface for an iterator with additional tearDown() method, to run the iterator again */
public interface IIterator<E> extends Iterator<E> {
  /**
   * Set up this iterator.
   *
   * @return
   */
  default boolean setUp() {
    return true;
  }

  /**
   * Set up this iterator and throw a {@link NoEvalException#CONST} exception if it fails.
   *
   * @return
   */
  default boolean setUpThrow() throws FlowControlException {
    boolean result = setUp();
    if (!isSetIterator() && !isNumericFunction()) {
      throw NoEvalException.CONST;
    }
    return result;
  }

  /**
   * Get a hint of how much iterations probably will occur.
   *
   * @return
   */
  default int allocHint() {
    return 10;
  }

  /**
   * Get the &quot;upper limit&quot; which is used in this iterator.
   *
   * @return <code>null</code> if no &quot;upper limit&quot; is defined
   */
  default IExpr getUpperLimit() {
    return null;
  }

  /**
   * Get the &quot;lower limit&quot; which is used in this iterator.
   *
   * @return <code>null</code> if no &quot;lower limit&quot; is defined
   */
  default IExpr getLowerLimit() {
    return null;
  }

  /**
   * Get the &quot;step&quot; which is used in this iterator.
   *
   * @return <code>null</code> if no &quot;step&quot; is defined
   */
  default IExpr getStep() {
    return null;
  }

  /**
   * Get the variable which is used in this iterator.
   *
   * @return <code>null</code> if no variable is defined
   */
  default ISymbol getVariable() {
    return null;
  }

  /**
   * Test if &quot;lower limit&quot;, &quot;upper limit&quot; and &quot;step&quot; are numeric
   * functions.
   *
   * @return
   */
  default boolean isNumericFunction() {
    return false;
  }

  /**
   * Test if there's a valid variable set for the iterator.
   *
   * @return
   */
  default boolean isSetIterator() {
    return false;
  }

  /**
   * Test if there's a valid variable set for the iterator and the &quot;upper limit&quot; is a
   * list.
   *
   * @return
   */
  default boolean isValidVariable() {
    return false;
  }

  default boolean isUniform() {
    return false;
  }

  default boolean isInvalidNumeric() {
    if (!isSetIterator()) {
      IExpr step = getStep();
      if (step != null) {
        if (step.isNonNegativeResult()) {
          if (S.Negative.ofQ(F.Subtract(getUpperLimit(), getLowerLimit()))) {
            return true;
          }
        } else if (step.isNegativeResult()) {
          if (S.Negative.ofQ(F.Subtract(getLowerLimit(), getUpperLimit()))) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /** Tear down this iterator. */
  default void tearDown() {}
}
