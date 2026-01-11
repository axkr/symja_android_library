package org.matheclipse.core.interfaces;

import java.text.Collator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.AbstractPatternSequence;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.PatternNested;
import org.matheclipse.core.expression.RepeatedPattern;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.UniformFlags;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.parser.client.ParserConfig;

/**
 * Interface for <b>String Expressions</b> in the Symja Algebra System.
 * <p>
 * {@code IStringX} represents a character string acting as an atomic expression within the system.
 * Unlike {@link ISymbol}, which represents an identifier (variable or function name), an
 * {@code IStringX} represents raw textual data (e.g., {@code "Hello World"}). It extends
 * {@link IExpr} to allow strings to be treated as first-class citizens in abstract syntax trees.
 * </p>
 *
 * <h3>1. Core Capabilities</h3>
 * <p>
 * This interface mirrors much of the standard {@link java.lang.String} API to facilitate easy
 * manipulation of string data within the CAS context:
 * </p>
 * <ul>
 * <li><b>Search:</b> Methods like {@link #indexOf(String)} and {@link #lastIndexOf(int)} allow
 * searching for substrings or characters.</li>
 * <li><b>Slicing:</b> Methods like {@link #substring(int, int)} allow extracting parts of the
 * text.</li>
 * <li><b>MIME Types:</b> The interface defines constants (e.g., {@link #TEXT_HTML},
 * {@link #TEXT_LATEX}) often used to tag the format of the string content during output
 * generation.</li>
 * </ul>
 *
 * <h3>2. Usage Examples</h3>
 *
 * <h4>Creating a String Expression</h4>
 * 
 * <pre>
 * // Create a Symja string "Hello"
 * IStringX str = F.stringx("Hello");
 * </pre>
 *
 * <h4>Manipulation</h4>
 * 
 * <pre>
 * IStringX s = F.stringx("Symja System");
 *
 * // Get character at index 1
 * char c = s.charAt(1); // 'y'
 *
 * // Extract substring
 * String sub = s.substring(0, 5); // "Symja"
 * </pre>
 *
 * <h4>Usage in Lists</h4>
 * 
 * <pre>
 * // Strings can be arguments in functions or lists
 * // List["Label", 10]
 * IAST list = F.List(F.stringx("Label"), F.C10);
 * </pre>
 *
 * @see org.matheclipse.core.expression.StringX
 * @see org.matheclipse.core.expression.F#stringx(String)
 */
public interface IStringX extends IExpr, IAtomicConstant, IAtomicEvaluate {
  public static final short TEXT_PLAIN = 1;
  public static final short TEXT_HTML = 2;
  public static final short TEXT_MATHML = 3;
  public static final short TEXT_LATEX = 4;

  public static final short APPLICATION_SYMJA = 5;
  public static final short APPLICATION_JAVA = 6;
  public static final short APPLICATION_JAVASCRIPT = 7;

  public static final short TEXT_JSON = 8;

  /**
   * Symbol names are compared lexicographically based on {@link Locale#US}
   */
  Collator US_COLLATOR = Collator.getInstance(Locale.US);
  /**
   * Index <code>1</code> in {@link REGEX_LONGEST} and {@link REGEX_SHORTEST}. The asterisk
   * quantifier indicates zero or more occurrences of the preceding element. For example, ab*c
   * matches "ac", "abc", "abbc", "abbbc", and so on.
   */
  static final int ASTERISK_Q = 1;

  /**
   * Index <code>0</code> in {@link REGEX_LONGEST} and {@link REGEX_SHORTEST}. The plus quantifier
   * indicates one or more occurrences of the preceding element. For example, ab+c matches "abc",
   * "abbc", "abbbc", and so on, but not "ac".
   */
  static final int PLUS_Q = 0;

  /**
   * Longest regex quantifier for <code>BlankSequence</code>,<code>BlankNullSequence</code>, <code>
   * Repeated</code>, <code>RepeatedNull</code>.
   *
   * @see <a href="https://en.wikipedia.org/wiki/Regular_expression">Wikipedia - Regular
   *      expression</a>
   */
  static final String[] REGEX_LONGEST = new String[] {"+", "*"};

  /**
   * Shortest regex quantifier for <code>BlankSequence</code>,<code>BlankNullSequence</code>, <code>
   * Repeated</code>, <code>RepeatedNull</code>.
   *
   * @see <a href="https://en.wikipedia.org/wiki/Regular_expression">Wikipedia - Regular
   *      expression</a>
   */
  static String[] REGEX_SHORTEST = new String[] {"+?", "*?"};

