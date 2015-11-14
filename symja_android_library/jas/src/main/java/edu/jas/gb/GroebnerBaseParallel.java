/*
 * $Id$
 */

package edu.jas.gb;


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
import edu.jas.util.Terminator;
import edu.jas.util.ThreadPool;


/**
 * Groebner Base parallel algortihm. Implements a shared memory parallel version
 * of Groebner bases.
 * @param <C> coefficient type
 * @author Heinz Kredel
 * 
 * @see edu.jas.application.GBAlgorithmBuilder
 * @see edu.jas.gbufd.GBFactory
 */

public class GroebnerBaseParallel<C extends RingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GroebnerBaseParallel.class);


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
    public GroebnerBaseParallel() {
        this(2);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     */
    public GroebnerBaseParallel(int threads) {
        this(threads, new ThreadPool(threads));
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param red parallelism aware reduction engine
     */
    public GroebnerBaseParallel(int threads, Reduction<C> red) {
        this(threads, new ThreadPool(threads), red);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param pl pair selection strategy
     */
    public GroebnerBaseParallel(int threads, PairList<C> pl) {
        this(threads, new ThreadPool(threads), new ReductionPar<C>(), pl);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param pool ThreadPool to use.
     */
    public GroebnerBaseParallel(int threads, ThreadPool pool) {
        this(threads, pool, new ReductionPar<C>());
    }


    /**
     * Constructor.
     * @param pool ThreadPool to use.
     * @param red Reduction engine
     */
    public GroebnerBaseParallel(int threads, ThreadPool pool, Reduction<C> red) {
        this(threads, pool, red, new OrderedPairlist<C>());
    }


    /**
     * Constructor.
     * @param red Reduction engine
     * @param pl pair selection strategy
     */
    public GroebnerBaseParallel(int threads, Reduction<C> red, PairList<C> pl) {
        this(threads, new ThreadPool(threads), red, pl);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param pool ThreadPool to use.
     * @param red parallelism aware reduction engine
     * @param pl pair selection strategy
     */
    public GroebnerBaseParallel(int threads, ThreadPool pool, Reduction<C> red, PairList<C> pl) {
        super(red, pl);
        if (!(red instanceof ReductionPar)) {
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
     * Parallel Groebner base using pairlist class.
     * @param modv number of module variables.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        List<GenPolynomial<C>> G = normalizeZerosOnes(F);
        G = PolyUtil.<C> monic(G);
        if (G.size() <= 1) {
            return G;
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
          int l = F.size();
          ListIterator<GenPolynomial<C>> it = F.listIterator();
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
          //pairlist = new OrderedPairlist<C>( modv, p.ring );
          pairlist = strategy.create(modv, p.ring);
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
        */
        logger.info("start " + pairlist);

        Terminator fin = new Terminator(threads);
        for (int i = 0; i < threads; i++) {
            Reducer<C> R = new Reducer<C>(fin, G, pairlist);
            pool.addJob(R);
        }
        fin.waitDone();
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("interrupt before minimalGB");
        }
        logger.debug("#parallel list = " + G.size());
        G = minimalGB(G);
        // not in this context // pool.terminate();
        logger.info("" + pairlist);
        return G;
    }


    /**
     * Minimal ordered groebner basis, parallel.
     * @param Fp a Groebner base.
     * @return minimalGB(F) a minimal Groebner base of Fp.
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
                F.add(a); // no thread at this point
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
        MiReducer<C>[] mirs = (MiReducer<C>[]) new MiReducer[G.size()];
        int i = 0;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            List<GenPolynomial<C>> R = new ArrayList<GenPolynomial<C>>(G.size() + F.size());
            R.addAll(G);
            R.addAll(F);
            // System.out.println("doing " + a.length());
            mirs[i] = new MiReducer<C>(R, a);
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
 * Reducing worker threads.
 */
class Reducer<C extends RingElem<C>> implements Runnable {


    private final List<GenPolynomial<C>> G;


    private final PairList<C> pairlist;


    private final Terminator fin;


    private final ReductionPar<C> red;


    private static final Logger logger = Logger.getLogger(Reducer.class);


    Reducer(Terminator fin, List<GenPolynomial<C>> G, PairList<C> L) {
        this.fin = fin;
        this.fin.initIdle(1);
        this.G = G;
        pairlist = L;
        red = new ReductionPar<C>();
    }


    /**
     * to string
     */
    @Override
    public String toString() {
        return "Reducer";
    }


    public void run() {
        Pair<C> pair;
        GenPolynomial<C> pi, pj, S, H;
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
                    fin.allIdle();
                    logger.info("shutdown " + fin + " after: " + e);
                    //throw new RuntimeException("interrupt 1 in pairlist.hasNext loop");
                    break;
                }
                if (Thread.currentThread().isInterrupted()) {
                    //fin.initIdle(1);
                    fin.allIdle();
                    logger.info("shutdown after .isInterrupted(): " + fin);
                    //throw new RuntimeException("interrupt 2 in pairlist.hasNext loop");
                    break;
                }
                if (!fin.hasJobs()) {
                    break;
                }
            }
            if (!pairlist.hasNext() && !fin.hasJobs()) {
                break;
            }
            //if ( set ) {
            //fin.notIdle(); set = false;
            //}

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

            H = H.monic();
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
 * Reducing worker threads for minimal GB.
 */
class MiReducer<C extends RingElem<C>> implements Runnable {


    private final List<GenPolynomial<C>> G;


    private GenPolynomial<C> H;


    private final ReductionPar<C> red;


    private final Semaphore done = new Semaphore(0);


    private static final Logger logger = Logger.getLogger(MiReducer.class);


    MiReducer(List<GenPolynomial<C>> G, GenPolynomial<C> p) {
        this.G = G;
        H = p;
        red = new ReductionPar<C>();
    }


    /**
     * to string
     */
    @Override
    public String toString() {
        return "MiReducer";
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
