package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;
import org.matheclipse.parser.client.SyntaxError;
import static org.matheclipse.core.expression.F.*;

/**
 * Expand the powers of a given expression.
 */
public class PowerExpand extends AbstractFunctionEvaluator {

	class PowerExpandVisitor extends VisitorExpr {
		public PowerExpandVisitor() {
			super();
		}

		/** {@inheritDoc} */
		@Override
		public IExpr visit2(IExpr head, IExpr arg1) {
			boolean evaled = false;
			IExpr x1 = arg1;
			IExpr result = arg1.accept(this);
			if (result != null) {
				evaled = true;
				x1 = result;
			}
			if (head.equals(Log)) {
				if (x1.isPower()) {
					IAST powerAST = (IAST) x1;
					return Times(powerAST.arg2(), Log(powerAST.arg1()));
				}
			}
			if (evaled) {
				return $(head, x1);
			}
			return null;
		}

		/** {@inheritDoc} */
		@Override
		public IExpr visit3(IExpr head, IExpr arg1, IExpr arg2) {
			boolean evaled = false;
			IExpr x1 = arg1;
			IExpr result = arg1.accept(this);
			if (result != null) {
				evaled = true;
				x1 = result;
			}
			IExpr x2 = arg2;
			result = arg2.accept(this);
			if (result != null) {
				evaled = true;
				x2 = result;
			}
			if (head.equals(Power)) {
				if (x1.isTimes()) {
					// Power[x_ * y_, z_] :> x^z * y^z
					IAST timesAST = (IAST) x1;
					return timesAST.mapAt(Power(Null, x2), 1);
				}
				if (x1.isPower()) {
					// Power[x_ ^ y_, z_] :> x ^(y*z)
					IAST powerAST = (IAST) x1;
					return Power(powerAST.arg1(), Times(powerAST.arg2(), x2));
				}
			}
			if (evaled) {
				return $(head, x1, x2);
			}
			return null;
		}

	}

	public PowerExpand() {
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		if (ast.arg1().isAST()) {
			PowerExpandVisitor pweVisitor = new PowerExpandVisitor();
			IExpr result = ast.arg1().accept(pweVisitor);
			if (result != null) {
				return result;
			}
			return ast.arg1();
		}
		return ast.arg1();
	}

	/** {@inheritDoc} */
	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}
