package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.JASModular;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <h2>FactorList</h2>
 *
 * <pre>
 * <code>FactorList(poly)
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives a list of the factors of a polynomial, together with their exponents.
 * </p>
 * </blockquote>
 *
 * <h3>Details and Options</h3>
 * <ul>
 * <li>The first element of the list is always the overall numerical factor. It is {1,1} if there is
 * no overall numerical factor.</li>
 * <li>FactorList takes the following options: Extension, GaussianIntegers, Modulus, Trig.</li>
 * <li>FactorList(poly, Modulus-&gt;p) requires that p be prime.</li>
 * <li>FactorList(poly, Extension-&gt;{a1, a2, ...}) allows coefficients that are arbitrary rational
 * combinations of the ai.</li>
 * </ul>
 */
public class FactorList extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr arg1 = ast.arg1();

    // Apply TrigExpand if Trig->True
    if (options[3].isTrue()) {
      arg1 = TrigExpand.trigExpand(arg1, engine);
    }

    if (options[0].isInteger() && JASModular.isModpMessage(S.FactorList, options[0])) {
      return F.NIL;
    }

    if (arg1.isList()) {
      // a list is no polynomial: it is the one factor
      return F.list(F.list(F.C1, F.C1), F.list(arg1, F.C1));
    }

    boolean field = options[1] != S.None || options[2].isTrue();
    if (field && options[0].isZero()) {
      IAST list = org.matheclipse.core.builtin.Algebra.Factor.factorListNumberField(arg1,
          options[1], options[2].isTrue(), engine);
      if (list.isPresent()) {
        return list;
      }
    }

    // Reconstruct the Factor AST to delegate the primary evaluation
    IASTAppendable factorAST = F.ast(S.Factor);
    factorAST.append(arg1);

    // Transfer options from the original AST, skipping 'Trig'
    for (int i = 2; i <= ast.argSize(); i++) {
      IExpr arg = ast.get(i);
      // if (arg.isRuleAST() && arg.first().equals(S.Trig)) {
      // continue;
      // }
      factorAST.append(arg);
    }

    // Delegate to standard polynomial factoring
    IExpr factored = engine.evaluate(factorAST);

    // If Factor remains unevaluated, FactorList should also remain unevaluated
    if (factored.isAST(S.Factor)) {
      return F.NIL;
    }

    // with a field option an algebraic number in front belongs to the content: {{Sqrt(2),1},...}
    return splitSymbolicExponents(factorListFromFactored(factored, field), engine);
  }

  /**
   * A factor with a symbolic exponent <code>b^(n+k*t)</code> is the factor <code>b^t</code> with
   * exponent <code>k</code> and the factor <code>b</code> with exponent <code>n</code>:
   * <code>(x+y)^(-3+3*e)</code> gives <code>{(x+y)^e, 3}</code> and <code>{x+y, -3}</code>.
   */
  private static IExpr splitSymbolicExponents(IExpr factorList, EvalEngine engine) {
    if (!factorList.isList() || !((IAST) factorList)
        .exists(x -> x.isList2() && !x.second().isNumber() && (x.second().isPlus() || x.second().isTimes()))) {
      return factorList;
    }
    IAST list = (IAST) factorList;
    IASTAppendable result = F.ListAlloc(list.size() + 2);
    for (IExpr pair : list) {
      if (!pair.isList2() || pair.second().isNumber()) {
        result.append(pair);
        continue;
      }
      IExpr base = pair.first();
      IExpr exponent = pair.second();
      IAST terms = exponent.isPlus() ? (IAST) exponent : F.list(exponent);
      if (!terms.forAll(t -> t.isInteger() || (t.isTimes() && t.first().isInteger()) || !t.isNumber())) {
        result.append(pair);
        continue;
      }
      IExpr integerPart = F.C0;
      for (IExpr term : terms) {
        if (term.isInteger()) {
          integerPart = term;
        } else if (term.isTimes() && term.first().isInteger()) {
          result.append(F.list(engine.evaluate(F.Power(base, term.rest().oneIdentity1())),
              term.first()));
        } else {
          result.append(F.list(engine.evaluate(F.Power(base, term)), F.C1));
        }
      }
      if (!integerPart.isZero()) {
        result.append(F.list(base, integerPart));
      }
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // not Listable: FactorList({x^2-4, x}) has the list as its one factor
    setOptions(newSymbol, Algebra.Factor.factorOptionKeys(), Algebra.Factor.factorOptionValues());
  }

  /**
   * Extract the factors and exponents from a factored polynomial expression. * @param factored the
   * fully factored polynomial expression
   * 
   * @return a list of factors and their exponents with the numeric constant as the first element
   */
  private static IAST factorListFromFactored(IExpr factored, boolean field) {
    IASTAppendable result = F.ListAlloc();

    if (factored.isNumber()) {
      // Constant numbers have no further polynomial factors. A rational is split into its signed
      // numerator (exponent 1) and denominator (exponent -1), e.g. 3/4 -> {{3,1},{4,-1}}.
      if (factored.isRational()) {
        appendRationalFactors(result, (IRational) factored);
      } else {
        result.append(F.List(factored, F.C1));
      }
      return result;
    } else if (factored.isTimes()) {
      IAST times = (IAST) factored;
      IExpr numericFactor = F.C1;

      // First pass: accumulate numeric factors for the content block
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr arg = times.get(i);
        if (isContent(arg, field)) {
          numericFactor = field ? EvalEngine.get().evaluate(F.Times(numericFactor, arg))
              : numericFactor.times(arg);
        }
      }
      // The numeric content becomes the leading factor(s). A rational content is split into
      // numerator/denominator entries, e.g. (x^2-1)/2 contributes {2,-1}; a pure numeric 1
      // still yields the conventional leading {1,1}.
      if (numericFactor.isRational()) {
        appendRationalFactors(result, (IRational) numericFactor);
      } else {
        result.append(F.List(numericFactor, F.C1));
      }

      // Second pass: append non-numeric polynomial factors
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr arg = times.get(i);
        if (!isContent(arg, field)) {
          appendFactor(result, arg);
        }
      }
    } else {
      // Single compound factor
      result.append(F.List(F.C1, F.C1));
      appendFactor(result, factored);
    }

    return leadingTermsPositive(result);
  }

  /** A number, or over a number field any numeric factor such as <code>Sqrt(2)</code>. */
  private static boolean isContent(IExpr factor, boolean field) {
    return factor.isNumber() || (field && factor.isNumericFunction(true));
  }

  /**
   * Every factor which is a sum is written with a positive leading term; the signs which are
   * taken out of the factors go into the numeric factor in front: <code>-x+y</code> is
   * <code>{{-1, 1}, {x-y, 1}}</code>.
   */
  private static IAST leadingTermsPositive(IASTAppendable factors) {
    if (factors.argSize() < 2 || !factors.arg1().first().isRational()
        || !factors.arg1().second().isOne()) {
      return factors;
    }
    EvalEngine engine = EvalEngine.get();
    boolean negative = false;
    for (int i = 2; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i).first();
      IExpr exponent = factors.get(i).second();
      if (factor.isPlus() && exponent.isInteger()
          && org.matheclipse.core.eval.AlgebraUtil.hasNegativeLeadingTerm((IAST) factor, engine)) {
        IExpr negated = engine.evaluate(F.Distribute(F.Times(F.CN1, factor)));
        if (negated.isPlus()) {
          factors.set(i, F.List(negated, exponent));
          negative ^= ((org.matheclipse.core.interfaces.IInteger) exponent).isOdd();
        }
      }
    }
    if (negative) {
      factors.set(1, F.List(factors.arg1().first().negate(), F.C1));
    }
    return factors;
  }

  /**
   * Appends the appropriate {base, exponent} list to the result. Prevents splitting when the base
   * is a built-in mathematical constant or number.
   */
  private static void appendFactor(IASTAppendable result, IExpr arg) {
    if (arg.isPower()) {
      IExpr base = arg.base();
      IExpr exponent = arg.exponent();
      // Distribute a factored base over the power, e.g. ((-1+x)*(1+x))^-1 -> {-1+x,-1},{1+x,-1}
      if (base.isTimes()) {
        IAST times = (IAST) base;
        for (int i = 1; i <= times.argSize(); i++) {
          appendFactor(result, F.Power(times.get(i), exponent));
        }
        return;
      }
      // If the base is a numeric constant, treat the entire Power as a single algebraic generator
      if (base.isNumber() || base == S.E || base == S.Pi || base == S.I) {
        result.append(F.List(arg, F.C1));
      } else {
        result.append(F.List(base, exponent));
      }
    } else {
      result.append(F.List(arg, F.C1));
    }
  }

  /**
   * Splits a rational number into its {@code FactorList} entries: the signed integer numerator with
   * exponent {@code 1} and, for a proper fraction, the positive denominator with exponent
   * {@code -1}. A bare {@code +1} numerator of a proper fraction is omitted, so {@code 1/2} yields
   * only {@code {2,-1}}, while an integer (denominator {@code 1}) is always emitted, so {@code 1}
   * yields {@code {1,1}}.
   *
   * @param result the list the {base, exponent} entries are appended to
   * @param rational the rational number to split
   */
  private static void appendRationalFactors(IASTAppendable result, IRational rational) {
    IInteger numerator = rational.numerator();
    IInteger denominator = rational.denominator();
    if (denominator.isOne()) {
      result.append(F.List(numerator, F.C1));
    } else {
      if (!numerator.isOne()) {
        result.append(F.List(numerator, F.C1));
      }
      result.append(F.List(denominator, F.CN1));
    }
  }
}
