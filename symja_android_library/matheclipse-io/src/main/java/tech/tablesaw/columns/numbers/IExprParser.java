package tech.tablesaw.columns.numbers;

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

  @Override
  public boolean canParse(String s) {
    if (isMissing(s)) {
      return true;
    }
    try {
      Double.parseDouble(AbstractColumnParser.remove(s, ','));
      return true;
    } catch (NumberFormatException e) {
      // it's all part of the plan
      return false;
    }
  }

  @Override
  public IExpr parse(String s) {
    return parseExpr(s);
  }

  @Override
  public IExpr parseExpr(String s) {
    if (isMissing(s)) {
      return ExprColumnType.missingValueIndicator();
    }
    ExprParser parser = new ExprParser(EvalEngine.get());
    return parser.parse(AbstractColumnParser.remove(s, ','));
  }
}
