package org.matheclipse.core.eval;

import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.y_;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.y;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBigNumber;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcherPlus;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcherTimes;
import org.matheclipse.core.patternmatching.hash.HashedPatternRules;
import org.matheclipse.core.patternmatching.hash.HashedPatternRulesTimes;
import org.matheclipse.core.visit.AbstractVisitorBoolean;
import org.matheclipse.core.visit.AbstractVisitorLong;
import org.matheclipse.core.visit.VisitorExpr;

public class SimplifyUtil extends VisitorExpr {
  static class IsBasicExpressionVisitor extends AbstractVisitorBoolean {
    public IsBasicExpressionVisitor() {
      super();
    }

    @Override
    public boolean visit(IAST ast) {
      if (ast.isTimes() || ast.isPlus()) {
        // check the arguments
        return ast.forAll(x -> x.accept(this));
      }
      if (ast.isPowerInteger()) {
        // check the arguments
        return ast.base().accept(this);
      }
      return false;
    }

    @Override
    public boolean visit(IComplex element) {
      return true;
    }

    @Override
    public boolean visit(IComplexNum element) {
      return true;
    }

    @Override
    public boolean visit(IFraction element) {
      return true;
    }

    @Override
    public boolean visit(IInteger element) {
      return true;
    }

    @Override
    public boolean visit(INum element) {
      return true;
    }

    @Override
    public boolean visit(ISymbol symbol) {
      return true;
    }
  }
  /** Calculate the number of leaves in an AST */
  public static class LeafCountVisitor extends AbstractVisitorLong {
    int fHeadOffset;

    public LeafCountVisitor() {
      this(1);
    }

    public LeafCountVisitor(int hOffset) {
      fHeadOffset = hOffset;
    }

    @Override
    public long visit(IAST list) {
      long sum = 0;
      for (int i = fHeadOffset; i < list.size(); i++) {
        sum += list.get(i).accept(this);
      }
      return sum;
    }

    @Override
    public long visit(IComplex element) {
      return element.leafCount();
    }

    @Override
    public long visit(IComplexNum element) {
      return element.leafCount();
    }

    @Override
    public long visit(IFraction element) {
      return element.leafCount();
    }
  }

  /**
   * Decides whether a candidate replaces the incumbent when both have the <b>same</b> complexity. A
   * candidate with strictly lower complexity always wins, independent of the policy.
   */
  public enum TiePolicy {
    /** Only a strictly lower complexity wins; ties keep the incumbent. */
    STRICT,
    /** Ties go to the candidate (the last equally good candidate offered wins). */
    ACCEPT,
    /**
     * Ties go to the candidate only if its head has the lower operator precedence, i.e. prefer
     * <code>Plus</code> (310) over <code>Times</code> (400) over <code>Power</code> (590).
     */
    HEAD_PRECEDENCE
  }

  public static class SimplifiedResult {
    IExpr result;

    long minCounter;

    final Function<IExpr, Long> complexityFunction;

    public SimplifiedResult(IExpr minExpr, Function<IExpr, Long> complexityFunction) {
      // this(minExpr, minExpr, complexityFunction);
      this.result = minExpr;
      this.complexityFunction = complexityFunction;
      this.minCounter = complexityFunction.apply(minExpr);
    }

    /**
     * Offer <code>candidate</code> as the new &ldquo;most simplified&rdquo; expression. It is
     * accepted if its complexity is lower than the incumbent's, or if the complexities tie and
     * <code>tiePolicy</code> lets the candidate through.
     *
     * @param candidate the rewritten expression to compare against the incumbent
     * @param tiePolicy how to break a complexity tie
     * @return <code>true</code> if <code>candidate</code> became the new result
     */
    public boolean offer(IExpr candidate, TiePolicy tiePolicy) {
      final long counter = complexityFunction.apply(candidate);
      if (counter > this.minCounter) {
        return false;
      }
      if (counter == this.minCounter) {
        switch (tiePolicy) {
          case STRICT:
            return false;
          case HEAD_PRECEDENCE:
            if (candidate == this.result || !headPrecedenceWins(candidate, this.result)) {
              return false;
            }
            break;
          case ACCEPT:
          default:
            break;
        }
      }
      this.minCounter = counter;
      this.result = candidate;
      return true;
    }

    /**
     * Offer <code>candidate</code> with a tie-break that is only decided — and only computed — when
     * the complexities actually tie. Use this when the tie-break has to inspect the expression, so
     * that a possibly expensive test is not paid for on every offer.
     *
     * @param candidate the rewritten expression to compare against the incumbent
     * @param tieAccepts evaluated <b>only</b> on a complexity tie; <code>true</code> lets the
     *        candidate through
     * @return <code>true</code> if <code>candidate</code> became the new result
     */
    public boolean offerOnTie(IExpr candidate, BooleanSupplier tieAccepts) {
      final long counter = complexityFunction.apply(candidate);
      if (counter > this.minCounter) {
        return false;
      }
      if (counter == this.minCounter && !tieAccepts.getAsBoolean()) {
        return false;
      }
      this.minCounter = counter;
      this.result = candidate;
      return true;
    }

    /**
     * Return <code>true</code> if both expressions are {@link IExpr#isPlusTimesPower()} and
     * <code>candidate</code>'s head binds less tightly than <code>incumbent</code>'s.
     */
    private static boolean headPrecedenceWins(IExpr candidate, IExpr incumbent) {
      if (!candidate.isPlusTimesPower() || !incumbent.isPlusTimesPower()) {
        return false;
      }
      final int candidateID = candidate.headID();
      final int incumbentID = incumbent.headID();
      if (candidateID == incumbentID) {
        return false;
      }
      switch (candidateID) {
        case ID.Plus: // precedence 310
          return incumbentID == ID.Times // precedence 400
              || incumbentID == ID.Power; // precedence 590
        case ID.Times: // precedence 400
          return incumbentID == ID.Power; // precedence 590
        default:
          return false;
      }
    }

    public boolean checkLess(IExpr expr) {
      return offer(expr, TiePolicy.STRICT);
    }

    public boolean checkLessEqual(IExpr expr) {
      return offer(expr, TiePolicy.ACCEPT);
    }

    public boolean checkLessPlusTimesPower(IExpr expr) {
      return offer(expr, TiePolicy.HEAD_PRECEDENCE);
    }

    public IExpr getResult() {
      return result;
    }
  }

  public static HashedOrderlessMatcherPlus PLUS_ORDERLESS_MATCHER =
      new HashedOrderlessMatcherPlus();

  public static HashedOrderlessMatcherTimes TIMES_ORDERLESS_MATCHER;

