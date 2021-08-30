/*
 * $Id$
 */

package edu.jas.ufd;


import java.io.Serializable;
import java.util.SortedMap;
import java.util.Map;

import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Container for the factors of a eventually non-squarefree factorization.
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class FactorsMap<C extends GcdRingElem<C>> implements Serializable {


    /**
     * Original polynomial to be factored with coefficients from C.
     */
    public final GenPolynomial<C> poly;


    /**
     * List of factors with coefficients from C.
     */
    public final SortedMap<GenPolynomial<C>, Long> factors;


    /**
     * List of factors with coefficients from AlgebraicNumberRings.
     */
    public final SortedMap<Factors<C>, Long> afactors;


    /**
     * Constructor.
     * @param p given GenPolynomial over C.
     * @param map irreducible factors of p with coefficients from C.
     */
    public FactorsMap(GenPolynomial<C> p, SortedMap<GenPolynomial<C>, Long> map) {
        this(p, map, null);
    }


    /**
     * Constructor.
     * @param p given GenPolynomial over C.
     * @param map irreducible factors of p with coefficients from C.
     * @param amap irreducible factors of p with coefficients from an algebraic
     *            number field.
     */
    public FactorsMap(GenPolynomial<C> p, SortedMap<GenPolynomial<C>, Long> map,
                    SortedMap<Factors<C>, Long> amap) {
        poly = p;
        factors = map;
        afactors = amap;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(poly.toString());
        sb.append(" =\n");
        boolean first = true;
        for (Map.Entry<GenPolynomial<C>,Long> me : factors.entrySet()) {
            GenPolynomial<C> p = me.getKey();
            if (first) {
                first = false;
            } else {
                sb.append(",\n ");
            }
            sb.append(p.toString());
            long e = me.getValue();
            if (e > 1) {
                sb.append("**" + e);
            }
        }
        if (afactors == null) {
            return sb.toString();
        }
        for (Map.Entry<Factors<C>,Long> me : afactors.entrySet()) {
            Factors<C> f = me.getKey();
            if (first) {
                first = false;
            } else {
                sb.append(",\n ");
            }
            sb.append(f.toString());
            Long e = me.getValue();
            if (e == null) {
                continue;
            }
            if (e > 1) {
                sb.append("**" + e);
            }
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
        //sb.append(poly.toScript());
        //sb.append(" =\n");
        boolean first = true;
        for (Map.Entry<GenPolynomial<C>,Long> me : factors.entrySet()) {
            GenPolynomial<C> p = me.getKey();
            if (first) {
                first = false;
            } else {
                sb.append("\n * ");
            }
            sb.append(p.toScript());
            long e = me.getValue();
            if (e > 1) {
                sb.append("**" + e);
            }
        }
        if (afactors == null) {
            return sb.toString();
        }
        for (Map.Entry<Factors<C>,Long> me : afactors.entrySet()) {
            Factors<C> f = me.getKey();
            if (first) {
                first = false;
            } else {
                sb.append("\n * ");
            }
            Long e = me.getValue();
            if (e == null) { // should not happen
                System.out.println("f = " + f);
                System.out.println("afactors = " + afactors);
                throw new RuntimeException("this should not happen");
            }
            if (e == 1) {
                sb.append(f.toScript());
            } else {
                sb.append("(\n");
                sb.append(f.toScript());
                sb.append("\n)**" + e);
            }
        }
        return sb.toString();
    }


    /**
     * Length. Number of factors.
     * @return number of distinct factors.
     */
    public int length() {
        int i = factors.keySet().size();
        if (afactors == null) {
            return i;
        }
        for (Factors<C> f : afactors.keySet()) {
             i += f.length();
        }
        return i;
    }


    /**
     * Find largest extension field.
     * @return largest extension field or null if no extension field
     */
    public AlgebraicNumberRing<C> findExtensionField() {
        if (afactors == null) {
            return null;
        }
        AlgebraicNumberRing<C> ar = null;
        int depth = 0;
        for (Factors<C> f : afactors.keySet()) {
            AlgebraicNumberRing<C> aring = f.findExtensionField();
            if (aring == null) {
                continue;
            }
            int d = aring.depth();
            if (d > depth) {
                depth = d;
                ar = aring;
            }
        }
        return ar;
    }

}
