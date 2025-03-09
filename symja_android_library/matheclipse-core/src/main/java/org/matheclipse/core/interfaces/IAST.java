package org.matheclipse.core.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.hipparchus.linear.AnyMatrix;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.generic.ObjIntPredicate;
import org.matheclipse.core.visit.IVisitor;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * (I)nterface for the (A)bstract (S)yntax (T)ree of a given function.
 *
 * <p>
 * In Symja, an abstract syntax tree (AST), is a tree representation of the abstract syntactic
 * structure of the Symja source code. Each node of the tree denotes a construct occurring in the
 * source code. The syntax is 'abstract' in the sense that it does not represent every detail that
 * appears in the real syntax. For instance, grouping parentheses are implicit in the tree
 * structure, and a syntactic construct such as a <code>Sin(x)</code> expression will be denoted by
 * an AST with 2 nodes. One node for the header <code>Sin</code> and one node for the argument
 * <code>x</code>. Internally an AST is represented as a list which contains
 *
 * <ul>
 * <li>the operator of a function (i.e. the &quot;header&quot;-symbol: Sin, Cos, Inverse, Plus,
 * Times,...) at index <code>0</code> and
 * <li>the <code>n</code> arguments of a function in the index <code>1 to n</code>
 * </ul>
 *
 * See <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract syntax tree</a>,
 * <a href="https://en.wikipedia.org/wiki/Directed_acyclic_graph">Directed acyclic graph</a>
 */
public interface IAST extends IExpr, Iterable<IExpr>, ITensorAccess, AnyMatrix {

  /**
   * The enumeration for the properties (keys) of the map possibly associated with this <code>IAST
   * </code> object.
   */
  public enum PROPERTY {
    CSS, //
    EMPIRICAL_DISTRIBUTION; // org.hipparchus.stat.fitting.EmpiricalDistribution
  }

  /**
   * Is set, if the built-in function associated with this object was evaluated and no further
   * evaluation is needed for the built-in evaluation function.
   */
  public static final int BUILT_IN_EVALED = 0x00040000;

  /**
   * One of the arguments of the list contains a pattern object which can be set to a default value
   * (or optional value).
   */
  public static final int CONTAINS_DEFAULT_PATTERN = 0x0004;

  /**
   * All of the arguments of the list are pattern objects which can be set to a default value (or
   * optional value).
   */
  public static final int CONTAINS_ALL_DEFAULT_PATTERN = 0x10000000;

  /** Negative flag mask for CONTAINS_DEFAULT_PATTERN */
  public static final int CONTAINS_NO_DEFAULT_PATTERN_MASK = 0xFFFB;

  /** The list or the lists subexpressions contain no pattern object. */
  public static final int CONTAINS_NO_PATTERN = 0x0008;

  /**
   * Is set, if one of the (nested) arguments of a numeric function contains a numeric expression.
   */
  public static final int CONTAINS_NUMERIC_ARG = 0x00010000;

  /** The head or one of the arguments of the list or sublists contains a pattern object */
  public static final int CONTAINS_PATTERN = 0x0001;

  /**
   * One of the arguments of the list or sublists contains a pattern object. Combination of <code>
   * CONTAINS_PATTERN, CONTAINS_PATTERN_SEQUENCE, CONTAINS_DEFAULT_PATTERN</code>
   */
  public static final int CONTAINS_PATTERN_EXPR = 0x0007;

  /** The head or one of the arguments of the list or sublists contains a pattern object */
  public static final int CONTAINS_PATTERN_SEQUENCE = 0x0002;

  /**
   * Flag which will be set for new allocated IAST expressions during a capsulated traversal
   * algorithm. Temporary flag which should be deleted after traversing the expression
   */
  public static final int IS_COPIED = 0x20000000;

  /** This expression is already evaluated by ExpandAll() function */
  public static final int IS_ALL_EXPANDED = 0x2000;

  /**
   * This expression represents an already decomposed partial fraction expression.
   * <p>
   * See: {@link S#Apart}
   *
   */
  public static final int IS_DECOMPOSED_PARTIAL_FRACTION = 0x0080;

  /** This expression is already evaluated in the Derivative[] function */
  public static final int IS_DERIVATIVE_EVALED = 0x8000;

  /** This expression is already evaluated by Expand() function */
  public static final int IS_EXPANDED = 0x1000;

  /** This expression is an already evaled expression */
  public static final int IS_FLAT_ORDERLESS_EVALED = 0x0800;

  /** This expression is an already flattened expression */
  public static final int IS_FLATTENED = 0x0100;

  /** This expression is an already flattened or sorted expression */
  public static final int IS_FLATTENED_OR_SORTED_MASK = 0x0300;

  /** This expression is already evaluated by a HashedOrderlessMatcher function */
  public static final int IS_HASH_EVALED = 0x4000;

  /** This expression has already applied the Listable attribute to its argument expressions */
  public static final int IS_LISTABLE_THREADED = 0x0400;

  /** This expression represents a matrix */
  public static final int IS_MATRIX = 0x0020;

  /** This expression represents a matrix or vector if one of the following bits is set. */
  public static final int IS_MATRIX_OR_VECTOR = 0x0060;

  public static final int IS_NOT_NUMERIC_FUNCTION = 0x00800000;

  public static final int IS_NOT_NUMERIC_FUNCTION_OR_LIST = 0x02000000;

  public static final int IS_NUMERIC_FUNCTION = 0x00400000;

  public static final int IS_NUMERIC_FUNCTION_OR_LIST = 0x01000000;

  public static final int IS_NUMERIC_MASK = 0x03C00000;

  /**
   * This expression is an already sorted expression (i.e. sorted with the <code>Order()</code>
   * function)
   */
  public static final int IS_SORTED = 0x0200;

  /** This expression represents a vector */
  public static final int IS_VECTOR = 0x0040;

  /** NO_FLAG ACTIVATED */
  public static final int NO_FLAG = 0x0000;

  public static final int NUMERIC_ARBITRARY_EVALED = 0x08000000;

  public static final int NUMERIC_DOUBLE_EVALED = 0x04000000;

  /** This List expression args should be printed in multi-line style */
  public static final int OUTPUT_MULTILINE = 0x00100000;

  public static final int SEQUENCE_FLATTENED = 0x00080000;

  /** The <code>Times(...)</code> expression was determined implicitly in the expression parser. */
  public static final int TIMES_PARSED_IMPLICIT = 0x00200000;

  default IExpr acceptChecked(IVisitor visitor) {
    try {
      return accept(visitor);
    } catch (StackOverflowError soe) {
      RecursionLimitExceeded.throwIt(Integer.MAX_VALUE, this);
    }
    return F.NIL;
  }

  /**
   * Add an evaluation flag to the existing ones.
   *
   * @param i
   */
  @Override
  public IAST addEvalFlags(int i);

  /**
   * Create a shallow copy of this <code>IAST</code> instance (the elements themselves are not
   * copied) and add the <code>expr</code> at the given <code>position</code>.
   *
   * @param position
   * @param expr
   * @return a clone with added <code>expr</code> element at the given <code>position</code>.
   */
  public IASTAppendable appendAtClone(int position, IExpr expr);

  /**
   * Append an expression to this list.
   *
   * @param expr the expression which should be appended
   * @return <code>this</code> after appending the given expression.
   */
  public IASTAppendable appendClone(IExpr expr);

  /**
   * Apply the given <code>head</code> to this expression (i.e. create a shallow copy and replace
   * the old head with the given one). <code>F.List(a,b,c).apply(F.Max)</code> gives
   * <code>Max(a,b,c)</code>
   *
   * @param head
   * @return create a shallow copy with element 0 set to <code>head</code>
   */
  public IASTAppendable apply(IExpr head);

  /**
   * Apply the given <code>head</code> to this expression (i.e. create a sublist shallow copy
   * starting from index start and replacing the old head with the given one)
   *
   * @param head
   * @param start the start index
   * @return a sublist clone with element 0 set to <code>head</code>
   */
  public IAST apply(IExpr head, int start);

  /**
   * Apply the given <code>head</code> to this expression (i.e. create a sublist shallow copy from
   * index start to end, and replacing the old head with the given one)
   *
   * @param head
   * @param start the start index inclusive
   * @param end the end index exclusive
   * @return a clone with element set to <code>head</code> at the given <code>0</code>.
   */
  public IAST apply(IExpr head, int start, int end);

