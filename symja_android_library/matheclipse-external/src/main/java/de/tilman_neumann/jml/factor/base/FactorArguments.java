package de.tilman_neumann.jml.factor.base;

import java.math.BigInteger;

public class FactorArguments {
  /** The number to factor */
  public BigInteger N;

  /** The number of bits of N */
  public int NBits;

  /** The exponent of N */
  public int exp;

  /**
   * the smallest factor that could occur, e.g. because smaller factors have been excluded by trial
   * division
   */
  public long smallestPossibleFactor;

  /**
   * Full constructor.
   *
   * @param N the number to factor
   * @param exp the exponent of N
   * @param smallestPossibleFactor the smallest factor that could occur this parameter may be
   *     ignored by algorithms where it does not make sense
   */
  public FactorArguments(BigInteger N, int exp, long smallestPossibleFactor) {
    this.N = N;
    this.NBits = N.bitLength();
    this.exp = exp;
    this.smallestPossibleFactor = smallestPossibleFactor;
  }
}
