/*
 * $Id$
 */

package edu.jas.util;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.log4j.Logger;


/**
 * Distributed thread pool. Using stack / list work-pile and Executable Channels
 * and Servers.
 * @author Heinz Kredel
 */

public class DistThreadPool /*extends ThreadPool*/{


    /**
     * machine file to use.
     */
    private final String mfile;


    /**
     * default machine file for test.
     */
    private final static String DEFAULT_MFILE = ExecutableChannels.DEFAULT_MFILE;


    /**
     * Number of threads to use.
     */
    protected final int threads;


    /**
     * Default number of threads to use.
     */
    static final int DEFAULT_SIZE = 3;


    /**
     * Channels to remote executable servers.
     */
    final ExecutableChannels ec;


    /**
     * Array of workers.
     */
    protected DistPoolThread[] workers;


    /**
     * Number of idle workers.
     */
    protected int idleworkers = 0;


    /**
     * Work queue / stack.
     */
    // should be expressed using strategy pattern
    // List or Collection is not appropriate
    // LIFO strategy for recursion
    protected LinkedList<Runnable> jobstack; // FIFO strategy for GB


    protected StrategyEnumeration strategy = StrategyEnumeration.LIFO;


    private static final Logger logger = Logger.getLogger(DistThreadPool.class);


    private final boolean debug = true; //logger.isDebugEnabled();


    /**
     * Constructs a new DistThreadPool with strategy StrategyEnumeration.FIFO
     * and size DEFAULT_SIZE.
     */
    public DistThreadPool() {
        this(StrategyEnumeration.FIFO, DEFAULT_SIZE, null);
    }


    /**
     * Constructs a new DistThreadPool with size DEFAULT_SIZE.
     * @param strategy for job processing.
     */
    public DistThreadPool(StrategyEnumeration strategy) {
        this(strategy, DEFAULT_SIZE, null);
    }


    /**
     * Constructs a new DistThreadPool with strategy StrategyEnumeration.FIFO.
     * @param size of the pool.
     */
    public DistThreadPool(int size) {
        this(StrategyEnumeration.FIFO, size, null);
    }


    /**
     * Constructs a new DistThreadPool with strategy StrategyEnumeration.FIFO.
     * @param size of the pool.
     * @param mfile machine file.
     */
    public DistThreadPool(int size, String mfile) {
        this(StrategyEnumeration.FIFO, size, mfile);
    }


