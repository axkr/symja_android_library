package org.matheclipse.core.convert;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.function.Blank;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.Complex;
import org.matheclipse.core.reflection.system.Pattern;
import org.matheclipse.core.reflection.system.Rational;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FloatNode;
import org.matheclipse.parser.client.ast.FractionNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.IntegerNode;
import org.matheclipse.parser.client.ast.Pattern2Node;
import org.matheclipse.parser.client.ast.Pattern3Node;
import org.matheclipse.parser.client.ast.PatternNode;
import org.matheclipse.parser.client.ast.StringNode;
import org.matheclipse.parser.client.ast.SymbolNode;

/**
 * Converts a parsed <code>org.matheclipse.parser.client.ast.ASTNode</code> expression into an IExpr expression
 * 
 */
public class AST2Expr {

	private final static String[] FUNCTION_STRINGS = { "DirectedInfinity", "False", "Flat", "HoldAll", "HoldFirst", "HoldRest",
			"Indeterminate", "Integer", "List", "Listable", "Modulus", "NumericFunction", "OneIdentity", "Orderless", "Real",
			"Slot", "SlotSequence", "String", "Symbol", "True", "Abs", "AddTo", "And", "Apart", "Append", "AppendTo", "Apply",
			"ArcCos", "ArcCosh", "ArcCot", "ArcCoth", "ArcCsc", "ArcCsch", "ArcSec", "ArcSech", "ArcSin", "ArcSinh", "ArcTan",
			"ArcTanh", "Arg", "Array", "AtomQ", "Attributes", "BernoulliB", "Binomial", "Blank", "Block", "Boole",
			"BooleanMinimize", "Break", "Cancel", "CartesianProduct", "Cases", "Catalan", "CatalanNumber", "Catch", "Ceiling",
			"CharacteristicPolynomial", "ChessboardDistance", "Chop", "Clear", "ClearAll", "Coefficient", "CoefficientList",
			"Collect", "Complement", "Complex", "ComplexExpand", "ComplexInfinity", "ComposeList", "CompoundExpression",
			"Condition", "Conjugate", "ConjugateTranspose", "ConstantArray", "Continue", "ContinuedFraction", "CoprimeQ", "Cos",
			"Cosh", "Cot", "Coth", "Count", "Cross", "Csc", "Csch", "Curl", "D", "Decrement", "Default", "Definition", "Degree",
			"Delete", "Denominator", "Depth", "Derivative", "Det", "DiagonalMatrix", "DigitQ", "Dimensions", "Discriminant",
			"Distribute", "Divergence", "DivideBy", "Do", "Dot", "Drop", "E", "Eigenvalues", "Eigenvectors", "Equal", "Erf",
			"EuclidianDistance", "EulerE", "EulerGamma", "EulerPhi", "EvenQ", "Exp", "Expand", "ExpandAll", "Exponent",
			"ExtendedGCD", "Extract", "Factor", "Factorial", "Factorial2", "FactorInteger", "FactorSquareFree",
			"FactorSquareFreeList", "FactorTerms", "Fibonacci", "FindRoot", "First", "Fit", "FixedPoint", "Floor", "Fold",
			"FoldList", "For", "FractionalPart", "FreeQ", "FrobeniusSolve", "FromCharacterCode", "FromContinuedFraction",
			"FullForm", "FullSimplify", "Function", "Gamma", "GCD", "GeometricMean", "Glaisher", "GoldenRatio", "Greater",
			"GreaterEqual", "GroebnerBasis", "HarmonicNumber", "Head", "HilbertMatrix", "Hold", "Horner", "I", "IdentityMatrix",
			"If", "Im", "Increment", "Infinity", "Inner", "IntegerPart", "IntegerPartitions", "IntegerQ", "Integrate",
			"InterpolatingFunction", "Intersection", "Inverse", "InverseFunction", "JacobiMatrix", "JacobiSymbol", "JavaForm",
			"Join", "Khinchin", "KOrderlessPartitions", "KPartitions", "Last", "LCM", "LeafCount", "Length", "Less", "LessEqual",
			"LetterQ", "Level", "Limit", "LinearProgramming", "LinearSolve", "Log", "LowerCaseQ", "LUDecomposition",
			"ManhattanDistance", "Map", "MapAll", "MapThread", "MatchQ", "MatrixForm", "MatrixPower", "MatrixQ", "Max", "Mean",
			"Median", "MemberQ", "Min", "Mod", "Module", "MoebiusMu", "Most", "Multinomial", "N", "Negative", "Nest", "NestList",
			"NestWhile", "NestWhileList", "NextPrime", "NIntegrate", "NonCommutativeMultiply", "NonNegative", "Norm", "Not",
			"NRoots", "NSolve", "NumberQ", "Numerator", "NumericQ", "OddQ", "Or", "Order", "OrderedQ", "Out", "Outer", "Package",
			"PadLeft", "PadRight", "ParametricPlot", "Part", "Partition", "Pattern", "Permutations", "Pi", "Plot", "Plot3D",
			"Plus", "Pochhammer", "PolynomialExtendedGCD", "PolynomialGCD", "PolynomialLCM", "PolynomialQ", "PolynomialQuotient",
			"PolynomialQuotientRemainder", "PolynomialRemainder", "Position", "Positive", "PossibleZeroQ", "Power", "PowerExpand",
			"PowerMod", "PreDecrement", "PreIncrement", "Prepend", "PrependTo", "PrimeQ", "PrimitiveRoots", "Print", "Product",
			"Quotient", "RandomInteger", "RandomReal", "Range", "Rational", "Rationalize", "Re", "Reap", "ReplaceAll",
			"ReplacePart", "ReplaceRepeated", "Rest", "Resultant", "Return", "Reverse", "Riffle", "RootIntervals", "Roots",
			"RotateLeft", "RotateRight", "Round", "Rule", "RuleDelayed", "SameQ", "Scan", "Sec", "Sech", "Select", "Set",
			"SetAttributes", "SetDelayed", "Sign", "SignCmp", "Simplify", "Sin", "SingularValueDecomposition", "Sinh", "Solve",
			"Sort", "Sow", "Sqrt", "SquaredEuclidianDistance", "SquareFreeQ", "StirlingS2", "StringDrop", "StringJoin",
			"StringLength", "StringTake", "Subsets", "SubtractFrom", "Sum", "Switch", "SyntaxLength", "SyntaxQ", "Table", "Take",
			"Tan", "Tanh", "Taylor", "Thread", "Through", "Throw", "Times", "TimesBy", "Timing", "ToCharacterCode", "Together",
			"ToString", "Total", "ToUnicode", "Tr", "Trace", "Transpose", "TrigExpand", "TrigReduce", "TrigToExp", "TrueQ",
			"Tuples", "Unequal", "Union", "UnitStep", "UnsameQ", "UpperCaseQ", "UpSet", "UpSetDelayed", "ValueQ",
			"VandermondeMatrix", "Variables", "VectorQ", "Which", "While" };

