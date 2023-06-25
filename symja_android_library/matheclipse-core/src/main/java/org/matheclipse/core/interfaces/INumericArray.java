package org.matheclipse.core.interfaces;

public interface INumericArray extends IDataExpr<Object> {

  /** The UNDEFINED value type token. */
  public static final byte UNDEFINED = (byte) (0xFF);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Integer8 = (byte) (0x00);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Integer16 = (byte) (0x01);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Integer32 = (byte) (0x02);
  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Integer64 = (byte) (0x03);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte UnsignedInteger8 = (byte) (0x10);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte UnsignedInteger16 = (byte) (0x11);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte UnsignedInteger32 = (byte) (0x12);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte UnsignedInteger64 = (byte) (0x13);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Real32 = (byte) (0x22);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Real64 = (byte) (0x23);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte ComplexReal32 = (byte) (0x33);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte ComplexReal64 = (byte) (0x34);

  /**
   * Get the dimensions of the sparse array.
   *
   * @return
   */
  public int[] getDimension();

  /**
   * Get the name of the type of the numeric array.
   *
   * @return
   */
  public String getStringType();

  /**
   * Get the type of the numeric array.
   *
   * @return
   */
  public byte getType();

  /**
   * Get the value at the current position as an {@link IExpr} object
   *
   * @param position
   * @return
   */
  public IExpr get(int position);

  /** Returns <code>true</code> for a numeric array object */
  @Override
  public boolean isNumericArray();

  @Override
  public IASTMutable normal(boolean nilIfUnevaluated);

  public IASTMutable normal(int[] dims);
}
