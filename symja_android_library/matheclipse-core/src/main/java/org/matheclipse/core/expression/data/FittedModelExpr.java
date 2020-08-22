package org.matheclipse.core.expression.data;
 
import org.hipparchus.stat.regression.SimpleRegression;
import org.hipparchus.stat.regression.UpdatingMultipleLinearRegression;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class FittedModelExpr extends DataExpr<UpdatingMultipleLinearRegression> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2779698690575246663L;

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static FittedModelExpr newInstance(final UpdatingMultipleLinearRegression value) {
		return new FittedModelExpr(value);
	}

	protected FittedModelExpr(final UpdatingMultipleLinearRegression function) {
		super(S.FittedModel, function);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof FittedModelExpr) {
			return fData.equals(((FittedModelExpr) obj).fData);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (fData == null) ? 461 : 461 + fData.hashCode();
	}

	@Override
	public int hierarchy() {
		return FITTEDMODELID;
	}

	public IExpr evaluate(IAST ast, EvalEngine engine) {
		return F.NIL;
	}

	@Override
	public IExpr copy() {
		return new FittedModelExpr(fData);
	}

	public IAST normal(boolean nilIfUnevaluated) {
		UpdatingMultipleLinearRegression model = toData();
		if (model instanceof SimpleRegression) {
			SimpleRegression simpleModel = (SimpleRegression) model;
			return F.Plus(F.num(simpleModel.getIntercept()), F.Times(F.num(simpleModel.getSlope()), S.C));
		}
		return F.NIL;
	}
}
