package org.matheclipse.core.compile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import org.matheclipse.core.builtin.CompilerFunctions.CompiledFunctionArg;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.JavaComplexFormFactory;
import org.matheclipse.core.form.output.JavaDoubleFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class CompileFactory {
  private int module = 1;
  private final IBuiltInSymbol domain;
  private HashSet<String> localVariables;
  private final VariableManager numericVariables;
  private final VariableManager variables;
  private int topOfStack;
  final CompiledFunctionArg[] args;

  private interface IConverter {
    boolean convert(CompileFactory factory, StringBuilder parentBuffer, StringBuilder methods,
        IAST function);
  }

  private static final Map<ISymbol, IConverter> CONVERTERS = new HashMap<>();

  static {
    CONVERTERS.put(S.CompoundExpression, CompileFactory::convertCompoundExpression);
    CONVERTERS.put(S.If, CompileFactory::convertIf);
    CONVERTERS.put(S.Set, CompileFactory::convertSet);
    CONVERTERS.put(S.Module, CompileFactory::convertModule);
  }

  public CompileFactory(VariableManager numericVariables, VariableManager variables,
      CompiledFunctionArg[] args, int topOfStack, IBuiltInSymbol domain) {
    this.localVariables = new HashSet<>();
    this.numericVariables = numericVariables;
    this.variables = variables;
    this.args = args;
    this.topOfStack = topOfStack;
    this.domain = domain;
  }

  private static boolean convertCompoundExpression(CompileFactory factory,
      final StringBuilder parentBuffer, final StringBuilder methods, final IAST f) {
    if (f.argSize() < 1) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    try {
      int m = factory.module++;
      methods.append("public IExpr compoundExpression").append(m).append("() {\n");
      tryBegin(methods);
      var subMethods = new StringBuilder();
      for (int i = 1; i < f.argSize(); i++) {
        var expressions = new StringBuilder();
        factory.convert(expressions, subMethods, f.get(i), false, true);
        methods.append(expressions).append(";\n");
      }
      var expressions = new StringBuilder();
      factory.convert(expressions, subMethods, f.last(), false, true);
      methods.append("return ").append(expressions).append(";\n");
      tryEnd(methods);
      methods.append("}\n\n").append(subMethods);
      parentBuffer.append("compoundExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertSet(CompileFactory factory, final StringBuilder parentBuffer,
      StringBuilder methods, final IAST f) {
    if (f.argSize() != 2 || !f.arg1().isVariable()) {
      return false;
    }
    String variable = f.arg1().toString();
    if (f.arg2().isNumericFunction(factory.numericVariables)) {
      var numericBuffer = new StringBuilder();
      int type = factory.convertNumeric(numericBuffer, f.arg2(), factory.domain);
      if (type > 0) {
        parentBuffer.append(type == 1 ? "INum " : "IComplexNum ").append(variable).append(" = ")
            .append(numericBuffer).append(";\n");
        parentBuffer.append("stack.set(top++, ").append(variable).append(")");
        factory.numericVariables.put(f.arg1(), "stack.get(" + (factory.topOfStack++) + ")");
        return true;
      }
    }
    parentBuffer.append("IExpr ").append(variable).append(" = ");
    factory.convert(parentBuffer, methods, f.arg2(), true, true);
    return true;
  }

  private static boolean convertIf(CompileFactory factory, final StringBuilder parentBuffer,
      StringBuilder methods, final IAST f) {
    if (f.argSize() < 2 || f.argSize() > 3) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    try {
      int m = factory.module++;
      methods.append("public IExpr ifExpression").append(m).append("() {\n");
      var subMethods = new StringBuilder();
      var expression = new StringBuilder();
      factory.convert(expression, subMethods, f.arg1(), false, true);
      methods.append("if(engine.evalTrue(").append(expression).append(")){\n");
      expression.setLength(0);
      factory.convert(expression, subMethods, f.arg2(), false, true);
      methods.append("return ").append(expression).append(";\n");
      if (f.size() == 4) {
        methods.append("} else {\n");
        expression.setLength(0);
        factory.convert(expression, subMethods, f.arg3(), false, true);
        methods.append("return ").append(expression).append(";\n");
      }
      methods.append("}\n}\n\n").append(subMethods);
      parentBuffer.append("ifExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertModule(CompileFactory factory, final StringBuilder parentBuffer,
      StringBuilder methods, final IAST f) {
    if (f.argSize() != 2 || !f.arg1().isList()) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    HashSet<String> oldLocalVariables = factory.localVariables;
    try {
      var localVariables = new HashSet<>(factory.localVariables);
      factory.localVariables = localVariables;
      IAST variableList = (IAST) f.arg1();
      int m = factory.module++;
      methods.append("public IExpr moduleExpression").append(m).append("() {\n");
      methods.append("ExprTrie oldVars = vars;");
      tryBegin(methods);
      methods.append("vars = vars.copy();\n");
      for (int i = 1; i < variableList.size(); i++) {
        IExpr arg = variableList.get(i);
        String symbolName;
        if (arg.isSymbol()) {
          symbolName = arg.toString();
        } else if (arg.isAST(S.Set, 3) && arg.first().isSymbol()) {
          symbolName = arg.first().toString();
        } else {
          return false;
        }
        localVariables.add(symbolName);
        methods.append("ISymbol ").append(symbolName).append(" = F.Dummy(\"").append(symbolName)
            .append("\");\n");
        methods.append("vars.put(\"").append(symbolName).append("\",").append(symbolName)
            .append(");\n");
        if (arg.isAST(S.Set, 3)) {
          var expressions = new StringBuilder();
          factory.convert(expressions, methods, arg.second(), true, false);
          methods.append("F.eval(F.Set(").append(symbolName).append(",")
              .append(expressions.toString()).append("));\n");
        }
      }
      var expressions = new StringBuilder();
      var subMethods = new StringBuilder();
      factory.convert(expressions, subMethods, f.arg2(), false, true);
      methods.append("return ").append(expressions).append(";\n");
      methods.append("} finally {top = oldTop; vars = oldVars;}\n");
      methods.append("}\n\n").append(subMethods);
      parentBuffer.append("moduleExpression").append(m).append("()");
    } finally {
      factory.localVariables = oldLocalVariables;
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  public void convert(StringBuilder buf, StringBuilder methods, IExpr expression, boolean symbolic,
      boolean addEval) {
    if (!symbolic && expression.isNumericFunction(numericVariables)) {
      int type = convertNumeric(buf, expression, domain);
      if (type > 0) {
        return;
      }
    }
    if (expression.isAST()) {
      IAST ast = (IAST) expression;
      if (ast.head().isSymbol()) {
        IConverter converter = CONVERTERS.get(ast.head());
        if (converter != null) {
          var sb = new StringBuilder();
          if (converter.convert(this, sb, methods, ast)) {
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

  private int convertNumeric(StringBuilder parentBuffer, IExpr expression, IBuiltInSymbol domain) {
    if (domain == S.Reals) {
      try {
        var buf = new StringBuilder();
        var factory = JavaDoubleFormFactory.get(true, false);
        buf.append("F.num(");
        IExpr substituted = F.subst(expression, getNumericSubstFunction("evalDouble"));
        factory.convert(buf, substituted);
        buf.append(")");
        parentBuffer.append(buf);
        return 1;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
    }
    try {
      var buf = new StringBuilder();
      var factory = JavaComplexFormFactory.get(true, false, -1, -1, true);
      buf.append("F.complexNum(");
      IExpr substituted = F.subst(expression, getNumericSubstFunction("evalComplex"));
      factory.convert(buf, substituted);
      buf.append(")");
      parentBuffer.append(buf);
      return 2;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return 0;
  }

  private Function<IExpr, IExpr> getNumericSubstFunction(String evalMethod) {
    return x -> {
      String str = numericVariables.apply(x);
      if (x.isSymbol() && str != null) {
        return F.stringx(evalMethod + "(" + str + ")");
      }
      return F.NIL;
    };
  }

  private boolean convertSymbolic(StringBuilder buf, IExpr expression) {
    try {
      buf.append(expression.internalJavaString(SourceCodeProperties.JAVA_FORM_PROPERTIES, -1, x -> {
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
