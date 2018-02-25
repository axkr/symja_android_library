/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.lab4inf.math.L4MObject;
import de.lab4inf.math.util.Accuracy;

import static java.lang.String.format;

/**
 * Generic base class for an adaptive learning vector quantization algorithm
 * for vectors of arbitrary type V. Derived classes have to specify the
 * vector distance, addition and centroid calculation methods for type V.
 *
 * @param <V> type of vectors to be clustered
 * @author nwulff
 * @version $Id: VectorQuantization.java,v 1.13 2010/02/25 15:31:55 nwulff Exp $
 * @since 25.04.2009
 */
public abstract class VectorQuantization<V> extends L4MObject {
    protected static final double EPS = Accuracy.FEPS;
    protected static final int MAXITERATIONS = 50;
    /**
     * reference to the WRONG_CODE_BOOKS_SIZE message.
     */
    private static final String WRONG_CODE_BOOKS_SIZE = "wrong code books size %d != %d";
    /**
     * the code book.
     */
    protected List<V> codeBook;
    /**
     * the associated code map.
     */
    protected Map<V, List<V>> codeMap;
    /**
     * precision to reach.
     */
    protected double eps;
    /**
     * maximal number of iterations.
     */
    protected int maxIterations;
    /**
     * listeners to register.
     */
    protected Set<VQListener<V>> listeners;

    /**
     * Default constructor.
     */
    public VectorQuantization() {
        init();
    }

    /**
     * Initialize the vector quantinizer.
     */
    protected void init() {
        eps = EPS;
        maxIterations = MAXITERATIONS;
        codeBook = new ArrayList<V>();
        listeners = new HashSet<VQListener<V>>();
    }

    /**
     * Register the given listener by adding to the information list.
     *
     * @param listener to register
     */
    public void addListener(final VQListener<V> listener) {
        listeners.add(listener);
    }

    /**
     * Unregister the given listener.
     *
     * @param listener to remove form the information list
     */
    public void removeListener(final VQListener<V> listener) {
        listeners.remove(listener);
    }

    /**
     * Inform all registered listeners that an iteration has finished.
     *
     * @param count    the actual iteration counter
     * @param codebook the actual state of the code book
     */
    protected void iterationFinished(final int count, final List<V> codebook) {
        for (VQListener<V> listener : listeners) {
            listener.iterationFinished(count, codebook);
        }
    }

    /**
     * Inform all registered listeners that an iteration has finished.
     *
     * @param count    the final number of iterations used
     * @param codebook the final state of the code book
     */
    protected void optimizationFinished(final int count, final List<V> codebook) {
        for (VQListener<V> listener : listeners) {
            listener.optimizationFinished(count, codebook);
        }
    }

    /**
     * Calculate a initial code book for the given vectors.
     *
     * @param vectorspace the vectors to describe with the code book
     * @return initial guess for the code book, maybe random
     */
    protected abstract List<V> createInitialCodeBook(final List<V> vectorspace);

    /**
     * Calculate the (metric) distance between two vectors.
     *
     * @param left  side vector
     * @param right side vector
     * @return ||left-right|| (metric) distance
     */
    public abstract double distance(final V left, final V right);

    /**
     * String representation of vector v.
     *
     * @param v vector to show
     * @return vector as string
     */
    protected String asString(final V v) {
        return v.toString();
    }

    /**
     * Add the given vectors.
     *
     * @param left  side vector
     * @param right side vector
     * @return left+right vector addition
     */
    public abstract V add(final V left, final V right);

    /**
     * Get the precision used within the iterations.
     *
     * @return the precision
     */
    public double getEps() {
        return eps;
    }

    /**
     * Set the precision to reach.
     *
     * @param eps the precision
     */
    public void setEps(final double eps) {
        this.eps = eps;
    }

    /**
     * Get the maximal number of iterations.
     *
     * @return the maximal iteration number
     */
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Set the maximal number of iterations for the code book
     * calculations.
     *
     * @param maxIterations the maximal number of iterations
     */
    public void setMaxIterations(final int maxIterations) {
        this.maxIterations = maxIterations;
    }

    /**
     * Return the code book, after calculated by the
     * findCodebook method.
     *
     * @return the calculated code book
     */
    public List<V> getCodeBook() {
        return codeBook;
    }

    /**
     * Find an optimal code book for the given vector space.
     *
     * @param set to be described by the code book
     * @return optimal code book calculated as set
     */
    public List<V> findCodeBook(final List<V> set) {
        return findCodeBook(createInitialCodeBook(set), set);
    }

    /**
     * Find a optimal code book for the given vector set, with the given
     * initial guess set.
     *
     * @param codeBookGuess the initial code book guess
     * @param set           to be described by the code book
     * @return optimal code book calculated as set
     */
    public abstract List<V> findCodeBook(final List<V> codeBookGuess,
                                         final List<V> set);

    /**
     * Calculate the distance between the two given code book vectors.
     * They are assumed to be in order, e.g. cbx(i) is the update of
     * cby(i) for all i=0...k-1.
     *
     * @param cbx first code book
     * @param cby second code book
     * @return distance between the code book vectors
     */
    protected double codeBookDistance(final List<V> cbx, final List<V> cby) {
        V cvx, cvy;
        int k = cbx.size();
        double dist = 0;
        if (cbx.size() != cby.size()) {
            String msg = format(WRONG_CODE_BOOKS_SIZE, cbx.size(), cby.size());
            throw new IllegalArgumentException(msg);
        }
        for (int i = 0; i < k; i++) {
            cvx = cbx.get(i);
            cvy = cby.get(i);
            dist += distance(cvx, cvy);
        }
        dist /= k;
        return dist;
    }

    /**
     * Get the vectors associated to the given code vector.
     *
     * @param cv code vector
     * @return list of elements associated to this vector
     */
    public List<V> getAssociatedMembers(final V cv) {
        return codeMap.get(cv);
    }
}
 