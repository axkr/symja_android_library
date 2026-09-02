package org.matheclipse.dataset.expression;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.io.output.StringBuilderWriter;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.RandomFunctions;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.MemoryLimitExceeded;
import org.matheclipse.core.expression.AbstractAST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DateObjectExpr;
import org.matheclipse.core.expression.data.TimeObjectExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitorLong;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.expr.ExprColumnType;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.ExprColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.html.HtmlWriteOptions;

public class ASTDataset extends AbstractAST
    implements IDataExpr<Table>, IASTDataset, Externalizable {

  private static final long serialVersionUID = 7276828936929270780L;

  /**
   * Create a dataset from a <a href="https://github.com/jtablesaw/tablesaw">Tablesaw table</a>
   *
   * @param table
   * @return {@link F#NIL} if the dataset cannot be created
   */
  public static ASTDataset newTablesawTable(Table table) {
    return new ASTDataset(table);
  }

  /**
   * A dataset of a bare list of values - the reference's <code>Dataset[{3, 7, 11}]</code>.
   *
   * <p>
   * Stored as a single column with no name, so that the printing shows the values and not a header
   * that was never given one; {@link #normal(boolean)} gives the bare list back.
   */
  public static IExpr newVector(IAST values) {
    Table table = Table.create();
    // a real name, not the empty one it is shown under: the CSV that writeExternal produces has a
    // header line, and an empty header does not survive being read back
    table.addColumns(ExprColumn.create(VECTOR_COLUMN, listValues(values)));
    ASTDataset dataset = new ASTDataset(table);
    dataset.fShape = Shape.VECTOR;
    return dataset;
  }

  /**
   * A dataset of a bare association - the reference's <code>Dataset[&lt;|"a" -&gt; 3|&gt;]</code>.
   *
   * <p>
   * Stored as a key column and a value column; {@link #normal(boolean)} gives the association back.
   * Distinct from {@link #newAssociationOfAssociations}, whose values are themselves associations
   * and which is a table of rows that happen to be named.
   */
  public static IExpr newAssociation(IAssociation assoc) {
    List<IExpr> keys = new ArrayList<IExpr>(assoc.argSize());
    List<IExpr> values = new ArrayList<IExpr>(assoc.argSize());
    for (int i = 1; i < assoc.size(); i++) {
      IAST rule = assoc.getRule(i);
      keys.add(rule.first());
      values.add(rule.second());
    }
    Table table = Table.create();
    table.addColumns(ExprColumn.create("key", keys), ExprColumn.create("value", values));
    ASTDataset dataset = new ASTDataset(table);
    dataset.fShape = Shape.ASSOCIATION;
    return dataset;
  }

  private static List<IExpr> listValues(IAST list) {
    List<IExpr> values = new ArrayList<IExpr>(list.argSize());
    for (int i = 1; i < list.size(); i++) {
      values.add(list.get(i));
    }
    return values;
  }

  /** The name the single column of a {@link Shape#VECTOR} is stored under, and never shown under. */
  private static final String VECTOR_COLUMN = "value";

  /**
   * The name of the column a dataset built from an association of associations keeps its row keys
   * in. See {@link #newAssociationOfAssociations(IAssociation)}, which reserves it.
   */
  private static final String KEY_COLUMN = "";

  /** Whether this dataset names its rows, keeping the names in {@link #KEY_COLUMN}. */
  private boolean hasRowKeys() {
    return fShape == Shape.TABLE && fTable.columnCount() > 1
        && KEY_COLUMN.equals(fTable.columnNames().get(0));
  }

  /** @see Shape */
  public Shape getShape() {
    return fShape;
  }

  /**
   * The same dataset with display options attached.
   *
   * @param rules a <code>List(...)</code> of option rules, or {@link F#NIL}
   */
  public ASTDataset withOptions(IAST rules) {
    DatasetOptions options = DatasetOptions.of(rules);
    if (options.isDefault()) {
      return this;
    }
    ASTDataset result = new ASTDataset(fTable);
    result.fOptions = options;
    return result;
  }

  /** The display options this dataset carries. */
  public DatasetOptions getOptions() {
    return fOptions;
  }

  /**
   * A dataset derived from this one - a selection, a copy, a slice - keeping the display options.
   * Styling a dataset and then asking it for a column should not lose the styling.
   */
  /**
   * A derived dataset that is a vector when the selection named a single column, and a table
   * otherwise. The column keeps its stored name either way; a vector simply does not show it.
   */
  private ASTDataset deriveColumn(Table table, boolean scalarColumn) {
    return deriveColumn(table, scalarColumn, false);
  }

  private ASTDataset deriveColumn(Table table, boolean scalarColumn, boolean keyedColumn) {
    ASTDataset result = derive(table);
    if (keyedColumn && table.columnCount() == 2) {
      result.fShape = Shape.ASSOCIATION;
    } else if (scalarColumn && table.columnCount() == 1) {
      result.fShape = Shape.VECTOR;
    }
    return result;
  }

  /**
   * The one value a one by one table holds - and a dataset of it when that value is itself a
   * collection, which is the rule the reference follows everywhere: a dataset wraps a collection
   * and gives a scalar back bare. <code>dataset[5, "c"]</code> on a column of lists is
   * <code>Dataset[{5, 6, 7}]</code>, while <code>dataset[1, "b"]</code> on a column of strings is
   * the bare string.
   */
  private static IExpr cell(Table table) {
    return IASTDataset.restoreDataset(Object2Expr.convertString(table.get(0, 0)));
  }

  private ASTDataset derive(Table table) {
    ASTDataset result = new ASTDataset(table);
    result.fOptions = fOptions;
    // a slice of a vector is still a vector, and of an association still an association
    result.fShape = fShape;
    return result;
  }

  /**
   * Create a <code>Dataset</code> object from a <code>List(...)</code> of associations. Each
   * association represents a row in the <code>Dataset</code>. The left-hand-side of each singular
   * rule in an association was assumed to be the name of the resulting dataset columns. Identical
   * names maps the right-hand-side values of the rule to the same columns in the resulting <code>
   * Dataset
   * </code>.
   *
   * @param listOfAssociations
   * @return {@link F#NIL} if the <code>Dataset</code> cannot be created
   */
  public static IExpr newListOfAssociations(IAST listOfAssociations) {
    // 1. phase: build up column names
    List<String> colNames = new ArrayList<String>();
    Set<String> colNamesSet = new HashSet<String>();
    for (int i = 1; i < listOfAssociations.size(); i++) {
      IAssociation assoc = (IAssociation) listOfAssociations.get(i);
      for (int j = 1; j < assoc.size(); j++) {
        IAST rule = assoc.getRule(j);
        String columnName = rule.first().toString();
        if (!colNamesSet.contains(columnName)) {
          colNamesSet.add(columnName);
          colNames.add(columnName);
        }
      }
    }
    if (colNames.size() > 0) {
      // 2. phase: collect the values column by column, so that each column can be given the type
      // its contents actually have - see typedColumn
      int rowCount = listOfAssociations.argSize();
      List<List<IExpr>> columns = emptyColumns(colNames.size(), rowCount);
      for (int i = 1; i < listOfAssociations.size(); i++) {
        IAssociation assoc = (IAssociation) listOfAssociations.get(i);
        for (int j = 1; j < assoc.size(); j++) {
          IAST rule = assoc.getRule(j);
          int column = colNames.indexOf(rule.first().toString());
          if (column >= 0) {
            columns.get(column).set(i - 1, rule.second());
          }
        }
      }
      return newTablesawTable(buildTable(colNames, columns));
    }
    return F.NIL;
  }

  /**
   * Create a <code>Dataset</code> object from a (head-)association <code>&lt;|...|&gt;</code> of
   * (sub-)associations. Each key in the (head-)association is used in the first column the
   * (sub-)association represents the other columns of a row in the <code>Dataset</code>. The
   * left-hand-side of each singular rule in a (sub-)association was assumed to be the name of the
   * resulting <code>Dataset</code> columns. Identical names maps the right-hand-side values of the
   * rule to the same columns in the resulting dataset.
   *
   * @param assocOfAssociations
   * @return {@link F#NIL} if the <code>Dataset</code> cannot be created
   */
  public static IExpr newAssociationOfAssociations(IAssociation assocOfAssociations) {
    // 1. phase: build up column names; reserve 1 column for header assoc
    List<String> colNames = new ArrayList<String>();
    Set<String> colNamesSet = new HashSet<String>();
    colNamesSet.add("");
    colNames.add("");
    for (int i = 1; i < assocOfAssociations.size(); i++) {
      IAssociation assoc = (IAssociation) assocOfAssociations.get(i);
      for (int j = 1; j < assoc.size(); j++) {
        IAST rule = assoc.getRule(j);
        String columnName = rule.first().toString();
        if (!colNamesSet.contains(columnName)) {
          colNamesSet.add(columnName);
          colNames.add(columnName);
        }
      }
    }
    if (colNames.size() > 0) {
      // 2. phase: as in newListOfAssociations - column by column, so each gets a real type
      int rowCount = assocOfAssociations.argSize();
      List<List<IExpr>> columns = emptyColumns(colNames.size(), rowCount);
      for (int i = 1; i < assocOfAssociations.size(); i++) {
        IExpr rule = assocOfAssociations.getRule(i);
        IAssociation assoc = (IAssociation) rule.second();
        columns.get(0).set(i - 1, rule.first());
        for (int j = 1; j < assoc.size(); j++) {
          rule = assoc.getRule(j);
          int column = colNames.indexOf(rule.first().toString());
          if (column >= 0) {
            columns.get(column).set(i - 1, rule.second());
          }
        }
      }
      return newTablesawTable(buildTable(colNames, columns));
    }
    return F.NIL;
  }

  /** One list per column, every cell missing until something is put in it. */
  private static List<List<IExpr>> emptyColumns(int columnCount, int rowCount) {
    List<List<IExpr>> columns = new ArrayList<List<IExpr>>(columnCount);
    for (int i = 0; i < columnCount; i++) {
      List<IExpr> column = new ArrayList<IExpr>(rowCount);
      for (int row = 0; row < rowCount; row++) {
        column.add(ExprColumnType.missingValueIndicator());
      }
      columns.add(column);
    }
    return columns;
  }

  private static Table buildTable(List<String> colNames, List<List<IExpr>> columns) {
    Column<?>[] cols = new Column<?>[colNames.size()];
    for (int i = 0; i < cols.length; i++) {
      cols[i] = typedColumn(colNames.get(i), columns.get(i));
    }
    return Table.create(cols);
  }

  /**
   * A column of the narrowest type its values all fit, and an {@link ExprColumn} otherwise.
   *
   * <p>
   * Everything used to go into an <code>ExprColumn</code>, which is neither a
   * <code>CategoricalColumn</code> nor a <code>NumericColumn</code> - so a <code>Dataset</code>
   * built from associations, which is how the reference documents building one, could not be
   * grouped, aggregated or summarized at all: <code>AggregateBy</code> failed with
   * <code>ExprColumn cannot be cast to CategoricalColumn</code>, and <code>Summary</code> reported
   * nothing but a count. The same data read from a CSV worked, because the CSV reader types its
   * columns. This closes that gap.
   *
   * <p>
   * Narrowest that round-trips, not narrowest possible: a column of integers becomes an
   * <code>IntColumn</code> rather than a <code>DoubleColumn</code>, so that <code>Normal</code>
   * gives back the <code>1</code> that was put in and not <code>1.0</code>. Rationals, complex
   * numbers, symbols and anything mixed stay expressions, because no other column can hold them
   * without changing what they are.
   */
  private static Column<?> typedColumn(String name, List<IExpr> values) {
    boolean allInteger = true;
    boolean allNumeric = true;
    boolean allString = true;
    boolean allBoolean = true;
    boolean any = false;
    for (IExpr value : values) {
      if (ExprColumnType.valueIsMissing(value)) {
        continue;
      }
      any = true;
      allInteger &= value.isInteger();
      // an integer or a machine real. Deliberately not a fraction: 1/2 in a DoubleColumn comes back
      // as 0.5, and a value that does not survive the round trip is not this method's to convert
      allNumeric &= value.isInteger() || value.isInexactNumber() && value.isReal();
      allString &= value.isString();
      allBoolean &= value == S.True || value == S.False;
    }
    if (!any) {
      return ExprColumn.create(name, values);
    }
    if (allBoolean) {
      BooleanColumn column = BooleanColumn.create(name);
      for (IExpr value : values) {
        if (ExprColumnType.valueIsMissing(value)) {
          column.appendMissing();
        } else {
          column.append(value == S.True);
        }
      }
      return column;
    }
    if (allInteger) {
      boolean fitsInt = true;
      for (IExpr value : values) {
        if (!ExprColumnType.valueIsMissing(value) && F.isNotPresent(value.toIntDefault())) {
          fitsInt = false;
          break;
        }
      }
      if (fitsInt) {
        IntColumn column = IntColumn.create(name);
        for (IExpr value : values) {
          if (ExprColumnType.valueIsMissing(value)) {
            column.appendMissing();
          } else {
            column.append(value.toIntDefault());
          }
        }
        return column;
      }
      boolean fitsLong = true;
      for (IExpr value : values) {
        if (!ExprColumnType.valueIsMissing(value) && value.toLongDefault() == Long.MIN_VALUE) {
          fitsLong = false;
          break;
        }
      }
      if (fitsLong) {
        LongColumn column = LongColumn.create(name);
        for (IExpr value : values) {
          if (ExprColumnType.valueIsMissing(value)) {
            column.appendMissing();
          } else {
            column.append(value.toLongDefault());
          }
        }
        return column;
      }
    }
    if (allNumeric) {
      DoubleColumn column = DoubleColumn.create(name);
      for (IExpr value : values) {
        if (ExprColumnType.valueIsMissing(value)) {
          column.appendMissing();
        } else {
          column.append(value.evalf());
        }
      }
      return column;
    }
    if (allString) {
      StringColumn column = StringColumn.create(name);
      for (IExpr value : values) {
        if (ExprColumnType.valueIsMissing(value)) {
          column.appendMissing();
        } else {
          column.append(value.toString());
        }
      }
      return column;
    }
    return ExprColumn.create(name, values);
  }

  private static void ruleCache(Cache<IAST, IAST> cache, IAssociation assoc, IAST rule) {
    IAST result = cache.getIfPresent(rule);
    if (result != null) {
      assoc.appendRule(result);
      return;
    }
    cache.put(rule, rule);
    assoc.appendRule(rule);
  }

  /**
   * The shape a dataset really has, which is not always the table it is stored in.
   *
   * <p>
   * The reference's dataset is typed - a vector of structs, a bare vector, an association - and this
   * one is a <code>tech.tablesaw</code> table underneath, which is only the first of those. A bare
   * vector is kept as a one column table and a bare association as two columns, and this says which
   * of the three it means, so that <code>Normal</code>, the printing and the indexing can answer for
   * the shape rather than for the storage.
   */
  public enum Shape {
    /** Rows of named columns: what a table is, and what most datasets are. */
    TABLE,
    /** A bare list of values, held in one column. */
    VECTOR,
    /** A bare association, held as a key column and a value column. */
    ASSOCIATION
  }

  protected transient Table fTable;

  /** @see Shape */
  protected transient Shape fShape = Shape.TABLE;

  /**
   * The display options, or {@link DatasetOptions#DEFAULT}. Display only: two datasets holding the
   * same table are equal whatever they are styled like, which is also what keeps
   * {@link #equals(Object)} and {@link #hashCode()} what they were.
   */
  protected transient DatasetOptions fOptions = DatasetOptions.DEFAULT;

  public ASTDataset() {
    // default ctor for serialization
  }

  protected ASTDataset(final Table table) {
    fTable = table;
  }

  @Override
  public void csv(Writer writer) {
    fTable.write().csv(writer);
  }

  /** {@inheritDoc} */
  @Override
  public long accept(IVisitorLong visitor) {
    return 0L;
  }

  @Override
  public ASTDataset copy() {
    return derive(fTable);
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Two datasets are equal when they hold the same data in the same shape, with the same display
   * options. This used to hand the question to <code>Table</code>, which has no equality of its own
   * and so answered by identity - meaning no dataset was ever equal to any other, and
   * <code>Dataset[{&lt;|"a" -&gt; 1|&gt;}] === Dataset[{&lt;|"a" -&gt; 1|&gt;}]</code> was
   * <code>False</code>. The canonical order was never affected: two datasets already compare by
   * their rows, which is why <code>Union</code> and <code>DeleteDuplicates</code> were right.
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ASTDataset)) {
      return false;
    }
    ASTDataset other = (ASTDataset) obj;
    if (fShape != other.fShape || !fOptions.rules().equals(other.fOptions.rules())) {
      return false;
    }
    // A vector has no field name - the column it is stored under is storage and nothing more, and
    // is "value" when it was built as one and whatever it was called when it was selected out of a
    // table. Comparing those names would make Dataset[{1, 2}] unequal to a column holding 1 and 2.
    return sameData(fTable, other.fTable, fShape != Shape.VECTOR);
  }

  /**
   * Whether two tables hold the same values in the same order, under the same column names when
   * <code>compareNames</code> says the names are part of the data.
   */
  private static boolean sameData(Table left, Table right, boolean compareNames) {
    if (left == right) {
      return true;
    }
    if (left == null || right == null || left.rowCount() != right.rowCount()
        || left.columnCount() != right.columnCount()
        || (compareNames && !left.columnNames().equals(right.columnNames()))) {
      return false;
    }
    for (int column = 0; column < left.columnCount(); column++) {
      Column<?> leftColumn = left.column(column);
      Column<?> rightColumn = right.column(column);
      for (int row = 0; row < left.rowCount(); row++) {
        // the values as expressions, not as tablesaw stores them. The same number reaches a
        // column as an int when it was read from a table and as an IExpr when it was built from a
        // list, and those two objects are not equal to each other - but 1 is 1
        if (!cellExpr(leftColumn, row).equals(cellExpr(rightColumn, row))) {
          return false;
        }
      }
    }
    return true;
  }

  /** One cell as the expression it holds, whatever column type it is stored in. */
  private static IExpr cellExpr(Column<?> column, int row) {
    return column.isMissing(row) ? ExprColumnType.missingValueIndicator()
        : dataToExpr(column.get(row), column.type());
  }

  @Override
  public IExpr evaluate(EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public IExpr evalEvaluate(EvalEngine engine) {
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    return head() + "(" + fTable.toString() + ")";
  }

  @Override
  public IExpr get(int location) {
    if (location == 0) {
      return head();
    }
    if (fShape == Shape.VECTOR) {
      // indexing a vector down to one element gives the element, not a dataset of one: the
      // reference's Dataset[{3, 7, 11}][[1]] is 3, head Integer
      return getColumnValue(location - 1, 0);
    }
    if (fShape == Shape.ASSOCIATION) {
      // ... and a position into an association gives the value at it
      return getColumnValue(location - 1, 1);
    }
    if (fTable.rowCount() == 1) {
      return getColumnValue(0, location - 1);
    }
    return derive(fTable.rows(location - 1));
  }

  private IExpr getColumnValue(int rowPosition, int columnPosition) {
    Column<?> column = fTable.column(columnPosition);
    ColumnType t = column.type();
    Object obj = fTable.get(rowPosition, columnPosition);
    if (obj == null || column.isMissing(rowPosition)) {
      // A cell that was never filled in - a row of the source lacked the key this column is named
      // after. Two ways it shows: a null, which every unboxing branch below threw
      // NullPointerException on the moment anything walked the dataset (the precision scan the
      // parser runs included), and a type's own sentinel, which came back as the number
      // -2147483648 out of an int column - a value that was never in the data.
      return ExprColumnType.missingValueIndicator();
    }
    if (t.equals(ColumnType.BOOLEAN)) {
      Boolean b = (Boolean) obj;
      if (b) {
        return S.True;
      } else {
        return S.False;
      }
    } else if (t.equals(ColumnType.SHORT)) {
      short sValue = (Short) obj;
      return F.ZZ(sValue);
    } else if (t.equals(ColumnType.INTEGER)) {
      int iValue = (Integer) obj;
      return F.ZZ(iValue);
    } else if (t.equals(ColumnType.LONG)) {
      long lValue = (Long) obj;
      return F.ZZ(lValue);
    } else if (t.equals(ColumnType.FLOAT)) {
      float fValue = (Float) obj;
      return F.num(fValue);
    } else if (t.equals(ColumnType.DOUBLE)) {
      double dValue = (Double) obj;
      return F.num(dValue);
    } else if (t.equals(ColumnType.STRING)) {
      return F.stringx((String) obj);
    } else if (t.equals(ColumnType.EXPR)) {
      return (IExpr) obj;
      // } else if (t.equals(ColumnType.SKIP)) {
      // ruleCache(cache, assoc, F.Rule(colName, F.Missing));
    }
    IExpr valueStr = F.stringx(obj.toString());
    return valueStr;
  }

  @Override
  public IAST getItems(int[] items, int length, int offset) {
    if (fTable.rowCount() == 1) {
      // One row indexes by column - Length is the column count and get(i) is a value - so a
      // selection of several is a list of values, the same thing get(i) would give one at a time.
      // See size() and get(int).
      IASTAppendable result = F.ListAlloc(Math.max(length, 0));
      for (int i = 0; i < length; i++) {
        result.append(get(items[i] + offset));
      }
      return result;
    }
    if (length <= 0) {
      // emptyCopy rather than Table.create: an empty selection of a table still has its columns
      return derive(fTable.emptyCopy());
    }
    int[] rows = new int[length];
    for (int i = 0; i < length; i++) {
      rows[i] = items[i] + offset - 1;
    }
    return derive(rowsInOrder(rows));
  }

  /**
   * Return the value associated to the <code>key</code>. If no value is available return <code>
   * Missing("KeyAbsent", key)</code>
   *
   * @param key
   * @return
   */
  @Override
  public IExpr getValue(IExpr key) {
    return getValue(key, () -> F.Missing(F.stringx("KeyAbsent"), key));
  }

  /**
   * Return the value associated to the <code>key</code>. If no value is available return the <code>
   * defaultValue</code>
   *
   * @param key
   * @param defaultValue
   * @return
   */
  @Override
  public IExpr getValue(IExpr key, Supplier<IExpr> defaultValue) {
    if (fShape == Shape.ASSOCIATION) {
      // a key names a value here, and the value comes back bare - Dataset[<|"a" -> 3|>]["a"] is 3
      for (int row = 0; row < fTable.rowCount(); row++) {
        if (dataToExpr(fTable.get(row, 0), fTable.column(0).type()).equals(key)) {
          return dataToExpr(fTable.get(row, 1), fTable.column(1).type());
        }
      }
      return defaultValue.get();
    }
    final String keyName = key.toString();
    if (fTable.rowCount() == 1) {
      int columnIndex = fTable.columnIndex(keyName);
      if (columnIndex < 0) {
        return defaultValue.get();
      }
      return select(1, columnIndex + 1);
    }
    String[] strList = new String[] {keyName};
    Table table = fTable.selectColumns(strList);
    if (table.columnCount() == 0) {
      return defaultValue.get();
    }
    return derive(table);
  }

  public IExpr sortBy(List<String> group) {
    String[] strings = new String[group.size()];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = group.get(i);
    }
    Table table = fTable.sortAscendingOn(strings);
    return derive(table);
  }

  @Override
  public IExpr groupBy(List<String> group) {
    String[] strings = new String[group.size()];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = group.get(i);
    }
    Table table = fTable.sortAscendingOn(strings);
    return derive(table);
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * Deliberately cheap: the shape of the table rather than its contents. Everything
   * {@link #equals(Object)} calls equal agrees on all of this, which is all a hash has to promise,
   * and a hash that walked every cell would make a dataset expensive to put in any map - the
   * evaluator's own caches included. Datasets of the same shape collide, and equality then decides.
   */
  @Override
  public int hashCode() {
    if (fTable == null) {
      return 59;
    }
    int result = 59 + fShape.hashCode();
    result = 31 * result + fTable.rowCount();
    result = 31 * result + fTable.columnCount();
    if (fShape != Shape.VECTOR) {
      // a vector's column name is not part of what it is - see equals
      result = 31 * result + fTable.columnNames().hashCode();
    }
    return result;
  }

  @Override
  public ISymbol head() {
    return S.Dataset;
  }

  @Override
  public int hierarchy() {
    return DATASETID;
  }

  @Override
  public IASTAppendable columnNames() {
    final List<String> names = fTable.columnNames();
    IASTAppendable list = F.ListAlloc(names.size());
    for (int i = 0; i < names.size(); i++) {
      list.append(names.get(i));
    }
    return list;
  }

  @Override
  public IASTAppendable normal(boolean nilIfUnevaluated) {
    if (fShape == Shape.ASSOCIATION) {
      // the association it was built from, not the two columns it is kept in
      IAssociation assoc = F.assoc();
      for (int row = 0; row < fTable.rowCount(); row++) {
        assoc.appendRule(F.Rule(dataToExpr(fTable.get(row, 0), fTable.column(0).type()),
            dataToExpr(fTable.get(row, 1), fTable.column(1).type())));
      }
      return (IASTAppendable) assoc;
    }
    Cache<IAST, IAST> cache = CacheBuilder.newBuilder().maximumSize(500).build();
    final List<String> names = fTable.columnNames();
    List<IStringX> namesStr = new ArrayList<IStringX>(names.size());
    for (int i = 0; i < names.size(); i++) {
      namesStr.add(F.stringx(names.get(i)));
    }
    if (names.size() == 1) {
      Column<?> column = fTable.column(names.get(0));
      ColumnType t = column.type();
      IASTAppendable resultList = F.ListAlloc(column.size());
      for (int j = 0; j < column.size(); j++) {
        if (column.isMissing(j)) {
          // the indicator, not the type's own sentinel - an int column with nothing in a cell
          // holds Integer.MIN_VALUE, and handing that back puts -2147483648 into the data
          resultList.append(ExprColumnType.missingValueIndicator());
          continue;
        }
        resultList.append(dataToExpr(column.get(j), t));
      }
      return resultList;
    }

    IASTAppendable list = F.ListAlloc(names.size());
    int size = fTable.rowCount();
    for (int k = 0; k < size; k++) {
      Row row = fTable.row(k);
      IAssociation assoc = F.assoc();
      for (int j = 0; j < row.columnCount(); j++) {
        String columnName = names.get(j);
        IStringX colName = namesStr.get(j);
        ColumnType t = row.getColumnType(columnName);
        if (fTable.column(j).isMissing(k)) {
          // as above: a cell the source never filled in reads as missing and not as the sentinel
          // its column type happens to keep for one
          ruleCache(cache, assoc, F.Rule(colName, ExprColumnType.missingValueIndicator()));
          continue;
        }
        Object obj = row.getObject(j);
        if (t.equals(ColumnType.EXPR)) {
          IExpr expr = (IExpr) obj;
          ruleCache(cache, assoc, F.Rule(colName, expr));
        } else if (t.equals(ColumnType.BOOLEAN)) {
          Boolean b = row.getBoolean(j);
          if (b) {
            ruleCache(cache, assoc, F.Rule(colName, S.True));
          } else {
            ruleCache(cache, assoc, F.Rule(colName, S.False));
          }
        } else if (t.equals(ColumnType.SHORT)) {
          short sValue = row.getShort(j);
          ruleCache(cache, assoc, F.Rule(colName, F.ZZ(sValue)));
        } else if (t.equals(ColumnType.INTEGER)) {
          int iValue = row.getInt(j);
          ruleCache(cache, assoc, F.Rule(colName, F.ZZ(iValue)));
        } else if (t.equals(ColumnType.LONG)) {
          long lValue = row.getLong(j);
          ruleCache(cache, assoc, F.Rule(colName, F.ZZ(lValue)));
        } else if (t.equals(ColumnType.FLOAT)) {
          float fValue = row.getFloat(j);
          ruleCache(cache, assoc, F.Rule(colName, F.num(fValue)));
        } else if (t.equals(ColumnType.DOUBLE)) {
          double dValue = row.getDouble(j);
          ruleCache(cache, assoc, F.Rule(colName, F.num(dValue)));
        } else if (t.equals(ColumnType.STRING)) {
          ruleCache(cache, assoc, F.Rule(colName, F.stringx(row.getString(j))));
        } else if (t.equals(ColumnType.LOCAL_DATE_TIME)) {
          LocalDateTime lDate = row.getDateTime(j);
          ruleCache(cache, assoc, F.Rule(colName, DateObjectExpr.newInstance(lDate)));
        } else if (t.equals(ColumnType.LOCAL_DATE)) {
          LocalDate lDate = row.getDate(j);
          ruleCache(cache, assoc,
              F.Rule(colName, DateObjectExpr.newInstance(lDate.atStartOfDay())));
        } else if (t.equals(ColumnType.LOCAL_TIME)) {
          LocalTime lTime = row.getTime(j);
          ruleCache(cache, assoc, F.Rule(colName, TimeObjectExpr.newInstance(lTime)));
        } else if (t.equals(ColumnType.SKIP)) {
          // ruleCache(cache, assoc, F.Rule(colName, F.Missing));
          ruleCache(cache, assoc, F.Rule(colName, F.Missing(S.NotAvailable)));
        } else {
          IExpr valueStr = F.stringx(obj.toString());
          ruleCache(cache, assoc, F.Rule(colName, valueStr));
        }
      }
      if (size == 1) {
        return assoc;
      }
      list.append(assoc);
    }
    return hasRowKeys() ? keyedRows(list) : list;
  }

  /**
   * The rows of a dataset whose rows have names, keyed by those names.
   *
   * <p>
   * The names are kept in a column of their own - see {@link #KEY_COLUMN} - because a table is what
   * this is stored in. They are not data in the rows, so
   * <code>Normal[Dataset[&lt;|"a" -&gt; &lt;|"x" -&gt; 1|&gt;|&gt;]]</code> is
   * <code>&lt;|"a" -&gt; &lt;|"x" -&gt; 1|&gt;|&gt;</code> and not a list of one row carrying its
   * own name under an empty key.
   */
  private static IASTAppendable keyedRows(IAST rows) {
    IAssociation result = F.assoc();
    IStringX keyColumn = F.stringx(KEY_COLUMN);
    for (int i = 1; i < rows.size(); i++) {
      IExpr row = rows.get(i);
      if (!row.isAssociation()) {
        // not the shape this was built for; leave it as it stands
        return (IASTAppendable) rows;
      }
      IAssociation assoc = (IAssociation) row;
      IAssociation withoutKey = F.assoc();
      for (int j = 1; j < assoc.size(); j++) {
        IAST rule = assoc.getRule(j);
        if (!KEY_COLUMN.equals(rule.first().toString())) {
          withoutKey.appendRule(rule);
        }
      }
      result.appendRule(F.Rule(assoc.getValue(keyColumn), withoutKey));
    }
    return (IASTAppendable) result;
  }

  private static IExpr dataToExpr(Object obj, ColumnType t) {
    IExpr expr;
    if (t.equals(ColumnType.EXPR)) {
      expr = (IExpr) obj;
    } else if (t.equals(ColumnType.BOOLEAN)) {
      Boolean b = (Boolean) obj;
      if (b) {
        expr = S.True;
      } else {
        expr = S.False;
      }
    } else if (t.equals(ColumnType.SHORT)) {
      short sValue = (Short) obj;
      expr = F.ZZ(sValue);
    } else if (t.equals(ColumnType.INTEGER)) {
      int iValue = (Integer) obj;
      expr = F.ZZ(iValue);
    } else if (t.equals(ColumnType.LONG)) {
      long lValue = (Long) obj;
      expr = F.ZZ(lValue);
    } else if (t.equals(ColumnType.FLOAT)) {
      float fValue = (Float) obj;
      expr = F.num(fValue);
    } else if (t.equals(ColumnType.DOUBLE)) {
      double dValue = (Double) obj;
      expr = F.num(dValue);
    } else if (t.equals(ColumnType.STRING)) {
      expr = F.stringx((String) obj);
    } else if (t.equals(ColumnType.SKIP)) {
      // ruleCache(cache, assoc, F.Rule(colName, F.Missing));
      expr = F.Missing(S.NotAvailable);
    } else if (t.equals(ColumnType.LOCAL_DATE_TIME)) {
      LocalDateTime lDate = (LocalDateTime) obj;
      expr = DateObjectExpr.newInstance(lDate);
    } else if (t.equals(ColumnType.LOCAL_DATE)) {
      LocalDate date = (LocalDate) obj;
      expr = DateObjectExpr.newInstance(date.atStartOfDay());
    } else if (t.equals(ColumnType.LOCAL_TIME)) {
      LocalTime lTime = (LocalTime) obj;
      expr = TimeObjectExpr.newInstance(lTime);
    } else {
      expr = F.stringx(obj.toString());
    }
    return expr;
  }

  @Override
  public IExpr select(IAST ast) {

    IExpr row = ast.arg1();
    IExpr column = ast.arg2();
    IExpr[] part = new IExpr[ast.size() - 3];
    IExpr result = select(row, column);
    if (part.length == 0) {
      return result;
    }
    if (result.isDataset()) {
      for (int i = 0; i < part.length; i++) {
        part[i] = ast.get(i + 3);
      }
      EvalEngine engine = EvalEngine.get();
      ASTDataset dataset = (ASTDataset) result;
      Table table = dataset.fTable;
      final List<String> names = table.columnNames();

      if (names.size() > 0) {
        Table resultTable = Table.create();
        Column<?>[] cols = new Column<?>[names.size()];
        for (int i = 0; i < names.size(); i++) {
          cols[i] = ExprColumn.create(names.get(i));
        }
        resultTable.addColumns(cols);

        // quiet: a part that is not there is the question being answered here, not a fault to
        // report. Part says "Part 1 of {} does not exist" and the cell then records exactly that
        // as Missing(PartAbsent, 1), so the message is the same news twice - and it reached the
        // servlet as an Error line beside a table that had already dealt with it
        boolean quiet = engine.isQuietMode();
        try {
          engine.setQuietMode(true);
          for (int i = 0; i < table.rowCount(); i++) {
            Row currentRow = table.row(i);
            Row resultRow = resultTable.appendRow();
            for (int j = 0; j < table.columnCount(); j++) {
              String columnName = names.get(j);
              ColumnType t = currentRow.getColumnType(columnName);
              IExpr arg = table.column(j).isMissing(i) ? ExprColumnType.missingValueIndicator()
                  : dataToExpr(table.get(i, j), t);

              IExpr value = S.Part.of1(engine, arg, part);
              if (value.isAST(S.Part) || value.isNIL()) {
                IASTAppendable missing = F.ast(S.Missing);
                missing.append(F.$str("PartAbsent"));
                missing.appendAll(part, 0, part.length);
                value = missing;
              }
              resultRow.setExpr(columnName, value);
            }
          }
        } finally {
          engine.setQuietMode(quiet);
        }
        return dataset.derive(resultTable);
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr select(IExpr row, IExpr column) {
    Table table = fTable;
    // A column named on its own reduces the result to a vector - dataset[All, "x"] is
    // Dataset[{1, 3, 5}] in the reference, not a table of one named column. A list of one,
    // dataset[All, {"x"}], keeps the name and stays a table, which is the distinction the
    // reference draws and the reason this is not simply "one column left".
    boolean scalarColumn = false;
    // ... unless the rows are named, in which case it is those values against those names
    boolean keyedColumn = false;

    int[] span = column.isSpan(table.columnCount() - 1);
    if (span != null && span[2] == 1) {
      int columnStart = span[0] - 1;
      int columnEnd = span[1];
      String[] strList = new String[columnEnd - columnStart];
      List<String> columnNames = table.columnNames();
      for (int i = 0; i < strList.length; i++) {
        strList[i] = columnNames.get(i + columnStart);
      }
      table = table.selectColumns(strList);
    } else if (column == S.All) {
    } else if (column.isString()) {
      if (hasRowKeys()) {
        // The rows of this dataset have names, so one of its columns is the values of that column
        // against those names - `Normal[data[All, "z"]]` is `<|"a" -> 3, "b" -> 7|>`, not
        // `{3, 7}`. Keeping the key column in the selection is what carries them: the row
        // selection below slices both columns together, and Shape.ASSOCIATION is exactly a key
        // column beside a value column.
        table = table.selectColumns(KEY_COLUMN, column.toString());
        keyedColumn = true;
      } else {
        table = table.selectColumns(column.toString());
        scalarColumn = true;
      }
    } else if (column.isList()) {
      IAST list = (IAST) column;
      String[] strList = new String[list.argSize()];
      int[] vector = list.toIntVector();
      if (vector == null) {
        for (int i = 0; i < strList.length; i++) {
          strList[i] = list.get(i + 1).toString();
        }
      } else {
        List<String> columnNames = table.columnNames();
        for (int i = 0; i < vector.length; i++) {
          strList[i] = columnNames.get(vector[i] - 1);
        }
      }
      table = table.selectColumns(strList);
    } else {
      int colIndex = column.toIntDefault();
      if (colIndex > 0 && colIndex <= table.columnCount()) {
        table = fTable.selectColumns(table.columnNames().get(colIndex - 1));
        scalarColumn = true;
      } else {
        // out of range is a column that is not there, the same as a name that is not there. It
        // used to index the name list unchecked and throw IndexOutOfBoundsException, which came
        // out as a Failure with a stack trace logged beside it
        return F.NIL;
      }
    }

    span = row.isSpan(table.rowCount() - 1);
    if (span != null && span[2] == 1) {
      int rowStart = span[0] - 1;
      int rowEnd = span[1];
      table = table.inRange(rowStart, rowEnd);
      return deriveColumn(table, scalarColumn, keyedColumn);
    } else if (row == S.All) {
      return deriveColumn(table, scalarColumn, keyedColumn);
    } else if (row.isList()) {
      IAST list = (IAST) row;
      int[] iList = new int[list.argSize()];
      for (int i = 1; i < list.size(); i++) {
        iList[i - 1] = list.get(i).toIntDefault();
        if (iList[i - 1] <= 0) {
          return F.NIL;
        }
        iList[i - 1]--;
      }
      table = rowsInOrder(table, iList);
      if (table.columnCount() == 1 && iList.length > 1 && scalarColumn) {
        return deriveColumn(table, true);
      }
      if (table.columnCount() == 1 && iList.length == 1) {
        // One row and one column is a single value. More than one row is not: this used to return
        // the first cell and drop the rest, so ds[{3,1}, {"name"}] answered with one name.
        return cell(table);
      }
      return derive(table);
    } else {
      int rowIndex = row.toIntDefault();
      if (rowIndex > 0) {
        table = table.rows(rowIndex - 1);
        if (table.columnCount() == 1) {
          return cell(table);
        }
        return derive(table);
      }
    }
    return F.NIL;
  }

  private IExpr select(int row, int column) {
    Table table = fTable;
    Object obj = table.column(column - 1).get(row - 1);
    return Object2Expr.convertString(obj);
  }

  /**
   * Removes all columns except for those given in the <code>list</code>.
   *
   * @param list
   * @return
   */
  private ASTDataset selectColumns(IAST list) {
    String[] strList = new String[list.argSize()];
    int[] vector = list.toIntVector();
    Table table = fTable;
    if (vector == null) {
      for (int i = 0; i < strList.length; i++) {
        strList[i] = list.get(i + 1).toString();
      }
      return derive(table.selectColumns(strList));
    }
    List<String> columnNames = table.columnNames();
    for (int i = 0; i < vector.length; i++) {
      strList[i] = columnNames.get(vector[i] - 1);
    }
    return derive(table.selectColumns(strList));
  }

  /**
   * Removes all columns except for those given in the <code>column</code>.
   *
   * @param column
   * @return
   */
  private ASTDataset selectColumns(int column) {
    String[] strList = new String[1];
    Table table = fTable;
    strList[0] = table.columnNames().get(column - 1);
    return derive(table.selectColumns(strList));
  }

  @Override
  public Table toData() {
    return fTable;
  }

  /**
   * A lone record laid out as name/value pairs, one per line, the way {@link #datasetToJSForm}
   * draws it. Text, so each value is what its own column prints; nothing here draws pictures.
   */
  private static Table transposed(Table table) {
    List<String> names = new ArrayList<String>(table.columnCount());
    List<String> values = new ArrayList<String>(table.columnCount());
    for (Column<?> column : table.columns()) {
      names.add(column.name());
      values.add(column.getString(0));
    }
    Table shown = Table.create();
    // both unnamed, so filled without the duplicate name check - see displayTable
    shown.internalAddWithoutValidation(StringColumn.create("", names));
    shown.internalAddWithoutValidation(StringColumn.create("", values));
    return shown;
  }

  @Override
  public String toString() {
    if (fShape == Shape.VECTOR) {
      // shown without the column's name: a vector has no named field, whether it was built as one
      // or selected out of a table
      return displayTable(fTable.column(0)).printAll();
    }
    if (fShape == Shape.ASSOCIATION) {
      // one line per key, as the browser shows it. Never the two columns it is kept in: the
      // key/value storage this shape happens to use is not something a reader should see
      return displayTable(fTable.column(0), fTable.column(1)).printAll();
    }
    if (fOptions.isAssociationFormat()) {
      // DatasetDisplayFormat -> "Associations" asks for the association itself, whatever shape
      return normal(false).toString();
    }
    // MaxItems, HiddenItems and the two display functions change the table itself, so a console
    // sees them too. The colours and sizes do not: there is nowhere to put CSS in a printed table.
    Table table = fOptions.isDefault() ? fTable : fOptions.apply(fTable);
    if (table.rowCount() == 1 && !hasRowKeys()) {
      // a row of a table reads down the page here too, its field names in the first column
      return transposed(table).printAll();
    }
    return table.printAll();
  }

  @Override
  public IExpr set(int i, IExpr object) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    if (fTable.rowCount() == 1) {
      return fTable.columnCount() + 1;
    }
    return fTable.rowCount() + 1;
  }

  /**
   * The named rows, in the order they were named.
   *
   * <p>
   * Not <code>fTable.rows(int...)</code>, which is the obvious call and is wrong twice over: it
   * selects through a bitmap, so the rows come back in ascending order however they were asked for
   * - <code>ds[{3,1}, All]</code> gave row 1 and then row 3 - and it calls <code>Ints.max</code>,
   * which has no answer for the empty array.
   *
   * @param rows zero-based row indices, in the order wanted
   */
  private Table rowsInOrder(int[] rows) {
    return rowsInOrder(fTable, rows);
  }

  /** {@link #rowsInOrder(int[])} of a table this dataset has already narrowed by column. */
  private static Table rowsInOrder(Table table, int[] rows) {
    Table result = table.emptyCopy();
    for (int row : rows) {
      result.addRow(table.row(row));
    }
    return result;
  }

  @Override
  public int rowCount() {
    return fTable.rowCount();
  }

  @Override
  public ASTDataset randomSample(int n, java.util.Random random) {
    int rowCount = fTable.rowCount();
    int take = Math.min(n, rowCount);
    int[] shuffled = RandomFunctions.shuffledIndices(rowCount, random);
    int[] rows = new int[take];
    System.arraycopy(shuffled, 0, rows, 0, take);
    // through rowsInOrder, so the sample keeps the order it was drawn in. Not Table.sampleN: that
    // draws from a static generator of tablesaw's own, out of reach of SeedRandom, and it rejects
    // a count equal to the row count.
    return derive(rowsInOrder(rows));
  }

  @Override
  public ASTDataset structure() {
    return derive(fTable.structure());
  }

  @Override
  public ASTDataset summary() {
    return derive(fTable.summary());
  }

  @Override
  public IExpr arg1() {
    return get(1);
  }

  @Override
  public IExpr arg2() {
    return get(2);
  }

  @Override
  public IExpr arg3() {
    return get(31);
  }

  @Override
  public IExpr arg4() {
    return get(4);
  }

  @Override
  public IExpr arg5() {
    return get(5);
  }

  @Override
  public IExpr arg6() {
    return get(6);
  }

  @Override
  public IASTAppendable copyAppendable() {
    return normal(false);
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    return normal(false);
  }

  @Override
  public IExpr[] toArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ASTDataset clone() {
    return copy();
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException {
    // the shape goes first: the CSV below carries the storage, and a vector and a one column table
    // are the same storage
    this.fShape = Shape.values()[objectInput.readByte()];
    String str = objectInput.readUTF();
    this.fTable = Table.read().csv(new StringReader(str));
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeByte(fShape.ordinal());
    StringBuilderWriter sw = new StringBuilderWriter();
    this.fTable.write().csv(sw);
    String str = sw.toString();
    if (str.length() >= Config.MAX_OUTPUT_SIZE) {
      throw new MemoryLimitExceeded("String length to big: " + str.length());
    }
    objectOutput.writeUTF(str);
  }

  /**
   * A table built only to be printed, whose columns are the given ones renamed. The names are
   * dropped because neither a vector nor an association has a field to show, and more than one
   * column can therefore end up unnamed - which {@link Table#addColumns} rejects as a duplicate, so
   * the display table is filled without that check. Nothing reads this table back.
   */
  private static Table displayTable(Column<?>... columns) {
    Table shown = Table.create();
    for (Column<?> column : columns) {
      shown.internalAddWithoutValidation(column.copy().setName(""));
    }
    return shown;
  }

  @Override
  public String datasetToJSForm() throws IOException {
    OutputStream out = new ByteArrayOutputStream();
    HtmlWriteOptions.Builder options = HtmlWriteOptions.builder(out).escapeText(true)
        .elementCreator(fOptions.elementCreator()).missingCell(DatasetOptions.MISSING_CELL);
    if (fShape == Shape.VECTOR) {
      // nothing names the column, so there is no header row to write
      displayTable(fTable.column(0)).write().usingOptions(options.showHeader(false).build());
      return out.toString();
    }
    if (fShape == Shape.ASSOCIATION) {
      // one row per key, so the browser shows the association and not the storage. The keys are
      // what names each row, and are written as headers rather than as data
      displayTable(fTable.column(0), fTable.column(1)).write()
          .usingOptions(options.showHeader(false).headerColumns(1).build());
      return out.toString();
    }
    Table table = fOptions.apply(fTable);
    if (table.rowCount() == 1 && !hasRowKeys()) {
      // A lone record reads down the page, its field names in the header column: this is the shape
      // a row of a table has, and every other part of this class already treats it as one - size()
      // is the field count, get(i) is a field, getValue(name) looks one up. Only the drawing said
      // otherwise, showing a header row with a single row beneath it.
      table.write().usingOptions(options.transposed(true).build());
      return out.toString();
    }
    // ... and where the first column names the rows, those names are headers too
    table.write().usingOptions(options.headerColumns(hasRowKeys() ? 1 : 0).build());
    return out.toString();
  }
}
