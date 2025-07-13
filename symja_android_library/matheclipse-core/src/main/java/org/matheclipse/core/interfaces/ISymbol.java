package org.matheclipse.core.interfaces;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import org.hipparchus.special.elliptic.jacobi.Theta;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMap.PatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherAndInvoker;
import org.matheclipse.core.patternmatching.RulesData;

/** An expression representing a symbol (i.e. variable- constant- or function-name) */
public interface ISymbol extends IExpr {

  /**
   * ISymbol attribute to indicate that a symbols evaluation should be printed to Console with
   * System.out.println();
   */
  // public final static int CONSOLE_OUTPUT = 0x1000;

  /** ISymbol attribute to indicate that a symbol has a constant value */
  public static final int CONSTANT = 0x0002;

  /**
   * ISymbol attribute for an associative function transformation. The evaluation of the function
   * will flatten the arguments list
   */
  public static final int FLAT = 0x0008;

  /** ISymbol attribute for a function, where the first argument should not be evaluated */
  public static final int HOLDFIRST = 0x0020;

  /** ISymbol attribute for a function, where only the first argument should be evaluated */
  public static final int HOLDREST = 0x0040;

  /** ISymbol attribute for a function, where no argument should be evaluated */
  public static final int HOLDALL = HOLDFIRST | HOLDREST;

  /** ISymbol attribute for a function, where no argument should be evaluated */
  public static final int HOLDCOMPLETE = HOLDALL | 0x0080;

  /**
   * ISymbol attribute for a function, where no <code>Sequence()</code> argument should be flattend
   * out
   */
  public static final int SEQUENCEHOLD = 0x00040000;

  /** ISymbol attribute for a function, where no argument should be evaluated */
  public static final int HOLDALLCOMPLETE = HOLDCOMPLETE | SEQUENCEHOLD | 0x0100;

  /** ISymbol attribute for a function with lists as arguments */
  public static final int LISTABLE = 0x0200;

  /**
   * ISymbol attribute for a function, where the first argument should not be evaluated numerically
   */
  public static final int NHOLDFIRST = 0x2000;

  /**
   * ISymbol attribute for a function, where the rest of the arguments should not be evaluated
   * numerically.
   */
  public static final int NHOLDREST = 0x4000;

  /** ISymbol attribute for a function, which should not be evaluated numerically */
  public static final int NHOLDALL = NHOLDFIRST | NHOLDREST;

  /** ISymbol attribute which means that no attribute is set. */
  public static final int NOATTRIBUTE = 0x0000;

  /** ISymbol attribute for a numeric function */
  public static final int NUMERICFUNCTION = 0x0400;

  /** ISymbol flag for a symbol which has already loaded it's package definition */
  public static final int PACKAGE_LOADED = 0x0800;

  /** ISymbol attribute for a function transformation: f(x) ==> x */
  public static final int ONEIDENTITY = 0x0001;

  /**
   * ISymbol attribute for a commutative function transformation. The evaluation of the function
   * will sort the arguments.
   */
  public static final int ORDERLESS = 0x0004;

  /** ISymbol attribute combination (ISymbol.FLAT and ISymbol.ORDERLESS) */
  public static final int FLATORDERLESS = FLAT | ORDERLESS;

  /** ISymbol attribute for a symbol where no rule definition should be possible */
  public static final int PROTECTED = 0x8000;


  /** ISymbol attribute for a symbol where the definition shouldn't be displayed */
  public static final int READPROTECTED = 0x10000;

  /**
   * ISymbol attribute for a locked symbol. If a symbol is once locked, you cannot reset the lock.
   */
  public static final int LOCKED = PROTECTED | 0x20000;

  /**
   * A mask which tests if any evaluation engine attribute is sets
   */
  public static final int NO_EVAL_ENGINE_ATTRIBUTE = 0xFFF877FF;
  //
  // Flags definition starts here
  //

  /** ISymbol flag to indicate that the symbols value is used by a method */
  public static final int DIRTY_FLAG_ASSIGNED_VALUE = 0x00000001;

  /** ISymbol flag to indicate that the symbols value is defined by SetDelayed */
  public static final int SETDELAYED_FLAG_ASSIGNED_VALUE = 0x10000002;