	public static final Map<String, String> PREDEFINED_SYMBOLS_MAP = new HashMap<String, String>(997);

	private final static String[] ALIASES_STRINGS = { "ACos", "ASin", "ATan", "ACosh", "ASinh", "ATanh", "Diff", "I", "Infinity",
			"Int", "Trunc" };

	private final static IExpr[] ALIASES_SYMBOLS = { F.ArcCos, F.ArcSin, F.ArcTan, F.ArcCosh, F.ArcSinh, F.ArcTanh, F.D, F.CI,
			F.CInfinity, F.Integrate, F.IntegerPart };

	/**
	 * Aliases which are mapped to the standard function symbols.
	 */
	public static final Map<String, IExpr> PREDEFINED_ALIASES_MAP = new HashMap<String, IExpr>(97);

	public static final String LIST_STRING = Config.PARSER_USE_LOWERCASE_SYMBOLS ? "list" : "List";
	public static final String POWER_STRING = Config.PARSER_USE_LOWERCASE_SYMBOLS ? "power" : "Power";
	public static final String PLUS_STRING = Config.PARSER_USE_LOWERCASE_SYMBOLS ? "plus" : "Plus";
	public static final String TIMES_STRING = Config.PARSER_USE_LOWERCASE_SYMBOLS ? "times" : "Times";
	public static final String FALSE_STRING = Config.PARSER_USE_LOWERCASE_SYMBOLS ? "false" : "False";
	public static final String TRUE_STRING = Config.PARSER_USE_LOWERCASE_SYMBOLS ? "true" : "True";
	// public static final String PART_STRING =
	// Config.PARSER_USE_LOWERCASE_SYMBOLS ? "part" : "Part";
	// public static final String SLOT_STRING =
	// Config.PARSER_USE_LOWERCASE_SYMBOLS ? "slot" : "Slot";
	// public static final String HOLD_STRING =
	// Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hold" : "Hold";
	// public static final String DIRECTEDINFINITY_STRING =
	// Config.PARSER_USE_LOWERCASE_SYMBOLS ? "directedinfinity"
	// : "DirectedInfinity";
	static {
		for (String str : FUNCTION_STRINGS) {
			PREDEFINED_SYMBOLS_MAP.put(str.toLowerCase(), str);
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			for (int i = 0; i < ALIASES_STRINGS.length; i++) {
				PREDEFINED_ALIASES_MAP.put(ALIASES_STRINGS[i].toLowerCase(), ALIASES_SYMBOLS[i]);
			}
		}
	}
	/**
	 * Typical instance of an ASTNode to IExpr converter
	 */
	public final static AST2Expr CONST = new AST2Expr();

