package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Set;
import java.util.StringTokenizer;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * (A)bstract (S)yntax (T)ree of a given function.
 *
 * <p>In Symja, an abstract syntax tree (AST), is a tree representation of the abstract syntactic
 * structure of the Symja source code. Each node of the tree denotes a construct occurring in the
 * source code. The syntax is 'abstract' in the sense that it does not represent every detail that
 * appears in the real syntax. For instance, grouping parentheses are implicit in the tree
 * structure, and a syntactic construct such as a <code>Sin[x]</code> expression will be denoted by
 * an AST with 2 nodes. One node for the header <code>Sin</code> and one node for the argument
 * <code>x</code>. Internally an AST is represented as a list which contains
 *
 * <ul>
 *   <li>the operator of a function (i.e. the &quot;header&quot;-symbol: Sin, Cos, Inverse, Plus,
 *       Times,...) at index <code>0</code> and
 *   <li>the <code>n</code> arguments of a function in the index <code>1 to n</code>
 * </ul>
 *
 * <p>See: <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Wikipedia: Abstract syntax
 * tree</a>.
 */
public class AST extends HMArrayList implements Externalizable {

  /** */
  private static final long serialVersionUID = 4295200630292148027L;

  /** <a href="https://en.wikipedia.org/wiki/Proxy_pattern">Proxy</a> for an AST object. */
  private static final class ASTProxy extends AbstractAST implements Externalizable {
    /** */
    private static final long serialVersionUID = -7027944101238962694L;

    IAST fDelegate;
    int fFirstIndex;

    public ASTProxy() {
      super();
      // needed for serialization
    }

    public ASTProxy(IAST delegate, int firstIndex) {
      this.fDelegate = delegate;
      this.fFirstIndex = firstIndex;
    }

    @Override
    public IExpr arg1() {
      return fDelegate.get(fFirstIndex);
    }

    @Override
    public IExpr arg2() {
      return fDelegate.get(fFirstIndex + 1);
    }

    @Override
    public IExpr arg3() {
      return fDelegate.get(fFirstIndex + 2);
    }

    @Override
    public IExpr arg4() {
      return fDelegate.get(fFirstIndex + 3);
    }

    @Override
    public IExpr arg5() {
      return fDelegate.get(fFirstIndex + 4);
    }

    @Override
    public Set<IExpr> asSet() {
      return fDelegate.asSet();
    }

    public IAST oopy() {
      return new ASTProxy(fDelegate, fFirstIndex);
    }

    @Override
    public IASTAppendable copyAppendable() {
      return fDelegate.copyFrom(fFirstIndex);
    }

    @Override
    public IASTAppendable copyAppendable(int additionalCapacity) {
      return copyAppendable();
    }

    @Override
    public IASTMutable copy() {
      return fDelegate.copyFrom(fFirstIndex);
    }

    @Override
    public IExpr head() {
      return fDelegate.head();
    }

    @Override
    public IExpr get(int location) {
      if (location == 0) {
        return fDelegate.head();
      }
      return fDelegate.get(fFirstIndex + location - 1);
    }

    @Override
    public IAST getItems(int[] items, int length) {
      AST result = new AST(length, true);
      result.set(0, head());
      for (int i = 0; i < length; i++) {
        result.set(i + 1, get(items[i]));
      }
      return result;
    }

    @Override
    public int size() {
      return fDelegate.size() - fFirstIndex + 1;
    }

    @Override
    public IExpr[] toArray() {
      throw new UnsupportedOperationException("ASTProxy#toArray()");
    }

    /** {@inheritDoc} */
    @Override
    public IAST rest() {
      int last = size();
      switch (last) {
        case 1:
          return this;
        case 2:
          return F.headAST0(head());
      }
      return new ASTProxy(fDelegate, fFirstIndex + 1);
    }

    /** {@inheritDoc} */
    @Override
    public IAST removeFromStart(int firstPosition) {
      if (firstPosition == 1) {
        return this;
      }
      if (0 < firstPosition && firstPosition <= size()) {
        int last = size();
        int size = last - firstPosition + 1;
        switch (size) {
          case 1:
            return F.headAST0(head());
          case 2:
            return F.unaryAST1(head(), get(last - 1));
        }
      }
      return new ASTProxy(fDelegate, fFirstIndex + firstPosition - 1);
    }

