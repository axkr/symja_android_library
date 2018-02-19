package org.matheclipse.core.visit;

import com.duy.lambda.Function;
import com.duy.lambda.Predicate;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Replace all occurrences of expressions where the given <code>function.apply()</code> method returns a non
 * <code>F.NIL</code> value. The visitors <code>visit()</code> methods return <code>F.NIL</code> if no substitution
 * occurred.
 */
public class VisitorPredicateReplaceAll extends VisitorReplaceAll {
	final Predicate<IExpr> fPredicate;

	public VisitorPredicateReplaceAll(Function<IExpr, IExpr> function, Predicate<IExpr> predicate) {
		this(function, predicate, 0);
	}

	public VisitorPredicateReplaceAll(Function<IExpr, IExpr> function, Predicate<IExpr> predicate, int offset) {
		super(function, offset);
		this.fPredicate = predicate;
	}

	public VisitorPredicateReplaceAll(IAST ast, Predicate<IExpr> predicate) {
		this(ast, predicate, 0);
	}

	public VisitorPredicateReplaceAll(IAST ast, Predicate<IExpr> predicate, int offset) {
		super(ast, offset);
		this.fPredicate = predicate;
	}

	@Override
	public IExpr visit(IASTMutable ast) {
		if (fPredicate.test(ast)) {
			return visitAST(ast);
		}
		IExpr temp = fFunction.apply(ast);
		if (temp.isPresent()) {
			return temp;
		}
		return F.NIL;
	}

}
