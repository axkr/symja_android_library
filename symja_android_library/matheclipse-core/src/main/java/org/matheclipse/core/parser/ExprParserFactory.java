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
package org.matheclipse.core.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;

public class ExprParserFactory implements IExprParserFactory {
	public final static int PLUS_PRECEDENCE = 2900;

	public final static int TIMES_PRECEDENCE = 3800;

	public final static int DIVIDE_PRECEDENCE = 4500;

	public final static int POWER_PRECEDENCE = 5700;

	static final String[] HEADER_STRINGS = { "MessageName", "Get", "PatternTest", "MapAll", "TimesBy", "Plus", "UpSet",
			"CompoundExpression", "Map", "Unset", "Apply", "ReplaceRepeated", "Less", "And", "Divide", "Set", "Increment",
			"Factorial2", "LessEqual", "NonCommutativeMultiply", "Factorial", "Times", "Power", "Dot", "Not", "PreMinus", "SameQ",
			"RuleDelayed", "GreaterEqual", "Condition", "Colon", "//", "DivideBy", "Or", "Span", "Equal", "StringJoin", "Unequal",
			"Decrement", "SubtractFrom", "PrePlus", "RepeatedNull", "UnsameQ", "Rule", "UpSetDelayed", "PreIncrement", "Function",
			"Greater", "PreDecrement", "Subtract", "SetDelayed", "Alternatives", "AddTo", "Repeated", "ReplaceAll" };

	static final String[] OPERATOR_STRINGS = { "::", "<<", "?", "//@", "*=", "+", "^=", ";", "/@", "=.", "@@", "//.", "<", "&&",
			"/", "=", "++", "!!", "<=", "**", "!", "*", "^", ".", "!", "-", "===", ":>", ">=", "/;", ":", "//", "/=", "||", ";;", "==",
			"<>", "!=", "--", "-=", "+", "...", "=!=", "->", "^:=", "++", "&", ">", "--", "-", ":=", "|", "+=", "..", "/." };

