package org.matheclipse.core.visit;

import java.util.function.Function;
import java.util.function.Predicate;

import org.matheclipse.core.expression.F;
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
public class VisitorReplaceAllLambda extends VisitorExpr {
	final Predicate<IExpr> fPredicate;
	final Function<IExpr, IExpr> fFunction;
	final int fOffset;

	public VisitorReplaceAllLambda(Predicate<IExpr> predicate, Function<IExpr, IExpr> function) {
		this(predicate, function, 0);
	}

	public VisitorReplaceAllLambda(Predicate<IExpr> predicate, Function<IExpr, IExpr> function, int offset) {
		super();
		this.fPredicate = predicate;
		this.fFunction = function;
		this.fOffset = offset;
	}

	@Override
	public IExpr visit(IInteger element) {
		return fPredicate.test(element) ? fFunction.apply(element) : F.NIL;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IFraction element) {
		return fPredicate.test(element) ? fFunction.apply(element) : F.NIL;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IComplex element) {
		return fPredicate.test(element) ? fFunction.apply(element) : F.NIL;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(INum element) {
		return fPredicate.test(element) ? fFunction.apply(element) : F.NIL;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IComplexNum element) {
		return fPredicate.test(element) ? fFunction.apply(element) : F.NIL;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(ISymbol element) {
		return fPredicate.test(element) ? fFunction.apply(element) : F.NIL;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IPattern element) {
		return fPredicate.test(element) ? fFunction.apply(element) : F.NIL;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IPatternSequence element) {
		return fPredicate.test(element) ? fFunction.apply(element) : F.NIL;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	@Override
	public IExpr visit(IStringX element) {
		return fPredicate.test(element) ? fFunction.apply(element) : F.NIL;
	}

	@Override
	public IExpr visit(IASTMutable ast) {
		if (fPredicate.test(ast)) {
			IExpr temp = fFunction.apply(ast);
			if (temp.isPresent()) {
				return temp;
			}
		}
		return visitAST(ast);
	}

	@Override
	protected IExpr visitAST(IAST ast) {
		IExpr temp;
		IASTMutable result = F.NIL;
		int i = fOffset;
		int size = ast.size();
		while (i < size) {
			temp = ast.get(i).accept(this);
			if (temp.isPresent()) {
				// something was evaluated - return a new IAST:
				result = ast.setAtCopy(i++, temp);
				break;
			}
			i++;
		}
		if (result.isPresent()) {
			while (i < size) {
				temp = ast.get(i).accept(this);
				if (temp.isPresent()) {
					result.set(i, temp);
				}
				i++;
			}
		}
		return result;
	}
}
