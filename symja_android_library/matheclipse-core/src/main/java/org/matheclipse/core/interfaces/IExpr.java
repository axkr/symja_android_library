package org.matheclipse.core.interfaces;

import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.Sqrt;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.hipparchus.CalculusFieldElement;
import org.hipparchus.Field;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.hipparchus.util.FieldSinCos;
import org.hipparchus.util.FieldSinhCosh;
import org.jgrapht.GraphType;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.PredicateQ;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.WolframFormFactory;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.core.visit.VisitorBooleanLevelSpecification;
import org.matheclipse.core.visit.VisitorReplaceAll;
import org.matheclipse.core.visit.VisitorReplaceAllLambda;
import org.matheclipse.core.visit.VisitorReplacePart;
import org.matheclipse.core.visit.VisitorReplaceSlots;
import edu.jas.structure.ElemFactory;
import edu.jas.structure.GcdRingElem;

/**
 * (I)nterface for a mathematical (Expr)ession<br>
 * <code>IExpr</code> is the main interface for the Symja object type hierarchy:
 *
 * <pre>
 * java.lang.Object
 *    |--- org.matheclipse.core.expression.AbstractAST
 *    |       |--- org.matheclipse.core.expression.HMArrayList
 *    |               |--- org.matheclipse.core.expression.AST - abstract syntax tree which represents lists, vectors, matrices and functions
 *    |                                   implements IAST, IASTMutable, IASTAppendable, IExpr
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
 */
