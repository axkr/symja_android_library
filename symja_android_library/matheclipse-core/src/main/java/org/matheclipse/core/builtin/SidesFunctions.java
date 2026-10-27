package org.matheclipse.core.builtin;

import java.util.function.Function;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class SidesFunctions {

  private static class Initializer {

    private static void init() {
      S.AddSides.setEvaluator(new AddSides());
      S.ApplySides.setEvaluator(new ApplySides());
      S.DivideSides.setEvaluator(new DivideSides());
      S.MultiplySides.setEvaluator(new MultiplySides());
      S.SubtractSides.setEvaluator(new SubtractSides());
    }
  }

  /**
   * The evaluator of a function which applies an operation with a second operand to both sides of
   * an equation or inequality, also of one wrapped in <code>ConditionalExpression</code>.
   */
  private abstract static class AbstractSidesEvaluator extends AbstractCoreFunctionEvaluator {

    /** The operand is required and evaluated before the equation; none may be an equation. */
    private final boolean operandFirst;

    /**
     * The operation scales the sides: a conditional equation becomes a <code>Piecewise</code> which
     * requires a nonzero operand.
     */
    private final boolean scaled;

    /** The operand is a divisor, which must not be zero for an equation or inequality. */
    private final boolean divisor;

    AbstractSidesEvaluator(boolean operandFirst, boolean scaled, boolean divisor) {
      this.operandFirst = operandFirst;
      this.scaled = scaled;
      this.divisor = divisor;
    }

    /**
     * Apply the operation with <code>operand</code> to both sides of <code>comparator</code>.
     *
     * @return {@link F#NIL} if <code>comparator</code> is no equation or inequality
     */
    protected abstract IExpr sides(IAST comparator, IExpr operand);

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2;
      if (operandFirst) {
        arg2 = engine.evaluate(ast.arg2());
        if (arg2.isComparatorFunction()) {
          // `1` should be an equation or inequality.
          return Errors.printMessage(ast.topHead(), "eqin", F.list(arg1), engine);
        }
      } else {
        arg2 = null;
      }
      if (!arg1.isComparatorFunction()) {
        arg1 = engine.evaluate(arg1);
      }
      if (arg1.isTrue() || arg1.isFalse()) {
        return arg1;
      }
      if (!operandFirst) {
        arg2 = sidesOperand(ast, arg1, engine);
        if (arg2 == null) {
          return F.NIL;
        }
      }
      if (arg2.isPresent()) {
        if (arg1.isConditionalExpression() && arg1.first().isAST()) {
          IAST a1 = (IAST) arg1.first();
          IExpr temp = sides(a1, arg2);
          if (temp.isPresent()) {
            if (scaled) {
              temp = engine.evaluate(temp);
              temp = F.Piecewise(F.list(F.list(temp), F.Unequal(arg2, F.C0)), a1);
            }
            return F.ConditionalExpression(temp, arg1.second());
          }
        } else if (arg1.isComparatorFunction()) {
          if (divisor && arg2.isZero()) {
            // Cannot divide sides of an equation or inequality by 0.
            return Errors.printMessage(ast.topHead(), "arg2", F.CEmptyList, engine);
          } else if (!arg2.isComparatorFunction()) {
            IExpr temp = sides((IAST) arg1, arg2);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }
      }
      // `1` should be an equation or inequality.
      return Errors.printMessage(ast.topHead(), "eqin", F.list(arg1), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return operandFirst ? ARGS_2_2 : ARGS_1_2;
    }
  }

  private static class AddSides extends AbstractSidesEvaluator {

    AddSides() {
      super(true, false, false);
    }

    @Override
    protected IExpr sides(IAST comparator, IExpr operand) {
      return shiftSides(comparator, x -> F.Plus(x, operand));
    }
  }

  private static class ApplySides extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = engine.evaluate(ast.arg1());
      IExpr arg2 = ast.arg2();
      if (!arg2.isComparatorFunction()) {
        arg2 = engine.evaluate(arg2);
      }

      if (arg2.isTrue() || arg2.isFalse()) {
        return arg2;
      }
      if (arg2.isComparatorFunction()) {
        return applyComparatorSides(arg1, arg2, engine);
      } else if (arg2.isConditionalExpression()) {
        if (arg2.first().isComparatorFunction()) {
          IAST temp = applyComparatorSides(arg1, arg2.first(), engine);
          if (temp.isPresent()) {
            return F.ConditionalExpression(temp, arg2.second());
          }
        }
      }

      // `1` should be an equation or inequality.
      return Errors.printMessage(S.ApplySides, "eqin", F.list(arg2), engine);
    }

    private static IAST applyComparatorSides(final IExpr arg1, IExpr arg2, EvalEngine engine) {
      IAST comparator = (IAST) arg2;
      int headID = comparator.headID();
      switch (headID) {
        case ID.Equal:
        case ID.Unequal:
        case ID.Less:
        case ID.LessEqual:
        case ID.Greater:
        case ID.GreaterEqual:
          return comparator.map(x -> F.unaryAST1(arg1, x));
        default:
      }
      // `1` should be an equation or inequality.
      return Errors.printMessage(S.ApplySides, "eqin", F.list(arg2), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * Apply <code>f</code>, an addition or subtraction, to both sides of an equation or inequality.
   *
   * @return {@link F#NIL} if <code>comparator</code> is no equation or inequality
   */
  private static IExpr shiftSides(IAST comparator, Function<IExpr, IExpr> f) {
    switch (comparator.headID()) {
      case ID.Equal:
      case ID.Unequal:
      case ID.Less:
      case ID.LessEqual:
      case ID.Greater:
      case ID.GreaterEqual:
        return comparator.map(f);
      default:
        return F.NIL;
    }
  }

  /**
   * Apply <code>f</code>, a multiplication or division by <code>c</code>, to both sides of an
   * equation or inequality. An inequality is reversed for a negative <code>c</code> and becomes a
   * <code>Piecewise</code> for a <code>c</code> of unknown sign.
   *
   * @return {@link F#NIL} if <code>comparator</code> is no equation or inequality
   */
  private static IExpr scaleSides(IAST comparator, IExpr c, Function<IExpr, IExpr> f) {
    switch (comparator.headID()) {
      case ID.Equal:
      case ID.Unequal:
        return comparator.map(f);
      case ID.Less:
      case ID.LessEqual:
      case ID.Greater:
      case ID.GreaterEqual:
        if (c.isNegative()) {
          return comparator.mapReverse(f);
        }
        if (c.isPositive()) {
          return comparator.map(f);
        }
        return piecewiseComparator(comparator, c, f);
      default:
        return F.NIL;
    }
  }

  /**
   * The right-hand side of an equation or inequality, also of one wrapped in
   * <code>ConditionalExpression</code>.
   *
   * @return {@link F#NIL} if a <code>ConditionalExpression</code> holds no equation or inequality
   */
  private static IExpr rightHandSide(IExpr arg1) {
    if (arg1.isConditionalExpression()) {
      IExpr a1 = arg1.first();
      if (a1.isComparatorFunction()) {
        switch (a1.headID()) {
          case ID.Equal:
          case ID.Unequal:
          case ID.Less:
          case ID.LessEqual:
          case ID.Greater:
          case ID.GreaterEqual:
            return a1.last();
          default:
            break;
        }
      }
      return F.NIL;
    }
    return arg1.second();
  }

  /**
   * The operand of <code>f(eqn, c)</code>, or for <code>f(eqn)</code> the right-hand side of the
   * equation or inequality.
   *
   * @param arg1 the evaluated first argument
   * @return <code>null</code> for one argument which has not two sides, where the function gives
   *         up without a message; {@link F#NIL} if <code>arg1</code> has no right-hand side
   */
  private static IExpr sidesOperand(final IAST ast, IExpr arg1, EvalEngine engine) {
    if (ast.isAST1()) {
      if (arg1.size() != 3) {
        return null;
      }
      return rightHandSide(arg1);
    }
    return engine.evaluate(ast.arg2());
  }

  private static class DivideSides extends AbstractSidesEvaluator {

    DivideSides() {
      super(false, true, true);
    }

    @Override
    protected IExpr sides(IAST comparator, IExpr operand) {
      return scaleSides(comparator, operand, x -> F.Divide(x, operand));
    }
  }

  private static class MultiplySides extends AbstractSidesEvaluator {

    MultiplySides() {
      super(true, true, false);
    }

    @Override
    protected IExpr sides(IAST comparator, IExpr operand) {
      return scaleSides(comparator, operand, x -> F.Times(x, operand));
    }
  }

  private static class SubtractSides extends AbstractSidesEvaluator {

    SubtractSides() {
      super(false, false, false);
    }

    @Override
    protected IExpr sides(IAST comparator, IExpr operand) {
      return shiftSides(comparator, x -> F.Subtract(x, operand));
    }
  }

  private static IExpr piecewiseComparator(IAST comparator, final IExpr value,
      Function<IExpr, IExpr> function) {
    IAST list1 = comparator.map(function);
    IAST list2 = comparator.mapReverse(function);
    IAST listOfConditions = F.list( //
        F.list(list1, F.Greater(value, F.C0)), //
        F.list(list2, F.Less(value, F.C0)));
    return F.Piecewise(listOfConditions, comparator);
  }

  public static void initialize() {
    Initializer.init();
  }

  private SidesFunctions() {}
}
