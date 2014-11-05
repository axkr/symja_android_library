package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Sum extends AbstractConverter {

	public Sum() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() >= 3) {
			return iteratorStep("\\sum_{", buf, f, 2);
		}
		return false;
	}

	public boolean iteratorStep(final String mathSymbol, final StringBuffer buf, final IAST f, int i) {
		if (i >= f.size()) {
			fFactory.convert(buf, f.arg1(), 0);
			return true;
		}
		if (f.get(i).isList()) {
			Iterator iterator = new Iterator((IAST) f.get(i), EvalEngine.get());
			if (iterator.isValidVariable() && iterator.getStep().isOne()) {
				buf.append(mathSymbol);
				fFactory.convert(buf, iterator.getVariable(), 0);
				buf.append(" = ");
				fFactory.convert(buf, iterator.getStart(), 0);
				buf.append("}^{");
				fFactory.convert(buf, iterator.getMaxCount(), 0);
				buf.append('}');
				if (!iteratorStep(mathSymbol, buf, f, i + 1)) {
					return false;
				}
				return true;
			}
		}
		return false;
	}
}