/*
 * $Id$
 */

package edu.jas.vector;


import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
// import edu.jas.arith.ModInteger;


/**
 * Examples for basic linear algebra.
 * @author Heinz Kredel
 */

public class Examples {


    /**
     * main.
     */
    public static void main(String[] args) {
        example1();
        example2();
        example3();
        // ComputerThreads.terminate();
    }


    /**
     * example1.
     */
    public static void example1() {
        System.out.println("\n\n example 1");

        BigInteger cfac;
        GenMatrixRing<BigInteger> mfac;

        cfac = new BigInteger();
        System.out.println("cfac = " + cfac);

        mfac = new GenMatrixRing<BigInteger>(cfac, 5, 5);
        System.out.println("mfac = " + mfac);

        GenMatrix<BigInteger> m;
        m = mfac.random(3, 0.4f);
        System.out.println("\nm = " + m);

        m = m.multiply(m);
        System.out.println("\nm = " + m);
    }


    /**
     * example2.
     */
    public static void example2() {
        System.out.println("\n\n example 2");

        BigRational cfac;
        GenMatrixRing<BigRational> mfac;

        cfac = new BigRational();
        System.out.println("cfac = " + cfac);

        mfac = new GenMatrixRing<BigRational>(cfac, 5, 5);
        System.out.println("mfac = " + mfac);

        GenMatrix<BigRational> m;
        m = mfac.random(3, 0.4f);
        System.out.println("\nm = " + m);

        m = m.multiply(m);
        System.out.println("\nm = " + m);
    }


    public static void example3() {
        System.out.println("\n\n example 3");
        BigRational r1, r2, r3, r4, r5, r6, fac;
        r1 = new BigRational(1, 10);
        r2 = new BigRational(6, 5);
        r3 = new BigRational(1, 9);
        r4 = new BigRational(1, 1);
        r5 = r2.sum(r3);
        r6 = r1.multiply(r4);

        fac = new BigRational();

        BigRational[][] aa = new BigRational[][] { { r1, r2, r3 }, { r4, r5, r6 }, { r2, r1, r3 } };
        GenMatrixRing<BigRational> mfac = new GenMatrixRing<BigRational>(fac, aa.length, aa[0].length);
        GenMatrix<BigRational> a = new GenMatrix<BigRational>(mfac, aa);
        System.out.println("system = " + a);

        BigRational[] ba = new BigRational[] { r1, r2, r3 };
        GenVectorModul<BigRational> vfac = new GenVectorModul<BigRational>(fac, ba.length);
        GenVector<BigRational> b = new GenVector<BigRational>(vfac, ba);
        System.out.println("right hand side = " + b);

        LinAlg<BigRational> lu = new LinAlg<BigRational>();
        GenVector<BigRational> x = lu.solve(a, b);
        System.out.println("solution = " + x);
    }

}
