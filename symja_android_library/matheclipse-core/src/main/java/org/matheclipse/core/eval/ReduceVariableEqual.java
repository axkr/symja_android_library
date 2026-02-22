package org.matheclipse.core.eval;

import java.util.function.Predicate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.InverseFunction;

/**
 * Try to reduce a <code>variable</code> for the {@link F#Equal(IExpr, IExpr)} relation.
 */
public class ReduceVariableEqual {
  /**
   * Convert a list of rules for one variable back to a list of values.
   * 
   * @param listOfRules
   * @param multipleValues if <code>false</code> return only the first found value in the list
   * @return
   */
  protected static IExpr listOfRulesToValues(IExpr listOfRules, IExpr variable,
      boolean multipleValues) {
    if (multipleValues) {
      IASTAppendable solveValues = F.ListAlloc(listOfRules.size());
      ((IAST) listOfRules).map(a -> {
        if (a.isList1() //
            && a.first().isRuleAST() && a.first().first().equals(variable)) {
          solveValues.append(a.first().second());
        }
        return F.NIL;
      });
      if (solveValues.size() > 1) {
        return solveValues;
      }
    } else {
      if (listOfRules.first().isRuleAST() //
          && listOfRules.first().equals(variable)) {
        return listOfRules.first().second();
      }
    }
    return F.NIL;
  }

  /**
   * Print message "Inverse functions are being used. Values may be lost for multivalued inverses."
   *
   * @param engine
   */
  private static void printIfunMessage(EvalEngine engine) {
    Errors.printMessage(S.InverseFunction, "ifun", F.CEmptyList, engine);
  }

  // /**
  // * Print message "Inverse functions are being used. Values may be lost for multivalued
  // inverses."
  // * and return the {@code result} by substituting {@code subExpr} with {@code replacementExpr}.
  // *
  // * @param result
  // * @param subExpr
  // * @param replacementExpr
  // * @param multipleValues if <code>true</code> multiple results are returned as lsit of values
  // * @param engine
  // * @return
  // */
  // private static IExpr resultWithIfunMessage(IExpr result, IExpr subExpr, IExpr replacementExpr,
  // boolean multipleValues, EvalEngine engine) {
  // printIfunMessage(engine);
  // IExpr expr = F.subst(result, subExpr, replacementExpr);
  // if (!multipleValues && expr.isList() && expr.size() > 1) {
  // return expr.first();
  // }
  // return expr;
  // }

  // /**
  // * <p>
  // * Solve <code>a*E^(b*f(x))*f(x) == z </code> as
  // * <code>InverseFunction(f, 1, 1)[ProductLog((b*z)/a)/b]</code>
  // *
  // * <p>
  // * See: <a href=
  // *
  // "https://de.wikipedia.org/wiki/Lambertsche_W-Funktion#Verwendung_au%C3%9Ferhalb_der_Kombinatorik">DE:Wikipedia
  // * - Lambertsche_W-Funktion Verwendung_ausserhalb der Kombinatorik</a>
  // *
  // * @param exprWithVariable the left-hand-side expression which contains a variable
  // * @param exprWithoutVariable the right-hand-side expression which contains no variable
  // * @param variable the variable
  // * @param multipleValues
  // * @param engine
  // * @return
  // */
  // private static IExpr solveLambertWEquation(IAST exprWithVariable, IExpr exprWithoutVariable,
  // IExpr variable, boolean multipleValues, EvalEngine engine) {
  // if (exprWithVariable.isTimes()) {
  // IASTAppendable[] variableFilter = exprWithVariable.filter(x -> x.isFree(variable));
  // int expIndexOf = variableFilter[1].indexOf(x -> x.isExp());
  // if (expIndexOf > 0) {
  // IExpr variableFunction1 = variableFilter[1].removeAtCopy(expIndexOf).oneIdentity1();
  // if (variableFunction1.isAST1()) {
  // IExpr expFunction = variableFilter[1].get(expIndexOf);
  //
  // IExpr a = variableFilter[0].oneIdentity1();
  // IExpr b = F.C1;
  // IExpr z = exprWithoutVariable;
  //
  // IExpr variableFunction2 = expFunction.exponent();
  // if (variableFunction2.isTimes()) {
  // IASTAppendable[] f1TimesFilter =
  // ((IAST) variableFunction2).filter(x -> x.isFree(variable));
  // if (f1TimesFilter[0].argSize() > 0) {
  // b = f1TimesFilter[0].oneIdentity1();
  // variableFunction2 = f1TimesFilter[1].oneIdentity1();
  // }
  // }
  //
  // if (variableFunction1.equals(variableFunction2)) {
  // IExpr head = variableFunction1.head();
  // // ProductLog((b*z)/a)
  // IAST lambertW = F.ProductLog(F.Times(a.inverse(), b, z));
  // IExpr result =
  // F.unaryAST1(F.InverseFunction(head, F.C1, F.C1), F.Times(b.inverse(), lambertW));
  // return resultWithIfunMessage(result, variable, exprWithoutVariable, multipleValues,
  // engine);
  // }
  // }
  // }
  // }
  // return F.NIL;
  // }

