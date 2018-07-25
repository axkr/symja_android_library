package org.matheclipse.core.builtin;

import java.util.function.UnaryOperator;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import ch.ethz.idsc.tensor.qty.IQuantity;
import ch.ethz.idsc.tensor.qty.IUnit;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.UnitSystem;

public class QuantityFunctions {
	static {
		if (ToggleFeature.QUANTITY) {
			F.Quantity.setEvaluator(new Quantity());
			F.QuantityMagnitude.setEvaluator(new QuantityMagnitude());
			F.UnitConvert.setEvaluator(new UnitConvert());
		}
	}

	/**
	 * <pre>
	 * Quantity(value, unit)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the quantity for <code>value</code> and <code>unit</code>
	 * </p>
	 * </blockquote>
	 */
	private final static class Quantity extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			try {
				if (ast.size() == 3) {
					IExpr arg1 = engine.evaluate(ast.arg1());
					IExpr arg2 = engine.evaluate(ast.arg2());
					if (arg2.isString()) {
						return IQuantity.of(arg1, IUnit.of(arg2.toString()));
					}
				}
			} catch (Exception e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}

			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * QuantityMagnitude(quantity)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the value of the <code>quantity</code>
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * QuantityMagnitude(quantity, unit)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the value of the <code>quantity</code> for the given <code>unit</code>
	 * </p>
	 * </blockquote>
	 */
	private final static class QuantityMagnitude extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			try {
				if (ast.size() == 2) {
					IExpr arg1 = engine.evaluate(ast.arg1());
					if (arg1.isQuantity()) {
						return ((IAST) arg1).arg1();
					}
				} else if (ast.size() == 3) {
					IExpr arg1 = engine.evaluate(ast.arg1());
					IExpr arg2 = engine.evaluate(ast.arg2());
					if (arg1.isQuantity()) {
						ch.ethz.idsc.tensor.qty.QuantityMagnitude quantityMagnitude = ch.ethz.idsc.tensor.qty.QuantityMagnitude
								.SI();
						IUnit unit = IUnit.of(arg2.toString());
						UnaryOperator<IExpr> suo = quantityMagnitude.in(unit);
						return suo.apply(arg1);
					}
				}
			} catch (Exception e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}

			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * UnitConvert(quantity)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * convert the <code>quantity</code> to the base unit
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * UnitConvert(quantity, unit)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * convert the <code>quantity</code> to the given <code>unit</code>
	 * </p>
	 * </blockquote>
	 */
	private final static class UnitConvert extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			try {
				if (ast.size() == 2) {
					IExpr arg1 = engine.evaluate(ast.arg1());
					if (arg1.isQuantity()) {
						return UnitSystem.SI().apply(arg1);
					}
				} else if (ast.size() == 3) {
					IExpr arg1 = engine.evaluate(ast.arg1());
					IExpr arg2 = engine.evaluate(ast.arg2());
					if (arg1.isQuantity()) {
						ch.ethz.idsc.tensor.qty.UnitConvert unitConvert = ch.ethz.idsc.tensor.qty.UnitConvert.SI();
						IUnit unit = IUnit.of(arg2.toString());
						return unitConvert.to(unit).apply(arg1);
					}
				}
			} catch (Exception e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}

			return F.NIL;
		}

	}

	private final static QuantityFunctions CONST = new QuantityFunctions();

	public static QuantityFunctions initialize() {
		return CONST;
	}

	private QuantityFunctions() {

	}

}
