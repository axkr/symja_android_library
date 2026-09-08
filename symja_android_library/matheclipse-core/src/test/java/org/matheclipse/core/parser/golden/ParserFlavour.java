package org.matheclipse.core.parser.golden;

import java.util.function.Function;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * The two parsers built on {@link org.matheclipse.parser.client.Scanner}, each recorded into its
 * own set of golden files.
 *
 * <p>
 * They matter separately because they share the scanner but not the parsing: {@link ExprParser}
 * builds {@link org.matheclipse.core.interfaces.IExpr} and applies input rewrites on the way
 * (<code>Sqrt(x)</code> becomes <code>Power(x, 1/2)</code>, <code>a/2</code> becomes a
 * <code>Rational</code>), while {@link Parser} builds a plain {@link ASTNode} tree with none of
 * that. A change to the shared scanner - number scanning, identifier scanning, operator token
 * scanning - can therefore break one and not the other, and only the {@code EXPR} flavour was
 * covered before.
 *
 * <p>
 * A third parser, {@code FuzzyParser} in matheclipse-api, also extends the same scanner. It is not
 * covered here because matheclipse-api sits downstream of this module; see the README.
 */
public enum ParserFlavour {

  /**
   * {@link ExprParser}, recorded as {@code IExpr.fullFormString()} - the unambiguous form.
   * {@code toString()} would render operators back out and hide exactly the grouping this corpus
   * exists to pin down.
   */
  EXPR("expr", mode -> {
    ExprParser parser = mode.newParser(mode.newEngine());
    return input -> {
      IExpr expr = parser.parse(input);
      return expr == null ? null : expr.fullFormString();
    };
  }),

  /** {@link Parser}, recorded as {@code ASTNode.toString()}. */
  AST("ast", mode -> {
    Parser parser = new Parser(mode.isRelaxedSyntax());
    return input -> {
      ASTNode node = parser.parse(input);
      return node == null ? null : node.toString();
    };
  });

  /**
   * Parses one input and returns its recorded form, or <code>null</code>. Throws
   * {@link SyntaxError} like the parsers themselves; {@link ParserGolden} turns that into a golden
   * marker.
   */
  @FunctionalInterface
  public interface Recorder {
    String parse(String input);
  }

  private final String id;
  private final Function<ParserMode, Recorder> recorderFactory;

  ParserFlavour(String id, Function<ParserMode, Recorder> recorderFactory) {
    this.id = id;
    this.recorderFactory = recorderFactory;
  }

  /** Short stable name used in the golden file names. */
  public String id() {
    return id;
  }

  /**
   * A recorder for this flavour in the given mode. Parsers carry scanner state, so each call
   * returns a recorder over a fresh parser.
   */
  public Recorder newRecorder(ParserMode mode) {
    return recorderFactory.apply(mode);
  }
}
