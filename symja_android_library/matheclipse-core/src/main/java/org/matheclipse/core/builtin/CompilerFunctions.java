package org.matheclipse.core.builtin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.OutputFunctions.VariableManager;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.Blank;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.CompiledFunctionExpr;
import org.matheclipse.core.form.output.DoubleFormFactory;
import org.matheclipse.core.form.output.JavaComplexFormFactory;
import org.matheclipse.core.form.output.JavaDoubleFormFactory;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public class CompilerFunctions {

  /** Template for CompilePrint */
  public static final String JAVA_SOURCE_CODE = //
      "/** Compile with <a href=\"https://github.com/janino-compiler/janino\">Janino compiler</a> */\n"
          + "package org.matheclipse.core.compile;                                      \n"
          + "                                                                           \n"
          + "import java.util.ArrayList;                                                \n"
          + "import org.hipparchus.complex.Complex;                                     \n"
          + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"
          + "import org.matheclipse.core.interfaces.*;                                  \n"
          + "import org.matheclipse.core.eval.EvalEngine;                               \n"
          + "import org.matheclipse.core.expression.ExprTrie;                           \n"
          + "import org.matheclipse.core.expression.S;                                  \n"
          + "import static org.matheclipse.core.expression.S.*;                         \n"
          + "import org.matheclipse.core.expression.F;                                  \n"
          + "import static org.matheclipse.core.expression.F.*;                         \n"
          + "                                                                           \n"
          + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
          + "  EvalEngine engine;\n" + "  IASTAppendable stack;\n" + "  ExprTrie vars;\n"
          + "  int top=1;\n"
          + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
          + "        if (ast.argSize()!={$size}) { return print(ast,{$size},engine); }  \n"
          + "        this.engine = engine;\n"
          + "        {$variables}                                                       \n"
          + "        return {$expression}\n"
          + "    }                                                                      \n"
          + "  public double evalDouble(IExpr expr)  { \n"
          + "    return engine.evalDouble(expr); \n" + "  }\n" + "\n"
          + "  public Complex evalComplex(IExpr expr)  { \n"
          + "    return engine.evalComplex(expr); \n" + "  }\n" + "\n" + "{$methods}\n"
          + "}                                                                          \n";

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.Compile.setEvaluator(new Compile());
        S.CompiledFunction.setEvaluator(new CompiledFunction());
      }
      S.CompilePrint.setEvaluator(new CompilePrint());
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private static class JavaIndenter {
    private static class JavaSourceLine {
      private String java = "";
      private int lenJava = 0;
      private int block = 0;

      /**
       * Constructor
       * 
       * @param line of a java program
       */
      public JavaSourceLine(String line, int lenJava) {
        line = line.trim();
        int PosOfComment = line.length();
        this.lenJava = lenJava;

        for (int i = 0; i < line.length() && PosOfComment == line.length(); i++) {
          switch (line.charAt(i)) {
            case '"':
              for (i++; i < line.length(); i++) {
                if (line.charAt(i) == '"' && line.charAt(i - 1) != '\\') {
                  break;
                }
              }
              break;
            case '{':
              block++;
              break;
            case '}':
              block--;
              break;
            default:
              break;
          }
        }
        if (block == -1) {
          this.lenJava--;
        }
        java = line.substring(0, PosOfComment);
      }

      public int getIndentation() {
        return this.lenJava;
      }

      public boolean startOfBlock() {
        return block > 0 && (block & 1) == 1;
      }

      /**
       * Return as an 'indented' line
       * 
       * <PRE>
       * JavaLine j = new JavaLine("int a;  
       * String res = j.returnLineWithCommentAt(10);
       * Would set res to be the following string:
       * int a;
       * </PRE>
       * 
       * @return a new version of the line
       */
      public String returnIndentedLine() {
        int number = 2 * this.lenJava;
        StringBuilder buf = new StringBuilder(number + java.length());
        for (int i = 0; i < number; i++) {
          buf.append(" ");
        }
        buf.append(java);
        return buf.toString();
      }

    }

    // The Java source lines
    private ArrayList<JavaSourceLine> programLines = new ArrayList<>();

    public JavaIndenter() {
      programLines.clear();
    }

    /**
     * Add the next line of Java source code
     * 
     * @param sourceLine A line of Java code
     */
    public void addSourceLine(String sourceLine) {
      if (programLines.size() == 0) {
        programLines.add(new JavaSourceLine(sourceLine, 0));
        return;
      }
      JavaSourceLine previous = programLines.get(programLines.size() - 1);
      int indentation = previous.getIndentation();

      if (previous.startOfBlock()) {
        indentation++;
      }
      programLines.add(new JavaSourceLine(sourceLine, indentation));
    }

    /**
     * Return the indented Java program
     * 
     * @return The Java program as a string
     */
    public String indentProgram() {
      StringBuilder buf = new StringBuilder();
      for (JavaSourceLine line : programLines) {
        buf.append(line.returnIndentedLine());
        buf.append("\n");
      }
      return buf.toString();
    }

  }

  /**
   * Contains the name, type and rank of an argument in a compiled function.
   *
   */
  public static class CompiledFunctionArg {

    private enum Rank {
      SCALAR, VECTOR, MATRIX
    }

    IExpr argument;
    IExpr type;
    Rank rank;

    public CompiledFunctionArg(IExpr argument, IExpr type) {
      this.argument = argument;
      this.type = type;
      this.rank = Rank.SCALAR;
    }

    public CompiledFunctionArg(IExpr argument, IExpr type, Rank rank) {
      this.argument = argument;
      this.type = type;
      this.rank = rank;
    }

    /**
     * 
     * @param rank
     * @return <code>null</code> if rank is undefined
     */
    public static Rank getRank(int rank) {
      if (rank == 0) {
        return Rank.SCALAR;
      } else if (rank == 1) {
        return Rank.VECTOR;
      } else if (rank == 2) {
        return Rank.MATRIX;
      }
      return null;
    }

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

  /** Compile a Symja expression to a java function */
  private static class Compile extends AbstractCoreFunctionEvaluator {

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
        CompiledFunctionArg[] args = checkIsVariableOrVariableList(ast, engine);
        if (args == null) {
          return F.NIL;
        }
        CompiledFunctionExpr compiled = compile(ast, args, engine);
        if (compiled != null) {
          return compiled;
        }
      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.JVM_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_2_3;
    }
  }

  private static class CompileFactory {
    int module = 1;

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
      public boolean convert(final StringBuilder parentBuffer, final StringBuilder methods,
          final IAST f) {
        if (f.size() < 2) {
          return false;
        }

        fFactory.variables.push();
        fFactory.numericVariables.push();
        try {
          int m = fFactory.module++;
          methods.append("public IExpr compoundExpression" + m + "() {\n");
          tryBegin(methods);
          StringBuilder expressions;
          StringBuilder subMethods = new StringBuilder();
          for (int i = 1; i < f.argSize(); i++) {
            expressions = new StringBuilder();
            fFactory.convert(expressions, subMethods, f.get(i), false, true);
            methods.append(expressions.toString() + ";\n");
          }
          expressions = new StringBuilder();
          fFactory.convert(expressions, subMethods, f.last(), false, true);
          methods.append("return " + expressions.toString() + ";\n");
          tryEnd(methods);
          methods.append("}\n\n");
          methods.append(subMethods);

          parentBuffer.append("compoundExpression" + m + "()");
        } finally {
          fFactory.variables.pop();
          fFactory.numericVariables.pop();
        }
        return true;
      }
    }

    private static class DoConverter extends AbstractConverter {
      @Override
      public boolean convert(final StringBuilder buf, StringBuilder methods, final IAST f) {
        // if (f.size() < 3) {
        // return false;
        // }
        //
        // final java.util.List<IIterator<IExpr>> iterList = new
        // ArrayList<IIterator<IExpr>>();
        // final EvalEngine engine = EvalEngine.get();
        // f.forEach(
        // 2,
        // f.size(),
        // (x, i) -> {
        // iterList.add(Iterator.create((IAST) x, i, engine));
        // });
        // final Programming.DoIterator generator = new Programming.DoIterator(iterList,
        // engine);
        // return generator.doIt(f.arg1());

        // buf.append("F.Do(\n");
        // fFactory.convert(buf, methods, f.arg1());
        // buf.append(",\n");
        // fFactory.convert(buf, methods, f.arg2());
        // buf.append("\n");
        // buf.append(")\n");
        return true;
      }
    }

    private static class SetConverter extends AbstractConverter {
      @Override
      public boolean convert(final StringBuilder parentBuffer, StringBuilder methods,
          final IAST f) {
        if (f.size() != 3 || !f.arg1().isVariable()) {
          return false;
        }

        String variable = f.arg1().toString();
        if (f.arg2().isNumericFunction(fFactory.numericVariables)) {

          StringBuilder numericBuffer = new StringBuilder();
          int type = fFactory.convertNumeric(numericBuffer, f.arg2(), fFactory.defaultNumericType);
          if (type > 0) {
            if (type == 1) {
              parentBuffer.append("INum " + variable + " = ");
            } else {
              parentBuffer.append("IComplexNum " + variable + " = ");
            }
            parentBuffer.append(numericBuffer + ";\n");
            parentBuffer.append("stack.set(top++, " + variable + ")");
            fFactory.numericVariables.put(f.arg1(), "stack.get(" + (fFactory.topOfStack++) + ")");
            return true;
          }
        }

        parentBuffer.append("IExpr " + variable + " = ");
        fFactory.convert(parentBuffer, methods, f.arg2(), true, true);
        // parentBuffer.append(")");
        return true;
      }
    }

    private interface IConverter {
      public boolean convert(StringBuilder parentBuffer, StringBuilder methods, IAST function);

      public void setFactory(final CompileFactory factory);
    }

    private static class IfConverter extends AbstractConverter {
      @Override
      public boolean convert(final StringBuilder parentBuffer, StringBuilder methods,
          final IAST f) {
        if (f.size() < 3 || f.size() > 4) {
          return false;
        }

        fFactory.variables.push();
        fFactory.numericVariables.push();
        try {
          int m = fFactory.module++;
          methods.append("public IExpr ifExpression" + m + "() {\n");
          StringBuilder expression = new StringBuilder();
          StringBuilder subMethods = new StringBuilder();
          fFactory.convert(expression, subMethods, f.arg1(), false, true);
          methods.append("if(engine.evalTrue(" + expression.toString() + ")){\n");
          expression = new StringBuilder();
          fFactory.convert(expression, subMethods, f.arg2(), false, true);
          methods.append("return ");
          methods.append(expression);
          methods.append(";\n");
          if (f.size() == 4) {
            methods.append("} else {\n");
            expression = new StringBuilder();
            fFactory.convert(expression, subMethods, f.arg3(), false, true);
            methods.append("return ");
            methods.append(expression);
            methods.append(";\n");
          }
          methods.append("}\n");
          methods.append("}\n\n");
          methods.append(subMethods);
          parentBuffer.append("ifExpression" + m + "()");
        } finally {
          fFactory.variables.pop();
          fFactory.numericVariables.pop();
        }
        return true;
      }
    }

    private static class ModuleConverter extends AbstractConverter {
      @Override
      public boolean convert(final StringBuilder parentBuffer, StringBuilder methods,
          final IAST f) {
        if (f.size() != 3 && f.arg1().isList()) {
          return false;
        }
        fFactory.variables.push();
        fFactory.numericVariables.push();
        HashSet<String> oldLocalVariables = fFactory.localVariables;
        try {
          HashSet<String> localVariables = new HashSet<>(fFactory.localVariables);
          fFactory.localVariables = localVariables;
          IAST variableList = (IAST) f.arg1();
          int m = fFactory.module++;
          methods.append("public IExpr moduleExpression" + m + "() {\n");
          methods.append("ExprTrie oldVars = vars;");
          tryBegin(methods);
          methods.append("vars = vars.copy();\n");
          StringBuilder expressions;
          for (int i = 1; i < variableList.size(); i++) {
            IExpr arg = variableList.get(i);
            if (arg.isSymbol()) {

              methods.append(
                  "ISymbol " + arg.toString() + " = F.Dummy(\"" + arg.toString() + "\");\n");
              localVariables.add(arg.toString());
              methods.append("vars.put(\"" + arg.toString() + "\"," + arg.toString() + ");\n");
              continue;
            }

            if (!arg.isAST(S.Set, 3) && !arg.first().isSymbol()) {
              return false;
            }
            String symbolName = arg.first().toString();
            localVariables.add(symbolName.toString());
            methods.append("ISymbol " + symbolName + " = F.Dummy(\"" + symbolName + "\");\n");
            localVariables.add(arg.toString());
            methods.append("vars.put(\"" + symbolName + "\"," + symbolName + ");\n");
            expressions = new StringBuilder();
            fFactory.convert(expressions, methods, arg.second(), true, false);
            methods.append("F.eval(F.Set(" + symbolName + "," + expressions.toString() + "));\n");
          }

          expressions = new StringBuilder();
          StringBuilder subMethods = new StringBuilder();
          fFactory.convert(expressions, subMethods, f.arg2(), false, true);
          methods.append("return " + expressions.toString() + ";\n");
          // tryEnd
          methods.append("} finally {top = oldTop; vars = oldVars;}\n");

          methods.append("}\n\n");
          methods.append(subMethods);

          parentBuffer.append("moduleExpression" + m + "()");
        } finally {
          fFactory.localVariables = oldLocalVariables;
          fFactory.variables.pop();
          fFactory.numericVariables.pop();
        }
        // parentBuffer.append("F.Module(\n");
        // fFactory.convert(parentBuffer, methods, f.arg1());
        // parentBuffer.append(",\n");
        // fFactory.convert(parentBuffer, methods, f.arg2());
        // parentBuffer.append("\n");
        // parentBuffer.append(")\n");
        return true;
      }
    }

    public static final Map<ISymbol, IConverter> CONVERTERS = new HashMap<ISymbol, IConverter>(199);

    static {
      CONVERTERS.put(S.CompoundExpression, new CompoundExpressionConverter());
      // CONVERTERS.put(S.Do, new DoConverter());
      CONVERTERS.put(S.If, new IfConverter());
      CONVERTERS.put(S.Set, new SetConverter());
      CONVERTERS.put(S.Module, new ModuleConverter());
    }

    int defaultNumericType;
    HashSet<String> localVariables;
    VariableManager numericVariables;
    VariableManager variables;
    int topOfStack;
    final CompiledFunctionArg[] args;

    public CompileFactory(VariableManager numericVariables, VariableManager variables,
        CompiledFunctionArg[] args, int topOfStack, int defaultNumericType) {
      this.localVariables = new HashSet<String>();
      this.numericVariables = numericVariables;
      this.variables = variables;
      this.args = args;
      this.topOfStack = topOfStack;
      this.defaultNumericType = defaultNumericType;
    }

    /**
     * Write <code>expression</code> into the <code>buf</code> string builder.
     *
     * @param buf
     * @param methods
     * @param expression
     * @param symbolic TODO
     * @param addEval if <code>true</code> wrap the expression with a <code>F.eval( ... )</code>
     *        statement.
     */
    public void convert(StringBuilder buf, StringBuilder methods, IExpr expression,
        boolean symbolic, boolean addEval) {
      if (expression.isNumericFunction(numericVariables)) {
        if (!symbolic) {
          int type = convertNumeric(buf, expression, defaultNumericType);
          if (type > 0) {
            return;
          }
        }
        convertSymbolic(buf, expression);
        return;
      }

      if (expression.isAST()) {
        IAST ast = (IAST) expression;
        IExpr h = ast.head();
        if (h.isSymbol()) {
          IConverter converter = CONVERTERS.get(h);
          if (converter != null) {
            converter.setFactory(this);
            StringBuilder sb = new StringBuilder();
            if (converter.convert(sb, methods, ast)) {
              buf.append(sb);
              return;
            }
          }
        }
      }
      if (addEval) {
        buf.append("F.eval(");
        convertSymbolic(buf, expression);
        buf.append(")");
      } else {
        convertSymbolic(buf, expression);
      }
    }

    /**
     * @param parentBuffer
     * @param expression
     * @param type <code>1</code> create double value; <code>2</code> create Complex value
     * @return <code>0</code> if an exception occured; <code>1</code> evalDouble; <code>2</code>
     *         evalComplex
     */
    private int convertNumeric(StringBuilder parentBuffer, IExpr expression, int type) {
      try {

        if (type == 1) {
          StringBuilder buf = new StringBuilder();
          DoubleFormFactory factory = JavaDoubleFormFactory.get(true, false);
          buf.append("F.num(");
          expression = F.subst(expression, x -> {
            String str = numericVariables.apply(x);
            if (x.isSymbol() && str != null) {
              return F.stringx("evalDouble(" + str + ")"); // x.toString() + "D");
            }
            return F.NIL;
          });
          factory.convert(buf, expression);
          buf.append(")");
          parentBuffer.append(buf);
          return 1;
        }

      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        //
      }
      try {
        StringBuilder buf = new StringBuilder();
        JavaComplexFormFactory factory = JavaComplexFormFactory.get(true, false, -1, -1, true);
        buf.append("F.complexNum(");
        expression = F.subst(expression, x -> {
          String str = numericVariables.apply(x);
          if (x.isSymbol() && str != null) {
            return F.stringx("evalComplex(" + str + ")"); // x.toString() + "D");
          }
          return F.NIL;
        });
        factory.convert(buf, expression);
        buf.append(")");
        parentBuffer.append(buf);
        return 2;

      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
      return 0;
    }

    private boolean convertSymbolic(StringBuilder buf, IExpr expression) {
      try {
        buf.append(
            expression.internalJavaString(SourceCodeProperties.JAVA_FORM_PROPERTIES, -1, x -> {
              if (localVariables.contains(x.toString())) {
                return "vars.get(\"" + x.toString() + "\")";
              }
              return numericVariables.apply(x);
            }));
        return true;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
      return false;
    }

    private static void tryBegin(final StringBuilder methods) {
      methods.append(" int oldTop =  top;\n try {\n");
    }

    private static void tryEnd(final StringBuilder methods) {
      methods.append("} finally {top = oldTop;}\n");
    }

  }

  private static final class CompiledFunction extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr head = ast.head();
      if (head instanceof CompiledFunctionExpr) {

        CompiledFunctionExpr compiledFunction = (CompiledFunctionExpr) head;
        IExpr result = F.NIL;
        try {
          result = compiledFunction.evaluate(ast, engine);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.CompiledFunction, rex, engine);
        }
        if (result.isPresent()) {
          result = engine.evaluate(result);
          if (result.isIndeterminate()) {
            // Numerical error encountered, proceeding with uncompiled evaluation.
            Errors.printMessage(S.CompiledFunction, "cfn", F.CEmptyList, engine);
            IAST variables = compiledFunction.getVariables();
            IExpr expr = compiledFunction.getExpr();
            return expr.replaceAll(Functors.equalRules(variables, ast));
          }
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.JVM_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  public static class CompilePrint extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ToggleFeature.COMPILE_PRINT) {
        return F.NIL;
      }
      if (ast.isAST3()) {
        // TODO implement for 3 args
        return F.NIL;
      }
      CompiledFunctionArg[] args = checkIsVariableOrVariableList(ast, engine);
      if (args == null) {
        return F.NIL;
      }

      String source = compilePrint(ast, args, engine);
      if (source != null) {
        source = indentSource(source);
        return F.stringx(source, IStringX.APPLICATION_JAVA);
      }
      return F.NIL;
    }

    /**
     * Indent the source code blocks with spaces.
     * 
     * @param source
     * @return the indented source code
     */
    private String indentSource(String source) {
      String[] split = source.split("\n");
      JavaIndenter p = new JavaIndenter();
      for (int i = 0; i < split.length; i++) {
        p.addSourceLine(split[i].trim());
      }
      source = p.indentProgram();
      return source;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_2_3;
    }
  }

  private CompilerFunctions() {}

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

  /**
   * Get an array with 3 elements returning the declared variables in the first entry and the
   * corresponding types <code>Real, Integer,...</code> for the variable names in the second entry
   * and the corresponding ranks <code>0, 1, 2,...</code> for the variable names in the third entry
   *
   * @param ast the original definition <code>
   * CompilePrint({variable/types}, function)</code>
   * @param engine the evaluation engine
   * @return <code>null</code> if the variable declaration isn't correct
   */
  private static CompiledFunctionArg[] checkIsVariableOrVariableList(IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isList()) {
      IAST list = (IAST) arg1;
      CompiledFunctionArg[] result = new CompiledFunctionArg[list.argSize()];
      for (int i = 1; i < list.size(); i++) {
        CompiledFunctionArg arg = checkVariable(list.get(i), engine);
        if (arg == null) {
          // `1` is not a valid variable.
          Errors.printMessage(ast.topHead(), "ivar", F.list(list.get(i)), engine);
          return null;
        }
        result[i - 1] = arg;
      }
      return result;
    }

    CompiledFunctionArg arg = checkVariable(arg1, engine);
    if (arg == null) {
      // `1` is not a valid variable.
      Errors.printMessage(ast.topHead(), "ivar", F.list(arg1), engine);
      return null;
    }
    return new CompiledFunctionArg[] {arg};
  }

  /**
   * @param arg the input argument for the current <code>variablesIndex</code>
   * @param engine
   * @return <code>null</code> if the variables and types
   */
  private static CompiledFunctionArg checkVariable(IExpr arg, EvalEngine engine) {
    IExpr sym = arg;
    IExpr headTest = S.Real;
    CompiledFunctionArg.Rank rank = CompiledFunctionArg.Rank.SCALAR;
    if (arg.isList1() || arg.isList2() || arg.isList3()) {
      sym = arg.first();
      if (arg.isList2() || arg.isList3()) {
        headTest = null;
        if (arg.second().isBlank()) {
          Blank blank = (Blank) arg.second();
          headTest = blank.getHeadTest();
          if (headTest == null) {
            return null;
          }
          if (headTest.equals(S.Integer) || headTest.equals(S.Complex) || headTest.equals(S.Real)) {
            // allowed machine-sized types
            if (arg.isList3()) {
              int intRank = arg.getAt(3).toIntDefault();
              if (intRank < 0 || intRank > 2) {
                return null;
              }
              rank = CompiledFunctionArg.getRank(intRank);
            }
          } else {
            headTest = null;
          }
        }
        if (headTest == null) {
          return null;
        }

      }
    }

    return new CompiledFunctionArg(sym, headTest, rank);
  }

  public static CompiledFunctionExpr compile(final IAST ast, CompiledFunctionArg[] args,
      EvalEngine engine) {
    try {
      IASTAppendable variables = F.ListAlloc(args.length);
      IASTAppendable types = F.ListAlloc(args.length);
      for (int i = 0; i < args.length; i++) {
        variables.append(args[i].argument);
        types.append(args[i].type);
      }
      String source = compilePrint(ast, args, engine);
      if (source != null) {
        SimpleCompiler comp = new org.codehaus.janino.SimpleCompiler();
        comp.cook(source);
        ClassLoader loader = comp.getClassLoader();
        Class<?> clazz = loader.loadClass("org.matheclipse.core.compile.CompiledFunction");
        return CompiledFunctionExpr.newInstance(variables, types, ast.arg2(), clazz);
      }
    } catch (CompileException | ClassNotFoundException | RuntimeException e) {
      Errors.printMessage(S.Compile, e, engine);
    }
    return null;
  }

  /**
   * Get the generated Java source code for <code>function</code> from call <code>
   * CompilePrint({variable/types}, function)</code>
   *
   * @param ast the definition <code>
   * CompilePrint({variable/types}, function)</code>
   * @param engine the evaluation engine
   * @return
   */
  public static String compilePrint(final IAST ast, CompiledFunctionArg[] args, EvalEngine engine) {
    Map<IExpr, String> symbolicVariables = new HashMap<IExpr, String>();
    Map<IExpr, String> numericVariables = new HashMap<IExpr, String>();
    StringBuilder variablesBuf = new StringBuilder();
    variablesBuf.append("stack  = F.ast(S.List, 100, true);\n");
    variablesBuf.append("vars = new ExprTrie();\n");
    int top = 1;
    int defaultNumericType = 1;
    for (int j = 0; j < args.length; j++) {
      IExpr argType = args[j].type;
      IExpr variable = args[j].argument;
      CompiledFunctionArg.Rank rank = args[j].rank;
      int i = j + 1;
      if (numericVariables.get(variable) != null) {
        // Duplicate parameter `1` found in `2`.
        Errors.printMessage(ast.topHead(), "fdup", F.list(variable, ast.arg1()), engine);
        return null;
      }
      if (argType.equals(S.Real)) {
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");
        switch (rank) {
          case SCALAR:
            variablesBuf
                .append("double " + variable + "d = engine.evalDouble(" + variable + ");\n");
            break;
          case VECTOR:
            variablesBuf.append(
                "double[] " + variable + "d = engine.evalDoubleVector(" + variable + ");\n");
            break;
          case MATRIX:
            variablesBuf.append(
                "double[][] " + variable + "d = engine.evalDoubleMatrix(" + variable + ");\n");
            break;
        }
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, "stack.get(" + top + ")");
        variablesBuf.append("stack.set(top++, F.num(" + variable + "d));\n");
        top++;
      } else if (argType.equals(S.Integer)) {
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");
        switch (rank) {
          case SCALAR:
            variablesBuf.append("int " + variable + "i = engine.evalInt(" + variable + ");\n");
            break;
          case VECTOR:
            variablesBuf
                .append("int[] " + variable + "i = engine.evalIntVector(" + variable + ");\n");
            break;
          case MATRIX:
            variablesBuf
                .append("int[][] " + variable + "i = engine.evalIntMatrix(" + variable + ");\n");
            break;
        }
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, "stack.get(" + top + ")");
        variablesBuf.append("stack.set(top++, F.ZZ(" + variable + "i));\n");
        top++;
      } else if (argType.equals(S.Complex)) {
        defaultNumericType = 2;
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");
        switch (rank) {
          case SCALAR:
            variablesBuf
                .append("Complex " + variable + "c = engine.evalComplex(" + variable + ");\n");
            break;
          case VECTOR:
            variablesBuf.append(
                "Complex[] " + variable + "c = engine.evalComplexVector(" + variable + ");\n");
            break;
          case MATRIX:
            variablesBuf.append(
                "Complex[][] " + variable + "c = engine.evalComplexMatrix(" + variable + ");\n");
            break;
        }
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, "stack.get(" + top + ")");
        variablesBuf.append("stack.set(top++, F.complexNum(" + variable + "c));\n");
        top++;
      } else if (argType.equals(S.Booleans)) {
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");

        switch (rank) {
          case SCALAR:
            variablesBuf
                .append("boolean " + variable + "b = engine.evalBoolean(" + variable + ");\n");
            break;
          case VECTOR:
            variablesBuf.append(
                "boolean[] " + variable + "b = engine.evalBooleanVector(" + variable + ");\n");
            break;
          case MATRIX:
            variablesBuf.append(
                "boolean[][] " + variable + "b = engine.evalBooleanMatrix(" + variable + ");\n");
            break;
        }
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, "stack.get(" + top + ")");
        variablesBuf.append("stack.set(top++, F.bool(" + variable + "b));\n");
        top++;
      }
    }
    IExpr expression = ast.arg2();
    VariableManager numericVars = new VariableManager(numericVariables);
    VariableManager symbolicVars = new VariableManager(symbolicVariables);
    CompileFactory cf =
        new CompileFactory(numericVars, symbolicVars, args, top, defaultNumericType);

    return generateClassSource(cf, expression, variablesBuf, args.length);
  }

  public static String compilePrintJavaParser(final IAST ast, CompiledFunctionArg[] args,
      EvalEngine engine) {
    if (ToggleFeature.COMPILE_WITH_JAVAPARSER) {
      // com.github.javaparser.ast.CompilationUnit javaCu =
      // new com.github.javaparser.ast.CompilationUnit("org.matheclipse.core.compile");
      // javaCu.addImport(ArrayList.class);
      // javaCu.addImport("org.matheclipse.core.interfaces.*");
      // javaCu.addImport("org.matheclipse.core.expression.F");
      // javaCu.addImport("static org.matheclipse.core.expression.F.*");
      // javaCu.addImport("org.matheclipse.core.expression.S");
      // javaCu.addImport("static org.matheclipse.core.expression.S.*");
      //
      // ClassOrInterfaceDeclaration javaClass = javaCu.addClass("CompiledFunction");
      // javaClass.addExtendedType(AbstractFunctionEvaluator.class);
      // javaClass.addField(EvalEngine.class, "engine", Keyword.PRIVATE);
      // javaClass.addField(IASTAppendable.class, "stack", Keyword.PRIVATE);
      // javaClass.addField(ExprTrie.class, "vars", Keyword.PRIVATE);
      // javaClass.addField(int.class, "top", Keyword.PRIVATE);
      // MethodDeclaration method = javaClass.addMethod("evaluate", Keyword.PUBLIC);
      // method.setType(IAST.class);
      // method.addModifier(Keyword.PUBLIC);
      // method.addParameter(IAST.class, "ast");
      // method.addParameter(EvalEngine.class, "engine");
      //
      // BlockStmt body = method.getBody().get();
      // AssignExpr topInit =
      // new AssignExpr(new NameExpr("this.top"), new NameExpr("1"), Operator.ASSIGN);
      // body.addStatement(topInit);
      //
      // AssignExpr engineInit =
      // new AssignExpr(new NameExpr("this.engine"), new NameExpr("engine"), Operator.ASSIGN);
      // body.addStatement(engineInit);
      //
      // MethodCallExpr methodAST = new MethodCallExpr("F.ast");
      // methodAST.addArgument("S.List");
      // methodAST.addArgument("100");
      // methodAST.addArgument("true");
      // AssignExpr stackInit = new AssignExpr(new NameExpr("this.stack"), methodAST,
      // Operator.ASSIGN);
      // body.addStatement(stackInit);
      //
      // AssignExpr varsInit = new AssignExpr(new NameExpr("this.vars"),
      // new MethodCallExpr("new ExprTrie"), Operator.ASSIGN);
      // body.addStatement(varsInit);
      //
      // MethodCallExpr methodASTget = new MethodCallExpr("ast.get");
      // methodASTget.addArgument("1");
      // body.addStatement(new AssignExpr(new NameExpr("IExpr x"), methodASTget, Operator.ASSIGN));
      //
      // MethodCallExpr methodEngineEvalDouble = new MethodCallExpr("engine.evalDouble");
      // methodEngineEvalDouble.addArgument("x");
      // body.addStatement(
      // new AssignExpr(new NameExpr("double xd"), methodEngineEvalDouble, Operator.ASSIGN));
      // //
      // // stack.set(top++, F.num(xd));
      // MethodCallExpr methodNum = new MethodCallExpr("F.num");
      // methodNum.addArgument("xd");
      // MethodCallExpr methodStackSet = new MethodCallExpr("stack.set");
      // methodStackSet.addArgument("top++");
      // methodStackSet.addArgument(methodNum);
      // body.addStatement(methodStackSet);
      // // ParseResult<Expression> parseExpression = new JavaParser().parseExpression("top = 1");
      //
      // return javaCu.toString(new DefaultPrinterConfiguration());
    }
    return "";
  }

  private static String generateClassSource(CompileFactory cf, IExpr expression,
      StringBuilder variablesBuf, int argsSize) {
    StringBuilder expressionBuf = new StringBuilder();
    StringBuilder methodsBuf = new StringBuilder();
    cf.convert(expressionBuf, methodsBuf, expression, false, true);
    expressionBuf.append(";\n");
    String source = JAVA_SOURCE_CODE.replace("{$variables}", variablesBuf.toString());
    source = source.replace("{$methods}", methodsBuf.toString());
    source = source.replace("{$expression}", expressionBuf.toString());
    source = source.replace("{$size}", Integer.toString(argsSize));
    return source;
  }

}
