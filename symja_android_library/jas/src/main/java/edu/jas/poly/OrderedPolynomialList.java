/*
 * $Id$
 */

package edu.jas.poly;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import edu.jas.structure.RingElem;


/**
 * Ordered list of polynomials. Mainly for storage and printing / toString and
 * conversions to other representations. Polynomials in this list are sorted
 * according to their head terms.
 * @author Heinz Kredel
 */

public class OrderedPolynomialList<C extends RingElem<C>> extends PolynomialList<C> {


    /**
     * Constructor.
     * @param r polynomial ring factory.
     * @param l list of polynomials.
     */
    public OrderedPolynomialList(GenPolynomialRing<C> r, List<GenPolynomial<C>> l) {
        super(r, sort(r, l));
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object p) {
        if (!super.equals(p)) {
            return false;
        }
        OrderedPolynomialList<C> pl = null;
        try {
            pl = (OrderedPolynomialList<C>) p;
        } catch (ClassCastException ignored) {
        }
        if (pl == null) {
            return false;
        }
        // compare sorted lists
        // done already in super.equals()
        return true;
    }


    /**
     * Hash code for OrderedPolynomialList.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }


    /**
     * Sort a list of polynomials with respect to the ascending order of the
     * leading Exponent vectors. The term order is taken from the ring.
     * @param r polynomial ring factory.
     * @param L polynomial list.
     * @return sorted polynomial list from L.
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> List<GenPolynomial<C>> sort(GenPolynomialRing<C> r,
                    List<GenPolynomial<C>> L) {
        if (L == null) {
            return L;
        }
        if (L.size() <= 1) { // nothing to sort
            return L;
        }
        final Comparator<ExpVector> evc = r.tord.getAscendComparator();
        Comparator<GenPolynomial<C>> cmp = new Comparator<GenPolynomial<C>>() {


            public int compare(GenPolynomial<C> p1, GenPolynomial<C> p2) {
                ExpVector e1 = p1.leadingExpVector();
                ExpVector e2 = p2.leadingExpVector();
                if (e1 == null) {
                    return -1; // dont care
                }
                if (e2 == null) {
                    return 1; // dont care
                }
                if (e1.length() != e2.length()) {
                    if (e1.length() > e2.length()) {
                        return 1; // dont care
                    }
                    return -1; // dont care
                }
                return evc.compare(e1, e2);
            }
        };
        GenPolynomial<C>[] s = null;
        try {
            s = (GenPolynomial<C>[]) new GenPolynomial[L.size()];
            int i = 0;
            for (GenPolynomial<C> p : L) {
                s[i++] = p;
            }
            Arrays.<GenPolynomial<C>> sort(s, cmp);
            return new ArrayList<GenPolynomial<C>>(Arrays.<GenPolynomial<C>> asList(s));
        } catch (ClassCastException ok) {
            System.out.println("Warning: polynomials not sorted");
        }
        return L; // unsorted
    }

}
