// Generated from org\jgrapht\nio\gml\Gml.g4 by ANTLR 4.8
package org.jgrapht.nio.gml;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GmlParser}.
 */
interface GmlListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GmlParser#gml}.
	 * @param ctx the parse tree
	 */
	void enterGml(GmlParser.GmlContext ctx);
	/**
	 * Exit a parse tree produced by {@link GmlParser#gml}.
	 * @param ctx the parse tree
	 */
	void exitGml(GmlParser.GmlContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringKeyValue}
	 * labeled alternative in {@link GmlParser#keyValuePair}.
	 * @param ctx the parse tree
	 */
	void enterStringKeyValue(GmlParser.StringKeyValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringKeyValue}
	 * labeled alternative in {@link GmlParser#keyValuePair}.
	 * @param ctx the parse tree
	 */
	void exitStringKeyValue(GmlParser.StringKeyValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumberKeyValue}
	 * labeled alternative in {@link GmlParser#keyValuePair}.
	 * @param ctx the parse tree
	 */
	void enterNumberKeyValue(GmlParser.NumberKeyValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumberKeyValue}
	 * labeled alternative in {@link GmlParser#keyValuePair}.
	 * @param ctx the parse tree
	 */
	void exitNumberKeyValue(GmlParser.NumberKeyValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ListKeyValue}
	 * labeled alternative in {@link GmlParser#keyValuePair}.
	 * @param ctx the parse tree
	 */
	void enterListKeyValue(GmlParser.ListKeyValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ListKeyValue}
	 * labeled alternative in {@link GmlParser#keyValuePair}.
	 * @param ctx the parse tree
	 */
	void exitListKeyValue(GmlParser.ListKeyValueContext ctx);
}