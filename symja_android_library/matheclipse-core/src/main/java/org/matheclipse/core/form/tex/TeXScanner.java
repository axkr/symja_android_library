package org.matheclipse.core.form.tex;

import org.matheclipse.parser.client.Characters;

public abstract class TeXScanner {

  /** Token type: End-of_File */
  protected static final int TT_EOF = 0;


  /** Token type: opening bracket for function arguments */
  protected static final int TT_ARGUMENTS_OPEN = 12;

  /** Token type: closing bracket for function arguments */
  protected static final int TT_ARGUMENTS_CLOSE = 13;

  /** Token type: opening bracket '(' for sub-formulas with higher precedence */
  protected static final int TT_PRECEDENCE_OPEN = 14;

  /** Token type: closing bracket ')' for sub-formulas with higher precedence */
  protected static final int TT_PRECEDENCE_CLOSE = 15;

  /** Token type: opening curly braces '{' for starting lists */
  protected static final int TT_LIST_OPEN = 16;

  /** Token type: closing curly braces '}' for ending lists */
  protected static final int TT_LIST_CLOSE = 17;

  /** Token type: operator found in input string */
  // protected static final int TT_OPERATOR = 31;

  /** ',' operator */
  protected static final int TT_COMMA = 134;

  /** '%' operator */
  // protected static final int TT_PERCENT = 135;

  /** Token type: string surrounded by &quot;....&quot; */
  protected static final int TT_STRING = 136;

  /** Token type: identifier name */
  protected static final int TT_IDENTIFIER = 137;

  /** Token type: digit 0,1,2,3,4,5,6,7,8,9 */
  protected static final int TT_DIGIT = 138;

  /** Token type: slot # */
  // protected static final int TT_SLOT = 140;

  /** Token type: pattern ''' (single apostrophe) for writing derivatives */
  protected static final int TT_DERIVATIVE = 147;

  /**
   * New line token for character '\n'. This token will only be scanned, if {@link #fPackageMode} is
   * <code>true</code> and the recursion depth {@link #fRecursionDepth} of the already parsed AST
   * nodes has depth <code>0</code>. Otherwise the newline is scanned like a whitespace character.
   */
  protected static final int TT_NEWLINE = 150;

  /** Token type: TeX command name */
  protected static final int TT_COMMAND = 151;

  /** Token type: single backslash character */
  protected static final int TT_BACKSLASH = 152;

  protected static final int TT_DOUBLE_BACKSLASH = 153;

  protected static final int TT_BACKSLASH_SPACE = 154;

  protected static final int TT_BEGIN = 155;

  protected static final int TT_END = 156;

  protected static final int TT_CHARACTER = 157;

  protected static final int TT_AMPERSAND = 158;

  /** Token type: '_' */
  protected static final int TT_SUBSCRIPT = 159;

  /** Token type: '^' */
  protected static final int TT_SUPERSCRIPT = 160;

  // ----------------optimized identifier management------------------
  private static final String string_a = "a", string_b = "b", string_c = "c", string_d = "d",
      string_e = "e", string_f = "f", string_g = "g", string_h = "h", string_i = "i",
      string_j = "j", string_k = "k", string_l = "l", string_m = "m", string_n = "n",
      string_o = "o", string_p = "p", string_q = "q", string_r = "r", string_s = "s",
      string_t = "t", string_u = "u", string_v = "v", string_w = "w", string_x = "x",
      string_y = "y", string_z = "z";
  private static final String string_A = "A", string_B = "B", string_C = "C", string_D = "D",
      string_E = "E", string_F = "F", string_G = "G", string_H = "H", string_I = "I",
      string_J = "J", string_K = "K", string_L = "L", string_M = "M", string_N = "N",
      string_O = "O", string_P = "P", string_Q = "Q", string_R = "R", string_S = "S",
      string_T = "T", string_U = "U", string_V = "V", string_W = "W", string_X = "X",
      string_Y = "Y", string_Z = "Z";
  private static final String var_a = "$a", var_b = "$b", var_c = "$c", var_d = "$d", var_e = "$e",
      var_f = "$f", var_g = "$g", var_h = "$h", var_i = "$i", var_j = "$j", var_k = "$k",
      var_l = "$l", var_m = "$m", var_n = "$n", var_o = "$o", var_p = "$p", var_q = "$q",
      var_r = "$r", var_s = "$s", var_t = "$t", var_u = "$u", var_v = "$v", var_w = "$w",
      var_x = "$x", var_y = "$y", var_z = "$z";

