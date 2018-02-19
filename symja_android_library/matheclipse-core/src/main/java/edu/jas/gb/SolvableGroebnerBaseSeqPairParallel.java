/*
 * $Id: SolvableGroebnerBaseSeqPairParallel.java 3990 2012-07-14 12:46:08Z
 * kredel $
 */

package edu.jas.gb;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Semaphore;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.RingElem;
import edu.jas.util.Terminator;
import edu.jas.util.ThreadPool;


/**
 * Solvable Groebner Base parallel algorithm. Makes some effort to produce the
 * same sequence of critical pairs as in the sequential version. However already
 * reduced pairs are not rereduced if new polynomials appear. Implements a
 * shared memory parallel version of Groebner bases. Threads maintain pairlist.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SolvableGroebnerBaseSeqPairParallel<C extends RingElem<C>> extends
        SolvableGroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(SolvableGroebnerBaseSeqPairParallel.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Number of threads to use.
     */
    protected final int threads;


    /**
     * Pool of threads to use.
     */
    protected transient final ThreadPool pool;


    /**
     * Constructor.
     */
    public SolvableGroebnerBaseSeqPairParallel() {
        this(2);
    }


    /**
     * Constructor.
     *
     * @param threads number of threads to use.
     */
    public SolvableGroebnerBaseSeqPairParallel(int threads) {
        this(threads, new ThreadPool(threads));
    }


    /**
     * Constructor.
     *
     * @param threads number of threads to use.
     * @param pool    ThreadPool to use.
     */
    public SolvableGroebnerBaseSeqPairParallel(int threads, ThreadPool pool) {
        this(threads, pool, new SolvableReductionPar<C>());
    }


    /**
     * Constructor.
     *
     * @param threads number of threads to use.
     * @param red     parallelism aware reduction engine
     */
    public SolvableGroebnerBaseSeqPairParallel(int threads, SolvableReduction<C> red) {
        this(threads, new ThreadPool(threads), red);
    }


    /**
     * Constructor.
     *
     * @param threads number of threads to use.
     * @param pool    ThreadPool to use.
     * @param sred    parallelism aware reduction engine
     */
    public SolvableGroebnerBaseSeqPairParallel(int threads, ThreadPool pool, SolvableReduction<C> sred) {
        super(sred);
        if (!(sred instanceof SolvableReductionPar)) {
            logger.warn("parallel GB should use parallel aware reduction");
        }
        if (threads < 1) {
            threads = 1;
        }
        this.threads = threads;
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
     * Parallel Groebner base using sequential pair order class. Threads
     * maintain pairlist.
     *
     * @param modv number of module variables.
     * @param F    polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> leftGB(int modv, List<GenSolvablePolynomial<C>> F) {
        GenSolvablePolynomial<C> p;
        List<GenSolvablePolynomial<C>> G = new ArrayList<GenSolvablePolynomial<C>>();
        CriticalPairList<C> pairlist = null;
        int l = F.size();
        ListIterator<GenSolvablePolynomial<C>> it = F.listIterator();
        while (it.hasNext()) {
            p = it.next();
            if (p.length() > 0) {
                p = p.monic();
                if (p.isONE()) {
                    G.clear();
                    G.add(p);
                    return G; // since no threads activated jet
                }
                G.add(p);
                if (pairlist == null) {
                    pairlist = new CriticalPairList<C>(modv, p.ring);
                    if (!p.ring.coFac.isField()) {
                        throw new IllegalArgumentException("coefficients not from a field");
                    }
                }
                // putOne not required
                pairlist.put(p);
            } else {
                l--;
            }
        }
        if (l <= 1) {
            return G; // since no threads activated jet
        }

        Terminator fin = new Terminator(threads);
        LeftSolvableReducerSeqPair<C> R;
        for (int i = 0; i < threads; i++) {
            R = new LeftSolvableReducerSeqPair<C>(fin, G, pairlist);
            pool.addJob(R);
        }
        fin.waitDone();
        logger.debug("#parallel list = " + G.size());
        G = leftMinimalGB(G);
        // not in this context // pool.terminate();
        logger.info("" + pairlist);
        return G;
    }


    /**
     * Minimal ordered groebner basis, parallel.
     *
     * @param Fp a Groebner base.
     * @return minimalGB(F) a minimal Groebner base of Fp.
     */
    @Override
    public List<GenSolvablePolynomial<C>> leftMinimalGB(List<GenSolvablePolynomial<C>> Fp) {
        GenSolvablePolynomial<C> a;
        ArrayList<GenSolvablePolynomial<C>> G;
        G = new ArrayList<GenSolvablePolynomial<C>>(Fp.size());
        ListIterator<GenSolvablePolynomial<C>> it = Fp.listIterator();
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
        GenSolvablePolynomial<C> p;
        ArrayList<GenSolvablePolynomial<C>> F;
        F = new ArrayList<GenSolvablePolynomial<C>>(G.size());
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
                F.add(a); // no thread at this point
            } else {
                // System.out.println("dropped " + a.length());
            }
        }
        G = F;
        if (G.size() <= 1) {
            return G;
        }

        @SuppressWarnings("cast")
        SolvableMiReducerSeqPair<C>[] mirs = (SolvableMiReducerSeqPair<C>[]) new SolvableMiReducerSeqPair[G
                .size()];
        int i = 0;
        F = new ArrayList<GenSolvablePolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            // System.out.println("doing " + a.length());
            List<GenSolvablePolynomial<C>> R = new ArrayList<GenSolvablePolynomial<C>>(G.size() + F.size());
            R.addAll(G);
            R.addAll(F);
            mirs[i] = new SolvableMiReducerSeqPair<C>(R, a);
            pool.addJob(mirs[i]);
            i++;
            F.add(a);
        }
        G = F;
        F = new ArrayList<GenSolvablePolynomial<C>>(G.size());
        for (i = 0; i < mirs.length; i++) {
            a = mirs[i].getNF();
            F.add(a);
        }
        return F;
    }


    /**
     * Solvable Extended Groebner base using critical pair class.
     *
     * @param modv module variable number.
     * @param F    solvable polynomial list.
     * @return a container for an extended left Groebner base of F.
     */
    public SolvableExtendedGB<C> extLeftGB(int modv, List<GenSolvablePolynomial<C>> F) {
        throw new UnsupportedOperationException("parallel extLeftGB not implemented");
    }


    /**
     * Twosided Groebner base using pairlist class.
     *
     * @param modv number of module variables.
     * @param Fp   solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> twosidedGB(int modv, List<GenSolvablePolynomial<C>> Fp) {
        if (Fp == null || Fp.size() == 0) { // 0 not 1
            return new ArrayList<GenSolvablePolynomial<C>>();
        }
        GenSolvablePolynomialRing<C> ring = Fp.get(0).ring; // assert != null
        // add also coefficient generators
        List<GenSolvablePolynomial<C>> X;
        X = PolynomialList.castToSolvableList(ring.generators(modv));
        logger.info("right multipliers = " + X);
        List<GenSolvablePolynomial<C>> F = new ArrayList<GenSolvablePolynomial<C>>(Fp.size() * (1 + X.size()));
        F.addAll(Fp);
        GenSolvablePolynomial<C> p, x, q;
        for (int i = 0; i < F.size(); i++) { // F changes
            p = F.get(i);
            for (int j = 0; j < X.size(); j++) {
                x = X.get(j);
                if (x.isONE()) {
                    continue;
                }
                q = p.multiply(x);
                q = sred.leftNormalform(F, q);
                if (!q.isZERO()) {
                    F.add(q);
                }
            }
        }
        //System.out.println("F generated = " + F);
        List<GenSolvablePolynomial<C>> G = new ArrayList<GenSolvablePolynomial<C>>();
        CriticalPairList<C> pairlist = null;
        int l = F.size();
        ListIterator<GenSolvablePolynomial<C>> it = F.listIterator();
        while (it.hasNext()) {
            p = it.next();
            if (p.length() > 0) {
                p = p.monic();
                if (p.isONE()) {
                    G.clear();
                    G.add(p);
                    return G; // since no threads are activated
                }
                G.add(p);
                if (pairlist == null) {
                    pairlist = new CriticalPairList<C>(modv, p.ring);
                    if (!p.ring.coFac.isField()) {
                        throw new IllegalArgumentException("coefficients not from a field");
                    }
                }
                // putOne not required
                pairlist.put(p);
            } else {
                l--;
            }
        }
        //System.out.println("G to check = " + G);
        if (l <= 1) { // 1 ok
            return G; // since no threads are activated
        }
        Terminator fin = new Terminator(threads);
        TwosidedSolvableReducerSeqPair<C> R;
        for (int i = 0; i < threads; i++) {
            R = new TwosidedSolvableReducerSeqPair<C>(fin, X, G, pairlist);
            pool.addJob(R);
        }
        fin.waitDone();
        logger.debug("#parallel list = " + G.size());
        G = leftMinimalGB(G);
        // not in this context // pool.terminate();
        logger.info("" + pairlist);
        return G;
    }

}


/**
 * Reducing left worker threads.
 *
 * @param <C> coefficient type
 */
