package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Transactional lowering from a Symja expression to the linear formula IR.
 *
 * <p>
 * Lowering is all or nothing: an expression which contains anything outside the supported grammar
 * makes the whole request fail with <code>null</code>. A partially interpreted formula would drop
 * an atom and turn an unsupported problem into a confidently wrong answer, so no sub expression is
 * ever approximated or skipped.
 *
 * <p>
 * The supported grammar is: exact rational affine terms; the six comparison relations, both binary
 * and chained; <code>Inequality</code>; <code>Mod(term, m) == r</code> and its negation;
 * <code>Divisible</code>; <code>And</code>, <code>Or</code>, <code>Not</code>, <code>Xor</code>,
 * <code>Nand</code>, <code>Nor</code>, <code>Implies</code>, <code>Equivalent</code>; and nested
 * <code>Exists</code> and <code>ForAll</code>.
 */
public final class Lowering {

  /** A complete <code>Reduce</code>/<code>Solve</code> request in the IR. */
  public static final class LinearRequest {
    private final Formula formula;
    private final List<Variable> targets;
    private final IntegerDomain domain;

    LinearRequest(Formula formula, List<Variable> targets, IntegerDomain domain) {
      this.formula = formula;
      this.targets = targets;
      this.domain = domain;
    }

    public Formula formula() {
      return formula;
    }

    public List<Variable> targets() {
      return targets;
    }

    public IntegerDomain domain() {
      return domain;
    }
  }

  private final Deque<Map<ISymbol, Variable>> scopes = new ArrayDeque<Map<ISymbol, Variable>>();

  private int nextBinder;

  private Lowering() {}

  /** Lower a condition, or return <code>null</code> if it is outside the grammar. */
  public static Formula lower(IExpr expr) {
    Formula formula = new Lowering().formula(expr);
    return formula == null ? null : formula.nnf().normalized();
  }

  /**
   * Lower a complete request.
   *
   * @param condition the condition to reduce
   * @param variables the variables of the reduction, all of them plain symbols
   * @param domain the domain of the reduction
   * @return the request, or <code>null</code> if the condition or the variable specification is
   *         outside the grammar
   */
  public static LinearRequest request(IExpr condition, IAST variables, IntegerDomain domain) {
    if (domain == null || variables == null) {
      return null;
    }
    List<Variable> targets = new ArrayList<Variable>(variables.argSize());
    TreeSet<Variable> seen = new TreeSet<Variable>();
    for (int i = 1; i < variables.size(); i++) {
      IExpr variable = variables.get(i);
      if (!variable.isSymbol() || variable.isBuiltInSymbol()) {
        return null;
      }
      Variable target = Variable.free((ISymbol) variable);
      if (!seen.add(target)) {
        return null;
      }
      targets.add(target);
    }
    if (targets.isEmpty()) {
      return null;
    }
    Formula formula = lower(condition);
    return formula == null ? null : new LinearRequest(formula, targets, domain);
  }

  // ---------------------------------------------------------------- terms

  private Variable resolve(ISymbol symbol) {
    for (Map<ISymbol, Variable> scope : scopes) {
      Variable bound = scope.get(symbol);
      if (bound != null) {
        return bound;
      }
    }
    return Variable.free(symbol);
  }

  private AffineTerm term(IExpr expr) {
    if (expr.isRational()) {
      return AffineTerm.constant((IRational) expr);
    }
    if (expr.isSymbol()) {
      if (expr.isBuiltInSymbol()) {
        // a built-in constant such as Pi or E is not an affine variable
        return null;
      }
      return AffineTerm.variable(resolve((ISymbol) expr));
    }
    if (expr.isPlus()) {
      IAST plus = (IAST) expr;
      AffineTerm sum = AffineTerm.ZERO;
      for (int i = 1; i < plus.size(); i++) {
        AffineTerm summand = term(plus.get(i));
        if (summand == null) {
          return null;
        }
        sum = sum.add(summand);
      }
      return sum;
    }
    if (expr.isTimes()) {
      IAST times = (IAST) expr;
      AffineTerm product = AffineTerm.constant(org.matheclipse.core.expression.F.C1);
      for (int i = 1; i < times.size(); i++) {
        AffineTerm factor = term(times.get(i));
        if (factor == null) {
          return null;
        }
        product = product.checkedMultiply(factor);
        if (product == null) {
          return null;
        }
      }
      return product;
    }
    if (expr.isPower()) {
      IExpr exponent = expr.exponent();
      if (exponent.isZero()) {
        return AffineTerm.constant(org.matheclipse.core.expression.F.C1);
      }
      if (exponent.isOne()) {
        return term(expr.base());
      }
      return null;
    }
    return null;
  }

