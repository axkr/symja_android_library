package org.matheclipse.core.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits Wolfram Language source into tokens that tile it exactly.
 *
 * <p>
 * This is what <code>CodeParser`</code> is for: reading source as source. Nothing here evaluates or
 * even parses - every character of the input belongs to exactly one token, and each token knows
 * where it starts and ends, so a caller can cut the text up again and put it back together. A
 * template engine that has to split a file at the newlines between its top-level expressions needs
 * precisely that, and cannot get it from an evaluator.
 *
 * <p>
 * The token kinds are coarser than the Wolfram Language's own: everything that is not a comment, a
 * string, a number, a symbol, a bracket or whitespace is one run of operator characters. What is
 * exact is the extent of each token and the bracket nesting, which is what tells a top-level
 * newline from one inside an expression.
 */
public final class CodeTokenizer {

  /** One token, with the part of the source it covers. */
  public static final class Token {
    private final String kind;
    private final String text;
    private final int start;
    private final int end;
    private final int depth;

    Token(String kind, String text, int start, int end, int depth) {
      this.kind = kind;
      this.text = text;
      this.start = start;
      this.end = end;
      this.depth = depth;
    }

    /** The name this token has in the <code>Token`</code> context, such as <code>Newline</code>. */
    public String kind() {
      return kind;
    }

    public String text() {
      return text;
    }

    /** One-based index of the first character, the way the Wolfram Language counts them. */
    public int start() {
      return start;
    }

    /** One-based index of the last character, inclusive. */
    public int end() {
      return end;
    }

    /** How many brackets are open around this token. */
    public int depth() {
      return depth;
    }

    /** Does this token open a bracketed group? */
    public boolean isOpen() {
      return OPEN_KINDS.contains(kind);
    }

    /** Does this token close one? */
    public boolean isClose() {
      return CLOSE_KINDS.contains(kind);
    }

    @Override
    public String toString() {
      return kind + "[" + text + "]@" + start + ".." + end;
    }
  }

  private static final java.util.Set<String> OPEN_KINDS = java.util.Set.of("OpenSquare",
      "OpenCurly", "OpenParen", "LessBar", "ColonColonOpenSquare");

  private static final java.util.Set<String> CLOSE_KINDS =
      java.util.Set.of("CloseSquare", "CloseCurly", "CloseParen", "BarGreater");

  /**
   * Operators which can stand at the end of a finished expression. Everything else that is an
   * operator is still waiting for what comes after it.
   */
  private static final java.util.Set<String> COMPLETING_OPERATORS =
      java.util.Set.of(";", "&", "++", "--", "!", "!!", "'", "_", "__", "___", "..", "...");

  /**
   * Would a line break after this token leave the expression unfinished?
   *
   * <p>
   * An operator waiting for its right-hand side does; a name, a number, a closing bracket or one
   * of the operators which finish an expression does not.
   */
  private static boolean continues(Token lastSignificant) {
    if (lastSignificant == null) {
      return false;
    }
    String kind = lastSignificant.kind();
    if (kind.equals("Comma")) {
      return true;
    }
    if (!kind.equals("Operator")) {
      return false;
    }
    return !COMPLETING_OPERATORS.contains(lastSignificant.text());
  }

  private CodeTokenizer() {}

  /** The tokens of <code>source</code>, in order, covering every character of it. */
  public static List<Token> tokenize(String source) {
    List<Token> tokens = new ArrayList<Token>();
    int position = 0;
    int depth = 0;
    // the last token which says anything about whether an expression is finished: whitespace,
    // comments and earlier line breaks say nothing
    Token lastSignificant = null;
    final int length = source.length();
    while (position < length) {
      if (!tokens.isEmpty()) {
        Token previous = tokens.get(tokens.size() - 1);
        String kind = previous.kind();
        if (!kind.equals("Whitespace") && !kind.equals("Comment") && !kind.equals("Newline")
            && !kind.equals("InternalNewline")) {
          lastSignificant = previous;
        }
      }
      char ch = source.charAt(position);
      int begin = position;

      if (ch == '\r' || ch == '\n') {
        if (ch == '\r' && position + 1 < length && source.charAt(position + 1) == '\n') {
          position++;
        }
        position++;
        // A newline ends an expression only where one can end. Inside brackets, or after an
        // operator still waiting for its right-hand side, the line break is part of the
        // expression rather than the end of it - `f[x_] :=` on one line and its body on the next
        // is one definition, not a definition of nothing followed by a loose body.
        String kind = depth > 0 || continues(lastSignificant) ? "InternalNewline" : "Newline";
        tokens.add(new Token(kind, source.substring(begin, position), begin + 1, position, depth));
        continue;
      }
      if (ch == ' ' || ch == '\t' || ch == '\f') {
        while (position < length && isSpace(source.charAt(position))) {
          position++;
        }
        tokens.add(new Token("Whitespace", source.substring(begin, position), begin + 1, position,
            depth));
        continue;
      }
      if (ch == '(' && position + 1 < length && source.charAt(position + 1) == '*') {
        position = skipComment(source, position);
        tokens.add(new Token("Comment", source.substring(begin, position), begin + 1, position,
            depth));
        continue;
      }
      if (ch == '"') {
        position = skipString(source, position);
        tokens.add(
            new Token("String", source.substring(begin, position), begin + 1, position, depth));
        continue;
      }
      if (Character.isDigit(ch)) {
        position = skipNumber(source, position);
        String text = source.substring(begin, position);
        String kind = text.indexOf('.') >= 0 ? "Real" : "Integer";
        tokens.add(new Token(kind, text, begin + 1, position, depth));
        continue;
      }
      if (isSymbolStart(ch)) {
        position = skipSymbol(source, position);
        tokens
            .add(new Token("Symbol", source.substring(begin, position), begin + 1, position, depth));
        continue;
      }

      String bracket = bracketKind(source, position);
      if (bracket != null) {
        int width = bracket.equals("LessBar") || bracket.equals("BarGreater") ? 2 : 1;
        position += width;
        Token token =
            new Token(bracket, source.substring(begin, position), begin + 1, position, depth);
        if (token.isOpen()) {
          depth++;
        } else if (token.isClose() && depth > 0) {
          depth--;
          // the closing bracket belongs to the level it closes
          token = new Token(bracket, token.text(), token.start(), token.end(), depth);
        }
        tokens.add(token);
        continue;
      }

      if (ch == ',') {
        position++;
        tokens.add(new Token("Comma", ",", begin + 1, position, depth));
        continue;
      }

      // one run of operator characters, or a single character that is none of the above
      position++;
      while (position < length && isOperatorChar(source.charAt(position))
          && bracketKind(source, position) == null && source.charAt(position) != ','
          && !(source.charAt(position) == '(' && position + 1 < length
              && source.charAt(position + 1) == '*')) {
        position++;
      }
      tokens
          .add(new Token("Operator", source.substring(begin, position), begin + 1, position, depth));
    }
    return tokens;
  }

  private static boolean isSpace(char ch) {
    return ch == ' ' || ch == '\t' || ch == '\f';
  }

  private static boolean isSymbolStart(char ch) {
    return Character.isLetter(ch) || ch == '$' || ch == '`';
  }

  private static boolean isSymbolPart(char ch) {
    return Character.isLetterOrDigit(ch) || ch == '$' || ch == '`';
  }

  private static boolean isOperatorChar(char ch) {
    return !Character.isLetterOrDigit(ch) && !isSpace(ch) && ch != '\n' && ch != '\r' && ch != '"'
        && ch != '$' && ch != '`';
  }

  /** The name of the bracket at <code>position</code>, or <code>null</code> if there is none. */
  private static String bracketKind(String source, int position) {
    char ch = source.charAt(position);
    switch (ch) {
      case '[':
        return "OpenSquare";
      case ']':
        return "CloseSquare";
      case '{':
        return "OpenCurly";
      case '}':
        return "CloseCurly";
      case '(':
        return "OpenParen";
      case ')':
        return "CloseParen";
      case '<':
        return position + 1 < source.length() && source.charAt(position + 1) == '|' ? "LessBar"
            : null;
      case '|':
        return position + 1 < source.length() && source.charAt(position + 1) == '>' ? "BarGreater"
            : null;
      default:
        return null;
    }
  }

  /** Past the end of a comment, which may hold comments of its own. */
  private static int skipComment(String source, int position) {
    int level = 0;
    final int length = source.length();
    while (position < length) {
      if (position + 1 < length && source.charAt(position) == '(' && source.charAt(position + 1) == '*') {
        level++;
        position += 2;
        continue;
      }
      if (position + 1 < length && source.charAt(position) == '*' && source.charAt(position + 1) == ')') {
        level--;
        position += 2;
        if (level == 0) {
          return position;
        }
        continue;
      }
      position++;
    }
    return length;
  }

  /** Past the end of a string, whose closing quote a backslash may hide. */
  private static int skipString(String source, int position) {
    final int length = source.length();
    position++;
    while (position < length) {
      char ch = source.charAt(position);
      if (ch == '\\') {
        position += 2;
        continue;
      }
      position++;
      if (ch == '"') {
        return position;
      }
    }
    return length;
  }

  /**
   * Past the end of a number, including the forms that are not written with digits alone:
   * <code>2^^101</code>, <code>1.5`20</code>, <code>2.5*^3</code>.
   */
  private static int skipNumber(String source, int position) {
    final int length = source.length();
    while (position < length && Character.isDigit(source.charAt(position))) {
      position++;
    }
    if (position + 1 < length && source.charAt(position) == '^'
        && source.charAt(position + 1) == '^') {
      position += 2;
      while (position < length && Character.isLetterOrDigit(source.charAt(position))) {
        position++;
      }
      return position;
    }
    if (position < length && source.charAt(position) == '.' //
        && !(position + 1 < length && source.charAt(position + 1) == '.')) {
      position++;
      while (position < length && Character.isDigit(source.charAt(position))) {
        position++;
      }
    }
    while (position < length && source.charAt(position) == '`') {
      position++;
      while (position < length && source.charAt(position) == '`') {
        position++;
      }
      while (position < length
          && (Character.isDigit(source.charAt(position)) || source.charAt(position) == '.')) {
        position++;
      }
    }
    if (position + 1 < length && source.charAt(position) == '*'
        && source.charAt(position + 1) == '^') {
      position += 2;
      if (position < length && (source.charAt(position) == '-' || source.charAt(position) == '+')) {
        position++;
      }
      while (position < length && Character.isDigit(source.charAt(position))) {
        position++;
      }
    }
    return position;
  }

  private static int skipSymbol(String source, int position) {
    final int length = source.length();
    while (position < length && isSymbolPart(source.charAt(position))) {
      position++;
    }
    return position;
  }
}
