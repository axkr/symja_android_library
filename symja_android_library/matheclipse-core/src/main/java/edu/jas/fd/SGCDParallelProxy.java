/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import edu.jas.kern.ComputerThreads;
import edu.jas.kern.PreemptingException;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Solvable greatest common divisor parallel proxy. Executes methods from two
 * implementations in parallel and returns the result from the fastest run. Uses
 * timeout on <code>invokeAny()</code> and return fake common divisor <it>1</it>
 * in case of timeout.
 *
 * @author Heinz Kredel
 */

public class SGCDParallelProxy<C extends GcdRingElem<C>> extends GreatestCommonDivisorAbstract<C> {


    private static final Logger logger = Logger.getLogger(SGCDParallelProxy.class);


    private static final boolean debug = logger.isDebugEnabled(); //logger.isInfoEnabled();


    /**
     * GCD engines.
     */
    public final GreatestCommonDivisorAbstract<C> e0;


    public final GreatestCommonDivisorAbstract<C> e1;


    public final GreatestCommonDivisorAbstract<C> e2;


    /**
     * Thread pool.
     */
    protected transient ExecutorService pool;


    /**
     * ParallelProxy constructor.
     *
     * @param cf coefficient ring.
     */
    public SGCDParallelProxy(RingFactory<C> cf, GreatestCommonDivisorAbstract<C> e1,
                             GreatestCommonDivisorAbstract<C> e2) {
        super(cf);
        this.e0 = new GreatestCommonDivisorFake<C>(cf);
        this.e1 = e1;
        this.e2 = e2;
        pool = ComputerThreads.getPool();
        //System.out.println("pool 2 = "+pool);
    }