  /**
   * Get the attrbutes of this <code>symbol</code> as symbolic constants in a list.
   *
   * @param symbol
   * @return
   */
  static IAST attributesList(ISymbol symbol) {

    int attributes = symbol.getAttributes();
    IASTAppendable result = F.ListAlloc(Integer.bitCount(attributes));

    if ((attributes & CONSTANT) != NOATTRIBUTE) {
      result.append(S.Constant);
    }

    if ((attributes & FLAT) != NOATTRIBUTE) {
      result.append(S.Flat);
    }

    if ((attributes & HOLDALLCOMPLETE) == HOLDALLCOMPLETE) {
      result.append(S.HoldAllComplete);
    } else if ((attributes & HOLDCOMPLETE) == HOLDCOMPLETE) {
      result.append(S.HoldComplete);
    } else if ((attributes & HOLDALL) == HOLDALL) {
      result.append(S.HoldAll);
    } else {
      if ((attributes & HOLDFIRST) != NOATTRIBUTE) {
        result.append(S.HoldFirst);
      }

      if ((attributes & HOLDREST) != NOATTRIBUTE) {
        result.append(S.HoldRest);
      }
    }

    if ((attributes & LISTABLE) != NOATTRIBUTE) {
      result.append(S.Listable);
    }

    if ((attributes & NHOLDALL) == NHOLDALL) {
      result.append(S.NHoldAll);
    } else {
      if ((attributes & NHOLDFIRST) != NOATTRIBUTE) {
        result.append(S.NHoldFirst);
      }

      if ((attributes & NHOLDREST) != NOATTRIBUTE) {
        result.append(S.NHoldRest);
      }
    }

    if ((attributes & NUMERICFUNCTION) != NOATTRIBUTE) {
      result.append(S.NumericFunction);
    }

    if ((attributes & ONEIDENTITY) != NOATTRIBUTE) {
      result.append(S.OneIdentity);
    }

    if ((attributes & ORDERLESS) != NOATTRIBUTE) {
      result.append(S.Orderless);
    }

    if ((attributes & LOCKED) == LOCKED) {
      result.append(S.Locked);
      result.append(S.Protected);
    } else {
      if ((attributes & PROTECTED) != NOATTRIBUTE) {
        result.append(S.Protected);
      }
    }

    if ((attributes & SEQUENCEHOLD) == SEQUENCEHOLD
        && ((attributes & HOLDALLCOMPLETE) != HOLDALLCOMPLETE)) {
      result.append(S.SequenceHold);
    }
    result.sortInplace(Comparators.CANONICAL_COMPARATOR);
    return result;
  }

  private static void collectSymbolsRecursive(IAST symbolsList, Set<ISymbol> symbolSet,
      Predicate<ISymbol> predicate) {
    for (int i = 1; i < symbolsList.size(); i++) {
      ISymbol symbol = (ISymbol) symbolsList.get(i);
      if (predicate.test(symbol)) {
        final IAST rules = symbol.definition();
        for (int j = 1; j < rules.size(); j++) {
          IExpr rule = rules.get(j);
          collectSymbolsRecursive(rule, symbolSet, predicate);
        }
      }
    }
  }

  private static void collectSymbolsRecursive(IExpr expr, Set<ISymbol> symbolSet,
      Predicate<ISymbol> predicate) {
    if (expr.isAST()) {
      IAST list = (IAST) expr;
      IExpr head = expr.head();
      if (head.isSymbol()) {
        final ISymbol symbol = (ISymbol) head;
        if (predicate.test(symbol)) {
          if (!symbolSet.contains(symbol)) {
            symbolSet.add(symbol);
          }
        }
      } else {
        collectSymbolsRecursive(head, symbolSet, predicate);
      }
      for (int i = 1; i < list.size(); i++) {
        IExpr arg = list.getRule(i);
        collectSymbolsRecursive(arg, symbolSet, predicate);
      }
    } else {
      if (expr.isSymbol()) {
        final ISymbol symbol = (ISymbol) expr;
        if (predicate.test(symbol)) {
          if (!symbolSet.contains(symbol)) {
            symbolSet.add(symbol);
          }
        }
      }
    }
  }

  static IAST fullDefinitionList(IAST symbolsList) {
    Set<ISymbol> symbolSet = new HashSet<ISymbol>();
    for (int i = 1; i < symbolsList.size(); i++) {
      symbolSet.add((ISymbol) symbolsList.get(i));
    }
    ISymbol.collectSymbolsRecursive(symbolsList, symbolSet,
        x -> x.isSymbol() && !(x.hasProtectedAttribute()));
    if (symbolSet.size() > 0) {
      IASTAppendable fullDefinition = F.ListAlloc();
      Iterator<ISymbol> iterator = symbolSet.iterator();
      while (iterator.hasNext()) {
        ISymbol symbol = iterator.next();

        IAST attributesList = ISymbol.attributesList(symbol);
        if (attributesList.size() > 1) {
          fullDefinition.append(F.Set(F.Attributes(symbol), attributesList));
        }
        IAST subRules = symbol.definition();
        fullDefinition.appendArgs(subRules);
      }
      return fullDefinition;
    }
    return F.NIL;
  }

  static String fullDefinitionListToString(IAST list) {
    IAST fullDefinition = fullDefinitionList(list);
    OutputFormFactory off = OutputFormFactory.get(EvalEngine.get().isRelaxedSyntax());
    off.setInputForm(true);
    off.setIgnoreNewLine(true);
    StringBuilder buf = new StringBuilder();
    for (int i = 1; i < fullDefinition.size(); i++) {
      if (!off.convert(buf, fullDefinition.getRule(i))) {
        return "ERROR-IN-OUTPUTFORM";
      }
      if (i < fullDefinition.argSize()) {
        buf.append("\n\n");
        off.setColumnCounter(0);
      }
    }
    return buf.toString();
  }

