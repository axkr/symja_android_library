package org.matheclipse.core.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * 
 * <p>
 * Appendable (I)nterface for the (A)bstract (S)yntax (T)ree of a given function.
 * </p>
 * <p>
 * An AST object to which <tt>IExpr</tt> sequences and values can be appended or removed. These operations typically
 * change the size of the {@code IAST}.
 * </p>
 * <p>
 * In Symja, an abstract syntax tree (AST), is a tree representation of the abstract syntactic structure of the Symja
 * source code. Each node of the tree denotes a construct occurring in the source code. The syntax is 'abstract' in the
 * sense that it does not represent every detail that appears in the real syntax. For instance, grouping parentheses are
 * implicit in the tree structure, and a syntactic construct such as a <code>Sin(x)</code> expression will be denoted by
 * an AST with 2 nodes. One node for the header <code>Sin</code> and one node for the argument <code>x</code>.
 * </p>
 * 
 * Internally an AST is represented as a list which contains
 * <ul>
 * <li>the operator of a function (i.e. the &quot;header&quot;-symbol: Sin, Cos, Inverse, Plus, Times,...) at index
 * <code>0</code> and</li>
 * <li>the <code>n</code> arguments of a function in the index <code>1 to n</code></li>
 * </ul>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract syntax tree</a>,
 * <a href="https://en.wikipedia.org/wiki/Directed_acyclic_graph">Directed acyclic graph</a>
 */
public interface IASTAppendable extends IASTMutable {

	/**
	 * Adds the specified expression at the end of this {@code List}.
	 * 
	 * @param expr
	 *            the object to add.
	 * @return always true.
	 * @throws UnsupportedOperationException
	 *             if adding to this {@code List} is not supported.
	 * @throws ClassCastException
	 *             if the class of the object is inappropriate for this {@code List}.
	 * @throws IllegalArgumentException
	 *             if the object cannot be added to this {@code List}.
	 */
	public boolean append(IExpr expr);

	/**
	 * Inserts the specified object into this {@code List} at the specified location. The object is inserted before the
	 * current element at the specified location. If the location is equal to the size of this {@code List}, the object
	 * is added at the end. If the location is smaller than the size of this {@code List}, then all elements beyond the
	 * specified location are moved by one position towards the end of the {@code List}.
	 * 
	 * @param location
	 *            the index at which to insert.
	 * @param object
	 *            the object to add.
	 * @throws UnsupportedOperationException
	 *             if adding to this {@code List} is not supported.
	 * @throws ClassCastException
	 *             if the class of the object is inappropriate for this {@code List}.
	 * @throws IllegalArgumentException
	 *             if the object cannot be added to this {@code List}.
	 * @throws IndexOutOfBoundsException
	 *             if {@code location < 0 || location > size()}
	 */
	public void append(int location, IExpr object);

	/**
	 * Adds the objects in the specified collection to the end of this {@code List}. The objects are added in the order
	 * in which they are returned from the collection's iterator.
	 * 
	 * @param collection
	 *            the collection of objects.
	 * @return {@code true} if this {@code List} is modified, {@code false} otherwise (i.e. if the passed collection was
	 *         empty).
	 * @throws UnsupportedOperationException
	 *             if adding to this {@code List} is not supported.
	 * @throws ClassCastException
	 *             if the class of an object is inappropriate for this {@code List}.
	 * @throws IllegalArgumentException
	 *             if an object cannot be added to this {@code List}.
	 */
	public boolean appendAll(Collection<? extends IExpr> collection);

	/**
	 * Appends all elements from offset <code>startPosition</code> to <code>endPosition</code> in the specified AST to
	 * the end of this AST.
	 * 
	 * @param ast
	 *            AST containing elements to be added to this AST
	 * @param startPosition
	 *            the start position, inclusive.
	 * @param endPosition
	 *            the ending position, exclusive.
	 * @return <tt>true</tt> if this AST changed as a result of the call
	 * 
	 */
	public boolean appendAll(IAST ast, int startPosition, int endPosition);

	/**
	 * Inserts the objects in the specified collection at the specified location in this AST. The objects are added in
	 * the order they are returned from the collection's iterator.
	 * 
	 * @param location
	 *            the index at which to insert.
	 * @param collection
	 *            the collection of objects.
	 * @return {@code true} if this {@code ArrayList} is modified, {@code false} otherwise.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || > size()}
	 */
	public boolean appendAll(int location, Collection<? extends IExpr> collection);

