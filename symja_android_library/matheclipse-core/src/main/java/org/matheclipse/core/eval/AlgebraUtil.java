package org.matheclipse.core.eval;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Predicate;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.PolynomialHomogenization;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import com.google.common.math.LongMath;
import edu.jas.arith.BigRational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.poly.TermOrderByName;
import edu.jas.structure.RingElem;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorComplex;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;

public class AlgebraUtil {
  private static class DistributeAlgorithm {
    final IASTAppendable resultCollector;
    final IExpr head;
    final IAST arg1;
    boolean evaled;

    DistributeAlgorithm(IASTAppendable resultCollector, IExpr head, IAST arg1) {
      this.resultCollector = resultCollector;
      this.head = head;
      this.arg1 = arg1;
      this.evaled = false;
    }

    public boolean distribute(final IAST ast) {
      IASTAppendable stepResult;
      final int stepSize = arg1.size();
      if (ast.size() >= 6) {
        stepResult = F.ast(ast.arg5(), stepSize);
      } else {
        stepResult = F.ast(arg1.head(), stepSize);
      }
      distributePositionRecursive(stepResult, 1);
      return evaled;
    }

    public void distributePositionRecursive(IASTAppendable stepResult, int position) {
      if (arg1.size() == position) {
        resultCollector.append(stepResult);
        return;
      }
      if (arg1.size() < position) {
        return;
      }
      if (arg1.get(position).isAST(head)) {
        IAST temp = (IAST) arg1.get(position);
        temp.forEach((IExpr x) -> distributeStep(x, stepResult, position));
        evaled = true;
      } else {
        IASTAppendable res2 = stepResult;
        res2.append(arg1.get(position));
        distributePositionRecursive(res2, position + 1);
      }
    }

    private void distributeStep(IExpr x, IAST stepResult, int position) {
      IASTAppendable res2 = stepResult.appendClone(x);
      // res2.append(x);
      distributePositionRecursive(res2, position + 1);
    }
  }

  public static class Expander {
    private static IExpr binaryFlatTimes(IExpr expr1, IExpr expr2) {
      if (expr1.isIndeterminate() || expr2.isIndeterminate()) {
        return S.Indeterminate;
      }
      if (expr1.isZero() || expr2.isZero()) {
        return F.C0;
      }
      if (expr1.isOne()) {
        return expr2;
      }
      if (expr2.isOne()) {
        return expr1;
      }
      if (expr1.isNumber() && expr2.isNumber()) {
        return expr1.times(expr2);
      }
      TimesOp timesOp = new TimesOp(32);
      timesOp.appendRecursive(expr1);
      timesOp.appendRecursive(expr2);
      return timesOp.getProduct();
    }

    private static IExpr expandSimpleTimesPlus(final IExpr expr0, final IExpr plusAST1) {
      if (plusAST1.isPlus2()) {
        IExpr p10 = plusAST1.first();
        IExpr p11 = plusAST1.second();
        if (expr0.isPlus2()) {
          IExpr p00 = expr0.first();
          IExpr p01 = expr0.second();

          if (expr0.second().equals(plusAST1.second())) {
            if (p00.equals(p10.negate())) {
              // Multiplication can be transformed into difference of squares
              // (a+b)*(a-b) == a^2 - b^2
              return F.Plus(p01.times(p01), p10.times(p10).negate());
            }
          } else if (p00.equals(p10)) {
            if (p01.equals(p11.negate())) {
              // Multiplication can be transformed into difference of squares
              // (a+b)*(a-b) == a^2 - b^2
              return F.Plus(p00.times(p10), p01.times(p01).negate());
            }
          }
        } else {
          // if (!p10.isAST() && !p11.isAST()) {

          // if (!expr0.isSymbol() && !expr0.isPower()) {
          // return F.Plus(expr0.times(p10), expr0.times(p11));
          // }

          // }
        }
      }
      return F.NIL;
    }

    private static IExpr flattenOneIdentity(IAST result, IExpr defaultValue) {
      return EvalAttributes.flattenDeep(result).orElse(result).oneIdentity(defaultValue);
    }

    /**
     * ASTs which are expanded in this <code>Expander</code> are cached in a set. The expansion is
     * only cached in the set, if the AST doesn t get the {@link IAST#IS_EXPANDED} flag.
     */
    Set<IAST> expandedASTs = Collections.newSetFromMap(new IdentityHashMap<>());
    final boolean expandNegativePowers;

    final boolean distributePlus;

    final boolean evalParts;

    final boolean factorTerms;

    /** Pattern matcher which may be <code>null</code> if undefined */
    final Predicate<IExpr> matcher;

    public Expander(Predicate<IExpr> matcher, boolean expandNegativePowers, boolean distributePlus,
        boolean evalParts, boolean factorTerms) {
      this.matcher = matcher;
      this.expandNegativePowers = expandNegativePowers;
      this.distributePlus = distributePlus;
      this.evalParts = evalParts;
      this.factorTerms = factorTerms;
    }

    private IExpr addExpanded(IExpr expr) {
      if (expr.isAST()) {
        if (expandNegativePowers && evalParts && !distributePlus && !factorTerms
            && matcher == null) {
          ((IAST) expr).addEvalFlags(IAST.IS_EXPANDED);
        } else {
          expandedASTs.add((IAST) expr);
        }
      }
      return expr;
    }

    /**
     * Evaluate <code>expr1 * expr2</code> and expand the resulting expression, if it's an <code>
     * IAST</code>.
     *
     * @param expr1
     * @param expr1Eval
     * @param expr2
     * @param expr2Eval
     * @param engine
     * @return
     */
    private IExpr binaryFlatTimesExpr(IExpr expr1, boolean expr1Eval, IExpr expr2,
        boolean expr2Eval, EvalEngine engine) {
      if (expr1.isIndeterminate() || expr2.isIndeterminate()) {
        return S.Indeterminate;
      }
      if (expr1.isZero() || expr2.isZero()) {
        return F.C0;
      }
      if (expr1.isOne()) {
        if (expr2Eval && expr2.isPlusTimesPower()) {
          expr2 = expandAST((IAST) expr2).orElse(expr2);
        }
        return expr2;
      }
      if (expr2.isOne()) {
        if (expr1Eval && expr1.isPlusTimesPower()) {
          expr1 = expandAST((IAST) expr1).orElse(expr1);
        }
        return expr1;
      }
      if (expr1Eval && expr1.isPlusTimesPower()) {
        expr1 = expandAST((IAST) expr1).orElse(expr1);
      }
      if (expr2Eval && expr2.isPlusTimesPower()) {
        expr2 = expandAST((IAST) expr2).orElse(expr2);
      }
      if (expr1.isNumber() && expr2.isNumber()) {
        return expr1.times(expr2);
      }
      int size = expr1.isTimes() ? expr1.size() : 1;
      size += expr2.isTimes() ? expr2.size() : 1;
      TimesOp timesOp = new TimesOp(size);
      timesOp.appendRecursive(expr1);
      timesOp.appendRecursive(expr2);
      return timesOp.getProduct();
      // IASTAppendable timesAST = F.TimesAlloc(size);
      // if (expr1.isTimes()) {
      // timesAST.appendAll((IAST) expr1, 1, expr1.size());
      // } else {
      // timesAST.append(expr1);
      // }
      // if (expr2.isTimes()) {
      // timesAST.appendAll((IAST) expr2, 1, expr2.size());
      // } else {
      // timesAST.append(expr2);
      // }
      // return TimesOp.getProduct(timesAST, engine);
    }

    /**
     * Evaluate <code>expr1 * expr2</code> and expand the resulting expression, if it's an <code>
     * IAST</code>. After that add the resulting expression to the <code>PlusOp</code>
     *
     * @param expr1
     * @param expr1Eval
     * @param expr2
     * @param expr2Eval
     * @param plusOp
     * @param engine
     */
    private void evalAndExpandAST(IExpr expr1, boolean expr1Eval, IExpr expr2, boolean expr2Eval,
        PlusOp plusOp, EvalEngine engine) {
      IExpr timesExpr = binaryFlatTimesExpr(expr1, expr1Eval, expr2, expr2Eval, engine);
      plusOp.plus(timesExpr);
    }

    /**
     * @param ast
     * @return
     */
    public IExpr expandAST(final IAST ast) {
      if (isPatternFree(ast)) {
        return F.NIL;
      }
      if (ast.isExpanded() && expandNegativePowers && !distributePlus) {
        return F.NIL;
      }
      if (expandedASTs.contains(ast)) {
        return F.NIL;
      }
      if (ast.isPower()) {
        return expandPowerNIL(ast);
      } else if (ast.isTimes()) {
        // (a+b)*(c+d)...
        EvalEngine engine = EvalEngine.get();

        Optional<IExpr[]> temp =
            fractionalPartsTimesPower(ast, false, false, false, evalParts, true, true);
        IExpr tempExpr;
        if (temp.isEmpty()) {
          return expandTimes(ast, engine);
        }
        IExpr[] parts = temp.get();
        if (parts[0].isOne()) {
          if (parts[1].isTimes()) {
            tempExpr = expandTimes((IAST) parts[1], engine);
            if (tempExpr.isPresent()) {
              return F.Power(tempExpr, F.CN1);
            }
            addExpanded(ast);
            return F.NIL;
          }
          if (parts[1].isPower() || parts[1].isPlus()) {
            IExpr denom = expandAST((IAST) parts[1]);
            if (denom.isPresent()) {
              return F.Power(denom, F.CN1);
            }
          }
          addExpanded(ast);
          return F.NIL;
        }

        if (parts[1].isOne()) {
          return expandTimes(ast, engine);
        }

        boolean evaled = false;
        if (parts[0].isTimes()) {
          tempExpr = expandTimes((IAST) parts[0], engine);
          if (tempExpr.isPresent()) {
            parts[0] = tempExpr;
            evaled = true;
          }
        }
        if (expandNegativePowers) {
          if (parts[1].isTimes()) {
            tempExpr = expandTimes((IAST) parts[1], engine);
            if (tempExpr.isPresent()) {
              parts[1] = tempExpr;
              evaled = true;
            }
          } else {
            if (parts[1].isPower() || parts[1].isPlus()) {
              IExpr denom = expandAST((IAST) parts[1]);
              if (denom.isPresent()) {
                parts[1] = denom;
                evaled = true;
              }
            }
          }
        }
        IExpr[] parts3 = temp.get();
        IExpr powerAST = F.Power(parts3[1], F.CN1);
        if (distributePlus && parts3[0].isPlus()) {
          IAST mappedAST =
              ((IAST) parts3[0]).mapThreadEvaled(EvalEngine.get(), F.Times(null, powerAST), 1);
          IExpr flattened = flattenOneIdentity(mappedAST, F.C0); // EvalAttributes.flatten(mappedAST).orElse(mappedAST);
          return addExpanded(flattened);
        }
        if (evaled) {
          // return addExpanded(binaryFlatTimesExpr(temp[0], powerAST, engine));
          return addExpanded(binaryFlatTimes(parts3[0], powerAST));
        }
        addExpanded(ast);
        return F.NIL;
      } else if (ast.isPlus()) {
        return expandPlus(ast);
      }
      addExpanded(ast);
      return F.NIL;
    }

    /**
     * <code>expr*(a+b+c) -> expr*a+expr*b+expr*c</code>
     *
     * @param expr1
     * @param plusAST
     * @return
     */
    private IExpr expandExprTimesPlus(final IExpr expr1, final IAST plusAST, EvalEngine engine) {
      PlusOp plusOp = new PlusOp(plusAST.argSize());
      final IExpr t = expr1.isPlusTimesPower() ? expandAST((IAST) expr1).orElse(expr1) : expr1;
      plusAST.forEach(x -> {
        // evaluate to flatten out Times() exprs
        evalAndExpandAST(t, false, x, true, plusOp, engine);
      });
      return plusOp.getSum();
    }

