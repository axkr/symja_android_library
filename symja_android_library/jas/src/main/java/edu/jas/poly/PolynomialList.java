/*
 * $Id$
 */

package edu.jas.poly;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.kern.Scripting;
import edu.jas.structure.RingElem;


/**
 * List of polynomials. Mainly for storage and printing / toString and
 * conversions to other representations.
 * @author Heinz Kredel
 */

public class PolynomialList<C extends RingElem<C>> implements Comparable<PolynomialList<C>>, Serializable {


    /**
     * The factory for the solvable polynomial ring.
     */
    public final GenPolynomialRing<C> ring;


    /**
     * The data structure is a List of polynomials.
     */
    public final List<GenPolynomial<C>> list;


    private static final Logger logger = Logger.getLogger(PolynomialList.class);


    /**
     * Constructor.
     * @param r polynomial ring factory.
     * @param l list of polynomials.
     */
    public PolynomialList(GenPolynomialRing<C> r, List<GenPolynomial<C>> l) {
        ring = r;
        list = l;
    }


    /**
     * Constructor.
     * @param r solvable polynomial ring factory.
     * @param l list of solvable polynomials.
     */
    public PolynomialList(GenSolvablePolynomialRing<C> r, List<GenSolvablePolynomial<C>> l) {
        this(r, PolynomialList.<C> castToList(l));
    }


    /**
     * Copy this.
     * @return a copy of this.
     */
    public PolynomialList<C> copy() {
        return new PolynomialList<C>(ring, new ArrayList<GenPolynomial<C>>(list));
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object p) {
        if (p == null) {
            return false;
        }
        if (!(p instanceof PolynomialList)) {
            System.out.println("no PolynomialList");
            return false;
        }
        PolynomialList<C> pl = (PolynomialList<C>) p;
        if (!ring.equals(pl.ring)) {
            System.out.println("not same Ring " + ring.toScript() + ", " + pl.ring.toScript());
            return false;
        }
        return (compareTo(pl) == 0);
        // otherwise tables may be different
    }


    /**
     * Polynomial list comparison.
     * @param L other PolynomialList.
     * @return lexicographical comparison, sign of first different polynomials.
     */
    public int compareTo(PolynomialList<C> L) {
        int si = L.list.size();
        if (list.size() < si) { // minimum
            si = list.size();
        }
        int s = 0;
        List<GenPolynomial<C>> l1 = OrderedPolynomialList.<C> sort(ring, list);
        List<GenPolynomial<C>> l2 = OrderedPolynomialList.<C> sort(ring, L.list);
        for (int i = 0; i < si; i++) {
            GenPolynomial<C> a = l1.get(i);
            GenPolynomial<C> b = l2.get(i);
            s = a.compareTo(b);
            if (s != 0) {
                return s;
            }
        }
        if (list.size() > si) {
            return 1;
        }
        if (L.list.size() > si) {
            return -1;
        }
        return s;
    }


    /**
     * Hash code for this polynomial list.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = ring.hashCode();
        h = 37 * h + (list == null ? 0 : list.hashCode());
        return h;
    }


    /**
     * String representation of the polynomial list.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer erg = new StringBuffer();
        String[] vars = null;
        if (ring != null) {
            erg.append(ring.toString());
            vars = ring.getVars();
        }
        boolean first = true;
        erg.append("\n(\n");
        String sa = null;
        for (GenPolynomial<C> oa : list) {
            if (vars != null) {
                sa = oa.toString(vars);
            } else {
                sa = oa.toString();
            }
            if (first) {
                first = false;
            } else {
                erg.append(", ");
                if (sa.length() > 10) {
                    erg.append("\n");
                }
            }
            erg.append("( " + sa + " )");
        }
        erg.append("\n)");
        return erg.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this polynomial list.
     */
    public String toScript() {
        StringBuffer s = new StringBuffer();
        if (ring instanceof GenSolvablePolynomialRing) {
            switch (Scripting.getLang()) {
            case Ruby:
                s.append("SolvIdeal.new(");
                break;
            case Python:
            default:
                s.append("SolvableIdeal(");
            }
        } else {
            switch (Scripting.getLang()) {
            case Ruby:
                s.append("SimIdeal.new(");
                break;
            case Python:
            default:
                s.append("Ideal(");
            }
        }
        if (ring != null) {
            s.append(ring.toScript());
        }
        if (list == null) {
            s.append(")");
            return s.toString();
        }
        switch (Scripting.getLang()) {
        case Ruby:
            s.append(",\"\",[");
            break;
        case Python:
        default:
            s.append(",list=[");
        }
        boolean first = true;
        String sa = null;
        for (GenPolynomial<C> oa : list) {
            sa = oa.toScript();
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            //s.append("( " + sa + " )");
            s.append(sa);
        }
        s.append("])");
        return s.toString();
    }


