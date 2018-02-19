/*
 * $Id$
 */

package edu.jas.poly;


import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import edu.jas.kern.Scripting;


/**
 * Term order class for ordered polynomials. Implements the most used term
 * orders: graded, lexicographical, weight aray and block orders. For the
 * definitions see for example the articles <a
 * href="http://doi.acm.org/10.1145/43882.43887">Kredel
 * "Admissible term orderings used in computer algebra systems"</a> and <a
 * href="http://doi.acm.org/10.1145/70936.70941">Sit,
 * "Some comments on term-ordering in Gr&ouml;bner basis computations"</a>.
 * <b>Note: </b> the naming is not quite easy to understand: in case of doubt
 * use the term orders with "I" in the name, like IGRLEX (the default) or
 * INVLEX. Not all algorithms may work with all term orders since not all are
 * well-founded, so watch your step. This class does not implement orders by
 * linear forms over Q[t]. Objects of this class are immutable.
 *
 * @author Heinz Kredel
 */

public final class TermOrder implements Serializable {


    public static final int LEX = 1;
    public final static int MIN_EVORD = LEX;


    // TermOrder index values
    public static final int INVLEX = 2;
    public static final int GRLEX = 3;
    public static final int IGRLEX = 4;
    public static final int REVLEX = 5;
    public static final int REVILEX = 6;
    public static final int REVTDEG = 7;
    public static final int REVITDG = 8;
    public static final int ITDEGLEX = 9;
    public static final int REVITDEG = 10;
    public final static int MAX_EVORD = REVITDEG;
    public final static int DEFAULT_EVORD = IGRLEX;
    private static final Logger logger = Logger.getLogger(TermOrder.class);
    private static final boolean debug = logger.isDebugEnabled();


    //public final static int DEFAULT_EVORD = INVLEX;


    // instance variables
    private final int evord;


    // for split termorders
    private final int evord2;


    private final int evbeg1;


    private final int evend1;


    private final int evbeg2;


    private final int evend2;


    /**
     * Defined array of weight vectors.
     */
    private final long[][] weight;


    /**
     * Defined descending order comparator. Sorts the highest terms first.
     */
    private final EVComparator horder;


    /**
     * Defined ascending order comparator. Sorts the lowest terms first.
     */
    private final EVComparator lorder;


    /**
     * Defined sugar order comparator. Sorts the graded lowest terms first.
     */
    private final EVComparator sugar;


    /**
     * Constructor for default term order.
     */
    public TermOrder() {
        this(DEFAULT_EVORD);
    }


    /**
     * Constructor for given term order.
     *
     * @param evord requested term order indicator / enumerator.
     */
    public TermOrder(int evord) {
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
            case TermOrder.LEX: {
                horder = new EVComparator() {


                    @Override
                    public int compare(ExpVector e1, ExpVector e2) {
                        return ExpVector.EVILCP(e1, e2);
                    }
                };
                break;
            }
            case TermOrder.INVLEX: {
                horder = new EVComparator() {


                    @Override
                    public int compare(ExpVector e1, ExpVector e2) {
                        return -ExpVector.EVILCP(e1, e2);
                    }
                };
                break;
            }
            case TermOrder.GRLEX: {
                horder = new EVComparator() {


                    @Override
                    public int compare(ExpVector e1, ExpVector e2) {
                        return ExpVector.EVIGLC(e1, e2);
                    }
                };
                break;
            }
            case TermOrder.IGRLEX: {
                horder = new EVComparator() {


                    @Override
                    public int compare(ExpVector e1, ExpVector e2) {
                        return -ExpVector.EVIGLC(e1, e2);
                    }
                };
                break;
            }
            case TermOrder.REVLEX: {
                horder = new EVComparator() {


                    @Override
                    public int compare(ExpVector e1, ExpVector e2) {
                        return ExpVector.EVRILCP(e1, e2);
                    }
                };
                break;
            }
            case TermOrder.REVILEX: {
                horder = new EVComparator() {


                    @Override
                    public int compare(ExpVector e1, ExpVector e2) {
                        return -ExpVector.EVRILCP(e1, e2);
                    }
                };
                break;
            }
            case TermOrder.REVTDEG: {
                horder = new EVComparator() {


                    @Override
                    public int compare(ExpVector e1, ExpVector e2) {
                        return ExpVector.EVRIGLC(e1, e2);
                    }
                };
                break;
            }
            case TermOrder.REVITDG: {
                horder = new EVComparator() {


                    @Override
                    public int compare(ExpVector e1, ExpVector e2) {
                        return -ExpVector.EVRIGLC(e1, e2);
                    }
                };
                break;
            }
            case TermOrder.ITDEGLEX: {
                horder = new EVComparator() {


                    @Override
                    public int compare(ExpVector e1, ExpVector e2) {
                        return -ExpVector.EVITDEGLC(e1, e2); // okay +/-
                    }
                };
                break;
            }
            case TermOrder.REVITDEG: {
                horder = new EVComparator() {


                    @Override
                    public int compare(ExpVector e1, ExpVector e2) {
                        return ExpVector.EVRLITDEGC(e1, e2); // okay +/-
                    }
                };
                break;
            }
            default: {
                horder = null;
            }
        }
        if (horder == null) {
            throw new IllegalArgumentException("invalid term order: " + evord);
        }

