package org.matheclipse.core.builtin;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldVector;
import org.matheclipse.core.builtin.OutputFunctions.VariableManager;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.Blank;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.form.output.DoubleFormFactory;
import org.matheclipse.core.form.output.JavaDoubleFormFactory;
import org.matheclipse.core.form.output.JavaScriptFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.HornerScheme;
import org.matheclipse.parser.client.FEConfig;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;

public final class OutputFunctions {
  /** Template for CompilePrint */
  public static final String JAVA_SOURCE_CODE = //
      "/* an in-memory compiled function */                                      \n"
          + "package org.matheclipse.core.compile;                                      \n"
          + "                                                                           \n"
          + "import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;     \n"
          + "import org.matheclipse.core.interfaces.IExpr;                              \n"
          + "import org.matheclipse.core.interfaces.IAST;                               \n"
          + "import org.matheclipse.core.eval.EvalEngine;                               \n"
          + "import org.matheclipse.core.expression.F;                                  \n"
          + "import static org.matheclipse.core.expression.F.*;                         \n"
          + "                                                                           \n"
          + "public class CompiledFunction extends AbstractFunctionEvaluator {          \n"
          + "    public IExpr evaluate(final IAST ast, EvalEngine engine){              \n"
          + "        if (ast.argSize()!={$size}) { return print(ast,{$size},engine); }     \n"
          + "        {$variables}                                                       \n"
          + "        return {$expression}\n"
          + "    }                                                                      \n"
          + "}                                                                          \n";

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.BaseForm.setEvaluator(new BaseForm());
      S.CForm.setEvaluator(new CForm());
      S.CompilePrint.setEvaluator(new CompilePrint());
      S.FullForm.setEvaluator(new FullForm());
      S.HoldForm.setEvaluator(new HoldForm());
      S.HornerForm.setEvaluator(new HornerForm());
      S.InputForm.setEvaluator(new InputForm());
      S.IntegerName.setEvaluator(new IntegerName());
      S.JavaForm.setEvaluator(new JavaForm());
      S.JSForm.setEvaluator(new JSForm());
      S.MathMLForm.setEvaluator(new MathMLForm());
      S.RomanNumeral.setEvaluator(new RomanNumeral());
      S.TableForm.setEvaluator(new TableForm());
      S.TeXForm.setEvaluator(new TeXForm());
      S.TreeForm.setEvaluator(new TreeForm());
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

    @Override
    public void setUp(ISymbol newSymbol) {}
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

  private static class CompileFactory {
    private abstract static class AbstractConverter implements IConverter {
      protected CompileFactory fFactory;

      public AbstractConverter() {}

      /** @param factory */
      @Override
      public void setFactory(final CompileFactory factory) {
        fFactory = factory;
      }
    }

    private static class CompoundExpressionConverter extends AbstractConverter {
      @Override
      public boolean convert(final StringBuilder buf, final IAST f) {

        buf.append("F.CompoundExpression(\n");
        for (int i = 1; i < f.size(); i++) {
          fFactory.convert(buf, f.get(i));
          if (i < f.size() - 1) {
            buf.append(",\n");
          } else {
            buf.append("\n");
          }
        }
        buf.append(")\n");
        return true;
      }
    }

    private static class DoConverter extends AbstractConverter {
      @Override
      public boolean convert(final StringBuilder buf, final IAST f) {
        if (f.size() != 3) {
          return false;
        }
        buf.append("F.Do(\n");
        fFactory.convert(buf, f.arg1());
        buf.append(",\n");
        fFactory.convert(buf, f.arg2());
        buf.append("\n");
        buf.append(")\n");
        return true;
      }
    }

    private interface IConverter {
      public boolean convert(StringBuilder buffer, IAST function);

      public void setFactory(final CompileFactory factory);
    }

    private static class ModuleConverter extends AbstractConverter {
      @Override
      public boolean convert(final StringBuilder buf, final IAST f) {
        if (f.size() != 3) {
          return false;
        }
        buf.append("F.Module(\n");
        fFactory.convert(buf, f.arg1());
        buf.append(",\n");
        fFactory.convert(buf, f.arg2());
        buf.append("\n");
        buf.append(")\n");
        return true;
      }
    }

    public static final HashMap<ISymbol, IConverter> CONVERTERS =
        new HashMap<ISymbol, IConverter>(199);

