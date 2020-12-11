package org.matheclipse.core.generic;

import java.util.Comparator;
import java.util.function.BiPredicate;

import org.matheclipse.core.interfaces.IExpr;

public class Comparators {
  /**
   * Compares an expression with another expression for order. Returns a negative integer, zero, or
   * a positive integer if this expression is canonical less than, equal to, or greater than the
   * specified expression.
   */
  public static final class ExprComparator implements Comparator<IExpr> {
    public static final ExprComparator CONS = new ExprComparator();

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
  public static final class ExprReverseComparator implements Comparator<IExpr> {
    public static final ExprReverseComparator CONS = new ExprReverseComparator();

    @Override
    public int compare(final IExpr o1, final IExpr o2) {
      return o2.compareTo(o1);
    }
  }

  /** Comparator for ExpVectors. */
  public static final class BinaryHeadComparator implements Comparator<IExpr> {
    BiPredicate<IExpr, IExpr> predicate;

    public BinaryHeadComparator(IExpr test) {
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
}
