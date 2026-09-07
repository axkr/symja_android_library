package org.matheclipse.tools.units;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses pint definition expressions into a monomial {@code coefficient * PROD name_i^exp_i}.
 * Supported grammar: numbers (exact decimals/scientific), identifiers (Unicode), {@code *},
 * {@code /}, {@code **} and {@code ^} with signed (possibly fractional) numeric exponents,
 * parentheses, unary sign, and implicit multiplication by juxtaposition ({@code 299792458 m/s},
 * {@code m^3/(kg s^2)}). {@link #parseNumber(String)} additionally allows {@code +}/{@code -} for
 * offset expressions like {@code 233.15 + 200 / 9} and requires a pure rational result.
 */
public final class ExprParser {

  /** A monomial: exact coefficient times a product of named factors with rational exponents. */
  public static final class Term {
    public Coefficient coeff = Coefficient.ONE;
    public final LinkedHashMap<String, BigRational> factors = new LinkedHashMap<>();

    public Term multiply(Term o) {
      Term t = new Term();
      t.coeff = coeff.multiply(o.coeff);
      t.factors.putAll(factors);
      for (Map.Entry<String, BigRational> e : o.factors.entrySet()) {
        t.addFactor(e.getKey(), e.getValue());
      }
      return t;
    }

    public Term pow(BigRational e) {
      Term t = new Term();
      t.coeff = coeff.pow(e);
      for (Map.Entry<String, BigRational> f : factors.entrySet()) {
        t.addFactor(f.getKey(), f.getValue().multiply(e));
      }
      return t;
    }

    public void addFactor(String name, BigRational exp) {
      BigRational merged = factors.containsKey(name) ? factors.get(name).add(exp) : exp;
      if (merged.isZero()) {
        factors.remove(name);
      } else {
        factors.put(name, merged);
      }
    }

    public boolean isPureNumber() {
      return factors.isEmpty();
    }

    @Override
    public String toString() {
      StringBuilder b = new StringBuilder(coeff.render());
      for (Map.Entry<String, BigRational> f : factors.entrySet()) {
        b.append('*').append(f.getKey()).append('^').append(f.getValue());
      }
      return b.toString();
    }
  }

  // ---------------------------------------------------------------- tokenizer

  private static final String DELIMITERS = "()*/^+-;=#";

  private static final class Token {
    static final int NUMBER = 0;
    static final int IDENT = 1;
    static final int OP = 2;
    final int kind;
    final String text;
    final BigRational value;

    Token(int kind, String text, BigRational value) {
      this.kind = kind;
      this.text = text;
      this.value = value;
    }
  }

  private static List<Token> tokenize(String s) {
    List<Token> tokens = new ArrayList<>();
    int i = 0;
    int n = s.length();
    while (i < n) {
      char c = s.charAt(i);
      if (Character.isWhitespace(c)) {
        i++;
        continue;
      }
      if (c == '#') {
        break; // comment to end of line
      }
      if (Character.isDigit(c) || (c == '.' && i + 1 < n && Character.isDigit(s.charAt(i + 1)))) {
        int start = i;
        while (i < n && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) {
          i++;
        }
        if (i < n && (s.charAt(i) == 'e' || s.charAt(i) == 'E')) {
          int j = i + 1;
          if (j < n && (s.charAt(j) == '+' || s.charAt(j) == '-')) {
            j++;
          }
          if (j < n && Character.isDigit(s.charAt(j))) {
            i = j;
            while (i < n && Character.isDigit(s.charAt(i))) {
              i++;
            }
          }
        }
        String text = s.substring(start, i);
        tokens.add(new Token(Token.NUMBER, text, BigRational.parseDecimal(text)));
        continue;
      }
      if (c == '*' && i + 1 < n && s.charAt(i + 1) == '*') {
        tokens.add(new Token(Token.OP, "^", null));
        i += 2;
        continue;
      }
      if (DELIMITERS.indexOf(c) >= 0) {
        tokens.add(new Token(Token.OP, String.valueOf(c), null));
        i++;
        continue;
      }
      // identifier: any run of non-delimiter, non-whitespace characters (Unicode friendly)
      int start = i;
      while (i < n && !Character.isWhitespace(s.charAt(i)) && DELIMITERS.indexOf(s.charAt(i)) < 0) {
        i++;
      }
      tokens.add(new Token(Token.IDENT, s.substring(start, i), null));
    }
    return tokens;
  }

  // ---------------------------------------------------------------- parser

  private final List<Token> tokens;
  private final String source;
  private int pos;

  private ExprParser(String source) {
    this.source = source;
    this.tokens = tokenize(source);
    this.pos = 0;
  }

  private Token peek() {
    return pos < tokens.size() ? tokens.get(pos) : null;
  }

  private Token next() {
    if (pos >= tokens.size()) {
      throw new IllegalArgumentException("unexpected end of expression: '" + source + "'");
    }
    return tokens.get(pos++);
  }

  private static boolean isOp(Token t, String text) {
    return t != null && t.kind == Token.OP && t.text.equals(text);
  }

  /** Parses a monomial expression (no top-level +/-). */
  public static Term parseMonomial(String s) {
    ExprParser p = new ExprParser(s);
    Term t = p.parseProduct();
    if (p.pos < p.tokens.size()) {
      throw new IllegalArgumentException(
          "trailing input at token " + p.pos + " in '" + s + "'");
    }
    return t;
  }

  /** Parses an additive numeric expression (offsets); must reduce to a plain rational. */
  public static BigRational parseNumber(String s) {
    ExprParser p = new ExprParser(s);
    Term t = p.parseProduct();
    BigRational sum = pureRational(t, s);
    while (true) {
      Token op = p.peek();
      if (isOp(op, "+") || isOp(op, "-")) {
        p.next();
        BigRational v = pureRational(p.parseProduct(), s);
        sum = op.text.equals("+") ? sum.add(v) : sum.subtract(v);
      } else {
        break;
      }
    }
    if (p.pos < p.tokens.size()) {
      throw new IllegalArgumentException("trailing input in numeric expression '" + s + "'");
    }
    return sum;
  }

  private static BigRational pureRational(Term t, String source) {
    if (!t.isPureNumber() || !t.coeff.isRational()) {
      throw new IllegalArgumentException("not a pure rational value: '" + source + "'");
    }
    return t.coeff.rationalValue();
  }

  private Term parseProduct() {
    Term result = parsePower();
    while (true) {
      Token t = peek();
      if (isOp(t, "*")) {
        next();
        result = result.multiply(parsePower());
      } else if (isOp(t, "/")) {
        next();
        result = result.multiply(parsePower().pow(BigRational.of(-1)));
      } else if (t != null && (t.kind == Token.NUMBER || t.kind == Token.IDENT || isOp(t, "("))) {
        // implicit multiplication by juxtaposition: "299792458 m/s", "kg s^2"
        result = result.multiply(parsePower());
      } else {
        break;
      }
    }
    return result;
  }

  private Term parsePower() {
    Term base = parseFactor();
    Token t = peek();
    if (isOp(t, "^")) {
      next();
      BigRational e = parseExponent();
      return base.pow(e);
    }
    return base;
  }

  private BigRational parseExponent() {
    boolean negative = false;
    Token t = next();
    if (isOp(t, "-")) {
      negative = true;
      t = next();
    } else if (isOp(t, "+")) {
      t = next();
    }
    BigRational value;
    if (t.kind == Token.NUMBER) {
      value = t.value;
    } else if (isOp(t, "(")) {
      // rare parenthesized exponent, e.g. (1/2)
      Term inner = parseProduct();
      Token close = next();
      if (!isOp(close, ")")) {
        throw new IllegalArgumentException("expected ')' in exponent of '" + source + "'");
      }
      value = pureRational(inner, source);
    } else {
      throw new IllegalArgumentException(
          "expected numeric exponent, found '" + t.text + "' in '" + source + "'");
    }
    return negative ? value.negate() : value;
  }

  private Term parseFactor() {
    Token t = next();
    if (isOp(t, "+")) {
      return parseFactor();
    }
    if (isOp(t, "-")) {
      Term inner = parseFactor();
      Term result = new Term();
      result.coeff = Coefficient.of(BigRational.of(-1)).multiply(inner.coeff);
      result.factors.putAll(inner.factors);
      return result;
    }
    if (isOp(t, "(")) {
      Term inner = parseProduct();
      Token close = next();
      if (!isOp(close, ")")) {
        throw new IllegalArgumentException("expected ')' in '" + source + "'");
      }
      return inner;
    }
    if (t.kind == Token.NUMBER) {
      Term result = new Term();
      result.coeff = Coefficient.of(t.value);
      return result;
    }
    if (t.kind == Token.IDENT) {
      Term result = new Term();
      result.addFactor(t.text, BigRational.ONE);
      return result;
    }
    throw new IllegalArgumentException("unexpected token '" + t.text + "' in '" + source + "'");
  }
}
