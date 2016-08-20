/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.gb.GBOptimized;
import edu.jas.gb.GBProxy;
import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseParallel;
import edu.jas.gbufd.GBFactory;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;


/**
 * ExamplesGeoTheorems for Groebner base usage.
 * @author GeoGebra developers.
 * @author Kovács Zoltán
 * @author Heinz Kredel.
 */
public class ExamplesGeoTheorems {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        example10();
        example11();
        example12();
        example13();
        example14();
        example15();
        example16();
        example17();
        ComputerThreads.terminate();
    }


    /**
     * get Pappus Example.
     */
    public static List<GenPolynomial<BigRational>> getExample() {
        String[] vars = { "a1", "a2", "b1", "b2", "c1", "c2", "d1", "d2", "e1", "e2", "f1", "f2", "g1", "g2",
                "h1", "h2", "i1", "i2", "j1", "j2", "z1", "z2", "z3" };

        BigRational br = new BigRational();
        GenPolynomialRing<BigRational> pring = new GenPolynomialRing<BigRational>(br, vars);

        GenPolynomial<BigRational> e1 = pring.parse("(a1*(b2 - c2) + a2*( - b1 + c1) + b1*c2 - b2*c1)");
        GenPolynomial<BigRational> e2 = pring.parse("(d1*(e2 - f2) + d2*( - e1 + f1) + e1*f2 - e2*f1)");
        GenPolynomial<BigRational> e3 = pring.parse("(a1*( - e2 + h2) + a2*(e1 - h1) - e1*h2 + e2*h1)");
        GenPolynomial<BigRational> e4 = pring.parse("(b1*(d2 - h2) + b2*( - d1 + h1) + d1*h2 - d2*h1)");
        GenPolynomial<BigRational> e5 = pring.parse("(c1*(d2 - i2) + c2*( - d1 + i1) + d1*i2 - d2*i1)");
        GenPolynomial<BigRational> e6 = pring.parse("(a1*( - f2 + i2) + a2*(f1 - i1) - f1*i2 + f2*i1)");
        GenPolynomial<BigRational> e7 = pring.parse("(c1*(e2 - j2) + c2*( - e1 + j1) + e1*j2 - e2*j1)");
        GenPolynomial<BigRational> e8 = pring.parse("(b1*( - f2 + j2) + b2*(f1 - j1) - f1*j2 + f2*j1)");
        GenPolynomial<BigRational> e9 = pring
                        .parse("(a1*(b2*z2 - d2*z2) + a2*( - b1*z2 + d1*z2) + b1*d2*z2 - b2*d1*z2 - 1)");
        GenPolynomial<BigRational> e10 = pring
                        .parse("(a1*(b2*z3 - e2*z3) + a2*( - b1*z3 + e1*z3) + b1*e2*z3 - b2*e1*z3 - 1)");
        GenPolynomial<BigRational> e11 = pring
                        .parse("(h1*(i2*z1 - j2*z1) + h2*( - i1*z1 + j1*z1) + i1*j2*z1 - i2*j1*z1 - 1)");

        List<GenPolynomial<BigRational>> cp = new ArrayList<GenPolynomial<BigRational>>(11);
        cp.add(e1);
        cp.add(e2);
        cp.add(e3);
        cp.add(e4);
        cp.add(e5);
        cp.add(e6);
        cp.add(e7);
        cp.add(e8);
        cp.add(e9);
        cp.add(e10);
        cp.add(e11);
        return cp;
    }


    /**
     * Example Pappus, sequential.
     */
    public static void example10() {
        List<GenPolynomial<BigRational>> cp = getExample();
        BigRational br = new BigRational();
        GenPolynomialRing<BigRational> pring = cp.get(0).ring;

        GroebnerBase<BigRational> sgb = GBFactory.getImplementation(br);
        List<GenPolynomial<BigRational>> gb;
        long t;
        t = System.currentTimeMillis();
        gb = sgb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(gb) = " + t);
        t = System.currentTimeMillis();
        gb = sgb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(gb) = " + t);

        PolynomialList<BigRational> pl = new PolynomialList<BigRational>(pring, gb);
        Ideal<BigRational> id = new Ideal<BigRational>(pl, true);
        System.out.println("cp = " + cp);
        System.out.println("id = " + id);

        Dimension dim = id.dimension();
        System.out.println("dim = " + dim);
    }


    /**
     * Example Pappus, parallel proxy.
     */
    public static void example11() {
        List<GenPolynomial<BigRational>> cp = getExample();
        BigRational br = new BigRational();
        GenPolynomialRing<BigRational> pring = cp.get(0).ring;

        GroebnerBaseAbstract<BigRational> sgb = GBFactory.getProxy(br);
        List<GenPolynomial<BigRational>> gb;
        long t;
        t = System.currentTimeMillis();
        gb = sgb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(proxy-gb) = " + t);
        t = System.currentTimeMillis();
        gb = sgb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(proxy-gb) = " + t);

        PolynomialList<BigRational> pl = new PolynomialList<BigRational>(pring, gb);
        Ideal<BigRational> id = new Ideal<BigRational>(pl, true);
        System.out.println("cp = " + cp);
        System.out.println("id = " + id);

        Dimension dim = id.dimension();
        System.out.println("dim = " + dim);
        sgb.terminate();
    }


    /**
     * Example Pappus, optimized term order.
     */
    public static void example12() {
        List<GenPolynomial<BigRational>> cp = getExample();
        BigRational br = new BigRational();
        GenPolynomialRing<BigRational> pring = cp.get(0).ring;

        GroebnerBaseAbstract<BigRational> sgb = GBFactory.getImplementation(br);
        GroebnerBaseAbstract<BigRational> ogb = new GBOptimized<BigRational>(sgb, true); // false no change for GB == 1
        List<GenPolynomial<BigRational>> gb;
        long t;
        t = System.currentTimeMillis();
        gb = ogb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(optimized-gb) = " + t);
        t = System.currentTimeMillis();
        gb = ogb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(optimized-gb) = " + t);

        PolynomialList<BigRational> pl = new PolynomialList<BigRational>(pring, gb);
        Ideal<BigRational> id = new Ideal<BigRational>(pl, true);
        System.out.println("cp = " + cp);
        System.out.println("id = " + id);

        Dimension dim = id.dimension();
        System.out.println("dim = " + dim);
        ogb.terminate();
    }


    /**
     * Example Pappus, optimized term order and parallel proxy.
     */
    public static void example13() {
        List<GenPolynomial<BigRational>> cp = getExample();
        BigRational br = new BigRational();
        GenPolynomialRing<BigRational> pring = cp.get(0).ring;

        GroebnerBaseAbstract<BigRational> sgb = GBFactory.getProxy(br);
        GroebnerBaseAbstract<BigRational> ogb = new GBOptimized<BigRational>(sgb, true); // false no change for GB == 1
        List<GenPolynomial<BigRational>> gb;
        long t;
        t = System.currentTimeMillis();
        gb = ogb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(optimized-proxy-gb) = " + t);
        t = System.currentTimeMillis();
        gb = ogb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(optimized-proxy-gb) = " + t);

        PolynomialList<BigRational> pl = new PolynomialList<BigRational>(pring, gb);
        Ideal<BigRational> id = new Ideal<BigRational>(pl, true);
        System.out.println("cp = " + cp);
        System.out.println("id = " + id);

        Dimension dim = id.dimension();
        System.out.println("dim = " + dim);
        ogb.terminate();
    }


    /**
     * Example Pappus, fraction free.
     */
    public static void example14() {
        List<GenPolynomial<BigRational>> cp = getExample();
        BigRational br = new BigRational();
        GenPolynomialRing<BigRational> pring = cp.get(0).ring;

        GroebnerBaseAbstract<BigRational> sgb = GBFactory.getImplementation(br, GBFactory.Algo.ffgb);
        List<GenPolynomial<BigRational>> gb;
        long t;
        t = System.currentTimeMillis();
        gb = sgb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(fraction-free-gb) = " + t);
        t = System.currentTimeMillis();
        gb = sgb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(fraction-free-gb) = " + t);

        PolynomialList<BigRational> pl = new PolynomialList<BigRational>(pring, gb);
        Ideal<BigRational> id = new Ideal<BigRational>(pl, true);
        System.out.println("cp = " + cp);
        System.out.println("id = " + id);

        Dimension dim = id.dimension();
        System.out.println("dim = " + dim);
        sgb.terminate();
    }


    /**
     * Example Pappus, optimized and fraction free.
     */
    public static void example15() {
        List<GenPolynomial<BigRational>> cp = getExample();
        BigRational br = new BigRational();
        GenPolynomialRing<BigRational> pring = cp.get(0).ring;

        GroebnerBaseAbstract<BigRational> sgb = GBFactory.getImplementation(br, GBFactory.Algo.ffgb);
        GroebnerBaseAbstract<BigRational> ogb = new GBOptimized<BigRational>(sgb, true); // false no change for GB == 1
        List<GenPolynomial<BigRational>> gb;
        long t;
        t = System.currentTimeMillis();
        gb = ogb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(optimized-fraction-free-gb) = " + t);
        t = System.currentTimeMillis();
        gb = ogb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(optimized-fraction-free-gb) = " + t);

        PolynomialList<BigRational> pl = new PolynomialList<BigRational>(pring, gb);
        Ideal<BigRational> id = new Ideal<BigRational>(pl, true);
        System.out.println("cp = " + cp);
        System.out.println("id = " + id);

        Dimension dim = id.dimension();
        System.out.println("dim = " + dim);
        ogb.terminate();
    }


    /**
     * Example Pappus, proxy, optimized and fraction free.
     */
    public static void example16() {
        List<GenPolynomial<BigRational>> cp = getExample();
        BigRational br = new BigRational();
        GenPolynomialRing<BigRational> pring = cp.get(0).ring;

        GroebnerBaseAbstract<BigRational> sgb = GBFactory.getImplementation(br, GBFactory.Algo.ffgb);
        GroebnerBaseAbstract<BigRational> ogb = new GBOptimized<BigRational>(sgb, true); // false no change for GB == 1
        GroebnerBaseAbstract<BigRational> pgb = new GroebnerBaseParallel<BigRational>();
        GroebnerBaseAbstract<BigRational> opgb = new GBOptimized<BigRational>(pgb, true); // false no change for GB == 1
        GroebnerBaseAbstract<BigRational> popgb = new GBProxy<BigRational>(ogb, opgb);
        List<GenPolynomial<BigRational>> gb;
        long t;
        t = System.currentTimeMillis();
        gb = popgb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(proxy-optimized-fraction-free-gb) = " + t);
        t = System.currentTimeMillis();
        gb = popgb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(proxy-optimized-fraction-free-gb) = " + t);

        PolynomialList<BigRational> pl = new PolynomialList<BigRational>(pring, gb);
        Ideal<BigRational> id = new Ideal<BigRational>(pl, true);
        System.out.println("cp = " + cp);
        System.out.println("id = " + id);

        Dimension dim = id.dimension();
        System.out.println("dim = " + dim);
        popgb.terminate();
    }


    /**
     * Example Pappus, optimized and parallel and fraction free.
     */
    public static void example17() {
        List<GenPolynomial<BigRational>> cp = getExample();
        BigRational br = new BigRational();
        GenPolynomialRing<BigRational> pring = cp.get(0).ring;

        GroebnerBaseAbstract<BigRational> sgb = GBFactory.getImplementation(br, GBFactory.Algo.ffgb);
        GroebnerBaseAbstract<BigRational> pgb = new GroebnerBaseParallel<BigRational>();
        GroebnerBaseAbstract<BigRational> ppgb = new GBProxy<BigRational>(sgb, pgb);
        GroebnerBaseAbstract<BigRational> ogb = new GBOptimized<BigRational>(ppgb, true); // false no change for GB == 1
        List<GenPolynomial<BigRational>> gb;
        long t;
        t = System.currentTimeMillis();
        gb = ogb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(optimized-proxy-fraction-free-gb) = " + t);
        t = System.currentTimeMillis();
        gb = ogb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("gb = " + gb);
        System.out.println("time(optimized-proxy-fraction-free-gb) = " + t);

        PolynomialList<BigRational> pl = new PolynomialList<BigRational>(pring, gb);
        Ideal<BigRational> id = new Ideal<BigRational>(pl, true);
        System.out.println("cp = " + cp);
        System.out.println("id = " + id);

        Dimension dim = id.dimension();
        System.out.println("dim = " + dim);
        ogb.terminate();
    }

}
