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
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import org.matheclipse.core.polynomials.longexponent.ExprTermOrder;
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

  /**
   * How many reduction steps {@link #multivariateDivision} may take before it gives up. Generous
   * for a real polynomial, where each step removes a leading term.
   */
  private static final int MAX_DIVISION_STEPS = 10000;

  public PolynomialReduce() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr polynomialExpr = ast.arg1();
    IExpr divisorsListExpr = ast.arg2().makeList();
    IAST variablesListExpr = (argSize == 3) ? ast.arg3().makeList() : F.CEmptyList;
    for (int i = 1; i < variablesListExpr.size(); i++) {
      IExpr variable = variablesListExpr.get(i);
      if (!variable.isVariable()) {
        // `1` is not a valid variable.
        return Errors.printMessage(S.PolynomialReduce, "ivar", F.List(variable), engine);
      }
    }

    IAST divisorsAST = (IAST) divisorsListExpr;

    try {
      TermOrder termOrder = TermOrderByName.LEX;
      termOrder = JASIExpr.monomialOrder(options[0], termOrder);

      IExpr coefficientDomain = options[1];
      if (coefficientDomain == S.RationalFunctions) {
        IExpr result =
            polynomialReduceRationals(polynomialExpr, divisorsAST, variablesListExpr, termOrder);
        // Fallback to symbolic expression reduction if BigRational conversion fails[cite: 7]
        if (result.isNIL()) {
          result = polynomialReduceExpr(polynomialExpr, divisorsAST, variablesListExpr, termOrder);
        }
        return result;
      }
      if (coefficientDomain == S.Integers) {
        return polynomialReduceIntegers(polynomialExpr, divisorsAST, variablesListExpr, termOrder);
      }
    } catch (RuntimeException rex) {
      return Errors.printMessage(S.PolynomialReduce, rex);
    }
    return F.NIL;
  }

  private static IExpr polynomialReduceExpr(IExpr polynomialExpr, IAST divisorsAST,
      IAST variablesListExpr, TermOrder termOrder) {

    List<IExpr> varList = new ArrayList<>(variablesListExpr.argSize());
    for (int i = 1; i < variablesListExpr.size(); i++) {
      varList.add(variablesListExpr.get(i));
    }

    // Initialize JASIExpr using the field-configured ExprRingFactory[cite: 3, 6]
    JASIExpr jas = new JASIExpr(varList, ExprRingFactory.CONST_FIELD, termOrder, false);
    ExprPolynomialRing exprRing =
        new ExprPolynomialRing(variablesListExpr, new ExprTermOrder(termOrder.getEvord()));
    GenPolynomialRing<IExpr> ring = jas.getPolynomialRingFactory();

    try {
      ExprPolynomial exprPoly = exprRing.create(polynomialExpr, false, true, true);
      GenPolynomial<IExpr> poly = jas.expr2IExprJAS(exprPoly);
      if (poly == null) {
        return F.NIL;
      }

      List<GenPolynomial<IExpr>> divisors = new ArrayList<>();
      for (int i = 1; i < divisorsAST.size(); i++) {
        ExprPolynomial divExprPoly = exprRing.create(divisorsAST.get(i), false, true, true);
        GenPolynomial<IExpr> divPoly = jas.expr2IExprJAS(divExprPoly);
        if (divPoly == null) {
          return F.NIL;
        }
        divisors.add(divPoly);
      }

      if (hasListCoefficient(poly)) {
        return F.NIL;
      }
      for (GenPolynomial<IExpr> divisor : divisors) {
        if (hasListCoefficient(divisor)) {
          return F.NIL;
        }
      }

      ReductionResult<IExpr> result = multivariateDivision(poly, divisors, ring);
      if (result == null) {
        return F.NIL;
      }

      IASTAppendable quotientsList = F.ListAlloc(divisors.size());
      for (GenPolynomial<IExpr> q : result.quotients) {
        quotientsList.append(jas.exprPoly2Expr(q));
      }

      IExpr remainderExpr = jas.exprPoly2Expr(result.remainder);

      // Evaluate the generated expressions to ensure F.Divide fractions are fully simplified
      EvalEngine engine = EvalEngine.get();
      return F.List(engine.evaluate(quotientsList), engine.evaluate(remainderExpr));

    } catch (RuntimeException rex) {
      return F.NIL;
    }
  }

  private static IExpr polynomialReduceRationals(IExpr polynomialExpr, IAST divisorsAST,
      IAST variablesListExpr, TermOrder termOrder) {
    JASConvert<BigRational> jas = new JASConvert<>(variablesListExpr, BigRational.ZERO, termOrder);

    GenPolynomialRing<BigRational> ring = jas.getPolynomialRingFactory();
    GenPolynomial<BigRational> poly = jas.expr2JAS(polynomialExpr, false);
    if (poly == null)
      return F.NIL; // Guard against conversion failure

    List<GenPolynomial<BigRational>> divisors = new ArrayList<>();
    for (int i = 1; i < divisorsAST.size(); i++) {
      GenPolynomial<BigRational> divPoly = jas.expr2JAS(divisorsAST.get(i), false);
      if (divPoly == null)
        return F.NIL;
      divisors.add(divPoly);
    }

    ReductionResult<BigRational> result = multivariateDivision(poly, divisors, ring);
    if (result == null) {
      return F.NIL;
    }

    IASTAppendable quotientsList = F.ListAlloc(divisors.size());
    for (GenPolynomial<BigRational> q : result.quotients) {
      quotientsList.append(jas.rationalPoly2Expr(q, false));
    }

    IExpr remainderExpr = jas.rationalPoly2Expr(result.remainder, false);
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
    if (result == null) {
      return F.NIL;
    }

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
   * Computes the symmetric quotient for BigIntegers: rounds to nearest, halves round to even.
   */
  private static BigInteger getIntegerQuotient(BigInteger c, BigInteger cDiv) {
    java.math.BigInteger jC = c.getVal();
    java.math.BigInteger jCDiv = cDiv.getVal();

    java.math.BigInteger[] qr = jC.divideAndRemainder(jCDiv);
    java.math.BigInteger q = qr[0];
    java.math.BigInteger r = qr[1];

    java.math.BigInteger absR = r.abs();
    java.math.BigInteger absDiv = jCDiv.abs();
    java.math.BigInteger[] halfRem = absDiv.divideAndRemainder(java.math.BigInteger.valueOf(2));
    java.math.BigInteger half = halfRem[0];
    boolean isTie = halfRem[1].equals(java.math.BigInteger.ZERO) && absR.equals(half);

    int cmp = absR.compareTo(half);
    if (cmp > 0 || (isTie && q.testBit(0))) {
      if (r.signum() == jCDiv.signum()) {
        q = q.add(java.math.BigInteger.ONE);
      } else {
        q = q.subtract(java.math.BigInteger.ONE);
      }
    }
    return new BigInteger(q);
  }

  /**
   * Algorithm: Multivariate Division P = q1*d1 + ... + qk*dk + r
   */
  @SuppressWarnings("unchecked")
  /**
   * Whether any coefficient of {@code poly} is itself a list.
   *
   * <p>
   * An argument like <code>{1,1,1}</code> is not a polynomial, but it converts into one all the
   * same, as a constant whose coefficient is the list. Reducing that cannot terminate: the
   * coefficient arithmetic threads a list against one of a different length, which answers a
   * Thread::tdlen message and an unevaluated product rather than a number, so nothing ever cancels
   * and each pass builds a larger expression than the last. One such call printed 524220 messages
   * - 95 distinct ones, repeated - and 40 MB of output before the step limit stopped it.
   *
   * @param poly the converted polynomial
   * @return {@code true} if it holds a coefficient no reduction can cancel
   */
  private static boolean hasListCoefficient(GenPolynomial<IExpr> poly) {
    for (IExpr coefficient : poly.getMap().values()) {
      if (coefficient.isList()) {
        return true;
      }
    }
    return false;
  }

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
    int steps = 0;
    while (!pCurr.isZERO()) {
      if (++steps > MAX_DIVISION_STEPS) {
        // A genuine reduction lowers the leading term in a well order, so this loop is finite for
        // anything that really is a polynomial. It is not for an argument that merely passes for
        // one: a coefficient which is itself a list divides and multiplies without ever
        // cancelling, so every pass yields a different polynomial rather than a smaller one, and
        // the stagnation guard below never sees the same leading term twice. Giving up is the
        // caller's cue to answer an unevaluated PolynomialReduce rather than a half-reduced one.
        return null;
      }
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
            C cDiv = div.leadingBaseCoefficient();
            C factorCoeff;

            // Compute pseudo-quotient for Integers, exact division for Fields[cite: 7]
            if (c instanceof BigInteger) {
              factorCoeff = (C) getIntegerQuotient((BigInteger) c, (BigInteger) cDiv);
            } else {
              factorCoeff = c.divide(cDiv);
            }

            if (!factorCoeff.isZERO()) {
              boolean valid = true;

              // Only strictly validate equality if it is not an Integer (pseudo-division)
              // and not an IExpr (structural equality mismatch)
              if (!(c instanceof BigInteger) && !(c instanceof IExpr)) {
                valid = factorCoeff.multiply(cDiv).equals(c);
              }

              if (valid) {
                ExpVector factorExp = e.subtract(eDiv);

                // Term S = (LT(p)/LT(div))
                GenPolynomial<C> S = ring.getONE().multiply(factorCoeff, factorExp);

                // Update quotient i: qi = qi + S
                quotients.set(i, quotients.get(i).sum(S));

                // Update polynomial: p = p - S * div
                GenPolynomial<C> reduced = pCurr.subtract(S.multiply(div));
                if (reduced.equals(pCurr)) {
                  // The subtraction cancelled nothing, so this is not progress. It happens with
                  // IExpr coefficients, whose quotient is accepted above without checking that
                  // factorCoeff * cDiv is back to c - there is no reliable structural equality to
                  // check it with. Counting it as a division would reset the stagnation guard
                  // below on every pass and the loop would never end. Leave the term to the next
                  // divisor, or to the remainder.
                  continue;
                }
                pCurr = reduced;

                divisionOccurred = true;
                lastExp = null;
                stagnationCount = 0;
                break; // Restart the loop with the new pCurr
              }
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