    /**
     * Get ModuleList from PolynomialList. Extract module from polynomial ring.
     * @see edu.jas.poly.ModuleList
     * @param i number of variables to be contract form the polynomials.
     * @return module list corresponding to this.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> getModuleList(int i) {
        GenPolynomialRing<C> pfac = ring.contract(i);
        logger.debug("contracted ring = " + pfac);
        //System.out.println("contracted ring = " + pfac);

        List<List<GenPolynomial<C>>> vecs = null;
        if (list == null) {
            return new ModuleList<C>(pfac, vecs);
        }
        int rows = list.size();
        vecs = new ArrayList<List<GenPolynomial<C>>>(rows);
        if (rows == 0) { // nothing to do
            return new ModuleList<C>(pfac, vecs);
        }

        ArrayList<GenPolynomial<C>> zr = new ArrayList<GenPolynomial<C>>(i - 1);
        GenPolynomial<C> zero = pfac.getZERO();
        for (int j = 0; j < i; j++) {
            zr.add(j, zero);
        }

        for (GenPolynomial<C> p : list) {
            if (p != null) {
                Map<ExpVector, GenPolynomial<C>> r = p.contract(pfac);
                //System.out.println("r = " + r ); 
                List<GenPolynomial<C>> row = new ArrayList<GenPolynomial<C>>(zr); //zr.clone();
                for (Map.Entry<ExpVector, GenPolynomial<C>> me : r.entrySet()) {
                    ExpVector e = me.getKey();
                    int[] dov = e.dependencyOnVariables();
                    int ix = 0;
                    if (dov.length > 1) {
                        throw new IllegalArgumentException("wrong dependencyOnVariables " + e);
                    } else if (dov.length == 1) {
                        ix = dov[0];
                    }
                    //ix = i-1 - ix; // revert
                    //System.out.println("ix = " + ix ); 
                    GenPolynomial<C> vi = me.getValue(); //r.get( e );
                    row.set(ix, vi);
                }
                //System.out.println("row = " + row ); 
                vecs.add(row);
            }
        }
        return new ModuleList<C>(pfac, vecs);
    }


    /**
     * Get list.
     * @return list from this.
     */
    public List<GenPolynomial<C>> getList() {
        return list;
    }


    /**
     * Get list as List of GenSolvablePolynomials. Required because no List
     * casts allowed. Equivalent to cast
     * (List&lt;GenSolvablePolynomial&lt;C&gt;&gt;) list.
     * @return solvable polynomial list from this.
     */
    public List<GenSolvablePolynomial<C>> castToSolvableList() {
        return castToSolvableList(list);
    }


    /**
     * Get list as List of GenSolvablePolynomials. Required because no List
     * casts allowed. Equivalent to cast
     * (List&lt;GenSolvablePolynomial&lt;C&gt;&gt;) list.
     * @return solvable polynomial list from this.
     */
    public List<GenSolvablePolynomial<C>> getSolvableList() {
        return castToSolvableList(list);
    }


    /**
     * Get ring as GenSolvablePolynomialRing.
     * @return solvable polynomial ring list from this.
     */
    public GenSolvablePolynomialRing<C> getSolvableRing() {
        return (GenSolvablePolynomialRing<C>) ring;
    }


    /**
     * Get list as List of GenSolvablePolynomials. Required because no List
     * casts allowed. Equivalent to cast
     * (List&lt;GenSolvablePolynomial&lt;C&gt;&gt;) list.
     * @param list list of extensions of polynomials.
     * @return solvable polynomial list from this.
     */
    public static <C extends RingElem<C>> List<GenSolvablePolynomial<C>> castToSolvableList(
                    List<GenPolynomial<C>> list) {
        List<GenSolvablePolynomial<C>> slist = null;
        if (list == null) {
            return slist;
        }
        slist = new ArrayList<GenSolvablePolynomial<C>>(list.size());
        GenSolvablePolynomial<C> s;
        for (GenPolynomial<C> p : list) {
            if (!(p instanceof GenSolvablePolynomial)) {
                throw new IllegalArgumentException("no solvable polynomial " + p);
            }
            s = (GenSolvablePolynomial<C>) p;
            slist.add(s);
        }
        return slist;
    }


