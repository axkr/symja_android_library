package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.matheclipse.core.expression.F;

/**
 * Quantifier elimination for linear integer arithmetic, by Cooper's method.
 *
 * <p>
 * The procedure is a decision procedure, not a search. Existential elimination does not look for a
 * witness in some interval: it instantiates the finitely many boundary terms of the ordered atoms
 * and the finitely many residues of the divisibility atoms, which Cooper's theorem proves to be
 * enough. An unbounded solution set is therefore decided exactly like a bounded one.
 *
 * <p>
 * The elimination of one variable produces <code>period * (boundaries + 1)</code> instances, and a
 * nested elimination multiplies those counts. That growth is inherent to the theory, so the engine
 * carries an explicit budget: when it is exceeded the elimination returns <code>null</code> and the
 * caller leaves the expression unevaluated. It never guesses and never truncates a solution set.
 *
 * <p>
 * References: D. C. Cooper, <i>Theorem Proving in Arithmetic without Multiplication</i>; A. Chaieb
 * and T. Nipkow, <i>Verifying and Reflecting Quantifier Elimination for Presburger Arithmetic</i>.
 */
public final class Presburger {

  /** Maximum number of instances one variable elimination may produce. */
  public static final int DEFAULT_BUDGET = 20000;

  private Presburger() {}

  /**
   * Eliminate every quantifier of a formula.
   *
   * @return an equivalent quantifier free formula, or <code>null</code> if the formula is outside
   *         linear integer arithmetic or the budget was exceeded
   */
  public static Formula eliminateQuantifiers(Formula formula) {
    return eliminateQuantifiers(formula, DEFAULT_BUDGET);
  }

  public static Formula eliminateQuantifiers(Formula formula, int budget) {
    Formula normalized = IntegerNormalizer.normalize(formula, false);
    if (normalized == null) {
      return null;
    }
    Formula result = eliminateRecursive(normalized, new FormulaMemo(), budget);
    return result == null ? null : simplify(result);
  }

  static Formula eliminateRecursive(Formula formula, FormulaMemo memo, int budget) {
    Formula cached = memo.get(formula);
    if (cached != null) {
      return cached;
    }
    Formula source = formula;
    Formula result;
    switch (formula.kind()) {
      case TRUE:
      case FALSE:
      case ATOM:
        return formula;
      case AND:
      case OR: {
        List<Formula> children = new ArrayList<Formula>(formula.children().size());
        for (Formula child : formula.children()) {
          Formula eliminated = eliminateRecursive(child, memo, budget);
          if (eliminated == null) {
            return null;
          }
          children.add(eliminated);
        }
        result = (formula.kind() == Formula.Kind.AND ? Formula.and(children) : Formula.or(children))
            .normalized();
        break;
      }
      case EXISTS:
      case FORALL: {
        Formula body = eliminateRecursive(formula.body(), memo, budget);
        if (body == null) {
          return null;
        }
        TreeSet<Variable> remaining = new TreeSet<Variable>(formula.boundVariables());
        while (true) {
          Variable variable = chooseEliminationVariable(body, remaining);
          if (variable == null) {
            break;
          }
          remaining.remove(variable);
          body = formula.kind() == Formula.Kind.EXISTS ? eliminateExists(body, variable, budget)
              : eliminateForAll(body, variable, budget);
          if (body == null) {
            return null;
          }
        }
        result = body.normalized();
        break;
      }
      default:
        // the input is normalized, so no Not survives above a compound formula
        return null;
    }
    memo.put(source, result);
    return result;
  }

  /**
   * Choose the next variable to eliminate: the one with the smallest estimated instance count. The
   * variable itself breaks ties, so the choice does not depend on the order the input was written
   * in.
   */
  public static Variable chooseEliminationVariable(Formula formula, TreeSet<Variable> candidates) {
    Variable best = null;
    Cost bestCost = null;
    for (Variable candidate : candidates) {
      if (!formula.containsVariable(candidate)) {
        // a variable which does not occur is eliminated by simply dropping it
        return candidate;
      }
      Cost cost = eliminationCost(formula, candidate);
      if (bestCost == null || cost.compareTo(bestCost) < 0) {
        best = candidate;
        bestCost = cost;
      }
    }
    return best;
  }

