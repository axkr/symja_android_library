package org.matheclipse.compile.builtin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.Modifier;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.hipparchus.complex.Complex;
import org.matheclipse.compile.CompilationOptions;
import org.matheclipse.compile.CompileAnalyzer;
import org.matheclipse.compile.CompileFactory;
import org.matheclipse.compile.CompiledFunctionArg;
import org.matheclipse.compile.CompoundAssignment;
import org.matheclipse.compile.ConstantHoisting;
import org.matheclipse.compile.InlineDefinitions;
import org.matheclipse.compile.RuntimeOptions;
import org.matheclipse.compile.VariableManager;
import org.matheclipse.compile.expression.CompiledFunctionExpr;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.Blank;
import org.matheclipse.core.expression.ExprTrie;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
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

  /**
   * The package of the class
   * {@link #compilePrint(IAST, CompiledFunctionArg[], RuntimeOptions, EvalEngine)} generates and
   * {@link #compile(IAST, CompiledFunctionArg[], IExpr, RuntimeOptions, CompilationOptions, EvalEngine)}
   * loads back.
   *
   * <p>
   * This name is written into the generated source and read again by
   * {@link ClassLoader#loadClass(String)}, so the two uses must stay in step: a mismatch is a
   * {@link ClassNotFoundException} which is only reported as a message, and <code>Compile</code>
   * then silently stops working.
   */
  private static final String GENERATED_PACKAGE = "org.matheclipse.compile.generated";

  /** The simple name of the generated class. See {@link #GENERATED_PACKAGE}. */
  private static final String GENERATED_CLASS = "CompiledFunction";

  /**
   * The options of <code>Compile</code> and <code>CompilePrint</code>, in the order their values
   * are handed to the evaluators: <code>options[i]</code> holds the value of
   * <code>OPTION_SYMBOLS[i]</code>, so this array, {@link #OPTION_DEFAULTS} and the
   * <code>OPTION_</code> indices below have to stay in step.
   *
   * <p>
   * {@link S#RuntimeAttributes}, {@link S#RuntimeOptions} and {@link S#CompilationOptions} are
   * read; the remaining two are declared so that code parses and evaluates here instead of failing
   * with an argument count message - a trailing option is only recognized as an option if the
   * symbol names one, and is counted as an ordinary argument otherwise. Their values are accepted
   * unchecked and ignored; they are deliberately not validated, because deciding which settings
   * count as supported is part of implementing them.
   */
  private static final IBuiltInSymbol[] OPTION_SYMBOLS = {S.RuntimeAttributes, S.RuntimeOptions,
      S.CompilationOptions, S.CompilationTarget, S.Parallelization};

  /**
   * The default value of each option in {@link #OPTION_SYMBOLS}.
   *
   */
  private static final IExpr[] OPTION_DEFAULTS =
      {F.CEmptyList, S.Automatic, S.Automatic, F.stringx("WVM"), S.Automatic};

  /** Index of the {@link S#RuntimeAttributes} value. See {@link #OPTION_SYMBOLS}. */
  private static final int OPTION_RUNTIME_ATTRIBUTES = 0;

  /** Index of the {@link S#RuntimeOptions} value. See {@link #OPTION_SYMBOLS}. */
  private static final int OPTION_RUNTIME_OPTIONS = 1;

  /** Index of the {@link S#CompilationOptions} value. See {@link #OPTION_SYMBOLS}. */
  private static final int OPTION_COMPILATION_OPTIONS = 2;

  private static class Initializer {
    private static void init() {
      // Belt and braces since matheclipse-compile left matheclipse-core: the only two modules
      // which set Config.FUZZY_PARSER are matheclipse-api and matheclipse-discord, and neither
      // depends on this module any more, so this branch is not reached today. It stays because
      // the guard is about untrusted input, not about who happens to be on the classpath - a new
      // fuzzy-parsing front end must not get a runtime Java compiler by default.
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
        IExpr runtimeAttributes = options[OPTION_RUNTIME_ATTRIBUTES];
        RuntimeOptions runtimeOptions =
            RuntimeOptions.parse(options[OPTION_RUNTIME_OPTIONS], ast, engine);
        CompilationOptions compilationOptions =
            CompilationOptions.parse(options[OPTION_COMPILATION_OPTIONS], ast, engine);
        CompiledFunctionExpr compiled =
            compile(ast, args, runtimeAttributes, runtimeOptions, compilationOptions, engine);
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
      setOptions(newSymbol, OPTION_SYMBOLS, OPTION_DEFAULTS);
    }
  }

  private static final class CompiledFunction extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      final IExpr head = ast.head();
      if (head instanceof CompiledFunctionExpr) {

        CompiledFunctionExpr compiledFunction = (CompiledFunctionExpr) head;
        RuntimeOptions runtimeOptions = compiledFunction.getRuntimeOptions();
        IAST variables = compiledFunction.getVariables();
        if (ast.argSize() != variables.argSize()) {
          if (runtimeOptions.isWarningMessages()) {
            // The number of arguments `1` does not match the length `2` of the argument template.
            Errors.printMessage(S.CompiledFunction, "cfct",
                F.List(F.ZZ(ast.argSize()), F.ZZ(variables.argSize())), engine);
          }
          return F.CompiledFunction(variables);
        }

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
        } catch (ArgumentTypeException atex) {
          // a non-numeric (e.g. symbolic) argument was passed: fall back to uncompiled evaluation
          if (!runtimeOptions.isEvaluateSymbolically()) {
            // leave the call unevaluated instead - the compiled code is all the caller asked for
            return F.NIL;
          }
          printArgumentError(compiledFunction, ast, runtimeOptions, engine);
          return engine
              .evaluate(F.subst(compiledFunction.getExpr(), Functors.equalRules(variables, ast)));
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return handleRuntimeError(runtimeOptions, ast, rex, engine);
        }
        if (result.isPresent()) {
          result = engine.evaluate(result);
          if (result.isIndeterminate()) {
            if (!runtimeOptions.isEvaluateSymbolically()) {
              // the indeterminate value the compiled code computed is the answer
              return result;
            }
            printNumericalError(runtimeOptions, engine);
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
      RuntimeOptions runtimeOptions =
          RuntimeOptions.parse(options[OPTION_RUNTIME_OPTIONS], ast, engine);
      CompilationOptions compilationOptions =
          CompilationOptions.parse(options[OPTION_COMPILATION_OPTIONS], ast, engine);
      String source = compilePrint(ast, args, runtimeOptions, compilationOptions, engine);
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
      setOptions(newSymbol, OPTION_SYMBOLS, OPTION_DEFAULTS);
    }
  }

  /**
   * Report why the compiled code could not take this call, naming the argument which is at fault
   * where one can be found.
   *
   * <p>
   * The compiled code reads each argument as a machine number and throws if it is not one, but it
   * does not say which argument that was - and a call which quietly falls back to evaluating the
   * uncompiled expression instead is easy to miss until it turns a fast function into a slow one.
   * Naming the argument and its position is what makes the difference between "something went
   * numerically wrong" and "argument 3 is not a real number".
   *
   * <p>
   * The arguments are checked in order, the same way the generated code reads them, and the first
   * one which does not fit its declared type is reported. A list is skipped: this expression
   * records the type of a vector or matrix argument but not its rank, so a list may well be exactly
   * what the argument template asks for. If no argument is at fault - the call failed somewhere
   * inside the body - the general message is reported instead.
   */
  private static void printArgumentError(CompiledFunctionExpr compiledFunction, IAST ast,
      RuntimeOptions runtimeOptions, EvalEngine engine) {
    if (!runtimeOptions.isWarningMessages()) {
      return;
    }
    IAST types = compiledFunction.getTypes();
    for (int i = 1; i < types.size() && i <= ast.argSize(); i++) {
      IExpr argument = ast.get(i);
      if (argument.isList()) {
        continue;
      }
      String expected = expectedNumberKind(types.get(i), argument, engine);
      if (expected != null) {
        // Argument `1` at position `2` should be a machine-size `3`.
        Errors.printMessage(S.CompiledFunction, "cfsa",
            F.list(argument, F.ZZ(i), F.stringx(expected)), engine);
        return;
      }
    }
    printNumericalError(runtimeOptions, engine);
  }

  /**
   * The kind of number <code>type</code> asks for, if <code>argument</code> cannot be read as one,
   * and <code>null</code> if it can.
   */
  private static String expectedNumberKind(IExpr type, IExpr argument, EvalEngine engine) {
    if (!type.isBuiltInSymbol()) {
      return null;
    }
    int ordinal = ((IBuiltInSymbol) type).ordinal();
    try {
      switch (ordinal) {
        case ID.Real:
          engine.evalDouble(argument);
          return null;
        case ID.Integer:
          engine.evalInt(argument);
          return null;
        case ID.Complex:
          engine.evalComplex(argument);
          return null;
        default:
          return null;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    switch (ordinal) {
      case ID.Real:
        return "real number";
      case ID.Integer:
        return "integer";
      case ID.Complex:
        return "complex number";
      default:
        return null;
    }
  }

  /**
   * Report that the compiled code could not produce a number, unless
   * <code>"WarningMessages" -> False</code> asked for silence.
   */
  private static void printNumericalError(RuntimeOptions runtimeOptions, EvalEngine engine) {
    if (runtimeOptions.isWarningMessages()) {
      // Numerical error encountered, proceeding with uncompiled evaluation.
      Errors.printMessage(S.CompiledFunction, "cfn", F.CEmptyList, engine);
    }
  }

  /**
   * Deal with an exception thrown out of the compiled code, as <code>"RuntimeErrorHandler"</code>
   * asks for.
   *
   * <p>
   * The default handler <code>Evaluate</code> reports the exception and leaves the call
   * unevaluated. Any other handler is applied to the call which failed, and its result becomes the
   * result of the call.
   *
   * <p>
   * The call is handed to the handler wrapped in <code>HoldForm</code>. Without it, evaluating
   * <code>handler(compiledCall)</code> would run the compiled code again, throw the same exception
   * again and call the handler again, so any handler which does not hold its argument would not
   * return - which is a poor answer to "tell me what to do when this fails".
   */
  private static IExpr handleRuntimeError(RuntimeOptions runtimeOptions, IAST ast,
      RuntimeException rex, EvalEngine engine) {
    if (runtimeOptions.isWarningMessages()) {
      // `1`.
      Errors.printMessage(S.CompiledFunction, rex, engine);
    }
    IExpr handler = runtimeOptions.getRuntimeErrorHandler();
    if (handler.equals(S.Evaluate)) {
      return F.NIL;
    }
    return engine.evaluate(F.unaryAST1(handler, F.HoldForm(ast)));
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
      IExpr runtimeAttributes, RuntimeOptions runtimeOptions, CompilationOptions compilationOptions,
      EvalEngine engine) {
    try {
      IASTAppendable variables = F.ListAlloc(args.length);
      IASTAppendable types = F.ListAlloc(args.length);
      for (CompiledFunctionArg arg : args) {
        variables.append(arg.argument());
        types.append(arg.type());
      }
      String source = compilePrint(ast, args, runtimeOptions, compilationOptions, engine);
      if (source != null) {
        SimpleCompiler comp = new org.codehaus.janino.SimpleCompiler();
        comp.cook(source);
        ClassLoader loader = comp.getClassLoader();
        Class<?> clazz = loader.loadClass(GENERATED_PACKAGE + "." + GENERATED_CLASS);
        return CompiledFunctionExpr.newInstance(variables, types, ast.arg2(), clazz,
            runtimeAttributes, runtimeOptions);
      }
    } catch (CompileException | ClassNotFoundException | RuntimeException e) {
      Errors.printMessage(S.Compile, e, engine);
    }
    return null;
  }

  public static String compilePrint(final IAST ast, CompiledFunctionArg[] args,
      RuntimeOptions runtimeOptions, CompilationOptions compilationOptions, EvalEngine engine) {
    Map<IExpr, String> symbolicVariables = new HashMap<>();
    Map<IExpr, String> numericVariables = new HashMap<>();
    IBuiltInSymbol domain = S.Reals;

    TypeSpec.Builder classBuilder = TypeSpec.classBuilder(GENERATED_CLASS)
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

    // rewrite `x += y` and its relatives into the `Set` they stand for, before anything looks at
    // the expression: the analyzer keys the types it infers on node identity, so the analyzer and
    // the code generator have to see the same nodes
    // expand the calls to other compiled functions first - the bodies they paste in may themselves
    // contain compound assignments, which the next line has to see
    IExpr expression = CompoundAssignment
        .normalize(InlineDefinitions.inline(ast.arg2(), compilationOptions, ast, engine));

    // lift the large constant lists into fields, before the analyzer records a type for every node
    // it sees: it keys those on node identity, so it and the code generator have to see the same
    // expression
    ConstantHoisting.Result hoisted = ConstantHoisting.hoist(expression);
    expression = hoisted.expression();
    Map<ISymbol, String> constantFields = new HashMap<>();
    Map<ISymbol, CompileFactory.ConstantArray> constantArrays = new HashMap<>();
    Map<ISymbol, CompileAnalyzer.VarType> constantElementTypes = new HashMap<>();
    int constantId = 1;
    for (Map.Entry<ISymbol, IExpr> constant : hoisted.constants().entrySet()) {
      String fieldName = "const_" + constantId++;
      // a lifted constant contains numbers and lists only, so it needs no variable substitution
      CharSequence source = constant.getValue()
          .internalJavaString(SourceCodeProperties.JAVA_FORM_PROPERTIES, -1, x -> null);
      classBuilder.addField(FieldSpec
          .builder(IExpr.class, fieldName, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
          .initializer("$L", source.toString()).build());
      constantFields.put(constant.getKey(), fieldName);

      // a table of numbers is emitted a second time as a primitive array, so that reading it
      // compiles to an array access rather than to an evaluation of Part
      int rank = ConstantHoisting.numericArrayRank(constant.getValue());
      if (rank > 0) {
        String arrayName = fieldName + "_a";
        TypeName arrayType = ArrayTypeName.of(TypeName.DOUBLE);
        if (rank == 2) {
          arrayType = ArrayTypeName.of(arrayType);
        }
        classBuilder.addField(FieldSpec
            .builder(arrayType, arrayName, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("$L", ConstantHoisting.arrayInitializer(constant.getValue())).build());
        constantArrays.put(constant.getKey(), new CompileFactory.ConstantArray(arrayName, rank));
        constantElementTypes.put(constant.getKey(),
            ConstantHoisting.isIntegerArray(constant.getValue()) ? CompileAnalyzer.VarType.INTEGER
                : CompileAnalyzer.VarType.REAL);
      }
    }
    VariableManager numericVars = new VariableManager(numericVariables);
    VariableManager symbolicVars = new VariableManager(symbolicVariables);

    CompileAnalyzer analyzer = new CompileAnalyzer();
    for (Map.Entry<ISymbol, CompileAnalyzer.VarType> constant : constantElementTypes.entrySet()) {
      analyzer.declareConstantArray(constant.getKey(), constant.getValue());
    }
    // seed the analyzer with the declared argument types, so downstream type inference can
    // propagate them (e.g. so that `a*b-b` on integer arguments infers an integer result)
    boolean allIntegerScalarArgs = args.length > 0;
    for (CompiledFunctionArg arg : args) {
      CompileAnalyzer.VarType varType = CompileAnalyzer.VarType.UNKNOWN;
      IExpr argType = arg.type();
      if (argType.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) argType).ordinal()) {
          case ID.Integer:
            varType = CompileAnalyzer.VarType.INTEGER;
            break;
          case ID.Real:
            varType = CompileAnalyzer.VarType.REAL;
            break;
          case ID.Complex:
            varType = CompileAnalyzer.VarType.COMPLEX;
            break;
          case ID.Booleans:
            varType = CompileAnalyzer.VarType.BOOLEAN;
            break;
          default:
            break;
        }
      }
      if (arg.argument().isSymbol() && varType != CompileAnalyzer.VarType.UNKNOWN) {
        analyzer.declareVariable((ISymbol) arg.argument(), varType);
      }
      if (varType != CompileAnalyzer.VarType.INTEGER
          || arg.rank() != CompiledFunctionArg.Rank.SCALAR) {
        allIntegerScalarArgs = false;
      }
    }
    CompileAnalyzer.VarType resultType = analyzer.analyze(expression);

    // an integer-typed pure numeric result (all integer scalar arguments) should be returned as an
    // exact machine integer instead of a real, to match `Compile` semantics
    boolean coerceToInteger =
        allIntegerScalarArgs && domain == S.Reals && resultType == CompileAnalyzer.VarType.INTEGER
            && (expression.isNumericFunction(numericVars) || ConstantHoisting
                .isNumericWithConstantReads(expression, hoisted.constants(), numericVars));

    CompileFactory cf = new CompileFactory(numericVars, symbolicVars, args, domain,
        analyzer.getNodeTypes(), classBuilder, constantFields, constantArrays);

    StringBuilder expressionBuf = new StringBuilder();
    cf.convert(expressionBuf, expression, false, true);

    String exprStr = expressionBuf.toString();
    if (exprStr.startsWith("throw ")) {
      evalMethod.addStatement("$L", exprStr);
    } else if (coerceToInteger) {
      // "CatchMachineIntegerOverflow" decides whether the result is tested for leaving the range
      // an exact integer can represent, or is cast and handed back whatever came out
      String wrapper = runtimeOptions.isCatchMachineIntegerOverflow() ? "symjifyInteger"
          : "symjifyIntegerUnchecked";
      evalMethod.addStatement("return $T.$L($L)", CompiledFunctionExpr.class, wrapper, exprStr);
    } else {
      evalMethod.addStatement("return $T.symjify($L)", F.class, exprStr);
    }

    classBuilder.addMethod(evalMethod.build());

    // Build the File without trying to use .addStaticImport()
    JavaFile javaFile = JavaFile.builder(GENERATED_PACKAGE, classBuilder.build())
        .addFileComment("Compile with Janino compiler\nDynamically generated by JavaPoet")
        .skipJavaLangImports(true).build();

    return javaFile.toString();
  }
}
