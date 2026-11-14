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
import org.matheclipse.core.io.TextEncoding;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;

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
    if (ExportString.isByteText(format)) {
      // a text format is read as the bytes of its file, a character for each: what ExportString
      // gives
      str1 = TextEncoding.fromByteCharacters(str1);
    }
    return importString(str1, format, element, engine);
  }

  /**
   * The first argument of <code>ImportString[data, format]</code> for a <code>text</code>: the
   * text itself, or for a format which is read as bytes the bytes of its UTF-8 encoding, a
   * character for each.
   */
  public static IStringX argument(String text, IExpr format) {
    Extension.ImportSpec spec = Extension.importSpec(format);
    return F.stringx(
        spec != null && ExportString.isByteText(spec.format) ? TextEncoding.toByteCharacters(text)
            : text);
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

          CSVFormat csvFormat = CSVFormat.RFC4180.builder().setDelimiter(',').get();
          return Convert.csvRecords2List(csvFormat.parse(new StringReader(str1)), parser,
              ast2Expr);
        case RAWJSON:
          return JSONConvert.importJSON(str1, true);
        case STRING:
          // the characters as they are
          return F.stringx(str1);
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
