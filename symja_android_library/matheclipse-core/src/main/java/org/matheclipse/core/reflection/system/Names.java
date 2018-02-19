package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.SuggestTree;
import org.matheclipse.core.eval.util.SuggestTree.Node;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;

import java.util.ArrayList;
import java.util.List;
import com.duy.lambda.IntFunction;

public class Names extends AbstractFunctionEvaluator {

	public Names() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 1, 2);

		if (ast.isAST0()) {
			return getAllNames();
		}

		if (ast.arg1() instanceof IStringX) {
			return getNamesByPrefix(ast.arg1().toString());
		}
		return F.NIL;
	}

	public static IAST getNamesByPrefix(String name) {

		if (name.length() == 0) {
			return F.List();
		}
		boolean exact = true;
		if (name.charAt(name.length() - 1) == '*') {
			name = name.substring(0, name.length() - 1);
			if (name.length() == 0) {
				return getAllNames();
			}
			exact = false;
		}
		SuggestTree suggestTree = AST2Expr.getSuggestTree();
		Node n = suggestTree.getAutocompleteSuggestions(name);
		if (n != null) {
			IASTAppendable list = F.ListAlloc(n.listLength());
			for (int i = 0; i < n.listLength(); i++) {
				if (exact) {
					if (name.equals(n.getSuggestion(i).getTerm())) {
						list.append(F.$s(n.getSuggestion(i).getTerm()));
					}
				} else {
					list.append(F.$s(n.getSuggestion(i).getTerm()));
				}
			}
			return list;
		}
		return F.List();
	}

	public static List<String> getAutoCompletionList(String namePrefix) {
		List<String> list = new ArrayList<String>();
		if (namePrefix.length() == 0) {
			return list;
		}
		SuggestTree suggestTree = AST2Expr.getSuggestTree();
		Node n = suggestTree.getAutocompleteSuggestions(namePrefix);
		if (n != null) {
			for (int i = 0; i < n.listLength(); i++) {
				list.add(n.getSuggestion(i).getTerm());
			}
		}
		return list;
	}

	public static IAST getAllNames() {
		int size = AST2Expr.FUNCTION_STRINGS.length;
		IASTAppendable list = F.ListAlloc(size);
		return list.appendArgs(0, size, new IntFunction<IExpr>() {
            @Override
            public IExpr apply(int i) {
                return F.$s(AST2Expr.FUNCTION_STRINGS[i]);
            }
        });
		// for (int i = 0; i < size; i++) {
		// list.append(F.$s(AST2Expr.FUNCTION_STRINGS[i]));
		// }
		// return list;
	}
}