package org.matheclipse.core.eval;

import java.io.IOException;
import java.io.Writer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.operator.Precedence;

/** Convert an expression into TeX output */
public class TeXUtilities {
  private static final Logger LOGGER = LogManager.getLogger();

  protected EvalEngine fEvalEngine;

  ExprParser fParser;

  /**
   * @param evalEngine
   * @param relaxedSyntax if <code>true</code> use '(...)' instead of '[...]' to parenthesize the
   *        arguments of a function.
   */
  public TeXUtilities(final EvalEngine evalEngine, final boolean relaxedSyntax) {
    fEvalEngine = evalEngine;
    // set the thread local instance
    EvalEngine.set(evalEngine);
    fParser = new ExprParser(evalEngine, relaxedSyntax);
  }

  /**
   * Converts the inputExpression string into a TeX expression and writes the result to the given
   * <code>Writer</code>
   *
   * @param inputExpression
   * @param out
   * @param reset TODO
   */
  public synchronized boolean toTeX(final String inputExpression, final Writer out, boolean reset) {
    IExpr parsedExpression = null;
    if (inputExpression != null) {
      try {
        if (reset) {
          EvalEngine.setReset(fEvalEngine);
        }
        parsedExpression = fParser.parse(inputExpression);
        return toTeX(parsedExpression, out);
        // parsedExpression = AST2Expr.CONST.convert(node);
      } catch (final RuntimeException rex) {
        LOGGER.debug("TeXUtilities.toTeX() failed", rex);
      }
    }
    return false;
  }

  /**
   * Converts the objectExpression into a TeX expression and writes the result to the given <code>
   * Writer</code>
   *
   * @param objectExpression
   * @param out
   */
  public synchronized boolean toTeX(final IExpr objectExpression, final Writer out) {
    final StringBuilder buf = new StringBuilder();

    if (objectExpression != null) {
      int exponentFigures = fEvalEngine.getSignificantFigures() - 1;
      int significantFigures = fEvalEngine.getSignificantFigures() + 1;
      try {
        IExpr result = objectExpression;
        if (objectExpression.isAST()) {
          result = fEvalEngine.evalHoldPattern((IAST) objectExpression, true, true);
        }

        TeXFormFactory teXFactory =
            new TeXFormFactory(exponentFigures, significantFigures, " \\cdot ");
        if (teXFactory.convert(buf, result, Precedence.NO_PRECEDENCE)) {
          out.write(buf.toString());
          return true;
        } else {
          out.write("ERROR-IN-TEXFORM");
        }
      } catch (final IOException ioe) {
        // parsedExpression == null ==> fError occured
      } catch (final RuntimeException rex) {
        LOGGER.debug("TeXUtilities.toTeX() failed", rex);
      }
      return false;
    }
    return true;
  }

  public void stopRequest() {
    fEvalEngine.stopRequest();
  }
}
