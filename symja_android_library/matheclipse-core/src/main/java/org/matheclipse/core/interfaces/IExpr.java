package org.matheclipse.core.interfaces;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.hipparchus.Field;
import org.hipparchus.FieldElement;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.ExprField;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.NILPointer;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.ISymbol2IntMap;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.core.visit.VisitorReplaceAll;
import org.matheclipse.core.visit.VisitorReplacePart;
import org.matheclipse.core.visit.VisitorReplaceSlots;

import edu.jas.structure.ElemFactory;
import edu.jas.structure.GcdRingElem;

/**
 * 
 * (I)nterface for a mathematical (Expr)ession<br />
 * 
 * <code>IExpr</code> is the main interface for the Symja object type hierarchy:
 * 
 * <pre>
 * java.lang.Object
 *    |--- java.util.AbstractCollection
 *    |       |--- java.util.AbstractList
 *    |               |--- org.matheclipse.core.expression.HMArrayList
 *    |                       |--- org.matheclipse.core.expression.AST - abstract syntax tree which represents lists, vectors, matrices and functions
 *    |                                           implements IAST, List, IExpr
 *    |
 *    |--- org.matheclipse.core.expression.ExprImpl 
 *            |           implements IExpr
 *            |
 *            |--- org.matheclipse.core.expression.ApcomplexNum - Apcomplex number
 *            |                   implements IComplexNum, INumber, IExpr
 *            |
 *            |--- org.matheclipse.core.expression.ApfloatNum - Apfloat number
 *            |                   implements INum, ISignedNumber, INumber, IExpr
 *            |
 *            |--- org.matheclipse.core.expression.ComplexNum - a complex number with real and imaginary part represented by Java <code>double</code>
 *            |                   implements IComplexNum, INumber, IExpr
 *            |
 *            |--- org.matheclipse.core.expression.ComplexSym - exact complex number
 *            |                   implements IComplex, IBigNumber, INumber, IExpr
 *            |
 *            |--- org.matheclipse.core.expression.FractionSym - exact fraction number
 *            |                   implements IFraction, IRational, ISignedNumber, IBigNumber, INumber, IExpr
 *            |
 *            |--- org.matheclipse.core.expression.IntegerSym - exact integer number 
 *            |                   implements IInteger, IRational, ISignedNumber, IBigNumber, INumber, IExpr
 *            |
 *            |--- org.matheclipse.core.expression.Num - a real number which is represented by a Java <code>double</code> value
 *            |                   implements INum, ISignedNumber, INumber, IExpr
 *            |
 *            |--- org.matheclipse.core.expression.Pattern - a pattern object (i.e. <code>x_</code>)
 *            |                   implements IPattern, IPatternObject, IExpr
 *            |
 *            |--- org.matheclipse.core.expression.PatternSequence - a pattern sequence object (i.e. <code>x__</code>)
 *            |                   implements IPatternSequence, IPatternObject, IExpr
 *            |
 *            |--- org.matheclipse.core.expression.StringX - a Java <code>string</code> wrapper
 *            |                   implements IStringX, IExpr
 *            |
 *            |--- org.matheclipse.core.expression.Symbol - represents variables, function names or constants
 *                                implements ISymbol, IExpr
 * </pre>
 * 
 */
public interface IExpr extends Comparable<IExpr>, GcdRingElem<IExpr>, Serializable, FieldElement<IExpr> {

	public enum COMPARE_TERNARY {
		TRUE, FALSE, UNDEFINED
	}

	public final static int ASTID = 1024;

	public final static int BLANKID = 256;

	public final static int COMPLEXID = 32;

	public final static int DOUBLECOMPLEXID = 4;

	public final static int DOUBLEID = 2;

	public final static int FRACTIONID = 16;

	public final static int INTEGERID = 8;

	public final static int METHODSYMBOLID = 2048;

	public final static int PATTERNID = 512;

	public final static int STRINGID = 64;

	public final static int SYMBOLID = 128;

	/**
	 * Returns an {@code IExpr} describing the specified value, if non-null, otherwise returns {@code F.NIL} .
	 * 
	 * @param value
	 *            the possibly-null value to describe
	 * @return an {@code IExpr} with a present value if the specified value is non-null, otherwise an empty
	 *         {@code Optional}
	 */
	public static IExpr ofNullable(@Nonnull IExpr value) {
		return value == null ? F.NIL : value;
	}

	/**
	 * Operator overloading for Scala operator <code>/</code>. Calls <code>divide(that)</code>.
	 * 
	 * @param that
	 * @return
	 */
	default IExpr $div(final IExpr that) {
		return divide(that);
	}

	/**
	 * Operator overloading for Scala operator <code>/</code>. Calls <code>divide(that)</code>.
	 * 
	 * @param that
	 * @return
	 */
	default IExpr $minus(final IExpr that) {
		return minus(that);
	}

	/**
	 * Operator overloading for Scala operator <code>+</code>. Calls <code>plus(that)</code>.
	 * 
	 * @param that
	 * @return
	 */
	default IExpr $plus(final IExpr that) {
		return plus(that);
	}

	/**
	 * Operator overloading for Scala operator <code>*</code>. Calls <code>times(that)</code>.
	 * 
	 * @param that
	 * @return
	 */
	default IExpr $times(final IExpr that) {
		return times(that);
	}

	/**
	 * Operator overloading for Scala operator <code>^</code>. Calls <code>power(that)</code>.
	 * 
	 * @param that
	 * @return
	 */
	default IExpr $up(final IExpr that) {
		return power(that);
	}

	@Override
	default IExpr abs() {
		return F.eval(F.Abs(this));
	}

	/**
	 * Accept a visitor with return type T
	 * 
	 * @param visitor
	 * @return
	 */
	public <T> T accept(IVisitor<T> visitor);

	/**
	 * Accept a visitor with return type <code>boolean</code>
	 * 
	 * @param visitor
	 * @return
	 */
	public boolean accept(IVisitorBoolean visitor);

	/**
	 * Accept a visitor with return type <code>int</code>
	 * 
	 * @param visitor
	 * @return
	 */
	public int accept(IVisitorInt visitor);

	/**
	 * Accept a visitor with return type <code>long</code>
	 * 
	 * @param visitor
	 * @return
	 */
	public long accept(IVisitorLong visitor);

	@Override
	default IExpr add(IExpr that) {
		return plus(that);
	}

	default IExpr and(final IExpr that) {
		return F.And(this, that);
	}

	/**
	 * @param leaves
	 * @return an IExpr instance with the current expression as head(), and leaves as leaves().
	 */
	default IExpr apply(IExpr... leaves) {
		final IAST ast = F.ast(head());
		for (int i = 0; i < leaves.length; i++) {
			ast.append(leaves[i]);
		}
		return ast;
	}

	/**
	 * @param leaves
	 * @return an IExpr instance with the current expression as head(), and leaves as leaves().
	 */
	default IExpr apply(List<? extends IExpr> leaves) {
		final IAST ast = F.ast(head());
		for (int i = 0; i < leaves.size(); i++) {
			ast.append(leaves.get(i));
		}
		return ast;
	}

