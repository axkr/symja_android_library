package org.matheclipse.core.expression;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.IsUnaryVariableOrPattern;
import org.matheclipse.core.generic.UnaryVariable2Slot;
import org.matheclipse.core.generic.interfaces.BiFunction;
import org.matheclipse.core.generic.util.HMArrayList;
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
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.VisitorReplaceAll;
import org.matheclipse.core.visit.VisitorReplacePart;
import org.matheclipse.core.visit.VisitorReplaceSlots;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import edu.jas.structure.ElemFactory;

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
public class AST extends HMArrayList<IExpr> implements IAST {

	/**
	 * The enumeration map which possibly maps the properties (keys) to a user defined object.
	 * 
	 */
	protected EnumMap<PROPERTY, Object> fProperties = null;

	/** {@inheritDoc} */
	public Object getProperty(PROPERTY key) {
		if (fProperties == null) {
			return null;
		}
		return fProperties.get(key);
	}

	/** {@inheritDoc} */
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
	 * Flags for controlling evaluation and left-hand-side pattern-matching expressions
	 * 
	 */
	transient private int fEvalFlags = 0;

	transient protected int fPatternMatchingHashValue = 0;

	/**
	 * simple parser to simplify unit tests. The parser assumes that the String contains no syntax errors.
	 * 
	 * Example &quot;List[x,List[y]]&quot;
	 */
	public static AST parse(final String inputString) {
		final StringTokenizer tokenizer = new StringTokenizer(inputString, "[],", true);
		String token = tokenizer.nextToken();
		final AST list = newInstance(StringX.valueOf(token));
		// list.setHeader(StringX.valueOf(token));
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
	 * Constructs an empty list with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *            the initial capacity (i.e. number of arguments without the header element) of the list.
	 * @param setLength
	 *            if <code>true</code>, sets the array's size to initialCapacity.
	 */
	private AST(final int initialCapacity, final boolean setLength) {
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
	 * Returns a shallow copy of this <tt>AST</tt> instance. (The elements themselves are not copied.)
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
	public IAST cloneSet(int position, IExpr expr) {
		IAST ast = clone();
		ast.set(position, expr);
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
	 * Returns the ISymbol of the IAST. If the head itself is a IAST it will recursively call head().
	 */
	@Override
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
				return F.Rational;
			}
		}
		if (header instanceof IComplex) {
			return F.Complex;
		}
		if (header instanceof IComplexNum) {
			return F.Complex;
		}
		if (header instanceof IPattern) {
			return F.PatternHead;
		}
		if (head() instanceof IStringX) {
			return F.StringHead;
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final int hierarchy() {
		return ASTID;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isLTOrdered(final IExpr obj) {
		return compareTo(obj) < 0;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isLEOrdered(final IExpr obj) {
		return compareTo(obj) <= 0;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isGTOrdered(final IExpr obj) {
		return compareTo(obj) > 0;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isGEOrdered(final IExpr obj) {
		return compareTo(obj) >= 0;
	}

	/** {@inheritDoc} */
	@Override
	public final int getEvalFlags() {
		return fEvalFlags;
	}

	/** {@inheritDoc} */
	@Override
	public final void setEvalFlags(final int i) {
		fEvalFlags = i;
	}

	/** {@inheritDoc} */
	@Override
	public final void addEvalFlags(final int i) {
		fEvalFlags |= i;
	}

	/** {@inheritDoc} */
	public IAST addOneIdentity(IAST value) {
		if (value.size() == 2) {
			add(value.get(1));
		} else {
			add(value);
		}
		return this;
	}

	/** {@inheritDoc} */
	public IExpr getOneIdentity(IExpr defaultValue) {
		if (size() > 2) {
			return this;
		}
		if (size() == 2) {
			return get(1);
		}
		return defaultValue;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isEvalFlagOn(final int i) {
		return (fEvalFlags & i) == i;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isEvalFlagOff(final int i) {
		return (fEvalFlags & i) == 0;
	}

	public IExpr opposite() {
		return F.Times(F.CN1, this);
	}

	@Override
	public IExpr plus(final IExpr that) {
		return F.Plus(this, that);
	}

	@Override
	public IExpr inverse() {
		return F.eval(F.Power(this, F.CN1));
	}

	@Override
	public IExpr times(final IExpr that) {
		return F.Times(this, that);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isList() {
		return isSameHeadSizeGE(F.List, 1);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSequence() {
		return isSameHeadSizeGE(F.Sequence, 1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isListOfLists() {
		if (head().equals(F.List)) {
			for (int i = 1; i < size(); i++) {
				if (!get(i).isList()) {
					// the row is no list
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isComplexInfinity() {
		return isSameHead(F.DirectedInfinity, 1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDirectedInfinity() {
		return get(0) == F.DirectedInfinity && (size() == 2 || size() == 1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isInfinity() {
		return this.equals(F.CInfinity);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegativeInfinity() {
		return this.equals(F.CNInfinity);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPlus() {
		return isSameHeadSizeGE(F.Plus, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPower() {
		return isSameHead(F.Power, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isTimes() {
		return isSameHeadSizeGE(F.Times, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSin() {
		return isSameHead(F.Sin, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isCos() {
		return isSameHead(F.Cos, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isTan() {
		return isSameHead(F.Tan, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcSin() {
		return isSameHead(F.ArcSin, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAnd() {
		return isSameHeadSizeGE(F.And, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcCos() {
		return isSameHead(F.ArcCos, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcTan() {
		return isSameHead(F.ArcTan, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSinh() {
		return isSameHead(F.Sinh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSlot() {
		return isSameHead(F.Slot, 2) && get(1).isInteger();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSlotSequence() {
		return isSameHead(F.SlotSequence, 2) && get(1).isInteger();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isCosh() {
		return isSameHead(F.Cosh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isTanh() {
		return isSameHead(F.Tanh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcSinh() {
		return isSameHead(F.ArcSinh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcCosh() {
		return isSameHead(F.ArcCosh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isArcTanh() {
		return isSameHead(F.ArcTanh, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isLog() {
		return isSameHead(F.Log, 2);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isOr() {
		return isSameHeadSizeGE(F.Or, 3);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isOne() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isMinusOne() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isZero() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isTrue() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFalse() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSame(IExpr expression) {
		return equals(expression);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSame(IExpr expression, double epsilon) {
		return equals(expression);
	}

	/** {@inheritDoc} */
	@Override
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

	/** {@inheritDoc} */
	@Override
	public final int isVector() {
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

	/** {@inheritDoc} */
	@Override
	public final boolean isFraction() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPattern() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPatternExpr() {
		return (fEvalFlags & CONTAINS_PATTERN_EXPR) != NO_FLAG;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPatternSequence() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isCondition() {
		return size() == 3 && head().equals(F.Condition);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isModule() {
		return size() == 3 && head().equals(F.Module);
	}

	@Override
	public final boolean isSymbol() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isConstant() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isComplex() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isComplexNumeric() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isInteger() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumEqualInteger(IInteger ii) throws ArithmeticException {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNumIntValue() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isRational() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isSignedNumber() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNot() {
		return size() == 2 && head().equals(F.Not);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNumeric() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumericMode() {
		ISymbol symbol = topHead();
		if ((symbol.getAttributes() & ISymbol.NUMERICFUNCTION) == ISymbol.NUMERICFUNCTION) {
			// check if one of the arguments is &quot;numeric&quot;
			for (int i = 1; i < size(); i++) {
				if (get(i).isNumericMode()) {
					return true;
				}
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNumericFunction() {
		ISymbol symbol = topHead();
		if ((symbol.getAttributes() & ISymbol.NUMERICFUNCTION) == ISymbol.NUMERICFUNCTION) {
			// check if all arguments are &quot;numeric&quot;
			for (int i = 1; i < size(); i++) {
				if (!get(i).isNumericFunction()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRealFunction() {
		if (isPlus() || isTimes()) {
			// check if all arguments are &quot;real values&quot;
			for (int i = 1; i < size(); i++) {
				if (!get(i).isRealFunction()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public final boolean isNumber() {
		return false;
	}

	@Override
	public IAST apply(final IExpr head) {
		final IAST ast = clone();
		ast.set(0, head);
		return ast;
	}

	@Override
	public final IAST apply(final IExpr head, final int start) {
		return apply(head, start, size());
	}

	@Override
	public IAST apply(final IExpr head, final int start, final int end) {
		final IAST ast = F.ast(head);
		for (int i = start; i < end; i++) {
			ast.add(get(i));
		}
		return ast;
	}

	@Override
	public IExpr apply(List<? extends IExpr> leaves) {
		final IAST ast = F.ast(head());
		addAll(leaves);
		return ast;
	}

	@Override
	public IExpr apply(IExpr... leaves) {
		final IAST ast = F.ast(head());
		Collections.addAll(ast, leaves);
		return ast;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST map(final Function<IExpr, IExpr> function) {
		return map(clone(), function);
	}

	/** {@inheritDoc} */
	@Override
	public IAST mapFirst(final IAST replacement) {
		return map(Functors.replace1st(replacement));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IAST map(final IExpr head, final Function<IExpr, IExpr> function) {
		final IAST f = clone();
		f.set(0, head);
		return map(f, function);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IAST map(final IAST clonedResultAST, final Function<IExpr, IExpr> function) {
		IExpr temp;
		for (int i = 1; i < size(); i++) {
			temp = function.apply(get(i));
			if (temp != null) {
				clonedResultAST.set(i, temp);
			}
		}
		return clonedResultAST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IAST map(IAST resultAST, IAST secondAST, BiFunction<IExpr, IExpr, IExpr> function) {
		for (int i = 1; i < size(); i++) {
			resultAST.add(function.apply(get(i), secondAST.get(i)));
		}
		return resultAST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IExpr replaceAll(final IAST astRules) {
		return this.accept(new VisitorReplaceAll(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IExpr replaceAll(final Function<IExpr, IExpr> function) {
		return this.accept(new VisitorReplaceAll(function));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IExpr replacePart(final IAST astRules) {
		return this.accept(new VisitorReplacePart(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IExpr replaceRepeated(final IAST astRules) {
		return ExprImpl.replaceRepeated(this, new VisitorReplaceAll(astRules));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IExpr replaceRepeated(final Function<IExpr, IExpr> function) {
		return ExprImpl.replaceRepeated(this, new VisitorReplaceAll(function));
	}

	@Override
	public final IExpr replaceSlots(final IAST astSlots) {
		return this.accept(new VisitorReplaceSlots(astSlots));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IAST filter(IAST filterAST, Predicate<IExpr> predicate) {
		return (new ASTRange(this, 1, size())).filter(filterAST, predicate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IAST filter(IAST filterAST, IAST restAST, Predicate<IExpr> predicate) {
		return (new ASTRange(this, 1, size())).filter(filterAST, restAST, predicate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	@Override
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
	@Override
	public final boolean isAST() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isOrderlessAST() {
		return ((ISymbol.ORDERLESS & topHead().getAttributes()) == ISymbol.ORDERLESS);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFlatAST() {
		return ((ISymbol.FLAT & topHead().getAttributes()) == ISymbol.FLAT);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAST(final IExpr header) {
		return isSameHead(header);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isAST(final IExpr header, final int length) {
		return isSameHead(header, length);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST(IExpr header, int length, IExpr... args) {
		if (isSameHead(header, length)) {
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					if (!get(i + 1).equals(args[i])) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isASTSizeGE(final IExpr header, final int length) {
		return isSameHeadSizeGE(header, length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isAST(final String symbol) {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			return get(0).toString().equals(symbol.toLowerCase());
		}
		return get(0).toString().equals(symbol);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isAST(final String symbol, final int length) {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			return (size() == length) && get(0).toString().equals(symbol.toLowerCase());
		}
		return (size() == length) && get(0).toString().equals(symbol);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isRuleAST() {
		return size() == 3 && (head().equals(F.Rule) || head().equals(F.RuleDelayed));
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFree(final IExpr pattern, boolean heads) {
		final IPatternMatcher matcher = new PatternMatcher(pattern);
		return !isMember(matcher, heads);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isFree(Predicate<IExpr> predicate, boolean heads) {
		return !isMember(predicate, heads);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isMember(final IExpr pattern, boolean heads) {
		final IPatternMatcher matcher = new PatternMatcher(pattern);
		return isMember(matcher, heads);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isMember(Predicate<IExpr> predicate, boolean heads) {
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
	@Override
	public final boolean isFunction() {
		return size() >= 2 && head().equals(F.Function);
	}

	/**
	 * Compares this (Times) AST with the specified AST for order. Returns a negative integer, zero, or a positive integer as this
	 * (Times) AST is canonical less than, equal to, or greater than the specified AST.
	 */
	private int compareToTimes(final AST ast) {
		if (ast.isPower()) {
			// compare from the last this (Times) element:
			final IExpr lastTimes = get(size() - 1);
			int cp;
			if (!(lastTimes instanceof IAST)) {
				cp = lastTimes.compareTo(ast.get(1));
				if (cp != 0) {
					return cp;
				}
				return F.C1.compareTo(ast.get(2));
			} else {
				if (lastTimes.isPower()) {
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
		} else if (ast.isTimes()) {
			// compare from the last element:
			int i0 = size();
			int i1 = ast.size();
			int commonArgCounter = (i0 > i1) ? i1 : i0;
			int cp;
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
	 * Compares this expression with the specified expression for canonical order. Returns a negative integer, zero, or a positive
	 * integer as this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof AST) {
			// special comparison for Times?
			if (isTimes()) {
				return compareToTimes((AST) expr);
			} else {
				if (expr.isTimes()) {
					return -1 * ((AST) expr).compareToTimes(this);
				}
			}
			return compareToAST((AST) expr);
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
		if (obj instanceof AST) {
			return super.equals(obj);
		}
		return false;
	}

	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (Config.DEBUG) {
			System.out.println(toString());
		}
		// Util.checkCanceled();
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

	/**
	 * Calculate a special hash value to find a matching rule in a hash table
	 * 
	 */
	@Override
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

	@Override
	public final boolean isAtom() {
		return false;
	}

	@Override
	public final IAST copyHead() {
		return newInstance(get(0));
	}

	@Override
	public final IAST copyUntil(int index) {
		return newInstance(this, index);
	}

	@Override
	public final IExpr variables2Slots(final Map<IExpr, IExpr> map, final List<IExpr> variableList) {
		return variables2Slots(this, new IsUnaryVariableOrPattern<IExpr>(), new UnaryVariable2Slot(map, variableList));
	}

	/**
	 * Replace all elements determined by the unary <code>from</code> predicate, with the element generated by the unary
	 * <code>to</code> function. If the unary function returns null replaceAll returns null.
	 */
	private static IExpr variables2Slots(final IExpr expr, final Predicate<IExpr> from, final Function<IExpr, ? extends IExpr> to) {
		if (from.apply(expr)) {
			return to.apply(expr);
		}
		IAST nestedList;
		if (expr.isAST()) {
			nestedList = (IAST) expr;
			IAST result = null;
			final IExpr head = nestedList.get(0);
			IExpr temp = variables2Slots(head, from, to);
			if (temp != null) {
				result = nestedList.clone();
				result.set(0, temp);
			} else {
				return null;
			}
			final int size = nestedList.size();
			for (int i = 1; i < size; i++) {

				temp = variables2Slots(nestedList.get(i), from, to);
				if (temp != null) {
					result = nestedList.clone();
					result.set(i, temp);
				} else {
					return null;
				}
			}
			return result;
		}
		return expr;
	}

	@Override
	public String fullFormString() {
		final String sep = ", ";
		IExpr temp = head();
		StringBuffer text = new StringBuffer();
		if (temp == null) {
			text.append("<null-head>");
		} else {
			text.append(temp.fullFormString());
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			text.append('(');
		} else {
			text.append('[');
		}
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
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			text.append(')');
		} else {
			text.append(']');
		}
		return text.toString();
	}

	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		final String sep = ",";
		final IExpr temp = head();
		if (temp.equals(F.Hold) && size() == 2) {
			return get(1).internalFormString(symbolsAsFactoryMethod, depth);
		}
		if (isInfinity()) {
			return "CInfinity";
		}
		if (isNegativeInfinity()) {
			return "CNInfinity";
		}
		if (isComplexInfinity()) {
			return "CComplexInfinity";
		}
		if (this.equals(F.Slot1)) {
			return "Slot1";
		}
		if (this.equals(F.Slot2)) {
			return "Slot2";
		}
		if (isPower()) {
			if (arg2().equals(F.C1D2)) {
				return "Sqrt(" + arg1().internalFormString(symbolsAsFactoryMethod, depth + 1) + ")";
			}
			if (arg2().equals(F.C2)) {
				return "Sqr(" + arg1().internalFormString(symbolsAsFactoryMethod, depth + 1) + ")";
			}
		}
		StringBuffer text = new StringBuffer(size() * 10);
		if (temp.isSymbol()) {
			ISymbol sym = (ISymbol) temp;
			String name = null;
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				name = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(sym.toString().toLowerCase());
			}
			if (name == null) {
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
		if (isTimes() || isPlus()) {
			if (depth == 0 && isList()) {
				text.append('\n');
			}
			internalFormOrderless(this, text, sep, symbolsAsFactoryMethod, depth);
			if (depth == 0 && isList()) {
				text.append('\n');
			}
		} else {
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
		}
		text.append(')');
		return text.toString();
	}

	private static void internalFormOrderless(IAST ast, StringBuffer text, final String sep, boolean symbolsAsFactoryMethod,
			int depth) {
		for (int i = 1; i < ast.size(); i++) {
			if ((ast.get(i) instanceof IAST) && ast.head().equals(ast.get(i).head())) {
				internalFormOrderless((IAST) ast.get(i), text, sep, symbolsAsFactoryMethod, depth);
			} else {
				text.append(ast.get(i).internalFormString(symbolsAsFactoryMethod, depth + 1));
			}
			if (i < ast.size() - 1) {
				text.append(sep);
			}
		}
	}

	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			OutputFormFactory.get(EvalEngine.get().isRelaxedSyntax()).convert(sb, this);
			return sb.toString();
		} catch (Exception e1) {
		}

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

			} else if (isAST(F.Slot, 2) && (get(1).isSignedNumber())) {
				try {
					final int slot = ((ISignedNumber) get(1)).toInt();
					if (slot <= 0) {
						return toFullFormString();
					}
					if (slot == 1) {
						return "#";
					}
					return "#" + slot;
				} catch (final ArithmeticException e) {
					// fall through
				}
				return toFullFormString();

			} else {
				return toFullFormString();
			}
		} catch (NullPointerException e) {
			if (Config.SHOW_STACKTRACE) {
				System.out.println(fullFormString());
			}
			throw e;
		}
	}

	private String toFullFormString() {
		final String sep = ", ";
		IExpr temp = null;
		if (size() > 0) {
			temp = head();
		}
		StringBuffer text;
		if (temp == null) {
			text = new StringBuffer("<null-tag>");
		} else {
			text = new StringBuffer(temp.toString());
		}
		text.append('[');
		for (int i = 1; i < size(); i++) {
			final IExpr o = get(i);
			text = text.append(o == this ? "(this AST)" : o.toString());
			if (i < size() - 1) {
				text.append(sep);
			}
		}
		text.append(']');
		return text.toString();
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
	 * {@inheritDoc}
	 */
	@Override
	public final ASTRange args() {
		return new ASTRange(this, 1);
	}

	/**
	 * Get the range of elements [0..sizeOfAST[ of the AST
	 * 
	 * @return
	 */
	@Override
	public final ASTRange range() {
		return new ASTRange(this, 0, size());
	}

	/**
	 * Get the range of elements [start..sizeOfAST[ of the AST
	 * 
	 * @return
	 */
	@Override
	public final ASTRange range(final int start) {
		return new ASTRange(this, start, size());
	}

	/**
	 * Get the range of elements [start..end[ of the AST
	 * 
	 * @return
	 */
	@Override
	public final ASTRange range(final int start, final int end) {
		return new ASTRange(this, start, end);
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

	private static AST newInstance(final IAST ast, int endPosition) {
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

	public static AST newInstance(final ISymbol symbol, final org.apache.commons.math3.complex.Complex... arr) {
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

	/** {@inheritDoc} */
	@Override
	public final <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public final int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	/**
	 * Additional <code>negative</code> method, which works like opposite to fulfill groovy's method signature
	 * 
	 * @return
	 */
	@Override
	public final IExpr negative() {
		return opposite();
	}

	@Override
	public IExpr minus(final IExpr that) {
		return F.Plus(this, F.Times(F.CN1, that));
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr multiply(final IExpr that) {
		if (that.isZero()) {
			return F.C0;
		}
		return F.eval(F.Times(this, that));
	}

	@Override
	public final IExpr power(final int n) {
		return F.Power(this, F.integer(n));
	}

	@Override
	public final IExpr power(final IExpr that) {
		return F.Power(this, that);
	}

	// @Override
	// public IExpr div(final IExpr that) {
	// return F.eval(F.Times(this, F.Power(that, F.CN1)));
	// }

	@Override
	public IExpr mod(final IExpr that) {
		return F.Mod(this, that);
	}

	@Override
	public IExpr and(final IExpr that) {
		return F.And(this, that);
	}

	@Override
	public IExpr or(final IExpr that) {
		return F.Or(this, that);
	}

	@Override
	public final IExpr getAt(final int index) {
		return get(index);
	}

	@Override
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
			if (temp.isSignedNumber()) {
				try {
					return Integer.valueOf(((ISignedNumber) this).toInt());
				} catch (final ArithmeticException e) {
				}
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
	 * Casts an <code>IExpr</code> at position <code>index</code> to an <code>IInteger</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 *             if the cast is not possible
	 */
	@Override
	public final IInteger getInt(int index) {
		try {
			return (IInteger) get(index);
		} catch (ClassCastException cce) {
		}
		throw new WrongArgumentType(this, get(index), index);
	}

	/**
	 * Casts an <code>IExpr</code> at position <code>index</code> to an <code>INumber</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 *             if the cast is not possible
	 */
	@Override
	public final INumber getNumber(int index) {
		try {
			return (INumber) get(index);
		} catch (ClassCastException cce) {
		}
		throw new WrongArgumentType(this, get(index), index);
	}

	/**
	 * Casts an <code>IExpr</code> at position <code>index</code> to an <code>IAST</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 *             if the cast is not possible
	 */
	@Override
	public final IAST getAST(int index) {
		try {
			return (IAST) get(index);
		} catch (ClassCastException cce) {
		}
		throw new WrongArgumentType(this, get(index), index);
	}

	/**
	 * Casts an <code>IExpr</code> which is a list at position <code>index</code> to an <code>IAST</code>.
	 * 
	 * @param index
	 * @return
	 * @throws WrongArgumentType
	 */
	@Override
	public final IAST getList(int index) {
		IExpr temp = get(index);
		if (temp.isList()) {
			return (IAST) temp;
		}
		throw new WrongArgumentType(this, temp, index);
	}

	@Override
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
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean isZERO() {
		return isZero();
	}

	/**
	 * Signum functionality is used in JAS toString() method, don't use it as math signum function.
	 * 
	 * @deprecated
	 */
	@Deprecated
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
		return F.eval(F.Plus(this, F.Times(F.CN1, that)));
	}

	@Override
	public IExpr sum(IExpr that) {
		if (that.isZero()) {
			return this;
		}
		return F.eval(F.Plus(this, that));
	}

	@Override
	public ElemFactory<IExpr> factory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final String toScript() {
		return toString();
	}

	@Override
	public final String toScriptFactory() {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr divide(IExpr that) {
		return F.eval(F.Times(this, F.Power(that, F.CN1)));
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isONE() {
		return isOne();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isUnit() {
		if (isZero()) {
			return false;
		}
		if (isOne()) {
			return true;
		}
		if (isNumber()) {
			return true;
		}
		IExpr temp = F.eval(F.Times(this, F.Power(this, F.CN1)));
		if (temp.isOne()) {
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isValue() {
		EvalEngine engine = EvalEngine.get();
		ISymbol symbol = topHead();
		IExpr result = engine.evalAttributes(symbol, this);
		if (result != null) {
			if (result.isAST(symbol)) {
				return engine.evalRules(symbol, (IAST) result) != null;
			}
			return false;
		}
		return engine.evalRules(symbol, this) != null;
	}

	@Override
	public IExpr remainder(IExpr S) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final IExpr negate() {
		return F.eval(F.Times(F.CN1, this));
	}

	@Override
	public final IExpr head() {
		return get(0);
	}

	protected static final class ASTIterator implements ListIterator<IExpr> {

		private HMArrayList<IExpr> _table;

		private int _currentIndex;

		private int _start; // Inclusive.

		private int _end; // Exclusive.

		private int _nextIndex;

		public boolean hasNext() {
			return (_nextIndex != _end);
		}

		public IExpr next() {
			if (_nextIndex == _end)
				throw new NoSuchElementException();
			return _table.get(_currentIndex = _nextIndex++);
		}

		public int nextIndex() {
			return _nextIndex;
		}

		public boolean hasPrevious() {
			return _nextIndex != _start;
		}

		public IExpr previous() {
			if (_nextIndex == _start)
				throw new NoSuchElementException();
			return _table.get(_currentIndex = --_nextIndex);
		}

		public int previousIndex() {
			return _nextIndex - 1;
		}

		public void add(IExpr o) {
			_table.add(_nextIndex++, o);
			_end++;
			_currentIndex = -1;
		}

		public void set(IExpr o) {
			if (_currentIndex >= 0) {
				_table.set(_currentIndex, o);
			} else {
				throw new IllegalStateException();
			}
		}

		public void remove() {
			if (_currentIndex >= 0) {
				_table.remove(_currentIndex);
				_end--;
				if (_currentIndex < _nextIndex) {
					_nextIndex--;
				}
				_currentIndex = -1;
			} else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * Returns an iterator over the elements in this <code>IAST</code> starting with offset <b>1</b>.
	 * 
	 * @return an iterator over this <code>IAST</code>s argument values from <code>1..(size-1)</code>.
	 */
	@Override
	public Iterator<IExpr> iterator() {
		ASTIterator i = new ASTIterator();
		i._table = this;
		i._start = 1;
		i._end = this.size();
		i._nextIndex = 1;
		i._currentIndex = 0;
		return i;
	}

	@Override
	/**
	 * Returns an iterator over the elements in this list starting with offset
	 * <b>0</b>.
	 * 
	 * @return an iterator over this list values.
	 */
	public Iterator<IExpr> iterator0() {
		return super.iterator();
	}

	/** {@inheritDoc} */
	@Override
	final public IExpr arg1() {
		return get(1);
	}

	/** {@inheritDoc} */
	@Override
	final public IExpr arg2() {
		return get(2);
	}

	/** {@inheritDoc} */
	@Override
	final public IExpr arg3() {
		return get(3);
	}

	/** {@inheritDoc} */
	@Override
	final public IExpr arg4() {
		return get(4);
	}
	
	/** {@inheritDoc} */
	@Override
	final public IExpr arg5() {
		return get(5);
	}
	
	/** {@inheritDoc} */
	@Override
	final public IExpr last() {
		return get(size() - 1);
	}

	@Override
	public IAST append(IExpr expr) {
		add(expr);
		return this;
	}

	@Override
	public IAST prepend(IExpr expr) {
		add(1, expr);
		return this;
	}

}