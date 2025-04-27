package org.matheclipse.io.builtin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
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
import org.matheclipse.io.expression.ASTDataset;
import tech.tablesaw.api.Table;

/** Import semantic data into a DataSet */
public class SemanticImport extends AbstractFunctionOptionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger(SemanticImport.class);

  public SemanticImport() {}

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
      final EvalEngine engine, IAST originalAST) {
    if (Config.isFileSystemEnabled(engine)) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }

      IExpr delimiters = option[0];
      String delimiter = ",";
      if (delimiters.isString()) {
        delimiter = delimiters.toString();
      }

      IStringX arg1 = (IStringX) ast.arg1();
      Extension format = Extension.importFilename(arg1.toString());
      String fileName = arg1.toString();

      if (fileName.startsWith("https://") || fileName.startsWith("http://")) {
        return readURL(fileName, format, engine);
      }

      if (format.equals(Extension.CSV) || format.equals(Extension.TSV)) {
        return readFile(fileName, engine);
      }
    }
    return F.NIL;
  }

  private static IExpr readFile(String fileName, EvalEngine engine) {
    try {
      File file = new File(fileName);
      if (file.exists()) {
        Table table = Table.read().csv(file);
        return ASTDataset.newTablesawTable(table);
      }
      LOGGER.log(engine.getLogLevel(), "file {} does not exist!", fileName);
      // } catch (IOException ioe) {
      // LOGGER.log(engine.getLogLevel(), "file {} not found!", fileName);
    } catch (RuntimeException rex) {
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
  private static IExpr readURL(String urlName, Extension format, EvalEngine engine) {
    try {
      URL url = new URL(urlName);
      if (format.equals(Extension.CSV) || format.equals(Extension.TSV)) {
        Table table = Table.read().csv(url);
        return ASTDataset.newTablesawTable(table);
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
    setOptions(newSymbol, S.Delimiters, F.stringx(","));
  }
}
