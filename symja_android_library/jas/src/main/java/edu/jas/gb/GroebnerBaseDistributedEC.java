/*
 * $Id$
 */

package edu.jas.gb;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;
import edu.jas.util.ChannelFactory;
import edu.jas.util.DistHashTable;
import edu.jas.util.DistHashTableServer;
import edu.jas.util.DistThreadPool;
import edu.jas.util.RemoteExecutable;
import edu.jas.util.SocketChannel;
import edu.jas.util.Terminator;
import edu.jas.util.ThreadPool;


/**
 * Groebner Base distributed algorithm. Implements a distributed memory parallel
 * version of Groebner bases with executable channels. Using pairlist class,
 * distributed tasks do reduction, one communication channel per task.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GroebnerBaseDistributedEC<C extends RingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseDistributedEC.class);


    /**
     * Number of threads to use.
     */
    protected final int threads;


    /**
     * Default number of threads.
     */
    protected static final int DEFAULT_THREADS = 2;


    /**
     * Pool of threads to use. <b>Note:</b> No ComputerThreads for one node
     * tests
     */
    protected transient final ThreadPool pool;


    /**
     * Default server port.
     */
    protected static final int DEFAULT_PORT = 55711;


    /**
     * Default distributed hash table server port.
     */
    protected final int DHT_PORT;


    /**
     * Server port to use.
     */
    protected final int port;


    /**
     * machine file to use.
     */
    protected final String mfile;


    /**
     * Distributed thread pool to use.
     */
    private final transient DistThreadPool dtp;


    /**
     * Distributed hash table server to use.
     */
    private final transient DistHashTableServer<Integer> dhts;


    /**
     * Constructor.
     * @param mfile name of the machine file.
     */
    public GroebnerBaseDistributedEC(String mfile) {
        this(mfile, DEFAULT_THREADS, DEFAULT_PORT);
    }


    /**
     * Constructor.
     * @param mfile name of the machine file.
     * @param threads number of threads to use.
     */
    public GroebnerBaseDistributedEC(String mfile, int threads) {
        this(mfile, threads, new ThreadPool(threads), DEFAULT_PORT);
    }


    /**
     * Constructor.
     * @param mfile name of the machine file.
     * @param threads number of threads to use.
     * @param port server port to use.
     */
    public GroebnerBaseDistributedEC(String mfile, int threads, int port) {
        this(mfile, threads, new ThreadPool(threads), port);
    }


    /**
     * Constructor.
     * @param mfile name of the machine file.
     * @param threads number of threads to use.
     * @param pool ThreadPool to use.
     * @param port server port to use.
     */
    public GroebnerBaseDistributedEC(String mfile, int threads, ThreadPool pool, int port) {
        this(mfile, threads, pool, new OrderedPairlist<C>(), port);
    }


    /**
     * Constructor.
     * @param mfile name of the machine file.
     * @param threads number of threads to use.
     * @param pl pair selection strategy
     * @param port server port to use.
     */
    public GroebnerBaseDistributedEC(String mfile, int threads, PairList<C> pl, int port) {
        this(mfile, threads, new ThreadPool(threads), pl, port);
    }


    /**
     * Constructor.
     * @param mfile name of the machine file.
     * @param threads number of threads to use.
     * @param pool ThreadPool to use.
     * @param pl pair selection strategy
     * @param port server port to use.
     */
    public GroebnerBaseDistributedEC(String mfile, int threads, ThreadPool pool, PairList<C> pl, int port) {
        super(new ReductionPar<C>(), pl);
        this.threads = threads;
        if (mfile == null || mfile.length() == 0) {
            this.mfile = "../util/machines"; // contains localhost
        } else {
            this.mfile = mfile;
        }
        if (threads < 1) {
            threads = 1;
        }
        if (pool == null) {
            pool = new ThreadPool(threads);
        }
        this.pool = pool;
        this.port = port;
        logger.info("machine file " + mfile + ", port = " + port);
        this.dtp = new DistThreadPool(this.threads, this.mfile);
        logger.info("running " + dtp);
        this.DHT_PORT = this.dtp.getEC().getMasterPort() + 100;
        this.dhts = new DistHashTableServer<Integer>(this.DHT_PORT);
        this.dhts.init();
        logger.info("running " + dhts);
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    @Override
    public void terminate() {
        terminate(true);
    }


    /**
     * Terminates the distributed thread pools.
     * @param shutDown true, if shut-down of the remote executable servers is
     *            requested, false, if remote executable servers stay alive.
     */
    public void terminate(boolean shutDown) {
        pool.terminate();
        dtp.terminate(shutDown);
        logger.info("dhts.terminate()");
        dhts.terminate();
    }


    /**
     * Distributed Groebner base.
     * @param modv number of module variables.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F or null, if a IOException occurs.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> Fp = normalizeZerosOnes(F);
        Fp = PolyUtil.<C> monic(Fp);
        if (Fp.size() <= 1) {
            return Fp;
        }
        if (!Fp.get(0).ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }

        String master = dtp.getEC().getMasterHost();
        //int port = dtp.getEC().getMasterPort(); // wrong port
        GBExerClient<C> gbc = new GBExerClient<C>(master, port, DHT_PORT);
        for (int i = 0; i < threads; i++) {
            // schedule remote clients
            dtp.addJob(gbc);
        }
        // run master
        List<GenPolynomial<C>> G = GBMaster(modv, Fp);
        return G;
    }


    /**
     * Distributed Groebner base.
     * @param modv number of module variables.
     * @param F non empty monic polynomial list.
     * @return GB(F) a Groebner base of F or null, if a IOException occurs.
     */
    List<GenPolynomial<C>> GBMaster(int modv, List<GenPolynomial<C>> F) {
        ChannelFactory cf = new ChannelFactory(port);
        cf.init();
        logger.info("GBMaster on " + cf);

        List<GenPolynomial<C>> G = F;
        if (G.isEmpty()) {
            throw new IllegalArgumentException("empty polynomial list not allowed");
        }
        GenPolynomialRing<C> ring = G.get(0).ring;
        if (!ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }
        PairList<C> pairlist = strategy.create(modv, ring);
        pairlist.put(G);

        /*
        GenPolynomial<C> p;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        PairList<C> pairlist = null;
        boolean oneInGB = false;
        int l = F.size();
        int unused;
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while (it.hasNext()) {
            p = it.next();
            if (p.length() > 0) {
                p = p.monic();
                if (p.isONE()) {
                    oneInGB = true;
                    G.clear();
                    G.add(p);
                    //return G; must signal termination to others
                }
                if (!oneInGB) {
                    G.add(p);
                }
                if (pairlist == null) {
                    //pairlist = new OrderedPairlist<C>(modv, p.ring);
                    pairlist = strategy.create(modv, p.ring);
                    if (!p.ring.coFac.isField()) {
                        throw new IllegalArgumentException("coefficients not from a field");
                    }
                }
                // theList not updated here
                if (p.isONE()) {
                    unused = pairlist.putOne();
                } else {
                    unused = pairlist.put(p);
                }
            } else {
                l--;
            }
        }
        logger.info("start " + pairlist); 
        //if (l <= 1) {
        //return G; must signal termination to others
        //}
        */
        logger.debug("looking for clients");
        DistHashTable<Integer, GenPolynomial<C>> theList = new DistHashTable<Integer, GenPolynomial<C>>(
                        "localhost", DHT_PORT);
        theList.init();
        List<GenPolynomial<C>> al = pairlist.getList();
        for (int i = 0; i < al.size(); i++) {
            GenPolynomial<C> nn = theList.put(Integer.valueOf(i), al.get(i));
            if (nn != null) {
                logger.info("double polynomials " + i + ", nn = " + nn + ", al(i) = " + al.get(i));
            }
        }
        // wait for arrival
        while (theList.size() < al.size()) {
            logger.info("#distributed list = " + theList.size() + " #pairlist list = " + al.size());
            @SuppressWarnings("unused")
            GenPolynomial<C> nn = theList.getWait(al.size() - 1);
        }

        Terminator fin = new Terminator(threads);
        ReducerServerEC<C> R;
        for (int i = 0; i < threads; i++) {
            R = new ReducerServerEC<C>(fin, cf, theList, pairlist);
            pool.addJob(R);
        }
        logger.debug("main loop waiting");
        fin.waitDone();
        int ps = theList.size();
        logger.debug("#distributed list = " + ps);
        // make sure all polynomials arrived: not needed in master
        G = pairlist.getList();
        if (ps != G.size()) {
            logger.warn("#distributed list = " + theList.size() + " #pairlist list = " + G.size());
        }
        long time = System.currentTimeMillis();
        List<GenPolynomial<C>> Gp;
        Gp = minimalGB(G); // not jet distributed but threaded
        time = System.currentTimeMillis() - time;
        logger.debug("parallel gbmi = " + time);
        /*
          time = System.currentTimeMillis();
          G = GroebnerBase.<C>GBmi(G); // sequential
          time = System.currentTimeMillis() - time;
          logger.info("sequential gbmi = " + time);
        */
        G = Gp;
        logger.debug("cf.terminate()");
        cf.terminate();
        logger.debug("theList.terminate()");
        theList.clear();
        theList.terminate();
        logger.info("" + pairlist);
        return G;
    }


    /**
     * GB distributed client part.
     * @param host the server runs on.
     * @param port the server runs.
     * @param dhtport of the DHT server.
     * @throws IOException
     */
    public static <C extends RingElem<C>> void clientPart(String host, int port, int dhtport)
                    throws IOException {
        ChannelFactory cf = new ChannelFactory(port + 10); // != port for localhost
        cf.init();
        logger.info("clientPart connecting to " + host + ", port = " + port + ", dhtport = " + dhtport);
        SocketChannel pairChannel = cf.getChannel(host, port);

        DistHashTable<Integer, GenPolynomial<C>> theList = new DistHashTable<Integer, GenPolynomial<C>>(host,
                        dhtport);
        theList.init();
        ReducerClientEC<C> R = new ReducerClientEC<C>(pairChannel, theList);

        logger.info("clientPart running on " + host + ", pairChannel = " + pairChannel);
        R.run();

        pairChannel.close();
        //master only: theList.clear();
        theList.terminate();
        cf.terminate();
        return;
    }


    /**
     * Minimal ordered groebner basis.
     * @param Fp a Groebner base.
     * @return a reduced Groebner base of Fp.
     */
    @Override
    public List<GenPolynomial<C>> minimalGB(List<GenPolynomial<C>> Fp) {
        GenPolynomial<C> a;
        ArrayList<GenPolynomial<C>> G;
        G = new ArrayList<GenPolynomial<C>>(Fp.size());
        ListIterator<GenPolynomial<C>> it = Fp.listIterator();
        while (it.hasNext()) {
            a = it.next();
            if (a.length() != 0) { // always true
                // already monic  a = a.monic();
                G.add(a);
            }
        }
        if (G.size() <= 1) {
            return G;
        }

        ExpVector e;
        ExpVector f;
        GenPolynomial<C> p;
        ArrayList<GenPolynomial<C>> F;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        boolean mt;

        while (G.size() > 0) {
            a = G.remove(0);
            e = a.leadingExpVector();

            it = G.listIterator();
            mt = false;
            while (it.hasNext() && !mt) {
                p = it.next();
                f = p.leadingExpVector();
                mt = e.multipleOf(f);
            }
            it = F.listIterator();
            while (it.hasNext() && !mt) {
                p = it.next();
                f = p.leadingExpVector();
                mt = e.multipleOf(f);
            }
            if (!mt) {
                F.add(a);
            } else {
                // System.out.println("dropped " + a.length());
            }
        }
        G = F;
        if (G.size() <= 1) {
            return G;
        }
        Collections.reverse(G); // important for lex GB

        @SuppressWarnings("cast")
        MiReducerServerEC<C>[] mirs = (MiReducerServerEC<C>[]) new MiReducerServerEC[G.size()];
        int i = 0;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            // System.out.println("doing " + a.length());
            List<GenPolynomial<C>> R = new ArrayList<GenPolynomial<C>>(G.size() + F.size());
            R.addAll(G);
            R.addAll(F);
            mirs[i] = new MiReducerServerEC<C>(R, a);
            pool.addJob(mirs[i]);
            i++;
            F.add(a);
        }
        G = F;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        for (i = 0; i < mirs.length; i++) {
            a = mirs[i].getNF();
            F.add(a);
        }
        return F;
    }

}


