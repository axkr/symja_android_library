/*
 * Copyright 2005-2008 Axel Kramer (axelclk@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.matheclipse.parser.client.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FloatNode;
import org.matheclipse.parser.client.ast.FractionNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.IParserFactory;
import org.matheclipse.parser.client.ast.IntegerNode;
import org.matheclipse.parser.client.ast.Pattern2Node;
import org.matheclipse.parser.client.ast.Pattern3Node;
import org.matheclipse.parser.client.ast.PatternNode;
import org.matheclipse.parser.client.ast.StringNode;
import org.matheclipse.parser.client.ast.SymbolNode;

public class ASTNodeFactory implements IParserFactory {
	
	public final static int PLUS_PRECEDENCE = 310;

	public final static int TIMES_PRECEDENCE = 400;

	public final static int DIVIDE_PRECEDENCE = 470;

	public final static int POWER_PRECEDENCE = 590;

	public final static int APPLY_PRECEDENCE = 620;

	static final String[] HEADER_STRINGS = { "MessageName", "Get", "PatternTest", "MapAll", "TimesBy", "Plus", "UpSet",
			"CompoundExpression", "Map", "Unset", "Apply", "Apply", "ReplaceRepeated", "Less", "And", "Divide", "Set",
			"Increment", "Factorial2", "LessEqual", "NonCommutativeMultiply", "Factorial", "Times", "Power", "Dot",
			"Not", "PreMinus", "SameQ", "RuleDelayed", "GreaterEqual", "Condition", "Colon", "//", "DivideBy", "Or",
			"Span", "Equal", "StringJoin", "Unequal", "Decrement", "SubtractFrom", "PrePlus", "RepeatedNull", "UnsameQ",
			"Rule", "UpSetDelayed", "PreIncrement", "Function", "Greater", "PreDecrement", "Subtract", "SetDelayed",
			"Alternatives", "AddTo", "Repeated", "ReplaceAll" };

	static final String[] OPERATOR_STRINGS = { "::", "<<", "?", "//@", "*=", "+", "^=", ";", "/@", "=.", "@@", "@@@",
			"//.", "<", "&&", "/", "=", "++", "!!", "<=", "**", "!", "*", "^", ".", "!", "-", "===", ":>", ">=", "/;",
			":", "//", "/=", "||", ";;", "==", "<>", "!=", "--", "-=", "+", "...", "=!=", "->", "^:=", "++", "&", ">",
			"--", "-", ":=", "|", "+=", "..", "/." };

	public static final ApplyOperator APPLY_OPERATOR = new ApplyOperator("@@", "Apply", APPLY_PRECEDENCE,
			InfixOperator.RIGHT_ASSOCIATIVE);
	public static final ApplyOperator APPLY_LEVEL_OPERATOR = new ApplyOperator("@@@", "Apply", APPLY_PRECEDENCE,
			InfixOperator.RIGHT_ASSOCIATIVE);

	static final Operator[] OPERATORS = { new InfixOperator("::", "MessageName", 750, InfixOperator.NONE),
			new PrefixOperator("<<", "Get", 720), 
			new InfixOperator("?", "PatternTest", 680, InfixOperator.NONE),
			new InfixOperator("//@", "MapAll", 620, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator("*=", "TimesBy", 100, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator("+", "Plus", PLUS_PRECEDENCE, InfixOperator.NONE),
			new InfixOperator("^=", "UpSet", 40, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator(";", "CompoundExpression", 10, InfixOperator.NONE),
			new InfixOperator("/@", "Map", 620, InfixOperator.RIGHT_ASSOCIATIVE),
			new PostfixOperator("=.", "Unset", 670),
			APPLY_OPERATOR,
			APPLY_LEVEL_OPERATOR,
			// new ApplyOperator("@@", "Apply", APPLY_PRECEDENCE,
			// InfixOperator.RIGHT_ASSOCIATIVE),
			// new ApplyOperator("@@@", "Apply", APPLY_PRECEDENCE, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator("//.", "ReplaceRepeated", 110, InfixOperator.LEFT_ASSOCIATIVE),
			new InfixOperator("<", "Less", 290, InfixOperator.NONE),
			new InfixOperator("&&", "And", 215, InfixOperator.NONE),
			new DivideOperator("/", "Divide", DIVIDE_PRECEDENCE, InfixOperator.LEFT_ASSOCIATIVE),
			new InfixOperator("=", "Set", 40, InfixOperator.RIGHT_ASSOCIATIVE),
			new PostfixOperator("++", "Increment", 660), 
			new PostfixOperator("!!", "Factorial2", 610),
			new InfixOperator("<=", "LessEqual", 290, InfixOperator.NONE),
			new InfixOperator("**", "NonCommutativeMultiply", 510, InfixOperator.NONE),
			new PostfixOperator("!", "Factorial", 610),
			new InfixOperator("*", "Times", TIMES_PRECEDENCE, InfixOperator.NONE),
			new InfixOperator("^", "Power", POWER_PRECEDENCE, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator(".", "Dot", 490, InfixOperator.NONE), 
			new PrefixOperator("!", "Not", 230),
			new PreMinusOperator("-", "PreMinus", 485), 
			new InfixOperator("===", "SameQ", 290, InfixOperator.NONE),
			new InfixOperator(":>", "RuleDelayed", 120, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator(">=", "GreaterEqual", 290, InfixOperator.NONE),
			new InfixOperator("/;", "Condition", 130, InfixOperator.LEFT_ASSOCIATIVE),
			new InfixOperator(":", "Colon", 80, InfixOperator.NONE),
			new InfixOperator("//", "//", 70, InfixOperator.LEFT_ASSOCIATIVE),
			new InfixOperator("/=", "DivideBy", 100, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator("||", "Or", 213, InfixOperator.NONE),
			new InfixOperator(";;", "Span", 305, InfixOperator.NONE),
			new InfixOperator("==", "Equal", 290, InfixOperator.NONE),
			new InfixOperator("<>", "StringJoin", 600, InfixOperator.NONE),
			new InfixOperator("!=", "Unequal", 290, InfixOperator.NONE), 
			new PostfixOperator("--", "Decrement", 660),
			new InfixOperator("-=", "SubtractFrom", 100, InfixOperator.RIGHT_ASSOCIATIVE), 
			new PrePlusOperator("+", "PrePlus", 670),
			new PostfixOperator("...", "RepeatedNull", 170),
			new InfixOperator("=!=", "UnsameQ", 290, InfixOperator.NONE),
			new InfixOperator("->", "Rule", 120, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator("^:=", "UpSetDelayed", 40, InfixOperator.RIGHT_ASSOCIATIVE),
			new PrefixOperator("++", "PreIncrement", 660), 
			new PostfixOperator("&", "Function", 90),
			new InfixOperator(">", "Greater", 290, InfixOperator.NONE), 
			new PrefixOperator("--", "PreDecrement", 660),
			new SubtractOperator("-", "Subtract", 310, InfixOperator.LEFT_ASSOCIATIVE),
			new InfixOperator(":=", "SetDelayed", 40, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator("|", "Alternatives", 160, InfixOperator.NONE),
			new InfixOperator("+=", "AddTo", 100, InfixOperator.RIGHT_ASSOCIATIVE), 
			new PostfixOperator("..", "Repeated", 170),
			new InfixOperator("/.", "ReplaceAll", 110, InfixOperator.LEFT_ASSOCIATIVE) };

	public final static ASTNodeFactory MMA_STYLE_FACTORY = new ASTNodeFactory(false);

	public final static ASTNodeFactory RELAXED_STYLE_FACTORY = new ASTNodeFactory(true);

	/**
	 */
	private static HashMap<String, Operator> fOperatorMap;

	/**
	 */
	private static HashMap<String, ArrayList<Operator>> fOperatorTokenStartSet;

	static {
		fOperatorMap = new HashMap<String, Operator>();
		fOperatorTokenStartSet = new HashMap<String, ArrayList<Operator>>();
		for (int i = 0; i < HEADER_STRINGS.length; i++) {
			addOperator(fOperatorMap, fOperatorTokenStartSet, OPERATOR_STRINGS[i], HEADER_STRINGS[i], OPERATORS[i]);
		}
	}

	private final boolean fIgnoreCase;

	/**
	 * Create a default ASTNode factory
	 * 
	 */
	public ASTNodeFactory(boolean ignoreCase) {
		this.fIgnoreCase = ignoreCase;
	}

	static public void addOperator(final Map<String, Operator> operatorMap,
			final Map<String, ArrayList<Operator>> operatorTokenStartSet, final String operatorStr,
			final String headStr, final Operator oper) {
		ArrayList<Operator> list;
		operatorMap.put(headStr, oper);
		list = operatorTokenStartSet.get(operatorStr);
		if (list == null) {
			list = new ArrayList<Operator>(2);
			list.add(oper);
			operatorTokenStartSet.put(operatorStr, list);
		} else {
			list.add(oper);
		}
	}

	public String getOperatorCharacters() {
		return DEFAULT_OPERATOR_CHARACTERS;
	}

	/**
	 * public Map<String, Operator> getIdentifier2OperatorMap()
	 */
	public Map<String, Operator> getIdentifier2OperatorMap() {
		return fOperatorMap;
	}

	public Operator get(final String identifier) {
		return fOperatorMap.get(identifier);
	}

	/**
	 * 
	 */
	public Map<String, ArrayList<Operator>> getOperator2ListMap() {
		return fOperatorTokenStartSet;
	}

	/**
	 * 
	 */
	public List<Operator> getOperatorList(final String key) {
		return fOperatorTokenStartSet.get(key);
	}

	static public InfixOperator createInfixOperator(final String operatorStr, final String headStr,
			final int precedence, final int grouping) {
		InfixOperator oper;
		if (headStr.equals("Apply")) {
			oper = new ApplyOperator(operatorStr, headStr, precedence, grouping);
		} else if (headStr.equals("Divide")) {
			oper = new DivideOperator(operatorStr, headStr, precedence, grouping);
		} else if (headStr.equals("Subtract")) {
			oper = new SubtractOperator(operatorStr, headStr, precedence, grouping);
		} else {
			oper = new InfixOperator(operatorStr, headStr, precedence, grouping);
		}
		return oper;
	}

	static public PrefixOperator createPrefixOperator(final String operatorStr, final String headStr,
			final int precedence) {
		PrefixOperator oper;
		if (headStr.equals("PreMinus")) {
			oper = new PreMinusOperator(operatorStr, headStr, precedence);
		} else if (headStr.equals("PrePlus")) {
			oper = new PrePlusOperator(operatorStr, headStr, precedence);
		} else {
			oper = new PrefixOperator(operatorStr, headStr, precedence);
		}
		return oper;
	}

	static public PostfixOperator createPostfixOperator(final String operatorStr, final String headStr,
			final int precedence) {
		return new PostfixOperator(operatorStr, headStr, precedence);
	}

	public ASTNode createDouble(final String doubleString) {
		return new FloatNode(doubleString);
	}

	public FunctionNode createFunction(final SymbolNode head) {
		return new FunctionNode(head);
	}

	public FunctionNode createFunction(final SymbolNode head, final ASTNode arg0) {
		return new FunctionNode(head, arg0);
	}

	public FunctionNode createFunction(final SymbolNode head, final ASTNode arg1, final ASTNode arg2) {
		return new FunctionNode(head, arg1, arg2);
	}

	/**
	 * Creates a new list with no arguments from the given header object .
	 */
	public FunctionNode createAST(final ASTNode headExpr) {
		return new FunctionNode(headExpr);
	}

	public IntegerNode createInteger(final String integerString, final int numberFormat) {
		return new IntegerNode(integerString, numberFormat);
	}

	public IntegerNode createInteger(final int intValue) {
		return new IntegerNode(intValue);
	}

	public FractionNode createFraction(final IntegerNode numerator, final IntegerNode denominator) {
		return new FractionNode(numerator, denominator);
	}

	public PatternNode createPattern(final SymbolNode patternName, final ASTNode check) {
		return new PatternNode(patternName, check);
	}

	public PatternNode createPattern(final SymbolNode patternName, final ASTNode check, boolean optional) {
		return new PatternNode(patternName, check, optional);
	}

	public PatternNode createPattern(final SymbolNode patternName, final ASTNode check, final ASTNode defaultValue) {
		return new PatternNode(patternName, check, defaultValue);
	}

	public PatternNode createPattern2(final SymbolNode patternName, final ASTNode check) {
		return new Pattern2Node(patternName, check);
	}

	public PatternNode createPattern3(final SymbolNode patternName, final ASTNode check) {
		return new Pattern3Node(patternName, check);
	}

	public StringNode createString(final StringBuilder buffer) {
		return new StringNode(buffer.toString());
	}

	public SymbolNode createSymbol(final String symbolName) {
		String name = symbolName;
		if (fIgnoreCase) {
			name = symbolName.toLowerCase();
		}
		if (Config.RUBI_CONVERT_SYMBOLS) {
			name = toRubiString(name);
		}
		// if (fIgnoreCase) {
		// return new SymbolNode(symbolName.toLowerCase());
		// }
		return new SymbolNode(name);
	}

	public boolean isValidIdentifier(String identifier) {
		return true;
	}

	private String toRubiString(final String nodeStr) {
		if (!Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			if (nodeStr.length() == 1) {
				return nodeStr;
			}
			String lowercaseName = nodeStr.toLowerCase();
			String temp = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(lowercaseName);
			if (temp != null) {
				if (!temp.equals(nodeStr)) {
					temp = F.PREDEFINED_INTERNAL_FORM_STRINGS.get(nodeStr);
					if (temp == null) {
						if (lowercaseName.length() > 1) {
							if (!lowercaseName.equals("sin") && !lowercaseName.equals("cos")
									&& !lowercaseName.equals("tan") && !lowercaseName.equals("cot")
									&& !lowercaseName.equals("csc") && !lowercaseName.equals("sec")) {
								System.out.println(nodeStr + " => §" + lowercaseName);
							}
						}
						return "§" + lowercaseName;
					}
				}
			} else {
				if (!nodeStr.equals(nodeStr.toLowerCase())) {
					temp = F.PREDEFINED_INTERNAL_FORM_STRINGS.get(nodeStr);
					if (temp == null) {
						if (lowercaseName.length() > 1) {
							System.out.println(nodeStr + " => §" + lowercaseName);
						}
						return "§" + lowercaseName;
					}
				}
			}
		}
		return nodeStr;
	}
}
