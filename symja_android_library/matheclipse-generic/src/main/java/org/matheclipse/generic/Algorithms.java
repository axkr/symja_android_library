package org.matheclipse.generic;

import java.util.Collection;
import java.util.List;

import org.matheclipse.generic.interfaces.BiFunction;
import org.matheclipse.generic.interfaces.ISequence;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Functional Programming algorithms for <code>java.util.List</code> and
 * <code>java.util.Collection</code>.
 * 
 * @see org.matheclipse.generic.nested.NestedAlgorithms NestedAlgorithms for
 *      nested lists algorithms
 */
public class Algorithms {

	/**
	 * Return the number of elements in the collection that equals the given
	 * value.
	 */
	public static <T> int count(final Collection<T> collection, final T value) {
		int counter = 0;
		for (final T obj : collection) {

			if (obj.equals(value)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Return the number of elements in the <code>list</code> that match the given
	 * predicate.
	 */
	public static <T> int count(final List<T> list, final int start, final int end, final Predicate<T> fn) {
		int counter = 0;
		for (int i = start; i < end; i++) {

			if (fn.apply(list.get(i))) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Return the number of elements in the <code>list</code> that equals the
	 * given value.
	 */
	public static <T> int count(final List<T> list, final int start, final int end, final T value) {
		int counter = 0;
		for (int i = start; i < end; i++) {

			if (list.get(i).equals(value)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Returns the number of elements in the collection that match the given
	 * predicate.
	 */
	public static <T> int countIf(final Collection<T> collection, final Predicate<T> predicate) {
		int counter = 0;
		for (final T obj : collection) {
			if (predicate.apply(obj)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Returns the number of elements in the collection that doesn't match the
	 * given predicate.
	 */
	public static <T> int countIfNot(final Collection<T> collection, final Predicate<T> predicate) {
		int counter = 0;
		for (final T obj : collection) {
			if (!predicate.apply(obj)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Drop (remove) the list elements according to the
	 * <code>sequenceSpecification</code> for the list indexes.
	 * 
	 * @param <T>
	 * @param list
	 * @param resultList
	 * @param sequenceSpecification
	 */
	public static <T> List<? extends T> drop(final List<? extends T> resultList, final ISequence sequenceSpecification) {
		sequenceSpecification.setListSize(resultList.size());
		int j = sequenceSpecification.getStart();
		for (int i = j; i < sequenceSpecification.getEnd(); i += sequenceSpecification.getStep()) {
			resultList.remove(j);
			j += sequenceSpecification.getStep() - 1;
		}
		return resultList;
	}

	/**
	 * Fold the list from lowest to highest index. If the <i>binaryFunction</i>
	 * returns <code>null</code>, the left element will be added to the result
	 * list, otherwise the result will be <i>folded</i> again with the next
	 * element in the list.
	 * 
	 * @param <T>
	 * @param list
	 * @param binaryFunction
	 * @param resultList
	 */
	public static <T> Collection<? super T> foldLeft(final T expr, final List<T> list,
			final BiFunction<T, T, ? extends T> binaryFunction, final Collection<? super T> resultList) {
		return Algorithms.foldLeft(expr, list, 0, list.size(), binaryFunction, resultList);
	}

	/**
	 * Fold the list from <code>start</code> index including to <code>end</code>
	 * index excluding into the <code>resultCollection</code>. If the
	 * <i>binaryFunction</i> returns <code>null</code>, the left element will be
	 * added to the result list, otherwise the result will be <i>folded</i> again
	 * with the next element in the list.
	 * 
	 * @param <T>
	 * @param list
	 * @param start
	 * @param end
	 * @param binaryFunction
	 * @param resultCollection
	 */
	public static <T> Collection<? super T> foldLeft(final T expr, final List<T> list, final int start, final int end,
			final BiFunction<T, T, ? extends T> binaryFunction, final Collection<? super T> resultCollection) {
		if (start < end) {
			T elem = expr;
			resultCollection.add(elem);
			for (int i = start; i < end; i++) {

				elem = binaryFunction.apply(elem, list.get(i));
				resultCollection.add(elem);
			}

		}
		return resultCollection;
	}

	/**
	 * ForEach applies the unary <code>function</code> to each element in the
	 * <code>iterable</code>.
	 * 
	 * @param <T>
	 * @param iterable
	 * @param function
	 */
	public static <T> void forEach(final Iterable<? extends T> iterable, final Function<T, ? extends T> function) {
		for (T t : iterable) {
			function.apply(t);
		}
	}

	/**
	 * ForEach applies the unary <code>function</code> to each element in the
	 * <code>list</code> in the range <code>[start .. list.size()[</code>.
	 * 
	 * @param <T>
	 * @param list
	 * @param start
	 * @param function
	 */
	public static <T> void forEach(final List<? extends T> list, int start, final Function<T, ? extends T> function) {
		forEach(list, start, list.size(), function);
	}

	/**
	 * ForEach applies the unary <code>function</code> to each element in the
	 * <code>list</code> in the range <code>[start .. end[</code>.
	 * 
	 * @param <T>
	 * @param list
	 * @param start
	 * @param end
	 * @param function
	 */
	public static <T> void forEach(final List<? extends T> list, int start, int end, final Function<T, ? extends T> function) {
		int len = end < list.size() ? end : list.size();
		for (int i = start; i < len; i++) {
			function.apply(list.get(i));
		}
	}

	/**
	 * Return a new expression by folding <code>expr</code> n times
	 * 
	 * @param <T>
	 * @param expr
	 * @param n
	 * @param cloneList
	 */
	// public static <T> T nest(final T expr, final int n, final Function<T, ?
	// extends T> fn) {
	// T temp = expr;
	// for (int i = 0; i < n; i++) {
	// temp = fn.apply(temp);
	// }
	// return temp;
	// }

	/**
	 * Return a resultList by folding <code>expr</code> n times
	 * 
	 * @param <T>
	 * @param expr
	 * @param n
	 * @param cloneList
	 * @param resultList
	 */
	// public static <T> void nestList(final T expr, final int n, final
	// Function<T, ? extends T> fn, final Collection<T> resultList) {
	// T temp = expr;
	// resultList.add(temp);
	// for (int i = 0; i < n; i++) {
	// temp = fn.apply(temp);
	// resultList.add(temp);
	// }
	// }

	/**
	 * Add the list elements to the <code>resultCollection</code> according to the
	 * <code>sequenceSpecification</code> for the list indexes.
	 * 
	 * @param <T>
	 * @param list
	 * @param resultCollection
	 * @param sequenceSpecification
	 */
	public static <T> Collection<? super T> take(final List<? extends T> list, final Collection<? super T> resultCollection,
			final ISequence sequenceSpecification) {
		sequenceSpecification.setListSize(list.size());
		for (int i = sequenceSpecification.getStart(); i < sequenceSpecification.getEnd(); i += sequenceSpecification.getStep()) {
			resultCollection.add(list.get(i));
		}
		return resultCollection;
	}

	/**
	 * Transform applies the unary <code>function</code> to each element in the
	 * <code>iterable</code>. The return value of the <code>function</code> is
	 * appended to the <code>resultCollection</code>.
	 * 
	 * @param <T>
	 * @param iterable
	 * @param function
	 * @param resultCollection
	 * @return
	 */
	public static <T> Collection<T> transform(final Iterable<T> iterable, final Function<T, ? extends T> function,
			final Collection<T> resultCollection) {
		for (T t : iterable) {
			resultCollection.add(function.apply(t));
		}
		return resultCollection;
	}

	/**
	 * Transform applies the unary <code>function</code> to each element in the
	 * <code>list</code> in the range <code>[start .. list.size()[</code>. The
	 * return value of the <code>function</code> is appended to the
	 * <code>resultCollection</code>.
	 * 
	 * @param <T>
	 * @param list
	 * @param start
	 * @param function
	 * @param resultCollection
	 * 
	 * @return the given resultCollection
	 */
	public static <T> Collection<T> transform(final List<T> list, int start, final Function<T, ? extends T> function,
			final Collection<T> resultCollection) {
		return transform(list, start, list.size(), function, resultCollection);
	}

	/**
	 * Transform applies the unary <code>function</code> to each element in the
	 * <code>list</code> in the range <code>[start .. end[</code>. The return
	 * value of the <code>function</code> is appended to the
	 * <code>resultCollection</code>.
	 * 
	 * @param <T>
	 * @param list
	 * @param start
	 * @param end
	 * @param function
	 * @param resultCollection
	 * 
	 * @return the given resultCollection
	 */
	public static <T> Collection<T> transform(final List<T> list, int start, int end, final Function<T, ? extends T> function,
			final Collection<T> resultCollection) {
		int len = end < list.size() ? end : list.size();
		for (int i = start; i < len; i++) {
			resultCollection.add(function.apply(list.get(i)));
		}
		return resultCollection;
	}

}
