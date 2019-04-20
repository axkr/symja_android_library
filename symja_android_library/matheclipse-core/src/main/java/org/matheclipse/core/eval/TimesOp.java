package org.matheclipse.core.eval;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

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
	 * Add or merge the <code>key, value</code> pair into the given <code>timesMap</code>.
	 * 
	 * @param key
	 *            the key expression
	 */
	public boolean addMerge(final IExpr key) {
		return addMerge(key, F.C1);
	}

	/**
	 * Add or merge the <code>key, value</code> pair into the given <code>timesMap</code>.
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
			if (!(temp instanceof IASTAppendable)) {
				temp = ((IAST) temp).copyAppendable();
			}
			((IASTAppendable) temp).append(value);
		} else {
			temp = F.Plus(temp, value);
		}
		timesMap.put(key, temp);
		return true;
	}

	/**
	 * Get the current evaluated result of the summation as a <code>Plus()</code> expression with respecting the
	 * <code>OneIdentity</code> attribute.
	 * 
	 * @return
	 */
	public IExpr getProduct() {

		IASTAppendable result = F.TimesAlloc(timesMap.size());
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
		return result.oneIdentity1();
	}

	/**
	 * Evaluate <code>Times(a1, a2,...)</code>.
	 * 
	 * @param a0
	 * @param a1
	 * @return
	 */
	public static IExpr times(IAST timesAST) {
		IAST temp = EvalEngine.get().evalFlatOrderlessAttributesRecursive(timesAST).orElse(timesAST);
		return Arithmetic.CONST_TIMES.evaluate(temp, EvalEngine.get()).orElseGet(() -> timesAST.oneIdentity0());
	}

	/**
	 * Evaluate <code>a0 * a2</code>.
	 * 
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static IExpr times(IExpr a1, IExpr a2) {
		final IAST times = F.Times(a1, a2);
		return Arithmetic.CONST_TIMES.evaluate(times, EvalEngine.get()).orElse(times);
	}

}
