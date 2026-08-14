package org.matheclipse.core.eval.util;

import java.util.function.IntPredicate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A specification of one machine-sized integer per level of a nested expression. Such a
 * specification is either a single integer <code>n</code>, which specifies level <code>1</code>, or
 * a list of integers <code>{n1, n2, ...}</code>, where <code>ni</code> specifies level <code>i
 * </code>.
 *
 * <p>
 * It's used for arguments of list functions like <code>RotateLeft(list, {n1, n2})</code>, <code>
 * RotateRight(list, {n1, n2})</code>, <code>PadLeft(list, {n1, n2})</code>, <code>
 * PadRight(list, {n1, n2})</code> or <code>Partition(list, {n1, n2}, {d1, d2})</code>.
 *
 * <p>
 * Note: although the syntax looks the same, this is not a <i>level specification</i> in the sense
 * of {@link LevelSpec}, {@link LevelSpecification} or
 * {@link org.matheclipse.core.visit.VisitorLevelSpecification}, which are used by functions like
 * <code>Level, Map, Apply, Cases</code>. There <code>{n1, n2}</code> denotes the <i>range</i> of
 * levels from <code>n1</code> to <code>n2</code> on which a single operation is applied, whereas
 * here <code>{n1, n2}</code> assigns an own value to each of the levels <code>1</code> and <code>2
 * </code>.
 */
public final class PerLevelIntSpec {

  private final int[] values;

  private final boolean scalar;

  private PerLevelIntSpec(int[] values, boolean scalar) {
    this.values = values;
    this.scalar = scalar;
  }

  /**
   * Create a specification from a single integer or a list of integers.
   *
   * @param specification a machine-sized integer or a list of machine-sized integers
   * @return <code>null</code> if <code>specification</code> isn't a machine-sized integer or a list
   *         of machine-sized integers
   */
  public static PerLevelIntSpec create(IExpr specification) {
    if (specification.isList()) {
      IAST list = (IAST) specification;
      int[] values = new int[list.argSize()];
      for (int i = 0; i < values.length; i++) {
        values[i] = list.get(i + 1).toMachineInt();
        if (F.isNotPresent(values[i])) {
          return null;
        }
      }
      return new PerLevelIntSpec(values, false);
    }
    int value = specification.toMachineInt();
    if (F.isNotPresent(value)) {
      return null;
    }
    return new PerLevelIntSpec(new int[] {value}, true);
  }


  /** The number of levels this specification defines. */
  public int levels() {
    return values.length;
  }

  /**
   * Return <code>true</code> if this specification was created from a single integer instead of a
   * list of integers. Such a specification defines level <code>1</code>, but some functions
   * broadcast it to every level; see {@link #toArray(int, int)}.
   */
  public boolean isScalar() {
    return scalar;
  }

  /**
   * The value for the (one-based) <code>level</code>.
   *
   * @param level must be in the range <code>1 <= level <= levels()</code>
   */
  public int get(int level) {
    return values[level - 1];
  }

  /**
   * The value for the (one-based) <code>level</code> or <code>defaultValue</code> if this
   * specification doesn't define that level.
   */
  public int get(int level, int defaultValue) {
    return (level >= 1 && level <= values.length) ? values[level - 1] : defaultValue;
  }

  /** Return <code>true</code> if all specified values fulfill the <code>predicate</code>. */
  public boolean allMatch(IntPredicate predicate) {
    for (int i = 0; i < values.length; i++) {
      if (!predicate.test(values[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * The specified values as an array, with the value for level <code>i</code> at index <code>i-1
    * </code>.
   */
  public int[] toArray() {
    return values.clone();
  }

  /**
   * The specified values expanded to exactly <code>levels</code> entries. A specification created
   * from a single integer is broadcast to every level, a specification created from a list fills
   * the levels it doesn't define with <code>defaultValue</code> and ignores surplus values.
   *
   * @param levels the number of levels of the returned array
   * @param defaultValue used for the levels which aren't defined by a list specification
   */
  public int[] toArray(int levels, int defaultValue) {
    int[] result = new int[levels];
    for (int i = 0; i < levels; i++) {
      result[i] = scalar ? values[0] : (i < values.length ? values[i] : defaultValue);
    }
    return result;
  }
}
