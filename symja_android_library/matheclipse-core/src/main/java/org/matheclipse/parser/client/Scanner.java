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
package org.matheclipse.parser.client;

import java.util.List;
import java.util.Stack;

import org.matheclipse.parser.client.operator.Operator;

public abstract class Scanner {

	/**
	 * Token type: End-of_File
	 */
	final static protected int TT_EOF = 0;

	/**
	 * Token type: opening bracket for associations
	 */
	final static protected int TT_ASSOCIATION_OPEN = 10;

	/**
	 * Token type: closing bracket for associations
	 */
	final static protected int TT_ASSOCIATION_CLOSE = 11;

	/**
	 * Token type: opening bracket for function arguments
	 */
	final static protected int TT_ARGUMENTS_OPEN = 12;

	/**
	 * Token type: closing bracket for function arguments
	 */
	final static protected int TT_ARGUMENTS_CLOSE = 13;

	/**
	 * Token type: opening bracket '(' for sub-formulas with higher precedence
	 */
	final static protected int TT_PRECEDENCE_OPEN = 14;

	/**
	 * Token type: closing bracket ')' for sub-formulas with higher precedence
	 */
	final static protected int TT_PRECEDENCE_CLOSE = 15;

	/**
	 * Token type: opening curly braces '{' for starting lists
	 */
	final static protected int TT_LIST_OPEN = 16;

	/**
	 * Token type: closing curly braces '}' for ending lists
	 */
	final static protected int TT_LIST_CLOSE = 17;

	/**
	 * Token type: opening brackets for starting the &quot;index part&quot; of an expression
	 */
	final static protected int TT_PARTOPEN = 18;

	/**
	 * Token type: closing brackets for ending the &quot;index part&quot; of an expression
	 */
	final static protected int TT_PARTCLOSE = 19;

	/**
	 * Token type: operator ';;'
	 */
	final static protected int TT_SPAN = 30;

	/**
	 * Token type: operator found in input string
	 */
	final static protected int TT_OPERATOR = 31;

	/**
	 * ',' operator
	 */
	final static protected int TT_COMMA = 134;

	/**
	 * '%' operator
	 */
	final static protected int TT_PERCENT = 135;

	/**
	 * Token type: string surrounded by &quot;....&quot;
	 */
	final static protected int TT_STRING = 136;

	/**
	 * Token type: identifier name
	 */
	final static protected int TT_IDENTIFIER = 137;

	/**
	 * Token type: digit 0,1,2,3,4,5,6,7,8,9
	 */
	final static protected int TT_DIGIT = 138;

	/**
	 * Token type: slot #
	 */
	final static protected int TT_SLOT = 140;

	/**
	 * Token type: slot sequence ##
	 */
	final static protected int TT_SLOTSEQUENCE = 141;

	/**
	 * Token type: pattern '_'
	 */
	final static protected int TT_BLANK = 142;

	/**
	 * Token type: pattern '__'
	 */
	final static protected int TT_BLANK_BLANK = 143;

	/**
	 * Token type: pattern '___'
	 */
	final static protected int TT_BLANK_BLANK_BLANK = 144;

	/**
	 * Token type: pattern '_.'
	 */
	final static protected int TT_BLANK_OPTIONAL = 145;

	/**
	 * Token type: pattern '_:'
	 */
	final static protected int TT_BLANK_COLON = 146;

	/**
	 * Token type: pattern ''' (single apostrophe) for writing derivatives
	 */
	final static protected int TT_DERIVATIVE = 147;

	/**
	 * New line token
	 */
	final static protected int TT_NEWLINE = 150;

	// ----------------optimized identifier management------------------
	private static final String string_a = "a", string_b = "b", string_c = "c", string_d = "d", string_e = "e",
			string_f = "f", string_g = "g", string_h = "h", string_i = "i", string_j = "j", string_k = "k",
			string_l = "l", string_m = "m", string_n = "n", string_o = "o", string_p = "p", string_q = "q",
			string_r = "r", string_s = "s", string_t = "t", string_u = "u", string_v = "v", string_w = "w",
			string_x = "x", string_y = "y", string_z = "z";

	private static final String string_A = "A", string_B = "B", string_C = "C", string_D = "D", string_E = "E",
			string_F = "F", string_G = "G", string_H = "H", string_I = "I", string_J = "J", string_K = "K",
			string_L = "L", string_M = "M", string_N = "N", string_O = "O", string_P = "P", string_Q = "Q",
			string_R = "R", string_S = "S", string_T = "T", string_U = "U", string_V = "V", string_W = "W",
			string_X = "X", string_Y = "Y", string_Z = "Z";