    /**
     * Get list of list as List of List of GenSolvablePolynomials. Required
     * because no List casts allowed. Equivalent to cast
     * (List&lt;GenSolvablePolynomial&lt;C&gt;&gt;) list.
     * @param list list of extensions of polynomials.
     * @return solvable polynomial list from this.
     */
    public static <C extends RingElem<C>> List<List<GenSolvablePolynomial<C>>> castToSolvableMatrix(
                    List<List<GenPolynomial<C>>> list) {
        List<List<GenSolvablePolynomial<C>>> slist = null;
        if (list == null) {
            return slist;
        }
        slist = new ArrayList<List<GenSolvablePolynomial<C>>>(list.size());
        List<GenSolvablePolynomial<C>> s;
        for (List<GenPolynomial<C>> p : list) {
            s = PolynomialList.<C> castToSolvableList(p);
            slist.add(s);
        }
        return slist;
    }


    /**
     * Get list of extensions of polynomials as List of GenPolynomials. Required
     * because no List casts allowed. Equivalent to cast
     * (List&lt;GenPolynomial&lt;C&gt;&gt;) list. Mainly used for lists of
     * GenSolvablePolynomials.
     * @param slist list of extensions of polynomials.
     * @return polynomial list from slist.
     */
    public static <C extends RingElem<C>> List<GenPolynomial<C>> castToList(
                    List<? extends GenPolynomial<C>> slist) {
        logger.debug("warn: can lead to wrong method dispatch");
        List<GenPolynomial<C>> list = null;
        if (slist == null) {
            return list;
        }
        list = new ArrayList<GenPolynomial<C>>(slist.size());
        for (GenPolynomial<C> p : slist) {
            list.add(p);
        }
        return list;
    }


    /**
     * Get list of list of extensions of polynomials as List of List of
     * GenPolynomials. Required because no List casts allowed. Equivalent to
     * cast (List&lt;GenPolynomial&lt;C&gt;&gt;) list. Mainly used for lists of
     * GenSolvablePolynomials.
     * @param slist list of extensions of polynomials.
     * @return polynomial list from slist.
     */
    public static <C extends RingElem<C>> List<List<GenPolynomial<C>>> castToMatrix(
                    List<List<? extends GenPolynomial<C>>> slist) {
        logger.debug("warn: can lead to wrong method dispatch");
        List<List<GenPolynomial<C>>> list = null;
        if (slist == null) {
            return list;
        }
        list = new ArrayList<List<GenPolynomial<C>>>(slist.size());
        for (List<? extends GenPolynomial<C>> p : slist) {
            list.add(PolynomialList.<C> castToList(p));
        }
        return list;
    }


    /**
     * Test if list contains only ZEROs.
     * @return true, if this is the 0 list, else false
     */
    public boolean isZERO() {
        if (list == null) {
            return true;
        }
        for (GenPolynomial<C> p : list) {
            if (p == null) {
                continue;
            }
            if (!p.isZERO()) {
                return false;
            }
        }
        return true;
    }


    /**
     * Test if list contains a ONE.
     * @return true, if this contains 1, else false
     */
    public boolean isONE() {
        if (list == null) {
            return false;
        }
        for (GenPolynomial<C> p : list) {
            if (p == null) {
                continue;
            }
            if (p.isONE()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Make homogeneous.
     * @return polynomial list of homogeneous polynomials.
     */
    public PolynomialList<C> homogenize() {
        GenPolynomialRing<C> pfac = ring.extend(1);
        List<GenPolynomial<C>> hom = new ArrayList<GenPolynomial<C>>(list.size());
        for (GenPolynomial<C> p : list) {
            GenPolynomial<C> h = p.homogenize(pfac);
            hom.add(h);
        }
        return new PolynomialList<C>(pfac, hom);
    }


    /**
     * Dehomogenize.
     * @return polynomial list of de-homogenized polynomials.
     */
    public PolynomialList<C> deHomogenize() {
        GenPolynomialRing<C> pfac = ring.contract(1);
        List<GenPolynomial<C>> dehom = new ArrayList<GenPolynomial<C>>(list.size());
        for (GenPolynomial<C> p : list) {
            GenPolynomial<C> h = p.deHomogenize(pfac);
            dehom.add(h);
        }
        return new PolynomialList<C>(pfac, dehom);
    }


    /**
     * Test if all polynomials are homogeneous.
     * @return true, if all polynomials are homogeneous, else false
     */
    public boolean isHomogeneous() {
        if (list == null) {
            return true;
        }
        for (GenPolynomial<C> p : list) {
            if (p == null) {
                continue;
            }
            if (!p.isHomogeneous()) {
                return false;
            }
        }
        return true;
    }

}