        // lorder = new EVlorder();
        lorder = new EVComparator() {


            @Override
            public int compare(ExpVector e1, ExpVector e2) {
                return -horder.compare(e1, e2);
            }
        };

        // sugar = new EVsugar();
        sugar = new EVComparator() {


            @Override
            public int compare(ExpVector e1, ExpVector e2) {
                return ExpVector.EVIGLC(e1, e2);
            }
        };
    }


    /**
     * Constructor for given exponent weights.
     *
     * @param w weight vector of longs.
     */
    public TermOrder(long[] w) {
        this(new long[][]{w});
    }


    /**
     * Constructor for given exponent weights.
     *
     * @param w weight array of longs.
     */
    public TermOrder(long[][] w) {
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
            public int compare(ExpVector e1, ExpVector e2) {
                return -ExpVector.EVIWLC(weight, e1, e2);
            }
        };

        // lorder = new EVlorder();
        lorder = new EVComparator() {


            @Override
            public int compare(ExpVector e1, ExpVector e2) {
                return +ExpVector.EVIWLC(weight, e1, e2);
                // return - horder.compare( e1, e2 );
            }
        };

        // sugar = new EVsugar();
        sugar = horder;
    }


    /**
     * Constructor for given split order.
     *
     * @param ev1   requested term order indicator for first block.
     * @param ev2   requested term order indicator for second block.
     * @param r     max number of exponents to compare.
     * @param split index.
     */
    public TermOrder(int ev1, int ev2, int r, int split) {
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
            throw new IllegalArgumentException("invalid term order split, r = " + r + ", split = " + split);
        }
        //System.out.println("evbeg2 " + evbeg2 + ", evend2 " + evend2);
        switch (evord) { // horder = new EVhorder();
            case TermOrder.LEX: {
                switch (evord2) {
                    case TermOrder.LEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.INVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.GRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.IGRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
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
            case TermOrder.INVLEX: {
                switch (evord2) {
                    case TermOrder.LEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.INVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.GRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.IGRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVILEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVTDEG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVITDG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
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
            case TermOrder.GRLEX: {
                switch (evord2) {
                    case TermOrder.LEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.INVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.GRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.IGRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
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
            case TermOrder.IGRLEX: {
                switch (evord2) {
                    case TermOrder.LEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.INVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.GRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.IGRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVILEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVTDEG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVITDG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
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
            //----- begin reversed -----------
            case TermOrder.REVLEX: {
                switch (evord2) {
                    case TermOrder.LEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.INVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.GRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.IGRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVILEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVTDEG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVITDG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
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
            case TermOrder.REVILEX: {
                switch (evord2) {
                    case TermOrder.LEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.INVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.GRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.IGRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVILEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVTDEG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVITDG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRILCP(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
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
            case TermOrder.REVTDEG: {
                switch (evord2) {
                    case TermOrder.LEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.INVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.GRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.IGRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVILEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVTDEG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVITDG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
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
            case TermOrder.REVITDG: {
                switch (evord2) {
                    case TermOrder.LEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.INVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.GRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.IGRLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVLEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVILEX: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRILCP(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVTDEG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
                            }
                        };
                        break;
                    }
                    case TermOrder.REVITDG: {
                        horder = new EVComparator() {


                            @Override
                            public int compare(ExpVector e1, ExpVector e2) {
                                int t = -ExpVector.EVRIGLC(e1, e2, evbeg1, evend1);
                                if (t != 0) {
                                    return t;
                                }
                                return -ExpVector.EVRIGLC(e1, e2, evbeg2, evend2);
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
            //----- end reversed-----------
            default: {
                horder = null;
            }
        }
        if (horder == null) {
            throw new IllegalArgumentException("invalid term order: " + evord + " 2 " + evord2);
        }

        lorder = new EVComparator() {


            @Override
            public int compare(ExpVector e1, ExpVector e2) {
                return -horder.compare(e1, e2);
            }
        };

        // sugar = new EVsugar();
        sugar = new EVComparator() {


            @Override
            public int compare(ExpVector e1, ExpVector e2) {
                return ExpVector.EVIGLC(e1, e2);
            }
        };
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
                logger.error("can not revert " + evord);
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
    public static long[] longArrayPermutation(List<Integer> P, long[] a) {
        if (a == null || a.length <= 1) {
            return a;
        }
        long[] b = new long[a.length];
        int j = 0;
        for (Integer i : P) {
            b[j] = a[i];
            j++;
        }
        return b;
    }


    /*
     * Constructor for default split order.
     * @param r max number of exponents to compare.
     * @param split index.
    public TermOrder(int r, int split) {
        this(DEFAULT_EVORD, DEFAULT_EVORD, r, split);
    }
     */

    /**
     * Weight TermOrder with reversed weight vectors.
     *
     * @param w weight matrix
     * @return TermOrder with reversed weight vectors
     */
    public static TermOrder reverseWeight(long[][] w) {
        if (w == null) {
            logger.warn("null weight matrix ignored");
            return new TermOrder();
        }
        long[][] wr = new long[w.length][];
        for (int j = 0; j < w.length; j++) {
            long[] wj = w[j];
            //System.out.println("reverseWeight: " + wj);
            long[] wrj = new long[wj.length];
            for (int i = 0; i < wj.length; i++) {
                wrj[i] = wj[wj.length - 1 - i];
            }
            wr[j] = wrj;
        }
        return new TermOrder(wr);
    }

    /**
     * Test if this term order is a split order.
     *
     * @return true if this is a split term order, else false.
     */
    public boolean isSplit() {
        //System.out.println("isSplit: " + evend2 + " == " + evbeg2);
        if (evend2 == evbeg2 || evend1 == Integer.MAX_VALUE) {
            return false;
        }
        return true;
    }

    /**
     * Create block term order at split index.
     *
     * @param s split index.
     * @return block TermOrder with split index.
     */
    public TermOrder blockOrder(int s) {
        return blockOrder(s, Integer.MAX_VALUE);
    }

    /**
     * Create block term order at split index.
     *
     * @param s   split index.
     * @param len length of ExpVectors to compare
     * @return block TermOrder with split index.
     */
    public TermOrder blockOrder(int s, int len) {
        return new TermOrder(evord, evord, len, s);
    }

    /**
     * Create block term order at split index.
     *
     * @param s split index.
     * @param t second term order.
     * @return block TermOrder with split index.
     */
    public TermOrder blockOrder(int s, TermOrder t) {
        return blockOrder(s, t, Integer.MAX_VALUE);
    }

    /**
     * Create block term order at split index.
     *
     * @param s   split index.
     * @param t   second term order.
     * @param len length of ExpVectors to compare
     * @return block TermOrder with split index.
     */
    public TermOrder blockOrder(int s, TermOrder t, int len) {
        return new TermOrder(evord, t.evord, len, s);
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
     * Get the exponent vector size.
     * <b>Note:</b> can be INTEGER.MAX_VALUE.
     *
     * @return size.
     */
    public int getSize() {
        return evend2; // can be INTEGER.MAX_VALUE
    }

    /**
     * Get the weight array.
     *
     * @return weight.
     */
    public long[][] getWeight() {
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
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof TermOrder)) {
            return false;
        }
        TermOrder b = (TermOrder) B;
        boolean t = evord == b.getEvord() && evord2 == b.evord2 && evbeg1 == b.evbeg1 && evend1 == b.evend1
                && evbeg2 == b.evbeg2 && evend2 == b.evend2;
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
     * @see Object#hashCode()
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
        StringBuffer erg = new StringBuffer();
        if (weight != null) {
            erg.append("(");
            for (int j = 0; j < weight.length; j++) {
                if (j > 0) {
                    erg.append(",");
                }
                long[] wj = weight[j];
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
        StringBuffer erg = new StringBuffer();
        if (weight != null) {
            erg.append("[");
            for (int j = 0; j < weight.length; j++) {
                if (j > 0) {
                    erg.append(",");
                }
                long[] wj = weight[j];
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
     * String representation of TermOrder.
     *
     * @return script representation of TermOrder.
     */
    public String toScript() {
        // cases Python and Ruby
        if (weight != null) {
            StringBuffer erg = new StringBuffer();
            //erg.append("TermOrder( ");
            erg.append(weightToScript());
            if (evend1 == evend2) {
                //erg.append(" )");
                return erg.toString();
            }
            erg.append("[" + evbeg1 + "," + evend1 + "]");
            erg.append("[" + evbeg2 + "," + evend2 + "]");
            //erg.append(" )");
            return erg.toString();
        }
        return toScriptPlain();
    }

    /**
     * String representation of TermOrder.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        if (weight != null) {
            StringBuffer erg = new StringBuffer();
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

    /**
     * String representation of TermOrder without prefix and weight matrix.
     */
    public String toStringPlain() {
        StringBuffer erg = new StringBuffer();
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

    /**
     * Script representation of TermOrder without prefix and weight matrix.
     */
    public String toScriptPlain() {
        StringBuffer erg = new StringBuffer();
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

    /**
     * Script and String representation of TermOrder name.
     */
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
                    case ITDEGLEX: //IGRLEX:
                        return "DegreeReverseLexicographic";
                    case REVLEX:
                        return "NegativeLexicographic";
                    case REVILEX:
                        return "Lexicographic";
                    case REVITDEG: //REVTDEG:
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
                    case ITDEGLEX: //IGRLEX:
                        return "degrevlex";
                    case REVLEX:
                        return "neglex";
                    case REVILEX:
                        return "lex";
                    case REVITDEG: //REVTDEG:
                        return "negdeglex";
                    case REVITDG:
                        return "deglex";
                    default:
                        return "invalid(" + ev + ")";
                }
            case Singular:
                switch (ev) {
                    //case LEX: // missing
                    //return "negrevlex";
                    case INVLEX:
                        return "rp";
                    case GRLEX:
                        return "ds";
                    case ITDEGLEX: //IGRLEX:
                        return "dp";
                    case REVLEX:
                        return "ls";
                    case REVILEX:
                        return "lp";
                    case REVITDEG: //REVTDEG:
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
        //return "invalid(" + ev + ")";
    }

    /**
     * Extend variables. Used e.g. in module embedding. Extend TermOrder by k
     * elements. <b>Note:</b> todo distinguish TOP and POT orders.
     *
     * @param r current number of variables.
     * @param k number of variables to extend.
     * @return extended TermOrder.
     */
    public TermOrder extend(int r, int k) {
        if (weight != null) {
            long[][] w = new long[weight.length][];
            for (int i = 0; i < weight.length; i++) {
                long[] wi = weight[i];
                long max = 0;
                // long min = Long.MAX_VALUE;
                for (int j = 0; j < wi.length; j++) {
                    if (wi[j] > max)
                        max = wi[j];
                    //if ( wi[j] < min ) min = wi[j];
                }
                max++;
                long[] wj = new long[wi.length + k];
                for (int j = 0; j < i; j++) {
                    wj[j] = max;
                }
                System.arraycopy(wi, 0, wj, i, wi.length);
                w[i] = wj;
            }
            return new TermOrder(w);
        }
        if (evord2 != 0) {
            logger.debug("warn: TermOrder is already extended");
            if (debug) {
                throw new IllegalArgumentException("TermOrder is already extended: " + this);
            }
            return new TermOrder(evord, evord2, r + k, evend1 + k);
        }
        //System.out.println("evord         = " + evord);
        //System.out.println("DEFAULT_EVORD = " + DEFAULT_EVORD);
        //System.out.println("tord          = " + this);
        return new TermOrder(DEFAULT_EVORD/*evord*/, evord, r + k, k); // don't change to evord, cause REVITDG
    }

    /**
     * Extend lower variables. Extend TermOrder by k elements. <b>Note:</b> todo
     * distinguish TOP and POT orders.
     *
     * @param r current number of variables.
     * @param k number of variables to extend.
     * @return extended TermOrder.
     */
    public TermOrder extendLower(int r, int k) {
        if (weight != null) {
            long[][] w = new long[weight.length][];
            for (int i = 0; i < weight.length; i++) {
                long[] wi = weight[i];
                //long max = 0;
                long min = Long.MAX_VALUE;
                for (int j = 0; j < wi.length; j++) {
                    //if ( wi[j] > max ) max = wi[j];
                    if (wi[j] < min)
                        min = wi[j];
                }
                //max++;
                long[] wj = new long[wi.length + k];
                for (int j = 0; j < i; j++) {
                    wj[wi.length + j] = min;
                }
                System.arraycopy(wi, 0, wj, 0, wi.length);
                w[i] = wj;
            }
            return new TermOrder(w);
        }
        if (evord2 != 0) {
            if (debug) {
                logger.warn("TermOrder is already extended");
            }
            return new TermOrder(evord, evord2, r + k, evend1 + k);
        }
        //System.out.println("evord         = " + evord);
        //System.out.println("DEFAULT_EVORD = " + DEFAULT_EVORD);
        //System.out.println("tord          = " + this);
        return new TermOrder(evord);
    }

    /**
     * Contract variables. Used e.g. in module embedding. Contract TermOrder to
     * non split status.
     *
     * @param k   position of first element to be copied.
     * @param len new length.
     * @return contracted TermOrder.
     */
    public TermOrder contract(int k, int len) {
        if (weight != null) {
            long[][] w = new long[weight.length][];
            for (int i = 0; i < weight.length; i++) {
                long[] wi = weight[i];
                long[] wj = new long[len];
                System.arraycopy(wi, k, wj, 0, len);
                w[i] = wj;
            }
            return new TermOrder(w);
        }
        if (evord2 == 0) {
            if (debug) {
                logger.warn("TermOrder is already contracted");
            }
            return new TermOrder(evord);
        }
        if (evend1 > k) { // < IntMax since evord2 != 0
            int el = evend1 - k;
            while (el > len) {
                el -= len;
            }
            if (el == 0L) {
                return new TermOrder(evord);
            }
            if (el == len) {
                return new TermOrder(evord);
            }
            return new TermOrder(evord, evord2, len, el);
        }
        return new TermOrder(evord2);
    }

    /**
     * Reverse variables. Used e.g. in opposite rings.
     *
     * @return TermOrder for reversed variables.
     */
    public TermOrder reverse() {
        return reverse(false);
    }

    /**
     * Reverse variables. Used e.g. in opposite rings.
     *
     * @param partial true for partialy reversed term orders.
     * @return TermOrder for reversed variables.
     */
    public TermOrder reverse(boolean partial) {
        TermOrder t;
        if (weight != null) {
            if (partial) {
                logger.error("partial reversed weight order not implemented");
            }
            long[][] w = new long[weight.length][];
            for (int i = 0; i < weight.length; i++) {
                long[] wi = weight[i];
                long[] wj = new long[wi.length];
                for (int j = 0; j < wj.length; j++) {
                    wj[j] = wi[wj.length - 1 - j];
                }
                w[i] = wj;
            }
            t = new TermOrder(w);
            logger.info("reverse = " + t + ", from = " + this);
            return t;
        }
        if (evord2 == 0) {
            t = new TermOrder(revert(evord));
            return t;
        }
        if (partial) {
            t = new TermOrder(revert(evord), revert(evord2), evend2, evend1);
        } else {
            t = new TermOrder(revert(evord2), revert(evord), evend2, evend2 - evbeg2);
        }
        logger.info("reverse = " + t + ", from = " + this);
        return t;
    }

    /**
     * Permutation of the termorder.
     *
     * @param P permutation.
     * @return P(a).
     */
    public TermOrder permutation(List<Integer> P) {
        TermOrder tord = this;
        if (getEvord2() != 0) {
            //throw new IllegalArgumentException("split term orders not permutable");
            tord = new TermOrder(getEvord2());
            logger.warn("split term order '" + this + "' not permutable, resetting to most base term order "
                    + tord);
        }
        long[][] weight = getWeight();
        if (weight != null) {
            long[][] w = new long[weight.length][];
            for (int i = 0; i < weight.length; i++) {
                w[i] = longArrayPermutation(P, weight[i]);
            }
            tord = new TermOrder(w);
        }
        return tord;
    }

    /**
     * Comparator for ExpVectors.
     */
    public static abstract class EVComparator implements Comparator<ExpVector>, Serializable {


        public abstract int compare(ExpVector e1, ExpVector e2);
    }

}
