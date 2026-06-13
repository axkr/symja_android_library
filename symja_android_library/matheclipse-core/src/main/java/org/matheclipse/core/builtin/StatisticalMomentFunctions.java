package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICentralMoment;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class StatisticalMomentFunctions {
  private static class Initializer {

    private static void init() {
      S.Cumulant.setEvaluator(new Cumulant());
      S.CentralMoment.setEvaluator(new CentralMoment());
      S.FactorialMoment.setEvaluator(new FactorialMoment());
      S.Moment.setEvaluator(new Moment());
    }
  }

  private static final class Moment extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        // Moment(r) represents the order r formal raw moment.
        return F.NIL;
      }

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();

      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        IntArrayList dimensions = LinearAlgebraUtil.dimensions(list);
        if (dimensions.size() == 0 || dimensions.contains(0)) {
          return F.NIL;
        }

        int N = list.argSize();
        if (N == 0)
          return F.NIL;

        if (arg2.isList()) {
          // Multivariate
          IAST rList = (IAST) arg2;
          int m = rList.argSize();
          if (dimensions.size() == 2 && dimensions.getInt(1) == m) {
            IASTAppendable sum = F.PlusAlloc(N);
            for (int i = 1; i <= N; i++) {
              IAST row = (IAST) list.get(i);
              IASTAppendable prod = F.TimesAlloc(m);
              for (int j = 1; j <= m; j++) {
                IExpr r = rList.get(j);
                if (r.isZero())
                  continue; // x^0 is 1, handled gracefully to avoid 0^0 issues
                if (r.isOne()) {
                  prod.append(row.get(j));
                } else {
                  prod.append(F.Power(row.get(j), r));
                }
              }
              IExpr term = F.C1;
              if (prod.argSize() == 1) {
                term = prod.arg1();
              } else if (prod.argSize() > 1) {
                term = prod;
              }
              sum.append(term);
            }
            return engine.evaluate(F.Divide(sum, F.ZZ(N)));
          } else if (dimensions.size() > 2) {
            // Fallback layout reduction for higher dimensional arrays
            return engine.evaluate(
                F.ArrayReduce(F.Function(F.Moment(F.Slot1, arg2)), list, F.List(F.C1, F.C2)));
          }
        } else {
          // Scalar
          if (arg2.isZero()) {
            if (dimensions.size() == 1)
              return F.C1;
            if (dimensions.size() == 2)
              return F.constantArray(F.C1, dimensions.getInt(1));
          }

          if (dimensions.size() == 1) {
            return engine.evaluate(F.Divide(F.Total(F.Power(list, arg2)), F.ZZ(N)));
          } else if (dimensions.size() > 1) {
            return engine.evaluate(F.ArrayReduce(F.Function(F.Moment(F.Slot1, arg2)), list, F.C1));
          }
        }
        return F.NIL;
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static final class CentralMoment extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        // CentralMoment(r) represents the order r formal central moment.
        return F.NIL;
      }

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();

      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        IntArrayList dimensions = LinearAlgebraUtil.dimensions(list);
        if (dimensions.size() == 0 || dimensions.contains(0)) {
          return F.NIL;
        }

        int N = list.argSize();
        if (N == 0)
          return F.NIL;

        if (arg2.isList()) {
          // Multivariate
          IAST rList = (IAST) arg2;
          int m = rList.argSize();
          if (dimensions.size() == 2 && dimensions.getInt(1) == m) {
            IExpr meanVec = engine.evaluate(F.Mean(list));
            if (meanVec.isList() && ((IAST) meanVec).argSize() == m) {
              IAST means = (IAST) meanVec;
              IASTAppendable sum = F.PlusAlloc(N);
              for (int i = 1; i <= N; i++) {
                IAST row = (IAST) list.get(i);
                IASTAppendable prod = F.TimesAlloc(m);
                for (int j = 1; j <= m; j++) {
                  IExpr r = rList.get(j);
                  if (r.isZero())
                    continue; // Handles 0-order gracefully (avoids 0^0)
                  IExpr diff = engine.evaluate(F.Subtract(row.get(j), means.get(j)));
                  if (r.isOne()) {
                    prod.append(diff);
                  } else {
                    prod.append(F.Power(diff, r));
                  }
                }
                IExpr term = F.C1;
                if (prod.argSize() == 1) {
                  term = prod.arg1();
                } else if (prod.argSize() > 1) {
                  term = prod;
                }
                sum.append(term);
              }
              return engine.evaluate(F.Divide(sum, F.ZZ(N)));
            }
          } else if (dimensions.size() > 2) {
            // Fallback layout reduction for higher dimensional arrays
            return engine.evaluate(F.ArrayReduce(F.Function(F.CentralMoment(F.Slot1, arg2)), list,
                F.List(F.C1, F.C2)));
          }
        } else {
          // Scalar
          if (arg2.isZero()) {
            if (dimensions.size() == 1)
              return F.C1;
            if (dimensions.size() == 2)
              return F.constantArray(F.C1, dimensions.getInt(1));
          }

          if (dimensions.size() == 1) {
            IExpr mean = engine.evaluate(F.Mean(list));
            return engine
                .evaluate(F.Divide(F.Total(F.Power(F.Subtract(list, mean), arg2)), F.ZZ(N)));
          } else if (dimensions.size() > 1) {
            return engine
                .evaluate(F.ArrayReduce(F.Function(F.CentralMoment(F.Slot1, arg2)), list, F.C1));
          }
        }
        return F.NIL;
      }

      try {
        if (arg1.isAST()) {
          IAST dist = (IAST) arg1;
          if (dist.head().isSymbol()) {
            ISymbol head = (ISymbol) dist.head();
            if (head instanceof IBuiltInSymbol) {
              IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
              if (evaluator instanceof ICentralMoment) {
                ICentralMoment centralMoment = (ICentralMoment) evaluator;
                dist = centralMoment.checkParameters(dist);
                if (dist.isPresent()) {
                  return centralMoment.centralMoment(dist, arg2, engine);
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
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Cumulant extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        // Cumulant(r) represents the formal cumulant.
        return F.NIL;
      }

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();

      int m;
      int[] R;
      if (arg2.isList()) {
        IAST rList = (IAST) arg2;
        m = rList.argSize();
        R = new int[m];
        for (int i = 1; i <= m; i++) {
          int r = rList.get(i).toIntDefault();
          if (r < 0) {
            return F.NIL;
          }
          R[i - 1] = r;
        }
      } else {
        int r = arg2.toIntDefault();
        if (r < 0) {
          return F.NIL;
        }
        m = 1;
        R = new int[] {r};
      }

      int totalR = 0;
      for (int i = 0; i < m; i++) {
        totalR += R[i];
      }

      if (totalR == 0) {
        return F.C0;
      }

      if (totalR == 1) {
        int k = 0;
        for (int i = 0; i < m; i++) {
          if (R[i] == 1) {
            k = i;
            break;
          }
        }
        if (arg1.isList()) {
          IAST data = (IAST) arg1;
          int[] dims = data.isMatrix();
          if (dims != null && dims[1] == m) {
            IASTAppendable col = F.ListAlloc(dims[0]);
            for (int i = 1; i <= dims[0]; i++) {
              col.append(((IAST) data.get(i)).get(k + 1));
            }
            return engine.evaluate(F.Mean(col));
          } else if (m == 1) {
            return engine.evaluate(F.Mean(arg1));
          }
        }
        // Fallback for distributions
        if (m == 1) {
          return engine.evaluate(F.Mean(arg1));
        }
        IExpr meanVec = engine.evaluate(F.Mean(arg1));
        if (meanVec.isList() && ((IAST) meanVec).argSize() >= k + 1) {
          return ((IAST) meanVec).get(k + 1);
        }
        return F.NIL;
      }

      // totalR >= 2
      int[] strides = new int[m];
      strides[m - 1] = 1;
      for (int j = m - 2; j >= 0; j--) {
        strides[j] = strides[j + 1] * (R[j + 1] + 1);
      }
      long totalSize = strides[0] * (R[0] + 1);

      if (totalSize > Config.MAX_AST_SIZE || totalSize < 0) {
        ASTElementLimitExceeded.throwIt(totalSize);
      }
      IExpr[] kArr = new IExpr[(int) totalSize];
      IExpr[] cArr = new IExpr[(int) totalSize];

      boolean isList = arg1.isList();
      int N = 0;
      IExpr[] means = null;
      boolean isMatrix = false;
      if (isList) {
        IAST data = (IAST) arg1;
        N = data.argSize();
        if (N == 0)
          return F.NIL;
        int[] dims = data.isMatrix();
        if (dims != null && dims[1] == m) {
          isMatrix = true;
          means = new IExpr[m];
          for (int j = 0; j < m; j++) {
            IASTAppendable col = F.ListAlloc(N);
            for (int i = 1; i <= N; i++) {
              col.append(((IAST) data.get(i)).get(j + 1));
            }
            means[j] = engine.evaluate(F.Mean(col));
          }
        } else if (m == 1) {
          means = new IExpr[1];
          means[0] = engine.evaluate(F.Mean(arg1));
        } else {
          return F.NIL;
        }
      }

      for (int idx = 0; idx < totalSize; idx++) {
        int[] gamma = new int[m];
        int temp = idx;
        int sumGamma = 0;
        for (int j = 0; j < m; j++) {
          gamma[j] = temp / strides[j];
          temp = temp % strides[j];
          sumGamma += gamma[j];
        }

        if (sumGamma < 2) {
          continue;
        }

        IExpr c_gamma = F.NIL;
        if (isList) {
          IASTAppendable sum = F.PlusAlloc(N);
          IAST data = (IAST) arg1;
          for (int i = 1; i <= N; i++) {
            IASTAppendable prod = F.TimesAlloc(m);
            for (int j = 0; j < m; j++) {
              if (gamma[j] == 0)
                continue;
              IExpr elem = isMatrix ? ((IAST) data.get(i)).get(j + 1) : data.get(i);
              IExpr diff = engine.evaluate(F.Subtract(elem, means[j]));
              if (gamma[j] == 1) {
                prod.append(diff);
              } else {
                prod.append(F.Power(diff, F.ZZ(gamma[j])));
              }
            }
            sum.append(prod.argSize() == 1 ? prod.arg1() : prod);
          }
          c_gamma = engine.evaluate(F.Divide(sum, F.ZZ(N)));
        } else {
          IExpr orderArg;
          if (m == 1) {
            orderArg = F.ZZ(gamma[0]);
          } else {
            IASTAppendable gammaList = F.ListAlloc(m);
            for (int g : gamma) {
              gammaList.append(F.ZZ(g));
            }
            orderArg = gammaList;
          }
          // For distributions/symbolic evaluation, fallback to CentralMoment evaluator
          c_gamma = engine.evaluate(F.CentralMoment(arg1, orderArg));
        }
        cArr[idx] = c_gamma;

        int i_choice = 0;
        for (int j = 0; j < m; j++) {
          if (gamma[j] >= 1) {
            i_choice = j;
            break;
          }
        }

        IASTAppendable sum = F.PlusAlloc(16);
        for (int betaIdx = 0; betaIdx < idx; betaIdx++) {
          if (kArr[betaIdx] == null)
            continue;
          int[] beta = new int[m];
          int t2 = betaIdx;
          int sumBeta = 0;
          boolean lessEqual = true;
          for (int j = 0; j < m; j++) {
            beta[j] = t2 / strides[j];
            t2 = t2 % strides[j];
            sumBeta += beta[j];
            if (beta[j] > gamma[j]) {
              lessEqual = false;
              break;
            }
          }

          if (!lessEqual || beta[i_choice] < 1 || sumBeta < 2) {
            continue;
          }

          int diffIdx = 0;
          for (int j = 0; j < m; j++) {
            diffIdx += (gamma[j] - beta[j]) * strides[j];
          }

          IExpr c_diff = cArr[diffIdx];
          if (c_diff == null || c_diff.isZero())
            continue;

          IExpr binomProduct = F.C1;
          for (int j = 0; j < m; j++) {
            int n_val = gamma[j] - (j == i_choice ? 1 : 0);
            int k_val = beta[j] - (j == i_choice ? 1 : 0);
            if (n_val > 0 && k_val > 0 && n_val != k_val) {
              binomProduct =
                  engine.evaluate(F.Times(binomProduct, F.Binomial(F.ZZ(n_val), F.ZZ(k_val))));
            }
          }

          sum.append(F.Times(binomProduct, kArr[betaIdx], c_diff));
        }

        if (sum.argSize() == 0) {
          kArr[idx] = c_gamma;
        } else {
          kArr[idx] = engine.evaluate(F.Subtract(c_gamma, sum));
        }
      }
      return kArr[(int) (totalSize - 1)];
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class FactorialMoment extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        // FactorialMoment(r) represents the order r formal factorial moment.
        return F.NIL;
      }

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();

      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        IntArrayList dimensions = LinearAlgebraUtil.dimensions(list);
        if (dimensions.size() == 0 || dimensions.contains(0)) {
          return F.NIL;
        }

        int N = list.argSize();
        if (N == 0)
          return F.NIL;

        if (arg2.isList()) {
          // Multivariate
          IAST rList = (IAST) arg2;
          int m = rList.argSize();
          if (dimensions.size() == 2 && dimensions.getInt(1) == m) {
            IASTAppendable sum = F.PlusAlloc(N);
            for (int i = 1; i <= N; i++) {
              IAST row = (IAST) list.get(i);
              IASTAppendable prod = F.TimesAlloc(m);
              for (int j = 1; j <= m; j++) {
                IExpr r = rList.get(j);
                if (r.isZero())
                  continue; // FactorialPower(x, 0) is 1
                prod.append(factorialPower2(row.get(j), r));
              }
              IExpr term = F.C1;
              if (prod.argSize() == 1) {
                term = prod.arg1();
              } else if (prod.argSize() > 1) {
                term = prod;
              }
              sum.append(term);
            }
            return engine.evaluate(F.Divide(sum, F.ZZ(N)));
          } else if (dimensions.size() > 2) {
            // Fallback layout reduction for higher dimensional arrays
            return engine.evaluate(F.ArrayReduce(F.Function(F.FactorialMoment(F.Slot1, arg2)), list,
                F.List(F.C1, F.C2)));
          }
        } else {
          // Scalar
          if (arg2.isZero()) {
            if (dimensions.size() == 1)
              return F.C1;
            if (dimensions.size() == 2)
              return F.constantArray(F.C1, dimensions.getInt(1));
          }

          if (dimensions.size() == 1) {
            IASTAppendable sum = F.PlusAlloc(N);
            for (int i = 1; i <= N; i++) {
              sum.append(factorialPower2(list.get(i), arg2));
            }
            return engine.evaluate(F.Divide(sum, F.ZZ(N)));
          } else if (dimensions.size() > 1) {
            return engine
                .evaluate(F.ArrayReduce(F.Function(F.FactorialMoment(F.Slot1, arg2)), list, F.C1));
          }
        }
        return F.NIL;
      }
      return F.NIL;
    }

    private IAST factorialPower2(IExpr arg1, IExpr arg2 ) {
      if (arg2.isInteger() && arg2.isPositive() && arg2.isPositive()
          && ((IInteger) arg2).isLE(F.C4)) {
        return F.FunctionExpand(F.FactorialPower(arg1, arg2));
      }
      return F.FactorialPower(arg1, arg2);
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  private static boolean isVectorMatrixOrDistribution(ISymbol head, IAST list,
      IntArrayList dimensions, EvalEngine engine) {
    if (dimensions.size() == 0 || dimensions.contains(0)) {
      // The first argument `1` is expected to be a vector, matrix or a distribution.
      Errors.printMessage(S.Moment, "arg1", F.list(list), engine);
      return false;
    }
    return true;
  }

  public static void initialize() {
    Initializer.init();
  }

  private StatisticalMomentFunctions() {}
}
