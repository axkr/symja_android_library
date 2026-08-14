package org.matheclipse.parser.client.operator;

/** Precedences of the built-in Symja operators */
public class Precedence {
  /**
   * Use 0 precedence, if at the start of an expression.
   */
  public static final int NO_PRECEDENCE = 0;

  public static final int ADDTO = 100;
  public static final int ALTERNATIVES = 160;
  /**
   * Mathics3/WMA value. Nothing sits between Symja's former 215 and this, so the change is not
   * observable today - it matters once an operator lands in the gap, Xor at 220.
   */
  public static final int AND = 225;
  public static final int APPLY = 620;
  public static final int APPLY_HEAD = 621;
  public static final int CENTERDOT = 410;
  public static final int CIRCLEDOT = 520;
  public static final int CIRCLETIMES = 420;
  public static final int COLON = 80;
  public static final int COMPOSITION = 625;
  public static final int DISTRIBUTED = 250;
  /**
   * Must stay below {@link #COMPOSITION}. Wolfram groups <code>@*</code> first in both
   * <code>f @* g /* h</code> (which gives <code>RightComposition[Composition[f,g],h]</code>) and
   * <code>f /* g @* h</code> (<code>RightComposition[f,Composition[g,h]]</code>), which only a
   * strictly tighter Composition reproduces - equal precedence gets one of the two wrong under
   * either associativity.
   *
   * <p>
   * Symja had 648 and Mathics3 records 650; both put RightComposition above Composition and so
   * parse both of those expressions wrongly. The value here is the largest one below
   * {@link #COMPOSITION}, chosen so that the only relative order this correction changes is the one
   * against Composition. Its position relative to <code>@</code> (621) and the 620 band
   * (<code>@@</code>, <code>/@</code>, <code>@@@</code>) is not established by the evidence above -
   * <code>f /* g @@ h</code> and <code>f /* g /@ h</code> would pin it.
   */
  public static final int RIGHTCOMPOSITION = 624;
  public static final int COMPOUNDEXPRESSION = 10;
  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Condition.md">Condition</a>
   */
  public static final int CONDITION = 130;

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Conditioned.md">Conditioned</a>
   */
  public static final int CONDITIONED = 195;

  public static final int DECREMENT = 660;
  /**
   * WMA value, confirmed against Mathematica. Symja had 120 - the same as {@link #RULE} - which put
   * the edge arrows in the rule band rather than the relational one, so that everything from
   * {@code /;} up to {@code ==} bound the wrong way against them.
   */
  public static final int DIRECTEDEDGE = 295;
  public static final int DIVIDE = 470;
  public static final int DIVIDEBY = 100;
  public static final int DOT = 490;
  public static final int ELEMENT = 250;
  public static final int NOTELEMENT = 250;
  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Equal.md">Equal</a>
   */
  public static final int EQUAL = 290;

  public static final int EQUIVALENT = 205;
  public static final int FACTORIAL = 610;
  public static final int FACTORIAL2 = 610;
  public static final int FUNCTION = 90;
  public static final int GET = 720;
  public static final int GREATER = 290;
  public static final int GREATEREQUAL = 290;
  public static final int IMPLIES = 200;
  public static final int INCREMENT = 660;
  public static final int INTERSECTION = 305;
  public static final int LESS = 290;
  public static final int LESSEQUAL = 290;
  public static final int MAP = 620;
  public static final int MAPAPPLY = 620;
  public static final int MAPALL = 620;
  public static final int MESSAGENAME = 750;
  public static final int NONCOMMUTATIVEMULTIPLY = 510;
  public static final int NOT = 230;
  /** Mathics3/WMA value; see {@link #AND}. Or stays below And, as before. */
  public static final int OR = 215;
  public static final int PATTERNTEST = 680;
  public static final int UNION = 300;
  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   */
  public static final int PLUS = 310;
  public static final int PLUSMINUS = 310;

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Power.md">Power</a>
   */
  public static final int POWER = 590;

  public static final int PREDECREMENT = 660;
  public static final int PREINCREMENT = 660;
  public static final int PREMINUS = 485;
  public static final int PREPLUS = 670;
  public static final int REPEATED = 170;
  public static final int REPEATEDNULL = 170;
  public static final int REPLACEALL = 110;
  public static final int REPLACEREPEATED = 110;
  public static final int RULE = 120;
  public static final int RULEDELAYED = 120;
  public static final int SAMEQ = 290;
  public static final int SET = 40;
  public static final int SETDELAYED = 40;
  public static final int SPAN = 305;
  public static final int STRINGEXPRESSION = 135;
  public static final int PATTERN = 150;
  public static final int STAR = 390;
  public static final int STRINGJOIN = 600;
  public static final int SUBTRACT = 310;
  public static final int SUBTRACTFROM = 100;
  public static final int TAGSET = 40;
  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Times.md">Times</a>
   */
  public static final int TIMES = 400;

  public static final int TIMESBY = 100;
  public static final int TWOWAYRULE = 125;
  /** WMA value, confirmed against Mathematica; see {@link #DIRECTEDEDGE}. */
  public static final int UNDIRECTEDEDGE = 295;
  public static final int UNEQUAL = 290;
  public static final int UNSAMEQ = 290;
  public static final int UNSET = 670;
  public static final int UPSET = 40;
  public static final int UPSETDELAYED = 40;
  public static final int WEDGE = 440;
  /**
   * Mathics3/WMA value. This one is observable: Symja had 495, which binds tighter than
   * {@link #DOT} at 490, while WMA binds it looser. So <code>a \u2297 b . c</code> used to group as
   * <code>(a \u2297 b) . c</code> and now groups as <code>a \u2297 (b . c)</code>.
   */
  public static final int TENSORPRODUCT = 487;
  public static final int TILDE_OPERATOR = 630;
}
