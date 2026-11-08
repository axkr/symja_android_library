package org.matheclipse.core.builtin;

import java.util.IdentityHashMap;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.NumberFormatter;
import org.matheclipse.core.form.output.NumberFormPrinter;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
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
      S.MakeExpression.setEvaluator(new MakeExpression());
    }
  }

  /**
   * <code>MakeExpression(boxes, form)</code> - the expression the boxes stand for, wrapped in
   * <code>HoldComplete</code>; the inverse of <code>MakeBoxes</code>.
   */
  private static class MakeExpression extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr form = ast.isAST2() ? ast.arg2() : S.StandardForm;
      if (form != S.StandardForm && form != S.TraditionalForm) {
        return F.NIL;
      }
      return engine.evaluate(F.ternaryAST3(S.ToExpression, ast.arg1(), form, S.HoldComplete));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class MakeBoxes extends AbstractEvaluator {
    // written as they are typed, as the outputs a WLJS notebook saves show them: the front end
    // draws
    // the arrow itself
    private static StandardFormOperator RULE = new StandardFormOperator(Precedence.RULE, "->");
    private static StandardFormOperator RULE_DELAYED =
        new StandardFormOperator(Precedence.RULEDELAYED, ":>");

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
        // the operator row is put in brackets as a whole: RowBox({"(", RowBox({a, "||", b}),
        // ")"})
        IASTAppendable row = precedence > fPrecedence ? F.ListAlloc(f.size() * 2) : list;
        for (int i = 1; i < f.size(); i++) {
          IExpr arg = f.get(i);
          if (f.isOr() && arg.isAnd()) {
            // a && b || c is written (a && b) || c
            row.append(F.RowBox(F.list(F.$str("("),
                standardFormRecursive(arg, 0, form, engine), F.$str(")"))));
          } else {
            row.append(standardFormRecursive(arg, fPrecedence, form, engine));
          }
          if (i < f.argSize()) {
            if (fOperator.compareTo("") != 0) {
              row.append(fOperator);
            }
          }
        }
        if (row != list) {
          list.append("(");
          list.append(F.RowBox(row));
          list.append(")");
        }
        return true;
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
     * <code>MakeBoxes</code> is HoldAllComplete and that probe is skipped for such a head.
     */
    private static IExpr upValueBoxes(IExpr expr, IExpr form, EvalEngine engine) {
      // an object held as an atom (a Graph, a ByteArray, ...) carries its head like an expression
      if (engine == null || !(expr.isASTOrAssociation() || expr instanceof IDataExpr)
          || !RulesData.isUpRulesDefined()) {
        return F.NIL;
      }
      ISymbol head = expr.topHead();
      IExpr boxes = head.evalUpRules(F.binaryAST2(S.MakeBoxes, expr, form), engine);
      if (boxes.isNIL() && head.isBuiltInSymbol() && expr.isAST()) {
        // a definition for pref_. * f[...] is kept with f, the first head which is not protected
        ISymbol tag = PatternMatching.firstUnprotectedHead(expr);
        if (tag != null && tag != head) {
          boxes = tag.evalUpRules(F.binaryAST2(S.MakeBoxes, expr, form), engine);
        }
      }
      if (boxes.isNIL() && expr instanceof IDataExpr) {
        // a rule written for the form of the object - Image[data, type, ...] -
        // is tried on that form: an image object has no parts for Image[_, type_, ___] to match
        IAST normal = ((IDataExpr<?>) expr).normal(true);
        if (normal.isPresent() && normal.head() == head) {
          boxes = head.evalUpRules(F.binaryAST2(S.MakeBoxes, normal, form), engine);
        }
      }
      return boxes;
    }

    /**
     * Boxes which a definition gave for an expression carry no operator precedence. In a product,
     * a <code>Dot</code> or a power they are put in parentheses when they are a row of several
     * boxes, and as the base of a power also when they are a superscript.
     */
    private static IExpr parenthesizeDefinedBoxes(IExpr boxes, int precedence) {
      IExpr core = boxes;
      while ((core.isAST(S.FormBox) || core.isAST(S.TagBox) || core.isAST(S.StyleBox))
          && core.argSize() >= 1) {
        core = core.first();
      }
      boolean row = core.isAST(S.RowBox, 2) && core.first().isList()
          && ((IAST) core.first()).argSize() > 1;
      boolean superscript = core.isAST(S.SuperscriptBox) || core.isAST(S.SubsuperscriptBox);
      if ((row && precedence >= Precedence.TIMES)
          || (superscript && precedence >= Precedence.POWER)) {
        return F.RowBox(F.list(F.$str("("), boxes, F.$str(")")));
      }
      return boxes;
    }

    private static IExpr standardFormRecursive(final IExpr expr, final int precedence,
        final IExpr form, final EvalEngine engine) {
      IExpr boxes = upValueBoxes(expr, form, engine);
      if (boxes.isPresent()) {
        return parenthesizeDefinedBoxes(boxes, precedence);
      }
      if (expr.isAST(S.Texture, 2)) {
        // a texture is shown as its image.
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
      if (expr.isAST(S.HoldForm, 2)) {
        // an invisible wrapper: TagBox keeps it for the way back, the front end shows the content
        return F.binaryAST2(S.TagBox, standardFormRecursive(expr.first(), precedence, form, engine),
            S.HoldForm);
      }
      if (expr.isAST(S.Defer, 2)) {
        return standardFormRecursive(expr.first(), precedence, form, engine);
      }
      if (expr.isAST(S.TraditionalForm, 2)) {
        return F.ternaryAST3(S.TagBox,
            F.binaryAST2(S.FormBox,
                standardFormRecursive(expr.first(), 0, S.TraditionalForm, engine),
                S.TraditionalForm),
            S.TraditionalForm, F.Rule(S.Editable, S.True));
      }
      if (expr.isString()) {
        return F.$str(quotedString(expr.toString()));
      }
      if (expr.isAST() && NumberFormatter.formKind(expr.head()) != null) {
        IExpr formatted = numberFormBox((IAST) expr, engine);
        if (formatted.isPresent()) {
          return formatted;
        }
      }
      if (form == S.TraditionalForm && expr.isImaginaryUnit()) {
        // \[ImaginaryI]
        return F.$str("");
      }
      if (form == S.TraditionalForm && expr == S.E) {
        // \[ExponentialE]
        return F.$str("");
      }
      IExpr special = specialSymbolBox(expr);
      if (special.isPresent()) {
        return special;
      }
      // MakeBoxes holds its argument, so <|a -> b|> may still be the unevaluated Association[a ->
      // b]
      if (expr.isAssociation()
          || (expr.isAST(S.Association) && ((IAST) expr).forAll(x -> x.isRuleAST()))) {
        IAST association = (IAST) expr;
        IASTAppendable list = F.ListAlloc(3);
        list.append("<|");
        appendArgumentRow(list, association, form, engine);
        list.append("|>");
        return F.RowBox(list);
      }
      IExpr shaped = shapedBox(expr, precedence, form, engine);
      if (shaped.isPresent()) {
        return shaped;
      }
      if (expr.isASTOrAssociation()) {
        IAST function = (IAST) expr;
        if (function.size() > 0) {
          if (function.isList()) {
            IASTAppendable list = F.ListAlloc(3);
            list.append("{");
            // {} is RowBox({"{","}"}), with no empty row in between
            appendArgumentRow(list, function, form, engine);
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
          if (form == S.TraditionalForm) {
            IExpr traditional = traditionalLogicBox(function, precedence, form, engine);
            if (traditional.isPresent()) {
              return traditional;
            }
          }
          if (function.isNot() && function.argSize() == 1) {
            return parenthesizeRow(
                F.RowBox(F.list(F.$str("!"),
                    standardFormRecursive(function.arg1(), Precedence.NOT, form, engine))),
                Precedence.NOT, precedence);
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
          if (form == S.TraditionalForm) {
            IExpr notation = traditionalNotationBox(function, form, engine);
            if (notation.isPresent()) {
              return notation;
            }
            IExpr head = function.head();
            if (head.isSymbol() && INVERSE_NAMES.containsKey(head) && function.argSize() == 1) {
              // arcsin(x) is sin^-1(x)
              return traditionalCallBox(function,
                  F.binaryAST2(S.SuperscriptBox, F.$str(INVERSE_NAMES.get(head)),
                      F.RowBox(F.list(F.$str("-"), F.$str("1")))),
                  form, engine);
            }
            if (!isBracketedBuiltin(head)) {
              return traditionalCallBox(function,
                  head.isSymbol() && head != S.E ? F.$str(traditionalName((ISymbol) head))
                      : standardFormRecursive(head, Precedence.POWER, form, engine),
                  form, engine);
            }
          }
          IASTAppendable list = F.ListAlloc(4);
          list.append(standardFormRecursive(function.head(), precedence, form, engine));
          list.append("[");
          appendArgumentRow(list, function, form, engine);
          list.append("]");
          return F.RowBox(list);
        }
      } else if (expr.isSymbol()) {
        return F.$str(expr.toString());
      } else if (expr.isNumber()) {
        return numberBox((INumber) expr, precedence);
      }
      return F.$str(expr.toString());
    }

    /**
     * Append the comma-separated <code>RowBox</code> of the arguments of <code>function</code> to
     * <code>list</code>. The arguments are separated by commas, so each one is boxed at precedence
     * <code>0</code>. Nothing is appended for a function without arguments.
     *
     * @param list the boxes of the enclosing bracket construct
     * @param function the function whose arguments are boxed
     * @param form the box form
     * @param engine the evaluation engine
     */
    private static void appendArgumentRow(IASTAppendable list, IAST function, IExpr form,
        EvalEngine engine) {
      if (function.argSize() == 1) {
        // a single argument is no row of its own: RowBox({"f", "[", "x", "]"})
        list.append(standardFormRecursive(function.getRule(1), 0, form, engine));
      } else if (function.argSize() > 0) {
        IASTAppendable argsList = F.ListAlloc(function.size());
        for (int i = 1; i < function.size(); i++) {
          argsList.append(standardFormRecursive(function.getRule(i), 0, form, engine));
          if (i < function.argSize()) {
            argsList.append(",");
          }
        }
        list.append(F.RowBox(argsList));
      }
    }

    /**
     * <code>Piecewise({{v1, c1}, ...}, default)</code> as a brace in front of a grid with one row
     * <code>{v, c}</code> per case and the default in a last row with the condition
     * <code>True</code>.
     *
     * @return {@link F#NIL} if the cases are not a list of pairs
     */
    private static IExpr piecewiseBox(IAST piecewise, IExpr form, EvalEngine engine) {
      if (!piecewise.arg1().isList()) {
        return F.NIL;
      }
      IAST cases = (IAST) piecewise.arg1();
      IASTAppendable rows = F.ListAlloc(cases.size() + 1);
      for (IExpr pair : cases) {
        if (!pair.isList2()) {
          return F.NIL;
        }
        rows.append(F.list(standardFormRecursive(pair.first(), 0, form, engine),
            standardFormRecursive(pair.second(), 0, form, engine)));
      }
      if (piecewise.isAST2()) {
        rows.append(F.list(standardFormRecursive(piecewise.arg2(), 0, form, engine),
            F.ternaryAST3(S.TagBox, F.$str("True"), F.$str("PiecewiseDefault"),
                F.Rule(S.AutoDelete, S.True))));
      }
      IAST grid = F.ast(new IExpr[] {rows, //
          F.Rule(S.ColumnAlignments, F.list(S.Left)), //
          F.Rule(S.ColumnSpacings, F.num(1.2)), //
          F.Rule(S.ColumnWidths, S.Automatic), //
          F.Rule(S.AllowedDimensions, F.list(F.C2, S.Automatic)), //
          F.Rule(S.Selectable, S.True), //
          F.Rule(S.Editable, S.True)}, S.GridBox);
      IAST brace = F.ast(new IExpr[] {F.list(F.list(F.$str(""), grid)), //
          F.Rule(S.ColumnAlignments, F.list(S.Left)), //
          F.Rule(S.ColumnSpacings, F.num(0.5)), //
          F.Rule(S.ColumnWidths, S.Automatic)}, S.GridBox);
      return F.ast(new IExpr[] {brace, F.$str("Piecewise"), //
          F.Rule(S.SyntaxForm, S.Equal), //
          F.Rule(S.SelectWithContents, S.True), //
          F.Rule(S.StripWrapperBoxes, S.True), //
          F.Rule(S.Selectable, S.False), //
          F.Rule(S.Editable, S.False), //
          F.Rule(S.DeleteWithContents, S.True)}, S.TagBox);
    }

    /** The elementary functions <code>TraditionalForm</code> writes in lower case. */
    private static final Map<ISymbol, String> TRADITIONAL_NAMES = new IdentityHashMap<>();

    static {
      ISymbol[] lowerCase = {S.Sin, S.Cos, S.Tan, S.Cot, S.Sec, S.Csc, S.Sinh, S.Cosh, S.Tanh,
          S.Coth, S.Sech, S.Csch, S.Log};
      for (ISymbol symbol : lowerCase) {
        TRADITIONAL_NAMES.put(symbol, symbol.toString().toLowerCase(java.util.Locale.ROOT));
      }
    }

    /** Further functions <code>TraditionalForm</code> writes in lower case, without exponents. */
    private static final Map<ISymbol, String> LOWER_CASE_NAMES = new IdentityHashMap<>();

    /** The inverse functions written as <code>sin^-1</code>, by the name of the function. */
    private static final Map<ISymbol, String> INVERSE_NAMES = new IdentityHashMap<>();

    static {
      for (ISymbol symbol : new ISymbol[] {S.Max, S.Min, S.GCD, S.LCM, S.Erf, S.Erfc}) {
        LOWER_CASE_NAMES.put(symbol, symbol.toString().toLowerCase(java.util.Locale.ROOT));
      }
      ISymbol[] inverse = {S.ArcSin, S.ArcCos, S.ArcTan, S.ArcCot, S.ArcSec, S.ArcCsc, S.ArcSinh,
          S.ArcCosh, S.ArcTanh, S.ArcCoth, S.ArcSech, S.ArcCsch};
      for (ISymbol symbol : inverse) {
        INVERSE_NAMES.put(symbol, symbol.toString().substring(3).toLowerCase(java.util.Locale.ROOT));
      }
    }

    /** The name of a function in <code>TraditionalForm</code>: <code>sin</code> for <code>Sin</code>. */
    private static String traditionalName(ISymbol head) {
      String name = TRADITIONAL_NAMES.get(head);
      if (name == null) {
        name = LOWER_CASE_NAMES.get(head);
      }
      return name != null ? name : head.toString();
    }

    /**
     * A built-in function which is no mathematical function, such as <code>Length</code>, keeps
     * its brackets in <code>TraditionalForm</code>; mathematical and user-defined functions take
     * parentheses.
     */
    private static boolean isBracketedBuiltin(IExpr head) {
      return head.isBuiltInSymbol() && head != S.E && !TRADITIONAL_NAMES.containsKey(head)
          && !LOWER_CASE_NAMES.containsKey(head) && !((ISymbol) head).hasNumericFunctionAttribute();
    }

    /**
     * A function call in <code>TraditionalForm</code>: the name followed by the arguments in
     * parentheses, <code>RowBox({"sin", "(", "x", ")"})</code>.
     *
     * @param name the box of the function name, which may carry an exponent
     */
    private static IExpr traditionalCallBox(IAST function, IExpr name, IExpr form,
        EvalEngine engine) {
      IASTAppendable list = F.ListAlloc(4);
      list.append(name);
      list.append("(");
      appendArgumentRow(list, function, form, engine);
      list.append(")");
      return F.RowBox(list);
    }

    /**
     * The textbook notation of a function in <code>TraditionalForm</code>: a derivative as a
     * quotient of differentials, a binomial coefficient as a stacked pair, a hypergeometric
     * function as <code>ₚF_q(a…; b…; z)</code> and the vector operators as <code>∇</code> with the
     * coordinates below.
     *
     * @return {@link F#NIL} for any other function
     */
    private static IExpr traditionalNotationBox(IAST function, IExpr form, EvalEngine engine) {
      if (function.isAST(S.D) && function.argSize() >= 2) {
        return derivativeBox(function, form, engine);
      }
      if (function.isAST(S.Binomial, 3)) {
        return F.RowBox(F.list(F.$str("("),
            F.unaryAST1(S.GridBox,
                F.list(F.list(standardFormRecursive(function.arg1(), 0, form, engine)),
                    F.list(standardFormRecursive(function.arg2(), 0, form, engine)))),
            F.$str(")")));
      }
      if (function.isAST(S.Hypergeometric0F1, 3)) {
        return hypergeometricBox(F.CEmptyList, F.list(function.arg1()), function.arg2(), form,
            engine);
      }
      if (function.isAST(S.Hypergeometric1F1, 4)) {
        return hypergeometricBox(F.list(function.arg1()), F.list(function.arg2()),
            function.arg3(), form, engine);
      }
      if (function.isAST(S.Hypergeometric2F1, 5)) {
        return hypergeometricBox(F.list(function.arg1(), function.arg2()),
            F.list(function.arg3()), function.arg4(), form, engine);
      }
      if (function.isAST(S.HypergeometricPFQ, 4) && function.arg1().isList()
          && function.arg2().isList()) {
        return hypergeometricBox((IAST) function.arg1(), (IAST) function.arg2(), function.arg3(),
            form, engine);
      }
      if ((function.isAST(S.Div, 3) || function.isAST(S.Laplacian, 3))
          && function.arg2().isList()) {
        IExpr coordinates = standardFormRecursive(function.arg2(), 0, form, engine);
        IExpr operand = standardFormRecursive(function.arg1(), Precedence.TIMES + 1, form, engine);
        if (function.isAST(S.Div)) {
          return F.RowBox(F.list(F.binaryAST2(S.SubscriptBox, F.$str("∇"), coordinates),
              F.$str("⋅"), operand));
        }
        return F.RowBox(F.list(
            F.ternaryAST3(S.SubsuperscriptBox, F.$str("∇"), coordinates, F.$str("2")),
            operand));
      }
      return F.NIL;
    }

    /**
     * <code>D(f, x, {y, 2})</code> as <code>∂³f/(∂x ∂y²)</code>. A quotient is
     * differentiated by the operator <code>∂/∂x</code> written in front of it instead.
     *
     * @return {@link F#NIL} if an order is no positive integer
     */
    private static IExpr derivativeBox(IAST d, IExpr form, EvalEngine engine) {
      IASTAppendable differentials = F.ListAlloc(d.size() * 2);
      int order = 0;
      for (int i = 2; i < d.size(); i++) {
        IExpr variable = d.get(i);
        int n = 1;
        if (variable.isList()) {
          if (!variable.isList2() || !variable.second().isInteger()) {
            return F.NIL;
          }
          n = variable.second().toIntDefault();
          if (n <= 0) {
            return F.NIL;
          }
          variable = variable.first();
        }
        order += n;
        IExpr variableBox = standardFormRecursive(variable, Precedence.POWER, form, engine);
        if (differentials.argSize() > 0) {
          // a thin space between the differentials
          differentials.append(F.$str(" "));
        }
        differentials.append(F.RowBox(F.list(F.$str("∂"), n == 1 ? variableBox
            : F.binaryAST2(S.SuperscriptBox, variableBox, F.$str(Integer.toString(n))))));
      }
      IExpr partial = order == 1 ? F.$str("∂")
          : F.binaryAST2(S.SuperscriptBox, F.$str("∂"), F.$str(Integer.toString(order)));
      IExpr below = differentials.argSize() == 1 ? differentials.arg1() : F.RowBox(differentials);
      IExpr derivand = d.arg1();
      IExpr derivandBox = standardFormRecursive(derivand, Precedence.TIMES + 1, form, engine);
      if (isQuotient(derivand)) {
        return F.binaryAST2(S.TagBox,
            F.RowBox(F.list(F.FractionBox(partial, below), derivandBox)), S.D);
      }
      return F.ternaryAST3(S.FractionBox, F.RowBox(F.list(partial, derivandBox)), below,
          F.Rule(S.MultilineFunction, S.None));
    }

    /** Whether the expression is written as a fraction. */
    private static boolean isQuotient(IExpr expr) {
      if (expr.isRational() && !expr.isInteger()) {
        return true;
      }
      if (expr.isPower()) {
        return expr.exponent().isNumber() && expr.exponent().isNegative();
      }
      return expr.isTimes() && ((IAST) expr).exists(x -> isQuotient(x));
    }

    /** <code>ₚF_q(a1, …; b1, …; z)</code>, with the counts of the parameters as subscripts. */
    private static IExpr hypergeometricBox(IAST upper, IAST lower, IExpr z, IExpr form,
        EvalEngine engine) {
      IExpr name = F.RowBox(F.list(
          F.binaryAST2(S.SubscriptBox, F.$str(""), F.$str(Integer.toString(upper.argSize()))),
          F.binaryAST2(S.SubscriptBox, F.$str("F"), F.$str(Integer.toString(lower.argSize())))));
      IASTAppendable row = F.ListAlloc(2 * (upper.size() + lower.size()) + 2);
      for (IAST parameters : new IAST[] {upper, lower}) {
        for (int i = 1; i < parameters.size(); i++) {
          if (i > 1) {
            row.append(F.$str(","));
          }
          row.append(standardFormRecursive(parameters.get(i), 0, form, engine));
        }
        row.append(F.$str(";"));
      }
      row.append(standardFormRecursive(z, 0, form, engine));
      return F.RowBox(F.List(name, F.$str("("), F.RowBox(row), F.$str(")")));
    }

    /**
     * <code>sin^n(x)</code>: in <code>TraditionalForm</code> an integer or symbolic power of a
     * function written in lower case goes on the function name.
     *
     * @return {@link F#NIL} for any other power or form
     */
    private static IExpr traditionalPowerBox(IExpr base, IExpr exponent, IExpr form,
        EvalEngine engine) {
      if (form != S.TraditionalForm || !base.isAST()
          || !(exponent.isInteger() || exponent.isSymbol())) {
        return F.NIL;
      }
      String name = TRADITIONAL_NAMES.get(base.head());
      if (name == null) {
        return F.NIL;
      }
      return traditionalCallBox((IAST) base, F.binaryAST2(S.SuperscriptBox, F.$str(name),
          standardFormRecursive(exponent, 0, form, engine)), form, engine);
    }

    /**
     * The sign of a comparison in <code>TraditionalForm</code>: <code>≤</code> for
     * <code>LessEqual</code>, <code>≥</code> for <code>GreaterEqual</code> and
     * <code>≠</code> for <code>Unequal</code>.
     *
     * @return <code>null</code> for a head which isn't a comparison
     */
    private static String traditionalComparison(IExpr head) {
      if (head == S.Less) {
        return "<";
      }
      if (head == S.LessEqual) {
        return "≤";
      }
      if (head == S.Greater) {
        return ">";
      }
      if (head == S.GreaterEqual) {
        return "≥";
      }
      if (head == S.Equal) {
        return "==";
      }
      if (head == S.Unequal) {
        return "≠";
      }
      return null;
    }

    /**
     * The boxes of a comparison chain, a conjunction, a disjunction or a negation in
     * <code>TraditionalForm</code>: <code>Inequality(x, LessEqual, y, Less, z)</code> is the row
     * <code>x ≤ y &lt; z</code>; <code>And</code>, <code>Or</code> and <code>Not</code> are
     * written with <code>∧</code>, <code>∨</code> and <code>¬</code>. A
     * logical connective inside a conjunction or a disjunction is put in parentheses.
     *
     * @return {@link F#NIL} if the expression is none of these
     */
    private static IExpr traditionalLogicBox(IAST function, int precedence, IExpr form,
        EvalEngine engine) {
      final IExpr head = function.head();
      if (function.isAST(S.Inequality) && function.argSize() >= 3
          && (function.argSize() & 1) == 1) {
        IASTAppendable row = F.ListAlloc(function.size());
        for (int i = 1; i < function.size(); i++) {
          if ((i & 1) == 0) {
            String operator = traditionalComparison(function.get(i));
            if (operator == null) {
              return F.NIL;
            }
            row.append(operator);
          } else {
            row.append(standardFormRecursive(function.get(i), Precedence.LESS, form, engine));
          }
        }
        return parenthesizeRow(F.RowBox(row), Precedence.LESS, precedence);
      }
      if (function.argSize() >= 2
          && (head == S.LessEqual || head == S.GreaterEqual || head == S.Unequal)) {
        return infixRow(function, traditionalComparison(head), Precedence.LESS, precedence, form,
            engine);
      }
      if (function.isNot()) {
        return parenthesizeRow(
            F.RowBox(F.list(F.$str("¬"),
                standardFormRecursive(function.arg1(), Precedence.NOT, form, engine))),
            Precedence.NOT, precedence);
      }
      if ((function.isAnd() || function.isOr()) && function.argSize() >= 2) {
        return function.isAnd()
            ? infixRow(function, "∧", Precedence.AND, precedence, form, engine)
            : infixRow(function, "∨", Precedence.OR, precedence, form, engine);
      }
      return F.NIL;
    }

    /**
     * The arguments of <code>function</code> with <code>operator</code> in between. A logical
     * connective among them is put in parentheses.
     */
    private static IExpr infixRow(IAST function, String operator, int operatorPrecedence,
        int precedence, IExpr form, EvalEngine engine) {
      IASTAppendable row = F.ListAlloc(2 * function.size());
      for (int i = 1; i < function.size(); i++) {
        if (i > 1) {
          row.append(operator);
        }
        IExpr arg = function.get(i);
        if (arg.isAnd() || arg.isOr() || arg.isAST(S.Xor) || arg.isAST(S.Nand) || arg.isAST(S.Nor)
            || arg.isAST(S.Implies) || arg.isAST(S.Equivalent)) {
          row.append(F.RowBox(
              F.list(F.$str("("), standardFormRecursive(arg, 0, form, engine), F.$str(")"))));
        } else {
          row.append(standardFormRecursive(arg, operatorPrecedence, form, engine));
        }
      }
      return parenthesizeRow(F.RowBox(row), operatorPrecedence, precedence);
    }

    /** The row in parentheses if its operator binds less tightly than the surrounding one. */
    private static IExpr parenthesizeRow(IExpr row, int rowPrecedence, int precedence) {
      if (precedence > rowPrecedence) {
        return F.RowBox(F.list(F.$str("("), row, F.$str(")")));
      }
      return row;
    }

    /**
     * A number in a number form wrapper as the number it is shown as: <code>NumberForm(2.71828, {4,
     * 2})</code> is <code>TagBox(InterpretationBox("2.72", 2.71828), NumberForm(#1, {4, 2})&amp;)
     * </code>, a number in scientific notation the row <code>7.08×10^7</code>. The
     * <code>InterpretationBox</code> reads back as the number and the tag as the wrapper.
     *
     * @return {@link F#NIL} if the wrapper does not format an integer or a real number
     */
    private static IExpr numberFormBox(IAST formAST, EvalEngine engine) {
      NumberFormatter.FormattedNumber formatted =
          NumberFormPrinter.formatArgument(formAST, engine == null ? EvalEngine.get() : engine);
      if (formatted == null) {
        return F.NIL;
      }
      IExpr shown = F.$str(formatted.mantissa);
      if (formatted.scientific) {
        shown = F.RowBox(F.list(shown, F.$str(formatted.multiplier),
            F.binaryAST2(S.SuperscriptBox, F.$str("10"),
                F.$str(Integer.toString(formatted.exponent)))));
      }
      return F.binaryAST2(S.TagBox, F.binaryAST2(S.InterpretationBox, shown, formAST.arg1()),
          F.Function(formAST.setAtCopy(1, F.Slot1)));
    }

    /**
     * A string as a string box: in quotation marks, with the characters which would end or break it
     * escaped - <code>ToBoxes["a\"b"]</code> is the box <code>"a\"b"</code>, marks included. The
     * marks are part of the box, which is how a notebook tells <code>{"123"}</code> from
     * <code>{123}</code>.
     */
    private static String quotedString(String text) {
      StringBuilder buf = new StringBuilder(text.length() + 2);
      buf.append('"');
      for (int i = 0; i < text.length(); i++) {
        char ch = text.charAt(i);
        switch (ch) {
          case '"':
            buf.append("\\\"");
            break;
          case '\\':
            buf.append("\\\\");
            break;
          case '\n':
            buf.append("\\n");
            break;
          case '\t':
            buf.append("\\t");
            break;
          default:
            buf.append(ch);
        }
      }
      return buf.append('"').toString();
    }

    /**
     * The constants a notebook draws as a glyph rather than spells out - <code>\[Pi]</code> for
     * <code>Pi</code>, <code>\[Infinity]</code> for <code>Infinity</code> - or {@link F#NIL} for
     * any other expression. <code>E</code> and <code>I</code> stay letters: that is how the outputs
     * a WLJS notebook saves spell them.
     */
    private static IExpr specialSymbolBox(IExpr expr) {
      if (expr == S.Pi) {
        return F.$str("\\[Pi]");
      }
      if (expr.isAST(S.DirectedInfinity, 2)) {
        if (expr.first().isOne()) {
          return F.$str("\\[Infinity]");
        }
        if (expr.first().isMinusOne()) {
          return F.RowBox(F.list(F.$str("-"), F.$str("\\[Infinity]")));
        }
      }
      if (expr.isAST(S.DirectedInfinity, 1)) {
        return F.$str("ComplexInfinity");
      }
      return F.NIL;
    }

    /** The spelling of the imaginary unit in a box, as a saved notebook output spells it. */
    private static final String IMAGINARY_I = "I";

    /**
     * A number as it's written in a box: a negative one with its sign in front, a rational as a
     * fraction, an imaginary part followed by <code>I</code> and a machine real with all of its
     * digits and the <code>`</code> which marks its precision - <code>0.8459659909775918`</code>,
     * as the notebook's saved outputs show it.
     */
    private static IExpr numberBox(INumber number, int precedence) {
      if (number.isComplex() || number.isComplexNumeric()) {
        INumber re = number.re();
        INumber im = number.im();
        if (re.isZero()) {
          if (im.isNegative()) {
            return parenthesize(F.RowBox(F.list(F.$str("-"), imaginaryBox(im.negate()))),
                Precedence.TIMES, precedence);
          }
          return imaginaryBox(im);
        }
        IExpr sum = F.RowBox(F.list(numberBox(re, Precedence.PLUS),
            F.$str(im.isNegative() ? "-" : "+"), imaginaryBox(im.isNegative() ? im.negate() : im)));
        return parenthesize(sum, Precedence.PLUS, precedence);
      }
      if (number.isNegative()) {
        // -2 is the row RowBox({"-", "2"}), as the minus is written in front of any other factor
        IExpr positive = numberBox(number.negate(), Precedence.TIMES);
        return parenthesize(F.RowBox(F.list(F.$str("-"), positive)), Precedence.TIMES, precedence);
      }
      if (number.isRational() && !number.isInteger()) {
        IRational rational = (IRational) number;
        return F.FractionBox(F.$str(rational.numerator().toString()),
            F.$str(rational.denominator().toString()));
      }
      return number instanceof org.matheclipse.core.expression.Num
          ? F.$str(machineReal(number.reDoubleValue()))
          : F.$str(number.toString());
    }

    /** <code>I</code>, <code>2 I</code> or <code>(1/2) I</code> for a positive imaginary part. */
    private static IExpr imaginaryBox(INumber im) {
      if (im.isOne()) {
        return F.$str(IMAGINARY_I);
      }
      return F.RowBox(F.list(numberBox(im, Precedence.TIMES), F.$str(" "), F.$str(IMAGINARY_I)));
    }

    /**
     * The InputForm digits of a machine real - the shortest ones which read back as the same double
     * - followed by the precision mark: <code>1.`</code>, <code>0.25`</code>,
     * <code>1.5`*^-7</code>.
     */
    static String machineReal(double value) {
      if (Double.isNaN(value) || Double.isInfinite(value)) {
        return Double.toString(value);
      }
      java.math.BigDecimal decimal =
          new java.math.BigDecimal(Double.toString(value)).stripTrailingZeros();
      String digits = decimal.unscaledValue().abs().toString();
      // the position of the decimal point relative to the first digit
      int exponent = digits.length() - decimal.scale() - 1;
      StringBuilder buf = new StringBuilder();
      if (value < 0) {
        buf.append('-');
      }
      if (decimal.signum() == 0) {
        return buf.append("0.`").toString();
      }
      if (exponent >= 6 || exponent <= -6) {
        buf.append(digits.charAt(0)).append('.').append(digits, 1, digits.length()).append('`')
            .append("*^").append(exponent);
        return buf.toString();
      }
      if (exponent < 0) {
        buf.append("0.");
        for (int i = -1; i > exponent; i--) {
          buf.append('0');
        }
        buf.append(digits);
      } else if (digits.length() <= exponent + 1) {
        buf.append(digits);
        for (int i = digits.length(); i <= exponent; i++) {
          buf.append('0');
        }
        buf.append('.');
      } else {
        buf.append(digits, 0, exponent + 1).append('.').append(digits, exponent + 1,
            digits.length());
      }
      return buf.append('`').toString();
    }

    /**
     * The boxes of the expressions a notebook shows as something other than their text: a colour as
     * a swatch, a table as a grid and a power series as its terms followed by <code>O[x]^n</code>.
     * The notebook turns a
     * <code>TemplateBox[&lt;|"color" -> c|&gt;, "RGBColorSwatchTemplate"]</code> or a
     * <code>GridBox</code> into its widget. {@link F#NIL} for any other expression.
     */
    private static IExpr shapedBox(IExpr expr, int precedence, IExpr form, EvalEngine engine) {
      if (expr instanceof ASTSeriesData) {
        return seriesBox((ASTSeriesData) expr, form, engine);
      }
      if (!expr.isAST() || expr.size() < 2) {
        return F.NIL;
      }
      IAST ast = (IAST) expr;
      if (ast.argSize() == 1 && ast.arg1().isList()
          && (ast.head() == S.RGBColor || ast.head() == S.GrayLevel || ast.head() == S.Hue)) {
        // the channels packed into one list are the same colour as the channels spread out
        ast = ((IAST) ast.arg1()).apply(ast.head());
      }
      String template = null;
      if (ast.isAST(S.RGBColor, 4, 5)) {
        template = "RGBColorSwatchTemplate";
      } else if (ast.isAST(S.GrayLevel, 2, 3)) {
        template = "GrayLevelColorSwatchTemplate";
      } else if (ast.isAST(S.Hue, 2, 5)) {
        template = "HueColorSwatchTemplate";
      }
      if (template != null) {
        if (ast.forAll(x -> x.isReal())) {
          return F.binaryAST2(S.TemplateBox, F.assoc(F.list(F.Rule(F.$str("color"), ast))),
              F.$str(template));
        }
        return F.NIL;
      }
      if (ast.isAST(S.Piecewise, 2, 3)) {
        IExpr piecewise = piecewiseBox(ast, form, engine);
        if (piecewise.isPresent()) {
          // an operand of a sum or a relation is put in parentheses
          return form == S.TraditionalForm && precedence > 0
              ? F.RowBox(F.list(F.$str("("), piecewise, F.$str(")")))
              : piecewise;
        }
      }
      if (ast.isAST(S.Style) && ast.argSize() >= 2) {
        // Style(expr, styles...): the boxes of expr with the styles
        IASTAppendable styleBox = F.ast(S.StyleBox, ast.size() + 1);
        styleBox.append(standardFormRecursive(ast.arg1(), 0, form, engine));
        for (int i = 2; i < ast.size(); i++) {
          styleBox.append(ast.get(i));
        }
        styleBox.append(F.Rule(S.StripOnInput, S.False));
        return styleBox;
      }
      if (ast.isAST(S.Subscript) && ast.argSize() >= 2) {
        // Subscript(x, i, j, ...): the indices as a row with commas below the base
        IASTAppendable indices = F.ListAlloc(2 * ast.argSize());
        for (int i = 2; i < ast.size(); i++) {
          if (i > 2) {
            indices.append(F.$str(","));
          }
          indices.append(standardFormRecursive(ast.get(i), 0, form, engine));
        }
        return F.binaryAST2(S.SubscriptBox,
            standardFormRecursive(ast.arg1(), Precedence.POWER, form, engine),
            indices.argSize() == 1 ? indices.arg1() : F.RowBox(indices));
      }
      if (ast.isAST(S.SuperStar, 2) || ast.isAST(S.SuperDagger, 2)) {
        // x^* and x^†: the mark is the superscript
        return F.binaryAST2(S.SuperscriptBox,
            standardFormRecursive(ast.arg1(), Precedence.POWER, form, engine),
            F.$str(ast.isAST(S.SuperStar) ? "*" : "†"));
      }
      if (ast.isAST(S.Superscript, 3)) {
        return F.binaryAST2(S.SuperscriptBox,
            standardFormRecursive(ast.arg1(), Precedence.POWER, form, engine),
            standardFormRecursive(ast.arg2(), 0, form, engine));
      }
      if (ast.isAST(S.Subsuperscript, 4)) {
        IExpr base = standardFormRecursive(ast.arg1(), Precedence.POWER, form, engine);
        IExpr subscript = standardFormRecursive(ast.arg2(), 0, form, engine);
        IExpr superscript = standardFormRecursive(ast.arg3(), 0, form, engine);
        // the template which is written as a SubsuperscriptBox
        return F.ternaryAST3(S.TemplateBox, F.list(base, subscript, superscript),
            F.$str("Subsuperscript"), F.Rule(S.SyntaxForm, S.SubsuperscriptBox));
      }
      if (ast.isAST(S.Row, 2) && ast.arg1().isList()) {
        // Row({e1, e2, ...}): the boxes of the elements side by side
        IAST elements = (IAST) ast.arg1();
        IASTAppendable boxes = F.ListAlloc(elements.argSize());
        for (int i = 1; i < elements.size(); i++) {
          boxes.append(standardFormRecursive(elements.get(i), 0, form, engine));
        }
        return F.binaryAST2(S.TemplateBox, boxes, F.$str("RowDefault"));
      }
      if (ast.isAST(S.TableForm, 2) || ast.isAST(S.Grid, 2)) {
        IExpr table = ast.arg1();
        if (table.isList() && ((IAST) table).argSize() > 0) {
          IAST rows = (IAST) table;
          boolean matrix = rows.forAll(x -> x.isList());
          if (!matrix && ast.isAST(S.Grid)) {
            return F.NIL;
          }
          IASTAppendable gridRows = F.ListAlloc(rows.size());
          for (int i = 1; i < rows.size(); i++) {
            IExpr row = rows.get(i);
            IASTAppendable gridRow = F.ListAlloc(row.size());
            if (matrix) {
              for (int j = 1; j < row.size(); j++) {
                gridRow.append(standardFormRecursive(((IAST) row).get(j), 0, form, engine));
              }
            } else {
              // a flat list is a table of one column
              gridRow.append(standardFormRecursive(row, 0, form, engine));
            }
            gridRows.append(gridRow);
          }
          return F.unaryAST1(S.GridBox, gridRows);
        }
      }
      return F.NIL;
    }

    /**
     * A power series as the notebook shows it: its terms, then <code>O[x]^n</code>, the whole held
     * in an <code>InterpretationBox</code> so the output still reads back as the series.
     */
    private static IExpr seriesBox(ASTSeriesData series, IExpr form, EvalEngine engine) {
      IExpr normal = engine.evaluate(F.Normal(series));
      IExpr variable = series.expansionVariable();
      IExpr point = series.expansionPoint();
      IExpr order = F.Divide(F.ZZ(series.truncateOrder()), F.ZZ(series.puiseuxDenominator()));
      IExpr bigO =
          F.Power(F.unaryAST1(S.O, point.isZero() ? variable : F.Subtract(variable, point)),
              engine.evaluate(order));
      IASTAppendable row = F.ListAlloc(3);
      if (!normal.isZero()) {
        row.append(standardFormRecursive(normal, Precedence.PLUS, form, engine));
        row.append(F.$str("+"));
      }
      row.append(standardFormRecursive(bigO, Precedence.PLUS, form, engine));
      return F.binaryAST2(S.InterpretationBox, F.RowBox(row), series.toSeriesData());
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
     * <code>Plus</code>, <code>Times</code> and <code>Power</code> are the three heads a printed
     * expression is mostly made of, and writing them the way every other head is written -
     * <code>Plus[Times[…], Power[…]]</code> - is what a notebook cell showed instead of
     * <code>x&#178; + &#8730;2/3</code>. A power becomes a superscript, a square root a radical,
     * and a factor of negative power moves under the line into a fraction.
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
      IExpr traditional = traditionalPowerBox(base, exponent, form, engine);
      if (traditional.isPresent()) {
        return traditional;
      }
      if (base.isAST(S.Subscript, 3)) {
        // p_x^n: the exponent stands over the index
        return F.ternaryAST3(S.SubsuperscriptBox,
            standardFormRecursive(base.first(), Precedence.POWER, form, engine),
            standardFormRecursive(base.second(), 0, form, engine),
            standardFormRecursive(exponent, 0, form, engine));
      }
      return F.binaryAST2(S.SuperscriptBox, //
          powerBaseBox(base, form, engine),
          standardFormRecursive(exponent, 0, form, engine));
    }

    /**
     * The base of a superscript. A base which is itself a superscript is put in parentheses, since
     * <code>(a^b)^c</code> would otherwise read as <code>a^(b^c)</code>.
     */
    private static IExpr powerBaseBox(IExpr base, IExpr form, EvalEngine engine) {
      IExpr box = standardFormRecursive(base, Precedence.POWER, form, engine);
      if (box.isAST(S.SuperscriptBox)) {
        return F.RowBox(F.list(F.$str("("), box, F.$str(")")));
      }
      return box;
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
      IExpr traditional = traditionalPowerBox(base, positive, form, engine);
      if (traditional.isPresent()) {
        return traditional;
      }
      return F.binaryAST2(S.SuperscriptBox, //
          powerBaseBox(base, form, engine),
          standardFormRecursive(positive, 0, form, engine));
    }

    private static IExpr timesBox(IAST times, int precedence, IExpr form, EvalEngine engine) {
      if (times.exists(x -> x.isTimes())) {
        IExpr merged = mergedFractionBox(times, form, engine);
        if (merged.isPresent()) {
          return parenthesize(merged, Precedence.TIMES, precedence);
        }
      }
      IExpr positive = withoutMinus(times);
      if (positive.isPresent() && isProductWithoutFraction(times)) {
        // -x y is the product of -x and y: the minus goes with the first factor, and -2 x is
        // the product of -2 and x
        IExpr coefficient = times.arg1();
        IASTAppendable factors = F.ListAlloc(times.size());
        int start = 2;
        if (coefficient.isMinusOne()) {
          factors.append(F.RowBox(F.list(F.$str("-"),
              standardFormRecursive(times.arg2(), Precedence.TIMES, form, engine))));
          start = 3;
        } else {
          factors.append(numberBox((INumber) coefficient, Precedence.TIMES));
        }
        for (int i = start; i < times.size(); i++) {
          factors.append(standardFormRecursive(times.get(i),
              factorPrecedence(times.get(i), Precedence.TIMES), form, engine));
        }
        return parenthesize(riffle(factors, " "), Precedence.TIMES, precedence);
      }
      if (positive.isPresent()) {
        // -x and -(b/2) carry their sign in front, as a sum writes it between its terms
        IExpr negated = F.RowBox(
            F.list(F.$str("-"), standardFormRecursive(positive, Precedence.TIMES, form, engine)));
        return parenthesize(negated, Precedence.TIMES, precedence);
      }
      IASTAppendable numerator = F.ListAlloc(times.size());
      IASTAppendable denominator = F.ListAlloc(times.size());
      IExpr coefficientDenominator = F.NIL;
      for (int i = 1; i < times.size(); i++) {
        IExpr factor = times.get(i);
        if (factor.isPower() && factor.exponent().isNumber() && factor.exponent().isNegative()) {
          denominator.append(factor);
        } else if (i == 1 && factor.isRational() && !factor.isInteger()) {
          // a rational coefficient is split over the line: b/2 rather than (1/2) b
          IRational rational = (IRational) factor;
          if (!rational.numerator().isOne()) {
            numerator.append(rational.numerator());
          }
          coefficientDenominator = F.$str(rational.denominator().toString());
        } else {
          numerator.append(factor);
        }
      }
      if (coefficientDenominator.isPresent() && denominator.isEmpty()) {
        IExpr above = numerator.isEmpty() //
            ? F.$str("1")
            : riffle(
                boxes(numerator, numerator.argSize() == 1 ? 0 : Precedence.TIMES, form, engine),
                " ");
        return F.FractionBox(above, coefficientDenominator);
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
      IASTAppendable belowBoxes = F.ListAlloc(denominator.size() + 1);
      if (coefficientDenominator.isPresent()) {
        belowBoxes.append(coefficientDenominator);
      }
      for (int i = 1; i < denominator.size(); i++) {
        belowBoxes.append(reciprocalBox((IAST) denominator.get(i), form, engine));
      }
      return F.FractionBox(above, riffle(belowBoxes, " "));
    }

    /**
     * A product of quotients as one fraction, as a held
     * <code>(a/b) (c/d)</code>: <code>(a c)/(b d)</code>. Each factor is a group above or below
     * the line, and a group of several factors is put in parentheses when it shares its side with
     * other groups. A minus sign of the factors goes in front of the fraction.
     *
     * @return {@link F#NIL} if no factor stands under the line
     */
    private static IExpr mergedFractionBox(IAST times, IExpr form, EvalEngine engine) {
      IASTAppendable above = F.ListAlloc(times.size());
      IASTAppendable below = F.ListAlloc(times.size());
      boolean negative = false;
      for (IExpr factor : times) {
        // a quotient inside the factor, G/c^4 in 8 Pi G/c^4, belongs to the same group
        IAST factors = factor.isTimes() ? flattenTimes((IAST) factor) : F.list(factor);
        IASTAppendable groupAbove = F.ListAlloc(factors.size());
        IASTAppendable groupBelow = F.ListAlloc(factors.size());
        for (IExpr part : factors) {
          if (part.isMinusOne()) {
            negative = !negative;
          } else if ((part.isInteger() || part.isReal()) && part.isNegative()) {
            // -2 a/b is -((2 a)/b): the sign goes in front of the fraction
            negative = !negative;
            groupAbove.append(((INumber) part).negate());
          } else if (part.isPower() && part.exponent().isNumber()
              && part.exponent().isNegative()) {
            IExpr positive = ((INumber) part.exponent()).negate();
            IExpr reciprocal = positive.isOne() ? part.base() : F.Power(part.base(), positive);
            groupBelow.appendArgs(reciprocal.isTimes() ? (IAST) reciprocal : F.list(reciprocal));
          } else if (part.isRational() && !part.isInteger()) {
            IRational rational = (IRational) part;
            if (rational.isNegative()) {
              negative = !negative;
              rational = rational.negate();
            }
            if (!rational.numerator().isOne()) {
              groupAbove.append(rational.numerator());
            }
            groupBelow.append(rational.denominator());
          } else if (!part.isOne()) {
            groupAbove.append(part);
          }
        }
        if (groupAbove.argSize() > 0) {
          above.append(groupAbove);
        }
        if (groupBelow.argSize() > 0) {
          below.append(groupBelow);
        }
      }
      if (below.isEmpty()) {
        return F.NIL;
      }
      IExpr fraction = F.FractionBox(groupsBox(above, form, engine), groupsBox(below, form, engine));
      return negative ? F.RowBox(F.list(F.$str("-"), fraction)) : fraction;
    }

    /** The factors of a held product with the products nested in it spliced in. */
    private static IAST flattenTimes(IAST times) {
      IASTAppendable factors = F.ListAlloc(times.size());
      for (IExpr factor : times) {
        if (factor.isTimes()) {
          factors.appendArgs(flattenTimes((IAST) factor));
        } else {
          factors.append(factor);
        }
      }
      return factors;
    }

    /** The groups of one side of a fraction, side by side; <code>1</code> for no group. */
    private static IExpr groupsBox(IAST groups, IExpr form, EvalEngine engine) {
      if (groups.isEmpty()) {
        return F.$str("1");
      }
      IASTAppendable row = F.ListAlloc(groups.size());
      for (IExpr group : groups) {
        IAST factors = (IAST) group;
        if (factors.argSize() == 1) {
          row.append(standardFormRecursive(factors.arg1(),
              groups.argSize() == 1 ? 0 : Precedence.TIMES, form, engine));
        } else {
          IExpr product = riffle(boxes(factors, Precedence.TIMES, form, engine), " ");
          row.append(groups.argSize() == 1 ? product
              : F.RowBox(F.list(F.$str("("), product, F.$str(")"))));
        }
      }
      return riffle(row, " ");
    }

    /**
     * Whether the product has a negative integer or real coefficient and no factor which stands
     * under the line of a fraction.
     */
    private static boolean isProductWithoutFraction(IAST times) {
      IExpr coefficient = times.arg1();
      if (!(coefficient.isInteger() || coefficient.isReal() && !coefficient.isRational())) {
        return false;
      }
      for (int i = 2; i < times.size(); i++) {
        IExpr factor = times.get(i);
        if (factor.isPower() && factor.exponent().isNumber() && factor.exponent().isNegative()) {
          return false;
        }
      }
      return true;
    }

    /**
     * Each of the expressions as boxes, read at the given precedence. A held product among the
     * factors of a product keeps its parentheses: <code>a (b c)</code>.
     */
    private static IAST boxes(IAST expressions, int precedence, IExpr form, EvalEngine engine) {
      IASTAppendable result = F.ListAlloc(expressions.size());
      for (int i = 1; i < expressions.size(); i++) {
        result.append(standardFormRecursive(expressions.get(i),
            factorPrecedence(expressions.get(i), precedence), form, engine));
      }
      return result;
    }

    /** The precedence a factor is read at: one more than a product for a product. */
    private static int factorPrecedence(IExpr factor, int precedence) {
      return precedence == Precedence.TIMES && factor.isTimes() ? Precedence.TIMES + 1
          : precedence;
    }

    private static IExpr plusBox(IAST plus, int precedence, IExpr form, EvalEngine engine) {
      IASTAppendable row = F.ListAlloc(plus.size() * 2);
      for (int i = 1; i < plus.size(); i++) {
        IExpr term = plus.get(i);
        // the first term carries its own sign: -a b - c d starts with the product of -a and b
        IExpr positive = i > 1 ? withoutMinus(term) : F.NIL;
        boolean negative = positive.isPresent();
        if (negative) {
          row.append(F.$str("-"));
        } else if (i > 1) {
          row.append(F.$str("+"));
        }
        row.append(
            standardFormRecursive(negative ? positive : term, Precedence.PLUS, form, engine));
      }
      return parenthesize(F.RowBox(row), Precedence.PLUS, precedence);
    }

    /**
     * The term without the minus sign a sum should write in front of it, or {@link F#NIL} when it
     * carries none. Structural on purpose: MakeBoxes is HoldAllComplete, so what it is shown is
     * what it must show.
     */
    private static IExpr withoutMinus(IExpr term) {
      if (isNegativeNumber(term)) {
        return ((INumber) term).negate();
      }
      if (term.isTimes()) {
        IAST times = (IAST) term;
        IExpr first = times.arg1();
        // a + (-2 + I) x is written a - (2 - I) x
        if (isNegativeNumber(first) || (first.isComplex() || first.isComplexNumeric())
            && ((INumber) first).re().isNegative()) {
          IExpr positive = ((INumber) first).negate();
          return positive.isOne() //
              ? (times.size() == 3 ? times.arg2() : times.rest())
              : times.setAtCopy(1, positive);
        }
      }
      return F.NIL;
    }

    /** A negative real number, or an imaginary one with a negative imaginary part. */
    private static boolean isNegativeNumber(IExpr expr) {
      if (!expr.isNumber()) {
        return false;
      }
      if (expr.isComplex() || expr.isComplexNumeric()) {
        INumber number = (INumber) expr;
        return number.re().isZero() && number.im().isNegative();
      }
      return expr.isNegative();
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
      if (ast.isAST2() && ast.arg2() != S.StandardForm && ast.arg2() != S.TraditionalForm) {
        // `1` is not a box formatting type.
        return Errors.printMessage(S.ToBoxes, "boxfmt", F.list(ast.arg2()), engine);
      }
      // the arguments are first evaluated in ToBoxes
      IExpr boxes = super.evaluate(ast, engine);
      if (ast.isAST2() && ast.arg2() == S.TraditionalForm && boxes.isPresent()) {
        // the boxes of a form other than StandardForm say which form they are written in
        return F.binaryAST2(S.FormBox, boxes, S.TraditionalForm);
      }
      return boxes;
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
