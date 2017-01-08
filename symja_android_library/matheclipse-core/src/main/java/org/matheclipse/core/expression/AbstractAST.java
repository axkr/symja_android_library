package org.matheclipse.core.expression;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.hipparchus.complex.Complex;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.function.LeafCount;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.generic.UnaryVariable2Slot;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.polynomials.ExprPolynomial;
import org.matheclipse.core.polynomials.ExprPolynomialRing;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

public abstract class AbstractAST implements IAST {

	protected static final class ASTIterator implements ListIterator<IExpr> {

		private int _currentIndex;

		private int _end; // Exclusive.

		private int _nextIndex;

		private int _start; // Inclusive.

		private AbstractAST _table;

		@Override
		public void add(IExpr o) {
			_table.append(_nextIndex++, o);
			_end++;
			_currentIndex = -1;
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
			if (_currentIndex >= 0) {
				_table.remove(_currentIndex);
				_end--;
				if (_currentIndex < _nextIndex) {
					_nextIndex--;
				}
				_currentIndex = -1;
			} else {
				throw new IllegalStateException();
			}
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
	 * 
	 */
	private static final long serialVersionUID = -8682706994448890660L;

	private static int compareToAST(final IAST lhaAST, final IAST rhsAST) {

		if (lhaAST.isPlusTimesPower()) {
			if (!rhsAST.isPlusTimesPower()) {
				return -1;
			}
		} else {
			if (rhsAST.isPlusTimesPower()) {
				return 1;
			}
		}

		// compare the headers
		int cp = lhaAST.head().compareTo(rhsAST.head());
		if (cp != 0) {
			return cp;
		}

		final int commonArgSize = (lhaAST.size() > rhsAST.size()) ? rhsAST.size() : lhaAST.size();
		for (int i = 1; i < commonArgSize; i++) {
			cp = lhaAST.get(i).compareTo(rhsAST.get(i));
			if (cp != 0) {
				return cp;
			}
		}

		return lhaAST.size() - rhsAST.size();
	}

	private static int compareToPowerExpr(final IAST lhsPowerAST, final IExpr rhsSymbolOrPattern) {
		IExpr arg1 = lhsPowerAST.arg1();
		if (arg1.isSymbolOrPatternObject()) {
			// if (rhsSymbolOrPattern.isPlus() || rhsSymbolOrPattern.isTimes())
			// {
			// return (-1) * compareToTimes((IAST) rhsSymbolOrPattern,
			// lhsPowerAST);
			// }
			final int cp = arg1.compareTo(rhsSymbolOrPattern);
			if (cp != 0) {
				return cp;
			}
			// "x^1" compared to "x^arg2()"
			IExpr arg2 = lhsPowerAST.arg2();
			if (arg2.isNumeric()) {
				return lhsPowerAST.arg2().compareTo(F.CD1);
			}
			return arg2.compareTo(F.C1);
		}
		return 1;
	}

	/**
	 * Compares lhsAST (Times) AST with the specified rhsAST for order. Returns
	 * a negative integer, zero, or a positive integer as lhsAST (Times) AST is
	 * canonical less than, equal to, or greater than the specified AST.
	 */
	private static int compareToTimes(final IAST lhsAST, final IAST rhsAST) {
		if (rhsAST.isPower()) {
			// compare from the last this (Times) element:
			final IExpr lastTimes = lhsAST.get(lhsAST.size() - 1);
			int cp;
			if (lastTimes.isSymbolOrPatternObject()) {
				cp = lastTimes.compareTo(rhsAST.arg1());
				if (cp != 0) {
					return cp;
				}
				IExpr arg2 = rhsAST.arg2();
				if (arg2.isNumeric()) {
					return F.CD1.compareTo(arg2);
				}
				return F.C1.compareTo(arg2);
				// return F.C1.compareTo(rhsAST.arg2());
			} else if (lastTimes.isPower()) {
				// compare 2 Power ast's
				cp = ((IAST) lastTimes).arg1().compareTo(rhsAST.arg1());
				if (cp != 0) {
					return cp;
				}
				cp = ((IAST) lastTimes).arg2().compareTo(rhsAST.arg2());
				if (cp != 0) {
					return cp;
				}
				return 1;
			} else {
				return compareToAST(lhsAST, rhsAST);
			}
		} else if (rhsAST.isTimes()) {
			// compare from the last element:
			int i0 = lhsAST.size();
			int i1 = rhsAST.size();
			int commonArgCounter = (i0 > i1) ? i1 : i0;
			int cp;
			while (--commonArgCounter > 0) {
				cp = lhsAST.get(--i0).compareTo(rhsAST.get(--i1));
				if (cp != 0) {
					return cp;
				}
			}
			return lhsAST.size() - rhsAST.size();
		}

		return compareToAST(lhsAST, rhsAST);
	}

	/**
	 * Compares lhsTimesAST (Times) AST with the specified rhsSymbolOrPattern
	 * for order. Returns a negative integer, zero, or a positive integer as
	 * lhsTimesAST (Times) AST is canonical less than, equal to, or greater than
	 * the specified AST.
	 */
	private static int compareToTimesExpr(final IAST lhsTimesAST, final IExpr rhsSymbolOrPattern) {
		// compare from the last this (Times) element:
		final IExpr lastTimes = lhsTimesAST.get(lhsTimesAST.size() - 1);
		int cp;
		if (!(lastTimes instanceof IAST)) {
			cp = lastTimes.compareTo(rhsSymbolOrPattern);
			if (cp != 0) {
				return cp;
			}
		} else {
			if (lastTimes.isPower()) {
				return compareToPowerExpr((IAST) lastTimes, rhsSymbolOrPattern);
			}
		}

		return 1;
	}

	private static void internalFormOrderless(IAST ast, StringBuilder text, final String sep,
			boolean symbolsAsFactoryMethod, int depth, boolean useOperators) {
		for (int i = 1; i < ast.size(); i++) {
			if ((ast.get(i) instanceof IAST) && ast.head().equals(ast.get(i).head())) {
				internalFormOrderless((IAST) ast.get(i), text, sep, symbolsAsFactoryMethod, depth, useOperators);
			} else {
				text.append(ast.get(i).internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators));
			}
			if (i < ast.size() - 1) {
				text.append(sep);
			}
		}
	}

