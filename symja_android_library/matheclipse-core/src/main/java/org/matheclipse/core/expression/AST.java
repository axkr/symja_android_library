package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.EnumMap;
import java.util.List;
import java.util.StringTokenizer;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * (A)bstract (S)yntax (T)ree of a given function.
 * </p>
 * 
 * <p>
 * In Symja, an abstract syntax tree (AST), is a tree representation of the abstract syntactic structure of the Symja source code.
 * Each node of the tree denotes a construct occurring in the source code. The syntax is 'abstract' in the sense that it does not
 * represent every detail that appears in the real syntax. For instance, grouping parentheses are implicit in the tree structure,
 * and a syntactic construct such as a <code>Sin[x]</code> expression will be denoted by an AST with 2 nodes. One node for the
 * header <code>Sin</code> and one node for the argument <code>x</code>.
 * </p>
 * 
 * Internally an AST is represented as a <code>java.util.List</code> which contains
 * <ul>
 * <li>the operator of a function (i.e. the &quot;header&quot;-symbol: Sin, Cos, Inverse, Plus, Times,...) at index <code>0</code>
 * and</li>
 * <li>the <code>n</code> arguments of a function in the index <code>1 to n</code></li>
 * </ul>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract syntax tree</a>.
 */
public class AST extends HMArrayList implements Externalizable {

	/**
	 * The enumeration map which possibly maps the properties (keys) to a user defined object.
	 * 
	 */
	protected transient EnumMap<PROPERTY, Object> fProperties = null;

	/**
	 * Returns the value to which the specified property is mapped, or <code>null</code> if this map contains no mapping for the
	 * property.
	 * 
	 * @param property
	 * @return
	 * @see #putProperty(PROPERTY, Object)
	 */
	public Object getProperty(PROPERTY key) {
		if (fProperties == null) {
			return null;
		}
		return fProperties.get(key);
	}

	@Override
	public int hashCode() {
		if (hashValue == 0) {
			hashValue = 17;
			for (int i = firstIndex; i < lastIndex; i++) {
				// http://stackoverflow.com/questions/4948780/magic-number-in-boosthash-combine
				// hashValue ^= array[i].hashCode() + 0x9e3779b9 + (hashValue << 6) + (hashValue >> 2);
				hashValue = 23 * hashValue + array[i].hashCode();
			}
		}
		return hashValue;
	}

