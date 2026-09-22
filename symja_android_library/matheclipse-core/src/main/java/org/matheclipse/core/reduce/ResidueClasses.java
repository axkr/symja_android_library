package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The surface form of an integer solution set with congruences, the way Mathematica writes it: a
 * residue class is given by a generated parameter instead of a <code>Mod</code> condition, so
 * <code>Mod(x,6)==4 &amp;&amp; x&gt;10</code> is
 * <code>C(1)&isin;Integers &amp;&amp; C(1)&gt;=2 &amp;&amp; x==4+6*C(1)</code>.
 *
 * <p>
 * Port of the residue class emitter of Woxi (<code>reduce_backend/emit.rs</code>, commit
 * <code>eee6117f5</code>). A bound on the variable becomes a bound on the parameter, a denied
 * congruence splits into the classes it leaves, every congruent variable gets its own parameter,
 * and a branch which pins a variable between two bounds reports its values.
 */
public final class ResidueClasses {

  /** More classes than this are left as the congruence they are. */
  private static final int MAX_CLASSES = 64;

  /** A branch bounded to at most this many values reports them. */
  private static final int MAX_VALUES = 64;

  /** The residue classes one variable is confined to in one branch. */
  private static final class TargetClasses {
    final Variable target;
    final BigInteger modulus;
    final List<BigInteger> residues;

    TargetClasses(Variable target, BigInteger modulus, List<BigInteger> residues) {
      this.target = target;
      this.modulus = modulus;
      this.residues = residues;
    }
  }

  /** Marker for a branch no integer satisfies. */
  private static final List<TargetClasses> UNSATISFIABLE = Collections.emptyList();

  private ResidueClasses() {}

  /**
   * The parametrized form of a quantifier free integer formula.
   *
   * @param formula the normalized formula
   * @param targets the variables of the reduction, in the order they were asked for
   * @return the parametrized form, or {@link F#NIL} if no branch contains a residue class which
   *         can be written this way
   */
  public static IExpr form(Formula formula, List<Variable> targets) {
    List<List<Atom>> branches = dnf(formula, new int[] {MAX_CLASSES});
    if (branches == null || branches.isEmpty()) {
      return F.NIL;
    }
    boolean parametrizedAny = false;
    IASTAppendable disjuncts = F.ast(S.Or, branches.size());
    for (List<Atom> branch : branches) {
      List<TargetClasses> classes = branchClasses(branch, targets);
      if (classes == null) {
        return F.NIL;
      }
      if (classes == UNSATISFIABLE) {
        continue;
      }
      if (classes.isEmpty()) {
        IExpr plain = plainBranch(branch, targets);
        if (plain.isPresent()) {
          disjuncts.append(plain);
        }
        continue;
      }
      parametrizedAny = true;
      disjuncts.appendArgs(parametrizedBranches(branch, targets, classes));
    }
    if (disjuncts.argSize() == 0) {
      // contradicting congruences, e.g. `Mod(x,4)==1 && Mod(x,6)==2`
      return S.False;
    }
    if (!parametrizedAny) {
      return F.NIL;
    }
    return disjuncts.argSize() == 1 ? disjuncts.arg1() : disjuncts;
  }

