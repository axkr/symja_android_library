package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class PrimitivePolynomialQ extends AbstractFunctionEvaluator {

  public PrimitivePolynomialQ() {}


  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr poly = ast.arg1();
    IExpr pArg = ast.arg2();

    // The modulus p must be a (positive) prime integer.
    if (!pArg.isInteger()) {
      return S.False;
    }
    IInteger p = (IInteger) pArg;
    if (!p.isProbablePrime()) {
      return S.False;
    }
    if (p.isNegative()) {
      // switch to positive modulus if a negative one is given
      // TODO write message "badmod"?
      p = p.negate();
    }

    // Determine the (single) variable of poly.
    VariablesSet variablesSet = new VariablesSet(poly);
    IAST variables = variablesSet.getVarList();
    if (variables.argSize() != 1) {
      // Constants and multivariate polynomials are never primitive.
      return S.False;
    }
    IExpr var = variables.arg1();

    // Structural polynomial check.
    IExpr isPoly = engine.evaluate(F.PolynomialQ(poly, variables));
    if (!isPoly.isTrue()) {
      return S.False;
    }

    // Reduce coefficients modulo p.
    IExpr polyModP = engine.evaluate(F.binaryAST2(S.PolynomialMod, poly, p));
    if (!polyModP.isPresent() || polyModP.isZero()) {
      return S.False;
    }

    // Degree n = Exponent[polyModP, var].
    IExpr degExpr = engine.evaluate(F.Exponent(polyModP, var));
    if (!degExpr.isInteger()) {
      return F.NIL;
    }
    IInteger nInt = (IInteger) degExpr;
    if (!nInt.isPositive()) {
      // Degree 0 or negative => constant mod p (or zero polynomial) => not primitive.
      return S.False;
    }
    int n = nInt.toIntDefault();
    if (n < 1) {
      // Degree too large to handle as a primitive Java int.
      return F.NIL;
    }

    // Leading coefficient (mod p).
    IExpr lc =
        engine.evaluate(F.binaryAST2(S.PolynomialMod, F.Coefficient(polyModP, var, nInt), p));
    if (!lc.isInteger() || lc.isZero()) {
      return S.False;
    }

    // Make monic if needed: multiply by the modular inverse of the leading coefficient.
    IExpr monicPoly = polyModP;
    if (!lc.isOne()) {
      IExpr lcInv = engine.evaluate(F.PowerMod(lc, F.CN1, p));
      if (!lcInv.isInteger()) {
        return F.NIL;
      }
      monicPoly =
          engine.evaluate(F.binaryAST2(S.PolynomialMod, F.Expand(F.Times(lcInv, polyModP)), p));
      if (!monicPoly.isPresent()) {
        return F.NIL;
      }
    }

    // Step (1): primitivity requires irreducibility over GF(p).
    IExpr irred =
        engine.evaluate(F.binaryAST2(S.IrreduciblePolynomialQ, monicPoly, F.Rule(S.Modulus, p)));
    if (irred.isFalse()) {
      return S.False;
    }
    if (!irred.isTrue()) {
      return F.NIL;
    }

    // Step (2): x must have multiplicative order p^n - 1 modulo (monicPoly, p).
    BigInteger pBig = p.toBigNumerator();
    BigInteger mBig = pBig.pow(n).subtract(BigInteger.ONE);
    if (mBig.signum() <= 0) {
      // n >= 1 and p >= 2 guarantee m >= 1; this is just a safety net.
      return S.True;
    }

    IInteger m = F.ZZ(mBig);
    IExpr factored = engine.evaluate(F.FactorInteger(m));
    if (!factored.isList()) {
      return F.NIL;
    }
    IAST factorList = (IAST) factored;

    for (int i = 1; i <= factorList.argSize(); i++) {
      IExpr pair = factorList.get(i);
      if (!pair.isList() || pair.size() != 3) {
        return F.NIL;
      }
      IExpr first = pair.first();
      if (!first.isInteger()) {
        return F.NIL;
      }
      IInteger q = (IInteger) first;
      BigInteger eBig = mBig.divide(q.toBigNumerator());
      IExpr xPow = modPolyPower(var, eBig, monicPoly, var, p, engine);
      if (!xPow.isPresent()) {
        return F.NIL;
      }
      if (xPow.isOne()) {
        // The order of x divides m/q < m, so it is not primitive.
        return S.False;
      }
    }
    return S.True;
  }

  /**
   * Compute <code>base^exponent mod (modulusPoly, p)</code> using square-and-multiply.
   *
   * @param base the polynomial base (typically the polynomial variable <code>var</code>)
   * @param exponent the non-negative exponent
   * @param modulusPoly the monic modulus polynomial (already reduced mod p)
   * @param var the polynomial variable
   * @param p the prime modulus on the coefficients
   * @param engine the evaluation engine
   * @return the reduced polynomial, or {@link F#NIL} when an intermediate evaluation fails
   */
  private static IExpr modPolyPower(IExpr base, BigInteger exponent, IExpr modulusPoly, IExpr var,
      IInteger p, EvalEngine engine) {
    IExpr result = F.C1;
    IExpr b = base;
    BigInteger e = exponent;
    while (e.signum() > 0) {
      if (e.testBit(0)) {
        result = reduceModPoly(F.Times(result, b), modulusPoly, var, p, engine);
        if (!result.isPresent()) {
          return F.NIL;
        }
      }
      e = e.shiftRight(1);
      if (e.signum() > 0) {
        b = reduceModPoly(F.Times(b, b), modulusPoly, var, p, engine);
        if (!b.isPresent()) {
          return F.NIL;
        }
      }
    }
    return result;
  }

  /**
   * Reduce <code>expr</code> first by polynomial division through <code>modulusPoly</code> in
   * <code>var</code>, then take coefficients modulo <code>p</code>.
   */
  private static IExpr reduceModPoly(IExpr expr, IExpr modulusPoly, IExpr var, IInteger p,
      EvalEngine engine) {
    IExpr expanded = engine.evaluate(F.Expand(expr));
    if (!expanded.isPresent()) {
      return F.NIL;
    }
    IExpr rem = engine.evaluate(F.PolynomialRemainder(expanded, modulusPoly, var));
    if (!rem.isPresent() || rem.isAST(S.PolynomialRemainder)) {
      return F.NIL;
    }
    IExpr modded = engine.evaluate(F.binaryAST2(S.PolynomialMod, rem, p));
    if (!modded.isPresent() || modded.isAST(S.PolynomialMod)) {
      return F.NIL;
    }
    return modded;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}

  @Override
  public int status() {
    return ImplementationStatus.FULL_SUPPORT;
  }

}
