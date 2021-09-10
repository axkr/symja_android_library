package org.matheclipse.core.reflection.system;

import java.io.StringReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;

/** Import some data from a given string. */
public class ImportString extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public ImportString() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!(ast.arg1() instanceof IStringX)) {
      return F.NIL;
    }

    String str1 = ((IStringX) ast.arg1()).toString();
    Extension format = Extension.TXT;

    if (ast.size() > 2) {
      if (!(ast.arg2() instanceof IStringX)) {
        return F.NIL;
      }
      format = Extension.importExtension(((IStringX) ast.arg2()).toString());
    }

    try {
      switch (format) {
        case TABLE:
          AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
          final Parser parser = new Parser(engine.isRelaxedSyntax(), true);

          CSVFormat csvFormat = CSVFormat.RFC4180.withDelimiter(',');
          Iterable<CSVRecord> records = csvFormat.parse(new StringReader(str1));
          IASTAppendable rowList = F.ListAlloc(256);
          for (CSVRecord record : records) {
            IASTAppendable columnList = F.ListAlloc(record.size());
            for (String string : record) {
              final ASTNode node = parser.parse(string);
              IExpr temp = ast2Expr.convert(node);
              columnList.append(temp);
            }
            rowList.append(columnList);
          }
          return rowList;
        case STRING:
          return ofString(str1, engine);
        case TXT:
          return ofText(str1, engine);
        default:
      }

    } catch (SyntaxError se) {
      LOGGER.log(engine.getLogLevel(), "ImportString: syntax error!", se);
    } catch (Exception ex) {
      LOGGER.log(engine.getLogLevel(), "ImportString", ex);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  /**
   * Get arbitrary data represented as a Symja expression string
   *
   * @param file
   * @param engine
   * @return
   */
  public static IExpr ofString(String str, EvalEngine engine) {
    AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
    final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
    final ASTNode node = parser.parse(str);
    return ast2Expr.convert(node);
  }

  /**
   * Get plain text from file
   *
   * @param file
   * @param engine
   * @return
   */
  public static IExpr ofText(String str, EvalEngine engine) {
    return F.stringx(str);
  }
}
