package org.matheclipse.core.builtin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;

import com.google.common.io.Files;
import com.google.common.io.Resources;

public class FileFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.BeginPackage.setEvaluator(new BeginPackage());
        S.EndPackage.setEvaluator(new EndPackage());
        S.Begin.setEvaluator(new Begin());
        S.End.setEvaluator(new End());
        S.Get.setEvaluator(new Get());
        S.Needs.setEvaluator(new Needs());
        S.Put.setEvaluator(new Put());
        S.ReadString.setEvaluator(new ReadString());
        S.URLFetch.setEvaluator(new URLFetch());
        S.WriteString.setEvaluator(new WriteString());
      }
    }
  }

  private static final class Begin extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        String contextName = Validate.checkContextName(ast, 1);
        org.matheclipse.core.expression.Context pack =
            EvalEngine.get().getContextPath().currentContext();
        // String packageName = pack.getContextName();
        // if (packageName.equals(Context.GLOBAL_CONTEXT_NAME)) {
        // completeContextName = contextName;
        // } else {
        // completeContextName = packageName.substring(0, packageName.length() - 1) + contextName;
        // }
        org.matheclipse.core.expression.Context context = engine.begin(contextName, pack);
        return F.stringx(context.completeContextName());
      } catch (ValidateException ve) {
        return engine.printMessage(ve.getMessage(ast.topHead()));
      }
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
      if (ast.size() > 1) {
        try {
          String contextName = Validate.checkContextName(ast, 1);
          engine.beginPackage(contextName);
          if (Config.isFileSystemEnabled(engine)) {
            try {
              for (int j = 2; j < ast.size(); j++) {
                // FileReader reader = new FileReader(ast.get(j).toString());
                FileInputStream fis = new FileInputStream(ast.get(j).toString());
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
                Get.loadPackage(engine, reader);
                reader.close();
                fis.close();
              }
            } catch (IOException e) {
              if (FEConfig.SHOW_STACKTRACE) {
                e.printStackTrace();
              }
            }
          }
          return F.Null;

        } catch (ValidateException ve) {
          return engine.printMessage(ve.getMessage(ast.topHead()));
        }
      }
      return F.NIL;
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
      return F.Null;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  /** Get[{&lt;file name&gt;}} */
  private static class Get extends AbstractFunctionEvaluator {

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
        e.printStackTrace();
      }
      return F.Null;
    }

    /**
     * Load a package from the given reader
     *
     * @param engine
     * @param is
     * @return the last evaluated expression result
     */
    protected static IExpr loadPackage(final EvalEngine engine, final BufferedReader is) {
      final BufferedReader r = is;
      try {
        final List<ASTNode> node = parseReader(r, engine);

        return evaluatePackage(node, engine);
      } catch (final Exception e) {
        e.printStackTrace();
      } finally {
        try {
          r.close();
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return F.Null;
    }

    private static IExpr getFile(File file, IAST ast, EvalEngine engine) {
      boolean packageMode = engine.isPackageMode();
      try {
        engine.setPackageMode(true);
        String str = Files.asCharSource(file, Charset.defaultCharset()).read();
        return Get.loadPackage(engine, str);
      } catch (IOException e) {
        if (FEConfig.SHOW_STACKTRACE) {
          e.printStackTrace();
        }
        // Cannot open `1`.
        return IOFunctions.printMessage(ast.topHead(), "noopen", F.List(ast.arg1()), engine);
      } finally {
        engine.setPackageMode(packageMode);
      }
    }

    private static IExpr getURL(URL url, IAST ast, EvalEngine engine) {
      boolean packageMode = engine.isPackageMode();
      try {
        engine.setPackageMode(true);
        String str = Resources.toString(url, StandardCharsets.UTF_8);
        return loadPackage(engine, str);
      } catch (IOException e) {
        if (FEConfig.SHOW_STACKTRACE) {
          e.printStackTrace();
        }
        // Cannot open `1`.
        return IOFunctions.printMessage(ast.topHead(), "noopen", F.List(ast.arg1()), engine);
      } finally {
        engine.setPackageMode(packageMode);
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {

        if (!(ast.arg1() instanceof IStringX)) {
          return IOFunctions.printMessage(ast.topHead(), "string", F.List(), engine);
        }
        String arg1Str = ((IStringX) ast.arg1()).toString();
        if (arg1Str.startsWith("https://")
            || //
            arg1Str.startsWith("http://")) {
          URL url;
          try {
            url = new URL(arg1Str);
            return getURL(url, ast, engine);
          } catch (MalformedURLException mue) {
            if (FEConfig.SHOW_STACKTRACE) {
              mue.printStackTrace();
            }
            return engine.printMessage(ast.topHead() + ": " + mue.getMessage());
          }
        }
        File file = new File(arg1Str);
        if (file.exists()) {
          return getFile(file, ast, engine);
        } else {
          file = FileSystems.getDefault().getPath(arg1Str).toAbsolutePath().toFile();
          if (file.exists()) {
            return getFile(file, ast, engine);
          }
        }
        String contextName = Validate.checkContextName(ast, 1);
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

  private static final class Needs extends Get {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      String contextName = Validate.checkContextName(ast, 1);
      if (!ContextPath.PACKAGES.contains(contextName)) {
        return super.evaluate(ast, engine);
      }
      return F.Null;
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
          return IOFunctions.printMessage(F.Put, "string", F.List(), engine);
        }
        IStringX fileName = (IStringX) ast.last();
        FileWriter writer = null;
        try {

          final StringBuilder buf = new StringBuilder();
          for (int i = 1; i < argSize; i++) {
            IExpr temp = engine.evaluate(ast.get(i));
            if (!OutputFormFactory.get().convert(buf, temp)) {
              return engine.printMessage(
                  "Put: file " + fileName.toString() + "ERROR-IN_OUTPUTFORM");
            }
            buf.append('\n');
            if (i < argSize - 1) {
              buf.append('\n');
            }
          }
          writer = new FileWriter(fileName.toString());
          writer.write(buf.toString());
        } catch (IOException e) {
          return engine.printMessage("Put: file " + fileName.toString() + " I/O exception !");
        } finally {
          if (writer != null) {
            try {
              writer.close();
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }
        return F.Null;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
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
        if (arg1.startsWith("https://")
            || //
            arg1.startsWith("http://")) {
          URL url;
          try {
            url = new URL(arg1);
            String str;
            str = Resources.toString(url, StandardCharsets.UTF_8);
            return F.stringx(str);
          } catch (IOException ioe) {
            if (FEConfig.SHOW_STACKTRACE) {
              ioe.printStackTrace();
            }
            return engine.printMessage(ast.topHead() + ": " + ioe.getMessage());
          }
        }
        File file = new File(arg1);
        if (file.exists()) {
          try {
            String str = Files.asCharSource(file, Charset.defaultCharset()).read();
            return F.stringx(str);
          } catch (IOException e) {
            if (FEConfig.SHOW_STACKTRACE) {
              e.printStackTrace();
            }
            engine.printMessage("ReadString exception: " + e.getMessage());
          }
          return F.Null;
        }
      }
      return F.NIL;
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
        return IOFunctions.printMessage(ast.topHead(), "string", F.List(), engine);
      }
      String arg1Str = ((IStringX) ast.arg1()).toString();
      if (arg1Str.startsWith("https://")
          || //
          arg1Str.startsWith("http://")) {
        URL url;
        try {
          url = new URL(arg1Str);
          String str = Resources.toString(url, StandardCharsets.UTF_8);
          return F.$s(str);
        } catch (IOException e) {
          if (FEConfig.SHOW_STACKTRACE) {
            e.printStackTrace();
          }
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

  private static final class WriteString extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {

        final int argSize = ast.argSize();
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
        FileWriter writer = null;
        try {
          writer = new FileWriter(fileName.toString());
          writer.write(str.toString());
        } catch (IOException e) {
          return engine.printMessage(
              ast.topHead() + ": file " + fileName.toString() + " I/O exception !");
        } finally {
          if (writer != null) {
            try {
              writer.close();
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }
        return F.Null;
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
   * @throws IOException
   */
  public static List<ASTNode> parseReader(final String reader, final EvalEngine engine)
      throws IOException {
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