    /**
     * @param ast
     * @return {@link F#NIL} if no evaluation is possible
     */
    private IExpr expandPlus(final IAST ast) {
      IASTAppendable result = F.NIL;
      for (int i = 1; i < ast.size(); i++) {
        final IExpr arg = ast.get(i);
        if (arg.isAST()) {
          IExpr temp = expandAST((IAST) arg);
          if (temp.isPresent()) {
            if (result.isNIL()) {
              result = ast.copyUntil(ast.size(), i);
            }
            result.append(temp);
            continue;
          }
        }
        result.ifAppendable(r -> r.append(arg));
      }
      if (result.isPresent()) {
        // return result;
        return addExpanded(flattenOneIdentity(result, F.C0));
        // return PlusOp.plus(result);
      }
      addExpanded(ast);
      return F.NIL;
    }

    /**
     * <code>(a+b)*(c+d) -> a*c+a*d+b*c+b*d</code>
     *
     * @param plusAST0
     * @param plusAST1
     * @return
     */
    private IExpr expandPlusTimesPlus(final IAST plusAST0, final IAST plusAST1) {
      final EvalEngine engine = EvalEngine.get();
      if (isPatternFree(plusAST0)) {
        if (isPatternFree(plusAST1)) {
          return F.NIL;
        }
        // result = F.ast(S.Plus, (int) plusAST1.argSize());
        PlusOp plusOp = new PlusOp(plusAST1.argSize());
        final IExpr t =
            plusAST0.isPlusTimesPower() ? expandAST(plusAST0).orElse(plusAST0) : plusAST0;
        plusAST1.forEach(x -> evalAndExpandAST(t, false, x, true, plusOp, engine));
        return plusOp.getSum();
      } else if (isPatternFree(plusAST1)) {
        // result = F.ast(S.Plus, plusAST0.argSize());
        PlusOp plusOp = new PlusOp(plusAST0.argSize());
        final IExpr t =
            plusAST1.isPlusTimesPower() ? expandAST(plusAST1).orElse(plusAST1) : plusAST1;
        plusAST0.forEach(x -> evalAndExpandAST(x, true, t, false, plusOp, engine));
        return plusOp.getSum();
      }
      IExpr expanded = expandSimpleTimesPlus(plusAST0, plusAST1);
      if (expanded.isPresent()) {
        return expanded;
      }
      long numberOfTerms = (long) (plusAST0.argSize()) * (long) (plusAST1.argSize());
      if (numberOfTerms > Config.MAX_AST_SIZE) {
        throw new ASTElementLimitExceeded(numberOfTerms);
      }
      PlusOp plusOp = new PlusOp((int) numberOfTerms);
      plusAST0.forEach(x -> {
        final IExpr t = x.isPlusTimesPower() ? expandAST((IAST) x).orElse(x) : x;
        plusAST1.forEach(y -> {
          // evaluate to flatten out Times() expressions
          evalAndExpandAST(t, false, y, true, plusOp, engine);
        });
      });
      return plusOp.getSum();
    }

    /**
     * Expand a polynomial power with the multinomial theorem. See
     * <a href= "http://en.wikipedia.org/wiki/Multinomial_theorem">Wikipedia - Multinomial
     * theorem</a>
     *
     * @param plusAST the base of the power
     * @param n <code>n &ge; 0</code> the exponent of the power
     * @return
     */
    private IExpr expandPower(final IAST plusAST, final int n) {
      if (n == 1) {
        return expandPlus(plusAST).orElseGet(() -> addExpanded(plusAST));
      }
      if (n == 0) {
        return F.C1;
      }

      if (isPatternFree(plusAST)) {
        addExpanded(plusAST);
        return F.NIL;
      }

      if (plusAST.isPlus2() && n == 2) {
        IExpr a = plusAST.arg1();
        IExpr b = plusAST.arg2();
        // Use binomial theorem (a+b)^2 = a^2 + 2 * a * b + b^2
        return F.Plus(a.times(a), F.C2.times(a).times(b), b.times(b));
      }

      int k = plusAST.argSize();
      if (Integer.MAX_VALUE - n - k < 0) {
        throw new ASTElementLimitExceeded(n + k);
      }
      long numberOfTerms = LongMath.binomial(n + k - 1, k - 1);
      if (numberOfTerms >= Integer.MAX_VALUE || numberOfTerms > Config.MAX_AST_SIZE) {
        throw new ASTElementLimitExceeded(numberOfTerms);
      }
      final IASTAppendable expandedResult =
          ExpandMultinomialTheorem.expand(plusAST, n, (int) numberOfTerms);
      return addExpanded(flattenOneIdentity(expandedResult, F.C0));
    }

    /**
     * Expand <code>(a+b)^i</code> with <code>i</code> an integer number in the range
     * Integer.MIN_VALUE+1 to Integer.MAX_VALUE.
     *
     * @param powerAST
     * @return
     */
    private IExpr expandPowerNIL(final IAST powerAST) {
      IExpr base = powerAST.arg1();
      IExpr exponent = powerAST.arg2();
      IExpr temp = F.NIL;
      if (base.isPlusTimesPower()) {
        temp = expandAST((IAST) base);
        if (temp.isPresent()) {
          base = temp;
        }
      }
      if ((base.isPlus())) {
        if (exponent.isFraction()) {
          IFraction fraction = (IFraction) exponent;
          if (fraction.isPositive()) {
            INumber floorPart = fraction.floorFraction().normalize();
            if (!floorPart.isZero()) {
              IFraction fractionalPart = fraction.fractionalPart();
              return expandAST(F.Times(F.Power(base, fractionalPart), F.Power(base, floorPart)));
            }
          }
        }

        int exp = exponent.toIntDefault();
        if (exp == Config.INVALID_INT) {
          addExpanded(powerAST);
          return F.NIL;
        }
        IAST plusAST = (IAST) base;
        if (exp < 0) {
          if (expandNegativePowers) {
            exp *= (-1);
            return F.Power(expandPower(plusAST, exp), F.CN1);
          }
          addExpanded(powerAST);
          return F.NIL;
        }
        return expandPower(plusAST, exp);
      }
      if (temp.isPresent()) {
        temp = F.Power(base, exponent);
        addExpanded(temp);
        return temp;
      }
      addExpanded(powerAST);
      return F.NIL;
    }

    private IExpr expandTimes(final IAST timesAST, EvalEngine engine) {
      IExpr result = timesAST.arg1();
      if (result.isPlusTimesPower()) {
        result = expandAST((IAST) result).orElse(result);
      }
      IExpr temp;
      boolean evaled = false;
      if (!isPatternFree(result)) {
        if (result.isPower()) {
          temp = expandPowerNIL((IAST) result);
          if (temp.isPresent()) {
            result = temp;
            evaled = true;
          }

        } else if (result.isPlus()) {
          temp = expandPlus((IAST) result);
          if (temp.isPresent()) {
            result = temp;
            evaled = true;
          }
        }
      }

      for (int i = 2; i < timesAST.size(); i++) {
        IExpr arg = timesAST.get(i);
        if (!isPatternFree(arg)) {
          if (arg.isPower()) {
            arg = expandPowerNIL((IAST) arg);
            if (arg.isNIL()) {
              arg = timesAST.get(i);
            } else {
              evaled = true;
            }
          } else if (arg.isPlus()) {
            arg = expandPlus((IAST) arg);
            if (arg.isNIL()) {
              arg = timesAST.get(i);
            } else {
              evaled = true;
            }
          }
        }
        result = expandTimesBinary(result, arg, engine);
      }
      if (!timesAST.equals(result)) {
        temp = EvalEngine.get().evaluateNIL(result);
        if (temp.isPresent()) {
          result = temp;
          evaled = true;
        }
      }
      if (!evaled && timesAST.equals(result)) {
        addExpanded(timesAST);
        return F.NIL;
      }
      return addExpanded(result);
    }

    private IExpr expandTimesBinary(final IExpr arg1, IExpr arg2, EvalEngine engine) {
      if (arg1.isPlus()) {
        if (!arg2.isPlus()) {
          return expandExprTimesPlus(arg2, (IAST) arg1, engine);
        }
        // assure Plus(...)
        final IAST ast1 = arg2.isPlus() ? (IAST) arg2 : F.Plus(arg2);
        return expandPlusTimesPlus((IAST) arg1, ast1);
      }
      if (arg2.isPlus()) {
        if (factorTerms && arg1.isExactNumber()) {
          IExpr temp = S.FactorTerms.ofNIL(EvalEngine.get(), arg2);
          if (temp.isPresent()) {
            return F.Times(arg1, temp);
          }
        }
        return expandExprTimesPlus(arg1, (IAST) arg2, engine);
      }
      if (arg1.equals(arg2)) {
        return F.Power(arg1, F.C2);
      }
      // return binaryFlatTimesExpr(arg1, arg2, engine);
      return binaryFlatTimes(arg1, arg2);
    }

    /**
     * Check if the given expression doesn't contain the pattern.
     *
     * @param expression
     * @return
     */
    private boolean isPatternFree(IExpr expression) {
      return (matcher != null && expression.isFree(matcher, false));
    }

  }

  public static final IBuiltInSymbol reduceConstantTerm =
      F.localFunction("reduceConstantTerm", (c) -> {
        if (c.isNumber()) {
          return F.list(c, F.C1);
        }
        if (c.isTimes() && c.first().isNumber()) {
          return F.list(c.first(), c.rest().oneIdentity1());
        }
        return F.list(F.C1, c);
      });

  private static boolean appendPlus(IASTAppendable ast, IExpr expr) {
    if (ast.head().equals(S.Plus) && expr.head().equals(S.Plus)) {
      return ast.appendArgs((IAST) expr);
    }
    return ast.append(expr);
  }

  public static void calculateNumeratorGCD(IExpr arg, int position, IASTAppendable numeratorPlus,
      IInteger gcd, boolean[] error) {
    if (!error[0]) {
      if (arg.isInteger()) {
        numeratorPlus.set(position, ((IInteger) arg).div(gcd));
        return;
      } else if (arg.isTimes()) {
        IExpr arg1 = arg.first();
        if (arg1.isInteger()) {
          IInteger factor = ((IInteger) arg1).div(gcd);
          if (factor.isOne()) {
            IASTMutable times = ((IAST) arg).removeAtCopy(1);
            numeratorPlus.set(position, times.oneIdentity1());
            return;
          } else {
            IASTMutable times = ((IAST) arg).copy();
            times.set(1, factor);
            numeratorPlus.set(position, times);
            return;
          }
        } else if (arg1.isComplex()) {
          IComplex cmp = ((IComplex) arg1);
          if (cmp.re().isInteger() && cmp.im().isInteger()) {
            IInteger factorRe = ((IInteger) cmp.re()).div(gcd);
            IInteger factorIm = ((IInteger) cmp.im()).div(gcd);
            IASTMutable times = ((IAST) arg).copy();
            times.set(1, F.CC(factorRe, factorIm));
            numeratorPlus.set(position, times);
            return;
          }
        }
      }
      error[0] = true;
    }
  }


  /**
   * Return the result divided by the gcd value.
   *
   * @param numeratorPlus a <code>Plus[...]</code> expression as the numerator
   * @param denominatorInt an integer value for the denominator
   * @param gcd the integer gcd value
   * @return {@link Optional#empty()} if evaluation wasn't possible
   */
  public static Optional<IExpr[]> calculatePlusIntegerGCD(IASTAppendable numeratorPlus,
      IInteger denominatorInt, IInteger gcd) {
    boolean[] error = new boolean[] {false};
    numeratorPlus
        .forEach((IExpr x, int i) -> calculateNumeratorGCD(x, i, numeratorPlus, gcd, error));
    if (error[0]) {
      return Optional.empty();
    }
    IExpr[] result = new IExpr[3];
    result[0] = F.C1;
    result[1] = numeratorPlus;
    result[2] = denominatorInt.div(gcd);
    return Optional.of(result);
  }

