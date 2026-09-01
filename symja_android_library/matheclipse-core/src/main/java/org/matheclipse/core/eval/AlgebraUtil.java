package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Predicate;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTSeriesData;
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
import org.matheclipse.core.polynomials.IPartialFractionGenerator;
import org.matheclipse.core.polynomials.PartialFractionGenerator;
import org.matheclipse.core.polynomials.PolynomialHomogenization;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import com.google.common.math.LongMath;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.poly.TermOrderByName;
import edu.jas.ps.PolynomialTaylorFunction;
import edu.jas.ps.TaylorFunction;
import edu.jas.ps.UnivPowerSeries;
import edu.jas.ps.UnivPowerSeriesRing;
import edu.jas.structure.RingElem;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorComplex;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;

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
    /**
     * ASTs which are expanded in this Expander are cached in a set. The expansion is only cached in
     * the set if the AST doesn't get the IAST.IS_EXPANDED flag. * Optimized: initialized lazily to
     * prevent massive memory allocations during deep tree traversals.
     */
    Set<IAST> expandedASTs = null;

    final boolean expandNegativePowers;
    final boolean distributePlus;
    final boolean evalParts;
    final boolean factorTerms;

    /** Pattern matcher which may be F.NIL if undefined */
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
          // Lazy initialization to save memory for nodes that don't require map caching
          if (expandedASTs == null) {
            expandedASTs = Collections.newSetFromMap(new IdentityHashMap<>());
          }
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
     * @return F.NIL if no evaluation is possible
     */
    public IExpr expandAST(final IAST ast) {
      if (isPatternFree(ast)) {
        return F.NIL;
      }
      if (ast.isExpanded() && expandNegativePowers && !distributePlus) {
        return F.NIL;
      }
      // Check the lazily initialized map safely
      if (expandedASTs != null && expandedASTs.contains(ast)) {
        return F.NIL;
      }

      if (ast.isPower()) {
        return expandPowerNIL(ast);
      } else if (ast.isTimes()) {
        // (a+b)*(c+d)...
        EvalEngine engine = EvalEngine.get();

        Optional<IExpr[]> temp = fractionalPartsTimesPower(ast, false,
            expandNegativePowers && distributePlus, false, evalParts, true, true);
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
        } else if (parts[0].isPower() || parts[0].isPlus()) {
          // for example the numerator (x+y)^2 in ((x+y)/z)^2
          tempExpr = expandAST((IAST) parts[0]);
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
        IExpr powerAST = F.Power(parts[1], F.CN1);
        if (distributePlus && parts[0].isPlus()) {
          IAST mappedAST =
              ((IAST) parts[0]).mapThreadEvaled(EvalEngine.get(), F.Times(null, powerAST), 1);
          IExpr flattened = flattenOneIdentity(mappedAST, F.C0);
          return addExpanded(flattened);
        }
        if (evaled) {
          return addExpanded(binaryFlatTimes(parts[0], powerAST));
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
        PlusOp plusOp = new PlusOp(plusAST1.argSize());
        final IExpr t =
            plusAST0.isPlusTimesPower() ? expandAST(plusAST0).orElse(plusAST0) : plusAST0;
        plusAST1.forEach(x -> evalAndExpandAST(t, false, x, true, plusOp, engine));
        return plusOp.getSum();
      } else if (isPatternFree(plusAST1)) {
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

  private static void addSquareFreeFactor(Map<IExpr, Long> factorMap, IExpr factor, long exponent) {
    factorMap.merge(factor, exponent, Long::sum);
  }

  private static boolean appendPlus(IASTAppendable ast, IExpr expr) {
    if (ast.head() == S.Plus && expr.head() == S.Plus) {
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

      // Optimized: Avoid IASTAppendable.remove() which causes O(N^2) array shifting overhead.
      // Use local arrays to track remaining terms.
      int p0ArgSize = p0Times.argSize();
      int p1ArgSize = p1Times.argSize();

      IExpr[] t0Args = new IExpr[p0ArgSize + 1];
      for (int i = 1; i <= p0ArgSize; i++) {
        t0Args[i] = p0Times.get(i);
      }

      IExpr[] t1Args = new IExpr[p1ArgSize + 1];
      for (int i = 1; i <= p1ArgSize; i++) {
        t1Args[i] = p1Times.get(i);
      }

      boolean evaled = false;
      IASTAppendable commonFactors = F.NIL;

      for (int i = 1; i <= p0ArgSize; i++) {
        if (t0Args[i] == null)
          continue;

        for (int j = 1; j <= p1ArgSize; j++) {
          if (t1Args[j] == null)
            continue;

          if (t0Args[i].equals(t1Args[j])) {
            if (Config.TRACE_BASIC_ARITHMETIC && EvalEngine.get().isTraceMode()) {
              if (commonFactors.isNIL()) {
                commonFactors = F.TimesAlloc(p0ArgSize + 1);
              }
              commonFactors.append(t0Args[i]);
            }

            t0Args[i] = null;
            t1Args[j] = null;
            evaled = true;
            break; // completely cancelled, move to next numerator term
          } else if (t0Args[i].isPower() || t1Args[j].isPower()) {
            final IExpr t0Base = t0Args[i].isPower() ? t0Args[i].base() : t0Args[i];
            final IExpr t0Exponent = t0Args[i].isPower() ? t0Args[i].exponent() : F.C1;

            final IExpr t1Base = t1Args[j].isPower() ? t1Args[j].base() : t1Args[j];
            final IExpr t1Exponent = t1Args[j].isPower() ? t1Args[j].exponent() : F.C1;

            if (t0Exponent.isReal() && t1Exponent.isReal() && t0Base.equals(t1Base)) {
              IReal exp0 = (IReal) t0Exponent;
              IReal exp1 = (IReal) t1Exponent;

              if (exp0.isGE(exp1)) {
                IReal subtracted = exp0.subtractFrom(exp1);
                if (subtracted.isZero()) {
                  t0Args[i] = null;
                } else {
                  t0Args[i] = F.Power(t0Base, subtracted);
                }
                t1Args[j] = null;

                if (Config.TRACE_BASIC_ARITHMETIC && EvalEngine.get().isTraceMode()) {
                  if (commonFactors.isNIL()) {
                    commonFactors = F.TimesAlloc(p0ArgSize + 1);
                  }
                  commonFactors.append(F.Power(t0Base, exp1));
                }
              } else {
                IReal subtracted = exp1.subtractFrom(exp0);
                t0Args[i] = null;
                t1Args[j] = F.Power(t1Base, subtracted);

                if (Config.TRACE_BASIC_ARITHMETIC && EvalEngine.get().isTraceMode()) {
                  if (commonFactors.isNIL()) {
                    commonFactors = F.TimesAlloc(p0ArgSize + 1);
                  }
                  commonFactors.append(F.Power(t0Base, exp0));
                }
              }
              evaled = true;
              if (t0Args[i] == null) {
                break; // move to next numerator term if this one is fully cancelled
              }
            }
          }
        }
      }

      if (evaled) {
        // Collect surviving factors into appropriately sized buffers
        IASTAppendable t0Final = F.TimesAlloc(p0ArgSize + 1);
        for (int i = 1; i <= p0ArgSize; i++) {
          if (t0Args[i] != null)
            t0Final.append(t0Args[i]);
        }

        IASTAppendable t1Final = F.TimesAlloc(p1ArgSize + 1);
        for (int j = 1; j <= p1ArgSize; j++) {
          if (t1Args[j] != null)
            t1Final.append(t1Args[j]);
        }

        IExpr p0Result = t0Final.oneIdentity1();
        IExpr p1Result = t1Final.oneIdentity1();

        if (Config.TRACE_BASIC_ARITHMETIC && EvalEngine.get().isTraceMode()) {
          if (!numer.equals(numerator)) {
            EvalEngine.get().addTraceStep(F.Divide(numerator, denominator),
                F.Divide(numer, denominator), F.List(S.Cancel, F.$str("Factor"), numerator, numer));
          }
          if (!denom.equals(denominator)) {
            EvalEngine.get().addTraceStep(F.Divide(numer, denominator), F.Divide(numer, denom),
                F.List(S.Cancel, F.$str("Factor"), denominator, denom));
          }
          EvalEngine.get().addTraceStep(F.Divide(numer, denom), F.Divide(p0Result, p1Result),
              F.List(S.Cancel, F.$str("CancelCommonFactors"), commonFactors));
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
      } else if (numerator.isPlus() && (denominator.isTimes() || denominator.isPower())) {
        // Cancel a common polynomial factor between a Plus numerator and a Times/Power
        // denominator, e.g. Together(x*(1/x + 1/y)) -> (x^2 + x*y)/(x*y) -> (x + y)/y.
        IAST numParts = numerator.partitionPlus(x -> isPolynomial(x), F.C0, F.C1, S.List);
        if (numParts.arg1().isPlus()) {
          Optional<IExpr[]> result = cancelGCD(numParts.arg1(), denominator);
          if (result.isPresent()) {
            IExpr[] elements = result.get();
            return F.Times(elements[0], elements[1], numParts.arg2(), F.Power(elements[2], F.CN1));
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
   * @return {@link Optional#empty()} if the expressions couldn't be converted to JAS polynomials,
   *         gcd equals 1 or an argument is larger than {@link Config#MAX_CANCEL_GCD_LEAFCOUNT}
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

      // The polynomial GCD below can suffer unbounded intermediate coefficient swell and cannot be
      // interrupted once it is running, so it is only attempted for arguments of a tractable size.
      if (numerator.leafCount() > Config.MAX_CANCEL_GCD_LEAFCOUNT
          || denominator.leafCount() > Config.MAX_CANCEL_GCD_LEAFCOUNT) {
        return Optional.empty();
      }

      VariablesSet eVar = new VariablesSet(numerator);
      eVar.addVarList(denominator);
      if (eVar.size() == 0) {
        return Optional.empty();
      }

      IAST vars = eVar.getVarList();
      PolynomialHomogenization substitutions = new PolynomialHomogenization(EvalEngine.get());
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
      } catch (AssertionError ae) {
        // JAS asserts that the dividend of GenPolynomial.divide is in descending leading-exponent
        // order and throws on some multivariate symbolic-coefficient polynomials (e.g. cancelling a
        // Together sub-expression of 1/(1+x+x^5)). AssertionError is an Error, so the catch above
        // does not cover it and it would abort the whole evaluation. Fall through to the
        // Complex<BigRational> GCD path below, which rebuilds the polynomials in a different
        // representation.
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
    } catch (AssertionError ae) {
      // see above: a JAS ordering assertion in the Complex path too - give up on cancelling and
      // let the caller keep the fraction un-cancelled rather than aborting the evaluation
      if (Config.DEBUG) {
        ae.printStackTrace();
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
   * Recursively walk the multiplicative structure of <code>expr</code> and collect its square-free
   * polynomial factors into <code>factorMap</code> (factor to exponent) and the numeric content
   * into <code>content</code>.
   *
   * @param multiplier the product of all enclosing integer exponents
   * @return <code>false</code> if a fatal conversion problem occurred; otherwise <code>true</code>
   */
  private static boolean collectSquareFreeFactors(IExpr expr, long multiplier, VariablesSet eVar,
      Map<IExpr, Long> factorMap, IRational[] content, boolean[] anyPolynomial, EvalEngine engine) {
    if (expr.isTimes()) {
      boolean result = true;
      IAST times = (IAST) expr;
      for (int i = 1; i < times.size(); i++) {
        result &= collectSquareFreeFactors(times.get(i), multiplier, eVar, factorMap, content,
            anyPolynomial, engine);
      }
      return result;
    }
    if (expr.isPower()) {
      int exponent = expr.exponent().toIntDefault();
      if (exponent > 0) {
        return collectSquareFreeFactors(expr.base(), multiplier * exponent, eVar, factorMap,
            content, anyPolynomial, engine);
      }
      // a non-positive-integer exponent (e.g. a denominator or a root): keep as an opaque factor
      addSquareFreeFactor(factorMap, expr, multiplier);
      return true;
    }
    if (expr.isRational()) {
      content[0] = content[0].multiply(((IRational) expr).powerRational(multiplier));
      return true;
    }
    if (expr.isPlus()) {
      return squareFreeLeaf((IAST) expr, multiplier, eVar, factorMap, content, anyPolynomial,
          engine);
    }
    // a symbol or a non-polynomial sub-expression like Sin(x): treat it as an opaque factor
    addSquareFreeFactor(factorMap, expr, multiplier);
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
   * Builds a holonomic-sequence {@code DifferenceRoot} object with the recurrence and initial
   * conditions embedded as equations:
   *
   * <pre>
   * DifferenceRoot(Function({y, k}, {recurrence, y(0) == v0, y(1) == v1, ...}))[n]
   * </pre>
   *
   * <p>
   * This is the shared representation used by both {@code RSolve} and {@code SeriesCoefficient} so
   * that both functions emit identical objects for non-closed-form linear recurrences.
   *
   * @param recurrence the recurrence equation expressed in terms of {@code y} and {@code k}
   * @param y the sequence function symbol used in {@code recurrence}
   * @param k the index symbol used in {@code recurrence}
   * @param initialConditions the values {@code v0, v1, ...} for {@code y(0), y(1), ...}
   * @param n the index at which the sequence is evaluated
   * @return the {@code DifferenceRoot(...)[n]} expression
   */
  public static IExpr differenceRoot(IExpr recurrence, ISymbol y, ISymbol k,
      List<IExpr> initialConditions, IExpr n) {
    IASTAppendable equations = F.ListAlloc(initialConditions.size() + 1);
    equations.append(recurrence);
    for (int i = 0; i < initialConditions.size(); i++) {
      equations.append(F.Equal(F.unaryAST1(y, F.ZZ(i)), initialConditions.get(i)));
    }
    IAST diffRootFunc = F.Function(F.List(y, k), equations);
    return F.unaryAST1(F.DifferenceRoot(diffRootFunc), n);
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

  private static IExpr distributeLaurentDenominator(IExpr numeratorFact, IExpr denominatorFact,
      Set<ISymbol> varSet, EvalEngine engine) {
    IASTAppendable newFactors = F.TimesAlloc(numeratorFact.isTimes() ? numeratorFact.argSize() : 2);
    Map<ISymbol, IExpr> totalPowers = new HashMap<>();
    for (ISymbol v : varSet) {
      totalPowers.put(v, F.C0);
    }

    if (numeratorFact.isTimes()) {
      for (int i = 1; i <= numeratorFact.argSize(); i++) {
        IExpr factor = numeratorFact.get(i);
        IExpr base = factor.isPower() ? factor.base() : factor;
        IExpr exp = factor.isPower() ? factor.exponent() : F.C1;

        IExpr newBase = base;
        for (ISymbol v : varSet) {
          IExpr degree = engine.evaluate(F.Exponent(newBase, v));
          if (degree.isInteger() && !degree.isZero()) {
            IExpr halfDegree = engine.evaluate(F.Times(degree, F.C1D2));
            newBase = engine.evaluate(F.Expand(F.Times(newBase, F.Power(v, halfDegree.negate()))));
            totalPowers.put(v,
                engine.evaluate(F.Plus(totalPowers.get(v), F.Times(halfDegree, exp))));
          }
        }
        newFactors.append(engine.evaluate(F.Power(newBase, exp)));
      }
    } else {
      IExpr factor = numeratorFact;
      IExpr base = factor.isPower() ? factor.base() : factor;
      IExpr exp = factor.isPower() ? factor.exponent() : F.C1;

      IExpr newBase = base;
      for (ISymbol v : varSet) {
        IExpr degree = engine.evaluate(F.Exponent(newBase, v));
        if (degree.isInteger() && !degree.isZero()) {
          IExpr halfDegree = engine.evaluate(F.Times(degree, F.C1D2));
          newBase = engine.evaluate(F.Expand(F.Times(newBase, F.Power(v, halfDegree.negate()))));
          totalPowers.put(v, engine.evaluate(F.Plus(totalPowers.get(v), F.Times(halfDegree, exp))));
        }
      }
      newFactors.append(engine.evaluate(F.Power(newBase, exp)));
    }

    IExpr newDenominator = denominatorFact;
    for (ISymbol v : varSet) {
      IExpr tp = totalPowers.get(v);
      if (!tp.isZero()) {
        newDenominator =
            engine.evaluate(F.Expand(F.Times(newDenominator, F.Power(v, tp.negate()))));
      }
    }
    return engine.evaluate(F.Divide(newFactors.oneIdentity1(), newDenominator));
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

  /**
   * Helper to extract coefficient, base, and exponent from a fractional power term. Returns
   * [coefficient, base, exponent] or null.
   */
  private static IExpr[] extractFractionalPower(IExpr expr) {
    if (expr.isPower() && expr.exponent().isFraction()) {
      return new IExpr[] {F.C1, expr.base(), expr.exponent()};
    }
    if (expr.isTimes()) {
      IAST times = (IAST) expr;
      int powerIndex = times.indexOf(x -> x.isPower() && x.exponent().isFraction());
      if (powerIndex > 0) {
        IExpr power = times.get(powerIndex);
        IExpr coeff = times.removeAtCopy(powerIndex).oneIdentity1();
        return new IExpr[] {coeff, power.base(), power.exponent()};
      }
    }
    return null;
  }

  public static IExpr factor(IAST ast, IExpr arg1, VariablesSet eVar, boolean squareFree,
      boolean withHomogenization, boolean togetherMode, boolean trig, EvalEngine engine) {
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
                squareFree, withHomogenization, trig, engine);
            IExpr denominator = factorExpr(F.Factor(fractionParts[1]), fractionParts[1], eVar,
                squareFree, withHomogenization, trig, engine);
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
      IExpr temp = factorExpr(ast, expr, eVar, squareFree, withHomogenization, trig, engine);
      engine.putCache(ast, temp);
      if (temp.isPresent()) {
        return temp;
      }
    } catch (JASConversionException e) {
    }
    return arg1;
  }

  public static IExpr factor(IAST expr, VariablesSet eVar, boolean squareFree,
      boolean withHomogenization, boolean trig, EvalEngine engine) throws JASConversionException {
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
          return factorWithPolynomialHomogenization(expr, eVar, trig, engine);
        }
        return F.NIL;
      }
      if (polyRat.length() <= 1) {
        return expr;
      }
      objects = jas.factorTerms(polyRat);
    } catch (JASConversionException e) {
      if (!squareFree && withHomogenization) {
        return factorWithPolynomialHomogenization(expr, eVar, trig, engine);
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

  public static IExpr factor(IExpr arg1, EvalEngine engine) {
    VariablesSet eVar = new VariablesSet(arg1);
    return factor(F.Factor(arg1), arg1, eVar, false, false, true, false, engine);
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
        return AlgebraUtil.factorRational(polyRat, jas, head);
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
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
      boolean withHomogenization, boolean trig, EvalEngine engine) {
    if (expr.isAST()) {
      IExpr temp;
      // if (expr.isPower()&&expr.base().isPlus()) {
      // temp = factorExpr(ast, expr.base(), varList);
      // temp = F.Power(temp, expr.exponent());
      // } else
      if (expr.isPower()) {
        IExpr p = factorExpr((IAST) expr, expr.base(), eVar, squareFree, withHomogenization, trig,
            engine);
        if (p.isPresent() && !p.equals(expr.base())) {
          return F.Power(p, expr.exponent());
        }
        return expr;
      } else if (expr.isTimes()) {
        temp = ((IAST) expr).map(x -> {
          if (x.isPlus()) {
            return factorExpr(ast, x, eVar, squareFree, withHomogenization, trig, engine);
          }
          if (x.isPower() && x.base().isPlus()) {
            IExpr p = factorExpr(ast, x.base(), eVar, squareFree, withHomogenization, trig, engine);
            if (p.isPresent() && !p.equals(x.base())) {
              return F.Power(p, x.exponent());
            }
          }
          return F.NIL;
        }, 1);
        return temp;
      } else {
        return factor((IAST) expr, eVar, squareFree, withHomogenization, trig, engine);
      }
    }
    return expr;
  }

  public static IAST factorModulus(IExpr expr, IAST varList, boolean factorSquareFree,
      IExpr option) throws JASConversionException {
    try {
      // found "Modulus" option => use ModIntegerRing
      ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((IReal) option);
      JASModInteger jas = new JASModInteger(varList, modIntegerRing);
      GenPolynomial<ModLong> poly = jas.expr2JAS(expr);
  
      return AlgebraUtil.factorModulus(jas, modIntegerRing, poly, factorSquareFree);
    } catch (ArithmeticException ae) {
      // toInt() conversion failed
      // LOGGER.debug("Algebra.factorModulus() failed", ae);
    }
    return F.NIL;
  }

  /**
   * @param jas
   * @param modIntegerRing
   * @param poly
   * @param factorSquareFree
   * @return {@link F#NIL} if evaluation is impossible.
   */
  public static IAST factorModulus(JASModInteger jas, ModLongRing modIntegerRing,
      GenPolynomial<ModLong> poly, boolean factorSquareFree) {
    SortedMap<GenPolynomial<ModLong>, Long> map;
    try {
      FactorAbstract<ModLong> factorAbstract = FactorFactory.getImplementation(modIntegerRing);
      if (factorSquareFree) {
        map = factorAbstract.squarefreeFactors(poly);
      } else {
        map = factorAbstract.factors(poly);
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      // JAS may throw RuntimeExceptions
      return F.NIL;
    }
    IASTAppendable result = F.TimesAlloc(map.size());
    for (SortedMap.Entry<GenPolynomial<ModLong>, Long> entry : map.entrySet()) {
      final GenPolynomial<ModLong> singleFactor = entry.getKey();
      final Long val = entry.getValue();
      result.append(F.Power(jas.modLongPoly2Expr(singleFactor), F.ZZ(val)));
    }
    return result;
  }


  public static IAST factorRational(GenPolynomial<BigRational> polyRat, JASConvert<BigRational> jas,
      ISymbol head) {
    if (polyRat.degree() > Config.MAX_POLYNOMIAL_DEGREE) {
      // Exponent ist out of bounds for function `1`.
      return Errors.printMessage(S.Factor, "lrgexp", F.List(S.Factor));
    }
    Object[] objects = jas.factorTerms(polyRat);
    GenPolynomial<edu.jas.arith.BigInteger> poly =
        (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
    FactorAbstract<edu.jas.arith.BigInteger> factorAbstract =
        FactorFactory.getImplementation(edu.jas.arith.BigInteger.ONE);
    SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
    map = factorAbstract.factors(poly);
    // if (map.size() == 1 && original != null) {
    // return F.unaryAST1(head, original);
    // }
    IASTAppendable result = F.ast(head, map.size() + 1);
    java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
    java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
    if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
      result.append(F.fraction(gcd, lcm));
    }
    for (SortedMap.Entry<GenPolynomial<edu.jas.arith.BigInteger>, Long> entry : map.entrySet()) {
      final GenPolynomial<BigInteger> key = entry.getKey();
      final Long value = entry.getValue();
      if (key.isONE() && value.equals(1L)) {
        continue;
      }
      if (value == 1L) {
        result.append(jas.integerPoly2Expr(key));
      } else {
        result.append(F.Power(jas.integerPoly2Expr(key), F.ZZ(value)));
      }
    }
    return result;
  }

  /**
   * Square-free factorization for the <code>FactorSquareFree</code> built-in. In contrast to
   * {@link #factor(IAST, VariablesSet, boolean, boolean, boolean, EvalEngine)} this method
   * preserves the multiplicative structure of the input (it does not expand a product of coprime
   * factors), normalizes every square-free polynomial factor to a positive leading coefficient and
   * collects the overall numeric content and sign into a single leading factor.
   *
   * <p>
   * The result is a regular (evaluated) expression. Symja normalizes an even power of a polynomial
   * with a negative constant term to a positive constant term (e.g. <code>(-1+x)^2</code> becomes
   * <code>(1-x)^2</code>); that canonical form is kept so the result stays usable by other
   * functions.
   *
   * @param arg1 the (already evaluated) argument of <code>FactorSquareFree</code>
   * @param eVar the variables contained in <code>arg1</code>
   * @param engine the evaluation engine
   * @return the factored expression or {@link F#NIL} if nothing polynomial could be factored
   */
  public static IExpr factorSquareFree(IExpr arg1, VariablesSet eVar, EvalEngine engine) {
    LinkedHashMap<IExpr, Long> factorMap = new LinkedHashMap<IExpr, Long>();
    IRational[] content = new IRational[] {F.C1};
    boolean[] anyPolynomial = new boolean[] {false};
    if (!collectSquareFreeFactors(arg1, 1L, eVar, factorMap, content, anyPolynomial, engine)
        || !anyPolynomial[0]) {
      return F.NIL;
    }

    IASTAppendable times = F.TimesAlloc(factorMap.size() + 1);
    if (!content[0].isOne()) {
      times.append(content[0]);
    }
    for (Map.Entry<IExpr, Long> entry : factorMap.entrySet()) {
      long exponent = entry.getValue();
      times.append(exponent == 1L ? entry.getKey() : F.Power(entry.getKey(), F.ZZ(exponent)));
    }
    return engine.evaluate(times);
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

  public static IExpr factorWithPolynomialHomogenization(IAST expr, VariablesSet eVar, boolean trig,
      EvalEngine engine) {
    boolean originalHasComplex = !expr.isFree(x -> x.isComplex() || x.isComplexNumeric(), false);
    PolynomialHomogenization substitutions = new PolynomialHomogenization(engine, trig);
    IExpr subsPolynomial = substitutions.replaceForward(expr);

    // Update gaussianIntegers based on the homogenized polynomial to support TrigToExp complex
    // numbers
    boolean gaussianIntegers = originalHasComplex;
    if (!gaussianIntegers) {
      gaussianIntegers = !subsPolynomial.isFree(x -> x.isComplex() || x.isComplexNumeric(), false);
    }

    IExpr factorization = F.NIL;

    if (subsPolynomial.isAST()) {
      Set<ISymbol> varSet = substitutions.substitutedVariablesSet();
      eVar.addAll(varSet);

      // Support Laurent polynomials (negative exponents) by converting them to a rational
      // expression first
      IExpr[] fractionParts = numeratorDenominator((IAST) subsPolynomial, true, engine);

      if (!fractionParts[1].isOne()) {
        IExpr numeratorFact =
            factorComplex(fractionParts[0], eVar.getVarList(), S.Times, gaussianIntegers, engine);
        if (numeratorFact.isPresent()) {
          IExpr denominatorFact =
              factorComplex(fractionParts[1], eVar.getVarList(), S.Times, gaussianIntegers, engine);
          IExpr denom = denominatorFact.isPresent() ? denominatorFact : fractionParts[1];

          if (trig && !varSet.isEmpty()) {
            factorization = distributeLaurentDenominator(numeratorFact, denom, varSet, engine);
          } else {
            factorization = engine.evaluate(F.Divide(numeratorFact, denom));
          }
        }
      } else {
        IExpr numeratorFact =
            factorComplex(fractionParts[0], eVar.getVarList(), S.Times, gaussianIntegers, engine);
        if (trig && !varSet.isEmpty() && numeratorFact.isPresent()) {
          factorization = distributeLaurentDenominator(numeratorFact, F.C1, varSet, engine);
        } else {
          factorization = numeratorFact.isPresent() ? numeratorFact : fractionParts[0];
        }
      }
    } else {
      factorization =
          factorComplex(subsPolynomial, eVar.getVarList(), S.Times, gaussianIntegers, engine);
    }

    if (factorization.isPresent()) {
      IExpr result = substitutions.replaceBackward(factorization);
      // Safety net: avoid generating complex numbers in the final result if they weren't present
      // originally
      // if (!originalHasComplex
      // && !result.isFree(x -> x.isComplex() || x.isComplexNumeric(), false)) {
      // return expr;
      // }
      return result;
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
   * Generates the optimal closed-form expression for the linear recurrence y_n = a * y_{n-1} - b *
   * y_{n-2} with initial conditions y_0 = 0, y_1 = 1. (Note: For generating functions 1/(1 - ax +
   * bx^2), pass n = n + 1).
   *
   * <p>
   * By default the canonical sequence identities (Fibonacci, ChebyshevU) are used when they match.
   */
  public static IExpr generalizedBinet(IExpr a, IExpr b, IExpr n, EvalEngine engine) {
    return generalizedBinet(a, b, n, engine, true);
  }

  /**
   * Generates the optimal closed-form expression for the linear recurrence y_n = a * y_{n-1} - b *
   * y_{n-2} with initial conditions y_0 = 0, y_1 = 1. (Note: For generating functions 1/(1 - ax +
   * bx^2), pass n = n + 1).
   *
   * @param useCanonicalForms if {@code true} the canonical sequence identities (Fibonacci,
   *        ChebyshevU) are returned when they match; if {@code false} the explicit universal Binet
   *        formula is always returned (used by {@code RSolve}, which canonicalizes special
   *        sequences in a later pass).
   */
  public static IExpr generalizedBinet(IExpr a, IExpr b, IExpr n, EvalEngine engine,
      boolean useCanonicalForms) {
    if (a == null || b == null || n == null) {
      return F.NIL;
    }

    // 1. Canonical Sequence Identities
    if (useCanonicalForms) {
      if (b.equals(F.CN1) && a.isOne()) {
        return F.Fibonacci(n);
      }
      if (b.isOne()) {
        IExpr t = engine.evaluate(F.Times(a, F.C1D2));
        return F.ChebyshevU(F.Subtract(n, F.C1), t);
      }
    }

    // 2. Universal Binet Formula for Generic Roots
    IExpr delta = engine.evaluate(F.Sqrt(F.Subtract(F.Sqr(a), F.Times(F.C4, b))));

    // r1 = a + delta, r2 = a - delta
    IExpr r1 = engine.evaluate(F.Plus(a, delta));
    IExpr r2 = engine.evaluate(F.Subtract(a, delta));

    // (r1^n - r2^n) / (2^n * delta)
    IExpr term1 = engine.evaluate(F.Power(r1, n));
    IExpr term2 = engine.evaluate(F.Power(r2, n));

    IExpr numBinet = engine.evaluate(F.Subtract(term1, term2));
    IExpr denBinet = engine.evaluate(F.Times(F.Power(F.C2, n), delta));

    return engine.evaluate(F.Together(F.Divide(numBinet, denBinet)));
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

  /**
   * Create an iterative partial fraction decomposition of the expression numerator / Times( ... )
   * for the given variable. * Example: Apart(1 / ((x - 1) * (x - 2)))
   *
   * @param numerator the numerator of the fraction expression
   * @param denominatorTimes the Times( ... ) expression of the denominator of the fraction
   *        expression
   * @param variable the variable to decompose over
   * @param engine the evaluation engine
   * @return the partial fraction decomposition if possible, otherwise F.NIL
   */
  public static IExpr partialFractionDecomposition(IExpr numerator, IExpr denominatorTimes,
      IExpr variable, EvalEngine engine) {
    if (!denominatorTimes.isTimes()) {
      return S.Times.of(engine, numerator, F.Power(denominatorTimes, -1));
    }
  
    IAST denomAST = (IAST) denominatorTimes;
    int argSize = denomAST.argSize();
  
    // Allocate a flat Plus buffer to collect the decomposed terms
    IASTAppendable resultPlus = F.PlusAlloc(argSize + 1);
  
    IExpr currentNumerator = numerator;
    IExpr currentFirst = denomAST.arg1();
    IExpr currentRest = denomAST.splice(1).oneIdentity0();
  
    for (int i = 1; i <= argSize; i++) {
      if (currentFirst.isFree(variable)) {
        // Factor is constant with respect to the variable, pull it into the numerator
        currentNumerator = S.Times.of(engine, currentNumerator, F.Power(currentFirst, -1));
      } else {
        IExpr v1 = S.Expand.of(engine, currentFirst);
        IExpr v2 = S.Expand.of(engine, currentRest);
        IExpr peGCD = S.PolynomialExtendedGCD.of(engine, v1, v2, variable);

        if (peGCD.isList() && peGCD.second().isList()) {
          // PolynomialExtendedGCD returns {g, {A, B}} with A*v1 + B*v2 == g. Splitting
          // n/(v1*v2) into n*B/v1 + n*A/v2 needs g to be a unit; when g still contains the
          // variable, v1 and v2 share a root and that split denotes n*g/(v1*v2) instead. That is
          // how Apart(1/((1-I*x)*(1+x^2))) - both factors contain x+I - used to return a wrong
          // value, and Apart(((I-x)*(I+x)^2)/((1-I*x)*(1+x^2))) collapsed to 0.
          IExpr gcd = peGCD.first();
          if (!gcd.isFree(variable) || gcd.isZero()) {
            return F.NIL;
          }
          IAST s = (IAST) peGCD.second();
          IExpr A = s.arg1();
          IExpr B = s.arg2();
          // dividing by the constant g makes the cofactors satisfy A*v1 + B*v2 == 1
          IExpr n = gcd.isOne() ? currentNumerator
              : S.Times.of(engine, currentNumerator, F.Power(gcd, -1));

          IExpr qr1 = S.PolynomialQuotientRemainder.ofNIL(engine, F.Expand(F.Times(B, n)), v1,
              variable);
          if (qr1.isList2()) {
            IExpr qr2 = S.PolynomialQuotientRemainder.ofNIL(engine, F.Expand(F.Times(A, n)), v2,
                variable);
            if (qr2.isList2()) {
              // n*B/v1 == q1 + u1/v1 and n*A/v2 == q2 + u2/v2. The quotients carry the polynomial
              // part of an improper fraction and cancel each other for a proper one, so keeping
              // them is what makes Apart(x^3/((1-I*x)*(2+x))) keep its x.
              resultPlus.append(qr1.first());
              resultPlus.append(qr2.first());
              // Append the resolved partial fraction term
              resultPlus.append(S.Times.of(engine, qr1.second(), F.Power(currentFirst, -1)));
              // Carry the remaining numerator forward
              currentNumerator = qr2.second();
            } else {
              return F.NIL;
            }
          } else {
            return F.NIL;
          }
        } else {
          return F.NIL;
        }
      }
  
      // Advance to the next factor in the denominator
      if (!currentRest.isTimes()) {
        // Base case: we have reached the last factor.
        // Append the final term and terminate the loop.
        resultPlus.append(S.Times.of(engine, currentNumerator, F.Power(currentRest, -1)));
        break;
      } else {
        IAST restAST = (IAST) currentRest;
        currentFirst = restAST.arg1();
        currentRest = restAST.splice(1).oneIdentity0();
      }
    }
  
    return engine.evaluate(resultPlus);
  }

  /**
   * Returns an AST with head <code>Plus</code>, which contains the partial fraction decomposition
   * of the numerator and denominator parts.
   *
   * @param pf partial fraction generator
   * @param parts
   * @param variableList a list of variable
   * @return {@link F#NIL} if the partial fraction decomposition wasn't constructed
   */
  public static IExpr partialFractionDecompositionRational(IPartialFractionGenerator pf,
      IExpr[] parts, IAST variableList) {
    try {
      IExpr exprNumerator = F.evalExpandAll(parts[0]);
      IExpr exprDenominator = F.evalExpandAll(parts[1]);
      JASConvert<BigRational> jas = new JASConvert<BigRational>(variableList, BigRational.ZERO);
      GenPolynomial<BigRational> numerator = jas.expr2JAS(exprNumerator, false);
      if (numerator == null) {
        return F.NIL;
      }
      GenPolynomial<BigRational> denominator = jas.expr2JAS(exprDenominator, false);
      if (denominator == null) {
        return F.NIL;
      }
      // get factors
      FactorAbstract<BigRational> factorAbstract =
          FactorFactory.getImplementation(BigRational.ZERO);
      SortedMap<GenPolynomial<BigRational>, Long> sfactors =
          factorAbstract.baseFactors(denominator);
  
      List<GenPolynomial<BigRational>> D =
          new ArrayList<GenPolynomial<BigRational>>(sfactors.keySet());
  
      SquarefreeAbstract<BigRational> sqf = SquarefreeFactory.getImplementation(BigRational.ZERO);
      List<List<GenPolynomial<BigRational>>> Ai = sqf.basePartialFraction(numerator, sfactors);
      // returns [ [Ai0, Ai1,..., Aie_i], i=0,...,k ] with A/prod(D) =
      // A0 + sum( sum ( Aij/di^j ) ) with deg(Aij) < deg(di).
  
      if (Ai.size() > 0) {
        // IAST result = F.Plus();
        pf.allocPlus(Ai.size() * 2);
        pf.setJAS(jas);
        if (!Ai.get(0).get(0).isZERO()) {
          pf.addNonFractionalPart(Ai.get(0).get(0));
        }
        for (int i = 1; i < Ai.size(); i++) {
          final List<GenPolynomial<BigRational>> list = Ai.get(i);
          int j = 0;
          for (GenPolynomial<BigRational> genPolynomial : list) {
            if (!genPolynomial.isZERO()) {
              final GenPolynomial<BigRational> Di_1 = D.get(i - 1);
              pf.addSinglePartialFraction(genPolynomial, Di_1, j);
            }
            j++;
          }
        }
        return pf.getResult();
      }
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      // JAS may throw JASConversionException and RuntimeExceptions
      // LOGGER.debug("Algebra.partialFractionDecompositionRational() failed", e);
    }
    return F.NIL;
  }

  /**
   * Returns an AST with head <code>Plus</code>, which contains the partial fraction decomposition
   * of the numerator and denominator parts.
   *
   * @param pf partial fraction generator
   * @param parts
   * @param variable a variable
   * @return {@link F#NIL} if the partial fraction decomposition wasn't constructed
   */
  public static IExpr partialFractionDecompositionRational(IPartialFractionGenerator pf,
      IExpr[] parts, IExpr variable) {
    return partialFractionDecompositionRational(pf, parts, F.list(variable));
  }

  /**
   * If possible returns an AST with head Plus, which contains the partial fraction decomposition of
   * the numerator and denominator parts.
   *
   * @param parts numerator and denominator parts
   * @param variable
   * @param engine
   * @return an AST with head Plus, which contains the partial fraction decomposition of the
   *         numerator and denominator parts. Otherwise return F.NIL
   */
  public static IExpr partsApart(IExpr[] parts, IExpr variable, EvalEngine engine) {
    IExpr temp = AlgebraUtil.partialFractionDecompositionRational(new PartialFractionGenerator(), parts,
        variable);
    if (temp.isPresent()) {
      return temp;
    }
    temp = S.Factor.of(engine, parts[1]);
    if (temp.isTimes()) {
      // The iterative method no longer requires a 'count' recursion tracker.
      return AlgebraUtil.partialFractionDecomposition(parts[0], temp, variable, engine);
    }
    return F.NIL;
  }

  public static IExpr polynomialTaylorSeries(IExpr[] parts, IExpr x, IExpr x0, int n,
      int expDenominator) {
    try {
      IExpr exprNumerator = F.evalExpandAll(parts[0]);
      IExpr exprDenominator = F.evalExpandAll(parts[1]);
  
      final UnivPowerSeries<BigRational> ps = AlgebraUtil.quotientPS(exprNumerator, exprDenominator, x);
      if (ps != null && !ps.isZERO()) {
        ASTSeriesData seriesData = new ASTSeriesData(x, x0, 0, n + expDenominator, expDenominator);
        // reversed order seems to be a bit faster
        for (int i = n; i >= 0; i--) {
          BigRational coefficient = ps.coefficient(i);
          seriesData.setCoeff(i, F.fraction(coefficient.numerator(), coefficient.denominator()));
        }
        return seriesData;
      }
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      // JAS may throw JASConversionException and RuntimeExceptions
      // LOGGER.debug("Algebra.polynomialTaylorSeries() failed", e);
    }
    return F.NIL;
  }

  public static UnivPowerSeries<BigRational> quotientPS(IExpr exprNumerator, IExpr exprDenominator,
      IExpr x) {
    JASConvert<BigRational> jas = new JASConvert<BigRational>(x.makeList(), BigRational.ZERO);
    GenPolynomial<BigRational> numerator = jas.expr2JAS(exprNumerator, false);
    if (numerator == null) {
      return null;
    }
    final UnivPowerSeries<BigRational> ps;
    BigRational cfac = BigRational.ONE;
    UnivPowerSeriesRing<BigRational> fac = new UnivPowerSeriesRing<BigRational>(cfac);
    TaylorFunction<BigRational> FN = new PolynomialTaylorFunction<BigRational>(numerator);
    if (exprNumerator.isOne()) {
      GenPolynomial<BigRational> denominator = jas.expr2JAS(exprDenominator, false);
      if (denominator == null) {
        return null;
      }
      TaylorFunction<BigRational> FD = new PolynomialTaylorFunction<BigRational>(denominator);
      UnivPowerSeries<BigRational> psD = fac.seriesOfTaylor(FD, BigRational.ZERO);
      ps = psD.inverse();
    } else {
      if (exprDenominator.isOne()) {
        ps = fac.seriesOfTaylor(FN, BigRational.ZERO);
      } else {
        GenPolynomial<BigRational> denominator = jas.expr2JAS(exprDenominator, false);
        if (denominator == null) {
          return null;
        }
        TaylorFunction<BigRational> FD = new PolynomialTaylorFunction<BigRational>(denominator);
        UnivPowerSeries<BigRational> psN = fac.seriesOfTaylor(FN, BigRational.ZERO);
        UnivPowerSeries<BigRational> psD = fac.seriesOfTaylor(FD, BigRational.ZERO);
        ps = psN.divide(psD);
      }
    }
    return ps;
  }

  public static IExpr reduceFactorConstant(IExpr p, EvalEngine engine) {

    if (!engine.isNumericMode() && p.isPlus() && !engine.isTogetherMode()) {
      IAST plusAST = (IAST) p;
      // ((reduceConstantTerm /@ (List @@ plusAST)) // Transpose)[[1]]
      IExpr terms = engine.evaluate(
          F.Map(F.Function(F.unaryAST1(reduceConstantTerm, F.Slot1)), F.Apply(S.List, plusAST)));
      if (!terms.isList()) {
        // evaluating plusAST collapsed it to a non-list (for example the terms cancelled to 0),
        // so there is no matrix of {constant, term} pairs to transpose
        return p;
      }
      IExpr cTerms = S.Transpose.of(engine, terms).first();
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

  /**
   * Compute the square-free factorization of a single polynomial (a {@link S#Plus} expression) with
   * JAS, normalize each factor to a positive leading coefficient and accumulate the result into
   * <code>factorMap</code> / <code>content</code>.
   */
  @SuppressWarnings("unchecked")
  private static boolean squareFreeLeaf(IAST plus, long multiplier, VariablesSet eVar,
      Map<IExpr, Long> factorMap, IRational[] content, boolean[] anyPolynomial, EvalEngine engine) {
    JASConvert<BigRational> jas =
        new JASConvert<BigRational>(eVar.getVarList(), BigRational.ZERO, TermOrderByName.INVLEX);
    GenPolynomial<BigRational> polyRat;
    try {
      polyRat = jas.expr2JAS(plus, false);
    } catch (JASConversionException e) {
      addSquareFreeFactor(factorMap, plus, multiplier);
      return true;
    }
    if (polyRat == null || polyRat.length() <= 1) {
      addSquareFreeFactor(factorMap, plus, multiplier);
      return true;
    }
    Object[] objects = jas.factorTerms(polyRat);
    SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
    try {
      GenPolynomial<edu.jas.arith.BigInteger> poly =
          (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
      FactorAbstract<edu.jas.arith.BigInteger> factorAbstract =
          FactorFactory.getImplementation(edu.jas.arith.BigInteger.ONE);
      map = factorAbstract.squarefreeFactors(poly);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      addSquareFreeFactor(factorMap, plus, multiplier);
      return true;
    }
    java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
    java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
    IRational leafContent = F.C1;
    if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
      leafContent = F.fraction(gcd, lcm).normalize();
    }
    anyPolynomial[0] = true;
    for (Map.Entry<GenPolynomial<edu.jas.arith.BigInteger>, Long> entry : map.entrySet()) {
      GenPolynomial<edu.jas.arith.BigInteger> poly = entry.getKey();
      long exponent = entry.getValue();
      if (poly.isONE() && exponent == 1L) {
        continue;
      }
      if (poly.leadingBaseCoefficient().signum() < 0) {
        // normalize to a positive leading coefficient; fold the sign into the numeric content
        poly = poly.negate();
        if ((exponent & 1L) == 1L) {
          leafContent = leafContent.negate();
        }
      }
      addSquareFreeFactor(factorMap, jas.integerPoly2Expr(poly), exponent * multiplier);
    }
    content[0] = content[0].multiply(leafContent.powerRational(multiplier));
    return true;
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
            // b^n == (1/b)^(-n) needs integer n, or a b that's known to be positive: for
            // fractional n the principal branches differ by Exp(-2*Pi*I*n) whenever b is a
            // negative real, so e.g. 1/Sqrt(-e/d) must not be rewritten to Sqrt(-d/e).
            if (arg1.exponent().isNegative()
                && (arg1.exponent().isInteger() || arg1.base().isPositiveResult())) {
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
   * Do a ExpandAll(ast) and call togetherAST afterwards with the result. Optimized to skip
   * redundant expansions for performance.
   *
   * @param ast
   * @return F.NIL couldn't be transformed by ExpandAll() or togetherAST()
   */
  public static IExpr togetherNull(IAST ast, EvalEngine engine) {
    boolean evaled = false;
    IExpr temp = F.NIL;

    // Skip expensive deep expansions if the expression is already marked as expanded
    if (!ast.isAllExpanded()) {
      temp = expandAll(ast, null, true, false, true, engine);
    }

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
   * Combine the terms of a Plus(...) expression into a single fractional expression, if possible.
   * * @param plusAST a Plus(...) expression
   * 
   * @return F.NIL if together couldn't be performed
   */
  public static IExpr togetherPlus(final IAST plusAST, EvalEngine engine) {
    if (plusAST.argSize() <= 1) {
      return F.NIL;
    }

    IExpr mergedRoots = tryMergeFractionalRoots(plusAST, engine);
    if (mergedRoots.isPresent()) {
      return mergedRoots;
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
          IAST termList = plusAST.setAtCopy(0, S.List);
          int argSize = termList.argSize();

          IASTAppendable numerators = F.ListAlloc(argSize);
          IASTAppendable denominators = F.ListAlloc(argSize);

          for (int i = 1; i <= argSize; i++) {
            IExpr arg = termList.get(i);
            Optional<IExpr[]> fractionalParts = fractionalPartsRational(arg, false, false);
            if (fractionalParts.isPresent()) {
              IExpr[] parts = fractionalParts.get();
              numerators.append(parts[0]);
              denominators.append(parts[1]);
            } else {
              numerators.append(arg);
              denominators.append(F.C1);
            }
          }

          if (denominators.exists(a -> !a.isOne())) {
            IExpr commonDenominator = engine.evaluate(denominators.setAtCopy(0, S.PolynomialLCM));
            IASTAppendable sum = F.PlusAlloc(numerators.argSize()); // 2025-12-05]

            boolean evaled = true;
            for (int i = 1; i <= numerators.argSize(); i++) {
              IExpr numer = numerators.get(i);
              IExpr denom = denominators.get(i);

              // Fast path: avoid symbolic quotient calculation if denominator is 1
              IExpr polynomialQuotient = denom.isOne() ? commonDenominator
                  : engine.evaluateNIL(F.PolynomialQuotient(commonDenominator, denom, variable));

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
                    true, true, true, engine);
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
          Errors.rethrowsInterruptException(rex);
        }
      }
    }

    int plusArgSize = plusAST.argSize();
    IASTAppendable numerator = F.PlusAlloc(plusArgSize);
    IASTAppendable denominatorList = F.TimesAlloc(plusArgSize);
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

    IExpr numOneId = numerator.oneIdentity0();
    IExpr denom = denominatorList.oneIdentity1();

    // Avoid double expansions if already expanded
    IExpr exprNumerator = numOneId.isExpanded() ? numOneId : F.evalExpand(numOneId);
    final IExpr exprDenominator = denom.isExpanded() ? denom : F.evalExpand(denom);

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
        // Handled silently
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
        // (p/q)^n == (q/p)^(-n) needs integer n, or a p/q that's known to be positive: for
        // fractional n the principal branches differ by Exp(-2*Pi*I*n) whenever p/q is a
        // negative real, so e.g. 1/Sqrt(-e/d) must not be rewritten to Sqrt(-d/e).
        if (ast.arg2().isNegative() && temp.isTimes()
            && (ast.arg2().isInteger() || temp.isPositiveResult())) {
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

  /**
   * Proactively find and merge fractional roots like c1*X^p + c2*Y^p where X * Y == 1. Rewrites to:
   * (c1 * X^(2p) + c2) / X^p
   */
  private static IExpr tryMergeFractionalRoots(IAST plusAST, EvalEngine engine) {
    int size = plusAST.argSize();
    if (size < 2)
      return F.NIL;

    for (int i = 1; i <= size; i++) {
      for (int j = i + 1; j <= size; j++) {
        IExpr arg1 = plusAST.get(i);
        IExpr arg2 = plusAST.get(j);

        IExpr[] part1 = extractFractionalPower(arg1);
        if (part1 == null)
          continue;

        IExpr[] part2 = extractFractionalPower(arg2);
        if (part2 == null)
          continue;

        IExpr c1 = part1[0];
        IExpr x = part1[1];
        IExpr p = part1[2];

        IExpr c2 = part2[0];
        IExpr y = part2[1];
        IExpr q = part2[2];

        // Ensure the fractional powers match
        if (!p.equals(q))
          continue;

        // Check if X and Y are reciprocals (X * Y == 1)
        IExpr product = engine.evaluate(F.Cancel(F.Times(x, y)));
        if (product.isOne()) {
          // Merge: (c1 * X^(2p) + c2) / X^p
          IExpr x2p = engine.evaluate(F.Power(x, F.Times(F.C2, p)));
          IExpr mergedNumerator = engine.evaluate(F.Plus(F.Times(c1, x2p), c2));
          IExpr mergedDenominator = engine.evaluate(F.Power(x, p));

          IExpr mergedTerm = F.Times(mergedNumerator, F.Power(mergedDenominator, F.CN1));

          // Replace the two terms in the Plus AST
          IASTAppendable newPlus = plusAST.removePositionsAtCopy(new int[] {i, j}, 2);
          newPlus.append(engine.evaluate(mergedTerm));

          // Return the evaluated AST, triggering further Together passes if needed
          return S.Together.of(engine, newPlus.oneIdentity0());
        }
      }
    }
    return F.NIL;
  }

  private AlgebraUtil() {
    // private constructor to avoid instantiation
  }

}