  /**
   * Does the attributes flag set contains the {@link ISymbol#FLAT} bit set?
   *
   * @return <code>true</code> if this attribute set contains the {@link ISymbol#FLAT} attribute.
   */
  public static boolean hasFlatAttribute(int attributes) {
    return (attributes & FLAT) == FLAT;
  }

  /**
   * Does the attributes flag set contains the {@link ISymbol#HOLDALLCOMPLETE} bit set?
   *
   * @param attributes
   * @return
   */
  public static boolean hasHoldAllCompleteAttribute(int attributes) {
    return (attributes & HOLDALLCOMPLETE) == HOLDALLCOMPLETE;
  }

  /**
   * Does the attributes flag set contains the {@link ISymbol#LISTABLE} bit set?
   *
   * @param attributes
   * @return
   */
  public static boolean hasListableAttribute(int attributes) {
    return (attributes & LISTABLE) == LISTABLE;
  }

  /**
   * Does this symbols attribute set contains the {@link ISymbol#ORDERLESS} attribute?
   *
   * @return <code>true</code> if this symbols attribute set contains the <code>Orderless</code>
   *         attribute.
   */
  public static boolean hasOrderlessAttribute(int attributes) {
    return (attributes & ORDERLESS) == ORDERLESS;
  }

  /**
   * Does the attributes flag set contains the {@link ISymbol#FLAT} and {@link ISymbol#ORDERLESS}
   * bits set?
   *
   * @return <code>true</code> if this attribute set contains the {@link ISymbol#FLAT} and
   *         {@link ISymbol#ORDERLESS} attribute.
   */
  public static boolean hasOrderlessFlatAttribute(int attributes) {
    return (attributes & FLATORDERLESS) == FLATORDERLESS;
  }

  static IAST symbolDefinition(ISymbol symbol) {

    if (symbol.equals(S.In)) {
      IAST list = EvalEngine.get().getEvalHistory().definitionIn();
      IASTAppendable result = F.ListAlloc(list.isNIL() ? 1 : list.size());
      result.appendArgs(list);
      return result;
    } else if (symbol.equals(S.Out)) {
      IAST list = EvalEngine.get().getEvalHistory().definitionOut();
      IASTAppendable result = F.ListAlloc(list.isNIL() ? 1 : list.size());
      result.appendArgs(list);
      return result;
    }

    List<IAST> rules = null;
    RulesData rulesData = symbol.getRulesData();
    if (rulesData != null) {
      rules = rulesData.definition();
    }
    IASTAppendable result = F.ListAlloc(rules == null ? 1 : rules.size() + 1);
    result = F.ListAlloc(rules == null ? 1 : rules.size() + 1);

    if (symbol.hasAssignedSymbolValue()) {
      IExpr assignedValue = symbol.assignedValue();
      if (symbol.isEvalFlagOn(SETDELAYED_FLAG_ASSIGNED_VALUE)) {
        result.append(F.SetDelayed(symbol, assignedValue));
      } else {
        result.append(F.Set(symbol, assignedValue));
      }
    }
    if (rules != null) {
      result.appendAll(rules);
    }
    return result;
  }

  public static String toString(final Context context, final String symbolName, EvalEngine engine) {
    if (context == Context.SYSTEM) {
      String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(symbolName);
      if (str != null) {
        return str;
      }
    } else if (context == Context.DUMMY) {
      return symbolName;
    } else if (context == Context.RUBI) {
      return context.completeContextName() + symbolName;
    }
    if (engine != null && engine.getContextPath().contains(context)) {
      return symbolName;
    }
    return context.completeContextName() + symbolName;
  }

  /**
   * Add the attributes to the existing attributes bit-set.
   *
   * @param attributes
   */
  public void addAttributes(final int attributes);

  /**
   * Get the <code>OwnValues</code> value which is assigned to the symbol or <code>null</code>, if
   * no value is assigned.
   *
   * @return <code>null</code>, if no value is assigned.
   */
  public IExpr assignedValue();

  /**
   * Set the <code>OwnValues</code> value of this variable. The value is assigned with the '='
   * operator.
   *
   * @param value the assigned 'right-hand-side' expression
   */
  default void assignValue(IExpr value) {
    assignValue(value, false);
  }

  /**
   * Set the <code>OwnValues</code> value of this variable
   *
   * @param value the assigned 'right-hand-side' expression
   * @param setDelayed if <code>true</code>, the value is assigned with the ':=' SetDelayed operator
   *        otherwise with the '=' operator.
   */
  public void assignValue(IExpr value, boolean setDelayed);

  /**
   * Clear the associated rules (<code>OwnValues</code>, <code>DownValues</code> and <code>UpValues
   * </code>) for this symbol but don't clear the attribute flags.
   *
   * @param engine the evaluation engine
   */
  public void clear(EvalEngine engine);

  /**
   * Clear all associated rules (<code>OwnValues</code>, <code>DownValues</code> and <code>UpValues
   * </code>) and attribute flags for this symbol.
   *
   * @param engine the evaluation engine
   */
  public void clearAll(EvalEngine engine);

  /**
   * Remove the attribute flags from the existing attributes bit-set.
   *
   * @param attributes
   */
  public void clearAttributes(final int attributes);

