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

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.matheclipse.core.basic.Config;
import org.matheclipse.parser.client.ast.IParserFactory;
import org.matheclipse.parser.client.operator.Operator;

public class Scanner {
	/**
	 * Token type: End-of_File
	 */
	final static public int TT_EOF = 0;

	/**
	 * Token type: floating point number
	 */
	final static public int TT_FLOATING_POINT = 10;

	/**
	 * Token type: opening bracket for function arguments
	 */
	final static public int TT_ARGUMENTS_OPEN = 12;

	/**
	 * Token type: closing bracket for function arguments
	 */
	final static public int TT_ARGUMENTS_CLOSE = 13;

	/**
	 * Token type: opening bracket '(' for sub-formulas with higher precedence
	 */
	final static public int TT_PRECEDENCE_OPEN = 14;

	/**
	 * Token type: closing bracket ')' for sub-formulas with higher precedence
	 */
	final static public int TT_PRECEDENCE_CLOSE = 15;

	/**
	 * Token type: opening curly braces '{' for starting lists
	 */
	final static public int TT_LIST_OPEN = 16;

	/**
	 * Token type: closing curly braces '}' for ending lists
	 */
	final static public int TT_LIST_CLOSE = 17;

	/**
	 * Token type: opening brackets for starting the &quot;index part&quot; of an expression
	 */
	final static public int TT_PARTOPEN = 18;

	/**
	 * Token type: closing brackets for ending the &quot;index part&quot; of an expression
	 */
	final static public int TT_PARTCLOSE = 19;

	/**
	 * Token type: operator found in input string
	 */
	final static public int TT_OPERATOR = 31;

	/**
	 * ',' operator
	 */
	final static public int TT_COMMA = 134;

	/**
	 * '%' operator
	 */
	final static public int TT_PERCENT = 135;

	/**
	 * Token type: string surrounded by &quot;....&quot;
	 */
	final static public int TT_STRING = 136;

	/**
	 * Token type: pattern placeholder '_'
	 */
	final static public int TT_BLANK = 137;

	/**
	 * Token type: identifier name
	 */
	final static public int TT_IDENTIFIER = 138;

	/**
	 * Token type: digit 0,1,2,3,4,5,6,7,8,9
	 */
	final static public int TT_DIGIT = 139;

	/**
	 * Token type: slot #
	 */
	final static public int TT_SLOT = 141;

	/**
	 * Token type: slot sequence ##
	 */
	final static public int TT_SLOTSEQUENCE = 142;

	final static public int TT_BLANK_BLANK = 143;

	final static public int TT_BLANK_BLANK_BLANK = 144;

	/**
	 * Token type: pattern placeholder '_.'
	 */
	final static public int TT_BLANK_OPTIONAL = 145;

	final static public int TT_BLANK_COLON = 146;

	final static public int TT_DERIVATIVE = 147;

	final static public int TT_NEWLINE = 150;

	// ----------------optimized identifier managment------------------
	static final String string_a = "a", string_b = "b", string_c = "c", string_d = "d", string_e = "e", string_f = "f",
			string_g = "g", string_h = "h", string_i = "i", string_j = "j", string_k = "k", string_l = "l",
			string_m = "m", string_n = "n", string_o = "o", string_p = "p", string_q = "q", string_r = "r",
			string_s = "s", string_t = "t", string_u = "u", string_v = "v", string_w = "w", string_x = "x",
			string_y = "y", string_z = "z";

	static final String var_a = "$a", var_b = "$b", var_c = "$c", var_d = "$d", var_e = "$e", var_f = "$f",
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
	 * Current parser input string
	 */
	protected String fInputString;

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
	 * Row counter for syntax errors.
	 */
	protected int rowCount;

	/**
	 * Column counter for syntax errors
	 */
	protected int fCurrentColumnStartPosition;

	protected int numFormat = 0;

	protected IParserFactory fFactory;

	protected final boolean fPackageMode;

	/**
	 * Initialize Scanner without a math-expression
	 * 
	 * @param packageMode
	 *            <code>true</code> if currently a package is read.
	 */
	public Scanner(boolean packageMode) {
		fPackageMode = packageMode;
		initializeNullScanner();
	}

