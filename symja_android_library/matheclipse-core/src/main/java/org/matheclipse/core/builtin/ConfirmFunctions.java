package org.matheclipse.core.builtin;

import java.util.ArrayDeque;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.exception.ConfirmationException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFastFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Structured error handling: <code>Enclose</code> and the <code>Confirm</code> functions, and the
 * property access of the <code>Failure(tag, assoc)</code> and <code>Success(tag, assoc)</code>
 * objects.
 *
 * <p>
 * A <code>Confirm...</code> function which isn't satisfied throws a
 * {@link ConfirmationException} carrying a <code>Failure("ConfirmationFailed", &lt;|...|&gt;)</code>
 * object. The nearest enclosing <code>Enclose</code> with the same tag returns it, or applies its
 * handler to it. A failed <code>Confirm...</code> without an enclosing <code>Enclose</code> with
 * its tag prints <code>::confirmnotag</code> and returns a <code>Failure("confirmnotag", ...)</code>
 * object; a satisfied one returns its value.
 */
public final class ConfirmFunctions {

  private static final IExpr CONFIRMATION_FAILED = F.stringx("ConfirmationFailed");

  /**
   * The tags of the <code>Enclose</code> calls which are evaluating on this thread, innermost
   * first. An untagged <code>Enclose</code> is {@link S#None}.
   */
  private static final ThreadLocal<ArrayDeque<IExpr>> ENCLOSE_TAGS =
      ThreadLocal.withInitial(ArrayDeque::new);

  private static class Initializer {

    private static void init() {
      S.Enclose.setEvaluator(new Enclose());
      S.Confirm.setEvaluator(new Confirm(S.Confirm, "Confirm"));
      S.ConfirmBy.setEvaluator(new Confirm(S.ConfirmBy, "ConfirmBy"));
      S.ConfirmMatch.setEvaluator(new Confirm(S.ConfirmMatch, "ConfirmMatch"));
      S.ConfirmAssert.setEvaluator(new Confirm(S.ConfirmAssert, "ConfirmAssert"));
      S.ConfirmQuiet.setEvaluator(new Confirm(S.ConfirmQuiet, "ConfirmQuiet"));
      S.Failure.setEvaluator(new FailureObject(S.Failure));
      S.Success.setEvaluator(new FailureObject(S.Success));
    }
  }

