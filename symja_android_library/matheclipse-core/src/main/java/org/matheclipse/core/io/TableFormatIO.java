package org.matheclipse.core.io;

import java.io.InputStream;
import java.io.Writer;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The table readers and writers used by <code>Import</code>, <code>ImportString</code>,
 * <code>Export</code> and <code>ExportString</code>.
 *
 * <p>
 * Three of those four live in <code>matheclipse-core</code>, which must not depend on
 * <code>matheclipse-dataset</code>, so the dependency is inverted: <code>DatasetInit.init()</code>
 * installs an implementation here and core calls it through this interface. The same pattern
 * <code>matheclipse-image</code> uses for {@link ImageFormatIO} and <code>matheclipse-bio</code>
 * for {@link BioSequenceFormat}.
 *
 * <p>
 * While no implementation is installed, <code>CSV</code> and <code>TSV</code> import falls back to
 * the nested list of {@link org.matheclipse.core.convert.Convert#fromCSV(java.io.Reader)} and
 * export falls back to {@link IASTDataset#csv(Writer)}, so core on its own keeps working. With one
 * installed, <code>CSV</code>, <code>TSV</code> and <code>XLSX</code> import produce a
 * <code>Dataset</code> instead - and <code>Extension#TABLE</code> keeps returning the nested list,
 * so that <code>Import(…, "Table")</code> and <code>Export(…, "Table")</code> stay a matched pair
 * for plain matrices.
 *
 * <p>
 * This interface is about <b>format registration</b>. The <code>Dataset</code> object itself is
 * {@link IASTDataset}, and the two are deliberately separate: a <code>Dataset</code> can already
 * write itself as CSV without any of this.
 */
public interface TableFormatIO {

  /**
   * The installed reader/writer, or <code>null</code> when <code>matheclipse-dataset</code> is not
   * present.
   */
  TableFormatIO[] INSTANCE = new TableFormatIO[1];

  /** Whether {@link #importTable(InputStream, Extension, IAST)} can read <code>format</code>. */
  boolean canImport(Extension format);

  /** Whether {@link #exportTable(Writer, IExpr, Extension, IAST)} can write <code>format</code>. */
  boolean canExport(Extension format);

  /**
   * Read a table from a byte stream. This is the form binary table formats need, and the caller
   * owns the stream.
   *
   * @param options a <code>List(...)</code> of rules, or
   *        {@link org.matheclipse.core.expression.F#NIL}. Understood are <code>Delimiters</code>,
   *        <code>HeaderLines</code> and <code>MissingValuePattern</code>; the reader silently
   *        ignores the rest, so core does not have to know which format understands what.
   * @return the <code>Dataset</code>, or {@link org.matheclipse.core.expression.F#NIL} if the
   *         stream holds no table of that format
   */
  IExpr importTable(InputStream inputStream, Extension format, IAST options);

  /**
   * Read a table from a string of content.
   *
   * @return the <code>Dataset</code>, or {@link org.matheclipse.core.expression.F#NIL}
   */
  IExpr importTable(String content, Extension format, IAST options);

  /**
   * Write <code>expr</code> - a <code>Dataset</code> - as <code>format</code>.
   *
   * <p>
   * A {@link Writer} rather than an <code>OutputStream</code>, unlike {@link ImageFormatIO}: every
   * table format that can be written is text, and both call sites - <code>Export</code> with a
   * <code>FileWriter</code> and <code>ExportString</code> with a string writer - already have one.
   *
   * @return <code>false</code> if <code>expr</code> is not a <code>Dataset</code>, or writing
   *         failed
   */
  boolean exportTable(Writer writer, IExpr expr, Extension format, IAST options);

  /** Install the reader/writer. Called from <code>org.matheclipse.dataset.DatasetInit</code>. */
  static void install(TableFormatIO tableFormatIO) {
    INSTANCE[0] = tableFormatIO;
  }

  /** @return the installed reader/writer, or <code>null</code> */
  static TableFormatIO get() {
    return INSTANCE[0];
  }
}
