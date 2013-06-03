/*
 * $Id$
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ListIterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.poly.Word;


/**
 * Non-commutative Groebner Bases abstract class.
 * Implements common Groebner bases and GB test methods.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public abstract class WordGroebnerBaseAbstract<C extends RingElem<C>> 
                      implements WordGroebnerBase<C> {

    private static final Logger logger = Logger.getLogger(WordGroebnerBaseAbstract.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Reduction engine.
     */
    public final WordReduction<C> red;


    /**
     * Strategy for pair selection.
     */
    public final WordPairList<C> strategy;


    /**
     * Constructor.
     */
    public WordGroebnerBaseAbstract() {
        this( new WordReductionSeq<C>() );
    }


    /**
     * Constructor.
     * @param red Word Reduction engine
     */
    public WordGroebnerBaseAbstract(WordReduction<C> red) {
        this(red, new OrderedWordPairlist<C>() );
    }


    /**
     * Constructor.
     * @param red Word Reduction engine
     * @param pl Word pair selection strategy
     */
    public WordGroebnerBaseAbstract(WordReduction<C> red, WordPairList<C> pl) {
        this.red = red;
        this.strategy = pl;
    }


    /**
     * Word Groebner base test.
     * @param F Word polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(List<GenWordPolynomial<C>> F) {  
        if ( F == null || F.isEmpty() ) {
           return true;
        }
        for ( int i = 0; i < F.size(); i++ ) {
            GenWordPolynomial<C> pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                GenWordPolynomial<C> pj = F.get(j);
                List<GenWordPolynomial<C>> S = red.SPolynomials( pi, pj );
                if ( S.isEmpty() ) {
                   continue;
                }
                for ( GenWordPolynomial<C> s : S ) {
                    GenWordPolynomial<C> h = red.normalform( F, s );
                    if ( ! h.isZERO() ) {
                        logger.info("no GB: pi = " + pi + ", pj = " + pj);
                        logger.info("s  = " + s  + ", h = " + h);
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Groebner base using pairlist class.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public abstract List<GenWordPolynomial<C>> GB( List<GenWordPolynomial<C>> F );


    /**
     * Minimal ordered Groebner basis.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    public List<GenWordPolynomial<C>> minimalGB(List<GenWordPolynomial<C>> Gp) {  
        if ( Gp == null || Gp.size() <= 1 ) {
            return Gp;
        }
        // remove zero polynomials
        List<GenWordPolynomial<C>> G
            = new ArrayList<GenWordPolynomial<C>>( Gp.size() );
        for ( GenWordPolynomial<C> a : Gp ) { 
            if ( a != null && !a.isZERO() ) { // always true in GB()
               // already positive a = a.abs();
               G.add( a );
            }
        }
        if ( G.size() <= 1 ) {
           return G;
        }
        // remove top reducible polynomials
        GenWordPolynomial<C> a;
        List<GenWordPolynomial<C>> F;
        F = new ArrayList<GenWordPolynomial<C>>( G.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0);
            if ( red.isTopReducible(G,a) || red.isTopReducible(F,a) ) {
               // drop polynomial 
               if ( debug ) {
                  System.out.println("dropped " + a);
                  List<GenWordPolynomial<C>> ff;
                  ff = new ArrayList<GenWordPolynomial<C>>( G );
                  ff.addAll(F);
                  a = red.normalform( ff, a );
                  if ( !a.isZERO() ) {
                     System.out.println("error, nf(a) " + a);
                  }
               }
            } else {
                F.add(a);
            }
        }
        G = F;
        if ( G.size() <= 1 ) {
           return G;
        }
        // reduce remaining polynomials
        Collections.reverse(G); // important for lex GB
        int len = G.size();
        if ( debug ) {
            System.out.println("#G " + len);
            for (GenWordPolynomial<C> aa : G) {
                System.out.println("aa = " + aa.length() + ", lt = " + aa.getMap().keySet());
            }
        }
        int i = 0;
        while ( i < len ) {
            a = G.remove(0);
            if ( debug ) {
                System.out.println("doing " + a.length() + ", lt = " + a.leadingWord());
            }
            a = red.normalform( G, a );
            G.add( a ); // adds as last
            i++;
        }
        return G;
    }


    /**
     * Test for minimal ordered Groebner basis.
     * @param Gp an ideal base.
     * @return true, if Gp is a reduced minimal Groebner base.
     */
    public boolean isMinimalGB(List<GenWordPolynomial<C>> Gp) {  
        if ( Gp == null || Gp.size() == 0 ) {
            return true;
        }
        // test for zero polynomials
        for ( GenWordPolynomial<C> a : Gp ) { 
            if ( a == null || a.isZERO() ) { 
                if (debug) {
                    logger.debug("zero polynomial " + a);
                }
                return false;
            }
        }
        // test for top reducible polynomials
        List<GenWordPolynomial<C>> G = new ArrayList<GenWordPolynomial<C>>( Gp );
        List<GenWordPolynomial<C>> F = new ArrayList<GenWordPolynomial<C>>( G.size() );
        while ( G.size() > 0 ) {
            GenWordPolynomial<C> a = G.remove(0);
            if ( red.isTopReducible(G,a) || red.isTopReducible(F,a) ) {
                if (debug) {
                    logger.debug("top reducible polynomial " + a);
                }
                return false;
            } else {
                F.add(a);
            }
        }
        G = F;
        if ( G.size() <= 1 ) {
           return true;
        }
        // test reducibility of polynomials
        int len = G.size();
        int i = 0;
        while ( i < len ) {
            GenWordPolynomial<C> a = G.remove(0);
            if ( ! red.isNormalform( G, a ) ) {
                if (debug) {
                    logger.debug("reducible polynomial " + a);
                }
                return false;
            }
            G.add( a ); // re-adds as last
            i++;
        }
        return true;
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    public void terminate() {
        logger.info("terminate not implemented");
    }


    /**
     * Cancel ThreadPool.
     */
    public int cancel() {
        logger.info("cancel not implemented");
        return 0;
    }

}
