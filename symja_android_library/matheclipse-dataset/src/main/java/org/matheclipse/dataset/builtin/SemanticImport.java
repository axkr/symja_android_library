package org.matheclipse.dataset.builtin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.io.FileSandbox;
import org.matheclipse.dataset.io.TablesawFormatIO;

/** Import semantic data into a DataSet */
public class SemanticImport extends AbstractFunctionOptionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger(SemanticImport.class);

  /**
   * The module's single reader path, the same one <code>Import</code> reaches through
   * {@link org.matheclipse.core.io.TableFormatIO}. Used directly rather than through
   * <code>TableFormatIO.get()</code> because this class only exists when the implementation does.
   */
  private static final TablesawFormatIO FORMAT_IO = new TablesawFormatIO();

  public SemanticImport() {}

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
      final EvalEngine engine, IAST originalAST) {
    if (Config.isFileSystemEnabled(engine)) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }

      // the Delimiters option used to be read and then dropped on the floor - readFile and readURL
      // both called Table.read().csv(...), which always splits on commas
      IAST readOptions = SemanticImportString.readOptions(option);

      IStringX arg1 = (IStringX) ast.arg1();
      Extension format = Extension.importFilename(arg1.toString());
      String fileName = arg1.toString();

      if (fileName.startsWith("https://") || fileName.startsWith("http://")) {
        return readURL(fileName, format, readOptions, engine);
      }

      if (format.equals(Extension.CSV) || format.equals(Extension.TSV)) {
        return readFile(fileName, format, readOptions, engine);
      }
    }
    return F.NIL;
  }

  private static IExpr readFile(String fileName, Extension format, IAST readOptions,
      EvalEngine engine) {
    try {
      File file = FileSandbox.resolveRead(S.SemanticImport, fileName, engine);
      if (file == null) {
        return F.NIL;
      }
      if (file.exists()) {
        try (InputStream inputStream = new FileInputStream(file)) {
          return FORMAT_IO.importTable(inputStream, format, readOptions);
        }
      }
      LOGGER.log(engine.getLogLevel(), "file {} does not exist!", fileName);
    } catch (IOException ioe) {
      LOGGER.log(engine.getLogLevel(), "file {} not found!", fileName, ioe);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      LOGGER.log(engine.getLogLevel(), "file {}", fileName, rex);
    }
    return F.NIL;
  }

  /**
   * Read CSV or TSV data from a URL.
   *
   * <p>
   * Example <code>urlName</code>:
   *
   * <pre>
   * https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/data/whiskey.csv\
   * </pre>
   *
   * @param urlName
   * @param format CSV or TSV format
   * @param engine
   * @return
   */
  private static IExpr readURL(String urlName, Extension format, IAST readOptions,
      EvalEngine engine) {
    try {
      URL url = new URL(urlName);
      if (format.equals(Extension.CSV) || format.equals(Extension.TSV)) {
        try (InputStream inputStream = url.openStream()) {
          return FORMAT_IO.importTable(inputStream, format, readOptions);
        }
      }
    } catch (ValidateException | MalformedURLException e) {
      LOGGER.log(engine.getLogLevel(), S.SemanticImport, e);
    } catch (IOException ioe) {
      LOGGER.log(engine.getLogLevel(), "SemanticImport: URL {} not found!", urlName);
    } catch (RuntimeException rex) {
      LOGGER.log(engine.getLogLevel(), "SemanticImport: URL {}", urlName, rex);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, SemanticImportString.OPTION_SYMBOLS,
        SemanticImportString.OPTION_DEFAULTS);
  }
}
