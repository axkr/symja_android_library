package org.matheclipse.core.builtin;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.AbstractPatternSequence;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.PatternNested;
import org.matheclipse.core.expression.RepeatedPattern;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.tex.TeXParser;
import org.matheclipse.core.form.tex.TeXSliceParser;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPredicate;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.ParserConfig;
import com.google.common.base.CharMatcher;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public final class StringFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Index <code>0</code> in {@link REGEX_LONGEST} and {@link REGEX_SHORTEST}. The plus quantifier
   * indicates one or more occurrences of the preceding element. For example, ab+c matches "abc",
   * "abbc", "abbbc", and so on, but not "ac".
   */
  static final int PLUS_Q = 0;

  /**
   * Index <code>1</code> in {@link REGEX_LONGEST} and {@link REGEX_SHORTEST}. The asterisk
   * quantifier indicates zero or more occurrences of the preceding element. For example, ab*c
   * matches "ac", "abc", "abbc", "abbbc", and so on.
   */
  static final int ASTERISK_Q = 1;

  /**
   * Longest regex quantifier for <code>BlankSequence</code>,<code>BlankNullSequence</code>, <code>
   * Repeated</code>, <code>RepeatedNull</code>.
   *
   * @see <a href="https://en.wikipedia.org/wiki/Regular_expression">Wikipedia - Regular
   *      expression</a>
   */
  static final String[] REGEX_LONGEST = new String[] {"+", "*"};

  /**
   * Shortest regex quantifier for <code>BlankSequence</code>,<code>BlankNullSequence</code>, <code>
   * Repeated</code>, <code>RepeatedNull</code>.
   *
   * @see <a href="https://en.wikipedia.org/wiki/Regular_expression">Wikipedia - Regular
   *      expression</a>
   */
  static final String[] REGEX_SHORTEST = new String[] {"+?", "*?"};

  /** The English alphabet */
  private static final String LATIN_ALPHABET = "abcdefghijklmnopqrstuvwxyz";

  private static final String CYRILLIC_ALPHABET =
      "\u0430\u0431\u0432\u0433\u0491\u0434\u0452\u0453\u0435\u0451\u0454\u0436\u0437\u0437\u0301\u0455\u0438\u0456\u0457\u0439\u0458\u043a\u043b\u0459\u043c\u043d\u045a\u043e\u043f\u0440\u0441\u0441\u0301\u0442\u045b\u045c\u0443\u045e\u0444\u0445\u0446\u0447\u045f\u0448\u0449\u044a\u044b\u044c\u044d\u044e\u044f";

  /** Alphabet strings which contain only single characters not separated by comma. */
  private static final String[] ALPHABETS = {"Arabic",
      "\u0627\u0628\u062a\u062b\u062c\u062d\u062e\u062f\u0630\u0631\u0632\u0633\u0634\u0635\u0636\u0637\u0638\u0639\u063a\u0641\u0642\u0643\u0644\u0645\u0646\u0647\u0648\u064a",
      "Belarusian",
      "\u0430\u0431\u0432\u0433\u0434\u0435\u0451\u0436\u0437\u0456\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u045e\u0444\u0445\u0446\u0447\u0448\u044b\u044c\u044d\u044e\u044f\u0027",
      "Bulgarian",
      "\u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044c\u044e\u044f",
      "Catalan", "abcçdefghijklmnopqrstuvwxyz",
      // "Chinese", EMPTY_ALPHABET, // not known
      "Cyrillic", CYRILLIC_ALPHABET, "Danish", "abcdefghijklmnopqrstuvwxyzæøå", "English",
      LATIN_ALPHABET, "Esperanto", "abcĉdefgĝhĥijĵklmnoprsŝtuŭvz", "Estonian",
      "abdefghijklmnoprsšzžtuvõäöü", "Finnish", "abcdefghijklmnopqrstuvwxyzåäö", "French",
      LATIN_ALPHABET, "German", LATIN_ALPHABET, "Greek",
      "\u03b1\u03b2\u03b3\u03b4\u03b5\u03b6\u03b7\u03b8\u03b9\u03ba\u03bb\u03bc\u03bd\u03be\u03bf\u03c0\u03c1\u03c3\u03c4\u03c5\u03c6\u03c7\u03c8\u03c9",
      "Hebrew",
      "\u05d0\u05d1\u05d2\u05d3\u05d4\u05d5\u05d6\u05d7\u05d8\u05d9\u05db\u05dc\u05de\u05e0\u05e1\u05e2\u05e4\u05e6\u05e7\u05e8\u05e9\u05ea",
      "Hindi",
      "\u0905\u0906\u0907\u0908\u0909\u090a\u090b\u090f\u0910\u0913\u0914\u0915\u0916\u0917\u0918\u0919\u091a\u091b\u091c\u091d\u091e\u091f\u0920\u0921\u0922\u0923\u0924\u0925\u0926\u0927\u0928\u092a\u092b\u092c\u092d\u092e\u092f\u0930\u0932\u0935\u0936\u0937\u0938\u0939",
      "Icelandic", "aábdðeéfghiíjklmnoóprstuúvxyýþæö", "Indonesian", LATIN_ALPHABET, "Irish",
      "abcdefghilmnoprstu", "Italian", "abcdefghilmnopqrstuvz",
      // "Japanese", EMPTY_ALPHABET,// not known
      "Korean",
      "\u3131\u3132\u3134\u3137\u3138\u3139\u3141\u3142\u3143\u3145\u3146\u3147\u3148\u3149\u314a\u314b\u314c\u314d\u314e\u314f\u3150\u3151\u3152\u3153\u3154\u3155\u3156\u3157\u3158\u3159\u315a\u315b\u315c\u315d\u315e\u315f\u3160\u3161\u3162\u3163",
      "Latin", LATIN_ALPHABET, "Latvian", "aābcčdeēfgģhiījkķlļmnņoprsštuūvzž", "Lithuanian",
      "aąbcčdeęėfghiįyjklmnoprsštuųūvzž", "Macedonian",
      "\u0430\u0431\u0432\u0433\u0434\u0453\u0435\u0436\u0437\u0455\u0438\u0458\u043a\u043b\u0459\u043c\u043d\u045a\u043e\u043f\u0440\u0441\u0442\u045c\u0443\u0444\u0445\u0446\u0447\u045f\u0448",
      "Malay", LATIN_ALPHABET, "Norwegian", "abcdefghijklmnopqrstuvwxyzæøå", "Polish",
      "aąbcćdeęfghijklłmnńoóprsśtuwyzźż", "Portuguese", LATIN_ALPHABET, "Romanian",
      "aăâbcdefghiîjklmnopqrsștțuvwxyz", "Russian",
      "\u0430\u0431\u0432\u0433\u0434\u0435\u0451\u0436\u0437\u0438\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044a\u044b\u044c\u044d\u044e\u044f",
      // "Serbian", EMPTY_ALPHABET,// not known
      "Slovenian", "abcčdefghijklmnoprsštuvzž", "Spanish", "abcdefghijklmnñopqrstuvwxyz", "Swedish",
      "abcdefghijklmnopqrstuvwxyzåäö",
      // "Thai", EMPTY_ALPHABET,// not known
      "Turkish", "abcçdefgğhıijklmnoöprsştuüvyz", "Ukrainian",
      "\u0430\u0431\u0432\u0433\u0491\u0434\u0435\u0454\u0436\u0437\u0438\u0456\u0457\u0439\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044c\u044e\u044f",
      "Vietnamese", "aăâbcdđeêfghiklmnoôơpqrstuưvxy"};

  /** Alphabet strings which contain single/multiple characters separated by comma. */
  private static final String[] ALPHABETS_CSV = {"Albanian",
      "a,b,c,ç,d,dh,e,ë,f,g,gj,h,i,j,k,l,ll,m,n,nj,o,p,q,r,rr,s,sh,t,th,u,v,x,xh,y,z,zh",
      "Croatian", "a,b,c,č,ć,d,dž,đ,e,f,g,h,i,j,k,l,lj,m,n,nj,o,p,r,s,š,t,u,v,z,ž", "Czech",
      "a,á,b,c,č,d,ď,e,é,ě,f,g,h,ch,i,í,j,k,l,m,n,ň,o,ó,p,q,r,ř,s,š,t,ť,u,ú,ů,v,w,x,y,ý,z,ž",
      "Dutch", "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,ij,z", "Hungarian",
      "a,á,b,c,cs,d,dz,dzs,e,é,f,g,gy,h,i,í,j,k,l,ly,m,n,ny,o,ó,ö,ő,p,q,r,s,sz,t,ty,u,ú,ü,ű,v,w,x,y,z,zs",
      "Maltese", "a,b,ċ,d,e,f,ġ,g,għ,h,ħ,i,ie,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,ż,z", "Slovak",
      "a,á,ä,b,c,č,d,ď,dz,dž,e,é,f,g,h,ch,i,í,j,k,l,ĺ,ľ,m,n,ň,o,ó,ô,p,q,r,ŕ,s,š,t,ť,u,ú,v,w,x,y,ý,z,ž"};

  /** The languages locale shortcut. */
  private static final String[] LANGUAGES = {"Albanian", "sq", "Arabic", "ar", "Belarusian", "be",
      "Bulgarian", "bg", "Catalan", "ca", "Chinese", "zh", "Croatian", "hr", "Czech", "cs",
      "Danish", "da", "Dutch", "nl", "English", "en", "Estonian", "et", "Finnish", "fi", "French",
      "fr", "German", "de", "Greek", "el", "Hebrew", "iw", "Hindi", "hi", "Hungarian", "hu",
      "Icelandic", "is", "Indonesian", "in", "Irish", "ga", "Italian", "it", "Japanese", "ja",
      "Korean", "ko", "Latvian", "lv", "Lithuanian", "lt", "Macedonian", "mk", "Malay", "ms",
      "Maltese", "mt", "Norwegian", "no", "Polish", "pl", "Portuguese", "pt", "Romanian", "ro",
      "Russian", "ru", "Serbian", "sr", "Slovak", "sk", "Slovenian", "sl", "Spanish", "es",
      "Swedish", "sv", "Thai", "th", "Turkish", "tr", "Ukrainian", "uk", "Vietnamese", "vi",};

  /** Map language name to alphabet string with characters not separated by comma.. */
  private static final Map<String, String> ALPHABET_MAP = new HashMap<String, String>();

  /** Map language name to alphabet character strings */
  private static final Map<String, String[]> ALPHABET_CSV_MAP = new HashMap<String, String[]>();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      for (int i = 0; i < ALPHABETS.length; i += 2) {
        ALPHABET_MAP.put(ALPHABETS[i], ALPHABETS[i + 1]);
      }
      for (int i = 0; i < ALPHABETS_CSV.length; i += 2) {
        String alphabetCSV = ALPHABETS_CSV[i + 1];
        String[] result = alphabetCSV.split(",");
        ALPHABET_CSV_MAP.put(ALPHABETS_CSV[i], result);
      }
      S.Alphabet.setEvaluator(new Alphabet());
      S.BaseDecode.setEvaluator(new BaseDecode());
      S.BaseEncode.setEvaluator(new BaseEncode());
      S.ByteArrayToString.setEvaluator(new ByteArrayToString());
      S.Characters.setEvaluator(new Characters());
      S.CharacterRange.setEvaluator(new CharacterRange());
      S.EditDistance.setEvaluator(new EditDistance());

      S.FileNameJoin.setEvaluator(new FileNameJoin());
      S.FileNameTake.setEvaluator(new FileNameTake());
      S.FromCharacterCode.setEvaluator(new FromCharacterCode());
      S.FromLetterNumber.setEvaluator(new FromLetterNumber());
      S.HammingDistance.setEvaluator(new HammingDistance());
      S.LetterNumber.setEvaluator(new LetterNumber());
      S.LetterQ.setEvaluator(new LetterQ());
      S.LowerCaseQ.setEvaluator(new LowerCaseQ());
      S.PrintableASCIIQ.setEvaluator(new PrintableASCIIQ());
      S.StringCases.setEvaluator(new StringCases());
      S.StringCount.setEvaluator(new StringCount());
      S.StringContainsQ.setEvaluator(new StringContainsQ());
      S.StringDrop.setEvaluator(new StringDrop());
      S.StringExpression.setEvaluator(new StringExpression());
      S.StringFreeQ.setEvaluator(new StringFreeQ());
      S.StringFormat.setEvaluator(new StringFormat());
      S.StringInsert.setEvaluator(new StringInsert());
      S.StringJoin.setEvaluator(new StringJoin());
      S.StringLength.setEvaluator(new StringLength());
      S.StringMatchQ.setEvaluator(new StringMatchQ());
      S.StringPart.setEvaluator(new StringPart());
      S.StringPosition.setEvaluator(new StringPosition());
      S.StringRepeat.setEvaluator(new StringRepeat());
      S.StringReplace.setEvaluator(new StringReplace());
      S.StringReverse.setEvaluator(new StringReverse());
      S.StringRiffle.setEvaluator(new StringRiffle());
      S.StringSplit.setEvaluator(new StringSplit());
      S.StringTake.setEvaluator(new StringTake());
      S.StringTemplate.setEvaluator(new StringTemplate());
      S.StringToByteArray.setEvaluator(new StringToByteArray());
      S.StringTrim.setEvaluator(new StringTrim());
      S.SyntaxLength.setEvaluator(new SyntaxLength());
      S.TemplateApply.setEvaluator(new TemplateApply());
      S.TemplateIf.setEvaluator(new TemplateIf());
      S.TemplateSlot.setEvaluator(new TemplateSlot());
      S.TextString.setEvaluator(new TextString());
      S.ToCharacterCode.setEvaluator(new ToCharacterCode());
      S.ToLowerCase.setEvaluator(new ToLowerCase());
      S.ToString.setEvaluator(new ToString());
      S.ToUnicode.setEvaluator(new ToUnicode());
      S.ToUpperCase.setEvaluator(new ToUpperCase());
      S.UpperCaseQ.setEvaluator(new UpperCaseQ());

      TeXParser.initialize();
      // if (!Config.FUZZY_PARSER) {
        S.ToExpression.setEvaluator(new ToExpression());
      // }
    }
  }

  private static class BaseDecode extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      String str = ast.arg1().toString();
      try {
        byte[] bArray = Base64.getDecoder().decode(str.toString());
        return ByteArrayExpr.newInstance(bArray);
      } catch (IllegalArgumentException iae) {
        //
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class BaseEncode extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof ByteArrayExpr) {
        byte[] bArray = (byte[]) ((IDataExpr) arg1).toData();
        if (bArray.length == 0) {
          return F.$str("");
        }

        String str = Base64.getEncoder().encodeToString(bArray);
        return F.$str(str);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class ByteArrayToString extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof ByteArrayExpr) {
        byte[] bArray = (byte[]) ((IDataExpr) arg1).toData();
        if (bArray.length == 0) {
          return F.$str("");
        }

        String str = new String(bArray, StandardCharsets.UTF_8);
        return F.$str(str);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static class Characters extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      final String str = ast.arg1().toString();
      return F.mapRange(0, str.length(), i -> F.$str(str.charAt(i)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>CharacterRange(min-character, max-character)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes a list of character strings from <code>min-character</code> to <code>max-character
   * </code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * The printable ASCII characters:
   *
   * <pre>
   * <code>&gt;&gt; CharacterRange(&quot; &quot;, &quot;~&quot;)
   * { ,!,&quot;,#,$,%,&amp;,',(,),*,+,,,-,.,/,0,1,2,3,4,5,6,7,8,9,:,;,&lt;,=,&gt;,?,@,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,[,\,],^,_,`,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,{,|,},~}
   *
   * </code>
   * </pre>
   */
  private static class CharacterRange extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX) || !(ast.arg2() instanceof IStringX)) {
        if (!(ast.arg1().isInteger()) || !(ast.arg2().isInteger())) {
          // Arguments `1` and `2` of `3` should be either non-negative integers or one-character
          // strings.
          return IOFunctions.printMessage(ast.topHead(), "argtype",
              F.list(ast.arg1(), ast.arg2(), ast.topHead()), engine);
        } else {
          int from = ast.arg1().toIntDefault();
          int to = ast.arg2().toIntDefault();
          if (from < 0 || to < 0) {
            // Arguments `1` and `2` of `3` should be either non-negative integers or one-character
            // strings.
            return IOFunctions.printMessage(ast.topHead(), "argtype",
                F.list(ast.arg1(), ast.arg2(), ast.topHead()), engine);
          }
          int size = to - from + 1;
          if (size <= 0) {
            return F.CEmptyList;
          }
          return F.mapRange(from, to + 1, i -> F.$str((char) i));
        }
      } else {
        String str1 = ast.arg1().toString();
        String str2 = ast.arg2().toString();
        if (str1.length() != 1 || str2.length() != 1) {
          // Arguments `1` and `2` of `3` should be either non-negative integers or one-character
          // strings.
          return IOFunctions.printMessage(ast.topHead(), "argtype",
              F.list(ast.arg1(), ast.arg2(), ast.topHead()), engine);
        }
        char from = str1.charAt(0);
        char to = str2.charAt(0);
        int size = (to) - (from) + 1;
        if (size <= 0) {
          return F.CEmptyList;
        }
        IASTAppendable result = F.ListAlloc(size);
        for (char i = from; i <= to; i++) {
          result.append(F.$str(i));
        }
        return result;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class FileNameJoin extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int from = 1;
      int to = 1;
      IExpr arg1 = ast.arg1();
      try {
        if (arg1.isListOfStrings()) {
          IAST list = ((IAST) arg1);
          if (list.isAST1()) {
            return list.get(1);
          }
          char separator = File.separatorChar;
          StringBuilder buf = new StringBuilder();
          for (int i = 1; i < list.size(); i++) {
            String arg = list.get(i).toString();
            if (arg.length() > 0) {
              buf.append(arg);
              if (i < list.size() - 1 //
                  && arg.charAt(arg.length() - 1) != separator) {
                buf.append(separator);
              }
            }
          }
          return F.stringx(buf.toString());
        }

      } catch (IndexOutOfBoundsException iob) {
        // from substring
        // Cannot take positions `1` through `2` in `3`.
        return IOFunctions.printMessage(ast.topHead(), "take", F.list(F.ZZ(from), F.ZZ(to), arg1),
            engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class FileNameTake extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int from = 1;
      int to = 1;
      IExpr arg1 = ast.arg1();
      try {
        if (arg1.isString()) {
          String fileName = ((IStringX) arg1).toString();
          String separator = "/";
          if (ast.isAST1()) {
            // first try Java dependent
            int index = fileName.lastIndexOf(separator);
            if (index >= 0) {
              return F.stringx(fileName.substring(index + separator.length()));
            }
            separator = File.separator;
            if (separator != null) {
              // try operator system dependent
              index = fileName.lastIndexOf(separator);
              if (index >= 0) {
                return F.stringx(fileName.substring(index + separator.length()));
              }
            }
            return arg1;
          }
        }

      } catch (IndexOutOfBoundsException iob) {
        // from substring
        // Cannot take positions `1` through `2` in `3`.
        return IOFunctions.printMessage(ast.topHead(), "take", F.list(F.ZZ(from), F.ZZ(to), arg1),
            engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>FromCharacterCode({ch1, ch2, ...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * converts the <code>ch1, ch2,...</code> character codes into a string of corresponding
   * characters.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; FromCharacterCode({97,45,51})
   * a-3
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="ToCharacterCode.md">ToCharacterCode</a>
   */
  private static class FromCharacterCode extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        final IAST list = (IAST) ast.arg1();
        return fromCharacterCode(list, ast, engine);
      }
      if (ast.arg1().isInteger()) {
        return fromCharacterCode(ast, ast, engine);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    private static IExpr fromCharacterCode(final IAST charList, final IAST fromCharacterCodeAST,
        EvalEngine engine) {
      final StringBuilder buffer = new StringBuilder(charList.size());
      char ch;
      for (int i = 1; i < charList.size(); i++) {
        if (charList.get(i).isInteger()) {
          int unicode = charList.get(i).toIntDefault();
          if (unicode < 0 || unicode >= 1114112) {
            // A character unicode, which should be a non-negative integer less than 1114112, is
            // expected at
            // position `2` in `1`.
            return IOFunctions.printMessage(S.FromCharacterCode, "notunicode",
                F.list(charList, F.ZZ(i)), engine);
          }
          ch = (char) unicode;

          buffer.append(ch);
        } else {
          return F.NIL;
        }
      }
      return StringX.valueOf(buffer);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * <code>FromLetterNumber(number)
   * </code>
   * </pre>
   *
   * <p>
   * get the corresponding characters from the English alphabet.
   *
   * <pre>
   * <code>FromLetterNumber(number, language-string)
   * </code>
   * </pre>
   *
   * <p>
   * get the corresponding characters from the languages alphabet.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; FromLetterNumber(-2, &quot;Dutch&quot;)
   * ij
   *
   * &gt;&gt; FromLetterNumber(26, &quot;Dutch&quot;)
   * ij
   *
   * &gt;&gt; FromLetterNumber(1)
   * a
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Alphabet.md">Alphabet</a>, <a href="LetterNumber.md">LetterNumber</a>
   */
  private static class FromLetterNumber extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        return ((IAST) ast.arg1()).mapThread(ast, 1);
      }
      int number = ast.arg1().toIntDefault();
      if (number != Integer.MIN_VALUE) {
        String alphabet = LATIN_ALPHABET;
        if (ast.isAST2()) {
          IExpr arg2 = ast.arg2();
          if (arg2.isList()) {
            return arg2.mapThread(ast, 2);
          }
          if (arg2.isString()) {
            String str = ast.arg2().toString();
            alphabet = ALPHABET_MAP.get(str);
            if (alphabet == null) {
              String[] strs = ALPHABET_CSV_MAP.get(str);
              if (strs == null) {
                // The alphabet `1` is not known or not available.
                IOFunctions.printMessage(ast.topHead(), "nalph", F.list(ast.arg2()), engine);
                return F.Missing(S.NotAvailable);
              }
              int length = strs.length;
              if (number > 0 && number <= length) {
                return F.stringx(strs[number - 1]);
              } else if (number < 0 && length + number >= 0) {
                return F.stringx(strs[length + number]);
              }
              return F.Missing(S.NotApplicable);
            }
          } else {
            return F.NIL;
          }
        }

        int length = alphabet.length();
        if (number > 0 && number <= length) {
          return F.stringx(alphabet.charAt(number - 1));
        } else if (number < 0 && length + number >= 0) {
          return F.stringx(alphabet.charAt(length + number));
        }
        return F.Missing(S.NotApplicable);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class HammingDistance extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isString() && arg2.isString()) {
        boolean ignoreCase = option[0].isTrue();

        org.apache.commons.text.similarity.HammingDistance hammingDistance =
            new org.apache.commons.text.similarity.HammingDistance();
        String str1 = arg1.toString();
        String str2 = arg2.toString();
        if (str1.length() != str2.length()) {
          // `1` and `2` must have the same length.
          return IOFunctions.printMessage(ast.topHead(), "idim", F.list(arg1, arg2), engine);
        }

        if (ignoreCase) {
          return F.ZZ(hammingDistance.apply(str1.toLowerCase(Locale.US), str2.toLowerCase(Locale.US)));
        }
        return F.ZZ(hammingDistance.apply(str1, str2));
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.IgnoreCase, S.False);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Alphabet()
   * </code>
   * </pre>
   *
   * <p>
   * gives the list of lowercase letters <code>a-z</code> in the English or Latin alphabet .
   *
   * <pre>
   * <code>Alphabet(language-string)
   * </code>
   * </pre>
   *
   * <p>
   * returns the languages alphabet as a list of lowercase letters.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Alphabet()
   * {a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}
   *
   * &gt;&gt; Alphabet(&quot;Dutch&quot;)
   * {a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,ij,z}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="FromLetterNumber.md">FromLetterNumber</a>, <a href="LetterNumber.md">LetterNumber</a>
   */
  private static class Alphabet extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      String alphabet = LATIN_ALPHABET;
      if (ast.isAST1()) {
        String str = ast.arg1().toString();
        alphabet = ALPHABET_MAP.get(str);
        if (alphabet == null) {
          String[] strs = ALPHABET_CSV_MAP.get(str);
          if (strs == null) {
            // The alphabet `1` is not known or not available.
            return IOFunctions.printMessage(ast.topHead(), "nalph", F.list(ast.arg1()), engine);
          }
          return F.List(strs);
        }
      }
      if (alphabet.length() > 2) {
        final String alphabetStr = alphabet;
        return F.mapRange(0, alphabet.length(), i -> F.stringx(alphabetStr.charAt(i)));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>LetterNumber(character)
   * </code>
   * </pre>
   *
   * <p>
   * returns the position of the <code>character</code> in the English alphabet.
   *
   * <pre>
   * <code>FromLetterNumber(character, language-string)
   * </code>
   * </pre>
   *
   * <p>
   * returns the position of the <code>character</code> in the languages alphabet.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; LetterNumber(&quot;b&quot;)
   * 2
   *
   * &gt;&gt; LetterNumber(&quot;B&quot;)
   * 2
   *
   * &gt;&gt; LetterNumber(&quot;ij&quot;, &quot;Dutch&quot;)
   * 26
   *
   * &gt;&gt; LetterNumber(&quot;dzs&quot;,&quot;Hungarian&quot;)
   * 8
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Alphabet.md">Alphabet</a>, <a href="FromLetterNumber.md">FromLetterNumber</a>
   */
  private static class LetterNumber extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        return ast.arg1().mapThread(ast, 1);
      }
      if (ast.arg1().isString()) {
        String characters = ast.arg1().toString().toLowerCase(Locale.US);
        String alphabet = LATIN_ALPHABET;
        if (ast.isAST2()) {
          String str = ast.arg2().toString();
          alphabet = ALPHABET_MAP.get(str);
          if (alphabet == null) {
            String[] strs = ALPHABET_CSV_MAP.get(str);
            if (strs == null) {
              // The alphabet `1` is not known or not available.
              IOFunctions.printMessage(ast.topHead(), "nalph", F.list(ast.arg2()), engine);
              return F.C0;
            }
            for (int i = 0; i < strs.length; i++) {
              if (strs[i].equals(characters)) {
                return F.ZZ(i + 1);
              }
            }
            return F.C0;
          }
        }

        if (characters.length() > 1) {
          final String alphabetStr = alphabet;
          return F.mapRange(0, characters.length(), i -> {
            final int indx = alphabetStr.indexOf(characters.charAt(i));
            return indx >= 0 ? F.ZZ(indx + 1) : F.C0;
          });
        }
        int indx = alphabet.indexOf(characters);
        if (indx >= 0) {
          return F.ZZ(indx + 1);
        }

        return F.C0;
      }
      // The argument `1` is not a string.
      return IOFunctions.printMessage(ast.topHead(), "nas", F.list(ast.arg1()), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * LetterQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * tests whether <code>expr</code> is a string, which only contains letters.
   *
   * </blockquote>
   *
   * <p>
   * A character is considered to be a letter if its general category type, provided by the Java
   * method <code>Character#getType()</code> is any of the following:
   *
   * <ul>
   * <li><code>UPPERCASE_LETTER</code>
   * <li><code>LOWERCASE_LETTER</code>
   * <li><code>TITLECASE_LETTER</code>
   * <li><code>MODIFIER_LETTER</code>
   * <li><code>OTHER_LETTER</code>
   * </ul>
   *
   * <p>
   * Not all letters have case. Many characters are letters but are neither uppercase nor lowercase
   * nor titlecase.
   */
  private static class LetterQ extends AbstractFunctionEvaluator implements Predicate<IExpr> {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString()) {
        return F.booleSymbol(test(ast.arg1()));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public boolean test(final IExpr obj) {
      final String str = obj.toString();
      char ch;
      for (int i = 0; i < str.length(); i++) {
        ch = str.charAt(i);
        if (!(Character.isLetter(ch))) {
          return false;
        }
      }
      return true;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>LowerCaseQ(str)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is <code>True</code> if the given <code>str</code> is a string which only contains lower case
   * characters.
   *
   * </blockquote>
   */
  private static class LowerCaseQ extends AbstractFunctionEvaluator implements Predicate<IExpr> {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkStringType(ast, 1, engine);
      if (arg1.isPresent()) {
        return F.booleSymbol(test(arg1));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public boolean test(final IExpr obj) {
      final String str = obj.toString();
      char ch;
      for (int i = 0; i < str.length(); i++) {
        ch = str.charAt(i);
        if (!(Character.isLowerCase(ch))) {
          return false;
        }
      }
      return true;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>EditDistance(a, b)
   * </code>
   * </pre>
   *
   * <p>
   * returns the Levenshtein distance of <code>a</code> and <code>b</code>, which is defined as the
   * minimum number of insertions, deletions and substitutions on the constituents of <code>a
   * </code> and <code>b</code> needed to transform one into the other.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Wikipedia - Levenshtein
   * distance</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; EditDistance(&quot;kitten&quot;, &quot;kitchen&quot;)
   * 2
   *
   * &gt;&gt; EditDistance(&quot;abc&quot;, &quot;ac&quot;)
   * 1
   * &gt;&gt; EditDistance(&quot;abc&quot;, &quot;acb&quot;)
   * 2
   *
   * &gt;&gt; EditDistance(&quot;azbc&quot;, &quot;abxyc&quot;)
   * 3
   * </code>
   * </pre>
   *
   * <p>
   * The <code>IgnoreCase</code> option makes <code>EditDistance</code> ignore the case of letters:
   *
   * <pre>
   * <code>&gt;&gt; EditDistance(&quot;time&quot;, &quot;Thyme&quot;)
   * 3
   *
   * &gt;&gt; EditDistance(&quot;time&quot;, &quot;Thyme&quot;, IgnoreCase -&gt; True)
   * 2
   * </code>
   * </pre>
   */
  private static final class EditDistance extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isString() && arg2.isString()) {
        boolean ignoreCase = option[0].isTrue();

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        if (ignoreCase) {
          return F.ZZ(levenshteinDistance.apply(arg1.toString().toLowerCase(Locale.US),
              arg2.toString().toLowerCase(Locale.US)));
        }
        return F.ZZ(levenshteinDistance.apply(arg1.toString(), arg2.toString()));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.IgnoreCase, S.False);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>PrintableASCIIQ(str)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if all characters in <code>str</code> are ASCII characters.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/ASCII">Wikipedia - ASCII</a>
   * <li><a href="https://en.wikipedia.org/wiki/UTF-8">Wikipedia - UTF-8</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; PrintableASCIIQ(&quot;Symja&quot;)
   * True
   * </code>
   * </pre>
   */
  private static class PrintableASCIIQ extends AbstractFunctionEvaluator
      implements Predicate<IExpr> {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkStringType(ast, 1, engine);
      if (arg1.isPresent()) {
        return F.booleSymbol(test(arg1));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }

    @Override
    public boolean test(final IExpr obj) {
      final String str = obj.toString();
      if (str.length() == 0) {
        return true;
      }
      return CharMatcher.inRange('\u0020', '\u007E').matchesAllOf(str);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>StringCases(string, pattern)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives all occurences of <code>pattern</code> in <code>string</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; StringCases(&quot;12341235678&quot;, &quot;123&quot; | &quot;78&quot;)
   * {123,123,78}
   *
   * &gt;&gt; StringCases(&quot;a#ä_123&quot;, WordCharacter)
   * {a,ä,1,2,3}
   *
   * StringCases(&quot;a#ä_123&quot;, LetterCharacter)
   * {a,ä}
   * </code>
   * </pre>
   */
  private static class StringCases extends AbstractCoreFunctionOptionEvaluator {

    @Override
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {

      if (argSize >= 2) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return arg1.mapThread(ast, 1);
        }
        if (arg1.isString()) {
          boolean ignoreCase = option[0].isTrue();

          String str = ((IStringX) arg1).toString();
          IExpr arg2 = ast.arg2();
          if (!arg2.isList()) {
            arg2 = F.list(arg2);
          }
          IAST list = (IAST) arg2;
          IASTAppendable result = F.ListAlloc();
          for (int i = 1; i < list.size(); i++) {
            IExpr arg = list.get(i);

            Map<ISymbol, String> groups = new HashMap<ISymbol, String>();
            java.util.regex.Pattern pattern =
                toRegexPattern(arg, true, ignoreCase, ast, groups, engine);
            if (pattern == null) {
              return F.NIL;
            }
            Matcher m = pattern.matcher(str);
            while (m.find()) {
              String s = m.group();
              result.append(F.$str(s));
            }
          }
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // TODO S.Overlaps
      setOptions(newSymbol, S.IgnoreCase, S.False);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>StringCount(string, pattern)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * counts all ocurences of <code>pattern</code> in <code>string</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; StringCount(&quot;a#ä_123&quot;, WordCharacter)
   * 5
   *
   * &gt;&gt; StringCount(&quot;a#ä_123&quot;, LetterCharacter)
   * 2
   * </code>
   * </pre>
   */
  private static class StringCount extends AbstractCoreFunctionOptionEvaluator {

    @Override
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {

      if (argSize >= 2) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return arg1.mapThread(ast, 1);
        }
        if (arg1.isString()) {
          boolean ignoreCase = option[0].isTrue();

          String str = ((IStringX) arg1).toString();
          IExpr arg2 = ast.arg2().makeList();
          IAST list = (IAST) arg2;
          int counter = 0;
          for (int i = 1; i < list.size(); i++) {
            IExpr arg = list.get(i);

            Map<ISymbol, String> groups = new HashMap<ISymbol, String>();
            java.util.regex.Pattern pattern =
                toRegexPattern(arg, true, ignoreCase, ast, groups, engine);
            if (pattern == null) {
              return F.NIL;
            }
            Matcher m = pattern.matcher(str);
            while (m.find()) {
              counter++;
            }
          }
          return F.ZZ(counter);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.IgnoreCase, S.False);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>StringContainsQ(str1, str2)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return a list of matches for <code>&quot;p1&quot;, &quot;p2&quot;,...</code> list of strings in
   * the string <code>str</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; StringContainsQ({&quot;the quick brown fox&quot;, &quot;jumps&quot;, &quot;over the lazy dog&quot;}, &quot;the&quot;)
   * {True,False,True}
   * </code>
   * </pre>
   */
  private static class StringContainsQ extends AbstractCoreFunctionOptionEvaluator {

    @Override
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {
      if (argSize >= 2) {
        boolean ignoreCase = option[0].isTrue();
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return arg1.mapThread(ast, 1);
        }
        if (arg1.isString() && !ast.arg2().isRuleAST()) {
          IExpr arg2 = ast.arg2();

          Map<ISymbol, String> groups = new HashMap<ISymbol, String>();
          java.util.regex.Pattern pattern =
              toRegexPattern(arg2, true, ignoreCase, ast, groups, engine);
          if (pattern == null) {
            return F.NIL;
          }
          String s1 = arg1.toString();
          java.util.regex.Matcher matcher = pattern.matcher(s1);
          if (matcher.find()) {
            return S.True;
          }
          return S.False;
        }
        return F.NIL;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.IgnoreCase, S.False);
    }
  }

  private static class StringDrop extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int from = 1;
      int to = 1;
      try {
        if (ast.arg1().isString()) {
          String s = ast.arg1().toString();
          from = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
          if (from >= 0) {
            from++;
            to = s.length();
          } else {
            to = s.length() + from;
            from = 1;
          }
          return F.$str(s.substring(from - 1, to));
        }
      } catch (IndexOutOfBoundsException iob) {
        // from substring
        // Cannot drop positions `1` through `2` in `3`.
        return IOFunctions.printMessage(ast.topHead(), "drop",
            F.list(F.ZZ(from - 1), F.ZZ(to), ast.arg1()), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class StringExpression extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      StringBuilder buf = new StringBuilder();
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isString()) {
          buf.append(arg.toString());
        } else {
          return F.NIL;
        }
      }
      return F.$str(buf.toString());
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.FLAT);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  private static class StringFormat extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      if (arg1.isString()) {
        String input = arg1.toString();
        CsvParserSettings settings = new CsvParserSettings();
        settings.detectFormatAutomatically();
        CsvParser parser = new CsvParser(settings);
        parser.beginParsing(new StringReader(input));
        CsvFormat format = parser.getDetectedFormat();
        parser.stopParsing();

        char delimiter = format.getDelimiter();
        switch (delimiter) {
          case ',':
            return F.stringx("CSV");
          case '\t':
            return F.stringx("TSV");
          case ' ':
            int index = input.indexOf('\n');
            if (index >= 0) {
              return F.stringx("Table");
            }
            return F.stringx("Text");
        }
        return F.stringx(delimiter);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class StringFreeQ extends AbstractCoreFunctionOptionEvaluator {

    @Override
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {

      if (argSize >= 2) {
        boolean ignoreCase = option[0].isTrue();

        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return arg1.mapThread(ast, 1);
        }
        if (!arg1.isString()) {
          // String or list of strings expected at position `1` in `2`.
          return IOFunctions.printMessage(ast.topHead(), "strse", F.list(F.C1, ast), engine);
        }

        IExpr arg2 = ast.arg2();
        if (arg2.isList()) {
          IAST list = (IAST) arg2;
          for (int i = 1; i < list.size(); i++) {
            IExpr temp = stringFreeQ(ast, arg1, list.get(i), ignoreCase, engine);
            if (temp.isTrue()) {
              continue;
            }
            return temp;
          }
          return S.True;
        }
        return stringFreeQ(ast, arg1, arg2, ignoreCase, engine);
      }
      return F.NIL;
    }

    private static IExpr stringFreeQ(IAST ast, IExpr arg1, IExpr arg2, boolean ignoreCase,
        EvalEngine engine) {
      Map<ISymbol, String> groups = new HashMap<ISymbol, String>();
      java.util.regex.Pattern pattern = toRegexPattern(arg2, true, ignoreCase, ast, groups, engine);
      if (pattern == null) {
        return F.NIL;
      }
      String s1 = arg1.toString();
      java.util.regex.Matcher matcher = pattern.matcher(s1);
      if (matcher.find()) {
        return S.False;
      }
      return S.True;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.IgnoreCase, S.False);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>StringInsert(string, new-string, position)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a string with <code>new-string</code> inserted starting at <code>position</code> in
   * <code>string</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>StringInsert(string, new-string, -position)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a string with <code>new-string</code> inserted starting at <code>position</code> from
   * the end of <code>string</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>StringInsert(string, new-string, {pos1, pos2,...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a string with <code>new-string</code> inserted at each position <code>posN</code> in
   * <code>string</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>StringInsert({str1, strr2,...}, new-string, position)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the list of results for each of the strings <code>strN</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; StringInsert({&quot;&quot;, &quot;Symja&quot;}, &quot;X&quot;, {1, 1, -1})
   * {XXX,XXSymjaX}
   * </code>
   * </pre>
   */
  private static class StringInsert extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (!arg1.isString()) {
        return F.NIL;
      }
      IExpr arg2 = ast.arg2();
      if (!arg2.isString()) {
        return F.NIL;
      }
      String str1 = ((IStringX) arg1).toString();
      String str2 = ((IStringX) arg2).toString();
      int[] listOfInts;
      if (ast.arg3().isList()) {
        listOfInts = Validate.checkListOfInts(ast, ast.arg3(), -str1.length() - 1,
            str1.length() + 1, engine);
      } else {
        int pos = ast.arg3().toIntDefault();
        if (Math.abs(pos) > str1.length() + 1) {
          return IOFunctions.printMessage(ast.topHead(), "ins", F.list(ast.arg3(), arg1), engine);
        }
        listOfInts = new int[] {pos};
      }
      if (listOfInts == null) {
        return F.NIL;
      }
      try {
        StringBuilder buf = new StringBuilder(str1.length() + str2.length() * listOfInts.length);
        for (int i = 0; i < listOfInts.length; i++) {
          if (listOfInts[i] < 0) {
            listOfInts[i] = str1.length() + listOfInts[i] + 2;
          } else if (listOfInts[i] == 0) {
            return IOFunctions.printMessage(ast.topHead(), "ins", F.list(F.C0, arg1), engine);
          }
        }
        Arrays.sort(listOfInts);
        int lastPos = 0;
        for (int i = 0; i < listOfInts.length; i++) {
          buf.append(str1.substring(lastPos, listOfInts[i] - 1));
          lastPos = listOfInts[i] - 1;
          buf.append(str2);
        }
        buf.append(str1.substring(lastPos));
        return F.$str(buf.toString());
      } catch (RuntimeException rex) {
        // example java.lang.StringIndexOutOfBoundsException
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
        return F.NIL;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>StringJoin(str1, str2, ... strN)
   * </code>
   * </pre>
   *
   * <p>
   * or
   *
   * <pre>
   * <code>str1 &lt;&gt; str2 &lt;&gt;  ... &lt;&gt; strN
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the concatenation of the strings <code>str1, str2, ... strN</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; &quot;Java&quot; &lt;&gt; ToString(8)
   * Java8
   *
   * &gt;&gt; StringJoin(&quot;Java&quot;, ToString(8))
   * Java8
   *
   * &gt;&gt; StringJoin({&quot;a&quot;, &quot;b&quot;})// InputForm
   * &quot;ab&quot;
   * </code>
   * </pre>
   */
  private static class StringJoin extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IAST list = ast;
      if (ast.size() > 1) {
        if (ast.isAST1()) {
          IExpr arg1 = ast.arg1();
          if (arg1.isList()) {
            list = (IAST) arg1;
          } else {
            if (arg1.isString()) {
              return arg1;
            }
            // String expected at position `1` in `2`.
            return IOFunctions.printMessage(ast.topHead(), "string", F.list(F.C1, ast), engine);
          }
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i < list.size(); i++) {
          if (list.get(i).isString()) {
            buf.append(list.get(i).toString());
          } else {
            // String expected at position `1` in `2`.
            return IOFunctions.printMessage(ast.topHead(), "string", F.list(F.ZZ(i), ast), engine);
          }
        }
        return F.$str(buf.toString());
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>StringLength(string)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the length of <code>string</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; StringLength(&quot;symja&quot;)
   * 5
   *
   * &gt;&gt; StringLength[{&quot;a&quot;, &quot;bc&quot;}]
   * {1, 2}
   * </code>
   * </pre>
   */
  private static class StringLength extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString()) {
        return F.ZZ(ast.arg1().toString().length());
      }
      // String expected at position `1` in `2`.
      return IOFunctions.printMessage(ast.topHead(), "string", F.list(F.C1, ast), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>StringMatchQ(string, regex-pattern)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * check if the regular expression <code>regex-pattern</code> matches the <code>string</code>.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Regular_expression">Wikipedia - Regular
   * expression</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; StringMatchQ({&quot;ExpandAll&quot;, &quot;listable&quot;, &quot;test&quot;}, RegularExpression(&quot;li(.+?)le&quot;))
   * {False,True,False}
   *
   * &gt;&gt; StringMatchQ(&quot;15a94xcZ6&quot;, (DigitCharacter | LetterCharacter)..)
   * True
   * </code>
   * </pre>
   */
  private static class StringMatchQ extends AbstractCoreFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {

      if (argSize >= 2) {
        boolean ignoreCase = option[0].isTrue();

        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return arg1.mapThread(ast, 1);
        }
        if (!arg1.isString()) {
          // String or list of strings expected at position `1` in `2`.
          return IOFunctions.printMessage(ast.topHead(), "strse", F.list(F.C1, ast), engine);
        }
        IExpr arg2 = ast.arg2();

        Map<ISymbol, String> groups = new HashMap<ISymbol, String>();
        java.util.regex.Pattern pattern =
            toRegexPattern(arg2, true, ignoreCase, ast, groups, engine);
        if (pattern == null) {
          return F.NIL;
        }
        String s1 = arg1.toString();
        try {
          java.util.regex.Matcher matcher = pattern.matcher(s1);
          if (matcher.matches()) {
            return S.True;
          }
          return S.False;
        } catch (StackOverflowError soe) {
          // Regex expression `1` error message: `2`.
          IOFunctions.printMessage(ast.topHead(), "zzregex",
              F.List(arg2, F.stringx("StackOverflowError")), engine);
          return S.False;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.IgnoreCase, S.False);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>StringPart(str, pos)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the character at position <code>pos</code> from the <code>str</code> string expression.
   *
   * </blockquote>
   *
   * <pre>
   * <code>StringPart(str, {pos1, pos2, pos3,....})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the characters at the position in the list <code>{pos1, pos2, pos3,....}</code> from the
   * <code>str</code> string expression.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; StringPart(&quot;1234567890&quot;,  {1, 3, 10})
   * {1,3,0}
   * </code>
   * </pre>
   */
  private static class StringPart extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      if (!ast.arg1().isString()) {
        return F.NIL;
      }

      IExpr arg2 = ast.arg2();
      if (arg2.isList()) {
        return arg2.mapThread(ast, 2);
      }
      String str = ((IStringX) ast.arg1()).toString();
      int part = arg2.toIntDefault();
      if (part > 0) {
        if (part > str.length()) {
          // Part `1` of `2` does not exist.
          return IOFunctions.printMessage(ast.topHead(), "partw", F.list(F.ZZ(part), ast.arg1()),
              engine);
        }
        return F.stringx(str.charAt(part - 1));
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class StringPosition extends AbstractCoreFunctionOptionEvaluator {

    @Override
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {

      if (argSize >= 2) {
        int maxOccurences = Integer.MAX_VALUE;
        boolean ignoreCase = option[0].isTrue();
        if (argSize > 2) {
          maxOccurences = ast.arg3().toIntDefault();
          if (maxOccurences < 0) {
            maxOccurences = Integer.MAX_VALUE;
          }
        }

        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return arg1.mapThread(ast, 1);
        }
        if (!arg1.isString()) {
          // String or list of strings expected at position `1` in `2`.
          return IOFunctions.printMessage(ast.topHead(), "strse", F.list(F.C1, ast), engine);
        }

        IExpr arg2 = ast.arg2();
        IASTAppendable result = F.ListAlloc();
        try {
          if (arg2.isList()) {
            IAST list = (IAST) arg2;
            for (int i = 1; i < list.size(); i++) {
              IExpr temp =
                  stringPosition(ast, arg1, list.get(i), maxOccurences, ignoreCase, result, engine);
              if (temp.isNIL()) {
                return F.NIL;
              }
              if (maxOccurences < result.size()) {
                return result;
              }
            }
            return result;
          }
          return stringPosition(ast, arg1, arg2, maxOccurences, ignoreCase, result, engine);
        } catch (StackOverflowError soe) {
          return F.NIL;
        }
      }
      return F.NIL;
    }

    private static IExpr stringPosition(IAST ast, IExpr arg1, IExpr arg2, int maxOccurences,
        boolean ignoreCase, IASTAppendable result, EvalEngine engine) {

      Map<ISymbol, String> groups = new HashMap<ISymbol, String>();
      java.util.regex.Pattern pattern = toRegexPattern(arg2, true, ignoreCase, ast, groups, engine);
      if (pattern == null) {
        return F.NIL;
      }
      String s1 = arg1.toString();
      java.util.regex.Matcher matcher = pattern.matcher(s1);
      while (matcher.find()) {
        if (maxOccurences < result.size()) {
          return result;
        }
        result.append(F.list(F.ZZ(matcher.start() + 1), F.ZZ(matcher.end())));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.IgnoreCase, S.False);
    }
  }

  private static class StringRepeat extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString()) {
        String str = ast.arg1().toString();
        int n = ast.arg2().toIntDefault();
        if (n < 0) {
          // Positive integer expected at position `2` in `1`.
          return IOFunctions.printMessage(ast.topHead(), "intp", F.List(ast, F.C2), engine);
        }
        int max = Integer.MAX_VALUE;
        if (ast.isAST3()) {
          max = ast.arg3().toIntDefault();
          if (max < 0) {
            // Positive integer expected at position `2` in `1`.
            return IOFunctions.printMessage(ast.topHead(), "intp", F.List(ast, F.C3), engine);
          }
        }

        if (n == 0 || max == 0 || str.length() == 0) {
          return F.CEmptyString;
        }
        int maxLength = n * str.length();
        if (maxLength < 0 || maxLength > Config.MAX_AST_SIZE) {
          throw new ASTElementLimitExceeded(maxLength);
        }
        StringBuilder buf = new StringBuilder(maxLength);
        for (int i = 0; i < n; i++) {
          buf.append(str);
          if (buf.length() > max) {
            break;
          }
        }
        if (max < maxLength) {
          return F.$str(buf.substring(0, max));
        }
        return F.$str(buf.toString());


      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

  }
  /**
   *
   *
   * <pre>
   * <code>StringReplace(string, fromStr -&gt; toStr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * replaces each occurrence of <code>fromStr</code> with <code>toStr</code> in <code>string
   * </code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; StringReplace(&quot;the quick brown fox jumps over the lazy dog&quot;, &quot;the&quot; -&gt; &quot;a&quot;)
   * a quick brown fox jumps over a lazy dog
   *
   * &gt;&gt; StringReplace(&quot;01101100010&quot;, &quot;01&quot; .. -&gt; &quot;x&quot;)
   * x1x100x0
   * </code>
   * </pre>
   */
  private static class StringReplace extends AbstractCoreFunctionOptionEvaluator {

    @Override
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {

      if (argSize >= 2) {
        boolean ignoreCase = option[0].isTrue();
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return arg1.mapThread(ast, 1);
        }
        if (!arg1.isString()) {
          return F.NIL;
        }
        String str = ((IStringX) arg1).toString();
        IExpr arg2 = ast.arg2();
        if (!arg2.isListOfRules(false)) {
          if (arg2.isRuleAST()) {
            arg2 = F.list(arg2);
          } else {
            return F.NIL;
          }
        }
        IAST list = (IAST) arg2;
        for (int i = 1; i < list.size(); i++) {
          final IAST rule = (IAST) list.get(i);
          final IExpr ruleLHS = rule.arg1();
          final IExpr ruleRHS = rule.arg2();

          // see github #221 - use Java regex - named capturing groups
          Map<ISymbol, String> namedRegexGroups = new HashMap<ISymbol, String>();
          if (ruleLHS.isCondition()) {
            final IAST condition = (IAST) ruleLHS;
            final IExpr conditionPattern = condition.arg1();
            final IExpr conditionTest = condition.arg2();
            java.util.regex.Pattern pattern =
                toRegexPattern(conditionPattern, true, ignoreCase, ast, namedRegexGroups, engine);
            if (pattern == null) {
              return F.NIL;
            }
            str = stringReplaceCondition(str, conditionTest, ruleRHS, pattern, namedRegexGroups,
                engine);
          } else {
            java.util.regex.Pattern pattern =
                toRegexPattern(ruleLHS, true, ignoreCase, ast, namedRegexGroups, engine);
            if (pattern == null) {
              return F.NIL;
            }
            str = stringReplace(str, ruleRHS, pattern, namedRegexGroups, engine);
          }
        }
        return F.$str(str);
      }
      return F.NIL;
    }

    /**
     * @param str
     * @param ruleRHS
     * @param pattern
     * @param namedRegexGroups maps a pattern symbol to the regex name
     * @param engine
     * @return
     */
    private static String stringReplace(String str, final IExpr ruleRHS,
        java.util.regex.Pattern pattern, Map<ISymbol, String> namedRegexGroups,
        final EvalEngine engine) {

      Matcher matcher = pattern.matcher(str);
      if (!ruleRHS.isString() && namedRegexGroups.size() > 0 && matcher.find()) {
        StringBuffer buf = new StringBuffer(str.length() + 16);
        do {
          IExpr replacedRHS = ruleRHS;
          replacedRHS = replaceGroups(replacedRHS, matcher, namedRegexGroups);
          IExpr temp = engine.evaluate(replacedRHS);
          matcher.appendReplacement(buf, temp.toString());
        } while (matcher.find());
        matcher.appendTail(buf);
        return buf.toString();
      }

      IExpr temp = engine.evaluate(ruleRHS);
      return pattern.matcher(str).replaceAll(temp.toString());
    }

    /**
     * @param str
     * @param conditionTest
     * @param ruleRHS
     * @param pattern
     * @param namedRegexGroups maps a pattern symbol to the regex name
     * @param engine
     * @return
     */
    private static String stringReplaceCondition(String str, IExpr conditionTest,
        final IExpr ruleRHS, java.util.regex.Pattern pattern, Map<ISymbol, String> namedRegexGroups,
        final EvalEngine engine) {

      Matcher matcher = pattern.matcher(str);
      if (!ruleRHS.isString() && namedRegexGroups.size() > 0 && matcher.find()) {
        StringBuffer buf = new StringBuffer(str.length() + 16);
        do {
          IExpr replacedTest = conditionTest;
          replacedTest = replaceGroups(replacedTest, matcher, namedRegexGroups);
          if (engine.evalTrue(replacedTest)) {
            IExpr replacedRHS = ruleRHS;
            replacedRHS = replaceGroups(replacedRHS, matcher, namedRegexGroups);
            IExpr temp = engine.evaluate(replacedRHS);
            matcher.appendReplacement(buf, temp.toString());
          }
        } while (matcher.find());
        matcher.appendTail(buf);
        return buf.toString();
      }

      IExpr temp = engine.evaluate(ruleRHS);
      return pattern.matcher(str).replaceAll(temp.toString());
    }

    /**
     * Replace all pattern symbols in <code>expr</code> with the value from the named regex group.
     *
     * @param expr
     * @param matcher
     * @param namedRegexGroups maps a pattern symbol to the regex name
     * @return
     */
    private static IExpr replaceGroups(IExpr expr, Matcher matcher,
        Map<ISymbol, String> namedRegexGroups) {

      for (Map.Entry<ISymbol, String> group : namedRegexGroups.entrySet()) {
        String groupValue = matcher.group(group.getValue());
        if (groupValue != null) {
          expr = F.subs(expr, group.getKey(), F.stringx(groupValue));
        }
      }
      return expr;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.IgnoreCase, S.False);
    }
  }

  private static class StringReverse extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      String str = arg1.toString();
      return F.stringx(new StringBuilder(str).reverse().toString());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static class StringRiffle extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      String sep1 = " ";
      String sep2 = "\n";
      String left = "";
      String right = "";
      boolean isListOfLists = arg1.isListOfLists();
      if (isListOfLists) {
        sep1 = "\n";
        sep2 = " ";
      }
      if (ast.size() >= 3) {
        IExpr arg2 = ast.arg2();
        if (arg2.isString()) {
          sep1 = arg2.toString();
        } else if (arg2.isAST(S.List, 4)) {
          IAST list = (IAST) arg2;
          left = list.arg1().toString();
          sep1 = list.arg2().toString();
          right = list.arg3().toString();
        } else {
          // String expected at position `1` in `2`.
          return IOFunctions.printMessage(ast.topHead(), "string", F.list(F.C2, ast), engine);
        }
      }
      if (ast.isAST3()) {
        IExpr arg3 = ast.arg3();
        if (arg3.isString()) {
          sep2 = arg3.toString();
        } else {
          // String expected at position `1` in `2`.
          return IOFunctions.printMessage(ast.topHead(), "string", F.list(F.C3, ast), engine);
        }
      }
      if (isListOfLists) {
        StringBuilder buf = new StringBuilder();
        IAST list1 = (IAST) arg1;
        buf.append(left);
        for (int i = 1; i < list1.size(); i++) {
          IAST row = (IAST) list1.get(i);
          for (int j = 1; j < row.size(); j++) {
            TextString.of(row.get(j), buf);
            if (j < row.size() - 1) {
              buf.append(sep2);
            }
          }
          if (i < list1.size() - 1) {
            buf.append(sep1);
          }
        }
        buf.append(right);
        return F.stringx(buf.toString());
      } else if (arg1.isList()) {
        StringBuilder buf = new StringBuilder();
        IAST list1 = (IAST) arg1;
        buf.append(left);
        for (int j = 1; j < list1.size(); j++) {
          TextString.of(list1.get(j), buf);
          if (j < list1.size() - 1) {
            buf.append(sep1);
          }
        }
        buf.append(right);
        return F.stringx(buf.toString());
      }
      LOGGER.log(engine.getLogLevel(), "StringRiffle: list expected as first argument");
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>StringSplit(str)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * split the string <code>str</code> by whitespaces into a list of strings.
   *
   * </blockquote>
   *
   * <pre>
   * <code>StringSplit(str1, str2)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * split the string <code>str1</code> by <code>str2</code> into a list of strings.
   *
   * </blockquote>
   *
   * <pre>
   * <code>StringSplit(str1, RegularExpression(str2))
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * split the string <code>str1</code> by the regular expression <code>str2</code> into a list of
   * strings.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Regular_expression">Wikipedia - Regular
   * expression</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; StringSplit(&quot;128.0.0.1&quot;, &quot;.&quot;)
   * {128,0,0,1}
   *
   * &gt;&gt; StringSplit(&quot;128.0.0.1&quot;, RegularExpression(&quot;\\W+&quot;))
   * {128,0,0,1}
   *
   * &gt;&gt; StringSplit(&quot;a1b2.2c0.333d4444.0efghijlkm&quot;, NumberString)
   * {a,b,c,d,efghijlkm}
   * </code>
   * </pre>
   */
  private static class StringSplit extends AbstractCoreFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      if (!arg1.isString()) {
        if (arg1.isListOfStrings()) {
          return arg1.mapThread(ast, 1);
        }
        // String or list of strings expected at position `1` in `2`.
        return IOFunctions.printMessage(ast.topHead(), "strse", F.list(F.C1, ast), engine);
      }
      String str1 = ((IStringX) arg1).toString().trim();
      if (ast.isAST1()) {
        return splitList(str1, str1.split("\\s+"));
      }
      boolean ignoreCase = option[0].isTrue();

      if (argSize >= 2) {
        IExpr arg2 = ast.arg2();
        if (arg2.isList()) {
          if (!arg2.isListOfRules()) {
            // rewrite lists on first level to Alternatives
            arg2 = ((IAST) arg2).setAtCopy(0, S.Alternatives);
          }
        }
        Map<ISymbol, String> groups = new HashMap<ISymbol, String>();
        java.util.regex.Pattern pattern =
            toRegexPattern(arg2, true, ignoreCase, ast, groups, engine);
        if (pattern == null) {
          return F.NIL;
        }
        return splitList(str1, pattern.split(str1));
      }
      return F.NIL;
    }

    private static IExpr splitList(String str, String[] result) {
      if (result == null || str.length() == 0) {
        return F.CEmptyList;
      }
      return F.mapRange(0, result.length, i -> {
        // TODO empty strings are not generally a problem
        // if (result[i].length() == 0) {
        // return F.NIL;
        // }
        return F.stringx(result[i]);
      });
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.IgnoreCase, S.False);
    }
  }

  private static class StringTake extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int from = 1;
      int to = 1;
      IExpr arg1 = ast.arg1();
      try {
        if (!arg1.isString()) {
          if (arg1.isListOfStrings()) {
            return arg1.mapThread(ast, 1);
          }
          // String or list of strings expected at position `1` in `2`.
          return IOFunctions.printMessage(ast.topHead(), "strse", F.list(F.C1, ast), engine);
        } else {
          // final ISequence[] sequ = Sequence.createSequences(ast, 2, "take", engine);
          // if (sequ == null) {
          // return F.NIL;
          // }
          String s = arg1.toString();
          IExpr arg2 = ast.arg2();
          if (arg2.isAST(S.UpTo, 2)) {
            int upTo = Validate.checkUpTo((IAST) arg2, engine);
            if (upTo == Integer.MIN_VALUE) {
              return F.NIL;
            }
            upTo = s.length() > upTo ? upTo : s.length();
            return F.$str(s.substring(0, upTo));
          } else if (arg2.equals(S.All)) {
            return arg1;
          } else if (arg2.isList()) {
            // int[][] sequ =
            // Validate.checkListOfSequenceSpec(ast, arg2, 2,
            // s.length(),Integer.MIN_VALUE, Integer.MAX_VALUE, engine);
            // if (sequ == null || sequ.length == 0) {
            // return F.NIL;
            // }
            // if( sequ.length >1) {
            // return ((IAST) arg2).mapThread(ast, 2);
            // }
            // switch (sequ.length) {
            // case 1:
            // from = sequ[0];
            // if (from < 0) {
            // from = s.length() + from + 1;
            // }
            // to = from;
            // return F.$str(s.substring(from - 1, to));
            // case 2:
            // from = sequ[0];
            // if (from < 0) {
            // from = s.length() + from + 1;
            // }
            // to = sequ[1];
            // if (to < 0) {
            // to = s.length() + to + 1;
            // }
            // return F.$str(s.substring(from - 1, to));
            // case 3:
            // from = sequ[0];
            // if (from < 0) {
            // from = s.length() + from + 1;
            // }
            // to = sequ[1];
            // if (to < 0) {
            // to = s.length() + to + 1;
            // }
            // int step = sequ[2];
            // if (step < 0) {
            // return F.NIL;
            // }
            // if (step == 0) {
            // return ((IAST) arg2).mapThread(ast, 2);
            // }
            // StringBuilder buf = new StringBuilder();
            // while (from <= to) {
            // buf.append(s.substring(from - 1, from));
            // from += step;
            // }
            // return F.$str(buf.toString());
            // default:
            // return ((IAST) arg2).mapThread(ast, 2);
            // }

            int[] sequ =
                Validate.checkListOfInts(ast, arg2, Integer.MIN_VALUE, Integer.MAX_VALUE, engine);
            if (sequ == null || sequ.length == 0) {
              return F.NIL;
            }
            switch (sequ.length) {
              case 1:
                from = sequ[0];
                if (from < 0) {
                  from = s.length() + from + 1;
                }
                to = from;
                return F.$str(s.substring(from - 1, to));
              case 2:
                from = sequ[0];
                if (from < 0) {
                  from = s.length() + from + 1;
                }
                to = sequ[1];
                if (to < 0) {
                  to = s.length() + to + 1;
                }
                return F.$str(s.substring(from - 1, to));
              case 3:
                from = sequ[0];
                if (from < 0) {
                  from = s.length() + from + 1;
                }
                to = sequ[1];
                if (to < 0) {
                  to = s.length() + to + 1;
                }
                int step = sequ[2];
                if (step < 0) {
                  return F.NIL;
                }
                if (step == 0) {
                  return ((IAST) arg2).mapThread(ast, 2);
                }
                StringBuilder buf = new StringBuilder();
                while (from <= to) {
                  buf.append(s.substring(from - 1, from));
                  from += step;
                }
                return F.$str(buf.toString());
              default:
                return ((IAST) arg2).mapThread(ast, 2);
            }
          }
          to = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
          if (to >= 0) {

          } else {
            from = s.length() + to + 1;
            to = s.length();
            // return F.$str(s.substring(s.length() + to, s.length()));
          }
          return F.$str(s.substring(from - 1, to));
        }
      } catch (IndexOutOfBoundsException iob) {
        // from substring
        // Cannot take positions `1` through `2` in `3`.
        return IOFunctions.printMessage(ast.topHead(), "take", F.list(F.ZZ(from), F.ZZ(to), arg1),
            engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class StringToByteArray extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (!arg1.isString()) {
        return F.NIL;
      }
      String str = ast.arg1().toString();
      try {
        byte[] bArray = str.getBytes(StandardCharsets.UTF_8);
        return ByteArrayExpr.newInstance(bArray);
      } catch (IllegalArgumentException iae) {
        //
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class StringTrim extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (arg1.isString()) {
        if (ast.isAST1()) {
          return F.$str(arg1.toString().trim());
        }
        if (ast.isAST2()) {
          if (!ast.arg1().isString()) {
            return F.NIL;
          }
          String str = ((IStringX) ast.arg1()).toString();
          try {
            Map<ISymbol, String> groups = new HashMap<ISymbol, String>();
            String regex = toRegexString(ast.arg2(), true, ast, REGEX_LONGEST, groups, engine);
            if (regex != null) {
              // prepend StartOfString
              java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\A" + regex);
              str = pattern.matcher(str).replaceAll("");
              // append EndOfString
              pattern = java.util.regex.Pattern.compile(regex + "\\Z");
              str = pattern.matcher(str).replaceAll("");
              return F.$str(str);
            }
          } catch (IllegalArgumentException iae) {
            // for example java.util.regex.PatternSyntaxException
            LOGGER.debug("StringTrim.evaluate() failed", iae);
            return regexErrorHandling(ast, iae, engine);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class SyntaxLength extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (!arg1.isString()) {
        return F.NIL;
      }
      final String str = arg1.toString();
      return F.ZZ(ExprParser.syntaxLength(str, engine));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class StringTemplate extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      if (ast.isAST1()) {
        IExpr head = ast.head();
        if (head.isAST(S.StringTemplate, 2)) {
          return S.TemplateApply.of(engine, head, ast.arg1());
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return null;
    }
  }

  private static class TemplateApply extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = F.NIL;
      if (ast.isAST2()) {
        arg2 = ast.arg2();
      }
      if (arg1.isString()) {
        return IOFunctions.templateApply(arg1.toString(), arg2);
      } else if (arg1.isAST(S.StringTemplate, 2) && arg1.first().isString()) {
        return IOFunctions.templateApply(arg1.first().toString(), arg2);
      }
      return templateApplyTemplateSlot(arg1, arg2);
    }

    /**
     * Replace all {@link S#TemplateSlot} expressions in an {@link IAST} tree.
     *
     * @param templateExpr
     * @param args
     * @return
     */
    private static IExpr templateApplyTemplateSlot(IExpr templateExpr, IExpr args) {
      final Map<IExpr, IExpr> context = new HashMap<>();
      if (args.isListOrAssociation()) {
        if (args.isAssociation()) {
          IAssociation assoc = (IAssociation) args;
          for (int i = 1; i < assoc.size(); i++) {
            IAST rule = assoc.getRule(i);
            IExpr lhs = rule.arg1();
            IExpr rhs = rule.arg2();
            context.put(lhs, rhs);
          }
        } else if (args.isList()) {
          IAST list = (IAST) args;
          for (int i = 1; i < list.size(); i++) {
            IExpr expr = list.get(i);
            context.put(F.ZZ(i), expr);
          }
        }
      }
      return templateExpr.replaceAll(x -> replaceTemplateSlotFunction(x, context))
          .orElse(templateExpr);
    }

    /**
     * Lambda function for replacing all {@link S#TemplateSlot} expressions in an {@link IAST} tree.
     *
     * @param expr the current expression
     * @param map the replacement map
     * @return {@link F#NIL} if no replacement was found
     */
    private static IExpr replaceTemplateSlotFunction(IExpr expr, final Map<IExpr, IExpr> map) {
      if (expr.isASTSizeGE(S.TemplateIf, 3)) {
        IAST templateIf = (IAST) expr;
        IExpr condition = templateIf.arg1();
        IExpr thenExpr = templateIf.arg2();
        IExpr elseExpr = F.CEmptySequence;
        if (templateIf.size() > 3) {
          elseExpr = templateIf.arg3();
        }
        condition =
            condition.replaceAll(x -> replaceTemplateSlotFunction(x, map)).orElse(condition);
        boolean b = EvalEngine.get().evalTrue(condition);
        if (b) {
          return thenExpr.replaceAll(x -> replaceTemplateSlotFunction(x, map)).orElse(thenExpr);
        }
        return elseExpr.replaceAll(x -> replaceTemplateSlotFunction(x, map)).orElse(elseExpr);
      }
      if (expr.isASTSizeGE(S.TemplateSlot, 2)) {
        IAST templateSlot = (IAST) expr;
        return replaceSingleTemplateSlot(map, templateSlot);
      }
      return F.NIL;
    }

    private static IExpr replaceSingleTemplateSlot(final Map<IExpr, IExpr> map, IAST templateSlot) {
      IExpr defaultValue = F.Missing(S.SlotAbsent, templateSlot.first());
      if (templateSlot.size() > 2) {
        final OptionArgs options =
            new OptionArgs(S.TemplateSlot, templateSlot, 2, EvalEngine.get(), true);
        IExpr option = options.getOption(S.DefaultValue);
        if (option.isPresent()) {
          defaultValue = option;
        }
        IExpr insertionFunction = options.getOption(S.InsertionFunction);
        if (insertionFunction.isPresent()) {
          IExpr result = map.get(templateSlot.first());
          if (result == null) {
            if (defaultValue.isPresent()) {
              return F.unaryAST1(insertionFunction, defaultValue);
            }
            return F.NIL;
          }
          return F.unaryAST1(insertionFunction, result);
        }
      }

      IExpr result = map.get(templateSlot.first());
      return result == null ? defaultValue : result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class TemplateIf extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // IExpr arg1 = ast.arg1();
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class TemplateSlot extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  private static class TextString extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      return of(arg1);
    }

    private static IExpr of(IExpr arg1) {
      if (arg1.isString()) {
        return arg1;
      }
      return F.stringx(arg1.toString());
    }

    protected static void of(IExpr arg1, StringBuilder buf) {
      if (arg1.isString()) {
        buf.append(((IStringX) arg1).toString());
        return;
      }
      buf.append(arg1.toString());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>ToCharacterCode(string)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * converts <code>string</code> into a list of corresponding integer character codes.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; ToCharacterCode(&quot;ABCD abcd&quot;)
   * {65,66,67,68,32,97,98,99,100}
   *
   * &gt;&gt; &quot;a-3&quot; // ToCharacterCode
   * {97,45,51}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="FromCharacterCode.md">FromCharacterCode</a>
   */
  private static class ToCharacterCode extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (!arg1.isString()) {
        return F.NIL;
      }

      return toCharacterCode(ast.arg1().toString(), StandardCharsets.UTF_8);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    public static IAST toCharacterCode(final String unicodeInput, final Charset inputEncoding) {
      final String utf8String =
          new String(unicodeInput.getBytes(inputEncoding), StandardCharsets.UTF_8);
      final int length = utf8String.length();
      return F.mapRange(0, length, i -> F.ZZ(utf8String.charAt(i)));
    }
  }

  /**
   *
   *
   * <pre>
   * <code>ToExpression(&quot;string&quot;, form)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * converts the <code>string</code> given in <code>form</code> into an expression.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; ToExpression(&quot;1 + 2 - x \\times 4 \\div 5&quot;, TeXForm)
   * 3-4/5*x
   * </code>
   * </pre>
   */
  private static final class ToExpression extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr head = F.NIL;
      if (arg1.isString()) {
        ISymbol form = S.InputForm;
        if (ast.size() == 3) {
          IExpr arg2 = ast.arg2();
          if (arg2.equals(S.InputForm)) {
            form = S.InputForm;
          } else if (arg2.equals(S.TeXForm)) {
            form = S.TeXForm;
          } else {
            return F.NIL;
          }
        }
        if (ast.size() == 4) {
          head = ast.arg3();
        }
        try {
          if (form.equals(S.InputForm)) {
            ExprParser parser = new ExprParser(engine);
            IExpr temp = parser.parse(arg1.toString());
            if (head.isPresent()) {
              return F.unaryAST1(head, temp);
            }
            return temp;
          } else if (form.equals(S.TeXForm)) {
            IExpr temp = TeXSliceParser.convert(arg1.toString());
            // TeXParser texParser = new TeXParser(engine);
            // IExpr temp = texParser.toExpression(arg1.toString());
            if (head.isPresent()) {
              return F.unaryAST1(head, temp);
            }
            return temp;
          }
        } catch (RuntimeException rex) {
          LOGGER.debug("ToExpression.evaluate() failed", rex);
          return S.$Aborted;
        }
      } else {
        // `1` is not a string.
        return IOFunctions.printMessage(ast.topHead(), "nostr", F.list(ast.arg1()), engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>ToString(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * converts <code>expr</code> into a string.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; &quot;Java&quot; &lt;&gt; ToString(8)
   * Java8
   * </code>
   * </pre>
   */
  private static class ToString extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString()) {
        return ast.arg1();
      }
      return F.stringx(inputForm(ast.arg1()));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class ToLowerCase extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isString()) {
        return F.NIL;
      }
      return F.stringx(((IStringX) arg1).toLowerCase());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>ToUnicode(string)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * converts <code>string</code> into a string of corresponding unicode character codes.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; ToUnicode(&quot;123abcABC&quot;)
   * &quot;\u0031\u0032\u0033\u0061\u0062\u0063\u0041\u0042\u0043&quot;
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="FromCharacterCode.md">FromCharacterCode</a>,
   * <a href="ToCharacterCode.md">ToCharacterCode</a>
   */
  private static class ToUnicode extends AbstractFunctionEvaluator {
    private static final String UNICODE_PREFIX = "\\u";

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (!arg1.isString()) {
        return F.NIL;
      }

      return StringX.valueOf(toUnicodeString(ast.arg1().toString(), StandardCharsets.UTF_8));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    public static String toUnicodeString(final String unicodeInput, final Charset inputEncoding) {
      final StringBuilder unicodeStringBuilder = new StringBuilder();
      String unicodeString = null;

      // try {
      final String utf8String =
          new String(unicodeInput.getBytes(inputEncoding), StandardCharsets.UTF_8);
      String hexValueString = null;
      int hexValueLength = 0;
      for (int i = 0; i < utf8String.length(); i++) {
        hexValueString = Integer.toHexString(utf8String.charAt(i));
        hexValueLength = hexValueString.length();
        if (hexValueLength < 4) {
          for (int j = 0; j < (4 - hexValueLength); j++) {
            hexValueString = "0" + hexValueString;
          }
        }
        unicodeStringBuilder.append(UNICODE_PREFIX);
        unicodeStringBuilder.append(hexValueString);
      }
      unicodeString = unicodeStringBuilder.toString();
      // } catch (final UnsupportedEncodingException e) {
      // LOGGER.error("ToUnicode.toUnicodeString() failed", e);
      // }
      return unicodeString;
    }
  }

  private static class ToUpperCase extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isString()) {
        return F.NIL;
      }
      return F.stringx(((IStringX) arg1).toUpperCase());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * UpperCaseQ(str)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is <code>True</code> if the given <code>str</code> is a string which only contains upper case
   * characters.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; UpperCaseQ("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
   * True
   *
   * &gt;&gt; UpperCaseQ("ABCDEFGHIJKLMNopqRSTUVWXYZ")
   * False
   * </pre>
   */
  private static final class UpperCaseQ extends AbstractCoreFunctionEvaluator
      implements Predicate<IExpr>, IPredicate {

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      IExpr temp = Validate.checkStringType(ast, 1, engine);
      if (temp.isPresent()) {
        return F.booleSymbol(test(arg1));
      }
      return F.NIL;
    }

    @Override
    public boolean test(final IExpr obj) {
      final String str = obj.toString();
      char ch;
      for (int i = 0; i < str.length(); i++) {
        ch = str.charAt(i);
        if (!(Character.isUpperCase(ch))) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static String inputForm(final IExpr expression, boolean relaxedSyntax) {
    try {
      StringBuilder buf = new StringBuilder();
      OutputFormFactory off = OutputFormFactory.get(relaxedSyntax, false);
      off.setIgnoreNewLine(true);
      off.setInputForm(true);
      if (off.convert(buf, expression)) {
        return buf.toString();
      }
    } catch (RuntimeException rex) {
      LOGGER.debug("StringFunctions.inputForm() failed", rex);
    }
    return null;
  }

  public static String inputForm(final IExpr expression) {
    return StringFunctions.inputForm(expression, ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS);
  }

  private static IExpr regexErrorHandling(final IAST ast, IllegalArgumentException iae,
      EvalEngine engine) {
    if (iae instanceof PatternSyntaxException) {
      PatternSyntaxException pse = (PatternSyntaxException) iae;
      // Regex expression `1` error message: `2`
      return IOFunctions.printMessage(S.RegularExpression, "zzregex",
          F.list(F.$str(pse.getPattern()), F.$str(pse.getMessage())), engine);
    } else {
      LOGGER.log(engine.getLogLevel(), ast.topHead(), iae);
      return F.NIL;
    }
  }

  /**
   * Unicode version of predefined character classes and POSIX character classes are enabled in the
   * resulting regex Pattern object.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href=
   * "https://github.com/mathics/Mathics/blob/master/mathics/builtin/strings.py#L78">to_regex()
   * function</a>
   * <li><a href="https://en.wikipedia.org/wiki/Perl_Compatible_Regular_Expressions">Wikipedia -
   * Perl Compatible Regular Expression</a>
   * </ul>
   *
   * @param partOfRegex the expression which represents a regex 'piece'
   * @param abbreviatedPatterns if <code>true</code> allow 'abbreviated patterns" in strings (i.e.
   *        '\','*' and '@' operatore)
   * @param ignoreCase if <code>true</code> enables case-insensitive matching.
   * @param stringFunction the original string function, used in error messages
   * @param engine the evaluation engine
   * @return
   */
  public static java.util.regex.Pattern toRegexPattern(IExpr partOfRegex,
      boolean abbreviatedPatterns, boolean ignoreCase, IAST stringFunction,
      Map<ISymbol, String> namedRegexGroups, EvalEngine engine) {

    String regex = toRegexString(partOfRegex, abbreviatedPatterns, stringFunction, REGEX_LONGEST,
        namedRegexGroups, engine);
    if (regex != null) {
      java.util.regex.Pattern pattern;
      try {
        if (ignoreCase) {
          pattern = java.util.regex.Pattern.compile(regex,
              Pattern.UNICODE_CHARACTER_CLASS | Pattern.CASE_INSENSITIVE);
        } else {
          pattern = java.util.regex.Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
        }
        return pattern;
      } catch (IllegalArgumentException iae) {
        // for example java.util.regex.PatternSyntaxException
        LOGGER.debug("StringFunctions.toRegexPattern() failed", iae);
        regexErrorHandling(stringFunction, iae, engine);
      }
    }

    return null;
  }

  /**
   * Convert a Symja expression which represents a 'piece of a regular expression' to a Java regular
   * expression string.
   *
   * @param partOfRegex the expression which represents a regex 'piece' which must be converted to a
   *        Java regex string
   * @param abbreviatedPatterns if <code>true</code> allow 'abbreviated patterns" in strings (i.e.
   *        '\','*' and '@' operators)
   * @param stringFunction the original string function, used in error messages
   * @param shortestLongest either {@link #REGEX_LONGEST} or {@link #REGEX_SHORTEST}
   * @param groups
   * @param engine the evaluation engine
   * @return
   * @see <a href="https://en.wikipedia.org/wiki/Perl_Compatible_Regular_Expressions">Wikipedia -
   *      Perl Compatible Regular Expression</a>
   */
  private static String toRegexString(IExpr partOfRegex, boolean abbreviatedPatterns,
      IAST stringFunction, String[] shortestLongest, Map<ISymbol, String> groups,
      EvalEngine engine) {

    if (partOfRegex.isString()) {
      final String str = partOfRegex.toString();
      if (abbreviatedPatterns) {
        StringBuilder pieces = new StringBuilder();
        int beginIndex = 0;
        int endIndex = 0;
        final int len = str.length();
        while (endIndex < len) {
          char c = str.charAt(endIndex);
          if (c == '\\' && endIndex + 1 < len) {
            pieces.append(Pattern.quote(str.substring(beginIndex, endIndex)));
            pieces.append(Pattern.quote(str.substring(endIndex + 1, endIndex + 2)));
            endIndex += 2;
            beginIndex = endIndex;
          } else if (c == '*') {
            pieces.append(Pattern.quote(str.substring(beginIndex, endIndex)));
            pieces.append("(.*)");
            endIndex += 1;
            beginIndex = endIndex;
          } else if (c == '@') {
            pieces.append(Pattern.quote(str.substring(beginIndex, endIndex)));
            // one or more characters, excluding upper case letters
            pieces.append("([^A-Z]+)");
            endIndex += 1;
            beginIndex = endIndex;
          } else {
            endIndex += 1;
          }
        }
        pieces.append(Pattern.quote(str.substring(beginIndex, endIndex)));
        return pieces.toString();
      } else {
        return Pattern.quote(str);
      }
    } else if (partOfRegex.isAST(S.Characters, 2) && partOfRegex.first().isString()) {
      String str = ((IStringX) partOfRegex.first()).toString();
      return "[" + str + "]";
    } else if (partOfRegex.isAST(S.RegularExpression, 2) && partOfRegex.first().isString()) {
      return ((IStringX) partOfRegex.first()).toString();
    } else if (partOfRegex instanceof RepeatedPattern) {
      RepeatedPattern repeated = (RepeatedPattern) partOfRegex;
      IExpr expr = repeated.getRepeatedExpr();
      if (expr == null) {
        return null;
      }
      if (expr.isAST(S.Pattern, 3) && expr.first().isSymbol()) {
        final ISymbol symbol = (ISymbol) expr.first();
        String str = toRegexString(expr.second(), abbreviatedPatterns, stringFunction,
            shortestLongest, groups, engine);
        if (str != null) {
          final String groupName = symbol.toString();
          groups.put(symbol, groupName);
          if (repeated.isNullSequence()) {
            return "(?<" + groupName + ">(" + str + ")" + shortestLongest[ASTERISK_Q] + ")";
          } else {
            return "(?<" + groupName + ">(" + str + ")" + shortestLongest[PLUS_Q] + ")";
          }
        }
      } else {
        String str = toRegexString(expr, abbreviatedPatterns, stringFunction, shortestLongest,
            groups, engine);
        if (str != null) {
          if (repeated.isNullSequence()) {
            return "(" + str + ")" + shortestLongest[ASTERISK_Q];
          } else {
            return "(" + str + ")" + shortestLongest[PLUS_Q];
          }
        }
      }
    } else if (partOfRegex.isAST(S.StringExpression)) {
      IAST stringExpression = (IAST) partOfRegex;
      return toRegexString(stringFunction, stringExpression, abbreviatedPatterns, shortestLongest,
          groups, engine);
    } else if (partOfRegex.isBlank()) {
      return "(.|\\n)";
    } else if (partOfRegex.isPattern()) {
      final IPattern pattern = (IPattern) partOfRegex;
      final ISymbol symbol = pattern.getSymbol();
      if (symbol != null && pattern.getHeadTest() == null) {
        // see github #221 - use Java regex - named capturing groups
        final String groupName = symbol.toString();
        groups.put(symbol, groupName);
        if (pattern instanceof PatternNested) {
          PatternNested pn = (PatternNested) pattern;
          IExpr subPattern = pn.getPatternExpr();
          String subPatternRegex = toRegexString(subPattern, abbreviatedPatterns, stringFunction,
              shortestLongest, groups, engine);
          return "(?<" + groupName + ">" + subPatternRegex + ")";
        }
        return "(?<" + groupName + ">(.|\\n))";
      }
    } else if (partOfRegex.isAST(S.Pattern, 3) && partOfRegex.first().isSymbol()) {
      final ISymbol symbol = (ISymbol) partOfRegex.first();
      String str = toRegexString(partOfRegex.second(), abbreviatedPatterns, stringFunction,
          shortestLongest, groups, engine);
      if (str != null) {
        final String groupName = symbol.toString();
        groups.put(symbol, groupName);
        return "(?<" + groupName + ">" + str + ")";
      }
    } else if (partOfRegex.isPatternSequence(false)) {
      AbstractPatternSequence ps = ((AbstractPatternSequence) partOfRegex);
      final ISymbol symbol = ps.getSymbol();
      final String str;
      if (ps.isNullSequence()) {
        // RepeatedNull
        str = "(.|\\n)" + shortestLongest[ASTERISK_Q];
      } else {
        // Repeated
        str = "(.|\\n)" + shortestLongest[PLUS_Q];
      }
      if (symbol == null) {
        return str;
      } else {
        final String groupName = symbol.toString();
        groups.put(symbol, groupName);
        return "(?<" + groupName + ">" + str + ")";
      }
    } else if (partOfRegex.isAST(S.CharacterRange, 3)) {
      String[] characterRange = characterRange((IAST) partOfRegex);
      if (characterRange != null) {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        buf.append(Pattern.quote(characterRange[0]));
        buf.append("-");
        buf.append(Pattern.quote(characterRange[1]));
        buf.append("]");
        return buf.toString();
      }
    } else if (partOfRegex.isAlternatives()) {
      IAST alternatives = (IAST) partOfRegex;
      StringBuilder pieces = new StringBuilder();
      for (int i = 1; i < alternatives.size(); i++) {
        String str = toRegexString(alternatives.get(i), abbreviatedPatterns, stringFunction,
            shortestLongest, groups, engine);
        if (str == null) {
          // `1` currently not supported in `2`.
          IOFunctions.printMessage(stringFunction.topHead(), "unsupported",
              F.list(alternatives.get(i), stringFunction.topHead()), engine);
          return null;
        }
        pieces.append(str);
        if (i < alternatives.size() - 1) {
          pieces.append('|');
        }
      }
      return pieces.toString();
    } else if (partOfRegex.isExcept()) {
      IAST exceptions = (IAST) partOfRegex;
      StringBuilder pieces = new StringBuilder();
      for (int i = 1; i < exceptions.size(); i++) {
        String str = toRegexString(exceptions.get(i), abbreviatedPatterns, stringFunction,
            shortestLongest, groups, engine);
        if (str == null) {
          // `1` currently not supported in `2`.
          IOFunctions.printMessage(stringFunction.topHead(), "unsupported",
              F.list(exceptions.get(i), stringFunction.topHead()), engine);
          return null;
        }
        pieces.append(str);
      }
      return "[^" + pieces.toString() + "]";
    } else if (partOfRegex.isAST(S.Shortest, 2)) {
      String str = toRegexString(partOfRegex.first(), abbreviatedPatterns, stringFunction,
          REGEX_SHORTEST, groups, engine);
      return str;
    } else if (partOfRegex.isAST(S.Longest, 2)) {
      return toRegexString(partOfRegex.first(), abbreviatedPatterns, stringFunction, REGEX_LONGEST,
          groups, engine);
    } else if (partOfRegex.isBuiltInSymbol()) {
      int ordinal = ((IBuiltInSymbol) partOfRegex).ordinal();
      switch (ordinal) {
        case ID.NumberString:
          // better suitable for StringSplit?
          return "[0-9]{1,13}(\\.[0-9]+)?";
        // mathics:
        // return "[-|+]?(\\d+(\\.\\d*)?|\\.\\d+)?";
        case ID.Whitespace:
          return "(?u)\\s+";
        case ID.DigitCharacter:
          return "\\d";
        case ID.WhitespaceCharacter:
          return "(?u)\\s";
        case ID.WordCharacter:
          return "(?u)[^\\W_]";
        case ID.StartOfLine:
          return "\\R";
        case ID.EndOfLine:
          return "$";
        case ID.StartOfString:
          return "\\A";
        case ID.EndOfString:
          return "\\Z";
        case ID.WordBoundary:
          return "\\b";
        case ID.LetterCharacter:
          return "(?u)[^\\W_0-9]";
        case ID.HexidecimalCharacter:
          return "[0-9a-fA-F]";
        default:
          // `1` currently not supported in `2`.
          IOFunctions.printMessage(stringFunction.topHead(), "unsupported",
              F.list(partOfRegex, stringFunction.topHead()), engine);
          return null;
      }
    }

    // `1` currently not supported in `2`.
    IOFunctions.printMessage(stringFunction.topHead(), "unsupported",
        F.list(partOfRegex, stringFunction.topHead()), engine);
    return null;
  }

  /**
   * Convert the <code>StringExpression( ... )</code> to a java regex string.
   *
   * @param ast
   * @param stringExpression the <code>StringExpression( ... )</code> expression
   * @param abbreviatedPatterns if <code>true</code> allow 'abbreviated patterns" in strings (i.e.
   *        '\','*' and '@' operators)
   * @param shortestLongest either {@link #REGEX_LONGEST} or {@link #REGEX_SHORTEST}
   * @param groups
   * @param engine the evaluation engine
   * @return
   * @see <a href="https://en.wikipedia.org/wiki/Perl_Compatible_Regular_Expressions">Wikipedia -
   *      Perl Compatible Regular Expression</a>
   */
  private static String toRegexString(IAST ast, IAST stringExpression, boolean abbreviatedPatterns,
      String[] shortestLongest, Map<ISymbol, String> groups, EvalEngine engine) {

    StringBuilder regex = new StringBuilder();
    for (int i = 1; i < stringExpression.size(); i++) {
      IExpr arg = stringExpression.get(i);
      String str = toRegexString(arg, abbreviatedPatterns, ast, shortestLongest, groups, engine);
      if (str == null) {
        return null;
      }
      regex.append(str);
    }

    return regex.toString();
  }

  /**
   * Get the character range of <code>CharacterRange(from, to)</code>
   *
   * @param characterRangeAST the character range <code>CharacterRange(a,b)</code>
   * @return <code>from</code> at offset 0 and <code>to</code> at offset 1. <code>null</code> if the
   *         character range cannot be generated
   */
  private static String[] characterRange(final IAST characterRangeAST) {

    if (!(characterRangeAST.arg1() instanceof IStringX)
        || !(characterRangeAST.arg2() instanceof IStringX)) {

      if (!(characterRangeAST.arg1().isInteger()) || !(characterRangeAST.arg2().isInteger())) {
        return null;
      }
      int from = characterRangeAST.arg1().toIntDefault();
      int to = characterRangeAST.arg2().toIntDefault();
      if (from < 0 || to < 0) {
        return null;
      }
      return new String[] {String.valueOf((char) from), String.valueOf((char) to)};
    }
    String str1 = characterRangeAST.arg1().toString();
    String str2 = characterRangeAST.arg2().toString();
    if (str1.length() != 1 || str2.length() != 1) {
      return null;
    }

    char from = str1.charAt(0);
    char to = str2.charAt(0);
    return new String[] {String.valueOf(from), String.valueOf(to)};
  }

  public static void initialize() {
    Initializer.init();
  }

  private StringFunctions() {}
}
