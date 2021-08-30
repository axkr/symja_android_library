/*
 * $Id$
 */

package edu.jas.util;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 


/**
 * ExecutableServer is used to receive and execute classes.
 * @author Heinz Kredel
 */

public class ExecutableServer extends Thread {


    private static final Logger logger = LogManager.getLogger(ExecutableServer.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * ChannelFactory to use.
     */
    protected final ChannelFactory cf;


    /**
     * List of server threads.
     */
    protected List<Executor> servers = null;


    /**
     * Default port to listen to.
     */
    public static final int DEFAULT_PORT = 7411;


    /**
     * Constant to signal completion.
     */
    public static final String DONE = "Done";


    /**
     * Constant to request shutdown.
     */
    public static final String STOP = "Stop";


    private volatile boolean goon = true;


    private volatile Thread mythread = null;


    /**
     * ExecutableServer on default port.
     */
    public ExecutableServer() {
        this(DEFAULT_PORT);
    }


    /**
     * ExecutableServer.
     * @param port
     */
    public ExecutableServer(int port) {
        this(new ChannelFactory(port));
    }


    /**
     * ExecutableServer.
     * @param cf channel factory to reuse.
     */
    public ExecutableServer(ChannelFactory cf) {
        this.cf = cf;
        cf.init();
        servers = new ArrayList<Executor>();
    }


    /**
     * main method to start serving thread.
     * @param args args[0] is port
     */
    public static void main(String[] args) throws InterruptedException {
        int port = DEFAULT_PORT;
        if (args.length < 1) {
            System.out.println("Usage: ExecutableServer <port>");
        } else {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
            }
        }
        //logger.info("ExecutableServer at port " + port);
        ExecutableServer es = new ExecutableServer(port);
        es.init();
        es.join(); // do not use terminate()
        // until CRTL-C
    }


    /**
     * thread initialization and start.
     */
    public void init() {
        this.start();
        logger.info("ExecutableServer at " + cf);
    }


    /**
     * number of servers.
     */
    public int size() {
        if ( servers == null ) {
            return -1;
        }
        return servers.size();
    }


    /**
     * run is main server method.
     */
    @Override
    public void run() {
        SocketChannel channel = null;
        Executor s = null;
        mythread = Thread.currentThread();
        while (goon) {
            if (debug) {
                logger.info("server " + this + " go on");
            }
            try {
                channel = cf.getChannel();
                logger.info("channel = " + channel);
                if (mythread.isInterrupted()) {
                    goon = false;
                    logger.debug("execute server " + this + " interrupted");
                    channel.close();
                } else {
                    s = new Executor(channel); // ---,servers);
                    if (goon) { // better synchronize with terminate
                        servers.add(s);
                        s.start();
                        logger.debug("server " + s + " started");
                    } else {
                        s = null;
                        channel.close();
                    }
                }
            } catch (InterruptedException e) {
                goon = false;
                Thread.currentThread().interrupt();
                if (debug) {
                    e.printStackTrace();
                }
            }
        }
        if (debug) {
            logger.info("server " + this + " terminated");
        }
    }


    /**
     * terminate all servers.
     */
    public void terminate() {
        goon = false;
        logger.info("terminating ExecutableServer");
        if (cf != null)
            cf.terminate();
        if (servers != null) {
            Iterator<Executor> it = servers.iterator();
            while (it.hasNext()) {
                Executor x = it.next();
                if (x.channel != null) {
                    x.channel.close();
                }
                try {
                    while (x.isAlive()) {
                        //System.out.print(".");
                        x.interrupt();
                        x.join(100);
                    }
                    logger.debug("server " + x + " terminated");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            servers = null;
        }
        logger.info("Executors terminated");
        if (mythread == null)
            return;
        try {
            while (mythread.isAlive()) {
                //System.out.print("-");
                mythread.interrupt();
                mythread.join(100);
            }
            //logger.debug("server " + mythread + " terminated");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        mythread = null;
        logger.info("terminated");
    }


    /**
     * String representation.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("ExecutableServer(");
        s.append(cf.toString());
        s.append(")");
        return s.toString();
    }

}


/**
 * class for executing incoming objects.
 */

class Executor extends Thread /*implements Runnable*/{


    private static final Logger logger = LogManager.getLogger(Executor.class);

    private static final boolean debug = logger.isDebugEnabled();


    protected final SocketChannel channel;


    Executor(SocketChannel s) {
        channel = s;
    }


    /**
     * run.
     */
    @Override
    public void run() {
        Object o;
        RemoteExecutable re = null;
        String d;
        boolean goon = true;
        logger.debug("executor started " + this);
        while (goon) {
            try {
                o = channel.receive();
                logger.info("receive: " + o + " from " + channel);
                if (this.isInterrupted()) {
                    goon = false;
                } else {
                    if (debug) {
                        logger.debug("receive: " + o + " from " + channel);
                    }
                    if (o instanceof String) {
                        d = (String) o;
                        if (ExecutableServer.STOP.equals(d)) {
                            goon = false; // stop this thread
                            channel.send(ExecutableServer.DONE);
                        } else {
                            logger.warn("invalid/unknown String: " + d + " from " + channel);
                            goon = false; // stop this thread ?
                            channel.send(ExecutableServer.DONE);
                        }
                    }
                    // check permission
                    if (o instanceof RemoteExecutable) {
                        re = (RemoteExecutable) o;
                        if (debug) {
                            logger.info("running " + re);
                        }
                        try {
                            re.run();
                        } catch(Exception e) {
                            logger.info("Exception on re.run()" + e);
                            if (logger.isInfoEnabled()) {
                                e.printStackTrace();
                            }
                        } finally {
                            logger.info("finally re.run() " + re);
                        }
                        if (debug) {
                            logger.info("finished " + re);
                        }
                        if (this.isInterrupted()) {
                            goon = false;
                        } else {
                            channel.send(ExecutableServer.DONE);
                            logger.info("finished send " + ExecutableServer.DONE);
                            //goon = false; // stop this thread
                        }
                    }
                }
            } catch (IOException e) {
                goon = false;
                logger.info("IOException " + e);
                if (debug) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                goon = false;
                logger.info("ClassNotFoundException " + e);
                e.printStackTrace();
            } finally {
                logger.info("finally " + this);
            }
        }
        channel.close();
        logger.info("terminated " + this);
    }

}
