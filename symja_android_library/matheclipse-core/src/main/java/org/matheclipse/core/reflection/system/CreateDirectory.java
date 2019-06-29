package org.matheclipse.core.reflection.system;

import java.io.File;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;

import com.google.common.io.Files;

/**
 * Create a default temporary directory
 *
 */
public class CreateDirectory extends AbstractEvaluator {

	public CreateDirectory() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (Config.isFileSystemEnabled(engine)) {
			if (ast.isAST0()) {
				File tempDir = Files.createTempDir();
				return F.stringx(tempDir.toString());
			} else if (ast.isAST1() && ast.arg1() instanceof IStringX) {
			}
		}
		return F.NIL;
	}

}
