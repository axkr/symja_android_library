package org.matheclipse.core.interfaces;

public interface INumericArray extends IDataExpr<Object> {

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
