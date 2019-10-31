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
import org.matheclipse.core.interfaces.IExpr;
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
			if (!INTEGRATE_RULES_READ) {
				INTEGRATE_RULES_READ = true;
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
}
	}

	public static RulesData INTEGRATE_RULES_DATA;
	/**
	 * Constructor for the singleton
	 */
	public final static Integrate CONST = new Integrate();

	public final static Set<ISymbol> INT_RUBI_FUNCTIONS = new HashSet<ISymbol>();

	public final static Set<IExpr> DEBUG_EXPR = new HashSet<IExpr>(64);

	public static volatile boolean INTEGRATE_RULES_READ = false;

	public Integrate() {
	}

	@Override
	public IExpr evaluate(final IAST holdallAST, EvalEngine engine) {
		try {
			await();
		} catch (InterruptedException e) {
		}
		boolean evaled = false;
		IExpr result;
		boolean numericMode = engine.isNumericMode();
		try {
			engine.setNumericMode(false);
			if (holdallAST.size() < 3) {
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

				result = integrateByRubiRules(fx, x, ast, engine);
				if (result.isPresent()) {
					return result;
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
			IExpr FZero = engine.evaluate(F.Limit(function, F.Rule(x, F.C0)));
			if (!FZero.isFree(F.DirectedInfinity, true) || !FZero.isFree(F.Indeterminate, true)) {
				return F.Plus(F.Subtract(Fb, FZero), F.Subtract(FZero, Fa));
			}
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

		if (Config.JAS_NO_THREADS) {
			INIT_THREAD.run();
		} else {
			INIT_THREAD.start();
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
				F.$rubi("Unintegrable"), F.$rubi("CannotIntegrate")));
		F.ISet(F.$s("§$heldfunctions"), F.List(F.Hold, F.HoldForm, F.Defer, F.Pattern));

		F.ISet(UtilityFunctionCtors.IntegerPowerQ, //
				F.Function(F.And(F.SameQ(F.Head(F.Slot1), F.Power), F.IntegerQ(F.Part(F.Slot1, F.C2)))));

		F.ISet(UtilityFunctionCtors.FractionalPowerQ, //
				F.Function(
						F.And(F.SameQ(F.Head(F.Slot1), F.Power), F.SameQ(F.Head(F.Part(F.Slot1, F.C2)), F.Rational))));
	}

}