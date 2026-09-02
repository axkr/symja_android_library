package tech.tablesaw.columns.numbers;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

import com.google.common.collect.Lists;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.expr.ExprColumnType;
import tech.tablesaw.io.ReadOptions;

public class IExprParser extends AbstractColumnParser<IExpr> {

  public IExprParser(ColumnType columnType) {
    super(columnType);
  }

  public IExprParser(ExprColumnType exprColumnType, ReadOptions readOptions) {
    super(exprColumnType);
    if (readOptions.missingValueIndicators() != null) {
      missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicators());
    }
  }

  /**
   * Whether this text is an expression.
   *
   * <p>
   * That is what the Symja parser answers, so that is what is asked. This used to test
   * <code>Double.parseDouble</code>, which said no to <code>x^2+1</code>, <code>Sin(x)</code> and
   * <code>f(a,b)</code> - everything an expression column exists to hold - and yes only to what was
   * already a number.
   */
  @Override
  public boolean canParse(String s) {
    if (isMissing(s)) {
      return true;
    }
    try {
      new ExprParser(EvalEngine.get()).parse(s);
      return true;
    } catch (RuntimeException e) {
      // SyntaxError, and anything else the parser can throw on nonsense
      return false;
    }
  }

  @Override
  public IExpr parse(String s) {
    return parseExpr(s);
  }

  /**
   * The expression a cell holds.
   *
   * <p>
   * Parsed as written: the text is <b>not</b> put through
   * <code>AbstractColumnParser.remove(s, ',')</code> first, as it used to be. That is the
   * thousands-separator trick the numeric parsers use, and in an expression a comma is syntax -
   * it turned <code>f(a,b)</code> into <code>f(ab)</code> and <code>{1,2,3}</code> into
   * <code>{123}</code>, silently.
   *
   * <p>
   * Evaluated once parsed, so that a cell reading <code>1+1</code> holds <code>2</code> - what the
   * same value would be had it come from an association, and what the reference does. A cell that
   * cannot be evaluated is kept as it parsed.
   */
  @Override
  public IExpr parseExpr(String s) {
    if (isMissing(s)) {
      return ExprColumnType.missingValueIndicator();
    }
    EvalEngine engine = EvalEngine.get();
    IExpr parsed = new ExprParser(engine).parse(s);
    try {
      IExpr evaluated = engine.evaluate(parsed);
      return evaluated.isPresent() ? evaluated : parsed;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return parsed;
    }
  }
}
