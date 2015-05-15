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
	public final static int PLUS_PRECEDENCE = 2900;

	public final static int TIMES_PRECEDENCE = 3800;

	public final static int POWER_PRECEDENCE = 5700;

	static final String[] HEADER_STRINGS = { "MapAll", "TimesBy", "Plus", "UpSet", "CompoundExpression", "Map", "Unset", "Apply",
			"ReplaceRepeated", "Less", "And", "Divide", "Set", "Increment", "Factorial2", "LessEqual", "NonCommutativeMultiply",
			"Factorial", "Times", "Power", "Dot", "Not", "PreMinus", "SameQ", "RuleDelayed", "GreaterEqual", "Condition", "Colon",
			"DivideBy", "Or", "Equal", "StringJoin", "Unequal", "Decrement", "SubtractFrom", "PrePlus",
			// "RepeatedNull",
			"UnsameQ", "Rule", "UpSetDelayed", "PreIncrement", "Function", "Greater", "PreDecrement", "Subtract", "SetDelayed",
			"Alternatives", "AddTo",
			// "Repeated",
			"ReplaceAll" };

	static final String[] OPERATOR_STRINGS = { "//@", "*=", "+", "^=", ";", "/@", "=.", "@@", "//.", "<", "&&", "/", "=", "++",
			"!!", "<=", "**", "!", "*", "^", ".", "!", "-", "===", ":>", ">=", "/;", ":", "/=", "||", "==", "<>", "!=", "--", "-=",
			"+",
			// "...",
			"=!=", "->", "^:=", "++", "&", ">", "--", "-", ":=", "|", "+=",
			// "..",
			"/." };

	static final Operator[] OPERATORS = { new InfixOperator("//@", "MapAll", 6100, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator("*=", "TimesBy", 900, InfixOperator.NONE),
			new InfixOperator("+", "Plus", PLUS_PRECEDENCE, InfixOperator.NONE),
			new InfixOperator("^=", "UpSet", 300, InfixOperator.NONE),
			new InfixOperator(";", "CompoundExpression", 100, InfixOperator.NONE),
			new InfixOperator("/@", "Map", 6100, InfixOperator.RIGHT_ASSOCIATIVE), new PostfixOperator("=.", "Unset", 300),
			new InfixOperator("@@", "Apply", 6100, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator("//.", "ReplaceRepeated", 1000, InfixOperator.LEFT_ASSOCIATIVE),
			new InfixOperator("<", "Less", 2600, InfixOperator.NONE), new InfixOperator("&&", "And", 2000, InfixOperator.NONE),
			new DivideOperator("/", "Divide", 4500, InfixOperator.LEFT_ASSOCIATIVE),
			new InfixOperator("=", "Set", 300, InfixOperator.RIGHT_ASSOCIATIVE), new PostfixOperator("++", "Increment", 6400),
			new PostfixOperator("!!", "Factorial2", 6000), new InfixOperator("<=", "LessEqual", 2600, InfixOperator.NONE),
			new InfixOperator("**", "NonCommutativeMultiply", 5000, InfixOperator.NONE),
			new PostfixOperator("!", "Factorial", 6000),
			new InfixOperator("*", "Times", TIMES_PRECEDENCE, InfixOperator.NONE),
			new InfixOperator("^", "Power", POWER_PRECEDENCE, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator(".", "Dot", 4700, InfixOperator.NONE),
			new PrefixOperator("!", "Not", 2100),
			new PreMinusOperator("-", "PreMinus", 4600),
			new InfixOperator("===", "SameQ", 2400, InfixOperator.NONE),
			new InfixOperator(":>", "RuleDelayed", 1100, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator(">=", "GreaterEqual", 2600, InfixOperator.NONE),
			new InfixOperator("/;", "Condition", 1200, InfixOperator.LEFT_ASSOCIATIVE),
			new InfixOperator(":", "Colon", 700, InfixOperator.NONE),
			new InfixOperator("/=", "DivideBy", 900, InfixOperator.NONE),
			new InfixOperator("||", "Or", 1900, InfixOperator.NONE),
			new InfixOperator("==", "Equal", 2600, InfixOperator.NONE),
			new InfixOperator("<>", "StringJoin", 5800, InfixOperator.NONE),
			new InfixOperator("!=", "Unequal", 2600, InfixOperator.NONE),
			new PostfixOperator("--", "Decrement", 6400),
			new InfixOperator("-=", "SubtractFrom", 900, InfixOperator.NONE),
			new PrePlusOperator("+", "PrePlus", 4600),
			// new PostfixOperator("...", "RepeatedNull", 1500),
			new InfixOperator("=!=", "UnsameQ", 2400, InfixOperator.NONE),
			new InfixOperator("->", "Rule", 1100, InfixOperator.RIGHT_ASSOCIATIVE),
			new InfixOperator("^:=", "UpSetDelayed", 300, InfixOperator.NONE), new PrefixOperator("++", "PreIncrement", 6400),
			new PostfixOperator("&", "Function", 800), new InfixOperator(">", "Greater", 2600, InfixOperator.NONE),
			new PrefixOperator("--", "PreDecrement", 6400),
			new SubtractOperator("-", "Subtract", 2900, InfixOperator.LEFT_ASSOCIATIVE),
			new InfixOperator(":=", "SetDelayed", 300, InfixOperator.NONE),
			new InfixOperator("|", "Alternatives", 1400, InfixOperator.NONE),
			new InfixOperator("+=", "AddTo", 900, InfixOperator.NONE),
			// new PostfixOperator("..", "Repeated", 1500),
			new InfixOperator("/.", "ReplaceAll", 1000, InfixOperator.LEFT_ASSOCIATIVE) };

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
			final Map<String, ArrayList<Operator>> operatorTokenStartSet, final String operatorStr, final String headStr,
			final Operator oper) {
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

	static public InfixOperator createInfixOperator(final String operatorStr, final String headStr, final int precedence,
			final int grouping) {
		InfixOperator oper;
		if (headStr.equals("Divide")) {
			oper = new DivideOperator(operatorStr, headStr, precedence, grouping);
		} else if (headStr.equals("Subtract")) {
			oper = new SubtractOperator(operatorStr, headStr, precedence, grouping);
		} else {
			oper = new InfixOperator(operatorStr, headStr, precedence, grouping);
		}
		return oper;
	}

	static public PrefixOperator createPrefixOperator(final String operatorStr, final String headStr, final int precedence) {
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

	static public PostfixOperator createPostfixOperator(final String operatorStr, final String headStr, final int precedence) {
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

	public FunctionNode createFunction(final SymbolNode head, final ASTNode arg0, final ASTNode arg1) {
		return new FunctionNode(head, arg0, arg1);
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

	public PatternNode createPattern2(final SymbolNode patternName, final ASTNode check) {
		return new Pattern2Node(patternName, check);
	}

	public PatternNode createPattern3(final SymbolNode patternName, final ASTNode check) {
		return new Pattern3Node(patternName, check);
	}

	public StringNode createString(final StringBuffer buffer) {
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
							if (!lowercaseName.equals("sin") && !lowercaseName.equals("cos") && !lowercaseName.equals("tan")
									&& !lowercaseName.equals("cot") && !lowercaseName.equals("csc") && !lowercaseName.equals("sec")) {
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
