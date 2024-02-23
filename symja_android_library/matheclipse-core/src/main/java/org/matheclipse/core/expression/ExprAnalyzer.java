package org.matheclipse.core.expression;

import java.util.HashSet;
import java.util.Set;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.InverseFunction;

/** Analyze an expression, if it has linear, polynomial or other form. */
public class ExprAnalyzer implements Comparable<ExprAnalyzer> {

  /**
   * Use <code>Infinity</code> as an equation expression for which we get no solution (i.e. <code>
   * (-1)==0  =>  False</code>)
   */
  // private static IExpr NO_EQUATION_SOLUTION = F.CInfinity;

  public static final ISymbol $InverseFunction = F.Dummy("$InverseFunction");

  private static final IAST $InverseFunction(IBuiltInSymbol symbol, IExpr arg1) {
    return F.binaryAST2($InverseFunction, symbol, arg1);
  }

  /** A linear expression for the given variables. */
  public static final int LINEAR = 0;
  /** A polynomial expression for the given variables. */
  public static final int POLYNOMIAL = 1;
  /** Others type of expression for the given variables. */
  public static final int OTHERS = 2;

  /** LINEAR, POLYNOMIAL or OTHERS */
  private int fEquationType;

  /** The expression which should be <code>0</code>. */
  private IExpr fTogetherExpr = null;

  /** The original expression if unequal <code>null</code>. */
  private IExpr fPowerRewrittenExpr = null;

  private final IExpr fOriginalExpr;

  /** The numerator of the expression */
  private IExpr fNumerator;

  /** The denominator of the expression */
  private IExpr fDenominator;

  /**
   * The number of leaves in an expression, used as an indicator for the complexity of the
   * expression.
   */
  private long fLeafCount;

  private HashSet<IExpr> fVariableSet;
  private IASTAppendable fMatrixRow;
  private IASTAppendable fPlusAST;

  final IAST fListOfVariables;
  final boolean fGenerateConditions;;
  final EvalEngine fEngine;

  /**
   * 
   * @param function the expression which should be analyzed if it's {@link #LINEAR},
   *        {@link #POLYNOMIAL} or {@link #OTHERS}
   * @param listOfVariables a list of variables used in the <code>function</code>
   * @param engine
   */
  public ExprAnalyzer(IExpr function, IAST listOfVariables, EvalEngine engine) {
    this(function, listOfVariables, false, engine);
  }

  /**
   * 
   * @param function the expression which should be analyzed if it's {@link #LINEAR},
   *        {@link #POLYNOMIAL} or {@link #OTHERS}
   * @param listOfVariables a list of variables used in the <code>function</code>
   * @param generateConditions if <code>true</code> the option <code>GenerateConditions->True</code>
   *        is set in {@link S#Solve} or {@link S#NSolve} function call
   * @param engine
   */
  public ExprAnalyzer(IExpr function, IAST listOfVariables, boolean generateConditions,
      EvalEngine engine) {
    super();
    this.fEngine = engine;
    this.fGenerateConditions = generateConditions;
    this.fTogetherExpr = function;
    this.fOriginalExpr = function;
    this.fNumerator = function;
    this.fDenominator = F.C1;
    if (this.fTogetherExpr.isAST()) {
      splitNumeratorDenominator((IAST) this.fTogetherExpr);
    }
    this.fListOfVariables = listOfVariables;
    this.fVariableSet = new HashSet<IExpr>();
    this.fLeafCount = 0;
    reset();
  }

  /** Analyze an expression, if it has linear, polynomial or other form. */
  private int analyze(IExpr eqExpr) {
    if (eqExpr.isFree(Predicates.in(fListOfVariables), true)) {
      fLeafCount++;
      fPlusAST.append(eqExpr);
    } else if (eqExpr.isPlus()) {
      fLeafCount++;
      IAST plusAST = (IAST) eqExpr;
      IExpr expr;
      for (int i = 1; i < plusAST.size(); i++) {
        expr = plusAST.get(i);
        if (expr.isFree(Predicates.in(fListOfVariables), true)) {
          fLeafCount++;
          fPlusAST.append(expr);
        } else {
          getPlusArgumentEquationType(expr);
        }
      }
    } else {
      getPlusArgumentEquationType(eqExpr);
    }
    return fEquationType;
  }

