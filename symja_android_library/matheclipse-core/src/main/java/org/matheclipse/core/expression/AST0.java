package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.RandomAccess;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

import org.matheclipse.core.generic.ObjIntPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Immutable (A)bstract (S)yntax (T)ree of a given function with <b>no argument</b>.
 *
 * <p>In Symja, an abstract syntax tree (AST), is a tree representation of the abstract syntactic
 * structure of the Symja source code. Each node of the tree denotes a construct occurring in the
 * source code. The syntax is 'abstract' in the sense that it does not represent every detail that
 * appears in the real syntax. For instance, grouping parentheses are implicit in the tree
 * structure, and a syntactic construct such as a <code>Sin[x]</code> expression will be denoted by
 * an AST with 2 nodes. One node for the header <code>Sin</code> and one node for the argument
 * <code>x</code>. Internally an AST is represented as a <code>java.util.List</code> which contains
 *
 * <ul>
 *   <li>the operator of a function (i.e. the &quot;header&quot;-symbol: Sin, Cos, Inverse, Plus,
 *       Times,...) at index <code>0</code> and
 *   <li>the <code>n</code> arguments of a function in the index <code>1 to n</code>
 * </ul>
 *
 * See <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract syntax tree</a>.
 *
 * @see AST
 */
public class AST0 extends AbstractAST implements Externalizable, RandomAccess {

  private static final int SIZE = 1;

  /** */
  private static final long serialVersionUID = -5023978098877603499L;

  /**
   * The head of this function.
   *
   * <p>Package private.
   */
  protected IExpr arg0;

  /** ctor for deserialization */
  public AST0() {
    super();
  }

  /**
   * Create a function with no arguments (i.e. <code>head[ ]</code>).
   *
   * @param head the head of the function
   */
  /* package private */ AST0(IExpr head) {
    super();
    this.arg0 = head;
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
    // be compatible with get() contract
    throw new IndexOutOfBoundsException("Index: 1, Size: " + size());
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
    // be compatible with get() contract
    throw new IndexOutOfBoundsException("Index: 2, Size: " + size());
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
    // be compatible with get() contract
    throw new IndexOutOfBoundsException("Index: 3, Size: " + size());
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
    // be compatible with get() contract
    throw new IndexOutOfBoundsException("Index: 4, Size: " + size());
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
    // be compatible with get() contract
    throw new IndexOutOfBoundsException("Index: 5, Size: " + size());
  }

  /** {@inheritDoc} */
  @Override
  public int argSize() {
    return SIZE - 1;
  }

  @Override
  public Set<IExpr> asSet() {
    // empty set:
    return new HashSet<IExpr>();
  }

