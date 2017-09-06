package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.interfaces.IAST;

public class Product extends Sum {

	public Product() {

	}

	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() >= 3) {
			// &Product; &#x220F;
			return iteratorStep(buf, "&#x220F;", f, 2);
		}
		return false;
	}

}