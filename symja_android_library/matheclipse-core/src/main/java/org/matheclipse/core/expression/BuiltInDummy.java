package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.Collator;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.AttributeFunctions;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.generic.UnaryVariable2Slot;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherAndInvoker;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.parser.client.ParserConfig;

public class BuiltInDummy implements IBuiltInSymbol, Serializable {
  private static final long serialVersionUID = -1921824292485125087L;

  private static final Logger LOGGER = LogManager.getLogger();

  private static final Collator US_COLLATOR = Collator.getInstance(Locale.US);

  /** The attribute values of the symbol represented by single bits. */
  protected int fAttributes = NOATTRIBUTE;

  /** Flags for controlling evaluation and left-hand-side pattern-matching expressions */
  protected int fEvalFlags = 0;

  /** The pattern matching &quot;down value&quot; rules associated with this symbol. */
  protected transient RulesData fRulesData;

  /**
   * The name of this symbol. The characters may be all lower-cases if the system doesn't
   * distinguish between lower- and upper-case function names.
   */
  protected String fSymbolName;

  private IExpr fValue = null;


  /** constructor for serialization */
  protected BuiltInDummy() {}

  /**
   * The evaluation class of this built-in-function. See packages: package <code>
   * org.matheclipse.core.builtin.function</code> and <code>org.matheclipse.core.reflection.system
   * </code>.
   */
  private transient IEvaluator fEvaluator;

  public BuiltInDummy(final String symbolName) {
    super();
    // fContext = context;
    fSymbolName = symbolName;
  }


