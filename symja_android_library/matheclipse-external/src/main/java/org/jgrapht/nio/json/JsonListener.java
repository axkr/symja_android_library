// Generated from org\jgrapht\nio\json\Json.g4 by ANTLR 4.8
package org.jgrapht.nio.json;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JsonParser}.
 */
interface JsonListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JsonParser#json}.
	 * @param ctx the parse tree
	 */
	void enterJson(JsonParser.JsonContext ctx);
	/**
	 * Exit a parse tree produced by {@link JsonParser#json}.
	 * @param ctx the parse tree
	 */
	void exitJson(JsonParser.JsonContext ctx);
	/**
	 * Enter a parse tree produced by {@link JsonParser#obj}.
	 * @param ctx the parse tree
	 */
	void enterObj(JsonParser.ObjContext ctx);
	/**
	 * Exit a parse tree produced by {@link JsonParser#obj}.
	 * @param ctx the parse tree
	 */
	void exitObj(JsonParser.ObjContext ctx);
	/**
	 * Enter a parse tree produced by {@link JsonParser#pair}.
	 * @param ctx the parse tree
	 */
	void enterPair(JsonParser.PairContext ctx);
	/**
	 * Exit a parse tree produced by {@link JsonParser#pair}.
	 * @param ctx the parse tree
	 */
	void exitPair(JsonParser.PairContext ctx);
	/**
	 * Enter a parse tree produced by {@link JsonParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(JsonParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link JsonParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(JsonParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link JsonParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(JsonParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link JsonParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(JsonParser.ValueContext ctx);
}