package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.AbstractPatternSequence;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.PatternNested;
import org.matheclipse.core.expression.RepeatedPattern;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Finds the matches of a string pattern - <code>"a" ~~ x__ ~~ "b"</code>,
 * <code>DigitCharacter..</code>, <code>RegularExpression["..."]</code> - in a string.
 *
 * <p>
 * Most patterns translate to a <code>java.util.regex</code> expression, and that is what searches
 * for them. A pattern which looks at what it matched cannot be written as one: a test
 * <code>p?test</code> or a condition <code>p /; cond</code> is evaluated, and a name used twice -
 * <code>x__ ~~ x__</code> - or inside of a repetition stands for one string everywhere. Such a
 * pattern is matched by a backtracking search over its own structure, which tries the candidates
 * in the order the regular expression would - start positions from left to right, longer repeats
 * first unless <code>Shortest</code> says otherwise - and accepts the first one whose parts pass.
 *
 * <p>
 * Positions are UTF-16 offsets into the string, as {@link Matcher} reports them.
 */
abstract class StringPatternMatcher {

  /** The string searched. */
  protected String input = "";

  /** Where the last match starts and ends. */
  protected int matchStart = -1;

  protected int matchEnd = -1;

  /** Whether <code>"$0"</code>, <code>"$1"</code>, … in a replacement name what was captured. */
  protected boolean templates = false;

  /**
   * The matcher of <code>pattern</code>, or <code>null</code> if it is no string pattern (a
   * message has been printed then).
   *
   * @param wildcards whether <code>*</code> and <code>@</code> in a literal string are wildcards
   */
  static StringPatternMatcher compile(IExpr pattern, boolean wildcards, boolean ignoreCase,
      IAST stringFunction, EvalEngine engine) {
    StringPatternMatcher matcher;
    if (needsSearch(pattern)) {
      matcher = SearchMatcher.build(pattern, wildcards, ignoreCase, stringFunction, engine);
    } else {
      matcher = RegexMatcher.build(pattern, wildcards, ignoreCase, stringFunction, engine);
    }
    if (matcher != null) {
      matcher.templates = containsRegularExpression(pattern);
    }
    return matcher;
  }

  /** The flags every expression is compiled with. */
  static int regexFlags(boolean ignoreCase) {
    return ignoreCase ? Pattern.UNICODE_CHARACTER_CLASS | Pattern.CASE_INSENSITIVE
        : Pattern.UNICODE_CHARACTER_CLASS;
  }

  /** Start searching <code>str</code>. */
  abstract void reset(String str);

  /** The leftmost match starting at or after <code>from</code>. */
  abstract boolean find(int from);

  /**
   * Whether the pattern matches exactly the characters from <code>from</code> to <code>to</code>.
   */
  abstract boolean matchRegion(int from, int to);

  /** Whether the pattern matches a beginning of the string. */
  abstract boolean lookingAt();

  /** The leftmost match starting at or after <code>from</code> which ends at the end. */
  abstract boolean findSuffix(int from);

  /** What <code>name</code> captured in the last match, or <code>null</code>. */
  abstract String value(ISymbol name);

  /** The names of the pattern. */
  abstract Set<ISymbol> names();

  /** The number of numbered groups. */
  abstract int groupCount();

  /** What numbered group <code>n</code> captured in the last match, or <code>null</code>. */
  abstract String group(int n);

  boolean matches() {
    return matchRegion(0, input.length());
  }

  int start() {
    return matchStart;
  }

  int end() {
    return matchEnd;
  }

  String group() {
    return input.substring(matchStart, matchEnd);
  }

  String input() {
    return input;
  }

  /**
   * The right hand side of a rule with what the last match captured put in: each name of the
   * pattern becomes the string it matched, or <code>""</code> if it took no part (a name in an
   * alternative which did not match). For a <code>RegularExpression</code> the strings
   * <code>"$0"</code>, <code>"$1"</code>, … anywhere inside are replaced as well; in any other
   * pattern they are literal text.
   */
  IExpr substitute(IExpr rhs) {
    IExpr result = rhs;
    for (ISymbol name : names()) {
      String text = value(name);
      result = F.xreplace(result, name, F.stringx(text == null ? "" : text));
    }
    if (templates) {
      IExpr substituted = result.replaceAll(x -> {
        if (x.isString()) {
          String text = x.toString();
          String replaced = substituteGroupReferences(text);
          return replaced.equals(text) ? F.NIL : F.stringx(replaced);
        }
        return F.NIL;
      });
      if (substituted.isPresent()) {
        result = substituted;
      } else if (result.isString()) {
        // replaceAll does not visit the expression itself when it is an atom
        String text = result.toString();
        String replaced = substituteGroupReferences(text);
        if (!replaced.equals(text)) {
          result = F.stringx(replaced);
        }
      }
    }
    return result;
  }

