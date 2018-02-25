/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package de.lab4inf.math.lvq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.lab4inf.math.util.Accuracy.hasConverged;

//import static java.lang.String.format;

/**
 * Standard k-means vector quantization algorithm.
 *
 * @param <V> type of vectors to be clustered
 * @author nwulff
 * @version $Id: KMeansQuantization.java,v 1.17 2015/03/06 11:05:32 nwulff Exp $
 * @since 26.04.2009
 */
public abstract class KMeansQuantization<V> extends VectorQuantization<V> {
    protected int k;
    protected ArrayList<V> orphanedList;

    /**
     * Constructor for a given k number of clusters to calculate.
     *
     * @param k the number of clusters or code vectors.
     */
    public KMeansQuantization(final int k) {
        this.k = k;
        orphanedList = new ArrayList<V>(k);
        ArrayList<V> dummy = new ArrayList<V>();
        for (int i = 0; i < k; i++) {
            dummy.add(createRndCodeVector());
        }
        codeBook = createInitialCodeBook(dummy);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.lvq.VectorQuantization#createInitialCodeBook(java.util.Collection)
     */
    @Override
    protected List<V> createInitialCodeBook(final List<V> set) {
        return createInitialCodeBook(k, set);
    }

    /**
     * Calculate the center of the given set.
     *
     * @param set of vectors
     * @return center of the set
     */
    protected abstract V calculateCenter(final Collection<V> set);

    /**
     * Create the initial code book of size k for the given vector set.
     *
     * @param size of the code book
     * @param set  the set of vectors to fit
     * @return the initial code book
     */
    protected abstract List<V> createInitialCodeBook(final int size, final List<V> set);

    /**
     * Create a random code vector. Used if a vector is orphaned
     * more than one time in order to converge to the given fixed
     * code book size.
     *
     * @return a random code vector
     */
    protected abstract V createRndCodeVector();

    /**
     * Find the nearest code book vector to v.
     *
     * @param v vector to use
     * @return nearest code book vector
     */
    public V bestCodeVector(final V v) {
        V nearest = null;
        double d, dist = Double.MAX_VALUE;
        for (V cb : codeBook) {
            d = distance(cb, v);
            if (d < dist) {
                nearest = cb;
                dist = d;
            }
        }
        return nearest;
    }

    /**
     * Find an optimal code book for the given vector set, with the given
     * initial guess set. In case that a given code book vector becomes
     * orphaned it will be replaced after the second iteration by a random
     * vector in order to keep the code book size constant.
     *
     * @param codeBookGuess the initial code book guess
     * @param set           to be described by the code book
     * @return optimal code book calculated as set
     */
    @Override
    public List<V> findCodeBook(final List<V> codeBookGuess, final List<V> set) {
        int ite = 0, nObjects = set.size();
        double cbDistance;
        List<V> newCodeBook = new ArrayList<V>(codeBookGuess);
        do {
            // logger.info(format("begin iteration:%d ",ite));
            codeBook = newCodeBook;
            codeMap = calculateCodeMap(set);
            // calculate a new optimized code book via center of gravity
            newCodeBook = new ArrayList<V>();
            for (int i = 0; i < k; i++) {
                V cv = codeBook.get(i);
                Collection<V> sphere = codeMap.get(cv);
                if (null != sphere && sphere.size() > 0) {
                    V center = calculateCenter(sphere);
                    newCodeBook.add(center);
                    orphanedList.remove(cv);
                    // logger.info(format("new cv[%d]:%s ",i,asString(center)));
                } else {
                    // logger.info(format("orphaned cv[%d]:%s ",i,
                    // asString(cv)));
                    if (orphanedList.contains(cv)) {
                        orphanedList.remove(cv);
                        cv = createRndCodeVector();
                        // logger.info(format("created rnd cv[%d]:%s ",i,
                        // asString(cv)));
                    }
                    orphanedList.add(cv);
                    newCodeBook.add(cv);
                }
            }
            cbDistance = codeBookDistance(newCodeBook, codeBook);
            // logger.info(format("distance: %.3f orphaned:%b",
            // cbDistance, orphanedList.size()>0));
            iterationFinished(ite, newCodeBook);
            if (orphanedList.size() > 0) {
                // we can't have converged yet, so force divergence
                cbDistance = 2 * eps;
            }
        } while (!hasConverged(cbDistance, 0, eps, ++ite, maxIterations));
        codeBook = newCodeBook;
        codeMap = calculateCodeMap(set);
        int checkSum = 0;
        for (V center : codeBook) {
            List<V> elements = codeMap.get(center);
            checkSum += elements.size();
        }
        if (nObjects != checkSum) {
            String msg = "checksum failed #" + (nObjects - checkSum);
            logger.error(msg);
            // throw new IllegalStateException(msg);
        }
        optimizationFinished(ite - 1, codeBook);
        return getCodeBook();
    }

    /**
     * Calculate for the given set of vectors and the internal actual code
     * book the sphere of nearest code vectors via an associative code map.
     *
     * @param set of vectors
     * @return codeMap for the given code book
     */
    protected Map<V, List<V>> calculateCodeMap(final Collection<V> set) {
        HashMap<V, List<V>> map = new HashMap<V, List<V>>();
        for (V cb : codeBook) {
            map.put(cb, new ArrayList<V>());
        }
        // associate the vectors with their code book vectors
        for (V v : set) {
            V cb = bestCodeVector(v);
            if (!similar(cb, v)) {
                map.get(cb).add(v);
            }
        }
        return map;
    }

    /**
     * Equals(~) relation for generic objects of type V.
     *
     * @param x first vector
     * @param y second vector
     * @return x ~ y
     */
    protected boolean similar(final V x, final V y) {
        return x.equals(y);
    }
}
 