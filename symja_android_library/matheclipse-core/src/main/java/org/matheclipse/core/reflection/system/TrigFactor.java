package org.matheclipse.core.reflection.system;

import java.util.HashSet;
import java.util.Set;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

public class TrigFactor extends AbstractFunctionEvaluator {

  private static final int TRIG_FACTOR_ATOM_THRESHOLD = 2;
  private static final int TRIG_FACTOR_DEGREE_THRESHOLD = 4;
  private static final int TRIG_FACTOR_TOTAL_ATOM_THRESHOLD = 3;
  /**
   * Highest power of a single trigonometric function for which the final <code>Factor()</code> of
   * the pipeline is still affordable. The cost grows exponentially with the degree: the expanded
   * form of <code>Cos(5*x)*Sin(5*x)</code>, of degree 9, factors in 16ms, the one of
   * <code>Cos(6*x)*Sin(6*x)</code>, of degree 11, in 1.8s.
   */
  private static final int TRIG_FACTOR_FINAL_DEGREE_THRESHOLD = 10;

  // Dummies used to shield polynomials from Symja's auto-evaluator during Factor/Together
  private static final ISymbol DSIN = F.Dummy("DSIN");
  private static final ISymbol DCOS = F.Dummy("DCOS");
  private static final ISymbol DSINH = F.Dummy("DSINH");
  private static final ISymbol DCOSH = F.Dummy("DCOSH");

  public TrigFactor() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr temp = CompareUtil.threadListLogicEquationOperators(ast.arg1(), ast, 1);
    if (temp.isPresent()) {
      return temp;
    }

    IExpr arg = ast.arg1();
    if (arg.isFree(v -> v.isTrigFunction() || v.isHyperbolicFunction(), false)) {
      return arg;
    }

    // Strict trigonometric identities on the argument itself. Factor() works on the polynomial
    // structure only and cannot reach any of them, because they all change the angle.
    IExpr identity = trigFactorIdentity(arg);
    if (identity.isPresent()) {
      return engine.evaluate(identity);
    }

    // PATH A: Run pipeline on argument directly (preserves angle-sums)
    IExpr resultA = trigFactorRunPipeline(arg, engine);

    if (!resultA.equals(arg)) {
      return resultA;
    }

    // PATH B: Expand angle-sums first to expose hidden structure, then run pipeline. Pointless
    // for a monomial like Cos(6*x)*Sin(6*x): expanding every factor and refactoring the product
    // can only give back the factors it started from, at the price of factoring a polynomial of
    // degree 12 (1.8s).
    if (!hasCompoundTrigStructure(arg) || arg.isFree(x -> x.isPlus(), true)) {
      return resultA;
    }

    IExpr expanded = engine.evaluate(F.TrigExpand(arg));
    IExpr resultB = trigFactorRunPipeline(expanded, engine);

    if (resultB.leafCount() < resultA.leafCount()) {
      return resultB;
    }

