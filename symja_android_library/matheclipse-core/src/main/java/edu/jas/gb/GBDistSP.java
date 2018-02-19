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
 *
 * @author Heinz Kredel
 *         TODO: to deprecate
 */

public class GBDistSP<C extends RingElem<C>> {


    /**
     * Number of threads to use.
     */
    protected final int threads;
    /**
     * Server port to use.
     */
    protected final int port;
    /**
     * machine file to use.
     */
    private final String mfile;
    /**
     * GB algorithm to use.
     */
    private final GroebnerBaseSeqPairDistributed<C> bbd;


    /**
     * Distributed thread pool to use.
     */
    private final DistThreadPool dtp;


    /**
     * Constructor.
     *
     * @param threads number of threads respectivly processes.
     * @param mfile   name of the machine file.
     * @param port    for GB server.
     */
    public GBDistSP(int threads, String mfile, int port) {
        this.threads = threads;
        if (mfile == null || mfile.length() == 0) {
            this.mfile = "../util/machines";
        } else {
            this.mfile = mfile;
        }
        this.port = port;
        bbd = new GroebnerBaseSeqPairDistributed<C>(threads, this.port);
        dtp = new DistThreadPool(threads, this.mfile);
    }


    /**
     * Execute a distributed GB example. Distribute clients and start master.
     *
     * @param F list of polynomials
     * @return GB(F) a Groebner base for F.
     */
    public List<GenPolynomial<C>> execute(List<GenPolynomial<C>> F) {
        String master = dtp.getEC().getMasterHost();
        int port = dtp.getEC().getMasterPort();
        GBClientSP<C> gbc = new GBClientSP<C>(master, port);
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
     *
     * @param shutDown true, if shut-down of the remote executable servers is
     *                 requested, false, if remote executable servers stay alive.
     */
    public void terminate(boolean shutDown) {
        bbd.terminate();
        dtp.terminate(shutDown);
    }

}


/**
 * Objects of this class are to be send to a ExecutableServer.
 */

class GBClientSP<C extends RingElem<C>> implements RemoteExecutable {


    String host;


    int port;


    /**
     * GBClient.
     *
     * @param host
     * @param port
     */
    public GBClientSP(String host, int port) {
        this.host = host;
        this.port = port;
    }


    /**
     * run.
     */
    public void run() {
        GroebnerBaseSeqPairDistributed<C> bbd;
        bbd = new GroebnerBaseSeqPairDistributed<C>(1, null, port);
        try {
            bbd.clientPart(host);
        } catch (IOException ignored) {
        }
        bbd.terminate();
    }

}