  // ------------------------------------------------------------- formulas

  private Formula formula(IExpr expr) {
    if (expr.isTrue()) {
      return Formula.TRUE;
    }
    if (expr.isFalse()) {
      return Formula.FALSE;
    }
    if (!expr.isAST()) {
      return null;
    }
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    if (head == S.List) {
      return conjunction(ast, 1);
    }
    if (head == S.And) {
      return conjunction(ast, 1);
    }
    if (head == S.Or) {
      return disjunction(ast, 1);
    }
    if (head == S.Not && ast.isAST1()) {
      Formula inner = formula(ast.arg1());
      return inner == null ? null : Formula.not(inner);
    }
    if (head == S.Nand) {
      Formula inner = conjunction(ast, 1);
      return inner == null ? null : Formula.not(inner);
    }
    if (head == S.Nor) {
      Formula inner = disjunction(ast, 1);
      return inner == null ? null : Formula.not(inner);
    }
    if (head == S.Implies && ast.isAST2()) {
      Formula premise = formula(ast.arg1());
      Formula conclusion = formula(ast.arg2());
      if (premise == null || conclusion == null) {
        return null;
      }
      return Formula.or(Formula.not(premise), conclusion);
    }
    if (head == S.Xor && ast.argSize() >= 2) {
      Formula result = formula(ast.arg1());
      if (result == null) {
        return null;
      }
      for (int i = 2; i < ast.size(); i++) {
        Formula next = formula(ast.get(i));
        if (next == null) {
          return null;
        }
        result = Formula.or(Formula.and(result, Formula.not(next)),
            Formula.and(Formula.not(result), next));
      }
      return result;
    }
    if (head == S.Equivalent && ast.argSize() >= 2) {
      List<Formula> parts = new ArrayList<Formula>();
      Formula previous = formula(ast.arg1());
      if (previous == null) {
        return null;
      }
      for (int i = 2; i < ast.size(); i++) {
        Formula next = formula(ast.get(i));
        if (next == null) {
          return null;
        }
        parts.add(Formula.or(Formula.and(previous, next),
            Formula.and(Formula.not(previous), Formula.not(next))));
        previous = next;
      }
      return Formula.and(parts);
    }
    if ((head == S.Exists || head == S.ForAll) && (ast.isAST2() || ast.isAST3())) {
      return quantified(ast, head == S.ForAll);
    }
    if (head == S.Element && ast.isAST2()) {
      return element(ast.arg2());
    }
    if (head == S.Divisible && ast.isAST2()) {
      return divisible(ast.arg1(), ast.arg2(), false);
    }
    if (head == S.Inequality && ast.argSize() >= 5 && (ast.argSize() % 2) == 1) {
      List<Formula> parts = new ArrayList<Formula>();
      for (int i = 2; i < ast.size(); i += 2) {
        Relation relation = relationOf(ast.get(i));
        if (relation == null) {
          return null;
        }
        Formula part = comparison(relation, ast.get(i - 1), ast.get(i + 1));
        if (part == null) {
          return null;
        }
        parts.add(part);
      }
      return Formula.and(parts);
    }
    Relation relation = relationOf(head);
    if (relation != null && ast.argSize() >= 2) {
      return relationChain(relation, ast);
    }
    return null;
  }

  private Formula conjunction(IAST ast, int from) {
    List<Formula> parts = new ArrayList<Formula>(ast.argSize());
    for (int i = from; i < ast.size(); i++) {
      Formula part = formula(ast.get(i));
      if (part == null) {
        return null;
      }
      parts.add(part);
    }
    return Formula.and(parts);
  }

  private Formula disjunction(IAST ast, int from) {
    List<Formula> parts = new ArrayList<Formula>(ast.argSize());
    for (int i = from; i < ast.size(); i++) {
      Formula part = formula(ast.get(i));
      if (part == null) {
        return null;
      }
      parts.add(part);
    }
    return Formula.or(parts);
  }