	public final static AST2Expr CONST_LC = new AST2Expr(true);

	private boolean fLowercaseEnabled;

	/**
	 * 
	 * @param sType
	 * @param tType
	 * @deprecated
	 */
	public AST2Expr(final Class<ASTNode> sType, final Class<IExpr> tType) {
		this(false);
	}

	public AST2Expr() {
		this(false);
	}

	public AST2Expr(boolean lowercaseEnabled) {
		super();
		fLowercaseEnabled = lowercaseEnabled;
	}

	/**
	 * Converts a parsed FunctionNode expression into an IAST expression
	 */
	public IAST convert(IAST ast, FunctionNode functionNode) throws ConversionException {
		ast.set(0, convert(functionNode.get(0)));
		for (int i = 1; i < functionNode.size(); i++) {
			ast.add(convert(functionNode.get(i)));
		}
		return ast;
	}

	/**
	 * Converts a parsed ASTNode expression into an IExpr expression
	 */
	public IExpr convert(ASTNode node) throws ConversionException {
		if (node == null) {
			return null;
		}
		// if (node instanceof Pattern2Node) {
		// throw new
		// UnsupportedOperationException("'__' pattern-matching expression not implemented");
		// }
		if (node instanceof Pattern3Node) {
			throw new UnsupportedOperationException("'___' pattern-matching expression not implemented");
		}
		if (node instanceof FunctionNode) {
			final FunctionNode functionNode = (FunctionNode) node;
			final IAST ast = F.ast(convert(functionNode.get(0)), functionNode.size(), false);
			for (int i = 1; i < functionNode.size(); i++) {
				ast.add(convert(functionNode.get(i)));
			}
			IExpr head = ast.head();
			if (ast.isASTSizeGE(F.GreaterEqual, 3)) {
				ISymbol compareHead = F.Greater;
				return rewriteLessGreaterAST(ast, compareHead);
			} else if (ast.isASTSizeGE(F.Greater, 3)) {
				ISymbol compareHead = F.GreaterEqual;
				return rewriteLessGreaterAST(ast, compareHead);
			} else if (ast.isASTSizeGE(F.LessEqual, 3)) {
				ISymbol compareHead = F.Less;
				return rewriteLessGreaterAST(ast, compareHead);
			} else if (ast.isASTSizeGE(F.Less, 3)) {
				ISymbol compareHead = F.LessEqual;
				return rewriteLessGreaterAST(ast, compareHead);
			} else if (head.equals(F.PatternHead)) {
				final IExpr expr = Pattern.CONST.evaluate(ast);
				if (expr != null) {
					return expr;
				}
			} else if (head.equals(F.BlankHead)) {
				final IExpr expr = Blank.CONST.evaluate(ast);
				if (expr != null) {
					return expr;
				}
			} else if (head.equals(F.Complex)) {
				final IExpr expr = Complex.CONST.evaluate(ast);
				if (expr != null) {
					return expr;
				}
			} else if (head.equals(F.Rational)) {
				final IExpr expr = Rational.CONST.evaluate(ast);
				if (expr != null) {
					return expr;
				}
			}
			return ast;
		}
		if (node instanceof SymbolNode) {
			String nodeStr = node.getString();
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				nodeStr = nodeStr.toLowerCase();
				IExpr temp = PREDEFINED_ALIASES_MAP.get(nodeStr);
				if (temp != null) {
					return temp;
				}
				return F.$s(nodeStr);
			} else {
				if (fLowercaseEnabled) {
					nodeStr = nodeStr.toLowerCase();
					String temp = PREDEFINED_SYMBOLS_MAP.get(nodeStr);
					if (temp != null) {
						nodeStr = temp;
					}
				}

				if (nodeStr.equals("I")) {
					// special - convert on input
					return F.CI;
				} else if (nodeStr.equals("Infinity")) {
					// special - convert on input
					return F.CInfinity;
				}
				return F.$s(nodeStr);
			}
		}
		// because of inheritance check Pattern2Node before PatternNode
		if (node instanceof Pattern2Node) {
			final Pattern2Node p2n = (Pattern2Node) node;
			return F.$ps((ISymbol) convert(p2n.getSymbol()), convert(p2n.getConstraint()), p2n.isDefault());
		}
		if (node instanceof PatternNode) {
			final PatternNode pn = (PatternNode) node;
			return F.$p((ISymbol) convert(pn.getSymbol()), convert(pn.getConstraint()), pn.isDefault());
		}

