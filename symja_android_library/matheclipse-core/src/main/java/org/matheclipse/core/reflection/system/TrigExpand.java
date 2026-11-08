package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.evalExpandAll;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorPlusTimesPowerReplaceAll;

/**
 *
 *
 * <pre>
 * TrigExpand(expr)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * expands out trigonometric expressions in <code>expr</code>.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; TrigExpand(Sin(x+y))
 * Cos(x)*Sin(y)+Cos(y)*Sin(x)
 * </pre>
 */
public class TrigExpand extends AbstractEvaluator {

  private static final class TrigExpandFunction implements Function<IExpr, IExpr> {
    /**
     * <code>if (coshResult==true) then TrigExand(Cosh(a+b+c+...))  else TrigExand(Sinh(a+b+c+...))</code>
     * 
     * @param plusAST
     * @param coshResult
     * @return
     */
    private static IExpr coshSinhPlus(IAST plusAST, boolean coshResult) {
      IExpr lhs = plusAST.arg1();
      IExpr rhs = plusAST.arg2();
      // Cosh(lhs)*Cosh(rhs) + Sinh(lhs)*Sinh(rhs)
      IExpr coshPlus = F.Plus(Times(F.Cosh(lhs), F.Cosh(rhs)), Times(F.Sinh(lhs), F.Sinh(rhs)));
      // Sinh(lhs)*Cosh(rhs) + Cosh(lhs)*Sinh(rhs)
      IExpr sinhPlus = F.Plus(Times(F.Sinh(lhs), F.Cosh(rhs)), Times(F.Cosh(lhs), F.Sinh(rhs)));
      for (int i = 3; i < plusAST.size(); i++) {
        lhs = plusAST.get(i);
        IExpr coshTemp = F.Plus(distributeTimes(Times(F.Cosh(lhs), coshPlus)),
            distributeTimes(Times(F.Sinh(lhs), sinhPlus)));
        IExpr sinhTemp = F.Plus(distributeTimes(Times(F.Sinh(lhs), coshPlus)),
            distributeTimes(Times(F.Cosh(lhs), sinhPlus)));
        coshPlus = coshTemp;
        sinhPlus = sinhTemp;
      }
      return coshResult ? coshPlus : sinhPlus;
    }

    /**
     * <code>if (cosResult==true) then TrigExand(Cos(a+b+c+...))  else TrigExand(Sin(a+b+c+...))</code>
     * 
     * @param plusAST
     * @param cosResult
     * @return
     */
    private static IExpr cosSinPlus(IAST plusAST, boolean cosResult) {
      IExpr lhs = plusAST.arg1();
      IExpr rhs = plusAST.arg2();
      IExpr cosPlus = F.Plus(Times(Cos(lhs), Cos(rhs)), Times(CN1, Sin(lhs), Sin(rhs)));
      IExpr sinPlus = F.Plus(Times(Sin(lhs), Cos(rhs)), Times(Cos(lhs), Sin(rhs)));
      for (int i = 3; i < plusAST.size(); i++) {
        lhs = plusAST.get(i);
        IExpr cosTemp = F.Plus(distributeTimes(Times(F.Cos(lhs), cosPlus)),
            distributeTimes(Times(CN1, Sin(lhs), sinPlus)));
        IExpr sinTemp = F.Plus(distributeTimes(Times(F.Sin(lhs), cosPlus)),
            distributeTimes(Times(F.Cos(lhs), sinPlus)));
        cosPlus = cosTemp;
        sinPlus = sinTemp;
      }
      return cosResult ? cosPlus : sinPlus;
    }

    /**
     * <code>Cos(n*x)</code>
     *
     * @param n positive integer
     * @param x the rest of the {@link S#Times} expression
     * @return summation formula derive from de Moivre's theorem
     */
    private static IExpr cosTimes(IInteger n, IExpr x) {
      int ni = n.toIntDefault();
      if (ni > Integer.MIN_VALUE) {
        // Sum(((-1)^m*Binomial(n,2*m)*Sin(x)^(2*m))/Cos(x)^(2*m-n),{m,0,Floor(n/2)})
        return F.sum(m -> F.Times(F.Power(-1, m), F.Binomial(n, F.Times(F.C2, m)),
            F.Power(F.Cos(x), F.Plus(F.Times(F.CN2, m), n)), F.Power(F.Sin(x), F.Times(F.C2, m))),
            0, ni / 2);
      }
      return F.NIL;
    }

