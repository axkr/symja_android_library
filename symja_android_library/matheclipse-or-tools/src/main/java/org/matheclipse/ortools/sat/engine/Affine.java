package org.matheclipse.ortools.sat.engine;

/**
 * An affine view {@code coeff * var + offset} of a variable of the Java engine which replaces the
 * C++ CP-SAT solver of OR-Tools; {@code var < 0} (or a zero coefficient) denotes the constant
 * {@code offset}.
 */
final class Affine {
  final int var;
  final long coeff;
  final long offset;

  Affine(int var, long coeff, long offset) {
    if (var < 0 || coeff == 0) {
      this.var = -1;
      this.coeff = 0;
    } else {
      this.var = var;
      this.coeff = coeff;
    }
    this.offset = offset;
  }

  static Affine constant(long value) {
    return new Affine(-1, 0, value);
  }

  boolean isConstant() {
    return var < 0;
  }

  long min(DomainStore s) {
    return s.affMin(var, coeff, offset);
  }

  long max(DomainStore s) {
    return s.affMax(var, coeff, offset);
  }

  boolean isFixed(DomainStore s) {
    return var < 0 || s.isFixed(var);
  }

  /** The value of a fixed view. */
  long value(DomainStore s) {
    return var < 0 ? offset : coeff * s.value(var) + offset;
  }

  /** The value for a given value of the variable. */
  long valueOf(long x) {
    return var < 0 ? offset : coeff * x + offset;
  }

  boolean setMin(DomainStore s, long x) {
    return s.affSetMin(var, coeff, offset, x);
  }

  boolean setMax(DomainStore s, long x) {
    return s.affSetMax(var, coeff, offset, x);
  }

  boolean contains(DomainStore s, long x) {
    return s.affContains(var, coeff, offset, x);
  }

  boolean remove(DomainStore s, long x) {
    return s.affRemove(var, coeff, offset, x);
  }

  boolean fix(DomainStore s, long x) {
    return s.affFix(var, coeff, offset, x);
  }

  /** Number of values (an upper bound for variables without bitset). */
  long size(DomainStore s) {
    return var < 0 ? 1 : s.size(var);
  }

  /** Smallest value of the view which is &gt;= x, or Long.MAX_VALUE. */
  long nextValueAtOrAfter(DomainStore s, long x) {
    if (var < 0) {
      return offset >= x ? offset : Long.MAX_VALUE;
    }
    if (coeff > 0) {
      long y = s.nextValueAtOrAfter(var, DomainStore.ceilDiv(x - offset, coeff));
      return y == Long.MAX_VALUE ? y : coeff * y + offset;
    }
    long y = s.prevValueAtOrBefore(var, DomainStore.floorDiv(x - offset, coeff));
    return y == Long.MIN_VALUE ? Long.MAX_VALUE : coeff * y + offset;
  }

  @Override
  public String toString() {
    return var < 0 ? Long.toString(offset) : coeff + "*v" + var + "+" + offset;
  }
}
