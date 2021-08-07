package org.matheclipse.core.expression;

import org.matheclipse.core.form.output.WolframFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.parser.client.FEConfig;

/**
 * A pattern with assigned &quot;pattern name&quot; (i.e. <code>x: -nested-pattern-expression-
 * </code>).
 */
public class PatternNested extends Pattern {
  private IExpr fPatternExpr;

  public static IPattern valueOf(final ISymbol symbol, final IExpr patternExpr) {
    return new PatternNested(symbol, patternExpr);
  }

  /** package private */
  PatternNested(final ISymbol symbol) {
    super(symbol, null, false);
  }

  /** package private */
  PatternNested(final ISymbol symbol, IExpr patternExpr) {
    super(symbol, null, false);
    fPatternExpr = patternExpr;
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof PatternNested) {
      int cp = fPatternExpr.compareTo(((PatternNested) expr).fPatternExpr);
      if (cp != 0) {
        return cp;
      }
    }
    return super.compareTo(expr);
  }

  @Override
  public IExpr copy() {
    return new PatternNested(fSymbol, fPatternExpr);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof PatternNested) {
      if (hashCode() != obj.hashCode()) {
        return false;
      }
      PatternNested pattern = (PatternNested) obj;
      if (fPatternExpr.equals(pattern.fPatternExpr)) {
        return super.equals(obj);
      }
    }
    return false;
  }

  /**
   * Check if the two left-hand-side pattern expressions are equivalent. (i.e. <code>f[x_,y_]</code>
   * is equivalent to <code>f[a_,b_]</code> )
   *
   * @param patternObject
   * @param pm1
   * @param pm2
   * @return
   */
  @Override
  public boolean equivalent(
      final IPatternObject patternObject, final IPatternMap pm1, IPatternMap pm2) {
    if (this == patternObject) {
      return true;
    }
    if (patternObject instanceof PatternNested) {
      // test if the pattern indices are equal
      final PatternNested p2 = (PatternNested) patternObject;
      if (!fPatternExpr.equals(p2.fPatternExpr)) {
        return false;
      }
      return super.equivalent(patternObject, pm1, pm2);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean matchPattern(final IExpr expr, IPatternMap patternMap) {
    IExpr value = patternMap.getValue(this);
    if (value != null) {
      return expr.equals(value);
    }
    return patternMap.setValue(this, expr);
  }

  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder();
    buf.append("Pattern");
    buf.append(FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');
    buf.append(fSymbol.toString());
    buf.append(", ");
    buf.append(fPatternExpr.fullFormString());
    buf.append(FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? ')' : ']');
    return buf.toString();
  }

  @Override
  public int getIndex(IPatternMap pm) {
    if (pm != null) {
      return pm.get(fSymbol);
    }
    return -1;
  }

  @Override
  public ISymbol getSymbol() {
    return fSymbol;
  }

  @Override
  public int hashCode() {
    return (fSymbol == null) ? 51 : 51 + fSymbol.hashCode();
  }

  @Override
  public ISymbol head() {
    return S.Pattern;
  }

  @Override
  public int hierarchy() {
    return PATTERNID;
  }

  @Override
  public boolean isConditionMatched(final IExpr expr, IPatternMap patternMap) {
    return (fHeadTest == null || expr.head().equals(fHeadTest));
  }

  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder();

    buffer.append('(');
    buffer.append(fSymbol.toString());
    buffer.append(':');
    buffer.append(fPatternExpr.toString());
    buffer.append(')');

    return buffer.toString();
  }

  @Override
  public String toWolframString() {
    final StringBuilder buffer = new StringBuilder();

    buffer.append('(');
    buffer.append(fSymbol.toString());
    buffer.append(':');
    buffer.append(WolframFormFactory.get().toString(fPatternExpr));
    buffer.append(')');

    return buffer.toString();
  }

  public IExpr getPatternExpr() {
    return fPatternExpr;
  }
}
