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
        } else if (arg1.isConditionalExpression() && arg1.first().isAST()) {
          IAST a1 = (IAST) arg1.first();
          IExpr temp = addSides(a1, arg2);
          if (temp.isPresent()) {
            return F.ConditionalExpression(temp, arg1.second());
          }
        } else if (arg1.isComparatorFunction()) {
          IAST comparator = (IAST) arg1;
          IExpr temp = addSides(comparator, arg2);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
      // `1` should be an equation or inequality.
      return Errors.printMessage(ast.topHead(), "eqin", F.list(arg1), engine);
    }

    private IExpr addSides(IAST comparator, final IExpr arg2) {
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
      return F.NIL;
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
      IExpr arg2 = F.NIL;
      if (ast.isAST1()) {
        if (arg1.size() != 3) {
          return F.NIL;
        }
        if (arg1.isConditionalExpression()) {
          IExpr a1 = arg1.first();
          if (a1.isComparatorFunction()) {
            int headID = a1.headID();
            switch (headID) {
              case ID.Equal:
              case ID.Unequal:
              case ID.Less:
              case ID.LessEqual:
              case ID.Greater:
              case ID.GreaterEqual:
                arg2 = a1.last();
                break;
              default:
                break;
            }
          }
        } else {
          arg2 = arg1.second();
        }
      } else {
        arg2 = engine.evaluate(ast.arg2());
      }
      if (arg2.isPresent()) {
        if (arg1.isConditionalExpression() && arg1.first().isAST()) {
          IAST a1 = (IAST) arg1.first();
          IExpr temp = divideSides(a1, arg2);
          if (temp.isPresent()) {
            temp = engine.evaluate(temp);
            IExpr piecewise = F.Piecewise(F.list(F.list(temp), F.Unequal(arg2, F.C0)), a1);
            return F.ConditionalExpression(piecewise, arg1.second());
          }
        } else if (arg1.isComparatorFunction()) {
          if (arg2.isZero()) {
            // Cannot divide sides of an equation or inequality by 0.
            return Errors.printMessage(ast.topHead(), "arg2", F.CEmptyList, engine);
          } else if (!arg2.isComparatorFunction()) {
            IAST comparator = (IAST) arg1;
            IExpr temp = divideSides(comparator, arg2);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }
      }
      // `1` should be an equation or inequality.
      return Errors.printMessage(ast.topHead(), "eqin", F.list(arg1), engine);
    }

    private IExpr divideSides(IAST comparator, IExpr arg2) {
      int headID = comparator.headID();
      switch (headID) {
        case ID.Equal:
          return comparator.map(x -> function(x, arg2));
        case ID.Unequal:
          return comparator.map(x -> function(x, arg2));
        case ID.Less: {
          if (arg2.isNegative()) {
            return comparator.mapReverse(x -> function(x, arg2));
          }
          if (arg2.isPositive()) {
            return comparator.map(x -> function(x, arg2));
          }
          return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
        }
        case ID.LessEqual: {
          if (arg2.isNegative()) {
            return comparator.mapReverse(x -> function(x, arg2));
          }
          if (arg2.isPositive()) {
            return comparator.map(x -> function(x, arg2));
          }
          return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
        }
        case ID.Greater: {
          if (arg2.isNegative()) {
            return comparator.mapReverse(x -> function(x, arg2));
          }
          if (arg2.isPositive()) {
            return comparator.map(x -> function(x, arg2));
          }
          return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
        }
        case ID.GreaterEqual: {
          if (arg2.isNegative()) {
            return comparator.mapReverse(x -> function(x, arg2));
          }
          if (arg2.isPositive()) {
            return comparator.map(x -> function(x, arg2));
          }
          return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
        }
      }
      return F.NIL;
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
        } else if (arg1.isConditionalExpression() && arg1.first().isAST()) {
          IAST a1 = (IAST) arg1.first();
          IExpr temp = multiplySides(a1, arg2);
          if (temp.isPresent()) {
            temp = engine.evaluate(temp);
            IExpr piecewise = F.Piecewise(F.list(F.list(temp), F.Unequal(arg2, F.C0)), a1);
            return F.ConditionalExpression(piecewise, arg1.second());
          }
        } else if (arg1.isComparatorFunction()) {
          IAST comparator = (IAST) arg1;
          IExpr temp = multiplySides(comparator, arg2);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
      // `1` should be an equation or inequality.
      return Errors.printMessage(ast.topHead(), "eqin", F.list(arg1), engine);
    }

    private IExpr multiplySides(IAST comparator, final IExpr arg2) {
      int headID = comparator.headID();
      switch (headID) {
        case ID.Equal:
          return comparator.map(x -> function(x, arg2));
        case ID.Unequal:
          return comparator.map(x -> function(x, arg2));
        case ID.Less: {
          if (arg2.isNegative()) {
            return comparator.mapReverse(x -> function(x, arg2));
          }
          if (arg2.isPositive()) {
            return comparator.map(x -> function(x, arg2));
          }
          return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
        }
        case ID.LessEqual: {
          if (arg2.isNegative()) {
            return comparator.mapReverse(x -> function(x, arg2));
          }
          if (arg2.isPositive()) {
            return comparator.map(x -> function(x, arg2));
          }
          return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
        }
        case ID.Greater: {
          if (arg2.isNegative()) {
            return comparator.mapReverse(x -> function(x, arg2));
          }
          if (arg2.isPositive()) {
            return comparator.map(x -> function(x, arg2));
          }
          return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
        }
        case ID.GreaterEqual: {
          if (arg2.isNegative()) {
            return comparator.mapReverse(x -> function(x, arg2));
          }
          if (arg2.isPositive()) {
            return comparator.map(x -> function(x, arg2));
          }
          return piecewiseComparator(comparator, arg2, x -> function(x, arg2));
        }
      }
      return F.NIL;
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
      IExpr arg2 = F.NIL;
      if (ast.isAST1()) {
        if (arg1.size() != 3) {
          return F.NIL;
        }
        if (arg1.isConditionalExpression()) {
          IExpr a1 = arg1.first();
          if (a1.isComparatorFunction()) {
            int headID = a1.headID();
            switch (headID) {
              case ID.Equal:
              case ID.Unequal:
              case ID.Less:
              case ID.LessEqual:
              case ID.Greater:
              case ID.GreaterEqual:
                arg2 = a1.last();
                break;
              default:
                break;
            }
          }
        } else {
          arg2 = arg1.second();
        }
      } else {
        arg2 = engine.evaluate(ast.arg2());
      }
      if (arg2.isPresent()) {
        if (arg1.isConditionalExpression() && arg1.first().isAST()) {
          IAST a1 = (IAST) arg1.first();
          IExpr temp = subtractSides(a1, arg2);
          if (temp.isPresent()) {
            return F.ConditionalExpression(temp, arg1.second());
          }
        } else if (arg1.isComparatorFunction()) {
          if (!arg2.isComparatorFunction()) {
            IAST comparator = (IAST) arg1;
            IExpr temp = subtractSides(comparator, arg2);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }
      }
      // `1` should be an equation or inequality.
      return Errors.printMessage(ast.topHead(), "eqin", F.list(arg1), engine);
    }

    private IExpr subtractSides(IAST comparator, final IExpr arg2) {
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
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
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
