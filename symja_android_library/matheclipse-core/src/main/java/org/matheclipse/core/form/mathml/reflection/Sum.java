package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.ISymbol;

public class Sum extends AbstractConverter {

	public Sum() {
	}

	/** {@inheritDoc} */
	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() >= 3) {
			// &sum; &#x2211
			return iteratorStep(buf, "&#x2211;", f, 2);
		}
		return false;
	}

	public boolean iteratorStep(final StringBuilder buf, final String mathSymbol, final IAST f, int i) {
		if (i >= f.size()) {
			fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
			return true;
		}
		fFactory.tagStart(buf, "mrow");
		if (f.get(i).isList()) {
			IIterator<IExpr> iterator = Iterator.create((IAST) f.get(i), EvalEngine.get());
			if (iterator.isValidVariable() && iterator.getStep().isOne()) {
				fFactory.tagStart(buf, "munderover");
				fFactory.tag(buf, "mo", mathSymbol);

				fFactory.tagStart(buf, "mrow");
				fFactory.convertSymbol(buf, iterator.getVariable());
				fFactory.tag(buf, "mo", "=");
				fFactory.convert(buf, iterator.getLowerLimit(), Integer.MIN_VALUE, false);
				fFactory.tagEnd(buf, "mrow");
				fFactory.convert(buf, iterator.getUpperLimit(), Integer.MIN_VALUE, false);
				fFactory.tagEnd(buf, "munderover");
				if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
					return false;
				}
				fFactory.tagEnd(buf, "mrow");
				return true;
			}
		} else if (f.get(i).isSymbol()) {
			ISymbol symbol = (ISymbol) f.get(i);
			fFactory.tagStart(buf, "munderover");
			fFactory.tag(buf, "mo", mathSymbol);
			fFactory.tagStart(buf, "mrow");
			fFactory.convertSymbol(buf, symbol);
			fFactory.tagEnd(buf, "mrow");
			// empty <mi> </mi>
			fFactory.tagStart(buf, "mi");
			fFactory.tagEnd(buf, "mi");

			fFactory.tagEnd(buf, "munderover");
			if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
				return false;
			}
			fFactory.tagEnd(buf, "mrow");
			return true;
		}
		return false;
	}

}