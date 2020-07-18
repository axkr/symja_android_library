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
import java.util.HashMap;
import java.util.List;

import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.util.Accuracy.hasConverged;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.String.format;

/**
 * Fuzzy c-means quantization code book generation. The implementation is
 * based on the ideas of
 * <p>
 * <pre>
 * J. C. Bezdek (1981):
 * "Pattern Recognition with Fuzzy Objective Function Algorithms",
 *  Plenum Press, New York
 * </pre>
 *
 * @param <V> type of vectors to be clustered
 * @author nwulff
 * @version $Id: FuzzyCMeansQuantization.java,v 1.13 2011/09/12 14:49:20 nwulff Exp $
 * @since 26.04.2009
 */
public abstract class FuzzyCMeansQuantization<V> extends VectorQuantization<V> {
    /**
     * the number of clusters.
     */
    protected int k;
    /**
     * the fuzzyfier value.
     */
    protected double f;
    /**
     * the membership matrix.
     */
    protected double[][] m;

    /**
     * Constructor for a given k number of clusters to calculate.
     *
     * @param k the number of clusters or code vectors.
     */
    public FuzzyCMeansQuantization(final int k) {
        setFuzzyfier(1.8);
        this.k = k;
        for (int i = 0; i < k; i++) {
            codeBook.add(createRndCodeVector());
        }
    }

    /**
     * Multiply Type V by the given scalar.
     *
     * @param s the scalar
     * @param x the element V
     * @return mij*x
     */
    protected abstract V mult(final double s, final V x);

    /**
     * Create a random code vector. Used if a vector is orphaned
     * more than one time in order to converge to the given fixed
     * code book size.
     *
     * @return a random code vector
     */
    protected abstract V createRndCodeVector();

    /**
     * Create the initial code book of size k for the given vector set.
     *
     * @param size of the code book
     * @param set  the set of vectors to fit
     * @return the initial code book
     */
    protected abstract List<V> createInitialCodeBook(final int size,
                                                     final List<V> set);

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.lvq.VectorQuantization#createInitialCodeBook(java.util.Collection)
     */
    @Override
    protected List<V> createInitialCodeBook(final List<V> set) {
        return createInitialCodeBook(k, set);
    }

    /**
     * Internal cross check, that the membership matrix m is calculated
     * correctly.
     *
     * @param n number of vectors to cluster
     */
    protected void membershipCheck(final int n) {
        String msg;
        double sum;
        // 1.st check total membership is 1
        for (int j = 0; j < n; j++) {
            sum = 0;
            for (int i = 0; i < k; i++) {
                sum += m[i][j];
            }
            if (abs(sum - 1) > Accuracy.FEPS) {
                msg = format("membership[%d] not unit: sum=%f", j, sum);
                logger.error(msg);
                throw new ArithmeticException(msg);
            }
        }
        // 2.nd check clusters are not empty
        for (int i = 0; i < k; i++) {
            sum = 0;
            for (int j = 0; j < n; j++) {
                sum += m[i][j];
            }
            if (sum <= 0) {
                msg = format("cluster[%d] is empty: sum=%f", i, sum);
                logger.error(msg);
                throw new ArithmeticException(msg);
            }
        }
    }

    /**
     * Update the code book via the weighted fuzzy membership product.
     *
     * @param x the vector of type V to cluster
     * @return the updated code book
     */
    protected List<V> updateCodeBook(final List<V> x) {
        double sum, mij;
        int n = x.size();
        List<V> cb = new ArrayList<V>(k);
        List<V> vs = new ArrayList<V>();
        codeMap = new HashMap<V, List<V>>();
        V vj, ci = null;
        membershipCheck(n);
        for (int i = 0; i < k; i++) {
            sum = 0;
            ci = null;
            for (int j = 0; j < n; j++) {
                mij = m[i][j];
                if (mij > 0) {
                    vj = x.get(j);
                    vs.add(vj);
                    mij = pow(mij, f);
                    sum += mij;
                    if (j == 0 || ci == null) {
                        ci = mult(mij, vj);
                    } else {
                        ci = add(ci, mult(mij, vj));
                    }
                }
            }
            if (sum > 0) {
                ci = mult(1.0 / sum, ci);
            } else {
                ci = createRndCodeVector();
                logger.info(format("no center found use random %s",
                        asString(ci)));
            }
            for (V cv : cb) {
                if (distance(cv, ci) < Accuracy.FEPS) {
                    logger.info(format("c[%d] not unique %s", i, asString(ci)));
                    ci = createRndCodeVector();
                    logger.info(format("create rnd %s", asString(ci)));
                    break;
                }
            }
            cb.add(ci);
            codeMap.put(ci, vs);
            // logger.info(format("new cv[%d]:%s ", i, asString(ci)));
        }
        return cb;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.lvq.VectorQuantization#findCodeBook(java.util.Collection, java.util.Collection)
     */
    @Override
    public List<V> findCodeBook(final List<V> codeBookGuess, final List<V> set) {
        double cbDistance, sum, dij, dlj, p = 2.0 / (f - 1);
        int ite = 0, n = set.size();
        List<V> newCodeBook = new ArrayList<V>(codeBookGuess);
        do {
            // logger.info("iteration: "+ite);
            codeBook = newCodeBook;
            // calculate new update matrix m
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < n; j++) {
                    sum = 0;
                    dij = distance(set.get(j), codeBook.get(i));
                    for (int l = 0; l < k; l++) {
                        dlj = distance(set.get(j), codeBook.get(l));
                        sum += pow(dij / dlj, p);
                    }
                    m[i][j] = 1.0 / sum;
                }
            }
            // calculate the new code book
            newCodeBook = updateCodeBook(set);
            cbDistance = codeBookDistance(newCodeBook, codeBook);
            // logger.info("distance: "+cbDistance);
            iterationFinished(ite, newCodeBook);
        } while (!hasConverged(cbDistance, 0, eps, ++ite, maxIterations));
        codeBook = newCodeBook;
        optimizationFinished(ite - 1, codeBook);
        return getCodeBook();
    }

    /**
     * Get the value of the fuzzyfier attribute.
     *
     * @return the fuzzyfier
     */
    public double getFuzzyfier() {
        return f;
    }

    /**
     * Set the fuzzyfier value.
     *
     * @param fuzzyfier to set
     */
    public void setFuzzyfier(final double fuzzyfier) {
        if (fuzzyfier <= 1) {
            String msg = format("fuzzyfier<=1: %f", fuzzyfier);
            throw new IllegalArgumentException(msg);
        }
        this.f = fuzzyfier;
    }

    /**
     * Get the vectors associated to the given code vector.
     *
     * @param cv code vector
     * @return list of elements associated to this vector
     */
    @Override
    public List<V> getAssociatedMembers(final V cv) {
        return codeMap.get(cv);
    }

}
 