  /** The estimated number of Cooper instances for one variable, with the atom count as tiebreak. */
  public static Cost eliminationCost(Formula formula, Variable variable) {
    Cost cost = new Cost();
    collectCost(formula, variable, cost);
    cost.instances = cost.period.multiply(cost.coefficientLcm)
        .multiply(BigInteger.valueOf(cost.boundaries + 1L));
    return cost;
  }

  /** The estimated cost of eliminating one variable. */
  public static final class Cost implements Comparable<Cost> {
    BigInteger period = BigInteger.ONE;
    BigInteger coefficientLcm = BigInteger.ONE;
    int boundaries;
    int occurrences;
    BigInteger instances = BigInteger.ONE;

    public BigInteger instances() {
      return instances;
    }

    public int occurrences() {
      return occurrences;
    }

    @Override
    public int compareTo(Cost that) {
      int byInstances = this.instances.compareTo(that.instances);
      return byInstances != 0 ? byInstances : Integer.compare(this.occurrences, that.occurrences);
    }

    @Override
    public String toString() {
      return instances + "/" + occurrences;
    }
  }

  private static void collectCost(Formula formula, Variable variable, Cost cost) {
    if (formula.kind() == Formula.Kind.ATOM) {
      Atom atom = formula.atom();
      BigInteger coefficient = coefficientOf(atom, variable);
      if (coefficient == null || coefficient.signum() == 0) {
        return;
      }
      cost.occurrences++;
      cost.coefficientLcm = IntegerMath.lcm(cost.coefficientLcm, coefficient.abs());
      if (atom.isDivides()) {
        cost.period = IntegerMath.lcm(cost.period, atom.modulus());
      } else {
        cost.boundaries++;
      }
      return;
    }
    for (Formula child : formula.children()) {
      collectCost(child, variable, cost);
    }
  }

  private static BigInteger coefficientOf(Atom atom, Variable variable) {
    org.matheclipse.core.interfaces.IRational coefficient = atom.term().coefficient(variable);
    if (!coefficient.isInteger()) {
      return null;
    }
    return coefficient.numerator().toBigNumerator();
  }

  private static Formula eliminateForAll(Formula body, Variable variable, int budget) {
    Formula negated = IntegerNormalizer.normalize(Formula.not(body), false);
    if (negated == null) {
      return null;
    }
    Formula exists = eliminateExists(negated, variable, budget);
    if (exists == null) {
      return null;
    }
    return IntegerNormalizer.normalize(Formula.not(exists), false);
  }

  /**
   * Eliminate <code>Exists(variable, body)</code>.
   *
   * <p>
   * After the coefficients of the variable are normalized to <code>&plusmn;1</code>, the truth of
   * the divisibility atoms is periodic with period <code>delta</code> and the truth of the ordered
   * atoms is constant below the smallest boundary. Cooper's theorem then reduces the existential to
   * one instance per residue of the stabilized tail plus one instance just above every lower
   * boundary.
   */
  static Formula eliminateExists(Formula body, Variable variable, int budget) {
    if (!body.containsVariable(variable)) {
      return body;
    }
    Normalized normalized = normalizeCoefficients(body, variable);
    if (normalized == null) {
      return null;
    }
    Formula prepared = normalized.formula;
    if (normalized.coefficientLcm.compareTo(BigInteger.ONE) > 0) {
      Atom scaled = Atom.divides(normalized.coefficientLcm, AffineTerm.variable(variable), false);
      if (scaled == null) {
        return null;
      }
      prepared = Formula.and(prepared, Formula.atom(scaled)).normalized();
    }

    BigInteger period = divisibilityPeriod(prepared, variable);
    List<AffineTerm> boundaries = new ArrayList<AffineTerm>();
    if (!collectBoundaries(prepared, variable, boundaries)) {
      return null;
    }
    BigInteger instances = period.multiply(BigInteger.valueOf(boundaries.size() + 1L));
    if (instances.compareTo(BigInteger.valueOf(budget)) > 0) {
      return null;
    }

    List<Formula> disjuncts = new ArrayList<Formula>(instances.intValue());
    for (BigInteger residue = BigInteger.ZERO; residue.compareTo(period) < 0; residue =
        residue.add(BigInteger.ONE)) {
      AffineTerm residueTerm = AffineTerm.integer(residue);
      Formula tail = negativeInfinityInstance(prepared, variable, residueTerm);
      if (tail == null) {
        return null;
      }
      disjuncts.add(tail);
      for (AffineTerm boundary : boundaries) {
        disjuncts.add(prepared.substitute(variable, boundary.add(residueTerm)).normalized());
      }
    }
    // substitution reintroduces non primitive coefficients, e.g. x <= 5 with x := 2t becomes
    // 2t - 5 <= 0, so normalize again: it tightens that to t <= 2 and keeps the coefficient
    // lowest common multiple of the next elimination small
    Formula result = IntegerNormalizer.normalize(simplify(Formula.or(disjuncts)), false);
    if (result == null) {
      return null;
    }
    if (result.containsVariable(variable)) {
      // a variable which survives its own elimination would silently change the meaning
      return null;
    }
    return simplify(result);
  }