	/**
	 * Associates the specified value with the specified property in the associated <code>EnumMap<PROPERTY, Object></code> map. If
	 * the map previously contained a mapping for this key, the old value is replaced.
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @see #getProperty(PROPERTY)
	 */
	public Object putProperty(PROPERTY key, Object value) {
		if (fProperties == null) {
			fProperties = new EnumMap<PROPERTY, Object>(PROPERTY.class);
		}
		return fProperties.put(key, value);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4295200630292148027L;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *            the initial capacity (i.e. number of arguments without the header element) of the list.
	 * @param setLength
	 *            if <code>true</code>, sets the array's size to initialCapacity.
	 */
	protected AST(final int initialCapacity, final boolean setLength) {
		// super(initialCapacity + 1, setLength ? initialCapacity + 1 : 0);
		super(initialCapacity + 1);
		// for (int i = 0; i < setLength; i++) {
		// add(null);
		// }
		lastIndex += (setLength ? initialCapacity + 1 : 0);
		modCount++;
	}

	/**
	 * Public no-arg constructor only needed for serialization
	 * 
	 */
	public AST() {
		// super(0);
		super(0);
		// add(null);
		lastIndex++;
		modCount++;
	}

	/**
	 * Package private constructor.
	 * 
	 * @param es
	 */
	AST(IExpr[] es) {
		super(es);
	}

	public AST(IExpr head, IExpr... es) {
		super(head, es);
	}

	/**
	 * simple parser to simplify unit tests. The parser assumes that the String contains no syntax errors.
	 * 
	 * Example &quot;List[x,List[y]]&quot;
	 */
	public static IAST parse(final String inputString) {
		final StringTokenizer tokenizer = new StringTokenizer(inputString, "[],", true);
		String token = tokenizer.nextToken();
		final IAST list = newInstance(StringX.valueOf(token));
		// list.setHeader(StringX.valueOf(token));
		token = tokenizer.nextToken();
		if (token.equals("[")) {
			parseList(tokenizer, list);
			return list;
		}
		// syntax fError occured
		return null;

	}

	private static void parseList(final StringTokenizer tokenizer, final IAST list) {
		String token = tokenizer.nextToken();
		String arg;
		IAST argList;
		do {
			if (token.equals("]")) {
				return;
			} else if (token.equals(",")) {
				arg = tokenizer.nextToken();
				token = tokenizer.nextToken();
				if (token.equals("[")) {
					argList = newInstance(StringX.valueOf(arg));
					// argList.setHeader(StringX.valueOf(arg));
					parseList(tokenizer, argList);
					list.add(argList);
				} else {
					list.add(StringX.valueOf(arg));
					continue;
				}
			} else if (token.equals(" ")) {
				// ignore spaces
			} else {
				arg = token;
				token = tokenizer.nextToken();
				if (token.equals("[")) {
					argList = newInstance(StringX.valueOf(arg));
					// argList.setHeader(StringX.valueOf(arg));
					parseList(tokenizer, argList);
					list.add(argList);
				} else {
					list.add(StringX.valueOf(arg));
					continue;
				}
			}
			token = tokenizer.nextToken();
		} while (tokenizer.hasMoreTokens());
	}

	/**
	 * Returns a shallow copy of this <tt>AST</tt> instance. (The elements themselves are not copied.)
	 * 
	 * @return a clone of this <tt>AST</tt> instance.
	 */
	@Override
	public AST clone() {
		AST ast = (AST) super.clone();
		// ast.fPatternMatchingHashValue = 0;
		ast.fProperties = null;
		return ast;
	}

	/** {@inheritDoc} */
	public IAST addOneIdentity(IAST value) {
		if (value.size() == 2) {
			add(value.arg1());
		} else {
			add(value);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean addAll(List<? extends IExpr> ast) {
		return addAll(ast, 1, ast.size());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(List<? extends IExpr> ast, int startPosition, int endPosition) {
		if (ast.size() > 0 && startPosition < endPosition) {
			ensureCapacity(size() + (endPosition - startPosition));
			for (int i = startPosition; i < endPosition; i++) {
				add(ast.get(i));
			}
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param intialCapacity
	 *            the initial capacity (i.e. number of arguments without the header element) of the list.
	 * @param head
	 * @return
	 */
	public static AST newInstance(final int intialCapacity, final IExpr head) {
		AST ast = new AST(intialCapacity + 1, false);
		ast.add(head);
		return ast;
	}

	public static AST newInstance(final IExpr head) {
		AST ast = new AST(5, false);
		ast.add(head);
		return ast;
	}

	protected static AST newInstance(final IAST ast, int endPosition) {
		AST result = new AST(5, false);
		result.addAll(ast, 0, endPosition);
		return result;
	}

	public static AST newInstance(final ISymbol symbol, final int... arr) {
		IExpr[] eArr = new IExpr[arr.length + 1];
		eArr[0] = symbol;
		for (int i = 1; i <= arr.length; i++) {
			eArr[i] = IntegerSym.valueOf(arr[i - 1]);
		}
		return new AST(eArr);
	}

	/**
	 * Constructs a list with header <i>symbol</i> and the arguments containing the given DoubleImpl values.
	 * 
	 * @see Num
	 */
	public static AST newInstance(final ISymbol symbol, final double... arr) {
		IExpr[] eArr = new IExpr[arr.length + 1];
		eArr[0] = symbol;
		for (int i = 1; i <= arr.length; i++) {
			eArr[i] = Num.valueOf(arr[i - 1]);
		}
		return new AST(eArr);
	}

	public static AST newInstance(final ISymbol symbol, final org.apache.commons.math4.complex.Complex... arr) {
		IExpr[] eArr = new IExpr[arr.length + 1];
		eArr[0] = symbol;
		for (int i = 1; i <= arr.length; i++) {
			eArr[i] = ComplexNum.valueOf(arr[i - 1].getReal(), arr[i - 1].getImaginary());
		}
		return new AST(eArr);
	}

	/**
	 * Constructs a list with header <i>symbol</i> and the arguments containing the given DoubleImpl matrix values as <i>List</i>
	 * rows
	 * 
	 * @see Num
	 */
	public static AST newInstance(final ISymbol symbol, final double[][] matrix) {
		IExpr[] eArr = new IExpr[matrix.length + 1];
		eArr[0] = symbol;
		for (int i = 1; i <= matrix.length; i++) {
			eArr[i] = newInstance(F.List, matrix[i - 1]);
		}
		return new AST(eArr);
	}

	// private void writeObject(ObjectOutputStream stream) throws IOException {
	// int size = size();
	// IExpr temp;
	// stream.writeInt(size);
	// // don't use an iterator here!
	// for (int i = 0; i < size; i++) {
	// temp = get(i);
	// stream.writeObject(temp);
	// }
	// }
	//
	// private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
	// int size = stream.readInt();
	// lastIndex = size;
	// array = new IExpr[size];
	// // IExpr temp;
	// for (int i = 0; i < lastIndex; i++) {
	// array[i] = (IExpr) stream.readObject();
	// }
	// }
	private Object writeReplace() throws ObjectStreamException {
		ExprID temp = F.GLOBAL_IDS_MAP.get(this);
		if (temp != null) {
			return temp;
		}
		return this;
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeShort(fEvalFlags);

		int size = size();
		byte attributeFlags = (byte) 0;
		if (size > 0 && size < 128) {
			ExprID temp = F.GLOBAL_IDS_MAP.get(head());
			if (temp != null) {

				short exprID = temp.getExprID();
				if (exprID <= Short.MAX_VALUE) {
					int exprIDSize = 1;
					short[] exprIDArray = new short[size];
					exprIDArray[0] = exprID;
					for (int i = 1; i < size; i++) {
						temp = F.GLOBAL_IDS_MAP.get(get(i));
						if (temp == null) {
							break;
						}
						exprID = temp.getExprID();
						if (exprID <= Short.MAX_VALUE) {
							exprIDArray[i] = exprID;
							exprIDSize++;
						} else {
							break;
						}
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
		}

		objectOutput.writeByte(attributeFlags);
		objectOutput.writeInt(size);
		for (int i = 0; i < size; i++) {
			objectOutput.writeObject(get(i));
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
				this.array[i] = F.GLOBAL_IDS[objectInput.readShort()];
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

}