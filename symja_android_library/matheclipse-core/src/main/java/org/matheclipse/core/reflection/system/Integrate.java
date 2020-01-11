package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.integrate.rubi.UtilityFunctionCtors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.RulesData;

import com.google.common.cache.CacheBuilder;

/**
 * <pre>
 * Integrate(f, x)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * integrates <code>f</code> with respect to <code>x</code>. The result does not contain the additive integration
 * constant.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * Integrate(f, {x,a,b})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * computes the definite integral of <code>f</code> with respect to <code>x</code> from <code>a</code> to
 * <code>b</code>.
 * </p>
 * </blockquote>
 * <p>
 * See: <a href="https://en.wikipedia.org/wiki/Integral">Wikipedia: Integral</a>
 * </p>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; Integrate(x^2, x)
 * x^3/3
 * 
 * &gt;&gt; Integrate(Tan(x) ^ 5, x)
 * -Log(Cos(x))-Tan(x)^2/2+Tan(x)^4/4
 * </pre>
 */
public class Integrate extends AbstractFunctionEvaluator {
	private static Thread INIT_THREAD = null;

	private final static CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

	/**
	 * Causes the current thread to wait until the INIT_THREAD has initialized the Integrate() rules.
	 *
	 */
	public final void await() throws InterruptedException {
		COUNT_DOWN_LATCH.await();
	}

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	public static class IntegrateInitializer implements Runnable {

		@Override
		public void run() {
			// long start = System.currentTimeMillis();
			if (!INTEGRATE_RULES_READ.get()) {
				INTEGRATE_RULES_READ.set(true);
				final EvalEngine engine = EvalEngine.get();
				ContextPath path = engine.getContextPath();
				try {
					engine.getContextPath().add(org.matheclipse.core.expression.Context.RUBI);
					getUtilityFunctionsRuleASTRubi45();
					getRuleASTStatic();
				} finally {
					engine.setContextPath(path);
				}
				// F.Integrate.setEvaluator(CONST);
				engine.setPackageMode(false);
				// long stop = System.currentTimeMillis();
				// System.out.println("Milliseconds: " + (stop - start));
				COUNT_DOWN_LATCH.countDown();
			}
		}

		private static synchronized void getRuleASTStatic() {
			INTEGRATE_RULES_DATA = F.Integrate.createRulesData(new int[] { 0, 7000 });
			getRuleASTRubi45();

			ISymbol[] rubiSymbols = { F.Derivative, F.D };
			for (int i = 0; i < rubiSymbols.length; i++) {
				INT_RUBI_FUNCTIONS.add(rubiSymbols[i]);
			}
		}

		private static void getUtilityFunctionsRuleASTRubi45() {
			IAST ast = org.matheclipse.core.integrate.rubi.UtilityFunctions0.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions1.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions2.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions3.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions4.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions5.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions6.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions7.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions8.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions9.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions10.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions11.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions12.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions13.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions14.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions15.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions16.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions17.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions18.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions19.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions20.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions21.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions22.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions23.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions24.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions25.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions26.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions27.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions28.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions29.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions30.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions31.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions32.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions33.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions34.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions35.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions36.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions37.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions38.RULES;
			ast = org.matheclipse.core.integrate.rubi.UtilityFunctions39.RULES;
			// org.matheclipse.core.integrate.rubi.UtilityFunctions.init();
		}

		private static void getRuleASTRubi45() {
			IAST init;
			init = org.matheclipse.core.integrate.rubi.IntRules0.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules1.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules2.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules3.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules4.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules5.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules6.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules7.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules8.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules9.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules10.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules11.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules12.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules13.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules14.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules15.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules16.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules17.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules18.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules19.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules20.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules21.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules22.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules23.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules24.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules25.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules26.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules27.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules28.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules29.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules30.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules31.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules32.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules33.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules34.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules35.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules36.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules37.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules38.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules39.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules40.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules41.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules42.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules43.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules44.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules45.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules46.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules47.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules48.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules49.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules50.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules51.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules52.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules53.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules54.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules55.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules56.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules57.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules58.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules59.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules60.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules61.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules62.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules63.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules64.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules65.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules66.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules67.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules68.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules69.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules70.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules71.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules72.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules73.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules74.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules75.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules76.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules77.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules78.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules79.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules80.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules81.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules82.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules83.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules84.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules85.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules86.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules87.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules88.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules89.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules90.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules91.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules92.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules93.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules94.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules95.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules96.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules97.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules98.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules99.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules100.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules101.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules102.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules103.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules104.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules105.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules106.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules107.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules108.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules109.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules110.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules111.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules112.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules113.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules114.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules115.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules116.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules117.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules118.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules119.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules120.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules121.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules122.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules123.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules124.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules125.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules126.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules127.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules128.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules129.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules130.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules131.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules132.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules133.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules134.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules135.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules136.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules137.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules138.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules139.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules140.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules141.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules142.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules143.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules144.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules145.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules146.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules147.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules148.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules149.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules150.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules151.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules152.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules153.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules154.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules155.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules156.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules157.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules158.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules159.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules160.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules161.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules162.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules163.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules164.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules165.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules166.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules167.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules168.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules169.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules170.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules171.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules172.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules173.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules174.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules175.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules176.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules177.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules178.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules179.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules180.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules181.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules182.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules183.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules184.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules185.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules186.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules187.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules188.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules189.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules190.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules191.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules192.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules193.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules194.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules195.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules196.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules197.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules198.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules199.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules200.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules201.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules202.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules203.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules204.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules205.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules206.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules207.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules208.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules209.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules210.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules211.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules212.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules213.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules214.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules215.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules216.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules217.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules218.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules219.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules220.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules221.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules222.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules223.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules224.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules225.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules226.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules227.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules228.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules229.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules230.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules231.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules232.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules233.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules234.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules235.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules236.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules237.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules238.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules239.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules240.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules241.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules242.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules243.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules244.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules245.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules246.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules247.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules248.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules249.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules250.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules251.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules252.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules253.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules254.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules255.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules256.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules257.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules258.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules259.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules260.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules261.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules262.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules263.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules264.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules265.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules266.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules267.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules268.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules269.RULES;

			init = org.matheclipse.core.integrate.rubi.IntRules270.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules271.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules272.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules273.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules274.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules275.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules276.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules277.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules278.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules279.RULES;

			init = org.matheclipse.core.integrate.rubi.IntRules280.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules281.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules282.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules283.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules284.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules285.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules286.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules287.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules288.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules289.RULES;

			init = org.matheclipse.core.integrate.rubi.IntRules290.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules291.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules292.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules293.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules294.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules295.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules296.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules297.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules298.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules299.RULES;

			init = org.matheclipse.core.integrate.rubi.IntRules300.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules301.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules302.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules303.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules304.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules305.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules306.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules307.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules308.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules309.RULES;

			init = org.matheclipse.core.integrate.rubi.IntRules310.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules311.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules312.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules313.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules314.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules315.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules316.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules317.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules318.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules319.RULES;

			init = org.matheclipse.core.integrate.rubi.IntRules320.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules321.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules322.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules323.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules324.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules325.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules326.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules327.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules328.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules329.RULES;

			init = org.matheclipse.core.integrate.rubi.IntRules330.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules331.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules332.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules333.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules334.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules335.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules336.RULES;
			init = org.matheclipse.core.integrate.rubi.IntRules337.RULES;

		}
	}

