package org.matheclipse.core.builtin;

import java.io.BufferedReader;
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
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.FileExpr;
import org.matheclipse.core.expression.data.InputStreamExpr;
import org.matheclipse.core.expression.data.NumericArrayExpr;
import org.matheclipse.core.expression.data.NumericArrayExpr.RangeException;
import org.matheclipse.core.expression.data.NumericArrayExpr.TypeException;
import org.matheclipse.core.expression.data.OutputStreamExpr;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

public class FileFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

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
        S.CreateDirectory.setEvaluator(new CreateDirectory());
        S.CreateFile.setEvaluator(new CreateFile());
        S.End.setEvaluator(new End());
        S.File.setEvaluator(new FileEvaluator());
        S.FilePrint.setEvaluator(new FilePrint());
        S.Get.setEvaluator(new Get());
        S.InputStream.setEvaluator(new InputStream());
        S.Needs.setEvaluator(new Needs());
        S.OpenAppend.setEvaluator(new OpenAppend());
        S.OpenRead.setEvaluator(new OpenRead());
        S.OpenWrite.setEvaluator(new OpenWrite());
        S.OutputStream.setEvaluator(new OutputStream());
        S.Put.setEvaluator(new Put());
        S.Read.setEvaluator(new Read());
        S.ReadString.setEvaluator(new ReadString());
        S.StringToStream.setEvaluator(new StringToStream());
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
          IAST needs = ((IAST) arg2).mapThread(F.Needs(F.Slot1), 1);
          return engine.evaluate(needs);
        }
        return engine.evaluate(F.Needs(arg2));
      }

      if (Config.isFileSystemEnabled(engine)) {
        for (int j = 2; j < ast.size(); j++) {
          try (FileInputStream fis = new FileInputStream(ast.get(j).toString());
              Reader r = new InputStreamReader(fis, StandardCharsets.UTF_8);
              BufferedReader reader =
                  new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8)); ) {
            Get.loadPackage(engine, reader);
          } catch (IOException e) {
            LOGGER.debug("BeginPackage.evaluate() failed", e);
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
          IExpr arg1 = ast.arg1();
          DataInput reader = null;
          if (arg1 instanceof FileExpr) {
            InputStreamExpr stream = InputStreamExpr.getFromFile((FileExpr) arg1, "String", engine);
            reader = stream.getDataInput();
          } else if (arg1 instanceof InputStreamExpr) {
            reader = ((InputStreamExpr) arg1).getDataInput();
          }
          if (reader != null) {
            if (ast.isAST2()) {
              IExpr typeExpr = ast.arg2();
              if (typeExpr.isList()) {
                IAST list = (IAST) ast.arg2();
                IASTAppendable result = F.ListAlloc(list.size());
                for (int i = 1; i < list.size(); i++) {
                  String typeStr = list.get(i).toString();
                  IExpr temp = readType(reader, typeStr);
                  result.append(temp);
                }

                return result;
              }
            }
            String typeStr = ast.isAST2() ? ast.arg2().toString() : "UnsignedInteger8";
            return readType(reader, typeStr);
          }
        } catch (EOFException ex) {
          return S.EndOfFile;
        } catch (IOException | RuntimeException ex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
          return S.$Failed;
        }
      }
      return F.NIL;
    }

    private IExpr readType(DataInput reader, String typeStr) throws IOException {
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
      return F.$Failed;
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
        } catch (RuntimeException | RangeException | TypeException | IOException e) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), e);
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
        LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
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
          LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
        }
      }
      return F.NIL;
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
          LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
        }
      }
      return F.NIL;
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
   * <p>end a context definition started with <code>Begin</code>
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
   * <p>end a package definition
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
          LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
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
              if (numberOfLines == Integer.MIN_VALUE) {
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
            LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
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
        final List<ASTNode> node = parseReader(is, engine);
        return evaluatePackage(node, engine);
      } catch (final Exception e) {
        LOGGER.error("Get.loadPackage() failed", e);
      }
      return S.Null;
    }

    /**
     * Load a package from the given file
     *
     * @param engine
     * @param file
     * @return the last evaluated expression result
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static IExpr loadPackage(final EvalEngine engine, final File file)
        throws FileNotFoundException {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      return loadPackage(engine, reader);
    }

    /**
     * Load a package from the given reader
     *
     * @param engine
     * @param is
     * @return the last evaluated expression result
     */
    protected static IExpr loadPackage(final EvalEngine engine, final BufferedReader is) {
      try (final BufferedReader r = is) {
        final List<ASTNode> node = parseReader(r, engine);

        return evaluatePackage(node, engine);
      } catch (final Exception e) {
        LOGGER.error("FileFunctions.Get.loadPackage", e);
      }
      return S.Null;
    }

    private static IExpr getFile(File file, IAST ast, String arg1Str, EvalEngine engine) {
      boolean packageMode = engine.isPackageMode();
      String input = engine.get$Input();
      String inputFileName = engine.get$InputFileName();
      try {
        engine.setPackageMode(true);
        engine.set$Input(arg1Str);
        engine.set$InputFileName(file.getAbsolutePath());
        String str = com.google.common.io.Files.asCharSource(file, Charset.defaultCharset()).read();
        return Get.loadPackage(engine, str);
      } catch (IOException e) {
        LOGGER.debug("Get.getFile() failed", e);
        // Cannot open `1`.
        return IOFunctions.printMessage(ast.topHead(), "noopen", F.List(ast.arg1()), engine);
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
      try {
        engine.setPackageMode(true);
        engine.set$Input(arg1Str);
        engine.set$InputFileName(url.getPath());
        String str = Resources.toString(url, StandardCharsets.UTF_8);
        return loadPackage(engine, str);
      } catch (IOException e) {
        LOGGER.debug("FileFunctions.Get.getURL() failed", e);
        // Cannot open `1`.
        return IOFunctions.printMessage(ast.topHead(), "noopen", F.List(ast.arg1()), engine);
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
            return IOFunctions.printMessage(ast.topHead(), "string", F.List(), engine);
          }
          String arg1Str = ((IStringX) ast.arg1()).toString();
          if (arg1Str.startsWith("https://") || arg1Str.startsWith("http://")) {
            URL url = new URL(arg1Str);
            return getURL(url, ast, arg1Str, engine);
          }
          File file = new File(arg1Str);
          if (file.exists()) {
            return getFile(file, ast, arg1Str, engine);
          } else {
            file = FileSystems.getDefault().getPath(arg1Str).toAbsolutePath().toFile();
            if (file.exists()) {
              return getFile(file, ast, arg1Str, engine);
            }
          }
          Validate.checkContextName(ast, 1);
        } catch (ValidateException ve) {
          return IOFunctions.printMessage(ast.topHead(), ve, engine);
        } catch (MalformedURLException e) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), e);
          return F.NIL;
        }
        // Cannot open `1`.
        return IOFunctions.printMessage(ast.topHead(), "noopen", F.List(ast.arg1()), engine);
      }
      return F.NIL;
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
          LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
        }
      }
      return F.NIL;
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
          LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
        }
      }
      return F.NIL;
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
     *     than the beginning
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
          LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
        }
      }
      return F.NIL;
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
          LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
        }
      }
      return F.NIL;
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
          return IOFunctions.printMessage(S.Put, "string", F.List(), engine);
        }
        IStringX fileName = (IStringX) ast.last();

        final StringBuilder buf = new StringBuilder();
        for (int i = 1; i < argSize; i++) {
          IExpr temp = engine.evaluate(ast.get(i));
          if (!OutputFormFactory.get().convert(buf, temp)) {
            LOGGER.log(engine.getLogLevel(), "Put: file {} ERROR-IN_OUTPUTFORM", fileName);
            return F.NIL;
          }
          buf.append('\n');
          if (i < argSize - 1) {
            buf.append('\n');
          }
        }
        try (FileWriter writer = new FileWriter(fileName.toString())) {
          writer.write(buf.toString());
        } catch (IOException e) {
          LOGGER.log(engine.getLogLevel(), "Put: file {} I/O exception !", fileName, e);
          return F.NIL;
        }
        return S.Null;
      }
      return F.NIL;
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
          LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
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
            IAST numberSeparators =
                F.list(
                    F.stringx("0"), //
                    F.stringx("1"),
                    F.stringx("2"),
                    F.stringx("3"),
                    F.stringx("4"),
                    F.stringx("5"),
                    F.stringx("6"),
                    F.stringx("7"),
                    F.stringx("8"),
                    F.stringx("9"));
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class ReadString extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {

        if (!(ast.arg1() instanceof IStringX)) {
          return IOFunctions.printMessage(ast.topHead(), "string", F.List(), engine);
        }
        String arg1 = ((IStringX) ast.arg1()).toString();
        if (arg1.startsWith("https://") || arg1.startsWith("http://")) {
          URL url;
          try {
            url = new URL(arg1);
            String str;
            str = Resources.toString(url, StandardCharsets.UTF_8);
            return F.stringx(str);
          } catch (IOException ioe) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), ioe);
            return F.NIL;
          }
        }
        File file = new File(arg1);
        if (file.exists()) {
          try {
            String str =
                com.google.common.io.Files.asCharSource(file, Charset.defaultCharset()).read();
            return F.stringx(str);
          } catch (IOException e) {
            LOGGER.log(engine.getLogLevel(), "ReadString exception", e);
          }
          return S.Null;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
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
          LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  private static final class URLFetch extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return IOFunctions.printMessage(ast.topHead(), "string", F.List(), engine);
      }
      String arg1Str = ((IStringX) ast.arg1()).toString();
      if (arg1Str.startsWith("https://") || arg1Str.startsWith("http://")) {
        URL url;
        try {
          url = new URL(arg1Str);
          String str = Resources.toString(url, StandardCharsets.UTF_8);
          return F.$s(str);
        } catch (IOException e) {
          LOGGER.debug("URLFetch.evaluate() failed", e);
          // Cannot open `1`.
          return IOFunctions.printMessage(ast.topHead(), "noopen", F.List(ast.arg1()), engine);
        }
      }
      // Cannot open `1`.
      return IOFunctions.printMessage(ast.topHead(), "noopen", F.List(ast.arg1()), engine);
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
            writer = out.getWriter();
            ;
          }
          if (arg1 instanceof OutputStreamExpr) {
            writer = ((OutputStreamExpr) arg1).getWriter();
            ;
          }
          if (writer != null) {
            String arg2String = StringFunctions.inputForm(arg2);
            writer.write(arg2String);
            writer.flush();
            return S.Null;
          }
        } catch (IOException | RuntimeException ex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
        }
      }
      return F.NIL;
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
          return IOFunctions.printMessage(ast.topHead(), "string", F.List(F.C1, ast), engine);
        }
        if (!(ast.arg2().isString())) {
          // String expected at position `1` in `2`.
          return IOFunctions.printMessage(ast.topHead(), "string", F.List(F.C2, ast), engine);
        }
        IStringX fileName = (IStringX) ast.arg1();
        IStringX str = (IStringX) ast.arg2();
        try (FileWriter writer = new FileWriter(fileName.toString())) {
          writer.write(str.toString());
        } catch (IOException e) {
          LOGGER.log(engine.getLogLevel(), "{}: file {} I/O exception", ast.topHead(), fileName, e);
          return F.NIL;
        }
        return S.Null;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  public static IExpr evaluatePackage(final List<ASTNode> node, final EvalEngine engine) {
    AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
    String compoundExpression =
        engine.isRelaxedSyntax() ? "compoundexpression" : "CompoundExpression";
    return evaluatePackageRecursive(node, 0, compoundExpression, ast2Expr, engine);
  }

  private static IExpr evaluatePackageRecursive(
      final List<ASTNode> node,
      int i,
      String compoundExpression,
      AST2Expr ast2Expr,
      final EvalEngine engine) {
    IExpr temp;
    IExpr result = S.Null;
    while (i < node.size()) {
      ASTNode astNode = node.get(i);
      if (astNode instanceof FunctionNode
          && //
          ((FunctionNode) astNode).get(0).getString().equals(compoundExpression)) {
        result =
            evaluatePackageRecursive(
                ((FunctionNode) astNode), 1, compoundExpression, ast2Expr, engine);
      } else {
        temp = ast2Expr.convert(astNode);
        result = engine.evaluate(temp);
      }
      i++;
    }
    return result;
  }

  /**
   * Parse the <code>reader</code> input.
   *
   * <p>This method ignores the first line of the script if it starts with the <code>#!</code>
   * characters (i.e. Unix Script Executables)
   *
   * <p><b>Note</b>: uses the <code>ASTNode</code> parser and not the <code>ExprParser</code>,
   * because otherwise the symbols couldn't be assigned to the contexts.
   *
   * @param reader
   * @param engine
   * @return
   */
  public static List<ASTNode> parseReader(final String reader, final EvalEngine engine) {
    final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
    final List<ASTNode> node = parser.parsePackage(reader);
    return node;
  }

  /**
   * Parse the <code>reader</code> input.
   *
   * <p>This method ignores the first line of the script if it starts with the <code>#!</code>
   * characters (i.e. Unix Script Executables)
   *
   * <p><b>Note</b>: uses the <code>ASTNode</code> parser and not the <code>ExprParser</code>,
   * because otherwise the symbols couldn't be assigned to the contexts.
   *
   * @param reader
   * @param engine
   * @return
   * @throws IOException
   */
  public static List<ASTNode> parseReader(final BufferedReader reader, final EvalEngine engine)
      throws IOException {
    String record;
    StringBuilder builder = new StringBuilder(2048);
    if ((record = reader.readLine()) != null) {
      // ignore the first line of the script if it starts with the #!
      // characters (i.e. Unix Script Executables)
      if (!record.startsWith("!#")) {
        builder.append(record);
        builder.append('\n');
      }
    }
    while ((record = reader.readLine()) != null) {
      builder.append(record);
      builder.append('\n');
    }

    final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
    final List<ASTNode> node = parser.parsePackage(builder.toString());
    return node;
  }

  public static void initialize() {
    Initializer.init();
  }

  private FileFunctions() {}
}
