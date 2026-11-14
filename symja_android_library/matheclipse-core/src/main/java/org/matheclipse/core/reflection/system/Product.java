package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Times;
import org.matheclipse.core.builtin.ListFunctions;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.reflection.system.rulesets.ProductRules;
import com.google.common.base.Suppliers;

public class Product extends ListFunctions.Table implements ProductRules {

  /**
   * The time limit of the limit of the partial products of an infinite product, and of the
   * simplification of its closed form.
   */
  private static final long LIMIT_SECONDS = 5L;

  private static com.google.common.base.Supplier<Matcher> MATCHER1;

  private static Matcher matcher1() {
    return MATCHER1.get();
  }

  public Product() {}

  /**
   * Product of expressions.
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Multiplication#Capital_Pi_notation">Wikipedia
   * Multiplication</a>
   */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    {
      // Product[..., {Subscript[k, 1], 1, n}]: a subscript as the iterator variable, or a formal
      // symbol
      IExpr localized =
          org.matheclipse.core.eval.util.Iterator.evaluateWithLocalizedVariables(ast, engine);
      if (localized != null) {
        return localized;
      }
    }
    if (Sum.hasAssumptionsOption(ast)) {
      // unevaluated under the assumptions, the product keeps its option
      return Sum.withAssumptionsOption(ast, engine);
    }
    IAST withValues = Sum.withIndexDependentValues(ast);
    if (withValues.isPresent()) {
      return withValues;
    }
    if (hasInvalidIterator(ast)) {
      // a count isn't an iterator, Product(x, 3) stays unevaluated
      return F.NIL;
    }
    IExpr numericProduct = Sum.numericIteration(ast, Times(), engine);
    if (numericProduct.isPresent()) {
      return numericProduct;
    }
    IExpr arg1 = ast.arg1();
    if (arg1.isList()) {
      // Product({f,g},{i,1,n}) is the list of the two products
      return arg1.mapThread(ast, 1);
    }
    if (arg1.isAST()) {
      // F.expand() falls back to its argument and never returns NIL
      arg1 = F.expand(arg1, false, false, false);
    }
    if (ast.isAST2() && ast.arg2().isList() && ast.arg2().argSize() == 3
        && ast.arg2().first().isSymbol()) {
      // a zero or pole of a factor on the range: not the products of the factors, each of which
      // is 0 or a pole for every n
      ISymbol k = (ISymbol) ast.arg2().first();
      IExpr from = engine.evaluate(ast.arg2().second());
      IExpr to = engine.evaluate(ast.arg2().last());
      IExpr pochhammer = pochhammerProduct(arg1, k, from, to, engine);
      if (pochhammer.isPresent()) {
        return pochhammer;
      }
      IExpr telescoping = telescopingProduct(arg1, k, from, to, engine);
      if (telescoping.isPresent()) {
        return telescoping;
      }
    }
    // the products of the factors of an infinite product may diverge, while their product doesn't
    final boolean infiniteRange = ast.last().isList() && ast.last().argSize() >= 3
        && (ast.last().last().isInfinity() || ast.last().second().isNegativeInfinity());
    if (arg1.isTimes() && !infiniteRange && !isLinearIn(arg1, ast.last(), engine)) {
      IExpr resultTimes = engine.evaluate(arg1.mapThread(ast, 1));
      if (resultTimes.isTimes()) {
        return factorialsToGamma(engine.evaluate(F.FullSimplify(resultTimes)), engine);
      }
      return resultTimes;
    }
    IAST preevaledProduct = engine.preevalForwardBackwardAST(ast, 1);
    if (preevaledProduct != ast && !preevaledProduct.equals(ast)) {
      // continue with the evaluated arguments, so that a product which can't be computed is
      // returned with them
      return preevaledProduct;
    }
    arg1 = preevaledProduct.arg1();
    return evaluateProduct(preevaledProduct, arg1, false, engine);
  }

  protected static IExpr evaluateProduct(final IAST preevaledProduct, IExpr arg1,
      boolean approximationMode, EvalEngine engine) {
    if (preevaledProduct.argSize() >= 2) {
      final IExpr lastArg = preevaledProduct.last();
      final IAST list = lastArg.makeList();

      if (list.isAST1()) {
        // indefinite product case
        IExpr variable = list.arg1();
        if (variable.isVariable()) {
          IExpr arg = preevaledProduct.arg1();
          if (preevaledProduct.arg1().isFree(variable)) {
            return indefiniteProduct(preevaledProduct, variable);
          }
          if (arg.isPower() && arg.equalsAt(1, variable)) {
            return productPowerFormula(arg, variable, F.C1, variable.dec());
          }
          if (arg.equals(variable)) {
            // the indefinite product of n: n!/(n-1)! == n
            return F.Factorial(F.Plus(F.CN1, variable));
          }
        }
      }

      // the variables of the outer iterators are symbolic while the innermost product is reduced
      // on its own; a global value like i=7 in Product(x,{i,2},{x,1,i}) must not leak into it
      final IAST outerVariables = Iterator.outerIteratorVariables(preevaledProduct);
      if (preevaledProduct.argSize() >= 2) {
        IAST productForm = preevaledProduct;
        IAST lastList = list;
        if (list.isAST2()) {
          // Product(f(x),..., {x, a}) ==> Product(f(x),..., {x, 1, a})
          lastList = F.List(list.arg1(), F.C1, list.arg2());
          productForm = productForm.setAtCopy(productForm.argSize(), lastList);
        }

        if (productForm.argSize() > 2) {
          // Multiple iterators: Evaluate the innermost product recursively. Quietly, like Sum: its
          // bounds may use an outer variable, e.g. list[[i]], which is symbolic here.
          IAST reducedProductForm = F.Product(productForm.arg1(), lastList);
          IExpr reducedResult =
              engine.evalBlock(() -> engine.evalQuietNIL(reducedProductForm), outerVariables);
          if (reducedResult.isPresent() && !reducedResult.equals(reducedProductForm)
              && !Iterator.losesVariable(lastList, reducedResult, outerVariables)) {
            IASTAppendable result = productForm.removeAtClone(productForm.argSize());
            result.set(1, reducedResult);
            return result;
          }
        } else {
          // Single iterator: apply pattern matcher rules if any
          IExpr result = matcher1().apply(productForm);
          if (result.isPresent()) {
            return result;
          }
        }
      }

      IExpr argN = lastArg;
      IIterator<IExpr> iterator = null;

      // === 1. SYMBOLIC REDUCTION INTERCEPT ===
      // Executed before evaluateTableThrow to prevent dummy variable shadowing
      // when limits contain the iterator variable symbolically.
      if (preevaledProduct.argSize() >= 2 && argN.isList()) {
        // A zero factor does NOT make the product zero on its own: an empty range is the empty
        // product 1, so Product(0,{i,1,0}) is 1 and not 0. The range is examined below.
        try {
          iterator =
              Iterator.createLocal((IAST) argN, preevaledProduct.argSize(), outerVariables, engine);
        } catch (final ValidateException ve) {
          return Errors.printMessage(S.Product, ve, engine);
        }

        // A list style iterator like {e, {2,1,1,1}} has no lower/upper limit and no step
        // ({@link IIterator} returns null for those). The symbolic reduction below only applies to
        // iterators with real limits; leave the list case to the numerical unrolling fallback.
        if (iterator != null && iterator.isValidVariable() && iterator.getLowerLimit() != null
            && iterator.getUpperLimit() != null && iterator.getStep() != null) {
          if (iterator.getUpperLimit().isInfinity()) {
            if (arg1.isOne()) {
              return F.C1;
            }
            if (arg1.isZero()) {
              // an infinite range is never the empty product
              return F.C0;
            }
            if (arg1.isPositiveResult() && arg1.isIntegerResult()) {
              return F.CInfinity;
            }
          }

          if (!iterator.isNumericFunction() && iterator.getStep().isOne()) {
            final ISymbol var = iterator.getVariable();
            final IExpr from = iterator.getLowerLimit();
            final IExpr to = iterator.getUpperLimit();

            if (to.isInfinity() && preevaledProduct.isAST2()) {
              IExpr gammaForm = infiniteRationalProduct(arg1, var, from, engine);
              if (gammaForm.isPresent()) {
                return gammaForm;
              }
              // the limit of the partial products: a closed form with Infinity substituted is
              // Gamma(Infinity)^2/Gamma(Infinity)^2, Indeterminate
              ISymbol upper = F.Dummy("m");
              IExpr partial = tryClosedFormReduction(arg1, var, from, upper, engine);
              if (partial.isPresent()) {
                IExpr limit = engine.evalQuiet(F.TimeConstrained(
                    F.Limit(partial, F.Rule(upper, F.CInfinity)), F.ZZ(LIMIT_SECONDS)));
                if (limit.isFree(x -> x.isAST(S.Limit) || x == S.Indeterminate || x == S.$Aborted,
                    true) && limit.isFree(upper)) {
                  return engine.evaluate(F.Simplify(limit));
                }
              }
            }
            // Divert to the Hypergeometric Symbolic Product Engine
            IExpr symProd = tryClosedFormReduction(arg1, var, from, to, engine);
            if (symProd.isPresent() && to.isInfinity()
                && !engine.evalQuiet(symProd).isFree(S.Indeterminate, true)) {
              // no value of the partial products' limit: not Indeterminate
              symProd = F.NIL;
            }
            if (symProd.isPresent()) {

              // Simplify the result to ensure factorial ratios are reduced
              symProd = engine.evaluate(F.Simplify(symProd));
              symProd = factorialsToGamma(symProd, engine);

              if (preevaledProduct.isAST2()) {
                return symProd;
              }
              IASTAppendable result = preevaledProduct.removeAtClone(preevaledProduct.argSize());
              result.set(1, symProd);
              return result;
            }

            // Universal evaluation for terms free of the iterator
            if (arg1.isFree(var)) {
              IExpr count = engine.evaluate(F.Simplify(F.Plus(F.Subtract(to, from), F.C1)));
              // an empty range is the empty product, whatever the factor is; in particular
              // Power(0, 0) must not be reached here
              IExpr evalPower = count.isNonPositiveResult() ? F.C1 : F.Power(arg1, count);
              if (preevaledProduct.isAST2()) {
                return engine.evaluate(evalPower);
              }
              IASTAppendable result = preevaledProduct.removeAtClone(preevaledProduct.argSize());
              result.set(1, engine.evaluate(evalPower));
              return result;
            }
          } else if (!iterator.isNumericFunction() && !iterator.getStep().isOne()) {
            // A stepped iterator is reindexed onto a step of 1 and multiplied again; the symbolic
            // reduction above assumes a step of 1.
            IExpr steppedProduct = steppedProduct(arg1, iterator, engine);
            if (steppedProduct.isPresent()) {
              if (preevaledProduct.isAST2()) {
                return steppedProduct;
              }
              IASTAppendable result = preevaledProduct.removeAtClone(preevaledProduct.argSize());
              result.set(1, steppedProduct);
              return result;
            }
          }
        }
      }

      // === 2. NUMERICAL UNROLLING FALLBACK ===
      try {
        IExpr temp = evaluateTableThrow(preevaledProduct, Times(), Times(), engine);
        if (temp.isPresent()) {
          return temp;
        }
      } catch (final ValidateException ve) {
        return Errors.printMessage(S.Product, ve, engine);
      } catch (RecursionLimitExceeded rle) {
        return recursionLimitMessage(S.Product, preevaledProduct, engine);
      }

      // === 3. FINAL MANUAL LOOP EVALUATION ===
      if (iterator != null) {
        try {
          if (preevaledProduct.argSize() > 2) {
            return F.NIL;
          }
          IAST resultList = Times();
          IExpr temp = evaluateLast(preevaledProduct.arg1(), iterator, resultList, F.C1, S.Product);
          if (temp.isNIL() || temp.equals(resultList)) {
            return F.NIL;
          }
          return temp;
        } catch (final ValidateException ve) {
          return Errors.printMessage(S.Product, ve, engine);
        } catch (RecursionLimitExceeded rle) {
          return recursionLimitMessage(S.Product, preevaledProduct, engine);
        }
      }
    }
    return F.NIL;
  }

  /**
   * <code>Product(f, {k, a, n})</code> of a rational function <code>f</code> with linear factors,
   * one of which has an integer zero or pole <code>r &gt;= a</code> on the range of a symbolic
   * <code>n</code>: <code>Product(k-r, {k, a, n}) == Pochhammer(a-r, n-a+1)</code>, which is 0 (or
   * the pole) only from <code>n == r</code> on. The Gamma form <code>Gamma(n+1-r)/Gamma(a-r)</code>
   * has a pole in its denominator and gave 0 or ComplexInfinity for every <code>n</code>:
   * <code>Product((k-3)/(k+1), {k, 1, n})</code> is -1 for <code>n == 1</code>.
   *
   * @return {@link F#NIL} if <code>f</code> isn't of this kind
   */
  private static IExpr pochhammerProduct(IExpr f, ISymbol k, IExpr from, IExpr to,
      EvalEngine engine) {
    if (!from.isInteger() || to.isInteger() || to.isInfinity() || !to.isFree(k, true)
        || f.isFree(k, true)) {
      return F.NIL;
    }
    IExpr together = engine.evaluate(F.Together(f));
    IExpr count = engine.evaluate(F.Plus(F.Subtract(to, from), F.C1));
    IASTAppendable result = F.TimesAlloc(8);
    boolean[] integerRootOnRange = {false};
    if (!pochhammerFactors(engine.evaluate(F.Numerator(together)), k, (IInteger) from, count,
        F.C1, result, integerRootOnRange, engine)
        || !pochhammerFactors(engine.evaluate(F.Denominator(together)), k, (IInteger) from,
            count, F.CN1, result, integerRootOnRange, engine)
        || !integerRootOnRange[0]) {
      return F.NIL;
    }
    return engine.evaluate(result);
  }

  /**
   * <code>Product(f, {k, a, n})</code> for a symbolic <code>n</code> if the factors cancel in
   * pairs: <code>f(k) == g(k)/g(k+1)</code> gives <code>g(a)/g(n+1)</code>. Also
   * <code>Product(k^(c*k), {k, 1, n}) == Hyperfactorial(n)^c</code>.
   *
   * @return {@link F#NIL} if <code>f</code> isn't of this kind
   */
  private static IExpr telescopingProduct(IExpr f, ISymbol k, IExpr from, IExpr to,
      EvalEngine engine) {
    if (!from.isInteger() || to.isNumber() || to.isInfinity() || !to.isFree(k, true)
        || f.isFree(k, true)) {
      return F.NIL;
    }
    if (from.isOne() && f.isPower() && f.base().equals(k)) {
      IExpr c = engine.evaluate(F.Divide(f.exponent(), k));
      if (c.isFree(k, true)) {
        return engine.evaluate(F.Power(F.Hyperfactorial(to), c));
      }
    }
    // f(k) == B(k)/B(k+1) for one of the candidates B
    IExpr together = engine.evaluate(F.Together(f));
    IASTAppendable candidates = F.ListAlloc(4);
    candidates.append(engine.evaluate(F.Power(F.Denominator(together), F.CN1)));
    candidates.append(engine.evaluate(F.Numerator(together)));
    if (f.isTimes() && f.argSize() == 2) {
      candidates.append(f.first());
      candidates.append(f.second());
    }
    final IInteger start = (IInteger) from;
    for (IExpr b : candidates) {
      if (b.isFree(k, true) || hasZeroOrPoleFrom(b, k, start)) {
        // a zero or pole of B on the range is a factor 0/0 or a pole which the quotient steps over
        continue;
      }
      IExpr quotient = engine.evaluate(
          F.Times(f, F.subst(b, k, F.Plus(k, F.C1)), F.Power(b, F.CN1)));
      if (quotient.isOne() || engine.evaluate(F.Together(quotient)).isOne()) {
        return engine.evaluate(
            F.Divide(F.subst(b, k, from), F.subst(b, k, F.Plus(to, F.C1))));
      }
    }
    return F.NIL;
  }

  /**
   * Whether a factor or a base of a power in <code>b</code> is <code>k</code> or a sum linear in
   * <code>k</code> with an integer root <code>&gt;= from</code>; exponents aren't looked at.
   */
  private static boolean hasZeroOrPoleFrom(IExpr b, ISymbol k, IInteger from) {
    if (b.isTimes()) {
      return ((IAST) b).exists(x -> hasZeroOrPoleFrom(x, k, from));
    }
    if (b.isPower()) {
      return hasZeroOrPoleFrom(b.base(), k, from);
    }
    return hasIntegerRootFrom(b, k, from);
  }

  /**
   * Whether <code>x</code> is <code>k</code> or a sum linear in <code>k</code> with an integer root
   * <code>&gt;= from</code>.
   */
  private static boolean hasIntegerRootFrom(IExpr x, ISymbol k, IInteger from) {
    if (x.equals(k)) {
      return !from.isPositive();
    }
    if (!x.isPlus()) {
      return false;
    }
    IExpr[] linear = x.linear(k);
    if (linear == null || linear[1].isZero() || !linear[0].isFree(k, true)
        || !linear[1].isFree(k, true)) {
      return false;
    }
    IExpr root = EvalEngine.get().evaluate(F.Negate(F.Divide(linear[0], linear[1])));
    return root.isInteger() && !((IInteger) root).isLT(from);
  }

  /**
   * Append the products of the factors of the polynomial <code>p</code> in <code>k</code> over
   * <code>count</code> terms from <code>from</code> on, raised to <code>sign</code>.
   *
   * @return <code>false</code> if a factor isn't linear in <code>k</code>
   */
  private static boolean pochhammerFactors(IExpr p, ISymbol k, IInteger from, IExpr count,
      IExpr sign, IASTAppendable result, boolean[] integerRootOnRange, EvalEngine engine) {
    IExpr factors = engine.evaluate(F.FactorList(p));
    if (!factors.isList()) {
      return false;
    }
    for (IExpr pair : (IAST) factors) {
      if (!pair.isList2()) {
        return false;
      }
      IExpr factor = pair.first();
      IExpr exponent = engine.evaluate(F.Times(sign, pair.second()));
      if (!exponent.isFree(k, true)) {
        // (k-3)^k: not a rational function
        return false;
      }
      if (factor.isFree(k, true)) {
        result.append(F.Power(factor, F.Times(count, exponent)));
        continue;
      }
      IExpr[] linear = factor.linear(k);
      if (linear == null || !linear[1].isFree(k, true) || !linear[0].isFree(k, true)) {
        return false;
      }
      // c1*k + c0 == c1*(k - r)
      IExpr root = engine.evaluate(F.Negate(F.Divide(linear[0], linear[1])));
      if (root.isInteger() && !((IInteger) root).isLT(from)) {
        integerRootOnRange[0] = true;
      }
      result.append(F.Power(linear[1], F.Times(count, exponent)));
      IExpr start = engine.evaluate(F.Subtract(from, root));
      // the first term is the zero or the pole: Pochhammer(0, n) is 0 for every n >= 1
      IExpr pochhammer = start.isZero() ? F.C0
          : start.isPositive() ? F.Divide(F.Gamma(F.Plus(start, count)), F.Gamma(start))
              : F.Pochhammer(start, count);
      result.append(F.Power(pochhammer, exponent));
    }
    return true;
  }

  /**
   * Hypergeometric Term Recognition (Pochhammer Mapping & Exponential Products) * @param pK the
   * product term with iterator k (e.g. k^2 + 3k + 2 or 2^k)
   * 
   * @param k the iterator variable
   * @param lower the lower bound of the product
   * @param upper the upper bound of the product
   * @param engine the evaluation engine
   * @return the reduced form of the product or F.NIL if not reducible
   */
  public static IExpr tryClosedFormReduction(IExpr pK, IExpr k, IExpr lower, IExpr upper,
      EvalEngine engine) {
    return tryClosedFormReduction(pK, k, lower, upper, false, engine);
  }

  /**
   * Hypergeometric Term Recognition, see
   * {@link #tryClosedFormReduction(IExpr, IExpr, IExpr, IExpr, EvalEngine)}.
   *
   * @param pochhammerForm write the product of a linear factor as <code>Pochhammer(start, count)
   *        </code>, like WMA's <code>RSolve</code> does: <code>RSolve(a(n+1) == (n+1)*a(n), a(n),
   *        n)</code> gives <code>C(1)*Pochhammer(1,n)</code>. Otherwise like WMA's
   *        <code>Product</code>: <code>n!/2</code> and <code>Gamma(3+n)/2</code>
   */
  public static IExpr tryClosedFormReduction(IExpr pK, IExpr k, IExpr lower, IExpr upper,
      boolean pochhammerForm, EvalEngine engine) {
    if (pK.isFree(k)) {
      IExpr count = engine.evaluate(F.Simplify(F.Plus(F.Subtract(upper, lower), F.C1)));
      return engine.evaluate(F.Power(pK, count));
    }

    // 1. Intercept Exponential Products & Constant Powers
    if (pK.isPower()) {
      IExpr base = pK.base();
      IExpr exponent = pK.exponent();
      if (!exponent.isFree(k)) {
        if (!base.isFree(k)) {
          // k^k: a base of the index is no constant of the product of the powers
          return F.NIL;
        }
        // Exponential product: base^f(k) -> base^Sum(f(k))
        IExpr sum = engine.evaluate(F.Sum(exponent, F.List(k, lower, upper)));
        if (sum.isPresent() && !sum.isAST(S.Sum)) {
          return engine.evaluate(F.Power(base, sum));
        }
      } else {
        // Constant power: f(k)^p -> Product(f(k))^p
        IExpr baseProd = tryClosedFormReduction(base, k, lower, upper, pochhammerForm, engine);
        if (baseProd.isPresent()) {
          return engine.evaluate(F.Power(baseProd, exponent));
        }
      }
    }

    // 2. Intercept already-factored terms (e.g. (k + 1/2) * (k + 3/2)); a linear 2*k is a single
    // factor below
    if (pK.isTimes() && !isLinearIn(pK, F.list(k, lower, upper), engine)) {
      IASTAppendable res = F.TimesAlloc(pK.argSize());
      for (IExpr arg : (IAST) pK) {
        IExpr termProd = tryClosedFormReduction(arg, k, lower, upper, pochhammerForm, engine);
        if (!termProd.isPresent()) {
          return F.NIL;
        }
        res.append(termProd);
      }
      return engine.evaluate(res);
    }

    // 3. Strict Linear Coefficient Extraction
    IExpr A = engine.evaluate(F.Coefficient(pK, k));
    IExpr B = engine.evaluate(F.Expand(F.Subtract(pK, F.Times(A, k))));
    IExpr check = engine.evaluate(F.ExpandAll(F.Subtract(pK, F.Plus(F.Times(A, k), B))));

    // A and B must strictly be free of the iterator K!
    if (check.isZero() && !A.isZero() && A.isFree(k) && B.isFree(k)) {
      IExpr root = engine.evaluate(F.Divide(B, A));
      IExpr count = engine.evaluate(F.Simplify(F.Plus(F.Subtract(upper, lower), F.C1)));
      IExpr startVal = engine.evaluate(F.Simplify(F.Plus(lower, root)));

      // If the very first term in the sequence evaluates to 0, the entire product is trivially 0.
      if (startVal.isZero()) {
        return F.C0;
      }

      IExpr poch;
      if (pochhammerForm && startVal.isInteger() && startVal.isPositive()) {
        poch = F.Pochhammer(startVal, count);
      } else if (!(A.isOne() && B.isZero()) && startVal.isInteger() && startVal.isPositive()) {
        // like WMA only the bare iterator gives a factorial, Product(k, {k,3,n}) == n!/2, a
        // shifted or scaled factor gives Gamma: Product(k+2, {k,1,n}) == Gamma(3+n)/2 and
        // Product(2*k, {k,1,n}) == 2^n*Gamma(1+n)
        poch = engine.evaluate(F.Divide(F.Gamma(F.Plus(count, startVal)), F.Gamma(startVal)));
      } else if (startVal.isOne()) {
        // Pochhammer(1, count) is strictly Factorial(count)
        poch = F.Factorial(count);
      } else if (startVal.isInteger() && startVal.greaterThan(F.C0).isTrue()) {
        IExpr m = startVal;
        // Pochhammer(m, count) mapped to (count + m - 1)! / (m - 1)!
        IExpr num = F.Factorial(F.Subtract(F.Plus(count, m), F.C1));
        IExpr den = engine.evaluate(F.Factorial(F.Subtract(m, F.C1)));
        poch = engine.evaluate(F.Divide(num, den));
      } else if (startVal.isNumber() && !startVal.isInteger()) {
        // Return Gamma ratio for numeric fractional or complex shifts
        IExpr num = F.Gamma(F.Plus(startVal, count));
        IExpr den = F.Gamma(startVal);
        poch = engine.evaluate(F.Divide(num, den));
      } else {
        // Keep Pochhammer for symbolic shift or non-positive integers
        poch = F.Pochhammer(startVal, count);
      }

      if (A.isOne()) {
        return poch;
      }
      return engine.evaluate(F.Times(F.Power(A, count), poch));
    }

    // 4. Try factoring generic polynomials (e.g. k^2 + 3k + 2 -> (k+1)*(k+2))
    IExpr factored = engine.evaluate(F.Factor(pK));
    if (!factored.equals(pK) && factored.isTimes()) {
      IASTAppendable res = F.TimesAlloc(factored.argSize());
      for (IExpr arg : (IAST) factored) {
        IExpr termProd = tryClosedFormReduction(arg, k, lower, upper, pochhammerForm, engine);
        if (!termProd.isPresent()) {
          return F.NIL;
        }
        res.append(termProd);
      }
      return engine.evaluate(res);
    }

    // 5. A polynomial which is irreducible over the rationals (e.g. k^2 + 2k + 2): split it over
    // its exact complex roots
    return polynomialRootProduct(pK, k, lower, upper, engine);
  }

  /**
   * If the closed form of a product contains <code>Gamma</code> factors (the roots of a polynomial
   * factor aren't rational), write its factorials as <code>Gamma</code> too, like WMA:
   * <code>Product(1+1/(i+1)^2, {i,1,n})</code> contains <code>1/Gamma(2+n)^2</code> and not
   * <code>1/((1+n)!)^2</code>. A product of linear factors keeps its factorials:
   * <code>Product(k, {k,3,n}) == n!/2</code>.
   */
  private static IExpr factorialsToGamma(IExpr closedForm, EvalEngine engine) {
    if (!closedForm.has(S.Gamma, true) || !closedForm.has(S.Factorial, true)) {
      return closedForm;
    }
    IExpr gammaForm = closedForm
        .replaceAll(e -> e.isAST(S.Factorial, 2) ? F.Gamma(F.Plus(F.C1, e.first())) : F.NIL);
    return gammaForm.isPresent() ? engine.evaluate(gammaForm) : closedForm;
  }

  /**
   * Is <code>factor</code> linear in the variable of the <code>iterator</code>? A linear factor
   * like <code>2*k</code> isn't split into <code>Product(2)*Product(k)</code>, like WMA it gives
   * <code>2^n*Gamma(1+n)</code> and not <code>2^n*n!</code>.
   */
  private static boolean isLinearIn(IExpr factor, IExpr iterator, EvalEngine engine) {
    IExpr k = iterator.isList() && iterator.argSize() >= 2 ? iterator.first() : F.NIL;
    if (!k.isSymbol() || !factor.isPolynomial(F.list(k))) {
      return false;
    }
    return engine.evaluate(F.Exponent(factor, k)).isOne();
  }

  /** Polynomials of higher degree have roots which aren't given by radicals. */
  private static final int MAX_ROOT_PRODUCT_DEGREE = 4;

  /**
   * <code>Product(R(k), {k, from, Infinity})</code> for a rational function
   * <code>R(k) = (k-a1)*...*(k-ad) / ((k-b1)*...*(k-bd))</code> with numeric roots of the same sum
   * (otherwise the partial products tend to <code>0</code> or diverge):
   *
   * <pre>
   * Product(Gamma(from-bj), {j, 1, d}) / Product(Gamma(from-aj), {j, 1, d})
   * </pre>
   *
   * @return {@link F#NIL} if <code>R</code> isn't of this form, a root can't be found exactly or a
   *         factor of the denominator vanishes in the range
   */
  private static IExpr infiniteRationalProduct(IExpr term, ISymbol k, IExpr from,
      EvalEngine engine) {
    if (!from.isInteger()) {
      return F.NIL;
    }
    try {
      IExpr together = engine.evaluate(F.Together(term));
      IExpr numerator = engine.evaluate(F.Numerator(together));
      IExpr denominator = engine.evaluate(F.Denominator(together));
      if (denominator.isFree(k) || !engine.evaluate(F.Variables(together)).equals(F.list(k))
          || !numerator.isPolynomial(F.list(k)) || !denominator.isPolynomial(F.list(k))) {
        return F.NIL;
      }
      IExpr degree = engine.evaluate(F.Exponent(numerator, k));
      if (!degree.isInteger() || !degree.equals(engine.evaluate(F.Exponent(denominator, k)))) {
        return F.NIL;
      }
      IExpr previous = engine.evaluate(F.Subtract(degree, F.C1));
      // equal leading coefficients and equal sums of the roots
      IExpr leadingNumerator = engine.evaluate(F.Coefficient(numerator, k, degree));
      IExpr leadingDenominator = engine.evaluate(F.Coefficient(denominator, k, degree));
      IExpr ratio = engine.evaluate(F.Subtract(
          F.Divide(F.Coefficient(numerator, k, previous), leadingNumerator),
          F.Divide(F.Coefficient(denominator, k, previous), leadingDenominator)));
      if (!leadingNumerator.equals(leadingDenominator) || !ratio.isZero()) {
        return F.NIL;
      }
      IASTAppendable numeratorStarts = F.ListAlloc();
      IASTAppendable denominatorStarts = F.ListAlloc();
      int zeroFactor = rootStarts(numerator, k, from, numeratorStarts, engine);
      if (zeroFactor < 0) {
        return F.NIL;
      }
      if (rootStarts(denominator, k, from, denominatorStarts, engine) != 0) {
        // a vanishing factor of the denominator, or roots which aren't known
        return F.NIL;
      }
      if (zeroFactor > 0) {
        return F.C0;
      }
      IExpr result = engine.evaluate(F.Divide(gammaProduct(denominatorStarts, true, engine),
          gammaProduct(numeratorStarts, true, engine)));
      IExpr simplified = engine.evalQuiet(F.TimeConstrained(F.Simplify(result),
          F.ZZ(LIMIT_SECONDS)));
      return simplified.isPresent() && simplified != S.$Aborted ? simplified : result;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * Append <code>from - r</code> for each root <code>r</code> of the polynomial <code>p</code> in
   * <code>k</code>, as often as its multiplicity.
   *
   * @return <code>-1</code> if a root can't be found exactly, <code>1</code> if a root is an
   *         integer <code>&gt;= from</code> (a factor of the product vanishes), <code>0</code>
   *         otherwise
   */
  private static int rootStarts(IExpr p, ISymbol k, IExpr from, IASTAppendable starts,
      EvalEngine engine) {
    IExpr factorList = engine.evaluate(F.FactorList(p));
    if (!factorList.isList()) {
      return -1;
    }
    int result = 0;
    for (IExpr factorPower : (IAST) factorList) {
      if (!factorPower.isList2()) {
        return -1;
      }
      IExpr factor = factorPower.first();
      int multiplicity = factorPower.second().toIntDefault();
      if (factor.isFree(k)) {
        continue;
      }
      int factorDegree = engine.evaluate(F.Exponent(factor, k)).toIntDefault();
      if (multiplicity <= 0 || factorDegree <= 0 || factorDegree > MAX_ROOT_PRODUCT_DEGREE) {
        return -1;
      }
      IExpr solved = engine.evalQuiet(F.SolveDistinct(F.Equal(factor, F.C0), k));
      if (!solved.isList() || ((IAST) solved).argSize() != factorDegree) {
        return -1;
      }
      for (IExpr solution : (IAST) solved) {
        if (!solution.isList1() || !solution.first().isRule()
            || !solution.first().first().equals(k)) {
          return -1;
        }
        IExpr root = solution.first().second();
        if (!root.isFree(k) || !root.isNumericFunction(true)
            || !root.isFree(h -> h == S.Root || h == S.RootSum, true)) {
          return -1;
        }
        // Solve returns (-1)^(2/3); the real and imaginary parts are needed for the reflection
        root = engine.evaluate(F.Expand(F.ComplexExpand(root)));
        IExpr start = engine.evaluate(F.Subtract(from, root));
        if (start.isInteger() && !start.isPositive()) {
          result = 1;
        }
        for (int i = 0; i < multiplicity; i++) {
          starts.append(start);
        }
      }
    }
    return result;
  }

  /**
   * <code>Product(p(k), {k, lower, upper})</code> for a polynomial <code>p(k) = c*(k-r1)*...*(k-rd)
   * </code> with exact (possibly complex) roots <code>rj</code>:
   *
   * <pre>
   * c^(upper-lower+1) * Product(Gamma(upper+1-rj) / Gamma(lower-rj), {j, 1, d})
   * </pre>
   *
   * The constant denominators of pairs of roots whose sum is an integer are combined with the
   * reflection formula, e.g. <code>Gamma(2-I)*Gamma(2+I) == 2*Pi/Sinh(Pi)</code>.
   *
   * @return {@link F#NIL} if <code>p</code> isn't such a polynomial, a root can't be found exactly
   *         or a factor of the product is zero
   */
  private static IExpr polynomialRootProduct(IExpr p, IExpr k, IExpr lower, IExpr upper,
      EvalEngine engine) {
    if (!k.isSymbol() || !p.isPolynomial(F.list(k)) || !lower.isNumber()) {
      return F.NIL;
    }
    IExpr degreeExpr = engine.evaluate(F.Exponent(p, k));
    if (!degreeExpr.isInteger()) {
      return F.NIL;
    }
    int degree = degreeExpr.toIntDefault();
    if (degree < 2 || degree > MAX_ROOT_PRODUCT_DEGREE) {
      return F.NIL;
    }
    IExpr leadingCoefficient = engine.evaluate(F.Coefficient(p, k, degreeExpr));
    IASTAppendable gammaArgs = F.ListAlloc(degree);
    if (rootStarts(p, (ISymbol) k, lower, gammaArgs, engine) != 0) {
      // a root which isn't known, or Gamma(lower - root) is singular: the integer root is inside
      // the range
      return F.NIL;
    }
    IASTAppendable numerator = F.TimesAlloc(degree + 1);
    // Gamma(upper + 1 - root) == Gamma(count + start), count = upper - lower + 1
    IExpr shift = engine.evaluate(F.Plus(F.Subtract(upper, lower), F.C1));
    for (IExpr start : gammaArgs) {
      numerator.append(F.Gamma(F.Plus(shift, start)));
    }
    numerator.append(F.Power(leadingCoefficient, shift));
    IExpr denominator = gammaProduct(gammaArgs, engine);
    return engine.evaluate(F.Divide(numerator, denominator));
  }

  /**
   * The product of <code>Gamma(s)</code> for the arguments <code>s</code> in <code>args</code>. Two
   * arguments <code>c+z, c-z</code> with a positive <code>c</code> which is an integer or a half
   * integer are combined with the reflection formula (<code>m</code> is a non negative integer):
   *
   * <pre>
   * Gamma(m+z)*Gamma(m-z) == Pi*z/Sin(Pi*z) * Product(j^2-z^2, {j, 1, m-1})
   * Gamma(m+1/2+z)*Gamma(m+1/2-z) == Pi/Cos(Pi*z) * Product((j+1/2)^2-z^2, {j, 0, m-1})
   * </pre>
   */
  private static IExpr gammaProduct(IAST args, EvalEngine engine) {
    return gammaProduct(args, false, engine);
  }

  /**
   * See {@link #gammaProduct(IAST, EvalEngine)}.
   *
   * @param rationalShifts combine also <code>c+z, c-z</code> with a rational <code>z</code>
   */
  private static IExpr gammaProduct(IAST args, boolean rationalShifts, EvalEngine engine) {
    IASTAppendable result = F.TimesAlloc(args.argSize());
    boolean[] used = new boolean[args.size()];
    for (int i = 1; i < args.size(); i++) {
      if (used[i]) {
        continue;
      }
      IExpr s1 = args.get(i);
      IExpr paired = F.NIL;
      for (int j = i + 1; j < args.size() && paired.isNIL(); j++) {
        if (!used[j]) {
          paired = reflectionPair(s1, args.get(j), rationalShifts, engine);
          if (paired.isPresent()) {
            used[j] = true;
          }
        }
      }
      result.append(paired.isPresent() ? paired : F.Gamma(s1));
    }
    return engine.evaluate(result.oneIdentity1());
  }

  /**
   * <code>Gamma(s1)*Gamma(s2)</code> by the reflection formula, if <code>s1 == c+z</code> and
   * <code>s2 == c-z</code> for a positive integer <code>2*c</code> and a <code>z</code> where both
   * Gammas are finite. Like WMA also for a real <code>z</code>:
   * <code>Gamma(2-Sqrt(2))*Gamma(2+Sqrt(2)) == -Sqrt(2)*Pi/Sin(Sqrt(2)*Pi)</code>.
   *
   * @param rationalShifts allow a rational <code>z</code>
   * @return {@link F#NIL} if the arguments aren't of this form
   */
  private static IExpr reflectionPair(IExpr s1, IExpr s2, boolean rationalShifts,
      EvalEngine engine) {
    IExpr twoC = engine.evaluate(F.Plus(s1, s2));
    if (!twoC.isInteger() || !twoC.isPositive()) {
      return F.NIL;
    }
    IExpr c = engine.evaluate(F.Times(F.C1D2, twoC));
    IExpr z = engine.evaluate(F.Expand(F.Subtract(s1, c)));
    if ((z.isRational() && (!rationalShifts || z.isInteger())) || !z.isNumericFunction(true)) {
      // a rational z belongs to linear factors, which are multiplied elsewhere
      return F.NIL;
    }
    int twoCInt = twoC.toIntDefault();
    if (twoCInt <= 0 || twoCInt > 200) {
      return F.NIL;
    }
    IASTAppendable result = F.TimesAlloc(twoCInt / 2 + 2);
    IExpr zSquared = F.Sqr(z);
    if (twoCInt % 2 == 0) {
      // Gamma(m+z)*Gamma(m-z) == Pi*z/Sin(Pi*z) * Product(j^2-z^2, {j, 1, m-1})
      int m = twoCInt / 2;
      IExpr sin = engine.evaluate(F.Sin(F.Times(S.Pi, z)));
      if (sin.isZero()) {
        return F.NIL;
      }
      result.append(F.Divide(F.Times(S.Pi, z), sin));
      for (int j = 1; j < m; j++) {
        result.append(F.Subtract(F.ZZ(j * j), zSquared));
      }
    } else {
      // Gamma(m+1/2+z)*Gamma(m+1/2-z) == Pi/Cos(Pi*z) * Product((j+1/2)^2-z^2, {j, 0, m-1})
      int m = twoCInt / 2;
      IExpr cos = engine.evaluate(F.Cos(F.Times(S.Pi, z)));
      if (cos.isZero()) {
        return F.NIL;
      }
      result.append(F.Divide(S.Pi, cos));
      for (int j = 0; j < m; j++) {
        result.append(F.Subtract(F.Sqr(F.Plus(F.ZZ(j), F.C1D2)), zSquared));
      }
    }
    return engine.evaluate(result);
  }

  private static IExpr productPowerFormula(IExpr powerAST, IExpr k, IExpr from, IExpr to) {
    if (from.isOne()) {
      // ((-1+variable)!)^exponent
      return F.Power(F.Factorial(to), powerAST.exponent());
    }
    return F.NIL;
  }

  /**
   * Multiply over an iterator whose step differs from <code>1</code> by reindexing it onto a step
   * of <code>1</code>, see {@link ListFunctions.Table#reindexStepIterator(IExpr, IIterator)}.
   *
   * @return the closed form or {@link F#NIL} if the reindexed product has none either
   */
  private static IExpr steppedProduct(IExpr expr, IIterator<IExpr> iterator, EvalEngine engine) {
    IAST reindexed = reindexStepIterator(expr, iterator);
    if (reindexed.isNIL()) {
      return F.NIL;
    }
    IExpr result = engine.evalQuietNIL(F.Product(reindexed.arg1(), reindexed.arg2()));
    if (result.isPresent() && result.isFreeAST(S.Product) && result.isFreeAST(S.Sum)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Create a new Product() by removing last iterator or return result of indefinite sum case for
   * Product(a, x)
   *
   * @param ast
   * @param variable the iterator variable
   * @return
   */
  private static IExpr indefiniteProduct(final IAST ast, IExpr variable) {
    IExpr result = F.Power(ast.arg1(), F.Plus(F.CN1, variable));
    int argSize = ast.argSize();
    if (argSize == 2) {
      return result;
    }
    IASTAppendable newSum = ast.removeAtClone(argSize);
    newSum.set(1, result);
    return newSum;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public IExpr numericEval(final IAST functionList, EvalEngine engine) {
    return evaluate(functionList, engine);
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.HOLDALL);
    MATCHER1 = Suppliers.memoize(ProductRules::init1);
  }
}
