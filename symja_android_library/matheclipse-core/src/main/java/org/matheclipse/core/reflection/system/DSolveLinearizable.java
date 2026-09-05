package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * First order equations which a substitution <code>u == phi(y)</code> makes linear.
 *
 * <p>
 * For <code>y' == F(x,y)</code> the substitution gives <code>u' == phi'(y)*F(x,y)</code>, and once
 * that is written in <code>u</code> by replacing <code>y</code> with the inverse of
 * <code>phi</code>, the right hand side of a suitable equation turns out to be linear in
 * <code>u</code>, or at least of a shape the rest of the cascade recognizes. The solution is mapped
 * back with the same inverse.
 *
 * <p>
 * Only the substitutions worth trying are tried, and only on equations which can profit from them:
 * the right hand side has to apply a logarithm, an exponential or a circular function to something
 * containing <code>y</code>, which is what these substitutions undo. That gate also keeps the
 * method from entering itself, because the equation it produces is rational in <code>u</code>.
 */
final class DSolveLinearizable {

  private DSolveLinearizable() {}

  /** How big a right hand side is still worth substituting into. */
  private static final int MAX_LEAF_COUNT = 300;

  /** The substitutions, with their inverses. */
  private static final ISymbol[] PHI = {S.Log, S.Exp, S.Sin, S.Cos, S.Tan};

  private static final ISymbol[] PSI = {S.Exp, S.Log, S.ArcSin, S.ArcCos, S.ArcTan};

  /** The circular functions, each of which one of the substitutions can undo. */
  private static final int[] CIRCULAR = {ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc};

  /** The functions of the unknown which one of the substitutions can undo. */
  private static final int[] TRANSCENDENTAL = {ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc,
      ID.Sinh, ID.Cosh, ID.Tanh, ID.Log};

  /**
   * The general solution of the equation, or {@link F#NIL} if no substitution makes it one the
   * cascade can solve.
   */
  static IExpr solve(IExpr lhs, IExpr yFunction, IExpr xVar, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr head = yFunction.head();
    IExpr dyx = engine.evaluate(F.D(yFunction, xVar));
    IExpr coefficient = engine.evaluate(F.Coefficient(lhs, dyx));
    // The coefficient of the derivative may depend on the unknown, as it does in
    // y'(x)*Cos(y(x)) == ...; all that is needed is to be able to solve for the derivative, which
    // asks for the equation to be of the first degree in it.
    if (coefficient.isZero() || !DSolveODE.isLinearInDerivative(lhs, dyx, engine)) {
      return F.NIL;
    }
    IExpr rest = engine.evaluate(F.Subtract(lhs, F.Times(coefficient, dyx)));
    IExpr yDummy = F.Dummy("Y");
    IExpr right = engine.evaluate(
        F.subst(F.Cancel(F.Together(F.Divide(F.Negate(rest), coefficient))), yFunction, yDummy));
    if (!right.isFree(head, true) || right.leafCount() > MAX_LEAF_COUNT) {
      return F.NIL;
    }
    if (!hasTranscendental(right, yDummy) || isSeparable(right, xVar, yDummy)) {
      // Nothing to undo, or the separable solver already answers it in a simpler form.
      return F.NIL;
    }
    if (!ctx.enterLinearizable()) {
      return F.NIL;
    }
    try {
      for (int i = 0; i < PHI.length; i++) {
        if (!hasKernel(right, yDummy, i)) {
          continue;
        }
        IExpr solution = trySubstitution(lhs, right, yFunction, xVar, yDummy, i, c_n, ctx);
        if (solution.isPresent()) {
          return solution;
        }
      }
      return F.NIL;
    } finally {
      ctx.leaveLinearizable();
    }
  }

