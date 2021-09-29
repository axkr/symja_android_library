package org.matheclipse.io.builtin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.io.expression.ASTDataset;
import tech.tablesaw.api.Table;

/** Import semantic data into a DataSet */
public class SemanticImport extends AbstractEvaluator {

  public SemanticImport() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (Config.isFileSystemEnabled(engine)) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
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
      return engine.printMessage("SemanticImport: file " + fileName + " does not exist!");
    } catch (IOException ioe) {
      return engine.printMessage("SemanticImport: file " + fileName + " not found!");
    } catch (RuntimeException rex) {
      return engine.printMessage("SemanticImport: file " + fileName + " - " + rex.getMessage());
    } finally {
    }
  }

  /**
   * Read CSV or TSV data from a URL.
   *
   * <p>Example <code>urlName</code>:
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
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
      return engine.printMessage(S.SemanticImport, e);
    } catch (IOException ioe) {
      return engine.printMessage("SemanticImport: URL " + urlName + " not found!");
    } catch (RuntimeException rex) {
      return engine.printMessage("SemanticImport: URL " + urlName + " - " + rex.getMessage());
    } finally {
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }
}
