package org.matheclipse.core.interfaces;

import java.util.function.Function;

import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldVector;
import org.matheclipse.parser.trie.Trie;

public interface ISparseArray extends IDataExpr<Trie<int[], IExpr>> {

  /**
   * Get the dimensions of the sparse array.
   *
   * @return
   */
  public int[] getDimension();

  /**
   * Flatten the sparse array into a sparse array of depth 1.
   *
   * @return
   */
  public ISparseArray flatten();

  /**
   * Get the default value of the sparse array. Typically <code>0</code>.
   *
   * @return
   */
  public IExpr getDefaultValue();

  public IExpr get(int position);

  /**
   * Low level access. It is assumed that <code>positions</code> is a full index within the dimensions of
   * this sparse array.
   *
   * @param positions
   * @return
   */
  public IExpr getIndex(int... positions);

  /**
   * Get the <code>Part(-ISparseArray-,...)</code> of a sparse array, with index being an integer
   * number or symbol <code>All</code>. Low level access.  
   *
   * @param ast
   * @param startPosition
   * @return
   */
  public IExpr getPart(IAST ast, int startPosition) throws IndexOutOfBoundsException;

  /**
   * Returns the element at the specified positions in the nested ASTs.
   *
   * @param positions index of the element to return
   * @return the element at the specified positions in this nested AST or {@link F#NIL}
   * @throws IndexOutOfBoundsException if one of the positions are out of range
   */
  public IExpr getPart(final int... positions) throws IndexOutOfBoundsException;

  /**
   * Convert this sparse array to Symja rules.
   *
   * @return
   */
  public IAST arrayRules();

  /** Returns <code>true</code> for a sparse array object */
  @Override
  public boolean isSparseArray();

  /**
   * <code>Join(this, that)</code>. This method assumes, that both sparse arrays have depth==2 (i.e.
   * this is a matrix) and the column dimensions are equal. The new row dimension is <code>
   * this.dimension[0] + that.dimension[0]</code>.
   *
   * @param that
   * @return
   */
  public ISparseArray join(ISparseArray that);

  // public ISparseArray map(final BiFunction<IExpr, IExpr, IExpr> function, ISparseArray s2);

  /**
   * Maps the values of this sparse array with the unary <code>functor</code>. If the <code>functor
   * </code> returns <code>F.NIL</code> the original element of this AST list is used. <br>
   * <br>
   * Example for mapping with <code>Functors#replace1st()</code>, where the first argument will be
   * replaced by the current argument of this AST:
   *
   * <pre>
   * plusAST.map(Functors.replace1st(F.D(null, dAST.arg2())));
   * </pre>
   *
   * @param function a unary function
   * @return
   */
  public ISparseArray map(final Function<IExpr, IExpr> function);

  public ISparseArray mapThread(final IAST replacement, int position);

  /**
   * Convert this sparse array to a FieldMatrix. If conversion is not possible, return <code>null
   * </code>.
   *
   * @param arrayCopy whether to copy or reference the input array.
   * @return the corresponding FieldMatrix if possible, otherwise return <code>null</code>.
   */
  public FieldMatrix<IExpr> toFieldMatrix(boolean arrayCopy);

  @Override
  public IASTMutable normal(boolean nilIfUnevaluated);

  public IASTMutable normal(int[] dims);

  /**
   * Convert this sparse array to a FieldMatrix. If conversion is not possible, return <code>null
   * </code>.
   *
   * @param arrayCopy whether to copy or reference the input array.
   * @return the corresponding FieldMatrix if possible, otherwise return <code>null</code>.
   */
  public FieldVector<IExpr> toFieldVector(boolean arrayCopy);

  /**
   * Create the total of all elements. Optimized for <code>head==S.Plus</code> and default value
   * <code>0</code>.
   *
   * @param head the head (symbol) of the result.
   * @return
   */
  public IExpr total(IExpr head);
}
