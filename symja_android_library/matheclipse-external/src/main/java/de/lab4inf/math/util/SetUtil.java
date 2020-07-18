/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2011,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lab4inf.math.L4MObject;

/**
 * Utility class for mathematical set operations.
 *
 * @author nwulff
 * @version $Id: SetUtil.java,v 1.3 2011/10/12 12:09:44 nwulff Exp $
 * @since 13.09.2011
 */
public final class SetUtil extends L4MObject {
    /**
     * Hidden constructor, no instances of this utility class are allowed.
     */
    private SetUtil() {
    }

    /**
     * Calculate the union of two collection.
     *
     * @param setA first set
     * @param setB second set
     * @param <T>  type of the elements
     * @return A &cup; B
     */
    public static <T> List<T> union(final Collection<T> setA, final Collection<T> setB) {
        List<T> c = new ArrayList<T>(setA);
        c.addAll(setB);
        return c;
    }

    /**
     * Calculate the intersection of two collections.
     *
     * @param setA first set
     * @param setB second set
     * @param <T>  type of the elements
     * @return A &cap; B
     */
    public static <T> List<T> intersection(final Collection<T> setA, final Collection<T> setB) {
        List<T> c = new ArrayList<T>(setA);
        c.retainAll(setB);
        return c;
    }

    /**
     * Calculate the symmetric difference of two collections.
     * I.e. all elements either in A or in B but not in both.
     *
     * @param setA first set
     * @param setB second set
     * @param <T>  type of the elements
     * @return A &Delta; B
     */
    public static <T> List<T> difference(final Collection<T> setA, final Collection<T> setB) {
        List<T> c = union(setA, setB);
        c = complement(intersection(setA, setB), c);
        return c;
    }

    /**
     * Calculate the relative complement B\A of A in B.
     * I.e. all elements of B not in A.
     *
     * @param setA first set
     * @param setB second set
     * @param <T>  type of the elements
     * @return B\A
     */
    public static <T> List<T> complement(final Collection<T> setA, final Collection<T> setB) {
        List<T> c = new ArrayList<T>(setB);
        c.removeAll(setA);
        return c;
    }

    /**
     * Calculate the Tanimoto (or Jaccard)
     * similarity measure of two sets.
     *
     * @param setA first set
     * @param setB second set
     * @param <T>  type of the elements
     * @return similarity of the sets
     */
    public static <T> double similarity(final Collection<T> setA, final Collection<T> setB) {
        Collection<T> c = intersection(setA, setB);
        double coeff = c.size();
        if (coeff > 0) {
            coeff /= setA.size() + setB.size() - coeff;
        }
        return coeff;
    }

    /**
     * Calculate the Gini index for the given items.
     * An index of one means all items are the same.
     *
     * @param items set of objects of type T
     * @param <T>   type of the elements
     * @return gini index
     */
    public static <T> double giniIndex(final Collection<T> items) {
        return 1 - giniImpurity(items);
    }

    /**
     * Calculate the Gini impurity of the given list of objects.
     *
     * @param items set of objects of type T
     * @param <T>   type of the elements
     * @return gini impurity measure
     */
    public static <T> double giniImpurity(final Collection<T> items) {
        double gini = 1;
        double v;
        int t = items.size();
        Map<T, Double> probability = new HashMap<T, Double>(t);
        for (T item : items) {
            v = 0;
            if (probability.containsKey(item)) {
                v = probability.get(item);
            }
            v++;
            probability.put(item, v);
        }
        gini = ((double)t) * ((double)t);
        for (Map.Entry<T, Double> item : probability.entrySet()) {
            v = item.getValue();
            gini -= v * v;
        }
        gini /= ((double)t) * ((double)t);
        return gini;
    }
}
 