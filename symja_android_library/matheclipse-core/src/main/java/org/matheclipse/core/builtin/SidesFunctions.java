package org.matheclipse.core.builtin;

import java.util.function.Function;
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

  private static class AddSides extends AbstractCoreFunctionEvaluator {

    private static IExpr function(IExpr x, IExpr y) {
      return F.Plus(x, y);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg2 = engine.evaluate(ast.arg2());
      IExpr arg1 = ast.arg1();
      if (!arg2.isComparatorFunction()) {
        if (!arg1.isComparatorFunction()) {
          arg1 = engine.evaluate(arg1);
        }
        if (arg1.isTrue() || arg1.isFalse()) {
          return arg1;
        }
        if (arg1.isComparatorFunction()) {
          IAST comparator = (IAST) arg1;
          int headID = comparator.headID();
          switch (headID) {
            case ID.Equal:
            case ID.Unequal:
            case ID.Less:
            case ID.LessEqual:
            case ID.Greater:
            case ID.GreaterEqual:
              return comparator.map(x -> function(x, arg2));
          }
        }
      }
      // `1` should be an equation or inequality.
      return IOFunctions.printMessage(ast.topHead(), "eqin", F.List(arg1), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
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
        }
      }

      // `1` should be an equation or inequality.
      return IOFunctions.printMessage(ast.topHead(), "eqin", F.List(arg2), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class DivideSides extends AbstractCoreFunctionEvaluator {

    private static IExpr function(IExpr x, IExpr y) {
      return F.Divide(x, y);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isComparatorFunction()) {
        arg1 = engine.evaluate(arg1);
      }
      if (arg1.isTrue() || arg1.isFalse()) {
        return arg1;
      }
      if (arg1.isComparatorFunction()) {
        final IExpr arg2;
        if (ast.isAST1()) {
          if (arg1.size() != 3) {
            return F.NIL;
          }
          arg2 = arg1.second();
        } else {
          arg2 = engine.evaluate(ast.arg2());
        }
        if (arg2.isZero()) {
          // Cannot divide sides of an equation or inequality by 0.
          return IOFunctions.printMessage(ast.topHead(), "arg2", F.CEmptyList, engine);
        }
        if (!arg2.isComparatorFunction()) {
          IAST comparator = (IAST) arg1;
          int headID = comparator.headID();
          switch (headID) {
            case ID.Equal:
              return comparator.map(x -> function(x, arg2));
            case ID.Unequal:
              return comparator.map(x -> function(x, arg2));
            case ID.Less:
              {
                if (arg2.isNegative()) {
                  return comparator.mapReverse(x -> function(x, arg2));
                }
                if (arg2.isPositive()) {
                  return comparator.map(x -> function(x, arg2));
                }
                return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
              }
            case ID.LessEqual:
              {
                if (arg2.isNegative()) {
                  return comparator.mapReverse(x -> function(x, arg2));
                }
                if (arg2.isPositive()) {
                  return comparator.map(x -> function(x, arg2));
                }
                return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
              }
            case ID.Greater:
              {
                if (arg2.isNegative()) {
                  return comparator.mapReverse(x -> function(x, arg2));
                }
                if (arg2.isPositive()) {
                  return comparator.map(x -> function(x, arg2));
                }
                return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
              }
            case ID.GreaterEqual:
              {
                if (arg2.isNegative()) {
                  return comparator.mapReverse(x -> function(x, arg2));
                }
                if (arg2.isPositive()) {
                  return comparator.map(x -> function(x, arg2));
                }
                return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
              }
          }
        }
      }
      // `1` should be an equation or inequality.
      return IOFunctions.printMessage(ast.topHead(), "eqin", F.List(arg1), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class MultiplySides extends AbstractCoreFunctionEvaluator {

    private static IExpr function(IExpr x, IExpr y) {
      return F.Times(x, y);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg2 = engine.evaluate(ast.arg2());
      IExpr arg1 = ast.arg1();
      if (!arg2.isComparatorFunction()) {
        if (!arg1.isComparatorFunction()) {
          arg1 = engine.evaluate(arg1);
        }
        if (arg1.isTrue() || arg1.isFalse()) {
          return arg1;
        }
        if (arg1.isComparatorFunction()) {
          IAST comparator = (IAST) arg1;
          int headID = comparator.headID();
          switch (headID) {
            case ID.Equal:
              return comparator.map(x -> function(x, arg2));
            case ID.Unequal:
              return comparator.map(x -> function(x, arg2));
            case ID.Less:
              {
                if (arg2.isNegative()) {
                  return comparator.mapReverse(x -> function(x, arg2));
                }
                if (arg2.isPositive()) {
                  return comparator.map(x -> function(x, arg2));
                }
                return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
              }
            case ID.LessEqual:
              {
                if (arg2.isNegative()) {
                  return comparator.mapReverse(x -> function(x, arg2));
                }
                if (arg2.isPositive()) {
                  return comparator.map(x -> function(x, arg2));
                }
                return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
              }
            case ID.Greater:
              {
                if (arg2.isNegative()) {
                  return comparator.mapReverse(x -> function(x, arg2));
                }
                if (arg2.isPositive()) {
                  return comparator.map(x -> function(x, arg2));
                }
                return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
              }
            case ID.GreaterEqual:
              {
                if (arg2.isNegative()) {
                  return comparator.mapReverse(x -> function(x, arg2));
                }
                if (arg2.isPositive()) {
                  return comparator.map(x -> function(x, arg2));
                }
                return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
              }
          }
        }
      }
      // `1` should be an equation or inequality.
      return IOFunctions.printMessage(ast.topHead(), "eqin", F.List(arg1), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class SubtractSides extends AbstractCoreFunctionEvaluator {

    private static IExpr function(IExpr x, IExpr y) {
      return F.Subtract(x, y);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isComparatorFunction()) {
        arg1 = engine.evaluate(arg1);
      }
      if (arg1.isTrue() || arg1.isFalse()) {
        return arg1;
      }
      if (arg1.isComparatorFunction()) {
        final IExpr arg2;
        if (ast.isAST1()) {
          if (arg1.size() != 3) {
            return F.NIL;
          }
          arg2 = arg1.second();
        } else {
          arg2 = engine.evaluate(ast.arg2());
        }
        if (!arg2.isComparatorFunction()) {
          IAST comparator = (IAST) arg1;
          int headID = comparator.headID();
          switch (headID) {
            case ID.Equal:
            case ID.Unequal:
            case ID.Less:
            case ID.LessEqual:
            case ID.Greater:
            case ID.GreaterEqual:
              return comparator.map(x -> function(x, arg2));
          }
        }
      }
      // `1` should be an equation or inequality.
      return IOFunctions.printMessage(ast.topHead(), "eqin", F.List(arg1), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static IExpr piecewiseComparator(
      IAST comparator, final IExpr value, Function<IExpr, IExpr> function) {
    IAST list1 = comparator.map(function);
    IAST list2 = comparator.mapReverse(function);
    IAST listOfConditions =
        F.List( //
            F.List(list1, F.Greater(value, F.C0)), //
            F.List(list2, F.Less(value, F.C0)));
    return F.Piecewise(listOfConditions, comparator);
  }

  public static void initialize() {
    Initializer.init();
  }

  private SidesFunctions() {}
}