  /**
   * Get the first argument (i.e. the second element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(1) ). <br>
   * <b>Example:</b> for the AST representing the expression <code>Sin(x)</code>, <code>arg1()
   * </code> returns <code>x</code>.
   *
   * @return the first argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  public IExpr arg1();

  /**
   * Get the second argument (i.e. the third element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(2) ). <br>
   * <b>Example:</b> for the AST representing the expression <code>x^y</code> (i.e. <code>
   * Power(x, y)</code>), <code>arg2()</code> returns <code>y</code>.
   *
   * @return the second argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  public IExpr arg2();

  /**
   * Get the third argument (i.e. the fourth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(3) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b, c)</code>, <code>arg3()
   * </code> returns <code>c</code>.
   *
   * @return the third argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  public IExpr arg3();

  /**
   * Get the fourth argument (i.e. the fifth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(4) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d)</code>, <code>
   * arg4()</code> returns <code>d</code>.
   *
   * @return the fourth argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  public IExpr arg4();

  /**
   * Get the fifth argument (i.e. the sixth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(5) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d, e)</code>, <code>
   * arg5()</code> returns <code>e</code> .
   *
   * @return the fifth argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  public IExpr arg5();

  /** {@inheritDoc} */
  @Override
  default int argSize() {
    return size() - 1;
  }

  /**
   * Converts the elements of this AST into a SortedSet.
   * 
   * @return a SortedSet containing the elements of this AST
   */
  default SortedSet<IExpr> asSortedSet() {
    return asSortedSet(Comparators.CANONICAL_COMPARATOR);
  }

  /**
   * Converts the elements of this AST into a SortedSet.
   * 
   * @param comparator the Comparator to be used for sorting the elements in the set
   * @return a SortedSet containing the elements of this AST, sorted according to the provided
   *         Comparator
   */
  public SortedSet<IExpr> asSortedSet(Comparator<? super IExpr> comparator);

  /**
   * Call <code>setEvalFlags(IAST.BUILT_IN_EVALED)</code>
   */
  public default void builtinEvaled() {
    setEvalFlags(IAST.BUILT_IN_EVALED);
  }

  /** Set the cached hash value to zero. */
  public void clearHashCache();

  /**
   * Compare all adjacent elements from lowest to highest index and return true, if the binary
   * predicate gives true in each step. If <code>size &lt; 2</code> the method returns
   * <code>false</code>.
   * 
   * @param predicate the binary predicate
   * @return <code>true</code>, if the binary predicate gives true in each step
   */
  public boolean compareAdjacent(BiPredicate<IExpr, IExpr> predicate);

  /**
   * Tests whether this {@code List} contains the specified object.
   *
   * @param object the object to search for.
   * @return {@code true} if object is an element of this {@code List}, {@code false} otherwise
   */
  public boolean contains(Object object);

  /**
   * Tests whether this {@code Collection} contains all objects contained in the specified {@code
   * Collection}. This implementation iterates over the specified {@code Collection}. If one element
   * returned by the iterator is not contained in this {@code Collection}, then {@code false} is
   * returned; {@code true} otherwise.
   *
   * @param collection the collection of objects.
   * @return {@code true} if all objects in the specified {@code Collection} are elements of this
   *         {@code Collection}, {@code false} otherwise.
   * @throws ClassCastException if one or more elements of {@code collection} isn't of the correct
   *         type.
   * @throws NullPointerException if {@code collection} contains at least one {@code null} element
   *         and this {@code Collection} doesn't support {@code null} elements.
   * @throws NullPointerException if {@code collection} is {@code null}.
   */
  public default boolean containsAll(Collection<?> collection) {
    Iterator<?> it = collection.iterator();
    while (it.hasNext()) {
      if (!contains(it.next())) {
        return false;
      }
    }
    return true;
  }

  /**
   * <p>
   * Returns a shallow copy of this <code>IAST</code> instance (the elements themselves are not
   * copied). In contrast to the {@link #copyAppendable()} method, this method returns exactly the
   * same type for <code>AST0, AST1, AST2, AST3, ASTRealVector, ASTRealMatrix</code> and tries to
   * transform {@link IAST} objects to
   * <code>AST0, AST1, AST2, AST3, ASTRealVector, ASTRealMatrix</code> if possible.
   * <p>
   * Because it's not allowed to set the header object (offset 0) to an arbitrary expression after a
   * <code>copy()</code>, this method should only be used if the arguments (offset 1..argSize)
   * should be set to new expressions.
   *
   * @return a copy of this <code>IAST</code> instance.
   */
  @Override
  public IASTMutable copy();

  /**
   * <p>
   * Returns a shallow copy of this <code>IAST</code> instance (the elements themselves are not
   * copied). In contrast to the {@link #copy()} method, this method doesn't return exactly the same
   * type for a given <code>AST0, AST1, AST2, AST3, ASTRealVector, ASTRealMatrix,...</code> object
   * but transforms it into a new {@link AST} object, so that additional arguments could be appended
   * at the end.
   * <p>
   * This also allows to set the header object to an arbitrary expression.
   *
   * @return a copy of this <code>IAST</code> instance.
   */
  public IASTAppendable copyAppendable();

  /**
   * <p>
   * Returns a shallow copy of this <code>IAST</code> instance (the elements themselves are not
   * copied). In contrast to the {@link #copy()} method, this method doesn't return exactly the same
   * type for a given <code>AST0, AST1, AST2, AST3...</code> object but transforms it into a new
   * <code>AST</code> object, so that additional arguments could be appended at the end.
   * <p>
   * This also allows to set the header object to an arbitrary expression.
   *
   * @return a copy of this <code>IAST</code> instance.
   */
  public IASTAppendable copyAppendable(int additionalCapacity);

  /**
   * <p>
   * Return a copy of the pure {@link IAST} instance (the elements themselves are not copied).
   * Additionally to the <code>copy()</code> method, if this is a {@link IAssociation} the values of
   * the rules are copied.
   * <p>
   * Because it's not allowed to set the header object (offset 0) to an arbitrary expression after a
   * <code>copy()</code>, this method should only be used if the arguments (offset 1..argSize)
   * should be set to new expressions.
   *
   * @return a copy of this <code>IAST</code> instance.
   */
  default IASTMutable copyAST() {
    return copy();
  }

  /**
   * <p>
   * Create a copy of this <code>AST</code>, which contains the same head and all elements from the
   * given <code>position</code> (inclusive).
   * <p>
   * 
   * @param position
   * @return a copy of this <code>AST</code> instance from the given <code>position</code>
   *         (inclusive)
   * @deprecated use {@link #subList(int)} instead
   */
  @Deprecated
  public IASTAppendable copyFrom(int position);

  default IASTAppendable subList(int startPosition) {
    return copyFrom(startPosition);
  }

  /**
   * <p>
   * Create a copy of this <code>AST</code>, which contains the same head and all elements from the
   * given <code>startPosition</code> (inclusive) to the <code>endPosition</code> (exclusive)
   * <p>
   * 
   * @param startPosition the position to start copying the elements (inclusive)
   * @param endPosition the position to end copying the elements (exclusive)
   * @return a copy of this <code>AST</code> instance from the given <code>startPosition</code>
   *         (inclusive) to the <code>endPosition</code> (exclusive)
   * @deprecated use {@link #subList(int, int)} instead
   */
  @Deprecated
  default IASTAppendable copyFrom(int startPosition, int endPosition) {
    return subList(startPosition, endPosition);
  }

  public IASTAppendable subList(int startPosition, int endPosition);

  /**
   * <p>
   * Create a copy of this <code>AST</code>, which contains the same head and all elements from the
   * given <code>startPosition</code> (inclusive) to the <code>endPosition</code> (exclusive).
   * <p>
   * 
   * @param startPosition the position to start copying the elements (inclusive)
   * @param endPosition the position to end copying the elements (exclusive)
   * @param step the step size for copying the elements. If step is negative
   *        <code>startPosition</code> must be greater than <code>endPosition</code>
   * @return a copy of this <code>AST</code> instance from the given <code>startPosition</code>
   *         (inclusive) to the <code>endPosition</code> (exclusive)
   */
  public IASTAppendable subList(int startPosition, int endPosition, int step);



  /**
   * <p>
   * Create a copy of this <code>AST</code>, which only contains the head element of the list (i.e.
   * the element with index 0). For <code>ASTAssociation</code>s create a copy of the full form
   * <code>AST</code>, which only contains the head <code>S.Association</code> (i.e. the element
   * with index 0). In further evaluation steps this full form can be converted back into a real
   * <code>ASTAssociation</code>.
   * <p>
   * This also allows to set the header object to an arbitrary expression.
   *
   * @return a copy of this <code>IAST</code> instance, which only contains the head.
   */
  public IASTAppendable copyHead();

