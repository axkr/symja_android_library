package tech.tablesaw.columns.expr;

import org.matheclipse.core.expression.F;
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

  /**
   * Whether this value stands for a missing one.
   *
   * <p>
   * Anything headed <code>Missing</code> counts, not only the exact indicator: the reference writes
   * a missing value as <code>Missing[]</code> or <code>Missing["Reason", …]</code>, and
   * <code>ASTDataset</code> produces <code>Missing(NotAvailable)</code> for a skipped column. All of
   * those used to answer <code>false</code> here, so a genuinely missing value was not counted,
   * removed or reported as missing by anything built on this.
   */
  public static boolean valueIsMissing(IExpr expr) {
    return expr != null && expr.isAST(S.Missing) || S.Missing.equals(expr);
  }

  /**
   * Whether this cell holds no value at all, as against holding a <code>Missing</code> expression
   * that says why.
   *
   * <p>
   * The distinction is about display and only about display. <code>Missing(PartAbsent, 1)</code> is
   * a missing value - {@link #valueIsMissing} says so, and counting, removing and
   * <code>DeleteMissing</code> all treat it as one - but it is a missing value the user asked a
   * question to get, and printing it as an empty cell throws away the answer. An empty cell is for
   * a cell that was never filled in.
   */
  public static boolean cellIsEmpty(IExpr expr) {
    return missingValueIndicator().equals(expr) || S.Missing.equals(expr);
  }

  @Override
  public ExprColumn create(String name) {
    return ExprColumn.create(name);
  }

  @Override
  public IExprParser customParser(ReadOptions options) {
    return new IExprParser(this, options);
  }

  /**
   * The value a missing cell holds: <code>Missing(NotAvailable)</code>, the head applied, as the
   * reference writes a missing value - not the bare symbol <code>Missing</code>, which is what this
   * used to return and which nothing else produces.
   *
   * <p>
   * That particular reason because <code>ASTDataset.normal</code> already converts a skipped column
   * to <code>Missing(NotAvailable)</code>: the two halves of the module now name the same thing the
   * same way, and {@link #valueIsMissing} accepts any of the reference's spellings so a value from
   * either side is recognised.
   */
  public static IExpr missingValueIndicator() {
    return F.Missing(S.NotAvailable);
  }
}
