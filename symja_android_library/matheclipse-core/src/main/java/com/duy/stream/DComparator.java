package com.duy.stream;

import com.duy.lambda.Function;
import com.duy.lambda.ToLongFunction;

import java.util.Comparator;

/**
 * Created by Duy on 10/3/2017.
 */

public class DComparator {
    /**
     * Accepts a function that extracts a {@code long} sort key from a type
     * {@code T}, and returns a {@code Comparator<T>} that compares by that
     * sort key.
     * <p>
     * <p>The returned comparator is serializable if the specified function is
     * also serializable.
     *
     * @param <T>          the type of element to be compared
     * @param keyExtractor the function used to extract the long sort key
     * @return a comparator that compares by an extracted key
     * @throws NullPointerException if the argument is null
     * @see #comparing(Function)
     * @since 1.8
     */
    public static <T> Comparator<T> comparingLong(ToLongFunction<? super T> keyExtractor) {
        return new Comparator<T>() {
            @Override
            public int compare(T c1, T c2) {
                return DLong.compare(keyExtractor.applyAsLong(c1), keyExtractor.applyAsLong(c2));
            }
        };
    }

}