		if (node instanceof IntegerNode) {
			final IntegerNode integerNode = (IntegerNode) node;
			final String iStr = integerNode.getString();
			if (iStr != null) {
				return F.integer(iStr, integerNode.getNumberFormat());
			}
			return F.integer(integerNode.getIntValue());
		}
		if (node instanceof FractionNode) {
			FractionNode fr = (FractionNode) node;
			if (fr.isSign()) {
				return F.fraction((IInteger) convert(fr.getNumerator()), (IInteger) convert(fr.getDenominator())).negate();
			}
			return F.fraction((IInteger) convert(((FractionNode) node).getNumerator()),
					(IInteger) convert(((FractionNode) node).getDenominator()));
		}
		if (node instanceof StringNode) {
			return F.stringx(node.getString());
		}
		if (node instanceof FloatNode) {
			return F.num(node.getString());
		}

		return F.$s(node.toString());
	}

	/**
	 * Convert less or greter relations on input. Example: convert expressions like <code>a<b<=c</code> to
	 * <code>Less[a,b]&&LessEqual[b,c]</code>.
	 * 
	 * @param ast
	 * @param compareHead
	 * @return
	 */
	private IExpr rewriteLessGreaterAST(final IAST ast, ISymbol compareHead) {
		IExpr temp;
		boolean evaled = false;
		IAST andAST = F.ast(F.And);
		for (int i = 1; i < ast.size(); i++) {
			temp = ast.get(i);
			if (temp.isASTSizeGE(compareHead, 3)) {
				IAST lt = (IAST) temp;
				andAST.add(lt);
				ast.set(i, lt.get(lt.size() - 1));
				evaled = true;
			}
		}
		if (evaled) {
			andAST.add(ast);
			return andAST;
		} else {
			return ast;
		}
	}
}
