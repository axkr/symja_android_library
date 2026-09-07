package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.matheclipse.core.interfaces.IRational;

/**
 * Proves bounds for the variables of a conjunction of integer inequalities.
 *
 * <p>
 * The bounds are a proof, not a search window. A variable this class leaves unbounded is one whose
 * range the constraints do not pin down, and enumerating it anyway inside an arbitrary interval is
 * exactly how a solver reports that a system has no solution when the solutions lie outside that
 * interval.
 */
public final class BoundProver {

  /**
   * Bounds for a set of variables. A <code>null</code> bound means the constraints do not bound
   * that variable on that side.
   */
  public static final class Box {

    private final Map<Variable, BigInteger> lower = new TreeMap<Variable, BigInteger>();

    private final Map<Variable, BigInteger> upper = new TreeMap<Variable, BigInteger>();

    private boolean infeasible;

    public BigInteger lower(Variable variable) {
      return lower.get(variable);
    }

    public BigInteger upper(Variable variable) {
      return upper.get(variable);
    }

    public boolean isInfeasible() {
      return infeasible;
    }

    public boolean isBounded(Variable variable) {
      return lower.containsKey(variable) && upper.containsKey(variable);
    }

    /** The number of integers between the bounds, or <code>null</code> if unbounded. */
    public BigInteger size(Variable variable) {
      if (!isBounded(variable)) {
        return null;
      }
      BigInteger count = upper.get(variable).subtract(lower.get(variable)).add(BigInteger.ONE);
      return count.signum() <= 0 ? BigInteger.ZERO : count;
    }

    boolean tightenLower(Variable variable, BigInteger value) {
      BigInteger current = lower.get(variable);
      if (current != null && current.compareTo(value) >= 0) {
        return false;
      }
      lower.put(variable, value);
      return true;
    }

    boolean tightenUpper(Variable variable, BigInteger value) {
      BigInteger current = upper.get(variable);
      if (current != null && current.compareTo(value) <= 0) {
        return false;
      }
      upper.put(variable, value);
      return true;
    }

    void checkEmpty() {
      for (Map.Entry<Variable, BigInteger> entry : lower.entrySet()) {
        BigInteger top = upper.get(entry.getKey());
        if (top != null && entry.getValue().compareTo(top) > 0) {
          infeasible = true;
          return;
        }
      }
    }
  }

  private BoundProver() {}

  /**
   * Propagate the bounds of a conjunction of atoms to a fixpoint.
   *
   * <p>
   * Each atom <code>a*v + rest &lt;= 0</code> bounds <code>v</code> as soon as the extremum of
   * <code>rest</code> is known, and every bound found makes the next extremum tighter, so the
   * process is monotone. It is iterated until nothing changes, with a cap on the number of rounds:
   * a cyclic system such as <code>y &gt;= x + 1 &amp;&amp; x &gt;= y + 1</code> tightens forever
   * without the cap, and is infeasible rather than unbounded.
   *
   * @param atoms the atoms of one conjunction, normalized to <code>term &lt;= 0</code>
   * @param variables the variables to bound
   * @param domain the domain, whose own lower bound seeds the propagation: over the primes every
   *        variable is at least two, which is often what makes a system finite at all
   */
  public static Box bounds(List<Atom> atoms, List<Variable> variables, IntegerDomain domain) {
    Box box = new Box();
    BigInteger domainLower = domain == null ? null : domain.lowerBound();
    if (domainLower != null) {
      for (Variable variable : variables) {
        box.tightenLower(variable, domainLower);
      }
    }
    final int rounds = 4 * (variables.size() + atoms.size()) + 8;
    for (int round = 0; round < rounds; round++) {
      boolean changed = false;
      for (Atom atom : atoms) {
        if (!atom.isRelation() || atom.relation() != Relation.LESS_EQUAL
            || !atom.term().isIntegral()) {
          continue;
        }
        for (Variable variable : variables) {
          BigInteger coefficient =
              atom.term().coefficient(variable).numerator().toBigNumerator();
          if (coefficient.signum() == 0) {
            continue;
          }
          BigInteger minimumRest = minimumRest(atom.term(), variable, box);
          if (minimumRest == null) {
            continue;
          }
          if (coefficient.signum() > 0) {
            changed |= box.tightenUpper(variable,
                IntegerMath.floorDiv(minimumRest.negate(), coefficient));
          } else {
            changed |= box.tightenLower(variable,
                IntegerMath.ceilDiv(minimumRest.negate(), coefficient));
          }
        }
      }
      box.checkEmpty();
      if (box.isInfeasible()) {
        return box;
      }
      if (!changed) {
        break;
      }
    }
    box.checkEmpty();
    if (box.isInfeasible() || isFullyBounded(box, variables)) {
      return box;
    }
    // Propagation reads one atom at a time, so it cannot see a bound which only follows from a
    // combination of several. Project the other variables away instead, which finds every bound
    // the constraints imply.
    projectBounds(atoms, variables, box);
    box.checkEmpty();
    return box;
  }

  private static boolean isFullyBounded(Box box, List<Variable> variables) {
    for (Variable variable : variables) {
      if (!box.isBounded(variable)) {
        return false;
      }
    }
    return true;
  }

