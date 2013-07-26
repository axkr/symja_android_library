package org.matheclipse.core.patternmatching;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluationEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.collect.ArrayListMultimap;

/**
 * The pattern matching rules associated with a symbol.
 */
public class RulesData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8843909916823779295L;

	// private transient Map<IExpr, Pair<ISymbol, IExpr>> fEqualRules;
	private transient Map<IExpr, PatternMatcherEquals> fEqualRules;
	private transient ArrayListMultimap<Integer, IPatternMatcher> fSimplePatternRules;
	private transient List<IPatternMatcher> fPatternRules;

	public RulesData() {
		this.fEqualRules = null;
		this.fSimplePatternRules = null;
		this.fPatternRules = null;
	}

	public void clear() {
		fEqualRules = null;
		fSimplePatternRules = null;
		fPatternRules = null;
	}

	public IExpr evalDownRule(final IExpr expression) {
		return evalDownRule(EvalEngine.get(), expression);
	}

	public IExpr evalDownRule(final IEvaluationEngine ee, final IExpr expression) {
		// Pair<ISymbol, IExpr> res;
		// if (fEqualRules != null) {
		// res = fEqualRules.get(expression);
		// if (res != null) {
		// return res.getSecond();
		// }
		// }
		PatternMatcherEquals res;
		if (fEqualRules != null) {
			res = fEqualRules.get(expression);
			if (res != null) {
				return res.getRHS();
			}
		}

		try {
			IExpr result;
			IPatternMatcher pmEvaluator;
			if ((fSimplePatternRules != null) && (expression instanceof IAST)) {
				final Integer hash = Integer.valueOf(((IAST) expression).patternHashCode());
				final List<IPatternMatcher> list = fSimplePatternRules.get(hash);
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						pmEvaluator = (IPatternMatcher) list.get(i).clone();
						result = pmEvaluator.eval(expression);
						if (result != null) {
							return result;
						}
					}
				}
			}

			if (fPatternRules != null) {
				for (int i = 0; i < fPatternRules.size(); i++) {
					pmEvaluator = (IPatternMatcher) fPatternRules.get(i).clone();
					result = pmEvaluator.eval(expression);
					if (result != null) {
						return result;
					}
				}
			}
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
		}
		return null;
	}

	public IPatternMatcher putDownRule(ISymbol setSymbol, final boolean equalRule, final IExpr leftHandSide,
			final IExpr rightHandSide, final int priority) {
		if (equalRule) {
			fEqualRules = getEqualRules();
			// fEqualRules.put(leftHandSide, new Pair<ISymbol, IExpr>(setSymbol,
			// rightHandSide));
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		final PatternMatcherAndEvaluator pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, leftHandSide, rightHandSide);

		if (pmEvaluator.isRuleWithoutPatterns()) {
			fEqualRules = getEqualRules();
			// fEqualRules.put(leftHandSide, new Pair<ISymbol, IExpr>(setSymbol,
			// rightHandSide));
			PatternMatcherEquals pmEquals = new PatternMatcherEquals(setSymbol, leftHandSide, rightHandSide);
			fEqualRules.put(leftHandSide, pmEquals);
			return pmEquals;
		}

		// pmEvaluator.setCondition(condition);
		if (!isComplicatedPatternRule(leftHandSide)) {

			fSimplePatternRules = getSimplePatternRules();
			return addSimplePatternRule(leftHandSide, pmEvaluator);

		} else {

			fPatternRules = getPatternRules();
			if (F.isSystemInitialized) {
				for (int i = 0; i < fPatternRules.size(); i++) {
					if (pmEvaluator.equals(fPatternRules.get(i))) {
						fPatternRules.set(i, pmEvaluator);

						return pmEvaluator;
					}
				}
			}
			fPatternRules.add(pmEvaluator);
			return pmEvaluator;

		}

	}

	/**
	 * Create a pattern hash value for the left-hand-side expression and insert
	 * the left-hand-side as a simple pattern rule to the
	 * <code>fSimplePatternRules</code>.
	 * 
	 * @param leftHandSide
	 * @param pmEvaluator
	 * @return
	 */
	private PatternMatcher addSimplePatternRule(final IExpr leftHandSide, final PatternMatcher pmEvaluator) {
		final Integer hash = Integer.valueOf(((IAST) leftHandSide).patternHashCode());
		if (F.isSystemInitialized && fSimplePatternRules.containsEntry(hash, pmEvaluator)) {
			fSimplePatternRules.remove(hash, pmEvaluator);
		}
		fSimplePatternRules.put(hash, pmEvaluator);
		return pmEvaluator;
	}

	/** {@inheritDoc} */
	public PatternMatcher putDownRule(final PatternMatcherAndInvoker pmEvaluator) {
		final IExpr leftHandSide = pmEvaluator.getLHS();
		if (!isComplicatedPatternRule(leftHandSide)) {

			fSimplePatternRules = getSimplePatternRules();
			return addSimplePatternRule(leftHandSide, pmEvaluator);

		} else {

			fPatternRules = getPatternRules();
			for (int i = 0; i < fPatternRules.size(); i++) {
				if (pmEvaluator.equals(fPatternRules.get(i))) {
					fPatternRules.set(i, pmEvaluator);
					return pmEvaluator;
				}
			}
			fPatternRules.add(pmEvaluator);
			return pmEvaluator;
		}
	}

	private boolean isComplicatedPatternRule(final IExpr lhsExpr) {
		if (lhsExpr.isAST()) {
			final IAST lhsAST = ((IAST) lhsExpr);
			if (lhsAST.size() > 1) {
				final int attr = lhsAST.topHead().getAttributes();
				if ((ISymbol.ORDERLESS & attr) == ISymbol.ORDERLESS) {
					return true;
				}
				if (lhsAST.get(1).isAST()) {
					IAST arg1 = (IAST) lhsAST.get(1);
					if (arg1.isCondition()) {
						return true;
					}
					// the left hand side is associated with the first argument
					// see if the first argument is complicated
					for (int i = 2; i < arg1.size(); i++) {
						if (arg1.get(i).isPattern() && ((IPattern) arg1.get(i)).isDefault()) {
							return true;
						}
					}
				} else if (lhsAST.get(1).isPattern()) {
					return true;
				} else if (lhsAST.get(1).isPatternSequence()) {
					return true;
				}
				for (int i = 2; i < lhsAST.size(); i++) {
					if (lhsAST.get(i).isPattern() && ((IPattern) lhsAST.get(i)).isDefault()) {
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

	/**
	 * @return Returns the equalRules.
	 */
	// public Map<IExpr, Pair<ISymbol, IExpr>> getEqualRules() {
	// if (fEqualRules == null) {
	// fEqualRules = new HashMap<IExpr, Pair<ISymbol, IExpr>>();
	// }
	// return fEqualRules;
	// }
	public Map<IExpr, PatternMatcherEquals> getEqualRules() {
		if (fEqualRules == null) {
			fEqualRules = new HashMap<IExpr, PatternMatcherEquals>();
		}
		return fEqualRules;
	}

	private List<IPatternMatcher> getPatternRules() {
		if (fPatternRules == null) {
			fPatternRules = new ArrayList<IPatternMatcher>();
		}
		return fPatternRules;
	}

	private ArrayListMultimap<Integer, IPatternMatcher> getSimplePatternRules() {
		if (fSimplePatternRules == null) {
			fSimplePatternRules = ArrayListMultimap.create();
		}
		return fSimplePatternRules;
	}

	public List<IAST> definition() {
		ArrayList<IAST> definitionList = new ArrayList<IAST>();
		Iterator<IExpr> iter;
		IExpr key;
		// Pair<ISymbol, IExpr> pair;
		// IExpr condition;
		// ISymbol setSymbol;
		// IAST ast;
		// PatternMatcherAndEvaluator pmEvaluator;
		// if (fEqualRules != null && fEqualRules.size() > 0) {
		// iter = fEqualRules.keySet().iterator();
		// while (iter.hasNext()) {
		// key = iter.next();
		// pair = fEqualRules.get(key);
		// setSymbol = pair.getFirst();
		// ast = F.ast(setSymbol);
		// ast.add(key);
		// ast.add(pair.getSecond());
		// definitionList.add(ast);
		// }
		// }
		PatternMatcherEquals pme;
		IExpr condition;
		ISymbol setSymbol;
		IAST ast;
		PatternMatcherAndEvaluator pmEvaluator;
		if (fEqualRules != null && fEqualRules.size() > 0) {
			iter = fEqualRules.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				pme = fEqualRules.get(key);
				setSymbol = pme.getSetSymbol();
				ast = F.ast(setSymbol);
				ast.add(key);
				ast.add(pme.getRHS());
				definitionList.add(ast);
			}
		}
		if (fSimplePatternRules != null && fSimplePatternRules.size() > 0) {
			Iterator<IPatternMatcher> listIter = fSimplePatternRules.values().iterator();
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
		if (fPatternRules != null && fPatternRules.size() > 0) {
			for (int i = 0; i < fPatternRules.size(); i++) {
				if (fPatternRules.get(i) instanceof PatternMatcherAndEvaluator) {
					pmEvaluator = (PatternMatcherAndEvaluator) fPatternRules.get(i);
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
			}

		}

		return definitionList;
	}

	public void readSymbol(java.io.ObjectInputStream stream) throws IOException {

		String astString;
		IExpr key;
		IExpr value;
		EvalEngine engine = EvalEngine.get();
		ISymbol setSymbol;
		int len = stream.read();
		if (len > 0) {
			// fEqualRules = new HashMap<IExpr, Pair<ISymbol, IExpr>>();
			// for (int i = 0; i < len; i++) {
			// astString = stream.readUTF();
			// setSymbol = F.$s(astString);
			//
			// astString = stream.readUTF();
			// key = engine.parse(astString);
			// astString = stream.readUTF();
			// value = engine.parse(astString);
			// fEqualRules.put(key, new Pair<ISymbol, IExpr>(setSymbol, value));
			// }
			fEqualRules = new HashMap<IExpr, PatternMatcherEquals>();
			for (int i = 0; i < len; i++) {
				astString = stream.readUTF();
				setSymbol = F.$s(astString);

				astString = stream.readUTF();
				key = engine.parse(astString);
				astString = stream.readUTF();
				value = engine.parse(astString);
				fEqualRules.put(key, new PatternMatcherEquals(setSymbol, key, value));
			}
		}

		len = stream.read();
		IExpr lhs;
		IExpr rhs;
		IExpr condition;
		int listLength;
		int condLength;
		PatternMatcherAndEvaluator pmEvaluator;
		if (len > 0) {
			fSimplePatternRules = ArrayListMultimap.create();
			for (int i = 0; i < len; i++) {
				astString = stream.readUTF();
				setSymbol = F.$s(astString);

				astString = stream.readUTF();
				lhs = engine.parse(astString);
				astString = stream.readUTF();
				rhs = engine.parse(astString);
				pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, lhs, rhs);

				condLength = stream.read();
				if (condLength == 0) {
					condition = null;
				} else {
					astString = stream.readUTF();
					condition = engine.parse(astString);
					pmEvaluator.setCondition(condition);
				}
				addSimplePatternRule(lhs, pmEvaluator);
			}

		}

		len = stream.read();
		if (len > 0) {
			fPatternRules = new ArrayList<IPatternMatcher>();
			listLength = stream.read();
			for (int j = 0; j < listLength; j++) {
				astString = stream.readUTF();
				setSymbol = F.$s(astString);

				astString = stream.readUTF();
				lhs = engine.parse(astString);
				astString = stream.readUTF();
				rhs = engine.parse(astString);
				pmEvaluator = new PatternMatcherAndEvaluator(setSymbol, lhs, rhs);

				condLength = stream.read();
				if (condLength == 0) {
					condition = null;
				} else {
					astString = stream.readUTF();
					condition = engine.parse(astString);
					pmEvaluator.setCondition(condition);
				}
				addSimplePatternRule(lhs, pmEvaluator);
			}
		}
	}

	public void writeSymbol(java.io.ObjectOutputStream stream) throws java.io.IOException {
		Iterator<IExpr> iter;
		IExpr key;
		IExpr condition;
		// Pair<ISymbol, IExpr> pair;
		// ISymbol setSymbol;
		// PatternMatcherAndEvaluator pmEvaluator;
		// if (fEqualRules == null || fEqualRules.size() == 0) {
		// stream.write(0);
		// } else {
		// stream.write(fEqualRules.size());
		// iter = fEqualRules.keySet().iterator();
		// while (iter.hasNext()) {
		// key = iter.next();
		// pair = fEqualRules.get(key);
		// stream.writeUTF(pair.getFirst().toString());
		// stream.writeUTF(key.fullFormString());
		// stream.writeUTF(pair.getSecond().fullFormString());
		// }
		// }
		PatternMatcherEquals pme;
		ISymbol setSymbol;
		PatternMatcherAndEvaluator pmEvaluator;
		if (fEqualRules == null || fEqualRules.size() == 0) {
			stream.write(0);
		} else {
			stream.write(fEqualRules.size());
			iter = fEqualRules.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				pme = fEqualRules.get(key);
				stream.writeUTF(pme.getLHS().toString());
				stream.writeUTF(key.fullFormString());
				stream.writeUTF(pme.getRHS().fullFormString());
			}
		}

		if (fSimplePatternRules == null || fSimplePatternRules.size() == 0) {
			stream.write(0);
		} else {
			stream.write(fSimplePatternRules.size());
			Iterator<IPatternMatcher> listIter = fSimplePatternRules.values().iterator();
			IPatternMatcher elem;
			while (listIter.hasNext()) {
				elem = listIter.next();
				pmEvaluator = (PatternMatcherAndEvaluator) elem;
				setSymbol = pmEvaluator.getSetSymbol();
				stream.writeUTF(setSymbol.toString());
				stream.writeUTF(pmEvaluator.getLHS().fullFormString());
				stream.writeUTF(pmEvaluator.getRHS().fullFormString());
				condition = pmEvaluator.getCondition();
				if (condition == null) {
					stream.write(0);
				} else {
					stream.write(1);
					stream.writeUTF(condition.fullFormString());
				}
			}
		}
		if (fPatternRules == null || fPatternRules.size() == 0) {
			stream.write(0);
		} else {
			stream.write(fPatternRules.size());

			for (int i = 0; i < fPatternRules.size(); i++) {
				pmEvaluator = (PatternMatcherAndEvaluator) fPatternRules.get(i);
				setSymbol = pmEvaluator.getSetSymbol();
				stream.writeUTF(setSymbol.toString());
				stream.writeUTF(pmEvaluator.getLHS().fullFormString());
				stream.writeUTF(pmEvaluator.getRHS().fullFormString());
				condition = pmEvaluator.getCondition();
				if (condition == null) {
					stream.write(0);
				} else {
					stream.write(1);
					stream.writeUTF(condition.fullFormString());
				}
			}

		}
	}
}