  @Override
  public int compareTo(ExprAnalyzer o) {
    if (fVariableSet.size() != o.fVariableSet.size()) {
      return (fVariableSet.size() < o.fVariableSet.size()) ? -1 : 1;
    }
    if (fEquationType != o.fEquationType) {
      return (fEquationType < o.fEquationType) ? -1 : 1;
    }
    if (fLeafCount != o.fLeafCount) {
      return (fLeafCount < o.fLeafCount) ? -1 : 1;
    }
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ExprAnalyzer other = (ExprAnalyzer) obj;
    if (fDenominator == null) {
      if (other.fDenominator != null)
        return false;
    } else if (!fDenominator.equals(other.fDenominator))
      return false;
    if (fEquationType != other.fEquationType)
      return false;
    if (fTogetherExpr == null) {
      if (other.fTogetherExpr != null)
        return false;
    } else if (!fTogetherExpr.equals(other.fTogetherExpr))
      return false;
    if (fLeafCount != other.fLeafCount)
      return false;
    if (fMatrixRow == null) {
      if (other.fMatrixRow != null)
        return false;
    } else if (!fMatrixRow.equals(other.fMatrixRow))
      return false;
    if (fNumerator == null) {
      if (other.fNumerator != null)
        return false;
    } else if (!fNumerator.equals(other.fNumerator))
      return false;
    if (fPlusAST == null) {
      if (other.fPlusAST != null)
        return false;
    } else if (!fPlusAST.equals(other.fPlusAST))
      return false;
    if (fVariableSet == null) {
      if (other.fVariableSet != null)
        return false;
    } else if (!fVariableSet.equals(other.fVariableSet))
      return false;
    if (fListOfVariables == null) {
      if (other.fListOfVariables != null)
        return false;
    } else if (!fListOfVariables.equals(other.fListOfVariables))
      return false;
    return true;
  }

  public IExpr getDenominator() {
    return fDenominator;
  }

  /** @return the expr */
  public IExpr getOriginalExpr() {
    return fOriginalExpr;
  }

  /** @return the expr */
  public IExpr getTogetherExpr() {
    return fTogetherExpr;
  }

  /**
   * 
   * @return the original expression; may be <code>null</code>.
   */
  public IExpr getPowerRewrittenExpr() {
    return fPowerRewrittenExpr;
  }

  public int getNumberOfVars() {
    return fVariableSet.size();
  }

  public IExpr getNumerator() {
    return fNumerator;
  }

  /**
   * Get the argument type.
   *
   * @param eqExpr
   */
  private void getPlusArgumentEquationType(IExpr eqExpr) {
    if (eqExpr.isTimes()) {
      IExpr variable = null;
      fLeafCount++;
      IAST arg = (IAST) eqExpr;
      IExpr expr;
      for (int i = 1; i < arg.size(); i++) {
        expr = arg.get(i);
        if (expr.isFree(Predicates.in(fListOfVariables), true)) {
          fLeafCount++;
        } else if (expr.isVariable()) {
          fLeafCount++;
          for (int j = 1; j < fListOfVariables.size(); j++) {
            if (fListOfVariables.get(j).equals(expr)) {
              fVariableSet.add(expr);
              if (variable != null) {
                if (fEquationType == LINEAR) {
                  fEquationType = POLYNOMIAL;
                }
              } else {
                variable = expr;
                if (fEquationType == LINEAR) {
                  IAST cloned = arg.splice(i);
                  fMatrixRow.set(j, F.Plus(fMatrixRow.get(j), cloned));
                }
              }
            }
          }
        } else if (expr.isPower() && (expr.base().isInteger() || expr.exponent().isNumIntValue())) {
          if (fEquationType == LINEAR) {
            fEquationType = POLYNOMIAL;
          }
          if (expr.exponent().isNumIntValue()) {
            getTimesArgumentEquationType(expr.base());
          } else {
            getTimesArgumentEquationType(expr.exponent());
          }
        } else {
          fLeafCount += eqExpr.leafCount();
          if (fEquationType <= POLYNOMIAL) {
            fEquationType = OTHERS;
          }
        }
      }
      if (fEquationType == LINEAR && variable == null) {
        // should never happen??
      }
    } else {
      getTimesArgumentEquationType(eqExpr);
    }
  }

