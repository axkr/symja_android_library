package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPredicate;
import org.matheclipse.core.interfaces.ISymbol;

public class ContainsFunctions {

  private static class Initializer {

    private static void init() {
      S.ContainsAny.setEvaluator(ContainsAny.CONST);
      S.ContainsAll.setEvaluator(ContainsAll.CONST);
      S.ContainsExactly.setEvaluator(ContainsExactly.CONST);
      S.ContainsNone.setEvaluator(ContainsNone.CONST);
      S.ContainsOnly.setEvaluator(ContainsOnly.CONST);

      // seemed to be the same behavior as ContainsXXX functions, if the headers of the lists are
      // identical
      S.DisjointQ.setEvaluator(new DisjointQ());
      S.IntersectingQ.setEvaluator(new IntersectingQ());
      S.SubsetQ.setEvaluator(new SubsetQ());
    }
  }

  private static class DisjointQ extends ContainsNone implements IPredicate {
    @Override
    public boolean validateArgs(IExpr arg1, IExpr arg2, EvalEngine engine) {
      return arg1.isAST() && arg2.isAST(arg1.head());
    }
  }

  private static class IntersectingQ extends ContainsAny implements IPredicate {
    @Override
    public boolean validateArgs(IExpr arg1, IExpr arg2, EvalEngine engine) {
      return arg1.isAST() && arg2.isAST(arg1.head());
    }
  }

  private static class SubsetQ extends ContainsAll implements IPredicate {
    @Override
    public boolean validateArgs(IExpr arg1, IExpr arg2, EvalEngine engine) {
      return arg1.isAST() && arg2.isAST(arg1.head());
    }
  }

  private static class ContainsAny extends AbstractFunctionOptionEvaluator {

    static final ContainsAny CONST = new ContainsAny();

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {
      if (argSize >= 2 && validateArgs(ast.arg1(), ast.arg2(), engine)) {
        IExpr sameTest = option[0].equals(S.Automatic) ? S.SameQ : option[0];
        IAST list1 = (IAST) ast.arg1();
        IAST list2 = (IAST) ast.arg2();
        return containsFunction(list1, list2, sameTest, engine);
      }
      return F.NIL;
    }

    public boolean validateArgs(IExpr arg1, IExpr arg2, EvalEngine engine) {
      return arg1.isListOrAssociation() && arg2.isListOrAssociation();
    }

    public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
      for (int i = 1; i < list1.size(); i++) {
        IExpr list1Arg = list1.get(i);

        if (list2.exists(x -> engine.evalTrue(sameTest, list1Arg, x))) {
          return S.True;
        }
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.SameTest, S.Automatic);
    }
  }

  private static final class ContainsExactly extends ContainsAny {
    static final ContainsExactly CONST = new ContainsExactly();

    @Override
    public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
      if (ContainsAll.CONST.containsFunction(list1, list2, sameTest, engine).isTrue()) {
        if (ContainsOnly.CONST.containsFunction(list1, list2, sameTest, engine).isTrue()) {
          return S.True;
        }
      }
      return S.False;
    }
  }

  private static class ContainsAll extends ContainsAny {
    static final ContainsAll CONST = new ContainsAll();

    @Override
    public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
      boolean evaledTrue;
      for (int i = 1; i < list2.size(); i++) {
        IExpr list2Arg = list2.get(i);
        evaledTrue = false;
        for (int j = 1; j < list1.size(); j++) {
          IExpr list1Arg = list1.get(j);
          if (engine.evalTrue(sameTest, list1Arg, list2Arg)) {
            evaledTrue = true;
            break;
          }
        }
        if (!evaledTrue) {
          return S.False;
        }
      }
      return S.True;
    }
  }

  private static final class ContainsOnly extends ContainsAny {
    static final ContainsOnly CONST = new ContainsOnly();

    @Override
    public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
      boolean evaledTrue;
      for (int i = 1; i < list1.size(); i++) {
        IExpr list1Arg = list1.get(i);
        evaledTrue = false;
        for (int j = 1; j < list2.size(); j++) {
          IExpr list2Arg = list2.get(j);
          if (engine.evalTrue(sameTest, list1Arg, list2Arg)) {
            evaledTrue = true;
            break;
          }
        }
        if (!evaledTrue) {
          return S.False;
        }
      }
      return S.True;
    }
  }

  private static class ContainsNone extends ContainsAny {
    static final ContainsNone CONST = new ContainsNone();

    @Override
    public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
      for (int i = 1; i < list1.size(); i++) {
        IExpr list1Arg = list1.get(i);
        if (list2.exists(x -> engine.evalTrue(sameTest, list1Arg, x))) {
          return S.False;
        }
      }
      return S.True;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ContainsFunctions() {}
}
