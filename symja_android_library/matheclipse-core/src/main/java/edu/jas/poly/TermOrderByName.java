/*
 * $Id$
 */

package edu.jas.poly;


import org.apache.log4j.Logger;

import java.util.List;


/**
 * Term order names for ordered polynomials. Defines names for the most used
 * term orders: graded and lexicographical orders. For the definitions see for
 * example the articles <a href="http://doi.acm.org/10.1145/43882.43887">Kredel
 * "Admissible term orderings used in computer algebra systems"</a> and
 * <a href="http://doi.acm.org/10.1145/70936.70941">Sit,
 * "Some comments on term-ordering in Gr&ouml;bner basis computations"</a>. Not
 * all algorithms may work with all term orders since not all are well-founded,
 * so watch your step.
 * <p>
 * <b>Note:</b> Variables in printed JAS polynomial <b>(low, ..., medium, ...,
 * high)</b> Variables in other CAS polynomial <b>(high, ..., medium, ...,
 * low)</b> with <b>low</b> &lt; <b>medium</b> &lt; <b>high</b>. Example: for
 * variables x<sub>1</sub>, ..., x<sub>r</sub> it is assumed in JAS that x
 * <sub>1</sub> &lt; ... &lt; x<sub>r</sub> in other CAS it means x<sub>1</sub>
 * &gt; ... &gt; x<sub>r</sub>.
 *
 * @author Heinz Kredel
 */

public class TermOrderByName {


    /**
     * TermOrder named LEX.
     */
    public static final TermOrder LEX = new TermOrder(TermOrder.LEX);
    /**
     * TermOrder named INVLEX.
     */
    public static final TermOrder INVLEX = new TermOrder(TermOrder.INVLEX);
    /**
     * TermOrder named GRLEX.
     */
    public static final TermOrder GRLEX = new TermOrder(TermOrder.GRLEX);
    /**
     * TermOrder named IGRLEX.
     */
    public static final TermOrder IGRLEX = new TermOrder(TermOrder.IGRLEX);
    /**
     * TermOrder named REVLEX.
     */
    public static final TermOrder REVLEX = new TermOrder(TermOrder.REVLEX);
    /**
     * TermOrder named REVILEX.
     */
    public static final TermOrder REVILEX = new TermOrder(TermOrder.REVILEX);
    /**
     * TermOrder named REVTDEG.
     */
    public static final TermOrder REVTDEG = new TermOrder(TermOrder.REVTDEG);
    /**
     * TermOrder named REVITDG.
     */
    public static final TermOrder REVITDG = new TermOrder(TermOrder.REVITDG);
    /**
     * TermOrder named ITDEGLEX.
     */
    public static final TermOrder ITDEGLEX = new TermOrder(TermOrder.ITDEGLEX);
    /**
     * TermOrder named REVITDEG.
     */
    public static final TermOrder REVITDEG = new TermOrder(TermOrder.REVITDEG);
    /**
     * Default TermOrder.
     */
    public final static TermOrder DEFAULT = new TermOrder(TermOrder.DEFAULT_EVORD);
    /**
     * TermOrder name Lexicographic of Math like CAS.
     */
    public final static TermOrder Lexicographic = REVILEX;


    // Math like term orders:
    /**
     * TermOrder name NegativeLexicographic of Math like CAS.
     */
    public final static TermOrder NegativeLexicographic = REVLEX;
    /**
     * TermOrder name DegreeLexicographic of Math like CAS.
     */
    public final static TermOrder DegreeLexicographic = REVITDG;
    /**
     * TermOrder name NegativeDegreeLexicographic of Math like CAS.
     */
    public final static TermOrder NegativeDegreeLexicographic = REVITDEG; // was REVTDEG;
    /**
     * TermOrder name ReverseLexicographic of Math like CAS.
     */
    public final static TermOrder ReverseLexicographic = INVLEX;
    /**
     * TermOrder name DegreeReverseLexicographic of Math like CAS.
     */
    public final static TermOrder DegreeReverseLexicographic = ITDEGLEX; // was IGRLEX;
    /**
     * TermOrder name NegativeReverseLexicographic of Math like CAS.
     */
    public final static TermOrder NegativeReverseLexicographic = LEX;
    /**
     * TermOrder name NegativeDegreeReverseLexicographic of Math like CAS.
     */
    public final static TermOrder NegativeDegreeReverseLexicographic = GRLEX;
    /**
     * TermOrder name lex of Sage.
     */
    public final static TermOrder lex = Lexicographic; // = REVILEX;