	default Object asType(Class<?> clazz) {
		if (clazz.equals(Boolean.class)) {
			if (isTrue()) {
				return Boolean.TRUE;
			}
			if (isFalse()) {
				return Boolean.FALSE;
			}
		} else if (clazz.equals(Integer.class)) {
			if (isSignedNumber()) {
				try {
					return Integer.valueOf(((ISignedNumber) this).toInt());
				} catch (final ArithmeticException e) {
				}
			}
		} else if (clazz.equals(java.math.BigInteger.class)) {
			if (this instanceof IInteger) {
				return new java.math.BigInteger(((IInteger) this).toByteArray());
			}
		} else if (clazz.equals(String.class)) {
			return toString();
		}
		throw new UnsupportedOperationException("ExprImpl.asType() - cast not supported.");
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive
	 * integer as this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	default int compareTo(IExpr expr) {
		if (expr.isAST()) {
			if (!expr.isDirectedInfinity()) {
				return -1 * expr.compareTo(this);
			}
		}
		int x = hierarchy();
		int y = expr.hierarchy();
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	/**
	 * Return the argument of a complex number.
	 * 
	 * @return the argument of a complex number
	 */
	default IExpr complexArg() {
		return F.eval(F.Arg(this));
	}

	/**
	 * Conjugate this (complex-) number.
	 * 
	 * @return the conjugate complex number
	 */
	default IExpr conjugate() {
		return F.eval(F.Conjugate(this));
	}

	/**
	 * Returns an <code>IExpr</code> whose value is <code>(this - 1)</code>. Calculates
	 * <code>F.eval(F.Subtract(this, C1))</code> in the common case and uses a specialized implementation for derived
	 * number classes.
	 * 
	 * @return
	 */
	default IExpr dec() {
		return plus(F.CN1);
	}

	/**
	 * Returns an <code>IExpr</code> whose value is <code>(this / that)</code>. Calculates
	 * <code>F.eval(F.Times(this, F.Power(that, F.CN1)))</code> in the common case and uses a specialized implementation
	 * for derived number classes.
	 * 
	 * @param that
	 * @return
	 */
	@Override
	default IExpr divide(IExpr that) {
		if (that.isOne()) {
			return this;
		}
		if (that.isMinusOne()) {
			return negate();
		}
		return F.eval(F.Times(this, that.inverse()));
	}

	@Override
	default IExpr[] egcd(IExpr b) {
		throw new UnsupportedOperationException(toString());
	}

	/**
	 * Compare if <code>this == that</code:
	 * <ul>
	 * <li>return F.True if the comparison is <code>true</code></li>
	 * <li>return F.False if the comparison is <code>false</code></li>
	 * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
	 * </ul>
	 * 
	 * @param that
	 * @return <code>F.True, F.False or F.NIL</code
	 */
	default public IExpr equalTo(IExpr that) {
		COMPARE_TERNARY temp = BooleanFunctions.CONST_EQUAL.compareTernary(this, that);
		return ITernaryComparator.convertToExpr(temp);
	}

	/**
	 * Evaluate the expression to a <code>INumber</code> value.
	 * 
	 * @return <code>null</code> if the conversion is not possible.
	 */
	default Complex evalComplex() {
		if (isNumber()) {
			return ((INumber) this).complexNumValue().complexValue();
		}
		throw new WrongArgumentType(this, "Conversion into a complex numeric value is not possible!");
	}

	/**
	 * Evaluate the expression to a Java <code>double</code> value. If the conversion to a double value is not possible,
	 * the method throws a <code>WrongArgumentType</code> exception.
	 * 
	 * @return this expression converted to a Java <code>double</code> value.
	 */
	default double evalDouble() {
		if (isSignedNumber()) {
			return ((ISignedNumber) this).doubleValue();
		}
		throw new WrongArgumentType(this, "Conversion into a double numeric value is not possible!");
	}

	/**
	 * Evaluate the expression to a <code>INumber</code> value.
	 * 
	 * @return <code>null</code> if the conversion is not possible.
	 */
	default INumber evalNumber() {
		if (isNumber()) {
			return (INumber) EvalEngine.get().evalN(this);
		}
		return null;
	}

	/**
	 * Evaluate the expression to a <code>ISignedNumber</code> value.
	 * 
	 * @return <code>null</code> if the conversion is not possible.
	 */
	default ISignedNumber evalSignedNumber() {
		if (isSignedNumber()) {
			return (ISignedNumber) EvalEngine.get().evalN(this);
		}
		return null;
	}

	/**
	 * Evaluate an expression
	 * 
	 * @param engine
	 *            the evaluation engine
	 * @return the evaluated Object or <code>F.NIL</code> if the evaluation is not possible (i.e. the evaluation doesn't
	 *         change the object).
	 */
	default IExpr evaluate(EvalEngine engine) {
		return F.NIL;
	}

	default IExpr evaluateHead(IAST ast, EvalEngine engine) {
		IExpr result = engine.evalLoop(this);
		if (result.isPresent()) {
			// set the new evaluated header !
			return ast.apply(result);
		}
		return F.NIL;
	}

	@Override
	default ElemFactory<IExpr> factory() {
		return ExprRingFactory.CONST;
	}

	/**
	 * Return the <code>FullForm()</code> of this expression
	 * 
	 * @return
	 */
	default String fullFormString() {
		return toString();
	}

	@Override
	default IExpr gcd(IExpr that) {
		if (equals(that)) {
			return that;
		}
		return F.C1;
	}

	/**
	 * 
	 * Get the element at the specified <code>index</code> if this object is of type <code>IAST</code>.
	 * 
	 * @param index
	 * @return
	 */
	default IExpr getAt(final int index) {
		return F.Part(this, F.integer(index));
	}

	@Override
	default public Field<IExpr> getField() {
		return ExprField.CONST;
	}

	/**
	 * Compare if <code>this >= that</code:
	 * <ul>
	 * <li>return F.True if the comparison is <code>true</code></li>
	 * <li>return F.False if the comparison is <code>false</code></li>
	 * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
	 * </ul>
	 * 
	 * @param that
	 * @return <code>F.True, F.False or F.NIL</code
	 */
	default public IExpr greaterEqualThan(IExpr that) {
		COMPARE_TERNARY temp = BooleanFunctions.CONST_GREATER_EQUAL.prepareCompare(this, that);
		return ITernaryComparator.convertToExpr(temp);
	}

	/**
	 * Compare if <code>this > that</code:
	 * <ul>
	 * <li>return F.True if the comparison is <code>true</code></li>
	 * <li>return F.False if the comparison is <code>false</code></li>
	 * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
	 * </ul>
	 * 
	 * @param that
	 * @return <code>F.True, F.False or F.NIL</code
	 */
	default public IExpr greaterThan(IExpr that) {
		COMPARE_TERNARY temp = BooleanFunctions.CONST_GREATER.prepareCompare(this, that);
		return ITernaryComparator.convertToExpr(temp);
	}

	/**
	 * If this object is an instance of <code>IAST</code> get the first element (offset 0) of the <code>IAST</code> list
	 * (i.e. get(0) ).
	 * 
	 * @return the head of the expression, which must not be null.
	 */
	public IExpr head();

	/**
	 * A unique integer ID for the implementation of this expression
	 * 
	 * @return a unique integer id for the implementation of this expression
	 */
	public int hierarchy();

	/**
	 * If this expression unequals <code>F.NIL</code>, invoke the specified consumer with the <code>this</code> object,
	 * otherwise do nothing.
	 *
	 * @param consumer
	 *            block to be executed if this expression unequals <code>F.NIL</code>
	 * @see java.util.Optional#ifPresent(Consumer)
	 */
	default void ifPresent(Consumer<? super IExpr> consumer) {
		consumer.accept(this);
	}

	/**
	 * Return the imaginary part of this expression if possible. Otherwise return <code>Im(this)</code>.
	 * 
	 * @return real part
	 */
	default public IExpr im() {
		return F.eval(F.Im(this));
	}

	/**
	 * Returns an <code>IExpr</code> whose value is <code>(this + 1)</code>. Calculates
	 * <code>F.eval(F.Plus(this, C1))</code> in the common case and uses a specialized implementation for derived number
	 * classes.
	 * 
	 * @return
	 */
	default IExpr inc() {
		return plus(F.C1);
	}

	/**
	 * Return the internal Java form of this expression.
	 * 
	 * @param symbolsAsFactoryMethod
	 *            if <code>true</code> use the <code>F.symbol()</code> method, otherwise print the symbol name.
	 * @param depth
	 *            the recursion depth of this call. <code>0</code> indicates &quot;recurse without a limit&quot;.
	 * @return the internal Java form of this expression
	 */
	default String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return toString();
	}

	/**
	 * Return the internal Java form of this expression.
	 * 
	 * @param symbolsAsFactoryMethod
	 *            if <code>true</code> use the <code>F.symbol()</code> method, otherwise print the symbol name.
	 * @param depth
	 *            the recursion depth of this call. <code>0</code> indicates &quot;recurse without a limit&quot;.
	 * @param useOperators
	 *            use operators instead of function names for representation of Plus, Times, Power,...
	 * @return the internal Java form of this expression
	 */
	default String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators) {
		return toString();
	}

