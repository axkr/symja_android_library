package org.matheclipse.core.form.output;

import java.io.IOException;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;

public class WolframFormFactory extends OutputFormFactory {

  private WolframFormFactory(final boolean relaxedSyntax, final boolean reversed,
      int exponentFigures, int significantFigures) {
    super(relaxedSyntax, reversed, exponentFigures, significantFigures);
    this.fInputForm = true;
  }

  @Override
  public void convertSymbol(final Appendable buf, final ISymbol symbol) throws IOException {
    Context context = symbol.getContext();
    if (context == Context.DUMMY) {
      append(buf, symbol.getSymbolName());
      return;
    }

    String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(symbol.getSymbolName());
    if (str != null) {
      // assuming Wolfram language built-in function
      append(buf, str);
      return;
    }
    if (EvalEngine.get().getContextPath().contains(context)) {
      append(buf, symbol.getSymbolName());
    } else {
      append(buf, context.completeContextName() + symbol.getSymbolName());
    }
  }

  public void convertPattern(final Appendable buf, final IPatternObject pattern)
      throws IOException {
    append(buf, pattern.toWolframString());
  }

  /**
   * Get an <code>WolframFormFactory</code> for converting an internal expression to Wolfram
   * language input form string.
   *
   * @return
   */
  public static WolframFormFactory get() {
    return new WolframFormFactory(false, false, -1, -1);
  }
}