  private Formula quantified(IAST ast, boolean forAll) {
    IExpr specification = ast.arg1();
    IAST variables = specification.makeList();
    List<Variable> bound = new ArrayList<Variable>(variables.argSize());
    Map<ISymbol, Variable> scope = new HashMap<ISymbol, Variable>();
    for (int i = 1; i < variables.size(); i++) {
      IExpr variable = variables.get(i);
      if (!variable.isSymbol() || variable.isBuiltInSymbol()) {
        return null;
      }
      ISymbol symbol = (ISymbol) variable;
      if (scope.containsKey(symbol)) {
        return null;
      }
      Variable binder = Variable.bound(symbol, nextBinder++);
      scope.put(symbol, binder);
      bound.add(binder);
    }
    if (bound.isEmpty()) {
      return null;
    }
    scopes.push(scope);
    try {
      Formula body;
      if (ast.isAST3()) {
        Formula condition = formula(ast.arg2());
        Formula inner = formula(ast.arg3());
        if (condition == null || inner == null) {
          return null;
        }
        // ForAll(vars, cond, expr) is cond => expr ; Exists(vars, cond, expr) is cond && expr
        body = forAll ? Formula.or(Formula.not(condition), inner) : Formula.and(condition, inner);
      } else {
        body = formula(ast.arg2());
      }
      if (body == null) {
        return null;
      }
      return Formula.quantified(forAll ? Formula.Kind.FORALL : Formula.Kind.EXISTS, bound, body);
    } finally {
      scopes.pop();
    }
  }

  /**
   * An <code>Element</code> membership. Lowering runs over a discrete domain, so a membership in a
   * domain which contains every integer is a tautology; anything else rejects the request.
   */
  private Formula element(IExpr domain) {
    if (domain == S.Integers || domain == S.Rationals || domain == S.Reals
        || domain == S.Complexes || domain == S.Algebraics) {
      return Formula.TRUE;
    }
    return null;
  }

  private Formula divisible(IExpr dividend, IExpr divisor, boolean negated) {
    if (!divisor.isInteger()) {
      return null;
    }
    BigInteger modulus = ((IRational) divisor).numerator().toBigNumerator();
    if (modulus.signum() == 0) {
      return null;
    }
    AffineTerm term = term(dividend);
    if (term == null) {
      return null;
    }
    return Formula.atom(Atom.divides(modulus.abs(), term, negated));
  }

  private Formula relationChain(Relation relation, IAST ast) {
    List<Formula> parts = new ArrayList<Formula>();
    if (relation == Relation.NOT_EQUAL) {
      // Unequal(a, b, c) means the arguments are pairwise distinct
      for (int i = 1; i < ast.size(); i++) {
        for (int j = i + 1; j < ast.size(); j++) {
          Formula part = comparison(relation, ast.get(i), ast.get(j));
          if (part == null) {
            return null;
          }
          parts.add(part);
        }
      }
      return Formula.and(parts);
    }
    for (int i = 1; i + 1 < ast.size(); i++) {
      Formula part = comparison(relation, ast.get(i), ast.get(i + 1));
      if (part == null) {
        return null;
      }
      parts.add(part);
    }
    return Formula.and(parts);
  }

  private Formula comparison(Relation relation, IExpr left, IExpr right) {
    if (containsMod(left) || containsMod(right)) {
      if (relation != Relation.EQUAL && relation != Relation.NOT_EQUAL) {
        // an ordered comparison of a residue is not a congruence class
        return null;
      }
      return congruence(relation, left, right);
    }
    AffineTerm leftTerm = term(left);
    AffineTerm rightTerm = term(right);
    if (leftTerm == null || rightTerm == null) {
      return null;
    }
    return Formula.atom(Atom.relation(relation, leftTerm.subtract(rightTerm)));
  }

