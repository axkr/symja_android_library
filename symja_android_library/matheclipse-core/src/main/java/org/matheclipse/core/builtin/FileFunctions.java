package org.matheclipse.core.builtin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.PackageUtil;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.FileExpr;
import org.matheclipse.core.expression.data.InputStreamExpr;
import org.matheclipse.core.expression.data.NumericArrayExpr;
import org.matheclipse.core.expression.data.NumericArrayExpr.RangeException;
import org.matheclipse.core.expression.data.NumericArrayExpr.TypeException;
import org.matheclipse.core.expression.data.OutputStreamExpr;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.io.Extension;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.math.MathException;
import com.google.common.io.CharStreams;

public class FileFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {

        S.BeginPackage.setEvaluator(new BeginPackage());
        S.BinaryRead.setEvaluator(new BinaryRead());
        S.BinaryWrite.setEvaluator(new BinaryWrite());
        S.EndPackage.setEvaluator(new EndPackage());
        S.Begin.setEvaluator(new Begin());
        S.Close.setEvaluator(new Close());
        S.Compress.setEvaluator(new Compress());
        S.CreateDirectory.setEvaluator(new CreateDirectory());
        S.CreateFile.setEvaluator(new CreateFile());
        S.End.setEvaluator(new End());
        S.File.setEvaluator(new FileEvaluator());
        S.FileFormat.setEvaluator(new FileFormat());
        S.FilePrint.setEvaluator(new FilePrint());
        S.FindList.setEvaluator(new FindList());
        S.Get.setEvaluator(new Get());
        S.InputStream.setEvaluator(new InputStream());
        S.Needs.setEvaluator(new Needs());
        S.OpenAppend.setEvaluator(new OpenAppend());
        S.OpenRead.setEvaluator(new OpenRead());
        S.OpenWrite.setEvaluator(new OpenWrite());
        S.OutputStream.setEvaluator(new OutputStream());
        S.Put.setEvaluator(new Put());
        S.Read.setEvaluator(new Read());
        S.ReadLine.setEvaluator(new ReadLine());
        S.ReadList.setEvaluator(new ReadList());
        S.ReadString.setEvaluator(new ReadString());
        S.Save.setEvaluator(new Save());
        S.StringToStream.setEvaluator(new StringToStream());
        S.Uncompress.setEvaluator(new Uncompress());
        S.URLDecode.setEvaluator(new URLDecode());
        S.URLEncode.setEvaluator(new URLEncode());
        S.URLFetch.setEvaluator(new URLFetch());
        S.Write.setEvaluator(new Write());
        S.WriteString.setEvaluator(new WriteString());
      }
    }
  }

  private static final class Begin extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      String contextName = Validate.checkContextName(ast, 1);
      org.matheclipse.core.expression.Context pack =
          EvalEngine.get().getContextPath().currentContext();
      org.matheclipse.core.expression.Context context = engine.begin(contextName, pack);
      return F.stringx(context.completeContextName());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class BeginPackage extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      String contextName = Validate.checkContextName(ast, 1);
      engine.beginPackage(contextName);
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        if (arg2.isList()) {
          return arg2.mapThread(F.Needs(F.Slot1), 1).eval(engine);
        }
        return engine.evaluate(F.Needs(arg2));
      }

      if (Config.isFileSystemEnabled(engine)) {
        for (int j = 2; j < ast.size(); j++) {
          try (FileInputStream fis = new FileInputStream(ast.get(j).toString());
              Reader r = new InputStreamReader(fis, StandardCharsets.UTF_8);
              BufferedReader reader =
                  new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));) {
            PackageUtil.loadPackage(engine, reader);
          } catch (IOException e) {
            Errors.printMessage(S.BeginPackage, e);
          }
        }
      }
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class BinaryRead extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          final DataInput reader = getDataInput(ast.arg1(), engine);
          if (reader != null) {
            if (ast.isAST2()) {
              IExpr typeExpr = ast.arg2();
              if (typeExpr.isList()) {
                return F.mapList((IAST) typeExpr, t -> readType(reader, t.toString()));
              }
            }
            String typeStr = ast.isAST2() ? ast.arg2().toString() : "UnsignedInteger8";
            return readType(reader, typeStr);
          }
        } catch (EOFException ex) {
          return S.EndOfFile;
        } catch (IOException | RuntimeException ex) {
          Errors.printMessage(S.BinaryRead, ex);
          return S.$Failed;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class BinaryWrite extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          IExpr arg1 = ast.arg1();
          DataOutput dataOutput = null;
          if (arg1 instanceof FileExpr) {
            File file = ((FileExpr) arg1).toData();
            OutputStreamExpr out = OutputStreamExpr.newInstance(file, false);
            dataOutput = out.getDataOutput();
          }
          if (arg1 instanceof OutputStreamExpr) {
            dataOutput = ((OutputStreamExpr) arg1).getDataOutput();
          }
          if (dataOutput != null) {
            IExpr arg2 = ast.arg2();
            String typeStr = ast.isAST3() ? ast.arg3().toString() : "UnsignedInteger8";
            if (typeStr.equals("Byte")) {
              typeStr = "UnsignedInteger8";
            }
            byte typeByte = NumericArrayExpr.toType(typeStr);
            if (typeByte == NumericArrayExpr.UNDEFINED) {
              return S.$Failed;
            }
            if (arg2.isList()) {
              IAST list = (IAST) arg2;
              for (int i = 1; i < list.size(); i++) {

                if (!writeType(dataOutput, list.get(i), typeByte)) {
                  return S.$Failed;
                }
              }
              return S.Null;
            }
            writeType(dataOutput, arg2, typeByte);
            return S.Null;
          }
        } catch (RuntimeException | RangeException | TypeException | IOException ex) {
          Errors.rethrowsInterruptException(ex);
          Errors.printMessage(S.BinaryWrite, ex);
          return F.$Failed;
        }
      }
      return F.NIL;
    }

    private static boolean writeType(DataOutput outputChannel, IExpr arg, byte typeByte)
        throws RangeException, TypeException, IllegalArgumentException, IOException {
      switch (typeByte) {
        case NumericArrayExpr.Integer8:
          outputChannel.writeByte(NumericArrayExpr.getByte(arg));
          return true;
        case NumericArrayExpr.UnsignedInteger8:
          outputChannel.writeByte(NumericArrayExpr.getUnsignedByte(arg));
          return true;
      }
      return false;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  private static final class Close extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr arg1 = ast.arg1();
        if (arg1 instanceof OutputStreamExpr) {
          OutputStreamExpr out = (OutputStreamExpr) arg1;
          out.close();
          return F.stringx(out.getStreamName());
        }
        if (arg1 instanceof InputStreamExpr) {
          InputStreamExpr in = (InputStreamExpr) arg1;
          in.close();
          return F.stringx(in.getStreamName());
        }
      } catch (IOException | RuntimeException ex) {
        Errors.printMessage(S.Close, ex);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class Compress extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr expr = ast.arg1();

      // Convert expression to string in InputForm
      String inputForm = IStringX.inputForm(expr);

      // Compress and Encode
      try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
          gzipOutputStream.write(inputForm.getBytes(StandardCharsets.UTF_8));
        }
        byte[] compressedBytes = byteArrayOutputStream.toByteArray();
        return F.stringx(Base64.getEncoder().encodeToString(compressedBytes));
      } catch (IOException e) {
        throw new RuntimeException("Compression failed", e);
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  /** Create a default temporary directory */
  private static class CreateDirectory extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          if (ast.isAST0()) {
            Path tempDir = Files.createTempDirectory("");
            return F.stringx(tempDir.toString());
          } else if (ast.isAST1() && ast.arg1() instanceof IStringX) {
            Path path = Paths.get(ast.arg1().toString());
            if (!Files.exists(path)) {
              Files.createDirectory(path);
            }
            return F.stringx(path.toString());
          }
        } catch (IOException | RuntimeException ex) {
          Errors.printMessage(S.CreateDirectory, ex);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /** Create a default temporary directory */
  private static class CreateFile extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          if (ast.isAST0()) {
            Path tempFile = Files.createTempFile(null, null);
            return F.stringx(tempFile.toString());
          } else if (ast.isAST1() && ast.arg1() instanceof IStringX) {
          }
        } catch (IOException | RuntimeException ex) {
          Errors.printMessage(S.CreateFile, ex);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }
  /**
   *
   *
   * <pre>
   * <code>End( )
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * end a context definition started with <code>Begin</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Begin(&quot;mytest`&quot;)
   *
   * &gt;&gt; Context()
   * mytest`
   *
   * &gt;&gt; $ContextPath
   * {System`,Global`}
   *
   * &gt;&gt; End()
   * mytest`
   *
   * &gt;&gt; Context()
   * Global`
   *
   * &gt;&gt; $ContextPath
   * {System`,Global`}
   *
   * </code>
   * </pre>
   */
  private static final class End extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      org.matheclipse.core.expression.Context context = engine.end();
      if (context == null) {
        return F.NIL;
      }
      return F.stringx(context.completeContextName());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>EndPackage( )
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * end a package definition
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; BeginPackage(&quot;Test`&quot;)
   *
   * &gt;&gt; $ContextPath
   * {Test`,System`}
   *
   * &gt;&gt; EndPackage( )
   *
   * &gt;&gt; $ContextPath
   * {Test`,System`,Global`}
   * </code>
   * </pre>
   */
  private static final class EndPackage extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      engine.endPackage();
      return S.Null;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static final class FileEvaluator extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          IExpr arg1 = ast.arg1();
          if (arg1.isString()) {
            return FileExpr.newInstance(arg1.toString());
          }
        } catch (RuntimeException ex) {
          Errors.rethrowsInterruptException(ex);
          Errors.printMessage(S.File, ex);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class FileFormat extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }

      IStringX arg1 = (IStringX) ast.arg1();
      Extension format = Extension.importFilename(arg1.toString());
      if (format.equals(Extension.STRING)) {
        // no format was suitable
        return S.None;
      }
      return F.stringx(format.toString());
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class FilePrint extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        IExpr arg1 = ast.arg1();

        if (arg1.isString()) {
          try (BufferedReader br = new BufferedReader(new FileReader(arg1.toString()))) {
            final PrintStream stream = engine.getOutPrintStream();
            String line;
            int numberOfLines = Integer.MAX_VALUE;
            if (ast.isAST2()) {
              numberOfLines = ast.arg2().toIntDefault();
              if (F.isNotPresent(numberOfLines)) {
                return F.NIL;
              }
            }
            if (numberOfLines < 0) {
              numberOfLines = -numberOfLines;
              String[] lastLines = new String[numberOfLines];
              int i = 0;
              while ((line = br.readLine()) != null && i < numberOfLines) {
                lastLines[i++] = line;
              }
              while ((line = br.readLine()) != null) {
                System.arraycopy(lastLines, 1, lastLines, 0, numberOfLines - 1);
                lastLines[numberOfLines - 1] = line;
              }
              i = 0;
              while (i < lastLines.length && lastLines[i] != null) {
                stream.println(lastLines[i++]);
              }
            } else {
              while ((line = br.readLine()) != null && numberOfLines-- > 0) {
                stream.println(line);
              }
            }
            return S.Null;

          } catch (IOException | RuntimeException ex) {
            Errors.printMessage(S.FilePrint, ex);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class FindList extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          IAST searchStrings = Validate.checkListOfStrings(ast, 2, S.FindList, engine);
          if (searchStrings.isNIL()) {
            return F.NIL;
          }
          if (searchStrings.isEmpty()) {
            return F.CEmptyList;
          }

          final DataInput reader = getDataInput(ast.arg1(), engine);
          if (reader != null) {
            int n = Integer.MAX_VALUE;
            if (ast.isAST3()) {
              n = ast.arg3().toIntDefault();
              if (n <= 0) {
                // Non-negative machine-sized integer expected at position `2` in `1`
                return Errors.printMessage(S.FindList, "intnm", F.List(F.C3, ast), engine);
              }
            }

            IASTAppendable result = F.ListAlloc();
            int counter = 0;
            try {
              do {
                String line = reader.readLine();
                if (line == null) {
                  break;
                }
                for (int i = 1; i < searchStrings.size(); i++) {
                  String searchText = searchStrings.get(i).toString();
                  if (line.indexOf(searchText) >= 0) {
                    result.append(F.stringx(line));
                    counter++;
                    break;
                  }
                }

                if (counter >= n) {
                  break;
                }
              } while (true);
            } catch (IOException e) {
              //
            }
            if (result.isPresent()) {
              return result;
            }
          }
        } catch (EOFException ex) {
          return S.EndOfFile;
        } catch (IOException | RuntimeException ex) {
          Errors.printMessage(S.FindList, ex);
          return S.$Failed;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /** Get[{&lt;file name&gt;}} */
  public static class Get extends AbstractFunctionEvaluator {

    /**
     * Load a package from the given reader
     *
     * @param engine
     * @param is
     * @return the last evaluated expression result
     */
    protected static IExpr loadPackage(final EvalEngine engine, final String is) {
      try {
        final List<ASTNode> node = PackageUtil.parseReader(is, engine);
        return PackageUtil.evaluatePackage(node, engine);
      } catch (final RuntimeException rex) {
        Errors.printMessage(S.Get, rex, engine);
      }
      return S.Null;
    }



    private static IExpr getFile(Path file, IAST ast, String arg1Str, EvalEngine engine) {
      boolean packageMode = engine.isPackageMode();
      String input = engine.get$Input();
      String inputFileName = engine.get$InputFileName();
      try {
        engine.setPackageMode(true);
        engine.set$Input(arg1Str);
        engine.set$InputFileName(file.toAbsolutePath().toString());
        String str = Files.readString(file, Charset.defaultCharset());
        return Get.loadPackage(engine, str);
      } catch (IOException e) {
        // LOGGER.debug("Get.getFile() failed", e);
        // Cannot open `1`.
        return Errors.printMessage(S.Get, "noopen", F.list(ast.arg1()), engine);
      } finally {
        engine.setPackageMode(packageMode);
        engine.set$Input(input);
        engine.set$InputFileName(inputFileName);
      }
    }

    private static IExpr getURL(URL url, IAST ast, String arg1Str, EvalEngine engine) {
      boolean packageMode = engine.isPackageMode();
      String input = engine.get$Input();
      String inputFileName = engine.get$InputFileName();
      try (java.io.InputStream in = url.openStream()) {
        engine.setPackageMode(true);
        engine.set$Input(arg1Str);
        engine.set$InputFileName(url.getPath());
        String str = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        return loadPackage(engine, str);
      } catch (IOException e) {
        // LOGGER.debug("FileFunctions.Get.getURL() failed", e);
        // Cannot open `1`.
        return Errors.printMessage(S.Get, "noopen", F.list(ast.arg1()), engine);
      } finally {
        engine.setPackageMode(packageMode);
        engine.set$Input(input);
        engine.set$InputFileName(inputFileName);
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          if (!(ast.arg1() instanceof IStringX)) {
            // String expected at position `1` in `2`.
            return Errors.printMessage(S.Get, "string", F.List(), engine);
          }
          String arg1Str = ast.arg1().toString();
          if (arg1Str.startsWith("https://") || arg1Str.startsWith("http://")) {
            URL url = new URL(arg1Str);
            return getURL(url, ast, arg1Str, engine);
          }
          Path file = Path.of(arg1Str);
          if (Files.isRegularFile(file)) {
            return getFile(file, ast, arg1Str, engine);
          } else {
            file = file.toAbsolutePath();
            if (Files.isRegularFile(file)) {
              return getFile(file, ast, arg1Str, engine);
            }
          }
          Validate.checkContextName(ast, 1);
        } catch (ValidateException ve) {
          return Errors.printMessage(S.Get, ve, engine);
        } catch (MalformedURLException e) {
          return Errors.printMessage(S.Get, e, engine);
        }
        // Cannot open `1`.
        return Errors.printMessage(S.Get, "noopen", F.list(ast.arg1()), engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class InputStream extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          IExpr arg1 = ast.arg1();
          if (arg1.isString()) {
            return InputStreamExpr.newInstance(arg1.toString(), "String");
          }
        } catch (FileNotFoundException | RuntimeException ex) {
          Errors.printMessage(S.InputStream, ex);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class OpenAppend extends OpenWrite {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return openOutputStream(ast, true, engine);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  private static final class OpenRead extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(EvalEngine.get())) {
        try {
          if (ast.isAST1()) {
            IExpr arg1 = ast.arg1();
            if (arg1.isString()) {
              return InputStreamExpr.newInstance(arg1.toString(), "String");
            }
          }
        } catch (FileNotFoundException | RuntimeException ex) {
          Errors.printMessage(S.OpenRead, ex);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  private static class OpenWrite extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return openOutputStream(ast, false, engine);
    }

    /**
     * @param ast
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather
     *        than the beginning
     * @param engine
     * @return
     */
    protected IExpr openOutputStream(final IAST ast, boolean append, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          if (ast.isAST0()) {
            return OutputStreamExpr.newInstance();
          }
          if (ast.isAST1()) {
            IExpr arg1 = ast.arg1();
            if (arg1.isString()) {
              return OutputStreamExpr.newInstance(arg1.toString(), append);
            }
          }
        } catch (IOException | RuntimeException ex) {
          Errors.printMessage(S.OpenWrite, ex);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  private static final class OutputStream extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          IExpr arg1 = ast.arg1();
          if (arg1.isString()) {
            return OutputStreamExpr.newInstance(arg1.toString(), false);
          }
        } catch (IOException | RuntimeException ex) {
          Errors.printMessage(S.OutputStream, ex);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class Needs extends Get {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      String contextName = Validate.checkContextName(ast, 1);
      if (!ContextPath.PACKAGES.contains(contextName)) {
        return super.evaluate(ast, engine);
      }
      return S.Null;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }
  /** Put[{&lt;file name&gt;}} */
  private static final class Put extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {

        final int argSize = ast.argSize();
        if (!(ast.last() instanceof IStringX)) {
          // String expected at position `1` in `2`.
          return Errors.printMessage(S.Put, "string", F.List(F.ZZ(argSize), ast), engine);
        }
        IStringX fileName = (IStringX) ast.last();

        final StringBuilder buf = new StringBuilder();
        for (int i = 1; i < argSize; i++) {
          IExpr temp = engine.evaluate(ast.get(i));
          if (!OutputFormFactory.get().convert(buf, temp)) {
            Errors.printMessage(S.Put, "error",
                F.List("File " + fileName + " ERROR-IN_OUTPUTFORM."));
            // LOGGER.log(engine.getLogLevel(), "Put: file {} ERROR-IN_OUTPUTFORM", fileName);
            return F.NIL;
          }
          buf.append('\n');
          if (i < argSize - 1) {
            buf.append('\n');
          }
        }
        try (FileWriter writer = new FileWriter(fileName.toString())) {
          writer.write(buf.toString());
        } catch (IOException ex) {
          Errors.printMessage(S.Put, ex);
          return F.NIL;
        }
        return S.Null;
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }
  }

  private static final class Read extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          IExpr arg1 = ast.arg1();
          Reader reader = null;
          if (arg1 instanceof FileExpr) {
            InputStreamExpr in = InputStreamExpr.getFromFile((FileExpr) arg1, "String", engine);
            reader = in.getReader();
          } else if (arg1 instanceof InputStreamExpr) {
            reader = ((InputStreamExpr) arg1).getReader();
          }
          if (reader != null) {
            if (ast.isAST1()) {
              String str = CharStreams.toString(reader);
              return S.ToExpression.of(engine, F.stringx(str));
            }
            IExpr typeExpr = ast.arg2();
            if (typeExpr.isList()) {
              IAST list = (IAST) ast.arg2();
              IASTAppendable result = F.ListAlloc(list.size());
              for (int i = 1; i < list.size(); i++) {
                IExpr argTypeExpr = list.get(i);
                IExpr temp = readTypeOrHold(argTypeExpr, reader, engine);
                if (temp == S.$Failed) {
                  return S.$Failed;
                }
                result.append(temp);
              }

              return result;
            }
            return readTypeOrHold(typeExpr, reader, engine);
          }
        } catch (IOException | RuntimeException ex) {
          Errors.printMessage(S.Read, ex);
          return S.$Failed;
        }
      }
      return F.NIL;
    }

    private IExpr readTypeOrHold(IExpr arg, Reader reader, EvalEngine engine) throws IOException {
      if (arg.isAST(S.Hold, 2)) {
        IExpr holdArg = arg.first();
        if (!holdArg.isBuiltInSymbol()) {
          return F.$Failed;
        }
        IExpr temp = readType((IBuiltInSymbol) holdArg, reader, engine);
        if (temp.isPresent()) {
          return F.Hold(temp);
        }
      } else if (!arg.isBuiltInSymbol()) {
        return F.$Failed;
      }
      return readType((IBuiltInSymbol) arg, reader, engine);
    }

    private IExpr readType(IBuiltInSymbol arg2, Reader reader, EvalEngine engine)
        throws IOException {
      int headID = arg2.ordinal();
      int numberOfChar = -1;
      if (headID > 0) {

        switch (headID) {
          case ID.Byte:
            // TODO
            return F.$Failed;
          case ID.Character:
            char[] temp = new char[1];
            numberOfChar = reader.read(temp);
            if (numberOfChar < 0) {
              return S.EndOfFile;
            }
            return F.$str(temp[0]);
          case ID.Expression:
            StringBuilder buf = new StringBuilder();
            char[] tempExpr = new char[1];
            numberOfChar = reader.read(tempExpr);
            if (numberOfChar < 0) {
              return S.EndOfFile;
            }
            buf.append(tempExpr[0]);
            while (true) {
              numberOfChar = reader.read(tempExpr);
              if (numberOfChar < 0) {
                break;
              }
              buf.append(tempExpr[0]);
              if (tempExpr[0] == '\n') {
                try {
                  String str = buf.toString();
                  ExprParser parser = new ExprParser(engine);
                  return parser.parse(str);
                } catch (SyntaxError se) {
                  // the string is probably not a complete expression
                  continue;
                }
              }
            }
            try {
              String str = buf.toString();
              ExprParser parser = new ExprParser(engine);
              return parser.parse(str);
            } catch (SyntaxError se) {

            }
            return F.$Failed;
          case ID.Number:
            IAST numberSeparators = F.List(F.stringx("0"), //
                F.stringx("1"), F.stringx("2"), F.stringx("3"), F.stringx("4"), F.stringx("5"),
                F.stringx("6"), F.stringx("7"), F.stringx("8"), F.stringx("9"));
            String tempNumber = numberReader(reader, numberSeparators);
            if (tempNumber == null) {
              return S.EndOfFile;
            }
            return F.$str(tempNumber);

          case ID.Real:
            // TODO
            return F.$Failed;
          case ID.Record:
            IExpr recordSeparators = engine.evaluate(S.RecordSeparators);
            if (recordSeparators.isAST()) {
              String tempRecord = separatorReader(reader, (IAST) recordSeparators);
              if (tempRecord == null) {
                return S.$Failed;
              }
              return F.$str(tempRecord);
            }
            return F.$Failed;
          case ID.String:
            BufferedReader sReader = new BufferedReader(reader);
            String tempStr = sReader.readLine();
            if (tempStr == null) {
              return S.EndOfFile;
            }
            return F.$str(tempStr + "\n");
          case ID.Word:
            IExpr wordSeparators = engine.evaluate(S.WordSeparators);
            if (wordSeparators.isAST()) {
              String tempWord = separatorReader(reader, (IAST) wordSeparators);
              if (tempWord == null) {
                return S.$Failed;
              }
              return F.$str(tempWord);
            }
            return F.$Failed;
          default:
            break;
        }
      }
      return F.$Failed;
    }

    private static String numberReader(Reader reader, IAST wordSeparators) throws IOException {

      try {
        StringBuilder word = new StringBuilder();
        char[] temp = new char[1];
        reader.mark(256);
        int numberOfChar = reader.read(temp);
        if (numberOfChar < 0) {
          return null;
        }

        int indx = wordSeparators.indexOf(F.stringx(temp[0]));
        if (indx > 0) {
          word.append(temp[0]);
        } else {
          reader.reset();
          return word.toString();
        }
        while (true) {
          reader.mark(256);
          numberOfChar = reader.read(temp);
          if (numberOfChar < 0) {
            break;
          }
          indx = wordSeparators.indexOf(F.stringx(temp[0]));
          if (indx > 0) {
            word.append(temp[0]);
          } else {
            reader.reset();
            return word.toString();
          }
        }
        return word.toString();
      } catch (IOException ioex) {
        reader.reset();
      }
      return null;
    }

    private static String separatorReader(Reader reader, IAST wordSeparators) throws IOException {
      reader.mark(256);
      try {
        StringBuilder word = new StringBuilder();
        char[] temp = new char[1];
        int numberOfChar = reader.read(temp);
        if (numberOfChar < 0) {
          return null;
        }

        while (true) {
          int indx = wordSeparators.indexOf(F.stringx(temp[0]));
          if (indx < 0) {
            word.append(temp[0]);
            break;
          }
          numberOfChar = reader.read(temp);
          if (numberOfChar < 0) {
            return word.toString();
          }
        }
        while (true) {
          numberOfChar = reader.read(temp);
          if (numberOfChar < 0) {
            break;
          }
          int indx = wordSeparators.indexOf(F.stringx(temp[0]));
          if (indx < 0) {
            word.append(temp[0]);
          } else {
            return word.toString();
          }
        }
        return word.toString();
      } catch (IOException ioex) {
        reader.reset();
      }
      return null;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }
  private static final class ReadLine extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          final DataInput reader = getDataInput(ast.arg1(), engine);
          if (reader != null) {
            try {
              String line = reader.readLine();
              if (line != null) {
                return F.stringx(line);
              }
            } catch (IOException e) {
              //
            }
          }
        } catch (EOFException ex) {
          return S.EndOfFile;
        } catch (IOException | RuntimeException ex) {
          Errors.printMessage(S.ReadLine, ex);
          return S.$Failed;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class ReadList extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          final DataInput reader = getDataInput(ast.arg1(), engine);
          if (reader != null) {
            if (ast.argSize() >= 2) {
              IExpr typeExpr = ast.arg2();
              if (typeExpr.isList()) {
                return F.mapList((IAST) typeExpr, t -> readType(reader, t.toString()));
              }
            }
            int n = Integer.MAX_VALUE;
            if (ast.isAST3()) {
              n = ast.arg3().toIntDefault();
              if (n <= 0) {
                // Non-negative machine-sized integer expected at position `2` in `1`
                return Errors.printMessage(S.ReadList, "intnm", F.List(F.C3, ast), engine);
              }
            }
            IExpr type = ast.isAST2() ? ast.arg2() : S.Expression;
            if (type == S.Expression || type == S.String) {
              IASTAppendable result = F.ListAlloc();
              int counter = 0;
              try {
                do {
                  String line = reader.readLine();
                  if (line == null) {
                    break;
                  }
                  if (line.trim().length() == 0) {
                    continue;
                  }
                  if (type == S.String) {
                    result.append(F.stringx(line));
                    counter++;
                  } else {
                    IExpr toExpr = S.ToExpression.ofNIL(engine, F.stringx(line));
                    if (toExpr.isPresent() && toExpr != S.$Failed) {
                      result.append(toExpr);
                      counter++;
                    }
                  }
                  if (counter >= n) {
                    break;
                  }
                } while (true);
              } catch (IOException e) {
                //
              }
              if (result.isPresent()) {
                return result;
              }
            }
          }
        } catch (EOFException ex) {
          return S.EndOfFile;
        } catch (IOException | RuntimeException ex) {
          Errors.printMessage(S.ReadList, ex);
          return S.$Failed;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static final class ReadString extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {

        if (!(ast.arg1() instanceof IStringX)) {
          return Errors.printMessage(ast.topHead(), "string", F.List(), engine);
        }
        String arg1 = ast.arg1().toString();
        if (arg1.startsWith("https://") || arg1.startsWith("http://")) {
          try (java.io.InputStream in = new URL(arg1).openStream()) {
            String str = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return F.stringx(str);
          } catch (IOException ex) {
            return Errors.printMessage(S.ReadString, ex);
          }
        }
        Path file = Path.of(arg1);
        if (Files.isRegularFile(file)) {
          try {
            String str = Files.readString(file, Charset.defaultCharset());
            return F.stringx(str);
          } catch (IOException ex) {
            Errors.printMessage(S.ReadString, ex);
          }
          return S.Null;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <pre>
   * <code>Save(&quot;path-to-filename&quot;, expression)
   * </code>
   * </pre>
   * 
   * <p>
   * if the file system is enabled, export the <code>FullDefinition</code> of the
   * <code>expression</code> to the &quot;path-to-filename&quot; file. The saved file can be
   * imported with <code>Get</code>.
   * </p>
   * 
   * <pre>
   * <code>Save(&quot;path-to-filename&quot;, &quot;Global`*&quot;)
   * </code>
   * </pre>
   * 
   * <p>
   * if the file system is enabled, export the <code>FullDefinition</code> of all symbols in the
   * <code>&quot;Global</code>*&quot;` context to the &quot;path-to-filename&quot; file.
   * </p>
   * 
   * <h3>Examples</h3>
   * <p>
   * Save a definition with dependent symbol definitions into temporary file
   * </p>
   * 
   * <pre>
   * <code>&gt;&gt; g(x_) := x^3;
   * 
   * &gt;&gt; g(x_,y_) := f(x,y); 
   * 
   * &gt;&gt; SetAttributes(f, Listable); 
   * 
   * &gt;&gt; f(x_) := g(x^2); 
   * 
   * &gt;&gt; temp = FileNameJoin({$TemporaryDirectory, \&quot;savedlist.txt\&quot;});Print(temp); 
   * 
   * &gt;&gt; Save(temp, {f,g}) 
   *  
   * &gt;&gt; ClearAll(f,g) 
   *   
   * &gt;&gt; &quot;Attributes(f)  
   * 
   * &gt;&gt; {f(2),g(7)}
   * 
   * &gt;&gt; Get(temp) 
   *      
   * &gt;&gt; {f(2),g(7)} 
   * {64,343}
   * 
   * &gt;&gt; Attributes(f) 
   * {Listable}
   * </code>
   * </pre>
   * 
   * <h3>Related terms</h3>
   * <p>
   * <a href="BinaryDeserialize.md">BinaryDeserialize</a>,
   * <a href="BinarySerialize.md">BinarySerialize</a>, <a href="ByteArray.md">ByteArray</a>,
   * <a href="ByteArrayQ.md">ByteArrayQ</a>, <a href="Export.md">Export</a>,
   * <a href="Import.md">Import</a>
   * </p>
   */
  private static final class Save extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {

        if (!(ast.arg1() instanceof IStringX)) {
          // String expected at position `1` in `2`.
          return Errors.printMessage(S.Save, "string", F.List(F.C1, ast), engine);
        }
        IStringX fileName = (IStringX) ast.arg1();
        IExpr arg2 = ast.arg2();

        IAST symbolsList = F.NIL;
        if (arg2.isSymbol()) {
          symbolsList = arg2.makeList();
        } else if (arg2.isListOf(S.Symbol)) {
          symbolsList = (IAST) arg2;
        } else if (arg2.isString()) {
          boolean ignoreCase = false;
          symbolsList = Documentation.getSymbolsByPattern(arg2, ignoreCase, ast, engine);
        }
        if (symbolsList.isPresent()) {
          String str = ISymbol.fullDefinitionListToString(symbolsList);
          try (FileWriter writer = new FileWriter(fileName.toString())) {
            writer.write(str);
          } catch (IOException ex) {
            return Errors.printMessage(S.Save, ex);
          }
          return S.Null;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDREST);
    }
  }

  private static final class StringToStream extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          if (ast.isAST1()) {
            IExpr arg1 = ast.arg1();
            if (arg1.isString()) {
              StringReader stringReader = new StringReader(arg1.toString());
              return InputStreamExpr.newInstance(stringReader);
            }
          }
        } catch (RuntimeException | IOException ex) {
          Errors.rethrowsInterruptException(ex);
          Errors.printMessage(S.StringToStream, ex);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }


  private static final class Uncompress extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!(arg1 instanceof IStringX)) {
        return F.NIL;
      }
      String compressedString = ast.arg1().toString();

      try {
        // Decode Base64
        byte[] compressedBytes = Base64.getDecoder().decode(compressedString);

        // Decompress GZIP
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedBytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            InputStreamReader reader =
                new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8);
            BufferedReader buffered = new BufferedReader(reader)) {

          StringBuilder output = new StringBuilder();
          String line;
          while ((line = buffered.readLine()) != null) {
            output.append(line);
          }

          String inputFormStr = output.toString();
          // Parse string in InputForm back to IExpr
          return engine.evaluate(inputFormStr);
        }
      } catch (IOException e) {
        Errors.printMessage(S.Uncompress, e, engine);
      } catch (MathException e) {
        Errors.printMessage(S.Uncompress, e, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class URLDecode extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!(arg1 instanceof IStringX)) {
        return F.NIL;
      }
      String inputString = ast.arg1().toString();
      String decoded = URLDecoder.decode(inputString, StandardCharsets.UTF_8);
      return F.stringx(decoded);
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class URLEncode extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!(arg1 instanceof IStringX)) {
        if (arg1.isTrue()) {
          return F.stringx("true");
        }
        if (arg1.isFalse()) {
          return F.stringx("false");
        }
        if (arg1 == S.Null || arg1 == S.Missing || arg1 == S.None) {
          return F.CEmptyString;
        }
        return F.NIL;
      }
      String inputString = ast.arg1().toString();

      String encoded = URLEncoder.encode(inputString, StandardCharsets.UTF_8);
      // MMA style
      encoded = encoded.replace("+", "%20");
      return F.stringx(encoded);

    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }
  private static final class URLFetch extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return Errors.printMessage(ast.topHead(), "string", F.List(), engine);
      }
      String arg1Str = ast.arg1().toString();
      if (arg1Str.startsWith("https://") || arg1Str.startsWith("http://")) {
        try (java.io.InputStream in = new URL(arg1Str).openStream()) {
          String str = new String(in.readAllBytes(), StandardCharsets.UTF_8);
          return F.$s(str);
        } catch (IOException ex) {
          // Cannot open `1`.
          return Errors.printMessage(S.URLFetch, "noopen", F.list(ast.arg1()), engine);
        }
      }
      // Cannot open `1`.
      return Errors.printMessage(S.URLFetch, "noopen", F.list(ast.arg1()), engine);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static final class Write extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        try {
          IExpr arg1 = ast.arg1();
          IExpr arg2 = ast.arg2();

          Writer writer = null;
          if (arg1 instanceof FileExpr) {
            File file = ((FileExpr) arg1).toData();
            OutputStreamExpr out = OutputStreamExpr.newInstance(file, false);
            writer = out.getWriter();;
          }
          if (arg1 instanceof OutputStreamExpr) {
            writer = ((OutputStreamExpr) arg1).getWriter();;
          }
          if (writer != null) {
            String arg2String = IStringX.inputForm(arg2);
            writer.write(arg2String);
            writer.flush();
            return S.Null;
          }
        } catch (IOException | RuntimeException ex) {
          Errors.printMessage(S.Write, ex);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static final class WriteString extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {

        if (!(ast.arg1().isString())) {
          // String expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "string", F.list(F.C1, ast), engine);
        }
        if (!(ast.arg2().isString())) {
          // String expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "string", F.list(F.C2, ast), engine);
        }
        IStringX fileName = (IStringX) ast.arg1();
        IStringX str = (IStringX) ast.arg2();
        try (FileWriter writer = new FileWriter(fileName.toString())) {
          writer.write(str.toString());
        } catch (IOException ex) {
          return Errors.printMessage(S.WriteString, ex);
        }
        return S.Null;
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static DataInput getDataInput(IExpr readerExpr, EvalEngine engine)
      throws FileNotFoundException, IOException {
    if (readerExpr instanceof FileExpr) {
      InputStreamExpr stream = InputStreamExpr.getFromFile((FileExpr) readerExpr, "String", engine);
      return stream.getDataInput();
    } else if (readerExpr instanceof InputStreamExpr) {
      return ((InputStreamExpr) readerExpr).getDataInput();
    }
    return null;
  }

  private static IExpr readType(DataInput reader, String typeStr) {
    try {
      if (typeStr.equals("Byte")) {
        typeStr = "UnsignedInteger8";
      }
      byte typeByte = NumericArrayExpr.toType(typeStr);
      if (typeByte == NumericArrayExpr.UNDEFINED) {
        if (typeStr.equals("Character8")) {
          int uInt = Byte.toUnsignedInt(reader.readByte());
          char ch = (char) uInt;
          return F.stringx(ch);
        }
        return S.$Failed;
      }
      switch (typeByte) {
        case NumericArrayExpr.Integer8:
          byte sByte = reader.readByte();
          return F.ZZ(sByte);
        case NumericArrayExpr.UnsignedInteger8:
          byte uByte = reader.readByte();
          return F.ZZ(Byte.toUnsignedInt(uByte));
      }
    } catch (EOFException ex) {
      return S.EndOfFile;
    } catch (IOException e) {
    }
    return S.$Failed;
  }

  public static void initialize() {
    Initializer.init();
  }

  private FileFunctions() {}
}
