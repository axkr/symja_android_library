package org.matheclipse.io.builtin;

import java.io.File;
import java.io.IOException;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
// import org.matheclipse.core.expression.data.DataSetExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.io.expression.ASTDataset;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

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
      if (format.equals(Extension.CSV) || format.equals(Extension.TSV)) {
        try {
          File file = new File(arg1.toString());
          if (file.exists()) {
            Table table = Table.read().csv(file);
            // System.out.println(table.printAll());
            // System.out.println(table.structure().printAll());
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
    }
    return F.NIL;
  }

  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }
}
