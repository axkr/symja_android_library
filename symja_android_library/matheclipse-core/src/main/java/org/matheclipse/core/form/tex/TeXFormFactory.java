package org.matheclipse.core.form.tex;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.util.Iterator;
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
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Characters;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Precedence;
import org.matheclipse.parser.trie.TrieMatch;

/**
 * Generates TeX presentation output. This class is not thread safe. Create a new instance for every
 * usage.
 */
public class TeXFormFactory {
  private static final Logger LOGGER = LogManager.getLogger(TeXFormFactory.class);

  /** The conversion wasn't called with an operator preceding the <code>IExpr</code> object. */
  public static final boolean NO_PLUS_CALL = false;

  /**
   * The conversion was called with a &quot;+&quot; operator preceding the <code>IExpr</code>
   * object.
   */
  public static final boolean PLUS_CALL = true;

  private static final class Binomial extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      buffer.append('{');
      fFactory.convertInternal(buffer, f.arg1(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      buffer.append("\\choose ");
      fFactory.convertInternal(buffer, f.arg2(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      buffer.append('}');
      return true;
    }
  }

  private static final class Complex extends TeXFormOperator {

    public Complex() {
      super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence(), " + ");
    }

    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return super.convert(buffer, f, precedence);
      }
      precedenceOpen(buffer, precedence);
      IExpr arg1 = f.arg1();
      boolean reZero = arg1.isZero();
      IExpr arg2 = f.arg2();
      boolean imZero = arg2.isZero();

      if (!reZero) {
        fFactory.convertInternal(buffer, arg1, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      }
      if (!imZero) {
        if (!reZero && !arg2.isNegativeSigned()) {
          buffer.append(" + ");
        }
        if (arg2.isMinusOne()) {
          buffer.append(" - ");
        } else if (!arg2.isOne()) {
          fFactory.convertInternal(buffer, arg2, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
          buffer.append("\\,"); // InvisibleTimes
        }
        buffer.append("\\imag");
      }
      return true;
    }
  }

  private static final class IntervalData extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() == 1) {
        buffer.append("\\emptyset ");
      } else {
        for (int i = 1; i < f.size(); i++) {
          IExpr expr = f.get(i);
          if (!expr.isAST(S.List, 5)) {
            return false;
          }
        }
        for (int i = 1; i < f.size(); i++) {
          IAST list4 = (IAST) f.get(i);
          IExpr min = list4.arg1();
          IBuiltInSymbol left = (IBuiltInSymbol) list4.arg2();
          IBuiltInSymbol right = (IBuiltInSymbol) list4.arg3();
          IExpr max = list4.arg4();
          if (left == S.Less) {
            buffer.append("\\left(");
          } else {
            buffer.append("\\left[");
          }
          fFactory.convertInternal(buffer, min, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
          buffer.append(", ");
          fFactory.convertInternal(buffer, max, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
          if (right == S.Less) {
            buffer.append("\\right) ");
          } else {
            buffer.append("\\right] ");
          }
          if (i < f.argSize()) {
            buffer.append("\\cup ");
          }
        }
      }
      return true;
    }

  }

  private static final class Conjugate extends TeXFormOperator {
    public Conjugate() {
      super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "^*");
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      precedenceOpen(buffer, precedence);
      fFactory.convertInternal(buffer, f.arg1(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      buffer.append("^*");
      precedenceClose(buffer, precedence);
      return true;
    }
  }

  public static final class D extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.isAST2()) {
        IExpr arg2 = f.arg2();
        int n = 1;
        if (arg2.isList2() && arg2.second().isInteger()) {
          n = arg2.second().toIntDefault();
          if (n <= 0) {
            return false;
          }
          arg2 = arg2.first();
        }
        buffer.append("\\frac{" + fFactory.symbolOptions.getDerivativeSymbol() + " ");
        if (n > 1) {
          buffer.append("^" + n + " ");
        }
        fFactory.convertInternal(buffer, f.arg1(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
        buffer.append("}{" + fFactory.symbolOptions.getDerivativeSymbol() + " ");
        fFactory.convertInternal(buffer, arg2, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
        if (n > 1) {
          buffer.append("^" + n);
        }
        buffer.append("}");

        return true;
      }
      return false;
    }
  }

