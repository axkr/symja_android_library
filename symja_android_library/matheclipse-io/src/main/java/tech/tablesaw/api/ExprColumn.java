/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.matheclipse.core.interfaces.IExpr;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntComparator;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.expr.ExprColumnFormatter;
import tech.tablesaw.columns.expr.ExprColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** A column that contains IExpr values. */
public class ExprColumn extends AbstractColumn<ExprColumn, IExpr> {
  static Predicate<IExpr> isMissing = i -> i == ExprColumnType.MISSING_VALUE;

  static Predicate<IExpr> isNotMissing = i -> i != ExprColumnType.MISSING_VALUE;
  public static ExprColumn create(String name) {
    return new ExprColumn(name);
  }

  public static ExprColumn create(String name, Collection<IExpr> strings) {
    return new ExprColumn(name, strings);
  }

  public static ExprColumn create(String name, IExpr... strings) {
    return new ExprColumn(name, strings);
  }

  public static ExprColumn create(String name, int size) {
    ArrayList<IExpr> values = new ArrayList<IExpr>(size);
    for (int i = 0; i < size; i++) {
      values.add(ExprColumnType.missingValueIndicator());
    }
    return new ExprColumn(name, values);
  }

  public static ExprColumn create(String name, Stream<IExpr> stream) {
    ExprColumn column = create(name);
    stream.forEach(column::append);
    return column;
  }

  public static boolean valueIsMissing(IExpr expr) {
    return ExprColumnType.valueIsMissing(expr);
  }

  private ExprColumnFormatter printFormatter = new ExprColumnFormatter();

  // holds each element in the column.
  private ArrayList<IExpr> values;

  private final IntComparator rowComparator =
      (i, i1) -> {
        IExpr f1 = get(i);
        IExpr f2 = get(i1);
        return f1.compareTo(f2);
      };

  private final Comparator<String> descendingStringComparator = Comparator.reverseOrder();

  private ExprColumn(String name) {
    super(ExprColumnType.instance(), name, ExprColumnType.DEFAULT_PARSER);
    values = new ArrayList<IExpr>(DEFAULT_ARRAY_SIZE);
  }

  private ExprColumn(String name, Collection<IExpr> collection) {
    super(ExprColumnType.instance(), name, ExprColumnType.DEFAULT_PARSER);
    values = new ArrayList<IExpr>(collection.size());
    for (IExpr expr : collection) {
      append(expr);
    }
  }

  private ExprColumn(String name, IExpr[] strings) {
    super(ExprColumnType.instance(), name, ExprColumnType.DEFAULT_PARSER);
    values = new ArrayList<IExpr>(strings.length);
    for (IExpr string : strings) {
      append(string);
    }
  }

  /**
   * Add all the strings in the list to this column
   *
   * @param stringValues a list of values
   */
  public ExprColumn addAll(List<IExpr> stringValues) {
    for (IExpr stringValue : stringValues) {
      append(stringValue);
    }
    return this;
  }

  @Override
  public ExprColumn append(Column<IExpr> column) {
    Preconditions.checkArgument(column.type() == this.type());
    ExprColumn source = (ExprColumn) column;
    final int size = source.size();
    for (int i = 0; i < size; i++) {
      append(source.get(i));
    }
    return this;
  }

  @Override
  public Column<IExpr> append(Column<IExpr> column, int row) {
    Preconditions.checkArgument(column.type() == this.type());
    return append(((ExprColumn) column).get(row));
  }

  /** Added for naming consistency with all other columns */
  @Override
  public ExprColumn append(IExpr expr) {
    // appendCell(value);
    values.add(expr);
    return this;
  }

