package org.matheclipse.core.interfaces;

import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldVector;
import org.matheclipse.parser.trie.Trie;

public interface ISparseArray extends IDataExpr<Trie<int[], IExpr>> {
	public int[] getDimension();

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
	 * Convert this sparse array to a FieldMatrix. If conversion is not possible, return <code>null</code>.
	 * 
	 * @return the corresponding FieldMatrix if possible, otherwise return <code>null</code>.
	 */
	public FieldMatrix<IExpr> toFieldMatrix();
	
	/**
	 * Convert this sparse array to a FieldMatrix. If conversion is not possible, return <code>null</code>.
	 * 
	 * @return the corresponding FieldMatrix if possible, otherwise return <code>null</code>.
	 */
	public FieldVector<IExpr> toFieldVector();
}
