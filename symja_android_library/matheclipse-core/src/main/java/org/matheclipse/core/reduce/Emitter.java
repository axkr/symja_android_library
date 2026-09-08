package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * Deterministic conversion of the linear IR back into a Symja expression.
 *
 * <p>
 * The decision procedures produce formulas; this class produces the surface form Wolfram Language
 * uses for them: a relation solved for its target, a congruence written with <code>Mod</code>, a
 * conjunction whose conditions on the parameters come first, and an explicit domain membership on
 * a solution set which is not finite.
 */
public final class Emitter {

  private Emitter() {}

  /** Emit a formula without preferring any variable. */
  public static IExpr formula(Formula formula) {
    return formula(formula, Collections.<Variable>emptyList());
  }

  /**
   * Emit a formula, isolating the given target variables.
   *
   * @param targets the variables of the reduction, in the order the caller asked for them
   */
  public static IExpr formula(Formula formula, List<Variable> targets) {
    switch (formula.kind()) {
      case TRUE:
        return S.True;
      case FALSE:
        return S.False;
      case ATOM:
        return atom(formula.atom(), targets);
      case NOT:
        return F.Not(formula(formula.children().get(0), targets));
      case AND:
      case OR: {
        List<Formula> children = new ArrayList<Formula>(formula.children());
        if (formula.kind() == Formula.Kind.AND && !targets.isEmpty()) {
          // a condition on the parameters is a premise of the solution set, so it reads first
          Collections.sort(children, (left, right) -> Boolean.compare(mentions(left, targets),
              mentions(right, targets)));
        }
        IASTAppendable result = formula.kind() == Formula.Kind.AND
            ? F.ast(S.And, children.size())
            : F.ast(S.Or, children.size());
        for (Formula child : children) {
          result.append(formula(child, targets));
        }
        return result;
      }
      default: {
        IASTAppendable variables = F.ListAlloc(formula.boundVariables().size());
        for (Variable variable : formula.boundVariables()) {
          variables.append(variable.symbol());
        }
        IExpr specification = variables.argSize() == 1 ? variables.arg1() : variables;
        return F.binaryAST2(formula.kind() == Formula.Kind.EXISTS ? S.Exists : S.ForAll,
            specification, formula(formula.body(), targets));
      }
    }
  }

  private static boolean mentions(Formula formula, List<Variable> targets) {
    for (Variable target : targets) {
      if (formula.containsVariable(target)) {
        return true;
      }
    }
    return false;
  }

  private static IExpr atom(Atom atom, List<Variable> targets) {
    if (atom.isDivides()) {
      return divides(atom, targets);
    }
    Variable isolated = chooseIsolatedVariable(atom.term(), targets);
    if (isolated != null) {
      IRational coefficient = atom.term().coefficient(isolated);
      AffineTerm rest = atom.term()
          .subtract(AffineTerm.variable(isolated).scale(coefficient));
      AffineTerm boundary = rest.scale(coefficient.inverse().negate());
      Relation relation = coefficient.complexSign() < 0 ? atom.relation().reversed()
          : atom.relation();
      return comparison(relation, isolated.symbol(), term(boundary));
    }
    return comparison(atom.relation(), term(atom.term()), F.C0);
  }

  /**
   * A congruence on a single variable with unit coefficient is written the way it was asked, with
   * <code>Mod</code>. Anything else keeps the <code>Divisible</code> form.
   */
  private static IExpr divides(Atom atom, List<Variable> targets) {
    AffineTerm variablePart = atom.term().subtract(AffineTerm.constant(atom.term().constant()));
    if (variablePart.coefficients().size() == 1 && atom.term().isIntegral()) {
      Variable variable = variablePart.variables().iterator().next();
      BigInteger coefficient = variablePart.coefficient(variable).numerator().toBigNumerator();
      if (coefficient.abs().equals(BigInteger.ONE)) {
        BigInteger constant = atom.term().constant().numerator().toBigNumerator();
        BigInteger residue = coefficient.signum() > 0 ? constant.negate() : constant;
        residue = IntegerMath.euclideanMod(residue, atom.modulus());
        IExpr mod = F.Mod(variable.symbol(), F.ZZ(atom.modulus()));
        return atom.isNegated() ? F.Unequal(mod, F.ZZ(residue)) : F.Equal(mod, F.ZZ(residue));
      }
    }
    IExpr divisible = F.Divisible(term(atom.term()), F.ZZ(atom.modulus()));
    return atom.isNegated() ? F.Not(divisible) : divisible;
  }

  /**
   * The variable a relation is solved for: the first requested target which occurs, or the only
   * variable of the term when no target occurs in it.
   */
  private static Variable chooseIsolatedVariable(AffineTerm term, List<Variable> targets) {
    for (Variable target : targets) {
      if (!term.coefficient(target).isZero()) {
        return target;
      }
    }
    return term.coefficients().size() == 1 ? term.variables().iterator().next() : null;
  }

