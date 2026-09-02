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
import org.matheclipse.core.interfaces.statistics.IStatistics;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICentralMoment;
import org.matheclipse.core.interfaces.statistics.IDiscreteDistribution;
import org.matheclipse.core.interfaces.statistics.IGeneratingFunction;
import org.matheclipse.external.fastutil.ints.IntArrayList;

public class StatisticalMomentFunctions {
  private static class Initializer {

    private static void init() {
      S.CentralMomentGeneratingFunction.setEvaluator(new CentralMomentGeneratingFunction());
      S.CharacteristicFunction.setEvaluator(new CharacteristicFunction());
      S.Cumulant.setEvaluator(new Cumulant());
      S.CumulantGeneratingFunction.setEvaluator(new CumulantGeneratingFunction());
      S.CentralMoment.setEvaluator(new CentralMoment());
      S.FactorialMoment.setEvaluator(new FactorialMoment());
      S.FactorialMomentGeneratingFunction.setEvaluator(new FactorialMomentGeneratingFunction());
      S.Moment.setEvaluator(new Moment());
      S.MomentGeneratingFunction.setEvaluator(new MomentGeneratingFunction());
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

      if (arg1.isAST() && arg1.isDistribution()) {
        IStatistics statistics = ((IAST) arg1).headInstanceOf(IStatistics.class);
        if (statistics != null) {
          IExpr moment = statistics.moment((IAST) arg1, arg2);
          if (moment.isPresent()) {
            return engine.evaluate(moment);
          }
        }
        return F.NIL;
      }

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
          int r = rList.get(i).toMachineInt();
          if (r < 0) {
            return F.NIL;
          }
          R[i - 1] = r;
        }
      } else {
        int r = arg2.toMachineInt();
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

    private IAST factorialPower2(IExpr arg1, IExpr arg2) {
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


  /** The {@link IGeneratingFunction} implementation of the distribution head or null. */
  private static IGeneratingFunction generatingFunction(IExpr distribution) {
    if (distribution.isAST() && distribution.isDistribution()) {
      return ((IAST) distribution).headInstanceOf(IGeneratingFunction.class);
    }
    return null;
  }

  /**
   * The probability (factorial moment) generating function <code>E[z^X]</code>: the distributions
   * {@link IGeneratingFunction#pgf(IAST, IExpr, EvalEngine)} implementation or - for a discrete
   * distribution with a small finite support - the exact enumeration <code>Sum(z^k*pmf(k))</code>.
   */
  private static IExpr probabilityGeneratingFunction(IExpr distribution, IExpr z,
      EvalEngine engine) {
    IGeneratingFunction gf = generatingFunction(distribution);
    if (gf != null) {
      IExpr pgf = gf.pgf((IAST) distribution, z, engine);
      if (pgf.isPresent()) {
        return pgf;
      }
    }
    if (distribution.isDiscreteDistribution()) {
      IDiscreteDistribution dist = StatisticsFunctions.getDiscreteDistribution(distribution);
      long lo = DistributionRegion.supportLowerBound(dist, distribution);
      long hi = DistributionRegion.supportUpperBound(dist, distribution);
      long[] windows = new long[] {lo, hi};
      if (DistributionRegion
          .countWindows(windows) <= DistributionRegion.SYMBOLIC_ENUMERATION_LIMIT) {
        ISymbol x = F.Dummy("x");
        IExpr pdf = S.PDF.ofNIL(engine, distribution, x);
        if (pdf.isPresent()) {
          return DistributionRegion.enumerateSymbolic(F.Times(F.Power(z, x), pdf), x, windows,
              F.NIL, engine);
        }
      }
    }
    return F.NIL;
  }

  /**
   * The moment generating function <code>E[E^(t*X)]</code>: the distributions
   * {@link IGeneratingFunction#mgf(IAST, IExpr, EvalEngine)} implementation or the probability
   * generating function at <code>E^t</code>.
   */
  private static IExpr momentGeneratingFunction(IExpr distribution, IExpr t, EvalEngine engine) {
    IGeneratingFunction gf = generatingFunction(distribution);
    if (gf != null) {
      IExpr mgf = gf.mgf((IAST) distribution, t, engine);
      if (mgf.isPresent()) {
        return mgf;
      }
    }
    return probabilityGeneratingFunction(distribution, F.Exp(t), engine);
  }

  /** <code>Mean(termMap(x_k))</code> for the empirical distribution of a data list. */
  private static IExpr dataGeneratingFunction(IAST data,
      java.util.function.Function<IExpr, IExpr> termMap) {
    IASTAppendable sum = F.PlusAlloc(data.size());
    for (int i = 1; i < data.size(); i++) {
      sum.append(termMap.apply(data.get(i)));
    }
    return F.Divide(sum, F.ZZ(data.argSize()));
  }

  /**
   * Formal logarithm of a generating function: <code>Log(E^f) == f</code> and the logarithm of a
   * product is expanded into a sum, so that e.g. the cumulant generating function of the normal
   * distribution is returned as <code>t*m+t^2*s^2/2</code> and not as
   * <code>Log(E^(t*m+t^2*s^2/2))</code>.
   */
  private static IExpr logGeneratingFunction(IExpr mgf) {
    if (mgf.isPower() && mgf.base() == S.E) {
      return mgf.exponent();
    }
    if (mgf.isTimes()) {
      IAST times = (IAST) mgf;
      IASTAppendable sum = F.PlusAlloc(times.size());
      for (int i = 1; i < times.size(); i++) {
        sum.append(logGeneratingFunction(times.get(i)));
      }
      return sum;
    }
    return F.Log(mgf);
  }

  /**
   * <code>MomentGeneratingFunction(dist, t)</code> - the moment generating function
   * <code>E[E^(t*X)]</code> of the distribution or of a data list. The result is a formal closed
   * form without convergence conditions.
   */
  private static class MomentGeneratingFunction extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      final IExpr t = ast.arg2();
      if (arg1.isList()) {
        // the empirical moment generating function of a data list
        return dataGeneratingFunction((IAST) arg1, x -> F.Exp(F.Times(t, x)));
      }
      return momentGeneratingFunction(arg1, t, engine);
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

  /**
   * <code>CharacteristicFunction(dist, t)</code> - the characteristic function
   * <code>E[E^(I*t*X)]</code> of the distribution or of a data list.
   */
  private static class CharacteristicFunction extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      final IExpr t = ast.arg2();
      if (arg1.isList()) {
        return dataGeneratingFunction((IAST) arg1, x -> F.Exp(F.Times(F.CI, t, x)));
      }
      IGeneratingFunction gf = generatingFunction(arg1);
      if (gf != null) {
        IExpr cf = gf.cf((IAST) arg1, t, engine);
        if (cf.isPresent()) {
          return cf;
        }
      }
      return momentGeneratingFunction(arg1, F.Times(F.CI, t), engine);
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

  /**
   * <code>FactorialMomentGeneratingFunction(dist, z)</code> - the probability generating function
   * <code>E[z^X]</code> of the distribution or of a data list.
   */
  private static class FactorialMomentGeneratingFunction extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      final IExpr z = ast.arg2();
      if (arg1.isList()) {
        return dataGeneratingFunction((IAST) arg1, x -> F.Power(z, x));
      }
      IExpr pgf = probabilityGeneratingFunction(arg1, z, engine);
      if (pgf.isPresent()) {
        return pgf;
      }
      IGeneratingFunction gf = generatingFunction(arg1);
      if (gf != null) {
        IExpr mgf = gf.mgf((IAST) arg1, F.Log(z), engine);
        if (mgf.isPresent()) {
          return mgf;
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

  /**
   * <code>CentralMomentGeneratingFunction(dist, t)</code> -
   * <code>E[E^(t*(X - mean))] == E^(-t*mean) * MomentGeneratingFunction(dist, t)</code>.
   */
  private static class CentralMomentGeneratingFunction extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      final IExpr t = ast.arg2();
      if (arg1.isList()) {
        IExpr mean = engine.evaluate(F.Mean(arg1));
        return dataGeneratingFunction((IAST) arg1, x -> F.Exp(F.Times(t, F.Subtract(x, mean))));
      }
      IExpr mgf = momentGeneratingFunction(arg1, t, engine);
      if (mgf.isPresent()) {
        IStatistics statistics = ((IAST) arg1).headInstanceOf(IStatistics.class);
        if (statistics != null) {
          IExpr mean = statistics.mean((IAST) arg1);
          if (mean.isPresent()) {
            return F.Times(F.Exp(F.Times(F.CN1, t, mean)), mgf);
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

  /**
   * <code>CumulantGeneratingFunction(dist, t)</code> -
   * <code>Log(MomentGeneratingFunction(dist, t))</code>.
   */
  private static class CumulantGeneratingFunction extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      final IExpr t = ast.arg2();
      if (arg1.isList()) {
        return F.Log(dataGeneratingFunction((IAST) arg1, x -> F.Exp(F.Times(t, x))));
      }
      IExpr mgf = momentGeneratingFunction(arg1, t, engine);
      if (mgf.isPresent()) {
        return logGeneratingFunction(engine.evaluate(mgf));
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

  private static boolean isVectorMatrixOrDistribution(ISymbol head, IAST list,
      IntArrayList dimensions, EvalEngine engine) {
    if (dimensions.size() == 0 || dimensions.contains(0)) {
      // The first argument `1` is expected to be `1`.
      Errors.printMessage(S.Moment, "arg1",
          F.List(list, F.stringx("a vector, matrix or a distribution")), engine);
      return false;
    }
    return true;
  }

  public static void initialize() {
    Initializer.init();
  }

  private StatisticalMomentFunctions() {}
}
