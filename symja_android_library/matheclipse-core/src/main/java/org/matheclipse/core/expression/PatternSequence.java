package org.matheclipse.core.expression;

import java.util.List;
import java.util.function.Function;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.form.output.WolframFormFactory;
import org.matheclipse.core.generic.GenericPair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.parser.client.ParserConfig;

/** A concrete pattern sequence implementation (i.e. x__) */
public class PatternSequence extends AbstractPatternSequence {

  /** */
  private static final long serialVersionUID = 2773651826316158627L;

  /**
   * Create a new PatternSequence.
   *
   * @param symbol the associated symbol of the pattern sequence. Maybe <code>null</code>.
   * @param check a header check.Maybe <code>null</code>.
   * @param defaultEnabled if <code>true</code> replace with default value, if no matching was
   *        possible
   * @param zeroArgsAllowed if <code>true</code>, 0 arguments are allowed, otherwise the number of
   *        args has to be >= 1.
   * @return a new PatternSequence
   */
  public static PatternSequence valueOf(final ISymbol symbol, final IExpr check,
      final boolean defaultEnabled, boolean zeroArgsAllowed) {
    PatternSequence p = new PatternSequence();
    p.fSymbol = symbol;
    p.fHeadTest = check;
    p.fDefault = defaultEnabled;
    p.fZeroArgsAllowed = zeroArgsAllowed;
    return p;
  }

  /**
   * Create a new PatternSequence.
   *
   * @param symbol the associated symbol of the pattern sequence. Maybe <code>null</code>.
   * @param check a header check.Maybe <code>null</code>.
   * @param zeroArgsAllowed if <code>true</code>, 0 arguments are allowed, otherwise the number of
   *        args has to be >= 1.
   * @return a new PatternSequence
   */
  public static PatternSequence valueOf(final ISymbol symbol, final IExpr check,
      boolean zeroArgsAllowed) {
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
   *        args has to be >= 1.
   * @return a new PatternSequence
   */
  public static PatternSequence valueOf(final ISymbol symbol, boolean zeroArgsAllowed) {
    return valueOf(symbol, null, zeroArgsAllowed);
  }

  /** The expression which should check this pattern sequence */
  protected IExpr fHeadTest;

  protected PatternSequence() {
    super();
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
      if (fSymbol.equals(pattern.fSymbol) && fDefault == pattern.fDefault
          && fZeroArgsAllowed == pattern.fZeroArgsAllowed) {
        if ((fHeadTest != null) && (pattern.fHeadTest != null)) {
          return fHeadTest.equals(pattern.fHeadTest);
        }
        return fHeadTest == pattern.fHeadTest;
      }
    }
    return false;
  }

  @Override
  public IExpr getHeadTest() {
    return fHeadTest;
  }

  @Override
  public int hashCode() {
    return (fSymbol == null) ? 203 : 17 + fSymbol.hashCode();
  }

  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    if (properties.symbolsAsFactoryMethod) {
      String prefix = SourceCodeProperties.getPrefixF(properties);
      final StringBuilder buffer = new StringBuilder();
      buffer.append(prefix).append("$ps(");
      if (fSymbol == null) {
        buffer.append("(ISymbol)null");
        if (fHeadTest != null) {
          buffer.append(",").append(fHeadTest.internalJavaString(properties, 0, variables));
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
          buffer.append(",").append(fHeadTest.internalJavaString(properties, 0, variables));
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

  @Override
  public String toWolframString() {
    final StringBuilder buffer = new StringBuilder();
    if (fSymbol != null) {
      buffer.append(WolframFormFactory.get().toString(fSymbol));
    }
    buffer.append("__");
    if (fZeroArgsAllowed) {
      buffer.append('_');
    }
    if (fDefault) {
      buffer.append('.');
    }
    if (fHeadTest != null) {
      buffer.append(WolframFormFactory.get().toString(fHeadTest));
    }
    return buffer.toString();
  }

  private CharSequence toCharSequence() {
    final StringBuilder buffer = new StringBuilder();
    if (fSymbol != null) {
      buffer.append(fSymbol.toString());
    }
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
    return buffer;
  }

  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder();
    if (fSymbol == null) {
      buf.append(fZeroArgsAllowed ? "BlankNullSequence" : "BlankSequence");
      buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');
      if (fHeadTest != null) {
        buf.append(fHeadTest.fullFormString());
      }
      buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? ')' : ']');
    } else {
      buf.append("PatternSequence");
      buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? '(' : '[');
      buf.append(fSymbol.toString());
      buf.append(", ");
      buf.append(fZeroArgsAllowed ? "BlankNullSequence" : "BlankSequence");
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        buf.append('(');
      } else {
        buf.append('[');
      }
      if (fHeadTest != null) {
        buf.append(fHeadTest.fullFormString());
      }
      buf.append(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? "))" : "]]");
    }

    return buf.toString();
  }

  @Override
  public int[] addPattern(List<GenericPair<IExpr, IPatternObject>> patternIndexMap) {
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
    return PatternSequence.super.compareTo(expr);
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public boolean isBlank() {
    return false;
  }

  @Override
  public boolean isConditionMatchedSequence(final IAST sequence, IPatternMap patternMap) {
    if (fHeadTest == null) {
      return patternMap.setValue(this, sequence);
    }
    if (sequence.size() > 1) {
      if (!sequence.arg1().head().equals(fHeadTest)) {
        return false;
      }
      if (fHeadTest instanceof BuiltInSymbol) {
        int requiredMask = UniformFlags.uniformMask((BuiltInSymbol) fHeadTest);
        if (requiredMask != UniformFlags.NONE && sequence.isUniform(requiredMask)) {
          return patternMap.setValue(this, sequence);
        }
      }

      for (int i = 2; i < sequence.size(); i++) {
        if (!sequence.get(i).head().equals(fHeadTest)) {
          return false;
        }
      }
    }
    return patternMap.setValue(this, sequence);
    // return true;
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
