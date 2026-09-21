package org.matheclipse.core.eval.util;

import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.Subtract;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.FormalSymbol;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Create iterators for functions like <code>Table()</code>, <code>Sum()</code> or <code>Product()
 * </code>
 */
public class Iterator {
  private static class ExprIterator implements IIterator<IExpr> {

    IExpr count;

    final boolean fNumericMode;

    EvalEngine evalEngine;

    IExpr lowerLimit;

    IExpr maxCounterOrList;

    /**
     * If <code>maxCounterOrList</code> is a list the <code>maxCounterOrListIndex</code> attribute
     * points to the current element.
     */
    int maxCounterOrListIndex;

    IExpr step;

    final IExpr originalLowerLimit;

    final IExpr originalUpperLimit;

    final IExpr originalStep;

    final ISymbol variable;

    /**
     * The value of the variable before the iteration started. Used in {@link #setUp()} to save the
     * old value, and used in {@link #tearDown()} to reset the variable to the old value.
     */
    IExpr variableValueBeforeIteration;

    public ExprIterator(final ISymbol symbol, final IExpr originalStart,
        final IExpr originalMaxCount, final IExpr originalStep, boolean numericMode,
        final EvalEngine engine) {
      this.variable = symbol;
      this.evalEngine = engine;
      this.originalLowerLimit = originalStart;
      this.originalUpperLimit = originalMaxCount;
      this.originalStep = originalStep;
      this.fNumericMode = numericMode;
    }

    @Override
    public int allocHint() {
      return 10;
    }

    @Override
    public IExpr getLowerLimit() {
      return originalLowerLimit;
    }

    @Override
    public IExpr getStep() {
      return originalStep;
    }

    @Override
    public IExpr getUpperLimit() {
      return originalUpperLimit;
    }

    @Override
    public ISymbol getVariable() {
      return variable;
    }

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return <code>true</code> if this enumeration contains more elements; <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean hasNext() {
      if (maxCounterOrList == null) { // || (illegalIterator)) {
        throw NoEvalException.CONST;
      }
      if ((maxCounterOrList.isDirectedInfinity()) || count.isDirectedInfinity()) {
        throw NoEvalException.CONST;
      }
      // if (count == null || count.isDirectedInfinity()) {
      // throw new NoEvalException();
      // }
      if (maxCounterOrList.isList()) {
        if (maxCounterOrListIndex <= ((IAST) maxCounterOrList).size()) {
          return true;
        }
      } else {
        if (step.isZero()) {
          throw NoEvalException.CONST;
        }
        if (step.isReal()) {
          if (step.isNegative()) {
            if (maxCounterOrList.lessEqual(count).isTrue()) {
              // if (S.LessEqual.ofQ(evalEngine, maxCounterOrList, count)) {
              return true;
            }
          } else {
            if (count.lessEqual(maxCounterOrList).isTrue()) {
              // if (S.LessEqual.ofQ(evalEngine, count, maxCounterOrList)) {
              return true;
            }
          }
        }
        IExpr sub = evalEngine.evaluate(Divide(Subtract(maxCounterOrList, count), step));
        if (sub.isReal()) {
          return !sub.isNegative();
        }
        double d = sub.evalfNaN();
        if (!Double.isNaN(d)) {
          return !(d < 0.0);
        }
        IExpr together = evalEngine.evaluate(F.Together(sub));
        d = together.evalfNaN();
        if (!Double.isNaN(d)) {
          return !(d < 0.0);
        }
        // Whether the iteration is over cannot be decided, e.g. for {i,1,n} with a symbolic n.
        // Answering "no more elements" would silently produce an empty table for a range whose
        // length is simply unknown, so the whole iteration is abandoned instead.
        throw NoEvalException.CONST;
      }
      return false;
    }

    @Override
    public boolean isNumericFunction() {
      return originalLowerLimit.isNumericFunction(true) && originalStep.isNumericFunction(true)
          && originalUpperLimit.isNumericFunction(true);
    }

    @Override
    public boolean isSetIterator() {
      return variable != null && originalUpperLimit != null && originalUpperLimit.isList();
    }

    @Override
    public boolean isValidVariable() {
      return variable != null && originalLowerLimit != null && originalStep != null
          && originalUpperLimit != null && !originalUpperLimit.isList();
    }

    /**
     * Returns the next element of this enumeration.
     *
     * @return the next element of this enumeration.
     */
    @Override
    public IExpr next() {
      if (variable != null && variable != count) {
        variable.assignValue(count, false);
      }
      final IExpr temp = count;
      if (maxCounterOrList.isList()) {
        if (maxCounterOrListIndex == ((IAST) maxCounterOrList).size()) {
          maxCounterOrListIndex++;
          return temp;
        }
        count = ((IAST) maxCounterOrList).get(maxCounterOrListIndex++);
      } else {
        count = evalEngine.evaluate(count.plus(step));
      }
      return temp;
    }

    /** Not implemented; throws UnsupportedOperationException */
    @Override
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean setUp() {
      if (variable != null) {
        variableValueBeforeIteration = variable.assignedValue();
      }
      lowerLimit = originalLowerLimit;
      if (!(originalLowerLimit.isReal())) {
        lowerLimit = evalEngine.evalWithoutNumericReset(originalLowerLimit);
      }
      maxCounterOrList = originalUpperLimit;
      if (!(originalUpperLimit.isReal())) {
        maxCounterOrList = evalEngine.evalWithoutNumericReset(originalUpperLimit);
      }
      // points to first element in maxCounterOrList if it's a list
      maxCounterOrListIndex = 1;

      step = originalStep;
      if (!(originalStep.isReal())) {
        step = evalEngine.evalWithoutNumericReset(originalStep);
      }
      if (step.isReal()) {
        if (step.isNegative()) {
          if (evalEngine.evaluate(Less(lowerLimit, maxCounterOrList)).isTrue()) {
            return false;
          }
        } else {
          if (evalEngine.evaluate(Less(maxCounterOrList, lowerLimit)).isTrue()) {
            return false;
          }
        }
      }
      if (maxCounterOrList.isList()) {
        if (maxCounterOrListIndex < maxCounterOrList.size()) {
          count = maxCounterOrList.getAt(maxCounterOrListIndex++);
        } else {
          return false;
        }
      } else {
        count = lowerLimit;
      }
      if (variable != null && variable != count) {
        variable.assignValue(count, false);
      }
      return true;
    }

    /** Method Declaration. */
    @Override
    public void tearDown() {
      if (variable != null) {
        variable.clearValue(variableValueBeforeIteration);
      }
      EvalEngine.get().setNumericMode(fNumericMode);
    }
  }

  /** Iterate over a list of values. */
  private static class ExprListIterator implements IIterator<IExpr> {

    IExpr count;

    EvalEngine evalEngine;

    IAST maxCounterOrListAssoc;

    /**
     * If <code>maxCounterOrList</code> is a list the <code>maxCounterOrListIndex</code> attribute
     * points to the current element.
     */
    int maxCounterOrListIndex;

