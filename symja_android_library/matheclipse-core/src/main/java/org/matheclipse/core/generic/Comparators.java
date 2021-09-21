package org.matheclipse.core.generic;

import java.util.Comparator;
import java.util.function.BiPredicate;
import org.matheclipse.core.builtin.ListFunctions;
import org.matheclipse.core.interfaces.IExpr;

/** Provide <code>Comparator<IExpr></code> classes. */
public final class Comparators {

  private Comparators() {}

  /**
   * Compares an expression with another expression for order. Returns a negative integer, zero, or
   * a positive integer if this expression is canonical less than, equal to, or greater than the
   * specified expression.
   */
  public static final ExprComparator CANONICAL_COMPARATOR = new ExprComparator();

  /**
   * Reverse {@link #CANONICAL_COMPARATOR}, Compares an expression with another expression for
   * order. Returns a negative integer, zero, or a positive integer if this expression is canonical
   * greater than, equal to, or less than the specified expression.
   */
  public static final ExprReverseComparator REVERSE_CANONICAL_COMPARATOR =
      new ExprReverseComparator();

  /**
   * Compares an expression with another expression for order. Returns a negative integer, zero, or
   * a positive integer if this expression is canonical less than, equal to, or greater than the
   * specified expression.
   */
  static final class ExprComparator implements Comparator<IExpr> {

    @Override
    public final int compare(final IExpr o1, final IExpr o2) {
      return o1.compareTo(o2);
    }
  }

  /**
   * Compares an expression with another expression for order. Returns a negative integer, zero, or
   * a positive integer if this expression is canonical greater than, equal to, or less than the
   * specified expression.
   */
  static final class ExprReverseComparator implements Comparator<IExpr> {

    /**
     * Compares an expression with another expression for order. Returns a negative integer, zero,
     * or a positive integer if this expression is canonical greater than, equal to, or less than
     * the specified expression.
     */
    @Override
    public int compare(final IExpr o1, final IExpr o2) {
      return o2.compareTo(o1);
    }
  }

  /**
   * Comparator for using an internal {@link BiPredicate} from {@link
   * Predicates#isBinaryTrue(IExpr)} as a test for equality. If not equal call the <code>compareTo()
   * </code> method from <code>IExpr</code>.
   */
  static final class BinaryEqualsComparator implements Comparator<IExpr> {
    BiPredicate<IExpr, IExpr> predicate;

    public BinaryEqualsComparator(IExpr test) {
      this.predicate = Predicates.isBinaryTrue(test);
    }

    @Override
    public int compare(IExpr e1, IExpr e2) {
      if (predicate.test(e1, e2)) {
        return 0;
      }
      return e1.compareTo(e2);
    }
  }

  /**
   * Comparator for using an internal {@link BiPredicate} from {@link
   * Predicates#isBinaryTrue(IExpr)} as a test for equality. If not equal call the <code>compareTo()
   * </code> method from <code>IExpr</code>.
   *
   * @param test the test header. See for example function {@link ListFunctions.Gather}
   * @return
   */
  public static Comparator<IExpr> binaryPredicateComparator(IExpr test) {

    return new BinaryEqualsComparator(test);
  }
}
