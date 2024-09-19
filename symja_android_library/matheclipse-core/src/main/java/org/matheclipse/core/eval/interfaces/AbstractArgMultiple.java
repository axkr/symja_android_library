package org.matheclipse.core.eval.interfaces;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.OperationSystem;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher;

/** */
public abstract class AbstractArgMultiple extends AbstractArg2 {
  private static final Logger LOGGER = LogManager.getLogger();

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {

    if (ast.isAST2()) {
      IExpr temp = binaryOperator(ast, ast.arg1(), ast.arg2(), engine);
      if (temp.isPresent()) {
        return temp;
      }
      return evaluateHashsRepeated(ast, engine);
    }

    if (ast.size() > 3) {
      IASTAppendable tempAST = ast.copyAppendable();
      final ISymbol sym = tempAST.topHead();
      final IASTAppendable result = F.ast(sym);
      IExpr tres;
      IExpr temp = tempAST.arg1();
      boolean evaled = false;
      int i = 2;

      while (i < tempAST.size()) {

        tres = binaryOperator(ast, temp, tempAST.get(i), engine);

        if (tres.isNIL()) {

          for (int j = i + 1; j < tempAST.size(); j++) {
            tres = binaryOperator(ast, temp, tempAST.get(j), engine);

            if (tres.isPresent()) {
              evaled = true;
              temp = tres;

              tempAST.remove(j);

              break;
            }
          }

          if (tres.isNIL()) {
            result.append(temp);
            if (i == tempAST.argSize()) {
              result.append(tempAST.get(i));
            } else {
              temp = tempAST.get(i);
            }
            i++;
          }

        } else {
          evaled = true;
          temp = tres;

          if (i == tempAST.argSize()) {
            result.append(temp);
          }

          i++;
        }
      }

      if (evaled) {

        if ((result.isAST1()) && sym.hasOneIdentityAttribute()) {
          return result.arg1();
        }

        return result;
      }
      if (tempAST.size() > 2) {
        return evaluateHashsRepeated(tempAST, engine);
      }
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return null;
  }

  /**
   * Return a matcher for an {@link IAST} with attribute {@link S#Orderless} (i.e. WITH HEAD
   * {@link S#Plus} or {@link S#Times}).
   */
  public HashedOrderlessMatcher getHashRuleMap() {
    return null;
  }

  /**
   * Evaluates the given {@link ISymbol#ORDERLESS} AST repeatedly using the HashedOrderlessMatcher.
   * This method is used when the evaluation engine's trigonometric rules are not disabled.
   *
   * @param orderlessAST the AST to be evaluated, which should have the {@link ISymbol#ORDERLESS}
   *        attribute
   * @param engine the evaluation engine used for the evaluation
   * @return the result of the evaluation if the HashedOrderlessMatcher is not null, otherwise
   *         returns {@link F#NIL}
   */
  public IAST evaluateHashsRepeated(final IAST orderlessAST, final EvalEngine engine) {
    if (!engine.isDisabledTrigRules()) {
      HashedOrderlessMatcher hashRuleMap = getHashRuleMap();
      if (hashRuleMap != null) {
        return hashRuleMap.evaluateRepeated(orderlessAST, engine);
      }
    }
    return F.NIL;
  }

  /**
   * Define the rule for the <code>Orderless</code> operator <b>OP</b>. <code>
   * OP[lhs1, lhs2, ...] := OP[rhs, ...] /; condition</code>
   *
   * @param lhs1
   * @param lhs2
   * @param rhs
   * @param condition
   * @see org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher#defineHashRule(org.matheclipse.core.interfaces.IExpr,
   *      org.matheclipse.core.interfaces.IExpr, org.matheclipse.core.interfaces.IExpr,
   *      org.matheclipse.core.interfaces.IExpr)
   */
  public void defineHashRule(IExpr lhs1, IExpr lhs2, IExpr rhs, IExpr condition) {
    getHashRuleMap().defineHashRule(lhs1, lhs2, rhs, condition);
  }

  /**
   * @param lhs1
   * @param lhs2
   * @param rhs
   * @param condition
   * @see org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher#defineHashRule(org.matheclipse.core.interfaces.IExpr,
   *      org.matheclipse.core.interfaces.IExpr, org.matheclipse.core.interfaces.IExpr,
   *      org.matheclipse.core.interfaces.IExpr)
   */
  public void setUpHashRule2(IExpr lhs1, IExpr lhs2, IExpr rhs, IExpr condition) {
    getHashRuleMap().definePatternHashRule(lhs1, lhs2, rhs, condition);
  }

  @Override
  public IExpr binaryOperator(IAST ast, final IExpr o0, final IExpr o1, EvalEngine engine) {
    IExpr result = F.NIL;
    try {
      if (o0 instanceof INumber || o1 instanceof INumber) {
        if (o0 instanceof INum) {
          // use specialized methods for numeric mode
          if (o1 instanceof INum) {
            result = e2DblArg((INum) o0, (INum) o1);
          } else if (o1.isInteger()) {
            result = e2DblArg((INum) o0, F.num((IInteger) o1));
          } else if (o1.isFraction()) {
            result = e2DblArg((INum) o0, F.num((IFraction) o1));
          } else if (o1 instanceof IComplexNum) {
            if (o0 instanceof ApfloatNum) {
              result = e2DblComArg(F.complexNum(((INum) o0).apfloatValue()), (IComplexNum) o1);
            } else {
              result = e2DblComArg(F.complexNum(((INum) o0).getRealPart()), (IComplexNum) o1);
            }
          }
          if (result.isPresent()) {
            if (result.isIndeterminate()) {
              // Indeterminate expression `1` encountered
              Errors.printMessage(ast.topHead(), "indet", F.List(ast), engine);
            }
            return result;
          }
          return e2ObjArg(ast, o0, o1);
        } else if (o1 instanceof INum) {
          // use specialized methods for numeric mode
          if (o0.isInteger()) {
            result = e2DblArg(F.num((IInteger) o0), (INum) o1);
          } else if (o0.isFraction()) {
            result = e2DblArg(F.num((IFraction) o0), (INum) o1);
          } else if (o0 instanceof IComplexNum) {
            if (o1 instanceof ApfloatNum) {
              result = e2DblComArg((IComplexNum) o0, F.complexNum(((INum) o1).apfloatValue()));
            } else {
              result = e2DblComArg((IComplexNum) o0, F.complexNum(((INum) o1).getRealPart()));
            }
          }
          if (result.isPresent()) {
            if (result.isIndeterminate()) {
              // Indeterminate expression `1` encountered
              Errors.printMessage(ast.topHead(), "indet", F.List(ast), engine);
            }
            return result;
          }
          return e2ObjArg(null, o0, o1);
        }

        if (o0 instanceof IComplexNum) {
          // use specialized methods for complex numeric mode
          if (o1.isInteger()) {
            result = e2DblComArg((IComplexNum) o0, F.complexNum((IInteger) o1));
          } else if (o1.isFraction()) {
            result = e2DblComArg((IComplexNum) o0, F.complexNum((IFraction) o1));
          } else if (o1 instanceof IComplexNum) {
            result = e2DblComArg((IComplexNum) o0, (IComplexNum) o1);
          }
          if (result.isPresent()) {
            if (result.isIndeterminate()) {
              // Indeterminate expression `1` encountered
              Errors.printMessage(ast.topHead(), "indet", F.List(ast), engine);
            }
            return result;
          }
          return e2ObjArg(null, o0, o1);
        } else if (o1 instanceof IComplexNum) {
          // use specialized methods for complex numeric mode
          if (o0.isInteger()) {
            result = e2DblComArg(F.complexNum((IInteger) o0), (IComplexNum) o1);
          } else if (o0.isFraction()) {
            result = e2DblComArg(F.complexNum((IFraction) o0), (IComplexNum) o1);
          }
          if (result.isPresent()) {
            if (result.isIndeterminate()) {
              // Indeterminate expression `1` encountered
              Errors.printMessage(ast.topHead(), "indet", F.List(ast), engine);
            }
            return result;
          }
          return e2ObjArg(ast, o0, o1);
        }

        if (o0 instanceof IInteger) {
          if (o1 instanceof IInteger) {
            return e2IntArg((IInteger) o0, (IInteger) o1);
          }
          if (o1 instanceof IFraction) {
            return e2FraArg(F.fraction((IInteger) o0, F.C1), (IFraction) o1);
          }
          if (o1 instanceof IComplex) {
            return e2ComArg(F.CC((IInteger) o0, F.C0), (IComplex) o1);
          }
        } else if (o0 instanceof IFraction) {
          if (o1 instanceof IInteger) {
            return e2FraArg((IFraction) o0, F.fraction((IInteger) o1, F.C1));
          }
          if (o1 instanceof IFraction) {
            return e2FraArg((IFraction) o0, (IFraction) o1);
          }
          if (o1 instanceof IComplex) {
            return e2ComArg(F.CC((IFraction) o0), (IComplex) o1);
          }
        } else if (o0 instanceof IComplex) {
          if (o1 instanceof IInteger) {
            return eComIntArg((IComplex) o0, (IInteger) o1);
          }
          if (o1 instanceof IFraction) {
            return e2ComArg((IComplex) o0, F.CC((IFraction) o1));
          }
          if (o1 instanceof IComplex) {
            return e2ComArg((IComplex) o0, (IComplex) o1);
          }
        }
      }
      result = e2ObjArg(ast, o0, o1);
      if (result.isPresent()) {
        return result;
      }

      if (o0 instanceof ISymbol) {
        if (o1 instanceof ISymbol) {
          return e2SymArg((ISymbol) o0, (ISymbol) o1);
        }
      } else {
        if (o1 instanceof IInteger && o0 instanceof IAST) {
          return eFunIntArg((IAST) o0, (IInteger) o1);
        }
        if (o1 instanceof IAST && o0 instanceof IAST) {
          return e2FunArg((IAST) o0, (IAST) o1);
        }
      }
    } catch (LimitException le) {
      throw le;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
    }
    return F.NIL;
  }

  @Override
  public abstract IExpr e2IntArg(final IInteger i0, final IInteger i1);
}
