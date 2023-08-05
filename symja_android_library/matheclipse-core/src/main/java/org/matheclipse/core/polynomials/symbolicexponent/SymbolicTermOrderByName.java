/*
 * $Id: ExprTermOrderByName.java 5391 2016-01-04 13:46:50Z kredel $
 */

package org.matheclipse.core.polynomials.symbolicexponent;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.polynomials.longexponent.ExpVectorLong;

/**
 * Term order names for ordered polynomials. Defines names for the most used term orders: graded and
 * lexicographical orders.
 */
public class SymbolicTermOrderByName {

  /** ExpVectorTermOrder named LEX. */
  public static final SymbolicTermOrder LEX = new SymbolicTermOrder(SymbolicTermOrder.LEX);

  /** ExpVectorTermOrder named INVLEX. */
  public static final SymbolicTermOrder INVLEX = new SymbolicTermOrder(SymbolicTermOrder.INVLEX);

  /** ExpVectorTermOrder named GRLEX. */
  public static final SymbolicTermOrder GRLEX = new SymbolicTermOrder(SymbolicTermOrder.GRLEX);

  /** ExpVectorTermOrder named IGRLEX. */
  public static final SymbolicTermOrder IGRLEX = new SymbolicTermOrder(SymbolicTermOrder.IGRLEX);

  /** ExpVectorTermOrder named REVLEX. */
  public static final SymbolicTermOrder REVLEX = new SymbolicTermOrder(SymbolicTermOrder.REVLEX);

  /** ExpVectorTermOrder named REVILEX. */
  public static final SymbolicTermOrder REVILEX = new SymbolicTermOrder(SymbolicTermOrder.REVILEX);

  /** ExpVectorTermOrder named REVTDEG. */
  public static final SymbolicTermOrder REVTDEG = new SymbolicTermOrder(SymbolicTermOrder.REVTDEG);

  /** ExpVectorTermOrder named REVITDG. */
  public static final SymbolicTermOrder REVITDG = new SymbolicTermOrder(SymbolicTermOrder.REVITDG);

  /** ExpVectorTermOrder named ITDEGLEX. */
  public static final SymbolicTermOrder ITDEGLEX =
      new SymbolicTermOrder(SymbolicTermOrder.ITDEGLEX);

  /** ExpVectorTermOrder named REVITDEG. */
  public static final SymbolicTermOrder REVITDEG =
      new SymbolicTermOrder(SymbolicTermOrder.REVITDEG);

  /** Default ExpVectorTermOrder. */
  public static final SymbolicTermOrder DEFAULT =
      new SymbolicTermOrder(SymbolicTermOrder.DEFAULT_EVORD);

  // Math like term orders:

  /** ExpVectorTermOrder name Lexicographic of Math like CAS. */
  public static final SymbolicTermOrder Lexicographic = REVILEX;

  /** ExpVectorTermOrder name NegativeLexicographic of Math like CAS. */
  public static final SymbolicTermOrder NegativeLexicographic = REVLEX;

  /** ExpVectorTermOrder name DegreeLexicographic of Math like CAS. */
  public static final SymbolicTermOrder DegreeLexicographic = REVITDG;

  /** ExpVectorTermOrder name NegativeDegreeLexicographic of Math like CAS. */
  public static final SymbolicTermOrder NegativeDegreeLexicographic = REVITDEG; // was REVTDEG;

  /** ExpVectorTermOrder name ReverseLexicographic of Math like CAS. */
  public static final SymbolicTermOrder ReverseLexicographic = INVLEX;

  /** ExpVectorTermOrder name DegreeReverseLexicographic of Math like CAS. */
  public static final SymbolicTermOrder DegreeReverseLexicographic = ITDEGLEX; // was IGRLEX;

  /** ExpVectorTermOrder name NegativeReverseLexicographic of Math like CAS. */
  public static final SymbolicTermOrder NegativeReverseLexicographic = LEX;

  /** ExpVectorTermOrder name NegativeDegreeReverseLexicographic of Math like CAS. */
  public static final SymbolicTermOrder NegativeDegreeReverseLexicographic = GRLEX;

  // Sage term orders:

  /** ExpVectorTermOrder name lex of Sage. */
  public static final SymbolicTermOrder lex = Lexicographic; // = REVILEX;

  /** ExpVectorTermOrder name degrevlex of Sage. */
  public static final SymbolicTermOrder degrevlex = DegreeReverseLexicographic; // = IGRLEX;

  /** ExpVectorTermOrder name deglex of Sage. */
  public static final SymbolicTermOrder deglex = DegreeLexicographic; // = REVITDG;

  /** ExpVectorTermOrder name invlex of Sage. */
  public static final SymbolicTermOrder invlex = INVLEX; // ReverseLexicographic

