package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;
import java.util.RandomAccess;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.generic.ObjIntPredicate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public abstract class B1 extends AbstractAST implements Externalizable, RandomAccess {
  static class Cos extends B1 {
    public Cos() {
      super();
    }

    Cos(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Cos(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Cos;
    }

    @Override
    public final int headID() {
      return ID.Cos;
    }
  }

  static class Csc extends B1 {
    public Csc() {
      super();
    }

    Csc(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Csc(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Csc;
    }

    @Override
    public final int headID() {
      return ID.Csc;
    }
  }

  static class Im extends B1 {
    public Im() {
      super();
    }

    Im(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Im(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Im;
    }

    @Override
    public final int headID() {
      return ID.Im;
    }

    @Override
    public final boolean isIm() {
      return true;
    }
  }

  static class IntegerQ extends B1 {
    public IntegerQ() {
      super();
    }

    IntegerQ(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new IntegerQ(arg1);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(EvalEngine engine) {
      if (arg1.isNumber()) {
        return arg1.isInteger() ? S.True : S.False;
      }
      return engine.evaluate(arg1).isInteger() ? S.True : S.False;
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.IntegerQ;
    }

    @Override
    public final int headID() {
      return ID.IntegerQ;
    }
  }

  static class Line extends B1 {
    public Line() {
      super();
    }

    Line(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Line(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Line;
    }

    @Override
    public final int headID() {
      return ID.Line;
    }
  }

  static class List extends B1 {
    public List() {
      super();
    }

    List(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new List(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.List;
    }

    @Override
    public final int headID() {
      return ID.List;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isList() {
      return true;
    }

    @Override
    public boolean isList1() {
      return true;
    }
  }

  static class Log extends B1 {
    public Log() {
      super();
    }

    Log(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Log(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Log;
    }

    @Override
    public final int headID() {
      return ID.Log;
    }
  }

  static class Missing extends B1 {
    public Missing() {
      super();
    }

    Missing(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Missing(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Missing;
    }

    @Override
    public final int headID() {
      return ID.Missing;
    }
  }

  static class Not extends B1 {
    public Not() {
      super();
    }

    Not(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Not(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Not;
    }

    @Override
    public final int headID() {
      return ID.Not;
    }
  }

  static class Point extends B1 {
    public Point() {
      super();
    }

    Point(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Point(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Point;
    }

    @Override
    public final int headID() {
      return ID.Point;
    }
  }

  static class Re extends B1 {
    public Re() {
      super();
    }

    Re(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Re(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Re;
    }

    @Override
    public final int headID() {
      return ID.Re;
    }

    @Override
    public final boolean isRe() {
      return true;
    }
  }

  static class Return extends B1 {
    public Return() {
      super();
    }

    Return(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Return(arg1);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(EvalEngine engine) {
      return B1.returnEval(this, engine);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Return;
    }

    @Override
    public final int headID() {
      return ID.Return;
    }

  }

  static class Sin extends B1 {
    public Sin() {
      super();
    }

    Sin(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Sin(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Sin;
    }

    @Override
    public final int headID() {
      return ID.Sin;
    }
  }

  static class Slot extends B1 {
    public Slot() {
      super();
    }

    Slot(IExpr arg1) {
      super(arg1);
      builtinEvaled();
    }

    Slot(int slot) {
      super(F.ZZ(slot));
      builtinEvaled();
    }

    @Override
    public IASTMutable copy() {
      return new Slot(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Slot;
    }

    @Override
    public final int headID() {
      return ID.Slot;
    }
  }

  static class Tan extends B1 {
    public Tan() {
      super();
    }

    Tan(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Tan(arg1);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Tan;
    }

    @Override
    public final int headID() {
      return ID.Tan;
    }
  }

  static class Throw extends B1 {
    public Throw() {
      super();
    }

    Throw(IExpr arg1) {
      super(arg1);
    }

    @Override
    public IASTMutable copy() {
      return new Throw(arg1);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(EvalEngine engine) {
      return B1.throwEval(this, engine);
    }

    @Override
    public final IBuiltInSymbol head() {
      return S.Throw;
    }

    @Override
    public final int headID() {
      return ID.Throw;
    }
  }

  private static final int SIZE = 2;

  public static IExpr returnEval(final IAST ast, EvalEngine engine) throws ReturnException {
    if (ast == F.CReturnFalse) {
      throw ReturnException.RETURN_FALSE;
    }
    if (ast == F.CReturnTrue) {
      throw ReturnException.RETURN_TRUE;
    }
    if (ast.isAST1()) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      if (arg1.isFalse()) {
        throw ReturnException.RETURN_FALSE;
      }
      if (arg1.isTrue()) {
        throw ReturnException.RETURN_TRUE;
      }
      throw new ReturnException(arg1);
    }
    if (ast.isAST0()) {
      throw new ReturnException();
    }
    return F.NIL;
  }

  public static IExpr throwEval(final IAST ast, EvalEngine engine) {
    if (ast == F.CThrowFalse) {
      throw ThrowException.THROW_FALSE;
    }
    if (ast == F.CThrowTrue) {
      throw ThrowException.THROW_TRUE;
    }
    if (ast.isAST1()) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      if (arg1.isFalse()) {
        throw ThrowException.THROW_FALSE;
      }
      if (arg1.isTrue()) {
        throw ThrowException.THROW_TRUE;
      }
      throw new ThrowException(arg1);
    } else if (ast.isAST2()) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      throw new ThrowException(arg1, ast.arg2());
    }
    return F.NIL;
  }

  /** The second argument of this function. */
  protected IExpr arg1;

  /** ctor for deserialization */
  public B1() {
    super();
  }

  /**
   * Create a function with one argument (i.e. <code>head[arg1, arg2]</code> ).
   *
   * @param arg1 the first argument of the function
   */
  public B1(IExpr arg1) {
    this.arg1 = arg1;
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
  public final int argSize() {
    return SIZE - 1;
  }

  @Override
  public SortedSet<IExpr> asSortedSet(Comparator<? super IExpr> comparator) {
    SortedSet<IExpr> set = new TreeSet<>(comparator);
    set.add(arg1);
    return set;
  }

  /**
   * Returns a new {@code AST2} with the same elements, the same size and the same capacity as this
   * {@code AST2}.
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
    return head().equals(object) || arg1.equals(object);
  }

  /** {@inheritDoc} */
  @Override
  public abstract IASTMutable copy();

  @Override
  public IASTAppendable copyAppendable() {
    return new AST(head(), arg1);
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    IASTAppendable result = F.ast(head(), additionalCapacity + 1);
    result.append(arg1);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof AbstractAST) {
      final IAST list = (IAST) obj;
      ISymbol head = head();
      if (head != list.head()) {
        // compared with IBuiltInSymbol object identity
        return false;
      }
      return list.size() == SIZE && arg1.equals(list.arg1());
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean exists(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head(), 0) || predicate.test(arg1, 1);
      case 1:
        return predicate.test(arg1, 1);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean exists(Predicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head()) || predicate.test(arg1);
      case 1:
        return predicate.test(arg1);
    }
    return false;
  }

  @Override
  public boolean existsLeft(BiPredicate<IExpr, IExpr> stopPredicate) {
    return false;
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
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
    if (predicate.test(arg1)) {
      filterAST.append(arg1);
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
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr findFirst(Function<IExpr, IExpr> function) {
    return function.apply(arg1);
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(ObjIntPredicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head(), 0) && predicate.test(arg1, 1);
      case 1:
        return predicate.test(arg1, 1);
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(Predicate<? super IExpr> predicate, int startOffset) {
    switch (startOffset) {
      case 0:
        return predicate.test(head()) && predicate.test(arg1);
      case 1:
        return predicate.test(arg1);
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(Consumer<? super IExpr> action) {
    action.accept(arg1);
  }

  /** {@inheritDoc} */
  @Override
  public void forEach(Consumer<? super IExpr> action, int startOffset) {
    switch (startOffset) {
      case 0:
        action.accept(head());
        action.accept(arg1);
        break;
      case 1:
        action.accept(arg1);
        break;
      default:
        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(startOffset) + ", Size: 2");
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
          }
          break;
        case 1:
          action.accept(arg1);
          break;
        default:
          throw new IndexOutOfBoundsException(
              "Index: " + Integer.valueOf(startOffset) + ", Size: 2");
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
          }
          break;
        case 1:
          action.accept(arg1, 1);
          break;
        default:
          throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(start) + ", Size: 2");
      }
    }
  }

  @Override
  public final void forEach(ObjIntConsumer<? super IExpr> action) {
    action.accept(arg1, 1);
  }

  @Override
  public void forEach2(ObjIntConsumer<? super IExpr> action) {
    // do nothing
  }

  @Override
  public IExpr get(int location) {
    switch (location) {
      case 0:
        return head();
      case 1:
        return arg1;
      default:
        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 2");
    }
  }

  @Override
  public IAST getItems(int[] items, int length, int offset) {
    if (length == 0) {
      return new AST0(head());
    }
    AST result = new AST(length, true);
    result.set(0, head());
    for (int i = 0; i < length; i++) {
      result.set(i + 1, get(items[i] + offset));
    }
    return result;
  }

  @Override
  public int hashCode() {
    if (hashValue == 0 && arg1 != null) {
      hashValue = (0x811c9dc5 * 16777619) ^ (SIZE & 0xff); // decimal 2166136261;
      hashValue = (hashValue * 16777619) ^ (head().hashCode() & 0xff);
      hashValue = (hashValue * 16777619) ^ (arg1.hashCode() & 0xff);
    }
    return hashValue;
  }

  @Override
  public abstract ISymbol head();

  @Override
  public int headID() {
    final IExpr head = head();
    return head instanceof IBuiltInSymbol ? ((IBuiltInSymbol) head).ordinal() : ID.UNKNOWN;
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(final IExpr expr) {
    return arg1.equals(expr) ? 1 : -1;
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(Predicate<? super IExpr> predicate, int fromIndex) {
    if (fromIndex == 1 && predicate.test(arg1)) {
      return 1;
    }
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isAST1() {
    return true;
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
  public final boolean isBuiltInFunction() {
    return true;
  }

  @Override
  public boolean isList2() {
    return false;
  }

  @Override
  public boolean isList3() {
    return false;
  }

  @Override
  public boolean isList4() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPlus() {
    return head() == S.Plus;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPower() {
    return head() == S.Power;
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
  public final boolean isTimes() {
    return head() == S.Times;
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr last() {
    return arg1;
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr oneIdentity(IExpr defaultValue) {
    return arg1;
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
      return this;
    }
    throw new IndexOutOfBoundsException(
        "Index: " + Integer.valueOf(fromPosition) + ", Size: " + size());
  }

  @Override
  public IASTAppendable reverse(IASTAppendable resultList) {
    if (resultList.isNIL()) {
      resultList = F.ListAlloc(argSize());
    }
    resultList.append(arg1);
    return resultList;
  }

  /**
   * Replaces the element at the specified location in this {@code ArrayList} with the specified
   * object.Internally the <code>hashValue</code> will be reset to <code>0</code>.
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
        throw new IndexOutOfBoundsException("Index: 0, Size: 2");
      case 1:
        result = arg1;
        arg1 = object;
        return result;
      default:
        throw new IndexOutOfBoundsException("Index: " + Integer.valueOf(location) + ", Size: 2");
    }
  }

  @Override
  public IASTMutable setAtCopy(int i, IExpr expr) {
    if (i == 0) {
      return new AST1(expr, arg1());
    }
    IASTMutable ast = copy();
    ast.set(i, expr);
    return ast;
  }

  /**
   * Returns the number of elements in this {@code ArrayList}.
   *
   * @return the number of elements in this {@code ArrayList}.
   */
  @Override
  public final int size() {
    return SIZE;
  }

  /**
   * Returns a new array containing all elements contained in this {@code ArrayList}.
   *
   * @return an array of the elements from this {@code ArrayList}
   */
  @Override
  public IExpr[] toArray() {
    return new IExpr[] {head(), arg1};
  }

  @Override
  public final ISymbol topHead() {
    return head();
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
