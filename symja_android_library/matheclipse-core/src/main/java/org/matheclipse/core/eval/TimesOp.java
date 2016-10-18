package org.matheclipse.core.eval;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.Times;

/**
 * 
 */
public class TimesOp {
	final Map<IExpr, IExpr> timesMap;
	boolean evaled;

	// IExpr numberValue;

	public TimesOp(final int size) {
		timesMap = new HashMap<IExpr, IExpr>(size);
		evaled = false;
		// numberValue = null;
	}

	/**
	 * Add or merge the <code>key, value</code> pair into the given
	 * <code>timesMap</code>.
	 * 
	 * @param key
	 *            the key expression
	 */
	public boolean addMerge(final IExpr key) {
		return addMerge(key, F.C1);
	}

	/**
	 * Add or merge the <code>key, value</code> pair into the given
	 * <code>timesMap</code>.
	 * 
	 * @param key
	 *            the key expression
	 * @param value
	 *            the value expression
	 */
	public boolean addMerge(final IExpr key, final IExpr value) {
		IExpr temp = timesMap.get(key);
		if (temp == null) {
			timesMap.put(key, value);
			return false;
		}
		// merge both values
		if (temp.isNumber() && value.isNumber()) {
			temp = temp.plus(value);
			if (temp.isOne()) {
				timesMap.remove(key);
				return true;
			}
		} else if (temp.head().equals(F.Plus)) {
			((IAST) temp).append(value);
		} else {
			temp = F.Plus(temp, value);
		}
		timesMap.put(key, temp);
		return true;
	}

	/**
	 * Get the current evaluated result of the summation as a
	 * <code>Plus()</code> expression with respecting the
	 * <code>OneIdentity</code> attribute.
	 * 
	 * @return
	 */
	public IExpr getProduct() {

		IAST result = F.Times();
		// if (numberValue != null && !numberValue.isZero()) {
		// if (numberValue.isComplexInfinity()) {
		// return numberValue;
		// }
		// result.add(numberValue);
		// }
		for (Map.Entry<IExpr, IExpr> element : timesMap.entrySet()) {
			if (element.getValue().isOne()) {
				final IExpr temp = element.getKey();
				if (temp.isPlus()) {
					result.appendArgs((IAST) temp);
				} else {
					result.append(temp);
				}
				continue;
			}
			result.append(F.Power(element.getValue(), element.getKey()));
		}
		return result.getOneIdentity(F.C1);
	}

	/**
	 * Evaluate <code>Times(a1, a2,...)</code>.
	 * 
	 * @param a0
	 * @param a1
	 * @return
	 */
	public static IExpr times(IAST timesAST) {
		IAST temp = EvalEngine.get().evalFlatOrderlessAttributesRecursive(timesAST);
		if (!temp.isPresent()) {
			temp = timesAST;
		}
		IExpr expr = Times.CONST.evaluate(temp, null);
		if (!expr.isPresent()) {
			return timesAST.getOneIdentity(F.C0);
		}
		return expr;
	}

	/**
	 * Evaluate <code>a0 * a2</code>.
	 * 
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static IExpr times(IExpr a1, IExpr a2) {
		IExpr expr = timesNull(a1, a2);
		if (expr == null) {
			return F.Times(a1, a2);
		}
		return expr;
	}

	public static IExpr timesNull(IExpr a1, IExpr a2) {
		IAST times = F.Times(a1, a2);
		IExpr temp = Times.CONST.evaluate(times, null);
		if (temp.isPresent()) {
			return temp;
		}
		return null;
	}
}
