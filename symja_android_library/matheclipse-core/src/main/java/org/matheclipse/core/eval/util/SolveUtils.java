package org.matheclipse.core.eval.util;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.ExprAnalyzer;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.Solve;

public class SolveUtils {

  /**
   * Rewrite an <code>And(...)</code> of equations into the equivalent <code>List(...)</code> of
   * equations.
   *
   * <p>
   * The solvers accept their equations either as a list or combined with the
   * <code>&amp;&amp;</code> operator, for example
   * <code>DSolve(y'(x)==y(x) &amp;&amp; y(0)==1, y(x), x)</code>.
   *
   * @param expr the equations argument of a solver
   * @return the list of equations, or <code>expr</code> unchanged if it wasn't an <code>And(...)
   *     </code> expression
   */
  public static IExpr toEquationList(IExpr expr) {
    if (expr.isAnd()) {
      return ((IAST) expr).apply(S.List);
    }
    return expr;
  }

  /**
   * The rules of the first solution of a <code>Solve</code> style result.
   *
   * @param solveResult a result of the shape <code>{{x-&gt;1,y-&gt;2},{x-&gt;3,y-&gt;4}}</code>
   * @return the rules of the first solution, or {@link F#NIL} if there is no solution
   */
  public static IAST firstSolutionRules(IExpr solveResult) {
    if (solveResult.isListOfLists() && solveResult.argSize() >= 1) {
      return (IAST) solveResult.first();
    }
    return F.NIL;
  }

  /**
   * The value of the first rule of every solution of a <code>Solve</code> style result.
   *
   * <p>
   * Used where the caller solved for a single unknown and wants the values it can take, so that
   * <code>{{y-&gt;1},{y-&gt;-1}}</code> becomes <code>{1,-1}</code>.
   *
   * @param solveResult a result of the shape <code>{{y-&gt;1},{y-&gt;-1}}</code>
   * @return the values, which is an empty list if the result had no solution of that shape
   */
  public static IAST firstRuleValues(IExpr solveResult) {
    IASTAppendable results = F.ListAlloc();
    if (solveResult.isList()) {
      IAST solutions = (IAST) solveResult;
      for (int i = 1; i <= solutions.argSize(); i++) {
        IExpr solution = solutions.get(i);
        if (solution.isList() && ((IAST) solution).argSize() >= 1
            && ((IAST) solution).arg1().isRule()) {
          results.append(((IAST) solution).arg1().second());
        }
      }
    }
    return results;
  }

  /**
   * <code>result[0]</code> is the list of expressions <code>== 0</code> . <code>result[1]</code>are
   * the <code>Unequal, Less, LessEqual, Greater, GreaterEqual</code> expressions. If <code>
   * result[2].isPresent()</code> return the entry as solution.
   *
   * @param list
   * @param solution
   * @param isNumeric set isNumeric[0] = true, if an expression must be rationalized
   * @return
   */
  public static IASTMutable[] filterSolveLists(IAST list, IAST solution, boolean[] isNumeric) {
    // if numeric is true we use NSolve instead of SOlve
    boolean numeric = isNumeric[0];
    IASTMutable[] result = new IASTMutable[3];
    IASTAppendable termsEqualZero = F.ListAlloc(list.size());
    IASTAppendable inequalityTerms = F.ListAlloc(list.size());
    result[0] = termsEqualZero;
    result[1] = inequalityTerms;
    result[2] = F.NIL;
    int i = 1;
    while (i < list.size()) {
      IExpr arg = list.get(i);
      if (arg.isTrue()) {
      } else if (arg.isFalse()) {
        // no solution possible
        result[2] = F.ListAlloc();
        return result;
      } else if (arg.isEqual()) {
        // arg must be Equal(_, 0)
        IExpr arg1 = arg.first();
        if (numeric) {
          // NSolve
          termsEqualZero.append(arg1);
        } else {
          // Solve
          IExpr temp = AbstractFractionSym.rationalize(arg1, false);
          if (temp.isPresent()) {
            isNumeric[0] = true;
            termsEqualZero.append(temp);
          } else {
            termsEqualZero.append(arg1);
          }
        }
      } else {
        inequalityTerms.append(arg);
      }
      i++;
    }
    EvalAttributes.sort(result[0]);
    EvalAttributes.sort(result[1]);
    if (result[0].isEmpty() && result[1].isEmpty()) {
      if (solution.isPresent()) {
        result[2] = solution.copy();
      } else {
        result[2] = F.unary(S.List, F.List());
      }
      return result;
    }
    return result;
  }