	private static final String var_a = "$a", var_b = "$b", var_c = "$c", var_d = "$d", var_e = "$e", var_f = "$f",
			var_g = "$g", var_h = "$h", var_i = "$i", var_j = "$j", var_k = "$k", var_l = "$l", var_m = "$m",
			var_n = "$n", var_o = "$o", var_p = "$p", var_q = "$q", var_r = "$r", var_s = "$s", var_t = "$t",
			var_u = "$u", var_v = "$v", var_w = "$w", var_x = "$x", var_y = "$y", var_z = "$z";

	/**
	 * <p>
	 * Simple bracket balancer for pairs of &quot;( )&quot;, &quot;[ ]&quot;, &quot;{ }&quot; brackets.
	 * </p>
	 * <p>
	 * Doesn't work for comments or strings at the moment.
	 * </p>
	 * 
	 * @param sourceCode
	 *            the source which should be checked for balanced brackets
	 * @return the resulting String which can close the "open brackets" or <code>null</code> if the brackets are
	 *         unbalanced.
	 */
	public static String balanceCode(CharSequence sourceCode) {
		Stack<Character> openBracketStack = new Stack<Character>();

		for (int j = 0; j < sourceCode.length(); j++) {
			char ch = sourceCode.charAt(j);
			switch (ch) {
			case '{':
			case '(':
			case '[':
				openBracketStack.push(ch);
				break;
			case '}':
				if (openBracketStack.isEmpty()) {
					return null;
				}
				ch = openBracketStack.pop();
				if (!(ch == '{')) {
					return null;
				}
				break;
			case ')':
				if (openBracketStack.isEmpty()) {
					return null;
				}
				ch = openBracketStack.pop();
				if (!(ch == '(')) {
					return null;
				}
				break;
			case ']':
				if (openBracketStack.isEmpty()) {
					return null;
				}
				ch = openBracketStack.pop();
				if (!(ch == '[')) {
					return null;
				}
				break;
			default:
				// do nothing
			}
		}
		if (!openBracketStack.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			char ch = 0;
			while (!openBracketStack.isEmpty()) {
				ch = openBracketStack.pop();
				switch (ch) {
				case '{':
					builder.append('}');
					break;
				case '(':
					builder.append(')');
					break;
				case '[':
					builder.append(']');
					break;
				}
			}
			return builder.toString();
		}
		return "";
	}

	/**
	 * Call method <code>balanceCode()</code>, and if missing closing &quot;( )&quot;, &quot;[ ]&quot;, &quot;{ }&quot;
	 * brackets are found, append them to the end of <code>sourceCode</code>.
	 * 
	 * @param sourceCode
	 *            the (unbalanced) source code string
	 * @return
	 */
	public static String appendMissingBrackets(String sourceCode) {
		String balanceCode = balanceCode(sourceCode);
		if (balanceCode != null && balanceCode.length() > 0) {
			return sourceCode + balanceCode;
		}
		return sourceCode;
	}