  /**
   * Cancel common factors in numerator and denominator.
   * 
   * @param numerator
   * @param denominator
   * @return a {@link IAST} list <code>{numerator, denominator}</code> with common factors canceled
   *         out or {@link F#NIL} if no cmmon factors were found
   */
  public static IAST cancelCommonFactors(IExpr numerator, IExpr denominator) {
    IExpr numer = numerator;
    if (numerator.isPlus() //
        || (numerator.isTimes() && numerator.exists(x -> x.isPlus()))) {
      if (VariablesSet.isMultivariate(numerator, 3)) {
        numer = S.Factor.of(numerator);
      }
    } else if (numerator.isSymbol() //
        || (numerator.isPower() && numerator.first().isSymbol())) {
      numer = F.Times(numerator);
    }
    IExpr denom = denominator;
    if (denominator.isPlus()//
        || (denominator.isTimes() && denominator.exists(x -> x.isPlus()))) {
      if (VariablesSet.isMultivariate(denominator, 3)) {
        denom = S.Factor.of(denominator);
      }
    } else if (denominator.isSymbol() //
        || (denominator.isPower() && denominator.first().isSymbol())) {
      denom = F.Times(denominator);
    }
    if (numer.isTimes() || denom.isTimes()) {
      IAST p0Times = AbstractFunctionEvaluator.getNegativePlusInTimes(numer);
      IAST p1Times = AbstractFunctionEvaluator.getNegativePlusInTimes(denom);
      // IAST p0Times = p0.isTimes() ? (IAST) p0 : F.Times(p0);
      // IAST p1Times = p1.isTimes() ? (IAST) p1 : F.Times(p0);
      IASTAppendable t0 = p0Times.copyAppendable();
      IASTAppendable t1 = p1Times.copyAppendable();
      int i = 1;
      boolean evaled = false;

      IASTAppendable commonFactors = F.NIL;
      termChanged: while (i < t0.size()) {
        IExpr t0Arg = t0.get(i);
        int j = 1;
        while (j < t1.size()) {
          IExpr t1Arg = t1.get(j);
          if (t0Arg.equals(t1Arg)) {
            t0.remove(i);
            t1.remove(j);
            evaled = true;
            if (Config.TRACE_BASIC_ARITHMETIC) {
              if (EvalEngine.get().isTraceMode()) {
                if (commonFactors.isNIL()) {
                  commonFactors = F.TimesAlloc(i);
                }
                commonFactors.append(t0Arg);
              }
            }
            break termChanged;
          } else if (t0Arg.isPower() || t1Arg.isPower()) {
            final IExpr t0Base;
            final IExpr t0Exponent;
            if (!t0Arg.isPower()) {
              t0Base = t0Arg;
              t0Exponent = F.C1;
            } else {
              t0Base = t0Arg.base();
              t0Exponent = t0Arg.exponent();
            }
            final IExpr t1Base;
            final IExpr t1Exponent;
            if (!t1Arg.isPower()) {
              t1Base = t1Arg;
              t1Exponent = F.C1;
            } else {
              t1Base = t1Arg.base();
              t1Exponent = t1Arg.exponent();
            }
            if (t0Exponent.isReal() && t1Exponent.isReal() && t0Base.equals(t1Base)) {
              IReal exp0 = (IReal) t0Exponent;
              IReal exp1 = (IReal) t1Exponent;
              final IReal subtracted;
              if (exp0.isGE(exp1)) {
                subtracted = exp0.subtractFrom(exp1);
                t0.set(i, F.Power(t0Base, subtracted));
                t1.remove(j);
                i++;
                if (Config.TRACE_BASIC_ARITHMETIC) {
                  if (EvalEngine.get().isTraceMode()) {
                    if (commonFactors.isNIL()) {
                      commonFactors = F.TimesAlloc(i);
                    }
                    commonFactors.append(F.Power(t0Base, exp1));
                  }
                }
              } else {
                subtracted = exp1.subtractFrom(exp0);
                t0.remove(i);
                t1.set(j, F.Power(t1Base, subtracted));
                j++;
                if (Config.TRACE_BASIC_ARITHMETIC) {
                  if (EvalEngine.get().isTraceMode()) {
                    if (commonFactors.isNIL()) {
                      commonFactors = F.TimesAlloc(i);
                    }
                    commonFactors.append(F.Power(t0Base, exp0));
                  }
                }
              }
              evaled = true;
              break termChanged;
            }
          }
          j++;
        }
        i++;
      }
      if (evaled) {
        IExpr p0Result = t0.oneIdentity1();
        IExpr p1Result = t1.oneIdentity1();
        if (Config.TRACE_BASIC_ARITHMETIC && EvalEngine.get().isTraceMode()) {
          if (EvalEngine.get().isTraceMode()) {
            if (!numer.equals(numerator)) {
              EvalEngine.get().addTraceStep(F.Divide(numerator, denominator),
                  F.Divide(numer, denominator),
                  F.List(S.Cancel, F.$str("Factor"), numerator, numer));
            }
            if (!denom.equals(denominator)) {
              EvalEngine.get().addTraceStep(F.Divide(numer, denominator), F.Divide(numer, denom),
                  F.List(S.Cancel, F.$str("Factor"), denominator, denom));
            }
            EvalEngine.get().addTraceStep(F.Divide(numer, denom), F.Divide(p0Result, p1Result),
                F.List(S.Cancel, F.$str("CancelCommonFactors"), commonFactors));
          }
        }
        return F.pair(p0Result, p1Result);
      }
    }
    return F.NIL;

  }

  /**
   * @param powerTimesAST an <code>Times[...] or Power[...]</code> AST, where common factors should
   *        be canceled out.
   * @return {@link F#NIL} is no evaluation was possible
   * @throws JASConversionException
   */
  public static IExpr cancelFractionalParts(IExpr powerTimesAST) throws JASConversionException {
    Optional<IExpr[]> parts = fractionalParts(powerTimesAST, false);
    IExpr cancelResult = F.NIL;
    if (parts.isPresent()) {
      IExpr numerator = parts.get()[0];
      IExpr denominator = parts.get()[1];
      if (numerator.isPlus() || numerator.isTimes() || denominator.isPlus()
          || denominator.isTimes()) {
        IAST temp = cancelCommonFactors(numerator, denominator);
        if (temp.isPresent()) {
          numerator = temp.first();
          denominator = temp.second();
          cancelResult = F.Divide(temp.first(), temp.second());
        }
      }

      if (numerator.isPlus() && denominator.isPlus()) {
        // VariablesSet eVar = new VariablesSet(powerTimesAST);
        // IASTAppendable variables = eVar.getVarList();
        IAST numParts = numerator.partitionPlus(x -> isPolynomial(x), F.C0, F.C1, S.List);
        IAST denParts = denominator.partitionPlus(x -> isPolynomial(x), F.C0, F.C1, S.List);
        if (denParts.isPresent() && !denParts.arg1().isOne()) {
          Optional<IExpr[]> result = cancelGCD(numParts.arg1(), denParts.arg1());
          if (result.isPresent()) {
            IExpr[] elements = result.get();
            return F.Times(elements[0], elements[1], numParts.arg2(),
                F.Power(F.Times(elements[2], denParts.arg2()), F.CN1));
          }
        }
      }
    }
    return cancelResult;
  }

