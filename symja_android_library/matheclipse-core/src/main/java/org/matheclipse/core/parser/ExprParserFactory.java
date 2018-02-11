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
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class ExprParserFactory implements IExprParserFactory {

	/**
	 * @@@ operator (not @@ operator)
	 *
	 */
	private static class ApplyOperator extends InfixExprOperator {
		public ApplyOperator(final String oper, final String functionName, final int precedence, final int grouping) {
			super(oper, functionName, precedence, grouping);
		}

		@Override
		public IASTMutable createFunction(final IExprParserFactory factory, ExprParser parser, final IExpr lhs,
				final IExpr rhs) {
			IASTAppendable fn = F.ast(F.Apply);
			fn.append(lhs);
			fn.append(rhs);
			if (fOperatorString.equals("@@")) {
				return fn;
			}
			fn.append(F.List(F.C1));
			return fn;
		}

	}

	private static class DivideExprOperator extends InfixExprOperator {
		public DivideExprOperator(final String oper, final String functionName, final int precedence,
				final int grouping) {
			super(oper, functionName, precedence, grouping);
		}

		@Override
		public IASTMutable createFunction(final IExprParserFactory factory, ExprParser parser, final IExpr lhs,
				final IExpr rhs) {

			if (rhs.isInteger() && !rhs.isZero()) {
				if (lhs.isInteger()) {
					if (!parser.isHoldOrHoldFormOrDefer()) {
						return (IASTMutable) F.Rational((IInteger) lhs, (IInteger) rhs);
					}
				}
				return (IASTMutable) F.Times(F.fraction(F.C1, (IInteger) rhs), lhs);
			}

			if (lhs.equals(F.C1)) {
				return (IASTMutable) F.Power(rhs, F.CN1);
			}
			if (rhs.isPower() && rhs.exponent().isNumber()) {
				return F.Times(lhs, F.Power(rhs.base(), rhs.exponent().negate()));
			}
			return F.Times(lhs, F.Power(rhs, F.CN1));
		}
	}

	private static class PreMinusExprOperator extends PrefixExprOperator {

		public PreMinusExprOperator(final String oper, final String functionName, final int precedence) {
			super(oper, functionName, precedence);
		}

		@Override
		public IExpr createFunction(final IExprParserFactory factory, final IExpr argument) {
			return F.Times(F.CN1, argument);
		}
	}

	private static class PrePlusExprOperator extends PrefixExprOperator {

		public PrePlusExprOperator(final String oper, final String functionName, final int precedence) {
			super(oper, functionName, precedence);
		}

		@Override
		public IExpr createFunction(final IExprParserFactory factory, final IExpr argument) {
			return argument;
		}
	}

	private static class SubtractExprOperator extends InfixExprOperator {
		public SubtractExprOperator(final String oper, final String functionName, final int precedence,
				final int grouping) {
			super(oper, functionName, precedence, grouping);
		}

		@Override
		public IASTMutable createFunction(final IExprParserFactory factory, ExprParser parser, final IExpr lhs,
				final IExpr rhs) {
			if (rhs.isNumber()) {
				return (IASTMutable) F.Plus(lhs, rhs.negate());
			}
			if (rhs.isTimes() && rhs.first().isNumber()) {
				return (IASTMutable) F.Plus(lhs, ((IAST) rhs).setAtClone(1, rhs.first().negate()));
			}
			return (IASTMutable) F.Plus(lhs, F.Times(F.CN1, rhs));
		}
	}

	public final static int PLUS_PRECEDENCE = 310;

	public final static int TIMES_PRECEDENCE = 400;

	public final static int DIVIDE_PRECEDENCE = 470;

	public final static int POWER_PRECEDENCE = 590;

	public final static int APPLY_PRECEDENCE = 620;

	public final static ApplyOperator APPLY_OPERATOR = new ApplyOperator("@@", "Apply", 620,
			InfixExprOperator.RIGHT_ASSOCIATIVE);

	public final static ApplyOperator APPLY_LEVEL_OPERATOR = new ApplyOperator("@@@", "Apply", 620,
			InfixExprOperator.RIGHT_ASSOCIATIVE);

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

	static final AbstractExprOperator[] OPERATORS = {
			new InfixExprOperator("::", "MessageName", 750, InfixExprOperator.NONE),
			new PrefixExprOperator("<<", "Get", 720),
			new InfixExprOperator("?", "PatternTest", 680, InfixExprOperator.NONE),
			new InfixExprOperator("//@", "MapAll", 620, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator("*=", "TimesBy", 100, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator("+", "Plus", PLUS_PRECEDENCE, InfixExprOperator.NONE),
			new InfixExprOperator("^=", "UpSet", 40, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator(";", "CompoundExpression", 10, InfixExprOperator.NONE),
			new InfixExprOperator("/@", "Map", 620, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new PostfixExprOperator("=.", "Unset", 670), APPLY_OPERATOR, APPLY_LEVEL_OPERATOR,
			new InfixExprOperator("//.", "ReplaceRepeated", 110, InfixExprOperator.LEFT_ASSOCIATIVE),
			new InfixExprOperator("<", "Less", 290, InfixExprOperator.NONE),
			new InfixExprOperator("&&", "And", 215, InfixExprOperator.NONE),
			new DivideExprOperator("/", "Divide", DIVIDE_PRECEDENCE, InfixExprOperator.LEFT_ASSOCIATIVE),
			new InfixExprOperator("=", "Set", 40, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new PostfixExprOperator("++", "Increment", 660), new PostfixExprOperator("!!", "Factorial2", 610),
			new InfixExprOperator("<=", "LessEqual", 290, InfixExprOperator.NONE),
			new InfixExprOperator("**", "NonCommutativeMultiply", 510, InfixExprOperator.NONE),
			new PostfixExprOperator("!", "Factorial", 610),
			new InfixExprOperator("*", "Times", TIMES_PRECEDENCE, InfixExprOperator.NONE),
			new InfixExprOperator("^", "Power", POWER_PRECEDENCE, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator(".", "Dot", 490, InfixExprOperator.NONE), new PrefixExprOperator("!", "Not", 230),
			new PreMinusExprOperator("-", "PreMinus", 485),
			new InfixExprOperator("===", "SameQ", 290, InfixExprOperator.NONE),
			new InfixExprOperator(":>", "RuleDelayed", 120, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator(">=", "GreaterEqual", 290, InfixExprOperator.NONE),
			new InfixExprOperator("/;", "Condition", 130, InfixExprOperator.LEFT_ASSOCIATIVE),
			new InfixExprOperator(":", "Colon", 80, InfixExprOperator.NONE),
			new InfixExprOperator("//", "//", 70, InfixExprOperator.LEFT_ASSOCIATIVE),
			new InfixExprOperator("/=", "DivideBy", 100, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator("||", "Or", 213, InfixExprOperator.NONE),
			new InfixExprOperator(";;", "Span", 305, InfixExprOperator.NONE),
			new InfixExprOperator("==", "Equal", 290, InfixExprOperator.NONE),
			new InfixExprOperator("<>", "StringJoin", 600, InfixExprOperator.NONE),
			new InfixExprOperator("!=", "Unequal", 290, InfixExprOperator.NONE),
			new PostfixExprOperator("--", "Decrement", 660),
			new InfixExprOperator("-=", "SubtractFrom", 100, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new PrePlusExprOperator("+", "PrePlus", 670), new PostfixExprOperator("...", "RepeatedNull", 170),
			new InfixExprOperator("=!=", "UnsameQ", 290, InfixExprOperator.NONE),
			new InfixExprOperator("->", "Rule", 120, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator("^:=", "UpSetDelayed", 40, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new PrefixExprOperator("++", "PreIncrement", 660), new PostfixExprOperator("&", "Function", 90),
			new InfixExprOperator(">", "Greater", 290, InfixExprOperator.NONE),
			new PrefixExprOperator("--", "PreDecrement", 660),
			new SubtractExprOperator("-", "Subtract", 310, InfixExprOperator.LEFT_ASSOCIATIVE),
			new InfixExprOperator(":=", "SetDelayed", 40, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new InfixExprOperator("|", "Alternatives", 160, InfixExprOperator.NONE),
			new InfixExprOperator("+=", "AddTo", 100, InfixExprOperator.RIGHT_ASSOCIATIVE),
			new PostfixExprOperator("..", "Repeated", 170),
			new InfixExprOperator("/.", "ReplaceAll", 110, InfixExprOperator.LEFT_ASSOCIATIVE) };

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

	static public void addOperator(final Map<String, AbstractExprOperator> operatorMap,
			final Map<String, ArrayList<AbstractExprOperator>> operatorTokenStartSet, final String operatorStr,
			final String headStr, final AbstractExprOperator oper) {
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

	static public InfixExprOperator createInfixOperator(final String operatorStr, final String headStr,
			final int precedence, final int grouping) {
		InfixExprOperator oper;

		if (headStr.equals("Apply")) {
			oper = new ApplyOperator(operatorStr, headStr, precedence, grouping);
		} else if (headStr.equals("Divide")) {
			oper = new DivideExprOperator(operatorStr, headStr, precedence, grouping);
		} else if (headStr.equals("Subtract")) {
			oper = new SubtractExprOperator(operatorStr, headStr, precedence, grouping);
		} else {
			oper = new InfixExprOperator(operatorStr, headStr, precedence, grouping);
		}
		return oper;
	}

	static public PostfixExprOperator createPostfixOperator(final String operatorStr, final String headStr,
			final int precedence) {
		return new PostfixExprOperator(operatorStr, headStr, precedence);
	}

	static public PrefixExprOperator createPrefixOperator(final String operatorStr, final String headStr,
			final int precedence) {
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

	/**
	 * Create a default ASTNode factory
	 * 
	 */
	public ExprParserFactory() {
		// this.fIgnoreCase = ignoreCase;
	}

	@Override
	public AbstractExprOperator get(final String identifier) {
		return fOperatorMap.get(identifier);
	}

	/**
	 * public Map<String, Operator> getIdentifier2OperatorMap()
	 */
	@Override
	public Map<String, AbstractExprOperator> getIdentifier2OperatorMap() {
		return fOperatorMap;
	}

	/**
	 * 
	 */
	@Override
	public Map<String, ArrayList<org.matheclipse.core.parser.AbstractExprOperator>> getOperator2ListMap() {
		return fOperatorTokenStartSet;
	}

	@Override
	public String getOperatorCharacters() {
		return DEFAULT_OPERATOR_CHARACTERS;
	}

	/**
	 * 
	 */
	@Override
	public List<AbstractExprOperator> getOperatorList(final String key) {
		return fOperatorTokenStartSet.get(key);
	}

	@Override
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
