package org.matheclipse.core.integrate.rubi;

import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.$str;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Defer;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Print;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.UnsameQ;
import static org.matheclipse.core.expression.F.u;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NumericFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;

import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pattern;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * UtilityFunctions from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 * TODO a lot of functions are only placeholders at the moment.
 * 
 */
public class UtilityFunctions {

	public final static ISymbol pa = F.initFinalHiddenSymbol("§a");
	public final static ISymbol pb = F.initFinalHiddenSymbol("§b");
	public final static ISymbol pc = F.initFinalHiddenSymbol("§c");
	public final static ISymbol pd = F.initFinalHiddenSymbol("§dv");
	public final static ISymbol pe = F.initFinalHiddenSymbol("§ev");
	public final static ISymbol pf = F.initFinalHiddenSymbol("§f");
	public final static ISymbol pg = F.initFinalHiddenSymbol("§g");
	public final static ISymbol ph = F.initFinalHiddenSymbol("§h");
	public final static ISymbol pi = F.initFinalHiddenSymbol("§i");
	public final static ISymbol pj = F.initFinalHiddenSymbol("§j");
	public final static ISymbol pk = F.initFinalHiddenSymbol("§k");
	public final static ISymbol pl = F.initFinalHiddenSymbol("§l");
	public final static ISymbol pm = F.initFinalHiddenSymbol("§m");
	public final static ISymbol pn = F.initFinalHiddenSymbol("§n");
	public final static ISymbol po = F.initFinalHiddenSymbol("§o");
	public final static ISymbol pp = F.initFinalHiddenSymbol("§p");
	public final static ISymbol pq = F.initFinalHiddenSymbol("§q");
	public final static ISymbol pr = F.initFinalHiddenSymbol("§r");
	public final static ISymbol ps = F.initFinalHiddenSymbol("§s");
	public final static ISymbol pt = F.initFinalHiddenSymbol("§t");
	public final static ISymbol pu = F.initFinalHiddenSymbol("§u");
	public final static ISymbol pv = F.initFinalHiddenSymbol("§v");
	public final static ISymbol pw = F.initFinalHiddenSymbol("§w");
	public final static ISymbol px = F.initFinalHiddenSymbol("§x");
	public final static ISymbol py = F.initFinalHiddenSymbol("§y");
	public final static ISymbol pz = F.initFinalHiddenSymbol("§z");
	public final static IPattern pa_ = F.initPredefinedPattern(pa);
	public final static IPattern pb_ = F.initPredefinedPattern(pb);
	public final static IPattern pc_ = F.initPredefinedPattern(pc);
	public final static IPattern pd_ = F.initPredefinedPattern(pd);
	public final static IPattern pe_ = F.initPredefinedPattern(pe);
	public final static IPattern pf_ = F.initPredefinedPattern(pf);
	public final static IPattern pg_ = F.initPredefinedPattern(pg);
	public final static IPattern ph_ = F.initPredefinedPattern(ph);
	public final static IPattern pi_ = F.initPredefinedPattern(pi);
	public final static IPattern pj_ = F.initPredefinedPattern(pj);
	public final static IPattern pk_ = F.initPredefinedPattern(pk);
	public final static IPattern pl_ = F.initPredefinedPattern(pl);
	public final static IPattern pm_ = F.initPredefinedPattern(pm);
	public final static IPattern pn_ = F.initPredefinedPattern(pn);
	public final static IPattern po_ = F.initPredefinedPattern(po);
	public final static IPattern pp_ = F.initPredefinedPattern(pp);
	public final static IPattern pq_ = F.initPredefinedPattern(pq);
	public final static IPattern pr_ = F.initPredefinedPattern(pr);
	public final static IPattern ps_ = F.initPredefinedPattern(ps);
	public final static IPattern pt_ = F.initPredefinedPattern(pt);
	public final static IPattern pu_ = F.initPredefinedPattern(pu);
	public final static IPattern pv_ = F.initPredefinedPattern(pv);
	public final static IPattern pw_ = F.initPredefinedPattern(pw);
	public final static IPattern px_ = F.initPredefinedPattern(px);
	public final static IPattern py_ = F.initPredefinedPattern(py);
	public final static IPattern pz_ = F.initPredefinedPattern(pz);

	public final static IPattern pa_DEFAULT = new Pattern(pa, null, true);
	public final static IPattern pb_DEFAULT = new Pattern(pb, null, true);
	public final static IPattern pc_DEFAULT = new Pattern(pc, null, true);
	public final static IPattern pd_DEFAULT = new Pattern(pd, null, true);
	public final static IPattern pe_DEFAULT = new Pattern(pe, null, true);
	public final static IPattern pf_DEFAULT = new Pattern(pf, null, true);
	public final static IPattern pg_DEFAULT = new Pattern(pg, null, true);
	public final static IPattern ph_DEFAULT = new Pattern(ph, null, true);
	public final static IPattern pi_DEFAULT = new Pattern(pi, null, true);
	public final static IPattern pj_DEFAULT = new Pattern(pj, null, true);
	public final static IPattern pk_DEFAULT = new Pattern(pk, null, true);
	public final static IPattern pl_DEFAULT = new Pattern(pl, null, true);
	public final static IPattern pm_DEFAULT = new Pattern(pm, null, true);
	public final static IPattern pn_DEFAULT = new Pattern(pn, null, true);
	public final static IPattern po_DEFAULT = new Pattern(po, null, true);
	public final static IPattern pp_DEFAULT = new Pattern(pp, null, true);
	public final static IPattern pq_DEFAULT = new Pattern(pq, null, true);
	public final static IPattern pr_DEFAULT = new Pattern(pr, null, true);
	public final static IPattern ps_DEFAULT = new Pattern(ps, null, true);
	public final static IPattern pt_DEFAULT = new Pattern(pt, null, true);
	public final static IPattern pu_DEFAULT = new Pattern(pu, null, true);
	public final static IPattern pv_DEFAULT = new Pattern(pv, null, true);
	public final static IPattern pw_DEFAULT = new Pattern(pw, null, true);
	public final static IPattern px_DEFAULT = new Pattern(px, null, true);
	public final static IPattern py_DEFAULT = new Pattern(py, null, true);
	public final static IPattern pz_DEFAULT = new Pattern(pz, null, true);

}