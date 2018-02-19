package org.matheclipse.core.eval;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.SymbolNode;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.IDoubleCallbackFunction;
import org.matheclipse.parser.client.math.MathException;

import java.util.function.IntFunction;

/**
 * A call back function which could be used in <code>DoubleEvaluator</code>, for evaluating Symja numerical functions.
 * 
 */
public class CoreCallbackFunction implements IDoubleCallbackFunction {
	public final static CoreCallbackFunction CONST = new CoreCallbackFunction();

	@Override
	public double evaluate(DoubleEvaluator doubleEngine, FunctionNode functionNode, double[] args) {
		ASTNode node = functionNode.getNode(0);
		if (node instanceof SymbolNode) {
			AST2Expr ast2Expr = new AST2Expr();
			IExpr head = ast2Expr.convert(node);
			IASTAppendable fun = F.ast(head, args.length, false);
			fun.appendArgs(0, args.length, new IntFunction<IExpr>() {
                @Override
                public IExpr apply(int i) {
                    return F.num(args[i]);
                }
            });
			// for (int i = 0; i < args.length; i++) {
			// fun.append(F.num(args[i]));
			// }
			final IExpr result = F.evaln(fun);
			if (result.isSignedNumber()) {
				return ((ISignedNumber) result).doubleValue();
			}
		} else if (node instanceof FunctionNode) {
			AST2Expr ast2Expr = new AST2Expr();
			IExpr head = ast2Expr.convert(node);
			IASTAppendable fun = F.ast(head);
			for (int i = 0; i < args.length; i++) {
				fun.append(F.num(args[i]));
			}
			final IExpr result = F.evaln(fun);
			if (result.isSignedNumber()) {
				return ((ISignedNumber) result).doubleValue();
			}
		}
		throw new MathException("CoreCallbackFunction#evaluate() not possible for: " + functionNode.toString());
	}

}
