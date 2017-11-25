package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISymbol;

public class Integrate extends AbstractConverter {

	public Integrate() {
	}

	/** {@inheritDoc} */
	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() >= 3) {
			// &sum; &#x2211
			return iteratorStep(buf, "&#x222B;", f, 2);
		}
		return false;
	}

	public boolean iteratorStep(final StringBuilder buf, final String mathSymbol, final IAST f, int i) {
		if (i >= f.size()) {
			fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
			return true;
		}
		if (f.get(i).isList()) {
			IAST list = (IAST) f.get(i);
			if (list.isAST3() && list.arg1().isSymbol()) {
				ISymbol symbol = (ISymbol) list.arg1();
				fFactory.tagStart(buf, "msubsup");
				// &Integral; &#x222B;
				fFactory.tag(buf, "mo", mathSymbol);
				fFactory.convert(buf, list.arg2(), Integer.MIN_VALUE, false);
				fFactory.convert(buf, list.arg3(), Integer.MIN_VALUE, false);
				fFactory.tagEnd(buf, "msubsup");
				if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
					return false;
				}
				fFactory.tagStart(buf, "mrow");
				// &dd; &#x2146;
				fFactory.tag(buf, "mo", "&#x2146;");
				fFactory.convertSymbol(buf, symbol);
				fFactory.tagEnd(buf, "mrow");
				return true;
			}
		} else if (f.get(i).isSymbol()) {
			ISymbol symbol = (ISymbol) f.get(i);
			// &Integral; &#x222B;
			fFactory.tag(buf, "mo", mathSymbol);
			if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
				return false;
			}
			fFactory.tagStart(buf, "mrow");
			// &dd; &#x2146;
			fFactory.tag(buf, "mo", "&#x2146;");
			fFactory.convertSymbol(buf, symbol);
			fFactory.tagEnd(buf, "mrow");
			return true;
		}
		return false;
	}
}