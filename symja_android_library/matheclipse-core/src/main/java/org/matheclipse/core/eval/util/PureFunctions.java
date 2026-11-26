package org.matheclipse.core.eval.util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.matheclipse.core.builtin.AttributeFunctions;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.patternmatching.CaptureAvoidance;
import org.matheclipse.core.visit.ModuleReplaceAll;
import org.matheclipse.core.visit.VisitorReplaceSlots;

/**
 * Everything a pure function is made of, in one place: how a <code>Slot</code> is filled, how a
 * <code>Function</code> is validated, and how the two application forms bind their arguments.
 *
 * <p>
 * A <code>Function</code> comes in two unrelated forms, and it is worth being explicit about which
 * is which, because they share nothing but their head:
 *
 * <ul>
 * <li><b>the slot form</b>, <code>Function(body)</code>, whose body refers to the arguments as
 * <code>#1</code>, <code>#2</code>, <code>##2</code> or <code>#name</code>. Applying it substitutes
 * the slots; see {@link #applySlotForm(IAST, IAST, EvalEngine)}.
 * <li><b>the named form</b>, <code>Function(x, body)</code> or <code>Function({x,y}, body)</code>,
 * which binds ordinary symbols and contains no slots at all. Applying it renames the bound symbols
 * and substitutes them; see {@link #applyNamedForm(IAST, IAST, EvalEngine)}.
 * </ul>
 *
 * <p>
 * A nested pure function binds its own slots, so the outer application must not reach into it.
 * {@link VisitorReplaceSlots} enforces that for every argument it descends into, and
 * {@link #substituteSlots(IExpr, IAST)} enforces it for the body it is handed.
 *
 * <p>
 * Not to be confused with two other things this codebase calls a slot: the index positions of a
 * tensor in <code>TensorContract</code> and <code>HodgeDual</code>, and the <code>TemplateSlot</code>
 * of <code>StringTemplate</code>. Neither has anything to do with <code>S.Slot</code>. Note also
 * that <code>F.Slot1</code> is used at many sites purely as a positional placeholder token for
 * {@link org.matheclipse.core.interfaces.IAST#mapThread(IAST, int)}, which performs plain positional
 * substitution and never consults this class.
 */
public final class PureFunctions {

  private PureFunctions() {}

  /**
   * Replace every <code>Slot</code> and <code>SlotSequence</code> in <code>body</code> with the
   * corresponding argument of <code>application</code>.
   *
   * <p>
   * <code>application</code> is the whole application <code>Function(body)(a1, a2, ...)</code>, so
   * that index 0 is the function itself, which is how <code>#0</code> resolves. A slot which cannot
   * be filled is left as it stands.
   *
   * @return {@link F#NIL} if nothing was replaced
   */
  public static IExpr substituteSlots(IExpr body, final IAST application) {
    if (body.isPureFunction()) {
      // body binds its own slots
      return F.NIL;
    }
    return body.accept(new VisitorReplaceSlots(application));
  }

  /**
   * As {@link #substituteSlots(IExpr, IAST)}, answering <code>elseExpr</code> when nothing was
   * replaced.
   */
  public static IExpr substituteSlotsOrElse(IExpr body, final IExpr application, IExpr elseExpr) {
    if (body.isPureFunction()) {
      return elseExpr;
    }
    if (application.isAST()) {
      return body.accept(new VisitorReplaceSlots((IAST) application)).orElse(elseExpr);
    }
    return elseExpr;
  }

  /**
   * Fill <code>#1</code> in a template expression with a single value.
   *
   * <p>
   * This is not a <code>Function</code> application: the template is an ordinary expression written
   * with <code>#1</code> as a placeholder, so <code>#0</code> and <code>##</code> have no useful
   * meaning here. Kept separate from {@link #substituteSlots(IExpr, IAST)} so that the difference is
   * visible at the call site.
   *
   * @return {@link F#NIL} if nothing was replaced
   */
  public static IExpr substituteSlot1(IExpr template, IExpr value) {
    return template.accept(new VisitorReplaceSlots(F.List(value)));
  }