	/**
	 * Return the internal Scala form of this expression.
	 * 
	 * @param symbolsAsFactoryMethod
	 *            if <code>true</code> use the <code>F.symbol()</code> method, otherwise print the symbol name.
	 * @param depth
	 *            the recursion depth of this call. <code>0</code> indicates &quot;recurse without a limit&quot;.
	 * @return the internal Scala form of this expression
	 */
	default String internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
		return toString();
	}

	/**
	 * Returns the multiplicative inverse of this object. It is the object such as
	 * <code>this.times(this.inverse()) == ONE </code>, with <code>ONE</code> being the multiplicative identity.
	 * Calculates <code>F.eval(F.Power(this, F.CN1))</code> in the common case and uses a specialized implmentation for
	 * derived number classes.
	 * 
	 * @return <code>ONE / this</code>.
	 */
	@Override
	default IExpr inverse() {
		return power(F.CN1);
	}

	/**
	 * Test if this expression is the function <code>Abs[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isAbs() {
		return false;
	}

	/**
	 * Test if this expression and all subexpressions are already expanded i.e. all <code>Plus, Times, Power</code>
	 * (sub-)expressions are expanded.
	 * 
	 * @return
	 */
	default boolean isAllExpanded() {
		return true;
	}

	/**
	 * Test if this expression is the <code>Alternatives</code> function
	 * <code>Alternatives[&lt;pattern1&gt;, &lt;pattern2&gt;, ...]</code>
	 * 
	 * @return
	 */
	default boolean isAlternatives() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>And[&lt;arg&gt;,...]</code>
	 * 
	 * @return
	 */
	default boolean isAnd() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>ArcCos[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isArcCos() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>ArcCosh[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isArcCosh() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>ArcSin[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isArcSin() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>ArcSinh[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isArcSinh() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>ArcTan[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isArcTan() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>ArcTanh[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isArcTanh() {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. the function name) at index
	 * position <code>0</code> and some optional <b>argument elements</b> at the index positions <code>1..n</code>.
	 * Therefore this expression is no <b>atomic expression</b>.
	 * 
	 * @return
	 * @see #isAtom()
	 */
	default boolean isAST() {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains the given <b>header element</b> at index position
	 * <code>0</code> and some optional <b>argument elements</b> at the index positions <code>1..(size()-1)</code>.
	 * Therefore this expression is not an <b>atomic expression</b>.
	 * 
	 * @param header
	 *            the header element at position 0, which should be tested
	 * @return
	 * @see #isAtom()
	 * 
	 */
	default boolean isAST(IExpr header) {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains the given <b>header element</b> at index position
	 * <code>0</code> and optional <b>argument elements</b> at the index positions <code>1..(length-1)</code>. Therefore
	 * this expression is not an <b>atomic expression</b>.
	 * 
	 * @param header
	 *            the header element at position 0, which should be tested
	 * @param length
	 *            the size the AST expression must have
	 * @return
	 * @see #isAtom()
	 */
	default boolean isAST(IExpr header, int length) {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains the given <b>header element</b> at index position
	 * <code>0</code> and optional <b>argument elements</b> at the index positions <code>1..(length-1)</code>. Therefore
	 * this expression is not an <b>atomic expression</b>.
	 * 
	 * @param header
	 *            the header element at position 0, which should be tested
	 * @param length
	 *            the size the AST expression must have
	 * @param args
	 *            the arguments of this AST which should be tested, if they are equal, a <code>null</code> value
	 *            argument skips the equals check.
	 * @return
	 * @see #isAtom()
	 */
	default boolean isAST(IExpr header, int length, IExpr... args) {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains the given <b>header element</b> at index position
	 * <code>0</code> and optional <b>argument elements</b> at the index positions <code>1..(length-1)</code>. Therefore
	 * this expression is not an <b>atomic expression</b>.
	 * 
	 * @param header
	 *            the header element at position 0, which should be tested
	 * @param minLength
	 *            the minimum size the AST expression must have
	 * @param maxLength
	 *            the maximum size the AST expression must have
	 * @return
	 * @see #isAtom()
	 */
	default boolean isAST(IExpr header, int minLength, int maxLength) {
		return false;
	}

	/**
	 * <p>
	 * Test if this expression is an AST list, where the string representation of the <b>header element</b> at index
	 * position <code>0</code> equals the given <code>symbol</code> and some optional <b>argument elements</b> at the
	 * index positions <code>1..(size()-1)</code>. Therefore this expression is no <b>atomic expression</b>. Example:
	 * <code>isAST("Sin")</code> gives <code>true</code> for <code>Sin[Pi/2]</code>.
	 * </p>
	 * <b>Note:</b> this is a performance critical method, only use it in special cases like for example UI handling
	 * etc.
	 * 
	 * @param headerStr
	 *            string representation of the <b>header element</b> at index position <code>0</code>
	 * @return
	 * @see #isAtom()
	 * 
	 */
	default boolean isAST(String headerStr) {
		return false;
	}

	/**
	 * Test if this expression is an AST list, where the string representation of the <b>header element</b> at index
	 * position <code>0</code> equals the given <code>symbol</code> and some optional <b>argument elements</b> at the
	 * index positions <code>1..(length-1)</code>. Therefore this expression is no <b>atomic expression</b>. Example:
	 * <code>isAST("Sin", 2)</code> gives <code>true</code> for <code>Sin[0]</code>.
	 * 
	 * @param headerStr
	 *            string representation of the <b>header element</b> at index position <code>0</code>
	 * @param length
	 *            the size the AST expression must have
	 * @return
	 * @see #isAtom()
	 */
	default boolean isAST(String headerStr, int length) {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. the function name) at index
	 * position <code>0</code> and no <b>argument elements</b>. Therefore this expression is no <b>atomic
	 * expression</b>.
	 * 
	 * @return
	 * @see #isAtom()
	 */
	default boolean isAST0() {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. the function name) at index
	 * position <code>0</code> and one <b>argument element</b> at the index position <code>1</code>. Therefore this
	 * expression is no <b>atomic expression</b>.
	 * 
	 * @return
	 * @see #isAtom()
	 */
	default boolean isAST1() {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. the function name) at index
	 * position <code>0</code> and two <b>argument elements</b> at the index positions <code>1, 2</code>. Therefore this
	 * expression is no <b>atomic expression</b>.
	 * 
	 * @return
	 * @see #isAtom()
	 */
	default boolean isAST2() {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. the function name) at index
	 * position <code>0</code> and three <b>argument elements</b> at the index positions <code>1, 2, 3</code>. Therefore
	 * this expression is no <b>atomic expression</b>.
	 * 
	 * @return
	 * @see #isAtom()
	 */
	default boolean isAST3() {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains the given <b>header element</b> at index position
	 * <code>0</code> and optional <b>argument elements</b> at the index positions <code>1..n</code>. <code>n</code>
	 * must be greater equal than the given <code>length</code>. Therefore this expression is no <b>atomic
	 * expression</b>.
	 * 
	 * @param header
	 *            the header element at position 0, which should be tested
	 * @param length
	 *            the size the AST expression must have
	 * @return
	 * @see #isAtom()
	 */
	default boolean isASTSizeGE(IExpr header, int length) {
		return false;
	}

	/**
	 * Test if this expression is an atomic expression (i.e. no AST expression)
	 * 
	 * @return
	 */
	default boolean isAtom() {
		return true;
	}

	/**
	 * Test if this expression is a <code>Blank[]</code> object
	 * 
	 * @return
	 */
	default boolean isBlank() {
		return false;
	}

	/**
	 * Test if this expression is a symbol (instanceof IBuiltInSymbol)
	 * 
	 * @return
	 */
	default boolean isBuiltInSymbol() {
		return false;
	}

	/**
	 * Test if this expression is a symbolic complex number (i.e. <code>instanceof IComplex</code>)
	 * 
	 * @return
	 */
	default boolean isComplex() {
		return this instanceof IComplex;
	}

	/**
	 * Test if this expression is representing ComplexInfinity (i.e. DirectedInfinity[])
	 * 
	 * @return
	 */
	default boolean isComplexInfinity() {
		return false;
	}

	/**
	 * Test if this expression is a numeric complex number (i.e. <code>instanceof IComplexNum</code>)
	 * 
	 * @return
	 */
	default boolean isComplexNumeric() {
		return this instanceof IComplexNum;
	}

	/**
	 * Test if this expression is the Condition function <code>Condition[&lt;arg1&gt;, &lt;arg2&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isCondition() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>Conjugate[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isConjugate() {
		return false;
	}

	/**
	 * Test if this expression is a symbol with attribute <code>Constant</code>. Therefore numbers return
	 * <code>false</code> for this method!
	 * 
	 * @return
	 * @see #isRealResult()
	 * @see #isNumericFunction()
	 */
	default boolean isConstant() {
		return false;
	}

	/**
	 * Test if this expression is a <code>IBuiltInSymbol</code> symbol and the evaluator implements
	 * <code>ICoreFunctionEvaluator</code> (see package <code>org.matheclipse.core.builtin.function</code>).
	 * 
	 * @return
	 */
	default boolean isCoreFunctionSymbol() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>Cos[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isCos() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>Cosh[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isCosh() {
		return false;
	}

	/**
	 * <p>
	 * Test if this expression is a <code>Derivative[number, ...][symbol][arg,...]</code> or
	 * <code>Derivative[number][symbol]</code> expression and return the corresponding <code>IAST</code> structures.
	 * <ul>
	 * <li>The expression at index <code>[0]</code> contains the <code>Derivative[number, ...]</code> AST part.</li>
	 * <li>The expression at index <code>[1]</code> contains the <code>Derivative[...][symbol]</code> AST part.</li>
	 * <li>The expression at index <code>[2]</code> contains the <code>Derivative[...][...][arg, ...]</code> AST part,
	 * if available.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * <b>Note:</b> the result at index <code>[2]</code> maybe <code>null</code> , if no argument is available.
	 * </p>
	 * 
	 * @return <code>null</code> if the expression is not a <code>Derivative[number, ...][symbol][arg, ...]</code> or
	 *         <code>Derivative[number, ...][symbol]</code> expression.
	 */
	default IAST[] isDerivative() {
		return null;
	}

	/**
	 * <p>
	 * Test if this expression is a <code>Derivative[number][symbol][arg]</code> or
	 * <code>Derivative[number][symbol]</code> expression with one argument and return the corresponding
	 * <code>IAST</code> structures.
	 * <ul>
	 * <li>The expression at index <code>[0]</code> contains the <code>Derivative[number,...]</code> AST part.</li>
	 * <li>The expression at index <code>[1]</code> contains the <code>Derivative[...][symbol]</code> AST part.</li>
	 * <li>The expression at index <code>[2]</code> contains the <code>Derivative[...][...][arg]</code> AST part, if
	 * available.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * <b>Note:</b> the result at index <code>[2]</code> maybe <code>null</code> , if no argument is available.
	 * </p>
	 * 
	 * @return <code>null</code> if the expression is not a <code>Derivative[number][symbol][arg]</code> or
	 *         <code>Derivative[number][symbol]</code> expression.
	 */
	default IAST[] isDerivativeAST1() {
		return null;
	}

	/**
	 * Test if this expression is representing a DirectedInfinity (i.e. <code>Infinity->DirectedInfinity[1]</code>,
	 * <code>-Infinity->DirectedInfinity[-1]</code>, <code>ComplexInfinity->DirectedInfinity[]</code>)
	 * 
	 * @return
	 */
	default boolean isDirectedInfinity() {
		return false;
	}

	/**
	 * Test if this expression is representing a DirectedInfinity (i.e. <code>Infinity->DirectedInfinity[1]</code>,
	 * <code>-Infinity->DirectedInfinity[-1]</code>, <code>ComplexInfinity->DirectedInfinity[]</code>)
	 * 
	 * @param x
	 * @return
	 */
	default boolean isDirectedInfinity(IExpr x) {
		return false;
	}

	/**
	 * Test if this expression equals <code>E</code> (base of the natural logarithm; approximately equal to 2.71828...)
	 * in symbolic or numeric mode.
	 * 
	 * <br>
	 * See <a href="http://en.wikipedia.org/wiki/E_%28mathematical_constant%29">e (mathematical constant)</a>
	 * 
	 * @return
	 */
	default boolean isE() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>Equal[&lt;arg1&gt;, &lt;arg2&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isEqual() {
		return false;
	}

	/**
	 * Test if this expression is an exact number. I.e. an instance of type <code>IRational</code> or
	 * <code>IComplex</code>.
	 * 
	 * @return
	 */
	default boolean isExactNumber() {
		return this instanceof IRational || this instanceof IComplex;
	}

	/**
	 * Test if this expression is the <code>Except</code> function <code>Except[&lt;pattern1&gt;]</code> or
	 * <code>Except[&lt;pattern1&gt;, &lt;pattern2&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isExcept() {
		return false;
	}

	/**
	 * Test if this expression is already expanded i.e. <code>Plus, Times, Power</code> expression is expanded.
	 * 
	 * @return
	 */
	default boolean isExpanded() {
		return true;
	}

	/**
	 * Test if this expression equals the symbol "False"
	 * 
	 * @return
	 */
	default boolean isFalse() {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. a function symbol like for
	 * example <code>Dot, Plus or Times</code>) with attribute <code>Flat</code> at index position <code>0</code> and
	 * some optional <b>argument elements</b> at the index positions <code>1..(size()-1)</code>. Examples for
	 * <code>Flat</code> functions are <code>Dot[], Plus[] or Times[]</code>. Therefore this expression is no <b>atomic
	 * expression</b>.
	 * 
	 * @return
	 * @see #isAtom()
	 * 
	 */
	default boolean isFlatAST() {
		return false;
	}

	/**
	 * Test if this expression is a fractional number, but no integer number.
	 * 
	 * @return
	 */
	default boolean isFraction() {
		return this instanceof IFraction;
	}

	/**
	 * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the expression itself, did not
	 * match the given pattern. Calls <code>isFree(pattern, true)</code>.
	 * 
	 * @param pattern
	 *            a pattern-matching expression
	 * @return
	 */
	default boolean isFree(IExpr pattern) {
		return isFree(pattern, true);
	}

	/**
	 * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the expression itself, did not
	 * match the given pattern.
	 * 
	 * @param pattern
	 *            a pattern-matching expression
	 * @param heads
	 *            if set to <code>false</code>, only the arguments of an IAST should be tested and not the
	 *            <code>Head[]</code> element.
	 * @return
	 */
	default boolean isFree(IExpr pattern, boolean heads) {
		final IPatternMatcher matcher = new PatternMatcher(pattern);
		return !matcher.test(this);
	}

	/**
	 * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the expression itself, did not
	 * satisfy the given unary predicate.
	 * 
	 * @param predicate
	 *            a unary predicate
	 * @param heads
	 *            if set to <code>false</code>, only the arguments of an IAST should be tested and not the
	 *            <code>Head[]</code> element.
	 * @return
	 */
	default boolean isFree(Predicate<IExpr> predicate, boolean heads) {
		return !predicate.test(this);
	}

	/**
	 * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the expression itself, aren't
	 * ASTs with a head which match the given pattern.
	 * 
	 * @param pattern
	 *            a pattern-matching expression
	 * @return
	 */
	default boolean isFreeAST(IExpr pattern) {
		return true;
	}

	/**
	 * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the expression itself, aren't
	 * ASTs with a head which match the given predicate.
	 * 
	 * @param predicate
	 *            a unary predicate
	 * @return
	 */
	default boolean isFreeAST(Predicate<IExpr> predicate) {
		return true;
	}

	/**
	 * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the expression itself, are no
	 * pattern objects.
	 * 
	 * @return <code>true</code> if the expression contains no <code>IPatternObject</code>.
	 */
	default boolean isFreeOfPatterns() {
		return true;
	}

	/**
	 * Test if this expression is a <code>Function( arg1 )</code> expression with at least 1 argument.
	 * 
	 * @return
	 */
	default boolean isFunction() {
		return false;
	}

	/**
	 * Compares this expression with the specified expression for order. Returns true if this expression is canonical
	 * greater than or equal to the specified expression (&lt;= relation).
	 * 
	 * @param expr
	 *            an expression to compare with
	 * @return true if this expression is canonical greater than or equal to the specified expression.
	 */
	default boolean isGEOrdered(IExpr expr) {
		return compareTo(expr) >= 0;
	}

	/**
	 * Compares this expression with the specified expression for order. Returns true if this expression is canonical
	 * greater than the specified expression (&lt; relation).
	 * 
	 * @param expr
	 *            an expression to compare with
	 * @return true if this expression is canonical greater than the specified expression.
	 */
	default boolean isGTOrdered(IExpr expr) {
		return compareTo(expr) > 0;
	}

	/**
	 * Test if this expression is th symbol <code>Hold</code> or <code>HoldForm</code>
	 * 
	 * @return
	 */
	default boolean isHoldOrHoldFormOrDefer() {
		return false;
	}

	/**
	 * Test if this expression is representing <code>I</code>.
	 * 
	 * @return
	 */
	default boolean isImaginaryUnit() {
		return false;
	}

	/**
	 * Test if this expression is representing <code>Indeterminate</code>
	 * 
	 * @return
	 */
	default boolean isIndeterminate() {
		return false;
	}

	/**
	 * Test if this expression is an inexact number. I.e. an instance of type <code>INum</code> or
	 * <code>IComplexNum</code>.
	 * 
	 * @return
	 */
	default boolean isInexactNumber() {
		return this instanceof INum || this instanceof IComplexNum;
	}

	/**
	 * Test if this expression is representing <code>Infinity</code> (i.e. <code>Infinity->DirectedInfinity[1]</code>)
	 * 
	 * @return
	 */
	default boolean isInfinity() {
		return false;
	}

	/**
	 * Test if this expression is a integer number
	 * 
	 * @return
	 */
	default boolean isInteger() {
		return this instanceof IInteger;
	}

	/**
	 * Test if this expression is a integer function (i.e. a number, a symbolic constant or an integer function where
	 * all arguments are also &quot;integer functions&quot;)
	 * 
	 * @return <code>true</code>, if the given expression is a integer function or value.
	 * @see #isRealResult()
	 */
	default boolean isIntegerResult() {
		if (F.True.equals(AbstractAssumptions.assumeInteger(this))) {
			return true;
		}
		return this instanceof IInteger;
	}

	/**
	 * Test if this expression is an interval expression with one or more <code>List[min, max]</code> arguments
	 * <code>Interval[{min1, max1}, {min2, max2}, ...]</code> which represent the union of the interval ranges.
	 * 
	 * @return
	 */
	default boolean isInterval() {
		return false;
	}

	/**
	 * Test if this expression is an interval expression with one <code>List[min, max]</code> argument
	 * <code>Interval[{min, max}]</code>
	 * 
	 * @return
	 */
	default boolean isInterval1() {
		return false;
	}

	/**
	 * Compares this expression with the specified expression for order. Returns true if this expression is canonical
	 * less than or equal to the specified expression (&lt;= relation).
	 * 
	 * @param expr
	 *            an expression to compare with
	 * @return true if this expression is canonical less than or equal to the specified expression.
	 */
	default boolean isLEOrdered(IExpr expr) {
		return compareTo(expr) <= 0;
	}

	/**
	 * Test if this expression is a list (i.e. an AST with head List)
	 * 
	 * @return
	 */
	default boolean isList() {
		return false;
	}

	/**
	 * Test if this expression is a list of lists
	 * 
	 * @return
	 * @see #isList()
	 * @see #isMatrix(boolean)
	 * @see #isVector()
	 */
	default boolean isListOfLists() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>Log[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isLog() {
		return false;
	}

	/**
	 * Compares this expression with the specified expression for order. Returns true if this expression is canonical
	 * less than the specified expression (&lt; relation).
	 * 
	 * @param expr
	 *            an expression to compare with
	 * @return true if this expression is canonical less than the specified expression.
	 */
	default boolean isLTOrdered(IExpr expr) {
		return compareTo(expr) < 0;
	}

	/**
	 * Test if this expression is a machine-precision (Java double type) real or complex number. I.e. an instance of
	 * type <code>Num</code> or <code>ComplexNum</code>.
	 * 
	 * @return
	 */
	default boolean isMachineNumber() {
		return this instanceof Num || this instanceof ComplexNum;
	}

	/**
	 * Test if this expression is a matrix and return the dimensions as array [row-dimension, column-dimension]. This
	 * expression is only a matrix, if all elements are lists with the header <code>List</code> and have the same size.
	 * 
	 * @param setMatrixFormat
	 *            TODO
	 * 
	 * @return <code>null</code> if the expression is not a matrix
	 */
	default int[] isMatrix() {
		return isMatrix(true);
	}

	/**
	 * Test if this expression is a matrix and return the dimensions as array [row-dimension, column-dimension]. This
	 * expression is only a matrix, if all elements are lists with the header <code>List</code> and have the same size.
	 * 
	 * @param setMatrixFormat
	 *            set the <code>IAST.IS_MATRIX</code> flag for formatting as a matrix.
	 * @return <code>null</code> if the expression is not a matrix
	 */
	default int[] isMatrix(boolean setMatrixFormat) {
		// default: no matrix
		return null;
	}

	/**
	 * Returns <code>true</code>, if <b>at least one of the elements</b> in the subexpressions or the expression itself,
	 * match the given pattern.
	 * 
	 * 
	 * @param pattern
	 *            a pattern-matching expression
	 * @param heads
	 *            if set to <code>false</code>, only the arguments of an IAST should be tested and not the
	 *            <code>Head[]</code> element.
	 * @return
	 */
	default boolean isMember(IExpr pattern, boolean heads) {
		final IPatternMatcher matcher = new PatternMatcher(pattern);
		return isMember(matcher, heads);
	}

	/**
	 * Returns <code>true</code>, if <b>at least one of the elements</b> in the subexpressions or the expression itself,
	 * satisfy the given unary predicate.
	 * 
	 * @param predicate
	 *            a unary predicate
	 * @param heads
	 *            if set to <code>false</code>, only the arguments of an IAST should be tested and not the
	 *            <code>Head[]</code> element.
	 * @return
	 */
	default boolean isMember(Predicate<IExpr> predicate, boolean heads) {
		return predicate.test(this);
	}

	/**
	 * Test if this expression equals <code>-1</code> in symbolic or numeric mode.
	 * 
	 * @return
	 */
	default boolean isMinusOne() {
		return false;
	}

	/**
	 * Test if this expression is the Module function <code>Module[&lt;arg1&gt;, &lt;arg2&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isModuleOrWith() {
		return false;
	}
	
	default boolean isModuleOrWithCondition() {
		return false;
	}

	/**
	 * Test if this object is a negative signed number. For an <code>IAST</code> object the method checks, if it is a
	 * numeric constant. If the <code>IAST</code> object evaluates to a negative numeric expression this method returns
	 * <code>true</code>.
	 * 
	 * @return <code>true</code>, if <code>this < 0</code>; <code>false</code> in all other case.
	 */
	default boolean isNegative() {
		return false;
	}

	/**
	 * Test if this expression is representing <code>-I</code>.
	 * 
	 * @return
	 */
	default boolean isNegativeImaginaryUnit() {
		return false;
	}

	/**
	 * Test if this expression is representing <code>-Infinity</code> (i.e.
	 * <code>-Infinity->DirectedInfinity[-1]</code>)
	 * 
	 * @return
	 */
	default boolean isNegativeInfinity() {
		return false;
	}

	/**
	 * Test if this expression has a negative result (i.e. less than 0) or is assumed to be negative.
	 * 
	 * @return <code>true</code>, if the given expression is a negative function or value.
	 * @see #isRealResult()
	 */
	default boolean isNegativeResult() {
		return AbstractAssumptions.assumeNegative(this);
	}

	/**
	 * Check if the expression is a negative signed expression. This method is used in output forms of
	 * <code>Plus[...]</code> expressions.
	 * 
	 * @param expr
	 *            the expression which should be analyzed for a negative sign
	 * @return <code>true</code> if the expression is a negative signed expression
	 */
	default boolean isNegativeSigned() {
		if (isNumber()) {
			if (((INumber) this).complexSign() < 0) {
				return true;
			}
		} else if (isTimes()) {
			IExpr arg1 = ((IAST) this).arg1();
			if (arg1.isNumber()) {
				if (((INumber) arg1).complexSign() < 0) {
					return true;
				}
			} else if (arg1.isNegativeInfinity()) {
				return true;
			}
		} else if (isPlus()) {
			IExpr arg1 = ((IAST) this).arg1();
			if (arg1.isNumber()) {
				if (((INumber) arg1).complexSign() < 0) {
					return true;
				}
			} else if (arg1.isNegativeInfinity()) {
				return true;
			}
		} else if (isNegativeInfinity()) {
			return true;
		}

		return false;
	}

	/**
	 * Test if this expression has a non-negative result (i.e. greater equal 0) or is assumed to be non-negative.
	 * 
	 * @return <code>true</code>, if the given expression is a non-negative function or value.
	 * @see #isRealResult()
	 */
	default boolean isNonNegativeResult() {
		return AbstractAssumptions.assumeNonNegative(this);
	}

	/**
	 * Test if this expression is the function <code>Not[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isNot() {
		return false;
	}

	/**
	 * Test if this expression is a number. I.e. an instance of type <code>INumber</code>.
	 * 
	 * @return
	 */
	default boolean isNumber() {
		return this instanceof INumber;
	}

	/**
	 * Check if this expression equals an <code>IInteger</code> value. The value of an <code>INum</code> or the value of
	 * an <code>IInteger</code> object can be equal to <code>value</code>.
	 * 
	 * @param value
	 * @return
	 * @throws ArithmeticException
	 */
	default boolean isNumEqualInteger(IInteger value) throws ArithmeticException {
		return false;
	}

	/**
	 * Check if this expression equals an <code>IRational</code> value. The value of an <code>IInteger</code>,
	 * <code>IFraction</code> or the value of an <code>INum</code> object can be equal to <code>value</code>.
	 * 
	 * @param value
	 * @return
	 * @throws ArithmeticException
	 * @see #isRational()
	 */
	default boolean isNumEqualRational(IRational value) throws ArithmeticException {
		return false;
	}

	/**
	 * Test if this expression is a numeric number (i.e. an instance of type <code>INum</code> or type
	 * <code>IComplexNum</code>.
	 * 
	 * @return
	 */
	default boolean isNumeric() {
		return this instanceof INum || this instanceof IComplexNum;
	}

	/**
	 * Test if this expression is a numeric number (i.e. an instance of type <code>INum</code> or type
	 * <code>IComplexNum</code>), an <code>ASTRealVector</code> or an <code>ASTRealMatrix</code>.
	 * 
	 * @return
	 */
	default boolean isNumericArgument() {
		return this instanceof INum || this instanceof IComplexNum || this instanceof ASTRealVector
				|| this instanceof ASTRealMatrix;
	}

	/**
	 * Test if this expression is a numeric function (i.e. a number, a symbolic constant or a function (with attribute
	 * NumericFunction) where all arguments are also &quot;numeric functions&quot;)
	 * 
	 * @return <code>true</code>, if the given expression is a numeric function or value.
	 * @see #isRealResult()
	 */
	default boolean isNumericFunction() {
		return isNumber() || isConstant();
	}

	/**
	 * Test if this expression contains a numeric number (i.e. of type <code>INum</code> or <code>IComplexNum</code>.
	 * 
	 * @return <code>true</code>, if the given expression contains numeric number (i.e. of type <code>INum</code> or
	 *         <code>IComplexNum</code>.
	 * @see #isRealResult()
	 */
	default boolean isNumericMode() {
		return isNumeric();
	}

	/**
	 * Check if this expression represents an <code>int</code> value. The value of an <code>INum</code> object can be an
	 * <code>int</code> value.
	 * 
	 * @return
	 */
	default boolean isNumIntValue() {
		return false;
	}

	/**
	 * Test if this expression equals <code>1</code> in symbolic or numeric mode.
	 * 
	 * @return
	 */
	default boolean isOne() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated use {@link #isOne()} instead.
	 */
	@Deprecated
	@Override
	default boolean isONE() {
		return isOne();
	}

	/**
	 * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. a function symbol like for
	 * example <code>Plus or Times</code>) with attribute <code>OneIdentity</code> at index position <code>0</code> and
	 * exactly <b>one argument</b> at the index position <code>1</code>. Examples for <code>OneIdentity</code> functions
	 * are <code>Plus[] or Times[]</code>. Therefore this expression is no <b>atomic expression</b>.
	 * 
	 * @return
	 * @see #isAtom()
	 */
	default boolean isOneIdentityAST1() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>Or[&lt;arg&gt;,...]</code>
	 * 
	 * @return
	 */
	default boolean isOr() {
		return false;
	}

	/**
	 * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. a function symbol like for
	 * example <code>Plus or Times</code>) with attribute <code>Orderless</code> at index position <code>0</code> and
	 * some optional <b>argument elements</b> at the index positions <code>1..n</code>. Examples for
	 * <code>Orderless</code> functions are <code>Plus[] or Times[]</code>. Therefore this expression is no <b>atomic
	 * expression</b>.
	 * 
	 * @return
	 * @see #isAtom()
	 */
	default boolean isOrderlessAST() {
		return false;
	}

	/**
	 * Test if this expression is a <code>Pattern[symbol]</code> object
	 * 
	 * @return
	 */
	default boolean isPattern() {
		return false;
	}

	/**
	 * Return <code>true</code>, if the expression is a pattern object with an associated default value (for example
	 * <code>0</code> is the default value for the addition expression <code>x_+y_.</code>)
	 * 
	 * @return
	 */
	default boolean isPatternDefault() {
		return false;
	}

	/**
	 * Test if this expression or a subexpression is a pattern object. Used in pattern-matching; checks flags in
	 * <code>IAST</code> with flag <code>IAST.CONTAINS_PATTERN_EXPR</code>.
	 * 
	 * @return
	 */
	default boolean isPatternExpr() {
		return false;
	}

	/**
	 * Return <code>true</code>, if the expression is a pattern object with an associated optional value (for example
	 * <code>value</code> is the default value for the expression <code>f[x_, y_:value]</code>)
	 * 
	 * @return
	 */
	default boolean isPatternOptional() {
		return false;
	}

	/**
	 * Test if this expression is a pattern sequence object
	 * 
	 * @return
	 */
	default boolean isPatternSequence() {
		return false;
	}

	/**
	 * Test if this expression is the <code>Except</code> function <code>Except[&lt;pattern1&gt;]</code> or
	 * <code>Except[&lt;pattern1&gt;, &lt;pattern2&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isPatternTest() {
		return false;
	}
	
	/**
	 * Test if this expression equals <code>Pi</code> (the ratio of a circle's circumference to its diameter, approx.
	 * 3.141592...) in symbolic or numeric mode.
	 * 
	 * <br>
	 * See <a href="http://en.wikipedia.org/wiki/Pi">Pi</a>
	 * 
	 * @return
	 */
	default boolean isPi() {
		return false;
	}

	/**
	 * Test if this expression is the addition function <code>Plus[&lt;arg1&gt;, &lt;arg2&gt;, ...]</code> with at least
	 * 2 arguments.
	 * 
	 * @return
	 */
	default boolean isPlus() {
		return false;
	}

	/**
	 * Test if this expression is a <code>Plus, Power or Times</code> function.
	 * 
	 * @return
	 */
	default boolean isPlusTimesPower() {
		return false;
	}

	/**
	 * <p>
	 * Test if this expression is a polynomial for the given list of <code>variables</code>.
	 * </p>
	 * <b>Note:</b> if the list contains no argument, this method returns <code>true</code> for a <code>Symbol</code>
	 * expression.
	 * 
	 * @param variables
	 *            a list of variables or an empty list
	 * @return <code>true</code> if this expression is a polynomial; <code>false</code>otherwise
	 * 
	 */
	default boolean isPolynomial(IAST variables) {
		return isNumber();
	}

	/**
	 * <p>
	 * Test if this expression is a polynomial for the given <code>variable</code>.
	 * </p>
	 * <b>Note:</b> if the variable is set to <code>null</code>, this method returns <code>true</code> for a
	 * <code>Symbol</code> expression.
	 * 
	 * @param variable
	 *            the variable of the polynomial
	 * @return
	 */
	default boolean isPolynomial(@Nullable IExpr variable) {
		return isNumber();
	}

	/**
	 * Test if this expression is a polynomial of <code>maxDegree</code> (i.e. the maximum exponent <= maxDegree) for
	 * the given <code>variable</code>.
	 * 
	 * @param variable
	 *            the variable of the polynomial
	 * @param maxDegree
	 *            the maximum degree of the polynomial; maxDegree must be greater 0
	 * @return
	 */
	default boolean isPolynomialOfMaxDegree(ISymbol variable, long maxDegree) {
		return isPolynomial(variable);
	}

	/**
	 * Test if this object is a positive signed number. For an <code>IAST</code> object the method checks, if it is a
	 * numeric constant. If the <code>IAST</code> object evaluates to a positive numeric expression this method returns
	 * <code>true</code>.
	 * 
	 * @return <code>true</code>, if <code>this > 0</code>; <code>false</code> in all other case.
	 */
	default boolean isPositive() {
		return false;
	}

	/**
	 * Test if this expression has a positive result (i.e. greater than 0) or is assumed to be positive.
	 * 
	 * @return <code>true</code>, if the given expression is a positive function or value.
	 * @see #isRealResult()
	 */
	default boolean isPositiveResult() {
		return AbstractAssumptions.assumePositive(this);
	}

	/**
	 * Test if this expression is the function <code>Power[&lt;arg1&gt;, &lt;arg2&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isPower() {
		return false;
	}

	/**
	 * Return {@code true} if this expression unequals <code>F.NIL</code>, otherwise {@code false}. This method is
	 * similar to <code>java.util.Optional#isPresent()</code>.
	 *
	 * @return {@code true} if the expression unequals <code>F.NIL</code>, otherwise {@code false}.
	 * @see java.util.Optional#isPresent()
	 */
	default boolean isPresent() {
		return true;
	}

	/**
	 * Test if this expression is a rational number, i.e. integer or fraction number.
	 * 
	 * @return
	 * @see #isNumEqualRational(IRational)
	 */
	default boolean isRational() {
		return this instanceof IRational;
	}

	/**
	 * Test if this expression is a rational function (i.e. a number, a symbolic constant or an rational function where
	 * all arguments are also &quot;rational functions&quot;)
	 * 
	 * @return <code>true</code>, if the given expression is a rational function or value.
	 * @see #isRealResult()
	 */
	default boolean isRationalResult() {
		if (F.True.equals(AbstractAssumptions.assumeRational(this))) {
			return true;
		}
		return this instanceof IRational;
	}

	/**
	 * Test if this expression equals the rational number <code>value</code> in symbolic or numeric mode.
	 * 
	 * @param value
	 *            the rational number
	 * @return
	 */
	default boolean isRationalValue(IRational value) {
		return false;
	}

	/**
	 * Test if this expression is a real matrix (i.e. an ASTRealMatrix) or a <code>List[List[...],...,List[...]]</code>
	 * matrix with elements of type <code>org.matheclipse.core.expression.Num</code>.
	 * 
	 * @return
	 */
	default boolean isRealMatrix() {
		return false;
	}

	/**
	 * Test if this expression is a number with no imaginary component. I.e. an instance of type <code>IRational</code>
	 * or <code>INum</code>.
	 * 
	 * @return
	 */
	default boolean isRealNumber() {
		return this instanceof IRational || this instanceof INum;
	}

	/**
	 * Test if this expression is a real function (i.e. a number, a symbolic constant or an integer function where all
	 * arguments are also &quot;reals functions&quot;)
	 * 
	 * @return <code>true</code>, if the given expression is a real function or value.
	 * @see #isIntegerResult
	 */
	default boolean isRealResult() {
		if (F.True.equals(AbstractAssumptions.assumeReal(this))) {
			return true;
		}
		return this instanceof ISignedNumber;
	}

	/**
	 * Test if this expression is a real vector (i.e. an ASTRealVector) or a <code>List[...]</code> with elements of
	 * type <code>org.matheclipse.core.expression.Num</code>.
	 * 
	 * @return
	 */
	default boolean isRealVector() {
		return false;
	}

	/**
	 * Test if this expression is of the form <code>Rule[&lt;arg1&gt;, &lt;arg2&gt;]</code>.
	 * 
	 * @return
	 */
	default boolean isRule() {
		return false;
	}

	/**
	 * Test if this expression is of the form <code>Rule[&lt;arg1&gt;, &lt;arg2&gt;]</code> or
	 * <code>RuleDelayed[&lt;arg1&gt;, &lt;arg2&gt;]</code>.
	 * 
	 * @return
	 */
	default boolean isRuleAST() {
		return false;
	}

	/**
	 * Test if this expression is of the form <code>RuleDelayed[&lt;arg1&gt;, &lt;arg2&gt;]</code>.
	 * 
	 * @return
	 */
	default boolean isRuleDelayed() {
		return false;
	}

	/**
	 * Test if this expression equals the given expression. If the compared expressions are of the same numeric type,
	 * they are equal to a given EPSILON
	 * 
	 * @param expression
	 * @return
	 */
	default boolean isSame(IExpr expression) {
		return isSame(expression, Config.DOUBLE_EPSILON);
	}

	/**
	 * Test if this expression equals the given expression. If the compared expressions are of the same numeric type,
	 * they are equal to a given EPSILON
	 * 
	 * @param expression
	 * @param epsilon
	 * @return
	 */
	default boolean isSame(IExpr expression, double epsilon) {
		return equals(expression);
	}

	/**
	 * Check if the object at index 0 (i.e. the head of the list) is the same object as <code>head</code> and if the
	 * size of the list is greater or equal <code>length</code>.
	 * 
	 * @param head
	 *            object to compare with element at location <code>0</code>
	 * @param length
	 * @return
	 */
	default boolean isSameHeadSizeGE(IExpr head, int length) {
		return false;
	}

	/**
	 * Test if this expression is a sequence (i.e. an AST with head Sequence)
	 * 
	 * @return
	 */
	default boolean isSequence() {
		return false;
	}

	/**
	 * Test if this expression is a signed number. I.e. an instance of type <code>ISignedNumber</code>.
	 * 
	 * @return
	 */
	default boolean isSignedNumber() {
		return this instanceof ISignedNumber;
	}

	/**
	 * Test if this expression is a <code>IBuiltInSymbol</code> symbol and the evaluator implements the
	 * <code>ISignedNumberConstant</code> interface (see package <code>org.matheclipse.core.builtin.constant</code>).
	 * 
	 * @return
	 */
	default boolean isSignedNumberConstant() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>Sin[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isSin() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>Sinh[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isSinh() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>Slot[&lt;integer-value&gt;]</code> (i.e. #1, #2, #3,....)
	 * 
	 * @return
	 */
	default boolean isSlot() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>SlotSequence[&lt;integer-value&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isSlotSequence() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>Span[...]</code> with 2 or 3 arguments.
	 * 
	 * @param size
	 *            the size of an AST for which <code>Span[]</code> should be applied.
	 * @return <code>null</code> if this is no <code>Span[...]</code> expression.
	 * @throws WrongArgumentType
	 */
	default int[] isSpan(int size) {
		return null;
	}

	/**
	 * Test if this expression is a string (instanceof IStringX)
	 * 
	 * @return
	 */
	default boolean isString() {
		return this instanceof IStringX;
	}

	/**
	 * Test if this expression is a symbol (instanceof ISymbol)
	 * 
	 * @return
	 */
	default boolean isSymbol() {
		return this instanceof ISymbol;
	}

	/**
	 * Test if this expression is a symbol (instanceof ISymbol) or a pattern object (instanceof IPatternObject)
	 * 
	 * @return
	 */
	default boolean isSymbolOrPattern() {
		return this instanceof ISymbol || this instanceof IPatternObject;
	}

	/**
	 * Test if this expression is the function <code>TAn[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isTan() {
		return false;
	}

	/**
	 * Test if this expression is the function <code>Tanh[&lt;arg&gt;]</code>
	 * 
	 * @return
	 */
	default boolean isTanh() {
		return false;
	}

	/**
	 * Test if this expression is the multiplication function <code>Times[&lt;arg1&gt;, &lt;arg2&gt;, ...]</code> with
	 * at least 2 arguments.
	 * 
	 * @return
	 */
	default boolean isTimes() {
		return false;
	}

	/**
	 * Test if this expression equals the symbol <code>True</code>.
	 * 
	 * @return <code>true</code> if the expression equals symbol <code>True</code> and <code>false</code> in all other
	 *         cases
	 */
	default boolean isTrue() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	default boolean isUnit() {
		return true;
	}

	/**
	 * Returns <code>true</code>, if this symbol or ast expression is bound to a value (i.e. the evaluation returns an
	 * <i>assigned</i> value).
	 * 
	 * @return
	 */
	default boolean isValue() {
		return false;
	}

	/**
	 * Test if this expression is a symbol which doesn't has attribute <code>Constant</code>.
	 * 
	 * @return
	 * @see #isConstant()
	 * @see #isSymbol()
	 */
	default boolean isVariable() {
		return false;
	}

	/**
	 * Test if this expression is a vector and return the dimension of the vector. This expression is only a vector, if
	 * the expression is a <code>List(...)</code> and no element is itself a <code>List(...)</code>.
	 * 
	 * @return <code>-1</code> if the expression is no vector or <code>size()-1</code> of this vector AST.
	 */
	default int isVector() {
		// default: no vector
		return -1;
	}

	/**
	 * Test if this expression equals <code>0</code> in symbolic or numeric mode.
	 * 
	 * @return
	 */
	default boolean isZero() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated use {@link #isZero()} instead.
	 */
	@Deprecated
	@Override
	default boolean isZERO() {
		return isZero();
	}

	/**
	 * Count the number of leaves of this expression.
	 * 
	 * @return
	 */
	default long leafCount() {
		return isAtom() ? 1L : 0L;
	}

	/**
	 * Count the number of leaves of this expression; for integer numbers in exact integer, fractional and complex
	 * numbers count the digits of the integers. This function is used in <code>Simplify</code> as the default
	 * &quot;complexity function&quot;.
	 * 
	 * @return
	 */
	default long leafCountSimplify() {
		return leafCount();
	}

	/**
	 * Compare if <code>this <= that</code:
	 * <ul>
	 * <li>return F.True if the comparison is <code>true</code></li>
	 * <li>return F.False if the comparison is <code>false</code></li>
	 * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
	 * </ul>
	 * 
	 * @param that
	 * @return <code>F.True, F.False or F.NIL</code
	 */
	default public IExpr lessEqualThan(IExpr that) {
		COMPARE_TERNARY temp = BooleanFunctions.CONST_LESS_EQUAL.prepareCompare(this, that);
		return ITernaryComparator.convertToExpr(temp);
	}

	/**
	 * Compare if <code>this < that</code:
	 * <ul>
	 * <li>return F.True if the comparison is <code>true</code></li>
	 * <li>return F.False if the comparison is <code>false</code></li>
	 * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
	 * </ul>
	 * 
	 * @param that
	 * @return <code>F.True, F.False or F.NIL</code
	 */
	default public IExpr lessThan(IExpr that) {
		COMPARE_TERNARY temp = BooleanFunctions.CONST_LESS.prepareCompare(this, that);
		return ITernaryComparator.convertToExpr(temp);
	}

	/**
	 * If this is a <code>Interval[{lower, upper}]</code> expression return the <code>lower</code> value. If this is a
	 * <code>ISignedNumber</code> expression return <code>this</code>.
	 * 
	 * @return <code>F.NIL</code> if this expression is no interval and no signed number.
	 */
	default public IExpr lower() {
		return F.NIL;
	}

	/**
	 * Returns an <code>IExpr</code> whose value is <code>(this - that)</code>. Calculates
	 * <code>F.eval(F.Plus(this, F.Times(F.CN1, that)))</code> in the common case and uses a specialized implementation
	 * for derived number classes.
	 * 
	 * @param that
	 * @return
	 */
	default IExpr minus(final IExpr that) {
		return subtract(that);
	}

	default IExpr mod(final IExpr that) {
		return F.Mod(this, that);
	}

	/**
	 * Additional multiply method which works like <code>times()</code> to fulfill groovy's method signature
	 * 
	 * @param that
	 * @return
	 * @see IExpr#times(IExpr)
	 */
	@Override
	default IExpr multiply(final IExpr that) {
		return times(that);
	}

	@Override
	default public IExpr multiply(int n) {
		return times(F.integer(n));
	}

	/** {@inheritDoc} */
	@Override
	default IExpr negate() {
		return opposite();
	}

	/**
	 * Additional negative method, which works like opposite to fulfill groovy's method signature
	 * 
	 * @return
	 * @see #opposite()
	 */
	default IExpr negative() {
		return opposite();
	}

	/**
	 * Returns an <code>IExpr</code> whose value is <code>(-1) * this</code>. Calculates
	 * <code>F.eval(F.Times(F.CN1, this))</code> in the common case and uses a specialized implementation for derived
	 * number classes.
	 * 
	 * @return
	 * @see #negative()
	 */
	default IExpr opposite() {
		return times(F.CN1);
	}

	/**
	 * The <code>ExprNull.NIL#optional()</code> method always returns <code>that</code>. All other objects which
	 * implement this method returns <code>that</code> if <code>that!=null</code> or <code>this</code> if
	 * <code>that==null</code>
	 * 
	 * @param that
	 * @return <code>that</code> if <code>that!=null</code> or <code>this</code> in all other cases.
	 * @see NILPointer#optional(IExpr)
	 */
	default IExpr optional(final IExpr that) {
		if (that != null) {
			return that;
		}
		return this;
	}

	default IExpr or(final IExpr that) {
		return F.Or(this, that);
	}

	/**
	 * Return <code>this</code> if <code>this</code> unequals <code>F.NIL</code> , otherwise return <code>other</code>.
	 * 
	 * @param other
	 * @return <code>this</code> if <code>this</code> unequals <code>F.NIL</code>, otherwise return <code>other</code>.
	 * @see java.util.Optional#orElse(Object)
	 */
	default IExpr orElse(final IExpr other) {
		return this;
	}

	/**
	 * Return <code>this</code> if <code>this</code> unequals <code>F.NIL</code> , otherwise invoke {@code other} and
	 * return the result of that invocation.
	 *
	 * @param other
	 *            a {@code Supplier} whose result is returned if no value is present
	 * @return <code>this</code> if <code>this</code> unequals <code>F.NIL</code>, otherwise the result of
	 *         {@code other.get()}
	 */
	default IExpr orElseGet(Supplier<? extends IExpr> other) {
		return this;
	}

	/**
	 * Return <code>this</code> if <code>this</code> unequals <code>F.NIL</code> , otherwise throw an exception to be
	 * created by the provided supplier.
	 *
	 * @apiNote A method reference to the exception constructor with an empty argument list can be used as the supplier.
	 *          For example, {@code IllegalStateException::new}
	 *
	 * @param <X>
	 *            Type of the exception to be thrown
	 * @param exceptionSupplier
	 *            The supplier which will return the exception to be thrown
	 * @return <code>this</code> if <code>this</code> unequals <code>F.NIL</code> or throw an exception
	 * @throws X
	 *             if there is no value present
	 */
	default <X extends Throwable> IExpr orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		return this;
	}

	/**
	 * <p>
	 * Select all elements by applying the <code>predicate</code> to each argument in this <code>AST</code> and append
	 * the arguments which satisfy the predicate to the <code>1st element</code> of the resulting AST, or otherwise
	 * append it to the <code>2nd element</code> of the resulting AST.
	 * </p>
	 * <p>
	 * See: <a href="https://people.eecs.berkeley.edu/~fateman/papers/partition.pdf">Fateman - Partitioning of Algebraic
	 * Subexpressions in Computer Algebra Systems</a>
	 * </p>
	 * 
	 * @param operator
	 *            the if the head of this expression equals <code>operator</code>, otherwise return <code>F.NIL</code>.
	 * @param predicate
	 *            the predicate which filters each element in this AST
	 * @param initTrue
	 *            the result for the 1st result element, if the predicate doesn't give <code>true</code> for any of the
	 *            arguments in this AST.
	 * @param initFalse
	 *            the result for the 2nd result element, if the predicate doesn't give <code>false</code> for any of the
	 *            arguments in this AST.
	 * @param combiner
	 *            the 1st and 2md results element head
	 * @return <code>F.NIL</code> if partitioning wasn't possible
	 */
	default IAST partition(ISymbol operator, Predicate<? super IExpr> predicate, IExpr initTrue, IExpr initFalse,
			ISymbol combiner, ISymbol action) {
		return F.NIL;
	}

	/**
	 * <p>
	 * Select all elements by applying the <code>predicate</code> to each argument in this <code>Plus(...)</code>
	 * expression and append the arguments which satisfy the predicate to the <code>1st element</code> of the resulting
	 * AST, or otherwise append it to the <code>2nd element</code> of the resulting AST.
	 * </p>
	 * <p>
	 * See: <a href="https://people.eecs.berkeley.edu/~fateman/papers/partition.pdf">Fateman - Partitioning of Algebraic
	 * Subexpressions in Computer Algebra Systems</a>
	 * </p>
	 * 
	 * @param predicate
	 *            the predicate which filters each element in this AST
	 * @param initTrue
	 *            the result for the 1st result element, if the predicate doesn't give <code>true</code> for any of the
	 *            arguments in this AST.
	 * @param initFalse
	 *            the result for the 2nd result element, if the predicate doesn't give <code>false</code> for any of the
	 *            arguments in this AST.
	 * @return <code>F.NIL</code> if partitioning wasn't possible
	 */
	default IAST partitionPlus(Predicate<? super IExpr> predicate, IExpr initTrue, IExpr initFalse, ISymbol action) {
		return F.NIL;
	}

	/**
	 * <p>
	 * Select all elements by applying the <code>predicate</code> to each argument in this <code>Times(...)</code>
	 * expression and append the arguments which satisfy the predicate to the <code>1st element</code> of the resulting
	 * AST, or otherwise append it to the <code>2nd element</code> of the resulting AST.
	 * </p>
	 * <p>
	 * See: <a href="https://people.eecs.berkeley.edu/~fateman/papers/partition.pdf">Fateman - Partitioning of Algebraic
	 * Subexpressions in Computer Algebra Systems</a>
	 * </p>
	 * 
	 * @param predicate
	 *            the predicate which filters each element in this AST
	 * @param initTrue
	 *            the result for the 1st result element, if the predicate doesn't give <code>true</code> for any of the
	 *            arguments in this AST.
	 * @param initFalse
	 *            the result for the 2nd result element, if the predicate doesn't give <code>false</code> for any of the
	 *            arguments in this AST.
	 * @return <code>F.NIL</code> if partitioning wasn't possible
	 */
	default IAST partitionTimes(Predicate<? super IExpr> predicate, IExpr initTrue, IExpr initFalse, ISymbol action) {
		return F.NIL;
	}

	/**
	 * Returns an <code>IExpr</code> whose value is <code>(this + that)</code>. Calculates
	 * <code>F.eval(F.Plus(this, that))</code> in the common case and uses a specialized implementation for derived
	 * number classes.
	 * 
	 * @param that
	 * @return
	 */
	default IExpr plus(final IExpr that) {
		return F.eval(F.Plus(this, that));
	}

	/**
	 * Returns an <code>IExpr</code> whose value is <code>(this ^ that)</code>. Calculates
	 * <code>F.eval(F.Power(this, that))</code> in the common case and uses a specialized implementation for derived
	 * number classes.
	 * 
	 * @param that
	 * @return <code>(this ^ that)</code>
	 */
	default IExpr power(final IExpr that) {
		if (that.isZero()) {
			if (!this.isZero()) {
				return F.C1;
			}
		} else if (that.isOne()) {
			return this;
		}
		if (this.isNumber() && that.isNumber()) {
			return F.eval(F.Power(this, that));
		}
		return F.Power(this, that);
	}

	/**
	 * Returns an <code>IExpr</code> whose value is <code>(this ^ n)</code>. Calculates
	 * <code>F.eval(F.Power(this, that))</code> in the common case and uses a specialized implementation for derived
	 * number classes.
	 * 
	 * @param n
	 *            the exponent
	 * @return <code>(this ^ n)</code>
	 */
	@Override
	default IExpr power(final long n) {
		if (n == 0L) {
			if (!this.isZero()) {
				return F.C1;
			}
			// don't return F.Indeterminate here! The evaluation of F.Power()
			// returns Indeterminate
			return F.Power(this, F.C0);
		} else if (n == 1L) {
			return this;
		} else if (this.isNumber()) {
			long exp = n;
			if (n < 0) {
				exp *= -1;
			}
			int b2pow = 0;

			while ((exp & 1) == 0) {
				b2pow++;
				exp >>= 1;
			}

			INumber r = (INumber) this;
			INumber x = r;

			while ((exp >>= 1) > 0) {
				x = (INumber) x.multiply(x);
				if ((exp & 1) != 0) {
					r = (INumber) r.multiply(x);
				}
			}

			while (b2pow-- > 0) {
				r = (INumber) r.multiply(r);
			}
			if (n < 0) {
				return r.inverse();
			}
			return r;
		}
		return F.Power(this, F.integer(n));
	}

	/**
	 * Return the real part of this expression if possible. Otherwise return <code>Re(this)</code>.
	 * 
	 * @return real part
	 */
	default public IExpr re() {
		return F.eval(F.Re(this));
	}

	@Override
	default public IExpr reciprocal() throws MathRuntimeException {
		return inverse();
	}

	@Override
	default IExpr remainder(IExpr that) {
		if (equals(that)) {
			return F.C0;
		}
		return this;
	}

	/**
	 * Replace all (sub-) expressions with the given unary function. If no substitution matches, the method returns
	 * <code>null</code>.
	 * 
	 * @param function
	 *            if the unary functions <code>apply()</code> method returns <code>F.NIL</code> the expression isn't
	 *            substituted.
	 * @return <code>F.NIL</code> if no substitution of a (sub-)expression was possible.
	 */
	@Nullable
	default IExpr replaceAll(final Function<IExpr, IExpr> function) {
		return accept(new VisitorReplaceAll(function));
	}

	/**
	 * Replace all (sub-) expressions with the given rule set. If no substitution matches, the method returns
	 * <code>null</code>.
	 * 
	 * @param astRules
	 *            rules of the form <code>x-&gt;y</code> or <code>{a-&gt;b, c-&gt;d}</code>; the left-hand-side of the
	 *            rule can contain pattern objects.
	 * @return <code>F.NIL</code> if no substitution of a (sub-)expression was possible.
	 */
	@Nullable
	default IExpr replaceAll(final IAST astRules) {
		return accept(new VisitorReplaceAll(astRules));
	}

	/**
	 * Replace all subexpressions with the given rule set. A rule must contain the position of the subexpression which
	 * should be replaced on the left-hand-side. If no substitution matches, the method returns <code>F.NIL</code>.
	 * 
	 * @param astRules
	 *            rules of the form <code>position-&gt;y</code> or <code>{position1-&gt;b, position2-&gt;d}</code>
	 * @return <code>F.NIL</code> if no substitution of a subexpression was possible.
	 */
	default IExpr replacePart(final IAST astRules) {
		return this.accept(new VisitorReplacePart(astRules));
	}

	/**
	 * Repeatedly replace all (sub-) expressions with the given unary function. If no substitution matches, the method
	 * returns <code>this</code>.
	 * 
	 * @param function
	 *            if the unary functions <code>apply()</code> method returns <code>null</code> the expression isn't
	 *            substituted.
	 * @return <code>this</code> if no substitution of a (sub-)expression was possible.
	 */
	default IExpr replaceRepeated(final Function<IExpr, IExpr> function) {
		return replaceRepeated(new VisitorReplaceAll(function));
	}

	/**
	 * Repeatedly replace all (sub-) expressions with the given rule set. If no substitution matches, the method returns
	 * <code>this</code>.
	 * 
	 * @param astRules
	 *            rules of the form <code>x-&gt;y</code> or <code>{a-&gt;b, c-&gt;d}</code>; the left-hand-side of the
	 *            rule can contain pattern objects.
	 * @return <code>this</code> if no substitution of a (sub-)expression was possible.
	 */
	default IExpr replaceRepeated(final IAST astRules) {
		return replaceRepeated(new VisitorReplaceAll(astRules));
	}

	/**
	 * Repeatedly replace all (sub-) expressions with the given visitor. If no substitution matches, the method returns
	 * <code>this</code>.
	 * 
	 * @param visitor
	 * @return
	 */
	default IExpr replaceRepeated(VisitorReplaceAll visitor) {
		IExpr result = this;
		IExpr temp = accept(visitor);
		final int iterationLimit = EvalEngine.get().getIterationLimit();
		int iterationCounter = 1;
		while (temp.isPresent()) {
			result = temp;
			temp = result.accept(visitor);
			if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
				IterationLimitExceeded.throwIt(iterationCounter, result);
			}
		}
		return result;
	}

	/**
	 * <p>
	 * Replace all occurrences of Slot[&lt;index&gt;] expressions with the expression at the appropriate
	 * <code>index</code> in the given <code>slotsList</code>.
	 * </p>
	 * <p>
	 * <b>Note:</b> If a slot value is <code>null</code> the Slot will not be substituted.
	 * </p>
	 * 
	 * @param slotsList
	 *            the values for the slots.
	 * @return <code>null</code> if no substitution occurred.
	 * @deprecated use org.matheclipse.core.eval.util.Lambda#replaceSlots() instead
	 */
	@Deprecated
	default IExpr replaceSlots(final IAST slotsList) {
		return accept(new VisitorReplaceSlots(slotsList));
	}

	/**
	 * Signum functionality is used in JAS toString() method, don't use it as math signum function.
	 * 
	 * @deprecated
	 */
	@Deprecated
	@Override
	default int signum() {
		if (isZero()) {
			return 0;
		}
		if (isSignedNumber()) {
			return ((ISignedNumber) this).sign();
		}
		return 1;
	}

	@Override
	default IExpr subtract(IExpr that) {
		if (that.isZero()) {
			return this;
		}
		if (that.isNumber()) {
			return F.eval(F.Plus(this, that.negate()));
		}
		return F.eval(F.Plus(this, F.Times(F.CN1, that)));
	}

	@Override
	default IExpr sum(final IExpr that) {
		return add(that);
	}

	/**
	 * Returns an <code>IExpr</code> whose value is <code>(this * that)</code>. Calculates
	 * <code>F.eval(F.Times(this, that))</code> in the common case and uses a specialized implementation for derived
	 * number classes.
	 * 
	 * @param that
	 *            the multiplier expression
	 * @return <code>(this * that)</code>
	 */
	default IExpr times(final IExpr that) {
		if (that.isZero()) {
			return F.C0;
		}
		if (that.isOne()) {
			return this;
		}
		return F.eval(F.Times(this, that));
	}

	/**
	 * Returns an <code>IExpr</code> whose value is <code>(this * that)</code>. Calculates
	 * <code>F.eval(F.Times(this, that))</code> in the common case and uses a specialized implementation for derived
	 * number classes.
	 * 
	 * @param that
	 *            the multiplier expression
	 * @return <code>(this * that)</code>
	 */
	default public IExpr timesDistributed(final IExpr that) {
		return times(that);
	}

	/**
	 * Convert this object into a <code>double[]</code> matrix.
	 * 
	 * @return <code>null</code> if this object can not be converted into a <code>double[]</code> matrix
	 */
	default double[][] toDoubleMatrix() {
		return null;
	}

	/**
	 * Converts this number to an <code>int</code> value; unlike {@link #intValue} this method returns
	 * <code>defaultValue</code> if the value of this integer isn't in the range <code>Integer.MIN_VALUE</code> to
	 * <code>Integer.MAX_VALUE</code> or the expression is not convertible to the int range.
	 * 
	 * @param defaultValue
	 *            the default value, if this integer is not in the <code>int</code> range
	 * @return the numeric value represented by this integer after conversion to type <code>int</code>.
	 */
	default int toIntDefault(int defaultValue) {
		return defaultValue;
	}

	/**
	 * Convert this object into a <code>double[]</code> vector.
	 * 
	 * @return <code>null</code> if this object can not be converted into a <code>double[]</code> vector
	 */
	default double[] toDoubleVector() {
		return null;
	}

	/**
	 * The 'highest level' head of the expression, before Symbol, Integer, Real or String. for example while the head of
	 * a[b][c] is a[b], the top head is a.
	 * 
	 * @return the 'highest level' head of the expression.
	 */
	default ISymbol topHead() {
		return (ISymbol) head();
	}

	/**
	 * Convert this object into a RealMatrix.
	 * 
	 * @return <code>null</code> if this object can not be converted into a RealMatrix
	 */
	default RealMatrix toRealMatrix() {
		return null;
	}

	/**
	 * Convert this object into a RealVector.
	 * 
	 * @return <code>null</code> if this object can not be converted into a RealVector
	 */
	default RealVector toRealVector() {
		return null;
	}

	@Override
	default String toScript() {
		return toString();
	}

	@Override
	default String toScriptFactory() {
		throw new UnsupportedOperationException(toString());
	}

	/**
	 * Compare if <code>this != that</code:
	 * <ul>
	 * <li>return F.True if the comparison is <code>true</code></li>
	 * <li>return F.False if the comparison is <code>false</code></li>
	 * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
	 * </ul>
	 * 
	 * @param that
	 * @return <code>F.True, F.False or F.NIL</code
	 */
	default public IExpr unequalTo(IExpr that) {
		COMPARE_TERNARY temp = BooleanFunctions.CONST_EQUAL.compareTernary(this, that);
		return ITernaryComparator.convertToExpr(temp);
	}

	/**
	 * If this is a <code>Interval[{lower, upper}]</code> expression return the <code>upper</code> value. If this is a
	 * <code>ISignedNumber</code> expression return <code>this</code>.
	 * 
	 * @return <code>F.NIL</code> if this expression is no interval and no signed number.
	 */
	default public IExpr upper() {
		return F.NIL;
	}

	/**
	 * Convert the variables (i.e. expressions of type <code>ISymbol</code> which aren't constants) in this expression
	 * into Slot[] s.
	 * 
	 * @param map
	 *            for every given symbol argument return the associated unique slot from this map
	 * @param variableCollector
	 *            collects the variables which are used in the replacement process
	 * @return <code>F.NIL</code> if no variable symbol was found.
	 */
	default IExpr variables2Slots(final Map<IExpr, IExpr> map, final Collection<IExpr> variableCollector) {
		return this;
	}
}