/**
 * Distributed server reducing worker threads.
 * @param <C> coefficient type
 */

class ReducerServerEC<C extends RingElem<C>> implements Runnable {


    private final Terminator pool;


    private final ChannelFactory cf;


    private SocketChannel pairChannel;


    private final DistHashTable<Integer, GenPolynomial<C>> theList;


    //private List<GenPolynomial<C>> G;
    private final PairList<C> pairlist;


    private static final Logger logger = Logger.getLogger(ReducerServerEC.class);


    ReducerServerEC(Terminator fin, ChannelFactory cf, DistHashTable<Integer, GenPolynomial<C>> dl,
                    PairList<C> L) {
        pool = fin;
        this.cf = cf;
        theList = dl;
        //this.G = G;
        pairlist = L;
    }


    public void run() {
        logger.info("reducer server running with " + cf);
        try {
            pairChannel = cf.getChannel();
        } catch (InterruptedException e) {
            logger.debug("get pair channel interrupted");
            e.printStackTrace();
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("pairChannel = " + pairChannel);
        }
        Pair<C> pair;
        //GenPolynomial<C> pi;
        //GenPolynomial<C> pj;
        //GenPolynomial<C> S;
        GenPolynomial<C> H = null;
        boolean set = false;
        boolean goon = true;
        int polIndex = -1;
        int red = 0;
        int sleeps = 0;

        // while more requests
        while (goon) {
            // receive request
            logger.info("receive request");
            Object req = null;
            try {
                req = pairChannel.receive();
            } catch (IOException e) {
                goon = false;
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                goon = false;
                e.printStackTrace();
            }
            //logger.debug("received request, req = " + req);
            if (req == null) {
                goon = false;
                break;
            }
            if (!(req instanceof GBTransportMessReq)) {
                goon = false;
                break;
            }

            // find pair
            logger.debug("find pair");
            while (!pairlist.hasNext()) { // wait
                if (!set) {
                    pool.beIdle();
                    set = true;
                }
                if (!pool.hasJobs() && !pairlist.hasNext()) {
                    goon = false;
                    break;
                }
                try {
                    sleeps++;
                    if (sleeps % 10 == 0) {
                        logger.info("reducer is sleeping, pool = " + pool);
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    goon = false;
                    break;
                }
            }
            if (Thread.currentThread().isInterrupted()) {
                goon = false;
                break;
            }
            if (!pairlist.hasNext() && !pool.hasJobs()) {
                goon = false;
                break; //continue; //break?
            }
            if (set) {
                set = false;
                pool.notIdle();
            }

            pair = pairlist.removeNext();
            /*
             * send pair to client, receive H
             */
            logger.debug("send pair = " + pair);
            GBTransportMess msg = null;
            if (pair != null) {
                msg = new GBTransportMessPairIndex(pair); // ,pairlist.size()-1); // size-1
            } else {
                msg = new GBTransportMess(); // not End(); at this time
                // goon ?= false;
            }
            try {
                pairChannel.send(msg);
            } catch (IOException e) {
                e.printStackTrace();
                goon = false;
                break;
            }
            logger.debug("#distributed list = " + theList.size());
            Object rh = null;
            try {
                rh = pairChannel.receive();
            } catch (IOException e) {
                e.printStackTrace();
                goon = false;
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                goon = false;
                break;
            }
            //logger.debug("received H polynomial");
            if (rh == null) {
                if (pair != null) {
                    pair.setZero();
                }
            } else if (rh instanceof GBTransportMessPoly) {
                // update pair list
                red++;
                H = ((GBTransportMessPoly<C>) rh).pol;
                if (logger.isDebugEnabled()) {
                    logger.debug("H = " + H);
                }
                if (H == null) {
                    if (pair != null) {
                        pair.setZero();
                    }
                } else {
                    if (H.isZERO()) {
                        pair.setZero();
                    } else {
                        if (H.isONE()) {
                            // pool.allIdle();
                            polIndex = pairlist.putOne();
                            theList.putWait(Integer.valueOf(polIndex), H);
                            goon = false;
                            break;
                        }
                        polIndex = pairlist.put(H);
                        // use putWait ? but still not all distributed
                        theList.putWait(Integer.valueOf(polIndex), H);
                    }
                }
            }
        }
        logger.info("terminated, done " + red + " reductions");

        /*
         * send end mark to client
         */
        logger.debug("send end");
        try {
            pairChannel.send(new GBTransportMessEnd());
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        pool.beIdle();
        pairChannel.close();
    }

}


/**
 * Distributed clients reducing worker threads.
 */

class ReducerClientEC<C extends RingElem<C>> implements Runnable {


    private final SocketChannel pairChannel;


    private final DistHashTable<Integer, GenPolynomial<C>> theList;


    private final ReductionPar<C> red;


    private static final Logger logger = Logger.getLogger(ReducerClientEC.class);


    ReducerClientEC(SocketChannel pc, DistHashTable<Integer, GenPolynomial<C>> dl) {
        pairChannel = pc;
        theList = dl;
        red = new ReductionPar<C>();
    }


    public void run() {
        logger.debug("pairChannel = " + pairChannel + " reducer client running");
        Pair<C> pair = null;
        GenPolynomial<C> pi, pj, ps;
        GenPolynomial<C> S;
        GenPolynomial<C> H = null;
        //boolean set = false;
        boolean goon = true;
        int reduction = 0;
        //int sleeps = 0;
        Integer pix, pjx, psx;

        while (goon) {
            /* protocol:
             * request pair, process pair, send result
             */
            // pair = (Pair) pairlist.removeNext();
            Object req = new GBTransportMessReq();
            logger.debug("send request = " + req);
            H = null;
            try {
                pairChannel.send(req);
            } catch (IOException e) {
                goon = false;
                e.printStackTrace();
                break;
            }
            logger.debug("receive pair, goon = " + goon);
            Object pp = null;
            try {
                pp = pairChannel.receive();
            } catch (IOException e) {
                goon = false;
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
                break;
            } catch (ClassNotFoundException e) {
                goon = false;
                e.printStackTrace();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("received pair = " + pp);
            }
            if (pp == null) { // should not happen
                continue;
            }
            if (pp instanceof GBTransportMessEnd) {
                goon = false;
                continue;
            }
            if (pp instanceof GBTransportMessPair || pp instanceof GBTransportMessPairIndex) {
                pi = pj = ps = null;
                if (pp instanceof GBTransportMessPair) { // obsolete, for tests
                    GBTransportMessPair<C> tmp = (GBTransportMessPair<C>) pp;
                    pair = tmp.pair;
                    if (pair != null) {
                        pi = pair.pi;
                        pj = pair.pj;
                        //logger.debug("pair: pix = " + pair.i 
                        //               + ", pjx = " + pair.j);
                    }
                }
                if (pp instanceof GBTransportMessPairIndex) {
                    GBTransportMessPairIndex tmpi = (GBTransportMessPairIndex) pp;
                    pix = tmpi.i;
                    pjx = tmpi.j;
                    psx = tmpi.s;
                    pi = theList.getWait(pix);
                    pj = theList.getWait(pjx);
                    ps = theList.getWait(psx);
                    //logger.info("pix = " + pix + ", pjx = " + pjx + ", psx = " + psx);
                }

                if (pi != null && pj != null) {
                    S = red.SPolynomial(pi, pj);
                    //System.out.println("S   = " + S);
                    if (S.isZERO()) {
                        // pair.setZero(); does not work in dist
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("ht(S) = " + S.leadingExpVector());
                        }
                        H = red.normalform(theList, S);
                        reduction++;
                        if (H.isZERO()) {
                            // pair.setZero(); does not work in dist
                        } else {
                            H = H.monic();
                            if (logger.isInfoEnabled()) {
                                logger.info("ht(H) = " + H.leadingExpVector());
                            }
                        }
                    }
                } else {
                    logger.info("pi = " + pi + ", pj = " + pj + ", ps = " + ps);
                }
            }

            // send H or must send null
            if (logger.isDebugEnabled()) {
                logger.debug("#distributed list = " + theList.size());
                logger.debug("send H polynomial = " + H);
            }
            try {
                pairChannel.send(new GBTransportMessPoly<C>(H));
            } catch (IOException e) {
                goon = false;
                e.printStackTrace();
            }
        }
        logger.info("terminated, " + reduction + " reductions, " + theList.size() + " polynomials");
        pairChannel.close();
    }
}


/**
 * Distributed server reducing worker threads for minimal GB Not jet distributed
 * but threaded.
 */

class MiReducerServerEC<C extends RingElem<C>> implements Runnable {