    final IAST originalListAssoc;

    final ISymbol variable;

    /**
     * The value of the variable before the iteration started. Used in {@link #setUp()} to save the
     * old value, and used in {@link #tearDown()} to reset the variable to the old value.
     */
    IExpr variableValueBeforeIteration;

    /**
     * Iterate over a list of values.
     *
     * @param symbol
     * @param originalList
     * @param engine
     */
    public ExprListIterator(final ISymbol symbol, final IAST originalList,
        final EvalEngine engine) {
      this.variable = symbol;
      this.evalEngine = engine;
      this.originalListAssoc = originalList;
    }

    @Override
    public int allocHint() {
      return 10;
    }

    @Override
    public ISymbol getVariable() {
      return variable;
    }

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return <code>true</code> if this enumeration contains more elements; <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean hasNext() {
      if (maxCounterOrListAssoc == null) { // || (illegalIterator)) {
        throw NoEvalException.CONST;
      }

      if (maxCounterOrListIndex <= maxCounterOrListAssoc.size()) {
        return true;
      }
      return false;
    }

    @Override
    public boolean isNumericFunction() {
      return false;
    }

    /**
     * This iterator always iterates over a fixed set of values, whether or not they are assigned to
     * a variable. A <code>{{e1, e2,...}}</code> specification has no variable at all.
     */
    @Override
    public boolean isSetIterator() {
      return true;
    }

    @Override
    public boolean isValidVariable() {
      return variable != null;
    }

    /**
     * Returns the next element of this enumeration.
     *
     * @return the next element of this enumeration.
     */
    @Override
    public IExpr next() {
      if (variable != null && variable != count) {
        variable.assignValue(count, false);
      }
      final IExpr temp = count;
      if (maxCounterOrListAssoc.isListOrAssociation()) {
        if (maxCounterOrListIndex == maxCounterOrListAssoc.size()) {
          maxCounterOrListIndex++;
          return temp;
        }
        count = maxCounterOrListAssoc.get(maxCounterOrListIndex++);
      }
      return temp;
    }

    /** Not implemented; throws UnsupportedOperationException */
    @Override
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean setUp() {
      if (variable != null) {
        variableValueBeforeIteration = variable.assignedValue();
      }
      maxCounterOrListAssoc = originalListAssoc;
      maxCounterOrListAssoc = originalListAssoc.map(x -> evalEngine.evalWithoutNumericReset(x));
      // points to first element in maxCounterOrList if it's a list
      maxCounterOrListIndex = 1;

      if (maxCounterOrListIndex < maxCounterOrListAssoc.size()) {
        count = maxCounterOrListAssoc.get(maxCounterOrListIndex++);
      } else {
        return false;
      }

      if (variable != null && variable != count) {
        variable.assignValue(count, false);
      }
      return true;
    }

    /** Method Declaration. */
    @Override
    public void tearDown() {
      if (variable != null) {
        variable.clearValue(variableValueBeforeIteration);
      }
    }
  }

  public static class DoubleIterator implements IIterator<IExpr> {
    double count;

    double lowerLimit;

    double upperLimit;

    double step;

    final ISymbol variable;

    /**
     * The value of the variable before the iteration started. Used in {@link #setUp()} to save the
     * old value, and used in {@link #tearDown()} to reset the variable to the old value.
     */
    IExpr variableValueBeforeIteration;

    final INum lowerLimitNum;

    final INum upperLimitNum;

    final INum stepNum;

    public DoubleIterator(final ISymbol symbol, final double lowerLimit, final double upperLimit,
        final double step) {
      this.variable = symbol;
      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
      this.step = step;
      this.lowerLimitNum = F.num(lowerLimit);
      this.upperLimitNum = F.num(upperLimit);
      this.stepNum = F.num(step);
    }

    @Override
    public int allocHint() {
      if (step < 0) {
        return (int) Math.round((lowerLimit - upperLimit) / (-step) + 1.0);
      }
      return (int) Math.round((upperLimit - lowerLimit) / step + 1.0);
    }

    @Override
    public INum getLowerLimit() {
      return lowerLimitNum;
    }

    @Override
    public INum getStep() {
      return stepNum;
    }

    @Override
    public INum getUpperLimit() {
      return upperLimitNum;
    }

    @Override
    public ISymbol getVariable() {
      return variable;
    }

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return <code>true</code> if this enumeration contains more elements; <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean hasNext() {
      if (step < 0.0) {
        return count >= upperLimit
            || F.isFuzzyEquals(count, upperLimit, Config.SPECIAL_FUNCTIONS_TOLERANCE);
      }
      return count <= upperLimit
          || F.isFuzzyEquals(count, upperLimit, Config.SPECIAL_FUNCTIONS_TOLERANCE);
    }

    @Override
    public boolean isNumericFunction() {
      return true;
    }

    @Override
    public boolean isSetIterator() {
      return variable != null;
    }

    @Override
    public boolean isValidVariable() {
      return variable != null;
    }

    @Override
    public boolean isUniform() {
      return true;
    }

    /**
     * Returns the next element of this enumeration.
     *
     * @return the next element of this enumeration.
     */
    @Override
    public INum next() {
      final INum temp = F.num(count);
      if (variable != null) {
        variable.assignValue(temp, false);
      }
      count += step;
      return temp;
    }

    /** Not implemented; throws UnsupportedOperationException */
    @Override
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean setUp() {
      if (variable != null) {
        variableValueBeforeIteration = variable.assignedValue();
      }
      count = lowerLimit;
      if (step < 0) {
        if (lowerLimit < upperLimit) {
          return false;
        }
      } else {
        if (lowerLimit > upperLimit) {
          return false;
        }
      }

      if (variable != null) {
        variable.assignValue(lowerLimitNum, false);
      }
      return true;
    }

    /** Method Declaration. */
    @Override
    public void tearDown() {
      if (variable != null) {
        variable.clearValue(variableValueBeforeIteration);
      }
    }
  }

  private static class RationalIterator implements IIterator<IExpr> {
    IRational count;

    IRational lowerLimit;

    IRational upperLimit;

    IRational step;

    final ISymbol variable;

    /**
     * The value of the variable before the iteration started. Used in {@link #setUp()} to save the
     * old value, and used in {@link #tearDown()} to reset the variable to the old value.
     */
    IExpr variableValueBeforeIteration;

    final IRational lowerLimitQQ;

    final IRational upperLimitQQ;

    final IRational stepQQ;

    public RationalIterator(final ISymbol symbol, final IRational lowerLimit,
        final IRational upperLimit, final IRational step) {
      this.variable = symbol;
      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
      this.step = step;
      this.lowerLimitQQ = lowerLimit;
      this.upperLimitQQ = upperLimit;
      this.stepQQ = step;
    }

    @Override
    public int allocHint() {
      IRational temp = lowerLimit.subtract(upperLimit).divideBy(step);
      IInteger hint = temp.numerator().div(temp.denominator());
      int alloc = hint.toInt();
      if (alloc < 0) {
        return (-alloc) + 1;
      }
      return alloc + 1;
    }

