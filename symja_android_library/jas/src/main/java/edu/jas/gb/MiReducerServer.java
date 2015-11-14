/*
 * $Id$
 */

package edu.jas.gb;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;

/**
 * Distributed server reducing worker threads for minimal GB Not jet distributed
 * but threaded.
 */

class MiReducerServer<C extends RingElem<C>> implements Runnable {


    private final List<GenPolynomial<C>> G;


    private GenPolynomial<C> H;


    private final Semaphore done = new Semaphore(0);


    private final Reduction<C> red;


    private static final Logger logger = Logger.getLogger(MiReducerServer.class);


    MiReducerServer(List<GenPolynomial<C>> G, GenPolynomial<C> p) {
        this.G = G;
        H = p;
        red = new ReductionPar<C>();
    }


    /**
     * getNF. Blocks until the normal form is computed.
     * @return the computed normal form.
     */
    public GenPolynomial<C> getNF() {
        try {
            done.acquire(); //done.P();
        } catch (InterruptedException e) {
        }
        return H;
    }


    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("ht(H) = " + H.leadingExpVector());
        }
        H = red.normalform(G, H); //mod
        done.release(); //done.V();
        if (logger.isDebugEnabled()) {
            logger.debug("ht(H) = " + H.leadingExpVector());
        }
        // H = H.monic();
    }
}


/**
 * Distributed clients reducing worker threads for minimal GB. <b>Note:</b> Not
 * jet used.
 */

class MiReducerClient<C extends RingElem<C>> implements Runnable {


    private final List<GenPolynomial<C>> G;


    private GenPolynomial<C> H;


    private final Reduction<C> red;


    private final Semaphore done = new Semaphore(0);


    private static final Logger logger = Logger.getLogger(MiReducerClient.class);


    MiReducerClient(List<GenPolynomial<C>> G, GenPolynomial<C> p) {
        this.G = G;
        H = p;
        red = new ReductionPar<C>();
    }


    /**
     * getNF. Blocks until the normal form is computed.
     * @return the computed normal form.
     */
    public GenPolynomial<C> getNF() {
        try {
            done.acquire(); //done.P();
        } catch (InterruptedException u) {
            Thread.currentThread().interrupt();
        }
        return H;
    }


    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("ht(S) = " + H.leadingExpVector());
        }
        H = red.normalform(G, H); //mod
        done.release(); //done.V();
        if (logger.isDebugEnabled()) {
            logger.debug("ht(H) = " + H.leadingExpVector());
        }
        // H = H.monic();
    }
}
