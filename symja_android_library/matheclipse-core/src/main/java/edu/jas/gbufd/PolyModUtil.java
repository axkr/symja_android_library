/*
 * $Id$
 */

package edu.jas.gbufd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseSeq;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;


/**
 * Package gb and gbufd utilities.
 *
 * @author Heinz Kredel
 */

public class PolyModUtil {


    private static final Logger logger = Logger.getLogger(PolyModUtil.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Least common multiple via ideal intersection.
     *
     * @param r solvable polynomial ring.
     * @param n first solvable polynomial.
     * @param d second solvable polynomial.
     * @return lcm(n, d)
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> syzLcm(GenSolvablePolynomialRing<C> r,
                                                                             GenSolvablePolynomial<C> n, GenSolvablePolynomial<C> d) {
        if (n.isZERO()) {
            return n;
        }
        if (d.isZERO()) {
            return d;
        }
        if (n.isONE()) {
            return d;
        }
        if (d.isONE()) {
            return n;
        }
        List<GenSolvablePolynomial<C>> A = new ArrayList<GenSolvablePolynomial<C>>(1);
        A.add(n);
        List<GenSolvablePolynomial<C>> B = new ArrayList<GenSolvablePolynomial<C>>(1);
        B.add(d);
        List<GenSolvablePolynomial<C>> c = PolyGBUtil.<C>intersect(r, A, B);
        //if (c.size() != 1) {
        // SolvableSyzygyAbstract<C> sz = new SolvableSyzygyAbstract<C>();
        // GenSolvablePolynomial<C>[] oc = sz.leftOreCond(n,d);
        // GenSolvablePolynomial<C> nc = oc[0].multiply(n);
        // System.out.println("nc = " + nc);
        // return nc;
        //}
        GenSolvablePolynomial<C> lcm = null;
        for (GenSolvablePolynomial<C> p : c) {
            if (p == null || p.isZERO()) {
                continue;
            }
            //System.out.println("p = " + p);
            if (lcm == null) {
                lcm = p;
                continue;
            }
            if (lcm.compareTo(p) > 0) {
                lcm = p;
            }
        }
        if (lcm == null) {
            throw new RuntimeException("this cannot happen: lcm == null: " + c);
        }
        return lcm;
    }


    /**
     * Greatest common divisor via least common multiple.
     *
     * @param r solvable polynomial ring.
     * @param n first solvable polynomial.
     * @param d second solvable polynomial.
     * @return gcd(n, d)
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> syzGcd(GenSolvablePolynomialRing<C> r,
                                                                             GenSolvablePolynomial<C> n, GenSolvablePolynomial<C> d) {
        return syzLeftGcd(r, n, d);
    }


    /**
     * Left greatest common divisor via least common multiple.
     *
     * @param r solvable polynomial ring.
     * @param n first solvable polynomial.
     * @param d second solvable polynomial.
     * @return gcd(n, d)
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> syzLeftGcd(
            GenSolvablePolynomialRing<C> r, GenSolvablePolynomial<C> n, GenSolvablePolynomial<C> d) {

        if (n.isZERO()) {
            return d;
        }
        if (d.isZERO()) {
            return n;
        }
        if (n.isConstant()) {
            return r.getONE();
        }
        if (d.isConstant()) {
            return r.getONE();
        }
        if (n.totalDegree() > 3 || d.totalDegree() > 3) { // how avoid too long running GBs ?
            //if (n.totalDegree() + d.totalDegree() > 6) { // how avoid too long running GBs ?
            // && n.length() < 10 && d.length() < 10
            logger.warn("skipping GB computation: degs = " + n.totalDegree() + ", " + d.totalDegree());
            return r.getONE();
        }
        List<GenSolvablePolynomial<C>> A = new ArrayList<GenSolvablePolynomial<C>>(2);
        A.add(n);
        A.add(d);
        SolvableGroebnerBaseAbstract<C> sbb = new SolvableGroebnerBaseSeq<C>();
        logger.warn("left syzGcd computing GB: " + A);
        List<GenSolvablePolynomial<C>> G = sbb.rightGB(A); //not: leftGB, not: sbb.twosidedGB(A);
        if (debug) {
            logger.info("G = " + G);
        }
        if (G.size() == 1) {
            return G.get(0);
        }
        logger.warn("gcd not determined, set to 1: " + G); // + ", A = " + A);
        return r.getONE();
    }


    /**
     * Right greatest common divisor via least common multiple.
     *
     * @param r solvable polynomial ring.
     * @param n first solvable polynomial.
     * @param d second solvable polynomial.
     * @return gcd(n, d)
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> syzRightGcd(
            GenSolvablePolynomialRing<C> r, GenSolvablePolynomial<C> n, GenSolvablePolynomial<C> d) {

        if (n.isZERO()) {
            return d;
        }
        if (d.isZERO()) {
            return n;
        }
        if (n.isConstant()) {
            return r.getONE();
        }
        if (d.isConstant()) {
            return r.getONE();
        }
        if (n.totalDegree() > 3 || d.totalDegree() > 3) { // how avoid too long running GBs ?
            //if (n.totalDegree() + d.totalDegree() > 6) { // how avoid too long running GBs ?
            // && n.length() < 10 && d.length() < 10
            logger.warn("skipping GB computation: degs = " + n.totalDegree() + ", " + d.totalDegree());
            return r.getONE();
        }
        List<GenSolvablePolynomial<C>> A = new ArrayList<GenSolvablePolynomial<C>>(2);
        A.add(n);
        A.add(d);
        SolvableGroebnerBaseAbstract<C> sbb = new SolvableGroebnerBaseSeq<C>();
        logger.warn("right syzGcd computing GB: " + A);
        List<GenSolvablePolynomial<C>> G = sbb.leftGB(A); // not: sbb.twosidedGB(A);
        if (debug) {
            logger.info("G = " + G);
        }
        if (G.size() == 1) {
            return G.get(0);
        }
        logger.warn("gcd not determined, set to 1: " + G); // + ", A = " + A);
        return r.getONE();
    }


    /**
     * Greatest common divisor and cofactors via least common multiple and
     * reduction.
     *
     * @param r solvable polynomial ring.
     * @param n first solvable polynomial.
     * @param d second solvable polynomial.
     * @return [ g=gcd(n,d), n/g, d/g ]
     */
    @SuppressWarnings({"unchecked", "cast"})
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C>[] syzGcdCofactors(
            GenSolvablePolynomialRing<C> r, GenSolvablePolynomial<C> n, GenSolvablePolynomial<C> d) {
        GenSolvablePolynomial<C>[] res = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[3];
        res[0] = PolyModUtil.<C>syzGcd(r, n, d);
        res[1] = n;
        res[2] = d;
        if (res[0].isONE()) {
            return res;
        }
        GenSolvablePolynomial<C>[] nqr = PolyGBUtil.<C>quotientRemainder(n, res[0]);
        if (!nqr[1].isZERO()) {
            res[0] = r.getONE();
            return res;
        }
        GenSolvablePolynomial<C>[] dqr = PolyGBUtil.<C>quotientRemainder(d, res[0]);
        if (!dqr[1].isZERO()) {
            res[0] = r.getONE();
            return res;
        }
        res[1] = nqr[0];
        res[2] = dqr[0];
        return res;
    }


    /**
     * Least common multiple. Just for fun, is not efficient.
     *
     * @param r polynomial ring.
     * @param n first polynomial.
     * @param d second polynomial.
     * @return lcm(n, d)
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> syzLcm(GenPolynomialRing<C> r,
                                                                     GenPolynomial<C> n, GenPolynomial<C> d) {
        List<GenPolynomial<C>> A = new ArrayList<GenPolynomial<C>>(1);
        A.add(n);
        List<GenPolynomial<C>> B = new ArrayList<GenPolynomial<C>>(1);
        B.add(d);
        List<GenPolynomial<C>> c = PolyGBUtil.<C>intersect(r, A, B);
        if (c.size() != 1) {
            logger.warn("lcm not uniqe: " + c);
            //throw new RuntimeException("lcm not uniqe: " + c);
        }
        GenPolynomial<C> lcm = c.get(0);
        return lcm;
    }


    /**
     * Greatest common divisor. Just for fun, is not efficient.
     *
     * @param r polynomial ring.
     * @param n first polynomial.
     * @param d second polynomial.
     * @return gcd(n, d)
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> syzGcd(GenPolynomialRing<C> r,
                                                                     GenPolynomial<C> n, GenPolynomial<C> d) {
        if (n.isZERO()) {
            return d;
        }
        if (d.isZERO()) {
            return n;
        }
        if (n.isONE()) {
            return n;
        }
        if (d.isONE()) {
            return d;
        }
        GenPolynomial<C> p = n.multiply(d);
        GenPolynomial<C> lcm = syzLcm(r, n, d);
        GenPolynomial<C> gcd = PolyUtil.<C>basePseudoDivide(p, lcm);
        return gcd;
    }


}
