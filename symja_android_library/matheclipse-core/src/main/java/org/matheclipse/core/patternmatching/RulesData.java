package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluationEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.collect.TreeMultimap;

/**
 * The pattern matching rules associated with a symbol.
 */
public class RulesData implements Serializable {

	private static final long serialVersionUID = -7747268035549814899L;

	private Map<IExpr, PatternMatcherEquals> fEqualDownRules;
	private TreeMultimap<Integer, IPatternMatcher> fSimplePatternDownRules;
	private TreeMultimap<Integer, IPatternMatcher> fSimpleOrderlesPatternDownRules;
	private TreeSet<IPatternMatcher> fPatternDownRules;
	private Map<IExpr, PatternMatcherEquals> fEqualUpRules;
	private TreeMultimap<Integer, IPatternMatcher> fSimplePatternUpRules;
	final private Context context;

	public RulesData(Context context) {
		this.context = context;
		clear();
	}

	private PatternMatcher addSimpleOrderlessPatternDownRule(final Set<ISymbol> headerSymbols, final IExpr leftHandSide,
			final PatternMatcher pmEvaluator) {
		for (ISymbol head : headerSymbols) {
			final Integer hash = Integer.valueOf(head.hashCode());
			if (F.isSystemInitialized && fSimpleOrderlesPatternDownRules.containsEntry(hash, pmEvaluator)) {
				fSimpleOrderlesPatternDownRules.remove(hash, pmEvaluator);
			}
			fSimpleOrderlesPatternDownRules.put(hash, pmEvaluator);
		}
		return pmEvaluator;
	}

	/**
	 * Create a pattern hash value for the left-hand-side expression and insert the left-hand-side as a simple pattern rule to the
	 * <code>fSimplePatternRules</code>.
	 * 
	 * @param leftHandSide
	 * @param pmEvaluator
	 * @return
	 */
	private PatternMatcher addSimplePatternDownRule(final IExpr leftHandSide, final PatternMatcher pmEvaluator) {
		final Integer hash = Integer.valueOf(((IAST) leftHandSide).patternHashCode());
		if (F.isSystemInitialized && fSimplePatternDownRules.containsEntry(hash, pmEvaluator)) {
			fSimplePatternDownRules.remove(hash, pmEvaluator);
		}
		fSimplePatternDownRules.put(hash, pmEvaluator);
		return pmEvaluator;
	}

	/**
	 * Create a pattern hash value for the left-hand-side expression and insert the left-hand-side as a simple pattern rule to the
	 * <code>fSimplePatternRules</code>.
	 * 
	 * @param leftHandSide
	 * @param pmEvaluator
	 * @return
	 */
	private PatternMatcher addSimplePatternUpRule(final IExpr leftHandSide, final PatternMatcher pmEvaluator) {
		final Integer hash = Integer.valueOf(((IAST) leftHandSide).patternHashCode());
		if (F.isSystemInitialized && fSimplePatternUpRules.containsEntry(hash, pmEvaluator)) {
			fSimplePatternUpRules.remove(hash, pmEvaluator);
		}
		fSimplePatternUpRules.put(hash, pmEvaluator);
		return pmEvaluator;
	}

	public void clear() {
		fEqualDownRules = null;
		fSimplePatternDownRules = null;
		fSimpleOrderlesPatternDownRules = null;
		fPatternDownRules = null;
		fEqualUpRules = null;
		fSimplePatternUpRules = null;
	}

	public boolean containsOrderlessASTOrDefaultPattern(final IAST lhsAST) {
		for (int i = 1; i < lhsAST.size(); i++) {
			if (lhsAST.get(i).isPatternDefault()) {
				return true;
			}
			if (lhsAST.get(i).isOrderlessAST()) {
				return true;
			}
		}
		return false;
	}

