package tech.tablesaw.columns.expr;

import java.util.function.Function;

import org.matheclipse.core.interfaces.IExpr;

public class ExprColumnFormatter {

  private final Function<IExpr, String> formatter;
  private String missingString = "";

  public ExprColumnFormatter() {
    this.formatter = null;
  }

  public ExprColumnFormatter(Function<IExpr, String> formatFunction) {
    this.formatter = formatFunction;
  }

  public ExprColumnFormatter(Function<IExpr, String> formatFunction, String missingString) {
    this.formatter = formatFunction;
    this.missingString = missingString;
  }

  public String format(IExpr value) {

    if (ExprColumnType.missingValueIndicator().equals(value)) {
      return missingString;
    }
    if (formatter == null) {
      return value.toString();
    }
    return formatter.apply(value);
  }

  @Override
  public String toString() {
    return "StringColumnFormatter{"
        + "format="
        + formatter
        + ", missingString='"
        + missingString
        + '\''
        + '}';
  }
}