  /**
   * Lower a comparison which mentions <code>Mod</code> into a congruence.
   *
   * <p>
   * The comparison need not be written as <code>Mod(t, m) == r</code>: an equation is normally
   * rearranged to <code>-1 + Mod(x, 3) == 0</code> before it reaches here, so the residue is
   * collected from both sides. <code>Mod</code> always returns a value in <code>[0, m)</code>, so
   * a residue outside that range decides the atom rather than describing a congruence class.
   */
  private Formula congruence(Relation relation, IExpr left, IExpr right) {
    ModSplit leftSplit = split(left);
    ModSplit rightSplit = split(right);
    if (leftSplit == null || rightSplit == null) {
      return null;
    }
    ModSplit withMod;
    AffineTerm otherSide;
    if (leftSplit.mod != null && rightSplit.mod == null) {
      withMod = leftSplit;
      otherSide = rightSplit.affine;
    } else if (rightSplit.mod != null && leftSplit.mod == null) {
      withMod = rightSplit;
      otherSide = leftSplit.affine;
    } else {
      // no Mod at all, or one on each side
      return null;
    }
    if (!withMod.mod.arg2().isInteger()) {
      return null;
    }
    BigInteger modulus = ((IRational) withMod.mod.arg2()).numerator().toBigNumerator();
    if (modulus.signum() <= 0) {
      // a negative or zero modulus changes the sign convention of Mod; stay out of it
      return null;
    }
    // sign * Mod(t, m) == otherSide - affine
    AffineTerm residueTerm = otherSide.subtract(withMod.affine);
    if (withMod.negative) {
      residueTerm = residueTerm.negate();
    }
    boolean negated = relation == Relation.NOT_EQUAL;
    if (!residueTerm.isConstant()) {
      return null;
    }
    IRational residue = residueTerm.constant();
    if (!residue.isInteger()) {
      return Formula.of(negated);
    }
    BigInteger value = residue.numerator().toBigNumerator();
    if (value.signum() < 0 || value.compareTo(modulus) >= 0) {
      return Formula.of(negated);
    }
    AffineTerm term = term(withMod.mod.arg1());
    if (term == null) {
      return null;
    }
    return Formula.atom(Atom.divides(modulus, term.subtract(AffineTerm.integer(value)), negated));
  }

  /** An expression split into an affine part and at most one <code>Mod</code> term. */
  private static final class ModSplit {
    private final AffineTerm affine;
    private final IAST mod;
    private final boolean negative;

    ModSplit(AffineTerm affine, IAST mod, boolean negative) {
      this.affine = affine;
      this.mod = mod;
      this.negative = negative;
    }
  }

  /**
   * Split an expression into an affine term plus at most one <code>Mod</code> summand with
   * coefficient <code>1</code> or <code>-1</code>. Anything else, in particular two residues or a
   * scaled one, is outside the grammar.
   */
  private ModSplit split(IExpr expr) {
    if (!containsMod(expr)) {
      AffineTerm affine = term(expr);
      return affine == null ? null : new ModSplit(affine, null, false);
    }
    if (expr.isAST(S.Mod, 3)) {
      return new ModSplit(AffineTerm.ZERO, (IAST) expr, false);
    }
    if (expr.isTimes() && expr.isAST2() && expr.first().isMinusOne()
        && expr.second().isAST(S.Mod, 3)) {
      return new ModSplit(AffineTerm.ZERO, (IAST) expr.second(), true);
    }
    if (!expr.isPlus()) {
      return null;
    }
    IAST plus = (IAST) expr;
    AffineTerm affine = AffineTerm.ZERO;
    IAST mod = null;
    boolean negative = false;
    for (int i = 1; i < plus.size(); i++) {
      IExpr summand = plus.get(i);
      if (!containsMod(summand)) {
        AffineTerm part = term(summand);
        if (part == null) {
          return null;
        }
        affine = affine.add(part);
        continue;
      }
      if (mod != null) {
        // two residues in one comparison are not one congruence class
        return null;
      }
      if (summand.isAST(S.Mod, 3)) {
        mod = (IAST) summand;
      } else if (summand.isTimes() && summand.isAST2() && summand.first().isMinusOne()
          && summand.second().isAST(S.Mod, 3)) {
        mod = (IAST) summand.second();
        negative = true;
      } else {
        return null;
      }
    }
    return new ModSplit(affine, mod, negative);
  }

  private static boolean containsMod(IExpr expr) {
    return !expr.isFree(S.Mod);
  }

  private static Relation relationOf(IExpr head) {
    if (head == S.Equal) {
      return Relation.EQUAL;
    }
    if (head == S.Unequal) {
      return Relation.NOT_EQUAL;
    }
    if (head == S.Less) {
      return Relation.LESS;
    }
    if (head == S.LessEqual) {
      return Relation.LESS_EQUAL;
    }
    if (head == S.Greater) {
      return Relation.GREATER;
    }
    if (head == S.GreaterEqual) {
      return Relation.GREATER_EQUAL;
    }
    return null;
  }
}
