package org.matheclipse.core.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * What an <code>Entity("Element", "Iron")</code> means, and which function can answer for it.
 *
 * <p>
 * An entity names a thing of a type, and an {@link S#EntityProperty} one of the things that may be
 * asked about it. Neither is a value: the answers live in the data functions -
 * <code>ElementData</code>, <code>PlanetData</code>, <code>StarData</code> - which are keyed by
 * plain names. This class is the one place that takes the wrapper off and the one place that knows
 * which function answers for which type, so that every data function reads an entity the same way
 * and {@link S#EntityValue} can reach all of them without knowing any of them.
 *
 * <p>
 * The registry is filled where the evaluators are installed, so a type cannot be registered without
 * the function that answers it; a module that is not on the classpath simply registers nothing and
 * its types stay unknown.
 *
 * <p>
 * <b>One way only:</b> <code>EntityValue</code> and <code>EntityList</code> call the data
 * functions, and a data function never calls them back. Every name handed to a data function here
 * has already been unwrapped, so nothing can bounce between the two.
 */
public final class Entities {

  /** The data function answering for each type, filled as the evaluators are installed. */
  private static final Map<String, IBuiltInSymbol> TYPES =
      new ConcurrentHashMap<String, IBuiltInSymbol>();

  private Entities() {}

  // ---------------------------------------------------------------- the wrappers

  /**
   * The name an <code>Entity(type, name)</code> stands for.
   *
   * <p>
   * The type has to match: an entity of another type is returned untouched, so that a function
   * handed one reports it as the thing it cannot read rather than quietly looking up a name of the
   * wrong kind. Anything which is not an entity at all - a plain name, an atomic number - is
   * returned untouched too, which is what lets a caller apply this to every argument.
   */
  public static IExpr nameOf(IExpr expr, IExpr type) {
    return expr.isAST(S.Entity, 3) && type.equals(expr.first()) ? ((IAST) expr).arg2() : expr;
  }

  public static IExpr nameOf(IExpr expr, String type) {
    return nameOf(expr, F.stringx(type));
  }

  /** The property an <code>EntityProperty(type, property)</code> names, under the same rule. */
  public static IExpr propertyOf(IExpr expr, IExpr type) {
    return expr.isAST(S.EntityProperty, 3) && type.equals(expr.first()) ? ((IAST) expr).arg2()
        : expr;
  }

  public static IExpr propertyOf(IExpr expr, String type) {
    return propertyOf(expr, F.stringx(type));
  }

  /** The same as {@link #propertyOf(IExpr, IExpr)}, for a caller that compares property names. */
  public static String propertyName(IExpr expr, IExpr type) {
    return propertyOf(expr, type).toString();
  }

  /** The type an entity, an entity class or a property belongs to, or {@code null}. */
  public static String typeOf(IExpr expr) {
    if ((expr.isAST(S.Entity, 3) || expr.isAST(S.EntityClass, 3)
        || expr.isAST(S.EntityProperty, 3)) && expr.first().isString()) {
      return expr.first().toString();
    }
    return null;
  }

  public static IAST entity(IExpr type, IExpr name) {
    return F.binaryAST2(S.Entity, type, name);
  }

  public static IAST entity(String type, IExpr name) {
    return entity(F.stringx(type), name);
  }

  public static IAST property(String type, String name) {
    return F.binaryAST2(S.EntityProperty, F.stringx(type), F.stringx(name));
  }

  // ---------------------------------------------------------------- the registry

  /**
   * Record that {@code dataFunction} answers for the entities of {@code type}.
   *
   * @param type the type as it is written in an entity, <code>"Element"</code> and not
   *        <code>"element"</code>
   */
  public static void register(String type, IBuiltInSymbol dataFunction) {
    TYPES.put(type, dataFunction);
  }

  /** The function answering for {@code type}, or {@code null} when nothing registered one. */
  public static IBuiltInSymbol dataFunction(String type) {
    return type == null ? null : TYPES.get(type);
  }

  public static IBuiltInSymbol dataFunction(IExpr type) {
    return type.isString() ? dataFunction(type.toString()) : null;
  }

  /**
   * Ask a data function, treating an answer it could not give as no answer.
   *
   * <p>
   * A function which cannot answer leaves its own call standing, and so does a symbol whose
   * evaluator was never installed because its module is absent. The two are the same thing from
   * here - there is nothing to report - so both come back as {@link F#NIL} and the caller stays
   * unevaluated, leaving whatever message the data function itself printed as the explanation.
   */
  public static IExpr ask(IBuiltInSymbol dataFunction, EvalEngine engine, IExpr... arguments) {
    IExpr result = engine.evaluate(F.ast(arguments, dataFunction));
    return result.isAST(dataFunction) ? F.NIL : result;
  }

  /**
   * Every entity of a type, from the data function's own no-argument form.
   *
   * <p>
   * A data function which already answers with entities - as <code>ElementData()</code> does - is
   * passed through; one which answers with plain names has them wrapped here, so that a function
   * joining the convention need not be rewritten first.
   *
   * @return {@link F#NIL} when the type is unknown or its function cannot list its entities
   */
  public static IAST entityList(String type, EvalEngine engine) {
    IBuiltInSymbol dataFunction = dataFunction(type);
    if (dataFunction == null) {
      return F.NIL;
    }
    IExpr all = ask(dataFunction, engine);
    if (!all.isList()) {
      return F.NIL;
    }
    IAST names = (IAST) all;
    IASTAppendable entities = F.ListAlloc(names.argSize());
    for (int i = 1; i <= names.argSize(); i++) {
      IExpr name = names.get(i);
      entities.append(name.isAST(S.Entity, 3) ? name : entity(type, name));
    }
    return entities;
  }
}
