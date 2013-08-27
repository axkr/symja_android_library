package org.matheclipse.generic.nested;

import java.util.Collection;
import java.util.List;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.interfaces.IPositionConverter;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Nested list algorithms. I.e. algorithms for a list which contains objects of interface type <code>List<IExpr></code> and
 * <code>IExpr</code>.
 * 
 * The derived instances have to define the clone and copy semantics.
 */
public abstract class NestedAlgorithms {

	/**
	 * Count all elements and nested elements in the list, which satisfies the unary predicate
	 * 
	 */
	public static int countIf(final IAST argList, final Predicate<? super IExpr> matcher) {
		return countIf(argList, matcher, 0);
	}

	/**
	 * Count all elements and nested elements in the list, which satisfies the unary predicate.
	 * 
	 * @param <IExpr>
	 * @param <IAST>
	 * @param argList
	 * @param matcher
	 * @param headOffset
	 * @param copier
	 * @return
	 */
	public static int countIf(final IAST argList, final Predicate<? super IExpr> matcher, int headOffset) {
		int counter = 0;
		IAST list;
		for (int i = headOffset; i < argList.size(); i++) {
			if (argList.get(i).isAST()) {
				list = (IAST) argList.get(i);
				counter += countIf(list, matcher, headOffset);
			} else if (matcher.apply(argList.get(i))) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Calculates the depth of an expression. Atomic expressions (no sublists) have depth <code>1</code> Example: the nested list
	 * <code>[x,[y]]</code> has depth <code>3</code>
	 * 
	 */
	public static int depth(final IAST list) {
		return depth(list, 0);
	}

	/**
	 * Calculates the depth of an expression. Atomic expressions (no sublists) have depth <code>1</code> Example: the nested list
	 * <code>[x,[y]]</code> has depth <code>3</code>
	 * 
	 * @param headOffset
	 * 
	 */
	public static int depth(final IAST list, int headOffset) {
		int maxDepth = 1;
		int d;
		for (int i = headOffset; i < list.size(); i++) {
			if (list.get(i).isAST()) {
				d = depth((IAST) list.get(i), headOffset);
				if (d > maxDepth) {
					maxDepth = d;
				}
			}
		}
		return ++maxDepth;
	}

	/**
	 * Traverse all <code>list</code> element's and filter out the element in the given <code>positions</code> list.
	 * 
	 * @param list
	 * @param positions
	 * @param positionConverter
	 *            the <code>positionConverter</code> creates an <code>int</code> value from the given position objects in
	 *            <code>positions</code>.
	 * @param headOffsez
	 */
	public static IExpr extract(final IAST list, final List<? extends IExpr> positions,
			final IPositionConverter<? super IExpr> positionConverter) {
		return extract(list, positions, positionConverter, 0);
	}

	/**
	 * Traverse all <code>list</code> element's and filter out the elements in the given <code>positions</code> list.
	 * 
	 * @param list
	 * @param positions
	 * @param positionConverter
	 *            the <code>positionConverter</code> creates an <code>int</code> value from the given position objects in
	 *            <code>positions</code>.
	 * @param headOffsez
	 */
	public static IExpr extract(final IAST list, final List<? extends IExpr> positions,
			final IPositionConverter<? super IExpr> positionConverter, int headOffset) {
		int p = 0;
		IAST temp = list;
		int posSize = positions.size() - 1;
		IExpr expr = list;
		for (int i = headOffset; i <= posSize; i++) {
			p = positionConverter.toInt(positions.get(i));
			if (temp == null || temp.size() <= p) {
				return null;
			}
			expr = temp.get(p);
			if (expr.isAST()) {
				temp = (IAST) expr;
			} else {
				if (i < positions.size()) {
					temp = null;
				}
			}
		}
		return expr;
	}

	/**
	 * Traverse all <code>nestedListElement</code>'s and add the matching elements to the <code>resultCollection</code>.
	 * 
	 * @param nestedListElement
	 * @param matcher
	 * @param resultCollection
	 * @param headOffsez
	 */
	public static Collection<? super IExpr> extract(IExpr nestedListElement, final Predicate<? super IExpr> matcher,
			final Collection<? super IExpr> resultCollection) {
		return extract(nestedListElement, matcher, resultCollection, 0);
	}

	/**
	 * Traverse all <code>nestedListElement</code>'s and add the matching elements to the <code>resultCollection</code>.
	 * 
	 * @param nestedListElement
	 * @param matcher
	 * @param resultCollection
	 * @param headOffsez
	 */
	public static Collection<? super IExpr> extract(IExpr nestedListElement, final Predicate<? super IExpr> matcher,
			final Collection<? super IExpr> resultCollection, int headOffset) {
		if (matcher.apply(nestedListElement)) {
			resultCollection.add(nestedListElement);
		}
		if (nestedListElement.isAST()) {
			IAST list = (IAST) nestedListElement;
			final int size = list.size();
			for (int i = headOffset; i < size; i++) {
				extract(list.get(i), matcher, resultCollection, headOffset);
			}
		}
		return resultCollection;
	}

	/**
	 * Add the positions to the <code>resultCollection</code> where the matching expressions appear in <code>list</code>. The
	 * <code>positionConverter</code> converts the <code>int</code> position into an object for the <code>resultCollection</code>.
	 * 
	 * @param list
	 * @param prototypeList
	 * @param resultCollection
	 * @param level
	 * @param matcher
	 * @param positionConverter
	 */
	public static Collection<? super IExpr> position(final IAST list, final IAST prototypeList,
			final Collection<? super IExpr> resultCollection, final LevelSpec level, final Predicate<? super IExpr> matcher,
			final IPositionConverter<? extends IExpr> positionConverter) {
		return position(list, prototypeList, resultCollection, level, matcher, positionConverter, 0);
	}

	/**
	 * Add the positions to the <code>resultCollection</code> where the matching expressions appear in <code>list</code>. The
	 * <code>positionConverter</code> converts the <code>int</code> position into an object for the <code>resultCollection</code>.
	 * 
	 * @param list
	 * @param prototypeList
	 * @param resultCollection
	 * @param level
	 * @param matcher
	 * @param positionConverter
	 * @param headOffset
	 */
	public static Collection<? super IExpr> position(final IAST list, final IAST prototypeList,
			final Collection<? super IExpr> resultCollection, final LevelSpec level, final Predicate<? super IExpr> matcher,
			final IPositionConverter<? extends IExpr> positionConverter, int headOffset) {
		int minDepth = 0;
		level.incCurrentLevel();
		IAST clone = null;
		final int size = list.size();
		for (int i = headOffset; i < size; i++) {
			if (matcher.apply(list.get(i))) {
				if (level.isInRange()) {
					clone = prototypeList.clone();
					IExpr IExpr = positionConverter.toObject(i);
					clone.add(IExpr);
					resultCollection.add(clone);
				}
			} else if (list.get(i).isAST()) {
				// clone = (INestedList<IExpr>) prototypeList.clone();
				clone = prototypeList.clone();
				clone.add(positionConverter.toObject(i));
				position((IAST) list.get(i), clone, resultCollection, level, matcher, positionConverter, headOffset);
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
	 * Replace all elements which are equal to <code>from</code>, found in expression <code>expr</code> with the <code>to</code>
	 * object. If no replacement is found return <code>null</code>
	 */
	public static IExpr replace(final IExpr expr, final IExpr from, final IExpr to) {
		return replace(expr, from, to, 0);
	}

	/**
	 * Replace all elements which are equal to <code>from</code>, found in expression <code>expr</code> with the <code>to</code>
	 * object. If no replacement is found return <code>null</code>
	 */
	public static IExpr replace(final IExpr expr, final IExpr from, final IExpr to, final int headOffset) {
		if (expr.equals(from)) {
			return to;
		}
		IAST nestedList;

		if (expr.isAST()) {
			nestedList = (IAST) expr;

			IAST result = null;
			IExpr temp;
			final int size = nestedList.size();
			for (int i = headOffset; i < size; i++) {

				temp = replace(nestedList.get(i), from, to, headOffset);
				if (temp != null) {
					if (result == null) {
						result = nestedList.clone();
					}
					result.set(i, temp);
				}
			}
			return result;
		}
		return null;
	}

	/**
	 * Replace all elements determined by the unary <code>from</code> predicate, with the element generated by the unary
	 * <code>to</code> function. If the unary function returns null replaceAll returns null.
	 */
	public static IExpr replaceAll(final IExpr expr, final Predicate<IExpr> from, final Function<IExpr, ? extends IExpr> to) {
		if (from.apply(expr)) {
			return to.apply(expr);
		}
		IAST nestedList;
		if (expr.isAST()) {
			nestedList = (IAST) expr;
			IAST result = null;
			final IExpr head = nestedList.get(0);
			IExpr temp = replaceAll(head, from, to);
			if (temp != null) {
				result = nestedList.clone();
				result.set(0, temp);
			} else {
				return null;
			}
			final int size = nestedList.size();
			for (int i = 1; i < size; i++) {

				temp = replaceAll(nestedList.get(i), from, to);
				if (temp != null) {
					result = nestedList.clone();
					result.set(i, temp);
				} else {
					return null;
				}
			}
			return result;
		}
		return expr;
	}

}
