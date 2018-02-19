/*
 * $Id$
 */

package edu.jas.gbufd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.ReductionAbstract;
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
 * <p>
 * The start term order t1 can be set by a constructor. The target term order t2
 * is taken from the input polynomials term order.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see GBFactory
 */
public class GroebnerBaseWalk<C extends GcdRingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseWalk.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * The backing GB algorithm implementation.
     */
    protected GroebnerBaseAbstract<C> sgb;


    /**
     * The start term order t1.
     */
    //protected TermOrder startTO = TermOrderByName.IGRLEX.blockOrder(2); 
    protected TermOrder startTO = TermOrderByName.IGRLEX;


    /**
     * Print intermediate GB after this number of iterations.
     */
    int iterPrint = 100;


    /**
     * Constructor.
     */
    public GroebnerBaseWalk() {
        super();
        sgb = null;
    }


    /**
     * Constructor.
     *
     * @param coFac coefficient ring of polynomial ring.
     */
    public GroebnerBaseWalk(RingFactory<C> coFac) {
        this(GBFactory.<C>getImplementation(coFac));
    }


    /**
     * Constructor.
     *
     * @param coFac coefficient ring of polynomial ring.
     * @param t1    start term order.
     */
    public GroebnerBaseWalk(RingFactory<C> coFac, TermOrder t1) {
        this(GBFactory.<C>getImplementation(coFac), t1);
    }


    /**
     * Constructor.
     *
     * @param gb backing GB algorithm.
     */
    public GroebnerBaseWalk(GroebnerBaseAbstract<C> gb) {
        super(gb.red, gb.strategy);
        sgb = gb;
    }


    /**
     * Constructor.
     *
     * @param gb backing GB algorithm.
     * @param t1 start term order.
     */
    public GroebnerBaseWalk(GroebnerBaseAbstract<C> gb, TermOrder t1) {
        this(gb);
        startTO = t1;
    }


    /**
     * Get the String representation with GB engine.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        if (sgb == null) {
            return "GroebnerBaseWalk(" + startTO.toScript() + ")";
        }
        return "GroebnerBaseWalk( " + sgb.toString() + ", " + startTO.toScript() + " )";
    }


    /**
     * Groebner base using Groebner Walk algorithm.
     *
     * @param modv module variable number.
     * @param F    polynomial list in target term order.
     * @return GB(F) a INVLEX / target term order Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(F);
        G = PolyUtil.<C>monic(G);
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
        // compute in graded / start term order
        TermOrder grord = startTO; //new TermOrder(TermOrder.IGRLEX);
        GenPolynomialRing<C> gfac = new GenPolynomialRing<C>(pfac, grord);
        if (debug) {
            logger.info("gfac = " + gfac.toScript());
        }
        List<GenPolynomial<C>> Fp = gfac.copy(F);
        logger.info("Term order: graded = " + grord + ", target = " + pfac.tord);

        // compute graded / start term order Groebner base
        if (sgb == null) {
            sgb = GBFactory.<C>getImplementation(pfac.coFac, strategy);
        }
        List<GenPolynomial<C>> Gp = sgb.GB(modv, Fp);
        logger.info("graded / start GB = " + Gp);
        if (grord.equals(pfac.tord)) {
            return Gp;
        }
        if (Gp.size() == 1) { // also dimension -1
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
        // compute target term order Groebner base via Groebner Walk
        G = walkGroebnerToTarget(modv, Gp, pfac);
        return G;
    }


    /**
     * Converts Groebner bases w.r.t. total degree / start term order to
     * Groebner base w.r.t to inverse lexicographical / target term order.
     *
     * @param modv module variable number.
     * @param Gl   Groebner base with respect to graded / start term order.
     * @param ufac target polynomial ring and term order.
     * @return Groebner base w.r.t inverse lexicographical / target term order
     */
    public List<GenPolynomial<C>> walkGroebnerToTarget(int modv, List<GenPolynomial<C>> Gl,
                                                       GenPolynomialRing<C> ufac) {
        if (Gl == null || Gl.size() == 0) {
            throw new IllegalArgumentException("G may not be null or empty");
        }
        //Polynomial ring of input Groebner basis Gl
        GenPolynomialRing<C> ring = Gl.get(0).ring;
        if (debug) {
            logger.info("ring = " + ring.toScript());
        }
        //Polynomial ring of newGB with INVLEX / target term order
        //TermOrder lexi = ufac.tord; //new TermOrder(TermOrder.INVLEX);
        logger.info("G walk from ev1 = " + ring.tord.toScript() + " to ev2 = " + ufac.tord.toScript());
        List<GenPolynomial<C>> Giter = Gl;
        // determine initial marks
        List<ExpVector> marks = new ArrayList<ExpVector>();
        List<Monomial<C>> M = new ArrayList<Monomial<C>>();
        for (GenPolynomial<C> p : Giter) {
            marks.add(p.leadingExpVector());
            M.add(new Monomial<C>(p.leadingMonomial()));
        }
        logger.info("marks = " + marks);
        List<Monomial<C>> Mp = new ArrayList<Monomial<C>>(M);
        // weight matrix for target term order
        long[][] ufweight = TermOrderByName.weightForOrder(ufac.tord, ring.nvar);
        //TermOrder word = TermOrder.reverseWeight(ufweight);
        TermOrder word = new TermOrder(ufweight);
        logger.info("weight order: " + word);
        ufweight = word.getWeight(); // because of weightDeg usage

        // loop throught term orders
        ExpVector w = null;
        int iter = 0; // count #loops
        boolean done = false;
        while (!done) {
            iter++;
            // determine V and w
            PolynomialList<C> Pl = new PolynomialList<C>(ring, Giter);
            SortedSet<ExpVector> delta = Pl.deltaExpVectors(marks);
            //logger.info("delta(marks) = " + delta);
            logger.info("w_old = " + w);
            ExpVector v = facetNormal(ring.tord, ufac.tord, delta, ring.evzero, ufweight);
            logger.info("minimal v = " + v);
            if (v == null) {
                done = true;
                break; // finished
            }
            w = v;
            // determine facet polynomials for w
            List<GenPolynomial<C>> iG = new ArrayList<GenPolynomial<C>>();
            int i = 0;
            for (GenPolynomial<C> f : Giter) {
                ExpVector h = marks.get(i++);
                GenPolynomial<C> ing = f.leadingFacetPolynomial(h, w);
                if (debug) {
                    logger.info("ing_g = [" + ing + "], lt(ing) = "
                            + ing.ring.toScript(ing.leadingExpVector()) + ", f = "
                            + f.ring.toScript(f.leadingExpVector()));
                }
                iG.add(ing);
            }
            List<GenPolynomial<C>> inOmega = ufac.copy(iG);
            if (debug) {
                logger.info("inOmega = " + inOmega);
                logger.info("inOmega.ring: " + inOmega.get(0).ring.toScript());
            }

            // INVLEX / target term order GB of inOmega
            List<GenPolynomial<C>> inOG = sgb.GB(modv, inOmega);
            if (debug) {
                logger.info("GB(inOmega) = " + inOG);
            }
            // remark polynomials 
            marks.clear();
            M.clear();
            for (GenPolynomial<C> p : inOG) {
                marks.add(p.leadingExpVector());
                M.add(new Monomial<C>(p.leadingMonomial()));
            }
            logger.info("new marks/M = " + marks);
            // lift and reduce
            List<GenPolynomial<C>> G = liftReductas(M, Mp, Giter, inOG);
            if (debug || (iter % iterPrint == 0)) {
                logger.info("lift(" + iter + ") inOG, new GB: " + G);
            }
            if (G.size() == 1) { // will not happen
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
     * Determine new facet normal.
     *
     * @param t1       old term order.
     * @param t2       new term order.
     * @param delta    exponent vectors deltas.
     * @param zero     exponent vector.
     * @param t2weight weight representation of t2.
     * @return new facet normal v or null if no new facet normal exists.
     */
    public ExpVector facetNormal(TermOrder t1, TermOrder t2, Set<ExpVector> delta, ExpVector zero,
                                 long[][] t2weight) {
        TermOrder.EVComparator ev1 = t1.getAscendComparator();
        TermOrder.EVComparator ev2 = t2.getAscendComparator();
        ExpVector v = null;
        long d = 0; // = Long.MIN_VALUE;
        for (ExpVector e : delta) {
            if (ev1.compare(zero, e) >= 0 || ev2.compare(zero, e) <= 0) { //ring.evzero
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
            for (long[] tau : t2weight) {
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
            //logger.info("step s  = " + s + ", d = " + d + ", d2 = " + d2 + ", e = " + e); 
            //   + ", v = " + v + ", et = " + et + ", vt = " + vt);
        }
        return v;
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
     *
     * @param M  new leading monomial list of polynomials as marks.
     * @param Mp old leading monomial list of polynomials as marks.
     * @param G  Groebner base polynomials for lift.
     * @param A  polynomial list of leading omega polynomials to lift.
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
        // remove mark monomials
        List<GenPolynomial<C>> Gp = new ArrayList<GenPolynomial<C>>(G.size());
        int i = 0;
        int len = G.size();
        for (i = 0; i < len; i++) {
            Monomial<C> mon = Mp.get(i);
            GenPolynomial<C> s = G.get(i).subtract(mon);
            Gp.add(s);
        }
        if (debug) {
            logger.info("lifter GB: Gp  = " + Gp + ", Mp = " + Mp);
        }
        // compute f^Gp for f in A
        //GenPolynomialRing<C> oring = pol.ring;
        logger.info("liftReductas: G = " + G.size() + ", Mp = " + Mp.size()); // + ", old = " + oring.toScript());
        List<GenPolynomial<C>> Ap = A; //oring.copy(A);
        //logger.info("to lift Ap = " + Ap);
        ReductionAbstract<C> sred = (ReductionAbstract<C>) sgb.red; //new ReductionSeq<C>();
        List<GenPolynomial<C>> red = new ArrayList<GenPolynomial<C>>();
        GenPolynomialRing<C> tring = A.get(0).ring;
        len = Ap.size();
        for (i = 0; i < len; i++) {
            GenPolynomial<C> a = Ap.get(i);
            GenPolynomial<C> r = sred.normalformMarked(Mp, Gp, a);
            red.add(r);
        }
        logger.info("liftReductas: red(A) = " + red.size());
        // combine f - f^Gp in tring
        if (debug) {
            logger.info("tring = " + tring.toScript()); // + ", M = " + M);
        }
        List<GenPolynomial<C>> nb = new ArrayList<GenPolynomial<C>>(red.size());
        for (i = 0; i < A.size(); i++) {
            GenPolynomial<C> x = tring.copy(A.get(i)); // Ap? A!
            GenPolynomial<C> r = tring.copy(red.get(i));
            GenPolynomial<C> s = x.subtract(r);
            Monomial<C> m = M.get(i);
            s.doAddTo(m.coefficient().negate(), m.exponent()); // remove marked term
            if (!s.coefficient(m.exponent()).isZERO()) {
                System.out.println("L-M: x = " + x + ", r = " + r);
                throw new IllegalArgumentException("mark not removed: " + s + ", m = " + m);
            }
            nb.add(s);
        }
        if (debug) {
            logger.info("lifted-M, nb = " + nb.size());
        }
        // minimal GB with preserved marks
        //Collections.reverse(nb); // important for lex GB
        len = nb.size();
        i = 0;
        while (i < len) {
            GenPolynomial<C> a = nb.remove(0);
            Monomial<C> m = M.remove(0); // in step with element from nb
            if (debug) {
                logger.info("doing " + a + ", lt = " + tring.toScript(m.exponent()));
            }
            //a = sgb.red.normalform(nb, a);
            a = sred.normalformMarked(M, nb, a);
            if (debug) {
                logger.info("done, a = " + a + ", lt = " + tring.toScript(a.leadingExpVector()));
            }
            nb.add(a); // adds as last
            M.add(m);
            i++;
        }
        // re-add mark after minimal
        for (i = 0; i < len; i++) {
            GenPolynomial<C> a = nb.get(i);
            Monomial<C> m = M.get(i);
            a.doAddTo(m.coefficient(), m.exponent()); // re-add marked term
            nb.set(i, a);
        }
        logger.info("liftReductas: nb = " + nb.size() + ", M = " + M.size());
        //Collections.reverse(nb); // undo reverse
        return nb;
    }

}