	public static RulesData INTEGRATE_RULES_DATA;
	/**
	 * Constructor for the singleton
	 */
	public final static Integrate CONST = new Integrate();

	public final static Set<ISymbol> INT_RUBI_FUNCTIONS = new HashSet<ISymbol>();

	public final static Set<IExpr> DEBUG_EXPR = new HashSet<IExpr>(64);

	public static final AtomicBoolean INTEGRATE_RULES_READ = new AtomicBoolean(false);

	public Integrate() {
	}

	@Override
	public IExpr evaluate(final IAST holdallAST, EvalEngine engine) {
		if (Config.JAS_NO_THREADS) {
			// Android changed: call static initializer in evaluate() method.
			new IntegrateInitializer().run();
		} else {
			// see #setUp() method
		}
		try {
			// wait for initializer runs completely, no matter how many threads call evaluate() method
			await();
		} catch (InterruptedException ignored) {
		}
		boolean evaled = false;
		IExpr result;
		boolean numericMode = engine.isNumericMode();
		try {
			engine.setNumericMode(false);
			if (holdallAST.size() < 3 || holdallAST.isEvalFlagOn(IAST.BUILT_IN_EVALED)) {
				return F.NIL;
			}
			final IExpr a1 = NumberTheory.rationalize(holdallAST.arg1()).orElse(holdallAST.arg1());
			IExpr arg1 = engine.evaluateNull(a1);
			if (arg1.isPresent()) {
				evaled = true;
			} else {
				arg1 = a1;
			}
			if (arg1.isIndeterminate()) {
				return F.Indeterminate;
			}
			if (holdallAST.size() > 3) {
				// reduce arguments by folding Integrate[fxy, x, y] to
				// Integrate[Integrate[fxy, y], x] ...
				return holdallAST.foldRight((x, y) -> engine.evaluate(F.Integrate(x, y)), arg1, 2);
			}

			IExpr arg2 = engine.evaluateNull(holdallAST.arg2());
			if (arg2.isPresent()) {
				evaled = true;
			} else {
				arg2 = holdallAST.arg2();
			}
			if (arg2.isList()) {
				IAST xList = (IAST) arg2;
				if (xList.isVector() == 3) {
					// Integrate[f[x], {x,a,b}]
					IAST copy = holdallAST.setAtCopy(2, xList.arg1());
					IExpr temp = engine.evaluate(copy);
					if (temp.isFreeAST(F.Integrate)) {
						return definiteIntegral(temp, xList, engine);
					}
				}
				return F.NIL;
			}
			if (arg1.isList() && arg2.isSymbol()) {
				return mapIntegrate((IAST) arg1, arg2);
			}

			final IASTAppendable ast = holdallAST.setAtClone(1, arg1);
			ast.set(2, arg2);
			final IExpr x = ast.arg2();

			if (arg1.isNumber()) {
				// Integrate[x_?NumberQ,y_Symbol] -> x*y
				return Times(arg1, x);
			}
			if (arg1 instanceof ASTSeriesData) {
				ASTSeriesData series = ((ASTSeriesData) arg1);
				if (series.getX().equals(x)) {
					final IExpr temp = ((ASTSeriesData) arg1).integrate(x);
					if (temp != null) {
						return temp;
					}
				}
				return F.NIL;
			}
			if (arg1.isFree(x, true)) {
				// Integrate[x_,y_Symbol] -> x*y /; FreeQ[x,y]
				return Times(arg1, x);
			}
			if (arg1.equals(x)) {
				// Integrate[x_,x_Symbol] -> x^2 / 2
				return Times(F.C1D2, Power(arg1, F.C2));
			}
			boolean showSteps = false;
			if (showSteps) {
				System.out.println("\nINTEGRATE: " + arg1.toString());
				if (DEBUG_EXPR.contains(arg1)) {
					// System.exit(-1);
				}
				DEBUG_EXPR.add(arg1);
			}
			if (arg1.isAST()) {
				final IAST fx = (IAST) arg1;
				if (fx.topHead().equals(x)) {
					// issue #91
					return F.NIL;
				}

				if (fx.isAST(F.Piecewise) && fx.size() >= 2 && fx.arg1().isList()) {
					return integratePiecewise(fx, ast);
				}
				result = integrateAbs(arg1, x);
				if (result.isPresent()) {
					if (result == F.Undefined) {
						return F.NIL;
					}
					return result;
				}

				result = integrateByRubiRules(fx, x, ast, engine);
				if (result.isPresent()) {
					IExpr temp = result.replaceAll(f -> {
						if (f.isAST(UtilityFunctionCtors.Unintegrable, 3)) {
							IAST integrate = F.Integrate(f.first(), f.second());
							integrate.addEvalFlags(IAST.BUILT_IN_EVALED);
							return integrate;
						} else if (f.isAST(F.$rubi("CannotIntegrate"), 3)) {
							IAST integrate = F.Integrate(f.first(), f.second());
							integrate.addEvalFlags(IAST.BUILT_IN_EVALED);
							return integrate;
						}
						return F.NIL;
					});
					return temp.orElse(result);
				}

				if (arg1.isTimes()) {
					IAST[] temp = ((IAST) arg1).filter(arg -> arg.isFree(x));
					IExpr free = temp[0].oneIdentity1();
					if (!free.isOne()) {
						IExpr rest = temp[1].oneIdentity1();
						// Integrate[free_ * rest_,x_Symbol] -> free*Integrate[rest, x] /; FreeQ[free,x]
						return Times(free, Integrate(rest, x));
					}
				}

				if (fx.isPower()) {
					// base ^ exponent
					IExpr base = fx.base();
					IExpr exponent = fx.exponent();
					if (base.equals(x) && exponent.isFree(x)) {
						if (exponent.isMinusOne()) {
							// Integrate[ 1 / x_ , x_ ] -> Log[x]
							return Log(x);
						}
						// Integrate[ x_ ^n_ , x_ ] -> x^(n+1)/(n+1) /; FreeQ[n, x]
						IExpr temp = Plus(F.C1, exponent);
						return Divide(Power(x, temp), temp);
					}
					if (exponent.equals(x) && base.isFree(x)) {
						if (base.isE()) {
							// E^x
							return arg1;
						}
						// a^x / Log(a)
						return F.Divide(fx, F.Log(base));
					}
				}

				result = callRestIntegrate(fx, x, engine);
				if (result.isPresent()) {
					return result;
				}

			}
			return evaled ? ast : F.NIL;
		} finally {
			engine.setNumericMode(numericMode);
		}
	}