  /** @return the row */
  public IAST getRow() {
    return fMatrixRow;
  }

  /** @return the symbolSet */
  public Set<IExpr> getVariableSet() {
    return fVariableSet;
  }

  public IAST variables() {
    IASTAppendable list = F.ListAlloc(fVariableSet.size());
    list.appendAll(fVariableSet);
    return list;
  }

  private void getTimesArgumentEquationType(IExpr expr) {
    if (expr.isVariable()) {
      fLeafCount++;
      int position = fListOfVariables.indexOf(expr);
      if (position > 0) {
        fVariableSet.add(expr);
        if (fEquationType == LINEAR) {
          fMatrixRow.set(position, F.Plus(fMatrixRow.get(position), F.C1));
        }
      }
      return;
    }
    if (expr.isFree(Predicates.in(fListOfVariables), true)) {
      fLeafCount++;
      fPlusAST.append(expr);
      return;
    }
    if (expr.isPower()) {
      IExpr base = expr.base();
      IExpr exponent = expr.exponent();
      if (exponent.isInteger()) {
        if (fEquationType == LINEAR) {
          fEquationType = POLYNOMIAL;
        }
        getTimesArgumentEquationType(base);
        return;
      }
      if (exponent.isNumIntValue()) {
        if (fEquationType == LINEAR) {
          fEquationType = POLYNOMIAL;
        }
        getTimesArgumentEquationType(base);
        return;
      }
      if (exponent.isVariable()) {
        int position = fListOfVariables.indexOf(exponent);
        if (position > 0) {
          if (fEquationType <= POLYNOMIAL) {
            fEquationType = OTHERS;
          }
          fVariableSet.add(exponent);
        }
        return;
      }
    }
    fLeafCount += expr.leafCount();
    if (fEquationType <= POLYNOMIAL) {
      fEquationType = OTHERS;
    }
  }

