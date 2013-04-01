package org.matheclipse.core.eval.util;

import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.LessEqual;
import static org.matheclipse.core.expression.F.Plus;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.generic.interfaces.IIterator;

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

	final IExpr maxCount;

	IExpr start;

	final IExpr step;

	final Symbol variable;

	public Iterator(final IAST lst, final EvalEngine sess) {
		// illegalIterator = false;
		evalEngine = sess;
		fNumericMode = evalEngine.isNumericMode() || lst.isMember(Predicates.isNumeric(), false);
		switch (lst.size()) {

		case 2:
			start = F.C1;
			maxCount = evalEngine.evalWithoutNumericReset(lst.get(1));
			step = F.C1;
			variable = null;

			break;
		case 3:
			start = F.C1;
			maxCount = evalEngine.evalWithoutNumericReset(lst.get(2));
			step = F.C1;

			if (lst.get(1) instanceof Symbol) {
				variable = (Symbol) lst.get(1);
			} else {
				variable = null;
			}

			break;
		case 4:
			start = evalEngine.evalWithoutNumericReset(lst.get(2));
			maxCount = evalEngine.evalWithoutNumericReset(lst.get(3));
			step = F.C1;

			// if (evalEngine.evaluate(LessEqual(start, maxCount)) != F.True) {
			// illegalIterator = true;
			// }
			if (lst.get(1) instanceof Symbol) {
				variable = (Symbol) lst.get(1);
			} else {
				variable = null;
			}

			break;
		case 5:
			start = evalEngine.evalWithoutNumericReset(lst.get(2));
			maxCount = evalEngine.evalWithoutNumericReset(lst.get(3));
			step = evalEngine.evalWithoutNumericReset(lst.get(4));
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
			if (lst.get(1) instanceof Symbol) {
				variable = (Symbol) lst.get(1);
			} else {
				variable = null;
			}

			break;
		default:
			maxCount = null;
			step = null;
			variable = null;

			// throw new NoIteratorException();

		}
	}

	public Iterator(final IAST lst, final Symbol symbol, final EvalEngine sess) {
		// illegalIterator = false;
		evalEngine = sess;
		fNumericMode = evalEngine.isNumericMode();
		switch (lst.size()) {

		case 2:
			start = F.C1;
			maxCount = evalEngine.evalWithoutNumericReset(lst.get(1));
			step = F.C1;
			variable = symbol;
			break;
		case 3:
			start = evalEngine.evalWithoutNumericReset(lst.get(1));
			maxCount = evalEngine.evalWithoutNumericReset(lst.get(2));
			step = F.C1;
			variable = symbol;
			// if (evalEngine.evaluate(LessEqual(start, maxCount)) != F.True) {
			// illegalIterator = true;
			// }
			break;
		case 4:
			start = evalEngine.evalWithoutNumericReset(lst.get(1));
			maxCount = evalEngine.evalWithoutNumericReset(lst.get(2));
			step = evalEngine.evalWithoutNumericReset(lst.get(3));
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
			maxCount = null;
			step = null;
			variable = null;

			// throw new NoIteratorException();

		}
	}

	/**
	 * Tests if this enumeration contains more elements.
	 * 
	 * @return <code>true</code> if this enumeration contains more elements;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasNext() {
		if ((maxCount == null)) {// || (illegalIterator)) {
			return false;
		}
		if (!(step instanceof ISignedNumber)) {
			return false;
		}
		if (((ISignedNumber) step).isNegative()) {
			if (evalEngine.evaluate(LessEqual(maxCount, count)) == F.True) {
				return true;
			}
		} else {
			if (evalEngine.evaluate(LessEqual(count, maxCount)) == F.True) {
				return true;
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
		count = evalEngine.evaluate(Plus(count, step));

		return temp;
	}

	public boolean setUp() {

		if (!(step instanceof ISignedNumber)) {
			return false;
		}
		if (((ISignedNumber) step).isNegative()) {
			if (evalEngine.evaluate(Less(start, maxCount)) == F.True) {
				return false;
			}
		} else {
			if (evalEngine.evaluate(Less(maxCount, start)) == F.True) {
				return false;
			}
		}
		count = start;
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
	 *          ast with Iterator specifications
	 * @param index
	 *          start index for iterstor specifications
	 * @param baseList
	 *          basic ast where the iterator elements are appended
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
	// return evalEngine.evaluate(ast.get(1));
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