    @Override
    public IRational getLowerLimit() {
      return lowerLimitQQ;
    }

    @Override
    public IRational getStep() {
      return stepQQ;
    }

    @Override
    public IRational getUpperLimit() {
      return upperLimitQQ;
    }

    @Override
    public ISymbol getVariable() {
      return variable;
    }

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return <code>true</code> if this enumeration contains more elements; <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean hasNext() {
      if (step.isNegative()) {
        return count.greaterEqualThan(upperLimit).isTrue();
      }
      return count.lessEqualThan(upperLimit).isTrue();
    }

    @Override
    public boolean isNumericFunction() {
      return true;
    }

    @Override
    public boolean isSetIterator() {
      return variable != null;
    }

    @Override
    public boolean isValidVariable() {
      return variable != null;
    }


    @Override
    public boolean isUniform() {
      return true;
    }

    /**
     * Returns the next element of this enumeration.
     *
     * @return the next element of this enumeration.
     */
    @Override
    public IRational next() {
      final IRational temp = count;
      if (variable != null) {
        variable.assignValue(temp, false);
      }
      count = count.add(step);
      return temp;
    }

    /** Not implemented; throws UnsupportedOperationException */
    @Override
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean setUp() {
      if (variable != null) {
        variableValueBeforeIteration = variable.assignedValue();
      }
      count = lowerLimit;
      if (step.isNegative()) {
        if (lowerLimit.lessThan(upperLimit).isTrue()) {
          return false;
        }
      } else {
        if (lowerLimit.greaterThan(upperLimit).isTrue()) {
          return false;
        }
      }
      if (variable != null) {
        variable.assignValue(lowerLimitQQ, false);
      }
      return true;
    }

    /** Method Declaration. */
    @Override
    public void tearDown() {
      if (variable != null) {
        variable.clearValue(variableValueBeforeIteration);
      }
    }
  }

  private static class QuantityIterator implements IIterator<IExpr> {
    IAST count;

    IAST lowerLimit;

    IAST upperLimit;

    IAST step;

    final ISymbol variable;

    /**
     * The value of the variable before the iteration started. Used in {@link #setUp()} to save the
     * old value, and used in {@link #tearDown()} to reset the variable to the old value.
     */
    IExpr variableValueBeforeIteration;

    final IExpr unit;

    final IAST originalLowerLimit;

    final IAST originalUpperLimit;

    final IAST originalStep;

    /** Converts the quantity to this iterator's unit; throws on incompatible units. */
    private static IAST toUnit(IAST quantity, IExpr unit) {
      if (quantity.arg2().equals(unit)) {
        return quantity;
      }
      IExpr magnitude = org.matheclipse.core.units.Units.convertMagnitude(quantity.arg1(),
          quantity.arg2(), unit, EvalEngine.get());
      if (magnitude.isNIL()) {
        // `1` and `2` are incompatible units
        throw new ArgumentTypeException("compat", F.list(quantity.arg2(), unit));
      }
      return F.Quantity(magnitude, unit);
    }

    public QuantityIterator(final ISymbol symbol, IAST lowerLimit, IAST upperLimit,
        final IAST step) {
      this.unit = lowerLimit.arg2();
      upperLimit = toUnit(upperLimit, unit);
      IAST normalizedStep = toUnit(step, unit);
      this.variable = symbol;
      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
      this.step = normalizedStep;
      this.originalLowerLimit = lowerLimit;
      this.originalUpperLimit = upperLimit;
      this.originalStep = normalizedStep;
    }

    public QuantityIterator(final ISymbol symbol, IAST lowerLimit, IAST upperLimit) {
      this.unit = lowerLimit.arg2();
      upperLimit = toUnit(upperLimit, unit);
      this.step = F.Quantity(F.C1, unit);
      this.variable = symbol;
      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
      this.originalLowerLimit = lowerLimit;
      this.originalUpperLimit = upperLimit;
      this.originalStep = step;
    }

    public QuantityIterator(final ISymbol symbol, IAST upperLimit) {
      this.unit = upperLimit.arg2();
      this.lowerLimit = F.Quantity(F.C1, unit);
      this.step = F.Quantity(F.C1, unit);
      this.variable = symbol;
      this.upperLimit = upperLimit;
      this.originalLowerLimit = lowerLimit;
      this.originalUpperLimit = upperLimit;
      this.originalStep = step;
    }

    @Override
    public int allocHint() {
      return 10;
    }

    @Override
    public IExpr getLowerLimit() {
      return originalLowerLimit;
    }

    @Override
    public IExpr getStep() {
      return originalStep;
    }

    @Override
    public IExpr getUpperLimit() {
      return originalUpperLimit;
    }

    @Override
    public ISymbol getVariable() {
      return variable;
    }

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return <code>true</code> if this enumeration contains more elements; <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean hasNext() {
      if (step.arg1().isNegative()) {
        return count.arg1().greaterEqualThan(upperLimit.arg1()).isTrue();
      }
      return count.arg1().lessEqualThan(upperLimit.arg1()).isTrue();
    }

    @Override
    public boolean isNumericFunction() {
      return true;
    }

    @Override
    public boolean isSetIterator() {
      return variable != null;
    }

    @Override
    public boolean isValidVariable() {
      return variable != null;
    }

    /**
     * Returns the next element of this enumeration.
     *
     * @return the next element of this enumeration.
     */
    @Override
    public IExpr next() {
      final IAST temp = count;
      if (variable != null) {
        variable.assignValue(temp, false);
      }
      count = F.Quantity(EvalEngine.get().evaluate(F.Plus(count.arg1(), step.arg1())), unit);
      return temp;
    }

    /** Not implemented; throws UnsupportedOperationException */
    @Override
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean setUp() {
      if (variable != null) {
        variableValueBeforeIteration = variable.assignedValue();
      }
      count = lowerLimit;
      if (step.arg1().isNegative()) {
        if (lowerLimit.arg1().lessThan(upperLimit.arg1()).isTrue()) {
          return false;
        }
      } else {
        if (lowerLimit.arg1().greaterThan(upperLimit.arg1()).isTrue()) {
          return false;
        }
      }
      if (variable != null) {
        variable.assignValue(originalLowerLimit, false);
      }
      return true;
    }

    /** Method Declaration. */
    @Override
    public void tearDown() {
      if (variable != null) {
        variable.clearValue(variableValueBeforeIteration);
      }
    }
  }

  private static class RealIterator implements IIterator<IExpr> {
    IReal count;

    IReal lowerLimit;

    IReal upperLimit;

    IReal step;

    final ISymbol variable;

    /**
     * The value of the variable before the iteration started. Used in {@link #setUp()} to save the
     * old value, and used in {@link #tearDown()} to reset the variable to the old value.
     */
    IExpr variableValueBeforeIteration;

    final IReal lowerLimitReal;

    final IReal upperLimitReal;

