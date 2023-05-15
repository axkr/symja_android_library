package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.RandomAccess;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import org.matheclipse.core.generic.ObjIntPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public abstract class B3 extends AbstractAST implements Externalizable, RandomAccess {
  private static final int SIZE = 4;

  static final class List extends B3 {
    public List() {
      super();
    }

    List(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.List;
    }

    @Override
    public final IBuiltInSymbol topHead() {
      return S.List;
    }

    @Override
    public IASTMutable copy() {
      return new List(arg1, arg2, arg3);
    }
  }

  static final class And extends B3 {
    public And() {
      super();
    }

    And(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.And;
    }

    @Override
    public IASTMutable copy() {
      return new And(arg1, arg2, arg3);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isFlatAST() {
      return true;
    }
  }

  static final class Equal extends B3 {
    public Equal() {
      super();
    }

    Equal(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Equal;
    }

    @Override
    public IASTMutable copy() {
      return new Equal(arg1, arg2, arg3);
    }
  }

  static final class Greater extends B3 {
    public Greater() {
      super();
    }

    Greater(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Greater;
    }

    @Override
    public IASTMutable copy() {
      return new Greater(arg1, arg2, arg3);
    }
  }

  static final class GreaterEqual extends B3 {
    public GreaterEqual() {
      super();
    }

    GreaterEqual(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.GreaterEqual;
    }

    @Override
    public IASTMutable copy() {
      return new GreaterEqual(arg1, arg2, arg3);
    }
  }

  static final class If extends B3 {
    public If() {
      super();
    }

    If(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.If;
    }

    @Override
    public IASTMutable copy() {
      return new If(arg1, arg2, arg3);
    }
  }

  static final class Less extends B3 {
    public Less() {
      super();
    }

    Less(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Less;
    }

    @Override
    public IASTMutable copy() {
      return new Less(arg1, arg2, arg3);
    }
  }

  static final class LessEqual extends B3 {
    public LessEqual() {
      super();
    }

    LessEqual(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.LessEqual;
    }

    @Override
    public IASTMutable copy() {
      return new LessEqual(arg1, arg2, arg3);
    }
  }


  static final class Or extends B3 {
    public Or() {
      super();
    }

    Or(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Or;
    }

    @Override
    public IASTMutable copy() {
      return new Or(arg1, arg2, arg3);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isFlatAST() {
      return true;
    }
  }

  static final class Part extends B3 {
    public Part() {
      super();
    }

    Part(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Part;
    }

    @Override
    public IASTMutable copy() {
      return new Part(arg1, arg2, arg3);
    }
  }

  static final class Plus extends B3 {
    public Plus() {
      super();
    }

    Plus(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Plus;
    }

    @Override
    public final IBuiltInSymbol topHead() {
      return S.Plus;
    }

    @Override
    public IASTMutable copy() {
      return new Plus(arg1, arg2, arg3);
    }

    @Override
    public boolean isPlus() {
      return true;
    }

    @Override
    public boolean isPlusTimesPower() {
      return true;
    }

    @Override
    public boolean isPower() {
      return false;
    }

    @Override
    public boolean isTimes() {
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isFlatAST() {
      return true;
    }
  }

  static final class Times extends B3 {
    public Times() {
      super();
    }

    Times(IExpr arg1, IExpr arg2, IExpr arg3) {
      super(arg1, arg2, arg3);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Times;
    }

    @Override
    public final IBuiltInSymbol topHead() {
      return S.Times;
    }

    @Override
    public IASTMutable copy() {
      return new Times(arg1, arg2, arg3);
    }

    @Override
    public boolean isPlusTimesPower() {
      return true;
    }

    @Override
    public boolean isPlus() {
      return false;
    }

    @Override
    public boolean isPower() {
      return false;
    }

    @Override
    public boolean isTimes() {
      return true;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isFlatAST() {
      return true;
    }
  }


  /** The second argument of this function. */
  protected IExpr arg1;

  /** The second argument of this function. */
  protected IExpr arg2;

  /** The third argument of this function. */
  protected IExpr arg3;

  /** ctor for deserialization */
  public B3() {
    super();
  }

  /**
   * Create a function with two arguments (i.e. <code>head[arg1, arg2]</code> ).
   *
   * @param arg1 the first argument of the function
   * @param arg2 the second argument of the function
   */
  private B3(IExpr arg1, IExpr arg2, IExpr arg3) {
    this.arg1 = arg1;
    this.arg2 = arg2;
    this.arg3 = arg3;
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
  public final IExpr arg1() {
    return arg1;
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
  public final IExpr arg2() {
    return arg2;
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
    return arg3;
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
    Set<IExpr> set = new TreeSet<>();
    set.add(arg1);
    set.add(arg2);
    set.add(arg3);
    return set;
  }

  /**
   * Returns a new {@code B3} with the same elements, the same size and the same capacity as this
   * {@code B3}.
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
  public boolean contains(Object object) {
    return head().equals(object) || arg1.equals(object) || arg2.equals(object)
        || arg3.equals(object);
  }

  /** {@inheritDoc} */
  @Override
  public abstract IASTMutable copy();

  @Override
  public IASTAppendable copyAppendable() {
    return new AST(head(), arg1, arg2, arg3);
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    IASTAppendable result = F.ast(head(), additionalCapacity + 2);
    result.append(arg1);
    result.append(arg2);
    result.append(arg3);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof AbstractAST) {
      final IAST list = (IAST) obj;
      final ISymbol head = head();
      if (head != ((AbstractAST) list).head()) {
        // compared with IBuiltInSymbol object identity
        return false;
      }
      return list.size() == SIZE && arg1.equals(list.arg1()) && arg2.equals(list.arg2())
          && arg3.equals(list.arg3());
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean exists(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head(), 0) || predicate.test(arg1, 1) || predicate.test(arg2, 2)
            || predicate.test(arg3, 3);
      case 1:
        return predicate.test(arg1, 1) || predicate.test(arg2, 2) || predicate.test(arg3, 3);
      case 2:
        return predicate.test(arg2, 2) || predicate.test(arg3, 3);
      case 3:
        return predicate.test(arg3, 3);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean exists(Predicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head()) || predicate.test(arg1) || predicate.test(arg2)
            || predicate.test(arg3);
      case 1:
        return predicate.test(arg1) || predicate.test(arg2) || predicate.test(arg3);
      case 2:
        return predicate.test(arg2) || predicate.test(arg3);
      case 3:
        return predicate.test(arg3);
    }
    return false;
  }

  @Override
  public boolean existsLeft(BiPredicate<IExpr, IExpr> stopPredicate) {
    return stopPredicate.test(arg1, arg2) || stopPredicate.test(arg2, arg3);
  }

  /** {@inheritDoc} */
  @Override
  public IAST filter(IASTAppendable filterAST, IASTAppendable restAST,
      Predicate<? super IExpr> predicate) {
    if (predicate.test(arg1)) {
      filterAST.append(arg1);
    } else {
      restAST.append(arg1);
    }
    if (predicate.test(arg2)) {
      filterAST.append(arg2);
    } else {
      restAST.append(arg2);
    }
    if (predicate.test(arg3)) {
      filterAST.append(arg3);
    } else {
      restAST.append(arg3);
    }
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
    if (predicate.test(arg1)) {
      filterAST.append(arg1);
    }
    if (predicate.test(arg2)) {
      filterAST.append(arg2);
    }
    if (predicate.test(arg3)) {
      filterAST.append(arg3);
    }
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public IAST filterFunction(IASTAppendable filterAST, IASTAppendable restAST,
      final Function<IExpr, IExpr> function) {
    IExpr expr = function.apply(arg1);
    if (expr.isPresent()) {
      filterAST.append(expr);
    } else {
      restAST.append(arg1);
    }
    expr = function.apply(arg2);
    if (expr.isPresent()) {
      filterAST.append(expr);
    } else {
      restAST.append(arg2);
    }
    expr = function.apply(arg3);
    if (expr.isPresent()) {
      filterAST.append(expr);
    } else {
      restAST.append(arg3);
    }
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head(), 0) && predicate.test(arg1, 1) && predicate.test(arg2, 2)
            && predicate.test(arg3, 3);
      case 1:
        return predicate.test(arg1, 1) && predicate.test(arg2, 2) && predicate.test(arg3, 3);
      case 2:
        return predicate.test(arg2, 2) && predicate.test(arg3, 3);
      case 3:
        return predicate.test(arg3, 3);
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(Predicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head()) && predicate.test(arg1) && predicate.test(arg2)
            && predicate.test(arg3);
      case 1:
        return predicate.test(arg1) && predicate.test(arg2) && predicate.test(arg3);
      case 2:
        return predicate.test(arg2) && predicate.test(arg3);
      case 3:
        return predicate.test(arg3);
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(Consumer<? super IExpr> action) {
    action.accept(arg1);
    action.accept(arg2);
    action.accept(arg3);
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(Consumer<? super IExpr> action, int startOffset) {
    switch (startOffset) {
      case 0:
        action.accept(head());
        action.accept(arg1);
        action.accept(arg2);
        action.accept(arg3);
        break;
      case 1:
        action.accept(arg1);
        action.accept(arg2);
        action.accept(arg3);
        break;
      case 2:
        action.accept(arg2);
        action.accept(arg3);
        break;
      case 3:
        action.accept(arg3);
        break;
    }
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(int startOffset, int endOffset, Consumer<? super IExpr> action) {
    if (startOffset < endOffset) {
      switch (startOffset) {
        case 0:
          action.accept(head());
          if (startOffset + 1 < endOffset) {
            action.accept(arg1);
            if (startOffset + 2 < endOffset) {
              action.accept(arg2);
              if (startOffset + 3 < endOffset) {
                action.accept(arg3);
              }
            }
          }
          break;
        case 1:
          action.accept(arg1);
          if (startOffset + 1 < endOffset) {
            action.accept(arg2);
            if (startOffset + 23 < endOffset) {
              action.accept(arg3);
            }
          }
          break;
        case 2:
          action.accept(arg2);
          if (startOffset + 1 < endOffset) {
            action.accept(arg3);
          }
          break;
        case 3:
          action.accept(arg3);
          break;
      }
    }
  }

  @Override
  public void forEach(int start, int end, ObjIntConsumer<? super IExpr> action) {
    if (start < end) {
      switch (start) {
        case 0:
          action.accept(head(), 0);
          if (start + 1 < end) {
            action.accept(arg1, 1);
            if (start + 2 < end) {
              action.accept(arg2, 2);
              if (start + 3 < end) {
                action.accept(arg3, 3);
              }
            }
          }
          break;
        case 1:
          action.accept(arg1, 1);
          if (start + 1 < end) {
            action.accept(arg2, 2);
            if (start + 2 < end) {
              action.accept(arg3, 3);
            }
          }
          break;
        case 2:
          action.accept(arg2, 2);
          if (start + 1 < end) {
            action.accept(arg3, 3);
          }
          break;
        case 3:
          action.accept(arg3, 3);
          break;
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(final IExpr expr) {
    if (arg1.equals(expr)) {
      return 1;
    }
    if (arg2.equals(expr)) {
      return 2;
    }
    if (arg3.equals(expr)) {
      return 3;
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(Predicate<? super IExpr> predicate, int fromIndex) {
    if (fromIndex == 1 && predicate.test(arg1)) {
      return 1;
    }
    if ((fromIndex == 1 || fromIndex == 2) && predicate.test(arg2)) {
      return 2;
    }
    if ((fromIndex == 1 || fromIndex == 2 || fromIndex == 3) && predicate.test(arg3)) {
      return 3;
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr findFirst(Function<IExpr, IExpr> function) {
    IExpr temp = function.apply(arg1);
    if (temp.isPresent()) {
      return temp;
    }
    temp = function.apply(arg2);
    if (temp.isPresent()) {
      return temp;
    }
    return function.apply(arg3);
  }

  @Override
  public IExpr get(int location) {
    switch (location) {
      case 0:
        return head();
      case 1:
        return arg1;
      case 2:
        return arg2;
      case 3:
        return arg3;
      default:
        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 3");
    }
  }

  @Override
  public IAST getItems(int[] items, int length) {
    if (length == 0) {
      return this;
    }
    AST result = new AST(length, true);
    result.set(0, head());
    for (int i = 0; i < length; i++) {
      result.set(i + 1, get(items[i]));
    }
    return result;
  }

  @Override
  public abstract ISymbol head();


  @Override
  public int headID() {
    final IExpr head = head();
    return head instanceof IBuiltInSymbol ? ((IBuiltInSymbol) head).ordinal() : ID.UNKNOWN;
  }

  @Override
  public int hashCode() {
    if (hashValue == 0 && arg2 != null) {
      hashValue = 0x811c9dc5; // decimal 2166136261;
      hashValue = (hashValue * 16777619) ^ (head().hashCode() & 0xff);
      hashValue = (hashValue * 16777619) ^ (arg1.hashCode() & 0xff);
      hashValue = (hashValue * 16777619) ^ (arg2.hashCode() & 0xff);
      hashValue = (hashValue * 16777619) ^ (arg3.hashCode() & 0xff);
    }
    return hashValue;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isAST1() {
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
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFlatAST() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPlus() {
    return head() == S.Plus;
  }

  @Override
  public boolean isPlusTimesPower() {
    final ISymbol head = head();
    return head == S.Plus || head == S.Times || head == S.Power;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPower() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head, int length) {
    return head() == head && length == SIZE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
    return head() == head && minLength <= SIZE && maxLength >= SIZE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHeadSizeGE(ISymbol head, int length) {
    return head() == head && length <= SIZE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isTimes() {
    return head() == S.Times;
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr last() {
    return arg3;
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr oneIdentity(IExpr defaultValue) {
    return this;
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    this.fEvalFlags = objectInput.readShort();

    // int size;
    // byte attributeFlags = objectInput.readByte();
    // if (attributeFlags != 0) {
    // size = attributeFlags;
    // int exprIDSize = objectInput.readByte();
    // for (int i = 0; i < exprIDSize; i++) {
    // set(i, F.GLOBAL_IDS[objectInput.readShort()]);
    // }
    // for (int i = exprIDSize; i < size; i++) {
    // set(i, (IExpr) objectInput.readObject());
    // }
    // return;
    // }

    // size = objectInput.readInt();
    for (int i = 1; i < SIZE; i++) {
      set(i, (IExpr) objectInput.readObject());
    }
  }

  @Override
  public IAST removeFromEnd(int fromPosition) {
    if (fromPosition == 1) {
      return new AST0(head());
    }
    if (fromPosition == 2) {
      return new AST1(head(), arg1);
    }
    if (fromPosition == 3) {
      return new AST2(head(), arg1, arg2);
    }
    if (fromPosition == 4) {
      return this;
    }
    throw new IndexOutOfBoundsException(
        "Index: " + Integer.valueOf(fromPosition) + ", Size: " + size());
  }

  @Override
  public IASTMutable setAtCopy(int i, IExpr expr) {
    if (i == 0) {
      return new AST3(expr, arg1(), arg2(), arg3());
    }
    IASTMutable ast = copy();
    ast.set(i, expr);
    return ast;
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
    IExpr result;
    switch (location) {
      case 0:
        throw new IndexOutOfBoundsException("Index: 0, Size: 4");
      case 1:
        result = arg1;
        arg1 = object;
        return result;
      case 2:
        result = arg2;
        arg2 = object;
        return result;
      case 3:
        result = arg3;
        arg3 = object;
        return result;
      default:
        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 4");
    }
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

  @Override
  public ISymbol topHead() {
    return head();
  }

  /**
   * Returns a new array containing all elements contained in this {@code ArrayList}.
   *
   * @return an array of the elements from this {@code ArrayList}
   */
  @Override
  public IExpr[] toArray() {
    return new IExpr[] {head(), arg1, arg2, arg3};
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeShort(fEvalFlags);

    // int size = size();
    // byte attributeFlags = (byte) 0;
    //
    // ExprID temp = F.GLOBAL_IDS_MAP.get(head());
    // if (temp != null) {
    // short exprID = temp.getExprID();
    // if (exprID <= Short.MAX_VALUE) {
    // int exprIDSize = 1;
    // short[] exprIDArray = new short[size];
    // exprIDArray[0] = exprID;
    // for (int i = 1; i < size; i++) {
    // temp = F.GLOBAL_IDS_MAP.get(get(i));
    // if (temp == null) {
    // break;
    // }
    // exprID = temp.getExprID();
    // if (exprID <= Short.MAX_VALUE) {
    // exprIDArray[i] = exprID;
    // exprIDSize++;
    // } else {
    // break;
    // }
    // }
    // // optimized path
    // attributeFlags = (byte) size;
    // objectOutput.writeByte(attributeFlags);
    // objectOutput.writeByte((byte) exprIDSize);
    // for (int i = 0; i < exprIDSize; i++) {
    // objectOutput.writeShort(exprIDArray[i]);
    // }
    // for (int i = exprIDSize; i < size; i++) {
    // objectOutput.writeObject(get(i));
    // }
    // return;
    // }
    // }

    // objectOutput.writeByte(attributeFlags);
    // objectOutput.writeInt(size-1);
    for (int i = 1; i < SIZE; i++) {
      objectOutput.writeObject(get(i));
    }
  }
}
