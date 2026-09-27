package org.matheclipse.core.reduce;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * Exact reduction of linear formulas over an ordered field - the {@link S#Reals} or the
 * {@link S#Rationals} - by Fourier-Motzkin elimination.
 *
 * <p>
 * Dense linear quantifier elimination, plus a cylindrical emitter: every variable is given by
 * bounds in the variables before it, the way Mathematica writes such a solution set, e.g.
 * <code>x+y&lt;1 &amp;&amp; x&gt;0 &amp;&amp; y&gt;0</code> is
 * <code>x&gt;0 &amp;&amp; x&lt;1 &amp;&amp; y&gt;0 &amp;&amp; y&lt;1-x</code>.
 *
 * <p>
 * The emitted form keeps every atom of the input and adds only atoms implied by it, so it is
 * equivalent to the input by construction; a branch is dropped only when the projection proves it
 * empty, which Fourier-Motzkin elimination does exactly over an ordered field.
 */
public final class RationalQE {

  /** The disjunctive normal form is abandoned beyond this many branches. */
  private static final int MAX_BRANCHES = 256;

  /** A lower or upper bound of a variable. */
  private static final class Bound {
    final AffineTerm value;
    final boolean strict;

    Bound(AffineTerm value, boolean strict) {
      this.value = value;
      this.strict = strict;
    }
  }

  private RationalQE() {}

  /**
   * Reduce a lowered formula for the given variables.
   *
   * @param formula the lowered formula, without divisibility atoms
   * @param targets the variables of the reduction, in the order they were asked for
   * @param rationals <code>true</code> for the {@link S#Rationals}: an infinite solution set then
   *        carries the membership of its variables
   * @return the reduced condition, or {@link F#NIL} if the formula is outside of this method
   */
  public static IExpr reduce(Formula formula, List<Variable> targets, boolean rationals) {
    if (formula.containsDivisibility()) {
      return F.NIL;
    }
    Formula quantifierFree = eliminateQuantifiers(formula);
    if (quantifierFree == null) {
      return F.NIL;
    }
    // the parameters are the outermost variables of the cylindrical form
    List<Variable> order = new ArrayList<Variable>();
    for (Variable variable : new TreeSet<Variable>(quantifierFree.freeVariables())) {
      if (!targets.contains(variable)) {
        order.add(variable);
      }
    }
    order.addAll(targets);
    List<List<Atom>> branches = dnf(splitDisequalities(quantifierFree));
    if (branches == null) {
      return F.NIL;
    }
    IASTAppendable disjuncts = F.ast(S.Or, branches.size());
    for (List<Atom> branch : branches) {
      IExpr cell = cylindricalCell(branch, order, targets, rationals);
      if (cell.isNIL()) {
        return F.NIL;
      }
      if (!cell.isFalse()) {
        disjuncts.append(cell);
      }
    }
    if (disjuncts.argSize() == 0) {
      return S.False;
    }
    return disjuncts.argSize() == 1 ? disjuncts.arg1() : factorCommonConjuncts(disjuncts);
  }

  /**
   * Pull the leading conjuncts which all cells share out of the disjunction:
   * <code>(x&isin;Rationals &amp;&amp; x&lt;2) || (x&isin;Rationals &amp;&amp; x&gt;2)</code> is
   * written <code>x&isin;Rationals &amp;&amp; (x&lt;2 || x&gt;2)</code>.
   */
  private static IExpr factorCommonConjuncts(IAST disjuncts) {
    List<IAST> cells = new ArrayList<IAST>(disjuncts.argSize());
    for (IExpr disjunct : disjuncts) {
      cells.add(disjunct.isAnd() ? (IAST) disjunct : F.And(disjunct));
    }
    int prefix = 0;
    boolean shared = true;
    while (shared) {
      IExpr candidate = F.NIL;
      for (IAST cell : cells) {
        // every cell keeps at least one conjunct of its own
        if (cell.argSize() <= prefix + 1
            || (candidate.isPresent() && !candidate.equals(cell.get(prefix + 1)))) {
          shared = false;
          break;
        }
        candidate = cell.get(prefix + 1);
      }
      if (shared) {
        prefix++;
      }
    }
    if (prefix == 0) {
      return disjuncts;
    }
    IASTAppendable result = F.ast(S.And, prefix + 1);
    result.appendArgs(cells.get(0), prefix + 1);
    IASTAppendable rest = F.ast(S.Or, cells.size());
    for (IAST cell : cells) {
      IASTAppendable remaining = F.ast(S.And, cell.argSize() - prefix);
      remaining.appendArgs(cell.subList(prefix + 1, cell.size()));
      rest.append(remaining.argSize() == 1 ? remaining.arg1() : remaining);
    }
    result.append(rest);
    return result;
  }

  /** Eliminate the quantifiers of a formula, or return <code>null</code>. */
  public static Formula eliminateQuantifiers(Formula formula) {
    Formula nnf = formula.nnf().normalized();
    Formula result = eliminateRecursive(nnf);
    return result == null ? null : result.normalized();
  }

  private static Formula eliminateRecursive(Formula formula) {
    switch (formula.kind()) {
      case TRUE:
      case FALSE:
      case ATOM:
        return formula;
      case NOT:
        return eliminateRecursive(formula.nnf().normalized());
      case AND:
      case OR: {
        List<Formula> children = new ArrayList<Formula>(formula.children().size());
        for (Formula child : formula.children()) {
          Formula eliminated = eliminateRecursive(child);
          if (eliminated == null) {
            return null;
          }
          children.add(eliminated);
        }
        return (formula.kind() == Formula.Kind.AND ? Formula.and(children) : Formula.or(children))
            .normalized();
      }
      default: {
        Formula body = eliminateRecursive(formula.body());
        if (body == null) {
          return null;
        }
        boolean exists = formula.kind() == Formula.Kind.EXISTS;
        for (Variable variable : formula.boundVariables()) {
          if (exists) {
            body = eliminateExists(body, variable);
          } else {
            // ForAll(v, p) is !Exists(v, !p)
            Formula negated = eliminateExists(Formula.not(body).nnf().normalized(), variable);
            body = negated == null ? null : Formula.not(negated).nnf().normalized();
          }
          if (body == null) {
            return null;
          }
        }
        return body;
      }
    }
  }

  private static Formula eliminateExists(Formula body, Variable variable) {
    List<List<Atom>> branches = dnf(splitDisequalities(body));
    if (branches == null) {
      return null;
    }
    List<Formula> projected = new ArrayList<Formula>(branches.size());
    for (List<Atom> branch : branches) {
      Formula projection = project(branch, variable);
      if (projection == null) {
        return null;
      }
      projected.add(projection);
    }
    return Formula.or(projected).normalized();
  }

  /** Replace every <code>t != 0</code> by <code>t &lt; 0 || t &gt; 0</code>. */
  private static Formula splitDisequalities(Formula formula) {
    switch (formula.kind()) {
      case ATOM: {
        Atom atom = formula.atom();
        if (atom.isRelation() && atom.relation() == Relation.NOT_EQUAL) {
          return Formula.or(Formula.atom(Atom.relation(Relation.LESS, atom.term())),
              Formula.atom(Atom.relation(Relation.GREATER, atom.term())));
        }
        return formula;
      }
      case AND:
      case OR: {
        List<Formula> children = new ArrayList<Formula>(formula.children().size());
        for (Formula child : formula.children()) {
          children.add(splitDisequalities(child));
        }
        return formula.kind() == Formula.Kind.AND ? Formula.and(children) : Formula.or(children);
      }
      default:
        return formula;
    }
  }

  /** The disjunctive normal form, or <code>null</code> beyond the branch budget. */
  private static List<List<Atom>> dnf(Formula formula) {
    switch (formula.kind()) {
      case TRUE: {
        List<List<Atom>> result = new ArrayList<List<Atom>>();
        result.add(new ArrayList<Atom>());
        return result;
      }
      case FALSE:
        return new ArrayList<List<Atom>>();
      case ATOM: {
        List<List<Atom>> result = new ArrayList<List<Atom>>();
        List<Atom> single = new ArrayList<Atom>();
        single.add(formula.atom());
        result.add(single);
        return result;
      }
      case OR: {
        List<List<Atom>> result = new ArrayList<List<Atom>>();
        for (Formula child : formula.children()) {
          List<List<Atom>> branches = dnf(child);
          if (branches == null) {
            return null;
          }
          result.addAll(branches);
          if (result.size() > MAX_BRANCHES) {
            return null;
          }
        }
        return result;
      }
      case AND: {
        List<List<Atom>> product = new ArrayList<List<Atom>>();
        product.add(new ArrayList<Atom>());
        for (Formula child : formula.children()) {
          List<List<Atom>> alternatives = dnf(child);
          if (alternatives == null) {
            return null;
          }
          List<List<Atom>> next = new ArrayList<List<Atom>>();
          for (List<Atom> prefix : product) {
            for (List<Atom> alternative : alternatives) {
              List<Atom> conjunction = new ArrayList<Atom>(prefix);
              conjunction.addAll(alternative);
              next.add(conjunction);
            }
          }
          if (next.size() > MAX_BRANCHES) {
            return null;
          }
          product = next;
        }
        return product;
      }
      default:
        return null;
    }
  }

  /**
   * Project a conjunction of atoms onto the other variables: an equation in the variable is solved
   * for it and substituted, otherwise every lower bound is paired with every upper bound.
   *
   * @return the projection, or <code>null</code> for a divisibility atom
   */
  private static Formula project(List<Atom> atoms, Variable variable) {
    for (int i = 0; i < atoms.size(); i++) {
      Atom atom = atoms.get(i);
      if (atom.isDivides()) {
        return null;
      }
      if (atom.relation() == Relation.EQUAL && !atom.term().coefficient(variable).isZero()) {
        AffineTerm value = solveFor(atom.term(), variable);
        List<Formula> substituted = new ArrayList<Formula>(atoms.size());
        for (int j = 0; j < atoms.size(); j++) {
          if (j != i) {
            substituted.add(Formula.atom(atoms.get(j).substitute(variable, value)));
          }
        }
        return Formula.and(substituted).normalized();
      }
    }
    List<Formula> independent = new ArrayList<Formula>();
    List<Bound> lower = new ArrayList<Bound>();
    List<Bound> upper = new ArrayList<Bound>();
    for (Atom atom : atoms) {
      if (atom.isDivides()) {
        return null;
      }
      IRational coefficient = atom.term().coefficient(variable);
      if (coefficient.isZero()) {
        independent.add(Formula.atom(atom));
        continue;
      }
      if (atom.relation() == Relation.NOT_EQUAL) {
        return null;
      }
      Bound bound = new Bound(solveFor(atom.term(), variable),
          atom.relation() == Relation.LESS || atom.relation() == Relation.GREATER);
      if (isUpperBound(atom.relation(), coefficient)) {
        upper.add(bound);
      } else {
        lower.add(bound);
      }
    }
    for (Bound low : lower) {
      for (Bound high : upper) {
        independent.add(Formula
            .atom(Atom.relation(low.strict || high.strict ? Relation.LESS : Relation.LESS_EQUAL,
                low.value.subtract(high.value))));
      }
    }
    return Formula.and(independent).normalized();
  }

  /** Test if <code>c*v + rest REL 0</code> bounds <code>v</code> from above. */
  private static boolean isUpperBound(Relation relation, IRational coefficient) {
    boolean positive = coefficient.complexSign() > 0;
    return relation == Relation.LESS || relation == Relation.LESS_EQUAL ? positive : !positive;
  }

  /** The value of <code>variable</code> which makes <code>term</code> zero. */
  private static AffineTerm solveFor(AffineTerm term, Variable variable) {
    IRational coefficient = term.coefficient(variable);
    AffineTerm rest = term.subtract(AffineTerm.variable(variable).scale(coefficient));
    return rest.scale(coefficient.inverse().negate());
  }

  /**
   * The cylindrical form of one conjunctive branch: for the variables in order, the atoms whose
   * last variable it is, with the values of the variables pinned by an equation substituted.
   *
   * @return {@link S#False} if the branch is empty, {@link F#NIL} if it can't be emitted
   */
  private static IExpr cylindricalCell(List<Atom> branch, List<Variable> order,
      List<Variable> targets, boolean rationals) {
    Map<Variable, List<Atom>> levels = new LinkedHashMap<Variable, List<Atom>>();
    List<Atom> current = new ArrayList<Atom>(branch);
    for (int k = order.size() - 1; k >= 0; k--) {
      Variable variable = order.get(k);
      List<Atom> level = new ArrayList<Atom>();
      for (Atom atom : current) {
        if (atom.containsVariable(variable)) {
          level.add(atom);
        }
      }
      Atom pivot = null;
      for (Atom atom : level) {
        if (atom.relation() == Relation.EQUAL) {
          pivot = atom;
          break;
        }
      }
      if (pivot != null) {
        // the other atoms of the level are substituted into the lower levels by the projection
        level.clear();
        level.add(pivot);
      }
      levels.put(variable, level);
      Formula projected = project(current, variable);
      if (projected == null) {
        return F.NIL;
      }
      if (projected.isFalse()) {
        return S.False;
      }
      current = atomsOf(projected);
      if (current == null) {
        return F.NIL;
      }
    }
    for (Atom atom : current) {
      if (!atom.isConstant()) {
        return F.NIL;
      }
      if (!atom.constantTruth()) {
        return S.False;
      }
    }

    IASTAppendable conjuncts = F.ast(S.And, branch.size() + order.size());
    List<Variable> pinned = new ArrayList<Variable>();
    List<AffineTerm> pinnedValues = new ArrayList<AffineTerm>();
    for (Variable variable : order) {
      List<Atom> level = new ArrayList<Atom>();
      for (Atom atom : levels.get(variable)) {
        Atom substituted = atom;
        for (int i = 0; i < pinned.size(); i++) {
          substituted = substituted.substitute(pinned.get(i), pinnedValues.get(i));
        }
        if (substituted.isConstant()) {
          if (!substituted.constantTruth()) {
            return S.False;
          }
          continue;
        }
        level.add(substituted);
      }
      level = dropDominatedBounds(level, variable);
      level = collapseEqualBounds(level, variable);
      List<Variable> isolated = new ArrayList<Variable>(1);
      isolated.add(variable);
      for (Atom atom : level) {
        if (atom.relation() == Relation.EQUAL && atom.term().coefficients().size() == 1) {
          pinned.add(variable);
          pinnedValues.add(solveFor(atom.term(), variable));
        }
        conjuncts.append(Emitter.formula(Formula.atom(atom), isolated));
      }
    }
    if (rationals) {
      IASTAppendable members = F.ast(S.Alternatives, targets.size());
      for (Variable target : targets) {
        if (!pinned.contains(target)) {
          members.append(target.symbol());
        }
      }
      if (members.argSize() > 0) {
        IASTAppendable withMembership = F.ast(S.And, conjuncts.argSize() + 1);
        withMembership
            .append(F.Element(members.argSize() == 1 ? members.arg1() : members, S.Rationals));
        withMembership.appendArgs(conjuncts);
        conjuncts = withMembership;
      }
    }
    if (conjuncts.argSize() == 0) {
      return S.True;
    }
    return conjuncts.argSize() == 1 ? conjuncts.arg1() : conjuncts;
  }

  /** The atoms of a conjunction, or <code>null</code> if it isn't one. */
  private static List<Atom> atomsOf(Formula formula) {
    List<Atom> atoms = new ArrayList<Atom>();
    switch (formula.kind()) {
      case TRUE:
        return atoms;
      case ATOM:
        atoms.add(formula.atom());
        return atoms;
      case AND:
        for (Formula child : formula.children()) {
          if (child.kind() != Formula.Kind.ATOM) {
            return null;
          }
          atoms.add(child.atom());
        }
        return atoms;
      default:
        return null;
    }
  }

  /**
   * Keep only the tightest of the numeric lower and of the numeric upper bounds of the variable; a
   * bound which depends on other variables is kept.
   */
  private static List<Atom> dropDominatedBounds(List<Atom> level, Variable variable) {
    Atom lowest = null;
    Atom highest = null;
    List<Atom> result = new ArrayList<Atom>(level.size());
    for (Atom atom : level) {
      if (atom.relation() == Relation.EQUAL || atom.relation() == Relation.NOT_EQUAL
          || atom.term().coefficients().size() != 1) {
        result.add(atom);
        continue;
      }
      IRational coefficient = atom.term().coefficient(variable);
      if (isUpperBound(atom.relation(), coefficient)) {
        highest = highest == null || tighter(atom, highest, variable, true) ? atom : highest;
      } else {
        lowest = lowest == null || tighter(atom, lowest, variable, false) ? atom : lowest;
      }
    }
    List<Atom> bounds = new ArrayList<Atom>(2);
    if (lowest != null) {
      bounds.add(lowest);
    }
    if (highest != null) {
      bounds.add(highest);
    }
    bounds.addAll(result);
    return bounds;
  }

  /** <code>x&gt;=c &amp;&amp; x&lt;=c</code> is <code>x==c</code>. */
  private static List<Atom> collapseEqualBounds(List<Atom> level, Variable variable) {
    if (level.size() < 2) {
      return level;
    }
    Atom first = level.get(0);
    Atom second = level.get(1);
    if (first.relation() != Relation.LESS_EQUAL && first.relation() != Relation.GREATER_EQUAL
        || second.relation() != Relation.LESS_EQUAL && second.relation() != Relation.GREATER_EQUAL
        || first.term().coefficients().size() != 1 || second.term().coefficients().size() != 1
        || !first.containsVariable(variable) || !second.containsVariable(variable)) {
      return level;
    }
    AffineTerm value = solveFor(first.term(), variable);
    if (!value.equals(solveFor(second.term(), variable))) {
      return level;
    }
    List<Atom> result = new ArrayList<Atom>(level.size() - 1);
    result.add(Atom.relation(Relation.EQUAL, first.term()));
    result.addAll(level.subList(2, level.size()));
    return result;
  }

  /** Test if the numeric bound <code>atom</code> is tighter than <code>other</code>. */
  private static boolean tighter(Atom atom, Atom other, Variable variable, boolean upper) {
    IRational value = solveFor(atom.term(), variable).constant();
    IRational otherValue = solveFor(other.term(), variable).constant();
    int comparison = value.compareTo(otherValue);
    if (comparison == 0) {
      return isStrict(atom.relation()) && !isStrict(other.relation());
    }
    return upper ? comparison < 0 : comparison > 0;
  }

  private static boolean isStrict(Relation relation) {
    return relation == Relation.LESS || relation == Relation.GREATER;
  }

  /**
   * Test if the formula can be reduced over the reals: it has an ordering atom, and every free
   * variable occurs in one - an equation alone doesn't make a variable real.
   */
  public static boolean isRealFormula(Formula formula) {
    TreeSet<Variable> ordered = new TreeSet<Variable>();
    collectOrderedVariables(formula, ordered);
    return !ordered.isEmpty() && ordered.containsAll(formula.freeVariables());
  }

  private static void collectOrderedVariables(Formula formula, TreeSet<Variable> ordered) {
    switch (formula.kind()) {
      case TRUE:
      case FALSE:
        return;
      case ATOM: {
        Atom atom = formula.atom();
        if (atom.isRelation() && atom.relation() != Relation.EQUAL
            && atom.relation() != Relation.NOT_EQUAL) {
          ordered.addAll(atom.variables());
        }
        return;
      }
      case AND:
      case OR:
      case NOT:
        for (Formula child : formula.children()) {
          collectOrderedVariables(child, ordered);
        }
        return;
      default:
        collectOrderedVariables(formula.body(), ordered);
        return;
    }
  }

  /** Test if the formula has an ordering atom. */
  public static boolean hasOrdering(Formula formula) {
    TreeSet<Variable> ordered = new TreeSet<Variable>();
    collectOrderedVariables(formula, ordered);
    return !ordered.isEmpty();
  }
}
