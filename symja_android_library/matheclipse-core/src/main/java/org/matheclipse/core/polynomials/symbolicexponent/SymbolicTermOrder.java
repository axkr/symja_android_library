/*
 * $Id: ExprTermOrder.java 5391 2016-01-04 13:46:50Z kredel $
 */

package org.matheclipse.core.polynomials.symbolicexponent;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import edu.jas.kern.Scripting;

/**
 * Term order class for ordered polynomials. Implements the most used term orders: graded,
 * lexicographical, weight aray and block orders.
 */
public final class SymbolicTermOrder implements Serializable {

  /** */
  private static final long serialVersionUID = 360644493672021694L;

  private static final boolean debug = Config.DEBUG;

  // ExprTermOrder index values

  public static final int LEX = 1;

  public static final int MIN_EVORD = LEX;

  public static final int INVLEX = 2;

  public static final int GRLEX = 3;

  public static final int IGRLEX = 4;

  public static final int REVLEX = 5;

  public static final int REVILEX = 6;

  public static final int REVTDEG = 7;

  public static final int REVITDG = 8;

  public static final int ITDEGLEX = 9;

  public static final int REVITDEG = 10;

  public static final int MAX_EVORD = REVITDEG;

  public static final int DEFAULT_EVORD = IGRLEX;

  // public final static int DEFAULT_EVORD = INVLEX;

  // instance variables

  private final int evord;

  // for split termorders
  private final int evord2;

  private final int evbeg1;

  private final int evend1;

  private final int evbeg2;

  private final int evend2;

  /** Defined array of weight vectors. */
  private final IExpr[][] weight;

  /** Defined descending order comparator. Sorts the highest terms first. */
  private final EVComparator horder;

  /** Defined ascending order comparator. Sorts the lowest terms first. */
  private final EVComparator lorder;

  /** Defined sugar order comparator. Sorts the graded lowest terms first. */
  private final EVComparator sugar;

  /** Comparator for ExpVectors. */
  public abstract static class EVComparator implements Comparator<ExpVectorSymbolic>, Serializable {

    private static final long serialVersionUID = 5313857235195179046L;

