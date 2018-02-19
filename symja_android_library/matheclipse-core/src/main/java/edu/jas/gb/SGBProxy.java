/*
 * $Id$
 */

package edu.jas.gb;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import edu.jas.kern.ComputerThreads;
import edu.jas.kern.PreemptingException;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Groebner bases parallel proxy.
 *
 * @author Heinz Kredel
 */

public class SGBProxy<C extends GcdRingElem<C>> extends SolvableGroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(SGBProxy.class);


    private static final boolean debug = logger.isDebugEnabled(); //logger.isInfoEnabled();


    /**
     * GB engines.
     */
    public final SolvableGroebnerBaseAbstract<C> e1;


    public final SolvableGroebnerBaseAbstract<C> e2;


    /**
     * Thread pool.
     */
    protected transient ExecutorService pool;


    /**
     * Proxy constructor.
     *
     * @param e1 Groebner base engine.
     * @param e2 Groebner base engine.
     */
    public SGBProxy(SolvableGroebnerBaseAbstract<C> e1, SolvableGroebnerBaseAbstract<C> e2) {
        this.e1 = e1;
        this.e2 = e2;
        pool = ComputerThreads.getPool();
        //System.out.println("pool 2 = "+pool);
    }


    /**
     * Get the String representation with GB engines.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "SGBProxy[ " + e1.toString() + ", " + e2.toString() + " ]";
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    @Override
    public void terminate() {
        e1.terminate();
        e2.terminate();
    }


    /**
     * Cancel ThreadPool.
     */
    @Override
    public int cancel() {
        int s = e1.cancel();
        s += e2.cancel();
        return s;
    }


    /**
     * Groebner base.
     *
     * @param modv module variable number.
     * @param F    polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    @Override
    public List<GenSolvablePolynomial<C>> leftGB(final int modv, final List<GenSolvablePolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return F;
        }
        // parallel case
        List<GenSolvablePolynomial<C>> G = null;
        List<Callable<List<GenSolvablePolynomial<C>>>> cs = new ArrayList<Callable<List<GenSolvablePolynomial<C>>>>(
                2);
        cs.add(new Callable<List<GenSolvablePolynomial<C>>>() {


            public List<GenSolvablePolynomial<C>> call() {
                try {
                    //System.out.println("starting e1 " + e1.getClass().getName());
                    List<GenSolvablePolynomial<C>> G = e1.leftGB(modv, F);
                    if (debug) {
                        logger.info("SGBProxy done e1 " + e1.getClass().getName());
                    }
                    return G;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGBProxy e1 preempted " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGBProxy e1 " + e);
                    logger.info("Exception SGBProxy F = " + F);
                    throw new RuntimeException("SGBProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<List<GenSolvablePolynomial<C>>>() {


            public List<GenSolvablePolynomial<C>> call() {
                try {
                    //System.out.println("starting e2 " + e2.getClass().getName());
                    List<GenSolvablePolynomial<C>> G = e2.leftGB(modv, F);
                    if (debug) {
                        logger.info("SGBProxy done e2 " + e2.getClass().getName());
                    }
                    return G;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGBProxy e2 preempted " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGBProxy e2 " + e);
                    logger.info("Exception SGBProxy F = " + F);
                    throw new RuntimeException("SGBProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            G = pool.invokeAny(cs);
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        }
        return G;
    }


    /**
     * Right Groebner base.
     *
     * @param modv module variable number.
     * @param F    polynomial list.
     * @return rightGB(F) a Groebner base of F.
     */
    @Override
    public List<GenSolvablePolynomial<C>> rightGB(final int modv, final List<GenSolvablePolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return F;
        }
        // parallel case
        List<GenSolvablePolynomial<C>> G = null;
        List<Callable<List<GenSolvablePolynomial<C>>>> cs = new ArrayList<Callable<List<GenSolvablePolynomial<C>>>>(
                2);
        cs.add(new Callable<List<GenSolvablePolynomial<C>>>() {


            public List<GenSolvablePolynomial<C>> call() {
                try {
                    //System.out.println("starting e1 " + e1.getClass().getName());
                    List<GenSolvablePolynomial<C>> G = e1.rightGB(modv, F);
                    if (debug) {
                        logger.info("SGBProxy done e1 " + e1.getClass().getName());
                    }
                    return G;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGBProxy e1 preempted " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGBProxy e1 " + e);
                    logger.info("Exception SGBProxy F = " + F);
                    throw new RuntimeException("SGBProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<List<GenSolvablePolynomial<C>>>() {


            public List<GenSolvablePolynomial<C>> call() {
                try {
                    //System.out.println("starting e2 " + e2.getClass().getName());
                    List<GenSolvablePolynomial<C>> G = e2.rightGB(modv, F);
                    if (debug) {
                        logger.info("SGBProxy done e2 " + e2.getClass().getName());
                    }
                    return G;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGBProxy e2 preempted " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGBProxy e2 " + e);
                    logger.info("Exception SGBProxy F = " + F);
                    throw new RuntimeException("SGBProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            G = pool.invokeAny(cs);
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        }
        return G;
    }


    /**
     * Groebner base.
     *
     * @param modv module variable number.
     * @param F    polynomial list.
     * @return twosidedGB(F) a Groebner base of F.
     */
    @Override
    public List<GenSolvablePolynomial<C>> twosidedGB(final int modv, final List<GenSolvablePolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return F;
        }
        // parallel case
        List<GenSolvablePolynomial<C>> G = null;
        List<Callable<List<GenSolvablePolynomial<C>>>> cs = new ArrayList<Callable<List<GenSolvablePolynomial<C>>>>(
                2);
        cs.add(new Callable<List<GenSolvablePolynomial<C>>>() {


            public List<GenSolvablePolynomial<C>> call() {
                try {
                    //System.out.println("starting e1 " + e1.getClass().getName());
                    List<GenSolvablePolynomial<C>> G = e1.twosidedGB(modv, F);
                    if (debug) {
                        logger.info("SGBProxy done e1 " + e1.getClass().getName());
                    }
                    return G;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGBProxy e1 preempted " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGBProxy e1 " + e);
                    logger.info("Exception SGBProxy F = " + F);
                    throw new RuntimeException("SGBProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<List<GenSolvablePolynomial<C>>>() {


            public List<GenSolvablePolynomial<C>> call() {
                try {
                    //System.out.println("starting e2 " + e2.getClass().getName());
                    List<GenSolvablePolynomial<C>> G = e2.twosidedGB(modv, F);
                    if (debug) {
                        logger.info("SGBProxy done e2 " + e2.getClass().getName());
                    }
                    return G;
                } catch (PreemptingException e) {
                    throw new RuntimeException("SGBProxy e2 preempted " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("SGBProxy e2 " + e);
                    logger.info("Exception SGBProxy F = " + F);
                    throw new RuntimeException("SGBProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            G = pool.invokeAny(cs);
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        }
        return G;
    }

}
