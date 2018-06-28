package org.matheclipse.core.preprocessor;

import java.util.ArrayList;
import java.util.Collections;

import org.matheclipse.core.convert.AST2Expr;

public class FunctionIDGenerator {

	final static String HEADER = //
			"package org.matheclipse.core.expression;\n" + //
					"\n" + //
					"public class ID {\n";

	public static void main(String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < AST2Expr.UPPERCASE_SYMBOL_STRINGS.length; i++) {
			list.add(AST2Expr.UPPERCASE_SYMBOL_STRINGS[i]);
		}
		for (int i = 0; i < AST2Expr.SYMBOL_STRINGS.length; i++) {
			list.add(AST2Expr.SYMBOL_STRINGS[i]);
		}
		for (int i = 0; i < AST2Expr.FUNCTION_STRINGS.length; i++) {
			list.add(AST2Expr.FUNCTION_STRINGS[i]);
		}
		Collections.sort(list);

		System.out.println(HEADER);
		System.out.println("    public final static int UNKNOWN = -1;");
		for (int i = 0; i < list.size(); i++) {
			System.out.println("    public final static int " + list.get(i) + " = " + i + ";");
		}
		System.out.println("");
		System.out.println("}");

	}
}