  public BuiltInDummy(final String symbolName, IEvaluator evaluator) {
    super();
    // fContext = context;
    fSymbolName = symbolName;
    fEvaluator = evaluator;
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
  public final ISymbol addEvalFlags(final int i) {
    fEvalFlags |= i;
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr apply(IExpr... expressions) {
    return F.ast(expressions, this);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr assignedValue() {
    Context globalContext = EvalEngine.get().getContextPath().getGlobalContext();
    ISymbol globalSubstitute = globalContext.get(fSymbolName);
    if (globalSubstitute != null) {
      return globalSubstitute.assignedValue();
    }
    addEvalFlags(DIRTY_FLAG_ASSIGNED_VALUE);
    return fValue;
  }

  /** {@inheritDoc} */
  @Override
  public final void assignValue(final IExpr value, boolean setDelayed) {
    Context globalContext = EvalEngine.get().getContextPath().getGlobalContext();
    ISymbol globalSubstitute = globalContext.get(fSymbolName);
    if (globalSubstitute != null) {
      globalSubstitute.assignValue(value, false);
      return;
    }
    fValue = value;
    clearEvalFlags(DIRTY_FLAG_ASSIGNED_VALUE);
    if (setDelayed) {
      addEvalFlags(SETDELAYED_FLAG_ASSIGNED_VALUE);
    } else {
      clearEvalFlags(SETDELAYED_FLAG_ASSIGNED_VALUE);
    }
    // final Deque<IExpr> localVariableStack = EvalEngine.get().localStack(this);
    // localVariableStack.remove();
    // localVariableStack.push(value);
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
      fRulesData = null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void clearAll(EvalEngine engine) {
    clear(engine);
    fAttributes = NOATTRIBUTE;
  }

  /** {@inheritDoc} */
  @Override
  public final void clearAttributes(final int attributes) {
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
      return US_COLLATOR.compare(fSymbolName, ((ISymbol) expr).getSymbolName()); // fSymbolName);
    }
    if (expr.isNot() && expr.first().isSymbol()) {
      int cp = compareTo(expr.first());
      return cp != 0 ? cp : -1;
    }
    return IBuiltInSymbol.super.compareTo(expr);
  }

  /** {@inheritDoc} */
  @Override
  public boolean containsRules() {
    return fRulesData != null;
  }

  @Override
  public IExpr copy() {
    return this;
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
    IASTAppendable result = F.ListAlloc();
    if (hasAssignedSymbolValue()) {
      if (isEvalFlagOn(SETDELAYED_FLAG_ASSIGNED_VALUE)) {
        result.append(F.SetDelayed(this, assignedValue()));
      } else {
        result.append(F.Set(this, assignedValue()));
      }
    }
    if (fRulesData != null) {
      result.appendAll(fRulesData.definition());
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
  public boolean equals(final Object obj) {
    if (Config.FUZZ_TESTING) {
      if (obj instanceof ISymbol) {
        if (fSymbolName.equals(((ISymbol) obj).getSymbolName())
            && getContext().equals(((ISymbol) obj).getContext())) {
          if (this != obj) {
            throw new NullPointerException();
          }
        }
      }
    }
    if (this == obj) {
      return true;
    }
    if (obj instanceof BuiltInDummy) {
      // BuiltInDummy symbol = (BuiltInDummy) obj;
      // if (hashCode() != symbol.hashCode()) {
      // return false;
      // }
      return fSymbolName.equals(((BuiltInDummy) obj).fSymbolName);
    }
    return false;
  }

  /** {@inheritDoc} */
  // @Override
  // public final Complex evalComplex() {
  // INumber number = evalNumber();
  // if (number != null) {
  // return number.complexNumValue().complexValue();
  // }
  // throw new ArgumentTypeException("conversion into a complex numeric value is not possible!");
  // }

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
      // } else if (fValue != null) {
    } else {
      IExpr temp = assignedValue();
      if (temp != null && temp.isNumericFunction(true)) {
        IExpr result = F.evaln(this);
        if (result.isNumber()) {
          return (INumber) result;
        }
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public final IReal evalReal() {
    if (isNumericFunction(true)) {
      IExpr result = F.evaln(this);
      if (result.isReal()) {
        return (IReal) result;
      }
      // } else if (fValue != null) {
    } else {
      IExpr temp = assignedValue();
      if (temp != null && temp.isNumericFunction(true)) {
        IExpr result = F.evaln(this);
        if (result.isReal()) {
          return (IReal) result;
        }
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    Context globalContext = engine.getContextPath().getGlobalContext();
    ISymbol globalSubstitute = globalContext.get(fSymbolName);
    if (globalSubstitute != null) {
      return globalSubstitute.evaluate(engine);
    }

    if (hasAssignedSymbolValue()) {
      return IExpr.ofNullable(assignedValue());
    }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluateHead(IAST ast, EvalEngine engine) {
    IExpr result = evaluate(engine);
    if (result.isPresent()) {
      // set the new evaluated header !
      return ast.apply(result);
    }
    return F.NIL;
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
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName);
      if (str != null) {
        return str;
      }
    }
    return fSymbolName;
  }

  /** {@inheritDoc} */
  @Override
  public final int getAttributes() {
    return fAttributes;
  }

  /** {@inheritDoc} */
  @Override
  public Context getContext() {
    return Context.DUMMY;
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

  @Override
  public IEvaluator getEvaluator() {
    return fEvaluator;
  }

  /**
   * Get the rules for initializing the pattern matching rules of this symbol.
   *
   * @return <code>null</code> if no rule is defined
   */
  @Override
  public RulesData getRulesData() {
    return fRulesData;
  }

  /** {@inheritDoc} */
  @Override
  public final String getSymbolName() {
    return fSymbolName;
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasAssignedSymbolValue() {
    IExpr temp = assignedValue();
    return temp != null;
  }

  @Override
  public boolean hasFlatAttribute() {
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
  public boolean hasOrderlessAttribute() {
    return (fAttributes & ORDERLESS) == ORDERLESS;
  }

  @Override
  public boolean hasOrderlessFlatAttribute() {
    return (fAttributes & FLATORDERLESS) == FLATORDERLESS;
  }

  /** {@inheritDoc} */
  @Override
  public ISymbol head() {
    return S.Symbol;
  }

  /** {@inheritDoc} */
  @Override
  public int hierarchy() {
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
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
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
  private String internalJavaStringAsFactoryMethod() {
    if (fSymbolName.length() == 1) {
      char ch = fSymbolName.charAt(0);
      if ('a' <= ch && ch <= 'z') {
        return fSymbolName;
      }
      if ('A' <= ch && ch <= 'G' && ch != 'D' && ch != 'E') {
        return fSymbolName + "Symbol";
      }
    }
    if (Config.RUBI_CONVERT_SYMBOLS) {
      if (fSymbolName.length() == 2 && '§' == fSymbolName.charAt(0)
          && Character.isLowerCase(fSymbolName.charAt(1))) {
        char ch = fSymbolName.charAt(1);
        if ('a' <= ch && ch <= 'z') {
          return "p" + ch;
        }
      } else if (fSymbolName.equals("Int")) {
        return "Integrate";
      }
    }
    if (Character.isUpperCase(fSymbolName.charAt(0))) {
      String alias = F.getPredefinedInternalFormString(fSymbolName);
      if (alias != null) {
        if (Config.RUBI_CONVERT_SYMBOLS) {
          if (alias.startsWith("Rubi`")) {
            return "$rubi(\"" + alias.substring(5) + "\")";
          }
        }
        return alias;
      }
    }
    return "$s(\"" + fSymbolName + "\")";
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
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isLocked(boolean packageMode) {
    return false;
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

    IExpr temp = assignedValue();
    if (temp != null && temp.isNumericFunction(true)) {
      return true;
    }
    // } else {
    // IExpr temp = evalDownRule(EvalEngine.get(), this);
    // if (temp.isPresent() && temp.isNumericFunction()) {
    // return true;
    // }

    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPolynomial(IAST variables) {
    if (variables.isAST0()) {
      return true;
    }
    if (isConstantAttribute()) {
      return true;
    }
    return variables.exists(x -> this.equals(x));
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPolynomial(IExpr variable) {
    if (variable == null) {
      return true;
    }
    return this.equals(variable);
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
    return isVariable();
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
  public boolean isProtected() {
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
  public boolean isVariable() {
    return !isConstantAttribute();
  }

  @Override
  public ISymbol mapToGlobal(EvalEngine engine) {
    Context globalContext = engine.getContextPath().getGlobalContext();
    ISymbol globalSubstitute = globalContext.get(fSymbolName);
    if (globalSubstitute != null) {
      globalSubstitute.setAttributes(fAttributes);
      globalSubstitute.assignValue(assignedValue(), false);
      return globalSubstitute;
    }
    globalSubstitute = new Symbol(fSymbolName, globalContext);
    globalContext.put(fSymbolName, globalSubstitute);
    globalSubstitute.setAttributes(fAttributes);
    globalSubstitute.assignValue(assignedValue(), false);
    return globalSubstitute;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr of(EvalEngine engine, IExpr... args) {
    return engine.evaluate(F.ast(args, this));
  }

  /** {@inheritDoc} */
  @Override
  public IExpr of(EvalEngine engine, Object... args) {
    IExpr[] convertedArgs = Object2Expr.convertArray(args, false, false);
    return engine.evaluate(F.ast(convertedArgs, this));
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
  public IExpr ofNIL(EvalEngine engine, IExpr... args) {
    IAST ast = F.ast(args, this);
    return engine.evaluateNIL(ast);
  }

  /** {@inheritDoc} */
  @Override
  public boolean ofQ(EvalEngine engine, IExpr... args) {
    IAST ast = F.ast(args, this);
    return engine.evalTrue(ast);
  }

  /** {@inheritDoc} */
  @Override
  public boolean ofQ(IExpr... args) {
    return ofQ(EvalEngine.get(), args);
  }

  /** {@inheritDoc} */
  @Override
  public final IPatternMatcher putDownRule(final int setSymbol, final boolean equalRule,
      final IExpr leftHandSide, final IExpr rightHandSide, boolean packageMode) {
    return putDownRule(setSymbol, equalRule, leftHandSide, rightHandSide,
        IPatternMap.DEFAULT_RULE_PRIORITY, packageMode);
  }

  // public Object readResolve() throws ObjectStreamException {
  // ISymbol sym = fContext.get(fSymbolName);
  // if (sym != null) {
  // return sym;
  // }
  // // probably user defined
  // Symbol symbol = new Symbol(fSymbolName, fContext);
  // fContext.put(fSymbolName, symbol);
  // symbol.fAttributes = fAttributes;
  // return symbol;
  // }

  /** {@inheritDoc} */
  @Override
  public final IPatternMatcher putDownRule(final int setSymbol, final boolean equalRule,
      final IExpr leftHandSide, final IExpr rightHandSide, final int priority,
      boolean packageMode) {
    if (!packageMode) {
      if (isLocked(packageMode)) {
        throw new RuleCreationError(leftHandSide);
      }
      EvalEngine.get().addModifiedVariable(this);
    }
    if (fRulesData == null) {
      fRulesData = new RulesData();
    }
    return fRulesData.putDownRule(setSymbol, equalRule, leftHandSide, rightHandSide, priority);
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
  public void putMessage(final int setSymbol, String messageName, IStringX message) {}

  /** {@inheritDoc} */
  @Override
  public final IPatternMatcher putUpRule(final int setSymbol, boolean equalRule, IAST leftHandSide,
      IExpr rightHandSide) {
    return putUpRule(setSymbol, equalRule, leftHandSide, rightHandSide,
        IPatternMap.DEFAULT_RULE_PRIORITY);
  }

  /** {@inheritDoc} */
  @Override
  public final IPatternMatcher putUpRule(final int setSymbol, final boolean equalRule,
      final IAST leftHandSide, final IExpr rightHandSide, final int priority) {
    throw new UnsupportedOperationException();
  }

  private void readObject(java.io.ObjectInputStream stream)
      throws IOException, ClassNotFoundException {
    fSymbolName = stream.readUTF();
    fAttributes = stream.read();
    IExpr value = (IExpr) stream.readObject();
    assignValue(value, false);
    // fContext = (Context) stream.readObject();
    // if (fContext == null) {
    // fContext = Context.SYSTEM;
    // } else {
    // boolean hasDownRulesData = stream.readBoolean();
    // if (hasDownRulesData) {
    // fRulesData = new RulesData(EvalEngine.get().getContext());
    // fRulesData = (RulesData) stream.readObject();
    // }
    // }
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
  public IExpr[] reassignSymbolValue(Function<IExpr, IExpr> function, ISymbol functionSymbol,
      EvalEngine engine) {
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
    IOFunctions.printMessage(functionSymbol, "rvalue", F.list(this), engine);
    // engine.printMessage(
    // functionSymbol.toString()
    // + ": "
    // + toString()
    // + " is not a variable with a value, so its value cannot be changed.");
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr[] reassignSymbolValue(IASTMutable ast, ISymbol functionSymbol, EvalEngine engine) {
    IExpr temp = assignedValue();
    if (temp != null) {
      IExpr[] result = new IExpr[2];
      result[0] = temp;
      // IExpr calculatedResult = function.apply(symbolValue);
      ast.set(1, temp);
      IExpr calculatedResult = engine.evaluate(ast); // F.binaryAST2(this, symbolValue, value));
      if (calculatedResult != null) {
        assignValue(calculatedResult, false);
        result[1] = calculatedResult;
        return result;
      }
    }
    throw new ArgumentTypeException(functionSymbol.toString() + " - Symbol: " + toString()
        + " has no value! Reassignment with a new value is not possible");
  }

  /** {@inheritDoc} */
  @Override
  public final boolean removeRule(final int setSymbol, final boolean equalRule,
      final IExpr leftHandSide, boolean packageMode) {
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
  public final void setAttributes(final int attributes) {
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
  public void setEvaluator(IEvaluator evaluator) {
    fEvaluator = evaluator;
  }

  @Override
  public void setPredicateQ(Predicate<IExpr> predicate) {
    throw new UnsupportedOperationException();
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

  /** {@inheritDoc} */
  @Override
  public IExpr variables2Slots(final Map<IExpr, IExpr> map,
      final Collection<IExpr> variableCollector) {
    final UnaryVariable2Slot uv2s = new UnaryVariable2Slot(map, variableCollector);
    return uv2s.apply(this);
  }

  private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
    stream.writeUTF(fSymbolName);
    stream.write(fAttributes);
    stream.writeObject(fValue);
    // if (fContext.equals(Context.SYSTEM)) {
    // stream.writeObject(null);
    // } else {
    // stream.writeObject(fContext);
    // if (fRulesData == null) {
    // stream.writeBoolean(false);
    // } else {
    // stream.writeBoolean(true);
    // stream.writeObject(fRulesData);
    // }
    // }
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
