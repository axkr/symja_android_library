/*
 * $Id$
 */

package edu.jas.gb;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Groebner Base Arri signature based sequential iterative algorithm. Implements
 * Groebner bases.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */

public class GroebnerBaseArriSigSeqIter<C extends RingElem<C>> extends GroebnerBaseSigSeqIter<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseArriSigSeqIter.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public GroebnerBaseArriSigSeqIter() {
        this(new SigReductionSeq<C>());
    }


    /**
     * Constructor.
     *
     * @param red Reduction engine
     */
    public GroebnerBaseArriSigSeqIter(SigReductionSeq<C> red) {
        super(red);
    }


    /**
     * S-Polynomial.
     *
     * @param P pair.
     * @return spol(A, B) the S-polynomial of the pair (A,B).
     */
    @Override
    GenPolynomial<C> SPolynomial(SigPair<C> P) {
        return P.pi.poly;
    }


    /**
     * Pair with signature.
     *
     * @param A polynomial with signature.
     * @param B polynomial with signature.
     * @param G polynomial ith signature list.
     * @return signature pair according to algorithm.
     */
    @Override
    SigPair<C> newPair(SigPoly<C> A, SigPoly<C> B, List<SigPoly<C>> G) {
        ExpVector e = A.poly.leadingExpVector().lcm(B.poly.leadingExpVector())
                .subtract(A.poly.leadingExpVector());
        GenPolynomial<C> sp = SPolynomial(A, B);
        GenPolynomial<C> sig = sp.ring.valueOf(e);
        return new SigPair<C>(sig, new SigPoly<C>(sig, sp), B, G);
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
    @Override
    SigPair<C> newPair(GenPolynomial<C> s, SigPoly<C> A, SigPoly<C> B, List<SigPoly<C>> G) {
        GenPolynomial<C> sp = SPolynomial(A, B);
        return new SigPair<C>(s, new SigPoly<C>(s, sp), B, G);
    }


    /**
     * Top normalform.
     *
     * @param A polynomial.
     * @param F polynomial list.
     * @param G polynomial list.
     * @return nf(A) with respect to F and G.
     */
    @Override
    SigPoly<C> sigNormalform(List<GenPolynomial<C>> F, List<SigPoly<C>> G, SigPoly<C> A) {
        return sred.sigSemiNormalform(F, G, A);
    }


    /**
     * Prune total pair list P.
     *
     * @param P   pair list.
     * @param syz list of exponent vectors representing syzygies.
     * @return updated pair list.
     */
    @Override
    List<SigPair<C>> pruneP(List<SigPair<C>> P, List<ExpVector> syz) {
        List<SigPair<C>> res = new ArrayList<SigPair<C>>(P.size());
        for (SigPair<C> p : P) {
            ExpVector f = p.sigma.leadingExpVector();
            if (f == null) {
                continue;
            }
            boolean div = false;
            for (ExpVector e : syz) {
                if (f.multipleOf(e)) {
                    div = true;
                    break;
                }
            }
            if (div) {
                continue;
            }
            res.add(p);
        }
        return res;
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
    @Override
    List<SigPair<C>> pruneS(List<SigPair<C>> S, List<ExpVector> syz, List<SigPoly<C>> done, List<SigPoly<C>> G) {
        List<SigPair<C>> res = new ArrayList<SigPair<C>>(S.size());
        for (SigPair<C> p : S) {
            ExpVector f = p.sigma.leadingExpVector();
            if (f == null) {
                continue;
            }
            boolean div = false;
            for (ExpVector e : syz) {
                if (f.multipleOf(e)) {
                    div = true;
                    break;
                }
            }
            if (div) {
                continue;
            }
            div = false;
            for (SigPair<C> q : S) {
                if (p.sigma.equals(q.sigma)) {
                    if (p.pi.poly.compareTo(q.pi.poly) > 0) {
                        div = true;
                        break;
                    }
                }
            }
            if (div) {
                continue;
            }
            div = false;
            for (SigPoly<C> q : done) {
                ExpVector e = q.sigma.leadingExpVector();
                if (e == null) {
                    continue;
                }
                if (f.multipleOf(e)) {
                    ExpVector g = f.subtract(e);
                    ExpVector f1 = g.sum(q.poly.leadingExpVector());
                    ExpVector e1 = p.pi.poly.leadingExpVector();
                    if (e1 == null) {
                        continue;
                    }
                    if (f1.compareTo(e1) < 0) {
                        div = true;
                        break;
                    }
                }
            }
            if (div) {
                continue;
            }
            res.add(p);
            logger.debug("added p = " + p.sigma);
        }
        return res;
    }


    /**
     * Initializes syzygy list.
     *
     * @param F polynomial list.
     * @param G polynomial with signature list.
     * @return list of exponent vectors representing syzygies.
     */
    @Override
    List<ExpVector> initializeSyz(List<GenPolynomial<C>> F, List<SigPoly<C>> G) {
        List<ExpVector> P = new ArrayList<ExpVector>();
        for (GenPolynomial<C> p : F) {
            if (p.isZERO()) {
                continue;
            }
            P.add(p.leadingExpVector());
        }
        return P;
    }


    /**
     * Update syzygy list.
     *
     * @param syz list of exponent vectors representing syzygies.
     * @param r   polynomial. <b>Note:</b> szy is modified to represent updated
     *            list of exponent vectors.
     */
    @Override
    void updateSyz(List<ExpVector> syz, SigPoly<C> r) {
        if (r.poly.isZERO() && !r.sigma.isZERO()) {
            syz.add(r.sigma.leadingExpVector());
        }
        return;
    }

}