  /**
   * Remove the evaluation flags from the existing flags bit-set.
   *
   * @param flags
   */
  public void clearEvalFlags(final int flags);

  /**
   * Clear the <code>OwnValues</code> value which is assigned to this symbol.
   * 
   */
  default void clearValue() {
    clearValue(null);
  }

  /**
   * Clear the <code>OwnValues</code> value which is assigned to this symbol.
   * 
   * @param resetValue resetTo this value especially in a Java <code>finally</code> block.
   */
  public void clearValue(IExpr resetValue);

  /**
   * Check if ths symbol contains a "DownRule" or "UpRule"
   *
   * @return <code>true</code> if this symbol contains a "DownRule" or "UpRule"
   */
  public boolean containsRules();

  /**
   * Create internal rules data structure with precalculated sizes
   *
   * <ul>
   * <li>index 0 - number of equal rules in <code>RULES</code>
   * </ul>
   *
   * @param sizes
   */
  public RulesData createRulesData(int[] sizes);

  /**
   * Return a list of the rules associated to this symbol
   */
  public IAST definition();

  /**
   * Return the rules associated to this symbol in <code>String</code> representation
   *
   * @return the <code>String</code> representation of the symbol definition
   * @throws IOException
   */
  public String definitionToString() throws IOException;

  @Override
  default IExpr eval(EvalEngine engine) {
    return evaluate(engine).orElse(this);
  }

  @Override
  default IExpr evalAsLeadingTerm(ISymbol x, IExpr logx, int cdir) {
    return x;
  }

  /**
   * Evaluate the given expression for the &quot;down value&quot; rules associated with this symbol
   *
   * @param engine
   * @param expression
   * @return <code>F.NIL</code> if no evaluation was possible
   */
  public IExpr evalDownRule(EvalEngine engine, IExpr expression);

  public IExpr evalMessage(String messageName);

  /**
   * Evaluate the given expression for the &quot;up value&quot; rules (i.e. defined with UpSet and
   * UpsetDelayed) associated with this symbol.
   *
   * @param expression
   * @param engine
   * @return <code>F.NIL</code> if no evaluation was possible
   */
  public IExpr evalUpRules(IExpr expression, EvalEngine engine);

  default IAST f(IExpr arg1) {
    return F.unaryAST1(this, arg1);
  }

  default IAST f(IExpr arg1, IExpr arg2) {
    return F.binaryAST2(this, arg1, arg2);
  }

  default IAST f(IExpr arg1, IExpr arg2, IExpr arg3) {
    return F.ternaryAST3(this, arg1, arg2, arg3);
  }

  public IAST fullDefinition();

  public String fullDefinitionToString();

  /**
   * Get the value which is assigned to the symbol or <code>null</code>, if no value is assigned.
   *
   * @return <code>null</code>, if no value is assigned.
   * @deprecated use {@link #assignedValue()} instead
   */
  @Deprecated
  default IExpr get() {
    return assignedValue();
  }

  /**
   * Get the Attributes of this symbol (i.e. LISTABLE, FLAT, ORDERLESS,...)
   *
   * @return
   * @see IBuiltInSymbol#FLAT
   */
  public int getAttributes();

  /**
   * Get the context this symbol is assigned to.
   *
   * @return
   */
  public Context getContext();

  /**
   * Get the full symbol name string with the context prefix included.
   *
   * @return
   */
  default String getContextSymbolName() {
    return getContext().getContextName() + getSymbolName();
  }

  /**
   * Get the <i>general default value</i> for this symbol (i.e. <code>1</code> is the default value
   * for <code>Times</code>, <code>0</code> is the default value for <code>Plus</code>). The general
   * default value is used in pattern-matching for expressions like <code>a_. * b_. + c_</code>
   *
   * @return the default value or <code>F.NIL</code> if undefined.
   */
  public IExpr getDefaultValue();

  /**
   * Get the <i>default value</i> at the arguments position for this symbol (i.e. <code>1</code> is
   * the default value for <code>Power</code> at <code>position</code> <code>2</code>). The default
   * value is used in pattern-matching for expressions like <code>a ^ b_.</code>
   *
   * @param position the position for the default value
   * @return the default value or <code>F.NIL</code> if undefined.
   */
  public IExpr getDefaultValue(int position);

  /**
   * Get the pattern matching rules associated with a symbol. <code>RulesData</code> contains <code>
   * DownValues</code> and <code>UpValues</code> rules for pattern matching. <b>Note:</b> <code>
   * OwnValues</code> are directly stored in a symbol.
   *
   * @return <code>null</code> if no rules are defined
   */
  public RulesData getRulesData();

  /**
   * Get the pure symbol name string without the context prefix.
   *
   * @return
   */
  public String getSymbolName();

  /**
   * Is a (local or global) value assigned for this symbol?
   *
   * @return <code>true</code> if this symbol has an assigned value.
   */
  public boolean hasAssignedSymbolValue();

  /**
   * Does this symbols attribute set contains the <code>Flat</code> attribute?
   *
   * @return <code>true</code> if this symbols attribute set contains the <code>Flat</code>
   *         attribute.
   */
  boolean hasFlatAttribute();

