package org.matheclipse.core.expression;

import java.util.List;
import java.util.function.Function;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.generic.GenericPair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.parser.client.ParserConfig;

/** A pattern with assigned &quot;pattern name&quot; (i.e. <code>x_</code>) */
public class Pattern extends Blank {

  public static IPattern valueOf(final ISymbol symbol) {
    if (symbol.getContext().equals(Context.DUMMY)) {
      IPattern value = F.getPredefinedPattern(symbol.toString());
      if (value != null) {
        return value;
      }
    }

    return new Pattern(symbol);
  }

  /**
   * @param symbol
   * @param check
   * @return
   */
  public static IPattern valueOf(final ISymbol symbol, final IExpr check) {
    return new Pattern(symbol, check);
  }

  /**
   * @param symbol
   * @param check
   * @param def if <code>true</code> use a default value, if matching isn't possible
   * @return
   */
  public static IPattern valueOf(final ISymbol symbol, final IExpr check, final boolean def) {
    return new Pattern(symbol, check, def);
  }

  private static final long serialVersionUID = 7617138748475243L;

  /** The associated symbol for this pattern */
  protected final ISymbol fSymbol;

  /** constructor for serialization */
  private Pattern() {
    fSymbol = null;
  }

  /** package private */
  Pattern(final ISymbol symbol) {
    this(symbol, null, false);
  }

  /** package private */
  Pattern(final ISymbol symbol, IExpr condition) {
    this(symbol, condition, false);
  }

  /** package private */
  public Pattern(final ISymbol symbol, IExpr condition, boolean def) {
    super(condition, def);
    fSymbol = symbol;
  }

  @Override
  public int[] addPattern(List<GenericPair<IExpr, IPatternObject>> patternIndexMap) {
    IPatternMap.addPattern(patternIndexMap, this);
    int[] result = new int[2];
    if (isPatternDefault()) {
      // the ast contains a pattern with default value (i.e. "x_." or
      // "x_:")
      result[0] = IAST.CONTAINS_DEFAULT_PATTERN;
      result[1] = 3;
    } else {
      // the ast contains a pattern without default value (i.e. "x_")
      result[0] = IAST.CONTAINS_PATTERN;
      result[1] = 6;
    }
    if (fHeadTest != null) {
      result[1] += 2;
    }
    return result;
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof Pattern) {
      Pattern pat = ((Pattern) expr);
      int cp = fSymbol.compareTo(pat.fSymbol);
      if (cp != 0) {
        return cp;
      }
    }
    return super.compareTo(expr);
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Pattern) {
      if (hashCode() != obj.hashCode()) {
        return false;
      }
      Pattern pattern = (Pattern) obj;
      if (fDefault != pattern.fDefault) {
        return false;
      }
      if (fSymbol.equals(pattern.fSymbol)) {
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
  public boolean equivalent(final IPatternObject patternObject, final IPatternMap pm1,
      IPatternMap pm2) {
    if (this == patternObject) {
      return true;
    }
    if (patternObject instanceof Pattern) {
      // test if the pattern indices are equal
      final IPattern p2 = (IPattern) patternObject;
      if (getIndex(pm1) != p2.getIndex(pm2)) {
        return false;
      }
      // test if the "check" expressions are equal
      final IExpr o1 = getHeadTest();
      final IExpr o2 = p2.getHeadTest();
      if ((o1 == null) || (o2 == null)) {
        return o1 == o2;
      }
      return o1.equals(o2);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean matchPattern(final IExpr expr, IPatternMap patternMap) {
    if (!isConditionMatched(expr, patternMap)) {
      return false;
    }

    IExpr value = patternMap.getValue(this);
    if (value != null) {
      return expr.equals(value);
    }
    return patternMap.setValue(this, expr);
  }

  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder();
    if (fDefault) {
      buf.append("Optional");
      buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');
    }
    buf.append("Pattern");
    buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');
    buf.append(fSymbol.toString());
    buf.append(", ");
    buf.append("Blank");
    buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');
    if (fHeadTest != null) {
      buf.append(fHeadTest.fullFormString());
    }
    buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? "))" : "]]");
    if (fDefault) {
      buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? ')' : ']');
    }
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
    return (fSymbol == null) ? 19 : 19 + fSymbol.hashCode();
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
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    String prefix = SourceCodeProperties.getPrefixF(properties);
    final StringBuilder buffer = new StringBuilder(prefix);
    String symbolStr = fSymbol.toString();
    char ch = symbolStr.charAt(0);
    if (symbolStr.length() == 1) { // && fOptionalValue == null) {
      if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'G' && ch != 'D' && ch != 'E')
          || ch == 'P' || ch == 'Q') {
        if (!fDefault) {
          if (fHeadTest == null) {
            return buffer.append(symbolStr).append("_");
          } else if (fHeadTest == S.Symbol) {
            return buffer.append(symbolStr).append("_Symbol");
          }
        } else {
          if (fHeadTest == null) {
            return buffer.append(symbolStr).append("_DEFAULT");
          }
        }
      }
    }
    if (Config.RUBI_CONVERT_SYMBOLS) {
      if (ch == '§' && symbolStr.length() == 2) {
        char ch2 = symbolStr.charAt(1);
        if ('a' <= ch2 && ch2 <= 'z') {
          if (!fDefault) {
            if (fHeadTest == null) {
              return buffer.append("p").append(ch2).append("_");
            }
          } else {
            if (fHeadTest == null) {
              return buffer.append("p").append(ch2).append("_DEFAULT");
            }
          }
        }
      }
    }
    buffer.append("$p(");
    if (symbolStr.length() == 1 && ('a' <= ch && ch <= 'z')) {
      buffer.append(prefix).append(symbolStr);
    } else {
      buffer.append("\"").append(symbolStr).append("\"");
    }
    if (fHeadTest != null) {
      if (fHeadTest == S.Integer) {
        buffer.append(", Integer");
      } else if (fHeadTest == S.Symbol) {
        buffer.append(", Symbol");
      } else {
        buffer.append(",").append(fHeadTest.internalJavaString(properties, 0, variables));
      }
    }
    if (fDefault) {
      buffer.append(",true");
    }
    return buffer.append(')');
  }

  /** {@inheritDoc} */
  @Override
  public boolean isBlank() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPattern() {
    return true;
  }

  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder();
    if (fHeadTest == null) {
      buffer.append(fSymbol.toString());
      buffer.append('_');
      if (fDefault) {
        buffer.append('.');
      }
    } else {
      buffer.append(fSymbol.toString());
      buffer.append('_');
      if (fDefault) {
        buffer.append('.');
      }
      buffer.append(fHeadTest.toString());
    }
    return buffer.toString();
  }

  private Object writeReplace() {
    return optional();
  }
}
