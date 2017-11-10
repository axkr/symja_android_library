package org.matheclipse.core.numbertheory;

import java.math.BigInteger;
//<XMP>
//Gaussian Integer factorization applet
//
//Written by Dario Alejandro Alpern (Buenos Aires - Argentina)
//Last updated May 31st, 2002, See http://www.alpertron.com.ar/GAUSSIAN.HTM
//
//No part of this code can be used for commercial purposes without
//the written consent from the author. Otherwise it can be used freely
//except that you have to write somewhere in the code this header.
//
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.matheclipse.core.expression.ComplexSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public final class GaussianInteger {

	private BigInteger Primes[];
	private int Exponents[];
	private BigInteger ValA, ValB;
	private int Ind;

	public static IAST factorize(BigInteger re, BigInteger im, IExpr num) {
		GaussianInteger g = new GaussianInteger();
		SortedMap<ComplexSym, Integer> complexMap = new TreeMap<ComplexSym, Integer>();
		g.gaussianFactorization2(re, im, complexMap);
		IASTAppendable list = F.ListAlloc(complexMap.size() + 1);
		IExpr factor = F.C1;
		IASTAppendable ast = F.TimesAlloc(complexMap.size());
		for (Map.Entry<ComplexSym, Integer> entry : complexMap.entrySet()) {
			ComplexSym key = entry.getKey();
			int i = entry.getValue();
			if (i == 1) {
				ast.append(key);
			} else {
				IInteger is = F.ZZ(i);
				ast.append(F.Power(key, is));
			}
		}
		factor = F.eval(F.Divide(num, ast));
		if (!factor.isOne()) {
			list.append(F.List(factor, F.C1));
		}
		for (Map.Entry<ComplexSym, Integer> entry : complexMap.entrySet()) {
			ComplexSym key = entry.getKey();
			IInteger is = F.ZZ(entry.getValue());
			list.append(F.List(key, is));
		}
		return list;
	}

	void gaussianFactorization2(BigInteger re, BigInteger im, SortedMap<ComplexSym, Integer> complexMap) {
		BigInteger BigInt2;
		BigInt2 = BigInteger.valueOf(2L);
		BigInteger K, Mult1, Mult2, p, q, M1, M2, Tmp;
		int index, index2;
		ValA = re;
		ValB = im;
		BigInteger norm = ValA.multiply(ValA).add(ValB.multiply(ValB));
		Ind = 0;
		if (norm.signum() == 0) {
			// Any gaussian prime divides this number
			return;
		}
		if (norm.compareTo(BigInteger.ONE) > 0) {
			SortedMap<BigInteger, Integer> bigMap = new TreeMap<BigInteger, Integer>();
			Primality.factorInteger(norm, bigMap);
			Ind = bigMap.size();
			Primes = new BigInteger[Ind];
			Exponents = new int[Ind];
			int i = 0;
			for (Map.Entry<BigInteger, Integer> entry : bigMap.entrySet()) {
				Primes[i] = entry.getKey();
				Exponents[i++] = entry.getValue();
			}
			for (index = 0; index < Ind; index++) {
				p = Primes[index];
				if (p.equals(BigInt2)) {
					for (index2 = 0; index2 < Exponents[index]; index2++) {
						DivideGaussian(BigInteger.ONE, BigInteger.ONE, complexMap); /* Divide by 1+i */
						DivideGaussian(BigInteger.ONE, BigInteger.ONE.negate(), complexMap); /* Divide by 1-i */
					}
				} else if (p.testBit(1) == false) { /* if p = 1 (mod 4) */
					q = p.subtract(BigInteger.ONE); /* q = p-1 */
					K = BigInteger.ONE;
					do { // Compute Mult1 = sqrt(-1) mod p
						K = K.add(BigInteger.ONE);
						Mult1 = K.modPow(q.shiftRight(2), p);
					} while (Mult1.equals(BigInteger.ONE) || Mult1.equals(q));
					Mult2 = BigInteger.ONE;
					while (true) {
						K = Mult1.multiply(Mult1).add(Mult2.multiply(Mult2)).divide(p);
						if (K.equals(BigInteger.ONE)) {
							break;
						}
						M1 = Mult1.mod(K);
						M2 = Mult2.mod(K);
						if (M1.compareTo(K.shiftRight(1)) > 0) {
							M1 = M1.subtract(K);
						}
						if (M2.compareTo(K.shiftRight(1)) > 0) {
							M2 = M2.subtract(K);
						}
						Tmp = Mult1.multiply(M1).add(Mult2.multiply(M2)).divide(K);
						Mult2 = Mult1.multiply(M2).subtract(Mult2.multiply(M1)).divide(K);
						Mult1 = Tmp;
					} /* end while */
					if (Mult1.abs().compareTo(Mult2.abs()) < 0) {
						Tmp = Mult1;
						Mult1 = Mult2;
						Mult2 = Tmp;
					}
					for (index2 = 0; index2 < Exponents[index]; index2++) {
						DivideGaussian(Mult1, Mult2, complexMap);
						DivideGaussian(Mult1, Mult2.negate(), complexMap);
					}
					/* end p = 1 (mod 4) */
				} else { /* if p = 3 (mod 4) */
					for (index2 = 0; index2 < Exponents[index]; index2++) {
						DivideGaussian(Primes[index], BigInteger.ZERO, complexMap);
					} /* end p = 3 (mod 4) */
				}
			}
		} 
	}

	private void DivideGaussian(BigInteger real, BigInteger imag, SortedMap<ComplexSym, Integer> complexMap) {
		real = real.abs();
		BigInteger temp;
		BigInteger norm = real.multiply(real).add(imag.multiply(imag));
		BigInteger realNum = ValA.multiply(real).add(ValB.multiply(imag));
		BigInteger imagNum = ValB.multiply(real).subtract(ValA.multiply(imag));
		if (realNum.mod(norm).signum() == 0 && imagNum.mod(norm).signum() == 0) {
			ValA = realNum.divide(norm);
			ValB = imagNum.divide(norm);
			if (real.signum() < 0) {
				real = real.negate();
				if (imag.signum() > 0) {
					temp = imag;
					imag = real;
					real = temp;
				} else {
					imag = imag.negate();
				}
			} else if (imag.signum() < 0) {
				imag = imag.negate();
				temp = imag;
				imag = real;
				real = temp;
			}
			ComplexSym c = ComplexSym.valueOf(F.ZZ(real), F.ZZ(imag));
			Integer value = complexMap.get(c);
			if (value == null) {
				complexMap.put(c, 1);
			} else {
				complexMap.put(c, value + 1);
			}
		}
	}
}