  /** Current parser input string */
  protected char[] fInputString;

  /** Recursion depth for brackets. */
  protected int fRecursionDepth;

  /** Current input character */
  protected char fCurrentChar;

  /** The position of the current character in the input string */
  protected int fCurrentPosition;

  /** Current input token */
  protected int fToken;

  /** The last determined operator string */
  // protected String fOperatorString;

  /** protected List<Operator> fOperList; */
  // protected List<Operator> fOperList;

  /** The last determined command string */
  protected String fCommandString;

  /** Row counter for reporting the row where a syntax error occurred. */
  protected int fRowCounter;

  /** Is true if the parser is parsing a <code>package</code>. */
  protected boolean fPackageMode = false;

  /** Current rows start position for reporting syntax errors */
  protected int fCurrentColumnStartPosition;

  /**
   * If <code>true</code> the <code>*</code> operator must be written for a <code>Times()</code>
   * expression. I.e. you cannot write <code>2(b+c)</code> anymore, but have to write <code>2*(b+c)
   * </code> to get <code>Times(2, Plus(b, c))</code>.
   *
   * <p>
   * You also enable
   * <a href="https://en.wikipedia.org/wiki/Scientific_notation#E-notation">scientific
   * E-notation</a>. I.e. <code>1E-2</code> is converted to a double value <code>0.01</code> for
   * floating point numbers and not parsed as <code>Plus(-2, E)</code> anymore.
   *
   * <p>
   * You also enable integer literal input with a prefix, similar to
   * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">Java integer
   * literals</a>
   *
   * <ul>
   * <li><code>0b</code> or <code>0B</code> for binary numbers
   * <li><code>0x</code> or <code>0X</code> for hexadecimal numbers
   * <li><code>0o</code> or <code>0O</code> for octal numbers
   * </ul>
   */
  protected final boolean fExplicitTimes;

  /** Initialize Scanner without a math-expression */
  protected TeXScanner(boolean packageMode, boolean explicitTimes) {
    fPackageMode = packageMode;
    fExplicitTimes = explicitTimes;
    initializeNullScanner();
  }

  /**
   * Get the character at the current position in the parsed input string.
   *
   * @return the character at the current position in the parsed input string.
   */
  protected final char charAtPosition() {
    return fInputString[fCurrentPosition];
  }

  /**
   * Return <code>'\0'</code> space character if the scanner is at the end of the input string;
   * otherwise return the character at the current position.
   * 
   * @return the character at the current position in the parsed input string or the
   *         <code>'\0'</code> space character if the scanner is at the end of the input string.
   */
  protected final char checkedCharAtPosition() {
    return fInputString.length > fCurrentPosition ? fInputString[fCurrentPosition] : '\0';
  }

  /**
   * Create a string with first character 1 appending exponent times 0.
   *
   * @param nonNegativeExponent the number of <code>0</code> which should be appended; the number
   *        must be a non-negative integer.
   * @return
   */
  protected static StringBuilder createPowersOf10(int nonNegativeExponent) {
    StringBuilder buf = new StringBuilder(nonNegativeExponent + 1);
    buf.append("1");
    for (int i = 0; i < nonNegativeExponent; i++) {
      buf.append('0');
    }
    return buf;
  }

  /**
   * Verify the length of the input string and get the next character from the input string. If the
   * current position is greater than the input length, set current character to SPACE and token to
   * TT_EOF.
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
   * Parse an identifier string (function, constant or variable name).
   *
   * @return an array which contains &quot;identifier&quot;
   */
  protected String getIdentifier() {
    int startPosition = fCurrentPosition - 1;

    getChar();
    while (Characters.isSymjaIdentifierPart(fCurrentChar)) {
      getChar();
    }
    int endPosition = fCurrentPosition--;
    final int length = (--endPosition) - startPosition;
    if (length == 1) {
      String name = optimizedCurrentTokenSource1(startPosition);
      if (name == null) {
        name = Characters.CharacterNamesMap.get(String.valueOf(fInputString[startPosition]));
        if (name != null) {
          return name;
        }
      } else {
        return name;
      }
      return new String(fInputString, startPosition, 1);
    }
    if (length == 2 && fInputString[startPosition] == '$') {
      return optimizedCurrentTokenSource2(startPosition);
    }
    return new String(fInputString, startPosition, endPosition - startPosition);
  }

