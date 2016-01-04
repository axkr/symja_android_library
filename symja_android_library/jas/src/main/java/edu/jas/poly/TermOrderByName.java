/*
 * $Id: TermOrderByName.java 5391 2016-01-04 13:46:50Z kredel $
 */

package edu.jas.poly;




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
 * <b>Note:</b> Variables in printed JAS polynomial <b>(low, ..., medium, ...,
 * high)</b> Variables in other CAS polynomial <b>(high, ..., medium, ...,
 * low)</b> with <b>low</b> &lt; <b>medium</b> &lt; <b>high</b>. Example: for
 * variables x<sub>1</sub>, ..., x<sub>r</sub> it is assumed in JAS that
 * x<sub>1</sub> &lt; ... &lt; x<sub>r</sub> in other CAS it means x<sub>1</sub>
 * &gt; ... &gt; x<sub>r</sub>.
 * 
 * @author Heinz Kredel
 */

public class TermOrderByName {


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


    // Math like term orders:

    /**
     * TermOrder name Lexicographic of Math like CAS.
     */
    public final static TermOrder Lexicographic = REVILEX;


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


    // Sage term orders:

    /**
     * TermOrder name lex of Sage.
     */
    public final static TermOrder lex = Lexicographic; // = REVILEX; 


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


    // Singular term orders:

    /**
     * TermOrder name lp of Singular.
     */
    public final static TermOrder lp = lex; // = REVILEX; 


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


    // missing: public final static TermOrder negrevlex; // = LEX; 


    /**
     * Construct elimination block TermOrder. Variables {x<sub>1</sub>, ...,
     * x<sub>s-1</sub>} &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
     * 
     * @param t1 term order for both blocks
     * @param s split index
     * @return constructed term order
     */
    public final static TermOrder blockOrder(TermOrder t1, int s) {
        return t1.blockOrder(s);
    }


    /**
     * Construct elimination block TermOrder. Variables {x<sub>1</sub>, ...,
     * x<sub>s-1</sub>} &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
     * 
     * @param t1 term order for both blocks
     * @param e exponent vector of desired length, r = length(e)
     * @param s split index
     * @return constructed term order
     */
    public final static TermOrder blockOrder(TermOrder t1, ExpVector e, int s) {
        return t1.blockOrder(s, e.length());
    }


    /**
     * Construct elimination block TermOrder. Variables {x<sub>1</sub>, ...,
     * x<sub>s-1</sub>} &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
     * 
     * @param t1 term order for lower valiables
     * @param t2 term order for higher variables
     * @param s split index
     * @return constructed term order
     */
    public final static TermOrder blockOrder(TermOrder t1, TermOrder t2, int s) {
        return new TermOrder(t1.getEvord(), t2.getEvord(), Integer.MAX_VALUE, s);
    }


    /**
     * Construct elimination block TermOrder. Variables {x<sub>1</sub>, ...,
     * x<sub>s-1</sub>} &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
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


    /**
     * 
     * Construct weight for INVLEX.
     * @return weight matrix
     */
    public final static long[][] weightForOrder(int to, int n) {
        long[][] w = new long[n][];
        switch (to) {
        case TermOrder.INVLEX:
        default:
	    for (int i = 0; i < n; i++ ) {
		w[i] = new long[n];
		long[] wi = w[i];
		for( int j = 0; j < n; j++ ) { 
		    if ((n-1-i) == j) {
			wi[j] = 1L;
		    } else {
			wi[j] = 0L;
		    }
		}
	    }
            break;
        case TermOrder.REVILEX:
	    for (int i = 0; i < n; i++ ) {
		w[i] = new long[n];
		long[] wi = w[i];
		for( int j = 0; j < n; j++ ) { 
		    if (i == j) {
			wi[j] = 1L;
		    } else {
			wi[j] = 0L;
		    }
		}
	    }
            break;
        }
        return w;
    }

}
