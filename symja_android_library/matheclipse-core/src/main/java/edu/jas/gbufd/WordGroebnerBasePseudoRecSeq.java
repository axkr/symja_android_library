/*
 * $Id$
 */

package edu.jas.gbufd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.jas.gb.OrderedWordPairlist;
import edu.jas.gb.WordGroebnerBaseAbstract;
import edu.jas.gb.WordPair;
import edu.jas.gb.WordPairList;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;


/**
 * Non-commutative word Groebner Base sequential algorithm. Implements Groebner
 * bases and GB test. Coefficients can for example be (commutative) multivariate
 * polynomials.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class WordGroebnerBasePseudoRecSeq<C extends GcdRingElem<C>> extends
        WordGroebnerBaseAbstract<GenPolynomial<C>> {


    private static final Logger logger = Logger.getLogger(WordGroebnerBasePseudoRecSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Greatest common divisor engine for coefficient content and primitive
     * parts.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Reduction engine.
     */
    protected final WordPseudoReduction<C> redRec;


    /**
     * Reduction engine.
     */
    protected final WordPseudoReduction<GenPolynomial<C>> red;


    /**
     * Coefficient ring factory.
     */
    //protected final RingFactory<GenPolynomial<C>> cofac;
    protected final GenPolynomialRing<C> cofac;


    /**
     * Constructor.
     *
     * @param rf coefficient ring factory.
     */
    public WordGroebnerBasePseudoRecSeq(RingFactory<GenPolynomial<C>> rf) {
        this(rf, new WordPseudoReductionSeq<GenPolynomial<C>>());
    }


    /**
     * Constructor.
     *
     * @param rf  coefficient ring factory.
     * @param red Reduction engine
     */
    public WordGroebnerBasePseudoRecSeq(RingFactory<GenPolynomial<C>> rf,
                                        WordPseudoReductionSeq<GenPolynomial<C>> red) {
        this(rf, red, new OrderedWordPairlist<GenPolynomial<C>>());
    }


    /**
     * Constructor.
     *
     * @param rf  coefficient ring factory.
     * @param red Reduction engine
     * @param pl  pair selection strategy
     */
    @SuppressWarnings("cast")
    public WordGroebnerBasePseudoRecSeq(RingFactory<GenPolynomial<C>> rf,
                                        WordPseudoReductionSeq<GenPolynomial<C>> red, WordPairList<GenPolynomial<C>> pl) {
        super(red, pl);
        this.red = red;
        redRec = (WordPseudoReduction<C>) (WordPseudoReduction) red;
        cofac = (GenPolynomialRing<C>) rf;
        if (!cofac.isCommutative()) {
            logger.warn("reduction not correct for " + cofac.toScript());
        }
        engine = GCDFactory.<C>getImplementation(cofac.coFac);
        //not used: engine = GCDFactory.<C>getProxy(cofac.coFac);
    }


    /**
     * Word Groebner base using word pairlist class.
     *
     * @param F word polynomial list.
     * @return GB(F) a finite non-commutative Groebner base of F, if it exists.
     */
    @Override
    public List<GenWordPolynomial<GenPolynomial<C>>> GB(List<GenWordPolynomial<GenPolynomial<C>>> F) {
        List<GenWordPolynomial<GenPolynomial<C>>> G = normalizeZerosOnes(F);
        //G = PolyUtil.<C> wordMonic(G);
        G = recursivePrimitivePart(G);
        G = normalizeZerosOnes(G);
        if (G.size() <= 1) {
            return G;
        }
        GenWordPolynomialRing<GenPolynomial<C>> ring = G.get(0).ring;
        if (!ring.coFac.isCommutative()) {
            throw new IllegalArgumentException("coefficient ring not commutative");
        }
        //Collections.sort(G);
        OrderedWordPairlist<GenPolynomial<C>> pairlist = (OrderedWordPairlist<GenPolynomial<C>>) strategy
                .create(ring);
        pairlist.put(G);
        logger.info("start " + pairlist);

        WordPair<GenPolynomial<C>> pair;
        GenWordPolynomial<GenPolynomial<C>> pi;
        GenWordPolynomial<GenPolynomial<C>> pj;
        List<GenWordPolynomial<GenPolynomial<C>>> S;
        GenWordPolynomial<GenPolynomial<C>> H;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            //logger.debug("pair = " + pair);
            if (pair == null) {
                continue;
            }
            pi = pair.pi;
            pj = pair.pj;
            if (debug) {
                logger.info("pi   = " + pi + ", pj = " + pj);
                //logger.info("pj    = " + pj);
            }

            S = red.SPolynomials(pi, pj);
            if (S.isEmpty()) {
                continue;
            }
            for (GenWordPolynomial<GenPolynomial<C>> s : S) {
                if (s.isZERO()) {
                    //pair.setZero();
                    continue;
                }
                if (debug) {
                    logger.info("ht(S) = " + s.leadingWord());
                }
                boolean t = pairlist.criterion3(pair.i, pair.j, s.leadingWord());
                //System.out.println("criterion3(" + pair.i + "," + pair.j + ") = " + t);
                //if ( !t ) {
                //    continue;  
                //}

                H = redRec.normalformRecursive(G, s);
                if (debug) {
                    //logger.info("pair = " + pair); 
                    //logger.info("ht(S) = " + S.monic()); //.leadingWord() );
                    logger.info("ht(H) = " + H.monic()); //.leadingWord() );
                }
                if (H.isZERO()) {
                    //pair.setZero();
                    continue;
                }
                if (!t) {
                    logger.info("criterion3(" + pair.i + "," + pair.j + ") wrong: " + s.leadingWord()
                            + " --> '" + H.leadingWord() + "'");
                }

                //H = H.monic();
                H = recursivePrimitivePart(H);
                H = H.abs();
                if (debug) {
                    logger.info("ht(H) = " + H.leadingWord());
                }
                if (H.isONE()) {
                    G.clear();
                    G.add(H);
                    return G; // since no threads are activated
                }
                if (debug) {
                    logger.info("H = " + H);
                }
                if (H.length() > 0) {
                    //l++;
                    G.add(H);
                    pairlist.put(H);
                }
            }
        }
        //logger.info("#sequential list = " + G.size());
        G = minimalGB(G);
        logger.info("" + pairlist);
        //Collections.sort(G);
        //Collections.reverse(G);
        return G;
    }


    /**
     * Minimal ordered Groebner basis.
     *
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    @Override
    public List<GenWordPolynomial<GenPolynomial<C>>> minimalGB(List<GenWordPolynomial<GenPolynomial<C>>> Gp) {
        if (Gp == null || Gp.size() <= 1) {
            return Gp;
        }
        // remove zero polynomials
        List<GenWordPolynomial<GenPolynomial<C>>> G = new ArrayList<GenWordPolynomial<GenPolynomial<C>>>(
                Gp.size());
        for (GenWordPolynomial<GenPolynomial<C>> a : Gp) {
            if (a != null && !a.isZERO()) { // always true in GB()
                // already positive a = a.abs();
                G.add(a);
            }
        }
        if (G.size() <= 1) {
            return G;
        }
        // remove top reducible polynomials
        GenWordPolynomial<GenPolynomial<C>> a;
        List<GenWordPolynomial<GenPolynomial<C>>> F;
        F = new ArrayList<GenWordPolynomial<GenPolynomial<C>>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                // drop polynomial 
                if (debug) {
                    System.out.println("dropped " + a);
                    List<GenWordPolynomial<GenPolynomial<C>>> ff;
                    ff = new ArrayList<GenWordPolynomial<GenPolynomial<C>>>(G);
                    ff.addAll(F);
                    a = redRec.normalformRecursive(ff, a);
                    if (!a.isZERO()) {
                        System.out.println("error, nf(a) " + a);
                    }
                }
            } else {
                F.add(a);
            }
        }
        G = F;
        if (G.size() <= 1) {
            return G;
        }
        // reduce remaining polynomials
        Collections.reverse(G); // important for lex GB
        int len = G.size();
        if (debug) {
            System.out.println("#G " + len);
            for (GenWordPolynomial<GenPolynomial<C>> aa : G) {
                System.out.println("aa = " + aa.length() + ", lt = " + aa.getMap().keySet());
            }
        }
        int i = 0;
        while (i < len) {
            a = G.remove(0);
            if (debug) {
                System.out.println("doing " + a.length() + ", lt = " + a.leadingWord());
            }
            a = redRec.normalformRecursive(G, a);
            G.add(a); // adds as last
            i++;
        }
        return G;
    }


    /**
     * Wird Groebner base simple test.
     *
     * @param F recursive polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    @Override
    public boolean isGB(List<GenWordPolynomial<GenPolynomial<C>>> F) {
        if (F == null || F.isEmpty()) {
            return true;
        }
        GenWordPolynomial<GenPolynomial<C>> pi, pj, h;
        List<GenWordPolynomial<GenPolynomial<C>>> S;
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            for (int j = 0; j < F.size(); j++) {
                if (i == j) {
                    continue;
                }
                pj = F.get(j);

                S = red.SPolynomials(pi, pj);
                if (S.isEmpty()) {
                    continue;
                }
                for (GenWordPolynomial<GenPolynomial<C>> s : S) {
                    //System.out.println("i, j = " + i + ", " + j); 
                    h = redRec.normalformRecursive(F, s);
                    if (!h.isZERO()) {
                        logger.info("no GB: pi = " + pi + ", pj = " + pj);
                        logger.info("s  = " + s + ", h = " + h);
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * GenWordPolynomial recursive coefficient content.
     *
     * @param P recursive GenWordPolynomial.
     * @return cont(P).
     */
    public GenPolynomial<C> recursiveContent(GenWordPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P.ring.getZEROCoefficient();
        }
        GenPolynomial<C> d = null;
        for (GenPolynomial<C> c : P.getMap().values()) {
            //System.out.println("value c = " + c.leadingExpVector());
            if (d == null) {
                d = c;
            } else {
                d = engine.gcd(d, c);
            }
            if (d.isONE()) {
                return d;
            }
        }
        if (d.signum() < 0) {
            d = d.negate();
        }
        return d;
    }


    /**
     * GenWordPolynomial recursive coefficient primitive part.
     *
     * @param P recursive GenWordPolynomial.
     * @return pp(P).
     */
    public GenWordPolynomial<GenPolynomial<C>> recursivePrimitivePart(GenWordPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomial<C> d = recursiveContent(P);
        if (d.isONE()) {
            return P;
        }
        //GenWordPolynomial<GenPolynomial<C>> pp = P.divide(d);
        GenWordPolynomial<GenPolynomial<C>> pp = PolyUtil.<C>recursiveDivide(P, d);
        if (debug) {
            GenWordPolynomial<GenPolynomial<C>> p = pp.multiply(d);
            if (!p.equals(P)) {
                throw new ArithmeticException("pp(p)*cont(p) != p: ");
            }
        }
        return pp;
    }


    /**
     * List of GenWordPolynomial recursive coefficient primitive part.
     *
     * @param F list of recursive GenWordPolynomials.
     * @return pp(F).
     */
    public List<GenWordPolynomial<GenPolynomial<C>>> recursivePrimitivePart(
            List<GenWordPolynomial<GenPolynomial<C>>> F) {
        if (F == null || F.isEmpty()) {
            return F;
        }
        List<GenWordPolynomial<GenPolynomial<C>>> Pp = new ArrayList<GenWordPolynomial<GenPolynomial<C>>>(
                F.size());
        for (GenWordPolynomial<GenPolynomial<C>> f : F) {
            GenWordPolynomial<GenPolynomial<C>> p = recursivePrimitivePart(f);
            Pp.add(p);
        }
        return Pp;
    }

}