    static {
      CONVERTERS.put(S.CompoundExpression, new CompoundExpressionConverter());
      CONVERTERS.put(S.Do, new DoConverter());
      CONVERTERS.put(S.Module, new ModuleConverter());
    }

    //    final Set<IExpr> variables;
    Function<IExpr, String> numericVariables;

    Function<IExpr, String> variables;

    final IAST types;

    public CompileFactory(
        Function<IExpr, String> numericVariables, Function<IExpr, String> variables, IAST types) {
      this.numericVariables = numericVariables;
      this.variables = variables;
      this.types = types;
    }

    public void convert(StringBuilder buf, IExpr expression) {
      if (expression.isNumericFunction(numericVariables)) {
        try {
          DoubleFormFactory factory = JavaDoubleFormFactory.get(true, false);
          buf.append("F.num(");
          expression =
              expression.replaceAll(
                  x -> {
                    String str = numericVariables.apply(x);
                    if (x.isSymbol() && str != null) {
                      return F.Dummy(str); // x.toString() + "D");
                    }
                    return F.NIL;
                  });
          factory.convert(buf, expression);
          buf.append(")");
          return;
        } catch (RuntimeException rex) {
          //
        }
      }

      if (expression.isAST()) {
        IAST ast = (IAST) expression;
        IExpr h = ast.head();
        if (h.isSymbol()) {
          IConverter converter = CONVERTERS.get(h);
          if (converter != null) {
            converter.setFactory(this);
            StringBuilder sb = new StringBuilder();
            if (converter.convert(sb, ast)) {
              buf.append(sb);
              return;
            }
          }
        }
      }
      buf.append(expression.internalJavaString(variables));
    }
  }

