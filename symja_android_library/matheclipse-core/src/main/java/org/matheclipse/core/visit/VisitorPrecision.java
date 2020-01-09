package org.matheclipse.core.visit;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Visit every node of an <code>IExpr</code> expression and determine the precision of the input.
 */
public class VisitorPrecision extends AbstractVisitor {
	long precision;

	/**
	 * Determine precision of the input. Set minimum <code>precision = Config.MACHINE_PRECISION</code>.
	 */
	public VisitorPrecision() {
		this.precision = Config.MACHINE_PRECISION;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IASTMutable ast) {
		if (ast.isAST(F.N, 3)) {
			int p = ast.arg2().toIntDefault();
			if (p > precision) {
				precision = p;
			}
		} else {
			ast.forEach(x -> x.accept(this));
		}
		return F.NIL;

	}

	@Override
	public IExpr visit(INum element) {
		if (element instanceof ApfloatNum) {
			if (((ApfloatNum) element).precision() > precision) {
				precision = ((ApfloatNum) element).precision();
			}
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(IComplexNum element) {
		if (element instanceof ApcomplexNum) {
			if (((ApcomplexNum) element).precision() > precision) {
				precision = ((ApcomplexNum) element).precision();
			}
		}
		return F.NIL;
	}

	/**
	 * Get the result of the input precision.
	 * 
	 * @return
	 */
	public long getNumericPrecision() {
		return precision;
	}
}