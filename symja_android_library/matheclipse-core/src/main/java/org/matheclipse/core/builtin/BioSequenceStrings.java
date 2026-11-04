package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.RepeatedPattern;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The string functions applied to a <code>BioSequence[type, letters]</code>.
 *
 * <p>
 * <code>StringTake</code>, <code>StringReplace</code>, <code>StringCount</code> and their siblings
 * accept a biomolecular sequence in place of a string: they work on its letters, and a string in
 * the result becomes a sequence of the same type again. In a string pattern a degenerate nucleotide
 * letter - <code>N</code>, <code>Y</code>, <code>R</code>, ... - matches every letter it stands
 * for, unless it is wrapped in <code>Verbatim</code>. A circular sequence wraps around from its
 * last letter to its first, for the matches of a pattern and for a range <code>{i, j}</code> with
 * <code>j &lt; i</code>.
 *
 * <p>
 * The sequence itself is an object of the <code>matheclipse-bio</code> module; here it is only seen
 * through its normal form <code>BioSequence["type", "letters"]</code>, and a result is built by
 * evaluating that form again.
 */
final class BioSequenceStrings {

  /** The nucleotide letters and the bases each one stands for. */
  private static final String[][] DNA = { //
      {"A", "A"}, {"C", "C"}, {"G", "G"}, {"T", "T"}, //
      {"R", "AG"}, {"Y", "CT"}, {"S", "CG"}, {"W", "AT"}, {"K", "GT"}, {"M", "AC"}, //
      {"B", "CGT"}, {"D", "AGT"}, {"H", "ACT"}, {"V", "ACG"}, {"N", "ACGT"}};

  private static final String[][] RNA = { //
      {"A", "A"}, {"C", "C"}, {"G", "G"}, {"U", "U"}, //
      {"R", "AG"}, {"Y", "CU"}, {"S", "CG"}, {"W", "AU"}, {"K", "GU"}, {"M", "AC"}, //
      {"B", "CGU"}, {"D", "AGU"}, {"H", "ACU"}, {"V", "ACG"}, {"N", "ACGU"}};

  /** The residue letters; the wildcard <code>X</code> stands for every one of them. */
  private static final String[][] PEPTIDE = peptideTable("ACDEFGHIKLMNPQRSTVWYUOBZJ");

  private static String[][] peptideTable(String residues) {
    String[][] table = new String[residues.length() + 1][];
    for (int i = 0; i < residues.length(); i++) {
      String letter = residues.substring(i, i + 1);
      table[i] = new String[] {letter, letter};
    }
    table[residues.length()] = new String[] {"X", residues + "X"};
    return table;
  }

  /** The type and the letters of a sequence. */
  private static final class Seq {
    final String type;

    final String letters;

    /** The letters and the letters each one stands for. */
    final String[][] table;

    final boolean circular;

    private Seq(String type, String letters, String[][] table, boolean circular) {
      this.type = type;
      this.letters = letters;
      this.table = table;
      this.circular = circular;
    }

    /** The sequence <code>expr</code> stands for, or <code>null</code>. */
    static Seq of(IExpr expr) {
      if (expr.head() != S.BioSequence) {
        return null;
      }
      IExpr normal = expr.isAST() ? expr : expr.normal(false);
      if (!normal.isAST() || normal.argSize() < 2 || normal.argSize() > 3
          || !normal.first().isString() || !normal.second().isString()) {
        return null;
      }
      String type = normal.first().toString();
      boolean circular = type.startsWith("Circular");
      String linear = circular ? type.substring("Circular".length()) : type;
      String[][] table;
      if (linear.equals("DNA")) {
        table = DNA;
      } else if (linear.equals("RNA")) {
        table = RNA;
      } else if (linear.equals("Peptide") && !circular) {
        table = PEPTIDE;
      } else {
        return null;
      }
      return new Seq(type, normal.second().toString(), table, circular);
    }

