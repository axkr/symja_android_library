package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPredicate;

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
    public boolean validateArgs(IExpr arg1, IExpr arg2, EvalEngine engine) {
      return arg1.isAST() && arg2.isAST(arg1.head());
    }
  }

  private static class IntersectingQ extends ContainsAny implements IPredicate {
    public boolean validateArgs(IExpr arg1, IExpr arg2, EvalEngine engine) {
      return arg1.isAST() && arg2.isAST(arg1.head());
    }
  }

  private static class SubsetQ extends ContainsAll implements IPredicate {
    public boolean validateArgs(IExpr arg1, IExpr arg2, EvalEngine engine) {
      return arg1.isAST() && arg2.isAST(arg1.head());
    }
  }

  private static class ContainsAny extends AbstractEvaluator {
    static final ContainsAny CONST = new ContainsAny();

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        ast = F.operatorFormAppend(ast);
        if (!ast.isPresent()) {
          return F.NIL;
        }
      }
      IExpr sameTest = F.SameQ;
      if (validateArgs(ast.arg1(), ast.arg2(), engine)) {
        if (ast.isAST3()) {
          // determine option SameTest
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
          IExpr option = options.getOptionAutomatic(F.SameTest);
          if (option.isPresent()) {
            sameTest = option;
          }
        }
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

        if (list2.exists(x -> engine.evalTrue(F.binaryAST2(sameTest, list1Arg, x)))) {
          return F.True;
        }
      }
      return F.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IOFunctions.ARGS_1_3;
    }

    @Override
    public IAST options() {
      return F.List(F.Rule(F.SameTest, F.Automatic));
    }
  }

  private static final class ContainsExactly extends ContainsAny {
    static final ContainsExactly CONST = new ContainsExactly();

    public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
      if (ContainsAll.CONST.containsFunction(list1, list2, sameTest, engine).isTrue()) {
        if (ContainsOnly.CONST.containsFunction(list1, list2, sameTest, engine).isTrue()) {
          return F.True;
        }
      }
      return F.False;
    }
  }

  private static class ContainsAll extends ContainsAny {
    static final ContainsAll CONST = new ContainsAll();

    public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
      boolean evaledTrue;
      for (int i = 1; i < list2.size(); i++) {
        IExpr list2Arg = list2.get(i);
        evaledTrue = false;
        for (int j = 1; j < list1.size(); j++) {
          IExpr list1Arg = list1.get(j);
          if (engine.evalTrue(F.binaryAST2(sameTest, list1Arg, list2Arg))) {
            evaledTrue = true;
            break;
          }
        }
        if (!evaledTrue) {
          return F.False;
        }
      }
      return F.True;
    }
  }

  private static final class ContainsOnly extends ContainsAny {
    static final ContainsOnly CONST = new ContainsOnly();

    public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
      boolean evaledTrue;
      for (int i = 1; i < list1.size(); i++) {
        IExpr list1Arg = list1.get(i);
        evaledTrue = false;
        for (int j = 1; j < list2.size(); j++) {
          IExpr list2Arg = list2.get(j);
          if (engine.evalTrue(F.binaryAST2(sameTest, list1Arg, list2Arg))) {
            evaledTrue = true;
            break;
          }
        }
        if (!evaledTrue) {
          return F.False;
        }
      }
      return F.True;
    }
  }

  private static class ContainsNone extends ContainsAny {
    static final ContainsNone CONST = new ContainsNone();

    public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
      for (int i = 1; i < list1.size(); i++) {
        IExpr list1Arg = list1.get(i);
        if (list2.exists(x -> engine.evalTrue(F.binaryAST2(sameTest, list1Arg, x)))) {
          return F.False;
        }
      }
      return F.True;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ContainsFunctions() {}
}