  /**
   * The source text of a <code>Slot</code> or <code>SlotSequence</code>: <code>#n</code> for a
   * non-negative integer, <code>#name</code> for a string which is a plain identifier, and <code>#"..."</code>
   * for any other string.
   *
   * <p>
   * Shared by the output, MathML and TeX form factories so that the three cannot disagree, and so
   * that what they print parses back to the same expression. Anything else, such as
   * <code>Slot(x)</code> with a symbol argument, has no short form and must be printed as an
   * ordinary function.
   *
   * @return the token without markup, or <code>null</code> if this expression has no short form
   */
  public static String slotToken(IAST slot) {
    if (!slot.isAST1()) {
      return null;
    }
    final String prefix = slot.isAST(S.SlotSequence, 2) ? "##" : "#";
    IExpr arg1 = slot.arg1();
    if (arg1.isInteger()) {
      // #-1 would read back as Slot(1)-1
      return arg1.isNegative() ? null : prefix + arg1.toString();
    }
    if (arg1.isString()) {
      String name = arg1.toString();
      return isIdentifier(name) ? prefix + name : prefix + "\"" + name + "\"";
    }
    return null;
  }

  /** Whether <code>name</code> can follow a <code>#</code> without quoting. */
  private static boolean isIdentifier(String name) {
    if (name.isEmpty()) {
      return false;
    }
    if (!Character.isLetter(name.charAt(0)) && name.charAt(0) != '$') {
      return false;
    }
    for (int i = 1; i < name.length(); i++) {
      char ch = name.charAt(i);
      if (!Character.isLetterOrDigit(ch) && ch != '$') {
        return false;
      }
    }
    return true;
  }

  /**
   * Apply the slot form <code>Function(body)</code> to <code>application</code>.
   *
   * <p>
   * A <code>Slot(n)</code> or <code>SlotSequence(n)</code> which names an argument that was not
   * supplied is left as it stands and reported with the <code>Function::slotn</code> or
   * <code>Function::slots</code> message. The value is the
   * partially substituted body either way; only the diagnostic is added.
   *
   * @param function the <code>Function(body)</code>, already validated
   * @param application the whole application, with its arguments evaluated
   */
  public static IExpr applySlotForm(IAST function, IAST application, EvalEngine engine) {
    return applySlotForm(function.arg1(), function, application, engine);
  }

  /**
   * As {@link #applySlotForm(IAST, IAST, EvalEngine)}, for a body which is not the function's first
   * argument: <code>Function(Null, body)</code> names no parameters and refers to its arguments as
   * slots, exactly as <code>Function(body)</code> does.
   *
   * @param body the body to substitute the slots in
   * @param function the whole <code>Function(...)</code>, only used in the diagnostic
   */
  public static IExpr applySlotForm(IExpr body, IAST function, IAST application,
      EvalEngine engine) {
    if (body.isPureFunction()) {
      // body binds its own slots
      return body;
    }
    java.util.Set<ISymbol> locals = CaptureAvoidance.scopeLocals(body);
    if (!locals.isEmpty() && CaptureAvoidance.collides(application.subList(1), locals)) {
      // (Module({e=1}, #+e)&)(e+1) is 2+e (WMA)
      IExpr renamed = CaptureAvoidance.renameColliding(body, application.subList(1),
          java.util.Collections.<ISymbol>emptyList(), engine);
      if (renamed.isPresent()) {
        body = renamed;
      }
    }
    VisitorReplaceSlots visitor = new VisitorReplaceSlots(application);
    IExpr result = body.accept(visitor).orElse(body);
    reportUnfillableSlots(visitor.getUnfillableSlots(), function, application, engine);
    IExpr unfillableName = visitor.getUnfillableNamedSlot();
    if (unfillableName.isPresent()) {
      // Named slot `1` in `2` cannot be filled from `3`.
      Errors.printMessage(S.Function, "slota",
          F.List(F.stringx(unfillableName.toString()), function,
              application.argSize() >= 1 ? application.arg1() : application),
          engine);
    }
    return result;
  }

  /** How many messages of one kind an application prints before it stops printing them. */
  private static final int SLOT_MESSAGE_LIMIT = 3;

