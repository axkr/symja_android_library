package org.matheclipse.core.builtin;

import java.util.IdentityHashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
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
        new IdentityHashMap<ISymbol, StandardFormOperator>();

    private static class StandardFormOperator {
      protected int fPrecedence;
      protected String fOperator;

      public StandardFormOperator(final int precedence, final String oper) {
        fPrecedence = precedence;
        fOperator = oper;
      }

      /**
       * Converts a given function
       *
       * @param list
       * @param f The math function which should be converted
       * @param precedence the precedence of the currently used operator
       */
      public boolean convert(final IASTAppendable list, final IAST f, final int precedence,
          final IExpr form, final EvalEngine engine) {
        precedenceOpen(list, precedence);
        for (int i = 1; i < f.size(); i++) {
          list.append(standardFormRecursive(f.get(i), fPrecedence, form, engine));
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
      return standardFormRecursive(ast.arg1(), 0, form, engine);
    }

    /**
     * The boxes a definition of the shape <code>head /: MakeBoxes[expr_head, form] := …</code>
     * gives for this expression, or {@link F#NIL} if there is none.
     *
     * <p>
     * Writing such an up-value is how a package says how its own objects are shown -
     * <code>Graphics /: MakeBoxes[g_Graphics, StandardForm] := ViewBox[…]</code> is how the WLJS
     * notebook hands a picture to the browser instead of printing its primitives. The rule has to
     * be looked for here rather than by the evaluator's own up-value probe, because
     * <code>MakeBoxes</code> is HoldAllComplete and that probe is skipped for such a head; the
     * Wolfram Language answers the same way, which is why the up-value is the documented way to do
     * this at all.
     */
    private static IExpr upValueBoxes(IExpr expr, IExpr form, EvalEngine engine) {
      // an object held as an atom (a Graph, a ByteArray, ...) carries its head like an expression
      if (engine == null || !(expr.isASTOrAssociation() || expr instanceof IDataExpr)
          || !RulesData.isUpRulesDefined()) {
        return F.NIL;
      }
      ISymbol head = expr.topHead();
      IExpr boxes = head.evalUpRules(F.binaryAST2(S.MakeBoxes, expr, form), engine);
      if (boxes.isNIL() && expr instanceof IDataExpr) {
        // a rule written for the Wolfram Language form of the object - Image[data, type, ...] -
        // is tried on that form: an image object has no parts for Image[_, type_, ___] to match
        IAST normal = ((IDataExpr<?>) expr).normal(true);
        if (normal.isPresent() && normal.head() == head) {
          boxes = head.evalUpRules(F.binaryAST2(S.MakeBoxes, normal, form), engine);
        }
      }
      return boxes;
    }

    private static IExpr standardFormRecursive(final IExpr expr, final int precedence,
        final IExpr form, final EvalEngine engine) {
      IExpr boxes = upValueBoxes(expr, form, engine);
      if (boxes.isPresent()) {
        return boxes;
      }
      if (expr.isAST(S.Texture, 2)) {
        // a texture is shown as its image, as the Wolfram Language shows it
        return standardFormRecursive(expr.first(), precedence, form, engine);
      }
      if (expr.isAST(S.Short, 2) || expr.isAST(S.Short, 3)) {
        // a notebook shows Short the way a front end does: what fits, and <<k>> for the rest
        IExpr shown = org.matheclipse.core.form.output.OutputFormFactory.shortForm((IAST) expr,
            engine != null && engine.isRelaxedSyntax());
        if (shown.isPresent()) {
          return standardFormRecursive(shown, precedence, form, engine);
        }
      }
      if (expr.isAST(S.Skeleton, 2)) {
        return F.$str("<<" + expr.first().toString() + ">>");
      }
      if (expr.isASTOrAssociation()) {
        IAST function = (IAST) expr;
        if (function.size() > 0) {
          if (function.isList()) {
            IASTAppendable list = F.ListAlloc(3);
            list.append("{");
            IASTAppendable argsList = F.ListAlloc(function.size());
            for (int i = 1; i < function.size(); i++) {
              argsList.append(standardFormRecursive(function.getRule(i), precedence, form, engine));
              if (i < function.argSize()) {
                argsList.append(",");
              }
            }
            list.append(F.RowBox(argsList));
            list.append("}");
            return F.RowBox(list);
          }
          if (function.isPower()) {
            return powerBox(function, precedence, form, engine);
          }
          if (function.isTimes()) {
            return timesBox(function, precedence, form, engine);
          }
          if (function.isPlus()) {
            return plusBox(function, precedence, form, engine);
          }
          if (function.head().isSymbol()) {
            StandardFormOperator operator = operatorFor((ISymbol) function.head());
            if (operator != null) {
              IASTAppendable argsList = F.ListAlloc(function.size());
              if (operator.convert(argsList, function, precedence, form, engine)) {
                return F.RowBox(argsList);
              }
            }
          }
          IASTAppendable list = F.ListAlloc(4);
          list.append(standardFormRecursive(function.head(), precedence, form, engine));
          list.append("[");
          IASTAppendable argsList = F.ListAlloc(function.size());
          for (int i = 1; i < function.size(); i++) {
            argsList.append(standardFormRecursive(function.getRule(i), precedence, form, engine));
            if (i < function.argSize()) {
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
            return F.RowBox(F.list(standardFormRecursive(im, precedence, form, engine), F.$str(" "),
                F.$str("\\[ImaginaryI]")));
          }
          return F.RowBox( //
              F.list(standardFormRecursive(re, precedence, form, engine), F.$str("+"),
                  F.RowBox(F.list(standardFormRecursive(im, precedence, form, engine), F.$str(" "),
                      F.$str("\\[ImaginaryI]")))));
        } else if (expr.isReal()) {
          return F.$str(expr.toString());
        } else if (expr.isComplexNumeric()) {
          IComplexNum complex = (IComplexNum) expr;
          IReal re = complex.re();
          IReal im = complex.im();
          if (re.isZero()) {
            return F.RowBox(F.list(F.$str(im.toString()), F.$str(" "), F.$str("\\[ImaginaryI]")));
          }
          return F.RowBox( //
              F.list(F.$str(re.toString()), F.$str("+"),
                  F.RowBox(F.list(F.$str(im.toString()), F.$str(" "), F.$str("\\[ImaginaryI]")))));
        }
      }
      return F.$str(expr.toString());
    }

    /** Whether {@link #OPERATOR_MAP} has been filled from the parser's operator table yet. */
    private static volatile boolean operatorsImported = false;

    /**
     * The infix operator a head is written with, or <code>null</code> if it is written
     * <code>Head[…]</code>.
     *
     * <p>
     * The precedences and spellings are the parser's own, read out of its operator table rather
     * than repeated here, so that every operator the language can read - the comparisons, the
     * logic, the assignments, <code>Alternatives</code>, <code>StringJoin</code> and the rest - is
     * also one it writes. Mathics3 builds its <code>MakeBoxes</code> the same way, from the table
     * that gives each operator its precedence and grouping.
     *
     * <p>
     * Only the few whose printed form is not their input form are named here: a product is written
     * with a space rather than a star, and a rule with an arrow rather than with a minus and a
     * greater-than.
     */
    private static StandardFormOperator operatorFor(ISymbol head) {
      if (!operatorsImported) {
        importOperators();
      }
      return OPERATOR_MAP.get(head);
    }

    private static synchronized void importOperators() {
      if (operatorsImported) {
        return;
      }
      for (Map.Entry<String, Operator> entry : ASTNodeFactory.MMA_STYLE_FACTORY
          .getIdentifier2OperatorMap().entrySet()) {
        Operator operator = entry.getValue();
        if (!(operator instanceof InfixOperator)) {
          continue;
        }
        // the System` context is keyed by the name as the parser spells it, which is lower case in
        // relaxed mode; the symbol the two names find is one and the same object
        String name = operator.getFunctionName();
        ISymbol head = Context.SYSTEM.get(name);
        if (head == null) {
          head = Context.SYSTEM.get(name.toLowerCase(java.util.Locale.ENGLISH));
        }
        // Plus, Times and Power are written as sums, fractions and superscripts rather than as
        // plain infix, and are answered before this map is asked
        if (head == null || head == S.Plus || head == S.Times || head == S.Power
            || OPERATOR_MAP.containsKey(head)) {
          continue;
        }
        OPERATOR_MAP.put(head,
            new StandardFormOperator(operator.getPrecedence(), operator.getOperatorString()));
      }
      operatorsImported = true;
    }

    /**
     * Arithmetic as the Wolfram Language shows it rather than as it is stored.
     *
     * <p>
     * <code>Plus</code>, <code>Times</code> and <code>Power</code> are the three heads a printed
     * expression is mostly made of, and writing them the way every other head is written -
     * <code>Plus[Times[…], Power[…]]</code> - is what a notebook cell showed instead of
     * <code>x&#178; + &#8730;2/3</code>. A power becomes a superscript, a square root a radical, and
     * a factor of negative power moves under the line into a fraction.
     */
    private static IExpr powerBox(IAST power, int precedence, IExpr form, EvalEngine engine) {
      IExpr base = power.base();
      IExpr exponent = power.exponent();
      if (exponent.equals(F.C1D2)) {
        return F.unaryAST1(S.SqrtBox, standardFormRecursive(base, 0, form, engine));
      }
      if (exponent.isNumber() && exponent.isNegative()) {
        return F.FractionBox(F.$str("1"), reciprocalBox(power, form, engine));
      }
      return F.binaryAST2(S.SuperscriptBox, //
          standardFormRecursive(base, Precedence.POWER, form, engine),
          standardFormRecursive(exponent, 0, form, engine));
    }

    /** What <code>a^-n</code> reads as once it stands under the line of a fraction. */
    private static IExpr reciprocalBox(IAST power, IExpr form, EvalEngine engine) {
      IExpr base = power.base();
      IExpr positive = ((INumber) power.exponent()).negate();
      if (positive.isOne()) {
        return standardFormRecursive(base, Precedence.POWER, form, engine);
      }
      if (positive.equals(F.C1D2)) {
        return F.unaryAST1(S.SqrtBox, standardFormRecursive(base, 0, form, engine));
      }
      return F.binaryAST2(S.SuperscriptBox, //
          standardFormRecursive(base, Precedence.POWER, form, engine),
          standardFormRecursive(positive, 0, form, engine));
    }

    private static IExpr timesBox(IAST times, int precedence, IExpr form, EvalEngine engine) {
      IASTAppendable numerator = F.ListAlloc(times.size());
      IASTAppendable denominator = F.ListAlloc(times.size());
      for (int i = 1; i < times.size(); i++) {
        IExpr factor = times.get(i);
        if (factor.isPower() && factor.exponent().isNumber() && factor.exponent().isNegative()) {
          denominator.append(factor);
        } else {
          numerator.append(factor);
        }
      }
      if (denominator.isEmpty()) {
        return parenthesize(riffle(boxes(numerator, Precedence.TIMES, form, engine), " "),
            Precedence.TIMES, precedence);
      }
      // a lone factor above or below the line needs no brackets of its own: the bar groups it
      IExpr above = numerator.isEmpty() //
          ? F.$str("1")
          : riffle(boxes(numerator, numerator.argSize() == 1 ? 0 : Precedence.TIMES, form, engine),
              " ");
      IASTAppendable belowBoxes = F.ListAlloc(denominator.size());
      for (int i = 1; i < denominator.size(); i++) {
        belowBoxes.append(reciprocalBox((IAST) denominator.get(i), form, engine));
      }
      return F.FractionBox(above, riffle(belowBoxes, " "));
    }

    /** Each of the expressions as boxes, read at the given precedence. */
    private static IAST boxes(IAST expressions, int precedence, IExpr form, EvalEngine engine) {
      IASTAppendable result = F.ListAlloc(expressions.size());
      for (int i = 1; i < expressions.size(); i++) {
        result.append(standardFormRecursive(expressions.get(i), precedence, form, engine));
      }
      return result;
    }

    private static IExpr plusBox(IAST plus, int precedence, IExpr form, EvalEngine engine) {
      IASTAppendable row = F.ListAlloc(plus.size() * 2);
      for (int i = 1; i < plus.size(); i++) {
        IExpr term = plus.get(i);
        IExpr positive = withoutMinus(term);
        boolean negative = positive.isPresent();
        if (negative) {
          row.append(F.$str(i > 1 ? "-" : "-"));
        } else if (i > 1) {
          row.append(F.$str("+"));
        }
        row.append(standardFormRecursive(negative ? positive : term, Precedence.PLUS, form, engine));
      }
      return parenthesize(F.RowBox(row), Precedence.PLUS, precedence);
    }

    /**
     * The term without the minus sign a sum should write in front of it, or {@link F#NIL} when it
     * carries none. Structural on purpose: MakeBoxes is HoldAllComplete, so what it is shown is
     * what it must show.
     */
    private static IExpr withoutMinus(IExpr term) {
      if (term.isNumber() && term.isNegative()) {
        return ((INumber) term).negate();
      }
      if (term.isTimes()) {
        IAST times = (IAST) term;
        IExpr first = times.arg1();
        if (first.isNumber() && first.isNegative()) {
          IExpr positive = ((INumber) first).negate();
          return positive.isOne() //
              ? (times.size() == 3 ? times.arg2() : times.rest())
              : times.setAtCopy(1, positive);
        }
      }
      return F.NIL;
    }

    /** The boxes with a separator between them, as one RowBox, or the single box itself. */
    private static IExpr riffle(IAST boxes, String separator) {
      if (boxes.argSize() == 1) {
        return boxes.arg1();
      }
      IASTAppendable row = F.ListAlloc(boxes.size() * 2);
      for (int i = 1; i < boxes.size(); i++) {
        if (i > 1) {
          row.append(F.$str(separator));
        }
        row.append(boxes.get(i));
      }
      return F.RowBox(row);
    }

    /** The boxes in brackets when what stands around them binds more tightly. */
    private static IExpr parenthesize(IExpr boxes, int own, int outer) {
      if (outer > own) {
        return F.RowBox(F.list(F.$str("("), boxes, F.$str(")")));
      }
      return boxes;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALLCOMPLETE);
      OPERATOR_MAP.put(S.Rule, RULE);
      OPERATOR_MAP.put(S.RuleDelayed, RULE_DELAYED);
      super.setUp(newSymbol);
    }
  }

  private static class ToBoxes extends MakeBoxes {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // the arguments are first evaluated in ToBoxes
      return super.evaluate(ast, engine);
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't call super.setUp() here!
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private BoxesFunctions() {}
}
