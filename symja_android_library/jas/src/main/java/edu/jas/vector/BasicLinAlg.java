/*
 * $Id$
 */

package edu.jas.vector;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;


/**
 * Basic linear algebra methods. Implements Basic linear algebra computations
 * and tests. <b>Note:</b> will use wrong method dispatch in JRE when used with
 * GenSolvablePolynomial.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class BasicLinAlg<C extends RingElem<C>> implements Serializable {


    private static final Logger logger = Logger.getLogger(BasicLinAlg.class);


    //private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public BasicLinAlg() {
    }


    /**
     * Scalar product of vectors of ring elements.
     * @param G a ring element list.
     * @param F a ring element list.
     * @return the scalar product of G and F.
     */
    public C scalarProduct(List<C> G, List<C> F) {
        C sp = null;
        Iterator<C> it = G.iterator();
        Iterator<C> jt = F.iterator();
        while (it.hasNext() && jt.hasNext()) {
            C pi = it.next();
            C pj = jt.next();
            if (pi == null || pj == null) {
                continue;
            }
            if (sp == null) {
                sp = pi.multiply(pj);
            } else {
                sp = sp.sum(pi.multiply(pj));
            }
        }
        if (it.hasNext() || jt.hasNext()) {
            logger.error("scalarProduct wrong sizes");
        }
        return sp;
    }


    /**
     * Scalar product of vectors and a matrix of ring elements.
     * @param G a ring element list.
     * @param F a list of ring element lists.
     * @return the scalar product of G and F.
     */
    public List<C> leftScalarProduct(List<C> G, List<List<C>> F) {
        List<C> sp = null; //new ArrayList<C>(G.size());
        Iterator<C> it = G.iterator();
        Iterator<List<C>> jt = F.iterator();
        while (it.hasNext() && jt.hasNext()) {
            C pi = it.next();
            List<C> pj = jt.next();
            if (pi == null || pj == null) {
                continue;
            }
            List<C> s = scalarProduct(pi, pj);
            if (sp == null) {
                sp = s;
            } else {
                sp = vectorAdd(sp, s);
            }
        }
        if (it.hasNext() || jt.hasNext()) {
            logger.error("scalarProduct wrong sizes");
        }
        return sp;
    }


    /**
     * Scalar product of vectors and a matrix of ring elements.
     * @param G a ring element list.
     * @param F a list of ring element lists.
     * @return the right scalar product of G and F.
     */
    public List<C> rightScalarProduct(List<C> G, List<List<C>> F) {
        List<C> sp = null; //new ArrayList<C>(G.size());
        Iterator<C> it = G.iterator();
        Iterator<List<C>> jt = F.iterator();
        while (it.hasNext() && jt.hasNext()) {
            C pi = it.next();
            List<C> pj = jt.next();
            if (pi == null || pj == null) {
                continue;
            }
            List<C> s = scalarProduct(pj, pi);
            if (sp == null) {
                sp = s;
            } else {
                sp = vectorAdd(sp, s);
            }
        }
        if (it.hasNext() || jt.hasNext()) {
            logger.error("scalarProduct wrong sizes");
        }
        return sp;
    }


    /**
     * Addition of vectors of ring elements.
     * @param a a ring element list.
     * @param b a ring element list.
     * @return a+b, the vector sum of a and b.
     */

    public List<C> vectorAdd(List<C> a, List<C> b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        List<C> V = new ArrayList<C>(a.size());
        Iterator<C> it = a.iterator();
        Iterator<C> jt = b.iterator();
        while (it.hasNext() && jt.hasNext()) {
            C pi = it.next();
            C pj = jt.next();
            C p = pi.sum(pj);
            V.add(p);
        }
        //System.out.println("vectorAdd" + V);
        if (it.hasNext() || jt.hasNext()) {
            logger.error("vectorAdd wrong sizes");
        }
        return V;
    }


    /**
     * Test vector of zero ring elements.
     * @param a a ring element list.
     * @return true, if all polynomial in a are zero, else false.
     */
    public boolean isZero(List<C> a) {
        if (a == null) {
            return true;
        }
        for (C pi : a) {
            if (pi == null) {
                continue;
            }
            if (!pi.isZERO()) {
                return false;
            }
        }
        return true;
    }


    /**
     * Scalar product of ring element with vector of ring elements.
     * @param p a ring element.
     * @param F a ring element list.
     * @return the scalar product of p and F.
     */
    public List<C> scalarProduct(C p, List<C> F) {
        List<C> V = new ArrayList<C>(F.size());
        for (C pi : F) {
            if (p != null) {
                pi = p.multiply(pi);
            } else {
                pi = null;
            }
            V.add(pi);
        }
        return V;
    }


    /**
     * Scalar product of vector of ring element with ring element.
     * @param F a ring element list.
     * @param p a ring element.
     * @return the scalar product of F and p.
     */
    public List<C> scalarProduct(List<C> F, C p) {
        List<C> V = new ArrayList<C>(F.size());
        for (C pi : F) {
            if (pi != null) {
                pi = pi.multiply(p);
            }
            V.add(pi);
        }
        return V;
    }

}
