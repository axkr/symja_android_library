package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IRational;

/**
 * Integer normal form of a formula.
 *
 * <p>
 * Over the integers a strict inequality is a non strict one on the next integer, so every ordered
 * atom can be written as <code>term &lt;= 0</code> with integral coefficients. Dividing such an atom
 * by the greatest common divisor of its coefficients and rounding the constant is the step which
 * makes <code>2*x &lt;= 5</code> and <code>x &lt;= 2</code> the same atom.
 */
public final class IntegerNormalizer {

  private IntegerNormalizer() {}

  /**
   * Normalize a formula for the integer decision procedures.
   *
   * @param formula the formula, in any shape
   * @param keepEqualities <code>true</code> to keep <code>EQUAL</code> atoms, which the lattice
   *        solver needs; <code>false</code> to split them into two inequalities, which Cooper's
   *        elimination needs
   * @return the normalized formula, or <code>null</code> if an atom cannot be made integral
   */
  public static Formula normalize(Formula formula, boolean keepEqualities) {
    Formula normalized = rewrite(formula.nnf(), keepEqualities);
    return normalized == null ? null : normalized.normalized();
  }

  private static Formula rewrite(Formula formula, boolean keepEqualities) {
    switch (formula.kind()) {
      case TRUE:
      case FALSE:
        return formula;
      case ATOM:
        return rewriteAtom(formula.atom(), keepEqualities);
      case AND:
      case OR: {
        List<Formula> children = new ArrayList<Formula>(formula.children().size());
        for (Formula child : formula.children()) {
          Formula rewritten = rewrite(child, keepEqualities);
          if (rewritten == null) {
            return null;
          }
          children.add(rewritten);
        }
        return formula.kind() == Formula.Kind.AND ? Formula.and(children) : Formula.or(children);
      }
      case EXISTS:
      case FORALL: {
        Formula body = rewrite(formula.body(), keepEqualities);
        return body == null ? null
            : Formula.quantified(formula.kind(), formula.boundVariables(), body);
      }
      default:
        // the input is in negation normal form, so no Not can sit above a compound formula
        return null;
    }
  }

  private static Formula rewriteAtom(Atom atom, boolean keepEqualities) {
    if (atom.isDivides()) {
      return rewriteDivides(atom);
    }
    AffineTerm term = clearDenominators(atom.term());
    AffineTerm negated = term.negate();
    AffineTerm one = AffineTerm.constant(F.C1);
    switch (atom.relation()) {
      case EQUAL: {
        // tighten as an equality first: a constant which the coefficient divisor does not divide
        // proves the atom false, which the two inequalities alone would only imply
        Formula tightened = tighten(Relation.EQUAL, term);
        if (keepEqualities || tightened.kind() != Formula.Kind.ATOM) {
          return tightened;
        }
        AffineTerm equality = tightened.atom().term();
        return Formula.and(tighten(Relation.LESS_EQUAL, equality),
            tighten(Relation.LESS_EQUAL, equality.negate()));
      }
      case NOT_EQUAL: {
        Formula tightened = tighten(Relation.EQUAL, term);
        if (tightened.isFalse()) {
          // the equality is unreachable, so the disequality holds everywhere
          return Formula.TRUE;
        }
        return Formula.or(tighten(Relation.LESS_EQUAL, term.add(one)),
            tighten(Relation.LESS_EQUAL, negated.add(one)));
      }
      case LESS:
        return tighten(Relation.LESS_EQUAL, term.add(one));
      case LESS_EQUAL:
        return tighten(Relation.LESS_EQUAL, term);
      case GREATER:
        return tighten(Relation.LESS_EQUAL, negated.add(one));
      default:
        return tighten(Relation.LESS_EQUAL, negated);
    }
  }

