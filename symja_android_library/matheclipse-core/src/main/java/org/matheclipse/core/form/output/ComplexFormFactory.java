package org.matheclipse.core.form.output;

import java.io.IOException;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.InfixOperator;

/** Converts an internal <code>IExpr</code> into a user readable string. */
public abstract class ComplexFormFactory extends AbstractCodeFormFactory {
  private boolean fPackagePrefix;

  public ComplexFormFactory(final boolean relaxedSyntax, final boolean reversed,
      int exponentFigures, int significantFigures, boolean packagePrefix) {
    super(relaxedSyntax, reversed, exponentFigures, significantFigures);
    fPackagePrefix = packagePrefix;
  }

  private void convertInfixOperator(final StringBuilder buf, final IAST operatorAST,
      String operatorStr) {
    int j = 0;
    for (int i = 1; i < operatorAST.size(); i++) {
      final IExpr plusArg = operatorAST.get(i);
      convertInternal(buf, plusArg);
      if (i < operatorAST.argSize()) {
        buf.append("." + operatorStr + "(");
        j++;
      }
    }
    for (int i = 0; i < j; i++) {
      buf.append(")");
    }
  }

  private void convertInfixOperatorReversed(final StringBuilder buf, final IAST operatorAST,
      String operatorStr) {

    IExpr plusArg;
    int size = operatorAST.argSize();
    int j = 0;
    // print Plus[] in reverse order (i.e. numbers at last)
    for (int i = size; i > 0; i--) {
      plusArg = operatorAST.get(i);
      convertInternal(buf, plusArg);
      if (i > 1) {
        buf.append("." + operatorStr + "(");
        j++;
      }
    }
    for (int i = 0; i < j; i++) {
      buf.append(")");
    }
  }

  public void convert(final StringBuilder buf, final IExpr o) {
    IExpr expr = EvalEngine.get().evaluate(F.PiecewiseExpand(o));
    convertInternal(buf, expr, Integer.MIN_VALUE, false);
  }

  @Override
  protected void convertInternal(final StringBuilder buf, final IExpr o, final int precedence,
      boolean isASTHead) {
    if (o instanceof IAST) {
      final IAST list = (IAST) o;

      if (convertOperatorAST(buf, list, precedence, isASTHead)) {
        return;
      }

      convertAST(buf, list);
      return;
    }
    if (o instanceof IReal) {
      double d = o.evalfNaN();
      if (fPackagePrefix) {
        buf.append("org.hipparchus.complex.");
      }
      buf.append("Complex.valueOf(" + d + ")");
      return;
    }
    if (o instanceof INumber) {
      Complex c = o.evalfc();
      if (c != null) {
        if (fPackagePrefix) {
          buf.append("org.hipparchus.complex.");
        }
        buf.append("Complex.valueOf(" + c.getReal() + ", " + c.getImaginary() + ")");
      } else {
        buf.append("Complex.valueOf(" + o.toString() + ")");
      }
      return;
    }
    convertSymbolOrPattern(buf, o);
  }

  @Override
  protected boolean convertArithmetic(final StringBuilder buf, final IAST list,
      final InfixOperator infixOperator, final int precedence, ISymbol head) {
    if (head == S.Plus) {
      if (fPlusReversed) {
        convertInfixOperatorReversed(buf, list, "add");
      } else {
        convertInfixOperator(buf, list, "add");
      }
      return true;
    } else if (head == S.Times) {
      convertInfixOperator(buf, list, "multiply");
      return true;
    } else if (list.isPower()) {
      convertInfixOperator(buf, list, "pow");
      return true;
    }
    return false;
  }


  public void convertFunctionArgs(final StringBuilder buf, final IAST list) {
    append(buf, "(");
    for (int i = 1; i < list.size(); i++) {
      convertInternal(buf, list.get(i));
      if (i < list.argSize()) {
        append(buf, ",");
      }
    }
    append(buf, ")");
  }

  /**
   * Write a function into the given <code>StringBuilder</code>.
   *
   * @param buf
   * @param function
   * @throws IOException
   */
  public abstract void convertAST(final StringBuilder buf, final IAST function);
  // {
  // if (function.isNumericFunction(true)) {
  // try {
  // double value = EvalEngine.get().evalDouble(function);
  // buf.append("(" + value + ")");
  // return;
  // } catch (RuntimeException rex) {
  // //
  // }
  // }
  // IExpr head = function.head();
  // if (head.isSymbol()) {
  // String str = functionHead((ISymbol) head);
  // if (str != null) {
  // buf.append(str);
  // if (function.isAST(S.ArcTan, 3)) {
  // buf.append("2");
  // }
  // convertArgs(buf, head, function,1);
  // return;
  // }
  // }
  // convertInternal(buf, head);
  // convertArgs(buf, head, function, 1);
  // }

  public void convertArgs(final StringBuilder buf, IExpr head, final IAST function,
      int startIndex) {
    append(buf, "(");
    final int argumentSize = function.argSize();
    for (int i = startIndex; i <= argumentSize; i++) {
      convertInternal(buf, function.get(i));
      if (i < argumentSize) {
        append(buf, ",");
      }
    }
    append(buf, ")");
  }

  /** @return Returns the columnCounter. */
  // public int getColumnCounter() {
  // return fColumnCounter;
  // }

  /** @param columnCounter The columnCounter to set. */
  // public void setColumnCounter(final int columnCounter) {
  // fColumnCounter = columnCounter;
  // }
}
