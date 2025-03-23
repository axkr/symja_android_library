package org.matheclipse.core.expression;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.form.output.WolframFormFactory;
import org.matheclipse.core.generic.GenericPair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.PatternMatcherEquals;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.parser.client.ParserConfig;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class OptionsPattern extends AbstractPatternSequence {

  private static final long serialVersionUID = 1086461999754718513L;

  public static void extractRules(IExpr x, IASTAppendable optionsPattern) {
    if (x != null) {
      if (x.isSequence() || x.isList()) {
        ((IAST) x).forEach(arg -> extractRules(arg, optionsPattern));
      } else if (x.isRuleAST()) {
        if (x.first().isSymbol()) {
          String name = ((ISymbol) x.first()).getSymbolName();
          optionsPattern.append(F.binaryAST2(x.topHead(), name, x.second()));
        } else {
          optionsPattern.append(x);
        }
      }
    }
  }

  /**
   * Returns a list of the default options of a symbol defined by
   * <code>Option(f)={a-&gt;b,...}</code>.
   *
   * @param symbol
   * @param optionValueRules convert to &quot;string&quot;" rules, suitable for <code>OptionValue
   *     </code>
   * @return
   */
  public static IAST optionsList(ISymbol symbol, boolean optionValueRules) {
    RulesData rules = symbol.getRulesData();
    if (rules != null) {
      Map<IExpr, PatternMatcherEquals> map = rules.getEqualDownRules();
      PatternMatcherEquals matcher = map.get(F.Options(symbol));
      if (matcher != null) {
        IExpr temp = matcher.getRHS();
        if (optionValueRules) {
          IASTAppendable result = F.ListAlloc(10);
          extractRules(temp, result);
          return result;
        }
        return temp.makeList();
      }
    }
    return F.CEmptyList;
  }

  public static OptionsPattern valueOf(final ISymbol symbol) {
    return valueOf(symbol, F.NIL);
  }

  public static OptionsPattern valueOf(final ISymbol symbol, IExpr defaultOptions) {
    OptionsPattern p = new OptionsPattern();
    p.fSymbol = symbol;
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
   * @param x
   * @param engine
   */
  public void addOptionsPattern(@Nonnull IExpr x, @Nonnull EvalEngine engine) {
    if (x.size() > 1 && (x.isSequence() || x.isList())) {
      ((IAST) x).forEach(arg -> addOptionsPattern(arg, engine));
    } else {
      addOptionsPatternRule((IAST) x, engine);
    }
  }

  /**
   * @param rule may be <code>null</code>
   */
  private void addOptionsPatternRule(@Nullable IAST rule, @Nonnull EvalEngine engine) {
    IdentityHashMap<ISymbol, IASTAppendable> optionsPattern = engine.peekOptionsStack();
    optionsPattern(rule, optionsPattern);
  }

  @Override
  public int[] addPattern(List<GenericPair<IExpr, IPatternObject>> patternIndexMap) {
    IPatternMap.addPattern(patternIndexMap, this);
    // the ast contains a pattern sequence (i.e. "x__")
    int[] result = new int[2];
    result[0] = IAST.CONTAINS_PATTERN_SEQUENCE;
    result[1] = 1;
    return result;
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
    return this;
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
      if (fSymbol.equals(pattern.fSymbol) && fDefault == pattern.fDefault
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
      buf.append(
          ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? "OptionsPattern()" : "OptionsPattern[]");
    }
    return buf.toString();
  }

  public IExpr getDefaultOptions() {
    return fDefaultOptions;
  }

  @Override
  public IExpr getHeadTest() {
    return null;
  }

  public ISymbol getOptionsPatternHead() {
    return fOptionsPatternHead;
  }

  @Override
  public int hashCode() {
    return (fSymbol == null) ? 213 : 37 + fSymbol.hashCode();
  }

  /** {@inheritDoc} */
  @Override
  public int hierarchy() {
    return PATTERNID;
  }

  @Override
  public boolean isConditionMatchedSequence(IAST sequence, IPatternMap patternMap) {
    return patternMap.setValue(this, sequence);
  }

  @Override
  public boolean isOptionsPattern() {
    return true;
  }

  @Override
  public boolean matchPatternSequence(final IAST sequence, IPatternMap patternMap,
      ISymbol optionsPatternHead) {
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
      IExpr ruleAST = ruleASTRecursive(sequence.get(i));
      if (ruleAST.isNIL()) {
        return false;
      }
    }

    IExpr value = patternMap.getValue(this);
    if (value != null) {
      if (value.isList() || value.isSequence()) {
        IAST list = (IAST) value;
        for (int i = 1; i < list.size(); i++) {
          IExpr ruleAST = ruleASTRecursive(list.get(i));
          if (ruleAST.isNIL()) {
            return false;
          }
        }
      } else {
        IExpr ruleAST = ruleASTRecursive(value);
        if (ruleAST.isNIL()) {
          return false;
        }
      }
      this.fOptionsPatternHead = optionsPatternHead;
      return true;
    }
    this.fOptionsPatternHead = optionsPatternHead;
    return patternMap.setValue(this, sequence);
  }

  /**
   * Extract the rule from the argument. If the argument is a {@link S#Rule} or
   * {@link S#RuleDelayed} AST return the argument. If the argument is a list with exactly one
   * argument, call this method recursively with the first argument of the list.
   * 
   * @param arg
   * @return the rule or {@link F#NIL} if no rule was found
   */
  private static IExpr ruleASTRecursive(IExpr arg) {
    if (arg.isRuleAST()) {
      return arg;
    }
    if (arg.isList()) {
      IAST list = (IAST) arg;
      for (int i = 1; i < list.size(); i++) {
        IExpr element = ruleASTRecursive(list.get(i));
        if (element.isPresent()) {
          return element;
        }
      }
    }
    return F.NIL;
  }

  /**
   * @param rule may be <code>null</code>
   */
  public void optionsPattern(@Nullable IAST rule,
      IdentityHashMap<ISymbol, IASTAppendable> optionsPatternMap) {
    IASTAppendable list = optionsPatternMap.get(getOptionsPatternHead());
    if (list == null) {
      list = F.ListAlloc(10);
      optionsPatternMap.put(getOptionsPatternHead(), list);
    }
    if (rule != null && rule.isRuleAST()) {
      if (rule.first().isSymbol()) {
        list.append(
            F.binaryAST2(rule.topHead(), ((ISymbol) rule.first()).getSymbolName(), rule.second()));
      } else {
        list.append(rule);
      }
    }
    IExpr defaultOptions = getDefaultOptions();
    if (defaultOptions.isPresent()) {
      IAST optionsList = null;
      if (defaultOptions.isSymbol()) {
        optionsList = OptionsPattern.optionsList((ISymbol) defaultOptions, true);
        extractRules(optionsList, list);
      } else if (defaultOptions.isList()) {
        extractRules(defaultOptions, list);
      } else if (defaultOptions.isRuleAST()) {
        extractRules(defaultOptions, list);
      }
    }
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
      buffer.append(WolframFormFactory.get().toString(fSymbol));
      buffer.append(":OptionsPattern[]");
    }
    return buffer.toString();
  }
}
