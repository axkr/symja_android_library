package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.z;
import java.util.function.Supplier;
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
import org.matheclipse.core.patternmatching.Matcher;
import com.google.common.base.Suppliers;

public class TrigSimplifyFu extends AbstractFunctionEvaluator {
  private static Supplier<Matcher> TR0_MATCHER;
  private static Supplier<Matcher> TR1_MATCHER;
  private static Supplier<Matcher> TR2_MATCHER;
  private static Supplier<Matcher> TR2I_MATCHER;
  private static Supplier<Matcher> TR3_MATCHER;
  private static Supplier<Matcher> TR4_MATCHER;
  private static Supplier<Matcher> TR5_MATCHER;
  private static Supplier<Matcher> TR7_MATCHER;
  private static Supplier<Matcher> TR8_MATCHER;
  private static Supplier<Matcher> TR12_MATCHER;
  private static Supplier<Matcher> TR13_MATCHER;
  private static Supplier<Matcher> TR14_MATCHER;

  private static class Initializer {
    private static Matcher initTR0() {
      Matcher tr3 = new Matcher();


      return tr3;
    }

    private static Matcher initTR1() {
      Matcher tr1 = new Matcher();

      tr1.caseOf(Sec(z_), //
          // [$ 1/Cos(z)
          // $]
          F.Power(F.Cos(z), F.CN1)); // $$);
      tr1.caseOf(Csc(z_), //
          // [$ 1/Sin(z)
          // $]
          F.Power(F.Sin(z), F.CN1)); // $$);

      return tr1;
    }

    private static Matcher initTR2() {
      Matcher tr2 = new Matcher();

      tr2.caseOf(Cot(z_), //
          // [$ Cos(z)/Sin(z)
          // $]
          F.Times(F.Cos(z), F.Power(F.Sin(z), F.CN1))); // $$);
      tr2.caseOf(Tan(z_), //
          // [$ Sin(z)/Cos(z)
          // $]
          F.Times(F.Power(F.Cos(z), F.CN1), F.Sin(z))); // $$);
      return tr2;
    }

    private static Matcher initTR2i() {
      Matcher tr2i = new Matcher();

      return tr2i;
    }

    private static Matcher initTR3() {
      Matcher tr3 = new Matcher();


      return tr3;
    }

    private static Matcher initTR4() {
      Matcher tr4 = new Matcher();


      return tr4;
    }

    private static Matcher initTR5() {
      // Replacement of sin(x)^2 with 1 - cos(x)^2.
      Matcher tr05 = new Matcher();

      tr05.caseOf(Power(Sin(z_), F.n_Integer), //
          // [$ (1-Cos(z)^2) ^ (n/2) /; EvenQ(n) && n>0
          // $]
          F.Condition(F.Power(F.Subtract(F.C1, F.Sqr(F.Cos(z))), F.Times(F.C1D2, n)),
              F.And(F.EvenQ(n), F.Greater(n, F.C0)))); // $$);
      return tr05;
    }

    private static Matcher initTR7() {
      // Lowering the degree of cos(x)^2.
      Matcher tr07 = new Matcher();

      tr07.caseOf(Power(Cos(z_), F.C2), //
          // [$ (1+Cos(2*z))/2
          // $]
          F.Times(F.C1D2, F.Plus(F.C1, F.Cos(F.Times(F.C2, z))))); // $$);
      return tr07;
    }

    private static Matcher initTR8() {
      Matcher tr8 = new Matcher();

      return tr8;
    }

    private static Matcher initTR11() {
      // Lowering the degree of cos(x)^2.
      Matcher tr07 = new Matcher();

      tr07.caseOf(Power(Cos(z_), F.C2), //
          // [$ (1+Cos(2*z))/2
          // $]
          F.Times(F.C1D2, F.Plus(F.C1, F.Cos(F.Times(F.C2, z))))); // $$);
      return tr07;
    }

    private static Matcher initTR12() {
      Matcher tr12 = new Matcher();


      return tr12;
    }

    private static Matcher initTR13() {
      Matcher tr13 = new Matcher();


      return tr13;
    }

    private static Matcher initTR14() {
      Matcher tr14 = new Matcher();


      return tr14;
    }
  }

  public TrigSimplifyFu() {}

