package org.matheclipse.core.builtin;

import java.util.List;
import java.util.Map;

import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.FormulaTransformation;
import org.logicng.formulas.Variable;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;
import org.logicng.transformations.cnf.CNFFactorization;
import org.logicng.transformations.dnf.DNFFactorization;
import org.logicng.transformations.qmc.QuineMcCluskeyAlgorithm;
//import org.logicng.transformations.qmc.QuineMcCluskeyAlgorithm;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.LogicFormula;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ITernaryComparator;

public final class BooleanFunctions {
	public final static Equal CONST_EQUAL = new Equal();
	public final static Greater CONST_GREATER = new Greater();
	public final static Less CONST_LESS = new Less();
	public final static GreaterEqual CONST_GREATER_EQUAL = new GreaterEqual();
	public final static LessEqual CONST_LESS_EQUAL = new LessEqual();

	static {
		F.AllTrue.setEvaluator(new AllTrue());
		F.And.setEvaluator(new And());
		F.AnyTrue.setEvaluator(new AnyTrue());
		F.Boole.setEvaluator(new Boole());
		F.BooleanConvert.setEvaluator(new BooleanConvert());
		F.BooleanMinimize.setEvaluator(new BooleanMinimize());
		F.BooleanTable.setEvaluator(new BooleanTable());
		F.BooleanVariables.setEvaluator(new BooleanVariables());
		F.Equal.setEvaluator(CONST_EQUAL);
		F.Equivalent.setEvaluator(new Equivalent());
		F.Exists.setEvaluator(new Exists());
		F.ForAll.setEvaluator(new ForAll());
		F.Greater.setEvaluator(CONST_GREATER);
		F.GreaterEqual.setEvaluator(new GreaterEqual());
		F.Implies.setEvaluator(new Implies());
		F.Inequality.setEvaluator(new Inequality());
		F.Less.setEvaluator(CONST_LESS);
		F.LessEqual.setEvaluator(new LessEqual());
		F.Max.setEvaluator(new Max());
		F.Min.setEvaluator(new Min());
		F.Nand.setEvaluator(new Nand());
		F.Negative.setEvaluator(new Negative());
		F.NoneTrue.setEvaluator(new NoneTrue());
		F.NonNegative.setEvaluator(new NonNegative());
		F.NonPositive.setEvaluator(new NonPositive());
		F.Nor.setEvaluator(new Nor());
		F.Not.setEvaluator(new Not());
		F.Or.setEvaluator(new Or());
		F.Positive.setEvaluator(new Positive());
		F.SameQ.setEvaluator(new SameQ());
		F.SatisfiabilityCount.setEvaluator(new SatisfiabilityCount());
		F.SatisfiabilityInstances.setEvaluator(new SatisfiabilityInstances());
		F.SatisfiableQ.setEvaluator(new SatisfiableQ());
		F.TautologyQ.setEvaluator(new TautologyQ());
		F.TrueQ.setEvaluator(new TrueQ());
		F.Unequal.setEvaluator(new Unequal());
		F.UnsameQ.setEvaluator(new UnsameQ());
		F.Xor.setEvaluator(new Xor());
	}

