package org.matheclipse.core.expression;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.util.Pair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.parser.client.FEConfig;

/** A concrete pattern sequence implementation (i.e. x__) */
public class PatternSequence implements IPatternSequence {
  private static final Logger LOGGER = LogManager.getLogger();


  /** */
  private static final long serialVersionUID = 2773651826316158627L;

  /**
   * Create a new PatternSequence.
   *
   * @param symbol the associated symbol of the pattern sequence. Maybe <code>null</code>.
   * @param check a header check.Maybe <code>null</code>.
   * @param def if <code>true</code> replace with default value, if no matching was possible
   * @param zeroArgsAllowed if <code>true</code>, 0 arguments are allowed, otherwise the number of
   *     args has to be >= 1.
   * @return
   */
  public static PatternSequence valueOf(
      final ISymbol symbol, final IExpr check, final boolean def, boolean zeroArgsAllowed) {
    PatternSequence p = new PatternSequence();
    p.fSymbol = symbol;
    p.fHeadTest = check;
    p.fDefault = def;
    p.fZeroArgsAllowed = zeroArgsAllowed;
    return p;
  }

  /**
   * Create a new PatternSequence.
   *
   * @param symbol the associated symbol of the pattern sequence. Maybe <code>null</code>.
   * @param check a header check.Maybe <code>null</code>.
   * @param zeroArgsAllowed if <code>true</code>, 0 arguments are allowed, otherwise the number of
   *     args has to be >= 1.
   * @return
   */
  public static PatternSequence valueOf(
      final ISymbol symbol, final IExpr check, boolean zeroArgsAllowed) {
    PatternSequence p = new PatternSequence();
    p.fSymbol = symbol;
    p.fHeadTest = check;
    p.fZeroArgsAllowed = zeroArgsAllowed;
    return p;
  }

  /**
   * Create a new PatternSequence.
   *
   * @param symbol the associated symbol of the pattern sequence. Maybe <code>null</code>.
   * @param zeroArgsAllowed if <code>true</code>, 0 arguments are allowed, otherwise the number of
   *     args has to be >= 1.
   * @return
   */
  public static PatternSequence valueOf(final ISymbol symbol, boolean zeroArgsAllowed) {
    return valueOf(symbol, null, zeroArgsAllowed);
  }

  /** The expression which should check this pattern sequence */
  protected IExpr fHeadTest;

  /** The associated symbol for this pattern sequence */
  protected ISymbol fSymbol;

  /** Use default value, if no matching was found */
  protected boolean fDefault = false;

  protected boolean fZeroArgsAllowed = false;

  protected PatternSequence() {}