	/**
	 * Replace all elements determined by the unary <code>from</code> predicate,
	 * with the element generated by the unary <code>to</code> function. If the
	 * unary function returns null replaceAll returns null.
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
			IAST result;
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
	 * Flags for controlling evaluation and left-hand-side pattern-matching
	 * expressions
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
	public final void addEvalFlags(final int i) {
		fEvalFlags |= i;
	}

	/** {@inheritDoc} */
	@Override
	public IAST appendAtClone(int position, IExpr expr) {
		IAST ast = clone();
		ast.append(position, expr);
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public IAST appendClone(IExpr expr) {
		IAST ast = clone();
		ast.append(expr);
		return ast;
	}

	@Override
	public IAST apply(final IExpr head) {
		return setAtCopy(0, head);
	} 

	@Override
	public final IAST apply(final IExpr head, final int start) {
		return apply(head, start, size());
	}

	@Override
	public IAST apply(final IExpr head, final int start, final int end) {
		final IAST ast = F.ast(head);
		for (int i = start; i < end; i++) {
			ast.append(get(i));
		}
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public final ASTRange args() {
		return new ASTRange(this, 1);
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
			if (temp.isSignedNumber()) {
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
	public IAST clone() {
		AbstractAST ast = null;
		try {
			ast = (AbstractAST) super.clone();
			ast.fEvalFlags = 0;
			ast.hashValue = 0;
		} catch (CloneNotSupportedException e) {
		}
		return ast;
	}

	/**
	 * Compares this expression with the specified expression for canonical
	 * order. Returns a negative integer, zero, or a positive integer as this
	 * expression is canonical less than, equal to, or greater than the
	 * specified expression.
	 */
	@Override
	public int compareTo(final IExpr rhsExpr) {
		if (isAST(F.DirectedInfinity)) {
			if (!rhsExpr.isAST(F.DirectedInfinity)) {
				return -1;
			}
			return compareToAST(this, (IAST) rhsExpr);
		} else {
			if (rhsExpr.isAST(F.DirectedInfinity)) {
				return 1;
			}
		}

		if (rhsExpr.isAST()) {
			// special comparison for Times?
			if (isTimes()) {
				return compareToTimes(this, (IAST) rhsExpr);
			}
			if (rhsExpr.isTimes()) {
				return -1 * compareToTimes((IAST) rhsExpr, this);
			}
			return compareToAST(this, (IAST) rhsExpr);
		}

		if (rhsExpr.isSymbolOrPatternObject() && (isPlus() || isTimes())) {
			return compareToTimesExpr(this, rhsExpr);
		}
		if (isPower()) {
			return compareToPowerExpr(this, rhsExpr);
		}

		int x = hierarchy();
		int y = rhsExpr.hierarchy();
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object object) {
		int size = size();
		for (int i = 0; i < size; i++) {
			if (object.equals(get(i))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public IAST copy() {
		return clone();
	}

	/** {@inheritDoc} */
	@Override
	public IAST copyFrom(int index) {
		AST result = new AST(size() - index + 1, false);
		result.append(head());
		result.appendAll(this, index, size());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST copyHead() {
		return AST.newInstance(head());
	}

	/** {@inheritDoc} */
	@Override
	public final IAST copyUntil(int index) {
		return AST.newInstance(index, this, index);
	}

	/** {@inheritDoc} */
	@Override
	public final IAST copyUntil(final int intialCapacity, int index) {
		return AST.newInstance(index, this, index);
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
			if (size() != list.size()) {
				return false;
			}
			int size = size();
			for (int i = 0; i < size; i++) {
				if (!get(i).equals(list.get(i))) {
					return false;
				}
			}
			return true;
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

		for (int i = from0; i < size() - 1; i++) {
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
	@Override
	public final double evalDouble() {
		ISignedNumber signedNumber = evalSignedNumber();
		if (signedNumber != null) {
			return signedNumber.doubleValue();
		}
		throw new WrongArgumentType(this, "Conversion into a double numeric value is not possible!");
	}

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
	public final ISignedNumber evalSignedNumber() {
		if (isNumericFunction()) {
			IExpr result = EvalEngine.get().evalN(this);
			if (result.isSignedNumber()) {
				return (ISignedNumber) result;
			}
		}
		return null;
	}

	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (Config.DEBUG) {
			System.out.println(toString());
		}
		IExpr temp = engine.evalAST(this);
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
	public final IAST[] filter(final Function<IExpr, IExpr> function) {
		IAST[] result = new IAST[2];
		result[0] = copyHead();
		result[1] = copyHead();
		filter(result[0], result[1], function);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IAST filterAST, IAST restAST, final Function<IExpr, IExpr> function) {
		final int size = size();
		for (int i = 1; i < size; i++) {
			IExpr expr = function.apply(get(i));
			if (expr.isPresent()) {
				filterAST.append(expr);
			} else {
				restAST.append(get(i));
			}
		}
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IAST filterAST, IAST restAST, Predicate<? super IExpr> predicate) {
		final int size = size();
		for (int i = 1; i < size; i++) {
			if (predicate.test(get(i))) {
				filterAST.append(get(i));
			} else {
				restAST.append(get(i));
			}
		}
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST filter(IAST filterAST, IExpr expr) {
		return filter(filterAST, Predicates.isTrue(expr));
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IAST filterAST, Predicate<? super IExpr> predicate) {
		final int size = size();
		for (int i = 1; i < size; i++) {
			if (predicate.test(get(i))) {
				filterAST.append(get(i));
			}
		}
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IAST filterAST, Predicate<? super IExpr> predicate, int maxMatches) {
		int count = 0;
		if (count >= maxMatches) {
			return filterAST;
		}
		final int size = size();
		for (int i = 1; i < size; i++) {
			if (predicate.test(get(i))) {
				if (++count == maxMatches) {
					filterAST.append(get(i));
					break;
				}
				filterAST.append(get(i));
			}
		}
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST[] filter(Predicate<? super IExpr> predicate) {
		IAST[] result = new IAST[2];
		result[0] = copyHead();
		result[1] = copyHead();
		filter(result[0], result[1], predicate);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public final int findFirstEquals(final IExpr expr) {
		for (int i = 1; i < size(); i++) {
			if (equalsAt(i, expr)) {
				return i;
			}
		}
		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public void forEach(Consumer<? super IExpr> action) {
		final int size = size();
		for (int i = 1; i < size; i++) {
			action.accept(get(i));
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String fullFormString() {
		final String sep = ", ";
		IExpr temp = head();
		StringBuilder text = new StringBuilder();
		if (temp == null) {
			text.append("<null-head>");
		} else {
			text.append(temp.fullFormString());
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			text.append('(');
		} else {
			text.append('[');
		}
		for (int i = 1; i < size(); i++) {
			temp = get(i);
			if (temp == null) {
				text.append("<null-arg>");
			} else {
				text.append(get(i).fullFormString());
				if (i < size() - 1) {
					text.append(sep);
				}
			}
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
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

	/**
	 * Casts an <code>IExpr</code> at position <code>index</code> to an
	 * <code>IAST</code>.
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
	 * Casts an <code>IExpr</code> at position <code>index</code> to an
	 * <code>IInteger</code>.
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
	 * Casts an <code>IExpr</code> which is a list at position
	 * <code>index</code> to an <code>IAST</code>.
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
	 * Casts an <code>IExpr</code> at position <code>index</code> to an
	 * <code>INumber</code>.
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
	public final IExpr getOneIdentity(IExpr defaultValue) {
		if (size() > 2) {
			return this;
		}
		if (size() == 2) {
			return arg1();
		}
		return defaultValue;
	}

	@Override
	public final IExpr getPart(final int... positions) {
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
	 * <p>
	 * FNV-1 hash code of this <code>IAST</code>.
	 * </p>
	 * 
	 * <p>
	 * See: <a href=
	 * "https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function#FNV-1_hash">
	 * Wikipedia: Fowler–Noll–Vo hash function</a>
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
	public final int hierarchy() {
		return ASTID;
	}

	/** {@inheritDoc} */
	@Override
	public final String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, false);
	}

	/** {@inheritDoc} */
	@Override
	public final String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators) {
		final String sep = ",";
		final IExpr temp = head();
		if (temp.equals(F.Hold) && size() == 2) {
			return arg1().internalFormString(symbolsAsFactoryMethod, depth);
		}
		if (isInfinity()) {
			return "oo";
		}
		if (isNegativeInfinity()) {
			return "Noo";
		}
		if (isComplexInfinity()) {
			return "CComplexInfinity";
		}
		if (this.equals(F.Slot1)) {
			return "Slot1";
		}
		if (this.equals(F.Slot2)) {
			return "Slot2";
		}
		if (isPower()) {
			if (equalsAt(2, F.C1D2)) {
				if (arg1().isInteger()) {
					// square root of an integer number
					IInteger i = (IInteger) arg1();
					if (i.equals(F.C2)) {
						return "CSqrt2";
					} else if (i.equals(F.C3)) {
						return "CSqrt3";
					} else if (i.equals(F.C5)) {
						return "CSqrt5";
					} else if (i.equals(F.C6)) {
						return "CSqrt6";
					} else if (i.equals(F.C7)) {
						return "CSqrt7";
					} else if (i.equals(F.C10)) {
						return "CSqrt10";
					}
				}
				return "Sqrt(" + arg1().internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators) + ")";
			}
			if (equalsAt(2, F.C2)) {
				return "Sqr(" + arg1().internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators) + ")";
			}
			if (equalsAt(2, F.CN1D2) && arg1().isInteger()) {
				// negative square root of an integer number
				IInteger i = (IInteger) arg1();
				if (i.equals(F.C2)) {
					return "C1DSqrt2";
				} else if (i.equals(F.C3)) {
					return "C1DSqrt3";
				} else if (i.equals(F.C5)) {
					return "C1DSqrt5";
				} else if (i.equals(F.C6)) {
					return "C1DSqrt6";
				} else if (i.equals(F.C7)) {
					return "C1DSqrt7";
				} else if (i.equals(F.C10)) {
					return "C1DSqrt10";
				}
			}
			if (arg2().isInteger()) {
				try {
					long exp = ((IInteger) arg2()).toLong();
					// create Power(arg1, exp)
					return "Power(" + arg1().internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators) + ","
							+ Long.toString(exp) + ")";

				} catch (RuntimeException ex) {

				}
			}

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
				text.append("$(");
				for (int i = 0; i < size(); i++) {
					text.append(get(i).internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators));
					if (i < size() - 1) {
						text.append(sep);
					}
				}
				text.append(')');
				return text.toString();
			}
		} else if (temp.isPattern() || temp.isAST()) {
			text.append("$(");
			for (int i = 0; i < size(); i++) {
				text.append(get(i).internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators));
				if (i < size() - 1) {
					text.append(sep);
				}
			}
			text.append(')');
			return text.toString();
		}

		if (isTimes() && size() == 3 && arg1().isMinusOne() && !arg2().isTimes()) {
			return "Negate(" + arg2().internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators) + ")";
		}

		if (useOperators && size() == 3) {
			if (isTimes()) {
				IExpr arg1 = arg1();
				IExpr arg2 = arg2();
				boolean isLowerPrecedence = arg1.isPlus();
				internalOperatorForm(arg1, isLowerPrecedence, symbolsAsFactoryMethod, depth, useOperators, text);
				text.append('*');
				isLowerPrecedence = arg2.isPlus();
				internalOperatorForm(arg2, isLowerPrecedence, symbolsAsFactoryMethod, depth, useOperators, text);
				return text.toString();
			} else if (isPlus()) {
				IExpr arg1 = arg1();
				IExpr arg2 = arg2();
				internalOperatorForm(arg1, false, symbolsAsFactoryMethod, depth, useOperators, text);
				text.append('+');
				internalOperatorForm(arg2, false, symbolsAsFactoryMethod, depth, useOperators, text);
				return text.toString();
			} else if (isPower()) {
				IExpr arg1 = arg1();
				IExpr arg2 = arg2();
				boolean isLowerPrecedence = arg1.isTimes() || arg1.isPlus();
				internalOperatorForm(arg1, isLowerPrecedence, symbolsAsFactoryMethod, depth, useOperators, text);
				text.append('^');
				isLowerPrecedence = arg2.isTimes() || arg2.isPlus();
				internalOperatorForm(arg2, isLowerPrecedence, symbolsAsFactoryMethod, depth, useOperators, text);
				return text.toString();
			}

		}
		text.append(temp.internalJavaString(false, 0, useOperators));
		text.append('(');
		if (isTimes() || isPlus()) {
			if (depth == 0 && isList()) {
				text.append('\n');
			}
			internalFormOrderless(this, text, sep, symbolsAsFactoryMethod, depth, useOperators);
			if (depth == 0 && isList()) {
				text.append('\n');
			}
		} else {
			if (depth == 0 && isList()) {
				text.append('\n');
			}
			for (int i = 1; i < size(); i++) {
				text.append(get(i).internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators));
				if (i < size() - 1) {
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
			boolean useOperators, StringBuilder text) {
		if (isLowerPrecedence) {
			text.append("(");
		}
		text.append(arg1.internalJavaString(symbolsAsFactoryMethod, depth + 1, useOperators));
		if (isLowerPrecedence) {
			text.append(")");
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, true);
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
		return isSameHead(header);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAST(final IExpr header, final int length) {
		return isSameHead(header, length);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST(IExpr header, int length, IExpr... args) {
		if (isSameHead(header, length)) {
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
	public final boolean isAST(IExpr header, int minLength, int maxLength) {
		return isSameHead(header, minLength, maxLength);
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
	public final boolean isASTSizeGE(final IExpr header, final int length) {
		return isSameHeadSizeGE(header, length);
	}

	@Override
	public final boolean isAtom() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isComplex() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isComplexInfinity() {
		return isSameHead(F.DirectedInfinity, 1);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isComplexNumeric() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isCondition() {
		return isSameHead(F.Condition, 3);
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
	public final IAST[] isDerivative() {
		if (head().isAST()) {
			IAST headAST = (IAST) head();
			if (headAST.isAST(F.Derivative, 2)) {
				IAST[] result = new IAST[3];
				result[0] = headAST;
				result[1] = this;
				return result;
			}

			if (headAST.head().isAST(F.Derivative, 2)) {
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

	@Override
	public final boolean isEmpty() {
		return size() == 0;
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
	public final boolean isFraction() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFree(final IExpr pattern) {
		return isFree(pattern, true);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFree(final IExpr pattern, boolean heads) {
		final IPatternMatcher matcher = new PatternMatcher(pattern);
		return !isMember(matcher, heads);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFree(Predicate<IExpr> predicate, boolean heads) {
		return !isMember(predicate, heads);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFreeAST(final IExpr pattern) {
		final IPatternMatcher matcher = new PatternMatcher(pattern);
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
		if ((getEvalFlags() & IAST.CONTAINS_NO_PATTERN) == IAST.CONTAINS_NO_PATTERN) {
			return true;
		}
		if ((getEvalFlags() & IAST.CONTAINS_PATTERN_EXPR) != IAST.NO_FLAG) {
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
				addEvalFlags(getEvalFlags());
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
	public final boolean isInfinity() {
		return this.equals(F.CInfinity);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isInteger() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isIntegerResult() {
		ISymbol symbol = topHead();
		if (symbol.equals(F.Floor) || symbol.equals(F.Ceiling) || symbol.equals(F.IntegerPart)) {
			return true;
		}
		if (isPower() && arg2().isInteger() && arg2().isPositive()) {
			if (arg1().isIntegerResult()) {
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
	public final boolean isInterval() {
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
	public final boolean isInterval1() {
		return isSameHead(F.Interval, 2) && arg1().isAST(F.List, 3);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isList() {
		return isSameHeadSizeGE(F.List, 1);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isListOfLists() {
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
	public final boolean isLog() {
		return isSameHead(F.Log, 2);
	}

	/** {@inheritDoc} */
	@Override
	public int[] isMatrix() {
		if (isEvalFlagOn(IAST.IS_MATRIX)) {
			final int[] dim = new int[2];
			dim[0] = size() - 1;
			dim[1] = ((IAST) arg1()).size() - 1;
			return dim;
		}
		if (isList()) {
			final int[] dim = new int[2];
			dim[0] = size() - 1;
			if (dim[0] > 0) {
				dim[1] = 0;
				if (arg1().isList()) {
					dim[1] = ((IAST) arg1()).size() - 1;
					for (int i = 2; i < size(); i++) {
						if (!get(i).isList()) {
							// this row is not a list
							return null;
						}
						if (dim[1] != ((IAST) get(i)).size() - 1) {
							// this row has another dimension
							return null;
						}
					}
					addEvalFlags(IAST.IS_MATRIX);
					return dim;
				}
			}

		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isMember(Predicate<IExpr> predicate, boolean heads) {
		if (predicate.test(this)) {
			return true;
		}
		int start = 1;
		if (heads) {
			start = 0;
		}
		for (int i = start; i < size(); i++) {
			if (get(i).isMember(predicate, heads)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isModule() {
		return size() == 3 && head().equals(F.Module);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNegative() {
		if (isNumericFunction()) {
			IExpr result = EvalEngine.get().evalN(this);
			if (result.isSignedNumber()) {
				return ((ISignedNumber) result).isNegative();
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNegativeInfinity() {
		return this.equals(F.CNInfinity);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNegativeResult() {
		if (isPlus()) {
			for (int i = 1; i < size(); i++) {
				if (get(i).isNegativeResult() || AbstractAssumptions.assumeNegative(get(i))) {
					continue;
				}
				return false;
			}
			return true;
		}
		if (isTimes()) {
			boolean flag = false;
			for (int i = 1; i < size(); i++) {
				if (get(i).isNonNegativeResult()) {
				} else if (AbstractAssumptions.assumeNonNegative(get(i))) {
				} else if (get(i).isNegativeResult()) {
					flag = !flag;
				} else if (AbstractAssumptions.assumeNegative(get(i))) {
					flag = !flag;
				} else {
					return false;
				}
			}
			return flag;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNonNegativeResult() {
		ISymbol symbol = topHead();
		if (symbol.equals(F.Abs)) {
			return true;
		}
		if (isPlus()) {
			for (int i = 1; i < size(); i++) {
				if (get(i).isNonNegativeResult() || AbstractAssumptions.assumeNonNegative(get(i))) {
					continue;
				}
				return false;
			}
			return true;
		}
		if (isTimes()) {
			boolean flag = true;
			for (int i = 1; i < size(); i++) {
				if (get(i).isNonNegativeResult()) {
				} else if (AbstractAssumptions.assumeNonNegative(get(i))) {
				} else if (get(i).isNegativeResult()) {
					flag = !flag;
				} else if (AbstractAssumptions.assumeNegative(get(i))) {
					flag = !flag;
				} else {
					return false;
				}
			}
			return flag;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNot() {
		return size() == 2 && head().equals(F.Not);
	}

	@Override
	public final boolean isNumber() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNumeric() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNumericFunction() {
		ISymbol symbol = topHead();
		if ((symbol.getAttributes() & ISymbol.NUMERICFUNCTION) == ISymbol.NUMERICFUNCTION) {
			// check if all arguments are &quot;numeric&quot;
			for (int i = 1; i < size(); i++) {
				if (!get(i).isNumericFunction()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNumericMode() {
		ISymbol symbol = topHead();
		if ((symbol.getAttributes() & ISymbol.NUMERICFUNCTION) == ISymbol.NUMERICFUNCTION) {
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
	public final boolean isOneIdentityAST1() {
		return isAST1() && topHead().hasOneIdentityAttribute();
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
	public final boolean isPatternExpr() {
		return (fEvalFlags & CONTAINS_PATTERN_EXPR) != NO_FLAG;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPlus() {
		return isSameHeadSizeGE(F.Plus, 3);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPlusTimesPower() {
		return isPlus() || isTimes() || isPower();
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
	public final boolean isPolynomial(ISymbol variable) {
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
	public final boolean isPositive() {
		if (isNumericFunction()) {
			IExpr result = EvalEngine.get().evalN(this);
			if (result.isSignedNumber()) {
				return ((ISignedNumber) result).isPositive();
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPositiveResult() {
		if (isPlus()) {
			for (int i = 1; i < size(); i++) {
				if (get(i).isPositiveResult() || AbstractAssumptions.assumePositive(get(i))) {
					continue;
				}
				return false;
			}
			return true;
		}
		if (isTimes()) {
			boolean flag = true;
			for (int i = 1; i < size(); i++) {
				if (get(i).isPositiveResult()) {
				} else if (AbstractAssumptions.assumePositive(get(i))) {
				} else if (get(i).isNegativeResult()) {
					flag = !flag;
				} else if (AbstractAssumptions.assumeNegative(get(i))) {
					flag = !flag;
				} else {
					return false;
				}
			}
			return flag;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPower() {
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
			dim[0] = size() - 1;
			if (dim[0] > 0) {
				dim[1] = 0;
				if (arg1().isList()) {
					IAST row = (IAST) arg1();
					dim[1] = row.size() - 1;
					boolean containsNum = false;
					for (int j = 1; j < row.size(); j++) {
						if (row.get(j).isSignedNumber()) {
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
						if (dim[1] != row.size() - 1) {
							// this row has another dimension
							return false;
						}
						for (int j = 1; j < row.size(); j++) {
							if (row.get(j).isSignedNumber()) {
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
	public final boolean isRealResult() {
		IExpr head = head();
		if (size() == 2 && F.Cos.equals(head) && F.Sin.equals(head)) {
			// TODO add more functions
			return arg1().isRealResult();
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
		if (isPower() && (!arg2().isZero() || !arg1().isZero())) {
			if (!arg1().isRealResult()) {
				return false;
			}
			if (!arg2().isRealResult()) {
				return false;
			}
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
				if (get(i).isSignedNumber()) {
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
	public final boolean isRuleAST() {
		return size() == 3 && (head().equals(F.Rule) || head().equals(F.RuleDelayed));
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
	 * Check if the object at index 0 (i.e. the head of the list) is the same
	 * object as <code>head</code>
	 * 
	 * @param head
	 *            object to compare with element at location <code>0</code>
	 * @return
	 */
	public boolean isSameHead(IExpr head) {
		return head().equals(head);
	}

	/**
	 * Check if the object at index 0 (i.e. the head of the list) is the same
	 * object as <code>head</code> and if the size of the list equals
	 * <code>length</code>.
	 * 
	 * @param head
	 *            object to compare with element at location <code>0</code>
	 * @param length
	 * @return
	 */
	public boolean isSameHead(IExpr head, int length) {
		return length == size() && head().equals(head);
	}

	/**
	 * Check if the object at index 0 (i.e. the head of the list) is the same
	 * object as <code>head</code> and if the size of the list is between
	 * <code>minLength</code> and <code>maxLength</code>.
	 * 
	 * @param head
	 *            object to compare with element at location <code>0</code>
	 * @param minLength
	 *            minimum length of list elements.
	 * @param maxLength
	 *            maximum length of list elements.
	 * @return
	 */
	public boolean isSameHead(IExpr head, int minLength, int maxLength) {
		int size = size();
		return head().equals(head) && minLength <= size && maxLength >= size;
	}

	/**
	 * Check if the object at index 0 (i.e. the head of the list) is the same
	 * object as <code>head</code> and if the size of the list is greater or
	 * equal <code>length</code>.
	 * 
	 * @param head
	 *            object to compare with element at location <code>0</code>
	 * @param length
	 * @return
	 */
	public boolean isSameHeadSizeGE(IExpr head, int length) {
		int size = size();
		return head().equals(head) && length <= size;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSequence() {
		return isSameHeadSizeGE(F.Sequence, 1);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSignedNumber() {
		return false;
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

	@Override
	public final boolean isSpan() {
		return isSameHeadSizeGE(F.Span, 3);
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
	public final boolean isTimes() {
		return isSameHeadSizeGE(F.Times, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isUnit() {
		if (isZero()) {
			return false;
		}
		if (isOne()) {
			return true;
		}
		if (isNumber()) {
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
			return size() - 1;
		}
		if (isList()) {
			final int dim = size() - 1;
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

	// @Override
	/**
	 * Returns an iterator over the elements in this list starting with offset
	 * <b>0</b>.
	 * 
	 * @return an iterator over this list values.
	 */
	// public final Iterator<IExpr> iterator0() {
	// return super.iterator();
	// }

	/**
	 * Returns an iterator over the elements in this <code>IAST</code> starting
	 * with offset <b>1</b>.
	 * 
	 * @return an iterator over this <code>IAST</code>s argument values from
	 *         <code>1..(size-1)</code>.
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
	public final IExpr last() {
		return get(size() - 1);
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
	public final IAST map(final Function<IExpr, IExpr> function) {
		return map(copy(), function);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IAST map(final IAST clonedResultAST, final Function<IExpr, IExpr> function) {
		IExpr temp;
		for (int i = 1; i < size(); i++) {
			temp = function.apply(get(i));
			if (temp != null) {
				clonedResultAST.set(i, temp);
			}
		}
		return clonedResultAST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IAST map(IAST resultAST, IAST secondAST, BiFunction<IExpr, IExpr, IExpr> function) {
		for (int i = 1; i < size(); i++) {
			resultAST.append(function.apply(get(i), secondAST.get(i)));
		}
		return resultAST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IAST map(final IExpr head, final Function<IExpr, IExpr> function) {
		return map(setAtCopy(0, head), function);
	}

	/** {@inheritDoc} */
	@Override
	public final IAST mapThread(IAST appendAST, final IAST replacement, int position) {
		final Function<IExpr, IExpr> function = Functors.replaceArg(replacement, position);
		IExpr temp;
		for (int i = 1; i < size(); i++) {
			temp = function.apply(get(i));
			if (temp != null) {
				appendAST.append(temp);
			}
		}
		return appendAST;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST mapThread(final IAST replacement, int position) {
		return map(Functors.replaceArg(replacement, position));
	}

	/**
	 * Additional <code>negative</code> method, which works like opposite to
	 * fulfill groovy's method signature
	 * 
	 * @return
	 */
	@Override
	public final IExpr negative() {
		return opposite();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr opposite() {
		if (isTimes()) {
			IExpr arg1 = arg1();
			if (arg1.isNumber()) {
				if (arg1.isMinusOne()) {
					if (size() == 3) {
						return arg2();
					}
					return removeAtClone(1);
				}
				return setAtCopy(1, ((INumber) arg1).negate());
			}
			IAST timesAST = clone();
			timesAST.append(1, F.CN1);
			return timesAST;
		}
		if (isNegativeInfinity()) {
			return F.CInfinity;
		}
		if (isInfinity()) {
			return F.CNInfinity;
		}
		return F.eval(F.Times(F.CN1, this));
	}

	@Override
	public final IExpr or(final IExpr that) {
		return F.Or(this, that);
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

	@Override
	public final IExpr plus(final IExpr that) {
		if (that.isZero()) {
			return this;
		}
		return F.eval(F.Plus(this, that));
	}

	/** {@inheritDoc} */
	@Override
	public final IAST prependClone(IExpr expr) {
		return appendAtClone(1, expr);
	}

	/**
	 * Get the range of elements [0..sizeOfAST[ of the AST
	 * 
	 * @return
	 */
	@Override
	public final ASTRange range() {
		return new ASTRange(this, 0, size());
	}

	/**
	 * Get the range of elements [start..sizeOfAST[ of the AST
	 * 
	 * @return
	 */
	@Override
	public final ASTRange range(final int start) {
		return new ASTRange(this, start, size());
	}

	/**
	 * Get the range of elements [start..end[ of the AST
	 * 
	 * @return
	 */
	@Override
	public final ASTRange range(final int start, final int end) {
		return new ASTRange(this, start, end);
	}

	/** {@inheritDoc} */
	@Override
	public final IAST removeAtClone(int position) {
		IAST ast = clone();
		ast.remove(position);
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST setAtClone(int position, IExpr expr) {
		IAST ast = clone();
		ast.set(position, expr);
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public final void setEvalFlags(final int i) {
		fEvalFlags = i;
	}

	/**
	 * Signum functionality is used in JAS toString() method, don't use it as
	 * math signum function.
	 * 
	 * @deprecated
	 */
	@Deprecated
	@Override
	public final int signum() {
		if (isTimes()) {
			IExpr temp = arg1();
			if (temp.isSignedNumber() && ((ISignedNumber) temp).isNegative()) {
				return -1;
			}
		}
		return 1;
	}

	@Override
	public final IExpr timesDistributed(final IExpr that) {
		if (that.isZero()) {
			return F.C0;
		}
		if (this.isPlus()) {
			IAST plus = this.map(x -> x.times(that));
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
				signedNumber = row.get(j).evalSignedNumber();
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
		double[] result = new double[size() - 1];
		ISignedNumber signedNumber;
		for (int i = 1; i < size(); i++) {
			signedNumber = get(i).evalSignedNumber();
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
		text.append('[');
		for (int i = 1; i < size(); i++) {
			final IExpr o = get(i);
			text = text.append(o == this ? "(this AST)" : o.toString());
			if (i < size() - 1) {
				text.append(sep);
			}
		}
		text.append(']');
		return text.toString();
	}

	/**
	 * Returns the ISymbol of the IAST. If the head itself is a IAST it will
	 * recursively call head().
	 */
	@Override
	public final ISymbol topHead() {
		IExpr header = head();
		if (header instanceof ISymbol) {
			// this should be the "most common" case:
			return (ISymbol) header;
		}
		if (header instanceof IAST) {
			// determine the head recursively
			return ((IAST) header).topHead();
		}
		// * Numbers return the header strings
		// * "DoubleComplex", "Double", "Integer", "Fraction", "Complex"
		if (header.isSignedNumber()) {
			if (header instanceof INum) {
				return F.RealHead;
			}
			if (header instanceof IInteger) {
				return F.IntegerHead;
			}
			if (header instanceof IFraction) {
				return F.Rational;
			}
		}
		if (header instanceof IComplex) {
			return F.Complex;
		}
		if (header instanceof IComplexNum) {
			return F.Complex;
		}
		if (header instanceof IPattern) {
			return F.PatternHead;
		}
		if (head() instanceof IStringX) {
			return F.StringHead;
		}
		return null;
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
			OutputFormFactory.get(EvalEngine.get().isRelaxedSyntax()).convert(sb, this);
			return sb.toString();
		} catch (IOException ioe) {
			if (Config.SHOW_STACKTRACE) {
				ioe.printStackTrace();
			}
		} catch (RuntimeException e1) {
		}

		try

		{
			final StringBuilder buf = new StringBuilder();
			if (size() > 0 && isList()) {
				buf.append('{');
				for (int i = 1; i < size(); i++) {
					buf.append(get(i) == this ? "(this AST)" : String.valueOf(get(i)));
					if (i < size() - 1) {
						buf.append(", ");
					}
				}
				buf.append('}');
				return buf.toString();

			} else if (isAST(F.Slot, 2) && (arg1().isSignedNumber())) {
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
		} catch (NullPointerException e) {
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
			return ((IAST) arg1()).arg2();
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