  /**
   * Get the character range of <code>CharacterRange(from, to)</code>
   *
   * @param characterRangeAST the character range <code>CharacterRange(a,b)</code>
   * @return <code>from</code> at offset 0 and <code>to</code> at offset 1. <code>null</code> if the
   *         character range cannot be generated
   */
  static String[] characterRange(final IAST characterRangeAST) {

    if (!(characterRangeAST.arg1() instanceof IStringX)
        || !(characterRangeAST.arg2() instanceof IStringX)) {

      if (!(characterRangeAST.arg1().isInteger()) || !(characterRangeAST.arg2().isInteger())) {
        return null;
      }
      int from = characterRangeAST.arg1().toIntDefault();
      int to = characterRangeAST.arg2().toIntDefault();
      if (from < 0 || to < 0) {
        return null;
      }
      return new String[] {String.valueOf((char) from), String.valueOf((char) to)};
    }
    String str1 = characterRangeAST.arg1().toString();
    String str2 = characterRangeAST.arg2().toString();
    if (str1.length() != 1 || str2.length() != 1) {
      return null;
    }

    char from = str1.charAt(0);
    char to = str2.charAt(0);
    return new String[] {String.valueOf(from), String.valueOf(to)};
  }

  static String inputForm(final IExpr expression) {
    return inputForm(expression, ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS);
  }