  @Override
  public ExprColumn appendCell(final String value) {
    try {
      return append(ExprColumnType.DEFAULT_PARSER.parse(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

  @Override
  public ExprColumn appendCell(final String value, AbstractColumnParser<?> parser) {
    try {
      return append(parser.parseExpr(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

  @Override
  public ExprColumn appendMissing() {
    append(ExprColumnType.missingValueIndicator());
    return this;
  }

  @Override
  public ExprColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (!(obj instanceof IExpr)) {
      throw new IllegalArgumentException(
          "Cannot append " + obj.getClass().getName() + " to TextColumn");
    }
    return append((IExpr) obj);
  }

  /** Returns the contents of the cell at rowNumber as a byte[] */
  @Override
  public byte[] asBytes(int rowNumber) {
    return new byte[0];
    // TODO (lwhite): FIX ME: return
    // ByteBuffer.allocate(byteSize()).putInt(getInt(rowNumber)).array();
  }

  /**
   * Returns a List&lt;String&gt; representation of all the values in this column
   *
   * <p>NOTE: Unless you really need a string consider using the column itself for large datasets as
   * it uses much less memory
   *
   * @return values as a list of String.
   */
  @Override
  public List<IExpr> asList() {

    return new ArrayList<>(values);
  }

  @Override
  public IExpr[] asObjectArray() {
    final IExpr[] output = new IExpr[size()];
    for (int i = 0; i < size(); i++) {
      output[i] = get(i);
    }
    return output;
  }

  @Override
  public Set<IExpr> asSet() {
    return new HashSet<>(values);
  }

  @Override
  public StringColumn asStringColumn() {
    StringColumn textColumn = StringColumn.create(name(), size());
    for (int i = 0; i < size(); i++) {
      textColumn.set(i, get(i).toString());
    }
    return textColumn;
  }

  @Override
  public int byteSize() {
    return 0;
  }

  @Override
  public void clear() {
    values.clear();
  }

  @Override
  public int compare(IExpr o1, IExpr o2) {
    return o1.compareTo(o2);
  }

  /**
   * Returns true if this column contains a cell with the given string, and false otherwise
   *
   * @param aString the value to look for
   * @return true if contains, false otherwise
   */
  @Override
  public boolean contains(IExpr aString) {
    return values.contains(aString);
  }

  // TODO (lwhite): This could avoid the append and do a list copy
  @Override
  public ExprColumn copy() {
    ExprColumn newCol = create(name(), size());
    int r = 0;
    for (IExpr string : this) {
      newCol.set(r, string);
      r++;
    }
    return newCol;
  }

  /** Returns the count of missing values in this column */
  @Override
  public int countMissing() {
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (ExprColumnType.missingValueIndicator().equals(get(i))) {
        count++;
      }
    }
    return count;
  }

  @Override
  public int countUnique() {
    return asSet().size();
  }

  @Override
  public ExprColumn emptyCopy() {
    return create(name());
  }

  @Override
  public ExprColumn emptyCopy(int rowSize) {
    return create(name(), rowSize);
  }

  // @Override
  // public ExprColumn appendCell(String object) {
  // values.add(ExprColumnType.DEFAULT_PARSER.parse(object));
  // return this;
  // }

  // @Override
  // public ExprColumn appendCell(String object, AbstractColumnParser<?> parser) {
  // return appendObj(parser.parse(object));
  // }

  @Override
  public boolean equals(int rowNumber1, int rowNumber2) {
    return get(rowNumber1).equals(get(rowNumber2));
  }

  public Selection eval(Predicate<IExpr> predicate) {
    Selection selection = new BitmapBackedSelection();
    for (int idx = 0; idx < values.size(); idx++) {
      if (predicate.test(get(idx))) {
        selection.add(idx);
      }
    }
    return selection;
  }

  public int firstIndexOf(IExpr value) {
    return values.indexOf(value);
  }

  /**
   * Returns the value at rowIndex in this column. The index is zero-based.
   *
   * @param rowIndex index of the row
   * @return value as String
   * @throws IndexOutOfBoundsException if the given rowIndex is not in the column
   */
  @Override
  public IExpr get(int rowIndex) {
    return values.get(rowIndex);
  }

  public ExprColumnFormatter getPrintFormatter() {
    return printFormatter;
  }

  @Override
  public String getString(int row) {
    final IExpr value = get(row);
    if (ExprColumnType.valueIsMissing(value)) {
      return "";
    }
    return String.valueOf(getPrintFormatter().format(value));
  }

  @Override
  public String getUnformattedString(int row) {
    return get(row).toString();
  }

  @Override
  public boolean isEmpty() {
    return values.isEmpty();
  }

  public Selection isIn(Collection<IExpr> strings) {
    Set<IExpr> stringSet = Sets.newHashSet(strings);

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      if (stringSet.contains(values.get(i))) {
        results.add(i);
      }
    }
    return results;
  }

  public Selection isIn(IExpr... strings) {
    Set<IExpr> stringSet = Sets.newHashSet(strings);

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      if (stringSet.contains(values.get(i))) {
        results.add(i);
      }
    }
    return results;
  }

  @Override
  public Selection isMissing() {
    return eval(isMissing);
  }

  @Override
  public boolean isMissing(int rowNumber) {
    return get(rowNumber).equals(ExprColumnType.missingValueIndicator());
  }

  public Selection isNotIn(Collection<IExpr> strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
    return results;
  }

  public Selection isNotIn(IExpr... strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
    return results;
  }

  @Override
  public Selection isNotMissing() {
    return eval(isNotMissing);
  }

  @Override
  public Iterator<IExpr> iterator() {
    return values.iterator();
  }

  @Override
  public ExprColumn lag(int n) {

    ExprColumn copy = emptyCopy();
    copy.setName(name() + " lag(" + n + ")");

    if (n >= 0) {
      for (int m = 0; m < n; m++) {
        copy.append(ExprColumnType.missingValueIndicator());
      }
      for (int i = 0; i < size(); i++) {
        if (i + n >= size()) {
          break;
        }
        // copy.appendCell(get(i));
        copy.append(get(i));
      }
    } else {
      for (int i = -n; i < size(); i++) {
        // copy.appendCell(get(i));
        copy.append(get(i));
      }
      for (int m = 0; m > n; m--) {
        copy.append(ExprColumnType.missingValueIndicator());
      }
    }

    return copy;
  }

  @Override
  public ExprColumn lead(int n) {
    ExprColumn column = lag(-n);
    column.setName(name() + " lead(" + n + ")");
    return column;
  }

  @Override
  public ExprColumn removeMissing() {
    ExprColumn noMissing = emptyCopy();
    for (IExpr v : this) {
      if (!ExprColumnType.valueIsMissing(v)) {
        noMissing.append(v);
      }
    }
    return noMissing;
  }

  @Override
  public IntComparator rowComparator() {
    return rowComparator;
  }

  @Override
  public Column<IExpr> set(int row, Column<IExpr> column, int sourceRow) {
    Preconditions.checkArgument(column.type() == this.type());
    return set(row, ((ExprColumn) column).get(sourceRow));
  }

  @Override
  public ExprColumn set(int rowIndex, IExpr stringValue) {
    if (stringValue == null) {
      return setMissing(rowIndex);
    }
    values.set(rowIndex, stringValue);
    return this;
  }

  /**
   * Conditionally update this column, replacing current values with newValue for all rows where the
   * current value matches the selection criteria
   *
   * <p>Examples: myCatColumn.set(myCatColumn.isEqualTo("Cat"), "Dog"); // no more cats
   * myCatColumn.set(myCatColumn.valueIsMissing(), "Fox"); // no more missing values
   */
  @Override
  public ExprColumn set(Selection rowSelection, IExpr newValue) {
    for (int row : rowSelection) {
      set(row, newValue);
    }
    return this;
  }

  @Override
  public ExprColumn setMissing(int i) {
    return set(i, ExprColumnType.missingValueIndicator());
  }

  public void setPrintFormatter(ExprColumnFormatter formatter) {
    Preconditions.checkNotNull(formatter);
    this.printFormatter = formatter;
  }

  /**
   * Returns the number of elements (a.k.a. rows or cells) in the column
   *
   * @return size as int
   */
  @Override
  public int size() {
    return values.size();
  }

  @Override
  public void sortAscending() {
    values.sort(
        new Comparator<IExpr>() {
          @Override
          public int compare(IExpr o1, IExpr o2) {
            return o1.compareTo(o2);
          }
        });
  }

  @Override
  public void sortDescending() {
    values.sort(
        new Comparator<IExpr>() {
          @Override
          public int compare(IExpr o1, IExpr o2) {
            return o2.compareTo(o1);
          }
        });
  }

  @Override
  public Table summary() {
    Table table = Table.create("Column: " + name());
    StringColumn measure = StringColumn.create("Measure");
    StringColumn value = StringColumn.create("Value");
    table.addColumns(measure);
    table.addColumns(value);

    measure.append("Count");
    value.append(String.valueOf(size()));

    measure.append("Missing");
    value.append(String.valueOf(countMissing()));
    return table;
  }

  /**
   * Returns a new Column containing all the unique values in this column
   *
   * @return a column with unique values.
   */
  @Override
  public ExprColumn unique() {
    List<IExpr> strings = new ArrayList<>(asSet());
    return ExprColumn.create(name() + " Unique values", strings);
  }

  @Override
  public int valueHash(int rowNumber) {
    return get(rowNumber).hashCode();
  }

  @Override
  public ExprColumn where(Selection selection) {
    return subset(selection.toArray());
  }
}
