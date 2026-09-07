package org.matheclipse.core.reduce;

import java.math.BigInteger;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/** The discrete domain a reduction is carried out over. */
public enum IntegerDomain {
  INTEGERS, PRIMES, RATIONALS;

  public static IntegerDomain of(ISymbol domain) {
    if (domain == S.Integers) {
      return INTEGERS;
    }
    if (domain == S.Primes) {
      return PRIMES;
    }
    if (domain == S.Rationals) {
      return RATIONALS;
    }
    return null;
  }

  public ISymbol symbol() {
    switch (this) {
      case PRIMES:
        return S.Primes;
      case RATIONALS:
        return S.Rationals;
      default:
        return S.Integers;
    }
  }

  /** The smallest member of the domain, or <code>null</code> if it is unbounded below. */
  public BigInteger lowerBound() {
    return this == PRIMES ? BigInteger.TWO : null;
  }

  /**
   * Test whether a value belongs to this domain. A prime is positive by definition, so a negative
   * value is rejected even though <code>PrimeQ</code> accepts it.
   */
  public boolean accepts(BigInteger value) {
    if (this != PRIMES) {
      return true;
    }
    if (value.signum() <= 0) {
      return false;
    }
    IInteger candidate = F.ZZ(value);
    return candidate.isProbablePrime();
  }
}
