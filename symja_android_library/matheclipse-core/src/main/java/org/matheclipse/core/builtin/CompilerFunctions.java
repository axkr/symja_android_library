package org.matheclipse.core.builtin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.compile.CompileAnalyzer;
import org.matheclipse.core.compile.CompileFactory;
import org.matheclipse.core.compile.VariableManager;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.Blank;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.CompiledFunctionExpr;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public class CompilerFunctions {

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
          + "import org.matheclipse.core.expression.CMath;                              \n"
          + "import org.matheclipse.core.expression.DMath;                              \n"
          + "import org.matheclipse.core.expression.F;                                  \n"
          + "import static org.matheclipse.core.expression.F.*;                         \n"
          + "                                                                           \n"
          + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
          + "  EvalEngine engine;\n" + "  IASTAppendable stack;\n" + "  ExprTrie vars;\n"
          + "  int top=1;\n"
          + "  {$fields}\n" // Primitive Class Attributes
          + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
          + "        if (ast.argSize()!={$size}) { return print(ast,{$size},engine); }  \n"
          + "        this.engine = engine;\n"
          + "        {$variables}                                                       \n"
          + "        return {$expression}\n"
          + "    }                                                                      \n"
          + "{$methods}\n"
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
      private String java;
      private int lenJava;
      private int block = 0;

      public JavaSourceLine(String line, int lenJava) {
        line = line.trim();
        int PosOfComment = line.length();
        this.lenJava = lenJava;

        for (int i = 0; i < line.length() && PosOfComment == line.length(); i++) {
          switch (line.charAt(i)) {
            case '"':
              for (int k = i + 1; k < line.length(); k++) {
                if (line.charAt(k) == '"' && line.charAt(k - 1) != '\\') {
                  i = k + 1;
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

    private ArrayList<JavaSourceLine> programLines = new ArrayList<>();

    public JavaIndenter() {
      programLines.clear();
    }

    public void addSourceLine(String sourceLine) {
      if (programLines.isEmpty()) {
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

    public String indentProgram() {
      StringBuilder buf = new StringBuilder();
      for (JavaSourceLine line : programLines) {
        buf.append(line.returnIndentedLine());
        buf.append("\n");
      }
      return buf.toString();
    }
  }

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
    Map<String, byte[]> classBytes = new HashMap<>();

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

  private static class Compile extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ToggleFeature.COMPILE) {
        return F.NIL;
      }
      try {
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
      return IFunctionEvaluator.ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
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
            Errors.printMessage(S.CompiledFunction, "cfn", F.CEmptyList, engine);
            IAST variables = compiledFunction.getVariables();
            IExpr expr = compiledFunction.getExpr();
            return F.subst(expr, Functors.equalRules(variables, ast));
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

    private String indentSource(String source) {
      String[] split = source.split("\n");
      JavaIndenter p = new JavaIndenter();
      for (int i = 0; i < split.length; i++) {
        p.addSourceLine(split[i].trim());
      }
      return p.indentProgram();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_2_2;
    }
  }

  private CompilerFunctions() {}

  public static Class<?> loadClass(String name, Map<String, byte[]> classBytes)
      throws ClassNotFoundException, IOException {
    try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
      return classLoader.loadClass(name);
    }
  }

  private static CompiledFunctionArg[] checkIsVariableOrVariableList(IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isList()) {
      IAST list = (IAST) arg1;
      CompiledFunctionArg[] result = new CompiledFunctionArg[list.argSize()];
      for (int i = 1; i <= list.argSize(); i++) {
        CompiledFunctionArg arg = checkVariable(list.get(i), engine);
        if (arg == null) {
          Errors.printMessage(ast.topHead(), "ivar", F.list(list.get(i)), engine);
          return null;
        }
        result[i - 1] = arg;
      }
      return result;
    }

    CompiledFunctionArg arg = checkVariable(arg1, engine);
    if (arg == null) {
      Errors.printMessage(ast.topHead(), "ivar", F.list(arg1), engine);
      return null;
    }
    return new CompiledFunctionArg[] {arg};
  }

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
          if (headTest.isBuiltInSymbol()) {
            switch (((IBuiltInSymbol) headTest).ordinal()) {
              case ID.Integer:
              case ID.Complex:
              case ID.Real:
                if (arg.isList3()) {
                  int intRank = ((IAST) arg).get(3).toIntDefault();
                  if (intRank < 0 || intRank > 2) {
                    return null;
                  }
                  rank = CompiledFunctionArg.getRank(intRank);
                }
                break;
              default:
                headTest = null;
                break;
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
      for (CompiledFunctionArg arg : args) {
        variables.append(arg.argument);
        types.append(arg.type);
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

  public static String compilePrint(final IAST ast, CompiledFunctionArg[] args, EvalEngine engine) {
    Map<IExpr, String> symbolicVariables = new HashMap<>();
    Map<IExpr, String> numericVariables = new HashMap<>();
    StringBuilder fieldsBuf = new StringBuilder();
    StringBuilder variablesBuf = new StringBuilder();
    variablesBuf.append("stack  = F.ast(S.List, 100, true);\n");
    variablesBuf.append("vars = new ExprTrie();\n");
    int top = 1;
    IBuiltInSymbol domain = S.Reals;

    for (int j = 0; j < args.length; j++) {
      IExpr variable = args[j].argument;
      if (numericVariables.containsKey(variable)) {
        Errors.printMessage(ast.topHead(), "fdup", F.list(variable, ast.arg1()), engine);
        return null;
      }

      String typeStr = "", suffix = "", evalName = "";
      IExpr argType = args[j].type;

      if (argType.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) argType).ordinal()) {
          case ID.Real:
            typeStr = "double";
            suffix = "d";
            evalName = "Double";
            break;
          case ID.Integer:
            typeStr = "int";
            suffix = "i";
            evalName = "Int";
            break;
          case ID.Complex:
            domain = S.Complexes;
            typeStr = "Complex";
            suffix = "c";
            evalName = "Complex";
            break;
          case ID.Booleans:
            typeStr = "boolean";
            suffix = "b";
            evalName = "Boolean";
            break;
          default:
            continue;
        }
      } else {
        continue;
      }

      String fieldName = "arg_" + (j + 1) + "_" + suffix;

      // Declare native Java primitive field
      fieldsBuf.append("  ").append(typeStr).append(" ").append(fieldName).append(";\n");

      // Initialize the field inside the evaluate method
      variablesBuf.append("this.").append(fieldName).append(" = engine.eval").append(evalName)
          .append("(ast.get(").append(j + 1).append("));\n");

      symbolicVariables.put(variable, variable.toString());
      numericVariables.put(variable, "this." + fieldName);
      top++;
    }

    IExpr expression = ast.arg2();
    VariableManager numericVars = new VariableManager(numericVariables);
    VariableManager symbolicVars = new VariableManager(symbolicVariables);

    CompileAnalyzer analyzer = new CompileAnalyzer();
    analyzer.analyze(expression);

    CompileFactory cf = new CompileFactory(numericVars, symbolicVars, args, top, domain,
        analyzer.getNodeTypes(), fieldsBuf);

    return generateClassSource(cf, expression, variablesBuf, fieldsBuf, args.length);
  }

  private static String generateClassSource(CompileFactory cf, IExpr expression,
      StringBuilder variablesBuf, StringBuilder fieldsBuf, int argsSize) {
    StringBuilder expressionBuf = new StringBuilder();
    StringBuilder methodsBuf = new StringBuilder();
    cf.convert(expressionBuf, methodsBuf, expression, false, true);

    // Unconditionally wrap the top-level evaluate() return with Symjify
    // This securely forces double, int, boolean, or Complex back into an IExpr
    String exprWrapped = "F.symjify(" + expressionBuf.toString() + ");\n";

    String source = JAVA_SOURCE_CODE.replace("{$fields}", fieldsBuf.toString());
    source = source.replace("{$variables}", variablesBuf.toString());
    source = source.replace("{$methods}", methodsBuf.toString());
    source = source.replace("{$expression}", exprWrapped);
    source = source.replace("{$size}", Integer.toString(argsSize));
    return source;
  }
}