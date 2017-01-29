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
	IExpr count;

	final boolean fNumericMode;

	EvalEngine evalEngine;

	IExpr start;

	IExpr maxCounterOrList;

	/**
	 * If <code>maxCounterOrList</code> is a list the
	 * <code>maxCounterOrListIndex</code> attribute points to the current
	 * element.
	 */
	int maxCounterOrListIndex;

	IExpr step;

	final IExpr originalStart;

	final IExpr originalMaxCount;

	final IExpr originalStep;

	final ISymbol variable;

	/**
	 * Iterator specification for functions like <code>Table()</code> or
	 * <code>Sum()</code> or <code>Product()</code>
	 * 
	 * @param list
	 *            a list representing an iterator specification
	 * @param engine
	 *            the evaluation engine
	 * @see org.matheclipse.core.reflection.system.Product
	 * @see org.matheclipse.core.reflection.system.Sum
	 * @see org.matheclipse.core.reflection.system.Table
	 */
	public Iterator(final IAST list, final EvalEngine engine) {
		evalEngine = engine;
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
				start = F.C1;
				maxCounterOrList = evalEngine.evalWithoutNumericReset(list.arg1());
				step = F.C1;
				variable = null;

				break;
			case 3:
				start = F.C1;
				maxCounterOrList = evalEngine.evalWithoutNumericReset(list.arg2());
				step = F.C1;

				if (list.arg1() instanceof ISymbol) {
					variable = (ISymbol) list.arg1();
				} else {
					variable = null;
				}

				break;
			case 4:
				start = evalEngine.evalWithoutNumericReset(list.arg2());
				maxCounterOrList = evalEngine.evalWithoutNumericReset(list.arg3());
				step = F.C1;

				if (list.arg1().isSymbol()) {
					variable = (ISymbol) list.arg1();
				} else {
					variable = null;
				}

				break;
			case 5:
				start = evalEngine.evalWithoutNumericReset(list.arg2());
				maxCounterOrList = evalEngine.evalWithoutNumericReset(list.arg3());
				step = evalEngine.evalWithoutNumericReset(list.arg4());
				if (list.arg1() instanceof ISymbol) {
					variable = (ISymbol) list.arg1();
				} else {
					variable = null;
				}

				break;
			default:
				start = null;
				maxCounterOrList = null;
				step = null;
				variable = null;
			}
		} finally {
			evalEngine.setNumericMode(localNumericMode);
		}
		originalStart = start;
		originalMaxCount = maxCounterOrList;
		originalStep = step;
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
	 * @see org.matheclipse.core.reflection.system.Product
	 * @see org.matheclipse.core.reflection.system.Sum
	 * @see org.matheclipse.core.reflection.system.Table
	 */
	public Iterator(final IAST list, final ISymbol symbol, final EvalEngine engine) {
		evalEngine = engine;
		boolean localNumericMode = evalEngine.isNumericMode();
		try {
			if (list.hasNumericArgument()) {
				evalEngine.setNumericMode(true);
			}
			fNumericMode = evalEngine.isNumericMode();
			switch (list.size()) {

			case 2:
				start = F.C1;
				maxCounterOrList = evalEngine.evalWithoutNumericReset(list.arg1());
				step = F.C1;
				variable = symbol;
				break;
			case 3:
				start = evalEngine.evalWithoutNumericReset(list.arg1());
				maxCounterOrList = evalEngine.evalWithoutNumericReset(list.arg2());
				step = F.C1;
				variable = symbol;
				break;
			case 4:
				start = evalEngine.evalWithoutNumericReset(list.arg1());
				maxCounterOrList = evalEngine.evalWithoutNumericReset(list.arg2());
				step = evalEngine.evalWithoutNumericReset(list.arg3());
				variable = symbol;
				break;
			default:
				start = null;
				maxCounterOrList = null;
				step = null;
				variable = null;
			}
		} finally {
			evalEngine.setNumericMode(localNumericMode);
		}
		originalStart = start;
		originalMaxCount = maxCounterOrList;
		originalStep = step;
	}

	public IExpr getMaxCount() {
		return originalMaxCount;
	}

	public IExpr getStart() {
		return originalStart;
	}

	public IExpr getStep() {
		return originalStep;
	}

	public ISymbol getVariable() {
		return variable;
	}

	/**
	 * Tests if this enumeration contains more elements.
	 * 
	 * @return <code>true</code> if this enumeration contains more elements;
	 *         <code>false</code> otherwise.
	 */
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

	public boolean isValidVariable() {
		return variable != null && originalStart != null && originalStep != null && originalMaxCount != null
				&& !originalMaxCount.isList();
	}

	public boolean isNumericFunction() {
		return originalStart.isNumericFunction() && originalStep.isNumericFunction()
				&& originalMaxCount.isNumericFunction();
	}

	public boolean isSetIterator() {
		return variable != null && originalMaxCount != null && originalMaxCount.isList();
	}

	/**
	 * Returns the next element of this enumeration.
	 * 
	 * @return the next element of this enumeration.
	 */
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
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public boolean setUp() {
		start = originalStart;
		if (!(originalStart.isSignedNumber())) {
			start = evalEngine.evalWithoutNumericReset(originalStart);
		}
		maxCounterOrList = originalMaxCount;
		if (!(originalMaxCount.isSignedNumber())) {
			maxCounterOrList = evalEngine.evalWithoutNumericReset(originalMaxCount);
		}
		// points to first element in maxCounterOrList if it's a list
		maxCounterOrListIndex = 1;

		step = originalStep;
		if (!(originalStep.isSignedNumber())) {
			step = evalEngine.evalWithoutNumericReset(originalStep);
		}
		if (step.isSignedNumber()) {
			if (step.isNegative()) {
				if (evalEngine.evaluate(Less(start, maxCounterOrList)) == F.True) {
					return false;
				}
			} else {
				if (evalEngine.evaluate(Less(maxCounterOrList, start)) == F.True) {
					return false;
				}
			}
		}
		if (maxCounterOrList.isList()) {
			count = maxCounterOrList.getAt(maxCounterOrListIndex++);
		} else {
			count = start;
		}
		if (variable != null) {
			variable.pushLocalVariable(count);
		}
		return true;
	}

	/**
	 * Method Declaration.
	 * 
	 * @see
	 */
	public void tearDown() {
		if (variable != null) {
			variable.popLocalVariable();
		}
		EvalEngine.get().setNumericMode(fNumericMode);
	}
}
