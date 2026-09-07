package org.matheclipse.tools.units;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Exact scale factor of a unit: {@code rational * Pi^piExp * PROD base_i^exp_i} where the radical
 * terms carry non-integral exponents (needed for pint's Gaussian units like
 * {@code franklin = erg**0.5 * centimeter**0.5} and constants like the fine-structure constant).
 * The {@link #render()} form is a Symja-parseable input string.
 */
public final class Coefficient {
  public static final Coefficient ONE =
      new Coefficient(BigRational.ONE, BigRational.ZERO, new TreeMap<>());

  private final BigRational rat;
  private final BigRational piExp;
  private final TreeMap<BigRational, BigRational> radicals; // base (>0, non-one) -> fractional exp

  private Coefficient(BigRational rat, BigRational piExp,
      TreeMap<BigRational, BigRational> radicals) {
    this.rat = rat;
    this.piExp = piExp;
    this.radicals = radicals;
  }

  public static Coefficient of(BigRational r) {
    return new Coefficient(r, BigRational.ZERO, new TreeMap<>());
  }

  public static Coefficient pi() {
    return new Coefficient(BigRational.ONE, BigRational.ONE, new TreeMap<>());
  }

  public boolean isRational() {
    return piExp.isZero() && radicals.isEmpty();
  }

  public BigRational rationalValue() {
    if (!isRational()) {
      throw new ArithmeticException("not a plain rational: " + render());
    }
    return rat;
  }

  public boolean isOne() {
    return rat.isOne() && isRational();
  }

  public boolean isZero() {
    return rat.isZero();
  }

  public Coefficient multiply(Coefficient o) {
    BigRational r = rat.multiply(o.rat);
    BigRational p = piExp.add(o.piExp);
    TreeMap<BigRational, BigRational> rad = new TreeMap<>(radicals);
    Coefficient result = new Coefficient(r, p, rad);
    for (Map.Entry<BigRational, BigRational> e : o.radicals.entrySet()) {
      result = result.timesRadical(e.getKey(), e.getValue());
    }
    return result;
  }

  public Coefficient divide(Coefficient o) {
    return multiply(o.pow(BigRational.of(-1)));
  }

  public Coefficient pow(BigRational e) {
    if (e.isZero()) {
      return ONE;
    }
    if (isOne()) {
      return ONE;
    }
    BigRational p = piExp.multiply(e);
    Coefficient result = new Coefficient(BigRational.ONE, p, new TreeMap<>());
    // rational part
    if (e.isInteger()) {
      result = new Coefficient(rat.pow(e.intValueExact()), result.piExp, result.radicals);
    } else {
      BigRational exact = rat.tryRootPow(e);
      if (exact != null) {
        result = new Coefficient(exact, result.piExp, result.radicals);
      } else {
        if (rat.signum() <= 0) {
          throw new ArithmeticException(
              "cannot raise non-positive rational " + rat + " to power " + e);
        }
        result = result.timesRadical(rat, e);
      }
    }
    // existing radicals
    for (Map.Entry<BigRational, BigRational> r : radicals.entrySet()) {
      result = result.timesRadical(r.getKey(), r.getValue().multiply(e));
    }
    return result;
  }

  /** Multiplies by {@code base^exp}, folding into the rational part when the result is exact. */
  private Coefficient timesRadical(BigRational base, BigRational exp) {
    if (exp.isZero() || base.isOne()) {
      return this;
    }
    if (exp.isInteger()) {
      return new Coefficient(rat.multiply(base.pow(exp.intValueExact())), piExp, radicals);
    }
    BigRational exact = base.tryRootPow(exp);
    if (exact != null) {
      return new Coefficient(rat.multiply(exact), piExp, radicals);
    }
    TreeMap<BigRational, BigRational> rad = new TreeMap<>(radicals);
    BigRational existing = rad.get(base);
    BigRational merged = existing == null ? exp : existing.add(exp);
    rad.remove(base);
    Coefficient result = new Coefficient(rat, piExp, rad);
    if (!merged.isZero()) {
      if (merged.isInteger()) {
        result = new Coefficient(result.rat.multiply(base.pow(merged.intValueExact())),
            result.piExp, result.radicals);
      } else {
        result.radicals.put(base, merged);
      }
    }
    return result;
  }

  private static String exponentString(BigRational e) {
    if (e.isInteger() && e.signum() > 0 && e.compareTo(BigRational.of(10)) < 0) {
      return e.toString();
    }
    return "(" + e + ")";
  }

  /** Renders a Symja-parseable exact input string, e.g. {@code 381/1250} or {@code Pi/180}. */
  public String render() {
    StringBuilder b = new StringBuilder();
    boolean needsRat = !rat.isOne() || (piExp.isZero() && radicals.isEmpty());
    if (needsRat) {
      b.append(rat);
    }
    if (!piExp.isZero()) {
      if (b.length() > 0) {
        b.append('*');
      }
      if (piExp.isOne()) {
        b.append("Pi");
      } else {
        b.append("Pi^").append(exponentString(piExp));
      }
    }
    for (Map.Entry<BigRational, BigRational> r : radicals.entrySet()) {
      if (b.length() > 0) {
        b.append('*');
      }
      b.append('(').append(r.getKey()).append(")^").append(exponentString(r.getValue()));
    }
    return b.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Coefficient)) {
      return false;
    }
    Coefficient c = (Coefficient) o;
    return rat.equals(c.rat) && piExp.equals(c.piExp) && radicals.equals(c.radicals);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rat, piExp, radicals);
  }

  @Override
  public String toString() {
    return render();
  }
}
