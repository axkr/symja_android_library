package org.matheclipse.core.expression;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.generic.GenericPair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.parser.client.ParserConfig;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A &quot;blank pattern&quot; with no assigned &quot;pattern name&quot; (i.e. &quot;<code>_</code>
 * &quot;)
 */
@SuppressFBWarnings("SING_SINGLETON_HAS_NONPRIVATE_CONSTRUCTOR")
public class Blank implements IPattern {

  /** */
  private static final long serialVersionUID = 1306007999071682207L;

  protected static final Blank BLANK_PATTERN = new Blank();

  private static IPattern valueOf() {
    return BLANK_PATTERN;
  }

  private static IPattern valueOf(final IExpr condition) {
    return new Blank(condition);
  }

  /** The expression which should check the head of the matched expression */
  protected final IExpr fHeadTest;

  /** Use the default value, if no matching expression was found */
  protected final boolean fDefault;

  protected Blank() {
    this(null);
  }

  protected Blank(final IExpr condition) {
    this(condition, false);
  }

  protected Blank(final IExpr condition, boolean def) {
    super();
    this.fHeadTest = condition;
    this.fDefault = def;
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

  @Override
  public int[] addPattern(List<GenericPair<IExpr, IPatternObject>> patternIndexMap) {
    IPatternMap.addPattern(patternIndexMap, this);
    int[] result = new int[2];
    if (isPatternDefault()) {
      // the ast contains a pattern with default value (i.e. "_." or
      // "_:")
      result[0] = IAST.CONTAINS_DEFAULT_PATTERN;
      result[1] = 2;
    } else {
      // the ast contains a pattern without default value (i.e. "_")
      result[0] = IAST.CONTAINS_PATTERN;
      result[1] = 5;
    }
    if (fHeadTest != null) {
      result[1] += 2;
    }
    return result;
  }

  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof Blank) {
      Blank blank = ((Blank) expr);
      if (fDefault != blank.fDefault) {
        return fDefault ? 1 : -1;
      }
      if (fHeadTest == null) {
        if (blank.fHeadTest != null) {
          return -1;
        }
      } else {
        if (blank.fHeadTest == null) {
          return 1;
        }
        int result = fHeadTest.compareTo(blank.fHeadTest);
        if (result != 0) {
          return result;
        }
      }
      return 0;
    }
    return IPattern.super.compareTo(expr);
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
    if (obj instanceof Blank) {
      if (hashCode() != obj.hashCode()) {
        return false;
      }
      Blank blank = (Blank) obj;
      if ((fHeadTest != null) && (blank.fHeadTest != null)) {
        return fHeadTest.equals(blank.fHeadTest);
      }
      return fHeadTest == blank.fHeadTest;
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
    if (patternObject instanceof Blank) {
      // test if the "check" expressions are equal
      final IExpr o1 = getHeadTest();
      final IExpr o2 = patternObject.getHeadTest();
      if ((o1 == null) || (o2 == null)) {
        return o1 == o2;
      }
      return o1.equals(o2);
    }
    return false;
  }

  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder();
    if (fDefault) {
      buf.append("Optional");
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        buf.append('(');
      } else {
        buf.append('[');
      }
    }
    buf.append("Blank");
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append('(');
    } else {
      buf.append('[');
    }
    if (fHeadTest != null) {
      buf.append(fHeadTest.fullFormString());
    }
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append(')');
    } else {
      buf.append(']');
    }
    if (fDefault) {
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        buf.append(')');
      } else {
        buf.append(']');
      }
    }
    return buf.toString();
  }

  @Override
  public IExpr getHeadTest() {
    return fHeadTest;
  }

  @Override
  public int getEvalFlags() {
    if (isPatternDefault()) {
      // the ast contains a pattern with default value (i.e. "x_." or
      // "x_:")
      return IAST.CONTAINS_DEFAULT_PATTERN;
    }
    // the ast contains a pattern without default value (i.e. "x_")
    return IAST.CONTAINS_PATTERN;
  }

  @Override
  public int getIndex(IPatternMap pm) {
    if (pm != null) {
      return pm.indexOf(this);
    }
    return -1;
  }

  @Override
  public ISymbol getSymbol() {
    return null;
  }

  @Override
  public int hashCode() {
    return (fHeadTest == null) ? 193 : 23 + fHeadTest.hashCode();
  }

  @Override
  public ISymbol head() {
    return S.Blank;
  }

  @Override
  public int hierarchy() {
    return BLANKID;
  }

  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    String prefix = SourceCodeProperties.getPrefixF(properties);
    final StringBuilder buffer = new StringBuilder(prefix).append("$b(");
    if (fHeadTest != null) {
      buffer.append(fHeadTest.internalJavaString(properties, 0, variables));
    }
    return buffer.append(')');
  }

  /** {@inheritDoc} */
  @Override
  public boolean isBlank() {
    return true;
  }

  /**
   * SymjaMMA operator overloading
   *
   * @param that
   * @return
   */
  public boolean isCase(IExpr that) {
    final IPatternMatcher matcher = new PatternMatcher(this);
    if (matcher.test(that)) {
      return true;
    }
    return false;
  }

  @Override
  public boolean isConditionMatched(final IExpr expr, IPatternMap patternMap) {
    if (fHeadTest == null || expr.head().equals(fHeadTest)) {
      return patternMap.setValue(this, expr);
      // return true;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFreeOfPatterns() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPatternDefault() {
    return fDefault;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPatternExpr() {
    return true;
  }

  @Override
  public boolean matchPattern(final IExpr expr, IPatternMap patternMap) {
    return isConditionMatched(expr, patternMap);
  }

  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder();
    buffer.append('_');
    if (fHeadTest != null) {
      buffer.append(fHeadTest.toString());
    }
    if (fDefault) {
      buffer.append('.');
    }
    return buffer.toString();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr variables2Slots(final Map<IExpr, IExpr> map,
      final Collection<IExpr> variableCollector) {
    return F.NIL;
  }

  private Object writeReplace() {
    return optional();
  }
}
