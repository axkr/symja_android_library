package org.matheclipse.core.builtin;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.PatternSequence;
import org.matheclipse.core.expression.RepeatedPattern;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.tex.TeXParser;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPredicate;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.FEConfig;

import com.google.common.base.CharMatcher;
import com.ibm.icu.text.Transliterator;

public final class StringFunctions {
  private static final Map<String, String> LANGUAGE_MAP = new HashMap<String, String>();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      LANGUAGE_MAP.put("English", "Latin");
      S.BaseDecode.setEvaluator(new BaseDecode());
      S.BaseEncode.setEvaluator(new BaseEncode());
      S.ByteArrayToString.setEvaluator(new ByteArrayToString());
      S.Characters.setEvaluator(new Characters());
      S.CharacterRange.setEvaluator(new CharacterRange());
      S.EditDistance.setEvaluator(new EditDistance());

      S.FileNameJoin.setEvaluator(new FileNameJoin());
      S.FileNameTake.setEvaluator(new FileNameTake());
      S.FromCharacterCode.setEvaluator(new FromCharacterCode());
      S.HammingDistance.setEvaluator(new HammingDistance());
      S.LetterQ.setEvaluator(new LetterQ());
      S.LowerCaseQ.setEvaluator(new LowerCaseQ());
      S.PrintableASCIIQ.setEvaluator(new PrintableASCIIQ());
      S.RemoveDiacritics.setEvaluator(new RemoveDiacritics());
      S.StringCases.setEvaluator(new StringCases());
      S.StringCount.setEvaluator(new StringCount());
      S.StringContainsQ.setEvaluator(new StringContainsQ());
      S.StringDrop.setEvaluator(new StringDrop());
      S.StringExpression.setEvaluator(new StringExpression());
      S.StringFreeQ.setEvaluator(new StringFreeQ());
      S.StringInsert.setEvaluator(new StringInsert());
      S.StringJoin.setEvaluator(new StringJoin());
      S.StringLength.setEvaluator(new StringLength());
      S.StringMatchQ.setEvaluator(new StringMatchQ());
      S.StringPart.setEvaluator(new StringPart());
      S.StringPosition.setEvaluator(new StringPosition());
      S.StringReplace.setEvaluator(new StringReplace());
      S.StringRiffle.setEvaluator(new StringRiffle());
      S.StringSplit.setEvaluator(new StringSplit());
      S.StringTake.setEvaluator(new StringTake());
      S.StringToByteArray.setEvaluator(new StringToByteArray());
      S.StringTrim.setEvaluator(new StringTrim());
      S.SyntaxLength.setEvaluator(new SyntaxLength());
      S.TextString.setEvaluator(new TextString());
      S.ToCharacterCode.setEvaluator(new ToCharacterCode());
      S.ToString.setEvaluator(new ToString());
      S.ToUnicode.setEvaluator(new ToUnicode());
      S.Transliterate.setEvaluator(new Transliterate());
      S.UpperCaseQ.setEvaluator(new UpperCaseQ());

