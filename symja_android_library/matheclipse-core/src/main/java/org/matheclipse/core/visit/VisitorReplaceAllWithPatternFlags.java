package org.matheclipse.core.visit;

import java.util.function.Function;
import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Replace all occurrences of expressions where the given <code>function.apply()</code> method returns a non
 * <code>F.NIL</code> value. The visitors <code>visit()</code> methods return <code>F.NIL</code> if no substitution
 * occurred.
 */
public class VisitorReplaceAllWithPatternFlags extends VisitorReplaceAll {
	boolean onlyNamedPatterns;

	public VisitorReplaceAllWithPatternFlags(Function<IExpr, IExpr> function, boolean onlyNamedPatterns) {
		super(function);
		this.onlyNamedPatterns = onlyNamedPatterns;
	}

	@Override
	public IExpr visit(IPattern element) {
		IExpr temp = fFunction.apply(element);
		if (temp.isPresent()) {
			return temp;
		}
//		ISymbol symbol = element.getSymbol();
//		if (symbol != null) {
//			IExpr expr = fFunction.apply(symbol);
//			if (expr.isPresent() && expr.isSymbol()) {
//				if (element.isPatternDefault()) {
//					return F.$p((ISymbol) expr, element.getHeadTest(), true);
//				}
//				return F.$p((ISymbol) expr, element.getHeadTest());
//			}
//		}
		return F.NIL;
	}
	
	@Override
	public IExpr visit(IPatternSequence element) {
		IExpr temp = fFunction.apply(element);
		if (temp.isPresent()) {
			return temp;
		}
//		ISymbol symbol = element.getSymbol();
//		if (symbol != null) {
//			IExpr expr = fFunction.apply(symbol);
//			if (expr.isPresent() && expr.isSymbol()) {
//				return F.$ps((ISymbol) expr, element.getHeadTest(), element.isDefault(), element.isNullSequence());
//			}
//		}
		return F.NIL;
	}
	
	@Override
	public IExpr visit(IASTMutable ast) {
		IExpr result = (IASTMutable) super.visit(ast);
		if (result.isPresent()) {
			if (result.isAST()) {
				if (result.isFlatAST()) {
					IExpr temp = EvalAttributes.flatten((IAST) result);
					if (temp.isPresent()) {
						result = temp;
					}
				}
				if (result.isOneIdentityAST1()) {
					result = result.first();
				} else if (result.isOrderlessAST()) {
					EvalAttributes.sort((IASTMutable) result);
				}
				// if (onlyNamedPatterns) {
				// System.out.println(" " + lhsPatternExpr.toString() + " -> " + result.toString());
				// }
			}
			// if (result instanceof IASTMutable) {
			// ((IASTMutable) result).setEvalFlags(ast.getEvalFlags() & IAST.CONTAINS_PATTERN_EXPR);
			// }
			return result;
		}
		return F.NIL;
	}

}
