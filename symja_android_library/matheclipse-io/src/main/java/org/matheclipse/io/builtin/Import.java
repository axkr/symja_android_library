package org.matheclipse.io.builtin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.imageio.ImageIO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.nio.graphml.GraphMLImporter;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.convert.ExpressionJSONConvert;
import org.matheclipse.core.convert.JSONConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.io.tensor.io.ImageFormat;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Import some data from file system. */
public class Import extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public Import() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (Config.isFileSystemEnabled(engine)) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }

      IStringX arg1 = (IStringX) ast.arg1();
      Extension format = Extension.importFilename(arg1.toString());
      String fileName = arg1.toString();

      if (ast.size() > 2) {
        if (!(ast.arg2() instanceof IStringX)) {
          return F.NIL;
        }
        format = Extension.importExtension(((IStringX) ast.arg2()).toString());
      }
      FileReader reader = null;

      try {
        File file = new File(fileName);
        switch (format) {
          case BMP:
          case GIF:
          case JPEG:
          case PNG:
            try (
                InputStream inputStream = new ReaderInputStream(reader, Charset.defaultCharset())) {
              return ImageFormat.from(ImageIO.read(inputStream));
            }
          case DOT:
          case GRAPHML:
            // graph Format
            reader = new FileReader(fileName);
            return graphImport(reader, format, engine);
          case EXPRESSIONJSON:
            return expressionJSONImport(fileName);
          case JSON:
            return jsonImport(fileName);
          case M:
            if (ast.isAST1()) {
              return S.Get.of(engine, ast.arg1());
            }
            break;
          case TABLE:
            reader = new FileReader(fileName);
            AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
            final Parser parser = new Parser(engine.isRelaxedSyntax(), true);

            CSVFormat csvFormat = CSVFormat.RFC4180.withDelimiter(' ');
            Iterable<CSVRecord> records = csvFormat.parse(reader);
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
            return ofString(file, engine);
          case TXT:
            return ofText(file, engine);
          case WXF:
            byte[] byteArray = com.google.common.io.Files.toByteArray(file);
            return WL.deserialize(byteArray);
          default:
        }
      } catch (IOException ioe) {
        LOGGER.log(engine.getLogLevel(), "Import: file {} not found!", fileName, ioe);
      } catch (SyntaxError se) {
        LOGGER.log(engine.getLogLevel(), "Import: file {} syntax error!", fileName, se);
      } catch (Exception ex) {
        LOGGER.log(engine.getLogLevel(), "Import: file {} ", fileName, ex);
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e) {
          }
        }
      }
    }
    return F.NIL;
  }

  private static IExpr expressionJSONImport(String fileName)
      throws MalformedURLException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(new URL(fileName));
    return ExpressionJSONConvert.importExpressionJSONRecursive(node);
  }

  private static IExpr jsonImport(String fileName) throws MalformedURLException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(new URL(fileName));
    return JSONConvert.importJSONRecursive(node);
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
   * @throws IOException
   */
  public static IExpr ofString(File file, EvalEngine engine) throws IOException {
    String filename = file.getName();
    Extension extension = Extension.importFilename(filename);
    // Extension extension = filename.extension();
    if (extension.equals(Extension.JPEG) || extension.equals(Extension.PNG)) {
      // if (filename.hasExtension("jpg") || filename.hasExtension("png")) {
      return ImageFormat.from(ImageIO.read(file));
    }

    String str = com.google.common.io.Files.asCharSource(file, Charset.defaultCharset()).read();

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
   * @throws IOException
   */
  public static IExpr ofText(File file, EvalEngine engine) throws IOException {
    String str = com.google.common.io.Files.asCharSource(file, Charset.defaultCharset()).read();
    return F.stringx(str);
  }

  private IExpr graphImport(Reader reader, Extension format, EvalEngine engine)
      throws ImportException {
    Graph<IExpr, ExprEdge> result;
    switch (format) {
      case DOT:
        DOTImporter<IExpr, ExprEdge> dotImporter = new DOTImporter<IExpr, ExprEdge>();
        dotImporter.setVertexFactory(label -> engine.parse(label));
        result = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
        dotImporter.importGraph(result, reader);
        return GraphExpr.newInstance(result);
      case GRAPHML:
        result = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
        // Map<String, Map<String, Attribute>> vertexAttributes = new HashMap<>();
        // Map<ExprEdge, Map<String, Attribute>> edgeAttributes =
        // new HashMap<ExprEdge, Map<String, Attribute>>();
        GraphMLImporter<IExpr, ExprEdge> graphMLImporter = new GraphMLImporter<IExpr, ExprEdge>();
        graphMLImporter.setVertexFactory(label -> engine.parse(label));
        graphMLImporter.importGraph(result, reader);
        return GraphExpr.newInstance(result);
      default:
    }
    return F.NIL;
  }
}
