/*
 * Copyright 2005-2013 Axel Kramer (axelclk@gmail.com)
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
import java.util.List;
import java.util.Locale;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.builtin.PatternMatching;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.NumStr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.operator.InfixOperator;

/**
 * Create an expression of the <code>ASTNode</code> class-hierarchy from a math formulas string representation
 * 
 * See <a href="http://en.wikipedia.org/wiki/Operator-precedence_parser">Operator -precedence parser</a> for the idea,
 * how to parse the operators depending on their precedence.
 */
public class ExprParser extends ExprScanner {
	static class NVisitorExpr extends VisitorExpr {
		final int fPrecision;

		NVisitorExpr(int precision) {
			super();
			fPrecision = precision;
		}

		@Override
		public IExpr visit(INum element) {
			if (element instanceof NumStr) {
				Apfloat apfloatValue = new Apfloat(((NumStr) element).getFloatStr(), fPrecision);
				int exponent = ((NumStr) element).getExponent();
				if (exponent != 1) {
					// value * 10 ^ exponent
					return F.num(apfloatValue.multiply(ApfloatMath.pow(new Apint(10), new Apint(exponent))));
				}
				return F.num(apfloatValue);
			}
			return element;
		}
	}

	static {
		F.initSymbols(null, null, true);
	}

	public final static ISymbol DERIVATIVE = F.Derivative;

	public static int syntaxLength(final String str, EvalEngine engine) throws SyntaxError {
		try {
			ExprParser parser = new ExprParser(engine);
			parser.parse(str);
		} catch (final SyntaxError e) {
			return e.getStartOffset();
		}
		return str.length();
	}

	public static boolean test(final String str, EvalEngine engine) {
		try {
			ExprParser fParser = new ExprParser(engine);
			final IExpr parsedExpression = fParser.parse(str);
			// final Parser fParser = new Parser();
			// final ASTNode parsedAST = fParser.parse(str);
			// final IExpr parsedExpression = AST2Expr.CONST.convert(parsedAST);
			if (parsedExpression != null) {
				return true;
			}
		} catch (final SyntaxError e) {

		}
		return false;
	}

	/**
	 * Set to true if the expression shouldn't be evaluated on input
	 */
	private boolean fHoldExpression;

	private final boolean fRelaxedSyntax;

	private List<IExpr> fNodeList = null;

	private final EvalEngine fEngine;

	public ExprParser(final EvalEngine engine) {
		this(engine, ExprParserFactory.MMA_STYLE_FACTORY, engine.isRelaxedSyntax(), false);
	}

	/**
	 * 
	 * @param engine
	 * @param relaxedSyntax
	 *            if <code>true</code>, use '('...')' as brackets for arguments
	 * @throws SyntaxError
	 */
	public ExprParser(final EvalEngine engine, final boolean relaxedSyntax) throws SyntaxError {
		this(engine, ExprParserFactory.MMA_STYLE_FACTORY, relaxedSyntax);
	}

	// public ExprParser(final EvalEngine engine, final boolean relaxedSyntax,
	// boolean packageMode) throws SyntaxError {
	// this(engine, ExprParserFactory.MMA_STYLE_FACTORY, relaxedSyntax,
	// packageMode);
	// }

	/**
	 * 
	 * @param engine
	 * @param factory
	 * @param relaxedSyntax
	 *            if <code>true</code>, use '('...')' as brackets for arguments
	 * @throws SyntaxError
	 */
	public ExprParser(final EvalEngine engine, IExprParserFactory factory, final boolean relaxedSyntax)
			throws SyntaxError {
		this(engine, factory, relaxedSyntax, false);
	}

	public ExprParser(final EvalEngine engine, IExprParserFactory factory, final boolean relaxedSyntax,
			boolean packageMode) throws SyntaxError {
		super(packageMode);
		this.fRelaxedSyntax = relaxedSyntax;
		this.fFactory = factory;
		this.fEngine = engine;
		if (packageMode) {
			fNodeList = new ArrayList<IExpr>(256);
		}
	}

