package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.hipparchus.complex.Complex;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.jgrapht.GraphType;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.DefaultGraphType.Builder;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.Structure.LeafCount;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IRewrite;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.generic.ObjIntPredicate;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.generic.UnaryVariable2Slot;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDiscreteDistribution;
import org.matheclipse.core.interfaces.IDistribution;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.IUnaryIndexFunction;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherEvalEngine;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public abstract class AbstractAST implements IASTMutable {
	protected static final class ASTIterator implements ListIterator<IExpr> {

		private int _currentIndex;

		private int _end; // Exclusive.

		private int _nextIndex;

		private int _start; // Inclusive.

		private IASTMutable _table;

		@Override
		public void add(IExpr o) {
			throw new UnsupportedOperationException();
			// _table.append(_nextIndex++, o);
			// _end++;
			// _currentIndex = -1;
		}

		@Override
		public boolean hasNext() {
			return _nextIndex != _end;
		}

		@Override
		public boolean hasPrevious() {
			return _nextIndex != _start;
		}

		@Override
		public IExpr next() {
			if (_nextIndex == _end)
				throw new NoSuchElementException();
			_currentIndex = _nextIndex++;
			return _table.get(_currentIndex);
		}

		@Override
		public int nextIndex() {
			return _nextIndex;
		}

		@Override
		public IExpr previous() {
			if (_nextIndex == _start)
				throw new NoSuchElementException();
			_currentIndex = --_nextIndex;
			return _table.get(_currentIndex);
		}

		@Override
		public int previousIndex() {
			return _nextIndex - 1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			// if (_currentIndex >= 0) {
			// _table.remove(_currentIndex);
			// _end--;
			// if (_currentIndex < _nextIndex) {
			// _nextIndex--;
			// }
			// _currentIndex = -1;
			// } else {
			// throw new IllegalStateException();
			// }
		}

		@Override
		public void set(IExpr o) {
			if (_currentIndex >= 0) {
				_table.set(_currentIndex, o);
			} else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * The class <code>NILPointer</code> implements the constant object <code>F.NIL</code> (not in list), which
	 * indicates in the evaluation process that no evaluation was possible (i.e. no further definition was found to
	 * create a new expression from the existing one).
	 * <p>
	 * Almost every modifying method in this class throws an <tt>UnsupportedOperationException</tt>, almost every
	 * predicate returns <code>false</code>. The main method to check if the object is valid is the
	 * <code>isPresent()</code> method. The method is designed similar to <code>java.util.Optional#isPresent()</code>.
	 * </p>
	 * 
	 * @see org.matheclipse.core.expression.F#NIL
	 * @see java.util.Optional#isPresent
	 */
	private final static class NILPointer extends AbstractAST implements IASTAppendable {

		private static final long serialVersionUID = -3552302876858011292L;

		/**
		 * The class <code>NILPointer</code> implements the constant object <code>F#NIL</code> (not in list), which
		 * indicates in the evaluation process that no evaluation was possible (i.e. no further definition was found to
		 * create a new expression from the existing one).
		 * 
		 * @see F#NIL
		 */
		protected NILPointer() {
		}

		@Override
		public boolean append(IExpr object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void append(int location, IExpr object) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean appendAll(Collection<? extends IExpr> collection) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean appendAll(Map<? extends IExpr, ? extends IExpr> map) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean appendAll(IAST ast, int startPosition, int endPosition) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean appendAll(IExpr[] args, int startPosition, int endPosition) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean appendAll(int location, Collection<? extends IExpr> collection) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean appendAll(List<? extends IExpr> list, int startPosition, int endPosition) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean appendArgs(IAST ast) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final boolean appendArgs(IAST ast, int untilPosition) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IASTAppendable appendArgs(int start, int end, IntFunction<IExpr> function) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IASTAppendable appendArgs(int end, IntFunction<IExpr> function) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IAST appendOneIdentity(IAST subAST) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr arg1() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr arg2() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr arg3() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr arg4() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IExpr arg5() {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc} */
		public int argSize() {
			return -1;
		}

		@Override
		public Set<IExpr> asSet() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IAST clone() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(Object object) {
			return false;
		}

		@Override
		public IASTAppendable copy() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IASTAppendable copyAppendable() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(final Object obj) {
			return this == obj;
		}

		@Override
		public final IExpr evaluate(EvalEngine engine) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean exists(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
			return false;
		}

		@Override
		public boolean exists(Predicate<? super IExpr> predicate, int startOffset) {
			return false;
		}

		@Override
		public boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
			return false;
		}

		@Override
		public boolean forAll(Predicate<? super IExpr> predicate, int startOffset) {
			return false;
		}

		@Override
		public int hashCode() {
			return -1;
		}

		@Override
		public final IExpr head() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void ifAppendable(Consumer<? super IASTAppendable> consumer) {
		}

		@Override
		public IExpr ifPresent(Function<? super IExpr, IExpr> function) {
			return F.NIL;
		}

		@Override
		public boolean isAbs() {
			return false;
		}

		@Override
		public boolean isAllExpanded() {
			return false;
		}

		@Override
		public final boolean isAST() {
			return false;
		}

		@Override
		public final boolean isAST(final IExpr header) {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean isAST(final IExpr header, final int length) {
			return false;
		}

		@Override
		public boolean isAST(IExpr header, int length, IExpr... args) {
			return false;
		}

		@Override
		public boolean isAST0() {
			return false;
		}

		@Override
		public boolean isAST1() {
			return false;
		}

		@Override
		public boolean isAST2() {
			return false;
		}

		@Override
		public boolean isAST3() {
			return false;
		}

		@Override
		public boolean isASTSizeGE(IExpr header, int length) {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isBooleanFormula() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isBooleanResult() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isIntegerResult() {
			return false;
		}

		public final boolean isInterval() {
			return false;
		}

		public final boolean isInterval1() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isList() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isList(Predicate<IExpr> pred) {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean isListOfLists() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public final GraphType isListOfEdges() {
			return null;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean isListOfRules() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public int[] isMatrix(boolean setMatrixFormat) {
			return null;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isNegativeResult() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isNonNegativeResult() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean isNumericFunction() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean isNumericMode() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean isOneIdentityAST1() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean isPlus() {
			return false;
		}

		@Override
		public boolean isPlusTimesPower() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean isPower() {
			return false;
		}

		@Override
		public final boolean isPresent() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isRealResult() {
			return false;
		}

		@Override
		public boolean isRealMatrix() {
			return false;
		}

		@Override
		public boolean isRealVector() {
			return false;
		}

		@Override
		public boolean isSameHead(ISymbol head) {
			return false;
		}

		@Override
		public boolean isSameHead(ISymbol head, int length) {
			return false;
		}

		@Override
		public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
			return false;
		}

		@Override
		public boolean isSameHeadSizeGE(ISymbol head, int length) {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean isTimes() {
			return false;
		}

		@Override
		public final int isVector() {
			return -1;
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public final boolean isZERO() {
			return false;
		}

		@Override
		public IExpr map(Function<? super IExpr, ? extends IExpr> mapper) {
			return this;
		}

		@Override
		public final IAST orElse(final IAST other) {
			return other;
		}

		@Override
		public final IExpr orElse(final IExpr other) {
			return other;
		}

		@Override
		public final IExpr orElseGet(Supplier<? extends IExpr> other) {
			return other.get();
		}

		@Override
		public final <X extends Throwable> IExpr orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
			throw exceptionSupplier.get();
		}

		private Object readResolve() throws ObjectStreamException {
			return F.NIL;
		}

		@Override
		public IExpr remove(int location) {
			throw new UnsupportedOperationException();
		}

		/**
		 * Removes the objects in the specified range from the start to the end, but not including the end index.
		 * 
		 * @param start
		 *            the index at which to start removing.
		 * @param end
		 *            the index one after the end of the range to remove.
		 * @throws IndexOutOfBoundsException
		 *             when {@code start < 0, start > end} or {@code end > size()}
		 */
		@Override
		public void removeRange(int start, int end) {
			throw new UnsupportedOperationException();
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
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return 0;
		}

		/**
		 * Returns a new array containing all elements contained in this {@code ArrayList}.
		 * 
		 * @return an array of the elements from this {@code ArrayList}
		 */
		@Override
		public IExpr[] toArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return "ExprNull";
		}
	}

	/**
	 * The enumeration map which possibly maps the properties (keys) to a user defined object.
	 * 
	 */
	private static Cache<IAST, EnumMap<PROPERTY, Object>> IAST_CACHE = null;

	/** package private */
	final static NILPointer NIL = new NILPointer();

	private static final long serialVersionUID = -8682706994448890660L;

	private static int compareToASTDecreasing(final IAST lhsAST, final IAST rhsAST) {
		int lhsSize = lhsAST.size();
		int rhsSize = rhsAST.size();
		int k = (lhsSize > rhsSize) ? rhsSize : lhsSize;
		k--;
		while (k-- > 0) {
			int cp = lhsAST.get(--lhsSize).compareTo(rhsAST.get(--rhsSize));
			if (cp != 0) {
				return cp;
			}
		}
		return (lhsSize > rhsSize) ? 1 : (lhsSize < rhsSize) ? -1 : 0;
	}

	private static int compareToASTDecreasingArg1(final IAST lhsAST, final IExpr arg1, IInteger value) {
		int lhsSize = lhsAST.size();
		int cp = lhsAST.get(lhsSize - 1).compareTo(arg1);
		if (cp != 0) {
			return cp;
		}
		if (lhsSize >= 2) {
			cp = lhsAST.get(--lhsSize - 1).compareTo(value);
			if (cp != 0) {
				return cp;
			}
		}
		return 1;
	}

	private static int compareToASTIncreasing(final IAST lhsAST, final IAST rhsAST) {

		if (lhsAST.isPlusTimesPower()) {
			if (!rhsAST.isPlusTimesPower()) {
				return -1;
			}
		} else {
			if (rhsAST.isPlusTimesPower()) {
				return 1;
			}
		}

		// compare the headers
		int cp = lhsAST.head().compareTo(rhsAST.head());
		if (cp != 0) {
			return cp;
		}

		final int minimumSize = (lhsAST.size() > rhsAST.size()) ? rhsAST.size() : lhsAST.size();
		for (int i = 1; i < minimumSize; i++) {
			cp = lhsAST.get(i).compareTo(rhsAST.get(i));
			if (cp != 0) {
				return cp;
			}
		}

		if (lhsAST.size() > rhsAST.size()) {
			return 1;
		}
		if (lhsAST.size() < rhsAST.size()) {
			return -1;
		}
		return 0;
	}

	private static int compareToASTIncreasingArg1(final IAST lhsAST, final IExpr arg1, IInteger value) {
		int cp = lhsAST.arg1().compareTo(arg1);
		if (cp != 0) {
			return cp;
		}
		if (lhsAST.size() >= 2) {
			cp = lhsAST.arg2().compareTo(value);
			if (cp != 0) {
				return cp;
			}
		}
		return 1;
	}

	private static void internalFormOrderless(IAST ast, StringBuilder text, final String sep,
			boolean symbolsAsFactoryMethod, int depth, boolean useOperators, boolean usePrefix,
			boolean noSymbolPrefix) {
		for (int i = 1; i < ast.size(); i++) {
			if ((ast.get(i) instanceof IAST) && ast.head().equals(ast.get(i).head())) {
				internalFormOrderless((IAST) ast.get(i), text, sep, symbolsAsFactoryMethod, depth, useOperators,
						usePrefix, noSymbolPrefix);
			} else {
				text.append(ast.get(i).internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators, usePrefix,
						noSymbolPrefix));
			}
			if (i < ast.argSize()) {
				text.append(sep);
			}
		}
	}

	/**
	 * Replace all elements determined by the unary <code>from</code> predicate, with the element generated by the unary
	 * <code>to</code> function. If the unary function returns null replaceAll returns null.
	 * 
	 * @return <code>F.NIL</code> if no replacement occurs.
	 */
	private static IExpr variables2Slots(final IExpr expr, final Predicate<IExpr> from,
			final Function<IExpr, ? extends IExpr> to) {
		if (from.test(expr)) {
			return to.apply(expr);
		}

		if (expr.isAST()) {
			IAST nestedList = (IAST) expr;
			IASTMutable result;
			final IExpr head = nestedList.head();
			IExpr temp = variables2Slots(head, from, to);
			if (temp.isPresent()) {
				result = nestedList.apply(temp);
			} else {
				return F.NIL;
			}

			final int size = nestedList.size();
			for (int i = 1; i < size; i++) {
				temp = variables2Slots(nestedList.get(i), from, to);
				if (temp.isPresent()) {
					result.set(i, temp);
				} else {
					return F.NIL;
				}
			}

			return result;
		}

		return expr;
	}

	/**
	 * Flags for controlling evaluation and left-hand-side pattern-matching expressions
	 * 
	 */
	protected int fEvalFlags = 0;

	protected transient int hashValue;

	public AbstractAST() {
		super();
		hashValue = 0;
	}

	/** {@inheritDoc} */
	@Override
	public final <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public final int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public final IAST addEvalFlags(final int i) {
		fEvalFlags |= i;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public IASTAppendable appendAtClone(int position, IExpr expr) {
		IASTAppendable ast = copyAppendable();
		ast.append(position, expr);
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public IASTAppendable appendClone(IExpr expr) {
		IASTAppendable ast = copyAppendable();
		ast.append(expr);
		return ast;
	}

	@Override
	public IASTAppendable apply(final IExpr head) {
		return setAtClone(0, head);
	}

	@Override
	public final IAST apply(final IExpr head, final int start) {
		return apply(head, start, size());
	}

	@Override
	public IAST apply(final IExpr head, final int start, final int end) {
		final IASTAppendable ast = F.ast(head, end - start, false);
		ast.appendArgs(start, end, i -> get(i));
		// for (int i = start; i < end; i++) {
		// ast.append(get(i));
		// }
		return ast;
	}

	@Override
	public Object asType(Class<?> clazz) {
		if (clazz.equals(Boolean.class)) {
			IExpr temp = F.eval(this);
			if (temp.equals(F.True)) {
				return Boolean.TRUE;
			}
			if (temp.equals(F.False)) {
				return Boolean.FALSE;
			}
		} else if (clazz.equals(Integer.class)) {
			IExpr temp = F.eval(this);
			if (temp.isReal()) {
				try {
					return Integer.valueOf(((ISignedNumber) this).toInt());
				} catch (final ArithmeticException e) {
				}
			}
		} else if (clazz.equals(java.math.BigInteger.class)) {
			IExpr temp = F.eval(this);
			if (temp instanceof AbstractIntegerSym) {
				return new java.math.BigInteger(((AbstractIntegerSym) temp).toByteArray());
			}
		} else if (clazz.equals(String.class)) {
			return toString();
		}
		throw new UnsupportedOperationException("AST.asType() - cast not supported.");
	}

	@Override
	public void clearHashCache() {
		this.hashValue = 0;
	}

	@Override
	public abstract IAST clone() throws CloneNotSupportedException;
	// {
	// AbstractAST ast = null;
	// try {
	// ast = (AbstractAST) super.clone();
	// ast.fEvalFlags = 0;
	// ast.hashValue = 0;
	// } catch (CloneNotSupportedException e) {
	// }
	// return ast;
	// }

	/**
	 * Compare all adjacent elements from lowest to highest index and return true, if the binary predicate gives true in
	 * each step. If the size is &lt; 2 the method returns false;
	 * 
	 * @param predicate
	 *            the binary predicate
	 * @return
	 */
	public boolean compareAdjacent(BiPredicate<IExpr, IExpr> predicate) {
		if (size() < 2) {
			return false;
		}
		IExpr elem = get(1);
		for (int i = 2; i < size(); i++) {

			if (!predicate.test(elem, get(i))) {
				return false;
			}
			elem = get(i);
		}
		return true;
	}

	/**
	 * Compares this expression with the specified expression for canonical order. Returns a negative integer, zero, or
	 * a positive integer as this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr rhsExpr) {
		final int lhsOrdinal = headID();
		int rhsOrdinal = -1;
		if (lhsOrdinal < 0) {
			if (rhsExpr.isNumber()) {
				// O-7
				return 1;
			}
			rhsOrdinal = rhsExpr.headID();
			if (rhsOrdinal < 0) {
				if (rhsExpr.isAST()) {
					return compareToASTIncreasing(this, (IAST) rhsExpr);
				}
				final int x = hierarchy();
				final int y = rhsExpr.hierarchy();
				return (x < y) ? -1 : ((x == y) ? 0 : 1);
			}
		} else {
			if (lhsOrdinal == ID.DirectedInfinity && isDirectedInfinity()) {
				if (!rhsExpr.isDirectedInfinity()) {
					return -1;
				}
				return compareToASTIncreasing(this, (IAST) rhsExpr);
			}
			if (rhsExpr.isNumber()) {
				// O-7
				return 1;
			}
			rhsOrdinal = (rhsExpr.head() instanceof IBuiltInSymbol) ? ((IBuiltInSymbol) rhsExpr.head()).ordinal() : -1;
		}
		if (rhsExpr.isAST()) {
			if (rhsOrdinal == ID.DirectedInfinity && rhsExpr.isDirectedInfinity()) {
				return 1;
			}
			if (lhsOrdinal >= ID.Plus && lhsOrdinal <= ID.Times && size() > 1) {
				IAST rhs = (IAST) rhsExpr;

				switch (lhsOrdinal) {
				case ID.Plus:
					if (rhsOrdinal == ID.Plus) {
						if (rhs.size() >= 1) {
							// O-3
							return compareToASTDecreasing(this, rhs);
						}
					} else if (!rhsExpr.isSameHeadSizeGE(F.Plus, 1) && !rhsExpr.isSameHeadSizeGE(F.Times, 1)) {
						// O-10
						return compareToASTDecreasingArg1(this, rhsExpr, F.C0);
					}
					break;
				case ID.Power:
					if (rhsOrdinal == ID.Power) {
						if (rhs.size() == 3) {
							// O-4
							// if (base().isNumber() && rhs.base().isNumber()) {
							// int exponentCompare = exponent().compareTo(rhs.exponent());
							// if (exponentCompare == 0) {
							// return base().compareTo(rhs.base());
							// }
							// return exponentCompare;
							// }
							int baseCompare = base().compareTo(rhs.base());
							if (baseCompare == 0) {
								return exponent().compareTo(rhs.exponent());
							}
							return baseCompare;
						}
						// O-9
						return compareToASTIncreasingArg1(this, rhsExpr, F.C1);
					} else if (!rhsExpr.isSameHeadSizeGE(F.Times, 1) && !rhsExpr.isSameHeadSizeGE(F.Plus, 1)) {
						// O-9
						return compareToASTIncreasingArg1(this, rhsExpr, F.C1);
					}
					break;
				case ID.Times:
					if (rhsOrdinal == ID.Times && rhs.size() >= 1) {
						// O-3
						return compareToASTDecreasing(this, rhs);
					}
					// O-8
					return compareToASTDecreasingArg1(this, rhsExpr, F.C1);
				}

			}
			if (rhsOrdinal < 0 || !rhsExpr.isPlusTimesPower()) {
				return compareToASTIncreasing(this, (IAST) rhsExpr);
			}
			return -1 * rhsExpr.compareTo(this);
		}

		if (lhsOrdinal >= ID.Not && lhsOrdinal <= ID.Times && size() > 1) {
			switch (lhsOrdinal) {
			case ID.Not:
				if (rhsExpr.isSymbol() && arg1().isSymbol() && size() == 2) {
					return -1 * rhsExpr.compareTo(this);
				}
				break;
			case ID.Plus:
				// Plus O-10
				return compareToASTDecreasingArg1(this, rhsExpr, F.C1);
			case ID.Power:
				if (size() == 3) {
					// O-9
					return compareToASTIncreasingArg1(this, rhsExpr, F.C1);
				}
				break;
			case ID.Times:
				// Times O-8
				return compareToASTDecreasingArg1(this, rhsExpr, F.C1);
			}

		}

		final int x = hierarchy();
		final int y = rhsExpr.hierarchy();
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object object) {
		return exists(x -> object.equals(x), 0);
	}

	public IAST copyAlloc(int capacity) {
		return copy();
	}

	/** {@inheritDoc} */
	@Override
	public IASTAppendable copyFrom(int index) {
		AST result = new AST(size() - index + 1, false);
		result.append(head());
		result.appendAll(this, index, size());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public final IASTAppendable copyHead() {
		return AST.newInstance(head());
	}

	/** {@inheritDoc} */
	@Override
	public final IASTAppendable copyHead(final int intialCapacity) {
		return AST.newInstance(intialCapacity, head());
	}

	/** {@inheritDoc} */
	@Override
	public IASTAppendable copyUntil(int index) {
		return AST.newInstance(index, this, index);
	}

	/** {@inheritDoc} */
	@Override
	public final IASTAppendable copyUntil(final int intialCapacity, int index) {
		return AST.newInstance(intialCapacity, this, index);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AbstractAST) {
			if (hashCode() != obj.hashCode()) {
				return false;
			}
			final IAST list = (IAST) obj;
			if (size() != list.size()) {
				return false;
			}
			IExpr head = head();
			if (head != ((AbstractAST) obj).head()) {
				if (head instanceof ISymbol) {
					return false;
				}
			}
			return forAll((x, i) -> x.equals(list.get(i)), 0);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equalsAt(int position, final IExpr expr) {
		return get(position).equals(expr);
	}

	public final boolean equalsFromPosition(final int from0, final IAST f1, final int from1) {
		if ((size() - from0) != (f1.size() - from1)) {
			return false;
		}

		int j = from1;
		for (int i = from0; i < argSize(); i++) {
			if (!get(i + 1).equals(f1.get(1 + j++))) {
				return false;
			}
		}

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final Complex evalComplex() {
		INumber number = evalNumber();
		if (number != null) {
			return number.complexNumValue().complexValue();
		}
		throw new WrongArgumentType(this, "Conversion into a complex numeric value is not possible!");
	}

	/** {@inheritDoc} */
	// @Override
	// public final double evalDouble() {
	// return EvalEngine.get().evalDouble(this);
	// ISignedNumber signedNumber = evalReal();
	// if (signedNumber != null) {
	// return signedNumber.doubleValue();
	// }
	// throw new WrongArgumentType(this, "Conversion into a double numeric value is not possible!");
	// }

	/** {@inheritDoc} */
	@Override
	public final INumber evalNumber() {
		if (isNumericFunction()) {
			IExpr result = EvalEngine.get().evalN(this);
			if (result.isNumber()) {
				return (INumber) result;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final ISignedNumber evalReal() {
		if (isNumericFunction()) {
			IExpr result = EvalEngine.get().evalN(this);
			if (result.isReal()) {
				return (ISignedNumber) result;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (Config.DEBUG) {
			System.out.println(toString());
		}
		final IExpr head = head();
		if (head.isCoreFunctionSymbol()) {
			IBuiltInSymbol header = ((IBuiltInSymbol) head);
			if ((header.getAttributes() & ISymbol.SEQUENCEHOLD) != ISymbol.SEQUENCEHOLD) {
				IExpr temp;
				if ((temp = engine.flattenSequences(this)).isPresent()) {
					return temp;
				}
			}
			ICoreFunctionEvaluator functionEvaluator = (ICoreFunctionEvaluator) ((IBuiltInSymbol) head).getEvaluator();
			int[] expected;
			if ((expected = functionEvaluator.expectedArgSize()) != null) {
				if (argSize() < expected[0] || argSize() > expected[1]) {
					return IOFunctions.printArgMessage(this, expected, engine);
				}
			}

			IExpr evaluateTemp = engine.evalEvaluate(this);
			if (evaluateTemp.isPresent()) {
				return evaluateTemp;
			}

			return functionEvaluator.evaluate(this, engine);
		}

		final ISymbol symbol = topHead();
		IExpr temp = engine.evalAttributes(symbol, this).orElseGet(() -> engine.evalRules(symbol, this));

		if (Config.SHOW_CONSOLE) {
			if (temp.isPresent() && (topHead().getAttributes() & ISymbol.CONSOLE_OUTPUT) == ISymbol.CONSOLE_OUTPUT) {
				System.out.println(toString());
				System.out.println(" => " + temp.toString());
			}

		}

		return temp;

	}

	/** {@inheritDoc} */
	@Override
	public boolean exists(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
		final int size = size();
		for (int i = startOffset; i < size; i++) {
			if (predicate.test(get(i), i)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean exists(Predicate<? super IExpr> predicate, int startOffset) {
		final int size = size();
		for (int i = startOffset; i < size; i++) {
			if (predicate.test(get(i))) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final IASTAppendable[] filterNIL(final Function<IExpr, IExpr> function) {
		IASTAppendable[] result = new IASTAppendable[2];
		result[0] = copyHead();
		result[1] = copyHead();
		filterFunction(result[0], result[1], function);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IASTAppendable filterAST, IASTAppendable restAST, Predicate<? super IExpr> predicate) {
		forEach(x -> {
			if (predicate.test(x)) {
				filterAST.append(x);
			} else {
				restAST.append(x);
			}
		});
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST filter(IASTAppendable filterAST, IExpr expr) {
		EvalEngine engine = EvalEngine.get();
		return filter(filterAST, x -> engine.evalTrue(F.unaryAST1(expr, x)));
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
		forEach(size(), x -> {
			if (predicate.test(x)) {
				filterAST.append(x);
			}
		});
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate, int maxMatches) {
		int[] count = new int[1];
		if (count[0] >= maxMatches) {
			return filterAST;
		}
		exists(x -> {
			if (predicate.test(x)) {
				if (++count[0] == maxMatches) {
					filterAST.append(x);
					return true;
				}
				filterAST.append(x);
			}
			return false;
		});
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST[] filter(Predicate<? super IExpr> predicate) {
		IASTAppendable[] result = new IASTAppendable[2];
		result[0] = copyHead();
		result[1] = copyHead();
		filter(result[0], result[1], predicate);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST removeIf(Predicate<? super IExpr> predicate) {
		IASTAppendable result = copyHead();
		for (int i = 1; i < size(); i++) {
			IExpr arg = get(i);
			if (predicate.test(arg)) {
				continue;
			}
			result.append(arg);
		}
		return result;
	}

	/**
	 * Select all elements by applying the <code>function</code> to each argument in this <code>AST</code> and append
	 * the result elements for which the <code>function</code> returns non <code>F.NIL</code> elements to the
	 * <code>filterAST</code>, or otherwise append the argument to the <code>restAST</code>.
	 * 
	 * @param filterAST
	 *            the non <code>F.NIL</code> elements which were returned by the <code>function#apply()</code> method
	 * @param restAST
	 *            the arguments in this <code>AST</code> for which the <code>function#apply()</code> method returned
	 *            <code>F.NIL</code>
	 * @param function
	 *            the function which filters each argument by returning a value which unequals <code>F.NIL</code>
	 * @return the given <code>filterAST</code>
	 */
	protected IAST filterFunction(IASTAppendable filterAST, IASTAppendable restAST,
			final Function<IExpr, IExpr> function) {
		forEach(x -> {
			IExpr expr = function.apply(x);
			if (expr.isPresent()) {
				filterAST.append(expr);
			} else {
				restAST.append(x);
			}
		});
		return filterAST;
	}

	/**
	 * Apply the functor to the elements of the range from left to right and return the final result. Results do
	 * accumulate from one invocation to the next: each time this method is called, the accumulation starts over with
	 * value from the previous function call.
	 * 
	 * @param function
	 *            a binary function that accumulate the elements
	 * @param startValue
	 * @return the accumulated elements
	 */
	public IExpr foldLeft(final BiFunction<IExpr, IExpr, ? extends IExpr> function, IExpr startValue, int start) {
		final IExpr[] value = { startValue };
		forEach(start, size(), x -> value[0] = function.apply(value[0], x));
		return value[0];
	}

	/**
	 * Apply the functor to the elements of the range from right to left and return the final result. Results do
	 * accumulate from one invocation to the next: each time this method is called, the accumulation starts over with
	 * value from the previous function call.
	 * 
	 * @param function
	 *            a binary function that accumulate the elements
	 * @param startValue
	 * @return the accumulated elements
	 */
	public IExpr foldRight(final BiFunction<IExpr, IExpr, ? extends IExpr> function, IExpr startValue, int start) {
		IExpr value = startValue;
		int end = argSize();
		for (int i = end; i >= start; i--) {
			value = function.apply(value, get(i));
		}
		return value;
	}

	/** {@inheritDoc} */
	@Override
	public boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
		final int size = size();
		for (int i = startOffset; i < size; i++) {
			if (!predicate.test(get(i), i)) {
				return false;
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean forAll(Predicate<? super IExpr> predicate, int startOffset) {
		final int size = size();
		for (int i = startOffset; i < size; i++) {
			if (!predicate.test(get(i))) {
				return false;
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean forAllLeaves(Predicate<? super IExpr> predicate, int startOffset) {
		final int size = size();
		for (int i = startOffset; i < size; i++) {
			if (get(i).isAST()) {
				if (!((IAST) get(i)).forAllLeaves(predicate, startOffset)) {
					return false;
				}
			} else if (!predicate.test(get(i))) {
				return false;
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public void forEach(Consumer<? super IExpr> action) {
		forEach(action, 1);
	}

	/** {@inheritDoc} */
	@Override
	public void forEach(Consumer<? super IExpr> action, int startOffset) {
		forEach(startOffset, size(), action);
	}

	/** {@inheritDoc} */
	@Override
	public String fullFormString() {
		return fullFormString(head());
	}

	protected String fullFormString(IExpr head) {
		final String sep = ", ";
		StringBuilder text = new StringBuilder();
		if (head == null) {
			text.append("<null-head>");
			head = F.Null;
		} else {
			text.append(head.fullFormString());
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS && head.isSymbol()) {
			text.append('(');
		} else {
			text.append('[');
		}
		for (int i = 1; i < size(); i++) {
			IExpr temp = get(i);
			if (temp == null) {
				text.append("<null-arg>");
			} else {
				text.append(get(i).fullFormString());
				if (i < argSize()) {
					text.append(sep);
				}
			}
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS && head.isSymbol()) {
			text.append(')');
		} else {
			text.append(']');
		}
		return text.toString();
	}

	@Override
	public final IExpr gcd(IExpr that) {
		if (equals(that)) {
			return that;
		}
		return F.C1;
	}

	@Override
	public IExpr get(int location) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr get(IInteger location) {
		return get(location.toIntDefault(Integer.MIN_VALUE));
	}

	/**
	 * Casts an <code>IExpr</code> at position <code>index</code> to an <code>IAST</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 *             if the cast is not possible
	 */
	@Override
	public final IAST getAST(int index) {
		try {
			return (IAST) get(index);
		} catch (ClassCastException cce) {
		}
		throw new WrongArgumentType(this, get(index), index);
	}

	@Override
	public final IExpr getAt(final int index) {
		return get(index);
	}

	/** {@inheritDoc} */
	@Override
	public final int getEvalFlags() {
		return fEvalFlags;
	}

	@Override
	public int getHashCache() {
		return hashValue;
	}

	/**
	 * Casts an <code>IExpr</code> at position <code>index</code> to an <code>IInteger</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 *             if the cast is not possible
	 */
	@Override
	public final IInteger getInt(int index) {
		try {
			return (IInteger) get(index);
		} catch (ClassCastException cce) {
		}
		throw new WrongArgumentType(this, get(index), index);
	}

	/**
	 * Casts an <code>IExpr</code> which is a list at position <code>index</code> to an <code>IAST</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 */
	@Override
	public final IAST getList(int index) {
		IExpr temp = get(index);
		if (temp.isList()) {
			return (IAST) temp;
		}
		throw new WrongArgumentType(this, temp, index);
	}

	/**
	 * Casts an <code>IExpr</code> at position <code>index</code> to an <code>INumber</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 *             if the cast is not possible
	 */
	@Override
	public final INumber getNumber(int index) {
		try {
			return (INumber) get(index);
		} catch (ClassCastException cce) {
		}
		throw new WrongArgumentType(this, get(index), index);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr getOptionalValue() {
		if (isAST(F.Optional, 3)) {
			return arg2();
		}
		return null;// fOptionalValue;
	}

	@Override
	public IExpr getPart(final int... positions) {
		IExpr expr = this;
		int size = positions.length;
		for (int i = 0; i < size; i++) {
			if (!expr.isAST()) {
				break;
			}
			expr = ((IAST) expr).get(positions[i]);
			if (i == (size - 1)) {
				return expr;
			}
		}
		return null;
	}

	@Override
	public final IExpr getPart(final List<Integer> positions) {
		IExpr expr = this;
		int size = positions.size();
		for (int i = 0; i < size; i++) {
			if (!expr.isAST()) {
				break;
			}
			expr = ((IAST) expr).get(positions.get(i));
			if (i == (size - 1)) {
				return expr;
			}
		}
		return null;
	}

	/**
	 * Returns the value to which the specified property is mapped, or <code>null</code> if this map contains no mapping
	 * for the property.
	 * 
	 * @param key
	 * @return
	 * @see #putProperty(PROPERTY, Object)
	 */
	public Object getProperty(PROPERTY key) {
		if (IAST_CACHE != null) {
			EnumMap<PROPERTY, Object> map = IAST_CACHE.getIfPresent(this);
			if (map == null) {
				return null;
			}
			return map.get(key);
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean has(Predicate<IExpr> predicate, boolean heads) {
		if (predicate.test(this)) {
			return true;
		}
		return exists(x -> x.has(predicate, heads), heads ? 0 : 1);
	}

	public final boolean hasTrigonometricFunction() {
		return has(x -> {
			if (x.isAST1()) {
				final IExpr head = x.head();
				if (head.isBuiltInSymbol()) {
					return //
					(head == F.ArcCos) || //
					(head == F.ArcCsc) || //
					(head == F.ArcCot) || //
					(head == F.ArcSec) || //
					(head == F.ArcSin) || //
					(head == F.ArcTan) || //
					(head == F.Cos) || //
					(head == F.Csc) || //
					(head == F.Cot) || //
					(head == F.Sec) || //
					(head == F.Sin) || //
					(head == F.Sinc) || //
					(head == F.Tan) || //
					(head == F.Cosh) || //
					(head == F.Csch) || //
					(head == F.Coth) || //
					(head == F.Sech) || //
					(head == F.Sinh) || //
					(head == F.Tanh) || //
					(head == F.Haversine) || //
					(head == F.InverseHaversine);
				}
			}
			if (x.isAST2()) {
				return x.head() == F.ArcTan;
			}
			return false;
		}, false);

	}

	/**
	 * <p>
	 * FNV-1 hash code of this <code>IAST</code>.
	 * </p>
	 * 
	 * <p>
	 * See: <a href= "https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function#FNV-1_hash"> Wikipedia:
	 * Fowler–Noll–Vo hash function</a>
	 * </p>
	 * 
	 */
	@Override
	public int hashCode() {
		if (hashValue == 0) {
			hashValue = 0x811c9dc5;// decimal 2166136261;
			int size = size();
			for (int i = 0; i < size; i++) {
				hashValue = (hashValue * 16777619) ^ (get(i).hashCode() & 0xff);
			}
		}
		return hashValue;
	}

	/** {@inheritDoc} */
	@Override
	public int hierarchy() {
		return ASTID;
	}

	/** {@inheritDoc} */
	@Override
	public final int indexOf(final IExpr expr) {
		for (int i = 1; i < size(); i++) {
			if (equalsAt(i, expr)) {
				return i;
			}
		}
		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(Predicate<? super IExpr> predicate) {
		for (int i = 1; i < size(); i++) {
			if (predicate.test(get(i))) {
				return i;
			}
		}
		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr findFirst(Function<IExpr, IExpr> function) {
		for (int i = 1; i < size(); i++) {
			IExpr temp = function.apply(get(i));
			if (temp.isPresent()) {
				return temp;
			}
		}
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public final String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, false, false, false);
	}

	/** {@inheritDoc} */
	@Override
	public final String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators,
			boolean usePrefix, boolean noSymbolPrefix) {
		final String sep = ",";
		final IExpr temp = head();
		String prefix = usePrefix ? "F." : "";
		if (temp.equals(F.HoldForm) && size() == 2) {
			return arg1().internalJavaString(symbolsAsFactoryMethod, depth, useOperators, usePrefix, noSymbolPrefix);
		}
		if (temp.equals(F.Hold) && size() == 2) {
			return arg1().internalJavaString(symbolsAsFactoryMethod, depth, useOperators, usePrefix, noSymbolPrefix);
		}
		if (isInfinity()) {
			return prefix + "oo";
		}
		if (isNegativeInfinity()) {
			return prefix + "Noo";
		}
		if (isComplexInfinity()) {
			return prefix + "CComplexInfinity";
		}
		if (this.equals(F.Slot1)) {
			return prefix + "Slot1";
		}
		if (this.equals(F.Slot2)) {
			return prefix + "Slot2";
		}
		if (temp.equals(F.Inequality) && size() >= 4) {
			return BooleanFunctions.inequality2And(this).internalJavaString(symbolsAsFactoryMethod, depth, useOperators,
					usePrefix, noSymbolPrefix);
		}
		if (temp.equals(F.Rational) && size() == 3) {
			if (arg1().isInteger() && arg2().isInteger()) {
				return F.QQ((IInteger) arg1(), (IInteger) arg2()).internalJavaString(symbolsAsFactoryMethod, depth,
						useOperators, usePrefix, noSymbolPrefix);
			}
		}
		if (isPower()) {
			if (arg1().isInteger() && arg2().isMinusOne()) {
				IInteger i = (IInteger) arg1();
				if (i.equals(F.C2)) {
					return prefix + "C1D2";
				} else if (i.equals(F.C3)) {
					return prefix + "C1D3";
				} else if (i.equals(F.C4)) {
					return prefix + "C1D4";
				} else if (i.equals(F.CN2)) {
					return prefix + "CN1D2";
				} else if (i.equals(F.CN3)) {
					return prefix + "CN1D3";
				} else if (i.equals(F.CN4)) {
					return prefix + "CN1D4";
				}
			}
			if (equalsAt(1, F.E)) {
				return prefix + "Exp(" + arg2().internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators,
						usePrefix, noSymbolPrefix) + ")";
			}
			if (equalsAt(2, F.C1D2)) {
				if (base().isInteger()) {
					// square root of an integer number
					IInteger i = (IInteger) base();
					if (i.equals(F.C2)) {
						return prefix + "CSqrt2";
					} else if (i.equals(F.C3)) {
						return prefix + "CSqrt3";
					} else if (i.equals(F.C5)) {
						return prefix + "CSqrt5";
					} else if (i.equals(F.C6)) {
						return prefix + "CSqrt6";
					} else if (i.equals(F.C7)) {
						return prefix + "CSqrt7";
					} else if (i.equals(F.C10)) {
						return prefix + "CSqrt10";
					}
				}
				return prefix + "Sqrt(" + arg1().internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators,
						usePrefix, noSymbolPrefix) + ")";
			}
			if (equalsAt(2, F.C2)) {
				return prefix + "Sqr(" + arg1().internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators,
						usePrefix, noSymbolPrefix) + ")";
			}
			if (equalsAt(2, F.CN1D2) && arg1().isInteger()) {
				// negative square root of an integer number
				IInteger i = (IInteger) arg1();
				if (i.equals(F.C2)) {
					return prefix + "C1DSqrt2";
				} else if (i.equals(F.C3)) {
					return prefix + "C1DSqrt3";
				} else if (i.equals(F.C5)) {
					return prefix + "C1DSqrt5";
				} else if (i.equals(F.C6)) {
					return prefix + "C1DSqrt6";
				} else if (i.equals(F.C7)) {
					return prefix + "C1DSqrt7";
				} else if (i.equals(F.C10)) {
					return prefix + "C1DSqrt10";
				}
			}
			// don't optimize if arg2() is integer
		}
		StringBuilder text = new StringBuilder(size() * 10);
		if (temp.isSymbol()) {
			ISymbol sym = (ISymbol) temp;
			String name = null;
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				name = sym.toString();
				if (name.length() > 0) {
					name = name.toLowerCase(Locale.ENGLISH);
				}
				name = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(name);
			}
			if (name == null && !Character.isUpperCase(sym.toString().charAt(0))) {
				text.append(prefix + "$(");
				for (int i = 0; i < size(); i++) {
					text.append(get(i).internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators, usePrefix,
							noSymbolPrefix));
					if (i < argSize()) {
						text.append(sep);
					}
				}
				text.append(')');
				return text.toString();
			}
		} else if (temp.isPattern() || temp.isAST()) {
			text.append(prefix + "$(");
			for (int i = 0; i < size(); i++) {
				text.append(get(i).internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators, usePrefix,
						noSymbolPrefix));
				if (i < argSize()) {
					text.append(sep);
				}
			}
			text.append(')');
			return text.toString();
		}

		if (isAST(F.Times, 3)) {
			if (equals(F.CNPi)) {
				return prefix + "CNPi";
			} else if (equals(F.CN2Pi)) {
				return prefix + "CN2Pi";
			} else if (equals(F.C2Pi)) {
				return prefix + "C2Pi";
			} else if (equals(F.CNPiHalf)) {
				return prefix + "CNPiHalf";
			} else if (equals(F.CPiHalf)) {
				return prefix + "CPiHalf";
			}
			if (arg1().isMinusOne() && !arg2().isTimes()) {
				if (arg2().isNumber()) {
					IExpr num = ((INumber) arg2()).negate();
					return num.internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators, usePrefix,
							noSymbolPrefix);
				}
				return prefix + "Negate(" + arg2().internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators,
						usePrefix, noSymbolPrefix) + ")";
			}
		} else if (isAST(F.Plus, 3)) {
			if (arg2().isAST(F.Times, 3) && arg2().first().isMinusOne()) {
				return prefix + "Subtract("
						+ arg1().internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators, usePrefix,
								noSymbolPrefix)
						+ "," + arg2().second().internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators,
								usePrefix, noSymbolPrefix)
						+ ")";
			}
		}

		if (useOperators && size() == 3) {
			if (isTimes()) {
				IExpr arg1 = arg1();
				IExpr arg2 = arg2();
				boolean isLowerPrecedence = arg1.isPlus();
				internalOperatorForm(arg1, isLowerPrecedence, symbolsAsFactoryMethod, depth, useOperators, usePrefix,
						noSymbolPrefix, text);
				text.append('*');
				isLowerPrecedence = arg2.isPlus();
				internalOperatorForm(arg2, isLowerPrecedence, symbolsAsFactoryMethod, depth, useOperators, usePrefix,
						noSymbolPrefix, text);
				return text.toString();
			} else if (isPlus()) {
				IExpr arg1 = arg1();
				IExpr arg2 = arg2();
				internalOperatorForm(arg1, false, symbolsAsFactoryMethod, depth, useOperators, usePrefix,
						noSymbolPrefix, text);
				text.append('+');
				internalOperatorForm(arg2, false, symbolsAsFactoryMethod, depth, useOperators, usePrefix,
						noSymbolPrefix, text);
				return text.toString();
			} else if (isPower()) {
				IExpr arg1 = arg1();
				IExpr arg2 = arg2();
				boolean isLowerPrecedence = arg1.isTimes() || arg1.isPlus();
				internalOperatorForm(arg1, isLowerPrecedence, symbolsAsFactoryMethod, depth, useOperators, usePrefix,
						noSymbolPrefix, text);
				text.append('^');
				isLowerPrecedence = arg2.isTimes() || arg2.isPlus();
				internalOperatorForm(arg2, isLowerPrecedence, symbolsAsFactoryMethod, depth, useOperators, usePrefix,
						noSymbolPrefix, text);
				return text.toString();
			}

		}
		text.append(temp.internalJavaString(false, 0, useOperators, usePrefix, noSymbolPrefix));
		text.append('(');
		if (isTimes() || isPlus()) {
			if (depth == 0 && isList()) {
				text.append('\n');
			}
			internalFormOrderless(this, text, sep, symbolsAsFactoryMethod, depth, useOperators, usePrefix,
					noSymbolPrefix);
			if (depth == 0 && isList()) {
				text.append('\n');
			}
		} else {
			if (depth == 0 && isList()) {
				text.append('\n');
			}
			for (int i = 1; i < size(); i++) {
				text.append(get(i).internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators, usePrefix,
						noSymbolPrefix));
				if (i < argSize()) {
					text.append(sep);
					if (depth == 0 && isList()) {
						text.append('\n');
					}
				}
			}
			if (depth == 0 && isList()) {
				text.append('\n');
			}
		}
		text.append(')');
		return text.toString();
	}

	private void internalOperatorForm(IExpr arg1, boolean isLowerPrecedence, boolean symbolsAsFactoryMethod, int depth,
			boolean useOperators, boolean usePrefix, boolean noSymbolPrefix, StringBuilder text) {
		if (isLowerPrecedence) {
			text.append('(');
		}
		text.append(
				arg1.internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators, usePrefix, noSymbolPrefix));
		if (isLowerPrecedence) {
			text.append(')');
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, true, false, false);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAbs() {
		return isSameHead(F.Abs, 2);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAllExpanded() {
		if (isEvalFlagOff(IAST.IS_ALL_EXPANDED)) {
			return false;
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAlternatives() {
		return isSameHeadSizeGE(F.Alternatives, 1);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAnd() {
		return isSameHeadSizeGE(F.And, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcCos() {
		return isSameHead(F.ArcCos, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcCosh() {
		return isSameHead(F.ArcCosh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcSin() {
		return isSameHead(F.ArcSin, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcSinh() {
		return isSameHead(F.ArcSinh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcTan() {
		return isSameHead(F.ArcTan, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcTanh() {
		return isSameHead(F.ArcTanh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST(final IExpr header) {
		return head().equals(header);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST(final IExpr header, final int length) {
		return head().equals(header) && length == size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST(IExpr header, int length, IExpr... args) {
		if (isAST(header, length)) {
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null && !get(i + 1).equals(args[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAST(IExpr head, int minLength, int maxLength) {
		int size = size();
		return head().equals(head) && minLength <= size && maxLength >= size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isAST(final String symbol) {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			String name = symbol;
			if (name.length() > 0) {
				name = symbol.toLowerCase(Locale.ENGLISH);
			}
			String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(name);
			if (str != null) {
				name = str;
			}
			return head().toString().equals(name);
		}
		return head().toString().equals(symbol);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isAST(final String symbol, final int length) {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			String name = symbol;
			if (name.length() > 0) {
				name = symbol.toLowerCase(Locale.ENGLISH);
			}
			return (size() == length) && head().toString().equals(name);
		}
		return (size() == length) && head().toString().equals(symbol);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST0() {
		return size() == 1;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST1() {
		return size() == 2;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST2() {
		return size() == 3;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST3() {
		return size() == 4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isASTSizeGE(final IExpr header, final int length) {
		return head().equals(header) && length <= size();
	}

	@Override
	public final boolean isAtom() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isComplexInfinity() {
		return isSameHead(F.DirectedInfinity, 1);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isCondition() {
		return head() == F.Condition && size() == 3;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isConditionalExpression() {
		return head() == F.ConditionalExpression && size() == 3;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isConjugate() {
		return isSameHead(F.Conjugate, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isCos() {
		return isSameHead(F.Cos, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isCosh() {
		return isSameHead(F.Cosh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isDefer() {
		return isSameHead(F.Defer, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final IAST[] isDerivative() {
		if (head().isAST()) {
			IAST headAST = (IAST) head();
			if (headAST.isSameHeadSizeGE(F.Derivative, 2)) {
				IAST[] result = new IAST[3];
				result[0] = headAST;
				result[1] = this;
				return result;
			}

			if (headAST.head().isSameHeadSizeGE(F.Derivative, 2)) {
				if (this.size() != ((IAST) headAST.head()).size()) {
					return null;
				}
				IAST[] result = new IAST[3];
				result[0] = (IAST) headAST.head();
				result[1] = headAST;
				result[2] = this;
				return result;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST[] isDerivativeAST1() {
		if (head().isAST()) {
			IAST headAST = (IAST) head();
			if (headAST.isAST(F.Derivative, 2)) {
				IAST[] result = new IAST[3];
				result[0] = headAST;
				result[1] = this;
				return result;
			}

			if (headAST.head().isAST(F.Derivative, 2)) {
				if (this.size() != ((IAST) headAST.head()).size()) {
					return null;
				}
				IAST[] result = new IAST[3];
				result[0] = (IAST) headAST.head();
				result[1] = headAST;
				result[2] = this;
				return result;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isDirectedInfinity() {
		return isSameHead(F.DirectedInfinity, 1, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isDirectedInfinity(IExpr x) {
		return isSameHead(F.DirectedInfinity, 2) && arg1().equals(x);
	}

	public boolean isDiscreteDistribution() {
		if (head().isBuiltInSymbol()) {
			IEvaluator evaluator = ((IBuiltInSymbol) head()).getEvaluator();
			return evaluator instanceof IDiscreteDistribution;
		}
		return false;
	}

	public boolean isDistribution() {
		if (head().isBuiltInSymbol()) {
			IEvaluator evaluator = ((IBuiltInSymbol) head()).getEvaluator();
			return evaluator instanceof IDistribution;
		}
		return false;
	}

	@Override
	public final boolean isEmpty() {
		return size() == 1;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isEqual() {
		return isSameHead(F.Equal, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isEvalFlagOff(final int i) {
		return (fEvalFlags & i) == 0;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isEvalFlagOn(final int i) {
		return (fEvalFlags & i) == i;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isExcept() {
		return isAST(F.Except, 2, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isExpanded() {
		return !(isPlusTimesPower() && (isEvalFlagOff(IAST.IS_EXPANDED)));
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFlatAST() {
		return topHead().hasFlatAttribute();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFree(final IExpr pattern) {
		return isFree(pattern, true);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFree(final IExpr pattern, boolean heads) {
		if (pattern.isSymbol() || pattern.isNumber() || pattern.isString()) {
			return isFree(x -> x.equals(pattern), heads);
		}
		final IPatternMatcher matcher = new PatternMatcherEvalEngine(pattern, EvalEngine.get());
		return !has(matcher, heads);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFree(Predicate<IExpr> predicate, boolean heads) {
		return !has(predicate, heads);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFreeAST(final IExpr pattern) {
		final IPatternMatcher matcher = new PatternMatcherEvalEngine(pattern, EvalEngine.get());
		return isFreeAST(matcher);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFreeAST(Predicate<IExpr> predicate) {
		if (predicate.test(head())) {
			return false;
		}
		for (int i = 1; i < size(); i++) {
			if (get(i).isAST() && !get(i).isFreeAST(predicate)) {
				return false;
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFreeAt(int position, final IExpr pattern) {
		return get(position).isFree(pattern, true);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFreeOfPatterns() {
		final int evalFlags = getEvalFlags();
		if ((evalFlags & IAST.CONTAINS_NO_PATTERN) == IAST.CONTAINS_NO_PATTERN) {
			return true;
		}
		if ((evalFlags & IAST.CONTAINS_PATTERN_EXPR) != IAST.NO_FLAG) {
			return false;
		}

		if (isPatternMatchingFunction()) {
			addEvalFlags(IAST.CONTAINS_PATTERN);
			return false;
		}
		boolean isFreeOfPatterns = true;
		for (int i = 0; i < size(); i++) {
			// all elements including head element
			IExpr temp = get(i);
			if (temp.isAST() && !temp.isFreeOfPatterns()) {
				isFreeOfPatterns = false;
				addEvalFlags(((IAST) temp).getEvalFlags() & IAST.CONTAINS_PATTERN_EXPR);
				continue;
			} else if (temp instanceof IPatternObject) {
				isFreeOfPatterns = false;
				if (temp instanceof IPatternSequence) {
					if (temp.isPatternDefault()) {
						addEvalFlags(IAST.CONTAINS_DEFAULT_PATTERN);
					}
					addEvalFlags(IAST.CONTAINS_PATTERN_SEQUENCE);
				} else {
					if (temp.isPatternDefault()) {
						addEvalFlags(IAST.CONTAINS_DEFAULT_PATTERN);
					}
					addEvalFlags(IAST.CONTAINS_PATTERN);
				}

			}
		}
		if (isFreeOfPatterns) {
			addEvalFlags(IAST.CONTAINS_NO_PATTERN);
		}
		return isFreeOfPatterns;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isFunction() {
		return size() >= 2 && head().equals(F.Function);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isGEOrdered(final IExpr obj) {
		return compareTo(obj) >= 0;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isGTOrdered(final IExpr obj) {
		return compareTo(obj) > 0;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isHoldPatternOrLiteral() {
		return isSameHead(F.HoldPattern, 2) || isSameHead(F.Literal, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isInfinity() {
		return this.equals(F.CInfinity);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isBooleanFormula() {
		return head().isBooleanFormulaSymbol() && exists(x -> !x.isBooleanFormula());
	}

	/** {@inheritDoc} */
	@Override
	public boolean isBooleanResult() {
		return head().isPredicateFunctionSymbol() //
				|| ((head().isBooleanFormulaSymbol() || head().isComparatorFunctionSymbol()) //
						&& exists(x -> !x.isBooleanResult()));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isIntegerResult() {
		ISymbol symbol = topHead();
		if (symbol.equals(F.Floor) || symbol.equals(F.Ceiling) || symbol.equals(F.IntegerPart)) {
			return true;
		}
		if (isPower() && exponent().isInteger() && base().isPositive()) {
			if (base().isIntegerResult()) {
				return true;
			}
			return false;
		}
		if (isPlus() || isTimes() || symbol.equals(F.Binomial) || symbol.equals(F.Factorial)) {
			// TODO add more integer functions
			// check if all arguments are &quot;integer functions&quot;
			for (int i = 1; i < size(); i++) {
				if (get(i).isIntegerResult()) {
					continue;
				}
				return false;
			}
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isInterval() {
		if (isSameHeadSizeGE(F.Interval, 2)) {
			for (int i = 1; i < size(); i++) {
				if (!(get(i).isVector() == 2)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isInterval1() {
		return isSameHead(F.Interval, 2) && arg1().isAST(F.List, 3);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isList() {
		return isSameHeadSizeGE(F.List, 1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isList(Predicate<IExpr> pred) {
		if (head().equals(F.List)) {
			for (int i = 1; i < size(); i++) {
				if (!pred.test(get(i))) {
					// the row is no list
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isListOfLists() {
		if (head().equals(F.List)) {
			for (int i = 1; i < size(); i++) {
				if (!get(i).isList()) {
					// the row is no list
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public GraphType isListOfEdges() {
		if (head().equals(F.List)) {
			boolean directed = true;
			for (int i = 1; i < size(); i++) {
				IExpr temp = get(i);
				if (temp.isAST2() && temp.head().isBuiltInSymbol()) {
					IBuiltInSymbol symbol = (IBuiltInSymbol) temp.head();
					if (symbol == F.DirectedEdge || symbol == F.Rule) {
						continue;
					}
					if (!(symbol == F.UndirectedEdge || symbol == F.TwoWayRule)) {
						// the row is no list of edges
						return null;
					}
					directed = false;
				} else {
					return null;
				}
			}

			Builder builder = new DefaultGraphType.Builder();
			if (directed) {
				return builder.directed().build();
			}
			return builder.undirected().build();
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEdge() {
		if (isAST2() && head().isBuiltInSymbol()) {
			IBuiltInSymbol symbol = (IBuiltInSymbol) head();
			return (symbol == F.DirectedEdge || symbol == F.UndirectedEdge || symbol == F.Rule
					|| symbol == F.TwoWayRule);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isListOfRules() {
		if (head().equals(F.List)) {
			for (int i = 1; i < size(); i++) {
				if (!get(i).isRuleAST()) {
					// the row is no list
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isLog() {
		return isSameHead(F.Log, 2);
	}

	/** {@inheritDoc} */
	@Override
	public int[] isMatrix(boolean setMatrixFormat) {
		if (isEvalFlagOn(IAST.IS_MATRIX)) {
			final int[] dim = new int[2];
			dim[0] = argSize();
			dim[1] = ((IAST) first()).argSize();
			return dim;
		}
		if (isList()) {
			final int[] dim = new int[2];
			dim[0] = argSize();
			if (dim[0] > 0) {
				dim[1] = 0;
				if (arg1().isList()) {
					dim[1] = ((IAST) arg1()).argSize();
					for (int i = 2; i < size(); i++) {
						if (!get(i).isList()) {
							// this row is not a list
							return null;
						}
						if (dim[1] != ((IAST) get(i)).argSize()) {
							// this row has another dimension
							return null;
						}
					}
					if (setMatrixFormat) {
						addEvalFlags(IAST.IS_MATRIX);
					}
					return dim;
				}
			}

		}
		return null;
	}

	public boolean isMember(IExpr pattern, boolean heads, IVisitorBoolean visitor) {
		if (visitor != null) {
			return IASTMutable.super.isMember(pattern, heads, visitor);
		}
		Predicate<IExpr> predicate;
		if (pattern.isSymbol() || pattern.isNumber() || pattern.isString()) {
			predicate = x -> x.equals(pattern);
		} else {
			predicate = new PatternMatcher(pattern);
		}
		return exists(predicate, heads ? 0 : 1);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isModule() {
		return head() == F.Module && size() == 3;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isModuleOrWithCondition() {
		return size() == 3 && (head().equals(F.With) || head().equals(F.Module)) && get(2).isCondition();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		if (isNumericFunction()) {

			IExpr result = EvalEngine.get().evalN(this);
			if (result.isReal()) {
				return result.isNegative();
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegativeInfinity() {
		return this.equals(F.CNInfinity);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegativeResult() {
		if (isDirectedInfinity()) {
			if (isNegativeInfinity()) {
				return true;
			}
			return false;
		}
		return AbstractAssumptions.isNegativeResult(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNonNegativeResult() {
		if (isDirectedInfinity()) {
			if (isInfinity()) {
				return true;
			}
			return false;
		}
		return AbstractAssumptions.isNonNegativeResult(this);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNot() {
		return size() == 2 && head().equals(F.Not);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumericArgument() {
		if (isEvalFlagOn(IAST.CONTAINS_NUMERIC_ARG)) {
			return forAll(x -> x.isNumericFunction() || //
					(x.isList() && ((IAST) x).forAll(y -> y.isNumericFunction())));
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumericFunction() {
		if (head().isSymbol() && ((ISymbol) head()).isNumericFunctionAttribute() || isList()) {
			// check if all arguments are &quot;numeric&quot;
			return forAll(x -> x.isNumericFunction());
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumericMode() {
		ISymbol symbol = topHead();
		if (isList() || symbol.isNumericFunctionAttribute()) {
			// check if one of the arguments is &quot;numeric&quot;
			for (int i = 1; i < size(); i++) {
				if (get(i).isNumericMode()) {
					return true;
				}
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOneIdentityAST1() {
		return isAST1() && topHead().hasOneIdentityAttribute();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isOptional() {
		return isAST(F.Optional, 2, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isOr() {
		return isSameHeadSizeGE(F.Or, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isOrderlessAST() {
		return topHead().hasOrderlessAttribute();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPatternDefault() {
		return isOptional();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPatternExpr() {
		return (fEvalFlags & CONTAINS_PATTERN_EXPR) != NO_FLAG;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPatternTest() {
		return isAST(F.PatternTest, 3);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPlus() {
		return head() == F.Plus && 3 <= size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPlusTimesPower() {
		final IExpr h = head();
		if (h instanceof IBuiltInSymbol) {
			if (4 <= size()) {
				return h == F.Plus || h == F.Times;
			}
			if (3 == size()) {
				return h == F.Plus || h == F.Times || h == F.Power;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPolynomial(IAST variables) {
		if (isPlus() || isTimes() || isPower()) {
			IExpr expr = F.evalExpandAll(this);
			ExprPolynomialRing ring = new ExprPolynomialRing(variables);
			return ring.isPolynomial(expr);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPolynomial(IExpr variable) {
		return isPolynomial(F.List(variable));
	}

	public final boolean isPolynomialOfMaxDegree(IAST variables, long maxDegree) {
		try {
			if (isPlus() || isTimes() || isPower()) {
				IExpr expr = F.evalExpandAll(this);
				ExprPolynomialRing ring = new ExprPolynomialRing(variables);
				ExprPolynomial poly = ring.create(expr);
				return poly.degree() <= maxDegree;
			}
		} catch (RuntimeException ex) {
			//
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPolynomialOfMaxDegree(ISymbol variable, long maxDegree) {
		return isPolynomialOfMaxDegree(F.List(variable), maxDegree);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		if (isNumericFunction()) {
			IExpr result = EvalEngine.get().evalN(this);
			if (result.isReal()) {
				return ((ISignedNumber) result).isPositive();
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPositiveResult() {
		if (isDirectedInfinity()) {
			if (isInfinity()) {
				return true;
			}
			return false;
		}
		return AbstractAssumptions.isPositiveResult(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPower() {
		return isSameHead(F.Power, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isRationalResult() {
		ISymbol symbol = topHead();
		if (symbol.equals(F.Floor) || symbol.equals(F.Ceiling) || symbol.equals(F.IntegerPart)) {
			return true;
		}
		if (isPower() && arg2().isInteger() && arg2().isPositive()) {
			if (arg1().isRationalResult()) {
				return true;
			}
			return false;
		}
		if (isPlus() || isTimes() || symbol.equals(F.Binomial) || symbol.equals(F.Factorial)) {
			// TODO add more functions
			// check if all arguments are &quot;rational functions&quot;
			for (int i = 1; i < size(); i++) {
				if (get(i).isRationalResult()) {
					continue;
				}
				return false;
			}
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRealMatrix() {
		if (isList()) {
			final int[] dim = new int[2];
			dim[0] = argSize();
			if (dim[0] > 0) {
				dim[1] = 0;
				if (arg1().isList()) {
					IAST row = (IAST) arg1();
					dim[1] = row.argSize();
					boolean containsNum = false;
					for (int j = 1; j < row.size(); j++) {
						if (row.get(j).isReal()) {
							if (row.get(j) instanceof INum) {
								if (!(row.get(j) instanceof Num)) {
									// Apfloat number
									return false;
								}
								containsNum = true;
							}
						} else {
							return false;
						}
					}

					for (int i = 2; i < size(); i++) {
						if (!get(i).isList()) {
							// this row is not a list
							return false;
						}
						row = (IAST) get(i);
						if (dim[1] != row.argSize()) {
							// this row has another dimension
							return false;
						}
						for (int j = 1; j < row.size(); j++) {
							if (row.get(j).isReal()) {
								if (row.get(j) instanceof INum) {
									if (!(row.get(j) instanceof Num)) {
										// Apfloat number
										return false;
									}
									containsNum = true;
								}
							} else {
								return false;
							}
						}
					}
					addEvalFlags(IAST.IS_MATRIX);
					return containsNum;
				}
			}

		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRealResult() {
		IExpr head = head();
		if (size() == 2 && F.Cos.equals(head) && F.Sin.equals(head)) {
			// TODO add more functions
			return arg1().isRealResult();
		}
		ISignedNumber e = evalReal();
		if (e != null) {
			return true;
		}
		if (isPlus() || isTimes()) {
			// check if all arguments are &quot;real values&quot;
			for (int i = 1; i < size(); i++) {
				if (get(i).isRealResult()) {
					continue;
				}
				return false;
			}
			return true;
		}
		if (isPower() && (!exponent().isZero() || !base().isZero())) {
			if (!arg1().isRealResult()) {
				return false;
			}
			if (arg1().isNegativeResult()) {
				return false;
			}
			if (!arg2().isRealResult()) {
				return false;
			}
			return true;
		}
		if (isInfinity() || isNegativeInfinity()) {
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRealVector() {
		if (isList()) {
			boolean containsNum = false;
			for (int i = 1; i < size(); i++) {
				if (get(i).isReal()) {
					if (get(i) instanceof INum) {
						if (!(get(i) instanceof Num)) {
							return false;
						}
						containsNum = true;
					}
				} else {
					return false;
				}
			}
			return containsNum;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isRule() {
		return head().equals(F.Rule) && size() == 3;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isRuleAST() {
		return (head().equals(F.Rule) || head().equals(F.RuleDelayed)) && size() == 3;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isRuleDelayed() {
		return head().equals(F.RuleDelayed) && size() == 3;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSame(IExpr expression) {
		return equals(expression);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSame(IExpr expression, double epsilon) {
		return equals(expression);
	}

	/**
	 * Check if the object at index 0 (i.e. the head of the list) is the same object as <code>head</code>
	 * 
	 * @param head
	 *            object to compare with element at location <code>0</code>
	 * @return
	 */
	public boolean isSameHead(ISymbol head) {
		return head() == head;
	}

	/**
	 * Check if the object at index 0 (i.e. the head of the list) is the same object as <code>head</code> and if the
	 * size of the list equals <code>length</code>.
	 * 
	 * @param head
	 *            object to compare with element at location <code>0</code>
	 * @param length
	 * @return
	 */
	public boolean isSameHead(ISymbol head, int length) {
		return head() == head && length == size();
	}

	/**
	 * Check if the object at index 0 (i.e. the head of the list) is the same object as <code>head</code> and if the
	 * size of the list is between <code>minLength</code> and <code>maxLength</code>.
	 * 
	 * @param head
	 *            object to compare with element at location <code>0</code>
	 * @param minLength
	 *            minimum length of list elements.
	 * @param maxLength
	 *            maximum length of list elements.
	 * @return
	 */
	public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
		int size = size();
		return head().equals(head) && minLength <= size && maxLength >= size;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSequence() {
		return isSameHeadSizeGE(F.Sequence, 1);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSin() {
		return isSameHead(F.Sin, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSinh() {
		return isSameHead(F.Sinh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSlot() {
		return isSameHead(F.Slot, 2) && arg1().isInteger();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSlotSequence() {
		return isSameHead(F.SlotSequence, 2) && arg1().isInteger();
	}

	/** {@inheritDoc} */
	@Override
	public final int[] isSpan(int size) {
		int[] result = null;
		if (isSameHead(F.Span, 3, 4)) {
			int step = 1;
			if (isAST3()) {
				step = Validate.checkIntType(this, 3, Integer.MIN_VALUE);
			}
			int index1 = Validate.checkIntType(this, 1, Integer.MIN_VALUE);
			int index2;
			if (arg2().equals(F.All)) {
				index2 = size - 1;
				if (step < 0) {
					int tempIndx = index1;
					index1 = index2;
					index2 = tempIndx;
				}
			} else {
				index2 = Validate.checkIntType(this, 2, Integer.MIN_VALUE);
			}

			int start = index1;
			if (index1 < 0) {
				start = size + index1;
			}
			int last = index2;
			if (index2 < 0) {
				last = size + index2;
			}
			result = new int[3];
			result[0] = start;
			result[1] = last;
			result[2] = step;
			return result;
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isTan() {
		return isSameHead(F.Tan, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isTanh() {
		return isSameHead(F.Tanh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTimes() {
		return head() == F.Times && 3 <= size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTrigFunction() {
		int id = headID();
		if (id >= 0) {
			if (size() == 2) {
				return id == ID.Cos || id == ID.ArcCos || //
						id == ID.Cot || id == ID.ArcCot || //
						id == ID.Csc || id == ID.ArcCsc || //
						id == ID.Sec || id == ID.ArcSec || //
						id == ID.Sin || id == ID.ArcSin || //
						id == ID.Tan || id == ID.ArcTan;
			}
			if (size() == 3) {
				return id == ID.ArcTan;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isHyperbolicFunction() {
		int id = headID();
		if (id >= 0) {
			if (size() == 2) {
				return id == ID.Cosh || id == ID.ArcCosh || //
						id == ID.Coth || id == ID.ArcCoth || //
						id == ID.Csch || id == ID.ArcCsch || //
						id == ID.Sech || id == ID.ArcSech || //
						id == ID.Sinh || id == ID.ArcSinh || //
						id == ID.Tanh || id == ID.ArcTanh;
			}
		}
		return false;
	}

	@Override
	public final boolean isPatternMatchingFunction() {
		final int id = headID();
		if (id >= ID.Alternatives && id <= ID.Rational) {
			if (size() >= 2) {
				return id == ID.HoldPattern || id == ID.Literal || id == ID.Condition || id == ID.Alternatives || //
						id == ID.Except || id == ID.Complex || id == ID.Rational || id == ID.Optional || //
						id == ID.PatternTest;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isUnit() {
		if (isZero()) {
			return false;
		}
		if (isNumber()) {
			return true;
		}
		if (isConstantAttribute()) {
			return true;
		}
		IExpr temp = F.eval(F.Times(this, F.Power(this, F.CN1)));
		if (temp.isOne()) {
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isValue() {
		EvalEngine engine = EvalEngine.get();
		ISymbol symbol = topHead();
		IExpr result = engine.evalAttributes(symbol, this);
		if (result.isPresent()) {
			if (result.isAST(symbol)) {
				return engine.evalRules(symbol, (IAST) result).isPresent();
			}
			return false;
		}
		return engine.evalRules(symbol, this).isPresent();
	}

	/** {@inheritDoc} */
	@Override
	public int isVector() {
		if (isEvalFlagOn(IAST.IS_VECTOR)) {
			return argSize();
		}
		if (isList()) {
			final int dim = argSize();
			if (dim > 0) {
				if (arg1().isList()) {
					return -1;
				}
				for (int i = 2; i < size(); i++) {
					if (get(i).isList()) {
						// row is a list
						return -1;
					}
				}
			}
			addEvalFlags(IAST.IS_VECTOR);
			return dim;
		}
		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isWith() {
		return head() == F.With && size() == 3;
	}

	/**
	 * Returns an iterator over the elements in this <code>IAST</code> starting with offset <b>1</b>.
	 * 
	 * @return an iterator over this <code>IAST</code>s argument values from <code>1..(size-1)</code>.
	 */
	@Override
	public final Iterator<IExpr> iterator() {
		ASTIterator i = new ASTIterator();
		i._table = this;
		i._start = 1;
		i._end = this.size();
		i._nextIndex = 1;
		i._currentIndex = 0;
		return i;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr last() {
		return get(argSize());
	}

	@Override
	public final int lastIndexOf(IExpr object) {
		int size = size();
		for (int i = size - 1; i >= 0; i--) {
			if (object.equals(get(i))) {
				return i;
			}
		}
		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public final long leafCount() {
		return accept(new LeafCount.LeafCountVisitor(0));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * use {@link #isZero()} instead.
	 */
	// @Deprecated
	// @Override
	// public boolean isZERO() {
	// return isZero();
	// }

	@Override
	public long leafCountSimplify() {
		long count = 0L;
		for (int i = 0; i < size(); i++) {
			count += get(i).leafCountSimplify();
		}
		return count;
		// return accept(new LeafCount.SimplifyLeafCountVisitor(0));
	}

	@Override
	public IExpr[] linear(IExpr variable) {
		int size = size();
		int counter = 0;

		if (isPlus()) {
			// a + b + c....
			IASTAppendable plusClone = copyAppendable();
			IExpr[] subLinear = null;
			int j = 1;
			for (int i = 1; i < size; i++) {
				if (get(i).isFree(variable, true)) {
					j++;
				} else {
					if (counter > 0 || get(i).isPlus()) {
						return null;
					}
					subLinear = get(i).linear(variable);
					if (subLinear != null) {
						counter++;
						plusClone.remove(j);
					} else {
						return null;
					}
				}
			}
			if (subLinear != null) {
				return new IExpr[] { plusClone.oneIdentity0(), subLinear[1] };
			}
			return new IExpr[] { plusClone.oneIdentity0(), F.C0 };
		} else if (isTimes()) {
			// a * b * c....
			IASTAppendable timesClone = copyAppendable();
			int j = 1;
			for (int i = 1; i < size; i++) {
				if (get(i).isFree(variable, true)) {
					j++;
				} else {
					if (get(i).equals(variable)) {
						if (counter > 0) {
							return null;
						}
						counter++;
						timesClone.remove(j);
					} else {
						return null;
					}
				}
			}
			return new IExpr[] { F.C0, timesClone.oneIdentity1() };
		} else if (this.equals(variable)) {
			return new IExpr[] { F.C0, F.C1 };
		} else if (isFree(variable, true)) {
			return new IExpr[] { this, F.C0 };
		}
		return null;
	}

	@Override
	public IExpr[] linearPower(IExpr variable) {
		int size = size();
		int counter = 0;

		if (isPlus()) {
			// a + b + c....
			IASTAppendable plusClone = copyAppendable();
			IExpr[] subLinear = null;
			int j = 1;
			for (int i = 1; i < size; i++) {
				if (get(i).isFree(variable, true)) {
					j++;
				} else {
					if (counter > 0 || get(i).isPlus()) {
						return null;
					}
					subLinear = get(i).linearPower(variable);
					if (subLinear != null) {
						counter++;
						plusClone.remove(j);
					} else {
						return null;
					}
				}
			}
			if (subLinear != null) {
				return new IExpr[] { plusClone.oneIdentity0(), subLinear[1], subLinear[2] };
			}
			return new IExpr[] { plusClone.oneIdentity0(), F.C0, F.C1 };
		} else if (isTimes()) {
			IInteger exp = F.C1;
			// a * b * c....
			IASTAppendable timesClone = copyAppendable();
			int j = 1;
			for (int i = 1; i < size; i++) {
				if (get(i).isFree(variable, true)) {
					j++;
				} else {
					if (get(i).equals(variable)) {
						if (counter > 0) {
							return null;
						}
						counter++;
						timesClone.remove(j);
						continue;
					} else if (get(i).isPower()) {
						if (counter > 0) {
							return null;
						}
						IAST power = (IAST) get(i);
						if (power.base().equals(variable) && power.exponent().isInteger()) {
							exp = (IInteger) power.exponent();
							counter++;
							timesClone.remove(j);
							continue;
						}
					}
					return null;
				}
			}
			return new IExpr[] { F.C0, timesClone.oneIdentity1(), exp };
		} else if (isPower() && base().equals(variable) && exponent().isInteger()) {
			return new IExpr[] { F.C0, F.C1, exponent() };
		} else if (this.equals(variable)) {
			return new IExpr[] { F.C0, F.C1, F.C1 };
		} else if (isFree(variable, true)) {
			return new IExpr[] { this, F.C0, F.C1 };
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr lower() {
		if (isInterval1()) {
			return ((IAST) arg1()).arg1();
		}
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public IAST map(final Function<IExpr, IExpr> function, final int startOffset) {
		IExpr temp;
		IASTMutable result = F.NIL;
		int i = startOffset;
		int size = size();
		while (i < size) {
			temp = function.apply(get(i));
			if (temp.isPresent()) {
				// something was evaluated - return a new IAST:
				result = copy();
				result.set(i++, temp);
				break;
			}
			i++;
		}
		if (result.isPresent()) {
			while (i < size) {
				temp = function.apply(get(i));
				if (temp.isPresent()) {
					result.set(i, temp);
				}
				i++;
			}
		}
		return (IAST) result.orElse(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IAST map(IASTAppendable resultAST, IAST secondAST, BiFunction<IExpr, IExpr, IExpr> function) {
		int size = size();
		for (int i = 1; i < size; i++) {
			resultAST.append(function.apply(get(i), secondAST.get(i)));
		}
		return resultAST;
	}

	/**
	 * Append the mapped ranges elements directly to the given <code>list</code>
	 * 
	 * @param astResult
	 * @param function
	 * @return
	 */
	public IASTAppendable map(IASTAppendable astResult, IUnaryIndexFunction<IExpr, IExpr> function) {
		for (int i = 1; i < size(); i++) {
			astResult.append(function.apply(i, get(i)));
		}
		return astResult;
	}

	/** {@inheritDoc} */
	@Override
	public IAST map(final IASTMutable clonedResultAST, final Function<IExpr, IExpr> function) {
		IExpr temp;
		int size = size();
		for (int i = 1; i < size; i++) {
			temp = function.apply(get(i));
			if (temp != null) {
				clonedResultAST.set(i, temp);
			}
		}
		return clonedResultAST;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST map(final IExpr head, final Function<IExpr, IExpr> function) {
		return map(setAtCopy(0, head), function);
	}

	/**
	 * Append the mapped ranges elements directly to the given <code>list</code>
	 * 
	 * @param list
	 * @param binaryFunction
	 *            binary function
	 * @param leftArg
	 *            left argument of the binary functions <code>apply()</code> method.
	 * @return
	 */
	public IAST mapLeft(IASTAppendable list, BiFunction<IExpr, IExpr, IExpr> binaryFunction, IExpr leftArg) {
		for (int i = 1; i < size(); i++) {
			list.append(binaryFunction.apply(leftArg, get(i)));
		}
		return list;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr mapMatrixColumns(int[] dim, Function<IExpr, IExpr> f) {
		final int rowSize = size();
		int columnSize = dim[1];
		IASTAppendable result = F.ListAlloc(columnSize++);
		for (int j = 1; j < columnSize; j++) {
			IASTAppendable row = F.ListAlloc(rowSize);
			for (int i = 1; i < rowSize; i++) {
				row.append(getPart(i, j));
			}
			result.append(f.apply(row));
		}
		return result;
	}

	/**
	 * Append the mapped ranges elements directly to the given <code>list</code>
	 * 
	 * @param list
	 * @param binaryFunction
	 *            a binary function
	 * @param rightArg
	 *            right argument of the binary functions <code>apply()</code> method.
	 * @return the given list
	 */
	public Collection<IExpr> mapRight(Collection<IExpr> list, BiFunction<IExpr, IExpr, IExpr> binaryFunction,
			IExpr rightArg) {
		for (int i = 1; i < size(); i++) {
			list.add(binaryFunction.apply(get(i), rightArg));
		}
		return list;
	}

	/** {@inheritDoc} */
	@Override
	public final IASTMutable mapThread(final IAST replacement, int position) {
		EvalEngine engine = EvalEngine.get();
		final Function<IExpr, IExpr> function = x -> engine.evaluate(replacement.setAtCopy(position, x));
		return (IASTMutable) map(function, 1);
	}

	/** {@inheritDoc} */
	@Override
	public IASTAppendable mapThread(IASTAppendable appendAST, final IAST replacement, int position) {
		EvalEngine engine = EvalEngine.get();
		final Function<IExpr, IExpr> function = x -> engine.evaluate(replacement.setAtCopy(position, x));

		IExpr temp;
		for (int i = 1; i < size(); i++) {
			temp = function.apply(get(i));
			if (temp != null) {
				appendAST.append(temp);
			}
		}
		return appendAST;
	}

	/**
	 * Additional <code>negative</code> method, which works like opposite to fulfill groovy's method signature
	 * 
	 * @return
	 */
	@Override
	public final IExpr negative() {
		return opposite();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr oneIdentity(IExpr defaultValue) {
		if (size() > 2) {
			return this;
		}
		if (size() == 2) {
			return arg1();
		}
		return defaultValue;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr opposite() {
		final int lhsOrdinal = (head() instanceof IBuiltInSymbol) ? ((IBuiltInSymbol) head()).ordinal() : -1;
		if (lhsOrdinal > 0) {
			if (isTimes()) {
				IExpr arg1 = arg1();
				if (arg1.isNumber()) {
					if (arg1.isMinusOne()) {
						if (size() == 3) {
							return arg2();
						}
						return rest();
					}
					return setAtCopy(1, ((INumber) arg1).negate());
				}
				IASTAppendable timesAST = copyAppendable();
				timesAST.append(1, F.CN1);
				return timesAST;
			}
			if (isNegativeInfinity()) {
				return F.CInfinity;
			}
			if (isInfinity()) {
				return F.CNInfinity;
			}
			if (isPlus()) {
				return map(x -> x.negate(), 1);
			}
		}
		return F.Times(F.CN1, this);
		// return F.eval(F.Times(F.CN1, this));
	}

	@Override
	public final IExpr or(final IExpr that) {
		return F.Or(this, that);
	}

	public IAST orElse(final IAST other) {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST partition(ISymbol operator, Predicate<? super IExpr> predicate, IExpr init1, IExpr init2,
			ISymbol combiner, ISymbol action) {
		if (head().equals(operator)) {
			IASTAppendable result = F.ast(action, 3, false);
			final int size = size();
			int newSize = size / 2;
			if (newSize <= 4) {
				newSize = 5;
			} else {
				newSize += 4;
			}
			IASTAppendable yesAST = F.ast(combiner, newSize, false);
			IASTAppendable noAST = F.ast(combiner, newSize, false);
			forEach(size, x -> {
				if (predicate.test(x)) {
					yesAST.append(x);
				} else {
					noAST.append(x);
				}
			});
			if (yesAST.size() > 1) {
				result.append(F.eval(yesAST));
			} else {
				result.append(init1);
			}
			if (noAST.size() > 1) {
				result.append(F.eval(noAST));
			} else {
				result.append(init2);
			}
			return result;
		}
		return F.NIL;
	}

	@Override
	public final IAST partitionPlus(Predicate<? super IExpr> predicate, IExpr initYes, IExpr initNo, ISymbol action) {
		return partition(F.Plus, predicate, initYes, initNo, F.Plus, F.List);
	}

	@Override
	public final IAST partitionTimes(Predicate<? super IExpr> predicate, IExpr initYes, IExpr initNo, ISymbol action) {
		return partition(F.Times, predicate, initYes, initNo, F.Times, F.List);
	}

	/**
	 * Calculate a special hash value to find a matching rule in a hash table
	 * 
	 */
	@Override
	public final int patternHashCode() {
		if (size() > 1) {
			final int attr = topHead().getAttributes() & ISymbol.FLATORDERLESS;
			if (attr != ISymbol.NOATTRIBUTE) {
				if (attr == ISymbol.FLATORDERLESS) {
					return 17 * head().hashCode();
				} else if (attr == ISymbol.FLAT) {
					if (arg1() instanceof IAST) {
						return 31 * head().hashCode() + ((IAST) arg1()).head().hashCode();
					}
					return 37 * head().hashCode() + arg1().hashCode();
				}
				return 17 * head().hashCode() + size();
			}
			if (arg1() instanceof IAST) {
				return 31 * head().hashCode() + ((IAST) arg1()).head().hashCode() + size();
			}
			return 37 * head().hashCode() + arg1().hashCode() + size();
		}
		if (size() == 1) {
			return 17 * head().hashCode();
		}
		// this case shouldn't happen
		return 41;
	}

	/** {@inheritDoc} */
	@Override
	public final IASTAppendable prependClone(IExpr expr) {
		return appendAtClone(1, expr);
	}

	/**
	 * Associates the specified value with the specified property in the associated
	 * <code>EnumMap<PROPERTY, Object></code> map. If the map previously contained a mapping for this key, the old value
	 * is replaced.
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @see #getProperty(PROPERTY)
	 */
	public Object putProperty(PROPERTY key, Object value) {
		if (IAST_CACHE == null) {
			IAST_CACHE = CacheBuilder.newBuilder().maximumSize(500).build();
		}
		EnumMap<PROPERTY, Object> map = IAST_CACHE.getIfPresent(this);
		if (map == null) {
			map = new EnumMap<PROPERTY, Object>(PROPERTY.class);
			IAST_CACHE.put(this, map);
		}
		return map.put(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public IASTAppendable remove(Predicate<? super IExpr> predicate) {
		int indx = 1;
		while (indx < size()) {
			if (predicate.test(get(indx))) {
				IASTAppendable removed = removeAtClone(indx);
				while (indx < removed.size()) {
					if (predicate.test(removed.get(indx))) {
						removed.remove(indx);
						continue;
					}
					indx++;
				}
				return removed;
			} else {
				indx++;
			}
		}
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public final IASTAppendable removeAtClone(int position) {
		IASTAppendable ast = copyAppendable();
		ast.remove(position);
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public IAST splice(int index, int howMany, IExpr... items) {
		IASTAppendable ast = copyAppendable();
		if (howMany > 0) {
			ast.removeRange(index, index + howMany);
		}
		for (int i = 0; i < items.length; i++) {
			ast.append(index++, items[i]);
		}
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public final IASTMutable removeAtCopy(int position) {
		int size = size();
		if (position < size) {
			switch (size) {
			case 2:
				switch (position) {
				case 0:
					return F.headAST0(arg1());
				case 1:
					return F.headAST0(head());
				}
			case 3:
				switch (position) {
				case 0:
					return F.unaryAST1(arg1(), arg2());
				case 1:
					return F.unaryAST1(head(), arg2());
				case 2:
					return F.unaryAST1(head(), arg1());
				}
			case 4:
				switch (position) {
				case 0:
					return F.binaryAST2(arg1(), arg2(), arg3());
				case 1:
					return F.binaryAST2(head(), arg2(), arg3());
				case 2:
					return F.binaryAST2(head(), arg1(), arg3());
				case 3:
					return F.binaryAST2(head(), arg1(), arg2());
				}
			}
		}
		IASTAppendable ast = copyAppendable();
		ast.remove(position);
		return ast;
	}

	/**
	 * Append the elements in reversed order to the given <code>list</code>
	 * 
	 * @param list
	 * @return
	 */
	public IASTAppendable reverse(IASTAppendable list) {
		for (int i = argSize(); i >= 1; i--) {
			list.append(get(i));
		}
		return list;
	}

	public IExpr rewrite(int functionID) {
		int headID = headID();
		if (headID > 0) {
			IEvaluator evaluator = ((IBuiltInSymbol) head()).getEvaluator();
			if (evaluator instanceof IRewrite) {
				return ((IRewrite) evaluator).rewrite(this, EvalEngine.get(), functionID);
			}
		}
		return F.NIL;
	}

	/**
	 * Rotate the ranges elements to the left by n places and append the resulting elements to the <code>list</code>
	 * 
	 * @param list
	 * @param n
	 * @return the given list
	 */
	public IAST rotateLeft(IASTAppendable list, final int n) {
		int size = size();
		int n1 = n + 1;
		for (int i = n1; i < size; i++) {
			list.append(get(i));
		}
		if (n <= size) {
			for (int i = 1; i < n1; i++) {
				list.append(get(i));
			}
		}
		return list;
	}

	/**
	 * Rotate the ranges elements to the right by n places and append the resulting elements to the <code>list</code>
	 * 
	 * @param list
	 * @param n
	 * @return the given list
	 */
	public IAST rotateRight(IASTAppendable list, final int n) {
		if (n <= size()) {
			for (int i = size() - n; i < size(); i++) {
				list.append(get(i));
			}
			for (int i = 1; i < size() - n; i++) {
				list.append(get(i));
			}
		}
		return list;
	}

	/** {@inheritDoc} */
	@Override
	public final IASTAppendable setAtClone(int position, IExpr expr) {
		IASTAppendable ast = copyAppendable();
		ast.set(position, expr);
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public final void setEvalFlags(final int i) {
		fEvalFlags = i;
	}

	@Override
	public IExpr setPart(IExpr value, final int... positions) {
		IExpr expr = this;
		int size = positions.length;
		for (int i = 0; i < size; i++) {
			if (!expr.isAST()) {
				break;
			}
			IASTMutable ast = (IASTMutable) expr;
			expr = ast.get(positions[i]);
			if (i == (size - 1)) {
				ast.set(positions[i], value);
				return expr;
			}
		}
		return null;
	}

	/**
	 * Signum functionality is used in JAS toString() method, don't use it as math signum function.
	 * 
	 * @deprecated
	 */
	@Deprecated
	@Override
	public final int signum() {
		if (isTimes()) {
			IExpr temp = arg1();
			if (temp.isReal() && temp.isNegative()) {
				return -1;
			}
		}
		return 1;
	}

	@Override
	public Stream<IExpr> stream() {
		return Arrays.stream(toArray(), 1, size());
	}

	@Override
	public Stream<IExpr> stream(int startInclusive, int endExclusive) {
		return Arrays.stream(toArray(), startInclusive, endExclusive);
	}

	@Override
	public final IExpr timesDistributed(final IExpr that) {
		if (that.isZero()) {
			return F.C0;
		}
		if (this.isPlus()) {
			IAST plus = this.map(x -> x.times(that), 1);
			return F.eval(plus);
		}
		return F.eval(F.Times(this, that));
	}

	/** {@inheritDoc} */
	@Override
	public double[][] toDoubleMatrix() {
		int[] dim = isMatrix();
		if (dim == null) {
			return null;
		}
		double[][] result = new double[dim[0]][dim[1]];
		ISignedNumber signedNumber;
		for (int i = 1; i <= dim[0]; i++) {
			IAST row = (IAST) get(i);
			for (int j = 1; j <= dim[1]; j++) {
				signedNumber = row.get(j).evalReal();
				if (signedNumber != null) {
					result[i - 1][j - 1] = signedNumber.doubleValue();
				} else {
					return null;
				}
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public double[] toDoubleVector() {
		if (Config.MAX_AST_SIZE < size()) {
			throw new ASTElementLimitExceeded(size());
		}
		double[] result = new double[argSize()];
		ISignedNumber signedNumber;
		for (int i = 1; i < size(); i++) {
			signedNumber = get(i).evalReal();
			if (signedNumber != null) {
				result[i - 1] = signedNumber.doubleValue();
			} else {
				return null;
			}
		}
		return result;
	}

	private final String toFullFormString() {
		final String sep = ", ";
		IExpr temp = null;
		if (size() > 0) {
			temp = head();
		}
		StringBuilder text;
		if (temp == null) {
			text = new StringBuilder("<null-tag>");
		} else {
			text = new StringBuilder(temp.toString());
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			text.append('(');
		} else {
			text.append('[');
		}
		for (int i = 1; i < size(); i++) {
			final IExpr o = get(i);
			text = text.append(o == this ? "(this AST)" : o.toString());
			if (i < argSize()) {
				text.append(sep);
			}
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			text.append(')');
		} else {
			text.append(']');
		}
		return text.toString();
	}

	/**
	 * Returns the ISymbol of the IAST. If the head itself is a IAST it will recursively call head().
	 */
	@Override
	public ISymbol topHead() {
		IExpr header = head();
		return header instanceof ISymbol ? (ISymbol) header : header.topHead();
	}

	/** {@inheritDoc} */
	@Override
	public RealMatrix toRealMatrix() {
		final double[][] elements = toDoubleMatrix();
		if (elements != null) {
			return new Array2DRowRealMatrix(elements, false);
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public RealVector toRealVector() {
		final double[] elements = toDoubleVector();
		if (elements != null) {
			return new ArrayRealVector(elements, false);
		}
		return null;
	}

	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			if (OutputFormFactory.get(EvalEngine.get().isRelaxedSyntax()).convert(sb, this)) {
				return sb.toString();
			}
			sb = null;

			final StringBuilder buf = new StringBuilder();
			if (size() > 0 && isList()) {
				buf.append('{');
				for (int i = 1; i < size(); i++) {
					buf.append(get(i) == this ? "(this AST)" : String.valueOf(get(i)));
					if (i < argSize()) {
						buf.append(", ");
					}
				}
				buf.append('}');
				return buf.toString();

			} else if (isAST(F.Slot, 2) && (arg1().isReal())) {
				try {
					final int slot = ((ISignedNumber) arg1()).toInt();
					if (slot <= 0) {
						return toFullFormString();
					}
					if (slot == 1) {
						return "#";
					}
					return "#" + slot;
				} catch (final ArithmeticException e) {
					// fall through
				}
				return toFullFormString();

			} else {
				return toFullFormString();
			}
		} catch (RuntimeException e) {
			if (Config.SHOW_STACKTRACE) {
				System.out.println(fullFormString());
			}
			throw e;
		}

	}

	/** {@inheritDoc} */
	@Override
	public IExpr upper() {
		if (isInterval1()) {
			return first().second();
		}
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr variables2Slots(final Map<IExpr, IExpr> map, final Collection<IExpr> variableCollector) {
		return variables2Slots(this, Predicates.isUnaryVariableOrPattern(),
				new UnaryVariable2Slot(map, variableCollector));
	}
}