package org.matheclipse.core.eval;

import java.io.IOException;
import java.io.Writer;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.mathml.MathMLFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

/**
 * Convert an expression into presentation MathML output
 *
 * <p>
 * See <a href="http://www.w3.org/TR/2000/CR-MathML2-20001113/byalpha.html">Chracters ordered by
 * Unicode</a>
 */
public class MathMLUtilities {

  protected EvalEngine fEvalEngine;

  // protected MathMLFormFactory fMathMLFactory;

  /** MS Internet Explorer client ? */
  boolean fMSIE;

  /** Print MathML header in output */
  boolean fMathMLHeader;

  static {
    // initialize the global available symbols
    F.initSymbols();
  }

  /**
   * Constructor for an object which converts an expression into presentation MathML output
   *
   * @param evalEngine
   * @param mathMLTagPrefix if set to <code>true</code> use &quot;m:&quot; as tag prefix for the
   *        MathML output.
   * @param mathMLHeader print MathML header in output
   */
  // public MathMLUtilities(final EvalEngine evalEngine, final boolean mathMLTagPrefix, final
  // boolean mathMLHeader) {
  // this(evalEngine, mathMLTagPrefix, mathMLHeader, null);
  // }

  /**
   * Constructor for an object which converts an expression into presentation MathML output
   *
   * @param evalEngine
   * @param mathMLTagPrefix if set to <code>true</code> use &quot;m:&quot; as tag prefix for the
   *        MathML output.
   * @param mathMLHeader print MathML header in output
   */
  public MathMLUtilities(final EvalEngine evalEngine, final boolean mathMLTagPrefix,
      final boolean mathMLHeader) {
    fEvalEngine = evalEngine;
    EvalEngine.set(fEvalEngine);
    // if (mathMLTagPrefix) {
    // fMathMLFactory = new MathMLFormFactory("m:", numberFormat);
    // } else {
    // fMathMLFactory = new MathMLFormFactory("", numberFormat);
    // }
    // fParser = new Parser(relaxedSyntax);
    fMSIE = mathMLTagPrefix;
    fMathMLHeader = mathMLHeader;
  }

  /**
   * Get the current evaluation engine.
   *
   * @return
   */
  public EvalEngine getEvalEngine() {
    return fEvalEngine;
  }

  public synchronized boolean toMathML(final String inputExpression, final Writer out) {
    return toMathML(inputExpression, out, false);
  }
  /**
   * Converts the inputExpression string into a MathML expression and writes the result to the given
   * <code>Writer</code>
   *
   * @param inputExpression
   * @param out
   */
  public synchronized boolean toMathML(final String inputExpression, final Writer out,
      boolean plusReversed) {
    IExpr parsedExpression = null;
    // ASTNode node;
    if (inputExpression != null) {
      try {
        ExprParser parser = new ExprParser(fEvalEngine);
        parsedExpression = parser.parse(inputExpression);
        // node = fEvalEngine.parseNode(inputExpression);
        // parsedExpression = AST2Expr.CONST.convert(node, fEvalEngine);
      } catch (final RuntimeException rex) {
        Errors.printMessage(S.MathMLForm, rex, fEvalEngine);
      }
    }
    return toMathML(parsedExpression, out, plusReversed, false);
  }

  /**
   * Converts the objectExpression into a MathML expression and writes the result to the given
   * <code>Writer</code>
   *
   * @param objectExpression
   * @param out
   */
  public synchronized boolean toMathML(final IExpr objectExpression, final Writer out) {
    return toMathML(objectExpression, out, false, false);
  }

  public synchronized boolean toMathML(final IExpr objectExpression, final Writer out,
      boolean useXmlns) {
    return toMathML(objectExpression, out, false, useXmlns);
  }

  public synchronized boolean toMathML(final IExpr objectExpression, final Writer out,
      boolean plusReversed, boolean useXmlns) {
    final StringBuilder buf = new StringBuilder();

    if (objectExpression != null) {
      try {
        MathMLFormFactory mathMLFactory = new MathMLFormFactory(fMSIE ? "m:" : "",
            fEvalEngine.isRelaxedSyntax(), plusReversed, null,
            fEvalEngine.getSignificantFigures() - 1, fEvalEngine.getSignificantFigures() + 1);

        if (mathMLFactory.convert(buf, objectExpression, Integer.MIN_VALUE, false)) {
          if (fMSIE) {
            out.write("<m:math>");
            out.write(buf.toString());
            out.write("</m:math>");
          } else {
            if (fMathMLHeader) {
              out.write("<?xml version=\"1.0\"?>\n"
                  + "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
                  + "<math mode=\"display\">\n");
            } else {
              if (useXmlns) {
                out.write("<math xmlns=\"http://www.w3.org/1999/xhtml\">");
              } else {
                out.write("<math>");
              }
            }

            out.write(buf.toString());
            out.write("</math>");
          }
          return true;
        }
      } catch (final IOException ioe) {
        //
      } catch (final RuntimeException rex) {
        Errors.printMessage(S.MathMLForm, rex, fEvalEngine);
      }
      return false;
    }
    return true;
  }

  private synchronized void toJava(final String inputExpression, final Writer out,
      boolean strictJava) {
    IExpr parsedExpression = null;
    // ASTNode node;
    if (inputExpression != null) {
      try {
        ExprParser parser = new ExprParser(fEvalEngine);
        parsedExpression = parser.parse(inputExpression);
        // node = fEvalEngine.parseNode(inputExpression);
        // parsedExpression = AST2Expr.CONST.convert(node, fEvalEngine);
        out.write(parsedExpression
            .internalJavaString(SourceCodeProperties.JAVA_FORM_PROPERTIES, -1, x -> null)
            .toString());
      } catch (final IOException ioe) {
        //
      } catch (final RuntimeException rex) {
        Errors.printMessage(S.MathMLForm, rex, fEvalEngine);
      }
    }
  }

  /** Stop the current evaluation thread. */
  // public void stopRequest() {
  // fEvalEngine.stopRequest();
  // }
}
