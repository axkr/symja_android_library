package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.RootsFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.QuarticSolver;

/** Try to solve a set of equations (i.e. <code>Equal[...]</code> expressions). */
public class NSolve extends AbstractFunctionEvaluator {
  /** Analyze an expression, if it has linear, polynomial or other form. */
  private static class ExprAnalyzer implements Comparable<ExprAnalyzer> {

    public static final int LINEAR = 0;
    public static final int OTHERS = 2;
    public static final int POLYNOMIAL = 1;

    private int equationType;
    private IExpr expr;
    private IExpr numer;
    private IExpr denom;
    private long leafCount;

    IASTAppendable row;
    HashSet<ISymbol> symbolSet;
    IASTAppendable value;

    final IAST vars;

    public ExprAnalyzer(IExpr expr, IAST vars, EvalEngine engine) {
      super();
      this.expr = expr;
      this.numer = expr;
      this.denom = F.C1;
      if (this.expr.isAST()) {
        this.expr = Algebra.together((IAST) this.expr, engine);
        // split expr into numerator and denominator
        this.denom = engine.evaluate(F.Denominator(this.expr));
        if (!this.denom.isOne()) {
          // search roots for the numerator expression
          this.numer = engine.evaluate(F.Numerator(this.expr));
        }
      }
      this.vars = vars;
      this.symbolSet = new HashSet<ISymbol>();
      this.leafCount = 0;
      reset();
    }

    public void analyze() {
      analyze(getNumerator());
    }

    /** Analyze an expression, if it has linear, polynomial or other form. */
    private void analyze(IExpr eqExpr) {
      if (eqExpr.isFree(Predicates.in(vars), true)) {
        leafCount++;
        value.append(eqExpr);
      } else if (eqExpr.isPlus()) {
        leafCount++;
        IAST arg = (IAST) eqExpr;
        arg.forEach(
            expr -> {
              if (expr.isFree(Predicates.in(vars), true)) {
                leafCount++;
                value.append(expr);
              } else {
                getPlusEquationType(expr);
              }
            });
      } else {
        getPlusEquationType(eqExpr);
      }
    }

    @Override
    public int compareTo(ExprAnalyzer o) {
      if (symbolSet.size() != o.symbolSet.size()) {
        if (symbolSet.size() < o.symbolSet.size()) {
          return -1;
        }
        return 1;
      }
      if (equationType != o.equationType) {
        if (equationType < o.equationType) {
          return -1;
        }
        return 1;
      }
      if (leafCount != o.leafCount) {
        if (leafCount < o.leafCount) {
          return -1;
        }
        return 1;
      }

      return 0;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ExprAnalyzer other = (ExprAnalyzer) obj;
      if (denom == null) {
        if (other.denom != null) return false;
      } else if (!denom.equals(other.denom)) return false;
      if (equationType != other.equationType) return false;
      if (expr == null) {
        if (other.expr != null) return false;
      } else if (!expr.equals(other.expr)) return false;
      if (leafCount != other.leafCount) return false;
      if (numer == null) {
        if (other.numer != null) return false;
      } else if (!numer.equals(other.numer)) return false;
      if (row == null) {
        if (other.row != null) return false;
      } else if (!row.equals(other.row)) return false;
      if (symbolSet == null) {
        if (other.symbolSet != null) return false;
      } else if (!symbolSet.equals(other.symbolSet)) return false;
      if (value == null) {
        if (other.value != null) return false;
      } else if (!value.equals(other.value)) return false;
      if (vars == null) {
        if (other.vars != null) return false;
      } else if (!vars.equals(other.vars)) return false;
      return true;
    }

    /** @return the expr */
    public IExpr getExpr() {
      return expr;
    }

    public IExpr getNumerator() {
      return numer;
    }

    public IExpr getDenominator() {
      return denom;
    }

    public int getNumberOfVars() {
      return symbolSet.size();
    }

    private void getPlusEquationType(IExpr eqExpr) {
      if (eqExpr.isTimes()) {
        ISymbol sym = null;
        leafCount++;
        IAST arg = (IAST) eqExpr;
        IExpr expr;
        for (int i = 1; i < arg.size(); i++) {
          expr = arg.get(i);
          if (expr.isFree(Predicates.in(vars), true)) {
            leafCount++;
          } else if (expr.isSymbol()) {
            leafCount++;
            for (int j = 1; j < vars.size(); j++) {
              if (vars.get(j).equals(expr)) {
                symbolSet.add((ISymbol) expr);
                if (sym != null) {
                  if (equationType == LINEAR) {
                    equationType = POLYNOMIAL;
                  }
                } else {
                  sym = (ISymbol) expr;
                  if (equationType == LINEAR) {
                    IAST cloned = arg.splice(i);
                    row.set(j, F.Plus(row.get(j), cloned));
                  }
                }
              }
            }
          } else if (expr.isPower()
              && (expr.base().isInteger() || expr.exponent().isNumIntValue())) {
            // (JASConvert.getExponent((IAST) expr) > 0)) {
            if (equationType == LINEAR) {
              equationType = POLYNOMIAL;
            }
            getTimesEquationType(expr.base());
          } else {
            leafCount += eqExpr.leafCount();
            if (equationType <= POLYNOMIAL) {
              equationType = OTHERS;
            }
          }
        }
        if (equationType == LINEAR) {
          if (sym == null) {
            // should never happen??
            // System.out.println("sym == null???");
          }
        }
      } else {
        getTimesEquationType(eqExpr);
      }
    }

