package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.interfaces.IAST;

public class Product extends Sum {

	public Product() {

	}

	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() >= 3) {
			// &Product; &#x220F;
			return iteratorStep("&#x220F;", buf, f, 2);
		}
		return false;
	}

}