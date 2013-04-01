package org.matheclipse.generic.interfaces;

/**
 * Defines an aggregation operation, i.e. how to aggregate the items of a collection.
 *
 * @param <T>
 */
public interface Aggregator<T> {
	/**
	 * Defines how this operation aggregates the objects
	 * 
	 * @param iterable the objects to be aggregated 
	 * @return
	 */
	public T aggregate(Iterable<? extends T> iterable);
}
