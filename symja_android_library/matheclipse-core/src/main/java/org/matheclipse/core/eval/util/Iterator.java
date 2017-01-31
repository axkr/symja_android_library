package org.matheclipse.core.eval.util;

import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.LessEqual;
import static org.matheclipse.core.expression.F.Subtract;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.interfaces.IIterator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Iterator for functions like <code>Table()</code> or <code>Sum()</code> or
 * <code>Product()</code>
 * 
 * @see org.matheclipse.core.reflection.system.Product
 * @see org.matheclipse.core.reflection.system.Sum
 * @see org.matheclipse.core.reflection.system.Table
 */
public class Iterator implements IIterator<IExpr> {
	public static class IntIterator implements IIterator<IExpr> {
		int count;

		int lowerLimit;

		int upperLimit;

		int step;

		final ISymbol variable;

		final IExpr originalLowerLimit;

		final IExpr originalUpperLimit;

		final IExpr originalStep;

		public IntIterator(final ISymbol symbol, final int lowerLimit, final int upperLimit, final int step) {
			this.variable = symbol;
			this.lowerLimit = lowerLimit;
			this.upperLimit = upperLimit;
			this.step = step;
			this.originalLowerLimit = F.integer(lowerLimit);
			this.originalUpperLimit = F.integer(upperLimit);
			this.originalStep = F.integer(step);
		}

		@Override
		public int allocHint() {
			if (step < 0) {
				return (lowerLimit - upperLimit) / (-step) + 1;
			}
			return (upperLimit - lowerLimit) / step + 1;
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
		 * @return <code>true</code> if this enumeration contains more elements;
		 *         <code>false</code> otherwise.
		 */
		@Override
		public boolean hasNext() {
			if (step < 0) {
				return count >= upperLimit;
			}
			return count <= upperLimit;
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
			final IExpr temp = F.integer(count);
			if (variable != null) {
				variable.set(temp);
			}
			count += step;
			return temp;
		}

		/**
		 * Not implemented; throws UnsupportedOperationException
		 * 
		 */
		@Override
		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean setUp() {
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
				variable.pushLocalVariable(originalLowerLimit);
			}
			return true;
		}

		/**
		 * Method Declaration.
		 * 
		 */
		@Override
		public void tearDown() {
			if (variable != null) {
				variable.popLocalVariable();
			}
		}
	}

	/**
	 * Iterator specification for functions like <code>Table()</code> or
	 * <code>Sum()</code> or <code>Product()</code>
	 * 
	 * @param list
	 *            a list representing an iterator specification
	 * @param engine
	 *            the evaluation engine
	 * @return the iterator
	 */
	public static IIterator<IExpr> create(final IAST list, final EvalEngine engine) {

		EvalEngine evalEngine = engine;
		IExpr lowerLimit;
		IExpr upperLimit;
		IExpr step;
		ISymbol variable;
		boolean fNumericMode;
		// fNumericMode = evalEngine.isNumericMode() ||
		// list.isMember(Predicates.isNumeric(), false);
		boolean localNumericMode = evalEngine.isNumericMode();
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
				if (upperLimit.isInteger()) {
					try {
						int iUpperLimit = ((IInteger) upperLimit).toInt();
						return new IntIterator(variable, 1, iUpperLimit, 1);
					} catch (ArithmeticException ae) {
						//
					}
				}
				break;

			case 3:
				lowerLimit = F.C1;
				upperLimit = evalEngine.evalWithoutNumericReset(list.arg2());
				step = F.C1;

				if (list.arg1() instanceof ISymbol) {
					variable = (ISymbol) list.arg1();
				} else {
					variable = null;
				}
				if (upperLimit.isInteger()) {
					try {
						int iUpperLimit = ((IInteger) upperLimit).toInt();
						return new IntIterator(variable, 1, iUpperLimit, 1);
					} catch (ArithmeticException ae) {
						//
					}
				}

				break;
			case 4:
				lowerLimit = evalEngine.evalWithoutNumericReset(list.arg2());
				upperLimit = evalEngine.evalWithoutNumericReset(list.arg3());
				step = F.C1;

				if (list.arg1().isSymbol()) {
					variable = (ISymbol) list.arg1();
				} else {
					variable = null;
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
				break;

			case 5:
				lowerLimit = evalEngine.evalWithoutNumericReset(list.arg2());
				upperLimit = evalEngine.evalWithoutNumericReset(list.arg3());
				step = evalEngine.evalWithoutNumericReset(list.arg4());
				if (list.arg1() instanceof ISymbol) {
					variable = (ISymbol) list.arg1();
				} else {
					variable = null;
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

				break;
			default:
				lowerLimit = null;
				upperLimit = null;
				step = null;
				variable = null;
			}

			return new Iterator(variable, evalEngine, lowerLimit, upperLimit, step, fNumericMode);
		} finally {
			evalEngine.setNumericMode(localNumericMode);
		}
	}