    /** A sequence of this type with other letters. */
    IExpr with(String newLetters, EvalEngine engine) {
      return engine.evaluate(F.binaryAST2(S.BioSequence, F.stringx(type), F.stringx(newLetters)));
    }

    /** Every string of a (nested) list result as a sequence of this type. */
    IExpr rewrap(IExpr result, EvalEngine engine) {
      if (result.isString()) {
        return with(result.toString(), engine);
      }
      if (result.isList()) {
        return ((IAST) result).map(x -> rewrap(x, engine));
      }
      return result;
    }

    /** The letters from position <code>from</code> to <code>to</code>, wrapping past the end. */
    String slice(int from, int to) {
      StringBuilder buf = new StringBuilder();
      int n = letters.length();
      for (int k = from; k <= to; k++) {
        buf.append(letters.charAt((k - 1) % n));
      }
      return buf.toString();
    }
  }

  private BioSequenceStrings() {}

  /** Whether the second argument of the string function is a string pattern. */
  private static boolean takesPattern(IExpr head) {
    return head == S.StringReplace || head == S.StringMatchQ || head == S.StringSplit
        || head == S.StringCases || head == S.StringFreeQ || head == S.StringPosition
        || head == S.StringCount || head == S.StringContainsQ || head == S.StringDelete
        || head == S.StringStartsQ || head == S.StringEndsQ;
  }

  /** Whether the string function takes positions, counts or strings, but no pattern. */
  private static boolean takesPlain(IExpr head, int argSize) {
    if (head == S.StringLength || head == S.StringReverse) {
      return argSize == 1;
    }
    if (head == S.StringTake || head == S.StringDrop || head == S.StringTakeDrop
        || head == S.StringRotateLeft || head == S.StringRotateRight || head == S.StringPart) {
      return argSize == 2;
    }
    if (head == S.StringRepeat || head == S.StringPadLeft || head == S.StringPadRight) {
      return argSize == 2 || argSize == 3;
    }
    if (head == S.StringInsert || head == S.StringReplacePart) {
      return argSize == 3;
    }
    return false;
  }

  /**
   * The string function <code>ast</code> applied to the sequence <code>arg1</code>, its first
   * argument evaluated.
   *
   * @return {@link F#NIL} if <code>arg1</code> is no sequence or the call is not supported
   */
  static IExpr evaluate(IAST ast, IExpr arg1, EvalEngine engine) {
    Seq seq = Seq.of(arg1);
    if (seq == null) {
      return F.NIL;
    }
    IExpr head = ast.head();
    int argSize = ast.argSize();
    if (seq.circular) {
      return circular(ast, seq, engine);
    }
    IASTMutable call = ast.setAtCopy(1, F.stringx(seq.letters));
    if (takesPattern(head) && argSize >= 2) {
      call.set(2, translate(engine.evaluate(ast.arg2()), seq.table, engine));
    } else if (!takesPlain(head, argSize)) {
      return F.NIL;
    }
    if ((head == S.StringPadLeft || head == S.StringPadRight) && argSize == 2) {
      // a sequence is padded with its wildcard letter
      return delegate(call.appendClone(F.stringx(seq.table == PEPTIDE ? "X" : "N")), seq, engine);
    }
    return delegate(call, seq, engine);
  }

  /**
   * <code>StringJoin</code> of sequences: all of one type, which the joined sequence has.
   *
   * @return {@link F#NIL} if an argument is no sequence
   */
  static IExpr join(IAST ast, EvalEngine engine) {
    StringBuilder buf = new StringBuilder();
    List<String> types = new ArrayList<String>();
    Seq first = null;
    for (int i = 1; i < ast.size(); i++) {
      Seq seq = Seq.of(ast.get(i));
      if (seq == null) {
        return F.NIL;
      }
      if (!types.contains(seq.type)) {
        types.add(seq.type);
      }
      if (first == null) {
        first = seq;
      }
      buf.append(seq.letters);
    }
    if (first == null) {
      return F.NIL;
    }
    if (types.size() > 1) {
      // Matching types are needed for `1` but received `2`.
      Errors.printMessage(S.BioSequence, "biojoin",
          F.list(ast.topHead(), F.stringx(String.join(", ", types))), engine);
      return S.$Failed;
    }
    return first.with(buf.toString(), engine);
  }