    final IReal stepReal;

    public RealIterator(final ISymbol symbol, final IReal lowerLimit, final IReal upperLimit,
        final IReal step) {
      this.variable = symbol;
      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
      this.step = step;
      this.lowerLimitReal = lowerLimit;
      this.upperLimitReal = upperLimit;
      this.stepReal = step;
    }

    @Override
    public int allocHint() {
      return 10;
    }

    @Override
    public IExpr getLowerLimit() {
      return lowerLimitReal;
    }

    @Override
    public IExpr getStep() {
      return stepReal;
    }

    @Override
    public IExpr getUpperLimit() {
      return upperLimitReal;
    }

    @Override
    public ISymbol getVariable() {
      return variable;
    }

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return <code>true</code> if this enumeration contains more elements; <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean hasNext() {
      if (step.isNegative()) {
        return count.greaterEqualThan(upperLimit).isTrue();
      }
      return count.lessEqualThan(upperLimit).isTrue();
    }

    @Override
    public boolean isNumericFunction() {
      return true;
    }

    @Override
    public boolean isSetIterator() {
      return variable != null;
    }

    @Override
    public boolean isValidVariable() {
      return variable != null;
    }

    @Override
    public boolean isUniform() {
      return true;
    }

    /**
     * Returns the next element of this enumeration.
     *
     * @return the next element of this enumeration.
     */
    @Override
    public IReal next() {
      final IReal temp = count;
      if (variable != null) {
        variable.assignValue(temp, false);
      }
      count = (IReal) count.plus(step);
      return temp;
    }

    /** Not implemented; throws UnsupportedOperationException */
    @Override
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean setUp() {
      if (variable != null) {
        variableValueBeforeIteration = variable.assignedValue();
      }
      count = lowerLimit;
      if (step.isNegative()) {
        if (lowerLimit.lessThan(upperLimit).isTrue()) {
          return false;
        }
      } else {
        if (lowerLimit.greaterThan(upperLimit).isTrue()) {
          return false;
        }
      }

      if (variable != null) {
        variable.assignValue(lowerLimitReal, false);
      }
      return true;
    }

    /** Method Declaration. */
    @Override
    public void tearDown() {
      if (variable != null) {
        variable.clearValue(variableValueBeforeIteration);
      }
    }
  }

  private static final class IntIterator implements IIterator<IExpr> {

    /**
     * The element which will be returned by {@link #next()} and incremented by step afterwards.
     */
    int nextElement;

    int lowerLimit;

    int upperLimit;

    int step;

    final ISymbol variable;

    /**
     * The value of the variable before the iteration started. Used in {@link #setUp()} to save the
     * old value, and used in {@link #tearDown()} to reset the variable to the old value.
     */
    IExpr variableValueBeforeIteration;

    final IInteger lowerLimitZZ;

    final IInteger upperLimitZZ;

    final IInteger stepZZ;

    public IntIterator(final ISymbol symbol, final int lowerLimit, final int upperLimit,
        final int step) {
      this.variable = symbol;
      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
      this.step = step;

      if (step < 0) {
        int limit = Integer.MIN_VALUE - step;
        if (limit > upperLimit) {
          throw new ArithmeticException("IntIterator out of int range (MIN_VALUE)");
        }
      } else {
        int limit = Integer.MAX_VALUE - step;
        if (limit < upperLimit) {
          throw new ArithmeticException("IntIterator out of int range (MAX_VALUE)");
        }
      }
      this.lowerLimitZZ = F.ZZ(lowerLimit);
      this.upperLimitZZ = F.ZZ(upperLimit);
      this.stepZZ = F.ZZ(step);
    }

    @Override
    public int allocHint() {
      if (step < 0) {
        return (lowerLimit - upperLimit) / (-step) + 1;
      }
      return (upperLimit - lowerLimit) / step + 1;
    }

    @Override
    public IInteger getLowerLimit() {
      return lowerLimitZZ;
    }

    @Override
    public IInteger getStep() {
      return stepZZ;
    }

    @Override
    public IInteger getUpperLimit() {
      return upperLimitZZ;
    }

    @Override
    public ISymbol getVariable() {
      return variable;
    }

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return <code>true</code> if this enumeration contains more elements; <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean hasNext() {
      if (step < 0) {
        return nextElement >= upperLimit;
      }
      return nextElement <= upperLimit;
    }

    @Override
    public boolean isNumericFunction() {
      return true;
    }

    @Override
    public boolean isSetIterator() {
      return variable != null;
    }

    @Override
    public boolean isValidVariable() {
      return variable != null;
    }

    @Override
    public boolean isUniform() {
      return true;
    }

    /**
     * Returns the next element of this enumeration.
     *
     * @return the next element of this enumeration.
     */
    @Override
    public IInteger next() {
      final IInteger temp = F.ZZ(nextElement);
      if (variable != null) {
        variable.assignValue(temp, false);
      }
      nextElement += step;
      return temp;
    }

    /** Not implemented; throws UnsupportedOperationException */
    @Override
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean setUp() {
      if (variable != null) {
        variableValueBeforeIteration = variable.assignedValue();
      }
      nextElement = lowerLimit;
      if (step < 0) {
        if (lowerLimit < upperLimit) {
          return false;
        }
      } else {
        if (lowerLimit > upperLimit) {
          return false;
        }
      }

      if (variable != null) {
        variable.assignValue(lowerLimitZZ, false);
      }
      return true;
    }

    /** Method Declaration. */
    @Override
    public void tearDown() {
      if (variable != null) {
        variable.clearValue(variableValueBeforeIteration);
      }
    }
  }