    @Override
    public IExpr set(int i, IExpr object) {
      throw new UnsupportedOperationException("ASTProxy#set()");
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
      fDelegate = (IAST) objectInput.readObject();
      fFirstIndex = 1;
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
      IASTAppendable ast = copyAppendable();
      objectOutput.writeObject(ast);
    }
  }

  public static AST newInstance(final IExpr head) {
    AST ast = new AST(5, false);
    ast.append(head);
    return ast;
  }

  protected static AST newInstance(final int intialCapacity, final IAST ast, int endPosition) {
    if (Config.MAX_AST_SIZE < intialCapacity) {
      ASTElementLimitExceeded.throwIt(intialCapacity);
    }
    AST result = new AST(intialCapacity, false);
    result.appendAll(ast, 0, endPosition);
    return result;
  }

  /**
   * @param initialCapacity the initial capacity (i.e. number of arguments without the header
   *     element) of the list.
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param initNull initialize all elements with <code>null</code>.
   * @return
   */
  public static AST newInstance(final int initialCapacity, final IExpr head, boolean initNull) {
    if (Config.MAX_AST_SIZE < initialCapacity || initialCapacity < 0) {
      ASTElementLimitExceeded.throwIt(initialCapacity);
    }
    AST ast = new AST(initialCapacity, initNull);
    if (initNull) {
      ast.set(0, head);
    } else {
      ast.append(head);
    }
    return ast;
  }

  public static AST newInstance(final int initialCapacity, final IExpr head) {
    if (Config.MAX_AST_SIZE < initialCapacity || initialCapacity < 0) {
      ASTElementLimitExceeded.throwIt(initialCapacity);
    }
    AST ast = new AST(initialCapacity);
    ast.append(head);
    return ast;
  }

  /**
   * Create a new function expression (AST - abstract syntax tree), where all arguments are Java
   * {@link ComplexNum} values.
   *
   * @param symbol
   * @param evalComplex if <code>true</code> test if the imaginary part of the complex number is
   *     zero and insert a {@link Num} real value.
   * @param arr the complex number arguments
   * @return
   */
  public static AST newInstance(
      final ISymbol symbol, boolean evalComplex, final org.hipparchus.complex.Complex... arr) {
    if (Config.MAX_AST_SIZE < arr.length) {
      ASTElementLimitExceeded.throwIt(arr.length);
    }
    IExpr[] eArr = new IExpr[arr.length + 1];
    eArr[0] = symbol;
    if (evalComplex) {
      double im;
      for (int i = 1; i <= arr.length; i++) {
        im = arr[i - 1].getImaginary();
        if (F.isZero(im)) {
          eArr[i] = Num.valueOf(arr[i - 1].getReal());
        } else {
          eArr[i] = ComplexNum.valueOf(arr[i - 1]);
        }
      }
    } else {
      for (int i = 1; i <= arr.length; i++) {
        eArr[i] = ComplexNum.valueOf(arr[i - 1]);
      }
    }
    return new AST(eArr);
  }

  /**
   * Constructs a list with header <i>symbol</i> and the arguments containing the given DoubleImpl
   * values.
   *
   * @param symbol
   * @param arr
   * @return
   */
  public static AST newInstance(final ISymbol symbol, final double... arr) {
    if (Config.MAX_AST_SIZE < arr.length) {
      ASTElementLimitExceeded.throwIt(arr.length);
    }
    IExpr[] eArr = new IExpr[arr.length + 1];
    eArr[0] = symbol;
    for (int i = 1; i <= arr.length; i++) {
      eArr[i] = Num.valueOf(arr[i - 1]);
    }
    return new AST(eArr);
  }

  /**
   * Constructs a list with header <i>symbol</i> and the arguments containing the given DoubleImpl
   * matrix values as <i>List</i> rows
   *
   * @param symbol
   * @param matrix
   * @return
   * @see Num
   */
  public static AST newInstance(final ISymbol symbol, final double[][] matrix) {
    if (Config.MAX_AST_SIZE < matrix.length) {
      ASTElementLimitExceeded.throwIt(matrix.length);
    }
    IExpr[] eArr = new IExpr[matrix.length + 1];
    eArr[0] = symbol;
    for (int i = 1; i <= matrix.length; i++) {
      eArr[i] = newInstance(S.List, matrix[i - 1]);
    }
    return new AST(eArr);
  }

