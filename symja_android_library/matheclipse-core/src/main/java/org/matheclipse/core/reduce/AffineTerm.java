package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IRational;

/**
 * An immutable affine term <code>constant + &Sigma; coefficient_i * variable_i</code> with exact
 * rational coefficients and a deterministic variable order.
 *
 * <p>
 * A zero coefficient is never stored, so two terms are equal exactly when they denote the same
 * function.
 */
public final class AffineTerm implements Comparable<AffineTerm> {

  /** The constant term <code>0</code>. */
  public static final AffineTerm ZERO = new AffineTerm(F.C0, new TreeMap<Variable, IRational>());

  private final IRational constant;

  private final SortedMap<Variable, IRational> coefficients;

  private AffineTerm(IRational constant, SortedMap<Variable, IRational> coefficients) {
    this.constant = constant;
    this.coefficients = coefficients;
  }

  private static AffineTerm create(IRational constant, SortedMap<Variable, IRational> coefficients) {
    Iterator<Map.Entry<Variable, IRational>> iterator = coefficients.entrySet().iterator();
    while (iterator.hasNext()) {
      if (iterator.next().getValue().isZero()) {
        iterator.remove();
      }
    }
    return new AffineTerm(constant, coefficients);
  }

  public static AffineTerm constant(IRational value) {
    return value.isZero() ? ZERO : new AffineTerm(value, new TreeMap<Variable, IRational>());
  }

  public static AffineTerm integer(BigInteger value) {
    return constant(F.ZZ(value));
  }

  public static AffineTerm variable(Variable variable) {
    TreeMap<Variable, IRational> coefficients = new TreeMap<Variable, IRational>();
    coefficients.put(variable, F.C1);
    return new AffineTerm(F.C0, coefficients);
  }

  public IRational constant() {
    return constant;
  }

  public SortedMap<Variable, IRational> coefficients() {
    return Collections.unmodifiableSortedMap(coefficients);
  }

  public Set<Variable> variables() {
    return coefficients().keySet();
  }

  public IRational coefficient(Variable variable) {
    IRational value = coefficients.get(variable);
    return value == null ? F.C0 : value;
  }

  public boolean isConstant() {
    return coefficients.isEmpty();
  }

  public boolean isZero() {
    return coefficients.isEmpty() && constant.isZero();
  }

  /** Test whether the constant and every coefficient is an integer. */
  public boolean isIntegral() {
    if (!constant.isInteger()) {
      return false;
    }
    for (IRational coefficient : coefficients.values()) {
      if (!coefficient.isInteger()) {
        return false;
      }
    }
    return true;
  }

  public AffineTerm add(AffineTerm that) {
    TreeMap<Variable, IRational> sum = new TreeMap<Variable, IRational>(this.coefficients);
    for (Map.Entry<Variable, IRational> entry : that.coefficients.entrySet()) {
      IRational existing = sum.get(entry.getKey());
      sum.put(entry.getKey(),
          existing == null ? entry.getValue() : existing.add(entry.getValue()));
    }
    return create(this.constant.add(that.constant), sum);
  }

  public AffineTerm subtract(AffineTerm that) {
    return add(that.negate());
  }

  public AffineTerm negate() {
    return scale(F.CN1);
  }

  public AffineTerm scale(IRational factor) {
    if (factor.isZero()) {
      return ZERO;
    }
    if (factor.isOne()) {
      return this;
    }
    TreeMap<Variable, IRational> scaled = new TreeMap<Variable, IRational>();
    for (Map.Entry<Variable, IRational> entry : coefficients.entrySet()) {
      scaled.put(entry.getKey(), entry.getValue().multiply(factor));
    }
    return create(constant.multiply(factor), scaled);
  }

  /**
   * The product of two affine terms, or <code>null</code> if both factors contain a variable: that
   * product is not affine and must reject the whole lowering rather than be approximated.
   */
  public AffineTerm checkedMultiply(AffineTerm that) {
    if (this.isConstant()) {
      return that.scale(this.constant);
    }
    if (that.isConstant()) {
      return this.scale(that.constant);
    }
    return null;
  }

  /** The quotient by a non zero constant, or <code>null</code> if the divisor isn't one. */
  public AffineTerm checkedDivide(AffineTerm that) {
    if (!that.isConstant() || that.constant.isZero()) {
      return null;
    }
    return scale(that.constant.inverse());
  }

  /** Replace every occurrence of <code>variable</code> by <code>replacement</code>. */
  public AffineTerm substitute(Variable variable, AffineTerm replacement) {
    IRational coefficient = coefficients.get(variable);
    if (coefficient == null) {
      return this;
    }
    TreeMap<Variable, IRational> remaining = new TreeMap<Variable, IRational>(coefficients);
    remaining.remove(variable);
    return new AffineTerm(constant, remaining).add(replacement.scale(coefficient));
  }

  /**
   * The positive least common multiple of the denominators of the constant and of every
   * coefficient. Multiplying by it makes the term integral.
   */
  public BigInteger denominatorLcm() {
    BigInteger common = constant.denominator().toBigNumerator();
    for (IRational coefficient : coefficients.values()) {
      common = IntegerMath.lcm(common, coefficient.denominator().toBigNumerator());
    }
    return common.signum() == 0 ? BigInteger.ONE : common.abs();
  }

  /**
   * The greatest common divisor of the (integral) variable coefficients, or <code>null</code> if
   * the term is constant or not integral.
   */
  public BigInteger coefficientGcd() {
    if (coefficients.isEmpty() || !isIntegral()) {
      return null;
    }
    BigInteger common = BigInteger.ZERO;
    for (IRational coefficient : coefficients.values()) {
      common = IntegerMath.gcd(common, coefficient.numerator().toBigNumerator());
    }
    return common;
  }

  @Override
  public int compareTo(AffineTerm that) {
    int bySize = Integer.compare(this.coefficients.size(), that.coefficients.size());
    if (bySize != 0) {
      return bySize;
    }
    Iterator<Map.Entry<Variable, IRational>> left = this.coefficients.entrySet().iterator();
    Iterator<Map.Entry<Variable, IRational>> right = that.coefficients.entrySet().iterator();
    while (left.hasNext() && right.hasNext()) {
      Map.Entry<Variable, IRational> leftEntry = left.next();
      Map.Entry<Variable, IRational> rightEntry = right.next();
      int byVariable = leftEntry.getKey().compareTo(rightEntry.getKey());
      if (byVariable != 0) {
        return byVariable;
      }
      int byCoefficient = leftEntry.getValue().compareTo(rightEntry.getValue());
      if (byCoefficient != 0) {
        return byCoefficient;
      }
    }
    return this.constant.compareTo(that.constant);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof AffineTerm)) {
      return false;
    }
    AffineTerm that = (AffineTerm) object;
    return this.constant.equals(that.constant) && this.coefficients.equals(that.coefficients);
  }

  @Override
  public int hashCode() {
    return 31 * constant.hashCode() + coefficients.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<Variable, IRational> entry : coefficients.entrySet()) {
      if (builder.length() > 0) {
        builder.append(" + ");
      }
      builder.append(entry.getValue()).append("*").append(entry.getKey());
    }
    if (!constant.isZero() || builder.length() == 0) {
      if (builder.length() > 0) {
        builder.append(" + ");
      }
      builder.append(constant);
    }
    return builder.toString();
  }
}
