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
import org.matheclipse.core.builtin.OutputFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.CompiledFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class CompilerFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Compile.setEvaluator(new Compile());
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
        IAST[] vars = OutputFunctions.checkIsVariableOrVariableList(ast, 1, engine);
        if (vars == null) {
          return F.NIL;
        }
        IAST variables = vars[0];
        IAST types = vars[1];

        String source = OutputFunctions.compilePrint(ast, variables, types, engine);
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
