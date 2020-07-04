package org.matheclipse.core.reflection.system;

import java.io.IOException;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.ASTDataset;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;
import org.matheclipse.parser.client.SyntaxError;

import tech.tablesaw.api.Table;

/**
 * Import semantic data into a DataSet
 *
 */
public class SemanticImportString extends AbstractEvaluator {

	public SemanticImportString() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (!(ast.arg1() instanceof IStringX)) {
			return F.NIL;
		}

		IStringX arg1 = (IStringX) ast.arg1(); 
		try {
			Table table = Table.read().csv(arg1.toString(), "");
			return ASTDataset.newInstance(table);
		} catch (RuntimeException rex) {
			return engine.printMessage("SemanticImportString: " + " - " + rex.getMessage());
		} finally {
		}
	}

	public int[] expectedArgSize(IAST ast) {
		return IOFunctions.ARGS_1_2;
	}

}
