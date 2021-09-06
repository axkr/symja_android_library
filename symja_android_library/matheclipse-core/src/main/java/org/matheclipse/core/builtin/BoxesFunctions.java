package org.matheclipse.core.builtin;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.Precedence;

public class BoxesFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.MakeBoxes.setEvaluator(new MakeBoxes());
      S.ToBoxes.setEvaluator(new ToBoxes());
    }
  }

  private static class MakeBoxes extends AbstractEvaluator {
    private static StandardFormOperator RULE =
        new StandardFormOperator(Precedence.RULE, "\\[Rule]");
    private static StandardFormOperator RULE_DELAYED =
        new StandardFormOperator(Precedence.RULEDELAYED, "\\[RuleDelayed]");

    private static Map<ISymbol, StandardFormOperator> OPERATOR_MAP =
        new HashMap<ISymbol, StandardFormOperator>();

    private static class StandardFormOperator {
      protected int fPrecedence;
      protected String fOperator;

      public StandardFormOperator(final int precedence, final String oper) {
        fPrecedence = precedence;
        fOperator = oper;
      }

      /**
       * Converts a given function into the corresponding MathML output
       *
       * @param buf StringBuilder for MathML output
       * @param f The math function which should be converted to MathML
       */
      public boolean convert(final IASTAppendable list, final IAST f, final int precedence) {
        precedenceOpen(list, precedence);
        for (int i = 1; i < f.size(); i++) {
          list.append(standardFormRecursive(f.get(i), fPrecedence));
          if (i < f.argSize()) {
            if (fOperator.compareTo("") != 0) {
              list.append(fOperator);
            }
          }
        }
        precedenceClose(list, precedence);
        return true;
      }

      public void precedenceClose(final IASTAppendable list, final int precedence) {
        if (precedence > fPrecedence) {
          list.append(")");
        }
      }

      public void precedenceOpen(final IASTAppendable list, final int precedence) {
        if (precedence > fPrecedence) {
          list.append("(");
        }
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr form = S.StandardForm;
      if (ast.isAST2()) {
        form = ast.arg2();
      }
      if (form == S.StandardForm) {
        return standardFormRecursive(ast.arg1(), 0);
      }
      return standardFormRecursive(ast.arg1(), 0);
    }

    private static IExpr standardFormRecursive(final IExpr expr, final int precedence) {
      if (expr.isAST()) {
        IAST function = (IAST) expr;
        if (function.size() > 0) {
          if (function.isList()) {
            IASTAppendable list = F.ListAlloc(3);
            list.append("{");
            IASTAppendable argsList = F.ListAlloc(function.size());
            for (int i = 1; i < function.size(); i++) {
              argsList.append(standardFormRecursive(function.get(i), precedence));
              if (i < function.size() - 1) {
                argsList.append(",");
              }
            }
            list.append(F.RowBox(argsList));
            list.append("}");
            return F.RowBox(list);
          }
          if (function.head().isSymbol()) {
            StandardFormOperator operator = OPERATOR_MAP.get(function.head());
            if (operator != null) {
              IASTAppendable argsList = F.ListAlloc(function.size());
              if (operator.convert(argsList, function, precedence)) {
                return F.RowBox(argsList);
              }
            }
          }
          IASTAppendable list = F.ListAlloc(4);
          list.append(standardFormRecursive(function.head(), precedence));
          list.append("[");
          IASTAppendable argsList = F.ListAlloc(function.size());
          for (int i = 1; i < function.size(); i++) {
            argsList.append(standardFormRecursive(function.get(i), precedence));
            if (i < function.size() - 1) {
              argsList.append(",");
            }
          }
          list.append(F.RowBox(argsList));
          list.append("]");
          return F.RowBox(list);
        }
      } else if (expr.isSymbol()) {
        return F.$str(expr.toString());
      } else if (expr.isNumber()) {
        if (expr.isInteger()) {
          return F.$str(expr.toString());
        } else if (expr.isRational()) {
          IRational rational = (IRational) expr;
          IInteger num = rational.numerator();
          IInteger den = rational.denominator();
          return F.FractionBox(F.$str(num.toString()), F.$str(den.toString()));
        } else if (expr.isComplex()) {
          IComplex complex = (IComplex) expr;
          IRational re = complex.re();
          IRational im = complex.im();
          if (re.isZero()) {
            return F.RowBox(
                F.List(
                    standardFormRecursive(im, precedence), F.$str(" "), F.$str("\\[ImaginaryI]")));
          }
          return F.RowBox( //
              F.List(
                  standardFormRecursive(re, precedence),
                  F.$str("+"),
                  F.RowBox(
                      F.List(
                          standardFormRecursive(im, precedence),
                          F.$str(" "),
                          F.$str("\\[ImaginaryI]")))));
        } else if (expr.isReal()) {
          return F.$str(expr.toString());
        } else if (expr.isComplexNumeric()) {
          IComplexNum complex = (IComplexNum) expr;
          ISignedNumber re = complex.re();
          ISignedNumber im = complex.im();
          if (re.isZero()) {
            return F.RowBox(F.List(F.$str(im.toString()), F.$str(" "), F.$str("\\[ImaginaryI]")));
          }
          return F.RowBox( //
              F.List(
                  F.$str(re.toString()),
                  F.$str("+"),
                  F.RowBox(F.List(F.$str(im.toString()), F.$str(" "), F.$str("\\[ImaginaryI]")))));
        }
      }
      return F.$str(expr.toString());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALLCOMPLETE);
      OPERATOR_MAP.put(S.Rule, RULE);
      OPERATOR_MAP.put(S.RuleDelayed, RULE_DELAYED);
      super.setUp(newSymbol);
    }
  }

  private static class ToBoxes extends MakeBoxes {

    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // the arguments are first evaluated in ToBoxes
      return super.evaluate(ast, engine);
    }

    public void setUp(final ISymbol newSymbol) {
      // don't call super.setUp() here!
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private BoxesFunctions() {}
}