  // /**
  // * <p>
  // * Check if the <code>plusAST</code> has 2 arguments and try a {@link F#PowerExpand(IExpr)}
  // * transformation step on the args.
  // *
  // * <p>
  // * See: <a href="//
  // *
  // https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">Solving
  // * Symbolic Equations with PRESS</a>
  // *
  // * @param plusAST
  // * @param variable
  // * @param multipleValues
  // * @param engine
  // * @return
  // */
  // private static IExpr tryPowerExpand(IAST plusAST, IExpr variable, boolean multipleValues,
  // EvalEngine engine) {
  // if (plusAST.argSize() == 2) {
  // // powerExpandLHS == powerExpandRHS
  // IExpr powerExpandLHS = Algebra.powerExpand(F.Log(plusAST.first()), false);
  // IExpr powerExpandRHS = Algebra.powerExpand(F.Log(plusAST.second().negate()), false);
  // if (powerExpandLHS.isPresent() || powerExpandRHS.isPresent()) {
  // if (powerExpandLHS.isNIL()) {
  // powerExpandLHS = F.Log(plusAST.first());
  // }
  // if (powerExpandRHS.isNIL()) {
  // powerExpandRHS = F.Log(plusAST.second());
  // }
  // IExpr termsEqualZero = engine.evaluate(F.Subtract(powerExpandLHS, powerExpandRHS));
  // IASTMutable newList = F.unaryAST1(S.List, termsEqualZero);
  // Solve.SolveData solveData = new Solve.SolveData();
  // IExpr result =
  // solveData.solveRecursive(newList, F.CEmptyList, false, F.List(variable), engine);
  // if (result.isListOfLists()) {
  // // Inverse functions are being used. Values may be lost for multivalued inverses.
  // Errors.printMessage(S.Solve, "ifun", F.CEmptyList, engine);
  // return listOfRulesToValues(result, variable, multipleValues);
  // }
  // }
  // }
  // return F.NIL;
  // }

  // /**
  // * Try to solve equations which contain trigonometric functions by using a
  // * {@link F#TrigToExp(IExpr)} transformation step, so that the equation contains
  // * <code>E^(...)</code> expressions.
  // *
  // * @param plusAST
  // * @param variable
  // * @param multipleValues
  // * @param engine
  // * @return
  // */
  // private static IExpr tryTrigToExp(IAST plusAST, IExpr variable, boolean multipleValues,
  // EvalEngine engine) {
  // // System.out.println(plusAST.leafCount());
  // if (plusAST.leafCount() > Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
  // return F.NIL;
  // }
  // IExpr termsEqualZero = engine.evaluateNIL(F.TrigToExp(plusAST));
  // if (termsEqualZero.isPresent()) {
  // IASTMutable newList = F.unaryAST1(S.List, termsEqualZero);
  // Solve.SolveData solveData = new Solve.SolveData();
  // IExpr result =
  // solveData.solveRecursive(newList, F.CEmptyList, false, F.List(variable), engine);
  // if (result.isListOfLists()) {
  // // Inverse functions are being used. Values may be lost for multivalued inverses.
  // Errors.printMessage(S.Solve, "ifun", F.CEmptyList, engine);
  // return listOfRulesToValues(result, variable, multipleValues);
  // }
  // }
  // return F.NIL;
  // }

  /**
   * Reduce the <code>variable</code> from the <code>binaryRelation</code> and return the value at
   * position <code>1</code> of the result. The result contains the relation after determining the
   * value at position <code>2</code>.
   * 
   * @param binaryRelation a relational binary operation {@link S#Equal}, {@link S#Unequal},
   *        {@link S#GreaterEqual}, {@link S#LessEqual}, {@link S#Greater} or {@link S#Less}. The
   *        {@link S#GreaterEqual}, {@link S#LessEqual}, {@link S#Greater} or {@link S#Less}
   *        relation may be modified if the right-hand-side is multiplied by a negative value and
   *        returned as resulting relation at index <code>1</code>.
   * @param variable the variable which should be eliminated
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @return a pair of elements, the first element is the value for the given <code>variable</code>
   *         of {@link F#NIL} and the second is the relation after determining the value.
   */
  public static IExpr reduce(IAST binaryRelation, IExpr variable, boolean multipleValues) {
    EvalEngine engine = EvalEngine.get();
    ReduceVariableEqual rv =
        new ReduceVariableEqual(engine);
    IExpr difference = engine.evaluate(//
        F.Subtract(F.Expand(binaryRelation.first()), F.Expand(binaryRelation.second()))//
    );
    return rv.extractVariable(difference, variable, multipleValues);
  }

