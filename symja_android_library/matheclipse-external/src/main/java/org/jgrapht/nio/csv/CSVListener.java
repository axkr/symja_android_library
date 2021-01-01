// Generated from org\jgrapht\nio\csv\CSV.g4 by ANTLR 4.8
package org.jgrapht.nio.csv;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CSVParser}.
 */
interface CSVListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CSVParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(CSVParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(CSVParser.FileContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVParser#header}.
	 * @param ctx the parse tree
	 */
	void enterHeader(CSVParser.HeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVParser#header}.
	 * @param ctx the parse tree
	 */
	void exitHeader(CSVParser.HeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link CSVParser#record}.
	 * @param ctx the parse tree
	 */
	void enterRecord(CSVParser.RecordContext ctx);
	/**
	 * Exit a parse tree produced by {@link CSVParser#record}.
	 * @param ctx the parse tree
	 */
	void exitRecord(CSVParser.RecordContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TextField}
	 * labeled alternative in {@link CSVParser#field}.
	 * @param ctx the parse tree
	 */
	void enterTextField(CSVParser.TextFieldContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TextField}
	 * labeled alternative in {@link CSVParser#field}.
	 * @param ctx the parse tree
	 */
	void exitTextField(CSVParser.TextFieldContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringField}
	 * labeled alternative in {@link CSVParser#field}.
	 * @param ctx the parse tree
	 */
	void enterStringField(CSVParser.StringFieldContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringField}
	 * labeled alternative in {@link CSVParser#field}.
	 * @param ctx the parse tree
	 */
	void exitStringField(CSVParser.StringFieldContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EmptyField}
	 * labeled alternative in {@link CSVParser#field}.
	 * @param ctx the parse tree
	 */
	void enterEmptyField(CSVParser.EmptyFieldContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EmptyField}
	 * labeled alternative in {@link CSVParser#field}.
	 * @param ctx the parse tree
	 */
	void exitEmptyField(CSVParser.EmptyFieldContext ctx);
}