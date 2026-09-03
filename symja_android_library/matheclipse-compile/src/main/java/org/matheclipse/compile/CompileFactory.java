package org.matheclipse.compile;

import java.util.Collections;
import java.util.HashMap;
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
import org.matheclipse.core.interfaces.IASTMutable;
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
  /** Names the symbols which stand for the statements lifted out of a symbolic expression. */
  private int statementCounter = 1;
  private final IBuiltInSymbol domain;
  private HashSet<String> localVariables;
  final VariableManager numericVariables;
  private final VariableManager variables;
  final CompiledFunctionArg[] args;
  final Map<IExpr, CompileAnalyzer.VarType> nodeTypes;
  private final TypeSpec.Builder classBuilder;
  private final IntegerFormWriter integerWriter;

  /**
   * The field which holds each constant {@link ConstantHoisting} lifted out of the expression,
   * keyed by the symbol which replaced it.
   */
  private final Map<ISymbol, String> constantFields;

  /** The primitive array a lifted constant table can also be read through, keyed by its symbol. */
  private final Map<ISymbol, ConstantArray> constantArrays;

  /** The name and rank of the primitive array which holds a lifted constant table. */
  public static final class ConstantArray {
    final String field;
    final int rank;

    public ConstantArray(String field, int rank) {
      this.field = field;
      this.rank = rank;
    }
  }

  public CompileFactory(VariableManager numericVariables, VariableManager variables,
      CompiledFunctionArg[] args, IBuiltInSymbol domain,
      Map<IExpr, CompileAnalyzer.VarType> nodeTypes, TypeSpec.Builder classBuilder) {
    this(numericVariables, variables, args, domain, nodeTypes, classBuilder, Collections.emptyMap(),
        Collections.emptyMap(), RuntimeOptions.DEFAULT);
  }

  public CompileFactory(VariableManager numericVariables, VariableManager variables,
      CompiledFunctionArg[] args, IBuiltInSymbol domain,
      Map<IExpr, CompileAnalyzer.VarType> nodeTypes, TypeSpec.Builder classBuilder,
      Map<ISymbol, String> constantFields, Map<ISymbol, ConstantArray> constantArrays) {
    this(numericVariables, variables, args, domain, nodeTypes, classBuilder, constantFields,
        constantArrays, RuntimeOptions.DEFAULT);
  }

  public CompileFactory(VariableManager numericVariables, VariableManager variables,
      CompiledFunctionArg[] args, IBuiltInSymbol domain,
      Map<IExpr, CompileAnalyzer.VarType> nodeTypes, TypeSpec.Builder classBuilder,
      Map<ISymbol, String> constantFields, Map<ISymbol, ConstantArray> constantArrays,
      RuntimeOptions runtimeOptions) {
    this.localVariables = new HashSet<>();
    this.numericVariables = numericVariables;
    this.variables = variables;
    this.args = args;
    this.domain = domain;
    this.nodeTypes = nodeTypes;
    this.classBuilder = classBuilder;
    this.constantFields = constantFields;
    this.constantArrays = constantArrays;
    this.integerWriter = new IntegerFormWriter(this, runtimeOptions.isCatchMachineIntegerOverflow());
  }

  /**
   * The Java source which reads <code>expression</code> out of the primitive array of a lifted
   * constant table, or <code>null</code> if it is not such a read.
   *
   * <p>
   * Both <code>t[[i, j]]</code> and <code>t[[i]][[j]]</code> are accepted; the indices are counted
   * from the outside in and have to add up to the rank of the array, so that what comes out is a
   * number rather than a row of one.
   */
  private String constantArrayAccess(IExpr expression) {
    if (constantArrays.isEmpty() || !expression.isAST(S.Part) || expression.argSize() < 2) {
      return null;
    }
    java.util.List<IExpr> indices = new java.util.ArrayList<>();
    IExpr current = expression;
    while (current.isAST(S.Part) && current.argSize() >= 2) {
      IAST part = (IAST) current;
      for (int i = part.argSize(); i >= 2; i--) {
        indices.add(0, part.get(i));
      }
      current = part.arg1();
    }
    if (!current.isSymbol()) {
      return null;
    }
    ConstantArray array = constantArrays.get(current);
    if (array == null || indices.size() != array.rank) {
      return null;
    }

    StringBuilder buf = new StringBuilder(array.field);
    for (IExpr index : indices) {
      StringBuilder indexBuffer = new StringBuilder();
      int indexType = convertNumeric(indexBuffer, index, S.Reals);
      if (indexType != 1 && indexType != 3) {
        return null;
      }
      buf.append("[(int)(").append(indexBuffer).append(") - 1]");
    }
    return buf.toString();
  }

  /**
   * Whether <code>expression</code> reads a lifted constant table anywhere inside it.
   *
   * <p>
   * Such a read is a number, but nothing in <code>isNumericFunction</code> knows that, so an
   * expression containing one is never recognized as numeric and never offered to the numeric
   * converter. This says when it is worth offering anyway; the converter still answers for itself.
   */
  private boolean containsConstantArrayAccess(IExpr expression) {
    if (constantArrays.isEmpty() || !expression.isAST()) {
      return false;
    }
    if (constantArrayAccess(expression) != null) {
      return true;
    }
    IAST ast = (IAST) expression;
    for (int i = 1; i < ast.size(); i++) {
      if (containsConstantArrayAccess(ast.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether <code>expression</code> is, or contains, a list literal.
   *
   * <p>
   * <code>isNumericFunction</code> treats every <code>List</code> as numeric regardless of its
   * elements, so a literal list reaches the numeric converter, which has no idea what to do with
   * one: it writes <code>F.List.ofN(...)</code> or <code>F.Clip.ofN(...)</code>, a call that
   * throws at run time because <code>ofN</code> only ever answers with a single number. The
   * expression is still handled correctly - the runtime failure falls back to the uncompiled
   * evaluation - but only after printing a spurious "numerical error" message. Keeping a list out
   * of the numeric converter's hands in the first place is what avoids that.
   */
  private static boolean containsList(IExpr expression) {
    if (expression.isList()) {
      return true;
    }
    if (!expression.isAST()) {
      return false;
    }
    IAST ast = (IAST) expression;
    for (int i = 1; i < ast.size(); i++) {
      if (containsList(ast.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether <code>expression</code> is, or contains, the imaginary unit or a non-real numeric
   * literal.
   *
   * <p>
   * The double emitter has no notion of a complex number: an expression which is complex-valued
   * despite every argument of the compiled function being real - <code>x + I</code>, a
   * <code>Module</code> which builds a complex number out of two real arguments - reaches it
   * anyway, because nothing about <code>isNumericFunction</code> depends on the compiled
   * function's domain. It then writes the imaginary unit as a bare Java identifier, which Janino
   * cannot resolve. Handing such an expression to the symbolic fallback instead lets ordinary
   * complex arithmetic compute the right answer.
   */
  private static boolean containsImaginary(IExpr expression) {
    if (expression == S.I || expression.isComplex() || expression.isComplexNumeric()) {
      return true;
    }
    if (!expression.isAST()) {
      return false;
    }
    IAST ast = (IAST) expression;
    for (int i = 1; i < ast.size(); i++) {
      if (containsImaginary(ast.get(i))) {
        return true;
      }
    }
    return false;
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

  /**
   * The field-name suffix a numeric field of {@link #convertNumeric}'s result <code>type</code>
   * gets: <code>1</code> (double) -&gt; <code>"d"</code>, <code>2</code> (complex) -&gt;
   * <code>"c"</code>, <code>3</code> (exact long) -&gt; <code>"l"</code>.
   */
  private static String fieldSuffix(int type) {
    return type == 1 ? "d" : type == 3 ? "l" : "c";
  }

  /** The Java type of a numeric field of {@link #convertNumeric}'s result <code>type</code>. */
  private static TypeName fieldType(int type) {
    if (type == 1) {
      return TypeName.DOUBLE;
    }
    if (type == 3) {
      return TypeName.LONG;
    }
    return ClassName.get(Complex.class);
  }

  private static boolean convertSet(CompileFactory factory, final StringBuilder parentBuffer,
      final IAST f) {
    if (f.argSize() != 2 || !f.arg1().isVariable()) {
      return false;
    }
    String variable = f.arg1().toString();

    CompileAnalyzer.VarType inferredType =
        factory.nodeTypes.getOrDefault(f.arg2(), CompileAnalyzer.VarType.UNKNOWN);
    boolean isNumericRHS = !containsList(f.arg2())
        && !(factory.domain == S.Reals && containsImaginary(f.arg2()))
        && (inferredType == CompileAnalyzer.VarType.REAL
            || inferredType == CompileAnalyzer.VarType.INTEGER
            || f.arg2().isNumericFunction(factory.numericVariables)
            || factory.containsConstantArrayAccess(f.arg2()));

    if (isNumericRHS) {
      StringBuilder numericBuffer = new StringBuilder();
      int type = factory.convertNumeric(numericBuffer, f.arg2(), factory.domain);
      if (type > 0) {
        int m = factory.module++;
        String existingVar = factory.numericVariables.apply(f.arg1());
        String fieldName;

        String rhs = numericBuffer.toString();
        if (existingVar != null && existingVar.startsWith("this.")) {
          fieldName = existingVar.substring(5);
          // an existing field's type is fixed by whatever assignment or initializer created it;
          // this assignment's own RHS may have resolved to a different one - most commonly a
          // scalar `_Integer` argument's `int` field fed by this writer's exact `long` arithmetic
          // - and Java only widens a narrower Java type into a wider one implicitly. A `long`
          // into an existing `int` field needs an explicit (lossy, but no more so than the `int`
          // arithmetic the field already commits it to) narrowing cast; a `double` into an
          // existing `long` field is the same case in the other direction.
          if (type == 3 && fieldName.endsWith("_i")) {
            rhs = "(int)(" + rhs + ")";
          } else if (type == 1 && fieldName.endsWith("_l")) {
            rhs = "(long)(" + rhs + ")";
          }
        } else {
          int fieldId = factory.fieldCounter++;
          fieldName = "local_var_" + fieldId + "_" + fieldSuffix(type);
          factory.numericVariables.put(f.arg1(), "this." + fieldName);
          factory.classBuilder.addField(fieldType(type), fieldName, Modifier.PRIVATE);
        }

        MethodSpec.Builder method = MethodSpec.methodBuilder("setExpression" + m)
            .addModifiers(Modifier.PRIVATE).returns(IExpr.class);

        method.addStatement("this.$L = $L", fieldName, rhs);
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

    return convertSymbolicSet(factory, parentBuffer, f, variable);
  }

  /**
   * Generate an assignment whose right hand side is not a number.
   *
   * <p>
   * A variable which was assigned a number once is read back out of the numeric field the code
   * generator gave it, so an assignment which only updates the <code>ExprTrie</code> entry - which
   * is what the symbolic fallback does - leaves the two disagreeing and every later read of the
   * variable sees the value it had before. Assigning the field as well is what keeps them in step.
   *
   * <p>
   * The value has to be a number for that: <code>evalDouble</code> throws an
   * {@link org.matheclipse.core.eval.exception.ArgumentTypeException} if it is not, which
   * <code>CompiledFunction</code> catches and answers by evaluating the uncompiled expression
   * instead. A slower right answer beats the silently wrong one.
   */
  private static boolean convertSymbolicSet(CompileFactory factory,
      final StringBuilder parentBuffer, final IAST f, String variable) {
    StringBuilder valueBuffer = new StringBuilder();
    factory.convert(valueBuffer, f.arg2(), false, true);
    String value = valueBuffer.toString();
    if (value.startsWith("throw ")) {
      return false;
    }

    int m = factory.module++;
    MethodSpec.Builder method = MethodSpec.methodBuilder("setExpression" + m)
        .addModifiers(Modifier.PRIVATE).returns(IExpr.class);
    method.addStatement("$T value = $L", IExpr.class, value);

    if (factory.localVariables.contains(variable)) {
      method.addStatement("F.eval(F.Set(vars.get($S), value))", variable);
    }

    String numericField = factory.numericVariables.apply(f.arg1());
    if (numericField != null && numericField.startsWith("this.")) {
      // convertSet names the field after the type it created it for
      String syncStatement = numericField.endsWith("_c") ? "$L = engine.evalComplex(value)"
          : numericField.endsWith("_l") ? "$L = (long) engine.evalInt(value)"
              : "$L = engine.evalDouble(value)";
      method.addStatement(syncStatement, numericField);
    }

    method.addStatement("return value");
    factory.classBuilder.addMethod(method.build());
    parentBuffer.append("setExpression").append(m).append("()");
    return true;
  }

  /**
   * Generate <code>x++</code> or <code>x--</code> for a variable which already has a numeric field.
   *
   * <p>
   * Unlike the compound assignments, which {@link CompoundAssignment} rewrites into a
   * <code>Set</code> before compilation starts, these two return the value the variable held
   * <i>before</i> the assignment. No arithmetic wrapped around a <code>Set</code> expresses that
   * and stays numeric - the enclosing expression would fall to the symbolic fallback and take the
   * assignment with it - so the old value is kept in a local of the generated method instead.
   *
   * <p>
   * A variable which has no numeric field yet is left to the caller's fallback: there is nothing to
   * increment, since the field is created by the first assignment to the variable.
   *
   * @param increment <code>true</code> for <code>Increment</code>, <code>false</code> for
   *        <code>Decrement</code>
   */
  private static boolean convertIncrement(CompileFactory factory, final StringBuilder parentBuffer,
      final IAST f, boolean increment) {
    if (f.argSize() != 1 || !f.arg1().isVariable()) {
      return false;
    }
    String fieldReference = factory.numericVariables.apply(f.arg1());
    if (fieldReference == null || !fieldReference.startsWith("this.")) {
      return false;
    }
    String fieldName = fieldReference.substring(5);
    // convertSet names the field after the type it created it for
    boolean complex = fieldName.endsWith("_c");
    boolean exactInteger = fieldName.endsWith("_l");
    if (!complex && !exactInteger && !fieldName.endsWith("_d")) {
      return false;
    }

    String variable = f.arg1().toString();
    int m = factory.module++;
    MethodSpec.Builder method = MethodSpec.methodBuilder("incrementExpression" + m)
        .addModifiers(Modifier.PRIVATE).returns(IExpr.class);

    if (complex) {
      method.addStatement("$T previous = this.$L", Complex.class, fieldName);
      method.addStatement("this.$L = previous.$L(1.0)", fieldName,
          increment ? "add" : "subtract");
    } else if (exactInteger) {
      method.addStatement("long previous = this.$L", fieldName);
      if (factory.integerWriter.isChecked()) {
        method.addStatement("this.$L = Math.$L(previous, 1L)", fieldName,
            increment ? "addExact" : "subtractExact");
      } else {
        method.addStatement("this.$L = previous $L 1L", fieldName, increment ? "+" : "-");
      }
    } else {
      method.addStatement("double previous = this.$L", fieldName);
      method.addStatement("this.$L = previous $L 1.0", fieldName, increment ? "+" : "-");
    }

    if (factory.localVariables.contains(variable)) {
      method.addStatement("F.eval(F.Set(vars.get($S), F.symjify(this.$L)))", variable, fieldName);
    }

    method.addStatement("return F.symjify(previous)");
    factory.classBuilder.addMethod(method.build());
    parentBuffer.append("incrementExpression").append(m).append("()");
    return true;
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
        if (!optimized) {
          factory.convert(testExpr, f.get(i), false, true);
        }

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

        // an exact `long` loop variable is only worth it when every bound writes as one; a bound
        // this writer declines (a symbolic or genuinely real one) falls back to the existing
        // `double` loop unchanged
        String iminLong = factory.integerWriter.write(imin);
        String imaxLong = factory.integerWriter.write(imax);
        String stepLong = factory.integerWriter.write(step);
        boolean integerLoop = iminLong != null && imaxLong != null && stepLong != null;

        String iterName = "iter_" + m;
        if (integerLoop) {
          method.addStatement("long $L_min = $L", iterName, iminLong);
          method.addStatement("long $L_max = $L", iterName, imaxLong);
          method.addStatement("long $L_step = $L", iterName, stepLong);
        } else {
          StringBuilder iminBuf = new StringBuilder();
          factory.convert(iminBuf, imin, false, true);
          StringBuilder imaxBuf = new StringBuilder();
          factory.convert(imaxBuf, imax, false, true);
          StringBuilder stepBuf = new StringBuilder();
          factory.convert(stepBuf, step, false, true);

          method.addStatement("double $L_min = engine.evalDouble(F.symjify($L))", iterName,
              iminBuf.toString());
          method.addStatement("double $L_max = engine.evalDouble(F.symjify($L))", iterName,
              imaxBuf.toString());
          method.addStatement("double $L_step = engine.evalDouble(F.symjify($L))", iterName,
              stepBuf.toString());
        }

        int loopId = factory.fieldCounter++;
        String loopVarName = "loop_var_" + loopId + "_" + (integerLoop ? "l" : "d");
        factory.classBuilder.addField(integerLoop ? TypeName.LONG : TypeName.DOUBLE, loopVarName,
            Modifier.PRIVATE);

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

          if (!containsList(arg.second())
              && !(factory.domain == S.Reals && containsImaginary(arg.second()))
              && (inferredType == CompileAnalyzer.VarType.REAL
                  || inferredType == CompileAnalyzer.VarType.INTEGER
                  || arg.second().isNumericFunction(factory.numericVariables)
                  || factory.containsConstantArrayAccess(arg.second()))) {
            StringBuilder numericBuffer = new StringBuilder();
            int type = factory.convertNumeric(numericBuffer, arg.second(), factory.domain);
            if (type > 0) {
              int fieldId = factory.fieldCounter++;
              String fieldName = "local_var_" + fieldId + "_" + fieldSuffix(type);
              factory.classBuilder.addField(fieldType(type), fieldName, Modifier.PRIVATE);
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
    if (!symbolic && !containsList(expression) && !(domain == S.Reals && containsImaginary(expression))
        && (expression.isNumericFunction(numericVariables) || containsConstantArrayAccess(expression))) {
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
          case ID.Increment:
          case ID.Decrement:
            converted = convertIncrement(this, sb, ast,
                ((IBuiltInSymbol) head).ordinal() == ID.Increment);
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
    String constantAccess = constantArrayAccess(expr);
    if (constantAccess != null) {
      return F.stringx(constantAccess);
    }
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

  /**
   * Whether <code>expression</code> contains a statement anywhere inside it.
   *
   * <p>
   * The type inference gives an assignment the type of its right hand side, so an expression which
   * ends in one looks numeric even though the numeric converter has no idea what to do with it: it
   * writes <code>Set</code> out as though it were arithmetic, and the generated code then tries to
   * assign to the <i>value</i> of the variable rather than to the variable. Such an expression has
   * to go the other way, where {@link #hoistStatements} turns each statement into a call.
   */
  private static boolean containsStatement(IExpr expression) {
    if (!expression.isAST()) {
      return false;
    }
    IAST ast = (IAST) expression;
    if (isNonNumeric(ast)) {
      return true;
    }
    for (int i = 1; i < ast.size(); i++) {
      if (containsStatement(ast.get(i))) {
        return true;
      }
    }
    return false;
  }

  private int convertNumeric(StringBuilder parentBuffer, IExpr expression, IBuiltInSymbol domain) {
    if (containsStatement(expression)) {
      return 0;
    }
    if (domain == S.Reals && nodeTypes.get(expression) == CompileAnalyzer.VarType.INTEGER) {
      // an expression the analyzer has proven exactly integer-valued is computed as an exact
      // `long` when this writer has a case for it; anything it declines falls through to the
      // double emitter below exactly as if this branch were not here
      String longExpr = integerWriter.write(expression);
      if (longExpr != null) {
        parentBuffer.append(longExpr);
        return 3;
      }
    }
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
    boolean forDouble = evalMethod.equals("evalf");
    return x -> {
      String str = numericVariables.apply(x);
      if (x.isSymbol() && str != null) {
        if (forDouble && (str.endsWith("_i") || str.endsWith("_l"))) {
          // a scalar `_Integer` argument or an exact-integer local is a Java `int`/`long` field;
          // substituting its bare name here would let its *declared* Java type - not the domain
          // this conversion is for - decide how the surrounding operator behaves. That is
          // invisible for `+`/`*`/`-`, where the other operand (or an implicit widening) usually
          // saves it, but not for `/`: two `int`/`long` operands do integer division in Java
          // regardless of what the analyzer inferred, so `n/2` silently truncated instead of
          // computing the real quotient real `Compile` always gives. The cast forces this
          // conversion's own domain throughout.
          str = "((double)(" + str + "))";
        }
        return F.stringx(str);
      }
      return F.NIL;
    };
  }

  /**
   * Whether an expression with this head has to be generated as a method rather than written into
   * the symbolic form of an enclosing expression.
   *
   * <p>
   * A scope declares local variables, which the symbolic form writes as Java identifiers that
   * nothing in the generated class declares. An assignment has to reach the numeric field of the
   * variable, which only the generated form does.
   *
   * <p>
   * The control flow heads are deliberately not here. Their symbolic form is valid - they declare
   * nothing and assign nothing - and generating them instead would compile their branches to
   * machine arithmetic, which answers some edge cases differently from the symbolic evaluation
   * they get today. A scope or an assignment nested inside one is still lifted, by the walk in
   * {@link #hoistStatements}.
   */
  private static boolean isStatement(IAST ast) {
    IExpr head = ast.head();
    if (!head.isBuiltInSymbol()) {
      return false;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.Set:
      case ID.SetDelayed:
      case ID.Module:
      case ID.Block:
      case ID.With:
        return true;
      default:
        return false;
    }
  }

  /**
   * Whether an expression with this head is something the numeric converter must not be handed.
   *
   * <p>
   * This is {@link #isStatement} plus the control flow heads: those do not have to be generated as
   * methods, but they are not arithmetic either, and the numeric converter writes whatever it is
   * given as though it were.
   */
  private static boolean isNonNumeric(IAST ast) {
    if (isStatement(ast)) {
      return true;
    }
    IExpr head = ast.head();
    if (!head.isBuiltInSymbol()) {
      return false;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.CompoundExpression:
      case ID.If:
      case ID.Which:
      case ID.While:
      case ID.Do:
      case ID.For:
      case ID.Break:
      case ID.Continue:
      case ID.Return:
        return true;
      default:
        return false;
    }
  }

  /**
   * Whether argument <code>argIndex</code> (1-based) of <code>parent</code> is held - not
   * evaluated when <code>parent</code> itself is, so an assignment nested inside it must not be
   * hoisted into a once-called method: Symja's own evaluator - once the whole symbolic expression
   * this walk is building is handed to <code>F.eval</code> - is what decides how many times, and
   * in which order, it actually runs. <code>Table</code>'s body runs once per iteration, an
   * <code>If</code>'s branch only if its condition picks it, a <code>Function</code>'s body once
   * per application; a once-called Java method could only ever give one of those the wrong answer.
   *
   * <p>
   * This reads the head's actual <code>HoldFirst</code>/<code>HoldRest</code> attributes rather
   * than naming heads, so it is automatically right about every one of them - <code>Table</code>,
   * <code>Sum</code>, <code>Product</code>, <code>Map</code>'s <code>Function</code> argument,
   * <code>Function</code> itself (its body is held exactly like everything else <code>HOLDALL</code>
   * covers) - and about a nested <code>If</code>/<code>Which</code>/<code>Do</code>/<code>While</code>/
   * <code>For</code> reached this way only because something else in the same expression (not
   * that head itself) is what fell to the symbolic fallback: those are compiled natively, with
   * correct held semantics of their own, whenever they are the expression a {@link #convert} call
   * is actually asked to handle - this is only reached when they are not.
   */
  private static boolean isHeldPosition(IAST parent, int argIndex) {
    IExpr head = parent.head();
    if (!head.isSymbol()) {
      return false;
    }
    int attributes = ((ISymbol) head).getAttributes();
    return argIndex == 1 ? (attributes & ISymbol.HOLDFIRST) != 0
        : (attributes & ISymbol.HOLDREST) != 0;
  }

  /**
   * Record the variable a held <code>Set</code>/<code>SetDelayed</code>/<code>Increment</code>/
   * <code>Decrement</code> assigns, so its numeric field - if it has one - can be resynced from
   * <code>vars</code> once the whole symbolic expression containing it has actually run. Does
   * nothing for anything else; safe to call unconditionally on every held argument.
   */
  private static void recordHeldAssignment(IAST ast, java.util.Set<ISymbol> heldAssigned) {
    IExpr head = ast.head();
    if (!head.isBuiltInSymbol()) {
      return;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.Set:
      case ID.SetDelayed:
        if (ast.argSize() == 2 && ast.arg1().isSymbol()) {
          heldAssigned.add((ISymbol) ast.arg1());
        }
        break;
      case ID.Increment:
      case ID.Decrement:
        if (ast.argSize() == 1 && ast.arg1().isSymbol()) {
          heldAssigned.add((ISymbol) ast.arg1());
        }
        break;
      default:
        break;
    }
  }

  /**
   * Replace the statements nested inside <code>expression</code> by symbols standing for the calls
   * which generate them.
   *
   * <p>
   * The symbolic form of an expression is written by {@link SymbolicFormWriter}, which knows how
   * to write a <code>Module</code> as <code>F.Module(F.List(x, y), ...)</code> - and the
   * <code>x</code> and <code>y</code> in it are local variables of the compiled function which no
   * Java declaration in the generated class matches. Handing those subexpressions to
   * {@link #convert} instead, and putting the call it returns in their place, keeps them out of the
   * symbolic form altogether - <i>except</i> inside a {@link #isHeldPosition held} argument, where
   * hoisting would run the statement once, eagerly, while the enclosing call's Java arguments are
   * being built, instead of however many times (or in whichever branch) the head that holds it
   * actually calls for. <code>held</code> is sticky once set: everything nested inside a held
   * position is itself left to the same one <code>F.eval</code> that runs the held position,
   * whatever further statements or scopes it contains.
   *
   * <p>
   * Only a nested statement is lifted, never <code>expression</code> itself: this runs from inside
   * {@link #convert}, so lifting the whole expression would hand it straight back.
   */
  private IExpr hoistStatements(IExpr expression, Map<ISymbol, String> generated, boolean held,
      java.util.Set<ISymbol> heldAssigned) {
    if (!expression.isAST()) {
      return expression;
    }
    IAST ast = (IAST) expression;
    IASTMutable result = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr argument = ast.get(i);
      boolean argHeld = held || isHeldPosition(ast, i);
      IExpr replaced = hoistArgument(argument, generated, argHeld, heldAssigned);
      if (replaced != argument) {
        if (result.isNIL()) {
          result = ast.copy();
        }
        result.set(i, replaced);
      }
    }
    return result.isPresent() ? result : ast;
  }

  private IExpr hoistArgument(IExpr argument, Map<ISymbol, String> generated, boolean held,
      java.util.Set<ISymbol> heldAssigned) {
    if (!argument.isAST()) {
      return argument;
    }
    IAST argAst = (IAST) argument;
    if (held) {
      recordHeldAssignment(argAst, heldAssigned);
      return hoistStatements(argument, generated, true, heldAssigned);
    }
    if (!isStatement(argAst)) {
      return hoistStatements(argument, generated, false, heldAssigned);
    }
    StringBuilder buf = new StringBuilder();
    convert(buf, argument, false, false);
    String source = buf.toString();
    if (source.startsWith("throw ")) {
      return argument;
    }
    ISymbol placeholder = F.Dummy("statement$" + statementCounter++);
    generated.put(placeholder, source);
    return placeholder;
  }

  /**
   * Generate a method that runs <code>symbolicText</code> - already-built, <code>F.eval</code>-
   * ready source - exactly once and then resyncs the numeric field of every variable in
   * <code>heldAssigned</code> from <code>vars</code>, and return a call to it.
   *
   * <p>
   * A variable assigned inside a held position - a <code>Table</code>'s body, an <code>If</code>'s
   * branch, a <code>Function</code>'s body - is deliberately left un-hoisted by
   * {@link #hoistArgument}, so its <code>vars</code> entry is correct the moment the whole
   * symbolic call above returns, but its numeric field mirror, if it has one, is not: nothing
   * inside the held subtree wrote it, on purpose. Later native code that reads the variable
   * through its field rather than through <code>vars</code> would otherwise see whatever value it
   * held before this call ran.
   */
  private String wrapWithFieldResync(String symbolicText, java.util.Set<ISymbol> heldAssigned) {
    int m = module++;
    MethodSpec.Builder method = MethodSpec.methodBuilder("symbolicExpression" + m)
        .addModifiers(Modifier.PRIVATE).returns(IExpr.class);
    method.addStatement("$T result = F.eval($L)", IExpr.class, symbolicText);
    for (ISymbol sym : heldAssigned) {
      String field = numericVariables.apply(sym);
      if (field == null || !field.startsWith("this.")) {
        continue;
      }
      String resync = field.endsWith("_c") ? "$L = engine.evalComplex(vars.get($S))"
          : field.endsWith("_l") ? "$L = (long) engine.evalInt(vars.get($S))"
              : "$L = engine.evalDouble(vars.get($S))";
      method.addStatement(resync, field, sym.toString());
    }
    method.addStatement("return result");
    classBuilder.addMethod(method.build());
    return "symbolicExpression" + m + "()";
  }

  private boolean convertSymbolic(StringBuilder buf, IExpr expression) {
    Map<ISymbol, String> generated = new HashMap<>();
    java.util.Set<ISymbol> heldAssigned = new java.util.LinkedHashSet<>();
    IExpr prepared = hoistStatements(expression, generated, false, heldAssigned);
    try {
      SymbolicFormWriter writer = new SymbolicFormWriter(x -> {
        // a statement lifted out above is reached through the method which generates it
        String statement = generated.get(x);
        if (statement != null) {
          return statement;
        }
        // a constant which was lifted into a field is read from the field, not written out again
        String constantField = constantFields.get(x);
        if (constantField != null) {
          return constantField;
        }
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
      });
      String symbolicText = writer.write(prepared);
      buf.append(heldAssigned.isEmpty() ? symbolicText
          : wrapWithFieldResync(symbolicText, heldAssigned));
      return true;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return false;
  }
}
