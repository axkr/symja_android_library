package org.matheclipse.core.form.mathml;

import java.io.IOException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.GraphicsUtil;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.ApfloatToMMA;
import org.matheclipse.core.form.DoubleToMMA;
import org.matheclipse.core.form.NumberFormatter;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Characters;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.PostfixOperator;
import org.matheclipse.parser.client.operator.Precedence;
import org.matheclipse.parser.client.operator.PrefixOperator;
import org.matheclipse.parser.trie.TrieBuilder;
import org.matheclipse.parser.trie.TrieMatch;

/** Generates MathML presentation output */
public class MathMLFormFactory extends AbstractMathMLFormFactory {

  private final class Abs extends AbstractConverter {

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      fFactory.tagStart(buf, "mrow");
      // fFactory.tag(buf, "mo", "&LeftBracketingBar;");
      fFactory.tag(buf, "mo", "&#10072;");
      fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
      // fFactory.tag(buf, "mo", "&RightBracketingBar;");
      fFactory.tag(buf, "mo", "&#10072;");
      fFactory.tagEnd(buf, "mrow");

      return true;
    }
  }

  private abstract static class AbstractConverter implements IConverter {
    protected MathMLFormFactory fFactory;

    public AbstractConverter() {}

    /** @param factory */
    @Override
    public void setFactory(final MathMLFormFactory factory) {
      fFactory = factory;
    }
  }

  private static final class Binomial extends AbstractConverter {

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      // "<mrow><mo>(</mo><mfrac linethickness=\"0\">{0}{1}</mfrac><mo>)</mo></mrow>"
      if (f.size() != 3) {
        return false;
      }
      fFactory.tagStart(buf, "mrow");
      fFactory.tag(buf, "mo", "(");
      fFactory.tagStart(buf, "mfrac", "linethickness=\"0\"");
      fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
      fFactory.convertInternal(buf, f.arg2(), Integer.MIN_VALUE, false);
      fFactory.tagEnd(buf, "mfrac");
      fFactory.tag(buf, "mo", ")");
      fFactory.tagEnd(buf, "mrow");
      return true;
    }
  }

  private static final class C extends AbstractConverter {

    /**
     * Convert C(1) to <code><msub><mi>c</mi><mn>1</mn></msub></code>
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.isAST1() && f.head() == S.C && f.arg1().isInteger()) {
        fFactory.tagStart(buf, "msub");
        buf.append("<mi>c</mi>");
        fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
        fFactory.tagEnd(buf, "msub");
        return true;
      }
      return false;
    }
  }

  private static final class Ceiling extends AbstractConverter {

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      fFactory.tagStart(buf, "mrow");
      // LeftCeiling &#002308;
      fFactory.tag(buf, "mo", "&#x2308;");
      fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
      // RightCeiling &#x02309;
      fFactory.tag(buf, "mo", "&#x2309;");
      fFactory.tagEnd(buf, "mrow");
      return true;
    }
  }

  private static final class D extends AbstractConverter {

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.isAST2()) {
        fFactory.tagStart(buf, "mfrac");
        fFactory.tagStart(buf, "mrow");
        // &PartialD; &x02202;
        fFactory.tag(buf, "mo", "&#x2202;");

        fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
        fFactory.tagEnd(buf, "mrow");
        fFactory.tagStart(buf, "mrow");
        // &PartialD; &x02202
        fFactory.tag(buf, "mo", "&#x2202;");
        fFactory.convertInternal(buf, f.arg2(), Integer.MIN_VALUE, false);

        fFactory.tagEnd(buf, "mrow");
        fFactory.tagEnd(buf, "mfrac");
        return true;
      }
      return false;
    }
  }

  private static final class Element extends AbstractConverter {

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.isAST2()) {
        fFactory.tagStart(buf, "mrow");
        fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
        buf.append("<mo>&#8712;</mo>");
        fFactory.convertInternal(buf, f.arg2(), Integer.MIN_VALUE, false);
        fFactory.tagEnd(buf, "mrow");
        return true;
      }
      return false;
    }
  }

  private static final class Floor extends AbstractConverter {

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      fFactory.tagStart(buf, "mrow");
      // &LeftFloor; &x0230A;
      fFactory.tag(buf, "mo", "&#x230A;");
      fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
      // &RightFloor; &#0230B;
      fFactory.tag(buf, "mo", "&#x230B;");
      fFactory.tagEnd(buf, "mrow");
      return true;
    }
  }

  private static final class Function extends AbstractConverter {

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      // "<mrow>{0}<mo>!</mo></mrow>"
      if (f.size() != 2) {
        return false;
      }
      fFactory.tagStart(buf, "mrow");
      fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
      fFactory.tag(buf, "mo", "&amp;");
      fFactory.tagEnd(buf, "mrow");
      return true;
    }
  }

  private interface IConverter {
    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buffer StringBuilder for MathML output
     * @param function the math function which should be converted to MathML
     */
    public boolean convert(StringBuilder buffer, IAST function, int precedence);

    public void setFactory(final MathMLFormFactory factory);
  }

  private static final class Integrate extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() >= 3) {
        // &sum; &#x2211
        return iteratorStep(buf, "&#x222B;", f, 2);
      }
      return false;
    }

    public boolean iteratorStep(final StringBuilder buf, final String mathSymbol, final IAST f,
        int i) {
      if (i >= f.size()) {
        fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
        return true;
      }
      if (f.get(i).isList()) {
        IAST list = (IAST) f.get(i);
        if (list.isAST3() && list.arg1().isSymbol()) {
          ISymbol symbol = (ISymbol) list.arg1();
          fFactory.tagStart(buf, "msubsup");
          // &Integral; &#x222B;
          fFactory.tag(buf, "mo", mathSymbol);
          fFactory.convertInternal(buf, list.arg2(), Integer.MIN_VALUE, false);
          fFactory.convertInternal(buf, list.arg3(), Integer.MIN_VALUE, false);
          fFactory.tagEnd(buf, "msubsup");
          if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
            return false;
          }
          fFactory.tagStart(buf, "mrow");
          // &dd; &#x2146;
          fFactory.tag(buf, "mo", "&#x2146;");
          fFactory.convertSymbol(buf, symbol);
          fFactory.tagEnd(buf, "mrow");
          return true;
        }
      } else if (f.get(i).isSymbol()) {
        ISymbol symbol = (ISymbol) f.get(i);
        // &Integral; &#x222B;
        fFactory.tag(buf, "mo", mathSymbol);
        if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
          return false;
        }
        fFactory.tagStart(buf, "mrow");
        // &dd; &#x2146;
        fFactory.tag(buf, "mo", "&#x2146;");
        fFactory.convertSymbol(buf, symbol);
        fFactory.tagEnd(buf, "mrow");
        return true;
      }
      return false;
    }
  }

  /**
   * Renders the <code>ScientificForm, EngineeringForm, NumberForm, AccountingForm, PaddedForm,
   * DecimalForm</code> display wrappers by installing a {@link NumberFormatter} for the wrapped
   * subtree.
   */
  private static final class NumberForm extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() < 2) {
        return false;
      }
      NumberFormatter formatter = NumberFormatter.of(f, EvalEngine.get());
      NumberFormatter previous = fFactory.fNumberFormatter;
      fFactory.fNumberFormatter = formatter;
      try {
        fFactory.convertInternal(buf, f.arg1(), precedence, false);
      } finally {
        fFactory.fNumberFormatter = previous;
      }
      return true;
    }
  }

  private static final class MatrixForm extends AbstractConverter {

    final boolean tableForm;

    public MatrixForm(boolean tableForm) {
      this.tableForm = tableForm;
    }

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      IExpr arg1 = f.arg1();
      int[] dims = arg1.isMatrix();
      if (dims == null) {
        int dim = arg1.isVector();
        if (dim < 0) {
          return false;
        } else {
          final IAST vector = (IAST) arg1.normal(false);
          if (!tableForm) {
            fFactory.tagStart(buf, "mrow");
            fFactory.tag(buf, "mo", "(");
          }
          fFactory.tagStart(buf, "mtable", "columnalign=\"center\"");

          IExpr temp;
          for (int i = 1; i < vector.size(); i++) {

            temp = vector.get(i);
            fFactory.tagStart(buf, "mtr");
            fFactory.tagStart(buf, "mtd", "columnalign=\"center\"");
            fFactory.convertInternal(buf, temp, Integer.MIN_VALUE, false);
            fFactory.tagEnd(buf, "mtd");
            fFactory.tagEnd(buf, "mtr");
          }

          fFactory.tagEnd(buf, "mtable");
          if (!tableForm) {
            fFactory.tag(buf, "mo", ")");
            fFactory.tagEnd(buf, "mrow");
          }
        }
      } else {
        final IAST matrix = (IAST) arg1.normal(false);
        if (!tableForm) {
          fFactory.tagStart(buf, "mrow");
          fFactory.tag(buf, "mo", "(");
        }
        fFactory.tagStart(buf, "mtable", "columnalign=\"center\"");

        IAST temp;
        for (int i = 1; i < matrix.size(); i++) {

          temp = (IAST) matrix.get(i);
          fFactory.tagStart(buf, "mtr");
          for (int j = 1; j < temp.size(); j++) {

            fFactory.tagStart(buf, "mtd", "columnalign=\"center\"");
            fFactory.convertInternal(buf, temp.get(j), Integer.MIN_VALUE, false);
            fFactory.tagEnd(buf, "mtd");
          }
          fFactory.tagEnd(buf, "mtr");
        }

        fFactory.tagEnd(buf, "mtable");
        if (!tableForm) {
          fFactory.tag(buf, "mo", ")");
          fFactory.tagEnd(buf, "mrow");
        }
      }
      return true;
    }
  }

  private static class MMLFunction extends AbstractConverter {

    String fFunctionName;

    public MMLFunction(final MathMLFormFactory factory, final String functionName) {
      super();
      fFunctionName = functionName;
    }

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {

      fFactory.tagStart(buf, "mrow");
      fFactory.tag(buf, "mi", fFunctionName);
      // &af; &#x2061;
      fFactory.tag(buf, "mo", "&#x2061;");

      fFactory.tag(buf, "mo", "(");
      for (int i = 1; i < f.size(); i++) {
        fFactory.convertInternal(buf, f.get(i), Integer.MIN_VALUE, false);
        if (i < f.argSize()) {
          fFactory.tag(buf, "mo", ",");
        }
      }
      fFactory.tag(buf, "mo", ")");
      fFactory.tagEnd(buf, "mrow");
      return true;
    }
  }

  private static class MMLOperator extends AbstractConverter {
    protected int fPrecedence;
    protected String fFirstTag;
    protected String fOperator;

    public MMLOperator(final int precedence, final String oper) {
      this(precedence, "mrow", oper);
    }

    public MMLOperator(final int precedence, final String firstTag, final String oper) {
      fPrecedence = precedence;
      fFirstTag = firstTag;
      fOperator = oper;
    }

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      final boolean isOr = f.isOr();
      fFactory.tagStart(buf, fFirstTag);
      precedenceOpen(buf, precedence);
      for (int i = 1; i < f.size(); i++) {
        if (isOr && f.get(i).isAnd()) {
          fFactory.tagStart(buf, "mrow");
          fFactory.tag(buf, "mo", "(");
        }
        fFactory.convertInternal(buf, f.get(i), fPrecedence, false);
        if (isOr && f.get(i).isAnd()) {
          fFactory.tag(buf, "mo", ")");
          fFactory.tagEnd(buf, "mrow");
        }
        if (i < f.argSize()) {
          if (fOperator.compareTo("") != 0) {
            fFactory.tag(buf, "mo", fOperator);
          }
        }
      }
      precedenceClose(buf, precedence);
      fFactory.tagEnd(buf, fFirstTag);
      return true;
    }

    public void precedenceClose(final StringBuilder buf, final int precedence) {
      if (precedence > fPrecedence) {
        fFactory.tag(buf, "mo", ")");
        fFactory.tagEnd(buf, "mrow");
      }
    }

    public void precedenceOpen(final StringBuilder buf, final int precedence) {
      if (precedence > fPrecedence) {
        fFactory.tagStart(buf, "mrow");
        fFactory.tag(buf, "mo", "(");
      }
    }
  }

  private static final class MMLPostfix extends AbstractConverter {

    final String fOperator;
    final int fPrecedence;

    public MMLPostfix(final String operator, int precedence) {
      super();
      fOperator = operator;
      fPrecedence = precedence;
    }

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.isAST1()) {
        fFactory.tagStart(buf, "mrow");
        if (fPrecedence <= precedence) {
          fFactory.tag(buf, "mo", "(");
        }
        fFactory.convertInternal(buf, f.arg1(), fPrecedence, false);
        fFactory.tag(buf, "mo", fOperator);
        if (fPrecedence <= precedence) {
          fFactory.tag(buf, "mo", ")");
        }
        fFactory.tagEnd(buf, "mrow");
        return true;
      }
      return false;
    }
  }

  private static final class Not extends AbstractConverter {

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      // <mrow><mo>&not;</mo>{0}</mrow>
      if (f.size() != 2) {
        return false;
      }
      fFactory.tagStart(buf, "mrow");
      // &not; &#x00AC;
      fFactory.tag(buf, "mo", "&#x00AC;");
      fFactory.convertInternal(buf, f.arg1(), Precedence.NOT, false);
      fFactory.tagEnd(buf, "mrow");
      return true;
    }
  }

  class Operator {
    String fOperator;

    Operator(final String oper) {
      fOperator = oper;
    }

    public void convert(final StringBuilder buf) {
      tagStart(buf, "mo");
      buf.append(fOperator);
      tagEnd(buf, "mo");
    }

    @Override
    public String toString() {
      return fOperator;
    }
  }

  private static final class Plus extends MMLOperator {

    public Plus() {
      super(Precedence.PLUS, "mrow", "+");
    }

    @Override
    public boolean convert(final StringBuilder buf, IAST f, final int precedence) {
      if (fFactory.fPlusReversed) {
        f = f.reverse(F.NIL);
      }
      IExpr expr;
      fFactory.tagStart(buf, fFirstTag);
      precedenceOpen(buf, precedence);
      final Times timesConverter = new Times();
      timesConverter.setFactory(fFactory);
      int size = f.argSize();
      for (int i = size; i > 0; i--) {
        expr = f.get(i);
        if ((i < size) && expr.isAST(S.Times)) {
          timesConverter.convertTimesFraction(buf, (IAST) expr, fPrecedence,
              MathMLFormFactory.PLUS_CALL);
        } else {
          if (i < size) {
            boolean plusTag = true;
            if (expr.isNumber()) {
              IExpr realPart = expr.re();
              if (realPart.isZero()) {
                IExpr imaginaryPart = expr.im();
                if (imaginaryPart.isNegative()) {
                  fFactory.tag(buf, "mo", "-");
                  expr = expr.negate();
                  plusTag = false;
                }
              } else {
                if (realPart.isNegative()) {
                  fFactory.tag(buf, "mo", "-");
                  expr = expr.negate();
                  plusTag = false;
                }
              }
            }
            if (plusTag) {
              fFactory.tag(buf, "mo", "+");
            }
          }
          fFactory.convertInternal(buf, expr, fPrecedence, false);
        }
      }
      precedenceClose(buf, precedence);
      fFactory.tagEnd(buf, fFirstTag);
      return true;
    }
  }

  private static final class Power extends MMLOperator {

    public Power() {
      super(Precedence.POWER, "msup", "");
    }

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      IExpr arg1 = f.arg1();
      IExpr arg2 = f.arg2();
      IExpr exp = arg2;
      IExpr den = F.C1;
      int useMROOT = 0;
      if (arg2.isFraction()) {
        IFraction f2 = ((IFraction) arg2);
        if (f2.isPositive()) {
          exp = f2.numerator();
          if (f2.isNumEqualRational(F.C1D2)) {
            fFactory.tagStart(buf, "msqrt");
            useMROOT = 1;
          } else {
            den = f2.denominator();
            fFactory.tagStart(buf, "mroot");
            useMROOT = 2;
          }
        }
      }
      if (useMROOT > 0 && exp.isOne()) {
        fFactory.convertInternal(buf, arg1, Integer.MIN_VALUE, false);
      } else {
        if (exp.isNegative()) {
          exp = exp.negate();
          fFactory.tagStart(buf, "mfrac");
          fFactory.convertInternal(buf, F.C1, Integer.MIN_VALUE, false);
          if (exp.isOne()) {
            fFactory.convertInternal(buf, arg1, Integer.MIN_VALUE, false);
          } else {
            convert(buf, F.Power(arg1, exp), Integer.MIN_VALUE);
          }
          fFactory.tagEnd(buf, "mfrac");
        } else {
          precedenceOpen(buf, precedence);
          fFactory.tagStart(buf, "msup");
          fFactory.convertInternal(buf, arg1, fPrecedence, false);
          fFactory.convertInternal(buf, exp, fPrecedence, false);
          fFactory.tagEnd(buf, "msup");
          precedenceClose(buf, precedence);
        }
      }
      if (useMROOT == 1) {
        fFactory.tagEnd(buf, "msqrt");
      } else if (useMROOT == 2) {
        fFactory.convertInternal(buf, den, fPrecedence, false);
        fFactory.tagEnd(buf, "mroot");
      }
      return true;
    }
  }

  private static final class Product extends Sum {

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() >= 3) {
        // &Product; &#x220F;
        return iteratorStep(buf, "&#x220F;", f, 2);
      }
      return false;
    }
  }

  private static final class Rational extends AbstractConverter {

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }

      // <mfrac><mi>k</mi><mn>2</mn></mfrac>
      fFactory.tagStart(buf, "mfrac");
      fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
      fFactory.convertInternal(buf, f.arg2(), Integer.MIN_VALUE, false);
      fFactory.tagEnd(buf, "mfrac");

      return true;
    }
  }

  private static final class Sqrt extends AbstractConverter {

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      fFactory.tagStart(buf, "msqrt");
      fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
      fFactory.tagEnd(buf, "msqrt");
      return true;
    }
  }

  private static final class Surd extends AbstractConverter {

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      if (f.arg2().isNegative()) {
        fFactory.tagStart(buf, "mfrac");
        fFactory.tagStart(buf, "mn");
        buf.append("1");
        fFactory.tagEnd(buf, "mn");
        fFactory.tagStart(buf, "mroot");
        fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
        fFactory.convertInternal(buf, f.arg2().negate(), Integer.MIN_VALUE, false);
        fFactory.tagEnd(buf, "mroot");
        fFactory.tagEnd(buf, "mfrac");
      } else {
        fFactory.tagStart(buf, "mroot");
        fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
        fFactory.convertInternal(buf, f.arg2(), Integer.MIN_VALUE, false);
        fFactory.tagEnd(buf, "mroot");
      }
      return true;
    }
  }

  private static final class Subscript extends MMLOperator {

    public Subscript() {
      super(0, "msub", "");
    }

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() < 3) {
        return false;
      }
      if (f.isAST2()) {
        IExpr arg1 = f.arg1();
        IExpr arg2 = f.arg2();
        fFactory.tagStart(buf, "msub");
        fFactory.convertInternal(buf, arg1, fPrecedence, false);
        fFactory.convertInternal(buf, arg2, fPrecedence, false);
        fFactory.tagEnd(buf, "msub");
        return true;
      }

      fFactory.tagStart(buf, "msub");
      fFactory.convertInternal(buf, f.arg1(), fPrecedence, false);
      fFactory.tagStart(buf, "mrow");
      for (int i = 2; i < f.size(); i++) {
        fFactory.convertInternal(buf, f.get(i), fPrecedence, false);
        if (i < f.argSize()) {
          buf.append("<mo>,</mo>");
        }
      }
      fFactory.tagEnd(buf, "mrow");
      fFactory.tagEnd(buf, "msub");
      return true;
    }
  }

  /**
   * <code>Button[label, action]</code> as a real button.
   *
   * <p>
   * The markup goes inside an <code>mtext</code>, which is an HTML integration point: an HTML
   * element there is parsed as HTML rather than as MathML. The interactive front end replaces the
   * second argument with the position of the action it kept, so a button of a Manipulate body
   * carries that position and one written anywhere else stays inert.
   */
  private static final class Button extends MMLOperator {

    public Button() {
      super(0, "mtext", "");
    }

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      // only a plain integer is the position of a kept action; anything else is the action
      // itself, which no front end can run, so that button stays inert
      int action = f.arg2().isInteger() ? f.arg2().toIntDefault() : -1;
      fFactory.tagStart(buf, "mtext");
      buf.append("<span class=\"symjabutton\"");
      if (action >= 0) {
        buf.append(" data-action=\"").append(action).append("\"");
      }
      buf.append(">");
      buf.append(escapeHtml(labelText(f.arg1())));
      buf.append("</span>");
      fFactory.tagEnd(buf, "mtext");
      return true;
    }

    private static String labelText(IExpr label) {
      return label.isString() ? label.toString() : label.toString();
    }

    private static String escapeHtml(String text) {
      return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"",
          "&quot;");
    }
  }

  /**
   * A control object - <code>Slider[Dynamic[x]]</code> and its relatives - as the place its widget
   * goes.
   *
   * <p>
   * Like {@link Button} this only ever emits a marker. The interactive front end has replaced the
   * arguments with the position of the control it kept, and it holds the description the widget is
   * built from; <code>Dynamic</code> around a lone integer means "the control at this position".
   * Anything else - including a perfectly ordinary static <code>Slider[0.5]</code> - is a control
   * object nobody is driving, which stays inert and prints as itself.
   */
  private static final class ControlWidget extends MMLOperator {

    public ControlWidget() {
      super(0, "mtext", "");
    }

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2 || !f.arg1().isAST(S.Dynamic, 2)) {
        return false;
      }
      int control = ((IAST) f.arg1()).arg1().toIntDefault();
      if (control < 0) {
        return false;
      }
      fFactory.tagStart(buf, "mtext");
      buf.append("<span class=\"symjacontrol\" data-control=\"").append(control).append("\">");
      buf.append("</span>");
      fFactory.tagEnd(buf, "mtext");
      return true;
    }
  }

  /**
   * <code>Row[{e1, e2, ...}]</code> and <code>Row[{...}, separator]</code> as the elements set
   * side by side.
   *
   * <p>
   * A row is the usual way to write a live read-out - <code>Row[{"moves: ", Dynamic[moves]}]</code>
   * - so it has to lay its elements out rather than print as a function call. Anything inside it
   * goes through the ordinary conversion, which is what lets a control or a button sit in one.
   */
  private static final class Row extends AbstractConverter {

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() < 2 || f.size() > 3 || !f.arg1().isList()) {
        return false;
      }
      IAST items = (IAST) f.arg1();
      IExpr separator = f.size() == 3 ? f.arg2() : F.NIL;
      fFactory.tagStart(buf, "mrow");
      for (int i = 1; i < items.size(); i++) {
        fFactory.convertInternal(buf, items.get(i), Integer.MIN_VALUE, false);
        if (separator.isPresent() && i < items.argSize()) {
          fFactory.convertInternal(buf, separator, Integer.MIN_VALUE, false);
        }
      }
      fFactory.tagEnd(buf, "mrow");
      return true;
    }
  }

  /**
   * <code>Graphics[...]</code> as the picture itself, drawn into the surrounding layout.
   *
   * <p>
   * A graphic that is the whole result is drawn by the front end, which has a path of its own for
   * it. One inside a layout had nowhere to go and printed as <code>Graphics({Disk()})</code> in the
   * middle of the page, which is what made a picture and a control impossible to put in one column
   * - and a <code>LocatorPane</code>, which is exactly a picture with controls on it, impossible to
   * show at all.
   *
   * <p>
   * The SVG goes inside an <code>mtext</code>, an HTML integration point, for the same reason a
   * button does: an HTML element there is parsed as HTML rather than as MathML.
   */
  private static final class GraphicsInline extends AbstractConverter {

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() < 2) {
        return false;
      }
      StringBuilder svg = new StringBuilder();
      try {
        if (!GraphicsUtil.renderGraphics2DSVG(svg, f, true, EvalEngine.get())) {
          return false;
        }
      } catch (RuntimeException rex) {
        // a picture that cannot be drawn falls back to printing as itself, rather than taking
        // the whole rendering with it
        return false;
      }
      fFactory.tagStart(buf, "mtext");
      buf.append(svg);
      fFactory.tagEnd(buf, "mtext");
      return true;
    }
  }

  /** <code>Column[{e1, e2, ...}]</code> as the elements one above the other. */
  private static final class Column extends AbstractConverter {

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() < 2 || !f.arg1().isList()) {
        return false;
      }
      IAST items = (IAST) f.arg1();
      fFactory.tagStart(buf, "mtable", "columnalign=\"left\"");
      for (int i = 1; i < items.size(); i++) {
        fFactory.tagStart(buf, "mtr");
        fFactory.tagStart(buf, "mtd", "columnalign=\"left\"");
        fFactory.convertInternal(buf, items.get(i), Integer.MIN_VALUE, false);
        fFactory.tagEnd(buf, "mtd");
        fFactory.tagEnd(buf, "mtr");
      }
      fFactory.tagEnd(buf, "mtable");
      return true;
    }
  }

  /** <code>Underscript[x, under]</code> as the MathML <code>munder</code> element. */
  private static final class Underscript extends MMLOperator {

    public Underscript() {
      super(0, "munder", "");
    }

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      fFactory.tagStart(buf, "munder");
      fFactory.convertInternal(buf, f.arg1(), fPrecedence, false);
      fFactory.convertInternal(buf, f.arg2(), fPrecedence, false);
      fFactory.tagEnd(buf, "munder");
      return true;
    }
  }

  /** <code>Overscript[x, over]</code> as the MathML <code>mover</code> element. */
  private static final class Overscript extends MMLOperator {

    public Overscript() {
      super(0, "mover", "");
    }

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      fFactory.tagStart(buf, "mover");
      fFactory.convertInternal(buf, f.arg1(), fPrecedence, false);
      fFactory.convertInternal(buf, f.arg2(), fPrecedence, false);
      fFactory.tagEnd(buf, "mover");
      return true;
    }
  }

  /**
   * <code>Underoverscript[x, under, over]</code> as the MathML <code>munderover</code> element.
   */
  private static final class Underoverscript extends MMLOperator {

    public Underoverscript() {
      super(0, "munderover", "");
    }

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      // munderover takes exactly a base, an underscript and an overscript
      if (f.size() != 4) {
        return false;
      }
      fFactory.tagStart(buf, "munderover");
      fFactory.convertInternal(buf, f.arg1(), fPrecedence, false);
      fFactory.convertInternal(buf, f.arg2(), fPrecedence, false);
      fFactory.convertInternal(buf, f.arg3(), fPrecedence, false);
      fFactory.tagEnd(buf, "munderover");
      return true;
    }
  }

  /** <code>Subsuperscript[x, sub, sup]</code> as the MathML <code>msubsup</code> element. */
  private static final class Subsuperscript extends MMLOperator {

    public Subsuperscript() {
      super(0, "msubsup", "");
    }

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      // msubsup takes exactly a base, a subscript and a superscript
      if (f.size() != 4) {
        return false;
      }
      fFactory.tagStart(buf, "msubsup");
      fFactory.convertInternal(buf, f.arg1(), fPrecedence, false);
      fFactory.convertInternal(buf, f.arg2(), fPrecedence, false);
      fFactory.convertInternal(buf, f.arg3(), fPrecedence, false);
      fFactory.tagEnd(buf, "msubsup");
      return true;
    }
  }

  /**
   * <code>Style[expr, directives...]</code> as an <code>mstyle</code> element.
   *
   * <p>
   * A directive that is not understood is skipped rather than making the whole conversion fail: the
   * expression itself still has to be shown. Without this the browser would print the literal
   * <code>Style[3.14159, Small]</code> instead of the number.
   */
  private static final class Style extends MMLOperator {

    public Style() {
      super(0, "mstyle", "");
    }

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() < 2) {
        return false;
      }
      StringBuilder attributes = new StringBuilder();
      StringBuilder css = new StringBuilder();
      for (int i = 2; i < f.size(); i++) {
        appendStyleDirective(attributes, css, f.get(i));
      }
      if (css.length() > 0) {
        attributes.append(" style=\"").append(css).append("\"");
      }

      if (attributes.length() == 0) {
        // nothing to apply: show the expression on its own
        fFactory.convertInternal(buf, f.arg1(), precedence, false);
        return true;
      }
      fFactory.tagStart(buf, "mstyle", attributes.toString().trim());
      fFactory.convertInternal(buf, f.arg1(), Precedence.NO_PRECEDENCE, false);
      fFactory.tagEnd(buf, "mstyle");
      return true;
    }

    /** Translate one <code>Style</code> directive into MathML attributes or CSS. */
    private static void appendStyleDirective(StringBuilder attributes, StringBuilder css,
        IExpr directive) {
      String color = colorOf(directive);
      if (color != null) {
        attributes.append(" mathcolor=\"").append(color).append("\"");
        return;
      }
      if (directive.isSymbol()) {
        String name = ((ISymbol) directive).getSymbolName();
        String size = fontSizeOf(name);
        if (size != null) {
          attributes.append(" mathsize=\"").append(size).append("\"");
          return;
        }
        // MathML Core dropped the general mathvariant, so the face is set with CSS
        if (name.equalsIgnoreCase("Bold")) {
          css.append("font-weight:bold;");
        } else if (name.equalsIgnoreCase("Italic")) {
          css.append("font-style:italic;");
        } else if (name.equalsIgnoreCase("Plain")) {
          css.append("font-weight:normal;font-style:normal;");
        } else if (name.equalsIgnoreCase("Underlined")) {
          css.append("text-decoration:underline;");
        }
        return;
      }
      if (directive.isReal()) {
        // a bare number is a font size in printer's points
        attributes.append(" mathsize=\"").append(directive.evalf()).append("pt\"");
        return;
      }
      if (directive.isAST(S.FontSize, 2) || directive.isRule()) {
        IExpr value = ((IAST) directive).arg2();
        if (directive.isRule() && ((IAST) directive).arg1() != S.FontSize) {
          return;
        }
        if (value.isReal()) {
          attributes.append(" mathsize=\"").append(value.evalf()).append("pt\"");
        }
      }
    }

    /** The named sizes, as a length: MathML Core no longer knows "small" and "big". */
    private static String fontSizeOf(String name) {
      if (name.equalsIgnoreCase("Tiny")) {
        return "0.6em";
      }
      if (name.equalsIgnoreCase("Small")) {
        return "0.8em";
      }
      if (name.equalsIgnoreCase("Medium")) {
        return "1em";
      }
      if (name.equalsIgnoreCase("Large")) {
        return "1.4em";
      }
      if (name.equalsIgnoreCase("Huge")) {
        return "1.8em";
      }
      return null;
    }
  }

  /**
   * The <code>#rrggbb</code> form of a colour directive, or <code>null</code> when the expression
   * is not a colour. Colours reach this point as <code>RGBColor</code>, because a named colour such
   * as <code>Red</code> evaluates to one.
   */
  private static String colorOf(IExpr expr) {
    if (expr.isAST(S.RGBColor, 4, 5)) {
      IAST rgb = (IAST) expr;
      return hexColor(rgb.arg1(), rgb.arg2(), rgb.arg3());
    }
    if (expr.isAST(S.GrayLevel, 2)) {
      IExpr level = ((IAST) expr).arg1();
      return hexColor(level, level, level);
    }
    return null;
  }

  private static String hexColor(IExpr red, IExpr green, IExpr blue) {
    if (!red.isReal() || !green.isReal() || !blue.isReal()) {
      return null;
    }
    return String.format("#%02x%02x%02x", channel(red), channel(green), channel(blue));
  }

  private static int channel(IExpr value) {
    int scaled = (int) Math.round(value.evalf() * 255.0);
    return scaled < 0 ? 0 : (scaled > 255 ? 255 : scaled);
  }

  private static final class Superscript extends MMLOperator {

    public Superscript() {
      super(0, "msup", "");
    }

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      IExpr arg1 = f.arg1();
      IExpr arg2 = f.arg2();
      fFactory.tagStart(buf, "msup");
      fFactory.convertInternal(buf, arg1, fPrecedence, false);
      fFactory.convertInternal(buf, arg2, fPrecedence, false);
      fFactory.tagEnd(buf, "msup");
      return true;
    }
  }

  private static class Sum extends AbstractConverter {

    public Sum() {}

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() >= 3) {
        // &sum; &#x2211
        return iteratorStep(buf, "&#x2211;", f, 2);
      }
      return false;
    }

    public boolean iteratorStep(final StringBuilder buf, final String mathSymbol, final IAST f,
        int i) {
      if (i >= f.size()) {
        fFactory.convertInternal(buf, f.arg1(), Integer.MIN_VALUE, false);
        return true;
      }
      fFactory.tagStart(buf, "mrow");
      if (f.get(i).isList()) {
        try {
          IIterator<IExpr> iterator = Iterator.create((IAST) f.get(i), i, EvalEngine.get());
          if (iterator.isValidVariable() && iterator.getStep().isOne()) {
            fFactory.tagStart(buf, "munderover");
            fFactory.tag(buf, "mo", mathSymbol);

            fFactory.tagStart(buf, "mrow");
            fFactory.convertSymbol(buf, iterator.getVariable());
            fFactory.tag(buf, "mo", "=");
            fFactory.convertInternal(buf, iterator.getLowerLimit(), Integer.MIN_VALUE, false);
            fFactory.tagEnd(buf, "mrow");
            fFactory.convertInternal(buf, iterator.getUpperLimit(), Integer.MIN_VALUE, false);
            fFactory.tagEnd(buf, "munderover");
            if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
              return false;
            }
            fFactory.tagEnd(buf, "mrow");
            return true;
          }
        } catch (final ArgumentTypeException e) {
          return false;
        }
      } else if (f.get(i).isSymbol()) {
        ISymbol symbol = (ISymbol) f.get(i);
        fFactory.tagStart(buf, "munderover");
        fFactory.tag(buf, "mo", mathSymbol);
        fFactory.tagStart(buf, "mrow");
        fFactory.convertSymbol(buf, symbol);
        fFactory.tagEnd(buf, "mrow");
        // empty <mi> </mi>
        fFactory.tagStart(buf, "mi");
        fFactory.tagEnd(buf, "mi");

        fFactory.tagEnd(buf, "munderover");
        if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
          return false;
        }
        fFactory.tagEnd(buf, "mrow");
        return true;
      }
      return false;
    }
  }

  private static final class Times extends MMLOperator {

    public Times() {
      super(Precedence.TIMES, "mrow", "&#0183;"); // centerdot instead
      // of
      // invisibletimes:
      // "&#8290;");
    }

    /**
     * Converts a given function into the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The math function which should be converted to MathML
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      return convertTimesFraction(buf, f, precedence, MathMLFormFactory.NO_PLUS_CALL);
    }

    /**
     * Try to split a given <code>Times[...]</code> function into numerator and denominator and add
     * the corresponding MathML output
     *
     * @param buf StringBuilder for MathML output
     * @param f The function which should be converted to MathML
     * @precedence
     * @caller
     */
    public boolean convertTimesFraction(final StringBuilder buf, final IAST f, final int precedence,
        final boolean caller) {
      Optional<IExpr[]> parts =
          AlgebraUtil.fractionalPartsTimesPower(f, false, true, false, false, false, false);
      if (parts.isEmpty()) {
        convertTimesOperator(buf, f, precedence, false, caller);
        return true;
      }
      final IExpr numerator = parts.get()[0];
      final IExpr denominator = parts.get()[1];
      precedenceOpen(buf, precedence);
      if (!denominator.isOne()) {
        boolean arg1Negate = false;
        // found a fraction expression
        if (caller == MathMLFormFactory.PLUS_CALL) {
          if (numerator.isTimes() && numerator.first().isReal() && numerator.first().isNegative()) {
            fFactory.tag(buf, "mo", "-");
            arg1Negate = true;
          } else {
            fFactory.tag(buf, "mo", "+");
          }
        }
        fFactory.tagStart(buf, "mfrac");
        // insert numerator in buffer:
        if (numerator.isTimes()) {
          convertTimesOperator(buf, (IAST) numerator, Integer.MIN_VALUE, arg1Negate,
              MathMLFormFactory.NO_PLUS_CALL);
        } else {
          fFactory.convertInternal(buf, numerator, Integer.MIN_VALUE, false);
        }
        if (denominator.isTimes()) {
          convertTimesOperator(buf, (IAST) denominator, Integer.MIN_VALUE, false,
              MathMLFormFactory.NO_PLUS_CALL);
        } else {
          fFactory.convertInternal(buf, denominator, Integer.MIN_VALUE, false);
        }
        fFactory.tagEnd(buf, "mfrac");
      } else {
        // if (numerator.size() <= 2) {
        if (numerator.isTimes()) {
          convertTimesOperator(buf, (IAST) numerator, precedence, false, caller);
        } else {
          convertTimesOperator(buf, f, precedence, false, caller);
        }
      }
      precedenceClose(buf, precedence);
      return true;
    }

    /**
     * Converts a given <code>Times[...]</code> function into the corresponding MathML output.
     *
     * @param buf
     * @param timesAST
     * @param precedence
     * @param arg1Negate negate first argument, because negative sign is already printed
     * @param caller
     * @return
     */
    private boolean convertTimesOperator(final StringBuilder buf, final IAST timesAST,
        final int precedence, final boolean arg1Negate, final boolean caller) {
      int size = timesAST.size();
      boolean noPrecedenceOpenCall = false;
      if (size > 1) {
        IExpr arg1 = timesAST.arg1();
        if (arg1Negate) {
          arg1 = arg1.negate();
        }
        if (arg1.isMinusOne()) {
          if (size == 2) {
            fFactory.tagStart(buf, fFirstTag);
            precedenceOpen(buf, precedence);
            fFactory.convertInternal(buf, arg1, fPrecedence, false);
          } else {
            if (caller == MathMLFormFactory.NO_PLUS_CALL) {
              noPrecedenceOpenCall = true;
              fFactory.tagStart(buf, fFirstTag);
              fFactory.tag(buf, "mo", "-");
              if (size == 3) {
                fFactory.convertInternal(buf, timesAST.arg2(), fPrecedence, false);
                fFactory.tagEnd(buf, fFirstTag);
                return true;
              }
              // fFactory.tagEnd(buf, fFirstTag);
              // fFactory.tagStart(buf, fFirstTag);
            } else {
              fFactory.tagStart(buf, fFirstTag);
              precedenceOpen(buf, precedence);
              fFactory.tag(buf, "mo", "-");
            }
          }
        } else if (arg1.isOne()) {
          if (size == 2) {
            fFactory.tagStart(buf, fFirstTag);
            precedenceOpen(buf, precedence);
            fFactory.convertInternal(buf, arg1, fPrecedence, false);
          } else {
            if (caller == MathMLFormFactory.PLUS_CALL) {
              if (size == 3) {
                fFactory.convertInternal(buf, timesAST.arg2(), fPrecedence, false);
                return true;
              }
              fFactory.tagStart(buf, fFirstTag);
            } else {
              fFactory.tagStart(buf, fFirstTag);
              precedenceOpen(buf, precedence);
            }
          }
        } else {
          if (caller == MathMLFormFactory.PLUS_CALL) {
            if (arg1.isReal() && arg1.isNegative()) {
              fFactory.tag(buf, "mo", "-");
              fFactory.tagStart(buf, fFirstTag);
              arg1 = ((IReal) arg1).negate();
            } else {
              fFactory.tag(buf, "mo", "+");
              fFactory.tagStart(buf, fFirstTag);
            }
          } else {
            fFactory.tagStart(buf, fFirstTag);
            precedenceOpen(buf, precedence);
          }
          fFactory.convertInternal(buf, arg1, fPrecedence, false);
          if (fOperator.length() > 0) {
            fFactory.tag(buf, "mo", fOperator);
          }
        }
      }

      for (int i = 2; i < size; i++) {
        fFactory.convertInternal(buf, timesAST.get(i), fPrecedence, false);
        if ((i < timesAST.argSize()) && (fOperator.length() > 0)) {
          fFactory.tag(buf, "mo", fOperator);
        }
      }
      if (!noPrecedenceOpenCall) {
        precedenceClose(buf, precedence);
      }
      fFactory.tagEnd(buf, fFirstTag);
      return true;
    }
  }

  /** The conversion wasn't called with an operator preceding the <code>IExpr</code> object. */
  public static final boolean NO_PLUS_CALL = false;

  /**
   * The conversion was called with a &quot;+&quot; operator preceding the <code>IExpr</code>
   * object.
   */
  public static final boolean PLUS_CALL = true;

  public static final Map<ISymbol, IConverter> CONVERTERS =
      new IdentityHashMap<ISymbol, IConverter>(199);

  private static final TrieBuilder<String, Object, ArrayList<Object>> constantBuilder =
      TrieBuilder.create();
  /** Table for constant symbols */
  public static final Map<String, Object> CONSTANT_SYMBOLS =
      constantBuilder.withMatch(TrieMatch.EXACT).build();

  private static final TrieBuilder<String, AbstractConverter, ArrayList<AbstractConverter>> converterBuilder =
      TrieBuilder.create();

  /** Description of the Field */
  public static final Map<String, AbstractConverter> OPERATORS =
      converterBuilder.withMatch(TrieMatch.EXACT).build();

  /** Table for constant expressions */
  public static final Map<IExpr, String> CONSTANT_EXPRS = new HashMap<IExpr, String>();

  /**
   * If <code>true</code> use &quot;(&quot; and &quot;)&quot; as parenthesis for function arguments;
   * otherwise use &quot;[&quot; and &quot;]&quot;
   */
  private final boolean fRelaxedSyntax;

  /**
   * Write the arguments of a {@link S#Plus} expression in reversed order.
   */
  private final boolean fPlusReversed;

  private boolean fUseSignificantFigures = false;
  private int fExponentFigures;
  private int fSignificantFigures;

  /**
   * Installed while converting the argument of a number form wrapper; <code>null</code> otherwise.
   */
  NumberFormatter fNumberFormatter = null;

  /** Constructor */
  public MathMLFormFactory() {
    this("", null, -1, -1);
  }

  public MathMLFormFactory(final String tagPrefix) {
    this(tagPrefix, null, -1, -1);
  }

  public MathMLFormFactory(final String tagPrefix, NumberFormat numberFormat, int exponentFigures,
      int significantFigures) {
    super(tagPrefix, numberFormat);
    fRelaxedSyntax = true;
    fPlusReversed = false;
    fExponentFigures = exponentFigures;
    fSignificantFigures = significantFigures;
    init();
  }

  public MathMLFormFactory(final String tagPrefix, boolean relaxedSyntax, NumberFormat numberFormat,
      int exponentFigures, int significantFigures) {
    this(tagPrefix, relaxedSyntax, false, numberFormat, exponentFigures, significantFigures);
  }

  public MathMLFormFactory(final String tagPrefix, boolean relaxedSyntax, boolean plusReversed,
      NumberFormat numberFormat, int exponentFigures, int significantFigures) {
    super(tagPrefix, numberFormat);
    fRelaxedSyntax = relaxedSyntax;
    fPlusReversed = plusReversed;
    fExponentFigures = exponentFigures;
    fSignificantFigures = significantFigures;
    init();
  }

  @Override
  public boolean convert(final StringBuilder buf, final IExpr o, final int precedence,
      boolean isASTHead) {
    try {
      convertInternal(buf, o, precedence, isASTHead);
      if (buf.length() >= Config.MAX_OUTPUT_SIZE) {
        return false;
      }
      return true;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    } catch (OutOfMemoryError oome) {
    }
    return false;
  }

  @Override
  void convertInternal(final StringBuilder buf, final IExpr o, final int precedence,
      boolean isASTHead) {
    String str = CONSTANT_EXPRS.get(o);
    if (str != null) {
      buf.append(str);
      return;
    }
    if (o instanceof IAST) {
      final IAST f = ((IAST) o);
      IAST ast = f;
      IAST temp;
      if (f.topHead().hasFlatAttribute()) {
        // associative
        if ((temp = EvalAttributes.flattenDeep(f)).isPresent()) {
          ast = temp;
        }
      }
      IExpr h = ast.head();
      if (h.isSymbol()) {
        IConverter converter = CONVERTERS.get(h);
        if (converter != null) {
          converter.setFactory(this);
          StringBuilder sb = new StringBuilder();
          if (converter.convert(sb, ast, precedence)) {
            buf.append(sb);
            return;
          }
        }
      }
      convertAST(buf, ast, 0);
      return;
    }
    if (convertNumber(buf, o, precedence, NO_PLUS_CALL)) {
      return;
    }
    if (o instanceof ISymbol) {
      convertSymbol(buf, (ISymbol) o);
      return;
    }
    convertString(buf, o.toString());
  }

  public void convertApcomplex(final StringBuilder buf, final Apcomplex ac, final int precedence) {
    Apfloat realPart = ac.real();
    Apfloat imaginaryPart = ac.imag();
    final boolean isImNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;

    tagStart(buf, "mrow");
    if (Precedence.PLUS < precedence) {
      tag(buf, "mo", "(");
    }
    // convertApfloat(buf, realPart);
    buf.append(convertApfloatToFormattedString(realPart));
    if (isImNegative) {
      tag(buf, "mo", "-");
      imaginaryPart = imaginaryPart.negate();
    } else {
      tag(buf, "mo", "+");
    }

    // convertApfloat(buf, imaginaryPart);
    buf.append(convertApfloatToFormattedString(imaginaryPart));

    // <!ENTITY InvisibleTimes "&#x2062;" >
    // <!ENTITY CenterDot "&#0183;" >
    tag(buf, "mo", "&#0183;");
    // <!ENTITY ImaginaryI "&#x2148;" >
    tag(buf, "mi", "&#x2148;"); // "&#x2148;");
    if (Precedence.PLUS < precedence) {
      tag(buf, "mo", ")");
    }
    tagEnd(buf, "mrow");
  }

  public void convertApfloat(final StringBuilder buf, final Apfloat realPart,
      final int precedence) {
    buf.append(convertApfloatToFormattedString(realPart));
    // convertApfloat(buf, realPart);
  }

  private String convertApfloatToFormattedString(Apfloat value) {
    if (fNumberFormatter != null) {
      String markup = formattedNumberMarkup(fNumberFormatter.format(value, fSignificantFigures));
      if (markup != null) {
        return markup;
      }
    }
    StringBuilder buf = new StringBuilder();
    int numericPrecision = (int) EvalEngine.get().getNumericPrecision();
    ApfloatToMMA.apfloatToMathML(buf, value, numericPrecision, numericPrecision,
        fUseSignificantFigures);
    return buf.toString();
  }

  /**
   * Append a <code>double</code>, honouring an installed {@link NumberFormatter}.
   */
  private void appendFormattedDouble(final StringBuilder buf, double value) {
    if (fNumberFormatter != null) {
      String markup = formattedNumberMarkup(fNumberFormatter.format(value, fSignificantFigures));
      if (markup != null) {
        buf.append(markup);
        return;
      }
    }
    tagStart(buf, "mn");
    buf.append(convertDoubleToFormattedString(value));
    tagEnd(buf, "mn");
  }

  /**
   * Render a {@link NumberFormatter.FormattedNumber} as MathML markup.
   *
   * @return <code>null</code> if the value could not be formatted and the caller should fall back
   *         to the default formatting
   */
  private String formattedNumberMarkup(NumberFormatter.FormattedNumber formatted) {
    if (formatted == null) {
      return null;
    }
    StringBuilder buf = new StringBuilder();
    if (formatted.custom.isPresent()) {
      NumberFormatter previous = fNumberFormatter;
      fNumberFormatter = null;
      try {
        convertInternal(buf, formatted.custom, Integer.MIN_VALUE, false);
        return buf.toString();
      } finally {
        fNumberFormatter = previous;
      }
    }
    if (!formatted.scientific) {
      tagStart(buf, "mn");
      buf.append(formatted.mantissa);
      tagEnd(buf, "mn");
      return buf.toString();
    }
    tagStart(buf, "mrow");
    tagStart(buf, "mn");
    buf.append(formatted.mantissa);
    tagEnd(buf, "mn");
    // <!ENTITY times "&#0215;" >
    tag(buf, "mo", "&#0215;");
    tagStart(buf, "msup");
    tag(buf, "mn", "10");
    tag(buf, "mn", Integer.toString(formatted.exponent));
    tagEnd(buf, "msup");
    tagEnd(buf, "mrow");
    return buf.toString();
  }
  // public void convertApfloat(StringBuilder buf, Apfloat num) {
  // String str = num.toString();
  // int index = str.indexOf('e');
  // if (index > 0) {
  // String result = str.substring(0, index);
  // String exponentStr = str.substring(index + 1);
  //
  // tagStart(buf, "mrow");
  // tagStart(buf, "mn");
  // buf.append(result);
  // tagEnd(buf, "mn");
  // tagStart(buf, "mo");
  // // dot operator for multiplication
  // buf.append("&#0183;");
  // tagEnd(buf, "mo");
  // tagStart(buf, "msup");
  // tagStart(buf, "mn");
  // buf.append("10");
  // tagEnd(buf, "mn");
  // tagStart(buf, "mn");
  // buf.append(exponentStr);
  // tagEnd(buf, "mn");
  // tagEnd(buf, "msup");
  // tagEnd(buf, "mrow");
  // return;
  // }
  // tagStart(buf, "mn");
  // buf.append(str);
  // tagEnd(buf, "mn");
  // }

  public void convertArgs(final StringBuilder buf, IExpr head, final IAST function) {
    tagStart(buf, "mrow");
    if (head.isAST() || !fRelaxedSyntax) {
      // append(buf, "[");
      tag(buf, "mo", "[");
    } else {
      // append(buf, "(");
      tag(buf, "mo", "(");
    }
    final int functionSize = function.size();
    if (functionSize > 1) {
      convertInternal(buf, function.arg1(), Integer.MIN_VALUE, false);
    }
    for (int i = 2; i < functionSize; i++) {
      tag(buf, "mo", ",");
      convertInternal(buf, function.get(i), Integer.MIN_VALUE, false);
    }
    if (head.isAST() || !fRelaxedSyntax) {
      // append(buf, "]");
      tag(buf, "mo", "]");
    } else {
      // append(buf, ")");
      tag(buf, "mo", ")");
    }
    tagEnd(buf, "mrow");
  }

  private void convertAST(final StringBuilder buf, final IAST ast, final int precedence) {
    final IAST list = ast;

    IExpr header = list.head();
    if (!header.isSymbol()) {
      // print expressions like: f(#1, y)& [x]

      IAST[] derivStruct = list.isDerivativeAST1();
      if (derivStruct != null) {
        IAST a1Head = derivStruct[0];
        IAST headAST = derivStruct[1];
        if (a1Head.isAST1() && headAST.isAST1()
            && (headAST.arg1().isSymbol() || headAST.arg1().isAST())) {
          try {
            int n = a1Head.arg1().toIntDefault();
            if (n == 1 || n == 2) {
              tagStart(buf, "mrow");
              tagStart(buf, "msup");
              IExpr symbolOrAST = headAST.arg1();
              convertInternal(buf, symbolOrAST, Integer.MAX_VALUE, false);
              if (n == 1) {
                tag(buf, "mo", "&#8242;");
              } else if (n == 2) {
                tag(buf, "mo", "&#8242;&#8242;");
              }
              tagEnd(buf, "msup");
              if (derivStruct[2] != null) {
                convertArgs(buf, symbolOrAST, list);
              }
              tagEnd(buf, "mrow");
              return;
            }

            tagStart(buf, "mrow");
            IExpr symbolOrAST = headAST.arg1();
            tagStart(buf, "msup");
            convertInternal(buf, symbolOrAST, Integer.MAX_VALUE, false);
            tagStart(buf, "mrow");
            tag(buf, "mo", "(");
            // supscript "(n)"
            convertInternal(buf, a1Head.arg1(), Integer.MIN_VALUE, false);
            tag(buf, "mo", ")");
            tagEnd(buf, "mrow");
            tagEnd(buf, "msup");
            if (derivStruct[2] != null) {
              convertArgs(buf, symbolOrAST, list);
            }
            tagEnd(buf, "mrow");
            return;

          } catch (ArithmeticException ae) {

          }
        }
      }

      convertInternal(buf, header, Integer.MIN_VALUE, false);
      convertFunctionArgs(buf, list);
      return;
    }

    ISymbol head = list.topHead();
    final org.matheclipse.parser.client.operator.Operator operator =
        OutputFormFactory.getOperator(head, list.argSize());
    if (operator != null) {
      if (operator instanceof PostfixOperator) {
        if (list.isAST1()) {
          // The enclosing expression's precedence, not the operator's own: passing its own made
          // the "parenthesize when the context binds tighter" test true for every prefix and
          // postfix operator reached here, so Del(f) came out as (\u2207f) and Increment(a) as
          // (a++). This is the precedence OutputFormFactory passes at the same point.
          convertPostfixOperator(buf, list, (PostfixOperator) operator, precedence);
          return;
        }
      } else {
        if (convertOperator(operator, list, buf, precedence, head)) {
          return;
        }
      }
    }
    if (list instanceof ASTSeriesData) {
      if (convertSeriesData(buf, (ASTSeriesData) list, precedence)) {
        return;
      }
    }
    if (list.isList() || list instanceof ASTRealVector || list instanceof ASTRealMatrix) {
      convertList(buf, list);
      return;
    }
    if (list.isAST(S.Parenthesis)) {
      convertArgs(buf, S.Parenthesis, list);
      return;
    }
    if (list.isInterval() && convertInterval(buf, list)) {
      return;
    }
    if (list.isAssociation()) {
      convertAssociation(buf, (IAssociation) list);
      return;
    }
    int functionID = ((ISymbol) list.head()).ordinal();
    if (functionID > ID.UNKNOWN) {
      switch (functionID) {
        case ID.Inequality:
          if (list.size() > 3 && convertInequality(buf, list, precedence)) {
            return;
          }
          break;
        case ID.Quantity:
          if (list.isAST2() && org.matheclipse.core.units.Units.isKnownUnit(list.arg2())) {
            // magnitude, invisible-times, unit abbreviation as text
            tagStart(buf, "mrow");
            convertInternal(buf, list.arg1(), Precedence.TIMES, false);
            tag(buf, "mo", "&#x2062;");
            tag(buf, "mtext",
                org.matheclipse.core.units.Units.renderUnit(list.arg2(), "Abbreviation"));
            tagEnd(buf, "mrow");
            return;
          }
          break;
        case ID.Part:
          if ((list.size() >= 3)) {
            convertPart(buf, list);
            return;
          }
          break;
        case ID.Slot:
          if ((list.isAST1()) && (list.arg1() instanceof IInteger)) {
            convertSlot(buf, list);
            return;
          }
          break;
        case ID.SlotSequence:
          if ((list.isAST1()) && (list.arg1() instanceof IInteger)) {
            convertSlotSequence(buf, list);
            return;
          }
          break;
        case ID.SparseArray:
          if (list.isSparseArray()) {
            tagStart(buf, "mtext");
            buf.append(list.toString());
            tagEnd(buf, "mtext");
            return;
          }
          break;
        case ID.Defer:
        case ID.HoldForm:
          if ((list.isAST1())) {
            convertInternal(buf, list.arg1(), precedence, false);
            return;
          }
          break;
        case ID.DirectedInfinity:
          if (list.isDirectedInfinity()) { // head.equals(F.DirectedInfinity))
            if (list.isAST0()) {
              convertSymbol(buf, S.ComplexInfinity);
              return;
            }
            if (list.isAST1()) {
              if (list.arg1().isOne()) {
                convertSymbol(buf, S.Infinity);
                return;
              } else if (list.arg1().isMinusOne()) {
                convertInternal(buf, F.Times(F.CN1, S.Infinity), precedence, false);
                return;
              } else if (list.arg1().isImaginaryUnit()) {
                convertInternal(buf, F.Times(F.CI, S.Infinity), precedence, false);
                return;
              } else if (list.arg1().isNegativeImaginaryUnit()) {
                convertInternal(buf, F.Times(F.CNI, S.Infinity), precedence, false);
                return;
              }
            }
          }
          break;
      }
    }
    // if (head.equals(F.SeriesData) && (list.size() == 7)) {
    // if (convertSeriesData(buf, list, precedence)) {
    // return;
    // }

    tagStart(buf, "mrow");
    convertHead(buf, ast.head());
    // &af; &#x2061;
    // tag(buf, "mo", "&#x2061;");
    tagStart(buf, "mrow");
    if (fRelaxedSyntax) {
      tag(buf, "mo", "(");
    } else {
      tag(buf, "mo", "[");
    }
    tagStart(buf, "mrow");
    for (int i = 1; i < ast.size(); i++) {
      convertInternal(buf, ast.get(i), Integer.MIN_VALUE, false);
      if (i < ast.argSize()) {
        tag(buf, "mo", ",");
      }
    }
    tagEnd(buf, "mrow");
    if (fRelaxedSyntax) {
      tag(buf, "mo", ")");
    } else {
      tag(buf, "mo", "]");
    }
    tagEnd(buf, "mrow");
    tagEnd(buf, "mrow");
  }

  private boolean convertInequality(final StringBuilder buf, final IAST inequality,
      final int precedence) {
    StringBuilder tempBuffer = new StringBuilder();

    tagStart(tempBuffer, "mrow");
    if (Precedence.EQUAL < precedence) {
      // append(buf, "(");
      tag(tempBuffer, "mo", "(");
    }

    final int listSize = inequality.size();
    int i = 1;
    while (i < listSize) {
      convertInternal(tempBuffer, inequality.get(i++), Precedence.EQUAL, false);
      if (i == listSize) {
        if (Precedence.EQUAL < precedence) {
          // append(buf, ")");
          tag(tempBuffer, "mo", ")");
        }
        tagEnd(tempBuffer, "mrow");
        buf.append(tempBuffer);
        return true;
      }
      IExpr head = inequality.get(i++);
      if (head.isBuiltInSymbol()) {
        int id = ((IBuiltInSymbol) head).ordinal();
        switch (id) {
          case ID.Equal:
            tag(tempBuffer, "mo", "==");
            break;
          case ID.Greater:
            tag(tempBuffer, "mo", "&gt;");
            break;
          case ID.GreaterEqual:
            tag(tempBuffer, "mo", "&gt;=");
            break;
          case ID.Less:
            tag(tempBuffer, "mo", "&lt;");
            break;
          case ID.LessEqual:
            tag(tempBuffer, "mo", "&lt;=");
            break;
          case ID.Unequal:
            tag(tempBuffer, "mo", "!=");
            break;
          default:
            return false;
        }
      } else {
        return false;
      }
    }
    if (Precedence.EQUAL < precedence) {
      // append(buf, ")");
      tag(tempBuffer, "mo", ")");
    }
    tagEnd(tempBuffer, "mrow");
    buf.append(tempBuffer);
    return true;
  }

  @Override
  public void convertComplex(final StringBuilder buf, final IComplex c, final int precedence,
      boolean caller) {
    boolean isReZero = c.getRealPart().isZero();

    IRational imaginaryPart = c.getImaginaryPart();
    final boolean isImOne = imaginaryPart.isOne();
    final boolean isImNegative = imaginaryPart.isNegative();
    final boolean isImMinusOne = imaginaryPart.isMinusOne();
    if (isReZero && isImOne) {
      tagStart(buf, "mrow");
      // <!ENTITY ImaginaryI "&#x2148;"
      tag(buf, "mi", "&#x2148;");
      tagEnd(buf, "mrow");
      return;
    }
    tagStart(buf, "mrow");
    if (!isReZero && (Precedence.PLUS < precedence)) {
      tag(buf, "mo", "(");
    }
    if (!isReZero) {
      convertFraction(buf, c.getRealPart(), Precedence.PLUS, caller);
    }

    if (isImOne) {
      tagStart(buf, "mrow");
      if (isReZero) {
        if (caller == PLUS_CALL) {
          tag(buf, "mo", "+");
        }
        // <!ENTITY ImaginaryI "&#x2148;"
        tag(buf, "mi", "&#x2148;");
      } else {
        tag(buf, "mo", "+");
        // <!ENTITY ImaginaryI "&#x2148;"
        tag(buf, "mi", "&#x2148;");
      }
    } else if (isImMinusOne) {
      tagStart(buf, "mrow");
      tag(buf, "mo", "-");
      // <!ENTITY ImaginaryI "&#x2148;"
      tag(buf, "mi", "&#x2148;");
    } else {
      tagStart(buf, "mrow");
      if (isImNegative) {
        imaginaryPart = imaginaryPart.negate();
      }
      if (!isReZero) {
        if (isImNegative) {
          tag(buf, "mo", "-");
        } else {
          tag(buf, "mo", "+");
        }
      } else {
        if (caller == PLUS_CALL) {
          tag(buf, "mo", "+");
        }
        if (isImNegative) {
          tag(buf, "mo", "-");
        }
      }
      convertFraction(buf, imaginaryPart, Precedence.TIMES, caller);
      // <!ENTITY InvisibleTimes "&#x2062;" >
      // <!ENTITY CenterDot "&#0183;" >
      tag(buf, "mo", "&#0183;");
      // <!ENTITY ImaginaryI "&#x2148;"
      tag(buf, "mi", "&#x2148;");
    }
    tagEnd(buf, "mrow");
    if (!isReZero && (Precedence.PLUS < precedence)) {
      tag(buf, "mo", ")");
    }
    tagEnd(buf, "mrow");
  }

  @Override
  public void convertDouble(final StringBuilder buf, final INum d, final int precedence,
      boolean caller) {
    if (d instanceof Num && F.isZero(d.doubleValue(), Config.ZERO_IN_OUTPUT_FORMAT)) {
      appendFormattedDouble(buf, 0.0);
      return;
    }
    final boolean isNegative = d.isNegative();
    if (isNegative && (precedence > Precedence.PLUS)) {
      tagStart(buf, "mrow");
      tag(buf, "mo", "(");
    }

    if (d instanceof ApfloatNum) {
      convertApfloat(buf, d.apfloatValue(), precedence);
    } else {
      appendFormattedDouble(buf, d.getRealPart());
    }

    if (isNegative && (precedence > Precedence.PLUS)) {
      tag(buf, "mo", ")");
      tagEnd(buf, "mrow");
    }
  }

  @Override
  public void convertDoubleComplex(final StringBuilder buf, final IComplexNum dc,
      final int precedence, boolean caller) {
    if (dc instanceof ApcomplexNum) {
      convertApcomplex(buf, ((ApcomplexNum) dc).apcomplexValue(), precedence);
      return;
    }
    double realPart = dc.getRealPart();
    double imaginaryPart = dc.getImaginaryPart();
    final boolean isImNegative = imaginaryPart < 0;

    tagStart(buf, "mrow");
    if (Precedence.PLUS < precedence) {
      tag(buf, "mo", "(");
    }
    appendFormattedDouble(buf, realPart);
    if (isImNegative) {
      tag(buf, "mo", "-");
      imaginaryPart *= (-1);
    } else {
      tag(buf, "mo", "+");
    }
    appendFormattedDouble(buf, imaginaryPart);

    // <!ENTITY InvisibleTimes "&#x2062;" >
    // <!ENTITY CenterDot "&#0183;" >
    tag(buf, "mo", "&#0183;");
    // <!ENTITY ImaginaryI "&#x2148;" >
    tag(buf, "mi", "&#x2148;"); // "&#x2148;");
    if (Precedence.PLUS < precedence) {
      tag(buf, "mo", ")");
    }
    tagEnd(buf, "mrow");
  }

  @Override
  protected String convertDoubleToFormattedString(double dValue) {
    if (fSignificantFigures > 0) {
      try {
        StringBuilder buf = new StringBuilder();
        DoubleToMMA.doubleToMMA(buf, dValue, fExponentFigures, fSignificantFigures, false,
            fRelaxedSyntax);
        return buf.toString();
      } catch (IOException ioex) {
      }
    }
    return Double.toString(dValue);
  }

  public void convertFraction(final StringBuilder buf, final BigInteger n, BigInteger denominator,
      final int precedence, boolean caller) {
    boolean isInteger = denominator.compareTo(BigInteger.ONE) == 0;
    BigInteger numerator = n;
    final boolean isNegative = numerator.signum() < 0;
    if (isNegative) {
      numerator = numerator.negate();
    }
    final int prec = isNegative ? Precedence.PLUS : Precedence.TIMES;
    tagStart(buf, "mrow");
    if (!isNegative) {
      if (caller == PLUS_CALL) {
        tag(buf, "mo", "-");
      }
    } else {
      tag(buf, "mo", "-");
    }
    if (prec < precedence) {
      tag(buf, "mo", "(");
    }

    String str = numerator.toString();
    if (!isInteger) {
      tagStart(buf, "mfrac");
      tagStart(buf, "mn");
      buf.append(str);
      tagEnd(buf, "mn");
      tagStart(buf, "mn");
      str = denominator.toString();
      buf.append(str);
      tagEnd(buf, "mn");
      tagEnd(buf, "mfrac");
    } else {
      tagStart(buf, "mn");
      buf.append(str);
      tagEnd(buf, "mn");
    }
    if (prec < precedence) {
      tag(buf, "mo", ")");
    }
    tagEnd(buf, "mrow");
  }

  public void convertFraction(final StringBuilder buf, final IFraction f, final int precedence) {
    boolean isInteger = f.denominator().isOne();
    if (f.isNegative() && (precedence > Precedence.PLUS)) {
      tagStart(buf, "mrow");
      tag(buf, "mo", "(");
    }
    if (isInteger) {
      tagStart(buf, "mn");
      buf.append(f.toBigNumerator().toString());
      tagEnd(buf, "mn");
    } else {
      tagStart(buf, "mfrac");
      tagStart(buf, "mn");
      buf.append(f.toBigNumerator().toString());
      tagEnd(buf, "mn");
      tagStart(buf, "mn");
      buf.append(f.toBigDenominator().toString());
      tagEnd(buf, "mn");
      tagEnd(buf, "mfrac");
    }
    if (f.isNegative() && (precedence > Precedence.PLUS)) {
      tag(buf, "mo", ")");
      tagEnd(buf, "mrow");
    }
  }

  @Override
  // public void convertFraction(final StringBuilder buf, final IRational frac, final int
  // precedence) {
  // IRational f = frac;
  // boolean isNegative = f.isNegative();
  // if (isNegative && (precedence > plusPrec)) {
  // tagStart(buf, "mrow");
  // tag(buf, "mo", "(");
  // }
  // if (isNegative) {
  // tag(buf, "mo", "-");
  // f = frac.negate();
  // }
  // if (f.getDenominator().isOne() || f.getNumerator().isZero()) {
  // tagStart(buf, "mn");
  // buf.append(f.getNumerator().toString());
  // tagEnd(buf, "mn");
  // } else {
  // tagStart(buf, "mfrac");
  // tagStart(buf, "mn");
  // buf.append(f.getNumerator().toString());
  // tagEnd(buf, "mn");
  // tagStart(buf, "mn");
  // buf.append(f.getDenominator().toString());
  // tagEnd(buf, "mn");
  // tagEnd(buf, "mfrac");
  // }
  // if (isNegative && (precedence > plusPrec)) {
  // tag(buf, "mo", ")");
  // tagEnd(buf, "mrow");
  // }
  // }

  public void convertFraction(final StringBuilder buf, final IRational f, final int precedence,
      boolean caller) {
    convertFraction(buf, f.toBigNumerator(), f.toBigDenominator(), precedence, caller);
  }

  public void convertFunctionArgs(final StringBuilder buf, final IAST list) {
    tag(buf, "mo", "[");
    for (int i = 1; i < list.size(); i++) {
      convertInternal(buf, list.get(i), Integer.MIN_VALUE, false);
      if (i < list.argSize()) {
        tag(buf, "mo", ",");
      }
    }
    tag(buf, "mo", "]");
  }

  @Override
  public void convertHead(final StringBuilder buf, final IExpr obj) {
    if (obj instanceof ISymbol) {
      String headStr = ((ISymbol) obj).getSymbolName();
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(headStr);
        if (str != null) {
          headStr = str;
        }
      }
      tagStart(buf, "mi");
      buf.append(headStr);
      tagEnd(buf, "mi");
      // &af; &#x2061;
      tag(buf, "mo", "&#x2061;");
      return;
    }
    convertInternal(buf, obj, Integer.MIN_VALUE, false);
  }

  public void convertInfixOperator(final StringBuilder buf, IAST list, final InfixOperator oper,
      final int precedence) {
    if (list.isAST2()) {
      IExpr arg1 = list.arg1();
      IExpr arg2 = list.arg2();
      tagStart(buf, "mrow");
      if (oper.getPrecedence() < precedence) {
        // append(buf, "(");
        tag(buf, "mo", "(");
      }
      if (oper.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE
          && list.arg1().head().equals(list.head())) {
        // append(buf, "(");
        tag(buf, "mo", "(");
        // } else {
        // if (oper.getOperatorString() == "^") {
        // final Operator operator = getOperator(list.arg1().topHead());
        // if (operator instanceof PostfixOperator) {
        // append(buf, "(");
        // }
        // }
      }
      convertInternal(buf, arg1, oper.getPrecedence(), false);
      if (oper.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE
          && list.arg1().head().equals(list.head())) {
        // append(buf, ")");
        tag(buf, "mo", ")");
        // } else {
        // if (oper.getOperatorString() == "^") {
        // final Operator operator = getOperator(list.arg1().topHead());
        // if (operator instanceof PostfixOperator) {
        // append(buf, ")");
        // }
        // }
      }

      appendOperatorUnicodeMapped(buf, oper.getOperatorString());

      if (oper.getGrouping() == InfixOperator.LEFT_ASSOCIATIVE
          && list.arg2().head().equals(list.head())) {
        // append(buf, "(");
        tag(buf, "mo", "(");
      }
      convertInternal(buf, arg2, oper.getPrecedence(), false);
      if (oper.getGrouping() == InfixOperator.LEFT_ASSOCIATIVE
          && list.arg2().head().equals(list.head())) {
        // append(buf, ")");
        tag(buf, "mo", ")");
      }

      if (oper.getPrecedence() < precedence) {
        // append(buf, ")");
        tag(buf, "mo", ")");
      }
      tagEnd(buf, "mrow");
      return;
    }

    tagStart(buf, "mrow");
    if (oper.getPrecedence() < precedence) {
      // append(buf, "(");
      tag(buf, "mo", "(");
    }
    if (list.size() > 1) {
      convertInternal(buf, list.arg1(), oper.getPrecedence(), false);
    }

    for (int i = 2; i < list.size(); i++) {
      appendOperatorUnicodeMapped(buf, oper.getOperatorString());
      convertInternal(buf, list.get(i), oper.getPrecedence(), false);
    }
    if (oper.getPrecedence() < precedence) {
      // append(buf, ")");
      tag(buf, "mo", ")");
    }
    tagEnd(buf, "mrow");
  }

  /**
   * Map the WL-unicode string to Unicode-equivalent string and surround by <code>
   * &lt;mo&gt;</code> tags.
   *
   * @param buf
   * @param operatorString the operator string
   */
  private void appendOperatorUnicodeMapped(final StringBuilder buf, String operatorString) {
    operatorString = Characters.mapWLUnicodeToEquivalent(operatorString);
    tag(buf, "mo", operatorString);
  }

  /**
   * Map the WL-unicode string to Unicode-equivalent string.
   *
   * @param buf
   * @param str the string which should be converted
   */
  private static void appendUnicodeMapped(final StringBuilder buf, String str) {
    str = Characters.mapWLUnicodeToEquivalent(str);
    buf.append(str);
  }

  @Override
  public void convertInteger(final StringBuilder buf, final IInteger i, final int precedence,
      boolean caller) {
    if (i.isNegative() && (precedence > Precedence.PLUS)) {
      tagStart(buf, "mrow");
      tag(buf, "mo", "(");
    }
    String formatted = fNumberFormatter == null ? null
        // exact integers only pick up DigitBlock, padding and sign options - never an exponent
        : formattedNumberMarkup(fNumberFormatter.format(i.toBigNumerator()));
    if (formatted != null) {
      buf.append(formatted);
    } else {
      tagStart(buf, "mn");
      buf.append(i.toBigNumerator().toString());
      tagEnd(buf, "mn");
    }
    if (i.isNegative() && (precedence > Precedence.PLUS)) {
      tag(buf, "mo", ")");
      tagEnd(buf, "mrow");
    }
  }

  public void convertList(final StringBuilder buf, final IAST list) {
    tagStart(buf, "mrow");
    tag(buf, "mo", "{");
    if (list.size() > 1) {
      tagStart(buf, "mrow");
      convertInternal(buf, list.arg1(), Integer.MIN_VALUE, false);
      for (int i = 2; i < list.size(); i++) {
        tag(buf, "mo", ",");
        convertInternal(buf, list.get(i), Integer.MIN_VALUE, false);
      }
      tagEnd(buf, "mrow");
    }
    tag(buf, "mo", "}");
    tagEnd(buf, "mrow");
  }

  public void convertAssociation(final StringBuilder buf, final IAssociation association) {
    IAST list = association.normal(false);
    tagStart(buf, "mrow");
    tag(buf, "mo", "&lt;|");
    if (list.size() > 1) {
      tagStart(buf, "mrow");
      convertInternal(buf, list.arg1(), Precedence.NO_PRECEDENCE, false);
      for (int i = 2; i < list.size(); i++) {
        tag(buf, "mo", ",");
        convertInternal(buf, list.get(i), Precedence.NO_PRECEDENCE, false);
      }
      tagEnd(buf, "mrow");
    }
    tag(buf, "mo", "|&gt;");
    tagEnd(buf, "mrow");
  }

  public boolean convertInterval(final StringBuilder buf, final IAST f) {
    if (f.size() > 1 && f.first().isASTSizeGE(S.List, 2)) {
      IAST interval = IntervalSym.normalize(f);

      tagStart(buf, "mrow");
      tagStart(buf, "mi");
      buf.append("Interval");
      tagEnd(buf, "mi");
      // &af; &#x2061;
      tag(buf, "mo", "&#x2061;");
      tagStart(buf, "mrow");
      if (fRelaxedSyntax) {
        tag(buf, "mo", "(");
      } else {
        tag(buf, "mo", "[");
      }

      for (int i = 1; i < interval.size(); i++) {
        tagStart(buf, "mrow");
        tag(buf, "mo", "{");

        IAST subList = (IAST) interval.get(i);
        IExpr min = subList.arg1();
        IExpr max = subList.arg2();
        if (min instanceof INum) {
          // tagStart(buf, "mn");
          convertDouble(buf, (INum) min, Integer.MIN_VALUE, NO_PLUS_CALL);
          // buf.append(convertDoubleToFormattedString(min.evalDouble()));
          // tagEnd(buf, "mn");
        } else {
          convertInternal(buf, min, Precedence.NO_PRECEDENCE, false);
        }
        tag(buf, "mo", ",");
        if (max instanceof INum) {
          // tagStart(buf, "mn");
          convertDouble(buf, (INum) max, Integer.MIN_VALUE, NO_PLUS_CALL);
          // buf.append(convertDoubleToFormattedString(max.evalDouble()));
          // tagEnd(buf, "mn");
        } else {
          convertInternal(buf, max, Precedence.NO_PRECEDENCE, false);
        }
        tag(buf, "mo", "}");
        tagEnd(buf, "mrow");
        if (i < interval.argSize()) {
          tag(buf, "mo", ",");
        }
      }
      if (fRelaxedSyntax) {
        tag(buf, "mo", ")");
      } else {
        tag(buf, "mo", "]");
      }
      tagEnd(buf, "mrow");
      tagEnd(buf, "mrow");
      return true;
    }
    return false;
  }

  public boolean convertNumber(final StringBuilder buf, final IExpr o, final int precedence,
      boolean caller) {
    if (o instanceof INum) {
      convertDouble(buf, (INum) o, precedence, caller);
      return true;
    }
    if (o instanceof IComplexNum) {
      convertDoubleComplex(buf, (IComplexNum) o, precedence, caller);
      return true;
    }
    if (o instanceof IInteger) {
      convertInteger(buf, (IInteger) o, precedence, caller);
      return true;
    }
    if (o instanceof IFraction) {
      convertFraction(buf, (IFraction) o, precedence, caller);
      return true;
    }
    if (o instanceof IComplex) {
      convertComplex(buf, (IComplex) o, precedence, caller);
      return true;
    }
    return false;
  }

  private boolean convertOperator(final org.matheclipse.parser.client.operator.Operator operator,
      IAST list, final StringBuilder buf, final int precedence, ISymbol head) {
    if ((operator instanceof PrefixOperator) && (list.isAST1())) {
      convertPrefixOperator(buf, list, (PrefixOperator) operator, precedence);
      return true;
    }
    if ((operator instanceof InfixOperator) && (list.size() > 2)) {
      InfixOperator infixOperator = (InfixOperator) operator;
      // if (head.equals(F.Plus)) {
      // if (fPlusReversed) {
      // convertPlusOperatorReversed(buf, list, infixOperator, precedence);
      // } else {
      // convertPlusOperator(buf, list, infixOperator, precedence);
      // }
      // return true;
      // } else
      // if (head.equals(F.Times)) {
      // convertTimesOperator(buf, list, infixOperator, precedence, NO_PLUS_CALL);
      // return true;
      // convertTimesFraction(buf, list, infixOperator, precedence, NO_PLUS_CALL);
      // return true;
      // } else if (list.isPower()) {
      // convertPowerOperator(buf, list, infixOperator, precedence);
      // return true;
      // } else
      if (list.isAST(S.Apply)) {
        if (list.size() == 3) {
          convertInfixOperator(buf, list, ASTNodeFactory.APPLY_OPERATOR, precedence);
          return true;
        }
        if (list.size() == 4 && list.arg2().equals(F.CListC1)) {
          convertInfixOperator(buf, list, ASTNodeFactory.MAPAPPLY_OPERATOR, precedence);
          return true;
        }
        return false;
      } else if (list.size() != 3 && infixOperator.getGrouping() != InfixOperator.NONE) {
        return false;
      }
      convertInfixOperator(buf, list, (InfixOperator) operator, precedence);
      return true;
    }
    if ((operator instanceof PostfixOperator) && (list.isAST1())) {
      convertPostfixOperator(buf, list, (PostfixOperator) operator, precedence);
      return true;
    }
    return false;
  }

  /**
   * This method will only be called if <code>list.isAST2()==true</code> and the head equals "Part".
   *
   * @param buf
   * @param list
   * @throws IOException
   */
  public void convertPart(final StringBuilder buf, final IAST list) {
    IExpr arg1 = list.arg1();
    tagStart(buf, "mrow");
    if (!(arg1 instanceof IAST)) {
      tag(buf, "mo", "(");
    }
    convertInternal(buf, arg1, Integer.MIN_VALUE, false);
    tag(buf, "mo", "[[");

    for (int i = 2; i < list.size(); i++) {
      convertInternal(buf, list.get(i), Integer.MIN_VALUE, false);
      if (i < list.argSize()) {
        tag(buf, "mo", ",");
      }
    }

    tag(buf, "mo", "]]");
    if (!(arg1 instanceof IAST)) {
      tag(buf, "mo", ")");
    }
    tagEnd(buf, "mrow");
  }

  public void convertPostfixOperator(final StringBuilder buf, final IAST list,
      final PostfixOperator oper, final int precedence) {
    tagStart(buf, "mrow");
    if (oper.getPrecedence() <= precedence) {
      // append(buf, "(");
      tag(buf, "mo", "(");
    }
    convertInternal(buf, list.arg1(), oper.getPrecedence(), false);
    // append(buf, oper.getOperatorString());
    tag(buf, "mo", oper.getOperatorString());
    if (oper.getPrecedence() <= precedence) {
      // append(buf, ")");
      tag(buf, "mo", ")");
    }
    tagEnd(buf, "mrow");
  }

  public void convertPrefixOperator(final StringBuilder buf, final IAST list,
      final PrefixOperator oper, final int precedence) {
    tagStart(buf, "mrow");
    if (oper.getPrecedence() <= precedence) {
      // append(buf, "(");
      tag(buf, "mo", "(");
    }
    // append(buf, oper.getOperatorString());
    tag(buf, "mo", oper.getOperatorString());
    convertInternal(buf, list.arg1(), oper.getPrecedence(), false);
    if (oper.getPrecedence() <= precedence) {
      // append(buf, ")");
      tag(buf, "mo", ")");
    }
    tagEnd(buf, "mrow");
  }

  /**
   * Convert a <code>SeriesData[...]</code> expression.
   *
   * @param buf
   * @param seriesData <code>SeriesData[x, x0, list, nmin, nmax, den]</code> expression
   * @param precedence the precedence of the parent expression
   * @return <code>true</code> if the conversion was successful
   * @throws IOException
   */
  public boolean convertSeriesData(final StringBuilder buf, final ASTSeriesData seriesData,
      final int precedence) {
    StringBuilder tempBuffer = new StringBuilder();
    tagStart(tempBuffer, "mrow");
    if (Precedence.PLUS < precedence) {
      tag(tempBuffer, "mo", "(");
    }
    try {
      IExpr plusArg;
      // SeriesData[x, x0, list, nmin, nmax, den]
      IExpr x = seriesData.expansionVariable();
      IExpr x0 = seriesData.expansionPoint();
      int nMin = seriesData.minExponent();
      int nMax = seriesData.exponentBound();
      int power = seriesData.truncateOrder();
      int den = seriesData.puiseuxDenominator();
      boolean call = NO_PLUS_CALL;
      INumber exp;
      IExpr pow;
      boolean first = true;
      IExpr x0Term = x.subtract(x0);
      for (int i = nMin; i < nMax; i++) {
        IExpr coefficient = seriesData.coefficient(i);
        if (!coefficient.isZero()) {
          if (!first) {
            tag(tempBuffer, "mo", "+");
          }
          exp = F.QQ(i, den).normalize();
          pow = x0Term.power(exp);

          call = convertSeriesDataArg(tempBuffer, coefficient, pow, call);

          first = false;
        }
      }
      plusArg = F.Power(F.O(x.subtract(x0)), F.QQ(power, den).normalize());
      if (!plusArg.isZero()) {
        tag(tempBuffer, "mo", "+");
        convertInternal(tempBuffer, plusArg, Integer.MIN_VALUE, false);
        call = PLUS_CALL;
      }

    } catch (Exception ex) {
      Errors.rethrowsInterruptException(ex);
      return false;
    }
    if (Precedence.PLUS < precedence) {
      tag(tempBuffer, "mo", ")");
    }
    tagEnd(tempBuffer, "mrow");
    buf.append(tempBuffer);
    return true;
  }

  /**
   * Convert a factor of a <code>SeriesData</code> object.
   *
   * @param buf
   * @param coefficient the coefficient expression of the factor
   * @param pow the power expression of the factor
   * @param call
   * @return the current call status
   */
  private boolean convertSeriesDataArg(StringBuilder buf, IExpr coefficient, IExpr pow,
      boolean call) {
    IExpr plusArg;
    if (coefficient.isZero()) {
      plusArg = F.C0;
    } else if (coefficient.isOne()) {
      plusArg = pow;
    } else {
      if (pow.isOne()) {
        plusArg = coefficient;
      } else {
        plusArg = F.binaryAST2(S.Times, coefficient, pow);
      }
    }
    if (!plusArg.isZero()) {
      convertInternal(buf, plusArg, Integer.MIN_VALUE, false);
      call = PLUS_CALL;
    }
    return call;
  }

  public void convertSlot(final StringBuilder buf, final IAST list) {
    try {
      final int slot = ((IReal) list.arg1()).toInt();
      // append(buf, "#" + slot);
      tag(buf, "mi", "#" + slot);
    } catch (final ArithmeticException e) {
      // add message to evaluation problemReporter
    }
  }

  public void convertSlotSequence(final StringBuilder buf, final IAST list) {
    try {
      final int slotSequenceStartPosition = ((IReal) list.arg1()).toInt();
      // append(buf, "##" + slotSequenceStartPosition);
      tag(buf, "mi", "##" + slotSequenceStartPosition);
    } catch (final ArithmeticException e) {
      // add message to evaluation problemReporter
    }
  }

  @Override
  public void convertString(final StringBuilder buf, final String str) {
    String[] splittedStr = str.split("\\n");
    final int splittedStrLength = splittedStr.length;
    for (int i = 0; i < splittedStrLength; i++) {
      tagStart(buf, "mtext");
      String text = splittedStr[i].replaceAll("\\&", "&amp;").replaceAll("\\<", "&lt;")
          .replaceAll("\\>", "&gt;");
      text = text.replaceAll("\\\"", "&quot;").replace(" ", "&nbsp;");
      appendUnicodeMapped(buf, text);
      tagEnd(buf, "mtext");
      if (splittedStrLength > 1) {
        buf.append("<mspace linebreak='newline' />");
      }
    }
  }

  /**
   * Convert to a MathML <code>mext</code> string with line breaks.
   * 
   * @param str
   * @return
   */
  public static String mathMLMtext(final String str) {
    StringBuilder buf = new StringBuilder();
    String[] splittedStr = str.split("\\n");
    final int splittedStrLength = splittedStr.length;
    for (int i = 0; i < splittedStrLength; i++) {
      buf.append("<mtext>");
      String text = splittedStr[i].replaceAll("\\&", "&amp;").replaceAll("\\<", "&lt;")
          .replaceAll("\\>", "&gt;");
      text = text.replaceAll("\\\"", "&quot;").replace(" ", "&nbsp;");
      appendUnicodeMapped(buf, text);
      buf.append("</mtext>");
      if (splittedStrLength > 1) {
        buf.append("<mspace linebreak='newline' />");
      }
    }
    return buf.toString();
  }

  @Override
  public void convertSymbol(final StringBuilder buf, final ISymbol sym) {
    Context context = sym.getContext();
    if (context == Context.DUMMY) {
      tagStart(buf, "mi");
      buf.append(sym.getSymbolName());
      tagEnd(buf, "mi");
      return;
    }
    String headStr = sym.getSymbolName();
    if (context.equals(Context.SYSTEM) || context.isGlobal()) {
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(headStr);
        if (str != null) {
          headStr = str;
        }
      }
      final Object convertedSymbol = CONSTANT_SYMBOLS.get(headStr);
      if (convertedSymbol == null) {
        tagStart(buf, "mi");
        buf.append(headStr);
        tagEnd(buf, "mi");
        return;
      }
      if (convertedSymbol instanceof Operator) {
        ((Operator) convertedSymbol).convert(buf);
        return;
      }
      tagStart(buf, "mi");
      buf.append(convertedSymbol.toString());
      tagEnd(buf, "mi");
      return;
    }
    if (EvalEngine.get().getContextPath().contains(context)) {
      tagStart(buf, "mi");
      buf.append(sym.getSymbolName());
    } else {
      tagStart(buf, "mi");
      buf.append(context.toString() + sym.getSymbolName());
    }
    tagEnd(buf, "mi");
  }

  public void init() {
    if (Config.MATHML_TRIG_LOWERCASE) {
      CONVERTERS.put(S.Sin, new MMLFunction(this, "sin"));
      CONVERTERS.put(S.Cos, new MMLFunction(this, "cos"));
      CONVERTERS.put(S.Csc, new MMLFunction(this, "csc"));
      CONVERTERS.put(S.Tan, new MMLFunction(this, "tan"));
      CONVERTERS.put(S.Sec, new MMLFunction(this, "sec"));
      CONVERTERS.put(S.Cot, new MMLFunction(this, "cot"));
      CONVERTERS.put(S.ArcSin, new MMLFunction(this, "arcsin"));
      CONVERTERS.put(S.ArcCos, new MMLFunction(this, "arccos"));
      CONVERTERS.put(S.ArcCsc, new MMLFunction(this, "arccsc"));
      CONVERTERS.put(S.ArcSec, new MMLFunction(this, "arcsec"));
      CONVERTERS.put(S.ArcTan, new MMLFunction(this, "arctan"));
      CONVERTERS.put(S.ArcCot, new MMLFunction(this, "arccot"));
      CONVERTERS.put(S.ArcSinh, new MMLFunction(this, "arcsinh"));
      CONVERTERS.put(S.ArcCosh, new MMLFunction(this, "arccosh"));
      CONVERTERS.put(S.ArcCsch, new MMLFunction(this, "arccsch"));
      CONVERTERS.put(S.ArcCoth, new MMLFunction(this, "arccoth"));
      CONVERTERS.put(S.ArcSech, new MMLFunction(this, "arcsech"));
      CONVERTERS.put(S.ArcTanh, new MMLFunction(this, "arctanh"));
      CONVERTERS.put(S.Log, new MMLFunction(this, "log"));
    }

    // operTab.put("Sum", new MMLSum(this));
    // operTab.put("Integrate", new MMLIntegrate(this));
    // operTab.put("D", new MMLD(this));
    // operTab.put(Factorial, new MMLFactorial(this));
    // operTab.put("Binomial", new MMLBinomial(this));

    CONSTANT_SYMBOLS.put("E", "&#x2147;");
    // CONSTANT_SYMBOLS.put("I", "\u2148"); // IMaginaryI
    CONSTANT_SYMBOLS.put("HEllipsis", new Operator("&#x2026;")); // &hellip;"));
    // greek Symbols:
    // CONSTANT_SYMBOLS.put("Pi", "&#x03A0;");
    // CONSTANT_SYMBOLS.put("pi", "&#x03C0;");
    CONSTANT_SYMBOLS.put("Alpha", "&#x0391;");
    CONSTANT_SYMBOLS.put("Beta", "&#x0392;");
    CONSTANT_SYMBOLS.put("Gamma", "&#x0393;");
    CONSTANT_SYMBOLS.put("Delta", "&#x0394;");
    CONSTANT_SYMBOLS.put("Epsilon", "&#x0395;");
    CONSTANT_SYMBOLS.put("Zeta", "&#x0396;");
    CONSTANT_SYMBOLS.put("Eta", "&#x0397;");
    CONSTANT_SYMBOLS.put("Theta", "&#x0398;");
    CONSTANT_SYMBOLS.put("Iota", "&#x0399;");
    CONSTANT_SYMBOLS.put("Kappa", "&#x039A;");
    CONSTANT_SYMBOLS.put("Lambda", "&#x039B;");
    CONSTANT_SYMBOLS.put("Mu", "&#x039C;");
    CONSTANT_SYMBOLS.put("Nu", "&#x039D;");
    CONSTANT_SYMBOLS.put("Xi", "&#x039E;");
    CONSTANT_SYMBOLS.put("Omicron", "&#x039F;");
    CONSTANT_SYMBOLS.put("Rho", "&#x03A1;");
    CONSTANT_SYMBOLS.put("Sigma", "&#x03A3;");
    CONSTANT_SYMBOLS.put("Tau", "&#x03A4;");
    CONSTANT_SYMBOLS.put("Upsilon", "&#x03A5;");
    CONSTANT_SYMBOLS.put("Phi", "&#x03A6;");
    CONSTANT_SYMBOLS.put("Chi", "&#x03A7;");
    CONSTANT_SYMBOLS.put("Psi", "&#x03A8;");
    CONSTANT_SYMBOLS.put("Omega", "&#x03A9;");

    CONSTANT_SYMBOLS.put("varTheta", "&#x03D1;");

    CONSTANT_SYMBOLS.put("alpha", "&#x03B1;");
    CONSTANT_SYMBOLS.put("beta", "&#x03B2;");
    CONSTANT_SYMBOLS.put("chi", "&#x03C7;");
    CONSTANT_SYMBOLS.put("selta", "&#x03B4;");
    CONSTANT_SYMBOLS.put("epsilon", "&#x03B5;");
    CONSTANT_SYMBOLS.put("phi", "&#x03C7;");
    CONSTANT_SYMBOLS.put("gamma", "&#x03B3;");
    CONSTANT_SYMBOLS.put("eta", "&#x03B7;");
    CONSTANT_SYMBOLS.put("iota", "&#x03B9;");
    CONSTANT_SYMBOLS.put("varphi", "&#x03C6;");
    CONSTANT_SYMBOLS.put("kappa", "&#x03BA;");
    CONSTANT_SYMBOLS.put("lambda", "&#x03BB;");
    CONSTANT_SYMBOLS.put("mu", "&#x03BC;");
    CONSTANT_SYMBOLS.put("nu", "&#x03BD;");
    CONSTANT_SYMBOLS.put("omicron", "&#x03BF;");
    CONSTANT_SYMBOLS.put("theta", "&#x03B8;");
    CONSTANT_SYMBOLS.put("rho", "&#x03C1;");
    CONSTANT_SYMBOLS.put("sigma", "&#x03C3;");
    CONSTANT_SYMBOLS.put("tau", "&#x03C4;");
    CONSTANT_SYMBOLS.put("upsilon", "&#x03C5;");
    CONSTANT_SYMBOLS.put("varsigma", "&#x03C2;");
    CONSTANT_SYMBOLS.put("omega", "&#x03C9;");
    CONSTANT_SYMBOLS.put("xi", "&#x03BE;");
    CONSTANT_SYMBOLS.put("psi", "&#x03C8;");
    CONSTANT_SYMBOLS.put("zeta", "&#x03B6;");

    ENTITY_TABLE.put("&af;", "&#xE8A0;");
    ENTITY_TABLE.put("&dd;", "&#xF74C;");
    ENTITY_TABLE.put("&ImaginaryI;", "i"); // "\u2148");
    ENTITY_TABLE.put("&InvisibleTimes;", "&#xE89E;");

    ENTITY_TABLE.put("&Integral;", "&#x222B;");
    ENTITY_TABLE.put("&PartialD;", "&#x2202;");
    ENTITY_TABLE.put("&Product;", "&#x220F;");

    CONSTANT_EXPRS.put(S.GoldenRatio, "<mi>&#x03C7;</mi>"); // phi
    CONSTANT_EXPRS.put(S.Pi, "<mi>&#x03C0;</mi>");
    CONSTANT_EXPRS.put(F.CInfinity, "<mi>&#x221E;</mi>"); // &infin;
    // mrow is important in mfrac element!
    CONSTANT_EXPRS.put(F.CNInfinity, "<mrow><mo>-</mo><mi>&#x221E;</mi></mrow>");
    CONSTANT_EXPRS.put(S.Catalan, "<mi>C</mi>");
    CONSTANT_EXPRS.put(S.Degree, "<mi>&#x00b0;</mi>");
    CONSTANT_EXPRS.put(S.Glaisher, "<mi>A</mi>");
    CONSTANT_EXPRS.put(S.EulerGamma, "<mi>&#x03B3;</mi>");
    CONSTANT_EXPRS.put(S.Khinchin, "<mi>K</mi>");

    CONSTANT_EXPRS.put(S.Complexes, "<mi>&#8450;</mi>");
    CONSTANT_EXPRS.put(S.Integers, "<mi>&#8484;</mi>");
    CONSTANT_EXPRS.put(S.Rationals, "<mi>&#8474;</mi>");
    CONSTANT_EXPRS.put(S.Reals, "<mi>&#8477;</mi>");

    CONVERTERS.put(S.Abs, new Abs());
    CONVERTERS.put(S.And, new MMLOperator(Precedence.AND, "&#x2227;"));
    CONVERTERS.put(S.Binomial, new Binomial());
    CONVERTERS.put(S.C, new C());
    CONVERTERS.put(S.Ceiling, new Ceiling());
    CONVERTERS.put(S.CompoundExpression, new MMLOperator(Precedence.COMPOUNDEXPRESSION, ";"));
    CONVERTERS.put(S.D, new D());
    CONVERTERS.put(S.DirectedEdge, new MMLOperator(Precedence.DIRECTEDEDGE, "-&gt;"));
    CONVERTERS.put(S.Dot, new MMLOperator(Precedence.DOT, "."));
    CONVERTERS.put(S.Element, new Element());
    CONVERTERS.put(S.Equal, new MMLOperator(Precedence.EQUAL, "=="));
    CONVERTERS.put(S.Factorial, new MMLPostfix("!", Precedence.FACTORIAL));
    CONVERTERS.put(S.Factorial2, new MMLPostfix("!!", Precedence.FACTORIAL2));
    CONVERTERS.put(S.Floor, new Floor());
    CONVERTERS.put(S.Function, new Function());
    CONVERTERS.put(S.Greater, new MMLOperator(Precedence.GREATER, "&gt;"));
    CONVERTERS.put(S.GreaterEqual, new MMLOperator(Precedence.GREATEREQUAL, "&#x2265;"));
    CONVERTERS.put(S.Integrate, new Integrate());
    CONVERTERS.put(S.Less, new MMLOperator(Precedence.LESS, "&lt;"));
    CONVERTERS.put(S.LessEqual, new MMLOperator(Precedence.LESSEQUAL, "&#x2264;"));
    CONVERTERS.put(S.MatrixForm, new MatrixForm(false));
    CONVERTERS.put(S.TableForm, new MatrixForm(true));

    NumberForm numberForm = new NumberForm();
    CONVERTERS.put(S.AccountingForm, numberForm);
    CONVERTERS.put(S.DecimalForm, numberForm);
    CONVERTERS.put(S.EngineeringForm, numberForm);
    CONVERTERS.put(S.NumberForm, numberForm);
    CONVERTERS.put(S.PaddedForm, numberForm);
    CONVERTERS.put(S.ScientificForm, numberForm);
    CONVERTERS.put(S.Not, new Not());
    CONVERTERS.put(S.Or, new MMLOperator(Precedence.OR, "&#x2228;"));
    CONVERTERS.put(S.Plus, new Plus());
    CONVERTERS.put(S.Power, new Power());
    CONVERTERS.put(S.Product, new Product());
    CONVERTERS.put(S.Rational, new Rational());
    CONVERTERS.put(S.Rule, new MMLOperator(Precedence.RULE, "-&gt;"));
    CONVERTERS.put(S.RuleDelayed, new MMLOperator(Precedence.RULEDELAYED, "&#x29F4;"));
    CONVERTERS.put(S.Set, new MMLOperator(Precedence.SET, "="));
    CONVERTERS.put(S.SetDelayed, new MMLOperator(Precedence.SETDELAYED, ":="));
    CONVERTERS.put(S.Sqrt, new Sqrt());
    CONVERTERS.put(S.Button, new Button());
    CONVERTERS.put(S.Row, new Row());
    CONVERTERS.put(S.Column, new Column());
    CONVERTERS.put(S.Graphics, new GraphicsInline());
    ControlWidget controlWidget = new ControlWidget();
    CONVERTERS.put(S.Slider, controlWidget);
    CONVERTERS.put(S.VerticalSlider, controlWidget);
    CONVERTERS.put(S.Slider2D, controlWidget);
    CONVERTERS.put(S.IntervalSlider, controlWidget);
    CONVERTERS.put(S.Manipulator, controlWidget);
    CONVERTERS.put(S.Checkbox, controlWidget);
    CONVERTERS.put(S.Toggler, controlWidget);
    CONVERTERS.put(S.Opener, controlWidget);
    CONVERTERS.put(S.TogglerBar, controlWidget);
    CONVERTERS.put(S.CheckboxBar, controlWidget);
    CONVERTERS.put(S.PopupMenu, controlWidget);
    CONVERTERS.put(S.SetterBar, controlWidget);
    CONVERTERS.put(S.RadioButtonBar, controlWidget);
    CONVERTERS.put(S.RadioButton, controlWidget);
    CONVERTERS.put(S.Setter, controlWidget);
    CONVERTERS.put(S.InputField, controlWidget);
    CONVERTERS.put(S.ColorSetter, controlWidget);
    CONVERTERS.put(S.ColorSlider, controlWidget);
    CONVERTERS.put(S.Trigger, controlWidget);
    CONVERTERS.put(S.Animator, controlWidget);
    CONVERTERS.put(S.Locator, controlWidget);
    CONVERTERS.put(S.LocatorPane, controlWidget);
    CONVERTERS.put(S.ProgressIndicator, controlWidget);
    CONVERTERS.put(S.Overscript, new Overscript());
    CONVERTERS.put(S.Style, new Style());
    CONVERTERS.put(S.Subscript, new Subscript());
    CONVERTERS.put(S.Subsuperscript, new Subsuperscript());
    CONVERTERS.put(S.Superscript, new Superscript());
    CONVERTERS.put(S.Underscript, new Underscript());
    CONVERTERS.put(S.Underoverscript, new Underoverscript());
    CONVERTERS.put(S.Sum, new Sum());
    CONVERTERS.put(S.Surd, new Surd());
    CONVERTERS.put(S.Times, new Times());
    CONVERTERS.put(S.TwoWayRule, new MMLOperator(Precedence.TWOWAYRULE, "&lt;-&gt;"));
    CONVERTERS.put(S.UndirectedEdge, new MMLOperator(Precedence.UNDIRECTEDEDGE, "&lt;-&gt;"));
    CONVERTERS.put(S.Unequal, new MMLOperator(Precedence.UNEQUAL, "!="));
    CONVERTERS.put(S.CenterDot, new MMLOperator(Precedence.CENTERDOT, "&#183;"));
    CONVERTERS.put(S.CircleDot, new MMLOperator(Precedence.CIRCLEDOT, "&#8857;"));
  }
}
