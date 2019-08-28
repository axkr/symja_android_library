package org.matheclipse.core.visit;

import java.util.Map;
import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
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

/**
 * Replace all occurrences of expressions where the given <code>function.apply()</code> method returns a non
 * <code>F.NIL</code> value. The visitors <code>visit()</code> methods return <code>F.NIL</code> if no substitution
 * occurred.
 */
public class VisitorReplaceAll extends VisitorExpr {
	final Function<IExpr, IExpr> fFunction;

	Function<IASTMutable, IExpr> fPostProcessing;

	final int fOffset;

	public VisitorReplaceAll(Function<IExpr, IExpr> function) {
		this(function, 0);
	}

	public VisitorReplaceAll(Function<IExpr, IExpr> function, int offset) {
		super();
		this.fFunction = function;
		this.fOffset = offset;
	}

	public VisitorReplaceAll(Map<? extends IExpr, ? extends IExpr> map) {
		this(map, 0);
	}

	public VisitorReplaceAll(Map<? extends IExpr, ? extends IExpr> map, int offset) {
		super();
		this.fFunction = x -> {
			IExpr subst = map.get(x);
			if (subst != null) {
				return subst;
			}
			return F.NIL;
		};
		this.fOffset = offset;
	}

	public VisitorReplaceAll(IAST ast) {
		this(ast, 0);
	}

	public VisitorReplaceAll(IAST ast, int offset) {
		super();
		this.fFunction = Functors.rules(ast, EvalEngine.get());
		this.fOffset = offset;
	}

	@Override
	public IExpr visit(IInteger element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IFraction element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IComplex element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(INum element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IComplexNum element) {
		return fFunction.apply(element);
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(ISymbol element) {
		return fFunction.apply(element);
	}

	@Override
	public IExpr visit(IPattern element) {
		IExpr temp = fFunction.apply(element);
		if (temp.isPresent()) {
			return temp;
		}
		ISymbol symbol = element.getSymbol();
		if (symbol != null) {
			IExpr expr = fFunction.apply(symbol);
			if (expr.isPresent() && expr.isSymbol()) {
				if (element.isPatternDefault()) {
					return F.$p((ISymbol) expr, element.getHeadTest(), true);
				}
				return F.$p((ISymbol) expr, element.getHeadTest());
			}
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(IPatternSequence element) {
		IExpr temp = fFunction.apply(element);
		if (temp.isPresent()) {
			return temp;
		}
		ISymbol symbol = element.getSymbol();
		if (symbol != null) {
			IExpr expr = fFunction.apply(symbol);
			if (expr.isPresent() && expr.isSymbol()) {
				return F.$ps((ISymbol) expr, element.getHeadTest(), element.isDefault(), element.isNullSequence());
			}
		}
		return F.NIL;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IStringX element) {
		return fFunction.apply(element);
	}

	@Override
	public IExpr visit(IASTMutable ast) {
		return fFunction.apply(ast).orElseGet(() -> visitAST(ast));
	}

	@Override
	protected IExpr visitAST(IAST ast) {
		int i = fOffset;
		int size = ast.size();
		while (i < size) {
			IExpr temp = ast.get(i).accept(this);
			if (temp.isPresent()) {
				// something was evaluated - return a new IAST:
				IASTMutable result = ast.setAtCopy(i++, temp);
				while (i < size) {
					temp = ast.get(i).accept(this);
					if (temp.isPresent()) {
						result.set(i, temp);
					}
					i++;
				}
				return postProcessing(result);

			}
			i++;
		}
		return F.NIL;
	}

	private IExpr postProcessing(IASTMutable result) {
		if (fPostProcessing != null) {
			return fPostProcessing.apply(result);
		}
		return result;
	}

	/**
	 * Define an optional post-processing function for the result of a substitution.
	 * 
	 * @param postProcessing
	 */
	public void setPostProcessing(Function<IASTMutable, IExpr> postProcessing) {
		this.fPostProcessing = postProcessing;
	}
}
