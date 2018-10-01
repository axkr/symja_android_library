package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.ArraySet;
import org.matheclipse.core.eval.util.OpenIntToIExprHashMap;
import org.matheclipse.core.eval.util.OpenIntToSet;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.AbstractVisitor;

/**
 * The pattern matching rules associated with a symbol.
 */
public class RulesData implements Serializable {
	private static final long serialVersionUID = -7747268035549814899L;

	static boolean showSteps = false;

	/**
	 * 
	 */
	public static final int DEFAULT_VALUE_INDEX = Integer.MIN_VALUE;

	public static boolean isComplicatedPatternRule(final IExpr lhsExpr, ArraySet<ISymbol> neededSymbols) {
		if (lhsExpr.isAST()) {
			final IAST lhsAST = ((IAST) lhsExpr);
			if (lhsAST.size() > 1) {
				int attr = lhsAST.topHead().getAttributes();
				if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
					return true;
				}
				if (lhsAST.arg1().isPattern()) {
					return true;
				} else if (lhsAST.arg1().isPatternSequence(false)) {
					return true;
				} else if (lhsAST.arg1().isAST()) {
					IAST arg1 = (IAST) lhsAST.arg1();
					if (arg1.isCondition() || arg1.isPatternTest() || arg1.isAlternatives() || arg1.isExcept()) {
						return true;
					}
					IExpr head = arg1.head();
					if (head.isPatternExpr()) {
						// the head contains a pattern F_(a1, a2,...)
						return true;
					}
					if (neededSymbols != null && arg1.isOrderlessAST()) {
						boolean lambda = !lhsAST.exists(x -> x.isPatternDefault() || x.isOrderlessAST());
						boolean[] isComplicated = { false };
						arg1.forEach(t -> {
							if (t.isPatternDefault()) {
								isComplicated[0] = true;
							} else if (lambda && t.isAST() && t.head().isSymbol()) {
								neededSymbols.add((ISymbol) t.head());
							}
						});
						return isComplicated[0];
					}
					// the left hand side is associated with the first argument
					// see if one of the arguments contain a pattern with default
					// value
					return arg1.exists(x -> x.isPatternDefault(), 1);
				}
				return lhsAST.exists(x -> x.isPatternDefault(), 2);
			}
		} else if (lhsExpr.isPattern()) {
			return true;
		} else if (lhsExpr.isPatternSequence(false)) {
			return true;
		}
		return false;
	}

	private OpenIntToIExprHashMap<IExpr> fDefaultValues;

	private Map<IExpr, PatternMatcherEquals> fEqualDownRules;

	private OpenIntToSet<IPatternMatcher> fSimplePatternDownRules;

	private OpenIntToSet<IPatternMatcher> fSimpleOrderlesPatternDownRules;

	private Set<IPatternMatcher> fPatternDownRules;
	private Map<IExpr, PatternMatcherEquals> fEqualUpRules;
	private OpenIntToSet<IPatternMatcher> fSimplePatternUpRules;

	// final transient private Context context;

	public RulesData(Context context) {
		// this.context = context;
		clear();
	}

	public RulesData(Context context, @Nonnull int[] sizes) {
		// this.context = context;
		clear();
		if (sizes.length > 0) {
			int capacity;
			if (sizes[0] > 0) {
				capacity = sizes[0];
				if (capacity < 8) {
					capacity = 8;
				}
				fEqualDownRules = new HashMap<IExpr, PatternMatcherEquals>(capacity);
			}
			if (sizes.length > 1) {
				if (sizes[1] >= 16) {
					capacity = sizes[1];
					fSimplePatternDownRules = new OpenIntToSet<IPatternMatcher>(IPatternMatcher.EQUIVALENCE_COMPARATOR,
							capacity);
				}
			}
		}
	}

	/**
	 * <p>
	 * Run the given visitor on every IAST stored in the rule database.
	 * </p>
	 * Example: optimize internal memory usage by sharing common objects.
	 * 
	 * @param visitor
	 *            the visitor whch manipulates the IAST objects
	 * @return
	 */
	public IAST accept(AbstractVisitor visitor) {
		Iterator<IExpr> iter;
		IExpr key;
		PatternMatcherEquals pmEquals;
		IAST ast;
		IExpr condition;
		PatternMatcherAndEvaluator pmEvaluator;
		if (fEqualUpRules != null && fEqualUpRules.size() > 0) {
			iter = fEqualUpRules.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				pmEquals = fEqualUpRules.get(key);
				if (key.isAST()) {
					key.accept(visitor);
				}
				if (pmEquals.getRHS().isAST()) {
					pmEquals.getRHS().accept(visitor);
				}
			}
		}
		if (fSimplePatternUpRules != null && fSimplePatternUpRules.size() > 0) {
			Iterator<IPatternMatcher> listIter;
			Set<IPatternMatcher>[] setArr = fSimplePatternUpRules.getValues();
			for (int i = 0; i < setArr.length; i++) {
				if (setArr[i] != null) {
					listIter = setArr[i].iterator();
					IPatternMatcher elem;
					while (listIter.hasNext()) {
						elem = listIter.next();
						if (elem instanceof PatternMatcherAndEvaluator) {
							pmEvaluator = (PatternMatcherAndEvaluator) elem;
							if (pmEvaluator.getLHS().isAST()) {
								pmEvaluator.getLHS().accept(visitor);
							}
							if (pmEvaluator.getRHS().isAST()) {
								pmEvaluator.getRHS().accept(visitor);
							}
							condition = pmEvaluator.getCondition();
							if (condition != null) {
								if (condition.isAST()) {
									condition.accept(visitor);
								}
							}
						}
						// if (elem instanceof PatternMatcherAndInvoker) {
						// don't show internal methods associated with a pattern
						// }
					}
				}
			}
		}

		if (fEqualDownRules != null && fEqualDownRules.size() > 0) {
			iter = fEqualDownRules.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				pmEquals = fEqualDownRules.get(key);
				ast = pmEquals.getAsAST();
				if (key.isAST()) {
					key.accept(visitor);
				}
				ast.accept(visitor);
			}
		}
		if (fSimplePatternDownRules != null && fSimplePatternDownRules.size() > 0) {
			Iterator<IPatternMatcher> listIter;
			Set<IPatternMatcher>[] setArr = fSimplePatternDownRules.getValues();
			final int length = setArr.length;
			for (int i = 0; i < length; i++) {
				if (setArr[i] != null) {
					listIter = setArr[i].iterator();
					IPatternMatcher elem;
					while (listIter.hasNext()) {
						elem = listIter.next();
						if (elem instanceof PatternMatcherAndEvaluator) {
							pmEvaluator = (PatternMatcherAndEvaluator) elem;
							ast = pmEvaluator.getAsAST();
							ast.accept(visitor);
						}
					}
				}
			}
		}

		if (fSimpleOrderlesPatternDownRules != null && fSimpleOrderlesPatternDownRules.size() > 0) {
			Iterator<IPatternMatcher> listIter;
			Set<IPatternMatcher>[] setArr = fSimpleOrderlesPatternDownRules.getValues();
			final int length = setArr.length;
			for (int i = 0; i < length; i++) {
				if (setArr[i] != null) {
					listIter = setArr[i].iterator();
					IPatternMatcher elem;
					Set<PatternMatcherAndEvaluator> set = new HashSet<PatternMatcherAndEvaluator>();
					while (listIter.hasNext()) {
						elem = listIter.next();
						if (elem instanceof PatternMatcherAndEvaluator) {
							pmEvaluator = (PatternMatcherAndEvaluator) elem;
							if (!set.contains(pmEvaluator)) {
								set.add(pmEvaluator);
								ast = pmEvaluator.getAsAST();
								ast.accept(visitor);
							}
						}
					}
				}
			}
		}
		if (fPatternDownRules != null && fPatternDownRules.size() > 0) {
			IPatternMatcher[] list = fPatternDownRules.toArray(new IPatternMatcher[0]);
			final int length = list.length;
			for (int i = 0; i < length; i++) {
				if (list[i] instanceof PatternMatcherAndEvaluator) {
					pmEvaluator = (PatternMatcherAndEvaluator) list[i];
					ast = pmEvaluator.getAsAST();
					ast.accept(visitor);
				}
			}

		}

		return null;
	}

	private PatternMatcher addSimpleOrderlessPatternDownRule(final ArraySet<ISymbol> headerSymbols,
			final IExpr leftHandSide, final PatternMatcher pmEvaluator) {
		for (ISymbol head : headerSymbols) {
			final int hash = head.hashCode();
			if (F.isSystemInitialized && fSimpleOrderlesPatternDownRules.containsEntry(hash, pmEvaluator)) {
				fSimpleOrderlesPatternDownRules.remove(hash, pmEvaluator);
			}
			fSimpleOrderlesPatternDownRules.put(hash, pmEvaluator);
		}
		return pmEvaluator;
	}

	/**
	 * Create a pattern hash value for the left-hand-side expression and insert the left-hand-side as a simple pattern
	 * rule to the <code>fSimplePatternRules</code>.
	 * 
	 * @param leftHandSide
	 * @param pmEvaluator
	 * @return
	 */
	private PatternMatcher addSimplePatternDownRule(final IExpr leftHandSide, final PatternMatcher pmEvaluator) {
		final int hash = ((IAST) leftHandSide).patternHashCode();
		if (F.isSystemInitialized && fSimplePatternDownRules.containsEntry(hash, pmEvaluator)) {
			fSimplePatternDownRules.remove(hash, pmEvaluator);
		}
		fSimplePatternDownRules.put(hash, pmEvaluator);
		return pmEvaluator;
	}

	/**
	 * Create a pattern hash value for the left-hand-side expression and insert the left-hand-side as a simple pattern
	 * rule to the <code>fSimplePatternRules</code>.
	 * 
	 * @param leftHandSide
	 * @param pmEvaluator
	 * @return
	 */
	private PatternMatcher addSimplePatternUpRule(final IExpr leftHandSide, final PatternMatcher pmEvaluator) {
		final int hash = ((IAST) leftHandSide).patternHashCode();
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
				definitionList.add(F.binaryAST2(setSymbol, key, pmEquals.getRHS()));
			}
		}
		if (fSimplePatternUpRules != null && fSimplePatternUpRules.size() > 0) {
			Iterator<IPatternMatcher> listIter;
			Set<IPatternMatcher>[] setArr = fSimplePatternUpRules.getValues();
			for (int i = 0; i < setArr.length; i++) {
				if (setArr[i] != null) {
					listIter = setArr[i].iterator();
					IPatternMatcher elem;
					while (listIter.hasNext()) {
						elem = listIter.next();
						if (elem instanceof PatternMatcherAndEvaluator) {
							pmEvaluator = (PatternMatcherAndEvaluator) elem;
							setSymbol = pmEvaluator.getSetSymbol();

							condition = pmEvaluator.getCondition();
							if (condition != null) {
								definitionList.add(F.binaryAST2(setSymbol, pmEvaluator.getLHS(),
										F.Condition(pmEvaluator.getRHS(), condition)));
							} else {
								definitionList.add(F.binaryAST2(setSymbol, pmEvaluator.getLHS(), pmEvaluator.getRHS()));
							}

						}
					}
				}
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
			Iterator<IPatternMatcher> listIter;
			Set<IPatternMatcher>[] setArr = fSimplePatternDownRules.getValues();
			for (int i = 0; i < setArr.length; i++) {
				if (setArr[i] != null) {
					listIter = setArr[i].iterator();
					IPatternMatcher elem;
					while (listIter.hasNext()) {
						elem = listIter.next();
						if (elem instanceof PatternMatcherAndEvaluator) {
							pmEvaluator = (PatternMatcherAndEvaluator) elem;
							ast = pmEvaluator.getAsAST();
							definitionList.add(ast);
						}
					}
				}
			}

		}
		if (fSimpleOrderlesPatternDownRules != null && fSimpleOrderlesPatternDownRules.size() > 0) {
			Iterator<IPatternMatcher> listIter;
			Set<IPatternMatcher>[] setArr = fSimpleOrderlesPatternDownRules.getValues();
			final int length = setArr.length;
			for (int i = 0; i < length; i++) {
				if (setArr[i] != null) {
					listIter = setArr[i].iterator();
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
			}
		}
		if (fPatternDownRules != null && fPatternDownRules.size() > 0) {
			IPatternMatcher[] list = fPatternDownRules.toArray(new IPatternMatcher[0]);
			final int length = list.length;
			for (int i = 0; i < length; i++) {
				if (list[i] instanceof PatternMatcherAndEvaluator) {
					pmEvaluator = (PatternMatcherAndEvaluator) list[i];
					ast = pmEvaluator.getAsAST();
					definitionList.add(ast);
				}
			}

		}

		return definitionList;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		RulesData other = (RulesData) obj;

		if (fEqualDownRules == null) {
			if (other.fEqualDownRules != null)
				return false;
		} else if (!fEqualDownRules.equals(other.fEqualDownRules))
			return false;
		if (fEqualUpRules == null) {
			if (other.fEqualUpRules != null)
				return false;
		} else if (!fEqualUpRules.equals(other.fEqualUpRules))
			return false;

		if (fPatternDownRules == null) {
			if (other.fPatternDownRules != null)
				return false;
		} else if (!fPatternDownRules.equals(other.fPatternDownRules))
			return false;

		if (fSimpleOrderlesPatternDownRules == null) {
			if (other.fSimpleOrderlesPatternDownRules != null)
				return false;
		} else if (!fSimpleOrderlesPatternDownRules.equals(other.fSimpleOrderlesPatternDownRules))
			return false;

		if (fSimplePatternDownRules == null) {
			if (other.fSimplePatternDownRules != null)
				return false;
		} else if (!fSimplePatternDownRules.equals(other.fSimplePatternDownRules))
			return false;

		if (fSimplePatternUpRules == null) {
			if (other.fSimplePatternUpRules != null)
				return false;
		} else if (!fSimplePatternUpRules.equals(other.fSimplePatternUpRules))
			return false;

		return true;
	}

	/**
	 * 
	 * @param expr
	 * @return <code>F.NIL</code> if no evaluation was possible
	 */
	public IExpr evalDownRule(final IExpr expr, @Nonnull EvalEngine engine) {
		PatternMatcherEquals res;

		if (Config.SHOW_PATTERN_EVAL_STEPS) {
			showSteps = Config.SHOW_PATTERN_SYMBOL_STEPS.contains(expr.topHead());
			if (showSteps) {
				System.out.println("EVAL_EXPR: " + expr.toString());
			}
		}
		if (fEqualDownRules != null) {
			// if (showSteps) {
			// System.out.println(" EQUAL RULES");
			// }
			res = fEqualDownRules.get(expr);
			if (res != null) {
				if (showSteps) {
					System.out.println("\n  >>>> " + res.getRHS().toString());
				}
				return res.getRHS();
			}
		}

		try {
			IPatternMatcher pmEvaluator;
			if (expr.isAST()) {
				IAST astExpr = (IAST) expr;
				if (fSimplePatternDownRules != null) {
					final int hash = ((IAST) expr).patternHashCode();
					if (fSimplePatternDownRules.containsKey(hash)) {
						IExpr temp = evalSimpleRatternDownRule(fSimplePatternDownRules, hash, astExpr, showSteps,
								engine);
						if (temp.isPresent()) {
							return temp;
						}
					}
				}

				if (fSimpleOrderlesPatternDownRules != null) {
					IExpr[] temp = new IExpr[1];
					if (astExpr.exists(x -> {
						if (x.isAST() && x.head().isSymbol()) {
							final int hash = x.head().hashCode();
							if (fSimpleOrderlesPatternDownRules.containsKey(hash)) {
								try {
									IExpr result = evalSimpleRatternDownRule(fSimpleOrderlesPatternDownRules, hash,
											astExpr, showSteps, engine);
									if (result.isPresent()) {
										temp[0] = result;
										return true;
									}
								} catch (CloneNotSupportedException cnse) {
									cnse.printStackTrace();
								}
							}
						}
						return false;
					})) {
						return temp[0];
					}
				}
			}

			if (fPatternDownRules != null) {
				IExpr result;
				for (IPatternMatcher patternEvaluator : fPatternDownRules) {
					pmEvaluator = (IPatternMatcher) patternEvaluator.clone();
					if (showSteps) {
						if (isShowSteps(pmEvaluator)) {
							IExpr rhs = pmEvaluator.getRHS();
							if (!rhs.isPresent()) {
								rhs = F.Null;
							}
							System.out
									.println(" COMPLEX: " + pmEvaluator.getLHS().toString() + " := " + rhs.toString());
						}
					}
					if (pmEvaluator.getLHSPriority() == 1706 && pmEvaluator.getLHS().isAST(F.Integrate)) {
						// don't use 1706 Rule from Rubi
						continue;
					}
					// if (pmEvaluator.getLHSPriority() == 6686) {
					// System.out.println("Debug from this line");
					// }
					if (Config.SHOW_STACKTRACE) {
						if (isShowPriority(pmEvaluator)) {
							System.out.print("try: " + pmEvaluator.getLHSPriority() + " - ");
						}
						// if (pmEvaluator.getLHSPriority() == 432) {
						// System.out.println(pmEvaluator.toString());
						// System.out.println(expr);
						// System.out.println("Debug from this line");
						// }
					}
					// System.out.println(pmEvaluator.toString());
					// System.out.println(">>"+expr);

					result = pmEvaluator.eval(expr, engine);
					if (result.isPresent()) {
						if (Config.SHOW_STACKTRACE) {
							if (isShowPriority(pmEvaluator)) {
								System.out.println(
										"matched: " + pmEvaluator.getLHSPriority() + ": " + pmEvaluator.toString());
							}
						}
						if (showSteps) {
							if (isShowSteps(pmEvaluator)) {
								IExpr rhs = pmEvaluator.getRHS();
								if (!rhs.isPresent()) {
									rhs = F.Null;
								}
								System.out.println(
										"\nCOMPLEX: " + pmEvaluator.getLHS().toString() + " := " + rhs.toString());
								System.out.println(" >>> " + expr.toString() + "  >>>>  " + result.toString());
							}
						}
						return result;
					} else {
						if (Config.SHOW_STACKTRACE) {
							if (isShowPriority(pmEvaluator)) {
								System.out.print("not matched: " + pmEvaluator.getLHSPriority() + " ");
							}
						}
					}
				}
			}
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
		}
		return F.NIL;
	}

	private boolean isShowSteps(IPatternMatcher pmEvaluator) {
		IExpr head = pmEvaluator.getLHS().head();
		if (head.toString().toLowerCase().contains("integrate::")) {
			return true;
		}
		return head.equals(F.Integrate);
	}

	private boolean isShowPriority(IPatternMatcher pmEvaluator) {
		IExpr head = pmEvaluator.getLHS().head();
		// if (head.toString().toLowerCase().contains("integrate::")) {
		// return true;
		// }
		return head.equals(F.Integrate);
	}

	public IExpr evalSimpleRatternDownRule(OpenIntToSet<IPatternMatcher> hashToMatcherMap, final int hash,
			final IAST expression, boolean showSteps, @Nonnull EvalEngine engine) throws CloneNotSupportedException {
		IPatternMatcher pmEvaluator;

		// TODO Performance hotspot
		Collection<IPatternMatcher> nset = hashToMatcherMap.get(hash);
		if (nset != null) {
			IExpr result;
			for (IPatternMatcher patternEvaluator : nset) {
				pmEvaluator = (IPatternMatcher) patternEvaluator.clone();
				if (showSteps) {
					// IExpr rhs = pmEvaluator.getRHS();
					// if (!rhs.isPresent()) {
					// rhs = F.Null;
					// }
					// System.out.println(" SIMPLE: " + pmEvaluator.getLHS().toString() + " <<>> " +
					// expression);
					// // + " := " + rhs.toString());
				}
				result = pmEvaluator.eval(expression, engine);
				if (result.isPresent()) {
					if (showSteps) {
						IExpr rhs = pmEvaluator.getRHS();
						if (!rhs.isPresent()) {
							rhs = F.Null;
						}
						System.out.println("\nSIMPLE: " + pmEvaluator.getLHS().toString() + " := " + rhs.toString());
						System.out.println(" >>> " + expression.toString() + "  >>>>  " + result.toString());
					}
					return result;
				}
			}
		}
		return F.NIL;
	}

	public IExpr evalUpRule(final IExpr expression, @Nonnull EvalEngine engine) {
		PatternMatcherEquals res;
		if (fEqualUpRules != null) {
			res = fEqualUpRules.get(expression);
			if (res != null) {
				return res.getRHS();
			}
		}

		try {
			IPatternMatcher pmEvaluator;
			if ((fSimplePatternUpRules != null) && (expression.isAST())) {
				final int hash = ((IAST) expression).patternHashCode();
				if (fSimplePatternUpRules.containsKey(hash)) {
					Set<IPatternMatcher> set = fSimplePatternUpRules.get(hash);
					if (set != null) {
						final IPatternMatcher[] list = set.toArray(new IPatternMatcher[0]);
						if (list != null) {
							IExpr result;
							for (int i = 0; i < list.length; i++) {
								pmEvaluator = (IPatternMatcher) list[i].clone();
								result = pmEvaluator.eval(expression, engine);
								if (result.isPresent()) {
									return result;
								}
							}
						}
					}
				}
			}

		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
		}
		return F.NIL;
	}

	final public IExpr getDefaultValue(int pos) {
		if (fDefaultValues == null) {
			return null;
		}
		return fDefaultValues.get(pos);
	}

	/**
	 * @return Returns the equalRules.
	 */
	final public Map<IExpr, PatternMatcherEquals> getEqualDownRules() {
		if (fEqualDownRules == null) {
			fEqualDownRules = new HashMap<IExpr, PatternMatcherEquals>();
		}
		return fEqualDownRules;
	}

	/**
	 * @return Returns the equalRules.
	 */
	final public Map<IExpr, PatternMatcherEquals> getEqualUpRules() {
		if (fEqualUpRules == null) {
			fEqualUpRules = new HashMap<IExpr, PatternMatcherEquals>();
		}
		return fEqualUpRules;
	}

	final public Set<IPatternMatcher> getPatternDownRules() {
		if (fPatternDownRules == null) {
			// fPatternDownRules = new TreeSet<IPatternMatcher>();
			fPatternDownRules = new TreeSet<IPatternMatcher>(IPatternMatcher.EQUIVALENCE_COMPARATOR);
		}
		return fPatternDownRules;
	}

	private OpenIntToSet<IPatternMatcher> getSimpleOrderlessPatternDownRules() {
		if (fSimpleOrderlesPatternDownRules == null) {
			fSimpleOrderlesPatternDownRules = new OpenIntToSet<IPatternMatcher>(IPatternMatcher.EQUIVALENCE_COMPARATOR);
		}
		return fSimpleOrderlesPatternDownRules;
	}

	public OpenIntToSet<IPatternMatcher> getSimplePatternDownRules() {
		if (fSimplePatternDownRules == null) {
			fSimplePatternDownRules = new OpenIntToSet<IPatternMatcher>(IPatternMatcher.EQUIVALENCE_COMPARATOR);
		}
		return fSimplePatternDownRules;
	}

	private OpenIntToSet<IPatternMatcher> getSimplePatternUpRules() {
		if (fSimplePatternUpRules == null) {
			fSimplePatternUpRules = new OpenIntToSet<IPatternMatcher>(IPatternMatcher.EQUIVALENCE_COMPARATOR);
		}
		return fSimplePatternUpRules;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fEqualDownRules == null) ? 0 : fEqualDownRules.hashCode());
		result = prime * result + ((fEqualUpRules == null) ? 0 : fEqualUpRules.hashCode());
		result = prime * result + ((fPatternDownRules == null) ? 0 : fPatternDownRules.hashCode());
		result = prime * result
				+ ((fSimpleOrderlesPatternDownRules == null) ? 0 : fSimpleOrderlesPatternDownRules.hashCode());
		result = prime * result + ((fSimplePatternDownRules == null) ? 0 : fSimplePatternDownRules.hashCode());
		result = prime * result + ((fSimplePatternUpRules == null) ? 0 : fSimplePatternUpRules.hashCode());
		return result;
	}

	public IPatternMatcher putDownRule(final IExpr leftHandSide, final AbstractPatternMatcherMethod pmEvaluator) {
		ArraySet<ISymbol> headerSymbols = new ArraySet<ISymbol>();
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
			}
			fPatternDownRules.add(pmEvaluator);
			return pmEvaluator;

		}

	}

	public final IPatternMatcher putDownRule(final IExpr leftHandSide, final IExpr rightHandSide) {
		return putDownRule(ISymbol.RuleType.SET_DELAYED, false, leftHandSide, rightHandSide,
				PatternMap.DEFAULT_RULE_PRIORITY);
	}

	public IPatternMatcher putDownRule(final ISymbol.RuleType setSymbol, final boolean equalRule,
			final IExpr leftHandSide, final IExpr rightHandSide) {
		return putDownRule(ISymbol.RuleType.SET_DELAYED, false, leftHandSide, rightHandSide,
				PatternMap.DEFAULT_RULE_PRIORITY);
	}

	public IPatternMatcher putDownRule(final ISymbol.RuleType setSymbol, final boolean equalRule,
			final IExpr leftHandSide, final IExpr rightHandSide, final int priority) {
		if (equalRule) {
			fEqualDownRules = getEqualDownRules();
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualDownRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		final PatternMatcherAndEvaluator pmEvaluator;
		if (leftHandSide.isAST(F.Integrate)) {
			pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, leftHandSide, rightHandSide, false);
			// keep Integrate rules in order predefined by Rubi project
			pmEvaluator.setLHSPriority(priority);

			fPatternDownRules = getPatternDownRules();
			fPatternDownRules.add(pmEvaluator);
			return pmEvaluator;
		} else {
			pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, leftHandSide, rightHandSide);
			if (pmEvaluator.isRuleWithoutPatterns()) {
				fEqualDownRules = getEqualDownRules();
				PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
				fEqualDownRules.put(leftHandSide, pmEquals);
				return pmEquals;
			}
		}

		if (PatternMap.DEFAULT_RULE_PRIORITY != priority) {
			pmEvaluator.setLHSPriority(priority);
		}

		ArraySet<ISymbol> headerSymbols = new ArraySet<ISymbol>();
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
			}
			fPatternDownRules.add(pmEvaluator);
			return pmEvaluator;

		}

	}

	public PatternMatcher putDownRule(final PatternMatcherAndInvoker pmEvaluator) {
		final IExpr leftHandSide = pmEvaluator.getLHS();
		ArraySet<ISymbol> headerSymbols = new ArraySet<ISymbol>();
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

	public void putfDefaultValues(IExpr expr) {
		putfDefaultValues(DEFAULT_VALUE_INDEX, expr);
	}

	public void putfDefaultValues(int pos, IExpr expr) {
		if (this.fDefaultValues == null) {
			this.fDefaultValues = new OpenIntToIExprHashMap<IExpr>();
		}
		fDefaultValues.put(pos, expr);
	}

	public IPatternMatcher putUpRule(final ISymbol.RuleType setSymbol, final boolean equalRule, final IAST leftHandSide,
			final IExpr rightHandSide) {
		if (equalRule) {
			fEqualUpRules = getEqualUpRules();
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualUpRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		final PatternMatcherAndEvaluator pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, leftHandSide,
				rightHandSide);

		if (pmEvaluator.isRuleWithoutPatterns()) {
			fEqualUpRules = getEqualUpRules();
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualUpRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		fSimplePatternUpRules = getSimplePatternUpRules();
		return addSimplePatternUpRule(leftHandSide, pmEvaluator);

	}

	public boolean removeRule(final ISymbol.RuleType setSymbol, final boolean equalRule, final IExpr leftHandSide) {
		if (equalRule) {
			if (fEqualDownRules != null) {
				return fEqualDownRules.remove(leftHandSide) != null;
			}
		}

		final PatternMatcherAndEvaluator pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, leftHandSide, null);
		if (pmEvaluator.isRuleWithoutPatterns()) {
			if (fEqualDownRules != null) {
				return fEqualDownRules.remove(leftHandSide) != null;
			}
		}

		ArraySet<ISymbol> headerSymbols = new ArraySet<ISymbol>();
		boolean removed = false;
		if (!isComplicatedPatternRule(leftHandSide, headerSymbols)) {
			if (fSimplePatternDownRules != null) {
				final int hash = ((IAST) leftHandSide).patternHashCode();
				if (fSimplePatternDownRules.containsEntry(hash, pmEvaluator)) {
					if (fSimplePatternDownRules.remove(hash, pmEvaluator)) {
						removed = true;
					}
				}
			}
			return removed;
		} else {
			if (headerSymbols.size() > 0) {
				if (fSimpleOrderlesPatternDownRules != null) {
					for (ISymbol head : headerSymbols) {
						final int hash = head.hashCode();
						if (fSimpleOrderlesPatternDownRules.containsEntry(hash, pmEvaluator)) {
							if (fSimpleOrderlesPatternDownRules.remove(hash, pmEvaluator)) {
								removed = true;
							}
						}
					}
				}
				return removed;
			}

			if (fPatternDownRules != null) {
				return fPatternDownRules.removeIf(x -> x.equivalentLHS(pmEvaluator) == 0);
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringWriter buf = new StringWriter();
		List<IAST> list = definition();
		final int size = list.size();
		for (int i = 0; i < size; i++) {
			buf.append(list.get(i).toString());
			if (i < size - 1) {
				buf.append(",\n ");
			}
		}
		return buf.toString();
	}
}