  /**
   * Returns a new {@code AST0} with the same elements, the same size and the same capacity as this
   * {@code AST0}.
   *
   * @return a shallow copy of this {@code ArrayList}
   * @see java.lang.Cloneable
   */
  @Override
  public IAST clone() {
    AST0 result = new AST0(arg0);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public boolean contains(Object object) {
    return arg0.equals(object);
  }

  /** {@inheritDoc} */
  @Override
  public IASTMutable copy() {
    return new AST0(arg0);
  }

  @Override
  public IASTAppendable copyAppendable() {
    return new AST(arg0);
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    return F.ast(arg0, additionalCapacity);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof AbstractAST) {
      final IAST list = (IAST) obj;
      if (arg0 != list.head() && arg0 instanceof ISymbol) {
        // compared with ISymbol object identity
        return false;
      }
      if (list.size() != SIZE) {
        return false;
      }
      return arg0 instanceof ISymbol || arg0.equals(list.head());
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean exists(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
    return (startOffset == 0) ? predicate.test(arg0, 0) : false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean exists(Predicate<? super IExpr> predicate, int startOffset) {
    return (startOffset == 0) ? predicate.test(arg0) : false;
  }

  /** {@inheritDoc} */
  @Override
  public IAST filter(
      IASTAppendable filterAST, IASTAppendable restAST, Predicate<? super IExpr> predicate) {
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public IAST filterFunction(
      IASTAppendable filterAST, IASTAppendable restAST, final Function<IExpr, IExpr> function) {
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
    return (startOffset == 0) ? predicate.test(arg0, 0) : true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(Predicate<? super IExpr> predicate, int startOffset) {
    return (startOffset == 0) ? predicate.test(arg0) : true;
  }

  @Override
  public void forEach(Consumer<? super IExpr> action) {
    // do nothing
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(Consumer<? super IExpr> action, int startOffset) {
    if (startOffset == 0) {
      action.accept(arg0);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(int startOffset, int endOffset, Consumer<? super IExpr> action) {
    if (startOffset == 0 && startOffset < endOffset) {
      action.accept(arg0);
    }
  }

  @Override
  public void forEach(int start, int end, ObjIntConsumer<? super IExpr> action) {
    if (start == 0) {
      action.accept(arg0, 0);
    }
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(Predicate<? super IExpr> predicate, int fromIndex) {
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr findFirst(Function<IExpr, IExpr> function) {
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr first() {
    return F.NIL;
  }

  @Override
  public IExpr get(int location) {
    if (location == 0) {
      return arg0;
    }
    throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 1");
  }

  @Override
  public IAST getItems(int[] items, int length) {
    if (length == 0) {
      return this;
    }
    throw new IndexOutOfBoundsException("Index: 0, Size: " + size());
  }

  @Override
  public int hashCode() {
    if (hashValue == 0 && arg0 != null) {
      hashValue = 0x811c9dc5; // decimal 2166136261;
      hashValue = (hashValue * 16777619) ^ (arg0.hashCode() & 0xff);
    }
    return hashValue;
  }

  @Override
  public final IExpr head() {
    return arg0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST0() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST1() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST2() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST3() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPlus() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPower() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head) {
    return arg0 == head;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head, int length) {
    return arg0 == head && length == SIZE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
    return arg0 == head && minLength <= SIZE && maxLength >= SIZE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHeadSizeGE(ISymbol head, int length) {
    return arg0 == head && length <= SIZE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isTimes() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr last() {
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr oneIdentity(IExpr defaultValue) {
    return defaultValue;
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    this.fEvalFlags = objectInput.readShort();

    int size;
    byte attributeFlags = objectInput.readByte();
    if (attributeFlags != 0) {
      size = attributeFlags;
      int exprIDSize = objectInput.readByte();
      for (int i = 0; i < exprIDSize; i++) {
        set(i, S.exprID(objectInput.readShort())); // F.GLOBAL_IDS[objectInput.readShort()]);
      }
      for (int i = exprIDSize; i < size; i++) {
        set(i, (IExpr) objectInput.readObject());
      }
      return;
    }

    size = objectInput.readInt();
    for (int i = 0; i < size; i++) {
      set(i, (IExpr) objectInput.readObject());
    }
  }

  @Override
  public IAST removeFromEnd(int fromPosition) {
    if (fromPosition == size()) {
      return this;
    }
    throw new IndexOutOfBoundsException(
        "Index: " + Integer.valueOf(fromPosition) + ", Size: " + size());
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
    if (location == 0) {
      IExpr result;
      result = arg0;
      arg0 = object;
      return result;
    }
    throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 1");
  }

  /**
   * Returns the number of elements in this {@code ArrayList}.
   *
   * @return the number of elements in this {@code ArrayList}.
   */
  @Override
  public int size() {
    return SIZE;
  }

  /**
   * Returns a new array containing all elements contained in this {@code ArrayList}.
   *
   * @return an array of the elements from this {@code ArrayList}
   */
  @Override
  public IExpr[] toArray() {
    return new IExpr[] {arg0};
  }

  /**
   * Returns the ISymbol of the IAST. If the head itself is a IAST it will recursively call head().
   */
  @Override
  public final ISymbol topHead() {
    return arg0 instanceof ISymbol ? (ISymbol) arg0 : arg0.topHead();
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeShort(fEvalFlags);

    int size = size();
    byte attributeFlags = (byte) 0;

    Short exprID = S.GLOBAL_IDS_MAP.get(head());
    if (exprID != null) {
      int exprIDSize = 1;
      short[] exprIDArray = new short[size];
      exprIDArray[0] = exprID;
      for (int i = 1; i < size; i++) {
        exprID = S.GLOBAL_IDS_MAP.get(get(i));
        if (exprID == null) {
          break;
        }
        exprIDArray[i] = exprID;
        exprIDSize++;
      }
      // optimized path
      attributeFlags = (byte) size;
      objectOutput.writeByte(attributeFlags);
      objectOutput.writeByte((byte) exprIDSize);
      for (int i = 0; i < exprIDSize; i++) {
        objectOutput.writeShort(exprIDArray[i]);
      }
      for (int i = exprIDSize; i < size; i++) {
        objectOutput.writeObject(get(i));
      }
      return;
    }

    objectOutput.writeByte(attributeFlags);
    objectOutput.writeInt(size);
    for (int i = 0; i < size; i++) {
      objectOutput.writeObject(get(i));
    }
  }

  private Object writeReplace() {
    return optional();
  }
}
