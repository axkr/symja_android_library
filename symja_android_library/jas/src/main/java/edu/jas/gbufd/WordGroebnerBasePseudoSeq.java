/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.gb.OrderedWordPairlist;
import edu.jas.gb.WordGroebnerBaseAbstract;
import edu.jas.gb.WordPair;
import edu.jas.gb.WordPairList;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Non-commutative word Groebner Base sequential algorithm. Implements Groebner
 * bases and GB test. Coefficients can for example be integers or (commutative)
 * univariate polynomials.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class WordGroebnerBasePseudoSeq<C extends GcdRingElem<C>> extends WordGroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(WordGroebnerBasePseudoSeq.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Coefficient ring factory.
     */
    protected final RingFactory<C> cofac;


    /**
     * Constructor.
     * @param rf coefficient ring factory.
     */
    public WordGroebnerBasePseudoSeq(RingFactory<C> rf) {
        this(rf, new WordPseudoReductionSeq<C>());
    }


    /**
     * Constructor.
     * @param rf coefficient ring factory.
     * @param red Reduction engine
     */
    public WordGroebnerBasePseudoSeq(RingFactory<C> rf, WordPseudoReductionSeq<C> red) {
        this(rf, red, new OrderedWordPairlist<C>());
    }


    /**
     * Constructor.
     * @param rf coefficient ring factory.
     * @param red Reduction engine
     * @param pl pair selection strategy
     */
    public WordGroebnerBasePseudoSeq(RingFactory<C> rf, WordPseudoReductionSeq<C> red, WordPairList<C> pl) {
        super(red, pl);
        cofac = rf;
        if (!cofac.isCommutative()) {
            logger.warn("reduction not correct for " + cofac.toScript());
        }
        //engine = GCDFactory.<C> getImplementation(rf);
        //not used: engine = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getProxy( rf );
    }


    /**
     * Word Groebner base using word pairlist class.
     * @param F word polynomial list.
     * @return GB(F) a finite non-commutative Groebner base of F, if it exists.
     */
    @Override
    public List<GenWordPolynomial<C>> GB(List<GenWordPolynomial<C>> F) {
        List<GenWordPolynomial<C>> G = normalizeZerosOnes(F);
        //G = PolyUtil.<C> wordMonic(G);
        G = basePrimitivePart(G);
        if (G.size() <= 1) {
            return G;
        }
        GenWordPolynomialRing<C> ring = G.get(0).ring;
        if (!ring.coFac.isCommutative()) {
            throw new IllegalArgumentException("coefficient ring not commutative");
        }
        //Collections.sort(G);
        OrderedWordPairlist<C> pairlist = (OrderedWordPairlist<C>) strategy.create(ring);
        pairlist.put(G);
        logger.info("start " + pairlist);

        WordPair<C> pair;
        GenWordPolynomial<C> pi;
        GenWordPolynomial<C> pj;
        List<GenWordPolynomial<C>> S;
        GenWordPolynomial<C> H;
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
            for (GenWordPolynomial<C> s : S) {
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

                H = red.normalform(G, s);
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
                                    + " --> " + H.leadingWord());
                }

                //H = H.monic();
                H = basePrimitivePart(H);
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
     * GenWordPolynomial base coefficient content.
     * @param P GenWordPolynomial.
     * @return cont(P).
     */
    public C baseContent(GenWordPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P.ring.getZEROCoefficient();
        }
        C d = null;
        for (C c : P.getMap().values()) {
            if (d == null) {
                d = c;
            } else {
                d = d.gcd(c);
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
     * GenWordPolynomial base coefficient primitive part.
     * @param P GenWordPolynomial.
     * @return pp(P).
     */
    public GenWordPolynomial<C> basePrimitivePart(GenWordPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        C d = baseContent(P);
        if (d.isONE()) {
            return P;
        }
        GenWordPolynomial<C> pp = P.divide(d);
        if (debug) {
            GenWordPolynomial<C> p = pp.multiply(d);
            if (!p.equals(P)) {
                throw new ArithmeticException("pp(p)*cont(p) != p: ");
            }
        }
        return pp;
    }


    /**
     * List of GenWordPolynomial base coefficient primitive part.
     * @param F list of GenWordPolynomials.
     * @return pp(F).
     */
    public List<GenWordPolynomial<C>> basePrimitivePart(List<GenWordPolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return F;
        }
        List<GenWordPolynomial<C>> Pp = new ArrayList<GenWordPolynomial<C>>(F.size());
        for (GenWordPolynomial<C> f : F) {
            GenWordPolynomial<C> p = basePrimitivePart(f);
            Pp.add(p);
        }
        return Pp;
    }

}