	private static IExpr integratePiecewise(final IAST piecewiseFunction, final IAST integrateFunction) {
		int[] dim = piecewiseFunction.arg1().isMatrix(false);
		if (dim != null && dim[0] > 0 && dim[1] == 2) {
			IAST list = (IAST) piecewiseFunction.arg1();
			if (list.size() > 1) {
				IASTAppendable pwResult = F.ListAlloc(list.size());
				for (int i = 1; i < list.size(); i++) {
					IASTMutable integrate = ((IAST) integrateFunction).copy();
					integrate.set(1, list.get(i).first());
					pwResult.append(F.List(integrate, list.get(i).second()));
				}
				IASTMutable piecewise = ((IAST) piecewiseFunction).copy();
				piecewise.set(1, pwResult);
				if (piecewiseFunction.size() > 2) {
					IASTMutable integrate = ((IAST) integrateFunction).copy();
					integrate.set(1, piecewiseFunction.second());
					piecewise.set(2, integrate);
				}
				return piecewise;
			}
		}
		return F.NIL;
	}

	/**
	 * Integrate forms of <code>Abs()</code> or <code>Abs()^n</code> with <code>n^n</code> integer.
	 * 
	 * @param function
	 * @param x
	 *            assumes to be an element of the Reals
	 * @return
	 */
	private static IExpr integrateAbs(IExpr function, final IExpr x) {
		IExpr constant = F.C0;

		if (x.isRealResult()) {
			if (function.isAbs()) {
				// Abs(x)
				IAST abs = (IAST) function;
				IExpr[] lin = abs.arg1().linearPower(x);
				if (lin != null && !lin[1].isZero() && lin[0].isRealResult() && lin[1].isRealResult()
						&& lin[2].isInteger()) {
					// Abs(l0 + l1 * x^exp)

					IExpr l0 = lin[0];
					IExpr l1 = lin[1];
					IInteger exp = (IInteger) lin[2];
					constant = F.Divide(F.Negate(l0), l1);
					if (exp.isOne()) {
						// Piecewise({{(-l0)*x - (l1*x^2)/2, x <= constant}}, l0^2/Pi + l0*x + (l1*x^2)/2)
						return F.Piecewise(//
								F.List(F.List(//
										F.Plus(F.Times(F.CN1, l0, F.x), F.Times(F.CN1D2, l1, F.Sqr(F.x))),
										F.LessEqual(F.x, constant))),
								F.Plus(F.Times(F.Sqr(l0), F.Power(F.Pi, F.CN1)), F.Times(l0, F.x),
										F.Times(F.C1D2, l1, F.Sqr(F.x))));
					} else if (exp.isMinusOne()) {
						// Abs(l0 + l1 * x^(-1))

						if (!l0.isZero()) {
							// Piecewise({{l0*x + l1*Log(x), x <= -(l1/l0)},
							// {(-l0)*x - l1*(2 - I*l1 - Log(l1)) + l1*(-2 + I*l1 + Log(l1)) - l1*Log(x),
							// Inequality(-(l1/l0), Less, x, LessEqual, 0)}}, l0*x + l1*Log(x))
							return F.Piecewise(F.List(F.List(//
									F.Plus(F.Times(l0, F.x), F.Times(l1, F.Log(F.x))),
									F.LessEqual(F.x, F.Times(F.CN1, F.Power(l0, F.CN1), l1))),
									F.List(F.Plus(F.Times(F.CN1, l0, F.x),
											F.Times(F.C2, l1, F.Plus(F.CN2, F.Times(F.CI, l1), F.Log(l1))),
											F.Times(F.CN1, l1, F.Log(F.x))),
											F.And(F.Less(F.Times(F.CN1, F.Power(l0, F.CN1), l1), F.x),
													F.LessEqual(F.x, F.C0)))),
									F.Plus(F.Times(l0, F.x), F.Times(l1, F.Log(F.x))));
						}
					} else if (exp.isPositive()) {
						IInteger expP1 = exp.inc();
						if (exp.isEven()) {
							// l0*x + (l1*x^(expP1))/(expP1)
							return F.Plus(F.Times(l0, F.x), F.Times(expP1.inverse(), l1, F.Power(F.x, expP1)));
						}
					} else if (exp.isNegative()) {
						IInteger expP1 = exp.inc();
						if (exp.isEven()) {
							// -(l1/(expP1*x^expP1)) + l0*x
							return F.Plus(F.Times(l0, F.x),
									F.Times(F.CN1, F.Power(expP1, F.CN1), l1, F.Power(F.x, F.Negate(expP1))));
						}

					}
				}
			} else if (function.isPower() && function.base().isAbs() && function.exponent().isInteger()) {
				IAST power = (IAST) function;
				IAST abs = (IAST) power.base();

				IExpr[] lin = abs.arg1().linear(x);
				if (lin != null && !lin[1].isZero() && lin[0].isRealResult() && lin[1].isRealResult()) {
					// Abs(l0 + l1 * x) ^ exp
					IExpr l0 = lin[0];
					IExpr l1 = lin[1];
					constant = F.Divide(F.Negate(l0), l1);
					IInteger exp = (IInteger) power.exponent();
					IInteger expP1 = exp.inc();
					if (exp.isNegative()) {
						if (exp.isMinusOne()) {
							// Abs(l0 + l1 * x) ^ (-1)

							return F.Piecewise( //
									F.List(F.List(F.Negate(F.Log(x)), F.LessEqual(x, constant))), //
									F.Log(x));
						}
						if (exp.isEven()) {
							return F.Times(expP1.inverse().negate(), F.Power(x, expP1));
						}
						return F.Piecewise( //
								F.List(F.List(F.Times(expP1.inverse().negate(), F.Power(x, expP1)),
										F.LessEqual(x, constant))), //
								F.Times(expP1.inverse(), F.Power(x, expP1)));
					}
					if (exp.isEven()) {
						return F.Divide(F.Power(x, expP1), expP1);
					}
					return F.Piecewise( //
							F.List(F.List(F.Divide(F.Power(x, expP1), expP1.negate()), F.LessEqual(x, constant))), //
							F.Divide(F.Power(x, expP1), expP1));
				}
			}
		}
		if (function.isAbs() || //
				(function.isPower() && function.base().isAbs())) {
			return F.Undefined;
		}
		return F.NIL;
	}

