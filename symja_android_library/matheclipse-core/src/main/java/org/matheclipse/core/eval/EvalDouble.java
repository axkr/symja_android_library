package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.eval.DoubleEvaluator;

public class EvalDouble extends DoubleEvaluator {
	static {
		// initialize the global available symbols
		F.initSymbols();
	}

	public EvalDouble() {
		this(null, false);
	}

	public EvalDouble(boolean relaxedSyntax) {
		this(null, relaxedSyntax);
	}

	public EvalDouble(ASTNode node, boolean relaxedSyntax) {
		super(node, relaxedSyntax);
		setCallbackFunction(CoreCallbackFunction.CONST);
	}
}