  /**
   * Does this symbols attribute set contains the {@link ISymbol#HOLDALLCOMPLETE} attribute?
   *
   * @return
   */
  boolean hasHoldAllCompleteAttribute();

  /**
   * Does this symbols attribute set contains the <code>Listable</code> attribute?
   *
   * @return <code>true</code> if this symbols attribute set contains {@link Theta} <code>Listable
   *     </code> attribute.
   */
  boolean hasListableAttribute();

  /**
   * Does this symbol have the <code>ISymbol.NUMERICFUNCTION</code> attribute set?
   * 
   */
  default boolean hasNumericFunctionAttribute() {
    return ((getAttributes() & NUMERICFUNCTION) == NUMERICFUNCTION);
  }

  /**
   * Does this symbols attribute set contains the <code>OneIdentity</code> attribute?
   *
   * @return <code>true</code> if this symbols attribute set contains the <code>OneIdentity</code>
   *         attribute.
   */
  boolean hasOneIdentityAttribute();

  /**
   * Does this symbols attribute set contains the <code>Orderless</code> attribute?
   *
   * @return <code>true</code> if this symbols attribute set contains the <code>Orderless</code>
   *         attribute.
   */
  boolean hasOrderlessAttribute();

  /**
   * Does this symbols attribute set contains the <code>Flat</code> and <code>Orderless</code>
   * attribute?
   *
   * @return <code>true</code> if this symbols attribute set contains the <code>Flat</code> and the
   *         <code>Orderless</code> attribute.
   */
  boolean hasOrderlessFlatAttribute();

  /**
   * Does this symbol have the {@link ISymbol#PROTECTED} attribute set?
   * 
   * 
   */
  default boolean hasProtectedAttribute() {
    return ((getAttributes() & PROTECTED) == PROTECTED);
  }

  /** {@inheritDoc} */
  @Override
  default boolean isBooleanFormula() {
    if (isConstantAttribute() && !(isTrue() || isFalse())) {
      return false;
    }
    return true;
  }

  @Override
  default boolean isConstantAttribute() {
    return (getAttributes() & CONSTANT) == CONSTANT;
  }

  /**
   * Gives <code>true</code> if this symbol is in the <code>context</code>.
   *
   * @return
   */
  default boolean isContext(final Context context) {
    return context == getContext();
  }

  /**
   * 
   * @return <code>true</code> if this is of type {@link IBuiltInSymbol} and the symbol name starts
   *         with a '$'; <code>false</code> otherwise.
   */
  default boolean isDollarSymbol() {
    return false;
  }

  /**
   * Gives <code>true</code> if the system is in server mode and cannot be modified
   *
   * @return
   */
  public boolean isLocked();

  /**
   * Gives <code>true</code> if the system is in server mode and cannot be modified
   *
   * @return
   */
  public boolean isLocked(boolean packageMode);

  @Override
  default boolean isNumericFunction(boolean allowList) {
    return isConstantAttribute();
  }

  /**
   * Does this symbol have the {@link ISymbol#NUMERICFUNCTION} attribute set?
   * 
   * @deprecated use {@link #hasNumericFunctionAttribute()} instead
   */
  @Deprecated
  default boolean isNumericFunctionAttribute() {
    return hasNumericFunctionAttribute();
  }

  /**
   * Does this symbol have the {@link ISymbol#ONEIDENTITY} attribute set?
   * 
   * @deprecated use {@link #hasOneIdentityAttribute()} instead
   */
  @Deprecated
  default boolean isOneIdentityAttribute() {
    return hasOneIdentityAttribute();
  }

  /**
   * Does this symbol have the {@link ISymbol#PROTECTED} attribute set?
   * 
   * @deprecated use {@link #hasProtectedAttribute()} instead
   */
  @Deprecated
  default boolean isProtected() {
    return hasProtectedAttribute();
  }

  /**
   * Tests if this symbols name equals the given string
   *
   * @param symbolName
   * @return
   */
  @Override
  public boolean isString(String symbolName);

  /**
   * Returns <code>true</code>, if this symbol is in the set of defined <code>ids</code>.
   *
   * @param ids the symbol ordinal number
   * @return
   * @see org.matheclipse.core.expression.ID
   */
  default boolean isSymbolID(int... ids) {
    return false;
  }

  /**
   * Returns <code>true</code>, if this symbol has the given name. The comparison of the symbols
   * name with the given name is done according to the <code>Config.PARSER_USE_LOWERCASE_SYMBOLS
   * </code> setting.
   *
   * @param name the symbol name
   * @return
   */
  public boolean isSymbolName(String name);

  @Override
  public default IExpr[] linear(IExpr variable) {
    if (this.equals(variable)) {
      return new IExpr[] {F.C0, F.C1};
    }
    return new IExpr[] {this, F.C0};
  }

  @Override
  public default IExpr[] linearPower(IExpr variable) {
    if (this.equals(variable)) {
      return new IExpr[] {F.C0, F.C1, F.C1};
    }
    return new IExpr[] {this, F.C0, F.C1};
  }

