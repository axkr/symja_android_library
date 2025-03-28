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

import static tech.tablesaw.api.ColumnType.STRING;
import static tech.tablesaw.api.ColumnType.TEXT;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.AbstractStringColumn;
import tech.tablesaw.columns.strings.TextColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * A column that contains String values. They are assumed to be free-form text. For categorical
 * data, use stringColumn
 *
 * <p>This is the default column type for SQL longvarchar and longnvarchar types
 *
 * <p>Because the MISSING_VALUE for this column type is an empty string, there is little or no need
 * for special handling of missing values in this class's methods.
 */
public class TextColumn extends AbstractStringColumn<TextColumn> {

  // holds each element in the column.
  protected List<String> values;

  private final IntComparator rowComparator =
      (i, i1) -> {
        String f1 = get(i);
        String f2 = get(i1);
        return f1.compareTo(f2);
      };

  private final Comparator<String> descendingStringComparator = Comparator.reverseOrder();

  /** {@inheritDoc} */
  @Override
  public int valueHash(int rowNumber) {
    return get(rowNumber).hashCode();
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(int rowNumber1, int rowNumber2) {
    return get(rowNumber1).equals(get(rowNumber2));
  }

  private TextColumn(String name, Collection<String> strings) {
    super(TextColumnType.instance(), name, TextColumnType.DEFAULT_PARSER);
    values = new ArrayList<>(strings.size());
    for (String string : strings) {
      append(string);
    }
  }

  private TextColumn(String name) {
    super(TextColumnType.instance(), name, TextColumnType.DEFAULT_PARSER);
    values = new ArrayList<>(DEFAULT_ARRAY_SIZE);
  }

  private TextColumn(String name, String[] strings) {
    super(TextColumnType.instance(), name, TextColumnType.DEFAULT_PARSER);
    values = new ArrayList<>(strings.length);
    for (String string : strings) {
      append(string);
    }
  }

  public static boolean valueIsMissing(String string) {
    return TextColumnType.valueIsMissing(string);
  }

  @Override
  public TextColumn appendMissing() {
    append(TextColumnType.missingValueIndicator());
    return this;
  }

  public static TextColumn create(String name) {
    return new TextColumn(name);
  }

  public static TextColumn create(String name, String... strings) {
    return new TextColumn(name, strings);
  }

  public static TextColumn create(String name, Collection<String> strings) {
    return new TextColumn(name, strings);
  }

  public static TextColumn create(String name, int size) {
    ArrayList<String> strings = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      strings.add(TextColumnType.missingValueIndicator());
    }
    return new TextColumn(name, strings);
  }