    return resultA;
  }

  private IExpr trigFactorRunPipeline(IExpr input, EvalEngine engine) {
    // Stage 1: Convert Tan/Cot/Sec/Csc into Sin and Cos DUMMY variables
    // This entirely blocks Symja from auto-evaluating Sin/Cos back into Tan during Factor
    IExpr dummified = input.accept(new ToDummyVisitor()).orElse(input);

    // Stage 2: Unify denominators algebraically
    IExpr togethered = engine.evaluate(F.Together(dummified));

    // Stage 3: Polynomial factorization with blowout prevention
    IExpr factored = togethered;
    if (shouldFactor(togethered)) {
      factored = engine.evaluate(F.Factor(togethered));
    }

    // Stage 4: Restore actual Trig functions for native identity matchers
    IExpr restored = factored.accept(new FromDummyVisitor()).orElse(factored);

    // Stage 5: Strict native identities
    IExpr collapsed = applyIdentities(restored, engine);

    // Stage 6: Restore Tan/Cot/Sec/Csc cleanly from fraction bases
    IExpr recombined = applyFromSincos(collapsed, engine);

    // Stage 7: Isolated degree-2 power reduction for leftover Sin^2 / Cos^2 polynomials
    IExpr postReduced = applyPostReduce(recombined, engine);

    // Final algebraic cleanup to cleanly group fractions and sub-expressions
    IExpr regrouped = maxTrigPower(postReduced) <= TRIG_FACTOR_FINAL_DEGREE_THRESHOLD
        ? engine.evaluate(F.Factor(postReduced))
        : postReduced;

    // Stage 8: Condense the single-angle powers which Factor() introduced back into multiple angles
    return condenseMultipleAngles(regrouped, engine);
  }

  /**
   * <code>Factor()</code> rewrites multiple angles into powers of a single angle, for example
   * <code>Sin(2*x)+Sin(4*x)</code> into <code>-2*Cos(x)*Sin(x)*(-1-2*Cos(x)^2+2*Sin(x)^2)</code>.
   * Condense every factor of the factored product back into multiple angles, so that the result is
   * <code>2*Cos(x)*(1+2*Cos(2*x))*Sin(x)</code>.
   *
   * @param expr the factored product
   * @param engine the evaluation engine
   * @return <code>expr</code> unchanged if no factor could be condensed
   */
  private IExpr condenseMultipleAngles(IExpr expr, EvalEngine engine) {
    if (expr.isTimes()) {
      IAST times = (IAST) expr;
      IASTAppendable result = F.TimesAlloc(times.size());
      boolean evaled = false;
      for (int i = 1; i < times.size(); i++) {
        IExpr factor = times.get(i);
        IExpr condensed = condenseFactor(factor, engine);
        if (condensed.isPresent()) {
          // TrigReduce() can return a canonical negative factor like -1-2*Cos(2*x). Extract the
          // minus sign into the product, to avoid printing -2*Cos(x)*Sin(x)*(-1-2*Cos(2*x))
          IExpr negated = getNormalizedNegativeExpression(condensed);
          if (negated.isPresent()) {
            result.append(F.CN1);
            condensed = negated;
          }
          result.append(condensed);
          evaled = true;
        } else {
          result.append(factor);
        }
      }
      return evaled ? engine.evaluate(result) : expr;
    }

    IExpr condensed = condenseFactor(expr, engine);
    return condensed.orElse(expr);
  }

  /**
   * Condense a single polynomial factor in <code>Sin(x)</code> and <code>Cos(x)</code> into
   * multiple angles with <code>TrigReduce()</code>.
   *
   * @param factor a factor of the factored product
   * @param engine the evaluation engine
   * @return {@link F#NIL} if <code>factor</code> is no sum or if condensing it isn't a
   *         simplification
   */
  private IExpr condenseFactor(IExpr factor, EvalEngine engine) {
    if (factor.isPlus()) {
      IExpr reduced = engine.evaluate(F.TrigReduce(factor));
      if (reduced.leafCount() < factor.leafCount()) {
        // pull the numeric content out of the condensed factor, because Factor() cannot be used
        // here without expanding the multiple angles again
        return engine.evaluate(F.FactorTerms(reduced));
      }
    }
    return F.NIL;
  }

  // ==========================================
  // Angle Changing Identities
  // ==========================================

  /**
   * Rewrite <code>expr</code> with one of the identities which contract a sum into a product by
   * changing the angle. <code>Factor()</code> works on the polynomial structure of the
   * <code>Sin(x)</code>, <code>Cos(x)</code>,... atoms alone and cannot reach any of them.
   *
   * @param expr the argument of <code>TrigFactor()</code>
   * @return {@link F#NIL} if no identity matches
   */
  private static IExpr trigFactorIdentity(IExpr expr) {
    if (expr.isPlus() && expr.size() == 3) {
      IAST plus = (IAST) expr;
      IExpr result = pythagoreanDifference(plus);
      if (result.isPresent()) {
        return result;
      }
      result = constantPlusTrig(plus);
      if (result.isPresent()) {
        return result;
      }
      return sumOfTwoTrigs(plus);
    }
    return doubleAngle(expr);
  }

  /**
   * <code>Sin(x)+Cos(x)</code> becomes <code>Sqrt(2)*Sin(x+Pi/4)</code> and the sum-to-product
   * identities like <code>Sin(x)+Sin(y)</code> become <code>2*Cos(x/2-y/2)*Sin(x/2+y/2)</code>.
   *
   * @param plus a sum of exactly two terms
   * @return {@link F#NIL} if the two terms aren't <code>+-Sin(u)</code>, <code>+-Cos(u)</code>,
   *         <code>+-Sinh(u)</code> or <code>+-Cosh(u)</code>
   */
  private static IExpr sumOfTwoTrigs(IAST plus) {
    SignedTrig first = signedTrig(plus.arg1());
    if (first == null) {
      return F.NIL;
    }
    SignedTrig second = signedTrig(plus.arg2());
    if (second == null) {
      return F.NIL;
    }

    SignedTrig sin = first.headID() == ID.Sin ? first : //
        (second.headID() == ID.Sin ? second : null);
    SignedTrig cos = first.headID() == ID.Cos ? first : //
        (second.headID() == ID.Cos ? second : null);
    if (sin != null && cos != null && sin.angle().equals(cos.angle())) {
      // sinSign*Sqrt(2)*Sin(angle+sinSign*cosSign*Pi/4)
      IExpr quarter = sin.negative == cos.negative ? F.CPiQuarter : F.Times(F.CN1, F.CPiQuarter);
      IExpr result = F.Times(F.CSqrt2, F.Sin(F.Plus(sin.angle(), quarter)));
      return sin.negative ? F.Negate(result) : result;
    }

    if (first.headID() != second.headID()) {
      return F.NIL;
    }
    IExpr u = first.angle();
    IExpr v = second.angle();
    if (!u.isSymbol() || !v.isSymbol() || u.equals(v)) {
      // Restricted to plain symbols on purpose. For multiple angles the polynomial pipeline finds
      // the better factorization: Sin(2*x)+Sin(4*x) becomes 2*Cos(x)*(1+2*Cos(2*x))*Sin(x) and not
      // 2*Cos(x)*Sin(3*x).
      return F.NIL;
    }
    // the sign of the first term is pulled out of the product, so only the relative sign of the
    // second term decides between the sum and the difference identity
    boolean difference = first.negative != second.negative;
    // kept distributed, so that the angles print as x/2+y/2 and not as 1/2*(x+y)
    IExpr half = F.Plus(F.Times(F.C1D2, u), F.Times(F.C1D2, v));
    IExpr halfDifference = F.Plus(F.Times(F.C1D2, u), F.Times(F.CN1D2, v));
    IExpr result;
    switch (first.headID()) {
      case ID.Sin:
        result = difference //
            ? F.Times(F.C2, F.Cos(half), F.Sin(halfDifference)) //
            : F.Times(F.C2, F.Sin(half), F.Cos(halfDifference));
        break;
      case ID.Cos:
        result = difference //
            ? F.Times(F.CN2, F.Sin(half), F.Sin(halfDifference)) //
            : F.Times(F.C2, F.Cos(half), F.Cos(halfDifference));
        break;
      case ID.Sinh:
        result = difference //
            ? F.Times(F.C2, F.Cosh(half), F.Sinh(halfDifference)) //
            : F.Times(F.C2, F.Sinh(half), F.Cosh(halfDifference));
        break;
      default: // ID.Cosh
        result = difference //
            ? F.Times(F.C2, F.Sinh(half), F.Sinh(halfDifference)) //
            : F.Times(F.C2, F.Cosh(half), F.Cosh(halfDifference));
        break;
    }
    return first.negative ? F.Negate(result) : result;
  }

  /**
   * The half angle identities <code>1+Cos(x)</code> becomes <code>2*Cos(x/2)^2</code>,
   * <code>1-Sin(x)</code> becomes <code>2*Sin(x/2-Pi/4)^2</code>, <code>Cosh(x)-1</code> becomes
   * <code>2*Sinh(x/2)^2</code>,...
   *
   * @param plus a sum of exactly two terms
   * @return {@link F#NIL} if the sum isn't <code>+-1+-Cos(x)</code>, <code>+-1+-Sin(x)</code> or
   *         <code>+-1+-Cosh(x)</code>. <code>Sinh()</code> has no such identity.
   */
  private static IExpr constantPlusTrig(IAST plus) {
    IExpr constant = plus.arg1();
    SignedTrig trig = signedTrig(plus.arg2());
    if (trig == null) {
      constant = plus.arg2();
      trig = signedTrig(plus.arg1());
    }
    if (trig == null || !(constant.isOne() || constant.isMinusOne())) {
      return F.NIL;
    }
    // true if the constant and the trigonometric function carry the same sign
    boolean sameSign = constant.isOne() != trig.negative;
    IExpr halfAngle = F.Times(F.C1D2, trig.angle());
    IExpr result;
    switch (trig.headID()) {
      case ID.Cos:
        result = sameSign //
            ? F.Times(F.C2, F.Sqr(F.Cos(halfAngle))) //
            : F.Times(F.C2, F.Sqr(F.Sin(halfAngle)));
        return constant.isOne() ? result : F.Negate(result);
      case ID.Sin:
        IExpr quarter = sameSign ? F.CPiQuarter : F.Times(F.CN1, F.CPiQuarter);
        result = F.Times(F.C2, F.Sqr(F.Sin(F.Plus(halfAngle, quarter))));
        return constant.isOne() ? result : F.Negate(result);
      case ID.Cosh:
        result = sameSign //
            ? F.Times(F.C2, F.Sqr(F.Cosh(halfAngle))) //
            : F.Times(F.C2, F.Sqr(F.Sinh(halfAngle)));
        return trig.negative ? F.Negate(result) : result;
      default: // ID.Sinh
        return F.NIL;
    }
  }

  /**
   * <code>Cos(x)^2-Sin(x)^2</code> is <code>Cos(2*x)</code> and <code>Sin(x)^2-Cos(x)^2</code> is
   * <code>-Cos(2*x)</code>, which {@link #cosineDoubleAngle(IExpr)} contracts into a product.
   *
   * @param plus a sum of exactly two terms
   * @return {@link F#NIL} if the sum isn't a difference of <code>Sin(x)^2</code> and
   *         <code>Cos(x)^2</code> with the same angle
   */
  private static IExpr pythagoreanDifference(IAST plus) {
    SignedTrig first = squaredTrig(plus.arg1());
    if (first == null) {
      return F.NIL;
    }
    SignedTrig second = squaredTrig(plus.arg2());
    if (second == null || first.negative == second.negative) {
      return F.NIL;
    }
    SignedTrig sin = first.headID() == ID.Sin ? first : //
        (second.headID() == ID.Sin ? second : null);
    SignedTrig cos = first.headID() == ID.Cos ? first : //
        (second.headID() == ID.Cos ? second : null);
    if (sin == null || cos == null || !sin.angle().equals(cos.angle())) {
      return F.NIL;
    }
    IExpr result = cosineDoubleAngle(sin.angle());
    return sin.negative ? result : F.Negate(result);
  }

  /**
   * <code>Sin(2*x)</code> becomes <code>2*Cos(x)*Sin(x)</code>, <code>Sinh(2*x)</code> becomes
   * <code>2*Cosh(x)*Sinh(x)</code> and <code>Cos(2*x)</code> becomes
   * <code>2*Sin(Pi/4-x)*Sin(Pi/4+x)</code>.
   *
   * <p>
   * Only used for a single trigonometric function, never for the terms of a sum:
   * <code>Sin(2*x)+Sin(4*x)</code> is left to the polynomial pipeline.
   *
   * @param expr the argument of <code>TrigFactor()</code>
   * @return {@link F#NIL} if <code>expr</code> isn't a sine or cosine of an even multiple of an
   *         angle
   */
  private static IExpr doubleAngle(IExpr expr) {
    if (!isBasicTrig(expr)) {
      return F.NIL;
    }
    IAST function = (IAST) expr;
    IExpr angle = function.arg1();
    if (!angle.isTimes()) {
      return F.NIL;
    }
    IExpr multiple = ((IAST) angle).arg1();
    if (!multiple.isInteger() || !((IInteger) multiple).isEven()) {
      return F.NIL;
    }
    IExpr half = F.Times(F.C1D2, angle);
    switch (function.headID()) {
      case ID.Sin:
        return F.Times(F.C2, F.Cos(half), F.Sin(half));
      case ID.Sinh:
        return F.Times(F.C2, F.Cosh(half), F.Sinh(half));
      case ID.Cos:
        return cosineDoubleAngle(half);
      default: // ID.Cosh has no product form with a real angle
        return F.NIL;
    }
  }

  /**
   * <code>Cos(2*x) == 2*Sin(Pi/4-x)*Sin(Pi/4+x)</code>
   *
   * @param angle the half angle <code>x</code> of <code>Cos(2*x)</code>
   */
  private static IExpr cosineDoubleAngle(IExpr angle) {
    return F.Times(F.C2, F.Sin(F.Subtract(F.CPiQuarter, angle)),
        F.Sin(F.Plus(F.CPiQuarter, angle)));
  }

  /**
   * Split <code>+-f(u)</code> into its sign and <code>f(u)</code>.
   *
   * @param term a term of a sum
   * @return <code>null</code> if <code>term</code> isn't a basic trigonometric function with an
   *         optional minus sign
   */
  private static SignedTrig signedTrig(IExpr term) {
    if (isBasicTrig(term)) {
      return new SignedTrig(false, (IAST) term);
    }
    if (term.isTimes() && term.size() == 3 && term.first().isMinusOne()) {
      IExpr rest = ((IAST) term).arg2();
      if (isBasicTrig(rest)) {
        return new SignedTrig(true, (IAST) rest);
      }
    }
    return null;
  }

  /**
   * Split <code>+-f(u)^2</code> into its sign and <code>f(u)</code>.
   *
   * @param term a term of a sum
   * @return <code>null</code> if <code>term</code> isn't the square of a basic trigonometric
   *         function with an optional minus sign
   */
  private static SignedTrig squaredTrig(IExpr term) {
    boolean negative = false;
    IExpr rest = term;
    if (rest.isTimes() && rest.size() == 3 && rest.first().isMinusOne()) {
      negative = true;
      rest = ((IAST) rest).arg2();
    }
    if (rest.isPower() && rest.exponent().equals(F.C2) && isBasicTrig(rest.base())) {
      return new SignedTrig(negative, (IAST) rest.base());
    }
    return null;
  }

  static boolean isBasicTrig(IExpr expr) {
    if (expr.isAST1()) {
      int headID = expr.headID();
      return headID == ID.Sin || headID == ID.Cos || headID == ID.Sinh || headID == ID.Cosh;
    }
    return false;
  }

  /** One term <code>+-f(u)</code> of a sum, with <code>f</code> in Sin, Cos, Sinh, Cosh. */
  private static final class SignedTrig {
    final boolean negative;
    final IAST function;

    SignedTrig(boolean negative, IAST function) {
      this.negative = negative;
      this.function = function;
    }

    IExpr angle() {
      return function.arg1();
    }

    int headID() {
      return function.headID();
    }
  }

  // ==========================================
  // Pipeline Loop Engines
  // ==========================================

  private IExpr applyIdentities(IExpr expr, EvalEngine engine) {
    IExpr current = expr;
    IdentityVisitor visitor = new IdentityVisitor(engine);
    while (true) {
      IExpr next = current.accept(visitor);
      if (next.isPresent()) {
        current = engine.evaluate(next);
      } else {
        break;
      }
    }
    return current;
  }

  private IExpr applyFromSincos(IExpr expr, EvalEngine engine) {
    IExpr current = expr;
    RecombinationVisitor visitor = new RecombinationVisitor(engine);
    while (true) {
      IExpr next = current.accept(visitor);
      if (next.isPresent()) {
        current = engine.evaluate(next);
      } else {
        break;
      }
    }
    return current;
  }

  private IExpr applyPostReduce(IExpr expr, EvalEngine engine) {
    IExpr current = expr;
    PostReduceVisitor visitor = new PostReduceVisitor(engine);
    while (true) {
      IExpr next = current.accept(visitor);
      if (next.isPresent()) {
        current = engine.evaluate(next);
      } else {
        break;
      }
    }
    return current;
  }

  // ==========================================
  // Pipeline Analysis Guards
  // ==========================================

  /**
   * The highest integer power of a trigonometric or hyperbolic function in <code>expr</code>; the
   * degree of the polynomial that <code>Factor()</code> would have to factor.
   */
  private static int maxTrigPower(IExpr expr) {
    if (!expr.isAST()) {
      return 0;
    }
    IAST ast = (IAST) expr;
    int max = 0;
    if (ast.isPower() && ast.exponent().isInteger()
        && (ast.base().isTrigFunction() || ast.base().isHyperbolicFunction())) {
      max = Math.abs(ast.exponent().toIntDefault(0));
    }
    for (int i = 1; i < ast.size(); i++) {
      max = Math.max(max, maxTrigPower(ast.get(i)));
    }
    return max;
  }

  private boolean hasCompoundTrigStructure(IExpr expr) {
    CompoundTrigVisitor v = new CompoundTrigVisitor();
    expr.accept(v);
    return v.hasCompound;
  }

  private boolean shouldFactor(IExpr expr) {
    TrigStatsVisitor stats = new TrigStatsVisitor();
    expr.accept(stats);
    if (stats.distinctSquaredAtoms > TRIG_FACTOR_ATOM_THRESHOLD)
      return false;
    if (stats.maxAtomPower > TRIG_FACTOR_DEGREE_THRESHOLD)
      return false;
    if (stats.distinctAtoms > TRIG_FACTOR_TOTAL_ATOM_THRESHOLD)
      return false;
    return true;
  }

  static boolean isDummyTrig(IExpr expr) {
    if (expr.isAST1()) {
      IExpr head = expr.head();
      return head.equals(DSIN) || head.equals(DCOS) || head.equals(DSINH) || head.equals(DCOSH);
    }
    return false;
  }

  // ==========================================
  // AST Visitors
  // ==========================================

  static class CompoundTrigVisitor extends VisitorExpr {
    boolean hasCompound = false;

    @Override
    public IExpr visitAST(IAST ast) {
      if (hasCompound)
        return F.NIL;

      if (ast.isPower() && ast.arg2().isInteger()) {
        int pow = Math.abs(ast.arg2().toIntDefault());
        if (pow >= 2 && ast.arg1().isAST1() && ast.arg1().isTrigFunction()) {
          hasCompound = true;
          return F.NIL;
        }
      }
      if (ast.isTimes()) {
        int count = 0;
        for (IExpr arg : ast) {
          if (arg.isAST1() && arg.isTrigFunction()) {
            count++;
          } else if (arg.isPower() && arg.base().isAST1() && arg.base().isTrigFunction()) {
            count++;
          }
        }
        if (count >= 2) {
          hasCompound = true;
          return F.NIL;
        }
      }
      return super.visitAST(ast);
    }
  }

  static class TrigStatsVisitor extends VisitorExpr {
    int distinctSquaredAtoms = 0;
    int maxAtomPower = 0;
    int distinctAtoms = 0;
    Set<IExpr> atoms = new HashSet<>();
    Set<IExpr> squaredAtoms = new HashSet<>();

    @Override
    public IExpr visitAST(IAST ast) {
      if (ast.isPower() && ast.arg2().isInteger()) {
        int pow = Math.abs(ast.arg2().toIntDefault());
        if (pow >= 2 && isDummyTrig(ast.arg1())) {
          maxAtomPower = Math.max(maxAtomPower, pow);
          if (squaredAtoms.add(ast.arg1()))
            distinctSquaredAtoms++;
        }
      }
      if (isDummyTrig(ast)) {
        if (atoms.add(ast))
          distinctAtoms++;
      }
      return super.visitAST(ast);
    }
  }

  static class ToDummyVisitor extends VisitorExpr {
    @Override
    public IExpr visitAST(IAST ast) {
      if (ast.isAST1()) {
        int headID = ast.headID();
        if (headID == ID.Sin || headID == ID.Cos || headID == ID.Tan || headID == ID.Cot
            || headID == ID.Sec || headID == ID.Csc || headID == ID.Sinh || headID == ID.Cosh
            || headID == ID.Tanh || headID == ID.Coth || headID == ID.Sech || headID == ID.Csch) {

          IExpr arg = ast.arg1();
          IExpr mappedArg = arg.accept(this).orElse(arg);

          switch (headID) {
            case ID.Sin:
              return F.unaryAST1(DSIN, mappedArg);
            case ID.Cos:
              return F.unaryAST1(DCOS, mappedArg);
            case ID.Tan:
              return F.Divide(F.unaryAST1(DSIN, mappedArg), F.unaryAST1(DCOS, mappedArg));
            case ID.Cot:
              return F.Divide(F.unaryAST1(DCOS, mappedArg), F.unaryAST1(DSIN, mappedArg));
            case ID.Sec:
              return F.Power(F.unaryAST1(DCOS, mappedArg), F.CN1);
            case ID.Csc:
              return F.Power(F.unaryAST1(DSIN, mappedArg), F.CN1);
            case ID.Sinh:
              return F.unaryAST1(DSINH, mappedArg);
            case ID.Cosh:
              return F.unaryAST1(DCOSH, mappedArg);
            case ID.Tanh:
              return F.Divide(F.unaryAST1(DSINH, mappedArg), F.unaryAST1(DCOSH, mappedArg));
            case ID.Coth:
              return F.Divide(F.unaryAST1(DCOSH, mappedArg), F.unaryAST1(DSINH, mappedArg));
            case ID.Sech:
              return F.Power(F.unaryAST1(DCOSH, mappedArg), F.CN1);
            case ID.Csch:
              return F.Power(F.unaryAST1(DSINH, mappedArg), F.CN1);
          }
        }
      }
      return super.visitAST(ast);
    }
  }

  static class FromDummyVisitor extends VisitorExpr {
    @Override
    public IExpr visitAST(IAST ast) {
      if (ast.isAST1()) {
        IExpr head = ast.head();
        if (head.equals(DSIN))
          return F.Sin(ast.arg1().accept(this).orElse(ast.arg1()));
        if (head.equals(DCOS))
          return F.Cos(ast.arg1().accept(this).orElse(ast.arg1()));
        if (head.equals(DSINH))
          return F.Sinh(ast.arg1().accept(this).orElse(ast.arg1()));
        if (head.equals(DCOSH))
          return F.Cosh(ast.arg1().accept(this).orElse(ast.arg1()));
      }
      return super.visitAST(ast);
    }
  }

  static class IdentityVisitor extends VisitorExpr {
    final EvalEngine fEngine;

    public IdentityVisitor(EvalEngine engine) {
      fEngine = engine;
    }

    @Override
    public IExpr visit(IASTMutable ast) {
      IExpr temp = ast;
      boolean evaled = false;

      if (temp.isAST()) {
        IExpr mapped = visitAST((IASTMutable) temp);
        if (mapped.isPresent())
          return fEngine.evaluate(mapped);
      }

      return evaled ? fEngine.evaluate(temp) : F.NIL;
    }
  }

  static class RecombinationVisitor extends VisitorExpr {
    final EvalEngine fEngine;

    public RecombinationVisitor(EvalEngine engine) {
      fEngine = engine;
    }

    @Override
    public IExpr visit(IASTMutable ast) {
      IExpr temp = ast;
      boolean evaled = false;

      if (ast.isTimes()) {
        IAST times = ast;
        IASTAppendable newTimes = F.TimesAlloc(times.argSize());
        boolean[] used = new boolean[times.argSize() + 1];
        boolean changed = false;

        for (int i = 1; i <= times.argSize(); i++) {
          if (used[i])
            continue;
          IExpr arg = times.get(i);

          if (arg.isPower() && arg.exponent().isInteger()) {
            IExpr base = arg.base();
            int exp = arg.exponent().toIntDefault();

            if (base.isSin()) {
              for (int j = i + 1; j <= times.argSize(); j++) {
                if (used[j])
                  continue;
                IExpr arg2 = times.get(j);
                if (arg2.isPower() && arg2.base().isCos()
                    && arg2.base().first().equals(base.first())) {
                  if (arg2.exponent().toMachineInt() == -exp) {
                    newTimes.append(F.Power(F.Tan(base.first()), F.ZZ(exp)));
                    used[i] = true;
                    used[j] = true;
                    changed = true;
                    break;
                  }
                }
              }
            } else if (base.isCos()) {
              for (int j = i + 1; j <= times.argSize(); j++) {
                if (used[j])
                  continue;
                IExpr arg2 = times.get(j);
                if (arg2.isPower() && arg2.base().isSin()
                    && arg2.base().first().equals(base.first())) {
                  if (arg2.exponent().toMachineInt() == -exp) {
                    newTimes.append(F.Power(F.Cot(base.first()), F.ZZ(exp)));
                    used[i] = true;
                    used[j] = true;
                    changed = true;
                    break;
                  }
                }
              }
            }
          } else if (arg.isSin()) {
            for (int j = 1; j <= times.argSize(); j++) {
              if (i == j || used[j])
                continue;
              IExpr arg2 = times.get(j);
              if (arg2.isPower() && arg2.base().isCos() && arg2.exponent().isMinusOne()
                  && arg2.base().first().equals(arg.first())) {
                newTimes.append(F.Tan(arg.first()));
                used[i] = true;
                used[j] = true;
                changed = true;
                break;
              }
            }
          } else if (arg.isCos()) {
            for (int j = 1; j <= times.argSize(); j++) {
              if (i == j || used[j])
                continue;
              IExpr arg2 = times.get(j);
              if (arg2.isPower() && arg2.base().isSin() && arg2.exponent().isMinusOne()
                  && arg2.base().first().equals(arg.first())) {
                newTimes.append(F.Cot(arg.first()));
                used[i] = true;
                used[j] = true;
                changed = true;
                break;
              }
            }
          }
        }

        if (changed) {
          for (int i = 1; i <= times.argSize(); i++) {
            if (!used[i])
              newTimes.append(times.get(i));
          }
          temp = fEngine.evaluate(newTimes);
          evaled = true;
        }
      } else if (ast.isPower()) {
        IAST power = ast;
        if (power.exponent().isInteger()) {
          int exp = power.exponent().toIntDefault();
          if (exp < 0) {
            if (power.base().isCos()) {
              temp = F.Power(F.Sec(power.base().first()), F.ZZ(-exp));
              evaled = true;
            } else if (power.base().isSin()) {
              temp = F.Power(F.Csc(power.base().first()), F.ZZ(-exp));
              evaled = true;
            } else if (power.base().isCosh()) {
              temp = F.Power(F.Sech(power.base().first()), F.ZZ(-exp));
              evaled = true;
            } else if (power.base().isSinh()) {
              temp = F.Power(F.Csch(power.base().first()), F.ZZ(-exp));
              evaled = true;
            }
          }
        }
      }

      if (temp.isAST()) {
        IExpr mapped = visitAST((IASTMutable) temp);
        if (mapped.isPresent())
          return fEngine.evaluate(mapped);
      }

      return evaled ? fEngine.evaluate(temp) : F.NIL;
    }
  }

  static class PostReduceVisitor extends VisitorExpr {
    final EvalEngine fEngine;

    public PostReduceVisitor(EvalEngine engine) {
      fEngine = engine;
    }

    @Override
    public IExpr visit(IASTMutable ast) {
      IExpr temp = ast;
      boolean evaled = false;

      // Selectively reduce isolated degree-2 Sine/Cosine bases
      if (ast.isPower() && ast.arg2().toMachineInt() == 2) {
        IExpr base = ast.arg1();
        if (base.isAST1()) {
          if (base.isCos()) {
            temp = fEngine
                .evaluate(F.Plus(F.C1D2, F.Times(F.C1D2, F.Cos(F.Times(F.C2, base.first())))));
            evaled = true;
          } else if (base.isSin()) {
            temp = fEngine
                .evaluate(F.Plus(F.C1D2, F.Times(F.CN1D2, F.Cos(F.Times(F.C2, base.first())))));
            evaled = true;
          } else if (base.isCosh()) {
            temp = fEngine
                .evaluate(F.Plus(F.C1D2, F.Times(F.C1D2, F.Cosh(F.Times(F.C2, base.first())))));
            evaled = true;
          } else if (base.isSinh()) {
            temp = fEngine
                .evaluate(F.Plus(F.CN1D2, F.Times(F.C1D2, F.Cosh(F.Times(F.C2, base.first())))));
            evaled = true;
          }
        }
      }

      if (temp.isAST()) {
        IExpr mapped = visitAST((IASTMutable) temp);
        if (mapped.isPresent())
          return fEngine.evaluate(mapped);
      }

      return evaled ? temp : F.NIL;
    }
  }

  // ==========================================
  // Rule Definitions
  // ==========================================

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