  /**
   * Collect constant values from a conditional expression or a constant value that satisfy the
   * condition
   * <code>lower &quot;lowerSymbol&quot; constantValue &quot;upperSymbol&quot; upper</code>. *
   * 
   * @param valueExpr
   * @param lower
   * @param upper
   * @param lowerSymbol {@link S#Less} or {@link S#LessEqual}
   * @param upperSymbol {@link S#Less} or {@link S#LessEqual}
   * @param collector
   * @param engine
   */
  public static void collectConstants(IExpr valueExpr, IExpr lower, IExpr upper,
      IBuiltInSymbol lowerSymbol, IBuiltInSymbol upperSymbol, IASTAppendable collector,
      EvalEngine engine) {
    if (valueExpr.isConditionalExpression()) {
      IAST ast = (IAST) valueExpr;
      IExpr val = ast.arg1();
      IExpr condition = ast.arg2();
      IExpr integersDomainVariable = F.NIL;
      if (condition.isAST(S.Element, 3) && condition.second() == S.Integers) {
        integersDomainVariable = condition.first();
      } else if (condition.isAnd()) {
        for (int i = 1; i < condition.size(); i++) {
          IExpr arg = condition.get(i);
          if (arg.isAST(S.Element, 3) && arg.second() == S.Integers) {
            integersDomainVariable = arg.first();
            break;
          }
        }
      }

      if (integersDomainVariable.isPresent()) {
        IExpr[] coeffs = val.linear(integersDomainVariable);
        if (coeffs != null) {
          IExpr c0 = coeffs[0];
          IExpr c1 = coeffs[1];
          IExpr minK = F.NIL;
          IExpr maxK = F.NIL;
          if (engine.evalTrue(F.GreaterEqual(c1, F.C0))) {
            minK = F.Ceiling(F.Divide(F.Subtract(lower, c0), c1));
            maxK = F.Floor(F.Divide(F.Subtract(upper, c0), c1));
          } else if (engine.evalTrue(F.LessEqual(c1, F.C0))) {
            minK = F.Ceiling(F.Divide(F.Subtract(upper, c0), c1));
            maxK = F.Floor(F.Divide(F.Subtract(lower, c0), c1));
          } else if (c1.isZero()) {
            collectConstants(c0, lower, upper, lowerSymbol, upperSymbol, collector, engine);
            return;
          }

          if (minK.isPresent() && maxK.isPresent()) {
            IExpr lowerInt = engine.evaluate(minK);
            IExpr upperInt = engine.evaluate(maxK);
            long start = lowerInt.toLongDefault();
            long end = upperInt.toLongDefault();
            if (F.isPresent(start) && F.isPresent(end)) {
              for (long k = start; k <= end; k++) {
                IExpr valK = F.Plus(c0, F.Times(c1, F.ZZ(k)));
                valueExpr = engine.evaluate(valK);
                if (engine.evalTrue(F.And(//
                    F.binaryAST2(lowerSymbol, lower, valueExpr),
                    F.binaryAST2(upperSymbol, valueExpr, upper)))) {
                  collector.append(valueExpr);
                }
              }
            }
          }
        }
        return;
      } else if (engine.evalTrue(condition)) {
        collectConstants(val, lower, upper, lowerSymbol, upperSymbol, collector, engine);
        return;
      }
    }

    if (engine.evalTrue(F.And(//
        F.binaryAST2(lowerSymbol, lower, valueExpr),
        F.binaryAST2(upperSymbol, valueExpr, upper)))) {
      collector.append(valueExpr);
    }
  }

