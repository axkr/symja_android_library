package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Each operator without a built-in meaning prints as its infix form, and that text is read back as the same expression. */
public class OperatorReadBackTest extends ExprEvaluatorTestCase {

  @Test
  public void testOperatorsReadBack() {
    check("Module({s = ToString(GreaterSlantEqual(a, b), InputForm)}, {s, ToExpression(s) === GreaterSlantEqual(a, b)})", //
        "{a⩾b,True}");
    check("Module({s = ToString(LessSlantEqual(a, b), InputForm)}, {s, ToExpression(s) === LessSlantEqual(a, b)})", //
        "{a⩽b,True}");
    check("Module({s = ToString(SuchThat(a, b), InputForm)}, {s, ToExpression(s) === SuchThat(a, b)})", //
        "{a∍b,True}");
    check("Module({s = ToString(UpArrow(a, b), InputForm)}, {s, ToExpression(s) === UpArrow(a, b)})", //
        "{a↑b,True}");
    check("Module({s = ToString(CircleMinus(a, b), InputForm)}, {s, ToExpression(s) === CircleMinus(a, b)})", //
        "{a⊖b,True}");
    check("Module({s = ToString(Coproduct(a, b), InputForm)}, {s, ToExpression(s) === Coproduct(a, b)})", //
        "{a∐b,True}");
    check("Module({s = ToString(CupCap(a, b), InputForm)}, {s, ToExpression(s) === CupCap(a, b)})", //
        "{a≍b,True}");
    check("Module({s = ToString(DotEqual(a, b), InputForm)}, {s, ToExpression(s) === DotEqual(a, b)})", //
        "{a≐b,True}");
    check("Module({s = ToString(DoubleDownArrow(a, b), InputForm)}, {s, ToExpression(s) === DoubleDownArrow(a, b)})", //
        "{a⇓b,True}");
    check("Module({s = ToString(DoubleLeftArrow(a, b), InputForm)}, {s, ToExpression(s) === DoubleLeftArrow(a, b)})", //
        "{a⇐b,True}");
    check("Module({s = ToString(DoubleLeftRightArrow(a, b), InputForm)}, {s, ToExpression(s) === DoubleLeftRightArrow(a, b)})", //
        "{a⇔b,True}");
    check("Module({s = ToString(DoubleLeftTee(a, b), InputForm)}, {s, ToExpression(s) === DoubleLeftTee(a, b)})", //
        "{a⫤b,True}");
    check("Module({s = ToString(DoubleLongLeftArrow(a, b), InputForm)}, {s, ToExpression(s) === DoubleLongLeftArrow(a, b)})", //
        "{a⟸b,True}");
    check("Module({s = ToString(DoubleLongLeftRightArrow(a, b), InputForm)}, {s, ToExpression(s) === DoubleLongLeftRightArrow(a, b)})", //
        "{a⟺b,True}");
    check("Module({s = ToString(DoubleLongRightArrow(a, b), InputForm)}, {s, ToExpression(s) === DoubleLongRightArrow(a, b)})", //
        "{a⟹b,True}");
    check("Module({s = ToString(DoubleRightArrow(a, b), InputForm)}, {s, ToExpression(s) === DoubleRightArrow(a, b)})", //
        "{a⇒b,True}");
    check("Module({s = ToString(DoubleRightTee(a, b), InputForm)}, {s, ToExpression(s) === DoubleRightTee(a, b)})", //
        "{a⊨b,True}");
    check("Module({s = ToString(DoubleUpArrow(a, b), InputForm)}, {s, ToExpression(s) === DoubleUpArrow(a, b)})", //
        "{a⇑b,True}");
    check("Module({s = ToString(DoubleUpDownArrow(a, b), InputForm)}, {s, ToExpression(s) === DoubleUpDownArrow(a, b)})", //
        "{a⇕b,True}");
    check("Module({s = ToString(DownArrow(a, b), InputForm)}, {s, ToExpression(s) === DownArrow(a, b)})", //
        "{a↓b,True}");
    check("Module({s = ToString(DownArrowBar(a, b), InputForm)}, {s, ToExpression(s) === DownArrowBar(a, b)})", //
        "{a⤓b,True}");
    check("Module({s = ToString(DownArrowUpArrow(a, b), InputForm)}, {s, ToExpression(s) === DownArrowUpArrow(a, b)})", //
        "{a⇵b,True}");
    check("Module({s = ToString(DownLeftRightVector(a, b), InputForm)}, {s, ToExpression(s) === DownLeftRightVector(a, b)})", //
        "{a⥐b,True}");
    check("Module({s = ToString(DownLeftTeeVector(a, b), InputForm)}, {s, ToExpression(s) === DownLeftTeeVector(a, b)})", //
        "{a⥞b,True}");
    check("Module({s = ToString(DownLeftVector(a, b), InputForm)}, {s, ToExpression(s) === DownLeftVector(a, b)})", //
        "{a↽b,True}");
    check("Module({s = ToString(DownLeftVectorBar(a, b), InputForm)}, {s, ToExpression(s) === DownLeftVectorBar(a, b)})", //
        "{a⥖b,True}");
    check("Module({s = ToString(DownRightTeeVector(a, b), InputForm)}, {s, ToExpression(s) === DownRightTeeVector(a, b)})", //
        "{a⥟b,True}");
    check("Module({s = ToString(DownRightVector(a, b), InputForm)}, {s, ToExpression(s) === DownRightVector(a, b)})", //
        "{a⇁b,True}");
    check("Module({s = ToString(DownRightVectorBar(a, b), InputForm)}, {s, ToExpression(s) === DownRightVectorBar(a, b)})", //
        "{a⥗b,True}");
    check("Module({s = ToString(DownTee(a, b), InputForm)}, {s, ToExpression(s) === DownTee(a, b)})", //
        "{a⊤b,True}");
    check("Module({s = ToString(DownTeeArrow(a, b), InputForm)}, {s, ToExpression(s) === DownTeeArrow(a, b)})", //
        "{a↧b,True}");
    check("Module({s = ToString(EqualTilde(a, b), InputForm)}, {s, ToExpression(s) === EqualTilde(a, b)})", //
        "{a≂b,True}");
    check("Module({s = ToString(Equilibrium(a, b), InputForm)}, {s, ToExpression(s) === Equilibrium(a, b)})", //
        "{a⇌b,True}");
    check("Module({s = ToString(GreaterEqualLess(a, b), InputForm)}, {s, ToExpression(s) === GreaterEqualLess(a, b)})", //
        "{a⋛b,True}");
    check("Module({s = ToString(GreaterFullEqual(a, b), InputForm)}, {s, ToExpression(s) === GreaterFullEqual(a, b)})", //
        "{a≧b,True}");
    check("Module({s = ToString(GreaterGreater(a, b), InputForm)}, {s, ToExpression(s) === GreaterGreater(a, b)})", //
        "{a≫b,True}");
    check("Module({s = ToString(GreaterLess(a, b), InputForm)}, {s, ToExpression(s) === GreaterLess(a, b)})", //
        "{a≷b,True}");
    check("Module({s = ToString(GreaterTilde(a, b), InputForm)}, {s, ToExpression(s) === GreaterTilde(a, b)})", //
        "{a≳b,True}");
    check("Module({s = ToString(HumpDownHump(a, b), InputForm)}, {s, ToExpression(s) === HumpDownHump(a, b)})", //
        "{a≎b,True}");
    check("Module({s = ToString(HumpEqual(a, b), InputForm)}, {s, ToExpression(s) === HumpEqual(a, b)})", //
        "{a≏b,True}");
    check("Module({s = ToString(LeftArrow(a, b), InputForm)}, {s, ToExpression(s) === LeftArrow(a, b)})", //
        "{a←b,True}");
    check("Module({s = ToString(LeftArrowBar(a, b), InputForm)}, {s, ToExpression(s) === LeftArrowBar(a, b)})", //
        "{a⇤b,True}");
    check("Module({s = ToString(LeftArrowRightArrow(a, b), InputForm)}, {s, ToExpression(s) === LeftArrowRightArrow(a, b)})", //
        "{a⇆b,True}");
    check("Module({s = ToString(LeftDownTeeVector(a, b), InputForm)}, {s, ToExpression(s) === LeftDownTeeVector(a, b)})", //
        "{a⥡b,True}");
    check("Module({s = ToString(LeftDownVector(a, b), InputForm)}, {s, ToExpression(s) === LeftDownVector(a, b)})", //
        "{a⇃b,True}");
    check("Module({s = ToString(LeftDownVectorBar(a, b), InputForm)}, {s, ToExpression(s) === LeftDownVectorBar(a, b)})", //
        "{a⥙b,True}");
    check("Module({s = ToString(LeftRightArrow(a, b), InputForm)}, {s, ToExpression(s) === LeftRightArrow(a, b)})", //
        "{a↔b,True}");
    check("Module({s = ToString(LeftRightVector(a, b), InputForm)}, {s, ToExpression(s) === LeftRightVector(a, b)})", //
        "{a⥎b,True}");
    check("Module({s = ToString(LeftTee(a, b), InputForm)}, {s, ToExpression(s) === LeftTee(a, b)})", //
        "{a⊣b,True}");
    check("Module({s = ToString(LeftTeeArrow(a, b), InputForm)}, {s, ToExpression(s) === LeftTeeArrow(a, b)})", //
        "{a↤b,True}");
    check("Module({s = ToString(LeftTeeVector(a, b), InputForm)}, {s, ToExpression(s) === LeftTeeVector(a, b)})", //
        "{a⥚b,True}");
    check("Module({s = ToString(LeftTriangle(a, b), InputForm)}, {s, ToExpression(s) === LeftTriangle(a, b)})", //
        "{a⊲b,True}");
    check("Module({s = ToString(LeftTriangleBar(a, b), InputForm)}, {s, ToExpression(s) === LeftTriangleBar(a, b)})", //
        "{a⧏b,True}");
    check("Module({s = ToString(LeftTriangleEqual(a, b), InputForm)}, {s, ToExpression(s) === LeftTriangleEqual(a, b)})", //
        "{a⊴b,True}");
    check("Module({s = ToString(LeftUpDownVector(a, b), InputForm)}, {s, ToExpression(s) === LeftUpDownVector(a, b)})", //
        "{a⥑b,True}");
    check("Module({s = ToString(LeftUpTeeVector(a, b), InputForm)}, {s, ToExpression(s) === LeftUpTeeVector(a, b)})", //
        "{a⥠b,True}");
    check("Module({s = ToString(LeftUpVector(a, b), InputForm)}, {s, ToExpression(s) === LeftUpVector(a, b)})", //
        "{a↿b,True}");
    check("Module({s = ToString(LeftUpVectorBar(a, b), InputForm)}, {s, ToExpression(s) === LeftUpVectorBar(a, b)})", //
        "{a⥘b,True}");
    check("Module({s = ToString(LeftVector(a, b), InputForm)}, {s, ToExpression(s) === LeftVector(a, b)})", //
        "{a↼b,True}");
    check("Module({s = ToString(LeftVectorBar(a, b), InputForm)}, {s, ToExpression(s) === LeftVectorBar(a, b)})", //
        "{a⥒b,True}");
    check("Module({s = ToString(LessEqualGreater(a, b), InputForm)}, {s, ToExpression(s) === LessEqualGreater(a, b)})", //
        "{a⋚b,True}");
    check("Module({s = ToString(LessFullEqual(a, b), InputForm)}, {s, ToExpression(s) === LessFullEqual(a, b)})", //
        "{a≦b,True}");
    check("Module({s = ToString(LessGreater(a, b), InputForm)}, {s, ToExpression(s) === LessGreater(a, b)})", //
        "{a≶b,True}");
    check("Module({s = ToString(LessLess(a, b), InputForm)}, {s, ToExpression(s) === LessLess(a, b)})", //
        "{a≪b,True}");
    check("Module({s = ToString(LessTilde(a, b), InputForm)}, {s, ToExpression(s) === LessTilde(a, b)})", //
        "{a≲b,True}");
    check("Module({s = ToString(LongLeftArrow(a, b), InputForm)}, {s, ToExpression(s) === LongLeftArrow(a, b)})", //
        "{a⟵b,True}");
    check("Module({s = ToString(LongLeftRightArrow(a, b), InputForm)}, {s, ToExpression(s) === LongLeftRightArrow(a, b)})", //
        "{a⟷b,True}");
    check("Module({s = ToString(LongRightArrow(a, b), InputForm)}, {s, ToExpression(s) === LongRightArrow(a, b)})", //
        "{a⟶b,True}");
    check("Module({s = ToString(LowerLeftArrow(a, b), InputForm)}, {s, ToExpression(s) === LowerLeftArrow(a, b)})", //
        "{a↙b,True}");
    check("Module({s = ToString(LowerRightArrow(a, b), InputForm)}, {s, ToExpression(s) === LowerRightArrow(a, b)})", //
        "{a↘b,True}");
    check("Module({s = ToString(NestedGreaterGreater(a, b), InputForm)}, {s, ToExpression(s) === NestedGreaterGreater(a, b)})", //
        "{a⪢b,True}");
    check("Module({s = ToString(NestedLessLess(a, b), InputForm)}, {s, ToExpression(s) === NestedLessLess(a, b)})", //
        "{a⪡b,True}");
    check("Module({s = ToString(NotCongruent(a, b), InputForm)}, {s, ToExpression(s) === NotCongruent(a, b)})", //
        "{a≢b,True}");
    check("Module({s = ToString(NotCupCap(a, b), InputForm)}, {s, ToExpression(s) === NotCupCap(a, b)})", //
        "{a≭b,True}");
    check("Module({s = ToString(NotDoubleVerticalBar(a, b), InputForm)}, {s, ToExpression(s) === NotDoubleVerticalBar(a, b)})", //
        "{a∦b,True}");
    check("Module({s = ToString(NotGreater(a, b), InputForm)}, {s, ToExpression(s) === NotGreater(a, b)})", //
        "{a≯b,True}");
    check("Module({s = ToString(NotGreaterEqual(a, b), InputForm)}, {s, ToExpression(s) === NotGreaterEqual(a, b)})", //
        "{a≱b,True}");
    check("Module({s = ToString(NotGreaterFullEqual(a, b), InputForm)}, {s, ToExpression(s) === NotGreaterFullEqual(a, b)})", //
        "{a≩b,True}");
    check("Module({s = ToString(NotGreaterLess(a, b), InputForm)}, {s, ToExpression(s) === NotGreaterLess(a, b)})", //
        "{a≹b,True}");
    check("Module({s = ToString(NotGreaterTilde(a, b), InputForm)}, {s, ToExpression(s) === NotGreaterTilde(a, b)})", //
        "{a≵b,True}");
    check("Module({s = ToString(NotLeftTriangle(a, b), InputForm)}, {s, ToExpression(s) === NotLeftTriangle(a, b)})", //
        "{a⋪b,True}");
    check("Module({s = ToString(NotLeftTriangleEqual(a, b), InputForm)}, {s, ToExpression(s) === NotLeftTriangleEqual(a, b)})", //
        "{a⋬b,True}");
    check("Module({s = ToString(NotLess(a, b), InputForm)}, {s, ToExpression(s) === NotLess(a, b)})", //
        "{a≮b,True}");
    check("Module({s = ToString(NotLessEqual(a, b), InputForm)}, {s, ToExpression(s) === NotLessEqual(a, b)})", //
        "{a≰b,True}");
    check("Module({s = ToString(NotLessFullEqual(a, b), InputForm)}, {s, ToExpression(s) === NotLessFullEqual(a, b)})", //
        "{a≨b,True}");
    check("Module({s = ToString(NotLessGreater(a, b), InputForm)}, {s, ToExpression(s) === NotLessGreater(a, b)})", //
        "{a≸b,True}");
    check("Module({s = ToString(NotLessTilde(a, b), InputForm)}, {s, ToExpression(s) === NotLessTilde(a, b)})", //
        "{a≴b,True}");
    check("Module({s = ToString(NotPrecedes(a, b), InputForm)}, {s, ToExpression(s) === NotPrecedes(a, b)})", //
        "{a⊀b,True}");
    check("Module({s = ToString(NotPrecedesSlantEqual(a, b), InputForm)}, {s, ToExpression(s) === NotPrecedesSlantEqual(a, b)})", //
        "{a⋠b,True}");
    check("Module({s = ToString(NotPrecedesTilde(a, b), InputForm)}, {s, ToExpression(s) === NotPrecedesTilde(a, b)})", //
        "{a⋨b,True}");
    check("Module({s = ToString(NotReverseElement(a, b), InputForm)}, {s, ToExpression(s) === NotReverseElement(a, b)})", //
        "{a∌b,True}");
    check("Module({s = ToString(NotRightTriangle(a, b), InputForm)}, {s, ToExpression(s) === NotRightTriangle(a, b)})", //
        "{a⋫b,True}");
    check("Module({s = ToString(NotRightTriangleEqual(a, b), InputForm)}, {s, ToExpression(s) === NotRightTriangleEqual(a, b)})", //
        "{a⋭b,True}");
    check("Module({s = ToString(NotSquareSubsetEqual(a, b), InputForm)}, {s, ToExpression(s) === NotSquareSubsetEqual(a, b)})", //
        "{a⋢b,True}");
    check("Module({s = ToString(NotSquareSupersetEqual(a, b), InputForm)}, {s, ToExpression(s) === NotSquareSupersetEqual(a, b)})", //
        "{a⋣b,True}");
    check("Module({s = ToString(NotSubset(a, b), InputForm)}, {s, ToExpression(s) === NotSubset(a, b)})", //
        "{a⊄b,True}");
    check("Module({s = ToString(NotSubsetEqual(a, b), InputForm)}, {s, ToExpression(s) === NotSubsetEqual(a, b)})", //
        "{a⊈b,True}");
    check("Module({s = ToString(NotSucceeds(a, b), InputForm)}, {s, ToExpression(s) === NotSucceeds(a, b)})", //
        "{a⊁b,True}");
    check("Module({s = ToString(NotSucceedsSlantEqual(a, b), InputForm)}, {s, ToExpression(s) === NotSucceedsSlantEqual(a, b)})", //
        "{a⋡b,True}");
    check("Module({s = ToString(NotSucceedsTilde(a, b), InputForm)}, {s, ToExpression(s) === NotSucceedsTilde(a, b)})", //
        "{a⋩b,True}");
    check("Module({s = ToString(NotSuperset(a, b), InputForm)}, {s, ToExpression(s) === NotSuperset(a, b)})", //
        "{a⊅b,True}");
    check("Module({s = ToString(NotSupersetEqual(a, b), InputForm)}, {s, ToExpression(s) === NotSupersetEqual(a, b)})", //
        "{a⊉b,True}");
    check("Module({s = ToString(NotTilde(a, b), InputForm)}, {s, ToExpression(s) === NotTilde(a, b)})", //
        "{a≁b,True}");
    check("Module({s = ToString(NotTildeEqual(a, b), InputForm)}, {s, ToExpression(s) === NotTildeEqual(a, b)})", //
        "{a≄b,True}");
    check("Module({s = ToString(NotTildeFullEqual(a, b), InputForm)}, {s, ToExpression(s) === NotTildeFullEqual(a, b)})", //
        "{a≇b,True}");
    check("Module({s = ToString(NotTildeTilde(a, b), InputForm)}, {s, ToExpression(s) === NotTildeTilde(a, b)})", //
        "{a≉b,True}");
    check("Module({s = ToString(Precedes(a, b), InputForm)}, {s, ToExpression(s) === Precedes(a, b)})", //
        "{a≺b,True}");
    check("Module({s = ToString(PrecedesEqual(a, b), InputForm)}, {s, ToExpression(s) === PrecedesEqual(a, b)})", //
        "{a⪯b,True}");
    check("Module({s = ToString(PrecedesSlantEqual(a, b), InputForm)}, {s, ToExpression(s) === PrecedesSlantEqual(a, b)})", //
        "{a≼b,True}");
    check("Module({s = ToString(PrecedesTilde(a, b), InputForm)}, {s, ToExpression(s) === PrecedesTilde(a, b)})", //
        "{a≾b,True}");
    check("Module({s = ToString(Proportion(a, b), InputForm)}, {s, ToExpression(s) === Proportion(a, b)})", //
        "{a∷b,True}");
    check("Module({s = ToString(Proportional(a, b), InputForm)}, {s, ToExpression(s) === Proportional(a, b)})", //
        "{a∝b,True}");
    check("Module({s = ToString(ReverseElement(a, b), InputForm)}, {s, ToExpression(s) === ReverseElement(a, b)})", //
        "{a∋b,True}");
    check("Module({s = ToString(ReverseEquilibrium(a, b), InputForm)}, {s, ToExpression(s) === ReverseEquilibrium(a, b)})", //
        "{a⇋b,True}");
    check("Module({s = ToString(ReverseUpEquilibrium(a, b), InputForm)}, {s, ToExpression(s) === ReverseUpEquilibrium(a, b)})", //
        "{a⥯b,True}");
    check("Module({s = ToString(RightArrow(a, b), InputForm)}, {s, ToExpression(s) === RightArrow(a, b)})", //
        "{a→b,True}");
    check("Module({s = ToString(RightArrowBar(a, b), InputForm)}, {s, ToExpression(s) === RightArrowBar(a, b)})", //
        "{a⇥b,True}");
    check("Module({s = ToString(RightArrowLeftArrow(a, b), InputForm)}, {s, ToExpression(s) === RightArrowLeftArrow(a, b)})", //
        "{a⇄b,True}");
    check("Module({s = ToString(RightDownTeeVector(a, b), InputForm)}, {s, ToExpression(s) === RightDownTeeVector(a, b)})", //
        "{a⥝b,True}");
    check("Module({s = ToString(RightDownVector(a, b), InputForm)}, {s, ToExpression(s) === RightDownVector(a, b)})", //
        "{a⇂b,True}");
    check("Module({s = ToString(RightDownVectorBar(a, b), InputForm)}, {s, ToExpression(s) === RightDownVectorBar(a, b)})", //
        "{a⥕b,True}");
    check("Module({s = ToString(RightTee(a, b), InputForm)}, {s, ToExpression(s) === RightTee(a, b)})", //
        "{a⊢b,True}");
    check("Module({s = ToString(RightTeeArrow(a, b), InputForm)}, {s, ToExpression(s) === RightTeeArrow(a, b)})", //
        "{a↦b,True}");
    check("Module({s = ToString(RightTeeVector(a, b), InputForm)}, {s, ToExpression(s) === RightTeeVector(a, b)})", //
        "{a⥛b,True}");
    check("Module({s = ToString(RightTriangle(a, b), InputForm)}, {s, ToExpression(s) === RightTriangle(a, b)})", //
        "{a⊳b,True}");
    check("Module({s = ToString(RightTriangleBar(a, b), InputForm)}, {s, ToExpression(s) === RightTriangleBar(a, b)})", //
        "{a⧐b,True}");
    check("Module({s = ToString(RightTriangleEqual(a, b), InputForm)}, {s, ToExpression(s) === RightTriangleEqual(a, b)})", //
        "{a⊵b,True}");
    check("Module({s = ToString(RightUpDownVector(a, b), InputForm)}, {s, ToExpression(s) === RightUpDownVector(a, b)})", //
        "{a⥏b,True}");
    check("Module({s = ToString(RightUpTeeVector(a, b), InputForm)}, {s, ToExpression(s) === RightUpTeeVector(a, b)})", //
        "{a⥜b,True}");
    check("Module({s = ToString(RightUpVector(a, b), InputForm)}, {s, ToExpression(s) === RightUpVector(a, b)})", //
        "{a↾b,True}");
    check("Module({s = ToString(RightUpVectorBar(a, b), InputForm)}, {s, ToExpression(s) === RightUpVectorBar(a, b)})", //
        "{a⥔b,True}");
    check("Module({s = ToString(RightVector(a, b), InputForm)}, {s, ToExpression(s) === RightVector(a, b)})", //
        "{a⇀b,True}");
    check("Module({s = ToString(RightVectorBar(a, b), InputForm)}, {s, ToExpression(s) === RightVectorBar(a, b)})", //
        "{a⥓b,True}");
    check("Module({s = ToString(SquareIntersection(a, b), InputForm)}, {s, ToExpression(s) === SquareIntersection(a, b)})", //
        "{a⊓b,True}");
    check("Module({s = ToString(SquareSubset(a, b), InputForm)}, {s, ToExpression(s) === SquareSubset(a, b)})", //
        "{a⊏b,True}");
    check("Module({s = ToString(SquareSubsetEqual(a, b), InputForm)}, {s, ToExpression(s) === SquareSubsetEqual(a, b)})", //
        "{a⊑b,True}");
    check("Module({s = ToString(SquareSuperset(a, b), InputForm)}, {s, ToExpression(s) === SquareSuperset(a, b)})", //
        "{a⊐b,True}");
    check("Module({s = ToString(SquareSupersetEqual(a, b), InputForm)}, {s, ToExpression(s) === SquareSupersetEqual(a, b)})", //
        "{a⊒b,True}");
    check("Module({s = ToString(SquareUnion(a, b), InputForm)}, {s, ToExpression(s) === SquareUnion(a, b)})", //
        "{a⊔b,True}");
    check("Module({s = ToString(Subset(a, b), InputForm)}, {s, ToExpression(s) === Subset(a, b)})", //
        "{a⊂b,True}");
    check("Module({s = ToString(SubsetEqual(a, b), InputForm)}, {s, ToExpression(s) === SubsetEqual(a, b)})", //
        "{a⊆b,True}");
    check("Module({s = ToString(Succeeds(a, b), InputForm)}, {s, ToExpression(s) === Succeeds(a, b)})", //
        "{a≻b,True}");
    check("Module({s = ToString(SucceedsEqual(a, b), InputForm)}, {s, ToExpression(s) === SucceedsEqual(a, b)})", //
        "{a⪰b,True}");
    check("Module({s = ToString(SucceedsSlantEqual(a, b), InputForm)}, {s, ToExpression(s) === SucceedsSlantEqual(a, b)})", //
        "{a≽b,True}");
    check("Module({s = ToString(SucceedsTilde(a, b), InputForm)}, {s, ToExpression(s) === SucceedsTilde(a, b)})", //
        "{a≿b,True}");
    check("Module({s = ToString(Superset(a, b), InputForm)}, {s, ToExpression(s) === Superset(a, b)})", //
        "{a⊃b,True}");
    check("Module({s = ToString(SupersetEqual(a, b), InputForm)}, {s, ToExpression(s) === SupersetEqual(a, b)})", //
        "{a⊇b,True}");
    check("Module({s = ToString(TildeEqual(a, b), InputForm)}, {s, ToExpression(s) === TildeEqual(a, b)})", //
        "{a≃b,True}");
    check("Module({s = ToString(TildeFullEqual(a, b), InputForm)}, {s, ToExpression(s) === TildeFullEqual(a, b)})", //
        "{a≅b,True}");
    check("Module({s = ToString(TildeTilde(a, b), InputForm)}, {s, ToExpression(s) === TildeTilde(a, b)})", //
        "{a≈b,True}");
    check("Module({s = ToString(UnionPlus(a, b), InputForm)}, {s, ToExpression(s) === UnionPlus(a, b)})", //
        "{a⊎b,True}");
    check("Module({s = ToString(UpArrowBar(a, b), InputForm)}, {s, ToExpression(s) === UpArrowBar(a, b)})", //
        "{a⤒b,True}");
    check("Module({s = ToString(UpArrowDownArrow(a, b), InputForm)}, {s, ToExpression(s) === UpArrowDownArrow(a, b)})", //
        "{a⇅b,True}");
    check("Module({s = ToString(UpDownArrow(a, b), InputForm)}, {s, ToExpression(s) === UpDownArrow(a, b)})", //
        "{a↕b,True}");
    check("Module({s = ToString(UpEquilibrium(a, b), InputForm)}, {s, ToExpression(s) === UpEquilibrium(a, b)})", //
        "{a⥮b,True}");
    check("Module({s = ToString(UpTee(a, b), InputForm)}, {s, ToExpression(s) === UpTee(a, b)})", //
        "{a⊥b,True}");
    check("Module({s = ToString(UpTeeArrow(a, b), InputForm)}, {s, ToExpression(s) === UpTeeArrow(a, b)})", //
        "{a↥b,True}");
    check("Module({s = ToString(UpperLeftArrow(a, b), InputForm)}, {s, ToExpression(s) === UpperLeftArrow(a, b)})", //
        "{a↖b,True}");
    check("Module({s = ToString(UpperRightArrow(a, b), InputForm)}, {s, ToExpression(s) === UpperRightArrow(a, b)})", //
        "{a↗b,True}");
    check("Module({s = ToString(VerticalTilde(a, b), InputForm)}, {s, ToExpression(s) === VerticalTilde(a, b)})", //
        "{a≀b,True}");
  }
}