  /** Substitutes <code>u == phi(y)</code> and solves what it leaves. */
  private static IExpr trySubstitution(IExpr lhs, IExpr right, IExpr yFunction, IExpr xVar,
      IExpr yDummy, int index, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr uDummy = F.Dummy("u");
    IExpr phi = F.unaryAST1(PHI[index], yDummy);
    IExpr derivative = engine.evaluate(F.D(phi, yDummy));
    IExpr inverse = engine.evaluate(F.unaryAST1(PSI[index], uDummy));

    // The substitution is made in the new variable only after the right hand side has been put in
    // terms of the function which is being substituted. Doing it the other way round leaves
    // Cos(2*ArcCos(u)) and Sin(ArcCos(u)) behind, and the root in the latter looks like a sign
    // that this is the wrong substitution.
    IExpr base = engine.evaluate(F.Cancel(F.Together(F.Times(derivative, right))));
    IExpr transformed = substituteInverse(base, yDummy, inverse, uDummy, ctx);
    if (transformed.isNIL()) {
      IExpr simplified = ctx.evalTimeConstrained(F.Simplify(base), 3);
      if (simplified.isNIL()) {
        return F.NIL;
      }
      transformed = substituteInverse(simplified, yDummy, inverse, uDummy, ctx);
    }
    if (transformed.isNIL()) {
      return F.NIL;
    }

    IAST branches;
    IExpr slope = engine.evaluate(F.D(transformed, uDummy));
    if (slope.isFree(uDummy, true)) {
      // u' == A(x)*u + B(x).
      IExpr intercept = engine.evaluate(F.subst(transformed, uDummy, F.C0));
      IExpr solution = DSolveODE.linearODE(engine.evaluate(F.Negate(slope)),
          engine.evaluate(F.Negate(intercept)), xVar, c_n, engine);
      if (solution.isNIL()) {
        return F.NIL;
      }
      branches = F.List(solution);
    } else {
      // Whatever it is, the cascade may still recognize it; a Bernoulli equation is the usual case.
      IExpr uFunction = F.unaryAST1(F.Dummy("uf"), xVar);
      IExpr equation = F.Equal(F.Subtract(engine.evaluate(F.D(uFunction, xVar)),
          F.subst(transformed, uDummy, uFunction)), F.C0);
      branches = DSolveODE.solveSubODE(equation, xVar, uFunction, c_n, ctx);
    }

    IASTAppendable results = F.ListAlloc(branches.argSize());
    for (int i = 1; i <= branches.argSize(); i++) {
      IExpr body = engine.evaluate(F.subst(inverse, uDummy, branches.get(i)));
      if (body.isNIL() || !body.isFree(uDummy, true)) {
        continue;
      }
      // An inverse of a circular function is one branch of many, so the answer has to be seen to
      // solve the equation rather than merely not seen to fail.
      if (index >= 2
          && !DSolveVerify.acceptODEStrict(F.List(lhs), yFunction, xVar, body, ctx.engine)) {
        continue;
      }
      results.append(body);
    }
    if (results.argSize() == 0) {
      return F.NIL;
    }
    return results.argSize() == 1 ? results.arg1() : results;
  }

  /**
   * Writes the right hand side in the new variable, or answers {@link F#NIL} if what is left is not
   * a function of it alone.
   */
  private static IExpr substituteInverse(IExpr base, IExpr yDummy, IExpr inverse, IExpr uDummy,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr transformed = engine.evaluate(F.subst(base, yDummy, inverse));
    if (!transformed.isFree(yDummy, true) || hasFractionalPower(transformed, uDummy)) {
      return F.NIL;
    }
    if (!transformed.isFree(x -> x.isAST(S.ArcSin) || x.isAST(S.ArcCos) || x.isAST(S.ArcTan),
        true)) {
      IExpr simplified = ctx.evalTimeConstrained(F.Simplify(transformed), 3);
      if (simplified.isNIL() || !simplified.isFree(
          x -> x.isAST(S.ArcSin) || x.isAST(S.ArcCos) || x.isAST(S.ArcTan), true)
          || hasFractionalPower(simplified, uDummy)) {
        return F.NIL;
      }
      transformed = simplified;
    }
    return transformed.leafCount() > MAX_LEAF_COUNT ? F.NIL : transformed;
  }

  /** Whether the right hand side applies to the unknown something a substitution can undo. */
  private static boolean hasTranscendental(IExpr expr, IExpr yDummy) {
    return !expr.isFree(x -> {
      if (x.isPower() && x.base().equals(S.E)) {
        return !x.exponent().isFree(yDummy, true);
      }
      if (x.isAST1() && x.isFunctionID(TRANSCENDENTAL)) {
        return !x.first().isFree(yDummy, true);
      }
      return false;
    }, true);
  }

  /** Whether the substitution has anything to work on at all. */
  private static boolean hasKernel(IExpr expr, IExpr yDummy, int index) {
    switch (index) {
      case 0: // u == Log(y) needs a logarithm of the unknown.
        return !expr.isFree(x -> x.isAST(S.Log, 2) && !x.first().isFree(yDummy, true), true);
      case 1: // u == Exp(y) needs the unknown in an exponent.
        return !expr.isFree(
            x -> x.isPower() && x.base().equals(S.E) && !x.exponent().isFree(yDummy, true), true);
      default: // The circular substitutions need a circular function of the unknown.
        return !expr.isFree(x -> x.isAST1() && x.isFunctionID(CIRCULAR)
            && !x.first().isFree(yDummy, true), true);
    }
  }

  /** Whether a root of an expression in the new variable was left behind. */
  private static boolean hasFractionalPower(IExpr expr, IExpr uDummy) {
    return !expr.isFree(x -> x.isPower() && !x.exponent().isInteger()
        && !x.base().isFree(uDummy, true), true);
  }

  /**
   * Whether the equation is already separable, in which case the solver for those answers it in a
   * simpler form than a substitution would.
   */
  private static boolean isSeparable(IExpr right, IExpr xVar, IExpr yDummy) {
    if (right.isFree(xVar)) {
      return true;
    }
    if (!right.isTimes()) {
      return false;
    }
    IAST factors = (IAST) right;
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      if (!factor.isFree(xVar) && !factor.isFree(yDummy, true)) {
        return false;
      }
    }
    return true;
  }
}
