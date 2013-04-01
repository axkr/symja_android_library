package org.matheclipse.core.expression;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.Util;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.generic.IsUnaryVariableOrPattern;
import org.matheclipse.core.generic.UnaryVariable2Slot;
import org.matheclipse.core.generic.util.NestedFastTable;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.VisitorReplaceAll;
import org.matheclipse.core.visit.VisitorReplacePart;
import org.matheclipse.core.visit.VisitorReplaceSlots;
import org.matheclipse.generic.interfaces.BiFunction;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import edu.jas.structure.ElemFactory;

/**
 * 
 * <p>
 * (A)bstract (S)yntax (T)ree of a given function.
 * </p>
 * 
 * <p>
 * In MathEclipse, an abstract syntax tree (AST), is a tree representation of
 * the abstract syntactic structure of the MathEclipse source code. Each node of
 * the tree denotes a construct occurring in the source code. The syntax is
 * 'abstract' in the sense that it does not represent every detail that appears
 * in the real syntax. For instance, grouping parentheses are implicit in the
 * tree structure, and a syntactic construct such as a <code>Sin[x]</code>
 * expression will be denoted by an AST with 2 nodes. One node for the header
 * <code>Sin</code> and one node for the argument <code>x</code>.
 * </p>
 * 
 * Internally an AST is represented as a <code>java.util.List</code> which
 * contains
 * <ul>
 * <li>the operator of a function (i.e. the &quot;header&quot;-symbol: Sin, Cos,
 * Inverse, Plus, Times,...) at index <code>0</code> and</li>
 * <li>the <code>n</code> arguments of a function in the index
 * <code>0 to n</code></li>
 * </ul>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract
 * syntax tree</a>.
 */
public class AST extends NestedFastTable<IExpr> implements IAST {
	private final static IAST AST_DUMMY_INSTANCE = new AST();

	public final static ASTCopy COPY = new ASTCopy((Class<IAST>) AST_DUMMY_INSTANCE.getClass());

	/**
	 * 
	 */
	private static final long serialVersionUID = 4295200630292148027L;

	/**
	 * Flags for controlling evaluation and left-hand-side pattern-matching
	 * expressions
	 * 
	 */
	transient private int fEvalFlags = 0;

	transient protected int fPatternMatchingHashValue = 0;

	/**
	 * Holds the factory for this AST.
	 */
	// private static final ObjectFactory<AST> FACTORY = new
	// ObjectFactory<AST>() {
	// @Override
	// public AST create() {
	// if (Config.SERVER_MODE && currentQueue().getSize() >=
	// Config.AST_MAX_POOL_SIZE) {
	// throw new PoolMemoryExceededException("AST", currentQueue().getSize());
	// }
	// return new AST(5, false);
	// }
	//
	// @Override
	// public void cleanup(AST obj) {
	// obj.reset();
	// }
	//
	// };

	/**
	 * simple parser to simplify unit tests. The parser assumes that the String
	 * contains no syntax errors.
	 * 
	 * Example &quot;List[x,List[y]]&quot;
	 */
	public static AST parse(final String inputString) {
		final StringTokenizer tokenizer = new StringTokenizer(inputString, "[],", true);
		final AST list = newInstance(null);
		String token = tokenizer.nextToken();
		list.setHeader(StringX.valueOf(token));
		token = tokenizer.nextToken();
		if (token.equals("[")) {
			parseList(tokenizer, list);
			return list;
		}
		// syntax fError occured
		return null;

	}

