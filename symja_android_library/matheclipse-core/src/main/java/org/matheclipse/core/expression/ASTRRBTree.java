package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.organicdesign.fp.StaticImports;
import org.organicdesign.fp.collections.RrbTree.ImRrbt;
import org.organicdesign.fp.collections.RrbTree.MutRrbt;
import org.organicdesign.fp.collections.UnmodSortedIterator;

/**
 * Immutable (A)bstract (S)yntax (T)ree of a given function with <b>no argument</b>.
 *
 * <p>
 * In Symja, an abstract syntax tree (AST), is a tree representation of the abstract syntactic
 * structure of the Symja source code. Each node of the tree denotes a construct occurring in the
 * source code. The syntax is 'abstract' in the sense that it does not represent every detail that
 * appears in the real syntax. For instance, grouping parentheses are implicit in the tree
 * structure, and a syntactic construct such as a <code>Sin[x]</code> expression will be denoted by
 * an AST with 2 nodes. One node for the header <code>Sin</code> and one node for the argument
 * <code>x</code>. Internally an AST is represented as a <code>java.util.List</code> which contains
 *
 * <ul>
 * <li>the operator of a function (i.e. the &quot;header&quot;-symbol: Sin, Cos, Inverse, Plus,
 * Times,...) at index <code>0</code> and
 * <li>the <code>n</code> arguments of a function in the index <code>1 to n</code>
 * </ul>
 *
 * See <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract syntax tree</a>.
 *
 * @see AST
 */
