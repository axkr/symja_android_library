package org.matheclipse.core.builtin;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Characters;

/**
 * String functions: <code>StringPartition</code>, <code>CharacterName</code>,
 * <code>CharacterNormalize</code>, <code>InsertLinebreaks</code>, <code>Snippet</code>,
 * <code>DamerauLevenshteinDistance</code> and <code>LongestCommonSequencePositions</code>, and the
 * inert heads <code>StringSkeleton</code> and <code>DelimitedSequence</code>.
 */
public final class StringUtilityFunctions {

  /** The normalization forms of <code>CharacterNormalize</code>. */
  private static final List<String> NORMALIZATION_FORMS =
      List.of("NFC", "NFD", "NFKC", "NFKCCasefold", "NFKD");

  /** The default line width of <code>InsertLinebreaks</code>. */
  private static final int DEFAULT_LINE_WIDTH = 78;

  /** The number of characters <code>Snippet</code> keeps of a line. */
  private static final int SNIPPET_LINE_LENGTH = 80;

  private static class Initializer {

    private static void init() {
      S.StringPartition.setEvaluator(new StringPartition());
      S.CharacterName.setEvaluator(new CharacterName());
      S.CharacterNormalize.setEvaluator(new CharacterNormalize());
      S.InsertLinebreaks.setEvaluator(new InsertLinebreaks());
      S.Snippet.setEvaluator(new Snippet());
      S.DamerauLevenshteinDistance.setEvaluator(new DamerauLevenshteinDistance());
      S.LongestCommonSequencePositions.setEvaluator(new LongestCommonSequencePositions());
      S.StringSkeleton.setEvaluator(new Inert());
      S.DelimitedSequence.setEvaluator(new Inert());
    }
  }

