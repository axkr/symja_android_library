package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

/**
 * <pre>
 * OptimizeExpression(function)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * common subexpressions elimination for a complicated <code>function</code> by generating &ldquo;dummy&rdquo; variables
 * for these subexpressions.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * &gt;&gt; OptimizeExpression( Sin(x) + Cos(Sin(x)) )
 * {v1+Cos(v1),{v1-&gt;Sin(x)}}
 * 
 * &gt;&gt; OptimizeExpression((3 + 3*a^2 + Sqrt(5 + 6*a + 5*a^2) + a*(4 + Sqrt(5 + 6*a + 5*a^2)))/6)
 * {1/6*(3+3*v1+v2+a*(4+v2)),{v1-&gt;a^2,v2-&gt;Sqrt(5+6*a+5*v1)}}
 * </pre>
 * <p>
 * Create the original expression:
 * </p>
 * 
 * <pre>
 * &gt;&gt; ReplaceRepeated(1/6*(3+3*v1+v2+a*(4+v2)), {v1-&gt;a^2, v2-&gt;Sqrt(5+6*a+5*v1)})
 * 1/6*(3+3*a^2+Sqrt(5+6*a+5*a^2)+a*(4+Sqrt(5+6*a+5*a^2)))
 * </pre>
 */
public class OptimizeExpression extends AbstractFunctionEvaluator {

	private static class ReferenceCounter implements Comparable<ReferenceCounter> {
		IExpr reference;
		int counter;

		public ReferenceCounter(IExpr reference) {
			this.reference = reference;
			counter = 1;
		}

		public void incCounter() {
			++counter;
		}

		@Override
		public int compareTo(ReferenceCounter o) {
			return counter > o.counter ? 1 : counter == o.counter ? 0 : -1;
		}

	}

	private static class ShareFunction implements Function<IExpr, IExpr> {
		java.util.Map<IExpr, ReferenceCounter> map;

		public ShareFunction() {
			map = new HashMap<IExpr, ReferenceCounter>();
		}

		@Override
		public IExpr apply(IExpr t) {
			ReferenceCounter value = map.get(t);
			if (value == null) {
				value = new ReferenceCounter(t);
				map.put(t, value);
				return null;
			} else {
				value.incCounter();
				if (value.reference == t) {
					return null;
				}
			}
			return value.reference;
		}
	}

	/**
	 * Replace all occurrences of expressions where the given <code>function.apply()</code> method returns a non
	 * <code>F.NIL</code> value. The visitors <code>visit()</code> methods return <code>F.NIL</code> if no substitution
	 * occurred.
	 */
	private static class ShareReplaceAll extends VisitorExpr {
		final Function<IExpr, IExpr> fFunction;
		final int fOffset;
		public int fCounter;

		public ShareReplaceAll(Function<IExpr, IExpr> function) {
			this(function, 0);
		}

		public ShareReplaceAll(Function<IExpr, IExpr> function, int offset) {
			super();
			this.fFunction = function;
			this.fOffset = offset;
			fCounter = 0;
		}

		@Override
		public IExpr visit(IInteger element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		@Override
		public IExpr visit(IFraction element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		@Override
		public IExpr visit(IComplex element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		@Override
		public IExpr visit(INum element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		@Override
		public IExpr visit(IComplexNum element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		@Override
		public IExpr visit(ISymbol element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		@Override
		public IExpr visit(IPattern element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		@Override
		public IExpr visit(IPatternSequence element) {
			return null;
		}

		/**
		 * 
		 * @return <code>F.NIL</code>, if no evaluation is possible
		 */
		@Override
		public IExpr visit(IStringX element) {
			return null;
		}

		@Override
		public IExpr visit(IASTMutable ast) {
			IExpr temp = fFunction.apply(ast);
			if (temp != null) {
				return temp;
			}
			return visitAST(ast);
		}

		@Override
		protected IExpr visitAST(IAST ast) {
			IExpr temp;
			boolean evaled = false;
			int i = fOffset;
			while (i < ast.size()) {
				temp = ast.get(i);
				if (temp.isAST()) {
					temp = temp.accept(this);
					if (temp != null) {
						// share the object with the same id:
						((IASTMutable) ast).set(i, temp);
						evaled = true;
						fCounter++;
					}
				}
				i++;
			}
			return evaled ? ast : null;
		}
	}

	public OptimizeExpression() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isAST()) {
			return optimizeExpression((IAST) ast.arg1());
		}
		return F.NIL;
	}

	/**
	 * Try to optimize/extract common sub-<code>IASTs</code> expressions to minimize the number of operations
	 *
	 * @param ast
	 *            the ast whose internal memory consumption should be minimized
	 * @return the number of shared sub-expressions
	 */
	private static IExpr optimizeExpression(final IAST ast) {
		ShareFunction function = new ShareFunction();
		ShareReplaceAll sra = new ShareReplaceAll(function, 1);
		IExpr sharedExpr = ast.accept(sra);
		if (sharedExpr != null) {
			ArrayList<ReferenceCounter> list = new ArrayList<ReferenceCounter>();
			for (Map.Entry<IExpr, ReferenceCounter> entry : function.map.entrySet()) {
				ReferenceCounter rc = entry.getValue();
				if (rc.counter > 1) {
					list.add(rc);
				}
			}

			int varCounter = 1;
			Collections.sort(list, Collections.reverseOrder());
			IASTAppendable variableSubstitutions = F.ListAlloc(list.size());
			IASTAppendable replaceList = F.ListAlloc(list.size());
			for (ReferenceCounter rc : list) {
				IExpr ref = rc.reference;
				IExpr temp = ref.replaceAll(variableSubstitutions).orElse(ref);
				ISymbol dummyVariable = F.Dummy("v" + varCounter);
				replaceList.append(F.Rule(dummyVariable, temp));
				variableSubstitutions.append(F.Rule(ref, dummyVariable));
				varCounter++;
			}
			sharedExpr = sharedExpr.replaceAll(variableSubstitutions);
			if (sharedExpr.isPresent()) {
				return F.List(sharedExpr, replaceList);
			}
		}
		return F.NIL;
	}

}
