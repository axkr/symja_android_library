package org.matheclipse.core.generic;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.BiPredicate;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.ListFunctions;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** Provide <code>Comparator<IExpr></code> classes. */
public final class Comparators {

  /**
   * Comparator for using an internal {@link BiPredicate} from
   * {@link Predicates#isBinaryTrue(IExpr)} as a test for equality. If not equal call the
   * <code>compareTo()
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
   * Compare the two {@link Complex#norm()} <code>double</code> values of the numbers. If the
   * <code>norm()</code> values are approximately equal, use
   * {@link ComplexNum#compare(Complex, Complex)} as the return value. Use
   * {@link Config#DEFAULT_CHOP_DELTA} to compare for approcimate equality.
   */
  private static final class ComplexNormReverseComparator
      implements Comparator<Complex>, Serializable {

    private static final long serialVersionUID = -343157250415373294L;

    @Override
    public final int compare(final Complex o1, final Complex o2) {
      double n1 = o2.norm();
      double n2 = o1.norm();
      if (F.isFuzzyEquals(n1, n2, Config.DEFAULT_CHOP_DELTA)) {
        return o1.compareTo(o2);
      }
      return n1 < n2 ? -1 : 1;
    }
  }

  public static final class SameTestComparator implements Comparator<IExpr>, Serializable {

    private static final long serialVersionUID = -3985330284976712163L;

    final BiPredicate<IExpr, IExpr> sameTest;

    public SameTestComparator(BiPredicate<IExpr, IExpr> sameTest) {
      this.sameTest = sameTest;
    }

    @Override
    public final int compare(final IExpr o1, final IExpr o2) {
      if (sameTest.test(o1,o2)) {
        return 0;
      }
      return o1.compareTo(o2);
    }
  }

  /**
   * Compares an expression with another expression for order. Returns a negative integer, zero, or
   * a positive integer if this expression is canonical less than, equal to, or greater than the
   * specified expression.
   */
  static final class ExprComparator implements Comparator<IExpr>, Serializable {

    private static final long serialVersionUID = -3985330284976712163L;

    @Override
    public final int compare(final IExpr o1, final IExpr o2) {
      return o1.compareTo(o2);
    }
  }

  static final class ExprLexicalComparator implements Comparator<IExpr>, Serializable {

    private static final long serialVersionUID = 6853759697109830142L;

    /**
     * Compares the elements of two {@link IAST}s (on the first level only) until the minimum size
     * of the {@link IAST}s, before considering their sequence lengths. Returns a negative integer,
     * zero, or a positive integer if this expression is canonical greater than, equal to, or less
     * than the specified expression.
     */
    @Override
    public int compare(final IExpr o1, final IExpr o2) {
      if (o1.isAST() && o2.isAST()) {
        IAST lhsAST = (IAST) o1;
        IAST rhsAST = (IAST) o2;

        if (lhsAST.isNot()) {
          IExpr arg1 = lhsAST.arg1();
          if ((rhsAST.isSymbol() && arg1.isSymbol()) || (rhsAST.isSlot() && arg1.isSlot())) {
            int ct = arg1.compareTo(rhsAST);
            if (ct != 0) {
              return ct;
            }
            return 1;
          }
        } else if (rhsAST.isNot()) {
          IExpr arg1 = rhsAST.arg1();
          if ((lhsAST.isSymbol() && arg1.isSymbol()) || (lhsAST.isSlot() && arg1.isSlot())) {
            int ct = lhsAST.compareTo(arg1);
            if (ct != 0) {
              return ct;
            }
            return 1;
          }
        }

        // compare the headers
        int cp = lhsAST.head().compareTo(rhsAST.head());
        if (cp != 0) {
          return cp;
        }

        final int minimumSize = (lhsAST.size() > rhsAST.size()) ? rhsAST.size() : lhsAST.size();
        for (int i = 1; i < minimumSize; i++) {
          cp = lhsAST.get(i).compareTo(rhsAST.get(i));
          if (cp != 0) {
            return cp;
          }
        }

        if (lhsAST.size() > rhsAST.size()) {
          return 1;
        }
        if (lhsAST.size() < rhsAST.size()) {
          return -1;
        }
        return 0;
      }
      return o1.compareTo(o2);
    }
  }

  /**
   * Compares an expression with another expression for order. Returns a negative integer, zero, or
   * a positive integer if this expression is canonical greater than, equal to, or less than the
   * specified expression.
   */
  static final class ExprReverseComparator implements Comparator<IExpr>, Serializable {

    private static final long serialVersionUID = -353117773175002640L;

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

  static final class ExprReversed implements Comparator<IExpr>, Serializable {
    private static final long serialVersionUID = 6629821962352737882L;

    Comparator<IExpr> comparator = new ExprComparator();

    public ExprReversed(Comparator<IExpr> comparator) {
      this.comparator = comparator;
    }

    /**
     * Compares an expression with another expression for order. Returns a negative integer, zero,
     * or a positive integer if this expression is canonical greater than, equal to, or less than
     * the specified expression.
     */
    @Override
    public int compare(final IExpr o1, final IExpr o2) {
      return comparator.compare(o2, o1);
    }
  }

  /**
   * Compares an expression with another expression for order. Returns a negative integer, zero, or
   * a positive integer if this expression is canonical less than, equal to, or greater than the
   * specified expression.
   */
  public static final ExprComparator CANONICAL_COMPARATOR = new ExprComparator();

  /**
   * Reverse {@link #CANONICAL_COMPARATOR}; Compares an expression with another expression for
   * order. Returns a negative integer, zero, or a positive integer if this expression is canonical
   * greater than, equal to, or less than the specified expression.
   */
  public static final ExprReverseComparator REVERSE_CANONICAL_COMPARATOR =
      new ExprReverseComparator();

  /**
   * Compares the elements of two {@link IAST}s (on the first level only) until the minimum size of
   * the {@link IAST}s, before considering their sequence lengths.
   */
  public static final ExprLexicalComparator LEXICAL_COMPARATOR = new ExprLexicalComparator();


  /**
   * Compare the two {@link Complex#norm()} <code>double</code> values of the numbers. If the
   * <code>norm()</code> values are approximately equal, use
   * {@link ComplexNum#compare(Complex, Complex)} as the return value.
   */
  public static final ComplexNormReverseComparator COMPLEX_NORM_REVERSE_COMPARATOR =
      new ComplexNormReverseComparator();

  public static Comparator<IExpr> reversedComparator(Comparator<IExpr> comparator) {
    return new ExprReversed(comparator);
  }

  /**
   * Comparator for using an internal {@link BiPredicate} from
   * {@link Predicates#isBinaryTrue(IExpr)} as a test for equality. If not equal call the
   * <code>compareTo()
   * </code> method from <code>IExpr</code>.
   *
   * @param test the test header. See for example function {@link ListFunctions.Gather}
   * @return
   */
  public static Comparator<IExpr> binaryPredicateComparator(IExpr test) {

    return new BinaryEqualsComparator(test);
  }

  private Comparators() {}
}