  /**
   * Calculate the 3 elements result array
   *
   * <pre>
   * [
   *   commonFactor,
   *   numeratorPolynomial.divide(gcd(numeratorPolynomial, denominatorPolynomial)),
   *   denominatorPolynomial.divide(gcd(numeratorPolynomial, denominatorPolynomial))
   * ]
   * </pre>
   *
   * for the given expressions <code>numeratorPolynomial</code> and <code>denominatorPolynomial
   * </code>.
   *
   * @param numerator an expression which should be converted to JAS polynomial (using
   *        substitutions)
   * @param denominator a expression which could be converted to JAS polynomial (using
   *        substitutions)
   * @return {@link Optional#empty()} if the expressions couldn't be converted to JAS polynomials or
   *         gcd equals 1
   * @throws JASConversionException
   */
  public static Optional<IExpr[]> cancelGCD(final IExpr numerator, final IExpr denominator)
      throws JASConversionException {
    try {
      if (denominator.isInteger() && numerator.isPlus()) {
        Optional<IExpr[]> result = cancelPlusIntegerGCD((IAST) numerator, (IInteger) denominator);
        if (result.isPresent()) {
          return result;
        }
      }

      VariablesSet eVar = new VariablesSet(numerator);
      eVar.addVarList(denominator);
      if (eVar.size() == 0) {
        return Optional.empty();
      }

      IAST vars = eVar.getVarList();
      PolynomialHomogenization substitutions = new PolynomialHomogenization(eVar, EvalEngine.get());
      IExpr[] subst = substitutions.replaceForward(numerator, denominator);
      IExpr numeratorPolynomial = subst[0];
      IExpr denominatorPolynomial = subst[1];
      if (substitutions.size() > 0) {
        eVar.clear();
        eVar.addAll(substitutions.substitutedVariablesSet());
        vars = eVar.getVarList();
      }
      try {
        ExprPolynomialRing ring = new ExprPolynomialRing(vars);
        ExprPolynomial pol1 = ring.create(numeratorPolynomial);
        ExprPolynomial pol2 = ring.create(denominatorPolynomial);
        List<IExpr> varList = eVar.getVarList().copyTo();
        JASIExpr jas = new JASIExpr(varList, true);
        GenPolynomial<IExpr> p1 = jas.expr2IExprJAS(pol1);
        GenPolynomial<IExpr> p2 = jas.expr2IExprJAS(pol2);

        GreatestCommonDivisor<IExpr> engine;
        engine = GCDFactory.getImplementation(ExprRingFactory.CONST);
        GenPolynomial<IExpr> gcd = engine.gcd(p1, p2);
        IExpr[] result = new IExpr[3];
        if (gcd.isONE()) {
          return Optional.empty();
          // result[0] = jas.exprPoly2Expr(gcd);
          // result[1] = jas.exprPoly2Expr(p1);
          // result[2] = jas.exprPoly2Expr(p2);
        } else {
          result[0] = F.C1;
          result[1] = F.eval(jas.exprPoly2Expr(p1.divide(gcd)));
          result[2] = F.eval(jas.exprPoly2Expr(p2.divide(gcd)));
        }
        result[0] = substitutions.replaceBackward(result[0]);
        result[1] = substitutions.replaceBackward(result[1]);
        result[2] = substitutions.replaceBackward(result[2]);
        return Optional.of(result);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
      // List<IExpr> varList = eVar.getVarList().copyTo();
      ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
      JASConvert<Complex<BigRational>> jas =
          new JASConvert<Complex<BigRational>>(eVar.getVarList(), cfac);
      GenPolynomial<Complex<BigRational>> p1 = jas.expr2JAS(numeratorPolynomial, false);
      if (p1 == null) {
        return Optional.empty();
      }
      GenPolynomial<Complex<BigRational>> p2 = jas.expr2JAS(denominatorPolynomial, false);
      if (p2 == null) {
        return Optional.empty();
      }
      GreatestCommonDivisor<Complex<BigRational>> engine;
      engine = GCDFactory.getImplementation(cfac);
      GenPolynomial<Complex<BigRational>> gcd;
      // if (numeratorPolynomial.isSymbol()||denominatorPolynomial.isSymbol() ) {
      // gcd = jas.expr2IExprJAS(F.C1);
      // }else {
      gcd = engine.gcd(p1, p2);
      // }
      IExpr[] result = new IExpr[3];
      if (gcd.isONE()) {
        return Optional.empty();
        // result[0] = jas.complexPoly2Expr(gcd);
        // result[1] = jas.complexPoly2Expr(p1);
        // result[2] = jas.complexPoly2Expr(p2);
      } else {
        result[0] = F.C1;
        result[1] = F.eval(jas.complexPoly2Expr(p1.divide(gcd)));
        result[2] = F.eval(jas.complexPoly2Expr(p2.divide(gcd)));
      }
      result[0] = substitutions.replaceBackward(result[0]);
      result[1] = substitutions.replaceBackward(result[1]);
      result[2] = substitutions.replaceBackward(result[2]);
      return Optional.of(result);
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      if (Config.DEBUG) {
        e.printStackTrace();
      }
    }
    return Optional.empty();
  }

  /**
   * Calculate the GCD[] of the integer factors in each element of the <code>numeratorPlus</code>
   * expression with the <code>denominator</code>. After that return the result divided by the gcd
   * value, if possible.
   *
   * @param numeratorPlus a <code>Plus[...]</code> expression as the numerator
   * @param denominator an integer value for the denominator
   * @return {@link Optional#empty()} if no gcd value was found
   */
  public static Optional<IExpr[]> cancelPlusIntegerGCD(IAST numeratorPlus, IInteger denominator) {
    IASTAppendable gcd = F.ast(S.GCD, numeratorPlus.size() + 1);
    gcd.append(denominator);
    boolean evaled = !numeratorPlus.exists((IExpr x) -> collectGCDFactors(x, gcd));
    if (evaled) {
      // GCD() has attribute Orderless, so the arguments will
      // be sorted by evaluation!
      IExpr igcd = F.eval(gcd);
      if (igcd.isInteger() && !igcd.isOne()) {
        IASTAppendable plus = numeratorPlus.copyAppendable();
        return calculatePlusIntegerGCD(plus, denominator, (IInteger) igcd);
      }
    }
    return Optional.empty();
  }

  /**
   * Append the gcd factors from <code>expr</code> in <code>gcdFactors</code>
   * 
   * @param expr
   * @param gcdFactors
   * @return <code>false</code> if a gcd factor could be collected; <code>true</code> otherwise.
   */
  public static boolean collectGCDFactors(IExpr expr, IASTAppendable gcdFactors) {
    if (expr.isInteger()) {
      gcdFactors.append(expr);
      return false;
    } else {
      if (expr.isTimes()) {
        IExpr arg1 = expr.first();
        if (arg1.isInteger()) {
          gcdFactors.append(arg1);
          return false;
        } else if (arg1.isComplex()) {
          IComplex cmp = (IComplex) arg1;
          if (cmp.re().isInteger() && cmp.im().isInteger()) {
            gcdFactors.append(cmp.re());
            gcdFactors.append(cmp.im());
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Get the &quot;denominator form&quot; of the given function. Example: <code>Csc[x]</code> gives
   * <code>Sin[x]</code>.
   *
   * @param function the function which should be transformed to &quot;denominator form&quot;
   *        determine the denominator by splitting up functions like <code>Tan[],Cot[], Csc[],...
   *     </code>
   * @param trig
   * @return {@link F#NIL} if <code>trig</code> is false or no form is found; may return
   *         <code>1</code> if no denominator form is available (Example Cos[]).
   */
  public static IExpr denominatorTrigForm(IAST function, boolean trig) {
    if (trig) {
      if (function.isAST1()) {
        for (int i = 0; i < F.DENOMINATOR_NUMERATOR_SYMBOLS.size(); i++) {
          final ISymbol symbol = F.DENOMINATOR_NUMERATOR_SYMBOLS.get(i);
          if (function.head().equals(symbol)) {
            IExpr result = F.DENOMINATOR_TRIG_TRUE_EXPRS.get(i);
            if (result.isSymbol()) {
              return F.unaryAST1(result, function.arg1());
            }
            return result;
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Call the distribute algorithm <code>F.Distribute(expr, head)</code>
   * 
   * @param ast
   * @param head
   */
  public static IExpr distribute(final IAST ast, IExpr head) {
    IAST list = (IAST) ast.arg1();
    IASTAppendable resultCollector;
    final int resultSize = list.argSize() > 127 ? list.argSize() : 127;
    if (ast.size() >= 5) {
      resultCollector = F.ast(ast.arg4(), resultSize);
    } else {
      resultCollector = F.ast(head, resultSize);
    }
    DistributeAlgorithm algorithm = new DistributeAlgorithm(resultCollector, head, list);
    if (algorithm.distribute(ast)) {
      return resultCollector;
    }
    return list;
  }


  /**
   * Call the distribute algorithm for a <code>Times(...)</code> {@link IAST}, which contains
   * <code>Plus(...)</code> terms. If <code>expr.isTimes() == false</code> return <code>expr</code>
   * 
   * @param expr should have the structure <code>Times(a,b,...)</code> with at least 2 arguments;
   *        otherwise the <code>expr</code> will be returned
   */
  public static IExpr distributeTimes(final IExpr expr) {
    if (expr.isTimes()) {
      return distribute(F.Distribute(expr), S.Plus);
    }
    return expr;
  }


  /**
   * Expand the given <code>ast</code> expression.
   *
   * @param ast
   * @param patt
   * @param distributePlus TODO
   * @param evalParts evaluate the determined numerator and denominator parts
   * @return {@link F#NIL} if the expression couldn't be expanded.
   */
  public static IExpr expand(final IAST ast, Predicate<IExpr> patt, boolean expandNegativePowers,
      boolean distributePlus, boolean evalParts) {

    return expand(ast, patt, expandNegativePowers, distributePlus, evalParts, false);
  }

  /**
   * Expand the given <code>ast</code> expression.
   *
   * @param ast
   * @param patt
   * @param evalParts evaluate the determined numerator and denominator parts
   * @param distributePlus
   * @param factorTerms
   * @return {@link F#NIL} if the expression couldn't be expanded.
   */
  public static IExpr expand(final IAST ast, Predicate<IExpr> patt, boolean expandNegativePowers,
      boolean distributePlus, boolean evalParts, boolean factorTerms) {
    Expander expander =
        new Expander(patt, expandNegativePowers, distributePlus, evalParts, factorTerms);
    return expander.expandAST(ast);
  }

  /**
   * Expand the given <code>ast</code> expression.
   *
   * @param ast
   * @param distributePlus
   * @return {@link F#NIL} if the expression couldn't be expanded.
   */
  public static IExpr expandAll(final IAST ast, Predicate<IExpr> patt, boolean expandNegativePowers,
      boolean distributePlus, boolean factorTerms, EvalEngine engine) {
    if (patt != null && ast.isFree(patt, true)) {
      return F.NIL;
    }
    IAST localAST = ast;
    IAST tempAST = F.NIL;
    if (localAST.isEvalFlagOff(IAST.IS_SORTED)) {
      tempAST = engine.evalFlatOrderlessAttrsRecursive(localAST);
      if (tempAST.isPresent()) {
        localAST = tempAST;
      }
    }
    if (localAST.isAllExpanded() && expandNegativePowers && !distributePlus) {
      if (localAST != ast) {
        return localAST;
      }
      return F.NIL;
    }
    IASTAppendable[] result = new IASTAppendable[1];
    result[0] = F.NIL;
    IExpr temp = F.NIL;

    int localASTSize = localAST.size();
    IExpr head = localAST.head();
    if (head.isAST()) {
      temp =
          expandAll((IAST) head, patt, expandNegativePowers, distributePlus, factorTerms, engine);
      temp.ifPresent(x -> result[0] = F.ast(x, localASTSize));
    }
    final IAST localASTFinal = localAST;
    localAST.forEach((x, i) -> {
      if (x.isAST()) {
        IExpr t =
            expandAll((IAST) x, patt, expandNegativePowers, distributePlus, factorTerms, engine);
        if (t.isPresent()) {
          if (result[0].isNIL()) {
            int size = localASTFinal.size() + 4; // 4 -> empirically determined value in JUnit tests
            if (t.isAST()) {
              size += ((IAST) t).size();
            }
            result[0] = F.ast(head, size);
            result[0].appendArgs(localASTFinal, i);
          }
          appendPlus(result[0], t);
          return;
        }
      }
      result[0].ifAppendable(r -> r.append(x));
    });

    if (result[0].isNIL()) {
      temp = expand(localAST, patt, expandNegativePowers, distributePlus, true, factorTerms);
      if (temp.isPresent()) {
        setAllExpanded(temp, expandNegativePowers, distributePlus);
        return temp;
      } else {
        if (localAST != ast) {
          setAllExpanded(localAST, expandNegativePowers, distributePlus);
          return localAST;
        }
      }
      setAllExpanded(ast, expandNegativePowers, distributePlus);
      return F.NIL;
    }
    temp = expand(result[0], patt, expandNegativePowers, distributePlus, true, factorTerms);
    if (temp.isPresent()) {
      return setAllExpanded(temp, expandNegativePowers, distributePlus);
    }
    return setAllExpanded(result[0], expandNegativePowers, distributePlus);
  }

  public static IExpr factor(IAST ast, IExpr arg1, VariablesSet eVar, boolean squareFree,
      boolean withHomogenization, boolean togetherMode, EvalEngine engine) {
    IExpr expr = arg1;
    if (!arg1.isTimes() && !arg1.isPower()) {
      if (togetherMode) {
        expr = S.Together.of(engine, arg1);
      }
      if (expr.isAST()) {
        IExpr[] fractionParts = numeratorDenominator((IAST) expr, true, engine);
        if (!fractionParts[1].isOne()) {
          try {
            IExpr numerator = factorExpr(F.Factor(fractionParts[0]), fractionParts[0], eVar,
                squareFree, withHomogenization, engine);
            IExpr denominator = factorExpr(F.Factor(fractionParts[1]), fractionParts[1], eVar,
                squareFree, withHomogenization, engine);
            if (numerator.isPresent() && denominator.isPresent()) {
              IExpr temp = engine.evaluate(F.Divide(numerator, denominator));
              engine.putCache(ast, temp);
              return temp;
            }
            engine.putCache(ast, F.NIL);
          } catch (JASConversionException e) {
            //
          }
          return arg1;
        }
      }
    }
    try {
      IExpr temp = factorExpr(ast, expr, eVar, squareFree, withHomogenization, engine);
      engine.putCache(ast, temp);
      if (temp.isPresent()) {
        return temp;
      }
    } catch (JASConversionException e) {
    }
    return arg1;
  }

  public static IExpr factor(IAST expr, VariablesSet eVar, boolean squareFree,
      boolean withHomogenization, EvalEngine engine) throws JASConversionException {
    if (expr.leafCount() > Config.MAX_FACTOR_LEAFCOUNT) {
      return expr;
    }

    // use TermOrderByName.INVLEX here!
    // See https://github.com/kredel/java-algebra-system/issues/8
    Object[] objects = null;
    JASConvert<BigRational> jas =
        new JASConvert<BigRational>(eVar.getVarList(), BigRational.ZERO, TermOrderByName.INVLEX);
    try {
      GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, false);
      if (polyRat == null) {
        if (!squareFree && withHomogenization) {
          return factorWithPolynomialHomogenization(expr, eVar, engine);
        }
        return F.NIL;
      }
      if (polyRat.length() <= 1) {
        return expr;
      }
      objects = jas.factorTerms(polyRat);
    } catch (JASConversionException e) {
      if (!squareFree && withHomogenization) {
        return factorWithPolynomialHomogenization(expr, eVar, engine);
      }
      return F.NIL;
    }

    if (objects != null) {

      SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map = null;
      try {
        GenPolynomial<edu.jas.arith.BigInteger> poly =
            (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
        if (eVar.size() == 1) {
          IExpr temp = heuristicXP2XPOne(poly, expr, eVar.getArrayList().get(0), engine);
          if (temp.isPresent()) {
            return temp;
          }
        }

        FactorAbstract<edu.jas.arith.BigInteger> factorAbstract =
            FactorFactory.getImplementation(edu.jas.arith.BigInteger.ONE);
        if (squareFree) {
          map = factorAbstract.squarefreeFactors(poly); // factors(poly);
        } else {
          map = factorAbstract.factors(poly);
        }
        // } catch (TimeExceededException texex) {
        // LOGGER.debug("Factor.factor() time limit exceeded", texex);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return expr;
      }
      if (map != null) {
        IASTAppendable result = F.TimesAlloc(map.size() + 1);
        java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
        java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
        IRational f = F.C1;
        if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
          f = F.fraction(gcd, lcm).normalize();
        }
        for (SortedMap.Entry<GenPolynomial<edu.jas.arith.BigInteger>, Long> entry : map
            .entrySet()) {
          if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
            continue;
          }
          IExpr base = jas.integerPoly2Expr(entry.getKey());
          if (entry.getValue() == 1L) {
            if (f.isMinusOne() && base.isPlus()) {
              base = ((IAST) base).map(x -> x.negate(), 1);
              f = F.C1;
            }
            result.append(base);
          } else {
            result.append(F.Power(base, F.ZZ(entry.getValue())));
          }
        }
        if (!f.isOne()) {
          result.append(f);
        }
        return engine.evaluate(result);
      }
    }
    return F.NIL;
  }

  /**
   * @param polynomial the complex-rational polynomial which should be factored
   * @param jas
   * @param head the head of the factorization result AST (typically <code>F.Times</code> or <code>
   *     F.List</code>)
   * @param cfac
   * @param original the original expression
   * @return
   */
  public static IExpr factorComplex(GenPolynomial<Complex<BigRational>> polynomial,
      JASConvert<? extends RingElem<?>> jas, ISymbol head, ComplexRing<BigRational> cfac,
      IExpr original) {
    if (polynomial.degree() > Config.MAX_POLYNOMIAL_DEGREE) {
      // Exponent ist out of bounds for function `1`.
      return Errors.printMessage(S.Factor, "lrgexp", F.List(S.Factor));
    }
    FactorComplex<BigRational> factorAbstract = new FactorComplex<BigRational>(cfac);
    SortedMap<GenPolynomial<Complex<BigRational>>, Long> map = factorAbstract.factors(polynomial);

    IASTAppendable result = F.ast(head, map.size());
    for (SortedMap.Entry<GenPolynomial<Complex<BigRational>>, Long> entry : map.entrySet()) {
      if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
        continue;
      }
      final IExpr key = jas.complexPoly2Expr(entry.getKey());
      if (entry.getValue().equals(1L) && map.size() <= 2
          && (key.equals(F.CNI) || key.equals(F.CI))) {
        // hack: factoring -I and I out of an expression should give no new factorized expression
        return original;
      }
      result.append(F.Power(jas.complexPoly2Expr(entry.getKey()), F.ZZ(entry.getValue())));
    }
    return result;
  }

  /**
   * Factor the <code>expr</code> in the domain of GaussianIntegers.
   *
   * @param expr the (polynomial) expression which should be factored
   * @param varList the list of variables
   * @param head the head of the factorization result AST (typically <code>F.Times</code> or <code>
   *     F.List</code>)
   * @param numeric2Rational transform numerical values to symbolic rational numbers
   * @param gaussianIntegers if <code>true</code> use Gaussian integers
   * @param engine
   * @return factorization result AST (typically with head <code>F.Times</code> or <code>
   *     F.List</code>)
   * @throws JASConversionException
   */
  public static IExpr factorComplex(IExpr expr, IAST varList, ISymbol head,
      boolean numeric2Rational, boolean gaussianIntegers, EvalEngine engine) {
    try {
      if (gaussianIntegers) {
        ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
        JASConvert<Complex<BigRational>> jas = new JASConvert<Complex<BigRational>>(varList, cfac);
        GenPolynomial<Complex<BigRational>> polyRat = jas.expr2JAS(expr, numeric2Rational);
        if (polyRat == null) {
          return expr;
        }
        return factorComplex(polyRat, jas, head, cfac, expr).eval(engine);
      } else {
        JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
        GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, numeric2Rational);
        if (polyRat == null) {
          return expr;
        }
        return Algebra.factorRational(polyRat, jas, head);
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      // LOGGER.debug("Algebra.factorComplex() failed", rex);
    }
    return expr;
  }

  /**
   * Factor the <code>expr</code> in the domain of GaussianIntegers.
   *
   * @param expr the (polynomial) expression which should be factored
   * @param varList the list of variables
   * @param head the head of the factorization result AST (typically <code>F.Times</code> or <code>
   *     F.List</code>)
   * @param gaussianIntegers if <code>true</code> use Gaussian integers
   * @param engine the evaluation engine
   * @return factorization result AST (typically with head <code>F.Times</code> or <code>
   *     F.List</code>)
   * @throws JASConversionException
   */
  public static IExpr factorComplex(IExpr expr, IAST varList, ISymbol head,
      boolean gaussianIntegers, EvalEngine engine) {
    return factorComplex(expr, varList, head, false, gaussianIntegers, engine);
  }

  public static IExpr factorExpr(final IAST ast, IExpr expr, VariablesSet eVar, boolean squareFree,
      boolean withHomogenization, EvalEngine engine) {
    if (expr.isAST()) {
      IExpr temp;
      // if (expr.isPower()&&expr.base().isPlus()) {
      // temp = factorExpr(ast, expr.base(), varList);
      // temp = F.Power(temp, expr.exponent());
      // } else
      if (expr.isPower()) {
        IExpr p =
            factorExpr((IAST) expr, expr.base(), eVar, squareFree, withHomogenization, engine);
        if (p.isPresent() && !p.equals(expr.base())) {
          return F.Power(p, expr.exponent());
        }
        return expr;
      } else if (expr.isTimes()) {
        temp = ((IAST) expr).map(x -> {
          if (x.isPlus()) {
            return factorExpr(ast, x, eVar, squareFree, withHomogenization, engine);
          }
          if (x.isPower() && x.base().isPlus()) {
            IExpr p = factorExpr(ast, x.base(), eVar, squareFree, withHomogenization, engine);
            if (p.isPresent() && !p.equals(x.base())) {
              return F.Power(p, x.exponent());
            }
          }
          return F.NIL;
        }, 1);
        return temp;
      } else {
        return factor((IAST) expr, eVar, squareFree, withHomogenization, engine);
      }
    }
    return expr;
  }

  public static Optional<IRational> factorTermsGCD(IAST plusAST, EvalEngine engine) {
    IRational gcd1 = null;
    if (plusAST.arg1().isRational()) {
      gcd1 = (IRational) plusAST.arg1();
    } else if (plusAST.arg1().isTimes() && plusAST.arg1().first().isRational()) {
      gcd1 = (IRational) plusAST.arg1().first();
    }
    if (gcd1 == null) {
      return Optional.empty();
    }
    for (int i = 2; i < plusAST.size(); i++) {
      IRational gcd2 = null;
      if (plusAST.get(i).isRational()) {
        gcd2 = (IRational) plusAST.get(i);
      } else if (plusAST.get(i).isTimes() && plusAST.get(i).first().isRational()) {
        gcd2 = (IRational) plusAST.get(i).first();
      }
      if (gcd2 == null) {
        return Optional.empty();
      }

      final IExpr gcd12 = gcd1.gcd(gcd2); // engine.evaluate(F.GCD(gcd1, gcd2));
      if (gcd12.isRational() && !gcd12.isOne()) {
        if (gcd1.isNegative() && gcd2.isNegative()) {
          gcd1 = ((IRational) gcd12).negate();
        } else {
          gcd1 = (IRational) gcd12;
        }
      } else {
        return Optional.empty();
      }
    }
    if (gcd1.isMinusOne()) {
      return Optional.empty();
    }
    return Optional.of(gcd1);
  }

  /**
   * Factor out a rational number which may be a factor in every sub-expression of <code>plus
   * </code>.
   *
   * @param plusAST
   * @param engine
   * @return {@link F#NIL} if the factor couldn't be found
   */
  /* package private */ public static IExpr factorTermsPlus(IAST plusAST, EvalEngine engine) {
    Optional<IRational> gcd1 = factorTermsGCD(plusAST, engine);
    if (gcd1.isPresent()) {
      IRational rationalGCD = gcd1.get();
      return F.Times(rationalGCD, F.Expand(F.Times(rationalGCD.inverse(), plusAST))).eval(engine);
    }
    return F.NIL;
  }

  public static IExpr factorWithPolynomialHomogenization(IAST expr, VariablesSet eVar,
      EvalEngine engine) {
    boolean gaussianIntegers = !expr.isFree(x -> x.isComplex() || x.isComplexNumeric(), false);
    PolynomialHomogenization substitutions = new PolynomialHomogenization(eVar, engine);
    IExpr subsPolynomial = substitutions.replaceForward(expr);
    // System.out.println(subsPolynomial.toString());
    // System.out.println(substitutions.substitutedVariables());
    if (substitutions.size() == 0) {
      return factorComplex(expr, eVar.getVarList(), S.Times, gaussianIntegers, engine);
    }
    if (subsPolynomial.isAST()) {
      // System.out.println(subsPolynomial);
      Set<ISymbol> varSet = substitutions.substitutedVariablesSet();
      eVar.addAll(varSet);
      IExpr factorization =
          factorComplex(subsPolynomial, eVar.getVarList(), S.Times, gaussianIntegers, engine);
      if (factorization.isPresent()) {
        return substitutions.replaceBackward(factorization);
      }
    }
    return expr;
  }

  /**
   * Determine common factors in a <code>Plus(...)</code> expression. Index <code>[0]</code>
   * contains the common factor. Index <code>[1]</code> contains the rest <code>Plus(...)</code>
   * factor;
   *
   * @param list a <code>List(...)</code> or <code>Plus(...)</code> AST of terms
   * @param reduceOneIdentityRest reduce the rest expression if only 1 argument is assigned
   * @return {@link Optional#empty()} if no common factor was found.
   */
  public static Optional<IExpr[]> findCommonFactors(IAST list, boolean reduceOneIdentityRest) {
    if (list.size() > 2) {
      HashMap<IExpr, IInteger> map = new HashMap<IExpr, IInteger>();
      splitTimesArg1(list.arg1(), map);
      if (map.size() != 0) {
        for (int i = 2; i < list.size(); i++) {
          if (!splitTimesRest(list.get(i), map)) {
            // fail fast
            return Optional.empty();
          }
        }

        IASTAppendable commonFactor = F.TimesAlloc(map.size());
        for (Map.Entry<IExpr, IInteger> entry : map.entrySet()) {
          final IExpr key = entry.getKey();
          IInteger exponent = entry.getValue();
          if (exponent.isOne()) {
            commonFactor.append(key);
          } else {
            commonFactor.append(F.Power(key, exponent));
          }
        }

        final IExpr[] result = new IExpr[2];
        result[0] = commonFactor.oneIdentity1();
        if (!result[0].isOne()) {
          IExpr inverse = result[0].inverse();

          IASTAppendable commonPlus = F.PlusAlloc(list.argSize());
          list.forEach(x -> commonPlus.append(F.Times(inverse, x)));
          if (reduceOneIdentityRest) {
            result[1] = commonPlus.oneIdentity1();
          } else {
            result[1] = commonPlus;
          }
          return Optional.of(result);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Split the expression into numerator and denominator parts, by separating positive and negative
   * powers and afterwards evaluate the numerator and denominator separately.
   *
   * @param arg
   * @param trig determine the denominator by splitting up functions like <code>
   *     Tan[],Cot[], Csc[],...</code>
   * @return the numerator and denominator expression or {@link Optional#empty()} if no denominator
   *         was found.
   */
  public static Optional<IExpr[]> fractionalParts(final IExpr arg, boolean trig) {
    return fractionalParts(arg, trig, true);
  }

  /**
   * Split the expression into numerator and denominator parts, by separating positive and negative
   * powers.
   *
   * @param arg
   * @param trig determine the denominator by splitting up functions like <code>
   *     Tan[],Cot[], Csc[],...</code>
   * @param evalParts evaluate the numerator and denominator separately
   * @return the numerator and denominator expression or {@link Optional#empty()} if no denominator
   *         was found.
   */
  public static Optional<IExpr[]> fractionalParts(final IExpr arg, boolean trig,
      boolean evalParts) {
    if (arg.isAST()) {
      IAST ast = (IAST) arg;
      if (arg.isTimes()) {
        return fractionalPartsTimesPower(ast, false, true, trig, evalParts, true, true);
      } else if (arg.isPower()) {
        return fractionalPartsPower(ast, trig, true);
      } else {
        IExpr numerForm = numeratorTrigForm(ast, trig);
        if (numerForm.isPresent()) {
          IExpr denomForm = denominatorTrigForm(ast, trig);
          if (denomForm.isPresent()) {
            IExpr[] parts = new IExpr[2];
            parts[0] = numerForm;
            parts[1] = denomForm;
            return Optional.of(parts);
          }
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Return the denominator for the given <code>Power[...]</code> {@link IAST} by separating
   * positive and negative powers.
   *
   * @param powerAST a power expression (a^b)
   * @param trig if <code>true</code> get the "trigonometric form" of the given function. Example:
   *        Csc[x] gives Sin[x].
   * @param splitPowerPlusExponents split <code>Power()</code> expressions with <code>Plus()
   *     </code> exponents like <code>a^(-x+y)</code> into numerator <code>a^y</code> and
   *        denominator <code>a^x</code>
   * @return the numerator and denominator expression or {@link Optional#empty()}
   */
  public static Optional<IExpr[]> fractionalPartsPower(final IAST powerAST, boolean trig,
      boolean splitPowerPlusExponents) {
    IExpr[] parts = new IExpr[2];
    parts[0] = F.C1;

    IExpr base = powerAST.base();
    IExpr exponent = powerAST.exponent();
    if (exponent.isReal()) {
      IReal sn = (IReal) exponent;
      if (sn.isMinusOne()) {
        parts[1] = base;
        return Optional.of(parts);
      } else if (sn.isNegative()) {
        parts[1] = F.Power(base, sn.negate());
        return Optional.of(parts);
      } else {
        if (sn.isInteger() && base.isAST()) {
          // positive integer
          IAST function = (IAST) base;
          // if (function.isTimes()) {
          // IExpr[] partsArg1 = fractionalPartsTimesPower(function, true, true, trig,
          // true);
          // if (partsArg1 != null) {
          // parts[0] = F.Power(partsArg1[0], sn);
          // parts[1] = F.Power(partsArg1[1], sn);
          // return parts;
          // }
          // }
          IExpr numerForm = numeratorTrigForm(function, trig);
          if (numerForm.isPresent()) {
            IExpr denomForm = denominatorTrigForm(function, trig);
            if (denomForm.isPresent()) {
              parts[0] = F.Power(numerForm, sn);
              parts[1] = F.Power(denomForm, sn);
              return Optional.of(parts);
            }
          }
        }
      }
    } else if (splitPowerPlusExponents && exponent.isPlus()) {
      // base ^ (a+b+c...)
      IAST plusAST = (IAST) exponent;
      IAST[] result = plusAST.filterNIL(AbstractFunctionEvaluator::getNormalizedNegativeExpression);
      IAST plus = result[0];
      if (plus.argSize() > 0) {
        parts[1] = base.power(plus.oneIdentity0());
        parts[0] = base.power(result[1].oneIdentity0());
        return Optional.of(parts);
      }
      return Optional.empty();
    }
    IExpr positiveExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(exponent);
    if (positiveExpr.isPresent()) {
      parts[1] = F.Power(base, positiveExpr);
      return Optional.of(parts);
    }
    return Optional.empty();
  }

  /**
   * Split the expression into numerator and denominator parts, by separating positive and negative
   * powers. Or split a number by numerator and denominator part.
   *
   * @param arg
   * @param trig determine the denominator by splitting up functions like <code>
   *     Tan[],Cot[], Csc[],...</code>
   * @param evalParts evaluate the numerator and denominator separately
   * @return the numerator and denominator expression or {@link Optional#empty()}
   */
  public static Optional<IExpr[]> fractionalPartsRational(final IExpr arg, boolean trig,
      boolean evalParts) {
    if (arg.isFraction()) {
      IFraction fr = (IFraction) arg;
      IExpr[] parts = new IExpr[2];
      parts[0] = fr.numerator();
      parts[1] = fr.denominator();
      return Optional.of(parts);
    } else if (arg.isComplex()) {
      IRational re = ((IComplex) arg).getRealPart();
      IRational im = ((IComplex) arg).getImaginaryPart();
      if (re.isFraction() || im.isFraction()) {
        IExpr[] parts = new IExpr[2];
        parts[0] = re.numerator().times(im.denominator())
            .plus(im.numerator().times(re.denominator()).times(F.CI));
        parts[1] = re.denominator().times(im.denominator());
        return Optional.of(parts);
      }
      return Optional.empty();
    }
    return fractionalParts(arg, trig, evalParts);
  }

  /**
   * Return the numerator and denominator for the given <code>Times[...]</code> or <code>Power[a, b]
   * </code> AST, by separating positive and negative powers.
   *
   * @param timesPower a Times[] or Power[] expression (a*b*c....) or a^b
   * @param splitNumeratorOne split a fractional number into numerator and denominator, only if the
   *        numerator is 1, if <code>true</code>, ignore <code>splitFractionalNumbers</code>
   *        parameter.
   * @param splitFractionalNumbers split a fractional number into numerator and denominator
   * @param trig try to find a trigonometric numerator/denominator form (Example: <code>Csc[x]
   *     </code> gives <code>1 / Sin[x]</code>)
   * @param evalParts evaluate the determined numerator and denominator parts
   * @param negateNumerDenom negate numerator and denominator, if they are both negative
   * @param splitPowerPlusExponents split <code>Power()</code> expressions with <code>Plus()</code>
   *        exponents like <code>a^(-x+y)</code> into numerator <code>a^y</code> and denominator
   *        <code>
   *     a^x</code>
   * @return the numerator and denominator expression and an optional fractional number (maybe
   *         <code>null</code>), if splitNumeratorOne is <code>true</code>
   */
  public static Optional<IExpr[]> fractionalPartsTimesPower(final IAST timesPower,
      boolean splitNumeratorOne, boolean splitFractionalNumbers, boolean trig, boolean evalParts,
      boolean negateNumerDenom, boolean splitPowerPlusExponents) {
    if (timesPower.isPower()) {
      return fractionalPartsPower(timesPower, trig, splitPowerPlusExponents);
    }

    IAST timesAST = timesPower;
    IExpr[] result = new IExpr[3];
    result[2] = null;
    IASTAppendable numerator = F.TimesAlloc(timesAST.size());
    IASTAppendable denominator = F.TimesAlloc(timesAST.size());

    IAST argAST;
    boolean evaled = false;
    boolean splitFractionEvaled = false;
    for (int i = 1; i < timesAST.size(); i++) {
      final IExpr arg = timesAST.get(i);
      if (arg.isAST()) {
        argAST = (IAST) arg;
        if (trig && argAST.isAST1()) {
          IExpr numerForm = numeratorTrigForm(argAST, trig);
          if (numerForm.isPresent()) {
            IExpr denomForm = denominatorTrigForm(argAST, trig);
            if (denomForm.isPresent()) {
              if (!numerForm.isOne()) {
                numerator.append(numerForm);
              }
              if (!denomForm.isOne()) {
                denominator.append(denomForm);
              }
              evaled = true;
              continue;
            }
          }
        } else if (arg.isPower()) {
          Optional<IExpr[]> parts = fractionalPartsPower((IAST) arg, trig, splitPowerPlusExponents);
          if (parts.isPresent()) {
            IExpr[] elements = parts.get();
            if (!elements[0].isOne()) {
              numerator.append(elements[0]);
            }
            if (!elements[1].isOne()) {
              denominator.append(elements[1]);
            }
            evaled = true;
            continue;
          }
        }
      } else if (i == 1) {
        if (arg.isFraction()) {
          if (splitNumeratorOne) {
            IFraction fr = (IFraction) arg;
            if (fr.numerator().isOne()) {
              denominator.append(fr.denominator());
              splitFractionEvaled = true;
              continue;
            }
            if (fr.numerator().isMinusOne()) {
              numerator.append(fr.numerator());
              denominator.append(fr.denominator());
              splitFractionEvaled = true;
              continue;
            }
            result[2] = fr;
            continue;
          } else if (splitFractionalNumbers) {
            IFraction fr = (IFraction) arg;
            if (!fr.numerator().isOne()) {
              numerator.append(fr.numerator());
            }
            denominator.append(fr.denominator());
            evaled = true;
            continue;
          }
        } else if (arg.isComplex()) {
          IComplex cmp = (IComplex) arg;
          if (splitFractionalNumbers) {
            IRational re = cmp.getRealPart();
            IRational im = cmp.getImaginaryPart();
            if (re.isFraction() || im.isFraction()) {
              numerator.append(re.numerator().times(im.denominator())
                  .plus(im.numerator().times(re.denominator()).times(F.CI)));
              denominator.append(re.denominator().times(im.denominator()));
              evaled = true;
              continue;
            }
          }

          // if (cmp.re().isZero() && cmp.im().isFraction()) {
          // IFraction fr = (IFraction) cmp.im();
          // if (splitNumeratorOne) {
          // if (fr.numerator().isOne()) {
          // numerator.append(F.CI);
          // denominator.append(fr.denominator());
          // splitFractionEvaled = true;
          // continue;
          // }
          // if (fr.numerator().isMinusOne()) {
          // numerator.append(F.CNI);
          // denominator.append(fr.denominator());
          // splitFractionEvaled = true;
          // continue;
          // }
          // } else
          // if (splitFractionalNumbers) {
          // numerator.append(F.CC(F.C0, fr.numerator()));
          // denominator.append(fr.denominator());
          // evaled = true;
          // continue;
          // }
          // }
        }
      }
      numerator.append(arg);
    }
    if (evaled) {
      if (evalParts) {
        result[0] = F.eval(numerator);
        result[1] = F.eval(denominator);
      } else {
        result[0] = numerator.oneIdentity1();
        result[1] = denominator.oneIdentity1();
      }
      if (negateNumerDenom && result[0].isNumber() && result[0].isNegative() && result[1].isPlus()
          && result[1].isAST2()) {
        // negate numerator and denominator:
        result[0] = result[0].negate();
        result[1] = result[1].negate();
      }
      return Optional.of(result);
    }
    if (splitFractionEvaled) {
      result[0] = numerator.oneIdentity1();
      if (!result[0].isTimes() && !result[0].isPlus()) {
        result[1] = denominator.oneIdentity1();
        return Optional.of(result);
      }
      if (result[0].isTimes() && result[0].isAST2() && ((IAST) result[0]).arg1().isMinusOne()) {
        result[1] = denominator.oneIdentity1();
        return Optional.of(result);
      }
    }
    return Optional.empty();
  }

  /**
   * Polynomials of the form <code>x^(2*p) + x^p + 1</code> have exactly two factors for all primes
   * <code>p != 3</code>. One is <code>x^2 + x + 1</code>, and its cofactor is a polynomial whose
   * coefficients are all <code>1, 0, or −1</code>.
   *
   * @param poly
   * @param expr
   * @param variable
   * @param engine
   * @return
   */
  public static IExpr heuristicXP2XPOne(GenPolynomial<edu.jas.arith.BigInteger> poly, IAST expr,
      IExpr variable, EvalEngine engine) {
    if (poly.length() == 3 && poly.ring.tord == TermOrderByName.INVLEX && poly.ring.nvar == 1) {
      edu.jas.arith.BigInteger a = edu.jas.arith.BigInteger.ZERO;
      edu.jas.arith.BigInteger b = edu.jas.arith.BigInteger.ZERO;
      edu.jas.arith.BigInteger c = edu.jas.arith.BigInteger.ZERO;
      edu.jas.arith.BigInteger one = edu.jas.arith.BigInteger.ONE;
      long expA = 0;
      long p = 0;
      long expC = 0;
      int i = 0;
      for (Monomial<edu.jas.arith.BigInteger> monomial : poly) {
        final edu.jas.arith.BigInteger coeff = monomial.coefficient();
        long lExp = monomial.exponent().getVal(0);
        i++;
        if (i == 1) {
          a = coeff;
          expA = lExp;
        } else if (i == 2) {
          b = coeff;
          p = lExp;
        } else if (i == 3) {
          c = coeff;
          expC = lExp;
        }
      }
      if (a.equals(one) && b.equals(one) && c.equals(one)) {
        if (expC == 0L && (p != 3L) && (expA == p * 2)
            && java.math.BigInteger.valueOf(p).isProbablePrime(32)) {
          // polynomials of the form x^(2*p) + x^p + 1 have exactly two factors for
          // all primes p != 3. One is x^2 + x + 1, and its cofactor is a polynomial whose
          // coefficients are all 1, 0, or −1.
          IExpr x = variable;
          IExpr p1 = F.Plus(F.Power(x, F.C2), x, F.C1);
          IExpr p2 = engine.evaluate(F.PolynomialQuotient(expr, p1, x));
          return F.Times(p1, p2);
        }
      }
    }
    return F.NIL;
  }

  public static boolean isPolynomial(IExpr expr) {
    if (expr.isPlus() || expr.isTimes() || expr.isPower()) {
      IExpr expanded = F.evalExpand(expr);
      ExprPolynomialRing ring = new ExprPolynomialRing(F.CEmptyList);
      return ring.isPolynomial(expanded);
    }
    return expr.isPolynomial(F.CEmptyList);
  }

  /**
   * Split the {@link IAST} expression into numerator and denominator parts, by calling the
   * <code>Numerator(ast)</code> and <code>Denominator(ast)</code> functions and return the result
   * at index <code>0</code> (numerator) and index <code>1</code> (denominator).
   *
   * @param ast
   * @param together if <code>true</code> the evaluated <code>Together(ast)</code> result, will be
   *        appended at index <code>2</code> in the result array
   * @return an array with the numerator, denominator and the evaluated <code>Together(ast)</code>
   *         if requested.
   */
  public static IExpr[] numeratorDenominator(IAST ast, boolean together, EvalEngine engine) {
    if (together) {
      boolean noSimplifyMode = engine.isNoSimplifyMode();
      try {
        engine.setNoSimplifyMode(true);
        IExpr[] result = new IExpr[3];
        result[2] = together(ast, engine);
        // result[2] = engine.evaluate(F.Together(ast));
        return splitNumeratorDenominator(ast, result[2], result, engine);
      } finally {
        engine.setNoSimplifyMode(noSimplifyMode);
      }
    }

    IExpr[] result = new IExpr[2];
    return splitNumeratorDenominator(ast, ast, result, engine);
  }

  /**
   * Get the &quot;numerator form&quot; of the given function. Example: <code>Csc[x]</code> gives
   * <code>1</code>.
   *
   * @param function the function which should be transformed to &quot;denominator form&quot;
   *        determine the denominator by splitting up functions like <code>Tan[9,Cot[], Csc[],...
   *     </code>
   * @param trig
   */
  public static IExpr numeratorTrigForm(IAST function, boolean trig) {
    if (trig) {
      if (function.isAST1()) {
        for (int i = 0; i < F.DENOMINATOR_NUMERATOR_SYMBOLS.size(); i++) {
          final ISymbol symbol = F.DENOMINATOR_NUMERATOR_SYMBOLS.get(i);
          if (function.head().equals(symbol)) {
            final IExpr result = F.NUMERATOR_TRIG_TRUE_EXPRS.get(i);
            if (result.isSymbol()) {
              return F.unaryAST1(result, function.arg1());
            }
            return result;
          }
        }
      }
    }
    return F.NIL;
  }

  public static IExpr reduceFactorConstant(IExpr p, EvalEngine engine) {

    if (!engine.isNumericMode() && p.isPlus() && !engine.isTogetherMode()) {
      IAST plusAST = (IAST) p;
      // ((reduceConstantTerm /@ (List @@ plusAST)) // Transpose)[[1]]
      IExpr cTerms = S.Transpose
          .of(engine,
              F.Map(F.Function(F.unaryAST1(reduceConstantTerm, F.Slot1)), F.Apply(S.List, plusAST)))
          .first();
      if (cTerms.isList()) {
        // GCD @@ cTerms
        IExpr c = S.Apply.of(engine, S.GCD, cTerms);
        if (cTerms.first().isNegative()) {
          c = c.negate();
        }
        IExpr gcd;
        if (!c.isFree(IExpr::isInexactNumber, false)) {
          gcd = AbstractFractionSym.rationalize(c, false);
          // gcd = engine.evaluate(F.Rationalize(c));
          gcd = engine.evalN(gcd);
        } else {
          gcd = engine.evaluate(c);
        }
        if (gcd.isFree(S.GCD)) {
          return F.Times(gcd, S.Distribute.of(engine, F.Divide(plusAST, gcd)));
        }
      }
    }

    return p;
  }

  /**
   * Expand the given <code>ast</code> expression.
   *
   * @param ast
   * @param distributePlus
   * @return {@link F#NIL} if the expression couldn't be expanded.
   */
  // public static IExpr expandAll(final IAST ast, Predicate<IExpr> patt, boolean
  // expandNegativePowers,
  // boolean distributePlus, boolean factorTerms, EvalEngine engine) {
  // if (patt != null && ast.isFree(patt, true)) {
  // return F.NIL;
  // }
  // IAST localAST = ast;
  // IAST tempAST = F.NIL;
  // if (localAST.isEvalFlagOff(IAST.IS_SORTED)) {
  // tempAST = engine.evalFlatOrderlessAttrsRecursive(localAST);
  // if (tempAST.isPresent()) {
  // localAST = tempAST;
  // }
  // }
  // if (localAST.isAllExpanded() && expandNegativePowers && !distributePlus) {
  // if (localAST != ast) {
  // return localAST;
  // }
  // return F.NIL;
  // }
  // IASTAppendable[] result = new IASTAppendable[1];
  // result[0] = F.NIL;
  // IExpr temp = F.NIL;
  //
  // int localASTSize = localAST.size();
  // IExpr head = localAST.head();
  // if (head.isAST()) {
  // temp =
  // expandAll((IAST) head, patt, expandNegativePowers, distributePlus, factorTerms, engine);
  // temp.ifPresent(x -> result[0] = F.ast(x, localASTSize));
  // }
  // final IAST localASTFinal = localAST;
  // localAST.forEach((x, i) -> {
  // if (x.isAST()) {
  // IExpr t =
  // expandAll((IAST) x, patt, expandNegativePowers, distributePlus, factorTerms, engine);
  // if (t.isPresent()) {
  // if (result[0].isNIL()) {
  // int size = localASTFinal.size() + 4; // 4 -> empirically determined value in JUnit tests
  // if (t.isAST()) {
  // size += ((IAST) t).size();
  // }
  // result[0] = F.ast(head, size);
  // result[0].appendArgs(localASTFinal, i);
  // }
  // Algebra.appendPlus(result[0], t);
  // return;
  // }
  // }
  // result[0].ifAppendable(r -> r.append(x));
  // });
  //
  // if (result[0].isNIL()) {
  // temp = expand(localAST, patt, expandNegativePowers, distributePlus, true, factorTerms);
  // if (temp.isPresent()) {
  // setAllExpanded(temp, expandNegativePowers, distributePlus);
  // return temp;
  // } else {
  // if (localAST != ast) {
  // setAllExpanded(localAST, expandNegativePowers, distributePlus);
  // return localAST;
  // }
  // }
  // setAllExpanded(ast, expandNegativePowers, distributePlus);
  // return F.NIL;
  // }
  // temp = expand(result[0], patt, expandNegativePowers, distributePlus, true, factorTerms);
  // if (temp.isPresent()) {
  // return setAllExpanded(temp, expandNegativePowers, distributePlus);
  // }
  // return setAllExpanded(result[0], expandNegativePowers, distributePlus);
  // }

  public static IExpr setAllExpanded(IExpr expr, boolean expandNegativePowers,
      boolean distributePlus) {
    if (expr != null && expandNegativePowers && !distributePlus && expr.isAST()) {
      ((IAST) expr).addEvalFlags(IAST.IS_ALL_EXPANDED);
    }
    return expr;
  }

  /**
   * Split <code>rewrittenAST</code> into numerator and denominator.
   * 
   * @param originalAST the original {@link IAST} expression
   * @param rewrittenAST the rewritten AST (for example by {@link S#Together}
   * @param result the allocated result array
   * @param engine the evaluation engine
   * 
   * @return the <code>result</code> array of expressions <code>[numerator, denominator]</code>.
   */
  private static IExpr[] splitNumeratorDenominator(final IAST originalAST, final IExpr rewrittenAST,
      IExpr[] result, EvalEngine engine) {
    result[1] = engine.evaluate(F.Denominator(rewrittenAST));
    if (!result[1].isOne()) {
      result[0] = engine.evaluate(F.Numerator(rewrittenAST));
    } else {
      result[0] = originalAST;
    }
    return result;
  }

  private static void splitTimesArg1(IExpr expr, HashMap<IExpr, IInteger> map) {
    if (expr.isTimes()) {
      IAST timesAST = (IAST) expr;
      for (int i = 1; i < timesAST.size(); i++) {
        final IExpr arg = timesAST.get(i);
        if (arg.isPowerInteger()) {
          if (!arg.base().isNumber()) {
            map.put(arg.base(), (IInteger) arg.exponent());
          }
        } else {
          if (!arg.isNumber()) {
            map.put(arg, F.C1);
          }
        }
      }
    } else if (expr.isPowerInteger()) {
      if (!expr.base().isNumber()) {
        map.put(expr.base(), (IInteger) expr.exponent());
      }
    } else {
      if (!expr.isNumber()) {
        map.put(expr, F.C1);
      }
    }
  }

  private static boolean splitTimesRest(IExpr expr, HashMap<IExpr, IInteger> map) {
    if (map.size() > 0) {
      if (expr.isTimes()) {
        IAST timesAST = (IAST) expr;
        Iterator<Entry<IExpr, IInteger>> iter = map.entrySet().iterator();
        // for (Map.Entry<IExpr, IInteger> entry : map.entrySet()) {
        while (iter.hasNext()) {
          Map.Entry<IExpr, IInteger> entry = iter.next();
          final IExpr key = entry.getKey();
          boolean foundValue = false;
          for (int i = 1; i < timesAST.size(); i++) {
            final IExpr arg = timesAST.get(i);
            if (arg.isPowerInteger()) {
              if (arg.base().equals(key)) {
                IInteger value = entry.getValue();
                IInteger exponent = (IInteger) arg.exponent();
                if (value.equals(exponent.negate())) {
                  return false;
                }
                if (exponent.isNegative()) {
                  if (value.isLT(exponent)) {
                    entry.setValue(exponent);
                  }
                } else {
                  if (value.isGT(exponent)) {
                    entry.setValue(exponent);
                  }
                }
                foundValue = true;
                break;
              }
            } else {
              if (arg.equals(key)) {
                IInteger value = entry.getValue();
                if (value.isMinusOne()) {
                  return false;
                }
                if (value.isGT(F.C1)) {
                  entry.setValue(F.C1);
                }
                foundValue = true;
                break;
              }
            }
          }
          if (!foundValue) {
            iter.remove();
            if (map.size() == 0) {
              return false;
            }
          }
        }
      } else {
        Iterator<Entry<IExpr, IInteger>> iter = map.entrySet().iterator();
        // for (Map.Entry<IExpr, IInteger> entry : map.entrySet()) {
        while (iter.hasNext()) {
          Map.Entry<IExpr, IInteger> entry = iter.next();
          final IExpr key = entry.getKey();
          if (expr.isPowerInteger()) {
            if (!expr.base().equals(key)) {
              iter.remove();
              if (map.size() == 0) {
                return false;
              }
            } else {
              IInteger value = entry.getValue();
              IInteger exponent = (IInteger) expr.exponent();
              if (value.equals(exponent.negate())) {
                return false;
              }
              if (exponent.isNegative()) {
                if (value.isLT(exponent)) {
                  entry.setValue(exponent);
                }
              } else {
                if (value.isGT(exponent)) {
                  entry.setValue(exponent);
                }
              }
            }
          } else {
            if (!expr.equals(key)) {
              iter.remove();
              if (map.size() == 0) {
                return false;
              }
            } else {
              IInteger value = entry.getValue();
              if (value.isMinusOne()) {
                return false;
              }
              if (value.isGT(F.C1)) {
                entry.setValue(F.C1);
              }
            }
          }
        }
      }
    }
    return map.size() != 0;
  }

  public static IExpr together(IAST ast, EvalEngine engine) {
    IExpr result = togetherExpr(ast, engine);
    if (result.isPresent()) {
      return engine.evaluate(result);
    }
    return ast;
  }

  public static IExpr togetherExpr(final IExpr arg1, EvalEngine engine) {
    if (arg1.isPlusTimesPower()) {
      if (arg1.isPower()) {
        if (arg1.base().isAtom() && arg1.exponent().isAtom()) {
          return arg1;
        }
        if (!arg1.exponent().isMinusOne()) {
          if (arg1.base().isPlusTimesPower()) {
            if (arg1.exponent().isNegative()) {
              return F.Power(togetherExpr(arg1.base().inverse(), engine), arg1.exponent().negate());
            }
            return F.Power(togetherExpr(arg1.base(), engine), arg1.exponent());
          }
        }
      } else if (arg1.isTimes()) {
        if (arg1.first().isAtom()) {
          IExpr times = ((IAST) arg1).splice(1).oneIdentity0();
          if (times.isPower()) {
            return F.Times(arg1.first(), togetherExpr(times, engine));
          }
        }
        // } else if (arg1.isPlus()) {
        // IExpr[] result = InternalFindCommonFactorPlus.findCommonFactors((IAST) arg1, true);
        // if (result != null && !result[0].isOne()) {
        // IExpr temp = togetherNull((IAST) result[1], engine).orElse(result[1]);
        // if (temp.isPresent()) {
        // temp = engine.evaluate(F.Times(result[0], reduceFactorConstant(temp, engine)));
        // }
        // if (temp.isTimes() || temp.isPower()) {
        // return F.Cancel(temp);
        // }
        // return temp;
        // }
      }
      IExpr temp = togetherNull((IAST) arg1, engine).orElse(arg1);
      if (temp.isPresent()) {
        return reduceFactorConstant(temp, engine);
      }
    }
    return reduceFactorConstant(arg1, engine);
  }

  /**
   * Calls <code>Together</code> for each argument of the <code>ast</code>.
   *
   * @param ast
   * @return {@link F#NIL} if the <code>ast</code> couldn't be evaluated.
   */
  public static IASTMutable togetherForEach(final IAST ast, EvalEngine engine) {
    IASTMutable result = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      final IExpr arg = ast.get(i);
      if (arg.isAST()) {
        final IExpr temp = togetherNull((IAST) arg, engine);
        if (temp.isPresent()) {
          if (result.isNIL()) {
            result = ast.copy();
          }
          result.set(i, temp);
        }
      }
    }
    return result;
  }

  /**
   * Do a <code>ExpandAll(ast)</code> and call <code>togetherAST</code> afterwards with the result..
   *
   * @param ast
   * @return {@link F#NIL} couldn't be transformed by <code>ExpandAll(()</code> od <code>
   *     togetherAST()</code>
   */
  public static IExpr togetherNull(IAST ast, EvalEngine engine) {
    boolean evaled = false;
    IExpr temp = expandAll(ast, null, true, false, true, engine);
    if (temp.isNIL()) {
      temp = ast;
    } else {
      evaled = true;
    }
    if (temp.isAST()) {
      IExpr result = togetherPlusTimesPower((IAST) temp, engine);
      if (result.isPresent()) {
        return engine.evaluate(result);
      }
    }
    if (evaled) {
      return temp;
    }
    return F.NIL;
  }

  /**
   * Combine the terms of a <code>Plus[...]</code> expression into a single fractional expression,
   * if possible.
   * 
   * @param plusAST a <code>Plus[...]</code> expression
   * @return {@link F#NIL} if together couldn't be performed
   */
  public static IExpr togetherPlus(final IAST plusAST, EvalEngine engine) {
    if (plusAST.size() <= 2) {
      return F.NIL;
    }

    if (plusAST.isFree(
        x -> x.isInexactNumber()
            || (x.isAST() && (!x.isPlusTimesPower() || (x.isPower() && !x.exponent().isInteger()))),
        false)) {
      if (plusAST.isFree(x -> x.isFraction() || x.isPower() && x.exponent().isNegative(), false)) {
        return F.NIL;
      }
      VariablesSet eVar = new VariablesSet(plusAST);
      if (eVar.size() == 1) {
        try {
          IExpr variable = eVar.firstVariable();
          IExpr arg1Evaled = plusAST;
          IAST termList = ((IAST) arg1Evaled).setAtCopy(0, S.List);
          IASTAppendable numerators = F.ListAlloc(termList.argSize());
          IASTAppendable denominators = F.ListAlloc(termList.argSize());
          for (int i = 1; i < termList.size(); i++) {
            IExpr arg = termList.get(i);
            Optional<IExpr[]> fractionalParts = fractionalPartsRational(arg, false, false);
            if (fractionalParts.isPresent()) {
              IExpr[] parts = fractionalParts.get();
              numerators.append(parts[0]);
              denominators.append(parts[1]);
              continue;
            }
            numerators.append(arg);
            denominators.append(F.C1);
          }
          if (denominators.exists(a -> !a.isOne())) {
            IExpr commonDenominator = engine.evaluate(denominators.setAtCopy(0, S.PolynomialLCM));
            IASTAppendable sum = F.PlusAlloc(numerators.argSize());

            boolean evaled = true;
            for (int i = 1; i < numerators.size(); i++) {
              IExpr numer = numerators.get(i);
              IExpr denom = denominators.get(i);
              IExpr polynomialQuotient =
                  engine.evaluateNIL(F.PolynomialQuotient(commonDenominator, denom, variable));
              if (polynomialQuotient.isNIL()) {
                evaled = false;
                break;
              }
              sum.append(numer.times(polynomialQuotient));
            }
            if (evaled) {
              IExpr newNumerator = engine.evaluate(sum);
              IExpr expandedNumerator = F.evalExpand(newNumerator);
              IExpr gcd = engine.evaluate(F.PolynomialGCD(expandedNumerator, commonDenominator));
              IExpr finalNumerator =
                  engine.evaluateNIL(F.PolynomialQuotient(expandedNumerator, gcd, variable));
              if (finalNumerator.isPresent()) {
                IExpr finalDenominator =
                    engine.evaluate(F.PolynomialQuotient(commonDenominator, gcd, variable));
                IExpr factored = factor(F.Factor(finalDenominator), finalDenominator, eVar, false,
                    true, true, engine);
                if (finalNumerator.isNegative()) {
                  return F.Divide(finalNumerator.negate(), factored.negate());
                }
                return F.Divide(finalNumerator, factored);
              }
            }
          } else {
            return F.NIL;
          }
        } catch (RuntimeException rex) {
          if (Config.SHOW_STACKTRACE) {
            rex.printStackTrace();
          }
          Errors.rethrowsInterruptException(rex);
        }
      }
    }

    IASTAppendable numerator = F.ast(S.Plus, plusAST.size());
    IASTAppendable denominatorList = F.ast(S.Times, plusAST.size());
    boolean[] evaled = new boolean[1];
    plusAST.forEach((IExpr x, int i) -> togetherPlusArg(x, i, numerator, denominatorList, evaled));
    if (!evaled[0]) {
      return F.NIL;
    }
    numerator.forEach(
        (IExpr x, int i) -> togetherPlusNumeratorArg(x, i, numerator, denominatorList, plusAST));
    int i = 1;
    while (denominatorList.size() > i) {
      if (denominatorList.get(i).isOne()) {
        denominatorList.remove(i);
        continue;
      }
      i++;
    }
    if (denominatorList.isAST0()) {
      return F.NIL;
    }

    IExpr exprNumerator = F.evalExpand(numerator.oneIdentity0());
    IExpr denom = F.eval(denominatorList.oneIdentity1());
    final IExpr exprDenominator = F.evalExpand(denom);
    if (exprNumerator.isNumber()) {
      if (exprNumerator.isZero()) {
        if (exprDenominator.isZero()) {
          // let the standard evaluation handle the division by zero 0^0
          return F.Times(exprNumerator, F.Power(exprDenominator, F.CN1));
        }
        return F.C0;
      } else if (exprDenominator.isZero()) {
        return ArithmeticUtil.printInfy(S.Divide, exprNumerator, exprDenominator);
      }
    }
    if (!exprDenominator.isOne()) {
      try {
        Optional<IExpr[]> result = cancelGCD(exprNumerator, exprDenominator);
        if (result.isPresent()) {
          IExpr[] parts = result.get();
          IExpr pInv = parts[2].inverse();
          if (parts[0].isOne()) {
            return F.Times(pInv, parts[1]);
          }
          return F.Times(parts[0], parts[1], pInv);
        }
      } catch (JASConversionException jce) {
        if (Config.DEBUG) {
          jce.printStackTrace();
        }
      }
      if (exprDenominator.isNumber()) {
        return exprDenominator.inverse().times(exprNumerator);
      }
      return F.Times(exprNumerator, F.Power(denom, -1));
    }
    return exprNumerator;
  }

  public static void togetherPlusArg(IExpr x, int i, IASTAppendable numerator,
      IASTAppendable denominator, boolean[] evaled) {
    if (x.isFraction()) {
      numerator.append(i, ((IFraction) x).numerator());
      denominator.append(i, ((IFraction) x).denominator());
    } else if (x.isComplex()) {
      IRational re = ((IComplex) x).getRealPart();
      IRational im = ((IComplex) x).getImaginaryPart();
      if (re.isFraction() || im.isFraction()) {
        numerator.append(i, re.numerator().times(im.denominator())
            .plus(im.numerator().times(re.denominator()).times(F.CI)));
        denominator.append(i, re.denominator().times(im.denominator()));
      } else {
        numerator.append(i, x);
        denominator.append(i, F.C1);
      }
    } else {
      Optional<IExpr[]> fractionalParts = fractionalParts(x, false);
      if (fractionalParts.isPresent()) {
        IExpr numer = fractionalParts.get()[0];
        IExpr denom = fractionalParts.get()[1];
        numerator.append(i, numer);
        if (!denom.isOne()) {
          evaled[0] = true;
        }
        denominator.append(i, denom);
      } else {
        numerator.append(i, x);
        denominator.append(i, F.C1);
      }
    }
  }

  public static void togetherPlusNumeratorArg(IExpr xarg, int position, IASTAppendable numerator,
      IASTAppendable denominator, IAST plusAST) {
    IASTAppendable ni = F.TimesAlloc(plusAST.argSize());
    ni.append(xarg);
    for (int j = 1; j < plusAST.size(); j++) {
      if (position == j) {
        continue;
      }
      final IExpr arg = denominator.get(j);
      if (!arg.isOne()) {
        ni.append(arg);
      }
    }
    numerator.set(position, ni.oneIdentity1());
  }

  public static IExpr togetherPlusTimesPower(final IAST ast, EvalEngine engine) {
    if (ast.isPlus()) {
      IAST result = togetherForEach(ast, engine);
      if (result.isPresent()) {
        return togetherPlus(result, engine).orElse(result);
      }
      return togetherPlus(ast, engine);
    } else if (ast.isTimes() || ast.isPower()) {
      try {
        IASTMutable result = F.NIL;
        if (ast.isTimes()) {
          result = togetherForEach(ast, engine);
        } else {
          // Power
          result = togetherPower(ast, result, engine);
        }
        if (result.isPresent()) {
          IExpr temp = engine.evaluate(result);
          if (temp.isTimes() || temp.isPower()) {
            return cancelFractionalParts(temp).orElse(temp);
          }
          return temp;
        }
        return cancelFractionalParts(ast);
      } catch (JASConversionException jce) {
        // could not convert to polynomial
        if (Config.DEBUG) {
          jce.printStackTrace();
        }
      }
    }
    return F.NIL;
  }

  public static IASTMutable togetherPower(final IAST ast, IASTMutable result, EvalEngine engine) {
    if (ast.arg1().isAST()) {
      IExpr temp = togetherNull((IAST) ast.arg1(), engine);
      if (temp.isPresent()) {
        if (result.isNIL()) {
          result = ast.copy();
        }
        if (ast.arg2().isNegative() && temp.isTimes()) {
          Optional<IExpr[]> fractionalParts = fractionalPartsRational(temp, false, true);
          if (fractionalParts.isPresent()) {
            IExpr[] parts = fractionalParts.get();
            result.set(1, F.Divide(parts[1], parts[0]));
            result.set(2, ast.arg2().negate());
          } else {
            result.set(1, temp);
          }
        } else {
          result.set(1, temp);
        }
      }
    }
    return result;
  }

  private AlgebraUtil() {
    // private constructor to avoid instantiation
  }

}