	private static void parseList(final StringTokenizer tokenizer, final AST list) {
		String token = tokenizer.nextToken();
		String arg;
		AST argList;
		do {
			if (token.equals("]")) {
				return;
			} else if (token.equals(",")) {
				arg = tokenizer.nextToken();
				token = tokenizer.nextToken();
				if (token.equals("[")) {
					argList = newInstance(null);
					argList.setHeader(StringX.valueOf(arg));
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
					argList = newInstance(null);
					argList.setHeader(StringX.valueOf(arg));
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
	 * Constructs an empty list with an initial capacity of five.
	 */
	// private AST() {
	// super(5);
	// }
	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	// private AST(final Collection<IExpr> c) {
	// super(c);
	// }
	/**
	 * Constructs a list containing the elements of the specified collection, in
	 * the order they are returned by the collection's iterator. The <tt>AST</tt>
	 * instance has an initial capacity of 110% the size of the specified
	 * collection.
	 * 
	 * @param c
	 *          the collection whose elements are to be placed into this list.
	 * @throws NullPointerException
	 *           if the specified collection is null.
	 */
	// private AST(final Collection<IExpr> c, final IExpr head) {
	// super(c, head);
	// }
	//
	// private AST(final IExpr[] arr, final IExpr head) {
	// super(head);
	// for (final IExpr expr : arr) {
	// add(expr);
	// }
	// }
	/*
	 * Creates a new list form the given list and symbol. if incl is set to <code>
	 * true </code> all arguments from index first to last-1 are copied in the new
	 * list if incl is set to <code> false </code> all arguments excluded from
	 * index first to last-1 are copied in the new list
	 */
	// private AST(final IAST f, final IExpr sym, final boolean incl,
	// final int first, final int last) {
	// super(sym);
	// if (incl == true) {
	// // range include
	// for (int i = first; i < last; i++) {
	// add(f.get(i));
	// }
	// } else {
	// // range exclude
	// for (int i = 0; i < first; i++) {
	// add(f.get(i));
	// }
	// for (int j = last; j < f.size(); j++) {
	// add(f.get(j));
	// }
	// }
	// }
	/**
	 * Constructs an empty list with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *          the initial capacity of the list.
	 * @exception IllegalArgumentException
	 *              if the specified initial capacity is negative
	 */
	// private AST(final int initialCapacity, final IExpr head) {
	// super(initialCapacity + 1);
	// setHeader(head);
	// }
	/**
	 * Constructs an empty list with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *          the initial capacity (i.e. number of arguments without the header
	 *          element) of the list.
	 * @param setLength
	 *          if <code>true</code>, sets the array's size to initialCapacity.
	 */
	private AST(final int initialCapacity, final boolean setLength) {
		super(initialCapacity + 1, setLength ? initialCapacity + 1 : 0);
	}

	/**
	 * Public no-arg constructor only needed for serialization
	 * 
	 */
	public AST() {
		super(0);
	}

	/**
	 * Constructs a list with header <i>symbol</i> and the arguments containing
	 * the given DoubleImpl values.
	 * 
	 * @see DoubleImpl
	 */
	// public AST(final ISymbol symbol, final double[] arr) {
	// this(arr.length, true);
	// for (int i = 1; i <= arr.length; i++) {
	// set(i, new DoubleImpl(arr[i - 1]));
	// }
	// setHeader(symbol);
	// }
	/**
	 * Constructs a list with header <i>symbol</i> and the arguments containing
	 * the given DoubleImpl matrix values as <i>List</i> rows
	 * 
	 * @see DoubleImpl
	 */
	// private AST(final ISymbol symbol, final double[][] matrix) {
	// this(matrix.length, true);
	// AST row;
	// final AbstractExpressionFactory f = EvalEngine.get()
	// .getExpressionFactory();
	// for (int i = 1; i <= matrix.length; i++) {
	// row = new AST(AbstractExpressionFactory.List, matrix[i - 1]);
	// set(i, row);
	// }
	// setHeader(symbol);
	// }
	/**
	 * Constructs an AST with header <i>symbol</i> and the arguments containing
	 * the given IntegerImpl values.
	 * 
	 * @see IntegerImpl
	 */
	// private AST(final ISymbol symbol, final int[] arr) {
	// this(arr.length, true);
	//
	// for (int i = 1; i <= arr.length; i++) {
	// set(i, IntegerImpl.valueOf(arr[i - 1]));
	// }
	// setHeader(symbol);
	// }
	/**
	 * Constructs an AST with header <i>symbol</i> and the arguments containing
	 * the given DoubleComplexImpl values.
	 * 
	 * @see DoubleComplexImpl
	 */
	// private AST(final ISymbol symbol, final DoubleComplexImpl[] arr) {
	// this(arr.length, true);
	// for (int i = 1; i <= arr.length; i++) {
	// set(i, arr[i - 1]);
	// }
	// setHeader(symbol);
	// }
	/**
	 * Returns a shallow copy of this <tt>AST</tt> instance. (The elements
	 * themselves are not copied.)
	 * 
	 * @return a clone of this <tt>AST</tt> instance.
	 */
	@Override
	public IAST clone() {
		AST ast = (AST) super.clone();
		ast.fEvalFlags = 0;
		ast.fPatternMatchingHashValue = 0;
		return ast;
	}

	@Override
	public IExpr copy() {
		return clone();
	}

	public boolean equalsFromPosition(final int from0, final AST f1, final int from1) {
		if ((size() - from0) != (f1.size() - from1)) {
			return false;
		}

		int j = from1;

		for (int i = from0; i < size() - 1; i++) {
			if (!get(i + 1).equals(f1.get(1 + j++))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns the ISymbol of the IAST. If the head itself is a IAST it will
	 * recursively call head().
	 */
	public ISymbol topHead() {
		IExpr header = head();
		if (header instanceof ISymbol) {
			// this should be the "most common" case:
			return (ISymbol) header;
		}
		if (header instanceof IAST) {
			// determine the head recursively
			return ((IAST) header).topHead();
		}
		// * Numbers return the header strings
		// * "DoubleComplex", "Double", "Integer", "Fraction", "Complex"
		if (header.isSignedNumber()) {
			if (header instanceof INum) {
				return F.RealHead;
			}
			if (header instanceof IInteger) {
				return F.IntegerHead;
			}
			if (header instanceof IFraction) {
				return F.RationalHead;
			}
		}
		if (header instanceof IComplex) {
			return F.ComplexHead;
		}
		if (header instanceof IComplexNum) {
			return F.ComplexHead;
		}
		if (header instanceof IPattern) {
			return F.PatternHead;
		}
		if (head() instanceof IStringX) {
			return F.StringHead;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.matheclipse.parser.interfaces.IExpr#hierarchy()
	 */
	public int hierarchy() {
		return ASTID;
	}

	public boolean isLTOrdered(final IExpr obj) {
		return compareTo(obj) < 0;
	}

	public boolean isLEOrdered(final IExpr obj) {
		return compareTo(obj) <= 0;
	}

	public boolean isGTOrdered(final IExpr obj) {
		return compareTo(obj) > 0;
	}

	public boolean isGEOrdered(final IExpr obj) {
		return compareTo(obj) >= 0;
	}

	/**
	 * @param properties
	 */
	// public void setProperties(ListProperties properties) {
	// fProperties = properties;
	// }
	/**
	 * @return
	 */
	public int getEvalFlags() {
		return fEvalFlags;
	}

	/**
	 * Set the flags to this value
	 */
	public void setEvalFlags(final int i) {
		fEvalFlags = i;
	}

	/**
	 * Add a new flag to the existing flags
	 */
	public void addEvalFlags(final int i) {
		fEvalFlags |= i;
	}

	/**
	 * @return
	 */
	public boolean isEvalFlagOn(final int i) {
		return (fEvalFlags & i) == i;
	}

	/**
	 * @return
	 */
	public boolean isEvalFlagOff(final int i) {
		return (fEvalFlags & i) == 0;
	}

	public IExpr opposite() {
		return F.function(F.Times, F.CN1, this);
	}

	public IExpr plus(final IExpr that) {
		return F.function(F.Plus, this, that);
	}

	public IExpr inverse() {
		return F.function(F.Power, this, F.CN1);
	}

	public IExpr times(final IExpr that) {
		return F.function(F.Times, this, that);
	}

	public boolean isList() {
		return head().equals(F.List);
	}

	public boolean isSequence() {
		return head().equals(F.Sequence);
	}

	public boolean isListOfLists() {
		if (head().equals(F.List)) {
			for (int i = 2; i < size(); i++) {
				if (!get(i).isList()) {
					// the row is no list
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean isPlus() {
		return size() >= 3 && head().equals(F.Plus);
	}

	public boolean isPower() {
		return size() == 3 && head().equals(F.Power);
	}

	public boolean isTimes() {
		return size() >= 3 && head().equals(F.Times);
	}

	public boolean isSin() {
		return size() == 2 && head().equals(F.Sin);
	}

	public boolean isCos() {
		return size() == 2 && head().equals(F.Cos);
	}

	public boolean isTan() {
		return size() == 2 && head().equals(F.Tan);
	}

	public boolean isArcSin() {
		return size() == 2 && head().equals(F.ArcSin);
	}

	public boolean isArcCos() {
		return size() == 2 && head().equals(F.ArcCos);
	}

	public boolean isArcTan() {
		return size() == 2 && head().equals(F.ArcTan);
	}

	public boolean isSinh() {
		return size() == 2 && head().equals(F.Sinh);
	}

	public boolean isSlot() {
		return size() == 2 && head().equals(F.Slot) && get(1).isInteger();
	}

	public boolean isSlotSequence() {
		return size() == 2 && head().equals(F.SlotSequence) && get(1).isInteger();
	}

	public boolean isCosh() {
		return size() == 2 && head().equals(F.Cosh);
	}

	public boolean isTanh() {
		return size() == 2 && head().equals(F.Tanh);
	}

	public boolean isArcSinh() {
		return size() == 2 && head().equals(F.ArcSinh);
	}

	public boolean isArcCosh() {
		return size() == 2 && head().equals(F.ArcCosh);
	}

	public boolean isArcTanh() {
		return size() == 2 && head().equals(F.ArcTanh);
	}

	public boolean isLog() {
		return size() == 2 && head().equals(F.Log);
	}

	public boolean isOne() {
		return false;
	}

	public boolean isMinusOne() {
		return false;
	}

	public boolean isZero() {
		return false;
	}

	public boolean isTrue() {
		return false;
	}

	public boolean isFalse() {
		return false;
	}

	public boolean isSame(IExpr expression) {
		return equals(expression);
	}

	public boolean isSame(IExpr expression, double epsilon) {
		return equals(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public int[] isMatrix() {
		if (isEvalFlagOn(IAST.IS_MATRIX)) {
			final int[] dim = new int[2];
			dim[0] = size() - 1;
			// if (size() <= 1) {
			// dim[1] = 0;
			// } else {
			dim[1] = ((IAST) get(1)).size() - 1;
			// }
			return dim;
		}
		if (head().equals(F.List)) {
			final int[] dim = new int[2];
			dim[0] = size() - 1;
			dim[1] = 0;
			if (dim[0] > 0) {
				if (get(1).isList()) {
					dim[1] = ((IAST) get(1)).size() - 1;

					for (int i = 2; i < size(); i++) {
						if (!get(i).isList()) {
							// row is no list
							return null;
						}
						if (dim[1] != ((IAST) get(i)).size() - 1) {
							// not the same length
							return null;
						}
					}
				} else {
					return null;
				}
				addEvalFlags(IAST.IS_MATRIX);
				return dim;
			}

		}
		return null;
	}

	public int isVector() {
		if (isEvalFlagOn(IAST.IS_VECTOR)) {
			return size() - 1;
		}
		if (head().equals(F.List)) {
			final int dim = size() - 1;
			if (dim > 0) {
				if (get(1).isList()) {
					return -1;
				}
				for (int i = 2; i < size(); i++) {
					if (get(i).isList()) {
						// row is a list
						return -1;
					}
				}
			}
			addEvalFlags(IAST.IS_VECTOR);
			return dim;
		}
		return -1;
	}

	public boolean isFraction() {
		return false;
	}

	public boolean isPattern() {
		return false;
	}

	public boolean isPatternSequence() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCondition() {
		return size() == 3 && head().equals(F.Condition);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isModule() {
		return size() == 3 && head().equals(F.Module);
	}

	public boolean isSymbol() {
		return false;
	}

	public boolean isComplex() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isInteger() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isNumIntValue() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isRational() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isSignedNumber() {
		return false;
	}

	/** {@inheritDoc} */
	public boolean isNumeric() {
		return false;
	}

	public boolean isNumber() {
		return false;
	}

	public IAST apply(final IExpr head) {
		final IAST ast = clone();
		ast.setHeader(head);
		return ast;
	}

	public IAST apply(final IExpr head, final int start) {
		return apply(head, start, size());
	}

	public IAST apply(final IExpr head, final int start, final int end) {
		final IAST ast = F.ast(head);
		for (int i = start; i < end; i++) {
			ast.add(get(i));
		}
		return ast;
	}

	public IExpr apply(List<? extends IExpr> leaves) {
		final IAST ast = F.ast(head());
		for (int i = 0; i < leaves.size(); i++) {
			ast.add(leaves.get(i));
		}
		return ast;
	}

	public IExpr apply(IExpr... leaves) {
		final IAST ast = F.ast(head());
		for (int i = 0; i < leaves.length; i++) {
			ast.add(leaves[i]);
		}
		return ast;
	}

	public IAST map(final Function<IExpr, IExpr> function) {
		final IAST f = clone();
		return map(f, function);
	}

	public IAST map(final IExpr head, final Function<IExpr, IExpr> function) {
		final IAST f = clone();
		f.set(0, head);
		return map(f, function);
	}

	public IAST map(final IAST resultAST, final Function<IExpr, IExpr> function) {
		IExpr temp;
		for (int i = 1; i < size(); i++) {
			temp = function.apply(get(i));
			if (temp != null) {
				resultAST.set(i, temp);
			}
		}
		return resultAST;
	}

	public IAST map(IAST resultAST, IAST secondAST, BiFunction<IExpr, IExpr, IExpr> function) {
		for (int i = 1; i < size(); i++) {
			resultAST.add(function.apply(get(i), secondAST.get(i)));
		}
		return resultAST;
	}

	/**
	 * {@inheritDoc}
	 */
	public IExpr replaceAll(final IAST astRules) {
		return this.accept(new VisitorReplaceAll(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	public IExpr replaceAll(final Function<IExpr, IExpr> function) {
		return this.accept(new VisitorReplaceAll(function));
	}

	/**
	 * {@inheritDoc}
	 */
	public IExpr replacePart(final IAST astRules) {
		return this.accept(new VisitorReplacePart(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	public IExpr replaceRepeated(final IAST astRules) {
		return ExprImpl.replaceRepeated(this, new VisitorReplaceAll(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	public IExpr replaceRepeated(final Function<IExpr, IExpr> function) {
		return ExprImpl.replaceRepeated(this, new VisitorReplaceAll(function));
	}

	public IExpr replaceSlots(final IAST astSlots) {
		return this.accept(new VisitorReplaceSlots(astSlots));
	}

	/**
	 * {@inheritDoc}
	 */
	public IAST filter(IAST filterAST, Predicate<IExpr> predicate) {
		return (new ASTRange(this, 1, size())).filter(filterAST, predicate);
	}

	/**
	 * {@inheritDoc}
	 */
	public IAST filter(IAST filterAST, IAST restAST, Predicate<IExpr> predicate) {
		return (new ASTRange(this, 1, size())).filter(filterAST, restAST, predicate);
	}

	/**
	 * {@inheritDoc}
	 */
	public IAST[] split(Predicate<IExpr> predicate) {
		IAST[] result = new IAST[2];
		result[0] = copyHead();
		result[1] = copyHead();
		new ASTRange(this, 1, size()).filter(result[0], result[1], predicate);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public IAST[] split(final Function<IExpr, IExpr> function) {
		IAST[] result = new IAST[2];
		result[0] = copyHead();
		result[1] = copyHead();
		new ASTRange(this, 1, size()).filter(result[0], result[1], function);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAST() {
		return true;
	}

	/** {@inheritDoc} */
	public boolean isOrderlessAST() {
		return ((ISymbol.ORDERLESS & topHead().getAttributes()) == ISymbol.ORDERLESS);
	}

	/** {@inheritDoc} */
	public boolean isFlatAST() {
		return ((ISymbol.FLAT & topHead().getAttributes()) == ISymbol.FLAT);
	}

	/** {@inheritDoc} */
	public boolean isAST(final IExpr header) {
		return get(0).equals(header);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAST(final IExpr header, final int length) {
		return (size() == length) && get(0).equals(header);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isASTSizeGE(final IExpr header, final int length) {
		return (size() >= length) && get(0).equals(header);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAST(final String symbol) {
		return get(0).toString().equals(symbol);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAST(final String symbol, final int length) {
		return (size() == length) && get(0).toString().equals(symbol);
	}

	/** {@inheritDoc} */
	public boolean isRuleAST() {
		return size() == 3 && (head().equals(F.Rule) || head().equals(F.RuleDelayed));
	}

	/** {@inheritDoc} */
	public boolean isFree(final IExpr pattern, boolean heads) {
		final PatternMatcher matcher = new PatternMatcher(pattern);
		return !isMember(matcher, heads);
	}

	/** {@inheritDoc} */
	public boolean isFree(Predicate<IExpr> predicate, boolean heads) {
		return !isMember(predicate, heads);
	}

	/** {@inheritDoc} */
	public boolean isMember(Predicate<IExpr> predicate, boolean heads) {
		if (predicate.apply(this)) {
			return true;
		}
		int start = 1;
		if (heads) {
			start = 0;
		}
		for (int i = start; i < size(); i++) {
			if (get(i).isMember(predicate, heads)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFunction() {
		return size() >= 2 && head().equals(F.Function);
	}

	/**
	 * Compares this (Times) AST with the specified AST for order. Returns a
	 * negative integer, zero, or a positive integer as this (Times) AST is
	 * canonical less than, equal to, or greater than the specified AST.
	 */
	private int compareToTimes(final AST ast) {
		final IExpr astHeader = ast.head();
		int cp;

		if (astHeader == F.Power) {
			// compare from the last this (Times) element:
			final IExpr lastTimes = get(size() - 1);

			if (!(lastTimes instanceof IAST)) {
				cp = lastTimes.compareTo(ast.get(1));
				if (cp != 0) {
					return cp;
				}
				return F.C1.compareTo(ast.get(2));
			} else {
				final IExpr lastTimesHeader = ((IAST) lastTimes).head();
				if ((lastTimesHeader == F.Power) && (((IAST) lastTimes).size() == 3)) {
					// compare 2 Power ast's
					cp = ((IAST) lastTimes).get(1).compareTo(ast.get(1));
					if (cp != 0) {
						return cp;
					}
					cp = ((IAST) lastTimes).get(2).compareTo(ast.get(2));
					if (cp != 0) {
						return cp;
					}
					return 1;
				} else {
					cp = lastTimes.compareTo(ast.get(1));
					if (cp != 0) {
						return cp;
					}
					return F.C1.compareTo(ast.get(2));
				}
			}
		} else if (astHeader == F.Times) {
			// compare from the last element:
			int i0 = size();
			int i1 = ast.size();
			int commonArgCounter = (i0 > i1) ? i1 : i0;
			while (--commonArgCounter > 0) {
				cp = get(--i0).compareTo(ast.get(--i1));
				if (cp != 0) {
					return cp;
				}
			}
			return size() - ast.size();
		}

		return compareToAST(ast);
	}

	/**
	 * Compares this expression with the specified expression for canonical order.
	 * Returns a negative integer, zero, or a positive integer as this expression
	 * is canonical less than, equal to, or greater than the specified expression.
	 */
	public int compareTo(final IExpr expr) {
		if (expr instanceof AST) {
			final AST ast = (AST) expr;

			if ((size() > 2) && (ast.size() > 2)) {
				// special comparison for Times?
				if (head() == F.Times) {
					return compareToTimes((AST) expr);
				} else {
					if (ast.head() == F.Times) {
						return -1 * ast.compareToTimes(this);
					}
				}
			}

			return compareToAST(ast);
		}

		if (expr instanceof Symbol) {
			return -1 * ((Symbol) expr).compareTo(this);
		}

		return (hierarchy() - (expr).hierarchy());
	}

	private int compareToAST(final AST ast) {
		// compare the headers of the 2 expressions:
		int cp = head().compareTo(ast.head());
		if (cp != 0) {
			return cp;
		}

		final int commonArgSize = (size() > ast.size()) ? ast.size() : size();
		for (int i = 1; i < commonArgSize; i++) {
			cp = get(i).compareTo(ast.get(i));
			if (cp != 0) {
				return cp;
			}
		}

		return size() - ast.size();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AST) {
			if (hashCode() != obj.hashCode()) {
				return false;
			}
			return super.equals(obj);
		}
		return false;
	}

	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (Config.DEBUG) {
			System.out.println(toString());
		}
		Util.checkCanceled();
		if (Config.SHOW_CONSOLE) {
			IExpr temp = engine.evalAST(this);
			if ((topHead().getAttributes() & ISymbol.CONSOLE_OUTPUT) == ISymbol.CONSOLE_OUTPUT) {
				if (temp != null) {
					System.out.println(toString());
					System.out.println(" => " + temp.toString());
				}
			}
			return temp;
		} else {
			return engine.evalAST(this);
		}

	}

	@Override
	public int hashCode() {
		if (fHashValue == 0) {
			final int sz = size();
			if (sz > 2) {
				fHashValue = (31 * get(0).hashCode() + 31) * get(1).hashCode() + get(2).hashCode() + sz;
			} else if (sz == 2) {
				fHashValue = 31 * get(0).hashCode() + get(1).hashCode();
			} else {
				if (sz == 1) {
					fHashValue = (17 * get(0).hashCode());
				} else {
					// this case shouldn't happen
					fHashValue = 41;
				}
			}
		}
		return fHashValue;
	}

	/**
	 * Calculate a special hash value to find a matching rule in a hash table
	 * 
	 */
	final public int patternHashCode() {
		if (fPatternMatchingHashValue == 0) {
			if (size() > 1) {
				final int attr = topHead().getAttributes() & ISymbol.FLATORDERLESS;
				if (attr != ISymbol.NOATTRIBUTE) {
					if (attr == ISymbol.FLATORDERLESS) {
						fPatternMatchingHashValue = (17 * get(0).hashCode());
					} else if (attr == ISymbol.FLAT) {
						if (get(1) instanceof IAST) {
							fPatternMatchingHashValue = (31 * get(0).hashCode() + ((IAST) get(1)).get(0).hashCode());
						} else {
							fPatternMatchingHashValue = (37 * get(0).hashCode() + get(1).hashCode());
						}
					} else { // attr == ISymbol.ORDERLESS
						fPatternMatchingHashValue = (17 * get(0).hashCode() + size());
					}
				} else {
					if (get(1) instanceof IAST) {
						fPatternMatchingHashValue = (31 * get(0).hashCode() + ((IAST) get(1)).get(0).hashCode() + size());
					} else {
						fPatternMatchingHashValue = (37 * get(0).hashCode() + get(1).hashCode() + size());
					}
				}
			} else {
				if (size() == 1) {
					fPatternMatchingHashValue = (17 * get(0).hashCode());
				} else {
					// this case shouldn't happen
					fPatternMatchingHashValue = 41;
				}
			}
		}
		return fPatternMatchingHashValue;
	}

	public boolean isAtom() {
		return false;
	}

	public IAST copyHead() {
		return newInstance(get(0));
	}

	public IAST copyUntil(int index) {
		return newInstance(this, index);
	}

	public IExpr variables2Slots(final Map<IExpr, IExpr> map, final List<IExpr> variableList) {
		return AST.COPY.replaceAll(this, new IsUnaryVariableOrPattern<IExpr>(), new UnaryVariable2Slot(map, variableList));
	}

	public String fullFormString() {
		final String sep = ", ";
		IExpr temp = head();
		StringBuffer text = new StringBuffer();
		if (temp == null) {
			text.append("<null-head>");
		} else {
			text.append(temp.fullFormString());
		}
		text.append('[');
		for (int i = 1; i < size(); i++) {
			temp = get(i);
			if (temp == null) {
				text.append("<null-arg>");
			} else {
				text.append(get(i).fullFormString());
				if (i < size() - 1) {
					text.append(sep);
				}
			}
		}
		text.append(']');
		return text.toString();
	}

	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		final String sep = ",";
		final IExpr temp = head();
		if (this.equals(F.CInfinity)) {
			return "CInfinity";
		}
		if (this.equals(F.CNInfinity)) {
			return "CNInfinity";
		}
		if (this.equals(F.Slot1)) {
			return "Slot1";
		}
		if (this.equals(F.Slot2)) {
			return "Slot2";
		}
		StringBuffer text = new StringBuffer(size() * 10);
		if (temp.isSymbol()) {
			ISymbol sym = (ISymbol) temp;
			if (!Character.isUpperCase(sym.toString().charAt(0))) {
				text.append("$(");
				for (int i = 0; i < size(); i++) {
					text.append(get(i).internalFormString(symbolsAsFactoryMethod, depth + 1));
					if (i < size() - 1) {
						text.append(sep);
					}
				}
				text.append(')');
				return text.toString();
			}
		} else if (temp.isPattern() || temp.isAST()) {
			text.append("$(");
			for (int i = 0; i < size(); i++) {
				text.append(get(i).internalFormString(symbolsAsFactoryMethod, depth + 1));
				if (i < size() - 1) {
					text.append(sep);
				}
			}
			text.append(')');
			return text.toString();
		}

		text.append(temp.internalFormString(false, 0));
		text.append('(');
		if (depth == 0 && isList()) {
			text.append('\n');
		}
		for (int i = 1; i < size(); i++) {
			text.append(get(i).internalFormString(symbolsAsFactoryMethod, depth + 1));
			if (i < size() - 1) {
				text.append(sep);
				if (depth == 0 && isList()) {
					text.append('\n');
				}
			}
		}
		if (depth == 0 && isList()) {
			text.append('\n');
		}
		text.append(')');
		return text.toString();
	}

	@Override
	public String toString() {
		try {
			final StringBuffer buf = new StringBuffer();
			if (size() > 0 && isList()) {
				buf.append('{');
				for (int i = 1; i < size(); i++) {
					buf.append(get(i) == this ? "(this AST)" : String.valueOf(get(i)));
					if (i < size() - 1) {
						buf.append(", ");
					}
				}
				buf.append('}');
				return buf.toString();

			} else if (isAST(F.Slot, 2) && (get(1) instanceof IInteger)) {
				try {
					final int slot = ((IInteger) get(1)).toInt();
					if (slot <= 0) {
						return super.toString();
					}
					if (slot == 1) {
						return "#";
					}
					return "#" + slot;
				} catch (final ArithmeticException e) {
					// fall through
				}
				return super.toString();

			} else {
				return super.toString();
			}
		} catch (NullPointerException e) {
			if (Config.SHOW_STACKTRACE) {
				System.out.println(fullFormString());
			}
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addAll(List<? extends IExpr> ast) {
		return addAll(ast, 1, ast.size());
	}

	/**
	 * {@inheritDoc}
	 */
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
	 * {@inheritDoc}
	 */
	public ASTRange args() {
		return new ASTRange(this, 1);
	}

	/**
	 * Get the range of elements [0..sizeOfAST[ of the AST
	 * 
	 * @return
	 */
	public ASTRange range() {
		return new ASTRange(this, 0, size());
	}

	/**
	 * Get the range of elements [start..sizeOfAST[ of the AST
	 * 
	 * @return
	 */
	public ASTRange range(final int start) {
		return new ASTRange(this, start, size());
	}

	/**
	 * Get the range of elements [start..end[ of the AST
	 * 
	 * @return
	 */
	public ASTRange range(final int start, final int end) {
		return new ASTRange(this, start, end);
	}

	/**
	 * 
	 * @param intialCapacity
	 *          the initial capacity (i.e. number of arguments without the header
	 *          element) of the list.
	 * @param head
	 * @return
	 */
	public static AST newInstance(final int intialCapacity, final IExpr head) {
		AST ast = new AST(intialCapacity + 1, false);
		ast.add(head);
		return ast;
	}

	public static AST newInstance(final IExpr[] arr, final IExpr head) {
		AST ast = new AST(arr.length + 1, false);
		ast.add(head);
		for (int i = 0; i < arr.length; i++) {
			ast.add(arr[i]);
		}
		return ast;
	}

	public static AST newInstance(final IExpr head) {
		// AST ast;
		// if (Config.SERVER_MODE) {
		// ast = FACTORY.object();
		// } else {
		// ast = new AST(5, false);
		// }
		AST ast = new AST(5, false);
		ast.add(head);
		return ast;
	}

	private static AST newInstance(final IAST ast, int endPosition) {
		AST result = new AST(5, false);
		result.addAll(ast, 0, endPosition);
		return result;
	}

	public static AST newInstance(final ISymbol symbol, final int[] arr) {
		// AST ast;
		// if (Config.SERVER_MODE) {
		// ast = FACTORY.object();
		// } else {
		// ast = new AST(5, false);
		// }
		AST ast = new AST(5, false);
		ast.add(symbol);
		for (int i = 1; i <= arr.length; i++) {
			ast.add(i, IntegerSym.valueOf(arr[i - 1]));
		}
		return ast;
	}

	/**
	 * Constructs a list with header <i>symbol</i> and the arguments containing
	 * the given DoubleImpl values.
	 * 
	 * @see Num
	 */
	public static AST newInstance(final ISymbol symbol, final double[] arr) {
		// AST ast;
		// if (Config.SERVER_MODE) {
		// ast = FACTORY.object();
		// } else {
		// ast = new AST(5, false);
		// }
		AST ast = new AST(5, false);
		ast.add(symbol);
		for (int i = 1; i <= arr.length; i++) {
			ast.add(i, Num.valueOf(arr[i - 1]));
		}
		return ast;
	}

	public static AST newInstance(final ISymbol symbol, final org.apache.commons.math3.complex.Complex[] arr) {
		// AST ast;
		// if (Config.SERVER_MODE) {
		// ast = FACTORY.object();
		// } else {
		// ast = new AST(5, false);
		// }
		AST ast = new AST(5, false);
		ast.add(symbol);
		for (int i = 1; i <= arr.length; i++) {
			ast.add(i, ComplexNum.valueOf(arr[i - 1].getReal(),arr[i - 1].getImaginary()));
		}
		return ast;
	}

	/**
	 * Constructs a list with header <i>symbol</i> and the arguments containing
	 * the given DoubleImpl matrix values as <i>List</i> rows
	 * 
	 * @see Num
	 */
	public static AST newInstance(final ISymbol symbol, final double[][] matrix) {
		// AST ast;
		// if (Config.SERVER_MODE) {
		// ast = FACTORY.object();
		// } else {
		// ast = new AST(5, false);
		// }
		AST ast = new AST(5, false);
		ast.add(symbol);
		AST row;
		for (int i = 1; i <= matrix.length; i++) {
			row = newInstance(F.List, matrix[i - 1]);
			ast.add(i, row);
		}
		return ast;
	}

	// @Override
	// public void reset() {
	// super.reset();
	// fEvalFlags = 0;
	// fHashValue = 0;
	// }

	// public void readExternal(ObjectInput in) throws IOException,
	// ClassNotFoundException {
	// Parser parser = new Parser();
	// String astString = in.readUTF();
	// ASTNode node = parser.parseExpression(astString);
	// IExpr inExpr = AST2Expr.CONST.convert(this, (FunctionNode) node);
	// if (Config.DEBUG) {
	// if (!(inExpr instanceof IAST)) {
	// throw new IllegalStateException("AST#readExternal()");
	// }
	// }
	// }
	//
	// public void writeExternal(ObjectOutput out) throws IOException {
	// out.writeUTF(toFullForm());
	// }

	// public IExpr copy() {
	// // AST ast;
	// // if (Config.SERVER_MODE) {
	// // ast = FACTORY.object();
	// // } else {
	// // ast = new AST(5, false);
	// // }
	// AST ast = new AST(5, false);
	// ast.fEvalFlags = 0;
	// ast.fHash = 0;
	// ast.fHashValue = 0;
	// for (int i = 0; i < size(); i++) {
	// ast.add(get(i).copy());
	// }
	// return ast;
	// }
	//
	// public IExpr copyNew() {
	// AST ast = new AST(5, false);
	// ast.fEvalFlags = 0;
	// ast.fHash = 0;
	// ast.fHashValue = 0;
	// for (int i = 0; i < size(); i++) {
	// ast.add(get(i).copyNew());
	// }
	// return ast;
	// }

	// public void recycle() {
	// for (int i = 0; i < size(); i++) {
	// if (get(i) != null) {
	// get(i).recycle();
	// }
	// }
	// FACTORY.recycle(this);
	// }

	/** {@inheritDoc} */
	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	public boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	public int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	/**
	 * Additional negative method, which works like opposite to fulfill groovy's
	 * method signature
	 * 
	 * @return
	 */
	public final IExpr negative() {
		return opposite();
	}

	public IExpr minus(final IExpr that) {
		return F.function(F.Plus, this, F.function(F.Times, F.CN1, that));
	}

	/**
	 * Additional multiply method, which works like times to fulfill groovy's
	 * method signature
	 * 
	 * @param that
	 * @return
	 */
	public final IExpr multiply(final IExpr that) {
		return times(that);
	}

	// @Override
	// public Field<IExpr> getField() {
	// return ExprFieldOld.CONST;
	// }

	public final IExpr power(final Integer n) {
		return F.function(F.Power, this, F.integer(n));
	}

	public final IExpr power(final IExpr that) {
		return F.function(F.Power, this, that);
	}

	public IExpr div(final IExpr that) {
		return F.eval(F.function(F.Times, this, F.function(F.Power, that, F.CN1)));
	}

	public IExpr mod(final IExpr that) {
		return F.function(F.Mod, this, that);
	}

	public IExpr and(final IExpr that) {
		return F.function(F.And, this, that);
	}

	public IExpr or(final IExpr that) {
		return F.function(F.Or, this, that);
	}

	public IExpr getAt(final int index) {
		return get(index);
	}

	public Object asType(Class clazz) {
		if (clazz.equals(Boolean.class)) {
			IExpr temp = F.eval(this);
			if (temp.equals(F.True)) {
				return Boolean.TRUE;
			}
			if (temp.equals(F.False)) {
				return Boolean.FALSE;
			}
		} else if (clazz.equals(Integer.class)) {
			IExpr temp = F.eval(this);
			if (temp instanceof IntegerSym) {
				return Integer.valueOf(((IInteger) this).toInt());
			}
		} else if (clazz.equals(java.math.BigInteger.class)) {
			IExpr temp = F.eval(this);
			if (temp instanceof IntegerSym) {
				return new java.math.BigInteger(((IntegerSym) temp).toByteArray());
			}
		} else if (clazz.equals(String.class)) {
			return toString();
		}
		throw new UnsupportedOperationException("AST.asType() - cast not supported.");
	}

	/**
	 * Casts an <code>IExpr</code> at position <code>index</code> to an
	 * <code>IInteger</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 *           if the cast is not possible
	 */
	public IInteger getInt(int index) {
		if (get(index) instanceof IInteger) {
			return (IInteger) get(index);
		}
		throw new WrongArgumentType(this, get(index), index);
	}

	/**
	 * Casts an <code>IExpr</code> at position <code>index</code> to an
	 * <code>INumber</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 *           if the cast is not possible
	 */
	public INumber getNumber(int index) {
		if (get(index) instanceof INumber) {
			return (INumber) get(index);
		}
		throw new WrongArgumentType(this, get(index), index);
	}

	/**
	 * Casts an <code>IExpr</code> at position <code>index</code> to an
	 * <code>IAST</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 *           if the cast is not possible
	 */
	public IAST getAST(int index) {
		if (get(index) instanceof IAST) {
			return (IAST) get(index);
		}
		throw new WrongArgumentType(this, get(index), index);
	}

	/**
	 * Casts an <code>IExpr</code> which is a list at position <code>index</code>
	 * to an <code>IAST</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 */
	public IAST getList(int index) {
		if (get(index).isList()) {
			return (IAST) get(index);
		}
		throw new WrongArgumentType(this, get(index), index);
	}

	public List<IExpr> leaves() {
		int sz = size();
		if (sz < 2) {
			return java.util.Collections.EMPTY_LIST;
		}
		return subList(1, sz);
	}

	@Override
	public IExpr[] egcd(IExpr b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr gcd(IExpr b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr abs() {
		if (this instanceof INumber) {
			return ((INumber) this).eabs();
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isZERO() {
		return isZero();
	}

	/**
	 * Signum functionality is used in JAS toString() method, don't use it as math
	 * signum function.
	 * 
	 * @deprecated
	 */
	@Override
	public int signum() {
		if (isTimes()) {
			IExpr temp = get(1);
			if (temp.isSignedNumber() && ((ISignedNumber) temp).isNegative()) {
				return -1;
			}
		}
		return 1;
	}

	@Override
	public IExpr subtract(IExpr that) {
		return this.plus(that.negate());
	}

	@Override
	public IExpr sum(IExpr that) {
		return this.plus(that);
	}

	@Override
	public ElemFactory<IExpr> factory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toScript() {
		return toString();
	}

	@Override
	public String toScriptFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr divide(IExpr that) {
		return this.div(that);
	}

	@Override
	public boolean isONE() {
		return isOne();
	}

	@Override
	public boolean isUnit() {
		return isOne();
	}

	@Override
	public IExpr remainder(IExpr S) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr negate() {
		return opposite();
	}

}