/*
 * $Id$
 */

package edu.jas.ufd;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;


/**
 * Container for the partial fraction decomposition of a squarefree denominator.
 * num/den = sum( a_i / d_i )
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class PartialFraction<C extends GcdRingElem<C>> implements Serializable {


    private static final Logger logger = LogManager.getLogger(PartialFraction.class);


    /**
     * Original numerator polynomial coefficients from C and deg(num) &lt;
     * deg(den).
     */
    public final GenPolynomial<C> num;


    /**
     * Original (irreducible) denominator polynomial coefficients from C.
     */
    public final GenPolynomial<C> den;


    /**
     * List of numbers from C.
     */
    public final List<C> cfactors;


    /**
     * List of linear factors of the denominator with coefficients from C.
     */
    public final List<GenPolynomial<C>> cdenom;


    /**
     * List of algebraic numbers of an algebraic field extension over C.
     */
    public final List<AlgebraicNumber<C>> afactors;


    /**
     * List of factors of the denominator with coefficients from an
     * AlgebraicNumberRing&lt;C&gt;.
     */
    public final List<GenPolynomial<AlgebraicNumber<C>>> adenom;


    /**
     * Constructor.
     * @param n numerator GenPolynomial over C.
     * @param d irreducible denominator GenPolynomial over C.
     * @param cf list of elements a_i.
     * @param cd list of linear factors d_i of d.
     * @param af list of algebraic elements a_i.
     * @param ad list of linear (irreducible) factors d_i of d with algebraic
     *            coefficients. n/d = sum( a_i / d_i )
     */
    public PartialFraction(GenPolynomial<C> n, GenPolynomial<C> d, List<C> cf, List<GenPolynomial<C>> cd,
                    List<AlgebraicNumber<C>> af, List<GenPolynomial<AlgebraicNumber<C>>> ad) {
        num = n;
        den = d;
        cfactors = cf;
        cdenom = cd;
        afactors = af;
        adenom = ad;
        for (GenPolynomial<C> p : cdenom) {
            if (p.degree(0) > 1) {
                throw new IllegalArgumentException("polynomial not linear, p = " + p);
            }
        }
        for (GenPolynomial<AlgebraicNumber<C>> a : adenom) {
            if (a.degree(0) > 1) {
                throw new IllegalArgumentException("polynomial not linear, a = " + a);
            }
        }
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(" + num.toString() + ")");
        sb.append(" / ");
        sb.append("(" + den.toString() + ")");
        sb.append(" =\n");
        boolean first = true;
        for (int i = 0; i < cfactors.size(); i++) {
            C cp = cfactors.get(i);
            if (first) {
                first = false;
            } else {
                sb.append(" + ");
            }
            sb.append("(" + cp.toString() + ")");
            GenPolynomial<C> p = cdenom.get(i);
            sb.append(" / (" + p.toString() + ")");
        }
        if (!first && afactors.size() > 0) {
            sb.append(" + ");
        }
        first = true;
        for (int i = 0; i < afactors.size(); i++) {
            if (first) {
                first = false;
            } else {
                sb.append(" + ");
            }
            AlgebraicNumber<C> ap = afactors.get(i);
            AlgebraicNumberRing<C> ar = ap.factory();
            GenPolynomial<AlgebraicNumber<C>> p = adenom.get(i);
            if (p.degree(0) < ar.modul.degree(0) && ar.modul.degree(0) > 2) {
                sb.append("sum_(" + ar.getGenerator() + " in ");
                sb.append("rootOf(" + ar.modul + ") ) ");
            } else {
                //sb.append("sum_("+ar+") ");
            }
            sb.append("(" + ap.toString() + ")");
            sb.append(" / (" + p.toString() + ")");
            //sb.append(" ## over " + ap.factory() + "\n");
        }
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this container.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    public String toScript() {
        // Python case
        StringBuffer sb = new StringBuffer();
        sb.append(num.toScript());
        sb.append(" / ");
        sb.append(den.toScript());
        sb.append(" = ");
        boolean first = true;
        int i = 0;
        for (C cp : cfactors) {
            if (first) {
                first = false;
            } else {
                sb.append(" + ");
            }
            sb.append(cp.toScript());
            GenPolynomial<C> p = cdenom.get(i);
            sb.append(" / " + p.toScript());
        }
        if (!first) {
            sb.append(" + ");
        }
        first = true;
        i = 0;
        for (AlgebraicNumber<C> ap : afactors) {
            if (first) {
                first = false;
            } else {
                sb.append(" + ");
            }
            AlgebraicNumberRing<C> ar = ap.factory();
            GenPolynomial<AlgebraicNumber<C>> p = adenom.get(i);
            if (p.degree(0) < ar.modul.degree(0) && ar.modul.degree(0) > 2) {
                sb.append("sum_(" + ar.getGenerator().toScript() + " in ");
                sb.append("rootOf(" + ar.modul.toScript() + ") ) ");
            } else {
                //sb.append("sum_("+ar+") ");
            }
            sb.append(ap.toScript());
            sb.append(" / " + p.toScript());
            //sb.append(" ## over " + ap.toScriptFactory() + "\n");
        }
        return sb.toString();
    }


    /**
     * Hash code for this Factors.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = num.hashCode();
        h = h * 37 + den.hashCode();
        h = h * 37 + cfactors.hashCode();
        h = h * 37 + cdenom.hashCode();
        h = h * 37 + afactors.hashCode();
        h = h * 37 + adenom.hashCode();
        return h;
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        if (B == null) {
            return false;
        }
        if (!(B instanceof PartialFraction)) {
            return false;
        }
        PartialFraction<C> a = (PartialFraction<C>) B;
        boolean t = num.equals(a.num) && den.equals(a.den);
        if (!t) {
            return t;
        }
        t = cfactors.equals(a.cfactors);
        if (!t) {
            return t;
        }
        t = cdenom.equals(a.cdenom);
        if (!t) {
            return t;
        }
        t = afactors.equals(a.afactors);
        if (!t) {
            return t;
        }
        t = adenom.equals(a.adenom);
        return t;
    }


    /**
     * Test if correct partial fraction. num/den = sum( a_i / d_i )
     */
    @SuppressWarnings("unchecked")
    public boolean isPartialFraction() {
        QuotientRing<C> qfac = new QuotientRing<C>(num.ring);
        // num / den
        Quotient<C> q = new Quotient<C>(qfac, num, den);
        //System.out.println("q = " + q);
        Quotient<C> qs = qfac.getZERO();
        int i = 0;
        for (C c : cfactors) {
            GenPolynomial<C> cp = cdenom.get(i++);
            // plus c / cp
            GenPolynomial<C> cd = num.ring.getONE().multiply(c);
            Quotient<C> qq = new Quotient<C>(qfac, cd, cp);
            qs = qs.sum(qq);
        }
        //System.out.println("qs = " + qs);
        if (afactors.isEmpty()) {
            return q.compareTo(qs) == 0;
        }

        // sort by extension field
        Set<AlgebraicNumberRing<C>> fields = new HashSet<AlgebraicNumberRing<C>>();
        for (AlgebraicNumber<C> ap : afactors) {
            if (ap.ring.depth() > 1) {
                logger.warn("extension field depth to high"); // todo
            }
            fields.add(ap.ring);
        }
        //System.out.println("fields = " + fields);
        Map<AlgebraicNumberRing<C>, List<AlgebraicNumber<C>>> facs = new HashMap<AlgebraicNumberRing<C>, List<AlgebraicNumber<C>>>();
        for (AlgebraicNumber<C> ap : afactors) {
            List<AlgebraicNumber<C>> cf = facs.get(ap.ring);
            if (cf == null) {
                cf = new ArrayList<AlgebraicNumber<C>>();
            }
            cf.add(ap);
            facs.put(ap.ring, cf);
        }
        //System.out.println("facs = " + facs);
        Map<AlgebraicNumberRing<C>, List<GenPolynomial<AlgebraicNumber<C>>>> pfacs = new HashMap<AlgebraicNumberRing<C>, List<GenPolynomial<AlgebraicNumber<C>>>>();
        for (GenPolynomial<AlgebraicNumber<C>> ap : adenom) {
            AlgebraicNumberRing<C> ar = (AlgebraicNumberRing<C>) ap.ring.coFac;
            List<GenPolynomial<AlgebraicNumber<C>>> cf = pfacs.get(ar);
            if (cf == null) {
                cf = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
            }
            cf.add(ap);
            pfacs.put(ar, cf);
        }
        //System.out.println("pfacs = " + pfacs);

        // check algebraic parts 
        boolean sumMissing = false;
        for (AlgebraicNumberRing<C> ar : fields) {
            if (ar.modul.degree(0) > 2) { //&& p.degree(0) < ar.modul.degree(0) ?
                sumMissing = true;
            }
            List<AlgebraicNumber<C>> cf = facs.get(ar);
            List<GenPolynomial<AlgebraicNumber<C>>> cfp = pfacs.get(ar);
            GenPolynomialRing<AlgebraicNumber<C>> apfac = cfp.get(0).ring;
            QuotientRing<AlgebraicNumber<C>> aqfac = new QuotientRing<AlgebraicNumber<C>>(apfac);
            Quotient<AlgebraicNumber<C>> aq = aqfac.getZERO();
            i = 0;
            for (AlgebraicNumber<C> c : cf) {
                GenPolynomial<AlgebraicNumber<C>> cp = cfp.get(i++);
                // plus c / cp
                GenPolynomial<AlgebraicNumber<C>> cd = apfac.getONE().multiply(c);
                Quotient<AlgebraicNumber<C>> qq = new Quotient<AlgebraicNumber<C>>(aqfac, cd, cp);
                //System.out.println("qq = " + qq);
                aq = aq.sum(qq);
            }
            //System.out.println("aq = " + aq);
            GenPolynomialRing<C> cfac = ar.ring;
            GenPolynomialRing<GenPolynomial<C>> prfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, apfac);
            GenPolynomial<GenPolynomial<C>> pqnum = PolyUtil.<C> fromAlgebraicCoefficients(prfac, aq.num);
            GenPolynomial<GenPolynomial<C>> pqden = PolyUtil.<C> fromAlgebraicCoefficients(prfac, aq.den);
            //System.out.println("pq = (" + pqnum + ") / (" + pqden + ")");

            C one = cfac.coFac.getONE(); // variable should no more occur in coefficient
            GenPolynomialRing<C> pfac = new GenPolynomialRing<C>(cfac.coFac, prfac);
            GenPolynomial<C> pnum = PolyUtil.<C> evaluateFirstRec(cfac, pfac, pqnum, one);
            GenPolynomial<C> pden = PolyUtil.<C> evaluateFirstRec(cfac, pfac, pqden, one);
            //System.out.println("p = (" + pnum + ") / (" + pden + ")");

            // iterate if multiple field extensions
            while (cfac.coFac instanceof AlgebraicNumberRing) {
                //System.out.println("cfac.coFac = " + cfac.coFac.toScript());
                AlgebraicNumberRing<C> ar2 = (AlgebraicNumberRing<C>) cfac.coFac;
                cfac = ar2.ring;
                prfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, apfac);
                GenPolynomial<AlgebraicNumber<C>> prnum = (GenPolynomial<AlgebraicNumber<C>>) pnum;
                GenPolynomial<AlgebraicNumber<C>> prden = (GenPolynomial<AlgebraicNumber<C>>) pden;
                pqnum = PolyUtil.<C> fromAlgebraicCoefficients(prfac, prnum);
                pqden = PolyUtil.<C> fromAlgebraicCoefficients(prfac, prden);
                one = cfac.coFac.getONE(); // variable should no more occur in coefficient
                pfac = new GenPolynomialRing<C>(cfac.coFac, prfac);
                pnum = PolyUtil.<C> evaluateFirstRec(cfac, pfac, pqnum, one);
                pden = PolyUtil.<C> evaluateFirstRec(cfac, pfac, pqden, one);
            }

            Quotient<C> qq = new Quotient<C>(qfac, pnum, pden);
            //System.out.println("qq = " + qq);
            qs = qs.sum(qq);
        }
        boolean cmp = q.compareTo(qs) == 0;
        if (!cmp) {
            System.out.println("q != qs: " + q + " != " + qs);
        }
        return cmp || sumMissing;
    }

}
