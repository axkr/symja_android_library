package org.matheclipse.io.builtin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.imageio.ImageIO;
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
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.ExpressionJSONConvert;
import org.matheclipse.core.convert.JSONConvert;
import org.matheclipse.core.convert.matlab.Mat5Symja;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
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

      if (ast.size() > 2) {
        if (!(ast.arg2() instanceof IStringX)) {
          return F.NIL;
        }
        format = Extension.importExtension(((IStringX) ast.arg2()).toString());
      }

      return importFromPath(arg1, format, null, engine);
    }
    return F.NIL;
  }

  /**
   * Import data from a file or URL specification.
   * 
   * @param pathName full pathName to a file or URL
   * @param format the format of the input data
   * @param dataFile if <code>dataFile!=null</code>, ignore pathName and use this {@link File}
   *        directly
   * @param engine
   * @return
   */
  public static IExpr importFromPath(IStringX pathName, Extension format, File dataFile,
      EvalEngine engine) {
    FileReader reader = null;
    String fileName = pathName.toString();
    try {
      File file = dataFile != null ? dataFile : new File(fileName);
      switch (format) {
        case BMP:
        case GIF:
        case JPEG:
        case PNG:
          reader = new FileReader(fileName);
          try (InputStream inputStream = //
              ReaderInputStream.builder()//
                  .setReader(reader)//
                  .get()) {
            return ImageFormat.from(ImageIO.read(inputStream));
          }
        case CSV:
          reader = new FileReader(fileName);
          return Convert.fromCSV(reader);
        case DOT:
        case GRAPHML:
          // graph Format
          reader = new FileReader(fileName);
          return graphImport(reader, format, engine);
        case EXPRESSIONJSON:
          return expressionJSONImport(fileName);
        case JSON:
          if (dataFile != null) {
            return jsonImport(dataFile, false);
          }
          return jsonImport(fileName, false);
        case M:
          return S.Get.of(engine, pathName);
        case MAT:
          try (InputStream inputStream = new FileInputStream(file)) {//
            return Mat5Symja.importMAT(inputStream, file.getName());
          }
        case TABLE:
          reader = new FileReader(fileName);
          return Convert.fromCSV(reader);
        // Table table = Table.read().csv(file);
        // return ASTDataset.newTablesawTable(table);
        case RAWJSON:
          if (dataFile != null) {
            return jsonImport(dataFile, true);
          }
          return jsonImport(fileName, true);
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
    return F.NIL;
  }

  // public static IExpr fromCSV(FileReader reader) throws IOException {
  // EvalEngine engine = EvalEngine.get();
  // AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
  // final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
  //
  // CSVFormat csvFormat = CSVFormat.RFC4180.withDelimiter(' ');
  // Iterable<CSVRecord> records = csvFormat.parse(reader);
  // IASTAppendable rowList = F.ListAlloc(256);
  // for (CSVRecord record : records) {
  // IASTAppendable columnList = F.ListAlloc(record.size());
  // for (String string : record) {
  // final ASTNode node = parser.parse(string);
  // IExpr temp = ast2Expr.convert(node);
  // columnList.append(temp);
  // }
  // rowList.append(columnList);
  // }
  // return rowList;
  // }

  private static IExpr expressionJSONImport(String fileName)
      throws MalformedURLException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(new URL(fileName));
    return ExpressionJSONConvert.importExpressionJSONRecursive(node);
  }

  private static IExpr jsonImport(File file, boolean rawJSON)
      throws MalformedURLException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(file);
    return JSONConvert.importJSONRecursive(node, rawJSON);
  }

  private static IExpr jsonImport(String fileName, boolean rawJSON)
      throws MalformedURLException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(new URL(fileName));
    return JSONConvert.importJSONRecursive(node, rawJSON);
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

  private static IExpr graphImport(Reader reader, Extension format, EvalEngine engine)
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