  private static IExpr comparison(Relation relation, IExpr left, IExpr right) {
    switch (relation) {
      case EQUAL:
        return F.Equal(left, right);
      case NOT_EQUAL:
        return F.Unequal(left, right);
      case LESS:
        return F.Less(left, right);
      case LESS_EQUAL:
        return F.LessEqual(left, right);
      case GREATER:
        return F.Greater(left, right);
      default:
        return F.GreaterEqual(left, right);
    }
  }

  /** Emit an affine term as a sum, with the constant last. */
  public static IExpr term(AffineTerm term) {
    IASTAppendable sum = F.PlusAlloc(term.coefficients().size() + 1);
    for (Map.Entry<Variable, IRational> entry : term.coefficients().entrySet()) {
      IRational coefficient = entry.getValue();
      IExpr variable = entry.getKey().symbol();
      sum.append(coefficient.isOne() ? variable : F.Times(coefficient, variable));
    }
    if (!term.constant().isZero() || sum.argSize() == 0) {
      sum.append(term.constant());
    }
    return sum.argSize() == 1 ? sum.arg1() : sum;
  }

  /**
   * Add the domain membership of every target which the solution set still constrains.
   *
   * <p>
   * A finite set names its members and needs no membership; an unbounded one, such as a ray or a
   * residue class, describes integers only together with <code>Element(x, Integers)</code>, which
   * is how Wolfram Language reports it.
   */
  public static IExpr withDomainConditions(IExpr expression, List<Variable> targets,
      IntegerDomain domain) {
    if (expression.isTrue() || expression.isFalse()) {
      return expression;
    }
    IASTAppendable conditions = F.ast(S.And, targets.size() + 1);
    for (Variable target : targets) {
      if (!expression.isFree(target.symbol())) {
        conditions.append(F.Element(target.symbol(), domain.symbol()));
      }
    }
    if (conditions.argSize() == 0) {
      return expression;
    }
    if (expression.isAnd()) {
      conditions.appendArgs((IAST) expression);
    } else {
      conditions.append(expression);
    }
    return conditions;
  }

  /**
   * The Wolfram Language form of a parametrized integer solution set.
   *
   * <p>
   * A variable the system never mentions is unconstrained and keeps its own membership; the fresh
   * parameters share one membership written with <code>Alternatives</code>; then every solved
   * variable is given in terms of those parameters. So
   * <code>Reduce(2*x == 4*y, {x, y}, Integers)</code> is
   * <code>C(1)&isin;Integers &amp;&amp; x == 2*C(1) &amp;&amp; y == C(1)</code>.
   *
   * @param solution the parametrized solution set
   * @param untouched the requested variables which do not occur in the system, in order
   * @param domain the domain of the reduction
   */
  public static IExpr latticeReduceForm(LatticeSolver.Solution solution, List<Variable> untouched,
      IntegerDomain domain) {
    return latticeReduceForm(solution, untouched, domain, F.NIL);
  }

  /**
   * The same form with a further condition on the generated parameters, which is what the
   * constraints beside the equations become once the parametrization is substituted into them.
   */
  public static IExpr latticeReduceForm(LatticeSolver.Solution solution, List<Variable> untouched,
      IntegerDomain domain, IExpr parameterCondition) {
    IASTAppendable conjuncts = F.ast(S.And, untouched.size() + solution.variables().size() + 2);
    for (Variable variable : untouched) {
      conjuncts.append(F.Element(variable.symbol(), domain.symbol()));
    }
    IExpr membership = parameterMembership(solution.parameterCount(), domain);
    if (membership.isPresent()) {
      conjuncts.append(membership);
    }
    if (parameterCondition.isPresent() && !parameterCondition.isTrue()) {
      if (parameterCondition.isAnd()) {
        conjuncts.appendArgs((IAST) parameterCondition);
      } else {
        conjuncts.append(parameterCondition);
      }
    }
    for (int index = 0; index < solution.variables().size(); index++) {
      conjuncts
          .append(F.Equal(solution.variables().get(index).symbol(), latticeValue(solution, index)));
    }
    return conjuncts.argSize() == 1 ? conjuncts.arg1() : conjuncts;
  }

