package org.matheclipse.core.eval.interfaces;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcherAndInvoker;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Abstract interface for built-in Symja functions. The <code>numericEval()</code> method delegates to the <code>evaluate()</code>
 * 
 */
public abstract class AbstractFunctionEvaluator extends AbstractEvaluator {

	public IAST getRuleAST() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		IAST ruleList;
		if ((ruleList = getRuleAST()) != null) {
			EvalEngine.get().addRules(ruleList);
		}

		F.SYMBOL_OBSERVER.createPredefinedSymbol(symbol.toString());
		if (Config.SERIALIZE_SYMBOLS && symbol.containsRules()) {
			FileOutputStream out;
			try {
				out = new FileOutputStream("c:\\temp\\ser\\" + symbol.getSymbolName() + ".ser");
				ObjectOutputStream oos = new ObjectOutputStream(out);
				symbol.writeRules(oos);
				oos.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	abstract public IExpr evaluate(final IAST ast);

	/**
	 * Create a rule which invokes the method name in this class instance.
	 * 
	 * @param symbol
	 * @param patternString
	 * @param methodName
	 */
	public void createRuleFromMethod(ISymbol symbol, String patternString, String methodName) {
		PatternMatcherAndInvoker pm = new PatternMatcherAndInvoker(patternString, this, methodName);
		symbol.putDownRule(pm);
	}

	/**
	 * Check if the expression is canonical negative.
	 * 
	 * @return <code>true</code> if the first argument is canonical negative
	 */
	public static boolean isNegativeExpression(final IExpr expr) {
		if (expr.isNumber()) {
			if (((INumber) expr).complexSign() < 0) {
				return true;
			}
		} else if (expr.isTimes()) {
			IExpr arg1 = ((IAST) expr).arg1();
			if (arg1.isNumber()) {
				if (((INumber) arg1).complexSign() < 0) {
					return true;
				}
			} else if (arg1.isNegativeInfinity()) {
				return true;
			}
		} else if (expr.isPlus()) {
			IExpr arg1 = ((IAST) expr).arg1();
			if (arg1.isNumber()) {
				if (((INumber) arg1).complexSign() < 0) {
					return true;
				}
			} else if (arg1.isNegativeInfinity()) {
				return true;
			}
		} else if (expr.isNegativeInfinity()) {
			return true;
		}

		return false;
	}

	/**
	 * Check if the expression is canonical negative.
	 * 
	 * @return the negated negative expression or <code>null</code> if a negative expression couldn't be extracted.
	 */
	public static IExpr getNormalizedNegativeExpression(final IExpr expr) {
		return getNormalizedNegativeExpression(expr, true);
	}

	/**
	 * Check if the expression is canonical negative.
	 * 
	 * @param checkTimesPlus
	 *            check <code>Times(...)</code> and <code>Plus(...)</code> expressions
	 * @return the negated negative expression or <code>null</code> if a negative expression couldn't be extracted.
	 */
	public static IExpr getNormalizedNegativeExpression(final IExpr expr, boolean checkTimesPlus) {
		IAST result;
		if (expr.isNumber()) {
			if (((INumber) expr).complexSign() < 0) {
				return ((INumber) expr).negate();
			}
		}
		if (expr.isAST()) {
			if (checkTimesPlus && expr.isTimes()) {
				IAST timesAST = ((IAST) expr);
				IExpr arg1 = timesAST.arg1();
				if (arg1.isNumber()) {
					if (((INumber) arg1).complexSign() < 0) {
						IExpr negNum = ((INumber) arg1).negate();
						if (negNum.isOne()) {
							return timesAST.removeAtClone(1).getOneIdentity(F.C1);
						}
						return timesAST.setAtClone(1, negNum);
					}
				} else if (arg1.isNegativeInfinity()) {
					return timesAST.setAtClone(1, F.CInfinity);
					// } else {
					// IExpr arg1Negated = getNormalizedNegativeExpression(arg1);
					// if (arg1Negated != null) {
					// for (int i = 2; i < timesAST.size(); i++) {
					// IExpr temp = timesAST.get(i);
					// if (temp.isPlus()||temp.isTimes()) {
					// return null;
					// }
					// }
					// result = timesAST.clone();
					// result.set(1, arg1Negated);
					// return result;
					// }
				}
			} else if (checkTimesPlus && expr.isPlus()) {
				IAST plusAST = ((IAST) expr);
				IExpr arg1 = plusAST.arg1();
				if (arg1.isNumber()) {
					if (((INumber) arg1).complexSign() < 0) {
						result = plusAST.clone();
						result.set(1, arg1.negate());
						for (int i = 2; i < plusAST.size(); i++) {
							result.set(i, plusAST.get(i).negate());
						}
						return result;
					}
				} else if (arg1.isNegativeInfinity()) {
					result = plusAST.clone();
					result.set(1, F.CInfinity);
					for (int i = 2; i < plusAST.size(); i++) {
						result.set(i, plusAST.get(i).negate());
					}
					return result;
				} else if (arg1.isTimes()) {
					IExpr arg1Negated = getNormalizedNegativeExpression(arg1, checkTimesPlus);
					if (arg1Negated != null) {
						int positiveElementsCounter = 0;
						result = plusAST.clone();
						result.set(1, arg1Negated);
						for (int i = 2; i < plusAST.size(); i++) {
							IExpr temp = plusAST.get(i);
							if (!temp.isTimes() && !temp.isPower()) {
								return null;
							}
							arg1Negated = getNormalizedNegativeExpression(temp, checkTimesPlus);
							if (arg1Negated != null) {
								result.set(i, arg1Negated);
							} else {
								positiveElementsCounter++;
								if (positiveElementsCounter * 2 >= plusAST.size() - 1) {
									// number of positive elements is greater than number of negative elements
									return null;
								}
								result.set(i, temp.negate());
							}
						}
						return result;
					}
				}
			} else if (expr.isNegativeInfinity()) {
				return F.CInfinity;
			}
		}
		if (expr.isNegativeResult()) {
			return F.eval(F.Negate(expr));
		}
		return null;
	}

	/**
	 * Try to split a periodic part from the expression: <code>expr == part[0]+ part[1] * period</code>
	 * 
	 * @param expr
	 * @param period
	 * @return <code>null</code> if no periodicity was found or the rest at index 0 and the factor of the period at index 1
	 */
	public static IExpr[] getPeriodicParts(final IExpr expr, final IExpr period) {
		IExpr[] result = new IExpr[2];
		result[0] = F.C0;
		result[1] = F.C1;
		if (expr.equals(period)) {
			return result;
		}
		if (expr.isAST()) {
			IAST ast = (IAST) expr;
			if (ast.isTimes()) {
				for (int i = 1; i < ast.size(); i++) {
					if (ast.get(i).equals(period)) {
						result[1] = ast.removeAtClone(i).getOneIdentity(F.C1);
						return result;
					}
				}
				return null;
			}
			if (ast.isPlus()) {
				for (int i = 1; i < ast.size(); i++) {
					IExpr[] temp = getPeriodicParts(ast.get(i), period);
					if (temp != null && temp[0].isZero()) {
						result[0] = ast.removeAtClone(i).getOneIdentity(F.C0);
						result[1] = temp[1];
						return result;
					}
				}
				return null;
			}
		}
		return null;
	}

	/**
	 * Check if <code>expr</code> is a pure imaginary number without a real part.
	 * 
	 * @param expr
	 * @return <code>null</code>, if <code>expr</code> is not a pure imaginary number.
	 */
	public static IExpr getPureImaginaryPart(final IExpr expr) {
		if (expr.isComplex() && ((IComplex) expr).getRe().isZero()) {
			IComplex compl = (IComplex) expr;
			return compl.getIm();
		}
		if (expr.isTimes()) {
			IAST times = ((IAST) expr);
			IExpr arg1 = times.arg1();
			if (arg1.isComplex() && ((IComplex) arg1).getRe().isZero()) {
				return times.setAtClone(1, ((IComplex) arg1).getIm());
			}
		}
		return null;
	}

	/**
	 * Initialize the serialized Rubi integration rules from ressource <code>/ser/integrate.ser</code>.
	 * 
	 * @param symbol
	 */
	public static void initSerializedRules(final ISymbol symbol) {
		EvalEngine engine = EvalEngine.get();
		boolean oldPackageMode = engine.isPackageMode();
		boolean oldTraceMode = engine.isTraceMode();
		try {
			engine.setPackageMode(true);
			engine.setTraceMode(false);

			InputStream in = AbstractFunctionEvaluator.class.getResourceAsStream("/ser/"
					+ symbol.getSymbolName().toLowerCase(Locale.ENGLISH) + ".ser");
			ObjectInputStream ois = new ObjectInputStream(in);
			// InputStream in = new FileInputStream("c:\\temp\\ser\\" + symbol.getSymbolName() + ".ser");
			// read files with BufferedInputStream to improve performance
			// ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(in));
			symbol.readRules(ois);
			ois.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			engine.setPackageMode(oldPackageMode);
			engine.setTraceMode(oldTraceMode);
		}
	}

}