// Generated from org\jgrapht\io\CSV.g4 by ANTLR 4.8
package org.jgrapht.io;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
class CSVParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, SEPARATOR=3, TEXT=4, STRING=5;
	public static final int
		RULE_file = 0, RULE_header = 1, RULE_record = 2, RULE_field = 3;
	private static String[] makeRuleNames() {
		return new String[] {
			"file", "header", "record", "field"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'\r'", "'\n'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "SEPARATOR", "TEXT", "STRING"
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
	public String getGrammarFileName() { return "CSV.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CSVParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class FileContext extends ParserRuleContext {
		public HeaderContext header() {
			return getRuleContext(HeaderContext.class,0);
		}
		public List<RecordContext> record() {
			return getRuleContexts(RecordContext.class);
		}
		public RecordContext record(int i) {
			return getRuleContext(RecordContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).exitFile(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(8);
			header();
			setState(10); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(9);
				record();
				}
				}
				setState(12); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << SEPARATOR) | (1L << TEXT) | (1L << STRING))) != 0) );
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

	public static class HeaderContext extends ParserRuleContext {
		public RecordContext record() {
			return getRuleContext(RecordContext.class,0);
		}
		public HeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_header; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).enterHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).exitHeader(this);
		}
	}

	public final HeaderContext header() throws RecognitionException {
		HeaderContext _localctx = new HeaderContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_header);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(14);
			record();
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

	public static class RecordContext extends ParserRuleContext {
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public List<TerminalNode> SEPARATOR() { return getTokens(CSVParser.SEPARATOR); }
		public TerminalNode SEPARATOR(int i) {
			return getToken(CSVParser.SEPARATOR, i);
		}
		public RecordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_record; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).enterRecord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).exitRecord(this);
		}
	}

	public final RecordContext record() throws RecognitionException {
		RecordContext _localctx = new RecordContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_record);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			field();
			setState(21);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEPARATOR) {
				{
				{
				setState(17);
				match(SEPARATOR);
				setState(18);
				field();
				}
				}
				setState(23);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(25);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(24);
				match(T__0);
				}
			}

			setState(27);
			match(T__1);
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

	public static class FieldContext extends ParserRuleContext {
		public FieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field; }
	 
		public FieldContext() { }
		public void copyFrom(FieldContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EmptyFieldContext extends FieldContext {
		public EmptyFieldContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).enterEmptyField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).exitEmptyField(this);
		}
	}
	public static class StringFieldContext extends FieldContext {
		public TerminalNode STRING() { return getToken(CSVParser.STRING, 0); }
		public StringFieldContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).enterStringField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).exitStringField(this);
		}
	}
	public static class TextFieldContext extends FieldContext {
		public TerminalNode TEXT() { return getToken(CSVParser.TEXT, 0); }
		public TextFieldContext(FieldContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).enterTextField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).exitTextField(this);
		}
	}

	public final FieldContext field() throws RecognitionException {
		FieldContext _localctx = new FieldContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_field);
		try {
			setState(32);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXT:
				_localctx = new TextFieldContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(29);
				match(TEXT);
				}
				break;
			case STRING:
				_localctx = new StringFieldContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(30);
				match(STRING);
				}
				break;
			case T__0:
			case T__1:
			case SEPARATOR:
				_localctx = new EmptyFieldContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\7%\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\3\2\3\2\6\2\r\n\2\r\2\16\2\16\3\3\3\3\3\4\3\4\3\4\7"+
		"\4\26\n\4\f\4\16\4\31\13\4\3\4\5\4\34\n\4\3\4\3\4\3\5\3\5\3\5\5\5#\n\5"+
		"\3\5\2\2\6\2\4\6\b\2\2\2%\2\n\3\2\2\2\4\20\3\2\2\2\6\22\3\2\2\2\b\"\3"+
		"\2\2\2\n\f\5\4\3\2\13\r\5\6\4\2\f\13\3\2\2\2\r\16\3\2\2\2\16\f\3\2\2\2"+
		"\16\17\3\2\2\2\17\3\3\2\2\2\20\21\5\6\4\2\21\5\3\2\2\2\22\27\5\b\5\2\23"+
		"\24\7\5\2\2\24\26\5\b\5\2\25\23\3\2\2\2\26\31\3\2\2\2\27\25\3\2\2\2\27"+
		"\30\3\2\2\2\30\33\3\2\2\2\31\27\3\2\2\2\32\34\7\3\2\2\33\32\3\2\2\2\33"+
		"\34\3\2\2\2\34\35\3\2\2\2\35\36\7\4\2\2\36\7\3\2\2\2\37#\7\6\2\2 #\7\7"+
		"\2\2!#\3\2\2\2\"\37\3\2\2\2\" \3\2\2\2\"!\3\2\2\2#\t\3\2\2\2\6\16\27\33"+
		"\"";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}