  public static AST newInstance(final ISymbol symbol, final int... arr) {
    if (Config.MAX_AST_SIZE < arr.length) {
      ASTElementLimitExceeded.throwIt(arr.length);
    }
    IExpr[] eArr = new IExpr[arr.length + 1];
    eArr[0] = symbol;
    for (int i = 1; i <= arr.length; i++) {
      eArr[i] = AbstractIntegerSym.valueOf(arr[i - 1]);
    }
    return new AST(eArr);
  }

  /**
   * simple parser to simplify unit tests. The parser assumes that the String contains no syntax
   * errors.
   *
   * <p>Example &quot;List[x,List[y]]&quot;
   *
   * @param inputString
   * @return
   */
  public static IAST parse(final String inputString) {
    final StringTokenizer tokenizer = new StringTokenizer(inputString, "[],", true);
    String token = tokenizer.nextToken();
    final IASTAppendable list = newInstance(StringX.valueOf(token));
    token = tokenizer.nextToken();
    if ("[".equals(token)) {
      parseList(tokenizer, list);
      return list;
    }
    // syntax fError occured
    return null;
  }

  private static void parseList(final StringTokenizer tokenizer, final IASTAppendable list) {
    String token = tokenizer.nextToken();
    do {
      if ("]".equals(token)) {
        return;
      } else if (" ".equals(token)) {
        // ignore spaces
      } else {
        String arg;
        if (",".equals(token)) {
          arg = tokenizer.nextToken();
        } else {
          arg = token;
        }
        token = tokenizer.nextToken();
        if ("[".equals(token)) {
          IASTAppendable argList = newInstance(StringX.valueOf(arg));
          parseList(tokenizer, argList);
          list.append(argList);
        } else {
          list.append(StringX.valueOf(arg));
          continue;
        }
      }
      token = tokenizer.nextToken();
    } while (tokenizer.hasMoreTokens());
  }

  /** Public no-arg constructor only needed for serialization */
  public AST() {
    super(0);
  }

  /* package private */ AST(IExpr head, IExpr... exprs) {
    super(head, exprs);
  }

  /**
   * Package private constructor.
   *
   * @param es
   */
  /* package private */ AST(IExpr[] exprs) {
    super(exprs);
  }

  /**
   * Constructs an empty list with the specified initial capacity.
   *
   * @param initialCapacity the initial capacity (i.e. number of arguments without the header
   *     element) of the list.
   * @param setLength if <code>true</code>, sets the array's size to initialCapacity.
   */
  protected AST(final int initialCapacity, final boolean setLength) {
    super(initialCapacity + 1);
    lastIndex += (setLength ? initialCapacity + 1 : 0);
  }

  protected AST(final int initialCapacity) {
    super(initialCapacity + 1);
  }

  /** {@inheritDoc} */
  @Override
  public IAST appendOneIdentity(IAST value) {
    if (value.isAST1()) {
      append(value.arg1());
    } else {
      append(value);
    }
    return this;
  }

  /**
   * Returns a shallow copy of this <tt>AST</tt> instance. (The elements themselves are not copied.)
   *
   * @return a clone of this <tt>AST</tt> instance.
   */
  //	@Override
  //	public IAST clone() {
  //		 throw new UnsupportedOperationException();
  ////		AST ast = new AST();
  ////		// ast.fProperties = null;
  ////		ast.array = array.clone();
  ////		ast.hashValue = 0;
  ////		ast.firstIndex = firstIndex;
  ////		ast.lastIndex = lastIndex;
  ////		return ast;
  //	}

