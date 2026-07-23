package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Integration of a product of polynomial powers times a polynomial cofactor,
 * {@code S(x) * P_1(x)^m_1 * ... * P_k(x)^m_k}, whose antiderivative has the form
 * {@code poly(x) * P_1(x)^(m_1+1) * ... * P_k(x)^(m_k+1)}.
 *
 * <p>
 * The exponents {@code m_i} may be symbolic. Differentiating the ansatz and dividing by
 * {@code prod(P_i^m_i)} gives, with {@code prod = P_1*...*P_k},
 *
 * <pre>
 * poly' * prod + poly * sum_j (m_j + 1) * P_j' * prod/P_j  =  S
 * </pre>
 *
 * a polynomial identity linear in the unknown polynomial {@code poly} (bounded degree
 * {@code deg(S) - deg(prod) + 1}), solved by a linear system in its coefficients. Only power factors
 * with a <em>non-integer / symbolic</em> exponent are treated this way (integer powers are rational
 * and handled by the rational-integration / Rubi stages). The result is diff-back self-verified.
 *
 * <p>
 * This recovers integrals such as {@code Integrate((a+b*x)^m, x) = (a+b*x)^(m+1)/(b*(m+1))} and the
 * multi-factor {@code Integrate(P^m*Q^n*S, x) = x*P^(m+1)*Q^(n+1)} that Rubi leaves unevaluated (its
 * linearity split over {@code S} destroys the structure). See {@code CLEANROOM.md}.
 */
public class ProductPowerIntegration {

  private static final int MAX_DEGREE = 32;

  private ProductPowerIntegration() {}

