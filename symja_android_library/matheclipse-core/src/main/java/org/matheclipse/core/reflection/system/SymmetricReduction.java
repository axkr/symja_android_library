package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import edu.jas.arith.BigInteger;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;

/**
 * SymmetricReduction as JAS implementation. Syntax: 1. SymmetricReduction(poly, {x1, ..., xn}) 2.
 * SymmetricReduction(poly, {x1, ..., xn}, {s1, ..., sn})
 */
public class SymmetricReduction extends AbstractFunctionEvaluator {

  public SymmetricReduction() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {

    IExpr polyExpr = ast.arg1();
    IExpr varsExpr = ast.arg2();
    IExpr symbolsExpr = (ast.size() == 4) ? ast.arg3().makeList() : F.NIL;

    IAST varsList = varsExpr.makeList();
    if (varsList.size() == 1 || varsList.exists(expr -> !expr.isVariable())) {
      // `1` is not list of variables.
      return Errors.printMessage(S.SymmetricReduction, "nvarl", F.List(varsList));
    }
    int n = varsList.size() - 1;

    if (!polyExpr.isPolynomial(varsList)) {
      // `1` is not a polynomial.
      return Errors.printMessage(S.SymmetricReduction, "poly", F.List(polyExpr));
    }

    IAST symPolySymbols = F.NIL;
    if (symbolsExpr.isList()) {
      symPolySymbols = (IAST) symbolsExpr;
      if (symPolySymbols.size() != varsList.size()) {
        // Lists `1` and `2` have different lengths.
        return Errors.printMessage(S.SymmetricReduction, "neql", F.List(varsList, symPolySymbols));
      }
    }

    try {
      // force lexikographic order (x1 > x2 > ... > xn)
      TermOrder termOrder = new TermOrder(TermOrder.LEX);
      JASConvert<BigInteger> jas = new JASConvert<>(varsList, BigInteger.ZERO, termOrder);

      // Create ring and polynomials
      GenPolynomialRing<BigInteger> ring = jas.getPolynomialRingFactory();
      GenPolynomial<BigInteger> poly = jas.expr2JAS(polyExpr, false);

      // Elementarsymmetric polynomial in JAS (e1, e2, ..., en)
      // e1 = x1 + ... + xn
      // e2 = x1x2 + x1x3 + ...
      List<GenPolynomial<BigInteger>> elementaryPolys = new ArrayList<>(n);
      for (int k = 1; k <= n; k++) {
        elementaryPolys.add(generateElementarySymmetricPoly(ring, k, n));
      }

      IExpr symmetricPart = F.C0;
      GenPolynomial<BigInteger> remainderJAS = ring.getZERO();

      GenPolynomial<BigInteger> pCurr = poly;

      // reduction -loop
      while (!pCurr.isZERO()) {
        ExpVector e = pCurr.leadingExpVector();
        BigInteger c = pCurr.leadingBaseCoefficient();

        // Symja: {x, y, z} -> x is variable 1, y variable 2 ...
        // JAS ExpVector: index 0 is z, index 1 is y, index 2 is x (reverse order)

        boolean isSymmetricTerm = true;
        long[] d = new long[n];

        for (int k = 0; k < n - 1; k++) {
          int jasIndexCurrent = n - 1 - k;
          int jasIndexNext = n - 1 - (k + 1);

          long expCurrent = e.getVal(jasIndexCurrent);
          long expNext = e.getVal(jasIndexNext);

          // symmetry-check: a_k >= a_{k+1}
          if (expCurrent < expNext) {
            isSymmetricTerm = false;
            break;
          }
          d[k] = expCurrent - expNext;
        }

        if (isSymmetricTerm) {
          d[n - 1] = e.getVal(0);
          GenPolynomial<BigInteger> subtrahend = ring.getONE().multiply(c);

          IExpr resultTerm = F.ZZ(c.val);

          for (int k = 0; k < n; k++) {
            if (d[k] > 0) {
              GenPolynomial<BigInteger> ek = elementaryPolys.get(k);
              subtrahend = subtrahend.multiply(ek.power(d[k]));

              IExpr symSymbol;
              if (symPolySymbols.isPresent()) {
                symSymbol = symPolySymbols.get(k + 1);
              } else {
                ISymbol symPolyHead = S.SymmetricPolynomial;
                symSymbol = F.function(symPolyHead, F.ZZ(k + 1), varsList);
              }

              resultTerm = F.Times(resultTerm, F.Power(symSymbol, F.ZZ(d[k])));
            }
          }

          // Important: the subtraction must eliminate the leading term
          pCurr = pCurr.subtract(subtrahend);
          symmetricPart = symmetricPart.plus(resultTerm);

        } else {
          // move to the rest
          GenPolynomial<BigInteger> lt = ring.getZERO().sum(c, e);
          remainderJAS = remainderJAS.sum(lt);
          pCurr = pCurr.subtract(lt);
        }
      }

      IExpr remainderExpr = jas.integerPoly2Expr(remainderJAS);

      return F.List(symmetricPart, remainderExpr);

    } catch (RuntimeException rex) {
      return Errors.printMessage(S.SymmetricReduction, rex);
    }
  }

  private static GenPolynomial<BigInteger> generateElementarySymmetricPoly(
      GenPolynomialRing<BigInteger> ring, int k, int n) {
    if (k == 0) {
      return ring.getONE();
    }
    if (k > n) {
      return ring.getZERO();
    }
    return elemSymRec(ring, 0, k, n);
  }

  /**
   * Generates the k-th elementary symmetric polynomial recursively.
   * 
   * @param ring
   * @param startIdx
   * @param depth
   * @param n
   * @return
   */
  private static GenPolynomial<BigInteger> elemSymRec(GenPolynomialRing<BigInteger> ring,
      int startIdx, int depth, int n) {
    if (depth == 0) {
      return ring.getONE();
    }

    GenPolynomial<BigInteger> sum = ring.getZERO();
    for (int i = startIdx; i <= n - depth; i++) {
      int jasIndex = n - 1 - i;
      GenPolynomial<BigInteger> xi = ring.univariate(jasIndex);
      GenPolynomial<BigInteger> rest = elemSymRec(ring, i + 1, depth - 1, n);

      sum = sum.sum(xi.multiply(rest));
    }
    return sum;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }
}
