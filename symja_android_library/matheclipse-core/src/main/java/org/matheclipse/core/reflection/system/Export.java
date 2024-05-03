package org.matheclipse.core.reflection.system;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import javax.imageio.ImageIO;
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
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.tensor.img.ImageFormat;

/** Export some data from file system. */
public class Export extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public Export() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (Config.isFileSystemEnabled(engine)) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      IStringX arg1 = (IStringX) ast.arg1();
      String filename = arg1.toString();
      Extension format = Extension.exportFilename(filename);
      if (ast.size() == 4) {
        if (!(ast.arg3() instanceof IStringX)) {
          return F.NIL;
        }
        // format = ((IStringX) ast.arg3()).toString();
        format = Extension.exportExtension(((IStringX) ast.arg3()).toString());
      }

      IExpr arg2 = ast.arg2();

      if (format.equals(Extension.GIF) || format.equals(Extension.PNG)) {
        // if (arg1 instanceof ImageExpr) {
        //
        // }
        // int[] dimensions = arg1.isMatrix();
        // if (dimensions != null && dimensions.length >= 2 && arg2.isAST()) {
        try {
          if (exportImage(filename, (IAST) arg2, format)) {
            return arg1;
          }
        } catch (RuntimeException rex) {
          rex.printStackTrace();
        }
        // }
        // return F.NIL;
      }

      try (FileWriter writer = new FileWriter(filename)) {
        if (arg2 instanceof GraphExpr) {
          graphExport(((GraphExpr<DefaultEdge>) arg2).toData(), writer, format);
          return arg1;
        }

        if (format.equals(Extension.CSV) || format.equals(Extension.TSV)) {
          if (arg2.isDataset()) {
            ((IASTDataset) arg2).csv(writer);
            return arg1;
          }
        } else if (format.equals(Extension.TABLE)) {
          int[] dims = arg2.isMatrix();
          if (dims != null) {
            for (int j = 0; j < dims[0]; j++) {
              IAST rowList = (IAST) arg2.getAt(j + 1);
              for (int i = 1; i <= dims[1]; i++) {
                if (rowList.get(i).isReal()) {
                  writer.append(rowList.get(i).toString());
                } else {
                  writer.append("\"");
                  writer.append(rowList.get(i).toString());
                  writer.append("\"");
                }
                if (i < dims[1]) {
                  writer.append(",");
                }
              }
              writer.append("\n");
            }
            return arg1;
          } else {
            if (arg2.isList()) {
            }
          }
        } else if (format.equals(Extension.DAT)) {
          Files.writeString(Path.of(filename), arg2.toString(), Charset.defaultCharset());
          return arg1;
        } else if (format.equals(Extension.MAT)) {
          //
        } else if (format.equals(Extension.WXF)) {
          byte[] bArray = WL.serialize(arg2);
          Files.write(Path.of(filename), bArray);
          return arg1;
        }
      } catch (FileNotFoundException ex) {
        LOGGER.log(engine.getLogLevel(), "Export: file {}", arg1, ex);
      } catch (IOException ioe) {
        LOGGER.log(engine.getLogLevel(), "Export: file {} not found!", arg1, ioe);
      } catch (Exception ex) {
        LOGGER.log(engine.getLogLevel(), "Export: file {}", arg1, ex);
      }
    }
    return F.NIL;
  }

  public static boolean exportImage(String filename, IAST matrix, Extension format) {
    try (OutputStream outputStream = new FileOutputStream(filename)) {
      return exportImage(outputStream, matrix, format);
    } catch (IOException e) {
      // e.printStackTrace();
    }
    return false;
  }

  public static boolean exportImage(OutputStream outputStream, IAST matrix, Extension format)
      throws IOException {
    BufferedImage intARGB = ImageFormat.toIntARGB(matrix);
    if (intARGB != null) {
      ImageIO.write(intARGB, format.name(), outputStream);
      return true;
    }
    return false;
  }

  private static final Function<IExpr, String> nameProvider = v -> String.valueOf(v);

  void graphExport(Graph<IExpr, DefaultEdge> g, Writer writer, Extension format)
      throws ExportException {
    switch (format) {
      case DOT:
        DOTExporter<IExpr, DefaultEdge> dotExporter = new DOTExporter<>(); // new
                                                                           // IntegerComponentNameProvider<>(),
                                                                           // null, null, null,
                                                                           // null);
        // dotExporter.putGraphAttribute("overlap", "false");
        // dotExporter.putGraphAttribute("splines", "true");
        dotExporter.setGraphAttributeProvider(() -> {
          Map<String, Attribute> map = new LinkedHashMap<>();
          map.put("overlap", DefaultAttribute.createAttribute("false"));
          map.put("splines", DefaultAttribute.createAttribute("true"));
          return map;
        });
        dotExporter.exportGraph(g, writer);
        return;
      case GRAPHML:
        GraphExporter<IExpr, DefaultEdge> graphMLExporter = new GraphMLExporter<>();
        graphMLExporter.exportGraph(g, writer);
        return;
      default:
    }

    // DEFAULT: return CSV file
    CSVExporter<IExpr, DefaultEdge> exporter = new CSVExporter<IExpr, DefaultEdge>(nameProvider,
        org.jgrapht.nio.csv.CSVFormat.EDGE_LIST, ';');
    exporter.exportGraph(g, writer);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }


  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
