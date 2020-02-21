package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.RandomAccess;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

import org.matheclipse.core.generic.ObjIntPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public abstract class B2 extends AbstractAST implements Cloneable, Externalizable, RandomAccess {
	private final static int SIZE = 3;

	public final static class List extends B2 {
		public List() {
			super();
		}

		List(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.List;
		}

		public IASTMutable copy() {
			return new List(arg1, arg2);
		}
	}

	public final static class And extends B2 {
		public And() {
			super();
		}

		And(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.And;
		}

		public IASTMutable copy() {
			return new And(arg1, arg2);
		}
	}

	public final static class Condition extends B2 {
		public Condition() {
			super();
		}

		Condition(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Condition;
		}

		public IASTMutable copy() {
			return new Condition(arg1, arg2);
		}
	}

	public final static class DirectedEdge extends B2 {
		public DirectedEdge() {
			super();
		}

		DirectedEdge(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.DirectedEdge;
		}

		public IASTMutable copy() {
			return new DirectedEdge(arg1, arg2);
		}
	}

	public final static class Equal extends B2 {
		public Equal() {
			super();
		}

		Equal(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Equal;
		}

		public IASTMutable copy() {
			return new Equal(arg1, arg2);
		}
	}

	public final static class FreeQ extends B2 {
		public FreeQ() {
			super();
		}

		FreeQ(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.FreeQ;
		}

		public IASTMutable copy() {
			return new FreeQ(arg1, arg2);
		}
	}

	public final static class Greater extends B2 {
		public Greater() {
			super();
		}

		Greater(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Greater;
		}

		public IASTMutable copy() {
			return new Greater(arg1, arg2);
		}
	}

	public final static class GreaterEqual extends B2 {
		public GreaterEqual() {
			super();
		}

		GreaterEqual(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.GreaterEqual;
		}

		public IASTMutable copy() {
			return new GreaterEqual(arg1, arg2);
		}
	}

	public final static class If extends B2 {
		public If() {
			super();
		}

		If(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.If;
		}

		public IASTMutable copy() {
			return new If(arg1, arg2);
		}
	}

	public final static class Integrate extends B2 {
		public Integrate() {
			super();
		}

		Integrate(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Integrate;
		}

		public IASTMutable copy() {
			return new Integrate(arg1, arg2);
		}
	}

	public final static class Less extends B2 {
		public Less() {
			super();
		}

		Less(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Less;
		}

		public IASTMutable copy() {
			return new Less(arg1, arg2);
		}
	}

	public final static class LessEqual extends B2 {
		public LessEqual() {
			super();
		}

		LessEqual(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.LessEqual;
		}

		public IASTMutable copy() {
			return new LessEqual(arg1, arg2);
		}
	}

	public final static class MemberQ extends B2 {
		public MemberQ() {
			super();
		}

		MemberQ(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.MemberQ;
		}

		public IASTMutable copy() {
			return new MemberQ(arg1, arg2);
		}
	}

	public final static class Or extends B2 {
		public Or() {
			super();
		}

		Or(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Or;
		}

		public IASTMutable copy() {
			return new Or(arg1, arg2);
		}
	}

	public final static class Part extends B2 {
		public Part() {
			super();
		}

		Part(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Part;
		}

		public IASTMutable copy() {
			return new Part(arg1, arg2);
		}
	}

	public final static class Plus extends B2 {
		public Plus() {
			super();
		}

		Plus(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Plus;
		}

		public IASTMutable copy() {
			return new Plus(arg1, arg2);
		}

		public boolean isPlus() {
			return true;
		}

		public boolean isPower() {
			return false;
		}

		public boolean isTimes() {
			return false;
		}
	}

	public final static class PolynomialQ extends B2 {
		public PolynomialQ() {
			super();
		}

		PolynomialQ(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.PolynomialQ;
		}

		public IASTMutable copy() {
			return new PolynomialQ(arg1, arg2);
		}
	}

	public final static class Power extends B2 {
		public Power() {
			super();
		}

		Power(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public IExpr base() {
			return arg1;
		}

		@Override
		public IExpr exponent() {
			return arg2;
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Power;
		}

		public IASTMutable copy() {
			return new Power(arg1, arg2);
		}

		public boolean isPlus() {
			return false;
		}

		public boolean isPower() {
			return true;
		}

		public boolean isTimes() {
			return false;
		}
	}

	public final static class B2Set extends B2 {
		public B2Set() {
			super();
		}

		B2Set(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Set;
		}

		public IASTMutable copy() {
			return new B2Set(arg1, arg2);
		}
	}

	public final static class Rule extends B2 {
		public Rule() {
			super();
		}

		Rule(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Rule;
		}

		public IASTMutable copy() {
			return new Rule(arg1, arg2);
		}
	}

	public final static class RuleDelayed extends B2 {
		public RuleDelayed() {
			super();
		}

		RuleDelayed(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.RuleDelayed;
		}

		public IASTMutable copy() {
			return new RuleDelayed(arg1, arg2);
		}
	}

	public final static class SameQ extends B2 {
		public SameQ() {
			super();
		}

		SameQ(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.SameQ;
		}

		public IASTMutable copy() {
			return new SameQ(arg1, arg2);
		}
	}

	public final static class Times extends B2 {
		public Times() {
			super();
		}

		Times(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.Times;
		}

		public IASTMutable copy() {
			return new Times(arg1, arg2);
		}

		public boolean isPlus() {
			return false;
		}

		public boolean isPower() {
			return false;
		}

		public boolean isTimes() {
			return true;
		}
	}

	public final static class UndirectedEdge extends B2 {
		public UndirectedEdge() {
			super();
		}

		UndirectedEdge(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.UndirectedEdge;
		}

		public IASTMutable copy() {
			return new UndirectedEdge(arg1, arg2);
		}
	}

	public final static class With extends B2 {
		public With() {
			super();
		}

		With(IExpr arg1, IExpr arg2) {
			super(arg1, arg2);
		}

		@Override
		public final IBuiltInSymbol head() {
			return F.With;
		}

		public IASTMutable copy() {
			return new With(arg1, arg2);
		}
	}

	/**
	 * The second argument of this function.
	 */
	protected IExpr arg1;

	/**
	 * The second argument of this function.
	 */
	protected IExpr arg2;

	/**
	 * ctor for deserialization
	 */
	public B2() {
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
	public B2(IExpr arg1, IExpr arg2) {
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	/**
	 * Get the first argument (i.e. the second element of the underlying list structure) of the <code>AST</code>
	 * function (i.e. get(1) ). <br />
	 * <b>Example:</b> for the AST representing the expression <code>Sin(x)</code>, <code>arg1()</code> returns
	 * <code>x</code>.
	 * 
	 * @return the first argument of the function represented by this <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	final public IExpr arg1() {
		return arg1;
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

	/**
	 * Get the third argument (i.e. the fourth element of the underlying list structure) of the <code>AST</code>
	 * function (i.e. get(3) ).<br />
	 * <b>Example:</b> for the AST representing the expression <code>f(a, b, c)</code>, <code>arg3()</code> returns
	 * <code>c</code>.
	 * 
	 * @return the third argument of the function represented by this <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public IExpr arg3() {
		// be compatible with get() contract
		throw new IndexOutOfBoundsException("Index: 3, Size: " + size());
	}

	/**
	 * Get the fourth argument (i.e. the fifth element of the underlying list structure) of the <code>AST</code>
	 * function (i.e. get(4) ).<br />
	 * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d)</code>, <code>arg4()</code> returns
	 * <code>d</code>.
	 * 
	 * @return the fourth argument of the function represented by this <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public IExpr arg4() {
		// be compatible with get() contract
		throw new IndexOutOfBoundsException("Index: 4, Size: " + size());
	}

	/**
	 * Get the fifth argument (i.e. the sixth element of the underlying list structure) of the <code>AST</code> function
	 * (i.e. get(5) ).<br />
	 * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d, e)</code>, <code>arg5()</code>
	 * returns <code>e</code> .
	 * 
	 * @return the fifth argument of the function represented by this <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public IExpr arg5() {
		// be compatible with get() contract
		throw new IndexOutOfBoundsException("Index: 5, Size: " + size());
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
	public IAST clone() {
		return copy();
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object object) {
		return head().equals(object) || arg1.equals(object) || arg2.equals(object);
	}

	/** {@inheritDoc} */
	public abstract IASTMutable copy();

	@Override
	public IASTAppendable copyAppendable() {
		return new AST(head(), arg1, arg2);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AbstractAST) {
			final IAST list = (IAST) obj;
			ISymbol head = head();
			if (head != ((AbstractAST) list).head()) {
				// compared with ISymbol object identity
				return false;
			}
			return list.size() == SIZE && arg1.equals(list.arg1()) && arg2.equals(list.arg2());
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean exists(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
		switch (startOffset) {
		case 0:
			return predicate.test(head(), 0) || predicate.test(arg1, 1) || predicate.test(arg2, 2);
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
			return predicate.test(head()) || predicate.test(arg1) || predicate.test(arg2);
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
			return predicate.test(head(), 0) && predicate.test(arg1, 1) && predicate.test(arg2, 2);
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
			return predicate.test(head()) && predicate.test(arg1) && predicate.test(arg2);
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
			action.accept(head());
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
				action.accept(head());
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
				action.accept(head(), 0);
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
			return head();
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
			return this;
		}
		AST result = new AST(length, true);
		result.set(0, head());
		for (int i = 0; i < length; i++) {
			result.set(i + 1, get(items[i]));
		}
		return result;
	}

	@Override
	public abstract ISymbol head();

	@Override
	public int hashCode() {
		if (hashValue == 0 && arg2 != null) {
			hashValue = 0x811c9dc5;// decimal 2166136261;
			hashValue = (hashValue * 16777619) ^ (head().hashCode() & 0xff);
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
	public boolean isPlus() {
		return head() == F.Plus;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPower() {
		return head() == F.Power;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(ISymbol head, int length) {
		return head() == head && length == SIZE;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
		return head() == head && minLength <= SIZE && maxLength >= SIZE;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHeadSizeGE(ISymbol head, int length) {
		return head() == head && length <= SIZE;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTimes() {
		return head() == F.Times;
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
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.fEvalFlags = objectInput.readShort();

		// int size;
		// byte attributeFlags = objectInput.readByte();
		// if (attributeFlags != 0) {
		// size = attributeFlags;
		// int exprIDSize = objectInput.readByte();
		// for (int i = 0; i < exprIDSize; i++) {
		// set(i, F.GLOBAL_IDS[objectInput.readShort()]);
		// }
		// for (int i = exprIDSize; i < size; i++) {
		// set(i, (IExpr) objectInput.readObject());
		// }
		// return;
		// }

		// size = objectInput.readInt();
		for (int i = 1; i < SIZE; i++) {
			set(i, (IExpr) objectInput.readObject());
		}
	}

	@Override
	public IAST removeFromEnd(int fromPosition) {
		if (fromPosition == 1) {
			return new AST0(head());
		}
		if (fromPosition == 2) {
			return new AST1(head(), arg1);
		}
		if (fromPosition == 3) {
			return this;
		}
		throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(fromPosition) + ", Size: " + size());
	}

	public IASTMutable setAtCopy(int i, IExpr expr) {
		if (i == 0) {
			IASTMutable ast = new AST2(head(), arg1(), arg2());
			ast.set(i, expr);
			return ast;
		}
		IASTMutable ast = copy();
		ast.set(i, expr);
		return ast;
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
			throw new IndexOutOfBoundsException("Index: 0, Size: 3");
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
		return new IExpr[] { head(), arg1, arg2 };
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeShort(fEvalFlags);

		// int size = size();
		// byte attributeFlags = (byte) 0;
		//
		// ExprID temp = F.GLOBAL_IDS_MAP.get(head());
		// if (temp != null) {
		// short exprID = temp.getExprID();
		// if (exprID <= Short.MAX_VALUE) {
		// int exprIDSize = 1;
		// short[] exprIDArray = new short[size];
		// exprIDArray[0] = exprID;
		// for (int i = 1; i < size; i++) {
		// temp = F.GLOBAL_IDS_MAP.get(get(i));
		// if (temp == null) {
		// break;
		// }
		// exprID = temp.getExprID();
		// if (exprID <= Short.MAX_VALUE) {
		// exprIDArray[i] = exprID;
		// exprIDSize++;
		// } else {
		// break;
		// }
		// }
		// // optimized path
		// attributeFlags = (byte) size;
		// objectOutput.writeByte(attributeFlags);
		// objectOutput.writeByte((byte) exprIDSize);
		// for (int i = 0; i < exprIDSize; i++) {
		// objectOutput.writeShort(exprIDArray[i]);
		// }
		// for (int i = exprIDSize; i < size; i++) {
		// objectOutput.writeObject(get(i));
		// }
		// return;
		// }
		// }

		// objectOutput.writeByte(attributeFlags);
		// objectOutput.writeInt(size-1);
		for (int i = 1; i < SIZE; i++) {
			objectOutput.writeObject(get(i));
		}
	}

}
