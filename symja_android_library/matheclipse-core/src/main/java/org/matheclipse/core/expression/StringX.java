package org.matheclipse.core.expression;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.IStringXImpl;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * A concrete IString implementation
 * 
 * @see org.matheclipse.core.interfaces.IStringX
 */
public class StringX extends IStringXImpl implements IStringX {
	/**
	 * 
	 */
	private static final long serialVersionUID = -68464824682534930L;

	// protected static final XmlFormat<StringImpl> SYMBOL_XML = new
	// XmlFormat<StringImpl>(StringImpl.class) {
	// @Override
	// public void format(StringImpl obj, XmlElement xml) {
	// StringImpl expr = obj;
	// xml.setAttribute("name", expr.fString);
	// }
	//
	// @Override
	// public StringImpl parse(XmlElement xml) {
	// StringImpl expr = (StringImpl) xml.object();
	// expr.fString = xml.getAttribute("name", "");
	// return expr;
	// }
	// };

	// private static final ObjectFactory<StringX> FACTORY = new
	// ObjectFactory<StringX>() {
	// @Override
	// protected StringX create() {
	// if (Config.SERVER_MODE && currentQueue().getSize() >=
	// Config.STRING_MAX_POOL_SIZE) {
	// throw new PoolMemoryExceededException("StringX",
	// currentQueue().getSize());
	// }
	// return new StringX(null);
	// }
	// };

	/**
	 * Be cautious with this method, no new internal String object is created
	 * 
	 * @param value
	 * @return
	 */
	protected static StringX newInstance(final String value) {
		// IntegerImpl z = new IntegerImpl();
		StringX d;
		// if (Config.SERVER_MODE) {
		// if (Config.STRING_MAX_SIZE < value.length()) {
		// throw new ObjectMemoryExceededException("StringX", value.length());
		// }
		// d = FACTORY.object();
		// } else {
		d = new StringX(null);
		// }
		d.fString = value;
		return d;
	}

	public static StringX newInstance(final byte[] bytes, Charset charset) {
		StringX d = new StringX(null);
		d.fString = new String(bytes, charset);
		return d;
	}

	private String fString;

	/**
	 * @param data
	 * @return
	 */
	public static StringX copyValueOf(final char[] data) {
		return newInstance(String.copyValueOf(data));
	}

	/**
	 * @param data
	 * @param offset
	 * @param count
	 * @return
	 */
	public static StringX copyValueOf(final char[] data, final int offset, final int count) {
		return newInstance(String.copyValueOf(data, offset, count));
	}

	/**
	 * @param b
	 * @return
	 */
	public static StringX valueOf(final boolean b) {
		return newInstance(String.valueOf(b));
	}

	/**
	 * @param c
	 * @return
	 */
	public static StringX valueOf(final char c) {
		return newInstance(String.valueOf(c));
	}

	/**
	 * @param data
	 * @return
	 */
	public static StringX valueOf(final char[] data) {
		return newInstance(String.valueOf(data));
	}

	/**
	 * @param data
	 * @param offset
	 * @param count
	 * @return
	 */
	public static StringX valueOf(final char[] data, final int offset, final int count) {
		return newInstance(String.valueOf(data, offset, count));
	}

	/**
	 * @param d
	 * @return
	 */
	public static StringX valueOf(final double d) {
		return newInstance(String.valueOf(d));
	}

	/**
	 * @param f
	 * @return
	 */
	public static StringX valueOf(final float f) {
		return newInstance(String.valueOf(f));
	}

	/**
	 * @param i
	 * @return
	 */
	public static StringX valueOf(final int i) {
		return newInstance(String.valueOf(i));
	}

	/**
	 * @param l
	 * @return
	 */
	public static StringX valueOf(final long l) {
		return newInstance(String.valueOf(l));
	}

	/**
	 * @param obj
	 * @return
	 */
	public static StringX valueOf(final Object obj) {
		return newInstance(String.valueOf(obj));
	}

