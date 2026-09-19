package org.matheclipse.core.expression.data;

import java.util.Arrays;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * What every <code>FittedModel</code> answers, whichever kind of fit produced it.
 *
 * <p>
 * A model is asked for a property by name - <code>model["RSquared"]</code> - or evaluated at a
 * point - <code>model[2.5]</code>, which is its best fit function at that point. A name the model
 * does not know prints <code>FittedModel::elmntavs</code> with the nearest name it does know, as
 * the reference implementation does, and leaves the call unevaluated.
 *
 * @param <T> the object holding the fit
 */
public abstract class AbstractFittedModelExpr<T> extends DataExpr<T> {

  private static final long serialVersionUID = 4386241931412806219L;

  protected AbstractFittedModelExpr(T data) {
    super(S.FittedModel, data);
  }

  /** The names {@link #property(String, EvalEngine)} answers for, sorted. */
  protected abstract String[] propertyNames();

  /** The value of one property, or {@link F#NIL} when this model has no such property. */
  protected abstract IExpr property(String name, EvalEngine engine);

  /**
   * The independent variables, in the order an evaluation at a point takes its arguments, or
   * {@link F#NIL} when the model cannot be evaluated at a point.
   */
  protected abstract IAST fitVariables();

  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (!(ast.head() instanceof AbstractFittedModelExpr)) {
      return F.NIL;
    }
    if (ast.isAST1() && ast.arg1().isString()) {
      String name = ast.arg1().toString();
      if ("Properties".equals(name)) {
        return F.mapRange(0, propertyNames().length, i -> F.stringx(propertyNames()[i]));
      }
      IExpr value = property(name, engine);
      if (value.isPresent()) {
        return value;
      }
      // "`1`" is not an available property. Did you mean "`2`" instead?
      return Errors.printMessage(S.FittedModel, "elmntavs",
          F.list(ast.arg1(), F.stringx(nearestProperty(name))), engine);
    }
    IAST variables = fitVariables();
    if (variables.isPresent() && ast.argSize() == variables.argSize()) {
      // the best fit function at a point
      IASTAppendable rules = F.ListAlloc(variables.argSize());
      for (int i = 1; i <= variables.argSize(); i++) {
        rules.append(F.Rule(variables.get(i), ast.get(i)));
      }
      return engine.evaluate(F.subst(normal(false), rules));
    }
    return F.NIL;
  }

  /**
   * The property name closest to one that was not recognised - <code>"Properties"</code> itself
   * included, which is what a misspelt request for the list of names most likely was.
   */
  private String nearestProperty(String name) {
    String[] names = Arrays.copyOf(propertyNames(), propertyNames().length + 1);
    names[names.length - 1] = "Properties";
    LevenshteinDistance distance = LevenshteinDistance.getDefaultInstance();
    String nearest = names[0];
    int best = Integer.MAX_VALUE;
    for (String candidate : names) {
      int d = distance.apply(name, candidate);
      if (d < best) {
        best = d;
        nearest = candidate;
      }
    }
    return nearest;
  }

  @Override
  public int hierarchy() {
    return FITTEDMODELID;
  }
}
