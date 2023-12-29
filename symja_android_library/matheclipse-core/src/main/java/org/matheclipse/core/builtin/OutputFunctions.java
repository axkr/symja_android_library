package org.matheclipse.core.builtin;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.hipparchus.linear.FieldMatrix;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.VariablesSet;
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
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.form.output.DoubleFormFactory;
import org.matheclipse.core.form.output.JavaComplexFormFactory;
import org.matheclipse.core.form.output.JavaDoubleFormFactory;
import org.matheclipse.core.form.output.JavaScriptFormFactory;
import org.matheclipse.core.interfaces.IAST;
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
import com.github.freva.asciitable.AsciiTable;
// import com.ibm.icu.text.NumberFormat;
// import com.ibm.icu.text.RuleBasedNumberFormat;

public final class OutputFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.BaseForm.setEvaluator(new BaseForm());
      S.CForm.setEvaluator(new CForm());
      S.FromRomanNumeral.setEvaluator(new FromRomanNumeral());
      S.FullForm.setEvaluator(new FullForm());
      S.HoldForm.setEvaluator(new HoldForm());
      S.HornerForm.setEvaluator(new HornerForm());
      S.Infix.setEvaluator(new InfixEvaluator());
      S.InputForm.setEvaluator(new InputForm());
      S.JavaForm.setEvaluator(new JavaForm());
      S.JSForm.setEvaluator(new JSForm());
      S.MathMLForm.setEvaluator(new MathMLForm());
      S.Prefix.setEvaluator(new PrefixEvaluator());
      S.Postfix.setEvaluator(new PostfixEvaluator());
      S.RomanNumeral.setEvaluator(new RomanNumeral());
      S.Row.setEvaluator(new Row());
      S.TableForm.setEvaluator(new TableForm());
      S.TeXForm.setEvaluator(new TeXForm());
      S.TreeForm.setEvaluator(new TreeForm());
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class CForm extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // CFormUtilities texUtil = new CFormUtilities(engine, engine.isRelaxedSyntax());
      // IExpr arg1 = engine.evaluate(ast.arg1());
      // StringWriter stw = new StringWriter();
      // texUtil.toCForm(arg1, stw);
      // return F.$str(stw.toString());
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
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
          return Errors.printMessage(S.FromRomanNumeral, rex, engine);
        }
      }
      return F.NIL;
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
      newSymbol.setAttributes(ISymbol.HOLDALL);
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
   * </pre>
   */
  private static class HornerForm extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST()) {

        IAST poly = (IAST) arg1;
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
            if (poly.isPlus()) {
              HornerScheme scheme = new HornerScheme();
              return scheme.generate(engine.isNumericMode(), poly, variables.arg1());
            }
          }
        }
      }
      return arg1;
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
        return F.stringx(StringFunctions.inputForm(arg1), IStringX.APPLICATION_SYMJA);
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
        IExpr arg1 = engine.evaluate(ast.arg1());
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
          IExpr optimized = S.OptimizeExpression.of(engine, arg1);
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
        return Errors.printMessage(S.JavaForm, rex, engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
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
          IASTAppendable temp = ((IAST) arg1).copyAppendable();
          temp.append(F.Rule(S.JSForm, S.True));
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
        if (arg1 instanceof GraphExpr) {
          return F.$str(((GraphExpr) arg1).graphToJSForm(), IStringX.APPLICATION_JAVASCRIPT);
        }

        return F.$str(toJavaScript(arg1, javascriptFlavor), IStringX.APPLICATION_JAVASCRIPT);
      } catch (IOException ioex) {
        return Errors.printMessage(S.JSForm, ioex, engine);
      } catch (RuntimeException rex) {
        return Errors.printMessage(S.JSForm, rex, engine);
      }
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
      StringWriter stw = new StringWriter();
      mathMLUtil.toMathML(arg1, stw);
      return F.stringx(stw.toString(), IStringX.TEXT_MATHML);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
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
          return Errors.printMessage(S.RomanNumeral, rex, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class Row extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isList() && arg2.isString()) {
        IAST list = (IAST) arg1;
        String separator = arg2.toString();
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i < list.size(); i++) {
          buf.append(list.getRule(i).toString());
          if (i < list.size() - 1) {
            buf.append(separator);
          }
        }
        return F.stringx(buf);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class TableForm extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {
      if (argSize >= 1) {
        // IExpr tableHeadings = option[0];
        IExpr arg1 = ast.arg1().normal(false);

        final String[][] tableData;
        if (arg1.isList()) {
          IAST list = (IAST) arg1;
          tableData = new String[list.argSize()][];
          for (int i = 1; i < list.size(); i++) {
            IExpr element = list.get(i);
            if (element.isList()) {
              IAST subList = (IAST) element;
              tableData[i - 1] = new String[subList.argSize()];
              for (int j = 1; j < subList.size(); j++) {
                IExpr subElement = subList.get(j);
                tableData[i - 1][j - 1] = subElement.toString();
              }
            } else {
              tableData[i - 1] = new String[1];
              tableData[i - 1][0] = element.toString();;
            }
          }

        } else {
          tableData = new String[1][1];
          tableData[0][0] = arg1.toString();
        }

        String[] headers = {};
        String[] footers = {};
        String asciiTable = AsciiTable.builder()//
            .lineSeparator("\n")//
            .border(AsciiTable.NO_BORDERS) //
            .header(headers) //
            .footer(footers) //
            .data(tableData)//
            .asString();
        return F.stringx(asciiTable, IStringX.TEXT_PLAIN);


        // StringBuilder tableForm = new StringBuilder();
        // if (plaintextTable(tableForm, arg1, " ", x -> x.toString(), true)) {
        // return F.stringx(tableForm.toString(), IStringX.TEXT_PLAIN);
        // }
        // if (arg1.isList()) {
        // IAST list = (IAST) arg1;
        // StringBuilder sb = new StringBuilder();
        // for (int i = 1; i < list.size(); i++) {
        // sb.append(list.get(i).toString());
        // sb.append("\n");
        // }
        // return F.stringx(sb.toString(), IStringX.TEXT_PLAIN);
        // }
        // int dim = arg1.isVector();
        // if (dim >= 0) {
        // FieldVector<IExpr> vector = Convert.list2Vector(arg1);
        // if (vector != null) {
        // StringBuilder sb = new StringBuilder();
        // for (int i = 0; i < dim; i++) {
        // sb.append(vector.getEntry(i).toString());
        // sb.append("\n");
        // }
        // return F.stringx(sb.toString(), IStringX.TEXT_PLAIN);
        // }
        // }
        // return F.stringx(arg1.toString(), IStringX.TEXT_PLAIN);
      }
      return F.NIL;
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
      StringWriter stw = new StringWriter();
      texUtil.toTeX(arg1, stw);
      return F.$str(stw.toString(), IStringX.TEXT_LATEX);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class TreeForm extends AbstractCoreFunctionEvaluator {
    private static void edgesToVisjs(StringBuilder buf,
        List<SimpleImmutableEntry<Integer, Integer>> edgeSet) {
      boolean first = true;

      buf.append("var edges = new vis.DataSet([\n");
      for (SimpleImmutableEntry<Integer, Integer> edge : edgeSet) {
        // {from: 1, to: 3},
        if (first) {
          buf.append("  {from: ");
        } else {
          buf.append(", {from: ");
        }
        buf.append(edge.getKey());
        buf.append(", to: ");
        buf.append(edge.getValue());
        // , arrows: { to: { enabled: true, type: 'arrow'}}
        buf.append(" , arrows: { to: { enabled: true, type: 'arrow'}}");
        buf.append("}\n");
        first = false;
      }
      buf.append("]);\n");
    }

    private static void treeToGraph(IAST tree, final int level, final int maxLevel,
        int[] currentCount, List<SimpleImmutableEntry<String, Integer>> vertexList,
        List<SimpleImmutableEntry<Integer, Integer>> edgeList) {
      vertexList.add(new SimpleImmutableEntry<String, Integer>(tree.head().toString(), level));
      int currentNode = vertexList.size();
      final int nextLevel = level + 1;
      for (int i = 1; i < tree.size(); i++) {
        currentCount[0]++;
        edgeList.add(new SimpleImmutableEntry<Integer, Integer>(currentNode, currentCount[0]));
        IExpr arg = tree.get(i);
        if (nextLevel >= maxLevel || !arg.isAST()) {
          vertexList.add(new SimpleImmutableEntry<String, Integer>(arg.toString(), nextLevel));
        } else {
          treeToGraph((IAST) arg, nextLevel, maxLevel, currentCount, vertexList, edgeList);
        }
      }
    }

    private static void vertexToVisjs(StringBuilder buf,
        List<SimpleImmutableEntry<String, Integer>> vertexSet) {
      buf.append("var nodes = new vis.DataSet([\n");
      boolean first = true;
      int counter = 1;
      for (SimpleImmutableEntry<String, Integer> expr : vertexSet) {
        // {id: 1, label: 'Node 1'},
        if (first) {
          buf.append("  {id: ");
        } else {
          buf.append(", {id: ");
        }
        buf.append(counter++);
        buf.append(", label: '");
        buf.append(expr.getKey().toString());
        buf.append("', level: ");
        buf.append(expr.getValue().toString());
        buf.append("}\n");
        first = false;
      }
      buf.append("]);\n");
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        int maxLevel = Integer.MAX_VALUE;
        if (ast.isAST2()) {
          maxLevel = ast.arg2().toIntDefault();
          if (maxLevel < 0) {
            return F.NIL;
          }
        }
        IExpr arg1 = engine.evaluate(ast.arg1());
        List<SimpleImmutableEntry<String, Integer>> vertexList =
            new ArrayList<SimpleImmutableEntry<String, Integer>>();
        List<SimpleImmutableEntry<Integer, Integer>> edgeList =
            new ArrayList<SimpleImmutableEntry<Integer, Integer>>();
        StringBuilder jsControl = new StringBuilder();
        if (maxLevel > 0 && arg1.isAST()) {
          IAST tree = (IAST) arg1;
          int[] currentCount = new int[] {1};
          treeToGraph(tree, 0, maxLevel, currentCount, vertexList, edgeList);
          vertexToVisjs(jsControl, vertexList);
          edgesToVisjs(jsControl, edgeList);
          return F.JSFormData(jsControl.toString(), "treeform");
        } else {
          vertexList.add(new SimpleImmutableEntry<String, Integer>(arg1.toString(), 0));
          vertexToVisjs(jsControl, vertexList);
          edgesToVisjs(jsControl, edgeList);
          return F.JSFormData(jsControl.toString(), "treeform");
        }

      } catch (RuntimeException rex) {
        return Errors.printMessage(S.TreeForm, rex, engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  public static class VariableManager implements Function<IExpr, String> {
    ArrayDeque<Map<IExpr, String>> varStack;

    public void put(IExpr key, String variable) {
      varStack.peek().put(key, variable);
    }

    public Map<IExpr, String> peek() {
      return varStack.peek();
    }

    public void push() {
      Map<IExpr, String> map = new HashMap<IExpr, String>();
      varStack.push(map);
    }

    public void push(Map<IExpr, String> map) {
      varStack.push(map);
    }

    public Map<IExpr, String> pop() {
      return varStack.pop();
    }

    public VariableManager(Map<IExpr, String> map) {
      varStack = new ArrayDeque<Map<IExpr, String>>();
      varStack.add(map);
    }

    @Override
    public String apply(IExpr expr) {
      for (Iterator<Map<IExpr, String>> iterator = varStack.descendingIterator(); iterator
          .hasNext();) {
        Map<IExpr, String> map = iterator.next();
        String temp = map.get(expr);
        if (temp != null) {
          return temp;
        }
      }
      return null;
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
          if (headTest.equals(S.Integer) || headTest.equals(S.Complex) || headTest.equals(S.Real)) {
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
