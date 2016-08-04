package org.matheclipse.core.reflection.system;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ProductLogRules;

/**
 * <p>
 * Lambert W function
 * </p>
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Lambert_W_function">Wikipedia - Lambert W function</a>
 */
public class ProductLog extends AbstractArg12 implements ProductLogRules {
	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public ProductLog() {
		// default ctor
	}

	@Override
	public IExpr e1DblArg(final INum d) {
		try {
			return F.num(ApfloatMath.w(new Apfloat(d.doubleValue())));
		} catch (Exception ce) {

		}
		return F.complexNum(ApcomplexMath.w(new Apfloat(d.doubleValue())));
	}

	@Override
	public IExpr e1DblComArg(IComplexNum arg1) {
		return F.complexNum(ApcomplexMath.w(new Apcomplex(new Apfloat(arg1.getRealPart()), new Apfloat(arg1.getImaginaryPart()))));
	}

	@Override
	public IExpr e1ApfloatArg(Apfloat arg1) {
		try {
			return F.num(ApfloatMath.w(arg1));
		} catch (Exception ce) {

		}
		return F.complexNum(ApcomplexMath.w(arg1));
	}

	@Override
	public IExpr e1ApcomplexArg(Apcomplex arg1) {
		return F.complexNum(ApcomplexMath.w(arg1));
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}

}
