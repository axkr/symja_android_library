package org.matheclipse.io.builtin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.io.expression.ASTDataset;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

/** Import semantic data into a DataSet */
public class SemanticImportString extends AbstractFunctionOptionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public SemanticImportString() {}

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
      final EvalEngine engine) {
    if (!(ast.arg1() instanceof IStringX)) {
      return F.NIL;
    }

    IStringX arg1 = (IStringX) ast.arg1();
    try {
      String contents = arg1.toString();
      IExpr delimiters = option[0];

      char delimiter;
      if (delimiters == S.Automatic) {
        delimiter = determineDelimiter(contents);
      } else {
        if (delimiters.isString()) {
          String str = delimiters.toString();
          if (str.length() == 1) {
            delimiter = str.charAt(0);
          } else {
            return dsdelimFailed(str, engine);
          }
        } else {
          return dsdelimFailed(delimiters.toString(), engine);
        }
      }

      IExpr type = S.Automatic;
      String typeStr = "";
      if (ast.argSize() >= 2) {
        type = ast.arg2();
        typeStr = type.toString();
      }

      if (!typeStr.equals("String") && typeStr.length() > 0) {
        // Interpreter type specification `1` is invalid.
        IOFunctions.printMessage(S.SemanticImport, "intype", F.List(typeStr), engine);
        return S.$Failed;
      }

      String formShape = "Dataset";
      if (ast.argSize() >= 3) {
        formShape = ast.arg3().toString();
      }
      if (formShape.equals("Columns") || formShape.equals("List")) {
        if (typeStr.equals("String")) {
          int index = contents.indexOf(delimiter);
          IASTAppendable columnList = F.ListAlloc();
          if (index < 0) {
            columnList.append(arg1);
            if (formShape.equals("List")) {
              return columnList;
            }
            return F.List(columnList);
          }
          int lastIndex = 0;
          while (index > 0) {
            columnList.append(contents.substring(lastIndex, index));
            lastIndex = index + 1;
            index = contents.indexOf(delimiter, lastIndex);
          }
          if (lastIndex < contents.length()) {
            columnList.append(contents.substring(lastIndex));
          }
          if (formShape.equals("List")) {
            return columnList;
          }
          return F.List(columnList);
        }
        return F.NIL;
      }
      if (formShape.equals("Dataset")) {
        // default Dataset
        CsvReadOptions.Builder builder =
            CsvReadOptions.builderFromString(contents).separator(delimiter);
        CsvReadOptions options = builder.build();
        Table table = Table.read().usingOptions(options);
        return ASTDataset.newTablesawTable(table);
      }
      // Shape specification `1` is invalid.
      IOFunctions.printMessage(S.SemanticImport, "shapespec", F.List(formShape), engine);
      return S.$Failed;
    } catch (Exception rex) {
      LOGGER.log(engine.getLogLevel(), "SemanticImportString ", rex);
      return F.NIL;
    }
  }

  private IExpr dsdelimFailed(final String delimiter, final EvalEngine engine) {
    // The delimiter specification is not valid.
    IOFunctions.printMessage(S.SemanticImport, "dsdelim", F.List(delimiter), engine);
    return S.$Failed;
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
    return IFunctionEvaluator.ARGS_1_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, S.Delimiters, S.Automatic);
  }
}