  /**
   * Parse a Java <code>int</code> value.
   *
   * @return
   * @throws TeXSyntaxError if the number couldn't be parsed as Java <code>int</code> value.
   */
  protected int getJavaInt() throws TeXSyntaxError {
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
   * @throws TeXSyntaxError if the number couldn't be parsed as Java <code>int</code> value.
   */
  protected long getJavaLong() throws TeXSyntaxError {
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

  /** @return <code>true</code> if a '\' (backslash) + new-line are detected */
  private boolean getNextChar() {
    fCurrentChar = fInputString[fCurrentPosition++];
    return true;
  }

  /**
   * 
   * 
   * @throws TeXSyntaxError
   */

  /**
   * Try to find the closing token recursively.
   * 
   * @param openToken
   * @param closeToken
   * @return
   * @throws TeXSyntaxError
   */
  protected int indexOfToken(int openToken, int closeToken) throws TeXSyntaxError {
    int nestingLevel = 1;
    while (nestingLevel != 0 && fToken != TT_EOF) {
      getNextToken();
      if (fToken == openToken) {
        nestingLevel++;
      } else if (fToken == closeToken) {
        nestingLevel--;
      }
    }
    if (fToken == TT_EOF) {
      return -1;
    }
    return fCurrentPosition - 1;
  }

  protected int indexOfCommand(String openCommand, String closeCommand) throws TeXSyntaxError {
    int nestingLevel = 1;
    while (nestingLevel != 0 && fToken != TT_EOF) {
      getNextToken();
      if (fToken == TT_COMMAND) {
        if (fCommandString.equals(openCommand)) {
          nestingLevel++;
        } else if (fCommandString.equals(closeCommand)) {
          nestingLevel--;
        }
      }
    }
    if (fToken == TT_EOF) {
      return -1;
    }
    return fCurrentPosition - closeCommand.length() - 1;
  }

  /**
   * Get the next token from the input string
   *
   * @throws TeXSyntaxError
   */
  protected void getNextToken() throws TeXSyntaxError {

    while (isValidPosition()) {
      getNextChar();
      fToken = TT_EOF;

      if ((fCurrentChar != '\t') && (fCurrentChar != '\r') && (fCurrentChar != ' ')) {
        if (fCurrentChar == '\n') {
          fRowCounter++;
          fCurrentColumnStartPosition = fCurrentPosition;
          if (fPackageMode) {
            fToken = TT_NEWLINE;
            return;
          }
          continue; // while loop
        }

        if (Characters.isSymjaIdentifierStart(fCurrentChar)) {
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
        if (fCurrentChar == '\\') {
          if (isValidPosition()) {
            char specialChar = charAtPosition();
            if (specialChar == '\\') {
              fCurrentPosition++;
              fToken = TT_DOUBLE_BACKSLASH;
              return;
            }
            if (specialChar == '!' //
                || specialChar == ',' //
                || specialChar == ':' //
                || specialChar == ';' //
                || specialChar == ':' //
                || specialChar == ':') {
              fCurrentPosition++;
              fToken = TT_BACKSLASH_SPACE;
              return;
            }
            if (specialChar == '{' || specialChar == '}') {
              fCurrentPosition++;
              fToken = TT_CHARACTER;
              return;
            }
          }
          // TeX command?
          getNextChar();
          StringBuilder command = new StringBuilder();
          while (isValidPosition() && Character.isJavaIdentifierPart(fCurrentChar) //
              && !(fCurrentChar == '_')) {
            command.append(fCurrentChar);
            getNextChar();
          }

          if (command.length() > 0) {
            fCurrentPosition--;
            fToken = TT_COMMAND;
            fCommandString = command.toString();
            if (fCommandString.equals("begin")) {
              fToken = TT_BEGIN;
            } else if (fCommandString.equals("end")) {
              fToken = TT_END;
            }
            return;
          }
          fToken = TT_BACKSLASH;
          return;
        }
        if (fCurrentChar == '.') {
          if (isValidPosition()) {
            if (Character.isDigit(charAtPosition())) {
              fToken = TT_DIGIT;
              return;
            }
          }
        }

        switch (fCurrentChar) {
          case '(':
            fToken = TT_PRECEDENCE_OPEN;
            skipWhitespace();
            break;
          case ')':
            fToken = TT_PRECEDENCE_CLOSE;
            break;
          case '{':
            fToken = TT_LIST_OPEN;
            skipWhitespace();
            break;
          case '}':
            fToken = TT_LIST_CLOSE;
            break;
          case '[':
            fToken = TT_ARGUMENTS_OPEN;
            skipWhitespace();
            break;
          case ']':
            fToken = TT_ARGUMENTS_CLOSE;
            break;
          case '&':
            fToken = TT_AMPERSAND;
            break;
          case '.':
            if (isValidPosition()) {
              if (Character.isDigit(charAtPosition())) {
                fToken = TT_DIGIT;
                skipWhitespace();
                break;
              }
            }
            // if (isOperatorCharacters()) {
            // fOperList = getOperator();
            // fToken = TT_OPERATOR;
            // return;
            // }
            fToken = TT_CHARACTER;
            break;
          // case '<':
          // if (checkedCharAtPosition() == '|') {
          // fCurrentPosition++;
          // fToken = TT_ASSOCIATION_OPEN;
          // skipWhitespace();
          // } else
          // if (isOperatorCharacters()) {
          // fOperList = getOperator();
          // fToken = TT_OPERATOR;
          // return;
          // }

          // break;
          // case ':':
          // if (isOperatorCharacters()) {
          // fOperList = getOperator();
          // fToken = TT_OPERATOR;
          // return;
          // }
          // break;
          // case ';':
          // // if (checkedCharAtPosition() == ';') {
          // // fCurrentPosition++;
          // // fToken = TT_SPAN;
          // // } else
          // if (isOperatorCharacters()) {
          // fOperList = getOperator();
          // fToken = TT_OPERATOR;
          // return;
          // }
          // break;
          // case '|':
          // if (checkedCharAtPosition() == '>') {
          // fCurrentPosition++;
          // fToken = TT_ASSOCIATION_CLOSE;
          // } else
          // if (isOperatorCharacters()) {
          // fOperList = getOperator();
          // fToken = TT_OPERATOR;
          // return;
          // }
          // break;
          case ',':
            fToken = TT_COMMA;
            break;
          case '^':
            fToken = TT_SUPERSCRIPT;
            break;
          case '_':
            fToken = TT_SUBSCRIPT;
            // if (isValidPosition()) {
            // if (charAtPosition() == '_') {
            // fCurrentPosition++;
            // if (checkedCharAtPosition() == '_') {
            // fCurrentPosition++;
            // fToken = TT_BLANK_BLANK_BLANK;
            // break;
            // }
            // fToken = TT_BLANK_BLANK;
            // } else if (charAtPosition() == '.') {
            // fCurrentPosition++;
            // fToken = TT_BLANK_OPTIONAL;
            // } else if (charAtPosition() == ':') {
            // fCurrentPosition++;
            // if (checkedCharAtPosition() == '>') {
            // fCurrentPosition--;
            // } else {
            // fToken = TT_BLANK_COLON;
            // }
            // }
            // break;
            // }
            break;
          case '"':
            fToken = TT_STRING;
            break;
          case '\'':
            fToken = TT_DERIVATIVE;
            break;
          // case '%':
          // fToken = TT_PERCENT;
          //
          // break;
          // case '#':
          // fToken = TT_SLOT;
          // break;
          default:
            // if (isOperatorCharacters()) {
            // fOperList = getOperator();
            // fToken = TT_OPERATOR;
            // return;
            // }
            // if (Characters.CharacterNamesMap.containsKey(String.valueOf(fCurrentChar))) {
            // fToken = TT_IDENTIFIER;
            // return;
            // }
            // if (isValidPosition()) {
            // int codePoint = Character.codePointAt(fInputString, fCurrentPosition - 1);
            // String str = Characters.unicodePoint(codePoint);
            // if (str != null) {
            // throwSyntaxError("unexpected (named unicode) character: '\\[" + str + "]'");
            // // fCurrentPosition++;
            // // fToken = TT_IDENTIFIER;
            // // return;
            // }
            // }
            fToken = TT_CHARACTER;
            return;
          // String str = Characters.unicodeName(fCurrentChar);
          // if (str != null) {
          // throwSyntaxError("unexpected (named unicode) character: '\\[" + str + "]'");
          // }
          // throwSyntaxError("unexpected character: '" + fCurrentChar + "'");
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
   * Return an array of a <code>String</code> at index 0 representing the parse number string and an
   * <code>Integer</code> representing the number format at index 1 and a <code>String</code>
   * representing the integer exponent at index 2. The number format value can be
   *
   * <ul>
   * <li>-1 for floating point numbers
   * <li>2 for a binary coded integer number
   * <li>8 for an octal coded integer number
   * <li>10 for a decimal coded integer number
   * <li>16 for a hexadecimal coded integer number
   * </ul>
   *
   * @return
   */
  protected Object[] getNumberString() {
    final Object[] result = new Object[3];
    result[2] = "1";
    int numFormat = 10;
    int startPosition = fCurrentPosition - 1;
    final char firstCh = fCurrentChar;
    boolean isFloatPointNumber = firstCh == '.' ? true : false;
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

    boolean backslash = false;
    if (numFormat == 10) {
      while (Character.isDigit(fCurrentChar) || (fCurrentChar == '.')) {
        if (fCurrentChar == '.') {
          if (isFloatPointNumber) {
            break;
          }
          isFloatPointNumber = true;
        }

        if (isValidPosition()) {
          if (getNextChar()) {
            backslash = true;
            if (Character.isDigit(fCurrentChar) || (fCurrentChar == '.')) {
              continue;
            }
            throwSyntaxError("error in number - unknown character after back-slash.");
          }
          continue;
        }
        fCurrentPosition = fInputString.length + 1;
        fCurrentChar = ' ';
        fToken = TT_EOF;
      }
      if (isFloatPointNumber) {
        numFormat = -1;
      }
      if (numFormat == 10 && fCurrentChar == '^' && isValidPosition()) {
        char nextChar = fInputString[fCurrentPosition];
        if (nextChar == '^') {
          try {
            String numberStr =
                new String(fInputString, startPosition, fCurrentPosition - startPosition - 1);
            if (backslash) {
              numberStr = sanitizeBackslash(numberStr);
            }
            numFormat = Integer.parseInt(numberStr);
            if (numFormat <= 0 || numFormat > 36) {
              throwSyntaxError("Base " + numFormat
                  + "^^... is invalid. Only bases between 1 and 36 are allowed");
            }
            fCurrentPosition++;
            startPosition = fCurrentPosition;
            boolean evaled = false;
            getChar();
            while (Character.isDigit(fCurrentChar) || (fCurrentChar >= 'a' && fCurrentChar <= 'z')
                || (fCurrentChar >= 'A' && fCurrentChar <= 'Z')) {
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
          throwSyntaxError(
              "Base " + numFormat + "^^... is invalid. Only bases between 1 and 36 are allowed");
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
        if (numFormat < 0 || numFormat == 10) {
          if (fCurrentChar == '*') {
            // numFormat = -1;
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
    String numberStr = new String(fInputString, startPosition, (--endPosition) - startPosition);
    if (backslash) {
      numberStr = sanitizeBackslash(numberStr);
    }
    if (numFormat == 10) {
      int indx = numberStr.indexOf("*^");
      if (indx > 0) {
        result[0] = numberStr.substring(0, indx);
        result[1] = Integer.valueOf(numFormat);
        result[2] = numberStr.substring(indx + 2);
        return result;
      }
    }
    result[0] = numberStr;
    result[1] = Integer.valueOf(numFormat);
    return result;
  }

  private String sanitizeBackslash(String numberStr) {
    StringBuilder buf = new StringBuilder(numberStr.length() - 2);
    for (int i = 0; i < numberStr.length(); i++) {
      char ch = numberStr.charAt(i);
      if (ch == '\\' || ch == '\r' || ch == '\n') {
        continue;
      }
      buf.append(ch);
    }
    numberStr = buf.toString();
    return numberStr;
  }

  /**
   * Get a list of operators for the operator string determined with TT_OPERATOR token detection.
   *
   * @return
   */
  // protected abstract List<Operator> getOperator();

  /**
   * Create a StringBuilder from the current parsed <code>&quot;...&quot;</code> string expression.
   *
   * @return
   * @throws TeXSyntaxError
   */
  protected StringBuilder getStringBuilder() throws TeXSyntaxError {
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
            case 'r':
              ident.append('\r');
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
              // a backslash at the end of the line means the scanner should continue on the next
              // line
              continue;
            case '\r':
              if (isValidPosition() && fInputString[fCurrentPosition] == '\n') {
                // a backslash at the end of the line means the scanner should continue on the next
                // line
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
        if (fToken == TT_EOF) {
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

  protected void initialize(final String s) throws TeXSyntaxError {
    initializeNullScanner();
    fInputString = s.toCharArray();// Characters.substituteCharacters(s).toCharArray();
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

  // protected abstract boolean isOperatorCharacters();
  //
  // protected abstract boolean isOperatorCharacters(char ch);

  public static boolean isIdentifier(String ident) {
    if (ident.length() == 0) {
      return false;
    }
    char ch = ident.charAt(0);
    if ((Character.isJavaIdentifierStart(ch) && (ch != '_')) || (ch == '$')) {
      for (int i = 1; i < ident.length(); i++) {
        ch = ident.charAt(i);
        if ((Character.isJavaIdentifierPart(ch) && (ch != '_')) || (ch == '$') || (ch == '`')) {
          continue;
        }
        return false;
      }
    }

    return true;
  }

  protected static final boolean isComparatorOperator(String operatorString) {
    return operatorString.equals("==") || operatorString.equals("!=") || operatorString.equals(">")
        || operatorString.equals(">=") || operatorString.equals("<") || operatorString.equals("<=");
  }

  /**
   * Test if the current position in the parsed input string is less than the input strings length.
   *
   * @return <code>true</code> if the current position in the parsed input string is less than the
   *         input strings length.
   */
  protected boolean isValidPosition() {
    return fInputString.length > fCurrentPosition;
  }

  /**
   * Determines if the current character is white space according to <code>Character#isWhitespace()
   * </code> method.
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
  private final String optimizedCurrentTokenSource1(final int startPosition) {
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
  private final String optimizedCurrentTokenSource2(final int startPosition) {
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

  public void throwSyntaxError(final String error) throws TeXSyntaxError {
    throw new TeXSyntaxError(fCurrentPosition - 1, fRowCounter,
        fCurrentPosition - fCurrentColumnStartPosition, getErrorLine(), error, 1);
  }

  protected void throwSyntaxError(final String error, final int errorLength) throws TeXSyntaxError {
    throw new TeXSyntaxError(fCurrentPosition - errorLength, fRowCounter,
        fCurrentPosition - fCurrentColumnStartPosition, getErrorLine(), error, errorLength);
  }

  /** Shows the current line for debugging purposes. */
  @Override
  public String toString() {
    if (fInputString == null || fCurrentPosition < 0) {
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
      String line =
          new String(fInputString, fCurrentColumnStartPosition, eol - fCurrentColumnStartPosition);
      final StringBuilder buf = new StringBuilder(line.length() + 256);
      buf.append(line);
      buf.append("\n");
      int indx = fCurrentPosition - fCurrentColumnStartPosition;
      for (int i = 0; i < indx; i++) {
        buf.append(' ');
      }
      buf.append("^(");
      buf.append(tokenToString(fToken));
      buf.append("-");
      buf.append(fToken);
      buf.append(")\n");
      return buf.toString();
    } catch (IndexOutOfBoundsException ioob) {
      // thrown by new String(...)
    }
    return "<end-of-line>";
  }

  /** Get the current token string for debugging purposes. */
  private static String tokenToString(int token) {
    switch (token) {
      case TT_EOF:
        return "TT_EOF";
      case TT_ARGUMENTS_OPEN:
        return "TT_ARGUMENTS_OPEN";
      case TT_ARGUMENTS_CLOSE:
        return "TT_ARGUMENTS_CLOSE";
      case TT_PRECEDENCE_OPEN:
        return "TT_PRECEDENCE_OPEN";
      case TT_PRECEDENCE_CLOSE:
        return "TT_PRECEDENCE_CLOSE";
      case TT_LIST_OPEN:
        return "TT_LIST_OPEN";
      case TT_LIST_CLOSE:
        return "TT_LIST_CLOSE";
      // case TT_OPERATOR:
      // return "TT_OPERATOR";
      case TT_COMMA:
        return "TT_COMMA";
      // case TT_PERCENT:
      // return "TT_PERCENT";
      case TT_STRING:
        return "TT_STRING";
      case TT_IDENTIFIER:
        return "TT_IDENTIFIER";
      case TT_DIGIT:
        return "TT_DIGIT";
      // case TT_SLOT:
      // return "TT_SLOT";
      case TT_SUBSCRIPT:
        return "TT_BLANK";
      case TT_DERIVATIVE:
        return "TT_DERIVATIVE";
      case TT_NEWLINE:
        return "TT_NEWLINE";
      case TT_COMMAND:
        return "TT_COMMAND";
      case TT_BACKSLASH:
        return "TT_BACKSLASH";
      case TT_DOUBLE_BACKSLASH:
        return "TT_DOUBLE_BACKSLASH";
      case TT_BEGIN:
        return "TT_BEGIN";
      case TT_END:
        return "TT_END";
      case TT_CHARACTER:
        return "TT_CHARACTER";
      case TT_AMPERSAND:
        return "TT_AMPERSAND";
      default:
        return "token undefined";
    }
  }

}
