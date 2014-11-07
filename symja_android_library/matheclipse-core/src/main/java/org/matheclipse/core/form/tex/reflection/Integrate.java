package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISymbol;

public class Integrate extends AbstractConverter {

	public Integrate() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() >= 3) {
			return iteratorStep(buf, "\\int", f, 2);
		}
		return false;
	}

	public boolean iteratorStep(final StringBuffer buf, final String mathSymbol, final IAST f, int i) {
		if (i >= f.size()) {
			buf.append(" ");
			fFactory.convert(buf, f.arg1(), 0);
			return true;
		}
		if (f.get(i).isList()) {
			IAST list = (IAST) f.get(i);
			if (list.size()==4 && list.arg1().isSymbol()) {
				ISymbol symbol = (ISymbol) list.arg1();
				buf.append(mathSymbol);
				buf.append("_{");
				fFactory.convert(buf, list.arg2(), 0);
				buf.append("}^{");
				fFactory.convert(buf, list.arg3(), 0);
				buf.append('}');
				if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
					return false;
				}
				buf.append("\\,\\mathrm{d}");
				fFactory.convertSymbol(buf, symbol);
				return true;
			}
		} else if (f.get(i).isSymbol()) {
			ISymbol symbol = (ISymbol) f.get(i);
			buf.append(mathSymbol);
			buf.append(" ");
			if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
				return false;
			}
			buf.append("\\,\\mathrm{d}");
			fFactory.convertSymbol(buf, symbol);
			return true;
		}
		return false;
	}
}