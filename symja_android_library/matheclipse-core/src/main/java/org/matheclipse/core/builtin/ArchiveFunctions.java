package org.matheclipse.core.builtin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.io.FileSandbox;
import org.matheclipse.core.io.ZipArchive;

/**
 * Reading a file, and reading and writing ZIP archives.
 *
 * <p>
 * <code>Import</code> here is the part of it that needs nothing but the core: it reads the file and
 * hands the text to <code>ImportString</code>, which already knows the text formats. When
 * <code>matheclipse-io</code> is on the classpath it replaces this with its own, which also does
 * images, graphs, spreadsheets and biological sequence formats. A console built on the core alone -
 * <code>symjascript</code> - has the text formats either way, which is what a script that reads its
 * own configuration needs.
 *
 * <p>
 * An archive is how a paclet is shipped, so a package manager downloads one and unpacks it:
 * <code>ExtractArchive</code> and <code>CreateArchive</code>.
 */
public class ArchiveFunctions {

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.CreateArchive.setEvaluator(new CreateArchive());
        S.ExtractArchive.setEvaluator(new ExtractArchive());
        S.Import.setEvaluator(new Import());
        S.ExportByteArray.setEvaluator(new ExportByteArray());
        S.ImportByteArray.setEvaluator(new ImportByteArray());
      }
    }
  }

  /**
   * <code>Import[file]</code> and <code>Import[file, format]</code> for the formats the core can
   * read, plus <code>"ZIP"</code>.
   */
  private static class Import extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      String fileName = ast.arg1().toString();
      Extension format = Extension.importFilename(fileName);
      IExpr element = F.NIL;
      if (ast.size() > 2) {
        IExpr arg2 = ast.arg2();
        if (arg2.isList() && arg2.size() > 1) {
          // Import[file, {"ZIP", "entry"}] names the format and what to take out of it
          format = Extension.importExtension(arg2.first().toString());
          if (arg2.size() > 2) {
            element = arg2.second();
          }
        } else if (arg2 instanceof IStringX) {
          format = Extension.importExtension(arg2.toString());
        } else {
          return F.NIL;
        }
      }

      if (fileName.startsWith("!")) {
        // Import["!command", ...] reads what a shell command prints - the WLJS notebook asks the
        // shell for the user's PATH that way. Only where the session may run programs at all.
        String output = ProcessFunctions.shellCommandOutput(fileName.substring(1), engine);
        if (output == null) {
          // Cannot open `1`.
          Errors.printMessage(S.Import, "noopen", F.list(ast.arg1()), engine);
          return S.$Failed;
        }
        if (format == Extension.STRING || format == Extension.TXT || format == Extension.DAT) {
          // like a text file, without the line end the command finished with
          return F.stringx(output.replaceAll("[\\r\\n]+$", ""));
        }
        return engine.evaluate(F.binaryAST2(S.ImportString, F.stringx(output),
            F.stringx(formatName(format, fileName))));
      }

      Path file = FileSandbox.resolveReadPath(S.Import, fileName, engine);
      if (file == null) {
        return F.NIL;
      }
      if (!Files.isRegularFile(file)) {
        // Cannot open `1`.
        Errors.printMessage(S.Import, "noopen", F.list(ast.arg1()), engine);
        return S.$Failed;
      }
      try {
        if (format == Extension.ZIP) {
          return importZip(file, element, engine);
        }
        if (format == Extension.M) {
          return engine.evaluate(F.Get(F.stringx(file.toString())));
        }
        String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        if (format == Extension.STRING || format == Extension.TXT || format == Extension.DAT) {
          // "String" and "Text" are the file as it stands. ImportString reads "String" as Symja
          // source, which is not what Import means by it, and a file with no usable extension is
          // read as text rather than not at all.
          return F.stringx(content);
        }
        return engine.evaluate(F.binaryAST2(S.ImportString, F.stringx(content),
            F.stringx(formatName(format, fileName))));
      } catch (IOException | RuntimeException ex) {
        Errors.rethrowsInterruptException(ex);
        Errors.printMessage(S.Import, ex, engine);
        return S.$Failed;
      }
    }

    /**
     * What is in the archive: the entry names, one named entry's content, or an association of all
     * of them.
     */
    private static IExpr importZip(Path file, IExpr element, EvalEngine engine)
        throws IOException {
      if (element.isPresent() && element.isString()) {
        List<ZipArchive.Entry> entries =
            ZipArchive.read(file, java.util.Collections.singletonList(element.toString()));
        if (entries.isEmpty()) {
          return S.$Failed;
        }
        return F.stringx(entries.get(0).contentAsString());
      }
      if (element.isPresent() && element.isList()) {
        List<String> wanted = new ArrayList<String>();
        IAST names = (IAST) element;
        for (int i = 1; i < names.size(); i++) {
          wanted.add(names.get(i).toString());
        }
        List<ZipArchive.Entry> entries = ZipArchive.read(file, wanted);
        IASTAppendable result = F.ListAlloc(entries.size());
        for (ZipArchive.Entry entry : entries) {
          result.append(F.stringx(entry.contentAsString()));
        }
        return result;
      }
      List<String> names = ZipArchive.names(file);
      IASTAppendable result = F.ListAlloc(names.size());
      for (String name : names) {
        result.append(F.stringx(name));
      }
      return result;
    }

    /** The format name {@code ImportString} knows this format under. */
    private static String formatName(Extension format, String fileName) {
      if (format == Extension.DAT) {
        // no usable extension: read it as text, which is what Import does with an unknown file
        return "Text";
      }
      return format.toString();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>ExportByteArray[expr, format]</code>: what <code>ExportString</code> writes, as bytes.
   *
   * <p>
   * This is how a notebook front end sends an expression down a socket -
   * <code>ExportByteArray[#, "ExpressionJSON"]&amp;</code> is the WLJS default serializer.
   */
  private static class ExportByteArray extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr format = ast.size() > 2 ? ast.arg2() : F.stringx("ExpressionJSON");
      IExpr text = engine.evaluate(F.binaryAST2(S.ExportString, ast.arg1(), format));
      if (!text.isString()) {
        return F.NIL;
      }
      return org.matheclipse.core.expression.data.ByteArrayExpr
          .newInstance(text.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /** <code>ImportByteArray[bytes, format]</code>: the other direction. */
  private static class ImportByteArray extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      byte[] bytes;
      if (arg1 instanceof org.matheclipse.core.expression.data.ByteArrayExpr) {
        bytes = ((org.matheclipse.core.expression.data.ByteArrayExpr) arg1).toData();
      } else if (arg1.isString()) {
        bytes = arg1.toString().getBytes(StandardCharsets.UTF_8);
      } else {
        return F.NIL;
      }
      IExpr format = ast.size() > 2 ? ast.arg2() : F.stringx("Text");
      return engine.evaluate(F.binaryAST2(S.ImportString,
          F.stringx(new String(bytes, StandardCharsets.UTF_8)), format));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /** <code>ExtractArchive[archive, directory]</code>: unpack an archive, answering what it wrote. */
  private static class ExtractArchive extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      Path archive = FileSandbox.resolveReadPath(S.ExtractArchive, ast.arg1().toString(), engine);
      if (archive == null) {
        return F.NIL;
      }
      Path directory;
      if (ast.size() > 2 && ast.arg2() instanceof IStringX) {
        directory = FileSandbox.resolveWritePath(S.ExtractArchive, ast.arg2().toString(), engine);
      } else {
        directory = FileSandbox.workingDirectory(engine);
      }
      if (directory == null) {
        return F.NIL;
      }
      try {
        List<Path> written = ZipArchive.extract(archive, directory);
        IASTAppendable result = F.ListAlloc(written.size());
        for (Path path : written) {
          result.append(F.stringx(path.toString()));
        }
        return result;
      } catch (IOException | RuntimeException ex) {
        Errors.rethrowsInterruptException(ex);
        Errors.printMessage(S.ExtractArchive, ex, engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>CreateArchive[directory, archive]</code> and
   * <code>CreateArchive[{file, …}, archive]</code>.
   */
  private static class CreateArchive extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      if (ast.size() < 3 || !(ast.arg2() instanceof IStringX)) {
        return F.NIL;
      }
      Path archive = FileSandbox.resolveWritePath(S.CreateArchive, ast.arg2().toString(), engine);
      if (archive == null) {
        return F.NIL;
      }
      try {
        List<ZipArchive.Entry> entries = new ArrayList<ZipArchive.Entry>();
        IExpr arg1 = ast.arg1();
        if (arg1 instanceof IStringX) {
          Path source = FileSandbox.resolveReadPath(S.CreateArchive, arg1.toString(), engine);
          if (source == null) {
            return F.NIL;
          }
          if (Files.isDirectory(source)) {
            entries.addAll(ZipArchive.entriesOf(source));
          } else {
            entries.add(entryOf(source));
          }
        } else if (arg1.isList()) {
          IAST names = (IAST) arg1;
          for (int i = 1; i < names.size(); i++) {
            Path source =
                FileSandbox.resolveReadPath(S.CreateArchive, names.get(i).toString(), engine);
            if (source != null && Files.isRegularFile(source)) {
              entries.add(entryOf(source));
            }
          }
        } else {
          return F.NIL;
        }
        ZipArchive.write(archive, entries);
        return F.stringx(archive.toString());
      } catch (IOException | RuntimeException ex) {
        Errors.rethrowsInterruptException(ex);
        Errors.printMessage(S.CreateArchive, ex, engine);
        return S.$Failed;
      }
    }

    private static ZipArchive.Entry entryOf(Path file) throws IOException {
      return new ZipArchive.Entry(file.getFileName().toString(), Files.readAllBytes(file));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ArchiveFunctions() {}
}
