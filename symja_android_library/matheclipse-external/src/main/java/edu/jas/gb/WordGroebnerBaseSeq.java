/*
 * $Id$
 */

package edu.jas.gb;


import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.kern.LocalTimeStatus;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;


/**
 * Non-commutative word Groebner Base sequential algorithm. Implements
 * Groebner bases and GB test. Run-time for GB computation is limited
 * to 20 seconds. To change this limit use
 * <pre>
 *   wbb.timestatus.setLimit(newLimit);
 * </pre>
 * or decativate it for infinite running time
 * <pre>
 *   wbb.timestatus.setNotActive();
 * </pre>
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class WordGroebnerBaseSeq<C extends RingElem<C>> extends WordGroebnerBaseAbstract<C> {


    private static final Logger logger = LogManager.getLogger(WordGroebnerBaseSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    public final LocalTimeStatus timestatus;


    /**
     * Constructor.
     */
    public WordGroebnerBaseSeq() {
        super();
        timestatus = new LocalTimeStatus(true, 20*1000, false);
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public WordGroebnerBaseSeq(WordReduction<C> red) {
        super(red);
        timestatus = new LocalTimeStatus(true, 20*1000, false);
    }


    /**
     * Constructor.
     * @param red Reduction engine
     * @param pl pair selection strategy
     */
    public WordGroebnerBaseSeq(WordReduction<C> red, WordPairList<C> pl) {
        super(red, pl);
        timestatus = new LocalTimeStatus(true, 20*1000, false);
    }


    /**
     * Word Groebner base using word pairlist class.
     * @param F word polynomial list.
     * @return GB(F) a finite non-commutative Groebner base of F, if it exists.
     */
    @Override
    public List<GenWordPolynomial<C>> GB(List<GenWordPolynomial<C>> F) {
        List<GenWordPolynomial<C>> G = normalizeZerosOnes(F);
        G = PolyUtil.<C> wordMonic(G);
        if (G.size() <= 1) {
            return G;
        }
        GenWordPolynomialRing<C> ring = G.get(0).ring;
        if (!ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        //Collections.sort(G);
        OrderedWordPairlist<C> pairlist = (OrderedWordPairlist<C>) strategy.create(ring);
        pairlist.put(G);
        logger.info("start {}", pairlist);
        timestatus.restart();
        //if (timestatus.isActive()) {
        //    throw new RuntimeException("timestatus: " + timestatus);
        //}

        WordPair<C> pair;
        GenWordPolynomial<C> pi;
        GenWordPolynomial<C> pj;
        List<GenWordPolynomial<C>> S;
        GenWordPolynomial<C> H;
        int infin = 0;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            //logger.debug("pair = {}", pair);
            if (pair == null) {
                continue;
            }
            pi = pair.pi;
            pj = pair.pj;
            if (debug) {
                logger.info("pi   = {}, pj = {}", pi, pj);
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
                    logger.info("ht(S) = {}", s.leadingWord());
                }
                boolean t = pairlist.criterion3(pair.i, pair.j, s.leadingWord());
                //System.out.println("criterion3(" + pair.i + "," + pair.j + ") = " + t);
                //if ( !t ) {
                //    continue;  
                //}

                H = red.normalform(G, s);
                if (debug) {
                    //logger.info("pair = {}", pair);
                    //logger.info("ht(S) = {}", S.monic()); //.leadingWord() );
                    logger.info("ht(H) = {}", H.monic()); //.leadingWord() );
                }
                if (H.isZERO()) {
                    //pair.setZero();
                    continue;
                }
                if (!t) {
                    logger.info("criterion3({},{}) wrong: {} --> {}", pair.i, pair.j, s.leadingWord(), H.leadingWord());
                }

                H = H.monic();
                if (debug) {
                    logger.info("ht(H) = {}", H.leadingWord());
                }
                if (H.isONE()) {
                    G.clear();
                    G.add(H);
                    return G; // since no threads are activated
                }
                if (debug) {
                    logger.info("H = {}", H);
                }
                if (H.length() > 0) {
                    //l++;
                    G.add(H);
                    pairlist.put(H);
                }
                if (H.degree() > 9) {
                    //System.out.println("deg(H): " + H.degree());
                    //logger.warn("word GB too high degree {}", H.degree());
                    timestatus.checkTime("word GB degree > 9: " + H.degree());
                }

                if (s.leadingWord().dependencyOnVariables().equals(H.leadingWord().dependencyOnVariables())) {
                    logger.info("LT depend: {} --> {}", s.leadingWord().dependencyOnVariables(), H.leadingWord().dependencyOnVariables());
                    logger.info("LT depend: {} --> {}", s, H);
                    infin++;
                    if (infin > 500) {
                        //System.out.println("deg(H): " + H.degree());
                        //throw new RuntimeException("no convergence in word GB: " + infin);
                        timestatus.checkTime("no convergence in word GB: > 500: " + infin);
                    }
                }
            }
        }
        //logger.info("#sequential list = {}", G.size());
        G = minimalGB(G);
        logger.info("end   {}", pairlist);
        //Collections.sort(G);
        //Collections.reverse(G);
        return G;
    }

}