  private final EvalEngine engine;

  public ReduceVariableEqual() {
    this(EvalEngine.get());
  }


  public ReduceVariableEqual(EvalEngine engine) {
    this.engine = engine;
  }

  /**
   * Extract the variable from the given <code>expr</code> assuming <code>expr == 0</code>.
   *
   * @param expr an expression.
   * @param variable the variable which should be eliminated/reduced.
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @return {@link F#NIL} if we can't find an equation for the given <code>variable</code>.
   */
  private IExpr extractVariable(IExpr expr, IExpr variable, boolean multipleValues) {
    Predicate<IExpr> predicate = Predicates.in(variable);
    IExpr result = F.NIL;
    if (!expr.isFree(predicate, true)) {
      result = extractVariableRecursive(expr, F.C0, predicate, variable, multipleValues);
    }
    return result;
  }

  /**
   * Extract a value for the given <code>variabe</code>.
   *
   * @param exprWithVariable expression which contains the given <code>variabe</code>.
   * @param exprWithoutVariable expression which doesn't contain the given <code>variabe</code>.
   * @param variable the variable which should be eliminated.
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @return {@link F#NIL} if we can't find an equation for the given variable <code>x</code>.
   */
  private IExpr extractVariableRecursive(IExpr exprWithVariable, IExpr exprWithoutVariable,
      Predicate<IExpr> predicate, IExpr variable, boolean multipleValues) {
    if (exprWithVariable.equals(variable)) {
      return exprWithoutVariable;
    }
    if (!exprWithoutVariable.isSpecialsFree()) {
      return F.NIL;
    }
    if (exprWithVariable.isAST()) {
      IAST ast = (IAST) exprWithVariable;
      if (ast.isAST1()) {
        IASTAppendable inverseFunction = InverseFunction.getUnaryInverseFunction(ast, true);
        if (inverseFunction.isPresent()) {
          if (exprWithVariable.isAbs()) {
            if (exprWithoutVariable.isNonNegativeResult()) {
              Errors.printMessage(S.Solve, "ifun", F.List());
              inverseFunction.append(exprWithoutVariable);
              return extractVariableRecursive(ast.arg1(), inverseFunction, predicate, variable,
                  multipleValues);
            }
            return S.True;
          } else {
            Errors.printMessage(S.Solve, "ifun", F.List());
            // example: Sin(f(x)) == y -> f(x) == ArcSin(y)
            inverseFunction.append(exprWithoutVariable);
            return extractVariableRecursive(ast.arg1(), inverseFunction, predicate, variable,
                multipleValues);
          }
        }
      } else {
        // int size = ast.size();
        // if (size > 2 && ast.leafCount() < Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT / 2) {
        // IExpr lambertWEquationResult =
        // solveLambertWEquation(ast, exprWithoutVariable, variable, multipleValues, engine);
        // if (lambertWEquationResult.isPresent()) {
        // return lambertWEquationResult;
        // }

        // if (exprWithoutVariable.isZero() && ast.isPlus()) {
        // IAST elimZeroPlus = F.binaryAST2(elimzeroplus, ast, variable);
        // IExpr result = zeroPlusMatcher().apply(elimZeroPlus);
        // if (result.isPresent()) {
        // return resultWithIfunMessage(result, variable, exprWithoutVariable, multipleValues,
        // engine);
        // }
        // }
        // IAST elimInverse = F.binaryAST2(eliminv, ast, variable);
        // IExpr result = inverseMatcher().apply(elimInverse);
        // if (result.isPresent()) {
        // return resultWithIfunMessage(result, variable, exprWithoutVariable, multipleValues,
        // engine);
        // }
        // }

        if (ast.isPlus()) {
          // a + b + c....
          // if (exprWithoutVariable.isNumericFunction() //
          // && ast.isPolynomial(variable) && ast.isNumericFunction(variable)) {
          // IAST temp = RootsFunctions.rootsOfVariable(F.Subtract.of(ast, exprWithoutVariable),
          // F.C1, F.list(variable), engine.isNumericMode(), engine);
          // if (temp.isList() && temp.size() > 1) {
          // if (!multipleValues || temp.size() == 2) {
          // return temp.first();
          // }
          // return temp;
          // }
          // }

          IAST[] plusFilter = ast.filter(x -> x.isFree(predicate, true));
          IAST plusWithoutVariable = plusFilter[0];
          IAST plusWithVariable = plusFilter[1];
          if (plusWithoutVariable.isAST0()) {
            IExpr factor = engine.evaluateNIL(F.Factor(ast));
            if (factor.isPresent() && factor.isTimes()) {
              // a * b * c....
              IAST times = (IAST) factor;
              IAST[] timesFilter = times.filter(x -> x.isFree(predicate, true));
              IAST timesWithoutVariable = timesFilter[0];
              IAST timesWithVariable = timesFilter[1];
              if (timesWithoutVariable.isAST0()) {
                return F.NIL;
              }
              IExpr rhsWithoutVariable =
                  engine.evaluate(F.Divide(exprWithoutVariable, timesWithoutVariable));
              return extractVariableRecursive(timesWithVariable.oneIdentity1(), rhsWithoutVariable,
                  predicate, variable, multipleValues);
            }
          } else {
            IExpr rhsWithoutVariable =
                engine.evaluate(F.Subtract(exprWithoutVariable, plusWithoutVariable));
            IExpr res = extractVariableRecursive(plusWithVariable.oneIdentity0(),
                rhsWithoutVariable, predicate, variable, multipleValues);
            if (res.isPresent()) {
              return res;
            }
          }
          // if (!ast.isFree(x -> x.isTrigFunction(), true)) {
          // return tryTrigToExp(ast, variable, multipleValues, engine);
          // } else if (ast.isFree(x -> x.isLog(), true)) {
          // return tryPowerExpand(ast, variable, multipleValues, engine);
          // }
        } else if (ast.isTimes()) {
          // a * b * c....
          IAST[] timesFilter = ast.filter(x -> x.isFree(predicate, true));
          IAST timesWithoutVariable = timesFilter[0];
          IAST timesWithVariable = timesFilter[1];
          if (timesWithoutVariable.isAST0()) {
            IExpr[] numerDenom = AlgebraUtil.numeratorDenominator(ast, true, EvalEngine.get());
            if (!numerDenom[1].isOne()) {
              IExpr[] numerLinear = numerDenom[0].linear(variable);
              if (numerLinear != null) {
                IExpr[] denomLinear = numerDenom[1].linear(variable);
                if (denomLinear != null) {
                  IExpr temp = EvalEngine.get()
                      .evaluate(numerLinear[1].subtract(denomLinear[1].times(exprWithoutVariable)));
                  if (!temp.isZero()) {
                    return numerLinear[0].negate().plus(denomLinear[0].times(exprWithoutVariable))
                        .times(temp.power(-1L));
                  }
                }
              }
            }
            // no change for given expression
            return F.NIL;
          }
          IExpr value = engine.evaluate(F.Divide(exprWithoutVariable, timesWithoutVariable));
          return extractVariableRecursive(timesWithVariable.oneIdentity1(), value, predicate,
              variable, multipleValues);
        } else if (ast.isPower()) {
          IExpr base = ast.base();
          IExpr exponent = ast.exponent();
          if (exponent.isFree(predicate, true)) {
            // f(x) ^ a
            IExpr reversedPower = exponent.inverse();
            if (!reversedPower.isMathematicalIntegerNonNegative()) {
              printIfunMessage(engine);
            }
            IExpr value = engine.evaluate(F.Power(exprWithoutVariable, reversedPower));
            return extractVariableRecursive(base, value, predicate, variable, multipleValues);
          } else if (base.isFree(predicate, true)) {
            if (base.isE()) {
              // if (domain == S.Reals //
              // || exponent.isRealResult()) {
              // E ^ f(x) /; Element(f(x), Reals)
              return extractVariableRecursive(exponent, F.Log(exprWithoutVariable), predicate,
                  variable, multipleValues);
              // }
              // // E ^ f(x) /; Element(f(x), Complexes)
              // try {
              // IExpr c_n = F.C(engine.incConstantCounter());
              // // ConditionalExpression(2*I*Pi*c1 + Log(exprwovar), Element(c1, Integers))
              // final IExpr exprwovar = exprWithoutVariable;
              // IExpr expr =
              // F.Plus(F.Times(F.C2, F.CI, S.Pi, c_n), engine.evaluate(F.Log(exprwovar)));
              // IExpr temp = F.ConditionalExpression(expr, F.Element(c_n, S.Integers));
              // return extractVariableRecursive(exponent, temp, predicate, variable,
              // multipleValues);
              // } finally {
              // engine.decConstantCounter();
              // }
            }
            // a ^ f(x)
            IExpr value = F.Divide(F.Log(exprWithoutVariable), F.Log(base));
            return extractVariableRecursive(exponent, value, predicate, variable, multipleValues);
          }
        }
      }
    }
    return F.NIL;
  }

}