class LeftSolvableReducerSeqPair<C extends RingElem<C>> implements Runnable {


    private static final Logger logger = Logger.getLogger(LeftSolvableReducerSeqPair.class);
    private static final boolean debug = logger.isDebugEnabled();
    private final List<GenSolvablePolynomial<C>> G;
    private final CriticalPairList<C> pairlist;
    private final Terminator pool;
    private final SolvableReductionPar<C> sred;


    LeftSolvableReducerSeqPair(Terminator fin, List<GenSolvablePolynomial<C>> G, CriticalPairList<C> L) {
        pool = fin;
        this.G = G;
        pairlist = L;
        sred = new SolvableReductionPar<C>();
    }


    @SuppressWarnings("unchecked")
    public void run() {
        CriticalPair<C> pair;
        GenSolvablePolynomial<C> S;
        GenSolvablePolynomial<C> H;
        boolean set = false;
        int reduction = 0;
        int sleeps = 0;
        while (pairlist.hasNext() || pool.hasJobs()) {
            while (!pairlist.hasNext()) {
                pairlist.update();
                // wait
                pool.beIdle();
                set = true;
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
                if (!pool.hasJobs()) {
                    break;
                }
            }
            if (!pairlist.hasNext() && !pool.hasJobs()) {
                break;
            }
            if (set) {
                pool.notIdle();
                set = false;
            }
            pair = pairlist.getNext();
            if (pair == null) {
                pairlist.update();
                continue;
            }
            if (debug) {
                logger.debug("pi = " + pair.pi);
                logger.debug("pj = " + pair.pj);
            }
            S = sred.leftSPolynomial((GenSolvablePolynomial<C>) pair.pi, (GenSolvablePolynomial<C>) pair.pj);
            if (S.isZERO()) {
                pairlist.record(pair, S);
                continue;
            }
            if (debug) {
                logger.debug("ht(S) = " + S.leadingExpVector());
            }
            H = sred.leftNormalform(G, S); //mod
            reduction++;
            if (H.isZERO()) {
                pairlist.record(pair, H);
                continue;
            }
            if (debug) {
                logger.debug("ht(H) = " + H.leadingExpVector());
            }
            H = H.monic();
            // System.out.println("H   = " + H);
            if (H.isONE()) {
                // pairlist.update( pair, H );
                pairlist.putOne(); // not really required
                synchronized (G) {
                    G.clear();
                    G.add(H);
                }
                pool.allIdle();
                return;
            }
            if (debug) {
                logger.debug("H = " + H);
            }
            synchronized (G) {
                G.add(H);
            }
            pairlist.update(pair, H);
            //pairlist.record( pair, H );
            //pairlist.update();
        }
        logger.info("terminated, done " + reduction + " reductions");
    }
}


