package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;

/**
 * Implementation of PolynomialReduce using JAS. Syntax: PolynomialReduce(poly, {divisors}, {vars})
 */
public class PolynomialReduce extends AbstractFunctionOptionEvaluator {

  public PolynomialReduce() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr polynomialExpr = ast.arg1();
    IExpr divisorsListExpr = ast.arg2().makeList();
    IAST variablesListExpr = (argSize == 3) ? ast.arg3().makeList() : F.CEmptyList;

    IAST divisorsAST = (IAST) divisorsListExpr;

    try {
      TermOrder termOrder = TermOrderByName.LEX;
      termOrder = JASIExpr.monomialOrder(options[0], termOrder);

      IExpr coefficientDomain = options[1];
      if (coefficientDomain == S.RationalFunctions) {
        return polynomialReduceRationals(polynomialExpr, divisorsAST, variablesListExpr, termOrder);
      }
      // TODO
      // if (coefficientDomain == S.Integers) {
      // return polynomialReduceIntegers(polynomialExpr, divisorsAST, variablesListExpr, termOrder);
      // }
    } catch (RuntimeException rex) {
      return Errors.printMessage(S.PolynomialReduce, rex);
    }
    return F.NIL;
  }

  private static IExpr polynomialReduceRationals(IExpr polynomialExpr, IAST divisorsAST,
      IAST variablesListExpr, TermOrder termOrder) {
    JASConvert<BigRational> jas = new JASConvert<>(variablesListExpr, BigRational.ZERO, termOrder);

    // Create ring and polynomials
    GenPolynomialRing<BigRational> ring = jas.getPolynomialRingFactory();
    GenPolynomial<BigRational> poly = jas.expr2JAS(polynomialExpr, false);

    List<GenPolynomial<BigRational>> divisors = new ArrayList<>();
    for (int i = 1; i < divisorsAST.size(); i++) {
      divisors.add(jas.expr2JAS(divisorsAST.get(i), false));
    }

    ReductionResult<BigRational> result = multivariateDivision(poly, divisors, ring);

    // Return format: {{q1, q2, ...}, remainder}
    IASTAppendable quotientsList = F.ListAlloc(divisors.size());
    for (GenPolynomial<BigRational> q : result.quotients) {
      quotientsList.append(jas.rationalPoly2Expr(q, false));
    }

    IExpr remainderExpr = jas.rationalPoly2Expr(result.remainder, false);

    // Final list {{...}, r}
    return F.List(quotientsList, remainderExpr);
  }

  private static IExpr polynomialReduceIntegers(IExpr polynomialExpr, IAST divisorsAST,
      IAST variablesListExpr, TermOrder termOrder) {
    JASConvert<BigInteger> jas = new JASConvert<>(variablesListExpr, BigInteger.ZERO, termOrder);

    // Create ring and polynomials
    GenPolynomialRing<BigInteger> ring = jas.getPolynomialRingFactory();
    GenPolynomial<BigInteger> poly = jas.expr2JAS(polynomialExpr, false);

    List<GenPolynomial<BigInteger>> divisors = new ArrayList<>();
    for (int i = 1; i < divisorsAST.size(); i++) {
      divisors.add(jas.expr2JAS(divisorsAST.get(i), false));
    }

    ReductionResult<BigInteger> result = multivariateDivision(poly, divisors, ring);

    // Return format: {{q1, q2, ...}, remainder}
    IASTAppendable quotientsList = F.ListAlloc(divisors.size());
    for (GenPolynomial<BigInteger> q : result.quotients) {
      quotientsList.append(jas.integerPoly2Expr(q));
    }

    IExpr remainderExpr = jas.integerPoly2Expr(result.remainder);

    // Final list {{...}, r}
    return F.List(quotientsList, remainderExpr);
  }

  private static class ReductionResult<C extends edu.jas.structure.RingElem<C>> {
    List<GenPolynomial<C>> quotients;
    GenPolynomial<C> remainder;

    public ReductionResult(List<GenPolynomial<C>> quotients, GenPolynomial<C> remainder) {
      this.quotients = quotients;
      this.remainder = remainder;
    }
  }

  /**
   * Algorithm: Multivariate Division P = q1*d1 + ... + qk*dk + r
   */
  private static <C extends edu.jas.structure.RingElem<C>> ReductionResult<C> multivariateDivision(
      GenPolynomial<C> P, List<GenPolynomial<C>> divisors, GenPolynomialRing<C> ring) {

    // Initialize quotients with 0
    List<GenPolynomial<C>> quotients = new ArrayList<>(divisors.size());
    for (int i = 0; i < divisors.size(); i++) {
      quotients.add(ring.getZERO());
    }

    GenPolynomial<C> remainder = ring.getZERO();
    GenPolynomial<C> pCurr = P;

    ExpVector lastExp = null;
    int stagnationCount = 0;

    // As long as the polynomial is not 0
    while (!pCurr.isZERO()) {
      boolean divisionOccurred = false;
      ExpVector e = pCurr.leadingExpVector();
      C c = pCurr.leadingBaseCoefficient();

      if (lastExp != null && lastExp.equals(e)) {
        stagnationCount++;
        if (stagnationCount > divisors.size()) {
          // Force moving the term to the remainder if we are stuck
          divisionOccurred = false;
        }
      } else {
        lastExp = e;
        stagnationCount = 0;
      }

      if (stagnationCount <= divisors.size()) {
        // Try to divide by each divisor
        for (int i = 0; i < divisors.size(); i++) {
          GenPolynomial<C> div = divisors.get(i);
          if (div.isZERO())
            continue;

          ExpVector eDiv = div.leadingExpVector();

          // Check if term is divisible (exponent difference not negative)
          if (e.multipleOf(eDiv)) {
            // Division possible
            C cDiv = div.leadingBaseCoefficient();

            // ATTENTION: For integers (BigInteger), exact division may not always work.
            // JAS "divide" throws an error or returns a remainder if not divisible in the ring.
            // For a Field (Rational) it is trivial. For an Integer Ring it must be exact.
            C factorCoeff = c.divide(cDiv);

            // If we are in Z and there is a remainder for the coefficients,
            // this term cannot be reduced (pseudo-division would be an alternative,
            // but PolynomialReduce often expects Field behavior or an exact Ring).
            // We assume here that the division works or we skip.
            if (factorCoeff.multiply(cDiv).equals(c)) {
              ExpVector factorExp = e.subtract(eDiv);

              // Term S = (LT(p)/LT(div))
              GenPolynomial<C> S = ring.getONE().multiply(factorCoeff, factorExp);

              // Update quotient i: qi = qi + S
              quotients.set(i, quotients.get(i).sum(S));

              // Update polynomial: p = p - S * div
              pCurr = pCurr.subtract(S.multiply(div));

              divisionOccurred = true;
              // A successful division occurred, so we are not stagnating.
              // Reset the stagnation detection.
              lastExp = null;
              stagnationCount = 0;
              break; // Restart the loop with the new pCurr
            }
          }
        }
      }

      if (!divisionOccurred) {
        // LT(p) is not divisible by any divisor -> move to remainder
        GenPolynomial<C> lt = ring.getONE().multiply(c, e);
        remainder = remainder.sum(lt);

        // Remove term from pCurr
        pCurr = pCurr.subtract(lt);

        // After moving to remainder, the leading term is gone,
        // so reset stagnation detection.
        lastExp = null;
        stagnationCount = 0;
      }
    }

    return new ReductionResult<>(quotients, remainder);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    IBuiltInSymbol[] optionKeys = new IBuiltInSymbol[] {S.MonomialOrder, S.CoefficientDomain};
    IExpr[] optionValues = new IExpr[] {S.Lexicographic, S.RationalFunctions};
    setOptions(newSymbol, optionKeys, optionValues);
  }

}