  private static final class DirectedInfinity extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.isComplexInfinity()) {
        buffer.append("ComplexInfinity");
        return true;
      } else if (f.isAST1()) {
        if (f.arg1().isOne()) {
          buffer.append("\\infty");
          return true;
        } else if (f.arg1().isMinusOne()) {
          buffer.append("- \\infty");
          return true;
        }
      }
      return false;
    }
  }

  private static final class HoldForm extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() == 2) {
        fFactory.convertInternal(buffer, f.arg1(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
        return true;
      }
      return false;
    }
  }

  private static final class HarmonicNumber extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.isAST1()) {
        buffer.append("H_");
        fFactory.convertInternal(buffer, f.arg1(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
        return true;
      } else if (f.isAST2()) {
        buffer.append("H_");
        fFactory.convertInternal(buffer, f.arg1(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);

        buffer.append("^{(");
        fFactory.convertInternal(buffer, f.arg2(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
        buffer.append(")}");
        return true;
      }
      return false;
    }
  }

  private static final class Integrate extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() >= 3) {
        return iteratorStep(buffer, "\\int", f, 2);
      }
      return false;
    }

    public boolean iteratorStep(final StringBuilder buf, final String mathSymbol, final IAST f,
        int i) {
      if (i >= f.size()) {
        buf.append(" ");
        fFactory.convertInternal(buf, f.arg1(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
        return true;
      }
      if (f.get(i).isList()) {
        IAST list = (IAST) f.get(i);
        if (list.size() == 4 && list.arg1().isSymbol()) {
          ISymbol symbol = (ISymbol) list.arg1();
          buf.append(mathSymbol);
          buf.append("_{");
          fFactory.convertInternal(buf, list.arg2(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
          buf.append("}^{");
          fFactory.convertInternal(buf, list.arg3(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
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

  private static final class Limit extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.isAST2() && f.arg2().isRuleAST()) {
        final IAST rule = (IAST) f.arg2();
        buffer.append("\\lim_{");
        fFactory.convertSubExpr(buffer, rule.arg1(), 0);
        buffer.append("\\to ");
        fFactory.convertSubExpr(buffer, rule.arg2(), 0);
        buffer.append(" }\\,");
        fFactory.convertSubExpr(buffer, f.arg1(), 0);

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

  private static final class List extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST ast, final int precedence) {

      if ((ast.getEvalFlags() & IAST.OUTPUT_MULTILINE) == IAST.OUTPUT_MULTILINE) {
        if (convertMultiline(buffer, ast)) {
          return true;
        }
      }
      // if ((ast instanceof ASTRealMatrix) || ast.isEvalFlagOn(IAST.IS_MATRIX)) {
      int[] dims = ast.isMatrix();
      if (dims != null && dims[0] > 1 && dims[1] > 1) {
        return convertMatrix(buffer, ast, dims);
      }
      // }

      if ((ast.getEvalFlags() & IAST.IS_VECTOR) == IAST.IS_VECTOR) {
        // create a LaTeX row vector
        // \begin{pmatrix} x & y \end{pmatrix}
        buffer.append("\\begin{pmatrix} ");
        if (ast.size() > 1) {
          for (int j = 1; j < ast.size(); j++) {
            fFactory.convertInternal(buffer, ast.get(j), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
            if (j < ast.argSize()) {
              buffer.append(" & ");
            }
          }
        }
        buffer.append(" \\end{pmatrix} ");
      } else {
        buffer.append("\\{");
        if (ast.size() > 1) {
          fFactory.convertInternal(buffer, ast.arg1(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
          for (int i = 2; i < ast.size(); i++) {
            buffer.append(',');
            fFactory.convertInternal(buffer, ast.get(i), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
          }
        }
        buffer.append("\\}");
      }
      return true;
    }

    private boolean convertMatrix(final StringBuilder buffer, final IAST ast, int[] dims) {
      // create a LaTeX matrix

      final IAST matrix = ast;

      if (Config.MATRIX_TEXFORM) {
        // problem with KaTeX?
        buffer.append("\\left(\n\\begin{array}{");
        for (int i = 0; i < dims[1]; i++) {
          buffer.append("c");
        }
        buffer.append("}\n");
        if (ast.size() > 1) {
          for (int i = 1; i < ast.size(); i++) {
            IAST row = ast.getAST(i);
            for (int j = 1; j < row.size(); j++) {
              fFactory.convert(buffer, row.get(j), Precedence.NO_PRECEDENCE);
              if (j < row.argSize()) {
                buffer.append(" & ");
              }
            }
            buffer.append(" \\\\\n");
          }
        }
        buffer.append("\\end{array}\n\\right) ");
      } else {
        buffer.append("\\begin{pmatrix}\n");
        IAST row;
        for (int i = 1; i < matrix.size(); i++) {
          row = (IAST) matrix.get(i);
          for (int j = 1; j < row.size(); j++) {
            buffer.append(' ');
            fFactory.convertInternal(buffer, row.get(j), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
            buffer.append(' ');
            if (j < row.argSize()) {
              buffer.append('&');
            }
          }
          buffer.append("\\\\\n");
        }

        buffer.append("\\end{pmatrix}");
      }
      return true;
    }

    private boolean convertMultiline(final StringBuilder buf, final IAST list) {

      buf.append("\\begin{array}{c}\n");
      IExpr element;
      for (int i = 1; i < list.size(); i++) {
        element = list.get(i);
        buf.append(' ');
        fFactory.convertInternal(buf, element, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
        buf.append(' ');
        if (i < list.argSize()) {
          buf.append("\\\\\n");
        }
      }
      buf.append("\n\\end{array}");

      return true;
    }
  }

  private static final class MatrixForm extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
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
          buffer.append("\\begin{pmatrix}\n");
          IExpr element;
          for (int i = 1; i < vector.size(); i++) {
            element = vector.get(i);
            buffer.append(' ');
            fFactory.convertInternal(buffer, element, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
            buffer.append(' ');
            if (i < vector.argSize()) {
              buffer.append('&');
            }
          }
          buffer.append("\\end{pmatrix}");
        }
      } else {
        final IAST matrix = (IAST) arg1.normal(false);
        if (Config.MATRIX_TEXFORM) {
          // problem with KaTeX?
          buffer.append("\\left(\n\\begin{array}{");
          for (int i = 0; i < dims[1]; i++) {
            buffer.append("c");
          }
          buffer.append("}\n");
          if (matrix.size() > 1) {
            for (int i = 1; i < matrix.size(); i++) {
              IAST row = matrix.getAST(i);
              for (int j = 1; j < row.size(); j++) {
                fFactory.convert(buffer, row.get(j), Precedence.NO_PRECEDENCE);
                if (j < row.argSize()) {
                  buffer.append(" & ");
                }
              }
              if (i < matrix.argSize()) {
                buffer.append(" \\\\\n");
              } else {
                buffer.append(" \\\n");
              }
            }
          }
          buffer.append("\\\\\n\\end{array}\n\\right) ");
        } else {
          buffer.append("\\begin{pmatrix}\n");
          IAST row;
          for (int i = 1; i < matrix.size(); i++) {
            row = (IAST) matrix.get(i);
            for (int j = 1; j < row.size(); j++) {
              buffer.append(' ');
              fFactory.convertInternal(buffer, row.get(j), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
              buffer.append(' ');
              if (j < row.argSize()) {
                buffer.append('&');
              }
            }
            buffer.append("\\\\\n");
          }

          buffer.append("\\end{pmatrix}");
        }
      }
      return true;
    }
  }

  private static final class TableForm extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
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
          buffer.append("\\begin{array}{c}\n");
          IExpr element;
          for (int i = 1; i < vector.size(); i++) {
            element = vector.get(i);
            buffer.append(' ');
            fFactory.convertInternal(buffer, element, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
            buffer.append(' ');
            if (i < vector.argSize()) {
              buffer.append("\\\\\n");
            }
          }
          buffer.append("\n\\end{array}");
        }
      } else {
        final IAST matrix = (IAST) f.arg1();
        buffer.append("\\begin{array}{");
        for (int i = 0; i < dims[1]; i++) {
          buffer.append("c");
        }
        buffer.append("}\n");
        IAST row;
        for (int i = 1; i < matrix.size(); i++) {
          row = (IAST) matrix.get(i);
          for (int j = 1; j < row.size(); j++) {
            buffer.append(' ');
            fFactory.convertInternal(buffer, row.get(j), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
            buffer.append(' ');
            if (j < row.argSize()) {
              buffer.append('&');
            }
          }
          buffer.append("\\\\\n");
        }

        buffer.append("\\end{array}");
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

  private static final class Parenthesis extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      buffer.append("(");
      fFactory.convertInternal(buffer, f.arg1(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      buffer.append(")");
      return true;
    }
  }

  private static final class Part extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() > 2) {
        fFactory.convertHead(buffer, f.arg1());
        buffer.append("[[");
        int argSize = f.argSize();
        for (int i = 2; i <= argSize; i++) {
          fFactory.convertInternal(buffer, f.get(i), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
          if (i < argSize) {
            buffer.append(",");
          }
        }
        buffer.append("]]");
        return true;
      }
      return false;
    }
  }

  private final class Plus extends TeXFormOperator {

    public Plus() {
      super(Precedence.PLUS, " + ");
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST plusAST, final int precedence) {
      IExpr expr;
      IAST f = plusAST;
      int size = f.size();
      if (size > 0) {
        if (fPlusReversed) {
          f = f.reverse(F.NIL);
        }
        precedenceOpen(buffer, precedence);
        final Times timesConverter = new Times();
        timesConverter.setFactory(fFactory);
        expr = f.arg1();
        if (expr.isTimes()) {
          timesConverter.convertTimesFraction(buffer, (IAST) expr, timesConverter.TIMES_OPERATOR,
              fPrecedence, NO_PLUS_CALL);
        } else {
          fFactory.convertInternal(buffer, expr, fPrecedence, NO_PLUS_CALL);
        }
        for (int i = 2; i < f.size(); i++) {
          expr = f.get(i);

          if (expr.isTimes()) {
            timesConverter.convertTimesFraction(buffer, (IAST) expr, timesConverter.TIMES_OPERATOR,
                fPrecedence, PLUS_CALL);
          } else {
            if (expr.isNumber() && (((INumber) expr).complexSign() < 0)) {
              buffer.append("-");
              expr = expr.negate();
            } else if (expr.isASTSizeGE(S.Plus, 2)) {
              if (fPlusReversed) {
                if (!expr.last().isNegativeSigned()) {
                  buffer.append(" + ");
                }
              } else {
                if (!expr.first().isNegativeSigned()) {
                  buffer.append(" + ");
                }
              }
            } else if (expr.isNegativeSigned()) {
            } else {
              buffer.append(" + ");
            }
            fFactory.convertInternal(buffer, expr, fPrecedence, NO_PLUS_CALL);
          }
        }
        precedenceClose(buffer, precedence);
        return true;
      }
      return false;
    }
  }

  private static final class PlusMinus extends TeXFormOperator {

    public PlusMinus() {
      super(ASTNodeFactory.MMA_STYLE_FACTORY.get("PlusMinus").getPrecedence(), " \\pm ");
    }

    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return super.convert(buffer, f, precedence);
      }
      precedenceOpen(buffer, precedence);
      buffer.append("\\pm{");
      fFactory.convertInternal(buffer, f.arg1(), fPrecedence, NO_PLUS_CALL);
      buffer.append("}");
      precedenceClose(buffer, precedence);
      return true;
    }
  }

  private static final class PostOperator extends AbstractTeXConverter {
    protected int fPrecedence;
    protected String fOperator;

    public PostOperator(final TeXFormFactory factory, final int precedence, final String oper) {
      super(factory);
      fPrecedence = precedence;
      fOperator = oper;
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      precedenceOpen(buffer, precedence);
      fFactory.convertInternal(buffer, f.arg1(), fPrecedence, NO_PLUS_CALL);
      buffer.append(fOperator);
      precedenceClose(buffer, precedence);
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
  private static final class Power extends TeXFormOperator {

    public Power() {
      super(Precedence.POWER, "^");
    }

    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return super.convert(buffer, f, precedence);
      }
      IExpr arg1 = f.arg1();
      IExpr arg2 = f.arg2();
      if (arg2.isNegative()) {
        buffer.append("\\frac{1}{");
        if (arg2.isMinusOne()) {
          fFactory.convertInternal(buffer, arg1, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
        } else {
          fFactory.convertInternal(buffer, F.Power(arg1, arg2.negate()), Precedence.NO_PRECEDENCE,
              NO_PLUS_CALL);
        }
        buffer.append('}');
        return true;
      }
      if (arg2.isNumEqualRational(F.C1D2)) {
        buffer.append("\\sqrt{");
        fFactory.convertInternal(buffer, arg1, fPrecedence, NO_PLUS_CALL);
        buffer.append('}');
        return true;
      }
      if (arg2.isFraction()) {
        if (((IFraction) arg2).numerator().isOne()) {
          buffer.append("\\sqrt[");
          fFactory.convertInternal(buffer, ((IFraction) arg2).denominator(), fPrecedence,
              NO_PLUS_CALL);
          buffer.append("]{");
          fFactory.convertInternal(buffer, arg1, fPrecedence, NO_PLUS_CALL);
          buffer.append('}');
          return true;
        }
      }

      precedenceOpen(buffer, precedence);

      // http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
      // For powers with more than one digit, surround the power with {}.
      buffer.append('{');
      fFactory.convertInternal(buffer, arg1, fPrecedence, NO_PLUS_CALL);
      buffer.append('}');
      if (fOperator.compareTo("") != 0) {
        buffer.append(fOperator);
      }

      buffer.append('{');
      fFactory.convertInternal(buffer, arg2, fPrecedence, NO_PLUS_CALL);
      buffer.append('}');
      precedenceClose(buffer, precedence);
      return true;
    }
  }

  private static final class PreOperator extends AbstractTeXConverter {
    protected int fPrecedence;
    protected String fOperator;

    public PreOperator(final TeXFormFactory factory, final int precedence, final String oper) {
      super(factory);
      fPrecedence = precedence;
      fOperator = oper;
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() != 2) {
        return false;
      }
      precedenceOpen(buffer, precedence);
      buffer.append(fOperator);
      fFactory.convertInternal(buffer, f.arg1(), fPrecedence, NO_PLUS_CALL);
      precedenceClose(buffer, precedence);
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
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() >= 3) {
        return iteratorStep(buffer, "\\prod", f, 2);
      }
      return false;
    }
  }

  private static final class Rational extends TeXFormOperator {

    public Rational() {
      super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "/");
    }

    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return super.convert(buffer, f, precedence);
      }
      precedenceOpen(buffer, precedence);
      buffer.append("\\frac{");
      fFactory.convertInternal(buffer, f.arg1(), fPrecedence, NO_PLUS_CALL);
      buffer.append("}{");
      fFactory.convertInternal(buffer, f.arg2(), fPrecedence, NO_PLUS_CALL);
      buffer.append('}');
      precedenceClose(buffer, precedence);
      return true;
    }
  }

  private static final class Style extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      IExpr arg1 = f.arg1();
      IExpr arg2 = f.arg2();
      if (arg2.isBuiltInSymbol()) {
        if (((IBuiltInSymbol) arg2).isSymbolID(ID.Black, ID.Brown, ID.Blue, ID.Cyan, ID.Green,
            ID.Pink, ID.Red, ID.Yellow, ID.White)) {
          buffer.append("\\textcolor{");
          buffer.append(arg2.toString().toLowerCase(Locale.US));
          buffer.append("}{");
          fFactory.convertInternal(buffer, arg1, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
          buffer.append("}");
          return true;
        }
      }
      return false;
    }
  }

  private static final class Subscript extends AbstractTeXConverter {
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() < 3) {
        return false;
      }
      IExpr arg1 = f.arg1();

      // http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
      // For powers with more than one digit, surround the power with {}.
      buffer.append('{');
      fFactory.convertInternal(buffer, arg1, precedence, NO_PLUS_CALL);
      buffer.append('}');
      buffer.append("_");

      buffer.append('{');

      for (int i = 2; i < f.size(); i++) {
        fFactory.convertInternal(buffer, f.get(i), precedence, NO_PLUS_CALL);
        if (i < f.argSize()) {
          buffer.append(',');
        }
      }
      buffer.append('}');
      return true;
    }
  }

  private static final class Subsuperscript extends AbstractTeXConverter {

    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() != 4) {
        return false;
      }
      IExpr arg1 = f.arg1();
      IExpr arg2 = f.arg2();
      IExpr arg3 = f.arg3();

      // http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
      // For powers with more than one digit, surround the power with {}.
      buffer.append('{');
      fFactory.convertInternal(buffer, arg1, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      buffer.append('}');
      buffer.append("_");

      buffer.append('{');
      fFactory.convertInternal(buffer, arg2, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      buffer.append('}');
      buffer.append("^");

      buffer.append('{');
      fFactory.convertInternal(buffer, arg3, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      buffer.append('}');
      return true;
    }
  }

  private static class Sum extends AbstractTeXConverter {

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() >= 3) {
        return iteratorStep(buffer, "\\sum", f, 2);
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
    public boolean iteratorStep(final StringBuilder buf, final String mathSymbol, final IAST f,
        int i) {
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
            fFactory.convertSubExpr(buf, iterator.getVariable(), Precedence.NO_PRECEDENCE);
            buf.append(" = ");
            fFactory.convertSubExpr(buf, iterator.getLowerLimit(), Precedence.NO_PRECEDENCE);
            buf.append("}^{");
            fFactory.convertInternal(buf, iterator.getUpperLimit(), Precedence.NO_PRECEDENCE,
                NO_PLUS_CALL);
            buf.append('}');
            if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
              return false;
            }
            return true;
          }
        } catch (final ValidateException ve) {
          Errors.printMessage(S.Sum, ve, EvalEngine.get());
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

  private static final class Superscript extends AbstractTeXConverter {

    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      if (f.size() != 3) {
        return false;
      }
      IExpr arg1 = f.arg1();
      IExpr arg2 = f.arg2();

      // http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
      // For powers with more than one digit, surround the power with {}.
      buffer.append('{');
      fFactory.convertInternal(buffer, arg1, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      buffer.append('}');

      buffer.append("^");
      buffer.append('{');
      fFactory.convertInternal(buffer, arg2, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      buffer.append('}');
      return true;
    }
  }

  public static final class TeXFunction extends AbstractTeXConverter {

    String fFunctionName;

    public TeXFunction(final TeXFormFactory factory, final String functionName) {
      super(factory);
      fFunctionName = functionName;
    }

    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      buffer.append('\\');
      buffer.append(fFunctionName);
      buffer.append('(');
      for (int i = 1; i < f.size(); i++) {
        fFactory.convertInternal(buffer, f.get(i), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
        if (i < f.argSize()) {
          buffer.append(',');
        }
      }
      buffer.append(')');
      return true;
    }
  }

  private static final class Times extends TeXFormOperator {
    /** The conversion wasn't called with an operator preceding the <code>IExpr</code> object. */

    public final InfixOperator TIMES_OPERATOR;

    // public static Times CONST = new Times();

    public Times() {
      // " \\cdot " is only a placeholder here. The <code>ffactory.timesOperator</code> is used as
      // multiplication operator
      super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), " \\cdot ");
      TIMES_OPERATOR = (InfixOperator) ASTNodeFactory.MMA_STYLE_FACTORY.get("Times");
    }

    /**
     * Converts a given function into the corresponding TeX output
     *
     * @param buffer StringBuilder for TeX output
     * @param f The math function which should be converted to TeX
     */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      convertTimesFraction(buffer, f, TIMES_OPERATOR, precedence, NO_PLUS_CALL);
      return true;
    }


    private void convertTimesFraction(final StringBuilder buf, final IAST timesAST,
        final InfixOperator oper, final int precedence, boolean caller) {
      if (timesAST.exists(x -> x.isAST(S.HoldForm, 2))) {
        convertTimesOperator(buf, timesAST, oper, precedence, caller);
        return;
      }
      Optional<IExpr[]> parts =
          Algebra.fractionalPartsTimesPower(timesAST, true, false, false, false, false, false);
      if (parts.isEmpty()) {
        convertTimesOperator(buf, timesAST, oper, precedence, caller);
        return;
      }
      IExpr numerator = parts.get()[0];
      final IExpr denominator = parts.get()[1];
      if (!denominator.isOne()) {
        boolean isNegative = numerator.isNegativeSigned();
        if (isNegative) {
          numerator = numerator.negate();
        }
        precedenceOpen(buf, precedence);
        final IExpr fraction = parts.get()[2];
        if (fraction != null) {
          fFactory.convertNumber(buf, fraction, Precedence.PLUS, caller);
          buf.append(fFactory.symbolOptions.getTimesSymbol());
          caller = NO_PLUS_CALL;
        }
        if (isNegative) {
          buf.append(" - ");
        } else if (caller == PLUS_CALL) {
          buf.append(" + ");
        }
        buf.append("\\frac{");
        if (numerator.isReal()) {
          // don't use Precedence.PLUS here
          fFactory.convertNumber(buf, numerator, Precedence.NO_PRECEDENCE, false);
        } else if (numerator.isComplex() || numerator.isComplexNumeric()) {
          fFactory.convertNumber(buf, numerator, Precedence.DIVIDE, false);
        } else {
          if (numerator.isTimes() && numerator.isAST2() && numerator.first().isMinusOne()) {
            buf.append(" - ");
            fFactory.convert(buf, numerator.second(), Precedence.TIMES);
          } else {
            // insert numerator in buffer:
            if (numerator.isTimes()) {
              convertTimesOperator(buf, (IAST) numerator, oper, Precedence.NO_PRECEDENCE,
                  NO_PLUS_CALL);
            } else {
              // don't use Precedence.DIVIDE here
              fFactory.convert(buf, numerator, Precedence.NO_PRECEDENCE);
            }
          }
        }
        buf.append("}{");
        // insert denominator in buffer:
        if (denominator.isTimes()) {
          // don't use Precedence.DIVIDE here
          convertTimesOperator(buf, (IAST) denominator, oper, Precedence.NO_PRECEDENCE,
              NO_PLUS_CALL);
        } else {
          // don't use Precedence.DIVIDE here
          fFactory.convert(buf, denominator, Precedence.NO_PRECEDENCE);
        }
        buf.append("}");
        precedenceClose(buf, precedence);
        return;
      }
      convertTimesOperator(buf, timesAST, oper, precedence, caller);
    }

    private void convertTimesOperator(final StringBuilder buf, final IAST timesAST,
        final InfixOperator oper, final int precedence, boolean caller) {
      int size = timesAST.size();
      boolean showOperator = true;
      int currPrecedence = oper.getPrecedence();
      if (currPrecedence < precedence) {
        buf.append("\\left( ");
      }

      if (size > 1) {
        IExpr arg1 = timesAST.arg1();
        if (arg1.isReal() && size > 2 && !timesAST.arg2().isNumber()) {
          if (arg1.isMinusOne()) {
            buf.append(" - ");
            showOperator = false;
          } else {
            fFactory.convertNumber(buf, arg1, Precedence.PLUS, caller);
          }
        } else if (arg1.isComplex() && size > 2) {
          fFactory.convertComplex(buf, (IComplex) arg1, oper.getPrecedence(), caller);
        } else {
          if (caller == PLUS_CALL) {
            buf.append(" + ");
          }
          fFactory.convert(buf, arg1, oper.getPrecedence());
        }
      }
      for (int i = 2; i < size; i++) {
        if (showOperator) {
          buf.append(fFactory.symbolOptions.getTimesSymbol());
        } else {
          showOperator = true;
        }
        fFactory.convert(buf, timesAST.get(i), oper.getPrecedence());
      }
      if (currPrecedence < precedence) {
        buf.append("\\right) ");
      }
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

  private static final class Zeta extends AbstractTeXConverter {
    /** {@inheritDoc} */
    @Override
    public boolean convert(final StringBuilder buffer, final IAST f, final int precedence) {
      fFactory.convertAST(buffer, f, "\\zeta ");
      return true;
    }
  }

  /** Table for constant symbols */
  public static final Map<String, String> CONSTANT_SYMBOLS =
      ParserConfig.TRIE_STRING2STRING_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  /** Table for constant expressions */
  public static final Map<IExpr, String> CONSTANT_EXPRS = new HashMap<IExpr, String>(199);

  /**
   * Assign a specialized {@link AbstractTeXConverter} to an {@link ISymbol} symbol name in this
   * {@link Map}
   */
  private final Map<ISymbol, AbstractTeXConverter> symbolToConverterMap =
      new HashMap<ISymbol, AbstractTeXConverter>(199);

  public static final boolean USE_IDENTIFIERS = false;

  private final boolean fPlusReversed;

  private boolean useSignificantFigures = false;

  private int exponentFigures;

  private int significantFigures;

  private TexFormSymbolOptions symbolOptions = new TexFormSymbolOptions();

  public TeXFormFactory() {
    this(" \\cdot ");
  }

  /**
   *
   *
   * @param timesOperator the current operator which will be used for {@link S#Times} conversion.
   *        <code>\cdot</code> isa used as the default operator
   */
  public TeXFormFactory(String timesOperator) {
    this(-1, -1, timesOperator);
  }

  /**
   * @param exponentFigures
   * @param significantFigures
   * @param timesOperator timesOperator the current operator which will be used for {@link S#Times}
   *        conversion. <code>\cdot</code> is used as the default operator
   */
  public TeXFormFactory(int exponentFigures, int significantFigures, String timesOperator) {
    this(false, exponentFigures, significantFigures, timesOperator);
  }

  /**
   * @param plusReversed if <code>true</code> print {@link S#Plus} expressions in reversed order
   * @param exponentFigures
   * @param significantFigures
   * @param timesOperator timesOperator the current operator which will be used for {@link S#Times}
   *        conversion. <code>\cdot</code> is used as the default operator
   */
  public TeXFormFactory(final boolean plusReversed, int exponentFigures, int significantFigures,
      String timesOperator) {
    this(plusReversed, exponentFigures, significantFigures, new TexFormSymbolOptions());
    this.symbolOptions.setTimesSymbol(timesOperator);
  }

  /**
   * 
   * @param exponentFigures
   * @param significantFigures
   * @param symbolOptions
   */
  public TeXFormFactory(int exponentFigures, int significantFigures,
      TexFormSymbolOptions symbolOptions) {
    this(false, exponentFigures, significantFigures, symbolOptions);
  }

  /**
   * 
   * @param plusReversed if <code>true</code> print {@link S#Plus} expressions in reversed order
   * @param exponentFigures
   * @param significantFigures
   * @param symbolOptions
   */
  public TeXFormFactory(final boolean plusReversed, int exponentFigures, int significantFigures,
      TexFormSymbolOptions symbolOptions) {
    this.fPlusReversed = plusReversed;
    this.exponentFigures = exponentFigures;
    this.significantFigures = significantFigures;
    this.symbolOptions = symbolOptions;
    init();
  }

  public TexFormSymbolOptions getSymbolOptions() {
    return symbolOptions;
  }

  public void setSymbolOptions(TexFormSymbolOptions symbolOptions) {
    this.symbolOptions = symbolOptions;
  }

  public void convertApcomplex(final StringBuilder buf, final Apcomplex dc, final int precedence,
      boolean caller) {
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
          convertDoubleString(buf, convertApfloatToFormattedString(imaginaryPart), Precedence.TIMES,
              isNegative);
          buf.append("\\,"); // InvisibleTimes
          buf.append("i ");
        }
      } else {
        if (caller == PLUS_CALL) {
          buf.append(" + ");
          caller = false;
        }

        final boolean isNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;
        convertDoubleString(buf, convertApfloatToFormattedString(imaginaryPart), Precedence.TIMES,
            isNegative);
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
        useSignificantFigures);
    return buf.toString();
  }


  /**
   * Convert the <code>expression</code> into a LaTeX math string representation and appedn it to
   * the <code>buffer</code>.
   *
   * @param buffer the buffer, to which the TeX string will be appended
   * @param expression the expression which should be converted to a TeX string
   * @return <code>false</code> if the conversion couldn't be terminated
   */
  public boolean convert(final StringBuilder buffer, final IExpr expression) {
    return convert(buffer, expression, Precedence.NO_PRECEDENCE);
  }

  /**
   *
   * @param buffer the buffer, to which the TeX string will be appended
   * @param expression the expression which should be converted to a TeX string
   * @param precedence the precedence of the &quot;calling operator&quot;
   * @return <code>false</code> if the conversion couldn't be terminated
   */
  public boolean convert(final StringBuilder buffer, final IExpr expression, final int precedence) {
    try {
      convertInternal(buffer, expression, precedence, NO_PLUS_CALL);
      if (buffer.length() >= Config.MAX_OUTPUT_SIZE) {
        return false;
      }
      return true;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      LOGGER.debug("TeXFormFactory.convert() failed", rex);
    } catch (OutOfMemoryError oome) {
    }
    return false;
  }

  public void convertInternal(final StringBuilder buf, final IExpr expr, final int precedence,
      boolean caller) {

    String str = CONSTANT_EXPRS.get(expr);
    if (str != null) {
      buf.append(str);
      return;
    }

    if (expr instanceof IAST) {
      final IAST f = (IAST) expr;
      IExpr h = f.head();
      if (h.isSymbol()) {
        final AbstractTeXConverter converter = symbolToConverterMap.get((h));
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
    if (convertNumber(buf, expr, precedence, caller)) {
      return;
    }
    if (expr instanceof ISymbol) {
      convertSymbol(buf, (ISymbol) expr);
      return;
    }
    convertString(buf, expr.toString());
  }

  private boolean convertNumber(final StringBuilder buf, final Object o, final int precedence,
      boolean caller) {
    if (o instanceof IReal) {
      IReal number = (IReal) o;
      boolean isNegative = number.isNegative();
      if (isNegative) {
        number = number.negate();
      }
      final boolean setBraces = isNegative && precedence > Precedence.PLUS;
      if (setBraces) {
        buf.append("\\left( ");
      }
      if (isNegative) {
        buf.append("-");
      } else if (caller) {
        buf.append("+");
      }
      if (number instanceof IInteger) {
        convertInteger(buf, (IInteger) number, precedence);
        if (setBraces) {
          buf.append("\\right) ");
        }
        return true;
      }
      if (number instanceof IFraction) {
        convertFraction(buf, (IFraction) number, precedence);
        if (setBraces) {
          buf.append("\\right) ");
        }
        return true;
      }
      if (number instanceof INum) {
        convertDouble(buf, (INum) number, precedence);
        if (setBraces) {
          buf.append("\\right) ");
        }
        return true;
      }
      return false;
    }
    if (o instanceof IComplexNum) {
      if (o instanceof ApcomplexNum) {
        convertApcomplex(buf, ((ApcomplexNum) o).apcomplexValue(), precedence, caller);
        return true;
      }
      convertDoubleComplex(buf, (IComplexNum) o, precedence, caller);
      return true;
    }
    if (o instanceof IComplex) {
      convertComplex(buf, (IComplex) o, precedence, caller);
      return true;
    }
    return false;
  }

  public void convertAST(StringBuilder buf, final IAST list, int precedence) {
    if (list.isNIL()) {
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
        if (a1Head.isAST1() && headAST.isAST1()
            && (headAST.arg1().isSymbol() || headAST.arg1().isAST())) {
          try {
            IExpr symbolOrAST = headAST.arg1();
            int n = a1Head.arg1().toIntDefault();
            if (n == 1 || n == 2) {
              convertInternal(buf, symbolOrAST, Integer.MAX_VALUE, NO_PLUS_CALL);
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
            convertInternal(buf, symbolOrAST, Integer.MAX_VALUE, NO_PLUS_CALL);
            buf.append("^{(");
            convertInternal(buf, a1Head.arg1(), Integer.MIN_VALUE, NO_PLUS_CALL);
            buf.append(")}");
            if (derivStruct[2] != null) {
              convertArgs(buf, symbolOrAST, list);
            }
            return;

          } catch (ArithmeticException ae) {

          }
        }
      }

      convertInternal(buf, header, Integer.MIN_VALUE, NO_PLUS_CALL);
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
                  convertDoubleString(buf, convertDoubleToFormattedString(min.evalf()),
                      Precedence.NO_PRECEDENCE, false);
                } else {
                  convertInternal(buf, min, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
                }
                buf.append(",");
                if (max instanceof INum) {
                  convertDoubleString(buf, convertDoubleToFormattedString(max.evalf()),
                      Precedence.NO_PRECEDENCE, false);
                } else {
                  convertInternal(buf, max, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
                }
                buf.append("\\}");
                if (i < interval.argSize()) {
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
      convertInternal(buf, list.get(i), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
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
      convertInternal(buf, function.arg1(), Integer.MIN_VALUE, NO_PLUS_CALL);
    }
    for (int i = 2; i < functionSize; i++) {
      convertInternal(buf, function.get(i), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
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
    list.joinToString(buf, //
        (b, x) -> convertInternal(b, x, Integer.MIN_VALUE, NO_PLUS_CALL), ",");
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
  public boolean convertAssociation(final StringBuilder buf, final IAssociation assoc,
      final int precedence) {
    IAST ast = assoc.normal(false);
    buf.append("\\langle|");
    if (ast.size() > 1) {
      convertInternal(buf, ast.arg1(), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      for (int i = 2; i < ast.size(); i++) {
        buf.append(',');
        convertInternal(buf, ast.get(i), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      }
    }
    buf.append("|\\rangle");

    return true;
  }

  private boolean convertInequality(final StringBuilder buf, final IAST inequality,
      final int precedence) {
    StringBuilder tempBuffer = new StringBuilder();

    if (Precedence.EQUAL < precedence) {
      tempBuffer.append("(");
    }

    final int listSize = inequality.size();
    int i = 1;
    while (i < listSize) {
      convertInternal(tempBuffer, inequality.get(i++), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
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
            tempBuffer.append(this.symbolOptions.getEqualSymbol());
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
    if (f.isNIL()) {
      buf.append("NIL");
      return;
    }
    buf.append(headString);
    buf.append("(");
    for (int i = 1; i < f.size(); i++) {
      convertInternal(buf, f.get(i), Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      if (i < f.argSize()) {
        buf.append(',');
      }
    }
    buf.append(")");
  }

  public void convertComplex(final StringBuilder buf, final IComplex c, final int precedence,
      boolean caller) {
    if (c.isImaginaryUnit()) {
      buf.append("i ");
      return;
    }
    if (c.isNegativeImaginaryUnit()) {
      if (caller && precedence > Precedence.PLUS) {
        buf.append("\\left( ");
      }
      buf.append(" - i ");
      if (caller && precedence > Precedence.PLUS) {
        buf.append("\\right) ");
      }
      return;
    }
    if (caller && precedence > Precedence.PLUS) {
      buf.append("\\left( ");
    }
    IRational re = c.getRealPart();
    IRational im = c.getImaginaryPart();
    if (!re.isZero()) {
      convertInternal(buf, re, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
      if (im.isNonNegativeResult()) {
        buf.append(" + ");
      } else {
        buf.append(" - ");
        im = im.negate();
      }
    }
    convertInternal(buf, im, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
    buf.append("\\,"); // InvisibleTimes
    buf.append("i ");
    if (caller && precedence > Precedence.PLUS) {
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
      convertDoubleString(buf, convertDoubleToFormattedString(d.getRealPart()), precedence,
          isNegative);
    } else {
      convertDoubleString(buf, convertApfloatToFormattedString(((ApfloatNum) d).apfloatValue()),
          precedence, isNegative);
    }
  }

  public void convertDoubleComplex(final StringBuilder buf, final IComplexNum dc,
      final int precedence, boolean caller) {
    double re = dc.getRealPart();
    double im = dc.getImaginaryPart();
    if (F.isZero(re)) {
      if (F.isNumIntValue(im, 1)) {
        buf.append("i ");
        return;
      }
      if (F.isNumIntValue(im, -1)) {
        if (precedence > Precedence.PLUS) {
          buf.append("\\left( ");
        }
        buf.append(" - i ");
        if (precedence > Precedence.PLUS) {
          buf.append("\\right) ");
        }
        return;
      }
    }
    if (precedence > Precedence.PLUS) {
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
    if (precedence > Precedence.PLUS) {
      buf.append("\\right) ");
    }
  }

  private void convertDoubleString(final StringBuilder buf, final String d, final int precedence,
      final boolean isNegative) {
    if (isNegative && (Precedence.PLUS < precedence)) {
      buf.append("\\left( ");
    }
    buf.append(d);
    if (isNegative && (Precedence.PLUS < precedence)) {
      buf.append("\\right) ");
    }
  }

  protected String convertDoubleToFormattedString(double dValue) {
    if (significantFigures > 0) {
      try {
        StringBuilder buf = new StringBuilder();
        DoubleToMMA.doubleToMMA(buf, dValue, exponentFigures, significantFigures, true, true);
        return buf.toString();
      } catch (IOException ioex) {
        LOGGER.error("TeXFormFactory.convertDoubleToFormattedString() failed", ioex);
      }
    }
    return Double.toString(dValue);
  }

  public void convertFraction(final StringBuilder buf, final IFraction f, final int precedence) {
    // if (f.isNegative() && (precedence > Precedence.PLUS)) {
    // buf.append("\\left( ");
    // }
    if (f.denominator().isOne()) {
      buf.append(f.numerator().toString());
    } else {
      buf.append("\\frac{");
      buf.append(f.toBigNumerator().toString());
      buf.append("}{");
      buf.append(f.toBigDenominator().toString());
      buf.append('}');
    }
    // if (f.isNegative() && (precedence > Precedence.PLUS)) {
    // buf.append("\\right) ");
    // }
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
    convertInternal(buf, obj, Precedence.NO_PRECEDENCE, NO_PLUS_CALL);
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
    // if (i.isNegative() && precedence > Precedence.PLUS) {
    // buf.append("\\left( ");
    // }
    buf.append(i.toBigNumerator().toString());
    // if (i.isNegative() && precedence > Precedence.PLUS) {
    // buf.append("\\right) ");
    // }
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
    convertInternal(buf, o, precedence, NO_PLUS_CALL);
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
      buf.append(context + sym.getSymbolName());
    }
  }

  private void convertConstantSymbol(final StringBuilder buf, final ISymbol sym,
      final Object convertedSymbol) {
    if (convertedSymbol.equals(AST2Expr.TRUE_STRING)) {
      buf.append('\\');
      buf.append(sym.getSymbolName());
      return;
    }
    if (convertedSymbol instanceof Operator) {
      ((Operator) convertedSymbol).convert(buf);
      return;
    }
    buf.append(convertedSymbol);
  }

  private void init() {
    // timesPrec =
    // ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence();
    initTeXConverter(S.Abs, new UnaryFunction("|", "|"));

    initTeXConverter(S.Beta, new BinaryFunction("B(", ",", ")"));
    initTeXConverter(S.BesselI, new BinaryFunction("I_", "(", ")"));
    initTeXConverter(S.BesselJ, new BinaryFunction("J_", "(", ")"));
    initTeXConverter(S.BesselK, new BinaryFunction("K_", "(", ")"));
    initTeXConverter(S.BesselY, new BinaryFunction("Y_", "(", ")"));
    initTeXConverter(S.CarlsonRC, new BinaryFunction("R_C(", ",", ")"));
    initTeXConverter(S.CarlsonRD, new TernaryFunction("R_D(", ",", ",", ")"));
    initTeXConverter(S.CarlsonRF, new TernaryFunction("R_F(", ",", ",", ")"));
    initTeXConverter(S.CarlsonRG, new TernaryFunction("R_G(", ",", ",", ")"));
    initTeXConverter(S.CarlsonRJ, new QuadrupleFunction("R_J(", ",", ",", ",", ")"));
    initTeXConverter(S.ChebyshevT, new BinaryFunction("T_", "(", ")"));
    initTeXConverter(S.ChebyshevU, new BinaryFunction("U_", "(", ")"));
    initTeXConverter(S.CosIntegral, new UnaryFunction("\\text{Ci}(", ")"));
    initTeXConverter(S.CoshIntegral, new UnaryFunction("\\text{Chi}(", ")"));

    initTeXConverter(S.BetaRegularized, new TernaryFunction("I_", "(", ",", ")"));
    initTeXConverter(S.Binomial, new Binomial());

    initTeXConverter(S.Ceiling, new UnaryFunction(" \\left \\lceil ", " \\right \\rceil "));
    initTeXConverter(S.Conjugate, new Conjugate());
    initTeXConverter(S.Complex, new Complex());
    initTeXConverter(S.CompoundExpression, new TeXFormOperator(
        ASTNodeFactory.MMA_STYLE_FACTORY.get("CompoundExpression").getPrecedence(), ", "));
    initTeXConverter(S.D, new D());
    initTeXConverter(S.Defer, new HoldForm());
    initTeXConverter(S.DirectedInfinity, new DirectedInfinity());

    initTeXConverter(S.EllipticE, new UnaryBinaryFunction("E(", ",", ")"));
    initTeXConverter(S.EllipticF, new BinaryFunction("F(", "|", ")"));
    initTeXConverter(S.EllipticK, new UnaryFunction("K(", ")"));
    initTeXConverter(S.EllipticPi, new BinaryTernaryFunction("\\Pi (", ";", "|", ")", true));
    initTeXConverter(S.EllipticTheta,
        new BinaryTernaryFunction("\\vartheta _", "(", ",", ")", false));
    initTeXConverter(S.Erf, new UnaryFunction("\\text{erf}(", ")"));
    initTeXConverter(S.Erfc, new UnaryFunction("\\text{erfc}(", ")"));
    initTeXConverter(S.Erfi, new UnaryFunction("\\text{erfi}(", ")"));
    initTeXConverter(S.EulerE, new UnaryBinaryFunction("E_", "(", ")", true));

    initTeXConverter(S.FactorialPower, new BinaryTernaryFunction("", "^{(", ",", ")}", false));
    initTeXConverter(S.Floor, new UnaryFunction(" \\left \\lfloor ", " \\right \\rfloor "));
    initTeXConverter(S.Function, new UnaryFunction("", "\\&"));

    initTeXConverter(S.GammaRegularized, new BinaryTernaryFunction("Q(", ",", ",", ")", false));
    initTeXConverter(S.Gudermannian, new UnaryFunction("\\text{gd}(", ")"));

    initTeXConverter(S.HankelH1, new BinaryFunction("H_", "^{(1)}(", ")"));
    initTeXConverter(S.HankelH2, new BinaryFunction("H_", "^{(2)}(", ")"));
    initTeXConverter(S.HarmonicNumber, new HarmonicNumber());
    initTeXConverter(S.HermiteH, new BinaryFunction("H_", "(", ")"));
    initTeXConverter(S.HoldForm, new HoldForm());
    initTeXConverter(S.HurwitzZeta, new Zeta());
    initTeXConverter(S.Hypergeometric0F1, new BinaryFunction("\\,_0F_1(;", ";", ")"));
    initTeXConverter(S.Hypergeometric1F1, new TernaryFunction("\\,_1F_1(", ",", ",", ")"));
    initTeXConverter(S.HypergeometricU, new TernaryFunction("U(", ",", ",", ")"));

    initTeXConverter(S.Integrate, new Integrate());
    initTeXConverter(S.IntervalData, new IntervalData());

    initTeXConverter(S.InverseBetaRegularized, new TernaryFunction("I_", "^{-1}(", ",", ")"));
    initTeXConverter(S.InverseErf, new UnaryFunction("\\text{erf}^{-1}(", ")"));
    initTeXConverter(S.InverseErfc, new UnaryFunction("\\text{erfc}^{-1}(", ")"));
    initTeXConverter(S.InverseGammaRegularized, new BinaryFunction("Q^{-1}(", ",", ")"));
    initTeXConverter(S.InverseGudermannian, new UnaryFunction("\\text{gd}^{-1}(", ")"));

    initTeXConverter(S.LaguerreL, new BinaryFunction("L_", "(", ")"));
    initTeXConverter(S.LegendreP, new BinaryTernaryFunction("P_", "^", "(", ")", true));
    initTeXConverter(S.LegendreQ, new BinaryTernaryFunction("Q_", "^", "(", ")", true));

    initTeXConverter(S.Limit, new Limit());
    initTeXConverter(S.List, new List());
    // initTeXConverter(S.$RealMatrix, new List());
    // initTeXConverter(S.$RealVector, new List());

    initTeXConverter(S.MatrixForm, new MatrixForm());
    initTeXConverter(S.TableForm, new TableForm());
    initTeXConverter(S.Parenthesis, new Parenthesis());
    initTeXConverter(S.Part, new Part());
    initTeXConverter(S.Plus, new Plus());
    initTeXConverter(S.Pochhammer, new BinaryFunction("(", ")_", ""));
    initTeXConverter(S.Power, new Power());
    initTeXConverter(S.Product, new Product());

    initTeXConverter(S.Rational, new Rational());

    initTeXConverter(S.SinIntegral, new UnaryFunction("\\text{Si}(", ")"));
    initTeXConverter(S.SinhIntegral, new UnaryFunction("\\text{Shi}(", ")"));
    initTeXConverter(S.Slot, new UnaryFunction("\\text{$\\#$", "}"));
    initTeXConverter(S.SlotSequence, new UnaryFunction("\\text{$\\#\\#$", "}"));
    initTeXConverter(S.SphericalBesselJ, new BinaryFunction("j_", "(", ")"));
    initTeXConverter(S.SphericalBesselY, new BinaryFunction("y_", "(", ")"));
    initTeXConverter(S.Sqrt, new UnaryFunction("\\sqrt{", "}"));
    initTeXConverter(S.Style, new Style());
    initTeXConverter(S.Subscript, new Subscript());
    initTeXConverter(S.Subsuperscript, new Subsuperscript());
    initTeXConverter(S.Sum, new Sum());
    initTeXConverter(S.Superscript, new Superscript());

    initTeXConverter(S.Times, new Times());

    initTeXConverter(S.WhittakerM, new TernaryFunction("M_{", ",", "}(", ")"));
    initTeXConverter(S.WhittakerW, new TernaryFunction("W_{", ",", "}(", ")"));

    initTeXConverter(S.Zeta, new Zeta());

    initTeXConverter(S.Condition, new TeXFormOperator(this, Precedence.CONDITION, "\\text{/;}"));
    initTeXConverter(S.Unset, new PostOperator(this, Precedence.UNSET, "\\text{=.}"));
    initTeXConverter(S.UpSetDelayed,
        new TeXFormOperator(this, Precedence.UPSETDELAYED, "\\text{^:=}"));
    initTeXConverter(S.UpSet, new TeXFormOperator(this, Precedence.UPSET, "\\text{^=}"));
    initTeXConverter(S.NonCommutativeMultiply,
        new TeXFormOperator(this, Precedence.NONCOMMUTATIVEMULTIPLY, "\\text{**}"));
    initTeXConverter(S.PreDecrement, new PreOperator(this, Precedence.PREDECREMENT, "\\text{--}"));
    initTeXConverter(S.ReplaceRepeated,
        new TeXFormOperator(this, Precedence.REPLACEREPEATED, "\\text{//.}"));
    initTeXConverter(S.MapAll, new TeXFormOperator(this, Precedence.MAPALL, "\\text{//@}"));
    initTeXConverter(S.AddTo, new TeXFormOperator(this, Precedence.ADDTO, "\\text{+=}"));
    initTeXConverter(S.Greater, new TeXFormOperator(this, Precedence.GREATER, " > "));
    initTeXConverter(S.GreaterEqual, new TeXFormOperator(this, Precedence.GREATEREQUAL, "\\geq "));
    initTeXConverter(S.SubtractFrom,
        new TeXFormOperator(this, Precedence.SUBTRACTFROM, "\\text{-=}"));
    initTeXConverter(S.Subtract, new TeXFormOperator(this, Precedence.SUBTRACT + 1, " - "));
    initTeXConverter(S.CompoundExpression,
        new TeXFormOperator(this, Precedence.COMPOUNDEXPRESSION, ";"));
    initTeXConverter(S.DivideBy, new TeXFormOperator(this, Precedence.DIVIDEBY, "\\text{/=}"));
    initTeXConverter(S.StringJoin, new TeXFormOperator(this, Precedence.STRINGJOIN, "\\text{<>}"));
    initTeXConverter(S.UnsameQ, new TeXFormOperator(this, Precedence.UNSAMEQ, "\\text{=!=}"));
    initTeXConverter(S.Decrement, new PostOperator(this, Precedence.DECREMENT, "\\text{--}"));
    initTeXConverter(S.LessEqual, new TeXFormOperator(this, Precedence.LESSEQUAL, "\\leq "));
    initTeXConverter(S.Colon, new TeXFormOperator(this, Precedence.COLON, "\\text{:}"));
    initTeXConverter(S.Increment, new PostOperator(this, Precedence.INCREMENT, "\\text{++}"));
    initTeXConverter(S.Alternatives,
        new TeXFormOperator(this, Precedence.ALTERNATIVES, "\\text{|}"));
    initTeXConverter(S.Equal,
        new TeXFormOperator(this, Precedence.EQUAL, this.symbolOptions.getEqualSymbol()));
    initTeXConverter(S.DirectedEdge, new TeXFormOperator(this, Precedence.DIRECTEDEDGE, "\\to "));
    initTeXConverter(S.Divide, new BinaryFunction("\\frac{", "}{", "}"));
    // new TeXFormOperator(this, Precedence.DIVIDE, "\\text{/}"));
    initTeXConverter(S.Apply, new TeXFormOperator(this, Precedence.APPLY, "\\text{@@}"));
    initTeXConverter(S.Set, new TeXFormOperator(this, Precedence.SET, " = "));
    // initTeXConverter(F.Minus,
    // new PreOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Minus").getPrecedence(),
    // "\\text{-}"));
    initTeXConverter(S.Map, new TeXFormOperator(this, Precedence.MAP, "\\text{/@}"));
    initTeXConverter(S.SameQ, new TeXFormOperator(this, Precedence.SAMEQ, "\\text{===}"));
    initTeXConverter(S.Less, new TeXFormOperator(this, Precedence.LESS, " < "));
    initTeXConverter(S.PreIncrement, new PreOperator(this, Precedence.PREINCREMENT, "\\text{++}"));
    initTeXConverter(S.Unequal, new TeXFormOperator(this, Precedence.UNEQUAL, "\\neq "));
    initTeXConverter(S.Or, new TeXFormOperator(this, Precedence.OR, " \\lor "));
    initTeXConverter(S.PlusMinus, new PlusMinus());

    initTeXConverter(S.Intersection, new TeXFormOperator(this, Precedence.INTERSECTION, " \\cap "));
    initTeXConverter(S.Union, new TeXFormOperator(this, Precedence.UNION, " \\cup "));

    // initTeXConverter(F.PrePlus,
    // new PreOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("PrePlus").getPrecedence(),
    // "\\text{+}"));
    initTeXConverter(S.TimesBy, new TeXFormOperator(this, Precedence.TIMESBY, "\\text{*=}"));
    initTeXConverter(S.And, new TeXFormOperator(this, Precedence.AND, " \\land "));
    initTeXConverter(S.Not, new PreOperator(this, Precedence.NOT, "\\neg "));
    initTeXConverter(S.Implies, new TeXFormOperator(this, Precedence.IMPLIES, "\\Rightarrow "));
    initTeXConverter(S.Factorial, new PostOperator(this, Precedence.FACTORIAL, " ! "));
    initTeXConverter(S.Factorial2, new PostOperator(this, Precedence.FACTORIAL2, " !! "));

    initTeXConverter(S.ReplaceAll,
        new TeXFormOperator(this, Precedence.REPLACEALL, "\\text{/.}\\,"));
    initTeXConverter(S.ReplaceRepeated,
        new TeXFormOperator(this, Precedence.REPLACEREPEATED, "\\text{//.}\\,"));
    initTeXConverter(S.Rule, new TeXFormOperator(this, Precedence.RULE, "\\to "));
    initTeXConverter(S.RuleDelayed, new TeXFormOperator(this, Precedence.RULEDELAYED, ":\\to "));
    initTeXConverter(S.Set, new TeXFormOperator(this, Precedence.SET, " = "));
    initTeXConverter(S.SetDelayed,
        new TeXFormOperator(this, Precedence.SETDELAYED, "\\text{:=}\\,"));
    initTeXConverter(S.UndirectedEdge,
        new TeXFormOperator(this, Precedence.UNDIRECTEDEDGE, "\\leftrightarrow "));
    initTeXConverter(S.TwoWayRule,
        new TeXFormOperator(this, Precedence.TWOWAYRULE, "\\leftrightarrow "));
    initTeXConverter(S.CenterDot, new TeXFormOperator(this, Precedence.CENTERDOT, " \\cdot "));
    initTeXConverter(S.CircleDot, new TeXFormOperator(this, Precedence.CIRCLEDOT, " \\odot "));

    initTeXConverter(S.ArcCos, new TeXFunction(this, "arccos "));
    initTeXConverter(S.ArcCot, new TeXFunction(this, "arccot "));
    initTeXConverter(S.ArcCsc, new TeXFunction(this, "arccsc "));
    initTeXConverter(S.ArcSec, new TeXFunction(this, "arcsec "));
    initTeXConverter(S.ArcSin, new TeXFunction(this, "arcsin "));
    initTeXConverter(S.ArcTan, new TeXFunction(this, "arctan "));

    initTeXConverter(S.ArcCosh, new TeXFunction(this, "arccosh "));
    initTeXConverter(S.ArcCoth, new TeXFunction(this, "arccoth "));
    initTeXConverter(S.ArcCsch, new TeXFunction(this, "arccsch "));
    initTeXConverter(S.ArcSech, new TeXFunction(this, "arcsech "));
    initTeXConverter(S.ArcSinh, new TeXFunction(this, "arcsinh "));
    initTeXConverter(S.ArcTanh, new TeXFunction(this, "arctanh "));

    initTeXConverter(S.Cos, new TeXFunction(this, "cos "));
    initTeXConverter(S.Cosh, new TeXFunction(this, "cosh "));
    initTeXConverter(S.Cot, new TeXFunction(this, "cot "));
    initTeXConverter(S.Coth, new TeXFunction(this, "coth "));
    initTeXConverter(S.Csc, new TeXFunction(this, "csc "));
    initTeXConverter(S.Csch, new TeXFunction(this, "csch "));
    initTeXConverter(S.Log, new TeXFunction(this, symbolOptions.getLogFunction()));
    initTeXConverter(S.LogisticSigmoid, new TeXFunction(this, "sigma "));
    initTeXConverter(S.Sec, new TeXFunction(this, "sec "));
    initTeXConverter(S.Sech, new TeXFunction(this, "sech "));
    initTeXConverter(S.Sin, new TeXFunction(this, "sin "));
    initTeXConverter(S.Sinh, new TeXFunction(this, "sinh "));
    initTeXConverter(S.Tan, new TeXFunction(this, "tan "));
    initTeXConverter(S.Tanh, new TeXFunction(this, "tanh "));

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

  public void initTeXConverter(ISymbol key, AbstractTeXConverter value) {
    symbolToConverterMap.put(key, value);
  }
}
