package org.matheclipse.core.generic.interfaces;

import javax.annotation.Nullable;

/**
 * A BiPredicate can determine a true or false value for any input of its
 * parameterized types. 
 * 
 * <p>
 * Implementors of BiPredicate which may cause side effects upon evaluation are
 * strongly encouraged to state this fact clearly in their API documentation.
 * 
 * <p>
 * <b>NOTE:</b> This interface <i>could</i> technically extend
 * {@link BiFunction}, since a predicate is just a special case of a function (one
 * that returns a boolean). However, since implementing this would entail
 * changing the signature of the {@link #apply} method to return a
 * {@link Boolean} instead of a {@code boolean}, which would in turn allow
 * people to return {@code null} from their BiPredicate, which would in turn
 * enable code that looks like this
 * {@code if (myPredicate.apply(myObject)) ... } to throw a
 * {@link NullPointerException}, it was decided not to make this change.
 * 
 */
public interface BiPredicate<T> {
	/**
   * Applies this Predicate to the given objects.
   *
   * @return the value of this Predicate when applied to input {@code t1} and {@code t2}.
   */
	boolean apply(@Nullable T t1, @Nullable T t2);
}
