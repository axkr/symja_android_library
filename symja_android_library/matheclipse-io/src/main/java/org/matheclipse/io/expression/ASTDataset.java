package org.matheclipse.io.expression;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.jsoup.nodes.Element;
import org.matheclipse.core.basic.Config;
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
      // 2. phase: define the columns
      Table table = Table.create();
      Column<?>[] cols = new Column<?>[colNames.size()];
      for (int i = 0; i < colNames.size(); i++) {
        cols[i] = ExprColumn.create(colNames.get(i));
      }
      table.addColumns(cols);
      // 3. phase: add the values
      for (int i = 1; i < listOfAssociations.size(); i++) {
        IAssociation assoc = (IAssociation) listOfAssociations.get(i);
        Row row = table.appendRow();
        for (int j = 1; j < assoc.size(); j++) {
          IAST rule = assoc.getRule(j);
          String columnName = rule.first().toString();
          IExpr value = rule.second();
          row.setExpr(columnName, value);
        }
      }
      return newTablesawTable(table);
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
      // 2. phase: define the columns
      Table table = Table.create();
      Column<?>[] cols = new Column<?>[colNames.size()];
      for (int i = 0; i < colNames.size(); i++) {
        cols[i] = ExprColumn.create(colNames.get(i));
      }
      table.addColumns(cols);
      // 3. phase: add the values
      for (int i = 1; i < assocOfAssociations.size(); i++) {
        IExpr rule = assocOfAssociations.getRule(i);
        IAssociation assoc = (IAssociation) rule.second();
        Row row = table.appendRow();
        row.setExpr("", rule.first());
        for (int j = 1; j < assoc.size(); j++) {
          rule = assoc.getRule(j);
          String columnName = rule.first().toString();
          IExpr value = rule.second();
          row.setExpr(columnName, value);
        }
      }
      return newTablesawTable(table);
    }
    return F.NIL;
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

  protected transient Table fTable;

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
    return new ASTDataset(fTable);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ASTDataset) {
      return fTable.equals(((ASTDataset) obj).fTable);
    }
    return false;
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
    if (fTable.rowCount() == 1) {
      return getColumnValue(0, location - 1);
    }
    return newTablesawTable(fTable.rows(location - 1));
  }

  private IExpr getColumnValue(int rowPosition, int columnPosition) {
    Column<?> column = fTable.column(columnPosition);
    ColumnType t = column.type();
    Object obj = fTable.get(rowPosition, columnPosition);
    if (t.equals(ColumnType.BOOLEAN)) {
      Boolean b = (Boolean) obj;
      if (b.booleanValue()) {
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
  public IAST getItems(int[] items, int length) {
    if (length <= 0) {
      return newTablesawTable(Table.create(fTable.name()));
    }
    int[] rows = new int[length];
    for (int i = 0; i < length; i++) {
      rows[i] = items[i] - 1;
    }
    return newTablesawTable(fTable.rows(rows));
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
    final String keyName = key.toString();
    if (fTable.rowCount() == 1) {
      int columnIndex = fTable.columnIndex(keyName);
      if (columnIndex < 0) {
        return defaultValue.get();
      }
      return select(1, columnIndex + 1);
    }
    String[] strList = new String[] {keyName};
    Table table = fTable.select(strList);
    if (table.columnCount() == 0) {
      return defaultValue.get();
    }
    return newTablesawTable(table);
  }

  public IExpr sortBy(List<String> group) {
    String[] strings = new String[group.size()];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = group.get(i);
    }
    Table table = fTable.sortAscendingOn(strings);
    return newTablesawTable(table);
  }

  @Override
  public IExpr groupBy(List<String> group) {
    String[] strings = new String[group.size()];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = group.get(i);
    }
    Table table = fTable.sortAscendingOn(strings);
    return newTablesawTable(table);
  }

  @Override
  public int hashCode() {
    return (fTable == null) ? 59 : 59 + fTable.hashCode();
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
        Object obj = column.get(j);
        IExpr expr = F.NIL;
        expr = dataToExpr(obj, t);
        resultList.append(expr);
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
        Object obj = row.getObject(j);
        if (t.equals(ColumnType.EXPR)) {
          IExpr expr = (IExpr) obj;
          ruleCache(cache, assoc, F.Rule(colName, expr));
        } else if (t.equals(ColumnType.BOOLEAN)) {
          Boolean b = row.getBoolean(j);
          if (b.booleanValue()) {
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
    return list;
  }

  private static IExpr dataToExpr(Object obj, ColumnType t) {
    IExpr expr;
    if (t.equals(ColumnType.EXPR)) {
      expr = (IExpr) obj;
    } else if (t.equals(ColumnType.BOOLEAN)) {
      Boolean b = (Boolean) obj;
      if (b.booleanValue()) {
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

        for (int i = 0; i < table.rowCount(); i++) {
          Row currentRow = table.row(i);
          Row resultRow = resultTable.appendRow();
          for (int j = 0; j < table.columnCount(); j++) {
            String columnName = names.get(j);
            ColumnType t = currentRow.getColumnType(columnName);
            IExpr arg = dataToExpr(table.get(i, j), t);

            IExpr value = S.Part.of1(engine, arg, part);
            if (value.isAST(S.Part) || !value.isPresent()) {
              IASTAppendable missing = F.ast(S.Missing);
              missing.append(F.$str("PartAbsent"));
              missing.appendAll(part, 0, part.length);
              value = missing;
            }
            resultRow.setExpr(columnName, value);
          }
        }
        return ASTDataset.newTablesawTable(resultTable);
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr select(IExpr row, IExpr column) {
    Table table = fTable;

    int[] span = column.isSpan(table.columnCount() - 1);
    if (span != null && span[2] == 1) {
      int columnStart = span[0] - 1;
      int columnEnd = span[1];
      String[] strList = new String[columnEnd - columnStart];
      List<String> columnNames = table.columnNames();
      for (int i = 0; i < strList.length; i++) {
        strList[i] = columnNames.get(i + columnStart);
      }
      table = table.select(strList);
    } else if (column.equals(S.All)) {
    } else if (column.isString()) {
      table = table.select(column.toString());
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
      table = table.select(strList);
    } else {
      int colIndex = column.toIntDefault();
      if (colIndex > 0) {
        table = fTable.select(table.columnNames().get(colIndex - 1));
      } else {
        return F.NIL;
      }
    }

    span = row.isSpan(table.rowCount() - 1);
    if (span != null && span[2] == 1) {
      int rowStart = span[0] - 1;
      int rowEnd = span[1];
      table = table.inRange(rowStart, rowEnd);
      return newTablesawTable(table);
    } else if (row.equals(S.All)) {
      return newTablesawTable(table);
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
      table = table.rows(iList);
      if (table.columnCount() == 1) {
        return Object2Expr.convertString(table.get(0, 0));
      }
      return newTablesawTable(table);
    } else {
      int rowIndex = row.toIntDefault();
      if (rowIndex > 0) {
        table = table.rows(rowIndex - 1);
        if (table.columnCount() == 1) {
          return Object2Expr.convertString(table.get(0, 0));
        }
        return newTablesawTable(table);
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
      return newTablesawTable(table.select(strList));
    }
    List<String> columnNames = table.columnNames();
    for (int i = 0; i < vector.length; i++) {
      strList[i] = columnNames.get(vector[i] - 1);
    }
    return newTablesawTable(table.select(strList));
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
    return newTablesawTable(table.select(strList));
  }

  @Override
  public Table toData() {
    return fTable;
  }

  @Override
  public String toString() {
    return fTable.printAll();
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

  @Override
  public ASTDataset structure() {
    return newTablesawTable(fTable.structure());
  }

  @Override
  public ASTDataset summary() {
    return newTablesawTable(fTable.summary());
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
    String str = objectInput.readUTF();
    this.fTable = Table.read().csv(new StringReader(str));
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    StringWriter sw = new StringWriter();
    this.fTable.write().csv(sw);
    String str = sw.toString();
    if (str.length() >= Config.MAX_OUTPUT_SIZE) {
      throw new MemoryLimitExceeded("String length to big: " + str.length());
    }
    objectOutput.writeUTF(str);
  }

  @Override
  public String datasetToJSForm() throws IOException {
    OutputStream baos = new ByteArrayOutputStream();
    fTable.write().usingOptions(HtmlWriteOptions.builder(baos).escapeText(true)
        .elementCreator((elementName, column, row) -> new Element(elementName)).build());
    return baos.toString();
  }
}
