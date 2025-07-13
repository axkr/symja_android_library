package org.matheclipse.core.eval;

import java.util.Optional;
import java.util.function.Predicate;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

public class AlgebraUtil {
  private static class DistributeAlgorithm {
    final IASTAppendable resultCollector;
    final IExpr head;
    final IAST arg1;
    boolean evaled;

    DistributeAlgorithm(IASTAppendable resultCollector, IExpr head, IAST arg1) {
      this.resultCollector = resultCollector;
      this.head = head;
      this.arg1 = arg1;
      this.evaled = false;
    }

    public boolean distribute(final IAST ast) {
      IASTAppendable stepResult;
      final int stepSize = arg1.size();
      if (ast.size() >= 6) {
        stepResult = F.ast(ast.arg5(), stepSize);
      } else {
        stepResult = F.ast(arg1.head(), stepSize);
      }
      distributePositionRecursive(stepResult, 1);
      return evaled;
    }

    public void distributePositionRecursive(IASTAppendable stepResult, int position) {
      if (arg1.size() == position) {
        resultCollector.append(stepResult);
        return;
      }
      if (arg1.size() < position) {
        return;
      }
      if (arg1.get(position).isAST(head)) {
        IAST temp = (IAST) arg1.get(position);
        temp.forEach((IExpr x) -> distributeStep(x, stepResult, position));
        evaled = true;
      } else {
        IASTAppendable res2 = stepResult;
        res2.append(arg1.get(position));
        distributePositionRecursive(res2, position + 1);
      }
    }

    private void distributeStep(IExpr x, IAST stepResult, int position) {
      IASTAppendable res2 = stepResult.appendClone(x);
      // res2.append(x);
      distributePositionRecursive(res2, position + 1);
    }
  }

  private static boolean appendPlus(IASTAppendable ast, IExpr expr) {
    if (ast.head().equals(S.Plus) && expr.head().equals(S.Plus)) {
      return ast.appendArgs((IAST) expr);
    }
    return ast.append(expr);
  }

