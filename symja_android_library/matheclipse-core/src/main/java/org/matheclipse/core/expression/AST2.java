package org.matheclipse.core.expression;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

import org.matheclipse.core.generic.ObjIntPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * Immutable (A)bstract (S)yntax (T)ree of a given function with <b>exactly 2 arguments</b>.
 * </p>
 * 
 * <p>
 * In Symja, an abstract syntax tree (AST), is a tree representation of the abstract syntactic structure of the Symja
 * source code. Each node of the tree denotes a construct occurring in the source code. The syntax is 'abstract' in the
 * sense that it does not represent every detail that appears in the real syntax. For instance, grouping parentheses are
 * implicit in the tree structure, and a syntactic construct such as a <code>Sin[x]</code> expression will be denoted by
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
 * See <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract syntax tree</a>.
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
	 * Create a function with two arguments (i.e. <code>head[arg1, arg2]</code> ).
	 * 
	 * @param head
	 *            the head of the function
	 * @param arg1
	 *            the first argument of the function
	 * @param arg2
	 *            the second argument of the function
	 */
	/* package private */ AST2(IExpr head, IExpr arg1, IExpr arg2) {
		super(head, arg1);
		this.arg2 = arg2;
	}

	/**
	 * Get the second argument (i.e. the third element of the underlying list structure) of the <code>AST</code>
	 * function (i.e. get(2) ). <br />
	 * <b>Example:</b> for the AST representing the expression <code>x^y</code> (i.e. <code>Power(x, y)</code>),
	 * <code>arg2()</code> returns <code>y</code>.
	 * 
	 * @return the second argument of the function represented by this <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	final public IExpr arg2() {
		return arg2;
	}

	/** {@inheritDoc} */
	@Override
	public int argSize() {
		return SIZE - 1;
	}

	@Override
	public Set<IExpr> asSet() {
		Set<IExpr> set = new HashSet<IExpr>();
		set.add(arg1);
		set.add(arg2);
		return set;
	}

	/**
	 * Returns a new {@code AST2} with the same elements, the same size and the same capacity as this {@code AST2}.
	 * 
	 * @return a shallow copy of this {@code ArrayList}
	 * @see java.lang.Cloneable
	 */
	@Override
	public IAST clone() {
		AST2 result = (AST2) super.clone();
		result.arg0 = arg0;
		result.arg1 = arg1;
		result.arg2 = arg2;
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object object) {
		return arg0.equals(object) || arg1.equals(object) || arg2.equals(object);
	}

	/** {@inheritDoc} */
	@Override
	public IASTMutable copy() {
		return new AST2(arg0, arg1, arg2);
	}

	@Override
	public IASTAppendable copyAppendable() {
		return new AST(arg0, arg1, arg2);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AbstractAST) {
			final IAST list = (IAST) obj;
			if (arg0 != ((AbstractAST) list).head() && arg0 instanceof ISymbol) {
				// compared with ISymbol object identity
				return false;
			}
			if (list.size() != SIZE) {
				return false;
			}
			return arg1.equals(list.arg1()) && arg2.equals(list.arg2())
					&& (arg0 instanceof ISymbol || arg0.equals(list.head()));
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean exists(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
		switch (startOffset) {
		case 0:
			return predicate.test(arg0, 0) || predicate.test(arg1, 1) || predicate.test(arg2, 2);
		case 1:
			return predicate.test(arg1, 1) || predicate.test(arg2, 2);
		case 2:
			return predicate.test(arg2, 2);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean exists(Predicate<? super IExpr> predicate, int startOffset) {
		switch (startOffset) {
		case 0:
			return predicate.test(arg0) || predicate.test(arg1) || predicate.test(arg2);
		case 1:
			return predicate.test(arg1) || predicate.test(arg2);
		case 2:
			return predicate.test(arg2);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IASTAppendable filterAST, IASTAppendable restAST, Predicate<? super IExpr> predicate) {
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
	public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
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
	public IAST filterFunction(IASTAppendable filterAST, IASTAppendable restAST,
			final Function<IExpr, IExpr> function) {
		IExpr expr = function.apply(arg1);
		if (expr.isPresent()) {
			filterAST.append(expr);
		} else {
			restAST.append(arg1);
		}
		expr = function.apply(arg2);
		if (expr.isPresent()) {
			filterAST.append(expr);
		} else {
			restAST.append(arg2);
		}
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
		switch (startOffset) {
		case 0:
			return predicate.test(arg0, 0) && predicate.test(arg1, 1) && predicate.test(arg2, 2);
		case 1:
			return predicate.test(arg1, 1) && predicate.test(arg2, 2);
		case 2:
			return predicate.test(arg2, 2);
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean forAll(Predicate<? super IExpr> predicate, int startOffset) {
		switch (startOffset) {
		case 0:
			return predicate.test(arg0) && predicate.test(arg1) && predicate.test(arg2);
		case 1:
			return predicate.test(arg1) && predicate.test(arg2);
		case 2:
			return predicate.test(arg2);
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public void forEach(Consumer<? super IExpr> action) {
		action.accept(arg1);
		action.accept(arg2);
	}

	/** {@inheritDoc} */
	@Override
	public void forEach(Consumer<? super IExpr> action, int startOffset) {
		switch (startOffset) {
		case 0:
			action.accept(arg0);
			action.accept(arg1);
			action.accept(arg2);
			break;
		case 1:
			action.accept(arg1);
			action.accept(arg2);
			break;
		case 2:
			action.accept(arg2);
			break;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void forEach(int startOffset, int endOffset, Consumer<? super IExpr> action) {
		if (startOffset < endOffset) {
			switch (startOffset) {
			case 0:
				action.accept(arg0);
				if (startOffset + 1 < endOffset) {
					action.accept(arg1);
					if (startOffset + 2 < endOffset) {
						action.accept(arg2);
					}
				}
				break;
			case 1:
				action.accept(arg1);
				if (startOffset + 1 < endOffset) {
					action.accept(arg2);
				}
				break;
			case 2:
				action.accept(arg2);
				break;
			}
		}
	}

	@Override
	public void forEach(int start, int end, ObjIntConsumer<? super IExpr> action) {
		if (start < end) {
			switch (start) {
			case 0:
				action.accept(arg0, 0);
				if (start + 1 < end) {
					action.accept(arg1, 1);
					if (start + 2 < end) {
						action.accept(arg2, 2);
					}
				}
				break;
			case 1:
				action.accept(arg1, 1);
				if (start + 1 < end) {
					action.accept(arg2, 2);
				}
				break;
			case 2:
				action.accept(arg2, 2);
				break;
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(Predicate<? super IExpr> predicate) {
		if (predicate.test(arg1)) {
			return 1;
		}
		if (predicate.test(arg2)) {
			return 2;
		}
		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr findFirst(Function<IExpr, IExpr> function) {
		IExpr temp = function.apply(arg1);
		if (temp.isPresent()) {
			return temp;
		}
		return function.apply(arg2);
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
	public IAST getItems(int[] items, int length) {
		if (length == 0) {
			return new AST0(head());
		}
		if (length == 2 && items[0] == 1 && items[1] == 2) {
			return this;
		}
		if (length == 1) {
			return new AST1(head(), get(items[0]));
		}
		throw new IndexOutOfBoundsException("Index: 1, Size: " + size());
	}

	@Override
	public int hashCode() {
		if (hashValue == 0 && arg2 != null) {
			hashValue = 0x811c9dc5;// decimal 2166136261;
			hashValue = (hashValue * 16777619) ^ (arg0.hashCode() & 0xff);
			hashValue = (hashValue * 16777619) ^ (arg1.hashCode() & 0xff);
			hashValue = (hashValue * 16777619) ^ (arg2.hashCode() & 0xff);
		}
		return hashValue;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAST1() {
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
	public final boolean isPlus() {
		return arg0 == F.Plus;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPower() {
		return arg0 == F.Power;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(ISymbol head, int length) {
		return arg0 == head && length == SIZE;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
		return arg0 == head && minLength <= SIZE && maxLength >= SIZE;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHeadSizeGE(ISymbol head, int length) {
		return arg0 == head && length <= SIZE;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isTimes() {
		return arg0 == F.Times;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr last() {
		return arg2;
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr oneIdentity(IExpr defaultValue) {
		return this;
	}

	@Override
	public IAST removeFromEnd(int fromPosition) {
		if (fromPosition == 1) {
			return new AST0(arg0);
		}
		if (fromPosition == 2) {
			return new AST1(arg0, arg1);
		}
		if (fromPosition == 3) {
			return this;
		}
		throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(fromPosition) + ", Size: " + size());
	}

	/**
	 * Replaces the element at the specified location in this {@code ArrayList} with the specified object.
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
	 * Returns a new array containing all elements contained in this {@code ArrayList}.
	 * 
	 * @return an array of the elements from this {@code ArrayList}
	 */
	@Override
	public IExpr[] toArray() {
		return new IExpr[] { arg0, arg1, arg2 };
	}

}