	/**
	 * Appends all elements from offset <code>startPosition</code> to <code>endPosition</code> in the specified list to
	 * the end of this AST.
	 * 
	 * @param list
	 *            list containing elements to be added to this AST
	 * @param startPosition
	 *            the start position, inclusive.
	 * @param endPosition
	 *            the ending position, exclusive.
	 * @return <tt>true</tt> if this AST changed as a result of the call
	 * 
	 */
	public boolean appendAll(List<? extends IExpr> list, int startPosition, int endPosition);

	/**
	 * Appends all elements from offset <code>startPosition</code> to <code>endPosition</code> in the specified list to
	 * the end of this AST.
	 * 
	 * @param args
	 *            array containing elements to be added to this AST
	 * @param startPosition
	 *            the start position, inclusive.
	 * @param endPosition
	 *            the ending position, exclusive.
	 * @return <tt>true</tt> if this AST changed as a result of the call
	 * 
	 */
	public boolean appendAll(IExpr[] args, int startPosition, int endPosition);

	/**
	 * Appends all of the arguments (starting from offset <code>1</code>) in the specified AST to the end of this AST.
	 * 
	 * @param ast
	 *            AST containing elements to be added to this AST
	 * @return <tt>true</tt> if this AST changed as a result of the call
	 * 
	 */
	public boolean appendArgs(IAST ast);

	/**
	 * Appends all of the arguments (starting from offset <code>1</code>) in the specified AST up to position
	 * <code>untilPosition</code> exclusive.
	 * 
	 * @param ast
	 *            AST containing elements to be added to this AST
	 * @param untilPosition
	 *            append all argumments of ast up to position <code>untilPosition</code> exclusive.
	 * 
	 * @return <tt>true</tt> if this AST changed as a result of the call
	 * 
	 */
	public boolean appendArgs(IAST ast, int untilPosition);

	/**
	 * Appends all elements generated by the given function from index <code>start</code> inclusive to <code>end</code>
	 * exclusive.
	 * 
	 * @param start
	 *            start index (inclusive)
	 * @param end
	 *            end index (exclusive)
	 * @param function
	 *            function which generates the elements which should be appended
	 * @return <tt>this</tt>
	 * 
	 */
	public IASTAppendable appendArgs(int start, int end, IntFunction<IExpr> function);

	/**
	 * Appends all elements generated by the given function from index <code>1</code> inclusive to <code>end</code>
	 * exclusive.
	 * 
	 * @param end
	 *            end index (exclusive)
	 * @param function
	 *            function which generates the elements which should be appended
	 * @return <tt>this</tt>
	 * 
	 */
	public IASTAppendable appendArgs(int end, IntFunction<IExpr> function);

	/**
	 * Append an <code>subAST</code> with attribute <code>OneIdentity</code> for example Plus[] or Times[].
	 * 
	 * @param subAST
	 *            an ast with attribute <code>OneIdentity</code>.
	 * @return <code>this</code> ast after adding the subAST
	 */
	public IAST appendOneIdentity(IAST subAST);

	/**
	 * Removes all elements from this {@code IAST}, leaving it empty (optional).
	 * 
	 */
	public void clear();

	/**
	 * If this expression unequals <code>F.NIL</code>, invoke the specified consumer with this
	 * <code>IASTAppendable</code> object, otherwise do nothing.
	 *
	 * @param consumer
	 *            block to be executed if this expression unequals <code>F.NIL</code>
	 * @see java.util.Optional#ifPresent(Consumer)
	 */
	default void ifAppendable(Consumer<? super IASTAppendable> consumer) {
		consumer.accept(this);
	}

	/**
	 * Removes the object at the specified location from this {@code IAST}.
	 * 
	 * @param location
	 *            the index of the object to remove.
	 * @return the removed object.
	 * @throws UnsupportedOperationException
	 *             if removing from this {@code IAST} is not supported.
	 * @throws IndexOutOfBoundsException
	 *             if {@code location < 0 || >= size()}
	 */
	public IExpr remove(int location);

	/**
	 * Removes the objects in the specified range from the start to the end, but not including the end index.
	 * 
	 * @param start
	 *            the index at which to start removing.
	 * @param end
	 *            the index one after the end of the range to remove. * @throws UnsupportedOperationException if
	 *            removing from this {@code IAST} is not supported.
	 * @throws IndexOutOfBoundsException
	 *             when {@code start < 0, start > end} or {@code end > size()}
	 */
	public void removeRange(int start, int end);
}
