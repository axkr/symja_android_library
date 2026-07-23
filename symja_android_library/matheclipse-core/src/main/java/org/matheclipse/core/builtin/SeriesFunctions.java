package org.matheclipse.core.builtin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.reflection.system.rulesets.SeriesCoefficientRules;
import com.google.common.base.Suppliers;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.ps.PolynomialTaylorFunction;
import edu.jas.ps.TaylorFunction;
import edu.jas.ps.UnivPowerSeriesRing;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;
import edu.jas.ufd.QuotientTaylorFunction;

public class SeriesFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {

      if (ToggleFeature.SERIES) {
        S.ComposeSeries.setEvaluator(new ComposeSeries());
        S.InverseSeries.setEvaluator(new InverseSeries());
        S.PadeApproximant.setEvaluator(new PadeApproximant());
        S.Residue.setEvaluator(new Residue());
        S.Series.setEvaluator(new Series());
        S.SeriesCoefficient.setEvaluator(new SeriesCoefficient());
        S.SeriesData.setEvaluator(new SeriesData());
      }
    }
  }

  private static final class PadeApproximant extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg2().isList3()) {
        IExpr function = ast.arg1();
        IAST list = (IAST) ast.arg2();
        IExpr x = list.arg1();
        // Add validation to ensure x is a valid variable
        if (!x.isVariable()) {
          // `1` is not a valid variable.
          return Errors.printMessage(S.PadeApproximant, "ivar", F.List(x), engine);
        }
        IExpr x0 = list.arg2();
        IExpr orderArg = list.arg3();

        int m = -1;
        int n = -1;

        if (orderArg.isList2()) {
          m = ((IAST) orderArg).arg1().toIntDefault();
          n = ((IAST) orderArg).arg2().toIntDefault();
        } else if (orderArg.isInteger()) {
          // If only a single order `m` is given, default to {m, m}
          m = orderArg.toIntDefault();
          n = m;
        }

        if (F.isNotPresent(m) || F.isNotPresent(n) || m < 0 || n < 0) {
          return F.NIL;
        }

        try {
          // Exact fast path with rational arithmetic for polynomials and rational functions.
          // Broaden fractional check beyond just `function.isTimes()`
          Optional<IExpr[]> numeratorDenominatorParts =
              AlgebraUtil.fractionalParts(function, false);

          if (numeratorDenominatorParts.isPresent()) {
            IExpr[] parts = numeratorDenominatorParts.get();
            // Route to quotientTaylorFunction if a distinct denominator exists
            if (!parts[1].isOne()) {
              return quotientTaylorFunction(parts, x, x0, m, n, engine);
            }
          }

          return taylorFunction(function, x, x0, m, n, engine);
        } catch (JASConversionException | ArithmeticException ex) {
          // `function` isn't a rational function with rational coefficients, or it has a pole at
          // `x0`. Fall back to the series expansion, which also copes with symbolic coefficients.
          return seriesPade(function, x, x0, m, n, engine).orElse(function);
        }
      }
      return F.NIL;
    }

    /**
     * Compute the Pade approximant of order <code>{m, n}</code> from the series expansion of
     * <code>function</code> at <code>x0</code>.
     *
     * <p>
     * The Taylor coefficients are used as symbolic {@link IExpr} coefficients, so this also works
     * for a function with unknown coefficients like <code>f(x)</code>, whose Taylor coefficients are
     * the derivatives <code>f(x0), f'(x0), f''(x0)/2, ...</code>
     *
     * <p>
     * If the series has a pole of order <code>p</code> at <code>x0</code>, the approximant of the
     * regular function <code>(x-x0)^p*function</code> of order <code>{m, n-p}</code> is computed and
     * the pole is factored back into the denominator. The numerator degree therefore stays
     * <code>&lt;= m</code> and the denominator degree stays <code>&lt;= n</code>.
     *
     * @return {@link F#NIL} if no approximant could be computed
     */
    private static IExpr seriesPade(IExpr function, IExpr x, IExpr x0, int m, int n,
        EvalEngine engine) {
      IExpr seriesExpr = engine.evaluate(F.Series(function, F.List(x, x0, F.ZZ(m + n))));
      if (!(seriesExpr instanceof ASTSeriesData)) {
        return F.NIL;
      }
      ASTSeriesData series = (ASTSeriesData) seriesExpr;
      if (series.puiseuxDenominator() != 1) {
        // The series is flagged Puiseux (e.g. because of a Sqrt), but functions such as
        // Sqrt(1+x) or Cos(Sqrt(x)) still expand into integer powers of x. When the normal
        // form is an ordinary polynomial in x, the rational approximant is well defined, so
        // compute it from that truncated Taylor polynomial (whose coefficients are exactly the
        // ones a Pade approximant of order {m, n} consumes).
        IExpr normal = engine.evaluate(F.Normal(seriesExpr));
        if (!normal.equals(function) && engine.evaluate(F.PolynomialQ(normal, x)).isTrue()) {
          return engine.evaluate(
              F.PadeApproximant(normal, F.List(x, x0, F.List(F.ZZ(m), F.ZZ(n)))));
        }
        // a genuine Puiseux series has fractional exponents a rational approximant can't represent
        return F.NIL;
      }
      int poleOrder = series.minExponent() < 0 ? -series.minExponent() : 0;
      if (n < poleOrder) {
        // the requested denominator degree is too small to cover the pole
        return F.NIL;
      }

      // Taylor coefficients a[k] of the regular function `(x-x0)^poleOrder * function`
      final int denominatorDegree = n - poleOrder;
      IExpr[] a = new IExpr[m + denominatorDegree + 1];
      for (int k = 0; k < a.length; k++) {
        a[k] = series.coefficient(k - poleOrder);
      }

      IExpr[] q = padeDenominatorCoefficients(a, m, denominatorDegree, engine);
      if (q == null) {
        return F.NIL;
      }
      IExpr t = x0.isZero() ? x : F.Subtract(x, x0);

      // numerator: Sum(p[k]*t^k, {k, 0, m}) with p[k] = Sum(a[k-j]*q[j], {j, 0, Min(k, n)})
      IASTAppendable numerator = F.PlusAlloc(m + 1);
      for (int k = 0; k <= m; k++) {
        int last = Math.min(k, denominatorDegree);
        IASTAppendable p = F.PlusAlloc(last + 1);
        for (int j = 0; j <= last; j++) {
          p.append(F.Times(coefficient(a, k - j), q[j]));
        }
        numerator.append(F.Times(engine.evaluate(p), F.Power(t, F.ZZ(k))));
      }

      // denominator: 1 + Sum(q[j]*t^j, {j, 1, n})
      IASTAppendable denominator = F.PlusAlloc(denominatorDegree + 1);
      for (int j = 0; j <= denominatorDegree; j++) {
        denominator.append(F.Times(q[j], F.Power(t, F.ZZ(j))));
      }

      IExpr num = collectCoefficients(engine.evaluate(numerator), x, engine);
      IExpr den = collectCoefficients(engine.evaluate(denominator), x, engine);
      if (poleOrder > 0) {
        den = F.Times(F.Power(t, F.ZZ(poleOrder)), den);
      }
      return F.Divide(num, den);
    }

    /**
     * Solve the linear system for the denominator coefficients of the Pade approximant, normalized
     * to <code>q[0] == 1</code>:
     *
     * <pre>
     * Sum(a[k-j]*q[j], {j, 0, n}) == 0   for k = m+1, ..., m+n
     * </pre>
     *
     * @param a the Taylor coefficients <code>a[0], ..., a[m+n]</code>
     * @param m degree of the approximant numerator
     * @param n degree of the approximant denominator
     * @return the coefficients <code>q[0], ..., q[n]</code> or <code>null</code> if the system has no
     *         solution
     */
    private static IExpr[] padeDenominatorCoefficients(IExpr[] a, int m, int n, EvalEngine engine) {
      IExpr[] q = new IExpr[n + 1];
      q[0] = F.C1;
      if (n == 0) {
        return q;
      }

      IASTAppendable matrix = F.ListAlloc(n);
      IASTAppendable vector = F.ListAlloc(n);
      for (int i = 1; i <= n; i++) {
        IASTAppendable row = F.ListAlloc(n);
        for (int j = 1; j <= n; j++) {
          row.append(coefficient(a, m + i - j));
        }
        matrix.append(row);
        vector.append(engine.evaluate(F.Negate(coefficient(a, m + i))));
      }

      // a rank deficient system is solved for one particular solution, which reduces the order of
      // the approximant; `LinearSolve` reports the deficiency in messages which aren't relevant here
      final boolean quietMode = engine.isQuietMode();
      IExpr solution;
      try {
        engine.setQuietMode(true);
        solution = engine.evaluate(F.LinearSolve(matrix, vector));
      } finally {
        engine.setQuietMode(quietMode);
      }
      if (!solution.isList() || solution.argSize() != n) {
        return null;
      }

      IAST solutionList = (IAST) solution;
      for (int j = 1; j <= n; j++) {
        q[j] = solutionList.get(j);
      }
      return q;
    }

    /**
     * Return the Taylor coefficient <code>a[k]</code>, or <code>0</code> if <code>k</code> is outside
     * the computed range.
     */
    private static IExpr coefficient(IExpr[] a, int k) {
      return (k < 0 || k >= a.length) ? F.C0 : a[k];
    }

    /**
     * Helper method to evaluate the denominator at the expansion point x0 and normalize the Pade
     * approximant fraction.
     */
    private static IExpr normalizePade(IExpr num, IExpr den, IExpr x, IExpr x0, EvalEngine engine) {
      IExpr constantFactor = engine.evaluate(F.ReplaceAll(den, F.Rule(x, x0)));
      if (!constantFactor.isZero() && !constantFactor.isOne()) {
        num = engine.evaluate(F.Expand(F.Divide(num, constantFactor)));
        den = engine.evaluate(F.Expand(F.Divide(den, constantFactor)));
      }
      return F.Divide(collectCoefficients(num, x, engine), collectCoefficients(den, x, engine));
    }

    /**
     * Collect <code>poly</code> in powers of <code>x</code> and apply <code>Together</code> to each
     * coefficient.
     *
     * <p>
     * The linear system which is solved for the Pade approximant divides by the series coefficients.
     * For symbolic coefficients (for example the Taylor coefficients <code>f'(0), f''(0),...</code>
     * of an unknown function <code>f</code>) that leaves the result as a sum of deeply nested
     * fractions. Collecting the powers of <code>x</code> and combining each coefficient over a
     * common denominator recovers the compact form. For purely numeric coefficients this is a no-op.
     */
    private static IExpr collectCoefficients(IExpr poly, IExpr x, EvalEngine engine) {
      return engine.evaluate(F.ternaryAST3(S.Collect, poly, x, S.Together));
    }

    /**
     * Pade approximant of the rational function
     * <code>numeratorDenominatorParts[0]/numeratorDenominatorParts[1]</code>, computed with exact
     * rational arithmetic.
     *
     * @throws JASConversionException if numerator and denominator aren't polynomials in
     *         <code>x</code> with rational coefficients, or <code>x0</code> isn't a rational number
     */
    private static IExpr quotientTaylorFunction(IExpr[] numeratorDenominatorParts, IExpr x, IExpr x0,
        final int m, final int n, EvalEngine engine) throws JASConversionException {
      if (!x0.isRational()) {
        throw JASConversionException.FAILED;
      }
      BigRational bf = ((IRational) x0).toBigRational();
      UnivPowerSeriesRing<BigRational> fac = new UnivPowerSeriesRing<>(BigRational.ZERO);
      JASConvert<BigRational> jas = new JASConvert<>(x.makeList(), BigRational.ZERO);

      GenPolynomial<BigRational> numerator = jas.expr2JAS(numeratorDenominatorParts[0], false);
      GenPolynomial<BigRational> denominator = jas.expr2JAS(numeratorDenominatorParts[1], false);
      if (numerator == null || denominator == null) {
        throw JASConversionException.FAILED;
      }

      QuotientRing<BigRational> qr = new QuotientRing<>(fac.polyRing());
      TaylorFunction<BigRational> TF =
          new QuotientTaylorFunction<>(new Quotient<>(qr, numerator, denominator));

      Quotient<BigRational> approximant = PolyUfdUtil.approximantOfPade(fac, TF, bf, m, n);
      IExpr numExpr = jas.rationalPoly2Expr(approximant.num, false);
      IExpr denExpr = jas.rationalPoly2Expr(approximant.den, false);

      return normalizePade(numExpr, denExpr, x, x0, engine);
    }

    /**
     * Pade approximant of the polynomial <code>function</code>, computed with exact rational
     * arithmetic.
     *
     * @throws JASConversionException if <code>function</code> isn't a polynomial in <code>x</code>
     *         with rational coefficients, or <code>x0</code> isn't a rational number
     */
    private static IExpr taylorFunction(IExpr function, IExpr x, IExpr x0, int m, int n,
        EvalEngine engine) throws JASConversionException {
      if (!x0.isRational()) {
        throw JASConversionException.FAILED;
      }
      BigRational expansionPoint = ((IRational) x0).toBigRational();
      JASConvert<BigRational> jas = new JASConvert<>(x.makeList(), BigRational.ZERO);
      GenPolynomial<BigRational> numerator = jas.expr2JAS(function, false);
      if (numerator == null) {
        throw JASConversionException.FAILED;
      }

      UnivPowerSeriesRing<BigRational> fac = new UnivPowerSeriesRing<>(BigRational.ZERO);
      TaylorFunction<BigRational> TF = new PolynomialTaylorFunction<>(numerator);

      Quotient<BigRational> approximant =
          PolyUfdUtil.approximantOfPade(fac, TF, expansionPoint, m, n);
      IExpr numExpr = jas.rationalPoly2Expr(approximant.num, false);
      IExpr denExpr = jas.rationalPoly2Expr(approximant.den, false);

      return normalizePade(numExpr, denExpr, x, x0, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }
  }


  /**
   *
   *
   * <pre>
   * ComposeSeries(series1, series2)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * substitute <code>series2</code> into <code>series1</code>
   *
   * </blockquote>
   *
   * <pre>
   * ComposeSeries(series1, series2, series3)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return multiple series composed.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ComposeSeries(SeriesData(x, 0, {1, 3}, 2, 4, 1), SeriesData(x, 0, {1, 1,0,0}, 0, 4, 1) - 1)
   * x^2+3*x^3+O(x)^4
   * </pre>
   */
  private static final class ComposeSeries extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() > 2) {
        if (ast.arg1() instanceof ASTSeriesData) {
          ASTSeriesData result = (ASTSeriesData) ast.arg1();
          for (int i = 2; i < ast.size(); i++) {
            if (ast.get(i) instanceof ASTSeriesData) {
              ASTSeriesData s2 = (ASTSeriesData) ast.get(i);
              result = result.composeCapped(s2);
              if (result == null) {
                return F.NIL;
              }
            }
          }
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }


  /**
   *
   *
   * <pre>
   * InverseSeries(series)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the inverse series.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; InverseSeries(Series(Sin(x), {x, 0, 7}))
   * x+x^3/6+3/40*x^5+5/112*x^7+O(x)^8
   * </pre>
   */
  /**
   * <pre>
   * InverseSeries(series)
   * </pre>
   * 
   * <blockquote>
   * <p>
   * return the inverse series.
   * </p>
   * </blockquote> *
   * 
   * <pre>
   * InverseSeries(f(x), x)
   * </pre>
   * 
   * <blockquote>
   * <p>
   * generates the inverse power series of the function f(x) using the Lagrange-Bürmann formula.
   * </p>
   * </blockquote>
   */
  private static final class InverseSeries extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      // 1. Invertierung eines bereits generierten ASTSeriesData Objekts
      if (ast.arg1() instanceof ASTSeriesData) {
        ASTSeriesData ps = (ASTSeriesData) ast.arg1();
        IExpr variable = ps.expansionVariable();
        if (ast.isAST2()) {
          variable = ast.arg2();
        }
        IExpr temp = ps.reversion(variable, engine);
        if (temp.isPresent()) {
          return temp;
        }
      }
      // 2. Direkte analytische Invertierung einer Funktion f(x) via Lagrange-Bürmann
      else if (ast.isAST2() && ast.arg2().isSymbol()) {
        IExpr f = ast.arg1();
        IExpr x = ast.arg2();

        // prüfen, ob f(0) == 0 gilt
        IExpr f0 = engine.evaluate(F.subst(f, x, F.C0));
        if (f0.isZero()) {
          int order = 6;
          IASTAppendable coeffs = F.ListAlloc(order);

          for (int i = 1; i < order; i++) {
            IExpr c = ASTSeriesData.lagrangeBurmannCoefficient(f, x, F.C0, F.ZZ(i), engine);
            if (c.isPresent()) {
              coeffs.append(c);
            } else {
              return F.NIL;
            }
          }

          // Konstruiert exakt ein SeriesData Objekt bis zur Ordnung O(x^6)
          return new ASTSeriesData(x, F.C0, coeffs, 1, order, 1);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }
  }

  private static final class Residue extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2() && (ast.arg2().isList2())) {
        IExpr function = ast.arg1();
        IAST list = (IAST) ast.arg2();

        IExpr x = list.arg1();
        IExpr x0 = list.arg2();
        if (!x.isVariable()) {
          // `1` is not a valid variable.
          return Errors.printMessage(S.Residue, "ivar", F.List(x), engine);
        }

        // Handle Residue at Infinity
        // Res(f(x), {x, Infinity}) = -Res(1/x^2 * f(1/x), {x, 0})
        if (x0.isDirectedInfinity()) {
          IExpr subFunc = engine.evaluate(F.subst(function, x, F.Power(x, F.CN1)));
          IExpr newFunc = engine.evaluate(F.Times(F.CN1, F.Power(x, F.CN2), subFunc));
          return engine.evaluate(F.Residue(newFunc, F.List(x, F.C0)));
        }

        // Fast-Path: Try direct evaluation via our optimized SeriesCoefficient
        IExpr coeff = engine.evaluate(F.SeriesCoefficient(function, F.List(x, x0, F.CN1)));
        if (coeff.isPresent() && coeff.isFree(S.SeriesCoefficient, true)) {
          if (!coeff.isFree(f -> f.isIndeterminate() || f.isDirectedInfinity(), true)) {
            // the -1 coefficient isn't determined - a residue is finite by definition, so don't
            // guess with the series fallback either
            return F.NIL;
          }
          return coeff.isNumber() ? coeff : engine.evaluate(F.Together(coeff));
        }

        // Essential singularity: if the function is analytic on the whole punctured plane, its
        // Laurent series around x0 is the expansion at Infinity, which the Series() code handles.
        IExpr essential = essentialSingularityResidue(function, x, x0, engine);
        if (essential.isPresent()) {
          return essential;
        }

        // Robust Fallback: Compute the Laurent series explicitly up to O((x-x0)^0)
        // and extract the precise -1 coefficient using ASTSeriesData properties.
        IExpr series = engine.evaluate(F.Series(function, F.List(x, x0, F.C0)));
        if (series instanceof ASTSeriesData) {
          ASTSeriesData seriesData = (ASTSeriesData) series;
          return engine.evaluate(F.Together(seriesData.coefficient(-1)));
        }
      }
      return F.NIL;
    }

    /**
     * Compute the residue of a function with an essential singularity at <code>x0</code>.
     *
     * <p>
     * If <code>function</code> is analytic in the whole punctured plane <code>0 &lt; |x-x0|</code>,
     * its Laurent series around <code>x0</code> is unique and identical to the expansion at
     * <code>Infinity</code>, which <code>Series()</code> computes. That expansion is indexed in
     * <code>w == 1/(x-x0)</code>, so the residue is the coefficient of <code>w^1</code>.
     *
     * @return {@link F#NIL} if the function isn't recognized as analytic on the punctured plane or
     *         no usable series could be computed
     */
    private static IExpr essentialSingularityResidue(IExpr function, IExpr x, IExpr x0,
        EvalEngine engine) {
      IExpr shifted = function;
      if (!x0.isZero()) {
        if (!x0.isFree(x) || x0.isDirectedInfinity()) {
          return F.NIL;
        }
        shifted = engine.evaluate(F.subst(function, x, F.Plus(x0, x)));
      }
      if (!isAnalyticOnPuncturedPlane(shifted, x)) {
        return F.NIL;
      }

      boolean quietMode = engine.isQuietMode();
      try {
        engine.setQuietMode(true);
        for (int order : new int[] {4, 12}) {
          IExpr series = engine.evaluate(F.Series(shifted, F.List(x, F.CInfinity, F.ZZ(order))));
          if (series instanceof ASTSeriesData) {
            ASTSeriesData seriesData = (ASTSeriesData) series;
            if (seriesData.puiseuxDenominator() == 1 && seriesData.truncateOrder() >= 2) {
              return engine.evaluate(F.Together(seriesData.coefficient(1)));
            }
          } else {
            // the expansion at Infinity isn't a Laurent series at all - retrying won't help
            break;
          }
        }
      } finally {
        engine.setQuietMode(quietMode);
      }
      return F.NIL;
    }

    /**
     * Test if <code>expr</code> is analytic in the punctured plane <code>0 &lt; |x| &lt; Infinity
     * </code>, i.e. if <code>x == 0</code> is its only singularity in the finite complex plane.
     *
     * <p>
     * The test is deliberately conservative - it only accepts rational monomials in <code>x</code>
     * and entire functions applied to such arguments.
     */
    private static boolean isAnalyticOnPuncturedPlane(IExpr expr, IExpr x) {
      if (expr.isFree(x, true) || expr.equals(x)) {
        return true;
      }
      if (expr.isPower()) {
        IExpr base = expr.base();
        IExpr exponent = expr.exponent();
        if (base.isFree(x, true)) {
          // b^f(x) == Exp(f(x)*Log(b)) is entire in f(x). This is the Exp() case, because
          // Exp(f(x)) is represented as Power(E, f(x)).
          return !base.isZero() && isAnalyticOnPuncturedPlane(exponent, x);
        }
        if (!exponent.isInteger()) {
          return false;
        }
        if (exponent.isNegative()) {
          // 1/f(x) introduces a pole at every zero of f(x) - only monomials c*x^n are safe
          return isMonomial(base, x);
        }
        return isAnalyticOnPuncturedPlane(base, x);
      }
      if (expr.isPlus() || expr.isTimes()) {
        IAST ast = (IAST) expr;
        for (int i = 1; i < ast.size(); i++) {
          if (!isAnalyticOnPuncturedPlane(ast.get(i), x)) {
            return false;
          }
        }
        return true;
      }
      if (expr.isAST1() && expr.head().isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) expr.head()).ordinal()) {
          case ID.Sin:
          case ID.Cos:
          case ID.Sinh:
          case ID.Cosh:
          case ID.Erf:
          case ID.Erfi:
            return isAnalyticOnPuncturedPlane(expr.first(), x);
          default:
            return false;
        }
      }
      return false;
    }

    /**
     * Test if <code>expr</code> is of the form <code>c * x^n</code> with <code>c</code> free of
     * <code>x</code> and integer <code>n</code>.
     */
    private static boolean isMonomial(IExpr expr, IExpr x) {
      if (expr.isFree(x, true)) {
        return true;
      }
      if (expr.equals(x)) {
        return true;
      }
      if (expr.isPower()) {
        return expr.base().equals(x) && expr.exponent().isInteger();
      }
      if (expr.isTimes()) {
        IAST times = (IAST) expr;
        for (int i = 1; i < times.size(); i++) {
          if (!isMonomial(times.get(i), x)) {
            return false;
          }
        }
        return true;
      }
      return false;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   *
   * <pre>
   * Series(expr, {x, x0, n})
   * </pre>
   *
   * <blockquote>
   * <p>
   * create a power series of <code>expr</code> up to order <code>(x- x0)^n</code> at the point
   * <code>x = x0</code> </blockquote>
   */
  private static final class Series extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 2) {
        IExpr currentExpr = ast.arg1();

        // Process iterators from LEFT to RIGHT to build the SeriesData hierarchy correctly!
        // Series(f, {x, x0, nx}, {y, y0, ny}) -> expands x first, then y.
        // This mathematically ensures x is the outer-most boundary.
        for (int i = 2; i <= ast.argSize(); i++) {
          IExpr iterator = ast.get(i);

          if (iterator.isList3()) {
            IAST list = (IAST) iterator;
            IExpr x = list.arg1();
            IExpr x0 = list.arg2();
            if (!x.isVariable()) {
              return Errors.printMessage(S.Series, "ivar", F.List(x), engine);
            }
            final int n = list.arg3().toIntDefault();
            if (F.isNotPresent(n)) {
              return F.NIL;
            }
            if (currentExpr.isFree(x)) {
              continue; // Optimization: if free of x, series in x is the expression itself
            }

            // Handle ASTSeriesData multi-variate mapping natively
            if (currentExpr instanceof ASTSeriesData) {
              ASTSeriesData outerSeries = (ASTSeriesData) currentExpr;
              if (!outerSeries.expansionVariable().equals(x)) {
                // Multivariate: map the new series iterator over the coefficients
                IAST coefficients = outerSeries.arg3();
                IASTAppendable newCoeffs = F.ListAlloc(coefficients.argSize());
                for (int j = 1; j <= coefficients.argSize(); j++) {
                  IExpr coeffSeries = engine.evaluate(F.Series(coefficients.get(j), iterator));
                  newCoeffs.append(coeffSeries);
                }
                currentExpr = new ASTSeriesData(outerSeries.expansionVariable(),
                    outerSeries.expansionPoint(), newCoeffs, outerSeries.minExponent(),
                    outerSeries.truncateOrder(), outerSeries.puiseuxDenominator());
                continue;
              } else {
                // Re-expanding in the same variable
                if (outerSeries.expansionPoint().equals(x0)) {
                  int targetTruncate =
                      n * outerSeries.puiseuxDenominator() + outerSeries.truncationOffset();
                  if (targetTruncate >= outerSeries.truncateOrder()) {
                    continue; // Cannot increase precision of existing series
                  } else {
                    int newNMin = Math.min(outerSeries.minExponent(), targetTruncate);
                    int capacity = targetTruncate - newNMin;
                    IASTAppendable newList = F.ListAlloc(capacity < 0 ? 1 : capacity + 1);
                    for (int j = newNMin; j < targetTruncate; j++) {
                      newList.append(outerSeries.coefficient(j));
                    }
                    currentExpr = new ASTSeriesData(x, x0, newList, newNMin, targetTruncate,
                        outerSeries.puiseuxDenominator());
                    continue;
                  }
                } else {
                  // Expanding at a different point! Flatten it first.
                  currentExpr = outerSeries.normal(false);
                }
              }
            }

            boolean isInfinity = x0.isDirectedInfinity();
            int direction = 0;
            if (x0.equals(F.CInfinity)) {
              direction = -1;
            } else if (x0.equals(F.CNInfinity)) {
              direction = 1;
            }

            // Expand the eight Arc* heads at their branch points (returns a composite IExpr for the
            // logarithmic / complex branches).
            if (!isInfinity && currentExpr.isAST()) {
              IExpr arcSeries = ASTSeriesData.arcBranchSeries((IAST) currentExpr, x, x0, n, engine);
              if (arcSeries.isPresent()) {
                currentExpr = arcSeries;
                continue;
              }
            }

            IExpr seriesX0 = x0;
            IExpr seriesFunction = currentExpr;

            if (isInfinity) {
              seriesX0 = F.C0;
              seriesFunction = engine.evaluate(F.subst(currentExpr, x, F.Power(x, F.CN1)));
            }

            ASTSeriesData series = ASTSeriesData.seriesDataRecursive(seriesFunction, x, seriesX0, n,
                direction, engine);

            if (series != null) {
              if (isInfinity) {
                currentExpr = new ASTSeriesData(x, x0, series.arg3(), series.minExponent(),
                    series.truncateOrder(), series.puiseuxDenominator());
              } else {
                currentExpr = series;
              }
            } else {
              // --- NEW FALLBACK HEURISTIC ---
              if (currentExpr.isAST() && x.isPresent()) {
                IAST astFunc = (IAST) currentExpr;
                IASTAppendable resultAST = F.ast(astFunc.head(), astFunc.argSize());
                boolean changed = false;
                for (int j = 1; j <= astFunc.argSize(); j++) {
                  IExpr arg = astFunc.get(j);
                  if (!arg.isFree(x)) {
                    IExpr argSeries = engine.evaluate(F.Series(arg, iterator));
                    if (argSeries.isPresent() && !argSeries.equals(arg)) {
                      resultAST.append(argSeries);
                      changed = true;
                    } else {
                      resultAST.append(arg);
                    }
                  } else {
                    resultAST.append(arg);
                  }
                }
                if (changed) {
                  currentExpr = engine.evaluate(resultAST);
                  continue;
                }
              }
              return F.NIL;
            }
          } else if (iterator.isRuleAST()) {
            IAST rule = (IAST) iterator;
            IExpr x = rule.arg1();
            IExpr x0 = rule.arg2();
            if (!x.isVariable()) {
              return Errors.printMessage(S.Series, "ivar", F.List(x), engine);
            }
            if (currentExpr.isFree(x)) {
              continue;
            }

            if (currentExpr instanceof ASTSeriesData) {
              ASTSeriesData outerSeries = (ASTSeriesData) currentExpr;
              if (!outerSeries.expansionVariable().equals(x)) {
                IAST coefficients = outerSeries.arg3();
                IASTAppendable newCoeffs = F.ListAlloc(coefficients.argSize());
                for (int j = 1; j <= coefficients.argSize(); j++) {
                  IExpr coeffSeries = engine.evaluate(F.Series(coefficients.get(j), iterator));
                  newCoeffs.append(coeffSeries);
                }
                currentExpr = new ASTSeriesData(outerSeries.expansionVariable(),
                    outerSeries.expansionPoint(), newCoeffs, outerSeries.minExponent(),
                    outerSeries.truncateOrder(), outerSeries.puiseuxDenominator());
                continue;
              } else {
                currentExpr = outerSeries.normal(false);
              }
            }

            boolean isInfinity = x0.isInfinity() || x0.isNegativeInfinity();
            int direction = 0;
            if (x0.equals(F.CInfinity)) {
              direction = -1;
            } else if (x0.equals(F.CNInfinity)) {
              direction = 1;
            }

            IExpr seriesX0 = x0;
            IExpr seriesFunction = currentExpr;

            if (isInfinity) {
              seriesX0 = F.C0;
              seriesFunction = engine.evaluate(F.subst(currentExpr, x, F.Power(x, F.CN1)));
            }

            int currentN = 1;
            ASTSeriesData series = ASTSeriesData.seriesDataRecursive(seriesFunction, x, seriesX0,
                currentN, direction, engine);

            int probeLimit = 30;
            while (series != null && currentN < probeLimit) {
              boolean foundNonZero = false;
              if (!series.isOrder()) {
                for (int j = series.minExponent(); j < series.truncateOrder(); j++) {
                  if (!series.coefficient(j).isZero()) {
                    foundNonZero = true;
                    break;
                  }
                }
              }
              if (foundNonZero) {
                break;
              }
              currentN += 4;
              series = ASTSeriesData.seriesDataRecursive(seriesFunction, x, seriesX0, currentN,
                  direction, engine);
            }

            if (series != null && !series.isOrder()) {
              int leadIndex = Integer.MAX_VALUE;
              for (int j = series.minExponent(); j < series.truncateOrder(); j++) {
                if (!series.coefficient(j).isZero()) {
                  leadIndex = j;
                  break;
                }
              }
              if (leadIndex != Integer.MAX_VALUE) {
                int nextIndex = -1;
                if (isInfinity || leadIndex < 0) {
                  for (int j = leadIndex + 1; j < series.truncateOrder(); j++) {
                    if (!series.coefficient(j).isZero()) {
                      nextIndex = j;
                      break;
                    }
                  }
                  if (nextIndex == -1) {
                    nextIndex = series.truncateOrder();
                  }
                }
                if (nextIndex == -1) {
                  nextIndex = leadIndex + series.puiseuxDenominator();
                }

                IASTAppendable coeffs = F.ListAlloc(2);
                coeffs.append(series.coefficient(leadIndex));

                if (isInfinity) {
                  currentExpr = new ASTSeriesData(x, x0, coeffs, leadIndex, nextIndex,
                      series.puiseuxDenominator());
                } else {
                  currentExpr = new ASTSeriesData(x, seriesX0, coeffs, leadIndex, nextIndex,
                      series.puiseuxDenominator());
                }
              } else {
                return F.NIL;
              }
            } else {
              if (currentExpr.isAST() && x.isPresent()) {
                IAST astFunc = (IAST) currentExpr;
                IASTAppendable resultAST = F.ast(astFunc.head(), astFunc.argSize());
                boolean changed = false;
                for (int j = 1; j <= astFunc.argSize(); j++) {
                  IExpr arg = astFunc.get(j);
                  if (!arg.isFree(x)) {
                    IExpr argSeries = engine.evaluate(F.Series(arg, iterator));
                    if (argSeries.isPresent() && !argSeries.equals(arg)) {
                      resultAST.append(argSeries);
                      changed = true;
                    } else {
                      resultAST.append(arg);
                    }
                  } else {
                    resultAST.append(arg);
                  }
                }
                if (changed) {
                  currentExpr = engine.evaluate(resultAST);
                  continue;
                }
              }
              return F.NIL;
            }
          } else {
            return F.NIL;
          }
        }
        return currentExpr;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_2_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }
  /**
   *
   *
   * <pre>
   * SeriesCoefficient(expr, {x, x0, n})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * get the coefficient of <code>(x- x0)^n</code> at the point <code>x = x0</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SeriesCoefficient(Sin(x),{x,f+g,n})
   * Piecewise({{Sin(f+g+1/2*n*Pi)/n!,n&gt;=0}},0)
   * </pre>
   */
  public static final class SeriesCoefficient extends AbstractFunctionEvaluator {
    private static com.google.common.base.Supplier<Matcher> MATCHER1;

    private static Matcher matcher1() {
      return MATCHER1.get();
    }

    // --- Recursion Guard to prevent StackOverflowError in Holonomic Engine ---
    private static final ThreadLocal<java.util.Set<IExpr>> HOLONOMIC_GUARD =
        ThreadLocal.withInitial(java.util.HashSet::new);

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() > 2) {
        IExpr currentExpr = ast.arg1();
        for (int i = 2; i <= ast.argSize(); i++) {
          currentExpr = engine.evaluate(F.SeriesCoefficient(currentExpr, ast.get(i)));
          if (!currentExpr.isPresent()) {
            return F.NIL;
          }
        }
        return currentExpr;
      }

      if (ast.isAST2()) {
        if (ast.arg1() instanceof ASTSeriesData) {
          IExpr arg1 = ast.arg1();
          IExpr arg2 = ast.arg2();
          ASTSeriesData seriesData = (ASTSeriesData) arg1;
          IExpr list = seriesData.arg3();
          IExpr nminExpr = seriesData.arg4();
          IExpr nmaxExpr = seriesData.arg5();
          IExpr denExpr = seriesData.arg6();
          IExpr nExpr = arg2;
          if (arg2.isList3()) {
            nExpr = ((IAST) arg2).arg3();
          }

          IAST listAST = (IAST) list;
          IExpr kExpr = engine.evaluate(F.Subtract(F.Times(nExpr, denExpr), nminExpr));

          if (kExpr.isInteger()) {
            int k = kExpr.toIntDefault();

            if (k < 0) {
              return F.C0;
            }

            int nmax = nmaxExpr.toIntDefault();
            int nmin = nminExpr.toIntDefault();

            if (k >= nmax - nmin) {
              return S.Indeterminate;
            }

            if (k < listAST.argSize()) {
              return listAST.get(k + 1);
            } else {
              return F.C0;
            }
          } else if (kExpr.isRational()) {
            return F.C0;
          }
          return F.NIL;
        }
        if (ast.arg2().isList3()) {
          IExpr matched = matcher1().apply(ast);
          if (matched.isPresent()) {
            return matched;
          }
          IExpr function = ast.arg1();

          IAST list = (IAST) ast.arg2();
          IExpr x = list.arg1();
          IExpr x0 = list.arg2();

          IExpr n = list.arg3();
          IExpr functionCoefficient = F.NIL;

          if (function.isAST()) {
            functionCoefficient = specialFunctionCoefficient((IAST) function, x, x0, n);
            if (functionCoefficient.isPresent()
                && functionCoefficient.isFree(f -> f.isIndeterminate(), true)) {
              return functionCoefficient;
            }
          }
          functionCoefficient = functionCoefficient(ast, function, x, x0, n, engine);
          if (functionCoefficient.isPresent()
              && functionCoefficient.isFree(f -> f.isIndeterminate(), true)) {
            return functionCoefficient;
          }
        }
      }

      return F.NIL;
    }

    private static IExpr functionCoefficient(final IAST ast, IExpr function, IExpr x, IExpr x0,
        IExpr n, EvalEngine engine) {

      if (x0.isDirectedInfinity()) {
        IExpr subFunc = engine.evaluate(F.Together(F.subst(function, x, F.Power(x, F.CN1))));
        return functionCoefficient(ast, subFunc, x, F.C0, n, engine);
      }

      if (n.isReal()) {
        if (n.isFraction() && !((IFraction) n).denominator().isOne()) {
          return F.C0;
        }
        if (!n.isInteger()) {
          return F.NIL;
        }
      }
      if (function.isFree(x)) {
        if (n.isZero()) {
          return function;
        }
        return F.Piecewise(F.list(F.list(function, F.Equal(n, F.C0))), F.C0);
      }

      // Rational Function Expansion Fast-Path ---
      IExpr rationalCoeff = rationalSeriesCoefficient(function, x, x0, n, engine);
      if (rationalCoeff.isPresent()) {
        return rationalCoeff;
      }

      // Binomial Power Fast-Path: (a + b*x)^p with a symbolic index yields a closed Binomial form
      // (avoids the opaque DifferenceRoot the holonomic engine would otherwise emit).
      IExpr binomialCoeff = binomialPowerSeriesCoefficient(function, x, x0, n, engine);
      if (binomialCoeff.isPresent()) {
        return binomialCoeff;
      }

      IExpr temp = polynomialSeriesCoefficient(function, x, x0, n, ast, engine);
      if (temp.isPresent()) {
        return temp;
      }

      // --- 1. Linearity over Plus Fast-Path ---
      if (function.isPlus()) {
        IAST plus = (IAST) function;
        return engine.evaluate(plus.mapThread(F.SeriesCoefficient(F.Slot1, F.List(x, x0, n)), 1));
      }

      // --- 2. Holonomic Sequence Fast-Path (Fibonacci, ChebyshevU, DifferenceRoot) ---
      IExpr holonomicCoeff = holonomicSeriesCoefficient(function, x, x0, n, engine);
      if (holonomicCoeff.isPresent()) {
        return holonomicCoeff;
      }

      // --- Series Multiplication (Cauchy Product) Fast-Path ---
      if (function.isTimes()) {
        IAST times = (IAST) function;
        IExpr varPart = x0.isZero() ? x : engine.evaluate(F.Subtract(x, x0));

        IExpr extractedPower = F.C0;
        IASTAppendable restTimes = F.TimesAlloc(times.argSize());

        for (int i = 1; i <= times.argSize(); i++) {
          IExpr arg = times.get(i);
          if (arg.equals(varPart)) {
            extractedPower = engine.evaluate(F.Plus(extractedPower, F.C1));
          } else if (arg.isPower() && arg.base().equals(varPart) && arg.exponent().isInteger()) {
            extractedPower = engine.evaluate(F.Plus(extractedPower, arg.exponent()));
          } else {
            restTimes.append(arg);
          }
        }

        if (!extractedPower.isZero()) {
          IExpr restFunction = engine.evaluate(restTimes.oneIdentity1());
          IExpr shiftedN = engine.evaluate(F.Subtract(n, extractedPower));
          return engine.evaluate(F.SeriesCoefficient(restFunction, F.List(x, x0, shiftedN)));
        }

        if (n.isInteger()) {
          int nInt = n.toIntDefault();
          ASTSeriesData productSeries = null;
          boolean success = true;

          int minTotal = 0;
          java.util.List<ASTSeriesData> components = new java.util.ArrayList<>();

          for (int i = 1; i <= times.argSize(); i++) {
            IExpr arg = times.get(i);
            ASTSeriesData probed = ASTSeriesData.seriesDataRecursive(arg, x, x0, 1, engine);
            if (probed == null) {
              success = false;
              break;
            }
            minTotal += probed.minExponent();
            components.add(probed);
          }

          if (success) {
            for (int i = 0; i < components.size(); i++) {
              ASTSeriesData probed = components.get(i);
              int minOthers = minTotal - probed.minExponent();
              int targetTruncate = Math.max(nInt - minOthers + 1, 0) + 2;

              ASTSeriesData componentSeries = ASTSeriesData.seriesDataRecursive(times.get(i + 1), x,
                  x0, targetTruncate, engine);

              if (componentSeries == null) {
                success = false;
                break;
              }

              if (productSeries == null) {
                productSeries = componentSeries;
              } else {
                productSeries = productSeries.timesPS(componentSeries);
              }
            }

            if (success && productSeries != null) {
              int kIdx = nInt * productSeries.puiseuxDenominator();
              IExpr coeff = productSeries.coefficient(kIdx);
              if (coeff != null) {
                return engine.evaluate(F.Together(F.ExpandAll(coeff)));
              }
            }
          }
        }
      }

      if (function.isAST(S.InverseSeries)) {
        IExpr f = function.first();
        IExpr innerX = x;
        if (function.argSize() >= 2) {
          innerX = function.second();
        }
        IExpr lbCoeff = ASTSeriesData.lagrangeBurmannCoefficient(f, innerX, x0, n, engine);
        if (lbCoeff.isPresent()) {
          return lbCoeff;
        }
      } else if (function.isAST1() && function.head().isAST(S.InverseFunction)) {
        IExpr fExpr = function.head().first();
        if (fExpr.isSymbol() || fExpr.isAST(S.Function)) {
          IExpr f = engine.evaluate(F.unaryAST1(fExpr, x));
          IExpr lbCoeff = ASTSeriesData.lagrangeBurmannCoefficient(f, x, x0, n, engine);
          if (lbCoeff.isPresent()) {
            return lbCoeff;
          }
        } else {
          return F.NIL;
        }
      }

      IExpr compositeCoeff = compositeSeriesCoefficient(function, x, x0, n, engine);
      if (compositeCoeff.isPresent()) {
        return engine.evaluate(F.Expand(compositeCoeff));
      }
      if (function.isPower()) {
        IExpr b = function.base();
        IExpr exponent = function.exponent();
        if (b.equals(x)) {
          if (exponent.isNumber()) {
            INumber exp = (INumber) exponent;
            if (exp.isInteger()) {
              if (x0.isZero()) {
                return F.Piecewise(F.list(F.list(F.C1, F.Equal(n, exp))), F.C0);
              }
              return F.Piecewise(
                  F.list(F.list(F.Times(F.Power(x0, F.Plus(exp, n.negate())), F.Binomial(exp, n)),
                      F.LessEqual(F.C0, n, exp))),
                  F.C0);
            }
          }
          if (!x0.isZero() && exponent.isFree(x)) {
            IExpr exp = exponent;
            return F.Piecewise(
                F.list(F.list(F.Times(F.Power(x0, F.Plus(exp, n.negate())), F.Binomial(exp, n)),
                    F.GreaterEqual(n, F.C0))),
                F.C0);
          }
        }
        if (b.isFree(x)) {
          IExpr[] linear = exponent.linear(x);
          if (linear != null) {
            if (x0.isZero()) {
              IExpr a = linear[0];
              IExpr c = linear[1];
              return F
                  .Piecewise(F.list(F.list(F.Times(F.Power(b, a), F.Power(F.Factorial(n), F.CN1),
                      F.Power(F.Times(c, F.Log(b)), n)), F.GreaterEqual(n, F.C0))), F.C0);
            } else if (linear[0].isZero() && linear[1].isOne()) {
              return F.Piecewise(F.list(F.list(
                  F.Times(F.Power(b, x0), F.Power(F.Factorial(n), F.CN1), F.Power(F.Log(b), n)),
                  F.GreaterEqual(n, F.C0))), F.C0);
            }
          }
        } else if (b.equals(exponent) && x0.isZero()) {
          if (exponent.equals(x)) {
            return F.Piecewise(F.list(F.list(
                F.Times(F.Power(b, x0), F.Power(F.Factorial(n), F.CN1), F.Power(F.Log(b), n)),
                F.GreaterEqual(n, F.C0))), F.C0);
          }
        }
      }

      if (x0.isReal()) {
        final int x0Value = x0.toIntDefault();
        if (x0Value != 0) {
          return taylorCoefficient(function, x, x0, n, engine);
        }
        x0 = F.ZZ(x0Value);
      }

      return taylorCoefficient(function, x, x0, n, engine);
    }

    /**
     * Series coefficient of a binomial power <code>(a + b*x)^p</code> with a symbolic index
     * <code>n</code>, expanded around <code>x==0</code>.
     *
     * <p>
     * The coefficient of <code>x^n</code> in <code>(a + b*x)^p</code> is
     * <code>b^n * a^(p-n) * Binomial(p, n)</code>. When <code>p</code> is a non-negative integer the
     * expansion is finite, so the index is bounded by <code>0 &lt;= n &lt;= p</code>; otherwise the
     * series runs for all <code>n &gt;= 0</code>. Returning this closed Binomial form is a
     * readability improvement over the equivalent (but opaque) <code>DifferenceRoot</code> the
     * holonomic engine would otherwise emit for a finite binomial power.
     *
     * @return a {@link S#Piecewise} expression, or {@link F#NIL} if <code>function</code> is not a
     *         binomial power with a symbolic index expanded around zero
     */
    private static IExpr binomialPowerSeriesCoefficient(IExpr function, IExpr x, IExpr x0, IExpr n,
        EvalEngine engine) {
      if (n.isNumber() || !x0.isZero() || !function.isPower()) {
        return F.NIL;
      }
      IExpr p = function.exponent();
      if (!p.isFree(x)) {
        return F.NIL;
      }
      // Negative integer exponents are rational functions handled by rationalSeriesCoefficient.
      if (p.isInteger() && p.isNegative()) {
        return F.NIL;
      }
      IExpr[] linear = function.base().linear(x);
      if (linear == null) {
        return F.NIL;
      }
      IExpr a = linear[0];
      IExpr b = linear[1];
      // a==0 would leave the messy 0^(p-n) factor; b==0 means the base is free of x.
      if (a.isZero() || b.isZero()) {
        return F.NIL;
      }
      IExpr coeff = engine
          .evaluate(F.Times(F.Power(b, n), F.Power(a, F.Subtract(p, n)), F.Binomial(p, n)));
      IExpr condition = p.isInteger() //
          ? F.LessEqual(F.C0, n, p) //
          : F.GreaterEqual(n, F.C0);
      return F.Piecewise(F.list(F.list(coeff, condition)), F.C0);
    }

    /**
     * Constructs a DifferenceRoot (Holonomic sequence) representation for the series coefficients
     * of rational functions P(x)/Q(x) when 'n' is symbolic, and matches known identities (like
     * Fibonacci and ChebyshevU) for both numeric and symbolic 'n'.
     */
    private static IExpr holonomicSeriesCoefficient(IExpr function, IExpr x, IExpr x0, IExpr n,
        EvalEngine engine) {
      java.util.Set<IExpr> guard = HOLONOMIC_GUARD.get();
      if (guard.contains(function)) {
        return F.NIL;
      }
      guard.add(function);
      try {
        IExpr funcToExpand = function;
        if (!x0.isZero()) {
          if (x0.isDirectedInfinity()) {
            funcToExpand = engine.evaluate(F.subst(function, x, F.Power(x, F.CN1)));
          } else {
            funcToExpand = engine.evaluate(F.subst(function, x, F.Plus(x, x0)));
          }
        }

        IExpr num = engine.evaluate(F.Numerator(F.Together(funcToExpand)));
        IExpr den = engine.evaluate(F.Denominator(F.Together(funcToExpand)));

        if (num.isPolynomial(x) && den.isPolynomial(x)) {
          IExpr denAtZero = engine.evaluate(F.subst(den, x, F.C0));
          if (!denAtZero.isZero()) {
            // Normalize so that the constant term of the denominator is 1
            IExpr normFactor = engine.evaluate(F.Divide(F.C1, denAtZero));
            IExpr normNum = engine.evaluate(F.Expand(F.Times(num, normFactor)));
            IExpr normDen = engine.evaluate(F.Expand(F.Times(den, normFactor)));

            IAST denCoeffs = ASTSeriesData.coefficientList(normDen, F.List(x));
            IAST numCoeffs = ASTSeriesData.coefficientList(normNum, F.List(x));

            if (denCoeffs.isList() && denCoeffs.argSize() > 0) {

              // --- Safely restrict Order-2 Identity extractions ---
              if (denCoeffs.argSize() == 3) {
                IExpr q1 = denCoeffs.get(2);
                IExpr q2 = denCoeffs.get(3);

                // 1/(1 - x - x^2) => Fibonacci
                if (q1.equals(F.CN1) && q2.equals(F.CN1)) {
                  if (normNum.isOne()) {
                    return F.Piecewise(
                        F.list(F.list(F.Fibonacci(F.Plus(n, F.C1)), F.GreaterEqual(n, F.C0))),
                        F.C0);
                  } else if (normNum.equals(x)) {
                    return F.Piecewise(F.list(F.list(F.Fibonacci(n), F.GreaterEqual(n, F.C0))),
                        F.C0);
                  }
                }

                // 1/(1 - 2*t*x + x^2) => ChebyshevU and ChebyshevT
                if (q2.isOne()) {
                  IExpr t = engine.evaluate(F.Divide(F.Negate(q1), F.C2));
                  if (normNum.isOne()) {
                    return F.Piecewise(F.list(F.list(F.ChebyshevU(n, t), F.GreaterEqual(n, F.C0))),
                        F.C0);
                  } else {
                    IExpr expectedNum = engine.evaluate(F.Expand(F.Subtract(F.C1, F.Times(t, x))));
                    if (normNum.equals(expectedNum)) {
                      return F.Piecewise(
                          F.list(F.list(F.ChebyshevT(n, t), F.GreaterEqual(n, F.C0))), F.C0);
                    }
                  }
                }

                // --- Unification: Order-2 Denominators ---
                // 1 / (1 + q1*x + q2*x^2) => y_n = -q1 * y_{n-1} - q2 * y_{n-2}
                if (normNum.isOne()) {
                  IExpr a = engine.evaluate(F.Negate(q1));
                  IExpr b = q2;

                  // The generating function 1/(1-ax+bx^2) corresponds to the sequence shifted by
                  // n+1
                  IExpr binetCoeff = AlgebraUtil.generalizedBinet(a, b, F.Plus(n, F.C1), engine);
                  if (binetCoeff.isPresent()) {
                    return F.Piecewise(F.list(F.list(binetCoeff, F.GreaterEqual(n, F.C0))), F.C0);
                  }
                }
              }

              // Only generate explicit DifferenceRoot if 'n' is symbolic
              if (!n.isNumber()) {
                int d = denCoeffs.argSize() - 1; // degree of denominator
                int m = numCoeffs.isList() ? numCoeffs.argSize() - 1 : 0; // degree of numerator

                ISymbol y = F.y;
                ISymbol k = F.n;

                IASTAppendable recSum = F.PlusAlloc(d + 1);
                for (int i = 0; i <= d; i++) {
                  IExpr q_i = denCoeffs.get(i + 1);
                  if (!q_i.isZero()) {
                    recSum.append(F.Times(q_i, F.unaryAST1(y, F.Plus(k, F.ZZ(d - i)))));
                  }
                }
                IExpr recurrence = F.Equal(recSum, F.C0);

                int maxInit = Math.max(d - 1, m);

                ASTSeriesData sd =
                    ASTSeriesData.seriesDataRecursive(funcToExpand, x, F.C0, maxInit + 1, engine);
                if (sd != null) {
                  java.util.List<IExpr> initialConditions = new java.util.ArrayList<>(maxInit + 1);
                  for (int i = 0; i <= maxInit; i++) {
                    initialConditions.add(sd.coefficient(i));
                  }
                  // Shared DifferenceRoot + initial-condition embedding (also used by RSolve).
                  return AlgebraUtil.differenceRoot(recurrence, y, k, initialConditions, n);
                }
              }
            }
          }
        }
        return F.NIL;
      } finally {
        guard.remove(function);
      }
    }

    private static IExpr rationalSeriesCoefficient(IExpr function, IExpr x, IExpr x0, IExpr n,
        EvalEngine engine) {
      IExpr den = engine.evaluate(F.Denominator(function));
      if (den.isOne() || den.isFree(x)) {
        return F.NIL;
      }

      IExpr apart = engine.evaluate(F.Apart(function, x));

      if (apart.isPlus()) {
        IAST apartPlus = (IAST) apart;
        Map<IExpr, IASTAppendable> condMap = new HashMap<IExpr, IASTAppendable>();
        boolean canMerge = true;
        IASTAppendable fallbackSum = F.PlusAlloc(apartPlus.argSize());

        for (int i = 1; i <= apartPlus.argSize(); i++) {
          IExpr termCoeff = rationalTermCoefficient(apartPlus.get(i), x, x0, n, engine);
          if (!termCoeff.isPresent()) {
            return F.NIL;
          }
          fallbackSum.append(termCoeff);

          if (canMerge && termCoeff.isAST(S.Piecewise, 3)) {
            IAST pw = (IAST) termCoeff;
            if (pw.arg1().isList1() && pw.arg1().first().isList2() && pw.arg2().isZero()) {
              IAST rule = (IAST) pw.arg1().first();
              IExpr val = rule.arg1();
              IExpr cond = rule.arg2();

              IASTAppendable sumForCond = condMap.get(cond);
              if (sumForCond == null) {
                sumForCond = F.PlusAlloc();
                condMap.put(cond, sumForCond);
              }
              sumForCond.append(val);
            } else {
              canMerge = false;
            }
          } else {
            canMerge = false;
          }
        }

        if (canMerge) {
          IASTAppendable mergedRules = F.ListAlloc(condMap.size());
          for (Map.Entry<IExpr, IASTAppendable> entry : condMap.entrySet()) {
            IExpr cond = entry.getKey();
            IExpr mergedVal = engine.evaluate(entry.getValue().oneIdentity1());
            mergedRules.append(F.List(mergedVal, cond));
          }
          return F.Piecewise(mergedRules, F.C0);
        }

        return engine.evaluate(fallbackSum);
      } else {
        return rationalTermCoefficient(apart, x, x0, n, engine);
      }
    }

    private static IExpr rationalTermCoefficient(IExpr term, IExpr x, IExpr x0, IExpr n,
        EvalEngine engine) {
      IExpr c = F.C1;
      IExpr core = term;

      if (term.isTimes()) {
        IAST times = (IAST) term;
        IASTAppendable cTimes = F.TimesAlloc(times.argSize());
        IASTAppendable coreTimes = F.TimesAlloc(times.argSize());
        for (int i = 1; i <= times.argSize(); i++) {
          IExpr arg = times.get(i);
          if (arg.isFree(x)) {
            cTimes.append(arg);
          } else {
            coreTimes.append(arg);
          }
        }
        c = cTimes.oneIdentity1();
        core = coreTimes.oneIdentity1();
      }

      if (core.isPower()) {
        IExpr base = core.base();
        IExpr exp = core.exponent();

        if (exp.isInteger() && ((IInteger) exp).isNegative()) {
          IExpr[] linear = base.linear(x);
          if (linear != null) {
            IExpr a = linear[0];
            IExpr b = linear[1];

            IExpr aPrime = engine.evaluate(F.Plus(a, F.Times(b, x0)));
            IExpr bPrime = b;

            if (aPrime.isZero()) {
              IExpr coeff = engine.evaluate(F.Times(c, F.Power(bPrime, exp)));
              return F.Piecewise(F.list(F.list(coeff, F.Equal(n, exp))), F.C0);
            }

            boolean useGroupedBase = !aPrime.isNumber();
            IExpr negRatio = engine.evaluate(F.Divide(F.Negate(bPrime), aPrime));

            IExpr coeff;
            if (exp.isMinusOne()) {
              if (useGroupedBase) {
                IExpr signShift = F.CN1;
                IExpr baseA = F.Power(F.Negate(aPrime), F.Subtract(exp, n));
                IExpr baseB = F.Power(bPrime, n);
                coeff = engine.evaluate(F.Together(F.Times(c, signShift, baseA, baseB)));
              } else {
                coeff = engine.evaluate(F.Times(c, F.Power(aPrime, F.CN1), F.Power(negRatio, n)));
              }
            } else {
              int m = exp.negate().toIntDefault();
              if (m > 1 && m < 1000) {
                IASTAppendable num = F.TimesAlloc(m);
                for (int i = 1; i < m; i++) {
                  num.append(F.Plus(n, F.ZZ(i)));
                }
                IExpr den = engine.evaluate(F.Factorial(F.ZZ(m - 1)));
                IExpr binomialExpanded = engine.evaluate(F.Divide(F.ExpandAll(num), den));

                if (useGroupedBase) {
                  IExpr signShift = F.Power(F.CN1, exp);
                  IExpr baseA = F.Power(F.Negate(aPrime), F.Subtract(exp, n));
                  IExpr baseB = F.Power(bPrime, n);

                  coeff = engine
                      .evaluate(F.Together(F.Times(c, signShift, baseA, baseB, binomialExpanded)));
                } else {
                  coeff = engine.evaluate(F.Together(
                      F.Times(c, F.Power(aPrime, exp), F.Power(negRatio, n), binomialExpanded)));
                }
              } else {
                coeff = engine.evaluate(F.Times(c, F.Power(aPrime, exp), F.Binomial(exp, n),
                    F.Power(F.Divide(bPrime, aPrime), n)));
              }
            }
            return F.Piecewise(F.list(F.list(coeff, F.GreaterEqual(n, F.C0))), F.C0);
          }
        }
      } else if (core.isFree(x)) {
        return F.Piecewise(F.list(F.list(engine.evaluate(F.Times(c, core)), F.Equal(n, F.C0))),
            F.C0);
      }

      return F.NIL;
    }

    private static IExpr compositeSeriesCoefficient(IExpr function, IExpr x, IExpr x0, IExpr n,
        EvalEngine engine) {
      if (!n.isInteger() || !n.isPositive()) {
        return F.NIL;
      }
      int nInt = n.toIntDefault();

      if (function.isAST1()) {
        IExpr fHead = function.head();
        IExpr gExpr = function.first();

        if (gExpr.isFree(x)) {
          return F.NIL;
        }

        IExpr g0 = engine.evaluate(F.subst(gExpr, x, x0));

        IASTAppendable gDerivs = F.ListAlloc(nInt);
        IExpr currentGDeriv = gExpr;
        for (int i = 1; i <= nInt; i++) {
          currentGDeriv = engine.evaluate(F.D(currentGDeriv, x));
          IExpr gDerivAtX0 = engine.evaluate(F.subst(currentGDeriv, x, x0));
          gDerivs.append(gDerivAtX0);
        }

        IASTAppendable sum = F.PlusAlloc(nInt);
        ISymbol yDum = F.Dummy("y");
        IExpr currentFDeriv = F.unaryAST1(fHead, yDum);

        IExpr[][] bellCache = new IExpr[nInt + 1][nInt + 1];

        for (int k = 1; k <= nInt; k++) {
          currentFDeriv = engine.evaluate(F.D(currentFDeriv, yDum));
          IExpr fDerivAtG0 = engine.evaluate(F.subst(currentFDeriv, yDum, g0));

          IExpr bellY = org.matheclipse.core.reflection.system.BellY.bellY(nInt, k, gDerivs,
              (IAST) function, engine, bellCache);

          sum.append(F.Times(fDerivAtG0, bellY));
        }

        return engine.evaluate(F.Times(F.Power(F.Factorial(n), F.CN1), sum));
      }
      return F.NIL;
    }

    private static IExpr taylorCoefficient(IExpr function, IExpr x, IExpr x0, IExpr n,
        EvalEngine engine) {
      final int nInt = n.toIntDefault();
      if (nInt < 0) {
        return F.NIL;
      }

      if (nInt == 0) {
        return function.subs(x, x0);
      }

      // Use efficient truncated Series evaluation before falling back to explosive symbolic
      // derivatives
      if (nInt >= 5 && nInt < 1000) {
        IExpr seriesExp = engine.evaluate(F.Series(function, F.List(x, x0, n)));
        if (seriesExp instanceof ASTSeriesData) {
          ASTSeriesData sd = (ASTSeriesData) seriesExp;
          return engine.evaluate(F.Together(sd.coefficient(nInt * sd.puiseuxDenominator())));
        }
      }

      IExpr derivedFunction = S.D.of(engine, function, F.list(x, n));
      IExpr substituted = derivedFunction.subs(x, x0);

      IExpr rawCoefficient = engine.evaluate(F.Times(F.Power(F.Factorial(n), F.CN1), substituted));
      return engine.evaluate(F.Together(F.ExpandAll(rawCoefficient)));
    }

    private static IExpr specialFunctionCoefficient(IAST function, IExpr x, IExpr x0, IExpr n) {
      if (function.isAST3()) {
        IExpr head = function.head();
        IExpr arg1 = function.first();
        IExpr arg2 = function.second();
        IExpr arg3 = function.arg3();

        if (arg3.equals(x) && head.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) head).ordinal()) {
            case ID.HypergeometricPFQ:
              if (x0.isZero() && arg1.isList() && arg2.isList()) {
                IAST aList = (IAST) arg1;
                IAST bList = (IAST) arg2;

                if (n.isInteger()) {
                  int nInt = n.toIntDefault();
                  if (nInt < 0) {
                    return F.C0;
                  }

                  IASTAppendable num = F.TimesAlloc(aList.argSize() * nInt + 1);
                  IASTAppendable den = F.TimesAlloc(bList.argSize() * nInt + 2);

                  for (int i = 1; i <= aList.argSize(); i++) {
                    IExpr a = aList.get(i);
                    for (int j = 0; j < nInt; j++) {
                      num.append(F.Plus(a, F.ZZ(j)));
                    }
                  }

                  for (int i = 1; i <= bList.argSize(); i++) {
                    IExpr b = bList.get(i);
                    for (int j = 0; j < nInt; j++) {
                      den.append(F.Plus(b, F.ZZ(j)));
                    }
                  }
                  den.append(F.Factorial(n));

                  return F.Divide(num.oneIdentity1(), den.oneIdentity1());

                } else {
                  IASTAppendable num = F.TimesAlloc(aList.argSize() + bList.argSize() + 1);
                  IASTAppendable den = F.TimesAlloc(aList.argSize() + bList.argSize() + 2);

                  for (int i = 1; i <= aList.argSize(); i++) {
                    IExpr a = aList.get(i);
                    num.append(F.Gamma(F.Plus(a, n)));
                    den.append(F.Gamma(a));
                  }

                  for (int i = 1; i <= bList.argSize(); i++) {
                    IExpr b = bList.get(i);
                    num.append(F.Gamma(b));
                    den.append(F.Gamma(F.Plus(b, n)));
                  }
                  den.append(F.Factorial(n));

                  IExpr coeff = F.Divide(num.oneIdentity1(), den.oneIdentity1());
                  return F.Piecewise(F.list(F.list(coeff, F.GreaterEqual(n, F.C0))), F.C0);
                }
              }
              break;
          }
        }
      }
      return F.NIL;
    }

    public static IExpr polynomialSeriesCoefficient(IExpr univariatePolynomial, IExpr x, IExpr x0,
        IExpr n, final IAST seriesTemplate, EvalEngine engine) {
      try {
        Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
        IASTAppendable rest = F.ListAlloc(4);
        ExprPolynomialRing.create(univariatePolynomial, x, coefficientMap, rest);
        IASTAppendable coefficientPlus = F.PlusAlloc(2);

        if (coefficientMap.size() > 0) {

          if (x0.isZero()) {
            IExpr coeff = coefficientMap.get(n);
            if (coeff != null) {
              coefficientPlus.append(coeff);
            } else if (!n.isNumber()) {
              IASTAppendable rules = F.ListAlloc(coefficientMap.size());
              for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
                rules.append(F.list(entry.getValue(), F.Equal(n, entry.getKey())));
              }
              coefficientPlus.append(F.Piecewise(rules, F.C0));
            }
          } else {
            IExpr defaultValue = F.C0;
            IASTAppendable rules = F.ListAlloc(2);
            IASTAppendable plus = F.PlusAlloc(coefficientMap.size());
            IAST comparator = F.GreaterEqual(n, F.C0);

            for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
              final IExpr exp = entry.getKey();
              if (exp.isZero()) {
                continue;
              }

              IExpr coefficient = entry.getValue();
              if (coefficient.isZero()) {
                continue;
              }

              IAST powerPart = F.Power(x0, exp);
              comparator = F.Greater(n, F.C0);
              IAST bin;
              int k = exp.toIntDefault();

              if (F.isPresent(k)) {
                if (k < 0) {
                  x0 = x0.negate();
                  int nk = -k;
                  IASTAppendable binomial = F.TimesAlloc(nk + 1);
                  for (int i = 1; i < nk; i++) {
                    binomial.append(F.Plus(n, F.ZZ(i)));
                  }
                  binomial.append(F.Power(F.Factorial(F.ZZ(nk - 1)), -1));
                  bin = binomial;
                  comparator = F.GreaterEqual(n, F.C0);
                } else {
                  comparator = F.LessEqual(F.C0, n, exp);
                  bin = F.Binomial(exp, n);
                }
              } else {
                bin = F.Binomial(exp, n);
              }

              if (coefficient.isOne()) {
                plus.append(F.Times(powerPart, bin));
              } else {
                plus.append(F.Times(coefficient, powerPart, bin));
              }
            }

            IExpr temp = engine.evaluate(plus);
            if (!temp.isZero()) {
              rules.append(
                  F.list(engine.evaluate(F.Times(F.Power(x0, n.negate()), plus)), comparator));
            }

            if (comparator.isAST(S.Greater)) {
              plus = F.PlusAlloc(coefficientMap.size());
              for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
                IExpr exp = entry.getKey();
                IExpr coefficient = entry.getValue();
                if (coefficient.isZero()) {
                  continue;
                }
                if (coefficient.isOne()) {
                  plus.append(F.Times(F.Power(x0, exp)));
                } else {
                  plus.append(F.Times(coefficient, F.Power(x0, exp)));
                }
              }
              rules.append(F.list(engine.evaluate(plus), F.Equal(n, F.C0)));
            }
            coefficientPlus.append(F.Piecewise(rules, defaultValue));
          }
        } else {
          if (!univariatePolynomial.isPlus()) {
            return F.NIL;
          }
        }

        for (int i = 1; i < rest.size(); i++) {
          IASTMutable term = seriesTemplate.copy();
          term.set(1, rest.get(i));
          coefficientPlus.append(term);
        }

        return coefficientPlus.oneIdentity0();

      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.SeriesCoefficient, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_2_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      MATCHER1 = Suppliers.memoize(SeriesCoefficientRules::init1);
    }
  }


  /**
   *
   *
   * <pre>
   * SeriesData(x, x0, {coeff0, coeff1, coeff2,...}, nMin, nMax, denominator)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * internal structure of a power series at the point <code>x = x0</code> the <code>coeff</code> -i
   * are coefficients of the power series.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)
   * Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)
   * </pre>
   */
  private static final class SeriesData extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int denominator = 1;
      if (ast.size() == 6 || ast.size() == 7) {
        if (ast.arg1().isNumber()) {
          // Attempt to evaluate a series at the number `1`. Returning Indeterminate.
          Errors.printMessage(S.SeriesData, "ssdn", F.List(), engine);
          return S.Indeterminate;
        }
        IExpr x = ast.arg1();
        IExpr x0 = ast.arg2();

        if (ast.arg3().isVector() < 0 || !ast.arg3().isAST()) {
          return F.NIL;
        }
        IAST coefficients = (IAST) ast.arg3();
        final int nMin = ast.arg4().toIntDefault();
        if (F.isNotPresent(nMin)) {
          return F.NIL;
        }
        final int truncate = ast.arg5().toIntDefault();
        if (F.isNotPresent(truncate)) {
          return F.NIL;
        }
        if (ast.size() == 7) {
          denominator = ast.get(6).toIntDefault();
        }
        return new ASTSeriesData(x, x0, coefficients, nMin, truncate, denominator);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private SeriesFunctions() {}
}

