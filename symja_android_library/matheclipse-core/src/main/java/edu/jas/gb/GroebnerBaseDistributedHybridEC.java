/*
 * $Id$
 */

package edu.jas.gb;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

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
import edu.jas.util.TaggedSocketChannel;
import edu.jas.util.Terminator;
import edu.jas.util.ThreadPool;


/**
 * Groebner Base distributed hybrid algorithm. Implements a distributed memory
 * with multi-core CPUs parallel version of Groebner bases with executable
 * channels. Using pairlist class, distributed multi-threaded tasks do
 * reduction, one communication channel per remote node.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GroebnerBaseDistributedHybridEC<C extends RingElem<C>> extends GroebnerBaseAbstract<C> {


    /**
     * Message tag for pairs.
     */
    public static final Integer pairTag = Integer.valueOf(1);
    /**
     * Message tag for results.
     */
    public static final Integer resultTag = Integer.valueOf(2);
    /**
     * Message tag for acknowledgments.
     */
    public static final Integer ackTag = Integer.valueOf(3);
    /**
     * Default number of threads.
     */
    protected static final int DEFAULT_THREADS = 2;
    /**
     * Default number of threads per compute node.
     */
    protected static final int DEFAULT_THREADS_PER_NODE = 1;
    /**
     * Default server port.
     */
    protected static final int DEFAULT_PORT = 55711;
    private static final Logger logger = Logger.getLogger(GroebnerBaseDistributedHybridEC.class);
    private static final boolean debug = logger.isDebugEnabled();
    /**
     * Number of threads to use.
     */
    protected final int threads;
    /**
     * Number of threads per node to use.
     */
    protected final int threadsPerNode;
    /**
     * Pool of threads to use.
     */
    //protected final ExecutorService pool; // not for single node tests
    protected transient final ThreadPool pool;
    /**
     * Default distributed hash table server port.
     */
    protected final int DHT_PORT;
    /**
     * machine file to use.
     */
    protected final String mfile;
    /**
     * Server port to use.
     */
    protected final int port;
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
     *
     * @param mfile name of the machine file.
     */
    public GroebnerBaseDistributedHybridEC(String mfile) {
        this(mfile, DEFAULT_THREADS, DEFAULT_PORT);
    }


    /**
     * Constructor.
     *
     * @param mfile   name of the machine file.
     * @param threads number of threads to use.
     */
    public GroebnerBaseDistributedHybridEC(String mfile, int threads) {
        this(mfile, threads, new ThreadPool(threads), DEFAULT_PORT);
    }


    /**
     * Constructor.
     *
     * @param mfile   name of the machine file.
     * @param threads number of threads to use.
     * @param port    server port to use.
     */
    public GroebnerBaseDistributedHybridEC(String mfile, int threads, int port) {
        this(mfile, threads, new ThreadPool(threads), port);
    }


    /**
     * Constructor.
     *
     * @param mfile          name of the machine file.
     * @param threads        number of threads to use.
     * @param threadsPerNode threads per node to use.
     * @param port           server port to use.
     */
    public GroebnerBaseDistributedHybridEC(String mfile, int threads, int threadsPerNode, int port) {
        this(mfile, threads, threadsPerNode, new ThreadPool(threads), port);
    }


    /**
     * Constructor.
     *
     * @param mfile   name of the machine file.
     * @param threads number of threads to use.
     * @param pool    ThreadPool to use.
     * @param port    server port to use.
     */
    public GroebnerBaseDistributedHybridEC(String mfile, int threads, ThreadPool pool, int port) {
        this(mfile, threads, DEFAULT_THREADS_PER_NODE, pool, port);
    }


    /**
     * Constructor.
     *
     * @param mfile          name of the machine file.
     * @param threads        number of threads to use.
     * @param threadsPerNode threads per node to use.
     * @param pl             pair selection strategy
     * @param port           server port to use.
     */
    public GroebnerBaseDistributedHybridEC(String mfile, int threads, int threadsPerNode, PairList<C> pl,
                                           int port) {
        this(mfile, threads, threadsPerNode, new ThreadPool(threads), pl, port);
    }


    /**
     * Constructor.
     *
     * @param mfile          name of the machine file.
     * @param threads        number of threads to use.
     * @param threadsPerNode threads per node to use.
     * @param port           server port to use.
     */
    public GroebnerBaseDistributedHybridEC(String mfile, int threads, int threadsPerNode, ThreadPool pool,
                                           int port) {
        this(mfile, threads, threadsPerNode, pool, new OrderedPairlist<C>(), port);
    }


    /**
     * Constructor.
     *
     * @param mfile          name of the machine file.
     * @param threads        number of threads to use.
     * @param threadsPerNode threads per node to use.
     * @param pool           ThreadPool to use.
     * @param pl             pair selection strategy
     * @param port           server port to use.
     */
    public GroebnerBaseDistributedHybridEC(String mfile, int threads, int threadsPerNode, ThreadPool pool,
                                           PairList<C> pl, int port) {
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
        this.threadsPerNode = threadsPerNode;
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
     * GB distributed client part.
     *
     * @param host    the server runs on.
     * @param port    the server runs.
     * @param dhtport of the DHT server.
     * @throws IOException
     */
    public static <C extends RingElem<C>> void clientPart(String host, int threadsPerNode, int port,
                                                          int dhtport) throws IOException {
        ChannelFactory cf = new ChannelFactory(port + 10); // != port for localhost
        cf.init();
        logger.info("clientPart connecting to " + host + ", port = " + port + ", dhtport = " + dhtport);
        SocketChannel channel = cf.getChannel(host, port);
        TaggedSocketChannel pairChannel = new TaggedSocketChannel(channel);
        pairChannel.init();

        DistHashTable<Integer, GenPolynomial<C>> theList = new DistHashTable<Integer, GenPolynomial<C>>(host,
                dhtport);
        theList.init();

        ThreadPool pool = new ThreadPool(threadsPerNode);
        logger.info("client using pool = " + pool);
        for (int i = 0; i < threadsPerNode; i++) {
            HybridReducerClientEC<C> Rr = new HybridReducerClientEC<C>(/*threadsPerNode,*/pairChannel, /*i,*/
                    theList);
            pool.addJob(Rr);
        }
        logger.debug("clients submitted");

        pool.terminate();
        logger.debug("client pool.terminate()");

        pairChannel.close();
        logger.debug("client pairChannel.close()");

        //master only: theList.clear();
        theList.terminate();
        cf.terminate();
        logger.info("client cf.terminate()");

        channel.close();
        logger.info("client channel.close()");
        return;
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
     *
     * @param shutDown true, if shut-down of the remote executable servers is
     *                 requested, false, if remote executable servers stay alive.
     */
    public void terminate(boolean shutDown) {
        pool.terminate();
        dtp.terminate(shutDown);
        logger.debug("dhts.terminate()");
        dhts.terminate();
    }

    /**
     * Distributed Groebner base.
     *
     * @param modv number of module variables.
     * @param F    polynomial list.
     * @return GB(F) a Groebner base of F or null, if a IOException occurs.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> Fp = normalizeZerosOnes(F);
        Fp = PolyUtil.<C>monic(Fp);
        if (Fp.size() <= 1) {
            return Fp;
        }
        if (!Fp.get(0).ring.coFac.isField()) {
            throw new IllegalArgumentException("coefficients not from a field");
        }

        String master = dtp.getEC().getMasterHost();
        //int port = dtp.getEC().getMasterPort(); // wrong port
        GBHybridExerClient<C> gbc = new GBHybridExerClient<C>(master, threadsPerNode, port, DHT_PORT);
        for (int i = 0; i < threads; i++) {
            // schedule remote clients
            dtp.addJob(gbc);
        }
        // run master
        List<GenPolynomial<C>> G = GBMaster(modv, Fp);
        return G;
    }

    /**
     * Distributed hybrid Groebner base.
     *
     * @param modv number of module variables.
     * @param F    non empty monic polynomial list without zeros.
     * @return GB(F) a Groebner base of F or null, if a IOException occurs.
     */
    List<GenPolynomial<C>> GBMaster(int modv, List<GenPolynomial<C>> F) {
        long t = System.currentTimeMillis();
        ChannelFactory cf = new ChannelFactory(port);
        cf.init();

        List<GenPolynomial<C>> G = F;
        if (G.isEmpty()) {
            throw new IllegalArgumentException("empty polynomial list not allowed");
        }
        GenPolynomialRing<C> ring = G.get(0).ring;
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
        //if (l <= 1) {
        //return G; must signal termination to others
        //}
        */
        logger.info("start " + pairlist);
        DistHashTable<Integer, GenPolynomial<C>> theList = new DistHashTable<Integer, GenPolynomial<C>>(
                "localhost", DHT_PORT);
        theList.init();
        List<GenPolynomial<C>> al = pairlist.getList();
        for (int i = 0; i < al.size(); i++) {
            // no wait required
            GenPolynomial<C> nn = theList.put(Integer.valueOf(i), al.get(i));
            if (nn != null) {
                logger.info("double polynomials " + i + ", nn = " + nn + ", al(i) = " + al.get(i));
            }
        }

        Terminator finner = new Terminator(threads * threadsPerNode);
        HybridReducerServerEC<C> R;
        logger.info("using pool = " + pool);
        for (int i = 0; i < threads; i++) {
            R = new HybridReducerServerEC<C>(threadsPerNode, finner, cf, theList, pairlist);
            pool.addJob(R);
            //logger.info("server submitted " + R);
        }
        logger.info("main loop waiting " + finner);
        finner.waitDone();
        int ps = theList.size();
        logger.info("#distributed list = " + ps);
        // make sure all polynomials arrived: not needed in master
        // G = (ArrayList)theList.values();
        G = pairlist.getList();
        if (ps != G.size()) {
            logger.info("#distributed list = " + theList.size() + " #pairlist list = " + G.size());
        }
        for (GenPolynomial<C> q : theList.getValueList()) {
            if (debug && q != null && !q.isZERO()) {
                logger.debug("final q = " + q.leadingExpVector());
            }
        }
        logger.debug("distributed list end");
        long time = System.currentTimeMillis();
        List<GenPolynomial<C>> Gp;
        Gp = minimalGB(G); // not jet distributed but threaded
        time = System.currentTimeMillis() - time;
        logger.debug("parallel gbmi time = " + time);
        G = Gp;
        logger.debug("server cf.terminate()");
        cf.terminate();
        logger.debug("server theList.terminate() " + theList.size());
        theList.clear();
        theList.terminate();
        t = System.currentTimeMillis() - t;
        logger.info("server GB end, time = " + t + ", " + pairlist.toString());
        return G;
    }

    /**
     * Minimal ordered groebner basis.
     *
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
        MiReducerServer<C>[] mirs = (MiReducerServer<C>[]) new MiReducerServer[G.size()];
        int i = 0;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            // System.out.println("doing " + a.length());
            List<GenPolynomial<C>> R = new ArrayList<GenPolynomial<C>>(G.size() + F.size());
            R.addAll(G);
            R.addAll(F);
            mirs[i] = new MiReducerServer<C>(R, a);
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
 * Distributed server reducing worker proxy threads.
 *
 * @param <C> coefficient type
 */
class HybridReducerServerEC<C extends RingElem<C>> implements Runnable {


    private static final Logger logger = Logger.getLogger(HybridReducerServerEC.class);


    private static final boolean debug = logger.isDebugEnabled();
    /**
     * Message tag for pairs.
     */
    public final Integer pairTag = GroebnerBaseDistributedHybridEC.pairTag;
    /**
     * Message tag for results.
     */
    public final Integer resultTag = GroebnerBaseDistributedHybridEC.resultTag;
    /**
     * Message tag for acknowledgments.
     */
    public final Integer ackTag = GroebnerBaseDistributedHybridEC.ackTag;
    private final Terminator finner;
    private final ChannelFactory cf;
    private final DistHashTable<Integer, GenPolynomial<C>> theList;
    private final PairList<C> pairlist;
    private final int threadsPerNode;
    private TaggedSocketChannel pairChannel;


    /**
     * Constructor.
     *
     * @param tpn number of threads per node
     * @param fin terminator
     * @param cf  channel factory
     * @param dl  distributed hash table
     * @param L   ordered pair list
     */
    HybridReducerServerEC(int tpn, Terminator fin, ChannelFactory cf,
                          DistHashTable<Integer, GenPolynomial<C>> dl, PairList<C> L) {
        threadsPerNode = tpn;
        finner = fin;
        this.cf = cf;
        theList = dl;
        pairlist = L;
        //logger.info("reducer server created " + this);
    }


    /**
     * Work loop.
     *
     * @see Runnable#run()
     */
    @Override
    public void run() {
        logger.info("reducer server running with " + cf);
        SocketChannel channel = null;
        try {
            channel = cf.getChannel();
            pairChannel = new TaggedSocketChannel(channel);
            pairChannel.init();
        } catch (InterruptedException e) {
            logger.debug("get pair channel interrupted");
            e.printStackTrace();
            return;
        }
        if (debug) {
            logger.info("pairChannel   = " + pairChannel);
        }
        // record idle remote workers (minus one?)
        //finner.beIdle(threadsPerNode-1);
        finner.initIdle(threadsPerNode);
        AtomicInteger active = new AtomicInteger(0);

        // start receiver
        HybridReducerReceiverEC<C> receiver = new HybridReducerReceiverEC<C>(/*threadsPerNode,*/finner,
                active, pairChannel, theList, pairlist);
        receiver.start();

        Pair<C> pair;
        //boolean set = false;
        boolean goon = true;
        //int polIndex = -1;
        int red = 0;
        int sleeps = 0;

        // while more requests
        while (goon) {
            // receive request if thread is reported incactive
            logger.debug("receive request");
            Object req = null;
            try {
                req = pairChannel.receive(pairTag);
            } catch (InterruptedException e) {
                goon = false;
                e.printStackTrace();
            } catch (IOException e) {
                goon = false;
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                goon = false;
                e.printStackTrace();
            }
            //logger.info("received request, req = " + req);
            if (req == null) {
                goon = false;
                break;
            }
            if (!(req instanceof GBTransportMessReq)) {
                goon = false;
                break;
            }

            // find pair and manage termination status
            logger.debug("find pair");
            while (!pairlist.hasNext()) { // wait
                if (!finner.hasJobs() && !pairlist.hasNext()) {
                    goon = false;
                    break;
                }
                try {
                    sleeps++;
                    if (sleeps % 3 == 0) {
                        logger.info("waiting for reducers, remaining = " + finner);
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
            if (!pairlist.hasNext() && !finner.hasJobs()) {
                logger.info("termination detection: no pairs and no jobs left");
                goon = false;
                break; //continue; //break?
            }
            finner.notIdle(); // before pairlist get!!
            pair = pairlist.removeNext();
            // send pair to client, even if null
            if (debug) {
                logger.info("active count = " + active.get());
                logger.info("send pair = " + pair);
            }
            GBTransportMess msg = null;
            if (pair != null) {
                msg = new GBTransportMessPairIndex(pair); //,pairlist.size()-1); // size-1
            } else {
                msg = new GBTransportMess(); // not End(); at this time
                // goon ?= false;
            }
            try {
                red++;
                pairChannel.send(pairTag, msg);
                @SuppressWarnings("unused")
                int a = active.getAndIncrement();
            } catch (IOException e) {
                e.printStackTrace();
                goon = false;
                break;
            }
            //logger.debug("#distributed list = " + theList.size());
        }
        logger.info("terminated, send " + red + " reduction pairs");

        /*
         * send end mark to clients
         */
        logger.debug("send end");
        try {
            for (int i = 0; i < threadsPerNode; i++) { // -1
                //do not wait: Object rq = pairChannel.receive(pairTag);
                pairChannel.send(pairTag, new GBTransportMessEnd());
            }
            // send also end to receiver
            pairChannel.send(resultTag, new GBTransportMessEnd());
            //beware of race condition 
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        receiver.terminate();

        int d = active.get();
        if (d > 0) {
            logger.info("remaining active tasks = " + d);
        }
        //logger.info("terminated, send " + red + " reduction pairs");
        pairChannel.close();
        logger.debug("redServ pairChannel.close()");
        finner.release();

        channel.close();
        logger.info("redServ channel.close()");
    }
}


/**
 * Distributed server receiving worker thread.
 *
 * @param <C> coefficient type
 */
class HybridReducerReceiverEC<C extends RingElem<C>> extends Thread {


    private static final Logger logger = Logger.getLogger(HybridReducerReceiverEC.class);


    private static final boolean debug = logger.isDebugEnabled();
    /**
     * Message tag for pairs.
     */
    public final Integer pairTag = GroebnerBaseDistributedHybridEC.pairTag;
    /**
     * Message tag for results.
     */
    public final Integer resultTag = GroebnerBaseDistributedHybridEC.resultTag;
    /**
     * Message tag for acknowledgments.
     */
    public final Integer ackTag = GroebnerBaseDistributedHybridEC.ackTag;
    private final DistHashTable<Integer, GenPolynomial<C>> theList;


    //private final int threadsPerNode;
    private final PairList<C> pairlist;
    private final TaggedSocketChannel pairChannel;
    private final Terminator finner;
    private final AtomicInteger active;
    private volatile boolean goon;


    /**
     * Constructor.
     *
     * @param fin terminator
     * @param a   active remote tasks count
     * @param pc  tagged socket channel
     * @param dl  distributed hash table
     * @param L   ordered pair list
     */
    //param tpn number of threads per node
    HybridReducerReceiverEC(/*int tpn,*/Terminator fin, AtomicInteger a, TaggedSocketChannel pc,
                            DistHashTable<Integer, GenPolynomial<C>> dl, PairList<C> L) {
        active = a;
        //threadsPerNode = tpn;
        finner = fin;
        pairChannel = pc;
        theList = dl;
        pairlist = L;
        goon = true;
        //logger.info("reducer server created " + this);
    }


    /**
     * Work loop.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        //Pair<C> pair = null;
        GenPolynomial<C> H = null;
        int red = 0;
        int polIndex = -1;
        //Integer senderId; // obsolete

        // while more requests
        while (goon) {
            // receive request
            logger.debug("receive result");
            //senderId = null;
            Object rh = null;
            try {
                rh = pairChannel.receive(resultTag);
                @SuppressWarnings("unused")
                int i = active.getAndDecrement();
            } catch (InterruptedException e) {
                goon = false;
                //e.printStackTrace();
                //?? finner.initIdle(1);
                break;
            } catch (IOException e) {
                e.printStackTrace();
                goon = false;
                finner.initIdle(1);
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                goon = false;
                finner.initIdle(1);
                break;
            }
            logger.info("received H polynomial");
            if (rh == null) {
                if (this.isInterrupted()) {
                    goon = false;
                    finner.initIdle(1);
                    break;
                }
                //finner.initIdle(1);
            } else if (rh instanceof GBTransportMessEnd) { // should only happen from server
                logger.info("received GBTransportMessEnd");
                goon = false;
                //?? finner.initIdle(1);
                break;
            } else if (rh instanceof GBTransportMessPoly) {
                // update pair list
                red++;
                GBTransportMessPoly<C> mpi = (GBTransportMessPoly<C>) rh;
                H = mpi.pol;
                //senderId = mpi.threadId;
                if (H != null) {
                    if (debug) {
                        logger.info("H = " + H.leadingExpVector());
                    }
                    if (!H.isZERO()) {
                        if (H.isONE()) {
                            // finner.allIdle();
                            polIndex = pairlist.putOne();
                            theList.putWait(Integer.valueOf(polIndex), H);
                            //goon = false; must wait for other clients
                            //finner.initIdle(1);
                            //break;
                        } else {
                            polIndex = pairlist.put(H);
                            // use putWait ? but still not all distributed
                            //GenPolynomial<C> nn = 
                            theList.putWait(Integer.valueOf(polIndex), H);
                        }
                    }
                }
            }
            // only after recording in pairlist !
            finner.initIdle(1);
            try {
                pairChannel.send(ackTag, new GBTransportMess());
                logger.debug("send acknowledgement");
            } catch (IOException e) {
                e.printStackTrace();
                goon = false;
                break;
            }
        } // end while
        goon = false;
        logger.info("terminated, received " + red + " reductions");
    }


    /**
     * Terminate.
     */
    public void terminate() {
        goon = false;
        //this.interrupt();
        try {
            this.join();
        } catch (InterruptedException e) {
            // unfug Thread.currentThread().interrupt();
        }
        logger.debug("HybridReducerReceiver terminated");
    }

}


/**
 * Distributed clients reducing worker threads.
 */
class HybridReducerClientEC<C extends RingElem<C>> implements Runnable {


    private static final Logger logger = Logger.getLogger(HybridReducerClientEC.class);


    private static final boolean debug = logger.isDebugEnabled();
    /**
     * Message tag for pairs.
     */
    public final Integer pairTag = GroebnerBaseDistributedHybridEC.pairTag;
    /**
     * Message tag for results.
     */
    public final Integer resultTag = GroebnerBaseDistributedHybridEC.resultTag;
    /**
     * Message tag for acknowledgments.
     */
    public final Integer ackTag = GroebnerBaseDistributedHybridEC.ackTag;


    //private final int threadsPerNode;


    /*
     * Identification number for this thread.
     */
    //public final Integer threadId; // obsolete
    private final TaggedSocketChannel pairChannel;
    private final DistHashTable<Integer, GenPolynomial<C>> theList;
    private final ReductionPar<C> red;


    /**
     * Constructor.
     *
     * @param tc tagged socket channel
     * @param dl distributed hash table
     */
    //param tpn number of threads per node
    //param tid thread identification
    HybridReducerClientEC(/*int tpn,*/TaggedSocketChannel tc, /*Integer tid,*/
                          DistHashTable<Integer, GenPolynomial<C>> dl) {
        //threadsPerNode = tpn;
        pairChannel = tc;
        //threadId = 100 + tid; // keep distinct from other tags
        theList = dl;
        red = new ReductionPar<C>();
    }


    /**
     * Work loop.
     *
     * @see Runnable#run()
     */
    @Override
    public void run() {
        if (debug) {
            logger.info("pairChannel   = " + pairChannel + " reducer client running");
        }
        Pair<C> pair = null;
        GenPolynomial<C> pi, pj, ps;
        GenPolynomial<C> S;
        GenPolynomial<C> H = null;
        //boolean set = false;
        boolean goon = true;
        boolean doEnd = true;
        int reduction = 0;
        //int sleeps = 0;
        Integer pix, pjx, psx;

        while (goon) {
            /* protocol:
             * request pair, process pair, send result, receive acknowledgment
             */
            // pair = (Pair) pairlist.removeNext();
            Object req = new GBTransportMessReq();
            logger.debug("send request");
            try {
                pairChannel.send(pairTag, req);
            } catch (IOException e) {
                goon = false;
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
                logger.info("receive pair, IOexception ");
                break;
            }
            logger.debug("receive pair, goon = " + goon);
            doEnd = true;
            Object pp = null;
            try {
                pp = pairChannel.receive(pairTag);
            } catch (InterruptedException e) {
                goon = false;
                e.printStackTrace();
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
            if (debug) {
                logger.info("received pair = " + pp);
            }
            H = null;
            if (pp == null) { // should not happen
                continue;
            }
            if (pp instanceof GBTransportMessEnd) {
                goon = false;
                //doEnd = false;
                continue;
            }
            if (pp instanceof GBTransportMessPair || pp instanceof GBTransportMessPairIndex) {
                pi = pj = ps = null;
                if (pp instanceof GBTransportMessPair) { // obsolet, for tests
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
                    //logger.info("ht(S) = " + S.leadingExpVector());
                    if (S.isZERO()) {
                        // pair.setZero(); does not work in dist
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("ht(S) = " + S.leadingExpVector());
                        }
                        H = red.normalform(theList, S);
                        //logger.info("ht(H) = " + H.leadingExpVector());
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
            if (pp instanceof GBTransportMess) {
                logger.debug("null pair results in null H poly");
            }

            // send H or must send null, if not at end
            if (logger.isDebugEnabled()) {
                logger.debug("#distributed list = " + theList.size());
                logger.debug("send H polynomial = " + H);
            }
            try {
                pairChannel.send(resultTag, new GBTransportMessPoly<C>(H)); //,threadId));
                doEnd = false;
            } catch (IOException e) {
                goon = false;
                e.printStackTrace();
            }
            //logger.info("done send poly message of " + pp);
            try {
                //pp = pairChannel.receive(threadId);
                pp = pairChannel.receive(ackTag);
            } catch (InterruptedException e) {
                goon = false;
                e.printStackTrace();
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
            if (!(pp instanceof GBTransportMess)) {
                logger.error("invalid acknowledgement " + pp);
            }
            logger.info("received acknowledgment ");
        }
        logger.info("terminated, " + reduction + " reductions, " + theList.size() + " polynomials");
        if (doEnd) {
            try {
                pairChannel.send(resultTag, new GBTransportMessEnd());
            } catch (IOException e) {
                //e.printStackTrace();
            }
            logger.debug("terminated, send done");
        }
    }
}


/**
 * Objects of this class are to be send to a ExecutableServer.
 */
class GBHybridExerClient<C extends RingElem<C>> implements RemoteExecutable {


    String host;


    int port;


    int dhtport;


    int threadsPerNode;


    /**
     * GBHybridExerClient.
     *
     * @param host
     * @param port
     * @param dhtport
     */
    public GBHybridExerClient(String host, int threadsPerNode, int port, int dhtport) {
        this.host = host;
        this.threadsPerNode = threadsPerNode;
        this.port = port;
        this.dhtport = dhtport;
    }


    /**
     * run.
     */
    public void run() {
        try {
            GroebnerBaseDistributedHybridEC.<C>clientPart(host, threadsPerNode, port, dhtport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * String representation.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("GBHybridExerClient(");
        s.append("host=" + host);
        s.append(", threadsPerNode=" + threadsPerNode);
        s.append(", port=" + port);
        s.append(", dhtport=" + dhtport);
        s.append(")");
        return s.toString();
    }

}