  /** @return the value */
  public IExpr getValue() {
    return fPlusAST.oneIdentity0();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fDenominator == null) ? 0 : fDenominator.hashCode());
    result = prime * result + fEquationType;
    result = prime * result + ((fTogetherExpr == null) ? 0 : fTogetherExpr.hashCode());
    result = prime * result + (int) (fLeafCount ^ (fLeafCount >>> 32));
    result = prime * result + ((fMatrixRow == null) ? 0 : fMatrixRow.hashCode());
    result = prime * result + ((fNumerator == null) ? 0 : fNumerator.hashCode());
    result = prime * result + ((fPlusAST == null) ? 0 : fPlusAST.hashCode());
    result = prime * result + ((fVariableSet == null) ? 0 : fVariableSet.hashCode());
    result = prime * result + ((fListOfVariables == null) ? 0 : fListOfVariables.hashCode());
    return result;
  }

  /**
   * Return <code>true</code> if the expression is linear.
   *
   * @return <code>true</code> if the expression is linear
   */
  public boolean isLinear() {
    return fEquationType == LINEAR;
  }

  public boolean isLinearOrPolynomial() {
    return fEquationType == LINEAR || fEquationType == POLYNOMIAL;
  }

  /**
   * Crosscheck every rule in the <code>listOfResultRules</code> if it's valid in the original
   * expression.
   * 
   * @param expr TODO
   * @param listOfResultRules list of possible solution rules.
   *
   * @return
   */
  public IAST mapOnOriginal(IExpr expr, IAST listOfResultRules) {
    if (expr != null) {
      return F.mapList(listOfResultRules, element -> {
        IExpr temp = expr.replaceAll((IAST) element);
        if (temp.isPresent()) {
          temp = fEngine.evaluate(F.Chop(temp));
          if (temp.isZero()) {
            return element;
          }
        }
        return F.NIL;
      });
    }
    return listOfResultRules;
  }

  public void reset() {
    int size = fListOfVariables.size();
    this.fMatrixRow = F.constantArray(F.C0, size - 1);
    this.fPlusAST = F.PlusAlloc(8);
    this.fEquationType = LINEAR;
  }

  /**
   * Rewrite an expression <code>numericPart*functionToInvert(x)==rhs</code> to
   * <code>x==inverseFunction(rhs / numericPart)</code>, if a possible inverse function is
   * available.
   *
   * @param lhs the expression <code>functionToInvert(x)</code> or
   *        <code>numericPart*functionToInvert(x)</code>
   * @param rhs
   * @return
   */
  private IExpr rewriteInverseFunction(IAST lhs, IExpr rhs) {
    if (lhs.isAbs() || lhs.isAST(S.RealAbs, 2)) {
      return fEngine.evaluate(F.Expand(
          F.Times(F.Subtract(lhs.arg1(), F.Times(F.CN1, rhs)), F.Subtract(lhs.arg1(), rhs))));
    } else if (lhs.isAST1()) {
      return rewriteInverseFunction(F.C1, lhs, rhs);
    } else if (lhs.isPower() && lhs.base().isSymbol() && lhs.exponent().isNumber()) {
      int position = fListOfVariables.indexOf(lhs.base());
      if (position > 0) {
        Errors.printIfunMessage(S.InverseFunction);
        IAST inverseFunction = F.Power(rhs, lhs.exponent().inverse());
        return fEngine.evaluate(F.Subtract(lhs.base(), inverseFunction));
      }
    } else if (lhs.isTimes() && lhs.size() == 3 && lhs.first().isNumericFunction(true)
        && lhs.second().isAST1()) {
      IAST timesArg2 = (IAST) lhs.second();
      IExpr numericPart = lhs.first();
      return rewriteInverseFunction(numericPart, timesArg2, rhs);
    }
    return F.NIL;
  }

  /**
   * Rewrite an expression <code>numericPart*functionToInvert(x)==rhs</code> to
   * <code>x==inverseFunction(rhs / numericPart)</code>, if a possible inverse function is
   * available.
   * 
   * @param numericPart
   * @param functionToInvert the function which should be inverted
   * @param rhs
   * @return
   */
  private IExpr rewriteInverseFunction(IExpr numericPart, IAST functionToInvert, IExpr rhs) {
    IExpr arg1 = functionToInvert.arg1();
    if (fGenerateConditions) {
      if (functionToInvert.isFunctionID(ID.Cos, ID.Cosh, ID.Cot, ID.Coth, ID.Csc, ID.Csch, ID.Sec,
          ID.Sech, ID.Sin, ID.Sinh, ID.Tan, ID.Tanh)) {
        // return dummy placeholder function
        return fEngine
            .evaluate(F.Subtract(arg1, $InverseFunction((IBuiltInSymbol) functionToInvert.head(),
                F.Divide(rhs, numericPart))));
      }
    }
    IASTAppendable inverseFunction =
        InverseFunction.getUnaryInverseFunction(functionToInvert, true);
    if (inverseFunction.isPresent()) {
      Errors.printIfunMessage(S.InverseFunction);
      // rewrite fNumer
      inverseFunction.append(F.Divide(rhs, numericPart));
      return fEngine.evaluate(F.Subtract(arg1, inverseFunction));
    }
    return F.NIL;
  }

  /**
   * Check for an applicable inverse function at the given <code>position</code> in the <code>
   * Plus(..., ,...)</code> expression.
   *
   * @param plusAST the <code>Plus(..., ,...)</code> expression
   * @param position
   * @return <code>F.NIL</code> if no inverse function was found, otherwise return the rewritten
   *         expression
   */
  private IExpr rewriteInverseFunction(IAST plusAST, int position) {
    IAST ast = (IAST) plusAST.get(position);
    IExpr plus = plusAST.splice(position).oneIdentity0();
    if (ast.isAbs() || ast.isAST(S.RealAbs, 2)) {
      if (plus.isNegative() || plus.isZero()) {
        if (plus.isFree(Predicates.in(fListOfVariables), true)) {
          return rewriteInverseFunction(ast, F.Negate(plus));
        }
      }
      return F.NIL;
    }
    if (plus.isFree(Predicates.in(fListOfVariables), true)) {
      return rewriteInverseFunction(ast, F.Negate(plus));
    }
    return F.NIL;
  }

  /**
   * Try to rewrite a <code>Plus(...,f(x), ...)</code> function which contains an invertible
   * function argument <code>f(x)</code>.
   */
  private IExpr rewritePlusWithInverseFunctions(IAST plusAST) {
    IExpr expr;
    for (int i = 1; i < plusAST.size(); i++) {
      expr = plusAST.get(i);
      if (expr.isFree(Predicates.in(fListOfVariables), true)) {
        continue;
      }

      if (expr.isAST()) {
        IAST function = (IAST) expr;
        IAST inverseFunction = InverseFunction.getUnaryInverseFunction(function, true);
        if (inverseFunction.isPresent()) {
          IExpr temp = rewriteInverseFunction(plusAST, i);
          if (temp.isPresent()) {
            return temp;
          }
        } else if (function.isPower()) {
          return rewritePower(plusAST, i, F.C1, function.base(), function.exponent());
        } else if (function.isTimes() && function.size() == 3
            && function.arg1().isNumericFunction(true)) {
          if (function.arg2().isPower()) {
            // function is num*Power(x, fraction)
            IAST power = (IAST) function.arg2();
            IExpr temp = rewritePower(plusAST, i, function.arg1(), power.base(), power.exponent());
            if (temp.isPresent()) {
              return fEngine.evaluate(temp);
            }
          } else if (function.arg2().isAST1()) {
            IAST inverseTimesFunction =
                InverseFunction.getUnaryInverseFunction((IAST) function.arg2(), true);
            if (inverseTimesFunction.isPresent()) {
              IExpr temp = rewriteInverseFunction(plusAST, i);
              if (temp.isPresent()) {
                return temp;
              }
            }
          }
        } else if (function.isAST(S.GammaRegularized, 3)) {
          IAST temp = plusAST.removeAtCopy(i);
          int position = fListOfVariables.indexOf(function.arg2());
          if (position > 0 && function.arg1().isFree(fListOfVariables)
              && temp.isFree(fListOfVariables)) {
            Errors.printIfunMessage(S.InverseFunction);
            return fEngine.evaluate(F.InverseGammaRegularized(function.arg1(), temp.negate()));
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Rewrite <code>num*base^exponent</code> at position <code>i</code> in <code>plusAST</code> if
   * the <code>exponent</code> is a fraction.
   *
   * @param plusAST
   * @param i
   * @param num
   * @param base
   * @param exponent
   * @return
   */
  private IExpr rewritePower(IAST plusAST, int i, IExpr num, IExpr base, IExpr exponent) {
    if (exponent.isFraction() || (exponent.isReal() && !exponent.isNumIntValue())) {
      IReal arg2 = (IReal) exponent;
      if (arg2.isPositive()) {
        IExpr plus = plusAST.splice(i).oneIdentity0();
        // if (plus.isPositiveResult()) {
        // // no solution possible
        // return NO_EQUATION_SOLUTION;
        // }
        fPowerRewrittenExpr = plusAST;
        if (num.isOne()) {
          return fEngine
              .evaluate(F.Subtract(F.Expand(F.Power(F.Negate(plus), arg2.inverse())), base));
        }
        return fEngine.evaluate(F.Subtract(base,
            F.Expand(F.Power(F.Times(num.inverse(), F.Negate(plus)), arg2.inverse()))));
      }
    } else if (base.isVariable() && base.equals(exponent)) {
      // rewrite num * x^x as ProductLog() (Lambert W function)
      IExpr plus = plusAST.splice(i).oneIdentity0().negate().divide(num);
      // Log(arg1)/ProductLog(Log(arg1))
      IAST inverseFunction =
          F.Plus(base, F.Times(F.Log(plus).negate(), F.Power(F.ProductLog(F.Log(plus)), F.CN1)));
      return fEngine.evaluate(inverseFunction);
    }
    if (fListOfVariables.size() == 2) {
      IExpr variable = fListOfVariables.arg1();
      if (exponent.equals(variable) && base.isInteger()) {
        IExpr plusRest = plusAST.splice(i).oneIdentity0().negate().divide(num);
        if (plusRest.isFree(variable)) {
          IInteger b = (IInteger) base;
          IAST c1 = F.C(fEngine.incConstantCounter());
          if (b.isNegative()) {
            // if (generateConditions().isTrue()) {
            return F.ConditionalExpression(F.Times( //
                // (2*I*Pi*c1 + Log(plusRest))/(I*Pi + Log(-b)
                F.Plus(F.Times(F.CC(0, 2), S.Pi, c1), F.Log(plusRest)),
                F.Power(F.Plus(F.Times(F.CI, S.Pi), F.Log(b.negate())), F.CN1)), //
                F.Element(c1, S.Integers));
            // } else {
            // return F.Times( //
            // // Log(plusRest)/(I*Pi + Log(-b)
            // F.Log(plusRest), //
            // F.Power(F.Plus(F.Times(F.CI, S.Pi), F.Log(b.negate())), F.CN1));
            // }
          } else {
            // if (generateConditions().isTrue()) {
            return F.ConditionalExpression(F.Plus(//
                F.Times(F.CC(0, 2), S.Pi, c1, F.Power(F.Log(b), F.CN1)), //
                F.Divide(F.Log(plusRest), F.Log(b))), //
                F.Element(c1, S.Integers));
            // } else {
            // return F.ConditionalExpression(F.Plus(//
            // F.Divide(F.Log(plusRest), F.Log(b))), //
            // F.Element(c1, S.Integers));
            // }
          }
        }
      }
      return rewritePower2ProductLog(plusAST, i, num, base, exponent, variable);
    }
    return F.NIL;
  }

  /**
   * Rewrite <code>base^variable + a*variable + b</code> as <code>
   * variable +  ((b)*Log(x) + a * ProductLog(-(Log(x)/(x^(b/a)*a))) ) / (a*Log(x))
   * </code>. ProductLog is the Lambert W function.
   *
   * <p>
   * See: <a href="https://en.wikipedia.org/wiki/Lambert_W_function#Solving_equations">Wikipedia -
   * Lambert W function - Solving equations</a>
   *
   * @param plusAST a <code>Plus(...)</code> expression
   * @param i the index in <code>plusAST</code> where the argument equals F<code>base ^ exponent
   *     </code>
   * @param num a factor for <code>factor*base^variable</code>
   * @param base must be free of <code>variable</code>
   * @param exponent must be equal to <code>variable</code>
   * @param variable the variable for which <code>plusAST</code> should be solved
   * @return {@link F#NIL} if no solution was found
   */
  private IExpr rewritePower2ProductLog(IAST plusAST, int i, IExpr num, IExpr base, IExpr exponent,
      IExpr variable) {
    if (variable.equals(exponent) && base.isFree(variable)) {
      IExpr restOfPlus = plusAST.splice(i).oneIdentity0();
      IExpr a = F.NIL;
      IExpr b = F.C0;
      if (restOfPlus.isPlus()) {
        int indx = restOfPlus.indexOf(x -> !x.isFree(variable));
        if (indx > 0) {
          IExpr restOfPlus2 = ((IAST) restOfPlus).splice(indx).oneIdentity1();
          if (restOfPlus2.isFree(variable)) {
            a = determineFactor(((IAST) restOfPlus).get(indx), variable);
            b = restOfPlus2;
          }
        }
      } else {
        a = determineFactor(restOfPlus, variable);
      }
      if (a.isPresent()) {
        // variable + ((b)*Log(base) + a * ProductLog(-(Log(base)/(base^(b/a)*a))) )/(a*Log(base))
        IAST z =
            F.Times(num, F.Log(base), F.Power(F.Times(a, F.Power(base, F.Divide(b, a))), F.CN1));
        IAST inverseFunction =
            F.Plus(variable, F.Times(F.Plus(F.Times(b, F.Log(base)), F.Times(a, F.ProductLog(z))),
                F.Power(F.Times(a, F.Log(base)), F.CN1)));
        // https://en.wikipedia.org/wiki/Lambert_W_function
        // When dealing with real numbers only, the two branches W(0) and W(−1) suffice ...
        IReal zReal = z.evalReal();
        if (zReal != null) {
            if (zReal.isNegative() && zReal.isGT(F.CND1DE)) {
              // TODO branch W(-1)
              IAST inverseFunctionNegative = F.Plus(variable,
                  F.Times(F.Plus(F.Times(b, F.Log(base)), F.Times(a, F.ProductLog(F.CN1, z))),
                      F.Power(F.Times(a, F.Log(base)), F.CN1)));
              return F.List(fEngine.evaluate(inverseFunction),
                  fEngine.evaluate(inverseFunctionNegative));
            }
        }
        return fEngine.evaluate(inverseFunction);
      }
    }
    return F.NIL;
  }

  private IExpr determineFactor(IExpr restOfPlus, IExpr variable) {
    if (restOfPlus.equals(variable)) {
      return F.C1;
    } else if (restOfPlus.isTimes()) {
      int indx = restOfPlus.indexOf(variable);
      if (indx > 0) {
        IExpr restOfTimes = ((IAST) restOfPlus).splice(indx).oneIdentity1();
        if (restOfTimes.isFree(variable)) {
          return restOfTimes;
        }
      }
    }
    return F.NIL;
  }

  /**
   * Try to rewrite a <code>Times(...,f(x), ...)</code> expression which may contain an invertible
   * function argument <code>f(x)</code> as subexpression.
   */
  private IExpr rewriteTimesWithInverseFunctions(IAST times) {
    IASTAppendable result = F.NIL;
    int j = 1;
    // remove constant sub-expressions from Times() expression
    for (int i = 1; i < times.size(); i++) {
      if (times.get(i).isFree(Predicates.in(fListOfVariables), true)
          && times.get(i).isNumericFunction(true)) {
        if (result.isNIL()) {
          result = times.copyAppendable();
        }
        result.remove(j);
        continue;
      }
      j++;
    }
    if (result.isNIL()) {
      return rewriteInverseFunction(times, F.C0);
    }
    IExpr temp0 = result.oneIdentity1();
    if (temp0.isAST()) {
      return rewriteInverseFunction((IAST) temp0, F.C0).orElse(temp0);
    }
    return temp0;
  }

  /**
   * If possible simplify the numerator expression. After that analyze the numerator expression, if
   * it has linear, polynomial or other form.
   */
  public int simplifyAndAnalyze() {
    IExpr temp = rewriteNumerator();
    if (temp.isPresent()) {
      if (temp.isAST() && fDenominator.isOne()) {
        splitNumeratorDenominator((IAST) temp);
      } else {
        fNumerator = temp;
      }
    }
    return analyze(fNumerator);
  }

  public int exprAnalyze(IExpr temp) {
    if (temp.isPresent()) {
      if (temp.isAST() && fDenominator.isOne()) {
        splitNumeratorDenominator((IAST) temp);
      } else {
        fNumerator = temp;
      }
    }
    return analyze(fNumerator);
  }

  public IExpr rewriteNumerator() {
    IExpr temp = F.NIL;
    if (fNumerator.isPlus()) {
      temp = rewritePlusWithInverseFunctions((IAST) fNumerator);
    } else if (fNumerator.isTimes() && !fNumerator.isFree(Predicates.in(fListOfVariables), true)) {
      temp = rewriteTimesWithInverseFunctions((IAST) fNumerator);
    } else if (fNumerator.isAST() && !fNumerator.isFree(Predicates.in(fListOfVariables), true)) {
      temp = rewriteInverseFunction((IAST) fNumerator, F.C0);
    }
    return temp;
  }

  public void splitNumeratorDenominator(IAST ast) {
    IExpr[] result = Algebra.numeratorDenominator(ast, true, fEngine);
    this.fNumerator = result[0];
    this.fDenominator = result[1];
    this.fTogetherExpr = result[2];
  }

  @Override
  public String toString() {
    if (fDenominator.isOne()) {
      return fTogetherExpr.toString();
    }
    return fTogetherExpr.toString() //
        + " [ " + fNumerator.toString() + " / " + fDenominator.toString() + " ]";
  }
}