	/**
	 * <p>
	 * Given a <code>function</code> of a real variable <code>x</code> and an interval <code>[a, b]</code> of the real
	 * line, calculate the definite integral <code>F(b)-F(a)</code>.
	 * </p>
	 * <a href="https://en.wikipedia.org/wiki/Integral">Wikipedia - Integral</a>
	 * 
	 * @param function
	 *            a function of <code>x</code>
	 * @param xValueList
	 *            a list of the form <code>{x,a,b}</code> with <code>3</code> arguments
	 * @param engine
	 *            the evaluation engine
	 * @return
	 */
	private static IExpr definiteIntegral(IExpr function, IAST xValueList, EvalEngine engine) {
		IExpr x = xValueList.arg1();
		IExpr a = xValueList.arg2();
		IExpr b = xValueList.arg3();
		IExpr Fb = engine.evaluate(F.Limit(function, F.Rule(x, b)));
		IExpr Fa = engine.evaluate(F.Limit(function, F.Rule(x, a)));
		if (!Fb.isFree(F.DirectedInfinity, true) || !Fb.isFree(F.Indeterminate, true)) {
			return engine.printMessage("Not integrable: " + function + " for limit " + x + " -> " + b);
		}
		if (!Fa.isFree(F.DirectedInfinity, true) || !Fa.isFree(F.Indeterminate, true)) {
			return engine.printMessage("Not integrable: " + function + " for limit " + x + " -> " + a);
		}
		if (a.isNegativeResult() && b.isPositiveResult()) {
			// 0 is a value inside he given interval
			IExpr FZeroAbove = engine.evaluate(F.Limit(function, F.Rule(x, F.C0), F.Rule(F.Direction, F.CN1)));
			if (!FZeroAbove.isFree(F.DirectedInfinity, true) || !FZeroAbove.isFree(F.Indeterminate, true)) {
				return engine.printMessage("Not integrable: " + function + " for limit " + x + " -> 0");
			}
			IExpr FZeroBelow = engine.evaluate(F.Limit(function, F.Rule(x, F.C0), F.Rule(F.Direction, F.C1)));
			if (!FZeroBelow.isFree(F.DirectedInfinity, true) || !FZeroBelow.isFree(F.Indeterminate, true)) {
				return engine.printMessage("Not integrable: " + function + " for limit " + x + " -> 0");
			}
			return F.Plus(F.Subtract(Fb, FZeroAbove), F.Subtract(FZeroBelow, Fa));
		}
		if (Fb.isAST() && Fa.isAST()) {
			IExpr bDenominator = F.Denominator.of(engine, Fb);
			IExpr aDenominator = F.Denominator.of(engine, Fa);
			if (bDenominator.equals(aDenominator)) {
				return F.Divide(F.Subtract(F.Numerator(Fb), F.Numerator(Fa)), bDenominator);
			}
		}
		return F.Subtract(Fb, Fa);
	}

