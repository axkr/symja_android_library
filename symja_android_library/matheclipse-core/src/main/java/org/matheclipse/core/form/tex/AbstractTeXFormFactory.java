package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Abstract Factory for generating TeX output
 * 
 */
abstract public class AbstractTeXFormFactory {

	public final static boolean USE_IDENTIFIERS = false;

	/**
	 * 
	 */
	public AbstractTeXFormFactory() {
	}

	/**
	 * Returns the reflection package name which is used in
	 * #getReflectionNamespace()
	 * 
	 * @return
	 */
//	abstract public String getReflectionNamespace();

	/**
	 * Determine the converter of the heads symbol string
	 * 
	 * @param headString
	 * @return
	 */
	// abstract public IConverter reflection(String headString);

	/**
	 * convert a double nummber
	 * 
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertDouble(StringBuilder buf, INum d, int precedence);

	/**
	 * convert a complex number with double real and imaginary part
	 * 
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertDoubleComplex(StringBuilder buf, IComplexNum dc, int precedence);

	/**
	 * convert an IInteger number
	 * 
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertInteger(StringBuilder buf, IInteger i, int precedence);

	/**
	 * convert a fraction nummber
	 * 
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertFraction(StringBuilder buf, IFraction f, int precedence);

	/**
	 * convert a complex nummber
	 * 
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertComplex(StringBuilder buf, IComplex c, int precedence);

	/**
	 * convert a string
	 * 
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertString(StringBuilder buf, String str);

	/**
	 * convert a symbol (i.e. functionname, constantname or variablename)
	 * 
	 * @param buf 
	 * @param d
	 * @param precedence
	 */
	abstract public void convertSymbol(StringBuilder buf, ISymbol sym);

	/**
	 * convert the head of a function
	 * 
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertHead(StringBuilder buf, Object obj);

	/**
	 * general entry point for converting an object
	 * 
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convert(StringBuilder buf, Object o, int precedence);

	abstract public void convertAST(StringBuilder buf, final IAST f);
	
	abstract public void convertAST(StringBuilder buf, final IAST f, final String headString);
	
	abstract public void convertSubExpr(StringBuilder buf, IExpr o, int precedence);

}