    /**
     * Constructs a new DistThreadPool.
     * @param strategy for job processing.
     * @param size of the pool.
     * @param mfile machine file.
     */
    public DistThreadPool(StrategyEnumeration strategy, int size, String mfile) {
        this.strategy = strategy;
        if (size < 0) {
            this.threads = 0;
        } else {
            this.threads = size;
        }
        if (mfile == null || mfile.length() == 0) {
            this.mfile = DEFAULT_MFILE;
        } else {
            this.mfile = mfile;
        }
        jobstack = new LinkedList<Runnable>(); // ok for all strategies ?
        try {
            ec = new ExecutableChannels(this.mfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("DistThreadPool " + e);
        }
        if (debug) {
            logger.info("ec = " + ec);
        }
        try {
            ec.open(threads);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("DistThreadPool " + e);
        }
        if (debug) {
            logger.info("ec = " + ec);
        }
        workers = new DistPoolThread[0];
    }


    /**
     * String representation.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("DistThreadPool(");
        s.append("threads="+threads);
        s.append(", exchan="+ec);
        s.append(", workers="+workers.length);
        s.append(")");
        return s.toString();
    }


    /**
     * thread initialization and start.
     */
    public void init() {
        if (workers == null || workers.length == 0) {
            workers = new DistPoolThread[threads];
            for (int i = 0; i < workers.length; i++) {
                workers[i] = new DistPoolThread(this, ec, i);
                workers[i].start();
            }
            logger.info("size = " + threads + ", strategy = " + strategy);
        }
    }


    /**
     * number of worker threads.
     */
    public int getNumber() {
        if (workers == null || workers.length < threads) {
            init(); // start threads
        }
        return workers.length; // not null
    }


    /**
     * get used strategy.
     */
    public StrategyEnumeration getStrategy() {
        return strategy;
    }


    /**
     * the used executable channel.
     */
    public ExecutableChannels getEC() {
        return ec; // not null
    }


    /**
     * Terminates the threads.
     * @param shutDown true, if shut-down of the remote executable servers is
     *            requested, false, if remote executable servers stay alive.
     */
    public void terminate(boolean shutDown) {
        if (shutDown) {
            ShutdownRequest sdr = new ShutdownRequest();
            for (int i = 0; i < workers.length; i++) {
                addJob(sdr);
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            logger.info("remaining jobs = " + jobstack.size());
            try {
                for (int i = 0; i < workers.length; i++) {
                    while (workers[i].isAlive()) {
                        workers[i].interrupt();
                        workers[i].join(100);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            terminate();
        }
    }


    /**
     * Terminates the threads.
     */
    public void terminate() {
        while (hasJobs()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        for (int i = 0; i < workers.length; i++) {
            try {
                while (workers[i].isAlive()) {
                    workers[i].interrupt();
                    workers[i].join(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        ec.close();
    }


    /**
     * adds a job to the workpile.
     * @param job
     */
    public synchronized void addJob(Runnable job) {
        if (workers == null || workers.length < threads) {
            init(); // start threads
        }
        jobstack.addLast(job);
        logger.debug("adding job");
        if (idleworkers > 0) {
            logger.debug("notifying a jobless worker");
            notifyAll(); // findbugs
        }
    }


    /**
     * get a job for processing.
     */
    protected synchronized Runnable getJob() throws InterruptedException {
        while (jobstack.isEmpty()) {
            idleworkers++;
            logger.debug("waiting");
            wait();
            idleworkers--;
        }
        // is expressed using strategy enumeration
        if (strategy == StrategyEnumeration.LIFO) {
            return jobstack.removeLast(); // LIFO
        }
        return jobstack.removeFirst(); // FIFO
    }


    /**
     * check if there are jobs for processing.
     */
    public boolean hasJobs() {
        if (jobstack.size() > 0) {
            return true;
        }
        for (int i = 0; i < workers.length; i++) {
            if (workers[i].working) {
                return true;
            }
        }
        return false;
    }


    /**
     * check if there are more than n jobs for processing.
     * @param n Integer
     * @return true, if there are possibly more than n jobs.
     */
    public boolean hasJobs(int n) {
        int j = jobstack.size();
        if (j > 0 && (j + workers.length > n)) {
            return true;
            // if j > 0 no worker should be idle
            // ( ( j > 0 && ( j+workers.length > n ) ) || ( j > n )
        }
        int x = 0;
        for (int i = 0; i < workers.length; i++) {
            if (workers[i].working) {
                x++;
            }
        }
        if ((j + x) > n) {
            return true;
        }
        return false;
    }

}


/**
 * Implements a shutdown task.
 */
class ShutdownRequest implements Runnable {


    /**
     * Run the thread.
     */
    public void run() {
        System.out.println("running ShutdownRequest");
    }


    /**
     * toString.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ShutdownRequest";
    }

}


/**
 * Implements one local part of the distributed thread.
 */
class DistPoolThread extends Thread {


    final DistThreadPool pool;


    final ExecutableChannels ec;


    final int myId;


    private static final Logger logger = Logger.getLogger(DistPoolThread.class);


    private final boolean debug = logger.isDebugEnabled();


    boolean working = false;


    /**
     * @param pool DistThreadPool.
     */
    public DistPoolThread(DistThreadPool pool, ExecutableChannels ec, int i) {
        this.pool = pool;
        this.ec = ec;
        myId = i;
    }


    /**
     * Run the thread.
     */
    @Override
    public void run() {
        logger.info("ready, myId = " + myId);
        Runnable job;
        int done = 0;
        long time = 0;
        long t;
        boolean running = true;
        while (running) {
            try {
                logger.debug("looking for a job");
                job = pool.getJob();
                working = true;
                if (debug) {
                    logger.info("working " + myId + " on " + job);
                }
                t = System.currentTimeMillis();
                // send and wait, like rmi
                try {
                    if (job instanceof ShutdownRequest) {
                        ec.send(myId, ExecutableServer.STOP);
                    } else {
                        ec.send(myId, job);
                    }
                    if (debug) {
                        logger.info("send " + myId + " at " + ec + " send job " + job);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.info("error send " + myId + " at " + ec + " e = " + e);
                    working = false;
                }
                // remote: job.run(); 
                Object o = null;
                try {
                    if (working) {
                        logger.info("waiting " + myId + " on " + job);
                        o = ec.receive(myId);
                        if (debug) {
                            logger.info("receive " + myId + " at " + ec + " send job " + job + " received " + o);
                        }
                    }
                } catch (IOException e) {
                    logger.info("receive exception " + myId + " send job " + job + ", " + e);
                    //e.printStackTrace();
                    running = false;
                } catch (ClassNotFoundException e) {
                    logger.info("receive exception " + myId + " send job " + job + ", " + e);
                    //e.printStackTrace();
                    running = false;
                } finally {
                    if (debug) {
                        logger.info("receive finally " + myId + " at " + ec + " send job " + job + " received "
                                    + o + " running " + running);
                    }
                }
                working = false;
                time += System.currentTimeMillis() - t;
                done++;
                if (debug) {
                    logger.info("done " + myId + " with " + o);
                }
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            }
        }
        logger.info("terminated " + myId + " , done " + done + " jobs in " + time + " milliseconds");
    }

}
