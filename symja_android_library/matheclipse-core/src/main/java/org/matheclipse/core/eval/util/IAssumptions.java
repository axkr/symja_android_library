package org.matheclipse.core.eval.util;

import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IReal;

public interface IAssumptions {
  public static void assign(IExpr arg1, final IAST assumptionExpr,
      IAssumptions oldAssumptions, final EvalEngine engine) {
    IAssumptions assumptions;
    if (oldAssumptions == null) {
      assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
    } else {
      assumptions = oldAssumptions.copy();
      assumptions = assumptions.addAssumption(assumptionExpr);
    }
    engine.setAssumptions(assumptions);
  }

  /**
   * Add more assumptions from the given <code>expr</code>. If <code>expr</code> is not a valid
   * assumption,+ return <code>this</code> unmodified
   *
   * @param expr the assumptions which should be added to the <code>assumptions</code> instance.
   * @return <code>this</code>
   */
  public IAssumptions addAssumption(IExpr expr);

  /**
   * Copy the assumptions to a new instance.
   *
   * @return
   */
  public IAssumptions copy();

  /**
   * Get the distribution which is associated with the expression
   *
   * @param expr the expression
   * @return <code>F.NIL</code> if no distribution is associated
   */
  public IAST distribution(IExpr expr);

  // public IExpr get$Assumptions();

  public Map<IExpr, IAST> getTensorsMap();

  /**
   * Gives <code>true</code>, if the expression is assumed to be an algebraic value (i.e. an element
   * of the <code>Algebraics</code> domain), <code>false</code> in all other cases.
   *
   * @param expr
   * @return
   */
  public boolean isAlgebraic(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a boolean value (i.e. an element of
   * the <code>Booleans</code> domain), <code>false</code> in all other cases.
   *
   * @param expr
   * @return
   */
  public boolean isBoolean(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a complex value (i.e. an element of
   * the <code>Complexes</code> domain), <code>false</code> in all other cases.
   *
   * @param expr
   * @return
   */
  public boolean isComplex(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to equal the number, <code>false
   * </code> in all other cases.
   * 
   * @param expr
   * @param number
   * @return
   */
  public boolean isEqual(IExpr expr, IReal number);

  /**
   * Gives <code>true</code>, if the expression is assumed to be greater equal number, <code>false
   * </code> in all other cases.
   *
   * @param expr
   * @param number
   * @return
   */
  public boolean isGreaterEqual(IExpr expr, IReal number);

  /**
   * Gives <code>true</code>, if the expression is assumed to be greater than number, <code>false
   * </code> in all other cases.
   *
   * @param expr
   * @param number
   * @return
   */
  public boolean isGreaterThan(IExpr expr, IReal number);

  /**
   * Gives <code>true</code>, if the expression is assumed to be an integer value (i.e. an element
   * of the <code>Integers</code> domain), <code>false</code> in all other cases.
   *
   * @param expr
   * @return
   */
  public boolean isInteger(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be less equal number, <code>false
   * </code> in all other cases.
   *
   * @param expr
   * @param number
   * @return
   */
  public boolean isLessEqual(IExpr expr, IReal number);

  /**
   * Gives <code>true</code>, if the expression is assumed to be less than number, <code>false
   * </code> in all other cases.
   *
   * @param expr
   * @param number
   * @return
   */
  public boolean isLessThan(IExpr expr, IReal number);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a negative value, <code>false
   * </code> in all other cases.
   *
   * @param expr
   * @return
   */
  public boolean isNegative(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a positive rational value (i.e. an
   * element of the {@link S#NegativeRationals} domain), <code>false</code> in all other cases.
   * 
   * @param expr
   * @return
   */
  public boolean isNegativeRational(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a negative real value (i.e. an
   * element of the {@link S#NegativeReals} domain), <code>false</code> in all other cases.
   * 
   * @param expr
   * @return
   */
  public boolean isNegativeReal(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a non-negative value, <code>false
   * </code> in all other cases.
   *
   * @param expr
   * @return
   */
  public boolean isNonNegative(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a positive rational value (i.e. an
   * element of the {@link S#NonNegativeRationals} domain), <code>false</code> in all other cases.
   * 
   * @param expr
   * @return
   */
  public boolean isNonNegativeRational(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a non negative real value (i.e. an
   * element of the {@link S#NonNegativeReals} domain), <code>false</code> in all other cases.
   * 
   * @param expr
   * @return
   */
  public boolean isNonNegativeReal(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a positive value, <code>false
   * </code> in all other cases.
   *
   * @param expr
   * @return
   */
  public boolean isPositive(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a positive rational value (i.e. an
   * element of the {@link S#PositiveRationals} domain), <code>false</code> in all other cases.
   * 
   * @param expr
   * @return
   */
  public boolean isPositiveRational(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a positive real value (i.e. an
   * element of the {@link S#PositiveReals} domain), <code>false</code> in all other cases.
   * 
   * @param expr
   * @return
   */
  public boolean isPositiveReal(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a prime number (i.e. an element of
   * the <code>Primes</code> domain), <code>false</code> in all other cases.
   *
   * @param expr
   * @return
   */
  public boolean isPrime(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be a rational value (i.e. an element
   * of the <code>Rationals</code> domain), <code>false</code> in all other cases.
   *
   * @param expr
   * @return
   */
  public boolean isRational(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to be an integer value (i.e. an element
   * of the <code>Reals</code> domain), <code>false</code> in all other cases.
   *
   * @param expr
   * @return
   */
  public boolean isReal(IExpr expr);

  /**
   * Gives <code>true</code>, if the expression is assumed to unequal the number, <code>false
   * </code> in all other cases.
   * 
   * @param expr
   * @param number
   * @return
   */
  public boolean isUnequal(IExpr expr, INumber number);

  /**
   * Reduce the integer range according to the assumptions.
   *
   * @param range
   * @return <code>null</code> if no interval can be determined
   */
  public int[] reduceRange(IExpr x, final int[] range);

  // public void set$Assumptions(IExpr expr);

  /**
   * Get some assumptions about tensor symbols
   *
   * @param expr
   * @return <code>F.NIL</code> if no tensor property is associated
   */
  public IAST tensors(IExpr expr);
}
