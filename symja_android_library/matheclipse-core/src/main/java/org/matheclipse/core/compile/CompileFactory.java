package org.matheclipse.core.compile;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import org.matheclipse.core.builtin.CompilerFunctions.CompiledFunctionArg;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.JavaComplexFormFactory;
import org.matheclipse.core.form.output.JavaDoubleFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class CompileFactory {
  private int module = 1;
  private int fieldCounter = 1;
  private final IBuiltInSymbol domain;
  private HashSet<String> localVariables;
  private final VariableManager numericVariables;
  private final VariableManager variables;
  private int topOfStack;
  final CompiledFunctionArg[] args;
  private final Map<IExpr, CompileAnalyzer.VarType> nodeTypes;
  private final StringBuilder fieldsBuf;

  public CompileFactory(VariableManager numericVariables, VariableManager variables,
      CompiledFunctionArg[] args, int topOfStack, IBuiltInSymbol domain,
      Map<IExpr, CompileAnalyzer.VarType> nodeTypes, StringBuilder fieldsBuf) {
    this.localVariables = new HashSet<>();
    this.numericVariables = numericVariables;
    this.variables = variables;
    this.args = args;
    this.topOfStack = topOfStack;
    this.domain = domain;
    this.nodeTypes = nodeTypes;
    this.fieldsBuf = fieldsBuf;
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
        methods.append("      ").append(expressions).append(";\n");
      }
      var expressions = new StringBuilder();
      factory.convert(expressions, subMethods, f.last(), false, true);
      methods.append("return F.symjify(").append(expressions).append(");\n");
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

    CompileAnalyzer.VarType inferredType =
        factory.nodeTypes.getOrDefault(f.arg2(), CompileAnalyzer.VarType.UNKNOWN);
    boolean isNumericRHS = inferredType == CompileAnalyzer.VarType.REAL
        || inferredType == CompileAnalyzer.VarType.INTEGER
        || f.arg2().isNumericFunction(factory.numericVariables);

    if (isNumericRHS) {
      var numericBuffer = new StringBuilder();
      int type = factory.convertNumeric(numericBuffer, f.arg2(), factory.domain);
      if (type > 0) {
        int m = factory.module++;
        String existingVar = factory.numericVariables.apply(f.arg1());
        String fieldName;

        if (existingVar != null && existingVar.startsWith("this.")) {
          fieldName = existingVar.substring(5);
        } else {
          int fieldId = factory.fieldCounter++;
          fieldName = "local_var_" + fieldId + "_" + (type == 1 ? "d" : "c");
          factory.numericVariables.put(f.arg1(), "this." + fieldName);
          factory.fieldsBuf.append("  ").append(type == 1 ? "double " : "Complex ")
              .append(fieldName).append(";\n");
        }

        methods.append("public IExpr setExpression").append(m).append("() {\n");
        methods.append("  this.").append(fieldName).append(" = ").append(numericBuffer)
            .append(";\n");

        String returnExpr = "F.symjify(this." + fieldName + ")";

        if (factory.localVariables.contains(variable)) {
          methods.append("  F.eval(F.Set(vars.get(\"").append(variable).append("\"), ")
              .append(returnExpr).append("));\n");
        }

        methods.append("  return ").append(returnExpr).append(";\n}\n\n");
        parentBuffer.append("setExpression").append(m).append("()");
        return true;
      }
    }
    return false;
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

      boolean optimizedTest = factory.tryOptimizeCondition(expression, f.arg1());
      if (!optimizedTest) {
        factory.convert(expression, subMethods, f.arg1(), false, true);
        methods.append("if(engine.evalTrue(").append(expression).append(")){\n");
      } else {
        methods.append("if(").append(expression).append("){\n");
      }

      expression.setLength(0);
      factory.convert(expression, subMethods, f.arg2(), false, true);
      methods.append("return F.symjify(").append(expression).append(");\n");
      if (f.isAST3()) {
        methods.append("} else {\n");
        expression.setLength(0);
        factory.convert(expression, subMethods, f.arg3(), false, true);
        methods.append("return F.symjify(").append(expression).append(");\n");
      }
      methods.append("}\n}\n\n").append(subMethods);
      parentBuffer.append("ifExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertWhich(CompileFactory factory, final StringBuilder parentBuffer,
      StringBuilder methods, final IAST f) {
    if (f.argSize() < 2 || f.argSize() % 2 != 0) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    try {
      int m = factory.module++;
      methods.append("public IExpr whichExpression").append(m).append("() {\n");
      var subMethods = new StringBuilder();

      for (int i = 1; i < f.argSize(); i += 2) {
        var testExpr = new StringBuilder();
        boolean optimized = factory.tryOptimizeCondition(testExpr, f.get(i));
        if (!optimized) {
          factory.convert(testExpr, subMethods, f.get(i), false, true);
          methods.append(i == 1 ? "if(" : "} else if(").append("engine.evalTrue(").append(testExpr)
              .append(")){\n");
        } else {
          methods.append(i == 1 ? "if(" : "} else if(").append(testExpr).append("){\n");
        }
        var valExpr = new StringBuilder();
        factory.convert(valExpr, subMethods, f.get(i + 1), false, true);
        methods.append("return F.symjify(").append(valExpr).append(");\n");
      }
      methods.append("}\nreturn F.Null;\n}\n\n").append(subMethods);
      parentBuffer.append("whichExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertDo(CompileFactory factory, final StringBuilder parentBuffer,
      StringBuilder methods, final IAST f) {
    if (f.argSize() != 2 || !f.arg2().isList()) {
      return false;
    }
    IAST iter = (IAST) f.arg2();
    if (iter.argSize() < 1) {
      return false;
    }

    factory.variables.push();
    factory.numericVariables.push();
    try {
      int m = factory.module++;
      methods.append("public IExpr doExpression").append(m).append("() {\n");
      var subMethods = new StringBuilder();

      if (iter.argSize() == 1) {
        String iterName = "iter_" + m;
        var imaxBuf = new StringBuilder();
        factory.convert(imaxBuf, subMethods, iter.arg1(), false, true);
        methods.append("  double ").append(iterName).append("_max = engine.evalDouble(F.symjify(")
            .append(imaxBuf).append("));\n");

        int loopId = factory.fieldCounter++;
        String loopVarName = "loop_var_" + loopId + "_d";
        factory.fieldsBuf.append("  double ").append(loopVarName).append(";\n");

        methods.append("  for (this.").append(loopVarName).append(" = 1; this.").append(loopVarName)
            .append(" <= ").append(iterName).append("_max; this.").append(loopVarName)
            .append("++) {\n");

        methods.append("    int oldTop = top;\n    try {\n");
        var bodyExpr = new StringBuilder();
        factory.convert(bodyExpr, subMethods, f.arg1(), false, true);
        methods.append("      ").append(bodyExpr).append(";\n");
        methods.append(
            "    } catch (org.matheclipse.core.eval.exception.BreakException e) { break; }\n");
        methods
            .append("      catch (org.matheclipse.core.eval.exception.ContinueException e) { }\n");
        methods.append("      finally { top = oldTop; }\n");
        methods.append("  }\n");
      } else {
        IExpr loopVar = iter.arg1();
        if (!loopVar.isSymbol())
          return false;

        IExpr imin = F.C1;
        IExpr imax = iter.arg2();
        IExpr step = F.C1;
        if (iter.argSize() >= 3) {
          imin = iter.arg2();
          imax = iter.arg3();
        }
        if (iter.argSize() >= 4) {
          step = iter.arg4();
        }

        var iminBuf = new StringBuilder();
        factory.convert(iminBuf, subMethods, imin, false, true);
        var imaxBuf = new StringBuilder();
        factory.convert(imaxBuf, subMethods, imax, false, true);
        var stepBuf = new StringBuilder();
        factory.convert(stepBuf, subMethods, step, false, true);

        String iterName = "iter_" + m;
        methods.append("  double ").append(iterName).append("_min = engine.evalDouble(F.symjify(")
            .append(iminBuf).append("));\n");
        methods.append("  double ").append(iterName).append("_max = engine.evalDouble(F.symjify(")
            .append(imaxBuf).append("));\n");
        methods.append("  double ").append(iterName).append("_step = engine.evalDouble(F.symjify(")
            .append(stepBuf).append("));\n");

        int loopId = factory.fieldCounter++;
        String loopVarName = "loop_var_" + loopId + "_d";
        factory.fieldsBuf.append("  double ").append(loopVarName).append(";\n");

        // Check step direction natively to allow loops from N down to 2 with step -1
        String loopCond = iterName + "_step > 0 ? this." + loopVarName + " <= " + iterName
            + "_max : this." + loopVarName + " >= " + iterName + "_max";

        methods.append("  for (this.").append(loopVarName).append(" = ").append(iterName)
            .append("_min; ");
        methods.append(loopCond).append("; ");
        methods.append("this.").append(loopVarName).append(" += ").append(iterName)
            .append("_step) {\n");

        factory.numericVariables.put(loopVar, "this." + loopVarName);

        methods.append("    int oldTop = top;\n    try {\n");

        // Force synchronization to vars so inner components like compoundExpression() can access i
        methods.append("      IExpr syncVar_").append(m).append(" = vars.get(\"")
            .append(loopVar.toString()).append("\");\n");
        methods.append("      if (syncVar_").append(m).append(" != null) {\n");
        methods.append("        F.eval(F.Set(syncVar_").append(m).append(", F.symjify(this.")
            .append(loopVarName).append(")));\n");
        methods.append("      }\n");

        var bodyExpr = new StringBuilder();
        factory.convert(bodyExpr, subMethods, f.arg1(), false, true);
        methods.append("      ").append(bodyExpr).append(";\n");
        methods.append(
            "    } catch (org.matheclipse.core.eval.exception.BreakException e) { break; }\n");
        methods
            .append("      catch (org.matheclipse.core.eval.exception.ContinueException e) { }\n");
        methods.append("      finally { top = oldTop; }\n");
        methods.append("  }\n");
      }

      methods.append("  return F.Null;\n}\n\n").append(subMethods);
      parentBuffer.append("doExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertWhile(CompileFactory factory, final StringBuilder parentBuffer,
      StringBuilder methods, final IAST f) {
    if (f.argSize() < 1 || f.argSize() > 2) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    try {
      int m = factory.module++;
      methods.append("public IExpr whileExpression").append(m).append("() {\n");
      var subMethods = new StringBuilder();
      var testExpr = new StringBuilder();

      boolean optimizedTest = factory.tryOptimizeCondition(testExpr, f.arg1());
      if (!optimizedTest) {
        factory.convert(testExpr, subMethods, f.arg1(), false, true);
        methods.append("  while(engine.evalTrue(").append(testExpr).append(")){\n");
      } else {
        methods.append("  while(").append(testExpr).append("){\n");
      }

      if (f.argSize() == 2) {
        methods.append("    int oldTop = top;\n    try {\n");
        var bodyExpr = new StringBuilder();
        factory.convert(bodyExpr, subMethods, f.arg2(), false, true);
        methods.append("      ").append(bodyExpr).append(";\n");
        methods.append(
            "    } catch (org.matheclipse.core.eval.exception.BreakException e) { break; }\n");
        methods.append(
            "      catch (org.matheclipse.core.eval.exception.ContinueException e) { continue; }\n");
        methods.append("      finally { top = oldTop; }\n");
      }
      methods.append("  }\n  return F.Null;\n}\n\n").append(subMethods);
      parentBuffer.append("whileExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertFor(CompileFactory factory, final StringBuilder parentBuffer,
      StringBuilder methods, final IAST f) {
    if (f.argSize() != 4) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    try {
      int m = factory.module++;
      methods.append("public IExpr forExpression").append(m).append("() {\n");
      var subMethods = new StringBuilder();

      var startExpr = new StringBuilder();
      factory.convert(startExpr, subMethods, f.arg1(), false, true);
      methods.append("  ").append(startExpr).append(";\n");

      var testExpr = new StringBuilder();
      boolean optimizedTest = factory.tryOptimizeCondition(testExpr, f.arg2());
      if (!optimizedTest) {
        factory.convert(testExpr, subMethods, f.arg2(), false, true);
        methods.append("  while(engine.evalTrue(").append(testExpr).append(")){\n");
      } else {
        methods.append("  while(").append(testExpr).append("){\n");
      }

      methods.append("    int oldTop = top;\n    try {\n");
      var bodyExpr = new StringBuilder();
      factory.convert(bodyExpr, subMethods, f.arg4(), false, true);
      methods.append("      ").append(bodyExpr).append(";\n");
      methods.append(
          "    } catch (org.matheclipse.core.eval.exception.BreakException e) { break; }\n");
      methods.append("      catch (org.matheclipse.core.eval.exception.ContinueException e) { }\n");
      methods.append("      finally { top = oldTop; }\n");

      var incrExpr = new StringBuilder();
      factory.convert(incrExpr, subMethods, f.arg3(), false, true);
      methods.append("    ").append(incrExpr).append(";\n");

      methods.append("  }\n  return F.Null;\n}\n\n").append(subMethods);
      parentBuffer.append("forExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertScope(CompileFactory factory, final StringBuilder parentBuffer,
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
      methods.append("public IExpr scopeExpression").append(m).append("() {\n");
      methods.append("ExprTrie oldVars = vars;\n");
      tryBegin(methods);
      methods.append("vars = vars.copy();\n");

      for (int i = 1; i <= variableList.argSize(); i++) {
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
          boolean isNumericInit = false;
          CompileAnalyzer.VarType inferredType =
              factory.nodeTypes.getOrDefault(arg.second(), CompileAnalyzer.VarType.UNKNOWN);

          if (inferredType == CompileAnalyzer.VarType.REAL
              || inferredType == CompileAnalyzer.VarType.INTEGER
              || arg.second().isNumericFunction(factory.numericVariables)) {
            var numericBuffer = new StringBuilder();
            int type = factory.convertNumeric(numericBuffer, arg.second(), factory.domain);
            if (type > 0) {
              int fieldId = factory.fieldCounter++;
              String fieldName = "local_var_" + fieldId + "_" + (type == 1 ? "d" : "c");
              factory.fieldsBuf.append("  ").append(type == 1 ? "double " : "Complex ")
                  .append(fieldName).append(";\n");
              factory.numericVariables.put(arg.first(), "this." + fieldName);

              methods.append("  this.").append(fieldName).append(" = ").append(numericBuffer)
                  .append(";\n");

              String returnExpr = "F.symjify(this." + fieldName + ")";
              methods.append("  F.eval(F.Set(").append(symbolName).append(", ").append(returnExpr)
                  .append("));\n");
              isNumericInit = true;
            }
          }

          if (!isNumericInit) {
            var expressions = new StringBuilder();
            factory.convert(expressions, methods, arg.second(), true, false);
            methods.append("  F.eval(F.Set(").append(symbolName).append(", F.symjify(")
                .append(expressions).append(")));\n");
          }
        }
      }
      var expressions = new StringBuilder();
      var subMethods = new StringBuilder();
      factory.convert(expressions, subMethods, f.arg2(), false, true);
      methods.append("return F.symjify(").append(expressions).append(");\n");
      methods.append("} finally {top = oldTop; vars = oldVars;}\n");
      methods.append("}\n\n").append(subMethods);
      parentBuffer.append("scopeExpression").append(m).append("()");
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
      IExpr head = ast.head();
      if (head.isBuiltInSymbol()) {
        boolean converted = false;
        var sb = new StringBuilder();

        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.CompoundExpression:
            converted = convertCompoundExpression(this, sb, methods, ast);
            break;
          case ID.If:
            converted = convertIf(this, sb, methods, ast);
            break;
          case ID.Which:
            converted = convertWhich(this, sb, methods, ast);
            break;
          case ID.Set:
          case ID.SetDelayed:
            converted = convertSet(this, sb, methods, ast);
            break;
          case ID.Module:
          case ID.Block:
          case ID.With:
            converted = convertScope(this, sb, methods, ast);
            break;
          case ID.While:
            converted = convertWhile(this, sb, methods, ast);
            break;
          case ID.Do:
            converted = convertDo(this, sb, methods, ast);
            break;
          case ID.For:
            converted = convertFor(this, sb, methods, ast);
            break;
          case ID.Break:
            sb.append("throw new org.matheclipse.core.eval.exception.BreakException()");
            converted = true;
            break;
          case ID.Continue:
            sb.append("throw new org.matheclipse.core.eval.exception.ContinueException()");
            converted = true;
            break;
          case ID.Return:
            sb.append("throw new org.matheclipse.core.eval.exception.ReturnException(");
            if (ast.argSize() == 1) {
              var tempBuf = new StringBuilder();
              convert(tempBuf, methods, ast.arg1(), symbolic, addEval);
              sb.append("F.symjify(").append(tempBuf).append(")");
            } else {
              sb.append("F.Null");
            }
            sb.append(")");
            converted = true;
            break;
        }

        if (converted) {
          buf.append(sb);
          return;
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

  private boolean tryOptimizeCondition(StringBuilder testExpr, IExpr arg) {
    if (arg.isAST2() && domain == S.Reals) {
      IAST testAST = (IAST) arg;
      IExpr head = testAST.head();

      if (head.isBuiltInSymbol() && testAST.arg1().isNumericFunction(numericVariables)
          && testAST.arg2().isNumericFunction(numericVariables)) {

        String op = null;
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Less:
            op = "<";
            break;
          case ID.LessEqual:
            op = "<=";
            break;
          case ID.Greater:
            op = ">";
            break;
          case ID.GreaterEqual:
            op = ">=";
            break;
          case ID.Equal:
            op = "==";
            break;
          case ID.Unequal:
            op = "!=";
            break;
        }

        if (op != null) {
          var leftBuf = new StringBuilder();
          var rightBuf = new StringBuilder();
          var doubleFactory = JavaDoubleFormFactory.get(true, false);

          try {
            doubleFactory.convert(leftBuf,
                F.subst(testAST.arg1(), getNumericSubstFunction("evalf")));
            doubleFactory.convert(rightBuf,
                F.subst(testAST.arg2(), getNumericSubstFunction("evalf")));
            testExpr.append("(").append(leftBuf).append(") ").append(op).append(" (")
                .append(rightBuf).append(")");
            return true;
          } catch (RuntimeException rex) {
            testExpr.setLength(0);
          }
        }
      }
    }
    return false;
  }

  private int convertNumeric(StringBuilder parentBuffer, IExpr expression, IBuiltInSymbol domain) {
    if (domain == S.Reals) {
      try {
        var buf = new StringBuilder();
        var factory = JavaDoubleFormFactory.get(true, false);
        IExpr substituted = F.subst(expression, getNumericSubstFunction("evalf"));
        factory.convert(buf, substituted);
        parentBuffer.append(buf);
        return 1;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
    }
    try {
      var buf = new StringBuilder();
      var factory = JavaComplexFormFactory.get(true, false, -1, -1, true);
      IExpr substituted = F.subst(expression, getNumericSubstFunction("evalComplex"));
      factory.convert(buf, substituted);
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
        if (str.startsWith("this.")) {
          return F.stringx(str);
        }
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
        String str = numericVariables.apply(x);
        if (str != null) {
          if (str.startsWith("this.") || str.startsWith("loop_var_")) {
            return "F.symjify(" + str + ")";
          }
          return str;
        }
        return null;
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
