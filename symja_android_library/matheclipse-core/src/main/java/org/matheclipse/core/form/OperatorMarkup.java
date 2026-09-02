package org.matheclipse.core.form;

import java.util.HashMap;
import java.util.Map;

/**
 * The LaTeX macro each operator head renders as.
 *
 * <p>
 * {@link org.matheclipse.parser.client.operator.OperatorTable} says which heads print as an
 * operator, how tightly they bind and in which position; it does not say how to spell them in
 * LaTeX, and it should not - it lives in the parser module, which knows nothing about output
 * formats. This table supplies that one missing piece, and {@code TeXFormFactory} joins the two.
 *
 * <p>
 * There is no MathML counterpart: {@code MathMLFormFactory} resolves any head the operator table
 * knows through {@code convertAST}, so an operator gains a MathML form from its row alone.
 *
 * <p>
 * The macros are the {@code amslatex} field of Mathics3's {@code named-characters.yml}, with three
 * additions and one correction. {@code Cross}, {@code Proportion} and
 * {@code NotSquareSupersetEqual} are recorded there without a macro but have a standard one
 * anyway. {@code Backslash} is recorded as <code>\\</code>, which is a line break in LaTeX rather
 * than the set-minus slash the character means, and is corrected to <code>\setminus</code>. The
 * remaining few heads have no macro in any common package and are listed as the character itself,
 * which is what a unicode-aware renderer such as MathJax wants.
 */
public final class OperatorMarkup {

  private static final Map<String, String> LATEX = new HashMap<>(182);

  private static void put(String head, String latex) {
    LATEX.put(head, latex);
  }

