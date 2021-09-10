package org.matheclipse.core.reflection.system;

import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.csv.CSVExporter;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.graphml.GraphMLExporter;
import org.matheclipse.core.convert.ExpressionJSONConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;

/** Export some data into a string representation */
public class ExportString extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public ExportString() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (!(ast.arg2() instanceof IStringX)) {
      return F.NIL;
    }
    Extension format = Extension.exportExtension(((IStringX) ast.arg2()).toString());
    try (StringWriter writer = new StringWriter()) {
      if (format.equals(Extension.JSON)) {
        if (arg1.isNumber() || arg1.isSymbol()) {
          return F.stringx(arg1.toString());
        } else if (arg1.isString()) {
          return F.stringx("'" + arg1.toString() + "'");
        }
        return ExpressionJSONConvert.exportIStringX(arg1);
      }

      if (arg1 instanceof GraphExpr) {
        graphExport(((GraphExpr<DefaultEdge>) arg1).toData(), writer, format);
        return F.stringx(writer.toString());
      }

      if (format.equals(Extension.CSV) || format.equals(Extension.TSV)) {
        if (arg1.isDataset()) {
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
          if (arg1.isList()) {}
        }
        // } else if (format.equals(Extension.DAT)) {
        // File file = new File(arg1.toString());
        // com.google.common.io.Files.write(arg2.toString(), file, Charset.defaultCharset());
        // return arg1;
        // } else if (format.equals(Extension.WXF)) {
        // File file = new File(arg1.toString());
        // byte[] bArray = WL.serialize(arg2);
        // com.google.common.io.Files.write(bArray, file);
        // return arg1;
      }

      // } catch (IOException ioe) {
      // return engine.printMessage("ExportString: " + arg1.toString() + " not found!");
    } catch (Exception ex) {
      LOGGER.log(engine.getLogLevel(), "format: {}", arg1, ex);
      return F.NIL;
    }
    return F.NIL;
  }

  private static final Function<IExpr, String> nameProvider = v -> String.valueOf(v);

  void graphExport(Graph<IExpr, DefaultEdge> g, Writer writer, Extension format)
      throws ExportException {
    switch (format) {
      case DOT:
        DOTExporter<IExpr, DefaultEdge> dotExporter =
            new DOTExporter<>(); // new IntegerComponentNameProvider<>(), null, null, null, null);
        //        dotExporter.putGraphAttribute("overlap", "false");
        //        dotExporter.putGraphAttribute("splines", "true");

        dotExporter.setGraphAttributeProvider(
            () -> {
              Map<String, Attribute> map = new LinkedHashMap<>();
              map.put("overlap", DefaultAttribute.createAttribute("false"));
              map.put("splines", DefaultAttribute.createAttribute("true"));
              return map;
            });
        return;
      case GRAPHML:
        GraphExporter<IExpr, DefaultEdge> graphMLExporter = new GraphMLExporter<>();
        graphMLExporter.exportGraph(g, writer);
        return;
      default:
    }

    // DEFAULT: return CSV file
    CSVExporter<IExpr, DefaultEdge> exporter =
        new CSVExporter<IExpr, DefaultEdge>(
            nameProvider, org.jgrapht.nio.csv.CSVFormat.EDGE_LIST, ';');
    exporter.exportGraph(g, writer);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }
}
