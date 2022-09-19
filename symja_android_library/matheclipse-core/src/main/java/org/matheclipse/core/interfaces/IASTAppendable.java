package org.matheclipse.core.interfaces;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ObjIntFunction;

/**
 * Appendable (I)nterface for the (A)bstract (S)yntax (T)ree of a given function.
 *
 * <p>
 * An AST object to which <tt>IExpr</tt> sequences and values can be appended or removed. These
 * operations typically change the size of the {@code IAST}.
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
public interface IASTAppendable extends IASTMutable {

  static final class CollectorImpl<T, A, R> implements Collector<T, A, R> {
    private final Supplier<A> supplier;
    private final BiConsumer<A, T> accumulator;
    private final BinaryOperator<A> combiner;

    CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner) {
      this.supplier = supplier;
      this.accumulator = accumulator;
      this.combiner = combiner;
    }

    @Override
    public BiConsumer<A, T> accumulator() {
      return accumulator;
    }

    @Override
    public Supplier<A> supplier() {
      return supplier;
    }

    @Override
    public BinaryOperator<A> combiner() {
      return combiner;
    }

    @Override
    public Function<A, R> finisher() {
      return i -> (R) i;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Collections.emptySet();
    }
  }

  /**
   * Returns a {@code Collector} that appends the input expressions into a {@code AST}, in encounter
   * order.
   *
   * @return a {@code Collector} that appends the input expressions into a {@code AST}, in encounter
   *         order.
   */
  public static Collector<IExpr, ?, IASTAppendable> toAST(final IExpr head,
      final int initialCapacity) {
    final Supplier<IASTAppendable> supplier = () -> F.ast(head, initialCapacity);
    return new CollectorImpl<IExpr, IASTAppendable, IASTAppendable>(supplier,
        IASTAppendable::append, (r1, r2) -> {
          r1.append(r2);
          return r1;
        });
  }

  /**
   * Returns a {@code Collector} that appends the input expressions into a {@code AST}, in encounter
   * order.
   *
   * @return a {@code Collector} that appends the input expressions into a {@code AST}, in encounter
   *         order.
   */
  public static Collector<IExpr, ?, IASTAppendable> toAST(final IExpr head) {
    return toAST(head, 5);
  }

  /**
   * Returns a {@code Collector} that appends the input expressions into the <code>appendable</code>
   *
   * @return a {@code Collector} that appends the input expressions into the <code>appendable</code>
   */
  public static Collector<IExpr, ?, IASTAppendable> toAST(final IASTAppendable appendable) {
    return new CollectorImpl<IExpr, IASTAppendable, IASTAppendable>(
        () -> appendable.copyAppendable(), IASTAppendable::append, (r1, r2) -> {
          r1.append(r2);
          return r1;
        });
  }

  /**
   * Adds the specified expression at the end of {@code this}.
   *
   * @param expr the object to add.
   * @return always true.
   * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
   * @throws ClassCastException if the class of the object is inappropriate for this {@code List}.
   * @throws IllegalArgumentException if the object cannot be added to this {@code List}.
   */
  public boolean append(IExpr expr);

  /**
   * Adds the specified expression at the end of {@code this}, if it is present.
   * 
   * @param expr the object to add.
   * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
   * @throws ClassCastException if the class of the object is inappropriate for this {@code List}.
   * @throws IllegalArgumentException if the object cannot be added to this {@code List}.
   */
  default void appendIfPresent(IExpr expr) {
    if (expr.isPresent()) {
      append(expr);
    }
  }

  /**
   * Adds the specified expression at the end of {@code this}. If it is not present, add the
   * defaultValue.
   * 
   * @param expr
   * @param defaultValue
   */
  default void appendIfPresent(IExpr expr, IExpr defaultValue) {
    if (expr.isPresent()) {
      append(expr);
    } else {
      append(defaultValue);
    }
  }

  /**
   * Adds the specified long value at the end of this {@code List}.
   *
   * @param value the long value which should be appended.
   * @return always true.
   * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
   * @throws ClassCastException if the class of the object is inappropriate for this {@code List}.
   * @throws IllegalArgumentException if the object cannot be added to this {@code List}.
   */
  default boolean append(long value) {
    return append(F.ZZ(value));
  }

  /**
   * Iterates over the lists elements and calls the function. Append the functions result expression
   * at the end of <code>this</code>, if the function results is not equal {@link F#NIL}. If the
   * function results is <code>null</code> stop iterating and return <code>false</code>.
   * 
   * @param list
   * @param function function those <code>apply(x)</code> method will be called with each element of
   *        the list.
   */
  default <T extends IExpr> boolean append(final IAST list, Function<T, IExpr> function) {
    for (int i = 1; i < list.size(); i++) {
      T arg = (T) list.getRule(i);
      IExpr temp = function.apply(arg);
      if (temp == null) {
        return false;
      }
      appendIfPresent(temp);
    }
    return true;
  }

  default <T extends IExpr> boolean append(final IAST list, int start, int end,
      Function<T, IExpr> function) {
    for (int i = start; i < end; i++) {
      T arg = (T) list.getRule(i);
      IExpr temp = function.apply(arg);
      if (temp == null) {
        return false;
      }
      appendIfPresent(temp);
    }
    return true;
  }

  default <T extends IExpr> boolean append(final IAST list, Function<T, IExpr> function,
      Predicate<T> predicate) {
    for (int i = 1; i < list.size(); i++) {
      T arg = (T) list.getRule(i);
      if (predicate.test(arg)) {
        IExpr temp = function.apply(arg);
        if (temp == null) {
          return false;
        }
        appendIfPresent(temp);
      }
    }
    return true;
  }

  default <T extends IExpr> boolean append(final IAST list, int start, int end,
      Function<T, IExpr> function, Predicate<T> predicate) {
    for (int i = start; i < end; i++) {
      T arg = (T) list.getRule(i);
      if (predicate.test(arg)) {
        IExpr temp = function.apply(arg);
        if (temp == null) {
          return false;
        }
        appendIfPresent(temp);
      }
    }
    return true;
  }

  /**
   * Iterates over the lists elements and calls the function. Append the functions result expression
   * at the end of <code>this</code>, if the function results is not equal {@link F#NIL}. If the
   * function results is <code>null</code> stop iterating and return <code>false</code>.
   * 
   * @param list
   * @param function
   */
  default boolean append(final IAST list, ObjIntFunction<IExpr, IExpr> function) {
    for (int i = 1; i < list.size(); i++) {
      IExpr temp = function.apply(list.getRule(i), i);
      if (temp == null) {
        return false;
      }
      appendIfPresent(temp);
    }
    return true;
  }

  /**
   * Iterates over the lists elements and calls the function. Append the functions result expression
   * at the end of <code>this</code>, if the function results is not equal {@link F#NIL}.
   * 
   * @param list
   * @param function
   */
  default <T> boolean append(final List<T> list, Function<T, IExpr> function) {
    for (int i = 0; i < list.size(); i++) {
      IExpr temp = function.apply(list.get(i));
      if (temp == null) {
        return false;
      }
      appendIfPresent(temp);
    }
    return true;
  }

  default <T> boolean append(final List<T> list, Function<T, IExpr> function,
      Predicate<T> predicate) {

    for (int i = 0; i < list.size(); i++) {
      T arg = list.get(i);
      if (predicate.test(arg)) {
        IExpr temp = function.apply(arg);
        if (temp == null) {
          return false;
        }
        appendIfPresent(temp);
      }
    }
    return true;
  }

  /**
   * Iterates over the elements of the <code>exprSet</code> and calls the function. Append the
   * functions result expression at the end of <code>this</code>, if the function results is not
   * equal {@link F#NIL}.
   * 
   * @param exprSet
   * @param function
   */
  default boolean append(final Set<? extends IExpr> exprSet, Function<IExpr, IExpr> function) {
    final Iterator<? extends IExpr> iter = exprSet.iterator();
    while (iter.hasNext()) {
      IExpr temp = function.apply(iter.next());
      if (temp == null) {
        return false;
      }
      appendIfPresent(temp);
    }
    return true;
  }

  /**
   * Iterates over the maps entries and calls the binary function with the key value pair of the
   * entry. Append the functions result expression at the end of <code>this</code>, if the function
   * results unequals {@link F#NIL}.
   * 
   * @param map
   * @param biFunction binary function those <code>apply(key,value)</code> method will be called
   *        with the key, value arguments of the maps entries
   */
  default boolean append(final Map<? extends IExpr, ? extends IExpr> map,
      BiFunction<IExpr, IExpr, IExpr> biFunction) {
    for (Entry<? extends IExpr, ? extends IExpr> entry : map.entrySet()) {
      IExpr temp = biFunction.apply(entry.getKey(), entry.getValue());
      if (temp == null) {
        return false;
      }
      appendIfPresent(temp);
    }
    return true;
  }

  default <T extends IExpr> boolean append(final int start, final int end,
      IntFunction<T> function) {
    for (int i = start; i < end; i++) {
      T temp = function.apply(i);
      if (temp == null) {
        return false;
      }
      appendIfPresent(temp);
    }
    return true;
  }

  /**
   * Adds the specified double value at the end of this {@code List}.
   *
   * @param value the double value which should be appended.
   * @return always true.
   * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
   * @throws ClassCastException if the class of the object is inappropriate for this {@code List}.
   * @throws IllegalArgumentException if the object cannot be added to this {@code List}.
   */
  default boolean append(double value) {
    return append(F.num(value));
  }

  /**
   * Adds the specified string value at the end of this {@code List}.
   *
   * @param value the string value which should be appended.
   * @return always true.
   * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
   * @throws ClassCastException if the class of the object is inappropriate for this {@code List}.
   * @throws IllegalArgumentException if the object cannot be added to this {@code List}.
   */
  default boolean append(String value) {
    return append(F.stringx(value));
  }

  /**
   * Adds the specified character value at the end of this {@code List}.
   *
   * @param value the character value which should be appended.
   * @return always true.
   * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
   * @throws ClassCastException if the class of the object is inappropriate for this {@code List}.
   * @throws IllegalArgumentException if the object cannot be added to this {@code List}.
   */
  default boolean append(char value) {
    return append(F.stringx(value));
  }

  /**
   * Adds the specified boolean value at the end of this {@code List}.
   *
   * @param value the boolean value which should be appended.
   * @return always true.
   * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
   * @throws ClassCastException if the class of the object is inappropriate for this {@code List}.
   * @throws IllegalArgumentException if the object cannot be added to this {@code List}.
   */
  default boolean append(boolean value) {
    return append(F.booleSymbol(value));
  }

  /**
   * Inserts the specified object into this {@code List} at the specified location. The object is
   * inserted before the current element at the specified location. If the location is equal to the
   * size of this {@code List}, the object is added at the end. If the location is smaller than the
   * size of this {@code List}, then all elements beyond the specified location are moved by one
   * position towards the end of the {@code List}.
   *
   * @param location the index at which to insert.
   * @param object the object to add.
   * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
   * @throws ClassCastException if the class of the object is inappropriate for this {@code List}.
   * @throws IllegalArgumentException if the object cannot be added to this {@code List}.
   * @throws IndexOutOfBoundsException if {@code location < 0 || location > size()}
   */
  public void append(int location, IExpr object);

  /**
   * Inserts the specified long value into this {@code List} at the specified location. The object
   * is inserted before the current element at the specified location. If the location is equal to
   * the size of this {@code List}, the object is added at the end. If the location is smaller than
   * the size of this {@code List}, then all elements beyond the specified location are moved by one
   * position towards the end of the {@code List}.
   *
   * @param location the index at which to insert.
   * @param value the long value which should be added
   * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
   * @throws ClassCastException if the class of the object is inappropriate for this {@code List}.
   * @throws IllegalArgumentException if the object cannot be added to this {@code List}.
   * @throws IndexOutOfBoundsException if {@code location < 0 || location > size()}
   */
  default void append(int location, long value) {
    append(location, F.ZZ(value));
  }

  /**
   * Adds the objects in the specified collection to the end of this {@code List}. The objects are
   * added in the order in which they are returned from the collection's iterator.
   *
   * @param collection the collection of objects.
   * @return {@code true} if this {@code List} is modified, {@code false} otherwise (i.e. if the
   *         passed collection was empty).
   * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
   * @throws ClassCastException if the class of an object is inappropriate for this {@code List}.
   * @throws IllegalArgumentException if an object cannot be added to this {@code List}.
   */
  public boolean appendAll(Collection<? extends IExpr> collection);

  /**
   * Adds the mappings in the specified map as <code>Rule(...)</code> to the end of this {@code
   * List}. The objects are added in the order in which they are returned from the map's iterator.
   *
   * @param map
   * @return
   */
  public boolean appendAll(Map<? extends IExpr, ? extends IExpr> map);

  /**
   * Appends all elements from offset <code>startPosition</code> to <code>endPosition</code> in the
   * specified AST to the end of this AST.
   *
   * @param ast AST containing elements to be added to this AST
   * @param startPosition the start position, inclusive.
   * @param endPosition the ending position, exclusive.
   * @return <tt>true</tt> if this AST changed as a result of the call
   */
  public boolean appendAll(IAST ast, int startPosition, int endPosition);

  /**
   * Inserts the objects in the specified collection at the specified location in this AST. The
   * objects are added in the order they are returned from the collection's iterator.
   *
   * @param location the index at which to insert.
   * @param collection the collection of objects.
   * @return {@code true} if this {@code ArrayList} is modified, {@code false} otherwise.
   * @throws IndexOutOfBoundsException when {@code location < 0 || > size()}
   */
  public boolean appendAll(int location, Collection<? extends IExpr> collection);

  /**
   * Appends all elements from offset <code>startPosition</code> to <code>endPosition</code> in the
   * specified list to the end of this AST.
   *
   * @param list list containing elements to be added to this AST
   * @param startPosition the start position, inclusive.
   * @param endPosition the ending position, exclusive.
   * @return <tt>true</tt> if this AST changed as a result of the call
   */
  public boolean appendAll(List<? extends IExpr> list, int startPosition, int endPosition);

  /**
   * Appends all elements from offset <code>startPosition</code> to <code>endPosition</code> in the
   * specified list to the end of this AST.
   *
   * @param args array containing elements to be added to this AST
   * @param startPosition the start position, inclusive.
   * @param endPosition the ending position, exclusive.
   * @return <tt>true</tt> if this AST changed as a result of the call
   */
  public boolean appendAll(IExpr[] args, int startPosition, int endPosition);

  /**
   * Appends all of the arguments (starting from offset <code>1</code>) in the specified <code>ast
   * </code> to the end of <code>this</code> AST.
   *
   * @param ast AST containing elements to be added to this AST
   * @return <tt>true</tt> if this AST changed as a result of the call
   */
  public boolean appendArgs(IAST ast);

  /**
   * Appends all of the arguments (starting from offset <code>1</code>) in the specified AST up to
   * position <code>untilPosition</code> exclusive.
   *
   * @param ast AST containing elements to be added to this AST
   * @param untilPosition append all argumments of ast up to position <code>untilPosition</code>
   *        exclusive.
   * @return <tt>true</tt> if this AST changed as a result of the call
   */
  public boolean appendArgs(IAST ast, int untilPosition);

  /**
   * Appends all elements generated by the given {@link IntFunction#apply(int)} method from index
   * <code>start</code> inclusive to <code>end</code> exclusive. If the result of the
   * {@link IntFunction#apply(int)} method is equal {@link F#NIL} then stop appending further
   * entries.
   *
   * @param start start index (inclusive)
   * @param end end index (exclusive)
   * @param function function those {@link IntFunction#apply(int)} method will be called with each
   *        number in the range. If the {@link IntFunction#apply(int)} method returns {@link F#NIL}
   *        then stop appending further entries
   * @return <tt>this</tt>
   */
  public IASTAppendable appendArgs(int start, int end, IntFunction<IExpr> function);

  /**
   * Appends all elements generated by the given function from index <code>1</code> inclusive to
   * <code>end</code> exclusive. Calls method <code>appendArgs(1, end, function)</code>.
   *
   * @param end the end limit index (exclusive)
   * @param function function which generates the elements which should be appended
   * @return <tt>this</tt>
   */
  default IASTAppendable appendArgs(int end, IntFunction<IExpr> function) {
    return appendArgs(1, end, function);
  }

  /**
   * Append an <code>subAST</code> with attribute <code>OneIdentity</code> for example Plus[] or
   * Times[].
   *
   * @param subAST an ast with attribute <code>OneIdentity</code>.
   * @return <code>this</code> ast after adding the subAST
   */
  public IAST appendOneIdentity(IAST subAST);

  /**
   * Adds the specified rule at the end of this association.
   *
   * @param expr the rule to add at the end of this association
   * @return always true
   */
  default void appendRule(IExpr expr) {
    append(expr);
  }

  /**
   * Adds the specified rule at the start of this association.
   *
   * @param rule the rule to add at the end of this association
   * @return always true
   */
  default void prependRule(IExpr rule) {
    append(1, rule);
  }

  /** Removes all elements from this {@code IAST}, leaving it empty (optional). */
  public void clear();

  /**
   * If this expression unequals <code>F.NIL</code>, invoke the specified consumer with this <code>
   * IASTAppendable</code> object, otherwise do nothing.
   *
   * @param consumer block to be executed if this expression unequals <code>F.NIL</code>
   * @see java.util.Optional#ifPresent(Consumer)
   */
  default void ifAppendable(Consumer<? super IASTAppendable> consumer) {
    consumer.accept(this);
  }

  /**
   * Removes the object at the specified location from this {@code IAST}.
   *
   * @param location the index of the object to remove.
   * @return the removed object.
   * @throws UnsupportedOperationException if removing from this {@code IAST} is not supported.
   * @throws IndexOutOfBoundsException if {@code location < 0 || >= size()}
   */
  public IExpr remove(int location);

  default boolean remove(IExpr expr) {
    int indexOf = indexOf(expr);
    if (indexOf >= 0) {
      remove(indexOf);
      return true;
    }
    return false;
  }

  /**
   * Removes the objects in the specified range from the start to the end, but not including the end
   * index.
   *
   * @param start the index at which to start removing.
   * @param end the index one after the end of the range to remove. * @throws
   *        UnsupportedOperationException if removing from this {@code IAST} is not supported.
   * @throws IndexOutOfBoundsException when {@code start < 0, start > end} or {@code end > size()}
   */
  public void removeRange(int start, int end);
}
