package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.interfaces.IAST;

public class Product extends Sum {

	public Product() {

	}

	/** {@inheritDoc} */
	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() >= 3) {
			return iteratorStep(buf, "\\prod", f, 2);
		}
		return false;
	}

}