// Generated from org\jgrapht\io\DOT.g4 by ANTLR 4.8
package org.jgrapht.io;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DOTParser}.
 */
interface DOTListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DOTParser#graph}.
	 * @param ctx the parse tree
	 */
	void enterGraph(DOTParser.GraphContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#graph}.
	 * @param ctx the parse tree
	 */
	void exitGraph(DOTParser.GraphContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void enterCompoundStatement(DOTParser.CompoundStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void exitCompoundStatement(DOTParser.CompoundStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#graphHeader}.
	 * @param ctx the parse tree
	 */
	void enterGraphHeader(DOTParser.GraphHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#graphHeader}.
	 * @param ctx the parse tree
	 */
	void exitGraphHeader(DOTParser.GraphHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#graphIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterGraphIdentifier(DOTParser.GraphIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#graphIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitGraphIdentifier(DOTParser.GraphIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(DOTParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(DOTParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#identifierPairStatement}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierPairStatement(DOTParser.IdentifierPairStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#identifierPairStatement}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierPairStatement(DOTParser.IdentifierPairStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#attributeStatement}.
	 * @param ctx the parse tree
	 */
	void enterAttributeStatement(DOTParser.AttributeStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#attributeStatement}.
	 * @param ctx the parse tree
	 */
	void exitAttributeStatement(DOTParser.AttributeStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#attributesList}.
	 * @param ctx the parse tree
	 */
	void enterAttributesList(DOTParser.AttributesListContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#attributesList}.
	 * @param ctx the parse tree
	 */
	void exitAttributesList(DOTParser.AttributesListContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#aList}.
	 * @param ctx the parse tree
	 */
	void enterAList(DOTParser.AListContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#aList}.
	 * @param ctx the parse tree
	 */
	void exitAList(DOTParser.AListContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#edgeStatement}.
	 * @param ctx the parse tree
	 */
	void enterEdgeStatement(DOTParser.EdgeStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#edgeStatement}.
	 * @param ctx the parse tree
	 */
	void exitEdgeStatement(DOTParser.EdgeStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#nodeStatement}.
	 * @param ctx the parse tree
	 */
	void enterNodeStatement(DOTParser.NodeStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#nodeStatement}.
	 * @param ctx the parse tree
	 */
	void exitNodeStatement(DOTParser.NodeStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#nodeStatementNoAttributes}.
	 * @param ctx the parse tree
	 */
	void enterNodeStatementNoAttributes(DOTParser.NodeStatementNoAttributesContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#nodeStatementNoAttributes}.
	 * @param ctx the parse tree
	 */
	void exitNodeStatementNoAttributes(DOTParser.NodeStatementNoAttributesContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#nodeIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterNodeIdentifier(DOTParser.NodeIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#nodeIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitNodeIdentifier(DOTParser.NodeIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#port}.
	 * @param ctx the parse tree
	 */
	void enterPort(DOTParser.PortContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#port}.
	 * @param ctx the parse tree
	 */
	void exitPort(DOTParser.PortContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#subgraphStatement}.
	 * @param ctx the parse tree
	 */
	void enterSubgraphStatement(DOTParser.SubgraphStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#subgraphStatement}.
	 * @param ctx the parse tree
	 */
	void exitSubgraphStatement(DOTParser.SubgraphStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#identifierPair}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierPair(DOTParser.IdentifierPairContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#identifierPair}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierPair(DOTParser.IdentifierPairContext ctx);
	/**
	 * Enter a parse tree produced by {@link DOTParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(DOTParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link DOTParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(DOTParser.IdentifierContext ctx);
}