package org.matheclipse.core.eval.util;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.generic.interfaces.IIterator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;

/**
 * Iterator for functions like Product[] or Sum[]
 * 
 * @see org.matheclipse.core.reflection.system.Product
 * @see org.matheclipse.core.reflection.system.Sum
 */
public class Iterator implements IIterator<IExpr> {
	IExpr count;

	// boolean illegalIterator;
	final boolean fNumericMode;

	EvalEngine evalEngine;

	IExpr start;

	IExpr maxCounterOrList;

	/**
	 * If <code>maxCounterOrList</code> is a list the <code>maxCounterOrListIndex</code> attribute points to the current element.
	 */
	int maxCounterOrListIndex;

	IExpr step;

	final IExpr originalStart;

	final IExpr originalMaxCount;

	final IExpr originalStep;

	final Symbol variable;

	public Iterator(final IAST lst, final EvalEngine sess) {
		// illegalIterator = false;
		evalEngine = sess;
		fNumericMode = evalEngine.isNumericMode() || lst.isMember(Predicates.isNumeric(), false);
		switch (lst.size()) {

		case 2:
			start = F.C1;
			maxCounterOrList = evalEngine.evalWithoutNumericReset(lst.arg1());
			step = F.C1;
			variable = null;

			break;
		case 3:
			start = F.C1;
			maxCounterOrList = evalEngine.evalWithoutNumericReset(lst.arg2());
			step = F.C1;

			if (lst.arg1() instanceof Symbol) {
				variable = (Symbol) lst.arg1();
			} else {
				variable = null;
			}

			break;
		case 4:
			start = evalEngine.evalWithoutNumericReset(lst.arg2());
			maxCounterOrList = evalEngine.evalWithoutNumericReset(lst.arg3());
			step = F.C1;

			// if (evalEngine.evaluate(LessEqual(start, maxCount)) != F.True) {
			// illegalIterator = true;
			// }
			if (lst.arg1().isSymbol()) {
				variable = (Symbol) lst.arg1();
			} else {
				variable = null;
			}

			break;
		case 5:
			start = evalEngine.evalWithoutNumericReset(lst.arg2());
			maxCounterOrList = evalEngine.evalWithoutNumericReset(lst.arg3());
			step = evalEngine.evalWithoutNumericReset(lst.arg4());
			// if (!(step instanceof ISignedNumber)) {
			// illegalIterator = true;
			// }
			// if (((ISignedNumber) step).isNegative()) {
			// if ((evalEngine.evaluate(Less(maxCount, start)) != F.True)) {
			// illegalIterator = true;
			// }
			// } else {
			// if ((evalEngine.evaluate(LessEqual(start, maxCount)) != F.True)) {
			// illegalIterator = true;
			// }
			// }
			if (lst.arg1() instanceof Symbol) {
				variable = (Symbol) lst.arg1();
			} else {
				variable = null;
			}

			break;
		default:
			start = null;
			maxCounterOrList = null;
			step = null;
			variable = null;

			// throw new NoIteratorException();

		}
		originalStart = start;
		originalMaxCount = maxCounterOrList;
		originalStep = step;
	}

	public Iterator(final IAST lst, final Symbol symbol, final EvalEngine sess) {
		// illegalIterator = false;
		evalEngine = sess;
		fNumericMode = evalEngine.isNumericMode();
		switch (lst.size()) {

		case 2:
			start = F.C1;
			maxCounterOrList = evalEngine.evalWithoutNumericReset(lst.arg1());
			step = F.C1;
			variable = symbol;
			break;
		case 3:
			start = evalEngine.evalWithoutNumericReset(lst.arg1());
			maxCounterOrList = evalEngine.evalWithoutNumericReset(lst.arg2());
			step = F.C1;
			variable = symbol;
			// if (evalEngine.evaluate(LessEqual(start, maxCount)) != F.True) {
			// illegalIterator = true;
			// }
			break;
		case 4:
			start = evalEngine.evalWithoutNumericReset(lst.arg1());
			maxCounterOrList = evalEngine.evalWithoutNumericReset(lst.arg2());
			step = evalEngine.evalWithoutNumericReset(lst.arg3());
			variable = symbol;
			// if (!(step instanceof ISignedNumber)) {
			// illegalIterator = true;
			// }
			// if (((ISignedNumber) step).isNegative()) {
			// if ((evalEngine.evaluate(Less(maxCount, start)) != F.True)) {
			// illegalIterator = true;
			// }
			// } else {
			// if ((evalEngine.evaluate(LessEqual(start, maxCount)) != F.True)) {
			// illegalIterator = true;
			// }
			// }

			break;
		default:
			start = null;
			maxCounterOrList = null;
			step = null;
			variable = null;

			// throw new NoIteratorException();
		}
		originalStart = start;
		originalMaxCount = maxCounterOrList;
		originalStep = step;
	}

	/**
	 * Tests if this enumeration contains more elements.
	 * 
	 * @return <code>true</code> if this enumeration contains more elements; <code>false</code> otherwise.
	 */
	public boolean hasNext() {
		if ((maxCounterOrList == null)) {// || (illegalIterator)) {
			throw new NoEvalException();
		}
		// if (!(step instanceof ISignedNumber)) {
		// throw new NoEvalException();
		// }
		if (maxCounterOrList.isList()) {
			if (maxCounterOrListIndex <= ((IAST) maxCounterOrList).size()) {
				return true;
			}
			return false;
		} else {
			if (step.isZero()) {
				throw new NoEvalException();
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

	/**
	 * Returns the next element of this enumeration.
	 * 
	 * @return the next element of this enumeration.
	 */
	public IExpr next() {
		if (variable != null) {
			variable.set(count);
			// variable.putDownRule(session, HRule.SET, variable, count);
		}
		final IExpr temp = count;
		if (maxCounterOrList.isList()) {
			if (maxCounterOrListIndex == ((IAST) maxCounterOrList).size()) {
				maxCounterOrListIndex++;
				return temp;
			}
			count = ((IAST) maxCounterOrList).get(maxCounterOrListIndex++);
		} else {
			count = evalEngine.evaluate(Plus(count, step));
		}
		return temp;
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
			// variable.clearSymbolRule(session, variable);
		}
		EvalEngine.get().setNumericMode(fNumericMode);
	}

	/**
	 * Helper method for functions like Product, Sum
	 * 
	 * @param ast
	 *            ast with Iterator specifications
	 * @param index
	 *            start index for iterstor specifications
	 * @param baseList
	 *            basic ast where the iterator elements are appended
	 * @return
	 */
	// public static IExpr iteratorLoop(IAST ast, int index, IAST baseList) {
	// EvalEngine evalEngine = EvalEngine.get();
	// if (index < ast.argsSize()) {
	// if (((IExpr) ast.getArg(index)).isList()) {
	// Iterator iter = null;
	// try {
	// iter = new Iterator((AST) ast.getArg(index), evalEngine);
	//
	// index++;
	// IAST lst = (IAST) baseList.clone();
	// IExpr temp;
	//
	// while (iter.hasNext()) {
	// temp = iteratorLoop(ast, index, baseList);
	//
	// if (temp != null) {
	// lst.add(temp);
	// }
	//
	// iter.next();
	// }
	// return lst;
	// } finally {
	// if (iter != null) {
	// iter.tearDown();
	// }
	// }
	// }
	// } else {
	// return evalEngine.evaluate(ast.arg1() );
	// }
	//
	// return null;
	// }
	/**
	 * Not implemented; throws UnsupportedOperationException
	 * 
	 */
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