    /**
     * <code>Cosh(n*x)</code>
     *
     * @param n positive integer
     * @param x the rest of the {@link S#Times} expression
     * @return summation formula derive from de Moivre's theorem
     */
    private static IExpr coshTimes(IInteger n, IExpr x) {
      int ni = n.toIntDefault();
      if (ni > Integer.MIN_VALUE) {
        // Sum((Binomial(n,2*m)*Sinh(x)^(2*m))/Cosh(x)^(2*m-n),{m,0,Floor(n/2)})
        return F.sum(m -> F.Times(F.Binomial(n, F.Times(F.C2, m)),
            F.Power(F.Cosh(x), F.Plus(F.Times(F.CN2, m), n)), F.Power(F.Sinh(x), F.Times(F.C2, m))),
            0, ni / 2);

      }
      return F.NIL;
    }

    private static IExpr distributeTimes(IExpr expr) {
      return AlgebraUtil.distributeTimes(expr);
    }

    /**
     * <code>Sin(n*x)</code>
     *
     * @param n positive integer
     * @param x the rest of the {@link S#Times} expression
     * @return summation formula derive from de Moivre's theorem
     */
    private static IExpr sinTimes(IInteger n, IExpr x) {
      int ni = n.toIntDefault();
      if (ni > Integer.MIN_VALUE) {
        // Sum(((-1)^m*Binomial(n,2*m+1)*Sin(x)^(2*m+1))/Cos(x)^(2*m+1-n),{m,0,Floor(1/2*(-1+n))})
        return F.sum(m -> F.Times(F.Power(-1, m), F.Binomial(n, F.Plus(F.Times(F.C2, m), F.C1)),
            F.Power(F.Cos(x), F.Plus(F.Negate(F.Plus(F.Times(F.C2, m), F.C1)), n)),
            F.Power(F.Sin(x), F.Plus(F.Times(F.C2, m), F.C1))), 0, (ni - 1) / 2);
      }
      return F.NIL;
    }

    /**
     * <code>Sinh(n*x)</code>
     *
     * @param n positive integer
     * @param x the rest of the {@link S#Times} expression
     * @return summation formula derive from de Moivre's theorem
     */
    private static IExpr sinhTimes(IInteger n, IExpr x) {
      int ni = n.toIntDefault();
      if (ni > Integer.MIN_VALUE) {
        // Sum((Binomial(n,2*m+1)*Sinh(x)^(2*m+1))/Cosh(x)^(2*m+1-n),{m,0,Floor(1/2*(-1+n))})
        return F.sum(m -> F.Times(F.Binomial(n, F.Plus(F.Times(F.C2, m), F.C1)),
            F.Power(F.Cosh(x), F.Plus(F.Negate(F.Plus(F.Times(F.C2, m), F.C1)), n)),
            F.Power(F.Sinh(x), F.Plus(F.Times(F.C2, m), F.C1))), 0, (ni - 1) / 2);
      }
      return F.NIL;
    }

    @Override
    public IExpr apply(IExpr ast) {
      if (ast.isAST1()) {
        IExpr first = ast.first();
        if (first.isPlus()) {
          IExpr temp = expandPlus((IAST) ast, (IAST) first);
          if (temp.isPresent()) {
            if (temp.isPlus()) {
              temp = ((IAST) temp).map(S.Plus, x -> distributeTimes(x));
            }
            return temp;
          }
        } else if (first.isTimes()) {
          return expandTimes((IAST) ast, (IAST) first);
        }
      }
      return F.NIL;
    }

    /**
     * Expand <code>f(a+b+c+...)</code> and create a trig function.
     *
     * @param ast
     * @param plusAST
     * @return
     */
    private IExpr expandPlus(IAST ast, IAST plusAST) {
      if (ast.isSin()) {
        return cosSinPlus(plusAST, false);
      } else if (ast.isCos()) {
        return cosSinPlus(plusAST, true);
      } else if (ast.isSinh()) {
        return coshSinhPlus(plusAST, false);
      } else if (ast.isCosh()) {
        return coshSinhPlus(plusAST, true);
      }
      return F.NIL;
    }

