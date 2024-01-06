/*
 * $Id$
 */

package edu.jas.vector;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.structure.RingElem;


/**
 * Basic linear algebra methods. Implements Basic linear algebra computations
 * and tests. <b>Note:</b> will eventually use wrong method dispatch in JRE when
 * used with GenSolvablePolynomial.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class BasicLinAlg<C extends RingElem<C>> implements Serializable {


    private static final Logger logger = LogManager.getLogger(BasicLinAlg.class);


    //private static final boolean debug = logger.isDebugEnabled();


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
     * Negative of vectors of ring elements.
     * @param a a ring element list.
     * @return -a, the vector of -a.
     */
    public List<C> vectorNegate(List<C> a) {
        if (a == null) {
            return a;
        }
        List<C> V = new ArrayList<C>(a.size());
        Iterator<C> it = a.iterator();
        while (it.hasNext()) {
            C pi = it.next();
            if (pi != null) {
                pi = pi.negate();
            }
            V.add(pi);
        }
        //System.out.println("vectorNegate" + V);
        return V;
    }


    /**
     * Combination of vectors for reduction representation.
     * @param a a ring element list.
     * @param b a ring element list.
     * @return a-b, the vector difference of a and b, with one entry more.
     */
    public List<C> vectorCombineRep(List<C> a, List<C> b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("a and b may not be empty");
        }
        // if (a.size() != b.size()) {
        //     throw new IllegalArgumentException("#a != #b");
        // }
        List<C> V = new ArrayList<C>(a.size() + 1);
        Iterator<C> it = a.iterator();
        Iterator<C> jt = b.iterator();
        while (it.hasNext() && jt.hasNext()) {
            C x = it.next();
            C y = jt.next();
            if (x == null) {
                if (y == null) {
                    //x = y;
                } else {
                    x = y.negate();
                }
            } else {
                if (y == null) {
                    //x = y;
                } else {
                    x = x.subtract(y);
                }
            }
            V.add(x);
        }
        if (it.hasNext() || jt.hasNext()) {
            logger.error("vectorCombineRep wrong sizes: it = {}, jt = {}", it, jt);
            //throw new RuntimeException("it = " + it + ", jt = " + jt);
        }
        V.add(null);
        //System.out.println("vectorCombineRep" + V);
        return V;
    }


    /**
     * Combination of vectors for syzygy representation.
     * @param a a ring element list.
     * @param b a ring element list.
     * @return (-a)+b, the vector sum of -a and b, with one entry more.
     */
    public List<C> vectorCombineSyz(List<C> a, List<C> b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("a and b may not be empty");
        }
        // if (a.size() != b.size()) {
        //     throw new IllegalArgumentException("#a != #b");
        // }
        List<C> V = new ArrayList<C>(a.size() + 1);
        Iterator<C> it = a.iterator();
        Iterator<C> jt = b.iterator();
        while (it.hasNext() && jt.hasNext()) {
            C x = it.next();
            C y = jt.next();
            if (x == null) {
                if (y == null) {
                    //x = y;
                } else {
                    x = y;
                }
            } else {
                if (y == null) {
                    //x = y;
                } else {
                    x = x.sum(y);
                }
            }
            V.add(x);
        }
        if (it.hasNext() || jt.hasNext()) {
            logger.error("vectorCombineSyz wrong sizes: it = {}, jt = {}", it, jt);
            //throw new RuntimeException("it = " + it + ", jt = " + jt);
        }
        V.add(null);
        //System.out.println("vectorCombineSyz" + V);
        return V;
    }


    /**
     * Combination of vectors for old representation.
     * @param a a ring element list.
     * @param b a ring element list.
     * @return -a-b, the vector difference of -a and b, with one entry more.
     */
    public List<C> vectorCombineOld(List<C> a, List<C> b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("a and b may not be empty");
        }
        // if (a.size() != b.size()) {
        //     throw new IllegalArgumentException("#a != #b");
        // }
        List<C> V = new ArrayList<C>(a.size() + 1);
        Iterator<C> it = a.iterator();
        Iterator<C> jt = b.iterator();
        while (it.hasNext() && jt.hasNext()) {
            C x = it.next();
            C y = jt.next();
            if (x != null) {
                //System.out.println("ms = " + m + " " + x);
                x = x.negate();
            }
            if (y != null) {
                y = y.negate();
                //System.out.println("mh = " + m + " " + y);
            }
            if (x == null) {
                x = y;
            } else if (y != null) {
                x = x.sum(y);
            }
            //System.out.println("mx = " + m + " " + x);
            V.add(x);
        }
        if (it.hasNext() || jt.hasNext()) {
            logger.error("vectorCombineOld wrong sizes: it = {}, jt = {}", it, jt);
            //throw new RuntimeException("it = " + it + ", jt = " + jt);
        }
        V.add(null);
        //System.out.println("vectorCombineOld" + V);
        return V;
    }


    /**
     * Generation of a vector of ring elements.
     * @param n length of vector.
     * @param a a ring element to fill vector entries.
     * @return V, a vector of length n and entries a.
     */
    public List<C> genVector(int n, C a) {
        List<C> V = new ArrayList<C>(n);
        if (n == 0) {
            return V;
        }
        for (int i = 0; i < n; i++) {
            V.add(a);
        }
        return V;
    }


    /**
     * Generation of a vector of ring elements.
     * @param n length of vector.
     * @param a a ring element to fill vector entries.
     * @param A vector of starting first entries.
     * @return V, a vector of length n and entries a, respectively A.
     */
    public List<C> genVector(int n, C a, List<C> A) {
        List<C> V = new ArrayList<C>(n);
        if (n == 0) {
            return V;
        }
        int i = 0;
        for (C b : A) {
            if (i < n) {
                V.add(b);
            } else {
                break;
            }
            i++;
        }
        for (int j = A.size(); j < n; j++) {
            V.add(a);
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


    /**
     * Product of a vector and a matrix of ring elements.
     * @param G a vectors of ring elements.
     * @param F a matrix of ring element lists.
     * @return the left product of G and F.
     */
    public GenVector<C> leftProduct(GenVector<C> G, GenMatrix<C> F) {
        ArrayList<C> sp = new ArrayList<C>(G.val.size());
        Iterator<ArrayList<C>> jt = F.matrix.iterator();
        while (jt.hasNext()) {
            ArrayList<C> pj = jt.next();
            if (pj == null) {
                continue;
            }
            C s = scalarProduct(G.val, pj);
            sp.add(s);
        }
        return new GenVector<C>(G.modul, sp);
    }


    /**
     * Product of a vector and a matrix of ring elements.
     * @param G a vector of element list.
     * @param F a matrix of ring element lists.
     * @return the right product of G and F.
     */
    public GenVector<C> rightProduct(GenVector<C> G, GenMatrix<C> F) {
        ArrayList<C> sp = new ArrayList<C>(G.val.size());
        Iterator<ArrayList<C>> jt = F.matrix.iterator();
        while (jt.hasNext()) {
            ArrayList<C> pj = jt.next();
            if (pj == null) {
                continue;
            }
            C s = scalarProduct(pj, G.val);
            sp.add(s);
        }
        return new GenVector<C>(G.modul, sp);
    }

}
