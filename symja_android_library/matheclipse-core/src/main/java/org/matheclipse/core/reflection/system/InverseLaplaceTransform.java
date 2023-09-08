package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import com.google.common.math.DoubleMath;

/**
 *
 *
 * <pre>
 * InverseLaplaceTransform(f, s, t)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the inverse laplace transform.
 *
 * </blockquote>
 *
 * <p>
 * See:
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Laplace_transform">Wikipedia - Laplace transform</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; InverseLaplaceTransform(3/(s-1)+(2*s)/(s^2+4),s,t)
 * 3*E^t+2*Cos(2*t)
 * </pre>
 */
public class InverseLaplaceTransform extends AbstractFunctionEvaluator {

  /**
   * The {@code InverseLaplaceTransformStehfest} implements the numerical calculation of the inverse
   * laplace transform using Stehfest's method.
   * 
   * @see <a href=
   *      "https://www.codeproject.com/Articles/25189/Numerical-Laplace-Transforms-and-Inverse-Transform">
   *      Numerical-Laplace-Transforms-and-Inverse-Transform </a>
   */
  private static class InverseLaplaceTransformStehfest {
    /**
     * Stehfest coefficients
     */
    private final double[] V;

    /**
     * Natural logarithm of 2.0
     */
    final static double ln2 = Math.log(2.0);

    final UnaryNumerical function;

    /**
     * Construct an Inverse Laplace Transform using Talbot's method. The number of Stehfest's
     * coefficients is set to 16.
     */
    InverseLaplaceTransformStehfest(UnaryNumerical function) {
      this(function, 16);
    }

    /**
     * Construct an Inverse Laplace Transform using Talbot's method.
     * 
     * @param n The number of Stehfest's coefficients.
     */
    InverseLaplaceTransformStehfest(UnaryNumerical function, int n) {
      this.function = function;
      int N2 = n / 2;
      int NV = 2 * N2;
      V = new double[NV];
      int sign = 1;
      if ((N2 % 2) != 0)
        sign = -1;
      for (int i = 0; i < NV; i++) {
        int kmin = (i + 2) / 2;
        int kmax = i + 1;
        if (kmax > N2)
          kmax = N2;
        V[i] = 0;
        sign = -sign;
        for (int k = kmin; k <= kmax; k++) {
          V[i] = V[i] + (Math.pow(k, N2) / DoubleMath.factorial(k))
              * (DoubleMath.factorial(2 * k) / DoubleMath.factorial(2 * k - i - 1))
              / DoubleMath.factorial(N2 - k) / DoubleMath.factorial(k - 1)
              / DoubleMath.factorial(i + 1 - k);
        }
        V[i] = sign * V[i];
      }
    }

    /**
     * Perform the Inverse Laplace Transform.
     * 
     * @param function The function to apply the Inverse Laplace to.
     * @param time Argument at which to evaluate the time response.
     * @return {@code L<sup>-1</sup>{Y(s)} = y(t) evaluated at t = time.}
     */
    public double inverseTransform(double time) {
      if (time == 0.0) {
        time = Config.DOUBLE_EPSILON;
      } else if (time == -0.0) {
        time = -Config.DOUBLE_EPSILON;;
      }
      double ln2t = ln2 / time;
      double x = 0;
      double y = 0;
      for (int i = 0; i < V.length; i++) {
        x += ln2t;
        y += V[i] * function.valueLimit(x);
      }
      return ln2t * y;
    }

  }



  public InverseLaplaceTransform() {}

  /** See: <a href="http://www.solitaryroad.com/c913.html">Inverse Laplace transforms</a> */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr a1 = ast.arg1();
    IExpr s = ast.arg2();
    IExpr t = ast.arg3();
    if (!s.isList() && !t.isList() && !s.equals(t)) {
      if (t instanceof INum && s.isSymbol()) {
        double tDouble = t.evalf();
        return numericInverseLaplaceTransform(a1, s, tDouble, engine);
      }
      if (a1.isFree(s)) {
        return F.Times(a1, F.DiracDelta(t));
      }
      if (ast.arg1().isAST()) {
        IAST arg1 = (IAST) ast.arg1();
        if (arg1.isTimes()) {
          IASTAppendable result = F.TimesAlloc(arg1.size());
          IASTAppendable rest = F.TimesAlloc(arg1.size());
          arg1.filter(result, rest, x -> x.isFree(s));
          if (result.size() > 1) {
            return F.Times(result.oneIdentity1(), F.InverseLaplaceTransform(rest, s, t));
          }
        }
        if (arg1.isTimes() || arg1.isPower()) {
          IExpr[] parts = Algebra.fractionalParts(arg1, false);
          if (parts != null) {
            IExpr temp = Algebra.partsApart(parts, s, engine);
            // IExpr temp = Algebra.partialFractionDecompositionRational(new
            // PartialFractionGenerator(),
            // parts,s);
            if (temp.isPlus()) {
              return temp.mapThread(F.InverseLaplaceTransform(F.Slot1, s, t), 1);
            }
          }
        }
        if (arg1.isPlus()) {
          // InverseLaplaceTransform[a_+b_+c_,s_,t_] ->
          // InverseLaplaceTransform[a,s,t]+InverseLaplaceTransform[b,s,t]+InverseLaplaceTransform[c,s,t]
          return arg1.mapThread(F.InverseLaplaceTransform(F.Slot1, s, t), 1);
        }
      }
    }
    return F.NIL;
  }

  private static IExpr numericInverseLaplaceTransform(IExpr function, IExpr s, double t,
      EvalEngine engine) {
    final IAST cacheKey = F.List(S.InverseLaplaceTransform, function, s);
    Object value = engine.getObjectCache(cacheKey);
    final InverseLaplaceTransformStehfest laplace;
    if (value instanceof InverseLaplaceTransformStehfest) {
      laplace = (InverseLaplaceTransformStehfest) value;
    } else {
      final UnaryNumerical unaryNumerical = new UnaryNumerical(function, (ISymbol) s, engine);
      laplace = new InverseLaplaceTransformStehfest(unaryNumerical);
      engine.putObjectCache(cacheKey, laplace);
    }
    return F.num(laplace.inverseTransform(t));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