    /**
     * Expand <code>f(n*theta)</code> and create a trig function.
     *
     * @param ast
     * @param timesAST
     * @return
     */
    private IExpr expandTimes(IAST ast, IAST timesAST) {
      if (timesAST.arg1().isInteger()) {
        IInteger n = (IInteger) timesAST.arg1();
        final IExpr theta;
        if (n.isNegative()) {
          n = n.negate();
          theta = timesAST.setAtCopy(1, F.CN1);
        } else {
          theta = timesAST.rest().oneIdentity1();
        }
        try {
          if (ast.isSin()) {
            return sinTimes(n, theta);
          } else if (ast.isCos()) {
            return cosTimes(n, theta);
          } else if (ast.isSinh()) {
            return sinhTimes(n, theta);
          } else if (ast.isCosh()) {
            return coshTimes(n, theta);
          }
        } catch (ArithmeticException ae) {

        }
      }
      return F.NIL;
    }

  }

  public static final Function<IExpr, IExpr> TRIG_EXPAND_FUNCTION = new TrigExpandFunction();

  private static final VisitorPlusTimesPowerReplaceAll TRIG_EXPAND_VISITOR =
      new VisitorPlusTimesPowerReplaceAll(TRIG_EXPAND_FUNCTION);

  /**
   * Rewrite to simpler circular or hyperbolic functions for {@link S#Cot}, {@link S#Coth},
   * {@link S#Csc}, {@link S#Csch}, {@link S#Sec}, {@link S#Sech}, {@link S#Tan}, {@link S#Tanh}.
   * 
   * @param expr
   * @return
   */
  public static IExpr rewriteCircularHyperbolic(IExpr expr) {
    if (expr.isAST1()) {
      int headID = expr.headID();
      if (headID > -1) {
        IExpr arg1 = expr.first();
        switch (headID) {
          case ID.Cot:
            return F.Divide(F.Cos(arg1), F.Sin(arg1));
          case ID.Tan:
            return F.Divide(F.Sin(arg1), F.Cos(arg1));
          case ID.Csc:
            return F.Power(F.Sin(arg1), F.CN1);
          case ID.Sec:
            return F.Power(F.Cos(arg1), F.CN1);
          case ID.Coth:
            return F.Divide(F.Cosh(arg1), F.Sinh(arg1));
          case ID.Tanh:
            return F.Divide(F.Sinh(arg1), F.Cosh(arg1));
          case ID.Csch:
            return F.Power(F.Sinh(arg1), F.CN1);
          case ID.Sech:
            return F.Power(F.Cosh(arg1), F.CN1);
        }
      }
    }
    return F.NIL;
  }

  public static IAST rewriteCircularHyperbolicOrderless(IAST orderlessAST,
      IBuiltInSymbol orderlessHead) {
    IASTMutable mutableOrderless = F.NIL;
    for (int i = 1; i < orderlessAST.size(); i++) {
      IExpr expr = orderlessAST.get(i);
      IExpr temp = rewriteCircularHyperbolic(expr);
      if (temp.isPresent()) {
        if (mutableOrderless.isNIL()) {
          mutableOrderless = orderlessAST.copy();
        }
        mutableOrderless.set(i, temp);
      }
    }
    if (mutableOrderless.isNIL()) {
      return orderlessAST;
    }
    IAST flattened = EvalAttributes.flatten(orderlessHead, mutableOrderless);
    if (!flattened.isTimes()) {
      flattened = mutableOrderless;
    }
    return flattened;
  }


  /**
   * Apply trig angle-addition identities to an already algebraically expanded expression.
   *
   * <p>
   * The caller is responsible for performing algebraic expansion (e.g. via {@link F#evalExpandAll}
   * or {@link AlgebraUtil#expand}) <em>before</em> calling this method. The internal fixpoint loop
   * still calls {@code evalExpandAll} on each intermediate result to flatten products that arise
   * from the angle-addition rewrites themselves.
   *
   * @param expr an algebraically pre-expanded expression
   * @param engine the evaluation engine
   * @return the trig-expanded result, or {@code expr} unchanged when no identity applies
   */
  /**
   * Expand <code>expr</code> algebraically, then rewrite trigonometric functions of sums and of
   * multiple angles, without first reducing powers and products as {@link S#TrigExpand} does.
   * Simplification uses this as one of its candidate forms.
   */
  public static IExpr expandAngles(IExpr expr, EvalEngine engine) {
    return trigExpand(evalExpandAll(expr, engine), engine);
  }

