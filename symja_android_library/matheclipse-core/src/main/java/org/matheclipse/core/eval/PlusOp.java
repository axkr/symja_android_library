package org.matheclipse.core.eval;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <p>
 * Plus operator for adding multiple arguments with the <code>plus(argument)</code> method and returning the result, with the
 * <code>getSum()</code> method, if <code>isEvaled()</code> returns <code>true</code>.
 * </p>
 * See: <a href="http://www.cs.berkeley.edu/~fateman/papers/newsimp.pdf">Experiments in Hash-coded Algebraic Simplification</a>
 * 
 */
public class PlusOp {
	final Map<IExpr, IExpr> plusMap;
	boolean evaled;

	IExpr numberValue;

	/**
	 * Constructor.
	 * 
	 * @param size
	 *            the approximated size of the resulting <code>Plus()</code> AST.
	 */
	public PlusOp(final int size) {
		plusMap = new HashMap<IExpr, IExpr>(size + 5 + size / 10);
		evaled = false;
		numberValue = null;
	}

	/**
	 * Add or merge the <code>key, value</code> pair into the given <code>plusMap</code>.
	 * 
	 * @param key
	 *            the key expression
	 * @param value
	 *            the value expression
	 */
	private boolean addMerge(final IExpr key, final IExpr value) {
		IExpr temp = plusMap.get(key);
		if (temp == null) {
			plusMap.put(key, value);
			return false;
		}
		// merge both values
		if (temp.isNumber() && value.isNumber()) {
			temp = temp.plus(value);
			if (temp.isZero()) {
				plusMap.remove(key);
			}
		} else if (temp.head().equals(F.Plus)) {
			((IAST) temp).add(value);
		} else {
			temp = F.Plus(temp, value);
		}
		plusMap.put(key, temp);
		return true;
	}

	/**
	 * Get the current evaluated result of the summation as a <code>Plus()</code> expression with respecting the
	 * <code>OneIdentity</code> attribute.
	 * 
	 * @return
	 */
	public IExpr getSum() {

		IAST result = F.Plus();
		if (numberValue != null) {
			result.add(numberValue);
		}
		for (Map.Entry<IExpr, IExpr> element : plusMap.entrySet()) {
			if (element.getValue().isOne()) {
				final IExpr temp = element.getKey();
				if (temp.isPlus()) {
					result.addAll((IAST) temp);
				} else {
					result.add(temp);
				}
				continue;
			}
			result.add(F.Times(element.getValue(), element.getKey()));
		}
		result.addEvalFlags(IAST.IS_EVALED);
		return result.getOneIdentity(F.C0);
	}

	private IExpr infinityPlus(final IExpr o1) {
		if (o1.isInfinity()) {
			return F.CInfinity;
		} else if (o1.isNegativeInfinity()) {
			EvalEngine.get().printMessage("Indeterminate expression Infinity-Infinity");
			return F.Indeterminate;
		}
		return F.CInfinity;
	}

	/**
	 * Test if any evaluation occurred by calling the <code>plus()</code> method
	 * 
	 * @return <code>true</code> if an evaluation occurred.
	 */
	public boolean isEvaled() {
		return evaled;
	}

	private IExpr negativeInfinityPlus(final IExpr o1) {
		if (o1.isInfinity()) {
			EvalEngine.get().printMessage("Indeterminate expression Infinity-Infinity");
			return F.Indeterminate;
		} else if (o1.isNegativeInfinity()) {
			return F.CNInfinity;
		}
		return F.CNInfinity;
	}

	/**
	 * Add an argument <code>arg</code> to this <code>Plus()</code> expression.
	 * 
	 * @param arg
	 * @return <code>F.Indeterminate</code> if the result is indeterminated, <code>null</code> otherwise.
	 */
	public IExpr plus(final IExpr arg) {

		if (arg.isASTSizeGE(F.Plus, 2)) {
			// flatten the Plus() argument
			final IAST plusAST = (IAST) arg;
			for (int i = 1; i < plusAST.size(); i++) {
				// recursive call to plus()
				final IExpr temp = plus(plusAST.get(i));
				if (temp != null) {
					return temp;
				}
			}
			return null;
		}

		if (arg.isIndeterminate()) {
			return F.Indeterminate;
		} else if (arg.isDirectedInfinity()) {
			if (arg.isInfinity()) {
				if (numberValue == null) {
					numberValue = arg;
					return null;
				}
				numberValue = infinityPlus(numberValue);
				if (numberValue.isIndeterminate()) {
					return F.Indeterminate;
				}
				evaled = true;
				return null;
			} else if (arg.isNegativeInfinity()) {
				if (numberValue == null) {
					numberValue = arg;
					return null;
				}
				numberValue = negativeInfinityPlus(numberValue);
				if (numberValue.isIndeterminate()) {
					return F.Indeterminate;
				}
				evaled = true;
				return null;
			} else if (arg.isComplexInfinity()) {
				if (numberValue == null) {
					numberValue = arg;
					return null;
				}
				if (numberValue.isNumber()) {
					numberValue = F.CComplexInfinity;
					evaled = true;
					return null;
				}
				return F.Indeterminate;
			}
		} else if (arg.isNumber()) {
			if (arg.isZero()) {
				evaled = true;
				return null;
			}
			if (numberValue == null) {
				numberValue = arg;
				return null;
			}
			if (numberValue.isNumber()) {
				numberValue = numberValue.plus(arg);
				evaled = true;
				return null;
			}
			if (numberValue.isInfinity()) {
				numberValue = infinityPlus(arg);
				if (numberValue.isIndeterminate()) {
					return F.Indeterminate;
				}
				evaled = true;
				return null;
			}
			if (numberValue.isNegativeInfinity()) {
				numberValue = negativeInfinityPlus(arg);
				if (numberValue.isIndeterminate()) {
					return F.Indeterminate;
				}
				evaled = true;
				return null;
			}
			return null;
		} else if (arg.isTimes()) {
			IAST timesAST = (IAST) arg;
			if (timesAST.arg1().isNumber()) {
				if (addMerge(timesAST.removeAtClone(1).getOneIdentity(F.C1), timesAST.arg1())) {
					evaled = true;
				}
				return null;
			}
			if (addMerge(timesAST, F.C1)) {
				evaled = true;
			}
			return null;
		}
		if (addMerge(arg, F.C1)) {
			evaled = true;
		}
		return null;
	}
}
