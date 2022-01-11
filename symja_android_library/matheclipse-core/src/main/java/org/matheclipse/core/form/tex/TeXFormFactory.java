package org.matheclipse.core.form.tex;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.ASTRealMatrix;
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
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Characters;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.Precedence;
import org.matheclipse.parser.trie.TrieMatch;

/** Generates TeX presentation output */
public class TeXFormFactory {
  private static final Logger LOGGER = LogManager.getLogger();

  /** The conversion wasn't called with an operator preceding the <code>IExpr</code> object. */
  public static final boolean NO_PLUS_CALL = false;

  /**
   * The conversion was called with a &quot;+&quot; operator preceding the <code>IExpr</code>
   * object.
   */
  public static final boolean PLUS_CALL = true;

  private abstract static class AbstractConverter {
    protected TeXFormFactory fFactory;

    public AbstractConverter() {
      fFactory = null;
    }

    public AbstractConverter(final TeXFormFactory factory) {
      fFactory = factory;
    }

    public abstract boolean convert(final StringBuilder buf, final IAST f, final int precedence);

    /** @param factory */
    public void setFactory(final TeXFormFactory factory) {
      fFactory = factory;
    }
  }

  private static class AbstractOperator extends AbstractConverter {
    protected int fPrecedence;
    protected String fOperator;

    public AbstractOperator(final int precedence, final String oper) {
      fPrecedence = precedence;
      fOperator = oper;
    }

    public AbstractOperator(final TeXFormFactory factory, final int precedence, final String oper) {
      super(factory);
      fPrecedence = precedence;
      fOperator = oper;
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      final boolean isOr = f.isOr();
      precedenceOpen(buf, precedence);
      for (int i = 1; i < f.size(); i++) {
        if (isOr && f.get(i).isAnd()) {
          buf.append("\\left( ");
        }
        fFactory.convertInternal(buf, f.get(i), fPrecedence);
        if (isOr && f.get(i).isAnd()) {
          buf.append("\\right) ");
        }
        if (i < f.argSize()) {
          if (fOperator.compareTo("") != 0) {
            buf.append(fOperator);
          }
        }
      }
      precedenceClose(buf, precedence);
      return true;
    }

    public void precedenceClose(final StringBuilder buf, final int precedence) {
      if (precedence > fPrecedence) {
        buf.append("\\right) ");
      }
    }

    public void precedenceOpen(final StringBuilder buf, final int precedence) {
      if (precedence > fPrecedence) {
        buf.append("\\left( ");
      }
    }
  }

