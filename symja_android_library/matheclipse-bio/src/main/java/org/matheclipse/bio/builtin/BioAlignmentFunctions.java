package org.matheclipse.bio.builtin;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.bio.align.GenericAligner;
import org.matheclipse.bio.align.GenericAligner.Alignment;
import org.matheclipse.bio.align.GenericAligner.Mode;
import org.matheclipse.bio.convert.SimilarityMatrices;
import org.matheclipse.bio.expression.data.BioSequenceExpr;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Tier 2 of the <code>matheclipse-bio</code> module: pairwise sequence alignment and similarity.
 *
 * <p>
 * Both arguments biological (a {@link BioSequenceExpr} of a BioJava-supported type) uses a real
 * substitution matrix from <code>biojava-core</code>; anything else — plain strings, lists of
 * arbitrary expressions — goes through {@link GenericAligner}, because BioJava's aligners are typed
 * over biological alphabets and cannot represent an arbitrary expression.
 */
public class BioAlignmentFunctions {

  /** Default match score when <code>SimilarityRules -&gt; Automatic</code>. */
  private static final int DEFAULT_MATCH = 1;

  /** Default mismatch score. */
  private static final int DEFAULT_MISMATCH = -1;

  /** Default gap penalty. */
  private static final int DEFAULT_GAP_PENALTY = 1;

  private static class Initializer {

    private static void init() {
      S.SequenceAlignment.setEvaluator(new SequenceAlignment());
      S.SmithWatermanSimilarity.setEvaluator(new SmithWatermanSimilarity());
      S.NeedlemanWunschSimilarity.setEvaluator(new NeedlemanWunschSimilarity());
    }
  }

  /** The alignment options common to all three functions, resolved from the option sequence. */
  private static class Settings {
    int match = DEFAULT_MATCH;
    int mismatch = DEFAULT_MISMATCH;
    int gapOpen = DEFAULT_GAP_PENALTY;
    int gapExtend = DEFAULT_GAP_PENALTY;
    boolean ignoreCase = false;
    boolean mergeDifferences = true;
    Mode mode;
    /** An explicit <code>SimilarityRules</code> rule list, or {@link F#NIL}. */
    IExpr similarityRules = F.NIL;
    /** A named substitution matrix such as <code>"BLOSUM62"</code>, or <code>null</code>. */
    String matrixName = null;

    Settings(Mode defaultMode) {
      this.mode = defaultMode;
    }
  }

