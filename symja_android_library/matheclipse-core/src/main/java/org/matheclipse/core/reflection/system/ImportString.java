package org.matheclipse.core.reflection.system;

import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;
import org.apache.commons.csv.CSVFormat;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.ExpressionJSONConvert;
import org.matheclipse.core.convert.JSONConvert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.io.Graph6Format;
import org.matheclipse.core.io.TableFormatIO;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;

/** Import some data from a given string. */
public class ImportString extends AbstractEvaluator {

  public ImportString() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!(ast.arg1() instanceof IStringX)) {
      return F.NIL;
    }

    String str1 = ast.arg1().toString();
    Extension format = Extension.TXT;
    String element = "";

    if (ast.size() > 2) {
      Extension.ImportSpec spec = Extension.importSpec(ast.arg2());
      if (spec == null) {
        return F.NIL;
      }
      format = spec.format;
      element = spec.element;
    }
    return importString(str1, format, element, engine);
  }

  /**
   * Import data from a string.
   *
   * @param str1 the data
   * @param format the format of the data
   * @param element the element of the format to import, or <code>""</code> for its default
   * @param engine
   * @return {@link F#NIL} if the data cannot be imported in this format
   */
  public static IExpr importString(String str1, Extension format, String element,
      EvalEngine engine) {
    try {
      switch (format) {
        case GRAPH6:
          return Graph6Format.importString(str1, element.isEmpty() ? "Graph" : element, engine);
        case BASE64:
          byte[] decodedBytes = Base64.getDecoder().decode(str1);
          return F.stringx(new String(decodedBytes));
        case JSON:
          return JSONConvert.importJSON(str1, false);
        case EXPRESSIONJSON:
          return ExpressionJSONConvert.importExpressionJSON(str1);
        case CSV:
        case TSV:
          // a Dataset when matheclipse-dataset is on the classpath, and nothing at all without it -
          // ImportString never handled CSV before, so there is no older shape to fall back to. Note
          // that TABLE below deliberately stays a nested list.
          TableFormatIO tableFormatIO = TableFormatIO.get();
          if (tableFormatIO != null && tableFormatIO.canImport(format)) {
            return tableFormatIO.importTable(str1, format, F.NIL);
          }
          return F.NIL;
        case TABLE:
          AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
          final Parser parser = new Parser(engine.isRelaxedSyntax(), true);

          CSVFormat csvFormat = CSVFormat.RFC4180.withDelimiter(',');
          return Convert.csvRecords2List(csvFormat.parse(new StringReader(str1)), parser,
              ast2Expr);
        case RAWJSON:
          return JSONConvert.importJSON(str1, true);
        case STRING:
          return ofString(str1, engine);
        case TXT:
          return ofText(str1, engine);
        default:
      }

    } catch (SyntaxError se) {
      Errors.printMessage(S.ImportString, se, engine);
    } catch (IOException jpe) {
      Errors.printMessage(S.ImportString, jpe, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(S.ImportString, rex, engine);
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  /**
   * Get arbitrary data represented as a Symja expression string
   *
   * @param str
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
   * @param str
   * @param engine
   * @return
   */
  public static IExpr ofText(String str, EvalEngine engine) {
    return F.stringx(str);
  }
}