  /**
   * Report every slot of the body which the arguments cannot fill, in the order of the body:
   * <code>Function::slotn</code> for <code>#n</code>, <code>Function::slots</code> for
   * <code>##n</code>. A repeated slot gives the same message, which is printed once in an
   * evaluation, so it is not counted again. After the third message of a kind, a
   * <code>General::stop</code> line says that further ones are suppressed.
   */
  private static void reportUnfillableSlots(java.util.List<IAST> unfillable, IAST function,
      IAST application, EvalEngine engine) {
    int slotn = 0;
    int slots = 0;
    for (IAST slot : new java.util.LinkedHashSet<IAST>(unfillable)) {
      boolean sequence = slot.isSlotSequence();
      int count = sequence ? ++slots : ++slotn;
      if (count > SLOT_MESSAGE_LIMIT) {
        continue;
      }
      String tag = sequence ? "slots" : "slotn";
      // Slot number `1` in `2` cannot be filled from `3`.
      // SlotSequence[`1`] in `2` cannot be filled from `3`.
      Errors.printMessage(S.Function, tag, F.List(slot.arg1(), function, application), engine);
      if (count == SLOT_MESSAGE_LIMIT) {
        // Further output of `1` will be suppressed during this calculation.
        Errors.printMessage(S.General, "stop", F.List(F.stringx("Function::" + tag)), engine);
      }
    }
  }

  /**
   * Apply the named form <code>Function(x, body)</code> or <code>Function({x,y}, body[, attrs])</code>
   * to <code>application</code>. The bound symbols are renamed first, so that a symbol of the same
   * name in the arguments is not captured.
   *
   * @param function the <code>Function(params, body[, attrs])</code>, already validated
   * @param application the whole application, with its arguments evaluated
   * @param attributes the attributes named by a third argument, or {@link ISymbol#NOATTRIBUTE}
   */
  public static IExpr applyNamedForm(IAST function, IAST application, int attributes,
      EvalEngine engine) {
    IExpr body = function.arg2();
    IAST parameters = function.arg1().makeList();
    if (parameters.size() > application.size()) {
      // To many parameters in `1` to be filled from `2`.
      return Errors.printMessage(S.Function, "fpct", F.list(parameters, function), engine);
    }

    IdentityHashMap<ISymbol, IExpr> moduleVariables = new IdentityHashMap<ISymbol, IExpr>();
    // a Module/With local which collides with a free symbol of an argument is renamed as well:
    // Function(v, Module({e=1}, v+e))(e+1) is 2+e (WMA)
    java.util.Set<ISymbol> locals = CaptureAvoidance.scopeLocals(body);
    if (!locals.isEmpty() && CaptureAvoidance.collides(application.subList(1), locals)) {
      for (int i = 1; i < application.size(); i++) {
        IExpr argument = application.get(i);
        if (!argument.isNumber() && !argument.isString()) {
          new VariablesSet(argument).initSymbols(moduleVariables);
        }
      }
      for (IExpr parameter : parameters) {
        if (parameter.isSymbol()) {
          moduleVariables.remove(parameter);
        }
      }
    }
    IExpr renamed =
        body.accept(new ModuleReplaceAll(moduleVariables, engine, EvalEngine.uniqueName("$")));
    if (renamed.isPresent()) {
      body = renamed;
    }

    // build the parameter map once, not once per visited subexpression
    final Map<IExpr, IExpr> rulesMap = parameterMap(parameters, application);
    IExpr result;
    if (!locals.isEmpty() && parameters.exists(p -> locals.contains(p))) {
      // an inner Module, With, Block or Function binds a parameter again
      Map<IExpr, IExpr> scopedMap = new HashMap<IExpr, IExpr>();
      for (int i = 1; i <= parameters.argSize(); i++) {
        scopedMap.put(parameters.get(i), application.get(i));
      }
      result = substituteScoped(body, scopedMap);
    } else {
      result = F.subst(body, x -> {
        IExpr temp = rulesMap.get(x);
        return temp != null ? temp : F.NIL;
      });
    }

    if (result.isAST() && function.argSize() == 3) {
      // LISTABLE was already applied to the arguments; applying it to the result as well would
      // thread an unrelated list produced by the body
      int resultAttributes = attributes & ~ISymbol.LISTABLE;
      if (resultAttributes != ISymbol.NOATTRIBUTE) {
        IASTMutable copy = ((IAST) result).copy();
        return engine.evalAttributes(copy, copy.size(), S.None, resultAttributes).orElse(result);
      }
    }
    return engine.evaluate(result);
  }

