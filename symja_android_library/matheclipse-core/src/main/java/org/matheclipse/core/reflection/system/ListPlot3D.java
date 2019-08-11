package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Graphics;
import static org.matheclipse.core.expression.F.Line;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Show;

import java.util.Arrays;

import org.hipparchus.stat.descriptive.moment.Mean;
import org.hipparchus.stat.descriptive.moment.StandardDeviation;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.graphics.Dimensions2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * Plot a list of Points in 3 dimesnsions
 *
 */
public class ListPlot3D extends AbstractEvaluator {
	public ListPlot3D() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (Config.USE_MATHCELL) {
			IExpr temp = F.Manipulate.of(engine, ast);
			if (temp.headID() == ID.JSFormData) {
				return temp;
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}
}
