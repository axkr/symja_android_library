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
	 *            StringBuilder for TeX output
	 * @param function 
	 *            the math function which should be converted to TeX
	 */
	public boolean convert(StringBuilder buffer, IAST function, int precedence);
	
	public void setFactory(final AbstractTeXFormFactory factory);
}
