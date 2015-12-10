/*
 * $Id$
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
 * @author Heinz Kredel
 */

public final class TermOrderByName implements Serializable {


    //private static final Logger logger = Logger.getLogger(TermOrderByName.class);


    /***
     * TermOrder named values.
     * 
     * Variables in printed polynomial <b>(low, ..., medium, ..., high)</b>
     */
    public static final TermOrder LEX = new TermOrder(TermOrder.LEX);


    public static final TermOrder INVLEX = new TermOrder(TermOrder.INVLEX);


    public static final TermOrder GRLEX = new TermOrder(TermOrder.GRLEX);


    public static final TermOrder IGRLEX = new TermOrder(TermOrder.IGRLEX);


    public static final TermOrder REVLEX = new TermOrder(TermOrder.REVLEX);


    public static final TermOrder REVILEX = new TermOrder(TermOrder.REVILEX);


    public static final TermOrder REVTDEG = new TermOrder(TermOrder.REVTDEG);


    public static final TermOrder REVITDG = new TermOrder(TermOrder.REVITDG);


    public final static TermOrder DEFAULT = new TermOrder(TermOrder.DEFAULT_EVORD);


    /**
     * TermOrder names from other CAS.
     * 
     * Variables in printed polynomial <b>(high, ..., medium, ..., low)</b>
     */
    public final static TermOrder Lexicographic = INVLEX;


    public final static TermOrder NegativeLexicographic = LEX;


    public final static TermOrder DegreeLexicographic = IGRLEX;


    public final static TermOrder NegativeDegreeLexicographic = GRLEX;


    public final static TermOrder DegreeReverseLexicographic = REVITDG;


    public final static TermOrder NegativeDegreeReverseLexicographic = REVTDEG;


    /**
     * Construct block TermOrder.
     * @param t1 term order for both blocks
     * @param e exponent vector of desired length
     * @param s split index
     * @return constructed term order
     */
    public final static TermOrder blockOrder(TermOrder t1, ExpVector e, int s) {
        return new TermOrder(t1.getEvord(), t1.getEvord(), e.length(), e.length() - s);
    }


    /**
     * Construct block TermOrder.
     * @param t1 term order for lower valiables
     * @param t2 term order for higher variables
     * @param e exponent vector of desired length
     * @param s split index
     * @return constructed term order
     */
    public final static TermOrder blockOrder(TermOrder t1, TermOrder t2, ExpVector e, int s) {
        return new TermOrder(t2.getEvord(), t1.getEvord(), e.length(), e.length() - s);
    }


    /**
     * Construct weight TermOrder.
     * @param w weight vector
     * @return constructed term order
     */
    public final static TermOrder weightOrder(long[] w) {
        return TermOrder.reverseWeight(new long[][] { w });
    }


    /**
     * Construct weight TermOrder.
     * @param w weight matrix
     * @return constructed term order
     */
    public final static TermOrder weightOrder(long[][] w) {
        return TermOrder.reverseWeight(w);
    }

}
