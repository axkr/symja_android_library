package org.matheclipse.core.form.mathml;

import java.util.Hashtable;

import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Abstract Factory for generating MathML output
 *
 */
abstract public class AbstractMathMLFormFactory {

	public final static boolean USE_IDENTIFIERS = false;

	public final static Hashtable<String, String> ENTITY_TABLE = new Hashtable<String, String>(199);

	// public final ExprFactory fExprFactory;

	private final String fTagPrefix;

	/**
	 * Constructor for the EMML object
	 */
	public AbstractMathMLFormFactory() {
		this("");
	}

	public AbstractMathMLFormFactory(final String tagPrefix) {
		// fExprFactory = exprFactory;
		fTagPrefix = tagPrefix;
	}

	public void entity(final StringBuffer buf, final String tag) {
		if (USE_IDENTIFIERS) {
			buf.append(tag);
		} else {
			final Object entityValue = ENTITY_TABLE.get(tag);
			if (entityValue != null) {
				buf.append(entityValue.toString());
			} else {
				buf.append(tag);
			}
		}
	}

	public void tag(final StringBuffer buf, final String tag, final String data) {
		tagStart(buf, tag);
		entity(buf, data);
		tagEnd(buf, tag);
	}

	public void tagStart(final StringBuffer buf, final String tag) {
		buf.append("<" + fTagPrefix + tag + ">");
	}

	public void tagStart(final StringBuffer buf, final String tag, final String attr0) {
		buf.append("<" + fTagPrefix + tag + " " + attr0 + ">");
	}

	public void tagEnd(final StringBuffer buf, final String tag) {
		buf.append("</" + fTagPrefix + tag + ">");
	}

	public void tagStartEnd(final StringBuffer buf, final String tag) {
		buf.append("<" + fTagPrefix + tag + " />");
	}

	/**
	 * Returns the reflection package name which is used in #getReflectionNamespace()
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
//	abstract public IConverter reflection(String headString);

	/**
	 * convert a double nummber
	 *
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertDouble(StringBuffer buf, INum d, int precedence);

	/**
	 * convert a complex number with double real and imaginary part
	 *
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertDoubleComplex(StringBuffer buf, IComplexNum dc, int precedence);

	/**
	 * convert an IInteger number
	 *
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertInteger(StringBuffer buf, IInteger i, int precedence);

	/**
	 * convert a fraction nummber
	 *
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertFraction(StringBuffer buf, IRational f, int precedence);

	/**
	 * convert a complex nummber
	 *
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertComplex(StringBuffer buf, IComplex c, int precedence);

	/**
	 * convert a string
	 *
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertString(StringBuffer buf, String str);

	/**
	 * convert a symbol (i.e. functionname, constantname or variablename)
	 *
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertSymbol(StringBuffer buf, ISymbol sym);

	/**
	 * convert the heade of a function
	 *
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convertHead(StringBuffer buf, IExpr obj);

	/**
	 * general entry point for converting an object
	 *
	 * @param buf
	 * @param d
	 * @param precedence
	 */
	abstract public void convert(StringBuffer buf, IExpr o, int precedence);

}