  /**
   * <p>
   * Create a copy of this <code>AST</code>, which only contains the head element of the list (i.e.
   * the element with index 0) and allocate the <code>intialCapacity</code> size of entries for the
   * arguments.
   * <p>
   * This also allows to set the header object to an arbitrary expression.
   *
   * @param intialCapacity the initial number of arguments
   * @return a copy of this <code>IAST</code> instance, which only contains the head.
   */
  public IASTAppendable copyHead(final int intialCapacity);

  /**
   * Copy the arguments of this AST to a list.
   *
   * @return a <code>java.util.List</code> containing the arguments of this AST
   */
  default List<IExpr> copyTo() {
    return (List<IExpr>) copyTo(new ArrayList<IExpr>(size()));
  }

  /**
   * Copy the arguments of this AST to a given collection object.
   *
   * @param collection
   * @return the given collection object containing the arguments of this IAST
   */
  default Collection<IExpr> copyTo(Collection<IExpr> collection) {
    for (int i = 1; i < size(); i++) {
      collection.add(get(i));
    }
    return collection;
  }

  /**
   * Create a copy of this <code>AST</code>, which contains the same head and all elements up to the
   * given <code>position</code> (exclusive).
   *
   * @param position the position up to which the copy should be created
   * @return a copy of this <code>AST</code> instance up to the given <code>position</code>
   *         (exclusive).
   */
  public IASTAppendable copyUntil(int position);

  /**
   * Create a copy of this <code>AST</code>, which contains the same head and all elements up to the
   * given <code>position</code> (exclusive).
   *
   * @param intialCapacity the initial capacity of elements
   * @param position the position up to which the copy should be created
   * @return a copy of this <code>IAST</code> instance up to the given <code>position</code>
   */
  public IASTAppendable copyUntil(final int intialCapacity, int position);

  default int count(Predicate<? super IExpr> predicate) {
    return count(predicate, 1);
  }

  /**
   * Test each argument with the {@link Predicate} and sum up how often it returns
   * <code>true</code>.
   * 
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @param fromIndex the start index from which the elements have to be tested
   * @return the number of elements which satisfy the predicate
   */
  public int count(Predicate<? super IExpr> predicate, int fromIndex);

  /**
   * Calls <code>get(position).equals(expr)</code>.
   *
   * @param position the position which should be tested for equality
   * @param expr the expr which should be tested for equality
   * @return <code>true</code> if the element at the given position is equal to the given expr
   */
  @Override
  public boolean equalsAt(int position, final IExpr expr);

  /**
   * Check all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and return <code>true</code> if one of the arguments starting from index <code>1</code>
   * satisfy the predicate.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @return the <code>true</code> if the predicate is true the first time or <code>false</code>
   *         otherwise
   */
  default boolean exists(ObjIntPredicate<? super IExpr> predicate) {
    return exists(predicate, 1);
  }

  /**
   * Check all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and return <code>true</code> if one of the arguments starting from index <code>
   * startOffset</code> satisfy the predicate.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @param startOffset start offset from which the element have to be tested
   * @return the <code>true</code> if the predicate is true the first time or <code>false</code>
   *         otherwise
   */
  public boolean exists(ObjIntPredicate<? super IExpr> predicate, int startOffset);

  /**
   * Check all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and return <code>true</code> if one of the arguments starting from index <code>1</code>
   * satisfy the predicate.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @return <code>true</code> if the predicate is true the first time or <code>false</code>
   *         otherwise
   */
  @Override
  default boolean exists(Predicate<? super IExpr> predicate) {
    return exists(predicate, 1);
  }

  /**
   * Check all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and return <code>true</code> if one of the arguments starting from index <code>
   * startOffset</code> satisfy the predicate.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @param startOffset start offset from which the element have to be tested
   * @return the <code>true</code> if the predicate is true the first time or <code>false</code>
   *         otherwise
   */
  public boolean exists(Predicate<? super IExpr> predicate, int startOffset);

  /**
   * Compare the arguments pairwise with the <code>stopPredicate</code>. If the predicate gives
   * <code>true</code> return <code>true</code>. If the <code>stopPredicate</code> gives false for
   * each pairwise comparison return <code>false</code> at the end.
   *
   * @param stopPredicate the predicate which filters the arguments pairwise in this
   *        <code>IAST</code>
   * @return <code>true</code> if the <code>stopPredicate</code> gives <code>true</code> for of of
   *         the pairwise comparisons
   */
  default boolean existsLeft(BiPredicate<IExpr, IExpr> stopPredicate) {
    int size = size();
    for (int i = 2; i < size; i++) {
      if (stopPredicate.test(get(i - 1), get(i))) {
        return true;
      }
    }
    return false;
  }

  /** @deprecated use {@link #slice(int, int)} instead */
  @Deprecated
  default IASTAppendable extract(int fromIndex, int toIndex) {
    return slice(fromIndex, toIndex);
  }

  /**
   * Extract <code>ConditionalExpression</code> from the arguments of <code>this</code> expression.
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/ConditionalExpression.md">ConditionalExpression</a>
   *
   * @param isUnaryConditionalExpression if <code>true</code> <code>this</code> is of the form
   *        <code>head( ConditionalExpression(expr, condition) )</code>
   * @return the <code>ConditionalExpression</code> or <code>F.NIL</code>
   */
  public IExpr extractConditionalExpression(boolean isUnaryConditionalExpression);

  /**
   * Select all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and append the elements which satisfy the <code>predicate</code> to the <code>filterAST
   * </code>, or otherwise append it to the <code>restAST</code>.
   *
   * @param filterAST the elements where the <code>predicate#apply()</code> method returns <code>
   *     true</code>
   * @param restAST the elements which don't match the predicate
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @return the <code>filterAST</code>
   */
  public IAST filter(IASTAppendable filterAST, IASTAppendable restAST,
      Predicate<? super IExpr> predicate);

  /**
   * Select all elements by applying the <code>Predicates.isTrue(expr)</code> predicate to each
   * argument in this <code>AST</code> and append the elements which satisfy the <code>
   * Predicates.isTrue(expr)</code> predicate to the <code>filterAST</code>.
   *
   * @param filterAST the elements where the <code>predicate#apply()</code> method returns <code>
   *     true</code>
   * @param unaryHead create a <code>Predicates.isTrue(unaryHead)</code> predicate which filters
   *        each element in this AST.
   * @return the <code>filterAST</code>
   */
  public IAST filter(IASTAppendable filterAST, IExpr unaryHead);

