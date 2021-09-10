package org.matheclipse.io.builtin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.io.expression.ASTDataset;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

/** Import semantic data into a DataSet */
public class SemanticImportString extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public SemanticImportString() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!(ast.arg1() instanceof IStringX)) {
      return F.NIL;
    }

    IStringX arg1 = (IStringX) ast.arg1();
    try {
      String contents = arg1.toString();
      char delimiter = determineDelimiter(contents);
      CsvReadOptions.Builder builder =
          CsvReadOptions.builderFromString(contents).separator(delimiter);
      CsvReadOptions options = builder.build();
      Table table = Table.read().usingOptions(options);
      return ASTDataset.newTablesawTable(table);
    } catch (Exception rex) {
      LOGGER.log(engine.getLogLevel(), "SemanticImportString ", rex);
      return F.NIL;
    }
  }

  /**
   * Determine the delimiter from the first line of the CSV content. Default delimiter is <code>,
   * </code>.
   *
   * @param contents
   * @return
   */
  public static char determineDelimiter(String contents) {
    char delimiter = ',';
    int index = contents.indexOf('\n');
    if (index > 0) {
      int[] count = new int[4];
      for (int i = 0; i < index; i++) {
        char ch = contents.charAt(i);
        if (ch == ';') {
          count[0]++;
        } else if (ch == ',') {
          count[1]++;
        } else if (ch == '\t') {
          count[2]++;
        } else if (ch == ':') {
          count[3]++;
        }
      }
      int countIndx = -1;
      int max = -1;
      for (int i = 0; i < count.length; i++) {
        if (count[i] > max) {
          countIndx = i;
          max = count[i];
        }
      }
      if (countIndx > 0) {
        switch (countIndx) {
          case 0:
            delimiter = ';';
            break;
          case 1:
            delimiter = ',';
            break;
          case 2:
            delimiter = '\t';
            break;
          case 3:
            delimiter = ':';
            break;
          default:
        }
      }
    }
    return delimiter;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }
}
