// Generated from org\jgrapht\nio\dot\DOT.g4 by ANTLR 4.8
package org.jgrapht.nio.dot;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
class DOTParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, STRICT=11, GRAPH=12, DIGRAPH=13, NODE=14, EDGE=15, SUBGRAPH=16, 
		Numeral=17, String=18, Id=19, HtmlString=20, WS=21, COMMENT=22, LINE_COMMENT=23, 
		PREPROC=24;
	public static final int
		RULE_graph = 0, RULE_compoundStatement = 1, RULE_graphHeader = 2, RULE_graphIdentifier = 3, 
		RULE_statement = 4, RULE_identifierPairStatement = 5, RULE_attributeStatement = 6, 
		RULE_attributesList = 7, RULE_aList = 8, RULE_edgeStatement = 9, RULE_nodeStatement = 10, 
		RULE_nodeStatementNoAttributes = 11, RULE_nodeIdentifier = 12, RULE_port = 13, 
		RULE_subgraphStatement = 14, RULE_identifierPair = 15, RULE_identifier = 16;
	private static String[] makeRuleNames() {
		return new String[] {
			"graph", "compoundStatement", "graphHeader", "graphIdentifier", "statement", 
			"identifierPairStatement", "attributeStatement", "attributesList", "aList", 
			"edgeStatement", "nodeStatement", "nodeStatementNoAttributes", "nodeIdentifier", 
			"port", "subgraphStatement", "identifierPair", "identifier"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "';'", "'}'", "'['", "']'", "','", "'->'", "'--'", "':'", 
			"'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, "STRICT", 
			"GRAPH", "DIGRAPH", "NODE", "EDGE", "SUBGRAPH", "Numeral", "String", 
			"Id", "HtmlString", "WS", "COMMENT", "LINE_COMMENT", "PREPROC"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "DOT.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DOTParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class GraphContext extends ParserRuleContext {
		public GraphHeaderContext graphHeader() {
			return getRuleContext(GraphHeaderContext.class,0);
		}
		public CompoundStatementContext compoundStatement() {
			return getRuleContext(CompoundStatementContext.class,0);
		}
		public GraphContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_graph; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterGraph(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitGraph(this);
		}
	}

	public final GraphContext graph() throws RecognitionException {
		GraphContext _localctx = new GraphContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_graph);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			graphHeader();
			setState(35);
			compoundStatement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CompoundStatementContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public CompoundStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compoundStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterCompoundStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitCompoundStatement(this);
		}
	}

	public final CompoundStatementContext compoundStatement() throws RecognitionException {
		CompoundStatementContext _localctx = new CompoundStatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_compoundStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(37);
			match(T__0);
			setState(44);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << GRAPH) | (1L << NODE) | (1L << EDGE) | (1L << SUBGRAPH) | (1L << Numeral) | (1L << String) | (1L << Id) | (1L << HtmlString))) != 0)) {
				{
				{
				setState(38);
				statement();
				setState(40);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__1) {
					{
					setState(39);
					match(T__1);
					}
				}

				}
				}
				setState(46);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(47);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GraphHeaderContext extends ParserRuleContext {
		public TerminalNode GRAPH() { return getToken(DOTParser.GRAPH, 0); }
		public TerminalNode DIGRAPH() { return getToken(DOTParser.DIGRAPH, 0); }
		public TerminalNode STRICT() { return getToken(DOTParser.STRICT, 0); }
		public GraphIdentifierContext graphIdentifier() {
			return getRuleContext(GraphIdentifierContext.class,0);
		}
		public GraphHeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_graphHeader; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterGraphHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitGraphHeader(this);
		}
	}

	public final GraphHeaderContext graphHeader() throws RecognitionException {
		GraphHeaderContext _localctx = new GraphHeaderContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_graphHeader);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRICT) {
				{
				setState(49);
				match(STRICT);
				}
			}

			setState(52);
			_la = _input.LA(1);
			if ( !(_la==GRAPH || _la==DIGRAPH) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(54);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Numeral) | (1L << String) | (1L << Id) | (1L << HtmlString))) != 0)) {
				{
				setState(53);
				graphIdentifier();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GraphIdentifierContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public GraphIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_graphIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterGraphIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitGraphIdentifier(this);
		}
	}

	public final GraphIdentifierContext graphIdentifier() throws RecognitionException {
		GraphIdentifierContext _localctx = new GraphIdentifierContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_graphIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public NodeStatementContext nodeStatement() {
			return getRuleContext(NodeStatementContext.class,0);
		}
		public EdgeStatementContext edgeStatement() {
			return getRuleContext(EdgeStatementContext.class,0);
		}
		public AttributeStatementContext attributeStatement() {
			return getRuleContext(AttributeStatementContext.class,0);
		}
		public IdentifierPairStatementContext identifierPairStatement() {
			return getRuleContext(IdentifierPairStatementContext.class,0);
		}
		public SubgraphStatementContext subgraphStatement() {
			return getRuleContext(SubgraphStatementContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_statement);
		try {
			setState(63);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(58);
				nodeStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(59);
				edgeStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(60);
				attributeStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(61);
				identifierPairStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(62);
				subgraphStatement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierPairStatementContext extends ParserRuleContext {
		public IdentifierPairContext identifierPair() {
			return getRuleContext(IdentifierPairContext.class,0);
		}
		public IdentifierPairStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierPairStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterIdentifierPairStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitIdentifierPairStatement(this);
		}
	}

	public final IdentifierPairStatementContext identifierPairStatement() throws RecognitionException {
		IdentifierPairStatementContext _localctx = new IdentifierPairStatementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_identifierPairStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			identifierPair();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttributeStatementContext extends ParserRuleContext {
		public AttributesListContext attributesList() {
			return getRuleContext(AttributesListContext.class,0);
		}
		public TerminalNode GRAPH() { return getToken(DOTParser.GRAPH, 0); }
		public TerminalNode NODE() { return getToken(DOTParser.NODE, 0); }
		public TerminalNode EDGE() { return getToken(DOTParser.EDGE, 0); }
		public AttributeStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterAttributeStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitAttributeStatement(this);
		}
	}

	public final AttributeStatementContext attributeStatement() throws RecognitionException {
		AttributeStatementContext _localctx = new AttributeStatementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_attributeStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GRAPH) | (1L << NODE) | (1L << EDGE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(68);
			attributesList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttributesListContext extends ParserRuleContext {
		public List<AListContext> aList() {
			return getRuleContexts(AListContext.class);
		}
		public AListContext aList(int i) {
			return getRuleContext(AListContext.class,i);
		}
		public AttributesListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributesList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterAttributesList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitAttributesList(this);
		}
	}

	public final AttributesListContext attributesList() throws RecognitionException {
		AttributesListContext _localctx = new AttributesListContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_attributesList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(70);
				match(T__3);
				setState(72);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Numeral) | (1L << String) | (1L << Id) | (1L << HtmlString))) != 0)) {
					{
					setState(71);
					aList();
					}
				}

				setState(74);
				match(T__4);
				}
				}
				setState(77); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__3 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AListContext extends ParserRuleContext {
		public List<IdentifierPairContext> identifierPair() {
			return getRuleContexts(IdentifierPairContext.class);
		}
		public IdentifierPairContext identifierPair(int i) {
			return getRuleContext(IdentifierPairContext.class,i);
		}
		public AListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterAList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitAList(this);
		}
	}

	public final AListContext aList() throws RecognitionException {
		AListContext _localctx = new AListContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_aList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(79);
				identifierPair();
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__1 || _la==T__5) {
					{
					setState(80);
					_la = _input.LA(1);
					if ( !(_la==T__1 || _la==T__5) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				}
				}
				setState(85); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Numeral) | (1L << String) | (1L << Id) | (1L << HtmlString))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EdgeStatementContext extends ParserRuleContext {
		public List<NodeStatementNoAttributesContext> nodeStatementNoAttributes() {
			return getRuleContexts(NodeStatementNoAttributesContext.class);
		}
		public NodeStatementNoAttributesContext nodeStatementNoAttributes(int i) {
			return getRuleContext(NodeStatementNoAttributesContext.class,i);
		}
		public List<SubgraphStatementContext> subgraphStatement() {
			return getRuleContexts(SubgraphStatementContext.class);
		}
		public SubgraphStatementContext subgraphStatement(int i) {
			return getRuleContext(SubgraphStatementContext.class,i);
		}
		public AttributesListContext attributesList() {
			return getRuleContext(AttributesListContext.class,0);
		}
		public EdgeStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_edgeStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterEdgeStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitEdgeStatement(this);
		}
	}

	public final EdgeStatementContext edgeStatement() throws RecognitionException {
		EdgeStatementContext _localctx = new EdgeStatementContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_edgeStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Numeral:
			case String:
			case Id:
			case HtmlString:
				{
				setState(87);
				nodeStatementNoAttributes();
				}
				break;
			case T__0:
			case SUBGRAPH:
				{
				setState(88);
				subgraphStatement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(96); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(91);
				_la = _input.LA(1);
				if ( !(_la==T__6 || _la==T__7) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(94);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case Numeral:
				case String:
				case Id:
				case HtmlString:
					{
					setState(92);
					nodeStatementNoAttributes();
					}
					break;
				case T__0:
				case SUBGRAPH:
					{
					setState(93);
					subgraphStatement();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(98); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__6 || _la==T__7 );
			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(100);
				attributesList();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NodeStatementContext extends ParserRuleContext {
		public NodeIdentifierContext nodeIdentifier() {
			return getRuleContext(NodeIdentifierContext.class,0);
		}
		public AttributesListContext attributesList() {
			return getRuleContext(AttributesListContext.class,0);
		}
		public NodeStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nodeStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterNodeStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitNodeStatement(this);
		}
	}

	public final NodeStatementContext nodeStatement() throws RecognitionException {
		NodeStatementContext _localctx = new NodeStatementContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_nodeStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			nodeIdentifier();
			setState(105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(104);
				attributesList();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NodeStatementNoAttributesContext extends ParserRuleContext {
		public NodeIdentifierContext nodeIdentifier() {
			return getRuleContext(NodeIdentifierContext.class,0);
		}
		public NodeStatementNoAttributesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nodeStatementNoAttributes; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterNodeStatementNoAttributes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitNodeStatementNoAttributes(this);
		}
	}

	public final NodeStatementNoAttributesContext nodeStatementNoAttributes() throws RecognitionException {
		NodeStatementNoAttributesContext _localctx = new NodeStatementNoAttributesContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_nodeStatementNoAttributes);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			nodeIdentifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NodeIdentifierContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public PortContext port() {
			return getRuleContext(PortContext.class,0);
		}
		public NodeIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nodeIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterNodeIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitNodeIdentifier(this);
		}
	}

	public final NodeIdentifierContext nodeIdentifier() throws RecognitionException {
		NodeIdentifierContext _localctx = new NodeIdentifierContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_nodeIdentifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			identifier();
			setState(111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(110);
				port();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PortContext extends ParserRuleContext {
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public PortContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_port; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterPort(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitPort(this);
		}
	}

	public final PortContext port() throws RecognitionException {
		PortContext _localctx = new PortContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_port);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			match(T__8);
			setState(114);
			identifier();
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(115);
				match(T__8);
				setState(116);
				identifier();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubgraphStatementContext extends ParserRuleContext {
		public CompoundStatementContext compoundStatement() {
			return getRuleContext(CompoundStatementContext.class,0);
		}
		public TerminalNode SUBGRAPH() { return getToken(DOTParser.SUBGRAPH, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public SubgraphStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subgraphStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterSubgraphStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitSubgraphStatement(this);
		}
	}

	public final SubgraphStatementContext subgraphStatement() throws RecognitionException {
		SubgraphStatementContext _localctx = new SubgraphStatementContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_subgraphStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SUBGRAPH) {
				{
				setState(119);
				match(SUBGRAPH);
				setState(121);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Numeral) | (1L << String) | (1L << Id) | (1L << HtmlString))) != 0)) {
					{
					setState(120);
					identifier();
					}
				}

				}
			}

			setState(125);
			compoundStatement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierPairContext extends ParserRuleContext {
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
		}
		public IdentifierPairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierPair; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterIdentifierPair(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitIdentifierPair(this);
		}
	}

	public final IdentifierPairContext identifierPair() throws RecognitionException {
		IdentifierPairContext _localctx = new IdentifierPairContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_identifierPair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			identifier();
			setState(128);
			match(T__9);
			setState(129);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode Id() { return getToken(DOTParser.Id, 0); }
		public TerminalNode String() { return getToken(DOTParser.String, 0); }
		public TerminalNode HtmlString() { return getToken(DOTParser.HtmlString, 0); }
		public TerminalNode Numeral() { return getToken(DOTParser.Numeral, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DOTListener ) ((DOTListener)listener).exitIdentifier(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Numeral) | (1L << String) | (1L << Id) | (1L << HtmlString))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\32\u0088\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\3\2\3\2\3\2\3\3\3\3\3\3\5\3+\n\3\7\3-\n\3\f\3\16\3\60\13\3\3\3\3\3\3"+
		"\4\5\4\65\n\4\3\4\3\4\5\49\n\4\3\5\3\5\3\6\3\6\3\6\3\6\3\6\5\6B\n\6\3"+
		"\7\3\7\3\b\3\b\3\b\3\t\3\t\5\tK\n\t\3\t\6\tN\n\t\r\t\16\tO\3\n\3\n\5\n"+
		"T\n\n\6\nV\n\n\r\n\16\nW\3\13\3\13\5\13\\\n\13\3\13\3\13\3\13\5\13a\n"+
		"\13\6\13c\n\13\r\13\16\13d\3\13\5\13h\n\13\3\f\3\f\5\fl\n\f\3\r\3\r\3"+
		"\16\3\16\5\16r\n\16\3\17\3\17\3\17\3\17\5\17x\n\17\3\20\3\20\5\20|\n\20"+
		"\5\20~\n\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\2\2\23\2\4\6"+
		"\b\n\f\16\20\22\24\26\30\32\34\36 \"\2\7\3\2\16\17\4\2\16\16\20\21\4\2"+
		"\4\4\b\b\3\2\t\n\3\2\23\26\2\u008b\2$\3\2\2\2\4\'\3\2\2\2\6\64\3\2\2\2"+
		"\b:\3\2\2\2\nA\3\2\2\2\fC\3\2\2\2\16E\3\2\2\2\20M\3\2\2\2\22U\3\2\2\2"+
		"\24[\3\2\2\2\26i\3\2\2\2\30m\3\2\2\2\32o\3\2\2\2\34s\3\2\2\2\36}\3\2\2"+
		"\2 \u0081\3\2\2\2\"\u0085\3\2\2\2$%\5\6\4\2%&\5\4\3\2&\3\3\2\2\2\'.\7"+
		"\3\2\2(*\5\n\6\2)+\7\4\2\2*)\3\2\2\2*+\3\2\2\2+-\3\2\2\2,(\3\2\2\2-\60"+
		"\3\2\2\2.,\3\2\2\2./\3\2\2\2/\61\3\2\2\2\60.\3\2\2\2\61\62\7\5\2\2\62"+
		"\5\3\2\2\2\63\65\7\r\2\2\64\63\3\2\2\2\64\65\3\2\2\2\65\66\3\2\2\2\66"+
		"8\t\2\2\2\679\5\b\5\28\67\3\2\2\289\3\2\2\29\7\3\2\2\2:;\5\"\22\2;\t\3"+
		"\2\2\2<B\5\26\f\2=B\5\24\13\2>B\5\16\b\2?B\5\f\7\2@B\5\36\20\2A<\3\2\2"+
		"\2A=\3\2\2\2A>\3\2\2\2A?\3\2\2\2A@\3\2\2\2B\13\3\2\2\2CD\5 \21\2D\r\3"+
		"\2\2\2EF\t\3\2\2FG\5\20\t\2G\17\3\2\2\2HJ\7\6\2\2IK\5\22\n\2JI\3\2\2\2"+
		"JK\3\2\2\2KL\3\2\2\2LN\7\7\2\2MH\3\2\2\2NO\3\2\2\2OM\3\2\2\2OP\3\2\2\2"+
		"P\21\3\2\2\2QS\5 \21\2RT\t\4\2\2SR\3\2\2\2ST\3\2\2\2TV\3\2\2\2UQ\3\2\2"+
		"\2VW\3\2\2\2WU\3\2\2\2WX\3\2\2\2X\23\3\2\2\2Y\\\5\30\r\2Z\\\5\36\20\2"+
		"[Y\3\2\2\2[Z\3\2\2\2\\b\3\2\2\2]`\t\5\2\2^a\5\30\r\2_a\5\36\20\2`^\3\2"+
		"\2\2`_\3\2\2\2ac\3\2\2\2b]\3\2\2\2cd\3\2\2\2db\3\2\2\2de\3\2\2\2eg\3\2"+
		"\2\2fh\5\20\t\2gf\3\2\2\2gh\3\2\2\2h\25\3\2\2\2ik\5\32\16\2jl\5\20\t\2"+
		"kj\3\2\2\2kl\3\2\2\2l\27\3\2\2\2mn\5\32\16\2n\31\3\2\2\2oq\5\"\22\2pr"+
		"\5\34\17\2qp\3\2\2\2qr\3\2\2\2r\33\3\2\2\2st\7\13\2\2tw\5\"\22\2uv\7\13"+
		"\2\2vx\5\"\22\2wu\3\2\2\2wx\3\2\2\2x\35\3\2\2\2y{\7\22\2\2z|\5\"\22\2"+
		"{z\3\2\2\2{|\3\2\2\2|~\3\2\2\2}y\3\2\2\2}~\3\2\2\2~\177\3\2\2\2\177\u0080"+
		"\5\4\3\2\u0080\37\3\2\2\2\u0081\u0082\5\"\22\2\u0082\u0083\7\f\2\2\u0083"+
		"\u0084\5\"\22\2\u0084!\3\2\2\2\u0085\u0086\t\6\2\2\u0086#\3\2\2\2\24*"+
		".\648AJOSW[`dgkqw{}";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}