	private IExpr convert(IASTMutable ast) {
		IExpr head = ast.head();
		if (ast.isAST(F.Hold) || ast.isAST(F.HoldForm)) {
			return ast;
		} else if (ast.isAST(F.N, 3)) {
			return convertN(ast);
		} else if (ast.isAST(F.Sqrt, 2)) {
			// rewrite from input: Sqrt(x) => Power(x, 1/2)
			return F.Power(ast.arg1(), F.C1D2);
		} else if (ast.isAST(F.Exp, 2)) {
			// rewrite from input: Exp(x) => E^x
			return F.Power(F.E, ast.arg1());
		} else if (ast.isPower() && ast.base().isPower() && ast.exponent().isMinusOne()) {
			IAST arg1Power = (IAST) ast.base();
			if (arg1Power.exponent().isNumber()) {
				// Division operator
				// rewrite from input: Power(Power(x, <number>),-1) => Power(x,
				// - <number>)
				return F.Power(arg1Power.base(), arg1Power.exponent().negate());
			}
		} else if (ast.isASTSizeGE(F.GreaterEqual, 3)) {
			ISymbol compareHead = F.Greater;
			return AST2Expr.rewriteLessGreaterAST(ast, compareHead);
		} else if (ast.isASTSizeGE(F.Greater, 3)) {
			ISymbol compareHead = F.GreaterEqual;
			return AST2Expr.rewriteLessGreaterAST(ast, compareHead);
		} else if (ast.isASTSizeGE(F.LessEqual, 3)) {
			ISymbol compareHead = F.Less;
			return AST2Expr.rewriteLessGreaterAST(ast, compareHead);
		} else if (ast.isASTSizeGE(F.Less, 3)) {
			ISymbol compareHead = F.LessEqual;
			return AST2Expr.rewriteLessGreaterAST(ast, compareHead);
		} else if (head.equals(F.Pattern)) {
			final IExpr expr = PatternMatching.Pattern.CONST.evaluate(ast, fEngine);
			if (expr.isPresent()) {
				return expr;
			}
		} else if (head.equals(F.Blank)) {
			final IExpr expr = PatternMatching.Blank.CONST.evaluate(ast, fEngine);
			if (expr.isPresent()) {
				return expr;
			}
		} else if (head.equals(F.Complex)) {
			final IExpr expr = Arithmetic.CONST_COMPLEX.evaluate(ast, fEngine);
			if (expr.isPresent()) {
				return expr;
			}
		} else if (head.equals(F.Rational)) {
			final IExpr expr = Arithmetic.CONST_RATIONAL.evaluate(ast, fEngine);
			if (expr.isPresent()) {
				return expr;
			}
		}
		return ast;
	}

	private IExpr convertN(final IASTMutable function) {
		try {
			int precision = Validate.checkIntType(function.arg2());
			if (EvalEngine.isApfloat(precision)) {
				NVisitorExpr nve = new NVisitorExpr(precision);
				IExpr temp = function.arg1().accept(nve);
				if (temp.isPresent()) {
					function.set(1, temp);
				}
			}
		} catch (WrongArgumentType wat) {

		}
		return function;
	}