  /** Maximum number of variables a projection is attempted for. */
  private static final int MAX_PROJECTION_VARIABLES = 8;

  /** Maximum number of inequalities one projection step may produce. */
  private static final int MAX_PROJECTION_ATOMS = 2000;

  /**
   * Fourier-Motzkin projection: for each variable which is still unbounded, eliminate every other
   * variable and read the bound off what remains.
   *
   * <p>
   * Divisibility atoms are ignored here. Dropping a constraint can only enlarge the solution set,
   * so a bound proved without them still holds with them.
   */
  private static void projectBounds(List<Atom> atoms, List<Variable> variables, Box box) {
    if (variables.size() > MAX_PROJECTION_VARIABLES) {
      return;
    }
    List<AffineTerm> inequalities = new ArrayList<AffineTerm>(atoms.size());
    for (Atom atom : atoms) {
      if (atom.isRelation() && atom.relation() == Relation.LESS_EQUAL) {
        inequalities.add(atom.term());
      }
    }
    if (inequalities.isEmpty() || inequalities.size() > MAX_PROJECTION_ATOMS) {
      return;
    }
    for (Variable target : variables) {
      if (box.isBounded(target)) {
        continue;
      }
      List<AffineTerm> remaining = inequalities;
      for (Variable other : variables) {
        if (other.equals(target)) {
          continue;
        }
        remaining = project(remaining, other);
        if (remaining == null) {
          break;
        }
      }
      if (remaining == null) {
        continue;
      }
      readBounds(remaining, target, box);
      if (box.isInfeasible()) {
        return;
      }
    }
  }

  /** Eliminate one variable from a conjunction of <code>term &lt;= 0</code> inequalities. */
  private static List<AffineTerm> project(List<AffineTerm> terms, Variable variable) {
    List<AffineTerm> independent = new ArrayList<AffineTerm>();
    List<AffineTerm> positive = new ArrayList<AffineTerm>();
    List<AffineTerm> negative = new ArrayList<AffineTerm>();
    for (AffineTerm term : terms) {
      IRational coefficient = term.coefficient(variable);
      if (coefficient.isZero()) {
        independent.add(term);
      } else if (coefficient.complexSign() > 0) {
        positive.add(term);
      } else {
        negative.add(term);
      }
    }
    if (positive.isEmpty() || negative.isEmpty()) {
      // the variable is unbounded on one side, so it constrains nothing once eliminated
      return independent;
    }
    if ((long) positive.size() * negative.size() + independent.size() > MAX_PROJECTION_ATOMS) {
      return null;
    }
    List<AffineTerm> projected = new ArrayList<AffineTerm>(independent);
    for (AffineTerm upper : positive) {
      IRational upperCoefficient = upper.coefficient(variable);
      for (AffineTerm lower : negative) {
        IRational lowerCoefficient = lower.coefficient(variable);
        // scale both so the variable cancels, which keeps both inequalities pointing the same way
        AffineTerm combined = upper.scale((IRational) lowerCoefficient.negate())
            .add(lower.scale(upperCoefficient));
        if (combined.isConstant()) {
          if (combined.constant().complexSign() > 0) {
            // a constant which is positive contradicts `<= 0`
            return null;
          }
          continue;
        }
        if (!projected.contains(combined)) {
          projected.add(combined);
        }
      }
    }
    return projected;
  }

  /** Read the bounds of a variable off inequalities which contain no other variable. */
  private static void readBounds(List<AffineTerm> terms, Variable variable, Box box) {
    for (AffineTerm term : terms) {
      IRational coefficient = term.coefficient(variable);
      if (coefficient.isZero()) {
        if (term.isConstant() && term.constant().complexSign() > 0) {
          box.infeasible = true;
        }
        continue;
      }
      if (term.coefficients().size() != 1) {
        continue;
      }
      IRational boundary = (IRational) term.constant().negate().divideBy(coefficient);
      if (coefficient.complexSign() > 0) {
        box.tightenUpper(variable, boundary.floor().toBigNumerator());
      } else {
        box.tightenLower(variable, boundary.ceil().toBigNumerator());
      }
    }
  }

  /**
   * The smallest value the term can take with the given variable omitted, or <code>null</code> if
   * another variable is unbounded on the side which decides that minimum.
   */
  private static BigInteger minimumRest(AffineTerm term, Variable omitted, Box box) {
    BigInteger value = term.constant().numerator().toBigNumerator();
    for (Map.Entry<Variable, org.matheclipse.core.interfaces.IRational> entry : term.coefficients()
        .entrySet()) {
      if (entry.getKey().equals(omitted)) {
        continue;
      }
      BigInteger coefficient = entry.getValue().numerator().toBigNumerator();
      if (coefficient.signum() == 0) {
        continue;
      }
      BigInteger extremum =
          coefficient.signum() > 0 ? box.lower(entry.getKey()) : box.upper(entry.getKey());
      if (extremum == null) {
        return null;
      }
      value = value.add(coefficient.multiply(extremum));
    }
    return value;
  }
}