    // Sage term orders:
    /**
     * TermOrder name degrevlex of Sage.
     */
    public final static TermOrder degrevlex = DegreeReverseLexicographic; // = IGRLEX;
    /**
     * TermOrder name deglex of Sage.
     */
    public final static TermOrder deglex = DegreeLexicographic; // = REVITDG;
    /**
     * TermOrder name invlex of Sage.
     */
    public final static TermOrder invlex = INVLEX; //ReverseLexicographic
    /**
     * TermOrder name neglex of Sage.
     */
    public final static TermOrder neglex = NegativeLexicographic; // = REVLEX;
    /**
     * TermOrder name negdegrevlex of Sage.
     */
    public final static TermOrder negdegrevlex = NegativeDegreeReverseLexicographic; // = GRLEX;
    /**
     * TermOrder name negdeglex of Sage.
     */
    public final static TermOrder negdeglex = NegativeDegreeLexicographic; // = REVTDEG;
    /**
     * TermOrder name negrevlex of Sage.
     */
    public final static TermOrder negrevlex = NegativeReverseLexicographic; // = LEX;
    /**
     * TermOrder name lp of Singular.
     */
    public final static TermOrder lp = lex; // = REVILEX;


    // Singular term orders:
    /**
     * TermOrder name dp of Singular.
     */
    public final static TermOrder dp = degrevlex; // = IGRLEX;
    /**
     * TermOrder name Dp of Singular.
     */
    public final static TermOrder Dp = deglex; // = REVITDG;
    /**
     * TermOrder name rp of Singular.
     */
    public final static TermOrder rp = invlex; // = INVLEX;
    /**
     * TermOrder name ls of Singular.
     */
    public final static TermOrder ls = neglex; // = REVLEX;
    /**
     * TermOrder name ds of Singular.
     */
    public final static TermOrder ds = negdegrevlex; // = GRLEX;
    /**
     * TermOrder name Ds of Singular.
     */
    public final static TermOrder Ds = negdeglex; // = REVTDEG;
    private static final Logger logger = Logger.getLogger(TermOrderByName.class);


    // missing: public final static TermOrder negrevlex; // = LEX; 

    /**
     * Construct elimination block TermOrder. Variables {x<sub>1</sub>, ..., x
     * <sub>s-1</sub>} &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
     *
     * @param t1 term order for both blocks
     * @param s  split index
     * @return constructed term order
     */
    public final static TermOrder blockOrder(TermOrder t1, int s) {
        return t1.blockOrder(s);
    }


    /**
     * Construct elimination block TermOrder. Variables {x<sub>1</sub>, ..., x
     * <sub>s-1</sub>} &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
     *
     * @param t1 term order for both blocks
     * @param e  exponent vector of desired length, r = length(e)
     * @param s  split index
     * @return constructed term order
     */
    public final static TermOrder blockOrder(TermOrder t1, ExpVector e, int s) {
        return t1.blockOrder(s, e.length());
    }


    /**
     * Construct elimination block TermOrder. Variables {x<sub>1</sub>, ..., x
     * <sub>s-1</sub>} &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
     *
     * @param t1 term order for lower valiables
     * @param t2 term order for higher variables
     * @param s  split index
     * @return constructed term order
     */
    public final static TermOrder blockOrder(TermOrder t1, TermOrder t2, int s) {
        return new TermOrder(t1.getEvord(), t2.getEvord(), Integer.MAX_VALUE, s);
    }


    /**
     * Construct elimination block TermOrder. Variables {x<sub>1</sub>, ..., x
     * <sub>s-1</sub>} &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
     *
     * @param t1 term order for lower valiables
     * @param t2 term order for higher variables
     * @param e  exponent vector of desired length, r = length(e)
     * @param s  split index
     * @return constructed term order
     */
    public final static TermOrder blockOrder(TermOrder t1, TermOrder t2, ExpVector e, int s) {
        return new TermOrder(t1.getEvord(), t2.getEvord(), e.length(), s);
    }


    /**
     * Construct weight TermOrder.
     *
     * @param v weight vector
     * @return constructed term order
     */
    public final static TermOrder weightOrder(long[] v) {
        return TermOrder.reverseWeight(new long[][]{v});
    }


    /**
     * Construct weight TermOrder.
     *
     * @param w weight matrix
     * @return constructed term order
     */
    public final static TermOrder weightOrder(long[][] w) {
        return TermOrder.reverseWeight(w);
    }


    /**
     * Construct weight TermOrder.
     *
     * @param wa weight matrix as List
     * @return constructed term order
     */
    public final static TermOrder weightOrder(List<List<Long>> wa) {
        int n = wa.size();
        long[][] w = new long[n][];
        for (int i = 0; i < n; i++) {
            List<Long> row = wa.get(i);
            int m = row.size();
            long[] wi = new long[m];
            for (int j = 0; j < m; j++) {
                wi[j] = row.get(j);
            }
            w[i] = wi;
        }
        //return TermOrder.reverseWeight(w);
        return weightOrder(w);
    }


    /**
     * Construct weight for term order.
     *
     * @param to term order
     * @param n  exponent vector size
     * @return weight matrix
     */
    public final static long[][] weightForOrder(TermOrder to, int n) {
        if (to.isSplit()) {
            return weightForSplitOrder(to.getEvord(), to.getEvord2(), n, to.getSplit());
        }
        return weightForOrder(to.getEvord(), n);
    }


