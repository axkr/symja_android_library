package org.matheclipse.core.convert.series;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.arith.BigRational;
import edu.jas.ps.Coefficients;
import edu.jas.ps.UnivPowerSeries;
import edu.jas.ps.UnivPowerSeriesMap;
import edu.jas.ps.UnivPowerSeriesRing;

/**
 * 
 * @deprecated doesn't work at the moment
 */
public class SeriesGenerator {
	private final UnivPowerSeriesRing<BigRational> psr;
	private final String varName;
	private final ISymbol fSymbol;

	public SeriesGenerator(int truncate, ISymbol symbol) {
		varName = symbol.toString();
		fSymbol = symbol;
		psr = new UnivPowerSeriesRing<BigRational>(BigRational.ONE, truncate, varName);

	}

	public UnivPowerSeries<BigRational> sin(int truncate) {
		final BigRational fac = new BigRational();
		final UnivPowerSeriesRing<BigRational> pfac = new UnivPowerSeriesRing<BigRational>(fac, truncate, varName);

		return new UnivPowerSeries<BigRational>(pfac, new Coefficients<BigRational>() {

			@Override
			public BigRational generate(int i) {
				BigRational c;
				if (i == 0) {
					c = fac.getZERO();
				} else if (i == 1) {
					c = fac.getONE();
				} else {
					c = get(i - 2).negate();
					c = c.divide(fac.fromInteger(i)).divide(fac.fromInteger(i - 1));
				}
				return c;
			}
		});
	}

	public UnivPowerSeries<BigRational> exp(int truncate) {

		final BigRational fac = new BigRational();
		final UnivPowerSeriesRing<BigRational> pfac = new UnivPowerSeriesRing<BigRational>(fac, truncate, varName);
		return pfac.fixPoint(new UnivPowerSeriesMap<BigRational>() {

			public UnivPowerSeries<BigRational> map(UnivPowerSeries<BigRational> e) {
				return e.integrate(fac.getONE());
			}
		});
	}

	private UnivPowerSeries<BigRational> exp() {
		return psr.getEXP();
	}

	public UnivPowerSeries<BigRational> exprToSeries(IExpr expr) {
		UnivPowerSeries<BigRational> ps = psr.getEXP();
		UnivPowerSeries<BigRational> temp;
		if (expr instanceof IAST) {
			IAST ast = (IAST) expr;
			if (ast.size() == 2) {
				temp = exprToSeries(ast.get(1));
				ps = psr.getEXP();
				// temp.map(ps);
			}
		}

		return ps;
	}
}
