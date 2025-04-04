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

package tech.tablesaw.io.csv;

import java.io.IOException;
import java.io.Reader;
import org.hipparchus.util.Pair;
import com.google.common.io.CharStreams;
import com.google.errorprone.annotations.Immutable;
import com.univocity.parsers.common.AbstractParser;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.DataReader;
import tech.tablesaw.io.FileReader;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.io.ReaderRegistry;
import tech.tablesaw.io.RuntimeIOException;
import tech.tablesaw.io.Source;

@Immutable
public class CsvReader extends FileReader implements DataReader<CsvReadOptions> {

  private static final CsvReader INSTANCE = new CsvReader();

  static {
    register(Table.defaultReaderRegistry);
  }

  public static void register(ReaderRegistry registry) {
    registry.registerExtension("csv", INSTANCE);
    registry.registerMimeType("text/csv", INSTANCE);
    registry.registerOptions(CsvReadOptions.class, INSTANCE);
  }

  /** Constructs a CsvReader */
  public CsvReader() {
    super();
  }

  /**
   * Determines column types if not provided by the user Reads all input into memory unless File was
   * provided
   */
  private Pair<Reader, ReadOptions.ColumnTypeReadOptions> getReaderAndColumnTypes(
      Source source, CsvReadOptions options) throws IOException {
    ReadOptions.ColumnTypeReadOptions columnTypeReadOptions = options.columnTypeReadOptions();
    byte[] bytesCache = null;

    boolean need2ParseFile =
        !columnTypeReadOptions.hasColumnTypeForAllColumns()
            && (!options.header()
                || !columnTypeReadOptions.hasColumnTypeForAllColumnsIfHavingColumnNames());
    if (need2ParseFile) {
      Reader reader = source.createReader(null);
      if (source.file() == null) {
        String s = CharStreams.toString(reader);
        bytesCache = source.getCharset() != null ? s.getBytes(source.getCharset()) : s.getBytes();
        // create a new reader since we just exhausted the existing one
        reader = source.createReader(bytesCache);
      }
      ColumnType[] detectedColumnTypes = detectColumnTypes(reader, options);
      // If no columns where returned from detectColumnTypes leave initial options (that's the case
      // for only header present)
      if (detectedColumnTypes.length > 0) {
        columnTypeReadOptions = ReadOptions.ColumnTypeReadOptions.of(detectedColumnTypes);
      }
    }

    return Pair.create(source.createReader(bytesCache), columnTypeReadOptions);
  }

  @Override
  public Table read(CsvReadOptions options) {
    try {
      return read(options, false);
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
  }

  private Table read(CsvReadOptions options, boolean headerOnly) throws IOException {
    Pair<Reader, ReadOptions.ColumnTypeReadOptions> pair =
        getReaderAndColumnTypes(options.source(), options);
    Reader reader = pair.getKey();
    ReadOptions.ColumnTypeReadOptions columnTypeReadOptions = pair.getValue();

    AbstractParser<?> parser = csvParser(options);

    try {
      return parseRows(
          options, headerOnly, reader, columnTypeReadOptions, parser, options.sampleSize());
    } finally {
      if (options.source().reader() == null) {
        // if we get a reader back from options it means the client opened it, so let the client
        // close it
        // if it's null, we close it here.
        parser.stopParsing();
        reader.close();
      }
    }
  }

  /**
   * Returns a string representation of the column types in file {@code csvFilename}, as determined
   * by the type-detection algorithm
   *
   * <p>This method is intended to help analysts quickly fix any erroneous types, by printing out
   * the types in a format such that they can be edited to correct any mistakes, and used in an
   * array literal
   *
   * <p>For example:
   *
   * <p>LOCAL_DATE, // 0 date SHORT, // 1 approval STRING, // 2 who
   *
   * <p>Note that the types are array separated, and that the index position and the column name are
   * printed such that they would be interpreted as comments if you paste the output into an array:
   *
   * <p>
   *
   * @throws IOException if file cannot be read
   */
  public String printColumnTypes(CsvReadOptions options) throws IOException {
    Table structure = read(options, true).structure();
    return getTypeString(structure);
  }

  /**
   * Estimates and returns the type for each column in the delimited text file {@code file}
   *
   * <p>The type is determined by checking a sample of the data in the file. Because only a sample
   * of the data is checked, the types may be incorrect. If that is the case a Parse Exception will
   * be thrown.
   *
   * <p>The method {@code printColumnTypes()} can be used to print a list of the detected columns
   * that can be corrected and used to explicitly specify the correct column types.
   */
  protected ColumnType[] detectColumnTypes(Reader reader, CsvReadOptions options) {
    boolean header = options.header();
    CsvParser parser = csvParser(options);

    try {
      String[] columnNames = null;
      if (header) {
        parser.beginParsing(reader);
        columnNames = getColumnNames(options, options.columnTypeReadOptions(), parser);
      }
      return getColumnTypes(reader, options, 0, parser, columnNames);
    } finally {
      parser.stopParsing();
      // we don't close the reader since we didn't create it
    }
  }

  private CsvParser csvParser(CsvReadOptions options) {
    CsvParserSettings settings = new CsvParserSettings();
    settings.setLineSeparatorDetectionEnabled(options.lineSeparatorDetectionEnabled());
    settings.setFormat(csvFormat(options));
    settings.setMaxCharsPerColumn(options.maxCharsPerColumn());
    if (options.maxNumberOfColumns() != null) {
      settings.setMaxColumns(options.maxNumberOfColumns());
    }
    return new CsvParser(settings);
  }

  private CsvFormat csvFormat(CsvReadOptions options) {
    CsvFormat format = new CsvFormat();
    if (options.quoteChar() != null) {
      format.setQuote(options.quoteChar());
    }
    if (options.escapeChar() != null) {
      format.setQuoteEscape(options.escapeChar());
    }
    if (options.separator() != null) {
      format.setDelimiter(options.separator());
    }
    if (options.lineEnding() != null) {
      format.setLineSeparator(options.lineEnding());
    }
    if (options.commentPrefix() != null) {
      format.setComment(options.commentPrefix());
    }
    return format;
  }

  @Override
  public Table read(Source source) {
    return read(CsvReadOptions.builder(source).build());
  }
}
