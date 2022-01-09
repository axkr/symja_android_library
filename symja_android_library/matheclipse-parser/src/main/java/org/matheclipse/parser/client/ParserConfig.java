package org.matheclipse.parser.client;

import java.util.ArrayList;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.trie.TrieBuilder;

/** Parser configuration */
public class ParserConfig {

  /** Use <code>Num</code> objects for numeric calculations up to 16 digits precision. */
  public static final long MACHINE_PRECISION = 16L;

  /**
   * If <code>true</code> the parser doesn't distinguish between lower- or uppercase symbols (i.e.
   * constants, function names,...), with the exception of symbols with only one character (i.e. the
   * variable &quot;i&quot; is different from the imaginary unit &quot;I&quot;)
   */
  public static boolean PARSER_USE_LOWERCASE_SYMBOLS = true;

  /**
   * If <code>true</code> the parser doesn't allow "square brackets" instead of "parentheses" for
   * enclosing function arguments in relaxed mode. The syntax <code>f[x, y, ...]</code> isn't
   * allowed then. Always use <code>f(x, y, ...)</code>.
   */
  public static final boolean PARSER_USE_STRICT_SYNTAX = false;

  /**
   * If <code>true</code> the <code>*</code> operator must be written for a <code>Times()</code>
   * expression. I.e. you cannot write <code>2(b+c)</code> anymore, but have to write <code>2*(b+c)
   * </code> to get <code>Times(2, Plus(b, c))</code>.
   *
   * <p>You also enable <a
   * href="https://en.wikipedia.org/wiki/Scientific_notation#E-notation">scientific E-notation</a>.
   * I.e. <code>1E-2</code> is converted to a double value <code>0.01</code> for floating point
   * numbers and not parsed as <code>Plus(-2, E)</code> anymore.
   *
   * <p>You also enable integer literal input with a prefix, similar to <a
   * href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">Java integer
   * literals</a>
   *
   * <ul>
   *   <li><code>0b</code> or <code>0B</code> for binary numbers
   *   <li><code>0x</code> or <code>0X</code> for hexadecimal numbers
   *   <li><code>0o</code> or <code>0O</code> for octal numbers
   * </ul>
   */
  public static boolean EXPLICIT_TIMES_OPERATOR = false;

  /**
   * If <code>true</code> the implicit <code>*</code> operator has a higher precedence than all
   * other operators. I.e. <code>1/2Pi</code> is parsed as <code>Power(Times(2, Pi), -1)</code>. If
   * <code>false</code> the implicit <code>*</code> operator has a normal precedence as in all other
   * cases. I.e. <code>1/2Pi</code> is parsed as <code>Times(Rational(1,2), Pi)</code>.
   */
  public static boolean DOMINANT_IMPLICIT_TIMES = false;

  /** A trie builder for mapping strings to other strings. */
  public static final TrieBuilder<String, String, ArrayList<String>> TRIE_STRING2STRING_BUILDER =
      TrieBuilder.create();

  public static final TrieBuilder<String, Operator, ArrayList<Operator>>
      TRIE_STRING2OPERATOR_BUILDER = TrieBuilder.create();

  public static final TrieBuilder<String, ArrayList<Operator>, ArrayList<ArrayList<Operator>>>
      TRIE_STRING2OPERATORLIST_BUILDER = TrieBuilder.create();
}