  /**
   * Try the product-of-powers ansatz (any number of power factors).
   *
   * @param integrand the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine) {
    return integrate(integrand, x, engine, 1);
  }

  /**
   * Try the product-of-powers ansatz, requiring at least {@code minPowerFactors} power factors.
   *
   * <p>
   * The pre-Rubi cascade calls this with {@code minPowerFactors == 2}: the single-power case
   * {@code (a+b*x)^m * poly} overlaps heavily with integrals Rubi already renders in a canonical
   * form, so only the genuinely multi-power case (which Rubi leaves unevaluated) is intercepted
   * before the rules; the {@code Method -> "ProductPower"} form allows a single factor.
   *
   * @param integrand the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @param minPowerFactors minimum number of symbolic-power factors required to apply
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine, int minPowerFactors) {
    if (!Config.INTEGRATE_ALGORITHMS) {
      return F.NIL;
    }
    List<IExpr> bases = new ArrayList<>();
    List<IExpr> exponents = new ArrayList<>();
    IExpr cofactor = F.C1;
    IAST factors = integrand.isTimes() ? (IAST) integrand : F.Times(integrand);
    for (int i = 1; i < factors.size(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(x, true)) {
        cofactor = F.Times(cofactor, factor);
      } else if (factor.isPower() && factor.exponent().isFree(x, true)
          && !factor.exponent().isInteger()
          && engine.evaluate(F.PolynomialQ(factor.base(), x)).isTrue()
          && engine.evaluate(F.Exponent(factor.base(), x)).toIntDefault(-1) >= 1) {
        bases.add(factor.base());
        exponents.add(factor.exponent());
      } else {
        cofactor = F.Times(cofactor, factor);
      }
    }
    if (bases.size() < Math.max(1, minPowerFactors)) {
      return F.NIL;
    }
    IExpr s = engine.evaluate(F.ExpandAll(cofactor));
    if (!engine.evaluate(F.PolynomialQ(s, x)).isTrue()) {
      return F.NIL;
    }
    // prod = product of the bases; logDerivativeTerm = sum_j (m_j+1) * P_j' * prod/P_j
    IExpr prod = F.C1;
    for (IExpr base : bases) {
      prod = F.Times(prod, base);
    }
    prod = engine.evaluate(F.ExpandAll(prod));
    IASTAppendable logDerivativeSum = F.PlusAlloc(bases.size());
    for (int j = 0; j < bases.size(); j++) {
      IExpr others = F.C1;
      for (int i = 0; i < bases.size(); i++) {
        if (i != j) {
          others = F.Times(others, bases.get(i));
        }
      }
      logDerivativeSum.append(
          F.Times(F.Plus(exponents.get(j), F.C1), F.D(bases.get(j), x), others));
    }
    IExpr logDerivativeTerm = engine.evaluate(F.ExpandAll(logDerivativeSum));

    int degreeProd = engine.evaluate(F.Exponent(prod, x)).toIntDefault(-1);
    int degreeS = engine.evaluate(F.Exponent(s, x)).toIntDefault(-1);
    if (degreeProd < 1) {
      return F.NIL;
    }
    int beta = Math.max(0, degreeS) - degreeProd + 1;
    if (beta < 0 || beta > MAX_DEGREE) {
      return F.NIL;
    }
    ISymbol[] coefficients = new ISymbol[beta + 1];
    IASTAppendable trial = F.PlusAlloc(beta + 1);
    for (int i = 0; i <= beta; i++) {
      coefficients[i] = F.Dummy("pp$c" + i);
      trial.append(F.Times(coefficients[i], F.Power(x, F.ZZ(i))));
    }
    IExpr poly = trial;
    // poly' * prod + poly * logDerivativeTerm must equal S identically in x. Matching the
    // coefficient of each power of x yields a linear system in the unknown coefficients c_i. It is
    // solved with LinearSolve on the top (highest-degree) square block, which is triangular for this
    // ansatz -- deliberately *not* Solve/SolveAlways, whose general (nonlinear-capable) machinery is
    // far too slow, or non-terminating, on coefficients carrying the symbolic exponents/parameters.
    IExpr residual = engine.evaluate(F.ExpandAll(
        F.Subtract(F.Plus(F.Times(F.D(poly, x), prod), F.Times(poly, logDerivativeTerm)), s)));
    IExpr coeffListExpr = engine.evaluate(F.CoefficientList(residual, x));
    if (!coeffListExpr.isList()) {
      return F.NIL;
    }
    IAST residualCoefficients = (IAST) coeffListExpr;
    int unknowns = beta + 1;
    int rows = residualCoefficients.argSize();
    if (rows < unknowns) {
      return F.NIL;
    }
    IASTAppendable zeroRules = F.ListAlloc(unknowns);
    for (ISymbol coefficient : coefficients) {
      zeroRules.append(F.Rule(coefficient, F.C0));
    }
    IASTAppendable matrix = F.ListAlloc(unknowns);
    IASTAppendable vector = F.ListAlloc(unknowns);
    for (int row = rows - unknowns + 1; row <= rows; row++) {
      IExpr equationExpr = residualCoefficients.get(row);
      IASTAppendable matrixRow = F.ListAlloc(unknowns);
      for (int i = 0; i < unknowns; i++) {
        matrixRow.append(engine.evaluate(F.Coefficient(equationExpr, coefficients[i])));
      }
      matrix.append(matrixRow);
      vector.append(engine.evaluate(F.ReplaceAll(equationExpr, zeroRules)).negate());
    }
    IExpr solution = engine.evaluate(F.LinearSolve(matrix, vector));
    if (!solution.isList() || ((IAST) solution).argSize() != unknowns) {
      return F.NIL;
    }
    IAST solutionVector = (IAST) solution;
    IASTAppendable polyBuilder = F.PlusAlloc(unknowns);
    for (int i = 0; i < unknowns; i++) {
      polyBuilder.append(F.Times(solutionVector.get(i + 1), F.Power(x, F.ZZ(i))));
    }
    IExpr result = engine.evaluate(polyBuilder);
    for (int i = 0; i < bases.size(); i++) {
      result = F.Times(result, F.Power(bases.get(i), F.Plus(exponents.get(i), F.C1)));
    }
    result = engine.evaluate(result);
    // Collect every free symbol from the bare bases/exponents/cofactor (Variables descends into
    // these polynomials, but treats a symbolic power P^m as one opaque variable and would miss
    // symbols occurring only inside it, e.g. a and x in (a+b*x)^m).
    IASTAppendable symbolSource = F.ListAlloc(bases.size() * 2 + 2);
    symbolSource.append(x);
    symbolSource.append(cofactor);
    for (IExpr base : bases) {
      symbolSource.append(base);
    }
    for (IExpr exponent : exponents) {
      symbolSource.append(exponent);
    }
    IExpr freeSymbols = engine.evaluate(F.Variables(symbolSource));
    return verifyAntiderivative(result, integrand, x, freeSymbols, engine) ? result : F.NIL;
  }

  /**
   * Diff-back self-verification. Purely numeric: {@code Simplify}/{@code Together} on an expression
   * with symbolic powers {@code P^m}, {@code P^(m+1)} would rarely cancel and is far too slow here
   * (it triggers JAS multivariate factorisation). Instead every free symbol (parameters, exponents
   * and {@code x}) is given several generic values and {@code D(result) - integrand} is checked
   * against zero with a relative tolerance.
   */
  private static boolean verifyAntiderivative(IExpr result, IExpr integrand, IExpr x,
      IExpr freeSymbols, EvalEngine engine) {
    if (result.isNIL() || !result.isFreeAST(S.Integrate) || !result.isSpecialsFree()
        || !freeSymbols.isList()) {
      return false;
    }
    try {
      IExpr diff = engine.evaluate(F.Subtract(F.D(result, x), integrand));
      if (diff.isZero()) {
        // Single-power / trivial-cofactor cases cancel symbolically (no Simplify needed).
        return true;
      }
      // Keep only atomic symbols defensively (the source list is already symbols).
      IAST allVariables = (IAST) freeSymbols;
      IASTAppendable symbols = F.ListAlloc(allVariables.size());
      for (int i = 1; i < allVariables.size(); i++) {
        if (allVariables.get(i).isSymbol()) {
          symbols.append(allVariables.get(i));
        }
      }
      if (symbols.argSize() == 0) {
        return false;
      }
      for (int trial = 0; trial < 4; trial++) {
        IASTAppendable rules = F.ListAlloc(symbols.size());
        for (int i = 1; i <= symbols.argSize(); i++) {
          // Positive, non-integer, well-spread values keep polynomial bases positive so symbolic
          // powers stay real, and avoid accidental roots of the base polynomials.
          rules.append(F.Rule(symbols.get(i), F.num(0.6 + 0.37 * i + 0.13 * trial)));
        }
        double diffValue =
            engine.evaluate(F.N(F.Abs(F.ReplaceAll(diff, rules)))).toDoubleDefault(Double.NaN);
        double refValue =
            engine.evaluate(F.N(F.Abs(F.ReplaceAll(integrand, rules)))).toDoubleDefault(Double.NaN);
        if (Double.isNaN(diffValue) || Double.isNaN(refValue)) {
          return false;
        }
        if (diffValue > 1.0e-6 * (1.0 + refValue)) {
          return false;
        }
      }
      return true;
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return false;
    }
  }
}