    /**
     * Get the String representation with gcd engines.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "SGCDParallelProxy[ " + e1.getClass().getName() + ", " + e2.getClass().getName() + " ]";
    }


    /**
     * Left univariate GenSolvablePolynomial greatest common divisor.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return gcd(P, S).
     */
    @Override
    public GenSolvablePolynomial<C> leftBaseGcd(final GenSolvablePolynomial<C> P,
                                                final GenSolvablePolynomial<C> S) {
        if (debug) {
            if (ComputerThreads.NO_THREADS) {
                throw new RuntimeException("this should not happen");
            }
        }
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        // parallel case
        GenSolvablePolynomial<C> g = P.ring.getONE();
        //Callable<GenSolvablePolynomial<C>> c0;
        //Callable<GenSolvablePolynomial<C>> c1;
        List<Callable<GenSolvablePolynomial<C>>> cs = new ArrayList<Callable<GenSolvablePolynomial<C>>>(2);
        cs.add(new Callable<GenSolvablePolynomial<C>>() {


            public GenSolvablePolynomial<C> call() {
                try {
                    //System.out.println("starting e1 " + e1.getClass().getName());
                    GenSolvablePolynomial<C> g = e1.leftBaseGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e1 " + e1.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e1 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e1 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<GenSolvablePolynomial<C>>() {


            public GenSolvablePolynomial<C> call() {
                try {
                    //System.out.println("starting e2 " + e2.getClass().getName());
                    GenSolvablePolynomial<C> g = e2.leftBaseGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e2 " + e2.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e2 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e2 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            if (ComputerThreads.getTimeout() < 0) {
                g = pool.invokeAny(cs);
            } else {
                g = pool.invokeAny(cs, ComputerThreads.getTimeout(), ComputerThreads.getTimeUnit());
            }
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            logger.info("TimeoutException after " + ComputerThreads.getTimeout() + " "
                    + ComputerThreads.getTimeUnit());
            g = e0.leftBaseGcd(P, S); // fake returns 1
        }
        return g;
    }


    /**
     * left univariate GenSolvablePolynomial recursive greatest common divisor.
     *
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return gcd(P, S).
     */
    @Override
    public GenSolvablePolynomial<GenPolynomial<C>> leftRecursiveUnivariateGcd(
            final GenSolvablePolynomial<GenPolynomial<C>> P,
            final GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (debug) {
            if (ComputerThreads.NO_THREADS) {
                throw new RuntimeException("this should not happen");
            }
        }
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        // parallel case
        GenSolvablePolynomial<GenPolynomial<C>> g = P.ring.getONE();
        //Callable<GenSolvablePolynomial<GenPolynomial<C>>> c0;
        //Callable<GenSolvablePolynomial<GenPolynomial<C>>> c1;
        List<Callable<GenSolvablePolynomial<GenPolynomial<C>>>> cs = new ArrayList<Callable<GenSolvablePolynomial<GenPolynomial<C>>>>(
                2);
        cs.add(new Callable<GenSolvablePolynomial<GenPolynomial<C>>>() {


            public GenSolvablePolynomial<GenPolynomial<C>> call() {
                try {
                    GenSolvablePolynomial<GenPolynomial<C>> g = e1.leftRecursiveUnivariateGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e1 " + e1.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e1 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e1 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<GenSolvablePolynomial<GenPolynomial<C>>>() {


            public GenSolvablePolynomial<GenPolynomial<C>> call() {
                try {
                    GenSolvablePolynomial<GenPolynomial<C>> g = e2.leftRecursiveUnivariateGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e2 " + e2.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e2 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e2 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            if (ComputerThreads.getTimeout() < 0) {
                g = pool.invokeAny(cs);
            } else {
                g = pool.invokeAny(cs, ComputerThreads.getTimeout(), ComputerThreads.getTimeUnit());
            }
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            logger.info("TimeoutException after " + ComputerThreads.getTimeout() + " "
                    + ComputerThreads.getTimeUnit());
            g = e0.leftRecursiveUnivariateGcd(P, S); // fake returns 1
        }
        return g;
    }


    /**
     * Left GenSolvablePolynomial greatest common divisor.
     *
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return leftGcd(P, S).
     */
    @Override
    public GenSolvablePolynomial<C> leftGcd(final GenSolvablePolynomial<C> P,
                                            final GenSolvablePolynomial<C> S) {
        if (debug) {
            if (ComputerThreads.NO_THREADS) {
                throw new RuntimeException("this should not happen");
            }
        }
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        // parallel case
        GenSolvablePolynomial<C> g = P.ring.getONE();
        //Callable<GenSolvablePolynomial<C>> c0;
        //Callable<GenSolvablePolynomial<C>> c1;
        List<Callable<GenSolvablePolynomial<C>>> cs = new ArrayList<Callable<GenSolvablePolynomial<C>>>(2);
        cs.add(new Callable<GenSolvablePolynomial<C>>() {


            public GenSolvablePolynomial<C> call() {
                try {
                    //System.out.println("starting e1 " + e1.getClass().getName());
                    GenSolvablePolynomial<C> g = e1.leftGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e1 " + e1.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e1 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e1 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<GenSolvablePolynomial<C>>() {


            public GenSolvablePolynomial<C> call() {
                try {
                    //System.out.println("starting e2 " + e2.getClass().getName());
                    GenSolvablePolynomial<C> g = e2.leftGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e2 " + e2.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e2 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e2 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            if (ComputerThreads.getTimeout() < 0) {
                g = pool.invokeAny(cs);
            } else {
                g = pool.invokeAny(cs, ComputerThreads.getTimeout(), ComputerThreads.getTimeUnit());
            }
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            logger.info("TimeoutException after " + ComputerThreads.getTimeout() + " "
                    + ComputerThreads.getTimeUnit());
            g = e0.leftGcd(P, S); // fake returns 1
        }
        return g;
    }


    /**
     * Right univariate GenSolvablePolynomial greatest common divisor.
     *
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return gcd(P, S).
     */
    @Override
    public GenSolvablePolynomial<C> rightBaseGcd(final GenSolvablePolynomial<C> P,
                                                 final GenSolvablePolynomial<C> S) {
        if (debug) {
            if (ComputerThreads.NO_THREADS) {
                throw new RuntimeException("this should not happen");
            }
        }
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        // parallel case
        GenSolvablePolynomial<C> g = P.ring.getONE();
        //Callable<GenSolvablePolynomial<C>> c0;
        //Callable<GenSolvablePolynomial<C>> c1;
        List<Callable<GenSolvablePolynomial<C>>> cs = new ArrayList<Callable<GenSolvablePolynomial<C>>>(2);
        cs.add(new Callable<GenSolvablePolynomial<C>>() {


            public GenSolvablePolynomial<C> call() {
                try {
                    //System.out.println("starting e1 " + e1.getClass().getName());
                    GenSolvablePolynomial<C> g = e1.rightBaseGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e1 " + e1.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e1 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e1 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<GenSolvablePolynomial<C>>() {


            public GenSolvablePolynomial<C> call() {
                try {
                    //System.out.println("starting e2 " + e2.getClass().getName());
                    GenSolvablePolynomial<C> g = e2.rightBaseGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e2 " + e2.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e2 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e2 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            if (ComputerThreads.getTimeout() < 0) {
                g = pool.invokeAny(cs);
            } else {
                g = pool.invokeAny(cs, ComputerThreads.getTimeout(), ComputerThreads.getTimeUnit());
            }
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            logger.info("TimeoutException after " + ComputerThreads.getTimeout() + " "
                    + ComputerThreads.getTimeUnit());
            g = e0.rightBaseGcd(P, S); // fake returns 1
        }
        return g;
    }


    /**
     * right univariate GenSolvablePolynomial recursive greatest common divisor.
     *
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return gcd(P, S).
     */
    @Override
    public GenSolvablePolynomial<GenPolynomial<C>> rightRecursiveUnivariateGcd(
            final GenSolvablePolynomial<GenPolynomial<C>> P,
            final GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (debug) {
            if (ComputerThreads.NO_THREADS) {
                throw new RuntimeException("this should not happen");
            }
        }
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        // parallel case
        GenSolvablePolynomial<GenPolynomial<C>> g = P.ring.getONE();
        //Callable<GenSolvablePolynomial<GenPolynomial<C>>> c0;
        //Callable<GenSolvablePolynomial<GenPolynomial<C>>> c1;
        List<Callable<GenSolvablePolynomial<GenPolynomial<C>>>> cs = new ArrayList<Callable<GenSolvablePolynomial<GenPolynomial<C>>>>(
                2);
        cs.add(new Callable<GenSolvablePolynomial<GenPolynomial<C>>>() {


            public GenSolvablePolynomial<GenPolynomial<C>> call() {
                try {
                    GenSolvablePolynomial<GenPolynomial<C>> g = e1.rightRecursiveUnivariateGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e1 " + e1.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e1 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e1 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<GenSolvablePolynomial<GenPolynomial<C>>>() {


            public GenSolvablePolynomial<GenPolynomial<C>> call() {
                try {
                    GenSolvablePolynomial<GenPolynomial<C>> g = e2.rightRecursiveUnivariateGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e2 " + e2.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e2 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e2 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            if (ComputerThreads.getTimeout() < 0) {
                g = pool.invokeAny(cs);
            } else {
                g = pool.invokeAny(cs, ComputerThreads.getTimeout(), ComputerThreads.getTimeUnit());
            }
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            logger.info("TimeoutException after " + ComputerThreads.getTimeout() + " "
                    + ComputerThreads.getTimeUnit());
            g = e0.rightRecursiveUnivariateGcd(P, S); // fake returns 1
        }
        return g;
    }


    /**
     * Right GenSolvablePolynomial greatest common divisor.
     *
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return rightGcd(P, S).
     */
    @Override
    public GenSolvablePolynomial<C> rightGcd(final GenSolvablePolynomial<C> P,
                                             final GenSolvablePolynomial<C> S) {
        if (debug) {
            if (ComputerThreads.NO_THREADS) {
                throw new RuntimeException("this should not happen");
            }
        }
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        // parallel case
        GenSolvablePolynomial<C> g = P.ring.getONE();
        //Callable<GenSolvablePolynomial<C>> c0;
        //Callable<GenSolvablePolynomial<C>> c1;
        List<Callable<GenSolvablePolynomial<C>>> cs = new ArrayList<Callable<GenSolvablePolynomial<C>>>(2);
        cs.add(new Callable<GenSolvablePolynomial<C>>() {


            public GenSolvablePolynomial<C> call() {
                try {
                    //System.out.println("starting e1 " + e1.getClass().getName());
                    GenSolvablePolynomial<C> g = e1.rightGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e1 " + e1.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e1 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e1 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<GenSolvablePolynomial<C>>() {


            public GenSolvablePolynomial<C> call() {
                try {
                    //System.out.println("starting e2 " + e2.getClass().getName());
                    GenSolvablePolynomial<C> g = e2.rightGcd(P, S);
                    if (debug) {
                        logger.info("SGCDParallelProxy done e2 " + e2.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGCDParallelProxy e2 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGCDParallelProxy e2 " + e);
                    logger.info("SGCDParallelProxy P = " + P);
                    logger.info("SGCDParallelProxy S = " + S);
                    throw new RuntimeException("SGCDParallelProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            if (ComputerThreads.getTimeout() < 0) {
                g = pool.invokeAny(cs);
            } else {
                g = pool.invokeAny(cs, ComputerThreads.getTimeout(), ComputerThreads.getTimeUnit());
            }
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            logger.info("TimeoutException after " + ComputerThreads.getTimeout() + " "
                    + ComputerThreads.getTimeUnit());
            g = e0.rightGcd(P, S); // fake returns 1
        }
        return g;
    }

}