    private final List<GenPolynomial<C>> G;


    private GenPolynomial<C> H;


    private final Semaphore done = new Semaphore(0);


    private final Reduction<C> red;


    private static final Logger logger = Logger.getLogger(MiReducerServerEC.class);


    MiReducerServerEC(List<GenPolynomial<C>> G, GenPolynomial<C> p) {
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
 * Distributed clients reducing worker threads for minimal GB. Not jet used.
 */

class MiReducerClientEC<C extends RingElem<C>> implements Runnable {


    private final List<GenPolynomial<C>> G;


    private GenPolynomial<C> H;


    private final Reduction<C> red;


    private final Semaphore done = new Semaphore(0);


    private static final Logger logger = Logger.getLogger(MiReducerClientEC.class);


    MiReducerClientEC(List<GenPolynomial<C>> G, GenPolynomial<C> p) {
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


/**
 * Objects of this class are to be send to a ExecutableServer.
 */

class GBExerClient<C extends RingElem<C>> implements RemoteExecutable {


    String host;


    int port;


    int dhtport;


    /**
     * GBExerClient.
     * @param host
     * @param port
     * @param dhtport
     */
    public GBExerClient(String host, int port, int dhtport) {
        this.host = host;
        this.port = port;
        this.dhtport = dhtport;
    }


    /**
     * run.
     */
    public void run() {
        //System.out.println("running " + this);
        try {
            GroebnerBaseDistributedEC.<C> clientPart(host, port, dhtport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * String representation.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("GBExerClient(");
        s.append("host=" + host);
        s.append(", port=" + port);
        s.append(", dhtport=" + dhtport);
        s.append(")");
        return s.toString();
    }

}
