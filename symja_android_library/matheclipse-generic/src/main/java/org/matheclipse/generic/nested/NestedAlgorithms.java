package org.matheclipse.generic.nested;

import java.util.Collection;
import java.util.List;

import org.matheclipse.generic.interfaces.IPositionConverter;
import org.matheclipse.generic.interfaces.ISequence;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Nested list algorithms. I.e. algorithms for a list which contains objects of
 * interface type <code>List<T></code> and <code>T</code>.
 * 
 * The derived instances have to define the clone and copy semantics.
 */
public abstract class NestedAlgorithms<T extends INestedListElement, L extends List<T> & INestedListElement> implements
		INestedList<T, L> {
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	final public L cast(T obj) {
		return (L) obj;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	final public T castList(L list) {
		return (T) list;
	}

	/**
	 * Replace all headers in the nested list with the <code>newHead</code>,
	 * according to the level specification.
	 * 
	 */
	// public T apply(final T expr, final Function<T, T> function, final LevelSpec
	// level) {
	// return apply(expr, function, level, 0);
	// }

	/**
	 * Replace all headers in the nested list with the <code>newHead</code>,
	 * according to the level specification.
	 * 
	 */
	// public T apply(final T expr, final Function<T, T> function, final LevelSpec
	// level, final int headOffset) {
	// L result = null;
	// int minDepth = 0;
	//
	// level.incCurrentLevel();
	//
	// T temp;
	//
	// if (isInstance(expr)) {
	// final L list = cast(expr);
	// for (int i = headOffset; i < list.size(); i++) {
	//
	// temp = apply(list.get(i), function, level, headOffset);
	// if (temp != null) {
	// if (result == null) {
	// result = clone(list);
	// }
	// result.set(i, temp);
	// }
	// if (level.getCurrentDepth() < minDepth) {
	// minDepth = level.getCurrentDepth();
	// }
	// }
	//
	// level.setCurrentDepth(--minDepth);
	// level.decCurrentLevel();
	// if (level.isInRange()) {
	// if (result == null) {
	// result = clone(list);
	// }
	// result.set(0, function.apply(result.get(0)));
	// }
	// } else {
	// level.setCurrentDepth(--minDepth);
	// level.decCurrentLevel();
	// }
	// return castList(result);
	// }

	/**
	 * Count all elements and nested elements in the list, which satisfies the
	 * unary predicate
	 * 
	 */
	public int countIf(final L argList, final Predicate<? super T> matcher) {
		return countIf(argList, matcher, 0);
	}

	/**
	 * Count all elements and nested elements in the list, which satisfies the
	 * unary predicate.
	 * 
	 * @param <T>
	 * @param <L>
	 * @param argList
	 * @param matcher
	 * @param headOffset
	 * @param copier
	 * @return
	 */
	public int countIf(final L argList, final Predicate<? super T> matcher, int headOffset) {
		int counter = 0;
		L list;
		for (int i = headOffset; i < argList.size(); i++) {
			if (isInstance(argList.get(i))) {
				list = cast(argList.get(i));
				counter += countIf(list, matcher, headOffset);
			} else if (matcher.apply(argList.get(i))) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Calculates the depth of an expression. Atomic expressions (no sublists)
	 * have depth <code>1</code> Example: the nested list <code>[x,[y]]</code> has
	 * depth <code>3</code>
	 * 
	 */
	public int depth(final L list) {
		return depth(list, 0);
	}

	/**
	 * Calculates the depth of an expression. Atomic expressions (no sublists)
	 * have depth <code>1</code> Example: the nested list <code>[x,[y]]</code> has
	 * depth <code>3</code>
	 * 
	 * @param headOffset
	 * 
	 */
	public int depth(final L list, int headOffset) {
		int maxDepth = 1;
		int d;
		for (int i = headOffset; i < list.size(); i++) {
			if (isInstance(list.get(i))) {
				d = depth(cast(list.get(i)), headOffset);
				if (d > maxDepth) {
					maxDepth = d;
				}
			}
		}
		return ++maxDepth;
	}

	/**
	 * Traverse all <code>list</code> element's and filter out the element in the
	 * given <code>positions</code> list.
	 * 
	 * @param list
	 * @param positions
	 * @param positionConverter
	 *          the <code>positionConverter</code> creates an <code>int</code>
	 *          value from the given position objects in <code>positions</code>.
	 * @param headOffsez
	 */
	public T extract(final L list, final List<? extends T> positions, final IPositionConverter<? super T> positionConverter) {
		return extract(list, positions, positionConverter, 0);
	}

	/**
	 * Traverse all <code>list</code> element's and filter out the elements in the
	 * given <code>positions</code> list.
	 * 
	 * @param list
	 * @param positions
	 * @param positionConverter
	 *          the <code>positionConverter</code> creates an <code>int</code>
	 *          value from the given position objects in <code>positions</code>.
	 * @param headOffsez
	 */
	public T extract(final L list, final List<? extends T> positions, final IPositionConverter<? super T> positionConverter,
			int headOffset) {
		int p = 0;
		L temp = list;
		int posSize = positions.size() - 1;
		T expr = castList(list);
		for (int i = headOffset; i <= posSize; i++) {
			p = positionConverter.toInt(positions.get(i));
			if (temp == null || temp.size() <= p) {
				return null;
			}
			expr = temp.get(p);
			if (isInstance(expr)) {
				temp = cast(expr);
			} else {
				if (i < positions.size()) {
					temp = null;
				}
			}
		}
		return expr;
	}

	/**
	 * Traverse all <code>nestedListElement</code>'s and add the matching elements
	 * to the <code>resultCollection</code>.
	 * 
	 * @param nestedListElement
	 * @param matcher
	 * @param resultCollection
	 * @param headOffsez
	 */
	public Collection<? super T> extract(T nestedListElement, final Predicate<? super T> matcher,
			final Collection<? super T> resultCollection) {
		return extract(nestedListElement, matcher, resultCollection, 0);
	}

	/**
	 * Traverse all <code>nestedListElement</code>'s and add the matching elements
	 * to the <code>resultCollection</code>.
	 * 
	 * @param nestedListElement
	 * @param matcher
	 * @param resultCollection
	 * @param headOffsez
	 */
	public Collection<? super T> extract(T nestedListElement, final Predicate<? super T> matcher,
			final Collection<? super T> resultCollection, int headOffset) {
		if (matcher.apply(nestedListElement)) {
			resultCollection.add(nestedListElement);
		}
		if (isInstance(nestedListElement)) {
			L list = cast(nestedListElement);
			final int size = list.size();
			for (int i = headOffset; i < size; i++) {
				extract(list.get(i), matcher, resultCollection, headOffset);
			}
		}
		return resultCollection;
	}

	/**
	 * Flatten out all sublists of <code>argList</code> into
	 * <code>resultList</code>
	 * 
	 * @param argList
	 * @param resultList
	 * @param headOffset
	 * @return <code>true</code> if a sublist was flattened out
	 */
	public boolean flatten(final L argList, final Collection<? super T> resultList) {
		return flatten(argList, resultList, 0);
	}

	/**
	 * Flatten out all sublists of <code>argList</code> into
	 * <code>resultList</code>
	 * 
	 * @param argList
	 * @param resultList
	 * @param headOffset
	 * @return <code>true</code> if a sublist was flattened out
	 */
	public boolean flatten(final L argList, final Collection<? super T> resultList, final int headOffset) {
		boolean isEvaled = false;
		final int astSize = argList.size();
		for (int i = headOffset; i < astSize; i++) {
			if (isInstance(argList.get(i))) {
				isEvaled = true;
				flatten(cast(argList.get(i)), resultList, headOffset);
			} else {
				resultList.add(argList.get(i));
			}
		}

		return isEvaled;
	}

	/**
	 * Flatten the list [i.e. the lists <code>get(0)</code> element has the same
	 * head] example: suppose the head f should be flattened out:<br>
	 * f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]
	 * 
	 * @param argList
	 * @return <code>true</code> if a sublist was flattened out
	 */
	public boolean flatten(final T head, final L argList, final Collection<? super T> resultList) {
		return flatten(head, argList, resultList, 0);
	}

	/**
	 * Flatten the list [i.e. the lists <code>get(0)</code> element has the same
	 * head] example: suppose the head f should be flattened out:<br>
	 * f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]
	 * 
	 * @param argList
	 * @return <code>true</code> if a sublist was flattened out
	 */
	public boolean flatten(final T head, final L argList, final Collection<? super T> resultList, final int headOffset) {
		boolean isEvaled = false;

		L list;
		final int astSize = argList.size();
		for (int i = headOffset; i < astSize; i++) {
			if (isInstance(argList.get(i))) {
				list = cast(argList.get(i));
				if (list.get(0).equals(head)) {
					isEvaled = true;
					flatten(head, list, resultList, headOffset);
				} else {
					resultList.add(argList.get(i));
				}
			} else {
				resultList.add(argList.get(i));
			}
		}

		return isEvaled;
	}

	/**
	 * Add all expressions according to the level specification <code>level</code>
	 * to the <code>resultCollection</code>
	 * 
	 * @param <T>
	 * @param expr
	 * @param level
	 * @param resultCollection
	 */
	// public Collection<? super T> level(final T expr, final LevelSpec level,
	// final Collection<? super T> resultCollection) {
	// return level(expr, level, resultCollection, 0);
	// }

	/**
	 * Add all expressions according to the level specification <code>level</code>
	 * to the <code>resultCollection</code>
	 * 
	 * @param <T>
	 * @param expr
	 * @param level
	 * @param resultCollection
	 * @param headOffset
	 */
	// public Collection<? super T> level(final T expr, final LevelSpec level,
	// final Collection<? super T> resultCollection,
	// int headOffset) {
	// int minDepth = 0;
	// level.incCurrentLevel();
	// L list;
	// if (isInstance(expr)) {
	// list = cast(expr);
	// for (int i = headOffset; i < list.size(); i++) {
	//
	// level(list.get(i), level, resultCollection, headOffset);
	// if (level.getCurrentDepth() < minDepth) {
	// minDepth = level.getCurrentDepth();
	// }
	// }
	//
	// level.setCurrentDepth(--minDepth);
	// level.decCurrentLevel();
	// if (level.isInRange()) {
	// resultCollection.add(expr);
	// }
	// } else {
	// level.setCurrentDepth(--minDepth);
	// level.decCurrentLevel();
	// if (level.isInRange()) {
	// resultCollection.add(expr);
	// }
	// }
	// return resultCollection;
	// }

	// public void total(final T expr, final LevelSpec level, final Function<T, T>
	// function, int headOffset) {
	// int minDepth = 0;
	// level.incCurrentLevel();
	// L list;
	// if (isInstance(expr)) {
	// list = cast(expr);
	// for (int i = headOffset; i < list.size(); i++) {
	//
	// total(list.get(i), level, function, headOffset);
	// if (level.getCurrentDepth() < minDepth) {
	// minDepth = level.getCurrentDepth();
	// }
	// }
	// level.setCurrentDepth(--minDepth);
	// level.decCurrentLevel();
	// } else {
	// level.setCurrentDepth(--minDepth);
	// level.decCurrentLevel();
	// if (level.isInRange()) {
	// function.apply(expr);
	// }
	// }
	// }

	/**
	 * Add the positions to the <code>resultCollection</code> where the matching
	 * expressions appear in <code>list</code>. The <code>positionConverter</code>
	 * converts the <code>int</code> position into an object for the
	 * <code>resultCollection</code>.
	 * 
	 * @param list
	 * @param prototypeList
	 * @param resultCollection
	 * @param level
	 * @param matcher
	 * @param positionConverter
	 */
	public Collection<? super T> position(final L list, final L prototypeList, final Collection<? super T> resultCollection,
			final LevelSpec level, final Predicate<? super T> matcher, final IPositionConverter<? extends T> positionConverter) {
		return position(list, prototypeList, resultCollection, level, matcher, positionConverter, 0);
	}

	/**
	 * Add the positions to the <code>resultCollection</code> where the matching
	 * expressions appear in <code>list</code>. The <code>positionConverter</code>
	 * converts the <code>int</code> position into an object for the
	 * <code>resultCollection</code>.
	 * 
	 * @param list
	 * @param prototypeList
	 * @param resultCollection
	 * @param level
	 * @param matcher
	 * @param positionConverter
	 * @param headOffset
	 */
	public Collection<? super T> position(final L list, final L prototypeList, final Collection<? super T> resultCollection,
			final LevelSpec level, final Predicate<? super T> matcher, final IPositionConverter<? extends T> positionConverter,
			int headOffset) {
		int minDepth = 0;
		level.incCurrentLevel();
		L clone = null;
		final int size = list.size();
		for (int i = headOffset; i < size; i++) {
			if (matcher.apply(list.get(i))) {
				if (level.isInRange()) {
					clone = clone(prototypeList);
					T t = positionConverter.toObject(i);
					clone.add(t);
					resultCollection.add(castList(clone));
				}
			} else if (isInstance(list.get(i))) {
				// clone = (INestedList<T>) prototypeList.clone();
				clone = clone(prototypeList);
				clone.add(positionConverter.toObject(i));
				position(cast(list.get(i)), clone, resultCollection, level, matcher, positionConverter, headOffset);
				if (level.getCurrentDepth() < minDepth) {
					minDepth = level.getCurrentDepth();
				}
			}
		}
		level.setCurrentDepth(--minDepth);
		level.decCurrentLevel();
		return resultCollection;
	}

	/**
	 * Replace all elements which are equal to <code>from</code>, found in
	 * expression <code>expr</code> with the <code>to</code> object. If no
	 * replacement is found return <code>null</code>
	 */
	public T replace(final T expr, final T from, final T to) {
		return replace(expr, from, to, 0);
	}

	/**
	 * Replace all elements which are equal to <code>from</code>, found in
	 * expression <code>expr</code> with the <code>to</code> object. If no
	 * replacement is found return <code>null</code>
	 */
	public T replace(final T expr, final T from, final T to, final int headOffset) {
		if (expr.equals(from)) {
			return to;
		}
		L nestedList;

		if (isInstance(expr)) {
			nestedList = cast(expr);

			L result = null;
			T temp;
			final int size = nestedList.size();
			for (int i = headOffset; i < size; i++) {

				temp = replace(nestedList.get(i), from, to, headOffset);
				if (temp != null) {
					if (result == null) {
						result = clone(nestedList);
					}
					result.set(i, temp);
				}
			}
			return castList(result);
		}
		return null;
	}

	/**
	 * Replace all elements determined by the unary <code>from</code> predicate,
	 * with the element generated by the unary <code>to</code> function. If the
	 * unary function returns null replaceAll returns null.
	 */
	public T replaceAll(final T expr, final Predicate<T> from, final Function<T, ? extends T> to) {
		if (from.apply(expr)) {
			return to.apply(expr);
		}
		L nestedList;
		if (isInstance(expr)) {
			nestedList = cast(expr);
			L result = null;
			final T head = nestedList.get(0);
			T temp = replaceAll(head, from, to);
			if (temp != null) {
				result = clone(nestedList);
				result.set(0, temp);
			} else {
				return null;
			}
			final int size = nestedList.size();
			for (int i = 1; i < size; i++) {

				temp = replaceAll(nestedList.get(i), from, to);
				if (temp != null) {
					if (result == null) {
						result = clone(nestedList);
					}
					result.set(i, temp);
				} else {
					return null;
				}
			}
			return castList(result);
		}
		return expr;
	}

	/**
	 * Replace all elements in the <code>from</code> list, found in expression
	 * <i>expr</i> with the corresponding elements in the <code>to</code> list. If
	 * no replacement is found return <code>null</code>
	 * 
	 * @param <T>
	 * @param expr
	 * @param from
	 * @param to
	 * @return
	 */
	// public T replaceAll(final T expr, final L from, final L to) {
	// return replaceAll(expr, from, to, 0);
	// }

	/**
	 * Replace all elements in the <code>from</code> list, found in expression
	 * <code>expr</code> with the corresponding elements in the <code>to</code>
	 * list. If no replacement is found return <code>null</code>
	 */
	// public T replaceAll(final T expr, final L from, final L to, final int
	// headOffset) {
	// for (int i = headOffset; i < from.size(); i++) {
	//
	// if (expr.equals(from.get(i))) {
	// return to.get(i);
	// }
	// }
	// L nestedList;
	// if (isInstance(expr)) {
	// nestedList = cast(expr);
	// L result = null;
	// final T head = nestedList.get(0);
	// T temp = replaceAll(head, from, to, headOffset);
	// if (temp != null) {
	// result = clone(nestedList);
	// result.set(0, temp);
	// }
	// for (int i = 1; i < nestedList.size(); i++) {
	//
	// temp = replaceAll(nestedList.get(i), from, to, headOffset);
	// if (temp != null) {
	// if (result == null) {
	// result = clone(nestedList);
	// }
	// result.set(i, temp);
	// }
	// }
	// return castList(result);
	// }
	// return null;
	// }

	public L take(final L list, final int level, final ISequence[] sequ) {
		sequ[level].setListSize(list.size());
		final L resultList = newInstance(list);
		final int newLevel = level + 1;
		for (int i = sequ[level].getStart(); i < sequ[level].getEnd(); i += sequ[level].getStep()) {
			if (sequ.length > newLevel) {
				if (isInstance(list.get(i))) {
					resultList.add(castList(take(cast(list.get(i)), newLevel, sequ)));
				}
			} else {
				resultList.add(list.get(i));
			}
		}
		return resultList;
	}
}