  public static TextColumn create(String name, Stream<String> stream) {
    TextColumn column = create(name);
    stream.forEach(column::append);
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMissing(int rowNumber) {
    return get(rowNumber).equals(TextColumnType.missingValueIndicator());
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn emptyCopy() {
    TextColumn empty = create(name());
    empty.setPrintFormatter(getPrintFormatter());
    return empty;
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn emptyCopy(int rowSize) {
    return create(name(), rowSize);
  }

  /** {@inheritDoc} */
  @Override
  public void sortAscending() {
    values.sort(String::compareTo);
  }

  /** {@inheritDoc} */
  @Override
  public void sortDescending() {
    values.sort(descendingStringComparator);
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

  /**
   * Returns the value at rowIndex in this column. The index is zero-based.
   *
   * @param rowIndex index of the row
   * @return value as String
   * @throws IndexOutOfBoundsException if the given rowIndex is not in the column
   */
  @Override
  public String get(int rowIndex) {
    return values.get(rowIndex);
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
  public List<String> asList() {
    return new ArrayList<>(values);
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public void clear() {
    values.clear();
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn lead(int n) {
    TextColumn column = lag(-n);
    column.setName(name() + " lead(" + n + ")");
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn lag(int n) {

    TextColumn copy = emptyCopy();
    copy.setName(name() + " lag(" + n + ")");

    if (n >= 0) {
      for (int m = 0; m < n; m++) {
        copy.appendMissing();
      }
      for (int i = 0; i < size(); i++) {
        if (i + n >= size()) {
          break;
        }
        copy.append(get(i));
      }
    } else {
      for (int i = -n; i < size(); i++) {
        copy.append(get(i));
      }
      for (int m = 0; m > n; m--) {
        copy.appendMissing();
      }
    }

    return copy;
  }

  /**
   * Conditionally update this column, replacing current values with newValue for all rows where the
   * current value matches the selection criteria
   *
   * <p>Examples: myCatColumn.set(myCatColumn.isEqualTo("Cat"), "Dog"); // no more cats
   * myCatColumn.set(myCatColumn.valueIsMissing(), "Fox"); // no more missing values
   */
  @Override
  public TextColumn set(Selection rowSelection, String newValue) {
    for (int row : rowSelection) {
      set(row, newValue);
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn set(int rowIndex, String stringValue) {
    if (stringValue == null) {
      return setMissing(rowIndex);
    }
    values.set(rowIndex, stringValue);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public int countUnique() {
    return asSet().size();
  }

  /**
   * Returns true if this column contains a cell with the given string, and false otherwise
   *
   * @param aString the value to look for
   * @return true if contains, false otherwise
   */
  @Override
  public boolean contains(String aString) {
    return values.contains(aString);
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn setMissing(int i) {
    return set(i, TextColumnType.missingValueIndicator());
  }

  /**
   * Add all the strings in the list to this column
   *
   * @param stringValues a list of values
   */
  public TextColumn addAll(List<String> stringValues) {
    for (String stringValue : stringValues) {
      append(stringValue);
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn appendCell(String object) {
    append(parser().parse(object));
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn appendCell(String object, AbstractColumnParser<?> parser) {
    append(String.valueOf(parser.parse(object)));
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public IntComparator rowComparator() {
    return rowComparator;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEmpty() {
    return values.isEmpty();
  }

  /**
   * Returns a new Column containing all the unique values in this column
   *
   * @return a column with unique values.
   */
  @Override
  public TextColumn unique() {
    List<String> strings = new ArrayList<>(asSet());
    return TextColumn.create(name() + " Unique values", strings);
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn where(Selection selection) {
    return subset(selection.toArray());
  }

  // TODO (lwhite): This could avoid the append and do a list copy
  /** {@inheritDoc} */
  @Override
  public TextColumn copy() {
    TextColumn newCol = create(name(), size());
    int r = 0;
    for (String string : this) {
      newCol.set(r, string);
      r++;
    }
    return newCol;
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn append(Column<String> column) {
    Preconditions.checkArgument(
        column.type() == TEXT || column.type().equals(STRING),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    final int size = column.size();
    for (int i = 0; i < size; i++) {
      append(column.getString(i));
    }
    return this;
  }

  /** Returns the count of missing values in this column */
  @Override
  public int countMissing() {
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (TextColumnType.missingValueIndicator().equals(get(i))) {
        count++;
      }
    }
    return count;
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn removeMissing() {
    TextColumn noMissing = emptyCopy();
    for (String v : this) {
      if (!TextColumnType.valueIsMissing(v)) {
        noMissing.append(v);
      }
    }
    return noMissing;
  }

  /** {@inheritDoc} */
  @Override
  public Iterator<String> iterator() {
    return values.iterator();
  }

  /** {@inheritDoc} */
  @Override
  public Set<String> asSet() {
    return new HashSet<>(values);
  }

  /** Returns the contents of the cell at rowNumber as a byte[] */
  @Override
  public byte[] asBytes(int rowNumber) {
    String value = get(rowNumber);
    return value.getBytes();
  }

  /** Added for naming consistency with all other columns */
  @Override
  public TextColumn append(String value) {
    values.add(value);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public TextColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (!(obj instanceof String)) {
      throw new IllegalArgumentException(
          "Cannot append " + obj.getClass().getName() + " to TextColumn");
    }
    return append((String) obj);
  }

  /** {@inheritDoc} */
  @Override
  public Selection isIn(String... strings) {
    Set<String> stringSet = Sets.newHashSet(strings);

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      if (stringSet.contains(values.get(i))) {
        results.add(i);
      }
    }
    return results;
  }

  /** {@inheritDoc} */
  @Override
  public Selection isIn(Collection<String> strings) {
    Set<String> stringSet = Sets.newHashSet(strings);

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      if (stringSet.contains(values.get(i))) {
        results.add(i);
      }
    }
    return results;
  }

  /** {@inheritDoc} */
  @Override
  public Selection isNotIn(String... strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
    return results;
  }

  /** {@inheritDoc} */
  @Override
  public Selection isNotIn(Collection<String> strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
    return results;
  }

  public int firstIndexOf(String value) {
    return values.indexOf(value);
  }

  /** {@inheritDoc} */
  @Override
  public String[] asObjectArray() {
    final String[] output = new String[size()];
    for (int i = 0; i < size(); i++) {
      output[i] = get(i);
    }
    return output;
  }

  /** {@inheritDoc} */
  @Override
  public StringColumn asStringColumn() {
    StringColumn textColumn = StringColumn.create(name(), size());
    for (int i = 0; i < size(); i++) {
      textColumn.set(i, get(i));
    }
    return textColumn;
  }
}