  private static final class Normalized {
    final Formula formula;
    final BigInteger coefficientLcm;

    Normalized(Formula formula, BigInteger coefficientLcm) {
      this.formula = formula;
      this.coefficientLcm = coefficientLcm;
    }
  }

  /**
   * Make every occurrence of the variable have coefficient <code>&plusmn;1</code>, by scaling each
   * atom and reading the variable as <code>L*variable</code>. The caller adds the constraint
   * <code>L | variable</code>, without which the transformation would admit non integral values.
   */
  private static Normalized normalizeCoefficients(Formula formula, Variable variable) {
    BigInteger coefficientLcm = collectCoefficientLcm(formula, variable);
    if (coefficientLcm == null) {
      return null;
    }
    Formula transformed = transform(formula, variable, coefficientLcm);
    if (transformed == null) {
      return null;
    }
    return new Normalized(transformed.normalized(), coefficientLcm);
  }

  private static BigInteger collectCoefficientLcm(Formula formula, Variable variable) {
    if (formula.kind() == Formula.Kind.ATOM) {
      Atom atom = formula.atom();
      if (atom.isRelation() && atom.relation() != Relation.LESS_EQUAL) {
        return null;
      }
      BigInteger coefficient = coefficientOf(atom, variable);
      if (coefficient == null) {
        return null;
      }
      return coefficient.signum() == 0 ? BigInteger.ONE : coefficient.abs();
    }
    if (formula.kind() == Formula.Kind.TRUE || formula.kind() == Formula.Kind.FALSE) {
      return BigInteger.ONE;
    }
    if (formula.kind() != Formula.Kind.AND && formula.kind() != Formula.Kind.OR) {
      return null;
    }
    BigInteger common = BigInteger.ONE;
    for (Formula child : formula.children()) {
      BigInteger childLcm = collectCoefficientLcm(child, variable);
      if (childLcm == null) {
        return null;
      }
      common = IntegerMath.lcm(common, childLcm);
    }
    return common;
  }

  private static Formula transform(Formula formula, Variable variable, BigInteger coefficientLcm) {
    switch (formula.kind()) {
      case TRUE:
      case FALSE:
        return formula;
      case ATOM: {
        Atom atom = formula.atom();
        BigInteger coefficient = coefficientOf(atom, variable);
        if (coefficient == null) {
          return null;
        }
        if (coefficient.signum() == 0) {
          return formula;
        }
        BigInteger scale = coefficientLcm.divide(coefficient.abs());
        AffineTerm scaled = atom.term().scale(F.ZZ(scale));
        // the variable now stands for coefficientLcm times itself, so its coefficient is its sign
        AffineTerm withUnitCoefficient = scaled
            .subtract(AffineTerm.variable(variable).scale(scaled.coefficient(variable)))
            .add(AffineTerm.variable(variable)
                .scale(F.ZZ(BigInteger.valueOf(coefficient.signum()))));
        if (atom.isDivides()) {
          // scaling the divisible term without scaling the modulus would not be an equivalence
          Atom divides =
              Atom.divides(atom.modulus().multiply(scale), withUnitCoefficient, atom.isNegated());
          return divides == null ? null : Formula.atom(divides);
        }
        return Formula.atom(Atom.relation(Relation.LESS_EQUAL, withUnitCoefficient));
      }
      case AND:
      case OR: {
        List<Formula> children = new ArrayList<Formula>(formula.children().size());
        for (Formula child : formula.children()) {
          Formula transformed = transform(child, variable, coefficientLcm);
          if (transformed == null) {
            return null;
          }
          children.add(transformed);
        }
        return formula.kind() == Formula.Kind.AND ? Formula.and(children) : Formula.or(children);
      }
      default:
        return null;
    }
  }

