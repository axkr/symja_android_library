package tech.tablesaw.io.html;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.jsoup.nodes.Element;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriteOptions;

public class HtmlWriteOptions extends WriteOptions {

  private final ElementCreator elementCreator;
  private final boolean escapeText;
  private final boolean showHeader;
  private final int headerColumns;
  private final boolean transposed;
  private final String missingCell;

  protected HtmlWriteOptions(Builder builder) {
    super(builder);
    this.escapeText = builder.escapeText;
    this.elementCreator = builder.elementCreator;
    this.showHeader = builder.showHeader;
    this.headerColumns = builder.headerColumns;
    this.transposed = builder.transposed;
    this.missingCell = builder.missingCell;
  }

  public boolean escapeText() {
    return escapeText;
  }

  /**
   * The markup a missing cell is written as, or <code>null</code> to write it the ordinary way -
   * as the empty text the column prints for it.
   */
  public String missingCell() {
    return missingCell;
  }

  /** Whether the column names are written as a header row. */
  public boolean showHeader() {
    return showHeader;
  }

  /**
   * How many columns on the left are written as <code>th</code> rather than <code>td</code>, for a
   * table whose first column names its rows instead of holding data.
   */
  public int headerColumns() {
    return headerColumns;
  }

  /**
   * Whether to write one output row per column - the column's name in a <code>th</code>, then its
   * single cell - rather than one per row. Only meaningful for a table of one row, which is how a
   * lone record is kept: it reads down the page as name/value pairs instead of across as a header
   * row with one row under it.
   */
  public boolean transposed() {
    return transposed;
  }

  public ElementCreator elementCreator() {
    return elementCreator;
  }

  public static Builder builder(Destination dest) {
    return new Builder(dest);
  }

  public static Builder builder(OutputStream dest) {
    return new Builder(dest);
  }

  public static Builder builder(Writer dest) {
    return new Builder(dest);
  }

  public static Builder builder(File dest) throws IOException {
    return new Builder(dest);
  }

  public static Builder builder(String fileName) throws IOException {
    return builder(new File(fileName));
  }

  public static class Builder extends WriteOptions.Builder {
    private ElementCreator elementCreator = (elementName, column, row) -> new Element(elementName);

    private boolean escapeText = true;

    private boolean showHeader = true;

    private int headerColumns = 0;

    private boolean transposed = false;

    private String missingCell = null;

    protected Builder(Destination dest) {
      super(dest);
    }

    protected Builder(File file) throws IOException {
      super(file);
    }

    public Builder escapeText(boolean escapeText) throws IOException {
      this.escapeText = escapeText;
      return this;
    }

    protected Builder(Writer writer) {
      super(writer);
    }

    protected Builder(OutputStream stream) {
      super(stream);
    }

    public Builder elementCreator(ElementCreator elementCreator) {
      this.elementCreator = elementCreator;
      return this;
    }

    public Builder showHeader(boolean showHeader) {
      this.showHeader = showHeader;
      return this;
    }

    public Builder headerColumns(int headerColumns) {
      this.headerColumns = headerColumns;
      return this;
    }

    public Builder transposed(boolean transposed) {
      this.transposed = transposed;
      return this;
    }

    public Builder missingCell(String missingCell) {
      this.missingCell = missingCell;
      return this;
    }

    public HtmlWriteOptions build() {
      return new HtmlWriteOptions(this);
    }
  }

  public static interface ElementCreator {
    /**
     * Called for each element created. Used as a hook to add classes or other attributes to the
     * element.
     *
     * @param elementName element type to create. E.g. table, thead, tbody, tr, th, td
     * @param column the column this table cell corresponds to. null if not a td or th
     * @param row the row this table cell corresponds to. null if not a td or tr in table body
     * @return HTML element
     */
    Element create(String elementName, Column<?> column, Integer row);

    default Element create(String elementName) {
      return create(elementName, null, null);
    }
  }
}