	public static boolean isBalancedCode(CharSequence sourceCode) {
		Stack<Character> openBracketStack = new Stack<Character>();

		for (int j = 0; j < sourceCode.length(); j++) {
			char ch = sourceCode.charAt(j);
			switch (ch) {
			case '{':
			case '(':
			case '[':
				openBracketStack.push(ch);
				break;
			case '}':
				if (openBracketStack.isEmpty()) {
					return false;
				}
				ch = openBracketStack.pop();
				if (!(ch == '{')) {
					return false;
				}
				break;
			case ')':
				if (openBracketStack.isEmpty()) {
					return false;
				}
				ch = openBracketStack.pop();
				if (!(ch == '(')) {
					return false;
				}
				break;
			case ']':
				if (openBracketStack.isEmpty()) {
					return false;
				}
				ch = openBracketStack.pop();
				if (!(ch == '[')) {
					return false;
				}
				break;
			default:
				// do nothing
			}
		}
		if (!openBracketStack.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Current parser input string
	 */
	protected char[] fInputString;

	/**
	 * Recursion depth for brackets.
	 */
	protected int fRecursionDepth;

	/**
	 * Current input character
	 */
	protected char fCurrentChar;

	/**
	 * The position of the current character in the input string
	 */
	protected int fCurrentPosition;

	/**
	 * Current input token
	 */
	protected int fToken;

	/**
	 * The last determined operator string
	 */
	protected String fOperatorString;

	/**
	 * protected List<Operator> fOperList;
	 */
	protected List<Operator> fOperList;

	/**
	 * Row counter for reporting the row where a syntax error occurred.
	 */
	protected int fRowCounter;

	/**
	 * Is true if the parser should parse a <code>package</code>.
	 */
	protected boolean fPackageMode = false;

	/**
	 * Current rows start position for reporting syntax errors
	 */
	protected int fCurrentColumnStartPosition;

	/**
	 * <p>
	 * If <code>true</code> the <code>*</code> operator must be written for a <code>Times()</code> expression. I.e. you
	 * cannot write <code>2(b+c)</code> anymore, but have to write <code>2*(b+c)</code> to get
	 * <code>Times(2, Plus(b, c))</code>.
	 * </p>
	 * <p>
	 * You also enable <a href="https://en.wikipedia.org/wiki/Scientific_notation#E-notation">scientific E-notation</a>.
	 * I.e. <code>1E-2</code> is converted to a double value <code>0.01</code> for floating point numbers and not parsed
	 * as <code>Plus(-2, E)</code> anymore.
	 * </p>
	 * <p>
	 * You also enable integer literal input with a prefix, similar to
	 * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">Java integer literals</a>
	 * <ul>
	 * <li><code>0b</code> or <code>0B</code> for binary numbers</li>
	 * <li><code>0x</code> or <code>0X</code> for hexadecimal numbers</li>
	 * <li><code>0o</code> or <code>0O</code> for octal numbers</li>
	 * </ul>
	 * </p>
	 */
	protected final boolean fExplicitTimes;

	/**
	 * Initialize Scanner without a math-expression
	 */
	protected Scanner(boolean packageMode, boolean explicitTimes) {
		fPackageMode = packageMode;
		fExplicitTimes = explicitTimes;
		initializeNullScanner();
	}

	/**
	 * Get the character at the current position in the parsed input string.
	 * 
	 * @return the character at the current position in the parsed input string.
	 */
	private final char charAtPosition() {
		return fInputString[fCurrentPosition];
	}

	/**
	 * Verify the length of the input string and get the next character from the input string. If the current position
	 * is greater than the input length, set current character to SPACE and token to TT_EOF.
	 * 
	 */
	protected void getChar() {
		if (isValidPosition()) {
			getNextChar();
			return;
		}
		fCurrentPosition = fInputString.length + 1;
		fCurrentChar = ' ';
		fToken = TT_EOF;
	}

	/**
	 * Parse a multiline comment <code>(* ... *)</code>
	 */
	private void getComment() {
		int startPosition = fCurrentPosition;
		int level = 0;
		fCurrentPosition++;
		try {
			while (true) {
				if (charAtPosition() == '*' && fInputString[fCurrentPosition + 1] == ')') {
					fCurrentPosition++;
					fCurrentPosition++;
					if (level == 0) {
						break;
					}
					level--;
					continue;
				} else if (charAtPosition() == '(' && fInputString[fCurrentPosition + 1] == '*') {
					fCurrentPosition++;
					fCurrentPosition++;
					level++;
					continue;
				} else if (charAtPosition() == '\n') {
					fCurrentPosition++;
					fRowCounter++;
					fCurrentColumnStartPosition = fCurrentPosition;
					continue;
				}
				fCurrentPosition++;
			}
		} catch (IndexOutOfBoundsException ioobe) {
			fCurrentPosition = startPosition;
			throwSyntaxError("Comment doesn't end with '*)' (open multiline comment)");
		}
	}

	/**
	 * Get the error line which should be thrown in a <code>SyntaxError</code> exception.
	 * 
	 * @return
	 */
	private String getErrorLine() {
		if (fInputString.length < fCurrentPosition) {
			fCurrentPosition--;
		}
		// read until end-of-line after the current fError
		int eol = fCurrentPosition;
		while (fInputString.length > eol) {
			fCurrentChar = fInputString[eol++];
			if (fCurrentChar == '\n') {
				eol--;
				break;
			}
		}
		return new String(fInputString, fCurrentColumnStartPosition, eol - fCurrentColumnStartPosition);
	}

	/**
	 * Parse an identifier string (function, constant or variable name) and the corresponding context if possible.
	 * 
	 * @return an array which contains &quot;the main identifier&quot; at offset 0 and &quot;context(or
	 *         <code>null</code>)&quot; at offset 1.
	 */
	protected String[] getIdentifier() {
		int startPosition = fCurrentPosition - 1;

		getChar();
		if (fCurrentChar == '$') {
			getChar();
		}
		int contextIndex = -1;
		while ((Character.isJavaIdentifierPart(fCurrentChar) && (fCurrentChar != '_')) || (fCurrentChar == '$')
				|| (fCurrentChar == '`')) {
			if (fCurrentChar == '`') {
				contextIndex = fCurrentPosition - 1;
			}
			getChar();
		}
		while ((Character.isJavaIdentifierPart(fCurrentChar) && (fCurrentChar != '_')) || (fCurrentChar == '$')
				|| (fCurrentChar == '`')) {
			if (fCurrentChar == '`') {
				contextIndex = fCurrentPosition - 1;
			}
			getChar();
		}
		String context = "";
		if (contextIndex > 0) {
			context = new String(fInputString, startPosition, contextIndex - startPosition + 1);
			startPosition = contextIndex + 1;
		}
		int endPosition = fCurrentPosition--;
		final int length = (--endPosition) - startPosition;
		if (length == 1) {
			String name = optimizedCurrentTokenSource1(startPosition);
			if (name == null) {
				name = Characters.CharacterNamesMap.get(String.valueOf(fInputString[startPosition]));
				if (name != null) {
					return new String[] { name, context };
				}
			}
			return new String[] { new String(fInputString, startPosition, 1), context };
		}
		if (length == 2 && fInputString[startPosition] == '$') {
			return new String[] { optimizedCurrentTokenSource2(startPosition), context };
		}
		return new String[] { new String(fInputString, startPosition, endPosition - startPosition), context };
	}

	/**
	 * Parse a Java <code>int</code> value.
	 * 
	 * @return
	 * @throws SyntaxError
	 *             if the number couldn't be parsed as Java <code>int</code> value.
	 */
	protected int getJavaInt() throws SyntaxError {
		final String number = getIntegerString();
		int intValue = 0;
		try {
			intValue = Integer.parseInt(number, 10);
		} catch (final NumberFormatException e) {
			throwSyntaxError("Number format error (not an int type): " + number, number.length());
		}
		getNextToken();
		return intValue;
	}

	/**
	 * Parse a Java <code>int</code> value.
	 * 
	 * @return
	 * @throws SyntaxError
	 *             if the number couldn't be parsed as Java <code>int</code> value.
	 */
	protected long getJavaLong() throws SyntaxError {
		final String number = getIntegerString();
		long longValue = 0;
		try {
			longValue = Long.parseLong(number, 10);
		} catch (final NumberFormatException e) {
			throwSyntaxError("Number format error (not an int type): " + number, number.length());
		}
		getNextToken();
		return longValue;
	}

	/**
	 * Parse a Java <code>string</code> from the digits <code>0,1,2,3,4,5,6,7,8,9</code>.
	 * 
	 * @return
	 */
	protected String getIntegerString() {
		int startPosition = fCurrentPosition - 1;
		getChar();
		while (Character.isDigit(fCurrentChar)) {
			getChar();
		}
		int endPosition = fCurrentPosition--;
		return new String(fInputString, startPosition, (--endPosition) - startPosition);
	}

	private void getNextChar() {
		fCurrentChar = fInputString[fCurrentPosition++];
		if (fCurrentChar == '\\') {
			// search next non-whitespace character
			while (isValidPosition()) {
				fCurrentChar = fInputString[fCurrentPosition++];
				if (!Character.isWhitespace(fCurrentChar) && fCurrentChar != '\\') {
					return;
				}
				if (fCurrentChar == '\n') {
					fRowCounter++;
					fCurrentColumnStartPosition = fCurrentPosition;
				}
			}
		}
	}

	/**
	 * Get the next token from the input string
	 * 
	 * @throws SyntaxError
	 */
	protected void getNextToken() throws SyntaxError {

		while (isValidPosition()) {
			getNextChar();
			fToken = TT_EOF;

			if ((fCurrentChar != '\t') && (fCurrentChar != '\r') && (fCurrentChar != ' ')) {
				if (fCurrentChar == '\n') {
					fRowCounter++;
					fCurrentColumnStartPosition = fCurrentPosition;
					if (fPackageMode && fRecursionDepth == 0) {
						fToken = TT_NEWLINE;
						return;
					}
					continue; // while loop
				}

				if ((Character.isJavaIdentifierStart(fCurrentChar) && (fCurrentChar != '_')) || (fCurrentChar == '$')) {
					// the Character.isUnicodeIdentifierStart method doesn't
					// work in Google Web Toolkit:
					// || (Character.isUnicodeIdentifierStart(fCurrentChar))) {
					fToken = TT_IDENTIFIER;
					return;
				}
				if (Character.isDigit(fCurrentChar)) {
					fToken = TT_DIGIT;
					return;
				}
				if (fCurrentChar == '.') {
					if (Character.isDigit(charAtPosition())) {
						fToken = TT_DIGIT;
						return;
					}
				}
				if (fCurrentChar == '(') {
					if (isValidPosition()) {
						if (charAtPosition() == '*') {
							getComment();
							continue;
						}
					}
				}

				switch (fCurrentChar) {

				case '(':
					fToken = TT_PRECEDENCE_OPEN;

					break;
				case ')':
					fToken = TT_PRECEDENCE_CLOSE;

					break;
				case '{':
					fToken = TT_LIST_OPEN;

					break;
				case '}':
					fToken = TT_LIST_CLOSE;

					break;
				case '[':
					fToken = TT_ARGUMENTS_OPEN;
					skipWhitespace();
					if (isValidPosition()) {
						if (charAtPosition() == '[') {
							fCurrentPosition++;
							fToken = TT_PARTOPEN;
							break;
						}
					}
					break;
				case ']':
					fToken = TT_ARGUMENTS_CLOSE;
					break;
				case '<':
					if (isValidPosition()) {
						if (charAtPosition() == '|') {
							fCurrentPosition++;
							fToken = TT_ASSOCIATION_OPEN;
							break;
						}
					}
					if (isOperatorCharacters()) {
						fOperList = getOperator();
						fToken = TT_OPERATOR;
						return;
					}

					break;
				case ';':
					if (isValidPosition()) {
						if (charAtPosition() == ';') {
							fCurrentPosition++;
							fToken = TT_SPAN;
							break;
						}
					}
					if (isOperatorCharacters()) {
						fOperList = getOperator();
						fToken = TT_OPERATOR;
						return;
					}

					break;
				case '|':
					if (isValidPosition()) {
						if (charAtPosition() == '>') {
							fCurrentPosition++;
							fToken = TT_ASSOCIATION_CLOSE;
							break;
						}
					}
					if (isOperatorCharacters()) {
						fOperList = getOperator();
						fToken = TT_OPERATOR;
						return;
					}
					break;
				case ',':
					fToken = TT_COMMA;
					break;
				case '_':
					fToken = TT_BLANK;
					if (isValidPosition()) {
						if (charAtPosition() == '_') {
							fCurrentPosition++;
							if (isValidPosition()) {
								if (charAtPosition() == '_') {
									fCurrentPosition++;
									fToken = TT_BLANK_BLANK_BLANK;
									break;
								}
							}
							fToken = TT_BLANK_BLANK;
						} else if (charAtPosition() == '.') {
							fCurrentPosition++;
							fToken = TT_BLANK_OPTIONAL;
						} else if (charAtPosition() == ':') {
							fCurrentPosition++;
							if (isValidPosition()) {
								if (charAtPosition() == '>') {
									fCurrentPosition--;
								} else {
									fToken = TT_BLANK_COLON;
								}
							}
						}
						break;
					}

					break;
				case '"':
					fToken = TT_STRING;

					break;
				case '\'':
					fToken = TT_DERIVATIVE;
					break;
				case '%':
					fToken = TT_PERCENT;

					break;
				case '#':
					fToken = TT_SLOT;
					if (isValidPosition()) {
						if (charAtPosition() == '#') {
							fCurrentPosition++;
							fToken = TT_SLOTSEQUENCE;

							break;
						}
					}
					break;
				default:
					if (isOperatorCharacters()) {
						fOperList = getOperator();
						fToken = TT_OPERATOR;
						return;
					}
					// if (fCurrentChar == '.') {
					// if (isValidPosition()) {
					// if (Character.isDigit(fCurrentChar)) {
					// // don't increment fCurrentPosition (see
					// // getNumberString())
					// fToken = TT_DIGIT; // floating-point number
					// break;
					// }
					// }
					// break;
					// } else {
					if (Characters.CharacterNamesMap.containsKey(String.valueOf(fCurrentChar))) {
						fToken = TT_IDENTIFIER;
						return;
					}
					throwSyntaxError("unexpected character: '" + fCurrentChar + "'");
					// }
				}

				if (fToken == TT_EOF) {
					throwSyntaxError("token not found");
				}

				return;
			}
		}

		fCurrentPosition = fInputString.length + 1;
		fCurrentChar = ' ';
		fToken = TT_EOF;
	}

	/**
	 * <p>
	 * Return an array of a <code>String</code> at index 0 representing the parse number string and an
	 * <code>Integer</code> representing the number format at index 1.
	 * </p>
	 * 
	 * The number format value can be
	 * <ul>
	 * <li>-1 for floating point numbers</li>
	 * <li>2 for a binary coded integer number</li>
	 * <li>8 for an octal coded integer number</li>
	 * <li>10 for a decimal coded integer number</li>
	 * <li>16 for a hexadecimal coded integer number</li>
	 * </ul>
	 * 
	 * @return
	 */
	protected Object[] getNumberString() {
		final Object[] result = new Object[2];
		int numFormat = 10;
		int startPosition = fCurrentPosition - 1;
		final char firstCh = fCurrentChar;
		boolean dFlag = false;
		// first digit
		if (fCurrentChar == '.') {
			dFlag = true;
		}
		getChar();
		if (fExplicitTimes) {
			if (firstCh == '0') {
				switch (fCurrentChar) {
				case 'b': // binary format
					numFormat = 2;
					startPosition = fCurrentPosition;
					getChar();
					break;
				case 'B': // binary format
					numFormat = 2;
					startPosition = fCurrentPosition;
					getChar();
					break;
				case 'o': // octal format
					numFormat = 8;
					startPosition = fCurrentPosition;
					getChar();
					break;
				case 'O': // octal format
					numFormat = 8;
					startPosition = fCurrentPosition;
					getChar();
					break;
				case 'x': // hexadecimal format
					numFormat = 16;
					startPosition = fCurrentPosition;
					getChar();
					break;
				case 'X': // hexadecimal format
					numFormat = 16;
					startPosition = fCurrentPosition;
					getChar();
					break;
				}
			}
		}

		if (numFormat == 10) {
			while (Character.isDigit(fCurrentChar) || (fCurrentChar == '.')) {
				if (fCurrentChar == '.') {
					if ((fCurrentChar == '.') && dFlag) {
						break;
					}
					if (fCurrentChar != ' ') {
						dFlag = true;
					}
				}
				getChar();
			}
			if (dFlag) {
				numFormat = -1;
			}
			if (numFormat == 10 && fCurrentChar == '^' && isValidPosition()) {
				char nextChar = fInputString[fCurrentPosition];
				if (nextChar == '^') {
					try {
						numFormat = Integer.parseInt(
								new String(fInputString, startPosition, fCurrentPosition - startPosition - 1));
						if (numFormat <= 0 || numFormat > 36) {
							throwSyntaxError(
									"Base " + numFormat + "^^... is invalid. Only bases between 1 and 36 are allowed");
						}
						fCurrentPosition++;
						startPosition = fCurrentPosition;
						boolean evaled = false;
						getChar();
						while (Character.isDigit(fCurrentChar) || //
								(fCurrentChar >= 'a' && fCurrentChar <= 'z') || //
								(fCurrentChar >= 'A' && fCurrentChar <= 'Z')) {
							evaled = true;
							getChar();
						}
						if (evaled && numFormat > 0 && numFormat <= 36) {
							int endPosition = fCurrentPosition--;
							result[0] = new String(fInputString, startPosition, (--endPosition) - startPosition);
							result[1] = Integer.valueOf(numFormat);
							return result;
						}
					} catch (RuntimeException rex) {
						//
					}
					throwSyntaxError("Base " + numFormat + "^^... is invalid. Only bases between 1 and 36 are allowed");
				}
			}
			if (fCurrentChar == 'E' || fCurrentChar == 'e') {
				if (fExplicitTimes) {
					numFormat = -1;
					getChar();
					if ((fCurrentChar == '+') || (fCurrentChar == '-')) {
						getChar();
					}
					while (Character.isDigit(fCurrentChar)) {
						getChar();
					}
				}
			} else {
				if (numFormat < 0) {
					if (fCurrentChar == '*') {
						int lastPosition = fCurrentPosition;
						getChar();
						if (fCurrentChar == '^') {
							getChar();
							if ((fCurrentChar == '+') || (fCurrentChar == '-')) {
								getChar();
							}
							if (Character.isDigit(fCurrentChar)) {
								do {
									getChar();
								} while (Character.isDigit(fCurrentChar));
							} else {
								fCurrentPosition = lastPosition;
							}
						} else {
							fCurrentPosition = lastPosition;
						}
					}
				}
			}
		} else if (numFormat == 16) {
			while (Character.isDigit(fCurrentChar) || ((fCurrentChar >= 'a') && (fCurrentChar <= 'f'))
					|| ((fCurrentChar >= 'A') && (fCurrentChar <= 'F'))) {
				getChar();
			}
		} else if (numFormat == 2) {
			while (fCurrentChar == '0' || fCurrentChar == '1') {
				getChar();
			}
		} else if (numFormat == 8) {
			while ((fCurrentChar >= '0') && (fCurrentChar <= '7')) {
				getChar();
			}
		}

		int endPosition = fCurrentPosition--;
		result[0] = new String(fInputString, startPosition, (--endPosition) - startPosition);
		result[1] = Integer.valueOf(numFormat);
		return result;
	}

	/**
	 * Get a list of operators for the operator string determined with TT_OPERATOR token detection.
	 * 
	 * @return
	 */
	abstract protected List<Operator> getOperator();

	/**
	 * Create a StringBuilder from the current parsed <code>&quot;...&quot;</code> string expression.
	 * 
	 * @return
	 * @throws SyntaxError
	 */
	protected StringBuilder getStringBuilder() throws SyntaxError {
		final StringBuilder ident = new StringBuilder();

		if (isValidPosition()) {
			fCurrentChar = fInputString[fCurrentPosition++];
		} else {
			throwSyntaxError("string - end of string not reached.");
		}
		if ((fCurrentChar == '\n') || (fToken == TT_EOF)) {
			throwSyntaxError("string -" + ident.toString() + "- contains no character.");
		}

		while (fCurrentChar != '"' && isValidPosition()) {

			if ((fCurrentChar == '\\')) {
				if (isValidPosition()) {
					fCurrentChar = fInputString[fCurrentPosition++];

					switch (fCurrentChar) {

					case '\\':
						ident.append(fCurrentChar);

						break;
					case 'n':
						ident.append('\n');

						break;
					case 't':
						ident.append('\t');

						break;
					case '\"':
						ident.append('\"');

						break;
					case '\n':
						fRowCounter++;
						fCurrentColumnStartPosition = fCurrentPosition;
						// a backslash at the end of the line means the scanner should continue on the next line
						continue;
					case '\r':
						if (isValidPosition() && fInputString[fCurrentPosition] == '\n') {
							// a backslash at the end of the line means the scanner should continue on the next line
							continue;
						}
						throwSyntaxError("string - unknown character after back-slash.");
					default:
						// throwSyntaxError("string - unknown character after back-slash.");
					}
				} else {
					throwSyntaxError("string - unknown character after back-slash.");
				}

				if (isValidPosition()) {
					fCurrentChar = fInputString[fCurrentPosition++];
				} else {
					throwSyntaxError("string - end of string not reached.");
				}
			} else {
				if ((fCurrentChar != '"') && (fToken == TT_EOF)) {
					throwSyntaxError("string -" + ident.toString() + "- not closed.");
				}

				ident.append(fCurrentChar);
				if (fCurrentChar == '\n') {
					fRowCounter++;
					fCurrentColumnStartPosition = fCurrentPosition;
				}
				if (isValidPosition()) {
					fCurrentChar = fInputString[fCurrentPosition++];
				} else {
					throwSyntaxError("string - end of string not reached.");
				}
			}
		}

		return ident;
	}

	protected void initialize(final String s) throws SyntaxError {
		initializeNullScanner();
		fInputString = Characters.substituteCharacters(s).toCharArray();
		getNextToken();
	}

	private void initializeNullScanner() {
		fInputString = null;
		fToken = TT_EOF;
		fCurrentPosition = 0;
		fRowCounter = 0;
		fCurrentColumnStartPosition = 0;
		fRecursionDepth = 0;
	}

	abstract protected boolean isOperatorCharacters();

	protected static final boolean isComparatorOperator(String operatorString) {
		return operatorString.equals("==") || operatorString.equals("!=") || //
				operatorString.equals(">") || operatorString.equals(">=") || //
				operatorString.equals("<") || operatorString.equals("<=");
	}

	/**
	 * Test if the current position in the parsed input string is less than the input strings length.
	 * 
	 * @return <code>true</code> if the current position in the parsed input string is less than the input strings
	 *         length.
	 */
	protected boolean isValidPosition() {
		return fInputString.length > fCurrentPosition;
	}

	/**
	 * Determines if the current character is white space according to <code>Character#isWhitespace()</code> method.
	 * 
	 * @return
	 */
	protected boolean isWhitespace() {
		if (isValidPosition()) {
			return Character.isWhitespace(charAtPosition());
		}
		return false;
	}

	protected void skipWhitespace() {
		if (isValidPosition()) {
			char ch = charAtPosition();
			if (!Character.isWhitespace(ch)) {
				return;
			}
			fCurrentPosition++;
			if (ch == '\n') {
				fRowCounter++;
				fCurrentColumnStartPosition = fCurrentPosition;
			}
		}
	}

	/**
	 * Return constant strings for variables of length 1.
	 * 
	 * @param startPosition
	 * @return <code>null</code> if no predefined constant string can be found
	 */
	final private String optimizedCurrentTokenSource1(final int startPosition) {
		// return always the same String build only once
		switch (fInputString[startPosition]) {
		case 'a':
			return string_a;
		case 'b':
			return string_b;
		case 'c':
			return string_c;
		case 'd':
			return string_d;
		case 'e':
			return string_e;
		case 'f':
			return string_f;
		case 'g':
			return string_g;
		case 'h':
			return string_h;
		case 'i':
			return string_i;
		case 'j':
			return string_j;
		case 'k':
			return string_k;
		case 'l':
			return string_l;
		case 'm':
			return string_m;
		case 'n':
			return string_n;
		case 'o':
			return string_o;
		case 'p':
			return string_p;
		case 'q':
			return string_q;
		case 'r':
			return string_r;
		case 's':
			return string_s;
		case 't':
			return string_t;
		case 'u':
			return string_u;
		case 'v':
			return string_v;
		case 'w':
			return string_w;
		case 'x':
			return string_x;
		case 'y':
			return string_y;
		case 'z':
			return string_z;

		case 'A':
			return string_A;
		case 'B':
			return string_B;
		case 'C':
			return string_C;
		case 'D':
			return string_D;
		case 'E':
			return string_E;
		case 'F':
			return string_F;
		case 'G':
			return string_G;
		case 'H':
			return string_H;
		case 'I':
			return string_I;
		case 'J':
			return string_J;
		case 'K':
			return string_K;
		case 'L':
			return string_L;
		case 'M':
			return string_M;
		case 'N':
			return string_N;
		case 'O':
			return string_O;
		case 'P':
			return string_P;
		case 'Q':
			return string_Q;
		case 'R':
			return string_R;
		case 'S':
			return string_S;
		case 'T':
			return string_T;
		case 'U':
			return string_U;
		case 'V':
			return string_V;
		case 'W':
			return string_W;
		case 'X':
			return string_X;
		case 'Y':
			return string_Y;
		case 'Z':
			return string_Z;
		default:
			return null;
		}
	}

	/**
	 * Return constant strings for variables of length 2 starting with a '$' character.
	 * 
	 * @param startPosition
	 * @return
	 */
	final private String optimizedCurrentTokenSource2(final int startPosition) {
		// return always the same String build only once
		switch (fInputString[startPosition + 1]) {
		case 'a':
			return var_a;
		case 'b':
			return var_b;
		case 'c':
			return var_c;
		case 'd':
			return var_d;
		case 'e':
			return var_e;
		case 'f':
			return var_f;
		case 'g':
			return var_g;
		case 'h':
			return var_h;
		case 'i':
			return var_i;
		case 'j':
			return var_j;
		case 'k':
			return var_k;
		case 'l':
			return var_l;
		case 'm':
			return var_m;
		case 'n':
			return var_n;
		case 'o':
			return var_o;
		case 'p':
			return var_p;
		case 'q':
			return var_q;
		case 'r':
			return var_r;
		case 's':
			return var_s;
		case 't':
			return var_t;
		case 'u':
			return var_u;
		case 'v':
			return var_v;
		case 'w':
			return var_w;
		case 'x':
			return var_x;
		case 'y':
			return var_y;
		case 'z':
			return var_z;
		default:
			return new String(fInputString, startPosition, 2);
		}
	}

	protected void throwSyntaxError(final String error) throws SyntaxError {
		throw new SyntaxError(fCurrentPosition - 1, fRowCounter, fCurrentPosition - fCurrentColumnStartPosition,
				getErrorLine(), error, 1);
	}

	protected void throwSyntaxError(final String error, final int errorLength) throws SyntaxError {
		throw new SyntaxError(fCurrentPosition - errorLength, fRowCounter,
				fCurrentPosition - fCurrentColumnStartPosition, getErrorLine(), error, errorLength);
	}

	/**
	 * Shows the current line for debugging purposes.
	 */
	public String toString() {
		if (fInputString == null || //
				fCurrentPosition < 0) {
			return "<undefined scanner>";
		}
		// read until end-of-line
		try {
			int eol = fCurrentPosition;
			while (fInputString.length > eol) {
				char ch = fInputString[eol++];
				if (ch == '\n') {
					eol--;
					break;
				}
			}
			String line = new String(fInputString, fCurrentColumnStartPosition, eol - fCurrentColumnStartPosition);
			final StringBuilder buf = new StringBuilder(line.length() + 256);
			buf.append(line + "\n");
			int indx = fCurrentPosition - fCurrentColumnStartPosition;
			for (int i = 0; i < indx; i++) {
				buf.append(' ');
			}
			buf.append("^\n");
			return buf.toString();
		} catch (IndexOutOfBoundsException ioob) {
			// thrown by new String(...)
		}
		return "<end-of-line>";
	}
}