	private static IExpr callRestIntegrate(IAST arg1, final IExpr x, final EvalEngine engine) {
		IExpr fxExpanded = F.expand(arg1, false, false, false);
		if (fxExpanded.isAST()) {
			if (fxExpanded.isPlus()) {
				return mapIntegrate((IAST) fxExpanded, x);
			}

			final IAST arg1AST = (IAST) fxExpanded;
			if (arg1AST.isTimes()) {
				// Integrate[a_*y_,x_Symbol] -> a*Integrate[y,x] /; FreeQ[a,x]
				IASTAppendable filterCollector = F.TimesAlloc(arg1AST.size());
				IASTAppendable restCollector = F.TimesAlloc(arg1AST.size());
				arg1AST.filter(filterCollector, restCollector, new Predicate<IExpr>() {
					@Override
					public boolean test(IExpr input) {
						return input.isFree(x, true);
					}
				});
				if (filterCollector.size() > 1) {
					if (restCollector.size() > 1) {
						filterCollector.append(F.Integrate(restCollector.oneIdentity0(), x));
					}
					return filterCollector;
				}

				// IExpr temp = integrateTimesTrigFunctions(arg1AST, x);
				// if (temp.isPresent()) {
				// return temp;
				// }
			}

			if (arg1AST.size() >= 3 && arg1AST.isFree(F.Integrate) && arg1AST.isPlusTimesPower()) {
				if (!arg1AST.isEvalFlagOn(IAST.IS_DECOMPOSED_PARTIAL_FRACTION) && x.isSymbol()) {
					IExpr[] parts = Algebra.fractionalParts(arg1, true);
					if (parts != null) {
						IExpr temp = Algebra.partsApart(parts, x, engine);
						if (temp.isPresent() && !temp.equals(arg1)) {
							if (temp.isPlus()) {
								return mapIntegrate((IAST) temp, x);
							}
							// return F.Integrate(temp, x);
							// return mapIntegrate((IAST) temp, x);
						}
						// if (temp.isPlus()) {
						// return mapIntegrate((IAST) temp, x);
						// }
						// return Algebra.partialFractionDecompositionRational(new
						// PartialFractionIntegrateGenerator(x),parts, x);
					}
				}
			}
		}
		if (arg1.isTrigFunction() || arg1.isHyperbolicFunction()) {
			// https://github.com/RuleBasedIntegration/Rubi/issues/12
			IExpr temp = engine.evaluate(F.TrigToExp(arg1));
			return engine.evaluate(F.Integrate(temp, x));
		}
		return F.NIL;
	}