  /**
   * Iterator specification for functions like <code>Table()</code> or <code>Sum()</code> or <code>
   * Product()</code>
   *
   * @param list a list representing an iterator specification
   * @param position the position of the list in the argument sequence, for printing an error if
   *        list cannot be converted into an iterator form
   * @param engine the evaluation engine
   * @return the iterator
   */
  public static IIterator<IExpr> create(final IAST list, int position, final EvalEngine engine) {

    EvalEngine evalEngine = engine;
    IExpr lowerLimit;
    IExpr upperLimit;
    IExpr step;
    ISymbol variable;
    boolean fNumericMode;
    // fNumericMode = evalEngine.isNumericMode() ||
    // list.isMember(Predicates.isNumeric(), false);
    boolean oldNumericMode = evalEngine.isNumericMode();
    try {
      if (list.hasNumericArgument()) {
        evalEngine.setNumericMode(true);
      }
      fNumericMode = evalEngine.isNumericMode();
      switch (list.size()) {
        case 2:
          lowerLimit = F.C1;
          upperLimit = evalEngine.evalWithoutNumericReset(list.arg1());
          step = F.C1;
          variable = null;
          if (upperLimit.isListOrAssociation()) {
            // {{e1, e2,...}} iterate over the elements of the list without assigning them to a
            // variable; the number of elements determines the number of iterations
            return new ExprListIterator(null, (IAST) upperLimit, evalEngine);
          }
          if (upperLimit instanceof INum) {
            return new DoubleIterator(variable, 1.0, ((INum) upperLimit).doubleValue(), 1.0);
          }
          if (upperLimit.isInteger()) {
            try {
              return new IntIterator(variable, 1, ((IInteger) upperLimit).toInt(), 1);
            } catch (ArithmeticException ae) {
              //
            }
          }
          if (upperLimit.isRational()) {
            try {
              return new RationalIterator(variable, F.C1, (IRational) upperLimit, F.C1);
            } catch (ArithmeticException ae) {
              //
            }
          } else if (upperLimit.isQuantity()) {
            return new QuantityIterator(variable, (IAST) upperLimit);
          } else if (upperLimit.isReal()) {
            return new RealIterator(variable, F.C1, (IReal) upperLimit, F.C1);
          }
          if (!list.arg1().isVariable()) {
            // Iterator does not have appropriate bounds.
            // A one element iterator is a count, not a variable, so a `vloc` "cannot be localized"
            // message would name something which was never meant to be a variable.
            throw new ArgumentTypeException(
                Errors.getMessage("iterb", F.list(list), EvalEngine.get()));
          }
          break;
        case 3:
          lowerLimit = F.C1;
          upperLimit = evalEngine.evalWithoutNumericReset(list.arg2());
          step = F.C1;

          if (list.arg1() instanceof ISymbol) {
            ISymbol sym = (ISymbol) list.arg1();
            if (!isIteratorVariable(sym)) {
              // Cannot assign to raw object `1`.
              throw new ArgumentTypeException(
                  Errors.getMessage("setraw", F.list(sym), EvalEngine.get()));
            }
            variable = sym;
          } else {
            // Raw object `1` cannot be used as an iterator.
            throw new ArgumentTypeException(
                Errors.getMessage("itraw", F.list(list.arg1()), EvalEngine.get()));
          }
          if (upperLimit.isListOrAssociation()) {
            return new ExprListIterator(variable, (IAST) upperLimit, evalEngine);
          }
          if (upperLimit instanceof INum) {
            return new DoubleIterator(variable, 1.0, ((INum) upperLimit).doubleValue(), 1.0);
          }
          if (upperLimit.isInteger()) {
            try {
              int iUpperLimit = ((IInteger) upperLimit).toInt();
              return new IntIterator(variable, 1, iUpperLimit, 1);
            } catch (ArithmeticException ae) {
              //
            }
          }
          if (upperLimit.isRational()) {
            try {
              return new RationalIterator(variable, F.C1, (IRational) upperLimit, F.C1);
            } catch (ArithmeticException ae) {
              //
            }
          } else if (upperLimit.isQuantity()) {
            return new QuantityIterator(variable, (IAST) upperLimit);
          } else if (upperLimit.isReal()) {
            return new RealIterator(variable, F.C1, (IReal) upperLimit, F.C1);
          }

          break;
        case 4:
          lowerLimit = evalEngine.evalWithoutNumericReset(list.arg2());
          upperLimit = evalEngine.evalWithoutNumericReset(list.arg3());
          step = F.C1;

          if (list.arg1().isSymbol()) {
            ISymbol sym = (ISymbol) list.arg1();
            if (!isIteratorVariable(sym)) {
              // Cannot assign to raw object `1`.
              throw new ArgumentTypeException(
                  Errors.getMessage("setraw", F.list(sym), EvalEngine.get()));
            }
            variable = sym;
          } else {
            // Raw object `1` cannot be used as an iterator.
            throw new ArgumentTypeException(
                Errors.getMessage("itraw", F.list(list.arg1()), EvalEngine.get()));
          }
          if (lowerLimit instanceof INum && upperLimit instanceof INum) {
            return new DoubleIterator(variable, ((INum) lowerLimit).doubleValue(),
                ((INum) upperLimit).doubleValue(), 1.0);
          }
          if (lowerLimit.isInteger() && upperLimit.isInteger()) {
            try {
              int iLowerLimit = ((IInteger) lowerLimit).toInt();
              int iUpperLimit = ((IInteger) upperLimit).toInt();
              return new IntIterator(variable, iLowerLimit, iUpperLimit, 1);
            } catch (ArithmeticException ae) {
              //
            }
          }
          if (lowerLimit.isRational() && upperLimit.isRational()) {
            try {
              return new RationalIterator(variable, (IRational) lowerLimit, (IRational) upperLimit,
                  F.C1);
            } catch (ArithmeticException ae) {
              //
            }
          } else if (lowerLimit.isQuantity() && upperLimit.isQuantity()) {
            return new QuantityIterator(variable, (IAST) lowerLimit, (IAST) upperLimit);
          } else if (lowerLimit.isReal() && upperLimit.isReal()) {
            IReal iLowerLimit = (IReal) lowerLimit;
            IReal iUpperLimit = (IReal) upperLimit;
            return new RealIterator(variable, iLowerLimit, iUpperLimit, F.C1);
          }
          break;

        case 5:
          lowerLimit = evalEngine.evalWithoutNumericReset(list.arg2());
          upperLimit = evalEngine.evalWithoutNumericReset(list.arg3());
          step = evalEngine.evalWithoutNumericReset(list.arg4());
          checkNonZeroStep(list, step);
          if (list.arg1() instanceof ISymbol) {
            ISymbol sym = (ISymbol) list.arg1();
            if (!isIteratorVariable(sym)) {
              // Cannot assign to raw object `1`.
              throw new ArgumentTypeException(
                  Errors.getMessage("setraw", F.list(sym), EvalEngine.get()));
            }
            variable = sym;
          } else {
            // Raw object `1` cannot be used as an iterator.
            throw new ArgumentTypeException(
                Errors.getMessage("itraw", F.list(list.arg1()), EvalEngine.get()));
          }
          if (lowerLimit instanceof INum && upperLimit instanceof INum && step instanceof INum) {
            return new DoubleIterator(variable, ((INum) lowerLimit).doubleValue(),
                ((INum) upperLimit).doubleValue(), ((INum) step).doubleValue());
          }
          if (lowerLimit.isInteger() && upperLimit.isInteger() && step.isInteger()) {
            try {
              int iLowerLimit = ((IInteger) lowerLimit).toInt();
              int iUpperLimit = ((IInteger) upperLimit).toInt();
              int iStep = ((IInteger) step).toInt();
              return new IntIterator(variable, iLowerLimit, iUpperLimit, iStep);
            } catch (ArithmeticException ae) {
              //
            }
          }
          if (lowerLimit.isRational() && upperLimit.isRational() && step.isRational()) {
            try {
              return new RationalIterator(variable, (IRational) lowerLimit, (IRational) upperLimit,
                  (IRational) step);
            } catch (ArithmeticException ae) {
              //
            }
          } else if (lowerLimit.isQuantity() && upperLimit.isQuantity() && step.isQuantity()) {
            return new QuantityIterator(variable, (IAST) lowerLimit, (IAST) upperLimit,
                (IAST) step);
          } else if (lowerLimit.isReal() && upperLimit.isReal() && step.isReal()) {
            return new RealIterator(variable, (IReal) lowerLimit, (IReal) upperLimit, (IReal) step);
          }

          break;
        default:
          // Argument `1` at position `2` does not have the correct form for an iterator.
          String str = Errors.getMessage("itform", F.list(list, F.ZZ(position)), EvalEngine.get());
          throw new ArgumentTypeException(str);

        // lowerLimit = null;
        // upperLimit = null;
        // step = null;
        // variable = null;
      }
      checkAppropriateBounds(list, lowerLimit, upperLimit, step);
      return new ExprIterator(variable, lowerLimit, upperLimit, step, fNumericMode, evalEngine);
    } catch (LimitException le) {
      throw le;
    } catch (ArgumentTypeException atex) {
      throw atex;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      // Argument `1` at position `2` does not have the correct form for an iterator.
      String str = Errors.getMessage("itform", F.list(list, F.ZZ(position)), EvalEngine.get());
      throw new ArgumentTypeException(str);
    } finally {
      evalEngine.setNumericMode(oldNumericMode);
    }
  }

