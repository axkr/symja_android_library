/*
 * $Id$
 */

package edu.jas.gb;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.OrderedPolynomialList;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;


/**
 * Groebner Base signature based sequential iterative algorithm. Implements
 * Groebner bases after the paper
 * "Signature-based Algorithms to Compute Gröbner Bases" by Christian Eder and
 * John Perry, ISSAC 2011. Compare the jython+JAS code in
 * examples/basic_sigbased_gb.py. Originally the Python+Sage code is from
 * http://www.math.usm.edu/perry/Research/basic_sigbased_gb.py
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 * @see edu.jas.gb.GroebnerBaseGGVSigSeqIter
 * @see edu.jas.gb.GroebnerBaseArriSigSeqIter
 * @see edu.jas.gb.GroebnerBaseF5zSigSeqIter
 */

public class GroebnerBaseSigSeqIter<C extends RingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseSigSeqIter.class);


    private static final boolean debug = logger.isDebugEnabled();


    final SigReductionSeq<C> sred;


    /**
     * Constructor.
     */
    public GroebnerBaseSigSeqIter() {
        this(new SigReductionSeq<C>());
    }


    /**
     * Constructor.
     *
     * @param red Reduction engine
     */
    public GroebnerBaseSigSeqIter(SigReductionSeq<C> red) {
        super();
        sred = red;
    }


    /**
     * Groebner base signature iterative algorithm.
     *
     * @param modv module variable number.
     * @param F    polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(F);
        G = PolyUtil.<C>monic(G);
        if (G.size() <= 1) {
            return G;
        }
        // sort, no reverse
        //  G = OrderedPolynomialList.<C> sort(G);
        G = OrderedPolynomialList.<C>sortDegree(G);
        //no: Collections.reverse(G);
        logger.info("G-sort = " + G);
        List<GenPolynomial<C>> Gp = new ArrayList<GenPolynomial<C>>();
        for (GenPolynomial<C> p : G) {
            if (debug) {
                logger.info("p = " + p);
            }
            GenPolynomial<C> pp = red.normalform(Gp, p);
            if (pp.isZERO()) {
                continue;
            }
            Gp = GB(modv, Gp, p);
            if (Gp.size() > 0) {
                if (Gp.get(0).isONE()) {
                    return Gp;
                }
            }
        }
        return Gp;
    }


    /**
     * Groebner base iterated.
     *
     * @param modv module variable number.
     * @param G    polynomial list of a Groebner base.
     * @param f    polynomial.
     * @return GB(G, f) a Groebner base of G+(f).
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> G, GenPolynomial<C> f) {
        List<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>(G);
        GenPolynomial<C> g = f.monic();
        if (F.isEmpty()) {
            F.add(g);
            return F; // commutative
        }
        if (g.isZERO()) {
            return F;
        }
        if (g.isONE()) {
            F.clear();
            F.add(g);
            return F;
        }
        GenPolynomialRing<C> ring = F.get(0).ring;
        if (!ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        if (modv != 0) {
            throw new UnsupportedOperationException("motv != 0 not implemented");
        }
        // add signatures
        List<SigPoly<C>> Gs = new ArrayList<SigPoly<C>>();
        for (GenPolynomial<C> p : F) {
            Gs.add(new SigPoly<C>(ring.getZERO(), p));
        }
        SigPoly<C> gs = new SigPoly<C>(ring.getONE(), g);
        Gs.add(gs);
        //logger.info("Gs = " + Gs);
        // construct critical pair list
        List<SigPair<C>> pairlist = new ArrayList<SigPair<C>>();
        for (SigPoly<C> p : Gs) {
            if (p.equals(gs)) {
                continue;
            }
            pairlist.add(newPair(gs, p, Gs));
        }
        logger.info("start " + pairlist.size());

        List<ExpVector> syz = initializeSyz(F, Gs);
        List<SigPoly<C>> done = new ArrayList<SigPoly<C>>();

        SigPair<C> pair;
        //SigPoly<C> pi, pj;
        GenPolynomial<C> S, H, sigma;
        while (!pairlist.isEmpty()) {
            pairlist = pruneP(pairlist, syz);
            if (pairlist.isEmpty()) {
                continue;
            }
            List<SigPair<C>>[] spl = sred.minDegSubset(pairlist);
            List<SigPair<C>> Sl = spl[0];
            long mdeg = sred.minimalSigDegree(Sl);
            pairlist = spl[1];
            logger.info("treating " + Sl.size() + " signatures of degree " + mdeg);
            //logger.info("Sl(" + mdeg + ") = " + Sl);
            while (!Sl.isEmpty()) {
                //logger.info("Sl_full = " + sred.sigmas(Sl));
                Sl = pruneS(Sl, syz, done, Gs);
                if (Sl.isEmpty()) {
                    continue;
                }
                Sl = sred.sortSigma(Sl);
                //logger.info("Sl_sort = " + Sl);
                pair = Sl.remove(0);
                if (pair == null) {
                    continue;
                }
                //logger.info("pair.full = " + pair);
                S = SPolynomial(pair);
                SigPoly<C> Ss = new SigPoly<C>(pair.sigma, S);
                if (S.isZERO()) {
                    updateSyz(syz, Ss);
                    done.add(Ss);
                    continue;
                }
                if (debug) {
                    logger.debug("ht(S) = " + S.leadingExpVector());
                }

                SigPoly<C> Hs = sigNormalform(F, Gs, Ss);
                H = Hs.poly;
                sigma = Hs.sigma;
                if (debug) {
                    logger.info("new polynomial = " + Hs); //.leadingExpVector() );
                }
                if (H.isZERO()) {
                    updateSyz(syz, Hs);
                    done.add(Hs);
                    continue;
                }
                H = H.monic();
                if (debug) {
                    logger.info("ht(H) = " + H.leadingExpVector());
                }

                if (H.isONE()) {
                    G.clear();
                    G.add(H);
                    logger.info("end " + pairlist);
                    return G; // since no threads are activated
                }
                if (sred.isSigRedundant(Gs, Hs)) {
                    continue;
                }
                if (debug) {
                    logger.info("new polynomial = " + Hs);
                }
                if (H.length() > 0) {
                    for (SigPoly<C> p : Gs) {
                        if (p.poly.isZERO()) {
                            continue;
                        }
                        GenPolynomial<C> tau = p.sigma;
                        GenPolynomial<C>[] mult = SPolynomialFactors(Hs, p);
                        //System.out.print("sigma = " + sigma + ", tau = " + tau);
                        //System.out.println(", mult  = " + Arrays.toString(mult));
                        ExpVector se = sigma.leadingExpVector();
                        ExpVector te = tau.leadingExpVector();
                        if (mult[0].multiply(se).equals(mult[1].multiply(te))) {
                            //logger.info("skip by sigma");
                            continue;
                        }
                        SigPair<C> pp;
                        //boolean xy = mult[0].multiply(se).compareTo(mult[1].multiply(te)) > 0;
                        if (mult[0].multiply(se).compareTo(mult[1].multiply(te)) > 0) {
                            pp = newPair(sigma.multiply(mult[0]), Hs, p, Gs);
                        } else {
                            pp = newPair(tau.multiply(mult[1]), p, Hs, Gs);
                        }
                        //System.out.println("new_pair " + pp.sigma + ", xy = " + xy + ", sigma = " + sigma + ", tau = " + tau + ", mult  = " + Arrays.toString(mult) + ", m0*se = " + mult[0].multiply(se) + ", m1*te = " + mult[1].multiply(te) );
                        if (pp.sigma.degree() == mdeg) { // mdeg is sigma.degree()
                            Sl.add(pp); // do not check contains
                        } else {
                            pairlist.add(pp); // do not check contains
                        }
                    }
                    Gs.add(Hs);
                    done.add(Hs);
                }
            }
        }
        logger.info("#sequential list before reduction = " + Gs.size());
        List<GenPolynomial<C>> Gp = sred.polys(Gs);
        //logger.info("G_full = " + Gp);
        G = minimalGB(Gp);
        //G = red.irreducibleSet(Gp);
        //G = OrderedPolynomialList.<C> sortDegree(G);
        //logger.info("G_reduced = " + G);
        logger.info("end " + pairlist);
        return G;
    }


    /**
     * S-Polynomial.
     *
     * @param A monic polynomial.
     * @param B monic polynomial.
     * @return spol(A, B) the S-polynomial of the A and B.
     */
    GenPolynomial<C> SPolynomial(SigPoly<C> A, SigPoly<C> B) {
        return sred.SPolynomial(A, B);
    }


    /**
     * S-Polynomial.
     *
     * @param P pair.
     * @return spol(A, B) the S-polynomial of the pair (A,B).
     */
    GenPolynomial<C> SPolynomial(SigPair<C> P) {
        return sred.SPolynomial(P.pi, P.pj);
    }


    /**
     * S-Polynomial polynomial factors.
     *
     * @param A monic polynomial.
     * @param B monic polynomial.
     * @return polynomials [e,f] such that spol(A,B) = e*a - f*B.
     */
    GenPolynomial<C>[] SPolynomialFactors(SigPoly<C> A, SigPoly<C> B) {
        return sred.SPolynomialFactors(A, B);
    }


    /**
     * Pair with signature.
     *
     * @param A polynomial with signature.
     * @param B polynomial with signature.
     * @param G polynomial ith signature list.
     * @return signature pair according to algorithm.
     */
    SigPair<C> newPair(SigPoly<C> A, SigPoly<C> B, List<SigPoly<C>> G) {
        ExpVector e = A.poly.leadingExpVector().lcm(B.poly.leadingExpVector())
                .subtract(A.poly.leadingExpVector());
        return new SigPair<C>(e, A, B, G);
    }


    /**
     * Pair with signature.
     *
     * @param s signature for pair.
     * @param A polynomial with signature.
     * @param B polynomial with signature.
     * @param G polynomial ith signature list.
     * @return signature pair according to algorithm.
     */
    SigPair<C> newPair(GenPolynomial<C> s, SigPoly<C> A, SigPoly<C> B, List<SigPoly<C>> G) {
        return new SigPair<C>(s, A, B, G);
    }


    /**
     * Top normalform.
     *
     * @param A polynomial.
     * @param F polynomial list.
     * @param G polynomial list.
     * @return nf(A) with respect to F and G.
     */
    SigPoly<C> sigNormalform(List<GenPolynomial<C>> F, List<SigPoly<C>> G, SigPoly<C> A) {
        return sred.sigNormalform(F, G, A);
    }


    /**
     * Prune total pair list P.
     *
     * @param P   pair list.
     * @param syz list of exponent vectors representing syzygies.
     * @return updated pair list.
     */
    List<SigPair<C>> pruneP(List<SigPair<C>> P, List<ExpVector> syz) {
        if (debug) {
            logger.debug("unused " + syz);
        }
        return P;
    }


    /**
     * Prune pair list of degree d.
     *
     * @param S    pair list.
     * @param syz  list of exponent vectors representing syzygies.
     * @param done list of treated polynomials.
     * @param G    polynomial with signature list.
     * @return updated pair list.
     */
    List<SigPair<C>> pruneS(List<SigPair<C>> S, List<ExpVector> syz, List<SigPoly<C>> done,
                            List<SigPoly<C>> G) {
        if (debug) {
            logger.debug("unused " + syz + " " + done + " " + G);
        }
        return S;
    }


    /**
     * Initializes syzygy list.
     *
     * @param F polynomial list.
     * @param G polynomial with signature list.
     * @return list of exponent vectors representing syzygies.
     */
    List<ExpVector> initializeSyz(List<GenPolynomial<C>> F, List<SigPoly<C>> G) {
        if (debug) {
            logger.debug("unused " + G + " " + F);
        }
        List<ExpVector> P = new ArrayList<ExpVector>();
        return P;
    }


    /**
     * Update syzygy list.
     *
     * @param syz list of exponent vectors representing syzygies.
     * @param r   polynomial. <b>Note:</b> szy is modified to represent updated
     *            list of exponent vectors.
     */
    void updateSyz(List<ExpVector> syz, SigPoly<C> r) {
        if (debug) {
            logger.debug("unused " + syz + " " + r);
        }
        return;
    }

}