  /** Multiply by the least common multiple of the denominators, which preserves the relation. */
  private static AffineTerm clearDenominators(AffineTerm term) {
    BigInteger common = term.denominatorLcm();
    return common.equals(BigInteger.ONE) ? term : term.scale(F.ZZ(common));
  }

  /**
   * Divide an integral atom by the greatest common divisor of its variable coefficients.
   *
   * <p>
   * For <code>term &lt;= 0</code> the constant is rounded up, which is the integer strengthening:
   * <code>2*x + 5 &lt;= 0</code> becomes <code>x + 3 &lt;= 0</code>. For an equality a constant
   * which the divisor does not divide proves the atom false.
   */
  private static Formula tighten(Relation relation, AffineTerm term) {
    BigInteger divisor = term.coefficientGcd();
    if (divisor == null || divisor.signum() == 0 || divisor.equals(BigInteger.ONE)) {
      return Formula.atom(Atom.relation(relation, term));
    }
    BigInteger constant = term.constant().numerator().toBigNumerator();
    AffineTerm scaled = term.scale(F.QQ(BigInteger.ONE, divisor));
    if (relation == Relation.EQUAL) {
      if (IntegerMath.euclideanMod(constant, divisor).signum() != 0) {
        return Formula.FALSE;
      }
      return Formula.atom(Atom.relation(Relation.EQUAL, scaled));
    }
    AffineTerm rounded = scaled.subtract(AffineTerm.constant(scaled.constant()))
        .add(AffineTerm.integer(IntegerMath.ceilDiv(constant, divisor)));
    return Formula.atom(Atom.relation(relation, rounded));
  }

  /**
   * Reduce a divisibility atom by the common divisor of its coefficients and its modulus, and put
   * its constant into <code>[0, modulus)</code>.
   */
  private static Formula rewriteDivides(Atom atom) {
    AffineTerm term = atom.term();
    if (!term.isIntegral()) {
      return null;
    }
    BigInteger modulus = atom.modulus();
    BigInteger divisor = term.coefficientGcd();
    if (divisor == null) {
      divisor = BigInteger.ZERO;
    }
    divisor = IntegerMath.gcd(divisor, modulus);
    BigInteger constant = term.constant().numerator().toBigNumerator();
    if (divisor.signum() != 0 && !divisor.equals(BigInteger.ONE)) {
      if (IntegerMath.euclideanMod(constant, divisor).signum() != 0) {
        // no value of the term is divisible by the modulus
        return Formula.of(atom.isNegated());
      }
      IRational inverse = F.QQ(BigInteger.ONE, divisor);
      term = term.scale(inverse);
      modulus = modulus.divide(divisor);
      constant = constant.divide(divisor);
    }
    if (modulus.equals(BigInteger.ONE)) {
      return Formula.of(!atom.isNegated());
    }
    AffineTerm variablePart = term.subtract(AffineTerm.constant(term.constant()));
    if (variablePart.coefficients().size() == 1) {
      // one variable: solve the congruence so the coefficient becomes 1 and the residue canonical
      Variable variable = variablePart.variables().iterator().next();
      BigInteger coefficient = variablePart.coefficient(variable).numerator().toBigNumerator();
      BigInteger[] solved =
          IntegerMath.solveLinearCongruence(coefficient, constant.negate(), modulus);
      if (solved == null) {
        return Formula.of(atom.isNegated());
      }
      if (solved[1].equals(BigInteger.ONE)) {
        return Formula.of(!atom.isNegated());
      }
      AffineTerm canonical = AffineTerm.variable(variable)
          .add(AffineTerm.integer(IntegerMath.euclideanMod(solved[0].negate(), solved[1])));
      return Formula.atom(Atom.divides(solved[1], canonical, atom.isNegated()));
    }
    BigInteger residue = IntegerMath.euclideanMod(constant, modulus);
    AffineTerm canonical = variablePart.add(AffineTerm.integer(residue));
    return Formula.atom(Atom.divides(modulus, canonical, atom.isNegated()));
  }
}