	private static class AllTrue extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				IExpr head = ast.arg2();
				return allTrue(list, head, engine);
			}
			return F.NIL;
		}

		/**
		 * If all expressions evaluates to <code>true</code> for a given unary predicate function return
		 * <code>True</code>, if any expression evaluates to <code>false</code> return <code>False</code>, else return
		 * an <code>And(...)</code> expression of the result expressions.
		 * 
		 * @param list
		 *            list of expressions
		 * @param head
		 *            the head of a unary predicate function
		 * @param engine
		 * @return
		 */
		public IExpr allTrue(IAST list, IExpr head, EvalEngine engine) {
			IASTAppendable logicalAnd = F.And();

			if (!list.forAll(x -> {
				IExpr temp = engine.evaluate(F.unaryAST1(head, x));
				if (temp.isTrue()) {
					return true;
				} else if (temp.isFalse()) {
					return false;
				}
				logicalAnd.append(temp);
				return true;
			})) {
				return F.False;
			}

			if (logicalAnd.size() > 1) {
				return logicalAnd;
			}
			return F.True;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * And(expr1, expr2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * <code>expr1 &amp;&amp; expr2 &amp;&amp; ...</code> evaluates each expression in turn, returning
	 * <code>False</code> as soon as an expression evaluates to <code>False</code>. If all expressions evaluate to
	 * <code>True</code>, <code>And</code> returns <code>True</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; True &amp;&amp; True &amp;&amp; False
	 * False
	 * </pre>
	 * <p>
	 * If an expression does not evaluate to <code>True</code> or <code>False</code>, <code>And</code> returns a result
	 * in symbolic form:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a &amp;&amp; b &amp;&amp; True &amp;&amp; c
	 * a &amp;&amp; b &amp;&amp; c
	 * </pre>
	 */
	private static class And extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return F.True;
			}

			boolean evaled = false;

			int index = 1;
			IExpr temp;
			IExpr sym;

			IAST flattenedAST = EvalAttributes.flatten(ast);
			if (flattenedAST.isPresent()) {
				evaled = true;
			} else {
				flattenedAST = ast;
			}

			IASTAppendable result = flattenedAST.copyAppendable();
			int[] symbols = new int[flattenedAST.size()];
			int[] notSymbols = new int[flattenedAST.size()];
			for (int i = 1; i < flattenedAST.size(); i++) {
				temp = flattenedAST.get(i);
				if (temp.isFalse()) {
					return F.False;
				}
				if (temp.isTrue()) {
					result.remove(index);
					evaled = true;
					continue;
				}

				temp = engine.evaluateNull(temp);
				if (temp.isPresent()) {
					if (temp.isFalse()) {
						return F.False;
					}
					if (temp.isTrue()) {
						result.remove(index);
						evaled = true;
						continue;
					}
					result.set(index, temp);
					evaled = true;
				} else {
					temp = flattenedAST.get(i);
				}

				if (temp.isSymbol()) {
					symbols[i] = flattenedAST.get(i).hashCode();
				} else if (temp.isNot()) {
					sym = temp.first();
					if (sym.isSymbol()) {
						notSymbols[i] = sym.hashCode();
					}
				}
				index++;
			}
			for (int i = 1; i < symbols.length; i++) {
				if (symbols[i] != 0) {
					for (int j = 1; j < notSymbols.length; j++) {
						if (i != j && symbols[i] == notSymbols[j] && (result.equalsAt(i, result.get(j).first()))) {
							// And[a, Not[a]] => True
							return F.False;
						}
					}
				}
			}
			if (result.isAST1()) {
				return result.arg1();
			}
			if (evaled) {
				if (result.isAST0()) {
					return F.True;
				}

				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ONEIDENTITY | ISymbol.FLAT);
		}
	}

	/**
	 * <pre>
	 * AnyTrue({expr1, expr2, ...}, test)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if any application of <code>test</code> to <code>expr1, expr2, ...</code> evaluates to
	 * <code>True</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * AnyTrue(list, test, level)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if any application of <code>test</code> to items of <code>list</code> at
	 * <code>level</code> evaluates to <code>True</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * AnyTrue(test)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives an operator that may be applied to expressions.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; AnyTrue({1, 3, 5}, EvenQ)
	 * False
	 * &gt;&gt; AnyTrue({1, 4, 5}, EvenQ)
	 * True
	 * &gt;&gt; AnyTrue({}, EvenQ)
	 * False
	 * </pre>
	 */
	private static class AnyTrue extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				IExpr head = ast.arg2();
				return anyTrue(list, head, engine);
			}
			return F.NIL;
		}

		/**
		 * If any expression evaluates to <code>true</code> for a given unary predicate function return
		 * <code>True</code>, if all are <code>false</code> return <code>False</code>, else return an
		 * <code>Or(...)</code> expression of the result expressions.
		 * 
		 * @param list
		 *            list of expressions
		 * @param head
		 *            the head of a unary predicate function
		 * @param engine
		 * @return
		 */
		public IExpr anyTrue(IAST list, IExpr head, EvalEngine engine) {
			IASTAppendable logicalOr = F.Or();
			if (list.exists(x -> anyTrueArgument(x, head, logicalOr, engine))) {
				return F.True;
			}
			return logicalOr.isAST0() ? F.False : logicalOr;
		}

		private static boolean anyTrueArgument(IExpr x, IExpr head, IASTAppendable resultCollector, EvalEngine engine) {
			IExpr temp = engine.evaluate(F.unaryAST1(head, x));
			if (temp.isTrue()) {
				return true;
			} else if (!temp.isFalse()) {
				resultCollector.append(temp);
			}
			return false;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * Boole(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>1</code> if <code>expr</code> evaluates to <code>True</code>; returns <code>0</code> if
	 * <code>expr</code> evaluates to <code>False</code>; and gives no result otherwise.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Boole(2 == 2)    
	 * 1    
	 * &gt;&gt; Boole(7 &lt; 5)    
	 * 0    
	 * &gt;&gt; Boole(a == 7)    
	 * Boole(a==7)
	 * </pre>
	 */
	private static class Boole extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isSymbol()) {
				if (ast.arg1().isTrue()) {
					return F.C1;
				}
				if (ast.arg1().isFalse()) {
					return F.C0;
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * BooleanConvert(logical - expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * convert the <code>logical-expr</code> to
	 * <a href="https://en.wikipedia.org/wiki/Disjunctive_normal_form">disjunctive normal form</a>
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * BooleanConvert(logical - expr, "CNF")
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * convert the <code>logical-expr</code> to
	 * <a href="https://en.wikipedia.org/wiki/Conjunctive_normal_form">conjunctive normal form</a>
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * BooleanConvert(logical - expr, "DNF")
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * convert the <code>logical-expr</code> to
	 * <a href="https://en.wikipedia.org/wiki/Disjunctive_normal_form">disjunctive normal form</a>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; BooleanConvert(Xor(x,y))
	 * x&amp;&amp;!y||y&amp;&amp;!x
	 * 
	 * &gt;&gt; BooleanConvert(Xor(x,y), "CNF")
	 * (x||y)&amp;&amp;(!x||!y)
	 * 
	 * &gt;&gt; BooleanConvert(Xor(x,y), "DNF")
	 * x&amp;&amp;!y||y&amp;&amp;!x
	 * </pre>
	 */
	private static class BooleanConvert extends AbstractFunctionEvaluator {

		// static class BooleanConvertVisitor extends VisitorExpr {
		// public BooleanConvertVisitor() {
		// super();
		// }
		//
		// @Override
		// protected IExpr visitAST(IAST ast) {
		// if (ast.isNot()) {
		// if (ast.arg1().isAST()) {
		// IAST notArg1 = (IAST) ast.arg1();
		// if (notArg1.isASTSizeGE(Nand, 1)) {
		// return notArg1.apply(And);
		// } else if (notArg1.isASTSizeGE(Nor, 1)) {
		// return notArg1.apply(Or);
		// } else if (notArg1.isASTSizeGE(And, 1)) {
		// return convertNand(notArg1);
		// } else if (notArg1.isASTSizeGE(Or, 1)) {
		// return convertNor(notArg1);
		// }
		//
		// }
		// } else if (ast.isASTSizeGE(Equivalent, 1)) {
		// return convertEquivalent(ast);
		// } else if (ast.isAST(Implies, 3)) {
		// return convertImplies(ast);
		// } else if (ast.isASTSizeGE(Nand, 1)) {
		// return convertNand(ast);
		// } else if (ast.isASTSizeGE(Nor, 1)) {
		// return convertNor(ast);
		// } else if (ast.isASTSizeGE(Xor, 3)) {
		// return convertXor(ast);
		// }
		// return super.visitAST(ast);
		// }
		//
		// public IAST convertEquivalent(IAST ast) {
		// IAST term1 = ast.apply(F.And);
		// IAST term2 = term1.mapThread(F.Not(null), 1);
		// return F.Or(term1, term2);
		// }
		//
		// public IAST convertImplies(IAST ast) {
		// return F.Or(F.Not(ast.arg1()), ast.arg2());
		// }
		//
		// public IAST convertNand(IAST ast) {
		// return Lambda.forEachAppend(ast, F.Or(), x -> F.Not(x));
		// }
		//
		// public IAST convertNor(IAST ast) {
		// return Lambda.forEachAppend(ast, F.And(), x -> F.Not(x));
		// }
		//
		// public IAST convertXor(IAST ast) {
		// IExpr temp = ast.arg2();
		// if (ast.size() > 3) {
		// IASTAppendable clone = ast.copyAppendable();
		// clone.remove(1);
		// temp = convertXor(clone);
		// }
		// return F.Or(F.And(ast.arg1(), F.Not(temp)), F.And(F.Not(ast.arg1()), temp));
		// }
		// }

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			// BooleanConvertVisitor bcVisitor = new BooleanConvertVisitor();
			// IExpr result = ast.arg1().accept(bcVisitor);
			try {

				FormulaTransformation transformation = transformation(ast, engine);
				if (transformation != null) {
					LogicFormula lf = new LogicFormula();
					final Formula formula = lf.expr2BooleanFunction(ast.arg1());
					return lf.booleanFunction2Expr(formula.transform(transformation));
				}

			} catch (ClassCastException cce) {
				if (Config.DEBUG) {
					cce.printStackTrace();
				}
			}
			return F.NIL;
		}

		/**
		 * Get the transformation from the ast options. Default is DNF.
		 * 
		 * @param ast
		 * @param engine
		 * @return <code>null</code> if no or wrong method is defined as option
		 */
		private static FormulaTransformation transformation(final IAST ast, EvalEngine engine) {
			int size = ast.argSize();
			if (size > 1 && ast.get(size).isString()) {
				IStringX arg2 = (IStringX) ast.arg2();
				String method = arg2.toString();
				if (method.equals("DNF") || method.equals("SOP")) {
					return new DNFFactorization();
				} else if (method.equals("CNF") || method.equals("POS")) {
					return new CNFFactorization();
				}
				engine.printMessage("Unknown method: " + method);
				return null;
			}
			return new DNFFactorization();
		}
	}

	/**
	 * <pre>
	 * BooleanMinimize(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * minimizes a boolean function with the
	 * <a href="https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm">Quine McCluskey algorithm</a>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; BooleanMinimize(x&amp;&amp;y||(!x)&amp;&amp;y)
	 * y
	 * 
	 * &gt;&gt; BooleanMinimize((a&amp;&amp;!b)||(!a&amp;&amp;b)||(b&amp;&amp;!c)||(!b&amp;&amp;c))
	 * a&amp;&amp;!b||!a&amp;&amp;c||b&amp;&amp;!c
	 * </pre>
	 */
	private static class BooleanMinimize extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			FormulaFactory factory = new FormulaFactory();
			LogicFormula lf = new LogicFormula(factory);
			Formula formula = lf.expr2BooleanFunction(ast.arg1());
			// System.out.println(">> " + formula.toString());
			// only DNF form
			formula = QuineMcCluskeyAlgorithm.compute(formula);
			// System.out.println(formula.toString());
			return lf.booleanFunction2Expr(formula);
			// TODO CNF form after minimizing blows up the formula.
			// FormulaTransformation transformation = BooleanConvert.transformation(ast, engine);
			// return lf.booleanFunction2Expr(formula.transform(transformation));
		}
	}

	/**
	 * <pre>
	 * BooleanTable(logical - expr, variables)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * generate <a href="https://en.wikipedia.org/wiki/Truth_table">truth values</a> from the <code>logical-expr</code>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; BooleanTable(Implies(Implies(p, q), r), {p, q, r})
	 * {True,False,True,True,True,False,True,False}
	 * 
	 * &gt;&gt; BooleanTable(Xor(p, q, r), {p, q, r})
	 * {True,False,False,True,False,True,True,False}
	 * </pre>
	 */
	private static class BooleanTable extends AbstractFunctionEvaluator {

		private static class BooleanTableParameter {
			public IAST variables;
			public IASTAppendable resultList;
			public EvalEngine engine;

			public BooleanTableParameter(IAST variables, EvalEngine engine) {
				this.variables = variables;
				this.resultList = F.ListAlloc(variables.size());
				this.engine = engine;
			}

			public IAST booleanTable(IExpr expr, int position) {
				if (variables.size() <= position) {
					resultList.append(engine.evalTrue(expr) ? F.True : F.False);
					return resultList;
				}
				IExpr sym = variables.get(position);
				if (sym.isSymbol()) {
					try {
						((ISymbol) sym).pushLocalVariable(F.True);
						booleanTable(expr, position + 1);
					} finally {
						((ISymbol) sym).popLocalVariable();
					}
					try {
						((ISymbol) sym).pushLocalVariable(F.False);
						booleanTable(expr, position + 1);
					} finally {
						((ISymbol) sym).popLocalVariable();
					}
				}
				return resultList;
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IAST variables;
			if (ast.isAST2()) {
				variables = ast.arg2().orNewList();
			} else {
				variables = BooleanVariables.booleanVariables(ast.arg2());
			}

			BooleanTableParameter btp = new BooleanTableParameter(variables, engine);
			return btp.booleanTable(ast.arg1(), 1);
		}

	}

	/**
	 * <pre>
	 * BooleanVariables(logical - expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives a list of the boolean variables that appear in the <code>logical-expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; BooleanVariables(Xor(p,q,r))
	 * {p,q,r}
	 * </pre>
	 */
	private static class BooleanVariables extends AbstractFunctionEvaluator {

		/**
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			return booleanVariables(ast.arg1());
		}

		private static IAST booleanVariables(final IExpr expr) {
			VariablesSet eVar = new VariablesSet();
			eVar.addBooleanVarList(expr);
			return eVar.getVarList();
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * Equal(x, y) 
	 * 
	 * x == y
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * yields <code>True</code> if <code>x</code> and <code>y</code> are known to be equal, or <code>False</code> if
	 * <code>x</code> and <code>y</code> are known to be unequal.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * lhs == rhs
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents the equation <code>lhs = rhs</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; a==a
	 * True
	 * 
	 * &gt;&gt; a==b
	 * a == b
	 * 
	 * &gt;&gt; 1==1
	 * True
	 * </pre>
	 * <p>
	 * Lists are compared based on their elements:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {{1}, {2}} == {{1}, {2}}
	 * True
	 * &gt;&gt; {1, 2} == {1, 2, 3}
	 * False
	 * </pre>
	 * <p>
	 * Symbolic constants are compared numerically:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; E &gt; 1
	 * True
	 * 
	 * &gt;&gt; Pi == 3.14
	 * False
	 * 
	 * &gt;&gt; Pi ^ E == E ^ Pi
	 * False
	 * 
	 * &gt;&gt; N(E, 3) == N(E)
	 * True
	 * 
	 * &gt;&gt; {1, 2, 3} &lt; {1, 2, 3}
	 * {1, 2, 3} &lt; {1, 2, 3}
	 * 
	 * &gt;&gt; E == N(E)
	 * True
	 * 
	 * &gt;&gt; {Equal(Equal(0, 0), True), Equal(0, 0) == True}
	 * {True, True}
	 * 
	 * &gt;&gt; {Mod(6, 2) == 0, Mod(6, 4) == 0, (Mod(6, 2) == 0) == (Mod(6, 4) == 0), (Mod(6, 2) == 0) != (Mod(6, 4) == 0)}
	 * {True,False,False,True}
	 * 
	 * &gt;&gt; a == a == a
	 * True
	 * 
	 * &gt;&gt; {Equal(), Equal(x), Equal(1)}
	 * {True, True, True}
	 * </pre>
	 */

	public static class Equal extends AbstractFunctionEvaluator implements ITernaryComparator {

		/**
		 * Create the result for a <code>simplifyCompare()</code> step
		 * <code>equalOrUnequalSymbol[lhsAST.rest(), rhs]</code>
		 * 
		 * @param equalOrUnequalSymbol
		 * @param lhsAST
		 * @param rhs
		 * 
		 * @return
		 */
		private static IExpr createComparatorResult(IBuiltInSymbol equalOrUnequalSymbol, IAST lhsAST, IExpr rhs) {
			return F.binaryAST2(equalOrUnequalSymbol, lhsAST.rest(), rhs);
		}

		/**
		 * Try to simplify a comparator expression. Example: <code>3*x > 6</code> will be simplified to
		 * <code>x > 2</code>.
		 * 
		 * @param equalOrUnequalSymbol
		 *            symbol for which the simplification was started
		 * @param a1
		 *            left-hand-side of the comparator expression
		 * @param a2
		 *            right-hand-side of the comparator expression
		 * 
		 * @return the simplified comparator expression or <code>F.NIL</code> if no simplification was found
		 */
		protected static IExpr simplifyCompare(IBuiltInSymbol equalOrUnequalSymbol, IExpr a1, IExpr a2) {
			IExpr lhs, rhs;
			if (a2.isNumber()) {
				lhs = a1;
				rhs = a2;
			} else if (a1.isNumber()) {
				lhs = a2;
				rhs = a1;
			} else {
				return F.NIL;
			}
			if (lhs.isAST()) {
				IAST lhsAST = (IAST) lhs;
				if (lhsAST.isTimes()) {
					if (lhsAST.arg1().isNumber()) {
						INumber sn = (INumber) lhsAST.arg1();
						rhs = F.eval(F.Divide(rhs, sn));
						return createComparatorResult(equalOrUnequalSymbol, lhsAST, rhs);
					}
				} else if (lhsAST.isPlus() && lhsAST.arg1().isNumber()) {
					INumber sn = (INumber) lhsAST.arg1();
					rhs = F.eval(F.Subtract(rhs, sn));
					return createComparatorResult(equalOrUnequalSymbol, lhsAST, rhs);
				}
			}
			return F.NIL;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 2) {
				IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.UNDEFINED;
				if (ast.isAST2()) {
					return equalNull(ast.arg1(), ast.arg2(), engine);
				}
				boolean evaled = false;
				IASTAppendable result = ast.copyAppendable();
				int i = 2;
				IExpr arg1 = F.expandAll(result.arg1(), true, true);
				while (i < result.size()) {
					IExpr arg2 = F.expandAll(result.get(i), true, true);
					b = prepareCompare(arg1, arg2, engine);
					if (b == IExpr.COMPARE_TERNARY.FALSE) {
						return F.False;
					} else if (b == IExpr.COMPARE_TERNARY.TRUE) {
						evaled = true;
						result.remove(i - 1);
					} else {
						result.set(i - 1, arg1);
						i++;
						arg1 = arg2;
					}

				}
				if (evaled) {
					if (result.isAST1()) {
						return F.True;
					}
					return result;
				}
				return F.NIL;
			}
			return F.True;
		}

		/**
		 * 
		 * @param arg1
		 * @param arg2
		 * @param engine
		 * @return
		 */
		protected IExpr.COMPARE_TERNARY prepareCompare(final IExpr arg1, final IExpr arg2, EvalEngine engine) {
			if (arg1.isIndeterminate() || arg2.isIndeterminate()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}
			if (arg1.isList() && arg2.isList()) {
				IAST list1 = (IAST) arg1;
				IAST list2 = (IAST) arg2;
				int size1 = list1.size();
				if (size1 != list2.size()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				}
				IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.TRUE;
				for (int i = 1; i < size1; i++) {
					b = prepareCompare(list1.get(i), list2.get(i), engine);
					if (b == IExpr.COMPARE_TERNARY.FALSE) {
						return IExpr.COMPARE_TERNARY.FALSE;
					}
					if (b == IExpr.COMPARE_TERNARY.TRUE) {
					} else {
						return IExpr.COMPARE_TERNARY.UNDEFINED;
					}
				}
				return IExpr.COMPARE_TERNARY.TRUE;
			}
			IExpr a0 = arg1;
			IExpr a1 = arg2;
			if (!a0.isReal() && a0.isNumericFunction()) {
				a0 = engine.evalN(a0);
			} else if (a1.isNumeric() && a0.isRational()) {
				a0 = engine.evalN(a0);
			}
			if (!a1.isReal() && a1.isNumericFunction()) {
				a1 = engine.evalN(a1);
			} else if (a0.isNumeric() && a1.isRational()) {
				a1 = engine.evalN(a1);
			}

			return compareTernary(a0, a1);
		}

		/** {@inheritDoc} */
		@Override
		public IExpr.COMPARE_TERNARY compareTernary(final IExpr o0, final IExpr o1) {
			if (o0.isSame(o1)) {
				return IExpr.COMPARE_TERNARY.TRUE;
			}

			if (o0.isTrue()) {
				if (o1.isTrue()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				} else if (o1.isFalse()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				}
			} else if (o0.isFalse()) {
				if (o1.isTrue()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				} else if (o1.isFalse()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				}
			}
			if (o0.isConstant() && o1.isConstant()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}

			if (o0.isNumber() && o1.isNumber()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}

			if ((o0 instanceof StringX) && (o1 instanceof StringX)) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}

			IExpr difference = F.eval(F.Subtract(o0, o1));
			if (difference.isNumber()) {
				if (difference.isZero()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				}
				return IExpr.COMPARE_TERNARY.FALSE;
			}
			if (difference.isConstant()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}

			return IExpr.COMPARE_TERNARY.UNDEFINED;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}
	}

	/**
	 * <pre>
	 * Equivalent(arg1, arg2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Equivalence relation. <code>Equivalent(A, B)</code> is <code>True</code> iff <code>A</code> and <code>B</code>
	 * are both <code>True</code> or both <code>False</code>. Returns <code>True</code> if all of the arguments are
	 * logically equivalent. Returns <code>False</code> otherwise. <code>Equivalent(arg1, arg2, ...)</code> is
	 * equivalent to <code>(arg1 &amp;&amp; arg2 &amp;&amp; ...) || (!arg1 &amp;&amp; !arg2 &amp;&amp; ...)</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Equivalent(True, True, False)
	 * False
	 * 
	 * &gt;&gt; Equivalent(x, x &amp;&amp; True)
	 * True
	 * </pre>
	 * <p>
	 * If all expressions do not evaluate to 'True' or 'False', 'Equivalent' returns a result in symbolic form:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Equivalent(a, b, c)
	 * Equivalent(a,b,c)
	 * </pre>
	 * <p>
	 * Otherwise, 'Equivalent' returns a result in DNF
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Equivalent(a, b, True, c)
	 * a &amp;&amp; b &amp;&amp; c
	 * &gt;&gt; Equivalent()
	 * True
	 * &gt;&gt; Equivalent(a)
	 * True
	 * </pre>
	 */
	private final static class Equivalent extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0() || ast.isAST1()) {
				return F.True;
			}
			IASTAppendable result = ast.copyHead();
			IExpr last = F.NIL;
			IExpr boole = F.NIL;
			boolean evaled = false;

			for (int i = 1; i < ast.size(); i++) {
				IExpr temp = ast.get(i);
				if (temp.isFalse()) {
					if (!boole.isPresent()) {
						boole = F.False;
						evaled = true;
					} else if (boole.isTrue()) {
						return F.False;
					} else {
						evaled = true;
					}
				} else if (temp.isTrue()) {
					if (!boole.isPresent()) {
						boole = F.True;
						evaled = true;
					} else if (boole.isFalse()) {
						return F.False;
					} else {
						evaled = true;
					}
				} else {
					if (!last.equals(temp)) {
						result.append(temp);
					} else {
						evaled = true;
					}

					last = temp;
				}
			}
			if (evaled) {
				if (result.isAST0()) {
					if (boole.isPresent()) {
						return F.True;
					}
				} else if (result.isAST1() && !boole.isPresent()) {
					return F.True;
				}
				if (boole.isPresent()) {
					result = result.apply(F.And);
					if (boole.isTrue()) {
						return result;
					} else {
						return result.mapThread(F.Not(null), 1);
					}
				}
				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ORDERLESS);
		}
	}

	private final static class Exists extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			boolean evaled = false;
			// TODO localize x
			IExpr x = engine.evaluateNull(ast.arg1());
			if (x.isPresent()) {
				evaled = true;
			} else {
				x = ast.arg1();
			}

			IExpr expr = engine.evaluateNull(ast.arg2());
			if (expr.isPresent()) {
				evaled = true;
			} else {
				expr = ast.arg2();
			}

			if (ast.isAST3()) {
				IExpr arg3 = engine.evaluateNull(ast.arg3());
				if (arg3.isPresent()) {
					evaled = true;
				} else {
					arg3 = ast.arg3();
				}
				if (evaled) {
					return F.Exists(x, expr, arg3);
				}
				return F.NIL;
			}

			if (expr.isFree(x)) {
				return expr;
			}
			if (evaled) {
				return F.Exists(x, expr);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class ForAll extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			boolean evaled = false;
			// TODO localize x
			IExpr x = engine.evaluateNull(ast.arg1());
			if (x.isPresent()) {
				evaled = true;
			} else {
				x = ast.arg1();
			}

			IExpr expr = engine.evaluateNull(ast.arg2());
			if (expr.isPresent()) {
				evaled = true;
			} else {
				expr = ast.arg2();
			}

			if (ast.isAST3()) {
				IExpr arg3 = engine.evaluateNull(ast.arg3());
				if (arg3.isPresent()) {
					evaled = true;
				} else {
					arg3 = ast.arg3();
				}
				if (evaled) {
					return F.ForAll(x, expr, arg3);
				}
				return F.NIL;
			}

			if (expr.isFree(x)) {
				return expr;
			}
			if (evaled) {
				return F.ForAll(x, expr);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * Greater(x, y) 
	 * 
	 * x &gt; y
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * yields <code>True</code> if <code>x</code> is known to be greater than <code>y</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * lhs &gt; rhs
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents the inequality <code>lhs &gt; rhs</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Pi&gt;0
	 * True
	 * 
	 * &gt;&gt; {Greater(), Greater(x), Greater(1)}
	 * {True, True, True}
	 * </pre>
	 */
	public static class Greater extends AbstractCoreFunctionEvaluator implements ITernaryComparator {
		public final static Greater CONST = new Greater();

		/**
		 * Check assumptions for the comparison operator. Will be overridden in
		 * <code>GreaterEqual, Less, LessEqual</code>.
		 * 
		 * @param arg1
		 *            the left-hand-side of the comparison
		 * @param arg2
		 *            the right-hand-side of the comparison
		 * @return
		 */
		protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
			if (arg2.isNegative()) {
				// arg1 > "negative number"
				if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
					return F.True;
				}
			} else if (arg2.isZero()) {
				// arg1 > 0
				if (arg1.isPositiveResult()) {
					return F.True;
				}
				if (arg1.isNegativeResult()) {
					return F.False;
				}
			} else {
				// arg1 > "positive number" > 0
				if (arg1.isNegativeResult()) {
					return F.False;
				}
			}
			return F.NIL;
		}

		/**
		 * Compare two intervals if they are greater.
		 * 
		 * <ul>
		 * <li>Return TRUE if the comparison is <code>true</code></li>
		 * <li>Return FALSE if the comparison is <code>false</code></li>
		 * <li>Return UNDEFINED if the comparison is undetermined (i.e. could not be evaluated)</li>
		 * </ul>
		 * 
		 * @param lower0
		 *            the lower bound of the first interval
		 * @param upper0
		 *            the upper bound of the first interval
		 * @param lower1
		 *            the lower bound of the second interval
		 * @param upper1
		 *            the upper bound of the second interval
		 * @return
		 */
		private static IExpr.COMPARE_TERNARY compareGreaterIntervalTernary(final IExpr lower0, final IExpr upper0,
				final IExpr lower1, final IExpr upper1) {
			if (lower0.greaterThan(upper1).isTrue()) {
				return IExpr.COMPARE_TERNARY.TRUE;
			} else {
				if (upper0.lessThan(lower1).isTrue()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				}
			}
			return IExpr.COMPARE_TERNARY.UNDEFINED;
		}

		/** {@inheritDoc} */
		@Override
		public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
			// don't compare strings
			if (a0.isReal()) {
				if (a1.isReal()) {
					return ((ISignedNumber) a0).isGreaterThan((ISignedNumber) a1) ? IExpr.COMPARE_TERNARY.TRUE
							: IExpr.COMPARE_TERNARY.FALSE;
				} else if (a1.isInfinity()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				} else if (a1.isNegativeInfinity()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				} else if (a1.isInterval1()) {
					return compareGreaterIntervalTernary(a0.lower(), a0.upper(), a1.lower(), a1.upper());
				}
			} else if (a1.isReal()) {
				if (a0.isInfinity()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				} else if (a0.isNegativeInfinity()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				} else if (a0.isInterval1()) {
					return compareGreaterIntervalTernary(a0.lower(), a0.upper(), a1.lower(), a1.upper());
				}
			} else if (a0.isInfinity()) {
				if (a1.isRealResult() || a1.isNegativeInfinity()) {
					return IExpr.COMPARE_TERNARY.TRUE;
				}
			} else if (a0.isNegativeInfinity()) {
				if (a1.isRealResult() || a1.isInfinity()) {
					return IExpr.COMPARE_TERNARY.FALSE;
				}
			} else if (a1.isInfinity() && a0.isRealResult()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			} else if (a1.isNegativeInfinity() && a0.isRealResult()) {
				return IExpr.COMPARE_TERNARY.TRUE;
			} else if (a0.isInterval1() && a1.isInterval1()) {
				return compareGreaterIntervalTernary(a0.lower(), a0.upper(), a1.lower(), a1.upper());
			}

			if (a0.equals(a1) && !a0.isList()) {
				return IExpr.COMPARE_TERNARY.FALSE;
			}

			return IExpr.COMPARE_TERNARY.UNDEFINED;
		}

		/**
		 * Create the result for a <code>simplifyCompare()</code> step.
		 * 
		 * @param lhs
		 *            left-hand-side of the comparator expression
		 * @param rhs
		 *            right-hand-side of the comparator expression
		 * @param useOppositeHeader
		 *            use the opposite header to create the result
		 * @param originalHead
		 *            symbol of the comparator operator for which the simplification was started
		 * @param oppositeHead
		 *            opposite of the symbol of the comparator operator for which the comparison was started
		 * @return
		 */
		private IAST createComparatorResult(IExpr lhs, IExpr rhs, boolean useOppositeHeader, ISymbol originalHead,
				ISymbol oppositeHead) {
			return F.binary(useOppositeHeader ? oppositeHead : originalHead, lhs, rhs);
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// Validate.checkRange(ast, 3);
			if (ast.size() <= 2) {
				return F.True;
			}

			IExpr temp = engine.evalAttributes((ISymbol) ast.head(), ast);
			if (temp.isPresent()) {
				return temp;
			}
			
			IAST astEvaled = ast;
			if (astEvaled.isAST2()) {
				IExpr arg1 = astEvaled.arg1();
				IExpr arg2 = astEvaled.arg2();
				IExpr result = simplifyCompare(arg1, arg2);
				if (result.isPresent()) {
					// the result may return an AST with an "opposite header"
					// (i.e. Less instead of Greater)
					return result;
				}
				if (arg2.isReal()) {
					// this part is used in other comparator operations like
					// Less,
					// GreaterEqual,...
					IExpr temp2 = checkAssumptions(arg1, arg2);
					if (temp2.isPresent()) {
						return temp2;
					}
				}
			}
			boolean evaled = false;
			IExpr.COMPARE_TERNARY b;
			IASTAppendable result = astEvaled.copyAppendable();
			IExpr.COMPARE_TERNARY[] cResult = new IExpr.COMPARE_TERNARY[astEvaled.size()];
			cResult[0] = IExpr.COMPARE_TERNARY.TRUE;
			for (int i = 1; i < astEvaled.argSize(); i++) {
				b = prepareCompare(result.get(i), result.get(i + 1), engine);
				if (b == IExpr.COMPARE_TERNARY.FALSE) {
					return F.False;
				}
				if (b == IExpr.COMPARE_TERNARY.TRUE) {
					evaled = true;
				}
				cResult[i] = b;
			}
			cResult[astEvaled.argSize()] = IExpr.COMPARE_TERNARY.TRUE;
			if (!evaled) {
				// expression doesn't change
				return F.NIL;
			}
			int i = 2;
			evaled = false;
			for (int j = 1; j < astEvaled.size(); j++) {
				if (cResult[j - 1] == IExpr.COMPARE_TERNARY.TRUE && cResult[j] == IExpr.COMPARE_TERNARY.TRUE) {
					evaled = true;
					result.remove(i - 1);
				} else {
					i++;
				}
			}

			if (evaled) {
				if (result.size() <= 2) {
					return F.True;
				}
				return result;
			}
			return F.NIL;
		}

		private IExpr.COMPARE_TERNARY prepareCompare(IExpr a0, IExpr a1, EvalEngine engine) {
			if (!a0.isReal() && a0.isNumericFunction()) {
				a0 = engine.evalN(a0);
			} else if (a1.isNumeric() && a0.isRational()) {
				a0 = engine.evalN(a0);
			}
			if (!a1.isReal() && a1.isNumericFunction()) {
				a1 = engine.evalN(a1);
			} else if (a0.isNumeric() && a1.isRational()) {
				a1 = engine.evalN(a1);
			}

			return compareTernary(a0, a1);
		}

		public IExpr.COMPARE_TERNARY prepareCompare(final IExpr o0, final IExpr o1) {
			return prepareCompare(o0, o1, EvalEngine.get());
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.FLAT);
		}

		/**
		 * Try to simplify a comparator expression. Example: <code>3*x &gt; 6</code> will be simplified to
		 * <code>x &gt; 2</code>.
		 * 
		 * @param a1
		 *            left-hand-side of the comparator expression
		 * @param a2
		 *            right-hand-side of the comparator expression
		 * @return the simplified comparator expression or <code>null</code> if no simplification was found
		 */
		protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
			return simplifyCompare(a1, a2, F.Greater, F.Less, true);
		}

		/**
		 * Try to simplify a comparator expression. Example: <code>3*x &gt; 6</code> wll be simplified to
		 * <code>x &gt; 2</code>.
		 * 
		 * @param a1
		 *            left-hand-side of the comparator expression
		 * @param a2
		 *            right-hand-side of the comparator expression
		 * @param originalHead
		 *            symbol of the comparator operator for which the simplification was started
		 * @param oppositeHead
		 *            opposite of the symbol of the comparator operator for which the comparison was started
		 * @param setTrue
		 *            if <code>true</code> return F.True otherwise F.False
		 * @return the simplified comparator expression or <code>F.NIL</code> if no simplification was found
		 */
		final protected IExpr simplifyCompare(IExpr a1, IExpr a2, IBuiltInSymbol originalHead,
				IBuiltInSymbol oppositeHead, boolean setTrue) {
			IExpr lhs;
			IExpr rhs;
			boolean useOppositeHeader = false;
			if (a2.isNumericFunction()) {
				lhs = a1;
				rhs = a2;
			} else if (a1.isNumericFunction()) {
				lhs = a2;
				rhs = a1;
				useOppositeHeader = true;
			} else {
				lhs = F.eval(F.Subtract(a1, a2));
				rhs = F.C0;
			}
			if (lhs.isAST()) {
				IAST lhsAST = (IAST) lhs;
				if (useOppositeHeader) {
					setTrue = !setTrue;
				}
				if (lhsAST.isInfinity() && rhs.isRealResult()) {
					// Infinity > rhs ?
					return setTrue ? F.True : F.False;
				}
				if (lhsAST.isNegativeInfinity() && rhs.isRealResult()) {
					// -Infinity > rhs ?
					return setTrue ? F.False : F.True;
				}
				if (rhs.isInfinity() && lhsAST.isRealResult()) {
					// lhs > Infinity ?
					return setTrue ? F.False : F.True;
				}
				if (rhs.isNegativeInfinity() && lhsAST.isRealResult()) {
					// lhs > -Infinity ?
					return setTrue ? F.True : F.False;
				}
				if (lhsAST.isTimes()) {
					IAST result = lhsAST.partitionTimes(x -> x.isNumericFunction(), F.C0, F.C1, F.List);
					if (!result.get(1).isZero()) {
						if (result.get(1).isNegative()) {
							useOppositeHeader = !useOppositeHeader;
						}
						rhs = rhs.divide(result.get(1));
						return createComparatorResult(result.get(2), rhs, useOppositeHeader, originalHead,
								oppositeHead);
					}
				} else if (lhsAST.isPlus()) {
					IAST result = lhsAST.partitionPlus(x -> x.isNumericFunction(), F.C0, F.C0, F.List);
					if (!result.arg1().isZero()) {
						rhs = rhs.subtract(result.get(1));
						return createComparatorResult(result.arg2(), rhs, useOppositeHeader, originalHead,
								oppositeHead);
					}
				}
			}
			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * GreaterEqual(x, y) 
	 * 
	 * x &gt;= y
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * yields <code>True</code> if <code>x</code> is known to be greater than or equal to <code>y</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * lhs &gt;= rhs
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents the inequality <code>lhs &gt;= rhs</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; x&gt;=x
	 * True
	 * 
	 * &gt;&gt; {GreaterEqual(), GreaterEqual(x), GreaterEqual(1)}
	 * {True, True, True}
	 * </pre>
	 */
	public final static class GreaterEqual extends Greater {
		public final static GreaterEqual CONST = new GreaterEqual();

		/** {@inheritDoc} */
		@Override
		protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
			if (arg2.isNegative()) {
				// arg1 >= "negative number"
				if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
					return F.True;
				}
			} else if (arg2.isZero()) {
				// arg1 >= 0
				if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
					return F.True;
				}
				if (arg1.isNegativeResult()) {
					return F.False;
				}
			} else {
				// arg1 >= "positive number" > 0
				if (arg1.isNegativeResult()) {
					return F.False;
				}
			}
			return F.NIL;
		}

		/** {@inheritDoc} */
		@Override
		protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
			return simplifyCompare(a1, a2, F.GreaterEqual, F.LessEqual, true);
		}

		/** {@inheritDoc} */
		@Override
		public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
			if (a0.equals(a1)) {
				return IExpr.COMPARE_TERNARY.TRUE;
			}
			return super.compareTernary(a0, a1);
		}

	}

	/**
	 * <pre>
	 * Implies(arg1, arg2)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Logical implication. <code>Implies(A, B)</code> is equivalent to <code>!A || B</code>.
	 * <code>Implies(expr1, expr2)</code> evaluates each argument in turn, returning <code>True</code> as soon as the
	 * first argument evaluates to <code>False</code>. If the first argument evaluates to <code>True</code>,
	 * <code>Implies</code> returns the second argument.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Implies(False, a)
	 * True
	 * &gt;&gt; Implies(True, a)
	 * a
	 * </pre>
	 * <p>
	 * If an expression does not evaluate to <code>True</code> or <code>False</code>, <code>Implies</code> returns a
	 * result in symbolic form:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Implies(a, Implies(b, Implies(True, c)))
	 * Implies(a,Implies(b,c))
	 * </pre>
	 */
	private final static class Implies extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			boolean evaled = false;
			IExpr arg1 = engine.evaluateNull(ast.arg1());
			if (arg1.isPresent()) {
				evaled = true;
			} else {
				arg1 = ast.arg1();
			}
			if (arg1.isTrue()) {
				return ast.arg2();
			}
			if (arg1.isFalse()) {
				return F.True;
			}

			IExpr arg2 = engine.evaluateNull(ast.arg2());
			if (arg2.isPresent()) {
				evaled = true;
			} else {
				arg2 = ast.arg2();
			}
			if (arg2.isTrue()) {
				return F.True;
			}
			if (arg2.isFalse()) {
				return F.Not(arg1);
			}
			if (arg1.equals(arg2)) {
				return F.True;
			}

			if (evaled) {
				return F.Implies(arg1, arg2);
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Inequality extends AbstractEvaluator {
		final static IBuiltInSymbol[] COMPARATOR_SYMBOLS = { F.Greater, F.GreaterEqual, F.Less, F.LessEqual };

		private int getCompSign(IExpr e) {
			if (e.isSymbol()) {
				if (e.equals(F.Less) || e.equals(F.LessEqual)) {
					return -1;
				}
				if (e.equals(F.Equal)) {
					return 0;
				}
				if (e.equals(F.Greater) || e.equals(F.GreaterEqual)) {
					return 1;
				}
			}
			return -2;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				return F.True;
			}
			try {
				Validate.checkRange(ast, 4, Integer.MAX_VALUE);

				if (ast.size() == 4) {
					for (IBuiltInSymbol sym : COMPARATOR_SYMBOLS) {
						if (sym.equals(ast.arg2())) {
							return F.binary(ast.arg2(), ast.arg1(), ast.arg3());
						}
					}
					return F.NIL;
				}

				if ((ast.size()) % 2 != 0) {
					return F.NIL;
				}
				int firstSign = getCompSign(ast.arg2());
				if (firstSign == -2) {
					return F.NIL;
				}
				if (firstSign != 0) {
					for (int i = 4; i < ast.size(); i += 2) {
						int thisSign = getCompSign(ast.get(i));
						if (thisSign == -2) {
							return F.NIL;
						}
						if (thisSign == -firstSign) {
							IASTAppendable firstIneq = F.ast(F.Inequality);
							IASTAppendable secondIneq = F.ast(F.Inequality);
							for (int j = 1; j < ast.size(); j++) {
								if (j < i) {
									firstIneq.append(ast.get(j));
								}
								if (j > (i - 2)) {
									secondIneq.append(ast.get(j));
								}
							}
							return F.And(firstIneq, secondIneq);
						}
					}
				}
				IASTAppendable res = F.ast(F.Inequality);
				for (int i = 0; i < (ast.size() - 1) / 2; i++) {
					IExpr lhs = ast.get(2 * i + 1);
					if (res.size() > 1) {
						lhs = res.get(res.size() - 1);
					}
					IExpr op = ast.get(2 * i + 2);
					IExpr rhs = ast.get(2 * i + 3);
					for (int rhsI = 2 * i + 3; rhsI < ast.size(); rhsI += 2) {
						IExpr temp = engine.evaluate(F.binaryAST2(op, lhs, ast.get(rhsI)));
						if (temp.isFalse()) {
							return F.False;
						}
					}
					IExpr evalRes = engine.evaluate(F.binaryAST2(op, lhs, rhs));
					if (!evalRes.isTrue()) {
						if (engine.evaluate(F.SameQ(lhs, res.get(res.size() - 1))).isFalse()) {
							res.append(lhs);
						}
						res.append(op);
						res.append(rhs);
					}
				}
				if (res.size() == 1) {
					return F.True;
				}
				if (res.size() == 4) {
					return F.binaryAST2(res.arg2(), res.arg1(), res.arg3());
				}
				if (res.size() == ast.size()) {
					return F.NIL;
				}
				return res;

				// return inequality(ast, engine);
			} catch (WrongNumberOfArguments woa) {
				engine.printMessage(woa.getMessage());
				return F.NIL;
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * Less(x, y) 
	 * 
	 * x &lt; y
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * yields <code>True</code> if <code>x</code> is known to be less than <code>y</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * lhs &lt; rhs
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents the inequality <code>lhs &lt; rhs</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; 3&lt;4
	 * True
	 * 
	 * &gt;&gt; {Less(), Less(x), Less(1)}
	 * {True, True, True}
	 * </pre>
	 */
	public final static class Less extends Greater {

		/** {@inheritDoc} */
		@Override
		protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
			if (arg2.isNegative()) {
				// arg1 < "negative number"
				if (arg1.isPositiveResult()) {
					return F.False;
				}

			} else if (arg2.isZero()) {
				// arg1 < 0
				if (arg1.isNegativeResult()) {
					return F.True;
				}
				if (arg1.isPositiveResult()) {
					return F.False;
				}
			} else {
				// arg1 < "positive number"
				if (arg1.isNegativeResult()) {
					return F.True;
				}
			}
			return F.NIL;
		}

		/** {@inheritDoc} */
		@Override
		protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
			return simplifyCompare(a1, a2, F.Less, F.Greater, false);
		}

		/** {@inheritDoc} */
		@Override
		public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
			// swap arguments
			return super.compareTernary(a1, a0);
		}

	}

	/**
	 * <pre>
	 * LessEqual(x, y) 
	 * 
	 * x &lt;= y
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * yields <code>True</code> if <code>x</code> is known to be less than or equal <code>y</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * lhs &lt;= rhs
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents the inequality <code>lhs ≤ rhs</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; 3&lt;=4
	 * True
	 * 
	 * &gt;&gt; {LessEqual(), LessEqual(x), LessEqual(1)}
	 * {True, True, True}
	 * </pre>
	 */
	public final static class LessEqual extends Greater {

		/** {@inheritDoc} */
		@Override
		protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
			if (arg2.isNegative()) {
				// arg1 <= "negative number"
				if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
					return F.False;
				}
			} else if (arg2.isZero()) {
				// arg1 <= 0
				if (arg1.isNegativeResult()) {
					return F.True;
				}
				if (arg1.isPositiveResult()) {
					return F.False;
				}
			} else {
				// arg1 <= "positive number"
				if (arg1.isNegativeResult()) {
					return F.True;
				}
			}
			return F.NIL;
		}

		/** {@inheritDoc} */
		@Override
		protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
			return simplifyCompare(a1, a2, F.LessEqual, F.GreaterEqual, false);
		}

		/** {@inheritDoc} */
		@Override
		public IExpr.COMPARE_TERNARY compareTernary(final IExpr a0, final IExpr a1) {
			// don't compare strings
			if (a0.equals(a1)) {
				return IExpr.COMPARE_TERNARY.TRUE;
			}
			// swap arguments
			return super.compareTernary(a1, a0);
		}
	}

	/**
	 * <pre>
	 * Max(e_1, e_2, ..., e_i)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the expression with the greatest value among the <code>e_i</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * Maximum of a series of numbers:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Max(4, -8, 1)
	 * 4
	 * </pre>
	 * <p>
	 * <code>Max</code> flattens lists in its arguments:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Max({1,2},3,{-3,3.5,-Infinity},{{1/2}})
	 * 3.5
	 * </pre>
	 * <p>
	 * <code>Max</code> with symbolic arguments remains in symbolic form:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Max(x, y)
	 * Max(x,y)
	 * 
	 * &gt;&gt; Max(5, x, -3, y, 40)
	 * Max(40,x,y)
	 * </pre>
	 * <p>
	 * With no arguments, <code>Max</code> gives <code>-Infinity</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Max()
	 * -Infinity
	 * 
	 * &gt;&gt; Max(x)
	 * x
	 * </pre>
	 */
	private static class Max extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 1);

			if (ast.isAST0()) {
				return F.CNInfinity;
			}

			if (ast.arg1().isInterval1()) {
				IAST list = (IAST) ast.arg1().first();
				try {
					return list.arg2();
				} catch (ClassCastException cca) {
					// do nothing
				}
			}

			IAST resultList = EvalAttributes.flatten(F.List, ast);
			if (resultList.isPresent()) {
				return maximum(resultList, true);
			}

			return maximum(ast, false);
		}

		private IExpr maximum(IAST list, boolean flattenedList) {
			boolean evaled = false;
			// int j = 1;
			IASTAppendable f = Lambda.remove(list, x -> x.isNegativeInfinity());
			if (f.isPresent()) {
				if (f.isAST0()) {
					return F.CNInfinity;
				}
				list = f;
				evaled = true;
			}
			if (!evaled) {
				evaled = flattenedList;
			}
			IExpr max1;
			IExpr max2;
			max1 = list.arg1();

			IExpr.COMPARE_TERNARY comp;
			f = list.copyHead();
			for (int i = 2; i < list.size(); i++) {
				max2 = list.get(i);
				if (max1.equals(max2)) {
					continue;
				}
				comp = BooleanFunctions.CONST_LESS.prepareCompare(max1, max2);

				if (comp == IExpr.COMPARE_TERNARY.TRUE) {
					max1 = max2;
					evaled = true;
				} else if (comp == IExpr.COMPARE_TERNARY.FALSE) {
					evaled = true;
				} else {
					if (comp == IExpr.COMPARE_TERNARY.UNDEFINED) {
						// undetermined
						if (max1.isNumber()) {
							f.append(max2);
						} else {
							f.append(max1);
							max1 = max2;
						}
					}
				}
			}
			if (f.size() > 1) {
				f.append(max1);
				if (!evaled) {
					return F.NIL;
				}
				return f;
			} else {
				return max1;
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * Min(e_1, e_2, ..., e_i)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the expression with the lowest value among the <code>e_i</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * Minimum of a series of numbers:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Max(4, -8, 1)
	 * -8
	 * </pre>
	 * <p>
	 * <code>Min</code> flattens lists in its arguments:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Min({1,2},3,{-3,3.5,-Infinity},{{1/2}})
	 * -Infinity
	 * </pre>
	 * <p>
	 * <code>Min</code> with symbolic arguments remains in symbolic form:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Min(x, y)
	 * Min(x,y)
	 * 
	 * &gt;&gt; Min(5, x, -3, y, 40)
	 * Min(-3,x,y)
	 * </pre>
	 * <p>
	 * With no arguments, <code>Min</code> gives <code>Infinity</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Min()
	 * Infinity
	 * 
	 * &gt;&gt; Min(x)
	 * x
	 * </pre>
	 */
	private static class Min extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 1);

			if (ast.isAST0()) {
				return F.CInfinity;
			}

			if (ast.arg1().isInterval1()) {
				IAST list = (IAST) ast.arg1().first();
				try {
					return list.arg1();
				} catch (ClassCastException cca) {
					// do nothing
				}
			}

			IAST resultList = EvalAttributes.flatten(F.List, ast);
			if (resultList.isPresent()) {
				return minimum(resultList, true);
			}

			return minimum(ast, false);
		}

		private IExpr minimum(IAST list, final boolean flattenedList) {
			boolean evaled = false;
			IASTAppendable f = Lambda.remove(list, x -> x.isInfinity());
			if (f.isPresent()) {
				if (f.isAST0()) {
					return F.CNInfinity;
				}
				list = f;
				evaled = true;
			}
			if (!evaled) {
				evaled = flattenedList;
			}

			IExpr min1;
			IExpr min2;
			min1 = list.arg1();
			f = list.copyHead();
			IExpr.COMPARE_TERNARY comp;
			for (int i = 2; i < list.size(); i++) {
				min2 = list.get(i);
				if (min2.isInfinity()) {
					evaled = true;
					continue;
				}

				if (min1.equals(min2)) {
					continue;
				}
				comp = BooleanFunctions.CONST_GREATER.prepareCompare(min1, min2);

				if (comp == IExpr.COMPARE_TERNARY.TRUE) {
					min1 = min2;
					evaled = true;
				} else if (comp == IExpr.COMPARE_TERNARY.FALSE) {
					evaled = true;
				} else {
					if (comp == IExpr.COMPARE_TERNARY.UNDEFINED) {
						// undetermined
						if (min1.isNumber()) {
							f.append(min2);
						} else {
							f.append(min1);
							min1 = min2;
						}
					}
				}
			}
			if (f.size() > 1) {
				f.append(1, min1);
				if (!evaled) {
					return F.NIL;
				}
				return f;
			} else {
				return min1;
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.NUMERICFUNCTION);
		}
	}

	/**
	 * <pre>
	 * Nand(arg1, arg2, ...)'
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Logical NAND function. It evaluates its arguments in order, giving <code>True</code> immediately if any of them
	 * are <code>False</code>, and <code>False</code> if they are all <code>True</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Nand(True, True, True)
	 * False
	 * 
	 * &gt;&gt; Nand(True, False, a)
	 * True
	 * </pre>
	 */
	private final static class Nand extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return F.False;
			}
			if (ast.isAST1()) {
				return F.Not(ast.arg1());
			}
			IASTAppendable result = ast.copyHead();
			boolean evaled = false;

			for (int i = 1; i < ast.size(); i++) {
				IExpr temp = engine.evaluate(ast.get(i));
				if (temp.isFalse()) {
					return F.True;
				} else if (temp.isTrue()) {
					evaled = true;
				} else {
					result.append(temp);
				}
			}
			if (evaled) {
				if (result.isAST0()) {
					return F.False;
				}
				if (result.isAST1()) {
					return F.Not(result.arg1());
				}
				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * Negative(x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>x</code> is a negative real number.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Negative(0)
	 * False
	 * 
	 * &gt;&gt; Negative(-3)
	 * True
	 * 
	 * &gt;&gt; Negative(10/7)
	 * False
	 * 
	 * &gt;&gt; Negative(1+2*I)
	 * False
	 * 
	 * &gt;&gt; Negative(a + b)
	 * Negative(a+b)
	 * 
	 * &gt;&gt; Negative(-E)
	 * True
	 * 
	 * &gt;&gt; Negative(Sin({11, 14}))
	 * {True, False}
	 * </pre>
	 */
	private static class Negative extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			IExpr arg1 = ast.arg1();
			if (arg1.isReal()) {
				return F.bool(arg1.isNegative());
			}
			if (arg1.isNumber()) {
				return F.False;
			}
			ISignedNumber signedNumber = arg1.evalReal();
			if (signedNumber != null) {
				return F.bool(signedNumber.isNegative());
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * NoneTrue({expr1, expr2, ...}, test)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if no application of <code>test</code> to <code>expr1, expr2, ...</code> evaluates to
	 * <code>True</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * NoneTrue(list, test, level)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if no application of <code>test</code> to items of <code>list</code> at
	 * <code>level</code> evaluates to <code>True</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * NoneTrue(test)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives an operator that may be applied to expressions.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; NoneTrue({1, 3, 5}, EvenQ)
	 * True
	 * &gt;&gt; NoneTrue({1, 4, 5}, EvenQ)
	 * False
	 * &gt;&gt; NoneTrue({}, EvenQ)
	 * True
	 * </pre>
	 */
	private static class NoneTrue extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				IExpr head = ast.arg2();
				return noneTrue(list, head, engine);
			}
			return F.NIL;
		}

		/**
		 * If any expression evaluates to <code>true</code> for a given unary predicate function return
		 * <code>False</code>, if all are <code>false</code> return <code>True</code>, else return an
		 * <code>Nor(...)</code> expression of the result expressions.
		 * 
		 * @param list
		 *            list of expressions
		 * @param head
		 *            the head of a unary predicate function
		 * @param engine
		 * @return
		 */
		public IExpr noneTrue(IAST list, IExpr head, EvalEngine engine) {
			IASTAppendable logicalNor = F.ast(F.Nor);
			if (list.exists(x -> noneTrueArgument(x, head, logicalNor, engine))) {
				return F.False;
			}
			return logicalNor.isAST0() ? F.True : logicalNor;
		}

		private static boolean noneTrueArgument(IExpr x, IExpr head, IASTAppendable resultCollector,
				EvalEngine engine) {
			IExpr temp = engine.evaluate(F.unaryAST1(head, x));
			if (temp.isTrue()) {
				return true;
			} else if (!temp.isFalse()) {
				resultCollector.append(temp);
			}
			return false;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * NonNegative(x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>x</code> is a positive real number or zero.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; {Positive(0), NonNegative(0)}
	 * {False,True}
	 * </pre>
	 */
	private final static class NonNegative extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isReal()) {
				return F.bool(!arg1.isNegative());
			}
			if (arg1.isNumber()) {
				return F.False;
			}
			ISignedNumber signedNumber = arg1.evalReal();
			if (signedNumber != null) {
				return F.bool(!signedNumber.isNegative());
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * NonPositive(x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>x</code> is a negative real number or zero.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; {Negative(0), NonPositive(0)}
	 * {False,True}
	 * </pre>
	 */
	private final static class NonPositive extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isReal()) {
				return F.bool(!arg1.isPositive());
			}
			if (arg1.isNumber()) {
				return F.False;
			}
			ISignedNumber signedNumber = arg1.evalReal();
			if (signedNumber != null) {
				return F.bool(!signedNumber.isPositive());
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * Nor(arg1, arg2, ...)'
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Logical NOR function. It evaluates its arguments in order, giving <code>False</code> immediately if any of them
	 * are <code>True</code>, and <code>True</code> if they are all <code>False</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Nor(False, False, False)
	 * True
	 * 
	 * &gt;&gt; Nor(False, True, a)
	 * False
	 * </pre>
	 */
	private static class Nor extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return F.True;
			}
			if (ast.isAST1()) {
				return F.Not(ast.arg1());
			}
			IASTAppendable result = ast.copyHead();
			boolean evaled = false;

			for (int i = 1; i < ast.size(); i++) {
				IExpr temp = engine.evaluate(ast.get(i));
				if (temp.isTrue()) {
					return F.False;
				} else if (temp.isFalse()) {
					evaled = true;
				} else {
					result.append(temp);
				}
			}
			if (evaled) {
				if (result.isAST0()) {
					return F.True;
				}
				if (result.isAST1()) {
					return F.Not(result.arg1());
				}
				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * Not(expr)
	 * </pre>
	 * <p>
	 * or
	 * </p>
	 * 
	 * <pre>
	 * !expr
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Logical Not function (negation). Returns <code>True</code> if the statement is <code>False</code>. Returns
	 * <code>False</code> if the <code>expr</code> is <code>True</code>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; !True
	 * False
	 * &gt;&gt; !False
	 * True
	 * &gt;&gt; !b
	 * !b
	 * </pre>
	 */
	private static class Not extends AbstractArg1 {

		@Override
		public IExpr e1ObjArg(final IExpr o) {
			if (o.isTrue()) {
				return F.False;
			}
			if (o.isFalse()) {
				return F.True;
			}
			if (o.isAST()) {
				IAST temp = (IAST) o;
				if (o.isNot()) {
					return o.first();
				}
				if (temp.isAST2()) {
					int functionID = temp.headID();
					if (functionID > ID.UNKNOWN) {
						switch (functionID) {
						case ID.Exists:
							return F.ForAll(temp.first(), F.Not(temp.second()));
						case ID.ForAll:
							return F.Exists(temp.first(), F.Not(temp.second()));
						case ID.Equal:
							return temp.apply(F.Unequal);
						case ID.Unequal:
							return temp.apply(F.Equal);
						case ID.Greater:
							return temp.apply(F.LessEqual);
						case ID.GreaterEqual:
							return temp.apply(F.Less);
						case ID.Less:
							return temp.apply(F.GreaterEqual);
						case ID.LessEqual:
							return temp.apply(F.Greater);
						default:
							break;
						}
					}
				}
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Or(expr1, expr2, ...)'
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * <code>expr1 || expr2 || ...</code> evaluates each expression in turn, returning <code>True</code> as soon as an
	 * expression evaluates to <code>True</code>. If all expressions evaluate to <code>False</code>, <code>Or</code>
	 * returns <code>False</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; False || True
	 * True
	 * </pre>
	 * <p>
	 * If an expression does not evaluate to <code>True</code> or <code>False</code>, <code>Or</code> returns a result
	 * in symbolic form:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a || False || b
	 * a || b
	 * </pre>
	 */
	private static class Or extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return F.False;
			}

			boolean evaled = false;
			IAST flattenedAST = EvalAttributes.flatten(ast);
			if (flattenedAST.isPresent()) {
				evaled = true;
			} else {
				flattenedAST = ast;
			}

			IASTAppendable result = flattenedAST.copyAppendable();
			IExpr temp;
			IExpr sym;
			int[] symbols = new int[flattenedAST.size()];
			int[] notSymbols = new int[flattenedAST.size()];
			int index = 1;

			for (int i = 1; i < flattenedAST.size(); i++) {
				temp = flattenedAST.get(i);
				if (temp.isTrue()) {
					return F.True;
				}
				if (temp.isFalse()) {
					result.remove(index);
					evaled = true;
					continue;
				}

				temp = engine.evaluateNull(flattenedAST.get(i));
				if (temp.isPresent()) {
					if (temp.isTrue()) {
						return F.True;
					}
					if (temp.isFalse()) {
						result.remove(index);
						evaled = true;
						continue;
					}
					result.set(index, temp);
					evaled = true;
				} else {
					temp = flattenedAST.get(i);
				}

				if (temp.isSymbol()) {
					symbols[i] = flattenedAST.get(i).hashCode();
				} else if (temp.isNot()) {
					sym = temp.first();
					if (sym.isSymbol()) {
						notSymbols[i] = sym.hashCode();
					}
				}
				index++;
			}
			for (int i = 1; i < symbols.length; i++) {
				if (symbols[i] != 0) {
					for (int j = 1; j < notSymbols.length; j++) {
						if (i != j && symbols[i] == notSymbols[j] && (result.equalsAt(i, result.get(j).first()))) {
							// Or[a, Not[a]] => True
							return F.True;
						}
					}
				}
			}
			if (result.isAST1()) {
				return result.arg1();
			}
			if (evaled) {
				if (result.isAST0()) {
					return F.False;
				}
				return result;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ONEIDENTITY | ISymbol.FLAT);
		}
	}

	/**
	 * <pre>
	 * Positive(x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>x</code> is a positive real number.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Positive(1)
	 * True
	 * </pre>
	 * <p>
	 * <code>Positive</code> returns <code>False</code> if <code>x</code> is zero or a complex number:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Positive(0)
	 * False
	 * 
	 * &gt;&gt; Positive(1 + 2 I)
	 * False
	 * 
	 * &gt;&gt; Positive(Pi)
	 * True
	 * 
	 * &gt;&gt; Positive(x)
	 * Positive(x)
	 * 
	 * &gt;&gt; Positive(Sin({11, 14}))
	 * {False, True}
	 * </pre>
	 */
	private final static class Positive extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IExpr arg1 = ast.arg1();
			if (arg1.isNumber()) {
				return F.bool(arg1.isPositive());
			}
			if (arg1.isNumber()) {
				return F.False;
			}
			ISignedNumber signedNumber = arg1.evalReal();
			if (signedNumber != null) {
				return F.bool(signedNumber.isPositive());
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	/**
	 * <pre>
	 * SameQ(x, y)
	 * 
	 * x===y
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>x</code> and <code>y</code> are structurally identical.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * Any object is the same as itself:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a===a
	 * True
	 * </pre>
	 * <p>
	 * Unlike <code>Equal</code>, <code>SameQ</code> only yields <code>True</code> if <code>x</code> and <code>y</code>
	 * have the same type:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {1==1., 1===1.}
	 * {True,False}
	 * </pre>
	 */
	private final static class SameQ extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1) {
				IAST temp = engine.evalArgs(ast, ISymbol.NOATTRIBUTE).orElse(ast);
				return F.bool(!temp.existsLeft((x, y) -> !x.isSame(y)));
			}
			return F.False;
		}

	}

	/**
	 * <pre>
	 * SatisfiabilityCount(boolean-expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * test whether the <code>boolean-expr</code> is satisfiable by a combination of boolean <code>False</code> and
	 * <code>True</code> values for the variables of the boolean expression and return the number of possible
	 * combinations.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * SatisfiabilityCount(boolean-expr, list-of-variables)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * test whether the <code>boolean-expr</code> is satisfiable by a combination of boolean <code>False</code> and
	 * <code>True</code> values for the <code>list-of-variables</code> and return the number of possible combinations.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; SatisfiabilityCount((a || b) &amp;&amp; (! a || ! b), {a, b})
	 * 2
	 * </pre>
	 */
	private final static class SatisfiabilityCount extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 4);

			IAST userDefinedVariables;
			IExpr arg1 = ast.arg1();
			try {
				// currently only SAT is available
				String method = "SAT";
				if (ast.size() > 2) {
					userDefinedVariables = ast.arg2().orNewList();
					if (ast.size() > 3) {
						final Options options = new Options(ast.topHead(), ast, 3, engine);
						// "BDD" (binary decision diagram), "SAT", "TREE" ?
						IExpr optionMethod = options.getOption("Method");
						if (optionMethod.isString()) {
							method = optionMethod.toString();
						}
					}
				} else {
					VariablesSet vSet = new VariablesSet(arg1);
					userDefinedVariables = vSet.getVarList();
				}
				return logicNGSatisfiabilityCount(arg1, userDefinedVariables);
			} catch (ClassCastException cce) {
				if (Config.DEBUG) {
					cce.printStackTrace();
				}
			}
			return F.NIL;
		}

		/**
		 * Use LogicNG MiniSAT method.
		 * 
		 * @param booleanExpression
		 * @param variables
		 *            a list of variables
		 * @param maxChoices
		 *            maximum number of choices, which satisfy the given boolean expression
		 * @return
		 */
		private static IInteger logicNGSatisfiabilityCount(IExpr booleanExpression, IAST variables) {
			FormulaFactory factory = new FormulaFactory();
			LogicFormula lf = new LogicFormula(factory);
			final Formula formula = lf.expr2BooleanFunction(booleanExpression);
			final SATSolver miniSat = MiniSat.miniSat(factory);
			miniSat.add(formula);
			Variable[] vars = lf.ast2Variable(variables);
			List<Assignment> assignments = miniSat.enumerateAllModels(vars);
			return F.integer(assignments.size());
		}
	}

	/**
	 * <pre>
	 * SatisfiabilityInstances(boolean-expr, list-of-variables)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * test whether the <code>boolean-expr</code> is satisfiable by a combination of boolean <code>False</code> and
	 * <code>True</code> values for the <code>list-of-variables</code> and return exactly one instance of
	 * <code>True, False</code> combinations if possible.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * SatisfiabilityInstances(boolean-expr, list-of-variables, combinations)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * test whether the <code>boolean-expr</code> is satisfiable by a combination of boolean <code>False</code> and
	 * <code>True</code> values for the <code>list-of-variables</code> and return up to <code>combinations</code>
	 * instances of <code>True, False</code> combinations if possible.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; SatisfiabilityInstances((a || b) &amp;&amp; (! a || ! b), {a, b}, All)
	 * {{False,True},{True,False}}
	 * </pre>
	 */
	private final static class SatisfiabilityInstances extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 4);

			IAST userDefinedVariables;
			IExpr arg1 = ast.arg1();
			try {
				// currently only SAT is available
				String method = "SAT";
				int maxChoices = 1;
				if (ast.size() > 2) {
					userDefinedVariables = ast.arg2().orNewList();
					if (ast.size() > 3) {
						final Options options = new Options(ast.topHead(), ast, 3, engine);
						// "BDD" (binary decision diagram), "SAT", "TREE" ?
						IExpr optionMethod = options.getOption("Method");
						if (optionMethod.isString()) {
							method = optionMethod.toString();
						}
					}

					IExpr argN = ast.last();
					if (argN.equals(F.All)) {
						maxChoices = Integer.MAX_VALUE;
					} else if (argN.isReal()) {
						ISignedNumber sn = (ISignedNumber) argN;
						maxChoices = sn.toIntDefault(0);
					}
				} else {
					VariablesSet vSet = new VariablesSet(arg1);
					userDefinedVariables = vSet.getVarList();
				}
				return satisfiabilityInstances(arg1, userDefinedVariables, maxChoices);
			} catch (ClassCastException cce) {
				if (Config.DEBUG) {
					cce.printStackTrace();
				}
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * SatisfiableQ(boolean-expr, list-of-variables)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * test whether the <code>boolean-expr</code> is satisfiable by a combination of boolean <code>False</code> and
	 * <code>True</code> values for the <code>list-of-variables</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; SatisfiableQ((a || b) &amp;&amp; (! a || ! b), {a, b})
	 * True
	 * </pre>
	 */
	private final static class SatisfiableQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 4);

			IASTMutable userDefinedVariables;
			IExpr arg1 = ast.arg1();
			try {
				// currently only SAT is available
				String method = "SAT";
				if (ast.size() > 2) {
					if (ast.arg2().isList()) {
						userDefinedVariables = ((IAST) ast.arg2()).copy();
						EvalAttributes.sort(userDefinedVariables);
					} else {
						userDefinedVariables = F.ListAlloc(ast.arg2());
					}
					if (ast.size() > 3) {
						final Options options = new Options(ast.topHead(), ast, 3, engine);
						// "BDD" (binary decision diagram), "SAT", "TREE" ?
						IExpr optionMethod = options.getOption("Method");
						if (optionMethod.isString()) {
							method = optionMethod.toString();
						}
					}
					VariablesSet vSet = new VariablesSet(arg1);
					IAST variables = vSet.getVarList();
					if (variables.equals(userDefinedVariables)) {
						return logicNGSatisfiableQ(arg1);
					}

				} else {
					return logicNGSatisfiableQ(arg1);
				}
				return bruteForceSatisfiableQ(arg1, userDefinedVariables, 1) ? F.True : F.False;
			} catch (ClassCastException cce) {
				if (Config.DEBUG) {
					cce.printStackTrace();
				}
			}
			return F.NIL;
		}

		/**
		 * Use LogicNG MiniSAT method.
		 * 
		 * @param arg1
		 * @return
		 */
		private static IExpr logicNGSatisfiableQ(IExpr arg1) {
			FormulaFactory factory = new FormulaFactory();
			LogicFormula lf = new LogicFormula(factory);
			final Formula formula = lf.expr2BooleanFunction(arg1);
			final SATSolver miniSat = MiniSat.miniSat(factory);
			miniSat.add(formula);
			final Tristate result = miniSat.sat();
			if (result == Tristate.TRUE) {
				return F.True;
			}
			if (result == Tristate.FALSE) {
				return F.False;
			}
			return F.NIL;
		}

		/**
		 * Use brute force method.
		 * 
		 * @param expr
		 * @param variables
		 * @param position
		 * @return
		 */
		private static boolean bruteForceSatisfiableQ(IExpr expr, IAST variables, int position) {
			if (variables.size() <= position) {
				return EvalEngine.get().evalTrue(expr);
			}
			IExpr sym = variables.get(position);
			if (sym.isSymbol()) {
				try {
					((ISymbol) sym).pushLocalVariable(F.True);
					if (bruteForceSatisfiableQ(expr, variables, position + 1)) {
						return true;
					}
				} finally {
					((ISymbol) sym).popLocalVariable();
				}
				try {
					((ISymbol) sym).pushLocalVariable(F.False);
					if (bruteForceSatisfiableQ(expr, variables, position + 1)) {
						return true;
					}
				} finally {
					((ISymbol) sym).popLocalVariable();
				}
			}
			return false;
		}
	}

	/**
	 * <pre>
	 * TautologyQ(boolean-expr, list-of-variables)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * test whether the <code>boolean-expr</code> is satisfiable by all combinations of boolean <code>False</code> and
	 * <code>True</code> values for the <code>list-of-variables</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Tautology_(logic)">Wikipedia - Tautology (logic)</a></li>
	 * </ul>
	 */
	private static class TautologyQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IASTMutable userDefinedVariables;
			IExpr arg1 = ast.arg1();
			try {
				if (ast.isAST2()) {
					if (ast.arg2().isList()) {
						userDefinedVariables = ((IAST) ast.arg2()).copy();
						EvalAttributes.sort(userDefinedVariables);
					} else {
						userDefinedVariables = F.ListAlloc(ast.arg2());
					}
					VariablesSet vSet = new VariablesSet(arg1);
					IAST variables = vSet.getVarList();
					if (variables.equals(userDefinedVariables)) {
						return logicNGTautologyQ(arg1);
					}
				} else {
					return logicNGTautologyQ(arg1);
				}

				return bruteForceTautologyQ(arg1, userDefinedVariables, 1) ? F.True : F.False;
			} catch (ClassCastException cce) {
				if (Config.DEBUG) {
					cce.printStackTrace();
				}
			}
			return F.NIL;
		}

		/**
		 * <p>
		 * Use LogicNG MiniSAT method.
		 * </p>
		 * <p>
		 * <b>Note:</b> <code>TautologyQ(formula)</code> is equivalent to <code>!SatisfiableQ(!formula)</code>.
		 * </p>
		 * 
		 * @param arg1
		 * @return
		 */
		private static IExpr logicNGTautologyQ(IExpr arg1) {
			IExpr temp = SatisfiableQ.logicNGSatisfiableQ(F.Not(arg1));
			if (temp.isPresent()) {
				return temp.isTrue() ? F.False : F.True;
			}
			return F.NIL;
		}

		/**
		 * Use brute force method.
		 * 
		 * @param expr
		 * @param variables
		 * @param position
		 * @return
		 */
		private static boolean bruteForceTautologyQ(IExpr expr, IAST variables, int position) {
			if (variables.size() <= position) {
				return EvalEngine.get().evalTrue(expr);
			}
			IExpr sym = variables.get(position);
			if (sym.isSymbol()) {
				try {
					((ISymbol) sym).pushLocalVariable(F.True);
					if (!bruteForceTautologyQ(expr, variables, position + 1)) {
						return false;
					}
				} finally {
					((ISymbol) sym).popLocalVariable();
				}
				try {
					((ISymbol) sym).pushLocalVariable(F.False);
					if (!bruteForceTautologyQ(expr, variables, position + 1)) {
						return false;
					}
				} finally {
					((ISymbol) sym).popLocalVariable();
				}
			}
			return true;
		}
	}

	/**
	 * <pre>
	 * TrueQ(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if and only if <code>expr</code> is <code>True</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; TrueQ(True)
	 * True
	 * &gt;&gt; TrueQ(False)
	 * False
	 * &gt;&gt; TrueQ(a)
	 * False
	 * </pre>
	 */
	private static class TrueQ extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			return F.bool(ast.equalsAt(1, F.True));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <pre>
	 * Unequal(x, y) 
	 * 
	 * x != y
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * yields <code>False</code> if <code>x</code> and <code>y</code> are known to be equal, or <code>True</code> if
	 * <code>x</code> and <code>y</code> are known to be unequal.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * lhs != rhs
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents the inequality <code>lhs &lt;&gt; rhs</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; 1 != 1
	 * False
	 * </pre>
	 * <p>
	 * Lists are compared based on their elements:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {1} != {2}
	 * True
	 * 
	 * &gt;&gt; {1, 2} != {1, 2}
	 * False
	 * 
	 * &gt;&gt; {a} != {a}
	 * False
	 * 
	 * &gt;&gt; "a" != "b"
	 * True
	 * 
	 * &gt;&gt; "a" != "a"
	 * False
	 * 
	 * &gt;&gt; Pi != N(Pi)
	 * False
	 * 
	 * &gt;&gt; a_ != b_
	 * a_ != b_
	 * 
	 * &gt;&gt; a != a != a
	 * False
	 * 
	 * &gt;&gt; "abc" != "def" != "abc"
	 * False
	 * 
	 * &gt;&gt; a != a != b
	 * False
	 * 
	 * &gt;&gt; a != b != a
	 * a != b != a
	 * 
	 * &gt;&gt; {Unequal(), Unequal(x), Unequal(1)}
	 * {True, True, True}
	 * </pre>
	 */
	private final static class Unequal extends Equal {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 2) {
				IExpr.COMPARE_TERNARY b = IExpr.COMPARE_TERNARY.UNDEFINED;
				if (ast.isAST2()) {
					return unequalNull(ast.arg1(), ast.arg2(), engine);
				}

				IASTMutable result = ast.copy();
				result.setArgs(result.size(), i -> F.expandAll(result.get(i), true, true));
				int i = 2;
				int j;
				while (i < result.size()) {
					j = i;
					while (j < result.size()) {
						b = compareTernary(result.get(i - 1), result.get(j++));
						if (b == IExpr.COMPARE_TERNARY.TRUE) {
							return F.False;
						} else if (b == IExpr.COMPARE_TERNARY.UNDEFINED) {
							return F.NIL;
						}
					}
					i++;
				}
			}
			return F.True;
		}

	}

	/**
	 * <pre>
	 * UnsameQ(x, y)
	 * 
	 * x=!=y
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>True</code> if <code>x</code> and <code>y</code> are not structurally identical.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * Any object is the same as itself:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a=!=a
	 *  = False
	 * </pre>
	 * 
	 * <pre>
	 * ```
	 * &gt;&gt; 1=!=1
	 * True
	 * </pre>
	 */
	private final static class UnsameQ extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1) {
				IAST temp = engine.evalArgs(ast, ISymbol.NOATTRIBUTE).orElse(ast);
				if (ast.isAST2()) {
					return F.bool(!temp.arg1().isSame(temp.arg2()));
				}
				int i = 2;
				int j;
				while (i < temp.size()) {
					j = i;
					while (j < temp.size()) {
						if (temp.get(i - 1).isSame(temp.get(j++))) {
							return F.False;
						}
					}
					i++;
				}
				return F.True;

			}
			return F.False;
		}

	}

	/**
	 * <pre>
	 * Xor(arg1, arg2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Logical XOR (exclusive OR) function. Returns <code>True</code> if an odd number of the arguments are
	 * <code>True</code> and the rest are <code>False</code>. Returns <code>False</code> if an even number of the
	 * arguments are <code>True</code> and the rest are <code>False</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See: <a href="https://en.wikipedia.org/wiki/Exclusive_or">Wikipedia: Exclusive or</a>
	 * </p>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Xor(False, True)
	 * True
	 * &gt;&gt; Xor(True, True)
	 * False
	 * </pre>
	 * <p>
	 * If an expression does not evaluate to <code>True</code> or <code>False</code>, <code>Xor</code> returns a result
	 * in symbolic form:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Xor(a, False, b)
	 * Xor(a,b)
	 * &gt;&gt; Xor()
	 * False
	 * &gt;&gt; Xor(a)
	 * a
	 * &gt;&gt; Xor(False)
	 * False
	 * &gt;&gt; Xor(True)
	 * True
	 * &gt;&gt; Xor(a, b)
	 * Xor(a,b)
	 * </pre>
	 */
	private static class Xor extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 1) {
				return F.False;
			}
			if (ast.size() == 2) {
				return ast.arg1();
			}

			IExpr temp;
			IExpr result = ast.arg1();
			int size = ast.size();
			IASTAppendable xor = F.ast(F.Xor, size - 1, false);
			boolean evaled = false;
			for (int i = 2; i < size; i++) {
				temp = ast.get(i);
				if (temp.isTrue()) {
					if (result.isTrue()) {
						result = F.False;
					} else if (result.isFalse()) {
						result = F.True;
					} else {
						result = F.Not.of(engine, result);
					}
					evaled = true;
				} else if (temp.isFalse()) {
					if (result.isTrue()) {
						result = F.True;
					} else if (result.isFalse()) {
						result = F.False;
					}
					evaled = true;
				} else {
					if (temp.equals(result)) {
						result = F.False;
						evaled = true;
					} else {
						if (result.isTrue()) {
							result = F.Not.of(engine, temp);
							evaled = true;
						} else if (result.isFalse()) {
							result = temp;
							evaled = true;
						} else {
							xor.append(temp);
						}
					}
				}
			}
			if (evaled) {
				xor.append(result);
				return xor;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.ONEIDENTITY | ISymbol.FLAT);
		}

	}

	/**
	 * Compare if the first and second argument are equal after expanding the arguments.
	 * 
	 * @param a1
	 *            first argument
	 * @param a2
	 *            second argument
	 * @return <code>F.NIL</code> or the simplified expression, if equality couldn't be determined.
	 */
	public static IExpr equalNull(final IExpr a1, final IExpr a2, EvalEngine engine) {
		IExpr.COMPARE_TERNARY b;
		IExpr arg1 = F.expandAll(a1, true, true);
		IExpr arg2 = F.expandAll(a2, true, true);

		b = CONST_EQUAL.prepareCompare(arg1, arg2, engine);
		if (b == IExpr.COMPARE_TERNARY.FALSE) {
			return F.False;
		}
		if (b == IExpr.COMPARE_TERNARY.TRUE) {
			return F.True;
		}

		return Equal.simplifyCompare(F.Equal, arg1, arg2);
	}

	/**
	 * Compare if the first and second argument are unequal after expanding the arguments.
	 * 
	 * @param a1
	 *            first argument
	 * @param a2
	 *            second argument
	 * @return <code>F.NIL</code> or the simplified expression, if equality couldn't be determined.
	 */
	public static IExpr unequalNull(IExpr a1, IExpr a2, EvalEngine engine) {
		IExpr.COMPARE_TERNARY b;
		IExpr arg1 = F.expandAll(a1, true, true);
		IExpr arg2 = F.expandAll(a2, true, true);
		b = CONST_EQUAL.prepareCompare(arg1, arg2, engine);
		if (b == IExpr.COMPARE_TERNARY.FALSE) {
			return F.True;
		}
		if (b == IExpr.COMPARE_TERNARY.TRUE) {
			return F.False;
		}

		return Unequal.simplifyCompare(F.Unequal, arg1, arg2);
	}

	/**
	 * Transform <code>Inequality()</code> AST to <code>And()</code> expression.
	 * 
	 * @param ast
	 *            an Inequality AST with <code>size()>=4</code>.
	 * @return
	 */
	public static IAST inequality2And(final IAST ast) {
		IASTAppendable result = F.And();
		for (int i = 3; i < ast.size(); i += 2) {
			result.append(F.binaryAST2(ast.get(i - 1), ast.get(i - 2), ast.get(i)));
		}
		return result;
	}

	public static IExpr equals(final IAST ast) {
		return equalNull(ast.arg1(), ast.arg2(), EvalEngine.get()).orElse(ast);
	}

	/**
	 * Use LogicNG MiniSAT method.
	 * 
	 * Example: Create a list of rules in the form <code>{{False,True,False,False},{True,False,False,False},...}</code>
	 * for the variables <code>{a,b,c,d}</code>
	 * 
	 * @param booleanExpression
	 *            an expression build from symbols and boolean operators like
	 *            <code>And, Or, Not, Xor, Nand, Nor, Implies, Equivalent,...</code>
	 * @param variables
	 *            the possible variables. Example: <code>{a,b,c,d}</code>
	 * @param maxChoices
	 *            maximum number of choices, which satisfy the given boolean expression
	 * @return
	 */
	public static IAST satisfiabilityInstances(IExpr booleanExpression, IAST variables, int maxChoices) {
		LogicFormula lf = new LogicFormula();
		Variable[] vars = lf.ast2Variable(variables);
		List<Assignment> assignments = logicNGSatisfiabilityInstances(booleanExpression, vars, lf, maxChoices);
		Map<String, Integer> map = LogicFormula.name2Position(vars);
		IASTAppendable list = F.ListAlloc(assignments.size());
		for (int i = 0; i < assignments.size(); i++) {
			if (i >= maxChoices) {
				break;
			}
			list.append( //
					lf.literals2BooleanList(assignments.get(i).literals(), map) //
			);
		}
		return list;
	}

	/**
	 * Example: Create a list of rules in the form
	 * <code>{{a->False,b->True,c->False,d->False},{a->True,b->False,c->False,d->False},...}</code> for the variables
	 * <code>{a,b,c,d}</code>
	 * 
	 * @param booleanExpression
	 *            an expression build from symbols and boolean operators like
	 *            <code>And, Or, Not, Xor, Nand, Nor, Implies, Equivalent,...</code>
	 * @param variables
	 *            the possible variables. Example: <code>{a,b,c,d}</code>
	 * @param maxChoices
	 * @return
	 */
	public static IAST solveInstances(IExpr booleanExpression, IAST variables, int maxChoices) {
		LogicFormula lf = new LogicFormula();
		Variable[] vars = lf.ast2Variable(variables);
		List<Assignment> assignments = logicNGSatisfiabilityInstances(booleanExpression, vars, lf, maxChoices);
		Map<String, Integer> map = LogicFormula.name2Position(vars);
		IASTAppendable list = F.ListAlloc(assignments.size());
		for (int i = 0; i < assignments.size(); i++) {
			if (i >= maxChoices) {
				break;
			}
			list.append( //
					lf.literals2VariableList(assignments.get(i).literals(), map) //
			);
		}
		return list;
	}

	public static List<Assignment> logicNGSatisfiabilityInstances(IExpr booleanExpression, Variable[] vars,
			LogicFormula lf, int maxChoices) {

		final Formula formula = lf.expr2BooleanFunction(booleanExpression);
		final SATSolver miniSat = MiniSat.miniSat(lf.getFactory());
		miniSat.add(formula);
		return miniSat.enumerateAllModels(vars);
	}

	private final static BooleanFunctions CONST = new BooleanFunctions();

	public static BooleanFunctions initialize() {
		return CONST;
	}

	private BooleanFunctions() {

	}

}
