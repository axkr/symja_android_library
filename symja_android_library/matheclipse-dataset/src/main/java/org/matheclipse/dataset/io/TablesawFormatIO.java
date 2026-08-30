package org.matheclipse.dataset.io;

import java.io.InputStream;
import java.io.Writer;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.io.TableFormatIO;
import org.matheclipse.dataset.expression.ASTDataset;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.io.xlsx.XlsxReadOptions;

/**
 * The {@link TableFormatIO} implementation backed by the vendored <code>tech.tablesaw</code> fork.
 *
 * <p>
 * This is the single reader/writer path of the module: <code>Import</code>,
 * <code>ImportString</code>, <code>Export</code>, <code>ExportString</code>,
 * <code>SemanticImport</code> and <code>SemanticImportString</code> all come through here, so the
 * option handling and the delimiter defaults are written down once.
 */
public class TablesawFormatIO implements TableFormatIO {

  @Override
  public boolean canImport(Extension format) {
    return format == Extension.CSV || format == Extension.TSV || format == Extension.XLSX;
  }

  @Override
  public boolean canExport(Extension format) {
    // XLSX is import only: the fork has an XlsxReader and no XlsxWriter
    return format == Extension.CSV || format == Extension.TSV;
  }

  @Override
  public IExpr importTable(InputStream inputStream, Extension format, IAST options) {
    try {
      if (format == Extension.XLSX) {
        XlsxReadOptions.Builder builder = XlsxReadOptions.builder(new Source(inputStream));
        if (noHeaderLine(options)) {
          builder.header(false);
        }
        String[] missing = missingValueIndicators(options);
        if (missing != null) {
          builder.missingValueIndicator(missing);
        }
        return newDataset(Table.read().usingOptions(builder.build()));
      }
      if (format == Extension.CSV || format == Extension.TSV) {
        return newDataset(Table.read()
            .usingOptions(csvOptions(CsvReadOptions.builder(inputStream), format, options)));
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(S.Import, rex, org.matheclipse.core.eval.EvalEngine.get());
    }
    return F.NIL;
  }

  @Override
  public IExpr importTable(String content, Extension format, IAST options) {
    try {
      if (format == Extension.CSV || format == Extension.TSV) {
        return newDataset(Table.read()
            .usingOptions(csvOptions(CsvReadOptions.builderFromString(content), format, options)));
      }
      // XLSX is binary; there is no meaningful reading of it from a string of characters
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(S.ImportString, rex, org.matheclipse.core.eval.EvalEngine.get());
    }
    return F.NIL;
  }

  @Override
  public boolean exportTable(Writer writer, IExpr expr, Extension format, IAST options) {
    if (!(expr instanceof ASTDataset) || !canExport(format)) {
      return false;
    }
    try {
      Table table = ((ASTDataset) expr).toData();
      CsvWriteOptions writeOptions =
          CsvWriteOptions.builder(writer).separator(separator(format, options)).build();
      table.write().usingOptions(writeOptions);
      return true;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(S.Export, rex, org.matheclipse.core.eval.EvalEngine.get());
      return false;
    }
  }

  private static IExpr newDataset(Table table) {
    return table == null ? F.NIL : ASTDataset.newTablesawTable(table);
  }

  /**
   * The field separator. <code>TSV</code> is tab separated and <code>CSV</code> comma separated
   * unless a <code>Delimiters</code> option says otherwise. A <code>Delimiters</code> value that is
   * not a one character string is ignored rather than rejected, because this is also the path
   * <code>Import</code> takes, and <code>Import</code> has no <code>Delimiters</code> option to
   * report a bad value against.
   */
  private static char separator(Extension format, IAST options) {
    IExpr delimiters = optionValue(options, S.Delimiters);
    if (delimiters.isString() && delimiters.toString().length() == 1) {
      return delimiters.toString().charAt(0);
    }
    return format == Extension.TSV ? '\t' : ',';
  }

  /**
   * <code>tech.tablesaw.io.ReadOptions.Builder</code> is protected, so the shared options cannot be
   * applied through the base type; each concrete builder applies them itself.
   */
  private static CsvReadOptions csvOptions(CsvReadOptions.Builder builder, Extension format,
      IAST options) {
    builder.separator(separator(format, options));
    if (noHeaderLine(options)) {
      builder.header(false);
    }
    String[] missing = missingValueIndicators(options);
    if (missing != null) {
      builder.missingValueIndicator(missing);
    }
    return builder.build();
  }

  /**
   * <code>HeaderLines -> 0</code> reads the first line as data rather than as column names. Any
   * other value leaves tablesaw's default of one header line in place - the reader has no notion of
   * skipping more than one.
   */
  private static boolean noHeaderLine(IAST options) {
    return optionValue(options, S.HeaderLines).toIntDefault() == 0;
  }

  /**
   * <code>MissingValuePattern</code> takes a string or a list of strings.
   *
   * @return <code>null</code> when the option is absent, so that tablesaw's own defaults stay in
   *         place rather than being overwritten with an empty array
   */
  private static String[] missingValueIndicators(IAST options) {
    IExpr missing = optionValue(options, S.MissingValuePattern);
    if (missing.isString()) {
      return new String[] {missing.toString()};
    }
    if (missing.isList()) {
      IAST list = (IAST) missing;
      String[] indicators = new String[list.argSize()];
      for (int i = 0; i < indicators.length; i++) {
        indicators[i] = list.get(i + 1).toString();
      }
      return indicators;
    }
    return null;
  }

  /**
   * @return the right-hand side of the <code>key -> value</code> rule in <code>options</code>, or
   *         {@link F#NIL} if <code>options</code> is {@link F#NIL} or holds no such rule
   */
  private static IExpr optionValue(IAST options, ISymbol key) {
    if (options == null || options.isNIL() || !options.isList()) {
      return F.NIL;
    }
    for (int i = 1; i < options.size(); i++) {
      IExpr rule = options.get(i);
      if (rule.isRuleAST() && rule.first().equals(key)) {
        return rule.second();
      }
    }
    return F.NIL;
  }
}
