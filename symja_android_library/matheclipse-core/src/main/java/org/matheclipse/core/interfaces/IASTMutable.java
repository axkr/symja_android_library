package org.matheclipse.core.interfaces;

import java.util.Comparator;
import java.util.function.IntFunction;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Comparators;

/**
 * (I)nterface for the (A)bstract (S)yntax (T)ree of a given function.
 *
 * <p>
 * <code>IASTMutable</code> is an AST object where {@link IExpr} element values could be replaced by
 * new values. This operation does not change the size of the {@link IAST}. If an {@link IAST} is
 * needed, which allows appending elements use interface {@link IASTAppendable}.
 * 
 * <p>
 * <code>IASTMutable</code> objects are typically created by copying an existing IAST with method
 * {@link IAST##copy()} or new created with method {@link F#astMutable(IExpr, int)}.
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
public interface IASTMutable extends IAST {

  /**
   * Replaces the element at the specified location in this {@code IAST} with the specified object.
   * This operation does not change the size of the {@code IAST}.
   *
   * @param i the index at which to put the specified object
   * @param object the object to insert
   * @return the previous element at the index
   * @throws UnsupportedOperationException if replacing elements in this {@code IAST} is not
   *         supported
   * @throws ClassCastException if the class of an object is inappropriate for this {@code IAST}
   * @throws IllegalArgumentException if an object cannot be added to this {@code IAST}
   * @throws IndexOutOfBoundsException if {@code location < 0 || >= size()}
   */
  public IExpr set(int i, IExpr object);

  /**
   * Replaces the element at the specified location in this {@code IAST} with the result of the
   * specified function. This operation does not change the size of the {@code IAST}.
   * 
   * @param i the index at which to put and read the argument
   * @param function insert the result of <code>function.apply(get(i))</code>
   * @return
   */
  default IExpr setApply(int i, java.util.function.Function<IExpr, IExpr> function) {
    return set(i, function.apply(get(i)));
  }

  /**
   * Replaces the element at the specified location in this {@code IAST} with the specified object.
   * This operation does not change the size of the {@code IAST}. If <code>this</code> is an
   * association, the value replaces the second argument of the rule at that place
   *
   * @param location the index at which to put the specified object.
   * @param value the object to insert.
   * @return the previous element at the index.
   * @throws UnsupportedOperationException if replacing elements in this {@code IAST} is not
   *         supported.
   * @throws ClassCastException if the class of an object is inappropriate for this {@code IAST}.
   * @throws IllegalArgumentException if an object cannot be added to this {@code IAST}.
   * @throws IndexOutOfBoundsException if {@code location < 0 || >= size()}
   */
  default IExpr setValue(final int location, final IExpr value) {
    return set(location, value);
  }

  /**
   * Set the value at the position in this {@link IAST}. If <code>this</code> equals {@link F#NIL}
   * make a copy of <code>ast</code> and set the value in the copy of <code>ast</code> at the
   * position.
   * 
   * @param ast this object should be copied if <code>this</code> equals {@link F#NIL}
   * @param position the position where to set the value
   * @param value an expression unequal {@link F#NIL}
   * @return if <code>this</code> equals {@link F#NIL} return a copy of <code>ast</code> where the
   *         value is set.
   */
  public default IASTMutable setIf(IAST ast, int position, IExpr value) {
    set(position, value);
    return this;
  }

  /**
   * Set the value at the position in this {@link IAST}. If <code>this</code> equals {@link F#NIL}
   * make a copy of <code>ast</code> and set the value in the copy of <code>ast</code> at the
   * position. If value equals {@link F#NIL} return this object unchanged.
   * 
   * @param ast this object should be copied if <code>this</code> equals {@link F#NIL}
   * @param position the position where to set the value
   * @param value if {@link F#NIL} return this unchanged
   * @return if <code>this</code> equals {@link F#NIL} return a copy of <code>ast</code> where the
   *         value is set.
   */
  public default IASTMutable setIfPresent(IAST ast, int position, IExpr value) {
    if (value.isPresent()) {
      set(position, value);
    }
    return this;
  }

  public IExpr setPart(IExpr value, final int... positions);

  public default IExpr setPart(double value, final int... positions) {
    return setPart(F.num(value), positions);
  }

  /**
   * Set all elements generated by the given function from index <code>start</code> inclusive to
   * <code>end</code> exclusive.
   *
   * @param start start index (inclusive)
   * @param end end index (exclusive)
   * @param function function which generates the elements which should be set
   * @return <tt>this</tt>
   */
  default IASTMutable setArgs(int start, int end, IntFunction<IExpr> function) {
    for (int i = start; i < end; i++) {
      set(i, function.apply(i));
    }
    return this;
  }

  /**
   * Set all elements generated by the given function from index <code>1</code> inclusive to <code>
   * end</code> exclusive.
   *
   * @param end end index (exclusive)
   * @param function function which generates the elements which should be set
   * @return <tt>this</tt>
   */
  default IASTMutable setArgs(int end, IntFunction<IExpr> function) {
    return setArgs(1, end, function);
  }

  /**
   * Sort <code>this</code> in place using function {@link Comparators#CANONICAL_COMPARATOR}.
   * <b>Example:</b> suppose the Symbol f has the attribute ISymbol.ORDERLESS <code>
   * f(z,d,a,b) ==> f(a,b,d,z)</code>
   *
   * <p>
   * <b>Warning</b> only call this method in certain steps of the evaluation chain (for example for
   * evaluating attribute {@link ISymbol#ORDERLESS})
   * 
   */
  default void sortInplace() {
    sortInplace(Comparators.CANONICAL_COMPARATOR);
  }


  /**
   * Sort <code>this</code> in place using function <code>comparator#compare(a, b)</code>.
   * <b>Example:</b> suppose the Symbol f has the attribute ISymbol.ORDERLESS <code>
   * f(z,d,a,b) ==> f(a,b,d,z)</code>
   *
   * <p>
   * <b>Warning</b> only call this method in certain steps of the evaluation chain (for example for
   * evaluating attribute {@link ISymbol#ORDERLESS})
   *
   * @param comparator the comparator used for sorting inplace.
   */
  public void sortInplace(Comparator<IExpr> comparator);
}
