package tech.tablesaw.columns.expr;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.ExprColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.numbers.IExprParser;
import tech.tablesaw.io.ReadOptions;

public class ExprColumnType extends AbstractColumnType {
  public static final IExpr MISSING_VALUE = missingValueIndicator();
  public static final int BYTE_SIZE = 4;
  public static final IExprParser DEFAULT_PARSER = new IExprParser(ColumnType.EXPR);

  private static ExprColumnType INSTANCE;

  private ExprColumnType(int byteSize, String name, String printerFriendlyName) {
    super(byteSize, name, printerFriendlyName);
  }

  public static ExprColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new ExprColumnType(BYTE_SIZE, "EXPR", "Expr");
    }
    return INSTANCE;
  }

  public static boolean valueIsMissing(IExpr expr) {
    return missingValueIndicator().equals(expr);
  }

  @Override
  public ExprColumn create(String name) {
    return ExprColumn.create(name);
  }

  @Override
  public IExprParser customParser(ReadOptions options) {
    return new IExprParser(this, options);
  }

  public static IExpr missingValueIndicator() {
    return S.Missing;
  }
}