  static {
    put("Backslash", "\\setminus");
    put("Because", "\\because");
    put("Cap", "\\cap");
    put("CircleMinus", "\\ominus");
    put("CirclePlus", "\\oplus");
    put("Colon", "\u2236");
    put("Congruent", "\\equiv");
    put("Coproduct", "\\coprod");
    put("Cross", "\\times");
    put("Cup", "\\cup");
    put("CupCap", "\\stackrel{\\smile}{\\frown}");
    put("Del", "\\nabla");
    put("Diamond", "\\diamond");
    put("Divides", "\\mid");
    put("DotEqual", "\\doteq");
    put("DoubleDownArrow", "\\Downarrow");
    put("DoubleLeftArrow", "\\Leftarrow");
    put("DoubleLeftRightArrow", "\\Leftrightarrow");
    put("DoubleLeftTee", "\u2ae4");
    put("DoubleLongLeftArrow", "\\Longleftarrow");
    put("DoubleLongLeftRightArrow", "\\Longleftrightarrow");
    put("DoubleLongRightArrow", "\\Longrightarrow");
    put("DoubleRightArrow", "\\Rightarrow");
    put("DoubleRightTee", "\\vDash");
    put("DoubleUpArrow", "\\Uparrow");
    put("DoubleUpDownArrow", "\\Updownarrow");
    put("DoubleVerticalBar", "\\parallel");
    put("DownArrow", "\\downarrow");
    put("DownArrowBar", "\\underline{\\downarrow}");
    put("DownArrowUpArrow", "\\downarrow \\uparrow");
    put("DownLeftRightVector", "\\leftharpoondown \\rightharpoondown");
    put("DownLeftTeeVector", "\\leftharpoondown |");
    put("DownLeftVector", "\\leftharpoondown");
    put("DownLeftVectorBar", "|\\leftharpoondown");
    put("DownRightTeeVector", "|\\rightharpoondown");
    put("DownRightVector", "\\rightharpoondown");
    put("DownRightVectorBar", "\\rightharpoondown |");
    put("DownTee", "\\top");
    put("DownTeeArrow", "\\bar{\\downarrow}");
    put("EqualTilde", "\\eqsim");
    put("Equilibrium", "\\rightleftharpoons");
    put("GreaterEqualLess", "\\gtreqless");
    put("GreaterFullEqual", "\\geqq");
    put("GreaterGreater", "\\gg");
    put("GreaterLess", "\\gtrless");
    put("GreaterSlantEqual", "\\geq");
    put("GreaterTilde", "\\gtrsim");
    put("HumpDownHump", "\\Bumpeq");
    put("HumpEqual", "\\bumpeq");
    put("LeftArrow", "\\leftarrow");
    put("LeftArrowBar", "|\\leftarrow");
    put("LeftArrowRightArrow", "\\leftrightarrows");
    put("LeftDownTeeVector", "\\bar{\\downharpoonleft}");
    put("LeftDownVector", "\\downharpoonleft");
    put("LeftDownVectorBar", "\\underline{\\downharpoonleft}");
    put("LeftRightArrow", "\\leftrightarrow");
    put("LeftRightVector", "\\leftharpoonup \\rightharpoonup");
    put("LeftTee", "\\dashv");
    put("LeftTeeArrow", "\\mapsfrom");
    put("LeftTeeVector", "\\leftharpoonup |");
    put("LeftTriangle", "\\triangleleft");
    put("LeftTriangleBar", "\\triangleleft |");
    put("LeftTriangleEqual", "\\trianglelefteq");
    put("LeftUpDownVector", "\\stackrel{\\upharpoonleft}{\\downharpoonleft}");
    put("LeftUpTeeVector", "\\underline{\\upharpoonleft}");
    put("LeftUpVector", "\\upharpoonleft");
    put("LeftUpVectorBar", "\\bar{\\upharpoonleft}");
    put("LeftVector", "\\leftharpoonup");
    put("LeftVectorBar", "|\\leftharpoonup");
    put("LessEqualGreater", "\\lesseqgtr");
    put("LessFullEqual", "\\leqq");
    put("LessGreater", "\\lessgtr");
    put("LessLess", "\\ll");
    put("LessSlantEqual", "\\leq");
    put("LessTilde", "\\lesssim");
    put("LongLeftArrow", "\\longleftarrow");
    put("LongLeftRightArrow", "\\longleftrightarrow");
    put("LongRightArrow", "\\longrightarrow");
    put("LowerLeftArrow", "\\swarrow");
    put("LowerRightArrow", "\\searrow");
    put("Minus", "-");
    put("MinusPlus", "\\mp");
    put("Nand", "\\barwedge");
    put("NestedGreaterGreater", "\\gg");
    put("NestedLessLess", "\\ll");
    put("Nor", "\\veebar");
    put("NotCongruent", "\\not{\\equiv}");
    put("NotCupCap", "\\not{\\stackrel{\\smile}{\\frown}}");
    put("NotDoubleVerticalBar", "\\nparallel");
    put("NotExists", "\\nexists");
    put("NotGreater", "\\ngtr");
    put("NotGreaterEqual", "\\ngeq");
    put("NotGreaterFullEqual", "\\ngeqq");
    put("NotGreaterLess", "\\not{\\gtrless}");
    put("NotGreaterTilde", "\\not{\\gtrsim}");
    put("NotLeftTriangle", "\\ntriangleleft");
    put("NotLeftTriangleEqual", "\\ntrianglelefteq");
    put("NotLess", "\\nless");
    put("NotLessEqual", "\\nleq");
    put("NotLessFullEqual", "\\nleqq");
    put("NotLessGreater", "\\not{\\lessgtr}");
    put("NotLessTilde", "\\not{\\lesssim}");
    put("NotPrecedes", "\\nprec");
    put("NotPrecedesSlantEqual", "\\not{\\preccurlyeq}");
    put("NotPrecedesTilde", "\\not{\\precsim}");
    put("NotReverseElement", "\\not{\\ni}");
    put("NotRightTriangle", "\\ntriangleright");
    put("NotRightTriangleEqual", "\\ntrianglerighteq");
    put("NotSquareSubsetEqual", "\\not{\\sqsubseteq}");
    put("NotSquareSupersetEqual", "\\not\\sqsupseteq");
    put("NotSubset", "\\not{\\subset}");
    put("NotSubsetEqual", "\\nsubseteq");
    put("NotSucceeds", "\\nsucc");
    put("NotSucceedsSlantEqual", "\\not{\\succeq}");
    put("NotSucceedsTilde", "\\not{\\succsim}");
    put("NotSuperset", "\\not{\\supset}");
    put("NotSupersetEqual", "\\nsupseteq");
    put("NotTilde", "\\not{\\sim}");
    put("NotTildeEqual", "\\not{\\simeq}");
    put("NotTildeFullEqual", "\\ncong");
    put("NotTildeTilde", "\\not{\\approx}");
    put("PartialD", "\\partial");
    put("Perpendicular", "\\perp");
    put("Precedes", "\\prec");
    put("PrecedesEqual", "\\preceq");
    put("PrecedesSlantEqual", "\\preccurlyeq");
    put("PrecedesTilde", "\\precsim");
    put("Proportion", "\\Colon");
    put("Proportional", "\\propto");
    put("ReverseElement", "\\ni");
    put("ReverseEquilibrium", "\\leftrightharpoons");
    put("ReverseUpEquilibrium", "\\downharpoonleft\\upharpoonright");
    put("RightArrow", "\\rightarrow");
    put("RightArrowBar", "\\rightarrow |");
    put("RightArrowLeftArrow", "\\rightleftarrows");
    put("RightDownTeeVector", "\\bar{\\downharpoonright}");
    put("RightDownVector", "\\underline{\\downharpoonright}");
    put("RightDownVectorBar", "\u2955");
    put("RightTeeArrow", "\\vdash");
    put("RightTeeVector", "|\\rightharpoonup");
    put("RightTriangle", "\\triangleright");
    put("RightTriangleBar", "|\\triangleright");
    put("RightTriangleEqual", "\\trianglerighteq");
    put("RightUpDownVector", "\\stackrel{\\upharpoonright}{\\downharpoonright}");
    put("RightUpTeeVector", "\\underline{\\upharpoonright}");
    put("RightUpVector", "\\upharpoonright");
    put("RightUpVectorBar", "\\bar{\\upharpoonright}");
    put("RightVector", "\\rightharpoonup");
    put("RightVectorBar", "\\rightharpoonup |");
    put("ShortUpArrow", "\\uparrow");
    put("SmallCircle", "\\circ");
    put("SquareIntersection", "\\sqcap");
    put("SquareSubset", "\\sqsubset");
    put("SquareSubsetEqual", "\\sqsubseteq");
    put("SquareSuperset", "\\sqsupset");
    put("SquareSupersetEqual", "\\sqsupseteq");
    put("SquareUnion", "\\sqcup");
    put("Subset", "\\subset");
    put("SubsetEqual", "\\subseteq");
    put("Succeeds", "\\succ");
    put("SucceedsEqual", "\\succeq");
    put("SucceedsSlantEqual", "\\succeq");
    put("SucceedsTilde", "\\succsim");
    put("Superset", "\\supset");
    put("SupersetEqual", "\\supseteq");
    put("Tilde", "\\sim");
    put("TildeEqual", "\\simeq");
    put("TildeFullEqual", "\\cong");
    put("TildeTilde", "\\approx");
    put("Union", "\\cup");
    put("UnionPlus", "\\uplus");
    put("UpArrowBar", "\\bar{\\uparrow}");
    put("UpArrowDownArrow", "\\updownarrow");
    put("UpDownArrow", "\\updownarrow");
    put("UpEquilibrium", "\\upharpoonleft \\downharpoonright");
    put("UpTeeArrow", "\\underline{\\uparrow}");
    put("UpperLeftArrow", "\\nwarrow");
    put("UpperRightArrow", "\\nearrow");
    put("Vee", "\\vee");
    put("VerticalBar", "\\shortmid");
    put("VerticalTilde", "\\wr");
    put("Xor", "\\oplus");
  }

  private OperatorMarkup() {}

  /**
   * The bare LaTeX for an operator head, or <code>null</code> if this table has no macro for it.
   * The caller adds the spacing its position needs - a macro has to be separated from whatever
   * follows it, so an infix operator wants a space on both sides and a prefix one only on the
   * right.
   */
  public static String latex(String head) {
    return LATEX.get(head);
  }
}