  public static IExpr trigExpand(IExpr expr, EvalEngine engine) {
    IExpr result = F.subst(expr, x -> rewriteCircularHyperbolic(x));
    IExpr temp = result.accept(TRIG_EXPAND_VISITOR);
    if (temp.isNIL()) {
      return expr;
    }
    do {
      result = evalExpandAll(temp, engine);
      result = F.subst(result, x -> rewriteCircularHyperbolic(x));
      temp = result.accept(TRIG_EXPAND_VISITOR);
    } while (temp.isPresent());
    return result;
  }


  public TrigExpand() {}

  /** The largest power of a quotient function whose reduced denominator is recognized. */
  private static final int MAX_DENOMINATOR_POWER = 64;

  /**
   * Expands the argument of sine and cosine functions.
   *
   * <p>
   * An expression with several trigonometric functions, with a power of a trigonometric function
   * of a sum or multiple angle, or with a variable of an angle also outside of the trigonometric
   * functions is linearized by {@link TrigReduce} first. Then <code>Cos(x+y)^2</code> becomes
   * <code>1/2+Cos(x)^2*Cos(y)^2/2-...</code>, while <code>Sin(x)^3</code> is kept. A denominator
   * which is a constant multiple of a reduced power <code>Cos(a)^k</code> or <code>Sin(a)^k</code>
   * is written as that power: <code>1/(1+Cos(2*x))</code> becomes <code>Sec(x)^2/2</code>.
   *
   * <p>
   * <a href="http://en.wikipedia.org/wiki/List_of_trigonometric_identities">List of trigonometric
   * identities</a>
   */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr temp = CompareUtil.threadListLogicEquationOperators(ast.arg1(), ast, 1);
    if (temp.isPresent()) {
      return temp;
    }
    IExpr arg1 = ast.arg1();
    IExpr prepared = arg1;
    if (trigTerms(arg1) > 1 || anglesShareVariables(arg1)) {
      prepared = reduceFirst(arg1, engine);
    }
    List<IExpr> roots = new ArrayList<IExpr>();
    Set<IExpr> keptRoots = new HashSet<IExpr>();
    collectQuotientPowers(arg1, roots, keptRoots);
    Map<IExpr, IExpr> dummies = new HashMap<IExpr, IExpr>();
    prepared = replaceReducedDenominators(prepared, roots, keptRoots, dummies, engine);

