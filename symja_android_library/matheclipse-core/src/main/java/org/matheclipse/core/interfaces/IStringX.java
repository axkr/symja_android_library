package org.matheclipse.core.interfaces;

/** (I)nterface for a (String) e(X)pression */
public interface IStringX extends IExpr {
  public static final short TEXT_PLAIN = 1;
  public static final short TEXT_HTML = 2;
  public static final short TEXT_MATHML = 3;
  public static final short TEXT_LATEX = 4;

  public static final short APPLICATION_SYMJA = 5;
  public static final short APPLICATION_JAVA = 6;
  public static final short APPLICATION_JAVASCRIPT = 7;
  // public static final String TEXT_MARKDOWN = "text/markdown";

  /**
   * Test if this string equals the given character sequence.
   *
   * @param cs
   * @return
   */
  public boolean contentEquals(final CharSequence cs);

  /**
   * Get the mime tpe of this string.
   *
   * @return
   * @see IStringX#TEXT_PLAIN
   * @see IStringX#TEXT_LATEX
   * @see IStringX#TEXT_MATHML
   * @see IStringX#TEXT_HTML
   */
  public short getMimeType();

  /**
   * @param ch
   * @return
   * @see String#indexOf(ch)
   */
  public int indexOf(final int ch);

  /**
   * @param ch
   * @param fromIndex
   * @return
   * @see String#indexOf(int, int)
   */
  public int indexOf(final int ch, final int fromIndex);

  /**
   * @param str
   * @return
   * @see String#indexOf(String)
   */
  public int indexOf(final String str);

  /**
   * @param str
   * @param fromIndex
   * @return
   * @see String#indexOf(String, int)
   */
  public int indexOf(final String str, final int fromIndex);

  /**
   * @param ch
   * @return
   * @see String#lastIndexOf(int)
   */
  public int lastIndexOf(final int ch);

  /**
   * @param ch
   * @param fromIndex
   * @return
   * @see String#lastIndexOf(int, int)
   */
  public int lastIndexOf(final int ch, final int fromIndex);

  /**
   * @param str
   * @return
   * @see String#lastIndexOf(String)
   */
  public int lastIndexOf(final String str);

  /**
   * @param str
   * @param fromIndex
   * @return
   * @see String#lastIndexOf(String, int)
   */
  public int lastIndexOf(final String str, final int fromIndex);

  /**
   * @param beginIndex
   * @return
   * @see String#substring(int)
   */
  public String substring(final int beginIndex);

  /**
   * @param beginIndex
   * @param endIndex
   * @return
   * @see String#substring(int, int)
   */
  public String substring(final int beginIndex, final int endIndex);
  
  public String toLowerCase();
  
  public String toUpperCase();
  
}
