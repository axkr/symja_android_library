// Generated from org\jgrapht\nio\gml\Gml.g4 by ANTLR 4.8
package org.jgrapht.nio.gml;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
class GmlParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, NUMBER=3, STRING=4, ID=5, COMMENT=6, WS=7;
	public static final int
		RULE_gml = 0, RULE_keyValuePair = 1;
	private static String[] makeRuleNames() {
		return new String[] {
			"gml", "keyValuePair"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "NUMBER", "STRING", "ID", "COMMENT", "WS"
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
	public String getGrammarFileName() { return "Gml.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public GmlParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class GmlContext extends ParserRuleContext {
		public List<KeyValuePairContext> keyValuePair() {
			return getRuleContexts(KeyValuePairContext.class);
		}
		public KeyValuePairContext keyValuePair(int i) {
			return getRuleContext(KeyValuePairContext.class,i);
		}
		public GmlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gml; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GmlListener ) ((GmlListener)listener).enterGml(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GmlListener ) ((GmlListener)listener).exitGml(this);
		}
	}

	public final GmlContext gml() throws RecognitionException {
		GmlContext _localctx = new GmlContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_gml);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(7);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ID) {
				{
				{
				setState(4);
				keyValuePair();
				}
				}
				setState(9);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class KeyValuePairContext extends ParserRuleContext {
		public KeyValuePairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyValuePair; }
	 
		public KeyValuePairContext() { }
		public void copyFrom(KeyValuePairContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NumberKeyValueContext extends KeyValuePairContext {
		public TerminalNode ID() { return getToken(GmlParser.ID, 0); }
		public TerminalNode NUMBER() { return getToken(GmlParser.NUMBER, 0); }
		public NumberKeyValueContext(KeyValuePairContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GmlListener ) ((GmlListener)listener).enterNumberKeyValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GmlListener ) ((GmlListener)listener).exitNumberKeyValue(this);
		}
	}
	public static class StringKeyValueContext extends KeyValuePairContext {
		public TerminalNode ID() { return getToken(GmlParser.ID, 0); }
		public TerminalNode STRING() { return getToken(GmlParser.STRING, 0); }
		public StringKeyValueContext(KeyValuePairContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GmlListener ) ((GmlListener)listener).enterStringKeyValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GmlListener ) ((GmlListener)listener).exitStringKeyValue(this);
		}
	}
	public static class ListKeyValueContext extends KeyValuePairContext {
		public TerminalNode ID() { return getToken(GmlParser.ID, 0); }
		public List<KeyValuePairContext> keyValuePair() {
			return getRuleContexts(KeyValuePairContext.class);
		}
		public KeyValuePairContext keyValuePair(int i) {
			return getRuleContext(KeyValuePairContext.class,i);
		}
		public ListKeyValueContext(KeyValuePairContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GmlListener ) ((GmlListener)listener).enterListKeyValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GmlListener ) ((GmlListener)listener).exitListKeyValue(this);
		}
	}

	public final KeyValuePairContext keyValuePair() throws RecognitionException {
		KeyValuePairContext _localctx = new KeyValuePairContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_keyValuePair);
		int _la;
		try {
			setState(23);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new StringKeyValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(10);
				match(ID);
				setState(11);
				match(STRING);
				}
				break;
			case 2:
				_localctx = new NumberKeyValueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(12);
				match(ID);
				setState(13);
				match(NUMBER);
				}
				break;
			case 3:
				_localctx = new ListKeyValueContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(14);
				match(ID);
				setState(15);
				match(T__0);
				setState(19);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==ID) {
					{
					{
					setState(16);
					keyValuePair();
					}
					}
					setState(21);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(22);
				match(T__1);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\t\34\4\2\t\2\4\3"+
		"\t\3\3\2\7\2\b\n\2\f\2\16\2\13\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3\24"+
		"\n\3\f\3\16\3\27\13\3\3\3\5\3\32\n\3\3\3\2\2\4\2\4\2\2\2\35\2\t\3\2\2"+
		"\2\4\31\3\2\2\2\6\b\5\4\3\2\7\6\3\2\2\2\b\13\3\2\2\2\t\7\3\2\2\2\t\n\3"+
		"\2\2\2\n\3\3\2\2\2\13\t\3\2\2\2\f\r\7\7\2\2\r\32\7\6\2\2\16\17\7\7\2\2"+
		"\17\32\7\5\2\2\20\21\7\7\2\2\21\25\7\3\2\2\22\24\5\4\3\2\23\22\3\2\2\2"+
		"\24\27\3\2\2\2\25\23\3\2\2\2\25\26\3\2\2\2\26\30\3\2\2\2\27\25\3\2\2\2"+
		"\30\32\7\4\2\2\31\f\3\2\2\2\31\16\3\2\2\2\31\20\3\2\2\2\32\5\3\2\2\2\5"+
		"\t\25\31";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}