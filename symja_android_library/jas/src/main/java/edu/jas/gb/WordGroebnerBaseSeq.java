/*
 * $Id$
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.poly.Word;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;


/**
 * Word Groebner Base sequential algorithm.
 * Implements Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class WordGroebnerBaseSeq<C extends RingElem<C>> 
       extends WordGroebnerBaseAbstract<C>  {

    private static final Logger logger = Logger.getLogger(WordGroebnerBaseSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public WordGroebnerBaseSeq() {
        super();
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public WordGroebnerBaseSeq(WordReduction<C> red) {
        super(red);
    }


    /**
     * Constructor.
     * @param red Reduction engine
     * @param pl pair selection strategy
     */
    public WordGroebnerBaseSeq(WordReduction<C> red, WordPairList<C> pl) {
        super(red,pl);
    }


    /**
     * Word Groebner base using word pairlist class.
     * @param F word polynomial list.
     * @return GB(F) a finite non-commutative Groebner base of F, if it exists.
     */
    public List<GenWordPolynomial<C>> GB( List<GenWordPolynomial<C>> F ) {  
        GenWordPolynomial<C> p;
        List<GenWordPolynomial<C>> G = new ArrayList<GenWordPolynomial<C>>();
        WordPairList<C> pairlist = null; 
        int l = F.size();
        ListIterator<GenWordPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               p = p.monic();
               if ( p.isONE() ) {
                  G.clear(); G.add( p );
                  return G; // since no threads are activated
               }
               G.add( p );
               if ( pairlist == null ) {
                   //pairlist = new OrderedPairlist<C>(p.ring );
                  pairlist = strategy.create( p.ring );
                  if ( ! p.ring.coFac.isField() ) {
                     throw new IllegalArgumentException("coefficients not from a field");
                  }
               }
               // putOne not required
               pairlist.put( p );
            } else { 
               l--;
            }
        }
        if ( l <= 1 ) {
           return G; // since no threads are activated
        }
        logger.info("start " + pairlist); 

        WordPair<C> pair;
        GenWordPolynomial<C> pi;
        GenWordPolynomial<C> pj;
        List<GenWordPolynomial<C>> S;
        GenWordPolynomial<C> H;
        while ( pairlist.hasNext() ) {
              pair = pairlist.removeNext();
              //logger.debug("pair = " + pair);
              if ( pair == null ) {
                  continue; 
              }
              pi = pair.pi; 
              pj = pair.pj; 
              if ( /*false &&*/ debug ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
              }

              S = red.SPolynomials( pi, pj );
              if ( S.isEmpty() ) {
                 continue;
              }
              for ( GenWordPolynomial<C> s : S ) {
                  if ( s.isZERO() ) {
                      //pair.setZero();
                      continue;
                  }
                  if ( debug ) {
                      logger.debug("ht(S) = " + s.leadingWord() );
                  }

                  H = red.normalform( G, s );
                  if ( debug ) {
                      //logger.info("pair = " + pair); 
                      //logger.info("ht(S) = " + S.monic()); //.leadingWord() );
                      logger.info("ht(H) = " + H.monic()); //.leadingWord() );
                  }
                  if ( H.isZERO() ) {
                      //pair.setZero();
                      continue;
                  }
                  H = H.monic();
                  if ( debug ) {
                      logger.info("ht(H) = " + H.leadingWord() );
                  }
                  if ( H.isONE() ) {
                      G.clear(); G.add( H );
                      return G; // since no threads are activated
                  }
                  if ( debug ) {
                      logger.info("H = " + H );
                  }
                  if ( H.length() > 0 ) {
                      l++;
                      G.add( H );
                      pairlist.put( H );
                  }
              }
        }
        logger.debug("#sequential list = " + G.size());
        G = minimalGB(G);
        logger.info("" + pairlist); 
        return G;
    }

}