  /**
   * <code>SequenceAlignment(s1, s2)</code> — the alignment as a list of common runs and
   * <code>{a, b}</code> differences.
   */
  private static class SequenceAlignment extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Settings settings = settings(ast, Mode.GLOBAL, engine);
      if (settings == null) {
        return F.NIL;
      }
      IExpr[] a = elements(ast.arg1(), settings.ignoreCase);
      IExpr[] b = elements(ast.arg2(), settings.ignoreCase);
      if (a == null || b == null) {
        return F.NIL;
      }
      Alignment alignment = align(ast, a, b, settings, engine);
      if (alignment == null) {
        return F.NIL;
      }
      boolean asString = ast.arg1().isString() && ast.arg2().isString();
      return toSequenceAlignment(alignment, settings, asString);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, F.List( //
          F.Rule(S.GapPenalty, F.ZZ(DEFAULT_GAP_PENALTY)), //
          F.Rule(S.IgnoreCase, S.False), //
          F.Rule(S.MergeDifferences, S.True), //
          F.Rule(S.Method, S.Automatic), //
          F.Rule(S.SimilarityRules, S.Automatic)));
    }
  }

  /**
   * <code>SmithWatermanSimilarity(s1, s2)</code> — matched elements in the best local alignment.
   */
  private static class SmithWatermanSimilarity extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return similarity(ast, Mode.LOCAL, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, similarityOptions());
    }
  }

  /**
   * <code>NeedlemanWunschSimilarity(s1, s2)</code> — matched elements in the best global alignment.
   */
  private static class NeedlemanWunschSimilarity extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return similarity(ast, Mode.GLOBAL, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, similarityOptions());
    }
  }

  /** The options shared by the two similarity functions. */
  private static IAST similarityOptions() {
    return F.List( //
        F.Rule(S.GapPenalty, F.ZZ(DEFAULT_GAP_PENALTY)), //
        F.Rule(S.IgnoreCase, S.False), //
        F.Rule(S.SimilarityRules, S.Automatic));
  }

  private static IExpr similarity(IAST ast, Mode mode, EvalEngine engine) {
    Settings settings = settings(ast, mode, engine);
    if (settings == null) {
      return F.NIL;
    }
    settings.mode = mode;
    IExpr[] a = elements(ast.arg1(), settings.ignoreCase);
    IExpr[] b = elements(ast.arg2(), settings.ignoreCase);
    if (a == null || b == null) {
      return F.NIL;
    }
    Alignment alignment = align(ast, a, b, settings, engine);
    if (alignment == null) {
      return F.NIL;
    }
    return F.ZZ(alignment.matchCount);
  }

  // ---------------------------------------------------------------- alignment

  private static Alignment align(IAST ast, IExpr[] a, IExpr[] b, Settings settings,
      EvalEngine engine) {
    final GenericAligner.Scorer scorer = scorer(ast, settings, engine);
    if (scorer == null) {
      return null;
    }
    return GenericAligner.align(a, b, scorer, settings.gapOpen, settings.gapExtend, settings.mode);
  }

  /**
   * The substitution scoring: a named BioJava matrix when <code>SimilarityRules</code> names one,
   * an explicit rule list when it is one, otherwise <code>+1</code>/<code>-1</code> default.
   */
  private static GenericAligner.Scorer scorer(IAST ast, final Settings settings,
      EvalEngine engine) {
    if (settings.matrixName != null) {
      final SimilarityMatrices.Lookup matrix = SimilarityMatrices.byName(settings.matrixName);
      if (matrix == null) {
        Errors.printMessage(ast.topHead(), "biomatrix", F.List(F.stringx(settings.matrixName)),
            engine);
        return null;
      }
      return new GenericAligner.Scorer() {
        @Override
        public int score(IExpr x, IExpr y) {
          Integer value = matrix.score(text(x), text(y));
          if (value != null) {
            return value.intValue();
          }
          return x.equals(y) ? settings.match : settings.mismatch;
        }

        @Override
        public boolean isMatch(IExpr x, IExpr y) {
          return x.equals(y);
        }
      };
    }

    final IExpr rules = settings.similarityRules;
    if (rules.isListOfRules(false)) {
      final IAST ruleList = (IAST) rules;
      return new GenericAligner.Scorer() {
        @Override
        public int score(IExpr x, IExpr y) {
          for (int i = 1; i < ruleList.size(); i++) {
            IAST rule = (IAST) ruleList.get(i);
            IExpr lhs = rule.arg1();
            if (lhs.isList() && ((IAST) lhs).argSize() == 2) {
              IExpr first = ((IAST) lhs).arg1();
              IExpr second = ((IAST) lhs).arg2();
              if (matchesPattern(first, x) && matchesPattern(second, y)) {
                int value = rule.arg2().toIntDefault();
                if (value != Integer.MIN_VALUE) {
                  return value;
                }
              }
            }
          }
          return x.equals(y) ? settings.match : settings.mismatch;
        }

        @Override
        public boolean isMatch(IExpr x, IExpr y) {
          return x.equals(y);
        }
      };
    }

    return new GenericAligner.Scorer() {
      @Override
      public int score(IExpr x, IExpr y) {
        return x.equals(y) ? settings.match : settings.mismatch;
      }

      @Override
      public boolean isMatch(IExpr x, IExpr y) {
        return x.equals(y);
      }
    };
  }

  /** <code>_</code> in a <code>SimilarityRules</code> left-hand side matches any element. */
  private static boolean matchesPattern(IExpr pattern, IExpr element) {
    if (pattern.isBlank()) {
      return true;
    }
    return pattern.equals(element);
  }

  // ---------------------------------------------------------------- output

  /**
   * Merge the aligned pair into run/difference list: equal stretches collapse to one element,
   * unequal stretches to a <code>{a, b}</code> pair.
   */
  private static IExpr toSequenceAlignment(Alignment alignment, Settings settings,
      boolean asString) {
    IASTAppendable result = F.ListAlloc(8);
    int index = 0;
    final int size = alignment.alignedA.length;
    while (index < size) {
      IExpr x = alignment.alignedA[index];
      IExpr y = alignment.alignedB[index];
      boolean equal = x != null && y != null && x.equals(y);
      int start = index;
      while (index < size) {
        IExpr nx = alignment.alignedA[index];
        IExpr ny = alignment.alignedB[index];
        boolean nextEqual = nx != null && ny != null && nx.equals(ny);
        if (nextEqual != equal) {
          break;
        }
        index++;
        if (!settings.mergeDifferences && !equal) {
          break;
        }
      }
      if (equal) {
        result.append(run(alignment.alignedA, start, index, asString));
      } else {
        result.append(F.List( //
            run(alignment.alignedA, start, index, asString), //
            run(alignment.alignedB, start, index, asString)));
      }
    }
    return result;
  }

  /**
   * The elements of <code>aligned[start, end)</code>, skipping gaps, as a string when the input was
   * a string and as a list otherwise.
   */
  private static IExpr run(IExpr[] aligned, int start, int end, boolean asString) {
    if (asString) {
      StringBuilder buf = new StringBuilder(end - start);
      for (int i = start; i < end; i++) {
        if (aligned[i] != null) {
          buf.append(text(aligned[i]));
        }
      }
      return F.stringx(buf.toString());
    }
    IASTAppendable list = F.ListAlloc(end - start);
    for (int i = start; i < end; i++) {
      if (aligned[i] != null) {
        list.append(aligned[i]);
      }
    }
    return list;
  }

  // ---------------------------------------------------------------- input

  /**
   * The elements to align: the characters of a string or a {@link BioSequenceExpr}, or the elements
   * of a list.
   *
   * @return <code>null</code> if <code>expr</code> is neither
   */
  private static IExpr[] elements(IExpr expr, boolean ignoreCase) {
    if (expr instanceof BioSequenceExpr) {
      return characters(((BioSequenceExpr) expr).getSequenceAsString(), ignoreCase);
    }
    if (expr.isString()) {
      return characters(expr.toString(), ignoreCase);
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      IExpr[] result = new IExpr[list.argSize()];
      for (int i = 1; i < list.size(); i++) {
        IExpr element = list.get(i);
        if (ignoreCase && element.isString()) {
          element = F.stringx(element.toString().toUpperCase());
        }
        result[i - 1] = element;
      }
      return result;
    }
    return null;
  }

  private static IExpr[] characters(String str, boolean ignoreCase) {
    if (ignoreCase) {
      str = str.toUpperCase();
    }
    IExpr[] result = new IExpr[str.length()];
    for (int i = 0; i < str.length(); i++) {
      result[i] = F.stringx(str.substring(i, i + 1));
    }
    return result;
  }

  private static String text(IExpr expr) {
    return expr.isString() ? expr.toString() : expr.toString();
  }

  // ---------------------------------------------------------------- options

  /** @return the resolved settings, or <code>null</code> when an option value is unusable */
  private static Settings settings(IAST ast, Mode defaultMode, EvalEngine engine) {
    Settings settings = new Settings(defaultMode);
    if (ast.argSize() <= 2) {
      return settings;
    }
    final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);

    IExpr gapPenalty = options.getOption(S.GapPenalty);
    if (gapPenalty.isPresent()) {
      if (gapPenalty.isList() && ((IAST) gapPenalty).argSize() == 2) {
        int open = ((IAST) gapPenalty).arg1().toIntDefault();
        int extend = ((IAST) gapPenalty).arg2().toIntDefault();
        if (open == Integer.MIN_VALUE || extend == Integer.MIN_VALUE) {
          Errors.printMessage(ast.topHead(), "biogap", F.List(gapPenalty), engine);
          return null;
        }
        settings.gapOpen = Math.abs(open);
        settings.gapExtend = Math.abs(extend);
      } else {
        int value = gapPenalty.toIntDefault();
        if (value == Integer.MIN_VALUE) {
          Errors.printMessage(ast.topHead(), "biogap", F.List(gapPenalty), engine);
          return null;
        }
        settings.gapOpen = Math.abs(value);
        settings.gapExtend = Math.abs(value);
      }
    }

    IExpr ignoreCase = options.getOption(S.IgnoreCase);
    if (ignoreCase.isPresent()) {
      settings.ignoreCase = ignoreCase.isTrue();
    }

    IExpr mergeDifferences = options.getOption(S.MergeDifferences);
    if (mergeDifferences.isPresent()) {
      settings.mergeDifferences = !mergeDifferences.isFalse();
    }

    IExpr method = options.getOption(S.Method);
    if (method.isPresent() && method.isString()) {
      String name = method.toString();
      if ("Local".equals(name)) {
        settings.mode = Mode.LOCAL;
      } else if ("Global".equals(name)) {
        settings.mode = Mode.GLOBAL;
      } else {
        Errors.printMessage(ast.topHead(), "biomethod", F.List(method), engine);
        return null;
      }
    }

    IExpr similarityRules = options.getOption(S.SimilarityRules);
    if (similarityRules.isPresent() && similarityRules != S.Automatic) {
      if (similarityRules.isString()) {
        settings.matrixName = similarityRules.toString();
      } else if (similarityRules.isListOfRules(false)) {
        settings.similarityRules = similarityRules;
      } else {
        Errors.printMessage(ast.topHead(), "biorules", F.List(similarityRules), engine);
        return null;
      }
    }
    return settings;
  }

  /** Unused today; kept so the list of shipped matrices is discoverable from this class. */
  static List<String> matrixNames() {
    return new ArrayList<String>(SimilarityMatrices.names());
  }

  public static void initialize() {
    Initializer.init();
  }

  private BioAlignmentFunctions() {}
}