  static String inputForm(final IExpr expression, boolean relaxedSyntax) {
    try {
      StringBuilder buf = new StringBuilder();
      OutputFormFactory off = OutputFormFactory.get(relaxedSyntax, false);
      off.setIgnoreNewLine(true);
      off.setInputForm(true);
      if (off.convert(buf, expression)) {
        return buf.toString();
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(S.InputForm, rex, EvalEngine.get());
    }
    return null;
  }

  static IExpr regexErrorHandling(final IAST ast, IllegalArgumentException iae, EvalEngine engine) {
    if (iae instanceof PatternSyntaxException) {
      PatternSyntaxException pse = (PatternSyntaxException) iae;
      // Regex expression `1` error message: `2`
      return Errors.printMessage(S.RegularExpression, "zzregex",
          F.list(F.$str(pse.getPattern()), F.$str(pse.getMessage())), engine);
    } else {
      return Errors.printMessage(S.RegularExpression, iae, engine);
    }
  }

  /**
   * Unicode version of predefined character classes and POSIX character classes are enabled in the
   * resulting regex Pattern object.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href=
   * "https://github.com/mathics/Mathics/blob/master/mathics/builtin/strings.py#L78">to_regex()
   * function</a>
   * <li><a href="https://en.wikipedia.org/wiki/Perl_Compatible_Regular_Expressions">Wikipedia -
   * Perl Compatible Regular Expression</a>
   * </ul>
   *
   * @param partOfRegex the expression which represents a regex 'piece'
   * @param abbreviatedPatterns if <code>true</code> allow 'abbreviated patterns" in strings (i.e.
   *        '\','*' and '@' operatore)
   * @param ignoreCase if <code>true</code> enables case-insensitive matching.
   * @param stringFunction the original string function, used in error messages
   * @param engine the evaluation engine
   */
  static java.util.regex.Pattern toRegexPattern(IExpr partOfRegex, boolean abbreviatedPatterns,
      boolean ignoreCase, IAST stringFunction, Map<ISymbol, String> namedRegexGroups,
      EvalEngine engine) {

    String regex = IStringX.toRegexString(partOfRegex, abbreviatedPatterns, stringFunction,
        IStringX.REGEX_LONGEST, namedRegexGroups, engine);
    if (regex != null) {
      java.util.regex.Pattern pattern;
      try {
        if (ignoreCase) {
          pattern = java.util.regex.Pattern.compile(regex,
              Pattern.UNICODE_CHARACTER_CLASS | Pattern.CASE_INSENSITIVE);
        } else {
          pattern = java.util.regex.Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
        }
        return pattern;
      } catch (IllegalArgumentException iae) {
        // for example java.util.regex.PatternSyntaxException
        IStringX.regexErrorHandling(stringFunction, iae, engine);
      }
    }

    return null;
  }

  /**
   * Convert the <code>StringExpression( ... )</code> to a java regex string.
   *
   * @param ast
   * @param stringExpression the <code>StringExpression( ... )</code> expression
   * @param abbreviatedPatterns if <code>true</code> allow 'abbreviated patterns" in strings (i.e.
   *        '\','*' and '@' operators)
   * @param shortestLongest either {@link IStringX#REGEX_LONGEST} or {@link IStringX#REGEX_SHORTEST}
   * @param groups
   * @param engine the evaluation engine
   * @return
   * @see <a href="https://en.wikipedia.org/wiki/Perl_Compatible_Regular_Expressions">Wikipedia -
   *      Perl Compatible Regular Expression</a>
   */
  static String toRegexString(IAST ast, IAST stringExpression, boolean abbreviatedPatterns,
      String[] shortestLongest, Map<ISymbol, String> groups, EvalEngine engine) {

    StringBuilder regex = new StringBuilder();
    for (int i = 1; i < stringExpression.size(); i++) {
      IExpr arg = stringExpression.get(i);
      String str = toRegexString(arg, abbreviatedPatterns, ast, shortestLongest, groups, engine);
      if (str == null) {
        return null;
      }
      regex.append(str);
    }

    return regex.toString();
  }

  /**
   * Convert a Symja expression which represents a 'piece of a regular expression' to a Java regular
   * expression string.
   *
   * @param partOfRegex the expression which represents a regex 'piece' which must be converted to a
   *        Java regex string
   * @param abbreviatedPatterns if <code>true</code> allow 'abbreviated patterns" in strings (i.e.
   *        '\','*' and '@' operators)
   * @param stringFunction the original string function, used in error messages
   * @param shortestLongest either {@link IStringX#REGEX_LONGEST} or {@link IStringX#REGEX_SHORTEST}
   * @param groups
   * @param engine the evaluation engine
   * @return
   * @see <a href="https://en.wikipedia.org/wiki/Perl_Compatible_Regular_Expressions">Wikipedia -
   *      Perl Compatible Regular Expression</a>
   */
  static String toRegexString(IExpr partOfRegex, boolean abbreviatedPatterns, IAST stringFunction,
      String[] shortestLongest, Map<ISymbol, String> groups, EvalEngine engine) {

    if (partOfRegex.isString()) {
      final String str = partOfRegex.toString();
      if (abbreviatedPatterns) {
        StringBuilder pieces = new StringBuilder();
        int beginIndex = 0;
        int endIndex = 0;
        final int len = str.length();
        while (endIndex < len) {
          char c = str.charAt(endIndex);
          if (c == '\\' && endIndex + 1 < len) {
            pieces.append(Pattern.quote(str.substring(beginIndex, endIndex)));
            pieces.append(Pattern.quote(str.substring(endIndex + 1, endIndex + 2)));
            endIndex += 2;
            beginIndex = endIndex;
          } else if (c == '*') {
            pieces.append(Pattern.quote(str.substring(beginIndex, endIndex)));
            pieces.append("(.*)");
            endIndex += 1;
            beginIndex = endIndex;
          } else if (c == '@') {
            pieces.append(Pattern.quote(str.substring(beginIndex, endIndex)));
            // one or more characters, excluding upper case letters
            pieces.append("([^A-Z]+)");
            endIndex += 1;
            beginIndex = endIndex;
          } else {
            endIndex += 1;
          }
        }
        pieces.append(Pattern.quote(str.substring(beginIndex, endIndex)));
        return pieces.toString();
      } else {
        return Pattern.quote(str);
      }
    } else if (partOfRegex.isAST(S.Characters, 2) && partOfRegex.first().isString()) {
      String str = partOfRegex.first().toString();
      return "[" + str + "]";
    } else if (partOfRegex.isAST(S.RegularExpression, 2) && partOfRegex.first().isString()) {
      return partOfRegex.first().toString();
    } else if (partOfRegex instanceof RepeatedPattern) {
      RepeatedPattern repeated = (RepeatedPattern) partOfRegex;
      IExpr expr = repeated.getRepeatedExpr();
      if (expr == null) {
        return null;
      }
      if (expr.isAST(S.Pattern, 3) && expr.first().isSymbol()) {
        final ISymbol symbol = (ISymbol) expr.first();
        String str = toRegexString(expr.second(), abbreviatedPatterns, stringFunction,
            shortestLongest, groups, engine);
        if (str != null) {
          final String groupName = symbol.toString();
          groups.put(symbol, groupName);
          if (repeated.isNullSequence()) {
            return "(?<" + groupName + ">(" + str + ")" + shortestLongest[IStringX.ASTERISK_Q]
                + ")";
          } else {
            return "(?<" + groupName + ">(" + str + ")" + shortestLongest[IStringX.PLUS_Q] + ")";
          }
        }
      } else {
        String str = toRegexString(expr, abbreviatedPatterns, stringFunction, shortestLongest,
            groups, engine);
        if (str != null) {
          if (repeated.isNullSequence()) {
            return "(" + str + ")" + shortestLongest[IStringX.ASTERISK_Q];
          } else {
            return "(" + str + ")" + shortestLongest[IStringX.PLUS_Q];
          }
        }
      }
    } else if (partOfRegex.isAST(S.StringExpression)) {
      IAST stringExpression = (IAST) partOfRegex;
      return IStringX.toRegexString(stringFunction, stringExpression, abbreviatedPatterns,
          shortestLongest, groups, engine);
    } else if (partOfRegex.isBlank()) {
      return "(.|\\n)";
    } else if (partOfRegex.isPattern()) {
      final IPattern pattern = (IPattern) partOfRegex;
      final ISymbol symbol = pattern.getSymbol();
      if (symbol != null && pattern.getHeadTest() == null) {
        // see github #221 - use Java regex - named capturing groups
        final String groupName = symbol.toString();
        groups.put(symbol, groupName);
        if (pattern instanceof PatternNested) {
          PatternNested pn = (PatternNested) pattern;
          IExpr subPattern = pn.getPatternExpr();
          String subPatternRegex = toRegexString(subPattern, abbreviatedPatterns, stringFunction,
              shortestLongest, groups, engine);
          return "(?<" + groupName + ">" + subPatternRegex + ")";
        }
        return "(?<" + groupName + ">(.|\\n))";
      }
    } else if (partOfRegex.isAST(S.Pattern, 3) && partOfRegex.first().isSymbol()) {
      final ISymbol symbol = (ISymbol) partOfRegex.first();
      String str = toRegexString(partOfRegex.second(), abbreviatedPatterns, stringFunction,
          shortestLongest, groups, engine);
      if (str != null) {
        final String groupName = symbol.toString();
        groups.put(symbol, groupName);
        return "(?<" + groupName + ">" + str + ")";
      }
    } else if (partOfRegex.isPatternSequence(false)) {
      AbstractPatternSequence ps = ((AbstractPatternSequence) partOfRegex);
      final ISymbol symbol = ps.getSymbol();
      final String str;
      if (ps.isNullSequence()) {
        // RepeatedNull
        str = "(.|\\n)" + shortestLongest[IStringX.ASTERISK_Q];
      } else {
        // Repeated
        str = "(.|\\n)" + shortestLongest[IStringX.PLUS_Q];
      }
      if (symbol == null) {
        return str;
      } else {
        final String groupName = symbol.toString();
        groups.put(symbol, groupName);
        return "(?<" + groupName + ">" + str + ")";
      }
    } else if (partOfRegex.isAST(S.CharacterRange, 3)) {
      String[] characterRange = IStringX.characterRange((IAST) partOfRegex);
      if (characterRange != null) {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        buf.append(Pattern.quote(characterRange[0]));
        buf.append("-");
        buf.append(Pattern.quote(characterRange[1]));
        buf.append("]");
        return buf.toString();
      }
    } else if (partOfRegex.isAlternatives()) {
      IAST alternatives = (IAST) partOfRegex;
      StringBuilder pieces = new StringBuilder();
      for (int i = 1; i < alternatives.size(); i++) {
        String str = toRegexString(alternatives.get(i), abbreviatedPatterns, stringFunction,
            shortestLongest, groups, engine);
        if (str == null) {
          // `1` currently not supported in `2`.
          Errors.printMessage(stringFunction.topHead(), "unsupported",
              F.list(alternatives.get(i), stringFunction.topHead()), engine);
          return null;
        }
        pieces.append(str);
        if (i < alternatives.argSize()) {
          pieces.append('|');
        }
      }
      return pieces.toString();
    } else if (partOfRegex.isExcept()) {
      IAST exceptions = (IAST) partOfRegex;
      StringBuilder pieces = new StringBuilder();
      for (int i = 1; i < exceptions.size(); i++) {
        String str = toRegexString(exceptions.get(i), abbreviatedPatterns, stringFunction,
            shortestLongest, groups, engine);
        if (str == null) {
          // `1` currently not supported in `2`.
          Errors.printMessage(stringFunction.topHead(), "unsupported",
              F.list(exceptions.get(i), stringFunction.topHead()), engine);
          return null;
        }
        pieces.append(str);
      }
      return "[^" + pieces.toString() + "]";
    } else if (partOfRegex.isAST(S.Shortest, 2)) {
      String str = toRegexString(partOfRegex.first(), abbreviatedPatterns, stringFunction,
          IStringX.REGEX_SHORTEST, groups, engine);
      return str;
    } else if (partOfRegex.isAST(S.Longest, 2)) {
      return toRegexString(partOfRegex.first(), abbreviatedPatterns, stringFunction,
          IStringX.REGEX_LONGEST, groups, engine);
    } else if (partOfRegex.isBuiltInSymbol()) {
      int ordinal = ((IBuiltInSymbol) partOfRegex).ordinal();
      switch (ordinal) {
        case ID.NumberString:
          // better suitable for StringSplit?
          return "[0-9]{1,13}(\\.[0-9]+)?";
        // mathics:
        // return "[-|+]?(\\d+(\\.\\d*)?|\\.\\d+)?";
        case ID.Whitespace:
          return "(?u)\\s+";
        case ID.DigitCharacter:
          return "\\d";
        case ID.WhitespaceCharacter:
          return "(?u)\\s";
        case ID.WordCharacter:
          return "(?u)[^\\W_]";
        case ID.StartOfLine:
          return "\\R";
        case ID.EndOfLine:
          return "$";
        case ID.StartOfString:
          return "\\A";
        case ID.EndOfString:
          return "\\Z";
        case ID.WordBoundary:
          return "\\b";
        case ID.LetterCharacter:
          return "(?u)[^\\W_0-9]";
        case ID.HexidecimalCharacter:
          return "[0-9a-fA-F]";
        default:
          // `1` currently not supported in `2`.
          Errors.printMessage(stringFunction.topHead(), "unsupported",
              F.list(partOfRegex, stringFunction.topHead()), engine);
          return null;
      }
    }

    // `1` currently not supported in `2`.
    Errors.printMessage(stringFunction.topHead(), "unsupported",
        F.list(partOfRegex, stringFunction.topHead()), engine);
    return null;
  }

  public char charAt(final int index);

  /**
   * Test if this string equals the given character sequence.
   *
   * @param cs
   * @return
   */
  public boolean contentEquals(final CharSequence cs);

  @Override
  default IExpr eval(EvalEngine engine) {
    return evaluate(engine).orElse(this);
  }

  /**
   * Get the mime tpe of this string.
   *
   * @return
   * @see IStringX#TEXT_PLAIN
   * @see IStringX#TEXT_LATEX
   * @see IStringX#TEXT_MATHML
   * @see IStringX#TEXT_HTML
   */
  public short getMimeType();

  /**
   * @param ch
   * @return
   * @see String#indexOf(ch)
   */
  public int indexOf(final int ch);

  /**
   * @param ch
   * @param fromIndex
   * @return
   * @see String#indexOf(int, int)
   */
  public int indexOf(final int ch, final int fromIndex);

  /**
   * @param str
   * @return
   * @see String#indexOf(String)
   */
  public int indexOf(final String str);

  /**
   * @param str
   * @param fromIndex
   * @return
   * @see String#indexOf(String, int)
   */
  public int indexOf(final String str, final int fromIndex);

  /**
   * @param ch
   * @return
   * @see String#lastIndexOf(int)
   */
  public int lastIndexOf(final int ch);

  /**
   * @param ch
   * @param fromIndex
   * @return
   * @see String#lastIndexOf(int, int)
   */
  public int lastIndexOf(final int ch, final int fromIndex);

  /**
   * @param str
   * @return
   * @see String#lastIndexOf(String)
   */
  public int lastIndexOf(final String str);

  /**
   * @param str
   * @param fromIndex
   * @return
   * @see String#lastIndexOf(String, int)
   */
  public int lastIndexOf(final String str, final int fromIndex);

  /**
   * @param beginIndex
   * @return
   * @see String#substring(int)
   */
  public String substring(final int beginIndex);

  /**
   * @param beginIndex
   * @param endIndex
   * @return
   * @see String#substring(int, int)
   */
  public String substring(final int beginIndex, final int endIndex);

  public String toLowerCase();

  public String toUpperCase();

  @Override
  default int uniformFlags() {
    return UniformFlags.STRING | UniformFlags.ATOM;
  }
}
