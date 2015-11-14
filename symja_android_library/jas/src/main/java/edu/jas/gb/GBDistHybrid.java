/*
 * $Id$
 */

package edu.jas.gb;


import java.io.IOException;
import java.util.List;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;
import edu.jas.util.DistThreadPool;
import edu.jas.util.RemoteExecutable;


/**
 * Setup to run a distributed GB example.
 * @author Heinz Kredel
 * @deprecated use GroebnerBaseDistributedHybridEC
 */
@Deprecated
public class GBDistHybrid<C extends RingElem<C>> {


    /**
     * machine file to use.
     */
    private final String mfile;


    /**
     * Number of threads to use.
     */
    protected final int threads;


    /**
     * Number of threads per node to use.
     */
    protected final int threadsPerNode;


    /**
     * Server port to use.
     */
    protected final int port;


    /**
     * GB algorithm to use.
     */
    private final GroebnerBaseDistributedHybrid<C> bbd;


    /**
     * Distributed thread pool to use.
     */
    private final DistThreadPool dtp;


    /**
     * Constructor.
     * @param threads number of threads respectivly processes.
     * @param threadsPerNode number of threads per node to use.
     * @param mfile name of the machine file.
     * @param port for GB server.
     */
    public GBDistHybrid(int threads, int threadsPerNode, String mfile, int port) {
        this(threads, threadsPerNode, new OrderedPairlist<C>(), mfile, port);
    }


    /**
     * Constructor.
     * @param threads number of threads respectivly processes.
     * @param threadsPerNode number of threads per node to use.
     * @param pl pair selection strategy
     * @param mfile name of the machine file.
     * @param port for GB server.
     */
    public GBDistHybrid(int threads, int threadsPerNode, PairList<C> pl, String mfile, int port) {
        this.threads = threads;
        this.threadsPerNode = threadsPerNode;
        if (mfile == null || mfile.length() == 0) {
            this.mfile = "../util/machines";
        } else {
            this.mfile = mfile;
        }
        this.port = port;
        bbd = new GroebnerBaseDistributedHybrid<C>(threads, threadsPerNode, pl, this.port);
        dtp = new DistThreadPool(threads, this.mfile); // findbugs
    }


    /**
     * Execute a distributed GB example. Distribute clients and start master.
     * @param F list of polynomials
     * @return GB(F) a Groebner base for F.
     */
    public List<GenPolynomial<C>> execute(List<GenPolynomial<C>> F) {
        String master = dtp.getEC().getMasterHost();
        int port = dtp.getEC().getMasterPort();
        GBClientHybrid<C> gbc = new GBClientHybrid<C>(threadsPerNode, master, port);
        for (int i = 0; i < threads; i++) {
            // schedule remote clients
            dtp.addJob(gbc);
        }
        // run master
        List<GenPolynomial<C>> G = bbd.GB(F);
        return G;
    }


    /**
     * Terminates the distributed thread pools.
     * @param shutDown true, if shut-down of the remote executable servers is
     *            requested, false, if remote executable servers stay alive.
     */
    public void terminate(boolean shutDown) {
        bbd.terminate();
        dtp.terminate(shutDown);
    }

}


/**
 * Objects of this class are to be send to a ExecutableServer.
 */

class GBClientHybrid<C extends RingElem<C>> implements RemoteExecutable {


    String host;

    int port;

    //int threads;

    int threadsPerNode;


    /**
     * GBClientHybrid.
     * @param threadsPerNode
     * @param host master
     * @param port
     */
    public GBClientHybrid(int threadsPerNode, String host, int port) {
        //this.threads = threads;
        this.threadsPerNode = threadsPerNode;
        this.host = host;
        this.port = port;
    }


    /** Get the String representation.
      * @see java.lang.Object#toString()
      */
    @Override
    public String toString() {
        return "GBClientHybrid(" + threadsPerNode + ", " + host + ":" + port + " )";
    }


    /**
     * run.
     */
    public void run() {
        GroebnerBaseDistributedHybrid<C> bbd;
        bbd = new GroebnerBaseDistributedHybrid<C>(1, threadsPerNode, null, null, port);
        try {
            bbd.clientPart(host);
        } catch (IOException e) {
            System.out.println("clientPart, exception " + e);
        } catch (Exception e) {
            System.out.println("clientPart, exception " + e);
        }
        bbd.terminate();
    }

}
