package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.SymbolNode;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.IDoubleCallbackFunction;
import org.matheclipse.parser.client.math.MathException;

/**
 * A call back function which could be used in <code>DoubleEvaluator</code>, for
 * evaluating Symja numerical functions.
 * 
 */
public class CoreCallbackFunction implements IDoubleCallbackFunction {
	public final static CoreCallbackFunction CONST = new CoreCallbackFunction();

	@Override
	public double evaluate(DoubleEvaluator doubleEngine, FunctionNode functionNode, double[] args) {
		ASTNode node = functionNode.getNode(0);
		if (node instanceof SymbolNode) {
			IAST fun = F.ast(F.$s(node.toString()));
			for (int i = 0; i < args.length; i++) {
				fun.append(F.num(args[i]));
			}
			final IExpr result = F.evaln(fun);
			if (result instanceof INum) {
				return ((INum) result).getRealPart();
			}
		}
		throw new MathException("CoreCallbackFunction#evaluate() not possible for: " + functionNode.toString());
	}

}