	/**
	 * Map <code>Integrate</code> on <code>ast</code>. Examples:
	 * <ul>
	 * <li><code>Integrate[{a_, b_,...},x_] -> {Integrate[a,x], Integrate[b,x], ...}</code> or</li>
	 * <li><code>Integrate[a_+b_+...,x_] -> Integrate[a,x]+Integrate[b,x]+...</code></li>
	 * </ul>
	 * 
	 * @param ast
	 *            a <code>List(...)</code> or <code>Plus(...)</code> ast
	 * @param x
	 *            the integ ration veariable
	 * @return
	 */
	private static IExpr mapIntegrate(IAST ast, final IExpr x) {
		return ast.mapThread(F.Integrate(null, x), 1);
	}

	/**
	 * See <a href="http://en.wikipedia.org/wiki/Integration_by_parts">Wikipedia- Integration by parts</a>
	 * 
	 * @param ast
	 *            TODO - not used
	 * @param arg1
	 * @param symbol
	 * 
	 * @return
	 */
	private static IExpr integratePolynomialByParts(IAST ast, final IAST arg1, IExpr symbol, EvalEngine engine) {
		IASTAppendable fTimes = F.TimesAlloc(arg1.size());
		IASTAppendable gTimes = F.TimesAlloc(arg1.size());
		collectPolynomialTerms(arg1, symbol, gTimes, fTimes);
		IExpr g = gTimes.oneIdentity1();
		IExpr f = fTimes.oneIdentity1();
		// conflicts with Rubi 4.5 integration rules
		// only call integrateByParts for simple Times() expressions
		if (f.isOne() || g.isOne()) {
			return F.NIL;
		}
		return integrateByParts(f, g, symbol, engine);
	}

