package org.matheclipse.core.series;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Gruntz {
	public static int compare(IExpr a, IExpr b, IExpr x) {
		// The log(exp(...)) must always be simplified here for termination.
		IExpr la;
		IExpr lb;
		if (a.isPower() && a.base().isE()) {
			la = a.exponent();
		} else {
			la = F.Log(a);
		}
		if (b.isPower() && b.base().isE()) {
			lb = b.exponent();
		} else {
			lb = F.Log(b);
		}

		IExpr c = limitinf(la.divide(lb), x);
		if (c.isZero()) {
			return -1;
		} else if (c.isInfinity()) {
			return 1;
		}
		return 0;
	}

	public static IExpr[] mrv_leadterm(IExpr e, IExpr x) {
		if (!e.has(x)) {
			return new IExpr[] { e, F.C0 };
		}

		e = e.replace(f -> f.isPower() && f.exponent().has(x), f -> F.Exp(F.Times(F.Log(f.exponent()), f.base())));
		// e = e.replace(f->f.isTimes()&&sum(a.is_Pow for a in f.args) > 1,
		// f-> Mul(exp(Add(*[a.exp for a in f.args if a.is_Pow and a.base is E])),
		// *[a for a in f.args if not a.is_Pow or a.base is not E]))

		// The positive dummy, w, is used here so log(w*2) etc. will expand.
		// TODO: For limits of complex functions, the algorithm would have to
		// be improved, or just find limits of Re and Im components separately.
		EvalEngine engine = EvalEngine.get();
		IAssumptions assumptions = engine.getAssumptions();
		try {
			ISymbol w = F.Dummy("w");
			// real=True, positive=True
			engine.setAssumptions(Assumptions.getInstance(F.Greater(w, F.C0)));

			// e, logw = rewrite(e, x, w);
			IExpr[] result = new IExpr[2];
			return result;
		} finally {
			if (assumptions != null) {
				engine.setAssumptions(assumptions);
			}
		}
	}

	public static IExpr sign(IExpr e, IExpr x) {
		/**
		 * Determine a sign of an expression at infinity.
		 * 
		 * Returns =======
		 * 
		 * {1, 0, -1} One or minus one, if `e > 0` or `e < 0` for `x` sufficiently large and zero if `e` is *constantly*
		 * zero for `x\to\infty`.
		 * 
		 * The result of this function is currently undefined if `e` changes sign arbitrarily often at infinity (e.g.
		 * `\sin(x)`).
		 */

		if (e.isFree(x)) {
			return F.Simplify.of(F.Sign.of(e));
		} else if (e.equals(x)) {
			return F.C1;
		} else if (e.isTimes()) {
			// e.as_two_terms();
			IExpr a = e.first();
			IExpr b = e.rest();
			return F.Times.of(sign(a, x), sign(b, x));
		} else if (e.isPower()) {
			IExpr s = sign(e.first(), x);
			if (s.isOne()) {
				return F.C1;
			}
		}

		IExpr[] res = mrv_leadterm(e, x);
		IExpr c0 = res[0];
		IExpr e0 = res[1];
		return sign(c0, x);
	}

	public static IExpr limitinf(IExpr e, IExpr x) {
		// Rewrite e in terms of tractable functions only:

		// TODO rewrite
		// e = e.rewrite("tractable", true);

		// TODO transform_abs
		// def transform_abs(f):
		// s = sgn(limitinf(f.args[0], x))
		// return s*f.args[0] if s in (1, -1) else f
		//
		// e = e.replace(lambda f: isinstance(f, Abs) and f.has(x),
		// transform_abs)

		if (e.isFree(x)) {
			// This is a bit of a heuristic for nice results. We always rewrite
			// tractable functions in terms of familiar intractable ones.
			// TODO: It might be nicer to rewrite the exactly to what they were
			// initially, but that would take some work to implement.

			// TODO rewrite
			// return e.rewrite("intractable", true);
			return e;
		}

		IExpr[] r = mrv_leadterm(e, x);
		IExpr c0 = r[0];
		IExpr e0 = r[1];

		IExpr sig = sign(e0, x);
		if (sig.isOne()) {
			return F.C0;
		} else if (sig.isMinusOne()) {
			IExpr s = sign(c0, x);
			// assert s != 0
			return F.Times(s, F.CInfinity);
		} else if (sig.isZero()) {
			return limitinf(c0, x);
		}
		throw new UnsupportedOperationException("Result depends on the sign of %s");// % sig);
	}

}