  public static class CompilePrint extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST3()) {
          // TODO implement for 3 args
          return F.NIL;
        }
        IAST[] vars = OutputFunctions.checkIsVariableOrVariableList(ast, 1, engine);
        if (vars == null) {
          return F.NIL;
        }
        IAST variables = vars[0];
        IAST types = vars[1];

        String source = OutputFunctions.compilePrint(ast, variables, types, engine);
        if (source != null) {
          return F.stringx(source, IStringX.APPLICATION_JAVA);
        }
        return F.NIL;
      } catch (ValidateException ve) {
        return engine.printMessage(ast.topHead(), ve);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_2_3;
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
   * <p>shows the internal representation of the given <code>expression</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>FullForm shows the difference in the internal expression representation:
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

  /**
   *
   *
   * <pre>
   * HoldForm(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p><code>HoldForm</code> doesn't evaluate <code>expr</code> and didn't appear in the output
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
   * <p>Generate the horner scheme for a univariate <code>polynomial</code>.
   *
   * </blockquote>
   *
   * <pre>
   * HornerForm(polynomial, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>Generate the horner scheme for a univariate <code>polynomial</code> in <code>x</code>.
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="http://en.wikipedia.org/wiki/Horner_scheme">Wikipedia - Horner scheme</a>
   *   <li><a href="https://rosettacode.org/wiki/">Rosetta Code - Horner's rule for polynomial
   *       evaluation</a>
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
        // if (FEConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        // return F.stringx(StringFunctions.inputForm(arg1, true), IStringX.APPLICATION_SYMJA);
        // }
        return F.stringx(StringFunctions.inputForm(arg1), IStringX.APPLICATION_SYMJA);
      }
      return F.NIL;
    }

    @Override
    public void setUp(ISymbol newSymbol) {}
  }

  private static class IntegerName extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        NumberFormat formatter = null;
        try {
          long value = ((IInteger) arg1).toLong();
          if (value != Integer.MIN_VALUE && ast.isAST1()) {
            formatter = new RuleBasedNumberFormat(RuleBasedNumberFormat.SPELLOUT);
            String textNumber = formatter.format(value);
            if (textNumber != null) {
              return F.stringx(textNumber);
            }
          }
          IStringX language = F.stringx("English");
          IStringX qual = F.stringx("Words");
          if (ast.isAST2()) {
            if (!ast.arg2().isString()) {
              return F.NIL;
            }
            IStringX arg2 = (IStringX) ast.arg2();
            if (arg2.isString("Dutch")
                || //
                arg2.isString("Finnish")
                || //
                arg2.isString("English")
                || //
                arg2.isString("Esperanto")
                || //
                arg2.isString("French")
                || //
                arg2.isString("German")
                || //
                arg2.isString("Hungarian")
                || //
                arg2.isString("Italian")
                || //
                arg2.isString("Latin")
                || //
                arg2.isString("Polish")
                || //
                arg2.isString("Portuguese")
                || //
                arg2.isString("Romanian")
                || //
                arg2.isString("Russian")
                || //
                arg2.isString("Spanish")
                || //
                arg2.isString("Swedish")
                || //
                arg2.isString("Tongan")
                || //
                arg2.isString("Turkish")) {
              language = arg2;
            } else {
              qual = arg2;
            }
          }

          if (qual.isString("Words")) {
            if (language.isString("Dutch")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("nl"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("English")) {
              formatter = new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Esperanto")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("eo"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Finnish")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("fi"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("French")) {
              formatter = new RuleBasedNumberFormat(Locale.FRENCH, RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("German")) {
              formatter = new RuleBasedNumberFormat(Locale.GERMAN, RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Hungarian")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("hu"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Italian")) {
              formatter = new RuleBasedNumberFormat(Locale.ITALIAN, RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Latin")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("vai"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Polish")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("pl"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Portuguese")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("pt"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Romanian")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("ro"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Russian")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("ru"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Spanish")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("es"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Swedish")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("sv"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Tongan")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("sv"), RuleBasedNumberFormat.SPELLOUT);
            } else if (language.isString("Turkish")) {
              formatter =
                  new RuleBasedNumberFormat(new Locale("tr"), RuleBasedNumberFormat.SPELLOUT);
            }
            if (formatter != null) {
              String textNumber = formatter.format(value);
              if (textNumber != null) {
                return F.stringx(textNumber);
              }
            }
          }
        } catch (Exception ex) {
          if (FEConfig.SHOW_STACKTRACE) {
            ex.printStackTrace();
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
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
   * <p>returns the Symja Java form of the <code>expr</code>. In Java you can use the created Symja
   * expressions.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>JavaForm can add the <code>F.</code> prefix for class <code>
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
   * <p>JavaForm evaluates its argument before creating the Java form:
   *
   * <pre>
   * &gt;&gt; JavaForm(D(sin(x)*cos(x),x))
   * "Plus(Sqr(Cos(x)),Negate(Sqr(Sin(x))))"
   * </pre>
   *
   * <p>You can use <code>Hold</code> to suppress the evaluation:
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

    public static String javaForm(IExpr arg1, boolean strictJava, boolean usePrefix) {
      return arg1.internalJavaString(strictJava, 0, false, usePrefix, false, F.CNullFunction);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr arg1 = engine.evaluate(ast.arg1());
        boolean floatJava = false;
        boolean strictJava = false;
        boolean usePrefix = false;
        if (ast.isAST2()) {
          IExpr arg2 = engine.evaluate(ast.arg2());
          if (arg2 == S.Float) {
            floatJava = true;
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
          return F.$str(toJavaDouble(arg1), IStringX.APPLICATION_JAVA);
        }
        String resultStr = javaForm(arg1, strictJava, usePrefix);
        return F.$str(resultStr, IStringX.APPLICATION_JAVA);
      } catch (Exception rex) {
        if (FEConfig.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
        return engine.printMessage("JavaForm: " + rex.getMessage());
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
        if (ast.isAST2() && ast.arg2().isString("Mathcell")) {
          javascriptFlavor = JavaScriptFormFactory.USE_MATHCELL;
        }
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isAST(S.JSFormData, 3)) {
          String manipulateStr = ((IAST) arg1).arg1().toString();
          return F.$str(manipulateStr, IStringX.APPLICATION_JAVASCRIPT);
        }
        if (arg1.isDataset()) {
          return F.$str(((IASTDataset) arg1).datasetToJSForm(), IStringX.TEXT_HTML);
        }
        if (arg1 instanceof GraphExpr) {
          return F.$str(
              GraphFunctions.graphToJSForm((GraphExpr) arg1), IStringX.APPLICATION_JAVASCRIPT);
        }

        return F.$str(toJavaScript(arg1, javascriptFlavor), IStringX.APPLICATION_JAVASCRIPT);
      } catch (Exception rex) {
        if (FEConfig.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
        return engine.printMessage("JSForm: " + rex.getMessage());
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
   * <p>returns the MathMLForm form of the evaluated <code>expr</code>.
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
      if (arg1.isInteger()) {
        try {
          long value = ((IInteger) arg1).toLong();
          if (value == 0) {
            return F.stringx("N");
          }
          net.numericalchameleon.util.romannumerals.RomanNumeral romanNumeral = //
              new net.numericalchameleon.util.romannumerals.RomanNumeral(value);
          return F.stringx(romanNumeral.toRoman());
        } catch (net.numericalchameleon.util.romannumerals.RomanNumeralException rne) {
          // Integer expected in range `1` to `2`.
          return IOFunctions.printMessage( //
              ast.topHead(), //
              "intrange", //
              F.List(
                  F.ZZ(net.numericalchameleon.util.romannumerals.RomanNumeral.MIN_VALUE), //
                  F.ZZ(net.numericalchameleon.util.romannumerals.RomanNumeral.MAX_VALUE)), //
              engine);
        } catch (RuntimeException rex) {
          if (FEConfig.SHOW_STACKTRACE) {
            rex.printStackTrace();
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static class TableForm extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        StringBuilder tableForm = new StringBuilder();
        if (plaintextTable(tableForm, arg1, " ", x -> x.toString(), true)) {
          return F.stringx(tableForm.toString(), IStringX.TEXT_PLAIN);
        }
        if (arg1.isList()) {
          IAST list = (IAST) arg1;
          StringBuilder sb = new StringBuilder();
          for (int i = 1; i < list.size(); i++) {
            sb.append(list.get(i).toString());
            sb.append("\n");
          }
          return F.stringx(sb.toString(), IStringX.TEXT_PLAIN);
        }
        int dim = arg1.isVector();
        if (dim >= 0) {
          FieldVector<IExpr> vector = Convert.list2Vector(arg1);
          if (vector != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dim; i++) {
              sb.append(vector.getEntry(i).toString());
              sb.append("\n");
            }
            return F.stringx(sb.toString(), IStringX.TEXT_PLAIN);
          }
        }
        return F.stringx(arg1.toString(), IStringX.TEXT_PLAIN);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {}
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
   * <p>returns the TeX form of the evaluated <code>expr</code>.
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
    private static void edgesToVisjs(
        StringBuilder buf, List<SimpleImmutableEntry<Integer, Integer>> edgeSet) {
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

    private static void treeToGraph(
        IAST tree,
        final int level,
        final int maxLevel,
        int[] currentCount,
        List<SimpleImmutableEntry<String, Integer>> vertexList,
        List<SimpleImmutableEntry<Integer, Integer>> edgeList) {
      vertexList.add(
          new SimpleImmutableEntry<String, Integer>(
              tree.head().toString(), Integer.valueOf(level)));
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

    private static void vertexToVisjs(
        StringBuilder buf, List<SimpleImmutableEntry<String, Integer>> vertexSet) {
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
          vertexList.add(
              new SimpleImmutableEntry<String, Integer>(arg1.toString(), Integer.valueOf(0)));
          vertexToVisjs(jsControl, vertexList);
          edgesToVisjs(jsControl, edgeList);
          return F.JSFormData(jsControl.toString(), "treeform");
        }

      } catch (Exception rex) {
        if (FEConfig.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
        return engine.printMessage("TreeForm: " + rex.getMessage());
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  public static class VariableManager implements Function<IExpr, String> {
    Map<IExpr, String> varMap;

    public VariableManager(Map<IExpr, String> map) {
      this.varMap = map;
    }

    @Override
    public String apply(IExpr expr) {
      return varMap.get(expr);
    }
  }

  public static IAST[] checkIsVariableOrVariableList(IAST ast, int position, EvalEngine engine) {
    IAST[] result = new IAST[2];

    if (ast.get(position).isList()) {
      IAST list = (IAST) ast.get(position);

      IASTMutable vars = list.copy();
      IASTAppendable types = F.ListAlloc(list.size());

      for (int i = 1; i < list.size(); i++) {
        if (!checkVariable(ast, engine, list.get(i), vars, types, i)) {
          return null;
        }
      }

      result[0] = vars;
      result[1] = types;
      return result;
    } else {
      IExpr arg = ast.arg1();
      IASTMutable vars = F.unaryAST1(S.List, arg);
      IASTAppendable types = F.ListAlloc(2);

      if (!checkVariable(ast, engine, arg, vars, types, 1)) {
        // `1` is not a valid variable.
        IOFunctions.printMessage(ast.topHead(), "ivar", F.List(arg), engine);
        return null;
      }

      result[0] = vars;
      result[1] = types;
      return result;
    }
  }

  public static boolean checkVariable(
      IAST ast, EvalEngine engine, IExpr arg, IASTMutable vars, IASTAppendable types, int i) {
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
            // `1` is not a valid variable.
            IOFunctions.printMessage(ast.topHead(), "ivar", F.List(sym), engine);
            return false;
          }
          if (headTest.equals(S.Integer) || headTest.equals(S.Complex) || headTest.equals(S.Real)) {
            // allowed machine-sized types
          } else {
            headTest = null;
          }
        }
        if (headTest == null) {
          // `1` is not a valid variable.
          IOFunctions.printMessage(ast.topHead(), "ivar", F.List(sym), engine);
          return false;
        }
      }
    }
    //    if (sym.isSymbol() && sym.isVariable()) {
    vars.set(i, sym);
    types.append(headTest);
    return true;
    //    }
    //    // `1` is not a valid variable.
    //    IOFunctions.printMessage(ast.topHead(), "ivar", F.List(sym), engine);
    //    return false;
  }

  public static String compilePrint(final IAST ast, IAST variables, IAST types, EvalEngine engine) {
    Map<IExpr, String> symbolicVariables = new HashMap<IExpr, String>();
    Map<IExpr, String> numericVariables = new HashMap<IExpr, String>();

    StringBuilder variablesBuf = new StringBuilder();
    for (int i = 1; i < variables.size(); i++) {
      IExpr argType = types.get(i);
      IExpr variable = variables.get(i);
      if (numericVariables.get(variable) != null) {
        // Duplicate parameter `1` found in `2`.
        IOFunctions.printMessage(ast.topHead(), "fdup", F.List(variable, ast.arg1()), engine);
        return null;
      }

      if (argType.equals(S.Real)) {
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");
        variablesBuf.append("double " + variable + "d = engine.evalDouble(" + variable + ");\n");
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, variable + "d");
      } else if (argType.equals(S.Integer)) {
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");
        variablesBuf.append("int " + variable + "i = engine.evalInt(" + variable + ");\n");
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, variable + "i");
      } else if (argType.equals(S.Complex)) {
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");
        variablesBuf.append("Complex " + variable + "c = engine.evalComplex(" + variable + ");\n");
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, variable + "c");
      } else if (argType.equals(S.Booleans)) {
        variablesBuf.append("IExpr " + variable + " = ast.get(" + i + ");\n");
        variablesBuf.append("boolean " + variable + "b = engine.evalBoolean(" + variable + ");\n");
        symbolicVariables.put(variable, variable.toString());
        numericVariables.put(variable, variable + "b");
      }
    }
    IExpr expression = ast.arg2();

    StringBuilder buf = new StringBuilder();
    VariableManager numericVars = new VariableManager(numericVariables);
    VariableManager symbolicVars = new VariableManager(symbolicVariables);
    OutputFunctions.CompileFactory cf =
        new OutputFunctions.CompileFactory(numericVars, symbolicVars, types);
    buf.append("\n");
    cf.convert(buf, expression);
    buf.append(";\n");
    String source = JAVA_SOURCE_CODE.replace("{$variables}", variablesBuf.toString());
    source = source.replace("{$expression}", buf.toString());
    source = source.replace("{$size}", Integer.toString(variables.argSize()));
    //    System.out.println(source);
    return source;
  }

  public static void initialize() {
    Initializer.init();
  }

  public static boolean markdownTable(
      StringBuilder result,
      IExpr expr,
      java.util.function.Function<IExpr, String> function,
      boolean fillUpWithSPACE) {
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

  public static boolean plaintextTable(
      StringBuilder result,
      IExpr expr,
      String delimiter,
      java.util.function.Function<IExpr, String> function,
      boolean fillUpWithSPACE) {
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

  public static String toJavaDouble(final IExpr arg1) throws IOException {
    DoubleFormFactory factory = JavaDoubleFormFactory.get(true, false);
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
