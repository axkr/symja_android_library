package org.matheclipse.core.builtin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.Modifier;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.compile.CompileAnalyzer;
import org.matheclipse.core.compile.CompileFactory;
import org.matheclipse.core.compile.CompiledFunctionArg;
import org.matheclipse.core.compile.VariableManager;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.Blank;
import org.matheclipse.core.expression.ExprTrie;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.CompiledFunctionExpr;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class CompilerFunctions {

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

  private static class Compile extends AbstractCoreFunctionOptionEvaluator {
    @Override
    protected IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine) {
      if (!ToggleFeature.COMPILE) {
        return F.NIL;
      }
      try {
        CompiledFunctionArg[] args = checkIsVariableOrVariableList(ast, engine);
        if (args == null) {
          return F.NIL;
        }
        IExpr runtimeAttributes = options[0];
        CompiledFunctionExpr compiled = compile(ast, args, runtimeAttributes, engine);
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
      setOptions(newSymbol, S.RuntimeAttributes, F.CEmptyList);
    }
  }

  private static final class CompiledFunction extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      final IExpr head = ast.head();
      if (head instanceof CompiledFunctionExpr) {

        CompiledFunctionExpr compiledFunction = (CompiledFunctionExpr) head;
        int attributes = compiledFunction.getAttributes();
        if (attributes != ISymbol.NOATTRIBUTE) {
          IASTMutable copy = (ast instanceof IASTMutable) ? (IASTMutable) ast : ast.copy();
          IExpr temp = engine.evalAttributes(copy, copy.size(), S.None, attributes);
          if (temp.isAST()) {
            if (!(temp.head() instanceof CompiledFunctionExpr)) {
              return temp;
            }
            ast = (IAST) temp;
          }
        }

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

  public static class CompilePrint extends AbstractCoreFunctionOptionEvaluator {
    @Override
    protected IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine) {
      if (!ToggleFeature.COMPILE_PRINT) {
        return F.NIL;
      }
      CompiledFunctionArg[] args = checkIsVariableOrVariableList(ast, engine);
      if (args == null) {
        return F.NIL; 
      }
      String source = compilePrint(ast, args, engine);
      if (source != null) {
        return F.stringx(source, IStringX.APPLICATION_JAVA);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
      setOptions(newSymbol, S.RuntimeAttributes, F.CEmptyList);
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
      IExpr runtimeAttributes, EvalEngine engine) {
    try {
      IASTAppendable variables = F.ListAlloc(args.length);
      IASTAppendable types = F.ListAlloc(args.length);
      for (CompiledFunctionArg arg : args) {
        variables.append(arg.argument());
        types.append(arg.type());
      }
      String source = compilePrint(ast, args, engine);
      if (source != null) {
        SimpleCompiler comp = new org.codehaus.janino.SimpleCompiler();
        comp.cook(source);
        ClassLoader loader = comp.getClassLoader();
        Class<?> clazz = loader.loadClass("org.matheclipse.core.compile.CompiledFunction");
        return CompiledFunctionExpr.newInstance(variables, types, ast.arg2(), clazz,
            runtimeAttributes);
      }
    } catch (CompileException | ClassNotFoundException | RuntimeException e) {
      Errors.printMessage(S.Compile, e, engine);
    }
    return null;
  }

  public static String compilePrint(final IAST ast, CompiledFunctionArg[] args, EvalEngine engine) {
    Map<IExpr, String> symbolicVariables = new HashMap<>();
    Map<IExpr, String> numericVariables = new HashMap<>();
    IBuiltInSymbol domain = S.Reals;

    TypeSpec.Builder classBuilder = TypeSpec.classBuilder("CompiledFunction")
        .addModifiers(Modifier.PUBLIC).superclass(AbstractFunctionEvaluator.class);

    classBuilder.addField(EvalEngine.class, "engine", Modifier.PRIVATE);
    classBuilder.addField(IASTAppendable.class, "stack", Modifier.PRIVATE);
    classBuilder.addField(ExprTrie.class, "vars", Modifier.PRIVATE);
    classBuilder.addField(
        FieldSpec.builder(TypeName.INT, "top", Modifier.PRIVATE).initializer("1").build());

    // Force TYPE imports for all dynamically generated references so Janino recognizes "F.xxx()"
    classBuilder.addMethod(MethodSpec.methodBuilder("dummyImports").addModifiers(Modifier.PRIVATE)
        .returns(void.class).addParameter(org.matheclipse.core.expression.CMath.class, "c")
        .addParameter(org.matheclipse.core.expression.DMath.class, "d")
        .addParameter(org.matheclipse.core.expression.F.class, "f")
        .addParameter(org.matheclipse.core.expression.S.class, "s")
        .addParameter(org.hipparchus.complex.Complex.class, "cmp")
        .addParameter(org.matheclipse.core.interfaces.ISymbol.class, "isy")
        .addParameter(org.matheclipse.core.interfaces.IExpr.class, "iex")
        .addParameter(org.matheclipse.core.expression.ExprTrie.class, "et").build());

    MethodSpec.Builder evalMethod = MethodSpec.methodBuilder("evaluate")
        .addAnnotation(Override.class).addModifiers(Modifier.PUBLIC).returns(IExpr.class)
        .addParameter(IAST.class, "ast", Modifier.FINAL).addParameter(EvalEngine.class, "engine");

    evalMethod.beginControlFlow("if (ast.argSize() != $L)", args.length);
    evalMethod.addStatement("return print(ast, $L, engine)", args.length);
    evalMethod.endControlFlow();

    evalMethod.addStatement("this.engine = engine");

    evalMethod.addStatement("this.stack = $T.ast($T.List, 100, true)", F.class, S.class);
    evalMethod.addStatement("this.vars = new $T()", ExprTrie.class);

    for (int j = 0; j < args.length; j++) {
      IExpr variable = args[j].argument();
      if (numericVariables.containsKey(variable)) {
        Errors.printMessage(ast.topHead(), "fdup", F.list(variable, ast.arg1()), engine);
        return null;
      }

      TypeName baseTypeName = null;
      String suffix = "", evalName = "";
      IExpr argType = args[j].type();

      if (argType.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) argType).ordinal()) {
          case ID.Real:
            baseTypeName = TypeName.DOUBLE;
            suffix = "d";
            evalName = "Double";
            break;
          case ID.Integer:
            baseTypeName = TypeName.INT;
            suffix = "i";
            evalName = "Int";
            break;
          case ID.Complex:
            domain = S.Complexes;
            baseTypeName = ClassName.get(Complex.class);
            suffix = "c";
            evalName = "Complex";
            break;
          case ID.Booleans:
            baseTypeName = TypeName.BOOLEAN;
            suffix = "b";
            evalName = "Boolean";
            break;
          default:
            continue;
        }
      } else {
        continue;
      }

      TypeName finalTypeName = baseTypeName;
      String evalRankStr = "";
      switch (args[j].rank) {
        case VECTOR:
          finalTypeName = ArrayTypeName.of(baseTypeName);
          evalRankStr = "Vector";
          break;
        case MATRIX:
          finalTypeName = ArrayTypeName.of(ArrayTypeName.of(baseTypeName));
          evalRankStr = "Matrix";
          break;
        case SCALAR:
          break;
      }

      String fieldName = "arg_" + (j + 1) + "_" + suffix;

      classBuilder.addField(finalTypeName, fieldName, Modifier.PRIVATE);
      evalMethod.addStatement("this.$L = engine.eval$L$L(ast.get($L))", fieldName, evalName,
          evalRankStr, j + 1);

      symbolicVariables.put(variable, variable.toString());
      numericVariables.put(variable, "this." + fieldName);
    }

    IExpr expression = ast.arg2();
    VariableManager numericVars = new VariableManager(numericVariables);
    VariableManager symbolicVars = new VariableManager(symbolicVariables);

    CompileAnalyzer analyzer = new CompileAnalyzer();
    analyzer.analyze(expression);

    CompileFactory cf = new CompileFactory(numericVars, symbolicVars, args, domain,
        analyzer.getNodeTypes(), classBuilder);

    StringBuilder expressionBuf = new StringBuilder();
    cf.convert(expressionBuf, expression, false, true);

    String exprStr = expressionBuf.toString();
    if (exprStr.startsWith("throw ")) {
      evalMethod.addStatement("$L", exprStr);
    } else {
      evalMethod.addStatement("return $T.symjify($L)", F.class, exprStr);
    }

    classBuilder.addMethod(evalMethod.build());

    // Build the File without trying to use .addStaticImport()
    JavaFile javaFile = JavaFile.builder("org.matheclipse.core.compile", classBuilder.build())
        .addFileComment("Compile with Janino compiler\nDynamically generated by JavaPoet")
        .skipJavaLangImports(true).build();

    return javaFile.toString();
  }
}