/**
 * Reducing twosided worker threads.
 *
 * @param <C> coefficient type
 */
class TwosidedSolvableReducerSeqPair<C extends RingElem<C>> implements Runnable {


    private static final Logger logger = Logger.getLogger(TwosidedSolvableReducerSeqPair.class);
    private static final boolean debug = logger.isDebugEnabled();
    private final List<GenSolvablePolynomial<C>> X;
    private final List<GenSolvablePolynomial<C>> G;
    private final CriticalPairList<C> pairlist;
    private final Terminator pool;
    private final SolvableReductionPar<C> sred;


    TwosidedSolvableReducerSeqPair(Terminator fin, List<GenSolvablePolynomial<C>> X,
                                   List<GenSolvablePolynomial<C>> G, CriticalPairList<C> L) {
        pool = fin;
        this.X = X;
        this.G = G;
        pairlist = L;
        sred = new SolvableReductionPar<C>();
    }


    public void run() {
        GenSolvablePolynomial<C> p, x;
        CriticalPair<C> pair;
        GenSolvablePolynomial<C> S;
        GenSolvablePolynomial<C> H;
        boolean set = false;
        int reduction = 0;
        int sleeps = 0;
        while (pairlist.hasNext() || pool.hasJobs()) {
            while (!pairlist.hasNext()) {
                pairlist.update();
                // wait
                pool.beIdle();
                set = true;
                try {
                    sleeps++;
                    if (sleeps % 10 == 0) {
                        logger.info(" reducer is sleeping");
                    } else {
                        logger.debug("r");
                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
                if (!pool.hasJobs()) {
                    break;
                }
            }
            if (!pairlist.hasNext() && !pool.hasJobs()) {
                break;
            }
            if (set) {
                pool.notIdle();
                set = false;
            }
            pair = pairlist.getNext();
            if (pair == null) {
                pairlist.update();
                continue;
            }
            if (debug) {
                logger.debug("pi = " + pair.pi);
                logger.debug("pj = " + pair.pj);
            }
            S = sred.leftSPolynomial((GenSolvablePolynomial<C>) pair.pi, (GenSolvablePolynomial<C>) pair.pj);
            if (S.isZERO()) {
                pairlist.record(pair, S);
                continue;
            }
            if (debug) {
                logger.debug("ht(S) = " + S.leadingExpVector());
            }
            H = sred.leftNormalform(G, S); //mod
            reduction++;
            if (H.isZERO()) {
                pairlist.record(pair, H);
                continue;
            }
            if (debug) {
                logger.debug("ht(H) = " + H.leadingExpVector());
            }
            H = H.monic();
            // System.out.println("H   = " + H);
            if (H.isONE()) {
                // pairlist.update( pair, H );
                pairlist.putOne(); // not really required
                synchronized (G) {
                    G.clear();
                    G.add(H);
                }
                pool.allIdle();
                return;
            }
            if (debug) {
                logger.debug("H = " + H);
            }
            synchronized (G) {
                G.add(H);
            }
            pairlist.update(pair, H);
            for (int j = 0; j < X.size(); j++) {
                x = X.get(j);
                if (x.isONE()) {
                    continue;
                }
                p = H.multiply(x);
                p = sred.leftNormalform(G, p);
                if (!p.isZERO()) {
                    p = p.monic();
                    if (p.isONE()) {
                        synchronized (G) {
                            G.clear();
                            G.add(p);
                        }
                        pool.allIdle();
                        return;
                    }
                    synchronized (G) {
                        G.add(p);
                    }
                    pairlist.put(p);
                }
            }
        }
        logger.info("terminated, done " + reduction + " reductions");
    }
}


/**
 * Reducing worker threads for minimal GB.
 *
 * @param <C> coefficient type
 */
class SolvableMiReducerSeqPair<C extends RingElem<C>> implements Runnable {


    private static final Logger logger = Logger.getLogger(SolvableMiReducerSeqPair.class);
    private static final boolean debug = logger.isDebugEnabled();
    private final List<GenSolvablePolynomial<C>> G;
    private final SolvableReductionPar<C> sred;
    private final Semaphore done = new Semaphore(0);
    private GenSolvablePolynomial<C> H;


    SolvableMiReducerSeqPair(List<GenSolvablePolynomial<C>> G, GenSolvablePolynomial<C> p) {
        this.G = G;
        H = p;
        sred = new SolvableReductionPar<C>();
    }


    /**
     * getNF. Blocks until the normal form is computed.
     *
     * @return the computed normal form.
     */
    public GenSolvablePolynomial<C> getNF() {
        try {
            done.acquire(); //done.P();
        } catch (InterruptedException e) {
        }
        return H;
    }


    public void run() {
        if (debug) {
            logger.debug("ht(H) = " + H.leadingExpVector());
        }
        H = sred.leftNormalform(G, H); //mod
        done.release(); //done.V();
        if (debug) {
            logger.debug("ht(H) = " + H.leadingExpVector());
        }
        // H = H.monic();
    }

}
