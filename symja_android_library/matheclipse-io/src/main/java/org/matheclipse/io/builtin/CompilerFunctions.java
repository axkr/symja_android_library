package org.matheclipse.io.builtin;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.Blank;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.CompiledFunctionExpr;
import org.matheclipse.core.form.output.DoubleFormFactory;
import org.matheclipse.core.form.output.JavaDoubleFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public class CompilerFunctions {

  private static final String JAVA_SOURCE_CODE = //
      "/* an in-memory compiled function */                                      \n"
          + "package org.matheclipse.core.compile;                                      \n"
          + "                                                                           \n"
          + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"
          + "import org.matheclipse.core.interfaces.IExpr;                              \n"
          + "import org.matheclipse.core.interfaces.IAST;                               \n"
          + "import org.matheclipse.core.eval.EvalEngine;                               \n"
          + "import org.matheclipse.core.expression.F;                                  \n"
          + "import static org.matheclipse.core.expression.F.*;                         \n"
          + "                                                                           \n"
          + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
          + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
          + "        if (ast.argSize()!={$size}) { return print(ast,{$size},engine); }     \n"
          + "        {$variables}                                                       \n"
          + "        return {$expression}\n"
          + "    }                                                                      \n"
          + "}                                                                          \n";

  private static class VariableManager implements Function<IExpr, String> {
    Map<IExpr, String> varMap;

    public VariableManager(Map<IExpr, String> map) {
      this.varMap = map;
    }

    @Override
    public String apply(IExpr expr) {
      return varMap.get(expr);
    }
  }

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Compile.setEvaluator(new Compile());
      S.CompilePrint.setEvaluator(new CompilePrint());
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  static class MemoryClassLoader extends URLClassLoader {

    // class name to class bytes:
    Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

    public MemoryClassLoader(Map<String, byte[]> classBytes) {
      super(new URL[0], MemoryClassLoader.class.getClassLoader());
      this.classBytes.putAll(classBytes);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      byte[] buf = classBytes.get(name);
      if (buf == null) {
        return super.findClass(name);
      }
      classBytes.remove(name);
      return defineClass(name, buf, 0, buf.length);
    }
  }

  private static class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    // compiled classes in bytes:
    final Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

    MemoryJavaFileManager(JavaFileManager fileManager) {
      super(fileManager);
    }

    public Map<String, byte[]> getClassBytes() {
      return new HashMap<String, byte[]>(this.classBytes);
    }

    @Override
    public void flush() throws IOException {}

    @Override
    public void close() throws IOException {
      classBytes.clear();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
        JavaFileManager.Location location, String className, Kind kind, FileObject sibling)
        throws IOException {
      if (kind == Kind.CLASS) {
        return new MemoryOutputJavaFileObject(className);
      } else {
        return super.getJavaFileForOutput(location, className, kind, sibling);
      }
    }

    JavaFileObject makeStringSource(String name, String code) {
      return new MemoryInputJavaFileObject(name, code);
    }

    class MemoryInputJavaFileObject extends SimpleJavaFileObject {

      final String code;

      MemoryInputJavaFileObject(String name, String code) {
        super(URI.create("string:///" + name), Kind.SOURCE);
        this.code = code;
      }

      @Override
      public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
        return CharBuffer.wrap(code);
      }
    }

    private class MemoryOutputJavaFileObject extends SimpleJavaFileObject {
      final String name;

      MemoryOutputJavaFileObject(String name) {
        super(URI.create("string:///" + name), Kind.CLASS);
        this.name = name;
      }

      @Override
      public OutputStream openOutputStream() {
        return new FilterOutputStream(new ByteArrayOutputStream()) {
          @Override
          public void close() throws IOException {
            out.close();
            ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
            classBytes.put(name, bos.toByteArray());
          }
        };
      }
    }
  }

  private static class CompileFactory {
    public static final HashMap<ISymbol, IConverter> CONVERTERS =
        new HashMap<ISymbol, IConverter>(199);

    static {
      CONVERTERS.put(S.CompoundExpression, new CompoundExpressionConverter());
      CONVERTERS.put(S.Do, new DoConverter());
      CONVERTERS.put(S.Module, new ModuleConverter());
    }

    //    final Set<IExpr> variables;
    Function<IExpr, String> numericVariables;
    Function<IExpr, String> variables;
    final IAST types;

    public CompileFactory(
        Function<IExpr, String> numericVariables, Function<IExpr, String> variables, IAST types) {
      this.numericVariables = numericVariables;
      this.variables = variables;
      this.types = types;
    }

    private interface IConverter {
      public boolean convert(StringBuilder buffer, IAST function);

      public void setFactory(final CompileFactory factory);
    }

    private abstract static class AbstractConverter implements IConverter {
      protected CompileFactory fFactory;

      public AbstractConverter() {}

      /** @param factory */
      @Override
      public void setFactory(final CompileFactory factory) {
        fFactory = factory;
      }
    }

    private static class CompoundExpressionConverter extends AbstractConverter {
      @Override
      public boolean convert(final StringBuilder buf, final IAST f) {

        buf.append("F.CompoundExpression(\n");
        for (int i = 1; i < f.size(); i++) {
          fFactory.convert(buf, f.get(i));
          if (i < f.size() - 1) {
            buf.append(",\n");
          } else {
            buf.append("\n");
          }
        }
        buf.append(")\n");
        return true;
      }
    }

    private static class DoConverter extends AbstractConverter {
      @Override
      public boolean convert(final StringBuilder buf, final IAST f) {
        if (f.size() != 3) {
          return false;
        }
        buf.append("F.Do(\n");
        fFactory.convert(buf, f.arg1());
        buf.append(",\n");
        fFactory.convert(buf, f.arg2());
        buf.append("\n");
        buf.append(")\n");
        return true;
      }
    }

    private static class ModuleConverter extends AbstractConverter {
      @Override
      public boolean convert(final StringBuilder buf, final IAST f) {
        if (f.size() != 3) {
          return false;
        }
        buf.append("F.Module(\n");
        fFactory.convert(buf, f.arg1());
        buf.append(",\n");
        fFactory.convert(buf, f.arg2());
        buf.append("\n");
        buf.append(")\n");
        return true;
      }
    }

    public void convert(StringBuilder buf, IExpr expression) {
      if (expression.isNumericFunction(numericVariables)) {
        try {
          DoubleFormFactory factory = JavaDoubleFormFactory.get(true, false);
          buf.append("F.num(");
          expression =
              expression.replaceAll(
                  x -> {
                    String str = numericVariables.apply(x);
                    if (x.isSymbol() && str != null) {
                      return F.Dummy(str); // x.toString() + "D");
                    }
                    return F.NIL;
                  });
          factory.convert(buf, expression);
          buf.append(")");
          return;
        } catch (RuntimeException rex) {
          //
        }
      }

      if (expression.isAST()) {
        IAST ast = (IAST) expression;
        IExpr h = ast.head();
        if (h.isSymbol()) {
          IConverter converter = CONVERTERS.get(h);
          if (converter != null) {
            converter.setFactory(this);
            StringBuilder sb = new StringBuilder();
            if (converter.convert(sb, ast)) {
              buf.append(sb);
              return;
            }
          }
        }
      }
      buf.append(expression.internalJavaString(variables));
    }
  }

  /** Compile an Symja expression to a java function */
  private static class Compile extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ToggleFeature.COMPILE) {
        return F.NIL;
      }
      try {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
          // For compiling functions, Symja needs to be executed on a Java Development Kit with
          // javax.tools.JavaCompiler installed.
          return IOFunctions.printMessage(S.Compile, "needsjdk", F.CEmptyList, engine);
        }
        if (ast.isAST3()) {
          // TODO implement for 3 args
          return F.NIL;
        }
        IAST[] vars = checkIsVariableOrVariableList(ast, 1, engine);
        if (vars == null) {
          return F.NIL;
        }
        IAST variables = vars[0];
        IAST types = vars[1];

        String source = compilePrint(ast, variables, types, engine);
        if (source != null) {
          Map<String, byte[]> results =
              CompilerFunctions.compile(compiler, "CompiledFunction.java", source);

          Class<?> clazz =
              CompilerFunctions.loadClass("org.matheclipse.core.compile.CompiledFunction", results);

          AbstractFunctionEvaluator fun =
              (AbstractFunctionEvaluator) clazz.getDeclaredConstructor().newInstance();
          return CompiledFunctionExpr.newInstance(variables, types, ast.arg2(), fun);
        }
        return F.NIL;
      } catch (ValidateException ve) {
        return engine.printMessage(ast.topHead(), ve);
      } catch (IOException iox) {
        iox.printStackTrace();
        return engine.printMessage("Compile: " + iox.getMessage());
      } catch (ClassNotFoundException cnfx) {
        cnfx.printStackTrace();
        return engine.printMessage("Compile: " + cnfx.getMessage());
      } catch (NoSuchMethodException
          | InvocationTargetException
          | InstantiationException
          | IllegalAccessException iax) {
        iax.printStackTrace();
        return engine.printMessage("Compile: " + iax.getMessage());
      } catch (RuntimeException rex) {
        rex.printStackTrace();
        return engine.printMessage("Compile: " + rex.getMessage());
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_2_3;
    }
  }

  private static class CompilePrint extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ToggleFeature.COMPILE) {
        return F.NIL;
      }
      try {
        if (ast.isAST3()) {
          // TODO implement for 3 args
          return F.NIL;
        }
        IAST[] vars = checkIsVariableOrVariableList(ast, 1, engine);
        if (vars == null) {
          return F.NIL;
        }
        IAST variables = vars[0];
        IAST types = vars[1];

        String source = compilePrint(ast, variables, types, engine);
        if (source != null) {
          return F.stringx(source, IStringX.APPLICATION_JAVA);
        }
        return F.NIL;
      } catch (ValidateException ve) {
        return engine.printMessage(ast.topHead(), ve);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_2_3;
    }
  }

  private static String compilePrint(
      final IAST ast, IAST variables, IAST types, EvalEngine engine) {
    Map<IExpr, String> symbolicVariables = new HashMap<IExpr, String>();
    Map<IExpr, String> numericVariables = new HashMap<IExpr, String>();

    StringBuilder variablesBuf = new StringBuilder();
    for (int i = 1; i < variables.size(); i++) {
      IExpr argType = types.get(i);
      IExpr variable = variables.get(i);
      if (numericVariables.get(variable) != null) {
        // Duplicate parameter `1` found in `2`.
        IOFunctions.printMessage(ast.topHead(), "fdup", F.List(variable, ast.arg1()), engine);
        return null;
      }

      if (argType.equals(S.Real)) {
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");
        variablesBuf.append("double " + variable + "d = engine.evalDouble(" + variable + ");\n");
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, variable + "d");
      } else if (argType.equals(S.Integer)) {
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");
        variablesBuf.append("int " + variable + "i = engine.evalInt(" + variable + ");\n");
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, variable + "i");
      } else if (argType.equals(S.Complex)) {
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");
        variablesBuf.append("Complex " + variable + "c = engine.evalComplex(" + variable + ");\n");
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, variable + "c");
      } else if (argType.equals(S.Booleans)) {
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");
        variablesBuf.append("boolean " + variable + "b = engine.evalBoolean(" + variable + ");\n");
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, variable + "b");
      }
    }
    IExpr expression = ast.arg2();

    StringBuilder buf = new StringBuilder();
    VariableManager numericVars = new VariableManager(numericVariables);
    VariableManager symbolicVars = new VariableManager(symbolicVariables);
    CompileFactory cf = new CompileFactory(numericVars, symbolicVars, types);
    buf.append("\n");
    cf.convert(buf, expression);
    buf.append(";\n");
    String source = JAVA_SOURCE_CODE.replace("{$variables}", variablesBuf.toString());
    source = source.replace("{$expression}", buf.toString());
    source = source.replace("{$size}", Integer.toString(variables.argSize()));
    //    System.out.println(source);
    return source;
  }

  private static IAST[] checkIsVariableOrVariableList(IAST ast, int position, EvalEngine engine) {
    IAST[] result = new IAST[2];

    if (ast.get(position).isList()) {
      IAST list = (IAST) ast.get(position);

      IASTMutable vars = list.copy();
      IASTAppendable types = F.ListAlloc(list.size());

      for (int i = 1; i < list.size(); i++) {
        if (!checkVariable(ast, engine, list.get(i), vars, types, i)) {
          return null;
        }
      }

      result[0] = vars;
      result[1] = types;
      return result;
    } else {
      IExpr arg = ast.arg1();
      IASTMutable vars = F.unaryAST1(S.List, arg);
      IASTAppendable types = F.ListAlloc(2);

      if (!checkVariable(ast, engine, arg, vars, types, 1)) {
        // `1` is not a valid variable.
        IOFunctions.printMessage(ast.topHead(), "ivar", F.List(arg), engine);
        return null;
      }

      result[0] = vars;
      result[1] = types;
      return result;
    }
  }

  private static boolean checkVariable(
      IAST ast, EvalEngine engine, IExpr arg, IASTMutable vars, IASTAppendable types, int i) {
    IExpr sym = arg;
    IExpr headTest = S.Real;
    if (arg.isList1() || arg.isList2()) {
      sym = arg.first();
      if (arg.isList2()) {
        headTest = null;
        if (arg.second().isBlank()) {
          Blank blank = (Blank) arg.second();
          headTest = blank.getHeadTest();
          if (headTest == null) {
            // `1` is not a valid variable.
            IOFunctions.printMessage(ast.topHead(), "ivar", F.List(sym), engine);
            return false;
          }
          if (headTest.equals(S.Integer) || headTest.equals(S.Complex) || headTest.equals(S.Real)) {
            // allowed machine-sized types
          } else {
            headTest = null;
          }
        }
        if (headTest == null) {
          // `1` is not a valid variable.
          IOFunctions.printMessage(ast.topHead(), "ivar", F.List(sym), engine);
          return false;
        }
      }
    }
    //    if (sym.isSymbol() && sym.isVariable()) {
    vars.set(i, sym);
    types.append(headTest);
    return true;
    //    }
    //    // `1` is not a valid variable.
    //    IOFunctions.printMessage(ast.topHead(), "ivar", F.List(sym), engine);
    //    return false;
  }

  private CompilerFunctions() {}

  /**
   * Compile a Java source file in memory.
   *
   * @param fileName Java file name, e.g. "Test.java"
   * @param source The source code as String.
   * @return The compiled results as Map that contains class name as key, class binary as value.
   * @throws IOException If compile error.
   */
  public static Map<String, byte[]> compile(JavaCompiler compiler, String fileName, String source)
      throws IOException {
    StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
    try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
      JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
      CompilationTask task =
          compiler.getTask(null, manager, null, null, null, Arrays.asList(javaFileObject));
      Boolean result = task.call();
      if (result == null || !result.booleanValue()) {
        throw new RuntimeException("Compilation failed.");
      }
      return manager.getClassBytes();
    }
  }

  /**
   * Load class from compiled classes.
   *
   * @param name Full class name.
   * @param classBytes Compiled results as a Map.
   * @return The Class instance.
   * @throws ClassNotFoundException If class not found.
   * @throws IOException If load error.
   */
  public static Class<?> loadClass(String name, Map<String, byte[]> classBytes)
      throws ClassNotFoundException, IOException {
    try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
      return classLoader.loadClass(name);
    }
  }
}