  /**
   * Iterator specification for functions like <code>Table()</code> or <code>Sum()</code> or <code>
   * Product()</code>
   *
   * @param list a list representing an iterator specification
   * @param symbol the variable symbol
   * @param engine the evaluation engine
   * @return the iterator
   */
  public static IIterator<IExpr> create(final IAST list, final ISymbol symbol,
      final EvalEngine engine) {
    EvalEngine evalEngine = engine;
    IExpr lowerLimit;
    IExpr upperLimit;
    IExpr step;
    ISymbol variable;
    boolean fNumericMode;

    if (symbol != null && !isIteratorVariable(symbol)) {
      // Cannot assign to raw object `1`.
      throw new ArgumentTypeException(
          Errors.getMessage("setraw", F.list(symbol), EvalEngine.get()));
    }

    boolean localNumericMode = evalEngine.isNumericMode();
    try {

      fNumericMode = evalEngine.isNumericMode();
      switch (list.size()) {
        case 2:
          // if (list.hasNumericArgument()) {
          // evalEngine.setNumericMode(true);
          // }
          lowerLimit = F.C1;
          upperLimit = evalEngine.evalWithoutNumericReset(list.arg1());
          step = F.C1;
          variable = symbol;
          // if (upperLimit instanceof INum) {
          // return new DoubleIterator(variable, 1.0, ((INum) upperLimit).doubleValue(), 1.0);
          // }
          if (upperLimit.isInteger()) {
            try {
              int iUpperLimit = ((IInteger) upperLimit).toInt();
              return new IntIterator(symbol, 1, iUpperLimit, 1);
            } catch (ArithmeticException ae) {
              //
            }
          }
          if (upperLimit.isRational()) {
            try {
              return new RationalIterator(symbol, F.C1, (IRational) upperLimit, F.C1);
            } catch (ArithmeticException ae) {
              //
            }
          } else if (upperLimit.isQuantity()) {
            return new QuantityIterator(symbol, (IAST) upperLimit);
          } else if (upperLimit.isReal()) {
            return new RealIterator(variable, F.C1, (IReal) upperLimit, F.C1);
          }
          break;
        case 3:
          lowerLimit = evalEngine.evalWithoutNumericReset(list.arg1());
          upperLimit = evalEngine.evalWithoutNumericReset(list.arg2());
          step = F.C1;
          variable = symbol;
          if (upperLimit.isListOrAssociation()) {
            if (variable != null) {
              if (!isIteratorVariable(variable)) {
                // Cannot assign to raw object `1`.
                throw new ArgumentTypeException(
                    Errors.getMessage("setraw", F.list(variable), EvalEngine.get()));
              }
            } else {
              // Raw object `1` cannot be used as an iterator.
              throw new ArgumentTypeException(
                  Errors.getMessage("itraw", F.list(list.arg1()), EvalEngine.get()));
            }
            return new ExprListIterator(variable, (IAST) upperLimit, evalEngine);
          }
          if (lowerLimit instanceof INum && upperLimit instanceof INum) {
            return new DoubleIterator(variable, ((INum) lowerLimit).doubleValue(),
                ((INum) upperLimit).doubleValue(), 1.0);
          }
          if (lowerLimit.isInteger() && upperLimit.isInteger()) {
            try {
              int iLowerLimit = ((IInteger) lowerLimit).toInt();
              int iUpperLimit = ((IInteger) upperLimit).toInt();
              return new IntIterator(symbol, iLowerLimit, iUpperLimit, 1);
            } catch (ArithmeticException ae) {
              //
            }
          }
          if (lowerLimit.isRational() && upperLimit.isRational()) {
            try {
              return new RationalIterator(symbol, (IRational) lowerLimit, (IRational) upperLimit,
                  F.C1);
            } catch (ArithmeticException ae) {
              //
            }
          } else if (lowerLimit.isQuantity() && upperLimit.isQuantity()) {
            return new QuantityIterator(symbol, (IAST) lowerLimit, (IAST) upperLimit);
          } else if (lowerLimit.isReal() && upperLimit.isReal()) {
            return new RealIterator(variable, (IReal) lowerLimit, (IReal) upperLimit, F.C1);
          }
          break;
        case 4:
          if (list.hasNumericArgument()) {
            evalEngine.setNumericMode(true);
          }
          lowerLimit = evalEngine.evalWithoutNumericReset(list.arg1());
          upperLimit = evalEngine.evalWithoutNumericReset(list.arg2());
          step = evalEngine.evalWithoutNumericReset(list.arg3());
          checkNonZeroStep(list, step);
          variable = symbol;
          if (lowerLimit instanceof INum && upperLimit instanceof INum && step instanceof INum) {
            return new DoubleIterator(variable, ((INum) lowerLimit).doubleValue(),
                ((INum) upperLimit).doubleValue(), ((INum) step).doubleValue());
          }
          if (lowerLimit.isInteger() && upperLimit.isInteger() && step.isInteger()) {
            try {
              int iLowerLimit = ((IInteger) lowerLimit).toInt();
              int iUpperLimit = ((IInteger) upperLimit).toInt();
              int iStep = ((IInteger) step).toInt();
              return new IntIterator(symbol, iLowerLimit, iUpperLimit, iStep);
            } catch (ArithmeticException ae) {
              //
            }
          }
          if (lowerLimit.isRational() && upperLimit.isRational() && step.isRational()) {
            try {
              return new RationalIterator(symbol, (IRational) lowerLimit, (IRational) upperLimit,
                  (IRational) step);
            } catch (ArithmeticException ae) {
              //
            }
          } else if (lowerLimit.isQuantity() && upperLimit.isQuantity() && step.isQuantity()) {
            return new QuantityIterator(symbol, (IAST) lowerLimit, (IAST) upperLimit, (IAST) step);
          } else if (lowerLimit.isReal() && upperLimit.isReal() && step.isReal()) {
            return new RealIterator(variable, (IReal) lowerLimit, (IReal) upperLimit, (IReal) step);
          }
          break;
        default:
          lowerLimit = null;
          upperLimit = null;
          step = null;
          variable = null;
          return new ExprIterator(variable, lowerLimit, upperLimit, step, fNumericMode, evalEngine);
      }
      checkAppropriateBounds(list, lowerLimit, upperLimit, step);
      return new ExprIterator(variable, lowerLimit, upperLimit, step, fNumericMode, evalEngine);
    } catch (LimitException le) {
      throw le;
    } catch (ArgumentTypeException atex) {
      throw atex;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      throw new ClassCastException();
    } finally {
      evalEngine.setNumericMode(localNumericMode);
    }
  }