  /**
   * If this symbol has attribute <code>ISymbol.CONSTANT</code> and the symbol's evaluator is of
   * instance <code>INumericConstant</code>, then apply the constants double value to the given
   * function and return the result, otherwise return <code>F.NIL</code>.
   *
   * @param function applys the function to a <code>double</code> value, resulting in an object of
   *        type {@code IExpr}.
   * @return the resulting expression from the function or <code>F.NIL</code>.
   * @see org.matheclipse.core.reflection.system.Abs
   * @see org.matheclipse.core.reflection.system.Ceiling
   * @see org.matheclipse.core.reflection.system.Floor
   */
  default IExpr mapConstantDouble(DoubleFunction<IExpr> function) {
    return F.NIL;
  }

  /**
   * Evaluate this symbol for the arguments as function <code>symbol(arg1, arg2, .... ,argN)</code>,
   * The <code>args</code> are converted from Java boolean to {@link S#True} or {@link S#False}
   * values.
   *
   * @param args
   * @return
   */
  default IExpr of(boolean... args) {
    IExpr[] array = new IExpr[args.length];
    for (int i = 0; i < array.length; i++) {
      array[i] = args[i] ? S.True : S.False;
    }
    return of(array);
  }

  /**
   * Evaluate this symbol for the arguments as function <code>symbol(arg1, arg2, .... ,argN)</code>.
   *
   * @param engine the current evaluation engine
   * @param args the arguments for which this function symbol should be evaluated
   * @return the evaluated expression; if no evaluation was possible return the created input
   *         expression.
   */
  public IExpr of(EvalEngine engine, IExpr... args);

  /**
   * Evaluate this symbol for the arguments converted to {@link IExpr} function
   * <code>symbol(expr1, expr2, .... ,exprN)</code>.
   * 
   * @param engine the current evaluation engine
   * @param args the arguments for which this function symbol should be evaluated
   * @return the evaluated expression; if no evaluation was possible return the created input
   *         expression
   */
  public IExpr of(EvalEngine engine, Object... args);

  /**
   * Evaluate this symbol for the arguments as function <code>symbol(arg1, arg2, .... ,argN)</code>.
   *
   * @param args the arguments for which this function symbol should be evaluated
   * @return the evaluated expression; if no evaluation was possible return the created input
   *         expression.
   */
  default IExpr of(IExpr... args) {
    return of(EvalEngine.get(), args);
  }

  /**
   * Evaluate this symbol for the arguments as function <code>
   * symbol(F.ZZ(arg1), F.ZZ(arg2), .... ,F.ZZ(argN))</code> by converting the args to
   * {@link IInteger} objects.
   *
   * @param args
   * @return the evaluated expression; if no evaluation was possible return the created input
   *         expression.
   */
  default IExpr of(int... args) {
    IExpr[] array = new IExpr[args.length];
    for (int i = 0; i < array.length; i++) {
      array[i] = F.ZZ(args[i]);
    }
    return of(array);
  }

  /**
   * Evaluate this symbol for the arguments as function <code>symbol(arg1, arg2, .... ,argN)</code>,
   * The <code>args</code> are converted from Java <code>String</code> to <code>IStringX</code>
   * values.
   *
   * @param args the string arguments of the function
   * @return
   */
  default IExpr of(String... args) {
    IExpr[] array = new IExpr[args.length];
    for (int i = 0; i < array.length; i++) {
      array[i] = F.stringx(args[i]);
    }
    return of(array);
  }

  /**
   * Evaluate this symbol for the arguments as function <code>
   * symbol(arg, part1, part2, .... , partN)</code>.
   *
   * @param engine the current evaluation engine
   * @param arg the main argument
   * @param parts the arguments for which this function symbol should be evaluated
   * @return the evaluated expression; if no evaluation was possible return the created input
   *         expression.
   */
  public IExpr of1(EvalEngine engine, IExpr arg, IExpr... parts);

  /**
   * Evaluate this symbol for the arguments as function <code>symbol(arg1, arg2, .... ,argN)</code>,
   * The <code>args</code> are converted from Java double to {@link INum} values.
   *
   * @param args
   * @return
   */
  default double ofN(double... args) throws ArgumentTypeException {
    IExpr[] array = new IExpr[args.length];
    for (int i = 0; i < array.length; i++) {
      array[i] = F.num(args[i]);
    }
    return of(array).evalf();
  }

  /**
   * This method returns <code>F.NIL</code> if no evaluation was possible. Evaluate this symbol for
   * the arguments as function <code>symbol(arg1, arg2, .... ,argN)</code>.
   *
   * @param engine the current evaluation engine
   * @param args the arguments for which this function symbol should be evaluated
   * @return <code>F.NIL</code> if no evaluation was possible.
   */
  public IExpr ofNIL(EvalEngine engine, IExpr... args);

