package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Representation of a differential-field tower {@code Q(x) = K0 &sub; K0(t1) &sub; ... &sub;
 * Kn-1(tn)} &mdash; the structural spine of the transcendental Risch algorithm.
 *
 * <p>
 * Each transcendental building block {@code Log(u)}, {@code Exp(u)} or {@code Tan(u)} of the
 * integrand becomes a <em>monomial</em> {@code t_i} (a fresh symbol treated as transcendental over
 * the previous field), together with its {@link MonomialType type} and its derivation
 * {@code D(t_i)}, expressed over the lower part of the tower:
 * <ul>
 * <li><b>primitive</b> ({@code Log(u)}): {@code D(t) = D(u)/u} in {@code K};
 * <li><b>hyperexponential</b> ({@code Exp(u)}): {@code D(t) = D(u)*t};
 * <li><b>hypertangent</b> ({@code Tan(u)}): {@code D(t) = D(u)*(1 + t^2)}.
 * </ul>
 * {@link #build} rewrites an integrand into <em>tower form</em> (a rational/polynomial expression
 * in {@code x, t1, ..., tn}); {@link #derivation} applies the derivation {@code D} to a tower-form
 * element by the chain rule {@code D(f) = &part;f/&part;x + &sum;_i (&part;f/&part;t_i) D(t_i)}.
 * 
 */
public class DifferentialTower {

  public enum MonomialType {
    PRIMITIVE, HYPEREXPONENTIAL, HYPERTANGENT
  }

  /** One tower monomial {@code t_i} standing for {@code Log/Exp/Tan(argument)}. */
  public static final class Monomial {
    public final ISymbol symbol;
    public final IExpr argument;
    public final MonomialType type;
    public final IExpr derivative; // D(symbol), in terms of x and lower monomials
    public final IExpr original; // the Log/Exp/Tan(argument) expression it replaces

    Monomial(ISymbol symbol, IExpr argument, MonomialType type, IExpr derivative, IExpr original) {
      this.symbol = symbol;
      this.argument = argument;
      this.type = type;
      this.derivative = derivative;
      this.original = original;
    }
  }

  private final IExpr baseVariable;
  private final List<Monomial> monomials = new ArrayList<>();
  private IExpr towerForm;

  private DifferentialTower(IExpr baseVariable) {
    this.baseVariable = baseVariable;
  }

  public List<Monomial> monomials() {
    return monomials;
  }

  /** The input expression rewritten in terms of {@code x} and the monomial symbols. */
  public IExpr towerForm() {
    return towerForm;
  }

  /**
   * Build the tower of {@code Log/Exp/Tan} monomials occurring in {@code expr} (inner monomials
   * first) and rewrite {@code expr} into tower form.
   */
  public static DifferentialTower build(IExpr expr, IExpr x, EvalEngine engine) {
    DifferentialTower tower = new DifferentialTower(x);
    Set<IExpr> collector = new LinkedHashSet<>();
    collectMonomials(expr, x, collector);
    // Order inner/smaller monomials first so a monomial's derivative can be expressed over the
    // already-added lower part of the tower.
    Set<IExpr> ordered = new TreeSet<>(
        Comparator.comparingLong(IExpr::leafCount).thenComparing(Comparator.naturalOrder()));
    ordered.addAll(collector);
    IExpr form = expr;
    for (IExpr mono : ordered) {
      ISymbol symbol = F.Dummy("tw$t");
      IExpr u;
      MonomialType type;
      IExpr derivative;
      if (mono.isAST(S.Log, 2)) {
        type = MonomialType.PRIMITIVE;
        u = mono.first();
        derivative = engine.evaluate(F.Divide(F.D(u, x), u));
      } else if (mono.isAST(S.Tan, 2)) {
        type = MonomialType.HYPERTANGENT;
        u = mono.first();
        derivative = engine.evaluate(F.Times(F.D(u, x), F.Plus(F.C1, F.Power(symbol, F.C2))));
      } else { // Power(E, u): hyperexponential
        type = MonomialType.HYPEREXPONENTIAL;
        u = mono.exponent();
        derivative = engine.evaluate(F.Times(F.D(u, x), symbol));
      }
      // Express the derivative and the running form over the already-added lower monomials.
      for (Monomial lower : tower.monomials) {
        derivative = F.subst(derivative, lower.original, lower.symbol);
      }
      tower.monomials.add(new Monomial(symbol, u, type, derivative, mono));
      form = F.subst(form, mono, symbol);
    }
    tower.towerForm = form;
    return tower;
  }

  /**
   * Apply the derivation {@code D} to a tower-form {@code element} by the chain rule
   * {@code D(f) = &part;f/&part;x + &sum;_i (&part;f/&part;t_i) D(t_i)}.
   */
  public IExpr derivation(IExpr element, EvalEngine engine) {
    IExpr result = engine.evaluate(F.D(element, baseVariable));
    for (Monomial m : monomials) {
      IExpr partial = engine.evaluate(F.D(element, m.symbol));
      if (!partial.isZero()) {
        result = F.Plus(result, F.Times(partial, m.derivative));
      }
    }
    return engine.evaluate(result);
  }

  /** Substitute the monomial symbols back to their {@code Log/Exp/Tan(argument)} expressions. */
  public IExpr toExpression(IExpr element) {
    IExpr result = element;
    for (Monomial m : monomials) {
      result = F.subst(result, m.symbol, m.original);
    }
    return result;
  }

  private static void collectMonomials(IExpr expr, IExpr x, Set<IExpr> collector) {
    if (expr.isFree(x, true) || !expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST(S.Log, 2) || ast.isAST(S.Tan, 2)) {
      collectMonomials(ast.first(), x, collector);
      collector.add(ast);
      return;
    }
    if (ast.isPower() && ast.base().equals(S.E) && !ast.exponent().isFree(x, true)) {
      collectMonomials(ast.exponent(), x, collector);
      collector.add(ast);
      return;
    }
    for (int i = 1; i < ast.size(); i++) {
      collectMonomials(ast.get(i), x, collector);
    }
  }
}