  /** The least common multiple of the moduli of the divisibility atoms containing the variable. */
  private static BigInteger divisibilityPeriod(Formula formula, Variable variable) {
    if (formula.kind() == Formula.Kind.ATOM) {
      Atom atom = formula.atom();
      if (atom.isDivides() && atom.containsVariable(variable)) {
        return atom.modulus();
      }
      return BigInteger.ONE;
    }
    BigInteger period = BigInteger.ONE;
    for (Formula child : formula.children()) {
      period = IntegerMath.lcm(period, divisibilityPeriod(child, variable));
    }
    return period;
  }

  /**
   * The lower boundaries of the variable: from an atom <code>-x + rest &lt;= 0</code>, which is
   * <code>x &gt;= rest</code>, the term <code>rest</code>. Instantiating <code>rest + j</code> for
   * every residue <code>j</code> covers every value the variable can first take.
   */
  private static boolean collectBoundaries(Formula formula, Variable variable,
      List<AffineTerm> output) {
    if (formula.kind() == Formula.Kind.ATOM) {
      Atom atom = formula.atom();
      if (atom.isDivides()) {
        return true;
      }
      if (atom.relation() != Relation.LESS_EQUAL) {
        return false;
      }
      BigInteger coefficient = coefficientOf(atom, variable);
      if (coefficient == null) {
        return false;
      }
      if (coefficient.signum() < 0) {
        AffineTerm rest = atom.term()
            .subtract(AffineTerm.variable(variable).scale(atom.term().coefficient(variable)));
        if (!output.contains(rest)) {
          output.add(rest);
        }
      }
      return true;
    }
    if (formula.kind() == Formula.Kind.TRUE || formula.kind() == Formula.Kind.FALSE) {
      return true;
    }
    if (formula.kind() != Formula.Kind.AND && formula.kind() != Formula.Kind.OR) {
      return false;
    }
    for (Formula child : formula.children()) {
      if (!collectBoundaries(child, variable, output)) {
        return false;
      }
    }
    return true;
  }

  /**
   * The formula far below every boundary: an upper bound holds there, a lower bound does not, and
   * the divisibility atoms keep their period, so the variable is replaced by its residue.
   */
  private static Formula negativeInfinityInstance(Formula formula, Variable variable,
      AffineTerm residue) {
    switch (formula.kind()) {
      case TRUE:
      case FALSE:
        return formula;
      case ATOM: {
        Atom atom = formula.atom();
        if (atom.isDivides()) {
          return Formula.atom(atom.substitute(variable, residue));
        }
        BigInteger coefficient = coefficientOf(atom, variable);
        if (coefficient == null) {
          return null;
        }
        if (coefficient.signum() > 0) {
          return Formula.TRUE;
        }
        if (coefficient.signum() < 0) {
          return Formula.FALSE;
        }
        return formula;
      }
      case AND:
      case OR: {
        List<Formula> children = new ArrayList<Formula>(formula.children().size());
        for (Formula child : formula.children()) {
          Formula instance = negativeInfinityInstance(child, variable, residue);
          if (instance == null) {
            return null;
          }
          children.add(instance);
        }
        return (formula.kind() == Formula.Kind.AND ? Formula.and(children) : Formula.or(children))
            .normalized();
      }
      default:
        return null;
    }
  }

  /** Maximum number of disjuncts the subsumption pass compares pairwise. */
  private static final int MAX_SUBSUMPTION_CHILDREN = 64;

