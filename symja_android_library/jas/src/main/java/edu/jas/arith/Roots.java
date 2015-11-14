/*
 * $Id$
 */

package edu.jas.arith;


// import java.util.Random;
import java.math.MathContext;

import edu.jas.structure.Power;


/**
 * Root computation algorithms. Roots for BigInteger and BigDecimals.
 * @author Heinz Kredel
 */
public class Roots {


    /**
     * Integer n-th root. Uses BigDecimal and newton iteration. R is the n-th
     * root of A.
     * @param A big integer.
     * @param n long.
     * @return the n-th root of A.
     */
    public static BigInteger root(BigInteger A, int n) {
        if (n == 1) {
            return A;
        }
        if (n == 2) {
            return sqrt(A);
        }
        if (n < 1) {
            throw new IllegalArgumentException("negative root not defined");
        }
        if (A == null || A.isZERO() || A.isONE()) {
            return A;
        }
        // ensure enough precision
        int s = A.val.bitLength() + 2;
        MathContext mc = new MathContext(s);
        //System.out.println("mc = " + mc);
        BigDecimal Ap = new BigDecimal(A.val, mc);
        //System.out.println("Ap = " + Ap);
        BigDecimal Ar = root(Ap, n);
        //System.out.println("Ar = " + Ar);
        java.math.BigInteger RP = Ar.val.toBigInteger();
        BigInteger R = new BigInteger(RP);
        while (true) {
            BigInteger P = Power.positivePower(R, n);
            //System.out.println("P = " + P);
            if (A.compareTo(P) >= 0) {
                break;
            }
            R = R.subtract(BigInteger.ONE);
        }
        return R;
    }


    /**
     * Integer square root. Uses BigDecimal and newton iteration. R is the
     * square root of A.
     * @param A big integer.
     * @return the square root of A.
     */
    public static BigInteger sqrt(BigInteger A) {
        if (A == null || A.isZERO() || A.isONE()) {
            return A;
        }
        // ensure enough precision
        int s = A.val.bitLength() + 2;
        MathContext mc = new MathContext(s);
        //System.out.println("mc = " + mc);
        // newton iteration
        BigDecimal Ap = new BigDecimal(A.val, mc);
        //System.out.println("Ap = " + Ap);
        BigDecimal Ar = sqrt(Ap);
        //System.out.println("Ar = " + Ar);
        java.math.BigInteger RP = Ar.val.toBigInteger();
        BigInteger R = new BigInteger(RP);
        while (true) {
            BigInteger P = R.multiply(R);
            //System.out.println("P = " + P);
            if (A.compareTo(P) >= 0) {
                break;
            }
            R = R.subtract(BigInteger.ONE);
        }
        return R;
    }


    /**
     * Integer square root. Uses BigInteger only. R is the square root of A.
     * @param A big integer.
     * @return the square root of A.
     */
    public static BigInteger sqrtInt(BigInteger A) {
        if (A == null || A.isZERO() || A.isONE()) {
            return A;
        }
        int s = A.signum();
        if (s < 0) {
            throw new ArithmeticException("root of negative not defined");
        }
        if (s == 0) {
            return A;
        }
        BigInteger R, R1, d;
        int log2 = A.val.bitLength();
        //System.out.println("A = " + A + ", log2 = " + log2);
        int rootlog2 = log2 - log2 / 2;
        R = new BigInteger(A.val.shiftRight(rootlog2));
        //System.out.println("R = " + R + ", rootlog2 = " + rootlog2);
        d = R;
        while (!d.isZERO()) {
            d = new BigInteger(d.val.shiftRight(1)); // div 2
            R1 = R.sum(d);
            s = A.compareTo(R1.multiply(R1));
            if (s == 0) {
                return R1;
            }
            if (s > 0) {
                R = R1;
            }
            //System.out.println("R1 = " + R1);
            //System.out.println("d  = " + d);
        }
        while (true) {
            R1 = R.sum(BigInteger.ONE);
            //System.out.println("R1 = " + R1);
            s = A.compareTo(R1.multiply(R1));
            if (s == 0) {
                return R1;
            }
            if (s > 0) {
                R = R1;
            }
            if (s < 0) {
                return R;
            }
        }
        //return R;
    }