      TeXParser.initialize();
      if (!Config.FUZZY_PARSER) {
        S.ToExpression.setEvaluator(new ToExpression());
      }
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

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
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

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
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
      String str = ast.arg1().toString();
      IASTAppendable result = F.ListAlloc(str.length());
      for (int i = 0; i < str.length(); i++) {
        result.append(F.$str(str.charAt(i)));
      }
      return result;
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
   * <p>computes a list of character strings from <code>min-character</code> to <code>max-character
   * </code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>The printable ASCII characters:
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
          return IOFunctions.printMessage(
              ast.topHead(), "argtype", F.List(ast.arg1(), ast.arg2(), ast.topHead()), engine);
        } else {
          int from = ast.arg1().toIntDefault();
          int to = ast.arg2().toIntDefault();
          if (from < 0 || to < 0) {
            // Arguments `1` and `2` of `3` should be either non-negative integers or one-character
            // strings.
            return IOFunctions.printMessage(
                ast.topHead(), "argtype", F.List(ast.arg1(), ast.arg2(), ast.topHead()), engine);
          }
          int size = to - from;
          if (size <= 0) {
            return F.CEmptyList;
          }
          IASTAppendable result = F.ListAlloc(size);
          for (int i = from; i <= to; i++) {
            result.append(F.$str((char) i));
          }
          return result;
        }
      } else {
        String str1 = ast.arg1().toString();
        String str2 = ast.arg2().toString();
        if (str1.length() != 1 || str2.length() != 1) {
          // Arguments `1` and `2` of `3` should be either non-negative integers or one-character
          // strings.
          return IOFunctions.printMessage(
              ast.topHead(), "argtype", F.List(ast.arg1(), ast.arg2(), ast.topHead()), engine);
        }
        char from = str1.charAt(0);
        char to = str2.charAt(0);
        int size = ((int) to) - ((int) from);
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
          String separator = File.separator;
          StringBuilder buf = new StringBuilder();
          for (int i = 1; i < list.size(); i++) {
            buf.append(list.get(i).toString());
            if (i < list.size() - 1) {
              buf.append(separator);
            }
          }
          return F.stringx(buf.toString());
        }

      } catch (IndexOutOfBoundsException iob) {
        // from substring
        // Cannot take positions `1` through `2` in `3`.
        return IOFunctions.printMessage(
            ast.topHead(), "take", F.List(F.ZZ(from), F.ZZ(to), arg1), engine);
      } catch (final ValidateException ve) {
        // int number validation
        return engine.printMessage(ve.getMessage(ast.topHead()));
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
              //   try operator system dependent
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
        return IOFunctions.printMessage(
            ast.topHead(), "take", F.List(F.ZZ(from), F.ZZ(to), arg1), engine);
      } catch (final ValidateException ve) {
        // int number validation
        return engine.printMessage(ve.getMessage(ast.topHead()));
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
   * <p>converts the <code>ch1, ch2,...</code> character codes into a string of corresponding
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
   * <p><a href="ToCharacterCode.md">ToCharacterCode</a>
   */
  private static class FromCharacterCode extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() != 2) {
        return F.NIL;
      }

      if (ast.arg1().isList()) {
        final IAST list = (IAST) ast.arg1();
        return fromCharacterCode(list, ast, engine);
      }
      if (ast.arg1().isInteger()) {
        return fromCharacterCode(ast, ast, engine);
      }

      return F.NIL;
    }

    private static IExpr fromCharacterCode(
        final IAST charList, final IAST fromCharacterCodeAST, EvalEngine engine) {
      final StringBuilder buffer = new StringBuilder(charList.size());
      char ch;
      for (int i = 1; i < charList.size(); i++) {
        if (charList.get(i).isInteger()) {
          int unicode = charList.get(i).toIntDefault(Integer.MIN_VALUE);
          if (unicode < 0 || unicode >= 1114112) {
            // A character unicode, which should be a non-negative integer less than 1114112, is
            // expected at
            // position `2` in `1`.
            return IOFunctions.printMessage(
                S.FromCharacterCode, "notunicode", F.List(charList, F.ZZ(i)), engine);
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

    // public static IAST fromCharacterCode(final String unicodeInput, final String inputEncoding,
    // final IASTAppendable list) {
    // try {
    // final String utf8String = new String(unicodeInput.getBytes(inputEncoding), "UTF-8");
    // int characterCode;
    // for (int i = 0; i < utf8String.length(); i++) {
    // characterCode = utf8String.charAt(i);
    // list.append(F.integer(characterCode));
    // }
    // return list;
    // } catch (final UnsupportedEncodingException e) {
    // e.printStackTrace();
    // }
    // return F.NIL;
    // }
  }

  private static final class HammingDistance extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isString() && arg2.isString()) {
        boolean ignoreCase = false;
        if (ast.size() > 3) {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine, true);
          IExpr option = options.getOption(S.IgnoreCase);
          if (option.isTrue()) {
            ignoreCase = true;
          }
        }

        org.apache.commons.text.similarity.HammingDistance hammingDistance =
            new org.apache.commons.text.similarity.HammingDistance();
        String str1 = arg1.toString();
        String str2 = arg2.toString();
        if (str1.length() != str2.length()) {
          // `1` and `2` must have the same length.
          return IOFunctions.printMessage(ast.topHead(), "idim", F.List(arg1, arg2), engine);
        }

        if (ignoreCase) {
          return F.ZZ(hammingDistance.apply(str1.toLowerCase(), str2.toLowerCase()));
        }
        return F.ZZ(hammingDistance.apply(str1, str2));
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
   * LetterQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>tests whether <code>expr</code> is a string, which only contains letters.
   *
   * </blockquote>
   *
   * <p>A character is considered to be a letter if its general category type, provided by the Java
   * method <code>Character#getType()</code> is any of the following:
   *
   * <ul>
   *   <li><code>UPPERCASE_LETTER</code>
   *   <li><code>LOWERCASE_LETTER</code>
   *   <li><code>TITLECASE_LETTER</code>
   *   <li><code>MODIFIER_LETTER</code>
   *   <li><code>OTHER_LETTER</code>
   * </ul>
   *
   * <p>Not all letters have case. Many characters are letters but are neither uppercase nor
   * lowercase nor titlecase.
   */
  private static class LetterQ extends AbstractFunctionEvaluator implements Predicate<IExpr> {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString()) {
        return F.bool(test(ast.arg1()));
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
   * <p>is <code>True</code> if the given <code>str</code> is a string which only contains lower
   * case characters.
   *
   * </blockquote>
   */
  private static class LowerCaseQ extends AbstractFunctionEvaluator implements Predicate<IExpr> {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = Validate.checkStringType(ast, 1, engine);
      if (arg1.isPresent()) {
        return F.bool(test(arg1));
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

  private static final class EditDistance extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isString() && arg2.isString()) {
        boolean ignoreCase = false;
        if (ast.size() > 3) {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine, true);
          IExpr option = options.getOption(S.IgnoreCase);
          if (option.isTrue()) {
            ignoreCase = true;
          }
        }

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        if (ignoreCase) {
          return F.ZZ(
              levenshteinDistance.apply(
                  arg1.toString().toLowerCase(), arg2.toString().toLowerCase()));
        }
        return F.ZZ(levenshteinDistance.apply(arg1.toString(), arg2.toString()));
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
   * <code>PrintableASCIIQ(str)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if all characters in <code>str</code> are ASCII characters.
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/ASCII">Wikipedia - ASCII</a>
   *   <li><a href="https://en.wikipedia.org/wiki/UTF-8">Wikipedia - UTF-8</a>
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
        return F.bool(test(arg1));
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
   * <code>RemoveDiacritics(&quot;string&quot;)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>replace characters with diacritics with characters without diacritics.
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Diacritic">Wikipedia - Diacritic</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; RemoveDiacritics(&quot;éèáàâ&quot;)
   * eeaaa
   * </code>
   * </pre>
   */
  private static class RemoveDiacritics extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      String str = ast.arg1().toString();
      Transliterator transform = Transliterator.getInstance("NFD; [:Nonspacing Mark:] Remove; NFC");
      String value = transform.transliterate(str);
      return F.$str(value);
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
   * <code>StringCases(string, pattern)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>gives all occurences of <code>pattern</code> in <code>string</code>.
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
  private static class StringCases extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      //      if (ast.isAST1()) {
      //        ast = F.operatorForm1Append(ast);
      //        if (!ast.isPresent()) {
      //          return F.NIL;
      //        }
      //      }
      if (ast.size() >= 3) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return ((IAST) arg1).mapThread(ast, 1);
        }
        if (arg1.isString()) {
          boolean ignoreCase = false;
          if (ast.size() > 3) {
            final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine, true);
            IExpr option = options.getOption(S.IgnoreCase);
            if (option.isTrue()) {
              ignoreCase = true;
            }
            // TODO Overlaps option
            // IExpr option = options.getOption(S.Overlaps);
            // if (option.isTrue()) {
            // ignoreCase = true;
            // }
          }
          String str = ((IStringX) arg1).toString();
          IExpr arg2 = ast.arg2();
          if (!arg2.isList()) {
            arg2 = F.List(arg2);
          }
          IAST list = (IAST) arg2;
          IASTAppendable result = F.ListAlloc();
          for (int i = 1; i < list.size(); i++) {
            IExpr arg = list.get(i);

            java.util.regex.Pattern pattern = toRegexPattern(arg, true, ignoreCase, ast, engine);
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
   * <p>counts all occurences of <code>pattern</code> in <code>string</code>.
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
  private static class StringCount extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      //      if (ast.isAST1()) {
      //        ast = F.operatorForm1Append(ast);
      //        if (!ast.isPresent()) {
      //          return F.NIL;
      //        }
      //      }
      if (ast.size() >= 3) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return ((IAST) arg1).mapThread(ast, 1);
        }
        if (arg1.isString()) {
          boolean ignoreCase = false;
          if (ast.size() > 3) {
            final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine, true);
            IExpr option = options.getOption(S.IgnoreCase);
            if (option.isTrue()) {
              ignoreCase = true;
            }
            // TODO Overlaps option
            // IExpr option = options.getOption(S.Overlaps);
            // if (option.isTrue()) {
            // ignoreCase = true;
            // }
          }
          String str = ((IStringX) arg1).toString();
          IExpr arg2 = ast.arg2();
          if (!arg2.isList()) {
            arg2 = F.List(arg2);
          }
          IAST list = (IAST) arg2;
          int counter = 0;
          for (int i = 1; i < list.size(); i++) {
            IExpr arg = list.get(i);

            java.util.regex.Pattern pattern = toRegexPattern(arg, true, ignoreCase, ast, engine);
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
   * <p>return a list of matches for <code>&quot;p1&quot;, &quot;p2&quot;,...</code> list of strings
   * in the string <code>str</code>.
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
  private static class StringContainsQ extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      //      if (ast.isAST1()) {
      //        ast = F.operatorForm1Append(ast);
      //        if (!ast.isPresent()) {
      //          return F.NIL;
      //        }
      //      }
      boolean ignoreCase = false;
      if (ast.size() > 3) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine, true);
        IExpr option = options.getOption(S.IgnoreCase);
        if (option.isTrue()) {
          ignoreCase = true;
        }
      }

      if (ast.size() >= 3) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return ((IAST) arg1).mapThread(ast, 1);
        }
        if (arg1.isString()) {
          IExpr arg2 = ast.arg2();
          java.util.regex.Pattern pattern = toRegexPattern(arg2, true, ignoreCase, ast, engine);
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
        return IOFunctions.printMessage(
            ast.topHead(), "drop", F.List(F.ZZ(from - 1), F.ZZ(to), ast.arg1()), engine);
      } catch (final ValidateException ve) {
        // int number validation
        return engine.printMessage(ve.getMessage(ast.topHead()));
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

  private static class StringFreeQ extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      //      if (ast.isAST1()) {
      //        ast = F.operatorForm1Append(ast);
      //        if (!ast.isPresent()) {
      //          return F.NIL;
      //        }
      //      }
      if (ast.size() >= 3) {
        boolean ignoreCase = false;
        if (ast.size() > 3) {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine, true);
          IExpr option = options.getOption(S.IgnoreCase);
          if (option.isTrue()) {
            ignoreCase = true;
          }
        }

        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return ((IAST) arg1).mapThread(ast, 1);
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

    private static IExpr stringFreeQ(
        IAST ast, IExpr arg1, IExpr arg2, boolean ignoreCase, EvalEngine engine) {
      java.util.regex.Pattern pattern = toRegexPattern(arg2, true, ignoreCase, ast, engine);
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
   * <p>returns a string with <code>new-string</code> inserted starting at <code>position</code> in
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
   * <p>returns a string with <code>new-string</code> inserted starting at <code>position</code>
   * from the end of <code>string</code>.
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
   * <p>returns a string with <code>new-string</code> inserted at each position <code>posN</code> in
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
   * <p>gives the list of results for each of the strings <code>strN</code>
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
        return ((IAST) arg1).mapThread(ast, 1);
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
        listOfInts =
            Validate.checkListOfInts(
                ast, ast.arg3(), -str1.length() - 1, str1.length() + 1, engine);
      } else {
        int pos = ast.arg3().toIntDefault();
        if (Math.abs(pos) > str1.length() + 1) {
          return IOFunctions.printMessage(ast.topHead(), "ins", F.List(ast.arg3(), arg1), engine);
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
            return IOFunctions.printMessage(ast.topHead(), "ins", F.List(F.C0, arg1), engine);
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
        if (FEConfig.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
        return engine.printMessage(ast.topHead(), rex);
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
   * <p>or
   *
   * <pre>
   * <code>str1 &lt;&gt; str2 &lt;&gt;  ... &lt;&gt; strN
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the concatenation of the strings <code>str1, str2, ... strN</code>.
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
            return arg1.isString() ? arg1 : F.NIL;
          }
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i < list.size(); i++) {
          if (list.get(i).isString()) {
            buf.append(list.get(i).toString());
          } else {
            return F.NIL;
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
   * <p>gives the length of <code>string</code>.
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
   * <p>check if the regular expression <code>regex-pattern</code> matches the <code>string</code>.
   *
   * </blockquote>
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Regular_expression">Wikipedia - Regular
   *       expression</a>
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
  private static class StringMatchQ extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      //      if (ast.isAST1()) {
      //        ast = F.operatorForm1Append(ast);
      //        if (!ast.isPresent()) {
      //          return F.NIL;
      //        }
      //      }
      if (ast.size() >= 3) {
        boolean ignoreCase = false;
        if (ast.size() > 3) {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine, true);
          IExpr option = options.getOption(S.IgnoreCase);
          if (option.isTrue()) {
            ignoreCase = true;
          }
        }

        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return ((IAST) arg1).mapThread(ast, 1);
        }

        IExpr arg2 = ast.arg2();
        java.util.regex.Pattern pattern = toRegexPattern(arg2, true, ignoreCase, ast, engine);
        if (pattern == null) {
          return F.NIL;
        }
        String s1 = arg1.toString();
        java.util.regex.Matcher matcher = pattern.matcher(s1);
        if (matcher.matches()) {
          return S.True;
        }
        return S.False;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
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
   * <p>return the character at position <code>pos</code> from the <code>str</code> string
   * expression.
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
   * <p>return the characters at the position in the list <code>{pos1, pos2, pos3,....}</code> from
   * the <code>str</code> string expression.
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
        return ((IAST) arg2).mapThread(ast, 2);
      }
      String str = ((IStringX) ast.arg1()).toString();
      int part = arg2.toIntDefault();
      if (part > 0) {
        if (part > str.length()) {
          // Part `1` of `2` does not exist.
          return IOFunctions.printMessage(
              ast.topHead(), "partw", F.List(F.ZZ(part), ast.arg1()), engine);
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

  private static class StringPosition extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      //      if (ast.isAST1()) {
      //        ast = F.operatorForm1Append(ast);
      //        if (!ast.isPresent()) {
      //          return F.NIL;
      //        }
      //      }
      if (ast.size() >= 3) {
        int maxOccurences = Integer.MAX_VALUE;
        boolean ignoreCase = false;
        if (ast.size() > 3) {
          maxOccurences = ast.arg3().toIntDefault();
          if (maxOccurences < 0) {
            maxOccurences = Integer.MAX_VALUE;
          }
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine, true);
          IExpr option = options.getOption(S.IgnoreCase);
          if (option.isTrue()) {
            ignoreCase = true;
          }
        }

        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return ((IAST) arg1).mapThread(ast, 1);
        }

        IExpr arg2 = ast.arg2();
        IASTAppendable result = F.ListAlloc();

        if (arg2.isList()) {
          IAST list = (IAST) arg2;
          for (int i = 1; i < list.size(); i++) {
            IExpr temp =
                stringPosition(ast, arg1, list.get(i), maxOccurences, ignoreCase, result, engine);
            if (!temp.isPresent()) {
              return F.NIL;
            }
            if (maxOccurences < result.size()) {
              return result;
            }
          }
          return result;
        }
        return stringPosition(ast, arg1, arg2, maxOccurences, ignoreCase, result, engine);
      }
      return F.NIL;
    }

    private static IExpr stringPosition(
        IAST ast,
        IExpr arg1,
        IExpr arg2,
        int maxOccurences,
        boolean ignoreCase,
        IASTAppendable result,
        EvalEngine engine) {
      java.util.regex.Pattern pattern = toRegexPattern(arg2, true, ignoreCase, ast, engine);
      if (pattern == null) {
        return F.NIL;
      }
      String s1 = arg1.toString();
      java.util.regex.Matcher matcher = pattern.matcher(s1);
      while (matcher.find()) {
        if (maxOccurences < result.size()) {
          return result;
        }
        result.append(F.List(F.ZZ(matcher.start() + 1), F.ZZ(matcher.end())));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
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
   * <p>replaces each occurrence of <code>fromStr</code> with <code>toStr</code> in <code>string
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
  private static class StringReplace extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      //      if (ast.isAST1()) {
      //        ast = F.operatorForm1Append(ast);
      //        if (!ast.isPresent()) {
      //          return F.NIL;
      //        }
      //      }
      if (ast.size() >= 3) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isList()) {
          return ((IAST) arg1).mapThread(ast, 1);
        }
        if (!arg1.isString()) {
          return F.NIL;
        }
        boolean ignoreCase = false;
        if (ast.size() > 3) {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine, true);
          IExpr option = options.getOption(S.IgnoreCase);
          if (option.isTrue()) {
            ignoreCase = true;
          }
        }
        String str = ((IStringX) arg1).toString();
        IExpr arg2 = ast.arg2();
        if (!arg2.isListOfRules(false)) {
          if (arg2.isRuleAST()) {
            arg2 = F.List(arg2);
          } else {
            return F.NIL;
          }
        }
        IAST list = (IAST) arg2;
        for (int i = 1; i < list.size(); i++) {
          IAST rule = (IAST) list.get(i);
          if (!rule.arg2().isString()) {
            // if (!rule.arg1().isString() || !rule.arg2().isString()) {
            return F.NIL;
          }

          java.util.regex.Pattern pattern =
              toRegexPattern(rule.arg1(), true, ignoreCase, ast, engine);
          if (pattern == null) {
            return F.NIL;
          }
          str = pattern.matcher(str).replaceAll(rule.arg2().toString());
        }
        return F.$str(str);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
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
        } else if (arg2.isAST(F.List, 4)) {
          IAST list = (IAST) arg2;
          left = list.arg1().toString();
          sep1 = list.arg2().toString();
          right = list.arg3().toString();
        } else {
          // String expected at position `1` in `2`.
          return IOFunctions.printMessage(ast.topHead(), "string", F.List(F.C2, ast), engine);
        }
      }
      if (ast.isAST3()) {
        IExpr arg3 = ast.arg3();
        if (arg3.isString()) {
          sep2 = arg3.toString();
        } else {
          // String expected at position `1` in `2`.
          return IOFunctions.printMessage(ast.topHead(), "string", F.List(F.C3, ast), engine);
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
      return engine.printMessage("StringRiffle: list expected as first argument");
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
   * <p>split the string <code>str</code> by whitespaces into a list of strings.
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
   * <p>split the string <code>str1</code> by <code>str2</code> into a list of strings.
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
   * <p>split the string <code>str1</code> by the regular expression <code>str2</code> into a list
   * of strings.
   *
   * </blockquote>
   *
   * <p>See
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Regular_expression">Wikipedia - Regular
   *       expression</a>
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
  private static class StringSplit extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      if (!arg1.isString()) {
        return F.NIL;
      }
      String str1 = ((IStringX) arg1).toString().trim();
      if (ast.isAST1()) {
        return splitList(str1, str1.split("\\s+"));
      }
      boolean ignoreCase = false;
      if (ast.size() > 3) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine, true);
        IExpr option = options.getOption(S.IgnoreCase);
        if (option.isTrue()) {
          ignoreCase = true;
        }
      }
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        java.util.regex.Pattern pattern = toRegexPattern(arg2, true, ignoreCase, ast, engine);
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
      IASTAppendable list = F.ListAlloc(result.length);
      for (int i = 0; i < result.length; i++) {
        list.append(F.stringx(result[i]));
      }
      return list;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static class StringTake extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int from = 1;
      int to = 1;
      IExpr arg1 = ast.arg1();
      try {
        if (arg1.isString()) {
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
            //        	  int[][] sequ =
            //                      Validate.checkListOfSequenceSpec(ast, arg2, 2,
            // s.length(),Integer.MIN_VALUE, Integer.MAX_VALUE, engine);
            //                  if (sequ == null || sequ.length == 0) {
            //                    return F.NIL;
            //                  }
            //                 if( sequ.length >1) {
            //                	 return ((IAST) arg2).mapThread(ast, 2);
            //                 }
            //                  switch (sequ.length) {
            //                    case 1:
            //                      from = sequ[0];
            //                      if (from < 0) {
            //                        from = s.length() + from + 1;
            //                      }
            //                      to = from;
            //                      return F.$str(s.substring(from - 1, to));
            //                    case 2:
            //                      from = sequ[0];
            //                      if (from < 0) {
            //                        from = s.length() + from + 1;
            //                      }
            //                      to = sequ[1];
            //                      if (to < 0) {
            //                        to = s.length() + to + 1;
            //                      }
            //                      return F.$str(s.substring(from - 1, to));
            //                    case 3:
            //                      from = sequ[0];
            //                      if (from < 0) {
            //                        from = s.length() + from + 1;
            //                      }
            //                      to = sequ[1];
            //                      if (to < 0) {
            //                        to = s.length() + to + 1;
            //                      }
            //                      int step = sequ[2];
            //                      if (step < 0) {
            //                        return F.NIL;
            //                      }
            //                      if (step == 0) {
            //                        return ((IAST) arg2).mapThread(ast, 2);
            //                      }
            //                      StringBuilder buf = new StringBuilder();
            //                      while (from <= to) {
            //                        buf.append(s.substring(from - 1, from));
            //                        from += step;
            //                      }
            //                      return F.$str(buf.toString());
            //                    default:
            //                      return ((IAST) arg2).mapThread(ast, 2);
            //                  }

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
        return IOFunctions.printMessage(
            ast.topHead(), "take", F.List(F.ZZ(from), F.ZZ(to), arg1), engine);
      } catch (final ValidateException ve) {
        // int number validation
        return engine.printMessage(ve.getMessage(ast.topHead()));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class StringToByteArray extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
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

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static class StringTrim extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString()) {
        if (ast.isAST1()) {
          return F.$str(ast.arg1().toString().trim());
        }
        if (ast.isAST2()) {
          if (!ast.arg1().isString()) {
            return F.NIL;
          }
          String str = ((IStringX) ast.arg1()).toString();
          try {
            String regex = toRegexString(ast.arg2(), true, ast, engine);
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

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  private static class SyntaxLength extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      final String str = ast.arg1().toString();
      return F.ZZ(ExprParser.syntaxLength(str, engine));
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
   * <p>converts <code>string</code> into a list of corresponding integer character codes.
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
   * <p><a href="FromCharacterCode.md">FromCharacterCode</a>
   */
  private static class ToCharacterCode extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }

      return toCharacterCode(ast.arg1().toString(), StandardCharsets.UTF_8);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }

    public static IAST toCharacterCode(final String unicodeInput, final Charset inputEncoding) {
      //      try {

      final String utf8String =
          new String(unicodeInput.getBytes(inputEncoding), StandardCharsets.UTF_8);
      int characterCode;
      final int length = utf8String.length();
      IASTAppendable list = F.ListAlloc(length);
      for (int i = 0; i < length; i++) {
        characterCode = utf8String.charAt(i);
        list.append(F.ZZ(characterCode));
      }
      return list;
      //      } catch (final UnsupportedEncodingException e) {
      //        e.printStackTrace();
      //      }
      //      return F.NIL;
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
   * <p>converts the <code>string</code> given in <code>form</code> into an expression.
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
      if (arg1.isString()) {
        ISymbol form = F.InputForm;
        if (ast.size() == 3) {
          IExpr arg2 = ast.arg2();
          if (arg2.equals(F.InputForm)) {
            form = F.InputForm;
          } else if (arg2.equals(F.TeXForm)) {
            form = F.TeXForm;
          } else {
            return F.NIL;
          }
        }
        try {
          if (form.equals(F.InputForm)) {
            ExprParser parser = new ExprParser(engine);
            IExpr temp = parser.parse(arg1.toString());
            return temp;
          } else if (form.equals(F.TeXForm)) {
            TeXParser texParser = new TeXParser(engine);
            return texParser.toExpression(arg1.toString());
          }
        } catch (RuntimeException rex) {
          if (FEConfig.SHOW_STACKTRACE) {
            rex.printStackTrace();
          }
          return F.$Aborted;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
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
   * <p>converts <code>expr</code> into a string.
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
   * <p>converts <code>string</code> into a string of corresponding unicode character codes.
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
   * <p><a href="FromCharacterCode.md">FromCharacterCode</a>, <a
   * href="ToCharacterCode.md">ToCharacterCode</a>
   */
  private static class ToUnicode extends AbstractFunctionEvaluator {
    private static final String UNICODE_PREFIX = "\\u";

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }

      return StringX.valueOf(toUnicodeString(ast.arg1().toString(), StandardCharsets.UTF_8));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }

    public static String toUnicodeString(final String unicodeInput, final Charset inputEncoding) {
      final StringBuilder unicodeStringBuilder = new StringBuilder();
      String unicodeString = null;

      //      try {
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
      //      } catch (final UnsupportedEncodingException e) {
      //        e.printStackTrace();
      //      }
      return unicodeString;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Transliterate(&quot;string&quot;)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>try converting the given string to a similar ASCII string
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Transliteration">Wikipedia - Transliteration</a>
   *   <li><a
   *       href="https://unicode-org.github.io/icu/userguide/transforms/general/">unicode-org.github.io
   *       - General Transforms </a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Transliterate(&quot;Горбачёв, Михаил Сергеевич&quot;)
   * Gorbacev, Mihail Sergeevic
   * </code>
   * </pre>
   */
  private static class Transliterate extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      // Enumeration<String> ids = Transliterator.getAvailableIDs();
      //
      // while (ids.hasMoreElements()) {
      // String s = ids.nextElement();
      // System.out.println(s);
      // }
      if (ast.isAST2()) {
        if (ast.arg2().isRuleAST()) {
          if (ast.arg2().first().isString()
              && //
              ast.arg2().second().isString()) {
            try {
              String str1 = mapToICU4J(ast.arg2().first().toString());
              String str2 = mapToICU4J(ast.arg2().second().toString());
              String str = ast.arg1().toString();
              Transliterator transform = Transliterator.getInstance(str1 + "-" + str2);
              String result = transform.transliterate(str);
              return F.$str(result);
            } catch (IllegalArgumentException iae) {

            }
          }
        } else if (ast.arg2().isString()) {
          try {
            String str1 = "Latin";
            String str2 = mapToICU4J(ast.arg2().toString());
            String str = ast.arg1().toString();
            Transliterator transform = Transliterator.getInstance(str1 + "-" + str2);
            String result = transform.transliterate(str);
            return F.$str(result);
          } catch (IllegalArgumentException iae) {

          }
        }
        return F.NIL;
      }
      if (ast.isAST1()) {
        String str = ast.arg1().toString();
        Transliterator transform = Transliterator.getInstance("Any-Latin");
        String latin = transform.transliterate(str);
        transform = Transliterator.getInstance("Latin-ASCII");
        // "NFD; [:Nonspacing Mark:] Remove; NFC."
        String ascii = transform.transliterate(latin);
        return F.$str(ascii);
      }
      return F.NIL;
    }

    private static String mapToICU4J(String language) {
      String temp = LANGUAGE_MAP.get(language);
      if (temp != null) {
        language = temp;
      }
      return language;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
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
   * <p>is <code>True</code> if the given <code>str</code> is a string which only contains upper
   * case characters.
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
        return F.bool(test(arg1));
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

    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static String inputForm(final IExpr expression, boolean relaxedSyntax) {
    try {
      StringBuilder buf = new StringBuilder();
      OutputFormFactory off = OutputFormFactory.get(relaxedSyntax, false);
      off.setIgnoreNewLine(true);
      off.setQuotes(true);
      if (off.convert(buf, expression)) {
        return buf.toString();
      }
    } catch (RuntimeException rex) {
      if (FEConfig.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
    }
    return null;
  }

  public static String inputForm(final IExpr expression) {
    if (FEConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      return StringFunctions.inputForm(expression, true);
    }
    return StringFunctions.inputForm(expression, false);
  }

  private static IExpr regexErrorHandling(
      final IAST ast, IllegalArgumentException iae, EvalEngine engine) {
    if (FEConfig.SHOW_STACKTRACE) {
      iae.printStackTrace();
    }
    if (iae instanceof PatternSyntaxException) {
      PatternSyntaxException pse = (PatternSyntaxException) iae;
      // Regex expression `1` error message: `2`
      return IOFunctions.printMessage(
          S.RegularExpression,
          "zzregex",
          F.List(F.$str(pse.getPattern()), F.$str(pse.getMessage())),
          engine);
    } else {
      return IOFunctions.printMessage(ast.topHead(), iae, engine);
    }
  }

  /**
   * Unicode version of predefined character classes and POSIX character classes are enabled in the
   * resulting regex Pattern object.
   *
   * <p>See:
   *
   * <ul>
   *   <li><a
   *       href="https://github.com/mathics/Mathics/blob/master/mathics/builtin/strings.py#L78">to_regex()
   *       function</a>
   *   <li><a href="https://en.wikipedia.org/wiki/Perl_Compatible_Regular_Expressions">Wikipedia -
   *       Perl Compatible Regular Expression</a>
   * </ul>
   *
   * @param partOfRegex the expression which represents a regex 'piece'
   * @param abbreviatedPatterns if <code>true</code> allow 'abbreviated patterns" in strings (i.e.
   *     '\','*' and '@' operatore)
   * @param ignoreCase if <code>true</code> enables case-insensitive matching.
   * @param stringFunction the original string function, used in error messages
   * @param engine the evaluation engine
   * @return
   */
  public static java.util.regex.Pattern toRegexPattern(
      IExpr partOfRegex,
      boolean abbreviatedPatterns,
      boolean ignoreCase,
      IAST stringFunction,
      EvalEngine engine) {
    String regex = toRegexString(partOfRegex, abbreviatedPatterns, stringFunction, engine);
    if (regex != null) {
      java.util.regex.Pattern pattern;
      try {
        if (ignoreCase) {
          pattern =
              java.util.regex.Pattern.compile(
                  regex, Pattern.UNICODE_CHARACTER_CLASS | Pattern.CASE_INSENSITIVE);
        } else {
          pattern = java.util.regex.Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
        }
        return pattern;
      } catch (IllegalArgumentException iae) {
        // for example java.util.regex.PatternSyntaxException
        regexErrorHandling(stringFunction, iae, engine);
      }
    }
    return null;
  }

  /**
   * See:
   *
   * <ul>
   *   <li><a
   *       href="https://github.com/mathics/Mathics/blob/master/mathics/builtin/strings.py#L78">to_regex()
   *       function</a>
   *   <li><a href="https://en.wikipedia.org/wiki/Perl_Compatible_Regular_Expressions">Wikipedia -
   *       Perl Compatible Regular Expression</a>
   * </ul>
   *
   * @param partOfRegex the expression which represents a regex 'piece'
   * @param abbreviatedPatterns if <code>true</code> allow 'abbreviated patterns" in strings (i.e.
   *     '\','*' and '@' operators)
   * @param stringFunction the original string function, used in error messages
   * @param engine the evaluation engine
   * @return
   */
  private static String toRegexString(
      IExpr partOfRegex, boolean abbreviatedPatterns, IAST stringFunction, EvalEngine engine) {
    if (partOfRegex.isString()) {
      // Guide to Escaping Characters in Java RegExps -
      // https://www.baeldung.com/java-regexp-escape-char
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
      if (repeated.isNullSequence()) {
        String str = toRegexString(expr, abbreviatedPatterns, stringFunction, engine);
        if (str == null) {
          return null;
        }
        return "(" + str + ")*";
      } else {
        String str = toRegexString(expr, abbreviatedPatterns, stringFunction, engine);
        if (str == null) {
          return null;
        }
        return "(" + str + ")+";
      }
    } else if (partOfRegex.isAST(S.StringExpression)) {
      IAST stringExpression = (IAST) partOfRegex;
      return toRegexString(stringFunction, stringExpression, abbreviatedPatterns, engine);
    } else if (partOfRegex.isBlank()) {
      return "(.|\\n)";
    } else if (partOfRegex.isPatternSequence(false)) {
      PatternSequence ps = ((PatternSequence) partOfRegex);
      if (ps.isNullSequence()) {
        // RepeatedNull
        return "(.|\\n)*";
      } else {
        // Repeated
        return "(.|\\n)+";
      }
    } else if (partOfRegex.isAST(S.CharacterRange, 3)) {
      String[] characterRange = characterRange((IAST) partOfRegex);
      if (characterRange == null) {
        return null;
      }
      StringBuilder buf = new StringBuilder();
      buf.append("[");
      buf.append(Pattern.quote(characterRange[0]));
      buf.append("-");
      buf.append(Pattern.quote(characterRange[1]));
      buf.append("]");
      return buf.toString();
    } else if (partOfRegex.isAlternatives()) {
      IAST alternatives = (IAST) partOfRegex;
      StringBuilder pieces = new StringBuilder();
      for (int i = 1; i < alternatives.size(); i++) {
        String str =
            toRegexString(alternatives.get(i), abbreviatedPatterns, stringFunction, engine);
        if (str == null) {
          return null;
        }
        pieces.append(str);
        if (i < alternatives.size() - 1) {
          pieces.append('|');
        }
      }
      return pieces.toString();
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
          IOFunctions.printMessage(
              stringFunction.topHead(),
              "unsupported",
              F.List(partOfRegex, stringFunction.topHead()),
              engine);
          return null;
      }
    } else {
      // `1` currently not supported in `2`.
      IOFunctions.printMessage(
          stringFunction.topHead(),
          "unsupported",
          F.List(partOfRegex, stringFunction.topHead()),
          engine);
    }
    return null;
  }

  private static String toRegexString(
      IAST ast, IAST stringExpression, boolean abbreviatedPatterns, EvalEngine engine) {
    StringBuilder regex = new StringBuilder();
    for (int i = 1; i < stringExpression.size(); i++) {
      IExpr arg = stringExpression.get(i);
      String str = toRegexString(arg, abbreviatedPatterns, ast, engine);
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
   *     character range cannot be generated
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
