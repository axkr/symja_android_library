package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDistribution;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISymbol;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class StatisticalMomentFunctions {
  private static class Initializer {

    private static void init() {
      S.Cumulant.setEvaluator(new Cumulant());
      S.CentralMoment.setEvaluator(new CentralMoment());
      S.Moment.setEvaluator(new Moment());
    }
  }

  /** central moment function */
  public interface ICentralMoment extends IDistribution {
    IExpr centralMoment(IAST dist, IExpr m, EvalEngine engine);

    IExpr kurtosis(IAST dist, EvalEngine engine);
  }

  private static final class Moment extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // TODO only vectors are implemented yet
      if (ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        IntArrayList dimensions = LinearAlgebra.dimensions(list);
        IExpr arg2 = ast.arg2();
        final int r = arg2.toIntDefault();
        if (r > 0) {
          if (r == 0) {
            return F.C1;
          }

          if (dimensions.size() == 1) {
            return F.Divide(F.Total(F.Power(list, r)), F.Length(list));
          } else if (dimensions.size() > 1) {
            return F.ArrayReduce(F.Function(F.Moment(F.Slot1, r)), list, F.C1);
          }
        } else if (r == Integer.MIN_VALUE) {
          if (!arg2.isList()) {
            if (dimensions.size() == 1) {
              return F.Divide(F.Total(F.Power(list, arg2)), F.Length(list));
            }
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * CentralMoment(list, r)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the the <code>r</code>th central moment (i.e. the <code>r</code>th moment about the mean)
   * of <code>list</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Central_moment">Wikipedia - Central moment</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt;&gt; CentralMoment({1.1, 1.2, 1.4, 2.1, 2.4}, 4)
   * 0.10085
   * </pre>
   */
  private static final class CentralMoment extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        IExpr arg2 = ast.arg2();
        IntArrayList dimensions = LinearAlgebra.dimensions(list);
        final int r = arg2.toIntDefault();
        if (r > 0) {
          if (dimensions.size() == 1) {
            // TODO only vectors are implemented yet
            return F.Divide(F.Total(F.Power(F.Subtract(list, F.Mean(list)), r)), F.Length(list));
          } else if (dimensions.size() > 1) {
            return F.ArrayReduce(F.Function(F.CentralMoment(F.Slot1, r)), list, F.C1);
          }
        } else if (r == Integer.MIN_VALUE) {
          if (!arg2.isList()) {
            if (dimensions.size() == 1) {
              return F.Divide(F.Total(F.Power(F.Subtract(list, F.Mean(list)), arg2)),
                  F.Length(list));
            }
          }
        }
      }
      try {
        if (ast.arg1().isAST()) {
          IAST dist = (IAST) ast.arg1();
          IExpr order = ast.arg2();
          if (dist.head().isSymbol()) {
            ISymbol head = (ISymbol) dist.head();
            if (dist.head().isSymbol()) {
              if (head instanceof IBuiltInSymbol) {
                IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
                if (evaluator instanceof ICentralMoment) {
                  ICentralMoment centralMoment = (ICentralMoment) evaluator;
                  dist = centralMoment.checkParameters(dist);
                  if (dist.isPresent()) {
                    return centralMoment.centralMoment(dist, order, engine);
                  }
                }
              }
            }
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.CentralMoment, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Cumulant extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();

      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        IntArrayList dimensions = LinearAlgebra.dimensions(list);
        final int argSize = list.argSize();
        int r = ast.arg2().toIntDefault();
        if (argSize > 0) {
          if (r == 0) {
            return F.C0;
          }
          if (r > 0) {
            if (dimensions.size() == 1) {
              // TODO only vectors are implemented yet
              IASTAppendable subtractions = F.PlusAlloc(argSize);
              IASTAppendable result = F.PlusAlloc(argSize);
              IFraction fraction = F.QQ(1, argSize);
              IAST times = F.Times(fraction, subtractions);
              for (int i = 1; i <= argSize; i++) {
                subtractions.append(list.get(i).negate());
                result.append(F.Power(F.Plus(list.get(i), times), F.ZZ(r)));
              }
              return F.Times(fraction, result);
              // } else if (dimensions.size() > 1) {
              // return F.ArrayReduce(F.Function(F.Cumulant(F.Slot1, r)), list, F.C1);
            }
          }


        }
      }
      return F.NIL;
    }



    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private StatisticalMomentFunctions() {}
}
