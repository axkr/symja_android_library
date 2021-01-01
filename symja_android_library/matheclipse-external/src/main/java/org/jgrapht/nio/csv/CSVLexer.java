// Generated from org\jgrapht\nio\csv\CSV.g4 by ANTLR 4.8
package org.jgrapht.nio.csv;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
class CSVLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, SEPARATOR=3, TEXT=4, STRING=5;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "SEPARATOR", "TEXT", "TEXTCHAR", "STRING"
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


	    char sep = ',';

	    public void setSep(char sep)
	    {
	        this.sep = sep;
	    }

	    private char getSep()
	    {
	        return sep;
	    }


	public CSVLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CSV.g4"; }

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

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 2:
			return SEPARATOR_sempred((RuleContext)_localctx, predIndex);
		case 4:
			return TEXTCHAR_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean SEPARATOR_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return  _input.LA(1) == sep ;
		}
		return true;
	}
	private boolean TEXTCHAR_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return  (_input.LA(1) != sep && _input.LA(1) != '\n' && _input.LA(1) != '\r' && _input.LA(1) != '"') ;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\7)\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\5"+
		"\6\5\30\n\5\r\5\16\5\31\3\6\3\6\3\6\3\7\3\7\3\7\3\7\7\7#\n\7\f\7\16\7"+
		"&\13\7\3\7\3\7\2\2\b\3\3\5\4\7\5\t\6\13\2\r\7\3\2\3\3\2$$\2*\2\3\3\2\2"+
		"\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\r\3\2\2\2\3\17\3\2\2\2\5\21\3"+
		"\2\2\2\7\23\3\2\2\2\t\27\3\2\2\2\13\33\3\2\2\2\r\36\3\2\2\2\17\20\7\17"+
		"\2\2\20\4\3\2\2\2\21\22\7\f\2\2\22\6\3\2\2\2\23\24\6\4\2\2\24\25\13\2"+
		"\2\2\25\b\3\2\2\2\26\30\5\13\6\2\27\26\3\2\2\2\30\31\3\2\2\2\31\27\3\2"+
		"\2\2\31\32\3\2\2\2\32\n\3\2\2\2\33\34\6\6\3\2\34\35\13\2\2\2\35\f\3\2"+
		"\2\2\36$\7$\2\2\37 \7$\2\2 #\7$\2\2!#\n\2\2\2\"\37\3\2\2\2\"!\3\2\2\2"+
		"#&\3\2\2\2$\"\3\2\2\2$%\3\2\2\2%\'\3\2\2\2&$\3\2\2\2\'(\7$\2\2(\16\3\2"+
		"\2\2\6\2\31\"$\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}