public class ASTRRBTree extends AbstractAST
    implements IASTAppendable, Externalizable, RandomAccess {

  /** The underlying RRB Tree */
  protected MutRrbt<IExpr> rrbTree;

  public ASTRRBTree() {
    super();
    // When Externalizable objects are deserialized, they first need to be constructed by invoking
    // the void constructor. Since this class does not have one, serialization and deserialization
    // will fail
    // at runtime.
    rrbTree = StaticImports.mutableRrb();
  }

  public ASTRRBTree(int size, boolean test) {
    super();
    rrbTree = StaticImports.mutableRrb();
  }

  public void ensureCapacity(int size) {}

  public static ASTRRBTree newInstance(final int initialCapacity, final IExpr head) {
    if (Config.MAX_AST_SIZE < initialCapacity || initialCapacity < 0) {
      ASTElementLimitExceeded.throwIt(initialCapacity);
    }
    return new ASTRRBTree(StaticImports.mutableRrb(head));
  }

  /**
   * @param rrbTree the vector which should be wrapped in this object.
   * @param deepCopy if <code>true</code> allocate new memory and copy all elements from the vector
   */
  public ASTRRBTree(IAST ast) {
    super();
    if (Config.MAX_AST_SIZE < ast.size()) {
      throw new ASTElementLimitExceeded(ast.size());
    }

    rrbTree = StaticImports.mutableRrb();
    for (int i = 0; i < ast.size(); i++) {
      rrbTree.append(ast.getRule(i));
    }
  }

  public ASTRRBTree(MutRrbt<IExpr> list) {
    super();
    if (Config.MAX_AST_SIZE < list.size()) {
      throw new ASTElementLimitExceeded(list.size());
    }

    this.rrbTree = list.toMutRrbt();
  }

  public ASTRRBTree(ImRrbt<IExpr> list) {
    super();
    if (Config.MAX_AST_SIZE < list.size()) {
      throw new ASTElementLimitExceeded(list.size());
    }

    this.rrbTree = list.toMutRrbt();
  }

  public ASTRRBTree(IExpr[] array) {
    super();
    if (Config.MAX_AST_SIZE < array.length) {
      throw new ASTElementLimitExceeded(array.length);
    }
    this.rrbTree = StaticImports.mutableRrb(array);
  }

  /**
   * Get the first argument (i.e. the second element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(1) ). <br>
   * <b>Example:</b> for the AST representing the expression <code>Sin(x)</code>, <code>arg1()
   * </code> returns <code>x</code>.
   *
   * @return the first argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg1() {
    return rrbTree.get(1);
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
    return rrbTree.get(2);
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
    return rrbTree.get(3);
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
    return rrbTree.get(4);
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
    return rrbTree.get(5);
  }

  /** {@inheritDoc} */
  @Override
  public int argSize() {
    return rrbTree.size() - 1;
  }

  @Override
  public Set<IExpr> asSet() {
    int size = size();
    Set<IExpr> set = new HashSet<IExpr>(size > 16 ? size : 16);
    for (int i = 1; i < size; i++) {
      set.add(get(i));
    }
    return set;
  }

  /**
   * Removes all elements from this {@code ArrayList}, leaving it empty.
   *
   * @see #isEmpty
   * @see #size
   */
  // @Override
  // public void clear() {
  // hashValue = 0;
  // throw new UnsupportedOperationException();
  // }

  /**
   * Returns a new {@code HMArrayList} with the same elements, the same size and the same capacity
   * as this {@code HMArrayList}.
   *
   * @return a shallow copy of this {@code ArrayList}
   * @see java.lang.Cloneable
   */
  @Override
  public IAST clone() {
    return copy();
  }

  /** {@inheritDoc} */
  @Override
  public ASTRRBTree copy() {
    return new ASTRRBTree(rrbTree.toMutRrbt());
  }

  @Override
  public IASTAppendable copyAppendable() {
    return copy();
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    return copyAppendable();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof AbstractAST) {
      final IAST ast = (AbstractAST) obj;
      final int size = size();
      if (size != ast.size()) {
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
      if (hashCode() != ast.hashCode()) {
        return false;
      }
      return forAll((x, i) -> x.equals(ast.getRule(i)), 1);
    }
    return false;
  }

  @Override
  public void forEach(int start, int end, ObjIntConsumer<? super IExpr> action) {
    UnmodSortedIterator<IExpr> iter = rrbTree.listIterator(start);
    for (int i = start; i < end; i++) {
      action.accept(iter.next(), i);
    }
  }

  @Override
  public IExpr get(int location) {
    return rrbTree.get(location);
  }

  @Override
  public IAST getItems(int[] items, int length) {
    MutRrbt<IExpr> mutableRrb = StaticImports.mutableRrb();
    mutableRrb.append(head());
    for (int i = 0; i < length; i++) {
      mutableRrb = mutableRrb.append(get(items[i]));
    }
    return new ASTRRBTree(mutableRrb);
  }

  @Override
  public int hashCode() {
    if (hashValue == 0) {
      hashValue = 0x811c9dc5; // decimal 2166136261;
      for (int i = 0; i < size(); i++) {
        hashValue = (hashValue * 16777619) ^ (get(i).hashCode() & 0xff);
      }
    }
    return hashValue;
  }

  @Override
  public IExpr head() {
    return rrbTree.get(0);
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
  public boolean isList() {
    return head() == S.List;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head) {
    return head() == head;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head, int length) {
    return head() == head && rrbTree.size() == length;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
    int size = rrbTree.size();
    return head() == head && minLength <= size && maxLength >= size;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHeadSizeGE(ISymbol head, int length) {
    return head() == head && length <= rrbTree.size();
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    this.fEvalFlags = objectInput.readShort();
    // MutRrbt is not serializable
    this.rrbTree = ((ImRrbt) objectInput.readObject()).toMutRrbt();
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
    hashValue = 0;
    if (location < rrbTree.size()) {
      IExpr value = rrbTree.get(location);
      rrbTree = rrbTree.replace(location, object);
      return value;
    }
    throw new IndexOutOfBoundsException("Index: " + location + ", Size: " + rrbTree.size());
  }

  @Override
  public IASTMutable setAtCopy(int i, IExpr expr) {
    IASTMutable ast = copy();
    ast.set(i, expr);
    return ast;
  }

  @Override
  public void sortInplace(Comparator<IExpr> comparator) {
    hashValue = 0;
    int size = rrbTree.size();
    IExpr[] a = new IExpr[size];
    rrbTree.toArray(a);
    Arrays.sort(a, 1, a.length, comparator);
    this.rrbTree = StaticImports.mutableRrb(a);
  }

  /**
   * Returns the number of elements in this {@code ArrayList}.
   *
   * @return the number of elements in this {@code ArrayList}.
   */
  @Override
  public final int size() {
    return rrbTree.size();
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeShort(fEvalFlags);
    // MutRrbt is not serializable
    objectOutput.writeObject(rrbTree.immutable());
  }

  @Override
  public IExpr[] toArray() {
    int size = rrbTree.size();
    IExpr[] result = new IExpr[size];
    return rrbTree.toArray(result);
  }

  @Override
  public boolean append(IExpr expr) {
    hashValue = 0;
    rrbTree = rrbTree.append(expr);
    return true;
  }

  @Override
  public void append(int location, IExpr expr) {
    hashValue = 0;
    if (location >= 0 && location <= size()) {
      if (location == size()) {
        append(expr);
        return;
      }
      rrbTree = rrbTree.insert(location, expr);
      return;
    }
    throw new IndexOutOfBoundsException(
        "Index: " + Integer.valueOf(location) + ", Size: " + size());
  }

  @Override
  public boolean appendAll(Collection<? extends IExpr> collection) {
    hashValue = 0;
    rrbTree.addAll(collection);
    return true;
  }

  @Override
  public boolean appendAll(Map<? extends IExpr, ? extends IExpr> map) {
    hashValue = 0;
    for (Map.Entry<? extends IExpr, ? extends IExpr> entry : map.entrySet()) {
      rrbTree.append(F.Rule(entry.getKey(), entry.getValue()));
    }
    return true;
  }

  @Override
  public boolean appendAll(IAST ast, int startPosition, int endPosition) {
    if (ast.size() > 0 && startPosition < endPosition) {
      hashValue = 0;
      for (int i = startPosition; i < endPosition; i++) {
        rrbTree = rrbTree.append(ast.get(i));
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean appendAll(int location, Collection<? extends IExpr> collection) {
    hashValue = 0;
    final int size = rrbTree.size();
    if (location < 0 || location > size) {
      throw new IndexOutOfBoundsException(
          "Index: " + Integer.valueOf(location) + ", Size: " + size);
    }
    if (location == size) {
      appendAll(collection);
      return true;
    }
    MutRrbt<IExpr> mutable = StaticImports.mutableRrb();
    for (int i = 0; i < location; i++) {
      mutable = mutable.append(rrbTree.get(i));
    }
    mutable.addAll(collection);
    for (int i = location; i < rrbTree.size(); i++) {
      mutable = mutable.append(rrbTree.get(i));
    }
    rrbTree = mutable;
    return true;
  }

  @Override
  public boolean appendAll(List<? extends IExpr> list, int startPosition, int endPosition) {
    if (list.size() > 0 && startPosition < endPosition) {
      hashValue = 0;
      for (int i = startPosition; i < endPosition; i++) {
        rrbTree = rrbTree.append(list.get(i));
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean appendAll(IExpr[] args, int startPosition, int endPosition) {
    if (args.length > 0 && startPosition < endPosition) {
      hashValue = 0;
      for (int i = startPosition; i < endPosition; i++) {
        rrbTree = rrbTree.append(args[i]);
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean appendArgs(IAST ast) {
    return appendArgs(ast, ast.size());
  }

  @Override
  public boolean appendArgs(IAST ast, int untilPosition) {
    if (untilPosition > 1) {
      hashValue = 0;
      for (int i = 1; i < untilPosition; i++) {
        rrbTree = rrbTree.append(ast.get(i));
      }
      return true;
    }
    return false;
  }

  @Override
  public IASTAppendable appendArgs(int start, int end, IntFunction<IExpr> function) {
    if (start >= end) {
      return this;
    }
    hashValue = 0;
    for (int i = start; i < end; i++) {
      IExpr temp = function.apply(i);
      if (temp.isPresent()) {
        rrbTree = rrbTree.append(temp);
        continue;
      }
      break;
    }
    return this;
  }

  @Override
  public IASTAppendable appendArgs(int end, IntFunction<IExpr> function) {
    return appendArgs(1, end, function);
  }

  @Override
  public IAST appendOneIdentity(IAST value) {
    if (value.isAST1()) {
      append(value.arg1());
    } else {
      append(value);
    }
    return this;
  }

  @Override
  public void clear() {
    hashValue = 0;
    fEvalFlags = NO_FLAG;
    rrbTree = StaticImports.mutableRrb();
  }

  @Override
  public IExpr remove(int location) {
    hashValue = 0;

    final int size = rrbTree.size();
    if (location >= 0 && location < size) {
      IExpr expr = rrbTree.get(location);
      rrbTree = rrbTree.without(location);
      // deprecated?
      // rrbTree = rrbTree.without(location);
      return expr;
    }
    throw new IndexOutOfBoundsException("Index: " + location);
  }

  @Override
  public void removeRange(int start, int end) {
    hashValue = 0;
    final int size = rrbTree.size();
    if (start >= 0 && start <= end && end <= size) {
      if (start == end) {
        return;
      }
      for (int i = end - 1; i >= start; i--) {
        rrbTree = rrbTree.without(i);
      }

      // MutRrbt<IExpr> mutable = StaticImports.mutableRrb();
      // for (int i = 0; i < start; i++) {
      // mutable.append(vector.get(i));
      // }
      // for (int i = end; i < size; i++) {
      // mutable.append(vector.get(i));
      // }
      // vector = mutable.immutable();
    } else {
      throw new IndexOutOfBoundsException("Index: " + size());
    }
  }
}
