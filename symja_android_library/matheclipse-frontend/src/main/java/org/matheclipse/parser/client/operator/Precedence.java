package org.matheclipse.parser.client.operator;

/**
 * Precedences of the built-in Symja operators
 */
public class Precedence {
	public final static int ADDTO = 100;
	public final static int ALTERNATIVES = 160;
	public final static int AND = 215;
	public final static int APPLY = 620;
	public final static int APPLY_HEAD = 660;
	public final static int CENTERDOT = 410;
	public final static int CIRCLEDOT = 520;
	public final static int COLON = 80;
	public final static int COMPOSITION = 625;
	public final static int COMPOUNDEXPRESSION = 10;
	/**
	 * See <a href=
	 * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Condition.md">Condition</a>
	 */
	public final static int CONDITION = 130;
	public final static int DECREMENT = 660;
	public final static int DIRECTEDEDGE = 120;
	public final static int DIVIDE = 470;
	public final static int DIVIDEBY = 100;
	public final static int DOT = 490;
	public final static int ELEMENT = 250;
	/**
	 * See <a href=
	 * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Equal.md">Equal</a>
	 */
	public final static int EQUAL = 290;
	public final static int FACTORIAL = 610;
	public final static int FACTORIAL2 = 610;
	public final static int FUNCTION = 90;
	public final static int GET = 720;
	public final static int GREATER = 290;
	public final static int GREATEREQUAL = 290;
	public final static int INCREMENT = 660;
	public final static int INTERSECTION = 305;
	public final static int LESS = 290;
	public final static int LESSEQUAL = 290;
	public final static int MAP = 620;
	public final static int MAPALL = 620;
	public final static int MESSAGENAME = 750;
	public final static int NONCOMMUTATIVEMULTIPLY = 510;
	public final static int NOT = 230;
	public final static int OR = 213;
	public final static int PATTERNTEST = 680;
	/**
	 * See <a href=
	 * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Plus.md">Plus</a>
	 */
	public final static int PLUS = 310;
	/**
	 * See <a href=
	 * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Power.md">Power</a>
	 */
	public final static int POWER = 590;
	public final static int PREDECREMENT = 660;
	public final static int PREINCREMENT = 660;
	public final static int PREMINUS = 485;
	public final static int PREPLUS = 670;
	public final static int REPEATED = 170;
	public final static int REPEATEDNULL = 170;
	public final static int REPLACEALL = 110;
	public final static int REPLACEREPEATED = 110;
	public final static int RULE = 120;
	public final static int RULEDELAYED = 120;
	public final static int SAMEQ = 290;
	public final static int SET = 40;
	public final static int SETDELAYED = 40;
	public final static int SPAN = 305;
	public final static int STRINGEXPRESSION = 135;
	public final static int STRINGJOIN = 600;
	public final static int SUBTRACT = 310;
	public final static int SUBTRACTFROM = 100;
	public final static int TAGSET = 40;
	/**
	 * See <a href=
	 * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Times.md">Times</a>
	 */
	public final static int TIMES = 400;
	public final static int TIMESBY = 100;
	public final static int TWOWAYRULE = 125;
	public final static int UNDIRECTEDEDGE = 120;
	public final static int UNEQUAL = 290;
	public final static int UNSAMEQ = 290;
	public final static int UNSET = 670;
	public final static int UPSET = 40;
	public final static int UPSETDELAYED = 40;
	public final static int WEDGE = 440;
}
