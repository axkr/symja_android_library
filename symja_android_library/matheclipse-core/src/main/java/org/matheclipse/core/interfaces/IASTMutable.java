package org.matheclipse.core.interfaces;

/**
 * 
 * <p>
 * (I)nterface for the (A)bstract (S)yntax (T)ree of a given function.
 * </p>
 * <p>
 * An AST object where {@code IExpr} element values could be replaced by new values. This operation does not change the
 * size of the {@code IAST}.
 * </p>
 * <p>
 * In Symja, an abstract syntax tree (AST), is a tree representation of the abstract syntactic structure of the Symja
 * source code. Each node of the tree denotes a construct occurring in the source code. The syntax is 'abstract' in the
 * sense that it does not represent every detail that appears in the real syntax. For instance, grouping parentheses are
 * implicit in the tree structure, and a syntactic construct such as a <code>Sin(x)</code> expression will be denoted by
 * an AST with 2 nodes. One node for the header <code>Sin</code> and one node for the argument <code>x</code>.
 * </p>
 * 
 * Internally an AST is represented as a <code>java.util.List</code> which contains
 * <ul>
 * <li>the operator of a function (i.e. the &quot;header&quot;-symbol: Sin, Cos, Inverse, Plus, Times,...) at index
 * <code>0</code> and</li>
 * <li>the <code>n</code> arguments of a function in the index <code>1 to n</code></li>
 * </ul>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract syntax tree</a>,
 * <a href="https://en.wikipedia.org/wiki/Directed_acyclic_graph">Directed acyclic graph</a>
 */
public interface IASTMutable extends IAST {

	/**
	 * Replaces the element at the specified location in this {@code IAST} with the specified object. This operation
	 * does not change the size of the {@code IAST}.
	 * 
	 * @param i
	 *            the index at which to put the specified object.
	 * @param object
	 *            the object to insert.
	 * @return the previous element at the index.
	 * @throws UnsupportedOperationException
	 *             if replacing elements in this {@code IAST} is not supported.
	 * @throws ClassCastException
	 *             if the class of an object is inappropriate for this {@code IAST}.
	 * @throws IllegalArgumentException
	 *             if an object cannot be added to this {@code IAST}.
	 * @throws IndexOutOfBoundsException
	 *             if {@code location < 0 || >= size()}
	 */
	public IExpr set(int i, IExpr object);

}