	/**
	 * Use the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - Symbolic Integration Rules</a> to integrate the
	 * expression.
	 * 
	 * @param ast
	 * @return
	 */
	private static IExpr integrateByRubiRules(IAST arg1, IExpr x, IAST ast, EvalEngine engine) {
		// EvalEngine engine = EvalEngine.get();
		int limit = engine.getRecursionLimit();
		boolean quietMode = engine.isQuietMode();
		ISymbol head = arg1.topHead();

		if (head.isNumericFunctionAttribute() || INT_RUBI_FUNCTIONS.contains(head)
				|| head.getSymbolName().startsWith("§")) {

			boolean newCache = false;
			try {

				if (engine.REMEMBER_AST_CACHE != null) {
					IExpr result = engine.REMEMBER_AST_CACHE.getIfPresent(ast);
					if (result != null) {// &&engine.getRecursionCounter()>0) {
						if (result.isPresent()) {
							return result;
						}
						return callRestIntegrate(arg1, x, engine);
					}
				} else {
					newCache = true;
					engine.REMEMBER_AST_CACHE = CacheBuilder.newBuilder().maximumSize(50).build();
				}
				try {
					engine.setQuietMode(true);
					if (limit <= 0 || limit > Config.INTEGRATE_RUBI_RULES_RECURSION_LIMIT) {
						engine.setRecursionLimit(Config.INTEGRATE_RUBI_RULES_RECURSION_LIMIT);
					}

					// System.out.println(ast.toString());
					engine.REMEMBER_AST_CACHE.put(ast, F.NIL);
					IExpr temp = F.Integrate.evalDownRule(EvalEngine.get(), ast);
					if (temp.isPresent()) {
						engine.REMEMBER_AST_CACHE.put(ast, temp);
						return temp;
					}
				} catch (RecursionLimitExceeded rle) {
					// engine.printMessage("Integrate(Rubi recursion): " + Config.INTEGRATE_RUBI_RULES_RECURSION_LIMIT
					// + " exceeded: " + ast.toString());
					engine.setRecursionLimit(limit);
					return engine.printMessage("Integrate(Rubi recursion): " + rle.getMessage());
				} catch (RuntimeException rex) {
					if (Config.SHOW_STACKTRACE) {
						rex.printStackTrace();
					}
					engine.setRecursionLimit(limit);
					return engine.printMessage("Integrate Rubi recursion limit "
							+ Config.INTEGRATE_RUBI_RULES_RECURSION_LIMIT + " RuntimeException: " + ast.toString());

				}

			} catch (AbortException ae) {
				if (Config.DEBUG) {
					ae.printStackTrace();
				}
			} catch (final FailedException fe) {
				if (Config.DEBUG) {
					fe.printStackTrace();
				}
			} finally {
				engine.setRecursionLimit(limit);
				if (newCache) {
					engine.REMEMBER_AST_CACHE = null;
				}
				engine.setQuietMode(quietMode);
			}
		}
		return F.NIL;
	}