  private static final class Binomial extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      buf.append('{');
      fFactory.convertInternal(buf, f.arg1(), 0);
      buf.append("\\choose ");
      fFactory.convertInternal(buf, f.arg2(), 0);
      buf.append('}');
      return true;
    }
  }

  private static final class Complex extends AbstractOperator {

    public Complex() {
      super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence(), "+");
    }

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return super.convert(buf, f, precedence);
      }
      precedenceOpen(buf, precedence);
      IExpr arg1 = f.arg1();
      boolean reZero = arg1.isZero();
      IExpr arg2 = f.arg2();
      boolean imZero = arg2.isZero();

      if (!reZero) {
        fFactory.convertInternal(buf, arg1, 0);
      }
      if (!imZero) {
        if (!reZero && !arg2.isNegativeSigned()) {
          buf.append(" + ");
        }
        if (arg2.isMinusOne()) {
          buf.append(" - ");
        } else if (!arg2.isOne()) {
          fFactory.convertInternal(buf, arg2, 0);
          buf.append("\\,"); // InvisibleTimes
        }
        buf.append("\\imag");
      }
      return true;
    }
  }

  private static final class Conjugate extends AbstractOperator {
    public Conjugate() {
      super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "^*");
    }
    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      precedenceOpen(buf, precedence);
      fFactory.convertInternal(buf, f.arg1(), 0);
      buf.append("^*");
      precedenceClose(buf, precedence);
      return true;
    }
  }

  private static final class D extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.isAST2()) {
        IExpr arg2 = f.arg2();
        int n = 1;
        if (arg2.isAST(S.List, 3) && arg2.second().isInteger()) {
          n = arg2.second().toIntDefault();
          if (n <= 0) {
            return false;
          }
          arg2 = arg2.first();
        }
        buf.append("\\frac{\\partial ");
        if (n > 1) {
          buf.append("^" + n + " ");
        }
        fFactory.convertInternal(buf, f.arg1(), 0);
        buf.append("}{\\partial ");
        fFactory.convertInternal(buf, arg2, 0);
        if (n > 1) {
          buf.append("^" + n);
        }
        buf.append("}");

        return true;
      }
      return false;
    }
  }

  private static final class DirectedInfinity extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.isComplexInfinity()) {
        buf.append("ComplexInfinity");
        return true;
      } else if (f.isAST1()) {
        if (f.arg1().isOne()) {
          buf.append("\\infty");
          return true;
        } else if (f.arg1().isMinusOne()) {
          buf.append("- \\infty");
          return true;
        }
      }
      return false;
    }
  }

  private static final class HoldForm extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() == 2) {
        fFactory.convertInternal(buf, f.arg1(), 0);
        return true;
      }
      return false;
    }
  }

  private static final class HarmonicNumber extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.isAST1()) {
        buf.append("H_");
        fFactory.convertInternal(buf, f.arg1(), 0);
        return true;
      } else if (f.isAST2()) {
        buf.append("H_");
        fFactory.convertInternal(buf, f.arg1(), 0);

        buf.append("^{(");
        fFactory.convertInternal(buf, f.arg2(), 0);
        buf.append(")}");
        return true;
      }
      return false;
    }
  }

  private static final class Integrate extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() >= 3) {
        return iteratorStep(buf, "\\int", f, 2);
      }
      return false;
    }

    public boolean iteratorStep(
        final StringBuilder buf, final String mathSymbol, final IAST f, int i) {
      if (i >= f.size()) {
        buf.append(" ");
        fFactory.convertInternal(buf, f.arg1(), 0);
        return true;
      }
      if (f.get(i).isList()) {
        IAST list = (IAST) f.get(i);
        if (list.size() == 4 && list.arg1().isSymbol()) {
          ISymbol symbol = (ISymbol) list.arg1();
          buf.append(mathSymbol);
          buf.append("_{");
          fFactory.convertInternal(buf, list.arg2(), 0);
          buf.append("}^{");
          fFactory.convertInternal(buf, list.arg3(), 0);
          buf.append('}');
          if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
            return false;
          }
          buf.append("\\,\\mathrm{d}");
          fFactory.convertSymbol(buf, symbol);
          return true;
        }
      } else if (f.get(i).isSymbol()) {
        ISymbol symbol = (ISymbol) f.get(i);
        buf.append(mathSymbol);
        buf.append(" ");
        if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
          return false;
        }
        buf.append("\\,\\mathrm{d}");
        fFactory.convertSymbol(buf, symbol);
        return true;
      }
      return false;
    }
  }

  private static final class Limit extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.isAST2() && f.arg2().isRuleAST()) {
        final IAST rule = (IAST) f.arg2();
        buf.append("\\lim_{");
        fFactory.convertSubExpr(buf, rule.arg1(), 0);
        buf.append("\\to ");
        fFactory.convertSubExpr(buf, rule.arg2(), 0);
        buf.append(" }\\,");
        fFactory.convertSubExpr(buf, f.arg1(), 0);

        // buf.append("\\mathop {\\lim }\\limits_{");
        // fFactory.convert(buf, rule.arg1(), 0);
        // buf.append(" \\to ");
        // fFactory.convert(buf, rule.arg2(), 0);
        // buf.append('}');
        // fFactory.convert(buf, f.arg1(), 0);
        return true;
      }
      return false;
    }
  }

  private static final class List extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST ast, final int precedence) {

      if ((ast.getEvalFlags() & IAST.OUTPUT_MULTILINE) == IAST.OUTPUT_MULTILINE) {
        if (convertMultiline(buf, ast)) {
          return true;
        }
      }
      if ((ast instanceof ASTRealMatrix) || ast.isEvalFlagOn(IAST.IS_MATRIX)) {
        int[] dims = ast.isMatrix();
        if (dims != null) {
          // create a LaTeX matrix

          final IAST matrix = ast;

          if (Config.MATRIX_TEXFORM) {
            // problem with KaTeX?
            buf.append("\\left(\n\\begin{array}{");
            for (int i = 0; i < dims[1]; i++) {
              buf.append("c");
            }
            buf.append("}\n");
            if (ast.size() > 1) {
              for (int i = 1; i < ast.size(); i++) {
                IAST row = ast.getAST(i);
                for (int j = 1; j < row.size(); j++) {
                  fFactory.convert(buf, row.get(j), 0);
                  if (j < row.argSize()) {
                    buf.append(" & ");
                  }
                }
                if (i < ast.argSize()) {
                  buf.append(" \\\\\n");
                } else {
                  buf.append(" \\\n");
                }
              }
            }
            buf.append("\\\\\n\\end{array}\n\\right) ");
          } else {
            buf.append("\\begin{pmatrix}\n");
            IAST row;
            for (int i = 1; i < matrix.size(); i++) {
              row = (IAST) matrix.get(i);
              for (int j = 1; j < row.size(); j++) {
                buf.append(' ');
                fFactory.convertInternal(buf, row.get(j), 0);
                buf.append(' ');
                if (j < row.argSize()) {
                  buf.append('&');
                }
              }
              buf.append("\\\\\n");
            }

            buf.append("\\end{pmatrix}");
          }
          return true;
        }
      }

      if ((ast.getEvalFlags() & IAST.IS_VECTOR) == IAST.IS_VECTOR) {
        // create a LaTeX row vector
        // \begin{pmatrix} x & y \end{pmatrix}
        buf.append("\\begin{pmatrix} ");
        if (ast.size() > 1) {
          for (int j = 1; j < ast.size(); j++) {
            fFactory.convertInternal(buf, ast.get(j), 0);
            if (j < ast.argSize()) {
              buf.append(" & ");
            }
          }
        }
        buf.append(" \\end{pmatrix} ");
      } else {
        buf.append("\\{");
        if (ast.size() > 1) {
          fFactory.convertInternal(buf, ast.arg1(), 0);
          for (int i = 2; i < ast.size(); i++) {
            buf.append(',');
            fFactory.convertInternal(buf, ast.get(i), 0);
          }
        }
        buf.append("\\}");
      }
      return true;
    }

    private boolean convertMultiline(final StringBuilder buf, final IAST list) {

      buf.append("\\begin{array}{c}\n");
      IExpr element;
      for (int i = 1; i < list.size(); i++) {
        element = list.get(i);
        buf.append(' ');
        fFactory.convertInternal(buf, element, 0);
        buf.append(' ');
        if (i < list.argSize()) {
          buf.append("\\\\\n");
        }
      }
      buf.append("\n\\end{array}");

      return true;
    }
  }

  private static final class MatrixForm extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      int[] dims = f.arg1().isMatrix();
      if (dims == null) {
        int dim = f.arg1().isVector();
        if (dim < 0) {
          return false;
        } else {
          final IAST vector = (IAST) f.arg1();
          buf.append("\\begin{pmatrix}\n");
          IExpr element;
          for (int i = 1; i < vector.size(); i++) {
            element = vector.get(i);
            buf.append(' ');
            fFactory.convertInternal(buf, element, 0);
            buf.append(' ');
            if (i < vector.argSize()) {
              buf.append('&');
            }
          }
          buf.append("\\end{pmatrix}");
        }
      } else {
        final IAST matrix = (IAST) f.arg1().normal(false);
        if (Config.MATRIX_TEXFORM) {
          // problem with KaTeX?
          buf.append("\\left(\n\\begin{array}{");
          for (int i = 0; i < dims[1]; i++) {
            buf.append("c");
          }
          buf.append("}\n");
          if (matrix.size() > 1) {
            for (int i = 1; i < matrix.size(); i++) {
              IAST row = matrix.getAST(i);
              for (int j = 1; j < row.size(); j++) {
                fFactory.convert(buf, row.get(j), 0);
                if (j < row.argSize()) {
                  buf.append(" & ");
                }
              }
              if (i < matrix.argSize()) {
                buf.append(" \\\\\n");
              } else {
                buf.append(" \\\n");
              }
            }
          }
          buf.append("\\\\\n\\end{array}\n\\right) ");
        } else {
          buf.append("\\begin{pmatrix}\n");
          IAST row;
          for (int i = 1; i < matrix.size(); i++) {
            row = (IAST) matrix.get(i);
            for (int j = 1; j < row.size(); j++) {
              buf.append(' ');
              fFactory.convertInternal(buf, row.get(j), 0);
              buf.append(' ');
              if (j < row.argSize()) {
                buf.append('&');
              }
            }
            buf.append("\\\\\n");
          }

          buf.append("\\end{pmatrix}");
        }
      }
      return true;
    }
  }

  private static final class TableForm extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      int[] dims = f.arg1().isMatrix();
      if (dims == null) {
        int dim = f.arg1().isVector();
        if (dim < 0) {
          return false;
        } else {
          final IAST vector = (IAST) f.arg1();
          buf.append("\\begin{array}{c}\n");
          IExpr element;
          for (int i = 1; i < vector.size(); i++) {
            element = vector.get(i);
            buf.append(' ');
            fFactory.convertInternal(buf, element, 0);
            buf.append(' ');
            if (i < vector.argSize()) {
              buf.append("\\\\\n");
            }
          }
          buf.append("\n\\end{array}");
        }
      } else {
        final IAST matrix = (IAST) f.arg1();
        buf.append("\\begin{array}{");
        for (int i = 0; i < dims[1]; i++) {
          buf.append("c");
        }
        buf.append("}\n");
        IAST row;
        for (int i = 1; i < matrix.size(); i++) {
          row = (IAST) matrix.get(i);
          for (int j = 1; j < row.size(); j++) {
            buf.append(' ');
            fFactory.convertInternal(buf, row.get(j), 0);
            buf.append(' ');
            if (j < row.argSize()) {
              buf.append('&');
            }
          }
          buf.append("\\\\\n");
        }

        buf.append("\\end{array}");
      }
      return true;
    }
  }

  static class Operator {
    String fOperator;

    Operator(final String oper) {
      fOperator = oper;
    }

    public void convert(final StringBuilder buf) {
      buf.append(fOperator);
    }

    @Override
    public String toString() {
      return fOperator;
    }
  }

  private static final class Parenthesis extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      buf.append("(");
      fFactory.convertInternal(buf, f.arg1(), 0);
      buf.append(")");
      return true;
    }
  }

  private static final class Part extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() > 2) {
        fFactory.convertHead(buf, f.arg1());
        buf.append("[[");
        int argSize = f.argSize();
        for (int i = 2; i <= argSize; i++) {
          fFactory.convertInternal(buf, f.get(i), 0);
          if (i < argSize) {
            buf.append(",");
          }
        }
        buf.append("]]");
        return true;
      }
      return false;
    }
  }

  private static final class Plus extends AbstractOperator {

    public Plus() {
      super(Precedence.PLUS, "+");
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      IExpr expr;
      precedenceOpen(buf, precedence);
      final Times timesConverter = new Times();
      timesConverter.setFactory(fFactory);
      for (int i = 1; i < f.size(); i++) {
        expr = f.get(i);

        if ((i > 1) && (expr instanceof IAST) && expr.isTimes()) {
          timesConverter.convertTimesFraction(buf, (IAST) expr, fPrecedence, Times.PLUS_CALL);
        } else {
          if (i > 1) {
            if (expr.isNumber() && (((INumber) expr).complexSign() < 0)) {
              buf.append("-");
              expr = ((INumber) expr).negate();
            } else if (expr.isNegativeSigned()) {
            } else {
              buf.append("+");
            }
          }
          fFactory.convertInternal(buf, expr, fPrecedence);
        }
      }
      precedenceClose(buf, precedence);
      return true;
    }
  }

  private static final class PostOperator extends AbstractConverter {
    protected int fPrecedence;
    protected String fOperator;

    public PostOperator(final TeXFormFactory factory, final int precedence, final String oper) {
      super(factory);
      fPrecedence = precedence;
      fOperator = oper;
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      precedenceOpen(buf, precedence);
      fFactory.convertInternal(buf, f.arg1(), fPrecedence);
      buf.append(fOperator);
      precedenceClose(buf, precedence);
      return true;
    }

    public void precedenceClose(final StringBuilder buf, final int precedence) {
      if (precedence >= fPrecedence) {
        buf.append("\\right) ");
      }
    }

    public void precedenceOpen(final StringBuilder buf, final int precedence) {
      if (precedence >= fPrecedence) {
        buf.append("\\left( ");
      }
    }
  }

  /**
   * See: <a href="http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices">Wikibooks -
   * LaTeX/Mathematics - Powers and indices</a>
   */
  private static final class Power extends AbstractOperator {

    public Power() {
      super(Precedence.POWER, "^");
    }

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return super.convert(buf, f, precedence);
      }
      IExpr arg1 = f.arg1();
      IExpr arg2 = f.arg2();
      if (arg2.isNegative()) {
        buf.append("\\frac{1}{");
        fFactory.convertInternal(buf, F.Power(arg1, arg2.negate()), 0);
        buf.append('}');
        return true;
      }
      if (arg2.isNumEqualRational(F.C1D2)) {
        buf.append("\\sqrt{");
        fFactory.convertInternal(buf, arg1, fPrecedence);
        buf.append('}');
        return true;
      }
      if (arg2.isFraction()) {
        if (((IFraction) arg2).numerator().isOne()) {
          buf.append("\\sqrt[");
          fFactory.convertInternal(buf, ((IFraction) arg2).denominator(), fPrecedence);
          buf.append("]{");
          fFactory.convertInternal(buf, arg1, fPrecedence);
          buf.append('}');
          return true;
        }
      }

      precedenceOpen(buf, precedence);

      // http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
      // For powers with more than one digit, surround the power with {}.
      buf.append('{');
      fFactory.convertInternal(buf, arg1, fPrecedence);
      buf.append('}');
      if (fOperator.compareTo("") != 0) {
        buf.append(fOperator);
      }

      buf.append('{');
      fFactory.convertInternal(buf, arg2, 0);
      buf.append('}');
      precedenceClose(buf, precedence);
      return true;
    }
  }

  private static final class PreOperator extends AbstractConverter {
    protected int fPrecedence;
    protected String fOperator;

    public PreOperator(final TeXFormFactory factory, final int precedence, final String oper) {
      super(factory);
      fPrecedence = precedence;
      fOperator = oper;
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      precedenceOpen(buf, precedence);
      buf.append(fOperator);
      fFactory.convertInternal(buf, f.arg1(), fPrecedence);
      precedenceClose(buf, precedence);
      return true;
    }

    public void precedenceClose(final StringBuilder buf, final int precedence) {
      if (precedence >= fPrecedence) {
        buf.append("\\right) ");
      }
    }

    public void precedenceOpen(final StringBuilder buf, final int precedence) {
      if (precedence >= fPrecedence) {
        buf.append("\\left( ");
      }
    }
  }

  private static final class Product extends Sum {
    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() >= 3) {
        return iteratorStep(buf, "\\prod", f, 2);
      }
      return false;
    }
  }

  private static final class Rational extends AbstractOperator {

    public Rational() {
      super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "/");
    }

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return super.convert(buf, f, precedence);
      }
      precedenceOpen(buf, precedence);
      buf.append("\\frac{");
      fFactory.convertInternal(buf, f.arg1(), fPrecedence);
      buf.append("}{");
      fFactory.convertInternal(buf, f.arg2(), fPrecedence);
      buf.append('}');
      precedenceClose(buf, precedence);
      return true;
    }
  }

  private static final class Style extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      IExpr arg1 = f.arg1();
      IExpr arg2 = f.arg2();
      if (arg2.isBuiltInSymbol()) {
        if (((IBuiltInSymbol) arg2)
            .isSymbolID(
                ID.Black, ID.Brown, ID.Blue, ID.Cyan, ID.Green, ID.Pink, ID.Red, ID.Yellow,
                ID.White)) {
          buf.append("\\textcolor{");
          buf.append(arg2.toString().toLowerCase());
          buf.append("}{");
          fFactory.convertInternal(buf, arg1, 0);
          buf.append("}");
          return true;
        }
      }
      return false;
    }
  }

  private static final class Subscript extends AbstractConverter {
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() < 3) {
        return false;
      }
      IExpr arg1 = f.arg1();

      // http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
      // For powers with more than one digit, surround the power with {}.
      buf.append('{');
      fFactory.convertInternal(buf, arg1, precedence);
      buf.append('}');
      buf.append("_");

      buf.append('{');

      for (int i = 2; i < f.size(); i++) {
        fFactory.convertInternal(buf, f.get(i), precedence);
        if (i < f.size() - 1) {
          buf.append(',');
        }
      }
      buf.append('}');
      return true;
    }
  }

  private static final class Subsuperscript extends AbstractConverter {

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 4) {
        return false;
      }
      IExpr arg1 = f.arg1();
      IExpr arg2 = f.arg2();
      IExpr arg3 = f.arg3();

      // http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
      // For powers with more than one digit, surround the power with {}.
      buf.append('{');
      fFactory.convertInternal(buf, arg1, Integer.MAX_VALUE);
      buf.append('}');
      buf.append("_");

      buf.append('{');
      fFactory.convertInternal(buf, arg2, Integer.MAX_VALUE);
      buf.append('}');
      buf.append("^");

      buf.append('{');
      fFactory.convertInternal(buf, arg3, Integer.MAX_VALUE);
      buf.append('}');
      return true;
    }
  }

  private static class Sum extends AbstractConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() >= 3) {
        return iteratorStep(buf, "\\sum", f, 2);
      }
      return false;
    }

    /**
     * See <a href="http://en.wikibooks.org/wiki/LaTeX/Mathematics">Wikibooks -
     * LaTeX/Mathematics</a>
     *
     * @param buf
     * @param mathSymbol the symbol for Sum or Product expressions
     * @param f
     * @param i
     * @return <code>true</code> if the expression could be transformed to LaTeX
     */
    public boolean iteratorStep(
        final StringBuilder buf, final String mathSymbol, final IAST f, int i) {
      if (i >= f.size()) {
        buf.append(" ");
        fFactory.convertSubExpr(buf, f.arg1(), 0);
        return true;
      }
      if (f.get(i).isList()) {
        try {
          IIterator<IExpr> iterator = Iterator.create((IAST) f.get(i), i, EvalEngine.get());
          if (iterator.isValidVariable() && iterator.getStep().isOne()) {
            buf.append(mathSymbol);
            buf.append("_{");
            fFactory.convertSubExpr(buf, iterator.getVariable(), 0);
            buf.append(" = ");
            fFactory.convertSubExpr(buf, iterator.getLowerLimit(), 0);
            buf.append("}^{");
            fFactory.convertInternal(buf, iterator.getUpperLimit(), 0);
            buf.append('}');
            if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
              return false;
            }
            return true;
          }
        } catch (final ValidateException ve) {
          IOFunctions.printMessage(S.Sum, ve, EvalEngine.get());
        }
        return false;
      } else if (f.get(i).isSymbol()) {
        ISymbol symbol = (ISymbol) f.get(i);
        buf.append(mathSymbol);
        buf.append("_{");
        fFactory.convertSymbol(buf, symbol);
        buf.append("}");
        if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
          return false;
        }
        return true;
      }
      return false;
    }
  }

  private static final class Superscript extends AbstractConverter {

    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      IExpr arg1 = f.arg1();
      IExpr arg2 = f.arg2();

      // http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
      // For powers with more than one digit, surround the power with {}.
      buf.append('{');
      fFactory.convertInternal(buf, arg1, 0);
      buf.append('}');

      buf.append("^");
      buf.append('{');
      fFactory.convertInternal(buf, arg2, 0);
      buf.append('}');
      return true;
    }
  }

  private static final class TeXFunction extends AbstractConverter {

    String fFunctionName;

    public TeXFunction(final TeXFormFactory factory, final String functionName) {
      super(factory);
      fFunctionName = functionName;
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      buf.append('\\');
      buf.append(fFunctionName);
      buf.append('(');
      for (int i = 1; i < f.size(); i++) {
        fFactory.convertInternal(buf, f.get(i), 0);
        if (i < f.argSize()) {
          buf.append(',');
        }
      }
      buf.append(')');
      return true;
    }
  }

  private static final class Times extends AbstractOperator {
    public static final int NO_SPECIAL_CALL = 0;

    public static final int PLUS_CALL = 1;

    // public static Times CONST = new Times();

    public Times() {
      super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "\\,");
    }

    /**
     * Converts a given function into the corresponding TeX output
     *
     * @param buf StringBuilder for TeX output
     * @param f The math function which should be converted to TeX
     */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      return convertTimesFraction(buf, f, precedence, NO_SPECIAL_CALL);
    }

    /**
     * Try to split a given <code>Times[...]</code> function into nominator and denominator and add
     * the corresponding TeX output
     *
     * @param buf StringBuilder for TeX output
     * @param f The math function which should be converted to TeX
     * @precedence
     * @caller
     */
    public boolean convertTimesFraction(
        final StringBuilder buf, final IAST f, final int precedence, final int caller) {
      IExpr[] parts = Algebra.fractionalPartsTimesPower(f, false, true, false, false, false, false);
      if (parts == null) {
        convertTimesOperator(buf, f, precedence, caller);
        return true;
      }
      final IExpr numerator = parts[0];
      final IExpr denominator = parts[1];
      if (!denominator.isOne()) {
        if (caller == PLUS_CALL) {
          buf.append('+');
        }
        precedenceOpen(buf, precedence);
        buf.append("\\frac{");
        // insert numerator in buffer:
        if (numerator.isTimes()) {
          convertTimesOperator(buf, (IAST) numerator, fPrecedence, NO_SPECIAL_CALL);
        } else {
          fFactory.convertInternal(buf, numerator, 0);
        }
        buf.append("}{");
        // insert denominator in buffer:
        if (denominator.isTimes()) {
          convertTimesOperator(buf, (IAST) denominator, fPrecedence, NO_SPECIAL_CALL);
        } else {
          fFactory.convertInternal(buf, denominator, 0);
        }
        buf.append('}');
        precedenceClose(buf, precedence);
      } else {
        if (numerator.isTimes()) {
          convertTimesOperator(buf, (IAST) numerator, fPrecedence, NO_SPECIAL_CALL);
        } else {
          fFactory.convertInternal(buf, numerator, precedence);
        }
      }

      return true;
    }

    private boolean convertTimesOperator(
        final StringBuilder buf, final IAST timesAST, final int precedence, final int caller) {
      int size = timesAST.size();
      IExpr arg1 = F.NIL;
      String timesOperator = "\\,";
      if (size > 1) {
        arg1 = timesAST.arg1();
        if (arg1.isMinusOne()) {
          if (size == 2) {
            precedenceOpen(buf, precedence);
            fFactory.convertInternal(buf, arg1, fPrecedence);
          } else {
            if (caller == PLUS_CALL) {
              buf.append(" - ");
              if (size == 3) {
                fFactory.convertInternal(buf, timesAST.arg2(), fPrecedence);
                return true;
              }
            } else {
              precedenceOpen(buf, precedence);
              buf.append(" - ");
            }
          }
        } else if (arg1.isOne()) {
          if (size == 2) {
            precedenceOpen(buf, precedence);
            fFactory.convertInternal(buf, arg1, fPrecedence);
          } else {
            if (caller == PLUS_CALL) {
              if (size == 3) {
                buf.append(" + ");
                fFactory.convertInternal(buf, timesAST.arg2(), fPrecedence);
                return true;
              }
            } else {
              precedenceOpen(buf, precedence);
            }
          }
        } else {
          if (caller == PLUS_CALL) {
            if ((arg1.isReal()) && arg1.isNegative()) {
              buf.append(" - ");
              arg1 = ((ISignedNumber) arg1).opposite();
            } else {
              buf.append(" + ");
            }
          } else {
            precedenceOpen(buf, precedence);
          }
          fFactory.convertInternal(buf, arg1, fPrecedence);
          if (fOperator.compareTo("") != 0) {
            if (size > 2) {
              if (isTeXNumberDigit(timesAST.arg1()) || isTeXNumberDigit(timesAST.arg2())) {
                // Issue #67, #117: if we have 2 TeX number
                // expressions we use
                // the \cdot operator see
                // http://tex.stackexchange.com/questions/40794/when-should-cdot-be-used-to-indicate-multiplication
                timesOperator = "\\cdot ";
              } else {
                for (int i = 2; i < size; i++) {
                  if ((i < timesAST.argSize())) {
                    if (isTeXNumberDigit(timesAST.get(i))
                        && isTeXNumberDigit(timesAST.get(i + 1))) {
                      timesOperator = "\\cdot ";
                      break;
                    }
                  }
                }
              }
              buf.append(timesOperator);
            }
          }
        }
      }

      for (int i = 2; i < size; i++) {
        if (i == 2 && (arg1.isOne() || arg1.isMinusOne())) {
          fFactory.convertInternal(buf, timesAST.get(i), precedence);
        } else {
          fFactory.convertInternal(buf, timesAST.get(i), fPrecedence);
        }
        if (i < timesAST.argSize()) {
          buf.append(timesOperator);
        }
        // if ((i < timesAST.argSize()) && (fOperator.compareTo("") != 0)) {
        // if (isTeXNumberDigit(timesAST.get(i)) && isTeXNumberDigit(timesAST.get(i + 1))) {
        // // Issue #67, #117: if we have 2 TeX number expressions we
        // // use
        // // the \cdot operator see
        // //
        // http://tex.stackexchange.com/questions/40794/when-should-cdot-be-used-to-indicate-multiplication
        // buf.append("\\cdot ");
        // } else {
        // buf.append("\\,");
        // }
        // }
      }
      precedenceClose(buf, precedence);
      return true;
    }

    /**
     * Does the TeX Form of <code>expr</code> begin with a number digit?
     *
     * @param expr
     * @return
     */
    private boolean isTeXNumberDigit(IExpr expr) {
      if (expr.isNumber()) {
        return true;
      }
      if (expr.isPower() && expr.base().isNumber() && !expr.exponent().isFraction()) {
        return true;
      }
      return false;
    }
  }

  private static final class UnaryFunction extends AbstractConverter {
    String pre;
    String post;

    /** constructor will be called by reflection */
    public UnaryFunction(String pre, String post) {
      this.pre = pre;
      this.post = post;
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      buf.append(pre);
      fFactory.convertInternal(buf, f.arg1(), 0);
      buf.append(post);
      return true;
    }
  }

  private static final class Zeta extends AbstractConverter {
    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
      fFactory.convertAST(buf, f, "zeta ");
      return true;
    }
  }

  /** Table for constant symbols */
  public static final Map<String, String> CONSTANT_SYMBOLS =
      ParserConfig.TRIE_STRING2STRING_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  /** Table for constant expressions */
  public static final Map<IExpr, String> CONSTANT_EXPRS = new HashMap<IExpr, String>(199);

  /** Description of the Field */
  public static final Map<ISymbol, AbstractConverter> operTab =
      new HashMap<ISymbol, AbstractConverter>(199);

  public static final boolean USE_IDENTIFIERS = false;

  private int plusPrec;

  // protected NumberFormat fNumberFormat = null;
  private boolean fUseSignificantFigures = false;
  private int fExponentFigures;
  private int fSignificantFigures;

  /** Constructor */
  public TeXFormFactory() {
    this(-1, -1);
  }

  /**
   * @param exponentFigures
   * @param significantFigures
   */
  public TeXFormFactory(int exponentFigures, int significantFigures) {
    fExponentFigures = exponentFigures;
    fSignificantFigures = significantFigures;
    init();
  }

  public void convertApcomplex(
      final StringBuilder buf, final Apcomplex dc, final int precedence, boolean caller) {
    if (Precedence.PLUS < precedence) {
      if (caller == PLUS_CALL) {
        buf.append(" + ");
        caller = false;
      }
      buf.append("\\left( ");
    }
    Apfloat realPart = dc.real();
    Apfloat imaginaryPart = dc.imag();
    boolean realZero = realPart.equals(Apcomplex.ZERO);
    boolean imaginaryZero = imaginaryPart.equals(Apcomplex.ZERO);
    if (realZero && imaginaryZero) {
      convertDoubleString(buf, "0.0", Precedence.PLUS, false);
    } else {
      if (!realZero) {
        buf.append(convertApfloatToFormattedString(realPart));
        if (!imaginaryZero) {
          buf.append(" + ");
          final boolean isNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;
          convertDoubleString(
              buf, convertApfloatToFormattedString(imaginaryPart), Precedence.TIMES, isNegative);
          buf.append("\\,"); // InvisibleTimes
          buf.append("i ");
        }
      } else {
        if (caller == PLUS_CALL) {
          buf.append("+");
          caller = false;
        }

        final boolean isNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;
        convertDoubleString(
            buf, convertApfloatToFormattedString(imaginaryPart), Precedence.TIMES, isNegative);
        buf.append("\\,"); // InvisibleTimes
        buf.append("i ");
      }
    }
    if (Precedence.PLUS < precedence) {
      buf.append("\\right) ");
    }
  }

  private String convertApfloatToFormattedString(Apfloat value) {
    StringBuilder buf = new StringBuilder();
    int numericPrecision = (int) EvalEngine.get().getNumericPrecision();
    ApfloatToMMA.apfloatToTeX(buf, value, numericPrecision, numericPrecision,
        fUseSignificantFigures);
    return buf.toString();
  }

  // public static String convertApfloat(Apfloat num) {
  // String str = num.toString();
  // int index = str.indexOf('e');
  // if (index > 0) {
  // String exponentStr = str.substring(index + 1);
  // String result = str.substring(0, index);
  // return result + "*10^" + exponentStr;
  // }
  // return str;
  // }

  public boolean convert(final StringBuilder buf, final IExpr o, final int precedence) {
    try {
      convertInternal(buf, o, precedence);
      if (buf.length() >= Config.MAX_OUTPUT_SIZE) {
        return false;
      }
      return true;
    } catch (RuntimeException rex) {
      LOGGER.debug("TeXFormFactory.convert() failed", rex);
    } catch (OutOfMemoryError oome) {
    }
    return false;
  }

  private void convertInternal(final StringBuilder buf, final Object o, final int precedence) {
    if (o instanceof IExpr) {
      IExpr expr = (IExpr) o;
      String str = CONSTANT_EXPRS.get(expr);
      if (str != null) {
        buf.append(str);
        return;
      }
    }
    if (o instanceof IAST) {
      final IAST f = ((IAST) o);
      IExpr h = f.head();
      if (h.isSymbol()) {
        final AbstractConverter converter = operTab.get((h));
        if (converter != null) {
          converter.setFactory(this);
          if (converter.convert(buf, f, precedence)) {
            return;
          }
        }
      }
      convertAST(buf, f, precedence);
      return;
    }
    if (o instanceof IInteger) {
      convertInteger(buf, (IInteger) o, precedence);
      return;
    }
    if (o instanceof IFraction) {
      convertFraction(buf, (IFraction) o, precedence);
      return;
    }
    if (o instanceof INum) {
      convertDouble(buf, (INum) o, precedence);
      return;
    }
    if (o instanceof IComplexNum) {
      if (o instanceof ApcomplexNum) {
        convertApcomplex(buf, ((ApcomplexNum) o).apcomplexValue(), precedence, NO_PLUS_CALL);
        return;
      }
      convertDoubleComplex(buf, (IComplexNum) o, precedence);
      return;
    }
    if (o instanceof IComplex) {
      convertComplex(buf, (IComplex) o, precedence);
      return;
    }
    if (o instanceof ISymbol) {
      convertSymbol(buf, (ISymbol) o);
      return;
    }
    // if (o instanceof BigFraction) {
    // convertFraction(buf, (BigFraction) o, precedence);
    // return;
    // }
    convertString(buf, o.toString());
  }

  public void convertAST(StringBuilder buf, final IAST list, int precedence) {
    if (!list.isPresent()) {
      buf.append("NIL");
      return;
    }
    IExpr header = list.head();
    if (!header.isSymbol()) {
      // print expressions like: f(#1, y)& [x]

      IAST[] derivStruct = list.isDerivativeAST1();
      if (derivStruct != null) {
        IAST a1Head = derivStruct[0];
        IAST headAST = derivStruct[1];
        if (a1Head.isAST1()
            && headAST.isAST1()
            && (headAST.arg1().isSymbol() || headAST.arg1().isAST())) {
          try {
            IExpr symbolOrAST = headAST.arg1();
            int n = a1Head.arg1().toIntDefault();
            if (n == 1 || n == 2) {
              convertInternal(buf, symbolOrAST, Integer.MAX_VALUE);
              if (n == 1) {
                buf.append("'");
              } else if (n == 2) {
                buf.append("''");
              }
              if (derivStruct[2] != null) {
                convertArgs(buf, symbolOrAST, list);
              }
              return;
            }
            convertInternal(buf, symbolOrAST, Integer.MAX_VALUE);
            buf.append("^{(");
            convertInternal(buf, a1Head.arg1(), Integer.MIN_VALUE);
            buf.append(")}");
            if (derivStruct[2] != null) {
              convertArgs(buf, symbolOrAST, list);
            }
            return;

          } catch (ArithmeticException ae) {

          }
        }
      }

      convertInternal(buf, header, Integer.MIN_VALUE);
      convertFunctionArgs(buf, list);
      return;
    }
    if (header.isBuiltInSymbol()) {
      int functionID = ((ISymbol) header).ordinal();
      if (functionID > ID.UNKNOWN) {
        switch (functionID) {
          case ID.Inequality:
            if (list.size() > 3 && convertInequality(buf, list, precedence)) {
              return;
            }
            break;
          case ID.Interval:
            if (list.size() > 1 && list.first().isASTSizeGE(S.List, 2)) {
              IAST interval = IntervalSym.normalize(list);
              buf.append("Interval(");
              for (int i = 1; i < interval.size(); i++) {
                buf.append("\\{");

                IAST subList = (IAST) interval.get(i);
                IExpr min = subList.arg1();
                IExpr max = subList.arg2();
                if (min instanceof INum) {
                  convertDoubleString(
                      buf, convertDoubleToFormattedString(min.evalDouble()), 0, false);
                } else {
                  convertInternal(buf, min, 0);
                }
                buf.append(",");
                if (max instanceof INum) {
                  convertDoubleString(
                      buf, convertDoubleToFormattedString(max.evalDouble()), 0, false);
                } else {
                  convertInternal(buf, max, 0);
                }
                buf.append("\\}");
                if (i < interval.size() - 1) {
                  buf.append(",");
                }
              }
              buf.append(")");
              return;
            }
            break;
          case ID.SparseArray:
            if (list.isSparseArray()) {
              buf.append("\\textnormal{");
              buf.append(list.toString());
              buf.append("}");
              return;
            }
            break;
        }
      }
    }

    if (list.isAssociation()) {
      convertAssociation(buf, (IAssociation) list, 0);
      return;
    }
    convertHead(buf, header);
    buf.append("(");
    for (int i = 1; i < list.size(); i++) {
      convertInternal(buf, list.get(i), 0);
      if (i < list.argSize()) {
        buf.append(',');
      }
    }
    buf.append(")");
  }

  public void convertArgs(final StringBuilder buf, IExpr head, final IAST function) {
    int functionSize = function.size();
    if (head.isAST()) {
      buf.append("[");
    } else {
      buf.append("(");
    }
    if (functionSize > 1) {
      convertInternal(buf, function.arg1(), Integer.MIN_VALUE);
    }
    for (int i = 2; i < functionSize; i++) {
      convertInternal(buf, function.get(i), 0);
      if (i < function.argSize()) {
        buf.append(',');
      }
    }
    if (head.isAST()) {
      buf.append("]");
    } else {
      buf.append(")");
    }
  }

  public void convertFunctionArgs(final StringBuilder buf, final IAST list) {
    buf.append("(");
    list.joinToString(
        buf, //
        (b, x) -> convertInternal(b, x, Integer.MIN_VALUE),
        ",");
    buf.append(")");
  }

  /**
   * Convert an association to TeX. <br>
   * <code>&lt;|a -> x, b -> y, c -> z|&gt;</code> gives <br>
   * <code>\langle|a\to x,b\to y,c\to z|\rangle</code>
   *
   * @param buf
   * @param assoc
   * @param precedence
   * @return
   */
  public boolean convertAssociation(
      final StringBuilder buf, final IAssociation assoc, final int precedence) {
    IAST ast = assoc.normal(false);
    buf.append("\\langle|");
    if (ast.size() > 1) {
      convertInternal(buf, ast.arg1(), 0);
      for (int i = 2; i < ast.size(); i++) {
        buf.append(',');
        convertInternal(buf, ast.get(i), 0);
      }
    }
    buf.append("|\\rangle");

    return true;
  }

  private boolean convertInequality(
      final StringBuilder buf, final IAST inequality, final int precedence) {
    StringBuilder tempBuffer = new StringBuilder();

    if (Precedence.EQUAL < precedence) {
      tempBuffer.append("(");
    }

    final int listSize = inequality.size();
    int i = 1;
    while (i < listSize) {
      convertInternal(tempBuffer, inequality.get(i++), 0);
      if (i == listSize) {
        if (Precedence.EQUAL < precedence) {
          tempBuffer.append(")");
        }
        buf.append(tempBuffer);
        return true;
      }
      IExpr head = inequality.get(i++);
      if (head.isBuiltInSymbol()) {
        int id = ((IBuiltInSymbol) head).ordinal();
        switch (id) {
          case ID.Equal:
            tempBuffer.append(" == ");
            break;
          case ID.Greater:
            tempBuffer.append(" > ");
            break;
          case ID.GreaterEqual:
            tempBuffer.append("\\geq ");
            break;
          case ID.Less:
            tempBuffer.append(" < ");
            break;
          case ID.LessEqual:
            tempBuffer.append("\\leq ");
            break;
          case ID.Unequal:
            tempBuffer.append("\\neq ");
            break;
          default:
            return false;
        }
      } else {
        return false;
      }
    }
    if (Precedence.EQUAL < precedence) {
      tempBuffer.append(")");
    }
    buf.append(tempBuffer);
    return true;
  }

  public void convertAST(StringBuilder buf, final IAST f, String headString) {
    if (!f.isPresent()) {
      buf.append("NIL");
      return;
    }
    buf.append(headString);
    buf.append("(");
    for (int i = 1; i < f.size(); i++) {
      convertInternal(buf, f.get(i), 0);
      if (i < f.argSize()) {
        buf.append(',');
      }
    }
    buf.append(")");
  }

  public void convertComplex(final StringBuilder buf, final IComplex c, final int precedence) {
    if (c.isImaginaryUnit()) {
      buf.append("i ");
      return;
    }
    if (c.isNegativeImaginaryUnit()) {
      if (precedence > plusPrec) {
        buf.append("\\left( ");
      }
      buf.append(" - i ");
      if (precedence > plusPrec) {
        buf.append("\\right) ");
      }
      return;
    }
    if (precedence > plusPrec) {
      buf.append("\\left( ");
    }
    IRational re = c.getRealPart();
    IRational im = c.getImaginaryPart();
    if (!re.isZero()) {
      convertInternal(buf, re, 0);
      if (im.compareInt(0) >= 0) {
        buf.append(" + ");
      } else {
        buf.append(" - ");
        im = im.negate();
      }
    }
    convertInternal(buf, im, 0);
    buf.append("\\,"); // InvisibleTimes
    buf.append("i ");
    if (precedence > plusPrec) {
      buf.append("\\right) ");
    }
  }

  public void convertDouble(final StringBuilder buf, final INum d, final int precedence) {
    if (d.isZero()) {
      buf.append(convertDoubleToFormattedString(0.0));
      return;
    }
    final boolean isNegative = d.isNegative();
    if (d instanceof Num) {
      convertDoubleString(
          buf, convertDoubleToFormattedString(d.getRealPart()), precedence, isNegative);
    } else {
      convertDoubleString(
          buf,
          convertApfloatToFormattedString(((ApfloatNum) d).apfloatValue()),
          precedence,
          isNegative);
    }
  }

  public void convertDoubleComplex(
      final StringBuilder buf, final IComplexNum dc, final int precedence) {
    double re = dc.getRealPart();
    double im = dc.getImaginaryPart();
    if (F.isZero(re)) {
      if (F.isNumIntValue(im, 1)) {
        buf.append("i ");
        return;
      }
      if (F.isNumIntValue(im, -1)) {
        if (precedence > plusPrec) {
          buf.append("\\left( ");
        }
        buf.append(" - i ");
        if (precedence > plusPrec) {
          buf.append("\\right) ");
        }
        return;
      }
    }
    if (precedence > plusPrec) {
      buf.append("\\left( ");
    }
    if (!F.isZero(re)) {
      buf.append(convertDoubleToFormattedString(re));
      if (im >= 0.0) {
        buf.append(" + ");
      } else {
        buf.append(" - ");
        im = -im;
      }
    }
    buf.append(convertDoubleToFormattedString(im));
    buf.append("\\,"); // InvisibleTimes
    buf.append("i ");
    if (precedence > plusPrec) {
      buf.append("\\right) ");
    }
  }

  private void convertDoubleString(
      final StringBuilder buf, final String d, final int precedence, final boolean isNegative) {
    if (isNegative && (Precedence.PLUS < precedence)) {
      buf.append("\\left( ");
    }
    buf.append(d);
    if (isNegative && (Precedence.PLUS < precedence)) {
      buf.append("\\right) ");
    }
  }

  protected String convertDoubleToFormattedString(double dValue) {
    if (fSignificantFigures > 0) {
      try {
        StringBuilder buf = new StringBuilder();
        DoubleToMMA.doubleToMMA(buf, dValue, fExponentFigures, fSignificantFigures, true);
        return buf.toString();
      } catch (IOException ioex) {
        LOGGER.error("TeXFormFactory.convertDoubleToFormattedString() failed", ioex);
      }
    }
    return Double.toString(dValue);
  }

  public void convertFraction(final StringBuilder buf, final IFraction f, final int precedence) {
    if (f.isNegative() && (precedence > plusPrec)) {
      buf.append("\\left( ");
    }
    if (f.denominator().isOne()) {
      buf.append(f.numerator().toString());
    } else {
      buf.append("\\frac{");
      buf.append(f.toBigNumerator().toString());
      buf.append("}{");
      buf.append(f.toBigDenominator().toString());
      buf.append('}');
    }
    if (f.isNegative() && (precedence > plusPrec)) {
      buf.append("\\right) ");
    }
  }

  public void convertHead(final StringBuilder buf, final IExpr obj) {
    if (obj instanceof ISymbol) {
      String str = ((ISymbol) obj).getSymbolName();
      final Object ho = CONSTANT_SYMBOLS.get(((ISymbol) obj).getSymbolName());
      if ((ho != null) && ho.equals(AST2Expr.TRUE_STRING)) {
        buf.append('\\');
        buf.append(str);
        return;
      }

      convertHeader(buf, str);
      return;
    }
    convertInternal(buf, obj, 0);
  }

  private void convertHeader(final StringBuilder buf, String str) {
    if (str.length() == 1) {
      buf.append(str);
    } else {
      buf.append("\\text{");
      String header = str;
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(header);
        if (str != null) {
          header = str;
        }
      }
      buf.append(header);
      buf.append('}');
    }
  }

  public void convertInteger(final StringBuilder buf, final IInteger i, final int precedence) {
    if (i.isNegative() && (precedence > plusPrec)) {
      buf.append("\\left( ");
    }
    buf.append(i.toBigNumerator().toString());
    if (i.isNegative() && (precedence > plusPrec)) {
      buf.append("\\right) ");
    }
  }

  /*
   *
   * '{': r'\{', '}': r'\}', '_': r'\_', '$': r'\$', '%': r'\%', '#': r'\#', '&': r'\&',7
   */
  public void convertString(final StringBuilder buf, final String str) {
    buf.append("\\textnormal{");
    String text = str.replaceAll("\\&", "\\\\&");
    text = text.replaceAll("\\#", "\\\\#");
    text = text.replaceAll("\\%", "\\\\%");
    text = text.replace("$", "\\$");
    text = text.replaceAll("\\_", "\\\\_");
    text = text.replace("{", "\\{");
    text = text.replace("}", "\\}");
    text = text.replaceAll("\\<", "\\$<\\$");
    text = text.replaceAll("\\>", "\\$>\\$");
    buf.append(text);
    buf.append("}");
  }

  private void convertSubExpr(StringBuilder buf, IExpr o, int precedence) {
    if (o.isAST()) {
      buf.append("{");
    }
    convertInternal(buf, o, precedence);
    if (o.isAST()) {
      buf.append("}");
    }
  }

  public void convertSymbol(final StringBuilder buf, final ISymbol sym) {
    Context context = sym.getContext();
    if (context == Context.DUMMY) {
      buf.append(sym.getSymbolName());
      return;
    }
    String headStr = sym.getSymbolName();
    if (headStr.length() == 1) {
      String c = Characters.unicodeName(headStr.charAt(0));
      if (c != null) {
        final Object convertedSymbol = CONSTANT_SYMBOLS.get(c);
        if (convertedSymbol != null) {
          convertConstantSymbol(buf, sym, convertedSymbol);
          return;
        }
      }
    }
    if (context.equals(Context.SYSTEM) || context.isGlobal()) {
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS && context.equals(Context.SYSTEM)) {
        String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(headStr);
        if (str != null) {
          headStr = str;
        }
      }
      final Object convertedSymbol = CONSTANT_SYMBOLS.get(headStr);
      if (convertedSymbol == null) {
        buf.append(headStr);
      } else {
        convertConstantSymbol(buf, sym, convertedSymbol);
      }
      return;
    }

    if (EvalEngine.get().getContextPath().contains(context)) {
      buf.append(sym.getSymbolName());
    } else {
      buf.append(context.toString() + sym.getSymbolName());
    }
  }

  private void convertConstantSymbol(
      final StringBuilder buf, final ISymbol sym, final Object convertedSymbol) {
    if (convertedSymbol.equals(AST2Expr.TRUE_STRING)) {
      buf.append('\\');
      buf.append(sym.getSymbolName());
      return;
    }
    if (convertedSymbol instanceof Operator) {
      ((Operator) convertedSymbol).convert(buf);
      return;
    }
    buf.append(convertedSymbol.toString());
  }

  public void init() {
    plusPrec = ASTNodeFactory.RELAXED_STYLE_FACTORY.get("Plus").getPrecedence();
    // timesPrec =
    // ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence();
    operTab.put(S.Abs, new UnaryFunction("|", "|"));
    operTab.put(S.Binomial, new Binomial());
    operTab.put(S.Ceiling, new UnaryFunction(" \\left \\lceil ", " \\right \\rceil "));
    operTab.put(S.Conjugate, new Conjugate());
    operTab.put(S.Complex, new Complex());
    operTab.put(
        S.CompoundExpression,
        new AbstractOperator(
            ASTNodeFactory.MMA_STYLE_FACTORY.get("CompoundExpression").getPrecedence(), ", "));
    operTab.put(S.D, new D());
    operTab.put(S.Defer, new HoldForm());
    operTab.put(S.DirectedInfinity, new DirectedInfinity());
    operTab.put(S.Floor, new UnaryFunction(" \\left \\lfloor ", " \\right \\rfloor "));
    operTab.put(S.Function, new UnaryFunction("", "\\&"));
    operTab.put(S.HarmonicNumber, new HarmonicNumber());
    operTab.put(S.HoldForm, new HoldForm());
    operTab.put(S.HurwitzZeta, new Zeta());
    operTab.put(S.Integrate, new Integrate());
    operTab.put(S.Limit, new Limit());
    operTab.put(S.List, new List());
    //    operTab.put(S.$RealMatrix, new List());
    //    operTab.put(S.$RealVector, new List());
    operTab.put(S.MatrixForm, new MatrixForm());
    operTab.put(S.TableForm, new TableForm());
    operTab.put(S.Parenthesis, new Parenthesis());
    operTab.put(S.Part, new Part());
    operTab.put(S.Plus, new Plus());
    operTab.put(S.Power, new Power());
    operTab.put(S.Product, new Product());
    operTab.put(S.Rational, new Rational());
    operTab.put(S.Slot, new UnaryFunction("\\text{$\\#$", "}"));
    operTab.put(S.SlotSequence, new UnaryFunction("\\text{$\\#\\#$", "}"));
    operTab.put(S.Sqrt, new UnaryFunction("\\sqrt{", "}"));
    operTab.put(S.Style, new Style());
    operTab.put(S.Subscript, new Subscript());
    operTab.put(S.Subsuperscript, new Subsuperscript());
    operTab.put(S.Sum, new Sum());
    operTab.put(S.Superscript, new Superscript());
    operTab.put(S.Times, new Times());
    operTab.put(S.Zeta, new Zeta());

    operTab.put(S.Condition, new AbstractOperator(this, Precedence.CONDITION, "\\text{/;}"));
    operTab.put(S.Unset, new PostOperator(this, Precedence.UNSET, "\\text{=.}"));
    operTab.put(S.UpSetDelayed, new AbstractOperator(this, Precedence.UPSETDELAYED, "\\text{^:=}"));
    operTab.put(S.UpSet, new AbstractOperator(this, Precedence.UPSET, "\\text{^=}"));
    operTab.put(
        S.NonCommutativeMultiply,
        new AbstractOperator(this, Precedence.NONCOMMUTATIVEMULTIPLY, "\\text{**}"));
    operTab.put(S.PreDecrement, new PreOperator(this, Precedence.PREDECREMENT, "\\text{--}"));
    operTab.put(
        S.ReplaceRepeated, new AbstractOperator(this, Precedence.REPLACEREPEATED, "\\text{//.}"));
    operTab.put(S.MapAll, new AbstractOperator(this, Precedence.MAPALL, "\\text{//@}"));
    operTab.put(S.AddTo, new AbstractOperator(this, Precedence.ADDTO, "\\text{+=}"));
    operTab.put(S.Greater, new AbstractOperator(this, Precedence.GREATER, " > "));
    operTab.put(S.GreaterEqual, new AbstractOperator(this, Precedence.GREATEREQUAL, "\\geq "));
    operTab.put(S.SubtractFrom, new AbstractOperator(this, Precedence.SUBTRACTFROM, "\\text{-=}"));
    operTab.put(S.Subtract, new AbstractOperator(this, Precedence.SUBTRACT, " - "));
    operTab.put(
        S.CompoundExpression, new AbstractOperator(this, Precedence.COMPOUNDEXPRESSION, ";"));
    operTab.put(S.DivideBy, new AbstractOperator(this, Precedence.DIVIDEBY, "\\text{/=}"));
    operTab.put(S.StringJoin, new AbstractOperator(this, Precedence.STRINGJOIN, "\\text{<>}"));
    operTab.put(S.UnsameQ, new AbstractOperator(this, Precedence.UNSAMEQ, "\\text{=!=}"));
    operTab.put(S.Decrement, new PostOperator(this, Precedence.DECREMENT, "\\text{--}"));
    operTab.put(S.LessEqual, new AbstractOperator(this, Precedence.LESSEQUAL, "\\leq "));
    operTab.put(S.Colon, new AbstractOperator(this, Precedence.COLON, "\\text{:}"));
    operTab.put(S.Increment, new PostOperator(this, Precedence.INCREMENT, "\\text{++}"));
    operTab.put(S.Alternatives, new AbstractOperator(this, Precedence.ALTERNATIVES, "\\text{|}"));
    operTab.put(S.Equal, new AbstractOperator(this, Precedence.EQUAL, " == "));
    operTab.put(S.DirectedEdge, new AbstractOperator(this, Precedence.DIRECTEDEDGE, "\\to "));
    operTab.put(S.Divide, new AbstractOperator(this, Precedence.DIVIDE, "\\text{/}"));
    operTab.put(S.Apply, new AbstractOperator(this, Precedence.APPLY, "\\text{@@}"));
    operTab.put(S.Set, new AbstractOperator(this, Precedence.SET, " = "));
    // operTab.put(F.Minus,
    // new PreOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Minus").getPrecedence(),
    // "\\text{-}"));
    operTab.put(S.Map, new AbstractOperator(this, Precedence.MAP, "\\text{/@}"));
    operTab.put(S.SameQ, new AbstractOperator(this, Precedence.SAMEQ, "\\text{===}"));
    operTab.put(S.Less, new AbstractOperator(this, Precedence.LESS, " < "));
    operTab.put(S.PreIncrement, new PreOperator(this, Precedence.PREINCREMENT, "\\text{++}"));
    operTab.put(S.Unequal, new AbstractOperator(this, Precedence.UNEQUAL, "\\neq "));
    operTab.put(S.Or, new AbstractOperator(this, Precedence.OR, " \\lor "));
    // operTab.put(F.PrePlus,
    // new PreOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("PrePlus").getPrecedence(),
    // "\\text{+}"));
    operTab.put(S.TimesBy, new AbstractOperator(this, Precedence.TIMESBY, "\\text{*=}"));
    operTab.put(S.And, new AbstractOperator(this, Precedence.AND, " \\land "));
    operTab.put(S.Not, new PreOperator(this, Precedence.NOT, "\\neg "));
    operTab.put(S.Factorial, new PostOperator(this, Precedence.FACTORIAL, " ! "));
    operTab.put(S.Factorial2, new PostOperator(this, Precedence.FACTORIAL2, " !! "));

    operTab.put(S.ReplaceAll, new AbstractOperator(this, Precedence.REPLACEALL, "\\text{/.}\\,"));
    operTab.put(
        S.ReplaceRepeated,
        new AbstractOperator(this, Precedence.REPLACEREPEATED, "\\text{//.}\\,"));
    operTab.put(S.Rule, new AbstractOperator(this, Precedence.RULE, "\\to "));
    operTab.put(S.RuleDelayed, new AbstractOperator(this, Precedence.RULEDELAYED, ":\\to "));
    operTab.put(S.Set, new AbstractOperator(this, Precedence.SET, " = "));
    operTab.put(S.SetDelayed, new AbstractOperator(this, Precedence.SETDELAYED, "\\text{:=}\\,"));
    operTab.put(
        S.UndirectedEdge,
        new AbstractOperator(this, Precedence.UNDIRECTEDEDGE, "\\leftrightarrow "));
    operTab.put(
        S.TwoWayRule, new AbstractOperator(this, Precedence.TWOWAYRULE, "\\leftrightarrow "));
    operTab.put(S.CenterDot, new AbstractOperator(this, Precedence.CENTERDOT, "\\cdot "));
    operTab.put(S.CircleDot, new AbstractOperator(this, Precedence.CIRCLEDOT, "\\odot "));

    operTab.put(S.Sin, new TeXFunction(this, "sin "));
    operTab.put(S.Cos, new TeXFunction(this, "cos "));
    operTab.put(S.Tan, new TeXFunction(this, "tan "));
    operTab.put(S.Cot, new TeXFunction(this, "cot "));
    operTab.put(S.Sinh, new TeXFunction(this, "sinh "));
    operTab.put(S.Cosh, new TeXFunction(this, "cosh "));
    operTab.put(S.Tanh, new TeXFunction(this, "tanh "));
    operTab.put(S.Coth, new TeXFunction(this, "coth "));
    operTab.put(S.Csc, new TeXFunction(this, "csc "));
    operTab.put(S.Sec, new TeXFunction(this, "sec "));
    operTab.put(S.ArcSin, new TeXFunction(this, "arcsin "));
    operTab.put(S.ArcCos, new TeXFunction(this, "arccos "));
    operTab.put(S.ArcTan, new TeXFunction(this, "arctan "));
    operTab.put(S.ArcCot, new TeXFunction(this, "arccot "));
    operTab.put(S.ArcSinh, new TeXFunction(this, "arcsinh "));
    operTab.put(S.ArcCosh, new TeXFunction(this, "arccosh "));
    operTab.put(S.ArcTanh, new TeXFunction(this, "arctanh "));
    operTab.put(S.ArcCoth, new TeXFunction(this, "arccoth "));
    operTab.put(S.Log, new TeXFunction(this, "log "));

    CONSTANT_SYMBOLS.put("Alpha", "\\alpha");
    CONSTANT_SYMBOLS.put("Beta", "\\beta");
    CONSTANT_SYMBOLS.put("Chi", "\\chi");
    CONSTANT_SYMBOLS.put("Delta", "\\delta");
    CONSTANT_SYMBOLS.put("Epsilon", "\\epsilon");
    CONSTANT_SYMBOLS.put("Phi", "\\phi");
    CONSTANT_SYMBOLS.put("Gamma", "\\gamma");
    CONSTANT_SYMBOLS.put("Eta", "\\eta");
    CONSTANT_SYMBOLS.put("Iota", "\\iota");
    CONSTANT_SYMBOLS.put("Kappa", "\\kappa");
    CONSTANT_SYMBOLS.put("Lambda", "\\lambda");
    CONSTANT_SYMBOLS.put("Mu", "\\mu");
    CONSTANT_SYMBOLS.put("Nu", "\\nu");
    CONSTANT_SYMBOLS.put("Omicron", "\\omicron");
    CONSTANT_SYMBOLS.put("Theta", "\\theta");
    CONSTANT_SYMBOLS.put("Rho", "\\rho");
    CONSTANT_SYMBOLS.put("Sigma", "\\sigma");
    CONSTANT_SYMBOLS.put("Tau", "\\tau");
    CONSTANT_SYMBOLS.put("Upsilon", "\\upsilon");
    CONSTANT_SYMBOLS.put("Omega", "\\omega");
    CONSTANT_SYMBOLS.put("Xi", "\\xi");
    CONSTANT_SYMBOLS.put("Psi", "\\psi");
    CONSTANT_SYMBOLS.put("Zeta", "\\zeta");

    CONSTANT_SYMBOLS.put("alpha", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("beta", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("chi", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("delta", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("epsilon", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("phi", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("gamma", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("eta", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("iota", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("varphi", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("kappa", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("lambda", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("mu", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("nu", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("omicron", AST2Expr.TRUE_STRING);
    // see F.Pi
    // CONSTANT_SYMBOLS.put("pi", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("theta", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("rho", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("sigma", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("tau", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("upsilon", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("varomega", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("omega", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("xi", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("psi", AST2Expr.TRUE_STRING);
    CONSTANT_SYMBOLS.put("zeta", AST2Expr.TRUE_STRING);

    CONSTANT_EXPRS.put(S.Catalan, "C");
    CONSTANT_EXPRS.put(S.Degree, "{}^{\\circ}");
    CONSTANT_EXPRS.put(S.E, "e");
    CONSTANT_EXPRS.put(S.Glaisher, "A");
    CONSTANT_EXPRS.put(S.GoldenRatio, "\\phi");
    CONSTANT_EXPRS.put(S.EulerGamma, "\\gamma");
    CONSTANT_EXPRS.put(S.Khinchin, "K");
    CONSTANT_EXPRS.put(S.Pi, "\\pi");
    CONSTANT_EXPRS.put(F.CInfinity, "\\infty");
    CONSTANT_EXPRS.put(F.CNInfinity, "-\\infty");
  }
}
