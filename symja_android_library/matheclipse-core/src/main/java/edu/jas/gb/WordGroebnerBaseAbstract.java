/*
 * $Id$
 */

package edu.jas.gb;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.Word;
import edu.jas.structure.RingElem;


/**
 * Non-commutative Groebner Bases abstract class. Implements common Groebner
 * bases and GB test methods.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public abstract class WordGroebnerBaseAbstract<C extends RingElem<C>> implements WordGroebnerBase<C> {


    private static final Logger logger = Logger.getLogger(WordGroebnerBaseAbstract.class);


    private static final boolean debug = logger.isDebugEnabled();


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
        this(new WordReductionSeq<C>());
    }


    /**
     * Constructor.
     *
     * @param red Word Reduction engine
     */
    public WordGroebnerBaseAbstract(WordReduction<C> red) {
        this(red, new OrderedWordPairlist<C>());
    }


    /**
     * Constructor.
     *
     * @param red Word Reduction engine
     * @param pl  Word pair selection strategy
     */
    public WordGroebnerBaseAbstract(WordReduction<C> red, WordPairList<C> pl) {
        this.red = red;
        this.strategy = pl;
    }


    /**
     * Get the String representation with GB engines.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }


    /**
     * Normalize polynomial list.
     *
     * @param A list of polynomials.
     * @return list of polynomials with zeros removed and ones/units reduced.
     */
    public List<GenWordPolynomial<C>> normalizeZerosOnes(List<GenWordPolynomial<C>> A) {
        if (A == null) {
            return A;
        }
        List<GenWordPolynomial<C>> N = new ArrayList<GenWordPolynomial<C>>(A.size());
        if (A.isEmpty()) {
            return N;
        }
        for (GenWordPolynomial<C> p : A) {
            if (p == null || p.isZERO()) {
                continue;
            }
            if (p.isUnit()) {
                N.clear();
                N.add(p.ring.getONE());
                return N;
            }
            N.add(p.abs());
        }
        //N.trimToSize();
        return N;
    }


    /**
     * Common zero test, test if univariate leading words exist for all
     * variables.
     *
     * @param F polynomial list.
     * @return -1, 0 or 1 if "dimension"(ideal(F)) &eq; -1, 0 or &ge; 1.
     */
    public int commonZeroTest(List<GenWordPolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return 1;
        }
        GenWordPolynomialRing<C> pfac = F.get(0).ring;
        if (pfac.alphabet.length() <= 0) {
            return -1;
        }
        Set<String> v = new HashSet<String>(); // for non reduced GBs
        for (GenWordPolynomial<C> p : F) {
            if (p.isZERO()) {
                continue;
            }
            if (p.isConstant()) { // for non-monic lists
                return -1;
            }
            Word e = p.leadingWord();
            if (e == null) {
                continue;
            }
            SortedMap<String, Integer> u = e.dependencyOnVariables();
            if (u == null) {
                continue;
            }
            if (u.size() == 1) {
                v.add(u.firstKey());
            }
        }
        if (pfac.alphabet.length() == v.size()) {
            return 0;
        }
        return 1;
    }


    /**
     * Univariate head term degrees.
     *
     * @param A list of polynomials.
     * @return a list of the degrees of univariate head terms.
     */
    public List<Long> univariateDegrees(List<GenWordPolynomial<C>> A) {
        List<Long> ud = new ArrayList<Long>();
        if (A == null || A.size() == 0) {
            return ud;
        }
        GenWordPolynomialRing<C> pfac = A.get(0).ring;
        if (pfac.alphabet.length() <= 0) {
            return ud;
        }
        SortedMap<String, Long> v = new TreeMap<String, Long>(); // for non reduced GBs
        for (GenWordPolynomial<C> p : A) {
            Word e = p.leadingWord();
            if (e == null) {
                continue;
            }
            SortedMap<String, Integer> u = e.dependencyOnVariables();
            if (u == null) {
                continue;
            }
            if (u.size() == 1) {
                Long d = v.get(u.firstKey());
                if (d == null) { // record only once
                    v.put(u.firstKey(), Long.valueOf(u.get(u.firstKey())));
                }
            }
        }
        ud.addAll(v.values());
        //Collections.reverse(ud);
        return ud;
    }


    /**
     * Word Groebner base test.
     *
     * @param F Word polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(List<GenWordPolynomial<C>> F) {
        if (F == null || F.size() <= 1) {
            return true;
        }
        for (int i = 0; i < F.size(); i++) {
            GenWordPolynomial<C> pi = F.get(i);
            for (int j = i + 1; j < F.size(); j++) {
                GenWordPolynomial<C> pj = F.get(j);
                List<GenWordPolynomial<C>> S = red.SPolynomials(pi, pj);
                for (GenWordPolynomial<C> s : S) {
                    //System.out.println("s-pol("+i+","+j+"): " + s.leadingWord());
                    GenWordPolynomial<C> h = red.normalform(F, s);
                    if (!h.isZERO()) {
                        logger.info("no GB: pi = " + pi + ", pj = " + pj);
                        logger.info("s  = " + s + ", h = " + h);
                        return false;
                    }
                }
                S = red.SPolynomials(pj, pi);
                for (GenWordPolynomial<C> s : S) {
                    //System.out.println("s-pol("+j+","+i+"): " + s.leadingWord());
                    GenWordPolynomial<C> h = red.normalform(F, s);
                    if (!h.isZERO()) {
                        logger.info("no GB: pj = " + pj + ", pi = " + pi);
                        logger.info("s  = " + s + ", h = " + h);
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Groebner base using pairlist class.
     *
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public abstract List<GenWordPolynomial<C>> GB(List<GenWordPolynomial<C>> F);


    /**
     * Minimal ordered Groebner basis.
     *
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    public List<GenWordPolynomial<C>> minimalGB(List<GenWordPolynomial<C>> Gp) {
        if (Gp == null || Gp.size() <= 1) {
            return Gp;
        }
        // remove zero polynomials
        List<GenWordPolynomial<C>> G = new ArrayList<GenWordPolynomial<C>>(Gp.size());
        for (GenWordPolynomial<C> a : Gp) {
            if (a != null && !a.isZERO()) { // always true in GB()
                // already positive a = a.abs();
                G.add(a);
            }
        }
        if (G.size() <= 1) {
            return G;
        }
        // remove top reducible polynomials
        GenWordPolynomial<C> a;
        List<GenWordPolynomial<C>> F;
        F = new ArrayList<GenWordPolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                // drop polynomial 
                if (debug) {
                    System.out.println("dropped " + a);
                    List<GenWordPolynomial<C>> ff;
                    ff = new ArrayList<GenWordPolynomial<C>>(G);
                    ff.addAll(F);
                    a = red.normalform(ff, a);
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
            for (GenWordPolynomial<C> aa : G) {
                System.out.println("aa = " + aa.length() + ", lt = " + aa.getMap().keySet());
            }
        }
        int i = 0;
        while (i < len) {
            a = G.remove(0);
            if (debug) {
                System.out.println("doing " + a.length() + ", lt = " + a.leadingWord());
            }
            a = red.normalform(G, a);
            G.add(a); // adds as last
            i++;
        }
        return G;
    }


    /**
     * Test for minimal ordered Groebner basis.
     *
     * @param Gp an ideal base.
     * @return true, if Gp is a reduced minimal Groebner base.
     */
    public boolean isMinimalGB(List<GenWordPolynomial<C>> Gp) {
        if (Gp == null || Gp.size() == 0) {
            return true;
        }
        // test for zero polynomials
        for (GenWordPolynomial<C> a : Gp) {
            if (a == null || a.isZERO()) {
                if (debug) {
                    logger.debug("zero polynomial " + a);
                }
                return false;
            }
        }
        // test for top reducible polynomials
        List<GenWordPolynomial<C>> G = new ArrayList<GenWordPolynomial<C>>(Gp);
        List<GenWordPolynomial<C>> F = new ArrayList<GenWordPolynomial<C>>(G.size());
        while (G.size() > 0) {
            GenWordPolynomial<C> a = G.remove(0);
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                if (debug) {
                    logger.debug("top reducible polynomial " + a);
                }
                return false;
            }
            F.add(a);
        }
        G = F;
        if (G.size() <= 1) {
            return true;
        }
        // test reducibility of polynomials
        int len = G.size();
        int i = 0;
        while (i < len) {
            GenWordPolynomial<C> a = G.remove(0);
            if (!red.isNormalform(G, a)) {
                if (debug) {
                    logger.debug("reducible polynomial " + a);
                }
                return false;
            }
            G.add(a); // re-adds as last
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
