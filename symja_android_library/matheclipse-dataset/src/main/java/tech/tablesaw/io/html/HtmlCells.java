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

package tech.tablesaw.io.html;

/**
 * A column that can render some of its cells as markup rather than as text.
 *
 * <p>
 * A Symja addition to the fork, for a column holding values that have a picture of their own - a
 * <code>Graphics</code> in an {@link tech.tablesaw.api.ExprColumn} is written as the SVG it draws
 * instead of as the source text of the expression.
 *
 * <p>
 * The markup is written out verbatim, which is why this is a decision the <b>column</b> makes and
 * not one a caller can ask for: it must only ever be markup the column produced itself, never text
 * that came in with the data. Ordinary cells are escaped as before, per
 * {@link HtmlWriteOptions#escapeText()}.
 */
public interface HtmlCells {

  /**
   * The markup for one cell, or <code>null</code> when the cell is ordinary text and should be
   * written the usual way.
   */
  String htmlCell(int row);

  /**
   * Whether this cell should be drawn as a missing one. Separate from
   * {@link tech.tablesaw.columns.Column#isMissing(int)}, which the writer asks as well: that one
   * decides what counting, removing and the aggregations treat as absent, and a column may want to
   * <b>show</b> more than that as missing without changing any of them.
   */
  default boolean cellIsMissing(int row) {
    return false;
  }
}
