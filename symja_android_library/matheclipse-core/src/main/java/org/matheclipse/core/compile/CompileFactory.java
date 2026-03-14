package org.matheclipse.core.compile;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import javax.lang.model.element.Modifier;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.JavaComplexFormFactory;
import org.matheclipse.core.form.output.JavaDoubleFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class CompileFactory {
  private int module = 1;
  private int fieldCounter = 1;
  private final IBuiltInSymbol domain;
  private HashSet<String> localVariables;
  private final VariableManager numericVariables;
  private final VariableManager variables;
  final CompiledFunctionArg[] args;
  private final Map<IExpr, CompileAnalyzer.VarType> nodeTypes;
  private final TypeSpec.Builder classBuilder;

  public CompileFactory(VariableManager numericVariables, VariableManager variables,
      CompiledFunctionArg[] args, IBuiltInSymbol domain,
      Map<IExpr, CompileAnalyzer.VarType> nodeTypes, TypeSpec.Builder classBuilder) {
    this.localVariables = new HashSet<>();
    this.numericVariables = numericVariables;
    this.variables = variables;
    this.args = args;
    this.domain = domain;
    this.nodeTypes = nodeTypes;
    this.classBuilder = classBuilder;
  }

  private static boolean convertCompoundExpression(CompileFactory factory,
      final StringBuilder parentBuffer, final IAST f) {
    if (f.argSize() < 1) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    try {
      int m = factory.module++;
      MethodSpec.Builder method = MethodSpec.methodBuilder("compoundExpression" + m)
          .addModifiers(Modifier.PRIVATE).returns(IExpr.class);

      method.addStatement("int oldTop = top");
      method.beginControlFlow("try");

      for (int i = 1; i < f.argSize(); i++) {
        StringBuilder expressions = new StringBuilder();
        factory.convert(expressions, f.get(i), false, true);
        String exprStr = expressions.toString();
        if (exprStr.startsWith("throw ")) {
          method.addStatement("$L", exprStr);
        } else {
          method.addStatement("Object _discard_$L_$L = $L", m, i, exprStr);
        }
      }

      StringBuilder expressions = new StringBuilder();
      factory.convert(expressions, f.last(), false, true);
      String exprStr = expressions.toString();
      if (exprStr.startsWith("throw ")) {
        method.addStatement("$L", exprStr);
      } else {
        method.addStatement("return F.symjify($L)", exprStr);
      }

      method.nextControlFlow("finally");
      method.addStatement("top = oldTop");
      method.endControlFlow();

      factory.classBuilder.addMethod(method.build());
      parentBuffer.append("compoundExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertSet(CompileFactory factory, final StringBuilder parentBuffer,
      final IAST f) {
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
      StringBuilder numericBuffer = new StringBuilder();
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
          TypeName fieldType = type == 1 ? TypeName.DOUBLE : ClassName.get(Complex.class);
          factory.classBuilder.addField(fieldType, fieldName, Modifier.PRIVATE);
        }

        MethodSpec.Builder method = MethodSpec.methodBuilder("setExpression" + m)
            .addModifiers(Modifier.PRIVATE).returns(IExpr.class);

        method.addStatement("this.$L = $L", fieldName, numericBuffer.toString());
        String returnExpr = "F.symjify(this." + fieldName + ")";

        if (factory.localVariables.contains(variable)) {
          method.addStatement("F.eval(F.Set(vars.get($S), $L))", variable, returnExpr);
        }

        method.addStatement("return $L", returnExpr);
        factory.classBuilder.addMethod(method.build());
        parentBuffer.append("setExpression").append(m).append("()");
        return true;
      }
    }
    return false;
  }

  private static boolean convertIf(CompileFactory factory, final StringBuilder parentBuffer,
      final IAST f) {
    if (f.argSize() < 2 || f.argSize() > 3) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    try {
      int m = factory.module++;
      MethodSpec.Builder method = MethodSpec.methodBuilder("ifExpression" + m)
          .addModifiers(Modifier.PRIVATE).returns(IExpr.class);

      StringBuilder expression = new StringBuilder();
      boolean optimizedTest = factory.tryOptimizeCondition(expression, f.arg1());
      if (!optimizedTest) {
        factory.convert(expression, f.arg1(), false, true);
        method.beginControlFlow("if (engine.evalTrue($L))", expression.toString());
      } else {
        method.beginControlFlow("if ($L)", expression.toString());
      }

      expression.setLength(0);
      factory.convert(expression, f.arg2(), false, true);
      String exprStr1 = expression.toString();
      if (exprStr1.startsWith("throw ")) {
        method.addStatement("$L", exprStr1);
      } else {
        method.addStatement("return F.symjify($L)", exprStr1);
      }

      if (f.isAST3()) {
        method.nextControlFlow("else");
        expression.setLength(0);
        factory.convert(expression, f.arg3(), false, true);
        String exprStr2 = expression.toString();
        if (exprStr2.startsWith("throw ")) {
          method.addStatement("$L", exprStr2);
        } else {
          method.addStatement("return F.symjify($L)", exprStr2);
        }
        method.endControlFlow();
      } else {
        method.nextControlFlow("else");
        method.addStatement("return F.Null");
        method.endControlFlow();
      }

      factory.classBuilder.addMethod(method.build());
      parentBuffer.append("ifExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertWhich(CompileFactory factory, final StringBuilder parentBuffer,
      final IAST f) {
    if (f.argSize() < 2 || f.argSize() % 2 != 0) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    try {
      int m = factory.module++;
      MethodSpec.Builder method = MethodSpec.methodBuilder("whichExpression" + m)
          .addModifiers(Modifier.PRIVATE).returns(IExpr.class);

      for (int i = 1; i < f.argSize(); i += 2) {
        StringBuilder testExpr = new StringBuilder();
        boolean optimized = factory.tryOptimizeCondition(testExpr, f.get(i));

        String condStr =
            optimized ? testExpr.toString() : "engine.evalTrue(" + testExpr.toString() + ")";
        if (i == 1) {
          method.beginControlFlow("if ($L)", condStr);
        } else {
          method.nextControlFlow("else if ($L)", condStr);
        }

        StringBuilder valExpr = new StringBuilder();
        factory.convert(valExpr, f.get(i + 1), false, true);
        String valStr = valExpr.toString();
        if (valStr.startsWith("throw ")) {
          method.addStatement("$L", valStr);
        } else {
          method.addStatement("return F.symjify($L)", valStr);
        }
      }
      method.endControlFlow();
      method.addStatement("return F.Null");

      factory.classBuilder.addMethod(method.build());
      parentBuffer.append("whichExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertDo(CompileFactory factory, final StringBuilder parentBuffer,
      final IAST f) {
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
      MethodSpec.Builder method = MethodSpec.methodBuilder("doExpression" + m)
          .addModifiers(Modifier.PRIVATE).returns(IExpr.class);

      if (iter.argSize() == 1) {
        String iterName = "iter_" + m;
        StringBuilder imaxBuf = new StringBuilder();
        factory.convert(imaxBuf, iter.arg1(), false, true);
        method.addStatement("double $L_max = engine.evalDouble(F.symjify($L))", iterName,
            imaxBuf.toString());

        int loopId = factory.fieldCounter++;
        String loopVarName = "loop_var_" + loopId + "_d";
        factory.classBuilder.addField(TypeName.DOUBLE, loopVarName, Modifier.PRIVATE);

        method.beginControlFlow("for (this.$L = 1; this.$L <= $L_max; this.$L++)", loopVarName,
            loopVarName, iterName, loopVarName);

        method.addStatement("int oldTop = top");
        method.beginControlFlow("try");

        StringBuilder bodyExpr = new StringBuilder();
        factory.convert(bodyExpr, f.arg1(), false, true);
        String exprStr = bodyExpr.toString();
        if (exprStr.startsWith("throw ")) {
          method.addStatement("$L", exprStr);
        } else {
          method.addStatement("Object _discard_$L = $L", m, exprStr);
        }

        method.nextControlFlow("catch ($T e)",
            org.matheclipse.core.eval.exception.BreakException.class);
        method.addStatement("break");
        method.nextControlFlow("catch ($T e)",
            org.matheclipse.core.eval.exception.ContinueException.class);
        method.nextControlFlow("finally");
        method.addStatement("top = oldTop");
        method.endControlFlow(); // try
        method.endControlFlow(); // for
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

        StringBuilder iminBuf = new StringBuilder();
        factory.convert(iminBuf, imin, false, true);
        StringBuilder imaxBuf = new StringBuilder();
        factory.convert(imaxBuf, imax, false, true);
        StringBuilder stepBuf = new StringBuilder();
        factory.convert(stepBuf, step, false, true);

        String iterName = "iter_" + m;
        method.addStatement("double $L_min = engine.evalDouble(F.symjify($L))", iterName,
            iminBuf.toString());
        method.addStatement("double $L_max = engine.evalDouble(F.symjify($L))", iterName,
            imaxBuf.toString());
        method.addStatement("double $L_step = engine.evalDouble(F.symjify($L))", iterName,
            stepBuf.toString());

        int loopId = factory.fieldCounter++;
        String loopVarName = "loop_var_" + loopId + "_d";
        factory.classBuilder.addField(TypeName.DOUBLE, loopVarName, Modifier.PRIVATE);

        String loopCond = iterName + "_step > 0 ? this." + loopVarName + " <= " + iterName
            + "_max : this." + loopVarName + " >= " + iterName + "_max";

        method.beginControlFlow("for (this.$L = $L_min; $L; this.$L += $L_step)", loopVarName,
            iterName, loopCond, loopVarName, iterName);

        factory.numericVariables.put(loopVar, "this." + loopVarName);

        method.addStatement("int oldTop = top");
        method.beginControlFlow("try");

        method.addStatement("$T syncVar_$L = vars.get($S)", IExpr.class, m, loopVar.toString());
        method.beginControlFlow("if (syncVar_$L != null)", m);
        method.addStatement("F.eval(F.Set(syncVar_$L, F.symjify(this.$L)))", m, loopVarName);
        method.endControlFlow();

        StringBuilder bodyExpr = new StringBuilder();
        factory.convert(bodyExpr, f.arg1(), false, true);
        String exprStr = bodyExpr.toString();
        if (exprStr.startsWith("throw ")) {
          method.addStatement("$L", exprStr);
        } else {
          method.addStatement("Object _discard_$L = $L", m, exprStr);
        }

        method.nextControlFlow("catch ($T e)",
            org.matheclipse.core.eval.exception.BreakException.class);
        method.addStatement("break");
        method.nextControlFlow("catch ($T e)",
            org.matheclipse.core.eval.exception.ContinueException.class);
        method.nextControlFlow("finally");
        method.addStatement("top = oldTop");
        method.endControlFlow(); // try
        method.endControlFlow(); // for
      }

      method.addStatement("return F.Null");
      factory.classBuilder.addMethod(method.build());
      parentBuffer.append("doExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertWhile(CompileFactory factory, final StringBuilder parentBuffer,
      final IAST f) {
    if (f.argSize() < 1 || f.argSize() > 2) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    try {
      int m = factory.module++;
      MethodSpec.Builder method = MethodSpec.methodBuilder("whileExpression" + m)
          .addModifiers(Modifier.PRIVATE).returns(IExpr.class);

      StringBuilder testExpr = new StringBuilder();
      boolean optimizedTest = factory.tryOptimizeCondition(testExpr, f.arg1());
      if (!optimizedTest) {
        factory.convert(testExpr, f.arg1(), false, true);
        method.beginControlFlow("while (engine.evalTrue($L))", testExpr.toString());
      } else {
        method.beginControlFlow("while ($L)", testExpr.toString());
      }

      if (f.argSize() == 2) {
        method.addStatement("int oldTop = top");
        method.beginControlFlow("try");

        StringBuilder bodyExpr = new StringBuilder();
        factory.convert(bodyExpr, f.arg2(), false, true);
        String exprStr = bodyExpr.toString();
        if (exprStr.startsWith("throw ")) {
          method.addStatement("$L", exprStr);
        } else {
          method.addStatement("Object _discard_$L = $L", m, exprStr);
        }

        method.nextControlFlow("catch ($T e)",
            org.matheclipse.core.eval.exception.BreakException.class);
        method.addStatement("break");
        method.nextControlFlow("catch ($T e)",
            org.matheclipse.core.eval.exception.ContinueException.class);
        method.addStatement("continue");
        method.nextControlFlow("finally");
        method.addStatement("top = oldTop");
        method.endControlFlow();
      }
      method.endControlFlow();
      method.addStatement("return F.Null");

      factory.classBuilder.addMethod(method.build());
      parentBuffer.append("whileExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertFor(CompileFactory factory, final StringBuilder parentBuffer,
      final IAST f) {
    if (f.argSize() != 4) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    try {
      int m = factory.module++;
      MethodSpec.Builder method = MethodSpec.methodBuilder("forExpression" + m)
          .addModifiers(Modifier.PRIVATE).returns(IExpr.class);

      StringBuilder startExpr = new StringBuilder();
      factory.convert(startExpr, f.arg1(), false, true);
      String startStr = startExpr.toString();
      if (startStr.startsWith("throw ")) {
        method.addStatement("$L", startStr);
      } else {
        method.addStatement("Object _start_$L = $L", m, startStr);
      }

      StringBuilder testExpr = new StringBuilder();
      boolean optimizedTest = factory.tryOptimizeCondition(testExpr, f.arg2());
      if (!optimizedTest) {
        factory.convert(testExpr, f.arg2(), false, true);
        method.beginControlFlow("while (engine.evalTrue($L))", testExpr.toString());
      } else {
        method.beginControlFlow("while ($L)", testExpr.toString());
      }

      method.addStatement("int oldTop = top");
      method.beginControlFlow("try");

      StringBuilder bodyExpr = new StringBuilder();
      factory.convert(bodyExpr, f.arg4(), false, true);
      String bodyStr = bodyExpr.toString();
      if (bodyStr.startsWith("throw ")) {
        method.addStatement("$L", bodyStr);
      } else {
        method.addStatement("Object _discard_$L = $L", m, bodyStr);
      }

      method.nextControlFlow("catch ($T e)",
          org.matheclipse.core.eval.exception.BreakException.class);
      method.addStatement("break");
      method.nextControlFlow("catch ($T e)",
          org.matheclipse.core.eval.exception.ContinueException.class);
      method.nextControlFlow("finally");
      method.addStatement("top = oldTop");
      method.endControlFlow();

      StringBuilder incrExpr = new StringBuilder();
      factory.convert(incrExpr, f.arg3(), false, true);
      String incrStr = incrExpr.toString();
      if (incrStr.startsWith("throw ")) {
        method.addStatement("$L", incrStr);
      } else {
        method.addStatement("Object _incr_$L = $L", m, incrStr);
      }

      method.endControlFlow();
      method.addStatement("return F.Null");

      factory.classBuilder.addMethod(method.build());
      parentBuffer.append("forExpression").append(m).append("()");
    } finally {
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertScope(CompileFactory factory, final StringBuilder parentBuffer,
      final IAST f) {
    if (f.argSize() != 2 || !f.arg1().isList()) {
      return false;
    }
    factory.variables.push();
    factory.numericVariables.push();
    HashSet<String> oldLocalVariables = factory.localVariables;
    try {
      HashSet<String> localVariables = new HashSet<>(factory.localVariables);
      factory.localVariables = localVariables;
      IAST variableList = (IAST) f.arg1();
      int m = factory.module++;
      MethodSpec.Builder method = MethodSpec.methodBuilder("scopeExpression" + m)
          .addModifiers(Modifier.PRIVATE).returns(IExpr.class);

      method.addStatement("$T oldVars = vars", org.matheclipse.core.expression.ExprTrie.class);
      method.addStatement("int oldTop = top");
      method.beginControlFlow("try");
      method.addStatement("vars = vars.copy()");

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
        method.addStatement("$T $L = F.Dummy($S)", ISymbol.class, symbolName, symbolName);
        method.addStatement("vars.put($S, $L)", symbolName, symbolName);

        if (arg.isAST(S.Set, 3)) {
          boolean isNumericInit = false;
          CompileAnalyzer.VarType inferredType =
              factory.nodeTypes.getOrDefault(arg.second(), CompileAnalyzer.VarType.UNKNOWN);

          if (inferredType == CompileAnalyzer.VarType.REAL
              || inferredType == CompileAnalyzer.VarType.INTEGER
              || arg.second().isNumericFunction(factory.numericVariables)) {
            StringBuilder numericBuffer = new StringBuilder();
            int type = factory.convertNumeric(numericBuffer, arg.second(), factory.domain);
            if (type > 0) {
              int fieldId = factory.fieldCounter++;
              String fieldName = "local_var_" + fieldId + "_" + (type == 1 ? "d" : "c");
              TypeName fieldType = type == 1 ? TypeName.DOUBLE : ClassName.get(Complex.class);
              factory.classBuilder.addField(fieldType, fieldName, Modifier.PRIVATE);
              factory.numericVariables.put(arg.first(), "this." + fieldName);

              method.addStatement("this.$L = $L", fieldName, numericBuffer.toString());
              String returnExpr = "F.symjify(this." + fieldName + ")";
              method.addStatement("F.eval(F.Set($L, $L))", symbolName, returnExpr);
              isNumericInit = true;
            }
          }

          if (!isNumericInit) {
            StringBuilder expressions = new StringBuilder();
            factory.convert(expressions, arg.second(), true, false);
            method.addStatement("F.eval(F.Set($L, $L))", symbolName, expressions.toString());
          }
        }
      }

      StringBuilder expressions = new StringBuilder();
      factory.convert(expressions, f.arg2(), false, true);
      String exprStr = expressions.toString();
      if (exprStr.startsWith("throw ")) {
        method.addStatement("$L", exprStr);
      } else {
        method.addStatement("return F.symjify($L)", exprStr);
      }

      method.nextControlFlow("finally");
      method.addStatement("top = oldTop");
      method.addStatement("vars = oldVars");
      method.endControlFlow();

      factory.classBuilder.addMethod(method.build());
      parentBuffer.append("scopeExpression").append(m).append("()");
    } finally {
      factory.localVariables = oldLocalVariables;
      factory.variables.pop();
      factory.numericVariables.pop();
    }
    return true;
  }

  private static boolean convertPart(CompileFactory factory, final StringBuilder parentBuffer,
      final IAST f) {
    if (f.argSize() < 2) {
      return false;
    }
    IExpr head = f.arg1();
    String arrName = factory.numericVariables.apply(head);
    if (arrName != null && arrName.startsWith("this.")) {
      CompiledFunctionArg argInfo = null;
      for (CompiledFunctionArg a : factory.args) {
        if (a.argument.equals(head)) {
          argInfo = a;
          break;
        }
      }
      if (argInfo != null && argInfo.rank != CompiledFunctionArg.Rank.SCALAR) {
        StringBuilder sb = new StringBuilder(arrName);
        for (int i = 2; i <= f.argSize(); i++) {
          StringBuilder idxBuf = new StringBuilder();
          factory.convert(idxBuf, f.get(i), false, true);
          sb.append("[(int)(").append(idxBuf).append(") - 1]");
        }
        parentBuffer.append(sb);
        return true;
      }
    }
    return false;
  }

  private static boolean convertLength(CompileFactory factory, final StringBuilder parentBuffer,
      final IAST f) {
    if (f.argSize() != 1) {
      return false;
    }
    IExpr head = f.arg1();
    String arrName = factory.numericVariables.apply(head);
    if (arrName != null && arrName.startsWith("this.")) {
      CompiledFunctionArg argInfo = null;
      for (CompiledFunctionArg a : factory.args) {
        if (a.argument.equals(head)) {
          argInfo = a;
          break;
        }
      }
      if (argInfo != null && argInfo.rank != CompiledFunctionArg.Rank.SCALAR) {
        parentBuffer.append(arrName).append(".length");
        return true;
      }
    }
    return false;
  }

  public void convert(StringBuilder buf, IExpr expression, boolean symbolic, boolean addEval) {
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
        StringBuilder sb = new StringBuilder();

        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.CompoundExpression:
            converted = convertCompoundExpression(this, sb, ast);
            break;
          case ID.If:
            converted = convertIf(this, sb, ast);
            break;
          case ID.Which:
            converted = convertWhich(this, sb, ast);
            break;
          case ID.Set:
          case ID.SetDelayed:
            converted = convertSet(this, sb, ast);
            break;
          case ID.Module:
          case ID.Block:
          case ID.With:
            converted = convertScope(this, sb, ast);
            break;
          case ID.While:
            converted = convertWhile(this, sb, ast);
            break;
          case ID.Do:
            converted = convertDo(this, sb, ast);
            break;
          case ID.For:
            converted = convertFor(this, sb, ast);
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
            if (ast.argSize() == 1) {
              StringBuilder tempBuf = new StringBuilder();
              convert(tempBuf, ast.arg1(), symbolic, addEval);
              String tStr = tempBuf.toString();
              if (tStr.startsWith("throw ")) {
                sb.append(tStr);
              } else {
                sb.append(
                    "throw new org.matheclipse.core.eval.exception.ReturnException(F.symjify(")
                    .append(tStr).append("))");
              }
            } else {
              sb.append("throw new org.matheclipse.core.eval.exception.ReturnException(F.Null)");
            }
            converted = true;
            break;
          case ID.Part:
            converted = convertPart(this, sb, ast);
            break;
          case ID.Length:
            converted = convertLength(this, sb, ast);
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
          StringBuilder leftBuf = new StringBuilder();
          StringBuilder rightBuf = new StringBuilder();
          JavaDoubleFormFactory doubleFactory = JavaDoubleFormFactory.get(true, false);

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

  private IExpr prepareForNumeric(IExpr expr) {
    if (expr.isAST(S.Part) && expr.argSize() >= 2) {
      IAST part = (IAST) expr;
      IExpr head = part.arg1();
      String arrName = numericVariables.apply(head);
      if (arrName != null && arrName.startsWith("this.")) {
        CompiledFunctionArg argInfo = null;
        for (CompiledFunctionArg a : args) {
          if (a.argument.equals(head)) {
            argInfo = a;
            break;
          }
        }
        if (argInfo != null && argInfo.rank != CompiledFunctionArg.Rank.SCALAR) {
          StringBuilder sb = new StringBuilder(arrName);
          for (int i = 2; i <= part.argSize(); i++) {
            StringBuilder idxBuf = new StringBuilder();
            convert(idxBuf, part.get(i), false, true);
            sb.append("[(int)(").append(idxBuf).append(") - 1]");
          }
          return F.stringx(sb.toString());
        }
      }
    }
    if (expr.isAST(S.Length) && expr.argSize() == 1) {
      IExpr head = ((IAST) expr).arg1();
      String arrName = numericVariables.apply(head);
      if (arrName != null && arrName.startsWith("this.")) {
        CompiledFunctionArg argInfo = null;
        for (CompiledFunctionArg a : args) {
          if (a.argument.equals(head)) {
            argInfo = a;
            break;
          }
        }
        if (argInfo != null && argInfo.rank != CompiledFunctionArg.Rank.SCALAR) {
          return F.stringx(arrName + ".length");
        }
      }
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IASTAppendable result = F.ast(ast.head(), ast.argSize());
      for (int i = 1; i <= ast.argSize(); i++) {
        result.append(prepareForNumeric(ast.get(i)));
      }
      return result;
    }
    if (expr.isSymbol()) {
      for (CompiledFunctionArg a : args) {
        if (a.argument.equals(expr) && a.rank != CompiledFunctionArg.Rank.SCALAR) {
          throw new IllegalArgumentException(
              "Matrix cannot be used directly as a primitive scalar.");
        }
      }
    }
    return expr;
  }

  private int convertNumeric(StringBuilder parentBuffer, IExpr expression, IBuiltInSymbol domain) {
    if (domain == S.Reals) {
      try {
        StringBuilder buf = new StringBuilder();
        JavaDoubleFormFactory factory = JavaDoubleFormFactory.get(true, false);
        IExpr preprocessed = prepareForNumeric(expression);
        IExpr substituted = F.subst(preprocessed, getNumericSubstFunction("evalf"));
        factory.convert(buf, substituted);
        parentBuffer.append(buf);
        return 1;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
    }
    try {
      StringBuilder buf = new StringBuilder();
      JavaComplexFormFactory factory = JavaComplexFormFactory.get(true, false, -1, -1, true);
      IExpr preprocessed = prepareForNumeric(expression);
      IExpr substituted = F.subst(preprocessed, getNumericSubstFunction("evalComplex"));
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
        return F.stringx(str);
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
}