  /** A head without evaluation of its own. */
  private static final class Inert extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>StringPartition(s, n)</code>, <code>StringPartition(s, n, d)</code> - the substrings of
   * length <code>n</code> of <code>s</code> with offset <code>d</code> (default <code>n</code>).
   */
  private static final class StringPartition extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      int n = ast.arg2().toIntDefault();
      if (n <= 0) {
        // Positive machine-sized integer expected at position `2` in `1`.
        return Errors.printMessage(S.StringPartition, "intpm", F.list(ast, F.C2), engine);
      }
      int d = n;
      if (ast.isAST3()) {
        d = ast.arg3().toIntDefault();
        if (d <= 0) {
          // Positive integer expected at position `2` in `1`.
          return Errors.printMessage(S.StringPartition, "intp", F.list(ast, F.C3), engine);
        }
      }
      String str = ast.arg1().toString();
      int[] codePoints = str.codePoints().toArray();
      IASTAppendable result = F.ListAlloc();
      for (int i = 0; i + n <= codePoints.length; i += d) {
        result.append(F.stringx(new String(codePoints, i, n)));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>CharacterName(c)</code> - the name of the character <code>c</code>, or of the character
   * with the code <code>c</code>; a list of names for a string of several characters.
   */
  private static final class CharacterName extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        int code = arg1.toIntDefault();
        if (code < 0 || !Character.isValidCodePoint(code)) {
          return F.NIL;
        }
        String name = name(code);
        return name == null ? F.NIL : F.stringx(name);
      }
      if (arg1.isString()) {
        int[] codePoints = arg1.toString().codePoints().toArray();
        if (codePoints.length == 0) {
          return F.NIL;
        }
        IASTAppendable names = F.ListAlloc(codePoints.length);
        for (int code : codePoints) {
          String name = name(code);
          if (name == null) {
            return F.NIL;
          }
          names.append(F.stringx(name));
        }
        return codePoints.length == 1 ? names.arg1() : names;
      }
      return F.NIL;
    }

    private static final String[] DIGITS =
        {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};

    private static final Map<Character, String> RAW = new HashMap<Character, String>();

    static {
      String raw = "\tRawTab RawSpace!RawExclamation\"RawDoubleQuote#RawNumberSign$RawDollar"
          + "%RawPercent&RawAmpersand'RawQuote(RawLeftParenthesis)RawRightParenthesis*RawStar"
          + "+RawPlus,RawComma-RawDash.RawDot/RawSlash:RawColon;RawSemicolon<RawLess=RawEqual"
          + ">RawGreater?RawQuestion@RawAt[RawLeftBracket\\RawBackslash]RawRightBracket^RawWedge"
          + "_RawUnderscore`RawBackquote{RawLeftBrace|RawVerticalBar}RawRightBrace~RawTilde";
      int i = 0;
      while (i < raw.length()) {
        char c = raw.charAt(i);
        int end = i + 1;
        while (end < raw.length() && Character.isLetter(raw.charAt(end))) {
          end++;
        }
        RAW.put(c, raw.substring(i + 1, end));
        i = end;
      }
    }

    /** The name of a character, or <code>null</code>. */
    private static String name(int code) {
      if (code < 128) {
        char c = (char) code;
        if (c >= 'A' && c <= 'Z') {
          return "LatinCapitalLetter" + c;
        }
        if (c >= 'a' && c <= 'z') {
          return "LatinSmallLetter" + Character.toUpperCase(c);
        }
        if (c >= '0' && c <= '9') {
          return "Digit" + DIGITS[c - '0'];
        }
        return RAW.get(c);
      }
      if (code > Character.MAX_VALUE) {
        return null;
      }
      return Characters.unicodeName((char) code);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  /**
   * <code>CharacterNormalize(s, form)</code> - the Unicode normalization <code>"NFC"</code>,
   * <code>"NFD"</code>, <code>"NFKC"</code>, <code>"NFKD"</code> or <code>"NFKCCasefold"</code> of
   * the string <code>s</code> or of a list of strings.
   */
  private static final class CharacterNormalize extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr text = ast.arg1();
      boolean list = text.isList() && ((IAST) text).forAll(x -> x.isString());
      if (!text.isString() && !list) {
        return F.NIL;
      }
      String form = ast.arg2().isString() ? ast.arg2().toString() : "";
      if (!NORMALIZATION_FORMS.contains(form)) {
        // "`1`" is not an available element. Possible elements include `2`.
        return Errors.printMessage(S.CharacterNormalize, "elmntavl",
            F.list(ast.arg2(), F.stringx(String.join(", ", NORMALIZATION_FORMS))), engine);
      }
      if (list) {
        return ((IAST) text).map(x -> F.stringx(normalize(x.toString(), form)));
      }
      return F.stringx(normalize(text.toString(), form));
    }

    private static String normalize(String str, String form) {
      switch (form) {
        case "NFC":
          return Normalizer.normalize(str, Normalizer.Form.NFC);
        case "NFD":
          return Normalizer.normalize(str, Normalizer.Form.NFD);
        case "NFKC":
          return Normalizer.normalize(str, Normalizer.Form.NFKC);
        case "NFKD":
          return Normalizer.normalize(str, Normalizer.Form.NFKD);
        default:
          // NFKC of the case folded string, normalized again
          String folded = Normalizer.normalize(str, Normalizer.Form.NFKC).toUpperCase(Locale.ROOT)
              .toLowerCase(Locale.ROOT);
          return Normalizer.normalize(folded, Normalizer.Form.NFKC);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>InsertLinebreaks(s)</code>, <code>InsertLinebreaks(s, n)</code> - break the string into
   * lines of at most <code>n</code> characters (default <code>78</code>) at the spaces between
   * words; a word longer than <code>n</code> is split.
   */
  private static final class InsertLinebreaks extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      int width = DEFAULT_LINE_WIDTH;
      if (ast.isAST2()) {
        width = ast.arg2().toIntDefault();
        if (width <= 0) {
          // Positive integer expected at position `2` in `1`.
          return Errors.printMessage(S.InsertLinebreaks, "intp", F.list(ast, F.C2), engine);
        }
      }
      List<String> lines = new ArrayList<String>();
      StringBuilder line = new StringBuilder();
      boolean lineHasWord = false;
      for (String word : ast.arg1().toString().split(" ", -1)) {
        if (lineHasWord) {
          if (line.length() + 1 + word.length() <= width) {
            line.append(' ').append(word);
            continue;
          }
          lines.add(line.toString());
          line.setLength(0);
        }
        String rest = word;
        while (rest.length() > width) {
          lines.add(rest.substring(0, width));
          rest = rest.substring(width);
        }
        line.append(rest);
        lineHasWord = true;
      }
      lines.add(line.toString());
      return F.stringx(String.join("\n", lines));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>Snippet(s)</code>, <code>Snippet(s, n)</code>, <code>Snippet(s, m;;n)</code> - the first
   * line, the first <code>n</code> lines (the last ones for a negative <code>n</code>) or the lines
   * <code>m</code> to <code>n</code> of the string, each cut to 80 characters.
   */
  private static final class Snippet extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      String[] lines = ast.arg1().toString().split("\n", -1);
      final int count = lines.length;
      int start = 0;
      int end = Math.min(1, count);
      if (ast.isAST2()) {
        IExpr spec = ast.arg2();
        if (spec.isInteger()) {
          int n = spec.toIntDefault();
          if (n == Integer.MIN_VALUE || n == 0) {
            return F.NIL;
          }
          if (n > 0) {
            end = Math.min(n, count);
          } else {
            start = Math.max(0, count + n);
            end = count;
          }
        } else if (spec.isAST(S.Span, 3)) {
          int from = spanIndex(spec.first(), count, true);
          int to = spanIndex(spec.second(), count, false);
          if (from < 0 || to < 0) {
            return F.NIL;
          }
          start = from;
          end = Math.max(from, to);
        } else {
          return F.NIL;
        }
      }
      StringBuilder buf = new StringBuilder();
      for (int i = start; i < end; i++) {
        if (i > start) {
          buf.append('\n');
        }
        String line = lines[i];
        buf.append(line.length() > SNIPPET_LINE_LENGTH ? line.substring(0, SNIPPET_LINE_LENGTH)
            : line);
      }
      return F.stringx(buf.toString());
    }

    /**
     * The 0-based start (<code>first</code>) or the exclusive end of a span bound, clamped to the
     * lines; <code>-1</code> for a bound which isn't understood.
     */
    private static int spanIndex(IExpr bound, int count, boolean first) {
      if (bound == S.All) {
        return first ? 0 : count;
      }
      int i = bound.toIntDefault();
      if (i == Integer.MIN_VALUE || i == 0) {
        return -1;
      }
      int index;
      if (first) {
        index = i > 0 ? i - 1 : count + i;
      } else {
        index = i > 0 ? i : count + i + 1;
      }
      return Math.max(0, Math.min(count, index));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * The elements of a string (its characters) or of a list, for the sequence distances; a string is
   * lower cased if <code>ignoreCase</code>.
   */
  private static IExpr[] tokens(IExpr expr, boolean ignoreCase) {
    if (expr.isString()) {
      String str = expr.toString();
      if (ignoreCase) {
        str = str.toLowerCase(Locale.ROOT);
      }
      return str.codePoints().mapToObj(c -> F.stringx(new String(Character.toChars(c))))
          .toArray(IExpr[]::new);
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      IExpr[] tokens = new IExpr[list.argSize()];
      for (int i = 0; i < tokens.length; i++) {
        tokens[i] = list.get(i + 1);
      }
      return tokens;
    }
    return null;
  }

  /**
   * <code>DamerauLevenshteinDistance(u, v)</code> - the smallest number of insertions, deletions,
   * substitutions and transpositions of two neighbors which turn the string or list <code>u</code>
   * into <code>v</code>. The option <code>IgnoreCase -&gt; True</code> compares strings in lower
   * case.
   */
  private static final class DamerauLevenshteinDistance extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      boolean ignoreCase = false;
      if (ast.isAST3()) {
        IExpr option = ast.arg3();
        if (!option.isRuleAST() || option.first() != S.IgnoreCase) {
          return F.NIL;
        }
        ignoreCase = option.second().isTrue();
      }
      if (ast.arg1().isString() != ast.arg2().isString()) {
        return F.NIL;
      }
      IExpr[] a = tokens(ast.arg1(), ignoreCase);
      IExpr[] b = tokens(ast.arg2(), ignoreCase);
      if (a == null || b == null) {
        return F.NIL;
      }
      return F.ZZ(distance(a, b, engine));
    }

    /**
     * The unrestricted Damerau-Levenshtein distance: a transposition may be combined with other
     * edits of the same characters.
     */
    private static int distance(IExpr[] a, IExpr[] b, EvalEngine engine) {
      final int n = a.length;
      final int m = b.length;
      final int infinity = n + m;
      // d[i + 1][j + 1] is the distance of the first i elements of a and the first j of b
      int[][] d = new int[n + 2][m + 2];
      d[0][0] = infinity;
      for (int i = 0; i <= n; i++) {
        d[i + 1][0] = infinity;
        d[i + 1][1] = i;
      }
      for (int j = 0; j <= m; j++) {
        d[0][j + 1] = infinity;
        d[1][j + 1] = j;
      }
      // the last row in which each element of a was seen
      Map<IExpr, Integer> lastRow = new HashMap<IExpr, Integer>();
      for (int i = 1; i <= n; i++) {
        engine.checkTimeLimit();
        int lastMatchColumn = 0;
        for (int j = 1; j <= m; j++) {
          int k = lastRow.getOrDefault(b[j - 1], 0);
          int l = lastMatchColumn;
          int cost = 1;
          if (a[i - 1].equals(b[j - 1])) {
            cost = 0;
            lastMatchColumn = j;
          }
          int value = Math.min(d[i][j] + cost, Math.min(d[i + 1][j] + 1, d[i][j + 1] + 1));
          value = Math.min(value, d[k][l] + (i - k - 1) + 1 + (j - l - 1));
          d[i + 1][j + 1] = value;
        }
        lastRow.put(a[i - 1], i);
      }
      return d[n + 1][m + 1];
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>LongestCommonSequencePositions(u, v)</code> - the positions of a longest common, not
   * necessarily contiguous, sequence of the strings or lists <code>u</code> and <code>v</code>:
   * <code>{spans of u, spans of v}</code>, where each span <code>{start, end}</code> is a run of
   * consecutive positions. <code>{}</code> if there is no common element.
   */
  private static final class LongestCommonSequencePositions extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString() != ast.arg2().isString()) {
        return F.NIL;
      }
      IExpr[] a = tokens(ast.arg1(), false);
      IExpr[] b = tokens(ast.arg2(), false);
      if (a == null || b == null) {
        return F.NIL;
      }
      final int m = a.length;
      final int n = b.length;
      int[][] length = new int[m + 1][n + 1];
      for (int i = 1; i <= m; i++) {
        engine.checkTimeLimit();
        for (int j = 1; j <= n; j++) {
          length[i][j] = a[i - 1].equals(b[j - 1]) ? length[i - 1][j - 1] + 1
              : Math.max(length[i - 1][j], length[i][j - 1]);
        }
      }
      int common = length[m][n];
      if (common == 0) {
        return F.CEmptyList;
      }
      int[] positionsA = new int[common];
      int[] positionsB = new int[common];
      int i = m;
      int j = n;
      int k = common;
      while (i > 0 && j > 0) {
        if (a[i - 1].equals(b[j - 1])) {
          k--;
          positionsA[k] = i;
          positionsB[k] = j;
          i--;
          j--;
        } else if (length[i - 1][j] >= length[i][j - 1]) {
          i--;
        } else {
          j--;
        }
      }
      return F.list(spans(positionsA), spans(positionsB));
    }

    /** The runs of consecutive positions as <code>{start, end}</code> pairs. */
    private static IAST spans(int[] positions) {
      IASTAppendable result = F.ListAlloc();
      int start = positions[0];
      int previous = positions[0];
      for (int k = 1; k < positions.length; k++) {
        if (positions[k] != previous + 1) {
          result.append(F.list(F.ZZ(start), F.ZZ(previous)));
          start = positions[k];
        }
        previous = positions[k];
      }
      result.append(F.list(F.ZZ(start), F.ZZ(previous)));
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private StringUtilityFunctions() {}
}
