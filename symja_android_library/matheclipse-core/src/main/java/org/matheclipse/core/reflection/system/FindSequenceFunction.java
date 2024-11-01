package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;


public class FindSequenceFunction extends AbstractEvaluator {

  public FindSequenceFunction() {
    // default ctor
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1().isList()) {
      IAST list = (IAST) ast.arg1();

      IInteger[] sequence = Convert.toIntegerArray(list);
      if (sequence.length > 2) {
        IExpr variable = F.NIL;
        if (ast.isAST2()) {
          variable = ast.arg2();
        }
        // Check for arithmetic sequence
        IInteger diff = sequence[1].subtract(sequence[0]);
        boolean isArithmetic = true;
        for (int i = 2; i < sequence.length; i++) {
          if (!sequence[i - 1].add(diff).equals(sequence[i])) {
            isArithmetic = false;
            break;
          }
        }
        if (isArithmetic) {
          IInteger constant = sequence[0].subtract(diff);
          IExpr times = diff.isOne() ? F.Slot1 : F.Times(diff, F.Slot1);
          IExpr plus = constant.isZero() ? times : F.Plus(constant, times);
          IAST function = F.Function(plus);
          return createFunction(function, variable, engine);
        }

        if (!sequence[0].isZero()) {
          // Check for geometric sequence
          IRational ratio = sequence[1].divideBy(sequence[0]);
          if (!ratio.isOne()) {
            boolean isGeometric = true;
            for (int i = 2; i < sequence.length; i++) {
              if (sequence[i - 1].isZero()
                  || !sequence[i].divideBy(sequence[i - 1]).equals(ratio)) {
                isGeometric = false;
                break;
              }
            }
            if (isGeometric) {
              IRational constant = sequence[0].divideBy(ratio);
              IExpr power = F.Power(ratio, F.Slot1);
              IExpr times = constant.isOne() ? power : F.Times(constant, power);
              IAST function = F.Function(times);
              return createFunction(function, variable, engine);
            }
          }
        }

        IInteger ratio = F.C1;
        if (!sequence[0].isOne()) {
          ratio = sequence[0];
          for (int i = 0; i < sequence.length; i++) {
            IRational divideBy = sequence[i].divideBy(ratio);
            if (!divideBy.denominator().isOne()) {
              return F.NIL;
            }
            sequence[i] = divideBy.numerator();
          }
        }
        if (sequence[0].isOne()) {
          IExpr result =
              compareSequence(F.Factorial(F.Slot1), FACTORIAL12, sequence, ratio, variable, engine);
          if (result.isPresent()) {
            return result;
          }
          result = compareSequence(F.Factorial2(F.Slot1), FACTORIAL219, sequence, ratio, variable,
              engine);
          if (result.isPresent()) {
            return result;
          }
          result =
              compareSequence(F.Fibonacci(F.Slot1), FIBONACCI40, sequence, ratio, variable, engine);
          if (result.isPresent()) {
            return result;
          }
        }
      }
    }
    return F.NIL;
  }


  private final static int[] FACTORIAL12 =
      {1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600};

  private final static int[] FACTORIAL219 = {1, 2, 3, 8, 15, 48, 105, 384, 945, 3840, 10395, 46080,
      135135, 645120, 2027025, 10321920, 34459425, 185794560, 654729075};

  private final static int[] FIBONACCI40 =
      {1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946,
          17711, 28657, 46368, 75025, 121393, 196418, 317811, 514229, 832040, 1346269, 2178309,
          3524578, 5702887, 9227465, 14930352, 24157817, 39088169, 63245986, 102334155};

  private static IExpr compareSequence(IExpr intFunction, int[] intSequence, IInteger[] sequence,
      IInteger ratio, IExpr variable, EvalEngine engine) {
    if (sequence.length <= intSequence.length) {
      boolean isEqual = true;
      for (int i = 0; i < sequence.length; i++) {
        if (!sequence[i].equalsInt(intSequence[i])) {
          isEqual = false;
          break;
        }
      }
      if (isEqual) {
        IExpr times = ratio.isOne() ? intFunction : F.Times(ratio, intFunction);
        IAST function = F.Function(times);
        return createFunction(function, variable, engine);
      }
    }
    return F.NIL;
  }

  private static IExpr createFunction(IAST function, IExpr variable, EvalEngine engine) {
    if (variable.isPresent()) {
      return engine.evaluate(F.unaryAST1(function, variable));
    }
    return function;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }


  /** {@inheritDoc} */
  @Override
  public void setUp(final ISymbol newSymbol) {}
}