  /** Evaluate the string function on the letters and turn strings into sequences again. */
  private static IExpr delegate(IAST call, Seq seq, EvalEngine engine) {
    IExpr result = engine.evaluate(call);
    if (result.isAST() && result.head() == call.head()) {
      return F.NIL;
    }
    return seq.rewrap(result, engine);
  }

  /**
   * The string pattern which matches what <code>pattern</code> matches in a sequence: a degenerate
   * letter in a literal string becomes the alternatives of all letters whose bases it covers, and
   * <code>Verbatim(s)</code> is the literal <code>s</code>. Only the left hand side of a rule is a
   * pattern.
   */
  private static IExpr translate(IExpr pattern, String[][] table, EvalEngine engine) {
    if (pattern.isString()) {
      return translateString(pattern.toString(), table);
    }
    if (pattern.isList()) {
      return ((IAST) pattern).map(x -> translate(x, table, engine));
    }
    if (pattern instanceof RepeatedPattern && ((RepeatedPattern) pattern).getSymbol() == null) {
      // p.. and p... evaluate to a pattern object
      RepeatedPattern repeated = (RepeatedPattern) pattern;
      return RepeatedPattern.valueOf(translate(repeated.getRepeatedExpr(), table, engine),
          repeated.getMin(), repeated.getMax(), repeated.isNullSequence(), engine);
    }
    if (!pattern.isAST()) {
      return pattern;
    }
    IAST ast = (IAST) pattern;
    IExpr head = ast.head();
    if (head == S.Verbatim && ast.isAST1()) {
      return ast.arg1();
    }
    if (head == S.StringExpression || head == S.Alternatives || head == S.Repeated
        || head == S.RepeatedNull || head == S.Shortest || head == S.Longest
        || head == S.Except) {
      return ast.map(x -> translate(x, table, engine));
    }
    if ((head == S.Rule || head == S.RuleDelayed || head == S.Condition || head == S.PatternTest)
        && ast.argSize() >= 1) {
      return ast.setAtCopy(1, translate(ast.arg1(), table, engine));
    }
    if (head == S.Pattern && ast.isAST2()) {
      return ast.setAtCopy(2, translate(ast.arg2(), table, engine));
    }
    return pattern;
  }

  private static IExpr translateString(String str, String[][] table) {
    IASTAppendable parts = F.ast(S.StringExpression);
    StringBuilder run = new StringBuilder();
    boolean degenerate = false;
    for (int i = 0; i < str.length(); i++) {
      String c = str.substring(i, i + 1);
      String bases = bases(c, table);
      if (bases != null && bases.length() > 1) {
        degenerate = true;
        if (run.length() > 0) {
          parts.append(F.stringx(run.toString()));
          run.setLength(0);
        }
        IASTAppendable covered = F.ast(S.Alternatives);
        for (String[] entry : table) {
          if (containsAll(bases, entry[1])) {
            covered.append(F.stringx(entry[0]));
          }
        }
        parts.append(covered);
      } else {
        run.append(c);
      }
    }
    if (!degenerate) {
      return F.stringx(str);
    }
    if (run.length() > 0) {
      parts.append(F.stringx(run.toString()));
    }
    return parts.isAST1() ? parts.arg1() : parts;
  }

  private static String bases(String letter, String[][] table) {
    for (String[] entry : table) {
      if (entry[0].equals(letter)) {
        return entry[1];
      }
    }
    return null;
  }

