/*
 * $Id$
  */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigInteger;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * Class to produce a system of equations defined as Cyclic.
 *
 * @author Heinz Kredel
 */
public class Cyclic {

    public final GenPolynomialRing<BigInteger> ring;
    final int N;
    final String var;
    final String order;
    /**
     * Cyclic constructor.
     *
     * @param n problem size.
     */
    public Cyclic(int n) {
        this("x", n);
    }

    /**
     * Cyclic constructor.
     *
     * @param v name of variables.
     * @param n problem size.
     */
    public Cyclic(String v, int n) {
        this(v, n, "G");
    }


    /**
     * Cyclic constructor.
     *
     * @param var   name of variables.
     * @param n     problem size.
     * @param order term order letter for output.
     */
    public Cyclic(String var, int n, String order) {
        this.var = var;
        this.N = n;
        this.order = order;
        BigInteger fac = new BigInteger();
        ring = new GenPolynomialRing<BigInteger>(fac, N); //,var);
        //System.out.println("ring = " + ring);
    }

    /**
     * main.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("usage: Cyclic N <order> <var>");
            return;
        }
        int n = Integer.parseInt(args[0]);
        Cyclic k = null;
        if (args.length == 1) {
            k = new Cyclic(n);
        }
        if (args.length == 2) {
            k = new Cyclic("x", n, args[1]);
        }
        if (args.length == 3) {
            k = new Cyclic(args[2], n, args[1]);
        }
        System.out.println("#Cyclic equations for N = " + n + ":");
        System.out.println("" + k);
    }

    /**
     * toString.
     *
     * @return Cyclic problem as string.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(ring.toString().replace("BigInteger", "Z"));
        s.append(System.getProperty("line.separator"));
        s.append(cyclicPolys(ring).toString());
        return s.toString();
    }


    /**
     * Compute list of polynomials.
     *
     * @return Cyclic problem as list of polynomials.
     */
    public String polyList() {
        return cyclicPolys(ring).toString().replace("[", "(").replace("]", ")");
    }


    /**
     * Compute list of polynomials.
     *
     * @return Cyclic problem as list of polynomials.
     */
    public List<GenPolynomial<BigInteger>> cyclicPolys() {
        return cyclicPolys(ring);
    }


    /**
     * Compute list of polynomials.
     *
     * @param ring polynomial ring.
     * @return Cyclic problem as list of polynomials.
     */
    List<GenPolynomial<BigInteger>> cyclicPolys(GenPolynomialRing<BigInteger> ring) {
        int n = ring.nvar;
        List<GenPolynomial<BigInteger>> cp = new ArrayList<GenPolynomial<BigInteger>>(n);
        for (int i = 1; i <= n; i++) {
            GenPolynomial<BigInteger> p = cyclicPoly(ring, n, i);
            cp.add(p);
            //System.out.println("p[" + i + "] = " + p);
        }
        return cp;
    }


    GenPolynomial<BigInteger> cyclicPoly(GenPolynomialRing<BigInteger> ring, int n, int i) {
        List<? extends GenPolynomial<BigInteger>> X = ring.univariateList();
        GenPolynomial<BigInteger> p = ring.getZERO();
        for (int j = 1; j <= n; j++) {
            GenPolynomial<BigInteger> pi = ring.getONE();
            for (int k = j; k < j + i; k++) {
                pi = pi.multiply(X.get(k % n));
            }
            p = p.sum(pi);
            if (i == n) {
                p = p.subtract(ring.getONE());
                break;
            }
        }
        return p;
    }

}