	private IExpr convertSymbol(final String nodeStr) {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			if (nodeStr.length() == 1) {
				if (nodeStr.equals("I")) {
					// special - convert on input
					return F.CI;
				}
				return F.symbol(nodeStr, fEngine);
			}
			String lowercaseStr = nodeStr.toLowerCase(Locale.ENGLISH);
			if (lowercaseStr.equals("infinity")) {
				// special - convert on input
				return F.CInfinity;
			} else if (lowercaseStr.equals("complexinfinity")) {
				// special - convert on input
				return F.CComplexInfinity;
			}
			String temp = AST2Expr.PREDEFINED_ALIASES_MAP.get(lowercaseStr);
			if (temp != null) {
				return F.symbol(temp, fEngine);
			}
			return F.symbol(lowercaseStr, fEngine);
		} else {
			String lowercaseStr = nodeStr;
			if (fRelaxedSyntax) {
				lowercaseStr = nodeStr.toLowerCase(Locale.ENGLISH);
				String temp = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(lowercaseStr);
				if (temp != null) {
					lowercaseStr = temp;
				}
			}

			if (Config.RUBI_CONVERT_SYMBOLS) {
				Integer num = AST2Expr.RUBI_STATISTICS_MAP.get(lowercaseStr);
				if (num == null) {
					AST2Expr.RUBI_STATISTICS_MAP.put(lowercaseStr, 1);
				} else {
					AST2Expr.RUBI_STATISTICS_MAP.put(lowercaseStr, num + 1);
				}
			}

			if (lowercaseStr.equals("I")) {
				// special - convert on input
				return F.CI;
			} else if (lowercaseStr.equals("Infinity")) {
				// special - convert on input
				return F.CInfinity;
			}
			return F.symbol(lowercaseStr, fEngine);
		}
	}

	private IExpr createInfixFunction(InfixExprOperator infixOperator, IExpr lhs, IExpr rhs) {
		IExpr temp = infixOperator.createFunction(fFactory, this, lhs, rhs);
		if (temp.isAST()) {
			return convert((IASTMutable) temp);
		}
		return temp;
		// if (infixOperator.getOperatorString().equals("//")) {
		// // lhs // rhs ==> rhs[lhs]
		// IAST function = F.ast(rhs);
		// function.add(lhs);
		// return function;
		// }
		// return F.$(F.$s(infixOperator.getFunctionName()), lhs, rhs);
	}

	/**
	 * Determine the current BinaryOperator
	 * 
	 * @return <code>null</code> if no binary operator could be determined
	 */
	private InfixExprOperator determineBinaryOperator() {
		AbstractExprOperator oper = null;
		for (int i = 0; i < fOperList.size(); i++) {
			oper = fOperList.get(i);
			if (oper instanceof InfixExprOperator) {
				return (InfixExprOperator) oper;
			}
		}
		return null;
	}

	/**
	 * Determine the current PostfixOperator
	 * 
	 * @return <code>null</code> if no postfix operator could be determined
	 */
	private PostfixExprOperator determinePostfixOperator() {
		AbstractExprOperator oper = null;
		for (int i = 0; i < fOperList.size(); i++) {
			oper = fOperList.get(i);
			if (oper instanceof PostfixExprOperator) {
				return (PostfixExprOperator) oper;
			}
		}
		return null;
	}

	/**
	 * Determine the current PrefixOperator
	 * 
	 * @return <code>null</code> if no prefix operator could be determined
	 */
	private PrefixExprOperator determinePrefixOperator() {
		AbstractExprOperator oper = null;
		for (int i = 0; i < fOperList.size(); i++) {
			oper = fOperList.get(i);
			if (oper instanceof PrefixExprOperator) {
				return (PrefixExprOperator) oper;
			}
		}
		return null;
	}

	/**
	 * construct the arguments for an expression
	 * 
	 */
	private void getArguments(final IASTAppendable function) throws SyntaxError {
		do {
			function.append(parseExpression());

			if (fToken != TT_COMMA) {
				break;
			}

			getNextToken();
			if (fToken == TT_PRECEDENCE_CLOSE || fToken == TT_ARGUMENTS_CLOSE) {
				function.append(F.Null);
				break;
			}
		} while (true);
	}

	private IExpr getFactor() throws SyntaxError {
		IExpr temp = null;

		if (fToken == TT_IDENTIFIER) {
			temp = getSymbol();
			if (temp.isSymbol() && fToken >= TT_BLANK && fToken <= TT_BLANK_COLON) {
				temp = getBlankPatterns(temp);
			}
			return parseArguments(temp);
		} else if (fToken == TT_PRECEDENCE_OPEN) {
			fRecursionDepth++;
			try {
				getNextToken();

				temp = parseExpression();

				if (fToken != TT_PRECEDENCE_CLOSE) {
					throwSyntaxError("\')\' expected.");
				}
			} finally {
				fRecursionDepth--;
			}
			getNextToken();
			if (fToken == TT_PRECEDENCE_OPEN) {
				if (!Config.EXPLICIT_TIMES_OPERATOR) {
					return getTimes(temp);
				}
			}
			if (fToken == TT_ARGUMENTS_OPEN) {
				return getFunctionArguments(temp);
			}
			return temp;

		} else if (fToken == TT_LIST_OPEN) {
			return getList();
		} else if (fToken >= TT_BLANK && fToken <= TT_BLANK_COLON) {
			return getBlanks(temp);
		} else if (fToken == TT_DIGIT) {
			return getNumber(false);
		} else if (fToken == TT_STRING) {
			return getString();
		} else if (fToken == TT_PERCENT) {

			final IASTAppendable out = F.ast(F.Out);

			int countPercent = 1;
			getNextToken();
			if (fToken == TT_DIGIT) {
				countPercent = getIntegerNumber();
				out.append(F.integer(countPercent));
				return out;
			}

			while (fToken == TT_PERCENT) {
				countPercent++;
				getNextToken();
			}

			out.append(F.integer(-countPercent));
			return parseArguments(out);
		} else if (fToken == TT_SLOT) {

			getNextToken();
			if (fToken == TT_DIGIT) {
				IExpr slotNumber = getNumber(false);
				if (slotNumber.equals(F.C1)) {
					return parseArguments(F.Slot1);
				} else if (slotNumber.equals(F.C2)) {
					return parseArguments(F.Slot2);
				}
				final IASTAppendable slot = F.ast(F.Slot);
				slot.append(slotNumber);
				return parseArguments(slot);
			} else {
				return parseArguments(F.Slot1);
			}

		} else if (fToken == TT_SLOTSEQUENCE) {

			getNextToken();
			final IASTAppendable slotSequencce = F.ast(F.SlotSequence);
			if (fToken == TT_DIGIT) {
				slotSequencce.append(getNumber(false));
			} else {
				slotSequencce.append(F.C1);
			}
			return parseArguments(slotSequencce);
			// final FunctionNode slotSequencce =
			// fFactory.createFunction(fFactory.createSymbol(IConstantOperators.SlotSequence));
			// if (fToken == TT_DIGIT) {
			// slotSequencce.add(getNumber(false));
			// } else {
			// slotSequencce.add(fFactory.createInteger(1));
			// }
			// return parseArguments(slotSequencce);
		}
		switch (fToken) {

		case TT_PRECEDENCE_CLOSE:
			throwSyntaxError("Too much closing ) in factor.");
			break;
		case TT_LIST_CLOSE:
			throwSyntaxError("Too much closing } in factor.");
			break;
		case TT_ARGUMENTS_CLOSE:
			throwSyntaxError("Too much closing ] in factor.");
			break;
		}

		throwSyntaxError("Error in factor at character: '" + fCurrentChar + "' (" + fToken + ")");
		return null;
	}

	/**
	 * Parse '_' expressions.
	 * 
	 * @param temp
	 * @return
	 */
	private IExpr getBlanks(IExpr temp) {
		if (fToken == TT_BLANK) {
			if (isWhitespace()) {
				getNextToken();
				temp = F.$b();
				// temp = fFactory.createPattern(null, null);
			} else {
				getNextToken();
				if (fToken == TT_IDENTIFIER) {
					final IExpr check = getSymbol();
					temp = F.$b(check);
					// temp = fFactory.createPattern(null, check);
				} else {
					temp = F.$b();
					// temp = fFactory.createPattern(null, null);
				}
			}

		} else if (fToken == TT_BLANK_BLANK) {
			// read '__'
			if (isWhitespace()) {
				getNextToken();
				temp = F.$ps(null, null);
				// temp = fFactory.createPattern2(null, null);
			} else {
				getNextToken();
				if (fToken == TT_IDENTIFIER) {
					final IExpr check = getSymbol();
					temp = F.$ps(null, check);
					// temp = fFactory.createPattern2(null, check);
				} else {
					temp = F.$ps(null, null);
					// temp = fFactory.createPattern2(null, null);
				}
			}
		} else if (fToken == TT_BLANK_BLANK_BLANK) {
			// read '___'
			if (isWhitespace()) {
				getNextToken();
				temp = F.$ps(null, null, false, true);
				// temp = fFactory.createPattern3(null, null);
			} else {
				getNextToken();
				if (fToken == TT_IDENTIFIER) {
					final IExpr check = getSymbol();
					temp = F.$ps(null, check, false, true);
					// temp = fFactory.createPattern3(null, check);
				} else {
					temp = F.$ps(null, null, false, true);
					// temp = fFactory.createPattern3(null, null);
				}
			}
		} else if (fToken == TT_BLANK_OPTIONAL) {
			// read '_.'
			if (isWhitespace()) {
				getNextToken();
				temp = F.$b(null, true);
				// temp = fFactory.createPattern(null, null, true);
			} else {
				getNextToken();
				if (fToken == TT_IDENTIFIER) {
					final IExpr check = getSymbol();
					temp = F.$b(check, true);
					// temp = fFactory.createPattern(null, check, true);
				} else {
					temp = F.$b(null, true);
					// temp = fFactory.createPattern(null, null, true);
				}
			}
		} else if (fToken == TT_BLANK_COLON) {
			// read '_:'
			getNextToken();
			IExpr defaultValue = parseExpression();
			temp = F.$b(null, defaultValue);
		}
		return parseArguments(temp);
	}

	/**
	 * Parse 'symbol_' pattern expressions.
	 * 
	 * @param head
	 * @return
	 */
	private IExpr getBlankPatterns(final IExpr head) {
		IExpr temp = head;
		final ISymbol symbol = (ISymbol) head;
		if (fToken == TT_BLANK) {
			// read '_'
			if (isWhitespace()) {
				temp = F.$p(symbol, null);
				getNextToken();
			} else {
				getNextToken();
				if (fToken == TT_IDENTIFIER) {
					final IExpr check = getSymbol();
					temp = F.$p(symbol, check);
				} else {
					temp = F.$p(symbol, null);
				}
			}
		} else if (fToken == TT_BLANK_BLANK) {
			// read '__'
			if (isWhitespace()) {
				temp = F.$ps(symbol, null);
				getNextToken();
			} else {
				getNextToken();
				if (fToken == TT_IDENTIFIER) {
					final IExpr check = getSymbol();
					temp = F.$ps(symbol, check);
				} else {
					temp = F.$ps(symbol, null);
				}
			}
		} else if (fToken == TT_BLANK_BLANK_BLANK) {
			// read '___'
			if (isWhitespace()) {
				temp = F.$ps(symbol, null, false, true);
				getNextToken();
			} else {
				getNextToken();
				if (fToken == TT_IDENTIFIER) {
					final IExpr check = getSymbol();
					temp = F.$ps(symbol, check, false, true);
				} else {
					temp = F.$ps(symbol, null, false, true);
				}
			}
		} else if (fToken == TT_BLANK_OPTIONAL) {
			// read '_.'
			if (isWhitespace()) {
				temp = F.$p(symbol, null, true);
				getNextToken();
			} else {
				getNextToken();
				if (fToken == TT_IDENTIFIER) {
					final IExpr check = getSymbol();
					temp = F.$p(symbol, check, true);
				} else {
					temp = F.$p(symbol, null, true);
				}
			}
		} else if (fToken == TT_BLANK_COLON) {
			// read '_:'
			getNextToken();
			IExpr defaultValue = parseExpression();
			temp = F.$p(symbol, null, defaultValue);
		}
		return temp;
	}

	public IExprParserFactory getFactory() {
		return fFactory;
	}

	/**
	 * Get a function f[...][...]
	 * 
	 */
	IASTAppendable getFunction(final IExpr head) throws SyntaxError {
		final IASTAppendable function = F.ast(head, 10, false);

		getNextToken();

		if (fRelaxedSyntax) {
			if (fToken == TT_PRECEDENCE_CLOSE) {
				getNextToken();
				if (fToken == TT_PRECEDENCE_OPEN) {
					return function;
				}
				if (fToken == TT_ARGUMENTS_OPEN) {
					return getFunctionArguments(function);
				}
				return function;
			}
		} else {
			if (fToken == TT_ARGUMENTS_CLOSE) {
				getNextToken();
				if (fToken == TT_ARGUMENTS_OPEN) {
					return getFunctionArguments(function);
				}
				return function;
			}
		}
		fRecursionDepth++;
		try {
			getArguments(function);
		} finally {
			fRecursionDepth--;
		}
		if (fRelaxedSyntax) {
			if (fToken == TT_PRECEDENCE_CLOSE) {
				getNextToken();
				if (fToken == TT_PRECEDENCE_OPEN) {
					return function;
				}
				if (fToken == TT_ARGUMENTS_OPEN) {
					return getFunctionArguments(function);
				}
				return function;
			}
		} else {
			if (fToken == TT_ARGUMENTS_CLOSE) {
				getNextToken();
				if (fToken == TT_ARGUMENTS_OPEN) {
					return getFunctionArguments(function);
				}
				return function;
			}
		}

		if (fRelaxedSyntax) {
			throwSyntaxError("')' expected.");
		} else {
			throwSyntaxError("']' expected.");
		}
		return null;

	}

	/**
	 * Get a function f[...][...]
	 * 
	 */
	IASTAppendable getFunctionArguments(final IExpr head) throws SyntaxError {

		final IASTAppendable function = F.ast(head);
		fRecursionDepth++;
		try {
			getNextToken();

			if (fToken == TT_ARGUMENTS_CLOSE) {
				getNextToken();
				if (fToken == TT_ARGUMENTS_OPEN) {
					return getFunctionArguments(function);
				}
				return function;
			}

			getArguments(function);
		} finally {
			fRecursionDepth--;
		}
		if (fToken == TT_ARGUMENTS_CLOSE) {
			getNextToken();
			if (fToken == TT_ARGUMENTS_OPEN) {
				return getFunctionArguments(function);
			}
			return function;
		}

		throwSyntaxError("']' expected.");
		return null;

	}

	private int getIntegerNumber() throws SyntaxError {
		final Object[] result = getNumberString();
		final String number = (String) result[0];
		final int numFormat = ((Integer) result[1]).intValue();
		int intValue = 0;
		try {
			intValue = Integer.parseInt(number, numFormat);
		} catch (final NumberFormatException e) {
			throwSyntaxError("Number format error (not an int type): " + number, number.length());
		}
		getNextToken();
		return intValue;
	}

	/**
	 * Get a list {...}
	 * 
	 */
	private IExpr getList() throws SyntaxError {
		getNextToken();

		if (fToken == TT_LIST_CLOSE) {
			getNextToken();
			return F.List();
		}

		final IASTAppendable function = F.ListAlloc(10); // fFactory.createFunction(fFactory.createSymbol(IConstantOperators.List));
		fRecursionDepth++;
		try {
			getArguments(function);
		} finally {
			fRecursionDepth--;
		}
		if (fToken == TT_LIST_CLOSE) {
			getNextToken();

			return function;
		}

		throwSyntaxError("'}' expected.");
		return null;
	}

	/**
	 * Method Declaration.
	 * 
	 * @return
	 * @see
	 */
	private IExpr getNumber(final boolean negative) throws SyntaxError {
		IExpr temp = null;
		final Object[] result = getNumberString();
		String number = (String) result[0];
		final int numFormat = ((Integer) result[1]).intValue();
		try {
			if (negative) {
				number = '-' + number;
			}
			if (numFormat < 0) {
				// TODO use getReal() if apfloat problems are fixed
				// temp = getReal(number);
				temp = new NumStr(number);
				// temp = fFactory.createDouble(number);
			} else {
				temp = F.integer(number, numFormat);
				// temp = fFactory.createInteger(number, numFormat);
			}
		} catch (final Throwable e) {
			throwSyntaxError("Number format error: " + number, number.length());
		}
		getNextToken();
		return temp;
	}

	private static INum getReal(String str) {
		int index = str.indexOf("*^");
		int fExponent = 1;
		String fFloatStr = str;
		if (index > 0) {
			fFloatStr = str.substring(0, index);
			fExponent = Integer.parseInt(str.substring(index + 2));
		}
		if (fFloatStr.length() > 15) {
			int precision = fFloatStr.length();
			Apfloat apfloatValue = new Apfloat(fFloatStr, precision);
			if (fExponent != 1) {
				// value * 10 ^ exponent
				return F.num(apfloatValue.multiply(ApfloatMath.pow(new Apfloat(10, precision), new Apint(fExponent))));
			}
			return F.num(apfloatValue);
		}

		double fDouble = Double.parseDouble(fFloatStr);
		if (fExponent != 1) {
			// value * 10 ^ exponent
			fDouble = fDouble * Math.pow(10, fExponent);
		}
		return new NumStr(fFloatStr, fExponent);
	}

	/**
	 * Get a <i>part [[..]]</i> of an expression <code>{a,b,c}[[2]]</code> &rarr; <code>b</code>
	 * 
	 */
	private IExpr getPart() throws SyntaxError {
		IExpr temp = getFactor();

		if (fToken != TT_PARTOPEN) {
			return temp;
		}

		IASTAppendable function = null;
		do {
			if (function == null) {
				function = F.Part(temp);
				// function =
				// fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Part),
				// temp);
			} else {
				function = F.Part(function);
				// function =
				// fFactory.createFunction(fFactory.createSymbol(IConstantOperators.Part),
				// function);
			}

			fRecursionDepth++;
			try {
				do {
					getNextToken();

					if (fToken == TT_ARGUMENTS_CLOSE) {
						if (fInputString.length() > fCurrentPosition && fInputString.charAt(fCurrentPosition) == ']') {
							throwSyntaxError("Statement (i.e. index) expected in [[ ]].");
						}
					}

					function.append(parseExpression());
				} while (fToken == TT_COMMA);

				if (fToken == TT_ARGUMENTS_CLOSE) {
					// scanner-step begin: (instead of getNextToken() call):
					if (fInputString.length() > fCurrentPosition) {
						if (fInputString.charAt(fCurrentPosition) == ']') {
							fCurrentPosition++;
							fToken = TT_PARTCLOSE;
						}
					}
					// scanner-step end
				}
				if (fToken != TT_PARTCLOSE) {
					throwSyntaxError("']]' expected.");
				}
				// }
			} finally {
				fRecursionDepth--;
			}
			getNextToken();
		} while (fToken == TT_PARTOPEN);

		return parseArguments(function);

	}

	/**
	 * Get the string as IStringX.
	 * 
	 * @return
	 * @throws SyntaxError
	 */
	private IStringX getString() throws SyntaxError {
		final StringBuilder ident = getStringBuilder();

		getNextToken();

		return F.stringx(ident);
	}

	/**
	 * Read the current identifier from the expression factories table
	 * 
	 * @return
	 * @see
	 */
	private IExpr getSymbol() throws SyntaxError {
		String identifier = getIdentifier();
		if (!fFactory.isValidIdentifier(identifier)) {
			throwSyntaxError("Invalid identifier: " + identifier + " detected.");
		}

		final IExpr symbol = convertSymbol(identifier);
		// final ISymbol symbol = F.$s(identifier);
		getNextToken();
		return symbol;
	}

	private IExpr getTimes(IExpr temp) throws SyntaxError {
		// FunctionNode func = fFactory.createAST(new SymbolNode("Times"));
		IASTAppendable func = F.TimesAlloc(8);
		func.append(temp);
		do {
			getNextToken();
			temp = parseExpression();
			func.append(temp);
			if (fToken != TT_PRECEDENCE_CLOSE) {
				throwSyntaxError("\')\' expected.");
			}
			getNextToken();
		} while (fToken == TT_PRECEDENCE_OPEN);
		return func;
	}

	/**
	 * Test if the current expression shouldn't be evaluated on input
	 * 
	 * @return <code>true</code> if the current expression shouldn't be evaluated on input
	 */
	public boolean isHoldOrHoldFormOrDefer() {
		return fHoldExpression;
	}

	/**
	 * Parse the given <code>expression</code> String into an IExpr.
	 * 
	 * @param expression
	 *            a formula string which should be parsed.
	 * @return the parsed IExpr representation of the given formula string
	 * @throws SyntaxError
	 */
	public IExpr parse(final String expression) throws SyntaxError {
		initialize(expression);
		final IExpr temp = parseExpression();
		if (fToken != TT_EOF) {
			if (fToken == TT_PRECEDENCE_CLOSE) {
				throwSyntaxError("Too many closing ')'; End-of-file not reached.");
			}
			if (fToken == TT_LIST_CLOSE) {
				throwSyntaxError("Too many closing '}'; End-of-file not reached.");
			}
			if (fToken == TT_ARGUMENTS_CLOSE) {
				throwSyntaxError("Too many closing ']'; End-of-file not reached.");
			}

			throwSyntaxError("End-of-file not reached.");
		}

		return temp;
	}

	private IExpr parseArguments(IExpr head) {
		boolean localHoldExpression = fHoldExpression;
		try {
			if (head.isHoldOrHoldFormOrDefer()) {
				fHoldExpression = true;
			}
			if (fRelaxedSyntax) {
				if (fToken == TT_ARGUMENTS_OPEN) {
					if (Config.PARSER_USE_STRICT_SYNTAX) {
						if (head.isSymbolOrPattern()) {
							throwSyntaxError("'(' expected after symbol or pattern instead of '['.");
						}
					}
					IASTMutable ast = getFunctionArguments(head);
					return convert(ast);
				} else if (fToken == TT_PRECEDENCE_OPEN) {
					IASTMutable ast = getFunction(head);
					return convert(ast);
				}
			} else {
				if (fToken == TT_ARGUMENTS_OPEN) {
					IASTMutable ast = getFunctionArguments(head);
					return convert(ast);
				}
			}
			return head;
		} finally {
			fHoldExpression = localHoldExpression;
		}
	}

	private IExpr parseCompoundExpressionNull(InfixExprOperator infixOperator, IExpr rhs) {
		if (infixOperator.getOperatorString().equals(";")) {
			if (fToken == TT_EOF || fToken == TT_ARGUMENTS_CLOSE || fToken == TT_LIST_CLOSE
					|| fToken == TT_PRECEDENCE_CLOSE) {
				return createInfixFunction(infixOperator, rhs, F.Null);
				// return infixOperator.createFunction(fFactory, rhs,
				// fFactory.createSymbol("Null"));
			}
			// if (fPackageMode && fRecursionDepth < 1) {
			// return createInfixFunction(infixOperator, rhs, F.Null);
			// }
		}
		return null;
	}

	private IExpr parseExpression() {
		return parseExpression(parsePrimary(), 0);
	}

	/**
	 * See <a href="http://en.wikipedia.org/wiki/Operator-precedence_parser"> Operator -precedence parser</a> for the
	 * idea, how to parse the operators depending on their precedence.
	 * 
	 * @param lhs
	 *            the already parsed left-hand-side of the operator
	 * @param min_precedence
	 * @return
	 */
	private IExpr parseExpression(IExpr lhs, final int min_precedence) {
		IExpr rhs = null;
		AbstractExprOperator oper;
		InfixExprOperator infixOperator;
		PostfixExprOperator postfixOperator;
		while (true) {
			if (fToken == TT_NEWLINE) {
				return lhs;
			}
			if ((fToken == TT_LIST_OPEN) || (fToken == TT_PRECEDENCE_OPEN) || (fToken == TT_IDENTIFIER)
					|| (fToken == TT_STRING) || (fToken == TT_DIGIT) || (fToken == TT_SLOT)
					|| (fToken == TT_SLOTSEQUENCE)) {
				// if (fPackageMode && fRecursionDepth < 1) {
				// return lhs;
				// }
				// if (fPackageMode && fToken == TT_IDENTIFIER && fLastChar ==
				// '\n') {
				// return lhs;
				// }

				if (!Config.EXPLICIT_TIMES_OPERATOR) {
					// lazy evaluation of multiplication
					oper = fFactory.get("Times");
					if (oper.getPrecedence() >= min_precedence) {
						rhs = parseLookaheadOperator(oper.getPrecedence());
						lhs = F.$(F.$s(oper.getFunctionName()), lhs, rhs);
						continue;
					}
				}
			} else {
				if (fToken == TT_DERIVATIVE) {
					lhs = parseDerivative(lhs);
				}
				if (fToken != TT_OPERATOR) {
					break;
				}
				infixOperator = determineBinaryOperator();

				if (infixOperator != null) {
					if (infixOperator.getPrecedence() >= min_precedence) {
						getNextToken();
						IExpr compoundExpressionNull = parseCompoundExpressionNull(infixOperator, lhs);
						if (compoundExpressionNull != null) {
							return compoundExpressionNull;
						}

						while (fToken == TT_NEWLINE) {
							getNextToken();
						}
						rhs = parseLookaheadOperator(infixOperator.getPrecedence());
						lhs = createInfixFunction(infixOperator, lhs, rhs);

						while (fToken == TT_OPERATOR && infixOperator.getGrouping() == InfixOperator.NONE
								&& infixOperator.getOperatorString().equals(fOperatorString)) {
							getNextToken();
							if (infixOperator.getOperatorString().equals(";")) {
								if (fToken == TT_EOF || fToken == TT_ARGUMENTS_CLOSE || fToken == TT_LIST_CLOSE
										|| fToken == TT_PRECEDENCE_CLOSE) {
									((IASTAppendable) lhs).append(F.Null);
									break;
								}
							}
							while (fToken == TT_NEWLINE) {
								getNextToken();
							}
							rhs = parseLookaheadOperator(infixOperator.getPrecedence());
							((IASTAppendable) lhs).append(rhs);
						}

						continue;
					}
				} else {
					postfixOperator = determinePostfixOperator();

					if (postfixOperator != null) {
						if (postfixOperator.getPrecedence() >= min_precedence) {
							getNextToken();
							lhs = postfixOperator.createFunction(fFactory, lhs);
							lhs = parseArguments(lhs);
							continue;
						}
					} else {
						throwSyntaxError("Operator: " + fOperatorString + " is no infix or postfix operator.");
					}
				}
			}
			break;
		}
		return lhs;
	}

	private IExpr parseLookaheadOperator(final int min_precedence) {
		IExpr rhs = parsePrimary();

		while (true) {
			final int lookahead = fToken;
			if (fToken == TT_NEWLINE) {
				return rhs;
			}
			if ((fToken == TT_LIST_OPEN) || (fToken == TT_PRECEDENCE_OPEN) || (fToken == TT_IDENTIFIER)
					|| (fToken == TT_STRING) || (fToken == TT_DIGIT) || (fToken == TT_SLOT)) {
				if (!Config.EXPLICIT_TIMES_OPERATOR) {
					// lazy evaluation of multiplication
					InfixExprOperator timesOperator = (InfixExprOperator) fFactory.get("Times");
					if (timesOperator.getPrecedence() > min_precedence) {
						rhs = parseExpression(rhs, timesOperator.getPrecedence());
						continue;
					} else if ((timesOperator.getPrecedence() == min_precedence)
							&& (timesOperator.getGrouping() == InfixExprOperator.RIGHT_ASSOCIATIVE)) {
						rhs = parseExpression(rhs, timesOperator.getPrecedence());
						continue;
					}
				}
			} else {
				if (fToken == TT_DERIVATIVE) {
					rhs = parseDerivative(rhs);
				}
				if (lookahead != TT_OPERATOR) {
					break;
				}
				InfixExprOperator infixOperator = determineBinaryOperator();
				if (infixOperator != null) {
					if (infixOperator.getPrecedence() > min_precedence
							|| ((infixOperator.getPrecedence() == min_precedence)
									&& (infixOperator.getGrouping() == InfixExprOperator.RIGHT_ASSOCIATIVE))) {
						if (infixOperator.getOperatorString().equals(";")) {
							rhs = F.Null;
							// if (fPackageMode && fRecursionDepth < 1) {
							// return createInfixFunction(infixOperator, lhs,
							// rhs);
							// }
						}
						rhs = parseExpression(rhs, infixOperator.getPrecedence());
						continue;
					}

				} else {
					PostfixExprOperator postfixOperator = determinePostfixOperator();
					if (postfixOperator != null) {
						if (postfixOperator.getPrecedence() >= min_precedence) {
							getNextToken();
							// rhs =
							// F.$(F.$s(postfixOperator.getFunctionName()),
							// rhs);
							rhs = postfixOperator.createFunction(fFactory, rhs);
							continue;
						}
					}
				}
			}
			break;
		}
		return rhs;

	}

	/**
	 * Parse expressions like <code>expr''[x]</code>
	 * 
	 * @param expr
	 * @return
	 */
	private IExpr parseDerivative(IExpr expr) {
		int derivativeCounter = 1;
		getNextToken();
		while (fToken == TT_DERIVATIVE) {
			derivativeCounter++;
			getNextToken();
		}
		IAST deriv = F.$(DERIVATIVE, F.integer(derivativeCounter));
		expr = F.$(deriv, expr);
		expr = parseArguments(expr);
		return expr;
	}

	public List<IExpr> parsePackage(final String expression) throws SyntaxError {
		initialize(expression);
		while (fToken == TT_NEWLINE) {
			getNextToken();
		}
		IExpr temp = parseExpression();
		fNodeList.add(temp);
		while (fToken != TT_EOF) {
			if (fToken == TT_PRECEDENCE_CLOSE) {
				throwSyntaxError("Too many closing ')'; End-of-file not reached.");
			}
			if (fToken == TT_LIST_CLOSE) {
				throwSyntaxError("Too many closing '}'; End-of-file not reached.");
			}
			if (fToken == TT_ARGUMENTS_CLOSE) {
				throwSyntaxError("Too many closing ']'; End-of-file not reached.");
			}
			while (fToken == TT_NEWLINE) {
				getNextToken();
			}
			if (fToken == TT_EOF) {
				return fNodeList;
			}
			temp = parseExpression();
			fNodeList.add(temp);
			// throwSyntaxError("End-of-file not reached.");
		}

		return fNodeList;
	}

	private IExpr parsePrimary() {
		if (fToken == TT_OPERATOR) {
			if (";;".equals(fOperatorString)) {
				IASTAppendable span = F.ast(F.Span);
				span.append(F.C1);
				getNextToken();
				if (fToken == TT_COMMA || fToken == TT_ARGUMENTS_CLOSE || fToken == TT_PRECEDENCE_CLOSE) {
					span.append(F.All);
					return span;
				}
				if (fToken == TT_OPERATOR && ";;".equals(fOperatorString)) {
					span.append(F.All);
					getNextToken();
				}
				span.append(parsePrimary());
				return span;
			}
			if (fOperatorString.equals(".")) {
				fCurrentChar = '.';
				// fToken = TT_DIGIT;
				// return getPart();
				return getNumber(false);
			}
			final PrefixExprOperator prefixOperator = determinePrefixOperator();
			if (prefixOperator != null) {
				getNextToken();
				final IExpr temp = parseLookaheadOperator(prefixOperator.getPrecedence());
				if (prefixOperator.getFunctionName().equals("PreMinus")) {
					// special cases for negative numbers
					if (temp.isNumber()) {
						return temp.negate();
					}
				}
				return prefixOperator.createFunction(fFactory, temp);
			}
			throwSyntaxError("Operator: " + fOperatorString + " is no prefix operator.");

		}
		return getPart();
	}

	/**
	 * Convert less or greater relations on input. Example: convert expressions like <code>a<b<=c</code> to
	 * <code>Less[a,b]&&LessEqual[b,c]</code>.
	 * 
	 * @param ast
	 * @param compareHead
	 * @return
	 */
	// private IExpr rewriteLessGreaterAST(final IASTMutable ast, ISymbol compareHead) {
	// IExpr temp;
	// boolean evaled = false;
	// IASTAppendable andAST = F.ast(F.And);
	// for (int i = 1; i < ast.size(); i++) {
	// temp = ast.get(i);
	// if (temp.isASTSizeGE(compareHead, 3)) {
	// IAST lt = (IAST) temp;
	// andAST.append(lt);
	// ast.set(i, lt.last());
	// evaled = true;
	// }
	// }
	// if (evaled) {
	// andAST.append(ast);
	// return andAST;
	// } else {
	// return ast;
	// }
	// }

	public void setFactory(final IExprParserFactory factory) {
		this.fFactory = factory;
	}
}