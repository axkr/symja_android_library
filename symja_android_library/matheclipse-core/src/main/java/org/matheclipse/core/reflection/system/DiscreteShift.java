package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * Implementation of the <code>DiscreteShift</code> function.
 * </p>
 *
 * <p>
 * DiscreteShift(expr, k) replaces k with k+1 in expr. DiscreteShift(expr, {k, m}) replaces k with
 * k+m in expr. DiscreteShift(expr, {k, m, h}) replaces k with k+(m*h) in expr.
 * </p>
 */
public class DiscreteShift extends AbstractFunctionEvaluator {

  public DiscreteShift() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    // DiscreteShift requires at least the expression and one variable spec
    // Usage: DiscreteShift(expr, k) or DiscreteShift(expr, {k, m}, ...)
    IExpr expression = ast.arg1();
    // Use IASTAppendable to collect the transformation rules.
    // We allocate size based on ast.size() - 1 args.
    IASTAppendable rulesList = F.ListAlloc(ast.argSize());
    boolean allIntegerShifts = true;

    for (int i = 2; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      ISymbol variable;
      IExpr shift;

      if (arg.isSymbol()) {
        // Case: DiscreteShift(expr, k) -> shift is 1
        variable = (ISymbol) arg;
        shift = F.C1;
      } else if (arg.isList()) {
        IAST listArg = (IAST) arg;
        if (listArg.size() > 1 && listArg.arg1().isSymbol()) {
          variable = (ISymbol) listArg.arg1();
          if (listArg.size() == 2) {
            // Case: DiscreteShift(expr, {k}) -> shift is 1
            shift = F.C1;
          } else if (listArg.size() == 3) {
            // Case: DiscreteShift(expr, {k, m}) -> shift is m
            shift = listArg.arg2();
          } else if (listArg.size() == 4) {
            // Case: DiscreteShift(expr, {k, m, h}) -> shift is m * h
            shift = F.Times(listArg.arg2(), listArg.arg3());
          } else {
            // Invalid list spec
            return F.NIL;
          }
        } else {
          // Invalid structure in list
          return F.NIL;
        }
      } else {
        // Argument is neither Symbol nor List
        return F.NIL;
      }

      if (!engine.evaluate(shift).isInteger()) {
        allIntegerShifts = false;
      }
      // Create the rule: variable -> variable + shift
      // Example: k -> k + 1 or k -> k + m
      rulesList.append(F.Rule(variable, F.Plus(variable, shift)));
    }

    // Apply all rules to the expression structurally.
    IExpr result = engine.evaluate(F.subst(expression, rulesList));
    // With an integer shift wolframscript combines rational summands over a common denominator, so
    // DiscreteShift(1/(2 n + 1), n) becomes (3 + 2 n)^-1 rather than (1 + 2 (1 + n))^-1. A symbolic
    // shift stays unfolded (e.g. (1 + 2 (k + n))^-1). A pure polynomial product such as
    // (1 + m) (1 + n) has no denominator to combine and must not be expanded here.
    boolean hasDenominator = !result.isFree(x -> x.isPower() && x.exponent().isNegative(), false);
    if (allIntegerShifts && hasDenominator) {
      IExpr together = engine.evaluate(F.Together(result));
      IExpr denominator = engine.evaluate(F.Denominator(together));
      // Together leaves a lone denominator such as 1 + 2 (1 + n) unexpanded, so expand it.
      result = denominator.isOne() //
          ? together
          : engine.evaluate(F.Divide(F.Numerator(together), F.Expand(denominator)));
    }
    // wolframscript expands the result only when its top-level head is Plus.
    if (result.isPlus()) {
      result = engine.evaluate(F.Expand(result));
    }
    return result;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }
}