  /**
   * An iterator specification which is turned into an iterator only when the iteration over it
   * starts, i.e. after the iterators in front of it have assigned their variables. Its bounds may
   * therefore depend on those variables in any way, as in
   * <code>Table(x, {i, 3}, {x, 0, list[[i]]})</code> or <code>Table(x, {i, 3}, {x, 0, If(IntegerQ(i), i, 0)})</code>.
   * Creating all iterators up front evaluated such a bound while <code>i</code> had no value - or
   * a global one - and froze the wrong range for every outer step.
   */
  private static final class LazyIterator implements IIterator<IExpr> {
    /** The iterator specification; a list, or an expression which should evaluate to a count */
    private final IExpr spec;

    /** The position of {@link #spec} in the calling function, for messages */
    private final int position;

    private final EvalEngine engine;

    /** The iterator for the current values of the outer iterator variables */
    private IIterator<IExpr> delegate;

    private LazyIterator(IExpr spec, int position, EvalEngine engine) {
      this.spec = spec;
      this.position = position;
      this.engine = engine;
    }

    private IIterator<IExpr> newDelegate() {
      delegate = null;
      if (spec.isList()) {
        delegate = create((IAST) spec, position, engine);
      } else {
        IExpr count = engine.evaluate(spec);
        if (!count.isReal()) {
          // Non-list iterator `1` at position `2` does not evaluate to a real numeric value.
          throw new ArgumentTypeException(
              Errors.getMessage("nliter", F.list(spec, F.ZZ(position)), engine));
        }
        delegate = create(F.list(count), position, engine);
      }
      return delegate;
    }

    @Override
    public boolean setUp() {
      return newDelegate().setUp();
    }

    @Override
    public boolean setUpThrow() throws FlowControlException {
      return newDelegate().setUpThrow();
    }

    @Override
    public void tearDown() {
      if (delegate != null) {
        delegate.tearDown();
      }
    }

    @Override
    public boolean hasNext() {
      return delegate.hasNext();
    }

    @Override
    public IExpr next() {
      return delegate.next();
    }

    @Override
    public int allocHint() {
      return delegate.allocHint();
    }

    @Override
    public IExpr getLowerLimit() {
      return delegate.getLowerLimit();
    }

    @Override
    public IExpr getUpperLimit() {
      return delegate.getUpperLimit();
    }

    @Override
    public IExpr getStep() {
      return delegate.getStep();
    }

    @Override
    public ISymbol getVariable() {
      return delegate.getVariable();
    }

    @Override
    public boolean isNumericFunction() {
      return delegate.isNumericFunction();
    }

    @Override
    public boolean isSetIterator() {
      return delegate.isSetIterator();
    }

    @Override
    public boolean isValidVariable() {
      return delegate.isValidVariable();
    }

    @Override
    public boolean isUniform() {
      return delegate.isUniform();
    }

    @Override
    public boolean isInvalidNumeric() {
      return delegate.isInvalidNumeric();
    }
  }

  /**
   * The iterators for the specifications <code>ast.get(2), ast.get(3), ...</code> of a
   * <code>Table</code>, <code>Do</code>, <code>Sum</code> or <code>Product</code> call.
   *
   * <p>
   * Each iterator is created only when the iteration over it starts, so that its bounds are
   * evaluated with the current values of the variables of the iterators in front of it. Only the
   * form of each specification, which does not need any evaluation, is checked here.
   *
   * @param ast <code>head(body, spec1, spec2, ...)</code>
   * @param makeList if <code>true</code> a specification which is not a list is taken as the only
   *        element of a list; otherwise it has to evaluate to a real count
   * @param engine the evaluation engine
   * @return the iterators in the order of the specifications
   * @throws ArgumentTypeException if a specification does not have the form of an iterator
   */
  public static java.util.List<IIterator<IExpr>> createIterators(final IAST ast, boolean makeList,
      final EvalEngine engine) {
    java.util.List<IIterator<IExpr>> iterList = new java.util.ArrayList<IIterator<IExpr>>();
    for (int i = 2; i < ast.size(); i++) {
      IExpr spec = ast.get(i);
      if (makeList) {
        spec = spec.makeList();
      }
      if (spec.isList()) {
        checkIteratorForm((IAST) spec, i);
      }
      iterList.add(new LazyIterator(spec, i, engine));
    }
    return iterList;
  }

  /**
   * Check the form of the iterator specification <code>list</code> without evaluating any part of
   * it: <code>{max}</code>, <code>{var, max}</code>, <code>{var, min, max}</code> or
   * <code>{var, min, max, step}</code> with a variable <code>var</code> which can be assigned.
   *
   * @param list the iterator specification
   * @param position the position of <code>list</code> in the calling function
   * @throws ArgumentTypeException if <code>list</code> does not have the form of an iterator
   */
  private static void checkIteratorForm(final IAST list, int position)
      throws ArgumentTypeException {
    if (list.size() < 2 || list.size() > 5) {
      // Argument `1` at position `2` does not have the correct form for an iterator.
      throw new ArgumentTypeException(
          Errors.getMessage("itform", F.list(list, F.ZZ(position)), EvalEngine.get()));
    }
    if (list.size() > 2) {
      if (!list.arg1().isSymbol()) {
        // Raw object `1` cannot be used as an iterator.
        throw new ArgumentTypeException(
            Errors.getMessage("itraw", F.list(list.arg1()), EvalEngine.get()));
      }
      if (!isIteratorVariable((ISymbol) list.arg1())) {
        // Cannot assign to raw object `1`.
        throw new ArgumentTypeException(
            Errors.getMessage("setraw", F.list(list.arg1()), EvalEngine.get()));
      }
    }
  }

  /**
   * Like {@link #create(IAST, int, EvalEngine)}, but the bounds are evaluated while the
   * <code>localVariables</code> have no value. <code>Sum</code> and <code>Product</code> reduce
   * their innermost iterator first; the variables of the outer iterators must stay symbolic in its
   * bounds and not take a global value.
   *
   * @param list the iterator specification
   * @param position the position of <code>list</code> in the calling function
   * @param localVariables the variables of the outer iterators
   * @param engine the evaluation engine
   * @return the iterator
   */
  public static IIterator<IExpr> createLocal(final IAST list, int position,
      final IAST localVariables, final EvalEngine engine) {
    if (localVariables.argSize() == 0) {
      return create(list, position, engine);
    }
    java.util.List<IIterator<IExpr>> result = new java.util.ArrayList<IIterator<IExpr>>(1);
    engine.evalBlock(() -> {
      result.add(create(list, position, engine));
      return S.Null;
    }, localVariables);
    return result.get(0);
  }

