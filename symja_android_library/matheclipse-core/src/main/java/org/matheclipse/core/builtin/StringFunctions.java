package org.matheclipse.core.builtin;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.eval.util.PackageUtil;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.expression.data.FileExpr;
import org.matheclipse.core.form.NumberFormatter;
import org.matheclipse.core.form.output.NumberFormPrinter;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.tex.TeXParser;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPredicate;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.BoxNotation;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import com.google.common.base.CharMatcher;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public final class StringFunctions {

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
            return Errors.printMessage(ast.topHead(), "nalph", F.list(ast.arg1()), engine);
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

  private static class BaseDecode extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      String encoding = baseEncoding(ast);
      if (encoding == null) {
        return F.NIL;
      }
      String str = ast.arg1().toString();
      try {
        switch (encoding) {
          case "BASE64":
            return ByteArrayExpr.newInstance(Base64.getDecoder().decode(str));
          case "BASE64URL":
            return ByteArrayExpr.newInstance(Base64.getUrlDecoder().decode(str));
          case "BASE32":
            return ByteArrayExpr.newInstance(new org.apache.commons.codec.binary.Base32()
                .decode(str.getBytes(StandardCharsets.US_ASCII)));
          case "BASE16":
            return ByteArrayExpr
                .newInstance(org.apache.commons.codec.binary.Hex.decodeHex(str.toCharArray()));
          default:
            return F.NIL;
        }
      } catch (IllegalArgumentException | org.apache.commons.codec.DecoderException iae) {
        //
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * The encoding an optional second argument names, upper-cased, or <code>"BASE64"</code> when
   * there is none - and <code>null</code> when the argument is not a string at all.
   */
  private static String baseEncoding(IAST ast) {
    if (ast.isAST1()) {
      return "BASE64";
    }
    IExpr arg2 = ast.arg2();
    if (arg2 instanceof IStringX) {
      return arg2.toString().toUpperCase();
    }
    return null;
  }

  private static class BaseEncode extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof ByteArrayExpr) {
        String encoding = baseEncoding(ast);
        if (encoding == null) {
          return F.NIL;
        }
        byte[] bArray = (byte[]) ((IDataExpr) arg1).toData();
        if (bArray.length == 0) {
          return F.$str("");
        }

        switch (encoding) {
          case "BASE64":
            return F.$str(Base64.getEncoder().encodeToString(bArray));
          case "BASE64URL":
            return F.$str(Base64.getUrlEncoder().encodeToString(bArray));
          case "BASE32":
            return F.$str(new String(new org.apache.commons.codec.binary.Base32().encode(bArray),
                StandardCharsets.US_ASCII));
          case "BASE16":
            return F.$str(org.apache.commons.codec.binary.Hex.encodeHexString(bArray));
          default:
            return F.NIL;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
          return Errors.printMessage(ast.topHead(), "argtype",
              F.list(ast.arg1(), ast.arg2(), ast.topHead()), engine);
        } else {
          int from = ast.arg1().toMachineInt();
          int to = ast.arg2().toMachineInt();
          if (from < 0 || to < 0) {
            // Arguments `1` and `2` of `3` should be either non-negative integers or one-character
            // strings.
            return Errors.printMessage(ast.topHead(), "argtype",
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
          return Errors.printMessage(ast.topHead(), "argtype",
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

  private static class Characters extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      // one string per code point, so a character above U+FFFF stays whole
      final int[] codePoints = ast.arg1().toString().codePoints().toArray();
      return F.mapRange(0, codePoints.length, i -> F.$str(new String(codePoints, i, 1)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  private static class CreateUUID extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        String uuid = UUID.randomUUID().toString();
        return F.stringx(uuid);
      }
      IExpr arg1 = ast.arg1();
      if (arg1.isString()) {
        String uuid = UUID.randomUUID().toString();
        return F.stringx(arg1.toString() + uuid);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
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
        final EvalEngine engine, IAST originalAST) {

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isString() && arg2.isString()) {
        boolean ignoreCase = option[0].isTrue();

        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();
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

  private static class FileNameDrop extends AbstractFunctionEvaluator {

    /**
     * Drop the first <code>n</code> elements from the file name. If <code>n</code> is negative,
     * drop the last <code>n</code> elements. The file separator character is the system dependent
     * {@link File#separatorChar}.
     * 
     * @param fileName the file name
     * @param n the number of path elements to drop
     * @return
     */
    private static IExpr fileDrop(String fileName, int n) {
      return fileDrop(fileName, n, File.separatorChar);
    }

    /**
     * Drop the first <code>n</code> elements from the file name. If <code>n</code> is negative,
     * drop the last <code>n</code> elements.
     * 
     * @param fileName the file name
     * @param n the number of path elements to drop
     * @param separator the filename separator character
     * @return
     */
    private static IExpr fileDrop(String fileName, int n, char separator) {
      if (n < 0) {
        int fromIndex = fileName.length() - 1;
        while (n < 0) {
          int lastIndex = fileName.lastIndexOf(separator, fromIndex);
          if (lastIndex < 0) {
            return F.CEmptyString;
          }
          if (n++ == -1) {
            if (lastIndex == 0 && fileName.length() >= 1) {
              return F.stringx(fileName.substring(0, 1));
            }
            return F.stringx(fileName.substring(0, lastIndex));
          }
          fromIndex = lastIndex - 1;
        }
      } else if (n > 0) {
        int fromIndex = 0;
        while (n > 0) {
          int index = fileName.indexOf(separator, fromIndex);
          if (index < 0) {
            return F.CEmptyString;
          }
          if (n-- == 1) {
            return F.stringx(fileName.substring(index + 1));
          }
          fromIndex = index + 1;
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      final IExpr arg2 = ast.isAST1() ? F.CN1 : ast.arg2();
      if (arg1.isList()) {
        return arg1.mapThread(F.FileNameDrop(F.Slot1, arg2), 1);
      }
      // TODO add implementation for sequence specs at arg2
      int n = arg2.toMachineInt();
      if (F.isPresent(n)) {
        String fileName = null;
        if (arg1.isString()) {
          fileName = arg1.toString();
        } else if (arg1 instanceof FileExpr) {
          File file = ((FileExpr) arg1).toData();
          fileName = file.getAbsolutePath();
        }
        if (fileName != null) {
          if (n == 0) {
            return arg1;
          }
          return fileDrop(fileName, n);
        }
      }
      // Sequence specification (+n,-n,{+n},{-n} or {m,n}) expected at position `2` in `1`.
      return Errors.printMessage(S.FileNameDrop, "seqso", F.List(ast, F.C2), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>ToFileName[{dir1, dir2, ...}, name]</code>, <code>ToFileName[dir, name]</code> and
   * <code>ToFileName[{dir1, dir2, ...}]</code>: the older form of <code>FileNameJoin</code>. A
   * directory alone is written with a trailing separator.
   */
  private static class ToFileName extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr dirs = ast.arg1();
      IASTAppendable parts = F.ListAlloc();
      if (dirs.isString()) {
        parts.append(dirs);
      } else if (dirs.isListOfStrings()) {
        parts.appendArgs((IAST) dirs);
      } else {
        return F.NIL;
      }
      if (ast.isAST2()) {
        if (!ast.arg2().isString()) {
          return F.NIL;
        }
        parts.append(ast.arg2());
      }
      IASTAppendable nonEmpty = F.ListAlloc(parts.size());
      for (int i = 1; i < parts.size(); i++) {
        if (!parts.get(i).toString().isEmpty()) {
          nonEmpty.append(parts.get(i));
        }
      }
      if (nonEmpty.isEmpty()) {
        return F.CEmptyString;
      }
      IExpr joined = engine.evaluate(F.FileNameJoin(nonEmpty));
      if (!joined.isString()) {
        return F.NIL;
      }
      String path = joined.toString();
      if (ast.isAST1() && !path.endsWith(File.separator)) {
        path = path + File.separator;
      }
      return F.stringx(path);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>LongestCommonSequence[a, b]</code>: the longest sequence, not necessarily contiguous, of
   * the characters of two strings or the elements of two lists which occurs in both.
   * <code>LongestCommonSubsequence[a, b]</code>: the longest contiguous one.
   */
  private static class LongestCommonSequence extends AbstractFunctionEvaluator {
    private final boolean contiguous;

    LongestCommonSequence(boolean contiguous) {
      this.contiguous = contiguous;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      if (a.isString() && b.isString()) {
        List<IExpr> x = characters(a.toString());
        List<IExpr> y = characters(b.toString());
        StringBuilder buf = new StringBuilder();
        for (IExpr c : common(x, y)) {
          buf.append(c.toString());
        }
        return F.stringx(buf.toString());
      }
      if (a.isList() && b.isList()) {
        List<IExpr> x = new java.util.ArrayList<>();
        for (int i = 1; i < a.size(); i++) {
          x.add(((IAST) a).get(i));
        }
        List<IExpr> y = new java.util.ArrayList<>();
        for (int i = 1; i < b.size(); i++) {
          y.add(((IAST) b).get(i));
        }
        IASTAppendable result = F.ListAlloc();
        common(x, y).forEach(result::append);
        return result;
      }
      return F.NIL;
    }

    private static List<IExpr> characters(String s) {
      List<IExpr> list = new java.util.ArrayList<>(s.length());
      s.codePoints().forEach(c -> list.add(F.stringx(new String(Character.toChars(c)))));
      return list;
    }

    private List<IExpr> common(List<IExpr> x, List<IExpr> y) {
      int n = x.size();
      int m = y.size();
      int[][] len = new int[n + 1][m + 1];
      int bestLength = 0;
      int bestEnd = 0;
      for (int i = n - 1; i >= 0; i--) {
        for (int j = m - 1; j >= 0; j--) {
          if (x.get(i).equals(y.get(j))) {
            len[i][j] = len[i + 1][j + 1] + 1;
          } else if (!contiguous) {
            len[i][j] = Math.max(len[i + 1][j], len[i][j + 1]);
          }
          if (contiguous && len[i][j] > bestLength) {
            bestLength = len[i][j];
            bestEnd = i;
          }
        }
      }
      List<IExpr> result = new java.util.ArrayList<>();
      if (contiguous) {
        for (int i = bestEnd; i < bestEnd + bestLength; i++) {
          result.add(x.get(i));
        }
        return result;
      }
      int i = 0;
      int j = 0;
      while (i < n && j < m) {
        if (x.get(i).equals(y.get(j))) {
          result.add(x.get(i));
          i++;
          j++;
        } else if (len[i + 1][j] >= len[i][j + 1]) {
          i++;
        } else {
          j++;
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>SequenceAlignment[a, b]</code>: the global alignment of two strings or two lists, as a
   * list of the common runs and of the pairs <code>{partOfA, partOfB}</code> where they differ.
   * A match scores 1, a mismatch and a gap -1; adjacent mismatches and gaps form one pair.
   */
  private static class SequenceAlignment extends AbstractFunctionEvaluator {
    /** The largest score table, about 100 MB. */
    private static final long MAX_ALIGNMENT_CELLS = 25_000_000L;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST3()) {
        // MergeDifferences gives the same alignment either way
        if (!ast.arg3().isRuleAST() || ast.arg3().first() != S.MergeDifferences) {
          // Unknown option `1` in `2`.
          return Errors.printMessage(ast.topHead(), "optx", F.list(ast.arg3(), ast), engine);
        }
      }
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      final boolean strings = a.isString() && b.isString();
      if (!strings && !(a.isList() && b.isList())) {
        return F.NIL;
      }
      List<IExpr> x = strings ? LongestCommonSequence.characters(a.toString()) : elements((IAST) a);
      List<IExpr> y = strings ? LongestCommonSequence.characters(b.toString()) : elements((IAST) b);
      final int n = x.size();
      final int m = y.size();
      final long cells = (long) (n + 1) * (m + 1);
      if (cells > MAX_ALIGNMENT_CELLS || cells > Config.MAX_AST_SIZE) {
        // the score table grows with the product of the lengths
        ASTElementLimitExceeded.throwIt(cells);
      }
      // score[i][j] is the best score of aligning x[i..] with y[j..]
      int[][] score = new int[n + 1][m + 1];
      for (int i = n; i >= 0; i--) {
        engine.checkTimeLimit();
        for (int j = m; j >= 0; j--) {
          if (i == n || j == m) {
            score[i][j] = -(n - i) - (m - j);
          } else {
            int diagonal = score[i + 1][j + 1] + (x.get(i).equals(y.get(j)) ? 1 : -1);
            score[i][j] =
                Math.max(diagonal, Math.max(score[i + 1][j] - 1, score[i][j + 1] - 1));
          }
        }
      }
      IASTAppendable result = F.ListAlloc();
      List<IExpr> common = new java.util.ArrayList<>();
      List<IExpr> partOfA = new java.util.ArrayList<>();
      List<IExpr> partOfB = new java.util.ArrayList<>();
      int i = 0;
      int j = 0;
      while (i < n || j < m) {
        if (i < n && j < m && x.get(i).equals(y.get(j))
            && score[i][j] == score[i + 1][j + 1] + 1) {
          appendDifference(result, partOfA, partOfB, strings);
          common.add(x.get(i));
          i++;
          j++;
          continue;
        }
        appendCommon(result, common, strings);
        if (i < n && j < m && !x.get(i).equals(y.get(j))
            && score[i][j] == score[i + 1][j + 1] - 1) {
          partOfA.add(x.get(i++));
          partOfB.add(y.get(j++));
        } else if (i < n && (j == m || score[i][j] == score[i + 1][j] - 1)) {
          partOfA.add(x.get(i++));
        } else {
          partOfB.add(y.get(j++));
        }
      }
      appendCommon(result, common, strings);
      appendDifference(result, partOfA, partOfB, strings);
      return result;
    }

    private static List<IExpr> elements(IAST list) {
      List<IExpr> result = new java.util.ArrayList<>(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        result.add(list.get(i));
      }
      return result;
    }

    private static IExpr join(List<IExpr> parts, boolean strings) {
      if (strings) {
        StringBuilder buf = new StringBuilder();
        parts.forEach(c -> buf.append(c.toString()));
        return F.stringx(buf.toString());
      }
      IASTAppendable list = F.ListAlloc(parts.size());
      parts.forEach(list::append);
      return list;
    }

    private static void appendCommon(IASTAppendable result, List<IExpr> common, boolean strings) {
      if (!common.isEmpty()) {
        result.append(join(common, strings));
        common.clear();
      }
    }

    private static void appendDifference(IASTAppendable result, List<IExpr> partOfA,
        List<IExpr> partOfB, boolean strings) {
      if (!partOfA.isEmpty() || !partOfB.isEmpty()) {
        result.append(F.list(join(partOfA, strings), join(partOfB, strings)));
        partOfA.clear();
        partOfB.clear();
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  private static class FileNameJoin extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int from = 1;
      int to = 1;
      IExpr arg1 = ast.arg1();
      try {
        if (arg1.isString()) {
          // One name is already a file name: `path // FileNameJoin` is how a path which may be a
          // list of segments or a finished string is normalised, and answering nothing for the
          // string left the path an unevaluated expression.
          return arg1;
        }
        if (arg1.isListOfStrings()) {
          IAST list = ((IAST) arg1);
          if (list.isAST1()) {
            return list.get(1);
          }
          char separator = File.separatorChar;
          StringBuilder buf = new StringBuilder();
          if (list.arg1().toString().isEmpty()) {
            // An empty first segment is the root: FileNameSplit["/a/b"] is {"", "a", "b"}, and
            // rejoining it has to give "/a/b" back rather than the relative "a/b".
            buf.append(separator);
          }
          for (int i = 1; i < list.size(); i++) {
            String arg = list.get(i).toString();
            if (arg.length() > 0) {
              buf.append(arg);
              if (i < list.argSize() //
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
        return Errors.printMessage(ast.topHead(), "take", F.list(F.ZZ(from), F.ZZ(to), arg1),
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
   * The unevaluated statements of a source with several lines. Every empty line which doesn't
   * continue an unfinished statement (an open bracket or string, a trailing operator) is a
   * <code>Null</code> statement; the line break which ends the source adds none.
   */
  private static List<IExpr> lineStatements(String source, EvalEngine engine) {
    final AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
    final List<IExpr> result = new ArrayList<IExpr>();
    final String[] lines = source.split("\n", -1);
    final int lineCount = lines.length > 0 && lines[lines.length - 1].isEmpty() ? lines.length - 1
        : lines.length;
    final StringBuilder chunk = new StringBuilder();
    for (int i = 0; i < lineCount; i++) {
      final String line = lines[i];
      if (line.trim().isEmpty()) {
        if (chunk.length() == 0) {
          result.add(S.Null);
          continue;
        }
        List<ASTNode> statements = parseStatements(chunk.toString(), engine);
        if (statements != null) {
          statements.forEach(node -> result.add(ast2Expr.convert(node)));
          chunk.setLength(0);
          result.add(S.Null);
          continue;
        }
      }
      if (chunk.length() > 0) {
        chunk.append('\n');
      }
      chunk.append(line);
      if (isCommentOnly(chunk, engine)) {
        // a comment standing on its own lines is a statement of its own
        result.add(S.Null);
        chunk.setLength(0);
      }
    }
    if (chunk.length() > 0) {
      for (ASTNode node : new Parser(engine.isRelaxedSyntax(), true).parseScript(chunk.toString())) {
        result.add(ast2Expr.convert(node));
      }
    }
    return result;
  }

  /** Is <code>chunk</code> one complete comment, or several, and nothing else? */
  private static boolean isCommentOnly(CharSequence chunk, EvalEngine engine) {
    String text = chunk.toString().trim();
    if (!text.startsWith("(*") || !text.endsWith("*)")) {
      return false;
    }
    List<ASTNode> statements = parseStatements(text, engine);
    return statements != null && statements.isEmpty();
  }

  /** @return <code>null</code> if <code>source</code> is not complete */
  private static List<ASTNode> parseStatements(String source, EvalEngine engine) {
    try {
      return new Parser(engine.isRelaxedSyntax(), true).parseScript(source);
    } catch (SyntaxError se) {
      return null;
    }
  }

  /**
   * <code>FileNameTake(name, spec)</code>: the path segments of a file name taken like
   * <code>Take</code> takes elements. The root <code>/</code> of an absolute name is a segment of
   * its own and a trailing separator is ignored.
   */
  private static class FileNameTake extends AbstractFunctionEvaluator {

    /** The segments of a file name; <code>"/"</code> first for an absolute name. */
    private static List<String> segments(String fileName) {
      final List<String> segments = new ArrayList<>();
      final char separator = File.separatorChar;
      int start = 0;
      if (!fileName.isEmpty() && (fileName.charAt(0) == '/' || fileName.charAt(0) == separator)) {
        segments.add(fileName.substring(0, 1));
        start = 1;
      }
      for (int i = start; i <= fileName.length(); i++) {
        if (i == fileName.length() || fileName.charAt(i) == '/' || fileName.charAt(i) == separator) {
          if (i > start) {
            segments.add(fileName.substring(start, i));
          }
          start = i + 1;
        }
      }
      return segments;
    }

    /** The segments <code>from..to</code> (1-based, inclusive) joined to a file name. */
    private static IExpr join(List<String> segments, int from, int to) {
      final StringBuilder buf = new StringBuilder();
      for (int i = from; i <= to; i++) {
        final String segment = segments.get(i - 1);
        if (buf.length() > 0 && buf.charAt(buf.length() - 1) != '/'
            && buf.charAt(buf.length() - 1) != File.separatorChar) {
          buf.append('/');
        }
        buf.append(segment);
      }
      return F.stringx(buf.toString());
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = ast.arg1();
      if (!arg1.isString()) {
        return F.NIL;
      }
      final List<String> segments = segments(arg1.toString());
      final int size = segments.size();
      if (ast.isAST1()) {
        return size == 0 ? F.CEmptyString : F.stringx(segments.get(size - 1));
      }
      final IExpr spec = ast.arg2();
      if (spec.isInteger()) {
        // the first n segments, or the last -n
        final int n = spec.toIntDefault();
        if (F.isNotPresent(n)) {
          return F.NIL;
        }
        if (n >= 0) {
          return join(segments, 1, Math.min(n, size));
        }
        return join(segments, Math.max(1, size + n + 1), size);
      }
      if (spec.isList1() || spec.isList2()) {
        final int[] positions = new int[spec.argSize()];
        for (int i = 0; i < positions.length; i++) {
          int position = spec.getAt(i + 1).toIntDefault();
          if (F.isNotPresent(position)) {
            return F.NIL;
          }
          positions[i] = position < 0 ? size + position + 1 : position;
        }
        final int from = positions[0];
        final int to = positions[positions.length - 1];
        if (from < 1 || to > size || from > to) {
          // a segment which does not exist
          return F.CEmptyString;
        }
        return join(segments, from, to);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** {@link #encodingLimit(String)} of the UTF-8 encoding, which maps a character to bytes. */
  private static final int UTF8 = -1;

  /**
   * {@link #encodingLimit(String)} of the printable ASCII encoding, which also leaves out the
   * control characters below the space except the newline.
   */
  private static final int PRINTABLE_ASCII = 0x7E;

  /**
   * The largest character code of an encoding name: <code>0x7F</code> for ASCII,
   * {@link #PRINTABLE_ASCII}, <code>0xFF</code> for ISO Latin 1, {@link #UTF8} for UTF-8 and
   * <code>Integer.MAX_VALUE</code> for Unicode and the names which are not known here.
   */
  private static int encodingLimit(String encoding) {
    switch (encoding) {
      case "UTF8":
      case "UTF-8":
        return UTF8;
      case "ASCII":
        return 0x7F;
      case "PrintableASCII":
        return PRINTABLE_ASCII;
      case "ISOLatin1":
      case "ISO8859-1":
        return 0xFF;
      default:
        return Integer.MAX_VALUE;
    }
  }

  /** <code>true</code> if the encoding with this {@link #encodingLimit(String)} lacks the code. */
  private static boolean outsideEncoding(int code, int limit) {
    return code > limit
        || (limit == PRINTABLE_ASCII && code >= 0 && code < 0x20 && code != '\n');
  }

  private static class FromCharacterCode extends AbstractFunctionEvaluator {

    private static IExpr fromCharacterCode(final IAST charList, final IAST fromCharacterCodeAST,
        EvalEngine engine) {
      final StringBuilder buffer = new StringBuilder(charList.size());
      for (int i = 1; i < charList.size(); i++) {
        if (charList.get(i).isInteger()) {
          int unicode = charList.get(i).toIntDefault();
          if (unicode < 0 || unicode >= 1114112) {
            // A character unicode, which should be a non-negative integer less than 1114112, is
            // expected at position `2` in `1`.
            return Errors.printMessage(S.FromCharacterCode, "notunicode", F.list(charList, F.ZZ(i)),
                engine);
          }
          // a code point above U+FFFF is a surrogate pair in Java; a (char) cast cut it off
          buffer.appendCodePoint(unicode);
        } else {
          return F.NIL;
        }
      }
      return StringX.valueOf(buffer);
    }

    /**
     * The string of the UTF-8 <code>bytes</code>. A byte which starts no valid sequence is
     * reported and taken as the ISO Latin 1 character of its code.
     */
    private static IExpr fromUTF8(final IAST bytes, EvalEngine engine) {
      byte[] buffer = new byte[bytes.argSize()];
      for (int i = 1; i < bytes.size(); i++) {
        int b = bytes.get(i).toIntDefault();
        if (b < 0 || b > 0xFF) {
          return F.NIL;
        }
        buffer[i - 1] = (byte) b;
      }
      java.nio.charset.CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
          .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
          .onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPORT);
      java.nio.ByteBuffer in = java.nio.ByteBuffer.wrap(buffer);
      java.nio.CharBuffer out = java.nio.CharBuffer.allocate(buffer.length + 1);
      IASTAppendable invalid = F.ListAlloc();
      while (true) {
        java.nio.charset.CoderResult result = decoder.decode(in, out, true);
        if (result.isError()) {
          for (int k = 0; k < result.length(); k++) {
            int b = in.get() & 0xFF;
            invalid.append(b);
            out.put((char) b);
          }
          continue;
        }
        decoder.flush(out);
        break;
      }
      if (invalid.argSize() > 0) {
        // The byte sequence `1` could not be interpreted as a character in the UTF-8 character
        // encoding.
        Errors.printMessage(S.$CharacterEncoding, "utf8", F.list(invalid), engine);
      }
      out.flip();
      return StringX.valueOf(out.toString());
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST2() && ast.arg2().isString()
          && encodingLimit(ast.arg2().toString()) == UTF8) {
        // the numbers are the bytes of the UTF-8 encoding
        if (arg1.isList()) {
          IAST list = (IAST) arg1;
          if (list.argSize() > 0 && list.arg1().isList()) {
            return list.mapThread(ast, 1);
          }
          return list.forAll(x -> x.isInteger()) ? fromUTF8(list, engine) : F.NIL;
        }
        return arg1.isInteger() ? fromUTF8(F.list(arg1), engine) : F.NIL;
      }
      // the other encodings name the characters by their codes, up to the largest code they have
      final int limit = ast.isAST2() && ast.arg2().isString()
          ? encodingLimit(ast.arg2().toString())
          : Integer.MAX_VALUE;
      if (limit < Integer.MAX_VALUE) {
        if (arg1.isList() && arg1.argSize() > 0 && arg1.first().isList()) {
          // each list of codes with the encoding
          return arg1.mapThread(ast, 1);
        }
        if (arg1.isList()
            ? ((IAST) arg1).exists(c -> c.isInteger() && outsideEncoding(c.toIntDefault(), limit))
            : arg1.isInteger() && outsideEncoding(arg1.toIntDefault(), limit)) {
          return F.NIL;
        }
      }
      IAST codes = ast.isAST1() ? ast : F.unaryAST1(ast.head(), arg1);

      if (arg1.isList()) {
        final IAST list = (IAST) arg1;

        // If the list contains sublists, map the function over the list
        if (list.argSize() > 0 && list.arg1().isList()) {
          return list.mapThread(codes, 1);
        }

        return fromCharacterCode(list, ast, engine);
      }

      if (arg1.isInteger()) {
        return fromCharacterCode(codes, ast, engine);
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
      int number = ast.arg1().toMachineInt();
      if (F.isPresent(number)) {
        String alphabet = LATIN_ALPHABET;
        if (ast.isAST2()) {
          IExpr arg2 = ast.arg2();
          if (arg2.isList()) {
            return arg2.mapThread(ast, 2);
          }
          // CPD-OFF evaluate prologues of sibling string builtins with different options
          if (arg2.isString()) {
            String str = ast.arg2().toString();
            alphabet = ALPHABET_MAP.get(str);
            if (alphabet == null) {
              String[] strs = ALPHABET_CSV_MAP.get(str);
              if (strs == null) {
                // The alphabet `1` is not known or not available.
                Errors.printMessage(ast.topHead(), "nalph", F.list(ast.arg2()), engine);
                return F.Missing(S.NotAvailable);
                // CPD-ON
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
      S.Capitalize.setEvaluator(new Capitalize(true));
      S.Characters.setEvaluator(new Characters());
      S.CharacterRange.setEvaluator(new CharacterRange());
      S.CreateUUID.setEvaluator(new CreateUUID());
      S.Decapitalize.setEvaluator(new Capitalize(false));
      S.EditDistance.setEvaluator(new EditDistance());

      S.FileNameDrop.setEvaluator(new FileNameDrop());
      S.FileNameJoin.setEvaluator(new FileNameJoin());
      S.ToFileName.setEvaluator(new ToFileName());
      S.LongestCommonSequence.setEvaluator(new LongestCommonSequence(false));
      S.LongestCommonSubsequence.setEvaluator(new LongestCommonSequence(true));
      S.SequenceAlignment.setEvaluator(new SequenceAlignment());
      S.FileNameTake.setEvaluator(new FileNameTake());
      S.FromCharacterCode.setEvaluator(new FromCharacterCode());
      S.FromLetterNumber.setEvaluator(new FromLetterNumber());
      S.LetterNumber.setEvaluator(new LetterNumber());
      S.LetterQ.setEvaluator(new LetterQ());
      S.LowerCaseQ.setEvaluator(new LowerCaseQ());
      S.PrintableASCIIQ.setEvaluator(new PrintableASCIIQ());
      S.StringCases.setEvaluator(new StringCases());
      S.StringCount.setEvaluator(new StringCount());
      S.StringContainsQ.setEvaluator(new StringContainsQ());
      S.StringDelete.setEvaluator(new StringDelete());
      S.StringDrop.setEvaluator(new StringDrop());
      S.StringExtract.setEvaluator(new StringExtract());
      S.StringExpression.setEvaluator(new StringExpression());
      S.StringFreeQ.setEvaluator(new StringFreeQ());
      S.StringForm.setEvaluator(new StringForm());
      S.StringFormat.setEvaluator(new StringFormat());
      S.StringInsert.setEvaluator(new StringInsert());
      S.StringJoin.setEvaluator(new StringJoin());
      S.StringLength.setEvaluator(new StringLength());
      S.IntegerString.setEvaluator(new IntegerString());
      S.StringPadLeft.setEvaluator(new StringPad(true));
      S.StringPadRight.setEvaluator(new StringPad(false));
      S.StringMatchQ.setEvaluator(new StringMatchQ());
      S.StringPart.setEvaluator(new StringPart());
      S.StringPosition.setEvaluator(new StringPosition());
      S.StringRepeat.setEvaluator(new StringRepeat());
      S.StringReplace.setEvaluator(new StringReplace());
      S.StringReplaceList.setEvaluator(new StringReplaceList());
      S.StringReverse.setEvaluator(new StringReverse());
      S.StringRotateLeft.setEvaluator(new StringRotate(true));
      S.StringRotateRight.setEvaluator(new StringRotate(false));
      S.StringRiffle.setEvaluator(new StringRiffle());
      S.StringSplit.setEvaluator(new StringSplit());
      S.StringStartsQ.setEvaluator(new StringStartsQ());
      S.StringTake.setEvaluator(new StringTake());
      S.StringTakeDrop.setEvaluator(new StringTakeDrop());
      S.StringReplacePart.setEvaluator(new StringReplacePart());
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

      // if (!Config.FUZZY_PARSER) {
      S.ToExpression.setEvaluator(new ToExpression());
      // }
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
        // CPD-OFF evaluate prologues of sibling string builtins with different options
        if (ast.isAST2()) {
          String str = ast.arg2().toString();
          alphabet = ALPHABET_MAP.get(str);
          if (alphabet == null) {
            String[] strs = ALPHABET_CSV_MAP.get(str);
            if (strs == null) {
              // The alphabet `1` is not known or not available.
              Errors.printMessage(ast.topHead(), "nalph", F.list(ast.arg2()), engine);
              return F.C0;
              // CPD-ON
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
      return Errors.printMessage(ast.topHead(), "nas", F.list(ast.arg1()), engine);
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
      // each character, one above U+FFFF included
      return obj.toString().codePoints().allMatch(Character::isLetter);
    }
  }

  /**
   * A predicate with one argument, which must be a string: <code>True</code> or <code>False</code>
   * from {@link #test(Object)} on that string.
   */
  private abstract static class AbstractStringPredicate extends AbstractFunctionEvaluator
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
  private static class LowerCaseQ extends AbstractStringPredicate {

    @Override
    public boolean test(final IExpr obj) {
      // each character, one above U+FFFF included
      return obj.toString().codePoints().allMatch(Character::isLowerCase);
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
  private static class PrintableASCIIQ extends AbstractStringPredicate {

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
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
   * The first argument of a string function evaluated, threaded over a list or reported with the
   * <code>strse</code> message when it is no string.
   *
   * @return the string, the threaded result or <code>F.NIL</code>
   */
  private static IExpr evalStringArg1(final IAST ast, EvalEngine engine) {
    IExpr arg1 = engine.evaluate(ast.arg1());
    if (arg1.isList()) {
      return arg1.mapThread(ast, 1);
    }
    if (!arg1.isString()) {
      // String or list of strings expected at position `1` in `2`.
      return Errors.printMessage(ast.topHead(), "strse", F.list(F.C1, ast), engine);
    }
    return arg1;
  }

  /**
   * The common start of <code>StringDrop</code> and <code>StringTake</code>. `Span(m, n)` denotes
   * the same range as the list `{m, n}`, which both functions already understand, so
   * `StringTake("hello", 2;;4)` needs no separate branch.
   *
   * @return the result of the call, or <code>null</code> when the first argument is a string and
   *         the second no span
   */
  private static IExpr stringPartPrelude(final IAST ast, EvalEngine engine) {
    if (ast.isAST2() && (ast.arg2().isAST(S.Span, 3) || ast.arg2().isAST(S.Span, 4))) {
      return engine.evaluate(ast.setAtCopy(2,
          org.matheclipse.core.eval.util.Sequence.spanToListSpec((IAST) ast.arg2())));
    }
    IExpr arg1 = ast.arg1();
    if (!arg1.isString()) {
      if (arg1.isListOfStrings()) {
        return arg1.mapThread(ast, 1);
      }
      // String or list of strings expected at position `1` in `2`.
      return Errors.printMessage(ast.topHead(), "strse", F.list(F.C1, ast), engine);
    }
    return null;
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
  // CPD-OFF evaluate prologues of sibling string builtins with different options
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
          int overlaps = StringPatternMatcher.overlaps(option[1]);
          // CPD-ON
          int limit = occurrenceLimit(ast, argSize, 3, engine);
          if (limit < 0) {
            return F.NIL;
          }

          // The pattern is evaluated: a pattern object evaluates to itself, while a regular
          // expression is usually built - RegularExpression["(\\w*)=\\{(" <> inner <> ")\\}"] -
          // and holding it leaves the StringJoin unevaluated. A RuleDelayed still holds its right
          // hand side, which is what makes the difference between -> and :> here.
          IExpr arg2 = engine.evaluate(ast.arg2());
          // A rule says what to make of each match rather than answering the matched text:
          // StringCases[s, RegularExpression["(\\w+)=(\\w+)"] -> ("$1" -> "$2")]
          StringPatternMatcher.Rules rules =
              StringPatternMatcher.Rules.of(arg2, true, false, ignoreCase, ast, engine);
          if (rules == null) {
            return F.NIL;
          }
          rules.reset(arg1.toString());
          IASTAppendable result = F.ListAlloc();
          if (limit > 0) {
            rules.forEach(overlaps, () -> {
              result.append(rules.value(engine));
              return result.argSize() < limit;
            }, engine);
          }
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_5_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IgnoreCase, S.Overlaps, S.MetaCharacters},
          new IExpr[] {S.False, S.False, S.Automatic});
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
  /**
   * Test the string pattern at position 2 of <code>ast</code> on the string at position 1 with
   * <code>test</code>; a list of strings at position 1 is threaded over.
   *
   * @return {@link S#True}, {@link S#False} or {@link F#NIL} if the arguments are no string and
   *         string pattern
   */
  private static IExpr stringPatternQ(final IAST ast, boolean ignoreCase, final EvalEngine engine,
      Predicate<StringPatternMatcher> test) {
    IExpr arg1 = engine.evaluate(ast.arg1());
    if (arg1.isList()) {
      return arg1.mapThread(ast, 1);
    }
    // a core evaluator gets its arguments unevaluated, and a string pattern is often
    // built rather than written out: a RegularExpression joined from pieces, or a
    // list of alternatives kept in a variable
    IExpr stringPattern = engine.evaluate(ast.arg2());
    if (arg1.isString() && !stringPattern.isRuleAST()) {
      StringPatternMatcher matcher =
          StringPatternMatcher.compile(stringPattern, false, ignoreCase, ast, engine);
      if (matcher == null) {
        return F.NIL;
      }
      matcher.reset(arg1.toString());
      return F.booleSymbol(test.test(matcher));
    }
    return F.NIL;
  }

  private static class StringContainsQ extends AbstractCoreFunctionOptionEvaluator {

    @Override
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {
      if (argSize >= 2) {
        return stringPatternQ(ast, option[0].isTrue(), engine, matcher -> matcher.find(0));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IgnoreCase, S.MetaCharacters},
          new IExpr[] {S.False, S.Automatic});
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
  // CPD-OFF evaluate prologues of sibling string builtins with different options
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
          int overlaps = StringPatternMatcher.overlaps(option[1]);
          // CPD-ON
          IExpr arg2 = engine.evaluate(ast.arg2());
          StringPatternMatcher.Rules rules =
              StringPatternMatcher.Rules.of(arg2, false, false, ignoreCase, ast, engine);
          if (rules == null) {
            return F.NIL;
          }
          rules.reset(arg1.toString());
          int[] counter = new int[1];
          rules.forEach(overlaps, () -> {
            counter[0]++;
            return true;
          }, engine);
          return F.ZZ(counter[0]);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // there is no operator form StringCount[p]
      return ARGS_2_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IgnoreCase, S.Overlaps, S.MetaCharacters},
          new IExpr[] {S.False, S.False, S.Automatic});
    }
  }

  private static class StringDrop extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr result = stringPartPrelude(ast, engine);
      if (result != null) {
        return result;
      }
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (isListOfSpecs(arg2, false)) {
        return F.NIL;
      }
      CodePointString s = new CodePointString(arg1.toString());
      int length = s.length();
      int[] positions = specPositions(arg2, length, engine);
      if (positions == null) {
        // Cannot drop positions `1` through `2` in `3`.
        return Errors.printMessage(ast.topHead(), "drop", specBounds(arg2, arg1), engine);
      }
      boolean[] dropped = new boolean[length];
      for (int p : positions) {
        dropped[p] = true;
      }
      StringBuilder buf = new StringBuilder();
      int start = -1;
      for (int i = 0; i <= length; i++) {
        if (i < length && !dropped[i]) {
          if (start < 0) {
            start = i;
          }
        } else if (start >= 0) {
          buf.append(s.substring(start, i));
          start = -1;
        }
      }
      return F.$str(buf.toString());
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
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.ONEIDENTITY, Attribute.FLAT);
    }
  }

  private static class StringForm extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isString()) {
        String[] args = new String[ast.argSize() - 1];
        for (int i = 2; i < ast.size(); i++) {
          args[i - 2] = ast.get(i).toString();
        }
        String templateString = arg1.toString();
        // int indexOf = templateString.indexOf("``");
        // if (indexOf >= 0) {
        // StringBuilder buf = new StringBuilder(templateString.length() + 10);
        // int lastIndex = 0;
        // int counter = 1;
        // while (indexOf >= 0) {
        // buf.append(templateString.substring(lastIndex, indexOf + 1));
        // if (counter - 1 >= args.length) {
        // // Item `1` requested in `2` out of range. `3` itms available.
        // return Errors.printMessage(S.StringForm, "sfr",
        // F.List(F.ZZ(counter), arg1, F.ZZ(args.length)), engine);
        // }
        // buf.append(counter++);
        // buf.append("`");
        // lastIndex = indexOf + 2;
        // indexOf = templateString.indexOf("``", indexOf + 2);
        // }
        // if (lastIndex > 0 && lastIndex < templateString.length()) {
        // buf.append(templateString.substring(lastIndex));
        // }
        // templateString = buf.toString();
        // }
        String newTemplateString = templateString.replace("`.`", "'");
        while (!newTemplateString.equals(templateString)) {
          templateString = newTemplateString;
          newTemplateString = templateString.replace("`.`", "'");
        }
        String str = Errors.templateRender(templateString, args);
        return F.stringx(str);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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

    private static IExpr stringFreeQ(IAST ast, IExpr arg1, IExpr arg2, boolean ignoreCase,
        EvalEngine engine) {
      StringPatternMatcher matcher =
          StringPatternMatcher.compile(arg2, false, ignoreCase, ast, engine);
      if (matcher == null) {
        return F.NIL;
      }
      matcher.reset(arg1.toString());
      return F.booleSymbol(!matcher.find(0));
    }

    @Override
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {

      if (argSize >= 2) {
        boolean ignoreCase = option[0].isTrue();

        IExpr arg1 = evalStringArg1(ast, engine);
        if (!arg1.isString()) {
          return arg1;
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

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IgnoreCase, S.MetaCharacters},
          new IExpr[] {S.False, S.Automatic});
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
      String str1 = arg1.toString();
      String str2 = arg2.toString();
      int[] listOfInts;
      if (ast.arg3().isList()) {
        listOfInts = Validate.checkListOfInts(ast, ast.arg3(), -str1.length() - 1,
            str1.length() + 1, engine);
      } else {
        int pos = ast.arg3().toMachineInt();
        if (Math.abs(pos) > str1.length() + 1) {
          return Errors.printMessage(ast.topHead(), "ins", F.list(ast.arg3(), arg1), engine);
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
            return Errors.printMessage(ast.topHead(), "ins", F.list(F.C0, arg1), engine);
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
        Errors.rethrowsInterruptException(rex);
        // example java.lang.StringIndexOutOfBoundsException
        Errors.printMessage(S.InputForm, rex, EvalEngine.get());
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
      if (ast.isAST1() && ast.arg1().isString()) {
        return ast.arg1();
      }
      // the strings may stand in lists, nested ones too; nothing joined is the empty string -
      // a notebook joins the strings of an empty row this way
      StringBuilder buf = new StringBuilder();
      for (int i = 1; i < ast.size(); i++) {
        if (!appendStrings(buf, ast.get(i))) {
          // String expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "string", F.list(F.ZZ(i), ast), engine);
        }
      }
      return F.$str(buf.toString());
    }

    /** Append the strings of <code>expr</code>; <code>false</code> if something else is found. */
    private static boolean appendStrings(StringBuilder buf, IExpr expr) {
      if (expr.isString()) {
        buf.append(expr.toString());
        return true;
      }
      if (expr.isList()) {
        IAST list = (IAST) expr;
        for (int i = 1; i < list.size(); i++) {
          if (!appendStrings(buf, list.get(i))) {
            return false;
          }
        }
        return true;
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.FLAT, Attribute.ONEIDENTITY);
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
  /**
   * <code>IntegerString(n)</code>, <code>IntegerString(n, b)</code>,
   * <code>IntegerString(n, b, len)</code> - the digits of <code>Abs(n)</code> in base
   * <code>b</code> (2 to 36, digits beyond 9 as lower case letters), padded on the left with
   * <code>0</code> to <code>len</code> digits, or cut to the last <code>len</code> of them.
   */
  private static class IntegerString extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      if (!n.isInteger()) {
        return F.NIL;
      }
      if (ast.isAST2() && ast.arg2().isString("Roman")) {
        // the digits of the absolute value, as for any base
        int value = n.toIntDefault();
        if (value != Integer.MIN_VALUE) {
          value = Math.abs(value);
        }
        return value >= 0 && value < 5000 ? F.stringx(toRoman(value)) : F.NIL;
      }
      int base = 10;
      if (ast.argSize() >= 2) {
        base = ast.arg2().toIntDefault();
        if (base < 2 || base > 36) {
          return F.NIL;
        }
      }
      String digits =
          ((org.matheclipse.core.interfaces.IInteger) n).toBigNumerator().abs().toString(base);
      if (ast.argSize() >= 3) {
        int length = ast.arg3().toIntDefault();
        if (length < 0) {
          return F.NIL;
        }
        if (digits.length() > length) {
          digits = digits.substring(digits.length() - length);
        } else if (digits.length() < length) {
          digits = "0".repeat(length - digits.length()) + digits;
        }
      }
      return F.stringx(digits);
    }

    private static final int[] ROMAN_VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

    private static final String[] ROMAN_DIGITS =
        {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    /** The Roman numeral of <code>0 &lt;= n &lt; 5000</code>; 0 is <code>N</code>. */
    private static String toRoman(int n) {
      if (n == 0) {
        return "N";
      }
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < ROMAN_VALUES.length; i++) {
        while (n >= ROMAN_VALUES[i]) {
          buf.append(ROMAN_DIGITS[i]);
          n -= ROMAN_VALUES[i];
        }
      }
      return buf.toString();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  /**
   * <code>StringPadLeft(s, n)</code>, <code>StringPadLeft(s, n, pad)</code>,
   * <code>StringPadLeft({s1, s2, ...})</code>,
   * <code>StringPadLeft({s1, s2, ...}, Automatic, pad)</code> and the <code>StringPadRight</code>
   * forms - a string padded to <code>n</code> characters, with spaces or with repetitions of
   * <code>pad</code>, or cut to <code>n</code> characters; a list of strings is padded to its
   * longest one when no width, or <code>Automatic</code>, is given.
   *
   * <p>
   * The repetitions of <code>pad</code> are lined up with the far end of the finished string - its
   * right end for <code>StringPadLeft</code>, its left end for <code>StringPadRight</code>.
   * <code>StringPadLeft["abc", 9, "xy"]</code> is <code>"yxyxyxabc"</code>,
   * <code>StringPadRight["abc", 9, "xy"]</code> is <code>"abcyxyxyx"</code>, and a string longer
   * than <code>n</code> keeps its right end (<code>StringPadLeft</code>) or its left end
   * (<code>StringPadRight</code>).
   */
  private static class StringPad extends AbstractFunctionEvaluator {
    private final boolean left;

    StringPad(boolean left) {
      this.left = left;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      String padding = " ";
      if (ast.argSize() >= 3) {
        if (!ast.arg3().isString() || ast.arg3().toString().isEmpty()) {
          return F.NIL;
        }
        padding = ast.arg3().toString();
      }
      boolean longest = ast.argSize() == 1 || ast.arg2() == S.Automatic;
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        if (list.isEmpty()) {
          // nothing to pad
          return F.CEmptyString;
        }
        int n = -1;
        if (!longest) {
          n = ast.arg2().toIntDefault();
          if (n < 0) {
            return F.NIL;
          }
        }
        for (IExpr s : list) {
          if (!s.isString()) {
            return F.NIL;
          }
          if (longest) {
            n = Math.max(n, s.toString().length());
          }
        }
        IASTAppendable result = F.ListAlloc(list.argSize());
        for (IExpr s : list) {
          result.append(F.stringx(pad(s.toString(), Math.max(n, 0), padding)));
        }
        return result;
      }
      if (!arg1.isString()) {
        // String expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "string", F.list(F.C1, ast), engine);
      }
      if (longest) {
        // the length of the longest is only defined for a list of strings
        return ast.argSize() == 1 ? F.NIL : arg1;
      }
      int n = ast.arg2().toIntDefault();
      if (n < 0) {
        return F.NIL;
      }
      return F.stringx(pad(arg1.toString(), n, padding));
    }

    /** <code>s</code> padded, or cut, to <code>n</code> characters. */
    private String pad(String s, int n, String padding) {
      int length = s.length();
      if (n <= length) {
        return left ? s.substring(length - n) : s.substring(0, n);
      }
      int m = padding.length();
      StringBuilder buf = new StringBuilder(n);
      if (left) {
        for (int k = 0; k < n - length; k++) {
          // counted from the right end of the finished string
          int fromRight = n - 1 - k;
          buf.append(padding.charAt(m - 1 - (fromRight % m)));
        }
        buf.append(s);
      } else {
        buf.append(s);
        for (int k = length; k < n; k++) {
          // counted from the left end of the finished string
          buf.append(padding.charAt(k % m));
        }
      }
      return buf.toString();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static class StringLength extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString()) {
        String str = ast.arg1().toString();
        return F.ZZ(str.codePointCount(0, str.length()));
      }
      // String expected at position `1` in `2`.
      return Errors.printMessage(ast.topHead(), "string", F.list(F.C1, ast), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
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
    // CPD-OFF evaluate prologues of sibling string builtins with different options
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {

      if (argSize >= 2) {
        boolean ignoreCase = option[0].isTrue();

        IExpr arg1 = evalStringArg1(ast, engine);
        if (!arg1.isString()) {
          return arg1;
        }
        // a core evaluator gets its arguments unevaluated, and a string pattern is often
        // built rather than written out: a RegularExpression joined from pieces, or a
        // list of alternatives kept in a variable
        IExpr arg2 = engine.evaluate(ast.arg2());

        // only here are * and @ in a literal string wildcards
        StringPatternMatcher matcher =
        // CPD-ON
            StringPatternMatcher.compile(arg2, true, ignoreCase, ast, engine);
        if (matcher == null) {
          return F.NIL;
        }
        try {
          matcher.reset(arg1.toString());
          return F.booleSymbol(matcher.matches());
        } catch (StackOverflowError soe) {
          // Regex expression `1` error message: `2`.
          Errors.printMessage(ast.topHead(), "zzregex",
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
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IgnoreCase, S.MetaCharacters},
          new IExpr[] {S.False, S.Automatic});
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
      IExpr arg1 = ast.arg1();
      if (!arg1.isString()) {
        if (arg1.isListOfStrings()) {
          return arg1.mapThread(ast, 1);
        }
        return F.NIL;
      }
      CodePointString str = new CodePointString(arg1.toString());
      int length = str.length();
      IExpr arg2 = ast.arg2();
      int[] positions;
      if (arg2 == S.All) {
        positions = rangePositions(1, length, 1, length);
      } else if (arg2.isAST(S.Span, 3) || arg2.isAST(S.Span, 4)) {
        IAST spec = org.matheclipse.core.eval.util.Sequence.spanToListSpec((IAST) arg2);
        if (!spec.forAll(x -> x.isInteger())) {
          return F.NIL;
        }
        positions = rangePositions(spec.arg1().toLongDefault(), spec.arg2().toLongDefault(),
            spec.isAST3() ? spec.arg3().toLongDefault() : 1, length);
        if (positions == null) {
          // Cannot take positions `1` through `2` in `3`.
          return Errors.printMessage(ast.topHead(), "take", F.list(spec.arg1(), spec.arg2(), arg1),
              engine);
        }
      } else if (arg2.isList() || arg2.isInteger()) {
        IAST list = arg2.isList() ? (IAST) arg2 : F.list(arg2);
        positions = new int[list.argSize()];
        for (int i = 1; i < list.size(); i++) {
          int part = list.get(i).toIntDefault();
          if (part < 0 && part != Integer.MIN_VALUE) {
            part = length + part + 1;
          }
          if (part < 1 || part > length) {
            // Part `1` of `2` does not exist.
            return Errors.printMessage(ast.topHead(), "partw", F.list(list.get(i), arg1), engine);
          }
          positions[i - 1] = part - 1;
        }
        if (arg2.isInteger()) {
          return F.stringx(str.substring(positions[0], positions[0] + 1));
        }
      } else {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(positions.length);
      for (int p : positions) {
        result.append(F.stringx(str.substring(p, p + 1)));
      }
      return result;
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
        boolean ignoreCase = option[0].isTrue();
        // the matches of StringPosition overlap unless Overlaps -> False says otherwise
        int overlaps = StringPatternMatcher.overlaps(option[1]);
        int limit = occurrenceLimit(ast, argSize, 3, engine);
        if (limit < 0) {
          return F.NIL;
        }

        // CPD-OFF evaluate prologues of sibling string builtins with different options
        IExpr arg1 = evalStringArg1(ast, engine);
        if (!arg1.isString()) {
          return arg1;
        }

        IExpr arg2 = engine.evaluate(ast.arg2());
        StringPatternMatcher.Rules rules =
            StringPatternMatcher.Rules.of(arg2, false, false, ignoreCase, ast, engine);
        if (rules == null) {
          return F.NIL;
        }
        // CPD-ON
        String str = arg1.toString();
        CodePointString positions = new CodePointString(str);
        rules.reset(str);
        IASTAppendable result = F.ListAlloc();
        if (limit > 0) {
          try {
            rules.forEach(overlaps, () -> {
              result.append(F.list(F.ZZ(positions.position(rules.start()) + 1),
                  F.ZZ(positions.position(rules.end()))));
              return result.argSize() < limit;
            }, engine);
          } catch (StackOverflowError soe) {
            return F.NIL;
          }
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_5_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IgnoreCase, S.Overlaps, S.MetaCharacters},
          new IExpr[] {S.False, S.True, S.Automatic});
    }
  }

  private static class StringRepeat extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString()) {
        String str = ast.arg1().toString();
        int n = ast.arg2().toMachineInt();
        if (n < 0) {
          // Positive integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intp", F.List(ast, F.C2), engine);
        }
        int max = Integer.MAX_VALUE;
        if (ast.isAST3()) {
          max = ast.arg3().toMachineInt();
          if (max < 0) {
            // Positive integer expected at position `2` in `1`.
            return Errors.printMessage(ast.topHead(), "intp", F.List(ast, F.C3), engine);
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
  /**
   * The rules of <code>StringReplace(s, rules, n)</code> and its occurrence limit <code>n</code>.
   */
  private static final class Replacements {
    final StringPatternMatcher.Rules rules;
    final int limit;

    private Replacements(StringPatternMatcher.Rules rules, int limit) {
      this.rules = rules;
      this.limit = limit;
    }

    /**
     * The replacement rules at position 2 of <code>ast</code> and the occurrence limit at position
     * 3.
     *
     * @return <code>null</code> if the second argument is no rule or list of rules or the limit is
     *         invalid
     */
    static Replacements of(final IAST ast, final int argSize, boolean ignoreCase,
        final EvalEngine engine) {
      IExpr arg2 = ast.arg2();
      if (!arg2.isListOfRules(false) && !arg2.isRuleAST()) {
        // rules written out stay as they are, so that their right-hand sides are evaluated only
        // for each match; rules held in a variable or made by a function have to be fetched
        arg2 = engine.evaluate(arg2);
      }
      if (!arg2.isListOfRules(false) && !arg2.isRuleAST()) {
        return null;
      }
      int limit = occurrenceLimit(ast, argSize, 3, engine);
      if (limit < 0) {
        return null;
      }
      StringPatternMatcher.Rules rules =
          StringPatternMatcher.Rules.of(arg2, true, false, ignoreCase, ast, engine);
      return rules == null ? null : new Replacements(rules, limit);
    }
  }

  // CPD-OFF evaluate prologues of sibling string builtins with different options
  private static class StringReplace extends AbstractCoreFunctionOptionEvaluator {

    @Override
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {

      if (argSize >= 2) {
        boolean ignoreCase = option[0].isTrue();
        IExpr arg1 = evalStringArg1(ast, engine);
        if (!arg1.isString()) {
          return arg1;
        }
        Replacements replacements = Replacements.of(ast, argSize, ignoreCase, engine);
        if (replacements == null) {
          return F.NIL;
        }
        // CPD-ON
        // the rules are tried together: at each position the first which matches replaces it,
        // and what a replacement inserted is not looked at again
        replacements.rules.reset(arg1.toString());
        return replaceMatches(replacements.rules, replacements.limit, F.NIL, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IgnoreCase, S.MetaCharacters},
          new IExpr[] {S.False, S.Automatic});
    }
  }

  /**
   * The occurrence limit <code>n</code> of <code>StringCases[s, p, n]</code> and similar
   * functions at <code>position</code>: a non-negative integer, <code>Infinity</code> or nothing
   * for no limit; <code>-1</code> for anything else.
   */
  private static int occurrenceLimit(IAST ast, int argSize, int position, EvalEngine engine) {
    if (argSize < position) {
      return Integer.MAX_VALUE;
    }
    IExpr n = engine.evaluate(ast.get(position));
    if (n.isInfinity()) {
      return Integer.MAX_VALUE;
    }
    int limit = n.toIntDefault();
    if (limit < 0) {
      return -1;
    }
    return limit;
  }

  /**
   * Add the replacement <code>value</code> to the pieces of a string being built. A string joins
   * the text; <code>Sequence[...]</code> adds its elements and <code>Sequence[]</code> or
   * <code>Nothing</code> nothing; anything else makes the result a <code>StringExpression</code>.
   */
  private static void appendPiece(IASTAppendable pieces, StringBuilder text, IExpr value) {
    if (value.isString()) {
      text.append(value.toString());
    } else if (value.isAST(S.Sequence)) {
      IAST sequence = (IAST) value;
      for (int i = 1; i < sequence.size(); i++) {
        appendPiece(pieces, text, sequence.get(i));
      }
    } else if (value != S.Nothing) {
      if (text.length() > 0) {
        pieces.append(F.stringx(text.toString()));
        text.setLength(0);
      }
      pieces.append(value);
    }
  }

  /**
   * The string or <code>StringExpression</code> the pieces make: a string if every replacement
   * was one.
   */
  private static IExpr joinPieces(IASTAppendable pieces, StringBuilder text) {
    if (pieces.argSize() == 0) {
      return F.stringx(text.toString());
    }
    if (text.length() > 0) {
      pieces.append(F.stringx(text.toString()));
    }
    return pieces;
  }

  /**
   * The string searched by <code>rules</code> with its first <code>limit</code> matches replaced -
   * by <code>fixedValue</code> if it is present, otherwise by what the matching rule gives.
   */
  private static IExpr replaceMatches(StringPatternMatcher.Rules rules, int limit,
      IExpr fixedValue, EvalEngine engine) {
    final String str = rules.input();
    final IASTAppendable pieces = F.ast(S.StringExpression);
    final StringBuilder text = new StringBuilder(str.length() + 16);
    final int[] state = {0, 0}; // the position after the last match, the number of matches
    if (limit > 0) {
      rules.forEach(StringPatternMatcher.OVERLAPS_NONE, () -> {
        text.append(str, state[0], rules.start());
        appendPiece(pieces, text, fixedValue.isPresent() ? fixedValue : rules.value(engine));
        state[0] = rules.end();
        return ++state[1] < limit;
      }, engine);
    }
    text.append(str, state[0], str.length());
    return joinPieces(pieces, text);
  }

  /**
   * <code>StringDelete[s, p]</code> - the string with the matches of <code>p</code> removed.
   */
  private static class StringDelete extends AbstractCoreFunctionOptionEvaluator {

    @Override
    // CPD-OFF evaluate prologues of sibling string builtins with different options
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {
      if (argSize >= 2) {
        boolean ignoreCase = option[0].isTrue();
        IExpr arg1 = evalStringArg1(ast, engine);
        if (!arg1.isString()) {
          return arg1;
        }
        IExpr arg2 = engine.evaluate(ast.arg2());
        StringPatternMatcher.Rules rules =
            StringPatternMatcher.Rules.of(arg2, false, false, ignoreCase, ast, engine);
        if (rules == null) {
          return F.NIL;
        }
        rules.reset(arg1.toString());
        return replaceMatches(rules, Integer.MAX_VALUE, F.CEmptyString, engine);
      }
      // CPD-ON
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IgnoreCase, S.MetaCharacters},
          new IExpr[] {S.False, S.Automatic});
    }
  }

  /**
   * <code>StringReplaceList[s, rules, n]</code> - the strings each of which has one match of the
   * rules replaced, the matches taken at every position, overlapping ones included.
   */
  // CPD-OFF evaluate prologues of sibling string builtins with different options
  private static class StringReplaceList extends AbstractCoreFunctionOptionEvaluator {

    @Override
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {
      if (argSize >= 2) {
        boolean ignoreCase = option[0].isTrue();
        IExpr arg1 = evalStringArg1(ast, engine);
        if (!arg1.isString()) {
          return arg1;
        }
        Replacements replacements = Replacements.of(ast, argSize, ignoreCase, engine);
        if (replacements == null) {
          return F.NIL;
        }
        StringPatternMatcher.Rules rules = replacements.rules;
        // CPD-ON
        int limit = replacements.limit;
        final String str = arg1.toString();
        rules.reset(str);
        // each match is {start, end, rule}; its replacement is kept in values
        List<int[]> matches = new java.util.ArrayList<int[]>();
        List<IExpr> values = new java.util.ArrayList<IExpr>();
        for (int i = 0; i < rules.size(); i++) {
          int from = 0;
          while (from <= str.length() && rules.find(i, from)) {
            engine.checkTimeLimit();
            matches.add(new int[] {rules.start(), rules.end(), i, values.size()});
            values.add(rules.value(engine));
            if (rules.start() >= str.length()) {
              break;
            }
            from = str.offsetByCodePoints(rules.start(), 1);
          }
        }
        // from left to right, and at one position in the order of the rules
        matches.sort((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0])
            : Integer.compare(a[2], b[2]));
        IASTAppendable result = F.ListAlloc(Math.min(limit, matches.size()));
        for (int[] match : matches) {
          if (result.argSize() >= limit) {
            break;
          }
          IASTAppendable pieces = F.ast(S.StringExpression);
          StringBuilder text = new StringBuilder(str.length() + 16);
          text.append(str, 0, match[0]);
          appendPiece(pieces, text, values.get(match[3]));
          text.append(str, match[1], str.length());
          result.append(joinPieces(pieces, text));
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IgnoreCase, S.MetaCharacters},
          new IExpr[] {S.False, S.Automatic});
    }
  }

  /**
   * The characters (0-based) of a string of <code>n</code> characters a position specification of
   * <code>StringTake</code> or <code>StringDrop</code> names, in the order it names them - or
   * <code>null</code> if they don't exist.
   *
   * <ul>
   * <li><code>k</code> is the first <code>k</code> characters, <code>-k</code> the last ones,
   * <code>UpTo[k]</code> at most <code>k</code> first ones;
   * <li><code>{m}</code> is character <code>m</code>, <code>{m, k}</code> the characters
   * <code>m</code> to <code>k</code>, <code>{m, k, s}</code> every <code>s</code>-th of them -
   * from <code>m</code> down to <code>k</code> if <code>s</code> is negative;
   * <li><code>All</code> is every character, <code>None</code> and <code>{}</code> none.
   * </ul>
   */
  private static int[] specPositions(IExpr spec, int n, EvalEngine engine) {
    if (spec == S.All) {
      return rangePositions(1, n, 1, n);
    }
    if (spec == S.None || spec.isEmptyList()) {
      return new int[0];
    }
    if (spec.isAST(S.UpTo, 2)) {
      int upTo = Validate.checkUpTo((IAST) spec, engine);
      if (F.isNotPresent(upTo)) {
        return null;
      }
      return rangePositions(1, Math.min(upTo, n), 1, n);
    }
    if (spec.isReal()) {
      long k = spec.toLongDefault();
      if (k == Long.MIN_VALUE || Math.abs(k) > n) {
        return null;
      }
      return k >= 0 ? rangePositions(1, k, 1, n) : rangePositions(n + k + 1, n, 1, n);
    }
    if (spec.isList() && spec.argSize() <= 3) {
      long[] values = new long[spec.argSize()];
      for (int i = 0; i < values.length; i++) {
        IExpr value = spec.getAt(i + 1);
        if (!value.isInteger()) {
          return null;
        }
        values[i] = value.toLongDefault();
        if (values[i] == Long.MIN_VALUE) {
          return null;
        }
      }
      if (values.length == 1) {
        long m = values[0] < 0 ? n + values[0] + 1 : values[0];
        if (m < 1 || m > n) {
          return null;
        }
        return new int[] {(int) m - 1};
      }
      return rangePositions(values[0], values[1], values.length == 3 ? values[2] : 1, n);
    }
    return null;
  }

  /**
   * The characters (0-based) <code>m</code>, <code>m + s</code>, … up to <code>k</code> (down to
   * it if <code>s</code> is negative) of a string of <code>n</code> characters, or
   * <code>null</code> if they don't exist. A negative position counts from the end, and so does a
   * start of <code>0</code>: <code>{0, n}</code> is empty like <code>{n + 1, n}</code>.
   */
  private static int[] rangePositions(long m, long k, long s, int n) {
    if (s == 0) {
      return null;
    }
    long from = m <= 0 ? n + m + 1 : m;
    long to = k < 0 ? n + k + 1 : k;
    long count;
    if (s > 0) {
      if (from < 1 || from > n + 1 || to < 0 || to > n || from > to + 1) {
        return null;
      }
      count = from > to ? 0 : (to - from) / s + 1;
    } else {
      if (from < 0 || from > n || to < 1 || to > n + 1 || from < to - 1) {
        return null;
      }
      count = from < to ? 0 : (from - to) / -s + 1;
    }
    int[] positions = new int[(int) count];
    long p = from;
    for (int i = 0; i < count; i++) {
      positions[i] = (int) p - 1;
      p += s;
    }
    return positions;
  }

  /** Whether <code>spec</code> is a list of position specifications rather than one. */
  private static boolean isListOfSpecs(IExpr spec, boolean zeroStepIsList) {
    if (!spec.isList() || spec.argSize() == 0) {
      return false;
    }
    if (spec.argSize() > 3) {
      return true;
    }
    IAST list = (IAST) spec;
    if (!list.forAll(x -> x.isInteger())) {
      return true;
    }
    return zeroStepIsList && list.argSize() == 3 && list.arg3().isZero();
  }

  /** The message arguments <code>m</code> and <code>k</code> of a failed specification. */
  private static IAST specBounds(IExpr spec, IExpr str) {
    if (spec.isList() && spec.argSize() >= 2) {
      return F.list(spec.first(), spec.second(), str);
    }
    if (spec.isList1()) {
      return F.list(spec.first(), spec.first(), str);
    }
    if (spec.isNegative()) {
      return F.list(spec, F.CN1, str);
    }
    return F.list(F.C1, spec, str);
  }

  /**
   * <code>StringTakeDrop[s, spec]</code> - the pair of <code>StringTake[s, spec]</code> and
   * <code>StringDrop[s, spec]</code>.
   */
  private static class StringTakeDrop extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isListOfStrings()) {
        return arg1.mapThread(ast, 1);
      }
      if (!arg1.isString()) {
        // String or list of strings expected at position `1` in `2`.
        return Errors.printMessage(ast.topHead(), "strse", F.list(F.C1, ast), engine);
      }
      IExpr taken = engine.evaluate(F.binaryAST2(S.StringTake, arg1, ast.arg2()));
      if (!taken.isString()) {
        return F.NIL;
      }
      IExpr dropped = engine.evaluate(F.binaryAST2(S.StringDrop, arg1, ast.arg2()));
      if (!dropped.isString()) {
        return F.NIL;
      }
      return F.list(taken, dropped);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>StringRotateLeft[s, n]</code> and <code>StringRotateRight[s, n]</code> - the characters
   * of <code>s</code> cycled <code>n</code> places, one if <code>n</code> is left out.
   */
  private static class StringRotate extends AbstractFunctionEvaluator {
    private final boolean left;

    StringRotate(boolean left) {
      this.left = left;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isListOfStrings()) {
        return arg1.mapThread(ast, 1);
      }
      if (!arg1.isString()) {
        return F.NIL;
      }
      long n = 1;
      if (ast.isAST2()) {
        if (!ast.arg2().isInteger()) {
          return F.NIL;
        }
        n = ast.arg2().toLongDefault();
        if (n == Long.MIN_VALUE) {
          return F.NIL;
        }
      }
      CodePointString s = new CodePointString(arg1.toString());
      int length = s.length();
      if (length == 0) {
        return arg1;
      }
      long shift = (left ? n : -n) % length;
      int k = (int) (shift < 0 ? shift + length : shift);
      return F.stringx(s.substring(k) + s.substring(0, k));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>Capitalize[s]</code> and <code>Decapitalize[s]</code> - the string with its first
   * character in upper (title) case or in lower case.
   */
  private static class Capitalize extends AbstractFunctionEvaluator {
    private final boolean upper;

    Capitalize(boolean upper) {
      this.upper = upper;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isListOfStrings()) {
        return arg1.mapThread(ast, 1);
      }
      if (!arg1.isString()) {
        return F.NIL;
      }
      String str = arg1.toString();
      if (str.isEmpty()) {
        return arg1;
      }
      int cp = str.codePointAt(0);
      int changed = upper ? Character.toTitleCase(cp) : Character.toLowerCase(cp);
      if (changed == cp) {
        return arg1;
      }
      return F.stringx(
          new String(Character.toChars(changed)) + str.substring(Character.charCount(cp)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
        } else if (arg2.isList3()) {
          IAST list = (IAST) arg2;
          left = list.arg1().toString();
          sep1 = list.arg2().toString();
          right = list.arg3().toString();
        } else {
          // String expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "string", F.list(F.C2, ast), engine);
        }
      }
      if (ast.isAST3()) {
        IExpr arg3 = ast.arg3();
        if (arg3.isString()) {
          sep2 = arg3.toString();
        } else {
          // String expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "string", F.list(F.C3, ast), engine);
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
            if (j < row.argSize()) {
              buf.append(sep2);
            }
          }
          if (i < list1.argSize()) {
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
          if (j < list1.argSize()) {
            buf.append(sep1);
          }
        }
        buf.append(right);
        return F.stringx(buf.toString());
      }
      // String expected at position `1` in `2`.
      return Errors.printMessage(S.StringRiffle, "string", F.list(F.C1, ast), engine);
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
  /**
   * <code>StringExtract[string, "delimiter" -&gt; spec, …]</code>: split and take, in one step.
   *
   * <p>
   * Each specification splits what is left of the string on its delimiter and takes the parts the
   * position specification names, so several of them read successively deeper. It is how a text
   * protocol is taken apart -
   * <code>StringExtract[request, "\r\n\r\n" -&gt; 1, "\r\n" -&gt; 2 ;; ]</code> is the header lines
   * of an HTTP request.
   */
  private static class StringExtract extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr current = ast.arg1();
      for (int i = 2; i < ast.size(); i++) {
        current = extract(current, ast.get(i), ast, engine);
        if (current.isNIL()) {
          return F.NIL;
        }
      }
      return current;
    }

    /** One specification, applied to a string or to each of a list of them. */
    private static IExpr extract(IExpr current, IExpr spec, IAST ast, EvalEngine engine) {
      if (current.isList()) {
        IAST list = (IAST) current;
        IASTAppendable result = F.ListAlloc(list.argSize());
        for (int i = 1; i < list.size(); i++) {
          IExpr part = extract(list.get(i), spec, ast, engine);
          if (part.isNIL()) {
            return F.NIL;
          }
          result.append(part);
        }
        return result;
      }
      if (!current.isString()) {
        return F.NIL;
      }
      IExpr delimiter = F.NIL;
      IExpr position = spec;
      if (spec.isRuleAST()) {
        delimiter = spec.first();
        position = spec.second();
      }
      String regex;
      if (delimiter.isPresent()) {
        Map<ISymbol, String> groups = new IdentityHashMap<ISymbol, String>();
        regex =
            IStringX.toRegexString(delimiter, false, ast, IStringX.REGEX_LONGEST, groups, engine);
        if (regex == null) {
          return F.NIL;
        }
      } else {
        // with no delimiter the pieces are the whitespace separated words
        regex = "\\s+";
      }
      String[] pieces;
      try {
        pieces = java.util.regex.Pattern.compile(regex).split(current.toString(), -1);
      } catch (java.util.regex.PatternSyntaxException pse) {
        // Regex expression `1` error message: `2`.
        return Errors.printMessage(S.StringExtract, "zzregex",
            F.list(F.stringx(regex), F.stringx(pse.getMessage())), engine);
      }
      IASTAppendable parts = F.ListAlloc(pieces.length);
      for (String piece : pieces) {
        // an empty piece counts, unlike in StringSplit: a delimiter at the very front leaves one,
        // and it is what makes the part after it the second and not the first
        parts.append(F.stringx(piece));
      }
      if (position.isInteger()) {
        int n = position.toIntDefault();
        if (n == 0 || n == Integer.MIN_VALUE || Math.abs(n) > parts.argSize()) {
          // a piece which isn't there
          return F.binaryAST2(S.Missing, F.stringx("PartAbsent"), position);
        }
      }
      return engine.evaluate(F.Part(parts, position));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

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
        return Errors.printMessage(ast.topHead(), "strse", F.list(F.C1, ast), engine);
      }
      boolean ignoreCase = option[0].isTrue();
      // without a delimiter the pieces are separated by whitespace
      IExpr delimiter = argSize >= 2 ? engine.evaluate(ast.arg2()) : S.Whitespace;
      // StringSplit[s, patt, n] gives at most n pieces: the last one is the rest of the string,
      // delimiters and all, and a piece which is empty is still a piece. `StringSplit["key:",
      // ":", 2]` is {"key", ""}, which is how a line of a configuration file with nothing after
      // its colon is read. All keeps the empty pieces at the ends as well.
      int pieces = Integer.MAX_VALUE;
      boolean keepEmpty = false;
      if (argSize >= 3) {
        keepEmpty = true;
        IExpr arg3 = engine.evaluate(ast.arg3());
        if (arg3 != S.All && !arg3.isInfinity()) {
          pieces = arg3.toIntDefault();
          if (pieces == Integer.MIN_VALUE) {
            return F.NIL;
          }
          if (pieces <= 0) {
            pieces = Integer.MAX_VALUE;
          }
        }
      }
      StringPatternMatcher.Rules rules =
          StringPatternMatcher.Rules.of(delimiter, true, false, ignoreCase, ast, engine);
      if (rules == null) {
        return F.NIL;
      }
      final String str = arg1.toString();
      final int limit = pieces;
      rules.reset(str);
      // the pieces between the delimiters, with what a rule makes of a delimiter in between
      final IASTAppendable result = F.ListAlloc();
      final int[] state = {0, 1}; // where the next piece starts, the number of pieces
      rules.forEach(StringPatternMatcher.OVERLAPS_NONE, () -> {
        if (state[1] >= limit) {
          return false;
        }
        if (rules.start() == rules.end() && (rules.end() == 0 || rules.end() == str.length())) {
          // an empty delimiter at an end separates nothing
          return true;
        }
        result.append(F.stringx(str.substring(state[0], rules.start())));
        if (rules.isRule()) {
          result.append(rules.value(engine));
        }
        state[0] = rules.end();
        state[1]++;
        return true;
      }, engine);
      result.append(F.stringx(str.substring(state[0])));
      if (!keepEmpty) {
        // an empty piece at either end is left out
        if (result.argSize() > 0 && result.arg1().equals(F.CEmptyString)) {
          result.remove(1);
        }
        if (result.argSize() > 0 && result.last().equals(F.CEmptyString)) {
          result.remove(result.argSize());
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IgnoreCase, S.MetaCharacters},
          new IExpr[] {S.False, S.Automatic});
    }
  }

  private static class StringStartsQ extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      if (argSize >= 2) {
        return stringPatternQ(ast, options[0].isTrue(), engine, StringPatternMatcher::lookingAt);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IgnoreCase, S.MetaCharacters},
          new IExpr[] {S.False, S.Automatic});
    }
  }

  /**
   * <code>StringReplacePart[str, new, {m, n}]</code> and the list form
   * <code>StringReplacePart[str, {new1, …}, {{m1, n1}, …}]</code>: the characters from
   * <code>m</code> to <code>n</code> replaced by something else.
   *
   * <p>
   * Positions count in the original string, so several replacements do not have to be adjusted for
   * one another - which is what makes this the way to put something back where a scan found it.
   */
  private static class StringReplacePart extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!ast.isAST3() || !arg1.isString()) {
        // StringReplacePart(snew, spec) is the operator form; a list of strings is not threaded
        // over
        return F.NIL;
      }
      CodePointString str = new CodePointString(arg1.toString());
      final int length = str.length();
      IExpr replacements = ast.arg2();
      IExpr positions = ast.arg3();

      IAST positionList;
      IAST replacementList;
      if (positions.isListOfLists()) {
        positionList = (IAST) positions;
        if (replacements.isList()) {
          if (replacements.size() != positionList.size()) {
            // there is no sensible pairing of `1` with `2`
            return Errors.printMessage(ast.topHead(), "incpt", F.list(ast), engine);
          }
          replacementList = (IAST) replacements;
        } else {
          // one replacement used at every position
          IASTAppendable repeated = F.ListAlloc(positionList.argSize());
          for (int i = 1; i < positionList.size(); i++) {
            repeated.append(replacements);
          }
          replacementList = repeated;
        }
      } else if (positions.isList2() || positions.isList1() || positions.isInteger()) {
        positionList = F.list(positions);
        replacementList = F.list(replacements);
      } else {
        return F.NIL;
      }

      int count = positionList.argSize();
      // the characters from - 1 to to - 1 (0-based, to exclusive) of each range
      int[][] spans = new int[count][2];
      for (int i = 0; i < count; i++) {
        IExpr span = positionList.get(i + 1);
        if (!replacementList.get(i + 1).isString()) {
          return F.NIL;
        }
        long from;
        long to;
        if (span.isInteger()) {
          // n is the first n characters, -n the last n
          long n = span.toLongDefault();
          if (n == Long.MIN_VALUE) {
            return F.NIL;
          }
          from = n >= 0 ? 1 : length + n + 1;
          to = n >= 0 ? n : length;
        } else if (span.isList1() || span.isList2()) {
          from = span.first().toLongDefault();
          to = span.isList1() ? from : span.second().toLongDefault();
          if (from == Long.MIN_VALUE || to == Long.MIN_VALUE) {
            return F.NIL;
          }
          if (from < 0) {
            from = length + from + 1;
          }
          if (to < 0) {
            to = length + to + 1;
          }
        } else {
          return F.NIL;
        }
        if (from < 1 || to > length || to < from - 1) {
          // Cannot take positions `1` through `2` in `3`.
          return Errors.printMessage(ast.topHead(), "take",
              F.list(F.ZZ(from), F.ZZ(to), arg1), engine);
        }
        spans[i][0] = (int) from - 1;
        spans[i][1] = (int) to;
      }
      // A range which overlaps one listed before it is left out; the others are applied from the
      // end, so that every position still refers to the original string when its turn comes.
      // Of two insertions at one place the one listed first comes first.
      boolean[] skipped = new boolean[count];
      for (int i = 0; i < count; i++) {
        for (int j = 0; j < i && !skipped[i]; j++) {
          if (!skipped[j] && spans[i][0] < spans[i][1] && spans[j][0] < spans[j][1]
              && spans[i][0] < spans[j][1] && spans[j][0] < spans[i][1]) {
            skipped[i] = true;
          }
        }
      }
      Integer[] order = new Integer[count];
      for (int i = 0; i < count; i++) {
        order[i] = i;
      }
      java.util.Arrays.sort(order,
          (a, b) -> spans[a][0] != spans[b][0] ? Integer.compare(spans[b][0], spans[a][0])
              : Integer.compare(b, a));

      CodePointString current = str;
      for (int index : order) {
        if (skipped[index]) {
          continue;
        }
        String replacement = replacementList.get(index + 1).toString();
        int end = Math.min(spans[index][1], current.length());
        int start = Math.min(spans[index][0], end);
        current = new CodePointString(
            current.substring(0, start) + replacement + current.substring(end));
      }
      return F.stringx(current.toString());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_1;
    }
  }

  private static class StringTake extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr result = stringPartPrelude(ast, engine);
      if (result != null) {
        return result;
      }
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (isListOfSpecs(arg2, true)) {
        // StringTake[str, {{m1, n1}, {m2, n2}, …}] takes several pieces at once and answers a
        // list of them. That is how a caller cuts a file into the pieces between the positions
        // something else told it about.
        return ((IAST) arg2).mapThread(ast, 2);
      }
      CodePointString s = new CodePointString(arg1.toString());
      int[] positions = specPositions(arg2, s.length(), engine);
      if (positions == null) {
        // Cannot take positions `1` through `2` in `3`.
        return Errors.printMessage(ast.topHead(), "take", specBounds(arg2, arg1), engine);
      }
      int last = positions.length - 1;
      if (positions.length > 0 && positions[last] - positions[0] == last) {
        // one run of characters
        return F.$str(s.substring(positions[0], positions[positions.length - 1] + 1));
      }
      StringBuilder buf = new StringBuilder();
      for (int p : positions) {
        buf.append(s.substring(p, p + 1));
      }
      return F.$str(buf.toString());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class StringTemplate extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      final IExpr head = ast.head();
      if (head.isAST(S.StringTemplate, 2)) {
        if (ast.isAST1()) {
          return S.TemplateApply.of(engine, head, ast.arg1());
        }
        // more than one argument fills the slots in order, as TemplateApply does from a list
        return S.TemplateApply.of(engine, head, ast.setAtCopy(0, S.List));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return null;
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

  private static class StringTrim extends AbstractCoreFunctionOptionEvaluator {

    /** Whitespace at the ends, the no-break space and the other Unicode spaces included. */
    private static final java.util.regex.Pattern WHITESPACE_ENDS = java.util.regex.Pattern
        .compile("\\A\\s+|\\s+\\z", java.util.regex.Pattern.UNICODE_CHARACTER_CLASS);

    @Override
    protected IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine) {
      IExpr arg1 = engine.evaluate(ast.arg1());
      if (arg1.isList()) {
        if (arg1.isEmptyList()) {
          // an empty list of strings
          return arg1;
        }
        if (!arg1.isListOfStrings()) {
          // String or list of strings expected at position `1` in `2`.
          return Errors.printMessage(ast.topHead(), "strse", F.list(F.C1, ast), engine);
        }
        return arg1.mapThread(ast, 1);
      }
      if (!arg1.isString()) {
        return F.NIL;
      }
      String str = arg1.toString();
      if (argSize == 1) {
        return F.$str(WHITESPACE_ENDS.matcher(str).replaceAll(""));
      }
      if (argSize == 2) {
        boolean ignoreCase = option[0].isTrue();
        IExpr arg2 = engine.evaluate(ast.arg2());
        StringPatternMatcher matcher =
            StringPatternMatcher.compile(arg2, false, ignoreCase, ast, engine);
        if (matcher == null) {
          return F.NIL;
        }
        // one match at the start and one at the end are removed - p.. removes runs of them
        matcher.reset(str);
        if (matcher.lookingAt()) {
          str = str.substring(matcher.end());
          matcher.reset(str);
        }
        if (matcher.findSuffix(0)) {
          str = str.substring(0, matcher.start());
        }
        return F.$str(str);
      }
      return F.NIL;
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

  private static class TemplateApply extends AbstractFunctionEvaluator {

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
        condition = F.subst(condition, x -> replaceTemplateSlotFunction(x, map));
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

    /**
     * Replace all {@link S#TemplateSlot} expressions in an {@link IAST} tree.
     *
     * @param templateExpr
     * @param args
     * @return
     */
    protected static IExpr templateApplyTemplateSlot(IExpr templateExpr, IExpr args) {
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
      } else if (args.isPresent()) {
        // one parameter which is not a list fills the first slot
        context.put(F.C1, args);
      }
      return F.subst(templateExpr, x -> replaceTemplateSlotFunction(x, context));
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = F.NIL;
      if (ast.isAST2()) {
        arg2 = ast.arg2();
      }
      if (arg1.isString()) {
        return Errors.templateApply(arg1.toString(), arg2);
      } else if (arg1.isAST(S.StringTemplate, 2) && arg1.first().isString()) {
        return Errors.templateApply(arg1.first().toString(), arg2);
      }
      return templateApplyTemplateSlot(arg1, arg2);
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
      newSymbol.setAttributes(Attribute.HOLDALL);
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
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  private static class TextString extends AbstractFunctionEvaluator {

    private static IExpr of(IExpr arg1) {
      if (arg1.isString()) {
        return arg1;
      }
      return F.stringx(arg1.toString());
    }

    protected static void of(IExpr arg1, StringBuilder buf) {
      if (arg1.isString()) {
        buf.append(arg1.toString());
        return;
      }
      buf.append(arg1.toString());
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      return of(arg1);
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

    public static IAST toCharacterCode(final String unicodeInput, final Charset inputEncoding) {
      final String utf8String =
          new String(unicodeInput.getBytes(inputEncoding), StandardCharsets.UTF_8);
      // code points: a character above U+FFFF is one code, not two surrogates
      final int[] codePoints = utf8String.codePoints().toArray();
      return F.mapRange(0, codePoints.length, i -> F.ZZ(codePoints[i]));
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      if (!arg1.isString()) {
        return F.NIL;
      }
      String text = ast.arg1().toString();
      if (ast.isAST2() && ast.arg2().isString()) {
        switch (encodingLimit(ast.arg2().toString())) {
          case UTF8:
            // the bytes of the UTF-8 encoding
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            return F.mapRange(0, bytes.length, i -> F.ZZ(bytes[i] & 0xFF));
          case 0x7F:
          case PRINTABLE_ASCII:
          case 0xFF:
            // a character the encoding does not have is None
            final int limit = encodingLimit(ast.arg2().toString());
            final int[] codePoints = text.codePoints().toArray();
            return F.mapRange(0, codePoints.length,
                i -> outsideEncoding(codePoints[i], limit) ? S.None : F.ZZ(codePoints[i]));
          default:
        }
      }
      return toCharacterCode(text, StandardCharsets.UTF_8);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // ToCharacterCode[string, encoding]: an encoding this function does not know gives the
      // character codes, as Unicode does
      return ARGS_1_2;
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
        if (ast.argSize() >= 2) {
          IExpr arg2 = ast.arg2();
          if (arg2 == S.InputForm || arg2 == S.StandardForm) {
            form = S.InputForm;
          } else if (arg2 == S.TeXForm) {
            form = S.TeXForm;
          } else {
            return F.NIL;
          }
        }
        if (ast.isAST3()) {
          head = ast.arg3();
        }
        try {
          if (form == S.InputForm) {
            String source = arg1.toString();
            if (head.isNIL() && source.indexOf('\n') >= 0) {
              // Statements on separate lines are evaluated one after the other, as Get does: a
              // Begin["ctx`"] among them decides the context of the symbols in the statements
              // after it, so each one is converted only when the one before has been evaluated.
              // Reading the whole string as one expression made the line breaks products.
              List<ASTNode> statements =
                  new Parser(engine.isRelaxedSyntax(), true).parseScript(source);
              if (statements.size() > 1) {
                return PackageUtil.evaluatePackage(statements, engine);
              }
            } else if (head.isPresent() && source.indexOf('\n') >= 0) {
              // with a head the statements on separate lines become its arguments, unevaluated
              List<IExpr> statements = lineStatements(source, engine);
              if (statements.size() > 1) {
                return F.mapRange(head, 0, statements.size(), i -> statements.get(i));
              }
            }
            ExprParser parser = new ExprParser(engine);
            // the parser raises the engine's precision to the one of the input, which is right for
            // a new input and wrong in the middle of an evaluation: an N[x, 30] read here would make
            // every later evaluation numeric with 30 digits
            final long precision = engine.getNumericPrecision();
            IExpr temp;
            try {
              temp = parser.parse(source);
            } finally {
              engine.setNumericPrecision(precision);
            }
            if (head.isPresent()) {
              return F.unaryAST1(head, temp);
            }
            return temp;
          } else if (form == S.TeXForm) {
            IExpr temp = TeXParser.convert(arg1.toString());
            if (head.isPresent()) {
              return F.unaryAST1(head, temp);
            }
            return temp;
          }
        } catch (SyntaxError sntx) {
          return S.$Failed;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.ToExpression, rex, engine);
          return S.$Failed;
        }
      } else if (arg1 == S.Null) {
        // the empty box
        return S.Null;
      } else if (BoxNotation.isBox(arg1)) {
        // ToExpression[boxes]: the expression which the boxes are the typeset form of
        try {
          IExpr temp =
              BoxNotation.toExpression(arg1, source -> new ExprParser(engine, false).parse(source));
          if (temp == null) {
            return S.$Failed;
          }
          return ast.isAST3() ? F.unaryAST1(ast.arg3(), temp) : temp;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Errors.printMessage(S.ToExpression, rex, engine);
          return S.$Failed;
        }
      } else if (ast.argSize() >= 2) {
        // The format type `1` is valid only for string input.
        return Errors.printMessage(ast.topHead(), "notstr", F.list(ast.arg2()), engine);
      } else if (arg1.isAtom()) {
        // a number or a symbol is its own value
        return arg1;
      } else {
        // `1` is not a string or a box. ToExpression can only interpret strings or boxes as
        // input.
        Errors.printMessage(ast.topHead(), "notstrbox", F.list(arg1), engine);
        return S.$Failed;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 2 && ast.last().isRuleAST()) {
        // options like CharacterEncoding -> "Unicode" or PageWidth -> Infinity: the string is
        // written in Unicode on one line anyway
        int end = ast.size();
        while (end > 2 && ast.get(end - 1).isRuleAST()) {
          end--;
        }
        ast = ast.subList(1, end);
      }
      if (ast.isAST2()) {
        return toStringForm(ast.arg1(), ast.arg2(), engine);
      }
      if (!ast.isAST1()) {
        return F.NIL;
      }
      IExpr arg1 = ast.arg1();
      if (arg1.isString()) {
        return arg1;
      }
      if (arg1.isAST() && NumberFormatter.formKind(arg1.head()) != null) {
        // a number form wrapper prints the number it formats, not the wrapper itself
        String formatted = NumberFormPrinter.print((IAST) arg1, engine);
        if (formatted != null) {
          return F.stringx(formatted);
        }
      }
      if (arg1.isAST(S.TraditionalForm, 2)) {
        IExpr traditional = traditionalForm(arg1.first(), engine);
        if (traditional.isPresent()) {
          return traditional;
        }
      }
      // ToString(expr) is ToString(expr, OutputForm) - the display wrappers Style, Row and
      // Graphics show what they format rather than themselves
      return F.stringx(outputForm(formatValues(arg1, engine, 0)));
    }

    /**
     * Replace every subexpression for which a <code>Format[...] := ...</code> definition exists by
     * what the definition gives, outermost first.
     */
    private static IExpr formatValues(IExpr expr, EvalEngine engine, int depth) {
      if (!expr.isAST() || !RulesData.isUpRulesDefined()
          || depth > engine.getRecursionLimit() && engine.getRecursionLimit() > 0) {
        return expr;
      }
      engine.checkTimeLimit();
      IAST ast = (IAST) expr;
      ISymbol head = ast.topHead();
      IExpr formatted = head.evalUpRules(F.unaryAST1(S.Format, ast), engine);
      if (formatted.isNIL() && head.isBuiltInSymbol()) {
        // a definition for pref_. * f[...] is kept with f, the first head which is not protected
        ISymbol tag = PatternMatching.firstUnprotectedHead(ast);
        if (tag != null && tag != head) {
          formatted = tag.evalUpRules(F.unaryAST1(S.Format, ast), engine);
        }
      }
      if (formatted.isPresent() && !formatted.equals(ast)) {
        return formatValues(formatted, engine, depth + 1);
      }
      return ast.map(x -> {
        IExpr arg = formatValues(x, engine, depth + 1);
        return arg == x ? F.NIL : arg;
      }).orElse(ast);
    }

    /**
     * A traditional form is typeset: the box escape <code>\!\(\*FormBox[..., TraditionalForm]\)
     * </code>.
     *
     * @return {@link F#NIL} if no boxes were created
     */
    private static IExpr traditionalForm(IExpr expr, EvalEngine engine) {
      IExpr boxes =
          engine.evaluate(F.binaryAST2(S.ToBoxes, F.Unevaluated(expr), S.TraditionalForm));
      if (BoxNotation.isBox(boxes) || boxes.isString()) {
        return F.stringx(BoxNotation.toBoxString(boxes));
      }
      return F.NIL;
    }

    /** Print <code>expr</code> the way the console shows it. */
    private static String outputForm(IExpr expr) {
      StringBuilder buf = new StringBuilder();
      int significantFigures = EvalEngine.get().getSignificantFigures();
      OutputFormFactory factory = OutputFormFactory.get(ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS,
          false, true, significantFigures - 1, significantFigures + 1);
      factory.setIgnoreNewLine(true);
      factory.setGraphicsPlaceholder(true);
      return factory.convert(buf, expr) ? buf.toString() : IStringX.inputForm(expr);
    }

    /**
     * <code>ToString(expr, form)</code> gives the string which <code>form(expr)</code> prints.
     *
     * @return {@link F#NIL} if <code>form</code> isn't a supported output form
     */
    private static IExpr toStringForm(IExpr expr, IExpr form, EvalEngine engine) {
      if (form == S.StandardForm) {
        if (expr.isString()) {
          return expr;
        }
        // the text of the boxes, or the box escape \!\(\*boxes\) which reads back as expr; expr
        // is already evaluated
        IExpr boxes = engine.evaluate(F.unaryAST1(S.ToBoxes, F.Unevaluated(expr)));
        if (BoxNotation.isBox(boxes) || boxes.isString()) {
          return F.stringx(BoxNotation.toBoxString(boxes));
        }
      }
      if (form == S.TraditionalForm && !expr.isString()) {
        IExpr traditional = traditionalForm(expr, engine);
        if (traditional.isPresent()) {
          return traditional;
        }
      }
      if (form.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) form).ordinal()) {
          case ID.InputForm:
          case ID.OutputForm:
          case ID.FullForm:
          case ID.TeXForm:
          case ID.MathMLForm:
          case ID.JavaForm:
          case ID.HoldForm:
          case ID.StandardForm:
          case ID.TraditionalForm:
            IExpr result = engine.evaluate(F.unaryAST1(form, expr));
            if (result.isString()) {
              return result;
            }
            return F.stringx(IStringX.inputForm(result));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
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
      return unicodeString;
    }

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
      newSymbol.setAttributes(Attribute.LISTABLE);
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
      if (arg1.isString()) {
        return F.booleSymbol(test(arg1));
      }
      // the message shows the evaluated argument
      Validate.checkStringType(ast.setAtCopy(1, arg1), 1, engine);
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public boolean test(final IExpr obj) {
      // each character, one above U+FFFF included
      return obj.toString().codePoints().allMatch(Character::isUpperCase);
    }
  }

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

  public static void initialize() {
    Initializer.init();
  }

  /**
   * A string indexed by characters - Unicode code points - rather than by Java's UTF-16 units: a
   * character above U+FFFF, like an emoji or <code>\\|01F600</code>, is one character, not a
   * surrogate pair. For a string without such characters the positions are the same and nothing is
   * converted.
   */
  static final class CodePointString {
    private final String s;
    /** The UTF-16 offset of each code point, or <code>null</code> if they are the same. */
    private final int[] offsets;

    CodePointString(String s) {
      this.s = s;
      int count = s.codePointCount(0, s.length());
      if (count == s.length()) {
        offsets = null;
      } else {
        offsets = new int[count];
        for (int i = 0, unit = 0; i < count; i++) {
          offsets[i] = unit;
          unit += Character.charCount(s.codePointAt(unit));
        }
      }
    }

    /** The number of characters. */
    int length() {
      return offsets == null ? s.length() : offsets.length;
    }

    /**
     * The UTF-16 offset of the character at <code>index</code>; <code>length()</code> maps to the
     * end.
     *
     * @throws IndexOutOfBoundsException for an index outside the string
     */
    private int unit(int index) {
      if (offsets == null || index == offsets.length) {
        return offsets == null ? index : s.length();
      }
      return offsets[index];
    }

    /** The characters <code>begin</code> (inclusive) to <code>end</code> (exclusive). */
    String substring(int begin, int end) {
      return s.substring(unit(begin), unit(end));
    }

    String substring(int begin) {
      return s.substring(unit(begin));
    }

    /** The character position of a UTF-16 offset, like a regular expression match reports. */
    int position(int unitOffset) {
      return offsets == null ? unitOffset : s.codePointCount(0, unitOffset);
    }

    @Override
    public String toString() {
      return s;
    }
  }

  private StringFunctions() {}
}