  /**
   * Evaluate this symbol for the arguments as function <code>symbol(arg1, arg2, .... ,argN)</code>,
   * The objects are converted from Java form to IExpr for according to method
   * {@link Object2Expr#convert(Object, boolean, boolean)}.
   *
   * @param args the objects which should be used as arguments
   * @return
   */
  default IExpr ofObject(Object... args) {
    IExpr[] array = new IExpr[args.length];
    for (int i = 0; i < array.length; i++) {
      array[i] = Object2Expr.convert(args[i], true, false);
    }
    return of(array);
  }

  /**
   * Evaluate this symbol for the arguments as function <code>symbol(arg1, arg2, .... ,argN)</code>
   * to a boolean value. If the result isn't a boolean value return <code>false</code>.
   *
   * @param engine the current evaluation engine
   * @param args the arguments for which this function symbol should be evaluated
   * @return if the result isn't a boolean value return <code>false</code>.
   */
  public boolean ofQ(EvalEngine engine, IExpr... args);

  /**
   * Evaluate this symbol for the arguments as function <code>symbol(arg1, arg2, .... ,argN)</code>
   * to a boolean value. If the result isn't a boolean value return <code>false</code>.
   *
   * @param args the arguments for which this function symbol should be evaluated
   * @return if the result isn't a boolean value return <code>false</code>.
   */
  public boolean ofQ(IExpr... args);

  /**
   * Get the ordinal number of this built-in symbol in the enumeration of built-in symbols. If this
   * is no built-in symbol return <code>-1</code> (ID.UNKNOWN)
   *
   * @return
   */
  default int ordinal() {
    return ID.UNKNOWN;
  }

  /**
   * Associate a new &quot;down value&quot; rule with default priority to this symbol.
   *
   * @param setSymbol which of the symbols <code>Set, SetDelayed, UpSet, UpSetDelayed</code> was
   *        used for defining this rule
   * @param equalRule <code>true</code> if the leftHandSide could be matched with equality
   * @param leftHandSide
   * @param rightHandSide
   * @param packageMode <code>true</code> if we are on &quot;package mode&quot;
   * @return <code>null</code> if no pattern matcher was generated
   * @see PatternMap#DEFAULT_RULE_PRIORITY
   */
  default IPatternMatcher putDownRule(final int setSymbol, boolean equalRule, IAST leftHandSide,
      IExpr rightHandSide, boolean packageMode) {
    return putDownRule(setSymbol, equalRule, leftHandSide, rightHandSide,
        IPatternMap.DEFAULT_RULE_PRIORITY, packageMode);
  }

  /**
   * Associate a new rule with the given priority to this symbol.<br>
   * Rules with lower numbers have higher priorities.
   *
   * @param setSymbol which of the symbols <code>Set, SetDelayed, UpSet, UpSetDelayed</code> was
   *        used for defining this rule
   * @param equalRule <code>true</code> if the leftHandSide could be matched with equality
   * @param leftHandSide
   * @param rightHandSide
   * @param priority the priority of the rule
   * @param packageMode <code>true</code> if we are on &quot;package mode&quot;
   * @return <code>null</code> if no pattern matcher was generated
   * @see PatternMap#DEFAULT_RULE_PRIORITY
   */
  public IPatternMatcher putDownRule(final int setSymbol, boolean equalRule, IAST leftHandSide,
      IExpr rightHandSide, int priority, boolean packageMode);

  /**
   * Associate a new &quot;down value&quot; rule with default priority to this symbol.
   *
   * @param setSymbol which of the symbols <code>Set, SetDelayed, UpSet, UpSetDelayed</code> was
   *        used for defining this rule
   * @param equalRule <code>true</code> if the leftHandSide could be matched with equality
   * @param leftHandSide
   * @param rightHandSide
   * @param packageMode <code>true</code> if we are on &quot;package mode&quot;
   * @return <code>null</code> if no pattern matcher was generated
   * @see PatternMap#DEFAULT_RULE_PRIORITY
   */
  default IPatternMatcher putDownRule(final int setSymbol, boolean equalRule,
      IPatternObject leftHandSide, IExpr rightHandSide, boolean packageMode) {
    return putDownRule(setSymbol, equalRule, leftHandSide, rightHandSide,
        IPatternMap.DEFAULT_RULE_PRIORITY, packageMode);
  }

  /**
   * Associate a new rule with the given priority to this symbol.<br>
   * Rules with lower numbers have higher priorities.
   *
   * @param setSymbol which of the symbols <code>Set, SetDelayed, UpSet, UpSetDelayed</code> was
   *        used for defining this rule
   * @param equalRule <code>true</code> if the leftHandSide could be matched with equality
   * @param leftHandSide
   * @param rightHandSide
   * @param priority the priority of the rule
   * @param packageMode <code>true</code> if we are on &quot;package mode&quot;
   * @return <code>null</code> if no pattern matcher was generated
   * @see PatternMap#DEFAULT_RULE_PRIORITY
   */
  public IPatternMatcher putDownRule(final int setSymbol, boolean equalRule,
      IPatternObject leftHandSide, IExpr rightHandSide, int priority, boolean packageMode);

