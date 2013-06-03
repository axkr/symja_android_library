/*
 * $Id$
 */

package edu.jas.integrate;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * Container for a rational function integral, quotient version .
 * integral(num/den) = pol + sum_rat( rat_i/rat_{i+1} ) + sum_log( a_i log ( d_i
 * ) )
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class QuotIntegral<C extends GcdRingElem<C>> implements Serializable {


    /**
     * Original rational function with coefficients from C.
     */
    public final Quotient<C> quot;


    /**
     * Integral of the polynomial and rational part.
     */
    public final List<Quotient<C>> rational;


    /**
     * Integral of the logarithmic part.
     */
    public final List<LogIntegral<C>> logarithm;


    /**
     * Constructor.
     * @param ri integral.
     */
    public QuotIntegral(Integral<C> ri) {
        this(new QuotientRing<C>(ri.den.ring), ri);
    }


    /**
     * Constructor.
     * @param r rational function QuotientRing over C.
     * @param ri integral.
     */
    public QuotIntegral(QuotientRing<C> r, Integral<C> ri) {
        this(new Quotient<C>(r, ri.num, ri.den), ri.pol, ri.rational, ri.logarithm);
    }


    /**
     * Constructor.
     * @param r rational function Quotient over C.
     * @param p integral of polynomial part.
     * @param rat list of rational integrals.
     */
    public QuotIntegral(Quotient<C> r, GenPolynomial<C> p, List<GenPolynomial<C>> rat) {
        this(r, p, rat, new ArrayList<LogIntegral<C>>());
    }


    /**
     * Constructor.
     * @param r rational function Quotient over C.
     * @param p integral of polynomial part.
     * @param rat list of rational integrals.
     * @param log list of logarithmic part.
     */
    public QuotIntegral(Quotient<C> r, GenPolynomial<C> p, List<GenPolynomial<C>> rat,
            List<LogIntegral<C>> log) {
        quot = r;
        QuotientRing<C> qr = r.ring;
        rational = new ArrayList<Quotient<C>>();
        if (!p.isZERO()) {
            rational.add(new Quotient<C>(qr, p));
        }
        for (int i = 0; i < rat.size(); i++) {
            GenPolynomial<C> rn = rat.get(i++);
            GenPolynomial<C> rd = rat.get(i);
            rational.add(new Quotient<C>(qr, rn, rd));
        }
        logarithm = log;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("integral( " + quot.toString() + " )");
        sb.append(" =\n");
        boolean first = true;
        if (rational.size() != 0) {
            for (int i = 0; i < rational.size(); i++) {
                if (first) {
                    first = false;
                } else {
                    sb.append(" + ");
                }
                sb.append("(" + rational.get(i) + ")");
            }
        }
        if (logarithm.size() != 0) {
            if (rational.size() != 0) {
                sb.append(" + ");
            }
            first = true;
            for (LogIntegral<C> pf : logarithm) {
                if (first) {
                    first = false;
                } else {
                    sb.append(" + ");
                }
                sb.append(pf);
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    /**
     * Hash code for QuotIntegral.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = quot.hashCode();
        h = h * 37 + rational.hashCode();
        h = h * 37 + logarithm.hashCode();
        return h;
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        QuotIntegral<C> b = null;
        try {
            b = (QuotIntegral<C>) B;
        } catch (ClassCastException ignored) {
        }
        if (b == null) {
            return false;
        }
        return quot.equals(b.quot) && 
               rational.equals(b.rational) && 
               logarithm.equals(b.logarithm);
    }

}
