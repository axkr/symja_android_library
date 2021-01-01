// Generated from org\jgrapht\nio\dot\DOT.g4 by ANTLR 4.8
package org.jgrapht.nio.dot;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
class DOTLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, STRICT=11, GRAPH=12, DIGRAPH=13, NODE=14, EDGE=15, SUBGRAPH=16, 
		Numeral=17, String=18, Id=19, HtmlString=20, WS=21, COMMENT=22, LINE_COMMENT=23, 
		PREPROC=24;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "STRICT", "GRAPH", "DIGRAPH", "NODE", "EDGE", "SUBGRAPH", "Numeral", 
			"String", "Id", "HtmlString", "HtmlTag", "SCharSequence", "SChar", "Digit", 
			"Letter", "WS", "COMMENT", "LINE_COMMENT", "PREPROC"
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


	public DOTLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DOT.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\32\u00f8\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3"+
		"\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\13"+
		"\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3"+
		"\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\5\22}\n\22"+
		"\3\22\3\22\6\22\u0081\n\22\r\22\16\22\u0082\3\22\6\22\u0086\n\22\r\22"+
		"\16\22\u0087\3\22\3\22\7\22\u008c\n\22\f\22\16\22\u008f\13\22\5\22\u0091"+
		"\n\22\5\22\u0093\n\22\3\23\3\23\5\23\u0097\n\23\3\23\3\23\3\24\3\24\3"+
		"\24\7\24\u009e\n\24\f\24\16\24\u00a1\13\24\3\25\3\25\3\25\7\25\u00a6\n"+
		"\25\f\25\16\25\u00a9\13\25\3\25\3\25\3\26\3\26\7\26\u00af\n\26\f\26\16"+
		"\26\u00b2\13\26\3\26\3\26\3\27\6\27\u00b7\n\27\r\27\16\27\u00b8\3\30\3"+
		"\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u00c3\n\30\3\31\3\31\3\32\3\32"+
		"\3\33\6\33\u00ca\n\33\r\33\16\33\u00cb\3\33\3\33\3\34\3\34\3\34\3\34\7"+
		"\34\u00d4\n\34\f\34\16\34\u00d7\13\34\3\34\3\34\3\34\3\34\3\34\3\35\3"+
		"\35\3\35\3\35\7\35\u00e2\n\35\f\35\16\35\u00e5\13\35\3\35\5\35\u00e8\n"+
		"\35\3\35\3\35\3\35\3\35\3\36\3\36\7\36\u00f0\n\36\f\36\16\36\u00f3\13"+
		"\36\3\36\3\36\3\36\3\36\6\u00b0\u00d5\u00e3\u00f1\2\37\3\3\5\4\7\5\t\6"+
		"\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24"+
		"\'\25)\26+\2-\2/\2\61\2\63\2\65\27\67\309\31;\32\3\2\26\4\2UUuu\4\2VV"+
		"vv\4\2TTtt\4\2KKkk\4\2EEee\4\2IIii\4\2CCcc\4\2RRrr\4\2JJjj\4\2FFff\4\2"+
		"PPpp\4\2QQqq\4\2GGgg\4\2WWww\4\2DDdd\4\2>>@@\4\2$$^^\3\2\62;\6\2C\\aa"+
		"c|\u0082\u0101\5\2\13\f\17\17\"\"\2\u0107\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\3=\3\2\2\2"+
		"\5?\3\2\2\2\7A\3\2\2\2\tC\3\2\2\2\13E\3\2\2\2\rG\3\2\2\2\17I\3\2\2\2\21"+
		"L\3\2\2\2\23O\3\2\2\2\25Q\3\2\2\2\27S\3\2\2\2\31Z\3\2\2\2\33`\3\2\2\2"+
		"\35h\3\2\2\2\37m\3\2\2\2!r\3\2\2\2#|\3\2\2\2%\u0094\3\2\2\2\'\u009a\3"+
		"\2\2\2)\u00a2\3\2\2\2+\u00ac\3\2\2\2-\u00b6\3\2\2\2/\u00c2\3\2\2\2\61"+
		"\u00c4\3\2\2\2\63\u00c6\3\2\2\2\65\u00c9\3\2\2\2\67\u00cf\3\2\2\29\u00dd"+
		"\3\2\2\2;\u00ed\3\2\2\2=>\7}\2\2>\4\3\2\2\2?@\7=\2\2@\6\3\2\2\2AB\7\177"+
		"\2\2B\b\3\2\2\2CD\7]\2\2D\n\3\2\2\2EF\7_\2\2F\f\3\2\2\2GH\7.\2\2H\16\3"+
		"\2\2\2IJ\7/\2\2JK\7@\2\2K\20\3\2\2\2LM\7/\2\2MN\7/\2\2N\22\3\2\2\2OP\7"+
		"<\2\2P\24\3\2\2\2QR\7?\2\2R\26\3\2\2\2ST\t\2\2\2TU\t\3\2\2UV\t\4\2\2V"+
		"W\t\5\2\2WX\t\6\2\2XY\t\3\2\2Y\30\3\2\2\2Z[\t\7\2\2[\\\t\4\2\2\\]\t\b"+
		"\2\2]^\t\t\2\2^_\t\n\2\2_\32\3\2\2\2`a\t\13\2\2ab\t\5\2\2bc\t\7\2\2cd"+
		"\t\4\2\2de\t\b\2\2ef\t\t\2\2fg\t\n\2\2g\34\3\2\2\2hi\t\f\2\2ij\t\r\2\2"+
		"jk\t\13\2\2kl\t\16\2\2l\36\3\2\2\2mn\t\16\2\2no\t\13\2\2op\t\7\2\2pq\t"+
		"\16\2\2q \3\2\2\2rs\t\2\2\2st\t\17\2\2tu\t\20\2\2uv\t\7\2\2vw\t\4\2\2"+
		"wx\t\b\2\2xy\t\t\2\2yz\t\n\2\2z\"\3\2\2\2{}\7/\2\2|{\3\2\2\2|}\3\2\2\2"+
		"}\u0092\3\2\2\2~\u0080\7\60\2\2\177\u0081\5\61\31\2\u0080\177\3\2\2\2"+
		"\u0081\u0082\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0093"+
		"\3\2\2\2\u0084\u0086\5\61\31\2\u0085\u0084\3\2\2\2\u0086\u0087\3\2\2\2"+
		"\u0087\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u0090\3\2\2\2\u0089\u008d"+
		"\7\60\2\2\u008a\u008c\5\61\31\2\u008b\u008a\3\2\2\2\u008c\u008f\3\2\2"+
		"\2\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u0091\3\2\2\2\u008f\u008d"+
		"\3\2\2\2\u0090\u0089\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u0093\3\2\2\2\u0092"+
		"~\3\2\2\2\u0092\u0085\3\2\2\2\u0093$\3\2\2\2\u0094\u0096\7$\2\2\u0095"+
		"\u0097\5-\27\2\u0096\u0095\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u0098\3\2"+
		"\2\2\u0098\u0099\7$\2\2\u0099&\3\2\2\2\u009a\u009f\5\63\32\2\u009b\u009e"+
		"\5\63\32\2\u009c\u009e\5\61\31\2\u009d\u009b\3\2\2\2\u009d\u009c\3\2\2"+
		"\2\u009e\u00a1\3\2\2\2\u009f\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0("+
		"\3\2\2\2\u00a1\u009f\3\2\2\2\u00a2\u00a7\7>\2\2\u00a3\u00a6\5+\26\2\u00a4"+
		"\u00a6\n\21\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a4\3\2\2\2\u00a6\u00a9\3"+
		"\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00aa\3\2\2\2\u00a9"+
		"\u00a7\3\2\2\2\u00aa\u00ab\7@\2\2\u00ab*\3\2\2\2\u00ac\u00b0\7>\2\2\u00ad"+
		"\u00af\13\2\2\2\u00ae\u00ad\3\2\2\2\u00af\u00b2\3\2\2\2\u00b0\u00b1\3"+
		"\2\2\2\u00b0\u00ae\3\2\2\2\u00b1\u00b3\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b3"+
		"\u00b4\7@\2\2\u00b4,\3\2\2\2\u00b5\u00b7\5/\30\2\u00b6\u00b5\3\2\2\2\u00b7"+
		"\u00b8\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9.\3\2\2\2"+
		"\u00ba\u00c3\n\22\2\2\u00bb\u00bc\7^\2\2\u00bc\u00c3\t\22\2\2\u00bd\u00be"+
		"\7^\2\2\u00be\u00c3\7\f\2\2\u00bf\u00c0\7^\2\2\u00c0\u00c1\7\17\2\2\u00c1"+
		"\u00c3\7\f\2\2\u00c2\u00ba\3\2\2\2\u00c2\u00bb\3\2\2\2\u00c2\u00bd\3\2"+
		"\2\2\u00c2\u00bf\3\2\2\2\u00c3\60\3\2\2\2\u00c4\u00c5\t\23\2\2\u00c5\62"+
		"\3\2\2\2\u00c6\u00c7\t\24\2\2\u00c7\64\3\2\2\2\u00c8\u00ca\t\25\2\2\u00c9"+
		"\u00c8\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00c9\3\2\2\2\u00cb\u00cc\3\2"+
		"\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00ce\b\33\2\2\u00ce\66\3\2\2\2\u00cf\u00d0"+
		"\7\61\2\2\u00d0\u00d1\7,\2\2\u00d1\u00d5\3\2\2\2\u00d2\u00d4\13\2\2\2"+
		"\u00d3\u00d2\3\2\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d5\u00d3"+
		"\3\2\2\2\u00d6\u00d8\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d8\u00d9\7,\2\2\u00d9"+
		"\u00da\7\61\2\2\u00da\u00db\3\2\2\2\u00db\u00dc\b\34\2\2\u00dc8\3\2\2"+
		"\2\u00dd\u00de\7\61\2\2\u00de\u00df\7\61\2\2\u00df\u00e3\3\2\2\2\u00e0"+
		"\u00e2\13\2\2\2\u00e1\u00e0\3\2\2\2\u00e2\u00e5\3\2\2\2\u00e3\u00e4\3"+
		"\2\2\2\u00e3\u00e1\3\2\2\2\u00e4\u00e7\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e6"+
		"\u00e8\7\17\2\2\u00e7\u00e6\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00e9\3"+
		"\2\2\2\u00e9\u00ea\7\f\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ec\b\35\2\2\u00ec"+
		":\3\2\2\2\u00ed\u00f1\7%\2\2\u00ee\u00f0\13\2\2\2\u00ef\u00ee\3\2\2\2"+
		"\u00f0\u00f3\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f2\u00f4"+
		"\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f4\u00f5\7\f\2\2\u00f5\u00f6\3\2\2\2\u00f6"+
		"\u00f7\b\36\2\2\u00f7<\3\2\2\2\26\2|\u0082\u0087\u008d\u0090\u0092\u0096"+
		"\u009d\u009f\u00a5\u00a7\u00b0\u00b8\u00c2\u00cb\u00d5\u00e3\u00e7\u00f1"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}