    /** @return the row */
    public IAST getRow() {
      return row;
    }

    /** @return the symbolSet */
    public HashSet<ISymbol> getSymbolSet() {
      return symbolSet;
    }

    private void getTimesEquationType(IExpr expr) {
      if (expr.isSymbol()) {
        leafCount++;
        vars.forEach(
            (x, i) -> {
              if (vars.equalsAt(i, expr)) {
                symbolSet.add((ISymbol) expr);
                if (equationType == LINEAR) {
                  row.set(i, F.Plus(row.get(i), F.C1));
                }
              }
            });
        return;
      }
      if (expr.isFree(Predicates.in(vars), true)) {
        leafCount++;
        value.append(expr);
        return;
      }
      if (expr.isPower()) {
        IExpr base = expr.base();
        IExpr exponent = expr.exponent();
        if (exponent.isInteger()) {
          if (equationType == LINEAR) {
            equationType = POLYNOMIAL;
          }
          getTimesEquationType(base);
          return;
        }
        if (exponent.isNumIntValue()) {
          if (equationType == LINEAR) {
            equationType = POLYNOMIAL;
          }
          getTimesEquationType(base);
          return;
        }
      }
      leafCount += expr.leafCount();
      if (equationType <= POLYNOMIAL) {
        equationType = OTHERS;
      }
    }

    /** @return the value */
    public IAST getValue() {
      return value;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((denom == null) ? 0 : denom.hashCode());
      result = prime * result + equationType;
      result = prime * result + ((expr == null) ? 0 : expr.hashCode());
      result = prime * result + (int) (leafCount ^ (leafCount >>> 32));
      result = prime * result + ((numer == null) ? 0 : numer.hashCode());
      result = prime * result + ((row == null) ? 0 : row.hashCode());
      result = prime * result + ((symbolSet == null) ? 0 : symbolSet.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
      result = prime * result + ((vars == null) ? 0 : vars.hashCode());
      return result;
    }

    /**
     * Return <code>true</code> if the expression is linear.
     *
     * @return <code>true</code> if the expression is linear
     */
    public boolean isLinear() {
      return equationType == LINEAR;
    }

    public boolean isLinearOrPolynomial() {
      return equationType == LINEAR || equationType == POLYNOMIAL;
    }

    public void reset() {
      this.row = F.ListAlloc(vars.size());
      for (int i = 1; i < vars.size(); i++) {
        row.append(F.C0);
      }
      this.value = F.PlusAlloc(16);
      this.equationType = LINEAR;
    }
  }

  @SuppressWarnings("serial")
  private static class NoSolution extends Exception {
    /** Solution couldn't be found. */
    public static final int NO_SOLUTION_FOUND = 1;

    /** Definitely wrong solution. */
    public static final int WRONG_SOLUTION = 0;

    final int solType;

    public NoSolution(int solType) {
      super();
      this.solType = solType;
    }

    public int getType() {
      return solType;
    }
  }

