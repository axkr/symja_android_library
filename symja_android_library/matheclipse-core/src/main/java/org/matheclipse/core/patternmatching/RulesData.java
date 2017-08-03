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
	static boolean showSteps = false;
	private static final long serialVersionUID = -7747268035549814899L;
	/**
	 * 
	 */
	public static final int DEFAULT_VALUE_INDEX = Integer.MIN_VALUE;

	public static boolean containsOrderlessASTOrDefaultPattern(final IAST lhsAST) {
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

	public static boolean isComplicatedPatternRule(final IExpr lhsExpr, Set<ISymbol> neededSymbols) {
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
					if (arg1.isAlternatives() || arg1.isExcept()) {
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
									if (arg1.get(i).isAST()
											&& !containsOrderlessASTOrDefaultPattern((IAST) arg1.get(i))) {
										neededSymbols.add(arg1.get(i).topHead());
									}
								}
								return isComplicated;
							}
						}
					}
					// the left hand side is associated with the first argument
					// see if one of the arguments contain a pattern with defaut
					// value
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

	private OpenIntToIExprHashMap fDefaultValues;

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
					fSimplePatternDownRules = new OpenIntToSet<IPatternMatcher>(capacity);
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
			for (int i = 0; i < setArr.length; i++) {
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
			for (int i = 0; i < setArr.length; i++) {
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
			for (int i = 0; i < list.length; i++) {
				if (list[i] instanceof PatternMatcherAndEvaluator) {
					pmEvaluator = (PatternMatcherAndEvaluator) list[i];
					ast = pmEvaluator.getAsAST();
					ast.accept(visitor);
				}
			}

		}

		return null;
	}

	private PatternMatcher addSimpleOrderlessPatternDownRule(final Set<ISymbol> headerSymbols, final IExpr leftHandSide,
			final PatternMatcher pmEvaluator) {
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
				// ast = F.ast(setSymbol);
				// ast.add(key);
				// ast.add(pmEquals.getRHS());
				// definitionList.add(ast);
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
								// ast = F.ast(setSymbol);
								// ast.add(pmEvaluator.getLHS());
								// ast.add(F.Condition(pmEvaluator.getRHS(),
								// condition));
								// definitionList.add(ast);
								definitionList.add(F.binaryAST2(setSymbol, pmEvaluator.getLHS(),
										F.Condition(pmEvaluator.getRHS(), condition)));
							} else {
								// ast = F.ast(setSymbol);
								// ast.add(pmEvaluator.getLHS());
								// ast.add(pmEvaluator.getRHS());
								// definitionList.add(ast);
								definitionList.add(F.binaryAST2(setSymbol, pmEvaluator.getLHS(), pmEvaluator.getRHS()));
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
			for (int i = 0; i < setArr.length; i++) {
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
	public IExpr evalDownRule(final IExpr expr) {
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
						IExpr temp = evalSimpleRatternDownRule(fSimplePatternDownRules, hash, astExpr, showSteps);
						if (temp.isPresent()) {
							return temp;
						}
					}
				}

				if (fSimpleOrderlesPatternDownRules != null) {
					int hash;
					for (int i = 1; i < astExpr.size(); i++) {
						if (astExpr.get(i).isAST() && astExpr.get(i).head().isSymbol()) {
							hash = astExpr.get(i).head().hashCode();
							if (fSimpleOrderlesPatternDownRules.containsKey(hash)) {
								IExpr temp = evalSimpleRatternDownRule(fSimpleOrderlesPatternDownRules, hash, astExpr,
										showSteps);
								if (temp.isPresent()) {
									return temp;
								}
							}
						}
					}
				}
			}

			if (fPatternDownRules != null) {
				IExpr result;
				for (IPatternMatcher patternEvaluator : fPatternDownRules) {
					pmEvaluator = (IPatternMatcher) patternEvaluator.clone();
					if (showSteps) {
						if (pmEvaluator.getLHS().head().equals(F.Integrate)) {
							IExpr rhs = pmEvaluator.getRHS();
							if (!rhs.isPresent()) {
								rhs = F.Null;
							}
							System.out
									.println(" COMPLEX: " + pmEvaluator.getLHS().toString() + " := " + rhs.toString());
						}
					}
					result = pmEvaluator.eval(expr);
					if (result.isPresent()) {
						if (showSteps) {
							if (pmEvaluator.getLHS().head().equals(F.Integrate)) {
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
					}
				}
			}
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
		}
		return F.NIL;
	}

	public IExpr evalSimpleRatternDownRule(OpenIntToSet<IPatternMatcher> hashToMatcherMap, final int hash,
			final IAST expression, boolean showSteps) throws CloneNotSupportedException {
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
					// System.out.println(" SIMPLE: " + pmEvaluator.getLHS().toString() + " <<>> " + expression);
					// // + " := " + rhs.toString());
				}
				result = pmEvaluator.eval(expression);
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

	public IExpr evalUpRule(final IExpr expression) {
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
								result = pmEvaluator.eval(expression);
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

	public IExpr getDefaultValue(int pos) {
		if (fDefaultValues == null) {
			return null;
		}
		return fDefaultValues.get(pos);
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

	private Set<IPatternMatcher> getPatternDownRules() {
		if (fPatternDownRules == null) {
			// fPatternDownRules = new TreeSet<IPatternMatcher>();
			fPatternDownRules = new TreeSet<IPatternMatcher>();
		}
		return fPatternDownRules;
	}

	private OpenIntToSet<IPatternMatcher> getSimpleOrderlessPatternDownRules() {
		if (fSimpleOrderlesPatternDownRules == null) {
			fSimpleOrderlesPatternDownRules = new OpenIntToSet<IPatternMatcher>();
		}
		return fSimpleOrderlesPatternDownRules;
	}

	private OpenIntToSet<IPatternMatcher> getSimplePatternDownRules() {
		if (fSimplePatternDownRules == null) {
			fSimplePatternDownRules = new OpenIntToSet<IPatternMatcher>();
		}
		return fSimplePatternDownRules;
	}

	private OpenIntToSet<IPatternMatcher> getSimplePatternUpRules() {
		if (fSimplePatternUpRules == null) {
			fSimplePatternUpRules = new OpenIntToSet<IPatternMatcher>();
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
			}
			fPatternDownRules.add(pmEvaluator);
			return pmEvaluator;

		}

	}

	public final IPatternMatcher putDownRule(final IExpr leftHandSide, final IExpr rightHandSide) {
		return putDownRule(ISymbol.RuleType.SET_DELAYED, false, leftHandSide, rightHandSide);
	}

	public IPatternMatcher putDownRule(final ISymbol.RuleType setSymbol, final boolean equalRule,
			final IExpr leftHandSide, final IExpr rightHandSide) {
		if (equalRule) {
			fEqualDownRules = getEqualDownRules();
			// fEqualRules.put(leftHandSide, new Pair<ISymbol, IExpr>(setSymbol,
			// rightHandSide));
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualDownRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		final PatternMatcherAndEvaluator pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, leftHandSide,
				rightHandSide);

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

	public void putfDefaultValues(IExpr expr) {
		putfDefaultValues(DEFAULT_VALUE_INDEX, expr);
	}

	public void putfDefaultValues(int pos, IExpr expr) {
		if (this.fDefaultValues == null) {
			this.fDefaultValues = new OpenIntToIExprHashMap();
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
				final int hash = ((IAST) leftHandSide).patternHashCode();
				if (fSimplePatternDownRules.containsEntry(hash, pmEvaluator)) {
					fSimplePatternDownRules.remove(hash, pmEvaluator);
				}
			}
			return;
		} else {
			if (headerSymbols.size() > 0) {
				if (fSimpleOrderlesPatternDownRules != null) {
					for (ISymbol head : headerSymbols) {
						final int hash = head.hashCode();
						if (fSimpleOrderlesPatternDownRules.containsEntry(hash, pmEvaluator)) {
							fSimpleOrderlesPatternDownRules.remove(hash, pmEvaluator);
						}
					}
				}
				return;
			}

			if (fPatternDownRules != null) {
				fPatternDownRules.removeIf(x -> x.equivalentLHS(pmEvaluator) == 0);
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