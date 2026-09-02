package org.matheclipse.core.form.output;

import java.io.IOException;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.Precedence;

public class WolframFormFactory extends OutputFormFactory {

  private WolframFormFactory(final boolean relaxedSyntax, final boolean reversed,
      int exponentFigures, int significantFigures) {
    super(relaxedSyntax, reversed, exponentFigures, significantFigures);
    this.fInputForm = true;
  }

  @Override
  public void convertSymbol(final Appendable buf, final ISymbol symbol) throws IOException {
    Context context = symbol.getContext();
    if (context == Context.DUMMY || context == Context.FORMAL) {
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

  @Override
  public void convertPattern(final Appendable buf, final IPatternObject pattern)
      throws IOException {
    append(buf, pattern.toWolframString());
  }

  /**
   * Render the non-finite machine doubles as the symbols the Wolfram Language uses for them, and
   * everything else the way {@link OutputFormFactory} does.
   *
   * <p>
   * The inherited input form appends the precision marker to whatever
   * {@link Double#toString(double)} produced, turning the three non-finite values into
   * <code>Infinity`</code>,
   * <code>-Infinity`</code> and <code>NaN`</code>. Symja reads all three back, so its own
   * <code>InputForm</code> is right to print them; the Wolfram Language does not. There a backtick
   * separates a context from a symbol rather than marking precision, so <code>Infinity`</code> is
   * not a number at all, and <code>NaN</code> is not a symbol it knows &mdash; the name is
   * <code>Indeterminate</code>. Bridging differences of exactly this kind is what this class is
   * for; it already does it for symbol names and for pattern objects.
   *
   * <p>
   * The substitutions are the ones Symja itself displays for these values in
   * {@link Num#toString()}, so nothing is invented here: only the input form disagreed.
   */
  @Override
  public void convertDouble(final Appendable buf, final INum d, final int precedence,
      final boolean caller) throws IOException {
    final double value = d.doubleValue();
    if (!Double.isNaN(value) && !Double.isInfinite(value)) {
      super.convertDouble(buf, d, precedence, caller);
      return;
    }
    final boolean isNegative = value < 0.0;
    if (!isNegative && caller == PLUS_CALL) {
      append(buf, " + ");
    }
    // a leading minus binds looser than what encloses it, exactly as for a negative number
    final boolean parenthesize = isNegative && Precedence.PLUS < precedence;
    if (parenthesize) {
      append(buf, "(");
    }
    appendNonFinite(buf, value);
    if (parenthesize) {
      append(buf, ")");
    }
  }

  /**
   * Render a machine complex whose real or imaginary part is not finite.
   *
   * <p>
   * The inherited version formats both parts through {@link Num#fullFormString(double)} and so
   * carries the same defect into <code>Infinity` + I*1.0`</code>. Anything finite is left to the
   * superclass.
   */
  @Override
  public void convertDoubleComplex(final Appendable buf, final IComplexNum dc, final int precedence,
      final boolean caller) throws IOException {
    final double realPart = dc.getRealPart();
    final double imaginaryPart = dc.getImaginaryPart();
    if (isFinite(realPart) && isFinite(imaginaryPart)) {
      super.convertDoubleComplex(buf, dc, precedence, caller);
      return;
    }
    if (caller == PLUS_CALL) {
      append(buf, " + ");
    }
    final boolean parenthesize = Precedence.PLUS < precedence;
    if (parenthesize) {
      append(buf, "(");
    }
    appendNonFinite(buf, realPart);
    append(buf, " + I*");
    appendNonFinite(buf, imaginaryPart);
    if (parenthesize) {
      append(buf, ")");
    }
  }

  /** Append {@code value} as Wolfram Language input, naming the non-finite values symbolically. */
  private void appendNonFinite(final Appendable buf, final double value) throws IOException {
    if (Double.isNaN(value)) {
      append(buf, "Indeterminate");
    } else if (Double.isInfinite(value)) {
      append(buf, value < 0.0 ? "-Infinity" : "Infinity");
    } else {
      append(buf, Num.fullFormString(value));
    }
  }

  /** @return {@code true} if {@code value} is neither infinite nor {@code NaN} */
  private static boolean isFinite(final double value) {
    return !Double.isNaN(value) && !Double.isInfinite(value);
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
