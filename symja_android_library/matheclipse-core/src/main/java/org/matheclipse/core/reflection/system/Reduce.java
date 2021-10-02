package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Reduce extends AbstractEvaluator {

  private static class ReduceComparison {

    final IExpr variable;

    /**
     * Implements value interval for <code>variable</code> as the interval: <code>
     * xMin (minType) variable (maxType) xMax</code>.
     *
     * <p><code>minType</code> and <code>maxType</code> define if it is an open interval (value == 1
     * (LessThan) ) or a closed interval (value == 2 (LessEqualThan) or value == 3 (EQUAL))
     */
    private static class VariableInterval {

      final IExpr variable;

      IExpr xMin;
      IExpr xMax;
      int minType; // 1 -> LT 2 -> LE 3 -> EQ
      int maxType; // 1 -> LT 2 -> LE 3 -> EQ

      public VariableInterval(IExpr variable) {
        this.variable = variable;
        this.xMin = F.CNInfinity;
        this.xMax = F.CInfinity;
        this.minType = 1;
        this.maxType = 1;
      }

      public void set(VariableInterval cd) {
        this.xMin = cd.xMin;
        this.xMax = cd.xMax;
        this.minType = cd.minType;
        this.maxType = cd.maxType;
      }

      public boolean reduceOr(final VariableInterval cd) {
        if (S.Equal.ofQ(this.xMax, cd.xMin)) {
          // | this.xMin ?? this.xMax == cd.xMin ?? cd.xMax |
          if (this.maxType >= 2 || cd.minType >= 2) {
            this.xMax = cd.xMax;
            this.maxType = cd.maxType;
            return true;
          }
        } else if (S.Equal.ofQ(cd.xMax, this.xMin)) {
          // | cd.xMin ?? cd.xMax == this.xMin ?? this.xMax |
          if (this.maxType >= 2 || cd.minType >= 2) {
            this.xMin = cd.xMin;
            this.minType = cd.minType;
            return true;
          }
        } else if (S.GreaterEqual.ofQ(cd.xMin, this.xMin)) {
          // | this.xMin <= cd.xMin ...  |
          if (S.LessEqual.ofQ(cd.xMax, this.xMax)) {
            // this interval includes cd interval
            // | this.xMin <= cd.xMin <--> cd.Max <= this.xMax |
            return true;
          }
        } else if (S.GreaterEqual.ofQ(this.xMin, cd.xMin)) {
          // | cd.xMin <= this.xMin ...  |
          if (S.LessEqual.ofQ(this.xMax, cd.xMax)) {
            // cd interval includes this interval
            // | cd.xMin <= this.xMin <--> this.xMax <= cd.xMax |
            this.xMin = cd.xMin;
            this.xMax = cd.xMax;
            this.minType = cd.minType;
            this.maxType = cd.maxType;
            return true;
          }
        }

        if (S.Greater.ofQ(this.xMax, cd.xMin)) {
          if (S.Less.ofQ(this.xMax, cd.xMax)) {
            // | cd.xMin <= this.xMax  <--> this.xMax <= cd.xMax |
            this.xMax = cd.xMax;
            this.maxType = cd.maxType;
            return true;
          }
        } else if (S.Greater.ofQ(this.xMin, cd.xMin)) {
          if (S.Less.ofQ(this.xMin, cd.xMax)) {
            // | cd.xMin <= this.xMin  <--> this.xMin <= cd.xMax |
            this.xMin = cd.xMin;
            this.minType = cd.minType;
            return true;
          }
        }
        return false;
      }

      boolean isInitial() {
        return xMin == F.CNInfinity && xMax == F.CInfinity;
      }

      private IExpr toExpr() {
        if (!xMin.equals(F.CNInfinity)) {
          if (xMax.equals(xMin)) {
            if (minType >= 2 && maxType >= 2) {
              return F.Equal(variable, xMin);
            }
            return F.False;
          }
          IAST gt =
              (minType >= 2) //
                  ? F.GreaterEqual(variable, xMin)
                  : F.Greater(variable, xMin);
          if (!xMax.equals(F.CInfinity)) {
            IAST lt =
                (maxType >= 2) //
                    ? F.LessEqual(variable, xMax)
                    : F.Less(variable, xMax);
            return F.And(gt, lt);
          }
          return gt;
        } else if (!xMax.equals(F.CInfinity)) {
          IAST lt =
              (maxType >= 2) //
                  ? F.LessEqual(variable, xMax)
                  : F.Less(variable, xMax);
          return lt;
        }
        return F.NIL;
      }

      @Override
      public String toString() {
        String minStr = minType == 1 ? " < " : minType == 2 ? " <= " : " == ";
        String maxStr = maxType == 1 ? " < " : maxType == 2 ? " <= " : " == ";
        return "|" + xMin.toString() + minStr + variable + maxStr + xMax.toString() + "|";
      }
    }

    public ReduceComparison(IExpr variable) {
      this.variable = variable;
    }

    protected IExpr evaluate(IExpr logicalExpand) throws ArgumentTypeException {
      return reduceAndOr(logicalExpand);
    }

    private IExpr reduceAndOr(IExpr expr) throws ArgumentTypeException {
      if (expr.isAST(S.And)) {
        VariableInterval cd = new VariableInterval(variable);
        IExpr temp = reduceAnd(expr, cd);
        if (temp.isPresent()) {
          if (temp == S.Continue) {
            return cd.toExpr();
          }
          return temp;
        }
        return F.NIL;
      } else if (expr.isAST(S.Or)) {
        VariableInterval cd = new VariableInterval(variable);
        IExpr temp = reduceOr(expr, cd);
        if (temp.isPresent()) {
          if (temp == S.Continue) {
            return cd.toExpr();
          }
          return temp;
        }
        return F.NIL;
      }

      return F.NIL;
    }

    private IExpr reduceOr(IExpr expr, VariableInterval cd) throws ArgumentTypeException {
      IAST orAST = (IAST) expr;
      if (orAST.isAST0()) {
        throw new ArgumentTypeException("Or: size == 0");
      }
      if (orAST.isAST1()) {
        return orAST.arg1();
      }
      for (int i = 1; i < orAST.size(); i++) {
        IExpr arg = orAST.get(i);
        if (arg.isAST(S.And)) {
          VariableInterval andCD = new VariableInterval(variable);
          IExpr temp = reduceAnd(arg, andCD);
          if (temp.isPresent()) {
            if (temp == S.Continue) {
              if (cd.isInitial()) {
                cd.set(andCD);
              } else {
                if (!cd.reduceOr(andCD)) {
                  return F.NIL;
                }
              }
              continue;
            }
            return temp;
          }
          return F.NIL;
        } else {
          // TODO check Simplify result
          IExpr temp = S.Simplify.of(arg);
          if (temp.isAST2() && temp.first().equals(variable)) {
            VariableInterval comparatorCD = new VariableInterval(variable);
            IExpr arg2 = temp.second();
            temp = reduceComparator(comparatorCD, temp, arg2);
            if (temp != S.Continue) {
              if (temp.isTrue()) {
                continue;
              }
              if (temp.isFalse()) {
                return S.False;
              }
              return temp;
            } else {
              if (cd.isInitial()) {
                cd.set(comparatorCD);
              } else {
                if (!cd.reduceOr(comparatorCD)) {
                  return F.NIL;
                }
              }
            }
          }
        }
      }
      return S.Continue;
    }

    private IExpr reduceAnd(IExpr expr, VariableInterval cd) throws ArgumentTypeException {
      IAST andAST = (IAST) expr;
      if (andAST.isAST0()) {
        throw new ArgumentTypeException("And: size == 0");
      }
      if (andAST.isAST1()) {
        return andAST.arg1();
      }

      for (int i = 1; i < andAST.size(); i++) {
        // TODO check Simplify result
        IExpr temp = S.Simplify.of(andAST.get(i));
        if (temp.isAST2() && temp.first().equals(cd.variable)) {
          IExpr arg2 = temp.second();
          temp = reduceComparator(cd, temp, arg2);
          if (temp != S.Continue) {
            if (temp.isTrue()) {
              continue;
            }
            if (temp.isFalse()) {
              return S.False;
            }
            return temp;
          }
        }
      }

      return S.Continue;
    }

    private IExpr reduceComparator(VariableInterval cd, IExpr temp, IExpr rhs)
        throws ArgumentTypeException {
      double d = rhs.evalDouble();
      if (!(Double.isNaN(d) || Double.isInfinite(d))) {
        switch (temp.headID()) {
          case ID.Equal:
            if (S.GreaterEqual.ofQ(rhs, cd.xMin) && S.LessEqual.ofQ(rhs, cd.xMax)) {
              if (cd.maxType == 1 && S.Equal.ofQ(rhs, cd.xMax)) {
                return S.False;
              }
              if (cd.minType == 1 && S.Equal.ofQ(rhs, cd.xMin)) {
                return S.False;
              }
              cd.xMax = rhs;
              cd.xMin = rhs;
              cd.minType = 3;
              cd.maxType = 3;
              return S.Continue;
            } else {
              if (S.Greater.ofQ(rhs, cd.xMax)) {
                return S.False;
              }
              if (S.Less.ofQ(rhs, cd.xMin)) {
                return S.False;
              }
            }
            break;
          case ID.Less:
            if (S.Less.ofQ(rhs, cd.xMax)) {
              if (cd.minType == 1 && S.LessEqual.ofQ(rhs, cd.xMin)) {
                return S.False;
              } else if (S.Less.ofQ(rhs, cd.xMin)) {
                return S.False;
              }
              cd.xMax = rhs;
              cd.maxType = 1;
            } else {
              if (S.LessEqual.ofQ(rhs, cd.xMin)) {
                return S.False;
              }
            }
            return S.Continue;
          case ID.LessEqual:
            if (S.LessEqual.ofQ(rhs, cd.xMax)) {
              if (cd.minType == 1 && S.LessEqual.ofQ(rhs, cd.xMin)) {
                return S.False;
              } else if (S.Less.ofQ(rhs, cd.xMin)) {
                return S.False;
              }
              cd.xMax = rhs;
              cd.maxType = 2;
            } else {
              if (S.Less.ofQ(rhs, cd.xMin)) {
                return S.False;
              }
            }
            return S.Continue;
          case ID.Greater:
            if (S.Greater.ofQ(rhs, cd.xMin)) {
              if (cd.maxType == 1 && S.GreaterEqual.ofQ(rhs, cd.xMax)) {
                return S.False;
              } else if (S.Greater.ofQ(rhs, cd.xMax)) {
                return S.False;
              }
              cd.xMin = rhs;
              cd.minType = 1;
            } else {
              if (S.GreaterEqual.ofQ(rhs, cd.xMax)) {
                return S.False;
              }
            }
            return S.Continue;
          case ID.GreaterEqual:
            if (S.GreaterEqual.ofQ(rhs, cd.xMin)) {
              if (cd.maxType == 1 && S.GreaterEqual.ofQ(rhs, cd.xMax)) {
                return S.False;
              } else if (S.Greater.ofQ(rhs, cd.xMax)) {
                return S.False;
              }
              cd.xMin = rhs;
              cd.minType = 2;
            } else {
              if (S.Greater.ofQ(rhs, cd.xMax)) {
                return S.False;
              }
            }
            return S.Continue;
          default:
            return F.NIL;
        }
      }
      return F.NIL;
    }
  }

  public Reduce() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IAST vars;
    ISymbol domain = S.Reals;
    if (ast.isAST2() || ast.isAST3()) {
      if (ast.arg2().isList()) {
        vars = (IAST) ast.arg2();
      } else {
        vars = F.List(ast.arg2());
      }
    } else {
      VariablesSet eVar = new VariablesSet(ast.arg1());
      vars = eVar.getVarList();
    }
    if (!vars.isList1()) {
      return F.NIL;
    }

    if (ast.isAST3()) {
      if (ast.arg3().isSymbol()) {
        domain = (ISymbol) ast.arg3();
      } else {
        return F.NIL;
      }
    }
    if (domain != S.Reals) {
      return F.NIL;
    }
    IExpr expr = ast.arg1();
    if (ast.arg1().isList()) {
      expr = ((IAST) expr).setAtCopy(0, S.And);
    } else if (!expr.isBooleanFunction()) {
      if (!expr.isComparatorFunction()) {
        expr = F.And(expr);
      } else {
        return F.NIL;
      }
    }
    IExpr variable = vars.get(1);
    IExpr logicalExpand = S.LogicalExpand.of(engine, expr);
    if (logicalExpand.isTrue() || logicalExpand.isFalse()) {
      return logicalExpand;
    }

    ReduceComparison rc = new ReduceComparison(variable);
    // may throw ArgumentTypeException
    IExpr reduced = rc.evaluate(logicalExpand);
    return reduced.orElse(logicalExpand);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_3;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    //
  }
}
