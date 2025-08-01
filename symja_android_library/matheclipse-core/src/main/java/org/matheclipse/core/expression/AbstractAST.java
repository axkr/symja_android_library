package org.matheclipse.core.expression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apfloat.Apcomplex;
import org.hipparchus.complex.Complex;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.RealMatrix;
import org.jgrapht.GraphType;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.DefaultGraphType.Builder;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.builtin.JavaFunctions;
import org.matheclipse.core.builtin.PredicateQ;
import org.matheclipse.core.builtin.StructureFunctions;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IRewrite;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.generic.ObjIntFunction;
import org.matheclipse.core.generic.ObjIntPredicate;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.generic.UnaryVariable2Slot;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IContinuousDistribution;
import org.matheclipse.core.interfaces.IDiscreteDistribution;
import org.matheclipse.core.interfaces.IDistribution;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IReal;
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
import org.matheclipse.core.visit.VisitorReplaceAll;
import org.matheclipse.parser.client.ParserConfig;
import com.google.common.base.Suppliers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.unimi.dsi.fastutil.ints.IntList;

public abstract class AbstractAST implements IASTMutable, Cloneable {

  protected static final class ASTIterator implements ListIterator<IExpr> {

    private int _currentIndex;

    private int _end; // Exclusive.

    private int _nextIndex;

    private int _start; // Inclusive.

    private IASTMutable _table;

    @Override
    public void add(IExpr o) {
      throw new UnsupportedOperationException();
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
      if (_nextIndex == _end) {
        throw new NoSuchElementException();
      }
      _currentIndex = _nextIndex++;
      return _table.get(_currentIndex);
    }

    @Override
    public int nextIndex() {
      return _nextIndex;
    }

