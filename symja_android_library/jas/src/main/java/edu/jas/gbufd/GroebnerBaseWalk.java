/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.ReductionAbstract;
import edu.jas.gb.ReductionSeq;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Groebner Base sequential Groebner Walk algorithm. Implements Groebner base
 * computation via Groebner Walk algorithm. See "The generic Groebner walk" by
 * Fukuda, Jensen, Lauritzen, Thomas, 2005.
 * @param <C> coefficient type
 * @author Heinz Kredel
 * 
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */
public class GroebnerBaseWalk<C extends GcdRingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseWalk.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * The backing GB algorithm implementation.
     */
    private GroebnerBaseAbstract<C> sgb;


    /**
     * Constructor.
     */
    public GroebnerBaseWalk() {
        super();
        sgb = null;
    }


    /**
     * Constructor.
     * @param coFac coefficient ring of polynomial ring.
     */
    public GroebnerBaseWalk(RingFactory<C> coFac) {
        this(GBFactory.<C> getImplementation(coFac));
    }


    /**
     * Constructor.
     * @param gb backing GB algorithm.
     */
    public GroebnerBaseWalk(GroebnerBaseAbstract<C> gb) {
        super(gb.red, gb.strategy);
        sgb = gb;
    }


    /**
     * Get the String representation with GB engine.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (sgb == null) {
            return "GroebnerBaseWalk()";
        }
        return "GroebnerBaseWalk( " + sgb.toString() + " )";
    }

    /**
     * Groebner base using Groebner Walk algorithm.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a INVLEX term order Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(F);
        G = PolyUtil.<C> monic(G);
        if (G == null || G.size() == 0) {
            return G;
        }
        GenPolynomialRing<C> pfac = G.get(0).ring;
        if (!pfac.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field: " + pfac.coFac);
        }
        if (G.size() <= 1) {
            GenPolynomial<C> p = pfac.copy(G.get(0)); // change term order
            G.clear();
            G.add(p);
            return G;
        }
        // compute in graded term order
        //TermOrder grord = new TermOrder(TermOrder.REVITDG);
        TermOrder grord = new TermOrder(TermOrder.IGRLEX);
        GenPolynomialRing<C> gfac = new GenPolynomialRing<C>(pfac, grord);
        if (debug) {
            logger.info("gfac = " + gfac.toScript());
        }
        List<GenPolynomial<C>> Fp = gfac.copy(F);

        // compute graded term order Groebner base
        if (sgb == null) {
            sgb = GBFactory.<C> getImplementation(pfac.coFac, strategy);
        }
        List<GenPolynomial<C>> Gp = sgb.GB(modv, Fp);
        logger.info("graded GB = " + Gp);
        if (grord.equals(pfac.tord)) {
            return Gp;
        }
        if (Gp.size() == 1) {
            GenPolynomial<C> p = pfac.copy(Gp.get(0)); // change term order
            G.clear();
            G.add(p);
            return G;
        }
        // info on FGLM
        int z = commonZeroTest(Gp);
        if (z == 0) {
            logger.info("ideal zero dimensional, can use also FGLM algorithm");
        }
        // compute INVLEX Groebner base via Groebner Walk
        G = walkGroebnerToLex(modv, Gp);
        return G;
    }


    /**
     * Converts Groebner bases w.r.t. total degree term order to Groebner base
     * w.r.t to inverse lexicographical term order.
     * @param modv module variable number.
     * @param Gl Groebner base with respect to graded term order.
     * @return Groebner base w.r.t to inverse lexicographical term order
     */
    public List<GenPolynomial<C>> walkGroebnerToLex(int modv, List<GenPolynomial<C>> Gl) {
        if (Gl == null || Gl.size() == 0) {
            throw new IllegalArgumentException("G may not be null or empty");
        }
        //Polynomial ring of input Groebner basis Gl
        GenPolynomialRing<C> ring = Gl.get(0).ring;
        if (debug) {
            logger.info("ring = " + ring.toScript());
        }
        //Polynomial ring of newGB with INVLEX order
        TermOrder lexi = new TermOrder(TermOrder.INVLEX);
        //TermOrder lexi = new TermOrder(TermOrder.REVILEX);
        GenPolynomialRing<C> ufac = new GenPolynomialRing<C>(ring, lexi);
        if (debug) {
            logger.info("ufac = " + ufac.toScript());
        }
        logger.info("ev1 = " + ring.tord + ", ev2 = " + ufac.tord);
        List<GenPolynomial<C>> Giter = Gl;
        List<ExpVector> marks = new ArrayList<ExpVector>();
        for (GenPolynomial<C> p : Giter) {
            marks.add(p.leadingExpVector());
        }
        List<Monomial<C>> M = new ArrayList<Monomial<C>>();
        for (GenPolynomial<C> p : Giter) {
            M.add(new Monomial<C>(p.leadingMonomial()));
        }
        List<Monomial<C>> Mp = new ArrayList<Monomial<C>>(M);

        long[][] il = TermOrderByName.weightForOrder(TermOrder.INVLEX, ring.nvar);
        il = TermOrder.reverseWeight(il).getWeight(); // because of weightDeg usage
        ExpVector w = null;
        boolean done = false;
        while (!done) {
            // determine V and w
            PolynomialList<C> Pl = new PolynomialList<C>(ring, Giter);
            SortedSet<ExpVector> delta = Pl.deltaExpVectors(marks);
            logger.info("marks = " + marks);
            //logger.info("delta(marks) = " + delta);
            logger.info("w_old = " + w);
            //TermOrder.EVComparator ev1 = ring.tord.getDescendComparator();
            //TermOrder.EVComparator ev2 = ufac.tord.getDescendComparator();
            TermOrder.EVComparator ev1 = ring.tord.getAscendComparator();
            TermOrder.EVComparator ev2 = ufac.tord.getAscendComparator();
            ExpVector v = null;
            long d = 0; // = Long.MIN_VALUE;
            for (ExpVector e : delta) {
                //if (ev1.compare(ring.evzero, e) <= 0 || ev2.compare(ring.evzero, e) >= 0) {
                if (ev1.compare(ring.evzero, e) >= 0 || ev2.compare(ring.evzero, e) <= 0) {
                    //logger.info("skip e = " + e);
                    continue;
                }
                int s = 0;
                long d2 = 0;
                ExpVector vt = null;
                ExpVector et = null;
                if (v == null) {
                    v = e;
                    logger.info("init v = " + v);
                    continue;
                }
                for (long[] tau : il) {
                    //logger.info("current tau = " + Arrays.toString(tau));
                    //compare
                    d = v.weightDeg(tau);
                    d2 = e.weightDeg(tau);
                    vt = v.scalarMultiply(d2);
                    et = e.scalarMultiply(d);
                    s = ev1.compare(et, vt);
                    if (s == 0) {
                        continue; // next tau
                    } else if (s > 0) { // <, (> by example)
                        v = e;
                        break;
                    } else {
                        break;
                    }
                }
                //logger.info("step s  = " + s + ", d = " + d + ", d2 = " + d2 + ", e = " + e + ", v = " + v + ", et = " + et + ", vt = " + vt);
            }
            logger.info("minimal v = " + v);
            if (v == null) {
                done = true;
                break; // finished
            }
            w = v;
            // determine facet polynomials for w
            //logger.info("w_new = " + w); // + ", " + ring.valueOf(w));
            List<GenPolynomial<C>> iG = new ArrayList<GenPolynomial<C>>();
            int i = 0;
            for (GenPolynomial<C> f : Giter) {
                ExpVector h = marks.get(i++);
                GenPolynomial<C> ing = f.leadingFacetPolynomial(h, w);
                logger.info("ing_g = [" + ing + "], f = " + f.leadingExpVector());
                iG.add(ing);
            }
            List<GenPolynomial<C>> inOmega = ufac.copy(iG);
            logger.info("inOmega = " + inOmega);

            // INVLEX GB of inOmega
            List<GenPolynomial<C>> inOG = sgb.GB(modv, inOmega);
            logger.info("GB(inOmega) = " + inOG);
            // remark polynomials 
            marks.clear();
            M.clear();
            for (GenPolynomial<C> p : inOG) {
                marks.add(p.leadingExpVector());
                M.add(new Monomial<C>(p.leadingMonomial()));
            }
            //logger.info("marks(newGB()) = " + marks);
            // lift and reduce
            List<GenPolynomial<C>> G = liftReductas(M, Mp, Giter, inOG);
            if (debug) {
                logger.info("minimal lift inOG  = " + G);
            }
            if (G.size() == 1) {
                GenPolynomial<C> p = ufac.copy(G.get(0)); // change term order
                G.clear();
                G.add(p);
                return G;
            }
            // iterate
            Giter = G;
            Mp.clear();
            Mp.addAll(M);
        }
        return Giter;
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    @Override
    public void terminate() {
        if (sgb == null) {
            return;
        }
        sgb.terminate();
    }


    /**
     * Cancel ThreadPool.
     */
    @Override
    public int cancel() {
        if (sgb == null) {
            return 0;
        }
        return sgb.cancel();
    }


    /**
     * Lift leading polynomials to full Groebner base with respect to term
     * order.
     * @param M new leading monomial list of polynomials as marks.
     * @param Mp old leading monomial list of polynomials as marks.
     * @param G Groebner base polynomials for lift.
     * @param A polynomial list of leading omega polynomials to lift.
     * @return lift(A) a Groebner base wrt M of ideal(G).
     */
    public List<GenPolynomial<C>> liftReductas(List<Monomial<C>> M, List<Monomial<C>> Mp,
                    List<GenPolynomial<C>> G, List<GenPolynomial<C>> A) {
        if (G == null || M == null || Mp == null || G.isEmpty()) {
            throw new IllegalArgumentException("null or empty lists not allowed");
        }
        if (A == null || A.isEmpty()) {
            return A;
        }
        if (G.size() != Mp.size() || A.size() != M.size()) {
            throw new IllegalArgumentException("equal sized lists required");
        }
        GenPolynomial<C> pol = G.get(0);
        if (pol == null) {
            throw new IllegalArgumentException("null polynomial not allowed");
        }
        // compute f^P for f in A
        GenPolynomialRing<C> oring = pol.ring;
        //logger.info("lifter G   = " + G + ", Mp = " + Mp);
        List<GenPolynomial<C>> Gp = new ArrayList<GenPolynomial<C>>(G.size());
        int i = 0;
        int len = G.size();
        for (i = 0; i < len; i++) {
            GenPolynomial<C> s = G.get(i).subtract(Mp.get(i));
            Gp.add(s);
        }
        if (debug) {
            logger.info("lifter GB: Gp  = " + Gp + ", Mp = " + Mp);
        }
        List<GenPolynomial<C>> Ap = oring.copy(A);
        //logger.info("to lift Ap = " + Ap);
        ReductionAbstract<C> sred = (ReductionAbstract<C>) sgb.red; //new ReductionSeq<C>();
        List<GenPolynomial<C>> red = new ArrayList<GenPolynomial<C>>();
        len = Ap.size();
        for (i = 0; i < len; i++) {
            GenPolynomial<C> r = sred.normalformMarked(Mp, Gp, Ap.get(i));
            red.add(r);
        }
        //logger.info("red(omega) = " + red);
        // combine f - f^P in tring
        GenPolynomialRing<C> tring = A.get(0).ring;
        if (debug) {
            logger.info("tring = " + tring.toScript() + ", M = " + M);
        }
        List<GenPolynomial<C>> nb = new ArrayList<GenPolynomial<C>>(red.size());
        for (i = 0; i < A.size(); i++) {
            GenPolynomial<C> x = tring.copy(A.get(i)); // Ap?
            GenPolynomial<C> r = tring.copy(red.get(i));
            GenPolynomial<C> s = x.subtract(r);
            Monomial<C> m = M.get(i);
            s.doAddTo(m.coefficient().negate(), m.exponent()); // remove marked term
            //s = s.subtract(M.get(i)); // remove head term
            nb.add(s);
        }
        if (debug) {
            logger.info("lifted-M, nb = " + nb);
        }
        // minimal GB with preserved marks
        //Collections.reverse(nb); // important for lex GB
        len = nb.size();
        i = 0;
        while (i < len) {
            GenPolynomial<C> a = nb.remove(0);
            Monomial<C> m = M.remove(0); // in step with element from nb
            if (debug) {
                logger.info("doing " + a + ", lt = " + m);
            }
            //a = sgb.red.normalform(nb, a);
            a = sred.normalformMarked(M, nb, a);
            if (debug) {
                logger.info("done, a = " + a + ", lt = " + a.leadingExpVector());
            }
            nb.add(a); // adds as last
            M.add(m);
            i++;
        }
        // remove mark after minimal
        for (i = 0; i < len; i++) {
            GenPolynomial<C> a = nb.get(i);
            Monomial<C> m = M.get(i);
            a.doAddTo(m.coefficient(), m.exponent()); // re-add marked term
            nb.set(i, a);
        }
        //Collections.reverse(nb); // undo reverse
        return nb;
    }

}
