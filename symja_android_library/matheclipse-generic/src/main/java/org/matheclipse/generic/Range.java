package org.matheclipse.generic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.matheclipse.generic.interfaces.Aggregator;
import org.matheclipse.generic.interfaces.BiFunction;
import org.matheclipse.generic.interfaces.BiPredicate;
import org.matheclipse.generic.interfaces.IUnaryIndexFunction;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * Create a range for a given <code>List</code> instance
 * 
 * @param <E>
 * @param <L>
 */
public class Range<E, L extends List<E>, C extends Collection<E>> implements Iterable<E> {
	class RangeIterator implements Iterator<E> {
		private int fCurrrent;

		private Range<E, L, C> fRange;

		public RangeIterator(Range<E, L, C> range) {
			fRange = range;
			fCurrrent = fRange.fStart;
		}

		public boolean hasNext() {
			return fCurrrent < fRange.fEnd;
		}

		public E next() {
			return fRange.get(fCurrrent++);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	final/* package private */int fEnd;

	final/* package private */L fList;

	final/* package private */int fStart;

	/**
	 * Construct a range for a List
	 * 
	 * @param list
	 */
	public Range(L list) {
		this(list, 0, list.size());
	}

	/**
	 * Construct a range for a List
	 * 
	 * throws IndexOutOfBoundsException if <code>start</code> isn't valid.
	 * 
	 * @param list
	 * @param start
	 * @param end
	 */
	public Range(L list, int start) {
		this(list, start, list.size());
	}

	/**
	 * Construct a range for a List
	 * 
	 * throws IndexOutOfBoundsException if <code>start</code> or <code>end</code>
	 * aren't valid.
	 * 
	 * @param list
	 * @param start
	 * @param end
	 */
	public Range(L list, int start, int end) {
		fList = list;
		fStart = start;
		fEnd = end;
		if (fStart < 0 || fStart > fList.size()) {
			throw new IndexOutOfBoundsException("Start index not allowed for the given list");
		}
		if (fEnd < 0 || fEnd > fList.size()) {
			throw new IndexOutOfBoundsException("End index not allowed for the given list");
		}
		if (fStart > fEnd) {
			throw new IndexOutOfBoundsException("Start index greater than end index");
		}
	}

	/**
	 * Aggregates the items in the given iterable using the given
	 * {@link Aggregator}.
	 * 
	 * @param aggregator
	 *          The function that defines how the objects in this iterable have to
	 *          be aggregated
	 * @return The result of the aggregation of all the items in the given
	 *         iterable
	 */
	public E aggregate(Aggregator<E> aggregator) {
		return aggregator.aggregate(this);
	}

	/**
	 * Returns true if the predicate returns true for all elements in the range.
	 * 
	 * @return true if the predicate returns true, false otherwise
	 */
	public boolean all(Predicate<E> predicate) {
		for (int i = fStart; i < fEnd; i++) {

			if (!predicate.apply(fList.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if all branch predicates return true for all elements in the
	 * range. Also returns true when there are no branch predicates.
	 * 
	 * @return true if all branch predicates return true, false otherwise
	 */
	public boolean all(Predicate<E>[] predicates) {
		for (int i = fStart; i < fEnd; i++) {

			for (int j = 0; j < predicates.length; j++) {

				if (!predicates[i].apply(fList.get(i))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns true if the predicate returns true for at least one element in the
	 * range.
	 * 
	 * @return true if the predicate returns true for at least one element, false
	 *         otherwise
	 */
	public boolean any(Predicate<E> predicate) {
		for (int i = fStart; i < fEnd; i++) {

			if (predicate.apply(fList.get(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if at least one branch predicate returns true for all elements
	 * in the range.
	 * 
	 * @return true if at least one branch predicate returns true, false otherwise
	 */
	public boolean any(Predicate<E>[] predicates) {
		for (int i = fStart; i < fEnd; i++) {

			for (int j = 0; j < predicates.length; j++) {

				if (predicates[i].apply(fList.get(i))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Compare all adjacent elemants from lowest to highest index and return true,
	 * if the binary predicate gives true in each step. If start &gt;= (end-1) the
	 * method return false;
	 * 
	 */
	public boolean compareAdjacent(BiPredicate<E> predicate) {
		if (fStart >= fEnd - 1) {
			return false;
		}
		E elem = fList.get(fStart);
		for (int i = fStart + 1; i < fEnd; i++) {

			if (!predicate.apply(elem, fList.get(i))) {
				return false;
			}
			elem = fList.get(i);
		}
		return true;
	}

	/**
	 * Create the (unordered) complement set from both ranges
	 * 
	 * @param result
	 * @param secondRange
	 * @return
	 */
	public C complement(final C result, final Range<E, L, C> secondRange) {
		if ((size() == 0) && (secondRange.size() == 0)) {
			return result;
		}
		Set<E> set1 = Sets.newHashSet(this);
		Set<E> set2 = Sets.newHashSet(secondRange);
		Set<E> set3 = Sets.difference(set1, set2);
		for (E e : set3) {
			result.add(e);
		}
		return result;
	}

	public boolean contains(E o) {
		return indexOf(o) >= 0;
	}

	public boolean containsAll(Collection<? extends E> c) {
		Iterator<? extends E> e = c.iterator();
		while (e.hasNext())
			if (!contains(e.next()))
				return false;
		return true;
	}

	/**
	 * Returns the number of elements in the range that equals the given value.
	 */
	public int count(Object value) {
		int counter = 0;
		for (int i = fStart; i < fEnd; i++) {

			if (value.equals(fList.get(i))) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Returns the number of elements in the range that match the given predicate.
	 */
	public int countIf(Predicate<E> predicate) {
		int counter = 0;
		for (int i = fStart; i < fEnd; i++) {

			if (predicate.apply(fList.get(i))) {
				counter++;
			}
		}
		return counter;
	}

	public C difference(C result, final Range<E, L, C> secondList) {
		if ((size() == 0) && (secondList.size() == 0)) {
			return result;
		}
		Set<E> set1 = Sets.newHashSet(this);
		Set<E> set2 = Sets.newHashSet(secondList);
		Set<E> set3 = Sets.difference(set1, set2);
		for (E e : set3) {
			result.add(e);
		}
		return result;
	}

	/**
	 * Apply the predicate to each element in the range and append the elements
	 * which match the predicate to the filterList, or otherwise append it to the
	 * restList.
	 * 
	 * @param filterList
	 *          the elements which match the predicate
	 * @param restList
	 *          the elements which don't match the predicate
	 * @param predicate
	 *          the predicate which filters each element in the range
	 * @return the <code>filterList</code>
	 */
	public C filter(C filterList, C restList, Predicate<E> predicate) {
		for (int i = fStart; i < fEnd; i++) {
			if (predicate.apply(fList.get(i))) {
				filterList.add(fList.get(i));
			} else {
				restList.add(fList.get(i));
			}
		}
		return filterList;
	}

	/**
	 * Apply the function to each element in the range and append the result
	 * elements for which the function returns non-null elements to the
	 * filterList, or otherwise append it to the restList.
	 * 
	 * @param filterList
	 *          the elements which match the predicate
	 * @param restList
	 *          the elements which don't match the predicate
	 * @param function
	 *          the function which filters each element in the range by returning
	 *          a non-null result.
	 * @return the <code>filterList</code>
	 */
	public C filter(C filterList, C restList, final Function<E, E> function) {
		for (int i = fStart; i < fEnd; i++) {
			E expr = function.apply(fList.get(i));
			if (expr != null) {
				filterList.add(expr);
			} else {
				restList.add(fList.get(i));
			}
		}
		return filterList;
	}

	/**
	 * Apply the predicate to each element in the range and append the elements to
	 * the list, which match the predicate.
	 * 
	 * @see Range#removeAll(List, Predicate)
	 * @see Range#replaceAll(List, Function)
	 */
	public C filter(C list, Predicate<E> predicate) {
		for (int i = fStart; i < fEnd; i++) {
			if (predicate.apply(fList.get(i))) {
				list.add(fList.get(i));
			}
		}
		return list;
	}

	/**
	 * Apply the predicate to each element in the range and append the elements to
	 * the list, which match the predicate.
	 * 
	 * @see Range#removeAll(List, Predicate)
	 * @see Range#replaceAll(List, Function)
	 */
	public C filter(C list, Predicate<E> predicate, int maxMatches) {
		int count = 0;
		if (count == maxMatches) {
			return list;
		}
		for (int i = fStart; i < fEnd; i++) {

			if (predicate.apply(fList.get(i))) {
				if (++count == maxMatches) {
					list.add(fList.get(i));
					break;
				}
				list.add(fList.get(i));
			}
		}
		return list;
	}

	/**
	 * Locates the first pair of adjacent elements in a range that are the same
	 * value.
	 * 
	 * @return the index of the first element
	 */
	public int findAdjacent(Object match) {
		return findAdjacent(match, fStart);
	}

	/**
	 * Locates the first pair of adjacent elements in a range that are the same
	 * value starting at index
	 * <code>start</start> and ending at the ranges upper limit.
	 * 
	 * @return the index of the first element
	 */
	public int findAdjacent(Object match, int start) {
		if (match == null) {
			for (int i = fStart; i < fEnd - 1; i++) {

				if (fList.get(i) == null && fList.get(i + 1) == null) {
					return i;
				}
			}
		} else {
			for (int i = fStart; i < fEnd - 1; i++) {

				if (match.equals(fList.get(i)) && match.equals(fList.get(i))) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Locates the first pair of adjacent elements in a range that match the given
	 * predicate
	 * 
	 * @param predicate
	 *          a predicate that analyzes the given elements
	 * @return the index of the first element
	 */
	public int findAdjacent(Predicate<E> predicate) {
		return findAdjacent(predicate, fStart);
	}

	/**
	 * Locates the first pair of adjacent elements in a range that match the given
	 * predicate starting at index
	 * <code>start</start> and ending at the ranges upper limit.
	 * 
	 * @param predicate
	 *          a predicate that analyzes the given elements
	 * @return the index of the first element
	 */
	private int findAdjacent(Predicate<E> predicate, int start) {
		for (int i = start; i < fEnd - 1; i++) {

			if (predicate.apply(fList.get(i)) && predicate.apply(fList.get(i + 1))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Apply the functor to the elements of the range from left to right and
	 * return the final result. Results do accumulate from one invocation to the
	 * next: each time this method is called, the accumulation starts over with
	 * value from the previous function call.
	 * 
	 * @param function
	 *          a binary function that accumulate the elements
	 * @return the accumulated elements
	 */
	public E foldLeft(final BiFunction<E, E, ? extends E> function, E startValue) {
		E value = startValue;
		for (int i = fStart; i < fEnd; i++) {
			value = function.apply(value, fList.get(i));
		}
		return value;
	}

	/**
	 * Apply the functor to the elements of the range from right to left and
	 * return the final result. Results do accumulate from one invocation to the
	 * next: each time this method is called, the accumulation starts over with
	 * value from the previous function call.
	 * 
	 * @param function
	 *          a binary function that accumulate the elements
	 * @return the accumulated elements
	 */
	public E foldRight(final BiFunction<E, E, ? extends E> function, E startValue) {
		E value = startValue;
		for (int i = fEnd - 1; i >= fStart; i--) {
			value = function.apply(value, fList.get(i));
		}
		return value;
	}

	/**
	 * Apply the functor to each element in the range and return the final result.
	 * 
	 * @return the result of the last execution of the functor, or null if the
	 *         functor is not executed.
	 */
	public E forEach(final Function<E, ? extends E> function) {
		E value = null;
		for (int i = fStart; i < fEnd; i++) {

			value = function.apply(fList.get(i));
		}
		return value;
	}

	/**
	 * Delegates to the lists get() method.
	 * 
	 * With this method you can access elements outside the given range
	 * 
	 * @param index
	 * @return
	 */
	final public E get(int index) {
		return fList.get(index);
	}

	final public int getEnd() {
		return fEnd;
	}

	/**
	 * get the list for this rnage
	 * 
	 * @return
	 */
	final public L getList() {
		return fList;
	}

	final public int getStart() {
		return fStart;
	}

	/**
	 * Returns the index of the first found object from the range start
	 * 
	 * @param match
	 * @return
	 */
	public int indexOf(E match) {
		return indexOf(match, fStart);
	}

	/**
	 * Returns the index of the first found object from the given start index
	 * 
	 * @param match
	 * @return
	 */
	public int indexOf(E match, int start) {
		if (match == null) {
			for (int i = start; i < fEnd; i++) {

				if (fList.get(i) == null) {
					return i;
				}
			}
		} else {
			for (int i = start; i < fEnd; i++) {

				if (match.equals(fList.get(i))) {
					return i;
				}
			}
		}
		return -1;
	}

	public int indexOf(Predicate<E> predicate) {
		return indexOf(predicate, fStart);
	}

	/**
	 * Returns the index of the first found object that match the predicate
	 * 
	 */
	public int indexOf(Predicate<E> predicate, int start) {
		for (int i = start; i < fEnd; i++) {

			if (predicate.apply(fList.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Create the (unordered) intersection set from both ranges. Multiple equal
	 * values in both ranges.
	 * 
	 * @param result
	 * @param secondRange
	 * @return
	 */
	public C intersection(C result, final Range<E, L, C> secondList) {
		if ((size() == 0) && (secondList.size() == 0)) {
			return result;
		}
		Set<E> set1 = Sets.newHashSet(this);
		Set<E> set2 = Sets.newHashSet(secondList);
		Set<E> set3 = Sets.intersection(set1, set2);
		for (E e : set3) {
			result.add(e);
		}
		// final HashMap<E, MutuableInteger> rangeElementMap = new HashMap<E,
		// MutuableInteger>();
		// E elem;
		// MutuableInteger counter;
		// for (int i = fStart; i < fEnd; i++) {
		// elem = fList.get(i);
		// counter = rangeElementMap.get(elem);
		// if (counter == null) {
		// rangeElementMap.put(elem, new MutuableInteger(1));
		// } else {
		// counter.inc();
		// }
		// }
		//
		// for (int i = secondList.fStart; i < secondList.fEnd; i++) {
		// elem = secondList.get(i);
		// counter = rangeElementMap.get(elem);
		// if (counter != null) {
		// result.add(elem);
		// counter.dec();
		// if (counter.intValue() == 0) {
		// rangeElementMap.remove(elem);
		// }
		// }
		// }
		return result;
	}

	public Iterator<E> iterator() {
		return new RangeIterator(this);
	}

	/**
	 * Returns the index of the first found object from the range end
	 * 
	 * @param match
	 * @return
	 */
	public int lastIndexOf(Object match) {
		if (match == null) {
			for (int i = fEnd - 1; i >= fStart; i--) {

				if (fList.get(i) == null) {
					return i;
				}
			}
		} else {
			for (int i = fEnd - 1; i >= fStart; i--) {

				if (match.equals(fList.get(i))) {
					return i;
				}
			}
		}
		return -1;
	}

	public int lastIndexOf(Predicate<E> predicate) {
		for (int i = fEnd - 1; i >= fStart; i--) {

			if (predicate.apply(fList.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Append the result of the pairwise mapped elements to the given
	 * <code>list</code>. If the function evaluates to <code>null</code> append
	 * the current range element directly otherwise evaluate the next pair of
	 * elements
	 * 
	 * @param list
	 * @param fromIndex
	 * @param toIndex
	 */
	public boolean map(C list, BiFunction<E, E, E> function) {
		if (fStart >= fEnd) {
			return false;
		}
		boolean evaled = false;
		E element = fList.get(fStart);
		E result;
		for (int i = fStart + 1; i < fEnd; i++) {

			result = function.apply(element, fList.get(i));
			if (result == null) {
				list.add(element);
				element = fList.get(i);
			} else {
				evaled = true;
				element = result;
			}
		}
		list.add(element);
		return evaled;
	}

	/**
	 * Append the mapped ranges elements directly to the given <code>list</code>
	 * 
	 * @param list
	 * @param function
	 */
	public C map(C list, Function<E, E> function) {
		for (int i = fStart; i < fEnd; i++) {
			list.add(function.apply(fList.get(i)));
		}
		return list;
	}

	/**
	 * Append the mapped ranges elements directly to the given <code>list</code>
	 * 
	 * @param list
	 * @param function
	 */
	public C map(C list, IUnaryIndexFunction<E, E> function) {
		for (int i = fStart; i < fEnd; i++) {
			list.add(function.apply(i, fList.get(i)));
		}
		return list;
	}

	/**
	 * Append the mapped ranges elements directly to the given <code>list</code>
	 * 
	 * @param list
	 * @param binaryFunction
	 *          binary function
	 * @param leftArg
	 *          left argument of the binary functions <code>apply()</code> method.
	 */
	public C mapLeft(C list, BiFunction<E, E, E> binaryFunction, E leftArg) {
		for (int i = fStart; i < fEnd; i++) {

			list.add(binaryFunction.apply(leftArg, fList.get(i)));
		}
		return list;
	}

	/**
	 * Append the mapped ranges elements directly to the given <code>list</code>
	 * 
	 * @param list
	 * @param binaryFunction
	 *          a binary function
	 * @param leftArg
	 *          right argument of the binary functions <code>apply()</code>
	 *          method.
	 */
	public C mapRight(C list, BiFunction<E, E, E> binaryFunction, E rightArg) {
		for (int i = fStart; i < fEnd; i++) {

			list.add(binaryFunction.apply(fList.get(i), rightArg));
		}
		return list;
	}

	/**
	 * Returns the largest value in the range
	 * 
	 */
	public E max(Comparator<? super E> comp) {
		E value = fList.get(fStart);
		for (int i = fStart + 1; i < fEnd; i++) {

			if (comp.compare(fList.get(i), value) > 0) {
				value = fList.get(i);
			}
		}
		return value;
	}

	/**
	 * Return the smallest value in the range
	 * 
	 */
	public E min(Comparator<? super E> comp) {
		E value = fList.get(fStart);
		for (int i = fStart + 1; i < fEnd; i++) {

			if (comp.compare(fList.get(i), value) < 0) {
				value = fList.get(i);
			}
		}
		return value;
	}

	/**
	 * Apply the predicate to each element in the range and append the elements to
	 * the list, which don't match the predicate.
	 * 
	 * @see Range#filter(List, Predicate)
	 * @see Range#replaceAll(List, Function)
	 */
	public C removeAll(C list, Predicate<E> predicate) {
		for (int i = fStart; i < fEnd; i++) {

			if (!predicate.apply(fList.get(i))) {
				list.add(fList.get(i));
			}
		}
		return list;
	}

	/**
	 * Apply the function to each element in the range and append the results to
	 * the list.
	 * 
	 * @see Range#filter(List, Predicate)
	 * @see Range#removeAll(List, Predicate)
	 */
	public C replaceAll(C list, final Function<E, ? extends E> function) {
		for (int i = fStart; i < fEnd; i++) {
			list.add(function.apply(fList.get(i)));
		}
		return list;
	}

	/**
	 * Append the ranges elements in reversed order to the given <code>list</code>
	 * 
	 * @param list
	 * @param fromIndex
	 * @param toIndex
	 */
	public C reverse(C list) {
		for (int i = fEnd - 1; i >= fStart; i--) {
			list.add(fList.get(i));
		}
		return list;
	}

	/**
	 * Rotate the ranges elements to the left by n places and append the resulting
	 * elements to the <code>list</code>
	 * 
	 * @param list
	 * @param n
	 */
	public C rotateLeft(C list, final int n) {
		for (int i = fStart + n; i < fEnd; i++) {
			list.add(fList.get(i));
		}
		if (n <= size()) {
			for (int i = fStart; i < fStart + n; i++) {
				list.add(fList.get(i));
			}
		}
		return list;
	}

	/**
	 * Rotate the ranges elements to the right by n places and append the
	 * resulting elements to the <code>list</code>
	 * 
	 * @param list
	 * @param n
	 */
	public C rotateRight(C list, final int n) {
		if (n <= size()) {
			for (int i = fEnd - n; i < fEnd; i++) {
				list.add(fList.get(i));
			}
			for (int i = fStart; i < fEnd - n; i++) {
				list.add(fList.get(i));
			}
		}
		return list;
	}

	/**
	 * The size of this range gives the number of elements, which this range
	 * include
	 * 
	 * @return
	 */
	public int size() {
		return fEnd - fStart;
	}

	/**
	 * Sorts the elements of the specified range "in place" (i.e. modify the
	 * internal referenced list), according to the order induced by the specified
	 * comparator.
	 */
	@SuppressWarnings("unchecked")
	public L sort(Comparator<E> comparator) {
		final E[] a = (E[]) fList.toArray();
		Arrays.sort(a, fStart, fEnd, comparator);
		for (int j = fStart; j < fEnd; j++) {
			fList.set(j, a[j]);
		}
		return fList;
	}

	public E[] toArray(E[] array) {
		int j = fStart;
		for (int i = 0; i < array.length; i++) {
			array[i] = fList.get(j++);
			if (j >= array.length) {
				break;
			}
		}
		return array;
	}

	/**
	 * Append the ranges elements to the given <code>list</code>
	 * 
	 * @param list
	 */
	public List<E> toList(List<E> list) {
		for (int i = fStart; i < fEnd; i++) {
			list.add(fList.get(i));
		}
		return list;
	}

	/**
	 * Create the (unordered) union set from both ranges. Multiple equal values in
	 * the given ranges are reduced to one value in the result.
	 * 
	 * @param result
	 * @param secondRange
	 * @return
	 */
	public C union(final C result, final Range<E, L, C> secondList) {
		if ((size() == 0) && (secondList.size() == 0)) {
			return result;
		}
		Set<E> set1 = Sets.newHashSet(this);
		Set<E> set2 = Sets.newHashSet(secondList);
		Set<E> set3 = Sets.union(set1, set2);
		for (E e : set3) {
			result.add(e);
		}
		return result;
	}

}