    @Override
    public IExpr previous() {
      if (_nextIndex == _start) {
        throw new NoSuchElementException();
      }
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
   * The class <code>NILPointer</code> implements the constant object <code>F.NIL</code> (not in
   * list), which indicates in the evaluation process that no evaluation was possible (i.e. no
   * further definition was found to create a new expression from the existing one).
   *
   * <p>
   * Almost every modifying method in this class throws an <tt>ArgumentTypeException</tt>, almost
   * every predicate returns <code>false</code>. The main method to check if the object is valid is
   * the <code>isPresent()</code> method. The method is designed similar to <code>
   * java.util.Optional#isPresent()</code>.
   *
   * @see org.matheclipse.core.expression.F#NIL
   * @see java.util.Optional#isPresent
   */
  public static final class NILPointer extends AbstractAST implements IAssociation, IPair {

    private static final long serialVersionUID = -3552302876858011292L;

    /**
     * The class <code>NILPointer</code> implements the constant object <code>F#NIL</code> (not in
     * list), which indicates in the evaluation process that no evaluation was possible (i.e. no
     * further definition was found to create a new expression from the existing one).
     *
     * @see F#NIL
     */
    protected NILPointer() {}

    /** {@inheritDoc} */
    @Override
    public IExpr accept(IVisitor visitor) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public boolean append(IExpr object) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public void append(int location, IExpr object) {
      ArgumentTypeException.throwNIL();
    }

    @Override
    public <T extends IExpr> boolean append(IAST list, Function<T, IExpr> function) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public boolean append(IAST list, ObjIntFunction<IExpr, IExpr> function) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public boolean append(Map<? extends IExpr, ? extends IExpr> map,
        BiFunction<IExpr, IExpr, IExpr> biFunction) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public boolean appendAll(Collection<? extends IExpr> collection) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public boolean appendAll(Map<? extends IExpr, ? extends IExpr> map) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public boolean appendAll(IAST ast, int startPosition, int endPosition) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public boolean appendAll(IExpr[] args, int startPosition, int endPosition) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public boolean appendAll(int location, Collection<? extends IExpr> collection) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public boolean appendAll(List<? extends IExpr> list, int startPosition, int endPosition) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public boolean appendArgs(IAST ast) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public final boolean appendArgs(IAST ast, int untilPosition) {
      ArgumentTypeException.throwNIL();
      return false;
    }

    @Override
    public IASTAppendable appendArgs(int start, int end, IntFunction<IExpr> function) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IASTAppendable appendArgs(int end, IntFunction<IExpr> function) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IAST appendOneIdentity(IAST subAST) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IExpr arg1() {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IExpr arg2() {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IExpr arg3() {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IExpr arg4() {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IExpr arg5() {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    /** {@inheritDoc} */
    @Override
    public int argSize() {
      return -1;
    }

    @Override
    public SortedSet<IExpr> asSortedSet(Comparator<? super IExpr> comparator) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public void clear() {
      ArgumentTypeException.throwNIL();
    }

    @Override
    public boolean contains(Object object) {
      return false;
    }

    @Override
    public IAssociation copy() {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IAssociation copyAppendable() {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IASTAppendable copyAppendable(int additionalCapacity) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IASTAppendable copyAST() {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public boolean equals(final Object obj) {
      return this == obj;
    }

    @Override
    public final IExpr evaluate(EvalEngine engine) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public final IExpr evaluateOrElse(EvalEngine engine, final IExpr other) {
      return other;
    }

    @Override
    public IExpr eval(EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr evalEvaluate(EvalEngine engine) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
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
    protected String fullFormString(IExpr head) {
      return "NIL";
    }

    @Override
    public IExpr get(int location) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
      // throw new UnsupportedOperationException();
    }

    @Override
    public IAST getItems(int[] items, int length, int offset) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
      // throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
      return -1;
    }

    @Override
    public final IExpr head() {
      if (Config.FUZZ_TESTING) {
        throw new NullPointerException();
      }
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    /**
     * Returns the ISymbol of the IAST. If the head itself is a IAST it will recursively call
     * head().
     */
    @Override
    public ISymbol topHead() {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public final int headID() {
      return ID.UNKNOWN;
    }

    @Override
    public void ifAppendable(Consumer<? super IASTAppendable> consumer) {}

    @Override
    public IExpr ifPresent(Function<? super IExpr, IExpr> function) {
      return F.NIL;
    }

    /** {@inheritDoc} */
    @Override
    public final CharSequence internalJavaString(SourceCodeProperties properties, int depth,
        Function<ISymbol, ? extends CharSequence> variables) {
      switch (properties.prefix) {
        case FULLY_QUALIFIED_CLASS_NAME:
          return "org.matheclipse.core.expression.F.NIL";
        case CLASS_NAME:
          return "F.NIL";
        case NONE:
        default:
          return "NIL";
      }
    }

    @Override
    public boolean isAbs() {
      return false;
    }

    @Override
    public boolean isAllExpanded() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isAST() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isAST(final IBuiltInSymbol header) {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isAST(final IExpr header) {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isAST(final IBuiltInSymbol header, final int length) {
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
    public boolean isBuiltInFunction() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isComparatorFunction() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCondition() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isConditionalExpression() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExcept() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExpanded() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isIntegerResult() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isInterval() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isIntervalData() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmptyIntervalData() {
      return false;
    }

    @Override
    public final boolean isInterval1() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmptyList() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNIL() {
      return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInvalid() {
      return this == INVALID;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNonEmptyList() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isList() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isListOfPoints(int dimension) {
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
    public final boolean isListOfMatrices() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final GraphType isListOfEdges() {
      return null;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isListOfRules(boolean ignoreEmptySublists) {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isListOfRulesOrAssociation(boolean ignoreEmptySublists) {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public int[] isMatrix(boolean setMatrixFormat) {
      return null;
    }

    /** {@inheritDoc} */
    @Override
    public int[] isMatrixIgnore() {
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
    public final boolean isNumericFunction(boolean allowList) {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isNumericFunction(VariablesSet varSet) {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isNumericFunction(IExpr expr) {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNumericFunctionAST() {
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
    public final boolean isOverflow() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public int[] isPiecewise() {
      return null;
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
    public boolean isPossibleZero(boolean fastTest, double tolerance) {
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

    @Override
    public boolean isRealResult() {
      return false;
    }

    @Override
    public boolean isRe() {
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
    public boolean isRGBColor() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isRule() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isRuleAST() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isRuleDelayed() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isSame(IExpr expression) {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isSame(IExpr expression, double epsilon) {
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

    @Override
    public final boolean isSequence() {
      return false;
    }

    @Override
    public boolean isSpecialsFree() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isTimes() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isUnderflow() {
      return false;
    }

    @Override
    public final int isVector() {
      return -1;
    }


    @Override
    public boolean isIm() {
      return false;
    }

    @Override
    public final int isInexactVector() {
      return -1;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isZERO() {
      return false;
    }

    @Override
    public final IASTMutable mapThread(final IAST replacement, int position) {
      return this;
    }

    @Override
    public IExpr mapExpr(Function<? super IExpr, ? extends IExpr> mapper) {
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
    public final <X extends Throwable> IExpr orElseThrow(Supplier<? extends X> exceptionSupplier)
        throws X {
      throw exceptionSupplier.get();
    }

    @Override
    public boolean parallelAllMatch(IAST ast, int startPosition, int endPosition,
        BiPredicate<? super IExpr, ? super IExpr> predicate) {
      return false;
    }

    @Override
    public boolean parallelAnyMatch(IAST ast, int startPosition, int endPosition,
        BiPredicate<? super IExpr, ? super IExpr> predicate) {
      return false;
    }

    private Object readResolve() {
      return F.NIL;
    }

    @Override
    public IExpr remove(int location) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
      // throw new UnsupportedOperationException();
    }

    /**
     * Removes the objects in the specified range from the start to the end, but not including the
     * end index.
     *
     * @param start the index at which to start removing.
     * @param end the index one after the end of the range to remove.
     * @throws IndexOutOfBoundsException when {@code start < 0, start > end} or {@code end > size()}
     */
    @Override
    public void removeRange(int start, int end) {
      ArgumentTypeException.throwNIL();
      // throw new UnsupportedOperationException();
    }

    public IExpr replace(final Predicate<IExpr> predicate, final Function<IExpr, IExpr> function) {
      return F.NIL;
    }

    @Override
    public IExpr replaceAll(final Function<IExpr, IExpr> function) {
      return F.NIL;
    }

    @Override
    public IExpr replaceAll(final IAST listOfRules) {
      return F.NIL;
    }

    @Override
    public IExpr replaceAll(final Map<? extends IExpr, ? extends IExpr> map) {
      return F.NIL;
    }

    public IExpr replaceAll(VisitorReplaceAll visitor) {
      return F.NIL;
    }

    @Override
    public IExpr replacePart(final IAST astRules, IExpr.COMPARE_TERNARY heads) {
      return F.NIL;
    }

    /**
     * Repeatedly replace all (sub-) expressions with the given unary function. If no substitution
     * matches, the method returns <code>this</code>.
     *
     * @param function if the unary functions <code>apply()</code> method returns <code>null</code>
     *        the expression isn't substituted.
     * @return <code>this</code> if no substitution of a (sub-)expression was possible.
     */
    @Override
    public IExpr replaceRepeated(final Function<IExpr, IExpr> function) {
      return F.NIL;
    }

    @Override
    public IExpr replaceRepeated(final IAST astRules) {
      return F.NIL;
    }

    @Override
    public IExpr replaceRepeated(VisitorReplaceAll visitor, int maxIterations) {
      return F.NIL;
    }

    @Override
    public IExpr set(int location, IExpr object) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IASTMutable setArgs(int start, int end, IntFunction<IExpr> function) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IASTMutable setArgs(int end, IntFunction<IExpr> function) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IExpr setValue(final int location, final IExpr value) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IExpr setPart(IExpr value, final int... positions) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IASTAppendable setAtClone(int i, IExpr expr) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    @Override
    public IASTMutable setAtCopy(int i, IExpr expr) {
      ArgumentTypeException.throwNIL();
      return F.NIL;
    }

    /** {@inheritDoc} */
    @Override
    public IASTMutable setIf(IAST ast, int position, IExpr value) {
      return ast.setAtCopy(position, value);
    }

    /** {@inheritDoc} */
    @Override
    public IASTMutable setIfPresent(IAST ast, int position, IExpr value) {
      if (value.isPresent()) {
        return ast.setAtCopy(position, value);
      }
      return F.NIL;
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public byte[][] toByteMatrix() {
      return null;
    }

    /** {@inheritDoc} */
    @Override
    public double[][] toDoubleMatrix(boolean setMatrixFormat) {
      return null;
    }

    /** {@inheritDoc} */
    @Override
    public double[][] toDoubleMatrixIgnore() {
      return null;
    }

    /** {@inheritDoc} */
    @Override
    public double[] toDoubleVector() {
      return null;
    }

    /** {@inheritDoc} */
    @Override
    public double[] toDoubleVectorIgnore() {
      return null;
    }

    @Override
    public int[][] toIntMatrix() {
      return null;
    }

    /**
     * Returns a new array containing all elements contained in this {@code ArrayList}.
     *
     * @return an array of the elements from this {@code ArrayList}
     */
    @Override
    public IExpr[] toArray() {
      ArgumentTypeException.throwNIL();
      return null;
      // throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "NIL";
    }

    @Override
    public void appendRules(IAST listOfRules) {
      ArgumentTypeException.throwNIL();
    }

    @Override
    public void appendRules(IAST listOfRules, int startPosition, int endPosition) {
      ArgumentTypeException.throwNIL();
    }

    @Override
    public IAssociation copyHead(int intialCapacity) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IExpr getKey(int position) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IAST getRule(int position) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IExpr getValue(int position) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IExpr getValue(IExpr key) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IExpr getValue(IExpr key, Supplier<IExpr> defaultValue) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public boolean isKey(IExpr expr) {
      return false;
    }

    @Override
    public IASTMutable keys() {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public ArrayList<String> keyNames() {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IAssociation keySort() {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IAssociation keySort(Comparator<IExpr> comparator) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    /**
     * @return <code>Long.MAX_VALUE</code>
     */
    @Override
    public long leafCountSimplify() {
      return Long.MAX_VALUE;
    }

    @Override
    public void mergeRule(IAST rule, IExpr head, EvalEngine engine) {
      ArgumentTypeException.throwNIL();
    }

    @Override
    public IASTMutable normal(boolean nilIfUnevaluated) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IAST matrixOrList() {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IAssociation sort() {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IAssociation sort(Comparator<IExpr> comparator) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IASTMutable values() {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IASTAppendable removePositionsAtCopy(int[] removedPositions, int untilIndex) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IAST removePositionsAtCopy(Predicate<IExpr> predicate) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IASTAppendable replaceSubset(int[] replacePosition, IExpr[] newEntries,
        int[] removePositions) {
      ArgumentTypeException.throwNIL();
      return null;
    }


    @Override
    public IAssociation reverse(IAssociation newAssoc) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public IAST getRule(String key) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public int getRulePosition(IExpr key) {
      ArgumentTypeException.throwNIL();
      return -1;
    }

    @Override
    public IAST getRule(IExpr key) {
      ArgumentTypeException.throwNIL();
      return null;
    }

    @Override
    public void prependRules(IAST listOfRules) {
      ArgumentTypeException.throwNIL();
    }

    @Override
    public void prependRules(IAST listOfRules, int startPosition, int endPosition) {
      ArgumentTypeException.throwNIL();
    }

    @Override
    public IExpr removeRule(IExpr key) {
      ArgumentTypeException.throwNIL();
      return null;
    }
  }

  /** The enumeration map which possibly maps the properties (keys) to a user defined object. */
  // private static Cache<IAST, EnumMap<PROPERTY, Object>> IAST_CACHE = null;
  private static Supplier<Cache<IAST, EnumMap<PROPERTY, Object>>> IAST_CACHE =
      Suppliers.memoize(AbstractAST::initCache);

  private static Cache<IAST, EnumMap<PROPERTY, Object>> initCache() {
    return CacheBuilder.newBuilder().maximumSize(500).build();
  }

  private static Cache<IAST, EnumMap<PROPERTY, Object>> propertyCache() {
    return IAST_CACHE.get();
  }

  /** package private */
  static final NILPointer NIL = new NILPointer();

  static final NILPointer INVALID = new NILPointer();

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

  private static int compareToASTDecreasingArg1(final IAST lhsAST, final IExpr arg1,
      IInteger value) {
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

    final int lhsSize = lhsAST.size();
    final int rhsSize = rhsAST.size();
    if (lhsSize == rhsSize) {
      for (int i = 1; i < lhsSize; i++) {
        cp = lhsAST.get(i).compareTo(rhsAST.get(i));
        if (cp != 0) {
          return cp;
        }
      }
      return 0;
    }

    return lhsSize > rhsSize ? 1 : -1;
  }

  private static int compareToASTIncreasingArg1(final IAST lhsAST, final IExpr arg1,
      IInteger value) {
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

  private static void internalFormOrderless(final IAST ast, StringBuilder text, final String sep,
      SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    final IExpr head = ast.head();
    for (int i = 1; i < ast.size(); i++) {
      if ((ast.get(i) instanceof IAST) && Objects.equals(head, ast.get(i).head())) {
        internalFormOrderless((IAST) ast.get(i), text, sep, properties, depth, variables);
      } else {
        text.append(ast.get(i).internalJavaString(properties, depth + 1, variables));
      }
      if (i < ast.argSize()) {
        text.append(sep);
      }
    }
  }

  /**
   * Replace all elements determined by the unary <code>from</code> predicate, with the element
   * generated by the unary <code>to</code> function. If the unary function returns null replaceAll
   * returns null.
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

  /** Flags for controlling evaluation and left-hand-side pattern-matching expressions */
  protected int fEvalFlags = 0;

  protected transient int hashValue;

  public AbstractAST() {
    super();
    hashValue = 0;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
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
    IASTAppendable ast = copyAppendable(1);
    ast.append(position, expr);
    return ast;
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable appendClone(IExpr expr) {
    IASTAppendable ast = copyAppendable(1);
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
    final IASTAppendable ast = F.ast(head, end - start);
    ast.appendArgs(start, end, (IntFunction<IExpr>) this::get);
    return ast;
  }

  @Override
  public IExpr getUnevaluated(int position) {
    IExpr arg = get(position);
    return arg.isUnevaluated() ? arg.first() : arg;
  }

  @Override
  public Pair asNumerDenom() {
    if (this.isPlusTimesPower()) {
      Optional<IExpr[]> parts = AlgebraUtil.fractionalPartsRational(this, true, true);
      if (parts.isPresent()) {
        final IExpr numerator = parts.get()[0];
        final IExpr denominator = parts.get()[1];
        return F.pair(numerator, denominator);
      }
    }
    return IASTMutable.super.asNumerDenom();
  }

  @Override
  public DefaultDict<IExpr> asPowersDict() {
    if (isPower()) {
      DefaultDict<IExpr> dict = new DefaultDict<>(() -> F.C0);
      dict.put(base(), exponent());
      return dict;
    } else if (isASTSizeGE(S.Times, 1)) {
      DefaultDict<IExpr> dict = new DefaultDict<>(() -> F.C0);
      for (int i = 1; i < size(); i++) {
        IExpr a = get(i);
        if (a.isPower()) {
          IExpr base = a.base();
          IExpr exponents = dict.getValue(base);
          exponents = exponents.plus(a.exponent());
          dict.put(base, exponents);
        } else {
          IExpr exponents = dict.getValue(a);
          exponents = exponents.plus(F.C1);
          dict.put(a, exponents);
        }
      }
      return dict;
    }
    return IASTMutable.super.asPowersDict();
  }

  @Override
  public SortedSet<IExpr> asSortedSet(Comparator<? super IExpr> comparator) {
    return null;
  }

  @Override
  public Object asType(Class<?> clazz) {
    if (clazz.equals(Boolean.class)) {
      IExpr temp = F.eval(this);
      if (temp.equals(S.True)) {
        return Boolean.TRUE;
      }
      if (temp.equals(S.False)) {
        return Boolean.FALSE;
      }
    } else if (clazz.equals(Integer.class)) {
      IExpr temp = F.eval(this);
      if (temp.isReal()) {
        try {
          return Integer.valueOf(((IReal) this).toInt());
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

  /**
   * Compare all adjacent elements from lowest to highest index and return true, if the binary
   * predicate gives true in each step. If the size is &lt; 2 the method returns false;
   *
   * @param predicate the binary predicate
   * @return <code>false</code> if the first comparison fails
   */
  @Override
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
   * Compares this "left-hand-side" expression with the specified "right-hand-side" expression for
   * canonical order. Returns a negative integer, zero, or a positive integer as this expression is
   * canonical less than, equal to, or greater than the specified expression.
   * <p>
   * See: "Computer Algebra and Symbolic Computation: Mathematical Methods" by Joel S. Cohen page 84
   * "The &#9665; order relation"
   * 
   * @param rhsExpr the right-hand-side expression to be compared
   */
  @Override
  public int compareTo(final IExpr rhsExpr) {
    if (rhsExpr.isNumber()) {
      // O-7
      return 1;
    }
    final int lhsOrdinal = headID();
    int rhsOrdinal = -1;
    if (lhsOrdinal < 0) {
      rhsOrdinal = rhsExpr.headID();
      if (rhsOrdinal < 0) {
        if (rhsExpr.isAST()) {
          return compareToASTIncreasing(this, (IAST) rhsExpr);
        }
        return IExpr.compareHierarchy(this, rhsExpr);
      }
    } else {
      if (lhsOrdinal == ID.DirectedInfinity && isDirectedInfinity()) {
        if (!rhsExpr.isDirectedInfinity()) {
          return -1;
        }
        return compareToASTIncreasing(this, (IAST) rhsExpr);
      }
      rhsOrdinal = rhsExpr.headID();
    }
    if (rhsExpr.isAST()) {
      if (rhsOrdinal == ID.DirectedInfinity && rhsExpr.isDirectedInfinity()) {
        if (isNumber()) {
          return -1;
        }
        if (!isDirectedInfinity()) {
          return 1;
        }
        return compareToASTIncreasing(this, (IAST) rhsExpr);
      }
      if (lhsOrdinal >= ID.Not && lhsOrdinal <= ID.Times && size() > 1) {
        IAST rhs = (IAST) rhsExpr;

        switch (lhsOrdinal) {
          case ID.Not:
            if (size() == 2) {
              IExpr arg1 = arg1();
              if (rhsExpr.isSlot() && arg1.isSlot()) {
                int ct = arg1.compareTo(rhsExpr);
                if (ct != 0) {
                  return ct;
                }
                return 1;
              }
            }
            break;
          case ID.Plus:
            if (rhsOrdinal == ID.Plus) {
              if (rhs.size() >= 1) {
                // O-3
                return compareToASTDecreasing(this, rhs);
              }
            } else if (rhsOrdinal != ID.Plus && rhsOrdinal != ID.Times) {
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
                  if (size() < 3) {
                    return -1; // lhs has no exponent, rhs has exponent
                  }
                  return exponent().compareTo(rhs.exponent());
                }
                return baseCompare;
              }
              // O-9
              return compareToASTIncreasingArg1(this, rhsExpr, F.C1);
            } else if (rhsOrdinal != ID.Plus && rhsOrdinal != ID.Times) {
              // O-9
              return compareToASTIncreasingArg1(this, rhsExpr, F.C1);
            }
            break;
          case ID.Slot:
            if (size() == 2 && rhsExpr.isNot() && rhsExpr.first().isSlot()) {
              int ct = this.compareTo(rhsExpr.first());
              if (ct != 0) {
                return ct;
              }
              return -1;
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
          if (size() == 2) {
            IExpr arg1 = arg1();
            if ((rhsExpr.isSymbol() && arg1.isSymbol())) {
              int ct = arg1.compareTo(rhsExpr);
              if (ct != 0) {
                return ct;
              }
              return 1;
            }
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
    return IExpr.compareHierarchy(this, rhsExpr);
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
  public IASTAppendable subList(int startPosition, int endPosition) {
    AST result = new AST(endPosition - startPosition + 1, false);
    result.append(head());
    result.appendAll(this, startPosition, endPosition);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable subList(int startPosition, int endPosition, int step) {
    AST result;
    if (step < 0) {
      // start position should be greater than end position in this case
      result = new AST(startPosition - endPosition + 1, false);
      result.append(head());
      for (int i = startPosition; i > endPosition; i += step) {
        result.append(get(i));
      }
    } else {
      result = new AST(endPosition - startPosition + 1, false);
      result.append(head());
      for (int i = startPosition; i < endPosition; i += step) {
        result.append(get(i));
      }
    }

    return result;
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable copyHead() {
    return AST.newInstance(head());
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable copyHead(final int intialCapacity) {
    return AST.newInstance(intialCapacity, head(), false);
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable copyUntil(int index) {
    return AST.newInstance(index, this, index);
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable copyUntil(final int intialCapacity, int index) {
    return AST.newInstance(intialCapacity, this, index);
  }

  @Override
  public int count(Predicate<? super IExpr> predicate, int fromIndex) {
    int counter = 0;
    for (int i = fromIndex; i < size(); i++) {
      if (predicate.test(getRule(i))) {
        counter++;
      }
    }
    return counter;
  }

  /** {@inheritDoc} */
  @Override
  public int depth(boolean includeHeads) {
    int maxDepth = 1;
    int d;
    final int start = includeHeads ? 0 : 1;
    for (int i = start; i < size(); i++) {
      if (get(i).isAST()) {
        d = get(i).depth(includeHeads);
        if (d > maxDepth) {
          maxDepth = d;
        }
      }
    }
    return ++maxDepth;
  }

  /** {@inheritDoc} */
  @Override
  public long determinePrecision(boolean postParserProcessing) {
    long precision = -1;
    if (isAST(S.N, 3)) {
      long determinedPrecision = arg1().determinePrecision(postParserProcessing);
      if (determinedPrecision > 0) {
        return determinedPrecision;
      }
      int p = arg2().toIntDefault();
      if (p >= ParserConfig.MACHINE_PRECISION) {
        precision = p;
      }
      return precision;
    }
    for (int i = 1; i < size(); i++) {
      long p = get(i).determinePrecision(postParserProcessing);
      if (p > precision && p != Apcomplex.INFINITE) {
        precision = p;
      }
    }
    return precision;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj instanceof AbstractAST) {
      final AbstractAST ast = (AbstractAST) obj;
      if (size() != ast.size()) {
        return false;
      }
      final IExpr head = head();
      if (head instanceof ISymbol) {
        if (head != ast.head()) {
          // compared with ISymbol object identity
          return false;
        }
      } else if (!head.equals(ast.head())) {
        return false;
      }
      if (hashCode() != obj.hashCode()) {
        return false;
      }
      return parallelAllMatch(ast, 1, size(), IExpr::equals);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equalsArgs(final IExpr that, int startPosition, int endPosition) {
    if (this == that) {
      return true;
    }
    if (!(that instanceof IAST)) {
      return false;
    }
    IAST other = (IAST) that;
    if (size() != other.size()) {
      return false;
    }
    return parallelAllMatch(other, startPosition, endPosition, IExpr::equals);
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

  @Override
  public IExpr.COMPARE_TERNARY equalTernary(IExpr that, EvalEngine engine) {
    if (that.isIndeterminate()) {
      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }
    if (this == that) {
      return IExpr.COMPARE_TERNARY.TRUE;
    }

    if (that.isAST()) {
      IAST list2 = (IAST) that;
      if (isList() && list2.isList()) {
        int size1 = size();
        if (size1 != list2.size()) {
          return IExpr.COMPARE_TERNARY.FALSE;
        }
        IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.TRUE;
        for (int i = 1; i < size1; i++) {
          b = get(i).equalTernary(list2.get(i), engine);
          if (b == IExpr.COMPARE_TERNARY.FALSE) {
            return IExpr.COMPARE_TERNARY.FALSE;
          }
          if (b != IExpr.COMPARE_TERNARY.TRUE) {
            return IExpr.COMPARE_TERNARY.UNDECIDABLE;
          }
        }
        return IExpr.COMPARE_TERNARY.TRUE;
      } else {
        int size1 = size();
        if (size1 == list2.size() && size1 > 0 && Objects.equals(head(), list2.head())) {
          IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.TRUE;
          for (int i = 1; i < size1; i++) {
            b = get(i).equalTernary(list2.get(i), engine);
            if (b != IExpr.COMPARE_TERNARY.TRUE) {
              return IASTMutable.super.equalTernary(that, engine);
            }
          }
          return IExpr.COMPARE_TERNARY.TRUE;
        }
      }
    }

    return IASTMutable.super.equalTernary(that, engine);
  }

  /** {@inheritDoc} */
  @Override
  public final INumber evalNumber() {
    if (isNumericFunction(true)) {
      IExpr result = EvalEngine.get().evalNumericFunction(this, false);
      if (result.isNumber()) {
        return (INumber) result;
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public final IReal evalReal() {
    if (isNumericFunction(true)) {
      IExpr result = EvalEngine.get().evalNumericFunction(this, false);
      if (result.isReal()) {
        return (IReal) result;
      }
      if (result.isComplexNumeric()) {
        IComplexNum cc = (IComplexNum) result;
        if (cc.im().isZero()) {
          return cc.re();
        }
      }
    } else if (isAST(S.Labeled, 3, 4)) {
      IExpr arg1 = arg1();
      if (arg1.isNumericFunction(true)) {
        IExpr result = EvalEngine.get().evalNumericFunction(arg1, false);
        if (result.isReal()) {
          return (IReal) result;
        }
        if (result.isComplexNumeric()) {
          IComplexNum cc = (IComplexNum) result;
          if (cc.im().isZero()) {
            return cc.re();
          }
        }
      }
    }
    return null;
  }

  /**
   * Evaluate arguments with the head <code>F.Evaluate</code>, i.e. <code>
   * f(a, ... , Evaluate(x), ...)</code>
   *
   * @param engine the evaluation engine
   * @return
   */
  public IExpr evalEvaluate(EvalEngine engine) {
    IASTMutable[] rlist = new IASTMutable[] {F.NIL};
    if (!isHoldAllCompleteAST()) {
      forEach((x, i) -> {
        if (x.isAST(S.Evaluate)) {
          engine.evalArg(rlist, this, x, i, false);
        }
      });
    }
    return rlist[0];
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    final IExpr head = head();
    if (head instanceof ISymbol) {
      if (head instanceof IBuiltInSymbol) {
        IBuiltInSymbol symbol = (IBuiltInSymbol) head;
        final IEvaluator evaluator = symbol.getEvaluator();
        if (evaluator instanceof ICoreFunctionEvaluator) {
          if (isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
            return F.NIL;
          }
          try {
            ICoreFunctionEvaluator functionEvaluator = (ICoreFunctionEvaluator) evaluator;
            EvalEngine.OptionsResult opres = engine.checkBuiltinArguments(this, functionEvaluator);
            if (opres == null) {
              return F.NIL;
            }
            IAST ast = opres.result;
            IBuiltInSymbol header = symbol;
            if ((header.getAttributes() & ISymbol.SEQUENCEHOLD) != ISymbol.SEQUENCEHOLD) {
              IExpr temp;
              if ((temp = F.flattenSequence(this)).isPresent()) {
                return temp;
              }
            }
            if (isBooleanFunction()) {
              IExpr temp = extractConditionalExpression(false);
              if (temp.isPresent()) {
                return temp;
              }
            }

            IExpr evaluateTemp = evalEvaluate(engine);
            if (evaluateTemp.isPresent()) {
              return evaluateTemp;
            }
            if (Config.PROFILE_MODE) {
              long beginTime = System.nanoTime();
              try {
                return functionEvaluator.evaluate(ast, engine);
              } finally {
                long endTime = System.nanoTime();
                BuiltinFunctionCalls calls = new BuiltinFunctionCalls(symbol);
                BuiltinFunctionCalls b = Config.PRINT_PROFILE.get(calls);
                if (b == null) {
                  Config.PRINT_PROFILE.put(calls, calls);
                } else {
                  calls = b;
                }
                calls.incCalls(endTime - beginTime);
              }
            }
            return functionEvaluator.evaluate(ast, engine);
          } catch (ValidateException ve) {
            return Errors.printMessage(topHead(), ve, engine);
          } catch (FlowControlException e) {
            throw e;
          } catch (SymjaMathException ve) {
            return Errors.printMessage(topHead(), ve, engine);
          }
        }
      } else {
        ISymbol headSymbol = (ISymbol) head;
        Class<?> clazz = headSymbol.getContext().getJavaClass();
        if (clazz != null) {
          String staticMethodName = headSymbol.getSymbolName();
          Method[] methods = clazz.getMethods();
          for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
              if (staticMethodName.equals(method.getName())) {
                Parameter[] parameters = method.getParameters();
                if (parameters.length == argSize()) {
                  Object[] params = JavaFunctions.determineParameters(this, parameters, 1);
                  if (params != null) {
                    Object result;
                    try {
                      result = method.invoke(null, params);

                      if (result instanceof String) {
                        return F.stringx((String) result);
                      }
                      return Object2Expr.convert(result, false, true);
                    } catch (IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                      // fall through?
                    }
                  }
                }
              }
            }
          }
        }
      }
    } else if (head.isAssociation() && argSize() == 1) {
      return ((IAssociation) head).getValue(engine.evaluate(arg1()));
    }

    final ISymbol symbol = topHead();
    boolean oldNumericMode = engine.isNumericMode();
    try {
      return engine.evalAttributes(symbol, this).orElseGet(() -> engine.evalRules(symbol, this));
    } finally {
      engine.setNumericMode(oldNumericMode);
    }
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
    result[0] = copyHead(size());
    result[1] = copyHead(size());
    filterFunction(result[0], result[1], function);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public IAST filter(IASTAppendable filterAST, IASTAppendable restAST,
      Predicate<? super IExpr> predicate) {
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
  public final IAST filter(IASTAppendable filterAST, IExpr unaryHead) {
    EvalEngine engine = EvalEngine.get();
    return filter(filterAST, x -> engine.evalTrue(unaryHead, x));
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

  @Override
  public IAST select(Predicate<? super IExpr> predicate) {
    int[] items = new int[size()];
    int length = 0;
    for (int i = 1; i < size(); i++) {
      if (predicate.test(get(i))) {
        items[length++] = i;
      }
    }
    if (length == argSize()) {
      return this;
    }
    return getItems(items, length, 0);
  }

  @Override
  public IAST select(Predicate<? super IExpr> predicate, int maxMatches) {
    maxMatches = size() > maxMatches ? maxMatches : size();
    int[] items = new int[maxMatches];
    int length = 0;
    for (int i = 1; i < size(); i++) {
      if (predicate.test(get(i))) {
        items[length++] = i;
        if (maxMatches == length) {
          break;
        }
      }
    }
    if (length == argSize()) {
      return this;
    }
    return getItems(items, length, 0);
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable[] filter(Predicate<? super IExpr> predicate) {
    IASTAppendable[] result = new IASTAppendable[2];
    result[0] = copyHead();
    result[1] = copyHead();
    filter(result[0], result[1], predicate);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public IAST removeIf(Predicate<? super IExpr> predicate) {
    IASTAppendable result = F.NIL;
    for (int i = 1; i < size(); i++) {
      final IExpr arg = get(i);
      if (predicate.test(arg)) {
        continue;
      }
      if (result.isNIL()) {
        result = copyHead(argSize());
      }
      result.appendRule(arg);
    }
    return result.orElse(this);
  }

  /**
   * Select all elements by applying the <code>function</code> to each argument in this <code>AST
   * </code> and append the result elements for which the <code>function</code> returns non <code>
   * F.NIL</code> elements to the <code>filterAST</code>, or otherwise append the argument to the
   * <code>restAST</code>.
   *
   * @param filterAST the non <code>F.NIL</code> elements which were returned by the <code>
   *     function#apply()</code> method
   * @param restAST the arguments in this <code>AST</code> for which the <code>function#apply()
   *     </code> method returned <code>F.NIL</code>
   * @param function the function which filters each argument by returning a value which unequals
   *        <code>F.NIL</code>
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

  /** {@inheritDoc} */
  @Override
  public IExpr foldLeft(final BiFunction<IExpr, IExpr, ? extends IExpr> function, IExpr startValue,
      int start) {
    IExpr value = startValue;
    for (int i = start; i < size(); i++) {
      value = function.apply(value, get(i));
      if (value.isNIL()) {
        return F.NIL;
      }
    }
    return value;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr foldRight(final BiFunction<IExpr, IExpr, ? extends IExpr> function, IExpr startValue,
      int start) {
    IExpr value = startValue;
    int end = argSize();
    for (int i = end; i >= start; i--) {
      value = function.apply(value, get(i));
      if (value.isNIL()) {
        return F.NIL;
      }
    }
    return value;
  }

  /** {@inheritDoc} */
  @Override
  public boolean parallelAllMatch(IAST ast, int startPosition, int endPosition,
      BiPredicate<? super IExpr, ? super IExpr> predicate) {
    IntStream stream = IntStream.range(startPosition, endPosition);
    int rangeSize = endPosition - startPosition;
    if (rangeSize <= Config.MIN_LIMIT_PERSISTENT_LIST || Config.JAS_NO_THREADS) {
      return stream.allMatch(i -> predicate.test(getRule(i), ast.getRule(i)));
    }
    return stream.parallel().allMatch(i -> predicate.test(getRule(i), ast.getRule(i)));
  }

  @Override
  public boolean parallelAnyMatch(IAST ast, int startPosition, int endPosition,
      BiPredicate<? super IExpr, ? super IExpr> predicate) {
    IntStream stream = IntStream.range(startPosition, endPosition);
    int rangeSize = endPosition - startPosition;
    if (rangeSize <= Config.MIN_LIMIT_PERSISTENT_LIST || Config.JAS_NO_THREADS) {
      return stream.anyMatch(i -> predicate.test(getRule(i), ast.getRule(i)));
    }
    return stream.parallel().anyMatch(i -> predicate.test(getRule(i), ast.getRule(i)));
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
    final int size = size();
    for (int i = startOffset; i < size; i++) {
      if (!predicate.test(getRule(i), i)) {
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
      if (!predicate.test(getRule(i))) {
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

  @Override
  public boolean forAllLeaves(IExpr head, Predicate<? super IExpr> predicate, int startOffset) {
    final int size = size();
    if (!Objects.equals(head(), head)) {
      return predicate.test(this);
    }
    for (int i = startOffset; i < size; i++) {
      if (get(i).isAST()) {
        if (!((IAST) get(i)).forAllLeaves(head, predicate, startOffset)) {
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
  public void forEachRule(Consumer<? super IExpr> action) {
    forEachRule(action, 1);
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(Consumer<? super IExpr> action, int startOffset) {
    forEach(startOffset, size(), action);
  }

  /** {@inheritDoc} */
  @Override
  public void forEachRule(Consumer<? super IExpr> action, int startOffset) {
    forEach(action, startOffset);
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
      head = S.Null;
    } else {
      text.append(head.fullFormString());
    }
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS && head.isSymbol()) {
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
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS && head.isSymbol()) {
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
  public final IExpr lcm(IExpr that) {
    if (that.isZero()) {
      return F.C0;
    }
    return IASTMutable.super.lcm(that);
  }

  @Override
  public abstract IExpr get(int location);

  @Override
  public IExpr get(IInteger location) {
    return get(location.toIntDefault());
  }

  /**
   * Casts an <code>IExpr</code> at position <code>index</code> to an <code>IAST</code>.
   *
   * @param index
   * @return
   * @throws IllegalArgumentException if the cast is not possible
   */
  @Override
  public final IAST getAST(int index) {
    try {
      return (IAST) get(index);
    } catch (ClassCastException cce) {
    }
    throw new IllegalArgumentException("argument " + get(index).toString() + " is not an IAST");
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
   * @throws IllegalArgumentException if the cast is not possible
   */
  @Override
  public final IInteger getInt(int index) {
    try {
      return (IInteger) get(index);
    } catch (ClassCastException cce) {
    }

    throw new IllegalArgumentException("argument " + get(index).toString() + " is not an IInteger");
  }

  /**
   * Casts an <code>IExpr</code> which is a list at position <code>index</code> to an <code>IAST
   * </code>.
   *
   * @param index
   * @return
   * @throws IllegalArgumentException
   */
  @Override
  public final IAST getList(int index) {
    IExpr temp = get(index);
    if (temp.isList()) {
      return (IAST) temp;
    }

    throw new IllegalArgumentException("argument " + get(index).toString() + " is not a list");
  }

  /**
   * Casts an <code>IExpr</code> at position <code>index</code> to an <code>INumber</code>.
   *
   * @param index
   * @return
   * @throws IllegalArgumentException if the cast is not possible
   */
  @Override
  public final INumber getNumber(int index) {
    try {
      return (INumber) get(index);
    } catch (ClassCastException cce) {
    }
    throw new IllegalArgumentException("argument " + get(index).toString() + " is not an INumber");
  }

  /** {@inheritDoc} */
  @Override
  public IExpr getOptionalValue() {
    if (isAST(S.Optional, 3)) {
      return arg2();
    }
    return null;
  }

  @Override
  public IExpr getPart(final int... positions) {
    IExpr expr = this;
    int size = positions.length;
    for (int i = 0; i < size; i++) {
      if (!expr.isASTOrAssociation()) {
        break;
      }

      // throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 2");
      IAST list = (IAST) expr;
      if (list.size() <= positions[i]) {
        Errors.printMessage(S.Part, "partw", F.List(F.ZZ(positions[i]), expr));
        throw new IndexOutOfBoundsException(
            "Index: " + Integer.valueOf(positions[i]) + ", Size: " + list.size());
      }
      expr = list.get(positions[i]);
      if (i == (size - 1)) {
        return expr;
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr getIndex(int... positions) {
    return getPart(positions);
  }

  @Override
  public final IExpr getPart(final IntList positions) {
    IExpr expr = this;
    int size = positions.size();
    for (int i = 0; i < size; i++) {
      if (!expr.isAST()) {
        break;
      }
      expr = ((IAST) expr).get(positions.getInt(i));
      if (i == (size - 1)) {
        return expr;
      }
    }
    return null;
  }

  /**
   * Returns the value to which the specified property is mapped, or <code>null</code> if this map
   * contains no mapping for the property.
   *
   * @param key
   * @return
   * @see #putProperty(PROPERTY, Object)
   */
  @Override
  public Object getProperty(PROPERTY key) {
    EnumMap<PROPERTY, Object> map = propertyCache().getIfPresent(this);
    if (map == null) {
      return null;
    }
    return map.get(key);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean has(Predicate<IExpr> predicate, boolean heads) {
    if (predicate.test(this)) {
      return true;
    }
    return exists(x -> x.has(predicate, heads), heads ? 0 : 1);
  }

  @Override
  public final boolean hasTrigonometricFunction() {
    return has(AbstractAST::isTrigonometricFunction, false);
  }

  /**
   * FNV-1 hash code of this <code>IAST</code>.
   *
   * <p>
   * See: <a href=
   * "https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function#FNV-1_hash">
   * Wikipedia: Fowler–Noll–Vo hash function</a>
   */
  @Override
  public int hashCode() {
    if (hashValue == 0) {
      int size = size();
      if (size <= 3) {
        hashValue = (0x811c9dc5 * 16777619) ^ (size & 0xff); // decimal 2166136261;
        for (int i = 0; i < size; i++) {
          hashValue = (hashValue * 16777619) ^ (get(i).hashCode() & 0xff);
        }
      } else {
        size = 4;
        hashValue = (0x811c9dc5 * 16777619) ^ (size & 0xff); // decimal 2166136261;
        hashValue = (hashValue * 16777619) ^ (head().hashCode() & 0xff);
        hashValue = (hashValue * 16777619) ^ (arg1().hashCode() & 0xff);
        hashValue = (hashValue * 16777619) ^ (arg2().hashCode() & 0xff);
        hashValue = (hashValue * 16777619) ^ (arg3().hashCode() & 0xff);
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
  public int indexOf(final IExpr expr) {
    for (int i = 1; i < size(); i++) {
      if (getRule(i).equals(expr)) {
        return i;
      }
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(Predicate<? super IExpr> predicate, int fromIndex) {
    for (int i = fromIndex; i < size(); i++) {
      if (predicate.test(getRule(i))) {
        return i;
      }
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr findFirst(Function<IExpr, IExpr> function) {
    for (int i = 1; i < size(); i++) {
      IExpr temp = function.apply(getRule(i));
      if (temp.isPresent()) {
        return temp;
      }
    }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public final CharSequence internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    return internalJavaString(SourceCodeProperties.stringFormProperties(symbolsAsFactoryMethod),
        depth, x -> null);
  }

  /** {@inheritDoc} */
  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    final String sep = ",";
    final IExpr temp = head();
    if ((temp.equals(S.HoldForm) && size() == 2) || (temp.equals(S.Hold) && size() == 2)) {
      return arg1().internalJavaString(properties, depth, variables);
    }
    String prefix = SourceCodeProperties.getPrefixF(properties);
    if (isInfinity()) {
      return new StringBuilder(prefix).append("oo");
    }
    if (isNegativeInfinity()) {
      return new StringBuilder(prefix).append("Noo");
    }
    if (isComplexInfinity()) {
      return new StringBuilder(prefix).append("CComplexInfinity");
    }
    if (this.equals(F.Slot1)) {
      return new StringBuilder(prefix).append("Slot1");
    }
    if (this.equals(F.Slot2)) {
      return new StringBuilder(prefix).append("Slot2");
    }
    if (temp.equals(S.Inequality) && size() >= 4) {
      return BooleanFunctions.inequality2And(this).internalJavaString(properties, depth, variables);
    }
    if (temp.equals(S.Rational) && size() == 3) {
      if (arg1().isInteger() && arg2().isInteger()) {
        return F.QQ((IInteger) arg1(), (IInteger) arg2()).internalJavaString(properties, depth,
            variables);
      }
    }
    if (isPower()) {
      if (arg1().isInteger() && arg2().isMinusOne()) {
        IInteger i = (IInteger) arg1();
        if (i.equals(F.C2)) {
          return new StringBuilder(prefix).append("C1D2");
        } else if (i.equals(F.C3)) {
          return new StringBuilder(prefix).append("C1D3");
        } else if (i.equals(F.C4)) {
          return new StringBuilder(prefix).append("C1D4");
        } else if (i.equals(F.CN2)) {
          return new StringBuilder(prefix).append("CN1D2");
        } else if (i.equals(F.CN3)) {
          return new StringBuilder(prefix).append("CN1D3");
        } else if (i.equals(F.CN4)) {
          return new StringBuilder(prefix).append("CN1D4");
        }
      }
      if (equalsAt(1, S.E)) {
        return new StringBuilder(prefix).append("Exp(")
            .append(arg2().internalJavaString(properties, depth + 1, variables)).append(")");
      }
      if (equalsAt(2, F.C1D2)) {
        if (base().isInteger()) {
          // square root of an integer number
          IInteger i = (IInteger) base();
          if (i.equals(F.C2)) {
            return new StringBuilder(prefix).append("CSqrt2");
          } else if (i.equals(F.C3)) {
            return new StringBuilder(prefix).append("CSqrt3");
          } else if (i.equals(F.C5)) {
            return new StringBuilder(prefix).append("CSqrt5");
          } else if (i.equals(F.C6)) {
            return new StringBuilder(prefix).append("CSqrt6");
          } else if (i.equals(F.C7)) {
            return new StringBuilder(prefix).append("CSqrt7");
          } else if (i.equals(F.C10)) {
            return new StringBuilder(prefix).append("CSqrt10");
          }
        }
        if (base().isPi()) {
          return new StringBuilder(prefix).append("CSqrtPi");
        }
        return new StringBuilder(prefix).append("Sqrt(")
            .append(arg1().internalJavaString(properties, depth + 1, variables)).append(")");
      }
      if (equalsAt(2, F.C2)) {
        return new StringBuilder(prefix).append("Sqr(")
            .append(arg1().internalJavaString(properties, depth + 1, variables)).append(")");
      }
      if (equalsAt(2, F.CN1D2) && arg1().isInteger()) {
        // negative square root of an integer number
        IInteger i = (IInteger) arg1();
        if (i.equals(F.C2)) {
          return new StringBuilder(prefix).append("C1DSqrt2");
        } else if (i.equals(F.C3)) {
          return new StringBuilder(prefix).append("C1DSqrt3");
        } else if (i.equals(F.C5)) {
          return new StringBuilder(prefix).append("C1DSqrt5");
        } else if (i.equals(F.C6)) {
          return new StringBuilder(prefix).append("C1DSqrt6");
        } else if (i.equals(F.C7)) {
          return new StringBuilder(prefix).append("C1DSqrt7");
        } else if (i.equals(F.C10)) {
          return new StringBuilder(prefix).append("C1DSqrt10");
        }
      }
      // don't optimize if arg2() is integer
    }
    StringBuilder text = new StringBuilder(size() * 10);
    if (temp.isSymbol()) {
      ISymbol sym = (ISymbol) temp;
      String name = null;
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        name = sym.toString();
        if (name.length() > 0) {
          name = name.toLowerCase(Locale.ENGLISH);
        }
        name = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(name);
      }
      if (name == null && !Character.isUpperCase(sym.toString().charAt(0))) {
        text.append(prefix).append("$(");
        for (int i = 0; i < size(); i++) {
          text.append(get(i).internalJavaString(properties, depth + 1, variables));
          if (i < argSize()) {
            text.append(sep);
          }
        }
        return text.append(')');
      }
    } else if (temp.isPattern() || temp.isAST()) {
      text.append(prefix).append("$(");
      for (int i = 0; i < size(); i++) {
        text.append(get(i).internalJavaString(properties, depth + 1, variables));
        if (i < argSize()) {
          text.append(sep);
        }
      }
      return text.append(')');
    }

    if (isTimes2()) {
      if (arg2().equals(S.Pi)) {
        if (equals(F.CNPi)) {
          return new StringBuilder(prefix).append("CNPi");
        } else if (equals(F.CN2Pi)) {
          return new StringBuilder(prefix).append("CN2Pi");
        } else if (equals(F.C2Pi)) {
          return new StringBuilder(prefix).append("C2Pi");
        } else if (equals(F.CNPiHalf)) {
          return new StringBuilder(prefix).append("CNPiHalf");
        } else if (equals(F.CPiHalf)) {
          return new StringBuilder(prefix).append("CPiHalf");
        } else if (equals(F.CNPiThird)) {
          return new StringBuilder(prefix).append("CNPiThird");
        } else if (equals(F.CPiThird)) {
          return new StringBuilder(prefix).append("CPiThird");
        } else if (equals(F.CNPiQuarter)) {
          return new StringBuilder(prefix).append("CNPiQuarter");
        } else if (equals(F.CPiQuarter)) {
          return new StringBuilder(prefix).append("CPiQuarter");
        }
      }
      if (arg1().isMinusOne() && !arg2().isTimes()) {
        if (arg2().isNumber()) {
          IExpr num = ((INumber) arg2()).negate();
          return num.internalJavaString(properties, depth + 1, variables);
        }
        return new StringBuilder(prefix).append("Negate(")
            .append(arg2().internalJavaString(properties, depth + 1, variables)).append(")");
      }
    } else if (isAST(S.Plus, 3)) {
      if (arg2().isTimes2() && arg2().first().isMinusOne()) {
        return new StringBuilder(prefix).append("Subtract(")
            .append(arg1().internalJavaString(properties, depth + 1, variables)).append(",")
            .append(arg2().second().internalJavaString(properties, depth + 1, variables))
            .append(")");
      }
    } else if (isList() && size() <= 4) {
      switch (size()) {
        case 2:
          return new StringBuilder(prefix).append("list(")
              .append(arg1().internalJavaString(properties, depth + 1, variables)).append(")");
        case 3:
          return new StringBuilder(prefix).append("list(")
              .append(arg1().internalJavaString(properties, depth + 1, variables)).append(",")
              .append(arg2().internalJavaString(properties, depth + 1, variables)).append(")");
        case 4:
          return new StringBuilder(prefix).append("list(")
              .append(arg1().internalJavaString(properties, depth + 1, variables)).append(",")
              .append(arg2().internalJavaString(properties, depth + 1, variables)).append(",")
              .append(arg3().internalJavaString(properties, depth + 1, variables)).append(")");

      }
    }

    if (properties.useOperators && size() == 3) {
      if (isTimes()) {
        IExpr arg1 = arg1();
        IExpr arg2 = arg2();
        boolean isLowerPrecedence = arg1.isPlus();
        internalOperatorForm(arg1, isLowerPrecedence, properties, depth, text);
        text.append('*');
        isLowerPrecedence = arg2.isPlus();
        internalOperatorForm(arg2, isLowerPrecedence, properties, depth, text);
        return text;
      } else if (isPlus()) {
        IExpr arg1 = arg1();
        IExpr arg2 = arg2();
        internalOperatorForm(arg1, false, properties, depth, text);
        text.append('+');
        internalOperatorForm(arg2, false, properties, depth, text);
        return text;
      } else if (isPower()) {
        IExpr arg1 = arg1();
        IExpr arg2 = arg2();
        boolean isLowerPrecedence = arg1.isTimes() || arg1.isPlus();
        internalOperatorForm(arg1, isLowerPrecedence, properties, depth, text);
        text.append('^');
        isLowerPrecedence = arg2.isTimes() || arg2.isPlus();
        internalOperatorForm(arg2, isLowerPrecedence, properties, depth, text);
        return text;
      }
    }

    text.append(temp.internalJavaString(
        SourceCodeProperties.copyWithoutSymbolsAsFactoryMethod(properties), 0, variables));
    text.append('(');
    if (isTimes() || isPlus()) {
      if (depth == 0 && isList()) {
        text.append('\n');
      }
      internalFormOrderless(this, text, sep, properties, depth, variables);
    } else {
      if (depth == 0 && isList()) {
        text.append('\n');
      }
      for (int i = 1; i < size(); i++) {
        text.append(get(i).internalJavaString(properties, depth + 1, variables));
        if (i < argSize()) {
          text.append(sep);
          if (depth == 0 && isList()) {
            text.append('\n');
          }
        }
      }
    }
    if (depth == 0 && isList()) {
      text.append('\n');
    }
    return text.append(')');
  }

  private void internalOperatorForm(IExpr arg1, boolean isLowerPrecedence,
      SourceCodeProperties properties, int depth, StringBuilder text) {
    if (isLowerPrecedence) {
      text.append('(');
    }
    text.append(arg1.internalJavaString(properties, depth + 1, x -> null));
    if (isLowerPrecedence) {
      text.append(')');
    }
  }

  /** {@inheritDoc} */
  @Override
  public final CharSequence internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
    return internalJavaString(SourceCodeProperties.scalaFormProperties(symbolsAsFactoryMethod),
        depth, x -> null);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAbs() {
    return isSameHead(S.Abs, 2);
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
    return head() == S.Alternatives;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isAnd() {
    return isSameHeadSizeGE(S.And, 3);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isArcCos() {
    return isSameHead(S.ArcCos, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isArcCosh() {
    return isSameHead(S.ArcCosh, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isArcSin() {
    return isSameHead(S.ArcSin, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isArcSinh() {
    return isSameHead(S.ArcSinh, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isArcTan() {
    return isSameHead(S.ArcTan, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isArcTanh() {
    return isSameHead(S.ArcTanh, 2);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST(final IBuiltInSymbol header) {
    return head() == header;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST(final IBuiltInSymbol header, final int length) {
    return head() == header && length == size();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST(final IExpr header) {
    return Objects.equals(head(), header);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST(final IExpr header, final int length) {
    return Objects.equals(head(), header) && length == size();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST(final IExpr header, final int length, Predicate<IExpr> pred) {
    if (isAST(header, length)) {
      for (int i = 1; i < length; i++) {
        if (!pred.test(get(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
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
  public boolean isAST(IExpr head, int minLength, int maxLength) {
    int size = size();
    return Objects.equals(head(), head) && minLength <= size && maxLength >= size;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST(final String symbol) {
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
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

  /** {@inheritDoc} */
  @Override
  public boolean isAST(final String symbol, final int length) {
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
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

  /** {@inheritDoc} */
  @Override
  public boolean isASTSizeGE(final IExpr header, final int length) {
    return Objects.equals(head(), header) && length <= size();
  }

  @Override
  public boolean isAtom() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isComplexInfinity() {
    return isSameHead(S.DirectedInfinity, 1);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isCondition() {
    return head() == S.Condition && size() == 3;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isConditionalExpression() {
    return head() == S.ConditionalExpression && size() == 3;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isConjugate() {
    return isSameHead(S.Conjugate, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isCos() {
    return isSameHead(S.Cos, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isCosh() {
    return isSameHead(S.Cosh, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isDefer() {
    return isSameHead(S.Defer, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final IAST[] isDerivative() {
    if (head().isAST()) {
      IAST headAST = (IAST) head();
      if (headAST.isSameHeadSizeGE(S.Derivative, 2)) {
        IAST[] result = new IAST[3];
        result[0] = headAST;
        result[1] = this;
        return result;
      }

      if (headAST.head().isSameHeadSizeGE(S.Derivative, 2)) {
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
      if (headAST.isAST(S.Derivative, 2)) {
        IAST[] result = new IAST[3];
        result[0] = headAST;
        result[1] = this;
        return result;
      }

      if (headAST.head().isAST(S.Derivative, 2)) {
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
    return isSameHead(S.DirectedInfinity, 1, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isDirectedInfinity(INumber x) {
    return isSameHead(S.DirectedInfinity, 2) && arg1().equals(x);
  }

  @Override
  public boolean isContinuousDistribution() {
    if (isBuiltInFunction()) {
      IEvaluator evaluator = ((IBuiltInSymbol) head()).getEvaluator();
      return evaluator instanceof IContinuousDistribution;
    }
    return false;
  }

  @Override
  public boolean isDiscreteDistribution() {
    if (isBuiltInFunction()) {
      IEvaluator evaluator = ((IBuiltInSymbol) head()).getEvaluator();
      return evaluator instanceof IDiscreteDistribution;
    }
    return false;
  }

  @Override
  public boolean isDistribution() {
    if (isBuiltInFunction()) {
      IEvaluator evaluator = ((IBuiltInSymbol) head()).getEvaluator();
      return evaluator instanceof IDistribution;
    }
    return false;
  }

  @Override
  public final boolean isEmpty() {
    return size() == 1;
  }

  @Override
  public final boolean isNotEmpty() {
    return size() >= 1;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isEqual() {
    return isSameHead(S.Equal, 3);
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
  public boolean isExcept() {
    return isAST(S.Except, 2, 3);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isExpanded() {
    return !(isPlusTimesPower() && (isEvalFlagOff(IAST.IS_EXPANDED)));
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFlatAST() {
    final IExpr head = head();
    return head.isSymbol() ? ((ISymbol) head).hasFlatAttribute() : false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isFree(IPatternMatcher predicate, boolean heads) {
    return !has(predicate, heads);
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
    if (predicate.test(this) || predicate.test(head())) {
      return false;
    }
    for (int i = 1; i < size(); i++) {
      IExpr arg = get(i);
      if (arg.isAST() && !arg.isFreeAST(predicate)) {
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
    int defaultOrOptionalCounter = 0;
    for (int i = 0; i < size(); i++) {
      // all elements including head element
      IExpr temp = getRule(i);
      if (temp.isASTOrAssociation() && !temp.isFreeOfPatterns()) {
        isFreeOfPatterns = false;
        if (temp.isOptional()) {
          addEvalFlags(IAST.CONTAINS_DEFAULT_PATTERN | IAST.CONTAINS_PATTERN);
          defaultOrOptionalCounter++;
        } else {
          addEvalFlags(((IAST) temp).getEvalFlags() & IAST.CONTAINS_PATTERN_EXPR);
        }
        continue;
      } else if (temp instanceof IPatternObject) {
        isFreeOfPatterns = false;
        if (temp instanceof IPatternSequence) {
          if (temp.isPatternDefault()) {
            addEvalFlags(IAST.CONTAINS_DEFAULT_PATTERN | IAST.CONTAINS_PATTERN_SEQUENCE);
            defaultOrOptionalCounter++;
          } else {
            addEvalFlags(IAST.CONTAINS_PATTERN_SEQUENCE);
          }
        } else {
          if (temp.isPatternDefault()) {
            addEvalFlags(IAST.CONTAINS_DEFAULT_PATTERN | IAST.CONTAINS_PATTERN);
            defaultOrOptionalCounter++;
          } else {
            addEvalFlags(IAST.CONTAINS_PATTERN);
          }
        }
      }
    }
    if (isFreeOfPatterns) {
      addEvalFlags(IAST.CONTAINS_NO_PATTERN);
    } else {
      if (defaultOrOptionalCounter == argSize()) {
        addEvalFlags(IAST.CONTAINS_ALL_DEFAULT_PATTERN);
      }
    }
    return isFreeOfPatterns;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isFunction() {
    return size() >= 2 && S.Function == head();
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
    return isSameHead(S.HoldPattern, 2) || isSameHead(S.Literal, 2);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFunctionID(int... builtinIDs) {
    int id = headID();
    if (id >= 0) {
      for (int builtinID : builtinIDs) {
        if (id == builtinID) {
          return true;
        }
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isHoldAllCompleteAST() {
    return topHead().hasHoldAllCompleteAttribute();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isInfinity() {
    return this.equals(F.CInfinity);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isBooleanFormula() {
    return head().isBooleanFormulaSymbol()
        && forAll((Predicate<? super IExpr>) IExpr::isBooleanFormula);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isBooleanResult() {
    return head().isPredicateFunctionSymbol()
        || ((head().isBooleanFormulaSymbol() || head().isComparatorFunctionSymbol())
            && forAll((Predicate<? super IExpr>) IExpr::isBooleanResult));
  }

  /** {@inheritDoc} */
  @Override
  public boolean isBuiltInFunction() {
    return head() instanceof IBuiltInSymbol;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isBooleanFunction() {
    return head().isBooleanFormulaSymbol() && size() >= 2;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isComparatorFunction() {
    return head().isComparatorFunctionSymbol() && size() > 2;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isIntegerResult() {
    if (S.True.equals(AbstractAssumptions.assumeInteger(this))) {
      return true;
    }
    if (isFunctionID(ID.Ceiling, ID.Floor, ID.IntegerPart)) {
      return true;
    }
    if (isPowerInteger() && base().isPositive()) {
      if (base().isIntegerResult()) {
        return true;
      }
      return false;
    }
    IExpr head = head();
    if (isPlus() || isTimes() || head == S.Binomial || head == S.Factorial) {
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
    if (isSameHeadSizeGE(S.Interval, 2)) {
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
  public boolean isIntervalData() {
    if (isAST(S.IntervalData)) {
      for (int i = 1; i < size(); i++) {
        if (!(get(i).isAST(S.List, 5))) {
          return false;
        }
      }
      // the empty IntervalData() interval returns also true.
      return true;
    }
    return false;
  }

  @Override
  public COMPARE_TERNARY isIrrational() {
    if (head() == S.Plus) {
      IAST plusAST = this;
      for (int i = 1; i < plusAST.size(); i++) {
        IExpr arg = plusAST.get(i);
        COMPARE_TERNARY irrational = arg.isIrrational();
        if (irrational == COMPARE_TERNARY.TRUE) {
          IAST others = plusAST.removeAtCopy(i);
          if (others.forAll((Predicate<? super IExpr>) IExpr::isRational)) {
            return COMPARE_TERNARY.TRUE;
          }
          return COMPARE_TERNARY.UNDECIDABLE;
        }
        if (irrational == COMPARE_TERNARY.UNDECIDABLE) {
          return COMPARE_TERNARY.UNDECIDABLE;
        }
      }
    } else if (head() == S.Times) {
      IAST timesAST = this;
      for (int i = 1; i < timesAST.size(); i++) {
        IExpr arg = timesAST.get(i);
        COMPARE_TERNARY irrational = arg.isIrrational();
        if (irrational == COMPARE_TERNARY.TRUE) {
          IAST others = timesAST.removeAtCopy(i);
          if (others.forAll(x -> x.isRational() && !x.isZero())) {
            return COMPARE_TERNARY.TRUE;
          }
          return COMPARE_TERNARY.UNDECIDABLE;
        }
        if (irrational == COMPARE_TERNARY.UNDECIDABLE) {
          return COMPARE_TERNARY.UNDECIDABLE;
        }
      }
      if (timesAST.forAll((Predicate<? super IExpr>) IExpr::isReal)) {
        return COMPARE_TERNARY.FALSE;
      }
    }
    return COMPARE_TERNARY.UNDECIDABLE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEmptyIntervalData() {
    return isAST(S.IntervalData, 1);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isInterval1() {
    return isSameHead(S.Interval, 2) && arg1().isList2();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEmptyList() {
    return equals(F.CEmptyList);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNonEmptyList() {
    return isList() && size() > 1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isKey() {
    return isSameHead(S.Key, 2);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isList() {
    return head() == S.List;
  }


  private boolean isList(int[] dimensions, int pos) {
    if (isList() && dimensions[pos] == argSize()) {
      final int posPlus1 = pos + 1;
      if (dimensions.length == posPlus1) {
        return true;
      }
      return this.forAll(x -> {
        if (x.isList()) {
          return ((AbstractAST) x).isList(dimensions, posPlus1);
        }
        return false;
      });
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isList(int[] dimensions) {
    if (dimensions == null) {
      return false;
    }
    return isList(dimensions, 0);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isListOfPoints(int pointDimension) {
    if (isList()) {
      for (int i = 1; i < size(); i++) {
        IExpr arg = get(i);
        if (arg.isAST(S.List, pointDimension + 1)) {
          continue;
        }
        if (arg.isASTSizeGE(S.Style, 2)) {
          if (arg.first().isAST(S.List, pointDimension + 1)) {
            continue;
          }
        } else if (arg.isASTSizeGE(S.Labeled, 2)) {
          if (arg.first().isAST(S.List, pointDimension + 1)) {
            continue;
          }
        }
        return false;
      }
      return true;
    }
    return false;
  }

  @Override
  public final boolean isListableAST() {
    return topHead().hasListableAttribute();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isList(Predicate<IExpr> pred) {
    if (isList() && size() > 1) {
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
    if (isList() && size() > 1) {
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
  public boolean isListOfMatrices() {
    if (isList() && size() > 1) {
      for (int i = 1; i < size(); i++) {
        if (get(i).isMatrix(false) == null) {
          // the row is no matrix
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
    if (S.List == head()) {
      boolean directed = true;
      for (int i = 1; i < size(); i++) {
        IExpr temp = get(i);
        if (temp.argSize() == 2 && temp.isBuiltInFunction()) {
          IBuiltInSymbol symbol = (IBuiltInSymbol) temp.head();
          if (symbol == S.DirectedEdge || symbol == S.Rule) {
            continue;
          }
          if (!(symbol == S.UndirectedEdge || symbol == S.TwoWayRule)) {
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
    if (argSize() == 2 && isBuiltInFunction()) {
      IBuiltInSymbol symbol = (IBuiltInSymbol) head();
      return (symbol == S.DirectedEdge || symbol == S.UndirectedEdge || symbol == S.Rule
          || symbol == S.TwoWayRule);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isListOfRules(boolean ignoreEmptySublists) {
    if (S.List == head()) {
      for (int i = 1; i < size(); i++) {
        if (!get(i).isRuleAST()) {
          if (ignoreEmptySublists && get(i).isEmptyList()) {
            continue;
          }
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
  public boolean isListOfRulesOrAssociation(boolean ignoreEmptySublists) {
    if (isAssociation()) {
      return true;
    }
    if (S.List == head()) {
      for (int i = 1; i < size(); i++) {
        if (!get(i).isRuleAST()) {
          if (get(i).isAssociation()) {
            if (!ignoreEmptySublists && get(i).size() <= 1) {
              return false;
            }
            continue;
          }
          if (ignoreEmptySublists && get(i).isEmptyList()) {
            continue;
          }
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
  public boolean isListOfStrings() {
    if (isList() && size() > 1) {
      for (int i = 1; i < size(); i++) {
        if (!get(i).isString()) {
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
    return isSameHead(S.Log, 2);
  }

  public int[] isAssociationMatrix() {
    return null;
  }

  public int isAssociationVector() {
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public int[] isMatrix(boolean setMatrixFormat) {
    if (isEvalFlagOn(IAST.IS_MATRIX)) {
      final int[] dim = new int[2];
      dim[0] = argSize();
      if (dim[0] > 0) {
        dim[1] = ((IAST) first()).argSize();
        return dim;
      }
    }
    if (isList()) {
      final int[] dim = new int[2];
      dim[0] = argSize();
      if (dim[0] > 0) {
        dim[1] = 0;
        if (arg1().isList()) {
          dim[1] = ((IAST) arg1()).argSize();
          for (int i = 1; i < size(); i++) {
            if (!get(i).isList()) {
              // this row is not a list
              return null;
            }
            IAST rowList = (IAST) get(i);
            if (dim[1] != rowList.argSize()) {
              // this row has another dimension
              return null;
            }
            for (int j = 1; j < rowList.size(); j++) {
              if (rowList.get(j).isList()) {
                // this row is not a list
                return null;
              }
            }
          }
          if (setMatrixFormat && (dim[0] > 1 || dim[1] > 0)) {
            addEvalFlags(IAST.IS_MATRIX);
          }
          return dim;
        }
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public int[] isMatrixIgnore() {
    if (isEvalFlagOn(IAST.IS_MATRIX)) {
      final int[] dim = new int[2];
      dim[0] = argSize();
      if (dim[0] > 0) {
        dim[1] = ((IAST) first()).argSize();
        return dim;
      }
    }
    if (isList()) {
      final int[] dim = new int[2];
      dim[0] = argSize();
      if (dim[0] > 0) {
        dim[1] = -1;
        for (int i = 1; i < size(); i++) {
          IExpr arg = get(i);
          if (arg.isList()) {
            if (dim[1] < 0) {
              dim[1] = ((IAST) arg).argSize();
            } else if (dim[1] != ((IAST) arg).argSize()) {
              // this row has another dimension
              return null;
            }
          } else {
            dim[0]--;
          }
        }
        if (dim[0] == 0) {
          return null;
        }
        return dim;
      }
    }
    return null;
  }

  @Override
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
    return head() == S.Module && size() == 3;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isModuleOrWithCondition() {
    if ((head() == S.With && size() >= 3) || (head() == S.Module && size() == 3)) {
      return (last().isCondition() || last().isModuleOrWithCondition());
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    if (isNumericFunction(true)) {
      IExpr result = EvalEngine.get().evalNumericFunction(this, false);
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
    if (isIntervalData() && size() > 1) {
      return IntervalDataSym.isNegativeResult(this);
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
    if (isIntervalData() && size() > 1) {
      return IntervalDataSym.isNonNegativeResult(this);
    }
    return AbstractAssumptions.isNonNegativeResult(this);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isNot() {
    return size() == 2 && S.Not == head();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericArgument(boolean allowList) {
    if (isEvalFlagOn(IAST.CONTAINS_NUMERIC_ARG)) {
      return forAll(x -> x.isNumericFunction(allowList)
          || (x.isList() && ((IAST) x).forAll(y -> y.isNumericFunction(allowList))));
    }
    if (allowList && isList()) {
      return exists(x -> x.isNumericArgument(allowList));
    }
    // TODO optimize this expression:
    return isAST(S.Interval) && forAll(x -> x.isNumericArgument(allowList)
        || (x.isList() && ((IAST) x).forAll(y -> y.isNumericArgument(allowList))));
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericFunction(boolean allowList) {

    final int evalFlags = getEvalFlags();
    if ((evalFlags & IAST.IS_NUMERIC_MASK) != IAST.NO_FLAG) {
      if (allowList) {
        if ((evalFlags & IAST.IS_NUMERIC_FUNCTION_OR_LIST) == IAST.IS_NUMERIC_FUNCTION_OR_LIST) {
          return true;
        } else if ((evalFlags
            & IAST.IS_NOT_NUMERIC_FUNCTION_OR_LIST) == IAST.IS_NOT_NUMERIC_FUNCTION_OR_LIST) {
          return false;
        }
      } else {
        if ((evalFlags & IAST.IS_NUMERIC_FUNCTION) == IAST.IS_NUMERIC_FUNCTION) {
          return true;
        } else if ((evalFlags & IAST.IS_NOT_NUMERIC_FUNCTION) == IAST.IS_NOT_NUMERIC_FUNCTION) {
          return false;
        }
      }
    }

    if (allowList) {
      if (isIntervalData()) {
        return true;
      }
      if (isNumericFunctionAST() || isList() || isIntervalData()) {
        // check if all arguments are &quot;numeric&quot;
        boolean forAll = forAll(x -> x.isNumericFunction(allowList), 1);
        if (forAll && !isList()) {
          forAll = hasExpectedArgSize(topHead());
        }
        addEvalFlags(
            forAll ? IAST.IS_NUMERIC_FUNCTION_OR_LIST : IAST.IS_NOT_NUMERIC_FUNCTION_OR_LIST);
        return forAll;
      }
    } else {
      if (isNumericFunctionAST()) {
        // check if all arguments are &quot;numeric&quot;
        boolean forAll = forAll(x -> x.isNumericFunction(allowList), 1);
        if (forAll) {
          forAll = hasExpectedArgSize(topHead());
        }
        addEvalFlags(forAll ? IAST.IS_NUMERIC_FUNCTION : IAST.IS_NOT_NUMERIC_FUNCTION);
        return forAll;
      }
    }
    if (isAST(S.Boole, 2) && arg1().isComparatorFunction()) {
      AbstractAST comparatorFunction = (AbstractAST) arg1();
      boolean forAll = comparatorFunction.forAll(x -> x.isNumericFunction(allowList), 1);
      if (forAll) {
        forAll = comparatorFunction.hasExpectedArgSize(comparatorFunction.topHead());
      }
      addEvalFlags(forAll ? IAST.IS_NUMERIC_FUNCTION : IAST.IS_NOT_NUMERIC_FUNCTION);
      return forAll;
    } else if (isPiecewise() != null) {
      VariablesSet varSet = new VariablesSet(this);
      return varSet.size() == 0;
    }

    return false;
  }

  private boolean hasExpectedArgSize(final ISymbol header) {
    if (header.isBuiltInSymbol()) {
      final IEvaluator evaluator = ((IBuiltInSymbol) header).getEvaluator();
      if (evaluator instanceof IFunctionEvaluator) {
        int[] expected = ((IFunctionEvaluator) evaluator).expectedArgSize(this);
        if (expected != null) {
          int argSize = argSize();
          if (argSize < expected[0] || argSize > expected[1]
              || (expected[1] == Integer.MAX_VALUE && expected.length == 2)) {
            if ((argSize < expected[0]) || (argSize > expected[1])) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericFunction(VariablesSet varSet) {
    if (head().isSymbol() && ((ISymbol) head()).hasNumericFunctionAttribute() || isList()) {
      // check if all arguments are &quot;numeric&quot;
      return forAll(x -> x.isNumericFunction(varSet));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericFunction(IExpr expr) {
    if (head().isSymbol() && ((ISymbol) head()).hasNumericFunctionAttribute() || isList()) {
      // check if all arguments are &quot;numeric&quot;
      return forAll(x -> x.isNumericFunction(expr));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericFunction(Function<IExpr, String> list) {
    if (head().isSymbol() && ((ISymbol) head()).hasNumericFunctionAttribute() || isList()
        || list.apply(this) != null) {
      // check if all arguments are &quot;numeric&quot;
      return forAll(x -> x.isNumericFunction(list));
    }
    return IASTMutable.super.isNumericFunction(list);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericMode() {
    ISymbol symbol = topHead();
    if (isList() || symbol.hasNumericFunctionAttribute()) {
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
    return isAST(S.Optional, 2, 3);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isOr() {
    return isSameHeadSizeGE(S.Or, 3);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isOrderlessAST() {
    final IExpr head = head();
    return head.isSymbol() ? ((ISymbol) head).hasOrderlessAttribute() : false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOverflow() {
    return isSameHead(S.Overflow, 1);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericFunctionAST() {
    final IExpr head = head();
    return head.isSymbol() ? ((ISymbol) head).hasNumericFunctionAttribute() : false;
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
    return isAST(S.PatternTest, 3);
  }

  /** {@inheritDoc} */
  @Override
  public int[] isPiecewise() {
    if (isSameHead(S.Piecewise, 2, 3) && arg1().isList()) {
      int[] result = arg1().isMatrix(false);
      if (result != null && (result[0] <= 0 || result[1] != 2)) {
        return null;
      }
      return result;
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPlus() {
    return head() == S.Plus && 3 <= size();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPlus2() {
    return head() == S.Plus && 3 == size();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPlus3() {
    return head() == S.Plus && 4 == size();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPlusTimesPower() {
    final IExpr h = head();
    if (h instanceof IBuiltInSymbol) {
      if (4 <= size()) {
        return h == S.Plus || h == S.Times;
      }
      if (3 == size()) {
        return h == S.Plus || h == S.Times || h == S.Power;
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPolynomial(IAST variables) {
    if (variables.isAST0()) {
      return true;
    }

    if (isPower() || isTimes() || isPlus()) {
      if (isPower()) {
        if (variables.indexOf(this) > 0) {
          return true;
        }
        IExpr exponent = exponent();
        if (exponent.isRational() && exponent.isPositive()) {
          return base().isPolynomial(variables);
        }
        if (exponent.isNegative()) {
          return base().isFree(x -> variables.indexOf(x) > 0, false);
        }
      } else if (isTimes() || isPlus()) {
        IAST ast = this;
        for (int i = 1; i < ast.size(); i++) {
          IExpr arg = ast.get(i);
          if (!arg.isPolynomial(variables)) {
            return false;
          }
        }
        return true;
      }
      IExpr expr = F.evalExpand(this);
      ExprPolynomialRing ring = new ExprPolynomialRing(variables);
      return ring.isPolynomial(expr);
    } else if (isFree(x -> variables.indexOf(x) > 0, false)) {
      return true;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPolynomial(IExpr variable) {
    return isPolynomial(F.list(variable));
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPolynomialStruct() {
    if ((isBuiltInFunction() && !((ISymbol) head()).hasNumericFunctionAttribute())
        || exists(x -> !x.isPolynomialStruct())) {
      return false;
    }
    return true;
  }

  public final boolean isPolynomialOfMaxDegree(IAST variables, long maxDegree) {
    try {
      if (isPlus() || isTimes() || isPower()) {
        IExpr expr = F.evalExpandAll(this);
        ExprPolynomialRing ring = new ExprPolynomialRing(variables);
        ExprPolynomial poly = ring.create(expr);
        return poly.degree() <= maxDegree;
      }
    } catch (ArithmeticException | ClassCastException | JASConversionException ex) {
      //
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPolynomialOfMaxDegree(IExpr variable, long maxDegree) {
    return isPolynomialOfMaxDegree(F.list(variable), maxDegree);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPositive() {
    if (isNumericFunction(true)) {
      IExpr result = EvalEngine.get().evalNumericFunction(this, false);
      if (result.isReal()) {
        return ((IReal) result).isPositive();
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
    if (isIntervalData() && size() > 1) {
      return IntervalDataSym.isPositiveResult(this);
    }
    return AbstractAssumptions.isPositiveResult(this);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPower() {
    return isSameHead(S.Power, 3);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPureFunction() {
    return size() == 2 && S.Function == head();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isRationalResult() {
    ISymbol symbol = topHead();
    if (symbol.equals(S.Floor) || symbol.equals(S.Ceiling) || symbol.equals(S.IntegerPart)) {
      return true;
    }
    if (isPowerInteger() && exponent().isPositive()) {
      if (arg1().isRationalResult()) {
        return true;
      }
      return false;
    }
    if (isPlus() || isTimes() || symbol.equals(S.Binomial) || symbol.equals(S.Factorial)) {
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
  public boolean isRe() {
    return isSameHead(S.Re, 2);
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
    if (S.True.equals(AbstractAssumptions.assumeReal(this))) {
      return true;
    }

    if (size() == 2 && isBuiltInFunction()) {
      final IExpr arg1 = arg1();
      final int id = headID();
      if (id > 0) {
        if (isFunctionID(ID.Cos, ID.Cosh, ID.Cot, ID.Coth, ID.Csc, ID.Csch, ID.Sec, ID.Sech, ID.Sin,
            ID.Sinh, ID.Tan, ID.Tanh, ID.Erf, ID.Erfc, ID.Erfi, ID.ExpIntegralEi, ID.Gamma,
            ID.Identity)) {
          return arg1.isRealResult();
        }
        if (isFunctionID(ID.Re, ID.Im, ID.Abs, ID.Arg, ID.RealSign)) {
          return true;
        }
        if (isFunctionID(ID.Log, ID.LogGamma) || isFunctionID(ID.ProductLog)) {
          // TODO improve for arg1 >= (-1/E)
          return arg1.isPositiveResult();
        }
      }
    }
    INumber e = evalNumber();
    if (e != null) {
      if (e.isReal()) {
        return true;
      }
      return false;
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
      final IExpr base = base();
      if (!base.isRealResult() || base.isNegativeResult() || !exponent().isRealResult()) {
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
      final int size = size();
      int i = 1;
      while (i < size) {
        if (get(i).isReal()) {
          if (get(i) instanceof INum) {
            if (!(get(i) instanceof Num)) {
              return false;
            }
            i++;
            while (i < size) {
              if (!get(i).isReal()) {
                return false;
              }
              i++;
            }
            return true;
          }
          i++;
        } else {
          return false;
        }
      }
    }
    return false;
  }

  @Override
  public boolean isRelational() {
    final int size = size();
    switch (size) {
      case 3:
        return isFunctionID(ID.Equal, ID.Unequal, ID.Greater, ID.GreaterEqual, ID.Less,
            ID.LessEqual, ID.Inequality);
      case 4:
        return isFunctionID(ID.Greater, ID.GreaterEqual, ID.Less, ID.LessEqual);
      case 6:
        return isFunctionID(ID.Inequality);
      default:
        return false;
    }
  }

  @Override
  public boolean isRelationalBinary() {
    if (size() == 3) {
      return isFunctionID(ID.Equal, ID.Unequal, ID.Greater, ID.GreaterEqual, ID.Less, ID.LessEqual);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRule() {
    return S.Rule == head() && size() == 3;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRuleAST() {
    return (S.Rule == head() || S.RuleDelayed == head()) && size() == 3;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRuleDelayed() {
    return S.RuleDelayed == head() && size() == 3;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSame(IExpr expression) {
    return equals(expression);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSame(IExpr expression, double epsilon) {
    return equals(expression);
  }

  /**
   * Check if the object at index 0 (i.e. the head of the list) is the same object as <code>head
   * </code>
   *
   * @param head object to compare with element at location <code>0</code>
   * @return
   */
  public boolean isSameHead(ISymbol head) {
    return head() == head;
  }

  /**
   * Check if the object at index 0 (i.e. the head of the list) is the same object as <code>head
   * </code> and if the size of the list equals <code>length</code>.
   *
   * @param head object to compare with element at location <code>0</code>
   * @param length
   * @return
   */
  public boolean isSameHead(ISymbol head, int length) {
    return head() == head && length == size();
  }

  /**
   * Check if the object at index 0 (i.e. the head of the list) is the same object as <code>head
   * </code> and if the size of the list is between <code>minLength</code> and <code>maxLength
   * </code>.
   *
   * @param head object to compare with element at location <code>0</code>
   * @param minLength minimum length of list elements.
   * @param maxLength maximum length of list elements.
   * @return
   */
  public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
    int size = size();
    return Objects.equals(head(), head) && minLength <= size && maxLength >= size;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSequence() {
    return head() == S.Sequence;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isSin() {
    return isSameHead(S.Sin, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isSinh() {
    return isSameHead(S.Sinh, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isSlot() {
    return isSameHead(S.Slot, 2) && (arg1().isInteger() || arg1().isString());
  }

  /** {@inheritDoc} */
  @Override
  public final IInteger intSlot() {
    if (isSameHead(S.Slot, 2) && arg1().isInteger()) {
      return ((IInteger) arg1());
    }
    return F.CN1;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isSlotSequence() {
    return isSameHead(S.SlotSequence, 2) && arg1().isInteger();
  }

  /** {@inheritDoc} */
  @Override
  public final int[] isSpan(int size) {
    int[] result = null;
    if (isSameHead(S.Span, 3, 4)) {
      int step = 1;
      if (isAST3()) {
        step = Validate.checkIntType(this, 3, Config.INVALID_INT);
      }
      int index1 = Validate.checkIntType(this, 1, Config.INVALID_INT);
      int index2;
      if (arg2().equals(S.All)) {
        index2 = size - 1;
        if (step < 0) {
          int tempIndx = index1;
          index1 = index2;
          index2 = tempIndx;
        }
      } else {
        index2 = Validate.checkIntType(this, 2, Config.INVALID_INT);
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
  public final boolean isSubscript() {
    return isSameHead(S.Subscript, 3) && arg1().isVariable();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isTan() {
    return isSameHead(S.Tan, 2);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isTanh() {
    return isSameHead(S.Tanh, 2);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isTimes() {
    return head() == S.Times && 3 <= size();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isTimes2() {
    return head() == S.Times && 3 == size();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isTimes3() {
    return head() == S.Times && 4 == size();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isTrigFunction() {
    if (size() == 2) {
      return isFunctionID(ID.Cos, ID.ArcCos, ID.Cot, ID.ArcCot, ID.Csc, ID.ArcCsc, ID.Sec,
          ID.ArcSec, ID.Sin, ID.ArcSin, ID.Tan, ID.ArcTan);
    } else if (size() == 3) {
      return isFunctionID(ID.ArcTan);
    }
    return false;
  }

  private static boolean isTrigonometricFunction(IExpr x) {
    if (x.isAST1()) {
      return x.isFunctionID(ID.ArcCos, ID.ArcCsc, ID.ArcCot, ID.ArcSec, ID.ArcSin, ID.ArcTan,
          ID.Cos, ID.Cosh, ID.Cot, ID.Coth, ID.Csc, ID.Csch, ID.Haversine, ID.InverseHaversine,
          ID.Sec, ID.Sech, ID.Sin, ID.Sinh, ID.Sinc, ID.Tan, ID.Tanh);
    }
    if (x.isAST2()) {
      return x.head() == S.ArcTan;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isHyperbolicFunction() {
    if (size() == 2) {
      return isFunctionID(ID.ArcCosh, ID.ArcCoth, ID.ArcCsch, ID.ArcSech, ID.ArcSinh, ID.ArcTanh,
          ID.Cosh, ID.Coth, ID.Csch, ID.Sech, ID.Sinh, ID.Tanh);
    }
    return false;
  }

  @Override
  public final boolean isPatternMatchingFunction() {
    if (size() >= 2) {
      return isFunctionID(ID.Alternatives, ID.Association, ID.Complex, ID.Condition, ID.Except,
          ID.HoldPattern, ID.KeyValuePattern, ID.Literal, ID.Optional, ID.PatternTest, ID.Rational,
          ID.Repeated, ID.RepeatedNull, ID.Verbatim);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isUnderflow() {
    return isSameHead(S.Underflow, 1);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isUnevaluated() {
    return isSameHead(S.Unevaluated, 2);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isUnit() {
    if (isZero()) {
      return false;
    }
    if (isNumber() || isConstantAttribute()) {
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
  public final boolean isVariable(boolean polynomialQTest) {
    if (headID() >= 0) {
      if (polynomialQTest) {
        // see message General::ivar
        if (isPower()) {
          if (exponent().isInteger() && base().isPlusTimesPower()) {
            return false;
          }
          return true;
        }
        return !isPlusTimesPower();
      }
      return isSlot() || isSubscript();
    }
    if (!head().isSymbol()) {
      return false;
    }
    for (int i = 1; i < size(); i++) {
      IExpr arg = get(i);
      if (!arg.isVariable()) {
        return false;
      }
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public int isVector() {
    if (isEvalFlagOn(IAST.IS_VECTOR)) {
      return argSize();
    }
    if (isList()) {
      final int length = argSize();
      if (length > 0) {
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
      return length;
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isIm() {
    return isSameHead(S.Im, 2);
  }

  /** {@inheritDoc} */
  @Override
  public int isInexactVector() {
    int result = isVector();
    if (result >= 0) {
      if (exists((Predicate<? super IExpr>) IExpr::isInexactNumber)) {
        return result;
      }
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericAST() {
    return exists((Predicate<? super IExpr>) IExpr::isInexactNumber);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isWith() {
    return head() == S.With && size() >= 3;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPossibleZero(boolean fastTest, double tolerance) {
    return PredicateQ.isPossibleZeroQ(this, fastTest, tolerance, EvalEngine.get());
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
    if (size() < 2) {
      return F.NIL;
    }
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

  @Override
  public final int lastIndexOf(Predicate<? super IExpr> test) {
    int size = size();
    for (int i = size - 1; i >= 0; i--) {
      if (test.test(get(i))) {
        return i;
      }
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public final long leafCount() {
    return accept(StructureFunctions.leafCountVisitor());
  }

  /**
   * {@inheritDoc}
   *
   * <p>
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
        return new IExpr[] {plusClone.oneIdentity0(), subLinear[1]};
      }
      return new IExpr[] {plusClone.oneIdentity0(), F.C0};
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
      return new IExpr[] {F.C0, timesClone.oneIdentity1()};
    } else if (this.equals(variable)) {
      return new IExpr[] {F.C0, F.C1};
    } else if (isFree(variable, true)) {
      return new IExpr[] {this, F.C0};
    }
    return null;
  }

  @Override
  public IExpr[] linearPower(IExpr variable) {

    if (isPlus()) {
      return linearPowerPlus(variable);
    } else if (isTimes()) {
      return linearPowerTimes(variable);
    } else if (isPower() && base().equals(variable) && exponent().isInteger()) {
      return new IExpr[] {F.C0, F.C1, exponent()};
    } else if (this.equals(variable)) {
      return new IExpr[] {F.C0, F.C1, F.C1};
    } else if (isFree(variable, true)) {
      return new IExpr[] {this, F.C0, F.C1};
    }
    return null;
  }

  /**
   * Helper method for {@link #linearPower(IExpr)}. Assumes that {@link IExpr#isPlus()} returns true
   * for this {@link IAST}.
   *
   * @param variable the variable <code>x</code> in the formula
   * @return
   */
  private IExpr[] linearPowerPlus(IExpr variable) {
    // a + b + c....
    int size = size();
    int counter = 0;
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
      return new IExpr[] {plusClone.oneIdentity0(), subLinear[1], subLinear[2]};
    }
    return new IExpr[] {plusClone.oneIdentity0(), F.C0, F.C1};
  }

  /**
   * Helper method for {@link #linearPower(IExpr)}. Assumes that {@link IExpr#isTimes()} returns
   * true for this {@link IAST}.
   *
   * @param variable the variable <code>x</code> in the formula
   * @return
   */
  private IExpr[] linearPowerTimes(IExpr variable) {
    // a * b * c....
    IInteger exp = F.C1;
    int size = size();
    int counter = 0;
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
    return new IExpr[] {F.C0, timesClone.oneIdentity1(), exp};
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
  public IExpr getArg(int position, IExpr defaultValue) {
    return argSize() >= position ? get(position) : defaultValue;
  }

  /** {@inheritDoc} */
  @Override
  public IAST makeList() {
    return isList() ? this : F.List(this);
  }

  @Override
  public IAST makeAST(IExpr head) {
    return isAST(head) ? this : F.unaryAST1(head, this);
  }

  /** {@inheritDoc} */
  @Override
  public IAST map(final Function<IExpr, ? extends IExpr> function) {
    return map(function, 1);
  }

  /** {@inheritDoc} */
  @Override
  public IAST map(final Function<IExpr, ? extends IExpr> function, final int startOffset) {
    IExpr temp;
    IASTMutable result = F.NIL;
    int i = startOffset;
    int size = size();
    while (i < size) {
      temp = function.apply(get(i));
      if (temp.isPresent()) {
        // something was evaluated - return a new IAST:
        if (i > 1) {
          result = copy();
        } else {
          result = copyAppendable();
        }
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
    return result.orElse(this);
  }

  /** {@inheritDoc} */
  @Override
  public IAST mapLeaf(IExpr testHead, final Function<IExpr, IExpr> function,
      final int startOffset) {
    IExpr temp = F.NIL;
    IASTMutable result = F.NIL;
    int i = startOffset;
    int size = size();

    while (i < size) {
      IExpr arg = get(i);
      if (arg.isAST(testHead)) {
        temp = ((IAST) arg).mapLeaf(testHead, function, startOffset);
        if (temp.isPresent()) {
          break;
        }
      } else {
        temp = function.apply(arg);
        if (temp.isPresent()) {
          break;
        }
      }
      i++;
    }
    if (temp.isPresent()) {
      // something was evaluated - return a new IAST:
      if (i > 0) {
        result = copy();
      } else {
        result = copyAppendable();
      }
      result.set(i++, temp);
    }
    if (result.isPresent()) {
      while (i < size) {
        IExpr arg = get(i);
        if (arg.isAST(testHead)) {
          temp = ((IAST) arg).mapLeaf(testHead, function, startOffset);
        } else {
          temp = function.apply(arg);
        }
        if (temp.isPresent()) {
          result.set(i, temp);
        }
        i++;
      }
    }
    return result.orElse(this);
  }

  @Override
  public IAST mapReverse(final Function<IExpr, IExpr> function) {
    IASTMutable result = copy();
    final int size = size();
    for (int i = 1; i < size; i++) {
      final IExpr arg = get(i);
      final IExpr value = function.apply(arg);
      result.set(size - i, value.orElse(arg));
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public final IAST map(IASTAppendable resultAST, IAST secondAST,
      BiFunction<IExpr, IExpr, IExpr> function) {
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
  @Override
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

  /** {@inheritDoc} */
  @Override
  public IAST mapLeft(IASTAppendable list, BiFunction<IExpr, IExpr, IExpr> binaryFunction,
      IExpr leftArg) {
    for (int i = 1; i < size(); i++) {
      IExpr functionResult = binaryFunction.apply(leftArg, get(i));
      if (functionResult.isPresent()) {
        list.append(functionResult);
      } else {
        return F.NIL;
      }
    }
    return list;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr mapMatrixColumns(int[] dim, Function<IExpr, IExpr> f) {
    final int rowSize = size();
    int columnSize = dim[1] + 1;
    return F.mapRange(1, columnSize, j -> {
      IASTAppendable row = F.mapRange(1, rowSize, i -> getPart(i, j));
      return f.apply(row);
    });
  }

  /** {@inheritDoc} */
  @Override
  public IAST mapRight(IASTAppendable list, BiFunction<IExpr, IExpr, IExpr> binaryFunction,
      IExpr rightArg) {
    for (int i = 1; i < size(); i++) {
      IExpr functionResult = binaryFunction.apply(get(i), rightArg);
      if (functionResult.isPresent()) {
        list.append(functionResult);
      } else {
        return F.NIL;
      }
    }
    return list;
  }

  /** {@inheritDoc} */
  @Override
  public IASTMutable mapThread(final IAST replacement, int position) {
    final Function<IExpr, IExpr> function = x -> replacement.setAtCopy(position, x);
    return (IASTMutable) map(function, 1);
  }

  /** {@inheritDoc} */
  @Override
  public final IASTMutable mapThreadEvaled(EvalEngine engine, final IAST replacement,
      int position) {
    final Function<IExpr, IExpr> function =
        x -> engine.evaluate(replacement.setAtCopy(position, x));
    return (IASTMutable) map(function, 1);
  }

  /** {@inheritDoc} */
  @Override
  public final IAST mapThread(Function<IExpr, IExpr> function) {
    // final Function<IExpr, IExpr> function = x -> replacement.setAtCopy(position, x );
    return map(function, 1);
  }

  /** {@inheritDoc} */
  @Override
  public final IASTMutable mapThread(IAST that, BiFunction<IExpr, IExpr, IExpr> function) {
    int size = size();
    if (that.size() < size()) {
      size = that.size();
    }
    if (size > 0) {
      IASTAppendable result = copyHead(size - 1);
      for (int i = 1; i < size; i++) {
        result.append(function.apply(get(i), that.get(i)));
      }
      return result;
    }
    return copyHead();
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable mapThreadEvaled(EvalEngine engine, IASTAppendable appendAST,
      final IAST replacement, int position) {
    final Function<IExpr, IExpr> function =
        x -> engine.evaluate(replacement.setAtCopy(position, x));

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
   * Additional <code>negative</code> method, which works like opposite to fulfill groovy's method
   * signature
   *
   * @return
   */
  @Override
  public final IExpr negative() {
    return opposite();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr normal(boolean nilIfUnevaluated) {
    if (isConditionalExpression()) {
      return arg1();
    }
    IExpr temp = map(x -> x.normal(nilIfUnevaluated));
    if (temp.isPresent() && temp != this) {
      return temp;
    }
    return nilIfUnevaluated ? F.NIL : this;
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
    final int lhsOrdinal =
        (head() instanceof IBuiltInSymbol) ? ((IBuiltInSymbol) head()).ordinal() : -1;
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
          return setAtCopy(1, arg1.negate());
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
        return map((Function<IExpr, ? extends IExpr>) IExpr::negate, 1);
      }
    }
    return F.Times(F.CN1, this);
    // return F.eval(F.Times(F.CN1, this));
  }

  @Override
  public IExpr optional() {
    Short id = S.GLOBAL_IDS_MAP.get(this);
    if (id != null) {
      return new ExprID(id);
    }
    return this;
  }

  @Override
  public IAST orElse(final IAST other) {
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public final IAST partition(ISymbol operator, Predicate<? super IExpr> predicate, IExpr init1,
      IExpr init2, ISymbol combiner, ISymbol action) {
    if (Objects.equals(head(), operator)) {
      int newSize = F.allocMax64(this);
      IASTAppendable result = F.ast(action, 3);
      final int size = size();
      IASTAppendable yesAST = F.ast(combiner, newSize);
      IASTAppendable noAST = F.ast(combiner, newSize);
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
  public final IAST partitionPlus(Predicate<? super IExpr> predicate, IExpr initYes, IExpr initNo,
      ISymbol action) {
    return partition(S.Plus, predicate, initYes, initNo, S.Plus, S.List);
  }

  @Override
  public final IAST partitionTimes(Predicate<? super IExpr> predicate, IExpr initYes, IExpr initNo,
      ISymbol action) {
    return partition(S.Times, predicate, initYes, initNo, S.Times, S.List);
  }

  /** Calculate a special hash value to find a matching rule in a hash table */
  @Override
  public final int patternHashCode() {
    if (size() > 1) {
      final int attr = topHead().getAttributes() & ISymbol.FLATORDERLESS;
      if (attr != ISymbol.NOATTRIBUTE) {
        if (ISymbol.hasOrderlessFlatAttribute(attr)) {
          return 17 * head().hashCode();
        } else if (ISymbol.hasFlatAttribute(attr)) {
          if (arg1() instanceof IAST) {
            return 31 * head().hashCode() + arg1().head().hashCode();
          }
          return 37 * head().hashCode() + arg1().hashCode();
        }
        return 17 * head().hashCode() + size();
      }
      if (arg1().isPresent()) {
        if (arg1() instanceof IAST) {
          IAST ast1 = (IAST) arg1();
          return 31 * head().hashCode() + ast1.head().hashCode() + size();
        }
        return 37 * head().hashCode() + arg1().hashCode() + size();
      }
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
   * Associates the specified value with the specified property in the associated <code>
   * EnumMap&lt;PROPERTY, Object&gt;</code> map. If the map previously contained a mapping for this
   * key, the old value is replaced.
   *
   * @param key
   * @param value
   * @return
   * @see #getProperty(PROPERTY)
   */
  @Override
  public Object putProperty(PROPERTY key, Object value) {
    Cache<IAST, EnumMap<PROPERTY, Object>> propertyCache = propertyCache();
    EnumMap<PROPERTY, Object> map = propertyCache.getIfPresent(this);
    if (map == null) {
      map = new EnumMap<>(PROPERTY.class);
      propertyCache.put(this, map);
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
    for (IExpr item : items) {
      ast.append(index++, item);
    }
    return ast;
  }

  /** {@inheritDoc} */
  @Override
  public IASTMutable removeAtCopy(int position) {
    int size = size();
    if (position < size()) {
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

  /** {@inheritDoc} */
  @Override
  public IASTAppendable removePositionsAtCopy(int[] removedPositions, int untilIndex) {
    // if (untilIndex == 1) {
    // return removeAtCopy(removedPositions[0]);
    // }
    IASTAppendable result = copyAppendable();
    for (int j = untilIndex - 1; j >= 0; j--) {
      result.remove(removedPositions[j]);
    }
    return result;
  }

  @Override
  public IAST removePositionsAtCopy(Predicate<IExpr> predicate) {
    int[] removedPositions = new int[size()];
    int untilIndex = 0;
    for (int i = 1; i < size(); i++) {
      if (predicate.test(getRule(i))) {
        removedPositions[untilIndex++] = i;
      }
    }
    if (untilIndex > 0) {
      return removePositionsAtCopy(removedPositions, untilIndex);
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable replaceSubset(int[] replacePosition, IExpr[] newEntries,
      int[] removePositions) {
    IASTAppendable result = copyAppendable();
    for (int j = 0; j < replacePosition.length; j++) {
      int position = replacePosition[j];
      if (position <= 0) {
        break;
      }
      result.set(position, newEntries[j]);
    }
    for (int j = removePositions.length - 1; j >= 0; j--) {
      int position = removePositions[j];
      if (position <= 0) {
        break;
      }
      result.remove(position);
    }
    return result;
  }

  /**
   * Append the elements in reversed order to the given <code>resultList</code>
   *
   * @param resultList if {@link F.NIL} create a new {@link IASTAppendable} list inside the method
   * @return
   */
  @Override
  public IASTAppendable reverse(IASTAppendable resultList) {
    if (resultList.isNIL()) {
      resultList = F.ListAlloc(argSize());
    }
    for (int i = argSize(); i >= 1; i--) {
      resultList.append(get(i));
    }
    return resultList;
  }

  @Override
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

  @Override
  public Optional<IEvaluator> isInstance(Class<?> clazz) {
    int headID = headID();
    if (headID > 0) {
      IEvaluator evaluator = ((IBuiltInSymbol) head()).getEvaluator();
      if (clazz.isInstance(evaluator)) {
        return Optional.of(evaluator);
      }
    }
    return Optional.empty();
  }

  /**
   * Rotate the ranges elements to the left by n places and append the resulting elements to the
   * <code>list</code>
   *
   * @param resultList
   * @param n
   * @return the given list
   */
  @Override
  public IAST rotateLeft(IASTAppendable resultList, final int n) {
    final int size = size();
    if (resultList.isNIL()) {
      resultList = F.ListAlloc(argSize());
    }
    int n1 = n + 1;
    for (int i = n1; i < size; i++) {
      resultList.append(get(i));
    }
    if (n <= size) {
      for (int i = 1; i < n1; i++) {
        resultList.append(get(i));
      }
    }
    return resultList;
  }

  /**
   * Rotate the ranges elements to the right by n places and append the resulting elements to the
   * <code>list</code>
   *
   * @param resultList
   * @param n
   * @return the given list
   */
  @Override
  public IAST rotateRight(IASTAppendable resultList, final int n) {
    final int size = size();
    if (resultList.isNIL()) {
      resultList = F.ListAlloc(size - 1);
    }
    if (n <= size()) {
      for (int i = size - n; i < size; i++) {
        resultList.append(get(i));
      }
      for (int i = 1; i < size - n; i++) {
        resultList.append(get(i));
      }
    }
    return resultList;
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable setAtClone(int position, IExpr expr) {
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
  public void sortInplace(Comparator<IExpr> comparator) {
    if (size() > 1) {
      final IExpr[] a = toArray();
      int end = a.length;
      if (Config.FUZZ_TESTING) {
        try {
          Arrays.sort(a, 1, size(), comparator);
          for (int j = 1; j < end; j++) {
            set(j, a[j]);
          }
        } catch (java.lang.IllegalArgumentException iae) {
          // java.util.TimSort.mergeHi(TimSort.java:899) - Comparison method violates its general
          // contract!
          throw iae;
        }
      } else {
        Arrays.sort(a, 1, size(), comparator);
        for (int j = 1; j < end; j++) {
          set(j, a[j]);
        }
      }
    }
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
  public double[][] toDoubleMatrix(boolean setMatrixFormat) {
    int[] dim = isMatrix(setMatrixFormat);
    if (dim == null) {
      return null;
    }
    try {
      double[][] result = new double[dim[0]][dim[1]];
      IReal realNumber;
      for (int i = 1; i <= dim[0]; i++) {
        IAST row = (IAST) get(i);
        for (int j = 1; j <= dim[1]; j++) {
          realNumber = row.get(j).evalReal();
          if (realNumber != null) {
            result[i - 1][j - 1] = realNumber.evalf();
          } else {
            return null;
          }
        }
      }
      return result;
    } catch (ArgumentTypeException rex) {
      //
    }
    return null;
  }

  @Override
  public boolean[] toBooleanVector() {
    boolean[] result = new boolean[argSize()];
    for (int i = 1; i < size(); i++) {
      IExpr temp = get(i);
      if (temp.isTrue()) {
        result[i - 1] = true;
      } else if (temp.isFalse()) {
        result[i - 1] = false;
      } else {
        return null;
      }
    }
    return result;
  }

  @Override
  public boolean[] toBooleValueVector() {
    boolean[] result = new boolean[argSize()];
    for (int i = 1; i < size(); i++) {
      IExpr temp = get(i);
      if (temp.equals(F.C1)) {
        result[i - 1] = true;
      } else if (temp.equals(F.C0)) {
        result[i - 1] = false;
      } else {
        return null;
      }
    }
    return result;
  }

  @Override
  public boolean[][] toBooleanMatrix() {
    int[] dim = isMatrix();
    if (dim == null) {
      return null;
    }
    boolean[][] result = new boolean[dim[0]][dim[1]];
    for (int i = 1; i <= dim[0]; i++) {
      IAST row = (IAST) get(i);
      for (int j = 1; j <= dim[1]; j++) {
        IExpr temp = row.get(j);
        if (temp.isTrue()) {
          result[i - 1][j - 1] = true;
        } else if (temp.isFalse()) {
          result[i - 1][j - 1] = false;
        } else {
          return null;
        }
      }
    }
    return result;
  }

  @Override
  public byte[][] toByteMatrix() {
    int[] dim = isMatrix();
    if (dim == null) {
      return null;
    }
    byte[][] result = new byte[dim[0]][dim[1]];
    int n;
    for (int i = 1; i <= dim[0]; i++) {
      IAST row = (IAST) get(i);
      for (int j = 1; j <= dim[1]; j++) {
        n = row.get(j).toIntDefault();
        if (n >= 0 && n < 256) {
          result[i - 1][j - 1] = (byte) n;
        } else {
          return null;
        }
      }
    }
    return result;
  }

  @Override
  public int[][] toIntMatrix() {
    int[] dim = isMatrix();
    if (dim == null) {
      return null;
    }
    int[][] result = new int[dim[0]][dim[1]];
    int n;
    for (int i = 1; i <= dim[0]; i++) {
      IAST row = (IAST) get(i);
      for (int j = 1; j <= dim[1]; j++) {
        n = row.get(j).toIntDefault();
        if (F.isNotPresent(n)) {
          return null;
        }
        result[i - 1][j - 1] = n;
      }
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public double[][] toDoubleMatrixIgnore() {
    int[] dim = isMatrixIgnore();
    if (dim == null) {
      return null;
    }
    double[][] result = new double[dim[0]][dim[1]];
    int rowIndex = 0;
    IReal realNumber;
    for (int i = 1; i < size(); i++) {
      IExpr row = get(i);
      if (row.isList()) {
        IAST list = (IAST) row;
        for (int j = 1; j <= dim[1]; j++) {
          realNumber = list.get(j).evalReal();
          if (realNumber != null) {
            result[rowIndex][j - 1] = realNumber.doubleValue();
          } else {
            return null;
          }
        }
        rowIndex++;
      }
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public double[] toDoubleVector() {
    try {
      double[] result = new double[argSize()];
      for (int i = 1; i < size(); i++) {
        result[i - 1] = get(i).evalf();
      }
      return result;
    } catch (ArgumentTypeException rex) {

    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public double[] toDoubleVectorIgnore() {
    int i = 0;
    int j = 1;
    double[] temp = new double[argSize()];
    while (j < size()) {
      try {
        temp[i] = get(j).evalf();
        i++;
      } catch (ArgumentTypeException rex) {

      }
      j++;
    }
    if (i == 0) {
      return null;
    }
    if (i == j - 1) {
      return temp;
    }
    double[] result = new double[i];
    System.arraycopy(temp, 0, result, 0, i);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public Complex[] toComplexVector() {
    try {
      Complex[] result = new Complex[argSize()];
      for (int i = 1; i < size(); i++) {
        result[i - 1] = get(i).evalfc();
      }
      return result;
    } catch (ArgumentTypeException ex) {

    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public Complex[][] toComplexMatrix() {
    try {
      int[] dims = isMatrix(false);
      if (dims != null) {
        Complex[][] result = new Complex[dims[0]][dims[1]];
        for (int i = 0; i < dims[0]; i++) {
          IAST subList = (IAST) get(i + 1);
          for (int j = 0; j < dims[1]; j++) {
            result[i][j] = subList.get(j + 1).evalfc();
          }
        }
        return result;
      }
    } catch (ArgumentTypeException ex) {

    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public int[] toIntVector() {
    int[] result = new int[argSize()];
    for (int i = 1; i < size(); i++) {
      int value = get(i).toIntDefault();
      if (F.isNotPresent(value)) {
        return null;
      }
      result[i - 1] = value;
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
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
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
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
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
  public RealMatrix toRealMatrixIgnore() {
    final double[][] elements = toDoubleMatrixIgnore();
    if (elements != null) {
      return new Array2DRowRealMatrix(elements, false);
    }
    return null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    EvalEngine engine = EvalEngine.get();
    if (OutputFormFactory.get(engine.isRelaxedSyntax(), false, false,
        engine.getSignificantFigures() - 1, engine.getSignificantFigures() + 1).convert(sb, this)) {
      return sb.toString();
    }
    sb = null;

    final StringBuilder buf = new StringBuilder();
    if (size() > 0 && isListOrAssociation()) {
      if (isList()) {
        buf.append('{');
      } else {
        buf.append("<|");
      }
      for (int i = 1; i < size(); i++) {
        buf.append(getRule(i) == this ? "(this AST)" : String.valueOf(getRule(i)));
        if (i < argSize()) {
          buf.append(", ");
        }
      }
      if (isList()) {
        buf.append('}');
      } else {
        buf.append("|>");
      }
      return buf.toString();

    } else if (isAST(S.Slot, 2) && (arg1().isReal())) {

      final int slot = arg1().toIntDefault();
      if (slot <= 0) {
        return toFullFormString();
      }
      if (slot == 1) {
        return "#";
      }
      return "#" + slot;
    } else {
      return toFullFormString();
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
  public final IExpr variables2Slots(final Map<IExpr, IExpr> map,
      final Collection<IExpr> variableCollector) {
    return variables2Slots(this, Predicates.isUnaryVariableOrPattern(),
        new UnaryVariable2Slot(map, variableCollector));
  }

  /** {@inheritDoc} */
  @Override
  public IExpr extractConditionalExpression(boolean isUnaryConditionalExpression) {
    if (isUnaryConditionalExpression) {
      if (isFunctionID(ID.Denominator, ID.Numerator, ID.PossibleZeroQ)) {
        return F.NIL;
      }

      // mergeConditionalExpression
      IAST conditionalExpr = (IAST) arg1();
      IASTMutable copy = copy();
      copy.set(1, conditionalExpr.arg1());
      return conditionalExpr.setAtCopy(1, copy);
    }
    IExpr head = head();
    if (head.isSymbol()) {
      ISymbol symbol = (ISymbol) head;
      if (symbol.hasNumericFunctionAttribute() || symbol.isBooleanFormulaSymbol()
          || symbol.isComparatorFunctionSymbol()) {
        int indx = indexOf((Predicate<? super IExpr>) IExpr::isConditionalExpression);
        if (indx > 0) {
          IAST conditionalExpr = (IAST) get(indx);
          IASTAppendable andExpr = F.And();
          IASTMutable copy = copy();
          copy.set(indx, conditionalExpr.arg1());
          andExpr.append(conditionalExpr.arg2());
          indx++;
          for (int i = indx; i < copy.size(); i++) {
            IExpr x = copy.get(i);
            if (x.isConditionalExpression()) {
              conditionalExpr = (IAST) x;
              copy.set(i, conditionalExpr.arg1());
              andExpr.append(conditionalExpr.arg2());
            }
          }
          IExpr arg = copy;
          if (isTimes() && arg1().isNumber()) {
            arg = F.Expand(arg);
          }
          IASTMutable mergedResult = conditionalExpr.setAtCopy(1, arg);
          mergedResult.set(2, andExpr);
          return mergedResult;
        }
      }
    }

    return F.NIL;
  }

  @Override
  public IAST removeO() {
    IASTAppendable result = F.ast(head(), argSize() - 1);
    for (int i = 1; i < size(); i++) {
      IExpr expr = get(i);
      if (!expr.isAST(S.O)) {
        result.append(expr);
      }
    }
    return result;
  }

  @Override
  public IAST getO() {
    IASTAppendable result = F.ast(head(), 4);
    for (int i = 1; i < size(); i++) {
      IExpr expr = get(i);
      if (expr.isAST(S.O)) {
        result.append(expr);
      }
    }
    return result;
  }
}
