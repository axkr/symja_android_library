package org.matheclipse.core.series;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISymbol;

import junit.framework.TestCase;

public class GruntzTestNotEnabled extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// wait for initializing of Integrate() rules:
		F.join();
	}

	public void testGruntz() {
		ISymbol x = F.Dummy("x");
		ISymbol m = F.Dummy("m");
		assertEquals(Gruntz.compare(F.ZZ(2), x, x) < 0, true);
		assertEquals(Gruntz.compare(x, F.Exp(x), x) < 0, true);
		assertEquals(Gruntz.compare(F.Exp(x), F.Exp(F.Sqr(x)), x) < 0, true);
		assertEquals(Gruntz.compare(F.Exp(F.Sqr(x)), F.Exp(F.Exp(x)), x) < 0, true);
		assertEquals(Gruntz.compare(F.ZZ(1), F.Exp(F.Exp(x)), x) < 0, true);

		assertEquals(Gruntz.compare(x, F.ZZ(2), x) > 0, true);
		assertEquals(Gruntz.compare(F.Exp(x), x, x) > 0, true);
		assertEquals(Gruntz.compare(F.Exp(F.Sqr(x)), F.Exp(x), x) > 0, true);
		assertEquals(Gruntz.compare(F.Exp(F.Exp(x)), F.Exp(F.Sqr(x)), x) > 0, true);
		assertEquals(Gruntz.compare(F.Exp(F.Exp(x)), F.ZZ(1), x) > 0, true);

		assertEquals(Gruntz.compare(F.ZZ(2), F.ZZ(3), x) == 0, true);
		assertEquals(Gruntz.compare(F.ZZ(3), F.ZZ(-5), x) == 0, true);
		assertEquals(Gruntz.compare(F.ZZ(2), F.ZZ(-5), x) == 0, true);

		assertEquals(Gruntz.compare(x, F.Sqr(x), x) == 0, true);
		assertEquals(Gruntz.compare(F.Sqr(x), F.Power(x, F.C3), x) == 0, true);
		assertEquals(Gruntz.compare(F.Power(x, F.C3), F.Power(x, F.CN1), x) == 0, true);
		assertEquals(Gruntz.compare(F.Power(x, F.CN1), F.Power(x, m), x) == 0, true);
		assertEquals(Gruntz.compare(F.Power(x, m), x.negate(), x) == 0, true);

		assertEquals(Gruntz.compare(F.Exp(x), F.Exp(x.negate()), x) == 0, true);
		assertEquals(Gruntz.compare(F.Exp(x.negate()), F.Exp(F.Sqr(x)), x) == 0, true);
		assertEquals(Gruntz.compare(F.Exp(F.Times(F.C1, x)), F.Sqr(F.Exp(x)), x) == 0, true);
		assertEquals(Gruntz.compare(F.Sqr(F.Exp(x)), F.Exp(F.Plus(x, F.Exp(x.negate()))), x) == 0, true);
		assertEquals(Gruntz.compare(F.Exp(x), F.Exp(F.Plus(x, F.Exp(x.negate()))), x) == 0, true);

		assertEquals(Gruntz.compare(F.Exp(F.Sqr(x)), F.Power(F.Exp(F.Sqr(x)), F.CN1), x) == 0, true);

		assertEquals(Gruntz.compare(F.Exp(x), F.Power(x, F.C5), x) > 0, true);
		assertEquals(Gruntz.compare(F.Exp(F.Sqr(x)), F.Sqr(F.Exp(x)), x) > 0, true);
		assertEquals(Gruntz.compare(F.Exp(x), F.Exp(F.Plus(x, F.Exp(x.negate()))), x) == 0, true);
		assertEquals(Gruntz.compare(F.Exp(F.Plus(x, F.Exp(x.negate()))), F.Exp(x), x) == 0, true);
		assertEquals(Gruntz.compare(F.Exp(F.Plus(x, F.Exp(x.negate()))), F.Exp(x.negate()), x) == 0, true);
		assertEquals(Gruntz.compare(F.Exp(x.negate()), x, x) > 0, true);
		assertEquals(Gruntz.compare(x, F.Exp(x.negate()), x) < 0, true);
		assertEquals(Gruntz.compare(F.Exp(F.Plus(x, F.Power(x, F.CN1))), x, x) > 0, true);
		assertEquals(Gruntz.compare(F.Exp(F.Exp(x).negate()), F.Exp(x), x) > 0, true);
		// assert compare(exp(exp(-exp(x)) + x), exp(-exp(x)), x) < 0
		assertEquals(Gruntz.compare(F.Exp(F.Plus(F.Exp(F.Exp(x).negate()))), //
				F.Exp(F.Exp(x).negate()), //
				x) < 0, //
				true);

		assertEquals(Gruntz.compare(F.Exp(F.Exp(x)), F.Exp(F.Plus(x, F.Exp(F.Exp(x).negate()))), x) > 0, true);
	}

}
