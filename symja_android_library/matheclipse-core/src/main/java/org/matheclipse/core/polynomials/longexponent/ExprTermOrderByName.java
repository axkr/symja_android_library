/*
 * $Id: ExprTermOrderByName.java 5391 2016-01-04 13:46:50Z kredel $
 */

package org.matheclipse.core.polynomials.longexponent;

/**
 * Term order names for ordered polynomials. Defines names for the most used term orders: graded and
 * lexicographical orders.
 */
public class ExprTermOrderByName {

  // private static final Logger logger = LogManager.getLogger(ExprTermOrderByName.class);

  /** ExprTermOrder named LEX. */
  public static final ExprTermOrder LEX = new ExprTermOrder(ExprTermOrder.LEX);

  /** ExprTermOrder named INVLEX. */
  public static final ExprTermOrder INVLEX = new ExprTermOrder(ExprTermOrder.INVLEX);

  /** ExprTermOrder named GRLEX. */
  public static final ExprTermOrder GRLEX = new ExprTermOrder(ExprTermOrder.GRLEX);

  /** ExprTermOrder named IGRLEX. */
  public static final ExprTermOrder IGRLEX = new ExprTermOrder(ExprTermOrder.IGRLEX);

  /** ExprTermOrder named REVLEX. */
  public static final ExprTermOrder REVLEX = new ExprTermOrder(ExprTermOrder.REVLEX);

  /** ExprTermOrder named REVILEX. */
  public static final ExprTermOrder REVILEX = new ExprTermOrder(ExprTermOrder.REVILEX);

  /** ExprTermOrder named REVTDEG. */
  public static final ExprTermOrder REVTDEG = new ExprTermOrder(ExprTermOrder.REVTDEG);

  /** ExprTermOrder named REVITDG. */
  public static final ExprTermOrder REVITDG = new ExprTermOrder(ExprTermOrder.REVITDG);

  /** ExprTermOrder named ITDEGLEX. */
  public static final ExprTermOrder ITDEGLEX = new ExprTermOrder(ExprTermOrder.ITDEGLEX);

  /** ExprTermOrder named REVITDEG. */
  public static final ExprTermOrder REVITDEG = new ExprTermOrder(ExprTermOrder.REVITDEG);

  /** Default ExprTermOrder. */
  public static final ExprTermOrder DEFAULT = new ExprTermOrder(ExprTermOrder.DEFAULT_EVORD);

  // Math like term orders:

  /** ExprTermOrder name Lexicographic of Math like CAS. */
  public static final ExprTermOrder Lexicographic = REVILEX;

  /** ExprTermOrder name NegativeLexicographic of Math like CAS. */
  public static final ExprTermOrder NegativeLexicographic = REVLEX;

  /** ExprTermOrder name DegreeLexicographic of Math like CAS. */
  public static final ExprTermOrder DegreeLexicographic = REVITDG;

  /** ExprTermOrder name NegativeDegreeLexicographic of Math like CAS. */
  public static final ExprTermOrder NegativeDegreeLexicographic = REVITDEG; // was REVTDEG;

  /** ExprTermOrder name ReverseLexicographic of Math like CAS. */
  public static final ExprTermOrder ReverseLexicographic = INVLEX;

  /** ExprTermOrder name DegreeReverseLexicographic of Math like CAS. */
  public static final ExprTermOrder DegreeReverseLexicographic = ITDEGLEX; // was IGRLEX;

  /** ExprTermOrder name NegativeReverseLexicographic of Math like CAS. */
  public static final ExprTermOrder NegativeReverseLexicographic = LEX;

  /** ExprTermOrder name NegativeDegreeReverseLexicographic of Math like CAS. */
  public static final ExprTermOrder NegativeDegreeReverseLexicographic = GRLEX;

  // Sage term orders:

  /** ExprTermOrder name lex of Sage. */
  public static final ExprTermOrder lex = Lexicographic; // = REVILEX;

  /** ExprTermOrder name degrevlex of Sage. */
  public static final ExprTermOrder degrevlex = DegreeReverseLexicographic; // = IGRLEX;

  /** ExprTermOrder name deglex of Sage. */
  public static final ExprTermOrder deglex = DegreeLexicographic; // = REVITDG;

  /** ExprTermOrder name invlex of Sage. */
  public static final ExprTermOrder invlex = INVLEX; // ReverseLexicographic

  /** ExprTermOrder name neglex of Sage. */
  public static final ExprTermOrder neglex = NegativeLexicographic; // = REVLEX;

  /** ExprTermOrder name negdegrevlex of Sage. */
  public static final ExprTermOrder negdegrevlex = NegativeDegreeReverseLexicographic; // = GRLEX;

