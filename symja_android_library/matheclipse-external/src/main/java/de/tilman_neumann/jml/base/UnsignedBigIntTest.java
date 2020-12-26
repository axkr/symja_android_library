package de.tilman_neumann.jml.base;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.UnsignedBigInt;
import de.tilman_neumann.util.ConfigUtil;

/**
 * Test for UnsignedBigInt classes.
 *
 * @author Tilman Neumann
 */
public class UnsignedBigIntTest {
  private static final Logger LOG = Logger.getLogger(UnsignedBigIntTest.class);
  private static final SecureRandom RNG = new SecureRandom();

  /**
   * create test set for performance test: random ints with random bit length < 1000
   *
   * @param nCount
   * @return
   */
  private static ArrayList<BigInteger> createTestSet(int nCount, int bits) {
    ArrayList<BigInteger> testSet = new ArrayList<BigInteger>();
    for (int i = 0; i < nCount; ) {
      BigInteger testNum = new BigInteger(bits, RNG);
      if (testNum.equals(I_0)) continue;
      testSet.add(testNum);
      i++;
    }
    return testSet;
  }

  private static void testCorrectness(int nCount) {
    // test conversion
    for (int bits = 100; bits <= 1000; bits += 100) {
      ArrayList<BigInteger> testSet = createTestSet(nCount, bits);
      for (BigInteger testNum : testSet) {
        UnsignedBigInt N = new UnsignedBigInt(testNum);
        BigInteger reverse = N.toBigInteger();
        if (!testNum.equals(reverse)) {
          LOG.error(
              "ERROR: conversion of " + testNum + " to UnsignedBigInt and back gave " + reverse);
        }
      }
      LOG.info("Tested correctness of " + nCount + " conversions of " + bits + "-bit numbers...");
    }

    // test division
    UnsignedBigInt quotient = new UnsignedBigInt(new int[32]); // buffer big enough for 1000 bits
    for (int bits = 100; bits <= 1000; bits += 100) {
      ArrayList<BigInteger> testSet = createTestSet(nCount, bits);
      for (BigInteger testNum : testSet) {
        int divisor = Math.max(2, RNG.nextInt(Integer.MAX_VALUE - 2));
        BigInteger[] referenceResult = testNum.divideAndRemainder(BigInteger.valueOf(divisor));
        int remainder = new UnsignedBigInt(testNum).divideAndRemainder(divisor, quotient);
        if (!quotient.toBigInteger().equals(referenceResult[0])) {
          LOG.error(
              "ERROR: divide("
                  + testNum
                  + ", "
                  + divisor
                  + "): correct quotient = "
                  + referenceResult[0]
                  + ", my result = "
                  + quotient);
        }
        if (remainder != referenceResult[1].intValue()) {
          LOG.error(
              "ERROR: divide("
                  + testNum
                  + ", "
                  + divisor
                  + "): correct remainder = "
                  + referenceResult[1]
                  + ", my result = "
                  + remainder);
        }
      }
      LOG.info(
          "Tested correctness of " + nCount + " divisions of " + bits + "-bit numbers by ints...");
    }

    // test modulus
    for (int bits = 100; bits <= 1000; bits += 100) {
      ArrayList<BigInteger> testSet = createTestSet(nCount, bits);
      for (BigInteger testNum : testSet) {
        int divisor = Math.max(2, RNG.nextInt(Integer.MAX_VALUE - 2));
        int correctRemainder = testNum.mod(BigInteger.valueOf(divisor)).intValue();
        int remainder = new UnsignedBigInt(testNum).mod(divisor);
        if (remainder != correctRemainder) {
          LOG.error(
              "ERROR: mod("
                  + testNum
                  + ", "
                  + divisor
                  + "): correct remainder = "
                  + correctRemainder
                  + ", my result = "
                  + remainder);
        }
      }
      LOG.info(
          "Tested correctness of "
              + nCount
              + " modulus computations of "
              + bits
              + "-bit numbers by ints...");
    }
  }

  private static void testPerformance(int nCount) {
    // test division performance
    UnsignedBigInt quotient = new UnsignedBigInt(new int[32]); // buffer big enough for 1000 bits
    int[] divisors = new int[1000];
    BigInteger[] divisors_big = new BigInteger[1000];
    for (int i = 0; i < 1000; i++) {
      divisors[i] = Math.max(2, RNG.nextInt(Integer.MAX_VALUE - 2));
      divisors_big[i] = BigInteger.valueOf(divisors[i]);
    }
    for (int bits = 100; bits <= 1000; bits += 100) {
      ArrayList<BigInteger> testSet = createTestSet(nCount, bits);
      ArrayList<UnsignedBigInt> testSet_UBI = new ArrayList<UnsignedBigInt>();
      for (BigInteger testNum : testSet) {
        testSet_UBI.add(new UnsignedBigInt(testNum));
      }

      long t0, t1;
      LOG.info("Test division performance of " + nCount + " " + bits + "-bit numbers:");
      t0 = System.currentTimeMillis();
      for (BigInteger divisor_big : divisors_big) {
        for (BigInteger testNum : testSet) {
          @SuppressWarnings("unused")
          BigInteger result = testNum.mod(divisor_big);
        }
      }
      t1 = System.currentTimeMillis();
      LOG.info("   Java's mod() took " + (t1 - t0) + " ms");

      t0 = System.currentTimeMillis();
      for (int divisor : divisors) {
        for (UnsignedBigInt testNum : testSet_UBI) {
          testNum.mod(divisor);
        }
      }
      t1 = System.currentTimeMillis();
      LOG.info("   UBI's mod() took " + (t1 - t0) + " ms");

      t0 = System.currentTimeMillis();
      for (BigInteger divisor_big : divisors_big) {
        for (BigInteger testNum : testSet) {
          @SuppressWarnings("unused")
          BigInteger[] result = testNum.divideAndRemainder(divisor_big);
        }
      }
      t1 = System.currentTimeMillis();
      LOG.info("   Java's divide() took " + (t1 - t0) + " ms");

      // divide(): slightly better than Java
      t0 = System.currentTimeMillis();
      for (int divisor : divisors) {
        for (UnsignedBigInt testNum : testSet_UBI) {
          testNum.divideAndRemainder(divisor, quotient);
        }
      }
      t1 = System.currentTimeMillis();
      LOG.info("   UBI's divide() took " + (t1 - t0) + " ms");
    }
  }

  /**
   * Test.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    ConfigUtil.initProject();

    testCorrectness(100000);
    testPerformance(10000);
  }
}