  /**
   * Substitute the dummy {@link ExprAnalyzer#$InverseFunction} in the <code>expr</code> with the
   * inverse function associated with the <code>symbol</code>.
   *
   * <p>
   * Delegates to the neutral {@link InverseFunctionExpander#substitute$InverseFunction(IExpr)}
   * utility.
   *
   * @param expr
   * @return
   */
  public static IExpr substitute$InverseFunction(IExpr expr) {
    return InverseFunctionExpander.substitute$InverseFunction(expr);
  }

  /**
   * Solve the equations in the residue class ring <code>Z / modulus Z</code>, i.e. determine all
   * variable assignments from <code>{0, 1, ..., modulus-1}</code> which fulfill every equation
   * modulo <code>modulus</code>.
   *
   * <p>
   * The complete residue system is enumerated, so the result is exact for arbitrary (also
   * non-prime) moduli, as long as the search space <code>modulus ^ numberOfVariables</code> stays
   * below {@link #MAX_MODULUS_SEARCH_SPACE}. Only polynomial equations with rational coefficients
   * are solved, all other systems are returned unevaluated.
   *
   * @param ast the <code>Solve(...)</code> ast
   * @param userDefinedVariables the variables the user asked to solve for
   * @param modulusOption the value of the {@link S#Modulus} option
   * @param engine the evaluation engine
   * @return the (possibly empty) list of solution lists or {@link F#NIL} if the system cannot be
   *         solved this way
   */
  public static IExpr solveModulus(IAST termsList, IAST userDefinedVariables,
      IExpr modulusOption, ISymbol reportingSymbol, EvalEngine engine) {
    int modulus = modulusOption.toIntDefault();
    if (!modulusOption.isInteger() || modulus < 1) {
      // Value of option `1` should be a prime number or zero.
      return Errors.printMessage(reportingSymbol, "modp", F.List(F.Rule(S.Modulus, modulusOption)),
          engine);
    }
    IInteger modulusValue = (IInteger) modulusOption;

    IASTMutable[] lists = SolveUtils.filterSolveLists(termsList, F.NIL, new boolean[] {false});
    if (lists[2].isPresent()) {
      // either no solution possible or no equations at all
      return lists[2];
    }
    if (lists[1].argSize() > 0) {
      // inequalities aren't defined in a residue class ring
      return Errors.printMessage(reportingSymbol, "nsmet", F.list(reportingSymbol), engine);
    }
    IAST termsEqualZeroList = lists[0];

    // only the variables which really occur in the equations are enumerated; the remaining
    // user defined variables are unconstrained and therefore not part of the solution rules
    VariablesSet equationVariables = new VariablesSet(termsEqualZeroList);
    IAST varList = equationVariables.getVarList();
    for (int i = 1; i < varList.size(); i++) {
      if (!userDefinedVariables.contains(varList.get(i))) {
        // the equations contain a symbol which shouldn't be solved for
        return Errors.printMessage(reportingSymbol, "nsmet", F.list(reportingSymbol), engine);
      }
    }
    // A fraction is zero exactly where its numerator is zero. Together() additionally clears the
    // denominators of rational coefficients, so that an equation like 2*x == 3 - which the
    // evaluator already normalized to x == 3/2 - is enumerated as the integer polynomial 2*x-3.
    IASTAppendable numerators = F.ListAlloc(termsEqualZeroList.argSize());
    IASTAppendable denominators = F.ListAlloc(termsEqualZeroList.argSize());
    for (int i = 1; i < termsEqualZeroList.size(); i++) {
      IExpr together = S.Together.of(engine, termsEqualZeroList.get(i));
      IExpr numerator = S.Numerator.of(engine, together);
      IExpr denominator = S.Denominator.of(engine, together);
      if (!isModulusPolynomial(numerator, varList, engine)
          || !isModulusPolynomial(denominator, varList, engine)) {
        // only polynomials with rational coefficients have a meaning in a residue class ring
        return Errors.printMessage(reportingSymbol, "nsmet", F.list(reportingSymbol), engine);
      }
      numerators.append(numerator);
      denominators.append(denominator);
    }

    IASTAppendable searchVariables = F.ListAlloc(userDefinedVariables.argSize());
    long searchSpace = 1L;
    for (int i = 1; i < userDefinedVariables.size(); i++) {
      IExpr variable = userDefinedVariables.get(i);
      if (equationVariables.contains(variable)) {
        searchVariables.append(variable);
        searchSpace *= modulus;
        if (searchSpace > MAX_MODULUS_SEARCH_SPACE) {
          // The system cannot be solved with the methods available to Solve.
          return Errors.printMessage(reportingSymbol, "nsmet", F.list(reportingSymbol), engine);
        }
      }
    }

    int numberOfVariables = searchVariables.argSize();
    int[] residues = new int[numberOfVariables];
    IASTAppendable result = F.ListAlloc();
    do {
      IASTAppendable rules = F.ListAlloc(numberOfVariables);
      for (int i = 0; i < numberOfVariables; i++) {
        rules.append(F.Rule(searchVariables.get(i + 1), F.ZZ(residues[i])));
      }
      if (isModulusSolution(numerators, denominators, rules, modulusValue, engine)) {
        result.append(rules);
      }
    } while (nextResidues(residues, modulus));
    return result;
  }