	public List<IAST> definition() {
		ArrayList<IAST> definitionList = new ArrayList<IAST>();
		Iterator<IExpr> iter;
		IExpr key;
		PatternMatcherEquals pmEquals;
		IAST ast;
		ISymbol setSymbol;
		IExpr condition;
		PatternMatcherAndEvaluator pmEvaluator;
		if (fEqualUpRules != null && fEqualUpRules.size() > 0) {
			iter = fEqualUpRules.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				pmEquals = fEqualUpRules.get(key);
				setSymbol = pmEquals.getSetSymbol();
				ast = F.ast(setSymbol);
				ast.add(key);
				ast.add(pmEquals.getRHS());
				definitionList.add(ast);
			}
		}
		if (fSimplePatternUpRules != null && fSimplePatternUpRules.size() > 0) {
			Iterator<IPatternMatcher> listIter = fSimplePatternUpRules.values().iterator();
			IPatternMatcher elem;
			while (listIter.hasNext()) {
				elem = listIter.next();
				if (elem instanceof PatternMatcherAndEvaluator) {
					pmEvaluator = (PatternMatcherAndEvaluator) elem;
					setSymbol = pmEvaluator.getSetSymbol();

					ast = F.ast(setSymbol);
					ast.add(pmEvaluator.getLHS());
					condition = pmEvaluator.getCondition();
					if (condition != null) {
						ast.add(F.Condition(pmEvaluator.getRHS(), condition));
					} else {
						ast.add(pmEvaluator.getRHS());
					}
					definitionList.add(ast);
				}
				// if (elem instanceof PatternMatcherAndInvoker) {
				// don't show internal methods associated with a pattern
				// }
			}
		}

