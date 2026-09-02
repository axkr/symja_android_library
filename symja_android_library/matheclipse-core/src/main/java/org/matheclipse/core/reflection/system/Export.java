package org.matheclipse.core.reflection.system;

import java.awt.image.BufferedImage;
import java.io.File;
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
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.graphics.SVGGraphics;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.io.FileSandbox;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IGraphExpr;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.io.ImageFormatIO;
import org.matheclipse.core.io.TableFormatIO;
import org.matheclipse.core.tensor.img.ImageFormat;

/** Export some data from file system. */
public class Export extends AbstractEvaluator {

  public Export() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (Config.isFileSystemEnabled(engine)) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      IStringX arg1 = (IStringX) ast.arg1();
      File outFile = FileSandbox.resolveWrite(S.Export, arg1.toString(), engine);
      if (outFile == null) {
        return F.NIL;
      }
      String filename = outFile.getPath();
      Extension format = Extension.exportFilename(arg1.toString());
      if (ast.size() == 4) {
        if (!(ast.arg3() instanceof IStringX)) {
          return F.NIL;
        }
        // format = ((IStringX) ast.arg3()).toString();
        format = Extension.exportExtension(ast.arg3().toString());
      }

      IExpr arg2 = ast.arg2();

      ImageFormatIO imageFormatIO = ImageFormatIO.get();
      if (imageFormatIO != null && imageFormatIO.canExport(format)) {
        // matheclipse-image is on the classpath, so Export writes whatever javax.imageio has a
        // writer for and accepts an Image object rather than only a matrix of pixel values
        try (OutputStream outputStream = new FileOutputStream(filename)) {
          if (imageFormatIO.exportImage(outputStream, arg2, format)) {
            return arg1;
          }
        } catch (IOException ioe) {
          return Errors.printMessage(S.Export, ioe, engine);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.Export, rex, engine);
        }
        // an image format never falls through to the text writer below, which would truncate the
        // file that was just written
        return F.NIL;
      } else if (format.equals(Extension.GIF) || format.equals(Extension.PNG)) {
        // core on its own can still write a matrix of pixel values
        try {
          if (arg2.isAST() && exportImage(filename, (IAST) arg2, format)) {
            return arg1;
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          rex.printStackTrace();
        }
      }

      if (format.equals(Extension.SVG)) {
        // Written here rather than through the text writer below: without this branch the
        // writer creates the file, finds no handler for SVG and leaves it empty.
        String svgString = SVGGraphics.svgDocument(arg2);
        if (svgString == null) {
          // Not a graphic: stay unevaluated, and in particular do not fall through to the
          // text writer, which would leave an empty .svg file behind.
          return F.NIL;
        }
        try (FileWriter writer = new FileWriter(filename)) {
          writer.write(svgString);
          return arg1;
        } catch (IOException ioe) {
          return Errors.printMessage(S.Export, ioe, engine);
        }
      }

      try (FileWriter writer = new FileWriter(filename)) {
        if (arg2 instanceof IGraphExpr) {
          ((IGraphExpr) arg2).graphExport(writer, format);
          return arg1;
        }

        if (format.equals(Extension.CSV) || format.equals(Extension.TSV)) {
          if (arg2.isDataset()) {
            // matheclipse-dataset writes the format's own separator - a tab for TSV - where the
            // IASTDataset fallback below always writes commas
            TableFormatIO tableFormatIO = TableFormatIO.get();
            if (tableFormatIO != null && tableFormatIO.canExport(format)) {
              if (tableFormatIO.exportTable(writer, arg2, format, F.NIL)) {
                return arg1;
              }
            }
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
        } else if (format.equals(Extension.FASTA)) {
          org.matheclipse.core.io.BioSequenceFormat bio =
              org.matheclipse.core.io.BioSequenceFormat.get();
          if (bio != null) {
            try (java.io.OutputStream out = new java.io.FileOutputStream(filename)) {
              if (bio.exportFASTA(out, arg2)) {
                return arg1;
              }
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
        Errors.printMessage(S.Export, ex, engine);
      } catch (IOException ioe) {
        Errors.printMessage(S.Export, ioe, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.Export, rex, engine);
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



  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }


  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