  @Override
  public int[] addPattern(List<Pair<IExpr, IPatternObject>> patternIndexMap) {
    IPatternMap.addPattern(patternIndexMap, this);
    // the ast contains a pattern sequence (i.e. "x__")
    int[] result = new int[2];
    result[0] = IAST.CONTAINS_PATTERN_SEQUENCE;
    result[1] = 1;
    if (fHeadTest != null) {
      result[1] += 2;
    }
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof PatternSequence) {
      PatternSequence pattern = (PatternSequence) obj;
      if (fSymbol == null) {
        if (pattern.fSymbol == null) {
          if (fDefault == pattern.fDefault && fZeroArgsAllowed == pattern.fZeroArgsAllowed) {
            if ((fHeadTest != null) && (pattern.fHeadTest != null)) {
              return fHeadTest.equals(pattern.fHeadTest);
            }
            return fHeadTest == pattern.fHeadTest;
          }
        }
        return false;
      }
      if (fSymbol.equals(pattern.fSymbol)
          && fDefault == pattern.fDefault
          && fZeroArgsAllowed == pattern.fZeroArgsAllowed) {
        if ((fHeadTest != null) && (pattern.fHeadTest != null)) {
          return fHeadTest.equals(pattern.fHeadTest);
        }
        return fHeadTest == pattern.fHeadTest;
      }
    }
    return false;
  }

  /**
   * Check if the two left-hand-side pattern expressions are equivalent. (i.e. <code>f[x_,y_]</code>
   * is equivalent to <code>f[a_,b_]</code> )
   *
   * @param patternExpr2
   * @param pm1
   * @param pm2
   * @return
   */
  @Override
  public boolean equivalent(
      final IPatternObject patternExpr2, final IPatternMap pm1, IPatternMap pm2) {
    if (this == patternExpr2) {
      return true;
    }
    if (patternExpr2 instanceof PatternSequence) {
      // test if the pattern indices are equal
      final IPatternSequence p2 = (IPatternSequence) patternExpr2;
      if (getIndex(pm1) != p2.getIndex(pm2)) {
        return false;
      }
      // test if the "check" expressions are equal
      final Object o1 = getHeadTest();
      final Object o2 = p2.getHeadTest();
      if ((o1 == null) || (o2 == null)) {
        return o1 == o2;
      }
      return o1.equals(o2);
    }
    return false;
  }

  @Override
  public boolean matchPattern(final IExpr expr, IPatternMap patternMap) {
    IAST sequence = F.Sequence(expr);
    return matchPatternSequence(sequence, patternMap, S.Missing);
  }

  @Override
  public boolean matchPatternSequence(
      final IAST sequence, IPatternMap patternMap, ISymbol optionsPatternHead) {
    if (!isConditionMatchedSequence(sequence, patternMap)) {
      return false;
    }
    if (sequence.size() == 1 && !isNullSequence()) {
      return false;
    }

    IExpr value = patternMap.getValue(this);
    if (value != null) {
      return sequence.equals(value);
    }
    return patternMap.setValue(this, sequence);
    // return true;
  }

  @Override
  public IExpr getHeadTest() {
    return fHeadTest;
  }

  @Override
  public int getEvalFlags() {
    // the ast contains a pattern sequence (i.e. "x__")
    return IAST.CONTAINS_PATTERN_SEQUENCE;
  }

  /** @return */
  @Override
  public int getIndex(IPatternMap pm) {
    if (pm != null) {
      return pm.get(fSymbol);
    }
    return -1;
  }

  /** @return */
  @Override
  public ISymbol getSymbol() {
    return fSymbol;
  }

  @Override
  public int hashCode() {
    return (fSymbol == null) ? 203 : 17 + fSymbol.hashCode();
  }

  /** {@inheritDoc} */
  @Override
  public int hierarchy() {
    return PATTERNID;
  }

  @Override
  public CharSequence internalJavaString(
      boolean symbolsAsFactoryMethod,
      int depth,
      boolean useOperators,
      boolean usePrefix,
      boolean noSymbolPrefix,
      Function<IExpr, ? extends CharSequence> variables) {
    if (symbolsAsFactoryMethod) {
      String prefix = usePrefix ? "F." : "";
      final StringBuilder buffer = new StringBuilder();
      buffer.append(prefix).append("$ps(");
      if (fSymbol == null) {
        buffer.append("(ISymbol)null");
        if (fHeadTest != null) {
          buffer.append(",")
              .append(fHeadTest.internalJavaString(
                      symbolsAsFactoryMethod,
                      0,
                      useOperators,
                      usePrefix,
                      noSymbolPrefix,
                      variables));
        }
        if (fDefault) {
          if (fHeadTest == null) {
            buffer.append(",null");
          }
          buffer.append(",true");
        }
      } else {
        buffer.append("\"" + fSymbol.toString() + "\"");
        if (fHeadTest != null) {
          buffer.append(",")
              .append(fHeadTest.internalJavaString(
                      symbolsAsFactoryMethod,
                      0,
                      useOperators,
                      usePrefix,
                      noSymbolPrefix,
                      variables));
        }
        if (fDefault) {
          buffer.append(",true");
        }
      }
      return buffer.append(')');
    }
    return toCharSequence();
  }

  @Override
  public String toString() {
    return toCharSequence().toString();
  }

  private CharSequence toCharSequence() {
    final StringBuilder buffer = new StringBuilder();
    if (fSymbol == null) {
      buffer.append("__");
      if (fZeroArgsAllowed) {
        buffer.append('_');
      }
      if (fDefault) {
        buffer.append('.');
      }
      if (fHeadTest != null) {
        buffer.append(fHeadTest.toString());
      }
    } else {
      if (fHeadTest == null) {
        buffer.append(fSymbol.toString());
        buffer.append("__");
        if (fZeroArgsAllowed) {
          buffer.append('_');
        }
        if (fDefault) {
          buffer.append('.');
        }
      } else {
        buffer.append(fSymbol.toString());
        buffer.append("__");
        if (fZeroArgsAllowed) {
          buffer.append('_');
        }
        if (fDefault) {
          buffer.append('.');
        }
        buffer.append(fHeadTest.toString());
      }
    }
    return buffer;
  }

  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder();
    if (fSymbol == null) {
      buf.append(fZeroArgsAllowed ? "BlankNullSequence" : "BlankSequence");
      buf.append(FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');
      if (fHeadTest != null) {
        buf.append(fHeadTest.fullFormString());
      }
      buf.append(FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? ')' : ']');
    } else {
      buf.append("PatternSequence");
      buf.append(FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');
      buf.append(fSymbol.toString());
      buf.append(", ");
      buf.append(fZeroArgsAllowed ? "BlankNullSequence" : "BlankSequence");
      if (FEConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        buf.append('(');
      } else {
        buf.append('[');
      }
      if (fHeadTest != null) {
        buf.append(fHeadTest.fullFormString());
      }
      buf.append(FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? "))" : "]]");
    }

    return buf.toString();
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof PatternSequence) {
      if (fSymbol == null) {
        if (((PatternSequence) expr).fSymbol != null) {
          return -1;
        }
      } else if (((PatternSequence) expr).fSymbol == null) {
        return 1;
      } else {
        int cp = fSymbol.compareTo(((PatternSequence) expr).fSymbol);
        if (cp != 0) {
          return cp;
        }
      }

      if (fHeadTest == null) {
        if (((PatternSequence) expr).fHeadTest != null) {
          return -1;
        }
      } else {
        if (((PatternSequence) expr).fHeadTest == null) {
          return 1;
        } else {
          return fHeadTest.compareTo(((PatternSequence) expr).fHeadTest);
        }
      }
      return 0;
    }
    return IPatternSequence.super.compareTo(expr);
  }

  @Override
  public IExpr copy() {
    try {
      return (IExpr) clone();
    } catch (CloneNotSupportedException e) {
      LOGGER.error("PatternSequence.copy() failed", e);
      return null;
    }
  }

  @Override
  public ISymbol head() {
    return S.Pattern;
  }

  @Override
  public boolean isBlank() {
    return false;
  }

  @Override
  public boolean isConditionMatchedSequence(final IAST sequence, IPatternMap patternMap) {
    if (fHeadTest == null) {
      return patternMap.setValue(this, sequence);
      // return true;
    }
    for (int i = 1; i < sequence.size(); i++) {
      if (!sequence.get(i).head().equals(fHeadTest)) {
        return false;
      }
    }
    return patternMap.setValue(this, sequence);
    // return true;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr variables2Slots(
      final Map<IExpr, IExpr> map, final Collection<IExpr> variableCollector) {
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public boolean accept(IVisitorBoolean visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public int accept(IVisitorInt visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public long accept(IVisitorLong visitor) {
    return visitor.visit(this);
  }

  /**
   * Use default value, if no matching was found.
   *
   * @return
   */
  @Override
  public boolean isDefault() {
    return fDefault;
  }

  @Override
  public boolean isNullSequence() {
    return fZeroArgsAllowed;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFreeOfPatterns() {
    return false;
  }

  /** {@inheritDoc} */
  // public boolean isFreeOfDefaultPatterns() {
  // return true;
  // }

  /** {@inheritDoc} */
  @Override
  public final boolean isPatternExpr() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPatternSequence(boolean testNullSequence) {
    if (testNullSequence) {
      return fZeroArgsAllowed;
    }
    return true;
  }

  /** SymjaMMA operator overloading */
  public boolean isCase(IExpr that) {
    final IPatternMatcher matcher = new PatternMatcher(this);
    if (matcher.test(that)) {
      return true;
    }
    return false;
  }
}
