/*
 * $Id$
 */

package edu.jas.root;


import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.arith.Rational;
import edu.jas.poly.Complex;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Real root utilities. For example real root count.
 * @author Heinz Kredel
 */
public class RootUtil {


    private static final Logger logger = LogManager.getLogger(RootUtil.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Count changes in sign.
     * @param <C> coefficient type.
     * @param L list of coefficients.
     * @return number of sign changes in L.
     */
    public static <C extends RingElem<C>> long signVar(List<C> L) {
        long v = 0;
        if (L == null || L.isEmpty()) {
            return v;
        }
        C A = L.get(0);
        for (int i = 1; i < L.size(); i++) {
            C B = L.get(i);
            while (B == null || B.signum() == 0) {
                i++;
                if (i >= L.size()) {
                    return v;
                }
                B = L.get(i);
            }
            if (A.signum() * B.signum() < 0) {
                v++;
            }
            A = B;
        }
        return v;
    }


    /**
     * Parse interval for a real root from String.
     * @param s String, syntax: [left, right] or [mid].
     * @return Interval from s.
     */
    public static <C extends RingElem<C> & Rational> Interval<C> parseInterval(RingFactory<C> fac, String s) {
        int r = s.length();
        int el = s.indexOf("[");
        if (el >= 0) {
            int ri = s.indexOf("]");
            if (ri > 0) {
                r = ri;
            }
        } else {
            el = -1;
        }
        //System.out.println("s  = " + s);
        String iv = s.substring(el + 1, r).trim();
        //System.out.println("iv = " + iv);
        int k = iv.indexOf(",");
        if (k < 0) {
            k = s.indexOf(" ");
        }
        if (k < 0) {
            C mid = fac.parse(iv);
            return new Interval<C>(mid);
        }
        //System.out.println("k  = " + k + ", len = " + iv.length());
        String ls = iv.substring(0, k).trim();
        String rs = iv.substring(k + 1, iv.length()).trim();
        //System.out.println("ls = " + ls + ", rs = " + rs);
        C left = fac.parse(ls);
        C right = fac.parse(rs);
        if (debug) {
            logger.debug("Interval: left = " + left + ", right = " + right);
        }
        return new Interval<C>(left, right);
    }


    /**
     * Parse rectangle for a complex root from String.
     * @param s String, syntax: [south-west, north-east] or [mid].
     * @return Interval from s.
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C> & Rational> Rectangle<C> parseRectangle(RingFactory<Complex<C>> fac,
                    String s) {
        int r = s.length();
        int el = s.indexOf("[");
        if (el >= 0) {
            int ri = s.indexOf("]");
            if (ri > 0) {
                r = ri;
            }
        } else {
            el = -1;
        }
        //System.out.println("s  = " + s);
        String iv = s.substring(el + 1, r).trim();
        //System.out.println("iv = " + iv);
        int k = iv.indexOf(",");
        if (k < 0) {
            k = s.indexOf(" ");
        }
        if (k < 0) {
            Complex<C> mid = fac.parse(iv);
            return new Rectangle<C>(mid);
        }
        //System.out.println("k  = " + k + ", len = " + iv.length());
        String ls = iv.substring(0, k).trim();
        String rs = iv.substring(k + 1, iv.length()).trim();
        //System.out.println("ls = " + ls + ", rs = " + rs);
        Object osw = fac.parse(ls);
        Object one = fac.parse(rs);
        //System.out.println("osw = " + osw + ", one = " + one);
        Complex<C> sw;
        Complex<C> ne;
        if (osw instanceof Complex) {
            sw = (Complex<C>)osw;
            ne = (Complex<C>)one;
        } else if (osw instanceof ComplexAlgebraicNumber) {
            ComplexAlgebraicNumber csw = (ComplexAlgebraicNumber) osw;
            ComplexAlgebraicNumber cne = (ComplexAlgebraicNumber) one;
            //System.out.println("csw::ring = " + csw.ring.algebraic.toScript());
            sw = (Complex<C>) csw.magnitude();
            ne = (Complex<C>) cne.magnitude();
        } else {
            sw = fac.getONE().negate();
            ne = fac.getONE();
        }
        //System.out.println("sw = " + sw + ", ne = " + ne);
        if (debug) {
            logger.debug("Rectangle: sw = " + sw + ", ne = " + ne);
        }
        return new Rectangle<C>(sw, ne);
    }

}
