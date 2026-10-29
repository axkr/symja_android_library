package org.matheclipse.core.reflection.system;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The serialized form of a compiled function,
 * <code>CompiledFunction(version, argumentTypes, registers, constants, ..., instructions, Function(...), Evaluate)</code>
 * with its 8 arguments, as the <code>InputForm</code> of a compiled function writes it.
 *
 * <p>
 * It is applied to arguments through the uncompiled <code>Function</code> it embeds next to the
 * instructions, which are not interpreted. An argument of the type <code>_Real</code> becomes a
 * machine real number, one of the type <code>_Complex</code> a machine complex number, and one of
 * the type <code>_Integer</code> has to be an integer. If an argument has no such value, the
 * message <code>CompiledFunction::cfsa</code> is printed and the <code>Function</code> is evaluated
 * with the arguments as they are. A form with another number of arguments is invalid: its call
 * stays unevaluated with the message <code>CompiledFunction::cfnv</code>.
 *
 * <p>
 * The <code>matheclipse-compile</code> module replaces this evaluator by its own, which handles
 * the compiled functions <code>Compile</code> creates and calls
 * {@link #evaluateForm(IAST, EvalEngine)} for the serialized form.
 */
public class CompiledFunctionForm extends AbstractCoreFunctionEvaluator {

  /** The number of arguments of the serialized form. */
  private static final int SERIALIZED_ARGUMENTS = 8;

  /** The position of the uncompiled <code>Function</code> in the serialized form. */
  private static final int FUNCTION_POSITION = 7;

  public CompiledFunctionForm() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return evaluateForm(ast, engine);
  }

  /**
   * Evaluate a serialized form <code>CompiledFunction(...)</code>, which only checks its number of
   * arguments, or its call <code>CompiledFunction(...)(arg1, arg2, ...)</code>.
   *
   * @return {@link F#NIL} if <code>ast</code> stays unevaluated
   */
  public static IExpr evaluateForm(final IAST ast, EvalEngine engine) {
    if (ast.head() == S.CompiledFunction) {
      if (ast.argSize() > 1) {
        // CompiledFunction(variables) with one argument is no serialized form: the unevaluated
        // call which a compiled function of the matheclipse-compile module answers
        checkArgumentCount(ast, engine);
      }
      return F.NIL;
    }
    if (ast.head().isAST(S.CompiledFunction)) {
      return apply((IAST) ast.head(), ast, engine);
    }
    return F.NIL;
  }

  /**
   * Print <code>CompiledFunction::argrx</code> for a serialized form with a number of arguments
   * other than 8.
   *
   * @return <code>true</code> if the form has 8 arguments
   */
  private static boolean checkArgumentCount(IAST compiledFunction, EvalEngine engine) {
    if (compiledFunction.argSize() == SERIALIZED_ARGUMENTS) {
      return true;
    }
    // `1` called with `2` arguments; `3` arguments are expected.
    Errors.printMessage(S.CompiledFunction, "argrx", F.list(S.CompiledFunction,
        F.ZZ(compiledFunction.argSize()), F.ZZ(SERIALIZED_ARGUMENTS)), engine);
    return false;
  }

  /**
   * Apply the serialized form of a compiled function to the arguments of <code>call</code>.
   *
   * @param compiledFunction <code>CompiledFunction(...)</code>
   * @param call <code>CompiledFunction(...)(arg1, arg2, ...)</code>
   * @return the value, or {@link F#NIL} if the call stays unevaluated
   */
  private static IExpr apply(IAST compiledFunction, IAST call, EvalEngine engine) {
    if (!checkArgumentCount(compiledFunction, engine)
        || !compiledFunction.get(FUNCTION_POSITION).isAST(S.Function)
        || !compiledFunction.arg2().isList()) {
      // CompiledFunction object is invalid and cannot be used.
      Errors.printMessage(S.CompiledFunction, "cfnv", F.CEmptyList, engine);
      return F.NIL;
    }
    final IAST types = (IAST) compiledFunction.arg2();
    if (call.argSize() != types.argSize()) {
      // The number of arguments `1` does not match the length `2` of the argument template.
      Errors.printMessage(S.CompiledFunction, "cfct",
          F.list(F.ZZ(call.argSize()), F.ZZ(types.argSize())), engine);
      return F.NIL;
    }
    final IExpr function = compiledFunction.get(FUNCTION_POSITION);
    final IASTAppendable arguments = F.ast(function, call.argSize());
    final IASTAppendable coerced = F.ast(function, call.argSize());
    boolean valid = true;
    for (int i = 1; i <= call.argSize(); i++) {
      final IExpr argument = engine.evaluate(call.get(i));
      arguments.append(argument);
      if (valid) {
        final IExpr type = types.get(i);
        final IExpr value = coerce(argument, type.isList() ? type.first() : type, engine);
        if (value.isNIL()) {
          valid = false;
          // Argument `1` at position `2` should be a machine-size `3`.
          Errors.printMessage(S.CompiledFunction, "cfsa",
              F.list(argument, F.ZZ(i), F.stringx(kind(type))), engine);
        } else {
          coerced.append(value);
        }
      }
    }
    // the arguments which have no machine value of their type are evaluated uncompiled
    return valid ? coerced : arguments;
  }

  /**
   * The machine value of an argument for its type; a list element by element.
   *
   * @return {@link F#NIL} if the argument has no value of the type
   */
  private static IExpr coerce(IExpr argument, IExpr type, EvalEngine engine) {
    if (argument.isList()) {
      final IAST list = (IAST) argument;
      final IASTAppendable result = F.ListAlloc(list.argSize());
      for (IExpr element : list) {
        final IExpr value = coerce(element, type, engine);
        if (value.isNIL()) {
          return F.NIL;
        }
        result.append(value);
      }
      return result;
    }
    final ISymbol head = typeHead(type);
    try {
      if (head == S.Real) {
        return F.num(engine.evalDouble(argument));
      }
      if (head == S.Complex) {
        final Complex c = engine.evalComplex(argument);
        return F.complexNum(c);
      }
    } catch (ArgumentTypeException ate) {
      return F.NIL;
    }
    if (head == S.Integer) {
      return argument instanceof IInteger
          && ((IInteger) argument).toLongDefault() != Config.INVALID_LONG ? argument : F.NIL;
    }
    return argument;
  }

  /** The head of <code>_Real</code>, <code>_Integer</code>, ...; <code>null</code> otherwise. */
  private static ISymbol typeHead(IExpr type) {
    if (type.isBlank()) {
      final IExpr head = ((IPatternObject) type).getHeadTest();
      return head != null && head.isSymbol() ? (ISymbol) head : null;
    }
    return null;
  }

  /** The kind of number which the message <code>CompiledFunction::cfsa</code> names. */
  private static String kind(IExpr type) {
    final ISymbol head = typeHead(type.isList() ? type.first() : type);
    if (head == S.Integer) {
      return "integer";
    }
    if (head == S.Complex) {
      return "complex number";
    }
    return "real number";
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.HOLDALL);
  }
}