	public static StringX valueOf(final StringBuilder builder) {
		return newInstance(builder.toString());
	}

	private StringX(final String str) {
		fString = str;
	}

	/**
	 * @param index
	 * @return
	 */
	public char charAt(final int index) {
		return fString.charAt(index);
	}

	// public int compareTo(Object o) {
	// return fString.compareTo(((StringImpl) o).fString);
	// }

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive
	 * integer as this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof StringX) {
			return fString.compareTo(((StringX) expr).fString);
		}
		return IStringX.super.compareTo(expr);
	}

	/**
	 * @param anotherString
	 * @return
	 */
	public int compareTo(final StringX anotherString) {
		return fString.compareTo(anotherString.fString);
	}

	/**
	 * @param str
	 * @return
	 */
	public int compareToIgnoreCase(final StringX str) {
		return fString.compareToIgnoreCase(str.fString);
	}

	/**
	 * @param str
	 * @return
	 */
	public String concat(final StringX str) {
		return fString.concat(str.fString);
	}

	/**
	 * @param cs
	 * @return
	 */
	@Override
	public boolean contentEquals(final CharSequence cs) {
		return fString.contentEquals(cs);
	}

	@Override
	public IExpr copy() {
		try {
			return (IExpr) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param suffix
	 * @return
	 */
	public boolean endsWith(final String suffix) {
		return fString.endsWith(suffix);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof StringX) {
			return fString.equals(((StringX) obj).fString);
		}
		return false;
	}

	/**
	 * @param anotherString
	 * @return
	 */
	public boolean equalsIgnoreCase(final String anotherString) {
		return fString.equalsIgnoreCase(anotherString);
	}

	/** {@inheritDoc} */
	@Override
	public String fullFormString() {
		return "\"" + fString + "\"";
	}

	/**
	 * @return
	 */
	public byte[] getBytes() {
		return fString.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * @param charsetName
	 * @return
	 * @throws java.io.UnsupportedEncodingException
	 */
	public byte[] getBytes(final String charsetName) throws UnsupportedEncodingException {
		return fString.getBytes(charsetName);
	}

	/**
	 * @param srcBegin
	 * @param srcEnd
	 * @param dst
	 * @param dstBegin
	 */
	public void getChars(final int srcBegin, final int srcEnd, final char[] dst, final int dstBegin) {
		fString.getChars(srcBegin, srcEnd, dst, dstBegin);
	}

	@Override
	public int hashCode() {
		return 37 * fString.hashCode();
	}

	@Override
	public int hierarchy() {
		return STRINGID;
	}

	/**
	 * @param ch
	 * @return
	 */
	public int indexOf(final int ch) {
		return fString.indexOf(ch);
	}

	/**
	 * @param ch
	 * @param fromIndex
	 * @return
	 */
	public int indexOf(final int ch, final int fromIndex) {
		return fString.indexOf(ch, fromIndex);
	}

	/**
	 * @param str
	 * @return
	 */
	public int indexOf(final String str) {
		return fString.indexOf(str);
	}

	/**
	 * @param str
	 * @param fromIndex
	 * @return
	 */
	public int indexOf(final String str, final int fromIndex) {
		return fString.indexOf(str, fromIndex);
	}

	/**
	 * @return
	 */
	public String intern() {
		return fString.intern();
	}

	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		if (symbolsAsFactoryMethod) {
			final StringBuilder buffer = new StringBuilder();
			buffer.append("$str(\"");
			buffer.append(fString);
			buffer.append("\")");
			return buffer.toString();
		}
		return "\"" + fString + "\"";
	}

	/** {@inheritDoc} */
	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators, boolean usePrefix) {
		final StringBuilder buffer = new StringBuilder();
		String prefix = usePrefix ? "F." : "";
		buffer.append(prefix+"$str(\"");
		buffer.append(fString);
		buffer.append("\")");
		return buffer.toString();
	}

	/**
	 * @param ch
	 * @return
	 */
	public int lastIndexOf(final int ch) {
		return fString.lastIndexOf(ch);
	}

	/**
	 * @param ch
	 * @param fromIndex
	 * @return
	 */
	public int lastIndexOf(final int ch, final int fromIndex) {
		return fString.lastIndexOf(ch, fromIndex);
	}

	/**
	 * @param str
	 * @return
	 */
	public int lastIndexOf(final String str) {
		return fString.lastIndexOf(str);
	}

	/**
	 * @param str
	 * @param fromIndex
	 * @return
	 */
	public int lastIndexOf(final String str, final int fromIndex) {
		return fString.lastIndexOf(str, fromIndex);
	}

	public int length() {
		return fString.length();
	}

	public boolean matches(final String regex) {
		return fString.matches(regex);
	}

	public boolean regionMatches(final boolean ignoreCase, final int toffset, final String other, final int ooffset,
			final int len) {
		return fString.regionMatches(ignoreCase, toffset, other, ooffset, len);
	}

	public boolean regionMatches(final int toffset, final String other, final int ooffset, final int len) {
		return fString.regionMatches(toffset, other, ooffset, len);
	}

	public String replace(final char oldChar, final char newChar) {
		return fString.replace(oldChar, newChar);
	}

	public String replaceFirst(final String regex, final String replacement) {
		return fString.replaceFirst(regex, replacement);
	}

	public String[] split(final String regex) {
		return fString.split(regex);
	}

	public String[] split(final String regex, final int limit) {
		return fString.split(regex, limit);
	}

	/**
	 * @param prefix
	 * @return
	 */
	public boolean startsWith(final String prefix) {
		return fString.startsWith(prefix);
	}

	/**
	 * @param prefix
	 * @param toffset
	 * @return
	 */
	public boolean startsWith(final String prefix, final int toffset) {
		return fString.startsWith(prefix, toffset);
	}

	/**
	 * @param start
	 * @param end
	 * @return
	 */
	public CharSequence subSequence(final int start, final int end) {
		return fString.subSequence(start, end);
	}

	/**
	 * @param beginIndex
	 * @return
	 */
	public String substring(final int beginIndex) {
		return fString.substring(beginIndex);
	}

	/**
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public String substring(final int beginIndex, final int endIndex) {
		return fString.substring(beginIndex, endIndex);
	}

	/**
	 * @return
	 */
	public char[] toCharArray() {
		return fString.toCharArray();
	}

	/**
	 * @return
	 */
	public String toLowerCase() {
		return fString.toLowerCase();
	}

	/**
	 * @param locale
	 * @return
	 */
	public String toLowerCase(final Locale locale) {
		return fString.toLowerCase(locale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	// public String toString() {
	// return fString.toString();
	// }
	/**
	 * @return
	 */
	public String toUpperCase() {
		return fString.toUpperCase();
	}

	/**
	 * @param locale
	 * @return
	 */
	public String toUpperCase(final Locale locale) {
		return fString.toUpperCase(locale);
	}

	/**
	 * @return
	 */
	public String trim() {
		return fString.trim();
	}

	// @Override
	// public boolean move(final ObjectSpace os) {
	// return super.move(os);
	// }
	// public StringX copy() {
	// StringX r = FACTORY.object();
	// r.fString = fString;
	// return r;
	// }
	//
	// public StringX copyNew() {
	// StringX r = new StringX(null);
	// r.fString = fString;
	// return r;
	// }
	//
	// public void recycle() {
	// FACTORY.recycle(this);
	// }

	// public Text toText() {
	// final TextBuilder tb = TextBuilder.newInstance();
	// tb.append(fString);
	// return tb.toText();
	// }
	@Override
	public ISymbol head() {
		return F.StringHead;
	}

	@Override
	public String toString() {
		return fString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return visitor.visit(this);
	}
}