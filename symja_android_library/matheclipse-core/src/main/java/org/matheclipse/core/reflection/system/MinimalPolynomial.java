package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * MinimalPolynomial(s, x) * Gives the minimal polynomial in x for the algebraic number s.
 */
public class MinimalPolynomial extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr s = ast.arg1();
    IExpr x = ast.arg2();

    if (!x.isSymbol()) {
      return F.NIL;
    }

    if (s.isRational()) {
      return normalizePolynomial(F.Subtract(x, s), x, engine);
    }

    IASTAppendable variables = F.ListAlloc();
    IASTAppendable equations = F.ListAlloc();
    Map<IExpr, ISymbol> radicalCache = new HashMap<>();
    int[] counter = new int[] {1};

    // Extract radicals, complex units, and negative exponents into pure polynomials
    IExpr modifiedS = extractRadicals(s, equations, variables, engine, radicalCache, counter);

    if (variables.argSize() == 0) {
      return normalizePolynomial(F.Subtract(x, modifiedS), x, engine);
    }

    // The main mapping equation (guaranteed to be a polynomial now)
    equations.append(engine.evaluate(F.Subtract(x, modifiedS)));
    variables.append(x);

    // Compute the Groebner basis to eliminate the dummy variables
    IExpr gb = engine.evaluate(F.GroebnerBasis(equations, variables));

    if (gb.isList()) {
      IAST gbList = (IAST) gb;
      for (int i = 1; i <= gbList.argSize(); i++) {
        IExpr poly = gbList.get(i);

        // Find the elimination ideal generator (the polynomial free of our generated variables)
        boolean onlyX = true;
        for (int j = 1; j < variables.argSize(); j++) {
          if (!poly.isFree(variables.get(j), true)) {
            onlyX = false;
            break;
          }
        }
        if (onlyX) {
          return normalizePolynomial(poly, x, engine);
        }
      }
    }

    return F.NIL;
  }

  /**
   * Normalizes the polynomial to an integer polynomial with a positive leading coefficient.
   */
  private static IExpr normalizePolynomial(IExpr poly, IExpr x, EvalEngine engine) {
    IExpr tog = engine.evaluate(F.Together(poly));
    IExpr num = engine.evaluate(F.Numerator(tog));
    IExpr lcNum = engine.evaluate(F.Coefficient(num, x, F.Exponent(num, x)));

    if (lcNum.isNegativeResult()) {
      num = engine.evaluate(F.Expand(F.Times(F.CN1, num)));
    } else {
      num = engine.evaluate(F.Expand(num));
    }
    return num;
  }

  /**
   * Extracts radical expressions, maps same-base fractional powers to shared variables, resolves
   * complex 'I', and linearizes negative powers.
   */
  private static IExpr extractRadicals(IExpr expr, IASTAppendable equations,
      IASTAppendable variables, EvalEngine engine, Map<IExpr, ISymbol> cache, int[] counter) {
    if (expr.isRational()) {
      return expr;
    }

    // Handle complex numbers (e.g. 2 - I)
    if (expr.isComplex()) {
      IComplex c = (IComplex) expr;
      IExpr re = engine.evaluate(c.re());
      IExpr im = engine.evaluate(c.im());
      IExpr key = S.I;
      ISymbol var = cache.get(key);
      if (var == null) {
        var = F.$s("r" + counter[0]++);
        cache.put(key, var);
        variables.append(var);
        // Introduce relation for I: var^2 + 1 == 0
        equations.append(F.Plus(F.Sqr(var), F.C1));
      }
      return engine.evaluate(F.Plus(re, F.Times(im, var)));
    }

    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();

      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Power: {
            if (ast.arg2().isRational()) {
              IRational exponent = (IRational) ast.arg2();
              if (!exponent.isInteger()) {
                IExpr base =
                    extractRadicals(ast.arg1(), equations, variables, engine, cache, counter);
                IInteger num = exponent.numerator();
                IInteger den = exponent.denominator();

                // Cache key representing base^(1/den)
                IExpr key = F.Power(base, F.QQ(F.C1, den));
                ISymbol var = cache.get(key);

                if (var == null) {
                  var = F.$s("r" + counter[0]++);
                  cache.put(key, var);
                  variables.append(var);

                  // Add relation: var^den - base == 0
                  IExpr eq = engine.evaluate(F.Subtract(F.Power(var, den), base));
                  equations.append(eq);
                }

                // Linearize negative fractional exponents (e.g. 1/Sqrt(5))
                if (num.isNegativeResult()) {
                  IExpr posNum = engine.evaluate(F.Times(F.CN1, num));
                  IExpr invKey = F.Power(var, num);
                  ISymbol invVar = cache.get(invKey);
                  if (invVar == null) {
                    invVar = F.$s("r" + counter[0]++);
                    cache.put(invKey, invVar);
                    variables.append(invVar);
                    // Relation: invVar * var^posNum - 1 == 0
                    equations.append(
                        engine.evaluate(F.Subtract(F.Times(invVar, F.Power(var, posNum)), F.C1)));
                  }
                  return invVar;
                } else {
                  return F.Power(var, num);
                }
              } else if (exponent.isNegativeResult()) {
                // Linearize negative integer exponents (e.g. base^(-2))
                IExpr base =
                    extractRadicals(ast.arg1(), equations, variables, engine, cache, counter);
                IExpr posNum = engine.evaluate(F.Times(F.CN1, exponent));
                IExpr invKey = F.Power(base, exponent);
                ISymbol invVar = cache.get(invKey);
                if (invVar == null) {
                  invVar = F.$s("r" + counter[0]++);
                  cache.put(invKey, invVar);
                  variables.append(invVar);
                  // Relation: invVar * base^posNum - 1 == 0
                  equations.append(
                      engine.evaluate(F.Subtract(F.Times(invVar, F.Power(base, posNum)), F.C1)));
                }
                return invVar;
              }
            }

            IASTAppendable powerAlloc = F.ast(head, ast.argSize());
            for (int i = 1; i <= ast.argSize(); i++) {
              powerAlloc.append(
                  extractRadicals(ast.get(i), equations, variables, engine, cache, counter));
            }
            return powerAlloc;
          }
          case ID.Plus: {
            IASTAppendable plusAlloc = F.PlusAlloc(ast.argSize());
            for (int i = 1; i <= ast.argSize(); i++) {
              plusAlloc.append(
                  extractRadicals(ast.get(i), equations, variables, engine, cache, counter));
            }
            return plusAlloc;
          }
          case ID.Times: {
            IASTAppendable timesAlloc = F.TimesAlloc(ast.argSize());
            for (int i = 1; i <= ast.argSize(); i++) {
              timesAlloc.append(
                  extractRadicals(ast.get(i), equations, variables, engine, cache, counter));
            }
            return timesAlloc;
          }
          default:
            break;
        }
      }

      // Default AST handling
      IASTAppendable newAST = F.ast(head, ast.argSize());
      for (int i = 1; i <= ast.argSize(); i++) {
        newAST.append(extractRadicals(ast.get(i), equations, variables, engine, cache, counter));
      }
      return newAST;
    }
    return expr;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE);
  }
}
