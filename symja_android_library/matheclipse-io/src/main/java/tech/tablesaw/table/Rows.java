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

package tech.tablesaw.table;

import com.google.errorprone.annotations.Immutable;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * A static utility class for row operations
 *
 * @deprecated Functionality provided by this class is methods in the {@link
 *     tech.tablesaw.api.Table} class hierarchy, and/or by methods in {@link tech.tablesaw.api.Row}
 */
@Immutable
@Deprecated
public final class Rows {

  // Don't instantiate
  private Rows() {}

  /**
   * Copies the rows indicated by the row index values in the given selection from oldTable to
   * newTable
   *
   * @deprecated Use the instance method {Table:where(Selection} instead
   */
  @Deprecated
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void copyRowsToTable(Selection rows, Table oldTable, Table newTable) {
    for (int columnIndex = 0; columnIndex < oldTable.columnCount(); columnIndex++) {
      Column oldColumn = oldTable.column(columnIndex);
      int r = 0;
      for (int i : rows) {
        newTable.column(columnIndex).set(r, oldColumn, i);
        r++;
      }
    }
  }

  /**
   * Copies the rows indicated by the row index values in the given array from oldTable to newTable
   *
   * @deprecated Use the instance method {@link tech.tablesaw.api.Table#copyRowsToTable(int[],
   *     Table)} instead
   */
  @Deprecated
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void copyRowsToTable(int[] rows, Table oldTable, Table newTable) {
    for (int columnIndex = 0; columnIndex < oldTable.columnCount(); columnIndex++) {
      Column oldColumn = oldTable.column(columnIndex);
      int r = 0;
      for (int i : rows) {
        newTable.column(columnIndex).set(r, oldColumn, i);
        r++;
      }
    }
  }

  /**
   * Appends a row from oldTable to newTable
   *
   * @deprecated Use the instance method {@link tech.tablesaw.api.Table#append(Row)} instead
   */
  @Deprecated
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void appendRowToTable(int row, Table oldTable, Table newTable) {
    int[] rows = new int[] {row};
    for (int columnIndex = 0; columnIndex < oldTable.columnCount(); columnIndex++) {
      Column oldColumn = oldTable.column(columnIndex);
      for (int i : rows) {
        newTable.column(columnIndex).append(oldColumn, i);
      }
    }
  }

  /**
   * @deprecated Use the static method {@link tech.tablesaw.api.Table#compareRows(int, Table,
   *     Table)} instead
   */
  @Deprecated
  public static boolean compareRows(int rowInOriginal, Table original, Table tempTable) {
    int columnCount = original.columnCount();
    boolean result;
    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
      ColumnType columnType = original.column(columnIndex).type();
      result =
          columnType.compare(
              rowInOriginal, tempTable.column(columnIndex), original.column(columnIndex));
      if (!result) {
        return false;
      }
    }
    return true;
  }

  /**
   * Copies the first n rows to a new table
   *
   * @deprecated Use {@link tech.tablesaw.api.Table#first(int)} instead
   */
  @Deprecated
  public static void head(int rowCount, Table oldTable, Table newTable) {
    Selection rows = new BitmapBackedSelection(rowCount);
    for (int i = 0; i < rowCount; i++) {
      rows.add(i);
    }
    copyRowsToTable(rows, oldTable, newTable);
  }

  /**
   * Copies the last n rows to a new table
   *
   * @deprecated Use {@link tech.tablesaw.api.Table#last(int)} instead
   */
  @Deprecated
  public static void tail(int rowsToInclude, Table oldTable, Table newTable) {
    int oldTableSize = oldTable.rowCount();
    int start = oldTableSize - rowsToInclude;
    Selection rows = new BitmapBackedSelection(rowsToInclude);
    for (int i = start; i < oldTableSize; i++) {
      rows.add(i);
    }
    copyRowsToTable(rows, oldTable, newTable);
  }
}
