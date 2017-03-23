package org.matheclipse.core.eval.util;

import java.util.List;
import java.util.function.Function;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.interfaces.IArrayFunction;
import org.matheclipse.core.generic.interfaces.IIterator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * Table structure generator (i.e. lists, vectors, matrices, tensors)
 */
public class TableGenerator {

	final List<? extends IIterator<IExpr>> fIterList;

	final IExpr fDefaultValue;

	final IAST fPrototypeList;

	final IArrayFunction fFunction;

	int fIndex;

	private IExpr[] fCurrentIndex;

	public TableGenerator(final List<? extends IIterator<IExpr>> iterList, final IAST prototypeList,
			final IArrayFunction function) {
		this(iterList, prototypeList, function, (IExpr) null);
	}

	public TableGenerator(final List<? extends IIterator<IExpr>> iterList, final IAST prototypeList,
			final IArrayFunction function, IExpr defaultValue) {
		fIterList = iterList;
		fPrototypeList = prototypeList;
		fFunction = function;
		fIndex = 0;
		fCurrentIndex = new IExpr[iterList.size()];
		fDefaultValue = defaultValue;
	}

	public IExpr table() {
		if (fIndex < fIterList.size()) {
			final IIterator<IExpr> iter = fIterList.get(fIndex);

			if (iter.setUp()) {
				try {
					final int index = fIndex++;
					if (fPrototypeList.head().equals(F.Plus) || fPrototypeList.head().equals(F.Times)) {
						if (iter.hasNext()) {
							fCurrentIndex[index] = iter.next();
							IExpr temp = table();
							if (temp == null) {
								temp = fDefaultValue;
							}
							if (temp.isNumber()) {
								if (fPrototypeList.head().equals(F.Plus)) {
									return tablePlus(temp, iter, index);
								} else {
									return tableTimes(temp, iter, index);
								}
							} else {
								return createGenericTable(iter, index, iter.allocHint(), temp, null);
							}
						}
					}
					return createGenericTable(iter, index, iter.allocHint(), null, null);
				} finally {
					--fIndex;
					iter.tearDown();
				}
			}
			return fDefaultValue;

		}
		return fFunction.evaluate(fCurrentIndex);
	}

	private IExpr tablePlus(IExpr temp, final IIterator<IExpr> iter, final int index) {
		INumber num;
		int counter = 0;
		num = (INumber) temp;
		while (iter.hasNext()) {
			fCurrentIndex[index] = iter.next();
			temp = table();
			if (temp == null) {
				temp = fDefaultValue;
			}
			if (temp.isNumber()) {
				num = (INumber) num.plus((INumber) temp);
			} else {
				return createGenericTable(iter, index, iter.allocHint() - counter, num, temp);
			}
			counter++;
		}
		return num;
	}

	private IExpr tableTimes(IExpr temp, final IIterator<IExpr> iter, final int index) {
		INumber num;
		int counter = 0;
		num = (INumber) temp;
		while (iter.hasNext()) {
			fCurrentIndex[index] = iter.next();
			temp = table();
			if (temp == null) {
				temp = fDefaultValue;
			}
			if (temp.isNumber()) {
				num = (INumber) num.times((INumber) temp);
			} else {
				return createGenericTable(iter, index, iter.allocHint() - counter, num, temp);
			}
			counter++;
		}
		return num;
	}

	/**
	 * 
	 * @param iter
	 *            the current Iterator index
	 * @param index
	 *            index
	 * @return
	 */
	private IExpr createGenericTable(final IIterator<IExpr> iter, final int index, final int allocationHint, IExpr arg1,
			IExpr arg2) {
		final IAST result = fPrototypeList.copyHead(fPrototypeList.size() + (allocationHint > 0 ? allocationHint : 0));
		result.appendArgs(fPrototypeList);
		if (arg1 != null) {
			result.append(arg1);
		}
		if (arg2 != null) {
			result.append(arg2);
		}
		while (iter.hasNext()) {
			fCurrentIndex[index] = iter.next();
			IExpr temp = table();
			if (temp == null) {
				result.append(fDefaultValue);
			} else {
				result.append(temp);
			}
		}
		return result;
	}
}
