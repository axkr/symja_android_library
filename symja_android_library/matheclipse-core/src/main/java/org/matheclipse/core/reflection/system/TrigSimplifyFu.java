package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import java.util.function.Function;
import org.matheclipse.core.builtin.SimplifyFunctions;
import org.matheclipse.core.builtin.StructureFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * See: <a href=
 * "https://github.com/sympy/sympy/blob/master/sympy/simplify/fu.py">sympy/simplify/fu.py</a>
 *
 */
public class TrigSimplifyFu extends AbstractFunctionEvaluator {

  private static class Chain implements Function<IExpr, IExpr> {
    Function<IExpr, IExpr>[] alternative1;
    Function<IExpr, IExpr>[] alternative2;
    Function<IExpr, Long> complexityFunction;
    EvalEngine engine;

    public Chain(Function<IExpr, IExpr>[] alternative1, Function<IExpr, IExpr>[] alternative2,
        Function<IExpr, Long> complexityFunction, EvalEngine engine) {
      this.engine = engine;
      this.complexityFunction = complexityFunction;
      this.alternative1 = alternative1;
      this.alternative2 = alternative2;
    }

    public Chain(Function<IExpr, IExpr> alternative1, Function<IExpr, IExpr>[] alternative2,
        Function<IExpr, Long> complexityFunction, EvalEngine engine) {
      this.engine = engine;
      this.complexityFunction = complexityFunction;
      Function<IExpr, IExpr>[] f1 = new Function[1];
      f1[0] = alternative1;
      this.alternative1 = f1;
      this.alternative2 = alternative2;;
    }

    public Chain(Function<IExpr, IExpr>[] alternative1, Function<IExpr, IExpr> alternative2,
        Function<IExpr, Long> complexityFunction, EvalEngine engine) {
      this.engine = engine;
      this.complexityFunction = complexityFunction;
      this.alternative1 = alternative1;
      Function<IExpr, IExpr>[] f2 = new Function[1];
      f2[0] = alternative2;
      this.alternative2 = f2;
    }

    @Override
    public IExpr apply(IExpr expr) {
      SimplifyFunctions.SimplifiedResult result1 =
          new SimplifyFunctions.SimplifiedResult(F.NIL, complexityFunction.apply(expr));

      for (int i = 0; i < alternative1.length; i++) {
        IExpr temp = expr.replaceAll(alternative1[i]);
        if (temp.isPresent()) {
          temp = engine.evaluate(F.evalExpandAll(temp));
          if (result1.checkLess(temp, complexityFunction.apply(temp))) {
          }
        }
      }
      if (result1.getResult().isPresent()) {
        expr = result1.getResult();
      }
      for (int i = 0; i < alternative2.length; i++) {
        IExpr temp = expr.replaceAll(alternative2[i]);
        if (temp.isPresent()) {
          temp = engine.evaluate(temp);
          result1.checkLess(temp, complexityFunction.apply(temp));
        }
      }

      return result1.getResult();

    }
  }

  public TrigSimplifyFu() {}


  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IAST tempAST = StructureFunctions.threadListLogicEquationOperators(arg1, ast, 1);
    if (tempAST.isPresent()) {
      return tempAST;
    }

    IExpr assumptionExpr = F.NIL;
    IExpr complexityFunctionHead = F.NIL;
    if (ast.size() > 2) {
      OptionArgs options = null;
      if (ast.size() > 2) {
        options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
        complexityFunctionHead = options.getOptionAutomatic(S.ComplexityFunction);
      }
      assumptionExpr = OptionArgs.determineAssumptions(ast, 2, options);
    }
    if (assumptionExpr.isPresent()) {
      if (assumptionExpr.isAST()) {
        IAssumptions oldAssumptions = engine.getAssumptions();
        IAssumptions assumptions;
        if (oldAssumptions == null) {
          assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
        } else {
          assumptions = oldAssumptions.copy();
          assumptions = assumptions.addAssumption(assumptionExpr);
        }
        if (assumptions != null) {
          try {
            engine.setAssumptions(assumptions);
            return simplifyFu(arg1, complexityFunctionHead, engine);
          } finally {
            engine.setAssumptions(oldAssumptions);
          }
        }
      }
    }