  /**
   * Drop a disjunct which constrains strictly more than another one.
   *
   * <p>
   * Cooper's boundary instances routinely produce <code>(A &amp;&amp; B) || A</code>, where the
   * first disjunct describes a subset of the second and contributes nothing. The test is purely
   * structural: a conjunction whose atoms are a superset of another disjunct's atoms implies it.
   */
  private static Formula dropSubsumedDisjuncts(Formula disjunction) {
    List<Formula> children = disjunction.children();
    if (children.size() < 2 || children.size() > MAX_SUBSUMPTION_CHILDREN) {
      return disjunction;
    }
    List<TreeSet<Formula>> atoms = new ArrayList<TreeSet<Formula>>(children.size());
    for (Formula child : children) {
      TreeSet<Formula> conjuncts = new TreeSet<Formula>();
      if (child.kind() == Formula.Kind.AND) {
        conjuncts.addAll(child.children());
      } else {
        conjuncts.add(child);
      }
      atoms.add(conjuncts);
    }
    List<Formula> kept = new ArrayList<Formula>(children.size());
    for (int i = 0; i < children.size(); i++) {
      boolean subsumed = false;
      for (int j = 0; j < children.size() && !subsumed; j++) {
        if (i == j) {
          continue;
        }
        // keep the first of two equal disjuncts, drop a strict superset either way
        boolean strictlyLarger = atoms.get(i).size() > atoms.get(j).size();
        if ((strictlyLarger || j < i) && atoms.get(i).containsAll(atoms.get(j))) {
          subsumed = true;
        }
      }
      if (!subsumed) {
        kept.add(children.get(i));
      }
    }
    if (kept.size() == children.size()) {
      return disjunction;
    }
    return Formula.or(kept).normalized();
  }

  /**
   * Simplifications which are valid only over the integers: a divisibility and its negation cannot
   * both hold, and a disjunction which offers every residue of one modulus is a tautology.
   */
  static Formula simplify(Formula formula) {
    switch (formula.kind()) {
      case AND:
      case OR: {
        List<Formula> children = new ArrayList<Formula>(formula.children().size());
        for (Formula child : formula.children()) {
          children.add(simplify(child));
        }
        Formula normalized =
            (formula.kind() == Formula.Kind.AND ? Formula.and(children) : Formula.or(children))
                .normalized();
        if (normalized.kind() != formula.kind()) {
          return normalized;
        }
        boolean conjunction = normalized.kind() == Formula.Kind.AND;
        // the contradiction test compares whole atoms: `2 | x` and `2 does not divide x+1` are
        // both true for an even x, so stripping the constant here would decide them contradictory
        TreeSet<Atom> positive = new TreeSet<Atom>();
        TreeSet<Atom> negative = new TreeSet<Atom>();
        // the coverage test groups by modulus and variable part, and collects the residues offered
        Map<Atom, TreeSet<BigInteger>> residues = new TreeMap<Atom, TreeSet<BigInteger>>();
        for (Formula child : normalized.children()) {
          if (child.kind() != Formula.Kind.ATOM || !child.atom().isDivides()) {
            continue;
          }
          Atom atom = child.atom();
          if (atom.isNegated()) {
            negative.add(atom);
            continue;
          }
          positive.add(atom);
          if (conjunction) {
            continue;
          }
          AffineTerm variablePart =
              atom.term().subtract(AffineTerm.constant(atom.term().constant()));
          Atom group = Atom.divides(atom.modulus(), variablePart, false);
          if (group == null) {
            continue;
          }
          TreeSet<BigInteger> seen = residues.get(group);
          if (seen == null) {
            seen = new TreeSet<BigInteger>();
            residues.put(group, seen);
          }
          seen.add(IntegerMath.euclideanMod(atom.term().constant().numerator().toBigNumerator(),
              atom.modulus()));
        }
        for (Atom atom : negative) {
          Atom twin = Atom.divides(atom.modulus(), atom.term(), false);
          if (twin != null && positive.contains(twin)) {
            // a divisibility and its own negation cannot both hold, and one of them always does
            return conjunction ? Formula.FALSE : Formula.TRUE;
          }
        }
        for (Map.Entry<Atom, TreeSet<BigInteger>> entry : residues.entrySet()) {
          if (BigInteger.valueOf(entry.getValue().size()).equals(entry.getKey().modulus())) {
            // every residue class of the modulus is offered, so one disjunct always holds
            return Formula.TRUE;
          }
        }
        return conjunction ? normalized : dropSubsumedDisjuncts(normalized);
      }
      case EXISTS:
      case FORALL:
        return Formula.quantified(formula.kind(), formula.boundVariables(),
            simplify(formula.body())).normalized();
      case NOT:
        return Formula.not(simplify(formula.children().get(0))).normalized();
      default:
        return formula;
    }
  }
}