    // TrigExpand(expr) is defined as ExpandAll first, then apply trig identities.
    IExpr expanded = evalExpandAll(prepared, engine);
    IExpr result = trigExpand(expanded, engine);
    if (!dummies.isEmpty()) {
      result = engine.evaluate(F.subst(result, dummies));
    }
    // if trigExpand made no change (returned expr unchanged), return original arg1
    return result == expanded && prepared == arg1 ? arg1 : result;
  }

  private static boolean isTrigFunction(IExpr expr) {
    return expr.isAST1() && expr.isFunctionID(ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc,
        ID.Sinh, ID.Cosh, ID.Tanh, ID.Coth, ID.Sech, ID.Csch);
  }

  /**
   * Count the trigonometric functions in <code>expr</code>. A power of a trigonometric function of
   * a sum or of an integer multiple counts twice.
   */
  private static int trigTerms(IExpr expr) {
    if (expr.isPower() && expr.exponent().isInteger() && expr.exponent().toIntDefault() > 1
        && isTrigFunction(expr.base())) {
      IExpr angle = expr.base().first();
      if (angle.isPlus() || (angle.isTimes() && angle.first().isInteger()
          && ((IInteger) angle.first()).abs().isGT(F.C1))) {
        return 2;
      }
    }
    if (isTrigFunction(expr)) {
      return 1;
    }
    if (expr.isAST()) {
      int count = 0;
      for (IExpr arg : (IAST) expr) {
        count += trigTerms(arg);
      }
      return count;
    }
    return 0;
  }

  /**
   * Test if a variable occurs in an argument of a trigonometric function and also outside of the
   * trigonometric functions.
   */
  private static boolean anglesShareVariables(IExpr expr) {
    Set<IExpr> angles = new HashSet<IExpr>();
    Set<IExpr> others = new HashSet<IExpr>();
    collectVariables(expr, false, angles, others);
    for (IExpr variable : angles) {
      if (others.contains(variable)) {
        return true;
      }
    }
    return false;
  }

  private static void collectVariables(IExpr expr, boolean inAngle, Set<IExpr> angles,
      Set<IExpr> others) {
    if (isTrigFunction(expr)) {
      collectVariables(expr.first(), true, angles, others);
    } else if (expr.isAST()) {
      for (IExpr arg : (IAST) expr) {
        collectVariables(arg, inAngle, angles, others);
      }
    } else if (expr.isVariable()) {
      if (inAngle) {
        angles.add(expr);
      } else {
        others.add(expr);
      }
    }
  }

  /**
   * Linearize <code>expr</code> with {@link TrigReduce}. If no angle is a sum or a multiple, the
   * summands with a quotient function like {@link ID#Tan} are kept and only the others are reduced
   * together: <code>Sin(x)*Tan(x)</code> stays, <code>Sin(x)^2+Cos(x)^2+Tan(x)</code> becomes
   * <code>1+Tan(x)</code>.
   */
  private static IExpr reduceFirst(IExpr expr, EvalEngine engine) {
    if (!onlySimpleAngles(expr) || !hasQuotientFunction(expr)) {
      return TrigReduce.trigReduceNormalized(expr, engine);
    }
    if (!expr.isPlus()) {
      return expr;
    }
    IASTAppendable kept = F.PlusAlloc(expr.argSize());
    IASTAppendable reducible = F.PlusAlloc(expr.argSize());
    for (IExpr term : (IAST) expr) {
      (hasQuotientFunction(term) ? kept : reducible).append(term);
    }
    if (reducible.argSize() == 0 || trigTerms(reducible) == 0) {
      return expr;
    }
    kept.append(TrigReduce.trigReduceNormalized(reducible.oneIdentity0(), engine));
    return engine.evaluate(kept);
  }

  /**
   * Test if no argument of a trigonometric function is a sum or an integer multiple, so that there
   * is no angle to expand.
   */
  private static boolean onlySimpleAngles(IExpr expr) {
    if (isTrigFunction(expr)) {
      IExpr angle = expr.first();
      return !angle.isPlus() && !(angle.isTimes() && angle.first().isInteger())
          && onlySimpleAngles(angle);
    }
    if (expr.isAST()) {
      for (IExpr arg : (IAST) expr) {
        if (!onlySimpleAngles(arg)) {
          return false;
        }
      }
    }
    return true;
  }

  /** Test if <code>expr</code> contains a trigonometric function other than a sine or cosine. */
  private static boolean hasQuotientFunction(IExpr expr) {
    return expr.has(x -> isTrigFunction(x)
        && !x.isFunctionID(ID.Sin, ID.Cos, ID.Sinh, ID.Cosh), true);
  }

  /**
   * Collect <code>Cos(a)^k</code> for <code>Tan(a)^k</code> (and the other quotient and reciprocal
   * functions) of the input. Their reduced forms may occur as denominators after
   * {@link TrigReduce}. The roots of quotient functions are added to <code>keptRoots</code>: their
   * expanded power is kept, while the power of a root of a reciprocal function like
   * {@link ID#Sec} is expanded.
   */
  private static void collectQuotientPowers(IExpr expr, List<IExpr> roots, Set<IExpr> keptRoots) {
    if (expr.isPower() && isTrigFunction(expr.base())) {
      int n = expr.exponent().toIntDefault();
      if (n >= 2 && n <= MAX_DENOMINATOR_POWER) {
        IExpr arg = expr.base().first();
        IExpr root = F.NIL;
        switch (expr.base().headID()) {
          case ID.Tan:
          case ID.Sec:
            root = F.Cos(arg);
            break;
          case ID.Cot:
          case ID.Csc:
            root = F.Sin(arg);
            break;
          case ID.Tanh:
          case ID.Sech:
            root = F.Cosh(arg);
            break;
          case ID.Coth:
          case ID.Csch:
            root = F.Sinh(arg);
            break;
          default:
            break;
        }
        if (root.isPresent()) {
          IExpr rootPower = F.Power(root, n);
          roots.add(rootPower);
          if (expr.base().isFunctionID(ID.Tan, ID.Cot, ID.Tanh, ID.Coth)) {
            keptRoots.add(rootPower);
          }
        }
      }
    }
    if (expr.isAST()) {
      for (IExpr arg : (IAST) expr) {
        collectQuotientPowers(arg, roots, keptRoots);
      }
    }
  }

  /**
   * Replace a denominator <code>c*Numerator(TrigReduce(f(a)^k))</code> by the power
   * <code>f(a)^k</code> of the expanded <code>f(a)</code>. An expanded sum is hidden behind a dummy
   * symbol, so that the following expansion keeps its power.
   */
  private static IExpr replaceReducedDenominators(IExpr expr, List<IExpr> roots,
      Set<IExpr> keptRoots, Map<IExpr, IExpr> dummies, EvalEngine engine) {
    return expr.replaceAll(x -> {
      if (x.isPower() && x.base().isPlus() && x.exponent().isInteger()) {
        int n = x.exponent().toIntDefault();
        if (n < 0 && n > -MAX_DENOMINATOR_POWER) {
          IExpr base = TrigReduce.distributeArguments(x.base(), engine);
          List<IExpr> candidates = new ArrayList<IExpr>(roots);
          halfAngleCandidates(base, candidates, engine);
          for (IExpr rootPower : candidates) {
            IExpr scale = reducedPowerScale(base, rootPower, engine);
            if (scale.isPresent()) {
              int k = rootPower.exponent().toIntDefault();
              IExpr root = trigExpand(evalExpandAll(rootPower.base(), engine), engine);
              if (root.isPlus() && (keptRoots.contains(rootPower) || !roots.contains(rootPower))) {
                ISymbol dummy = F.Dummy("$trigexpand" + dummies.size());
                dummies.put(dummy, root);
                root = dummy;
              }
              return F.Times(F.Power(scale, n), F.Power(root, F.ZZ((long) k * n)));
            }
          }
        }
      }
      return F.NIL;
    }).orElse(expr);
  }

  /**
   * For <code>c1+c2*Cos(2*a)</code> add the candidates <code>Cos(a)^2, Sin(a)^2</code>, for
   * <code>c1+c2*Cosh(2*a)</code> add <code>Cosh(a)^2, Sinh(a)^2</code>.
   */
  private static void halfAngleCandidates(IExpr sum, List<IExpr> candidates,
      EvalEngine engine) {
    if (sum.argSize() != 2 || !sum.first().isRational()) {
      return;
    }
    IExpr term = sum.second();
    IExpr function = term.isTimes() && term.argSize() == 2 && term.first().isRational()
        ? term.second()
        : term;
    if (function.isCos() || function.isCosh()) {
      IExpr half = engine.evaluate(F.Expand(F.Times(F.C1D2, function.first())));
      IAST terms = half.isPlus() ? (IAST) half : F.Plus(half);
      for (IExpr t : terms) {
        IExpr coefficient = t.isTimes() ? t.first() : t;
        if (coefficient.isFraction()) {
          return;
        }
      }
      if (function.isCos()) {
        candidates.add(F.Power(F.Cos(half), F.C2));
        candidates.add(F.Power(F.Sin(half), F.C2));
      } else {
        candidates.add(F.Power(F.Cosh(half), F.C2));
        candidates.add(F.Power(F.Sinh(half), F.C2));
      }
    }
  }

  /**
   * If <code>sum == scale*f(a)^k</code> with the reduced power of <code>rootPower = f(a)^k</code>,
   * return the rational <code>scale</code>.
   */
  private static IExpr reducedPowerScale(IExpr sum, IExpr rootPower, EvalEngine engine) {
    IExpr reduced = TrigReduce.trigReduceNormalized(rootPower, engine);
    IExpr numerator = engine.evaluate(F.Numerator(reduced));
    if (!numerator.isPlus()) {
      return F.NIL;
    }
    IExpr denominator = engine.evaluate(F.Denominator(reduced));
    IExpr factor = engine.evaluate(F.Together(F.Divide(sum, numerator)));
    if (factor.isRational() && !factor.isZero() && denominator.isRational()) {
      return factor.times(denominator);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
