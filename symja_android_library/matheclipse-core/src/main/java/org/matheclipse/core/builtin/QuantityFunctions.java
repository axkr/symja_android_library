package org.matheclipse.core.builtin;

import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.statistics.ICDF;
import org.matheclipse.core.interfaces.statistics.IContinuousDistribution;
import org.matheclipse.core.interfaces.statistics.IPDF;
import org.matheclipse.core.interfaces.statistics.IRandomVariate;
import org.matheclipse.core.interfaces.statistics.IStatistics;
import org.matheclipse.core.units.Units;

/**
 * Quantity/unit builtin functions on top of the {@link org.matheclipse.core.units} engine. A
 * quantity is a plain AST {@code Quantity[magnitude, unitExpr]} whose unit expression is algebra
 * over string atoms with canonical WMA names ({@code "Meters"}, {@code "Kilometers"/"Hours"}). See
 * {@code QUANTITY_REFACTORING_PLAN.md} in the repository root.
 */
public class QuantityFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.CompatibleUnitQ.setEvaluator(new CompatibleUnitQ());
      S.KnownUnitQ.setEvaluator(new KnownUnitQ());
      S.Quantity.setEvaluator(new Quantity());
      S.QuantityMagnitude.setEvaluator(new QuantityMagnitude());
      S.QuantityUnit.setEvaluator(new QuantityUnit());
      S.QuantityArray.setEvaluator(new QuantityArray());
      S.UnitConvert.setEvaluator(new UnitConvert());
      S.UnitDimensions.setEvaluator(new UnitDimensions());
      S.UnitSimplify.setEvaluator(new UnitSimplify());
      S.CommonUnits.setEvaluator(new CommonUnits());
      S.QuantityVariable.setEvaluator(new QuantityVariable());
      S.QuantityVariableCanonicalUnit.setEvaluator(new QuantityVariableCanonicalUnit());
      S.QuantityVariableDimensions.setEvaluator(new QuantityVariableDimensions());
      S.QuantityVariablePhysicalQuantity.setEvaluator(new QuantityVariablePhysicalQuantity());
      S.QuantityVariableIdentifier.setEvaluator(new QuantityVariableIdentifier());
      S.DimensionalCombinations.setEvaluator(new DimensionalCombinations());
      S.NondimensionalizationTransform.setEvaluator(new NondimensionalizationTransform());
      S.QuantityDistribution.setEvaluator(new QuantityDistribution());
      S.QuantityForm.setEvaluator(new QuantityForm());
    }
  }

  /** Renders a base-unit dimension map as the sorted WMA {{dimensionName, exponent}, ...} list. */
  private static IExpr dimensionsList(Map<String, IRational> dims) {
    java.util.TreeMap<String, IRational> byDimension = new java.util.TreeMap<>();
    for (Map.Entry<String, IRational> e : dims.entrySet()) {
      String dimension = Units.BASE_DIMENSIONS.get(e.getKey());
      byDimension.put(dimension == null ? e.getKey() : dimension, e.getValue());
    }
    IASTAppendable result = F.ListAlloc(byDimension.size());
    for (Map.Entry<String, IRational> e : byDimension.entrySet()) {
      result.append(F.list(F.stringx(e.getKey()), e.getValue()));
    }
    return result;
  }

  /**
   * QuantityVariable(var, physicalQuantity) - a symbolic variable annotated with a physical
   * quantity. The expression stays inert; the other {@code QuantityVariable*} functions read it.
   */
  private static final class QuantityVariable extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** QuantityVariableCanonicalUnit(qv) - the canonical unit of the quantity variable. */
  private static final class QuantityVariableCanonicalUnit extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr unit = Units.quantityVariableUnit(ast.arg1());
      if (unit.isNIL()) {
        return F.NIL;
      }
      return engine.evaluate(unit);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** QuantityVariableDimensions(qv) - the base dimensions of the quantity variable. */
  private static final class QuantityVariableDimensions extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr unit = Units.quantityVariableUnit(ast.arg1());
      if (unit.isNIL()) {
        return F.NIL;
      }
      Map<String, IRational> dims = Units.dimensions(unit);
      return dims == null ? F.NIL : dimensionsList(dims);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * QuantityVariablePhysicalQuantity(qv) - the physical quantity part; the optional second argument
   * selects the form {@code "CanonicalName"} (default) or {@code "Entity"}.
   */
  private static final class QuantityVariablePhysicalQuantity extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr spec;
      if (arg1.isAST(S.QuantityVariable, 2)) {
        spec = arg1.first();
      } else if (arg1.isAST(S.QuantityVariable, 3)) {
        spec = ((IAST) arg1).arg2();
      } else {
        return F.NIL;
      }
      if (spec.isString()) {
        String canonical =
            org.matheclipse.core.units.PhysicalQuantities.get().canonicalName(spec.toString());
        if (canonical == null) {
          return F.NIL;
        }
        spec = F.stringx(canonical);
      }
      if (ast.isAST2() && ast.arg2().isString() && ast.arg2().toString().equals("Entity")) {
        return F.Entity(F.stringx("PhysicalQuantity"), spec);
      }
      return spec;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** QuantityVariableIdentifier(qv) - the label part of {@code QuantityVariable(var, pq)}. */
  private static final class QuantityVariableIdentifier extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.QuantityVariable, 3)) {
        return arg1.first();
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  /**
   * QuantityArray(magnitudes, unit) - a structured array of magnitudes sharing a unit (or a list of
   * per-position units at the deepest level). {@code Normal} expands to an array of
   * {@code Quantity} objects.
   */
  private static final class QuantityArray extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr magnitudes = ast.arg1();
        if (!magnitudes.isList()) {
          if (magnitudes.isNumber()) {
            // scalar degenerates to an ordinary quantity
            return F.Quantity(magnitudes, ast.arg2());
          }
          return F.NIL;
        }
        IExpr spec = ast.arg2();
        IExpr normalized;
        if (spec.isList()) {
          IAST list = (IAST) spec;
          org.matheclipse.core.interfaces.IASTAppendable rebuilt = F.NIL;
          for (int i = 1; i < list.size(); i++) {
            IExpr norm = Units.normalize(list.get(i));
            if (norm.isNIL()) {
              return Errors.printMessage(S.QuantityArray, "unkunit", F.list(list.get(i)), engine);
            }
            IExpr evaluated = engine.evaluate(norm);
            if (!evaluated.equals(list.get(i)) && rebuilt.isNIL()) {
              rebuilt = list.copyAppendable();
            }
            if (rebuilt.isPresent()) {
              rebuilt.set(i, evaluated);
            }
          }
          normalized = rebuilt.isPresent() ? rebuilt : spec;
        } else {
          IExpr norm = Units.normalize(spec);
          if (norm.isNIL()) {
            return Errors.printMessage(S.QuantityArray, "unkunit", F.list(spec), engine);
          }
          normalized = engine.evaluate(norm);
        }
        if (normalized.equals(ast.arg2())) {
          return F.NIL; // fixed point
        }
        return F.binaryAST2(S.QuantityArray, magnitudes, normalized);
      }
      // 1-arg form: compact a flat vector of same-unit quantities
      if (ast.isAST1() && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        if (list.argSize() == 0) {
          return F.NIL;
        }
        IExpr unit = null;
        org.matheclipse.core.interfaces.IASTAppendable magnitudes = F.ListAlloc(list.argSize());
        for (int i = 1; i < list.size(); i++) {
          if (!list.get(i).isQuantity()) {
            return F.NIL;
          }
          IAST quantity = (IAST) list.get(i);
          if (unit == null) {
            unit = quantity.arg2();
          } else if (!unit.equals(quantity.arg2())) {
            return F.NIL;
          }
          magnitudes.append(quantity.arg1());
        }
        return F.binaryAST2(S.QuantityArray, magnitudes, unit);
      }
      return F.NIL;
    }

    /** Expands a QuantityArray into a plain array of Quantity objects (used by Normal). */
    public static IExpr normal(IAST quantityArray) {
      IExpr magnitudes = quantityArray.arg1();
      IExpr spec = quantityArray.arg2();
      if (!magnitudes.isList()) {
        return F.NIL;
      }
      return expand((IAST) magnitudes, spec);
    }

    private static IExpr expand(IAST magnitudes, IExpr spec) {
      org.matheclipse.core.interfaces.IASTAppendable result = F.ListAlloc(magnitudes.argSize());
      for (int i = 1; i < magnitudes.size(); i++) {
        IExpr element = magnitudes.get(i);
        if (element.isList()) {
          IExpr nested = expand((IAST) element, spec);
          if (nested.isNIL()) {
            return F.NIL;
          }
          result.append(nested);
        } else if (spec.isList()) {
          // per-position units at the deepest level
          IAST units = (IAST) spec;
          if (magnitudes.argSize() != units.argSize()) {
            return F.NIL;
          }
          result.append(F.Quantity(element, units.get(i)));
        } else {
          result.append(F.Quantity(element, spec));
        }
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDREST, Attribute.NHOLDREST);
    }
  }

  /** Expands {@code QuantityArray(...)} to an array of quantities; used by {@code Normal}. */
  public static IExpr quantityArrayNormal(IAST quantityArray) {
    return QuantityArray.normal(quantityArray);
  }

  /**
   * A {@code QuantityArray} as its array of quantities, or {@code expr} unchanged.
   *
   * <p>
   * {@code QuantityArray(mags, unit)} is a plain two argument expression, so anything measuring it
   * structurally sees a length of two rather than the array it stands for: {@code Dimensions} of a
   * four element one reported <code>{2}</code>, and {@code MatrixQ} of a 2x2 one was
   * <code>False</code>.
   *
   * <p>
   * This is applied by the functions that only <i>inspect</i> an array - Dimensions, ArrayDepth,
   * TensorRank, Length, ArrayQ, MatrixQ, VectorQ. It is deliberately NOT applied inside
   * {@link org.matheclipse.core.eval.LinearAlgebraUtil#dimensions}: dozens of callers take those
   * dimensions and then index the expression as a nested list, and on a QuantityArray position 2
   * is the unit, not a row.
   */
  /**
   * Plot data with every {@link S#Quantity} replaced by its magnitude.
   *
   * <p>
   * A flat list of quantities is the value axis. A list of rows is handled per column, so a list
   * of <code>{x,y}</code> pairs keeps a separate unit per axis - the only coherent reading, since
   * a single row mixes the two dimensions.
   *
   * <p>
   * <code>targetUnits</code> is the {@link S#TargetUnits} option: {@link S#Automatic} keeps each
   * column in the unit of its own first element; a single unit names the value axis (y in 2D, z in
   * 3D); a list of units is positional, one per column. Only the CONVERSION half of TargetUnits is
   * implemented - the axis is not relabelled with the unit.
   *
   * <p>
   * Without this the plotting pipeline reaches {@code toDoubleVectorIgnore}, which cannot turn a
   * quantity into a machine number and drops it in silence: plotting a list of quantities produced
   * a <code>Graphics</code> with no <code>Line</code> primitive at all.
   *
   * @return the data with magnitudes substituted, or <code>data</code> unchanged - including when
   *         a unit is incompatible, so that nothing is silently mixed
   */
  public static IExpr quantityPlotMagnitudes(IExpr data, IExpr targetUnits, EvalEngine engine) {
    IExpr normalized = normalizeQuantityArray(data);
    if (!normalized.isList() || normalized.argSize() == 0) {
      return data;
    }
    IAST list = (IAST) normalized;
    if (list.forAll(x -> x.isQuantity())) {
      IExpr magnitudes =
          magnitudesInUnit(list, targetUnitFor(targetUnits, 0, 0), engine);
      return magnitudes.isPresent() ? magnitudes : data;
    }
    // a list of rows: convert column by column
    final int width = list.arg1().argSize();
    if (width > 0 && list.forAll(x -> x.isList() && x.argSize() == width)
        && list.exists(row -> ((IAST) row).exists(x -> x.isQuantity()))) {
      IAST firstRow = (IAST) list.arg1();
      IASTAppendable rows = F.ListAlloc(list.argSize());
      for (int row = 1; row < list.size(); row++) {
        IAST source = (IAST) list.get(row);
        IASTAppendable rowValues = F.ListAlloc(width);
        for (int column = 1; column <= width; column++) {
          IExpr value = source.get(column);
          if (value.isQuantity()) {
            IExpr target = targetUnitFor(targetUnits, column, width);
            IExpr reference = target.isPresent() ? referenceQuantity(target, engine)
                : (firstRow.get(column).isQuantity() ? firstRow.get(column) : F.NIL);
            if (reference.isNIL()) {
              return data;
            }
            IExpr magnitude = org.matheclipse.core.units.QuantityOps
                .magnitudeInFirstUnit((IAST) reference, (IAST) value, engine);
            if (magnitude.isNIL()) {
              // incompatible units in one column: leave the data alone rather than mix them
              return data;
            }
            value = magnitude;
          }
          rowValues.append(value);
        }
        rows.append(rowValues);
      }
      return rows;
    }
    return data;
  }

  /**
   * A plot function rewritten to yield plain magnitudes, when it turns out to be quantity valued.
   *
   * <p>
   * Probing is the only way to tell. A function that returns quantities is usually not
   * syntactically a {@link S#Quantity} until its variable is bound: with
   * {@code f(t_) := Quantity(t,"Meters")}, the expression {@code f(x)} is just an unevaluated
   * {@code f(x)}. So the function is evaluated once at {@code samplePoint}, and only if that
   * yields a quantity is it wrapped in {@link S#QuantityMagnitude} - which also performs the
   * {@link S#TargetUnits} conversion, since it takes the target unit as its second argument.
   *
   * <p>
   * Without this the plot samples a quantity, {@code evalfNaN} cannot turn it into a machine
   * number, and every point is dropped: the picture came back empty.
   *
   * @param samplePoint rules binding each plot variable to a value inside its range
   * @return the rewritten function, or <code>function</code> unchanged when it is not quantity
   *         valued
   */
  public static IExpr quantityPlotFunction(IExpr function, IAST samplePoint, IExpr targetUnits,
      EvalEngine engine) {
    return quantityPlotFunction(function, samplePoint, targetUnits, 0, 0, engine);
  }

  /**
   * As {@link #quantityPlotFunction(IExpr, IAST, IExpr, EvalEngine)}, for one component of a
   * parametric curve, whose components carry a unit each.
   *
   * @param component the 1-based component, or <code>0</code> for a value-axis function
   * @param componentCount the number of components, or <code>0</code> for a value-axis function
   */
  public static IExpr quantityPlotFunction(IExpr function, IAST samplePoint, IExpr targetUnits,
      int component, int componentCount, EvalEngine engine) {
    IExpr probe;
    try {
      probe = engine.evaluate(F.subst(function, samplePoint));
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return function;
    }
    if (!probe.isQuantity()) {
      return function;
    }
    IExpr target = targetUnitFor(targetUnits, component, componentCount);
    return F.QuantityMagnitude(function, target.isPresent() ? target : ((IAST) probe).arg2());
  }

  /**
   * A pair of plot range endpoints reduced to plain magnitudes.
   *
   * <p>
   * Mathematica binds the plot variable to a plain number, not to a quantity:
   * {@code Plot[Quantity[x^2,"Meters"], {x, Quantity[0,"Seconds"], Quantity[2,"Seconds"]}]} draws
   * y == x squared over x from 0 to 2, which it could not if {@code x} carried the second. So the
   * range is simply stripped, both endpoints expressed in the first one's unit.
   *
   * @return <code>{min, max}</code> as magnitudes, or {@link F#NIL} unless both endpoints are
   *         quantities in compatible units
   */
  public static IAST quantityPlotRange(IExpr min, IExpr max, IExpr targetUnits,
      EvalEngine engine) {
    if (!min.isQuantity() || !max.isQuantity()) {
      return F.NIL;
    }
    // the range is the x axis, which is the first entry of a TargetUnits list
    IExpr target = targetUnitFor(targetUnits, 1, 2);
    IExpr reference = target.isPresent() ? referenceQuantity(target, engine) : min;
    if (reference.isNIL()) {
      return F.NIL;
    }
    IExpr minMagnitude = org.matheclipse.core.units.QuantityOps
        .magnitudeInFirstUnit((IAST) reference, (IAST) min, engine);
    IExpr maxMagnitude = org.matheclipse.core.units.QuantityOps
        .magnitudeInFirstUnit((IAST) reference, (IAST) max, engine);
    if (minMagnitude.isNIL() || maxMagnitude.isNIL()) {
      return F.NIL;
    }
    return F.List(minMagnitude, maxMagnitude);
  }

  /**
   * The {@link S#TargetUnits} entry that applies to one column of plot data.
   *
   * @param column the 1-based column, or <code>0</code> for a flat list of values
   * @param width the number of columns, or <code>0</code> for a flat list
   * @return the unit that column should be converted to, or {@link F#NIL} to keep the data's own
   */
  private static IExpr targetUnitFor(IExpr targetUnits, int column, int width) {
    if (targetUnits.isNIL() || targetUnits == S.Automatic) {
      return F.NIL;
    }
    if (targetUnits.isList()) {
      IAST units = (IAST) targetUnits;
      if (units.argSize() == 0) {
        return F.NIL;
      }
      // a flat list IS the value axis, which is the last entry of the specification
      return width == 0 ? units.last() : (column <= units.argSize() ? units.get(column) : F.NIL);
    }
    // a single unit names the value axis: y in 2D, z in 3D
    return (width == 0 || column == width) ? targetUnits : F.NIL;
  }

  /**
   * A {@code Quantity(1, unit)} to convert against, or {@link F#NIL} if the unit is not known.
   * Evaluating it is what resolves an alias or a prefix to its canonical name.
   */
  private static IExpr referenceQuantity(IExpr unit, EvalEngine engine) {
    IExpr reference = engine.evaluate(F.Quantity(F.C1, unit));
    return reference.isQuantity() ? reference : F.NIL;
  }

  /**
   * The magnitudes of a list of quantities, in <code>targetUnit</code> or - when that is
   * {@link F#NIL} - in the first element's unit.
   *
   * @return {@link F#NIL} if any element is in an incompatible unit
   */
  private static IExpr magnitudesInUnit(IAST quantities, IExpr targetUnit, EvalEngine engine) {
    IExpr reference =
        targetUnit.isPresent() ? referenceQuantity(targetUnit, engine) : quantities.arg1();
    if (reference.isNIL()) {
      return F.NIL;
    }
    IASTAppendable magnitudes = F.ListAlloc(quantities.argSize());
    for (int i = 1; i < quantities.size(); i++) {
      IExpr magnitude = org.matheclipse.core.units.QuantityOps
          .magnitudeInFirstUnit((IAST) reference, (IAST) quantities.get(i), engine);
      if (magnitude.isNIL()) {
        return F.NIL;
      }
      magnitudes.append(magnitude);
    }
    return magnitudes;
  }


  public static IExpr normalizeQuantityArray(IExpr expr) {
    if (expr.isAST(S.QuantityArray, 3)) {
      IExpr normal = quantityArrayNormal((IAST) expr);
      if (normal.isPresent()) {
        return normal;
      }
    }
    return expr;
  }

  /** Converts a single-unit QuantityArray to a target unit, element-wise (affine-safe). */
  private static IExpr convertQuantityArray(IAST quantityArray, IExpr target, EvalEngine engine) {
    IExpr spec = quantityArray.arg2();
    if (spec.isList()) {
      return convertPerPositionArray(quantityArray, (IAST) spec, target, engine);
    }
    if (target.isQuantity()) {
      target = ((IAST) target).arg2();
    }
    IExpr norm = Units.normalize(target);
    if (norm.isNIL()) {
      return Errors.printMessage(S.UnitConvert, "unkunit", F.list(target), engine);
    }
    IExpr normalized = engine.evaluate(norm);
    IExpr magnitudes = mapMagnitudes(quantityArray.arg1(), spec, normalized, engine);
    if (magnitudes.isNIL()) {
      Errors.printMessage(S.UnitConvert, "compat", F.list(spec, normalized), engine);
      return S.$Failed;
    }
    return F.binaryAST2(S.QuantityArray, magnitudes, normalized);
  }

  /**
   * Converts a QuantityArray with per-position units: a single target unit converts every position
   * that is compatible with it; a list target converts position-wise.
   */
  private static IExpr convertPerPositionArray(IAST quantityArray, IAST units, IExpr target,
      EvalEngine engine) {
    if (target.isQuantity()) {
      target = ((IAST) target).arg2();
    }
    IASTAppendable targets = F.ListAlloc(units.argSize());
    if (target.isList()) {
      IAST targetList = (IAST) target;
      if (targetList.argSize() != units.argSize()) {
        return F.NIL;
      }
      for (int i = 1; i < targetList.size(); i++) {
        IExpr norm = Units.normalize(targetList.get(i));
        if (norm.isNIL()) {
          return Errors.printMessage(S.UnitConvert, "unkunit", F.list(targetList.get(i)), engine);
        }
        targets.append(engine.evaluate(norm));
      }
    } else {
      IExpr norm = Units.normalize(target);
      if (norm.isNIL()) {
        return Errors.printMessage(S.UnitConvert, "unkunit", F.list(target), engine);
      }
      IExpr normalized = engine.evaluate(norm);
      for (int i = 1; i < units.size(); i++) {
        // positions of a different dimension keep their unit
        targets.append(Units.compatibleUnits(units.get(i), normalized) ? normalized : units.get(i));
      }
    }
    IExpr magnitudes = mapPerPositionMagnitudes(quantityArray.arg1(), units, targets, engine);
    if (magnitudes.isNIL()) {
      Errors.printMessage(S.UnitConvert, "compat", F.list(units, targets), engine);
      return S.$Failed;
    }
    return F.binaryAST2(S.QuantityArray, magnitudes, targets);
  }

  private static IExpr mapPerPositionMagnitudes(IExpr magnitudes, IAST units, IAST targets,
      EvalEngine engine) {
    if (!magnitudes.isList()) {
      return F.NIL;
    }
    IAST list = (IAST) magnitudes;
    IASTAppendable result = F.ListAlloc(list.argSize());
    boolean deepest = list.argSize() == units.argSize() && !list.arg1().isList();
    for (int i = 1; i < list.size(); i++) {
      IExpr element = list.get(i);
      if (element.isList()) {
        IExpr nested = mapPerPositionMagnitudes(element, units, targets, engine);
        if (nested.isNIL()) {
          return F.NIL;
        }
        result.append(nested);
      } else if (deepest) {
        IExpr converted = Units.convertMagnitude(element, units.get(i), targets.get(i), engine);
        if (converted.isNIL()) {
          return F.NIL;
        }
        result.append(converted);
      } else {
        return F.NIL;
      }
    }
    return result;
  }

  private static IExpr mapMagnitudes(IExpr magnitudes, IExpr fromUnit, IExpr toUnit,
      EvalEngine engine) {
    if (magnitudes.isList()) {
      IAST list = (IAST) magnitudes;
      IASTAppendable result = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        IExpr element = mapMagnitudes(list.get(i), fromUnit, toUnit, engine);
        if (element.isNIL()) {
          return F.NIL;
        }
        result.append(element);
      }
      return result;
    }
    return Units.convertMagnitude(magnitudes, fromUnit, toUnit, engine);
  }

  /** Extracts the unit of a quantity or bare unit expression; {@code F.NIL} if neither. */
  private static IExpr unitOf(IExpr expr) {
    if (expr.isQuantity()) {
      return ((IAST) expr).arg2();
    }
    return Units.normalize(expr);
  }

  private static final class KnownUnitQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr unit = Units.normalize(ast.arg1());
      if (unit.isNIL()) {
        return S.False;
      }
      if (ast.isAST2()) {
        return F.booleSymbol(matchesDimensionSpec(unit, ast.arg2()));
      }
      return S.True;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDFIRST);
    }
  }

  /** True if the unit expression has exactly the dimensions named by the specification. */
  private static boolean matchesDimensionSpec(IExpr unit, IExpr spec) {
    Map<String, IRational> expected = Units.dimensionSpec(spec);
    if (expected == null) {
      return false;
    }
    Map<String, IRational> actual = Units.dimensions(unit);
    return actual != null && actual.equals(expected);
  }

  private static final class CompatibleUnitQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST args = ast;
      if (ast.isAST1() && ast.arg1().isList()) {
        args = (IAST) ast.arg1();
      }
      Object commonDimensions = null;
      for (int i = 1; i < args.size(); i++) {
        IExpr unit = unitOf(args.get(i));
        if (unit.isNIL()) {
          return S.False;
        }
        Object dims = Units.dimensions(unit);
        if (dims == null) {
          return S.False;
        }
        if (commonDimensions == null) {
          commonDimensions = dims;
        } else if (!commonDimensions.equals(dims)) {
          return S.False;
        }
      }
      return commonDimensions == null ? F.NIL : S.True;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   *
   *
   * <pre>
   * Quantity(value, unit)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the quantity for <code>value</code> and <code>unit</code>
   *
   * </blockquote>
   */
  private static final class Quantity extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          IExpr norm = Units.normalize(ast.arg1());
          if (norm.isNIL()) {
            return Errors.printMessage(S.Quantity, "unkunit", F.list(ast.arg1()), engine);
          }
          return F.Quantity(F.C1, engine.evaluate(norm));
        }
        if (ast.isAST2()) {
          IExpr magnitude = ast.arg1();
          if (magnitude.isList()) {
            return magnitude.mapThread(F.Quantity(F.Slot1, ast.arg2()), 1);
          }
          IExpr unitRaw = engine.evaluate(ast.arg2());
          if (unitRaw.isNumber()) {
            // Quantity(2, 3/2) == 3
            return F.Times(magnitude, unitRaw);
          }
          IExpr norm = Units.normalize(unitRaw);
          if (norm.isNIL()) {
            return Errors.printMessage(S.Quantity, "unkunit", F.list(unitRaw), engine);
          }
          if (norm.isAST(S.MixedUnit, 2)) {
            // mixed units need a MixedMagnitude of matching length
            if (!magnitude.isAST(S.MixedMagnitude, 2) || !magnitude.first().isList()
                || ((IAST) magnitude.first()).argSize() != ((IAST) norm.first()).argSize()) {
              return Errors.printMessage(S.Quantity, "unkunit", F.list(unitRaw), engine);
            }
            if (norm.equals(ast.arg2())) {
              return F.NIL;
            }
            return F.Quantity(magnitude, norm);
          }
          IExpr normalized = engine.evaluate(norm);
          if (magnitude.isQuantity()) {
            // nested quantities multiply their units: Quantity(Quantity(4,"Meters"),1/"Seconds")
            IAST inner = (IAST) magnitude;
            IExpr unit = engine.evaluate(F.Times(inner.arg2(), normalized));
            if (unit.isOne()) {
              return inner.arg1();
            }
            return F.Quantity(inner.arg1(), unit);
          }
          if (normalized.equals(ast.arg2())) {
            return F.NIL; // fixed point: already canonical
          }
          return F.Quantity(magnitude, normalized);
        }
      } catch (RuntimeException e) {
        Errors.rethrowsInterruptException(e);
        return Errors.printMessage(S.Quantity, e, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDREST, Attribute.NHOLDREST);
    }
  }

  /**
   *
   *
   * <pre>
   * QuantityMagnitude(quantity)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the value of the <code>quantity</code>
   *
   * </blockquote>
   *
   * <pre>
   * QuantityMagnitude(quantity, unit)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the value of the <code>quantity</code> for the given <code>unit</code>
   *
   * </blockquote>
   */
  private static final class QuantityMagnitude extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr arg1 = ast.arg1();
        if (ast.isAST1()) {
          if (arg1.isList()) {
            return arg1.mapThread(F.QuantityMagnitude(F.Slot1), 1);
          }
          if (arg1.isQuantity()) {
            return ((IAST) arg1).arg1();
          }
          if (arg1.isAST(S.QuantityArray, 3)) {
            return ((IAST) arg1).arg1();
          }
          if (arg1.isNumber()) {
            return arg1;
          }
        } else if (ast.isAST2()) {
          if (arg1.isList()) {
            return arg1.mapThread(F.QuantityMagnitude(F.Slot1, ast.arg2()), 1);
          }
          if (arg1.isAST(S.QuantityArray, 3)) {
            IExpr converted = convertQuantityArray((IAST) arg1, ast.arg2(), engine);
            if (converted.isAST(S.QuantityArray, 3)) {
              return ((IAST) converted).arg1();
            }
            return converted.isPresent() ? converted : F.NIL;
          }
          if (arg1.isQuantity()) {
            IExpr converted = convertTo((IAST) arg1, ast.arg2(), engine);
            if (converted.isQuantity()) {
              return ((IAST) converted).arg1();
            }
            return converted.isPresent() ? converted : F.NIL;
          }
        }
      } catch (RuntimeException e) {
        Errors.rethrowsInterruptException(e);
        return Errors.printMessage(S.QuantityMagnitude, e, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class QuantityUnit extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(F.QuantityUnit(F.Slot1), 1);
      }
      if (arg1.isQuantity()) {
        return ((IAST) arg1).arg2();
      }
      if (arg1.isAST(S.QuantityArray, 3)) {
        return ((IAST) arg1).arg2();
      }
      if (arg1.isNumber()) {
        return F.stringx("DimensionlessUnit");
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * UnitConvert(quantity)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * convert the <code>quantity</code> to the base unit
   *
   * </blockquote>
   *
   * <pre>
   * UnitConvert(quantity, unit)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * convert the <code>quantity</code> to the given <code>unit</code>
   *
   * </blockquote>
   */
  private static final class UnitConvert extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr arg1 = ast.arg1();
        if (arg1.isList()) {
          IAST template = ast.isAST1() ? F.UnitConvert(F.Slot1)
              : F.UnitConvert(F.Slot1, ast.arg2());
          return arg1.mapThread(template, 1);
        }
        if (arg1.isAST(S.QuantityArray, 3)) {
          if (ast.isAST2()) {
            return convertQuantityArray((IAST) arg1, ast.arg2(), engine);
          }
          return F.NIL;
        }
        if (arg1.isAST(S.QuantityDistribution, 3)) {
          if (ast.isAST2()) {
            return convertQuantityDistribution((IAST) arg1, ast.arg2(), engine);
          }
          return F.NIL;
        }
        IAST quantity = asQuantity(arg1);
        if (quantity == null) {
          return F.NIL;
        }
        if (ast.isAST1()) {
          return convertTo(quantity, F.stringx("SIBase"), engine);
        }
        return convertTo(quantity, ast.arg2(), engine);
      } catch (RuntimeException e) {
        Errors.rethrowsInterruptException(e);
        return Errors.printMessage(S.UnitConvert, e, engine);
      }
    }

    private static IAST asQuantity(IExpr expr) {
      if (expr.isQuantity()) {
        return (IAST) expr;
      }
      IExpr norm = Units.normalize(expr);
      if (norm.isPresent()) {
        return F.Quantity(F.C1, EvalEngine.get().evaluate(norm));
      }
      return null;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * Converts a quantity to the given target spec (unit expression, {@code Quantity},
   * {@code "SIBase"}/{@code "SI"}). Returns the converted quantity, {@code S.$Failed} with a
   * {@code compat} message for incompatible units, or {@code F.NIL} with a message for an unknown
   * target.
   */
  private static IExpr convertTo(IAST quantity, IExpr target, EvalEngine engine) {
    // a mixed-unit quantity converts via its total in the first component's unit
    IExpr sourceMagnitude = quantity.arg1();
    IExpr sourceUnit = quantity.arg2();
    if (sourceUnit.isAST(S.MixedUnit, 2) && sourceUnit.first().isList()
        && sourceMagnitude.isAST(S.MixedMagnitude, 2)) {
      IExpr firstComponent = ((IAST) sourceUnit.first()).arg1();
      IExpr total =
          Units.mixedTotalIn((IAST) sourceMagnitude, (IAST) sourceUnit, firstComponent, engine);
      if (total.isNIL()) {
        return Errors.printMessage(S.UnitConvert, "unkunit", F.list(sourceUnit), engine);
      }
      sourceMagnitude = total;
      sourceUnit = firstComponent;
    }
    if (target.isString()) {
      String name = target.toString();
      if (name.equals("SIBase") || name.equals("SI")) {
        return Units.toBaseQuantity(sourceMagnitude, sourceUnit, engine);
      }
      if (name.equals("Metric") || name.equals("Imperial")) {
        // a unit system, not a unit: look up that system's preferred unit for this kind of
        // quantity. A compound unit has none and is returned unchanged, as Mathematica does.
        IExpr systemUnit = Units.systemUnit(sourceUnit, name);
        if (systemUnit.isNIL()) {
          return F.Quantity(sourceMagnitude, sourceUnit);
        }
        target = systemUnit;
      }
    }
    if (target.isQuantity()) {
      target = ((IAST) target).arg2(); // target magnitude is ignored
    }
    IExpr norm = Units.normalize(target);
    if (norm.isNIL()) {
      return Errors.printMessage(S.UnitConvert, "unkunit", F.list(target), engine);
    }
    if (norm.isAST(S.MixedUnit, 2)) {
      IExpr mixed = Units.toMixedQuantity(sourceMagnitude, sourceUnit, (IAST) norm, engine);
      if (mixed.isNIL()) {
        Errors.printMessage(S.UnitConvert, "compat", F.list(sourceUnit, norm), engine);
        return S.$Failed;
      }
      return mixed;
    }
    IExpr normalized = engine.evaluate(norm);
    IExpr magnitude = Units.convertMagnitude(sourceMagnitude, sourceUnit, normalized, engine);
    if (magnitude.isNIL()) {
      Errors.printMessage(S.UnitConvert, "compat", F.list(sourceUnit, normalized), engine);
      return S.$Failed;
    }
    return F.Quantity(magnitude, normalized);
  }

  /** UnitDimensions(unit) - the {{"LengthUnit",1},...} decomposition of a unit or quantity. */
  private static final class UnitDimensions extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(F.UnitDimensions(F.Slot1), 1);
      }
      IExpr unit = unitOf(arg1);
      if (unit.isNIL()) {
        return F.NIL;
      }
      Map<String, IRational> dims = Units.dimensions(unit);
      return dims == null ? F.NIL : dimensionsList(dims);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * UnitSimplify(quantity) - rewrite the unit as an equivalent named unit if one exists:
   * {@code UnitSimplify(Quantity(1, "Joules"/"Seconds"))} gives {@code Quantity(1, "Watts")}.
   */
  private static final class UnitSimplify extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(F.UnitSimplify(F.Slot1), 1);
      }
      IAST quantity;
      if (arg1.isQuantity()) {
        quantity = (IAST) arg1;
      } else {
        IExpr norm = Units.normalize(arg1);
        if (norm.isNIL()) {
          return F.NIL;
        }
        quantity = F.Quantity(F.C1, engine.evaluate(norm));
      }
      IExpr unit = quantity.arg2();
      IExpr magnitude = quantity.arg1();
      IAST unityDimensions = unityDimensionsOption(ast, engine);
      if (unit.isString() && (unityDimensions == null || unityDimensions.argSize() == 0)) {
        return quantity; // already a single named unit
      }
      if (unityDimensions != null && unityDimensions.argSize() > 0) {
        // factor out the named dimensions: convert them to their base unit first, then strike
        IExpr[] stripped = Units.stripDimensions(magnitude, unit, unityDimensions, engine);
        if (stripped != null) {
          magnitude = stripped[0];
          unit = stripped[1];
          if (unit.isOne()) {
            return magnitude;
          }
        }
      }
      String preferred = Units.simplifyUnit(unit);
      if (preferred == null) {
        return unit.equals(quantity.arg2()) ? quantity : F.Quantity(magnitude, unit);
      }
      IExpr target = F.stringx(preferred);
      IExpr converted = Units.convertMagnitude(magnitude, unit, target, engine);
      if (converted.isNIL()) {
        return quantity;
      }
      return F.Quantity(converted, target);
    }

    /** The {@code UnityDimensions} option value as a list of dimension names, or {@code null}. */
    private static IAST unityDimensionsOption(IAST ast, EvalEngine engine) {
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST() && arg.first() == S.UnityDimensions) {
          IExpr value = engine.evaluate(arg.second());
          if (value == S.Automatic) {
            return F.list(F.stringx("AngleUnit"), F.stringx("SolidAngleUnit"));
          }
          if (value.isList()) {
            return (IAST) value;
          }
          return F.CEmptyList;
        }
      }
      return null;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * CommonUnits({q1, q2, ...}) - convert quantities of the same dimension to one shared unit (the
   * first occurring unit of each dimension group).
   */
  private static final class CommonUnits extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      java.util.Map<Object, IExpr> unitByDimension = new java.util.HashMap<>();
      IASTAppendable result = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        IExpr element = list.get(i);
        if (!element.isQuantity()) {
          result.append(element);
          continue;
        }
        IAST quantity = (IAST) element;
        Object dims = Units.dimensions(quantity.arg2());
        if (dims == null) {
          result.append(element);
          continue;
        }
        IExpr commonUnit = unitByDimension.get(dims);
        if (commonUnit == null) {
          unitByDimension.put(dims, quantity.arg2());
          result.append(element);
          continue;
        }
        IExpr magnitude =
            Units.convertMagnitude(quantity.arg1(), quantity.arg2(), commonUnit, engine);
        result.append(magnitude.isNIL() ? element : F.Quantity(magnitude, commonUnit));
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * DimensionalCombinations({pq1, pq2, ...}) - the monomial combinations of the given physical
   * quantities that are dimensionless; with a second argument, the combinations having the
   * dimensions of that target.
   *
   * <p>
   * The exponents are the exact rational solutions of the linear system over the base dimensions
   * (Buckingham pi): the null space for the dimensionless case, a particular solution for a target.
   * Combinations are unique only up to powers and constant factors.
   */
  private static final class DimensionalCombinations extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      IASTAppendable pool = ((IAST) ast.arg1()).copyAppendable();
      IExpr target = F.NIL;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST()) {
          if (arg.first() == S.IncludeQuantities && arg.second().isList()) {
            pool.appendArgs((IAST) arg.second());
          }
          continue;
        }
        if (target.isNIL()) {
          target = arg;
        }
      }
      // dimensions of every pool element; dimensionless elements cannot contribute
      IASTAppendable bases = F.ListAlloc(pool.argSize());
      java.util.List<Map<String, IRational>> dimensions = new java.util.ArrayList<>();
      java.util.TreeSet<String> baseDimensions = new java.util.TreeSet<>();
      for (int i = 1; i < pool.size(); i++) {
        Map<String, IRational> dims = elementDimensions(pool.get(i));
        if (dims == null) {
          return F.NIL;
        }
        if (dims.isEmpty()) {
          continue;
        }
        bases.append(pool.get(i));
        dimensions.add(dims);
        baseDimensions.addAll(dims.keySet());
      }
      if (bases.argSize() == 0) {
        return F.CEmptyList;
      }
      Map<String, IRational> targetDimensions = java.util.Collections.emptyMap();
      if (target.isPresent()) {
        targetDimensions = elementDimensions(target);
        if (targetDimensions == null) {
          return F.NIL;
        }
        baseDimensions.addAll(targetDimensions.keySet());
      }
      // matrix rows = base dimensions, columns = pool elements
      IASTAppendable matrix = F.ListAlloc(baseDimensions.size());
      IASTAppendable rhs = F.ListAlloc(baseDimensions.size());
      for (String dimension : baseDimensions) {
        IASTAppendable row = F.ListAlloc(dimensions.size());
        for (Map<String, IRational> dims : dimensions) {
          row.append(dims.getOrDefault(dimension, F.C0));
        }
        matrix.append(row);
        rhs.append(targetDimensions.getOrDefault(dimension, F.C0));
      }
      IASTAppendable result = F.ListAlloc(4);
      if (target.isNIL()) {
        IExpr nullSpace = engine.evaluate(F.NullSpace(matrix));
        if (!nullSpace.isList()) {
          return F.NIL;
        }
        IAST vectors = (IAST) nullSpace;
        for (int i = 1; i < vectors.size(); i++) {
          IExpr monomial = monomial(bases, vectors.get(i), engine);
          if (monomial.isPresent()) {
            result.append(monomial);
          }
        }
      } else {
        IExpr solution = engine.evaluate(F.LinearSolve(matrix, rhs));
        if (solution.isList() && ((IAST) solution).argSize() == bases.argSize()) {
          IExpr monomial = monomial(bases, solution, engine);
          if (monomial.isPresent()) {
            result.append(monomial);
          }
        }
      }
      return result;
    }

    /** The dimension map of a quantity variable, physical quantity name or quantity. */
    private static Map<String, IRational> elementDimensions(IExpr element) {
      IExpr unit = Units.quantityVariableUnit(element);
      if (unit.isNIL()) {
        unit = Units.physicalQuantityUnit(element);
      }
      if (unit.isNIL() && element.isQuantity()) {
        unit = ((IAST) element).arg2();
      }
      if (unit.isNIL()) {
        return null;
      }
      return Units.dimensions(unit);
    }

    /**
     * Builds {@code PROD base_i^exponent_i}, scaled to integer exponents and normalized so that the
     * first non-zero exponent is positive.
     */
    private static IExpr monomial(IAST bases, IExpr exponents, EvalEngine engine) {
      if (!exponents.isList() || ((IAST) exponents).argSize() != bases.argSize()) {
        return F.NIL;
      }
      IAST vector = (IAST) exponents;
      java.math.BigInteger denominators = java.math.BigInteger.ONE;
      IExpr firstNonZero = F.NIL;
      for (int i = 1; i < vector.size(); i++) {
        IExpr exponent = vector.get(i);
        if (!exponent.isRational()) {
          return F.NIL;
        }
        denominators = lcm(denominators, ((IRational) exponent).denominator().toBigNumerator());
        if (firstNonZero.isNIL() && !exponent.isZero()) {
          firstNonZero = exponent;
        }
      }
      if (firstNonZero.isNIL()) {
        return F.NIL; // the zero vector is not a combination
      }
      IRational scale = F.ZZ(denominators);
      if (firstNonZero.isNegative()) {
        scale = scale.negate();
      }
      IASTAppendable product = F.TimesAlloc(vector.argSize());
      for (int i = 1; i < vector.size(); i++) {
        IExpr exponent = engine.evaluate(F.Times(scale, vector.get(i)));
        if (!exponent.isZero()) {
          product.append(F.Power(bases.get(i), exponent));
        }
      }
      return engine.evaluate(product);
    }

    private static java.math.BigInteger lcm(java.math.BigInteger a, java.math.BigInteger b) {
      if (b.signum() == 0) {
        return a;
      }
      return a.divide(a.gcd(b)).multiply(b).abs();
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   * NondimensionalizationTransform(eqn, ovars, fvars) - replaces every original variable by a
   * characteristic multiplier times a dimensionless replacement variable.
   *
   * <p>
   * The optional fourth argument selects the returned property: {@code "ReducedForm"} (default),
   * {@code "NondimensionalizationRules"}, {@code "DimensionalizationRules"},
   * {@code "NondimensionalizationMultipliers"} or {@code "PropertyAssociation"}. The multipliers
   * are named by the {@code GeneratedQuantityMagnitudes} option (default {@code K}).
   */
  private static final class NondimensionalizationTransform extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg2().isList() || !ast.arg3().isList()) {
        return F.NIL;
      }
      IAST originalVariables = (IAST) ast.arg2();
      IAST freeVariables = (IAST) ast.arg3();
      if (originalVariables.argSize() != freeVariables.argSize()) {
        return F.NIL;
      }
      IExpr property = F.stringx("ReducedForm");
      // WMA names the characteristic magnitudes K[1], K[2], ... by default
      ISymbol magnitudes = F.symbol("K");
      for (int i = 4; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST()) {
          if (arg.first() == S.GeneratedQuantityMagnitudes && arg.second().isSymbol()) {
            magnitudes = (ISymbol) arg.second();
          }
          continue;
        }
        property = arg;
      }
      IASTAppendable multipliers = F.ListAlloc(originalVariables.argSize());
      IASTAppendable nondimensionalization = F.ListAlloc(originalVariables.argSize());
      IASTAppendable dimensionalization = F.ListAlloc(originalVariables.argSize());
      for (int i = 1; i < originalVariables.size(); i++) {
        IExpr original = originalVariables.get(i);
        IExpr replacement = freeVariables.get(i);
        IExpr multiplier = F.unaryAST1(magnitudes, F.ZZ(i));
        IExpr unit = Units.quantityVariableUnit(original);
        if (unit.isPresent()) {
          // the multiplier carries the characteristic scale in the variable's canonical unit
          multiplier = F.Quantity(multiplier, engine.evaluate(unit));
        }
        multipliers.append(multiplier);
        nondimensionalization.append(F.Rule(original, F.Times(multiplier, replacement)));
        dimensionalization.append(F.Rule(replacement, F.Divide(original, multiplier)));
      }
      String name = property.isString() ? property.toString() : "ReducedForm";
      switch (name) {
        case "NondimensionalizationRules":
          return nondimensionalization;
        case "DimensionalizationRules":
          return dimensionalization;
        case "NondimensionalizationMultipliers":
          return multipliers;
        case "PropertyAssociation": {
          IASTAppendable association = F.ListAlloc(4);
          association.append(F.Rule(F.stringx("ReducedForm"),
              engine.evaluate(F.ReplaceAll(ast.arg1(), nondimensionalization))));
          association
              .append(F.Rule(F.stringx("NondimensionalizationRules"), nondimensionalization));
          association.append(F.Rule(F.stringx("DimensionalizationRules"), dimensionalization));
          association.append(F.Rule(F.stringx("NondimensionalizationMultipliers"), multipliers));
          return F.assoc(association);
        }
        default:
          return engine.evaluate(F.ReplaceAll(ast.arg1(), nondimensionalization));
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_INFINITY;
    }
  }

  /**
   * QuantityDistribution(dist, unit) - a distribution whose variate carries a unit, semantically
   * {@code TransformedDistribution(Quantity(x, unit), x ~ dist)}.
   *
   * <p>
   * Implementing the distribution interfaces makes the whole statistics machinery work on it:
   * {@code Mean}/{@code Median}/{@code Quantile}/{@code InverseCDF} come back in {@code unit},
   * {@code Variance} in {@code unit^2}, {@code CDF}/{@code SurvivalFunction} are dimensionless and
   * accept arguments in any compatible unit, and a continuous {@code PDF} is in {@code unit^-1}.
   */
  private static final class QuantityDistribution extends AbstractFunctionEvaluator
      implements IContinuousDistribution, IStatistics, ICDF, IPDF, IRandomVariate {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr norm = Units.normalize(ast.arg2());
      if (norm.isNIL()) {
        return Errors.printMessage(S.QuantityDistribution, "unkunit", F.list(ast.arg2()), engine);
      }
      IExpr normalized = engine.evaluate(norm);
      Map<String, IRational> dims = Units.dimensions(normalized);
      if (dims != null && dims.isEmpty()) {
        // a dimensionless unit collapses to the plain magnitude distribution
        IExpr[] split = Units.toBaseSplit(normalized, engine);
        return split[0].isOne() ? ast.arg1()
            : F.binaryAST2(S.TransformedDistribution, F.Times(split[0], F.Slot1), ast.arg1());
      }
      return normalized.equals(ast.arg2()) ? F.NIL
          : F.binaryAST2(S.QuantityDistribution, ast.arg1(), normalized);
    }

    private static IExpr inner(IAST qd) {
      return qd.arg1();
    }

    private static IExpr unit(IAST qd) {
      return qd.arg2();
    }

    /** Wraps a magnitude-level result in the distribution's unit (raised to {@code power}). */
    private static IExpr withUnit(IExpr magnitude, IAST qd, int power) {
      if (magnitude.isNIL()) {
        return F.NIL;
      }
      if (power == 0) {
        return magnitude;
      }
      IExpr unit =
          power == 1 ? unit(qd) : EvalEngine.get().evaluate(F.Power(unit(qd), F.ZZ(power)));
      return F.Quantity(magnitude, unit);
    }

    /** The magnitude of {@code x} in the distribution's unit; {@code F.NIL} if incompatible. */
    private static IExpr magnitudeIn(IExpr x, IAST qd, EvalEngine engine) {
      if (x.isQuantity()) {
        IAST quantity = (IAST) x;
        return Units.convertMagnitude(quantity.arg1(), quantity.arg2(), unit(qd), engine);
      }
      return x; // a bare number is taken to be in the distribution's unit
    }

    @Override
    public IExpr mean(IAST qd) {
      return withUnit(EvalEngine.get().evaluate(F.Mean(inner(qd))), qd, 1);
    }

    @Override
    public IExpr median(IAST qd) {
      return withUnit(EvalEngine.get().evaluate(F.Median(inner(qd))), qd, 1);
    }

    @Override
    public IExpr variance(IAST qd) {
      return withUnit(EvalEngine.get().evaluate(F.Variance(inner(qd))), qd, 2);
    }

    @Override
    public IExpr standardDeviation(IAST qd) {
      return withUnit(EvalEngine.get().evaluate(F.StandardDeviation(inner(qd))), qd, 1);
    }

    @Override
    public IExpr skewness(IAST qd) {
      // a shape statistic is dimensionless
      return EvalEngine.get().evaluate(F.Skewness(inner(qd)));
    }

    @Override
    public IExpr moment(IAST qd, IExpr n) {
      IExpr magnitude = EvalEngine.get().evaluate(F.Moment(inner(qd), n));
      if (magnitude.isNIL() || !n.isInteger()) {
        return F.NIL;
      }
      return F.Quantity(magnitude, EvalEngine.get().evaluate(F.Power(unit(qd), n)));
    }

    @Override
    public IExpr cdf(IAST qd, IExpr x, EvalEngine engine) {
      IExpr magnitude = magnitudeIn(x, qd, engine);
      return magnitude.isNIL() ? F.NIL : engine.evaluate(F.CDF(inner(qd), magnitude));
    }

    @Override
    public IExpr survivalFunction(IAST qd, IExpr x, EvalEngine engine) {
      IExpr magnitude = magnitudeIn(x, qd, engine);
      return magnitude.isNIL() ? F.NIL
          : engine.evaluate(F.SurvivalFunction(inner(qd), magnitude));
    }

    @Override
    public IExpr inverseCDF(IAST qd, IExpr p, EvalEngine engine) {
      return withUnit(engine.evaluate(F.InverseCDF(inner(qd), p)), qd, 1);
    }

    @Override
    public IExpr pdf(IAST qd, IExpr x, EvalEngine engine) {
      IExpr magnitude = magnitudeIn(x, qd, engine);
      if (magnitude.isNIL()) {
        return F.NIL;
      }
      IExpr density = engine.evaluate(F.PDF(inner(qd), magnitude));
      if (density.isNIL()) {
        return F.NIL;
      }
      // a continuous density is per unit; a discrete probability is dimensionless
      return inner(qd).isContinuousDistribution() ? withUnit(density, qd, -1) : density;
    }

    @Override
    public IExpr randomVariate(java.util.Random random, IAST qd, int size) {
      IExpr distribution = inner(qd);
      if (!distribution.isAST() || !distribution.isBuiltInFunction()) {
        return F.NIL;
      }
      org.matheclipse.core.interfaces.IEvaluator evaluator =
          ((org.matheclipse.core.interfaces.IBuiltInSymbol) distribution.head()).getEvaluator();
      if (!(evaluator instanceof IRandomVariate)) {
        return F.NIL;
      }
      IExpr variate = ((IRandomVariate) evaluator).randomVariate(random, (IAST) distribution, size);
      if (variate.isNIL()) {
        return F.NIL;
      }
      if (variate.isList()) {
        return F.binaryAST2(S.QuantityArray, variate, unit(qd));
      }
      return F.Quantity(variate, unit(qd));
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * Converts a {@code QuantityDistribution} to another unit: the variate is rescaled by the
   * conversion factor, so the converted distribution is a {@code TransformedDistribution}.
   */
  private static IExpr convertQuantityDistribution(IAST distribution, IExpr target,
      EvalEngine engine) {
    if (target.isQuantity()) {
      target = ((IAST) target).arg2();
    }
    IExpr norm = Units.normalize(target);
    if (norm.isNIL()) {
      return Errors.printMessage(S.UnitConvert, "unkunit", F.list(target), engine);
    }
    IExpr normalized = engine.evaluate(norm);
    IExpr unit = distribution.arg2();
    IExpr factor = Units.convertMagnitude(F.C1, unit, normalized, engine);
    if (factor.isNIL()) {
      Errors.printMessage(S.UnitConvert, "compat", F.list(unit, normalized), engine);
      return S.$Failed;
    }
    IExpr inner = distribution.arg1();
    if (!factor.isOne()) {
      inner =
          engine.evaluate(F.binaryAST2(S.TransformedDistribution, F.Times(factor, F.Slot1), inner));
    }
    return F.binaryAST2(S.QuantityDistribution, inner, normalized);
  }

  /**
   * QuantityForm(expr, form) - prints every quantity in {@code expr} with the requested unit
   * rendering: {@code "Abbreviation"} ({@code 3 m/s}), {@code "LongForm"}
   * ({@code 3 meters per second}) or {@code "SingularForm"} (the long form in the singular). The
   * wrapper itself disappears from the result.
   */
  private static final class QuantityForm extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      String form = "Abbreviation";
      if (ast.isAST2()) {
        IExpr spec = ast.arg2();
        if (spec.isList()) {
          // {"LongForm", "SingularForm"} - the singular form implies the long form
          IAST list = (IAST) spec;
          form =
              list.exists(x -> x.isString() && x.toString().equals("SingularForm")) ? "SingularForm"
                  : (list.argSize() > 0 && list.arg1().isString() ? list.arg1().toString()
                      : "Abbreviation");
        } else if (spec.isString()) {
          form = spec.toString();
        }
      }
      final String selected = form;
      IExpr result = ast.arg1().replaceAll(x -> {
        if (x.isQuantity() && Units.isKnownUnit(((IAST) x).arg2())) {
          IAST quantity = (IAST) x;
          return F.stringx(
              quantity.arg1().toString() + " " + Units.renderUnit(quantity.arg2(), selected));
        }
        return F.NIL;
      });
      return result.isPresent() ? result : ast.arg1();
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private QuantityFunctions() {}
}
