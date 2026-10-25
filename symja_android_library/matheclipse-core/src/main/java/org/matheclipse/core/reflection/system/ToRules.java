package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 *
 *
 * <pre>
 * ToRules(eqns)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * converts the logical combination of equations <code>eqns</code>, in the form which
 * <code>Reduce</code> and <code>Roots</code> give, into a sequence of lists of rules: every
 * alternative of an <code>Or</code> gives one list, and every equation <code>lhs == rhs</code> of a
 * conjunction one rule <code>lhs -&gt; rhs</code> in it. Relations which are no equations, like
 * inequalities or conditions on the domain of a parameter, are left out.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; {ToRules(x==1&amp;&amp;y==2 || x==3&amp;&amp;y==4)}
 * {{x-&gt;1,y-&gt;2},{x-&gt;3,y-&gt;4}}
 *
 * &gt;&gt; ToRules(x==1&amp;&amp;y==2)
 * {x-&gt;1,y-&gt;2}
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="Reduce.md">Reduce</a>, <a href="Roots.md">Roots</a>, <a href="Solve.md">Solve</a>
 */
public class ToRules extends AbstractFunctionEvaluator {

  public ToRules() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    return toRules(ast.arg1(), engine);
  }

  /**
   * The lists of rules of a logical combination of equations.
   *
   * @param eqns an equation, a conjunction or a list of relations, a disjunction of these,
   *        {@link S#True} or {@link S#False}
   * @param engine the evaluation engine
   * @return the list of rules of a single conjunction, the {@link S#Sequence} of the lists of rules
   *         of the alternatives of a disjunction, or {@link F#NIL} if <code>eqns</code> is no
   *         logical combination of equations
   */
  public static IExpr toRules(IExpr eqns, EvalEngine engine) {
    if (eqns.isFalse()) {
      return F.Sequence();
    }
    if (eqns.isOr()) {
      IAST alternatives = (IAST) eqns;
      IASTAppendable sequence = F.ast(S.Sequence, alternatives.argSize());
      for (IExpr alternative : alternatives) {
        if (alternative.isFalse()) {
          continue;
        }
        IExpr rules = toRules(alternative, engine);
        if (rules.isNIL()) {
          return F.NIL;
        }
        if (rules.isSequence()) {
          sequence.appendArgs((IAST) rules);
        } else {
          sequence.append(rules);
        }
      }
      return sequence.isAST1() ? sequence.arg1() : sequence;
    }
    if (eqns.isAnd() || eqns.isList()) {
      IAST conjunction = (IAST) eqns;
      if (conjunction.exists(x -> x.isOr())) {
        // distribute the conjunction over its alternatives
        IExpr expanded = engine.evaluate(F.unaryAST1(S.LogicalExpand,
            eqns.isList() ? conjunction.setAtCopy(0, S.And) : conjunction));
        if (expanded.isOr()) {
          return toRules(expanded, engine);
        }
        if (expanded.equals(eqns) || expanded.isAnd() && ((IAST) expanded).exists(x -> x.isOr())) {
          return F.NIL;
        }
        return toRules(expanded, engine);
      }
      IASTAppendable rules = F.ListAlloc(conjunction.argSize());
      for (IExpr relation : conjunction) {
        if (relation.isEqual()) {
          rules.append(F.Rule(relation.first(), relation.second()));
        } else if (relation.isFalse()) {
          return F.Sequence();
        } else if (!relation.isTrue() && !relation.isRelational() && !relation.isAST(S.Unequal)
            && !relation.isAST(S.Element, 3) && !relation.isAST(S.NotElement, 3)) {
          return F.NIL;
        }
      }
      return rules;
    }
    if (eqns.isTrue()) {
      return F.CEmptyList;
    }
    if (eqns.isEqual()) {
      return F.list(F.Rule(eqns.first(), eqns.second()));
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }
}