  /**
   * <code>Enclose(expr)</code>, <code>Enclose(expr, f)</code>, <code>Enclose(expr, f, tag)</code>
   * - evaluate <code>expr</code>; a failed confirmation inside it gives its <code>Failure</code>
   * object, or <code>f</code> applied to it. A string <code>f</code> names a property of the
   * failure.
   */
  private static final class Enclose extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int argSize = ast.argSize();
      if (argSize < 1 || argSize > 3) {
        return engine.checkBuiltinArgsSize(ast, this);
      }
      final IExpr tag = argSize == 3 ? engine.evaluate(ast.arg3()) : S.None;
      ArrayDeque<IExpr> tags = ENCLOSE_TAGS.get();
      IExpr failure;
      tags.push(tag);
      try {
        return engine.evaluate(ast.arg1());
      } catch (ConfirmationException ce) {
        if (!ce.getTag().equals(tag)) {
          throw ce;
        }
        failure = ce.getFailure();
      } finally {
        tags.pop();
      }
      if (argSize == 1) {
        return failure;
      }
      IExpr handler = engine.evaluate(ast.arg2());
      if (handler.isString()) {
        if (!hasProperty(failure, handler.toString())) {
          // `1` is not a valid property.
          Errors.printMessage(S.Enclose, "noprop", F.list(handler), engine);
          return failure;
        }
        return property(failure, handler);
      }
      return engine.evaluate(F.unaryAST1(handler, failure));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDFIRST);
    }
  }

  /**
   * <code>Confirm(expr, info, tag)</code>, <code>ConfirmBy(expr, f, info, tag)</code>,
   * <code>ConfirmMatch(expr, form, info, tag)</code>, <code>ConfirmAssert(test, info, tag)</code>
   * and <code>ConfirmQuiet(expr, info, tag)</code>. The <code>info</code> and <code>tag</code>
   * arguments are optional.
   */
  private static final class Confirm extends AbstractCoreFunctionEvaluator
      implements IFastFunctionEvaluator {
    private final IBuiltInSymbol kind;

    /** The name of the function, for the <code>"ConfirmationType"</code> of a failure. */
    private final String name;

    /** The number of arguments before the optional <code>info</code> argument. */
    private final int fixed;

    Confirm(IBuiltInSymbol kind, String name) {
      this.kind = kind;
      this.name = name;
      this.fixed = (kind == S.ConfirmBy || kind == S.ConfirmMatch) ? 2 : 1;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int argSize = ast.argSize();
      if (argSize < fixed || argSize > fixed + 2) {
        return engine.checkBuiltinArgsSize(ast, this);
      }
      final IExpr information =
          argSize >= fixed + 1 ? engine.evaluate(ast.get(fixed + 1)) : S.Null;
      final IExpr tag = argSize == fixed + 2 ? engine.evaluate(ast.get(fixed + 2)) : S.None;
      if (!ENCLOSE_TAGS.get().contains(tag)) {
        // `1` has no tag or surrounding Enclose.
        Errors.printMessage(kind, "confirmnotag", F.list(kind), engine);
        IASTAppendable rules = F.ListAlloc(3);
        rules.append(F.RuleDelayed(F.stringx("MessageTemplate"),
            F.binaryAST2(S.MessageName, kind, F.stringx("confirmnotag"))));
        rules.append(F.RuleDelayed(F.stringx("MessageParameters"), F.list(kind)));
        rules.append(F.Rule(F.stringx("HeldInput"), F.Hold(ast)));
        return F.binaryAST2(S.Failure, F.stringx("confirmnotag"), F.assoc(rules));
      }
      final IAST failure;
      final IExpr value;
      if (kind == S.ConfirmQuiet) {
        final boolean quiet = engine.isQuietMode();
        final int messagesBefore = engine.lineMessages().argSize();
        final Errors.CheckScope scope = Errors.enterCheck(true, null);
        try {
          engine.setQuietMode(false);
          value = engine.evaluate(ast.arg1());
        } finally {
          engine.setQuietMode(quiet);
          Errors.exitCheck(scope);
        }
        if (!scope.isFired()) {
          return value;
        }
        // the first message generated by the expression
        IAST messages = engine.lineMessages();
        IExpr messageName = messages.argSize() > messagesBefore
            ? F.Hold(messages.get(messagesBefore + 1).first())
            : F.Hold(F.Missing(S.NotAvailable));
        failure = confirmationFailure("`` produced a message on evaluation.",
            F.list(F.HoldForm(ast.arg1())), F.Hold(ast.arg1()), information,
            F.Rule(F.stringx("HeldExpression"), F.Hold(ast.arg1())),
            F.Rule(F.stringx("HeldMessageName"), messageName));
      } else if (kind == S.ConfirmAssert) {
        value = engine.evaluate(ast.arg1());
        if (value.isTrue()) {
          return value;
        }
        failure = confirmationFailure("Assertion `` failed.", F.list(F.HoldForm(ast.arg1())),
            value, information, F.RuleDelayed(F.stringx("Test"), value),
            F.Rule(F.stringx("HeldTest"), F.Hold(ast.arg1())));
      } else {
        value = engine.evaluate(ast.arg1());
        if (kind == S.Confirm) {
          if (!isFailure(value)) {
            return value;
          }
          failure = information.isPresent() && information != S.Null
              ? confirmationFailure(information.toString(), F.CEmptyList, value, information,
                  F.NIL, F.NIL)
              : confirmationFailure("`` encountered.", F.list(value), value, information, F.NIL,
                  F.NIL);
        } else if (kind == S.ConfirmBy) {
          IExpr f = engine.evaluate(ast.arg2());
          if (engine.evaluate(F.unaryAST1(f, value)).isTrue()) {
            return value;
          }
          failure = confirmationFailure("``[``] did not return True.", F.list(f, value), value,
              information, F.Rule(F.stringx("Function"), f), F.NIL);
        } else {
          IExpr form = ast.arg2();
          if (engine.evalPatternMatcher(form).test(value, engine)) {
            return value;
          }
          failure = confirmationFailure("`` does not match ``.", F.list(value, form), value,
              information, F.Rule(F.stringx("Pattern"), form), F.NIL);
        }
      }
      throw new ConfirmationException(failure, tag);
    }

    /**
     * The <code>Failure("ConfirmationFailed", &lt;|...|&gt;)</code> object of a failed
     * confirmation.
     *
     * @param extra1 an additional rule, or <code>F.NIL</code>
     * @param extra2 an additional rule, or <code>F.NIL</code>
     */
    private IAST confirmationFailure(String template, IAST parameters, IExpr expression,
        IExpr information, IExpr extra1, IExpr extra2) {
      IASTAppendable rules = F.ListAlloc(7);
      rules.append(F.Rule(F.stringx("MessageTemplate"), F.stringx(template)));
      // a delayed rule keeps the parameters and the expression from being evaluated again; an
      // empty list of parameters is an ordinary rule
      rules.append(parameters.isEmpty() ? F.Rule(F.stringx("MessageParameters"), parameters)
          : F.RuleDelayed(F.stringx("MessageParameters"), parameters));
      rules.append(F.Rule(F.stringx("ConfirmationType"), F.stringx(name)));
      rules.append(F.RuleDelayed(F.stringx("Expression"), expression));
      rules.append(F.Rule(F.stringx("Information"), information));
      if (extra1.isPresent()) {
        rules.append(extra1);
      }
      if (extra2.isPresent()) {
        rules.append(extra2);
      }
      return F.binaryAST2(S.Failure, CONFIRMATION_FAILED, F.assoc(rules));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return fixed == 2 ? ARGS_2_4 : ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /**
   * The inert objects <code>Failure(tag, assoc)</code> and <code>Success(tag, assoc)</code> and
   * their properties <code>obj("prop")</code>.
   */
  private static final class FailureObject extends AbstractEvaluator {
    private final IBuiltInSymbol head;

    FailureObject(IBuiltInSymbol head) {
      this.head = head;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr object = ast.head();
      if (object.isAST(head, 3) && object.second().isAssociation() && ast.isAST1()) {
        return property(object, ast.arg1());
      }
      return F.NIL;
    }
  }

  /**
   * Whether a <code>Failure</code> object has the property: <code>"Tag"</code>,
   * <code>"Message"</code> or a key of its association.
   */
  private static boolean hasProperty(IExpr object, String name) {
    if (name.equals("Tag") || name.equals("Message")) {
      return true;
    }
    if (!object.isAST(S.Failure, 3) || !object.second().isAssociation()) {
      return false;
    }
    return ((IAssociation) object.second()).getValue(F.stringx(name), () -> F.NIL).isPresent();
  }

  /** Is <code>value</code> one of the values <code>Confirm</code> treats as a failure? */
  private static boolean isFailure(IExpr value) {
    return value == S.$Failed || value == S.$Aborted || value.isAST(S.Failure)
        || value.isAST(S.Missing);
  }

  /**
   * A property of a <code>Failure(tag, assoc)</code> or <code>Success(tag, assoc)</code> object:
   * <code>"Tag"</code>, <code>"Message"</code>, <code>"Properties"</code> or a key of its
   * association. For anything else the object itself.
   */
  private static IExpr property(IExpr object, IExpr property) {
    if (!object.isAST(S.Failure, 3) && !object.isAST(S.Success, 3)
        || !object.second().isAssociation() || !property.isString()) {
      return object;
    }
    IAssociation assoc = (IAssociation) object.second();
    String name = property.toString();
    switch (name) {
      case "Tag":
        return object.first();
      case "Message":
        return F.stringx(message(assoc));
      case "Properties": {
        java.util.TreeSet<String> names = new java.util.TreeSet<String>();
        names.add("Message");
        names.add("Tag");
        for (int i = 1; i < assoc.size(); i++) {
          IExpr key = assoc.getRule(i).first();
          if (key.isString()) {
            names.add(key.toString());
          }
        }
        IASTAppendable list = F.ListAlloc(names.size());
        for (String n : names) {
          list.append(F.stringx(n));
        }
        return list;
      }
      default:
        return assoc.getValue(property, () -> F.Missing(S.NotAvailable, property));
    }
  }

  /**
   * The <code>"MessageTemplate"</code> of the association with its <code>"MessageParameters"</code>
   * filled in: <code>``</code> takes the next parameter, <code>`n`</code> the n-th one.
   */
  private static String message(IAssociation assoc) {
    IExpr template = assoc.getValue(F.stringx("MessageTemplate"), () -> F.NIL);
    if (!template.isString()) {
      return template.isPresent() ? template.toString() : "";
    }
    IExpr parameters = assoc.getValue(F.stringx("MessageParameters"), () -> F.NIL);
    IAST list = parameters.isList() ? (IAST) parameters : F.CEmptyList;
    String text = template.toString();
    StringBuilder buf = new StringBuilder();
    int next = 1;
    int i = 0;
    while (i < text.length()) {
      char c = text.charAt(i);
      if (c == '`') {
        int end = text.indexOf('`', i + 1);
        if (end > i) {
          String inner = text.substring(i + 1, end);
          int index = next;
          if (!inner.isEmpty()) {
            try {
              index = Integer.parseInt(inner.trim());
            } catch (NumberFormatException nfe) {
              index = -1;
            }
          } else {
            next++;
          }
          if (index >= 1 && index < list.size()) {
            IExpr p = list.get(index);
            buf.append(p.isAST(S.HoldForm, 2) ? p.first().toString() : p.toString());
          }
          i = end + 1;
          continue;
        }
      }
      buf.append(c);
      i++;
    }
    return buf.toString();
  }

  public static void initialize() {
    Initializer.init();
  }

  private ConfirmFunctions() {}
}
