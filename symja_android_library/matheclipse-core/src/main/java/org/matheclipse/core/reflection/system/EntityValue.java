package org.matheclipse.core.reflection.system;

import org.matheclipse.core.data.Entities;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>EntityValue(entity, property)</code> - what the data function of the entity's type says
 * about it.
 *
 * <p>
 * The entity carries its own type, so this reaches <code>ElementData</code>,
 * <code>PlanetData</code> and every other registered data function without naming any of them; see
 * {@link Entities}. Either argument may be a list, and an <code>EntityClass</code> stands for the
 * entities it contains.
 *
 * <p>
 * A list of properties is always a list of properties, never one property qualified by something
 * else: a date, as <code>PlanetData(planet, {"HelioCoordinates", date})</code> takes one, has to be
 * asked of the data function itself.
 *
 * <p>
 * A property the data function cannot answer leaves the whole call unevaluated rather than putting
 * a hole in the result, so that the message the data function printed - which names the property it
 * did not recognise - is the explanation the reader gets.
 */
public class EntityValue extends AbstractFunctionEvaluator {

  public EntityValue() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr entitySpec = ast.arg1();
    if (entitySpec.isString()) {
      // a bare type asks the data function itself, which is how the property list is reached:
      // EntityValue("Element", "Properties")
      IBuiltInSymbol byType = Entities.dataFunction(entitySpec);
      return byType == null ? F.NIL : Entities.ask(byType, engine, ast.arg2());
    }
    if (entitySpec.isAST(S.EntityClass, 3)) {
      IExpr members = engine.evaluate(F.EntityList(entitySpec));
      if (!members.isList()) {
        return F.NIL;
      }
      entitySpec = members;
    }

    boolean severalEntities = entitySpec.isList();
    IAST entities = severalEntities ? (IAST) entitySpec : F.List(entitySpec);
    if (entities.argSize() == 0) {
      return F.CEmptyList;
    }
    String type = null;
    IASTAppendable names = F.ListAlloc(entities.argSize());
    for (IExpr each : entities) {
      String eachType = Entities.typeOf(each);
      if (!each.isAST(S.Entity, 3) || eachType == null
          || (type != null && !type.equals(eachType))) {
        // not an entity, or a collection of more than one kind of thing, which no single data
        // function answers for
        return F.NIL;
      }
      type = eachType;
      names.append(((IAST) each).arg2());
    }
    IBuiltInSymbol dataFunction = Entities.dataFunction(type);
    if (dataFunction == null) {
      return F.NIL;
    }

    IExpr propertySpec = ast.arg2();
    boolean severalProperties = propertySpec.isList();
    IAST properties = severalProperties ? (IAST) propertySpec : F.List(propertySpec);
    IASTAppendable rows = F.ListAlloc(names.argSize());
    for (IExpr name : names) {
      IASTAppendable row = F.ListAlloc(properties.argSize());
      for (IExpr property : properties) {
        IExpr value = Entities.ask(dataFunction, engine, name, Entities.propertyOf(property, type));
        if (value.isNIL()) {
          return F.NIL;
        }
        row.append(value);
      }
      // one property asked for is one value answered, not a list holding it
      rows.append(severalProperties ? row : row.arg1());
    }
    return severalEntities ? rows : rows.arg1();
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }
}