  private static boolean containsAll(String bases, String subset) {
    for (int i = 0; i < subset.length(); i++) {
      if (bases.indexOf(subset.charAt(i)) < 0) {
        return false;
      }
    }
    return true;
  }

  /** The string functions on a circular sequence. */
  private static IExpr circular(IAST ast, Seq seq, EvalEngine engine) {
    IExpr head = ast.head();
    int n = seq.letters.length();
    if (head == S.StringLength && ast.isAST1()) {
      return F.ZZ(n);
    }
    if (head == S.StringReverse && ast.isAST1()) {
      return seq.with(new StringBuilder(seq.letters).reverse().toString(), engine);
    }
    if (((head == S.StringTake || head == S.StringDrop || head == S.StringTakeDrop)
        && ast.isAST2()) || (head == S.StringReplacePart && ast.isAST3())) {
      int[] range = wrappingRange(ast.last(), n);
      if (range == null) {
        return delegate(ast.setAtCopy(1, F.stringx(seq.letters)), seq, engine);
      }
      String taken = seq.slice(range[0], range[1] + n);
      String rest = seq.slice(range[1] + 1, range[0] - 1);
      if (head == S.StringTake) {
        return seq.with(taken, engine);
      }
      if (head == S.StringDrop) {
        return seq.with(rest, engine);
      }
      if (head == S.StringTakeDrop) {
        return F.list(seq.with(taken, engine), seq.with(rest, engine));
      }
      if (!ast.arg2().isString()) {
        return F.NIL;
      }
      return seq.with(rest + ast.arg2().toString(), engine);
    }
    if (ast.argSize() < 2 || n == 0) {
      return F.NIL;
    }
    IExpr pattern = translate(engine.evaluate(ast.arg2()), seq.table, engine);
    if (head == S.StringPosition || head == S.StringCount || head == S.StringCases
        || head == S.StringContainsQ || head == S.StringFreeQ) {
      if (pattern.isRuleAST()
          || (pattern.isList() && ((IAST) pattern).exists(x -> x.isRuleAST()))) {
        return F.NIL;
      }
      int overlaps = overlapsOption(ast, head == S.StringPosition, engine);
      if (overlaps < 0) {
        return F.NIL;
      }
      List<int[]> spans = circularSpans(seq, pattern, overlaps == 1, engine);
      if (spans == null) {
        return F.NIL;
      }
      if (head == S.StringPosition) {
        return F.ListAlloc(
            spans.stream().map(span -> F.list(F.ZZ(span[0]), F.ZZ((span[1] - 1) % n + 1))));
      }
      if (head == S.StringCount) {
        return F.ZZ(spans.size());
      }
      if (head == S.StringCases) {
        return F.ListAlloc(
            spans.stream().map(span -> seq.with(seq.slice(span[0], span[1]), engine)));
      }
      boolean found = !spans.isEmpty();
      return F.booleSymbol(head == S.StringContainsQ ? found : !found);
    }
    if (head == S.StringMatchQ && ast.isAST2()) {
      // a circular sequence matches when one of its rotations does
      for (int k = 0; k < n; k++) {
        engine.checkTimeLimit();
        IExpr result = engine
            .evaluate(F.binaryAST2(S.StringMatchQ, F.stringx(seq.slice(k + 1, k + n)), pattern));
        if (result.isTrue()) {
          return S.True;
        }
        if (!result.isFalse()) {
          return F.NIL;
        }
      }
      return S.False;
    }
    if ((head == S.StringReplace || head == S.StringDelete) && ast.isAST2()) {
      IExpr lhs = pattern;
      if (head == S.StringReplace) {
        lhs = ruleLeftHandSides(pattern);
        if (lhs.isNIL()) {
          return F.NIL;
        }
      }
      List<int[]> spans = circularSpans(seq, lhs, false, engine);
      if (spans == null) {
        return F.NIL;
      }
      // where the letters around a match which wraps around would go is open; without one the
      // circular sequence behaves like the linear one
      for (int[] span : spans) {
        if (span[1] > n) {
          return F.NIL;
        }
      }
      return delegate(F.binaryAST2(head, F.stringx(seq.letters), pattern), seq, engine);
    }
    return F.NIL;
  }

