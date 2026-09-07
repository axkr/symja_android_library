package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.Set;

/**
 * An atom of the linear formula IR: either a relation <code>term REL 0</code> or a divisibility
 * <code>modulus | term</code> (negated: <code>modulus &#8740; term</code>).
 */
public final class Atom implements Comparable<Atom> {

  private final Relation relation;

  private final BigInteger modulus;

  private final AffineTerm term;

  private final boolean negated;

  private Atom(Relation relation, BigInteger modulus, AffineTerm term, boolean negated) {
    this.relation = relation;
    this.modulus = modulus;
    this.term = term;
    this.negated = negated;
  }

  /** The atom <code>term REL 0</code>. */
  public static Atom relation(Relation relation, AffineTerm term) {
    return new Atom(relation, null, term, false);
  }

  /**
   * The atom <code>modulus | term</code>.
   *
   * @return <code>null</code> if the modulus isn't positive or the term isn't integral, which
   *         rejects the whole lowering instead of rounding a coefficient
   */
  public static Atom divides(BigInteger modulus, AffineTerm term, boolean negated) {
    if (modulus == null || modulus.signum() <= 0 || !term.isIntegral()) {
      return null;
    }
    return new Atom(null, modulus, term, negated);
  }

  public boolean isRelation() {
    return relation != null;
  }

  public boolean isDivides() {
    return relation == null;
  }

  public Relation relation() {
    return relation;
  }

  public BigInteger modulus() {
    return modulus;
  }

  public AffineTerm term() {
    return term;
  }

  public boolean isNegated() {
    return negated;
  }

  public Atom negate() {
    return isRelation() ? new Atom(relation.negated(), null, term, false)
        : new Atom(null, modulus, term, !negated);
  }

  public Atom withTerm(AffineTerm replacement) {
    return new Atom(relation, modulus, replacement, negated);
  }

  public Atom substitute(Variable variable, AffineTerm replacement) {
    AffineTerm substituted = term.substitute(variable, replacement);
    return substituted.equals(term) ? this : withTerm(substituted);
  }

  public Set<Variable> variables() {
    return term.variables();
  }

  public boolean containsVariable(Variable variable) {
    return !term.coefficient(variable).isZero();
  }

  /**
   * The truth value of an atom without variables, or <code>null</code> when the atom still depends
   * on a variable.
   */
  public Boolean constantTruth() {
    if (!term.isConstant()) {
      return null;
    }
    if (isDivides()) {
      BigInteger value = term.constant().numerator().toBigNumerator();
      BigInteger denominator = term.constant().denominator().toBigNumerator();
      if (!denominator.equals(BigInteger.ONE)) {
        // a non integer is divisible by no positive modulus
        return Boolean.valueOf(negated);
      }
      boolean divides = IntegerMath.euclideanMod(value, modulus).signum() == 0;
      return Boolean.valueOf(negated ? !divides : divides);
    }
    int sign = term.constant().complexSign();
    switch (relation) {
      case EQUAL:
        return Boolean.valueOf(sign == 0);
      case NOT_EQUAL:
        return Boolean.valueOf(sign != 0);
      case LESS:
        return Boolean.valueOf(sign < 0);
      case LESS_EQUAL:
        return Boolean.valueOf(sign <= 0);
      case GREATER:
        return Boolean.valueOf(sign > 0);
      default:
        return Boolean.valueOf(sign >= 0);
    }
  }

  @Override
  public int compareTo(Atom that) {
    int byKind = Boolean.compare(this.isDivides(), that.isDivides());
    if (byKind != 0) {
      return byKind;
    }
    if (isRelation()) {
      int byRelation = this.relation.compareTo(that.relation);
      if (byRelation != 0) {
        return byRelation;
      }
    } else {
      int byModulus = this.modulus.compareTo(that.modulus);
      if (byModulus != 0) {
        return byModulus;
      }
      int byNegation = Boolean.compare(this.negated, that.negated);
      if (byNegation != 0) {
        return byNegation;
      }
    }
    return this.term.compareTo(that.term);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof Atom)) {
      return false;
    }
    Atom that = (Atom) object;
    return this.relation == that.relation && this.negated == that.negated
        && (this.modulus == null ? that.modulus == null : this.modulus.equals(that.modulus))
        && this.term.equals(that.term);
  }

  @Override
  public int hashCode() {
    int hash = term.hashCode();
    hash = 31 * hash + (relation == null ? 0 : relation.hashCode());
    hash = 31 * hash + (modulus == null ? 0 : modulus.hashCode());
    return 31 * hash + (negated ? 1 : 0);
  }

  @Override
  public String toString() {
    if (isRelation()) {
      return "(" + term + ") " + relation + " 0";
    }
    return modulus + (negated ? " !| " : " | ") + "(" + term + ")";
  }
}
