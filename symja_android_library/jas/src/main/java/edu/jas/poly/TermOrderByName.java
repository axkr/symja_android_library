/*
 * $Id: TermOrderByName.java 5346 2015-12-09 22:41:06Z kredel $
 */

package edu.jas.poly;


import java.io.Serializable;


// import org.apache.log4j.Logger;


/**
 * Term order names for ordered polynomials. Defines names for the most used
 * term orders: graded and lexicographical orders. For the definitions see for
 * example the articles <a href="http://doi.acm.org/10.1145/43882.43887">Kredel,
 * "Admissible term orderings used in computer algebra systems"</a> and <a
 * href="http://doi.acm.org/10.1145/70936.70941">Sit,
 * "Some comments on term-ordering in Gr&ouml;bner basis computations"</a>. Not
 * all algorithms may work with all term orders since not all are well-founded,
 * so watch your step.
 *
 * <b>Note:</b>
 * Variables in printed JAS polynomial <b>(low, ..., medium, ..., high)</b>
 * Variables in other CAS polynomial <b>(high, ..., medium, ..., low)</b>
 * with <b>low</b> &lt; <b>medium</b> &lt; <b>high</b>.
 *
 * @author Heinz Kredel
 */

public final class TermOrderByName {


    //private static final Logger logger = Logger.getLogger(TermOrderByName.class);


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
     * Default TermOrder.
     */
    public final static TermOrder DEFAULT = new TermOrder(TermOrder.DEFAULT_EVORD);


    /**
     * TermOrder name Lexicographic of other CAS.
     */
    public final static TermOrder Lexicographic = INVLEX;


    /**
     * TermOrder name NegativeLexicographic of other CAS.
     */
    public final static TermOrder NegativeLexicographic = LEX;


    /**
     * TermOrder name ReverseLexicographic of other CAS.
     */
//    public final static TermOrder ReverseLexicographic = REVILEX;


    /**
     * TermOrder name DegreeLexicographic of other CAS.
     */
    public final static TermOrder DegreeLexicographic = IGRLEX;


    /**
     * TermOrder name NegativeDegreeLexicographic of other CAS.
     */
    public final static TermOrder NegativeDegreeLexicographic = REVTDEG;


    /**
     * TermOrder name DegreeReverseLexicographic of other CAS.
     */
    public final static TermOrder DegreeReverseLexicographic =  IGRLEX;  


    /**
     * TermOrder name NegativeDegreeReverseLexicographic of other CAS.
     */
    public final static TermOrder NegativeDegreeReverseLexicographic = REVTDEG;


    /**
     * Construct elimination block TermOrder.
     * Variables {x_1, ..., x_{s-1}} &lt; {x_s, ..., x_r}
     * 
     * @param t1 term order for both blocks
     * @param e exponent vector of desired length, r = length(e)
     * @param s split index
     * @return constructed term order
     */
    public final static TermOrder blockOrder(TermOrder t1, ExpVector e, int s) {
        return new TermOrder(t1.getEvord(), t1.getEvord(), e.length(), s);
    }


    /**
     * Construct elimination block TermOrder.
     * Variables {x_1, ..., x_{s-1}} &lt; {x_s, ..., x_r}
     *
     * @param t1 term order for lower valiables
     * @param t2 term order for higher variables
     * @param e exponent vector of desired length, r = length(e)
     * @param s split index
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
        return TermOrder.reverseWeight(new long[][] { v });
    }


    /**
     *
     * Construct weight TermOrder.
     * @param w weight matrix
     * @return constructed term order
     */
    public final static TermOrder weightOrder(long[][] w) {
        return TermOrder.reverseWeight(w);
    }

}
