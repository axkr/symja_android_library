/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.OrderedPairlist;
import edu.jas.gb.Pair;
import edu.jas.gb.PairList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.util.Terminator;
import edu.jas.util.ThreadPool;


/**
 * Groebner Base with recursive pseudo reduction multi-threaded parallel
 * algorithm. Implements coefficient fraction free Groebner bases.
 * Coefficients can for example be (commutative) multivariate polynomials. 
 * @param <C> coefficient type
 * @author Heinz Kredel
 * 
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */

public class GroebnerBasePseudoRecParallel<C extends GcdRingElem<C>> extends
                GroebnerBaseAbstract<GenPolynomial<C>> {


    private static final Logger logger = Logger.getLogger(GroebnerBasePseudoRecParallel.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Number of threads to use.
     */
    protected final int threads;


    /**
     * Pool of threads to use.
     */
    protected transient final ThreadPool pool;


    /**
     * Greatest common divisor engine for coefficient content and primitive
     * parts.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Pseudo reduction engine.
     */
    protected final PseudoReduction<C> redRec;


    /**
     * Pseudo reduction engine.
     */
    protected final PseudoReduction<GenPolynomial<C>> red;


    /**
     * Coefficient ring factory.
     */
    protected final RingFactory<GenPolynomial<C>> cofac;


    /**
     * Base coefficient ring factory.
     */
    protected final RingFactory<C> baseCofac;


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param rf coefficient ring factory.
     */
    public GroebnerBasePseudoRecParallel(int threads, RingFactory<GenPolynomial<C>> rf) {
        this(threads, rf, new PseudoReductionPar<GenPolynomial<C>>(), new ThreadPool(threads),
                        new OrderedPairlist<GenPolynomial<C>>(new GenPolynomialRing<GenPolynomial<C>>(rf, 1))); // 1=hack
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param rf coefficient ring factory. <b>Note:</b> red must be an instance
     *            of PseudoReductionPar.
     * @param red pseudo reduction engine.
     */
    public GroebnerBasePseudoRecParallel(int threads, RingFactory<GenPolynomial<C>> rf,
                    PseudoReduction<GenPolynomial<C>> red) {
        this(threads, rf, red, new ThreadPool(threads));
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param rf coefficient ring factory. <b>Note:</b> red must be an instance
     *            of PseudoReductionPar.
     * @param red pseudo reduction engine.
     * @param pool ThreadPool to use.
     */
    public GroebnerBasePseudoRecParallel(int threads, RingFactory<GenPolynomial<C>> rf,
                    PseudoReduction<GenPolynomial<C>> red, ThreadPool pool) {
        this(threads, rf, red, pool, new OrderedPairlist<GenPolynomial<C>>(
                        new GenPolynomialRing<GenPolynomial<C>>(rf, 1))); // 1=hack
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param rf coefficient ring factory. <b>Note:</b> red must be an instance
     *            of PseudoReductionPar.
     * @param pl pair selection strategy
     */
    public GroebnerBasePseudoRecParallel(int threads, RingFactory<GenPolynomial<C>> rf,
                    PairList<GenPolynomial<C>> pl) {
        this(threads, rf, new PseudoReductionPar<GenPolynomial<C>>(), new ThreadPool(threads), pl);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param rf coefficient ring factory. <b>Note:</b> red must be an instance
     *            of PseudoReductionPar.
     * @param red pseudo reduction engine.
     * @param pool ThreadPool to use.
     * @param pl pair selection strategy
     */
    @SuppressWarnings("cast")
    public GroebnerBasePseudoRecParallel(int threads, RingFactory<GenPolynomial<C>> rf,
                    PseudoReduction<GenPolynomial<C>> red, ThreadPool pool, PairList<GenPolynomial<C>> pl) {
        super(red, pl);
        if (!(red instanceof PseudoReductionPar)) {
            logger.warn("parallel GB should use parallel aware reduction");
        }
        this.red = red;
        this.redRec = (PseudoReduction<C>) (PseudoReduction) red;
        cofac = rf;
        if (threads < 1) {
            threads = 1;
        }
        this.threads = threads;
        GenPolynomialRing<C> rp = (GenPolynomialRing<C>) cofac;
        baseCofac = rp.coFac;
        //engine = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getImplementation( baseCofac );
        //not used: 
        engine = GCDFactory.<C> getProxy(baseCofac);
        this.pool = pool;
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    @Override
    public void terminate() {
        if (pool == null) {
            return;
        }
        pool.terminate();
    }


    /**
     * Cancel ThreadPool.
     */
    @Override
    public int cancel() {
        if (pool == null) {
            return 0;
        }
        int s = pool.cancel();
        return s;
    }


    /**
     * Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<GenPolynomial<C>>> GB(int modv, List<GenPolynomial<GenPolynomial<C>>> F) {
        List<GenPolynomial<GenPolynomial<C>>> G = normalizeZerosOnes(F);
        G = engine.recursivePrimitivePart(G);
        if (G.size() <= 1) {
            return G;
        }
        GenPolynomialRing<GenPolynomial<C>> ring = G.get(0).ring;
        if (ring.coFac.isField()) { // TODO remove
            throw new IllegalArgumentException("coefficients from a field");
        }
        PairList<GenPolynomial<C>> pairlist = strategy.create(modv, ring);
        pairlist.put(G);

        /*
        GenPolynomial<GenPolynomial<C>> p;
        List<GenPolynomial<GenPolynomial<C>>> G = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        PairList<GenPolynomial<C>> pairlist = null;
        int l = F.size();
        ListIterator<GenPolynomial<GenPolynomial<C>>> it = F.listIterator();
        while (it.hasNext()) {
            p = it.next();
            if (p.length() > 0) {
                p = engine.recursivePrimitivePart(p); //p.monic();
                p = p.abs();
                if (p.isConstant()) {
                    G.clear();
                    G.add(p);
                    return G; // since no threads are activated
                }
                G.add(p);
                if (pairlist == null) {
                    //pairlist = new OrderedPairlist<C>(modv, p.ring);
                    pairlist = strategy.create(modv, p.ring);
                }
                // putOne not required
                pairlist.put(p);
            } else {
                l--;
            }
        }
        if (l <= 1) {
            return G; // since no threads are activated
        }
        */
        logger.info("start " + pairlist);

        Terminator fin = new Terminator(threads);
        PseudoReducerRec<C> R;
        for (int i = 0; i < threads; i++) {
            R = new PseudoReducerRec<C>(fin, G, pairlist, engine);
            pool.addJob(R);
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
    public List<GenPolynomial<GenPolynomial<C>>> minimalGB(List<GenPolynomial<GenPolynomial<C>>> Gp) {
        List<GenPolynomial<GenPolynomial<C>>> G = normalizeZerosOnes(Gp);
        /*
        if (Gp == null || Gp.size() <= 1) {
            return Gp;
        }
        // remove zero polynomials
        List<GenPolynomial<GenPolynomial<C>>> G = new ArrayList<GenPolynomial<GenPolynomial<C>>>(Gp.size());
        for (GenPolynomial<GenPolynomial<C>> a : Gp) {
            if (a != null && !a.isZERO()) { // always true in GB()
                // already positive a = a.abs();
                G.add(a);
            }
        }
        */
        if (G.size() <= 1) {
            return G;
        }
        // remove top reducible polynomials
        GenPolynomial<GenPolynomial<C>> a;
        List<GenPolynomial<GenPolynomial<C>>> F;
        F = new ArrayList<GenPolynomial<GenPolynomial<C>>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                // drop polynomial 
                if (debug) {
                    System.out.println("dropped " + a);
                    List<GenPolynomial<GenPolynomial<C>>> ff;
                    ff = new ArrayList<GenPolynomial<GenPolynomial<C>>>(G);
                    ff.addAll(F);
                    //a = red.normalform(ff, a);
                    a = redRec.normalformRecursive(ff, a);
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
        @SuppressWarnings("cast")
        PseudoMiReducerRec<C>[] mirs = (PseudoMiReducerRec<C>[]) new PseudoMiReducerRec[G.size()];
        int i = 0;
        F = new ArrayList<GenPolynomial<GenPolynomial<C>>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            List<GenPolynomial<GenPolynomial<C>>> R = new ArrayList<GenPolynomial<GenPolynomial<C>>>(G.size()
                            + F.size());
            R.addAll(G);
            R.addAll(F);
            // System.out.println("doing " + a.length());
            mirs[i] = new PseudoMiReducerRec<C>(R, a, engine);
            pool.addJob(mirs[i]);
            i++;
            F.add(a);
        }
        G = F;
        F = new ArrayList<GenPolynomial<GenPolynomial<C>>>(G.size());
        for (i = 0; i < mirs.length; i++) {
            a = mirs[i].getNF();
            F.add(a);
        }
        return F;
    }


    /**
     * Groebner base simple test.
     * @param modv module variable number.
     * @param F recursive polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    @Override
    public boolean isGBsimple(int modv, List<GenPolynomial<GenPolynomial<C>>> F) {
        if (F == null || F.isEmpty()) {
            return true;
        }
        GenPolynomial<GenPolynomial<C>> pi, pj, s, h;
        ExpVector ei, ej, eij;
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            ei = pi.leadingExpVector();
            for (int j = i + 1; j < F.size(); j++) {
                pj = F.get(j);
                ej = pj.leadingExpVector();
                if (!red.moduleCriterion(modv, ei, ej)) {
                    continue;
                }
                eij = ei.lcm(ej);
                if (!red.criterion4(ei, ej, eij)) {
                    continue;
                }
                //if (!criterion3(i, j, eij, F)) {
                //    continue;
                //}
                s = red.SPolynomial(pi, pj);
                if (s.isZERO()) {
                    continue;
                }
                //System.out.println("i, j = " + i + ", " + j); 
                h = redRec.normalformRecursive(F, s);
                if (!h.isZERO()) {
                    logger.info("no GB: pi = " + pi + ", pj = " + pj);
                    logger.info("s  = " + s + ", h = " + h);
                    return false;
                }
            }
        }
        return true;
    }

}


/**
 * Pseudo GB Reducing worker threads.
 */
class PseudoReducerRec<C extends GcdRingElem<C>> implements Runnable {


    private final List<GenPolynomial<GenPolynomial<C>>> G;


    private final PairList<GenPolynomial<C>> pairlist;


    private final Terminator fin;


    private final PseudoReductionPar<GenPolynomial<C>> red;


    private final PseudoReductionPar<C> redRec;


    private final GreatestCommonDivisorAbstract<C> engine;


    private static final Logger logger = Logger.getLogger(PseudoReducerRec.class);


    PseudoReducerRec(Terminator fin, List<GenPolynomial<GenPolynomial<C>>> G, PairList<GenPolynomial<C>> L,
                    GreatestCommonDivisorAbstract<C> engine) {
        this.fin = fin;
        this.G = G;
        pairlist = L;
        red = new PseudoReductionPar<GenPolynomial<C>>();
        redRec = new PseudoReductionPar<C>();
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
        Pair<GenPolynomial<C>> pair;
        GenPolynomial<GenPolynomial<C>> pi, pj, S, H;
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

            //H = red.normalform(G, S); //mod
            H = redRec.normalformRecursive(G, S);
            reduction++;
            if (H.isZERO()) {
                pair.setZero();
                fin.initIdle(1);
                continue;
            }
            if (logger.isDebugEnabled()) {
                logger.info("ht(H) = " + H.leadingExpVector());
            }

            H = engine.recursivePrimitivePart(H); //H.monic();
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
class PseudoMiReducerRec<C extends GcdRingElem<C>> implements Runnable {


    private final List<GenPolynomial<GenPolynomial<C>>> G;


    private GenPolynomial<GenPolynomial<C>> H;


    //private final PseudoReductionPar<GenPolynomial<C>> red;


    private final PseudoReductionPar<C> redRec;


    private final Semaphore done = new Semaphore(0);


    private final GreatestCommonDivisorAbstract<C> engine;


    private static final Logger logger = Logger.getLogger(PseudoMiReducerRec.class);


    PseudoMiReducerRec(List<GenPolynomial<GenPolynomial<C>>> G, GenPolynomial<GenPolynomial<C>> p,
                    GreatestCommonDivisorAbstract<C> engine) {
        this.G = G;
        this.engine = engine;
        H = p;
        //red = new PseudoReductionPar<GenPolynomial<C>>();
        redRec = new PseudoReductionPar<C>();
    }


    /**
     * to string
     */
    @Override
    public String toString() {
        return "PseudoMiReducerRec";
    }


    /**
     * getNF. Blocks until the normal form is computed.
     * @return the computed normal form.
     */
    public GenPolynomial<GenPolynomial<C>> getNF() {
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
            //H = red.normalform(G, H); //mod
            H = redRec.normalformRecursive(G, H);
            H = engine.recursivePrimitivePart(H); //H.monic();
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
