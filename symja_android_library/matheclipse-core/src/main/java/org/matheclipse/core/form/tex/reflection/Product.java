package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Product extends AbstractConverter {

	public Product() {

	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() >= 3) {
			for (int i = 2; i < f.size(); i++) {
				if (f.get(i).isList()) {
					final IAST list = (IAST) f.get(i);
					buf.append("\\prod_{");

					if (list.size() > 1) {
						fFactory.convert(buf, list.arg1(), 0);
					}
					if (list.size() > 2) {
						buf.append(" = ");
						fFactory.convert(buf, list.arg2(), 0);
					}
					if (list.size() > 3) {
						buf.append("}^{");
						fFactory.convert(buf, list.arg3(), 0);
					}
					buf.append('}');
				} else {
					return false;
				}
			}
			fFactory.convert(buf, f.arg1(), 0);
			return true;
		}
		return false;
	}

}