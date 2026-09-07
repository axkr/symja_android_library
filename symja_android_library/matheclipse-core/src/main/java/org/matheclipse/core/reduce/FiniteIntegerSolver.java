package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Every solution of a quantifier free integer formula whose variables the constraints bound.
 *
 * <p>
 * The enumeration only starts once every requested variable has been proved to lie in a finite
 * range, so the result is the complete solution set rather than the part of it which happened to
 * fall inside a search window. A formula whose models were not proved finite is reported as
 * {@link IntegerSolveResult.Kind#UNSUPPORTED}, never as an empty solution set.
 */
public final class FiniteIntegerSolver {

  /** Maximum number of conjunctions the disjunctive normal form may expand to. */
  public static final int MAX_BRANCHES = 4096;

  /** Maximum number of points the enumeration may visit. */
  public static final long MAX_POINTS = 1000000L;

  private FiniteIntegerSolver() {}

  /** Lexicographic order on solution tuples. */
  private static final Comparator<BigInteger[]> LEXICOGRAPHIC = (left, right) -> {
    for (int index = 0; index < left.length && index < right.length; index++) {
      int comparison = left[index].compareTo(right[index]);
      if (comparison != 0) {
        return comparison;
      }
    }
    return Integer.compare(left.length, right.length);
  };

  public static IntegerSolveResult solve(Formula formula, List<Variable> targets,
      IntegerDomain domain) {
    return solve(formula, targets, domain, MAX_POINTS);
  }

  /**
   * @param formula a quantifier free condition
   * @param targets the variables to solve for
   * @param domain the domain of the solutions
   * @param maxPoints the enumeration budget
   */
  public static IntegerSolveResult solve(Formula formula, List<Variable> targets,
      IntegerDomain domain, long maxPoints) {
    Formula normalized = IntegerNormalizer.normalize(formula, false);
    if (normalized == null) {
      return IntegerSolveResult.notApplicable();
    }
    if (normalized.isFalse()) {
      return IntegerSolveResult.infeasible();
    }
    if (normalized.isTrue()) {
      // every assignment is a solution, which is not a finite set
      return IntegerSolveResult.notApplicable();
    }
    Set<Variable> free = normalized.freeVariables();
    if (!targets.containsAll(free)) {
      // a parameter makes the solution set depend on its value
      return IntegerSolveResult.unsupported();
    }
    List<Variable> present = new ArrayList<Variable>(targets.size());
    List<Variable> untouched = new ArrayList<Variable>();
    for (Variable target : targets) {
      if (free.contains(target)) {
        present.add(target);
      } else {
        untouched.add(target);
      }
    }
    if (present.isEmpty()) {
      return IntegerSolveResult.notApplicable();
    }

    List<List<Atom>> branches = disjunctiveNormalForm(normalized);
    if (branches == null) {
      return IntegerSolveResult.unsupported();
    }
    TreeSet<BigInteger[]> solutions = new TreeSet<BigInteger[]>(LEXICOGRAPHIC);
    for (List<Atom> branch : branches) {
      Boolean enumerated = enumerateBranch(branch, present, domain, maxPoints, solutions);
      if (enumerated == null) {
        return IntegerSolveResult.unsupported();
      }
    }
    return IntegerSolveResult.finite(present, new ArrayList<BigInteger[]>(solutions), untouched);
  }

  /**
   * Enumerate one conjunction.
   *
   * @return <code>Boolean.TRUE</code> when the branch was decided, or <code>null</code> when its
   *         variables were not proved to lie in a finite range
   */
  private static Boolean enumerateBranch(List<Atom> atoms, List<Variable> variables,
      IntegerDomain domain, long maxPoints, TreeSet<BigInteger[]> output) {
    BoundProver.Box box = BoundProver.bounds(atoms, variables, domain);
    if (box.isInfeasible()) {
      return Boolean.TRUE;
    }
    List<Domain> domains = new ArrayList<Domain>(variables.size());
    BigInteger total = BigInteger.ONE;
    for (Variable variable : variables) {
      BigInteger lower = box.lower(variable);
      BigInteger upper = box.upper(variable);
      if (lower == null || upper == null) {
        return null;
      }
      Domain range = domainOf(variable, lower, upper, atoms);
      if (range == null) {
        // an inconsistent congruence leaves this branch without a solution
        return Boolean.TRUE;
      }
      domains.add(range);
      total = total.multiply(range.count);
      if (total.compareTo(BigInteger.valueOf(maxPoints)) > 0) {
        return null;
      }
    }
    enumerate(0, variables, domains, atoms, domain, new TreeMap<Variable, BigInteger>(), output);
    return Boolean.TRUE;
  }

  /** The values one variable may take: an arithmetic progression inside its bounds. */
  private static final class Domain {
    final BigInteger start;
    final BigInteger upper;
    final BigInteger step;
    final BigInteger count;

    Domain(BigInteger start, BigInteger upper, BigInteger step) {
      this.start = start;
      this.upper = upper;
      this.step = step;
      this.count = start.compareTo(upper) > 0 ? BigInteger.ZERO
          : upper.subtract(start).divide(step).add(BigInteger.ONE);
    }
  }

  /**
   * The enumeration domain of one variable. The strongest congruence on that variable alone
   * becomes the stride, which is a search reduction only: every congruence is still checked at
   * each point.
   */
  private static Domain domainOf(Variable variable, BigInteger lower, BigInteger upper,
      List<Atom> atoms) {
    BigInteger residue = BigInteger.ZERO;
    BigInteger modulus = BigInteger.ONE;
    for (Atom atom : atoms) {
      if (!atom.isDivides() || atom.isNegated() || !atom.term().isIntegral()) {
        continue;
      }
      AffineTerm variablePart = atom.term().subtract(AffineTerm.constant(atom.term().constant()));
      if (variablePart.coefficients().size() != 1
          || variablePart.coefficient(variable).isZero()) {
        continue;
      }
      BigInteger[] solved = IntegerMath.solveLinearCongruence(
          variablePart.coefficient(variable).numerator().toBigNumerator(),
          atom.term().constant().numerator().toBigNumerator().negate(), atom.modulus());
      if (solved == null) {
        return null;
      }
      if (solved[1].compareTo(modulus) > 0) {
        residue = solved[0];
        modulus = solved[1];
      }
    }
    BigInteger start = lower.add(IntegerMath.euclideanMod(residue.subtract(lower), modulus));
    return new Domain(start, upper, modulus);
  }

  private static void enumerate(int index, List<Variable> variables, List<Domain> domains,
      List<Atom> atoms, IntegerDomain domain, Map<Variable, BigInteger> assignment,
      TreeSet<BigInteger[]> output) {
    if (index == variables.size()) {
      for (Atom atom : atoms) {
        if (!holds(atom, assignment)) {
          return;
        }
      }
      BigInteger[] solution = new BigInteger[variables.size()];
      for (int position = 0; position < variables.size(); position++) {
        solution[position] = assignment.get(variables.get(position));
      }
      output.add(solution);
      return;
    }
    Variable variable = variables.get(index);
    Domain range = domains.get(index);
    for (BigInteger value = range.start; value.compareTo(range.upper) <= 0; value =
        value.add(range.step)) {
      if (!domain.accepts(value)) {
        continue;
      }
      assignment.put(variable, value);
      enumerate(index + 1, variables, domains, atoms, domain, assignment, output);
    }
    assignment.remove(variable);
  }

  private static boolean holds(Atom atom, Map<Variable, BigInteger> assignment) {
    BigInteger value = atom.term().constant().numerator().toBigNumerator();
    if (!atom.term().constant().denominator().toBigNumerator().equals(BigInteger.ONE)) {
      return false;
    }
    for (Map.Entry<Variable, org.matheclipse.core.interfaces.IRational> entry : atom.term()
        .coefficients().entrySet()) {
      BigInteger assigned = assignment.get(entry.getKey());
      if (assigned == null || !entry.getValue().isInteger()) {
        return false;
      }
      value = value.add(entry.getValue().numerator().toBigNumerator().multiply(assigned));
    }
    if (atom.isDivides()) {
      boolean divides = IntegerMath.euclideanMod(value, atom.modulus()).signum() == 0;
      return atom.isNegated() ? !divides : divides;
    }
    int sign = value.signum();
    switch (atom.relation()) {
      case EQUAL:
        return sign == 0;
      case NOT_EQUAL:
        return sign != 0;
      case LESS:
        return sign < 0;
      case LESS_EQUAL:
        return sign <= 0;
      case GREATER:
        return sign > 0;
      default:
        return sign >= 0;
    }
  }

  /**
   * Expand a formula in negation normal form into its conjunctions.
   *
   * @return one list of atoms per conjunction, or <code>null</code> if the expansion exceeds
   *         {@link #MAX_BRANCHES}
   */
  static List<List<Atom>> disjunctiveNormalForm(Formula formula) {
    switch (formula.kind()) {
      case TRUE: {
        List<List<Atom>> branches = new ArrayList<List<Atom>>(1);
        branches.add(new ArrayList<Atom>());
        return branches;
      }
      case FALSE:
        return new ArrayList<List<Atom>>();
      case ATOM: {
        List<List<Atom>> branches = new ArrayList<List<Atom>>(1);
        List<Atom> branch = new ArrayList<Atom>(1);
        branch.add(formula.atom());
        branches.add(branch);
        return branches;
      }
      case OR: {
        List<List<Atom>> branches = new ArrayList<List<Atom>>();
        for (Formula child : formula.children()) {
          List<List<Atom>> childBranches = disjunctiveNormalForm(child);
          if (childBranches == null) {
            return null;
          }
          branches.addAll(childBranches);
          if (branches.size() > MAX_BRANCHES) {
            return null;
          }
        }
        return branches;
      }
      case AND: {
        List<List<Atom>> product = new ArrayList<List<Atom>>();
        product.add(new ArrayList<Atom>());
        for (Formula child : formula.children()) {
          List<List<Atom>> childBranches = disjunctiveNormalForm(child);
          if (childBranches == null) {
            return null;
          }
          if (childBranches.isEmpty()) {
            return new ArrayList<List<Atom>>();
          }
          if ((long) product.size() * childBranches.size() > MAX_BRANCHES) {
            return null;
          }
          List<List<Atom>> next =
              new ArrayList<List<Atom>>(product.size() * childBranches.size());
          for (List<Atom> prefix : product) {
            for (List<Atom> suffix : childBranches) {
              List<Atom> combined = new ArrayList<Atom>(prefix.size() + suffix.size());
              combined.addAll(prefix);
              combined.addAll(suffix);
              next.add(combined);
            }
          }
          product = next;
        }
        return product;
      }
      default:
        // a quantifier or a negation above a compound formula is not in normal form
        return null;
    }
  }
}
