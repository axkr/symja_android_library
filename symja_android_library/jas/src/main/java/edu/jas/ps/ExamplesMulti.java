/*
 * $Id$
 */

package edu.jas.ps;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * Examples for multivariate power series implementations.
 * @author Heinz Kredel
 */

public class ExamplesMulti {


    public static void main(String[] args) {
        BasicConfigurator.configure();
        if ( args.length > 0 ) {
            example1();
            example2();
            example3();
            example4();
            example5();
            example6();
            example7();
            example8();
            example9();
            example10();
        }
        example11();
    }


    public static void example1() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br, vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x^2  + x y");
        GenPolynomial<BigRational> b = pfac.parse("y^2  + x y");
        GenPolynomial<BigRational> c = pfac.parse("x^3  + x^2");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();

        MultiVarPowerSeries<BigRational> dp = red.normalform(L, cp);
        System.out.println("dp = " + dp);
    }


    public static void example2() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br, vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x - x y");
        GenPolynomial<BigRational> b = pfac.parse("y^2  + x^3");
        GenPolynomial<BigRational> c = pfac.parse("x^2  + y^2");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();

        MultiVarPowerSeries<BigRational> dp = red.normalform(L, cp);
        System.out.println("dp = " + dp);
    }


    public static void example3() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br, vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x");
        GenPolynomial<BigRational> b = pfac.parse("y");
        GenPolynomial<BigRational> c = pfac.parse("x^2 + y^2 + z^3 + x^4 + y^5");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();

        MultiVarPowerSeries<BigRational> dp = red.normalform(L, cp);
        System.out.println("dp = " + dp);
    }


    public static void example4() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br, vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x + x^3 + x^5");
        GenPolynomial<BigRational> c = pfac.parse("x + x^2");
        System.out.println("a = " + a);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        //fac.setTruncate(11);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();

        MultiVarPowerSeries<BigRational> dp = red.normalform(L, cp);
        System.out.println("dp = " + dp);
    }


    public static void example5() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br, vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> c = pfac.parse("x^2  + y^2 - 1");
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        //fac.setTruncate(19);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.getSIN(0);
        MultiVarPowerSeries<BigRational> bp = fac.getCOS(1);
        MultiVarPowerSeries<BigRational> ep = fac.getCOS(0);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("ep = " + ep);
        System.out.println("cp = " + cp);
        System.out.println("ap^2 + ep^2 = " + ap.multiply(ap).sum(ep.multiply(ep)));

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();

        MultiVarPowerSeries<BigRational> dp = red.normalform(L, cp);
        System.out.println("dp = " + dp);
    }


    public static void example6() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br, vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x^5 - x y^6 + z^7");
        GenPolynomial<BigRational> b = pfac.parse("x y + y^3 + z^3");
        GenPolynomial<BigRational> c = pfac.parse("x^2 + y^2 - z^2");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        //fac.setTruncate(11);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        L.add(cp);
        StandardBaseSeq<BigRational> tm = new StandardBaseSeq<BigRational>();

        List<MultiVarPowerSeries<BigRational>> S = tm.STD(L);
        for (MultiVarPowerSeries<BigRational> ps : S) {
            System.out.println("ps = " + ps);
        }
        System.out.println("\nS = " + S);

        boolean s = tm.isSTD(S);
        System.out.println("\nisSTD = " + s);

        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();
        s = red.contains(S, L);
        System.out.println("S contains L = " + s);
    }


    public static void example7() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br, vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x^10 + x^9 y^2");
        GenPolynomial<BigRational> b = pfac.parse("y^8 - x^2 y^7");
        System.out.println("a = " + a);
        System.out.println("b = " + b);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        fac.setTruncate(12);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);

        StandardBaseSeq<BigRational> tm = new StandardBaseSeq<BigRational>();
        List<MultiVarPowerSeries<BigRational>> S = tm.STD(L);
        for (MultiVarPowerSeries<BigRational> ps : S) {
            System.out.println("ps = " + ps);
        }
        System.out.println("\nS = " + S);

        boolean s = tm.isSTD(S);
        System.out.println("\nisSTD = " + s);

        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();
        s = red.contains(S, L);
        System.out.println("\nS contains L = " + s);
    }


    public static void example8() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br, vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> c = pfac.parse("x^2  + y^2");
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        //fac.setTruncate(19);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.getSIN(0);
        MultiVarPowerSeries<BigRational> bp = fac.getTAN(0);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        //L.add(cp);
        System.out.println("\nL = " + L);

        StandardBaseSeq<BigRational> tm = new StandardBaseSeq<BigRational>();

        List<MultiVarPowerSeries<BigRational>> S = tm.STD(L);
        for (MultiVarPowerSeries<BigRational> ps : S) {
            System.out.println("ps = " + ps);
        }
        System.out.println("\nS = " + S);

        boolean s = tm.isSTD(S);
        System.out.println("\nisSTD = " + s);

        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();
        s = red.contains(S, L);
        System.out.println("S contains L = " + s);
    }


    public static void example9() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br, vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x z - y z - y^2 z");
        GenPolynomial<BigRational> b = pfac.parse("x z - y z + y^2 z");
        GenPolynomial<BigRational> c = pfac.parse("z + y^2 z");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        fac.setTruncate(11);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        L.add(cp);
        StandardBaseSeq<BigRational> tm = new StandardBaseSeq<BigRational>();

        List<MultiVarPowerSeries<BigRational>> S = tm.STD(L);
        for (MultiVarPowerSeries<BigRational> ps : S) {
            System.out.println("ps = " + ps);
        }
        System.out.println("\nS = " + S);

        boolean s = tm.isSTD(S);
        System.out.println("\nisSTD = " + s);

        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();
        s = red.contains(S, L);
        System.out.println("S contains L = " + s);
    }


    public static void example10() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br, vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x^2 z^2 - y^6");
        GenPolynomial<BigRational> b = pfac.parse("x y z^2 + y^4 z - x^5 z - x^4 y^3");
        GenPolynomial<BigRational> c = pfac.parse("x z - y^3 + x^2 z - x y^3");
        GenPolynomial<BigRational> d = pfac.parse("y z + x y z - x^4 - x^5");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);
        System.out.println("d = " + d);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        fac.setTruncate(9);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        MultiVarPowerSeries<BigRational> dp = fac.fromPolynomial(d);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);
        System.out.println("dp = " + dp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        L.add(cp);
        L.add(dp);
        StandardBaseSeq<BigRational> tm = new StandardBaseSeq<BigRational>();

        List<MultiVarPowerSeries<BigRational>> S = tm.STD(L);
        for (MultiVarPowerSeries<BigRational> ps : S) {
            System.out.println("ps = " + ps);
        }
        System.out.println("\nS = " + S);

        boolean s = tm.isSTD(S);
        System.out.println("\nisSTD = " + s);

        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();
        s = red.contains(S, L);
        System.out.println("S contains L = " + s);

        List<MultiVarPowerSeries<BigRational>> R = red.totalNormalform(S);
        System.out.println("R = " + R);
        s = red.contains(R, L);
        System.out.println("R contains L = " + s);
        s = red.contains(R, S);
        System.out.println("R contains S = " + s);
    }


    public static void example11() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br, vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x^5 - x y^6 - z^7");
        GenPolynomial<BigRational> b = pfac.parse("x y + y^3 + z^3");
        GenPolynomial<BigRational> c = pfac.parse("x^2 + y^2 - z^2");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        //fac.setTruncate(9);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        L.add(cp);
        StandardBaseSeq<BigRational> tm = new StandardBaseSeq<BigRational>();

        List<MultiVarPowerSeries<BigRational>> S = tm.STD(L);
        for (MultiVarPowerSeries<BigRational> ps : S) {
            System.out.println("ps = " + ps);
        }
        System.out.println("\nS = " + S);

        boolean s = tm.isSTD(S);
        System.out.println("\nisSTD = " + s);

        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();
        s = red.contains(S, L);
        System.out.println("S contains L = " + s);

        List<MultiVarPowerSeries<BigRational>> R = red.totalNormalform(S);
        for (MultiVarPowerSeries<BigRational> ps : R) {
            System.out.println("ps = " + ps);
        }
        System.out.println("\nR = " + R);
        s = tm.isSTD(R);
        System.out.println("\nisSTD = " + s);

        s = red.contains(R, L);
        System.out.println("R contains L = " + s);
        s = red.contains(R, S);
        System.out.println("R contains S = " + s);
        s = red.contains(S,R);
        System.out.println("S contains R = " + s);
        s = red.contains(S,L);
        System.out.println("S contains L = " + s);

        List<MultiVarPowerSeries<BigRational>> Rs = tm.STD(R);
        for (MultiVarPowerSeries<BigRational> ps : Rs) {
            System.out.println("ps = " + ps);
        }
        System.out.println("\nRs = " + Rs);
        s = tm.isSTD(Rs);
        System.out.println("\nisSTD = " + s);

        s = red.contains(Rs, R);
        System.out.println("Rs contains R = " + s);

        s = red.contains(Rs, S);
        System.out.println("Rs contains S = " + s);
    }

}