  /**
   * The same solution set in the shape {@code Solve} returns: one rule per variable, each value
   * carrying the condition on the generated parameters.
   */
  public static IExpr latticeSolveRules(LatticeSolver.Solution solution, List<Variable> untouched,
      IntegerDomain domain) {
    IASTAppendable conditions = F.ast(S.And, untouched.size() + 1);
    for (Variable variable : untouched) {
      conditions.append(F.Element(variable.symbol(), domain.symbol()));
    }
    IExpr membership = parameterMembership(solution.parameterCount(), domain);
    if (membership.isPresent()) {
      conditions.append(membership);
    }
    IExpr condition = conditions.argSize() == 0 ? F.NIL
        : (conditions.argSize() == 1 ? conditions.arg1() : conditions);
    IASTAppendable rules = F.ListAlloc(solution.variables().size());
    for (int index = 0; index < solution.variables().size(); index++) {
      IExpr value = latticeValue(solution, index);
      rules.append(F.Rule(solution.variables().get(index).symbol(),
          condition.isNIL() ? value : F.ConditionalExpression(value, condition)));
    }
    return F.list(rules);
  }

  /**
   * A finite solution set as a disjunction of solved equations, which is how <code>Reduce</code>
   * reports it: <code>(x == 1 &amp;&amp; y == 4) || (x == 2 &amp;&amp; y == 3)</code>.
   */
  public static IExpr tuplesToOr(IntegerSolveResult result, IntegerDomain domain) {
    List<BigInteger[]> solutions = result.solutions();
    if (solutions.isEmpty()) {
      return S.False;
    }
    IASTAppendable disjuncts = F.ast(S.Or, solutions.size());
    for (BigInteger[] solution : solutions) {
      IASTAppendable conjuncts = F.ast(S.And, solution.length);
      for (int index = 0; index < solution.length; index++) {
        conjuncts.append(F.Equal(result.variables().get(index).symbol(), F.ZZ(solution[index])));
      }
      disjuncts.append(conjuncts.argSize() == 1 ? conjuncts.arg1() : conjuncts);
    }
    IExpr disjunction = disjuncts.argSize() == 1 ? disjuncts.arg1() : disjuncts;
    if (result.untouched().isEmpty()) {
      return disjunction;
    }
    IASTAppendable conjuncts = F.ast(S.And, result.untouched().size() + 1);
    for (Variable variable : result.untouched()) {
      conjuncts.append(F.Element(variable.symbol(), domain.symbol()));
    }
    conjuncts.append(disjunction);
    return conjuncts;
  }

  /**
   * The same finite solution set as the list of rules {@code Solve} returns. A variable the
   * condition does not mention is unconstrained, and travels as the condition of every value.
   */
  public static IExpr tuplesToRules(IntegerSolveResult result, IntegerDomain domain) {
    IExpr condition = F.NIL;
    if (!result.untouched().isEmpty()) {
      IASTAppendable conditions = F.ast(S.And, result.untouched().size());
      for (Variable variable : result.untouched()) {
        conditions.append(F.Element(variable.symbol(), domain.symbol()));
      }
      condition = conditions.argSize() == 1 ? conditions.arg1() : conditions;
    }
    IASTAppendable rules = F.ListAlloc(result.solutions().size());
    for (BigInteger[] solution : result.solutions()) {
      IASTAppendable tuple = F.ListAlloc(solution.length);
      for (int index = 0; index < solution.length; index++) {
        IExpr value = F.ZZ(solution[index]);
        tuple.append(F.Rule(result.variables().get(index).symbol(),
            condition.isNIL() ? value : F.ConditionalExpression(value, condition)));
      }
      rules.append(tuple);
    }
    return rules;
  }

  /** <code>Element(C(1), Integers)</code>, or one membership for all parameters at once. */
  private static IExpr parameterMembership(int count, IntegerDomain domain) {
    if (count == 0) {
      return F.NIL;
    }
    if (count == 1) {
      return F.Element(F.C(1), domain.symbol());
    }
    IASTAppendable alternatives = F.ast(S.Alternatives, count);
    for (int index = 1; index <= count; index++) {
      alternatives.append(F.C(index));
    }
    return F.Element(alternatives, domain.symbol());
  }

  /** The value of one variable of the solution set, as <code>offset + basis . C</code>. */
  private static IExpr latticeValue(LatticeSolver.Solution solution, int index) {
    IASTAppendable sum = F.PlusAlloc(solution.parameterCount() + 1);
    BigInteger offset = solution.offset()[index];
    if (offset.signum() != 0) {
      sum.append(F.ZZ(offset));
    }
    for (int row = 0; row < solution.parameterCount(); row++) {
      BigInteger coefficient = solution.basis()[row][index];
      if (coefficient.signum() == 0) {
        continue;
      }
      IExpr parameter = F.C(row + 1);
      sum.append(
          coefficient.equals(BigInteger.ONE) ? parameter : F.Times(F.ZZ(coefficient), parameter));
    }
    if (sum.argSize() == 0) {
      return F.C0;
    }
    return sum.argSize() == 1 ? sum.arg1() : sum;
  }
}