  /** ExpVectorTermOrder name neglex of Sage. */
  public static final SymbolicTermOrder neglex = NegativeLexicographic; // = REVLEX;

  /** ExpVectorTermOrder name negdegrevlex of Sage. */
  public static final SymbolicTermOrder negdegrevlex = NegativeDegreeReverseLexicographic; // =
                                                                                           // GRLEX;

  /** ExpVectorTermOrder name negdeglex of Sage. */
  public static final SymbolicTermOrder negdeglex = NegativeDegreeLexicographic; // = REVTDEG;

  /** ExpVectorTermOrder name negrevlex of Sage. */
  public static final SymbolicTermOrder negrevlex = NegativeReverseLexicographic; // = LEX;

  // Singular term orders:

  /** ExpVectorTermOrder name lp of Singular. */
  public static final SymbolicTermOrder lp = lex; // = REVILEX;

  /** ExpVectorTermOrder name dp of Singular. */
  public static final SymbolicTermOrder dp = degrevlex; // = IGRLEX;

  /** ExpVectorTermOrder name Dp of Singular. */
  public static final SymbolicTermOrder Dp = deglex; // = REVITDG;

  /** ExpVectorTermOrder name rp of Singular. */
  public static final SymbolicTermOrder rp = invlex; // = INVLEX;

  /** ExpVectorTermOrder name ls of Singular. */
  public static final SymbolicTermOrder ls = neglex; // = REVLEX;

  /** ExpVectorTermOrder name ds of Singular. */
  public static final SymbolicTermOrder ds = negdegrevlex; // = GRLEX;

  /** ExpVectorTermOrder name Ds of Singular. */
  public static final SymbolicTermOrder Ds = negdeglex; // = REVTDEG;

  // missing: public final static ExpVectorTermOrder negrevlex; // = LEX;

  /**
   * Construct elimination block ExpVectorTermOrder. Variables {x<sub>1</sub>, ..., x<sub>s-1</sub>}
   * &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
   *
   * @param t1 term order for both blocks
   * @param s split index
   * @return constructed term order
   */
  public static final SymbolicTermOrder blockOrder(SymbolicTermOrder t1, int s) {
    return t1.blockOrder(s);
  }

  /**
   * Construct elimination block ExpVectorTermOrder. Variables {x<sub>1</sub>, ..., x<sub>s-1</sub>}
   * &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
   *
   * @param t1 term order for both blocks
   * @param e exponent vector of desired length, r = length(e)
   * @param s split index
   * @return constructed term order
   */
  public static final SymbolicTermOrder blockOrder(SymbolicTermOrder t1, ExpVectorLong e, int s) {
    return t1.blockOrder(s, e.length());
  }

  /**
   * Construct elimination block ExpVectorTermOrder. Variables {x<sub>1</sub>, ..., x<sub>s-1</sub>}
   * &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
   *
   * @param t1 term order for lower valiables
   * @param t2 term order for higher variables
   * @param s split index
   * @return constructed term order
   */
  public static final SymbolicTermOrder blockOrder(SymbolicTermOrder t1, SymbolicTermOrder t2,
      int s) {
    return new SymbolicTermOrder(t1.getEvord(), t2.getEvord(), Integer.MAX_VALUE, s);
  }

  /**
   * Construct elimination block ExpVectorTermOrder. Variables {x<sub>1</sub>, ..., x<sub>s-1</sub>}
   * &lt; {x<sub>s</sub>, ..., x<sub>r</sub>}
   *
   * @param t1 term order for lower valiables
   * @param t2 term order for higher variables
   * @param e exponent vector of desired length, r = length(e)
   * @param s split index
   * @return constructed term order
   */
  public static final SymbolicTermOrder blockOrder(SymbolicTermOrder t1, SymbolicTermOrder t2,
      ExpVectorLong e, int s) {
    return new SymbolicTermOrder(t1.getEvord(), t2.getEvord(), e.length(), s);
  }

  /**
   * Construct weight ExpVectorTermOrder.
   *
   * @param v weight vector
   * @return constructed term order
   */
  public static final SymbolicTermOrder weightOrder(IExpr[] v) {
    return SymbolicTermOrder.reverseWeight(new IExpr[][] {v});
  }

  /**
   * Construct weight ExpVectorTermOrder.
   *
   * @param w weight matrix
   * @return constructed term order
   */
  public static final SymbolicTermOrder weightOrder(IExpr[][] w) {
    return SymbolicTermOrder.reverseWeight(w);
  }

  /**
   * Construct weight for INVLEX.
   *
   * @return weight matrix
   */
  public static final long[][] weightForOrder(int to, int n) {
    long[][] w = new long[n][];
    switch (to) {
      case SymbolicTermOrder.INVLEX:
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
      case SymbolicTermOrder.REVILEX:
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
