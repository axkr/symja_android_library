package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
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

    return factorListFromFactored(factored);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE);

    IBuiltInSymbol[] optionKeys =
        new IBuiltInSymbol[] {S.Modulus, S.Extension, S.GaussianIntegers, S.Trig};
    IExpr[] optionValues = new IExpr[] {F.C0, S.None, S.False, S.False};
    setOptions(newSymbol, optionKeys, optionValues);
  }

  /**
   * Extract the factors and exponents from a factored polynomial expression. * @param factored the
   * fully factored polynomial expression
   * 
   * @return a list of factors and their exponents with the numeric constant as the first element
   */
  private static IAST factorListFromFactored(IExpr factored) {
    IASTAppendable result = F.ListAlloc();

    if (factored.isNumber()) {
      // Constant numbers have no further polynomial factors
      result.append(F.List(factored, F.C1));
      return result;
    } else if (factored.isTimes()) {
      IAST times = (IAST) factored;
      IExpr numericFactor = F.C1;

      // First pass: accumulate numeric factors for the content block
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr arg = times.get(i);
        if (arg.isNumber()) {
          numericFactor = numericFactor.times(arg);
        }
      }
      result.append(F.List(numericFactor, F.C1));

      // Second pass: append non-numeric polynomial factors
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr arg = times.get(i);
        if (!arg.isNumber()) {
          appendFactor(result, arg);
        }
      }
    } else {
      // Single compound factor
      result.append(F.List(F.C1, F.C1));
      appendFactor(result, factored);
    }

    return result;
  }

  /**
   * Appends the appropriate {base, exponent} list to the result. Prevents splitting when the base
   * is a built-in mathematical constant or number.
   */
  private static void appendFactor(IASTAppendable result, IExpr arg) {
    if (arg.isPower()) {
      IExpr base = arg.base();
      // If the base is a numeric constant, treat the entire Power as a single algebraic generator
      if (base.isNumber() || base.equals(S.E) || base.equals(S.Pi) || base.equals(S.I)) {
        result.append(F.List(arg, F.C1));
      } else {
        result.append(F.List(base, arg.exponent()));
      }
    } else {
      result.append(F.List(arg, F.C1));
    }
  }
}
