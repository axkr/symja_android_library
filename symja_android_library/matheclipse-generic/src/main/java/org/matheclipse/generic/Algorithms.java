package org.matheclipse.generic;

import java.util.Collection;
import java.util.List;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.interfaces.BiFunction;
import org.matheclipse.generic.interfaces.ISequence;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Functional Programming algorithms for <code>java.util.List</code> and <code>java.util.Collection</code>.
 * 
 * @see org.matheclipse.generic.nested.NestedAlgorithms NestedAlgorithms for nested lists algorithms
 */
public class Algorithms {

	/**
	 * Return the number of elements in the collection that equals the given value.
	 */
	public static int count(final Collection<IExpr> collection, final IExpr value) {
		int counter = 0;
		for (final IExpr obj : collection) {

			if (obj.equals(value)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Return the number of elements in the <code>list</code> that match the given predicate.
	 */
	public static int count(final List<IExpr> list, final int start, final int end, final Predicate<IExpr> fn) {
		int counter = 0;
		for (int i = start; i < end; i++) {

			if (fn.apply(list.get(i))) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Return the number of elements in the <code>list</code> that equals the given value.
	 */
	public static int count(final List<IExpr> list, final int start, final int end, final IExpr value) {
		int counter = 0;
		for (int i = start; i < end; i++) {

			if (list.get(i).equals(value)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Returns the number of elements in the collection that match the given predicate.
	 */
	public static int countIf(final Collection<IExpr> collection, final Predicate<IExpr> predicate) {
		int counter = 0;
		for (final IExpr obj : collection) {
			if (predicate.apply(obj)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Returns the number of elements in the collection that doesn't match the given predicate.
	 */
	public static int countIfNot(final Collection<IExpr> collection, final Predicate<IExpr> predicate) {
		int counter = 0;
		for (final IExpr obj : collection) {
			if (!predicate.apply(obj)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Drop (remove) the list elements according to the <code>sequenceSpecification</code> for the list indexes.
	 * 
	 * @param <IExpr>
	 * @param list
	 * @param resultList
	 * @param sequenceSpecification
	 */
	public static List<? extends IExpr> drop(final List<? extends IExpr> resultList, final ISequence sequenceSpecification) {
		sequenceSpecification.setListSize(resultList.size());
		int j = sequenceSpecification.getStart();
		for (int i = j; i < sequenceSpecification.getEnd(); i += sequenceSpecification.getStep()) {
			resultList.remove(j);
			j += sequenceSpecification.getStep() - 1;
		}
		return resultList;
	}

	/**
	 * Fold the list from lowest to highest index. If the <i>binaryFunction</i> returns <code>null</code>, the left element will be
	 * added to the result list, otherwise the result will be <i>folded</i> again with the next element in the list.
	 * 
	 * @param <IExpr>
	 * @param list
	 * @param binaryFunction
	 * @param resultList
	 */
	public static Collection<? super IExpr> foldLeft(final IExpr expr, final List<IExpr> list,
			final BiFunction<IExpr, IExpr, ? extends IExpr> binaryFunction, final Collection<? super IExpr> resultList) {
		return Algorithms.foldLeft(expr, list, 0, list.size(), binaryFunction, resultList);
	}

	/**
	 * Fold the list from <code>start</code> index including to <code>end</code> index excluding into the
	 * <code>resultCollection</code>. If the <i>binaryFunction</i> returns <code>null</code>, the left element will be added to the
	 * result list, otherwise the result will be <i>folded</i> again with the next element in the list.
	 * 
	 * @param <IExpr>
	 * @param list
	 * @param start
	 * @param end
	 * @param binaryFunction
	 * @param resultCollection
	 */
	public static Collection<? super IExpr> foldLeft(final IExpr expr, final List<IExpr> list, final int start, final int end,
			final BiFunction<IExpr, IExpr, ? extends IExpr> binaryFunction, final Collection<? super IExpr> resultCollection) {
		if (start < end) {
			IExpr elem = expr;
			resultCollection.add(elem);
			for (int i = start; i < end; i++) {

				elem = binaryFunction.apply(elem, list.get(i));
				resultCollection.add(elem);
			}

		}
		return resultCollection;
	}

	/**
	 * ForEach applies the unary <code>function</code> to each element in the <code>iterable</code>.
	 * 
	 * @param <IExpr>
	 * @param iterable
	 * @param function
	 */
	public static void forEach(final Iterable<? extends IExpr> iterable, final Function<IExpr, ? extends IExpr> function) {
		for (IExpr t : iterable) {
			function.apply(t);
		}
	}

	/**
	 * ForEach applies the unary <code>function</code> to each element in the <code>list</code> in the range
	 * <code>[start .. list.size()[</code>.
	 * 
	 * @param <IExpr>
	 * @param list
	 * @param start
	 * @param function
	 */
	public static void forEach(final List<? extends IExpr> list, int start, final Function<IExpr, ? extends IExpr> function) {
		forEach(list, start, list.size(), function);
	}

	/**
	 * ForEach applies the unary <code>function</code> to each element in the <code>list</code> in the range
	 * <code>[start .. end[</code>.
	 * 
	 * @param <IExpr>
	 * @param list
	 * @param start
	 * @param end
	 * @param function
	 */
	public static void forEach(final List<? extends IExpr> list, int start, int end, final Function<IExpr, ? extends IExpr> function) {
		int len = end < list.size() ? end : list.size();
		for (int i = start; i < len; i++) {
			function.apply(list.get(i));
		}
	}

	/**
	 * Add the list elements to the <code>resultCollection</code> according to the <code>sequenceSpecification</code> for the list
	 * indexes.
	 * 
	 * @param <IExpr>
	 * @param list
	 * @param resultCollection
	 * @param sequenceSpecification
	 */
	public static Collection<? super IExpr> take(final List<? extends IExpr> list,
			final Collection<? super IExpr> resultCollection, final ISequence sequenceSpecification) {
		sequenceSpecification.setListSize(list.size());
		for (int i = sequenceSpecification.getStart(); i < sequenceSpecification.getEnd(); i += sequenceSpecification.getStep()) {
			resultCollection.add(list.get(i));
		}
		return resultCollection;
	}

}
