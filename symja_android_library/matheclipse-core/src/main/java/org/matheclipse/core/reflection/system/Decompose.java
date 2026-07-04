package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.BartonZippelDecomposition;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;

/**
 * <h2>Decompose</h2>
 *
 * <pre>
 * Decompose(poly, x)
 * </pre>
 *
 * <blockquote>
 * <p>
 * finds a list of polynomials p_1(x), p_2(x), ... such that poly = p_1(p_2(...(x)...)).
 * </p>
 * </blockquote>
 */
public class Decompose extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr poly = ast.arg1();
    IExpr x = ast.arg2();

    if (poly.equals(x)) {
      IASTAppendable list = F.ListAlloc(1);
      list.append(x);
      return list;
    }

    // 1. Explicitly expand the polynomial so JAS can reliably parse all terms
    IExpr expandedPoly = engine.evaluate(F.Expand(poly));

    IExpr modulus = options[0];
    if (!modulus.isZero() && modulus.isInteger()) {
      try {
        java.math.BigInteger modVal = ((IInteger) modulus).toBigNumerator();
        ModIntegerRing modRing = new ModIntegerRing(modVal);
        JASConvert<ModInteger> jasMod = new JASConvert<>(F.List(x), modRing);
        GenPolynomial<ModInteger> polyMod = jasMod.expr2JAS(expandedPoly, false);

        if (polyMod != null) {
          // Perform generic decomposition over ModInteger
          List<GenPolynomial<ModInteger>> resultList = recursiveDecompose(polyMod);

          IASTAppendable list = F.ListAlloc(resultList.size());
          for (GenPolynomial<ModInteger> p : resultList) {
            list.append(modPolyToExpr(p, x));
          }
          return list;
        }
      } catch (Exception e) {
        // Drop down to fallback if modular conversion or inversion fails
        // (e.g. division by a non-invertible element in a composite modulus ring).
      }

      // Modular fallback: return modulo'd polynomial as a single element
      IASTAppendable result = F.ListAlloc(1);
      result.append(engine.evaluate(F.PolynomialMod(expandedPoly, modulus)));
      return result;
    }

    try {
      // 2. Convert Symja expression to JAS GenPolynomial over BigRational
      JASConvert<BigRational> jas = new JASConvert<BigRational>(F.List(x), BigRational.ZERO);
      GenPolynomial<BigRational> polyRat = jas.expr2JAS(expandedPoly, false);

      if (polyRat != null) {
        // Perform recursive decomposition
        List<GenPolynomial<BigRational>> resultList = recursiveDecompose(polyRat);

        // Convert results back to Symja
        IASTAppendable list = F.ListAlloc(resultList.size());
        for (GenPolynomial<BigRational> p : resultList) {
          list.append(jas.rationalPoly2Expr(p, false));
        }
        return list;
      }
    } catch (Exception e) {
      // Fallback below on conversion exceptions
    }

    // Fallback: return the explicitly expanded original polynomial as a single-element list.
    IASTAppendable result = F.ListAlloc(1);
    result.append(expandedPoly);
    return result;
  }

  /**
   * Helper method to map GenPolynomial<ModInteger> directly to IExpr structure.
   */
  private IExpr modPolyToExpr(GenPolynomial<ModInteger> poly, IExpr x) {
    if (poly.isZERO()) {
      return F.C0;
    }
    IASTAppendable plus = F.PlusAlloc(poly.length());
    for (Map.Entry<ExpVector, ModInteger> entry : poly.getMap().entrySet()) {
      long exp = entry.getKey().getVal(0);
      java.math.BigInteger val = entry.getValue().getVal();
      IExpr coeff = F.ZZ(val);

      if (exp == 0) {
        plus.append(coeff);
      } else {
        IExpr xPow = (exp == 1) ? x : F.Power(x, F.ZZ(exp));
        if (val.equals(java.math.BigInteger.ONE)) {
          plus.append(xPow);
        } else {
          plus.append(F.Times(coeff, xPow));
        }
      }
    }
    return plus.oneIdentity0();
  }

  /**
   * Recursively decomposes a polynomial generically over any RingElem structure.
   */
  private <C extends RingElem<C>> List<GenPolynomial<C>> recursiveDecompose(GenPolynomial<C> p) {
    // Base case: constant or isolated monomial
    if (p.length() <= 1) {
      return Collections.singletonList(p);
    }

    // 1. Check for common exponent GCD (e.g. x^4 + 1 -> GCD is 4)
    long gcd = getExponentGCD(p);
    if (gcd > 1) {
      GenPolynomial<C> g = divideExponents(p, gcd);
      GenPolynomial<C> h = p.ring.univariate(0, gcd);

      List<GenPolynomial<C>> result = new ArrayList<>();
      result.addAll(recursiveDecompose(g));
      result.add(h);
      return result;
    }

    // 2. Try Barton-Zippel for deeper compositional structures
    List<GenPolynomial<C>> split = BartonZippelDecomposition.decompose(p);
    if (split != null) {
      List<GenPolynomial<C>> result = new ArrayList<>();
      result.addAll(recursiveDecompose(split.get(0)));
      result.addAll(recursiveDecompose(split.get(1)));
      return result;
    }

    // Base case: No further decomposition found
    return Collections.singletonList(p);
  }

  /**
   * Calculates the greatest common divisor of all strictly positive exponents in the polynomial.
   */
  private <C extends RingElem<C>> long getExponentGCD(GenPolynomial<C> p) {
    long gcd = 0;
    for (ExpVector exp : p.getMap().keySet()) {
      long e = exp.getVal(0);
      if (e > 0) {
        gcd = mathGCD(gcd, e);
        if (gcd == 1) {
          return 1; // Fast exit if coprime
        }
      }
    }
    return gcd;
  }

  private long mathGCD(long a, long b) {
    if (a == 0)
      return b;
    if (b == 0)
      return a;
    long absA = Math.abs(a);
    long absB = Math.abs(b);
    while (absB != 0) {
      long temp = absB;
      absB = absA % absB;
      absA = temp;
    }
    return absA;
  }

  /**
   * Constructs a new polynomial by dividing all exponents by the provided GCD.
   */
  private <C extends RingElem<C>> GenPolynomial<C> divideExponents(GenPolynomial<C> p, long gcd) {
    GenPolynomialRing<C> ring = p.ring;
    GenPolynomial<C> result = ring.getZERO();
    for (Map.Entry<ExpVector, C> entry : p.getMap().entrySet()) {
      long e = entry.getKey().getVal(0);
      long newE = e / gcd;
      GenPolynomial<C> term = ring.univariate(0, newE).multiply(entry.getValue());
      result = result.sum(term);
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Modulus}, // (Corrected from S.Method so options[0] binds correctly)
        new IExpr[] {F.C0});
  }
}