    return simplifyFu(arg1, complexityFunctionHead, engine);
  }

  final static Function<IExpr, IExpr> TR0 = TrigSimplifyFu::tr0;
  final static Function<IExpr, IExpr> TR5 = TrigSimplifyFu::tr5;
  final static Function<IExpr, IExpr> TR6 = TrigSimplifyFu::tr6;
  final static Function<IExpr, IExpr> TR10 = TrigSimplifyFu::tr10;
  final static Function<IExpr, IExpr> TR11 = TrigSimplifyFu::tr11;

  /**
   * See: <a href=
   * "https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/simplify/fu.py#L1569">sympy/simplify/fu.py#L1569</a>
   * 
   * @param expr
   * @param complexityFunctionHead
   * @param engine
   * @return
   */
  private IExpr simplifyFu(IExpr expr, IExpr complexityFunctionHead, EvalEngine engine) {
    Function<IExpr, Long> measure =
        SimplifyFunctions.createComplexityFunction(complexityFunctionHead, engine);

    // CTR1 = [(TR5, TR0), (TR6, TR0), identity]
    Function<IExpr, IExpr>[] CTR1 = new Function[2];
    CTR1[0] = TR5.andThen(TR0);
    CTR1[1] = TR6.andThen(TR0);

    // CTR2 = (TR11, [(TR5, TR0), (TR6, TR0), TR0])
    Function<IExpr, IExpr>[] CTR2 = new Function[1];
    CTR2[0] = new Chain(TR11, CTR1, measure, engine);

    Function<IExpr, IExpr> RL1 = new Chain(CTR1, CTR2, measure, engine);
    Function<IExpr, IExpr> RL2 = new Chain(CTR1, CTR2, measure, engine);


    IExpr rv = tr1(expr).orElse(expr);
    if (rv.has(x -> x.isTan() || x.isAST(S.Cot, 2), true)) {
      IExpr rv1 = RL1.apply(rv);
      if (measure.apply(rv1) < measure.apply(rv)) {
        rv = rv1;
      }
      if (rv.has(x -> x.isTan() || x.isAST(S.Cot, 2), true)) {
        rv = tr2(rv).orElse(rv);
      }
    }

    if (rv.has(x -> x.isSin() || x.isCos(), true)) {
      IExpr rv1 = RL2.apply(rv);
      IExpr rv2 = tr8(trMorrie(rv1));

      if (measure.apply(rv1) < measure.apply(rv)) {
        rv = rv1;
      }
      if (measure.apply(rv2) < measure.apply(rv)) {
        rv = rv2;
      }
    }

    if (!rv.isPresent()) {
      rv = expr;
    }
    IExpr rv3 = tr2i(rv).orElse(expr);
    if (measure.apply(rv3) < measure.apply(rv)) {
      rv = rv3;
    }
    return rv.orElse(expr);

  }

  private static IExpr tr0(IExpr expr) {
    if (expr.isAST()) {
      return EvalEngine.get().evaluate(F.Expand(F.Factor(expr)));
    }
    return F.NIL;
  }

  private static IExpr tr1(IExpr expr) {
    if (expr.isAST(S.Sec, 2)) {
      IExpr arg1 = expr.first();
      return F.Divide(1, F.Cos(arg1));
    }
    if (expr.isAST(S.Csc, 2)) {
      IExpr arg1 = expr.first();
      return F.Divide(1, F.Sin(arg1));
    }
    return F.NIL;
  }

  private static IExpr tr2(IExpr expr) {

    if (expr.isTan()) {
      IExpr arg1 = expr.first();
      return F.Divide(F.Sin(arg1), F.Cos(arg1));
    }
    if (expr.isAST(S.Cot, 2)) {
      IExpr arg1 = expr.first();
      return F.Divide(F.Cos(arg1), F.Sin(arg1));
    }
    return F.NIL;
  }

  /**
   * TODO implement method
   * 
   * @param expr
   * @return
   * @deprecated
   */
  @Deprecated
  private static IExpr tr2i(IExpr expr) {
    return F.NIL;
  }

  /**
   * <p>
   * Replacement of sin^2 with 1 - cos(x)^2.
   * 
   * Examples:
   * 
   * <pre>
   * >> TR5(sin(x)^2)
   * 1 - cos(x)^2
   * >> TR5(sin(x)^-2)  # unchanged
   * sin(x)^(-2)
   * >> TR5(sin(x)^4)
   * (1 - cos(x)^2)^^ 2
   * </pre>
   * 
   * 
   * @param expr
   * @return
   */
  private static IExpr tr5(IExpr expr) {
    if (expr.isPresent()) {
      if (expr.isPower() && expr.first().isSin() && expr.second().isInteger()) {
        IAST sinExpr = (IAST) expr.base();
        IInteger exponent = (IInteger) expr.exponent();
        if (exponent.isPositive() && exponent.isEven()) {
          IInteger div2 = exponent.div(F.C2);
          if (div2.isOne()) {
            return EvalEngine.get().evaluate(F.Subtract(F.C1, F.Sqr(F.Cos(sinExpr.arg1()))));
          }
          return EvalEngine.get()
              .evaluate(F.Power(F.Subtract(F.C1, F.Sqr(F.Cos(sinExpr.arg1()))), div2));
        }
      }
    }
    return F.NIL;
  }

  /**
   * <p>
   * Replacement of cos^2 with 1 - sin(x)^2.
   * 
   * Examples:
   * 
   * <pre>
   * >> TR6(cos(x)^2)
   * 1 - sin(x)^2
   * >> TR&(cos(x)^-2)  # unchanged
   * cos(x)^(-2)
   * >> TR6(cos(x)^4)
   * (1 - sin(x)^2)^^ 2
   * </pre>
   * 
   * 
   * @param expr
   * @return
   */
  private static IExpr tr6(IExpr expr) {
    if (expr.isPresent()) {
      if (expr.isPower() && expr.first().isCos() && expr.second().isInteger()) {
        IAST cosExpr = (IAST) expr.base();
        IInteger exponent = (IInteger) expr.exponent();
        if (exponent.isPositive() && exponent.isEven()) {
          IInteger div2 = exponent.div(F.C2);
          if (div2.isOne()) {
            return EvalEngine.get().evaluate(F.Subtract(F.C1, F.Sqr(F.Sin(cosExpr.arg1()))));
          }
          return EvalEngine.get()
              .evaluate(F.Power(F.Subtract(F.C1, F.Sqr(F.Sin(cosExpr.arg1()))), div2));
        }
      }
    }
    return F.NIL;
  }

  /**
   * TODO implement method
   * 
   * @param expr
   * @return
   * @deprecated
   */
  @Deprecated
  private static IExpr tr8(IExpr expr) {
    return F.NIL;
  }

  private static IExpr tr10(IExpr expr) {
    if ((expr.isSin() || expr.isCos())) {
      return EvalEngine.get().evaluate(F.TrigExpand(expr));
    }
    return F.NIL;
  }

  private static IExpr tr11(IExpr expr) {
    if ((expr.isSin() || expr.isCos()) && expr.first().isTimes()
        && expr.first().first().isInteger()) {
      IInteger times1 = (IInteger) expr.first().first();
      if (times1.isEven()) {
        if (expr.isSin()) {
          IExpr times1Half = times1.div(2);
          IExpr rest = expr.first().rest();
          return EvalEngine.get().evaluate(
              F.Times(F.C2, Sin(F.Times(times1Half, rest)), Cos(F.Times(times1Half, rest))));
        }
        if (expr.isCos()) {
          IExpr times1Half = times1.div(2);
          IExpr rest = expr.first().rest();
          return EvalEngine.get().evaluate(
              F.Subtract(F.C1, F.Times(F.C2, Power(Sin(F.Times(times1Half, rest)), F.C2))));
        }
      }
    }
    return F.NIL;
  }

  /**
   * TODO implement method
   * 
   * @param expr
   * @return
   * @deprecated
   */
  @Deprecated
  private static IExpr trMorrie(IExpr expr) {
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, //
        F.list(F.Rule(S.Assumptions, S.$Assumptions), //
            F.Rule(S.ComplexityFunction, S.Automatic)));
  }
}
