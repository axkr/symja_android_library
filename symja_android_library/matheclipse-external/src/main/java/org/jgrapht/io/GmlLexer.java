// Generated from org\jgrapht\io\Gml.g4 by ANTLR 4.8
package org.jgrapht.io;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
class GmlLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, NUMBER=3, STRING=4, ID=5, COMMENT=6, WS=7;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "NUMBER", "DIGIT", "LETTER", "STRING", "ID", "COMMENT", 
			"WS"
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


	public GmlLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Gml.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\t[\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2"+
		"\3\3\3\3\3\4\5\4\33\n\4\3\4\3\4\6\4\37\n\4\r\4\16\4 \3\4\6\4$\n\4\r\4"+
		"\16\4%\3\4\3\4\7\4*\n\4\f\4\16\4-\13\4\5\4/\n\4\5\4\61\n\4\3\5\3\5\3\6"+
		"\3\6\3\7\3\7\3\7\3\7\7\7;\n\7\f\7\16\7>\13\7\3\7\3\7\3\b\3\b\3\b\7\bE"+
		"\n\b\f\b\16\bH\13\b\3\t\3\t\7\tL\n\t\f\t\16\tO\13\t\3\t\3\t\3\t\3\t\3"+
		"\n\6\nV\n\n\r\n\16\nW\3\n\3\n\4<M\2\13\3\3\5\4\7\5\t\2\13\2\r\6\17\7\21"+
		"\b\23\t\3\2\5\3\2\62;\6\2C\\aac|\u0082\u0101\5\2\13\f\17\17\"\"\2d\2\3"+
		"\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2"+
		"\2\23\3\2\2\2\3\25\3\2\2\2\5\27\3\2\2\2\7\32\3\2\2\2\t\62\3\2\2\2\13\64"+
		"\3\2\2\2\r\66\3\2\2\2\17A\3\2\2\2\21I\3\2\2\2\23U\3\2\2\2\25\26\7]\2\2"+
		"\26\4\3\2\2\2\27\30\7_\2\2\30\6\3\2\2\2\31\33\7/\2\2\32\31\3\2\2\2\32"+
		"\33\3\2\2\2\33\60\3\2\2\2\34\36\7\60\2\2\35\37\5\t\5\2\36\35\3\2\2\2\37"+
		" \3\2\2\2 \36\3\2\2\2 !\3\2\2\2!\61\3\2\2\2\"$\5\t\5\2#\"\3\2\2\2$%\3"+
		"\2\2\2%#\3\2\2\2%&\3\2\2\2&.\3\2\2\2\'+\7\60\2\2(*\5\t\5\2)(\3\2\2\2*"+
		"-\3\2\2\2+)\3\2\2\2+,\3\2\2\2,/\3\2\2\2-+\3\2\2\2.\'\3\2\2\2./\3\2\2\2"+
		"/\61\3\2\2\2\60\34\3\2\2\2\60#\3\2\2\2\61\b\3\2\2\2\62\63\t\2\2\2\63\n"+
		"\3\2\2\2\64\65\t\3\2\2\65\f\3\2\2\2\66<\7$\2\2\678\7^\2\28;\7$\2\29;\13"+
		"\2\2\2:\67\3\2\2\2:9\3\2\2\2;>\3\2\2\2<=\3\2\2\2<:\3\2\2\2=?\3\2\2\2>"+
		"<\3\2\2\2?@\7$\2\2@\16\3\2\2\2AF\5\13\6\2BE\5\13\6\2CE\5\t\5\2DB\3\2\2"+
		"\2DC\3\2\2\2EH\3\2\2\2FD\3\2\2\2FG\3\2\2\2G\20\3\2\2\2HF\3\2\2\2IM\7%"+
		"\2\2JL\13\2\2\2KJ\3\2\2\2LO\3\2\2\2MN\3\2\2\2MK\3\2\2\2NP\3\2\2\2OM\3"+
		"\2\2\2PQ\7\f\2\2QR\3\2\2\2RS\b\t\2\2S\22\3\2\2\2TV\t\4\2\2UT\3\2\2\2V"+
		"W\3\2\2\2WU\3\2\2\2WX\3\2\2\2XY\3\2\2\2YZ\b\n\2\2Z\24\3\2\2\2\17\2\32"+
		" %+.\60:<DFMW\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}