	/**
	 * <p>
	 * Integrate by parts rule: <code>Integrate(f'(x) * g(x), x) = f(x) * g(x) - Integrate(f(x) * g'(x),x )</code> .
	 * </p>
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Integration_by_parts">Wikipedia- Integration by parts</a>
	 * 
	 * @param f
	 *            <code>f(x)</code>
	 * @param g
	 *            <code>g(x)</code>
	 * @param x
	 * @return <code>f(x) * g(x) - Integrate(f(x) * g'(x),x )</code>
	 */
	private static IExpr integrateByParts(IExpr f, IExpr g, IExpr x, EvalEngine engine) {
		int limit = engine.getRecursionLimit();
		try {
			if (limit <= 0 || limit > Config.INTEGRATE_BY_PARTS_RECURSION_LIMIT) {
				engine.setRecursionLimit(Config.INTEGRATE_BY_PARTS_RECURSION_LIMIT);
			}
			IExpr firstIntegrate = engine.evaluate(F.Integrate(f, x));
			if (!firstIntegrate.isFreeAST(Integrate)) {
				return F.NIL;
			}
			IExpr gDerived = F.eval(F.D(g, x));
			IExpr second2Integrate = F.eval(F.Integrate(F.Times(gDerived, firstIntegrate), x));
			if (!second2Integrate.isFreeAST(Integrate)) {
				return F.NIL;
			}
			return F.eval(F.Subtract(F.Times(g, firstIntegrate), second2Integrate));
		} catch (RecursionLimitExceeded rle) {
			engine.setRecursionLimit(limit);
		} finally {
			engine.setRecursionLimit(limit);
		}
		return F.NIL;
	}

	/**
	 * Collect all found polynomial terms into <code>polyTimes</code> and the rest into <code>restTimes</code>.
	 * 
	 * @param timesAST
	 *            an AST representing a <code>Times[...]</code> expression.
	 * @param symbol
	 * @param polyTimes
	 *            the polynomial terms part
	 * @param restTimes
	 *            the non-polynomil terms part
	 */
	private static void collectPolynomialTerms(final IAST timesAST, IExpr symbol, IASTAppendable polyTimes,
			IASTAppendable restTimes) {
		IExpr temp;
		for (int i = 1; i < timesAST.size(); i++) {
			temp = timesAST.get(i);
			if (temp.isFree(symbol, true)) {
				polyTimes.append(temp);
				continue;
			} else if (temp.equals(symbol)) {
				polyTimes.append(temp);
				continue;
			} else if (temp.isPolynomial(List(symbol))) {
				polyTimes.append(temp);
				continue;
			}
			restTimes.append(temp);
		}
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
		super.setUp(newSymbol);

		if (Config.THREAD_FACTORY != null) {
			INIT_THREAD = Config.THREAD_FACTORY.newThread(new IntegrateInitializer());
		} else {
			INIT_THREAD = new Thread(new IntegrateInitializer());
		}

		if (!Config.JAS_NO_THREADS) {
			INIT_THREAD.start();
		} else {
			// see #evaluate() method
		}

		F.ISet(F.$s("§simplifyflag"), F.False);

		F.ISet(F.$s("§$timelimit"), F.ZZ(Config.INTEGRATE_RUBI_TIMELIMIT));
		F.ISet(F.$s("§$showsteps"), F.False);
		UtilityFunctionCtors.ReapList.setAttributes(ISymbol.HOLDFIRST);
		F.ISet(F.$s("§$trigfunctions"), F.List(F.Sin, F.Cos, F.Tan, F.Cot, F.Sec, F.Csc));
		F.ISet(F.$s("§$hyperbolicfunctions"), F.List(F.Sinh, F.Cosh, F.Tanh, F.Coth, F.Sech, F.Csch));
		F.ISet(F.$s("§$inversetrigfunctions"), F.List(F.ArcSin, F.ArcCos, F.ArcTan, F.ArcCot, F.ArcSec, F.ArcCsc));
		F.ISet(F.$s("§$inversehyperbolicfunctions"),
				F.List(F.ArcSinh, F.ArcCosh, F.ArcTanh, F.ArcCoth, F.ArcSech, F.ArcCsch));
		F.ISet(F.$s("§$calculusfunctions"), F.List(F.D, F.Sum, F.Product, F.Integrate, F.$rubi("Unintegrable"),
				F.$rubi("CannotIntegrate"), F.$rubi("Dif"), F.$rubi("Subst")));
		F.ISet(F.$s("§$stopfunctions"), F.List(F.Hold, F.HoldForm, F.Defer, F.Pattern, F.If, F.Integrate,
				UtilityFunctionCtors.Unintegrable, F.$rubi("CannotIntegrate")));
		F.ISet(F.$s("§$heldfunctions"), F.List(F.Hold, F.HoldForm, F.Defer, F.Pattern));

		F.ISet(UtilityFunctionCtors.IntegerPowerQ, //
				F.Function(F.And(F.SameQ(F.Head(F.Slot1), F.Power), F.IntegerQ(F.Part(F.Slot1, F.C2)))));

		F.ISet(UtilityFunctionCtors.FractionalPowerQ, //
				F.Function(
						F.And(F.SameQ(F.Head(F.Slot1), F.Power), F.SameQ(F.Head(F.Part(F.Slot1, F.C2)), F.Rational))));
	}

}