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
	 * Get the default value of the sparse array. Typically <code>0</code>.
	 * 
	 * @return
	 */
	public IExpr getDefaultValue();

	/**
	 * Get the <code>Part(-ISparseArray-,...)</code> of a sparse array, with index being an integer number or symbol
	 * <code>All</code>.
	 * 
	 * @param ast
	 * @param startPosition
	 * @return
	 */
	public IExpr getPart(IAST ast, int startPosition);

	/**
	 * Convert this sparse array to Symja rules.
	 * 
	 * @return
	 */
	public IAST arrayRules();

	/**
	 * Returns <code>true</code> for a sparse array object
	 */
	public boolean isSparseArray();

	/**
	 * Maps the values of this sparse array with the unary <code>functor</code>. If the <code>functor</code> returns
	 * <code>F.NIL</code> the original element of this AST list is used.
	 * 
	 * <br />
	 * <br />
	 * Example for mapping with <code>Functors#replace1st()</code>, where the first argument will be replaced by the
	 * current argument of this AST:
	 * 
	 * <pre>
	 * plusAST.map(Functors.replace1st(F.D(null, dAST.arg2())));
	 * </pre>
	 * 
	 * @param functor
	 *            a unary function
	 * @return
	 */
	public ISparseArray map(final Function<IExpr, IExpr> function);

	public ISparseArray mapThread(final IAST replacement, int position);

	/**
	 * Convert this sparse array to a FieldMatrix. If conversion is not possible, return <code>null</code>.
	 * 
	 * @param copyArray
	 *            Whether to copy or reference the input array.
	 * @return the corresponding FieldMatrix if possible, otherwise return <code>null</code>.
	 */
	public FieldMatrix<IExpr> toFieldMatrix(boolean arrayCopy);

	/**
	 * Convert this sparse array to a FieldMatrix. If conversion is not possible, return <code>null</code>.
	 * 
	 * @param copyArray
	 *            Whether to copy or reference the input array.
	 * @return the corresponding FieldMatrix if possible, otherwise return <code>null</code>.
	 */
	public FieldVector<IExpr> toFieldVector(boolean arrayCopy);
}
