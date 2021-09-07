package org.matheclipse.core.expression;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.parser.client.ParserConfig;

public class OptionsPattern extends PatternSequence {
  private static final Logger LOGGER = LogManager.getLogger();

  private static final long serialVersionUID = 1086461999754718513L;

  public static OptionsPattern valueOf(final ISymbol symbol) {
    return valueOf(symbol, F.NIL);
  }

  public static OptionsPattern valueOf(final ISymbol symbol, IExpr defaultOptions) {
    OptionsPattern p = new OptionsPattern();
    p.fSymbol = symbol;
    p.fHeadTest = null;
    p.fDefault = false;
    p.fZeroArgsAllowed = true;
    p.fDefaultOptions = defaultOptions;
    p.fOptionsPatternHead = null;
    return p;
  }

  protected IExpr fDefaultOptions = F.NIL;

  private ISymbol fOptionsPatternHead = null;

  protected OptionsPattern() {
    super();
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof OptionsPattern) {
      OptionsPattern optionsPattern = (OptionsPattern) expr;
      if (fDefaultOptions.isPresent() && optionsPattern.fDefaultOptions.isPresent()) {
        int cp = fDefaultOptions.compareTo(optionsPattern.fDefaultOptions);
        if (cp != 0) {
          return cp;
        }
      } else {
        if (fDefaultOptions != optionsPattern.fDefaultOptions) {
          return fDefaultOptions.isPresent() ? 1 : -1;
        }
      }
    }
    return super.compareTo(expr);
  }

  @Override
  public IExpr copy() {
    try {
      return (IExpr) clone();
    } catch (CloneNotSupportedException e) {
      LOGGER.error("OptionsPattern.copy() failed", e);
      return null;
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof OptionsPattern) {
      OptionsPattern pattern = (OptionsPattern) obj;
      if (fSymbol == null) {
        if (pattern.fSymbol == null) {
          if (fDefault == pattern.fDefault && fZeroArgsAllowed == pattern.fZeroArgsAllowed) {
            return fDefaultOptions.equals(pattern.fDefaultOptions);
          }
        }
        return false;
      }
      if (fSymbol.equals(pattern.fSymbol)
          && fDefault == pattern.fDefault
          && fZeroArgsAllowed == pattern.fZeroArgsAllowed) {
        return fDefaultOptions.equals(pattern.fDefaultOptions);
      }
    }
    return false;
  }

  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder();

    if (fSymbol != null) {
      buf.append("Pattern");
      buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');

      buf.append(fSymbol.fullFormString());
      buf.append(
          ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? ",OptionsPattern())" : ",OptionsPattern[]]");
    } else {
      buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? "OptionsPattern()" : "OptionsPattern[]");
    }
    return buf.toString();
  }

  public IExpr getDefaultOptions() {
    return fDefaultOptions;
  }

  public ISymbol getOptionsPatternHead() {
    return fOptionsPatternHead;
  }

  @Override
  public int hashCode() {
    return (fSymbol == null) ? 213 : 37 + fSymbol.hashCode();
  }

  @Override
  public boolean isOptionsPattern() {
    return true;
  }

  @Override
  public boolean matchPatternSequence(
      final IAST sequence, IPatternMap patternMap, ISymbol optionsPatternHead) {
    // if (!isConditionMatchedSequence(sequence, patternMap)) {
    // return false;
    // }
    if (this.fOptionsPatternHead != null) {
      if (!this.fOptionsPatternHead.equals(optionsPatternHead)) {
        return false;
      }
    }
    if (sequence.size() == 1) {
      this.fOptionsPatternHead = optionsPatternHead;
      return patternMap.setValue(this, sequence);
    }
    for (int i = 1; i < sequence.size(); i++) {
      if (!sequence.get(i).isRuleAST()) {
        return false;
      }
    }

    IExpr value = patternMap.getValue(this);
    if (value != null) {
      if (value.isList() || value.isSequence()) {
        IAST list = (IAST) value;
        for (int i = 1; i < list.size(); i++) {
          if (!list.get(i).isRuleAST()) {
            return false;
          }
        }
      } else if (!value.isRuleAST()) {
        return false;
      }
      this.fOptionsPatternHead = optionsPatternHead;
      return true;
    }
    this.fOptionsPatternHead = optionsPatternHead;
    return patternMap.setValue(this, sequence);
  }

  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder();
    if (fSymbol == null) {
      buffer.append("OptionsPattern()");
    } else {
      buffer.append(fSymbol.toString());
      buffer.append(":OptionsPattern()");
    }
    return buffer.toString();
  }

  @Override
  public String toWolframString() {
    final StringBuilder buffer = new StringBuilder();
    if (fSymbol == null) {
      buffer.append("OptionsPattern[]");
    } else {
      buffer.append(fSymbol.toString());
      buffer.append(":OptionsPattern[]");
    }
    return buffer.toString();
  }
}