  /**
   * <code>$0</code> to <code>$9</code> in <code>text</code> replaced by what the match captured.
   * <code>$0</code> is the whole match; a group that did not take part contributes nothing, and
   * <code>$$</code> is a literal dollar.
   */
  private String substituteGroupReferences(String text) {
    int dollar = text.indexOf('$');
    if (dollar < 0) {
      return text;
    }
    StringBuilder buf = new StringBuilder(text.length() + 16);
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      if (ch == '$' && i + 1 < text.length()) {
        char next = text.charAt(i + 1);
        if (next == '$') {
          buf.append('$');
          i++;
          continue;
        }
        if (next >= '0' && next <= '9') {
          int n = next - '0';
          if (n == 0) {
            buf.append(group());
            i++;
            continue;
          }
          if (n <= groupCount()) {
            String value = group(n);
            if (value != null) {
              buf.append(value);
            }
            i++;
            continue;
          }
        }
      }
      buf.append(ch);
    }
    return buf.toString();
  }

  /** The offset of the character after the one at <code>index</code>. */
  int next(int index) {
    if (index >= input.length()) {
      return index + 1;
    }
    return index + Character.charCount(input.codePointAt(index));
  }

  private static boolean containsRegularExpression(IExpr pattern) {
    if (pattern.isAST(S.RegularExpression, 2)) {
      return true;
    }
    if (pattern.isAST()) {
      IAST ast = (IAST) pattern;
      for (int i = 1; i < ast.size(); i++) {
        if (containsRegularExpression(ast.get(i))) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * The character predicates a test <code>_?DigitQ</code> can stand for: a run of characters
   * passes such a test if each of them does.
   */
  static CodePointTest knownTest(IExpr test) {
    if (test.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) test).ordinal()) {
        case ID.DigitQ:
          return cp -> cp >= '0' && cp <= '9';
        case ID.LetterQ:
          return Character::isLetter;
        case ID.UpperCaseQ:
          return Character::isUpperCase;
        case ID.LowerCaseQ:
          return Character::isLowerCase;
        default:
          break;
      }
    }
    return null;
  }

  /**
   * Whether <code>p</code> is one of <code>_</code>, <code>__</code>, <code>___</code>, possibly
   * named.
   */
  static boolean isBlankLike(IExpr p) {
    if (p.isBlank() || p.isAST(S.Blank, 1) || p.isAST(S.BlankSequence, 1)
        || p.isAST(S.BlankNullSequence, 1)) {
      return true;
    }
    if (p instanceof RepeatedPattern) {
      return false;
    }
    if (p.isPatternSequence(false)) {
      return ((AbstractPatternSequence) p).getHeadTest() == null;
    }
    if (p.isPattern() && !(p instanceof PatternNested)) {
      return ((IPattern) p).getHeadTest() == null;
    }
    return false;
  }

  /**
   * Whether <code>pattern</code> needs the backtracking search: it has a test or a condition, a
   * name used twice or a name inside of a repetition.
   */
  private static boolean needsSearch(IExpr pattern) {
    Map<ISymbol, Integer> counts = new IdentityHashMap<ISymbol, Integer>();
    return needsSearch(pattern, false, counts);
  }

  private static boolean needsSearch(IExpr p, boolean inRepeat, Map<ISymbol, Integer> counts) {
    if (p.isString()) {
      return false;
    }
    if (p.isCondition()) {
      return true;
    }
    if (p.isAST(S.PatternTest, 3)) {
      if (knownTest(p.second()) == null || !isBlankLike(p.first())) {
        return true;
      }
      return needsSearch(p.first(), inRepeat, counts);
    }
    if (p.isAST(S.RegularExpression, 2)) {
      return false;
    }
    if (p instanceof RepeatedPattern) {
      RepeatedPattern repeated = (RepeatedPattern) p;
      if (repeated.getSymbol() != null && name(repeated.getSymbol(), inRepeat, counts)) {
        return true;
      }
      IExpr expr = repeated.getRepeatedExpr();
      return expr != null && needsSearch(expr, true, counts);
    }
    if ((p.isAST(S.Repeated) || p.isAST(S.RepeatedNull)) && p.argSize() >= 1) {
      return needsSearch(p.first(), true, counts);
    }
    if (p.isPatternSequence(false)) {
      ISymbol symbol = ((AbstractPatternSequence) p).getSymbol();
      return symbol != null && name(symbol, inRepeat, counts);
    }
    if (p.isPattern()) {
      ISymbol symbol = ((IPattern) p).getSymbol();
      if (symbol != null && name(symbol, inRepeat, counts)) {
        return true;
      }
      if (p instanceof PatternNested) {
        return needsSearch(((PatternNested) p).getPatternExpr(), inRepeat, counts);
      }
      return false;
    }
    if (p.isAST(S.Pattern, 3) && p.first().isSymbol()) {
      if (name((ISymbol) p.first(), inRepeat, counts)) {
        return true;
      }
      return needsSearch(p.second(), inRepeat, counts);
    }
    if (p.isAST()) {
      IAST ast = (IAST) p;
      for (int i = 1; i < ast.size(); i++) {
        if (needsSearch(ast.get(i), inRepeat, counts)) {
          return true;
        }
      }
    }
    return false;
  }

  /** Count one more use of <code>symbol</code>; <code>true</code> if that needs the search. */
  private static boolean name(ISymbol symbol, boolean inRepeat, Map<ISymbol, Integer> counts) {
    Integer count = counts.get(symbol);
    counts.put(symbol, count == null ? 1 : count + 1);
    return inRepeat || count != null;
  }

  /** <code>Overlaps -&gt; False</code>: a match starts after the one before. */
  static final int OVERLAPS_NONE = 0;

  /** <code>Overlaps -&gt; True</code>: a match starts after the start of the one before. */
  static final int OVERLAPS_TRUE = 1;

  /** <code>Overlaps -&gt; All</code>: every match at every position. */
  static final int OVERLAPS_ALL = 2;

  /** The value of an <code>Overlaps</code> option. */
  static int overlaps(IExpr option) {
    if (option.isTrue()) {
      return OVERLAPS_TRUE;
    }
    if (option == S.All) {
      return OVERLAPS_ALL;
    }
    return OVERLAPS_NONE;
  }

  /**
   * The patterns or rules a string function looks for at once. A list of them is a choice: the
   * leftmost match of any of them is taken, and of two which start at the same position the first
   * in the list - the way an alternative <code>p1 | p2</code> chooses.
   */
  static final class Rules {
    private final StringPatternMatcher[] matchers;
    /** The right hand side of each rule, {@link F#NIL} for a pattern. */
    private final IExpr[] rhs;
    /** Where the last search of each matcher started, and the match it found (-1: none). */
    private final int[] searchedFrom;
    private final int[] foundStart;
    private int winner = -1;
    private String input = "";

    private Rules(StringPatternMatcher[] matchers, IExpr[] rhs) {
      this.matchers = matchers;
      this.rhs = rhs;
      this.searchedFrom = new int[matchers.length];
      this.foundStart = new int[matchers.length];
    }

    /**
     * The rules or patterns of <code>arg</code>, or <code>null</code> if one of them is no string
     * pattern (a message has been printed then).
     *
     * @param rulesAllowed whether a rule <code>p -&gt; rhs</code> is one; otherwise it is a
     *        pattern too, which fails
     */
    static Rules of(IExpr arg, boolean rulesAllowed, boolean wildcards, boolean ignoreCase,
        IAST stringFunction, EvalEngine engine) {
      IAST list = arg.isList() ? (IAST) arg : F.list(arg);
      StringPatternMatcher[] matchers = new StringPatternMatcher[list.argSize()];
      IExpr[] rhs = new IExpr[list.argSize()];
      for (int i = 1; i < list.size(); i++) {
        IExpr element = list.get(i);
        IExpr lhs = element;
        rhs[i - 1] = F.NIL;
        if (rulesAllowed && element.isRuleAST()) {
          lhs = element.first();
          rhs[i - 1] = element.second();
        }
        matchers[i - 1] = compile(lhs, wildcards, ignoreCase, stringFunction, engine);
        if (matchers[i - 1] == null) {
          return null;
        }
      }
      return new Rules(matchers, rhs);
    }

    void reset(String str) {
      input = str;
      winner = -1;
      for (int i = 0; i < matchers.length; i++) {
        matchers[i].reset(str);
        searchedFrom[i] = Integer.MAX_VALUE;
      }
    }

    String input() {
      return input;
    }

    int size() {
      return matchers.length;
    }

    /** The leftmost match starting at or after <code>from</code>. */
    boolean find(int from) {
      winner = -1;
      int best = Integer.MAX_VALUE;
      for (int i = 0; i < matchers.length; i++) {
        // a match found before is still the leftmost if the search starts no later than it
        boolean known =
            searchedFrom[i] <= from && (foundStart[i] < 0 || foundStart[i] >= from);
        if (!known) {
          searchedFrom[i] = from;
          foundStart[i] = matchers[i].find(from) ? matchers[i].start() : -1;
        }
        if (foundStart[i] >= 0 && foundStart[i] < best) {
          best = foundStart[i];
          winner = i;
        }
      }
      return winner >= 0;
    }

    /**
     * Whether rule <code>i</code> matches exactly the characters from <code>from</code> to
     * <code>to</code>.
     */
    boolean matchRegion(int i, int from, int to) {
      // the search state of the matcher is lost
      searchedFrom[i] = Integer.MAX_VALUE;
      if (matchers[i].matchRegion(from, to)) {
        winner = i;
        return true;
      }
      return false;
    }

    /** Whether rule <code>i</code> has a match starting at or after <code>from</code>. */
    boolean find(int i, int from) {
      searchedFrom[i] = Integer.MAX_VALUE;
      if (matchers[i].find(from)) {
        winner = i;
        return true;
      }
      return false;
    }

    int start() {
      return matchers[winner].start();
    }

    int end() {
      return matchers[winner].end();
    }

    int winner() {
      return winner;
    }

    /** Whether the last match was of a rule. */
    boolean isRule() {
      return rhs[winner].isPresent();
    }

    /**
     * What the last match gives: the evaluated right hand side of its rule, or the matched string.
     */
    IExpr value(EvalEngine engine) {
      StringPatternMatcher matcher = matchers[winner];
      if (rhs[winner].isPresent()) {
        return engine.evaluate(matcher.substitute(rhs[winner]));
      }
      return F.stringx(matcher.group());
    }

    /**
     * Visit the matches in the string from left to right.
     *
     * @param overlaps one of {@link #OVERLAPS_NONE}, {@link #OVERLAPS_TRUE},
     *        {@link #OVERLAPS_ALL}
     * @param visitor gets each match; it answers <code>false</code> to stop
     */
    void forEach(int overlaps, java.util.function.BooleanSupplier visitor, EvalEngine engine) {
      final int length = input.length();
      if (matchers.length == 0) {
        return;
      }
      if (overlaps == OVERLAPS_ALL) {
        for (int s = 0; s <= length; s = matchers[0].next(s)) {
          for (int i = 0; i < matchers.length; i++) {
            for (int e = length; e >= s; e--) {
              engine.checkTimeLimit();
              if (Character.isLowSurrogate(charAt(e)) && Character.isHighSurrogate(charAt(e - 1))) {
                continue;
              }
              if (matchRegion(i, s, e) && !visitor.getAsBoolean()) {
                return;
              }
            }
          }
        }
        return;
      }
      int from = 0;
      while (from <= length && find(from)) {
        int s = start();
        int e = end();
        if (!visitor.getAsBoolean()) {
          return;
        }
        if (overlaps == OVERLAPS_TRUE || e == s) {
          from = matchers[0].next(s);
        } else {
          from = e;
        }
      }
    }

    private char charAt(int index) {
      return index >= 0 && index < input.length() ? input.charAt(index) : '\0';
    }
  }

  /** A test of one character. */
  @FunctionalInterface
  interface CodePointTest {
    boolean test(int codePoint);
  }

  /** The search through a regular expression. */
  private static final class RegexMatcher extends StringPatternMatcher {
    private final Pattern pattern;
    private final String regex;
    private final int flags;
    private final Map<ISymbol, String> groups;
    private Pattern suffixPattern;
    private Matcher matcher;
    private Matcher current;

    private RegexMatcher(Pattern pattern, String regex, int flags, Map<ISymbol, String> groups) {
      this.pattern = pattern;
      this.regex = regex;
      this.flags = flags;
      this.groups = groups;
    }

    static RegexMatcher build(IExpr p, boolean wildcards, boolean ignoreCase, IAST stringFunction,
        EvalEngine engine) {
      Map<ISymbol, String> groups = new IdentityHashMap<ISymbol, String>();
      String regex = IStringX.toRegexString(p, wildcards, stringFunction, IStringX.REGEX_LONGEST,
          groups, engine);
      if (regex == null) {
        return null;
      }
      int flags = regexFlags(ignoreCase);
      try {
        return new RegexMatcher(Pattern.compile(regex, flags), regex, flags, groups);
      } catch (IllegalArgumentException iae) {
        // for example java.util.regex.PatternSyntaxException
        IStringX.regexErrorHandling(stringFunction, iae, engine);
        return null;
      }
    }

    @Override
    void reset(String str) {
      input = str;
      matcher = pattern.matcher(str);
      matcher.useTransparentBounds(true);
      matcher.useAnchoringBounds(false);
      current = matcher;
    }

    private boolean found(Matcher m) {
      current = m;
      matchStart = m.start();
      matchEnd = m.end();
      return true;
    }

    @Override
    boolean find(int from) {
      if (from > input.length()) {
        return false;
      }
      return matcher.find(from) && found(matcher);
    }

    @Override
    boolean matchRegion(int from, int to) {
      matcher.region(from, to);
      boolean result = matcher.matches() && found(matcher);
      return result;
    }

    @Override
    boolean lookingAt() {
      matcher.reset();
      return matcher.lookingAt() && found(matcher);
    }

    @Override
    boolean findSuffix(int from) {
      if (from > input.length()) {
        return false;
      }
      if (suffixPattern == null) {
        suffixPattern = Pattern.compile("(?:" + regex + ")\\z", flags);
      }
      Matcher m = suffixPattern.matcher(input);
      return m.find(from) && found(m);
    }

    @Override
    String value(ISymbol name) {
      String groupName = groups.get(name);
      return groupName == null ? null : current.group(groupName);
    }

    @Override
    Set<ISymbol> names() {
      return groups.keySet();
    }

    @Override
    int groupCount() {
      return current.groupCount();
    }

    @Override
    String group(int n) {
      return current.group(n);
    }
  }

  /** The backtracking search over the structure of the pattern. */
  private static final class SearchMatcher extends StringPatternMatcher {

    /** What follows a part of the pattern, given where the part ended. */
    @FunctionalInterface
    private interface Continuation {
      boolean run(int pos);
    }

    /** A part of the pattern. */
    private abstract class Node {
      /** Whether the part matches at <code>pos</code> so that the rest <code>k</code> does too. */
      abstract boolean match(int pos, Continuation k);

      /** Whether the part is one character. */
      boolean isCharacter() {
        return false;
      }
    }

    private final EvalEngine engine;
    private final boolean ignoreCase;
    private final boolean wildcards;
    private final int flags;
    private final IAST stringFunction;
    private final Map<ISymbol, Integer> variables = new IdentityHashMap<ISymbol, Integer>();
    private final List<ISymbol> names = new ArrayList<ISymbol>();
    private int[] bindStart;
    private int[] bindEnd;
    private Node root;
    private final List<Map<String, Boolean>> testCaches = new ArrayList<Map<String, Boolean>>();
    private final List<RegexLeaf> leaves = new ArrayList<RegexLeaf>();

    private SearchMatcher(boolean wildcards, boolean ignoreCase, IAST stringFunction,
        EvalEngine engine) {
      this.wildcards = wildcards;
      this.ignoreCase = ignoreCase;
      this.flags = regexFlags(ignoreCase);
      this.stringFunction = stringFunction;
      this.engine = engine;
    }

    static SearchMatcher build(IExpr p, boolean wildcards, boolean ignoreCase,
        IAST stringFunction, EvalEngine engine) {
      SearchMatcher matcher = new SearchMatcher(wildcards, ignoreCase, stringFunction, engine);
      try {
        Node root = matcher.compile(p, true);
        if (root == null) {
          return null;
        }
        matcher.root = root;
      } catch (IllegalArgumentException iae) {
        IStringX.regexErrorHandling(stringFunction, iae, engine);
        return null;
      }
      matcher.bindStart = new int[matcher.names.size()];
      matcher.bindEnd = new int[matcher.names.size()];
      return matcher;
    }

    @Override
    void reset(String str) {
      input = str;
      for (Map<String, Boolean> cache : testCaches) {
        cache.clear();
      }
      for (RegexLeaf leaf : leaves) {
        leaf.reset();
      }
    }

    private void unbindAll() {
      java.util.Arrays.fill(bindStart, -1);
      java.util.Arrays.fill(bindEnd, -1);
    }

    @Override
    boolean find(int from) {
      final int length = input.length();
      for (int s = from; s <= length; s = next(s)) {
        engine.checkTimeLimit();
        unbindAll();
        if (root.match(s, p -> {
          matchEnd = p;
          return true;
        })) {
          matchStart = s;
          return true;
        }
      }
      return false;
    }

    @Override
    boolean matchRegion(int from, int to) {
      unbindAll();
      if (root.match(from, p -> p == to)) {
        matchStart = from;
        matchEnd = to;
        return true;
      }
      return false;
    }

    @Override
    boolean lookingAt() {
      unbindAll();
      if (root.match(0, p -> {
        matchEnd = p;
        return true;
      })) {
        matchStart = 0;
        return true;
      }
      return false;
    }

    @Override
    boolean findSuffix(int from) {
      final int length = input.length();
      for (int s = from; s <= length; s = next(s)) {
        engine.checkTimeLimit();
        if (matchRegion(s, length)) {
          return true;
        }
      }
      return false;
    }

    @Override
    String value(ISymbol name) {
      Integer index = variables.get(name);
      if (index == null || bindStart[index] < 0) {
        return null;
      }
      return input.substring(bindStart[index], bindEnd[index]);
    }

    @Override
    Set<ISymbol> names() {
      return variables.keySet();
    }

    @Override
    int groupCount() {
      return 0;
    }

    @Override
    String group(int n) {
      return null;
    }

    /** The current values of the names, for a condition to look at. */
    private IExpr bindValues(IExpr expr) {
      IExpr result = expr;
      for (int i = 0; i < names.size(); i++) {
        if (bindStart[i] >= 0) {
          result = F.xreplace(result, names.get(i),
              F.stringx(input.substring(bindStart[i], bindEnd[i])));
        }
      }
      return result;
    }

    private int variable(ISymbol symbol) {
      Integer index = variables.get(symbol);
      if (index == null) {
        index = names.size();
        variables.put(symbol, index);
        names.add(symbol);
      }
      return index;
    }

    // ---- building the nodes ----

    private Node unsupported(IExpr p) {
      // `1` currently not supported in `2`.
      org.matheclipse.core.eval.Errors.printMessage(stringFunction.topHead(), "unsupported",
          F.list(p, stringFunction.topHead()), engine);
      return null;
    }

    private Node compile(IExpr p, boolean greedy) {
      if (p.isString()) {
        return literal(p.toString(), greedy);
      }
      if (p.isAST(S.RegularExpression, 2) && p.first().isString()) {
        return new RegexLeaf(Pattern.compile(p.first().toString(), flags));
      }
      if (p.isCondition()) {
        // the pattern of a held condition is still written as its functions
        Node child = compile(engine.evaluate(p.first()), greedy);
        return child == null ? null : new ConditionNode(child, p.second());
      }
      if (p.isAST(S.PatternTest, 3)) {
        return patternTest(p.first(), p.second(), greedy);
      }
      if (p instanceof RepeatedPattern) {
        RepeatedPattern repeated = (RepeatedPattern) p;
        IExpr expr = repeated.getRepeatedExpr();
        if (expr == null) {
          return unsupported(p);
        }
        int min = repeated.getMin();
        if (min <= 0) {
          min = repeated.isNullSequence() ? 0 : 1;
        }
        Node child = repeat(expr, min, repeated.getMax(), greedy);
        if (child == null) {
          return null;
        }
        return repeated.getSymbol() == null ? child : named(repeated.getSymbol(), child);
      }
      if ((p.isAST(S.Repeated) || p.isAST(S.RepeatedNull)) && p.argSize() >= 1
          && p.argSize() <= 2) {
        boolean isNull = p.isAST(S.RepeatedNull);
        int min = isNull ? 0 : 1;
        int max = Integer.MAX_VALUE;
        if (p.argSize() == 2) {
          IExpr spec = p.second();
          if (spec.isInteger()) {
            max = spec.toIntDefault();
          } else if (spec.isList1() && spec.first().isInteger()) {
            min = spec.first().toIntDefault();
            max = min;
          } else if (spec.isList2() && spec.first().isInteger() && spec.second().isInteger()) {
            min = spec.first().toIntDefault();
            max = spec.second().toIntDefault();
          } else {
            return unsupported(p);
          }
        }
        return repeat(p.first(), min, max, greedy);
      }
      if (p.isAST(S.StringExpression)) {
        IAST ast = (IAST) p;
        Node[] parts = new Node[ast.argSize()];
        for (int i = 1; i < ast.size(); i++) {
          parts[i - 1] = compile(ast.get(i), greedy);
          if (parts[i - 1] == null) {
            return null;
          }
        }
        return new SequenceNode(parts);
      }
      if (p.isBlank() || p.isAST(S.Blank, 1)) {
        return new CharacterNode(cp -> true);
      }
      if (p.isAST(S.BlankSequence, 1) || p.isAST(S.BlankNullSequence, 1)) {
        return new RunNode(cp -> true, p.isAST(S.BlankNullSequence) ? 0 : 1, Integer.MAX_VALUE,
            greedy);
      }
      if (p.isPatternSequence(false)) {
        AbstractPatternSequence ps = (AbstractPatternSequence) p;
        Node run = new RunNode(cp -> true, ps.isNullSequence() ? 0 : 1, Integer.MAX_VALUE, greedy);
        return ps.getSymbol() == null ? run : named(ps.getSymbol(), run);
      }
      if (p.isPattern()) {
        IPattern pattern = (IPattern) p;
        ISymbol symbol = pattern.getSymbol();
        if (symbol != null && pattern.getHeadTest() == null) {
          if (pattern instanceof PatternNested) {
            Node child = compile(((PatternNested) pattern).getPatternExpr(), greedy);
            return child == null ? null : named(symbol, child);
          }
          return named(symbol, new CharacterNode(cp -> true));
        }
        return unsupported(p);
      }
      if (p.isAST(S.Pattern, 3) && p.first().isSymbol()) {
        Node child = compile(p.second(), greedy);
        return child == null ? null : named((ISymbol) p.first(), child);
      }
      if (p.isAlternatives() || p.isList()) {
        IAST ast = (IAST) p;
        Node[] choices = new Node[ast.argSize()];
        for (int i = 1; i < ast.size(); i++) {
          choices[i - 1] = compile(ast.get(i), greedy);
          if (choices[i - 1] == null) {
            return null;
          }
        }
        return new AlternativesNode(choices);
      }
      if (p.isExcept() && p.argSize() >= 1) {
        Node excluded = compile(p.first(), greedy);
        if (excluded == null) {
          return null;
        }
        Node allowed;
        if (p.isAST2()) {
          allowed = compile(p.second(), greedy);
          if (allowed == null) {
            return null;
          }
        } else {
          allowed = new CharacterNode(cp -> true);
        }
        return new ExceptNode(excluded, allowed);
      }
      if (p.isAST(S.Shortest, 2)) {
        return compile(p.first(), false);
      }
      if (p.isAST(S.Longest, 2)) {
        return compile(p.first(), true);
      }
      if (p.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) p).ordinal()) {
          case ID.StartOfString:
          case ID.EndOfString:
          case ID.StartOfLine:
          case ID.EndOfLine:
          case ID.WordBoundary:
            return new AnchorNode(((IBuiltInSymbol) p).ordinal());
          case ID.Whitespace:
            return new RunNode(regexTest("\\s"), 1, Integer.MAX_VALUE, greedy);
          default:
            break;
        }
      }
      // a character class or anything else the regular expression translation knows
      Map<ISymbol, String> groups = new IdentityHashMap<ISymbol, String>();
      String regex = IStringX.toRegexString(p, wildcards, stringFunction, IStringX.REGEX_LONGEST,
          groups, engine);
      if (regex == null) {
        return null;
      }
      if (p.isBuiltInSymbol() && ((IBuiltInSymbol) p).ordinal() != ID.NumberString
          || p.isAST(S.CharacterRange, 3) || p.isAST(S.Characters, 2)) {
        return new CharacterNode(regexTest(regex));
      }
      return new RegexLeaf(Pattern.compile(regex, flags));
    }

    private Node literal(String str, boolean greedy) {
      if (!wildcards || (str.indexOf('*') < 0 && str.indexOf('@') < 0 && str.indexOf('\\') < 0)) {
        return new LiteralNode(str);
      }
      List<Node> parts = new ArrayList<Node>();
      StringBuilder text = new StringBuilder();
      for (int i = 0; i < str.length(); i++) {
        char c = str.charAt(i);
        if (c == '\\' && i + 1 < str.length()
            && (str.charAt(i + 1) == '*' || str.charAt(i + 1) == '@')) {
          // a backslash makes the following * or @ literal; any other backslash is itself
          text.append(str.charAt(++i));
        } else if (c == '*' || c == '@') {
          if (text.length() > 0) {
            parts.add(new LiteralNode(text.toString()));
            text.setLength(0);
          }
          parts.add(c == '*' ? new RunNode(cp -> true, 0, Integer.MAX_VALUE, greedy)
              : new RunNode(cp -> !Character.isUpperCase(cp), 1, Integer.MAX_VALUE, greedy));
        } else {
          text.append(c);
        }
      }
      if (text.length() > 0) {
        parts.add(new LiteralNode(text.toString()));
      }
      return new SequenceNode(parts.toArray(new Node[parts.size()]));
    }

    private Node repeat(IExpr expr, int min, int max, boolean greedy) {
      Node child = compile(expr, greedy);
      if (child == null) {
        return null;
      }
      if (child instanceof CharacterNode) {
        return new RunNode(((CharacterNode) child).test, min, max, greedy);
      }
      return new RepeatNode(child, min, max, greedy);
    }

    private Node patternTest(IExpr p, IExpr test, boolean greedy) {
      CodePointTest known = knownTest(test);
      if (known != null && isBlankLike(p)) {
        ISymbol symbol = null;
        Node node;
        if (p.isBlank() || p.isAST(S.Blank, 1)) {
          node = new CharacterNode(known);
        } else if (p.isAST(S.BlankSequence, 1) || p.isAST(S.BlankNullSequence, 1)) {
          node = new RunNode(known, p.isAST(S.BlankNullSequence) ? 0 : 1, Integer.MAX_VALUE,
              greedy);
        } else if (p.isPatternSequence(false)) {
          AbstractPatternSequence ps = (AbstractPatternSequence) p;
          symbol = ps.getSymbol();
          node = new RunNode(known, ps.isNullSequence() ? 0 : 1, Integer.MAX_VALUE, greedy);
        } else {
          symbol = ((IPattern) p).getSymbol();
          node = new CharacterNode(known);
        }
        return symbol == null ? node : named(symbol, node);
      }
      Node child = compile(p, greedy);
      return child == null ? null : new TestNode(child, test);
    }

    private Node named(ISymbol symbol, Node child) {
      // the first use of a name which is reached binds it, any later one has to match the same
      return new NamedNode(variable(symbol), child);
    }

    private CodePointTest regexTest(String regex) {
      final Pattern pattern = Pattern.compile(regex, flags);
      final Map<Integer, Boolean> cache = new HashMap<Integer, Boolean>();
      return cp -> {
        Boolean result = cache.get(cp);
        if (result == null) {
          result = pattern.matcher(new String(Character.toChars(cp))).matches();
          cache.put(cp, result);
        }
        return result;
      };
    }

    // ---- the nodes ----

    private final class LiteralNode extends Node {
      private final String text;

      LiteralNode(String text) {
        this.text = text;
      }

      @Override
      boolean match(int pos, Continuation k) {
        if (input.regionMatches(ignoreCase, pos, text, 0, text.length())) {
          return k.run(pos + text.length());
        }
        return false;
      }
    }

    private final class CharacterNode extends Node {
      final CodePointTest test;

      CharacterNode(CodePointTest test) {
        this.test = test;
      }

      @Override
      boolean match(int pos, Continuation k) {
        if (pos < input.length()) {
          int cp = input.codePointAt(pos);
          if (test.test(cp)) {
            return k.run(pos + Character.charCount(cp));
          }
        }
        return false;
      }

      @Override
      boolean isCharacter() {
        return true;
      }
    }

    /** A run of characters which each pass a test - a blank sequence or a repeated class. */
    private final class RunNode extends Node {
      private final CodePointTest test;
      private final int min;
      private final int max;
      private final boolean greedy;

      RunNode(CodePointTest test, int min, int max, boolean greedy) {
        this.test = test;
        this.min = min;
        this.max = max;
        this.greedy = greedy;
      }

      @Override
      boolean match(int pos, Continuation k) {
        final int length = input.length();
        int[] ends = new int[16];
        ends[0] = pos;
        int count = 0;
        int p = pos;
        while (count < max && p < length) {
          int cp = input.codePointAt(p);
          if (!test.test(cp)) {
            break;
          }
          p += Character.charCount(cp);
          count++;
          if (count >= ends.length) {
            ends = java.util.Arrays.copyOf(ends, ends.length * 2);
          }
          ends[count] = p;
        }
        if (count < min) {
          return false;
        }
        if (greedy) {
          for (int c = count; c >= min; c--) {
            if (k.run(ends[c])) {
              return true;
            }
          }
        } else {
          for (int c = min; c <= count; c++) {
            if (k.run(ends[c])) {
              return true;
            }
          }
        }
        return false;
      }
    }

    private final class RepeatNode extends Node {
      private final Node child;
      private final int min;
      private final int max;
      private final boolean greedy;

      RepeatNode(Node child, int min, int max, boolean greedy) {
        this.child = child;
        this.min = min;
        this.max = max;
        this.greedy = greedy;
      }

      @Override
      boolean match(int pos, Continuation k) {
        return repeat(0, pos, k);
      }

      private boolean repeat(int count, int pos, Continuation k) {
        engine.checkTimeLimit();
        if (greedy) {
          // an iteration which matches nothing would repeat for ever
          if (count < max
              && child.match(pos, p -> (p != pos || count < min) && repeat(count + 1, p, k))) {
            return true;
          }
          return count >= min && k.run(pos);
        }
        if (count >= min && k.run(pos)) {
          return true;
        }
        return count < max
            && child.match(pos, p -> (p != pos || count < min) && repeat(count + 1, p, k));
      }
    }

    private final class SequenceNode extends Node {
      private final Node[] parts;

      SequenceNode(Node[] parts) {
        this.parts = parts;
      }

      @Override
      boolean match(int pos, Continuation k) {
        return match(0, pos, k);
      }

      private boolean match(int i, int pos, Continuation k) {
        if (i == parts.length) {
          return k.run(pos);
        }
        return parts[i].match(pos, p -> match(i + 1, p, k));
      }
    }

    private final class AlternativesNode extends Node {
      private final Node[] choices;

      AlternativesNode(Node[] choices) {
        this.choices = choices;
      }

      @Override
      boolean match(int pos, Continuation k) {
        for (Node choice : choices) {
          if (choice.match(pos, k)) {
            return true;
          }
        }
        return false;
      }
    }

    private final class NamedNode extends Node {
      private final int variable;
      private final Node child;

      NamedNode(int variable, Node child) {
        this.variable = variable;
        this.child = child;
      }

      @Override
      boolean match(int pos, Continuation k) {
        final int boundStart = bindStart[variable];
        if (boundStart >= 0) {
          // the name already stands for a string: match the same one again
          final int boundLength = bindEnd[variable] - boundStart;
          if (!input.regionMatches(ignoreCase, pos, input, boundStart, boundLength)) {
            return false;
          }
          return child.match(pos, p -> p == pos + boundLength && k.run(p));
        }
        // not bound yet - also a later use of the name, in another branch of an alternative
        return child.match(pos, p -> {
          bindStart[variable] = pos;
          bindEnd[variable] = p;
          if (k.run(p)) {
            return true;
          }
          bindStart[variable] = -1;
          bindEnd[variable] = -1;
          return false;
        });
      }
    }

    private final class TestNode extends Node {
      private final Node child;
      private final IExpr test;
      private final Map<String, Boolean> cache = new HashMap<String, Boolean>();

      TestNode(Node child, IExpr test) {
        this.child = child;
        this.test = test;
        testCaches.add(cache);
      }

      @Override
      boolean match(int pos, Continuation k) {
        return child.match(pos, p -> passes(input.substring(pos, p)) && k.run(p));
      }

      private boolean passes(String text) {
        Boolean result = cache.get(text);
        if (result == null) {
          result = engine.evalTrue(F.unaryAST1(test, F.stringx(text)));
          cache.put(text, result);
        }
        return result;
      }
    }

    private final class ConditionNode extends Node {
      private final Node child;
      private final IExpr condition;

      ConditionNode(Node child, IExpr condition) {
        this.child = child;
        this.condition = condition;
      }

      @Override
      boolean match(int pos, Continuation k) {
        return child.match(pos, p -> engine.evalTrue(bindValues(condition)) && k.run(p));
      }
    }

    private final class ExceptNode extends Node {
      private final Node excluded;
      private final Node allowed;

      ExceptNode(Node excluded, Node allowed) {
        this.excluded = excluded;
        this.allowed = allowed;
      }

      @Override
      boolean match(int pos, Continuation k) {
        // names inside of the excluded pattern bind nothing
        int[] start = bindStart.clone();
        int[] end = bindEnd.clone();
        boolean isExcluded = excluded.match(pos, p -> true);
        System.arraycopy(start, 0, bindStart, 0, start.length);
        System.arraycopy(end, 0, bindEnd, 0, end.length);
        if (isExcluded) {
          return false;
        }
        return allowed.match(pos, k);
      }
    }

    private final class AnchorNode extends Node {
      private final int kind;

      AnchorNode(int kind) {
        this.kind = kind;
      }

      @Override
      boolean match(int pos, Continuation k) {
        final int length = input.length();
        boolean at;
        switch (kind) {
          case ID.StartOfString:
            at = pos == 0;
            break;
          case ID.EndOfString:
            at = pos == length;
            break;
          case ID.StartOfLine:
            at = pos == 0 || input.charAt(pos - 1) == '\n';
            break;
          case ID.EndOfLine:
            at = pos == length || input.charAt(pos) == '\n';
            break;
          default:
            // WordBoundary
            boolean before = pos > 0 && isWordCharacter(input.codePointBefore(pos));
            boolean after = pos < length && isWordCharacter(input.codePointAt(pos));
            at = before != after;
            break;
        }
        return at && k.run(pos);
      }

      private boolean isWordCharacter(int cp) {
        return cp == '_' || Character.isLetterOrDigit(cp);
      }
    }

    /**
     * A part only a regular expression describes: it is tried with the end the expression
     * prefers first, then with every other end from the longest to the shortest.
     */
    private final class RegexLeaf extends Node {
      private final Pattern pattern;
      private Matcher matcher;

      RegexLeaf(Pattern pattern) {
        this.pattern = pattern;
        leaves.add(this);
      }

      void reset() {
        matcher = null;
      }

      private Matcher matcher() {
        if (matcher == null) {
          matcher = pattern.matcher(input);
          matcher.useTransparentBounds(true);
          matcher.useAnchoringBounds(false);
        }
        return matcher;
      }

      @Override
      boolean match(int pos, Continuation k) {
        final int length = input.length();
        Matcher m = matcher();
        m.region(pos, length);
        int preferred = -1;
        if (m.lookingAt()) {
          preferred = m.end();
          if (k.run(preferred)) {
            return true;
          }
        }
        for (int e = length; e >= pos; e--) {
          if (e == preferred) {
            continue;
          }
          m = matcher();
          m.region(pos, e);
          if (m.matches() && k.run(e)) {
            return true;
          }
        }
        return false;
      }
    }
  }
}