  @Override
  public IASTAppendable copyAppendable() {
    AST ast = new AST();
    // ast.fProperties = null;
    ast.array = array.clone();
    ast.hashValue = 0;
    ast.firstIndex = firstIndex;
    ast.lastIndex = lastIndex;
    return ast;
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    AST ast = new AST();
    // ast.fProperties = null;
    if (size() + additionalCapacity > array.length) {
      ast.array = new IExpr[size() + additionalCapacity];
      System.arraycopy(array, 0, ast.array, 0, array.length);
    } else {
      ast.array = array.clone();
    }
    ast.hashValue = 0;
    ast.firstIndex = firstIndex;
    ast.lastIndex = lastIndex;
    return ast;
  }

  @Override
  public IAST getItems(int[] items, int length) {
    AST result = new AST(length, true);
    result.set(0, head());
    for (int i = 0; i < length; i++) {
      result.set(i + 1, get(items[i]));
    }
    return result;
  }

  @Override
  public IASTMutable copy() {
    switch (size()) {
      case 1:
        return new AST0(head());
      case 2:
        return new AST1(head(), arg1());
      case 3:
        return new AST2(head(), arg1(), arg2());
      case 4:
        return new AST3(head(), arg1(), arg2(), arg3());
    }
    AST ast = new AST();
    // ast.fProperties = null;
    ast.array = array.clone();
    ast.hashValue = 0;
    ast.firstIndex = firstIndex;
    ast.lastIndex = lastIndex;
    return ast;
  }

  /** {@inheritDoc} */
  @Override
  public IAST rest() {
    switch (size()) {
      case 1:
        return this;
      case 2:
        return F.headAST0(head());
      case 3:
        return F.unaryAST1(head(), arg2());
        // case 4:
        // return F.binaryAST2(head(), arg2(), arg3());
        // case 5:
        // return F.ternaryAST3(head(), arg2(), arg3(), arg4());
      default:
        if (isOrderlessAST()) {
          return super.rest();
        }
        return new ASTProxy(this, 2);
    }
  }

  @Override
  public IAST removeFromEnd(int fromPosition) {
    if (0 < fromPosition && fromPosition <= size()) {
      if (fromPosition == size()) {
        return this;
      }
      AST ast = new AST(array);
      ast.firstIndex = firstIndex;
      ast.lastIndex = firstIndex + fromPosition;
      return ast;
    } else {
      throw new IndexOutOfBoundsException(
          "Index: "
              + Integer.valueOf(fromPosition)
              + ", Size: "
              + Integer.valueOf(lastIndex - firstIndex));
    }
  }

  @Override
  public IAST removeFromStart(int firstPosition) {
    if (firstPosition == 1) {
      return this;
    }
    if (0 < firstPosition && firstPosition <= size()) {
      int last = size();
      int size = last - firstPosition + 1;
      switch (size) {
        case 1:
          return F.headAST0(head());
        case 2:
          return F.unaryAST1(head(), get(last - 1));
          // case 3:
          // return F.binaryAST2(head(), get(last - 2), get(last - 1));
          // case 4:
          // return F.ternaryAST3(head(), get(last - 3), get(last - 2), get(last - 1));
        default:
          if (isOrderlessAST()) {
            return copyFrom(firstPosition);
          }
          return new ASTProxy(this, firstPosition);
      }
    } else {
      throw new IndexOutOfBoundsException(
          "Index: " + Integer.valueOf(firstPosition) + ", Size: " + size());
    }
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    this.fEvalFlags = objectInput.readShort();

    int size;
    byte attributeFlags = objectInput.readByte();
    if (attributeFlags != 0) {
      size = attributeFlags;
      IExpr[] array = new IExpr[size];
      init(array);
      int exprIDSize = objectInput.readByte();
      for (int i = 0; i < exprIDSize; i++) {
        this.array[i] = S.exprID(objectInput.readShort()); // F.GLOBAL_IDS[objectInput.readShort()];
      }
      for (int i = exprIDSize; i < size; i++) {
        this.array[i] = (IExpr) objectInput.readObject();
      }
      return;
    }

    size = objectInput.readInt();
    IExpr[] array = new IExpr[size];
    init(array);
    for (int i = 0; i < size; i++) {
      this.array[i] = (IExpr) objectInput.readObject();
    }
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeShort(fEvalFlags);

    int size = size();
    byte attributeFlags = (byte) 0;
    if (size > 0 && size < 128) {
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