  /**
   * <p>
   * Associate a new rule, which invokes a method, to this symbol.
   * 
   * <p>
   * Deprecated: don't use dynamically called methods.
   *
   * @param pmEvaluator
   * @return
   */
  @Deprecated
  public void putDownRule(final PatternMatcherAndInvoker pmEvaluator);

  public void putMessage(final int setSymbol, String messageName, IStringX message);

  /**
   * Associate a new &quot;up value&quot; rule with default priority to this symbol.
   *
   * @param setSymbol which of the symbols <code>Set, SetDelayed, UpSet, UpSetDelayed</code> was
   *        used for defining this rule
   * @param equalRule <code>true</code> if the leftHandSide could be matched with equality
   * @param leftHandSide
   * @param rightHandSide
   * @return <code>null</code> if no pattern matcher was generated
   * @see PatternMap#DEFAULT_RULE_PRIORITY
   */
  public IPatternMatcher putUpRule(final int setSymbol, boolean equalRule, IAST leftHandSide,
      IExpr rightHandSide);

  /**
   * Associate a new &quot;up value&quot; rule with the given priority to this symbol.<br>
   * Rules with lower numbers have higher priorities.
   *
   * @param setSymbol which of the symbols <code>Set, SetDelayed, UpSet, UpSetDelayed</code> was
   *        used for defining this rule
   * @param equalRule <code>true</code> if the leftHandSide could be matched with equality
   * @param leftHandSide
   * @param rightHandSide
   * @param priority the priority of the rule
   * @return <code>null</code> if no pattern matcher was generated
   * @see PatternMap#DEFAULT_RULE_PRIORITY
   */
  public IPatternMatcher putUpRule(final int setSymbol, final boolean equalRule,
      final IAST leftHandSide, final IExpr rightHandSide, final int priority);

  /**
   * Deserialize the rules associated to this object
   *
   * @param stream
   * @throws java.io.IOException
   */
  public void readRules(java.io.ObjectInputStream stream)
      throws IOException, ClassNotFoundException;

  /**
   * Apply the function to the currently assigned value of the symbol and reassign the result value
   * to the symbol. Used for functions like <code>
   * AppendTo, AssociateTo, Decrement, Increment, PrependTo,...</code>
   *
   * @param function the function which should be applied
   * @param functionSymbol if this method throws an exception the symbol will be displayed in the
   *        exceptions message
   * @param engine the evaluation engine
   * @return an array with the currently assigned value of the symbol and the new calculated value
   *         of the symbol or <code>null</code> if the reassignment isn't possible.
   */
  public IExpr[] reassignSymbolValue(Function<IExpr, IExpr> function, ISymbol functionSymbol,
      EvalEngine engine);

  /**
   * Apply the ast to the currently assigned value of the symbol and reassign the result value to
   * the symbol. Used for functions like AppendTo, Decrement, Increment,...
   *
   * @param ast
   * @param ast the ast which should be evaluated by replacing the first argument with the current
   *        value of the symbol
   * @param functionSymbol if this method throws an exception the symbol will be displayed in the
   *        exceptions message
   * @param engine the evaluation engine
   * @return an array with the currently assigned value of the symbol and the new calculated value
   *         of the symbol or <code>null</code> if the reassignment isn't possible.
   */
  public IExpr[] reassignSymbolValue(IASTMutable ast, ISymbol functionSymbol, EvalEngine engine);

  /**
   * Remove the rules associate with this symbol, which equals the given <code>leftHandSide</code>
   * expression.
   *
   * @param setSymbol
   * @param equalRule
   * @param leftHandSide
   * @param packageMode
   * @return <code>true</code> if a rule could be removed, <code>false</code> otherwise
   */
  public boolean removeRule(final int setSymbol, final boolean equalRule, final IExpr leftHandSide,
      boolean packageMode);

  /**
   * Set the Attributes of this symbol (i.e. LISTABLE, FLAT, ORDERLESS,...)
   *
   * @param attributes the Attributes of this symbol
   */
  public void setAttributes(int attributes);

  /**
   * Set the <i>general default value</i> for this symbol (i.e. <code>1</code> is the default value
   * for <code>Times</code>, <code>0</code> is the default value for <code>Plus</code>). The general
   * default value is used in pattern-matching for expressions like <code>a_. * b_. + c_</code>
   *
   * @param expr the general default value
   * @see IBuiltInSymbol#getDefaultValue()
   */
  public void setDefaultValue(IExpr expr);

  /**
   * Set the <i>default value</i> at the arguments position for this symbol (i.e. <code>1</code> is
   * the default value for <code>Power</code> at <code>position</code> <code>2</code>). The default
   * value is used in pattern-matching for expressions like <code>a ^ b_.</code>
   *
   * @param position the position for the default value
   * @param expr the default value for the given position
   * @see IBuiltInSymbol#getDefaultValue(int)
   */
  public void setDefaultValue(int position, IExpr expr);

  public void setRulesData(RulesData rd);

  /**
   * Serialize the rule definitions associated to this symbol
   *
   * @param stream
   * @throws java.io.IOException
   * @return <code>false</code> if the symbol contains no rule definition.
   */
  public boolean writeRules(java.io.ObjectOutputStream stream) throws java.io.IOException;
}