  /**
   * @param analyzerList
   * @param vars
   * @param resultList
   * @param matrix
   * @param vector
   * @return <code>null</code> if the solution couldn't be found
   */
  private static IASTAppendable analyzeSublist(
      ArrayList<ExprAnalyzer> analyzerList,
      IAST vars,
      IASTAppendable resultList,
      IASTAppendable matrix,
      IASTAppendable vector,
      EvalEngine engine)
      throws NoSolution {
    ExprAnalyzer exprAnalyzer;
    Collections.sort(analyzerList);
    int currEquation = 0;
    while (currEquation < analyzerList.size()) {
      exprAnalyzer = analyzerList.get(currEquation);
      if (exprAnalyzer.getNumberOfVars() == 0) {
        // check if the equation equals zero.
        IExpr expr = exprAnalyzer.getNumerator();
        if (!expr.isZero()) {
          if (expr.isNumber()) {
            throw new NoSolution(NoSolution.WRONG_SOLUTION);
          }
          if (!S.PossibleZeroQ.ofQ(engine, expr)) {
            throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
          }
        }
      } else if (exprAnalyzer.getNumberOfVars() == 1 && exprAnalyzer.isLinearOrPolynomial()) {
        IAST listOfRules = rootsOfUnivariatePolynomial(exprAnalyzer, engine);
        if (listOfRules.isPresent()) {
          boolean evaled = false;
          ++currEquation;
          for (int k = 1; k < listOfRules.size(); k++) {
            if (currEquation >= analyzerList.size()) {
              resultList.append(F.List(listOfRules.getAST(k)));
              evaled = true;
            } else {

              ArrayList<ExprAnalyzer> subAnalyzerList = new ArrayList<ExprAnalyzer>();
              // collect linear and univariate polynomial
              // equations:
              for (int i = currEquation; i < analyzerList.size(); i++) {
                IExpr expr = analyzerList.get(i).getExpr();
                IExpr temp = expr.replaceAll(listOfRules.getAST(k));
                if (temp.isPresent()) {
                  expr = engine.evaluate(temp);
                  exprAnalyzer = new ExprAnalyzer(expr, vars, engine);
                  exprAnalyzer.analyze();
                } else {
                  // reuse old analyzer; expression hasn't
                  // changed
                  exprAnalyzer = analyzerList.get(i);
                }
                subAnalyzerList.add(exprAnalyzer);
              }
              try {
                IAST subResultList =
                    analyzeSublist(subAnalyzerList, vars, F.ListAlloc(), matrix, vector, engine);
                if (subResultList != null) {
                  evaled = true;
                  for (IExpr expr : subResultList) {
                    if (expr.isList()) {
                      IASTAppendable list;
                      if (expr instanceof IASTAppendable) {
                        list = (IASTAppendable) expr;
                      } else {
                        list = ((IAST) expr).copyAppendable();
                      }
                      list.append(1, listOfRules.getAST(k));
                      resultList.append(list);
                    } else {
                      resultList.append(expr);
                    }
                  }
                }
              } catch (NoSolution e) {
                if (e.getType() == NoSolution.WRONG_SOLUTION) {
                  evaled = true;
                }
              }
            }
          }
          if (evaled) {
            return resultList;
          }
        }
        throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
      } else if (exprAnalyzer.isLinear()) {
        matrix.append(engine.evaluate(exprAnalyzer.getRow()));
        vector.append(engine.evaluate(F.Negate(exprAnalyzer.getValue())));
      } else {
        throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
      }
      currEquation++;
    }
    return resultList;
  }

  /**
   * Evaluate the roots of a univariate polynomial with the Roots[] function.
   *
   * @param exprAnalyzer
   * @param engine the evaluation engine
   * @return
   */
  private static IAST rootsOfUnivariatePolynomial(ExprAnalyzer exprAnalyzer, EvalEngine engine) {
    IExpr expr = exprAnalyzer.getNumerator();
    IExpr denom = exprAnalyzer.getDenominator();
    // try to solve the expr for a symbol in the symbol set
    for (ISymbol sym : exprAnalyzer.getSymbolSet()) {
      IExpr temp = RootsFunctions.rootsOfVariable(expr, denom, F.List(sym), true, engine);
      if (temp.isPresent()) {
        IASTAppendable resultList = F.ListAlloc();
        if (temp.isSameHeadSizeGE(S.List, 2)) {
          IAST rootsList = (IAST) temp;
          for (IExpr root : rootsList) {
            IAST rule = F.Rule(sym, root);
            resultList.append(rule);
          }
          return QuarticSolver.sortASTArguments(resultList);
        }
        return F.NIL;
      }
    }
    return F.NIL;
  }

  public NSolve() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return Solve.of(ast, true, engine);
    //		IAST vars = Validate.checkSymbolOrSymbolList(ast, 2);
    //		IAST termsEqualZeroList = Validate.checkEquations(ast, 1);
    //
    //		ExprAnalyzer exprAnalyzer;
    //		ArrayList<ExprAnalyzer> analyzerList = new ArrayList<ExprAnalyzer>();
    //		// collect linear and univariate polynomial equations:
    //		for (IExpr expr : termsEqualZeroList) {
    //			exprAnalyzer = new ExprAnalyzer(expr, vars, engine);
    //			exprAnalyzer.analyze();
    //			analyzerList.add(exprAnalyzer);
    //		}
    //		IASTAppendable matrix = F.ListAlloc();
    //		IASTAppendable vector = F.ListAlloc();
    //		try {
    //			IASTAppendable resultList = F.ListAlloc();
    //			resultList = analyzeSublist(analyzerList, vars, resultList, matrix, vector, engine);
    //
    //			if (vector.size() > 1) {
    //				// solve a linear equation <code>matrix.x == vector</code>
    //				IExpr temp = engine.evaluate(F.LinearSolve(matrix, vector));
    //				if (temp.isSameHeadSizeGE(F.List, 2)) {
    //					IAST rootsList = (IAST) temp;
    //					int size = vars.size();
    //					IASTAppendable list = F.ListAlloc(size);
    //					list.appendArgs(size, j -> F.Rule(vars.get(j), rootsList.get(j)));
    //					// for (int j = 1; j < size; j++) {
    //					// IAST rule = F.Rule(vars.get(j), rootsList.get(j));
    //					// list.append(rule);
    //					// }
    //					resultList.append(list);
    //				} else {
    //					return F.NIL;
    //				}
    //			}
    //
    //			return resultList;
    //		} catch (NoSolution e) {
    //			if (e.getType() == NoSolution.WRONG_SOLUTION) {
    //				return F.List();
    //			}
    //			return F.NIL;
    //		}
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }
}
