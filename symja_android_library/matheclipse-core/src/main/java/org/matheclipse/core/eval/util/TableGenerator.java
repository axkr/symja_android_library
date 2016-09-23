package org.matheclipse.core.eval.util;

import java.util.List;

import org.matheclipse.core.generic.interfaces.IArrayFunction;
import org.matheclipse.core.generic.interfaces.IIterator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

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

	public TableGenerator(final List<? extends IIterator<IExpr>> iterList, final IAST prototypeList, final IArrayFunction function) {
		this(iterList, prototypeList, function, (IExpr) null);
	}

	public TableGenerator(final List<? extends IIterator<IExpr>> iterList, final IAST prototypeList, final IArrayFunction function,
			IExpr defaultValue) {
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
					final IAST result = fPrototypeList.clone();
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
				} finally {
					--fIndex;
					iter.tearDown();
				}
			}
			return fDefaultValue;

		}
		return fFunction.evaluate(fCurrentIndex);
	}
}
