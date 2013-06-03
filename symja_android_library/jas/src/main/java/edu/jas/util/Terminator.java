/*
 * $Id$
 */

package edu.jas.util;


import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;


//import edu.unima.ky.parallel.Semaphore;

/**
 * Terminating helper class. Like a barrier, but with coming and going.
 * @author Heinz Kredel
 */

public class Terminator {


    private static final Logger logger = Logger.getLogger(Terminator.class);


    private final int workers;


    private int idler = 0;


    private final Semaphore fin;


    private /*volatile*/ boolean done;


    /**
     * Terminator.
     * @param workers number of expected threads.
     */
    public Terminator(int workers) {
        this.workers = workers;
        fin = new Semaphore(0);
        done = false;
        logger.info("constructor, workers = " + workers);
    }


    /**
     * to string
     */
    @Override
    public String toString() {
        return "Terminator(" + done + ",workers=" + workers + ",idler=" + idler + ")";
    }


    /**
     * beIdle.
     * Checks for release().
     */
    public synchronized void beIdle() {
        idler++;
        logger.info("beIdle, idler = " + idler);
        if (idler >= workers) {
            done = true;
            fin.release(); //fin.V();
        }
    }


    /**
     * initIdle.
     * No check for release().
     * @param i number of idle threads.
     */
    public synchronized void initIdle(int i) {
        idler += i;
        logger.info("initIdle, idler = " + idler);
        if ( idler > workers ) {
            if (done) {
                idler = workers;
            } else {
                throw new RuntimeException("idler > workers");
            }
        }
    }


    /**
     * beIdle.
     * Checks for release().
     * @param i number of idle threads.
     */
    public synchronized void beIdle(int i) {
        idler += i;
        logger.info("beIdle, idler = " + idler);
        if (idler >= workers) {
            done = true;
            fin.release(); //fin.V();
        }
    }


    /**
     * allIdle.
     * Checks for release().
     */
    public synchronized void allIdle() {
        idler = workers;
        logger.info("allIdle");
        done = true;
        fin.release(); //fin.V();
    }


    /**
     * notIdle.
     */
    public synchronized void notIdle() {
        idler--;
        logger.info("notIdle, idler = " + idler);
        if ( idler < 0 ) {
            throw new RuntimeException("idler < 0");
        }
    }


    /**
     * getJobs.
     * @return number of possible jobs.
     */
    public synchronized int getJobs() {
        return (workers - idler);
    }


    /**
     * hasJobs.
     * @return true, if there are possibly jobs, else false.
     */
    public synchronized boolean hasJobs() {
        return (idler < workers);
    }


    /**
     * Release if possible.
     */
    public synchronized void release() {
        logger.info("release = " + this);
        if ( idler >= workers ) {
            done = true;
            fin.release(); 
        }
        //logger.info("release, idler = " + idler);
    }


    /**
     * Wait until released.
     */
    public void waitDone() {
        try {
            fin.acquire(); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("waitDone " + this);
    }

}