  /** ExprTermOrder name negdeglex of Sage. */
  public static final ExprTermOrder negdeglex = NegativeDegreeLexicographic; // = REVTDEG;

  /** ExprTermOrder name negrevlex of Sage. */
  public static final ExprTermOrder negrevlex = NegativeReverseLexicographic; // = LEX;

  // Singular term orders:

  /** ExprTermOrder name lp of Singular. */
  public static final ExprTermOrder lp = lex; // = REVILEX;

  /** ExprTermOrder name dp of Singular. */
  public static final ExprTermOrder dp = degrevlex; // = IGRLEX;

  /** ExprTermOrder name Dp of Singular. */
  public static final ExprTermOrder Dp = deglex; // = REVITDG;

  /** ExprTermOrder name rp of Singular. */
  public static final ExprTermOrder rp = invlex; // = INVLEX;

  /** ExprTermOrder name ls of Singular. */
  public static final ExprTermOrder ls = neglex; // = REVLEX;

  /** ExprTermOrder name ds of Singular. */
  public static final ExprTermOrder ds = negdegrevlex; // = GRLEX;

  /** ExprTermOrder name Ds of Singular. */
  public static final ExprTermOrder Ds = negdeglex; // = REVTDEG;

  // missing: public final static ExprTermOrder negrevlex; // = LEX;

  /**
   * Construct elimination block ExprTermOrder. Variables {x<sub>1</sub>, ..., x<sub>s-1</sub>} &lt;
   * {x<sub>s</sub>, ..., x<sub>r</sub>}
   *
   * @param t1 term order for both blocks
   * @param s split index
   * @return constructed term order
   */
  public static final ExprTermOrder blockOrder(ExprTermOrder t1, int s) {
    return t1.blockOrder(s);
  }

  /**
   * Construct elimination block ExprTermOrder. Variables {x<sub>1</sub>, ..., x<sub>s-1</sub>} &lt;
   * {x<sub>s</sub>, ..., x<sub>r</sub>}
   *
   * @param t1 term order for both blocks
   * @param e exponent vector of desired length, r = length(e)
   * @param s split index
   * @return constructed term order
   */
  public static final ExprTermOrder blockOrder(ExprTermOrder t1, ExpVectorLong e, int s) {
    return t1.blockOrder(s, e.length());
  }

  /**
   * Construct elimination block ExprTermOrder. Variables {x<sub>1</sub>, ..., x<sub>s-1</sub>} &lt;
   * {x<sub>s</sub>, ..., x<sub>r</sub>}
   *
   * @param t1 term order for lower valiables
   * @param t2 term order for higher variables
   * @param s split index
   * @return constructed term order
   */
  public static final ExprTermOrder blockOrder(ExprTermOrder t1, ExprTermOrder t2, int s) {
    return new ExprTermOrder(t1.getEvord(), t2.getEvord(), Integer.MAX_VALUE, s);
  }

  /**
   * Construct elimination block ExprTermOrder. Variables {x<sub>1</sub>, ..., x<sub>s-1</sub>} &lt;
   * {x<sub>s</sub>, ..., x<sub>r</sub>}
   *
   * @param t1 term order for lower valiables
   * @param t2 term order for higher variables
   * @param e exponent vector of desired length, r = length(e)
   * @param s split index
   * @return constructed term order
   */
  public static final ExprTermOrder blockOrder(ExprTermOrder t1, ExprTermOrder t2, ExpVectorLong e,
      int s) {
    return new ExprTermOrder(t1.getEvord(), t2.getEvord(), e.length(), s);
  }

  /**
   * Construct weight ExprTermOrder.
   *
   * @param v weight vector
   * @return constructed term order
   */
  public static final ExprTermOrder weightOrder(long[] v) {
    return ExprTermOrder.reverseWeight(new long[][] {v});
  }

  /**
   * Construct weight ExprTermOrder.
   *
   * @param w weight matrix
   * @return constructed term order
   */
  public static final ExprTermOrder weightOrder(long[][] w) {
    return ExprTermOrder.reverseWeight(w);
  }

  /**
   * Construct weight for INVLEX.
   *
   * @return weight matrix
   */
  public static final long[][] weightForOrder(int to, int n) {
    long[][] w = new long[n][];
    switch (to) {
      case ExprTermOrder.INVLEX:
      default:
        for (int i = 0; i < n; i++) {
          w[i] = new long[n];
          long[] wi = w[i];
          for (int j = 0; j < n; j++) {
            if ((n - 1 - i) == j) {
              wi[j] = 1L;
            } else {
              wi[j] = 0L;
            }
          }
        }
        break;
      case ExprTermOrder.REVILEX:
        for (int i = 0; i < n; i++) {
          w[i] = new long[n];
          long[] wi = w[i];
          for (int j = 0; j < n; j++) {
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