  /**
   * Get the &quot;denominator form&quot; of the given function. Example: <code>Csc[x]</code> gives
   * <code>Sin[x]</code>.
   *
   * @param function the function which should be transformed to &quot;denominator form&quot;
   *        determine the denominator by splitting up functions like <code>Tan[],Cot[], Csc[],...
   *     </code>
   * @param trig
   * @return {@link F#NIL} if <code>trig</code> is false or no form is found; may return
   *         <code>1</code> if no denominator form is available (Example Cos[]).
   */
  public static IExpr denominatorTrigForm(IAST function, boolean trig) {
    if (trig) {
      if (function.isAST1()) {
        for (int i = 0; i < F.DENOMINATOR_NUMERATOR_SYMBOLS.size(); i++) {
          final ISymbol symbol = F.DENOMINATOR_NUMERATOR_SYMBOLS.get(i);
          if (function.head().equals(symbol)) {
            IExpr result = F.DENOMINATOR_TRIG_TRUE_EXPRS.get(i);
            if (result.isSymbol()) {
              return F.unaryAST1(result, function.arg1());
            }
            return result;
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Call the distribute algorithm <code>F.Distribute(expr, head)</code>
   * 
   * @param ast
   * @param head
   */
  public static IExpr distribute(final IAST ast, IExpr head) {
    IAST list = (IAST) ast.arg1();
    IASTAppendable resultCollector;
    final int resultSize = list.argSize() > 127 ? list.argSize() : 127;
    if (ast.size() >= 5) {
      resultCollector = F.ast(ast.arg4(), resultSize);
    } else {
      resultCollector = F.ast(head, resultSize);
    }
    DistributeAlgorithm algorithm = new DistributeAlgorithm(resultCollector, head, list);
    if (algorithm.distribute(ast)) {
      return resultCollector;
    }
    return list;
  }

  /**
   * Call the distribute algorithm for a <code>Times(...)</code> {@link IAST}, which contains
   * <code>Plus(...)</code> terms. If <code>expr.isTimes() == false</code> return <code>expr</code>
   * 
   * @param expr should have the structure <code>Times(a,b,...)</code> with at least 2 arguments;
   *        otherwise the <code>expr</code> will be returned
   */
  public static IExpr distributeTimes(final IExpr expr) {
    if (expr.isTimes()) {
      return distribute(F.Distribute(expr), S.Plus);
    }
    return expr;
  }

  /**
   * Expand the given <code>ast</code> expression.
   *
   * @param ast
   * @param patt
   * @param distributePlus TODO
   * @param evalParts evaluate the determined numerator and denominator parts
   * @return {@link F#NIL} if the expression couldn't be expanded.
   */
  public static IExpr expand(final IAST ast, Predicate<IExpr> patt, boolean expandNegativePowers,
      boolean distributePlus, boolean evalParts) {

    return expand(ast, patt, expandNegativePowers, distributePlus, evalParts, false);
  }

  /**
   * Expand the given <code>ast</code> expression.
   *
   * @param ast
   * @param patt
   * @param evalParts evaluate the determined numerator and denominator parts
   * @param distributePlus
   * @param factorTerms
   * @return {@link F#NIL} if the expression couldn't be expanded.
   */
  public static IExpr expand(final IAST ast, Predicate<IExpr> patt, boolean expandNegativePowers,
      boolean distributePlus, boolean evalParts, boolean factorTerms) {
    Algebra.Expand.Expander expander = new Algebra.Expand.Expander(patt, expandNegativePowers,
        distributePlus, evalParts, factorTerms);
    return expander.expandAST(ast);
  }

  /**
   * Expand the given <code>ast</code> expression.
   *
   * @param ast
   * @param distributePlus
   * @return {@link F#NIL} if the expression couldn't be expanded.
   */
  public static IExpr expandAll(final IAST ast, Predicate<IExpr> patt, boolean expandNegativePowers,
      boolean distributePlus, boolean factorTerms, EvalEngine engine) {
    if (patt != null && ast.isFree(patt, true)) {
      return F.NIL;
    }
    IAST localAST = ast;
    IAST tempAST = F.NIL;
    if (localAST.isEvalFlagOff(IAST.IS_SORTED)) {
      tempAST = engine.evalFlatOrderlessAttrsRecursive(localAST);
      if (tempAST.isPresent()) {
        localAST = tempAST;
      }
    }
    if (localAST.isAllExpanded() && expandNegativePowers && !distributePlus) {
      if (localAST != ast) {
        return localAST;
      }
      return F.NIL;
    }
    IASTAppendable[] result = new IASTAppendable[1];
    result[0] = F.NIL;
    IExpr temp = F.NIL;

    int localASTSize = localAST.size();
    IExpr head = localAST.head();
    if (head.isAST()) {
      temp =
          expandAll((IAST) head, patt, expandNegativePowers, distributePlus, factorTerms, engine);
      temp.ifPresent(x -> result[0] = F.ast(x, localASTSize));
    }
    final IAST localASTFinal = localAST;
    localAST.forEach((x, i) -> {
      if (x.isAST()) {
        IExpr t =
            expandAll((IAST) x, patt, expandNegativePowers, distributePlus, factorTerms, engine);
        if (t.isPresent()) {
          if (result[0].isNIL()) {
            int size = localASTFinal.size() + 4; // 4 -> empirically determined value in JUnit tests
            if (t.isAST()) {
              size += ((IAST) t).size();
            }
            result[0] = F.ast(head, size);
            result[0].appendArgs(localASTFinal, i);
          }
          appendPlus(result[0], t);
          return;
        }
      }
      result[0].ifAppendable(r -> r.append(x));
    });

    if (result[0].isNIL()) {
      temp = expand(localAST, patt, expandNegativePowers, distributePlus, true, factorTerms);
      if (temp.isPresent()) {
        setAllExpanded(temp, expandNegativePowers, distributePlus);
        return temp;
      } else {
        if (localAST != ast) {
          setAllExpanded(localAST, expandNegativePowers, distributePlus);
          return localAST;
        }
      }
      setAllExpanded(ast, expandNegativePowers, distributePlus);
      return F.NIL;
    }
    temp = expand(result[0], patt, expandNegativePowers, distributePlus, true, factorTerms);
    if (temp.isPresent()) {
      return setAllExpanded(temp, expandNegativePowers, distributePlus);
    }
    return setAllExpanded(result[0], expandNegativePowers, distributePlus);
  }

  /**
   * Split the expression into numerator and denominator parts, by separating positive and negative
   * powers and afterwards evaluate the numerator and denominator separately.
   *
   * @param arg
   * @param trig determine the denominator by splitting up functions like <code>
   *     Tan[],Cot[], Csc[],...</code>
   * @return the numerator and denominator expression or {@link Optional#empty()} if no denominator
   *         was found.
   */
  public static Optional<IExpr[]> fractionalParts(final IExpr arg, boolean trig) {
    return fractionalParts(arg, trig, true);
  }


  /**
   * Split the expression into numerator and denominator parts, by separating positive and negative
   * powers.
   *
   * @param arg
   * @param trig determine the denominator by splitting up functions like <code>
   *     Tan[],Cot[], Csc[],...</code>
   * @param evalParts evaluate the numerator and denominator separately
   * @return the numerator and denominator expression or {@link Optional#empty()} if no denominator
   *         was found.
   */
  public static Optional<IExpr[]> fractionalParts(final IExpr arg, boolean trig,
      boolean evalParts) {
    if (arg.isAST()) {
      IAST ast = (IAST) arg;
      if (arg.isTimes()) {
        return fractionalPartsTimesPower(ast, false, true, trig, evalParts, true, true);
      } else if (arg.isPower()) {
        return fractionalPartsPower(ast, trig, true);
      } else {
        IExpr numerForm = numeratorTrigForm(ast, trig);
        if (numerForm.isPresent()) {
          IExpr denomForm = denominatorTrigForm(ast, trig);
          if (denomForm.isPresent()) {
            IExpr[] parts = new IExpr[2];
            parts[0] = numerForm;
            parts[1] = denomForm;
            return Optional.of(parts);
          }
        }
      }
    }
    return Optional.empty();
  }


  /**
   * Return the denominator for the given <code>Power[...]</code> {@link IAST} by separating
   * positive and negative powers.
   *
   * @param powerAST a power expression (a^b)
   * @param trig if <code>true</code> get the "trigonometric form" of the given function. Example:
   *        Csc[x] gives Sin[x].
   * @param splitPowerPlusExponents split <code>Power()</code> expressions with <code>Plus()
   *     </code> exponents like <code>a^(-x+y)</code> into numerator <code>a^y</code> and
   *        denominator <code>a^x</code>
   * @return the numerator and denominator expression or {@link Optional#empty()}
   */
  public static Optional<IExpr[]> fractionalPartsPower(final IAST powerAST, boolean trig,
      boolean splitPowerPlusExponents) {
    IExpr[] parts = new IExpr[2];
    parts[0] = F.C1;

    IExpr base = powerAST.base();
    IExpr exponent = powerAST.exponent();
    if (exponent.isReal()) {
      IReal sn = (IReal) exponent;
      if (sn.isMinusOne()) {
        parts[1] = base;
        return Optional.of(parts);
      } else if (sn.isNegative()) {
        parts[1] = F.Power(base, sn.negate());
        return Optional.of(parts);
      } else {
        if (sn.isInteger() && base.isAST()) {
          // positive integer
          IAST function = (IAST) base;
          // if (function.isTimes()) {
          // IExpr[] partsArg1 = fractionalPartsTimesPower(function, true, true, trig,
          // true);
          // if (partsArg1 != null) {
          // parts[0] = F.Power(partsArg1[0], sn);
          // parts[1] = F.Power(partsArg1[1], sn);
          // return parts;
          // }
          // }
          IExpr numerForm = numeratorTrigForm(function, trig);
          if (numerForm.isPresent()) {
            IExpr denomForm = denominatorTrigForm(function, trig);
            if (denomForm.isPresent()) {
              parts[0] = F.Power(numerForm, sn);
              parts[1] = F.Power(denomForm, sn);
              return Optional.of(parts);
            }
          }
        }
      }
    } else if (splitPowerPlusExponents && exponent.isPlus()) {
      // base ^ (a+b+c...)
      IAST plusAST = (IAST) exponent;
      IAST[] result = plusAST.filterNIL(AbstractFunctionEvaluator::getNormalizedNegativeExpression);
      IAST plus = result[0];
      if (plus.argSize() > 0) {
        parts[1] = base.power(plus.oneIdentity0());
        parts[0] = base.power(result[1].oneIdentity0());
        return Optional.of(parts);
      }
      return Optional.empty();
    }
    IExpr positiveExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(exponent);
    if (positiveExpr.isPresent()) {
      parts[1] = F.Power(base, positiveExpr);
      return Optional.of(parts);
    }
    return Optional.empty();
  }

  /**
   * Split the expression into numerator and denominator parts, by separating positive and negative
   * powers. Or split a number by numerator and denominator part.
   *
   * @param arg
   * @param trig determine the denominator by splitting up functions like <code>
   *     Tan[],Cot[], Csc[],...</code>
   * @param evalParts evaluate the numerator and denominator separately
   * @return the numerator and denominator expression or {@link Optional#empty()}
   */
  public static Optional<IExpr[]> fractionalPartsRational(final IExpr arg, boolean trig,
      boolean evalParts) {
    if (arg.isFraction()) {
      IFraction fr = (IFraction) arg;
      IExpr[] parts = new IExpr[2];
      parts[0] = fr.numerator();
      parts[1] = fr.denominator();
      return Optional.of(parts);
    } else if (arg.isComplex()) {
      IRational re = ((IComplex) arg).getRealPart();
      IRational im = ((IComplex) arg).getImaginaryPart();
      if (re.isFraction() || im.isFraction()) {
        IExpr[] parts = new IExpr[2];
        parts[0] = re.numerator().times(im.denominator())
            .add(im.numerator().times(re.denominator()).times(F.CI));
        parts[1] = re.denominator().times(im.denominator());
        return Optional.of(parts);
      }
      return Optional.empty();
    }
    return fractionalParts(arg, trig, evalParts);
  }

  /**
   * Return the numerator and denominator for the given <code>Times[...]</code> or <code>Power[a, b]
   * </code> AST, by separating positive and negative powers.
   *
   * @param timesPower a Times[] or Power[] expression (a*b*c....) or a^b
   * @param splitNumeratorOne split a fractional number into numerator and denominator, only if the
   *        numerator is 1, if <code>true</code>, ignore <code>splitFractionalNumbers</code>
   *        parameter.
   * @param splitFractionalNumbers split a fractional number into numerator and denominator
   * @param trig try to find a trigonometric numerator/denominator form (Example: <code>Csc[x]
   *     </code> gives <code>1 / Sin[x]</code>)
   * @param evalParts evaluate the determined numerator and denominator parts
   * @param negateNumerDenom negate numerator and denominator, if they are both negative
   * @param splitPowerPlusExponents split <code>Power()</code> expressions with <code>Plus()</code>
   *        exponents like <code>a^(-x+y)</code> into numerator <code>a^y</code> and denominator
   *        <code>
   *     a^x</code>
   * @return the numerator and denominator expression and an optional fractional number (maybe
   *         <code>null</code>), if splitNumeratorOne is <code>true</code>
   */
  public static Optional<IExpr[]> fractionalPartsTimesPower(final IAST timesPower,
      boolean splitNumeratorOne, boolean splitFractionalNumbers, boolean trig, boolean evalParts,
      boolean negateNumerDenom, boolean splitPowerPlusExponents) {
    if (timesPower.isPower()) {
      return fractionalPartsPower(timesPower, trig, splitPowerPlusExponents);
    }

    IAST timesAST = timesPower;
    IExpr[] result = new IExpr[3];
    result[2] = null;
    IASTAppendable numerator = F.TimesAlloc(timesAST.size());
    IASTAppendable denominator = F.TimesAlloc(timesAST.size());

    IAST argAST;
    boolean evaled = false;
    boolean splitFractionEvaled = false;
    for (int i = 1; i < timesAST.size(); i++) {
      final IExpr arg = timesAST.get(i);
      if (arg.isAST()) {
        argAST = (IAST) arg;
        if (trig && argAST.isAST1()) {
          IExpr numerForm = numeratorTrigForm(argAST, trig);
          if (numerForm.isPresent()) {
            IExpr denomForm = denominatorTrigForm(argAST, trig);
            if (denomForm.isPresent()) {
              if (!numerForm.isOne()) {
                numerator.append(numerForm);
              }
              if (!denomForm.isOne()) {
                denominator.append(denomForm);
              }
              evaled = true;
              continue;
            }
          }
        } else if (arg.isPower()) {
          Optional<IExpr[]> parts = fractionalPartsPower((IAST) arg, trig, splitPowerPlusExponents);
          if (parts.isPresent()) {
            IExpr[] elements = parts.get();
            if (!elements[0].isOne()) {
              numerator.append(elements[0]);
            }
            if (!elements[1].isOne()) {
              denominator.append(elements[1]);
            }
            evaled = true;
            continue;
          }
        }
      } else if (i == 1) {
        if (arg.isFraction()) {
          if (splitNumeratorOne) {
            IFraction fr = (IFraction) arg;
            if (fr.numerator().isOne()) {
              denominator.append(fr.denominator());
              splitFractionEvaled = true;
              continue;
            }
            if (fr.numerator().isMinusOne()) {
              numerator.append(fr.numerator());
              denominator.append(fr.denominator());
              splitFractionEvaled = true;
              continue;
            }
            result[2] = fr;
            continue;
          } else if (splitFractionalNumbers) {
            IFraction fr = (IFraction) arg;
            if (!fr.numerator().isOne()) {
              numerator.append(fr.numerator());
            }
            denominator.append(fr.denominator());
            evaled = true;
            continue;
          }
        } else if (arg.isComplex()) {
          IComplex cmp = (IComplex) arg;
          if (splitFractionalNumbers) {
            IRational re = cmp.getRealPart();
            IRational im = cmp.getImaginaryPart();
            if (re.isFraction() || im.isFraction()) {
              numerator.append(re.numerator().times(im.denominator())
                  .add(im.numerator().times(re.denominator()).times(F.CI)));
              denominator.append(re.denominator().times(im.denominator()));
              evaled = true;
              continue;
            }
          }

          // if (cmp.re().isZero() && cmp.im().isFraction()) {
          // IFraction fr = (IFraction) cmp.im();
          // if (splitNumeratorOne) {
          // if (fr.numerator().isOne()) {
          // numerator.append(F.CI);
          // denominator.append(fr.denominator());
          // splitFractionEvaled = true;
          // continue;
          // }
          // if (fr.numerator().isMinusOne()) {
          // numerator.append(F.CNI);
          // denominator.append(fr.denominator());
          // splitFractionEvaled = true;
          // continue;
          // }
          // } else
          // if (splitFractionalNumbers) {
          // numerator.append(F.CC(F.C0, fr.numerator()));
          // denominator.append(fr.denominator());
          // evaled = true;
          // continue;
          // }
          // }
        }
      }
      numerator.append(arg);
    }
    if (evaled) {
      if (evalParts) {
        result[0] = F.eval(numerator);
        result[1] = F.eval(denominator);
      } else {
        result[0] = numerator.oneIdentity1();
        result[1] = denominator.oneIdentity1();
      }
      if (negateNumerDenom && result[0].isNumber() && result[0].isNegative() && result[1].isPlus()
          && result[1].isAST2()) {
        // negate numerator and denominator:
        result[0] = result[0].negate();
        result[1] = result[1].negate();
      }
      return Optional.of(result);
    }
    if (splitFractionEvaled) {
      result[0] = numerator.oneIdentity1();
      if (!result[0].isTimes() && !result[0].isPlus()) {
        result[1] = denominator.oneIdentity1();
        return Optional.of(result);
      }
      if (result[0].isTimes() && result[0].isAST2() && ((IAST) result[0]).arg1().isMinusOne()) {
        result[1] = denominator.oneIdentity1();
        return Optional.of(result);
      }
    }
    return Optional.empty();
  }

  /**
   * Split the {@link IAST} expression into numerator and denominator parts, by calling the
   * <code>Numerator(ast)</code> and <code>Denominator(ast)</code> functions and return the result
   * at index <code>0</code> (numerator) and index <code>1</code> (denominator).
   *
   * @param ast
   * @param together if <code>true</code> the evaluated <code>Together(ast)</code> result, will be
   *        appended at index <code>2</code> in the result array
   * @return an array with the numerator, denominator and the evaluated <code>Together(ast)</code>
   *         if requested.
   */
  public static IExpr[] numeratorDenominator(IAST ast, boolean together, EvalEngine engine) {
    if (together) {
      boolean noSimplifyMode = engine.isNoSimplifyMode();
      try {
        engine.setNoSimplifyMode(true);
        IExpr[] result = new IExpr[3];
        result[2] = together(ast, engine);
        // result[2] = engine.evaluate(F.Together(ast));
        return splitNumeratorDenominator(ast, result[2], result, engine);
      } finally {
        engine.setNoSimplifyMode(noSimplifyMode);
      }
    }

    IExpr[] result = new IExpr[2];
    return splitNumeratorDenominator(ast, ast, result, engine);
  }

  /**
   * Get the &quot;numerator form&quot; of the given function. Example: <code>Csc[x]</code> gives
   * <code>1</code>.
   *
   * @param function the function which should be transformed to &quot;denominator form&quot;
   *        determine the denominator by splitting up functions like <code>Tan[9,Cot[], Csc[],...
   *     </code>
   * @param trig
   */
  public static IExpr numeratorTrigForm(IAST function, boolean trig) {
    if (trig) {
      if (function.isAST1()) {
        for (int i = 0; i < F.DENOMINATOR_NUMERATOR_SYMBOLS.size(); i++) {
          final ISymbol symbol = F.DENOMINATOR_NUMERATOR_SYMBOLS.get(i);
          if (function.head().equals(symbol)) {
            final IExpr result = F.NUMERATOR_TRIG_TRUE_EXPRS.get(i);
            if (result.isSymbol()) {
              return F.unaryAST1(result, function.arg1());
            }
            return result;
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Expand the given <code>ast</code> expression.
   *
   * @param ast
   * @param distributePlus
   * @return {@link F#NIL} if the expression couldn't be expanded.
   */
  // public static IExpr expandAll(final IAST ast, Predicate<IExpr> patt, boolean
  // expandNegativePowers,
  // boolean distributePlus, boolean factorTerms, EvalEngine engine) {
  // if (patt != null && ast.isFree(patt, true)) {
  // return F.NIL;
  // }
  // IAST localAST = ast;
  // IAST tempAST = F.NIL;
  // if (localAST.isEvalFlagOff(IAST.IS_SORTED)) {
  // tempAST = engine.evalFlatOrderlessAttrsRecursive(localAST);
  // if (tempAST.isPresent()) {
  // localAST = tempAST;
  // }
  // }
  // if (localAST.isAllExpanded() && expandNegativePowers && !distributePlus) {
  // if (localAST != ast) {
  // return localAST;
  // }
  // return F.NIL;
  // }
  // IASTAppendable[] result = new IASTAppendable[1];
  // result[0] = F.NIL;
  // IExpr temp = F.NIL;
  //
  // int localASTSize = localAST.size();
  // IExpr head = localAST.head();
  // if (head.isAST()) {
  // temp =
  // expandAll((IAST) head, patt, expandNegativePowers, distributePlus, factorTerms, engine);
  // temp.ifPresent(x -> result[0] = F.ast(x, localASTSize));
  // }
  // final IAST localASTFinal = localAST;
  // localAST.forEach((x, i) -> {
  // if (x.isAST()) {
  // IExpr t =
  // expandAll((IAST) x, patt, expandNegativePowers, distributePlus, factorTerms, engine);
  // if (t.isPresent()) {
  // if (result[0].isNIL()) {
  // int size = localASTFinal.size() + 4; // 4 -> empirically determined value in JUnit tests
  // if (t.isAST()) {
  // size += ((IAST) t).size();
  // }
  // result[0] = F.ast(head, size);
  // result[0].appendArgs(localASTFinal, i);
  // }
  // Algebra.appendPlus(result[0], t);
  // return;
  // }
  // }
  // result[0].ifAppendable(r -> r.append(x));
  // });
  //
  // if (result[0].isNIL()) {
  // temp = expand(localAST, patt, expandNegativePowers, distributePlus, true, factorTerms);
  // if (temp.isPresent()) {
  // setAllExpanded(temp, expandNegativePowers, distributePlus);
  // return temp;
  // } else {
  // if (localAST != ast) {
  // setAllExpanded(localAST, expandNegativePowers, distributePlus);
  // return localAST;
  // }
  // }
  // setAllExpanded(ast, expandNegativePowers, distributePlus);
  // return F.NIL;
  // }
  // temp = expand(result[0], patt, expandNegativePowers, distributePlus, true, factorTerms);
  // if (temp.isPresent()) {
  // return setAllExpanded(temp, expandNegativePowers, distributePlus);
  // }
  // return setAllExpanded(result[0], expandNegativePowers, distributePlus);
  // }

  public static IExpr setAllExpanded(IExpr expr, boolean expandNegativePowers,
      boolean distributePlus) {
    if (expr != null && expandNegativePowers && !distributePlus && expr.isAST()) {
      ((IAST) expr).addEvalFlags(IAST.IS_ALL_EXPANDED);
    }
    return expr;
  }

  /**
   * Split <code>rewrittenAST</code> into numerator and denominator.
   * 
   * @param originalAST the original {@link IAST} expression
   * @param rewrittenAST the rewritten AST (for example by {@link S#Together}
   * @param result the allocated result array
   * @param engine the evaluation engine
   * 
   * @return the <code>result</code> array of expressions <code>[numerator, denominator]</code>.
   */
  private static IExpr[] splitNumeratorDenominator(final IAST originalAST, final IExpr rewrittenAST,
      IExpr[] result, EvalEngine engine) {
    result[1] = engine.evaluate(F.Denominator(rewrittenAST));
    if (!result[1].isOne()) {
      result[0] = engine.evaluate(F.Numerator(rewrittenAST));
    } else {
      result[0] = originalAST;
    }
    return result;
  }

  public static IExpr together(IAST ast, EvalEngine engine) {
    IExpr result = Algebra.togetherExpr(ast, engine);
    if (result.isPresent()) {
      return engine.evaluate(result);
    }
    return ast;
  }

  private AlgebraUtil() {
    // private constructor to avoid instantiation
  }

}