    @Override
    public abstract int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2);
  }

  /** Constructor for default term order. */
  public SymbolicTermOrder() {
    this(DEFAULT_EVORD);
  }

  /**
   * Constructor for given term order.
   *
   * @param evord requested term order indicator / enumerator.
   */
  public SymbolicTermOrder(int evord) {
    if (evord < MIN_EVORD || MAX_EVORD < evord) {
      throw new IllegalArgumentException("invalid term order: " + evord);
    }
    this.evord = evord;
    this.evord2 = 0;
    weight = null;
    evbeg1 = 0;
    evend1 = Integer.MAX_VALUE;
    evbeg2 = evend1;
    evend2 = evend1;
    switch (evord) { // horder = new EVhorder();
      case SymbolicTermOrder.LEX: {
        horder = new EVComparator() {

          @Override
          public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
            return ExpVectorSymbolic.EVILCP(e1, e2);
          }
        };
        break;
      }
      case SymbolicTermOrder.INVLEX: {
        horder = new EVComparator() {

          @Override
          public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
            return -ExpVectorSymbolic.EVILCP(e1, e2);
          }
        };
        break;
      }
      case SymbolicTermOrder.GRLEX: {
        horder = new EVComparator() {

          @Override
          public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
            return ExpVectorSymbolic.EVIGLC(e1, e2);
          }
        };
        break;
      }
      case SymbolicTermOrder.IGRLEX: {
        horder = new EVComparator() {

          @Override
          public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
            return -ExpVectorSymbolic.EVIGLC(e1, e2);
          }
        };
        break;
      }
      case SymbolicTermOrder.REVLEX: {
        horder = new EVComparator() {

          @Override
          public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
            return ExpVectorSymbolic.EVRILCP(e1, e2);
          }
        };
        break;
      }
      case SymbolicTermOrder.REVILEX: {
        horder = new EVComparator() {

          @Override
          public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
            return -ExpVectorSymbolic.EVRILCP(e1, e2);
          }
        };
        break;
      }
      case SymbolicTermOrder.REVTDEG: {
        horder = new EVComparator() {

          @Override
          public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
            return ExpVectorSymbolic.EVRIGLC(e1, e2);
          }
        };
        break;
      }
      case SymbolicTermOrder.REVITDG: {
        horder = new EVComparator() {

          @Override
          public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
            return -ExpVectorSymbolic.EVRIGLC(e1, e2);
          }
        };
        break;
      }
      case SymbolicTermOrder.ITDEGLEX: {
        horder = new EVComparator() {

          @Override
          public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
            return -ExpVectorSymbolic.EVITDEGLC(e1, e2); // okay +/-
          }
        };
        break;
      }
      case SymbolicTermOrder.REVITDEG: {
        horder = new EVComparator() {

          @Override
          public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
            return ExpVectorSymbolic.EVRLITDEGC(e1, e2); // okay +/-
          }
        };
        break;
      }
      default: {
        throw new IllegalArgumentException("invalid term order: " + evord);
      }
    }
    // lorder = new EVlorder();
    lorder = new EVComparator() {

      @Override
      public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
        return -horder.compare(e1, e2);
      }
    };

    // sugar = new EVsugar();
    sugar = new EVComparator() {

      @Override
      public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
        return ExpVectorSymbolic.EVIGLC(e1, e2);
      }
    };
  }

  /**
   * Constructor for given exponent weights.
   *
   * @param w weight vector of longs.
   */
  public SymbolicTermOrder(IExpr[] w) {
    this(new IExpr[][] {w});
  }

  /**
   * Constructor for given exponent weights.
   *
   * @param w weight array of longs.
   */
  public SymbolicTermOrder(IExpr[][] w) {
    if (w == null || w.length == 0) {
      throw new IllegalArgumentException("invalid term order weight");
    }
    weight = Arrays.copyOf(w, w.length); // > Java-5
    this.evord = 0;
    this.evord2 = 0;
    evbeg1 = 0;
    evend1 = weight[0].length;
    evbeg2 = evend1;
    evend2 = evend1;

    horder = new EVComparator() {

      @Override
      public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
        return -ExpVectorSymbolic.EVIWLC(weight, e1, e2);
      }
    };

    // lorder = new EVlorder();
    lorder = new EVComparator() {

      @Override
      public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
        return +ExpVectorSymbolic.EVIWLC(weight, e1, e2);
        // return - horder.compare( e1, e2 );
      }
    };

    // sugar = new EVsugar();
    sugar = horder;
  }

  /**
   * Constructor for given split order.
   *
   * @param ev1 requested term order indicator for first block.
   * @param ev2 requested term order indicator for second block.
   * @param r max number of exponents to compare.
   * @param split index.
   */
  public SymbolicTermOrder(int ev1, int ev2, int r, int split) {
    if (ev1 < MIN_EVORD || MAX_EVORD - 2 < ev1) {
      throw new IllegalArgumentException("invalid split term order 1: " + ev1);
    }
    if (ev2 < MIN_EVORD || MAX_EVORD - 2 < ev2) {
      throw new IllegalArgumentException("invalid split term order 2: " + ev2);
    }
    this.evord = ev1;
    this.evord2 = ev2;
    weight = null;
    evbeg1 = 0;
    evend1 = split; // excluded
    evbeg2 = split;
    evend2 = r;
    if (evbeg2 < 0 || evbeg2 > evend2) {
      throw new IllegalArgumentException(
          "invalid term order split, r = " + r + ", split = " + split);
    }
    switch (evord) { // horder = new EVhorder();
      case SymbolicTermOrder.LEX: {
        switch (evord2) {
          case SymbolicTermOrder.LEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.INVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.GRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.IGRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          default: {
            horder = null;
          }
        }
        break;
      }
      case SymbolicTermOrder.INVLEX: {
        switch (evord2) {
          case SymbolicTermOrder.LEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.INVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.GRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.IGRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVILEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVTDEG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVITDG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          default: {
            horder = null;
          }
        }
        break;
      }
      case SymbolicTermOrder.GRLEX: {
        switch (evord2) {
          case SymbolicTermOrder.LEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.INVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.GRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.IGRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          default: {
            horder = null;
          }
        }
        break;
      }
      case SymbolicTermOrder.IGRLEX: {
        switch (evord2) {
          case SymbolicTermOrder.LEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.INVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.GRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.IGRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVILEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVTDEG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVITDG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          default: {
            horder = null;
          }
        }
        break;
      }
      // ----- begin reversed -----------
      case SymbolicTermOrder.REVLEX: {
        switch (evord2) {
          case SymbolicTermOrder.LEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.INVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.GRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.IGRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVILEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVTDEG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVITDG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          default: {
            horder = null;
          }
        }
        break;
      }
      case SymbolicTermOrder.REVILEX: {
        switch (evord2) {
          case SymbolicTermOrder.LEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.INVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.GRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.IGRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVILEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVTDEG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVITDG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          default: {
            horder = null;
          }
        }
        break;
      }
      case SymbolicTermOrder.REVTDEG: {
        switch (evord2) {
          case SymbolicTermOrder.LEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.INVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.GRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.IGRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVILEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVTDEG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVITDG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          default: {
            horder = null;
          }
        }
        break;
      }
      case SymbolicTermOrder.REVITDG: {
        switch (evord2) {
          case SymbolicTermOrder.LEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.INVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.GRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.IGRLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVLEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVILEX: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRILCP(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVTDEG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          case SymbolicTermOrder.REVITDG: {
            horder = new EVComparator() {

              @Override
              public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
                int t = -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg1, evend1);
                if (t != 0) {
                  return t;
                }
                return -ExpVectorSymbolic.EVRIGLC(e1, e2, evbeg2, evend2);
              }
            };
            break;
          }
          default: {
            horder = null;
          }
        }
        break;
      }
      // ----- end reversed-----------
      default: {
        horder = null;
      }
    }
    if (horder == null) {
      throw new IllegalArgumentException("invalid term order: " + evord + " 2 " + evord2);
    }

    lorder = new EVComparator() {

      @Override
      public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
        return -horder.compare(e1, e2);
      }
    };

    // sugar = new EVsugar();
    sugar = new EVComparator() {

      @Override
      public int compare(ExpVectorSymbolic e1, ExpVectorSymbolic e2) {
        return ExpVectorSymbolic.EVIGLC(e1, e2);
      }
    };
  }

  /*
   * Constructor for default split order.
   *
   * @param r max number of exponents to compare.
   *
   * @param split index. public ExprTermOrder(int r, int split) { this(DEFAULT_EVORD, DEFAULT_EVORD,
   * r, split); }
   */

  /**
   * Create block term order at split index.
   *
   * @param s split index.
   * @return block ExprTermOrder with split index.
   */
  public SymbolicTermOrder blockOrder(int s) {
    return blockOrder(s, Integer.MAX_VALUE);
  }

  /**
   * Create block term order at split index.
   *
   * @param s split index.
   * @param len length of ExpVectors to compare
   * @return block ExprTermOrder with split index.
   */
  public SymbolicTermOrder blockOrder(int s, int len) {
    return new SymbolicTermOrder(evord, evord, len, s);
  }

  /**
   * Create block term order at split index.
   *
   * @param s split index.
   * @param t second term order.
   * @return block ExprTermOrder with split index.
   */
  public SymbolicTermOrder blockOrder(int s, SymbolicTermOrder t) {
    return blockOrder(s, t, Integer.MAX_VALUE);
  }

  /**
   * Create block term order at split index.
   *
   * @param s split index.
   * @param t second term order.
   * @param len length of ExpVectors to compare
   * @return block ExprTermOrder with split index.
   */
  public SymbolicTermOrder blockOrder(int s, SymbolicTermOrder t, int len) {
    return new SymbolicTermOrder(evord, t.evord, len, s);
  }

  /**
   * Get the first defined order indicator.
   *
   * @return evord.
   */
  public int getEvord() {
    return evord;
  }

  /**
   * Get the second defined order indicator.
   *
   * @return evord2.
   */
  public int getEvord2() {
    return evord2;
  }

  /**
   * Get the split index.
   *
   * @return split.
   */
  public int getSplit() {
    return evend1; // = evbeg2
  }

  /**
   * Get the weight array.
   *
   * @return weight.
   */
  public IExpr[][] getWeight() {
    if (weight == null) {
      return null;
    }
    return Arrays.copyOf(weight, weight.length); // > Java-5
  }

  /**
   * Get the descending order comparator. Sorts the highest terms first.
   *
   * @return horder.
   */
  public EVComparator getDescendComparator() {
    return horder;
  }

  /**
   * Get the ascending order comparator. Sorts the lowest terms first.
   *
   * @return lorder.
   */
  public EVComparator getAscendComparator() {
    return lorder;
  }

  /**
   * Get the sugar order comparator. Sorts the graded lowest terms first.
   *
   * @return sugar.
   */
  public EVComparator getSugarComparator() {
    return sugar;
  }

  /**
   * Comparison with any other object.
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object B) {
    if (!(B instanceof SymbolicTermOrder)) {
      return false;
    }
    SymbolicTermOrder b = (SymbolicTermOrder) B;
    boolean t = evord == b.getEvord() && evord2 == b.evord2 && evbeg1 == b.evbeg1
        && evend1 == b.evend1 && evbeg2 == b.evbeg2 && evend2 == b.evend2;
    if (!t) {
      return t;
    }
    if (!Arrays.deepEquals(weight, b.weight)) {
      return false;
    }
    return true;
  }

  /**
   * Hash code.
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int h = evord;
    h = (h << 3) + evord2;
    h = (h << 4) + evbeg1;
    h = (h << 4) + evend1;
    h = (h << 4) + evbeg2;
    h = (h << 4) + evend2;
    if (weight == null) {
      return h;
    }
    h = h * 7 + Arrays.deepHashCode(weight);
    return h;
  }

  /**
   * String representation of weight matrix.
   *
   * @return string representation of weight matrix.
   */
  public String weightToString() {
    StringBuilder erg = new StringBuilder();
    if (weight != null) {
      erg.append("(");
      for (int j = 0; j < weight.length; j++) {
        if (j > 0) {
          erg.append(",");
        }
        IExpr[] wj = weight[j];
        erg.append("(");
        for (int i = 0; i < wj.length; i++) {
          if (i > 0) {
            erg.append(",");
          }
          erg.append(String.valueOf(wj[wj.length - 1 - i]));
        }
        erg.append(")");
      }
      erg.append(")");
    }
    return erg.toString();
  }

  /**
   * Script representation of weight matrix.
   *
   * @return script representation of weight matrix.
   */
  public String weightToScript() {
    // cases Python and Ruby
    StringBuilder erg = new StringBuilder();
    if (weight != null) {
      erg.append("[");
      for (int j = 0; j < weight.length; j++) {
        if (j > 0) {
          erg.append(",");
        }
        IExpr[] wj = weight[j];
        erg.append("[");
        for (int i = 0; i < wj.length; i++) {
          if (i > 0) {
            erg.append(",");
          }
          erg.append(String.valueOf(wj[wj.length - 1 - i]));
        }
        erg.append("]");
      }
      erg.append("]");
    }
    return erg.toString();
  }

  /**
   * String representation of ExprTermOrder.
   *
   * @return script representation of ExprTermOrder.
   */
  public String toScript() {
    if (weight != null) {
      StringBuilder erg = new StringBuilder();
      // erg.append("ExprTermOrder( ");
      erg.append(weightToScript());
      if (evend1 == evend2) {
        // erg.append(" )");
        return erg.toString();
      }
      erg.append("[" + evbeg1 + "," + evend1 + "]");
      erg.append("[" + evbeg2 + "," + evend2 + "]");
      // erg.append(" )");
      return erg.toString();
    }
    return toScriptPlain();
  }

  /**
   * String representation of ExprTermOrder.
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (weight != null) {
      StringBuilder erg = new StringBuilder();
      erg.append("W( ");
      erg.append(weightToString());
      if (evend1 == evend2) {
        erg.append(" )");
        return erg.toString();
      }
      erg.append("[" + evbeg1 + "," + evend1 + "]");
      erg.append("[" + evbeg2 + "," + evend2 + "]");
      erg.append(" )");
      return erg.toString();
    }
    return toStringPlain();
  }

  /** String representation of ExprTermOrder without prefix and weight matrix. */
  public String toStringPlain() {
    StringBuilder erg = new StringBuilder();
    if (weight != null) {
      return erg.toString();
    }
    erg.append(toScriptOrder(evord)); // JAS only
    if (evord2 <= 0) {
      return erg.toString();
    }
    erg.append("[" + evbeg1 + "," + evend1 + "]");
    erg.append(toScriptOrder(evord2)); // JAS only
    erg.append("[" + evbeg2 + "," + evend2 + "]");
    return erg.toString();
  }

  /** Script representation of ExprTermOrder without prefix and weight matrix. */
  public String toScriptPlain() {
    StringBuilder erg = new StringBuilder();
    if (weight != null) {
      return toScript();
    }
    erg.append("Order");
    switch (Scripting.getLang()) {
      case Ruby:
        erg.append("::");
        break;
      case Python:
      default:
        erg.append(".");
    }
    erg.append(toScriptOrder(evord));
    if (evord2 <= 0) {
      return erg.toString();
    }
    if (evord == evord2) {
      erg.append(".blockOrder(" + evend1 + ")");
      return erg.toString();
    }
    erg.append(".blockOrder(");
    erg.append(evend1 + ",");
    erg.append("Order");
    switch (Scripting.getLang()) {
      case Ruby:
        erg.append("::");
        break;
      case Python:
      default:
        erg.append(".");
    }
    erg.append(toScriptOrder(evord2));
    erg.append(")");
    return erg.toString();
  }

  /** Script and String representation of ExprTermOrder name. */
  public String toScriptOrder(int ev) {
    switch (Scripting.getCAS()) {
      case Math:
        switch (ev) {
          case LEX:
            return "NegativeReverseLexicographic";
          case INVLEX:
            return "ReverseLexicographic";
          case GRLEX:
            return "NegativeDegreeReverseLexicographic";
          case ITDEGLEX: // IGRLEX:
            return "DegreeReverseLexicographic";
          case REVLEX:
            return "NegativeLexicographic";
          case REVILEX:
            return "Lexicographic";
          case REVITDEG: // REVTDEG:
            return "NegativeDegreeLexicographic";
          case REVITDG:
            return "DegreeLexicographic";
          default:
            return "invalid(" + ev + ")";
        }
      case Sage:
        switch (ev) {
          case LEX:
            return "negrevlex";
          case INVLEX:
            return "invlex";
          case GRLEX:
            return "negdegrevlex";
          case ITDEGLEX: // IGRLEX:
            return "degrevlex";
          case REVLEX:
            return "neglex";
          case REVILEX:
            return "lex";
          case REVITDEG: // REVTDEG:
            return "negdeglex";
          case REVITDG:
            return "deglex";
          default:
            return "invalid(" + ev + ")";
        }
      case Singular:
        switch (ev) {
          // case LEX: // missing
          // return "negrevlex";
          case INVLEX:
            return "rp";
          case GRLEX:
            return "ds";
          case ITDEGLEX: // IGRLEX:
            return "dp";
          case REVLEX:
            return "ls";
          case REVILEX:
            return "lp";
          case REVITDEG: // REVTDEG:
            return "Ds";
          case REVITDG:
            return "Dp";
          default:
            return "invalid(" + ev + ")";
        }
      case JAS:
      default:
        switch (ev) {
          case LEX:
            return "LEX";
          case INVLEX:
            return "INVLEX";
          case GRLEX:
            return "GRLEX";
          case IGRLEX:
            return "IGRLEX";
          case REVLEX:
            return "REVLEX";
          case REVILEX:
            return "REVILEX";
          case REVTDEG:
            return "REVTDEG";
          case REVITDG:
            return "REVITDG";
          case ITDEGLEX:
            return "ITDEGLEX";
          case REVITDEG:
            return "REVITDEG";
          default:
            return "invalid(" + ev + ")";
        }
    }
    // return "invalid(" + ev + ")";
  }

  /**
   * Extend variables. Used e.g. in module embedding. Extend ExprTermOrder by k elements.
   * <b>Note:</b> todo distinguish TOP and POT orders.
   *
   * @param r current number of variables.
   * @param k number of variables to extend.
   * @return extended ExprTermOrder.
   */
  public SymbolicTermOrder extend(int r, int k) {
    if (weight != null) {
      IExpr[][] w = new IExpr[weight.length][];
      for (int i = 0; i < weight.length; i++) {
        IExpr[] wi = weight[i];
        IExpr max = F.C0;
        // long min = Long.MAX_VALUE;
        for (int j = 0; j < wi.length; j++) {
          if (S.Greater.ofQ(wi[j], max)) {
            max = wi[j];
          }
          // if ( wi[j] < min ) min = wi[j];
        }
        max = max.inc();
        IExpr[] wj = new IExpr[wi.length + k];
        for (int j = 0; j < i; j++) {
          wj[j] = max;
        }
        System.arraycopy(wi, 0, wj, i, wi.length);
        w[i] = wj;
      }
      return new SymbolicTermOrder(w);
    }
    if (evord2 != 0) {
      // logger.debug("warn: ExprTermOrder is already extended");
      if (debug) {
        throw new IllegalArgumentException("ExprTermOrder is already extended: " + this);
      }
      return new SymbolicTermOrder(evord, evord2, r + k, evend1 + k);
    }
    return new SymbolicTermOrder(DEFAULT_EVORD /* evord */, evord, r + k, k); // don't change to
                                                                              // evord, cause
    // REVITDG
  }

  /**
   * Extend lower variables. Extend ExprTermOrder by k elements. <b>Note:</b> todo distinguish TOP
   * and POT orders.
   *
   * @param r current number of variables.
   * @param k number of variables to extend.
   * @return extended ExprTermOrder.
   */
  public SymbolicTermOrder extendLower(int r, int k) {
    if (weight != null) {
      IExpr[][] w = new IExpr[weight.length][];
      for (int i = 0; i < weight.length; i++) {
        IExpr[] wi = weight[i];
        IExpr min = F.CInfinity;
        for (int j = 0; j < wi.length; j++) {
          if (S.Less.ofQ(wi[j], min)) {
            min = wi[j];
          }
        }
        IExpr[] wj = new IExpr[wi.length + k];
        for (int j = 0; j < i; j++) {
          wj[wi.length + j] = min;
        }
        System.arraycopy(wi, 0, wj, 0, wi.length);
        w[i] = wj;
      }
      return new SymbolicTermOrder(w);
    }
    if (evord2 != 0) {
      if (debug) {
        // logger.warn("ExprTermOrder is already extended");
      }
      return new SymbolicTermOrder(evord, evord2, r + k, evend1 + k);
    }
    return new SymbolicTermOrder(evord);
  }

  /**
   * Contract variables. Used e.g. in module embedding. Contract ExprTermOrder to non split status.
   *
   * @param k position of first element to be copied.
   * @param len new length.
   * @return contracted ExprTermOrder.
   */
  public SymbolicTermOrder contract(int k, int len) {
    if (weight != null) {
      IExpr[][] w = new IExpr[weight.length][];
      for (int i = 0; i < weight.length; i++) {
        IExpr[] wi = weight[i];
        IExpr[] wj = new IExpr[len];
        System.arraycopy(wi, k, wj, 0, len);
        w[i] = wj;
      }
      return new SymbolicTermOrder(w);
    }
    if (evord2 == 0) {
      if (debug) {
        // logger.warn("ExprTermOrder is already contracted");
      }
      return new SymbolicTermOrder(evord);
    }
    if (evend1 > k) { // < IntMax since evord2 != 0
      int el = evend1 - k;
      while (el > len) {
        el -= len;
      }
      if (el == 0L) {
        return new SymbolicTermOrder(evord);
      }
      if (el == len) {
        return new SymbolicTermOrder(evord);
      }
      return new SymbolicTermOrder(evord, evord2, len, el);
    }
    return new SymbolicTermOrder(evord2);
  }

  /**
   * Reverse variables. Used e.g. in opposite rings.
   *
   * @return ExprTermOrder for reversed variables.
   */
  public SymbolicTermOrder reverse() {
    return reverse(false);
  }

  /**
   * Reverse variables. Used e.g. in opposite rings.
   *
   * @param partial true for partialy reversed term orders.
   * @return ExprTermOrder for reversed variables.
   */
  public SymbolicTermOrder reverse(boolean partial) {
    SymbolicTermOrder t;
    if (weight != null) {
      if (partial) {
        // logger.error("partial reversed weight order not implemented");
      }
      IExpr[][] w = new IExpr[weight.length][];
      for (int i = 0; i < weight.length; i++) {
        IExpr[] wi = weight[i];
        IExpr[] wj = new IExpr[wi.length];
        for (int j = 0; j < wj.length; j++) {
          wj[j] = wi[wj.length - 1 - j];
        }
        w[i] = wj;
      }
      t = new SymbolicTermOrder(w);
      // logger.info("reverse = " + t + ", from = " + this);
      return t;
    }
    if (evord2 == 0) {
      t = new SymbolicTermOrder(revert(evord));
      return t;
    }
    if (partial) {
      t = new SymbolicTermOrder(revert(evord), revert(evord2), evend2, evend1);
    } else {
      t = new SymbolicTermOrder(revert(evord2), revert(evord), evend2, evend2 - evbeg2);
    }
    // logger.info("reverse = " + t + ", from = " + this);
    return t;
  }

  /**
   * Revert exponent order. Used e.g. in opposite rings.
   *
   * @param evord exponent order to be reverted.
   * @return reverted exponent order.
   */
  public static int revert(int evord) {
    int i = evord;
    switch (evord) {
      case LEX:
        i = REVLEX;
        break;
      case INVLEX:
        i = REVILEX;
        break;
      case GRLEX:
        i = REVTDEG;
        break;
      case IGRLEX:
        i = REVITDG;
        break;
      case REVLEX:
        i = LEX;
        break;
      case REVILEX:
        i = INVLEX;
        break;
      case REVTDEG:
        i = GRLEX;
        break;
      case REVITDG:
        i = IGRLEX;
        break;
      default: // REVITDEG, ITDEGLEX
        // logger.error("can not revert " + evord);
        break;
    }
    return i;
  }

  /**
   * Permutation of a long array.
   *
   * @param a array of long.
   * @param P permutation.
   * @return P(a).
   */
  public static IExpr[] longArrayPermutation(List<Integer> P, IExpr[] a) {
    if (a == null || a.length <= 1) {
      return a;
    }
    IExpr[] b = new IExpr[a.length];
    int j = 0;
    for (Integer i : P) {
      b[j] = a[i];
      j++;
    }
    return b;
  }

  /**
   * Permutation of the termorder.
   *
   * @param P permutation.
   * @return P(a).
   */
  public SymbolicTermOrder permutation(List<Integer> P) {
    SymbolicTermOrder tord = this;
    if (getEvord2() != 0) {
      // throw new IllegalArgumentException("split term orders not permutable");
      tord = new SymbolicTermOrder(getEvord2());
      // logger.warn("split term order '" + this + "' not permutable, resetting to most base term
      // order "
      // + tord);
    }
    IExpr[][] weight = getWeight();
    if (weight != null) {
      IExpr[][] w = new IExpr[weight.length][];
      for (int i = 0; i < weight.length; i++) {
        w[i] = longArrayPermutation(P, weight[i]);
      }
      tord = new SymbolicTermOrder(w);
    }
    return tord;
  }

  /**
   * Weight ExprTermOrder with reversed weight vectors.
   *
   * @param w weight matrix
   * @return ExprTermOrder with reversed weight vectors
   */
  public static SymbolicTermOrder reverseWeight(IExpr[][] w) {
    if (w == null) {
      // logger.warn("null weight matrix ignored");
      return new SymbolicTermOrder();
    }
    IExpr[][] wr = new IExpr[w.length][];
    for (int j = 0; j < w.length; j++) {
      IExpr[] wj = w[j];
      IExpr[] wrj = new IExpr[wj.length];
      for (int i = 0; i < wj.length; i++) {
        wrj[i] = wj[wj.length - 1 - i];
      }
      wr[j] = wrj;
    }
    return new SymbolicTermOrder(wr);
  }
}
