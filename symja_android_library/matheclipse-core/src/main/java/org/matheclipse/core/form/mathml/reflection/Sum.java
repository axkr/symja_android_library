package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Sum extends AbstractConverter {

	public Sum() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() >= 3) {
			// &sum; &#x2211
			return iteratorStep("&#x2211;", buf, f, 2);
		}
		return false;
	}

	public boolean iteratorStep(final String mathSymbol, final StringBuffer buf, final IAST f, int i) {
		if (i >= f.size()) {
			fFactory.convert(buf, f.arg1(), 0);
			return true;
		}
		fFactory.tagStart(buf, "mrow");
		if (f.get(i).isList()) {
			Iterator iterator = new Iterator((IAST) f.get(i), EvalEngine.get());
			if (iterator.isValidVariable() && iterator.getStep().isOne()) {
				fFactory.tagStart(buf, "munderover");
				fFactory.tag(buf, "mo", mathSymbol);

				fFactory.tagStart(buf, "mrow");
				fFactory.convert(buf, iterator.getVariable(), 0);
				fFactory.tag(buf, "mo", "=");
				fFactory.convert(buf, iterator.getStart(), 0);
				fFactory.tagEnd(buf, "mrow");
				fFactory.convert(buf, iterator.getMaxCount(), 0);
				fFactory.tagEnd(buf, "munderover");
				if (!iteratorStep(mathSymbol, buf, f, i + 1)) {
					return false;
				}
				fFactory.tagEnd(buf, "mrow");
				return true;
			}
		}
		return false;
	}

}