package org.matheclipse.core.reflection.system;

import java.io.IOException;
import java.io.Writer;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.io.output.StringBuilderWriter;
import org.matheclipse.core.convert.ExpressionJSONConvert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.SVGGraphics;
import org.matheclipse.core.graphics.SVGGraphics3D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IGraphExpr;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.io.TableFormatIO;

/** Export some data into a string representation */
public class ExportString extends AbstractEvaluator {

  public ExportString() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (!(ast.arg2() instanceof IStringX)) {
      return F.NIL;
    }
    for (int i = 3; i < ast.size(); i++) {
      // options. "Compact" is the only one that would change the answer, and the exporters here
      // write compact already, so an option is accepted and makes no difference.
      IExpr option = ast.get(i);
      if (!option.isRuleAST() && !(option.isList() && ((IAST) option).forAll(x -> x.isRuleAST()))) {
        // `1` is not a valid option.
        return F.NIL;
      }
    }
    Extension format = Extension.exportExtension(ast.arg2().toString());
    try (StringBuilderWriter writer = new StringBuilderWriter()) {
      if (format.equals(Extension.EXPRESSIONJSON)) {
        // atoms too: 1/2 is ["Rational",1,2] and a string is a JSON string, not bare text
        return ExpressionJSONConvert.exportExpressionJSONIStringX(arg1);
      }

      if (format.equals(Extension.JSON) || format.equals(Extension.RAWJSON)) {
        return F.stringx(
            org.matheclipse.core.convert.JSONConvert.exportJSON(arg1,
                format.equals(Extension.RAWJSON)));
      }

      if (format.equals(Extension.STRING) || format.equals(Extension.TXT)) {
        // the text of it: a string is its own content, and anything else is written the way it
        // prints
        return arg1.isString() ? F.stringx(arg1.toString()) : F.stringx(arg1.toString());
      }

      if (format.equals(Extension.SVG)) {
        String svgString = SVGGraphics.svgDocument(arg1);
        if (svgString != null) {
          return F.stringx(svgString);
        }
      }
      if (arg1 instanceof IGraphExpr) {
        ((IGraphExpr) arg1).graphExport(writer, format);
        return F.stringx(writer.toString());
      }

      if (format.equals(Extension.BASE64)) {
        if (arg1.isString()) {
          String str = ast.arg1().toString();
          String encodedString = Base64.getEncoder().encodeToString(str.getBytes());
          return F.stringx(encodedString);
        }
      } else if (format.equals(Extension.CSV) || format.equals(Extension.TSV)) {
        if (arg1.isDataset()) {
          // matheclipse-dataset writes the format's own separator - a tab for TSV - where the
          // IASTDataset fallback below always writes commas
          TableFormatIO tableFormatIO = TableFormatIO.get();
          if (tableFormatIO != null && tableFormatIO.canExport(format)) {
            if (tableFormatIO.exportTable(writer, arg1, format, F.NIL)) {
              return F.stringx(writer.toString());
            }
          }
          ((IASTDataset) arg1).csv(writer);
          return F.stringx(writer.toString());
        }
      } else if (format.equals(Extension.TABLE)) {
        int[] dims = arg1.isMatrix();
        if (dims != null) {
          for (int j = 0; j < dims[0]; j++) {
            IAST rowList = (IAST) arg1.getAt(j + 1);
            for (int i = 1; i <= dims[1]; i++) {
              if (rowList.get(i).isReal()) {
                writer.append(rowList.get(i).toString());
              } else {
                writer.append("\"");
                writer.append(rowList.get(i).toString());
                writer.append("\"");
              }
              if (i < dims[1]) {
                writer.append(" ");
              }
            }
            writer.append("\n");
          }
          return F.stringx(writer.toString());
        } else {
          if (arg1.isList()) {
          }
        }
        // } else if (format.equals(Extension.DAT)) {
        // Path file = Path.of(arg1.toString());
        // Files.writeString(file, arg2.toString(), Charset.defaultCharset());
        // return arg1;
        // } else if (format.equals(Extension.WXF)) {
        // Path file = Path.of(arg1.toString());
        // byte[] bArray = WL.serialize(arg2);
        // Files.write(file, bArray);
        // return arg1;
      }

      // } catch (IOException ioe) {
      // return engine.printMessage("ExportString: " + arg1.toString() + " not found!");
    } catch (IOException ioex) {
      return Errors.printMessage(S.ExportString, ioex, EvalEngine.get());
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.ExportString, rex, EvalEngine.get());
    }
    return F.NIL;
  }



  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }


  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
