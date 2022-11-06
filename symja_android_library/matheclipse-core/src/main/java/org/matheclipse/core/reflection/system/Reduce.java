package org.matheclipse.core.reflection.system;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Reduce extends AbstractEvaluator {

  static class ReduceComparison {

    final IExpr variable;

    /**
     * Implements value interval for <code>variable</code> as the interval: <code>
     * xMin (minType) variable (maxType) xMax</code>.
     *
     * <p>
     * <code>minType</code> and <code>maxType</code> define if it is an open interval (value == 1
     * (LessThan) ) or a closed interval (value == 2 (LessEqualThan) or value == 3 (EQUAL))
     */
    static class VariableInterval {

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
          // | this.xMin <= cd.xMin ... |
          if (S.LessEqual.ofQ(cd.xMax, this.xMax)) {
            // this interval includes cd interval
            // | this.xMin <= cd.xMin <--> cd.Max <= this.xMax |
            return true;
          }
        } else if (S.GreaterEqual.ofQ(this.xMin, cd.xMin)) {
          // | cd.xMin <= this.xMin ... |
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
            // | cd.xMin <= this.xMax <--> this.xMax <= cd.xMax |
            this.xMax = cd.xMax;
            this.maxType = cd.maxType;
            return true;
          }
        } else if (S.Greater.ofQ(this.xMin, cd.xMin)) {
          if (S.Less.ofQ(this.xMin, cd.xMax)) {
            // | cd.xMin <= this.xMin <--> this.xMin <= cd.xMax |
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
          IAST gt = (minType >= 2) //
              ? F.GreaterEqual(variable, xMin)
              : F.Greater(variable, xMin);
          if (!xMax.equals(F.CInfinity)) {
            IAST lt = (maxType >= 2) //
                ? F.LessEqual(variable, xMax)
                : F.Less(variable, xMax);
            return F.And(gt, lt);
          }
          return gt;
        } else if (!xMax.equals(F.CInfinity)) {
          IAST lt = (maxType >= 2) //
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

      public IExpr reduceAnd(int headID, IExpr rhs) throws ArgumentTypeException {
        try {
          Complex c = rhs.evalfc();
          if (c != null && !F.isZero(c.getImaginary())) {
            // complex values are not allowed in intervals
            return S.False;
          }
        } catch (ArgumentTypeException ate) {
          // fall through
        }
        switch (headID) {
          case ID.Equal:
            if (S.GreaterEqual.ofQ(rhs, xMin) && S.LessEqual.ofQ(rhs, xMax)) {
              if (maxType == 1 && S.Equal.ofQ(rhs, xMax)) {
                return S.False;
              }
              if (minType == 1 && S.Equal.ofQ(rhs, xMin)) {
                return S.False;
              }
              xMax = rhs;
              xMin = rhs;
              minType = 3;
              maxType = 3;
              return S.Continue;
            } else {
              IExpr gt = S.Greater.of(rhs, xMax);
              if (gt.isTrue()) {
                return S.False;
              }
              IExpr lt = S.Less.of(rhs, xMin);
              if (lt.isTrue()) {
                return S.False;
              }
              if (gt.isFalse() && lt.isFalse()) {
                xMax = rhs;
                xMin = rhs;
                minType = 3;
                maxType = 3;
                return S.Continue;
              }
            }
            break;
          case ID.Less:
            if (S.Less.ofQ(rhs, xMax)) {
              if (minType == 1 && S.LessEqual.ofQ(rhs, xMin)) {
                return S.False;
              } else if (S.Less.ofQ(rhs, xMin)) {
                return S.False;
              }
              xMax = rhs;
              maxType = 1;
            } else {
              if (S.LessEqual.ofQ(rhs, xMin)) {
                return S.False;
              }
            }
            return S.Continue;
          case ID.LessEqual:
            if (S.LessEqual.ofQ(rhs, xMax)) {
              if (minType == 1 && S.LessEqual.ofQ(rhs, xMin)) {
                return S.False;
              } else if (S.Less.ofQ(rhs, xMin)) {
                return S.False;
              }
              xMax = rhs;
              maxType = 2;
            } else {
              if (S.Less.ofQ(rhs, xMin)) {
                return S.False;
              }
            }
            return S.Continue;
          case ID.Greater:
            if (S.Greater.ofQ(rhs, xMin)) {
              if (maxType == 1 && S.GreaterEqual.ofQ(rhs, xMax)) {
                return S.False;
              } else if (S.Greater.ofQ(rhs, xMax)) {
                return S.False;
              }
              xMin = rhs;
              minType = 1;
            } else {
              if (S.GreaterEqual.ofQ(rhs, xMax)) {
                return S.False;
              }
            }
            return S.Continue;
          case ID.GreaterEqual:
            if (S.GreaterEqual.ofQ(rhs, xMin)) {
              if (maxType == 1 && S.GreaterEqual.ofQ(rhs, xMax)) {
                return S.False;
              } else if (S.Greater.ofQ(rhs, xMax)) {
                return S.False;
              }
              xMin = rhs;
              minType = 2;
            } else {
              if (S.Greater.ofQ(rhs, xMax)) {
                return S.False;
              }
            }
            return S.Continue;
          default:
            return F.NIL;
        }
        return F.NIL;
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
        IExpr temp = reduceAnd((IAST) expr, cd);
        if (temp.isPresent()) {
          if (temp == S.Continue) {
            return cd.toExpr();
          }
          return temp;
        }
        return F.NIL;
      } else if (expr.isAST(S.Or)) {
        VariableInterval cd = new VariableInterval(variable);
        IExpr temp = reduceOr((IAST) expr, cd);
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

    private IExpr reduceOr(IAST expr, VariableInterval cd) throws ArgumentTypeException {
      IAST orAST = expr;
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
          IExpr temp = reduceAnd((IAST) arg, andCD);
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
            temp = comparatorCD.reduceAnd(temp.headID(), temp.second());
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

    /**
     * Try to reduce an {@link S#And} AST.
     *
     * @param andExpr the {@link S#And} AST
     * @param variableInterval
     * @return {@link S#Continue}, if all condition terms could be reduced (evaluated) in <code>
     *     variableInterval
     *     </code>, an {@link S#And} AST if some parts could be evaluated, {@link F#NIL} otherwise
     * @throws ArgumentTypeException
     */
    public IExpr reduceAnd(IAST andExpr, VariableInterval variableInterval)
        throws ArgumentTypeException {
      IASTMutable andAST = andExpr.copy();
      if (andAST.isAST0()) {
        throw new ArgumentTypeException("And: size == 0");
      }
      if (andAST.isAST1()) {
        return andAST.arg1();
      }

      boolean andEvaled = false;
      boolean variableInternalContinued = true;
      IExpr lastArg = rewriteVariableValue(variableInterval, andAST.arg1());
      int lastIndex = 1;
      IExpr temp = F.NIL;
      if (lastArg.isAST2() && lastArg.first().equals(variableInterval.variable)) {
        temp = variableInterval.reduceAnd(lastArg.headID(), lastArg.second());
        if (temp.isPresent()) {
          if (temp.isFalse()) {
            return S.False;
          }
          if (temp != S.Continue) {
            andAST.set(1, temp);
            andEvaled = true;
            lastArg = temp;
          }
        }
      }
      if (temp != S.Continue) {
        variableInternalContinued = false;
      }

      for (int i = 2; i < andAST.size(); i++) {
        // TODO check Simplify result
        IExpr arg = rewriteVariableValue(variableInterval, andAST.get(i));
        IExpr reducedArg = F.NIL;
        if (arg.isAST2() && arg.first().equals(variableInterval.variable)) {
          reducedArg = variableInterval.reduceAnd(arg.headID(), arg.second());
          if (reducedArg.isPresent()) {
            if (reducedArg.isFalse()) {
              return S.False;
            }
            if (reducedArg != S.Continue) {
              andAST.set(i, reducedArg);
              andEvaled = true;
              arg = reducedArg;
            }
          }
          if (reducedArg != S.Continue) {
            variableInternalContinued = false;
          }
          IASTMutable orAST = F.NIL;
          boolean evaled = false;
          if (arg.isComparatorFunction() && lastArg.isAST(S.Or)) {
            orAST = ((IAST) lastArg).copy();
            evaled = mapOrReduced(arg, orAST);

          } else if (lastArg.isComparatorFunction() && arg.isAST(S.Or)) {
            orAST = ((IAST) arg).copy();
            evaled = mapOrReduced(lastArg, orAST);
          }
          // TODO if 2 Or() expressions are involved, create a distributed And() expression
          if (evaled) {
            temp = EvalEngine.get().evaluate(orAST);
            andAST.set(lastIndex, S.True);
            lastArg = temp;
            lastIndex = i;
            andAST.set(i, lastArg);
            andEvaled = true;
          }
        }
      }
      if (andEvaled) {
        return andAST;
      }
      if (variableInternalContinued) {
        return S.Continue;
      }
      return F.NIL;
    }

    private boolean mapOrReduced(IExpr arg, IASTMutable orAST) {
      boolean evaled = false;
      for (int j = 1; j < orAST.size(); j++) {
        IExpr r = reduceAndBinary(arg, orAST.get(j));
        if (r.isPresent()) {
          orAST.set(j, r);
          evaled = true;
        }
      }
      return evaled;
    }

    private IExpr rewriteVariableValue(VariableInterval cd, IExpr lastArg) {
      if (lastArg.isEqual()) {
        IAST[] reduced =
            Eliminate.eliminateOneVariable(F.list(lastArg), cd.variable, false, EvalEngine.get());
        if (reduced != null && reduced[0].isEmptyList() && reduced[1].isRule()) {
          IAST rule = reduced[1];
          lastArg = F.Equal(variable, rule.second());
        }
      } else {
        lastArg = S.Simplify.of(lastArg, cd.variable);
      }
      return lastArg;
    }

    private IExpr reduceAndBinary(IExpr arg, IExpr orArg) {
      // VariableInterval cd = new VariableInterval(variable);
      // IExpr reduced = cd.reduceComparator(arg, orArg);
      // if (reduced.isPresent()) {
      // if (reduced == S.Continue || reduced.isAST(S.And)) {
      // return F.NIL;
      // }
      // return reduced;
      // }
      // return F.NIL;

      ReduceComparison rcAnd = new ReduceComparison(variable);
      IExpr reduced = rcAnd.evaluate(F.And(arg, orArg));
      if (reduced.isPresent()) {
        if (reduced == S.Continue || reduced.isAST(S.And)) {
          return F.NIL;
        }
        return reduced;
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
        vars = F.list(ast.arg2());
      }
    } else {
      VariablesSet eVar = new VariablesSet(ast.arg1());
      vars = eVar.getVarList();
    }
    if (!vars.isList1()) {
      // only univariate expressions allowed atm
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
    try {
      IExpr expr = ast.arg1();
      if (ast.arg1().isList()) {
        expr = ((IAST) expr).setAtCopy(0, S.And);
      } else if (!expr.isBooleanFunction()) {
        if (!expr.isComparatorFunction()) {
          expr = F.And(expr);
        }
      }
      IExpr variable = vars.get(1);
      IExpr logicalExpand = S.LogicalExpand.of(engine, expr);
      if (logicalExpand.isTrue() || logicalExpand.isFalse()) {
        return logicalExpand;
      }
      if (!logicalExpand.isBooleanFunction()) {
        logicalExpand = F.And(logicalExpand);
      }

      if (logicalExpand.isAST(S.And)) {
        IAST andAST = (IAST) logicalExpand;
        IASTMutable andResult = andAST.copy();
        for (int i = 1; i < andAST.size(); i++) {
          IExpr arg = andAST.get(i);
          if (arg.isEqual()) {
            IExpr roots = S.Roots.ofNIL(engine, arg, variable);
            if (roots.isPresent()) {
              andResult.set(i, roots);
            }
          }
        }

        logicalExpand = S.LogicalExpand.of(engine, andResult);
        if (logicalExpand.isTrue() || logicalExpand.isFalse()) {
          return logicalExpand;
        }
      }

      ReduceComparison rc = new ReduceComparison(variable);
      // may throw ArgumentTypeException
      IExpr reduced = rc.evaluate(logicalExpand);
      return reduced.orElse(logicalExpand);
    } catch (RuntimeException rex) {
      rex.printStackTrace();
    }
    return F.NIL;
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