    /**
     * Square root. R is the square root of A.
     * @param A big decimal.
     * @return the square root of A.
     */
    public static BigDecimal sqrt(BigDecimal A) {
        if (A == null || A.isZERO() || A.isONE()) {
            return A;
        }
        if ( A.signum() < 0 ) {
            throw new ArithmeticException("root of negative not defined");
        }
        // for small A use root of inverse
        if (A.abs().val.compareTo(BigDecimal.ONE.val) < 0) {
            BigDecimal Ap = A.inverse();
            Ap = sqrt(Ap);
            Ap = Ap.inverse();
            //System.out.println("sqrt(A).inverse() = " + Ap);
            return Ap;
        }
        // ensure enough precision
        MathContext mc = A.context;
        BigDecimal eps = new BigDecimal("0.1"); //e-13"); // TODO
        int p = Math.max(mc.getPrecision(),java.math.MathContext.DECIMAL64.getPrecision());
        //java.math.MathContext.UNLIMITED.getPrecision() == 0
        eps = Power.positivePower(eps,p/2);
        // newton iteration
        BigDecimal Ap = new BigDecimal(A.val, mc);
        BigDecimal ninv = new BigDecimal(0.5, mc);
        BigDecimal R1, R = Ap.multiply(ninv); // initial guess
        BigDecimal d;
        int i = 0;
        while (true) {
            R1 = R.sum(Ap.divide(R));
            R1 = R1.multiply(ninv); // div n
            d = R.subtract(R1).abs();
            R = R1;
            if (d.val.compareTo(eps.val) <= 0) {
                //System.out.println("d  = " + d + ", R = " + R);
                break;
            }
            if (i++ % 11 == 0) {
                eps = eps.sum(eps);
            }
            //System.out.println("eps  = " + eps + ", d = " + d);
        }
        return R;
    }


    /**
     * N-th root. R is the n-th root of A.
     * @param A big decimal.
     * @param n long.
     * @return the n-th root of A.
     */
    public static BigDecimal root(BigDecimal A, int n) {
        if (n == 1) {
            return A;
        }
        if (n == 2) {
            return sqrt(A);
        }
        if (n < 1) {
            throw new IllegalArgumentException("negative root not defined");
        }
        if (A == null || A.isZERO() || A.isONE()) {
            return A;
        }
        if ( A.signum() < 0 ) {
            throw new ArithmeticException("root of negative not defined");
        }
        // for small A use root of inverse
        if (A.abs().val.compareTo(BigDecimal.ONE.val) < 0) {
            BigDecimal Ap = A.inverse();
            //System.out.println("A.inverse() = " + Ap);
            Ap = root(Ap, n);
            return Ap.inverse();
        }
        // ensure enough precision
        MathContext mc = A.context;
        BigDecimal eps = new BigDecimal("0.1"); //e-10"); // TODO
        int p = Math.max(mc.getPrecision(),java.math.MathContext.DECIMAL64.getPrecision());
        //java.math.MathContext.UNLIMITED.getPrecision() == 0
        eps = Power.positivePower(eps,(p*2)/3);
        // newton iteration
        BigDecimal Ap = A;
        BigDecimal N = new BigDecimal(n, mc);
        BigDecimal ninv = new BigDecimal(1.0 / n, mc);
        BigDecimal nsub = new BigDecimal(1.0, mc); // because of precision
        nsub = nsub.subtract(ninv);
        BigDecimal P, R1, R = Ap.multiply(ninv); // initial guess
        BigDecimal d;
        int i = 0;
        while (true) {
            P = Power.positivePower(R, n - 1);
            R1 = Ap.divide(P.multiply(N));
            R1 = R.multiply(nsub).sum(R1);
            d = R.subtract(R1).abs();
            R = R1;
            if (d.val.compareTo(eps.val) <= 0) {
                //System.out.println("d.val  = " + d.val);
                break;
            }
            if (i++ % 11 == 0) {
                eps = eps.sum(eps);
            }
        }
        // System.out.println("eps  = " + eps + ", d = " + d);
        return R;
    }


    /**
     * Complex decimal number square root.
     * @param a big decimal complex.
     * @return sqrt(a).
     */
    public static BigDecimalComplex sqrt(BigDecimalComplex a) {
        if (a.isZERO() || a.isONE()) {
            return a;
        }
        BigDecimal r = a.re.abs().sum(a.abs().re);
        BigDecimal t = new BigDecimal(2);
        BigDecimal ti = new BigDecimal("0.5");
        //BigDecimal u = r.divide(t);
        BigDecimal u = r.multiply(ti);
        BigDecimal v = Roots.sqrt(u);
        //System.out.println("r = " + r + ", a = " + a);
        //System.out.println("v = " + v + ", u = " + u);
        if (a.re.signum() >= 0) {
            return new BigDecimalComplex(v, a.im.divide(v.multiply(t)));
        }
        u = v;
        if (a.im.signum() < 0) {
            u = u.negate();
        }
        return new BigDecimalComplex(a.im.abs().divide(v.multiply(t)), u);
    }

}