    /**
     * Construct weight for term order.
     *
     * @param to term order indicator
     * @param n  exponent vector size
     * @return weight matrix
     */
    /*public*/
    final static long[][] weightForOrder(int to, int n) {
        int k = 0;
        switch (to) {
            case TermOrder.IGRLEX:
                k = n + 1;
                break;
            case TermOrder.REVILEX:
                // no break
            case TermOrder.INVLEX:
                k = n;
                break;
            default:
        }
        logger.info("to = " + to + ", k = " + k);
        long[][] w = new long[k][];
        long[] wi;
        switch (to) {
            case TermOrder.INVLEX:
                for (int i = 0; i < n; i++) {
                    w[i] = new long[n];
                    wi = w[i];
                    for (int j = 0; j < n; j++) {
                        if (i == j) { //n - 1 -
                            wi[j] = 1L;
                        } else {
                            wi[j] = 0L;
                        }
                    }
                }
                break;
            case TermOrder.IGRLEX:
                w[0] = new long[n];
                wi = w[0];
                for (int j = 0; j < n; j++) {
                    wi[j] = 1L;
                }
                for (int i = 0; i < n; i++) {
                    w[i + 1] = new long[n];
                    wi = w[i + 1];
                    for (int j = 0; j < n; j++) {
                        if (i == j) { //n - 1 -
                            wi[j] = 1L;
                        } else {
                            wi[j] = 0L;
                        }
                    }
                }
                break;
            case TermOrder.REVILEX:
                for (int i = 0; i < n; i++) {
                    w[i] = new long[n];
                    wi = w[i];
                    for (int j = 0; j < n; j++) {
                        if ((n - 1 - i) == j) {
                            wi[j] = 1L;
                        } else {
                            wi[j] = 0L;
                        }
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("case " + to + " not implemented for weightForOrder");
        }
        return w;
    }


    /**
     * Construct weight for split term order.
     *
     * @param to1 first term order indicator
     * @param to2 second term order indicator
     * @param n   exponent vector size
     * @param s   slpit index
     * @return weight matrix
     */
    /*public*/
    final static long[][] weightForSplitOrder(int to, int to2, int n, int s) {
        int k = 0;
        switch (to) {
            case TermOrder.IGRLEX:
                k += 1;
                break;
            case TermOrder.INVLEX:
                k += s;
                break;
            default:
        }
        //System.out.println("to = " + to + ", k = " + k);
        switch (to2) {
            case TermOrder.IGRLEX:
                k += 1;
                break;
            case TermOrder.INVLEX:
                k += n - s;
                break;
            default:
        }
        logger.info("to = " + to + ", k = " + k);
        //System.out.println("to = " + to + ", k = " + k);
        long[][] w = new long[k + n][];
        boolean done = true;
        switch (to) {
            case TermOrder.IGRLEX:
                w[0] = new long[n];
                long[] wi = w[0];
                int j;
                for (j = 0; j < s; j++) {
                    wi[j] = 1L;
                }
                for (; j < n; j++) {
                    wi[j] = 0L;
                }
                break;
            case TermOrder.INVLEX:
                for (int i = 0; i < s; i++) {
                    w[i] = new long[n];
                    wi = w[i]; // long[]
                    for (j = 0; j < n; j++) {
                        if ((n - 1 - i) == j) {
                            wi[j] = 1L;
                        } else {
                            wi[j] = 0L;
                        }
                    }
                }
                break;
            default:
                done = false;
                // compiler/run time error:
                //throw new UnsupportedOperationException("case " + to + "/" + to2 + " not implemented for weightForOrder");
                break;
        }
        switch (to2) {
            case TermOrder.IGRLEX:
                w[k - 1] = new long[n];
                long[] wi = w[k - 1];
                int j;
                for (j = 0; j < s; j++) {
                    wi[j] = 0L;
                }
                for (; j < n; j++) {
                    wi[j] = 1L;
                }
                break;
            case TermOrder.INVLEX:
                for (int i = 0; i < s; i++) {
                    w[s + i] = new long[n];
                    wi = w[s + i]; // long[]
                    for (j = 0; j < n; j++) {
                        if ((n - 1 - i) == (s + j)) {
                            wi[j] = 1L;
                        } else {
                            wi[j] = 0L;
                        }
                    }
                }
                break;
            default:
                done = false;
                break;
        }
        if (!done) {
            //System.out.println("weightForSplitOrder case " + to + "/" + to2);
            throw new UnsupportedOperationException(
                    "case " + to + "/" + to2 + " not implemented for weightForOrder");
        }
        //System.out.println("weight: " + Arrays.toString(w));
        // break ties by inv lex term order
        for (int i = 0; i < n; i++) {
            w[k + i] = new long[n];
            long[] wi = w[k + i];
            for (int j = 0; j < n; j++) {
                if ((i) == j) { //n - 1 - 
                    wi[j] = 1L;
                } else {
                    wi[j] = 0L;
                }
            }
        }
        return w;
    }

}
