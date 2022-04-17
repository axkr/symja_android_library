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

  private static Supplier<Matcher> TR01_MATCHER;
  private static Supplier<Matcher> TR05_MATCHER;
  private static Supplier<Matcher> TR07_MATCHER;

  private static class Initializer {

    private static Matcher initTR01() {
      Matcher tr01 = new Matcher();

      tr01.caseOf(Sec(z_), //
          // [$ 1/Cos(z)
          // $]
          F.Power(F.Cos(z), F.CN1)); // $$);
      tr01.caseOf(Csc(z_), //
          // [$ 1/Sin(z)
          // $]
          F.Power(F.Sin(z), F.CN1)); // $$);
      tr01.caseOf(Cot(z_), //
          // [$ Cos(z)/Sin(z)
          // $]
          F.Times(F.Cos(z), F.Power(F.Sin(z), F.CN1))); // $$);
      tr01.caseOf(Tan(z_), //
          // [$ Sin(z)/Cos(z)
          // $]
          F.Times(F.Power(F.Cos(z), F.CN1), F.Sin(z))); // $$);
      return tr01;
    }

    private static Matcher initTR05() {
      // Replacement of sin(x)^2 with 1 - cos(x)^2.
      Matcher tr05 = new Matcher();

      tr05.caseOf(Power(Sin(z_), F.n_Integer), //
          // [$ (1-Cos(z)^2) ^ (n/2) /; EvenQ(n) && n>0
          // $]
          F.Condition(F.Power(F.Subtract(F.C1, F.Sqr(F.Cos(z))), F.Times(F.C1D2, n)),
              F.And(F.EvenQ(n), F.Greater(n, F.C0)))); // $$);
      return tr05;
    }

    private static Matcher initTR07() {
      // Lowering the degree of cos(x)^2.
      Matcher tr07 = new Matcher();

      tr07.caseOf(Power(Cos(z_), F.C2), //
          // [$ (1+Cos(2*z))/2
          // $]
          F.Times(F.C1D2, F.Plus(F.C1, F.Cos(F.Times(F.C2, z))))); // $$);
      return tr07;
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
  }

  public TrigSimplifyFu() {}

  private static Matcher tr01_matcher() {
    return TR01_MATCHER.get();
  }

  private static Matcher tr05_matcher() {
    return TR05_MATCHER.get();
  }

  private static Matcher tr07_matcher() {
    return TR07_MATCHER.get();
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
    boolean oldDisabledHashRules = engine.isDisabledTrigRules();
    try {
      engine.setDisabledTrigRules(true);

      IExpr temp = tr01_matcher().replaceAll(arg1, null).orElse(arg1);
      temp = engine.evaluate(temp);
      System.out.println(temp.toString());
      temp = tr05_matcher().replaceAll(arg1, null).orElse(temp);
      temp = engine.evaluate(temp);
      System.out.println(temp.toString());
      temp = tr07_matcher().replaceAll(arg1, null).orElse(temp);
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
    TR01_MATCHER = Suppliers.memoize(Initializer::initTR01);
    TR05_MATCHER = Suppliers.memoize(Initializer::initTR05);
    TR07_MATCHER = Suppliers.memoize(Initializer::initTR07);
  }
}