  /**
   * The maximum number of variable assignments which are enumerated for the {@link S#Modulus}
   * option, i.e. <code>modulus ^ numberOfVariables</code> must not exceed this limit.
   */
  private static final long MAX_MODULUS_SEARCH_SPACE = 1_000_000L;


  /**
   * Test if <code>expr</code> is a polynomial in the given variables and if all its coefficients
   * are rational numbers. Only such an expression can be mapped into a residue class ring.
   *
   * @param expr the expression to test
   * @param varList the variables of the polynomial
   * @param engine the evaluation engine
   * @return <code>true</code> if <code>expr</code> is a polynomial with rational coefficients
   */
  private static boolean isModulusPolynomial(IExpr expr, IAST varList, EvalEngine engine) {
    if (!expr.isPolynomial(varList)) {
      return false;
    }
    if (varList.isEmpty()) {
      return expr.isRational();
    }
    IExpr coefficientRules = S.CoefficientRules.of(engine, expr, varList);
    if (!coefficientRules.isList()) {
      return false;
    }
    return ((IAST) coefficientRules).forAll(rule -> rule.isRule() && rule.second().isRational());
  }

  /**
   * Test if all numerators are divisible by <code>modulus</code> after substituting the variables
   * with the values of the given <code>rules</code>. A residue for which a denominator becomes
   * divisible by <code>modulus</code> isn't a solution, because the corresponding term isn't
   * defined in the residue class ring.
   *
   * @param numerators the numerators of the expressions which should become <code>0</code>
   * @param denominators the corresponding denominators
   * @param rules the list of <code>variable -> residue</code> rules
   * @param modulus the modulus
   * @param engine the evaluation engine
   * @return <code>true</code> if every term is <code>0</code> modulo <code>modulus</code>
   */
  private static boolean isModulusSolution(IAST numerators, IAST denominators, IAST rules,
      IInteger modulus, EvalEngine engine) {
    for (int i = 1; i < numerators.size(); i++) {
      IExpr denominator = denominators.get(i);
      if (!denominator.isOne()
          && engine.evaluate(F.Mod(F.subst(denominator, rules), modulus)).isZero()) {
        // the term isn't defined for this residue
        return false;
      }
      IExpr numerator = F.subst(numerators.get(i), rules);
      if (!engine.evaluate(F.Mod(numerator, modulus)).isZero()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Step to the next tuple of the complete residue system in lexicographical order, i.e. increment
   * the last entry of <code>residues</code> and carry over to the entries on the left.
   *
   * @param residues the current tuple; modified in place
   * @param modulus the modulus
   * @return <code>false</code> if the last tuple was already reached
   */
  private static boolean nextResidues(int[] residues, int modulus) {
    for (int i = residues.length - 1; i >= 0; i--) {
      if (++residues[i] < modulus) {
        return true;
      }
      residues[i] = 0;
    }
    return false;
  }
}