  /**
   * Substitute the parameters in <code>expr</code>, but not where an inner scoping construct binds
   * one of them again: in <code>Module({x = x + 1}, x)</code> the parameter <code>x</code> is
   * substituted in the initializer, which belongs to the outer scope, but neither in the local
   * list nor in the body. <code>Function(x, Module({x = x + 1}, x))(5)</code> is <code>6</code>.
   */
  private static IExpr substituteScoped(IExpr expr, Map<IExpr, IExpr> rulesMap) {
    if (rulesMap.isEmpty()) {
      return expr;
    }
    if (!expr.isAST()) {
      IExpr temp = rulesMap.get(expr);
      return temp != null ? temp : expr;
    }
    IAST ast = (IAST) expr;
    // Block localizes values, not names: its local list receives the parameters
    final boolean scoping =
        (ast.isAST(S.Module, 3) || ast.isAST(S.With, 3)) && ast.arg1().isList();
    final boolean function = ast.isAST(S.Function, 3) || ast.isAST(S.Function, 4);
    if (scoping || function) {
      IAST localList = ast.arg1().makeList();
      java.util.Set<IExpr> bound = new java.util.HashSet<IExpr>();
      for (IExpr local : localList) {
        IExpr symbol = (local.isAST(S.Set, 3) || local.isAST(S.SetDelayed, 3)) ? local.first()
            : local;
        if (rulesMap.containsKey(symbol)) {
          bound.add(symbol);
        }
      }
      if (!bound.isEmpty()) {
        Map<IExpr, IExpr> inner = new HashMap<IExpr, IExpr>(rulesMap);
        inner.keySet().removeAll(bound);
        IASTMutable result = ast.copy();
        if (scoping) {
          // the initializers are evaluated in the outer scope
          IASTMutable newLocals = ((IAST) ast.arg1()).copy();
          for (int i = 1; i < newLocals.size(); i++) {
            IExpr local = newLocals.get(i);
            if (local.isAST(S.Set, 3) || local.isAST(S.SetDelayed, 3)) {
              newLocals.set(i, F.binaryAST2(local.head(), local.first(),
                  substituteScoped(local.second(), rulesMap)));
            }
          }
          result.set(1, newLocals);
        }
        result.set(2, substituteScoped(ast.arg2(), inner));
        return result;
      }
    }
    IASTMutable result = F.NIL;
    for (int i = 0; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      IExpr temp = substituteScoped(arg, rulesMap);
      if (temp != arg) {
        if (result.isNIL()) {
          result = ast.copy();
        }
        result.set(i, temp);
      }
    }
    return result.isPresent() ? result : ast;
  }

  /** Maps each bound parameter to the argument in the same position. */
  private static Map<IExpr, IExpr> parameterMap(final IAST parameters, final IAST application) {
    int size = parameters.argSize();
    final Map<IExpr, IExpr> rulesMap = size <= 5 //
        ? new OpenFixedSizeMap<IExpr, IExpr>(size * 3 - 1) //
        : new HashMap<IExpr, IExpr>();
    for (int i = 1; i <= size; i++) {
      rulesMap.put(parameters.get(i), application.get(i));
    }
    return rulesMap;
  }

  /**
   * Test that <code>function</code> is a well formed <code>Function(...)</code>, emitting the
   * message which says why if it is not.
   *
   * @return <code>false</code> if a message was emitted
   */
  public static boolean validate(final IAST function, EvalEngine engine) {
    if (function.argSize() > 3 || function.argSize() <= 0) {
      // `1` called with `2` arguments; between `3` and `4` arguments are expected.
      Errors.printMessage(S.Function, "argb",
          F.List(S.Function, F.ZZ(function.argSize()), F.C1, F.C3), engine);
      return false;
    }
    if (function.isAST2()) {
      IExpr parameters = function.arg1();
      if (parameters.isList()) {
        IAST listOfSymbols = (IAST) parameters;
        for (int i = 1; i < listOfSymbols.size(); i++) {
          if (!listOfSymbols.get(i).isSymbol()) {
            // Parameter specification `1` in `2` should be a symbol or a list of symbols.
            Errors.printMessage(S.Function, "flpar", F.List(parameters, function), engine);
            return false;
          }
        }
      } else if (!parameters.isSymbol()) {
        // Parameter specification `1` in `2` should be a symbol or a list of symbols.
        Errors.printMessage(S.Function, "flpar", F.List(parameters, function), engine);
      }
    }
    return true;
  }

  /**
   * The attributes named by the optional third argument of <code>function</code>.
   *
   * @return {@link ISymbol#NOATTRIBUTE} when there is no third argument
   */
  public static int attributes(IAST function, EvalEngine engine) {
    return function.argSize() == 3 //
        ? AttributeFunctions.getSymbolsAsAttributes(function.arg3().makeList(), engine) //
        : ISymbol.NOATTRIBUTE;
  }
}
