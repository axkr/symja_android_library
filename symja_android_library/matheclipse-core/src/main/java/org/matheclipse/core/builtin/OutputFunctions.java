package org.matheclipse.core.builtin;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.output.StringBuilderWriter;
import org.hipparchus.linear.FieldMatrix;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.Blank;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.NumberFormatter;
import org.matheclipse.core.form.output.ASCIIPrettyPrinter3;
import org.matheclipse.core.form.output.DoubleFormFactory;
import org.matheclipse.core.form.output.JavaComplexFormFactory;
import org.matheclipse.core.form.output.JavaDoubleFormFactory;
import org.matheclipse.core.form.output.JavaScriptFormFactory;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IGraphExpr;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.HornerScheme;
import com.baeldung.algorithms.romannumerals.RomanArabicConverter;
// import com.ibm.icu.text.NumberFormat;
// import com.ibm.icu.text.RuleBasedNumberFormat;

public final class OutputFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AccountingForm.setEvaluator(new NumberFormEvaluator(NumberFormatter.FormKind.ACCOUNTING));
      S.BaseForm.setEvaluator(new BaseForm());
      S.CForm.setEvaluator(new CForm());
      S.DecimalForm.setEvaluator(new NumberFormEvaluator(NumberFormatter.FormKind.DECIMAL));
      S.EngineeringForm.setEvaluator(new NumberFormEvaluator(NumberFormatter.FormKind.ENGINEERING));
      S.FromRomanNumeral.setEvaluator(new FromRomanNumeral());
      S.FullForm.setEvaluator(new FullForm());
      S.HoldForm.setEvaluator(new HoldForm());
      S.HornerForm.setEvaluator(new HornerForm());
      S.Infix.setEvaluator(new InfixEvaluator());
      S.InputForm.setEvaluator(new InputForm());
      S.JavaForm.setEvaluator(new JavaForm());
      S.JSForm.setEvaluator(new JSForm());
      S.MathMLForm.setEvaluator(new MathMLForm());
      S.NumberForm.setEvaluator(new NumberFormEvaluator(NumberFormatter.FormKind.NUMBER));
      S.OutputForm.setEvaluator(new OutputForm());
      S.PaddedForm.setEvaluator(new NumberFormEvaluator(NumberFormatter.FormKind.PADDED));
      S.Prefix.setEvaluator(new PrefixEvaluator());
      S.Postfix.setEvaluator(new PostfixEvaluator());
      S.RomanNumeral.setEvaluator(new RomanNumeral());
      S.Grid.setEvaluator(new Grid());
      S.Item.setEvaluator(new Item());
      S.Row.setEvaluator(new Row());
      S.TableForm.setEvaluator(new TableForm());
      S.TeXForm.setEvaluator(new TeXForm());
      S.ScientificForm.setEvaluator(new NumberFormEvaluator(NumberFormatter.FormKind.SCIENTIFIC));
    }
  }

  private static class InfixEvaluator extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST() && arg1.argSize() > 1) {
        IAST function = (IAST) arg1;
        if (ast.isAST2()) {
          String infixOperator = ast.arg2().toString();
          StringBuilder buf = new StringBuilder();
          buf.append(function.arg1().toString());
          for (int i = 2; i < function.size(); i++) {
            buf.append(" " + infixOperator + " ");
            buf.append(function.get(i).toString());
          }
          return F.stringx(buf.toString());
        }
        StringBuilder buf = new StringBuilder();
        buf.append(function.arg1().toString());
        for (int i = 2; i < function.size(); i++) {
          buf.append(" ~ ");
          buf.append(function.get(i).toString());
        }
        return F.stringx(buf.toString());
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class PrefixEvaluator extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST1()) {
        if (ast.isAST2()) {
          String postfixOperator = ast.arg2().toString();
          return F.stringx(postfixOperator + " " + arg1.first().toString());
        }
        return F.stringx(arg1.head() + " @ " + arg1.first().toString());
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class OutputForm extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = engine.evaluate(ast.arg1());

        ASCIIPrettyPrinter3 strBuffer = new ASCIIPrettyPrinter3();
        strBuffer.convert(arg1);
        String[] result = strBuffer.toStringBuilder();
        if (result != null && result.length == 3) {
          StringBuilder buf = new StringBuilder();
          if (result[0].trim().length() > 0 || result[2].trim().length() > 0) {
            buf.append(result[0]);
            buf.append("\n");
            buf.append(result[1]);
            buf.append("\n");
            buf.append(result[2]);
          } else {
            buf.append(result[1]);
          }
          return F.stringx(buf.toString(), IStringX.APPLICATION_SYMJA);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {}
  }

  private static class PostfixEvaluator extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST1()) {
        if (ast.isAST2()) {
          String prefixOperator = ast.arg2().toString();
          return F.stringx(arg1.first().toString() + " " + prefixOperator);
        }
        return F.stringx(arg1.first().toString() + " // " + arg1.head());
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class BaseForm extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      IExpr arg2 = engine.evaluate(ast.arg2());
      if (arg1.isInteger() && arg2.isInteger()) {
        int base = arg2.toIntDefault();
        if (base > 0 && base <= 36) {
          BigInteger big = ((IInteger) arg1).toBigNumerator();
          String str = big.toString(base);
          return F.Subscript(F.$str(str), arg2);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class CForm extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // CFormUtilities texUtil = new CFormUtilities(engine, engine.isRelaxedSyntax());
      // IExpr arg1 = engine.evaluate(ast.arg1());
      // StringBuilderWriter stw = new StringBuilderWriter();
      // texUtil.toCForm(arg1, stw);
      // return F.$str(stw.toString());
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.NO_SUPPORT;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * FullForm(expression)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * shows the internal representation of the given <code>expression</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * FullForm shows the difference in the internal expression representation:
   *
   * <pre>
   * &gt;&gt;&gt; FullForm(x(x+1))
   * "x(Plus(1, x))"
   *
   * &gt;&gt;&gt; FullForm(x*(x+1))
   * "Times(x, Plus(1, x))"
   * </pre>
   */
  private static class FullForm extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      String fullForm = engine.evaluate(ast.arg1()).fullFormString();
      return F.stringx(fullForm, IStringX.APPLICATION_SYMJA);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {}
  }

  private static class FromRomanNumeral extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (arg1.isString()) {
        try {
          String romanNumber = arg1.toString();
          int result = RomanArabicConverter.romanToArabic(romanNumber);
          return F.ZZ(result);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.FromRomanNumeral, rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * HoldForm(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * <code>HoldForm</code> doesn't evaluate <code>expr</code> and didn't appear in the output
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; HoldForm(3*2)
   * 3*2
   * </pre>
   */
  private static class HoldForm extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /**
   *
   *
   * <pre>
   * HornerForm(polynomial)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Generate the horner scheme for a univariate <code>polynomial</code>.
   *
   * </blockquote>
   *
   * <pre>
   * HornerForm(polynomial, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Generate the horner scheme for a univariate <code>polynomial</code> in <code>x</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Horner_scheme">Wikipedia - Horner scheme</a>
   * <li><a href="https://rosettacode.org/wiki/">Rosetta Code - Horner's rule for polynomial
   * evaluation</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; HornerForm(3+4*x+5*x^2+33*x^6+x^8)
   * 3+x*(4+x*(5+(33+x^2)*x^4))
   *
   * &gt;&gt; HornerForm(a+b*x+c*x^2,x)
   * a+x*(b+c*x)
   *
   * &gt;&gt; HornerForm((11*x^3-4*x^2+7*x+2)/(x^2-3*x+1))
   * (2+x*(7+x*(-4+11*x)))/(1+(-3+x)*x)
   * </pre>
   */
  private static class HornerForm extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST()) {

        VariablesSet eVar;
        IAST variables;
        if (ast.isAST2()) {
          variables = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
        } else {
          eVar = new VariablesSet(ast.arg1());
          variables = eVar.getVarList();
        }
        if (variables.isPresent()) {
          if (variables.size() >= 2) {
            IExpr variable = variables.arg1();
            IExpr temp = hornerScheme(arg1, variable, engine);
            if (temp.isPresent()) {
              return temp;
            }
            // generate the horner scheme for the numerator and denominator separately
            Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(arg1, false);
            if (parts.isPresent()) {
              IExpr numerator = parts.get()[0];
              IExpr denominator = parts.get()[1];
              if (!denominator.isOne()) {
                IExpr numeratorHorner = hornerScheme(numerator, variable, engine);
                IExpr denominatorHorner = hornerScheme(denominator, variable, engine);
                if (numeratorHorner.isPresent() || denominatorHorner.isPresent()) {
                  return F.Divide(numeratorHorner.orElse(numerator),
                      denominatorHorner.orElse(denominator));
                }
              }
            }
          }
        }
      }
      return arg1;
    }

    /**
     * Generate the horner scheme for the given <code>expr</code> in the given
     * <code>variable</code>.
     *
     * @param expr
     * @param variable
     * @param engine
     * @return {@link F#NIL} if <code>expr</code> isn't a {@link S#Plus} expression
     */
    private static IExpr hornerScheme(IExpr expr, IExpr variable, EvalEngine engine) {
      if (expr.isPlus()) {
        HornerScheme scheme = new HornerScheme();
        return scheme.generate(engine.isNumericMode(), (IAST) expr, variable);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class InputForm extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        // if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        // return F.stringx(StringFunctions.inputForm(arg1, true), IStringX.APPLICATION_SYMJA);
        // }
        return F.stringx(IStringX.inputForm(arg1), IStringX.APPLICATION_SYMJA);
      }
      return F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * JavaForm(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the Symja Java form of the <code>expr</code>. In Java you can use the created Symja
   * expressions.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * JavaForm can add the <code>F.</code> prefix for class <code>
   * org.matheclipse.core.expression.F</code> if you set <code>prefix-&gt;True</code>:
   *
   * <pre>
   * &gt;&gt; JavaForm(D(sin(x)*cos(x),x), prefix-&gt;True)
   * "F.Plus(F.Sqr(F.Cos(F.x)),F.Negate(F.Sqr(F.Sin(F.x))))"
   *
   * &gt;&gt; JavaForm(I/2*E^((-I)*x)-I/2*E^(I*x))
   * "Plus(Times(CC(0L,1L,1L,2L),Power(E,Times(CNI,x))),Times(CC(0L,1L,-1L,2L),Power(E,Times(CI,x))))"
   * </pre>
   *
   * <p>
   * JavaForm evaluates its argument before creating the Java form:
   *
   * <pre>
   * &gt;&gt; JavaForm(D(sin(x)*cos(x),x))
   * "Plus(Sqr(Cos(x)),Negate(Sqr(Sin(x))))"
   * </pre>
   *
   * <p>
   * You can use <code>Hold</code> to suppress the evaluation:
   *
   * <pre>
   * &gt;&gt; JavaForm(Hold(D(sin(x)*cos(x),x)))
   * "D(Times(Sin(x),Cos(x)),x)"
   *
   * &gt;&gt; JavaForm(Hold(D(sin(x)*cos(x),x)), prefix-&gt;True)
   * "F.D(F.Times(F.Sin(F.x),F.Cos(F.x)),F.x)"
   * </pre>
   */
  private static class JavaForm extends AbstractCoreFunctionEvaluator {

    public static CharSequence javaForm(IExpr arg1, boolean strictJava, boolean usePrefix) {
      SourceCodeProperties p = SourceCodeProperties.of(strictJava, false,
          usePrefix ? SourceCodeProperties.Prefix.CLASS_NAME : SourceCodeProperties.Prefix.NONE,
          false);
      return arg1.internalJavaString(p, 0, x -> null);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        final IExpr arg1 = engine.evaluate(ast.arg1());
        boolean floatJava = false;
        boolean complexJava = false;
        boolean strictJava = false;
        boolean usePrefix = false;
        if (ast.isAST2()) {
          IExpr arg2 = engine.evaluate(ast.arg2());
          if (arg2 == S.Float || arg2 == S.Real) {
            floatJava = true;
          } else if (arg2 == S.Complex) {
            complexJava = true;
          } else if (arg2 == S.Strict) {
            strictJava = true;
          } else if (arg2 == S.Prefix) {
            usePrefix = true;
          } else {
            final OptionArgs options = new OptionArgs(ast.topHead(), arg2, engine);
            floatJava = options.isTrue(S.Float);
            strictJava = options.isTrue(S.Strict);
            usePrefix = options.isTrue(S.Prefix);
          }
        }
        if (floatJava) {
          IExpr optimized = S.OptimizeExpression.funEval(engine, arg1);
          if (optimized.isList2() && optimized.second().isListOfRules()
              && !optimized.second().isEmptyList()) {
            IExpr newExpr = optimized.first();
            IAST listOfRules = (IAST) optimized.second();
            VariablesSet varSet = new VariablesSet(arg1);
            List<IExpr> functionsParameters = varSet.getArrayList();
            StringBuilder buf = new StringBuilder();
            long functionCounter = EvalEngine.incModuleCounter();
            buf.append("double f");
            buf.append(functionCounter);
            buf.append("(");
            for (int i = 0; i < functionsParameters.size(); i++) {
              buf.append("double ");
              buf.append(functionsParameters.get(i));
              if (i < functionsParameters.size() - 1) {
                buf.append(", ");
              }
            }
            buf.append(") {\n");
            for (int i = 1; i < listOfRules.size(); i++) {
              IAST rule = (IAST) listOfRules.get(i);
              buf.append("double ");
              buf.append(toJavaDouble(rule.first()));
              buf.append(" = ");
              buf.append(toJavaDouble(rule.second()));
              buf.append(";\n");
            }
            buf.append("return ");
            buf.append(toJavaDouble(newExpr));
            buf.append(";\n");
            buf.append("}\n");
            return F.$str(buf.toString(), IStringX.APPLICATION_JAVA);
          }

          return F.$str(toJavaDouble(arg1), IStringX.APPLICATION_JAVA);
        } else if (complexJava) {
          IExpr optimized = S.OptimizeExpression.of(engine, arg1);
          if (optimized.isList2() && optimized.second().isListOfRules()
              && !optimized.second().isEmptyList()) {
            IExpr newExpr = optimized.first();
            IAST listOfRules = (IAST) optimized.second();
            VariablesSet varSet = new VariablesSet(arg1);
            List<IExpr> functionsParameters = varSet.getArrayList();
            StringBuilder buf = new StringBuilder();
            long functionCounter = EvalEngine.incModuleCounter();
            buf.append("Complex f");
            buf.append(functionCounter);
            buf.append("(");
            for (int i = 0; i < functionsParameters.size(); i++) {
              buf.append("Complex ");
              buf.append(functionsParameters.get(i));
              if (i < functionsParameters.size() - 1) {
                buf.append(", ");
              }
            }
            buf.append(") {\n");
            for (int i = 1; i < listOfRules.size(); i++) {
              IAST rule = (IAST) listOfRules.get(i);
              buf.append("Complex ");
              buf.append(toJavaComplex(rule.first()));
              buf.append(" = ");
              buf.append(toJavaComplex(rule.second()));
              buf.append(";\n");
            }
            buf.append("return ");
            buf.append(toJavaComplex(newExpr));
            buf.append(";\n");
            buf.append("}\n");
            return F.$str(buf.toString(), IStringX.APPLICATION_JAVA);
          }

          return F.$str(toJavaComplex(arg1), IStringX.APPLICATION_JAVA);
        }
        String resultStr = javaForm(arg1, strictJava, usePrefix).toString();
        return F.$str(resultStr, IStringX.APPLICATION_JAVA);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.JavaForm, rex, engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  private static class JSForm extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        int javascriptFlavor = JavaScriptFormFactory.USE_PURE_JS;
        if (ast.isAST2() && ast.arg2().isStringIgnoreCase("mathcell")) {
          javascriptFlavor = JavaScriptFormFactory.USE_MATHCELL;
        }
        IExpr arg1 = ast.arg1();
        if (arg1.isFunctionID(ID.Plot, ID.ParametricPlot, ID.ParametricPlot)) {
          IASTAppendable temp = ((IAST) arg1).appendClone(F.Rule(S.JSForm, S.True));
          arg1 = temp;
        }
        arg1 = engine.evaluate(arg1);

        if (arg1.isAST(S.JSFormData, 3)) {
          String manipulateStr = ((IAST) arg1).arg1().toString();
          return F.$str(manipulateStr, IStringX.APPLICATION_JAVASCRIPT);
        }
        if (arg1.isDataset()) {
          return F.$str(((IASTDataset) arg1).datasetToJSForm(), IStringX.TEXT_HTML);
        }
        if (arg1 instanceof IGraphExpr) {
          return F.$str(((IGraphExpr) arg1).graphToJSForm(), IStringX.APPLICATION_JAVASCRIPT);
        }

        return F.$str(toJavaScript(arg1, javascriptFlavor), IStringX.APPLICATION_JAVASCRIPT);
      } catch (IOException ioex) {
        return Errors.printMessage(S.JSForm, ioex, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.JSForm, rex, engine);
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * MathMLForm(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the MathMLForm form of the evaluated <code>expr</code>.
   *
   * </blockquote>
   */
  private static class MathMLForm extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      MathMLUtilities mathMLUtil = new MathMLUtilities(engine, false, engine.isRelaxedSyntax());
      IExpr arg1 = ast.arg1();
      StringBuilderWriter stw = new StringBuilderWriter();
      mathMLUtil.toMathML(arg1, stw);
      return F.stringx(stw.toString(), IStringX.TEXT_MATHML);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  private static class RomanNumeral extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (arg1.isInteger()) {
        try {
          int value = arg1.toIntDefault();
          if (value < RomanArabicConverter.MIN_VALUE || value > RomanArabicConverter.MAX_VALUE) {
            // Integer expected in range `1` to `2`.
            return Errors.printMessage( //
                ast.topHead(), //
                "intrange", //
                F.List(F.ZZ(RomanArabicConverter.MIN_VALUE), //
                    F.ZZ(RomanArabicConverter.MAX_VALUE)), //
                engine);
          }
          String result = RomanArabicConverter.arabicToRoman(value);
          return F.stringx(result);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.RomanNumeral, rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>Row[{e1, e2, ...}]</code> - the elements side by side - and
   * <code>Row[{...}, separator]</code>.
   *
   * <p>
   * With a string separator the row is joined into one string here, because that is a value rather
   * than a layout. Every other form is a layout object and stays unevaluated for a front end to
   * lay out; <code>Row[{"moves: ", Dynamic[moves]}]</code>, the usual way to write a live read-out,
   * is the commonest of them.
   */
  private static class Row extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        return F.NIL;
      }
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isList() && arg2.isString()) {
        IAST list = (IAST) arg1;
        String separator = arg2.toString();
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i < list.size(); i++) {
          buf.append(list.getRule(i).toString());
          if (i < list.argSize()) {
            buf.append(separator);
          }
        }
        return F.stringx(buf);
      }

      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>Grid({{e11, e12, ...}, ...})</code> arranges expressions in a table.
   *
   * <p>
   * A display wrapper: <code>evaluate()</code> returns {@link F#NIL} so the expression survives
   * evaluation, and only the printed form changes. <code>OutputForm</code> deliberately keeps
   * printing the call itself, because a grid of expressions has no faithful one line text form;
   * the table is built by the MathML and TeX factories, which is what the web front end and
   * <code>TeXForm</code> show.
   *
   * <p>
   * Rows may be of different lengths, a cell may span with <code>SpanFromLeft</code>,
   * <code>SpanFromAbove</code> or <code>SpanFromBoth</code>, and <code>Item(expr, opts)</code>
   * gives one cell settings of its own.
   */
  private static class Grid extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // the wrapper must survive evaluation - it is resolved by the output factories
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // {{rows}}, and any number of option rules
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.List(F.Rule(S.Alignment, F.List(S.Center, S.Automatic)), //
              F.Rule(S.AllowedDimensions, S.Automatic), //
              F.Rule(S.Background, S.None), //
              F.Rule(S.BaselinePosition, S.Automatic), //
              F.Rule(S.BaseStyle, F.CEmptyList), //
              F.Rule(S.Dividers, F.CEmptyList), //
              F.Rule(S.Frame, S.None), //
              F.Rule(S.FrameStyle, S.Automatic), //
              F.Rule(S.ItemSize, S.Automatic), //
              F.Rule(S.ItemStyle, S.None), //
              F.Rule(S.Spacings, S.Automatic)));
    }
  }

  /**
   * <code>Item(expr, opts)</code> gives one cell of a <code>Grid</code> settings of its own.
   *
   * <p>
   * It means nothing on its own - Wolfram says as much - so it stays unevaluated and is read by
   * whatever lays the cell out. Outside a construct that supports it, it simply prints as itself.
   */
  private static class Item extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // read by org.matheclipse.core.graphics.svg.LayoutSpec, which needs the wrapper intact
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.List(F.Rule(S.Alignment, S.Automatic), //
              F.Rule(S.Background, S.None), //
              F.Rule(S.BaseStyle, F.CEmptyList), //
              F.Rule(S.Frame, S.False), //
              F.Rule(S.FrameStyle, S.Automatic), //
              F.Rule(S.ItemSize, S.Automatic)));
    }
  }

  /**
   * <code>TableForm(expr)</code> is a display wrapper: like <code>MatrixForm</code> it stays in the
   * expression tree and only the printed representation changes. That is why
   * <code>evaluate()</code> always returns {@link F#NIL} - the text table is laid out by
   * {@link org.matheclipse.core.form.output.OutputFormFactory} and the TeX and MathML factories
   * render it as an array.
   */
  private static class TableForm extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {
      // the wrapper must survive evaluation - it is resolved by the output factories
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      IBuiltInSymbol[] lhsOptionSymbols = new IBuiltInSymbol[] {S.TableAlignments, S.TableDepth,
          S.TableDirections, S.TableHeadings, S.TableSpacing};
      IExpr[] rhsValues = new IExpr[] {S.Automatic, F.CInfinity, S.Column, S.None, S.Automatic};
      setOptions(newSymbol, lhsOptionSymbols, rhsValues);
    }
  }

  /**
   *
   *
   * <pre>
   * TeXForm(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the TeX form of the evaluated <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt;&gt; TeXForm(D(sin(x)*cos(x),x))
   * "{\cos(x)}^{2}-{\sin(x)}^{2}"
   * </pre>
   */
  private static class TeXForm extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      TeXUtilities texUtil = new TeXUtilities(engine, engine.isRelaxedSyntax());
      IExpr arg1 = engine.evaluate(ast.arg1());
      StringBuilderWriter stw = new StringBuilderWriter();
      texUtil.toTeX(arg1, stw);
      return F.$str(stw.toString(), IStringX.TEXT_LATEX);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /**
   * The <code>ScientificForm, EngineeringForm, NumberForm, AccountingForm, PaddedForm,
   * DecimalForm</code> display wrappers.
   *
   * <p>
   * These heads stay in the expression tree unevaluated - <code>evaluate()</code> only validates
   * the precision specification and always returns {@link F#NIL}. The actual formatting happens in
   * the output factories, which recognize the head and install a
   * {@link org.matheclipse.core.form.NumberFormatter} for the wrapped subtree. That is why
   * <code>FullForm(ScientificForm(2.5))</code> still shows <code>ScientificForm(2.5)</code>.
   */
  private static class NumberFormEvaluator extends AbstractFunctionOptionEvaluator {

    private final NumberFormatter.FormKind kind;

    /** Index of {@link S#DigitBlock} in this wrapper's option array. */
    private int digitBlockIndex = -1;

    private NumberFormEvaluator(NumberFormatter.FormKind kind) {
      this.kind = kind;
    }

    /**
     * The option symbols this wrapper accepts. The forms differ: only
     * <code>NumberForm, PaddedForm, DecimalForm</code> take <code>DefaultPrintPrecision</code>,
     * only <code>PaddedForm</code> takes <code>ScientificNotationThreshold</code>, and
     * <code>DecimalForm</code> takes none of the exponent related options because it never uses
     * scientific notation.
     */
    private IBuiltInSymbol[] optionSymbolsFor(NumberFormatter.FormKind kind) {
      switch (kind) {
        case DECIMAL:
          return new IBuiltInSymbol[] {S.DefaultPrintPrecision, S.DigitBlock, S.NumberPadding,
              S.NumberPoint, S.NumberSeparator, S.NumberSigns, S.SignPadding};
        case NUMBER:
          return new IBuiltInSymbol[] {S.DefaultPrintPrecision, S.DigitBlock, S.ExponentFunction,
              S.ExponentStep, S.NumberFormat, S.NumberMultiplier, S.NumberPadding, S.NumberPoint,
              S.NumberSeparator, S.NumberSigns, S.SignPadding};
        case PADDED:
          return new IBuiltInSymbol[] {S.DefaultPrintPrecision, S.DigitBlock, S.ExponentFunction,
              S.ExponentStep, S.NumberFormat, S.NumberMultiplier, S.NumberPadding, S.NumberPoint,
              S.NumberSeparator, S.NumberSigns, S.ScientificNotationThreshold, S.SignPadding};
        default:
          return new IBuiltInSymbol[] {S.DigitBlock, S.ExponentFunction, S.ExponentStep,
              S.NumberFormat, S.NumberMultiplier, S.NumberPadding, S.NumberPoint, S.NumberSeparator,
              S.NumberSigns, S.SignPadding};
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {
      if (argSize >= 2) {
        IExpr arg2 = ast.arg2();
        if (arg2.isRule()) {
          // a rule that survived option stripping names an option this form does not accept
          // Unknown option `1` in `2`.
          return Errors.printMessage(ast.topHead(), "optx", F.list(arg2, ast.topHead()), engine);
        }
        if (arg2.isList()) {
          if (arg2.size() != 3 || !isPositiveOrZero(arg2.first()) || !isPositiveOrZero(arg2.second())) {
            // Positive machine-sized integer expected at position `2` in `1`.
            return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
          }
        } else if (!isPositive(arg2)) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
        }
      }
      IExpr digitBlock = digitBlockIndex >= 0 && digitBlockIndex < option.length
          ? option[digitBlockIndex]
          : null;
      if (digitBlock != null && digitBlock.isPresent() && !digitBlock.isInfinity()
          && !isPositive(digitBlock) && !isDigitBlockPair(digitBlock)) {
        // Value of option `1` should be a non-negative integer or Infinity.
        return Errors.printMessage(ast.topHead(), "iopnf", F.list(F.Rule(S.DigitBlock, digitBlock)),
            engine);
      }
      // the wrapper must survive evaluation - it is resolved by the output factories
      return F.NIL;
    }

    private static boolean isPositive(IExpr expr) {
      int value = expr.toIntDefault();
      return value != Integer.MIN_VALUE && value > 0;
    }

    private static boolean isPositiveOrZero(IExpr expr) {
      int value = expr.toIntDefault();
      return value != Integer.MIN_VALUE && value >= 0;
    }

    private static boolean isDigitBlockPair(IExpr expr) {
      return expr.isList() && expr.size() == 3
          && (expr.first().isInfinity() || isPositive(expr.first()))
          && (expr.second().isInfinity() || isPositive(expr.second()));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      IBuiltInSymbol[] lhsOptionSymbols = optionSymbolsFor(kind);
      IExpr[] rhsValues = new IExpr[lhsOptionSymbols.length];
      for (int i = 0; i < lhsOptionSymbols.length; i++) {
        rhsValues[i] = defaultValue(lhsOptionSymbols[i]);
        if (lhsOptionSymbols[i] == S.DigitBlock) {
          digitBlockIndex = i;
        }
      }
      setOptions(newSymbol, lhsOptionSymbols, rhsValues);
    }

    private IExpr defaultValue(IBuiltInSymbol optionSymbol) {
      switch (optionSymbol.ordinal()) {
        case ID.DigitBlock:
          return F.CInfinity;
        case ID.ExponentStep:
          return F.ZZ(kind == NumberFormatter.FormKind.ENGINEERING ? 3 : 1);
        case ID.NumberMultiplier:
          return F.stringx("×");
        case ID.NumberPadding:
          return numberPaddingDefault();
        case ID.NumberPoint:
          return kind == NumberFormatter.FormKind.DECIMAL ? S.Automatic : F.stringx(".");
        case ID.NumberSeparator:
          return F.list(F.stringx(","), F.stringx(""));
        case ID.NumberSigns:
          return numberSignsDefault();
        case ID.ScientificNotationThreshold:
          return F.list(F.ZZ(-5), F.ZZ(6));
        case ID.SignPadding:
          return S.False;
        default:
          // DefaultPrintPrecision, ExponentFunction, NumberFormat
          return S.Automatic;
      }
    }

    private IExpr numberPaddingDefault() {
      switch (kind) {
        case PADDED:
          return F.list(F.stringx(" "), F.stringx("0"));
        case NUMBER:
        case DECIMAL:
          return F.list(F.stringx(""), F.stringx("0"));
        default:
          return F.list(F.stringx(""), F.stringx(""));
      }
    }

    private IExpr numberSignsDefault() {
      if (kind == NumberFormatter.FormKind.ACCOUNTING) {
        return F.list(F.list(F.stringx("("), F.stringx(")")), F.stringx(""));
      }
      return F.list(F.stringx("-"), F.stringx(""));
    }
  }

  /**
   * Get an array with 2 elements returning the declared variables in the first entry and the
   * corresponding types <code>Real, Integer,...</code> for the variable names in the second entry.
   *
   * @param ast the original definition <code>
   * CompilePrint({variable/types}, function)</code>
   * @param engine the evaluation engine
   * @return <code>null</code> if the variable declaration isn't correct
   */
  @Deprecated
  public static IAST[] checkIsVariableOrVariableList(IAST ast, EvalEngine engine) {
    IASTMutable[] result = new IASTMutable[2];
    IExpr arg1 = ast.arg1();
    if (arg1.isList()) {
      IAST list = (IAST) arg1;
      result[0] = list.copy();
      result[1] = F.constantArray(S.Real, list.argSize());
      for (int i = 1; i < list.size(); i++) {
        if (!checkVariable(list.get(i), i, result[0], result[1], engine)) {
          // `1` is not a valid variable.
          Errors.printMessage(ast.topHead(), "ivar", F.list(list.get(i)), engine);
          return null;
        }
      }
    } else {
      result[0] = F.unaryAST1(S.List, arg1);
      result[1] = F.unaryAST1(S.List, S.Real);
      if (!checkVariable(arg1, 1, result[0], result[1], engine)) {
        // `1` is not a valid variable.
        Errors.printMessage(ast.topHead(), "ivar", F.list(arg1), engine);
        return null;
      }
    }
    return result;
  }

  /**
   * @param arg the input argument for the current <code>variablesIndex</code>
   * @param variablesIndex
   * @param variables set the variable at the current <code>variablesIndex</code>
   * @param types set the corresponding type <code>Real, Integer,...</code> for variable at the
   *        current <code>variablesIndex</code>
   * @param engine
   * @return <code>true</code> if the variables and types
   */
  @Deprecated
  private static boolean checkVariable(IExpr arg, int variablesIndex, IASTMutable variables,
      IASTMutable types, EvalEngine engine) {
    IExpr sym = arg;
    IExpr headTest = S.Real;
    if (arg.isList1() || arg.isList2()) {
      sym = arg.first();
      if (arg.isList2()) {
        headTest = null;
        if (arg.second().isBlank()) {
          Blank blank = (Blank) arg.second();
          headTest = blank.getHeadTest();
          if (headTest == null) {
            return false;
          }
          if (headTest == S.Integer || headTest == S.Complex || headTest == S.Real) {
            // allowed machine-sized types
          } else {
            headTest = null;
          }
        }
        if (headTest == null) {
          return false;
        }
      }
    }

    variables.set(variablesIndex, sym);
    types.set(variablesIndex, headTest);
    return true;
  }

  public static void initialize() {
    Initializer.init();
  }

  private static boolean markdownTable(StringBuilder result, IExpr expr,
      java.util.function.Function<IExpr, String> function, boolean fillUpWithSPACE) {
    int[] dim = expr.isMatrix();
    if (dim != null && dim[0] > 0 && dim[1] > 0) {
      IAST matrix = (IAST) expr;
      int rowDimension = dim[0];
      int columnDimension = dim[1];
      // int[] columnSizes = new int[columnDimension];
      String[][] texts = new String[rowDimension][columnDimension];
      for (int i = 0; i < rowDimension; i++) {
        for (int j = 0; j < columnDimension; j++) {
          final String str = function.apply(matrix.getPart(i + 1, j + 1));
          texts[i][j] = str;
          // if (str.length() > columnSizes[j]) {
          // columnSizes[j] = str.length();
          // }
        }
      }

      StringBuilder[] sb = new StringBuilder[rowDimension];
      for (int j = 0; j < rowDimension; j++) {
        sb[j] = new StringBuilder();
      }
      int rowLength = 0;

      for (int i = 0; i < columnDimension; i++) {
        int columnLength = 0;
        for (int j = 0; j < rowDimension; j++) {
          String str = texts[j][i];
          if (str.length() > columnLength) {
            columnLength = str.length();
          }
          sb[j].append('|');
          sb[j].append(str);
        }
        if (i < columnDimension - 1) {
          rowLength += columnLength + 1;
        } else {
          rowLength += columnLength;
        }
        if (fillUpWithSPACE) {
          for (int j = 0; j < rowDimension; j++) {
            int rest = rowLength - sb[j].length();
            for (int k = 0; k < rest; k++) {
              sb[j].append(' ');
            }
          }
        }
      }

      for (int i = 0; i < rowDimension; i++) {
        result.append(sb[i]);
        result.append("|");
        if (i < rowDimension - 1) {
          result.append("\n");
        }
      }
      return true;
    }
    return false;
  }


  /**
   * @param result
   * @param expr
   * @param delimiter
   * @param function
   * @param fillUpWithSPACE
   * @return
   * @deprecated use freva/ascii-table now - https://github.com/freva/ascii-table
   */
  @Deprecated
  public static boolean plaintextTable(StringBuilder result, IExpr expr, String delimiter,
      java.util.function.Function<IExpr, String> function, boolean fillUpWithSPACE) {
    int[] dim = expr.isMatrix();
    if (dim != null && dim[0] > 0 && dim[1] > 0) {
      int rowDimension = dim[0];
      int columnDimension = dim[1];
      StringBuilder[] sb = new StringBuilder[rowDimension];
      for (int j = 0; j < rowDimension; j++) {
        sb[j] = new StringBuilder();
      }
      if (expr.isAST()) {
        IAST matrix = (IAST) expr;
        int rowLength = 0;
        for (int i = 0; i < columnDimension; i++) {
          int columnLength = 0;
          for (int j = 0; j < rowDimension; j++) {
            String str = function.apply(matrix.getPart(j + 1, i + 1));
            if (str.length() > columnLength) {
              columnLength = str.length();
            }
            sb[j].append(str);
            if (i < columnDimension - 1) {
              sb[j].append(delimiter);
            }
          }
          if (i < columnDimension - 1) {
            rowLength += columnLength + 1;
          } else {
            rowLength += columnLength;
          }
          if (fillUpWithSPACE) {
            for (int j = 0; j < rowDimension; j++) {
              int rest = rowLength - sb[j].length();
              for (int k = 0; k < rest; k++) {
                sb[j].append(' ');
              }
            }
          }
        }
      } else {
        FieldMatrix<IExpr> matrix = Convert.list2Matrix(expr);
        int rowLength = 0;
        if (matrix == null) {
          return false;
        } else {
          for (int i = 0; i < columnDimension; i++) {
            int columnLength = 0;
            for (int j = 0; j < rowDimension; j++) {
              IExpr arg = matrix.getEntry(j, i);
              String str = function.apply(arg);
              if (str.length() > columnLength) {
                columnLength = str.length();
              }
              sb[j].append(str);
              if (i < columnDimension - 1) {
                sb[j].append(delimiter);
              }
            }
            if (i < columnDimension - 1) {
              rowLength += columnLength + 1;
            } else {
              rowLength += columnLength;
            }
            if (fillUpWithSPACE) {
              for (int j = 0; j < rowDimension; j++) {
                int rest = rowLength - sb[j].length();
                for (int k = 0; k < rest; k++) {
                  sb[j].append(' ');
                }
              }
            }
          }
        }
      }

      for (int i = 0; i < rowDimension; i++) {
        result.append(sb[i]);
        if (i < rowDimension - 1) {
          result.append("\n");
        }
      }
      return true;
    }
    return false;
  }

  public static String toJavaDouble(final IExpr arg1) {
    DoubleFormFactory factory = JavaDoubleFormFactory.get(true, false);
    StringBuilder buf = new StringBuilder();
    factory.convert(buf, arg1);
    return buf.toString();
  }

  public static String toJavaComplex(final IExpr arg1) {
    JavaComplexFormFactory factory = JavaComplexFormFactory.get(true, false);
    StringBuilder buf = new StringBuilder();
    factory.convert(buf, arg1);
    return buf.toString();
  }

  public static String toJavaScript(final IExpr arg1, int javascriptFlavor) {
    DoubleFormFactory factory = new JavaScriptFormFactory(true, false, -1, -1, javascriptFlavor);
    StringBuilder buf = new StringBuilder();
    factory.convert(buf, arg1);
    return buf.toString();
  }

  private OutputFunctions() {}
}