public interface IExpr
    extends Comparable<IExpr>, GcdRingElem<IExpr>, Serializable, CalculusFieldElement<IExpr> {

  /**
   * A three-state &quot;boolean&quot; value. If a comparison can not be evaluated to <code>S.True
   * </code> (&quot;state&quot; <code>TRUE</code>) or <code>S.False</code> (&quot;state&quot; <code>
   * FALSE</code>) it can get the &quot;state&quot; <code>UNDECIDABLE</code>.
   *
   * <p>See: <a href"https://en.wikipedia.org/wiki/Three-valued_logic">Three-valued logic</a>
   */
  public static enum COMPARE_TERNARY {
    TRUE,
    FALSE,
    UNDECIDABLE
  }

  public static final int ASTID = 1024;

  public static final int BLANKID = 4096;

  public static final int COMPLEXID = 32;

  public static final int DOUBLECOMPLEXID = 4;

  public static final int DOUBLEID = 2;

  public static final int FRACTIONID = 16;

  public static final int INTEGERID = 8;

  public static final int METHODSYMBOLID = 8192;

  public static final int PATTERNID = 2048;

  public static final int SERIESID = 64;

  public static final int QUANTITYID = 128;

  public static final int STRINGID = 256;

  public static final int SYMBOLID = 512;

  public static final int DATASETID = 16384;

  public static final int DATAID = 32786;

  public static final int BYTEARRAYID = DATAID + 1;

  public static final int COMPILEFUNCTONID = DATAID + 2;

  public static final int GEOPOSITIONID = DATAID + 3;

  public static final int GRAPHEXPRID = DATAID + 4;

  public static final int DATEOBJECTEXPRID = DATAID + 5;

  public static final int TIMEOBJECTEXPRID = DATAID + 6;

  public static final int FITTEDMODELID = DATAID + 7;

  public static final int INTERPOLATEDFUNCTONID = DATAID + 8;

  public static final int SPARSEARRAYID = DATAID + 9;

  public static final int NUMERICARRAYID = DATAID + 10;

  public static final int DISPATCHID = DATAID + 11;

  public static final int TESTREPORTOBJECT = DATAID + 12;

  public static final int TESTRESULTOBJECT = DATAID + 13;

  public static final int FILEEXPRID = DATAID + 15;

  public static final int OUTPUTSTREAMEXPRID = DATAID + 16;

  public static final int INPUTSTREAMEXPRID = DATAID + 17;

  public static final int JAVACLASSEXPRID = DATAID + 18;

  public static final int JAVAOBJECTEXPRID = DATAID + 19;

  public static final int LINEARSOLVEUNCTONID = DATAID + 20;

  public static IExpr convertToExpr(COMPARE_TERNARY temp) {
    if (temp == COMPARE_TERNARY.TRUE) {
      return S.True;
    }
    if (temp == COMPARE_TERNARY.FALSE) {
      return S.False;
    }
    return F.NIL;
  }

  /**
   * Returns an {@code IExpr} describing the specified value, if non-null, otherwise returns {@code
   * F.NIL} .
   *
   * @param value the possibly-null value to describe
   * @return an {@code IExpr} with a present value if the specified value is non-null, otherwise an
   *     empty {@code Optional}
   */
  public static IExpr ofNullable(IExpr value) {
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

  /**
   * Evaluate the absolute value of this.
   *
   * @return
   */
  @Override
  default IExpr abs() {
    return F.eval(F.Abs(this));
  }

  /**
   * Accept a visitor with return type T
   *
   * @param visitor
   * @return <code>F.NIL</code> if no evaluation was necessary
   */
  public IExpr accept(IVisitor visitor);

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

  /**
   * Set an evaluation flag. <b>Note</b> only certain data structures like <code>IAST</code> and
   * <code>ISparseArray</code> support evaluation flags, otherwise the <code>this</code> object will
   * be returned without modification.
   *
   * @param evalFlags
   * @return
   */
  default IExpr addEvalFlags(final int evalFlags) {
    return this;
  }

  /**
   * Apply the <code>And</code> operator
   *
   * @param that
   * @return
   * @deprecated use {@link F#And(IExpr, IExpr)}
   */
  @Deprecated
  default IExpr and(final IExpr that) {
    return F.And(this, that);
  }

  /**
   * @param leaves
   * @return an IExpr instance with the current expression as head(), and leaves as leaves().
   */
  default IExpr apply(IExpr... leaves) {
    return F.ast(leaves, head());
  }

  /**
   * @param leaves
   * @return an IExpr instance with the current expression as head(), and leaves as leaves().
   */
  default IExpr apply(List<? extends IExpr> leaves) {
    return F.ast(leaves.toArray(new IExpr[leaves.size()]), head());
    // final IASTMutable ast = F.ast(head() );
    // for (int i = 0; i < leaves.size(); i++) {
    // ast.append(leaves.get(i));
    // }
    // return ast;
  }

  /**
   * Returns the <b>number of arguments</b> in this {@code IAST}. The <b>number of arguments</b>
   * equals <code>size() - 1</code> (i.e. the <b>number of elements</b> minus 1). If this is an atom
   * return size <code>-1</code>.
   *
   * @return the number of arguments in this {@code IAST}.
   * @see #size()
   */
  default int argSize() {
    return -1;
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
      if (isReal()) {
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
   * Get the first element of this <code>AST</code> list (i.e. get(1)). Return <code>F.NIL</code> if
   * this object isn't an <code>AST</code>. Use this method if the AST gives <code>true</code> for
   * the <code>isPower()</code> method.
   *
   * @return the first argument of the function represented by this <code>AST</code> or <code>F.NIL
   *     </code> if this object isn't an AST.
   */
  default IExpr base() {
    if (Config.FUZZ_TESTING) {
      if (!isPower() && !isAST(S.Surd)) {
        throw new NullPointerException();
      }
    }
    return first();
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  default int compareTo(IExpr expr) {
    if (expr.isAST()) {
      // if (!expr.isDirectedInfinity()) {
      return -1 * expr.compareTo(this);
      // }
    }
    final int x = hierarchy();
    final int y = expr.hierarchy();
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
   * Get a nested list with <code>this</code> expression set as a value.
   *
   * <pre>
   * v.constantArray(2, 3) -> {{v, v, v}, {v, v, v}}
   * </pre>
   *
   * @param head the head for the new <code>IASTAppendable</code> objects.
   * @param startPosition the position from there to create the constant array recusively.
   * @param arr the nested lists dimensions. <code>arr.length</code> must be greater <code>0</code>
   * @return <code>F.NIL</code> if <code>arr</code> has length 0.
   */
  default IASTAppendable constantArray(IExpr head, final int startPosition, int... arr) {
    final int size = arr[startPosition];
    if (Config.MAX_AST_SIZE < size) {
      ASTElementLimitExceeded.throwIt(size);
    }
    if (arr.length - 1 == startPosition) {
      IExpr[] exprArr = new IExpr[size];
      for (int i = 0; i < size; i++) {
        exprArr[i] = this;
      }
      return F.ast(exprArr, head);
    }
    IExpr[] exprArr = new IExpr[size];
    for (int i = 0; i < size; i++) {
      exprArr[i] = constantArray(head, startPosition + 1, arr);
    }
    return F.ast(exprArr, head);
  }

  /**
   * Return <code>negate()</code> if <code>number.sign() < 0</code>, otherwise return <code>this
   * </code>
   *
   * @param number
   * @return
   */
  default IExpr copySign(ISignedNumber number) {
    return number.complexSign() < 0 ? negate() : this;
  }

  /**
   * Returns an <code>IExpr</code> whose value is <code>(this - 1)</code>. Calculates <code>
   * F.eval(F.Subtract(this, C1))</code> in the common case and uses a specialized implementation
   * for derived number classes.
   *
   * @return
   */
  default IExpr dec() {
    return plus(F.CN1);
  }

  /**
   * Calculates the depth of an expression. Atomic expressions (no sublists) have depth <code>1
   * </code> Example: the nested list <code>[x,[y]]</code> has depth <code>3</code>
   */
  default int depth() {
    return 1;
  }

  /**
   * Determine precision of this expression. Return -1 for symbolic evaluation.
   *
   * @return the precision of this expression. -1 for symbolic evaluation.
   */
  default long determinePrecision() {
    return -1;
  }

  /**
   * Returns an <code>IExpr</code> whose value is <code>(this / that)</code>. Calculates <code>
   * F.eval(F.Times(this, F.Power(that, F.CN1)))</code> in the common case and uses a specialized
   * implementation for derived number classes.
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

    EvalEngine engine = EvalEngine.get();
    IExpr inverse = that.inverse();
    if (this.isOne()) {
      return inverse;
    }
    if (this.isMinusOne()) {
      return inverse.negate();
    }
    //    if (this.isPlus()) {
    //      IExpr plusAST = ((IAST) this).mapThread(F.binaryAST2(S.Times, F.Slot1, inverse), 1);
    //      return engine.evaluate(plusAST);
    //    }
    if (engine.isTogetherMode() && (this.isPlusTimesPower() || inverse.isPlusTimesPower())) {
      if (this.isNumber() && inverse.isPlus()) {
        return engine.evaluate(F.Expand(F.Times(this, inverse)));
      }
      if (inverse.isNumber() && this.isPlus()) {
        return engine.evaluate(F.Expand(F.Times(inverse, this)));
      }
      return engine.evaluate(F.Together(F.Times(this, inverse)));
    }
    return engine.evaluate(F.Times(this, inverse));
  }

  @Override
  default IExpr[] egcd(IExpr b) {
    throw new UnsupportedOperationException(toString());
  }

  /**
   * Calls <code>get(position).equals(expr)</code> if <code>this</code> is an <code>IAST</code>.
   * Returns <code>false</code> otherwise.
   *
   * @param position the position in the <code>IAST</code> which should be tested for equality
   * @param expr the expression which should be tested for equality
   * @return
   */
  public default boolean equalsAt(int position, final IExpr expr) {
    return false;
  }

  /**
   * Compare if <code>this == that</code:
   * <ul>
   * <li>return S.True if the comparison is <code>true</code></li>
   * <li>return S.False if the comparison is <code>false</code></li>
   * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
   * </ul>
   *
   * @param that
   * @return <code>S.True, S.False or F.NIL</code
   */
  default IExpr equalTo(IExpr that) {
    COMPARE_TERNARY temp = this.equalTernary(that, EvalEngine.get());
    return convertToExpr(temp);
  }

  default IExpr.COMPARE_TERNARY equalTernary(IExpr that, EvalEngine engine) {
    if (isIndeterminate() || that.isIndeterminate()) {
      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }
    if (this == that) {
      return IExpr.COMPARE_TERNARY.TRUE;
    }

    IExpr arg1 = this;
    IExpr arg2 = that;
    if (!arg1.isReal() && arg1.isNumericFunction(x -> x.isDirectedInfinity() ? "" : null)) {
      arg1 = engine.evalN(arg1);
    }
    if (!arg2.isReal() && arg2.isNumericFunction(x -> x.isDirectedInfinity() ? "" : null)) {
      arg2 = engine.evalN(arg2);
    }
    if (arg2.isInexactNumber() && arg1.isExactNumber()) {
      arg1 = engine.evalN(arg1);
    }
    if (arg1.isInexactNumber() && arg2.isExactNumber()) {
      arg2 = engine.evalN(arg2);
    }

    if (isSame(that)) {
      return IExpr.COMPARE_TERNARY.TRUE;
    } else {
      if (isConstantAttribute() && that.isConstantAttribute()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
      if (isString() && that.isString()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
    }
    if (arg2.isDirectedInfinity()) {
      if (arg1.isNumber()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
      if (arg1.isDirectedInfinity()) {
        return arg1.equals(arg2) ? IExpr.COMPARE_TERNARY.TRUE : IExpr.COMPARE_TERNARY.FALSE;
      }
    }
    if (arg1.isDirectedInfinity()) {
      if (arg2.isNumber()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
      if (arg2.isDirectedInfinity()) {
        return arg1.equals(arg2) ? IExpr.COMPARE_TERNARY.TRUE : IExpr.COMPARE_TERNARY.FALSE;
      }
    }

    IExpr difference = engine.evaluate(F.Subtract(arg1, arg2));
    if (difference.isNumber()) {
      if (difference.isZero()) {
        return IExpr.COMPARE_TERNARY.TRUE;
      }
      return IExpr.COMPARE_TERNARY.FALSE;
    }
    if (difference.isConstantAttribute()) {
      return IExpr.COMPARE_TERNARY.FALSE;
    }

    if (arg1.isNumber() && arg2.isNumber()) {
      return IExpr.COMPARE_TERNARY.FALSE;
    }

    return IExpr.COMPARE_TERNARY.UNDECIDABLE;
  }

  /**
   * Evaluate the expression to a <code>Complex</code> value.
   *
   * @return
   * @throws ArgumentTypeException
   */
  default Complex evalComplex() throws ArgumentTypeException {
    return EvalEngine.get().evalComplex(this);
  }

  default double evalDouble() throws ArgumentTypeException {
    return EvalEngine.get().evalDouble(this);
  }

  /**
   * Evaluate the expression to a Java <code>double</code> value. If the conversion to a double
   * value is not possible, the method throws an exception.
   *
   * @return this expression converted to a Java <code>double</code> value.
   */
  default double getReal() throws ArgumentTypeException {
    if (isInfinity()) {
      return Double.POSITIVE_INFINITY;
    } else if (isNegativeInfinity()) {
      return Double.NEGATIVE_INFINITY;
    }
    return evalDouble();
  }

  /**
   * Evaluate the expression to a <code>INumber</code> value.
   *
   * @return <code>null</code> if the conversion is not possible.
   */
  default INumber evalNumber() {
    if (isNumber()) {
      IExpr result = EvalEngine.get().evalN(this);
      if (result.isNumber()) {
        return (INumber) result;
      }
    }
    return null;
  }

  /**
   * Evaluate the expression to a <code>ISignedNumber</code> value.
   *
   * @return <code>null</code> if the conversion is not possible.
   */
  default ISignedNumber evalReal() {
    if (isReal()) {
      return (ISignedNumber) this;
    }
    return null;
  }

  /**
   * Evaluate the expression to a <code>ISignedNumber</code> value.
   *
   * @return <code>null</code> if the conversion is not possible.
   * @deprecated use {@link #evalReal()} instead
   */
  @Deprecated
  default ISignedNumber evalSignedNumber() {
    return evalReal();
  }

  /**
   * Evaluate an expression
   *
   * @param engine the evaluation engine
   * @return the evaluated Object or <code>F.NIL</code> if the evaluation is not possible (i.e. the
   *     evaluation doesn't change the object).
   */
  default IExpr evaluate(EvalEngine engine) {
    return F.NIL;
  }

  default IExpr evaluateHead(IAST ast, EvalEngine engine) {
    IExpr result = engine.evaluateNIL(this);
    if (result.isPresent()) {
      // set the new evaluated header !
      return ast.apply(result);
    }
    return F.NIL;
  }

  /**
   * Get the second element of this <code>AST</code> list (i.e. get(2)). Return <code>F.NIL</code>
   * if this object isn't an <code>AST</code>. Use this method if the AST gives <code>true</code>
   * for the <code>isPower()</code> method.
   *
   * @return the second argument of the function represented by this <code>AST</code> or <code>F.NIL
   *     </code> if this object isn't an AST.
   */
  default IExpr exponent() {
    if (Config.FUZZ_TESTING) {
      if (!isPower() && !isAST(S.Surd)) {
        throw new NullPointerException();
      }
    }
    return second();
  }

  @Override
  default ElemFactory<IExpr> factory() {
    return ExprRingFactory.CONST;
  }

  /**
   * Get the first element of this <code>AST</code> list (i.e. get(1)). Return <code>F.NIL</code> if
   * this object isn't an <code>AST</code>.
   *
   * @return the second argument of the function represented by this <code>AST</code> or <code>F.NIL
   *     </code>.
   */
  default IExpr first() {
    return F.NIL;
  }

  /**
   * Return the <code>FullForm()</code> of this expression
   *
   * @return
   */
  default String fullFormString() {
    return toString();
  }

  /**
   * Return the Gaussian integers real and imaginary parts. If this is not a Gaussian integer return
   * <code>null</code>
   *
   * @return <code>null</code> if this is not a Gaussian integer
   */
  default IInteger[] gaussianIntegers() {
    return null;
  }

  @Override
  default IExpr gcd(IExpr that) {
    return S.GCD.of(this, that);
  }

  /**
   * Get the element at the specified <code>index</code> if this object is of type <code>IAST</code>
   * .
   *
   * @param index
   * @return
   */
  default IExpr getAt(final int index) {
    return S.Part.of(this, F.ZZ(index));
  }

  @Override
  public default Field<IExpr> getField() {
    return F.EXPR_FIELD;
  }

  default IExpr getOptionalValue() {
    return null;
  }

  /**
   * Evaluate Greater, if both arguments are real numbers
   *
   * @param a1
   * @return
   */
  default IExpr greater(final IExpr a1) {
    if (isReal() && a1.isReal()) {
      return ((ISignedNumber) this).isGT(((ISignedNumber) a1)) ? S.True : S.False;
    }
    EvalEngine engine = EvalEngine.get();
    return engine.evaluate(F.Greater(this, a1));
  }

  /**
   * Evaluate GreaterEqual, if both arguments are real numbers
   *
   * @param a1
   * @return
   */
  default IExpr greaterEqual(final IExpr a1) {
    if (isReal() && a1.isReal()) {
      return ((ISignedNumber) this).isLT(((ISignedNumber) a1)) ? S.False : S.True;
    }
    EvalEngine engine = EvalEngine.get();
    return engine.evaluate(F.GreaterEqual(this, a1));
  }

  /**
   * Compare if <code>this >= that</code:
   * <ul>
   * <li>return S.True if the comparison is <code>true</code></li>
   * <li>return S.False if the comparison is <code>false</code></li>
   * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
   * </ul>
   *
   * @param that
   * @return <code>S.True, S.False or F.NIL</code
   */
  public default IExpr greaterEqualThan(IExpr that) {
    COMPARE_TERNARY temp = BooleanFunctions.CONST_GREATER_EQUAL.prepareCompare(this, that);
    return convertToExpr(temp);
  }

  /**
   * Compare if <code>this > that</code:
   * <ul>
   * <li>return S.True if the comparison is <code>true</code></li>
   * <li>return S.False if the comparison is <code>false</code></li>
   * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
   * </ul>
   *
   * @param that
   * @return <code>S.True, S.False or F.NIL</code
   */
  public default IExpr greaterThan(IExpr that) {
    COMPARE_TERNARY temp = BooleanFunctions.CONST_GREATER.prepareCompare(this, that);
    return convertToExpr(temp);
  }

  /**
   * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the
   * expression itself, did not match the given pattern. Calls <code>isFree(pattern, true)</code>.
   *
   * @param pattern a pattern-matching expression
   * @return
   */
  default boolean has(IExpr pattern) {
    return isFree(pattern, true);
  }

  /**
   * Returns <code>true</code>, if <b>at least one of the elements</b> in the subexpressions or the
   * expression itself, match the given pattern.
   *
   * @param pattern a pattern-matching expression
   * @param heads if set to <code>false</code>, only the arguments of an IAST should be tested and
   *     not the <code>Head[]</code> element.
   * @return
   */
  default boolean has(IExpr pattern, boolean heads) {
    if (pattern.isSymbol() || pattern.isNumber() || pattern.isString()) {
      return has(x -> x.equals(pattern), heads);
    }
    final IPatternMatcher matcher = new PatternMatcher(pattern);
    return has(matcher, heads);
  }

  /**
   * Returns <code>true</code>, if <b>at least one of the elements</b> in the subexpressions or the
   * expression itself, satisfy the given unary predicate.
   *
   * @param predicate a unary predicate
   * @param heads if set to <code>false</code>, only the arguments of an IAST should be tested and
   *     not the <code>Head[]</code> element.
   * @return
   */
  default boolean has(Predicate<IExpr> predicate, boolean heads) {
    return predicate.test(this);
  }

  /**
   * Returns <code>false</code>, if <b>all of the elements</b> in the subexpressions or the
   * expression itself, aren't a symbolic or numerical complex number or a structure with complex
   * number arguments.
   *
   * @return <code>true</code> if this expression is a complex number or a structure with complex
   *     number arguments.
   */
  default boolean hasComplexNumber() {
    return !isFree(
        x ->
            (x.isComplex()
                || //
                x.isComplexNumeric()
                || //
                x == S.I
                || //
                x.isAST(S.Complex)), //
        false);
  }

  /**
   * If this object is an instance of <code>IAST</code> get the first element (offset 0) of the
   * <code>IAST</code> list (i.e. <code>#get(0)</code> ). Otherwise return the specific header, i.e.
   * for <code>
   * integer number type => S.Integer, fraction number type => S.Rational, complex number type => S.Complex, ...
   * </code>
   *
   * @return the head of the expression, which must not be null.
   */
  public IExpr head();

  /**
   * Get the head of an expression and if it is a built-in symbol return the ID of this symbol,
   * otherwise return <code>-1</code> (ID.UNKNOWN)
   *
   * @return return the ID of this built-in header symbol or <code>-1</code>
   */
  default int headID() {
    final IExpr head = head();
    return head.isBuiltInSymbol()
        ? //
        ((IBuiltInSymbol) head).ordinal()
        : //
        ID.UNKNOWN;
  }

  /**
   * A unique integer ID for the implementation of this expression
   *
   * @return a unique integer id for the implementation of this expression
   */
  public int hierarchy();

  /**
   * If this expression unequals <code>F.NIL</code>, invoke the specified consumer with the <code>
   * this</code> object, otherwise return <code>F#NIL</code>.
   *
   * @param function function to be executed if this expression unequals <code>F.NIL</code>
   * @see java.util.Optional#ifPresent(Consumer)
   */
  default IExpr ifPresent(Function<? super IExpr, IExpr> function) {
    return this.isPresent() ? function.apply(this) : F.NIL;
  }

  /**
   * Return the imaginary part of this expression if possible. Otherwise return <code>Im(this)
   * </code>.
   *
   * @return real part
   */
  public default IExpr im() {
    return S.Im.of(this);
  }

  /**
   * Returns an <code>IExpr</code> whose value is <code>(this + 1)</code>. Calculates <code>
   * F.eval(F.Plus(this, C1))</code> in the common case and uses a specialized implementation for
   * derived number classes.
   *
   * @return
   */
  default IExpr inc() {
    return plus(F.C1);
  }

  /**
   * If this is of type {@link IAST}, find the first argument position, which equals <code>expr
   * </code>. The search starts at index <code>1</code>. Otherwise return <code>-1</code>.
   *
   * @param expr
   * @return <code>-1</code> if no position was found
   */
  default int indexOf(final IExpr expr) {
    return -1;
  }

  /**
   * If this is of type {@link IAST}, find the first argument position, which fulfills the <code>
   * predicate</code>. The search starts at index <code>1</code>. Otherwise return <code>-1</code>.
   *
   * @param predicate
   * @return the index of the first occurrence of the specified predicate, or <code>-1</code> if no
   *     position was found
   */
  default int indexOf(Predicate<? super IExpr> predicate) {
    return indexOf(predicate, 1);
  }

  /**
   * If this is of type {@link IAST}, find the first argument position, which fulfills the <code>
   * predicate</code>. The search starts at index <code>fromIndex</code>. Otherwise return <code>-1
   * </code>.
   *
   * @param predicate
   * @param fromIndex
   * @return the index of the first occurrence of the specified predicate, starting at the specified
   *     index, or <code>-1</code> if no position was found
   */
  default int indexOf(Predicate<? super IExpr> predicate, int fromIndex) {
    return -1;
  }

  /**
   * Return the internal Java form of this expression.
   *
   * @param symbolsAsFactoryMethod if <code>true</code> use the <code>F.symbol()</code> method,
   *     otherwise print the symbol name.
   * @param depth the recursion depth of this call. <code>0</code> indicates &quot;recurse without a
   *     limit&quot;.
   * @return the internal Java form of this expression
   */
  default CharSequence internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    return toString();
  }

  /**
   * Return the internal Java form of this expression.
   *
   * @param symbolsAsFactoryMethod if <code>true</code> use the <code>F.symbol()</code> method,
   *     otherwise print the symbol name.
   * @param depth the recursion depth of this call. <code>0</code> indicates &quot;recurse without a
   *     limit&quot;.
   * @param useOperators use operators instead of function names for representation of Plus, Times,
   *     Power,...
   * @param usePrefix use the <code>F....</code> class prefix for generating Java code.
   * @param noSymbolPrefix for symbols like <code>x,y,z,...</code> don't use the <code>F....</code>
   *     class prefix for code generation
   * @param variables TODO
   * @return the internal Java form of this expression
   */
  default CharSequence internalJavaString(
      boolean symbolsAsFactoryMethod,
      int depth,
      boolean useOperators,
      boolean usePrefix,
      boolean noSymbolPrefix,
      Function<IExpr, ? extends CharSequence> variables) {
    return toString();
  }

  /**
   * Return the internal Scala form of this expression.
   *
   * @param symbolsAsFactoryMethod if <code>true</code> use the <code>F.symbol()</code> method,
   *     otherwise print the symbol name.
   * @param depth the recursion depth of this call. <code>0</code> indicates &quot;recurse without a
   *     limit&quot;.
   * @return the internal Scala form of this expression
   */
  default CharSequence internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
    return toString();
  }

  /**
   * Returns the multiplicative inverse of this object. It is the object such as <code>
   * this.times(this.inverse()) == ONE </code>, with <code>ONE</code> being the multiplicative
   * identity. Calculates <code>F.eval(F.Power(this, F.CN1))</code> in the common case and uses a
   * specialized implmentation for derived number classes.
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
   * Test if this expression and all subexpressions are already expanded i.e. all <code>
   * Plus, Times, Power</code> (sub-)expressions are expanded.
   *
   * @return
   */
  default boolean isAllExpanded() {
    return true;
  }

  /**
   * Test if this expression is the <code>Alternatives</code> function <code>
   * Alternatives[&lt;pattern1&gt;, &lt;pattern2&gt;, ...]</code>
   *
   * @return
   */
  default boolean isAlternatives() {
    return false;
  }

  /**
   * Test if this expression is the function <code>And[&lt;arg&gt;,...]</code> and has at least 2
   * arguments.
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
   * Test if this AST is an association <code>&lt;|a-&gt;b, c-&gt;d|&gt;</code>(i.e. type <code>
   * AssociationAST</code>)
   *
   * @return
   */
  default boolean isAssociation() {
    return false;
  }

  /**
   * Test if this expression is an AST function, which contains a <b>header element</b> (i.e. the
   * function name) at index position <code>0</code> and some optional <b>argument elements</b> at
   * the index positions <code>1..n</code>. Therefore this expression is no <b>atomic
   * expression</b>.
   *
   * @return
   * @see #isAtom()
   */
  default boolean isAST() {
    return false;
  }

  /**
   * Test if this expression is an AST function, which contains the given <b>header element</b> at
   * index position <code>0</code> and some optional <b>argument elements</b> at the index positions
   * <code>1..(size()-1)</code>. Therefore this expression is not an <b>atomic expression</b>.
   *
   * @param header the header element at position 0, which should be tested
   * @return
   * @see #isAtom()
   */
  default boolean isAST(IExpr header) {
    return false;
  }

  /**
   * Test if this expression is an AST function, which contains the given <b>header element</b> at
   * index position <code>0</code> and optional <b>argument elements</b> at the index positions
   * <code>1..(length-1)</code>. If this test gives <code>true</code> this expression is not an
   * <b>atomic expression</b>.
   *
   * @param header the header element at position 0, which should be tested
   * @param length the size the AST expression must have (<b>inclusive head element</b>)
   * @return
   * @see #isAtom()
   */
  default boolean isAST(IExpr header, int length) {
    return false;
  }

  /**
   * Test if this expression is an AST function, which contains the given <b>header element</b> at
   * index position <code>0</code> and optional <b>argument elements</b> at the index positions
   * <code>1..(length-1)</code>. If this test gives <code>true</code> this expression is not an
   * <b>atomic expression</b>.
   *
   * @param header the header element at position 0, which should be tested
   * @param length the size the AST expression must have
   * @param args the arguments of this AST which should be tested, if they are equal, a <code>null
   *     </code> value argument skips the equals check.
   * @return
   * @see #isAtom()
   */
  default boolean isAST(IExpr header, int length, IExpr... args) {
    return false;
  }

  /**
   * Test if this expression is an AST function, which contains the given <b>header element</b> at
   * index position <code>0</code> and optional <b>argument elements</b> at the index positions
   * <code>1..(length-1)</code>. If this test gives <code>true</code> this expression is not an
   * <b>atomic expression</b>.
   *
   * @param header the header element at position 0, which should be tested
   * @param minLength the minimum size the AST expression must have
   * @param maxLength the maximum size the AST expression must have
   * @return
   * @see #isAtom()
   */
  default boolean isAST(IExpr header, int minLength, int maxLength) {
    return false;
  }

  /**
   * Test if this expression is an AST function, where the string representation of the <b>header
   * element</b> at index position <code>0</code> equals the given <code>symbol</code> and some
   * optional <b>argument elements</b> at the index positions <code>1..(size()-1)</code>. If this
   * test gives <code>true</code> this expression is not an <b>atomic expression</b>.
   *
   * <p>Example: <code>isAST("Sin")</code> gives <code>true</code> for <code>Sin(Pi/2)</code>.
   *
   * <p><b>Note:</b> this is a performance critical method, only use it in special cases like for
   * example UI handling etc.
   *
   * @param headerStr string representation of the <b>header element</b> at index position <code>0
   *     </code>
   * @return
   * @see #isAtom()
   */
  default boolean isAST(String headerStr) {
    return false;
  }

  /**
   * Test if this expression is an AST function, where the string representation of the <b>header
   * element</b> at index position <code>0</code> equals the given <code>symbol</code> and some
   * optional <b>argument elements</b> at the index positions <code>1..(length-1)</code>.<br>
   * Therefore this expression is no <b>atomic expression</b>. <br>
   * Example: <code>isAST("Sin", 2)</code> gives <code>true</code> for <code>Sin(0)</code>.
   *
   * @param headerStr string representation of the <b>header element</b> at index position <code>0
   *     </code>
   * @param length the size the AST expression must have
   * @return
   * @see #isAtom()
   */
  default boolean isAST(String headerStr, int length) {
    return false;
  }

  /**
   * Test if this expression is an AST function or an Association, which contains a <b>header
   * element</b> (i.e. the function name) at index position <code>0</code> and some optional
   * <b>argument elements</b> at the index positions <code>1..n</code>.
   *
   * @return
   */
  default boolean isASTOrAssociation() {
    return isAST() || isAssociation();
  }

  /**
   * Test if this expression is an AST function, which contains a <b>header element</b> (i.e. the
   * function name) at index position <code>0</code> and no <b>argument elements</b>. <br>
   * Therefore this expression is no <b>atomic expression</b>.
   *
   * @return
   * @see #isAtom()
   */
  default boolean isAST0() {
    return false;
  }

  /**
   * Test if this expression is an AST function, which contains a <b>header element</b> (i.e. the
   * function name) at index position <code>0</code> and one <b>argument element</b> at the index
   * position <code>1</code>.<br>
   * Therefore this expression is no <b>atomic expression</b>.
   *
   * @return
   * @see #isAtom()
   */
  default boolean isAST1() {
    return false;
  }

  /**
   * Test if this expression is an AST function, which contains a <b>header element</b> (i.e. the
   * function name) at index position <code>0</code> and two <b>argument elements</b> at the index
   * positions <code>1, 2</code>.<br>
   * Therefore this expression is no <b>atomic expression</b>.
   *
   * @return
   * @see #isAtom()
   */
  default boolean isAST2() {
    return false;
  }

  /**
   * Test if this expression is an AST function, which contains a <b>header element</b> (i.e. the
   * function name) at index position <code>0</code> and three <b>argument elements</b> at the index
   * positions <code>1, 2, 3</code>.<br>
   * Therefore this expression is no <b>atomic expression</b>.
   *
   * @return
   * @see #isAtom()
   */
  default boolean isAST3() {
    return false;
  }

  /**
   * Test if this expression is an AST function, which contains the given <b>header element</b> at
   * index position <code>0</code> and optional <b>argument elements</b> at the index positions
   * <code>1..n</code>. <code>n</code> must be greater equal than the given <code>length</code>.<br>
   * Therefore this expression is no <b>atomic expression</b>.
   *
   * @param header the header element at position 0, which should be tested
   * @param length the size the AST expression must have
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
   * Test if this expression is a boolean formula (i.e. a symbol or a boolean function <code>
   * And, Equivalent, Nand, Nor, Not, Or, Xor</code> where all arguments are also &quot;boolean
   * formulas&quot;)
   *
   * @return <code>true</code>, if the given expression is a boolean formula or a symbol.
   * @see #isRealResult()
   */
  default boolean isBooleanFormula() {
    return false;
  }

  /**
   * Test if this expression is a <code>IBuiltInSymbol</code> symbol and the evaluator implements
   * <code>IBooleanFormula</code>.
   *
   * @return
   */
  default boolean isBooleanFormulaSymbol() {
    return false;
  }

  /**
   * Test if this expression is a boolean function with head <code>
   * And, Equivalent, Nand, Nor, Not, Or, Xor</code>.
   *
   * @return
   */
  default boolean isBooleanFunction() {
    return false;
  }

  /**
   * Test if this expression is a boolean function (i.e. a symbol or a boolean function like for
   * example <code>
   * And, Equivalent, Equal, Greater, GreaterEqual, Less, LessEqual, Nand, Nor, Not, Or, Xor,...
   * </code> where all arguments are also &quot;boolean functions&quot;) or a symbol or some builtin
   * predicates like for example <code>IntegerQ, EvenQ, PrimeQ,....</code>
   *
   * @return <code>true</code>, if the given expression is a boolean function or a symbol.
   * @see #isRealResult()
   */
  default boolean isBooleanResult() {
    if (S.True.equals(AbstractAssumptions.assumeBoolean(this))) {
      return true;
    }
    return isBooleanFormula();
  }

  /**
   * Test if this expression is a symbol (instanceof IBuiltInSymbol)
   *
   * @return
   */
  default boolean isBuiltInSymbol() {
    return this instanceof IBuiltInSymbol;
  }

  /**
   * Test if this expression is a comparator function (i.e. a function with head <code>
   * Equal, Equivalent, Greater, GreaterEqual, Less, LessEqual, Inequlity, SameQ, Unequal, UnsameQ
   * </code> and size greater 2)
   *
   * @return <code>true</code>, if the given expression is a comparator function.
   */
  default boolean isComparatorFunction() {
    return false;
  }

  /**
   * Test if this expression is a <code>IBuiltInSymbol</code> symbol and the evaluator implements
   * <code>IComparatorFunction</code>.
   *
   * @return
   */
  default boolean isComparatorFunctionSymbol() {
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
   * Test if this expression is the Condition function <code>Condition[&lt;arg1&gt;, &lt;arg2&gt;]
   * </code>
   *
   * @return
   */
  default boolean isCondition() {
    return false;
  }

  /**
   * Test if this expression is the ConditionalExpression function <code>
   * ConditionalExpression[&lt;arg1&gt;, &lt;arg2&gt;]</code>
   *
   * @return
   */
  default boolean isConditionalExpression() {
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
   * Test if this expression is a symbol with attribute <code>Constant</code>. Therefore numbers
   * return <code>false</code> for this method!
   *
   * @return
   * @see #isRealResult()
   * @see #isNumericFunction(boolean)
   */
  default boolean isConstantAttribute() {
    return false;
  }

  /**
   * Test if this expression is a continuous distribution AST (i.e. NormalDistribution(),
   * ExponentialDistribution(),...)
   *
   * @return
   */
  default boolean isContinuousDistribution() {
    return false;
  }

  /**
   * Test if this expression is a <code>IBuiltInSymbol</code> symbol and the evaluator implements
   * <code>ICoreFunctionEvaluator</code> (see package <code>org.matheclipse.core.builtin.function
   * </code>).
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
   * Test if this AST is a <code>Dataset</code> (i.e. instance of <code>IASTDataset</code>).
   *
   * @return
   */
  default boolean isDataset() {
    return this instanceof IASTDataset;
  }

  /**
   * Test if this expression is the function <code>Defer[&lt;arg&gt;]</code>
   *
   * @return
   */
  default boolean isDefer() {
    return false;
  }

  /**
   * Test if this expression is a <code>Derivative[number, ...][symbol][arg,...]</code> or <code>
   * Derivative[number][symbol]</code> expression and return the corresponding <code>IAST</code>
   * structures.
   *
   * <ul>
   *   <li>The expression at index <code>[0]</code> contains the <code>Derivative[number, ...]
   *       </code> AST part.
   *   <li>The expression at index <code>[1]</code> contains the <code>Derivative[...][symbol]
   *       </code> AST part.
   *   <li>The expression at index <code>[2]</code> contains the <code>
   *       Derivative[...][...][arg, ...]</code> AST part, if available.
   * </ul>
   *
   * <p><b>Note:</b> the result at index <code>[2]</code> maybe <code>null</code> , if no argument
   * is available.
   *
   * @return <code>null</code> if the expression is not a <code>
   *     Derivative[number, ...][symbol][arg, ...]</code> or <code>Derivative[number, ...][symbol]
   *     </code> expression.
   */
  default IAST[] isDerivative() {
    return null;
  }

  /**
   * Test if this expression is a <code>Derivative[number][symbol][arg]</code> or <code>
   * Derivative[number][symbol]</code> expression with one argument and return the corresponding
   * <code>IAST</code> structures.
   *
   * <ul>
   *   <li>The expression at index <code>[0]</code> contains the <code>Derivative[number,...]</code>
   *       AST part.
   *   <li>The expression at index <code>[1]</code> contains the <code>Derivative[...][symbol]
   *       </code> AST part.
   *   <li>The expression at index <code>[2]</code> contains the <code>Derivative[...][...][arg]
   *       </code> AST part, if available.
   * </ul>
   *
   * <p><b>Note:</b> the result at index <code>[2]</code> maybe <code>null</code> , if no argument
   * is available.
   *
   * @return <code>null</code> if the expression is not a <code>Derivative[number][symbol][arg]
   *     </code> or <code>Derivative[number][symbol]</code> expression.
   */
  default IAST[] isDerivativeAST1() {
    return null;
  }

  /**
   * Test if this expression is representing a DirectedInfinity (i.e. <code>
   * Infinity->DirectedInfinity[1]</code>, <code>-Infinity->DirectedInfinity[-1]</code>, <code>
   * ComplexInfinity->DirectedInfinity[]</code>)
   *
   * @return
   */
  default boolean isDirectedInfinity() {
    return false;
  }

  /**
   * Test if this expression is representing a DirectedInfinity (i.e. <code>
   * Infinity->DirectedInfinity[1]</code>, <code>-Infinity->DirectedInfinity[-1]</code>, <code>
   * ComplexInfinity->DirectedInfinity[]</code>)
   *
   * @param x
   * @return
   */
  default boolean isDirectedInfinity(IExpr x) {
    return false;
  }

  /**
   * Test if this expression is a discrete distribution AST (i.e. BinomialDistribution(),
   * PoissonDistribution(),...)
   *
   * @return
   */
  default boolean isDiscreteDistribution() {
    return false;
  }

  /**
   * Test if this expression is a distribution AST (i.e. NormalDistribution(),
   * PoissonDistribution(),...)
   *
   * @return
   */
  default boolean isDistribution() {
    return false;
  }

  /**
   * Test if this expression equals <code>E</code> (base of the natural logarithm; approximately
   * equal to 2.71828...) in symbolic or numeric mode. <br>
   * See <a href="http://en.wikipedia.org/wiki/E_%28mathematical_constant%29">e (mathematical
   * constant)</a>
   *
   * @return
   */
  default boolean isE() {
    return false;
  }

  /**
   * Test if this expression is an DirectedEdge, UndirectedEdge, Rule, TwoWayRule.
   *
   * @return
   */
  default boolean isEdge() {
    return false;
  }

  /**
   * Test if this expression is an empty list (i.e. a list <code>{}</code>)
   *
   * @return
   */
  default boolean isEmptyList() {
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
   * Are the given evaluation flags disabled for this list ?
   *
   * @param flags
   * @return
   * @see IAST#NO_FLAG
   */
  default boolean isEvalFlagOff(int flags) {
    return true;
  }

  /**
   * Are the given evaluation flags enabled for this list ?
   *
   * @param flags
   * @return
   * @see IAST#NO_FLAG
   */
  default boolean isEvalFlagOn(int flags) {
    return false;
  }

  /**
   * Check if this expression is an even integer result otherwise return false.
   *
   * @return <code>true</code> if this is an even integer result.
   */
  default boolean isEvenResult() {
    if (isInteger()) {
      return ((IInteger) this).isEven();
    }
    if (isIntegerResult()) {
      if (isTimes()) {
        IAST timesAST = (IAST) this;
        if (timesAST.exists(x -> x.isInteger() && ((IInteger) x).isEven())) {
          return true;
        }
      }
    }
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
   * Test if this expression is the <code>Except</code> function <code>Except(&lt;pattern1&gt;)
   * </code> or <code>Except(&lt;pattern1&gt;, &lt;pattern2&gt;)</code>
   *
   * @return
   */
  default boolean isExcept() {
    return false;
  }

  /**
   * Test if this expression is the function <code>E^&lt;x&gt;</code> or in full form <code>
   * Power[E, &lt;x&gt;]</code>
   *
   * @return
   */
  default boolean isExp() {
    return isPower() && first().isE();
  }

  /**
   * Test if this expression is already expanded i.e. <code>Plus, Times, Power</code> expression is
   * expanded.
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
   * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. a function
   * symbol like for example <code>Dot, Plus or Times</code>) with attribute <code>Flat</code> at
   * index position <code>0</code> and some optional <b>argument elements</b> at the index positions
   * <code>1..(size()-1)</code>. Examples for <code>Flat</code> functions are <code>
   * Dot[], Plus[] or Times[]</code>. Therefore this expression is no <b>atomic expression</b>.
   *
   * @return
   * @see #isAtom()
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
   * Returns <code>true</code>, if <b>all of the elements</b> (including the head expression) in the
   * subexpressions or the expression itself, did not match the given pattern. Calls <code>
   * isFree(pattern, true)</code>.
   *
   * @param pattern a pattern-matching expression
   * @return
   */
  default boolean isFree(IExpr pattern) {
    return isFree(pattern, true);
  }

  /**
   * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the
   * expression itself, did not match the given pattern.
   *
   * @param pattern a pattern-matching expression
   * @param heads if set to <code>false</code>, only the arguments of an IAST should be tested and
   *     not the <code>Head[]</code> element.
   * @return
   */
  default boolean isFree(IExpr pattern, boolean heads) {
    Predicate<IExpr> matcher = Predicates.toFreeQ(pattern);
    return isFree(matcher, heads);
  }

  /**
   * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the
   * expression itself, did not satisfy the given unary predicate.
   *
   * @param predicate a unary predicate
   * @param heads if set to <code>false</code>, only the arguments of an IAST should be tested and
   *     not the <code>Head[]</code> element.
   * @return
   */
  default boolean isFree(IPatternMatcher predicate, boolean heads) {
    return !predicate.test(this);
  }

  /**
   * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the
   * expression itself, did not satisfy the given unary predicate.
   *
   * @param predicate a unary predicate
   * @param heads if set to <code>false</code>, only the arguments of an IAST should be tested and
   *     not the <code>Head[]</code> element.
   * @return
   */
  default boolean isFree(Predicate<IExpr> predicate, boolean heads) {
    return !predicate.test(this);
  }

  /**
   * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the
   * expression itself, aren't ASTs with a head which match the given pattern.
   *
   * @param pattern a pattern-matching expression
   * @return
   */
  default boolean isFreeAST(IExpr pattern) {
    return true;
  }

  /**
   * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the
   * expression itself, aren't ASTs with a head which match the given predicate.
   *
   * @param predicate a unary predicate
   * @return
   */
  default boolean isFreeAST(Predicate<IExpr> predicate) {
    return true;
  }

  /**
   * Returns <code>true</code>, if <b>all of the elements</b> in the subexpressions or the
   * expression itself, are no pattern objects.
   *
   * @return <code>true</code> if the expression contains no <code>IPatternObject</code>.
   */
  default boolean isFreeOfPatterns() {
    return true;
  }

  /**
   * Test if this expression is a <code>Function( arg1 )</code> or <code>Function( arg1, arg2 )
   * </code> expression with at least 1 argument.
   *
   * @return
   * @see #isPureFunction()
   */
  default boolean isFunction() {
    return false;
  }

  /**
   * Compares this expression with the specified expression for order. Returns true if this
   * expression is canonical greater than or equal to the specified expression (&lt;= relation).
   *
   * @param expr an expression to compare with
   * @return true if this expression is canonical greater than or equal to the specified expression.
   */
  default boolean isGEOrdered(IExpr expr) {
    return compareTo(expr) >= 0;
  }

  /**
   * Compares this expression with the specified expression for order. Returns true if this
   * expression is canonical greater than the specified expression (&lt; relation).
   *
   * @param expr an expression to compare with
   * @return true if this expression is canonical greater than the specified expression.
   */
  default boolean isGTOrdered(IExpr expr) {
    return compareTo(expr) > 0;
  }

  /**
   * Test if this expression is an AST list, which contains a <b>header element</b> with attribute
   * {@link ISymbol#HOLDALLCOMPLETE} at index position <code>0</code>.
   *
   * @return
   */
  default boolean isHoldAllCompleteAST() {
    return false;
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
   * Test if this expression is the <code>HoldPattern</code> function <code>
   * HoldPattern[&lt;expression&gt;]</code> or the deprecated <code>Literal[&lt;expression&gt;]
   * </code> form.
   *
   * @return
   */
  default boolean isHoldPatternOrLiteral() {
    return false;
  }

  /**
   * Test if this expression is a hyperbolic function.
   *
   * <p><b> Note</b>: All detected function types have 1 argument.
   *
   * @return
   */
  default boolean isHyperbolicFunction() {
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
   * Test if this expression is representing <code>Infinity</code> (i.e. <code>
   * Infinity->DirectedInfinity[1]</code>)
   *
   * @return
   */
  default boolean isInfinity() {
    return false;
  }

  /**
   * Test if this expression is a integer number (i.e. instance of type <code>IInteger</code>):
   *
   * @return
   */
  default boolean isInteger() {
    return this instanceof IInteger;
  }

  /**
   * Test if this expression is a integer function (i.e. a number, a symbolic constant or an integer
   * function where all arguments are also &quot;integer functions&quot;)
   *
   * @return <code>true</code>, if the given expression is a integer function or value.
   * @see #isRealResult()
   */
  default boolean isIntegerResult() {
    if (S.True.equals(AbstractAssumptions.assumeInteger(this))) {
      return true;
    }
    return this instanceof IInteger;
  }

  /**
   * Test if this expression is an interval expression with one or more <code>List[min, max]</code>
   * arguments <code>Interval[{min1, max1}, {min2, max2}, ...]</code> which represent the union of
   * the interval ranges.
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
   * Compares this expression with the specified expression for order. Returns true if this
   * expression is canonical less than or equal to the specified expression (&lt;= relation).
   *
   * @param expr an expression to compare with
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
   * Test if this expression is an {@link IAST} list, which contains a <b>header element</b> (i.e. a
   * function symbol at index position <code>0</code> and some optional <b>argument elements</b> at
   * the index positions <code>1..n</code>. Examples for <code>Listable</code> functions are <code>
   * Cos[], Plus[] or Times[]
   * </code>. Therefore this expression is no <b>atomic expression</b>.
   *
   * @return
   * @see #isAtom()
   */
  default boolean isListableAST() {
    return false;
  }

  /**
   * Test if this expression is a list (i.e. an AST with head List) with all arguments fulfill the
   * predicate.
   *
   * @param predicate
   * @return
   */
  default boolean isList(Predicate<IExpr> predicate) {
    return false;
  }

  /**
   * Test if this expression is a list (i.e. an AST with head List) with exactly 1 arguments
   *
   * @return
   */
  default boolean isList1() {
    return isList() && size() == 2;
  }

  /**
   * Test if this expression is a list (i.e. an AST with head List) with exactly 2 arguments
   *
   * @return
   */
  default boolean isList2() {
    return isList() && size() == 3;
  }

  /**
   * Test if this expression is a list (i.e. an AST with head List) with exactly 3 arguments
   *
   * @return
   */
  default boolean isList3() {
    return isList() && size() == 4;
  }

  /**
   * Test if this expression is a list of DirectedEdge or UndirectedEdge
   *
   * @return
   */
  default GraphType isListOfEdges() {
    return null;
  }

  /**
   * Test if this expression is a list of lists <code>{{...},{...},...}</code> and contains at least
   * 1 sublist. The sublists are allowed to be empty lists.
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
   * Test if this expression is a list of matrices and contains at least 1 matrix.
   *
   * @return
   */
  default boolean isListOfMatrices() {
    return false;
  }

  /**
   * Test if this expression is a list of rules (head Rule or RuleDelayed)
   *
   * @return
   * @see #isList()
   * @see #isMatrix(boolean)
   * @see #isVector()
   */
  default boolean isListOfRules() {
    return isListOfRules(false);
  }

  /**
   * Test if this expression is a list of rules (head Rule or RuleDelayed)
   *
   * @param ignoreEmptySublists if <code>true</code>, ignore elements which equals an empty list
   *     <code>
   *     { }</code>
   * @return
   * @see #isList()
   * @see #isMatrix(boolean)
   * @see #isVector()
   */
  default boolean isListOfRules(boolean ignoreEmptySublists) {
    return false;
  }

  /**
   * Test if this expression is a list of rules (head Rule or RuleDelayed) or an Association.
   *
   * @param ignoreEmptySublists if <code>true</code>, ignore elements which equals an empty list
   *     <code>
   *     { }</code> but only in lists.
   * @return
   */
  default boolean isListOfRulesOrAssociation(boolean ignoreEmptySublists) {
    return false;
  }

  /**
   * Test if this expression is a list of strings and contains at least 1 element.
   *
   * @return
   * @see #isList()
   * @see #isMatrix(boolean)
   * @see #isVector()
   */
  default boolean isListOfStrings() {
    return false;
  }

  /**
   * Test if this expression is a list (i.e. an AST with head List) or an Association
   *
   * @return
   */
  default boolean isListOrAssociation() {
    return isList();
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
   * Compares this expression with the specified expression for order. Returns true if this
   * expression is canonical less than the specified expression (&lt; relation).
   *
   * @param expr an expression to compare with
   * @return true if this expression is canonical less than the specified expression.
   */
  default boolean isLTOrdered(IExpr expr) {
    return compareTo(expr) < 0;
  }

  /**
   * Test if this expression is a machine-precision (Java double type) real or complex number. I.e.
   * an instance of type <code>Num</code> or <code>ComplexNum</code>.
   *
   * @return
   */
  default boolean isMachineNumber() {
    return this instanceof Num || this instanceof ComplexNum;
  }

  /**
   * Test if this expression is a matrix and return the lengths as array [row-length,
   * column-length]. This expression is only a matrix, if it is a <code>ASTRealMatrix</code> or a
   * <code>List(...)</code> where all elements are lists with the header <code>List</code> and have
   * the same size or a <code>SparseArray(...)</code> of dimension <code>2</code>;
   *
   * @return <code>null</code> if the expression is not a matrix
   */
  default int[] isMatrix() {
    return isMatrix(true);
  }

  /**
   * Test if this expression is a matrix and return the dimensions as array [row-dimension,
   * column-dimension]. This expression is only a matrix, if it is a <code>ASTRealMatrix</code> or a
   * <code>List(...)</code> where all elements are lists with the header <code>List</code> and have
   * the same size.
   *
   * @param setMatrixFormat set the <code>IAST.IS_MATRIX</code> flag for formatting as a matrix.
   * @return <code>null</code> if the expression is not a matrix
   */
  default int[] isMatrix(boolean setMatrixFormat) {
    // default: no matrix
    return null;
  }

  /**
   * Test if this expression is a matrix and return the dimensions as array [row-dimension,
   * column-dimension]. This expression is a matrix, if it is a <code>ASTRealMatrix</code> or a
   * <code>List(...)</code> where elements which could not be converted to a row vector are ignored.
   *
   * @return <code>null</code> if the expression is not a matrix
   */
  default int[] isMatrixIgnore() {
    // default: no matrix
    return isMatrix(true);
  }

  /**
   * Returns <code>true</code>, if <b>at least one of the elements</b> in the subexpressions, match
   * the given pattern. By default <code>isMember()</code> only operates at level 1.
   *
   * @param pattern a pattern-matching expression
   * @return
   */
  default boolean isMember(IExpr pattern) {
    return isMember(pattern, false, null);
  }

  /**
   * Returns <code>true</code>, if <b>at least one of the elements</b> in the subexpressions, match
   * the given pattern. If <code>visitor==null</code> the <code>isMember()</code> method only
   * operates at level 1.
   *
   * @param pattern a pattern-matching expression
   * @param heads if set to <code>false</code>, only the arguments of an IAST should be tested and
   *     not the <code>Head[]</code> element.
   * @param visitor if <code>null</code> use <code>
   *     VisitorBooleanLevelSpecification(predicate, 1, heads)</code>
   * @return
   */
  default boolean isMember(IExpr pattern, boolean heads, IVisitorBoolean visitor) {
    Predicate<IExpr> predicate;
    if (pattern.isSymbol() || pattern.isNumber() || pattern.isString()) {
      predicate = x -> x.equals(pattern);
    } else {
      predicate = new PatternMatcher(pattern);
    }

    if (visitor == null) {
      visitor = new VisitorBooleanLevelSpecification(predicate, 1, heads);
    }
    return accept(visitor);
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
  default boolean isModule() {
    return false;
  }

  default boolean isModuleOrWithCondition() {
    return false;
  }

  /**
   * Test if this object is a negative signed number. For an <code>IAST</code> object the method
   * checks, if it is a numeric constant. If the <code>IAST</code> object evaluates to a negative
   * numeric expression this method returns <code>true</code>.
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
   * Test if this expression is representing <code>-Infinity</code> (i.e. <code>
   * -Infinity->DirectedInfinity[-1]</code>)
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
   * @return <code>true</code> if the expression is a negative signed expression
   */
  default boolean isNegativeSigned() {
    if (isNumber()) {
      if (((INumber) this).complexSign() < 0) {
        return true;
      }
    } else if (isTimes()) {
      IExpr arg1 = this.first();
      if (arg1.isNumber()) {
        if (((INumber) arg1).complexSign() < 0) {
          return true;
        }
      } else if (arg1.isNegativeInfinity()) {
        return true;
      }
    } else if (isPlus()) {
      IExpr arg1 = this.first();
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
   * Test if this expression is a list with at least one element (i.e. a list <code>{element, ...}
   * </code>)
   *
   * @return
   */
  default boolean isNonEmptyList() {
    return false;
  }

  /**
   * Test if this expression has a non-negative result (i.e. greater equal 0) or is assumed to be
   * non-negative.
   *
   * @return <code>true</code>, if the given expression is a non-negative function or value.
   * @see #isRealResult()
   */
  default boolean isNonNegativeResult() {
    return AbstractAssumptions.assumeNonNegative(this);
  }

  /**
   * Test if this expression unequals <code>0</code> and is a numeric complex value or is assumed to
   * be a negative or positive value.
   *
   * @return
   */
  default boolean isNonZeroComplexResult() {
    if (isZero()) {
      return false;
    }
    if (isNonZeroRealResult()) {
      return true;
    }
    if (isNumber()) {
      return true;
    }
    return false;
  }

  /**
   * Test if this expression unequals <code>0</code> and is a numeric real value or is assumed to be
   * a negative or positive value.
   *
   * @return
   */
  default boolean isNonZeroRealResult() {
    if (isZero()) {
      return false;
    }
    if (isNegativeResult() || isPositiveResult()) {
      return true;
    }
    if (isReal()) {
      return true;
    }
    if (isNegativeInfinity() || isInfinity()) {
      return true;
    }
    return false;
  }

  /**
   * Test if this expression is the function <code>Not[&lt;arg&gt;]</code>.
   *
   * @return
   */
  default boolean isNot() {
    return false;
  }

  default boolean isNotDefined() {
    return isIndeterminate() || isDirectedInfinity();
  }

  /**
   * Test if this expression is a number. I.e. an instance of type <code>INumber</code>.
   *
   * @return
   */
  default boolean isNumber() {
    return false;
  }

  /**
   * Check if this expression equals an <code>IInteger</code> value. The value of an <code>INum
   * </code> or the value of an <code>IInteger</code> object can be equal to <code>value</code>.
   *
   * @param value
   * @return
   * @throws ArithmeticException
   */
  default boolean isNumEqualInteger(IInteger value) throws ArithmeticException {
    return false;
  }

  /**
   * Check if this expression equals an <code>IRational</code> value. The value of an <code>IInteger
   * </code>, <code>IFraction</code> or the value of an <code>INum</code> object can be equal to
   * <code>value</code>.
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
   * @deprecated use {@link #isInexactNumber()}
   */
  @Deprecated
  default boolean isNumeric() {
    return isInexactNumber();
  }

  /**
   * Test if this expression is an instance of NumericArrayExpr
   *
   * @return
   */
  default boolean isNumericArray() {
    return false;
  }

  /**
   * Test if this expression is a numeric number (i.e. an instance of type <code>INum</code> or type
   * <code>IComplexNum</code>), an <code>ASTRealVector</code> or an <code>ASTRealMatrix</code>.
   *
   * @return
   */
  default boolean isNumericArgument() {
    return this instanceof INum
        || this instanceof IComplexNum
        || this instanceof ASTRealVector
        || this instanceof ASTRealMatrix;
  }

  /**
   * Test if this expression is an IAST and contains at least one numeric argument.
   *
   * @return
   */
  default boolean isNumericAST() {
    return false;
  }

  /**
   * Test if this expression is a numeric function (i.e. a number, a symbolic constant or a function
   * (with attribute NumericFunction) where all arguments are also &quot;numeric functions&quot;)
   * Calls <code>isNumericFunction(false)</code>.
   *
   * @return <code>true</code>, if the given expression is a numeric function or value.
   * @see #isRealResult()
   */
  default boolean isNumericFunction() {
    return isNumericFunction(false);
  }

  /**
   * Test if this expression is a numeric function (i.e. a number, a symbolic constant or a function
   * (with attribute NumericFunction) where all arguments are also &quot;numeric functions&quot;)
   *
   * @param allowList if <code>true</code> a <code>List(...)</code> AST is seen, as if it has
   *     attribute {@link ISymbol#NUMERICFUNCTION}
   * @return <code>true</code>, if the given expression is a numeric function or value.
   * @see #isRealResult()
   */
  default boolean isNumericFunction(boolean allowList) {
    return false;
  }

  /**
   * Test if this expression is a numeric function (i.e. a number, a symbolic constant or a function
   * (with attribute NumericFunction) where all arguments are also &quot;numeric functions&quot;)
   * under the assumption, that all variables contained in <code>varSet</code> are also numeric.
   *
   * @return <code>true</code>, if the given expression is a numeric function or value, assuming all
   *     variables contained in <code>varSet</code> are also numeric.
   */
  default boolean isNumericFunction(VariablesSet varSet) {
    return isNumericFunction(true) || varSet.contains(this);
  }

  /**
   * Test if this expression is a numeric function (i.e. a number, a symbolic constant or a function
   * (with attribute NumericFunction) where all arguments are also &quot;numeric functions&quot;)
   * under the assumption, that the <code>variable</code> is also numeric.
   *
   * @param variable
   * @return
   */
  default boolean isNumericFunction(IExpr variable) {
    return isNumericFunction(true) || variable.equals(this);
  }

  /**
   * Test if this expression is a numeric function (i.e. a number, a symbolic constant or a function
   * (with attribute NumericFunction) where all arguments are also &quot;numeric functions&quot;)
   * under the assumption, that all variables contained in <code>list</code> are also numeric.
   *
   * @param list a list of variable symbols
   * @return
   */
  default boolean isNumericFunction(Function<IExpr, String> list) {
    return isNumericFunction(true) || list.apply(this) != null;
  }

  /**
   * Test if this expression contains a numeric number (i.e. of type <code>INum</code> or <code>
   * IComplexNum</code>.
   *
   * @return <code>true</code>, if the given expression contains numeric number (i.e. of type <code>
   *     INum</code> or <code>IComplexNum</code>.
   * @see #isRealResult()
   */
  default boolean isNumericMode() {
    return isInexactNumber();
  }

  /**
   * Check if this expression represents an <code>int</code> value. The value of an <code>INum
   * </code> object can be an <code>int</code> value.
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
   * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. a function
   * symbol like for example <code>Plus or Times</code>) with attribute <code>OneIdentity</code> at
   * index position <code>0</code> and exactly <b>one argument</b> at the index position <code>1
   * </code>. Examples for <code>OneIdentity</code> functions are <code>Plus[] or Times[]</code>.
   * Therefore this expression is no <b>atomic expression</b>.
   *
   * @return
   * @see #isAtom()
   */
  default boolean isOneIdentityAST1() {
    return false;
  }

  /**
   * Test if this expression is the <code>Optional</code> function <code>Optional[&lt;pattern&gt;]
   * </code> or <code>Optional[&lt;pattern&gt;, &lt;value&gt;]</code>
   *
   * @return
   */
  default boolean isOptional() {
    return false;
  }

  /**
   * Test if this expression is the <code>OptionsPattern</code> function <code>OptionsPattern()
   * </code> or <code>OptionsPattern(&lt;symbol&gt;)</code>
   *
   * @return
   */
  default boolean isOptionsPattern() {
    return false;
  }

  /**
   * Test if this expression is the function <code>Or[&lt;arg&gt;,...]</code> and has at least 2
   * arguments.
   *
   * @return
   */
  default boolean isOr() {
    return false;
  }

  /**
   * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. a function
   * symbol like for example <code>Plus or Times</code>) with attribute <code>Orderless</code> at
   * index position <code>0</code> and some optional <b>argument elements</b> at the index positions
   * <code>1..n</code>. Examples for <code>Orderless</code> functions are <code>Plus[] or Times[]
   * </code>. Therefore this expression is no <b>atomic expression</b>.
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
   * Return <code>true</code>, if the expression is a pattern object with an associated default
   * value (for example <code>0</code> is the default value for the addition expression <code>x_+y_.
   * </code>)
   *
   * @return
   */
  default boolean isPatternDefault() {
    return false;
  }

  /**
   * Test if this expression or a subexpression is a pattern object. Used in pattern-matching;
   * checks flags in <code>IAST</code> with flag <code>IAST.CONTAINS_PATTERN_EXPR</code>.
   *
   * @return
   */
  default boolean isPatternExpr() {
    return false;
  }

  /**
   * Test if this expression is a special pattern-matching function (i.e. Alternatives, Except,...)
   *
   * @return
   */
  default boolean isPatternMatchingFunction() {
    return false;
  }

  /**
   * Return <code>true</code>, if the expression is a pattern object with an associated optional
   * value (for example <code>value</code> is the default value for the expression <code>
   * f[x_, y_:value]</code>)
   *
   * @return
   */
  default boolean isPatternOptional() {
    return false;
  }

  /**
   * Test if this expression is a pattern sequence object <code>__</code> or null pattern sequence
   * object <code>___</code>
   *
   * @param testNullSequence test if a sequence with no element is allowed
   * @return
   */
  default boolean isPatternSequence(boolean testNullSequence) {
    return false;
  }

  /**
   * Test if this expression is the <code>PatternTest</code> function <code>
   * PatternTest[&lt;pattern&gt;, &lt;test&gt;]</code>
   *
   * @return
   */
  default boolean isPatternTest() {
    return false;
  }

  /**
   * Test if this expression equals <code>Pi</code> (the ratio of a circle's circumference to its
   * diameter, approx. 3.141592...) in symbolic or numeric mode. <br>
   * See <a href="http://en.wikipedia.org/wiki/Pi">Pi</a>
   *
   * @return
   */
  default boolean isPi() {
    return false;
  }

  /**
   * Test if this expression is a <code>Piecewise({{...}}},...)</code> function and the first
   * argument is a matrix with dimension <code>[row-dimension, 2]</code> and <code>row-dimension > 0
   * </code>. Return the dimensions of the matrix as array <code>[row-dimension, column-dimension]
   * </code>. The first argument is only a matrix, if it is a <code>List(...)</code> where all
   * elements are lists with the header <code>List</code> and have the same size.
   *
   * @return <code>null</code> if the expression is not a <code>Piecewise({{...}}},...)</code>
   *     function or if the first argument is not a matrix
   */
  default int[] isPiecewise() {
    return null;
  }

  /**
   * Test if this expression is the addition function <code>Plus[&lt;arg1&gt;, &lt;arg2&gt;, ...]
   * </code> with at least 2 arguments.
   *
   * @return
   */
  default boolean isPlus() {
    return false;
  }

  /**
   * Test if this expression is the multiplication function <code>
   * Plus[&lt;arg1&gt;, &lt;arg2&gt;]</code> with exactly 2 arguments.
   *
   * @return
   */
  default boolean isPlus2() {
    return false;
  }

  /**
   * Test if this expression is the multiplication function <code>
   * Plus[&lt;arg1&gt;, &lt;arg2&gt;, &lt;arg3&gt;]</code> with exactly 3 arguments.
   *
   * @return
   */
  default boolean isPlus3() {
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
   * Test if this expression is a polynomial for the given list of <code>variables</code>.
   * <b>Note:</b> if the list contains no argument, this method returns <code>true</code> for a
   * <code>Symbol</code> expression.
   *
   * @param variables a list of variables or an empty list
   * @return <code>true</code> if this expression is a polynomial; <code>false</code>otherwise
   */
  default boolean isPolynomial(IAST variables) {
    return isNumber();
  }

  /**
   * Test if this expression is a polynomial for the given <code>variable</code>. <b>Note:</b> if
   * the variable is set to <code>null</code>, this method returns <code>true</code> for a <code>
   * Symbol</code> expression.
   *
   * @param variable the variable of the polynomial
   * @return
   */
  default boolean isPolynomial(IExpr variable) {
    return isNumber();
  }

  /**
   * Test if this expression is a polynomial of <code>maxDegree</code> (i.e. the maximum exponent
   * &lt;= maxDegree) for the given <code>variable</code>.
   *
   * @param variable the variable of the polynomial
   * @param maxDegree the maximum degree of the polynomial; maxDegree must be greater 0
   * @return
   */
  default boolean isPolynomialOfMaxDegree(ISymbol variable, long maxDegree) {
    return isPolynomial(F.List(variable));
  }

  /**
   * Test if this expression has a polynomial structiure, i.e. no built-in function as head
   *
   * @return <code>true</code> if this expression has a polynomial structure; <code>false</code>
   *     otherwise
   */
  default boolean isPolynomialStruct() {
    return isExactNumber();
  }

  /**
   * Test if this object is a positive signed number. For an <code>IAST</code> object the method
   * checks, if it is a numeric constant. If the <code>IAST</code> object evaluates to a positive
   * numeric expression this method returns <code>true</code>.
   *
   * @return <code>true</code>, if <code>this > 0</code>; <code>false</code> in all other case.
   */
  default boolean isPositive() {
    return false;
  }

  /**
   * Test if this expression has a positive result (i.e. greater than 0) or is assumed to be
   * positive.
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
   * Test if this expression is the function <code>Power[&lt;arg1&gt;, -1]</code> (i.e. <code>
   * 1 / &lt;arg1&gt;</code>). See: <a
   * href="https://en.wikipedia.org/wiki/Multiplicative_inverse">Wikipedia - Multiplicative
   * inverse</a>
   *
   * @return
   */
  default boolean isPowerReciprocal() {
    return isPower() && second().isMinusOne();
  }

  /**
   * Test if this expression is a <code>IBuiltInSymbol</code> symbol and the evaluator implements
   * <code>IPrediacte</code>.
   *
   * @return
   */
  default boolean isPredicateFunctionSymbol() {
    return false;
  }

  /**
   * Return {@code true} if this expression unequals <code>F.NIL</code>, otherwise {@code false}.
   * This method is similar to <code>java.util.Optional#isPresent()</code>.
   *
   * @return {@code true} if the expression unequals <code>F.NIL</code>, otherwise {@code false}.
   * @see java.util.Optional#isPresent()
   */
  default boolean isPresent() {
    return true;
  }

  /**
   * Test if this expression is a &quot;pure&quot; or &quot;anonymous&quot; <code>Function( arg1 )
   * </code> expression with exactly 1 argument.
   *
   * @return
   * @see #isFunction()
   */
  default boolean isPureFunction() {
    return false;
  }

  /**
   * Test if this expression is a <code>Quantity(a,unit)</code> expression.
   *
   * @return
   */
  default boolean isQuantity() {
    return false;
  }

  /**
   * Test if this expression is a symbolic rational number, i.e. integer or fraction number.
   *
   * @return
   * @see #isNumEqualRational(IRational)
   */
  default boolean isRational() {
    return this instanceof IRational;
  }

  /**
   * Test if this expression is a symbolic rational function (i.e. a number, a symbolic constant or
   * an rational function where all arguments are also &quot;rational functions&quot;)
   *
   * @return <code>true</code>, if the given expression is a rational function or value.
   * @see #isRealResult()
   */
  default boolean isRationalResult() {
    if (S.True.equals(AbstractAssumptions.assumeRational(this))) {
      return true;
    }
    return this instanceof IRational;
  }

  /**
   * Test if this expression equals the rational number <code>value</code> in symbolic or numeric
   * mode.
   *
   * @param value the rational number
   * @return
   */
  default boolean isRationalValue(IRational value) {
    return false;
  }

  /**
   * Test if this expression is a signed real number. I.e. an instance of type <code>IFraction
   * </code> for exact number values or <code>INum</code> for approximated numbers.
   *
   * @return
   */
  default boolean isReal() {
    return this instanceof ISignedNumber;
  }

  /**
   * Test if this expression is a <code>IBuiltInSymbol</code> symbol and the evaluator implements
   * the <code>ISignedNumberConstant</code> interface (see package <code>
   * org.matheclipse.core.builtin.constant</code>).
   *
   * @return
   */
  default boolean isRealConstant() {
    return false;
  }

  /**
   * Test if this expression is a real matrix (i.e. an ASTRealMatrix) or a <code>
   * List[List[...],...,List[...]]</code> matrix with elements of type <code>
   * org.matheclipse.core.expression.Num</code>.
   *
   * @return
   */
  default boolean isRealMatrix() {
    return false;
  }

  /**
   * Test if this expression is a number with no imaginary component. I.e. an instance of type
   * <code>IRational</code> or <code>INum</code>.
   *
   * @return
   * @deprecated use {@link isReal()}
   */
  @Deprecated
  default boolean isRealNumber() {
    return isReal();
  }

  /**
   * Test if this expression is a real function (i.e. a number, a symbolic constant or an integer
   * function where all arguments are also &quot;reals functions&quot;)
   *
   * @return <code>true</code>, if the given expression is a real function or value.
   * @see #isIntegerResult
   */
  default boolean isRealResult() {
    if (S.True.equals(AbstractAssumptions.assumeReal(this))) {
      return true;
    }
    return this instanceof ISignedNumber;
  }

  /**
   * Test if this expression is a real vector (i.e. an ASTRealVector) or a <code>List[...]</code>
   * with elements of type <code>org.matheclipse.core.expression.Num</code>.
   *
   * @return
   */
  default boolean isRealVector() {
    return false;
  }

  /**
   * Test if this expression is the <code>Repetead</code> function <code>Repetead(&lt;pattern1&gt;)
   * </code>.
   *
   * @return
   */
  default boolean isRepeated() {
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
   * Test if this expression is of the form <code>Rule[&lt;arg1&gt;, &lt;arg2&gt;]</code> or <code>
   * RuleDelayed[&lt;arg1&gt;, &lt;arg2&gt;]</code>.
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
   * Test if this expression equals the given expression. If the compared expressions are of the
   * same numeric type, they are equal to a given EPSILON
   *
   * @param expression
   * @return
   */
  default boolean isSame(IExpr expression) {
    return isSame(expression, Config.DOUBLE_EPSILON);
  }

  /**
   * Test if this expression equals the given expression. If the compared expressions are of the
   * same numeric type, they are equal to a given EPSILON
   *
   * @param expression
   * @param epsilon
   * @return
   */
  default boolean isSame(IExpr expression, double epsilon) {
    return equals(expression);
  }

  /**
   * Check if the object at index 0 (i.e. the head of the list) is the same object as <code>head
   * </code> and if the size of the list is greater or equal <code>length</code>.
   *
   * @param head object to compare with element at location <code>0</code>
   * @param length
   * @return
   */
  default boolean isSameHeadSizeGE(ISymbol head, int length) {
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
   * Test if this expression is a signed real number. I.e. an instance of type <code>IFraction
   * </code> for exact number values or <code>INum</code> for approximated numbers.
   *
   * @return
   * @deprecated use {@link #isReal()};
   */
  @Deprecated
  default boolean isSignedNumber() {
    return isReal();
  }

  /**
   * Test if this expression is a <code>IBuiltInSymbol</code> symbol and the evaluator implements
   * the <code>ISignedNumberConstant</code> interface (see package <code>
   * org.matheclipse.core.builtin.constant</code>).
   *
   * @return
   * @deprecated use {@link #isRealConstant()}
   */
  @Deprecated
  default boolean isSignedNumberConstant() {
    return isRealConstant();
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
   * Test if this expression is the function <code>Slot[&lt;integer-value&gt;]</code> (i.e. #1, #2,
   * #3,....)
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
   * @param size the size of an AST for which <code>Span[]</code> should be applied.
   * @return <code>null</code> if this is no <code>Span[...]</code> expression.
   */
  default int[] isSpan(int size) {
    return null;
  }

  /**
   * Test if this expression is a instance of SparseArrayExpr
   *
   * @return
   */
  default boolean isSparseArray() {
    return false;
  }

  /**
   * Test if this expression is the function <code>Power[&lt;arg1&gt;, 1/2]</code> (i.e. <code>
   * Sqrt[&lt;arg1&gt;]</code>).
   *
   * @return
   */
  default boolean isSqrt() {
    if (isPower() && second().isNumEqualRational(F.C1D2)) {
      return true;
    }
    return false;
  }

  /**
   * Test if this expression is the function <code>Power[&lt;arg1&gt;, 1/2]</code> (i.e. <code>
   * Sqrt[&lt;arg1&gt;]</code>) or <code>-Power[&lt;arg1&gt;, 1/2]</code> (i.e. <code>
   * -Sqrt[&lt;arg1&gt;]</code>)
   *
   * @return
   */
  default boolean isSqrtExpr() {
    if (isSqrt()) {
      return true;
    }
    if (isTimes() && first().equals(F.CN1) && size() == 3) {
      if (second().isPower() && second().second().isNumEqualRational(F.C1D2)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Test if this expression is the function <code>Power[&lt;arg1&gt;, 1/2]</code> (i.e. <code>
   * Sqrt[&lt;arg1&gt;]</code>) or <code>-Power[&lt;arg1&gt;, 1/2]</code> (i.e. <code>
   * -Sqrt[&lt;arg1&gt;]</code>)
   *
   * @return
   */
  default boolean isFactorSqrtExpr() {
    if (isSqrt()) {
      IExpr base = first();
      if (base.isRational() && base.isPositive()) {
        return true;
      }
      return false;
    }
    if (isTimes() && first().isRational() && size() == 3) {
      IExpr factor2 = second();
      if (factor2.isSqrt() && factor2.first().isRational() && factor2.first().isPositive()) {
        return true;
      }
    }
    return false;
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
   * Test if this expression is a string (instanceof IStringX) and equals <code>str</code>
   *
   * @return
   */
  default boolean isString(String str) {
    return this instanceof IStringX && toString().equals(str);
  }

  default boolean isStringIgnoreCase(String str) {
    return this instanceof IStringX && toString().equalsIgnoreCase(str);
  }

  /**
   * Test if this expression is the function <code>Subscript[var, &lt;integer-value&gt;]</code>.
   * <code>var</code> has to be a variable.
   *
   * @return
   */
  default boolean isSubscript() {
    return false;
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
   * Test if this expression is a symbol (instanceof ISymbol) or a pattern object (instanceof
   * IPatternObject)
   *
   * @return
   */
  default boolean isSymbolOrPattern() {
    return this instanceof ISymbol || this instanceof IPatternObject;
  }

  /**
   * Test if this expression is the function <code>Tan[&lt;arg&gt;]</code>
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
   * Test if this expression is the multiplication function <code>
   * Times[&lt;arg1&gt;, &lt;arg2&gt;, ...]</code> with at least 2 arguments.
   *
   * @return
   */
  default boolean isTimes() {
    return false;
  }

  /**
   * Test if this expression is the multiplication function <code>
   * Times[&lt;arg1&gt;, &lt;arg2&gt;]</code> with exactly 2 arguments.
   *
   * @return
   */
  default boolean isTimes2() {
    return false;
  }

  /**
   * Test if this expression is the multiplication function <code>
   * Times[&lt;arg1&gt;, &lt;arg2&gt;, &lt;arg3&gt;]</code> with exactly 3 arguments.
   *
   * @return
   */
  default boolean isTimes3() {
    return false;
  }

  /**
   * Test if this expression is a trigonometric function.
   *
   * <p><b> Note</b>: ArcTan(x,y) can have 2 arguments and is considered as a trigonometric
   * function. All other detected function types have 1 argument.
   *
   * @return
   */
  default boolean isTrigFunction() {
    return false;
  }

  /**
   * Test if this expression equals the symbol <code>True</code>.
   *
   * @return <code>true</code> if the expression equals symbol <code>True</code> and <code>false
   *     </code> in all other cases
   */
  default boolean isTrue() {
    return false;
  }

  /**
   * Test if this expression is the function <code>Unevaluated[&lt;arg&gt;]</code>
   *
   * @return
   */
  default boolean isUnevaluated() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  default boolean isUnit() {
    return true;
  }

  /**
   * Returns <code>true</code>, if this symbol or ast expression is bound to a value (i.e. the
   * evaluation returns an <i>assigned</i> value).
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
   * @see #isConstantAttribute()
   * @see #isSymbol()
   */
  default boolean isVariable() {
    return false;
  }

  /**
   * Test if this expression is a vector and return the length of the vector. This expression is
   * only a vector, if the expression is a <code>ASTRealVector</code> or a <code>List(...)</code>
   * and no element is itself a <code>List(...)</code> or a <code>SparseArray(...)</code> of
   * dimension <code>1</code>;
   *
   * @return <code>-1</code> if the expression is no vector or <code>size()-1</code> of this vector
   *     AST.
   */
  default int isVector() {
    // default: no vector
    return -1;
  }

  /**
   * Test if this expression is the With function <code>With[&lt;arg1&gt;, &lt;arg2&gt;]</code>
   *
   * @return
   */
  default boolean isWith() {
    return false;
  }

  /**
   * Test if this expression equals <code>0</code> in symbolic or numeric mode.
   *
   * @return
   */
  @Override
  default boolean isZero() {
    return false;
  }

  /**
   * Test if this expression equals <code>0</code> in symbolic or numeric mode. For the numeric test
   * multiple random numbers with a <code>Chop()</code> function test are used.
   *
   * @param fastTest checks only numerical; no symbolic tests are tried.
   * @return
   */
  default boolean isPossibleZero(boolean fastTest) {
    return isZero();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Calls <code>PossibleZeroQ()</code>
   */
  @Override
  default boolean isZERO() {
    if (isNumber()) {
      return isZero();
    }
    return isAST() && PredicateQ.isPossibleZeroQ((IAST) this, false, EvalEngine.get());
    // PredicateQ.isZeroTogether(this, EvalEngine.get());
  }

  /**
   * Get the last element of the <code>AST</code> list (i.e. get(size()-1). Return <code>F.NIL
   * </code> if this object isn't an <code>AST</code>or has <code>0</code> arguments (i.e. only a
   * header element)
   *
   * @return the last argument of the function represented by this <code>AST</code> or {@link F#NIL}
   * @see IExpr#head()
   */
  default IExpr last() {
    return F.NIL;
  }

  /**
   * Count the number of indivisible subexpressions (atoms/leaves) of this expression.
   *
   * @return
   */
  default long leafCount() {
    return isAtom() ? 1L : 0L;
  }

  /**
   * Count the number of leaves of this expression; for integer numbers in exact integer, fractional
   * and complex numbers count the digits of the integers. This function is used in <code>Simplify
   * </code> as the default &quot;complexity function&quot;.
   *
   * @return
   */
  default long leafCountSimplify() {
    return leafCount();
  }

  /**
   * Evaluate Less, if both arguments are real numbers
   *
   * @param a1
   * @return
   */
  default IExpr less(final IExpr a1) {
    if (isReal() && a1.isReal()) {
      return ((ISignedNumber) this).isLT(((ISignedNumber) a1)) ? S.True : S.False;
    }
    EvalEngine engine = EvalEngine.get();
    return engine.evaluate(F.Less(this, a1));
  }

  /**
   * Evaluate LessEqual, if both arguments are real numbers
   *
   * @param a1
   * @return
   */
  default IExpr lessEqual(final IExpr a1) {
    if (isReal() && a1.isReal()) {
      return ((ISignedNumber) this).isGT(((ISignedNumber) a1)) ? S.False : S.True;
    }
    EvalEngine engine = EvalEngine.get();
    return engine.evaluate(F.LessEqual(this, a1));
  }

  /**
   * Compare if <code>this <= that</code:
   * <ul>
   * <li>return S.True if the comparison is <code>true</code></li>
   * <li>return S.False if the comparison is <code>false</code></li>
   * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
   * </ul>
   *
   * @param that
   * @return <code>S.True, S.False or F.NIL</code
   */
  public default IExpr lessEqualThan(IExpr that) {
    COMPARE_TERNARY temp = BooleanFunctions.CONST_LESS_EQUAL.prepareCompare(this, that);
    return convertToExpr(temp);
  }

  /**
   * Compare if <code>this < that</code:
   * <ul>
   * <li>return S.True if the comparison is <code>true</code></li>
   * <li>return S.False if the comparison is <code>false</code></li>
   * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
   * </ul>
   *
   * @param that
   * @return <code>S.True, S.False or F.NIL</code
   */
  public default IExpr lessThan(IExpr that) {
    COMPARE_TERNARY temp = BooleanFunctions.CONST_LESS.prepareCompare(this, that);
    return convertToExpr(temp);
  }

  /**
   * If this is a linear expression <code>a + b * x</code> return the addend <code>a</code> at index
   * <code>0</code> and the multiplicant <code>b</code> at index <code>1</code>
   *
   * @param variable the variable <code>x</code> to check for linearity
   * @return <code>null</code> if this is not a linear expression
   */
  default IExpr[] linear(IExpr variable) {
    return null;
  }

  /**
   * If this is an expression of the form <code>a + b * x^n</code> with <code>n</code> integer,
   * return the addend at index <code>0</code> and the multiplicant at index <code>0</code> and the
   * exponent <code>n</code> at index <code>2</code>.
   *
   * @param variable the variable <code>x</code> in the formula
   * @return <code>null</code> if this is not an expression of the form <code>a+b*x^n</code>
   */
  default IExpr[] linearPower(IExpr variable) {
    return null;
  }

  /**
   * If this is a <code>Interval[{lower, upper}]</code> expression return the <code>lower</code>
   * value. If this is a <code>ISignedNumber</code> expression return <code>this</code>.
   *
   * @return <code>F.NIL</code> if this expression is no interval and no signed number.
   */
  public default IExpr lower() {
    return F.NIL;
  }

  /**
   * If a value is present (i.e. this unequals F.NIL), apply the provided mapping function to it,
   * and if the result is non-NIL, return the result. Otherwise return <code>F.NIL</code>
   *
   * @param mapper a mapping function to apply to the value, if present
   * @return an IExpr describing the result of applying a mapping function to the value of this
   *     object, if a value is present, otherwise return <code>F.NIL</code>.
   */
  default IExpr mapExpr(Function<? super IExpr, ? extends IExpr> mapper) {
    return mapper.apply(this);
  }

  /**
   * This method assumes that <code>this</code> is a list of lists in matrix form. It combines the
   * column values in a list as argument for the given <code>function</code>. <b>Example</b> a
   * matrix <code>{{x1, y1,...}, {x2, y2, ...}, ...}</code> will be converted to <code>
   * {f.apply({x1, x2,...}), f.apply({y1, y2, ...}), ...}</code>
   *
   * @param dim the dimension of the matrix
   * @param f a unary function
   * @return
   */
  default IExpr mapMatrixColumns(int[] dim, Function<IExpr, IExpr> f) {
    return F.NIL;
  }

  /**
   * Returns an <code>IExpr</code> whose value is <code>(this - that)</code>. Calculates <code>
   * F.eval(F.Plus(this, F.Times(F.CN1, that)))</code> in the common case and uses a specialized
   * implementation for derived number classes.
   *
   * @param that
   * @return
   */
  default IExpr minus(final IExpr that) {
    return subtract(that);
  }

  default IExpr mod(final IExpr that) {
    return S.Mod.of(this, that);
  }

  /**
   * Get the elements of the <code>AST</code> or <code>ASTAssociation
   * </code> list with the last element removed. Return <code>F.NIL</code> if this object isn't an
   * <code>AST</code> or <code>ASTAssociation
   * </code>.
   *
   * @return the argument of the function represented by this <code>AST</code> with the last element
   *     removed or {@link F#NIL}
   * @see IExpr#head()
   */
  default IExpr most() {
    return F.NIL;
  }

  /**
   * Additional multiply method which works with overriden <code>JAS</code> method.
   *
   * @param that
   * @return
   * @see IExpr#times(IExpr)
   */
  @Override
  default IExpr multiply(final IExpr that) {
    // if (isZero()) {
    // return this;
    // }
    // if (that.isZero()) {
    // return that;
    // }
    // if (isOne()) {
    // return that;
    // }
    // if (that.isOne()) {
    // return this;
    // }
    // if (isPlus() && !that.isPlus()) {
    // if (that.isAtom() || (that.isPower() && that.base().isAtom())) {
    // IExpr temp = ((IAST) this).mapThread(F.binaryAST2(F.Times, null, that), 1);
    // return EvalEngine.get().evaluate(temp);
    // }
    // } else if (!isPlus() && that.isPlus()) {
    // if (isAtom() || (isPower() && base().isAtom())) {
    // IExpr temp = ((IAST) that).mapThread(F.binaryAST2(F.Times, this, null), 2);
    // return EvalEngine.get().evaluate(temp);
    // }
    // }
    return times(that);
  }

  @Override
  public default IExpr multiply(int n) {
    if (isPlus()) {
      return F.evalExpand(times(F.ZZ(n)));
    }
    return times(F.ZZ(n));
  }

  /**
   * Multiply <code>this * that</code>. If oneof the arguments is a <code>Plus</code> expression,
   * distribute the other expression other <code>Plus</code>.
   *
   * @param that
   * @return
   */
  default IExpr multiplyDistributed(final IExpr that) {
    if (isZero()) {
      return this;
    }
    if (that.isZero()) {
      return that;
    }
    if (isOne()) {
      return that;
    }
    if (that.isOne()) {
      return this;
    }
    if (isPlus()) {
      if (that.isPlus()) {
        IExpr temp = ((IAST) this).map(x -> x.multiplyDistributed(that), 1);
        return EvalEngine.get().evaluate(temp);
      }
      IExpr temp = ((IAST) this).mapThread(F.binaryAST2(S.Times, F.Slot1, that), 1);
      return EvalEngine.get().evaluate(temp);
    } else if (that.isPlus()) {
      IExpr temp = ((IAST) that).mapThread(F.binaryAST2(S.Times, this, F.Slot1), 2);
      return EvalEngine.get().evaluate(temp);
    }
    return times(that);
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
   * Nest <code>this</code> expression with <code>head</code> applied <code>n</code> times to <code>
   * this</code>.
   *
   * <pre>
   *   this.nest(h, 4)
   * </pre>
   *
   * gives
   *
   * <pre>
   *   h(h(h(h(this))))
   * </pre>
   *
   * @param head the head which should be applied to this n times
   * @param n a value > 0, otherwise <code>this</code> will be returned as default value
   * @return
   */
  default IExpr nest(final IExpr head, final int n) {
    IExpr temp = this;
    final Function<IExpr, IExpr> function = x -> F.unaryAST1(head, x);
    for (int i = 0; i < n; i++) {
      temp = function.apply(temp);
    }
    return temp;
  }

  /**
   * Converts a <b>special expression</b> (like a series, association, dataset, sparse array, ...)
   * into a standard <i>normalized</i> expression.
   *
   * <pre>
   * &gt;&gt; Normal(SeriesData(x, 0, {1, 0, -1, -4, -17, -88, -549}, -1, 6, 1))
   * 1/x-x-4*x^2-17*x^3-88*x^4-549*x^5
   * </pre>
   *
   * @param nilIfUnevaluated if <code>true</code> return <code>F.NIL</code>, if no evaluation is
   *     necessary, otherwise <code>this</code>.
   * @return the standard expression for <b>special expression</b> or <code>F.NIL</code> otherwise
   */
  default IExpr normal(boolean nilIfUnevaluated) {
    return nilIfUnevaluated ? F.NIL : this;
  }

  /**
   * Returns an <code>IExpr</code> whose value is <code>(-1) * this</code>. Calculates <code>
   * F.eval(F.Times(F.CN1, this))</code> in the common case and uses a specialized implementation
   * for derived number classes.
   *
   * @return
   * @see #negative()
   */
  default IExpr opposite() {
    return times(F.CN1);
  }

  /**
   * The <code>F.NIL#optional()</code> method always returns <code>that</code>. All other objects
   * which implement this method returns <code>that</code> if <code>that!=null</code> or <code>this
   * </code> if <code>that==null</code>
   *
   * @return <code>that</code> if <code>that!=null</code> or <code>this</code> in all other cases.
   * @see NILPointer#optional(IExpr)
   */
  default IExpr optional() {
    return S.exprID(this);
  }

  /**
   * Apply the <code>Or</code> operator
   *
   * @param that
   * @return
   * @deprecated use {@link F#Or(IExpr, IExpr)}
   */
  @Deprecated
  default IExpr or(final IExpr that) {
    return F.Or(this, that);
  }

  /**
   * Return <code>this</code> if <code>this</code> unequals <code>F.NIL</code> , otherwise return
   * <code>other</code>.
   *
   * @param other
   * @return <code>this</code> if <code>this</code> unequals <code>F.NIL</code>, otherwise return
   *     <code>other</code>.
   * @see java.util.Optional#orElse(Object)
   */
  default IExpr orElse(final IExpr other) {
    return this;
  }

  /**
   * Return <code>this</code> if <code>this</code> unequals <code>F.NIL</code> , otherwise invoke
   * {@code other} and return the result of that invocation.
   *
   * @param other a {@code Supplier} whose result is returned if no value is present
   * @return <code>this</code> if <code>this</code> unequals <code>F.NIL</code>, otherwise the
   *     result of {@code other.get()}
   */
  default IExpr orElseGet(Supplier<? extends IExpr> other) {
    return this;
  }

  /**
   * Return <code>this</code> if <code>this</code> unequals <code>F.NIL</code> , otherwise throw an
   * exception to be created by the provided supplier.
   *
   * @apiNote A method reference to the exception constructor with an empty argument list can be
   *     used as the supplier. For example, {@code IllegalStateException::new}
   * @param <X> Type of the exception to be thrown
   * @param exceptionSupplier The supplier which will return the exception to be thrown
   * @return <code>this</code> if <code>this</code> unequals <code>F.NIL</code> or throw an
   *     exception
   * @throws X if there is no value present
   */
  default <X extends Throwable> IExpr orElseThrow(Supplier<? extends X> exceptionSupplier)
      throws X {
    return this;
  }

  /**
   * Return <code>this</code> if <code>isList()==true</code>, otherwise create a new list <code>
   * {this}</code> from this (i.e. return <code>F.List(this)</code>).
   *
   * @return <code>this</code> if <code>isList()==true</code>, otherwise return <code>F.List(this)
   *     </code>.
   */
  default IAST orNewList() {
    return isList() ? (IAST) this : F.List(this);
  }

  /**
   * Select all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and append the arguments which satisfy the predicate to the <code>1st element</code> of
   * the resulting AST, or otherwise append it to the <code>2nd element</code> of the resulting AST.
   *
   * <p>See: <a href= "https://people.eecs.berkeley.edu/~fateman/papers/partition.pdf">Fateman -
   * Partitioning of Algebraic Subexpressions in Computer Algebra Systems</a>
   *
   * @param operator the if the head of this expression equals <code>operator</code>, otherwise
   *     return <code>F.NIL</code>.
   * @param predicate the predicate which filters each element in this AST
   * @param initTrue the result for the 1st result element, if the predicate doesn't give <code>true
   *     </code> for any of the arguments in this AST.
   * @param initFalse the result for the 2nd result element, if the predicate doesn't give <code>
   *     false</code> for any of the arguments in this AST.
   * @param combiner the 1st and 2md results element head
   * @return <code>F.NIL</code> if partitioning wasn't possible
   */
  default IAST partition(
      ISymbol operator,
      Predicate<? super IExpr> predicate,
      IExpr initTrue,
      IExpr initFalse,
      ISymbol combiner,
      ISymbol action) {
    return F.NIL;
  }

  /**
   * Select all elements by applying the <code>predicate</code> to each argument in this <code>
   * Plus(...)</code> expression and append the arguments which satisfy the predicate to the <code>
   * 1st element</code> of the resulting AST, or otherwise append it to the <code>2nd element</code>
   * of the resulting AST.
   *
   * <p>See: <a href= "https://people.eecs.berkeley.edu/~fateman/papers/partition.pdf">Fateman -
   * Partitioning of Algebraic Subexpressions in Computer Algebra Systems</a>
   *
   * @param predicate the predicate which filters each element in this AST
   * @param initTrue the result for the 1st result element, if the predicate doesn't give <code>true
   *     </code> for any of the arguments in this AST.
   * @param initFalse the result for the 2nd result element, if the predicate doesn't give <code>
   *     false</code> for any of the arguments in this AST.
   * @return <code>F.NIL</code> if partitioning wasn't possible
   */
  default IAST partitionPlus(
      Predicate<? super IExpr> predicate, IExpr initTrue, IExpr initFalse, ISymbol action) {
    return F.NIL;
  }

  /**
   * Select all elements by applying the <code>predicate</code> to each argument in this <code>
   * Times(...)</code> expression and append the arguments which satisfy the predicate to the <code>
   * 1st element</code> of the resulting AST, or otherwise append it to the <code>2nd element</code>
   * of the resulting AST.
   *
   * <p>See: <a href= "https://people.eecs.berkeley.edu/~fateman/papers/partition.pdf">Fateman -
   * Partitioning of Algebraic Subexpressions in Computer Algebra Systems</a>
   *
   * @param predicate the predicate which filters each element in this AST
   * @param initTrue the result for the 1st result element, if the predicate doesn't give <code>true
   *     </code> for any of the arguments in this AST.
   * @param initFalse the result for the 2nd result element, if the predicate doesn't give <code>
   *     false</code> for any of the arguments in this AST.
   * @return <code>F.NIL</code> if partitioning wasn't possible
   */
  default IAST partitionTimes(
      Predicate<? super IExpr> predicate, IExpr initTrue, IExpr initFalse, ISymbol action) {
    return F.NIL;
  }

  /**
   * Returns an <code>IExpr</code> whose value is <code>(this + that)</code>. Calculates <code>
   * F.eval(F.Plus(this, that))</code> in the common case and uses a specialized implementation for
   * derived number classes.
   *
   * @param that
   * @return
   */
  default IExpr plus(final IExpr that) {
    if (that.isZero()) {
      return this;
    }
    EvalEngine engine = EvalEngine.get();
    if (engine.isTogetherMode() && (this.isPlusTimesPower() || that.isPlusTimesPower())) {
      return engine.evaluate(F.Together(F.Plus(this, that)));
    }
    return engine.evaluate(F.Plus(this, that));
  }

  /**
   * Returns an <code>IExpr</code> whose value is <code>(this ^ that)</code>. Calculates <code>
   * F.eval(F.Power(this, that))</code> in the common case and uses a specialized implementation for
   * derived number classes.
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
    } else if (that.isMinusOne()) {
      if (this.isPlus()
          && this.size() == 3
          && (this.first().isRational() || this.first().isFactorSqrtExpr())
          && this.second().isFactorSqrtExpr()) {
        // rat1 + rat2 * Sqrt( rat3 );
        // or: rat1 * Sqrt( rat2 ) + rat3 * Sqrt( rat4 );
        IExpr p1 = first();
        IExpr p2 = second();
        IRational denominator = (IRational) F.Subtract.of(F.Sqr(p1), F.Sqr(p2));
        denominator = denominator.inverse();
        p1 = denominator.multiply(p1);
        p2 = denominator.multiply(p2);
        return p1.subtract(p2);
      }
      if (this.isFactorSqrtExpr()) {
        if (isSqrt()) {
          return F.Times.of(first().inverse(), this);
        }
        if (isTimes()) {
          // rat1 * Sqrt( rat2 );
          IRational rat1 = (IRational) first();
          IRational rat2 = (IRational) second().first();
          return F.Times.of(rat1.inverse(), rat2.inverse(), second());
        }
      }
    }
    EvalEngine engine = EvalEngine.get();
    if (engine.isTogetherMode() && (this.isPlusTimesPower() || that.isPlusTimesPower())) {
      return S.Together.of(engine, F.Power(this, that));
    }
    return S.Power.of(engine, this, that);
  }

  /**
   * Returns an <code>IExpr</code> whose value is <code>(this ^ n)</code>. Calculates <code>
   * F.eval(F.Power(this, that))</code> in the common case and uses a specialized implementation for
   * derived number classes.
   *
   * @param n the exponent
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
        x = (INumber) x.times(x);
        if ((exp & 1) != 0) {
          r = (INumber) r.times(x);
        }
      }

      while (b2pow-- > 0) {
        r = (INumber) r.times(r);
      }
      if (n < 0) {
        return r.inverse();
      }
      return r;
    }
    return F.Power(this, F.ZZ(n));
  }

  /**
   * Return the real part of this expression if possible. Otherwise return <code>Re(this)</code>.
   *
   * @return real part
   */
  public default IExpr re() {
    return S.Re.of(this);
  }

  @Override
  public default IExpr reciprocal() throws MathRuntimeException {
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
   * Replace all (sub-) expressions with the given unary function, if the given predicate yields
   * <code>true</code>. If no substitution matches, the method returns <code>this</code>.
   *
   * @param predicate
   * @param function if the unary functions <code>apply()</code> method returns <code>F.NIL</code>
   *     the expression isn't substituted.
   * @return <code>this</code> if no substitution of a (sub-)expression was possible.
   */
  default IExpr replace(final Predicate<IExpr> predicate, final Function<IExpr, IExpr> function) {
    return accept(new VisitorReplaceAllLambda(predicate, function)).orElse(this);
  }

  /**
   * Replace all (sub-) expressions with the given unary function. If no substitution matches, the
   * method returns <code>F.NIL</code>.
   *
   * @param function if the unary functions <code>apply()</code> method returns <code>F.NIL</code>
   *     the expression isn't substituted.
   * @return <code>F.NIL</code> if no substitution of a (sub-)expression was possible.
   */
  default IExpr replaceAll(final Function<IExpr, IExpr> function) {
    return accept(new VisitorReplaceAll(function));
  }

  /**
   * Replace all (sub-) expressions with the given rule set. If no substitution matches, the method
   * returns <code>F.NIL</code>.
   *
   * @param listOfRules rules of the form <code>x-&gt;y</code> or <code>{a-&gt;b, c-&gt;d}</code>;
   *     the left-hand-side of the rule can contain pattern objects.
   * @return <code>F.NIL</code> if no substitution of a (sub-)expression was possible.
   */
  default IExpr replaceAll(final IAST listOfRules) {
    return accept(new VisitorReplaceAll(listOfRules));
  }

  /**
   * Replace all (sub-) expressions with the given <code>java.util.Map</code>n. If no substitution
   * matches, the method returns <code>F.NIL</code>.
   *
   * @param map if the maps <code>get()</code> method returns <code>F.NIL</code> the expression
   *     isn't substituted.
   * @return <code>F.NIL</code> if no substitution of a (sub-)expression was possible.
   */
  default IExpr replaceAll(final Map<? extends IExpr, ? extends IExpr> map) {
    return accept(new VisitorReplaceAll(map));
  }

  default IExpr replaceAll(VisitorReplaceAll visitor) {
    return accept(visitor);
  }

  /**
   * Replace all subexpressions with the given rule set. A rule must contain the position of the
   * subexpression which should be replaced on the left-hand-side. If no substitution matches, the
   * method returns <code>F.NIL</code>.
   *
   * @param astRules rules of the form <code>position-&gt;y</code> or <code>
   *     {position1-&gt;b, position2-&gt;d}</code>
   * @param heads if <code>TRUE</code> also replace the heads of expressions
   * @return <code>F.NIL</code> if no substitution of a subexpression was possible.
   */
  default IExpr replacePart(final IAST astRules, IExpr.COMPARE_TERNARY heads) {
    return this.accept(new VisitorReplacePart(astRules, heads));
  }

  /**
   * Repeatedly replace all (sub-) expressions with the given unary function. If no substitution
   * matches, the method returns <code>this</code>.
   *
   * @param function if the unary functions <code>apply()</code> method returns <code>null</code>
   *     the expression isn't substituted.
   * @return <code>this</code> if no substitution of a (sub-)expression was possible.
   */
  default IExpr replaceRepeated(final Function<IExpr, IExpr> function) {
    return replaceRepeated(new VisitorReplaceAll(function), -1);
  }

  /**
   * Repeatedly replace all (sub-) expressions with the given rule set. If no substitution matches,
   * the method returns <code>this</code>.
   *
   * @param astRules rules of the form <code>x-&gt;y</code> or <code>{a-&gt;b, c-&gt;d}</code>; the
   *     left-hand-side of the rule can contain pattern objects.
   * @return <code>this</code> if no substitution of a (sub-)expression was possible.
   */
  default IExpr replaceRepeated(final IAST astRules) {
    return replaceRepeated(new VisitorReplaceAll(astRules), -1);
  }

  /**
   * Repeatedly replace all (sub-) expressions with the given visitor. If no substitution matches,
   * the method returns <code>this</code>.
   *
   * @param visitor
   * @param maxIterations the maximum number of iterations
   * @return
   */
  default IExpr replaceRepeated(VisitorReplaceAll visitor, int maxIterations) {
    IExpr result = this;
    IExpr temp = accept(visitor);
    final EvalEngine engine = EvalEngine.get();
    int iterationLimit = engine.getIterationLimit();
    if (maxIterations > 0 && maxIterations < iterationLimit) {
      iterationLimit = maxIterations;
    }
    int iterationCounter = 0;
    while (temp.isPresent()) {
      result = engine.evaluate(temp);
      if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
        // Exiting after `1` scanned `2` times.
        IOFunctions.printMessage(
            S.ReplaceRepeated, "rrlim", F.List(this, F.ZZ(iterationLimit)), engine);
        return result;
      }

      temp = result.accept(visitor);
    }
    return result;
  }

  /**
   * Replace all occurrences of Slot[&lt;index&gt;] expressions with the expression at the
   * appropriate <code>index</code> in the given <code>slotsList</code>.
   *
   * <p><b>Note:</b> If a slot value is <code>null</code> the Slot will not be substituted.
   *
   * @param slotsList the values for the slots.
   * @return <code>null</code> if no substitution occurred.
   * @deprecated use org.matheclipse.core.eval.util.Lambda#replaceSlots() instead
   */
  @Deprecated
  default IExpr replaceSlots(final IAST slotsList) {
    return accept(new VisitorReplaceSlots(slotsList));
  }

  /**
   * Get the rest of the elements of this <code>AST</code> or <code>ASTAssociation</code> list.
   * Return <code>F.NIL</code> if this object isn't an <code>AST</code>.
   *
   * @return the rest arguments of the function represented by this <code>AST</code> with the first
   *     argument removed.
   * @see IExpr#head()
   */
  default IAST rest() {
    return F.NIL;
  }

  default IExpr rewrite(int functionID) {
    return F.NIL;
  }

  /**
   * Get the second element of this <code>AST</code> list (i.e. get(2)). Return <code>F.NIL</code>
   * if this object isn't an <code>AST</code>.
   *
   * @return the second argument of the function represented by this <code>AST</code> or <code>F.NIL
   *     </code> if this object isn't an AST.
   */
  default IExpr second() {
    return F.NIL;
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
    if (this instanceof INumber) {
      return ((INumber) this).complexSign();
    }
    return 1;
  }

  /**
   * Returns the <b>number of elements</b> in this {@code IAST}.The <b>number of elements</b> equals
   * <code>argSize() + 1</code> (i.e. the <b>number of arguments</b> plus 1). If this is an atom
   * return size <code>0</code>.
   *
   * @return the <b>number of elements</b> in this {@code IAST}.
   * @see #argSize()
   */
  default int size() {
    return 0;
  }

  /**
   * Generate <code>Sqrt(this)</code>.
   *
   * @return <code>Sqrt(this)</code>
   */
  default IExpr sqrt() {
    if (isPower()) {
      return F.Power(base(), F.Times(C1D2, exponent()));
    } else {
      //      if (isAST(S.Plus, 3)) {
      //        IAST plus = (IAST) this;
      //        final IExpr arg1 = plus.arg1();
      //        final IExpr arg2 = plus.arg2();
      //        if (arg1.isRational()) {
      //          IExpr temp = FunctionExpand.sqrtDenest((IRational) arg1, arg2);
      //          if (temp.isPresent()) {
      //            return temp;
      //          }
      //        }
      //      }
      if (isTimes()) {
        // see github issue #2: Get only real results
        IAST times = (IAST) this;
        int size = times.size();
        IASTAppendable timesSqrt = F.TimesAlloc(size);
        IASTAppendable timesRest = F.TimesAlloc(size);
        for (int i = 1; i < size; i++) {
          final IExpr arg = times.get(i);
          if (arg.isPower()) {
            timesRest.append( //
                F.Power(
                    arg.base(), //
                    F.Times(C1D2, arg.exponent())) //
                );
          } else {
            timesSqrt.append(arg);
          }
        }
        return F.Times(timesRest, Sqrt(timesSqrt));
      }
    }
    return F.Sqrt(this);
  }

  @Override
  default IExpr subtract(IExpr that) {
    if (that.isZero()) {
      return this;
    }
    return plus(that.negate());
    // EvalEngine engine = EvalEngine.get();
    // if (engine.isTogetherMode() && (this.isPlusTimesPower() || that.isPlusTimesPower())) {
    // return engine.evaluate(F.Together(F.Plus(this, F.Times(F.CN1, that))));
    // }
    // return engine.evaluate(F.Plus(this, F.Times(F.CN1, that)));
  }

  @Override
  default IExpr sum(final IExpr that) {
    return add(that);
  }

  /**
   * Returns an <code>IExpr</code> whose value is <code>(this * that)</code>. Calculates <code>
   * F.eval(F.Times(this, that))</code> in the common case and uses a specialized implementation for
   * derived number classes.
   *
   * @param that the multiplier expression
   * @return <code>(this * that)</code>
   */
  default IExpr times(final IExpr that) {
    if (that.isZero()) {
      return F.C0;
    }
    if (that.isOne()) {
      return this;
    }
    EvalEngine engine = EvalEngine.get();
    if (engine.isTogetherMode() && (this.isPlusTimesPower() || that.isPlusTimesPower())) {
      if (this.isNumber() && that.isPlus()) {
        return engine.evaluate(F.Expand(F.Times(this, that)));
      }
      if (that.isNumber() && this.isPlus()) {
        return engine.evaluate(F.Expand(F.Times(that, this)));
      }
      return engine.evaluate(F.Together(F.Times(this, that)));
    }
    return engine.evaluate(F.Times(this, that));
  }

  /**
   * Returns an <code>IExpr</code> whose value is <code>(this * that)</code>. Calculates <code>
   * F.eval(F.Times(this, that))</code> in the common case and uses a specialized implementation for
   * derived number classes.
   *
   * @param that the multiplier expression
   * @return <code>(this * that)</code>
   */
  public default IExpr timesDistributed(final IExpr that) {
    return times(that);
  }

  /**
   * Convert this object into a <code>Complex[]</code> vector.
   *
   * @return <code>null</code> if this object can not be converted into a <code>Complex[]</code>
   *     vector
   */
  default Complex[] toComplexVector() {
    return null;
  }

  /**
   * Convert this object into a <code>double[]</code> matrix.
   *
   * @return <code>null</code> if this object can not be converted into a <code>double[]</code>
   *     matrix
   */
  default double[][] toDoubleMatrix() {
    return null;
  }

  /**
   * Convert this object into a <code>double[]</code> matrix, if a row is not convertible to double
   * vector ignore the row.
   *
   * @return <code>null</code> if this object can not be converted into a <code>double[]</code>
   *     matrix
   */
  default double[][] toDoubleMatrixIgnore() {
    return toDoubleMatrix();
  }

  /**
   * Convert this object into a <code>double[]</code> vector.
   *
   * @return <code>null</code> if this object can not be converted into a <code>double[]</code>
   *     vector
   */
  default double[] toDoubleVector() {
    return null;
  }

  /**
   * Convert this object into a <code>double[]</code> vector, if an argument is not convertible to
   * double ignore the value.
   *
   * @return <code>null</code> if this object can not be converted into a <code>double[]</code>
   *     vector
   */
  default double[] toDoubleVectorIgnore() {
    return toDoubleVector();
  }

  default double toDoubleDefault() throws ArgumentTypeException {
    return EvalEngine.get().evalDouble(this, Double.MIN_VALUE);
  }

  default double toDoubleDefault(double defaultValue) {
    return EvalEngine.get().evalDouble(this, defaultValue);
  }

  /**
   * Converts this number to an <code>int</code> value; unlike {@link #intValue} this method returns
   * <code>Integer.MIN_VALUE</code> if the value of this integer isn't in the range <code>
   * Integer.MIN_VALUE</code> to <code>Integer.MAX_VALUE</code> or the expression is not convertible
   * to the <code>int</code> range.
   *
   * @return the numeric value represented by this expression after conversion to type <code>int
   *     </code> or <code>Integer.MIN_VALUE</code> if this expression cannot be converted.
   */
  default int toIntDefault() {
    return toIntDefault(Integer.MIN_VALUE);
  }

  /**
   * Converts this number to an <code>int</code> value; unlike {@link #intValue} this method returns
   * <code>defaultValue</code> if the value of this integer isn't in the range <code>
   * Integer.MIN_VALUE</code> to <code>Integer.MAX_VALUE</code> or the expression is not convertible
   * to the int range.
   *
   * @param defaultValue the default value, if this integer is not in the <code>int</code> range
   * @return the numeric value represented by this integer after conversion to type <code>int</code>
   */
  default int toIntDefault(int defaultValue) {
    return defaultValue;
  }

  /**
   * Converts this number to a <code>long</code> value; unlike {@link #longValue} this method
   * returns <code>Long.MIN_VALUE</code> if the value of this integer isn't in the range <code>
   * Long.MIN_VALUE</code> to <code>Long.MAX_VALUE</code> or the expression is not convertible to
   * the <code>long</code> range.
   *
   * @return the numeric value represented by this expression after conversion to type <code>long
   *     </code> or <code>Long.MIN_VALUE</code> if this expression cannot be converted.
   */
  default long toLongDefault() {
    return toLongDefault(Long.MIN_VALUE);
  }

  /**
   * Converts this number to a <code>long</code> value; unlike {@link #longValue} this method
   * returns <code>defaultValue</code> if the value of this integer isn't in the range <code>
   * Long.MIN_VALUE</code> to <code>Long.MAX_VALUE</code> or the expression is not convertible to
   * the <code>long</code> range.
   *
   * @param defaultValue the default value, if this integer is not in the <code>long</code> range
   * @return the numeric value represented by this integer after conversion to type <code>long
   *     </code>
   */
  default long toLongDefault(long defaultValue) {
    return defaultValue;
  }

  /**
   * Convert this object into a <code>int[]</code> vector.
   *
   * @return <code>null</code> if the conversion is not possible
   */
  default int[] toIntVector() {
    return null;
  }

  /**
   * The 'highest level' head of the expression, before Symbol, Integer, Real or String. for example
   * while the head of a[b][c] is a[b], the top head is a.
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
    final double[][] elements = toDoubleMatrix();
    if (elements != null && elements.length > 0 && elements[0].length > 0) {
      return new Array2DRowRealMatrix(elements, false);
    }
    return null;
  }

  /**
   * Convert this object into a RealMatrix.
   *
   * @return <code>null</code> if this object can not be converted into a RealMatrix
   */
  default RealMatrix toRealMatrixIgnore() {
    return null;
  }

  /**
   * Convert this object into a RealVector.
   *
   * @return <code>null</code> if this object can not be converted into a RealVector
   */
  default RealVector toRealVector() {
    final double[] elements = toDoubleVector();
    if (elements != null) {
      return new ArrayRealVector(elements, false);
    }
    return null;
  }

  /**
   * Return the <code>Mathematica()</code> form of this expression
   *
   * @return
   */
  default String toMMA() {
    return WolframFormFactory.get().toString(this);
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
   * <li>return S.True if the comparison is <code>true</code></li>
   * <li>return S.False if the comparison is <code>false</code></li>
   * <li>return F.NIL if the comparison is undetermined (i.e. could not be evaluated)</li>
   * </ul>
   *
   * @param that
   * @return <code>S.True, S.False or F.NIL</code
   */
  public default IExpr unequalTo(IExpr that) {
    COMPARE_TERNARY temp = this.equalTernary(that, EvalEngine.get());
    if (temp == COMPARE_TERNARY.TRUE) {
      return S.False;
    }
    if (temp == COMPARE_TERNARY.FALSE) {
      return S.True;
    }
    return F.NIL;
  }

  /**
   * Return <code>0</code> if this is less than <code>0</code>. Return <code>1</code> if this is
   * greater equal than <code>0</code>. Return <code>F.UnitStep(this)</code> for all other cases.
   *
   * @return
   */
  public default IExpr unitStep() {
    if (isNegativeResult()) {
      return F.C0;
    }
    if (isNonNegativeResult()) {
      return F.C1;
    }
    return F.UnitStep(this);
  }

  /**
   * If this is a <code>Interval({lower, upper})</code> expression return the <code>upper</code>
   * value. If this is a <code>ISignedNumber</code> expression return <code>this</code>.
   *
   * @return <code>F.NIL</code> if this expression is no interval and no signed number.
   */
  public default IExpr upper() {
    return F.NIL;
  }

  /**
   * Convert the variables (i.e. expressions of type <code>ISymbol</code> which aren't constants) in
   * this expression into Slot[] s.
   *
   * @param map for every given symbol argument return the associated unique slot from this map
   * @param variableCollector collects the variables which are used in the replacement process
   * @return <code>F.NIL</code> if no variable symbol was found.
   */
  default IExpr variables2Slots(
      final Map<IExpr, IExpr> map, final Collection<IExpr> variableCollector) {
    return this;
  }

  @Override
  default IExpr sign() {
    return S.Sign.of(this);
  }

  @Override
  default IExpr acos() {
    return S.ArcCos.of(this);
  }

  @Override
  default IExpr acosh() {
    return S.ArcCosh.of(this);
  }

  @Override
  default IExpr add(double that) {
    return plus(F.num(that));
  }

  @Override
  default IExpr asin() {
    return S.ArcSin.of(this);
  }

  @Override
  default IExpr asinh() {
    return S.ArcSinh.of(this);
  }

  @Override
  default IExpr atan() {
    return S.ArcTan.of(this);
  }

  @Override
  default IExpr atan2(IExpr that) throws MathIllegalArgumentException {
    // Beware of the order or arguments! As this is based on a two-arguments functions, in order to
    // be consistent with arguments order, the instance is the first argument and the single
    // provided argument is the second argument. In order to be consistent with programming
    // languages Math.atan2, this method computes Math.atan2(y, x).

    // The arguments of the Symja ArcTan() function is defined the other way round
    return S.ArcTan.of(that, this);
  }

  @Override
  default IExpr atanh() {
    return S.ArcTanh.of(this);
  }

  @Override
  default IExpr cbrt() {
    return S.Power.of(this, F.C1D3);
  }

  @Override
  default IExpr ceil() {
    return S.Ceiling.of(this);
  }

  @Override
  default IExpr copySign(double that) {
    return copySign(F.num(that));
  }

  @Override
  default IExpr copySign(IExpr that) {
    return abs().times(that.sign());
  }

  @Override
  default IExpr cos() {
    return S.Cos.of(this);
  }

  @Override
  default IExpr cosh() {
    return S.Cosh.of(this);
  }

  @Override
  default IExpr divide(double arg0) {
    return divide(F.num(arg0));
  }

  /**
   * If this expr is an {@link IAST}, check all elements by applying the <code>predicate</code> to
   * each argument in this {@link IAST} and return <code>true</code> if <b>one</b> of the arguments
   * starting from index <code>1</code> satisfy the predicate.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @return the <code>true</code> if the predicate is true the first time or <code>false</code>
   *     otherwise
   */
  default boolean exists(Predicate<? super IExpr> predicate) {
    return false;
  }

  @Override
  default IExpr exp() {
    return S.Exp.of(this);
  }

  @Override
  default IExpr expm1() {
    return S.Exp.of(this).subtract(F.C1);
  }

  @Override
  default IExpr floor() {
    return S.Floor.of(this);
  }

  /**
   * If this expr is an {@link IAST}, check all elements by applying the <code>predicate</code> to
   * each argument in this {@link IAST} and return <code>true</code> if <b>all</b> of the arguments
   * starting from index <code>1</code> satisfy the predicate.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @return <code>true</code> if the predicate is true for <b>all</b elements or <code>false</code>
   *     otherwise
   */
  default boolean forAll(Predicate<? super IExpr> predicate) {
    return false;
  }

  @Override
  default IExpr hypot(IExpr y) throws MathIllegalArgumentException {
    return S.Sqrt.of(F.Plus(F.Sqr(this), F.Sqr(y)));
  }

  @Override
  default IExpr linearCombination(
      double a1, IExpr b1, double a2, IExpr b2, double a3, IExpr b3, double a4, IExpr b4) {
    return linearCombination(new double[] {a1, a2, a3, a4}, new IExpr[] {b1, b2, b3, b4});
  }

  @Override
  default IExpr linearCombination(double a1, IExpr b1, double a2, IExpr b2, double a3, IExpr b3) {
    return linearCombination(new double[] {a1, a2, a3}, new IExpr[] {b1, b2, b3});
  }

  @Override
  default IExpr linearCombination(double a1, IExpr b1, double a2, IExpr b2) {
    return linearCombination(new double[] {a1, a2}, new IExpr[] {b1, b2});
  }

  @Override
  default IExpr linearCombination(double[] a, IExpr[] b) throws MathIllegalArgumentException {
    IASTAppendable result = F.PlusAlloc(a.length);
    for (int i = 0; i < a.length; i++) {
      result.append(F.Times(F.num(a[i]), b[i]));
    }
    return result;
  }

  @Override
  default IExpr linearCombination(
      IExpr a1, IExpr b1, IExpr a2, IExpr b2, IExpr a3, IExpr b3, IExpr a4, IExpr b4) {
    return linearCombination(new IExpr[] {a1, a2, a3, a4}, new IExpr[] {b1, b2, b3, b4});
  }

  @Override
  default IExpr linearCombination(IExpr a1, IExpr b1, IExpr a2, IExpr b2, IExpr a3, IExpr b3) {
    return linearCombination(new IExpr[] {a1, a2, a3}, new IExpr[] {b1, b2, b3});
  }

  @Override
  default IExpr linearCombination(IExpr a1, IExpr b1, IExpr a2, IExpr b2) {
    return linearCombination(new IExpr[] {a1, a2}, new IExpr[] {b1, b2});
  }

  @Override
  default IExpr linearCombination(IExpr[] a, IExpr[] b) throws MathIllegalArgumentException {
    IASTAppendable result = F.PlusAlloc(a.length);
    for (int i = 0; i < a.length; i++) {
      result.append(F.Times(a[i], b[i]));
    }
    return result;
  }

  @Override
  default IExpr log() {
    return S.Log.of(this);
  }

  @Override
  default IExpr log10() {
    return S.Log.of(F.C10, this);
  }

  @Override
  default IExpr log1p() {
    return S.Log.of(this.inc());
  }

  @Override
  default IExpr multiply(double that) {
    return times(F.num(that));
  }

  @Override
  default IExpr newInstance(double arg) {
    return F.num(arg);
  }

  @Override
  default IExpr pow(double n) {
    return S.Power.of(this, F.num(n));
  }

  @Override
  default IExpr pow(IExpr n) throws MathIllegalArgumentException {
    return S.Power.of(this, n);
  }

  @Override
  default IExpr pow(int n) {
    return S.Power.of(this, F.ZZ(n));
  }

  @Override
  default IExpr remainder(double arg0) {
    return S.Mod.of(this);
  }

  @Override
  default IExpr rint() {
    return S.IntegerPart.of(this);
  }

  @Override
  default IExpr rootN(int n) {
    return S.Power.of(this, F.QQ(1, n));
  }

  @Override
  default IExpr scalb(int n) {
    return times(F.C2.pow(n));
  }

  @Override
  default IExpr sin() {
    return S.Sin.of(this);
  }

  @Override
  default FieldSinCos<IExpr> sinCos() {
    return new FieldSinCos<IExpr>(sin(), cos());
  }

  @Override
  default IExpr sinh() {
    return S.Sinh.of(this);
  }

  @Override
  default FieldSinhCosh<IExpr> sinhCosh() {
    return new FieldSinhCosh<IExpr>(sinh(), cosh());
  }

  @Override
  default IExpr subtract(double arg0) {
    return subtract(F.num(arg0));
  }

  @Override
  default IExpr tan() {
    return S.Tan.of(this);
  }

  @Override
  default IExpr tanh() {
    return S.Tanh.of(this);
  }

  @Override
  default IExpr ulp() {
    return F.C0;
  }

  default IExpr getPi() {
    return S.Pi;
  }

  @Override
  default IExpr toDegrees() {
    // radians * (180 / Pi)
    return F.Times(this, F.ZZ(180), F.Inverse(S.Pi));
  }

  @Override
  default IExpr toRadians() {
    // degrees * (Pi / 180)
    return F.Times(F.QQ(1L, 180L), this, S.Pi);
  }

  default String toWolframString() {
    return toString();
  }
}