	/**
	 * Iterator specification for functions like <code>Table()</code> or
	 * <code>Sum()</code> or <code>Product()</code>
	 * 
	 * @param list
	 *            a list representing an iterator specification
	 * @param symbol
	 *            the variable symbol
	 * @param engine
	 *            the evaluation engine
	 * @return the iterator
	 */
	public static IIterator<IExpr> create(final IAST list, final ISymbol symbol, final EvalEngine engine) {
		EvalEngine evalEngine = engine;
		IExpr lowerLimit;
		IExpr upperLimit;
		IExpr step;
		ISymbol variable;
		boolean fNumericMode;

		boolean localNumericMode = evalEngine.isNumericMode();
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
				variable = symbol;
				if (upperLimit.isInteger()) {
					try {
						int iUpperLimit = ((IInteger) upperLimit).toInt();
						return new IntIterator(symbol, 1, iUpperLimit, 1);
					} catch (ArithmeticException ae) {
						//
					}
				}
				break;
			case 3:
				lowerLimit = evalEngine.evalWithoutNumericReset(list.arg1());
				upperLimit = evalEngine.evalWithoutNumericReset(list.arg2());
				step = F.C1;
				variable = symbol;
				if (lowerLimit.isInteger() && upperLimit.isInteger()) {
					try {
						int iLowerLimit = ((IInteger) lowerLimit).toInt();
						int iUpperLimit = ((IInteger) upperLimit).toInt();
						return new IntIterator(symbol, iLowerLimit, iUpperLimit, 1);
					} catch (ArithmeticException ae) {
						//
					}
				}
				break;
			case 4:
				lowerLimit = evalEngine.evalWithoutNumericReset(list.arg1());
				upperLimit = evalEngine.evalWithoutNumericReset(list.arg2());
				step = evalEngine.evalWithoutNumericReset(list.arg3());
				variable = symbol;
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
				break;
			default:
				lowerLimit = null;
				upperLimit = null;
				step = null;
				variable = null;
			}
			Iterator iter = new Iterator(variable, evalEngine, lowerLimit, upperLimit, step, fNumericMode);
			// iter.originalStart = start;
			// iter.originalMaxCount = maxCounterOrList;
			// iter.originalStep = step;
			return iter;
		} finally {
			evalEngine.setNumericMode(localNumericMode);
		}
	}

	IExpr count;

	final boolean fNumericMode;

	EvalEngine evalEngine;

	IExpr lowerLimit;

	IExpr maxCounterOrList;

	/**
	 * If <code>maxCounterOrList</code> is a list the
	 * <code>maxCounterOrListIndex</code> attribute points to the current
	 * element.
	 */
	int maxCounterOrListIndex;

	IExpr step;

	final IExpr originalLowerLimit;

	final IExpr originalUpperLimit;

	final IExpr originalStep;

	final ISymbol variable;

	public Iterator(final ISymbol symbol, final EvalEngine engine, final IExpr originalStart,
			final IExpr originalMaxCount, final IExpr originalStep, boolean numericMode) {
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
	 * @return <code>true</code> if this enumeration contains more elements;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean hasNext() {
		if (maxCounterOrList == null) {// || (illegalIterator)) {
			throw NoEvalException.CONST;
		}
		if ((maxCounterOrList.isDirectedInfinity()) || (count.isDirectedInfinity())) {
			throw NoEvalException.CONST;
		}
		// if (count == null || count.isDirectedInfinity()) {
		// throw new NoEvalException();
		// }
		if (maxCounterOrList.isList()) {
			if (maxCounterOrListIndex <= ((IAST) maxCounterOrList).size()) {
				return true;
			}
			return false;
		} else {
			if (step.isZero()) {
				throw NoEvalException.CONST;
			}
			if (step.isSignedNumber()) {
				if (((ISignedNumber) step).isNegative()) {
					if (evalEngine.evalTrue(LessEqual(maxCounterOrList, count))) {
						return true;
					}
				} else {
					if (evalEngine.evalTrue(LessEqual(count, maxCounterOrList))) {
						return true;
					}
				}
			} else {
				IExpr sub = evalEngine.evaluate(Divide(Subtract(maxCounterOrList, count), step));
				if (sub.isSignedNumber()) {
					return !((ISignedNumber) sub).isNegative();
				}
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean isNumericFunction() {
		return originalLowerLimit.isNumericFunction() && originalStep.isNumericFunction()
				&& originalUpperLimit.isNumericFunction();
	}

	@Override
	public boolean isSetIterator() {
		return variable != null && originalUpperLimit != null && originalUpperLimit.isList();
	}

	@Override
	public boolean isValidVariable() {
		return variable != null && originalLowerLimit != null && originalStep != null && originalUpperLimit != null
				&& !originalUpperLimit.isList();
	}

	/**
	 * Returns the next element of this enumeration.
	 * 
	 * @return the next element of this enumeration.
	 */
	@Override
	public IExpr next() {
		if (variable != null) {
			variable.set(count);
		}
		final IExpr temp = count;
		if (maxCounterOrList.isList()) {
			if (maxCounterOrListIndex == ((IAST) maxCounterOrList).size()) {
				maxCounterOrListIndex++;
				return temp;
			}
			count = ((IAST) maxCounterOrList).get(maxCounterOrListIndex++);
		} else {
			count = evalEngine.evaluate(count.add(step));
		}
		return temp;
	}

	/**
	 * Not implemented; throws UnsupportedOperationException
	 * 
	 */
	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setUp() {
		lowerLimit = originalLowerLimit;
		if (!(originalLowerLimit.isSignedNumber())) {
			lowerLimit = evalEngine.evalWithoutNumericReset(originalLowerLimit);
		}
		maxCounterOrList = originalUpperLimit;
		if (!(originalUpperLimit.isSignedNumber())) {
			maxCounterOrList = evalEngine.evalWithoutNumericReset(originalUpperLimit);
		}
		// points to first element in maxCounterOrList if it's a list
		maxCounterOrListIndex = 1;

		step = originalStep;
		if (!(originalStep.isSignedNumber())) {
			step = evalEngine.evalWithoutNumericReset(originalStep);
		}
		if (step.isSignedNumber()) {
			if (step.isNegative()) {
				if (evalEngine.evaluate(Less(lowerLimit, maxCounterOrList)) == F.True) {
					return false;
				}
			} else {
				if (evalEngine.evaluate(Less(maxCounterOrList, lowerLimit)) == F.True) {
					return false;
				}
			}
		}
		if (maxCounterOrList.isList()) {
			count = maxCounterOrList.getAt(maxCounterOrListIndex++);
		} else {
			count = lowerLimit;
		}
		if (variable != null) {
			variable.pushLocalVariable(count);
		}
		return true;
	}

	/**
	 * Method Declaration.
	 * 
	 */
	@Override
	public void tearDown() {
		if (variable != null) {
			variable.popLocalVariable();
		}
		EvalEngine.get().setNumericMode(fNumericMode);
	}
}