  /** The disjunctive normal form, or <code>null</code> if it has more than a budget of branches. */
  private static List<List<Atom>> dnf(Formula formula, int[] budget) {
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
          List<List<Atom>> branches = dnf(child, budget);
          if (branches == null) {
            return null;
          }
          result.addAll(branches);
        }
        return result.size() > budget[0] ? null : result;
      }
      case AND: {
        List<List<Atom>> product = new ArrayList<List<Atom>>();
        product.add(new ArrayList<Atom>());
        for (Formula child : formula.children()) {
          List<List<Atom>> alternatives = dnf(child, budget);
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
          if (next.size() > budget[0]) {
            return null;
          }
          product = next;
        }
        return product;
      }
      default:
        // negations and quantifiers are eliminated before
        return null;
    }
  }

  /**
   * The residue classes each target is confined to in one branch: an empty list if no target is
   * confined, {@link #UNSATISFIABLE} if no integer satisfies the branch, <code>null</code> if the
   * branch is outside of this form - a congruence tying several variables together, too many
   * classes, or an equation pinning a congruent variable (<code>Mod(x,2)==1 &amp;&amp; x==5</code>
   * is answered with <code>x==5</code>).
   */
  private static List<TargetClasses> branchClasses(List<Atom> branch, List<Variable> targets) {
    List<TargetClasses> classes = new ArrayList<TargetClasses>();
    for (Variable target : targets) {
      BigInteger residue = BigInteger.ZERO;
      BigInteger modulus = BigInteger.ONE;
      List<BigInteger[]> forbidden = new ArrayList<BigInteger[]>();
      for (Atom atom : branch) {
        if (!atom.isDivides() || atom.term().coefficient(target).isZero()) {
          continue;
        }
        AffineTerm term = atom.term();
        IRational coefficient = term.coefficient(target);
        if (term.coefficients().size() != 1 || !term.constant().isInteger()
            || !coefficient.isInteger()) {
          return null;
        }
        BigInteger[] solved = IntegerMath.solveLinearCongruence(
            coefficient.numerator().toBigNumerator(),
            term.constant().numerator().toBigNumerator().negate(), atom.modulus());
        if (atom.isNegated()) {
          if (solved != null) {
            forbidden.add(solved);
          }
          // a denied congruence which no integer solves denies nothing
        } else {
          if (solved == null) {
            return UNSATISFIABLE;
          }
          BigInteger[] combined = IntegerMath.crtPair(residue, modulus, solved[0], solved[1]);
          if (combined == null) {
            return UNSATISFIABLE;
          }
          residue = combined[0];
          modulus = combined[1];
        }
      }
      BigInteger period = modulus;
      for (BigInteger[] denied : forbidden) {
        period = IntegerMath.lcm(period, denied[1]);
      }
      if (period.compareTo(BigInteger.valueOf(MAX_CLASSES)) > 0) {
        return null;
      }
      int periodLength = period.intValue();
      List<BigInteger> residues = new ArrayList<BigInteger>();
      for (int candidate = 0; candidate < periodLength; candidate++) {
        BigInteger value = BigInteger.valueOf(candidate);
        if (!IntegerMath.euclideanMod(value, modulus).equals(residue)) {
          continue;
        }
        boolean denied = false;
        for (BigInteger[] forbiddenClass : forbidden) {
          if (IntegerMath.euclideanMod(value, forbiddenClass[1]).equals(forbiddenClass[0])) {
            denied = true;
            break;
          }
        }
        if (!denied) {
          residues.add(value);
        }
      }
      if (residues.isEmpty()) {
        return UNSATISFIABLE;
      }
      if (residues.size() == periodLength) {
        // every residue is allowed: no constraint, the target keeps its own membership
        continue;
      }
      classes.add(new TargetClasses(target, period, residues));
    }
    for (Atom atom : branch) {
      if (atom.isRelation() && atom.relation() == Relation.EQUAL) {
        for (TargetClasses targetClasses : classes) {
          if (!atom.term().coefficient(targetClasses.target).isZero()) {
            return null;
          }
        }
      }
    }
    long combinations = 1;
    for (TargetClasses targetClasses : classes) {
      combinations *= targetClasses.residues.size();
      if (combinations > MAX_CLASSES) {
        return null;
      }
    }
    return classes;
  }

  /**
   * A branch without a congruence: the membership of every target it mentions, then the branch
   * itself. A branch which pins its only target between two bounds reports the values instead
   * (<code>x==8</code>, not <code>x&isin;Integers &amp;&amp; x&gt;=8 &amp;&amp; x&lt;=8</code>).
   *
   * @return {@link F#NIL} if no integer satisfies the branch
   */
  private static IExpr plainBranch(List<Atom> branch, List<Variable> targets) {
    if (targets.size() == 1) {
      List<BigInteger> values = boundedValues(branch, targets.get(0));
      if (values != null) {
        if (values.isEmpty()) {
          return F.NIL;
        }
        return valuesExpr(targets.get(0), values);
      }
    }
    Formula formula = Formula.and(atomFormulas(branch)).normalized();
    IASTAppendable conjuncts = F.ast(S.And, targets.size() + 1);
    for (Variable target : targets) {
      if (formula.containsVariable(target)) {
        conjuncts.append(F.Element(target.symbol(), S.Integers));
      }
    }
    appendConjuncts(conjuncts, Emitter.formula(formula, targets));
    return conjuncts.argSize() == 1 ? conjuncts.arg1() : conjuncts;
  }

  /**
   * The integers of a branch which bounds its only variable from both sides, or <code>null</code>
   * if the branch isn't of this shape.
   */
  private static List<BigInteger> boundedValues(List<Atom> branch, Variable target) {
    BigInteger lower = null;
    BigInteger upper = null;
    List<BigInteger> excluded = new ArrayList<BigInteger>();
    for (Atom atom : branch) {
      AffineTerm term = atom.term();
      if (!atom.isRelation() || term.coefficients().size() != 1
          || term.coefficient(target).isZero()) {
        return null;
      }
      IRational coefficient = term.coefficient(target);
      // coefficient*x + constant REL 0  <=>  x REL' boundary
      IRational boundary = term.constant().negate().divideBy(coefficient);
      Relation relation = coefficient.complexSign() < 0 ? atom.relation().reversed()
          : atom.relation();
      BigInteger floor = IntegerMath.floorDiv(boundary.numerator().toBigNumerator(),
          boundary.denominator().toBigNumerator());
      BigInteger ceil = IntegerMath.ceilDiv(boundary.numerator().toBigNumerator(),
          boundary.denominator().toBigNumerator());
      boolean integral = boundary.isInteger();
      switch (relation) {
        case EQUAL:
          if (!integral) {
            return Collections.emptyList();
          }
          lower = max(lower, floor);
          upper = min(upper, floor);
          break;
        case NOT_EQUAL:
          if (integral) {
            excluded.add(floor);
          }
          break;
        case LESS:
          upper = min(upper, integral ? floor.subtract(BigInteger.ONE) : floor);
          break;
        case LESS_EQUAL:
          upper = min(upper, floor);
          break;
        case GREATER:
          lower = max(lower, integral ? ceil.add(BigInteger.ONE) : ceil);
          break;
        default:
          lower = max(lower, ceil);
          break;
      }
    }
    if (lower == null || upper == null) {
      return null;
    }
    if (upper.subtract(lower).compareTo(BigInteger.valueOf(MAX_VALUES)) >= 0) {
      return null;
    }
    List<BigInteger> values = new ArrayList<BigInteger>();
    for (BigInteger value = lower; value.compareTo(upper) <= 0; value = value.add(BigInteger.ONE)) {
      if (!excluded.contains(value)) {
        values.add(value);
      }
    }
    return values;
  }

  private static BigInteger max(BigInteger bound, BigInteger value) {
    return bound == null || value.compareTo(bound) > 0 ? value : bound;
  }

  private static BigInteger min(BigInteger bound, BigInteger value) {
    return bound == null || value.compareTo(bound) < 0 ? value : bound;
  }

  private static IExpr valuesExpr(Variable target, List<BigInteger> values) {
    IASTAppendable disjuncts = F.ast(S.Or, values.size());
    for (BigInteger value : values) {
      disjuncts.append(F.Equal(target.symbol(), F.ZZ(value)));
    }
    return disjuncts.argSize() == 1 ? disjuncts.arg1() : disjuncts;
  }

  /** One disjunct per combination of residue classes; empty combinations are dropped. */
  private static IAST parametrizedBranches(List<Atom> branch, List<Variable> targets,
      List<TargetClasses> classes) {
    List<List<BigInteger>> combinations = new ArrayList<List<BigInteger>>();
    combinations.add(new ArrayList<BigInteger>());
    for (TargetClasses targetClasses : classes) {
      List<List<BigInteger>> next = new ArrayList<List<BigInteger>>();
      for (List<BigInteger> prefix : combinations) {
        for (BigInteger residue : targetClasses.residues) {
          List<BigInteger> extended = new ArrayList<BigInteger>(prefix);
          extended.add(residue);
          next.add(extended);
        }
      }
      combinations = next;
    }
    List<IAST> disjuncts = new ArrayList<IAST>();
    for (List<BigInteger> choice : combinations) {
      IAST disjunct = parametrizedBranch(branch, targets, classes, choice);
      if (disjunct.isPresent()) {
        disjuncts.add(disjunct);
      }
    }
    return factorSharedMembership(disjuncts);
  }

  /**
   * <code>C(1)&isin;Integers &amp;&amp; x==3*C(1)</code> together with
   * <code>C(1)&isin;Integers &amp;&amp; x==2+3*C(1)</code> is written with the membership pulled
   * out front - but only while the classes carry nothing else, since a bound of one class has to
   * stay with its class.
   */
  private static IAST factorSharedMembership(List<IAST> disjuncts) {
    IASTAppendable result = F.ListAlloc(disjuncts.size());
    boolean factor = disjuncts.size() >= 2;
    IExpr membership = F.NIL;
    for (IAST disjunct : disjuncts) {
      if (!disjunct.isAnd() || disjunct.argSize() != 2 || !disjunct.arg2().isEqual()
          || (membership.isPresent() && !membership.equals(disjunct.arg1()))) {
        factor = false;
        break;
      }
      membership = disjunct.arg1();
    }
    if (!factor) {
      for (IAST disjunct : disjuncts) {
        result.append(disjunct);
      }
      return result;
    }
    IASTAppendable equations = F.ast(S.Or, disjuncts.size());
    for (IAST disjunct : disjuncts) {
      equations.append(disjunct.arg2());
    }
    result.append(F.And(membership, equations));
    return result;
  }

  private static IAST parametrizedBranch(List<Atom> branch, List<Variable> targets,
      List<TargetClasses> classes, List<BigInteger> choice) {
    List<Variable> parameters = new ArrayList<Variable>(classes.size());
    List<AffineTerm> replacements = new ArrayList<AffineTerm>(classes.size());
    IASTAppendable renames = F.ListAlloc(classes.size());
    for (int slot = 0; slot < classes.size(); slot++) {
      ISymbol symbol = F.Dummy("C$" + (slot + 1));
      Variable parameter = Variable.free(symbol);
      parameters.add(parameter);
      replacements.add(AffineTerm.variable(parameter)
          .scale(F.ZZ(classes.get(slot).modulus)).add(AffineTerm.integer(choice.get(slot))));
      renames.append(F.Rule(symbol, F.C(slot + 1)));
    }
    // the parameters lead the isolation order, so a relation which survives the substitution is
    // stated as a bound on its parameter
    List<Variable> isolationOrder = new ArrayList<Variable>(parameters);
    for (Variable target : targets) {
      if (!isClassTarget(target, classes)) {
        isolationOrder.add(target);
      }
    }

    IASTAppendable parameterBounds = F.ast(S.And, branch.size());
    IASTAppendable remaining = F.ast(S.And, branch.size());
    for (Atom atom : branch) {
      if (atom.isDivides() && mentionsClassTarget(atom, classes)) {
        continue;
      }
      Atom substituted = atom;
      for (int slot = 0; slot < classes.size(); slot++) {
        substituted = substituted.substitute(classes.get(slot).target, replacements.get(slot));
      }
      Formula normalized = IntegerNormalizer.normalize(Formula.atom(substituted), true);
      if (normalized == null) {
        return F.NIL;
      }
      if (normalized.isTrue()) {
        continue;
      }
      if (normalized.isFalse()) {
        // the bounds rule this residue class out
        return F.NIL;
      }
      boolean onlyParameters = parameters.containsAll(substituted.variables());
      IExpr expression = Emitter.formula(normalized, isolationOrder);
      appendConjuncts(onlyParameters ? parameterBounds : remaining, expression);
    }

    // `(y|C(1))∈Integers`: the targets which keep themselves first, then one parameter per
    // congruent target
    IASTAppendable members = F.ast(S.Alternatives, targets.size() + parameters.size());
    for (Variable target : targets) {
      if (!isClassTarget(target, classes) && mentions(branch, target)) {
        members.append(target.symbol());
      }
    }
    for (Variable parameter : parameters) {
      members.append(parameter.symbol());
    }
    IASTAppendable conjuncts = F.ast(S.And, branch.size() + classes.size() + 1);
    conjuncts.append(F.Element(members.argSize() == 1 ? members.arg1() : members, S.Integers));
    conjuncts.appendArgs(parameterBounds);
    for (int slot = 0; slot < classes.size(); slot++) {
      conjuncts.append(F.Equal(classes.get(slot).target.symbol(), classValue(choice.get(slot),
          classes.get(slot).modulus, parameters.get(slot).symbol())));
    }
    conjuncts.appendArgs(remaining);
    return (IAST) F.subst(conjuncts, renames);
  }

  /** <code>residue + modulus*C</code>. */
  private static IExpr classValue(BigInteger residue, BigInteger modulus, ISymbol parameter) {
    IExpr scaled = modulus.equals(BigInteger.ONE) ? parameter : F.Times(F.ZZ(modulus), parameter);
    return residue.signum() == 0 ? scaled : F.Plus(F.ZZ(residue), scaled);
  }

  private static boolean isClassTarget(Variable target, List<TargetClasses> classes) {
    for (TargetClasses targetClasses : classes) {
      if (targetClasses.target.equals(target)) {
        return true;
      }
    }
    return false;
  }

  private static boolean mentionsClassTarget(Atom atom, List<TargetClasses> classes) {
    for (TargetClasses targetClasses : classes) {
      if (atom.containsVariable(targetClasses.target)) {
        return true;
      }
    }
    return false;
  }

  private static boolean mentions(List<Atom> branch, Variable target) {
    for (Atom atom : branch) {
      if (atom.containsVariable(target)) {
        return true;
      }
    }
    return false;
  }

  private static List<Formula> atomFormulas(List<Atom> branch) {
    List<Formula> formulas = new ArrayList<Formula>(branch.size());
    for (Atom atom : branch) {
      formulas.add(Formula.atom(atom));
    }
    return formulas;
  }

  private static void appendConjuncts(IASTAppendable conjuncts, IExpr expression) {
    if (expression.isTrue()) {
      return;
    }
    if (expression.isAnd()) {
      conjuncts.appendArgs((IAST) expression);
    } else {
      conjuncts.append(expression);
    }
  }
}