  /**
   * A range <code>{i, j}</code> with <code>1 &lt;= j &lt; i &lt;= n</code>, which wraps around a
   * circular sequence of length <code>n</code>, or <code>null</code>.
   */
  private static int[] wrappingRange(IExpr spec, int n) {
    if (spec.isList2()) {
      int i = spec.first().toIntDefault();
      int j = spec.second().toIntDefault();
      if (i != Integer.MIN_VALUE && j != Integer.MIN_VALUE && 1 <= j && j < i && i <= n) {
        return new int[] {i, j};
      }
    }
    return null;
  }

  /**
   * The <code>Overlaps</code> option at positions 3 and later: <code>1</code> for overlapping
   * matches, <code>0</code> for none and <code>-1</code> for another argument.
   */
  private static int overlapsOption(IAST ast, boolean defaultValue, EvalEngine engine) {
    boolean overlaps = defaultValue;
    for (int i = 3; i < ast.size(); i++) {
      IExpr option = engine.evaluate(ast.get(i));
      if (!option.isRuleAST() || option.first() != S.Overlaps) {
        return -1;
      }
      if (option.second().isTrue()) {
        overlaps = true;
      } else if (option.second().isFalse()) {
        overlaps = false;
      } else {
        return -1;
      }
    }
    return overlaps ? 1 : 0;
  }

  /** The left hand sides of a rule or a list of rules, as one pattern, or {@link F#NIL}. */
  private static IExpr ruleLeftHandSides(IExpr rules) {
    if (rules.isRuleAST()) {
      return rules.first();
    }
    if (rules.isList() && ((IAST) rules).forAll(x -> x.isRuleAST())) {
      IASTAppendable alternatives = F.ast(S.Alternatives);
      ((IAST) rules).forEach(rule -> alternatives.append(rule.first()));
      return alternatives;
    }
    return F.NIL;
  }

  /**
   * The matches of <code>pattern</code> on a circular sequence as 1-based spans
   * <code>{start, end}</code>, where <code>end</code> exceeds the length for a match which wraps
   * past the last letter. Without overlaps the leftmost matches win, and a match which wraps around
   * may not reach into the first one.
   *
   * @return <code>null</code> if the matches cannot be found
   */
  private static List<int[]> circularSpans(Seq seq, IExpr pattern, boolean overlaps,
      EvalEngine engine) {
    int n = seq.letters.length();
    String extended = seq.letters + seq.letters.substring(0, n - 1);
    IExpr positions =
        engine.evaluate(F.binaryAST2(S.StringPosition, F.stringx(extended), pattern));
    if (!positions.isList()) {
      return null;
    }
    List<int[]> spans = new ArrayList<int[]>();
    for (IExpr position : (IAST) positions) {
      if (!position.isList2()) {
        return null;
      }
      int a = position.first().toIntDefault();
      int b = position.second().toIntDefault();
      if (a == Integer.MIN_VALUE || b == Integer.MIN_VALUE) {
        return null;
      }
      if (a <= n && b >= a && b - a < n) {
        spans.add(new int[] {a, b});
      }
    }
    if (overlaps) {
      return spans;
    }
    List<int[]> chosen = new ArrayList<int[]>();
    for (int[] span : spans) {
      if (!chosen.isEmpty() && span[0] <= chosen.get(chosen.size() - 1)[1]) {
        continue;
      }
      if (span[1] > n && !chosen.isEmpty() && span[1] - n >= chosen.get(0)[0]) {
        continue;
      }
      chosen.add(span);
    }
    return chosen;
  }
}