  /**
   * The variables of all iterator specifications of <code>ast</code> but the last, except the
   * variable of the last one. The innermost iterator of <code>Sum</code> or <code>Product</code>
   * is reduced on its own; these are the variables which must stay symbolic while that happens.
   *
   * @param ast <code>head(body, spec1, spec2, ..., specN)</code>
   * @return a list of symbols, possibly empty
   */
  public static IAST outerIteratorVariables(final IAST ast) {
    IExpr last = ast.last();
    IExpr innerVariable = last.isList() && last.size() > 2 ? last.first() : F.NIL;
    IASTAppendable result = F.ListAlloc(ast.size());
    for (int i = 2; i < ast.size() - 1; i++) {
      IExpr spec = ast.get(i);
      if (spec.isList() && spec.size() > 2 && spec.first().isVariable()
          && !spec.first().equals(innerVariable) && !result.contains(spec.first())) {
        result.append(spec.first());
      }
    }
    return result;
  }

  /**
   * Throws {@link ArgumentTypeException} if the range specification does not have appropriate
   * bounds.
   * 
   * @param list
   * @param lowerLimit
   * @param upperLimit
   * @param step
   */
  public static void checkAppropriateBounds(final IAST list, IExpr lowerLimit, IExpr upperLimit,
      IExpr step) throws ArgumentTypeException {
    if (step.isRealResult() && lowerLimit.isRealResult() && upperLimit.hasComplexNumber()) {
      // Range specification in `1` does not have appropriate bounds.
      String str = Errors.getMessage("range", F.list(list), EvalEngine.get());
      throw new ArgumentTypeException(str);
    }
  }

  /**
   * Test if <code>symbol</code> can be the variable of an iterator. A {@link FormalSymbol} is
   * Protected and still qualifies: the evaluating functions replace it by a fresh symbol first (see
   * {@link #evaluateWithLocalizedVariables(IAST, EvalEngine)}), and <code>TeXForm</code> or
   * <code>MathMLForm</code> only read the iterator of a <code>Sum</code> which contains it.
   */
  private static boolean isIteratorVariable(ISymbol symbol) {
    return symbol.isVariable()
        && (!symbol.hasProtectedAttribute() || symbol instanceof FormalSymbol);
  }

  /**
   * Reject a step of <code>0</code>, which would make the iterator run forever. The range iterators
   * advance by <code>count += step</code> and stop by comparing against the upper limit, so a step
   * of <code>0</code> never terminates.
   *
   * @throws ArgumentTypeException if <code>step</code> is zero
   */
  private static void checkNonZeroStep(final IAST list, IExpr step) throws ArgumentTypeException {
    if (step.isZero()) {
      // Iterator does not have appropriate bounds.
      throw new ArgumentTypeException(Errors.getMessage("iterb", F.list(list), EvalEngine.get()));
    }
  }

  /**
   * Evaluate a <code>Table</code>, <code>Sum</code>, <code>Product</code> or <code>Do</code> whose
   * iterator variables include a subscript such as <code>Subscript[a, 1]</code> or a
   * {@link FormalSymbol} such as <code>\[FormalK]</code>.
   *
   * <p>
   * An iterator gives its variable a value. A subscript can't hold one, and a formal symbol is
   * shared by every evaluation on every thread and must never hold one. Each such iterator variable
   * is therefore replaced by a fresh symbol throughout the call - in the body and in the bounds of
   * the other iterators - the call is evaluated, and the original variable is put back into
   * whatever of the result still mentions it.
   *
   * @param ast the call, <code>head[body, iterator1, iterator2, ...]</code>, unevaluated
   * @return <code>null</code> when the caller has to evaluate <code>ast</code> itself: no iterator
   *         variable needs a fresh symbol, or only subscripts did and the call does not evaluate;
   *         {@link F#NIL} when a formal symbol was replaced and the call does not evaluate - the
   *         caller must not go on with the formal symbol, which it would have to assign; otherwise
   *         the result
   */
  public static IExpr evaluateWithLocalizedVariables(final IAST ast, EvalEngine engine) {
    boolean formalSymbol = false;
    java.util.Map<IExpr, IExpr> forward = null;
    for (int i = 2; i < ast.size(); i++) {
      IExpr iterator = ast.get(i);
      // {i, imax}, {i, imin, imax}, ... - in {imax} the first element is a count, not a variable
      if (iterator.isList() && iterator.argSize() >= 2) {
        IExpr variable = iterator.first();
        if (forward != null && forward.containsKey(variable)) {
          continue;
        }
        if (variable.isSubscript()) {
          if (forward == null) {
            forward = new java.util.HashMap<IExpr, IExpr>();
          }
          forward.put(variable, F.Dummy("Subscript" + EvalEngine.uniqueName("$")));
        } else if (variable instanceof FormalSymbol) {
          if (forward == null) {
            forward = new java.util.HashMap<IExpr, IExpr>();
          }
          forward.put(variable, localVariable((FormalSymbol) variable));
          formalSymbol = true;
        }
      }
    }
    if (forward == null) {
      return null;
    }
    IExpr result = evaluateRenamed(ast, forward, engine);
    return result.isPresent() || formalSymbol ? result : null;
  }

  /**
   * A fresh symbol which stands in for the formal symbol <code>variable</code> while a localizing
   * construct is evaluated, named like the local variables of <code>Module</code>.
   *
   * @param variable the formal symbol
   * @return a new {@link Context#DUMMY} symbol
   */
  public static ISymbol localVariable(FormalSymbol variable) {
    return F.Dummy(variable.getSymbolName() + EvalEngine.uniqueName("$"));
  }

  /**
   * Replace the keys of <code>forward</code> by their values throughout <code>ast</code>, evaluate
   * the result, and put the keys back into it.
   *
   * @param ast the unevaluated call
   * @param forward maps each variable to the fresh symbol which replaces it
   * @return {@link F#NIL} if the call does not evaluate
   */
  public static IExpr evaluateRenamed(final IAST ast, java.util.Map<IExpr, IExpr> forward,
      EvalEngine engine) {
    java.util.Map<IExpr, IExpr> back = new java.util.HashMap<IExpr, IExpr>();
    for (java.util.Map.Entry<IExpr, IExpr> entry : forward.entrySet()) {
      back.put(entry.getValue(), entry.getKey());
    }
    IExpr renamed = F.subst(ast, forward);
    IExpr result = F.subst(engine.evaluate(renamed), back);
    // an unevaluated call comes back as it went in; answering it would evaluate it again forever
    return result.equals(ast) ? F.NIL : result;
  }

}
