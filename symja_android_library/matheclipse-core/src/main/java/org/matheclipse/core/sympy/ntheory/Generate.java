package org.matheclipse.core.sympy.ntheory;

import java.math.BigInteger;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IInteger;

public class Generate {
  public static IAST primeRange(IInteger a) {
    return primeRange(a, F.C0);
  }

  public static IAST primeRange(long a, long b) {
    return primeRange(F.ZZ(a), F.ZZ(b));
  }

  public static IAST primeRange(IInteger a, IInteger b) {
    IASTAppendable list = F.ListAlloc();
    if (b.isZero()) {
      b = a;
      a = F.C2;
    }
    if (a.isGT(b)) {
      return F.CEmptyList;
    }
    BigInteger primeCandidate = a.toBigNumerator();
    BigInteger bb = b.toBigNumerator();
    if (primeCandidate.isProbablePrime(IInteger.PRIME_CERTAINTY)) {
      list.append(F.ZZ(primeCandidate));
    }
    do {
      primeCandidate = primeCandidate.nextProbablePrime();
      if (bb.compareTo(primeCandidate) <= 0) {
        break;
      }
      list.append(F.ZZ(primeCandidate));
    } while (true);
    return list;
  }

  public static IInteger nextPrime(IInteger a) {
    BigInteger primeCandidate = a.toBigNumerator();
    primeCandidate = primeCandidate.nextProbablePrime();
    return F.ZZ(primeCandidate);
  }
}
