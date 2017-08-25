package org.matheclipse.core.builtin.function;

import java.io.IOException;
import java.io.PrintStream;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * Definition(symbol)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * prints user-defined values and rules associated with <code>symbol</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; Definition(ArcSinh)
 * {ArcSinh(0)=0,
 *  ArcSinh(I*1/2)=I*1/6*Pi,
 *  ArcSinh(I)=I*1/2*Pi,
 *  ArcSinh(1)=Log(1+Sqrt(2)),
 *  ArcSinh(I*1/2*Sqrt(2))=I*1/4*Pi,
 *  ArcSinh(I*1/2*Sqrt(3))=I*1/3*Pi,
 *  ArcSinh(Infinity)=Infinity,
 *  ArcSinh(I*Infinity)=Infinity,
 *  ArcSinh(ComplexInfinity)=ComplexInfinity}
 * 
 * &gt;&gt; a=2
 * 2
 * 
 * &gt;&gt; Definition(a)
 * {a=2}
 * </pre>
 */
public class Definition extends AbstractCoreFunctionEvaluator {

	public Definition() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		ISymbol symbol = Validate.checkSymbolType(ast, 1);

		PrintStream stream;
		stream = engine.getOutPrintStream();
		if (stream == null) {
			stream = System.out;
		}
		try {
			return F.stringx(symbol.definitionToString());
		} catch (IOException e) {
			stream.println(e.getMessage());
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}

		return F.Null;
	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}

}
