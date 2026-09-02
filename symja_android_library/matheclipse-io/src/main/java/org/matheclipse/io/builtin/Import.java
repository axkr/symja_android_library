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
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.ExpressionJSONConvert;
import org.matheclipse.core.convert.JSONConvert;
import org.matheclipse.core.convert.matlab.Mat5Symja;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.io.FileSandbox;
import org.matheclipse.core.io.ImageFormatIO;
import org.matheclipse.core.io.TableFormatIO;
import org.matheclipse.io.tensor.io.ImageFormat;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Import some data from file system. */
public class Import extends AbstractEvaluator {
  private static final Logger LOGGER = LogManager.getLogger(Import.class);

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
        format = Extension.importExtension(ast.arg2().toString());
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
      File file;
      if (dataFile != null) {
        // an already resolved file from an internal caller
        file = dataFile;
      } else {
        file = FileSandbox.resolveRead(S.Import, fileName, engine);
        if (file == null) {
          return F.NIL;
        }
      }
      switch (format) {
        case BMP:
        case GIF:
        case ICO:
        case JPEG:
        case PNG:
        case PNM:
        case PSD:
        case TGA:
        case TIFF:
        case WEBP:
          // note that this reads bytes, where it used to wrap a FileReader in a ReaderInputStream
          // and so pushed the file through the platform charset on the way in
          try (InputStream inputStream = new FileInputStream(file)) {
            ImageFormatIO imageFormatIO = ImageFormatIO.get();
            if (imageFormatIO != null && imageFormatIO.canImport(format)) {
              IExpr image = imageFormatIO.importImage(inputStream, format);
              if (image.isPresent()) {
                return image;
              }
              return F.NIL;
            }
          }
          // without matheclipse-image the reader falls back to a matrix of pixel values
          try (InputStream inputStream = new FileInputStream(file)) {
            return ImageFormat.from(ImageIO.read(inputStream));
          }
        case CSV:
        case TSV:
          // with matheclipse-dataset on the classpath these read as a Dataset, which is what the
          // reference does; without it they fall back to the nested list Convert.fromCSV builds.
          // TABLE below is deliberately left alone, so that Import(..., "Table") and
          // Export(..., "Table") stay a matched pair for plain matrices.
          try (InputStream inputStream = new FileInputStream(file)) {
            TableFormatIO tableFormatIO = TableFormatIO.get();
            if (tableFormatIO != null && tableFormatIO.canImport(format)) {
              IExpr dataset = tableFormatIO.importTable(inputStream, format, F.NIL);
              if (dataset.isPresent()) {
                return dataset;
              }
            }
          }
          reader = new FileReader(file);
          return Convert.fromCSV(reader);
        case XLSX:
          // no fallback: reading a workbook needs matheclipse-dataset
          try (InputStream inputStream = new FileInputStream(file)) {
            TableFormatIO tableFormatIO = TableFormatIO.get();
            if (tableFormatIO != null && tableFormatIO.canImport(format)) {
              return tableFormatIO.importTable(inputStream, format, F.NIL);
            }
          }
          return F.NIL;
        case DOT:
        case GRAPHML:
          // graph Format
          reader = new FileReader(file);
          return org.matheclipse.graphtheory.io.GraphImport.fromReader(reader, format, engine);
        case EXPRESSIONJSON:
          return expressionJSONImport(fileName);
        case FASTA:
          return org.matheclipse.bio.io.BioSequenceImport.importFASTA(file, false);
        case GENBANK:
          return org.matheclipse.bio.io.BioSequenceImport.importGenBank(file, false);
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
          reader = new FileReader(file);
          return Convert.fromCSV(reader);
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
      Errors.rethrowsInterruptException(ex);
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

}