  static {
    // Cosh(x)+Sinh(x) -> Exp(x)
    PLUS_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRules( //
        F.Cosh(x_), //
        F.Sinh(x_), //
        F.Exp(x), //
        false, //
        null, true));
    // Moved out of Arithmetic.Plus: WMA does not fold these during plain evaluation, only
    // under Simplify/FullSimplify. Unlike the complementary-angle pairs these may fire on part of
    // a larger sum — a+ArcTan(-2)+ArcTan(-1/2)+2/3 becomes 2/3+a-Pi/2 — because the matched terms
    // collapse to a constant, which is ordinary numeric simplification.
    // ArcTan(x)+ArcTan(1/x) == Pi/2 for positive x
    PLUS_ORDERLESS_MATCHER.defineHashRule(F.ArcTan(x_), F.ArcTan(y_), //
        F.CPiHalf, //
        F.And(F.Positive(x), F.Equal(y, F.Power(x, F.CN1))));
    // ArcTan(1/2) + ArcTan(1/3) == Pi/4
    PLUS_ORDERLESS_MATCHER.defineHashRule(F.ArcTan(F.C1D3), F.ArcTan(F.C1D2), //
        F.CPiQuarter);
    // ArcTan(1/3) + ArcTan(1/7) == ArcTan(1/2)
    PLUS_ORDERLESS_MATCHER.defineHashRule(F.ArcTan(F.C1D3), F.ArcTan(F.QQ(1L, 7L)), //
        F.ArcTan(F.C1D2));
  }

  /**
   * The right-hand-side of a Pythagorean identity <code>c + s*f(x)^2 -&gt; +/- g(x)^2</code>: the
   * head <code>g</code> to rewrite the squared function to, and whether that square is negated.
   */
  private static final class PythagoreanIdentity {
    final IBuiltInSymbol resultHead;

    final boolean negate;

    PythagoreanIdentity(IBuiltInSymbol resultHead, boolean negate) {
      this.resultHead = resultHead;
      this.negate = negate;
    }
  }

  /** <code>1 + f(x)^2</code>, keyed by the {@link ID} of <code>f</code>. */
  private static final Map<Integer, PythagoreanIdentity> ONE_PLUS_SQUARE = new HashMap<>();

  /** <code>1 - f(x)^2</code>, keyed by the {@link ID} of <code>f</code>. */
  private static final Map<Integer, PythagoreanIdentity> ONE_MINUS_SQUARE = new HashMap<>();

  /** <code>-1 + f(x)^2</code>, keyed by the {@link ID} of <code>f</code>. */
  private static final Map<Integer, PythagoreanIdentity> MINUS_ONE_PLUS_SQUARE = new HashMap<>();

  /** <code>-1 - f(x)^2</code>, keyed by the {@link ID} of <code>f</code>. */
  private static final Map<Integer, PythagoreanIdentity> MINUS_ONE_MINUS_SQUARE = new HashMap<>();

  static {
    // 1+Cot(x)^2 -> Csc(x)^2 etc.
    ONE_PLUS_SQUARE.put(ID.Cot, new PythagoreanIdentity(S.Csc, false));
    ONE_PLUS_SQUARE.put(ID.Csch, new PythagoreanIdentity(S.Coth, false));
    ONE_PLUS_SQUARE.put(ID.Sinh, new PythagoreanIdentity(S.Cosh, false));
    ONE_PLUS_SQUARE.put(ID.Tan, new PythagoreanIdentity(S.Sec, false));

    // 1-Cos(x)^2 -> Sin(x)^2 etc.
    ONE_MINUS_SQUARE.put(ID.Cos, new PythagoreanIdentity(S.Sin, false));
    ONE_MINUS_SQUARE.put(ID.Cosh, new PythagoreanIdentity(S.Sinh, true));
    ONE_MINUS_SQUARE.put(ID.Coth, new PythagoreanIdentity(S.Csch, true));
    ONE_MINUS_SQUARE.put(ID.Csc, new PythagoreanIdentity(S.Cot, true));
    ONE_MINUS_SQUARE.put(ID.Sec, new PythagoreanIdentity(S.Tan, true));
    ONE_MINUS_SQUARE.put(ID.Sech, new PythagoreanIdentity(S.Tanh, false));
    ONE_MINUS_SQUARE.put(ID.Sin, new PythagoreanIdentity(S.Cos, false));
    ONE_MINUS_SQUARE.put(ID.Tanh, new PythagoreanIdentity(S.Sech, false));

    // -1+Cos(x)^2 -> -Sin(x)^2 etc.
    MINUS_ONE_PLUS_SQUARE.put(ID.Cos, new PythagoreanIdentity(S.Sin, true));
    MINUS_ONE_PLUS_SQUARE.put(ID.Csc, new PythagoreanIdentity(S.Cot, false));
    MINUS_ONE_PLUS_SQUARE.put(ID.Cosh, new PythagoreanIdentity(S.Sinh, false));
    MINUS_ONE_PLUS_SQUARE.put(ID.Coth, new PythagoreanIdentity(S.Csch, false));
    MINUS_ONE_PLUS_SQUARE.put(ID.Sec, new PythagoreanIdentity(S.Tan, false));
    MINUS_ONE_PLUS_SQUARE.put(ID.Sech, new PythagoreanIdentity(S.Tanh, true));
    MINUS_ONE_PLUS_SQUARE.put(ID.Sin, new PythagoreanIdentity(S.Cos, true));
    MINUS_ONE_PLUS_SQUARE.put(ID.Tanh, new PythagoreanIdentity(S.Sech, true));

    // -1-Cot(x)^2 -> -Csc(x)^2 etc.
    MINUS_ONE_MINUS_SQUARE.put(ID.Cot, new PythagoreanIdentity(S.Csc, true));
    MINUS_ONE_MINUS_SQUARE.put(ID.Csch, new PythagoreanIdentity(S.Coth, true));
    MINUS_ONE_MINUS_SQUARE.put(ID.Tan, new PythagoreanIdentity(S.Sec, true));
    MINUS_ONE_MINUS_SQUARE.put(ID.Sinh, new PythagoreanIdentity(S.Cosh, true));
  }

  /**
   * Look up the Pythagorean identity for <code>(+/-1) (+/-) f(x)^2</code>.
   *
   * @param constantIsOne <code>true</code> for a leading <code>1</code>, <code>false</code> for
   *        <code>-1</code>
   * @param sqrType {@link #SQR_ARG} if the square is added, {@link #NEGATIVE_SQR_ARG} if subtracted
   * @param headID the {@link ID} of the squared function
   * @return the matching identity, or <code>null</code> if there is none
   */
  private static PythagoreanIdentity pythagoreanIdentity(boolean constantIsOne, int sqrType,
      int headID) {
    final Map<Integer, PythagoreanIdentity> table;
    if (constantIsOne) {
      table = (sqrType == SQR_ARG) ? ONE_PLUS_SQUARE : ONE_MINUS_SQUARE;
    } else {
      table = (sqrType == SQR_ARG) ? MINUS_ONE_PLUS_SQUARE : MINUS_ONE_MINUS_SQUARE;
    }
    return table.get(headID);
  }

  /** No special function expression was found in the args of the expression */
  private static final int UNDEFINED = -1;
  /** A trigonometric or hyperbolic function <code>trig(x)^2</code> was found. */
  private static final int SQR_ARG = 1;

  /** A trigonometric or hyperbolic function <code>-trig(x)^2</code> was found. */
  private static final int NEGATIVE_SQR_ARG = 2;

  /**
   * Return <code>Arg(x+I*y)</code>. If possible, simplify <code>Arg(Re(z)+I*Im(z))</code> to
   * <code>Arg(z)</code>, or simplify <code>Arg(factor * (Re(z)+I*Im(z)))</code> to
   * <code>Arg( (+/- 1) * z)</code>.
   * 
   * @param realPart the real part
   * @param imaginaryPart the imaginary part
   * @return
   */
  public static IExpr argReXImY(IExpr realPart, IExpr imaginaryPart, EvalEngine engine) {
    // TODO: add this rule to FullSimplify
    IExpr factorTerms = engine.evaluate(F.FactorTerms(F.Plus(realPart, imaginaryPart)));
    boolean negativeFactor = false;
    if (factorTerms.isTimes2() && factorTerms.first().isReal()) {
      // a factor could be determined
      IExpr factor = factorTerms.first();
      if (factor.isNegative()) {
        negativeFactor = true;
      }
      realPart = engine.evaluate(F.Divide(realPart, factor));
      imaginaryPart = engine.evaluate(F.Divide(imaginaryPart, factor));
    }

    final IExpr arg;
    if (realPart.isRe() && realPart.first().isSymbol() //
        && imaginaryPart.isIm() //
        && realPart.first().equals(imaginaryPart.first())) {
      ISymbol symbol = (ISymbol) realPart.first();
      arg = F.Arg(negativeFactor ? F.Times(F.CN1, symbol) : symbol);
    } else {
      final IExpr z;
      if (negativeFactor) {
        z = F.Plus(F.Times(F.CN1, realPart), F.Times(F.CNI, imaginaryPart));
      } else {
        z = F.Plus(realPart, F.Times(F.CI, imaginaryPart));
      }
      arg = F.Arg(z);
    }
    return arg;
  }

  /**
   * Creata the complexity function which determines the &quot;more simplified&quot; expression.
   *
   * @param complexityFunctionHead
   * @param engine
   * @return
   */
  public static Function<IExpr, Long> createComplexityFunction(IExpr complexityFunctionHead,
      EvalEngine engine) {
    Function<IExpr, Long> complexityFunction = x -> x.leafCountSimplify();
    if (complexityFunctionHead.isPresent()) {
      final IExpr head = complexityFunctionHead;
      complexityFunction = x -> {
        IExpr temp = engine.evaluate(F.unaryAST1(head, x));
        if (temp.isInteger() && !temp.isNegative()) {
          return ((IInteger) temp).toLong();
        }
        return Long.MAX_VALUE;
      };
    }
    return complexityFunction;
  }


  public static HashedOrderlessMatcherTimes initTimesHashMatcher() {
    HashedOrderlessMatcherTimes timesMatcher = new HashedOrderlessMatcherTimes();
    // Abs(x_)*Sign(x_) := x
    timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
        F.Abs(x_), //
        F.Sign(x_), //
        F.x));
    // Abs(x_)*Abs(y_) := Abs(x*y). HashedPatternRulesTimes combines the two factors only when their
    // exponents are equal, so Abs(a)*Abs(b) and Abs(a)^-1*Abs(b)^-1 contract while a mixed pair
    // such as Abs(a)/Abs(b) is left alone instead of being folded into a wrong Abs(a*b).
    timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
        F.Abs(x_), //
        F.Abs(y_), //
        F.Abs(F.Times(x, y))));
    return timesMatcher;
  }

  public static AbstractVisitorLong leafCountVisitor() {
    return new LeafCountVisitor(0);
  }

  /**
   * Return a value > 0 at index <code>0</code>, if a power trig- or hyperbolicfunction was found.
   * The determined type <code>SQR_ARG,NEGATIVE_SQR_ARG</code> of the expression at index
   * <code>1</code>.
   *
   * @param plusAST the <code>Plus( ... )</code> expression
   * @param fromPosition start searching at this index inclusive.
   * @return a value > 0 at index <code>0</code>, if a power trig- or hyperbolicfunction was found.
   *         The type of expression found at index <code>1</code>.
   */
  private static int[] plusASTIndexOf(IASTMutable plusAST, int fromPosition) {
    for (int i = fromPosition; i < plusAST.size(); i++) {
      IExpr x = plusAST.get(i);
      if (x.isPower() && x.exponent().isNumEqualInteger(F.C2) && x.base().size() == 2 && //
          (x.base().isTrigFunction() || x.base().isHyperbolicFunction())) {
        return new int[] {i, SQR_ARG};
      } else if (x.isTimes2() && x.first().isMinusOne() && x.second().isPower() && //
          x.second().exponent().isNumEqualInteger(F.C2) && x.second().base().size() == 2 && //
          (x.second().base().isTrigFunction() || x.second().base().isHyperbolicFunction())) {
        return new int[] {i, NEGATIVE_SQR_ARG};
      }
    }
    return new int[] {-1, UNDEFINED};
  }

  public static IExpr simplifyStep(IExpr arg1, IExpr defaultResult, boolean fullSimplify,
      boolean noApart, EvalEngine engine) {
    Function<IExpr, Long> complexityFunction = createComplexityFunction(F.NIL, engine);
    long minCounter = complexityFunction.apply(arg1);
    return simplifyStep(arg1, defaultResult, complexityFunction, minCounter, fullSimplify, noApart,
        engine);
  }

  public static IExpr simplifyStep(IExpr arg1, IExpr defaultResult,
      Function<IExpr, Long> complexityFunction, long minCounter, boolean fullSimplify,
      boolean noApart, EvalEngine engine) {
    long count;
    IExpr temp;
    // One cache for the whole fixpoint loop below. Every pass walks the expression again, and most
    // of its subexpressions are the ones the previous pass already saw, so the pipeline is asked
    // the same question over and over. Everything tryTransformations() depends on — the complexity
    // function, the two mode flags, the engine's assumptions — is fixed for this call, so a hit is
    // the same answer and not merely a similar one.
    Map<IExpr, IExpr> transformationCache = new HashMap<>();
    // Every entry into the simplifier passes through here, so this is where the reentrancy depth is
    // maintained. Builtins that the pipeline calls as a rewrite candidate — Apart, PossibleZeroQ —
    // consult it to skip their own internal simplification while we are already running.
    engine.incSimplifyDepth();
    try {
      temp = arg1.accept(
          new SimplifyUtil(complexityFunction, fullSimplify, engine, noApart, transformationCache));
      while (temp.isPresent()) {
        // No early exit on an atom: leafCountSimplify() charges an integer by its number of digits,
        // so an atom is not automatically the cheapest result. Returning one unconditionally made
        // Integrate(Sin(x)^3,x) come back unevaluated, where the loop used to keep defaultResult
        // because the atom weighed more.
        count = complexityFunction.apply(temp);
        if (count == minCounter) {
          return temp;
        }
        if (count < minCounter) {
          minCounter = count;
          defaultResult = temp;
          temp = defaultResult.accept(new SimplifyUtil(complexityFunction, fullSimplify, engine,
              noApart, transformationCache));
        } else {
          return defaultResult;
        }
      }
      return defaultResult;
    } finally {
      engine.decSimplifyDepth();
    }
  }

  /**
   * Simplify <code>Log(x)+Log(y)+p*Log(z)</code> if x, y, z are real numbers and p is an integer
   * number
   *
   * @param plusAST
   * @return
   */
  /**
   * Rewrite a sum of logarithms that all carry the same integer factor by pulling that factor out:
   * <code>4*Log(2)+4*Log(3)</code> becomes <code>4*Log(6)</code>.
   *
   * <p>
   * {@link #tryPlusLog(IAST)} folds the whole sum into a single logarithm, which is only an
   * improvement while the resulting integer stays short: <code>Log(1296)</code> weighs 5 against
   * the 4 of <code>4*Log(6)</code>. The candidate is offered with a strict comparison, so a tie
   * keeps the single logarithm — <code>3*Log(2)+3*Log(3)</code> stays <code>Log(216)</code>, where
   * both forms weigh 4.
   *
   * <p>
   * Reducing an already collapsed <code>Log(n)</code> by its perfect powers would be the more
   * general rule, but a wrong one: it turns <code>2*Log(100)</code> into <code>4*Log(10)</code> (5)
   * instead of the expected <code>Log(10000)</code> (6). Only a factor the input itself shares is
   * pulled out.
   *
   * @param plusAST a sum whose summands are all logarithms or integer multiples of logarithms
   * @return the factored sum, or {@link F#NIL} if there is no common factor greater than 1
   */
  /**
   * Split a positive integer into <code>base^exponent</code> with the largest possible exponent,
   * e.g. <code>16</code> into <code>2^4</code> and <code>1296</code> into <code>6^4</code>.
   *
   * @param expr the candidate integer
   * @return <code>{base, exponent}</code>, or <code>null</code> if <code>expr</code> is not an
   *         integer perfect power
   */
  private IInteger[] perfectPower(IExpr expr) {
    if (!expr.isInteger() || !expr.isPositive() || expr.isOne()) {
      return null;
    }
    IInteger n = (IInteger) expr;
    IExpr factors = eval(F.FactorInteger(n));
    if (!factors.isList()) {
      return null;
    }
    IAST list = (IAST) factors;
    IInteger exponent = null;
    for (int i = 1; i < list.size(); i++) {
      IExpr pair = list.get(i);
      if (!pair.isList2() || !pair.second().isInteger()) {
        return null;
      }
      IInteger e = (IInteger) pair.second();
      exponent = (exponent == null) ? e : exponent.gcd(e);
    }
    if (exponent == null || exponent.isOne()) {
      return null;
    }
    IExpr root = S.Power.of(n, F.Divide(F.C1, exponent));
    if (!root.isInteger()) {
      return null;
    }
    return new IInteger[] {(IInteger) root, exponent};
  }

  /**
   * Put each group of summands that share a denominator over that denominator, instead of putting
   * the whole sum over one common denominator: <code>a/x+b/x+c/y</code> becomes
   * <code>(a+b)/x+c/y</code>, where {@link S#Together} on the whole sum would give the heavier
   * <code>(c*x+a*y+b*y)/(x*y)</code>.
   *
   * @param plusAST the sum to regroup
   * @return the regrouped sum, or {@link F#NIL} if there is nothing to group
   */
  /**
   * Test whether the given denominators share no factor, so that putting each group over its own
   * denominator does not throw away a cancellation.
   *
   * @param denominators the distinct denominators of the groups
   * @return <code>true</code> if every pair has a constant greatest common divisor
   */
  private boolean areCoprime(Collection<IExpr> denominators) {
    IExpr[] values = denominators.toArray(new IExpr[0]);
    for (int i = 0; i < values.length; i++) {
      for (int j = i + 1; j < values.length; j++) {
        try {
          IExpr gcd = eval(F.PolynomialGCD(values[i], values[j]));
          if (!gcd.isNumber()) {
            return false;
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          // PolynomialGCD does not handle every denominator; be conservative
          return false;
        }
      }
    }
    return true;
  }

  /** The complementary-angle pairs: <code>f(u) + g(u) == Pi/2</code>. */
  private static final IBuiltInSymbol[][] COMPLEMENTARY_PAIRS = { //
      {S.ArcSin, S.ArcCos}, //
      {S.ArcTan, S.ArcCot}, //
      {S.ArcSec, S.ArcCsc}};

  /**
   * Combine a sum that is built <b>only</b> from one complementary pair applied to one common
   * argument, using <code>f(u) == Pi/2 - g(u)</code>: <code>ArcSin(x)+ArcCos(x)</code> becomes
   * <code>Pi/2</code> and <code>-3*ArcSin(x)-2*ArcCos(x)</code> becomes
   * <code>-3*Pi/2+ArcCos(x)</code>. Both eliminations are offered and the complexity function
   * picks.
   *
   * <p>
   * Every summand has to be a multiple of <code>f(u)</code> or <code>g(u)</code> with the
   * <b>same</b> <code>u</code>. That restriction cannot come from the complexity function, because
   * all of these weigh exactly the same: <code>ArcSin(x)+ArcCos(x)+z</code> and <code>Pi/2+z</code>
   * are both 7, <code>ArcSin(x)+ArcCos(x)+ArcSin(y)</code> and <code>Pi/2+ArcSin(y)</code> are both
   * 8, and WMA leaves both alone — while <code>-3*ArcSin(x)-2*ArcCos(x)</code>, also a tie, it does
   * combine.
   *
   * <p>
   * <code>ArcTan(u)+ArcCot(u) == Pi/2</code> only holds for <code>Re(u) &gt; 0</code> — at
   * <code>u = -1</code> both terms are <code>-Pi/4</code> — so that pair needs a positive argument.
   *
   * @param plusAST the sum to combine
   * @param sResult collects the two candidate eliminations
   */
  private void complementaryAngleCombination(IAST plusAST, SimplifiedResult sResult) {
    for (IBuiltInSymbol[] pair : COMPLEMENTARY_PAIRS) {
      IExpr argument = F.NIL;
      IExpr[] coefficient = {F.C0, F.C0};
      boolean matched = true;
      for (int i = 1; i < plusAST.size(); i++) {
        IExpr summand = plusAST.get(i);
        IExpr factor = F.C1;
        IExpr angle = summand;
        if (summand.isTimes()) {
          IAST times = (IAST) summand;
          int index = times.indexOf(x -> x.isAST(pair[0], 2) || x.isAST(pair[1], 2));
          if (index <= 0) {
            matched = false;
            break;
          }
          angle = times.get(index);
          factor = times.removePositionsAtCopy(new int[] {index}, 1).oneIdentity1();
        }
        int slot = angle.isAST(pair[0], 2) ? 0 : (angle.isAST(pair[1], 2) ? 1 : -1);
        if (slot < 0) {
          matched = false;
          break;
        }
        if (argument.isNIL()) {
          argument = angle.first();
        } else if (!argument.equals(angle.first())) {
          matched = false;
          break;
        }
        coefficient[slot] = coefficient[slot].plus(factor);
      }
      if (!matched || argument.isNIL() || coefficient[0].isZero() || coefficient[1].isZero()) {
        continue;
      }
      if (pair[0] == S.ArcTan && !argument.isPositiveResult()) {
        continue;
      }
      // f(u) = Pi/2 - g(u), so c0*f(u)+c1*g(u) == c0*Pi/2 + (c1-c0)*g(u), and symmetrically
      for (int keep = 0; keep < 2; keep++) {
        int drop = 1 - keep;
        sResult.checkLessEqual(eval(F.Plus( //
            F.Times(coefficient[drop], F.CPiHalf), //
            F.Times(coefficient[keep].subtract(coefficient[drop]),
                F.unaryAST1(pair[keep], argument)))));
      }
      return;
    }
  }

  private IExpr tryGroupwiseTogether(IAST plusAST) {
    if (plusAST.argSize() < 3) {
      // with two summands the grouping is what Together() already does
      return F.NIL;
    }
    Map<IExpr, IASTAppendable> groups = new LinkedHashMap<>();
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr summand = plusAST.get(i);
      IExpr denominator = eval(F.Denominator(summand));
      groups.computeIfAbsent(denominator, key -> F.PlusAlloc(plusAST.size())).append(summand);
    }
    if (groups.size() < 2 || groups.size() == plusAST.argSize()) {
      // a single group is plain Together(), all-distinct denominators have nothing to combine
      return F.NIL;
    }
    if (!areCoprime(groups.keySet())) {
      // When one denominator divides another — (d+e*x^2) and (d+e*x^2)^2 — the whole sum belongs
      // over one common denominator and splitting it into groups is a step backwards.
      return F.NIL;
    }
    IASTAppendable result = F.PlusAlloc(groups.size());
    for (IASTAppendable group : groups.values()) {
      result.append(group.isAST1() ? group.arg1() : eval(F.Together(group)));
    }
    return eval(result);
  }

  /**
   * Recognize a perfect power hidden among the summands: the variable-dependent part of
   * <code>1+c^2+2*c*d+d^2</code> factors to <code>(c+d)^2</code>, giving <code>1+(c+d)^2</code>.
   * {@link S#Factor} on the whole sum cannot find this, because the sum including the constant is
   * irreducible.
   *
   * <p>
   * Only a perfect power is accepted. Factoring the variable part of any sum is far too eager —
   * <code>2+3*x+x^2</code> would become <code>2+x*(3+x)</code>, which is one leaf lighter but not
   * what WMA returns.
   *
   * @param plusAST the sum to inspect
   * @return the sum with its variable part replaced by a power, or {@link F#NIL}
   */
  private IExpr tryFactorConstantPlusPower(IAST plusAST) {
    IASTAppendable constants = F.PlusAlloc(plusAST.size());
    IASTAppendable rest = F.PlusAlloc(plusAST.size());
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr summand = plusAST.get(i);
      if (summand.isNumber()) {
        constants.append(summand);
      } else {
        rest.append(summand);
      }
    }
    if (constants.argSize() == 0 || rest.argSize() < 2) {
      return F.NIL;
    }
    IExpr factored = eval(F.Factor(rest.oneIdentity0()));
    if (!factored.isPower() || !factored.exponent().isInteger()) {
      return F.NIL;
    }
    IInteger exponent = (IInteger) factored.exponent();
    if (!exponent.isPositive() || exponent.isOne()) {
      return F.NIL;
    }
    constants.append(factored);
    return eval(constants);
  }

  /**
   * Collect the sum in each of its variables, simplifying the coefficients:
   * <code>a^2+2*a*b+b^2+2*a*x+2*b*x+x^2+c^2*x^2+2*c*d*x^2+d^2*x^2</code> becomes
   * <code>(a+b)^2+2*(a+b)*x+(1+(c+d)^2)*x^2</code>.
   *
   * <p>
   * {@link S#Factor} cannot reach this — the sum is irreducible as a whole, and the structure only
   * shows up per power of <code>x</code>.
   *
   * <p>
   * <b>Currently not wired into the pipeline.</b> Passing {@link S#Simplify} as the third argument
   * of {@link S#Collect} simplifies every coefficient recursively, and because Rubi calls
   * <code>Simplify</code> from inside <code>Integrate</code> the work multiplies until the integral
   * gives up: <code>Integrate(x^2/(x^2+Sqrt(1-x^2)),x)</code> came back unevaluated. A safe version
   * has to collect with the binary {@link S#Collect} and then factor each coefficient directly —
   * {@link #tryFactorConstantPlusPower(IAST)} plus {@link S#Factor} are all that these cases need —
   * instead of re-entering the whole simplifier.
   *
   * @param plusAST the sum to collect
   * @param sResult collects the candidates, one per variable
   */
  private void tryCollectVariables(IAST plusAST, SimplifiedResult sResult) {
    if (!fFullSimplify || plusAST.argSize() < 4
        || sResult.minCounter >= Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT) {
      return;
    }
    VariablesSet variables = new VariablesSet(plusAST);
    List<IExpr> vars = variables.getArrayList();
    if (vars.size() < 2 || vars.size() > 6) {
      return;
    }
    for (int i = 0; i < vars.size(); i++) {
      // Each variable is tried on its own: Factor() can throw, and without this the first failure
      // would abort the loop and the variable that actually pays off — x in
      // a^2+2*a*b+b^2+2*a*x+...+d^2*x^2 — would never be reached.
      try {
        sResult.checkLess(collectOnVariable(plusAST, vars.get(i)));
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
    }
  }

  /**
   * Group the summands by their power of <code>variable</code> and factor each group's coefficient:
   * <code>a^2+2*a*b+b^2+2*a*x+2*b*x+x^2+c^2*x^2+2*c*d*x^2+d^2*x^2</code> becomes
   * <code>(a+b)^2+2*(a+b)*x+(1+(c+d)^2)*x^2</code>.
   *
   * <p>
   * The grouping is done here rather than by {@link S#Collect}, whose result keeps the
   * <code>x^0</code> part as separate summands — <code>a^2+2*a*b+b^2+(2*a+2*b)*x+...</code> — so
   * that <code>a^2+2*a*b+b^2</code> never reaches {@link S#Factor} as one expression.
   *
   * @param plusAST the sum to collect
   * @param variable the variable to collect on
   * @return the collected and factored sum
   */
  private IExpr collectOnVariable(IAST plusAST, IExpr variable) {
    Map<IExpr, IASTAppendable> byPower = new LinkedHashMap<>();
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr summand = plusAST.get(i);
      IASTAppendable coefficient = F.TimesAlloc(4);
      IASTAppendable variablePart = F.TimesAlloc(4);
      if (summand.isTimes()) {
        ((IAST) summand).forEach(factor -> {
          if (factor.isFree(variable, true)) {
            coefficient.append(factor);
          } else {
            variablePart.append(factor);
          }
        });
      } else if (summand.isFree(variable, true)) {
        coefficient.append(summand);
      } else {
        variablePart.append(summand);
      }
      byPower.computeIfAbsent(variablePart.oneIdentity1(), key -> F.PlusAlloc(plusAST.size()))
          .append(coefficient.oneIdentity1());
    }
    if (byPower.size() < 2) {
      return plusAST;
    }
    IASTAppendable result = F.PlusAlloc(byPower.size());
    for (Map.Entry<IExpr, IASTAppendable> entry : byPower.entrySet()) {
      IExpr coefficient = bestCoefficientForm(entry.getValue().oneIdentity0());
      IExpr power = entry.getKey();
      result.append(power.isOne() ? coefficient : F.Times(coefficient, power));
    }
    return eval(result);
  }

  /**
   * Pick the cheapest form of one collected coefficient: as it stands, factored, or with a perfect
   * power pulled out of its variable part.
   *
   * @param coefficient the summed coefficient of one power
   * @return the cheapest of the three forms
   */
  private IExpr bestCoefficientForm(IExpr coefficient) {
    if (!coefficient.isPlus()) {
      return coefficient;
    }
    SimplifiedResult best = new SimplifiedResult(coefficient, fComplexityFunction);
    best.checkLess(eval(F.Factor(coefficient)));
    IExpr constantPlusPower = tryFactorConstantPlusPower((IAST) coefficient);
    if (constantPlusPower.isPresent()) {
      best.checkLess(constantPlusPower);
    }
    return best.result;
  }

  private IExpr tryPlusLogCommonFactor(IAST plusAST) {
    if (plusAST.size() <= 2) {
      return F.NIL;
    }
    IInteger gcd = null;
    IInteger[] coefficients = new IInteger[plusAST.size()];
    IExpr[] arguments = new IExpr[plusAST.size()];
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr summand = plusAST.get(i);
      if (summand.isTimes2() && summand.first().isInteger() //
          && summand.second().isLog() && summand.second().first().isRealResult()) {
        coefficients[i] = (IInteger) summand.first();
        arguments[i] = summand.second().first();
      } else if (summand.isLog() && summand.first().isRealResult()) {
        coefficients[i] = F.C1;
        arguments[i] = summand.first();
      } else {
        // every summand has to be a logarithm, otherwise there is nothing to pull out
        return F.NIL;
      }
      // A summand may already have absorbed its factor: the children of the sum are simplified
      // first, and 4*Log(2) collapses to the cheaper Log(16) before this runs. Split such an
      // argument back into base^exponent so that Log(16) counts as 4*Log(2) again.
      IInteger[] power = perfectPower(arguments[i]);
      if (power != null) {
        coefficients[i] = coefficients[i].multiply(power[1]);
        arguments[i] = power[0];
      }
      IInteger magnitude = coefficients[i].abs();
      gcd = (gcd == null) ? magnitude : gcd.gcd(magnitude);
      if (gcd.isOne()) {
        return F.NIL;
      }
    }
    if (gcd == null || gcd.isOne()) {
      return F.NIL;
    }
    IExpr product = F.C1;
    for (int i = 1; i < plusAST.size(); i++) {
      product = product.multiply(S.Power.of(arguments[i], coefficients[i].div(gcd)));
    }
    return F.Times(gcd, F.Log(eval(product)));
  }

  private IExpr tryPlusLog(IAST plusAST) {
    if (plusAST.size() > 2) {
      IASTAppendable logPlus = F.PlusAlloc(plusAST.size());
      IExpr a1 = F.NIL;
      boolean evaled = false;
      for (int i = 1; i < plusAST.size(); i++) {
        IExpr a2 = plusAST.get(i);
        IExpr arg = F.NIL;
        if (a2.isTimes2() && a2.first().isInteger() && //
            a2.second().isLog() && a2.second().first().isRealResult()) {
          arg = S.Power.of(a2.second().first(), a2.first());
        } else if (a2.isLog() && a2.first().isRealResult()) {
          arg = a2.first();
        }
        if (arg.isRealResult()) {
          if (a1.isPresent()) {
            a1 = a1.multiply(arg);
            evaled = true;
          } else {
            a1 = arg;
          }
          continue;
        }
        logPlus.append(a2);
      }
      if (evaled) {
        a1 = eval(a1);
        if (logPlus.isEmpty()) {
          return F.Log(a1);
        } else {
          logPlus.append(F.Log(a1));
          return logPlus;
        }
      }
    }
    return F.NIL;
  }

  private static IExpr tryTimesLog(IAST timesAST) {
    if (timesAST.size() > 2 && timesAST.first().isInteger() && !timesAST.first().isMinusOne()) {

      for (int i = 2; i < timesAST.size(); i++) {
        IExpr temp = timesAST.get(i);
        if (temp.isLog() && temp.first().isReal()) {
          IAST result = timesAST.splice(i, 1, F.Log(S.Power.of(temp.first(), timesAST.first())));
          return result.splice(1).oneIdentity0();
        }
      }
    }
    return F.NIL;
  }

  final IsBasicExpressionVisitor isBasicAST = new IsBasicExpressionVisitor();

  /**
   * This function is used to determine the “weight” of an expression. For example by counting the
   * leafs of an expression with the <code>IExpr#leafCountSimplify()</code> method.
   */
  final Function<IExpr, Long> fComplexityFunction;

  /** If <code>true</code> we are in full simplify mode (i.e. function FullSimplify) */
  final boolean fFullSimplify;

  final boolean fNoApart;

  /** The current evlaution engine */
  final EvalEngine fEngine;

  /**
   * Memoizes {@link #tryTransformations(IExpr)}, shared with the visitors of the other passes of
   * the {@link #simplifyStep} fixpoint loop. See {@link #MAX_TRANSFORMATION_CACHE_SIZE}.
   */
  final Map<IExpr, IExpr> fTransformationCache;

  public SimplifyUtil(Function<IExpr, Long> complexityFunction, boolean fullSimplify,
      EvalEngine engine, boolean noApart) {
    this(complexityFunction, fullSimplify, engine, noApart, new HashMap<>());
  }

  public SimplifyUtil(Function<IExpr, Long> complexityFunction, boolean fullSimplify,
      EvalEngine engine, boolean noApart, Map<IExpr, IExpr> transformationCache) {
    super();
    fEngine = engine;
    fComplexityFunction = complexityFunction;
    fFullSimplify = fullSimplify;
    fNoApart = noApart;
    fTransformationCache = transformationCache;
  }

  private IExpr eval(IExpr a) {
    return fEngine.evaluate(a);
  }

  private void functionExpand(IExpr expr, SimplifiedResult sResult) {
    if (expr.isBooleanFunction()) {
      try {
        IExpr temp = eval(F.BooleanMinimize(expr));
        if (sResult.checkLessPlusTimesPower(temp)) {
          expr = temp;
        }
        return;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        //
      }
    }
    if (fFullSimplify) {
      try {
        IExpr temp = eval(F.FunctionExpand(expr));
        if (sResult.checkLessPlusTimesPower(temp)) {
          expr = temp;
        } else if (!temp.equals(expr)) {
          // FunctionExpand can return a larger intermediate expression which only collapses after
          // the surrounding factors are expanded. Example: denesting Sqrt(3-Sqrt(5)) in
          // Sqrt(3-Sqrt(5))*(3+Sqrt(5))*(-Sqrt(2)+Sqrt(10)) grows the expression, but expanding the
          // denested product reduces it to 8.
          // Only expand: the full tryTransformations() pipeline would call Apart(), which for a
          // variable-free Times re-enters simplifyStep() in fullSimplify mode and comes back here,
          // recursing on an ever larger expression.
          IExpr expanded = F.evalExpandAll(temp, fEngine);
          if (sResult.checkLessPlusTimesPower(expanded)) {
            expr = expanded;
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        //
      }
      if (expr.isAST(S.Arg, 2)) {
        try {
          IExpr re = expr.first().re();
          IExpr im = expr.first().im();
          IExpr temp = argReXImY(re, im, fEngine);
          sResult.checkLessPlusTimesPower(temp);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          //
        }
      } else if (expr.isAST(S.Mod, 3) && expr.first().isPlus()) {
        IAST plusAST = (IAST) expr.first();
        IExpr arg2Mod = expr.second();
        int indexOf = plusAST.indexOf(x -> x.isAST(S.Mod, 3) && x.second().equals(arg2Mod));
        if (indexOf > 0) {
          // Mod(Mod(a_, m_) + Mod(b_, m_) + x_, m_) := Mod(a + b + x, m)
          IASTMutable result = plusAST.copy();
          IExpr indexOfArg = result.get(indexOf);
          result.set(indexOf, indexOfArg.first());
          for (int i = indexOf; i < plusAST.size(); i++) {
            IExpr arg = plusAST.get(i);
            if (arg.isAST(S.Mod, 3) && arg.second().equals(arg2Mod)) {
              result.set(i, arg.first());
            }
          }
          sResult.checkLessPlusTimesPower(F.Mod(result, arg2Mod));
        }
      } else if (expr.isTimes()) {
        try {
          // These rules cannot go into TIMES_ORDERLESS_MATCHER: one side is a bare pattern, and
          // that matcher indexes its rules by the head of each factor, so it has nothing to hash.
          // x_ * Conjugate(x_) := Abs(x)^2
          if (contractArgumentFactor(expr, S.Conjugate, u -> F.Sqr(F.Abs(u)), sResult)) {
            expr = sResult.result;
          }
          // x_ * Gamma(x_) := Gamma(1+x)
          if (contractArgumentFactor(expr, S.Gamma, u -> F.Gamma(F.Plus(F.C1, u)), sResult)) {
            expr = sResult.result;
          }

          if (TIMES_ORDERLESS_MATCHER != null) {
            IAST temp = TIMES_ORDERLESS_MATCHER.evaluateRepeatedNoCache((IAST) expr, fEngine);
            if (temp.isPresent()) {
              // The matcher hands back an unevaluated result — Abs(a)*Abs(b) comes out as
              // Times(Abs(a*b)^1), still carrying the Power(..,1) wrapper and the one-element
              // Times. Weighing that against the input makes a correct contraction lose on leaf
              // count, so evaluate it first, exactly as visitPlus() does for the Plus matcher.
              sResult.checkLessPlusTimesPower(eval(temp));
            }
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          //
        }
      }

    } else {
      if (expr.isLog() //
          || (expr.isPower() && expr.first().isAbs())) {
        try {
          expr = eval(F.FunctionExpand(expr));
          sResult.checkLessEqual(expr);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          //
        }
      }
    }
  }

  /**
   * Try reducing for {@link S#Plus} expressions in the denominators of the {@link S#Times} function
   * by creating a conjugate expression and use the rule <code>(a+b)*(a-b) == a^2 - b^2</code>
   * 
   * @param timesAST
   * @param sResult
   */
  private IExpr reduceConjugateFactors(IASTMutable timesAST, SimplifiedResult sResult) {
    IExpr temp;
    IASTAppendable newTimes = F.NIL;
    int i = 1;
    int lastIndex = -1;
    INumber numberFactors = F.C1;
    IExpr exprFactors = F.C1;
    while (i < timesAST.size()) {
      IExpr timesArg = timesAST.get(i);
      if (timesArg.isPower()) {
        IExpr base = timesArg.base();
        IExpr exponent = timesArg.exponent();
        if (timesArg.isPowerReciprocal()) {
          if (base.isPlus() && base.argSize() >= 2 && base.argSize() <= 10) {
            // try multiplying the conjugate
            // example plusDenominator(5+Sqrt(17)) => plusConjugate(5-Sqrt(17))
            IAST plusDenominator = (IAST) base;
            // search for prefered Sqrt() expressions
            int index = plusDenominator.lastIndexOf(x -> (x.isSqrt() && x.first().isNumber()) //
                || (x.isTimes() && x.last().isSqrt() && x.last().first().isNumber()));
            if (index == -1) {
              index = plusDenominator.lastIndexOf(x -> x.isSqrt() //
                  || (x.isTimes() && x.last().isSqrt()));
              if (index == -1) {
                // fall back to default
                index = plusDenominator.argSize();
              }
            }
            IAST plusConjugate =
                plusDenominator.setAtCopy(index, plusDenominator.get(index).negate());
            // example (5+Sqrt(17)) * (5-Sqrt(17))
            IExpr newDenominator = eval(F.Expand(F.Times(plusDenominator, plusConjugate)));
            if (!newDenominator.isZero()
                && newDenominator.leafCount() < plusDenominator.leafCount()) {
              IExpr inversedDenominator = newDenominator.inverse();
              if (inversedDenominator.isNumber()) {
                numberFactors = numberFactors.times((INumber) inversedDenominator);
              } else {
                exprFactors = exprFactors.times(inversedDenominator);
              }
              // replace the reciprocal Power in the timesAST[i] with the plusConjugate
              if (newTimes.isPresent()) {
                newTimes.set(i, plusConjugate);
              } else {
                newTimes = timesAST.setAtClone(i, plusConjugate);
              }
              i++;
              continue; // while
            }
          }
        }
        if ((i + 1 < timesAST.size())
            && ((fFullSimplify && base.isAST()) || (base.isPlus() && base.first().isReal()))) {
          IExpr rhs = timesAST.get(i + 1);
          if (rhs.isPower() && rhs.exponent().equals(exponent) //
              && ((fFullSimplify && rhs.base().isAST())
                  || (rhs.base().isPlus() && rhs.base().first().equals(base.first())))) {
            if (fFullSimplify) {
              IAST test = F.Times(base, rhs.base());
              long minCounter = fComplexityFunction.apply(test);
              temp = simplifyStep(test, F.NIL, fComplexityFunction, minCounter, fFullSimplify,
                  false, fEngine);
              if (temp.isPresent()) {
                IExpr powerSimplified = F.Power(temp, rhs.exponent());
                if (newTimes.isPresent()) {
                  newTimes.set(i, powerSimplified);
                } else {
                  newTimes = timesAST.setAtClone(i, powerSimplified);
                }
                if (++i < newTimes.size()) {
                  // TODO if no check for size is implemented TrigFactor(Sin(x)^2 + Tan(x)^2) throws
                  // IndexOutOfBoundsException
                  newTimes.remove(i);
                }
                continue; // while
              }
            } else {
              IExpr lhsRest = base.rest();
              IExpr rhsRest = rhs.base().rest();
              IExpr zeroCandidate = eval(F.Plus(lhsRest, rhsRest));
              if (zeroCandidate.isZero()) {
                // found something like: (2-rest)^(z) * (2+rest)^(z) ==> (4-rest^2)^(z)
                IExpr powerSimplified =
                    F.Power(F.Subtract(F.Sqr(rhs.base().first()), F.Sqr(lhsRest)), rhs.exponent());
                if (newTimes.isPresent()) {
                  newTimes.set(i, powerSimplified);
                  newTimes.remove(i + 1);
                } else {
                  newTimes = timesAST.setAtClone(i, powerSimplified);
                  newTimes.remove(i + 1);
                }
                i++;
                continue; // while
              }
            }
          }
        }
      }

      if (timesArg.isPlus()) {
        IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(timesArg.first());
        if (negExpr.isPresent()) {
          // try avoiding negative expressions in the first arg of Plus()
          if (lastIndex < 0) {
            lastIndex = i;
          } else {

            if (newTimes.isNIL()) {
              newTimes = timesAST.copyAppendable();
            }
            newTimes.set(lastIndex, timesAST.get(lastIndex).negate());
            newTimes.set(i, timesArg.negate());
            lastIndex = -1;
            i++;
            continue; // while
          }
        }
      }
      i++;
    }
    if (newTimes.isPresent()) {
      sResult.result = timesAST;
      try {
        if (exprFactors.isOne()) {
          temp = eval(newTimes);
          IExpr temp2 = numberFactors.times(temp);
          if (sResult.checkLessEqual(temp2)) {
            if (temp2.isAtom()) {
              return temp2;
            }
          }
        } else {
          temp = F.Times(numberFactors, exprFactors, newTimes);
        }
        temp = eval(F.Expand(temp));
        temp = numberFactors.times(temp);
        if (sResult.checkLessPlusTimesPower(temp)) {
          if (temp.isAtom()) {
            return temp;
          }
        }
        if (temp.isTimes()) {
          temp = eval(F.Expand(temp));
          if (sResult.checkLessPlusTimesPower(temp)) {
            if (temp.isAtom()) {
              return temp;
            }
          }
        }

      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(fFullSimplify ? S.FullSimplify : S.Simplify, rex, EvalEngine.get());
      }
    }
    return F.NIL;
  }

  private IExpr reduceNumberFactor(IASTMutable timesAST) {
    IExpr temp;
    IASTAppendable basicTimes = F.TimesAlloc(timesAST.size());
    IASTAppendable restTimes = F.TimesAlloc(timesAST.size());
    INumber number = null;
    IExpr arg1 = timesAST.arg1();

    if (arg1.isNumber()) {
      if (!arg1.isZero()) {
        number = (INumber) arg1;
      }
    } else if (arg1.isPlus()) { // && arg1.first().isNumber()) {
      long minCounter = fComplexityFunction.apply(arg1);
      IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1.first(), F.CI);
      if (imPart.isPresent()) {
        IExpr negativeAST = eval(F.Distribute(F.Times(F.CI, arg1)));
        long count = fComplexityFunction.apply(negativeAST);
        if (count <= minCounter) {
          return eval(F.Times(negativeAST, F.Distribute(F.Times(F.CNI, timesAST.rest()))));
        }
      } else {
        IExpr negativeAST = eval(F.Distribute(F.Times(F.CN1, arg1)));
        long count = fComplexityFunction.apply(negativeAST);
        if (count <= minCounter) {
          IASTAppendable result = F.TimesAlloc(timesAST.size());
          result.append(F.CN1);
          result.append(negativeAST);
          result.appendAll(timesAST, 2, timesAST.size());
          return result;
        }
      }
    }
    IExpr reduced = F.NIL;
    for (int i = 1; i < timesAST.size(); i++) {
      temp = timesAST.get(i);
      if (temp.accept(isBasicAST)) {
        if (i != 1 && number != null) {
          if (temp.isPlus()) {
            // <number> * Plus[.....]
            reduced = tryExpand(timesAST, (IAST) temp, number, i, false);
          } else if (temp.isPowerReciprocal() && temp.base().isPlus()) {
            // <number> * Power[Plus[...], -1 ]
            reduced = tryExpand(timesAST, (IAST) temp.base(), number.inverse(), i, true);
          }
          if (reduced.isPresent()) {
            return reduced;
          }
        }
        basicTimes.append(temp);
      } else {
        restTimes.append(temp);
      }
    }

    if (basicTimes.size() > 1) {
      temp = tryTransformations(basicTimes.oneIdentity0());
      if (temp.isPresent()) {
        if (restTimes.isAST0()) {
          return temp;
        }
        return F.Times(temp, restTimes);
      }
    }
    return F.NIL;
  }

  /**
   * Check if <code>plusAST</code> has the form <code>+/- 1 + ... + ... </code>. Try to find a
   * trigonometric or hyperbolic function <code>+/- trig(x)^2</code> in the rest of the <code>
   * plusAST</code> and simplify if possible.
   *
   * @param plusAST
   * @return <code>F.NIL</code> if no simplification was found
   */
  private IExpr tryArg1IsOnePlus(IASTMutable plusAST, SimplifiedResult sResult) {
    IExpr plusArg1 = plusAST.arg1();
    if (plusArg1.isOne() || plusArg1.isMinusOne()) {
      int iterIndx = 2;
      while (iterIndx > 0) {
        int[] indx = plusASTIndexOf(plusAST, iterIndx);
        if (indx[0] > 0) {
          IExpr transformResult = F.NIL;
          boolean negate = false;
          // for SQR_ARG the summand is the Power itself, for NEGATIVE_SQR_ARG it is
          // Times(-1, Power(...))
          IExpr summand = plusAST.get(indx[0]);
          IAST power = (IAST) (indx[1] == SQR_ARG ? summand : summand.second());
          IAST trigFunction = (IAST) power.base();
          PythagoreanIdentity identity =
              pythagoreanIdentity(plusArg1.isOne(), indx[1], trigFunction.headID());
          if (identity != null) {
            transformResult = F.unaryAST1(identity.resultHead, trigFunction.arg1());
            negate = identity.negate;
          }

          if (transformResult.isPresent()) {
            // remove -1 or +1 from first position
            IASTMutable result = plusAST.removeAtCopy(1);
            if (negate) {
              result.set(indx[0] - 1, F.Power(transformResult, F.C2).negate());
            } else {
              result.set(indx[0] - 1, F.Power(transformResult, F.C2));
            }
            IExpr temp = result.oneIdentity0();
            if (temp.isPlus()) {
              sResult.checkLessPlusTimesPower(temp);
              return F.NIL;
            }
            return temp;
          }
          iterIndx = indx[0] + 1;
          continue;
        }
        return F.NIL;
      }
    }
    return F.NIL;
  }

  private IExpr tryExpand(IAST timesAST, IAST plusAST, INumber arg1, int i,
      boolean isPowerReciprocal) {
    IExpr expandedAst = tryExpandTransformation(plusAST, F.Times(arg1, plusAST));
    if (expandedAst.isPresent()) {
      IASTAppendable result = F.TimesAlloc(timesAST.size());
      // ast.range(2, ast.size()).toList(result.args());
      result.appendAll(timesAST, 2, timesAST.size());
      if (isPowerReciprocal) {
        result.set(i - 1, F.Power(expandedAst, F.CN1));
      } else {
        result.set(i - 1, expandedAst);
      }
      return result;
    }
    return F.NIL;
  }

  private IExpr tryExpandTransformation(IExpr original, IExpr test) {
    long minCounter = fComplexityFunction.apply(original);
    IExpr temp;
    long count;

    try {
      temp = F.evalExpand(test);
      if (temp != test) {
        IExpr simplified = temp.accept(this);
        // Fallback to temp if the visitor yields NIL (e.g., for atomic integers like 1)
        IExpr result = simplified.isPresent() ? simplified : temp;
        count = fComplexityFunction.apply(result);
        if (count < minCounter) {
          return result;
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }

    return F.NIL;
  }

  /**
   * Try <code>F.PolynomialQuotientRemainder(numerator, denominator, variable)</code> for differnt
   * variables and numerator, denominator combinations.
   *
   * @param numerator
   * @param denominator
   * @param sResult
   */
  private void tryPolynomialQuotientRemainder(IExpr numerator, IExpr denominator,
      SimplifiedResult sResult) {
    IExpr temp;
    VariablesSet variables = new VariablesSet(numerator);
    variables.addVarList(denominator);
    List<IExpr> vars = variables.getArrayList();
    boolean evaled = false;
    for (int i = 0; i < vars.size(); i++) {
      temp = EvalEngine.get()
          .evaluate(F.PolynomialQuotientRemainder(numerator, denominator, vars.get(i)));
      if (temp.isList2() && //
          temp.second().isZero()) {
        // the remainder is 0 here:
        IExpr arg1 = temp.first();
        if (sResult.checkLessPlusTimesPower(arg1)) {
          evaled = true;
          break;
        }
      }
    }
    if (!evaled) {
      for (int i = 0; i < vars.size(); i++) {
        temp = EvalEngine.get()
            .evaluate(F.PolynomialQuotientRemainder(denominator, numerator, vars.get(i)));
        if (temp.isList2() && //
            temp.second().isZero()) {
          // the remainder is 0 here:
          IExpr arg1 = temp.first().reciprocal();
          if (sResult.checkLessPlusTimesPower(arg1)) {
            break;
          }
        }
      }
    }
  }

  /**
   * Move a leading minus sign out of the denominator of a fraction, e.g. rewrite
   * <code>(I*2*x)/(-1-x^2)</code> as <code>(-2*I*x)/(1+x^2)</code>. Both denote the same value, but
   * the negated denominator weighs less.
   *
   * @param expr a fraction to normalize
   * @param util the visitor whose evaluation engine to use
   * @return the normalized fraction, or {@link F#NIL} if the denominator does not lead with a
   *         negative term
   */
  private static IExpr normalizeDenominatorSign(IExpr expr, SimplifyUtil util) {
    if (!expr.isTimes() && !expr.isPower()) {
      return F.NIL;
    }
    IExpr denominator = util.eval(F.Denominator(expr));
    if (denominator.isPlus() && denominator.first().isNegative()) {
      IExpr numerator = util.eval(F.Numerator(expr));
      return util.eval(F.Divide(numerator.negate(), denominator.negate()));
    }
    return F.NIL;
  }

  /**
   * Tie-break for pulling a common factor out of a {@link S#Plus}. The factored form and the sum
   * usually weigh the same, so the complexity function cannot decide between them.
   *
   * <p>
   * Take the factored form when the sum is a polynomial in its variables, or — for a non-polynomial
   * sum — when the factor pulled out is a plain number and no reciprocal power of a variable
   * remains behind:
   *
   * <pre>
   * 3*x+6                            -&gt; 3*(2+x)               polynomial
   * Pi*x+6*x^7                       -&gt; x*(Pi+6*x^6)          polynomial, symbolic factor
   * z+11*z^2+11*z^3+z^4              -&gt; z*(1+11*z+11*z^2+z^3) polynomial, symbolic factor
   * 1/2-(-1)^n/2                     -&gt; 1/2*(1-(-1)^n)        numeric factor, no 1/x left
   * 4*Cos(u)-4*x^2*Sin(u)-...        -&gt; 4*(Cos(u)-...)        numeric factor, no 1/x left
   * 4+2/x                            -&gt; 4+2/x                 remainder 2+1/x has 1/x
   * -4/5+6/(5*x)                     -&gt; -4/5+6/(5*x)          remainder has 1/x
   * n*C(1)/E^2+n*Log(Log(n))/Log(2)  -&gt; stays a sum           not polynomial, factor n symbolic
   * </pre>
   *
   * @param original the sum a common factor was pulled out of
   * @param factored the result of factoring that common term out
   * @return <code>true</code> if the factored form should win a complexity tie
   */
  private static boolean prefersFactoredPlus(IExpr original, IExpr factored) {
    if (!factored.isTimes()) {
      return false;
    }
    if (original.isPlus()) {
      VariablesSet variables = new VariablesSet(original);
      if (original.isPolynomial(variables.getVarList())) {
        return true;
      }
    }
    IAST times = (IAST) factored;
    if (!times.arg1().isNumber()) {
      return false;
    }
    // a reciprocal power of a variable, e.g. x^(-1); E^(-2) does not count, its base is a constant
    return times.rest().oneIdentity1().isFree(
        x -> x.isPower() && x.exponent().isNegative() && !x.base().isNumericFunction(), true);
  }

  /**
   * Contract a pair of factors <code>u</code> and <code>head(u)</code> inside a {@link S#Times}
   * into a single factor, e.g. <code>x*Gamma(x)</code> to <code>Gamma(1+x)</code> and
   * <code>x*Conjugate(x)</code> to <code>Abs(x)^2</code>.
   *
   * <p>
   * Such a rule cannot live in {@link #TIMES_ORDERLESS_MATCHER}: one of its two left-hand-sides is
   * a bare pattern <code>x_</code>, and that matcher indexes rules by the head of each factor, so a
   * bare pattern gives it nothing to hash. (Verified — a bare-pattern rule there never matches.)
   *
   * <p>
   * The contraction wins a complexity tie only when it consumes the <b>whole</b> product, the same
   * rule the {@link S#Plus} hash-rule offer uses. <code>x*Gamma(x)</code> and
   * <code>Gamma(1+x)</code> both weigh 4 and WMA contracts them, while <code>2*x*Gamma(x)</code> —
   * where a factor is left over — stays as it is.
   *
   * @param expr the {@link S#Times} expression to search
   * @param head the head to look for, applied to a single argument
   * @param combiner builds the replacement factor from that argument
   * @param sResult collects the candidate
   * @return <code>true</code> if the contracted form became the new result
   */
  private boolean contractArgumentFactor(IExpr expr, ISymbol head, Function<IExpr, IExpr> combiner,
      SimplifiedResult sResult) {
    if (!expr.isTimes()) {
      return false;
    }
    IAST times = (IAST) expr;
    int index1 = times.indexOf(x -> x.isAST(head, 2));
    if (index1 <= 0) {
      return false;
    }
    final IExpr argument = times.get(index1).first();
    int index2 = times.indexOf(x -> x.equals(argument));
    if (index2 <= 0) {
      return false;
    }
    final IASTAppendable rest = times.removePositionsAtCopy(new int[] {index1, index2}, 2);
    rest.append(combiner.apply(argument));
    final IExpr contracted = eval(rest.oneIdentity1());
    // consumed the whole product if nothing but the combined factor is left
    final boolean consumedWholeProduct = rest.argSize() == 1;
    return consumedWholeProduct ? sResult.checkLessEqual(contracted)
        : sResult.checkLessPlusTimesPower(contracted);
  }

  /** How a {@link Step} deals with exceptions thrown by its action. */
  private enum CatchPolicy {
    /**
     * Let everything propagate. Only the {@link LimitException} handler around the whole pipeline
     * applies.
     */
    NONE,
    /**
     * Swallow {@link ValidateException}. Every other {@link RuntimeException} — including
     * {@link LimitException} — propagates.
     */
    VALIDATE,
    /** Swallow every {@link RuntimeException} after re-throwing interrupts. */
    RUNTIME
  }

  /**
   * The mutable state threaded through {@link #TRANSFORMATION_STEPS}. Steps read {@link #expr},
   * offer candidates to {@link #result}, and a few of them publish a value that a later step reads
   * ({@link #expandAllCounter}, {@link #together}).
   */
  private static final class RewriteContext {
    final SimplifyUtil util;

    final SimplifiedResult result;

    /** The expression the steps rewrite. Only a {@code rebase} step reassigns it. */
    IExpr expr;

    /** Complexity of the {@code ExpandAll} result; gates the factorization step. */
    long expandAllCounter;

    /** The {@code Together} result; read by the numerator/denominator split. */
    IExpr together;

    RewriteContext(SimplifyUtil util, IExpr expr) {
      this.util = util;
      this.expr = expr;
      this.result = new SimplifiedResult(expr, util.fComplexityFunction);
      this.together = expr;
    }

    /** Adopt the current best as the expression the following steps rewrite. */
    void rebase() {
      if (result.result.isAST()) {
        expr = result.result;
      }
    }

    /** The working expression is an {@link IAST} by construction of {@link #rebase()}. */
    IAST ast() {
      return (IAST) expr;
    }
  }

  /** One named entry of the ordered rewrite pipeline. */
  private static final class Step {
    final String name;

    final CatchPolicy catchPolicy;

    final Consumer<RewriteContext> action;

    Step(String name, CatchPolicy catchPolicy, Consumer<RewriteContext> action) {
      this.name = name;
      this.catchPolicy = catchPolicy;
      this.action = action;
    }

    void run(RewriteContext ctx) {
      if (catchPolicy == CatchPolicy.NONE) {
        action.accept(ctx);
        return;
      }
      try {
        action.accept(ctx);
      } catch (RuntimeException rex) {
        if (catchPolicy == CatchPolicy.RUNTIME) {
          Errors.rethrowsInterruptException(rex);
          return;
        }
        if (!(rex instanceof ValidateException)) {
          throw rex;
        }
      }
    }

    @Override
    public String toString() {
      return name;
    }
  }

  /**
   * The ordered rewrite pipeline used by {@link #tryTransformations(IExpr)}: try {@code ExpandAll},
   * {@code Together}, {@code Apart}, {@code Factor}, … and keep whichever result the complexity
   * function rates lowest.
   */
  private static final Step[] TRANSFORMATION_STEPS = { //
      new Step("CollectLogAndTerms", CatchPolicy.NONE, ctx -> {
        if (ctx.expr.isTimes()) {
          IExpr temp = tryTimesLog(ctx.ast());
          if (temp.isPresent()) {
            ctx.result.checkLessEqual(temp);
          }
        } else if (ctx.expr.isPlus()) {
          IExpr temp = AlgebraUtil.factorTermsPlus(ctx.ast(), EvalEngine.get());
          if (temp.isPresent()) {
            final IExpr source = ctx.expr;
            final IExpr factored = temp;
            ctx.result.offerOnTie(temp, () -> prefersFactoredPlus(source, factored));
          }
        }
      }), //
      new Step("Rebase", CatchPolicy.NONE, RewriteContext::rebase), //
      new Step("ExpandAll", CatchPolicy.RUNTIME, ctx -> {
        IExpr temp = F.evalExpandAll(ctx.expr);
        ctx.expandAllCounter = ctx.util.fComplexityFunction.apply(temp);
        ctx.result.checkLessPlusTimesPower(temp);
      }), //
      new Step("Rebase", CatchPolicy.NONE, RewriteContext::rebase), //
      new Step("TrigExpand", CatchPolicy.VALIDATE, ctx -> {
        if (ctx.ast().hasTrigonometricFunction()) {
          ctx.result.checkLessPlusTimesPower(ctx.util.eval(F.TrigExpand(ctx.expr)));
        }
      }), //
      new Step("TrigToExp", CatchPolicy.VALIDATE, ctx -> {
        if (ctx.ast().hasTrigonometricFunction()) {
          IExpr temp = ctx.util.eval(F.TrigToExp(ctx.expr));
          if (!ctx.result.checkLessPlusTimesPower(temp) && ctx.util.fFullSimplify
          // Same bound the Factorization step below applies, and for the same reason: nested
          // trigonometry explodes when it is rewritten to exponentials — TrigToExp turns the
          // 28 leaves of 2*Cos(Pi/180*(60+3*Tan(Pi/180*(45-2*Sin(Pi/60))))) into 405 — and
          // factoring something that big costs far more than the chance of it collapsing is
          // worth. The cases this does pay for stay well inside the bound.
              && ctx.util.fComplexityFunction.apply(temp) < Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT) {
            ctx.result.checkLessPlusTimesPower(ctx.util.eval(F.Factor(temp)));
          }
        }
      }), //
      new Step("TrigReduce", CatchPolicy.VALIDATE, ctx -> {
        if (ctx.ast().hasTrigonometricFunction()) {
          ctx.result.checkLessPlusTimesPower(ctx.util.eval(F.TrigReduce(ctx.expr)));
        }
      }), //
      new Step("TrigFactor", CatchPolicy.VALIDATE, ctx -> {
        if (ctx.ast().hasTrigonometricFunction()) {
          // contracts a trigonometric sum into a product, which neither TrigReduce nor TrigExpand
          // reaches: 3*Cos(x)^2*Sin(x)^2+Sin(x)^4 becomes (2+Cos(2*x))*Sin(x)^2.
          // STRICT for the same reason as the fraction candidates above.
          ctx.result.checkLess(ctx.util.eval(F.TrigFactor(ctx.expr)));
        }
      }), //
      new Step("TogetherAndFractionParts", CatchPolicy.VALIDATE, ctx -> {
        ctx.together = ctx.expr;
        if (ctx.result.minCounter < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
          ctx.together = ctx.util.eval(F.Together(ctx.expr));
          ctx.result.checkLessPlusTimesPower(ctx.together);

        }
        if (ctx.util.fFullSimplify) {
          if (ctx.together.isTimes()) {
            IExpr[] fractionParts =
                AlgebraUtil.numeratorDenominator((IAST) ctx.together, true, EvalEngine.get());
            IExpr numerator = fractionParts[0];
            IExpr denominator = fractionParts[1];
            // common factors in numerator, denominator may be canceled here, so check if we have
            // a new minimal expression
            ctx.result.checkLessPlusTimesPower(F.Divide(numerator, denominator));

            if (!numerator.isOne() && //
                !denominator.isOne()) {
              ctx.util.tryPolynomialQuotientRemainder(numerator, denominator, ctx.result);
            }
          }
          ctx.result.checkLessPlusTimesPower(ctx.util.eval(F.ExpToTrig(ctx.expr)));
        }
      }), //
      // Runs in its own step with CatchPolicy.RUNTIME on purpose. Folded into
      // TogetherAndFractionParts (CatchPolicy.VALIDATE) any non-ValidateException thrown here
      // escapes tryTransformations(), whose only handler is for LimitException, and aborts the
      // whole Simplify — the caller then sees the untouched input. That is how this step first
      // turned ZTransform(n*a^n,n,z) back into z*(1/(a-z)+z/(-a+z)^2).
      new Step("ExpandFractionParts", CatchPolicy.RUNTIME, ctx -> {
        if (ctx.result.minCounter >= Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT
            || ctx.together == ctx.expr) {
          return;
        }
        // Only worth trying when the denominator is a product of sums, which is the shape whose
        // expansion actually collapses something: (a-I*x)*(a+I*x) becomes a^2+x^2. When Together
        // already produced a single sum (or a power of one) expanding is a no-op that only
        // perturbs which candidate the pipeline settles on.
        IExpr togetherDenominator = ctx.util.eval(F.Denominator(ctx.together));
        if (!togetherDenominator.isTimes()
            || !((IAST) togetherDenominator).exists(factor -> factor.isPlus())) {
          return;
        }
        // Together leaves the denominator factored, so its result can weigh as much as the input
        // and never win: 3*(1/(a-I*x)+1/(a+I*x)) becomes (6*a)/((a-I*x)*(a+I*x)). Expanding the
        // denominator turns that into (6*a)/(a^2+x^2).
        //
        // Both candidates are offered STRICT on purpose: an equally heavy fraction is no gain, and
        // simplifyStep() returns as soon as a pass comes back with an unchanged complexity, so
        // swapping in an equally heavy result would end the fixpoint loop a pass early.
        IExpr expandedDenominator = ctx.util.eval(F.ExpandDenominator(ctx.together));
        ctx.result.checkLess(expandedDenominator);

        // ... and a denominator that leads with a negative term hides a cheaper form:
        // (I*2*x)/(-1-x^2) is the same value as (-2*I*x)/(1+x^2) but weighs more.
        IExpr signNormalized = normalizeDenominatorSign(expandedDenominator, ctx.util);
        if (signNormalized.isPresent()) {
          ctx.result.checkLess(signNormalized);
        }
      }), //
      new Step("Factorization", CatchPolicy.VALIDATE, ctx -> {
        // TODO: Factor is not fast enough for large expressions!
        // Maybe restricting factoring to smaller expressions is necessary here
        if (ctx.util.fFullSimplify && ctx.expandAllCounter < 50) {
          ctx.result.checkLessPlusTimesPower(ctx.util.eval(F.Factor(ctx.expr)));
        }
        if (ctx.expandAllCounter < Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT) {
          ctx.result.checkLessPlusTimesPower(ctx.util.eval(F.FactorSquareFree(ctx.expr)));
        }
      }), //
      new Step("Apart", CatchPolicy.VALIDATE, ctx -> {
        if (!ctx.util.fNoApart //
            && ctx.result.minCounter < Config.MAX_SIMPLIFY_APART_LEAFCOUNT) {
          ctx.result.checkLessPlusTimesPower(ctx.util.eval(F.Apart(ctx.expr)));
        }
      }), //
      new Step("PlusFactorTermsAndLog", CatchPolicy.NONE, ctx -> {
        if (!ctx.expr.isPlus()) {
          return;
        }
        IExpr temp = AlgebraUtil.factorTermsPlus(ctx.ast(), EvalEngine.get());
        if (temp.isPresent()) {
          final IExpr source = ctx.expr;
          final IExpr factored = temp;
          ctx.result.offerOnTie(temp, () -> prefersFactoredPlus(source, factored));
        }

        Optional<IExpr[]> commonFactors = AlgebraUtil.findCommonFactors(ctx.ast(), true);
        if (commonFactors.isPresent()) {
          final IExpr combined =
              ctx.util.eval(F.Times(commonFactors.get()[0], commonFactors.get()[1]));
          ctx.result.offerOnTie(combined, () -> prefersFactoredPlus(ctx.expr, combined));
        }

        final IAST logSource = ctx.result.result.isPlus() //
            ? (IAST) ctx.result.result
            : ctx.ast();
        temp = ctx.util.tryPlusLog(logSource);
        if (temp.isPresent()) {
          ctx.result.checkLessEqual(temp);
        }
        // offered after tryPlusLog() so it competes against the single collapsed logarithm
        temp = ctx.util.tryPlusLogCommonFactor(logSource);
        if (temp.isPresent()) {
          ctx.result.checkLess(temp);
        }

      }), //
      // Own step with CatchPolicy.RUNTIME: these call Factor() and PolynomialGCD(), and in
      // PlusFactorTermsAndLog (CatchPolicy.NONE) anything they throw escapes tryTransformations,
      // whose only handler is for LimitException. That aborts the whole Simplify, and because Rubi
      // calls Simplify from inside Integrate the integral silently comes back unevaluated —
      // Integrate(x^2/(x^2+Sqrt(1-x^2)),x) did exactly that.
      new Step("GroupwisePlus", CatchPolicy.RUNTIME, ctx -> {
        if (!ctx.expr.isPlus()) {
          return;
        }
        // both rewrites are only worth taking when they are strictly cheaper
        IExpr temp = ctx.util.tryGroupwiseTogether(ctx.ast());
        if (temp.isPresent()) {
          ctx.result.checkLess(temp);
        }
        temp = ctx.util.tryFactorConstantPlusPower(ctx.ast());
        if (temp.isPresent()) {
          ctx.result.checkLess(temp);
        }
        ctx.util.tryCollectVariables(ctx.ast(), ctx.result);
      }) //
  };

  /**
   * Upper bound on {@link #fTransformationCache}. The cache only has to hold the subexpressions of
   * one expression, so this is a runaway guard, not a tuning knob.
   */
  private static final int MAX_TRANSFORMATION_CACHE_SIZE = 4096;

  private IExpr tryTransformations(IExpr expr) {
    if (!expr.isAST()) {
      return F.NIL;
    }
    IExpr cached = fTransformationCache.get(expr);
    if (cached != null) {
      return cached;
    }
    IExpr result;
    try {
      RewriteContext ctx = new RewriteContext(this, expr);
      for (int i = 0; i < TRANSFORMATION_STEPS.length; i++) {
        TRANSFORMATION_STEPS[i].run(ctx);
      }
      result = ctx.result.result;
    } catch (LimitException aele) {
      // Not cached: whether a limit is hit depends on how deep the evaluation already was when we
      // got here, so this says nothing about the expression itself.
      return F.NIL;
    }
    if (fTransformationCache.size() < MAX_TRANSFORMATION_CACHE_SIZE) {
      fTransformationCache.put(expr, result);
    }
    return result;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    SimplifiedResult sResult = new SimplifiedResult(ast, fComplexityFunction);

    if (ast.isPlus()) {
      // Has to run before the children are rewritten. In FullSimplify mode visitAST() below
      // FunctionExpand()s every summand, turning ArcSin(x) into a logarithmic form, after which the
      // complementary pair is no longer recognizable. Runs in both modes: WMA's plain
      // Simplify combines ArcSin(x)+ArcCos(x) as well.
      SimplifiedResult angleResult = new SimplifiedResult(ast, fComplexityFunction);
      complementaryAngleCombination(ast, angleResult);
      if (!angleResult.result.equals(ast)) {
        return angleResult.result;
      }
    }

    IExpr temp = visitAST(ast);
    if (temp.isPresent()) {
      temp = eval(temp);
      if (sResult.checkLessEqual(temp)) {
        if (temp.isAST()) {
          ast = (IASTMutable) temp;
          // result = temp;
        } else {
          return temp;
        }
      }
      // long count = fComplexityFunction.apply(temp);
      // if (count <= minCounter[0]) {
      // minCounter[0] = count;
      // if (temp.isAST()) {
      // ast = (IASTMutable) temp;
      // result = temp;
      // } else {
      // return temp;
      // }
      // }
    }
    if (ast.isPower()) {
      temp = visitPower(ast, sResult);
      if (temp.isPresent()) {
        return temp;
      }
    } else if (ast.isTimes()) {
      temp = visitTimes(ast, sResult);
      if (temp.isPresent()) {
        return temp;
      }
    } else if (ast.isPlus()) {
      temp = visitPlus(ast, sResult);
      if (temp.isPresent()) {
        return temp;
      }
    }

    temp = sResult.result;
    if (temp.isPresent()) {
      if (temp.isAST()) {
        ast = (IASTMutable) temp;
      } else {
        return temp;
      }
    }
    // temp = F.evalExpandAll(ast);
    // sResult.checkLess(temp);

    functionExpand(ast, sResult);
    return sResult.result;
  }

  private IExpr visitPlus(IASTMutable plusAST, SimplifiedResult sResult) {
    IExpr temp = tryArg1IsOnePlus(plusAST, sResult);
    if (temp.isPresent()) {
      return temp;
    }
    temp = sResult.result;
    if (temp.isPlus()) {
      plusAST = (IASTMutable) sResult.result;
    }

    IASTAppendable basicPlus = F.PlusAlloc(plusAST.size());
    IASTAppendable restPlus = F.PlusAlloc(plusAST.size());
    plusAST.forEach(x -> {
      if (x.accept(isBasicAST)) {
        basicPlus.append(x);
      } else {
        restPlus.append(x);
      }
    });
    if (basicPlus.size() > 1) {
      temp = tryTransformations(basicPlus.oneIdentity0());
      if (temp.isPresent()) {
        if (!restPlus.isAST0()) {
          temp = eval(F.Plus(temp, restPlus));
        }
        if (!temp.isPlus()) {
          return temp;
        }
        if (sResult.checkLessPlusTimesPower(temp)) {
          temp = sResult.result;
          if (temp.isPlus()) {
            plusAST = (IASTMutable) sResult.result;
          }
        }
      }
    }

    temp = tryTransformations(plusAST);
    if (temp.isPresent()) {
      if (sResult.checkLessEqual(temp)) {
        temp = sResult.result;
        if (temp.isPlus()) {
          plusAST = (IASTMutable) sResult.result;
        } else {
          return temp;
        }
      }
    }

    HashedOrderlessMatcher plusRuleMap = PLUS_ORDERLESS_MATCHER;
    if (plusRuleMap != null) {
      plusAST.setEvalFlags(plusAST.getEvalFlags() ^ IAST.IS_HASH_EVALED);
      temp = plusRuleMap.evaluateRepeated(plusAST, fEngine);
      if (temp.isPresent()) {
        temp = eval(temp);
        // a rule that consumes the whole sum wins a tie; a partial one must not — the matcher
        // splits coefficients, so Cosh+Sinh -> E^x would turn 10*Cosh(x)+4*Sinh(x) into the
        // equally heavy 4*E^x+6*Cosh(x)
        final boolean consumedWholeSum = !temp.isPlus();
        if (consumedWholeSum ? sResult.checkLessEqual(temp)
            : sResult.checkLessPlusTimesPower(temp)) {
          return temp;
        }
      }
    }

    if (fFullSimplify) {
      functionExpand(plusAST, sResult);
    }

    return sResult.result;
  }

  private IExpr visitPower(IAST powerAST, SimplifiedResult sResult) {
    if (fFullSimplify && powerAST.exponent().isComplex() && (powerAST.base().isExactNumber())) {
      IExpr powerSimplified = ArithmeticUtil.powerComplexComplex((IBigNumber) powerAST.base(),
          (IComplex) powerAST.exponent(), fEngine);
      if (powerSimplified.isPresent() && sResult.checkLessPlusTimesPower(powerSimplified)) {
        return powerSimplified;
      }
    }
    if (powerAST.base().isPlus2()) {

      if (fFullSimplify && powerAST.exponent().isFraction()) {
        IFraction expFrac = (IFraction) powerAST.exponent();
        if (expFrac.isNegative()) {
          IAST plus1 = (IAST) powerAST.base();
          IAST plus2 = plus1.setAtCopy(2, plus1.arg2().negate());
          IExpr product = eval(F.Expand(F.Times(plus1, plus2)));
          if (product.isRational() && !product.isZero()) {
            // (plus1)^(-p/q) = (plus2 / product)^(p/q)
            // = (plus2)^(p/q) * product^(-p/q)
            IFraction posExp = expFrac.negate();
            // Only handle 1/3 for now
            if (posExp.equals(F.C1D3)) {
              // plus2 = arg1 + arg2, where we expect arg1 = rational a, arg2 = b*Sqrt(c)
              IExpr arg1 = plus2.arg1(); // rational a
              IExpr arg2 = plus2.arg2(); // b*Sqrt(c) or -b*Sqrt(c)

              // Extract b and c from arg2
              IExpr b, c;
              if (arg2.isSqrt()) { // Sqrt(c)
                b = F.C1;
                c = arg2.first();
              } else if (arg2.isTimes() && arg2.last().isSqrt()) { // b*Sqrt(c)
                b = ((IAST) arg2).removeAtCopy(arg2.argSize()).oneIdentity1(); // all factors except
                                                                               // last
                c = arg2.last().first();
              } else {
                b = F.NIL;
                c = F.NIL;
              }

              if (b.isPresent() && c.isPresent() && arg1.isRational()) {
                IExpr denested = denestCubeRootPlusSqrt(arg1, b, c, fEngine);
                if (denested.isPresent()) {
                  // result = denested * product^(-1/3)
                  // use real cube root here
                  IExpr powerSimplified = eval(F.Times(denested, F.CubeRoot(product)));
                  if (powerSimplified.isPresent()
                      && sResult.checkLessPlusTimesPower(powerSimplified)) {
                    return powerSimplified;
                  }
                }
              }
            }
          }
        }
      }

      if (powerAST.isPowerReciprocal()) {
        // example 1/(5+Sqrt(17)) => 1/8*(5-Sqrt(17))
        IAST plus1 = (IAST) powerAST.base();
        IAST plus2 = plus1.setAtCopy(2, plus1.arg2().negate());
        // example (5+Sqrt(17)) * (5-Sqrt(17))
        IExpr expr = eval(F.Expand(F.Times(plus1, plus2)));
        if (expr.isNumber() && !expr.isZero()) {
          IExpr powerSimplified = S.Times.of(expr.inverse(), plus2);
          if (sResult.checkLessPlusTimesPower(powerSimplified)) {
            return powerSimplified;
          }
        }
      } else {
        int n = powerAST.exponent().toIntDefault();
        if (F.isPresent(n) && Math.abs(n) < Config.MAX_SIMPLIFY_EXPAND_PLUS_EXPONENT) {
          if (n < 0) {
            powerAST = F.Power(powerAST.base(), F.ZZ(-n));
          }
          IExpr powerSimplified = tryExpandTransformation(powerAST, powerAST);
          if (powerSimplified.isPresent()) {
            if (n < 0) {
              return F.Power(powerSimplified, -1);
            }
            return powerSimplified;
          }
        }
      }
    }
    if (powerAST.base().isE() && powerAST.exponent().isPlus()) {
      // E^(a*Log(f)+b+Log(g)) ==> E^(b) * f^a * g
      IAST plusAST = (IAST) powerAST.exponent();
      IASTAppendable plusResult = F.NIL;
      IASTAppendable logFactor = F.NIL;
      for (int i = 1; i < plusAST.size(); i++) {
        IExpr plusArg = plusAST.get(i);
        if (plusArg.isTimes()) {
          IAST timesAST = (IAST) plusArg;
          int indx1 = timesAST.indexOf(x -> x.isLog());
          if (indx1 > 0) {
            int indx2 = timesAST.indexOf(x -> x.isLog(), indx1 + 1);
            if (indx2 < 0) {
              if (plusResult.isNIL()) {
                plusResult = plusAST.copyUntil(plusAST.argSize(), i);
                logFactor = F.TimesAlloc(10);
              }
              logFactor.append(F.Power(timesAST.get(indx1).first(), timesAST.removeAtCopy(indx1)));
              continue;
            }
          }
        } else if (plusArg.isLog()) {
          if (plusResult.isNIL()) {
            plusResult = plusAST.copyUntil(plusAST.argSize(), i);
            logFactor = F.TimesAlloc(10);
          }
          logFactor.append(plusArg.first());
          continue;
        }
        if (plusResult.isPresent()) {
          plusResult.append(plusArg);
        }
      }

      if (plusResult.isPresent()) {
        logFactor.append(F.Power(S.E, plusResult));
        IExpr temp = eval(logFactor);
        sResult.checkLessEqual(temp);
      }
    }
    return F.NIL;
  }

  // Solve: u^3 + 15*u*v^2 = -2 AND 3*u^2*v + 5*v^3 = 1
  // Try denominators 1,2,3,4... for u = p/d, v = q/d
  private static IExpr denestCubeRootPlusSqrt(IExpr a, IExpr b, IExpr c, EvalEngine engine) {
    // (a + b*Sqrt(c))^(1/3) = u + v*Sqrt(c)?
    // u^3 + 3*u*v^2*c = a
    // 3*u^2*v + v^3*c = b
    // Try rational u,v with small denominators
    for (int den = 1; den <= 12; den++) {
      for (int pn = -den * 4; pn <= den * 4; pn++) {
        for (int qn = -den * 4; qn <= den * 4; qn++) {
          IRational u = F.QQ(pn, den).normalize();
          IRational v = F.QQ(qn, den).normalize();
          // Check: u^3 + 3*u*v^2*c == a and 3*u^2*v + v^3*c == b
          IExpr rationalPart =
              engine.evaluate(F.Plus(F.Power(u, 3), F.Times(F.C3, u, F.Power(v, 2), c)));
          if (rationalPart.equals(a)) {
            IExpr irrationalPart =
                engine.evaluate(F.Plus(F.Times(F.C3, F.Power(u, 2), v), F.Times(F.Power(v, 3), c)));
            if (irrationalPart.equals(b)) {
              return F.Times(F.QQ(1, den), F.Plus(pn, F.Times(qn, F.Sqrt(c))));
            }
          }
        }
      }
    }
    return F.NIL;
  }


  private IExpr visitTimes(IASTMutable timesAST, SimplifiedResult sResult) {
    final IExpr denominator = eval(F.Denominator(timesAST));
    if (!denominator.isNumber()) {
      final IExpr numerator = eval(F.Numerator(timesAST));
      if (numerator.isAST(S.RealAbs, 2) && numerator.first().equals(denominator)
          && denominator.isPlus() && denominator.first().isRational()) {
        IRational n = (IRational) denominator.first();
        IExpr rest = denominator.rest().oneIdentity0();
        // Piecewise({{-1,rest<(-n)}},1)
        return F.Piecewise(F.list(F.list(F.CN1, F.Less(rest, n.negate()))), F.C1);
      } else if (denominator.isAST(S.RealAbs, 2) && denominator.first().equals(numerator)
          && numerator.isPlus() && numerator.first().isRational()) {
        IRational n = (IRational) numerator.first();
        IExpr rest = numerator.rest().oneIdentity0();
        // Piecewise({{-1,rest<(-n)}},1)
        return F.Piecewise(F.list(F.list(F.CN1, F.Less(rest, n.negate()))), F.C1);
      }
      if (fFullSimplify || numerator.isTimes() || denominator.isTimes()) {
        IExpr numer = F.evalExpandAll(numerator);
        IExpr denom = F.evalExpandAll(denominator);
        if (S.PossibleZeroQ.ofQ(F.Subtract(numer, denom))) {
          return F.C1;
        }
      }
    }

    IExpr temp = reduceNumberFactor(timesAST);
    if (temp.isPresent()) {
      sResult.result = temp;
      sResult.minCounter = fComplexityFunction.apply(temp);
    }

    temp = reduceConjugateFactors(timesAST, sResult);
    if (temp.isPresent()) {
      return temp;
    }

    if (timesAST.isTimes2() && timesAST.arg1().isPower() && timesAST.arg2().isPower()) {
      IAST sqrt1 = (IAST) timesAST.arg1();
      IAST sqrt2 = (IAST) timesAST.arg2();
      IExpr base1 = sqrt1.arg1();
      IExpr base2 = sqrt2.arg1();
      IExpr exponent1 = sqrt1.arg2();
      IExpr exponent2 = sqrt2.arg2();
      if (exponent1.equals(exponent2)) {
        temp = base1.plus(base2);
        if (temp.isNonNegativeResult()) {
          // https://functions.wolfram.com/ElementaryFunctions/Power/16/08/01/0004/
          // a^(c)*b^(c) => (a*b) ^c
          long leafCountTimes = base1.leafCountSimplify() + base2.leafCountSimplify() + 4;
          if (leafCountTimes < Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT) {
            IExpr expanded = F.evalExpand(F.Times(base1, base2));
            if (expanded.leafCountSimplify() <= leafCountTimes) {
              return F.Power(expanded, exponent1);
            }
          }
        }
      }
    }
    // IExpr evalExpand = tryExpandTransformation(timesAST, timesAST);
    // // IExpr evalExpand = F.evalExpand(powerAST);
    // // IExpr powerSimplified = evalExpand.accept(this);
    // if (evalExpand.isPresent()) {
    // return evalExpand;
    // }

    temp = tryTransformations(sResult.result.orElse(timesAST));
    if (temp.isPresent()) {
      sResult.result = temp;
    }
    temp = sResult.result.orElse(timesAST);
    sResult.minCounter = fComplexityFunction.apply(temp);
    functionExpand(temp, sResult); // minCounter[0], result);
    return F.NIL;
  }
}
