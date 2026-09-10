package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.OptionsPattern;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.RulesData;

/**
 * A symbol's definitions read and written as data.
 *
 * <p>
 * <code>DownValues</code>, <code>UpValues</code> and the rest could already be read; here they can
 * also be assigned, and <code>Language`ExtendedFullDefinition</code> reads all of them at once as
 * one expression that can be renamed and assigned back:
 *
 * <pre>
 * Language`ExtendedFullDefinition[type] = Language`ExtendedFullDefinition[parent] /. parent -&gt; type
 * </pre>
 *
 * <p>
 * That single line is how an application built on symbols-as-objects derives one type from another
 * - it is the whole of <code>CreateUType</code> in the WLJS Notebook. Without it every such type is
 * an empty shell.
 */
public class DefinitionFunctions {

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.SubValues.setEvaluator(new SubValues());
        S.ExtendedFullDefinition.setEvaluator(new ExtendedFullDefinition());
      }
    }
  }

  /**
   * <code>SubValues[symbol]</code>: the rules for <code>symbol[…][…]</code>.
   *
   * <p>
   * Symja keeps them among the symbol's down rules rather than in a store of their own, so this is
   * the part of <code>DownValues</code> whose left-hand side is a call of a call.
   */
  private static final class SubValues extends AbstractCoreFunctionEvaluator
      implements ISetEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
      if (arg1.isNIL()) {
        return F.NIL;
      }
      RulesData rulesData = ((ISymbol) arg1).getRulesData();
      if (rulesData == null) {
        return F.CEmptyList;
      }
      IAST downValues = rulesData.downValues();
      IASTAppendable result = F.ListAlloc(downValues.argSize());
      for (int i = 1; i < downValues.size(); i++) {
        if (isSubValueRule(downValues.get(i))) {
          result.append(downValues.get(i));
        }
      }
      return result;
    }

    @Override
    public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      return assignValues(leftHandSide, rightHandSide, builtinSymbol, engine, false);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /** Is this a rule for <code>f[…][…]</code> rather than for <code>f[…]</code>? */
  private static boolean isSubValueRule(IExpr rule) {
    IExpr lhs = leftHandSideOf(rule);
    return lhs.isAST() && lhs.head().isAST();
  }

  /** The left-hand side a rule matches, with its <code>HoldPattern</code> taken off. */
  private static IExpr leftHandSideOf(IExpr rule) {
    if (rule.isRuleAST()) {
      IExpr lhs = rule.first();
      if (lhs.isAST(S.HoldPattern, 2) || lhs.isAST(S.Literal, 2)) {
        return lhs.first();
      }
      return lhs;
    }
    return F.NIL;
  }

  /**
   * <code>Language`ExtendedFullDefinition[symbol]</code>: everything that has been assigned to a
   * symbol, as one expression.
   *
   * <p>
   * The answer is a list of <code>HoldForm[symbol] -&gt; &lt;|"OwnValues" -&gt; …|&gt;</code>, held
   * throughout, so that renaming the symbol in it with <code>/.</code> and assigning it back copies
   * the definitions onto another symbol.
   */
  private static final class ExtendedFullDefinition extends AbstractCoreFunctionEvaluator
      implements ISetEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
      if (arg1.isNIL()) {
        return F.NIL;
      }
      ISymbol symbol = (ISymbol) arg1;
      IASTAppendable parts = F.ListAlloc(8);
      parts.append(F.Rule(F.stringx("OwnValues"), ownValues(symbol)));
      parts.append(F.Rule(F.stringx("DownValues"), downValues(symbol, false)));
      parts.append(F.Rule(F.stringx("SubValues"), downValues(symbol, true)));
      parts.append(F.Rule(F.stringx("UpValues"), upValues(symbol)));
      parts.append(F.Rule(F.stringx("Attributes"), listOrEmpty(engine.evaluate(F.Attributes(symbol)))));
      parts.append(F.Rule(F.stringx("Options"), listOrEmpty(engine.evaluate(F.Options(symbol)))));
      parts.append(F.Rule(F.stringx("Messages"),
          listOrEmpty(engine.evaluate(F.unaryAST1(S.Messages, symbol)))));
      parts.append(F.Rule(F.stringx("DefaultValues"), defaultValues(symbol)));
      return F.list(F.Rule(F.HoldForm(symbol), F.assoc(parts)));
    }

    @Override
    public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide,
        IBuiltInSymbol builtinSymbol, EvalEngine engine) {
      if (!leftHandSide.isAST(S.ExtendedFullDefinition, 2)) {
        return F.NIL;
      }
      IExpr definition =
          builtinSymbol == S.SetDelayed ? rightHandSide : engine.evaluate(rightHandSide);
      IAST definitions = definition.isList() ? (IAST) definition : F.list(definition);
      for (int i = 1; i < definitions.size(); i++) {
        if (!install(definitions.get(i), engine)) {
          return F.NIL;
        }
      }
      return builtinSymbol == S.Set ? definition : S.Null;
    }

    /** Put one <code>HoldForm[symbol] -&gt; &lt;|…|&gt;</code> onto the symbol it names. */
    private static boolean install(IExpr entry, EvalEngine engine) {
      if (!entry.isRuleAST()) {
        return false;
      }
      IExpr target = entry.first();
      if (target.isAST(S.HoldForm, 2) || target.isAST(S.Hold, 2)) {
        target = target.first();
      }
      if (!target.isSymbol() || !(entry.second() instanceof IAssociation)) {
        return false;
      }
      ISymbol symbol = (ISymbol) target;
      if (symbol.hasProtectedAttribute()) {
        // Symbol `1` is Protected.
        Errors.printMessage(S.ExtendedFullDefinition, "wrsym", F.list(symbol), engine);
        return false;
      }
      IAssociation values = (IAssociation) entry.second();
      // the attributes come first: a rule for a symbol which holds its arguments is stored
      // differently from one for a symbol which does not
      IExpr attributes = values.getValue(F.stringx("Attributes"));
      if (attributes != null && attributes.isList()) {
        engine.evaluate(F.Set(F.Attributes(symbol), attributes));
      }
      assignRuleList(symbol, values.getValue(F.stringx("OwnValues")), false, engine);
      assignRuleList(symbol, values.getValue(F.stringx("DownValues")), false, engine);
      assignRuleList(symbol, values.getValue(F.stringx("SubValues")), false, engine);
      assignRuleList(symbol, values.getValue(F.stringx("UpValues")), true, engine);
      assignRuleList(symbol, values.getValue(F.stringx("DefaultValues")), false, engine);
      IExpr options = values.getValue(F.stringx("Options"));
      if (options != null && options.isList() && !options.isEmptyList()) {
        engine.evaluate(F.Set(F.Options(symbol), options));
      }
      IExpr messages = values.getValue(F.stringx("Messages"));
      if (messages != null && messages.isList()) {
        assignRuleList(symbol, messages, false, engine);
      }
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /** A list, or the empty list where the answer was something else. */
  private static IExpr listOrEmpty(IExpr expr) {
    return expr.isList() ? expr : F.CEmptyList;
  }

  private static IAST ownValues(ISymbol symbol) {
    IExpr value = symbol.assignedValue();
    return value == null ? F.CEmptyList : F.list(F.RuleDelayed(F.HoldPattern(symbol), value));
  }

  private static IAST downValues(ISymbol symbol, boolean subValues) {
    RulesData rulesData = symbol.getRulesData();
    if (rulesData == null) {
      return F.CEmptyList;
    }
    IAST all = rulesData.downValues();
    IASTAppendable result = F.ListAlloc(all.argSize());
    for (int i = 1; i < all.size(); i++) {
      if (isSubValueRule(all.get(i)) == subValues) {
        result.append(all.get(i));
      }
    }
    return result;
  }

  private static IAST upValues(ISymbol symbol) {
    RulesData rulesData = symbol.getRulesData();
    return rulesData == null ? F.CEmptyList : rulesData.upValues();
  }

  private static IAST defaultValues(ISymbol symbol) {
    RulesData rulesData = symbol.getRulesData();
    if (rulesData == null) {
      return F.CEmptyList;
    }
    IAST values = rulesData.defaultValues(symbol);
    // answers NIL when there are none, and one NIL inside spoils the whole expression
    return values.isPresent() ? values : F.CEmptyList;
  }

  /**
   * <code>DownValues[f] = rules</code> and the rest of them.
   *
   * @param upRules whether the rules are attached to the symbol as up values
   */
  static IExpr assignValues(final IExpr leftHandSide, IExpr rightHandSide,
      IBuiltInSymbol builtinSymbol, EvalEngine engine, boolean upRules) {
    if (!leftHandSide.isAST() || !leftHandSide.first().isSymbol()) {
      return F.NIL;
    }
    ISymbol symbol = (ISymbol) leftHandSide.first();
    if (symbol.hasProtectedAttribute()) {
      // Symbol `1` is Protected.
      Errors.printMessage(leftHandSide.topHead(), "wrsym", F.list(symbol), engine);
      return F.NIL;
    }
    IExpr rules = builtinSymbol == S.SetDelayed ? rightHandSide : engine.evaluate(rightHandSide);
    if (!assignRuleList(symbol, rules, upRules, engine)) {
      return F.NIL;
    }
    return builtinSymbol == S.Set ? rules : S.Null;
  }

  /**
   * Attach a list of <code>RuleDelayed(HoldPattern(lhs), rhs)</code> to a symbol.
   *
   * @return <code>false</code> if the argument is not a list of rules
   */
  private static boolean assignRuleList(ISymbol symbol, IExpr rules, boolean upRules,
      EvalEngine engine) {
    if (rules == null || rules.isNIL()) {
      return true;
    }
    if (!rules.isList()) {
      return false;
    }
    IAST list = (IAST) rules;
    for (int i = 1; i < list.size(); i++) {
      IExpr rule = list.get(i);
      if (!rule.isRuleAST()) {
        return false;
      }
      IExpr lhs = freshOptionsPatterns(leftHandSideOf(rule));
      IExpr rhs = rule.second();
      if (lhs.isSymbol()) {
        // an own value
        ((ISymbol) lhs).assignValue(rhs, false);
      } else if (lhs.isAST()) {
        if (upRules) {
          symbol.putUpRule(IPatternMatcher.TAGSET | IPatternMatcher.SET_DELAYED, false, (IAST) lhs,
              rhs);
        } else {
          symbol.putDownRule(IPatternMatcher.SET_DELAYED, false, (IAST) lhs, rhs, true);
        }
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * The left-hand side with a new <code>OptionsPattern</code> object wherever it had one.
   *
   * <p>
   * An <code>OptionsPattern</code> remembers the symbol whose options it last matched, so two rules
   * must not share one object. Copying a symbol's definitions onto another symbol - which is how
   * one object type is derived from another - would otherwise hand the copy the very same pattern
   * objects, and the first type to be used would claim them: every later type's constructor would
   * then be refused.
   */
  private static IExpr freshOptionsPatterns(IExpr expr) {
    if (expr instanceof OptionsPattern) {
      OptionsPattern options = (OptionsPattern) expr;
      return OptionsPattern.valueOf(options.getSymbol(), options.getDefaultOptions());
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IASTMutable result = F.NIL;
      for (int i = 0; i < ast.size(); i++) {
        IExpr part = freshOptionsPatterns(ast.get(i));
        if (part != ast.get(i)) {
          if (result.isNIL()) {
            result = ast.copy();
          }
          result.set(i, part);
        }
      }
      return result.isPresent() ? result : expr;
    }
    return expr;
  }

  public static void initialize() {
    Initializer.init();
  }

  private DefinitionFunctions() {}
}
