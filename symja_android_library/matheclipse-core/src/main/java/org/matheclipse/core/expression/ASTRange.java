package org.matheclipse.core.expression;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IUnaryIndexFunction;

/**
 * Create a range for a given <code>List</code> instance, with the exception of
 * the <code>sort()</code> method which may sort the internal elements range.
 * 
 */
public class ASTRange extends AbstractList<IExpr> implements Iterable<IExpr> {

	static class ASTRangeIterator implements Iterator<IExpr> {
		private int fCurrrent;

		final private ASTRange fRange;

		public ASTRangeIterator(ASTRange range) {
			fRange = range;
			fCurrrent = fRange.fStart;
		}

		@Override
		public boolean hasNext() {
			return fCurrrent < fRange.fEnd;
		}

		@Override
		public IExpr next() {
			if (fCurrrent == fRange.fEnd) {
				throw new NoSuchElementException();
			}
			return fRange.get(fCurrrent++ - fRange.fStart);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	final/* package private */IAST fList;

	final/* package private */int fStart;

	/* package private */int fEnd;

	/**
	 * Construct a range for a List
	 * 
	 * @param list
	 */
	public ASTRange(IAST list) {
		this(list, 0, list.size());
	}

	/**
	 * Construct a range for a List
	 * 
	 * throws IndexOutOfBoundsException if <code>start</code> isn't valid.
	 * 
	 * @param list
	 * @param start
	 */
	public ASTRange(IAST list, int start) {
		this(list, start, list.size());
	}

	/**
	 * Construct a range for a List
	 * 
	 * throws IndexOutOfBoundsException if <code>start</code> or
	 * <code>end</code> aren't valid.
	 * 
	 * @param list
	 * @param start
	 * @param end
	 */
	public ASTRange(IAST list, int start, int end) {
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

	public boolean add(IExpr element) {
		fList.append(fEnd++, element);
		return true;
	}

	public void add(int location, IExpr element) {
		if (location < fStart && location > fEnd) {
			throw new IndexOutOfBoundsException(
					"Index: " + Integer.valueOf(location) + ", Size: " + Integer.valueOf(fEnd));
		}
		fList.append(location, element);
		fEnd++;
	}

	/**
	 * Returns true if the predicate returns true for all elements in the range.
	 * 
	 * @param predicate
	 * @return true if the predicate returns true, false otherwise
	 */
	public boolean all(Predicate<IExpr> predicate) {
		for (int i = fStart; i < fEnd; i++) {

			if (!predicate.test(fList.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if all branch predicates return true for all elements in the
	 * range. Also returns true when there are no branch predicates.
	 * 
	 * @param predicates
	 * @return true if all branch predicates return true, false otherwise
	 */
	public boolean all(Predicate<IExpr>[] predicates) {
		for (int i = fStart; i < fEnd; i++) {

			for (int j = 0; j < predicates.length; j++) {

				if (!predicates[i].test(fList.get(i))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns true if the predicate returns true for at least one element in
	 * the range.
	 * 
	 * @param predicate
	 * @return true if the predicate returns true for at least one element,
	 *         false otherwise
	 */
	public boolean any(Predicate<IExpr> predicate) {
		for (int i = fStart; i < fEnd; i++) {

			if (predicate.test(fList.get(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if at least one branch predicate returns true for all
	 * elements in the range.
	 * 
	 * @param predicates
	 * @return true if at least one branch predicate returns true, false
	 *         otherwise
	 */
	public boolean any(Predicate<IExpr>[] predicates) {
		for (int i = fStart; i < fEnd; i++) {

			for (int j = 0; j < predicates.length; j++) {

				if (predicates[i].test(fList.get(i))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Compare all adjacent elements from lowest to highest index and return
	 * true, if the binary predicate gives true in each step. If start &gt;=
	 * (end-1) the method return false;
	 * 
	 * @param predicate
	 *            the binary predicate
	 * @return
	 */
	public boolean compareAdjacent(BiPredicate<IExpr, IExpr> predicate) {
		if (fStart >= fEnd - 1) {
			return false;
		}
		IExpr elem = fList.get(fStart);
		for (int i = fStart + 1; i < fEnd; i++) {

			if (!predicate.test(elem, fList.get(i))) {
				return false;
			}
			elem = fList.get(i);
		}
		return true;
	}

	public boolean contains(IExpr o) {
		return indexOf(o) >= 0;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		Iterator<?> iter = c.iterator();
		while (iter.hasNext())
			if (!contains(iter.next()))
				return false;
		return true;
	}

	/**
	 * Returns the number of elements in the range that equals the given value.
	 * 
	 * @param value
	 * @return the number of elements in the range that equals the given value.
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
	 * Returns the number of elements in the range that match the given
	 * predicate.
	 * 
	 * @param predicate
	 * @return the number of elements in the range that match the given
	 *         predicate.
	 */
	public int countIf(Predicate<IExpr> predicate) {
		int counter = 0;
		for (int i = fStart; i < fEnd; i++) {

			if (predicate.test(fList.get(i))) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Apply the function to each element in the range and append the result
	 * elements for which the function returns non-null elements to the
	 * filterList, or otherwise append it to the restList.
	 * 
	 * @param filterList
	 *            the elements which match the predicate
	 * @param restList
	 *            the elements which don't match the predicate
	 * @param function
	 *            the function which filters each element in the range by
	 *            returning a non-null result.
	 * @return the <code>filterList</code>
	 */
	public Collection<IExpr> filter(Collection<IExpr> filterList, Collection<IExpr> restList,
			final Function<IExpr, IExpr> function) {
		for (int i = fStart; i < fEnd; i++) {
			IExpr expr = function.apply(fList.get(i));
			if (expr != null) {
				filterList.add(expr);
			} else {
				restList.add(fList.get(i));
			}
		}
		return filterList;
	}

	/**
	 * Apply the predicate to each element in the range and append the elements
	 * which match the predicate to the filterList, or otherwise append it to
	 * the restList.
	 * 
	 * @param filterList
	 *            the elements which match the predicate
	 * @param restList
	 *            the elements which don't match the predicate
	 * @param predicate
	 *            the predicate which filters each element in the range
	 * @return the <code>filterList</code>
	 */
	public IAST filter(IAST filterList, Collection<IExpr> restList, Predicate<IExpr> predicate) {
		for (int i = fStart; i < fEnd; i++) {
			if (predicate.test(fList.get(i))) {
				filterList.append(fList.get(i));
			} else {
				restList.add(fList.get(i));
			}
		}
		return filterList;
	}

	/**
	 * Apply the predicate to each element in the range and append the elements
	 * which match the predicate to the <code>astResult</code>.
	 * 
	 * @param astResult
	 * @param predicate
	 * @return
	 */
	public IAST filter(IAST astResult, Predicate<IExpr> predicate) {
		for (int i = fStart; i < fEnd; i++) {
			if (predicate.test(fList.get(i))) {
				astResult.append(fList.get(i));
			}
		}
		return astResult;
	}

	/**
	 * Apply the predicate to each element in the range and append the elements
	 * to the list, which match the predicate.
	 * 
	 * @param astResult
	 * @param predicate
	 * @param maxMatches
	 * @return
	 */
	public IAST filter(IAST astResult, Predicate<IExpr> predicate, int maxMatches) {
		int count = 0;
		if (count == maxMatches) {
			return astResult;
		}
		for (int i = fStart; i < fEnd; i++) {

			if (predicate.test(fList.get(i))) {
				if (++count == maxMatches) {
					astResult.append(fList.get(i));
					break;
				}
				astResult.append(fList.get(i));
			}
		}
		return astResult;
	}

	/**
	 * Locates the first pair of adjacent elements in a range that match the
	 * given predicate
	 * 
	 * @param predicate
	 *            a predicate that analyzes the given elements
	 * @return the index of the first element
	 */
	public int findAdjacent(Predicate<IExpr> predicate) {
		return findAdjacent(predicate, fStart);
	}

	/**
	 * Locates the first pair of adjacent elements in a range that match the
	 * given predicate starting at index <code>start</start> and ending at the
	 * ranges upper limit.
	 * 
	 * @param predicate
	 *            a predicate that analyzes the given elements
	 * @return the index of the first element
	 */
	private int findAdjacent(Predicate<IExpr> predicate, int start) {
		for (int i = start; i < fEnd - 1; i++) {

			if (predicate.test(fList.get(i)) && predicate.test(fList.get(i + 1))) {
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
	 *            a binary function that accumulate the elements
	 * @param startValue
	 * @return the accumulated elements
	 */
	public IExpr foldLeft(final BiFunction<IExpr, IExpr, ? extends IExpr> function, IExpr startValue) {
		IExpr value = startValue;
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
	 *            a binary function that accumulate the elements
	 * @param startValue
	 * @return the accumulated elements
	 */
	public IExpr foldRight(final BiFunction<IExpr, IExpr, ? extends IExpr> function, IExpr startValue) {
		IExpr value = startValue;
		for (int i = fEnd - 1; i >= fStart; i--) {
			value = function.apply(value, fList.get(i));
		}
		return value;
	}

	/**
	 * Apply the functor to each element in the range and return the final
	 * result.
	 * 
	 * @param function
	 * @return the result of the last execution of the functor, or null if the
	 *         functor is not executed.
	 */
	public IExpr forEach(final Function<IExpr, ? extends IExpr> function) {
		IExpr value = null;
		for (int i = fStart; i < fEnd; i++) {

			value = function.apply(fList.get(i));
		}
		return value;
	}

	/**
	 * Delegates to the lists <code>get(fStart+index)</code> method.
	 * 
	 * @param index
	 * @return
	 */
	@Override
	final public IExpr get(int index) {
		return fList.get(fStart + index);
	}

	final public int getEnd() {
		return fEnd;
	}

	/**
	 * get the list for this rnage
	 * 
	 * @return
	 */
	final public IAST getList() {
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
	public int indexOf(IExpr match) {
		return indexOf(match, fStart);
	}

	/**
	 * Returns the index of the first found object from the given start index
	 * 
	 * @param match
	 * @param start
	 * @return the index of the first found object from the given start index
	 */
	public int indexOf(IExpr match, int start) {
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

	public int indexOf(Predicate<IExpr> predicate) {
		return indexOf(predicate, fStart);
	}

	/**
	 * Returns the index of the first found expression that match the predicate
	 * 
	 * @param predicate
	 * @param start
	 * @return the index of the first found expression that match the predicate
	 */
	public int indexOf(Predicate<IExpr> predicate, int start) {
		for (int i = start; i < fEnd; i++) {
			if (predicate.test(fList.get(i))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Iterator<IExpr> iterator() {
		return new ASTRangeIterator(this);
	}

	/**
	 * Returns the index of the first found object from the range end
	 * 
	 * @param match
	 * @return
	 */
	@Override
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

	public int lastIndexOf(Predicate<IExpr> predicate) {
		for (int i = fEnd - 1; i >= fStart; i--) {

			if (predicate.test(fList.get(i))) {
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
	 * @param function
	 *            a binary function
	 * @return
	 */
	public boolean map(Collection<IExpr> list, BiFunction<IExpr, IExpr, IExpr> function) {
		if (fStart >= fEnd) {
			return false;
		}
		boolean evaled = false;
		IExpr element = fList.get(fStart);
		IExpr result;
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
	 * @param astResult
	 * @param function
	 * @return
	 */
	public IAST map(IAST astResult, IUnaryIndexFunction<IExpr, IExpr> function) {
		for (int i = fStart; i < fEnd; i++) {
			astResult.append(function.apply(i, fList.get(i)));
		}
		return astResult;
	}

	/**
	 * Append the mapped ranges elements directly to the given <code>list</code>
	 * 
	 * @param list
	 * @param binaryFunction
	 *            binary function
	 * @param leftArg
	 *            left argument of the binary functions <code>apply()</code>
	 *            method.
	 * @return
	 */
	public IAST mapLeft(IAST list, BiFunction<IExpr, IExpr, IExpr> binaryFunction, IExpr leftArg) {
		for (int i = fStart; i < fEnd; i++) {

			list.append(binaryFunction.apply(leftArg, fList.get(i)));
		}
		return list;
	}

	/**
	 * Append the mapped ranges elements directly to the given <code>list</code>
	 * 
	 * @param list
	 * @param binaryFunction
	 *            a binary function
	 * @param rightArg
	 *            right argument of the binary functions <code>apply()</code>
	 *            method.
	 * @return the given list
	 */
	public Collection<IExpr> mapRight(Collection<IExpr> list, BiFunction<IExpr, IExpr, IExpr> binaryFunction,
			IExpr rightArg) {
		for (int i = fStart; i < fEnd; i++) {
			list.add(binaryFunction.apply(fList.get(i), rightArg));
		}
		return list;
	}

	/**
	 * Returns the largest value in the range
	 * 
	 * @param comp
	 * @return
	 */
	public IExpr max(Comparator<? super IExpr> comp) {
		IExpr value = fList.get(fStart);
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
	 * @param comp
	 * @return
	 */
	public IExpr min(Comparator<? super IExpr> comp) {
		IExpr value = fList.get(fStart);
		for (int i = fStart + 1; i < fEnd; i++) {

			if (comp.compare(fList.get(i), value) < 0) {
				value = fList.get(i);
			}
		}
		return value;
	}

	/**
	 * Apply the predicate to each element in the range and append the elements
	 * to the list, which don't match the predicate.
	 * 
	 * @param list
	 * @param predicate
	 * @return the given list
	 */
	public Collection<IExpr> removeAll(Collection<IExpr> list, Predicate<IExpr> predicate) {
		for (int i = fStart; i < fEnd; i++) {

			if (!predicate.test(fList.get(i))) {
				list.add(fList.get(i));
			}
		}
		return list;
	}

	/**
	 * Apply the function to each element in the range and append the results to
	 * the list.
	 * 
	 * @param list
	 * @param function
	 * @return the given list
	 */
	public Collection<IExpr> replaceAll(Collection<IExpr> list, final Function<IExpr, ? extends IExpr> function) {
		for (int i = fStart; i < fEnd; i++) {
			list.add(function.apply(fList.get(i)));
		}
		return list;
	}

	/**
	 * Append the ranges elements in reversed order to the given
	 * <code>list</code>
	 * 
	 * @param list
	 * @return
	 */
	public Collection<IExpr> reverse(Collection<IExpr> list) {
		for (int i = fEnd - 1; i >= fStart; i--) {
			list.add(fList.get(i));
		}
		return list;
	}

	/**
	 * Rotate the ranges elements to the left by n places and append the
	 * resulting elements to the <code>list</code>
	 * 
	 * @param list
	 * @param n
	 * @return the given list
	 */
	public IAST rotateLeft(IAST list, final int n) {
		for (int i = fStart + n; i < fEnd; i++) {
			list.append(fList.get(i));
		}
		if (n <= size()) {
			for (int i = fStart; i < fStart + n; i++) {
				list.append(fList.get(i));
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
	 * @return the given list
	 */
	public IAST rotateRight(IAST list, final int n) {
		if (n <= size()) {
			for (int i = fEnd - n; i < fEnd; i++) {
				list.append(fList.get(i));
			}
			for (int i = fStart; i < fEnd - n; i++) {
				list.append(fList.get(i));
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
	@Override
	public int size() {
		return fEnd - fStart;
	}

	/**
	 * Sorts the elements of the specified range "in place" (i.IExpr. modify the
	 * internal referenced list), according to the order induced by the
	 * specified comparator.
	 */
	@Override
	public void sort(Comparator<? super IExpr> comparator) {
		final IExpr[] a = fList.toArray();//new IExpr[fList.size()]);
		Arrays.sort(a, fStart, fEnd, comparator);
		for (int j = fStart; j < fEnd; j++) {
			fList.set(j, a[j]);
		}
	}

	public IExpr[] toArray(IExpr[] array) {
		int j = fStart;
		for (int i = 0; i < array.length; i++) {
			array[i] = fList.get(j++);
			if (j >= array.length) {
				break;
			}
		}
		return array;
	}

}
