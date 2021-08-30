/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.OrderedPairlist;
import edu.jas.gb.Pair;
import edu.jas.gb.PairList;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.util.Terminator;


/**
 * Groebner Base with pseudo reduction multi-threaded parallel algorithm.
 * Implements coefficient fraction free Groebner bases.
 * Coefficients can for example be integers or (commutative) univariate polynomials.
 * @param <C> coefficient type
 * @author Heinz Kredel
 * 
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */

public class GroebnerBasePseudoParallel<C extends GcdRingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = LogManager.getLogger(GroebnerBasePseudoParallel.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Number of threads to use.
     */
    protected final int threads;


    /**
     * Pool of threads to use.
     */
    protected transient final ExecutorService pool;


    /**
     * Greatest common divisor engine for coefficient content and primitive
     * parts.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Pseudo reduction engine.
     */
    protected final PseudoReduction<C> red;


    /**
     * Coefficient ring factory.
     */
    protected final RingFactory<C> cofac;


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param rf coefficient ring factory.
     */
    public GroebnerBasePseudoParallel(int threads, RingFactory<C> rf) {
        this(threads, rf, new PseudoReductionPar<C>());
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param rf coefficient ring factory. <b>Note:</b> red must be an instance
     *            of PseudoReductionPar.
     * @param red pseudo reduction engine.
     */
    public GroebnerBasePseudoParallel(int threads, RingFactory<C> rf, PseudoReduction<C> red) {
        this(threads, rf, red, Executors.newFixedThreadPool(threads));
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param rf coefficient ring factory. <b>Note:</b> red must be an instance
     *            of PseudoReductionPar.
     * @param red pseudo reduction engine.
     * @param pool ExecutorService to use.
     */
    public GroebnerBasePseudoParallel(int threads, RingFactory<C> rf, PseudoReduction<C> red, ExecutorService pool) {
        this(threads, rf, red, pool, new OrderedPairlist<C>());
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param rf coefficient ring factory. <b>Note:</b> red must be an instance
     *            of PseudoReductionPar.
     * @param pl pair selection strategy
     */
    public GroebnerBasePseudoParallel(int threads, RingFactory<C> rf, PairList<C> pl) {
        this(threads, rf, new PseudoReductionPar<C>(), Executors.newFixedThreadPool(threads), pl);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param rf coefficient ring factory. <b>Note:</b> red must be an instance
     *            of PseudoReductionPar.
     * @param red pseudo reduction engine.
     * @param pool ExecutorService to use.
     * @param pl pair selection strategy
     */
    public GroebnerBasePseudoParallel(int threads, RingFactory<C> rf, PseudoReduction<C> red,
                    ExecutorService pool, PairList<C> pl) {
        super(red, pl);
        if (!(red instanceof PseudoReductionPar)) {
            logger.warn("parallel GB should use parallel aware reduction");
        }
        this.red = red;
        cofac = rf;
        if (threads < 1) {
            threads = 1;
        }
        this.threads = threads;
        engine = GCDFactory.<C> getImplementation(rf);
        //not used: engine = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getProxy( rf );
        this.pool = pool;
    }


    /**
     * Cleanup and terminate ExecutorService.
     */
    @Override
    public void terminate() {
        if (pool == null) {
            return;
        }
        pool.shutdown();
        try {
            while (!pool.isTerminated()) {
                //logger.info("await");
                boolean rest = pool.awaitTermination(1000L, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info(pool.toString());
    }


    /**
     * Cancel ExecutorService.
     */
    @Override
    public int cancel() {
        if (pool == null) {
            return 0;
        }
        int s = pool.shutdownNow().size();
        logger.info(pool.toString());
        return s;
    }


    /**
     * Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(F);
        G = engine.basePrimitivePart(G);
        if ( G.size() <= 1 ) {
            return G;
        }
        GenPolynomialRing<C> ring = G.get(0).ring;
        if ( ring.coFac.isField() ) { // remove ?
            throw new IllegalArgumentException("coefficients from a field");
        }
        PairList<C> pairlist = strategy.create( modv, ring ); 
        pairlist.put(G);
        logger.info("start " + pairlist);

        Terminator fin = new Terminator(threads);
        PseudoReducer<C> R;
        for (int i = 0; i < threads; i++) {
            R = new PseudoReducer<C>(fin, G, pairlist, engine);
            pool.execute(R);
        }
        fin.waitDone();
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("interrupt before minimalGB");
        }
        logger.debug("#parallel list = " + G.size());
        G = minimalGB(G);
        logger.info("" + pairlist);
        return G;
    }


    /**
     * Minimal ordered Groebner basis.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    @Override
    public List<GenPolynomial<C>> minimalGB(List<GenPolynomial<C>> Gp) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(Gp);
        if (G.size() <= 1) {
            return G;
        }
        // remove top reducible polynomials
        GenPolynomial<C> a;
        List<GenPolynomial<C>> F;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                // drop polynomial 
                if (debug) {
                    System.out.println("dropped " + a);
                    List<GenPolynomial<C>> ff;
                    ff = new ArrayList<GenPolynomial<C>>(G);
                    ff.addAll(F);
                    a = red.normalform(ff, a);
                    if (!a.isZERO()) {
                        System.out.println("error, nf(a) " + a);
                    }
                }
            } else {
                F.add(a);
            }
        }
        G = F;
        if (G.size() <= 1) {
            return G;
        }
        Collections.reverse(G); // important for lex GB
        // reduce remaining polynomials
        @SuppressWarnings("unchecked")
        PseudoMiReducer<C>[] mirs = (PseudoMiReducer<C>[]) new PseudoMiReducer[G.size()];
        int i = 0;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            List<GenPolynomial<C>> R = new ArrayList<GenPolynomial<C>>(G.size() + F.size());
            R.addAll(G);
            R.addAll(F);
            // System.out.println("doing " + a.length());
            mirs[i] = new PseudoMiReducer<C>(R, a, engine);
            pool.execute(mirs[i]);
            i++;
            F.add(a);
        }
        G = F;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        for (i = 0; i < mirs.length; i++) {
            a = mirs[i].getNF();
            F.add(a);
        }
        Collections.reverse(F); // undo reverse
        return F;
    }

}


/**
 * Pseudo GB Reducing worker threads.
 */
class PseudoReducer<C extends GcdRingElem<C>> implements Runnable {


    private final List<GenPolynomial<C>> G;


    private final PairList<C> pairlist;


    private final Terminator fin;


    private final PseudoReductionPar<C> red;


    private final GreatestCommonDivisorAbstract<C> engine;


    private static final Logger logger = LogManager.getLogger(PseudoReducer.class);


    PseudoReducer(Terminator fin, List<GenPolynomial<C>> G, PairList<C> L,
                    GreatestCommonDivisorAbstract<C> engine) {
        this.fin = fin;
        this.G = G;
        pairlist = L;
        red = new PseudoReductionPar<C>();
        this.engine = engine;
        fin.initIdle(1);
    }


    /**
     * to string
     */
    @Override
    public String toString() {
        return "PseudoReducer";
    }


    public void run() {
        Pair<C> pair;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        GenPolynomial<C> H;
        //boolean set = false;
        int reduction = 0;
        int sleeps = 0;
        while (pairlist.hasNext() || fin.hasJobs()) {
            while (!pairlist.hasNext()) {
                // wait
                //fin.beIdle(); set = true;
                try {
                    sleeps++;
                    if (sleeps % 10 == 0) {
                        logger.info(" reducer is sleeping");
                    } else {
                        logger.debug("r");
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
                if (!fin.hasJobs()) {
                    break;
                }
            }
            if (!pairlist.hasNext() && !fin.hasJobs()) {
                break;
            }

            fin.notIdle(); // before pairlist get
            pair = pairlist.removeNext();
            if (Thread.currentThread().isInterrupted()) {
                fin.initIdle(1);
                throw new RuntimeException("interrupt after removeNext");
            }
            if (pair == null) {
                fin.initIdle(1);
                continue;
            }

            pi = pair.pi;
            pj = pair.pj;
            if (logger.isDebugEnabled()) {
                logger.debug("pi    = " + pi);
                logger.debug("pj    = " + pj);
            }

            S = red.SPolynomial(pi, pj);
            if (S.isZERO()) {
                pair.setZero();
                fin.initIdle(1);
                continue;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("ht(S) = " + S.leadingExpVector());
            }

            H = red.normalform(G, S); //mod
            reduction++;
            if (H.isZERO()) {
                pair.setZero();
                fin.initIdle(1);
                continue;
            }
            if (logger.isDebugEnabled()) {
                logger.info("ht(H) = " + H.leadingExpVector());
            }

            H = engine.basePrimitivePart(H); //H.monic();
            H = H.abs();
            // System.out.println("H   = " + H);
            if (H.isONE()) {
                // putOne not required
                pairlist.put(H);
                synchronized (G) {
                    G.clear();
                    G.add(H);
                }
                fin.allIdle();
                return;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("H = " + H);
            }
            synchronized (G) {
                G.add(H);
            }
            pairlist.put(H);
            fin.initIdle(1);
        }
        fin.allIdle();
        logger.info("terminated, done " + reduction + " reductions");
    }
}


/**
 * Pseudo Reducing worker threads for minimal GB.
 */
class PseudoMiReducer<C extends GcdRingElem<C>> implements Runnable {


    private final List<GenPolynomial<C>> G;


    private GenPolynomial<C> H;


    private final PseudoReductionPar<C> red;


    private final Semaphore done = new Semaphore(0);


    private final GreatestCommonDivisorAbstract<C> engine;


    private static final Logger logger = LogManager.getLogger(PseudoMiReducer.class);


    PseudoMiReducer(List<GenPolynomial<C>> G, GenPolynomial<C> p, GreatestCommonDivisorAbstract<C> engine) {
        this.G = G;
        this.engine = engine;
        H = p;
        red = new PseudoReductionPar<C>();
    }


    /**
     * to string
     */
    @Override
    public String toString() {
        return "PseudoMiReducer";
    }


    /**
     * getNF. Blocks until the normal form is computed.
     * @return the computed normal form.
     */
    public GenPolynomial<C> getNF() {
        try {
            done.acquire(); //done.P();
        } catch (InterruptedException e) {
            throw new RuntimeException("interrupt in getNF");
        }
        return H;
    }


    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("ht(H) = " + H.leadingExpVector());
        }
        try {
            H = red.normalform(G, H); //mod
            H = engine.basePrimitivePart(H); //H.monic();
            H = H.abs();
            done.release(); //done.V();
        } catch (RuntimeException e) {
            Thread.currentThread().interrupt();
            //throw new RuntimeException("interrupt in getNF");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("ht(H) = " + H.leadingExpVector());
        }
        // H = H.monic();
    }

}
