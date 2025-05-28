/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.generic.ObjIntPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * HMArrayList is an implementation of a list, backed by an array. All optional operations adding,
 * removing, and replacing are supported. The elements can be any objects.
 */
public abstract class HMArrayList extends AbstractAST
    implements IASTAppendable, Serializable, RandomAccess {

  private static final long serialVersionUID = 8683452581122892189L;

  protected transient IExpr[] array;

  transient int firstIndex;

  protected transient int lastIndex;

  /** Constructs a new instance of list with capacity <code>10</code>. */
  /* package protected */ HMArrayList() {
    this(10);
  }

  /**
   * Constructs a new list containing the elements of the specified collection. The initial size of
   * the {@code ArrayList} will be 10% higher than the size of the specified collection.
   *
   * @param collection the collection of elements to add.
   */
  /* package protected */ HMArrayList(Collection<? extends IExpr> collection) {
    firstIndex = hashValue = 0;
    Object[] objects = collection.toArray();
    int size = objects.length;
    array = newElementArray(size + (size / 10));
    System.arraycopy(objects, 0, array, 0, size);
    lastIndex = size;
  }

  /**
   * Constructs a new list by allocating <code>1 + arguments.length</code> new elements for the
   * list. The list contains the <code>headExpr</code> as element at offset <code>0</code> and the
   * arguments at offset <code>1 .. arguments.length</code> in the list.
   *
   * @param headExpr the header expression
   * @param arguments the argument expressions
   */
  /* package protected */ HMArrayList(IExpr headExpr, IExpr... arguments) {
    firstIndex = hashValue = 0;
    lastIndex = arguments.length + 1;
    switch (lastIndex) {
      case 1:
        array = new IExpr[] {headExpr};
        break;
      case 2:
        array = new IExpr[] {headExpr, arguments[0]};
        break;
      case 3:
        array = new IExpr[] {headExpr, arguments[0], arguments[1]};
        break;
      case 4:
        array = new IExpr[] {headExpr, arguments[0], arguments[1], arguments[2]};
        break;
      case 5:
        array = new IExpr[] {headExpr, arguments[0], arguments[1], arguments[2], arguments[3]};
        break;
      case 6:
        array = new IExpr[] {headExpr, arguments[0], arguments[1], arguments[2], arguments[3],
            arguments[4]};
        break;
      case 7:
        array = new IExpr[] {headExpr, arguments[0], arguments[1], arguments[2], arguments[3],
            arguments[4], arguments[5]};
        break;
      case 8:
        array = new IExpr[] {headExpr, arguments[0], arguments[1], arguments[2], arguments[3],
            arguments[4], arguments[5], arguments[6]};
        break;
      case 9:
        array = new IExpr[] {headExpr, arguments[0], arguments[1], arguments[2], arguments[3],
            arguments[4], arguments[5], arguments[6], arguments[7]};
        break;
      default:
        array = newElementArray(lastIndex);
        array[0] = headExpr;
        System.arraycopy(arguments, 0, array, 1, lastIndex - 1);
    }
  }

  /**
   * Constructs a new list assigning the given <code>array</code> to this lists array. No new memory
   * is allocated for the list. The list contains the arrays elements form offset <code>0</code> to
   * offset <code>array.length-1</code> in the list.
   *
   * @param array the array which will be used to store this lists elements.
   */
  protected HMArrayList(IExpr[] array) {
    this.array = array;
    this.firstIndex = this.hashValue = 0;
    this.lastIndex = array.length;
  }

  /**
   * Constructs a new instance of {@code HMArrayList} with the specified capacity.
   *
   * @param capacity the initial capacity of this {@code ArrayList}.
   */
  public HMArrayList(int capacity) throws IllegalArgumentException {
    if (capacity < 0) {
      throw new IllegalArgumentException();
    }
    firstIndex = lastIndex = hashValue = 0;
    if (capacity > 0) {
      array = newElementArray(capacity);
    }
  }

  /**
   * Adds the specified object at the end of this {@code ArrayList}.
   *
   * @param object the object to add.
   * @return always true
   */
  @Override
  public boolean append(IExpr object) {
    hashValue = 0;
    if (lastIndex == array.length) {
      growAtEnd(2);
    }
    array[lastIndex++] = object;
    return true;
  }

  /**
   * Inserts the specified object into this {@code ArrayList} at the specified location. The object
   * is inserted before any previous element at the specified location. If the location is equal to
   * the size of this {@code ArrayList}, the object is added at the end.
   *
   * @param location the index at which to insert the object.
   * @param object the object to add.
   * @throws IndexOutOfBoundsException when {@code location < 0 || > size()}
   */
  @Override
  public final void append(int location, IExpr object) {
    hashValue = 0;
    int size = lastIndex - firstIndex;
    if (0 < location && location < size) {
      if (firstIndex == 0 && lastIndex == array.length) {
        growForInsert(location, 1);
      } else if ((location < size / 2 && firstIndex > 0) || lastIndex == array.length) {
        System.arraycopy(array, firstIndex, array, --firstIndex, location);
      } else {
        int index = location + firstIndex;
        System.arraycopy(array, index, array, index + 1, size - location);
        lastIndex++;
      }
      array[location + firstIndex] = object;
    } else if (location == 0) {
      if (firstIndex == 0) {
        growAtFront(1);
      }
      array[--firstIndex] = object;
    } else if (location == size) {
      if (lastIndex == array.length) {
        growAtEnd(1);
      }
      array[lastIndex++] = object;
    } else {
      throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: "
          + Integer.valueOf(lastIndex - firstIndex));
    }
  }

  /**
   * Adds the objects in the specified collection to this {@code ArrayList}.
   *
   * @param collection the collection of objects.
   * @return {@code true} if this {@code ArrayList} is modified, {@code false} otherwise.
   */
  @Override
  public boolean appendAll(Collection<? extends IExpr> collection) {
    hashValue = 0;
    Object[] dumpArray = collection.toArray();
    if (dumpArray.length == 0) {
      return false;
    }
    if (dumpArray.length > array.length - lastIndex) {
      growAtEnd(dumpArray.length);
    }
    System.arraycopy(dumpArray, 0, this.array, lastIndex, dumpArray.length);
    lastIndex += dumpArray.length;
    return true;
  }

  @Override
  public boolean appendAll(Map<? extends IExpr, ? extends IExpr> map) {
    for (Map.Entry<? extends IExpr, ? extends IExpr> entry : map.entrySet()) {
      append(F.Rule(entry.getKey(), entry.getValue()));
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean appendAll(IAST ast, int startPosition, int endPosition) {
    if (ast.size() > 0 && startPosition < endPosition) {
      hashValue = 0;
      int length = endPosition - startPosition;
      if (length > array.length - lastIndex) {
        growAtEnd(length);
      }
      for (int i = startPosition; i < endPosition; i++) {
        array[lastIndex++] = ast.get(i);
      }
      return true;

      // ensureCapacity(size() + (endPosition - startPosition));
      // for (int i = startPosition; i < endPosition; i++) {
      // append(ast.get(i));
      // }
      // return true;
    }
    return false;
  }

  /**
   * Inserts the objects in the specified collection at the specified location in this List. The
   * objects are added in the order they are returned from the collection's iterator.
   *
   * @param location the index at which to insert.
   * @param collection the collection of objects.
   * @return {@code true} if this {@code ArrayList} is modified, {@code false} otherwise.
   * @throws IndexOutOfBoundsException when {@code location < 0 || > size()}
   */
  @Override
  public boolean appendAll(int location, Collection<? extends IExpr> collection) {
    hashValue = 0;
    int size = lastIndex - firstIndex;
    if (location < 0 || location > size) {
      throw new IndexOutOfBoundsException(
          "Index: " + location + ", Size: " + Integer.valueOf(lastIndex - firstIndex));
    }
    Object[] dumparray = collection.toArray();
    int growSize = dumparray.length;
    if (growSize == 0) {
      return false;
    }

    if (0 < location && location < size) {
      if (array.length - size < growSize) {
        growForInsert(location, growSize);
      } else if ((location < size / 2 && firstIndex > 0) || lastIndex > array.length - growSize) {
        int newFirst = firstIndex - growSize;
        if (newFirst < 0) {
          int index = location + firstIndex;
          System.arraycopy(array, index, array, index - newFirst, size - location);
          lastIndex -= newFirst;
          newFirst = 0;
        }
        System.arraycopy(array, firstIndex, array, newFirst, location);
        firstIndex = newFirst;
      } else {
        int index = location + firstIndex;
        System.arraycopy(array, index, array, index + growSize, size - location);
        lastIndex += growSize;
      }
    } else if (location == 0) {
      growAtFront(growSize);
      firstIndex -= growSize;
    } else if (location == size) {
      if (lastIndex > array.length - growSize) {
        growAtEnd(growSize);
      }
      lastIndex += growSize;
    }

    System.arraycopy(dumparray, 0, this.array, location + firstIndex, growSize);
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean appendAll(List<? extends IExpr> list, int startPosition, int endPosition) {
    if (list.size() > 0 && startPosition < endPosition) {
      hashValue = 0;
      int length = endPosition - startPosition;
      if (length > array.length - lastIndex) {
        growAtEnd(length);
      }
      for (int i = startPosition; i < endPosition; i++) {
        array[lastIndex++] = list.get(i);
      }
      return true;

      // ensureCapacity(size() + (endPosition - startPosition));
      // for (int i = startPosition; i < endPosition; i++) {
      // append(ast.get(i));
      // }
      // return true;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean appendAll(IExpr[] args, int startPosition, int endPosition) {
    if (args.length > 0 && startPosition < endPosition) {
      hashValue = 0;
      int length = endPosition - startPosition;
      if (length > array.length - lastIndex) {
        growAtEnd(length);
      }
      for (int i = startPosition; i < endPosition; i++) {
        array[lastIndex++] = args[i];
      }
      return true;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(int startOffset, int endOffset, Consumer<? super IExpr> action) {
    int index = firstIndex + startOffset;
    if (index < lastIndex) {
      for (int i = startOffset; i < endOffset; i++) {
        action.accept(array[index++]);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(int startOffset, int endOffset, ObjIntConsumer<? super IExpr> action) {
    int index = firstIndex + startOffset;
    if (index < lastIndex) {
      for (int i = startOffset; i < endOffset; i++) {
        action.accept(array[index++], i);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final boolean appendArgs(IAST ast) {
    return appendArgs(ast, ast.size());
  }

  /** {@inheritDoc} */
  @Override
  public final boolean appendArgs(IAST ast, int untilPosition) {
    if (untilPosition > 1) {
      hashValue = 0;
      int length = untilPosition - 1;
      if (length > array.length - lastIndex) {
        growAtEnd(length);
      }
      for (int i = 1; i < untilPosition; i++) {
        array[lastIndex++] = ast.get(i);
      }
      return true;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public IASTAppendable appendArgs(final int start, final int end, IntFunction<IExpr> function) {
    if (start >= end) {
      return this;
    }
    hashValue = 0;
    int length = end - start;
    if (length > array.length - lastIndex) {
      growAtEnd(length);
    }
    for (int i = start; i < end; i++) {
      IExpr temp = function.apply(i);
      if (temp.isPresent()) {
        array[lastIndex++] = temp;
        continue;
      }
      break;
    }
    return this;
  }

  /**
   * Get the first argument (i.e. the second element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(1) ).
   * <p>
   * <b>Example:</b> for the AST representing the expression <code>Sin(x)</code>, <code>arg1()
   * </code> returns <code>x</code>.
   *
   * @return the first argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg1() {
    return array[firstIndex + 1];
  }

  /**
   * Get the second argument (i.e. the third element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(2) ). <br>
   * <b>Example:</b> for the AST representing the expression <code>x^y</code> (i.e. <code>
   * Power(x, y)</code>), <code>arg2()</code> returns <code>y</code>.
   *
   * @return the second argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg2() {
    return array[firstIndex + 2];
  }

  /**
   * Get the third argument (i.e. the fourth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(3) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b, c)</code>, <code>arg3()
   * </code> returns <code>c</code>.
   *
   * @return the third argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg3() {
    return array[firstIndex + 3];
  }

  /**
   * Get the fourth argument (i.e. the fifth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(4) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d)</code>, <code>
   * arg4()</code> returns <code>d</code>.
   *
   * @return the fourth argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg4() {
    return array[firstIndex + 4];
  }

  /**
   * Get the fifth argument (i.e. the sixth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(5) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d, e)</code>, <code>
   * arg5()</code> returns <code>e</code> .
   *
   * @return the fifth argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg5() {
    return array[firstIndex + 5];
  }

  /** {@inheritDoc} */
  @Override
  public final int argSize() {
    return lastIndex - firstIndex - 1;
  }

  @Override
  public SortedSet<IExpr> asSortedSet(Comparator<? super IExpr> comparator) {
    int size = size();
    SortedSet<IExpr> set = new TreeSet<>(comparator);
    for (int i = 1; i < size; i++) {
      set.add(array[firstIndex + i]);
    }
    return set;
  }

  /**
   * Removes all elements from this {@code ArrayList}, leaving it empty.
   *
   * @see #isEmpty
   * @see #size
   */
  @Override
  public void clear() {
    if (firstIndex != lastIndex) {
      Arrays.fill(array, firstIndex, lastIndex, null);
      firstIndex = lastIndex = 0;
    }
    hashValue = 0;
  }

  /**
   * Ensures that after this operation the {@code ArrayList} can hold the specified number of
   * elements without further growing.
   *
   * @param minimumCapacity the minimum capacity asked for.
   */
  public void ensureCapacity(int minimumCapacity) {
    if (array.length < minimumCapacity) {
      if (firstIndex > 0) {
        growAtFront(minimumCapacity - array.length);
      } else {
        growAtEnd(minimumCapacity - array.length);
      }
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (hashCode() != obj.hashCode()) {
      return false;
    }
    if (obj instanceof AbstractAST) {
      final IAST ast = (AbstractAST) obj;
      final int size = lastIndex - firstIndex;
      if (size != ast.size()) {
        return false;
      }
      final IExpr head = array[firstIndex];
      if (head instanceof ISymbol) {
        if (head != ast.head()) {
          // compared with ISymbol object identity
          return false;
        }
      } else if (!head.equals(ast.head())) {
        return false;
      }
      return forAll((x, i) -> x.equals(ast.getRule(i)), 1);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean exists(Predicate<? super IExpr> predicate, int startOffset) {
    int i = firstIndex + startOffset;
    while (i < lastIndex) {
      if (predicate.test(array[i++])) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean existsLeft(BiPredicate<IExpr, IExpr> stopPredicate) {
    int i = firstIndex + 2;
    while (i < lastIndex) {
      if (stopPredicate.test(array[i - 1], array[i])) {
        return true;
      }
      i++;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean exists(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
    int i = firstIndex + startOffset;
    int j = startOffset;
    while (i < lastIndex) {
      if (predicate.test(array[i++], j++)) {
        return true;
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final IAST filter(IASTAppendable filterAST, IASTAppendable restAST,
      Predicate<? super IExpr> predicate) {
    for (int i = firstIndex + 1; i < lastIndex; i++) {
      final IExpr temp = array[i];
      if (predicate.test(temp)) {
        filterAST.append(temp);
      } else {
        restAST.append(temp);
      }
    }
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public final IAST filterFunction(IASTAppendable filterAST, IASTAppendable restAST,
      final Function<IExpr, IExpr> function) {
    for (int i = firstIndex + 1; i < lastIndex; i++) {
      IExpr temp = array[i];
      IExpr expr = function.apply(temp);
      if (expr.isPresent()) {
        filterAST.append(expr);
      } else {
        restAST.append(temp);
      }
    }
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(Predicate<? super IExpr> predicate, int startOffset) {
    int i = firstIndex + startOffset;
    while (i < lastIndex) {
      if (!predicate.test(array[i++])) {
        return false;
      }
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
    int i = firstIndex + startOffset;
    int j = startOffset;
    while (i < lastIndex) {
      if (!predicate.test(array[i++], j++)) {
        return false;
      }
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(Consumer<? super IExpr> action, int startOffset) {
    int i = firstIndex + startOffset;
    while (i < lastIndex) {
      action.accept(array[i++]);
    }
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(final IExpr expr) {
    int index = 1;
    int i = firstIndex + 1;
    while (i < lastIndex) {
      if (array[i++].equals(expr)) {
        return index;
      }
      index++;
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(Predicate<? super IExpr> predicate, int fromIndex) {
    int index = fromIndex;
    int i = firstIndex + index;
    while (i < lastIndex) {
      if (predicate.test(array[i++])) {
        return index;
      }
      index++;
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr findFirst(Function<IExpr, IExpr> function) {
    int i = firstIndex + 1;
    while (i < lastIndex) {
      IExpr temp = function.apply(array[i++]);
      if (temp.isPresent()) {
        return temp;
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr get(int location) {
    if (Config.FUZZ_TESTING) {
      int index;
      if ((index = firstIndex + location) < lastIndex) {
        return array[index];
      }
      throw new IndexOutOfBoundsException(
          "Index: " + location + ", Size: " + (lastIndex - firstIndex));
    }
    return array[firstIndex + location];
  }

  private void growAtEnd(int required) {
    int size = lastIndex - firstIndex;
    if (firstIndex >= required - (array.length - lastIndex)) {
      int newLast = lastIndex - firstIndex;
      if (size > 0) {
        System.arraycopy(array, firstIndex, array, 0, size);
        int start = newLast < firstIndex ? firstIndex : newLast;
        Arrays.fill(array, start, array.length, null);
      }
      firstIndex = 0;
      lastIndex = newLast;
    } else {
      // if (Config.FUZZ_TESTING) {
      // throw new NullPointerException();
      // }
      int increment = size / 2;
      if (required > increment) {
        increment = required;
      }
      if (increment < 12) {
        increment = 12;
      }
      IExpr[] newArray = newElementArray(size + increment);
      if (size > 0) {
        System.arraycopy(array, firstIndex, newArray, 0, size);
        firstIndex = 0;
        lastIndex = size;
      }
      array = newArray;
    }
  }

  private void growAtFront(int required) {
    int size = lastIndex - firstIndex;
    if (array.length - lastIndex + firstIndex >= required) {
      int newFirst = array.length - size;
      if (size > 0) {
        System.arraycopy(array, firstIndex, array, newFirst, size);
        int length = firstIndex + size > newFirst ? newFirst : firstIndex + size;
        Arrays.fill(array, firstIndex, length, null);
      }
      firstIndex = newFirst;
      lastIndex = array.length;
    } else {
      int increment = size / 2;
      if (required > increment) {
        increment = required;
      }
      if (increment < 12) {
        increment = 12;
      }
      IExpr[] newArray = newElementArray(size + increment);
      if (size > 0) {
        System.arraycopy(array, firstIndex, newArray, newArray.length - size, size);
      }
      firstIndex = newArray.length - size;
      lastIndex = newArray.length;
      array = newArray;
    }
  }

  private void growForInsert(int location, int required) {
    int size = lastIndex - firstIndex;
    int increment = size / 2;
    if (required > increment) {
      increment = required;
    }
    if (increment < 12) {
      increment = 12;
    }
    IExpr[] newArray = newElementArray(size + increment);
    int newFirst = increment - required;
    // Copy elements after location to the new array skipping inserted
    // elements
    System.arraycopy(array, location + firstIndex, newArray, newFirst + location + required,
        size - location);
    // Copy elements before location to the new array from firstIndex
    System.arraycopy(array, firstIndex, newArray, newFirst, location);
    firstIndex = newFirst;
    lastIndex = size + increment;

    array = newArray;
  }

  @Override
  public int hashCode() {
    if (hashValue == 0) {
      int size = size();
      if (size <= 3) {
        hashValue = (0x811c9dc5 * 16777619) ^ (size & 0xff); // decimal 2166136261;
        for (int i = firstIndex; i < lastIndex; i++) {
          hashValue = (hashValue * 16777619) ^ (array[i].hashCode() & 0xff);
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

  @Override
  public final IExpr head() {
    return array[firstIndex];
  }

  /**
   * Constructs a new list assigning the given <code>array</code> to this lists array. No new memory
   * is allocated for the list. The list contains the arrays elements from offset <code>0</code> to
   * offset <code>array.length-1</code> in the list.
   *
   * @param array the array which will be used to store this lists elements.
   */
  protected final void init(IExpr[] array) {
    this.array = array;
    this.firstIndex = this.hashValue = 0;
    this.lastIndex = array.length;
  }

  @Override
  public IAST mapReverse(final Function<IExpr, IExpr> function) {
    IASTMutable result = F.NIL;
    int i = firstIndex + 1;
    int j = lastIndex - 1;
    while (i < lastIndex) {
      IExpr temp = function.apply(array[i++]);
      if (temp.isPresent()) {
        // something was evaluated - return a new IAST:
        result = copy();
        result.set(j--, temp);
        break;
      }
      j--;
    }
    if (result.isPresent()) {
      while (i < lastIndex) {
        IExpr temp = function.apply(array[i++]);
        if (temp.isPresent()) {
          result.set(j, temp);
        }
        j--;
      }
      return result;
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public final IAST map(final IASTMutable clonedResultAST, final Function<IExpr, IExpr> function) {
    int j = 1;
    for (int i = firstIndex + 1; i < lastIndex; i++) {
      IExpr temp = function.apply(array[i]);
      if (temp != null) {
        clonedResultAST.set(j, temp);
      }
      j++;
    }
    return clonedResultAST;
  }

  /** {@inheritDoc} */
  @Override
  public final IASTAppendable mapThreadEvaled(EvalEngine engine, IASTAppendable appendAST,
      final IAST replacement, int position) {
    // final Function<IExpr, IExpr> function = Functors.replaceArg(replacement,
    // position);
    final Function<IExpr, IExpr> function = x -> replacement.setAtCopy(position, x).eval(engine);
    IExpr temp;
    for (int i = firstIndex + 1; i < lastIndex; i++) {
      temp = function.apply(array[i]);
      if (temp != null) {
        appendAST.append(temp);
      }
    }
    return appendAST;
  }

  private static IExpr[] newElementArray(int size) {
    if (Config.MAX_AST_SIZE < size) {
      throw new ASTElementLimitExceeded(size);
    }
    return new IExpr[size];
  }

  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField fields = stream.readFields();
    lastIndex = fields.get("size", 0); // $NON-NLS-1$
    array = newElementArray(lastIndex);
    for (int i = 0; i < lastIndex; i++) {
      array[i] = (IExpr) stream.readObject();
    }
  }

  /**
   * Removes the object at the specified location from this list.
   *
   * @param location the index of the object to remove.
   * @return the removed object.
   * @throws IndexOutOfBoundsException when {@code location < 0 || >= size()}
   */
  @Override
  public IExpr remove(int location) {
    if (Config.FUZZ_TESTING && isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
      throw new NullPointerException("Index: " + location);
    }
    hashValue = 0;
    IExpr result;
    int size = lastIndex - firstIndex;
    if (0 <= location && location < size) {
      if (location == size - 1) {
        result = array[--lastIndex];
        array[lastIndex] = null;
      } else if (location == 0) {
        result = array[firstIndex];
        array[firstIndex++] = null;
      } else {
        int elementIndex = firstIndex + location;
        result = array[elementIndex];
        if (location < size / 2) {
          System.arraycopy(array, firstIndex, array, firstIndex + 1, location);
          array[firstIndex++] = null;
        } else {
          System.arraycopy(array, elementIndex + 1, array, elementIndex, size - location - 1);
          array[--lastIndex] = null;
        }
      }
      if (firstIndex == lastIndex) {
        firstIndex = lastIndex = 0;
      }
    } else {
      throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: "
          + Integer.valueOf(lastIndex - firstIndex));
    }

    return result;
  }

  /**
   * Removes the objects in the specified range from the start to the end, but not including the end
   * index.
   *
   * @param start the index at which to start removing.
   * @param end the index one after the end of the range to remove.
   * @throws IndexOutOfBoundsException when {@code start < 0, start > end} or {@code end > size()}
   */
  @Override
  public void removeRange(int start, int end) {
    hashValue = 0;
    if (start >= 0 && start <= end && end <= (lastIndex - firstIndex)) {
      if (start == end) {
        return;
      }
      int size = lastIndex - firstIndex;
      if (end == size) {
        Arrays.fill(array, firstIndex + start, lastIndex, null);
        lastIndex = firstIndex + start;
      } else if (start == 0) {
        Arrays.fill(array, firstIndex, firstIndex + end, null);
        firstIndex += end;
      } else {
        System.arraycopy(array, firstIndex + end, array, firstIndex + start, size - end);
        int newLast = lastIndex + start - end;
        Arrays.fill(array, newLast, lastIndex, null);
        lastIndex = newLast;
      }
    } else {
      throw new IndexOutOfBoundsException("Index: " + (lastIndex - firstIndex - end));
    }
  }

  @Override
  public IASTAppendable reverse(IASTAppendable resultList) {
    if (resultList.isNIL()) {
      resultList = F.ListAlloc(argSize());
    }
    if (resultList instanceof HMArrayList) {
      HMArrayList list = ((HMArrayList) resultList);
      if (list.array.length < size()) {
        list.growAtEnd(size());
      }
      IExpr[] resultArray = list.array;
      int i = firstIndex + argSize();
      while (i > firstIndex) {
        resultArray[list.lastIndex++] = array[i--];
      }
      return list;
    }

    int i = firstIndex + argSize();
    while (i > firstIndex) {
      resultList.append(array[i--]);
    }
    return resultList;
  }

  /**
   * Replaces the element at the specified location in this {@code ArrayList} with the specified
   * object. Internally the <code>hashValue</code> will be reset to <code>0</code>.
   *
   * @param location the index at which to put the specified object.
   * @param object the object to add.
   * @return the previous element at the index.
   * @throws IndexOutOfBoundsException when {@code location < 0 || >= size()}
   */
  @Override
  public IExpr set(int location, IExpr object) {
    if (Config.FUZZ_TESTING && isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
      throw new NullPointerException(
          "Index: " + location + ", Size: " + (lastIndex - firstIndex));
    }
    hashValue = 0;
    // if (0 <= location && location < (lastIndex - firstIndex)) {
    IExpr result = array[firstIndex + location];
    array[firstIndex + location] = object;
    return result;
    // }
    // throw new IndexOutOfBoundsException(
    // "Index: " + Integer.valueOf(location) + ", Size: " + Integer.valueOf(lastIndex -
    // firstIndex));
  }

  /** {@inheritDoc} */
  @Override
  public final int size() {
    return lastIndex - firstIndex;
  }

  @Override
  public Stream<IExpr> stream() {
    return Arrays.stream(array, firstIndex + 1, lastIndex);
  }

  @Override
  public Stream<IExpr> stream(int startInclusive, int endExclusive) {
    return Arrays.stream(toArray(), firstIndex + startInclusive, firstIndex + endExclusive);
  }

  /**
   * Returns a new array containing all elements contained in this {@code ArrayList}.
   *
   * @return an array of the elements from this {@code ArrayList}
   */
  @Override
  public IExpr[] toArray() {
    int size = lastIndex - firstIndex;
    IExpr[] result = new IExpr[size];
    System.arraycopy(array, firstIndex, result, 0, size);
    return result;
  }

  /**
   * Sets the capacity of this {@code ArrayList} to be the same as the current size.
   *
   * @see #size
   */
  public void trimToSize() {
    int size = lastIndex - firstIndex;
    IExpr[] newArray = newElementArray(size);
    System.arraycopy(array, firstIndex, newArray, 0, size);
    array = newArray;
    firstIndex = 0;
    lastIndex = array.length;
  }

  private void writeObject(ObjectOutputStream stream) throws IOException {
    ObjectOutputStream.PutField fields = stream.putFields();
    int size = lastIndex - firstIndex;
    fields.put("size", size); // $NON-NLS-1$
    stream.writeFields();
    // don't use an iterator here!
    for (int i = 0; i < size; i++) {
      stream.writeObject(get(i));
    }
  }
}
