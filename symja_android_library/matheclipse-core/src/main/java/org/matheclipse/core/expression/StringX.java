package org.matheclipse.core.expression;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
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
public class StringX implements IStringX {
  private static final Logger LOGGER = LogManager.getLogger();

  /** */
  private static final long serialVersionUID = -68464824682534930L;

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

  public static StringX newInstance(final byte[] bytes, Charset charset) {
    StringX d = new StringX(null);
    d.fString = new String(bytes, charset);
    return d;
  }

  /**
   * Mime type <code>IStringX#TEXT_PLAIN</code> will be used.
   *
   * @param value the internal Java string value
   * @return
   */
  protected static StringX newInstance(final String value) {
    return new StringX(value);
  }

  /**
   * @param value the internal Java string value
   * @param mimeType the mime type of the string
   * @return
   * @see IStringX#TEXT_PLAIN
   * @see IStringX#TEXT_LATEX
   * @see IStringX#TEXT_MATHML
   * @see IStringX#TEXT_HTML
   */
  protected static StringX newInstance(final String value, short mimeType) {
    return new StringX(value, mimeType);
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

  public static StringX valueOf(final Object obj, final short mimeType) {
    return newInstance(String.valueOf(obj), mimeType);
  }

  public static StringX valueOf(final StringBuilder builder) {
    return newInstance(builder.toString());
  }

  private short fMimeType;

  private String fString;

  /** constructor for serialization */
  private StringX() {}

  private StringX(final String str) {
    fMimeType = TEXT_PLAIN;
    fString = str;
  }

  private StringX(final String str, short mimeType) {
    fMimeType = mimeType;
    fString = str;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
    return visitor.visit(this);
  }

  // public int compareTo(Object o) {
  // return fString.compareTo(((StringImpl) o).fString);
  // }

  /** {@inheritDoc} */
  @Override
  public boolean accept(IVisitorBoolean visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public int accept(IVisitorInt visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public long accept(IVisitorLong visitor) {
    return visitor.visit(this);
  }

  /**
   * @param index
   * @return
   */
  public char charAt(final int index) {
    return fString.charAt(index);
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof StringX) {
      return IStringX.US_COLLATOR.compare(fString, ((StringX) expr).fString);
    }
    return IStringX.super.compareTo(expr);
  }

  /**
   * @param anotherString
   * @return
   */
  public int compareTo(final StringX anotherString) {
    // sort lexicographically
    return IStringX.US_COLLATOR.compare(fString, anotherString.fString);
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
      LOGGER.error("StringX.copy() failed", e);
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

  /** Equals doesn't compare the mime type. */
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
   * <code>equalsIgnoreCase</code> doesn't compare the mime type.
   *
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

  /** @return */
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
  public short getMimeType() {
    return fMimeType;
  }

  @Override
  public int hashCode() {
    return (fString == null) ? 37 : 37 + fString.hashCode();
  }

  @Override
  public ISymbol head() {
    return S.String;
  }

  @Override
  public int hierarchy() {
    return STRINGID;
  }

  /**
   * @param ch
   * @return
   */
  @Override
  public int indexOf(final int ch) {
    return fString.indexOf(ch);
  }

  /**
   * @param ch
   * @param fromIndex
   * @return
   */
  @Override
  public int indexOf(final int ch, final int fromIndex) {
    return fString.indexOf(ch, fromIndex);
  }

  /**
   * @param str
   * @return
   */
  @Override
  public int indexOf(final String str) {
    return fString.indexOf(str);
  }

  /**
   * @param str
   * @param fromIndex
   * @return
   */
  @Override
  public int indexOf(final String str, final int fromIndex) {
    return fString.indexOf(str, fromIndex);
  }

  /** @return */
  public String intern() {
    return fString.intern();
  }

  @Override
  public CharSequence internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    if (symbolsAsFactoryMethod) {
      return new StringBuilder("$str(\"").append(fString).append("\")");
    }
    return new StringBuilder(fString.length() + 2).append("\"").append(fString).append("\"");
  }

  /** {@inheritDoc} */
  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    String prefix = AbstractAST.getPrefixF(properties);
    return new StringBuilder(prefix).append("$str(\"").append(fString).append("\")");
  }

  /**
   * @param ch
   * @return
   */
  @Override
  public int lastIndexOf(final int ch) {
    return fString.lastIndexOf(ch);
  }

  /**
   * @param ch
   * @param fromIndex
   * @return
   */
  @Override
  public int lastIndexOf(final int ch, final int fromIndex) {
    return fString.lastIndexOf(ch, fromIndex);
  }

  /**
   * @param str
   * @return
   */
  @Override
  public int lastIndexOf(final String str) {
    return fString.lastIndexOf(str);
  }

  /**
   * @param str
   * @param fromIndex
   * @return
   */
  @Override
  public int lastIndexOf(final String str, final int fromIndex) {
    return fString.lastIndexOf(str, fromIndex);
  }

  public int length() {
    return fString.length();
  }

  public boolean matches(final String regex) {
    return fString.matches(regex);
  }

  public boolean regionMatches(final boolean ignoreCase, final int toffset, final String other,
      final int ooffset, final int len) {
    return fString.regionMatches(ignoreCase, toffset, other, ooffset, len);
  }

  public boolean regionMatches(final int toffset, final String other, final int ooffset,
      final int len) {
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

  @Override
  public String substring(final int beginIndex) {
    return fString.substring(beginIndex);
  }

  /**
   * @param beginIndex
   * @param endIndex
   * @return
   */
  @Override
  public String substring(final int beginIndex, final int endIndex) {
    return fString.substring(beginIndex, endIndex);
  }

  /** @return */
  public char[] toCharArray() {
    return fString.toCharArray();
  }

  /** @return */
  @Override
  public String toLowerCase() {
    return fString.toLowerCase(Locale.US);
  }

  /**
   * @param locale
   * @return
   */
  public String toLowerCase(final Locale locale) {
    return fString.toLowerCase(locale);
  }

  @Override
  public String toString() {
    return fString;
  }

  /** @return */
  @Override
  public String toUpperCase() {
    return fString.toUpperCase(Locale.US);
  }

  /**
   * @param locale
   * @return
   */
  public String toUpperCase(final Locale locale) {
    return fString.toUpperCase(locale);
  }

  /** @return */
  public String trim() {
    return fString.trim();
  }
}
