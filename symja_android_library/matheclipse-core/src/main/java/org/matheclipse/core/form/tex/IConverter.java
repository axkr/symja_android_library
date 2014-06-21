package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;

/**
 * General conversion interface for TeX functions.
 */
public interface IConverter {
	/**
	 * Converts a given function into the corresponding TeX output
	 * 
	 * @param buffer
	 *            StringBuffer for TeX output
	 * @param function
	 *            the math function which should be converted to TeX
	 */
	public boolean convert(StringBuffer buffer, IAST function, int precedence);
}
