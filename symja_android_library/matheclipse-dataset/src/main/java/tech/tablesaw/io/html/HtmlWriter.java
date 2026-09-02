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

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.RuntimeIOException;
import tech.tablesaw.io.WriterRegistry;
import tech.tablesaw.io.html.HtmlWriteOptions.ElementCreator;

public class HtmlWriter implements DataWriter<HtmlWriteOptions> {

  private static final HtmlWriter INSTANCE = new HtmlWriter();

  static {
    register(Table.defaultWriterRegistry);
  }

  public static void register(WriterRegistry registry) {
    registry.registerExtension("html", INSTANCE);
    registry.registerOptions(HtmlWriteOptions.class, INSTANCE);
  }

  public void write(Table table, HtmlWriteOptions options) {
    ElementCreator elements = options.elementCreator();
    Element html = elements.create("table");
    if (options.showHeader() && !options.transposed()) {
      html.appendChild(header(table.columns(), elements));
    }

    Element tbody = elements.create("tbody");
    html.appendChild(tbody);
    if (options.transposed()) {
      // one row per column: the name, then the column's single cell
      for (int column = 0; column < table.columnCount(); column++) {
        Column<?> col = table.column(column);
        Element tr = elements.create("tr", null, column);
        tr.appendChild(elements.create("th", col, null).appendText(col.name()));
        tr.appendChild(cell("td", col, 0, elements, options));
        tbody.appendChild(tr);
      }
    } else {
      for (int row = 0; row < table.rowCount(); row++) {
        tbody.appendChild(row(row, table, elements, options));
      }
    }

    try (Writer writer = options.destination().createWriter()) {
      writer.write(html.toString());
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
  }

  /** Returns a string containing the html output of one table row */
  private static Element row(
      int row, Table table, ElementCreator elements, HtmlWriteOptions options) {
    Element tr = elements.create("tr", null, row);
    int index = 0;
    for (Column<?> col : table.columns()) {
      // the leading columns of a table that names its rows are headers, not data
      String name = index++ < options.headerColumns() ? "th" : "td";
      tr.appendChild(cell(name, col, row, elements, options));
    }
    return tr;
  }

  /** One cell, as the column's own markup where it has some and as text otherwise. */
  private static Element cell(
      String name, Column<?> col, int row, ElementCreator elements, HtmlWriteOptions options) {
    if (options.missingCell() != null && isMissing(col, row)) {
      return elements.create(name, col, row).appendChild(new DataNode(options.missingCell()));
    }
    String markup = col instanceof HtmlCells ? ((HtmlCells) col).htmlCell(row) : null;
    if (markup != null) {
      // written through a DataNode, which is emitted verbatim: the markup is the column's own
      // and must not be escaped, nor re-parsed - jsoup's HTML parser lower cases attribute
      // names, which would turn an SVG's viewBox into viewbox and break its scaling
      return elements.create(name, col, row).appendChild(new DataNode(markup));
    }
    if (options.escapeText()) {
      return elements.create(name, col, row).appendText(String.valueOf(col.getString(row)));
    }
    return elements.create(name, col, row)
        .appendChild(new DataNode(String.valueOf(col.getString(row))));
  }

  /** Missing as the column counts it, and missing as the column wants it drawn. */
  private static boolean isMissing(Column<?> col, int row) {
    return col.isMissing(row) || (col instanceof HtmlCells && ((HtmlCells) col).cellIsMissing(row));
  }

  private static Element header(List<Column<?>> cols, ElementCreator elements) {
    Element thead = elements.create("thead");
    Element tr = elements.create("tr");
    thead.appendChild(tr);
    for (Column<?> col : cols) {
      tr.appendChild(elements.create("th", col, null).appendText(col.name()));
    }
    return thead;
  }

  @Override
  public void write(Table table, Destination dest) {
    write(table, HtmlWriteOptions.builder(dest).build());
  }
}
