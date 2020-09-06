package org.matheclipse.core.interfaces;

import org.matheclipse.parser.trie.Trie;

public interface ISparseArray extends IDataExpr<Trie<int[], IExpr>> {
	public int[] getDimension();

	public IExpr getPart(IAST ast, int startPosition);
	
	/**
	 * Convert this sparse array to Symja rules.
	 * @return 
	 */
	public IAST arrayRules();
	
	/**
	 * Returns <code>true</code> for a sparse array object
	 */
	public boolean isSparseArray();
}
