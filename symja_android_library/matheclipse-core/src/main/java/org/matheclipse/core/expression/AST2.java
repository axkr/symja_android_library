package org.matheclipse.core.expression;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <p>
 * Immutable (A)bstract (S)yntax (T)ree of a given function with <b>exactly 2
 * arguments</b>.
 * </p>
 * 
 * <p>
 * In Symja, an abstract syntax tree (AST), is a tree representation of the
 * abstract syntactic structure of the Symja source code. Each node of the tree
 * denotes a construct occurring in the source code. The syntax is 'abstract' in
 * the sense that it does not represent every detail that appears in the real
 * syntax. For instance, grouping parentheses are implicit in the tree
 * structure, and a syntactic construct such as a <code>Sin[x]</code> expression
 * will be denoted by an AST with 2 nodes. One node for the header
 * <code>Sin</code> and one node for the argument <code>x</code>.
 * </p>
 * 
 * Internally an AST is represented as a <code>java.util.List</code> which
 * contains
 * <ul>
 * <li>the operator of a function (i.e. the &quot;header&quot;-symbol: Sin, Cos,
 * Inverse, Plus, Times,...) at index <code>0</code> and</li>
 * <li>the <code>n</code> arguments of a function in the index
 * <code>1 to n</code></li>
 * </ul>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract
 * syntax tree</a>.
 * 
 * @see AST
 */
public class AST2 extends AST1 {
	private final static int SIZE = 3;

	/**
	 * The second argument of this function.
	 */
	protected IExpr arg2;

	/**
	 * ctor for deserialization
	 */
	public AST2() {
		super();
	}

	/**
	 * Create a function with two arguments (i.e. <code>head[arg1, arg2]</code>
	 * ).
	 * 
	 * @param head
	 *            the head of the function
	 * @param arg1
	 *            the first argument of the function
	 * @param arg2
	 *            the second argument of the function
	 */
	public AST2(IExpr head, IExpr arg1, IExpr arg2) {
		super(head, arg1);
		this.arg2 = arg2;
	}

	/**
	 * Get the second argument (i.e. the third element of the underlying list
	 * structure) of the <code>AST</code> function (i.e. get(2) ). <br />
	 * <b>Example:</b> for the AST representing the expression <code>x^y</code>
	 * (i.e. <code>Power(x, y)</code>), <code>arg2()</code> returns
	 * <code>y</code>.
	 * 
	 * @return the second argument of the function represented by this
	 *         <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	final public IExpr arg2() {
		return arg2;
	}

	@Override
	public Set<IExpr> asSet() {
		Set<IExpr> set = new HashSet<IExpr>();
		set.add(arg1);
		set.add(arg2);
		return set;
	}

	/**
	 * Returns a new {@code HMArrayList} with the same elements, the same size
	 * and the same capacity as this {@code HMArrayList}.
	 * 
	 * @return a shallow copy of this {@code ArrayList}
	 * @see java.lang.Cloneable
	 */
	@Override
	public IASTMutable clone() {
		return new AST(arg0, arg1, arg2);
	}

	/** {@inheritDoc} */
	@Override
	public IASTMutable copy() {
		return new AST2(arg0, arg1, arg2);
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object object) {
		return arg0.equals(object) || arg1.equals(object) || arg2.equals(object);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof AbstractAST) {
			if (hashCode() != obj.hashCode()) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			IAST list = (IAST) obj;
			if (list.size() != SIZE) {
				return false;
			}
			return arg0.equals(list.head()) && arg1.equals(list.arg1()) && arg2.equals(list.arg2());
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IAST filterAST, IAST restAST, Predicate<? super IExpr> predicate) {
		if (predicate.test(arg1)) {
			filterAST.append(arg1);
		} else {
			restAST.append(arg1);
		}
		if (predicate.test(arg2)) {
			filterAST.append(arg2);
		} else {
			restAST.append(arg2);
		}
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IAST filterAST, Predicate<? super IExpr> predicate) {
		if (predicate.test(arg1)) {
			filterAST.append(arg1);
		}
		if (predicate.test(arg2)) {
			filterAST.append(arg2);
		}
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public void forEach(Consumer<? super IExpr> action) {
		action.accept(arg1);
		action.accept(arg2);
	}

	@Override
	public IExpr get(int location) {
		switch (location) {
		case 0:
			return arg0;
		case 1:
			return arg1;
		case 2:
			return arg2;
		default:
			throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 3");
		}
	}

	@Override
	public int hashCode() {
		if (hashValue == 0) {
			hashValue = 0x811c9dc5;// decimal 2166136261;
			hashValue = (hashValue * 16777619) ^ (arg0.hashCode() & 0xff);
			hashValue = (hashValue * 16777619) ^ (arg1.hashCode() & 0xff);
			hashValue = (hashValue * 16777619) ^ (arg2.hashCode() & 0xff);
		}
		return hashValue;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST0() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST1() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST2() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST3() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(IExpr head, int length) {
		return length == SIZE && arg0.equals(head);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(IExpr head, int minLength, int maxLength) {
		return arg0.equals(head) && minLength <= SIZE && maxLength >= SIZE;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHeadSizeGE(IExpr head, int length) {
		return arg0.equals(head) && length <= SIZE;
	}

	/**
	 * Replaces the element at the specified location in this {@code ArrayList}
	 * with the specified object.
	 * 
	 * @param location
	 *            the index at which to put the specified object.
	 * @param object
	 *            the object to add.
	 * @return the previous element at the index.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || >= size()}
	 */
	@Override
	public IExpr set(int location, IExpr object) {
		hashValue = 0;
		IExpr result;
		switch (location) {
		case 0:
			result = arg0;
			arg0 = object;
			return result;
		case 1:
			result = arg1;
			arg1 = object;
			return result;
		case 2:
			result = arg2;
			arg2 = object;
			return result;
		default:
			throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 3");
		}
	}

	/**
	 * Returns the number of elements in this {@code ArrayList}.
	 * 
	 * @return the number of elements in this {@code ArrayList}.
	 */
	@Override
	public int size() {
		return SIZE;
	}

	/**
	 * Returns a new array containing all elements contained in this
	 * {@code ArrayList}.
	 * 
	 * @return an array of the elements from this {@code ArrayList}
	 */
	@Override
	public IExpr[] toArray() {
		IExpr[] result = new IExpr[SIZE];
		result[0] = arg0;
		result[1] = arg1;
		result[2] = arg2;
		return result;
	}

}
