package org.matheclipse.core.parser.golden;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;
import org.matheclipse.parser.client.ParserConfig;

/**
 * The parser configurations the golden corpus is recorded in.
 *
 * <p>
 * The three switches below are not independent knobs the parser reads once - they change which
 * grammar is accepted and which expression a given input denotes, so a refactoring which is correct
 * in one of them can silently be wrong in another. Recording every mode is what makes the golden
 * files a usable safety net for a parser rewrite.
 *
 * <p>
 * {@link ParserConfig#EXPLICIT_TIMES_OPERATOR} and {@link ParserConfig#DOMINANT_IMPLICIT_TIMES} are
 * mutable static fields read directly by {@link ExprParser}. {@link #run(Runnable)} sets them for
 * the duration of a recording and restores the previous values afterwards, so that running the
 * golden test does not leak a configuration into the rest of the suite.
 */
public enum ParserMode {

  /**
   * The default Symja console configuration: <code>f(x)</code> is a function call, <code>2x</code>
   * is an implicit product, symbols are matched case-insensitively.
   */
  RELAXED("relaxed", true, false, false),

  /**
   * WMA bracket syntax: <code>f[x]</code> is a function call, <code>f(x)</code> is an implicit
   * product with a parenthesized expression, symbols are case-sensitive.
   */
  MMA("mma", false, false, false),

  /**
   * <code>*</code> has to be written out. This also switches on scientific E-notation and prefixed
   * integer literals, so it changes number scanning as well as operator parsing.
   */
  EXPLICIT_TIMES("explicit-times", true, true, false),

  /**
   * Implicit multiplication binds tighter than every other operator, so that <code>1/2Pi</code> is
   * <code>1/(2*Pi)</code> rather than <code>(1/2)*Pi</code>.
   */
  DOMINANT_TIMES("dominant-times", true, false, true);

  private final String id;
  private final boolean relaxedSyntax;
  private final boolean explicitTimes;
  private final boolean dominantImplicitTimes;

  ParserMode(String id, boolean relaxedSyntax, boolean explicitTimes,
      boolean dominantImplicitTimes) {
    this.id = id;
    this.relaxedSyntax = relaxedSyntax;
    this.explicitTimes = explicitTimes;
    this.dominantImplicitTimes = dominantImplicitTimes;
  }

  /** Short stable name used in the golden file names. */
  public String id() {
    return id;
  }

  public boolean isRelaxedSyntax() {
    return relaxedSyntax;
  }

  /**
   * A fresh engine for this mode. The parser reads the relaxed-syntax flag from its own field, but
   * symbol creation goes through the engine, so the engine has to agree with the mode.
   */
  public EvalEngine newEngine() {
    EvalEngine engine =
        new EvalEngine("golden-" + id, 256, 256, System.out, System.err, relaxedSyntax);
    engine.init();
    return engine;
  }

  /** A fresh parser for this mode. Parsers carry scanner state, so they are not shared. */
  public ExprParser newParser(EvalEngine engine) {
    return new ExprParser(engine, ExprParserFactory.MMA_STYLE_FACTORY, relaxedSyntax, false,
        explicitTimes);
  }

  /**
   * Run <code>body</code> with the global parser configuration this mode requires, restoring the
   * previous values afterwards.
   */
  public void run(Runnable body) {
    boolean savedExplicitTimes = ParserConfig.EXPLICIT_TIMES_OPERATOR;
    boolean savedDominantTimes = ParserConfig.DOMINANT_IMPLICIT_TIMES;
    try {
      ParserConfig.EXPLICIT_TIMES_OPERATOR = explicitTimes;
      ParserConfig.DOMINANT_IMPLICIT_TIMES = dominantImplicitTimes;
      body.run();
    } finally {
      ParserConfig.EXPLICIT_TIMES_OPERATOR = savedExplicitTimes;
      ParserConfig.DOMINANT_IMPLICIT_TIMES = savedDominantTimes;
    }
  }
}
