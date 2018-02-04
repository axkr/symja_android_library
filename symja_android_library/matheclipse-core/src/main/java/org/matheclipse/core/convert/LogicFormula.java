package org.matheclipse.core.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import org.logicng.formulas.And;
import org.logicng.formulas.CFalse;
import org.logicng.formulas.CTrue;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Not;
import org.logicng.formulas.Or;
import org.logicng.formulas.Variable;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Comparators.ExprComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class LogicFormula {

	/**
	 * Create a map which assigns the position of each variable name in the given <code>vars</code> array to the
	 * corresponding variable name.
	 * 
	 * @param vars
	 *            an array of variables
	 * @return
	 */
	public static Map<String, Integer> name2Position(Variable[] vars) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < vars.length; i++) {
			map.put(vars[i].name(), i);
		}
		return map;
	}

	final FormulaFactory factory;

	Map<ISymbol, Variable> symbol2variableMap = new HashMap<ISymbol, Variable>();
	Map<Variable, ISymbol> variable2symbolMap = new HashMap<Variable, ISymbol>();

	public LogicFormula() {
		this(new FormulaFactory());
	}

	public LogicFormula(FormulaFactory factory) {
		this.factory = factory;
	}

	public Variable[] ast2Variable(final IAST listOfSymbols) throws ClassCastException {
		if (listOfSymbols instanceof IAST) {
			Variable[] result = new Variable[listOfSymbols.argSize()];
			for (int i = 1; i < listOfSymbols.size(); i++) {
				IExpr temp = listOfSymbols.get(i);
				if (temp instanceof ISymbol) {
					ISymbol symbol = (ISymbol) temp;
					if (symbol.isFalse()) {
						throw new ClassCastException(F.False.toString());
					}
					if (symbol.isTrue()) {
						throw new ClassCastException(F.True.toString());
					}
					Variable v = symbol2variableMap.get(symbol);
					if (v == null) {
						final Variable value = factory.variable(symbol.getSymbolName());
						symbol2variableMap.put(symbol, value);
						variable2symbolMap.put(value, symbol);
						result[i - 1] = value;
					} else {
						result[i - 1] = v;
					}
				} else {
					throw new ClassCastException(temp.toString());
				}
			}
			return result;
		}
		throw new ClassCastException(listOfSymbols.toString());
	}

	public IExpr booleanFunction2Expr(final Formula formula) throws ClassCastException {
		if (formula instanceof And) {
			And a = (And) formula;
			IExpr[] result = new IExpr[a.numberOfOperands()];
			int i = 0;
			for (Formula f : a) {
				result[i++] = booleanFunction2Expr(f);
			}
			Arrays.sort(result, ExprComparator.CONS);
			return F.And(result);
		} else if (formula instanceof Or) {
			Or a = (Or) formula;
			IExpr[] result = new IExpr[a.numberOfOperands()];
			int i = 0;
			for (Formula f : a) {
				result[i++] = booleanFunction2Expr(f);
			}
			Arrays.sort(result, ExprComparator.CONS);
			return F.Or(result);
		} else if (formula instanceof Not) {
			Not a = (Not) formula;
			return F.Not(booleanFunction2Expr(a.operand()));
		} else if (formula instanceof CFalse) {
			return F.False;
		} else if (formula instanceof CTrue) {
			return F.True;
		} else if (formula instanceof Literal) {
			Literal a = (Literal) formula;
			if (a.phase()) {
				return variable2symbolMap.get(a.variable());
			}
			return F.Not(variable2symbolMap.get(a.variable()));
		} else if (formula instanceof Variable) {
			Variable a = (Variable) formula;
			return variable2symbolMap.get(a);
		}
		throw new ClassCastException(formula.toString());
	}

	private Formula convertEquivalent(IAST ast) {
		Formula[] result1 = new Formula[ast.argSize()];
		Formula[] result2 = new Formula[ast.argSize()];
		for (int i = 1; i < ast.size(); i++) {
			result1[i - 1] = factory.not(expr2BooleanFunction(ast.get(i)));
			result2[i - 1] = factory.not(result1[i - 1]);
		}
		return factory.or(factory.and(result1), factory.and(result2));
	}

	private Formula convertXor(IAST ast) {
		Formula arg1 = expr2BooleanFunction(ast.arg1());
		Formula arg2 = expr2BooleanFunction(ast.arg2());
		if (ast.size() > 3) {
			IASTAppendable clone = ast.copyAppendable();
			clone.remove(1);
			arg2 = convertXor(clone);
		}
		return factory.or(factory.and(arg1, factory.not(arg2)), factory.and(factory.not(arg1), arg2));
	}

	public Formula expr2BooleanFunction(final IExpr logicExpr) throws ClassCastException {
		if (logicExpr instanceof IAST) {
			final IAST ast = (IAST) logicExpr;
			if (ast.isAnd()) {
				Formula[] result = new Formula[ast.argSize()];
				for (int i = 1; i < ast.size(); i++) {
					result[i - 1] = expr2BooleanFunction(ast.get(i));
				}
				return factory.and(result);
			} else if (ast.isOr()) {
				Formula[] result = new Formula[ast.argSize()];
				for (int i = 1; i < ast.size(); i++) {
					result[i - 1] = expr2BooleanFunction(ast.get(i));
				}
				return factory.or(result);
			} else if (ast.isASTSizeGE(F.Nand, 3)) {
				Formula[] result = new Formula[ast.argSize()];
				for (int i = 1; i < ast.size(); i++) {
					result[i - 1] = factory.not(expr2BooleanFunction(ast.get(i)));
				}
				return factory.or(result);
			} else if (ast.isASTSizeGE(F.Nor, 3)) {
				Formula[] result = new Formula[ast.argSize()];
				for (int i = 1; i < ast.size(); i++) {
					result[i - 1] = factory.not(expr2BooleanFunction(ast.get(i)));
				}
				return factory.and(result);
			} else if (ast.isASTSizeGE(F.Equivalent, 3)) {
				return convertEquivalent(ast);
			} else if (ast.isASTSizeGE(F.Xor, 3)) {
				return convertXor(ast);
			} else if (ast.isAST(F.Implies, 3)) {
				return factory.implication(expr2BooleanFunction(ast.arg1()), expr2BooleanFunction(ast.arg2()));
			} else if (ast.isNot()) {
				IExpr expr = ast.arg1();
				return factory.not(expr2BooleanFunction(expr));
			}
		} else if (logicExpr instanceof ISymbol) {
			ISymbol symbol = (ISymbol) logicExpr;
			if (symbol.isFalse()) {
				return factory.falsum();
			}
			if (symbol.isTrue()) {
				return factory.verum();
			}
			Variable v = symbol2variableMap.get(symbol);
			if (v == null) {
				final Variable value = factory.variable(symbol.getSymbolName());
				symbol2variableMap.put(symbol, value);
				variable2symbolMap.put(value, symbol);
				return value;
			}
			return v;
		}
		throw new ClassCastException(logicExpr.toString());
	}

	public FormulaFactory getFactory() {
		return factory;
	}

	public Collection<Variable> list2LiteralCollection(final IAST list) throws ClassCastException {
		Collection<Variable> arr = new ArrayList<Variable>(list.argSize());
		for (int i = 1; i < list.size(); i++) {
			IExpr temp = list.get(i);
			if (!temp.isSymbol()) {
				throw new ClassCastException(temp.toString());
			}

			ISymbol symbol = (ISymbol) temp;

			Variable v = symbol2variableMap.get(symbol);
			if (v == null) {
				final Variable value = factory.variable(symbol.getSymbolName());
				symbol2variableMap.put(symbol, value);
				variable2symbolMap.put(value, symbol);
			}
			arr.add(v);

		}
		return arr;
	}

	/**
	 * Convert the literals into a <code>List()</code> of <code>False, True</code> values according to the assigned
	 * position for the variable name in the given <code>map</code>.
	 * 
	 * @param literals
	 *            a set of literals which could be converted to False and True values
	 * @param map
	 *            a map which maps a variable name to the position in the resulting list
	 * @return
	 */
	public IAST literals2BooleanList(final SortedSet<Literal> literals, Map<String, Integer> map) {
		IASTAppendable list = F.ast(F.List, map.size(), true);

		// initialize all list elements with Null
		for (int i = 0; i < map.size(); i++) {
			list.set(i + 1, F.Null);
		}

		for (Literal a : literals) {
			Integer val = map.get(a.name());
			if (val != null) {
				if (a.phase()) {
					list.set(val + 1, F.True);
				} else {
					list.set(val + 1, F.False);
				}
			}
		}
		return list;
	}

	/**
	 * Example: Create a list of rules in the form
	 * <code>{{a->False,b->True,c->False,d->False},{a->True,b->False,c->False,d->False},...}</code> for the variables
	 * <code>{a,b,c,d}</code>
	 * 
	 * @param booleanExpression
	 * @param variables
	 *            the possible variables
	 * @param maxChoices
	 * @return
	 */
	public IAST literals2VariableList(final SortedSet<Literal> literals, Map<String, Integer> map) {
		IASTAppendable list = F.ast(F.List, map.size(), true);

		// initialize all list elements with Null
		for (int i = 0; i < map.size(); i++) {
			list.set(i + 1, F.Null);
		}

		for (Literal a : literals) {
			Integer val = map.get(a.name());
			if (val != null) {
				if (a.phase()) {
					list.set(val + 1, F.Rule(variable2symbolMap.get(a.variable()), F.True));
				} else {
					list.set(val + 1, F.Rule(variable2symbolMap.get(a.variable()),F.False));
				}
			}
		}
		return list;
	}
}