	/**
	 * Verify the length of the input string and get the next character from the input string. If the current position
	 * is greater than the input length, set current character to SPACE and token to TT_EOF.
	 * 
	 */
	private void getChar() {
		if (fInputString.length() > fCurrentPosition) {
			getNextChar();
			return;
		}
		fCurrentPosition = fInputString.length() + 1;
		fCurrentChar = ' ';
		fToken = TT_EOF;
	}

	private void getComment() {
		int startPosition = fCurrentPosition;
		int level = 0;
		fCurrentPosition++;
		// read multiline comment until end of file:
		try {
			while (true) {
				if (fInputString.charAt(fCurrentPosition) == '*' && fInputString.charAt(fCurrentPosition + 1) == ')') {
					fCurrentPosition++;
					fCurrentPosition++;
					if (level == 0) {
						break;
					}
					level--;
					continue;
				} else if (fInputString.charAt(fCurrentPosition) == '('
						&& fInputString.charAt(fCurrentPosition + 1) == '*') {
					fCurrentPosition++;
					fCurrentPosition++;
					level++;
					continue;
				} else if (fInputString.charAt(fCurrentPosition) == '\n') {
					fCurrentPosition++;
					rowCount++;
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

	private String getErrorLine() {
		if (fInputString.length() < fCurrentPosition) {
			fCurrentPosition--;
		}
		// read until end-of-line after the current fError
		int eol = fCurrentPosition;
		while (fInputString.length() > eol) {
			fCurrentChar = fInputString.charAt(eol++);
			if (fCurrentChar == '\n') {
				eol--;
				break;
			}
		}
		final String line = fInputString.substring(fCurrentColumnStartPosition, eol);
		return line;
	}

	protected String getIdentifier() {
		final int startPosition = fCurrentPosition - 1;

		getChar();
		if (fCurrentChar == '$') {
			getChar();
		}
		while (Character.isLetterOrDigit(fCurrentChar) || (fCurrentChar == '$')) {
			getChar();
		}

		int endPosition = fCurrentPosition--;
		final int length = (--endPosition) - startPosition;
		if (length == 1) {
			return optimizedCurrentTokenSource1(startPosition, endPosition);
		}
		if (length == 2 && fInputString.charAt(startPosition) == '$') {
			return optimizedCurrentTokenSource2(startPosition, endPosition);
		}

		return fInputString.substring(startPosition, endPosition);
	}

	private void getNextChar() {
		fCurrentChar = fInputString.charAt(fCurrentPosition++);
		if (fCurrentChar == '\\') {
			// search next non-whitespace character
			while (fInputString.length() > fCurrentPosition) {
				fCurrentChar = fInputString.charAt(fCurrentPosition++);
				if (!Character.isWhitespace(fCurrentChar) && fCurrentChar != '\\') {
					return;
				}
				if (fCurrentChar == '\n') {
					rowCount++;
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

		while (fInputString.length() > fCurrentPosition) {

			getNextChar();
			fToken = TT_EOF;

			if (fFactory.getOperatorCharacters().indexOf(fCurrentChar) >= 0) {
				fOperList = getOperator();
				fToken = TT_OPERATOR;
				return;
			}

			if ((fCurrentChar != '\t') && (fCurrentChar != '\r') && (fCurrentChar != ' ')) {
				if (fCurrentChar == '\n') {
					rowCount++;
					fCurrentColumnStartPosition = fCurrentPosition;
					if (fPackageMode && fRecursionDepth == 0) {
						fToken = TT_NEWLINE;
						return;
					}
					continue; // while loop
				}
				if (Character.isLetter(fCurrentChar) || (fCurrentChar == '$')) {
					// the Character.isUnicodeIdentifierStart method doesn't
					// work in Google Web Toolkit:
					fToken = TT_IDENTIFIER;
					return;
				}
				if ((fCurrentChar >= '0') && (fCurrentChar <= '9')) {
					fToken = TT_DIGIT;

					return;
				}
				if (fCurrentChar == '(' && fInputString.length() > fCurrentPosition
						&& fInputString.charAt(fCurrentPosition) == '*') {
					getComment();
					continue;
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
					if (fInputString.length() > fCurrentPosition && fInputString.charAt(fCurrentPosition) == '[') {
						fCurrentPosition++;
						fToken = TT_PARTOPEN;
					}
					break;
				case ']':
					fToken = TT_ARGUMENTS_CLOSE;
					break;
				case ',':
					fToken = TT_COMMA;

					break;
				case '_':
					fToken = TT_BLANK;
					if (fInputString.length() > fCurrentPosition) {
						if (fInputString.charAt(fCurrentPosition) == '_') {
							fCurrentPosition++;
							if (fInputString.length() > fCurrentPosition
									&& fInputString.charAt(fCurrentPosition) == '_') {
								fCurrentPosition++;
								fToken = TT_BLANK_BLANK_BLANK;
								break;
							}
							fToken = TT_BLANK_BLANK;
							break;
						} else if (fInputString.charAt(fCurrentPosition) == '.') {
							fCurrentPosition++;
							fToken = TT_BLANK_OPTIONAL;
							break;
						} else if (fInputString.charAt(fCurrentPosition) == ':') {
							fCurrentPosition++;
							fToken = TT_BLANK_COLON;
							break;
						}
					}

					break;
				case '.':
					if (fInputString.length() > fCurrentPosition && (fInputString.charAt(fCurrentPosition) >= '0')
							&& (fInputString.charAt(fCurrentPosition) <= '9')) {
						// don't increment fCurrentPosition (see
						// getNumberString())
						fToken = TT_DIGIT; // floating-point number
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
					if (fInputString.length() > fCurrentPosition && fInputString.charAt(fCurrentPosition) == '#') {
						fCurrentPosition++;
						fToken = TT_SLOTSEQUENCE;
					}

					break;
				default:
					throwSyntaxError("unexpected character: '" + fCurrentChar + "'");
				}

				if (fToken == TT_EOF) {
					throwSyntaxError("token not found");
				}

				return;
			}
		}

		fCurrentPosition = fInputString.length() + 1;
		fCurrentChar = ' ';
		fToken = TT_EOF;
	}

	protected Object[] getNumberString() {
		final Object[] result = new Object[2];
		numFormat = 10;
		int startPosition = fCurrentPosition - 1;
		final char firstCh = fCurrentChar;
		char dFlag = ' ';
		// first digit
		if (fCurrentChar == '.') {
			dFlag = fCurrentChar;
		}
		getChar();
		if (Config.EXPLICIT_TIMES_OPERATOR) {
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
				default:
				}
			}
		}

		if (numFormat == 2) {
			while ((fCurrentChar >= '0') && (fCurrentChar <= '1')) {
				getChar();
			}
		} else if (numFormat == 8) {
			while ((fCurrentChar >= '0') && (fCurrentChar <= '7')) {
				getChar();
			}
		} else if (numFormat == 16) {
			while (((fCurrentChar >= '0') && (fCurrentChar <= '9')) || ((fCurrentChar >= 'a') && (fCurrentChar <= 'f'))
					|| ((fCurrentChar >= 'A') && (fCurrentChar <= 'F'))) {
				getChar();
			}
		} else {
			while (((fCurrentChar >= '0') && (fCurrentChar <= '9')) || (fCurrentChar == '.')) {
				if (fCurrentChar == '.') {
					if ((fCurrentChar == '.') && (dFlag != ' ')) {
						break;
					}
					dFlag = fCurrentChar;
					getChar();
				} else {
					getChar();
				}
			}
			if (dFlag != ' ') {
				numFormat = -1;
			}
		}

		if ((fCurrentChar == 'E') || (fCurrentChar == 'e')) {
			if (Config.EXPLICIT_TIMES_OPERATOR) {
				getChar();
				if ((fCurrentChar == '+') || (fCurrentChar == '-')) {
					getChar();
				}
				while ((fCurrentChar >= '0') && (fCurrentChar <= '9')) {
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
						if ((fCurrentChar >= '0') && (fCurrentChar <= '9')) {
							while ((fCurrentChar >= '0') && (fCurrentChar <= '9')) {
								getChar();
							}
						} else {
							fCurrentPosition = lastPosition;
						}
					} else {
						fCurrentPosition = lastPosition;
					}
				}
			}
		}

		int endPosition = fCurrentPosition--;
		result[0] = fInputString.substring(startPosition, --endPosition);
		result[1] = Integer.valueOf(numFormat);
		return result;
	}

	/**
	 * protected List<Operator> getOperator()
	 * 
	 * @return
	 */
	protected List<Operator> getOperator() {
		char lastChar;
		final int startPosition = fCurrentPosition - 1;
		fOperatorString = fInputString.substring(startPosition, fCurrentPosition);
		List<Operator> list = fFactory.getOperatorList(fOperatorString);
		List<Operator> lastList = null;
		int lastOperatorPosition = -1;
		if (list != null) {
			lastList = list;
			lastOperatorPosition = fCurrentPosition;
		}
		getChar();
		while (fFactory.getOperatorCharacters().indexOf(fCurrentChar) >= 0) {
			lastChar = fCurrentChar;
			fOperatorString = fInputString.substring(startPosition, fCurrentPosition);
			list = fFactory.getOperatorList(fOperatorString);
			if (list != null) {
				lastList = list;
				lastOperatorPosition = fCurrentPosition;
			}
			getChar();
			if (lastChar == ';' && fCurrentChar != ';') {
				break;
			}
		}
		if (lastOperatorPosition > 0) {
			fCurrentPosition = lastOperatorPosition;
			return lastList;
		}
		final int endPosition = fCurrentPosition--;
		fCurrentPosition = startPosition;
		throwSyntaxError("Operator token not found: " + fInputString.substring(startPosition, endPosition - 1));
		return null;
	}

	protected StringBuilder getStringBuilder() throws SyntaxError {
		final StringBuilder ident = new StringBuilder();

		if (fInputString.length() > fCurrentPosition) {
			fCurrentChar = fInputString.charAt(fCurrentPosition++);
		} else {
			throwSyntaxError("string - end of string not reached.");
		}
		if ((fCurrentChar == '\n') || (fToken == TT_EOF)) {
			throwSyntaxError("string -" + ident.toString() + "- contains no character.");
		}

		while (fCurrentChar != '"') {
			if (fCurrentChar == '\\') {
				if (fInputString.length() > fCurrentPosition) {
					fCurrentChar = fInputString.charAt(fCurrentPosition++);

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
					default:
						throwSyntaxError("string - unknown character after back-slash.");
					}
				} else {
					throwSyntaxError("string - unknown character after back-slash.");
				}

				if (fInputString.length() > fCurrentPosition) {
					fCurrentChar = fInputString.charAt(fCurrentPosition++);
				} else {
					throwSyntaxError("string - end of string not reached.");
				}
			} else {
				if ((fCurrentChar != '"') && (fToken == TT_EOF)) {
					throwSyntaxError("string -" + ident.toString() + "- not closed.");
				}

				ident.append(fCurrentChar);
				if (fInputString.length() > fCurrentPosition) {
					fCurrentChar = fInputString.charAt(fCurrentPosition++);
				} else {
					throwSyntaxError("string - end of string not reached.");
				}
			}
		}

		return ident;
	}

	protected void initialize(final String s) throws SyntaxError {
		initializeNullScanner();
		StringBuilder buf = new StringBuilder(s.length());
		fInputString = Characters.substituteCharacters(s, buf);
		getNextToken();
	}

	private void initializeNullScanner() {
		fInputString = null;
		fToken = TT_EOF;
		fCurrentPosition = 0;
		rowCount = 0;
		fCurrentColumnStartPosition = 0;
	}

	/**
	 * Determines if the current character is white space according to <code>Character#isWhitespace()</code> method.
	 * 
	 * @return
	 */
	protected boolean isWhitespace() {
		if (fInputString.length() > fCurrentPosition) {
			return Character.isWhitespace(fInputString.charAt(fCurrentPosition));
		}
		return false;
	}

	final private String optimizedCurrentTokenSource1(final int startPosition, final int endPosition) {
		// return always the same String build only once

		switch (fInputString.charAt(startPosition)) {
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
		default:
			return fInputString.substring(startPosition, endPosition);
		}
	}

	final private String optimizedCurrentTokenSource2(final int startPosition, final int endPosition) {
		// return always the same String build only once
		switch (fInputString.charAt(startPosition + 1)) {
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
			return fInputString.substring(startPosition, endPosition);
		}
	}

	protected void throwSyntaxError(final String error) throws SyntaxError {
		throw new SyntaxError(fCurrentPosition - 1, rowCount, fCurrentPosition - fCurrentColumnStartPosition,
				getErrorLine(), error, 1);
	}

	protected void throwSyntaxError(final String error, final int errorLength) throws SyntaxError {
		throw new SyntaxError(fCurrentPosition - errorLength, rowCount, fCurrentPosition - fCurrentColumnStartPosition,
				getErrorLine(), error, errorLength);
	}
}