		if (fEqualDownRules != null && fEqualDownRules.size() > 0) {
			iter = fEqualDownRules.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				pmEquals = fEqualDownRules.get(key);
				ast = pmEquals.getAsAST();
				definitionList.add(ast);
			}
		}
		if (fSimplePatternDownRules != null && fSimplePatternDownRules.size() > 0) {
			Iterator<IPatternMatcher> listIter = fSimplePatternDownRules.values().iterator();
			IPatternMatcher elem;
			while (listIter.hasNext()) {
				elem = listIter.next();
				if (elem instanceof PatternMatcherAndEvaluator) {
					pmEvaluator = (PatternMatcherAndEvaluator) elem;
					ast = pmEvaluator.getAsAST();
					definitionList.add(ast);
				}
				// if (elem instanceof PatternMatcherAndInvoker) {
				// don't show internal methods associated with a pattern
				// }
			}
		}
		if (fSimpleOrderlesPatternDownRules != null && fSimpleOrderlesPatternDownRules.size() > 0) {
			Iterator<IPatternMatcher> listIter = fSimpleOrderlesPatternDownRules.values().iterator();
			IPatternMatcher elem;
			Set<PatternMatcherAndEvaluator> set = new HashSet<PatternMatcherAndEvaluator>();
			while (listIter.hasNext()) {
				elem = listIter.next();
				if (elem instanceof PatternMatcherAndEvaluator) {
					pmEvaluator = (PatternMatcherAndEvaluator) elem;
					if (!set.contains(pmEvaluator)) {
						set.add(pmEvaluator);
						ast = pmEvaluator.getAsAST();
						definitionList.add(ast);
					}
				}
			}
		}
		if (fPatternDownRules != null && fPatternDownRules.size() > 0) {
			IPatternMatcher[] list = fPatternDownRules.toArray(new IPatternMatcher[0]);
			for (int i = 0; i < list.length; i++) {
				if (list[i] instanceof PatternMatcherAndEvaluator) {
					pmEvaluator = (PatternMatcherAndEvaluator) list[i];
					ast = pmEvaluator.getAsAST();
					definitionList.add(ast);
				}
			}

		}

		return definitionList;
	}

	public IExpr evalDownRule(final IEvaluationEngine ee, final IExpr expression) {
		PatternMatcherEquals res;
		boolean showSteps = false;
		if (Config.SHOW_PATTERN_EVAL_STEPS) {
			showSteps = Config.SHOW_PATTERN_SYMBOL_STEPS.contains(expression.topHead());
			if (showSteps) {
				System.out.println("EVAL_EXPR: " + expression.toString());
			}
		}
		if (fEqualDownRules != null) {
			// if (showSteps) {
			// System.out.println("  EQUAL RULES");
			// }
			res = fEqualDownRules.get(expression);
			if (res != null) {
				if (showSteps) {
					System.out.println("\n  >>>> " + res.getRHS().toString());
				}
				return res.getRHS();
			}
		}

		try {
			IExpr result;
			IPatternMatcher pmEvaluator;
			if (expression.isAST()) {
				IAST astExpr = (IAST) expression;
				if (fSimplePatternDownRules != null) {
					final Integer hash = Integer.valueOf(((IAST) expression).patternHashCode());
					if (fSimplePatternDownRules.containsKey(hash)) {
						IExpr temp = evalSimpleRatternDownRule(fSimplePatternDownRules, hash, astExpr, showSteps);
						if (temp != null) {
							return temp;
						}
					}
				}

				if (fSimpleOrderlesPatternDownRules != null) {
					Integer hash;
					for (int i = 1; i < astExpr.size(); i++) {
						if (astExpr.get(i).isAST() && astExpr.get(i).head().isSymbol()) {
							hash = Integer.valueOf(astExpr.get(i).head().hashCode());
							if (fSimpleOrderlesPatternDownRules.containsKey(hash)) {
								IExpr temp = evalSimpleRatternDownRule(fSimpleOrderlesPatternDownRules, hash, astExpr, showSteps);
								if (temp != null) {
									return temp;
								}
							}
						}
					}
				}
			}

			if (fPatternDownRules != null) {
				for (IPatternMatcher patternEvaluator : fPatternDownRules) {
					pmEvaluator = (IPatternMatcher) patternEvaluator.clone();
					if (showSteps) {
						IExpr rhs = pmEvaluator.getRHS();
						if (rhs == null) {
							rhs = F.Null;
						}
						System.out.println("  COMPLEX: " + pmEvaluator.getLHS().toString() + "  :=  " + rhs.toString());
					}
					result = pmEvaluator.eval(expression);
					if (result != null) {
						if (showSteps) {
							IExpr rhs = pmEvaluator.getRHS();
							if (rhs == null) {
								rhs = F.Null;
							}
							// System.out.println("\nCOMPLEX: " + pmEvaluator.getLHS().toString() + "  :=  " + rhs.toString());
							System.out.println(" >>> " + expression.toString() + "  >>>>  " + result.toString());
						}
						return result;
					}
				}
			}
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
		}
		return null;
	}

	public IExpr evalDownRule(final IExpr expression) {
		return evalDownRule(EvalEngine.get(), expression);
	}

	public IExpr evalSimpleRatternDownRule(TreeMultimap<Integer, IPatternMatcher> multiMap, final Integer hash,
			final IAST expression, boolean showSteps) throws CloneNotSupportedException {
		IExpr result;
		IPatternMatcher pmEvaluator;

		// TODO Performance hotspot
		Set<IPatternMatcher> nset = multiMap.get(hash);
		for (IPatternMatcher patternEvaluator : nset) {
			pmEvaluator = (IPatternMatcher) patternEvaluator.clone();
			if (showSteps) {
				IExpr rhs = pmEvaluator.getRHS();
				if (rhs == null) {
					rhs = F.Null;
				}
				System.out.println("  SIMPLE:  " + pmEvaluator.getLHS().toString() + " <<>> " + expression);
				// + "  :=  " + rhs.toString());
			}
			result = pmEvaluator.eval(expression);
			if (result != null) {
				if (showSteps) {
					IExpr rhs = pmEvaluator.getRHS();
					if (rhs == null) {
						rhs = F.Null;
					}
					// System.out.println("\nSIMPLE:  " + pmEvaluator.getLHS().toString() + "  :=  " +
					// rhs.toString());
					System.out.println(" >>> " + expression.toString() + "  >>>>  " + result.toString());
				}
				return result;
			}
		}
		return null;
	}

	public IExpr evalUpRule(final IEvaluationEngine ee, final IExpr expression) {
		PatternMatcherEquals res;
		if (fEqualUpRules != null) {
			res = fEqualUpRules.get(expression);
			if (res != null) {
				return res.getRHS();
			}
		}

		try {
			IExpr result;
			IPatternMatcher pmEvaluator;
			if ((fSimplePatternUpRules != null) && (expression.isAST())) {
				final Integer hash = Integer.valueOf(((IAST) expression).patternHashCode());
				if (fSimplePatternUpRules.containsKey(hash)) {
					final IPatternMatcher[] list = fSimplePatternUpRules.get(hash).toArray(new IPatternMatcher[0]);
					if (list != null) {
						for (int i = 0; i < list.length; i++) {
							pmEvaluator = (IPatternMatcher) list[i].clone();
							result = pmEvaluator.eval(expression);
							if (result != null) {
								return result;
							}
						}
					}
				}
			}

		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
		}
		return null;
	}

	/**
	 * @return Returns the equalRules.
	 */
	public Map<IExpr, PatternMatcherEquals> getEqualDownRules() {
		if (fEqualDownRules == null) {
			fEqualDownRules = new HashMap<IExpr, PatternMatcherEquals>();
		}
		return fEqualDownRules;
	}

	/**
	 * @return Returns the equalRules.
	 */
	public Map<IExpr, PatternMatcherEquals> getEqualUpRules() {
		if (fEqualUpRules == null) {
			fEqualUpRules = new HashMap<IExpr, PatternMatcherEquals>();
		}
		return fEqualUpRules;
	}

	private TreeSet<IPatternMatcher> getPatternDownRules() {
		if (fPatternDownRules == null) {
			fPatternDownRules = new TreeSet<IPatternMatcher>();
		}
		return fPatternDownRules;
	}

	private TreeMultimap<Integer, IPatternMatcher> getSimpleOrderlessPatternDownRules() {
		if (fSimpleOrderlesPatternDownRules == null) {
			fSimpleOrderlesPatternDownRules = TreeMultimap.create();
		}
		return fSimpleOrderlesPatternDownRules;
	}

	private TreeMultimap<Integer, IPatternMatcher> getSimplePatternDownRules() {
		if (fSimplePatternDownRules == null) {
			fSimplePatternDownRules = TreeMultimap.create();
		}
		return fSimplePatternDownRules;
	}

	private TreeMultimap<Integer, IPatternMatcher> getSimplePatternUpRules() {
		if (fSimplePatternUpRules == null) {
			fSimplePatternUpRules = TreeMultimap.create();
		}
		return fSimplePatternUpRules;
	}

	private boolean isComplicatedPatternRule(final IExpr lhsExpr, Set<ISymbol> neededSymbols) {
		if (lhsExpr.isAST()) {
			final IAST lhsAST = ((IAST) lhsExpr);
			if (lhsAST.size() > 1) {
				int attr = lhsAST.topHead().getAttributes();
				if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
					return true;
				}
				if (lhsAST.arg1().isPattern()) {
					return true;
				} else if (lhsAST.arg1().isPatternSequence()) {
					return true;
				} else if (lhsAST.arg1().isAST()) {
					IAST arg1 = (IAST) lhsAST.arg1();
					if (arg1.isAST(F.PatternTest, 3)) {
						return true;
					}
					if (arg1.isCondition()) {
						return true;
					}
					if (arg1.head().isPatternExpr()) {
						// the head contains a pattern F_(a1, a2,...)
						return true;
					}
					if (arg1.head().isSymbol()) {
						attr = ((ISymbol) arg1.head()).getAttributes();
						if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
							if (neededSymbols != null) {
								boolean isComplicated = false;
								for (int i = 1; i < arg1.size(); i++) {
									if (arg1.get(i).isPatternDefault()) {
										isComplicated = true;
										continue;
									}
									if (arg1.get(i).isAST() && !containsOrderlessASTOrDefaultPattern((IAST) arg1.get(i))) {
										neededSymbols.add(arg1.get(i).topHead());
									}
								}
								return isComplicated;
							}
						}
					}
					// the left hand side is associated with the first argument
					// see if one of the arguments contain a pattern with defaut value
					for (int i = 1; i < arg1.size(); i++) {
						if (arg1.get(i).isPatternDefault()) {
							return true;
						}
					}
					return false;
				}
				for (int i = 2; i < lhsAST.size(); i++) {
					if (lhsAST.get(i).isPatternDefault()) {
						return true;
					}
				}
			}
		} else if (lhsExpr.isPattern()) {
			return true;
		} else if (lhsExpr.isPatternSequence()) {
			return true;
		}
		return false;
	}

	public IPatternMatcher putDownRule(final ISymbol.RuleType setSymbol, final boolean equalRule, final IExpr leftHandSide,
			final IExpr rightHandSide) {
		if (equalRule) {
			fEqualDownRules = getEqualDownRules();
			// fEqualRules.put(leftHandSide, new Pair<ISymbol, IExpr>(setSymbol,
			// rightHandSide));
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualDownRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		final PatternMatcherAndEvaluator pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, leftHandSide, rightHandSide);

		if (pmEvaluator.isRuleWithoutPatterns()) {
			fEqualDownRules = getEqualDownRules();
			// fEqualRules.put(leftHandSide, new Pair<ISymbol, IExpr>(setSymbol,
			// rightHandSide));
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualDownRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		// pmEvaluator.setCondition(condition);
		Set<ISymbol> headerSymbols = new HashSet<ISymbol>();
		if (!isComplicatedPatternRule(leftHandSide, headerSymbols)) {
			fSimplePatternDownRules = getSimplePatternDownRules();
			return addSimplePatternDownRule(leftHandSide, pmEvaluator);

		} else {
			if (headerSymbols.size() > 0) {
				fSimpleOrderlesPatternDownRules = getSimpleOrderlessPatternDownRules();
				return addSimpleOrderlessPatternDownRule(headerSymbols, leftHandSide, pmEvaluator);
			}

			fPatternDownRules = getPatternDownRules();
			if (F.isSystemInitialized) {
				fPatternDownRules.remove(pmEvaluator);
				// for (int i = 0; i < fPatternDownRules.size(); i++) {
				// if (pmEvaluator.equals(fPatternDownRules.get(i))) {
				// fPatternDownRules.set(i, pmEvaluator);
				//
				// return pmEvaluator;
				// }
				// }
			}
			fPatternDownRules.add(pmEvaluator);
			return pmEvaluator;

		}

	}

	/** {@inheritDoc} */
	public PatternMatcher putDownRule(final PatternMatcherAndInvoker pmEvaluator) {
		final IExpr leftHandSide = pmEvaluator.getLHS();
		Set<ISymbol> headerSymbols = new HashSet<ISymbol>();
		if (!isComplicatedPatternRule(leftHandSide, headerSymbols)) {
			fSimplePatternDownRules = getSimplePatternDownRules();
			return addSimplePatternDownRule(leftHandSide, pmEvaluator);
		} else {
			if (headerSymbols.size() > 0) {
				fSimpleOrderlesPatternDownRules = getSimpleOrderlessPatternDownRules();
				return addSimpleOrderlessPatternDownRule(headerSymbols, leftHandSide, pmEvaluator);
			}
			fPatternDownRules = getPatternDownRules();
			fPatternDownRules.remove(pmEvaluator);
			fPatternDownRules.add(pmEvaluator);
			return pmEvaluator;
		}
	}

	public IPatternMatcher putUpRule(final ISymbol.RuleType setSymbol, final boolean equalRule, final IAST leftHandSide,
			final IExpr rightHandSide) {
		if (equalRule) {
			fEqualUpRules = getEqualUpRules();
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualUpRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		final PatternMatcherAndEvaluator pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, leftHandSide, rightHandSide);

		if (pmEvaluator.isRuleWithoutPatterns()) {
			fEqualUpRules = getEqualUpRules();
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualUpRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		fSimplePatternUpRules = getSimplePatternUpRules();
		return addSimplePatternUpRule(leftHandSide, pmEvaluator);

	}

	public void removeRule(final ISymbol.RuleType setSymbol, final boolean equalRule, final IExpr leftHandSide) {
		if (equalRule) {
			if (fEqualDownRules != null) {
				fEqualDownRules.remove(leftHandSide);
				return;
			}
		}

		final PatternMatcherAndEvaluator pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, leftHandSide, null);
		if (pmEvaluator.isRuleWithoutPatterns()) {
			if (fEqualDownRules != null) {
				fEqualDownRules.remove(leftHandSide);
				return;
			}
		}

		Set<ISymbol> headerSymbols = new HashSet<ISymbol>();
		if (!isComplicatedPatternRule(leftHandSide, headerSymbols)) {
			if (fSimplePatternDownRules != null) {
				final Integer hash = Integer.valueOf(((IAST) leftHandSide).patternHashCode());
				if (fSimplePatternDownRules.containsEntry(hash, pmEvaluator)) {
					fSimplePatternDownRules.remove(hash, pmEvaluator);
				}
			}
			return;
		} else {
			if (headerSymbols.size() > 0) {
				if (fSimpleOrderlesPatternDownRules != null) {
					for (ISymbol head : headerSymbols) {
						final Integer hash = Integer.valueOf(head.hashCode());
						if (fSimpleOrderlesPatternDownRules.containsEntry(hash, pmEvaluator)) {
							fSimpleOrderlesPatternDownRules.remove(hash, pmEvaluator);
						}
					}
				}
				return;
			}

			if (fPatternDownRules != null) {
				fPatternDownRules.remove(pmEvaluator);
				return;
			}
		}
	}

	@Override
	public String toString() {
		StringWriter buf = new StringWriter();
		List<IAST> list = definition();
		for (int i = 0; i < list.size(); i++) {
			buf.append(list.get(i).toString());
			if (i < list.size() - 1) {
				buf.append(",\n ");
			}
		}
		return buf.toString();
	}

}