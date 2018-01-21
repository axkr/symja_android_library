package org.matheclipse.core.preprocessor;

import java.util.ArrayList;
import java.util.Collections;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.SymbolEnumeration;
import org.matheclipse.core.interfaces.IBuiltInSymbol;

public class EnumGenerator {

	final static String HEADER = "package org.matheclipse.core.expression;\n" + "\n"
			+ "public enum SymbolEnumeration {";

	final static String FOOTER = "\n" + "	private final String name;\n" + "	private final int symbolID;\n" + "\n"
			+ "	SymbolEnumeration(String name, int symbolID) {\n" + "		this.name = name;\n"
			+ "		this.symbolID = symbolID;\n" + "	}\n" + "\n" + "	public final int id() {\n"
			+ "		return symbolID;\n" + "	}\n" + "	\n" + "	public final String str() {\n" + "		return name;\n"
			+ "	}\n" + "}";

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
		// Map<String, String> map = AST2Expr.PREDEFINED_SYMBOLS_MAP;
		 System.out.println(HEADER);
		 for (int i = 0; i < list.size(); i++) {
		 System.out.print(" " + list.get(i) + "( \"" + list.get(i).toLowerCase() + "\", " + i);
		 if (i < list.size() - 1) {
		 System.out.println(" ), //");
		 } else {
		 System.out.println(" );");
		 }
		 }
		 System.out.println(FOOTER);

		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		for (int i = 0; i < list.size(); i++) {
			System.out.println("    " + list.get(i) + " = " + i + ";");
		}

		// public final static IBuiltInSymbol XXXXX = SymbolEnumeration.valueOf(SymbolEnumeration.XXXXX);
//		System.out.println("");
//		System.out.println("");
//		System.out.println("");
//		System.out.println("");
//		System.out.println("");
//		for (int i = 0; i < list.size(); i++) {
//			System.out.println("        public final static IBuiltInSymbol " + list.get(i)
//					+ " = SymbolEnumeration.valueOf(SymbolEnumeration." + list.get(i) + ");");
//		}
		
	}
}