	static final AbstractExprOperator[] OPERATORS = { new InfixExprOperator("::", "MessageName", 7400, InfixExprOperator.NONE),
			new PrefixExprOperator("<<", "Get", 7000), new InfixExprOperator("?", "PatternTest", 6600, InfixExprOperator.NONE),
			new InfixExprOperator("//@", "MapAll", 6100, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator("*=", "TimesBy", 900, InfixExprOperator.NONE),
			new InfixExprOperator("+", "Plus", PLUS_PRECEDENCE, InfixExprOperator.NONE),
			new InfixExprOperator("^=", "UpSet", 300, InfixExprOperator.NONE),
			new InfixExprOperator(";", "CompoundExpression", 100, InfixExprOperator.NONE),
			new InfixExprOperator("/@", "Map", 6100, InfixExprOperator.RIGHT_ASSOCIATIVE), new PostfixExprOperator("=.", "Unset", 300),
			new InfixExprOperator("@@", "Apply", 6100, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator("//.", "ReplaceRepeated", 1000, InfixExprOperator.LEFT_ASSOCIATIVE),
			new InfixExprOperator("<", "Less", 2600, InfixExprOperator.NONE), new InfixExprOperator("&&", "And", 2000, InfixExprOperator.NONE),
			new DivideExprOperator("/", "Divide", 4500, InfixExprOperator.LEFT_ASSOCIATIVE),
			new InfixExprOperator("=", "Set", 300, InfixExprOperator.RIGHT_ASSOCIATIVE), new PostfixExprOperator("++", "Increment", 6400),
			new PostfixExprOperator("!!", "Factorial2", 6000), new InfixExprOperator("<=", "LessEqual", 2600, InfixExprOperator.NONE),
			new InfixExprOperator("**", "NonCommutativeMultiply", 5000, InfixExprOperator.NONE),
			new PostfixExprOperator("!", "Factorial", 6000), new InfixExprOperator("*", "Times", TIMES_PRECEDENCE, InfixExprOperator.NONE),
			new InfixExprOperator("^", "Power", POWER_PRECEDENCE, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator(".", "Dot", 4700, InfixExprOperator.NONE), new PrefixExprOperator("!", "Not", 2100),
			new PreMinusExprOperator("-", "PreMinus", 4600), new InfixExprOperator("===", "SameQ", 2400, InfixExprOperator.NONE),
			new InfixExprOperator(":>", "RuleDelayed", 1100, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator(">=", "GreaterEqual", 2600, InfixExprOperator.NONE),
			new InfixExprOperator("/;", "Condition", 1200, InfixExprOperator.LEFT_ASSOCIATIVE),
			new InfixExprOperator(":", "Colon", 700, InfixExprOperator.NONE), new InfixExprOperator("//", "//", 600, InfixExprOperator.NONE),
			new InfixExprOperator("/=", "DivideBy", 900, InfixExprOperator.NONE), new InfixExprOperator("||", "Or", 1900, InfixExprOperator.NONE),
			new InfixExprOperator(";;", "Span", 2700, InfixExprOperator.NONE),
			new InfixExprOperator("==", "Equal", 2600, InfixExprOperator.NONE),
			new InfixExprOperator("<>", "StringJoin", 5800, InfixExprOperator.NONE),
			new InfixExprOperator("!=", "Unequal", 2600, InfixExprOperator.NONE), new PostfixExprOperator("--", "Decrement", 6400),
			new InfixExprOperator("-=", "SubtractFrom", 900, InfixExprOperator.NONE), new PrePlusExprOperator("+", "PrePlus", 4600),
			new PostfixExprOperator("...", "RepeatedNull", 1500), new InfixExprOperator("=!=", "UnsameQ", 2400, InfixExprOperator.NONE),
			new InfixExprOperator("->", "Rule", 1100, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator("^:=", "UpSetDelayed", 300, InfixExprOperator.NONE), new PrefixExprOperator("++", "PreIncrement", 6400),
			new PostfixExprOperator("&", "Function", 800), new InfixExprOperator(">", "Greater", 2600, InfixExprOperator.NONE),
			new PrefixExprOperator("--", "PreDecrement", 6400),
			new SubtractExprOperator("-", "Subtract", 2900, InfixExprOperator.LEFT_ASSOCIATIVE),
			new InfixExprOperator(":=", "SetDelayed", 300, InfixExprOperator.NONE),
			new InfixExprOperator("|", "Alternatives", 1400, InfixExprOperator.NONE),
			new InfixExprOperator("+=", "AddTo", 900, InfixExprOperator.NONE), new PostfixExprOperator("..", "Repeated", 1500),
			new InfixExprOperator("/.", "ReplaceAll", 1000, InfixExprOperator.LEFT_ASSOCIATIVE) };

	public final static ExprParserFactory MMA_STYLE_FACTORY = new ExprParserFactory();

	public final static ExprParserFactory RELAXED_STYLE_FACTORY = new ExprParserFactory();

	/**
	 */
	private static HashMap<String, AbstractExprOperator> fOperatorMap;

	/**
	 */
	private static HashMap<String, ArrayList<AbstractExprOperator>> fOperatorTokenStartSet;

	static {
		fOperatorMap = new HashMap<String, AbstractExprOperator>();
		fOperatorTokenStartSet = new HashMap<String, ArrayList<AbstractExprOperator>>();
		for (int i = 0; i < HEADER_STRINGS.length; i++) {
			addOperator(fOperatorMap, fOperatorTokenStartSet, OPERATOR_STRINGS[i], HEADER_STRINGS[i], OPERATORS[i]);
		}
	}

	/**
	 * Create a default ASTNode factory
	 * 
	 */
	public ExprParserFactory() {
		// this.fIgnoreCase = ignoreCase;
	}

	static public void addOperator(final Map<String, AbstractExprOperator> operatorMap,
			final Map<String, ArrayList<AbstractExprOperator>> operatorTokenStartSet, final String operatorStr, final String headStr,
			final AbstractExprOperator oper) {
		ArrayList<AbstractExprOperator> list;
		operatorMap.put(headStr, oper);
		list = operatorTokenStartSet.get(operatorStr);
		if (list == null) {
			list = new ArrayList<AbstractExprOperator>(2);
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
	public Map<String, AbstractExprOperator> getIdentifier2OperatorMap() {
		return fOperatorMap;
	}

	public AbstractExprOperator get(final String identifier) {
		return fOperatorMap.get(identifier);
	}

	/**
	 * 
	 */
	public Map<String, ArrayList<AbstractExprOperator>> getOperator2ListMap() {
		return fOperatorTokenStartSet;
	}

	/**
	 * 
	 */
	public List<AbstractExprOperator> getOperatorList(final String key) {
		return fOperatorTokenStartSet.get(key);
	}

	static public InfixExprOperator createInfixOperator(final String operatorStr, final String headStr, final int precedence,
			final int grouping) {
		InfixExprOperator oper;
		if (headStr.equals("Divide")) {
			oper = new DivideExprOperator(operatorStr, headStr, precedence, grouping);
		} else if (headStr.equals("Subtract")) {
			oper = new SubtractExprOperator(operatorStr, headStr, precedence, grouping);
		} else {
			oper = new InfixExprOperator(operatorStr, headStr, precedence, grouping);
		}
		return oper;
	}

	static public PrefixExprOperator createPrefixOperator(final String operatorStr, final String headStr, final int precedence) {
		PrefixExprOperator oper;
		if (headStr.equals("PreMinus")) {
			oper = new PreMinusExprOperator(operatorStr, headStr, precedence);
		} else if (headStr.equals("PrePlus")) {
			oper = new PrePlusExprOperator(operatorStr, headStr, precedence);
		} else {
			oper = new PrefixExprOperator(operatorStr, headStr, precedence);
		}
		return oper;
	}

	static public PostfixExprOperator createPostfixOperator(final String operatorStr, final String headStr, final int precedence) {
		return new PostfixExprOperator(operatorStr, headStr, precedence);
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