  /**
   * Select all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and append the arguments which satisfy the predicate to the <code>filterAST</code>.
   *
   * @param filterAST the elements where the <code>predicate#apply()</code> method returns <code>
   *     true</code>
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @return the <code>filterAST</code>
   */
  public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate);

  /**
   * Select all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and append up to <code>maxMatches</code> arguments which satisfy the predicate to the
   * <code>filterAST</code>.
   *
   * @param filterAST the elements where the <code>predicate#apply()</code> method returns <code>
   *     true</code>
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @param maxMatches the maximum number of matches
   * @return the <code>filterAST</code>
   */
  public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate, int maxMatches);

  /**
   * Select all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and append the arguments which satisfy the predicate to the <code>0th element</code> of
   * the result array, or otherwise append it to the <code>1st element</code> of the result array.
   *
   * @param predicate the predicate which filters each element in the range
   * @return the resulting ASTs in the 0-th and 1-st element of the array
   */
  public IASTAppendable[] filter(Predicate<? super IExpr> predicate);

  /**
   * Select all elements by applying the <code>function</code> to each argument in this <code>AST
   * </code> and append the result elements for which the function returns non <code>F.NIL</code>
   * elements to the <code>0th element</code> of the result array, or otherwise append it to the
   * <code>1st element</code> of the result array.
   *
   * @param function the function which filters each argument in this AST by returning a <code>
   *     result != F.NIL</code> .
   * @return the resulting ASTs in the 0-th and 1-st element of the array
   * @see F#NIL
   */
  public IASTAppendable[] filterNIL(final Function<IExpr, IExpr> function);

  /**
   * Find the first argument position, where the the <code>function</code> doesn't return <code>
   * F.NIL</code>. The search starts at index <code>1</code>.
   *
   * <p>
   * <b>Note</b>: If this is an <code>IAssociation</code> the rule at the position will be returned.
   *
   * @param function
   * @return <code>F.NIL</code> if no position was found
   */
  public IExpr findFirst(Function<IExpr, IExpr> function);

  /**
   * Find the first argument position, which equals <code>expr</code>. The search starts at index
   * <code>1</code>.
   *
   * @param expr the expression which should be found
   * @return <code>-1</code> if no position was found
   * @deprecated use {@link #indexOf(IExpr)} instead
   */
  @Deprecated
  public default int findFirstEquals(final IExpr expr) {
    return indexOf(expr);
  }

  /** {@inheritDoc} */
  @Override
  default IExpr first() {
    if (size() < 2) {
      return F.NIL;
    }
    return arg1();
  }

  /**
   * Apply the functor to the elements of the range from left to right and return the final result.
   * Results do accumulate from one invocation to the next: each time this method is called, the
   * accumulation starts over with value from the previous function call.
   *
   * @param function a binary function that accumulate the elements
   * @param startValue
   * @return the accumulated elements or {@link F#NIL} if the function returns {@link F#NIL}
   */
  public IExpr foldLeft(final BiFunction<IExpr, IExpr, ? extends IExpr> function, IExpr startValue,
      int start);

  /**
   * Apply the functor to the elements of the range from right to left and return the final result.
   * Results do accumulate from one invocation to the next: each time this method is called, the
   * accumulation starts over with value from the previous function call.
   *
   * @param function a binary function that accumulate the elements
   * @param startValue
   * @return the accumulated elements or {@link F#NIL} if the function returns {@link F#NIL}
   */
  public IExpr foldRight(final BiFunction<IExpr, IExpr, ? extends IExpr> function, IExpr startValue,
      int start);

  /**
   * Check all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and return <code>true</code> if all of the arguments starting from index <code>1</code>
   * satisfy the predicate.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @return <code>true</code> if the predicate is true for all elements or <code>false</code>
   *         otherwise
   */
  default boolean forAll(ObjIntPredicate<? super IExpr> predicate) {
    return forAll(predicate, 1);
  }

  /**
   * Check all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and return <code>true</code> if all of the arguments starting from index <code>
   * startOffset</code> satisfy the predicate.
   *
   * <p>
   * <b>Note</b>: If this is an <code>IAssociation</code> the rule at the position will be returned.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @param startOffset start offset from which the element have to be tested
   * @return <code>true</code> if the predicate is true for all elements or <code>false</code>
   *         otherwise
   */
  public boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset);

  /**
   * Check all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and return <code>true</code> if all of the arguments starting from index <code>1</code>
   * satisfy the predicate.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @return <code>true</code> if the predicate is true for all elements or <code>false</code>
   *         otherwise
   */
  @Override
  default boolean forAll(Predicate<? super IExpr> predicate) {
    return forAll(predicate, 1);
  }

  /**
   * Check all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and return <code>true</code> if all of the arguments starting from index <code>
   * startOffset</code> satisfy the predicate.
   *
   * <p>
   * <b>Note</b>: If this is an <code>IAssociation</code> the rule at the position will be returned.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @param startOffset start offset from which the element have to be tested
   * @return <code>true</code> if the predicate is true for all elements or <code>false</code>
   *         otherwise
   */
  public boolean forAll(Predicate<? super IExpr> predicate, int startOffset);

  @Override
  default boolean forAllLeaves(Predicate<? super IExpr> predicate) {
    return forAllLeaves(predicate, 1);
  }

  /**
   * Check all {@link IAST} recursively, which don't have <code>head</code> as head element and
   * apply the <code>predicate</code> to each leaf argument in this {@link IAST} and
   * sub-<code>AST</code>s and return <code>true</code> if all of the leaf elements starting from
   * index <code>startOffset</code> satisfy the predicate.
   * 
   * @param head the head which should be ignored
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @param startOffset start offset from which the leaf elements have to be tested
   * @return <code>true</code> if the predicate is true for all leaves of this <code>IAST</code>
   */
  public boolean forAllLeaves(IExpr head, Predicate<? super IExpr> predicate, int startOffset);

  /**
   * Check all atomic (non IAST objects) leaf elements by applying the <code>predicate</code> to
   * each leaf argument in this <code>AST</code> and sub-<code>AST</code>s and return <code>true
   * </code> if all of the leaf elements starting from index <code>startOffset</code> satisfy the
   * predicate.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @param startOffset start offset from which the leaf elements have to be tested
   * @return the <code>true</code> if the predicate is true for all elements or <code>false</code>
   *         otherwise
   */
  public boolean forAllLeaves(Predicate<? super IExpr> predicate, int startOffset);

  /**
   * Iterate over all <code>value-elements</code> from index <code>1</code> to <code>size()-1</code>
   * and call the method <code>Consumer.accept()</code> for these elements. <b>Note:</b> the 0-th
   * element (i.e. the head of the AST) will not be selected.
   */
  @Override
  public void forEach(Consumer<? super IExpr> action);

  /**
   * Iterate over all <code>value-elements</code> from index <code>startOffset</code> to
   * <code>size()-1</code> and call the method <code>Consumer.accept()</code> for these elements.
   * <b>Note:</b> the 0-th element (i.e. the head of the AST) will not be selected.
   *
   * @param action the action which should be executed for each element
   * @param startOffset the start offset from which the action.accept() method should be executed
   */
  public void forEach(Consumer<? super IExpr> action, int startOffset);

  /**
   * Every <code>entry</code> of this {@link IAST} is assumed to be a list of at least 2 elements.
   * <code>biFunction.apply(entry.first(), entry.second())</code> will be called for every entry of
   * this {@link IAST}. If it returns {@link F#NIL}, the result won't be appended to
   * <code>appendableList</code>. Otherwise the result will be appended to
   * <code>appendableList</code>.
   * 
   * @param appendableList the result list
   * @param biFunction the function which should be applied to the elements
   * @return the <code>appendableList</code>
   */
  default IASTAppendable forEach(IASTAppendable appendableList,
      BiFunction<IExpr, IExpr, IExpr> biFunction) {
    for (int i = 1; i < size(); i++) {
      IAST entry = (IAST) get(i);
      IExpr bf = biFunction.apply(entry.first(), entry.second());
      if (bf.isPresent()) {
        appendableList.append(bf);
      }
    }
    return appendableList;
  }

  /**
   * Consume all <code>value-elements</code> generated by the given function from index
   * <code>1</code> inclusive to <code>end</code> exclusive.
   *
   * @param end end index (exclusive)
   * @param action function which accepts the elements
   */
  default void forEach(int end, Consumer<? super IExpr> action) {
    forEach(1, end, action);
  }

  /**
   * Consume all <code>value-elements</code> generated by the given function from index
   * <code>start</code> inclusive to <code>end</code> exclusive.
   *
   * @param start start index (inclusive)
   * @param end end index (exclusive)
   * @param action function which accepts the elements
   */
  default void forEach(int start, int end, Consumer<? super IExpr> action) {
    for (int i = start; i < end; i++) {
      action.accept(get(i));
    }
  }

  /**
   * Consume all <code>value-elements</code> generated by the given function from index
   * <code>start</code> inclusive to <code>end</code> exclusive.
   * 
   * @param start start index (inclusive)
   * @param end end index (exclusive)
   * @param consumer function which accepts the elements
   */
  default void forEach(int start, int end, ObjIntConsumer<? super IExpr> consumer) {
    for (int i = start; i < end; i++) {
      consumer.accept(get(i), i);
    }
  }

  /**
   * Consume all <code>value-elements</code> generated by the given function from index
   * <code>2</code> inclusive to <code>size()</code> exclusive.
   * 
   * @param consumer function which accepts the elements
   */
  default void forEach2(final ObjIntConsumer<? super IExpr> consumer) {
    forEach(2, size(), consumer);
  }

  /**
   * Consume all <code>value-elements</code> generated by the given function from index
   * <code>1</code> inclusive.
   * 
   * @param end end index (exclusive)
   * @param consumer function which accepts the elements
   */
  default void forEach(int end, final ObjIntConsumer<? super IExpr> consumer) {
    forEach(1, end, consumer);
  }

  /**
   * Consume all <code>value-elements</code> generated by the given function from index
   * <code>1</code> inclusive to <code>size()</code> exclusive.
   * 
   * @param consumer function which accepts the elements
   */
  default void forEach(ObjIntConsumer<? super IExpr> consumer) {
    forEach(1, size(), consumer);
  }

  /**
   * Iterate over all elements from index <code>1</code> to <code>size()-1</code> and call the
   * method <code>Consumer.accept()</code> for these elements. <b>Note:</b> the 0-th element (i.e.
   * the head of the AST) will not be selected. If the element is an Association the complete rule
   * will be selected as element.
   */
  public void forEachRule(Consumer<? super IExpr> action);

  /**
   * Iterate over all elements from index <code>startOffset</code> to <code>size()-1</code> and call
   * the method <code>Consumer.accept()</code> for these elements. <b>Note:</b> the 0-th element
   * (i.e. the head of the AST) will not be selected. If the element is an Association the complete
   * rule will be selected as element.
   *
   * @param action the action which should be executed for each element
   * @param startOffset the start offset from which the action.accept() method should be executed
   */
  public void forEachRule(Consumer<? super IExpr> action, int startOffset);

  /**
   * Set {@link IAST#BUILT_IN_EVALED} flag.
   * 
   * @return <code>this</code> instance
   */
  default IAST functionEvaled() {
    addEvalFlags(IAST.BUILT_IN_EVALED);
    return this;
  }

  /**
   * Returns the element at the specified location in this {@code IAST}.
   *
   * @param location the index of the element to return.
   * @return the element at the specified location.
   * @throws IndexOutOfBoundsException if {@code location < 0 || >= size()}
   */
  public IExpr get(IInteger location);

  /**
   * Returns the element at the specified location in this {@code IAST}. If this is an
   * {@link IAssociation} return the value of the rule at the specified location.
   *
   * @param location the index of the element to return.
   * @return the element at the specified location.
   * @throws IndexOutOfBoundsException if {@code location < 0 || >= size()}
   */
  @Override
  public IExpr get(int location);

  /**
   * Casts an <code>IExpr</code> at position <code>index</code> to an <code>IAST</code>.
   *
   * @param index the position of the element which should be cast to an <code>IAST</code>
   * @return <code>F.NIL</code> if the element at the given position is not an <code>IAST</code>
   */
  public IAST getAST(int index);

  /**
   * If this is a matrix, return the column dimension. Otherwise return <code>-1</code>.
   */
  @Override
  default int getColumnDimension() {
    int[] matrix = isMatrix(false);
    if (matrix != null) {
      return matrix[1];
    }
    return -1;
  }

  /**
   * Get the evaluation flags for this list.
   *
   * @return the evaluation flags
   */
  public int getEvalFlags();

  /**
   * Get the cached hash value.
   *
   * @return the cached hash value
   */
  public int getHashCache();

  /**
   * Casts an <code>IExpr</code> at position <code>index</code> to an <code>IInteger</code>.
   *
   * @param index
   * @return the element at the specified position casted to an <code>IInteger</code>
   * @throws IllegalArgumentException if the cast is not possible
   */
  public IInteger getInt(int index);

  /**
   * Returns <code>length</code> number of elements specified in the <code>items</code> position
   * array in this {@code IAST}.
   *
   * @param items ascending ordered array of positions which should be selected from this {@code
   *     IAST}.
   * @param length the end position (exclusive) to which the <code>items</code> array is filled with
   *        valid element positions
   * @return the elements specified in the <code>items</code> array
   */
  default IAST getItems(int[] items, int length) {
    return getItems(items, length, 0);
  }

  /**
   * Returns <code>length</code> number of elements specified in the <code>items</code> position
   * array in this {@code IAST}.
   *
   * @param items ascending ordered array of positions which should be selected from this {@code
   *     IAST}.
   * @param length the end position (exclusive) to which the <code>items</code> array is filled with
   *        valid element positions
   * @param offset the start position from which the elements should be selected
   * @return the elements specified in the <code>items</code> array
   */
  public IAST getItems(int[] items, int length, int offset);

  /**
   * Casts an <code>IExpr</code> which is a {@link S#List} at position <code>index</code> to an
   * <code>IAST
   * </code>.
   *
   * @param index the position of the element which should be cast to an <code>IAST</code>
   * @return the element at the specified position casted to an <code>IAST</code>
   * @throws IllegalArgumentException if the cast is not possible
   */
  public IAST getList(int index);

  /**
   * Casts an <code>IExpr</code> at position <code>index</code> to an <code>INumber</code>.
   *
   * @param index the position of the element which should be cast to an <code>INumber</code>
   * @return the element at the specified position casted to an <code>INumber</code>
   * @throws IllegalArgumentException if the cast is not possible
   */
  public INumber getNumber(int index);

  /**
   * Returns the element at the specified positions in the nested ASTs.
   *
   * @param positions index of the element to return
   * @return the element at the specified positions in this nested AST or {@link F#NIL}
   * @throws IndexOutOfBoundsException if one of the positions are out of range
   */
  public IExpr getPart(final int... positions) throws IndexOutOfBoundsException;

  /**
   * Returns the element at the specified positions in the nested ASTs.
   *
   * @param positions index of the element to return
   * @return the element at the specified positions in this nested AST
   * @throws IndexOutOfBoundsException if one of the positions are out of range
   */
  public IExpr getPart(final IntList positions);

  public Object getProperty(PROPERTY key);

  /**
   * If this is a matrix, return the row dimension. Otherwise return <code>-1</code>.
   */
  @Override
  default int getRowDimension() {
    int[] matrix = isMatrix(false);
    if (matrix != null) {
      return matrix[0];
    }
    return -1;
  }

  /**
   * Assuming this is a list of rules or an <code>IAssociation</code>. Return the first rule which
   * equals the <code>key</code> argument.
   *
   * @param key the key which should be found in the rules.
   * @return the first rule which equals the <code>key</code> argument; otherwise return
   *         {@link F#NIL}
   */
  default IAST getRule(IExpr key) {
    int index = indexOf(x -> x.isRuleAST() && x.first().equals(key));
    if (index > 0) {
      return (IAST) get(index);
    }
    return F.NIL;
  }

  /**
   * If this is an <code>IAssociation</code> return the rule at the position. Otherwise call
   * {@link #get(int)}.
   *
   * @param position
   * @return the rule at the position for associations or the element at the position.
   */
  default IExpr getRule(int position) {
    return get(position);
  }

  /**
   * Assuming this is a list of rules or an <code>IAssociation</code>. Return the first rule which
   * equals the <code>key</code> string at the left-hand-side of the rule.
   *
   * @param key the key which should be found in the rules.
   * @return the first rule which equals the <code>key</code> argument; otherwise return
   *         {@link F#NIL}
   */
  default IAST getRule(String key) {
    int index = indexOf(x -> x.isRuleAST() && x.first().equals(F.$str(key)));
    if (index > 0) {
      return (IAST) get(index);
    }
    return F.NIL;
  }

  /**
   * Get the argument of the {@link IAST} function (i.e. {@link #get(int)} ) and if the expression
   * is the function {@link F#Unevaluated(IExpr)} return the first argument of the
   * {@link F#Unevaluated(IExpr)} function.
   * 
   * @param position the position of the element
   * @return if this is {@link F#Unevaluated(IExpr)} return the first argument of the
   *         {@link F#Unevaluated(IExpr)} function.
   */
  default IExpr getUnevaluated(int position) {
    return get(position);
  }

  /**
   * If this is an <code>IAssociation</code> return the value of the rule at the position. Otherwise
   * call {@link #get(int)}.
   *
   * @param position the position of the element
   * @return the value of the rule at the position for associations or the element at the position.
   */
  default IExpr getValue(int position) {
    return get(position);
  }

  /**
   * Test if one of the arguments gives <code>true</code> for the <code>isNumericArgument()</code>
   * method
   *
   * @return <code>true</code> if one of the arguments gives <code>true</code> for the <code>
   *     isNumericArgument()</code> method
   */
  default boolean hasNumericArgument() {
    int size = size();
    for (int i = 1; i < size; i++) {
      if (get(i).isNumericArgument()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Test if the last argument contains a pattern with an optional argument. (i.e. <code>x_:value
   * </code>)
   *
   * @return <code>true</code> if the last argument contains a pattern with an optional argument
   */
  default boolean hasOptionalArgument() {
    return (size() > 1) && last().isPatternDefault();
  }

  /**
   * Returns<code>true</code>, if one of the headers of this AST contains a trigonometric function.
   *
   * @return <code>true</code>, if one of the headers of this AST contains a trigonometric function.
   */
  public boolean hasTrigonometricFunction();

  @Override
  default boolean isASTOrAssociation() {
    return true;
  }

  /**
   * Test if this AST contains no argument
   *
   * @return <code>true</code> if this IAST is empty
   */
  @Override
  public boolean isEmpty();

  /**
   * Are the given evaluation flags disabled for this list ?
   *
   * @param flags the evaluation flags to test against
   * @return <code>true</code> if the evaluation flags are disabled
   * @see IAST#NO_FLAG
   */
  @Override
  public boolean isEvalFlagOff(int flags);

  /**
   * Are the given evaluation flags enabled for this list ?
   *
   * @param flags the evaluation flags to test against
   * @return <code>true</code> if the evaluation flags are enabled
   * @see IAST#NO_FLAG
   */
  @Override
  public boolean isEvalFlagOn(int flags);

  /**
   * Returns <code>true</code> if the expression at the given <code>position</code>, did not match
   * the <code>pattern</code>. Calls <code>get(Position).isFree(pattern, true)</code>.
   *
   * @param position
   * @param pattern a pattern-matching expression
   * @return <code>true</code> if the expression at the given <code>position</code> is free of the
   *         pattern.
   */
  public boolean isFreeAt(int position, final IExpr pattern);

  @Override
  default boolean isNotDefined() {
    if (isIndeterminate() || isDirectedInfinity()) {
      return true;
    }
    for (int i = 0; i < size(); i++) {
      if (get(i).isIndeterminate() || get(i).isDirectedInfinity()) {
        return true;
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  default boolean isNumberOrInfinity() {
    return isInfinity() || isNegativeInfinity();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPatternMatchingFunction();

  default boolean isRealsIntervalData() {
    return isAST(S.IntervalData, 5)//
        && arg1().equals(F.CNIInfinity)//
        && arg2() == S.Less //
        && arg3() == S.Less //
        && arg4().equals(F.CIInfinity);
  }

  /**
   * Check if the object at index 0 (i.e. the head of the list) is the same object as <code>head
   * </code> and if the size of the list is greater or equal <code>length</code>.
   *
   * @param head object to compare with element at location <code>0</code>
   * @param length
   * @return <code>true</code> if the head is the same object as <code>head</code> and the size of
   *         the list is greater or equal <code>length</code>
   */
  @Override
  default boolean isSameHeadSizeGE(ISymbol head, int length) {
    return head() == head && length <= size();
  }

  @Override
  default boolean isSquare() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isTimes();

  /**
   * Returns an iterator over the elements in this list starting with offset <b>1</b>.
   *
   * @return an iterator over this list values.
   */
  @Override
  public Iterator<IExpr> iterator();

  /**
   * Append a String composed of copies of the arguments of this AST joined together with the
   * specified {@code delimiter}.
   *
   * @param builder join the elements as strings
   * @param delimiter the delimiter that separates each element
   */
  default void joinToString(StringBuilder builder, BiConsumer<StringBuilder, IExpr> consumer,
      CharSequence delimiter) {
    final int size = size();
    for (int i = 1; i < size; i++) {
      consumer.accept(builder, get(i));
      if (i < size - 1) {
        builder.append(delimiter);
      }
    }
  }

  /**
   * Append a String composed of copies of the arguments of this AST joined together with the
   * specified {@code delimiter}.
   *
   * @param builder join the elements as strings
   * @param delimiter the delimiter that separates each element
   */
  default void joinToString(StringBuilder builder, CharSequence delimiter) {
    final int size = size();
    for (int i = 1; i < size; i++) {
      builder.append(get(i).toString());
      if (i < size - 1) {
        builder.append(delimiter);
      }
    }
  }

  /**
   * Maps the elements of this IAST with the unary <code>functor</code>. If the <code>functor</code>
   * returns <code>F.NIL</code> the original element of this AST list is used. <br>
   * <br>
   * Example for mapping with <code>Functors#replace1st()</code>, where the first argument will be
   * replaced by the current argument of this AST:
   *
   * <pre>
   * plusAST.map(Functors.replace1st(F.D(null, dAST.arg2())));
   * </pre>
   *
   * @param function a unary function
   * @return the resulting IAST with the mapped elements
   */
  public IAST map(final Function<IExpr, ? extends IExpr> function);

  /**
   * Maps the elements of this IAST with the unary <code>functor</code>. If the <code>functor</code>
   * returns <code>F.NIL</code> the original element of this AST list is used. <br>
   * <br>
   * Example for mapping with <code>Functors#replace1st()</code>, where the first argument will be
   * replaced by the current argument of this AST:
   *
   * <pre>
   * plusAST.map(Functors.replace1st(F.D(null, dAST.arg2())));
   * </pre>
   *
   * @param functor a unary function
   * @param startOffset the startOffset from there the <code>functor</code> should be used.
   * @return the resulting IAST with the mapped elements
   */
  public IAST map(final Function<IExpr, ? extends IExpr> functor, final int startOffset);

  /**
   * Maps the elements of this IAST with the elements of the <code>secondAST</code>.
   *
   * @param resultAST
   * @param secondAST
   * @param function a binary function
   * @return the given resultAST.
   * @throws IndexOutOfBoundsException if the secondAST size is lesser than this AST size
   */
  public IAST map(IASTAppendable resultAST, IAST secondAST,
      BiFunction<IExpr, IExpr, IExpr> function);

  /**
   * Append the mapped ranges elements directly to the given <code>list</code>
   *
   * @param astResult
   * @param function
   * @return the resulting IAST with the mapped elements
   */
  public IASTAppendable map(IASTAppendable astResult, IUnaryIndexFunction<IExpr, IExpr> function);

  /**
   * Maps the elements of this IAST with the unary functor. If the function returns <code>F.NIL
   * </code> the original element of the result list is used.
   *
   * @param clonedResultAST a list which is cloned from <code>this</code> list or greater or equal
   *        in size of <code>this</code> list.
   * @param functor a unary function
   * @return the resulting IAST with the mapped elements
   */
  public IAST map(final IASTMutable clonedResultAST, final Function<IExpr, IExpr> functor);

  /**
   * Maps the elements of this IAST with the unary functor. If the function returns <code>null
   * </code> the original element of the result list is used.
   *
   * @param head the new head element of the result list
   * @param functor a unary function
   * @return the resulting IAST with the mapped elements
   */
  public IAST map(final IExpr head, final Function<IExpr, IExpr> functor);

  default IAST mapAt(final IASTAppendable replacement, int position) {
    return mapThread(replacement, position);
  }

  /**
   * Maps the leafs (relative to <code>testHead</code>) of this IAST with the unary functor. If the
   * function returns <code>F.NIL</code> the original leaf of the result list is used.
   * 
   * @param testHead if the levels head equals testHead apply mapLeaf on this list element
   * @param function the startOffset from there the <code>functor</code> should be used.
   * @return the resulting IAST with the mapped leafs
   */
  default IAST mapLeaf(IExpr testHead, final Function<IExpr, IExpr> function) {
    return mapLeaf(testHead, function, 1);
  }

  /**
   * Maps the leafs (relative to <code>testHead</code>) of this IAST with the unary functor. If the
   * function returns <code>F.NIL</code> the original leaf of the result list is used.
   * 
   * @param testHead if the levels head equals testHead apply mapLeaf on this list element
   * @param function functor a unary function
   * @param startOffset the startOffset from there the <code>functor</code> should be used.
   * @return the resulting IAST with the mapped leafs
   */
  public IAST mapLeaf(IExpr testHead, final Function<IExpr, IExpr> function, final int startOffset);

  /**
   * Append the mapped ranges elements directly to the given <code>list</code>
   *
   * @param list
   * @param binaryFunction binary function
   * @param leftArg left argument of the binary functions <code>apply()</code> method.
   * @return {@link F#NIL} if <code>binaryFunction</code> returns {@link F#NIL}
   */
  public IAST mapLeft(IASTAppendable list, BiFunction<IExpr, IExpr, IExpr> binaryFunction,
      IExpr leftArg);

  /**
   * This method assumes that <code>this</code> is a list of list in matrix form. It combines the
   * column values in a list as argument for the given <code>function</code>.
   * <p>
   * <b>Example</b> a matrix <code>{{x1, y1,...}, {x2, y2, ...}, ...}</code> will be converted to
   * <code>{f.apply({x1, x2,...}), f.apply({y1, y2, ...}), ...}</code>
   *
   * @param dim the dimension of the matrix
   * @param f a unary function
   * @return the resulting IAST with the mapped elements
   */
  @Override
  public IExpr mapMatrixColumns(int[] dim, Function<IExpr, IExpr> f);

  /**
   * Maps the elements of this IAST in the reversed order with the unary <code>functor</code>. If
   * the <code>functor</code> returns <code>F.NIL</code> the original element of this AST list is
   * set at the reversed position.
   *
   * @param function a unary function
   * @return the reversed list mapped with the function
   */
  public IAST mapReverse(final Function<IExpr, IExpr> function);

  /**
   * Append the mapped ranges elements directly to the given <code>list</code>
   *
   * @param list
   * @param binaryFunction a binary function
   * @param rightArg right argument of the binary functions <code>apply()</code> method.
   * @return {@link F#NIL} if <code>binaryFunction</code> returns {@link F#NIL}
   */
  public IAST mapRight(IASTAppendable list, BiFunction<IExpr, IExpr, IExpr> binaryFunction,
      IExpr rightArg);

  /**
   * Maps the elements of this IAST on the first level of arguments with the unary
   * <code>function</code>.
   *
   * @param function a unary function which maps each argument
   * @return the resulting IAST with the mapped elements
   */
  public IASTMutable mapThread(Function<IExpr, IExpr> function);

  /**
   * Maps the elements of <code>this</code> IAST and of <code>that</code> IAST on the first level of
   * arguments with the binary <code>function</code>. The size of the result is the minimum size of
   * <code>this</code> and <code>that</code>.
   * 
   * @param that
   * @param function a unary function which maps each argument of <code>this</code> IAST and
   *        <code>that</code>
   * @return the size of the result is the minimum size of <code>this</code> and <code>that</code>.
   */
  public IASTMutable mapThread(IAST that, BiFunction<IExpr, IExpr, IExpr> function);

  /** {@inheritDoc} */
  @Override
  public IASTMutable mapThread(final IAST replacement, int positionInReplacement);

  /**
   * Maps the elements of this {@link IAST} on the first level of arguments with the evaluated unary
   * functor <code> Functors.replaceArg(replacement, position)</code>, there <code>replacement
   * </code> is an IAST at which the argument at the given position will be replaced by the
   * currently mapped element. This can be used to create an effect as if &quot;the <code>position
   * </code>-th argument of an IAST object would be <code>Listable</code>&quot;.
   *
   * <p>
   * Example for mapping with <code>Functors#replaceArg()</code>, where the argument at the given
   * position will be replaced by the current argument of this AST:
   *
   * <pre>
   * plusAST.mapThreadEvaled(engine, F.D(F.Slot1, F.x), 1);
   * </pre>
   *
   * @param engine
   * @param replacement an IAST there the argument at the given position is replaced by the
   *        currently mapped argument of this {@link IAST}.
   * @param positionInReplacement the position in <code>replacement</code> which should be replaced
   *        by the corresponding argument of this {@link IAST}
   * @return the resulting IAST with the mapped elements
   * @see IAST#map(Function, int)
   */
  public IASTMutable mapThreadEvaled(EvalEngine engine, final IAST replacement,
      int positionInReplacement);

  /**
   * Maps the elements of this IAST with the unary functor <code>
   * Functors.replaceArg(replacement, position)</code>, there <code>replacement</code> is an IAST at
   * which the argument at the given position will be replaced by the currently mapped element and
   * appends the element to <code>appendAST</code>.
   *
   * @param engine TODO
   * @param appendAST
   * @param replacement an IAST there the argument at the given position is replaced by the
   *        currently mapped argument of this IAST.
   * @param position
   * @return <code>appendAST</code>
   * @see IAST#map(Function, int)
   */
  public IASTAppendable mapThreadEvaled(EvalEngine engine, IASTAppendable appendAST,
      final IAST replacement, int position);

  /** {@inheritDoc} */
  @Override
  default IAST most() {
    switch (size()) {
      case 0:
        return this;
      case 1:
        return this;
      default:
        return splice(argSize());
    }
  }

  /**
   * Return the argument at index 1, if the <code>argSize() == 1</code>. Or return the complete ast
   * if the <code>argSize() > 1</code> If the <code>argSize() == 0</code> return
   * <code>defaultValue</code> (useful for ASTs with attribute <code>OneIdentity</code> for example
   * for <code>Plus[]</code> you can call <code>getOneIdentity(F.C0)</code> or for
   * <code>Times[]</code>) you can call <code>getOneIdentity(F.C1)</code>.
   *
   * @param defaultValue default value, if <code>size() < 2</code>.
   * @return the argument at index 1, if the <code>argSize() == 1</code> or
   *         <code>defaultValue</code>, if the <code>argSize() == 0</code>; otherwise return
   *         <code>this</code>
   */
  public IExpr oneIdentity(IExpr defaultValue);

  /**
   * Return the argument at index <code>1</code>, if the <code>argSize() == 1</code>. Or return the
   * complete IAST if the <code>argSize() > 1</code> If the <code>argSize() == 0</code> return
   * <code>0</code>.
   *
   * @return the argument at index <code>1</code>, if the <code>argSize() == 1</code> or
   *         <code>0</code> if the <code>argSize() == 0</code>; otherwise return <code>this</code>.
   */
  default IExpr oneIdentity0() {
    return oneIdentity(F.C0);
  }

  /**
   * Return the argument at index 1, if the <code>argSize() == 1</code>. Or return the complete ast
   * if the <code>argSize() > 1</code> If the <code>argSize() == 0</code> return <code>1</code>.
   *
   * @return the argument at index 1, if the <code>argSize() == 1</code> or <code>1</code> if the
   *         <code>argSize() == 0</code>; otherwise return <code>this</code>.
   */
  default IExpr oneIdentity1() {
    return oneIdentity(F.C1);
  }

  /**
   * Return <code>this</code> if <code>this</code> unequals <code>F.NIL</code> , otherwise return
   * <code>other</code>.
   *
   * @param other
   * @return <code>this</code> if <code>this</code> unequals <code>F.NIL</code>, otherwise return
   *         <code>other</code>.
   * @see java.util.Optional#orElse(Object)
   */
  public IAST orElse(final IAST other);

  public boolean parallelAllMatch(IAST ast, int startPosition, int endPosition,
      BiPredicate<? super IExpr, ? super IExpr> predicate);

  public boolean parallelAnyMatch(IAST ast, int startPosition, int endPosition,
      BiPredicate<? super IExpr, ? super IExpr> predicate);

  /**
   * Calculate a hash value especially for pattern matching
   *
   * @return the pattern hash value
   */
  public int patternHashCode();

  /**
   * Prepend an expression to this list.
   *
   * @param expr the expression to prepend
   * @return <code>this</code> after prepending the given expression.
   */
  public IASTAppendable prependClone(IExpr expr);

  public Object putProperty(PROPERTY key, Object value);

  /**
   * Removes all objects which satisfy the given predicate.
   *
   * @param predicate the predicate which filters the arguments
   * @return {@link F#NIL} if no element could be removed
   */
  public IASTAppendable remove(Predicate<? super IExpr> predicate);

  /**
   * Create a shallow copy of this <code>IAST</code> instance (the elements themselves are not
   * copied) and remove the element at the given <code>position</code>.
   *
   * @param position the position of the element which should be removed
   * @return a shallow copy with removed element at the given position.
   */
  public IASTAppendable removeAtClone(int position);

  /**
   * Create a shallow copy of this <code>IAST</code> instance (the elements themselves are not
   * copied) and remove the element at the given <code>position</code>.
   *
   * @param position the position of the element which should be removed
   * @return an IAST with removed element at the given position.
   */
  public IASTMutable removeAtCopy(int position);

  /**
   * Create a new <code>IAST</code> and remove all arguments from position <code>fromPosition</code>
   * inclusive to the end of this AST.
   *
   * @param fromPosition
   * @return the resulting AST with removed element at the given position
   * @throws IndexOutOfBoundsException if the <code>fromPosition</code> is out of range
   */
  public default IAST removeFromEnd(int fromPosition) {
    if (0 < fromPosition && fromPosition <= size()) {
      if (fromPosition == size()) {
        return this;
      }
      IASTAppendable ast = F.ast(head(), fromPosition);
      ast.appendArgs(this, fromPosition);
      return ast;
    } else {
      throw new IndexOutOfBoundsException(
          "Index: " + Integer.valueOf(fromPosition) + ", Size: " + size());
    }
  }

  /**
   * Copy to a new <code>IAST</code> and remove all arguments from start position <code>1</code>
   * inclusive to the <code>firstPosition</code> exclusive of this AST.
   *
   * @param endPosition the end position (exclusive) to which the elements will be removed
   * @throws IndexOutOfBoundsException if the <code>fromPosition</code> is out of range
   */
  public default IAST removeFromStart(int endPosition) {
    if (0 < endPosition && endPosition <= size()) {
      if (endPosition == 1) {
        return this;
      }
      int last = size();
      int size = last - endPosition + 1;
      switch (size) {
        case 1:
          return F.headAST0(head());
        case 2:
          return F.unaryAST1(head(), get(last - 1));
        case 3:
          return F.binaryAST2(head(), get(last - 2), get(last - 1));
        case 4:
          return F.ternaryAST3(head(), get(last - 3), get(last - 2), get(last - 1));
      }
      return copyFrom(endPosition);
    } else {
      throw new IndexOutOfBoundsException(
          "Index: " + Integer.valueOf(endPosition) + ", Size: " + size());
    }
  }

  /**
   * Removes all the elements from this list which satisfies the given predicate and return the
   * result as a new List
   *
   * @param predicate the predicate which filters each element in the range
   * @return the resulting ASTs in the 0-th and 1-st element of the array
   */
  public IAST removeIf(Predicate<? super IExpr> predicate);

  /**
   * Create a shallow copy of this <code>IAST</code> instance (the elements themselves are not
   * copied) and remove the elements defined in the given <code>removedPositionsArray</code> up to
   * <code>untilIndex</code> (exclusive).
   *
   * @param removedPositions
   * @param untilIndex up to this position (exclusive) the elements will be removed from the copy
   * @return an IAST with removed element at the given position.
   */
  public IASTAppendable removePositionsAtCopy(int[] removedPositions, int untilIndex);

  public IAST removePositionsAtCopy(Predicate<IExpr> predicate);

  /**
   * Create a shallow copy of this <code>IAST</code> instance (the elements themselves are not
   * copied) and replace the <code>replacePosition</code> with <code>newEntries</code> and remove
   * the elements defined in the given <code>removePositions</code>
   * 
   * @param replacePosition the position which should be replaced
   * @param newEntries the new entries which should be inserted at the given position
   * @param removePositions the positions which should be removed
   * 
   * @return an IAST with removed element at the given position.
   */
  public IASTAppendable replaceSubset(int[] replacePosition, IExpr[] newEntries,
      int[] removePositions);

  /** {@inheritDoc} */
  @Override
  default IAST rest() {
    switch (size()) {
      case 0:
        return this;
      case 1:
        return this;
      case 2:
        return F.headAST0(head());
      case 3:
        return F.unaryAST1(head(), arg2());
      case 4:
        return F.binaryAST2(head(), arg2(), arg3());
      case 5:
        return F.ternaryAST3(head(), arg2(), arg3(), arg4());
      default:
        return removeAtClone(1);
    }
  }

  /**
   * Append the elements in reversed order to the <code>resultList</code>
   *
   * @param resultList the list to which the elements are appended in reversed order
   * @return the <code>resultList</code>
   */
  public IASTAppendable reverse(IASTAppendable resultList);

  /**
   * Rotate the elements to the left by n places and append the resulting elements to the <code>
   * resultList</code>
   *
   * @param resultList the list to which the elements are appended in rotated order
   * @param n the number of places to rotate
   * @return the <code>resultList</code>
   */
  public IAST rotateLeft(IASTAppendable resultList, final int n);

  /**
   * Rotate the elements to the right by n places and append the resulting elements to the <code>
   * resultList</code>
   *
   * @param resultList the list to which the elements are appended in rotated order
   * @param n the number of places to rotate
   * @return the <code>resultList</code>
   */
  public IAST rotateRight(IASTAppendable resultList, final int n);

  /** {@inheritDoc} */
  @Override
  default IExpr second() {
    return arg2();
  }

  /**
   * Select all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and append the arguments which satisfy the predicate to the result.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @return the selected IAST with the arguments which satisfy the predicate
   */
  public IAST select(Predicate<? super IExpr> predicate);

  /**
   * Select all elements by applying the <code>predicate</code> to each argument in this <code>AST
   * </code> and append up to <code>maxMatches</code> arguments which satisfy the predicate.
   *
   * @param predicate the predicate which filters each argument in this <code>AST</code>
   * @param maxMatches the maximum number of matches
   * @return the selected ast
   */
  public IAST select(Predicate<? super IExpr> predicate, int maxMatches);

  /**
   * Create a shallow copy of this <code>IAST</code> instance (the elements themselves are not
   * copied) and set the <code>expr</code> at the given <code>position</code>.
   *
   * @param i
   * @param expr
   * @return a clone with element set to <code>expr</code> at the given <code>position</code>.
   */
  public IASTAppendable setAtClone(int i, IExpr expr);

  /**
   * Create a shallow copy of this <code>IAST</code> instance (the elements themselves are not
   * copied) and set the <code>expr</code> at the given <code>position</code>. In contrast to the
   * <code>setAtClone()</code> method, this method returns exactly the same type for <code>
   * AST0, AST1, AST2, AST3</code>.
   *
   * @param i
   * @param expr
   * @return a copy with element set to <code>expr</code> at the given <code>position</code>.
   */
  public default IASTMutable setAtCopy(int i, IExpr expr) {
    IASTMutable ast = copy();
    ast.set(i, expr);
    return ast;
  }

  /**
   * Set the evaluation flags for this list (i.e. replace all existing flags).
   *
   * @param i
   */
  public void setEvalFlags(int i);

  /**
   * Returns the <b>number of elements</b> in this {@code IAST}.The <b>number of elements</b> equals
   * <code>argSize() + 1</code> (i.e. the <b>number of arguments</b> plus 1). If this is an atom
   * return size <code>0</code>.
   *
   * @return the <b>number of elements</b> in this {@code IAST}.
   * @see #argSize()
   */
  @Override
  public int size();

  /**
   * Copy of a sub <code>AST</code> from <code>start</code> (inclusive) to <code>end</code>
   * (exclusive). The <code>slice()</code> method selects the elements starting at the given <code>
   * start</code> argument, and ends at, but does not include, the given <code>end</code> argument.
   *
   * @param start An integer that specifies where to start the selection (the first argument has an
   *        index of <code>1</code>, the head of the AST is at index <code>0</code>).
   * @param end An integer that specifies where to end the selection.
   * @return a copy of sub <code>AST</code> from <code>start</code> (inclusive) to <code>end</code>
   *         (exclusive).
   */
  default IASTAppendable slice(int start, int end) {
    if (0 < start && start <= size() && start < end && end <= size()) {
      IASTAppendable ast = F.ast(head(), end - start);
      for (int i = start; i < end; i++) {
        ast.append(get(i));
      }
      return ast;
    }
    throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(start) + ", Size: " + size());
  }

  /**
   * The <code>splice()</code> method removes the item from an AST copy, and returns the copy.
   *
   * @param index An integer that specifies at what position to add/remove items.
   * @return an IAST with removed element at the given position.
   */
  default IAST splice(int index) {
    return removeAtCopy(index);
  }

  /**
   * The <code>splice()</code> method adds/removes items to/from an AST copy, and returns the copy.
   *
   * @param index An integer that specifies at what position to add/remove items.
   * @param howMany The number of items to be removed. If set to 0, no items will be removed
   * @param items Optional. The new item(s) to be added to the AST copy
   * @return an IAST with removed element at the given position.
   */
  public IAST splice(int index, int howMany, IExpr... items);


  /**
   * Returns a sequential {@link Stream} which starts at index <code>1</code>of the specified array
   * as its source.
   *
   * @return a {@code Stream} for the internal array range
   */
  public Stream<IExpr> stream();

  /**
   * Returns a sequential {@link Stream} with the specified range of the specified array as its
   * source.
   *
   * @param startInclusive the first index to cover, inclusive
   * @param endExclusive index immediately past the last index to cover
   * @return a {@code Stream} for the internal array range
   * @throws ArrayIndexOutOfBoundsException if {@code startInclusive} is negative, {@code
   *     endExclusive} is less than {@code startInclusive}, or {@code endExclusive} is greater than
   *         the array size
   */
  public Stream<IExpr> stream(int startInclusive, int endExclusive);

  /**
   * Returns a sequential {@link Stream} which starts at index <code>0</code> of the specified
   * array.
   *
   * @return a {@code Stream} for the internal array range
   * @throws ArrayIndexOutOfBoundsException
   */
  default Stream<IExpr> stream0() {
    return stream(0, size());
  }

  /**
   * Returns an array containing all elements contained in this {@code List}.
   *
   * @return an array of the elements from this {@code List}.
   */
  public IExpr[] toArray();

  default IExpr[] toArray(int startIndex) {
    IExpr[] result = new IExpr[size() - startIndex];
    int j = 0;
    for (int i = startIndex; i < size(); i++) {
      result[j++] = get(i);
    }
    return result;
  }

  /**
   * Returns the header. If the header itself is an ISymbol it will return the symbol object. If the
   * header itself is an IAST it will recursively call headSymbol(). If the head is of type
   * INumbers, the head will return one of these headers: "DoubleComplex", "Double", "Integer",
   * "Fraction", "Complex". All other objects return <code>null</code>.
   */
  @Override
  public ISymbol topHead();
}
