package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.AttributeFunctions;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.generic.UnaryVariable2Slot;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherAndInvoker;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.parser.client.ParserConfig;

public class Symbol implements ISymbol, Serializable {
  private static final Logger LOGGER = LogManager.getLogger();

  protected transient Context fContext;

  /** The attribute values of the symbol represented by single bits. */
  protected int fAttributes = NOATTRIBUTE;

  /** Flags for controlling evaluation and left-hand-side pattern-matching expressions */
  protected int fEvalFlags = 0;

  /** The value associate with this symbol. */
  private transient IExpr fValue;

  /** The pattern matching &quot;down value&quot; rules associated with this symbol. */
  protected transient RulesData fRulesData;

  /**
   * The name of this symbol. The characters may be all lower-cases if the system doesn't
   * distinguish between lower- and upper-case function names.
   */
  protected String fSymbolName;

  // public static ISymbol valueOf(final String symbolName, final Context context) {
  // return new Symbol(symbolName, context);
  // }

  public Symbol(final String symbolName, final Context context) {
    super();
    fContext = context;
    fSymbolName = symbolName;
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

  /** {@inheritDoc} */
  @Override
  public final void addAttributes(final int attributes) {
    fAttributes |= attributes;
    if (isLocked()) {
      throw new RuleCreationError(this);
    }
    EvalEngine engine = EvalEngine.get();
    engine.addModifiedVariable(this);
  }

  /** {@inheritDoc} */
  @Override
  public final ISymbol addEvalFlags(final int flags) {
    fEvalFlags |= flags;
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr apply(IExpr... expressions) {
    return F.function(this, expressions);
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr assignedValue() {
    if (this instanceof IBuiltInSymbol) {
      return fValue;
    }
    addEvalFlags(DIRTY_FLAG_ASSIGNED_VALUE);
    return fValue;
  }

  /** {@inheritDoc} */
  @Override
  public void assignValue(final IExpr value, boolean setDelayed) {
    fValue = value;
    clearEvalFlags(DIRTY_FLAG_ASSIGNED_VALUE);
    if (setDelayed) {
      addEvalFlags(SETDELAYED_FLAG_ASSIGNED_VALUE);
    } else {
      clearEvalFlags(SETDELAYED_FLAG_ASSIGNED_VALUE);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void clear(EvalEngine engine) {
    if (!engine.isPackageMode()) {
      if (isLocked()) {
        throw new RuleCreationError(this);
      }
    }
    clearValue();
    if (fRulesData != null) {
      fRulesData.clear();
    }
  }

  /** {@inheritDoc} */
  @Override
  public void clearAll(EvalEngine engine) {
    if (!engine.isPackageMode()) {
      if (isLocked()) {
        throw new RuleCreationError(this);
      }
    }
    fAttributes = NOATTRIBUTE;
    clearValue();
    if (fRulesData != null) {
      fRulesData = null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public void clearAttributes(final int attributes) {
    fAttributes &= (0xffffffff ^ attributes);
    if (isLocked()) {
      throw new RuleCreationError(this);
    }
    EvalEngine engine = EvalEngine.get();
    engine.addModifiedVariable(this);
  }

  @Override
  public void clearEvalFlags(final int flags) {
    fEvalFlags &= (0xffffffff ^ flags);
  }

  /** {@inheritDoc} */
  @Override
  public void clearValue() {
    fValue = null;
    clearEvalFlags(DIRTY_FLAG_ASSIGNED_VALUE);
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof ISymbol) {
      // O-2
      if (this == expr) {
        // Symbols are unique objects
        // Makes no sense to compare the symbol names, if they are equal
        return 0;
      }
      // sort lexicographically
      return StringX.US_COLLATOR.compare(fSymbolName, ((ISymbol) expr).getSymbolName());
    }
    if (expr.isAST()) {
      final int id = expr.headID();
      if (id == ID.DirectedInfinity && expr.isDirectedInfinity()) {
        return -1;
      }
      if (id >= ID.Not && id <= ID.Power) {
        if (expr.isNot() && expr.first().isSymbol()) {
          final int cp = compareTo(expr.first());
          return cp != 0 ? cp : -1;
        } else if (expr.isPower()) {
          // O-4
          int baseCompare = this.compareTo(expr.base());
          if (baseCompare == 0) {
            return F.C1.compareTo(expr.exponent());
          }
          return baseCompare;
        }
      }
      // if (!expr.isDirectedInfinity()) {
      return -1 * expr.compareTo(this);
      // }
      // return -1;
    }

    int x = hierarchy();
    int y = expr.hierarchy();
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  /** {@inheritDoc} */
  @Override
  public boolean containsRules() {
    return fRulesData != null;
  }

  @Override
  public IExpr copy() {
    try {
      return (IExpr) clone();
    } catch (CloneNotSupportedException e) {
      LOGGER.error("Symbol.copy() failed", e);
      return null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final RulesData createRulesData(int[] sizes) {
    if (fRulesData == null) {
      fRulesData = new RulesData(sizes);
    }
    return fRulesData;
  }

  /** {@inheritDoc} */
  @Override
  public IAST definition() {
    List<IAST> rules = null;
    if (fRulesData != null) {
      rules = fRulesData.definition();
    }
    IASTAppendable result = F.ListAlloc(rules == null ? 1 : rules.size());
    if (hasAssignedSymbolValue()) {
      if (isEvalFlagOn(SETDELAYED_FLAG_ASSIGNED_VALUE)) {
        result.append(F.SetDelayed(this, assignedValue()));
      } else {
        result.append(F.Set(this, assignedValue()));
      }
    }
    if (rules != null) {
      result.appendAll(rules);
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public String definitionToString() {
    StringWriter buf = new StringWriter();
    IAST attributesList = AttributeFunctions.attributesList(this);
    if (attributesList.size() > 1) {
      buf.append("Attributes(");
      buf.append(this.toString());
      buf.append(")=");
      buf.append(attributesList.toString());
      buf.append("\n");
    }

    OutputFormFactory off = OutputFormFactory.get(EvalEngine.get().isRelaxedSyntax());
    off.setIgnoreNewLine(true);
    IAST list = definition();
    for (int i = 1; i < list.size(); i++) {
      if (!off.convert(buf, list.get(i))) {
        return "ERROR-IN-OUTPUTFORM";
      }
      if (i < list.size() - 1) {
        buf.append("\n");
        off.setColumnCounter(0);
      }
    }
    return buf.toString();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr divide(IExpr that) {
    IExpr inverse = that.inverse();
    if (inverse.isOne()) {
      return this;
    }
    if (inverse.isMinusOne()) {
      return negate();
    }
    if (hasNoValue() && !(this == that) && !that.isPlusTimesPower()) {
      return F.Times(this, inverse);
    }
    return ISymbol.super.times(inverse);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj) {
    return this == obj;
  }

  /** {@inheritDoc} */
  //  @Override
  //  public final Complex evalComplex() {
  //    INumber number = evalNumber();
  //    if (number != null) {
  //      return number.complexNumValue().complexValue();
  //    }
  //    throw new ArgumentTypeException("conversion into a complex numeric value is not possible!");
  //  }

  /** {@inheritDoc} */
  @Override
  public final IExpr evalDownRule(final EvalEngine engine, final IExpr expression) {
    if (fRulesData == null) {
      return F.NIL;
    }
    return fRulesData.evalDownRule(expression, engine);
  }

  @Override
  public IExpr evalMessage(String messageName) {
    if (fRulesData != null) {
      IExpr temp = fRulesData.getMessages().get(messageName);
      if (temp != null) {
        return temp;
      }
    }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public final INumber evalNumber() {
    if (isNumericFunction(true)) {
      IExpr result = F.evaln(this);
      if (result.isNumber()) {
        return (INumber) result;
      }
    } else if (hasAssignedSymbolValue()) {
      IExpr temp = assignedValue();
      if (temp != null && temp.isNumericFunction(true)) {
        IExpr result = F.evaln(this);
        if (result.isNumber()) {
          return (INumber) result;
        }
      }
      // } else {
      // IExpr temp = evalDownRule(EvalEngine.get(), this);
      // if (temp.isPresent() && temp.isNumericFunction()) {
      // IExpr result = F.evaln(this);
      // if (result.isNumber()) {
      // return (INumber) result;
      // }
      // }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public final ISignedNumber evalReal() {
    if (isNumericFunction(true)) {
      IExpr result = F.evaln(this);
      if (result.isReal()) {
        return (ISignedNumber) result;
      }
    } else if (hasAssignedSymbolValue()) {
      IExpr temp = assignedValue();
      if (temp != null && temp.isNumericFunction(true)) {
        IExpr result = F.evaln(this);
        if (result.isReal()) {
          return (ISignedNumber) result;
        }
      }
      // } else {
      // IExpr temp = evalDownRule(EvalEngine.get(), this);
      // if (temp.isPresent() && temp.isNumericFunction()) {
      // IExpr result = F.evaln(this);
      // if (result.isReal()) {
      // return (ISignedNumber) result;
      // }
      // }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    if (hasAssignedSymbolValue()) {
      return assignedValue();
    }
    // if (hasLocalVariableStack()) {
    // return ExprUtil.ofNullable(get());
    // }
    return evalDownRule(engine, this);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluateHead(IAST ast, EvalEngine engine) {
    IExpr result = evaluate(engine);
    // set the new evaluated header !
    return result.isPresent() ? ast.apply(result) : F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr evalUpRules(final IExpr expression, final EvalEngine engine) {
    if (fRulesData == null) {
      return F.NIL;
    }
    return fRulesData.evalUpRule(expression, engine);
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    try {
      StringBuilder sb = new StringBuilder();
      OutputFormFactory.get(EvalEngine.get().isRelaxedSyntax()).convertSymbol(sb, this);
      return sb.toString();
    } catch (Exception e1) {
      return fSymbolName;
    }
    //    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
    //      String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName);
    //      if (str != null) {
    //        return str;
    //      }
    //    }
    //    return fSymbolName;
  }

  /** {@inheritDoc} */
  @Override
  public final int getAttributes() {
    return fAttributes;
  }

  /** {@inheritDoc} */
  @Override
  public final Context getContext() {
    return fContext;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr getDefaultValue() {
    // special case for a general default value
    IExpr value =
        fRulesData != null ? fRulesData.getDefaultValue(RulesData.DEFAULT_VALUE_INDEX) : null;
    return value == null ? F.NIL : value;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr getDefaultValue(int pos) {
    // default value at this position
    IExpr value = fRulesData != null ? fRulesData.getDefaultValue(pos) : null;
    return value == null ? F.NIL : value;
  }

  /**
   * Get the rules for initializing the pattern matching rules of this symbol.
   *
   * @return <code>null</code> if no rule is defined
   */
  @Override
  public final RulesData getRulesData() {
    return fRulesData;
  }

  /** {@inheritDoc} */
  @Override
  public final String getSymbolName() {
    return fSymbolName;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasAssignedSymbolValue() {
    return fValue != null;
  }

  private boolean hasNoValue() {
    return fValue == null && fRulesData == null;
  }

  @Override
  public final boolean hasFlatAttribute() {
    return ISymbol.hasFlatAttribute(fAttributes);
  }

  @Override
  public final boolean hasHoldAllCompleteAttribute() {
    return ISymbol.hasHoldAllCompleteAttribute(fAttributes);
  }

  @Override
  public final boolean hasListableAttribute() {
    return ISymbol.hasListableAttribute(fAttributes);
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return (fSymbolName == null) ? 31 : fSymbolName.hashCode();
  }

  @Override
  public boolean hasOneIdentityAttribute() {
    return (fAttributes & ONEIDENTITY) == ONEIDENTITY;
  }

  @Override
  public final boolean hasOrderlessAttribute() {
    return ISymbol.hasOrderlessAttribute(fAttributes);
  }

  @Override
  public final boolean hasOrderlessFlatAttribute() {
    return ISymbol.hasOrderlessFlatAttribute(fAttributes);
  }

  /** {@inheritDoc} */
  @Override
  public final ISymbol head() {
    return S.Symbol;
  }

  /** {@inheritDoc} */
  @Override
  public final int hierarchy() {
    return SYMBOLID;
  }

  /** {@inheritDoc} */
  @Override
  public CharSequence internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = AbstractAST.stringFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  /** {@inheritDoc} */
  @Override
  public CharSequence internalJavaString(
      SourceCodeProperties properties,
      int depth,
      Function<IExpr, ? extends CharSequence> variables) {
    CharSequence result = variables.apply(this);
    if (result != null) {
      return result;
    }
    String prefix = AbstractAST.getPrefixF(properties);
    if (properties.symbolsAsFactoryMethod) {
      return new StringBuilder(prefix).append(internalJavaStringAsFactoryMethod());
    }
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      String name;
      if (fSymbolName.length() == 1) {
        name = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName);
      } else {
        name = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName.toLowerCase(Locale.ENGLISH));
      }
      if (name != null) {
        return new StringBuilder(prefix).append(name);
      }
    } else {
      String name = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName.toLowerCase(Locale.ENGLISH));
      if (name != null && name.equals(fSymbolName)) {
        return new StringBuilder(prefix).append(name);
      }
    }
    char ch = fSymbolName.charAt(0);
    if (!properties.noSymbolPrefix && fSymbolName.length() == 1 && ('a' <= ch && ch <= 'z')) {
      return new StringBuilder(prefix).append(fSymbolName);
    } else {
      return fSymbolName;
    }
  }

  /**
   * Used to generate special Symja Java code
   *
   * @return
   */
  protected CharSequence internalJavaStringAsFactoryMethod() {
    if (fSymbolName.length() == 1) {
      char ch = fSymbolName.charAt(0);
      if ('a' <= ch && ch <= 'z') {
        return fSymbolName;
      }
      if ((Config.RUBI_CONVERT_SYMBOLS && 'A' <= ch && ch <= 'G' && ch != 'D' && ch != 'E')
          || ('A' <= ch && ch <= 'G' && ch != 'D' && ch != 'E')
          || ('P' == ch || ch == 'Q')) {
        return new StringBuilder(fSymbolName).append("Symbol");
      }
    }
    if (Config.RUBI_CONVERT_SYMBOLS) {
      if (fSymbolName.length() == 2
          && '§' == fSymbolName.charAt(0)
          && Character.isLowerCase(fSymbolName.charAt(1))) {
        char ch = fSymbolName.charAt(1);
        if ('a' <= ch && ch <= 'z') {
          return new StringBuilder("p").append(ch);
        }
      } else if (fSymbolName.equals("Int")) {
        return "Integrate";
      }
    }
    if (Character.isUpperCase(fSymbolName.charAt(0))) {
      String alias = F.getPredefinedInternalFormString(fSymbolName);
      if (alias != null) {
        if (Config.RUBI_CONVERT_SYMBOLS && alias.startsWith("Rubi`")) {
          return new StringBuilder("$rubi(\"").append(alias, 5, alias.length()).append("\")");
        }
        return alias;
      }
    }
    return new StringBuilder("$s(\"").append(fSymbolName).append("\")");
  }

  /** {@inheritDoc} */
  @Override
  public CharSequence internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = AbstractAST.scalaFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isAtom() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isEvalFlagOff(final int i) {
    return (fEvalFlags & i) == 0;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isEvalFlagOn(final int i) {
    return (fEvalFlags & i) == i;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isLocked() {
    return !EvalEngine.get().isPackageMode()
        && (fContext == Context.SYSTEM || fContext == Context.RUBI);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isLocked(boolean packageMode) {
    return !packageMode && (fContext == Context.SYSTEM || fContext == Context.RUBI);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    if (isNumericFunction(true)) {
      IExpr temp = F.evaln(this);
      if (temp.isReal() && temp.isNegative()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isNumericFunction(boolean allowList) {
    if (isConstantAttribute()) {
      return true;
    }
    //    if (hasAssignedSymbolValue()) {
    //      IExpr temp = assignedValue();
    //      if (temp != null) {
    //        return temp != this && temp.isNumericFunction(true);
    //      }
    //    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPolynomial(IAST variables) {
    if (variables.isAST0()) {
      return true;
    }
    // if (isConstant()) {
    // return true;
    // }
    ExprPolynomialRing ring = new ExprPolynomialRing(variables);
    return ring.isPolynomial(this);
    // return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPolynomial(IExpr variable) {
    return isPolynomial(F.List(variable));
    // if (variable == null) {
    // return true;
    // }
    // return this.equals(variable);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPolynomialOfMaxDegree(ISymbol variable, long maxDegree) {
    if (maxDegree == 0L) {
      if (this.equals(variable)) {
        return false;
      }
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPolynomialStruct() {
    return ((fAttributes & CONSTANT) == CONSTANT) || isVariable();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPositive() {
    if (isNumericFunction(true)) {
      IExpr temp = F.evaln(this);
      if (temp.isReal() && temp.isPositive()) {
        return true;
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isString(final String str) {
    return fSymbolName.equals(str);
  }

  @Override
  public final boolean isStringIgnoreCase(final String str) {
    return fSymbolName.equalsIgnoreCase(str);
  }

  @Override
  public final boolean isSymbolName(String name) {
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      if (fSymbolName.length() == 1) {
        return fSymbolName.equals(name);
      }
      return fSymbolName.equalsIgnoreCase(name);
    }
    return fSymbolName.equals(name);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isValue() {
    return evaluate(EvalEngine.get()).isPresent();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isVariable() {
    return ((fAttributes & (CONSTANT | NUMERICFUNCTION)) == NOATTRIBUTE)
        && (this != S.ComplexInfinity)
        && (this != S.Indeterminate)
        && (this != S.DirectedInfinity)
        && (this != S.Infinity);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr of(EvalEngine engine, IExpr... args) {
    IAST ast = F.function(this, args);
    return engine.evaluate(ast);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr of1(EvalEngine engine, IExpr arg, IExpr... parts) {
    IASTAppendable ast = F.ast(this, 1 + parts.length);
    ast.append(arg);
    ast.appendAll(parts, 0, parts.length);
    return engine.evaluate(ast);
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr ofNIL(EvalEngine engine, IExpr... args) {
    IAST ast = F.function(this, args);
    IExpr temp = engine.evaluateNIL(ast);
    if (temp.isPresent() && temp.head() == this) {
      return F.NIL;
    }
    return temp;
  }

  /** {@inheritDoc} */
  @Override
  public boolean ofQ(EvalEngine engine, IExpr... args) {
    IAST ast = F.function(this, args);
    return engine.evalTrue(ast);
  }

  @Override
  public final boolean ofQ(IExpr... args) {
    return ofQ(EvalEngine.get(), args);
  }

  @Override
  public IExpr inverse() {
    if (hasNoValue()) {
      return F.Power(this, F.CN1);
    }
    return power(F.CN1);
  }

  @Override
  public IExpr opposite() {
    if (hasNoValue()) {
      return F.Times(F.CN1, this);
    }
    return times(F.CN1);
  }

  @Override
  public IExpr plus(final IExpr that) {
    if (hasNoValue() && this != that && !that.isPlusTimesPower()) {
      if (that.isZero()) {
        return this;
      }
      return F.Plus(this, that);
    }
    return ISymbol.super.plus(that);
  }

  /** {@inheritDoc} */
  @Override
  public final void putDownRule(
      final int setSymbol,
      final boolean equalRule,
      final IExpr leftHandSide,
      final IExpr rightHandSide,
      boolean packageMode) {
    putDownRule(
        setSymbol,
        equalRule,
        leftHandSide,
        rightHandSide,
        IPatternMap.DEFAULT_RULE_PRIORITY,
        packageMode);
  }

  /** {@inheritDoc} */
  @Override
  public final void putDownRule(
      final int setSymbol,
      final boolean equalRule,
      final IExpr leftHandSide,
      final IExpr rightHandSide,
      final int priority,
      boolean packageMode) {
    if (!packageMode) {
      if (isLocked(packageMode)) {
        throw new RuleCreationError(leftHandSide);
      }
      EvalEngine.get().addModifiedVariable(this);
    }
    if (leftHandSide.isSymbol()) {
      assignValue(rightHandSide, false);
      return;
    }
    if (fRulesData == null) {
      fRulesData = new RulesData();
    }
    fRulesData.putDownRule(setSymbol, equalRule, leftHandSide, rightHandSide, priority);
  }

  /** {@inheritDoc} */
  @Override
  public final void putDownRule(final PatternMatcherAndInvoker pmEvaluator) {
    if (fRulesData == null) {
      fRulesData = new RulesData();
    }
    fRulesData.insertMatcher(pmEvaluator);
  }

  @Override
  public void putMessage(final int setSymbol, String messageName, IStringX message) {
    if (fRulesData == null) {
      fRulesData = new RulesData();
    }
    fRulesData.getMessages().put(messageName, message);
  }

  /** {@inheritDoc} */
  @Override
  public final IPatternMatcher putUpRule(
      final int setSymbol, boolean equalRule, IAST leftHandSide, IExpr rightHandSide) {
    return putUpRule(
        setSymbol, equalRule, leftHandSide, rightHandSide, IPatternMap.DEFAULT_RULE_PRIORITY);
  }

  /** {@inheritDoc} */
  @Override
  public final IPatternMatcher putUpRule(
      final int setSymbol,
      final boolean equalRule,
      final IAST leftHandSide,
      final IExpr rightHandSide,
      final int priority) {
    EvalEngine engine = EvalEngine.get();
    if (!engine.isPackageMode()) {
      if (isLocked(false)) {
        throw new RuleCreationError(leftHandSide);
      }

      engine.addModifiedVariable(this);
    }
    if (fRulesData == null) {
      fRulesData = new RulesData();
    }
    return fRulesData.putUpRule(setSymbol, equalRule, leftHandSide, rightHandSide);
  }

  private void readObject(java.io.ObjectInputStream stream)
      throws IOException, ClassNotFoundException {
    fSymbolName = stream.readUTF();
    fAttributes = stream.read();
    IExpr value = (IExpr) stream.readObject();
    assignValue(value, false);
    int contextNumber = stream.readInt();
    switch (contextNumber) {
      case 1:
        fContext = Context.SYSTEM;
        break;
      case 2:
        fContext = Context.RUBI;
        break;
      case 3:
        fContext = Context.DUMMY;
        break;
      default:
        String contextName = stream.readUTF();
        fContext = EvalEngine.get().getContextPath().getContext(contextName);
        Symbol symbol = (Symbol) fContext.get(fSymbolName);
        if (symbol == null) {
          fContext.put(fSymbolName, this);
          symbol = this;
        } else {
          symbol.fAttributes = fAttributes;
          symbol.fValue = fValue;
          symbol.clearEvalFlags(DIRTY_FLAG_ASSIGNED_VALUE);
        }
        boolean hasDownRulesData = stream.readBoolean();
        if (hasDownRulesData) {
          symbol.fRulesData = (RulesData) stream.readObject();
        }
    }
  }

  public Object readResolve() {
    return fContext == Context.DUMMY ? this : fContext.get(fSymbolName);
  }

  /** {@inheritDoc} */
  @Override
  public void readRules(java.io.ObjectInputStream stream)
      throws IOException, ClassNotFoundException {
    fSymbolName = stream.readUTF();
    fAttributes = stream.read();
    boolean hasDownRulesData = stream.readBoolean();
    if (hasDownRulesData) {
      fRulesData = new RulesData();
      fRulesData = (RulesData) stream.readObject();
    }
  }

  /** {@inheritDoc} */
  @Override
  public IExpr[] reassignSymbolValue(
      Function<IExpr, IExpr> function, ISymbol functionSymbol, EvalEngine engine) {
    if (hasAssignedSymbolValue()) {
      IExpr[] result = new IExpr[2];
      result[0] = fValue;
      if (isEvalFlagOn(DIRTY_FLAG_ASSIGNED_VALUE) && result[0].isAST()) {
        result[0] = ((IAST) result[0]).copy();
      }
      IExpr calculatedResult = function.apply(result[0]);
      if (calculatedResult.isPresent()) {
        assignValue(calculatedResult, false);
        result[1] = calculatedResult;
        return result;
      }
    }
    // `1` is not a variable with a value, so its value cannot be changed.
    IOFunctions.printMessage(functionSymbol, "rvalue", F.List(this), engine);
    //    engine.printMessage(
    //        functionSymbol.toString()
    //            + ": "
    //            + toString()
    //            + " is not a variable with a value, so its value cannot be changed.");
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr[] reassignSymbolValue(IASTMutable ast, ISymbol functionSymbol, EvalEngine engine) {
    if (hasAssignedSymbolValue()) {
      IExpr[] result = new IExpr[2];
      result[0] = fValue;
      // if (fReferences > 0 && result[0].isAST()) {
      // result[0] = ((IAST) result[0]).copy();
      // }
      ast.set(1, result[0]);
      IExpr calculatedResult = engine.evaluate(ast); // F.binaryAST2(this, symbolValue, value));
      if (calculatedResult != null) {
        assignValue(calculatedResult, false);
        result[1] = calculatedResult;
        return result;
      }
    }
    throw new ArgumentTypeException(
        functionSymbol.toString()
            + " - Symbol: "
            + toString()
            + " has no value! Reassignment with a new value is not possible");
  }

  /** {@inheritDoc} */
  @Override
  public final boolean removeRule(
      final int setSymbol, final boolean equalRule, final IExpr leftHandSide, boolean packageMode) {
    if (!packageMode) {
      if (isLocked(packageMode)) {
        throw new RuleCreationError(leftHandSide);
      }

      EvalEngine.get().addModifiedVariable(this);
    }
    if (leftHandSide.isSymbol()) {
      clearValue();
      return true;
    } else if (fRulesData != null) {
      return fRulesData.removeRule(setSymbol, equalRule, leftHandSide);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public void setAttributes(final int attributes) {
    fAttributes = attributes;
    if (isLocked()) {
      throw new RuleCreationError(this);
    }
    EvalEngine engine = EvalEngine.get();
    engine.addModifiedVariable(this);
  }

  /** {@inheritDoc} */
  @Override
  public void setDefaultValue(IExpr expr) {
    // special case for a general default value
    setDefaultValue(RulesData.DEFAULT_VALUE_INDEX, expr);
  }

  /** {@inheritDoc} */
  @Override
  public void setDefaultValue(int pos, IExpr expr) {
    // default value at this position
    if (fRulesData == null) {
      fRulesData = new RulesData();
    }
    fRulesData.putfDefaultValues(pos, expr);
  }

  @Override
  public void setRulesData(RulesData rd) {
    fRulesData = rd;
  }

  @Override
  public String toString() {
    try {
      StringBuilder sb = new StringBuilder();
      OutputFormFactory.get(EvalEngine.get().isRelaxedSyntax()).convertSymbol(sb, this);
      return sb.toString();
    } catch (Exception e1) {
      return fSymbolName;
    }
  }

  @Override
  public IExpr times(final IExpr that) {
    if (hasNoValue() && !(this == that) && !that.isPlusTimesPower()) {
      if (that.isZero()) {
        return F.C0;
      }
      if (that.isOne()) {
        return this;
      }
      return F.Times(this, that);
    }
    return ISymbol.super.times(that);
  }

  @Override
  public String toMMA() {
    String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName);
    if (str != null) {
      return str;
    }
    return fSymbolName;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr variables2Slots(
      final Map<IExpr, IExpr> map, final Collection<IExpr> variableCollector) {
    final UnaryVariable2Slot uv2s = new UnaryVariable2Slot(map, variableCollector);
    return uv2s.apply(this);
  }

  private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
    stream.writeUTF(fSymbolName);
    stream.write(fAttributes);
    stream.writeObject(fValue);
    if (fContext.equals(Context.SYSTEM)) {
      stream.writeInt(1);
    } else if (fContext.equals(Context.RUBI)) {
      stream.writeInt(2);
    } else if (fContext.equals(Context.DUMMY)) {
      stream.writeInt(3);
    } else {
      stream.writeInt(0);
      stream.writeUTF(fContext.getContextName());
      if (fRulesData == null) {
        stream.writeBoolean(false);
      } else {
        stream.writeBoolean(true);
        stream.writeObject(fRulesData);
      }
    }
  }

  private Object writeReplace() {
    return optional();
  }

  /** {@inheritDoc} */
  @Override
  public boolean writeRules(java.io.ObjectOutputStream stream) throws java.io.IOException {
    stream.writeUTF(fSymbolName);
    stream.write(fAttributes);
    // if (!containsRules()) {
    // return false;
    // }
    if (fRulesData == null) {
      stream.writeBoolean(false);
    } else {
      stream.writeBoolean(true);
      stream.writeObject(fRulesData);
    }
    return true;
  }
}