  private static Matcher tr1_matcher() {
    return TR1_MATCHER.get();
  }

  private static Matcher tr2_matcher() {
    return TR2_MATCHER.get();
  }

  private static Matcher tr2i_matcher() {
    return TR2I_MATCHER.get();
  }

  private static Matcher tr5_matcher() {
    return TR5_MATCHER.get();
  }

  private static Matcher tr7_matcher() {
    return TR7_MATCHER.get();
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IAST tempAST = StructureFunctions.threadListLogicEquationOperators(arg1, ast, 1);
    if (tempAST.isPresent()) {
      return tempAST;
    }

    IExpr assumptionExpr = F.NIL;
    if (ast.size() > 2) {
      IExpr arg2 = ast.arg2();
      if (!arg2.isRule()) {
        assumptionExpr = arg2;
      }
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
      assumptionExpr = options.getOption(S.Assumptions).orElse(assumptionExpr);
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
            return callMatcher(ast, arg1, engine);
          } finally {
            engine.setAssumptions(oldAssumptions);
          }
        }
      }
    }
    return callMatcher(ast, arg1, engine);
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
          IExpr times1Half = times1.divide(2);
          IExpr rest = expr.first().rest();
          return F.Times(F.C2, Sin(F.Times(times1Half, rest)), Cos(F.Times(times1Half, rest)));
        }
        if (expr.isCos()) {
          IExpr times1Half = times1.divide(2);
          IExpr rest = expr.first().rest();
          return F.Subtract(F.C1, F.Times(F.C2, Power(Sin(F.Times(times1Half, rest)), F.C2)));
        }
      }
    }
    return F.NIL;
  }

  /**
   * @param ast
   * @param arg1
   * @return {@link F#NIL} if no match was found
   */
  public static IExpr callMatcher(final IAST ast, IExpr arg1, EvalEngine engine) {
    // https://github.com/sympy/sympy/blob/dfef951e777dba36ad75162c8dc9402b228d11ed/sympy/simplify/fu.py#L1639
    boolean oldDisabledHashRules = engine.isDisabledTrigRules();
    try {
      engine.setDisabledTrigRules(true);
      long leafCountSimplify = ast.leafCountSimplify();

      // RL1 = (TR4, TR3, TR4, TR12, TR4, TR13, TR4, TR0);
      IExpr temp = tr1_matcher().replaceAll(arg1, null).orElse(arg1);
      temp = engine.evaluate(temp);
      System.out.println(temp.toString());
      temp = tr5_matcher().replaceAll(arg1, null).orElse(temp);
      temp = engine.evaluate(temp);
      System.out.println(temp.toString());
      temp = tr7_matcher().replaceAll(arg1, null).orElse(temp);
      temp = engine.evaluate(temp);
      System.out.println(temp.toString());
      temp = temp.replaceAll(TrigSimplifyFu::tr10).orElse(temp);
      temp = engine.evaluate(temp);
      System.out.println(temp.toString());
      temp = temp.replaceAll(TrigSimplifyFu::tr11).orElse(temp);
      temp = engine.evaluate(temp);
      System.out.println(temp.toString());
      return temp;

    } finally {
      engine.setDisabledTrigRules(oldDisabledHashRules);
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // Initializer.init();
    TR0_MATCHER = Suppliers.memoize(Initializer::initTR0);
    TR1_MATCHER = Suppliers.memoize(Initializer::initTR1);
    TR2_MATCHER = Suppliers.memoize(Initializer::initTR2);
    TR2I_MATCHER = Suppliers.memoize(Initializer::initTR2i);
    TR3_MATCHER = Suppliers.memoize(Initializer::initTR3);
    TR4_MATCHER = Suppliers.memoize(Initializer::initTR4);
    TR5_MATCHER = Suppliers.memoize(Initializer::initTR5);
    TR7_MATCHER = Suppliers.memoize(Initializer::initTR7);
    TR8_MATCHER = Suppliers.memoize(Initializer::initTR8);
    TR12_MATCHER = Suppliers.memoize(Initializer::initTR12);
    TR13_MATCHER = Suppliers.memoize(Initializer::initTR13);
    TR14_MATCHER = Suppliers.memoize(Initializer::initTR14);
  }
}
