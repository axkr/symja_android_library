package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;

/**
 * Language features which packages written for Mathematica rely on, found by loading FeynCalc
 * unmodified (SymjaTests, <code>org.matheclipse.junit.packages</code>). The inputs are made up for
 * these tests.
 */
public class PackageSupportTest extends ExprEvaluatorTestCase {

  /** A number before .. or ... is the operand of Repeated, not the real number 0. */
  @Test
  public void testRepeatedAfterANumber() {
    check("FullForm(Hold({0.., 1...}))", //
        "Hold(List(Repeated(0), RepeatedNull(1)))");
    check("MatchQ({0, 0, 0}, {0..})", //
        "True");
    check("FullForm(Hold(x:1..))", //
        "Hold(Pattern(x, Repeated(1)))");
  }

  /** A blank starts an operand: _. _f and _Integer _ are products. */
  @Test
  public void testProductOfPatterns() {
    check("FullForm(Hold(_. _f))", //
        "Hold(Times(Optional(Blank()), Blank(f)))");
    check("FullForm(Hold(_Integer _))", //
        "Hold(Times(Blank(Integer), Blank()))");
    check("MatchQ(3*y, _Integer _)", //
        "True");
  }

  /** A comment standing alone on its lines is read as the statement Null. */
  @Test
  public void testReadCommentIsNullAndEndsWithEndOfFile() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    try {
      Config.FILESYSTEM_ENABLED = true;
      check("s = StringToStream(\"(* a comment\\n over two lines *)\\nf(1)\\n(* only a comment *)\\n\"); "
          + "{Read(s, Hold(Expression)), Read(s, Hold(Expression)), Read(s, Expression), Read(s, Expression)}", //
          "{Hold(Null),Hold(f(1)),Null,EndOfFile}");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  /** Format and MakeBoxes definitions are kept, as up-values of the head they are about. */
  @Test
  public void testFormatAndMakeBoxesDefinitions() {
    check("Format(psfq(x_)) := Subscript(q, x); UpValues(psfq)", //
        "{HoldPattern(Format(psfq(x_))):>Subscript(q,x)}");
    check("MakeBoxes(c_. * psfr(x_), TraditionalForm) := \"r\"; Length(UpValues(psfr))", //
        "1");
  }

  /** Derivative is not protected: derivatives of a function can be defined. */
  @Test
  public void testDerivativeDefinitions() {
    check("psff'(x_) := 2*x", //
        "");
    check("{psff'(3), D(psff(y), y)}", //
        "{6,2*y}");
    check("Attributes(Derivative)", //
        "{NHoldAll}");
  }

  /** The rules and options of a symbol can be prepended and appended to. */
  @Test
  public void testAppendToDefinitionLists() {
    check("psfc(x_) := gen; PrependTo(DownValues(psfc), HoldPattern(psfc(1)) :> one); "
        + "{psfc(1), psfc(2)}", //
        "{one,gen}");
    check("Options(psfo) = {a -> 1}; AppendTo(Options(psfo), b -> 2); Options(psfo)", //
        "{a->1,b->2}");
  }

  /** An assignment to DownValues replaces all of them, also the memoized values. */
  @Test
  public void testDownValuesAssignmentReplaces() {
    check("psfm(x_) := (psfm(x) = False); psfm(c); psfm(d); "
        + "DownValues(psfm) = Select(DownValues(psfm), FreeQ(#, c) &); DownValues(psfm)", //
        "{HoldPattern(psfm(d)):>False,HoldPattern(psfm(x_)):>(psfm(x)=False)}");
    check("DownValues(psfm) = {HoldPattern(psfm(d)) :> 7}; {psfm(d), psfm(e)}", //
        "{7,psfm(e)}");
  }

  /** ReplaceRepeated ends when the expression no longer changes. */
  @Test
  public void testReplaceRepeatedEndsAtAFixedPoint() {
    check("psfu(x_) := x; f(1) //. f :> psfu", //
        "1");
    check("psfv(x_) := f(x); f(1) //. f :> psfv", //
        "f(1)");
  }

  /** A condition is evaluated normally also when its rule is tried on a left-hand side. */
  @Test
  public void testConditionOnALeftHandSide() {
    check("Options(psfoo) = {k -> 1}; seen = {}; "
        + "psfcond(x_) := (AppendTo(seen, Options(psfoo)); False); psfh2(x_) /; psfcond(x) := 1; "
        + "psfk2(psfh2(z_)) := 2; seen", //
        "{{k->1}}");
  }

  @Test
  public void testOptionQ() {
    check("{OptionQ(a -> 1), OptionQ({a -> 1, {b :> 2}}), OptionQ({}), OptionQ(x), "
        + "OptionQ({a -> 1, x})}", //
        "{True,True,True,False,False}");
    // the name of an option is a symbol or a string
    check("{OptionQ(\"p\" -> 1), OptionQ(2 -> 1), OptionQ(g(p) -> 1)}", //
        "{True,False,False}");
    // and FilterRules takes either for the other
    check("{FilterRules({p -> 1, \"q\" -> 2}, q), FilterRules({\"p\" -> 1, q -> 2}, p), FilterRules({p -> 1, \"q\" -> 2}, Except(q)), FilterRules({p -> 1, q -> 2}, Except(p | q))} // InputForm", //
        "{{\"q\"->2},{\"p\"->1},{p->1},{}}");
  }

  @Test
  public void testFilterRules() {
    check("FilterRules({a -> 1, b -> 2, c -> 3}, {a, c})", //
        "{a->1,c->3}");
    check("FilterRules({a -> 1, b -> 2}, Except({a -> 3}))", //
        "{b->2}");
    check("FilterRules({a -> 1, b -> 2}, Except(a | b))", //
        "{}");
    check("Options(psfopt) = {b -> 0}; FilterRules({a -> 1, b -> 2}, Options(psfopt))", //
        "{b->2}");
  }

  @Test
  public void testHoldPatternWithOptionsPattern() {
    check("MatchQ(psfq8(a), HoldPattern(psfq8(x_, OptionsPattern())))", //
        "True");
    check("MatchQ(psfq8(a, c -> 1), HoldPattern(psfq8(x_, OptionsPattern())))", //
        "True");
  }

  @Test
  public void testNamedOptionsPattern() {
    check("MatchQ(psfc2(d, a), psfc2(e_, f_, opts:OptionsPattern()))", //
        "True");
    check("MatchQ(psfc2(d, a, b), psfc2(e_, f_, g__, opts:OptionsPattern()))", //
        "True");
    check("psff8(x_, opts:OptionsPattern({k -> 3})) := OptionValue(k); {psff8(1), psff8(1, k -> 4)}", //
        "{3,4}");
  }

  /** A sequence name which occurs twice stands for the same sequence both times. */
  @Test
  public void testRepeatedSequenceName() {
    check("MatchQ(g(h(2), h(3)), g(h(c___), h(c___)))", //
        "False");
    check("MatchQ(g(h(1, 2), h(1, 3)), g(h(b_, c___), h(d_, c___)))", //
        "False");
    check("MatchQ(g(h(2, 3), h(2, 3)), g(h(c__), h(c__)))", //
        "True");
    check("MatchQ(g(1, 2), g(__, __))", //
        "True");
    // the first argument is matched again when the second one fails
    check("h(i, j1, k) + h(i, j2, k) /. h(a___, b1_, c___) + h(a___, b2_, c___) :> h(a, b1 + b2, c)", //
        "h(i,j1+j2,k)");
  }

  /** A sequence nested in a factor; the explicit forms Pattern(r, BlankSequence(h)). */
  @Test
  public void testNestedAndExplicitSequences() {
    check("psfdg(x_ * psfm1(p_, d1___), d2___) := x * psfdg(psfm1(p, d1), d2); "
        + "{psfdg(2 * psfm1(b)), psfdg(-psfm1(p, d), d)}", //
        "{2*psfdg(psfm1(b)),-psfdg(psfm1(p,d),d)}");
    check("{MatchQ(g(1, 2), g(Pattern(r, BlankSequence(Integer)))), "
        + "MatchQ(g(1, x), g(BlankSequence(Integer))), MatchQ(g(), g(Pattern(r, BlankNullSequence())))}", //
        "{True,False,True}");
    check("MatchQ(a . c, Pattern(r1, BlankNullSequence()) . a . c . Pattern(r2, BlankNullSequence()))", //
        "True");
  }

  /** A named repetition b:p.. stands for the whole sequence. */
  @Test
  public void testNamedRepeated() {
    check("psffs(a_, b:Except(_?OptionQ).., opts:OptionsPattern()) := {a, {b}}; "
        + "{psffs(1, 2), psffs(1, 2, x*y), psffs(1, 2, 3, k -> 1)}", //
        "{{1,{2}},{1,{2,x*y}},{1,{2,3}}}");
    check("{MatchQ(g(2, 3), g(b:_Integer..)), MatchQ(g(2, x), g(b:_Integer..)), "
        + "g(1, 1, 2) /. g(b:1.., c_) :> {{b}, c}}", //
        "{True,False,{{1,1},2}}");
  }

  /** OptionValue in a rule with a condition reads the options of that rule, also when called from another. */
  @Test
  public void testOptionValueInAConditionalRule() {
    check("Options(psfg2) = {Inverse -> False}; "
        + "psfg2(x_, h_List, OptionsPattern()) := Block({r}, r = OptionValue(Inverse); {x, r}) /; "
        + "!FreeQ(x, Alternatives @@ h); "
        + "psfd2(x_, h_List, opts:OptionsPattern()) := psfg2(x, h, Inverse -> True, opts); "
        + "{psfg2(k(a), {k}, Inverse -> True), psfd2(k(a), {k})}", //
        "{{k(a),True},{k(a),True}}");
  }

  /** Unevaluated(Sequence(...)) as an argument is spliced in, so a rule can delete an element. */
  @Test
  public void testUnevaluatedSequenceArgument() {
    check("{{1, 2} /. 1 :> Unevaluated(Sequence()), {1} /. 1 :> Unevaluated(Sequence()), "
        + "f(Unevaluated(Sequence()), 2), Hold(Unevaluated(Sequence()))}", //
        "{{2},{},f(2),Hold(Unevaluated(Sequence()))}");
  }

  /** OptionValue(f, {opts}, name) in a rule reads the options the rule was called with. */
  @Test
  public void testOptionValueWithRuleOptions() {
    check("Options(psfsp) = {SetDims -> {4, D}}; "
        + "psff5(a_, c:OptionsPattern()) := OptionValue(psfsp, {c}, SetDims); "
        + "{psff5(1, SetDims -> {X}), psff5(1)}", //
        "{{X},{4,D}}");
  }

  /** A condition around a sequence pattern sees the whole sequence, which may be empty. */
  @Test
  public void testConditionAroundASequence() {
    check("psftt(x__ /; Length({x}) > 1) := ok; {psftt(1), psftt(1, 2), psftt(1, 2, 3)}", //
        "{psftt(1),ok,ok}");
    check("psftu(a_, x___ /; Length({x}) =!= 1) := {a, {x}}; {psftu(1), psftu(1, 2), psftu(1, 2, 3)}", //
        "{{1,{}},psftu(1,2),{1,{2,3}}}");
    check("psfiso(ex_, (opts:OptionsPattern()) /; opts =!= {}) := {ex, {opts}}; "
        + "{psfiso(a), psfiso(a, x -> 1, y -> 2)}", //
        "{{a,{}},{a,{x->1,y->2}}}");
  }

  /** A pattern f(y_) of a OneIdentity (and Flat) symbol matches f(...) expressions only. */
  @Test
  public void testOneIdentityPatternOfAUserSymbol() {
    check("SetAttributes(psfsf, {Flat, OneIdentity}); "
        + "{MatchQ(a, psfsf(y__)), MatchQ(psfsf(a), psfsf(y__)), "
        + "Cases({a, psfsf(b), g(c)}, psfsf(y__), Infinity)}", //
        "{False,True,{psfsf(b)}}");
    // a value f(b) is kept as it is; only a part the Flat matcher splits off is unwrapped
    check("{f(psfsf(b)) /. f(x_) :> {x}, psfsf(a, b, c) /. psfsf(x_, y_) :> {x, y}}", //
        "{{psfsf(b)},{a,psfsf(b,c)}}");
    check("v1(psfsf(b)) /. {psfsf(b) -> psfsf(sh(f1))}", //
        "v1(psfsf(sh(f1)))");
  }

  /** A script read by ToExpression gives a - 4 the number -4, as a single expression does. */
  @Test
  public void testSubtractedNumberInAScript() {
    check("ToExpression(\"psfx1 = FullForm(Hold(d_Symbol-4, a-b))\\npsfx1\")", //
        "Hold(Plus(Pattern(d, Blank(Symbol)), -4), Plus(a, Times(-1, b)))");
  }

  /** A variable of CoefficientArrays is not rewritten while the polynomial is expanded. */
  @Test
  public void testCoefficientArraysWithACompoundVariable() {
    check("Normal(CoefficientArrays(-2*h(x/(m^4 - 2*m^2*y + y^2)), {h(x/(m^4 - 2*m^2*y + y^2))}))", //
        "{0,{-2}}");
  }

  /** Permute by a permutation group gives the expression permuted by each group element. */
  @Test
  public void testPermuteByAGroup() {
    check("Permute({a, b}, PermutationGroup({Cycles({{1, 2}})}))", //
        "{{a,b},{b,a}}");
  }

  /** SetOptions changes the defaults of a symbol's options. */
  @Test
  public void testSetOptions() {
    check("Options(psfpol) = {Tr -> False, Q -> 1}; {SetOptions(psfpol, Tr -> True), "
        + "SetOptions(psfpol, {Q -> 2}), Options(psfpol)}", //
        "{{Tr->True,Q->1},{Tr->True,Q->2},{Q->2,Tr->True}}");
  }

  /** A rule which replaces some terms of a sum decides with its condition rhs /; test. */
  @Test
  public void testConditionOfARuleForSomeTerms() {
    check("{a + 2*m(x) + 3*m(y) /. (n3_. * m(x_) + n4_. * m(y_)) :> "
        + "(m(n3*x + n4*y) /; NumberQ(n3) && NumberQ(n4)), "
        + "a + b*m(x) + 3*m(y) /. (n3_. * m(x_) + n4_. * m(y_)) :> "
        + "(m(n3*x + n4*y) /; NumberQ(n3) && NumberQ(n4))}", //
        "{a+m(2*x+3*y),a+b*m(x)+3*m(y)}");
  }

  /** A pattern test on the last argument, next to a sequence nested in another argument. */
  @Test
  public void testTrailingPatternTestNextToASequence() {
    check("psfg16 /: psfg16(a__)^n_Integer?Positive := {a, n}; "
        + "{psfg16(x)^2, psfg16(x, y)^3, psfg16(x)^(-1)}", //
        "{{x,2},{x,y,3},1/psfg16(x)}");
  }

  /** Dispatch of a dispatch table is the table. */
  @Test
  public void testDispatchOfADispatchTable() {
    check("psfd = Dispatch({a -> 1, b -> 2}); {{a, b, c} /. Dispatch(psfd), FullForm(Dispatch({}))}", //
        "{{1,2,c},Dispatch(List())}");
  }

  /** The coefficients of a polynomial are not evaluated with approximate arguments. */
  @Test
  public void testCoefficientsStayExact() {
    check("psfr(x_Real) := (Print(\"approximated\"); Abort()); "
        + "{Coefficient(x2 * psfr(5) * y, y), Normal(CoefficientArrays(x2 * psfr(5) * y + 1, {y}))}", //
        "{x2*psfr(5),{1,{x2*psfr(5)}}}");
    check("PossibleZeroQ(Sqrt(2)^2 - 2 + 0 * psfr(5))", //
        "True");
  }

  /** A replacement reaches the parts a pattern is written with. */
  @Test
  public void testReplaceInsidePatterns() {
    check("{a_, b__, c_Integer} /. Pattern :> pid", //
        "{pid(a,_),pid(b,__),pid(c,_Integer)}");
  }

  /** In a Flat head a ___ may stand for no argument at all. */
  @Test
  public void testFlatHeadWithNullSequence() {
    check("MatchQ((x + y) . z . w, Dot(a___, b_ + c_, d___))", //
        "True");
    check("(g(1) + g(2)) . g(3) . g(4) //. Dot(a___, b_ + c_, d___) :> Distribute(Dot(a, b + c, d))", //
        "g(1).g(3).g(4)+g(2).g(3).g(4)");
  }

  /** A failing condition rhs /; test lets a rule try the other ways to match, also in ReplaceAll. */
  @Test
  public void testRightHandSideConditionBacktracks() {
    check("x*y /. a_*b_ :> {a, b} /; a === y", //
        "{y,x}");
    check("SetAttributes(psfp4, Orderless); "
        + "psfp4 /: psfp4(psfli(z_), psfli(x_)) * f_(a__) := {z, x} /; z === mu; "
        + "psfp4(psfli(mu), psfli(nu)) * dg(psfli(nu))", //
        "{mu,nu}");
  }

  /** A condition inside an argument does not decide about the definition it is used in. */
  @Test
  public void testConditionInsideARuleList() {
    check("psfco(e_) := e /. {psfcm(a_, b_) /; !OrderedQ({a, b}) :> -psfcm(b, a)}; "
        + "psfco(psfcm(x, y) + psfcm(y, x))", //
        "0");
    check("psfg1(x_) := Module({y = x}, y /; y > 0); {psfg1(1), psfg1(-1)}", //
        "{1,psfg1(-1)}");
  }

  /** An empty or nested list of options matches OptionsPattern. */
  @Test
  public void testEmptyOptionList() {
    check("psff9(x_, OptionsPattern()) := 1; {psff9(a, {}), psff9(a, b -> 1, {}), psff9(a, {{}}), "
        + "psff9(a, {b -> 1, c})}", //
        "{1,1,1,psff9(a,{b->1,c})}");
  }

  /** The names are sorted. */
  @Test
  public void testNamesAreSorted() {
    check("psfnb = 1; psfna = 2; psfnc = 3; Names(\"Global`psfn*\")", //
        "{psfna,psfnb,psfnc}");
  }

  /** The pattern and the options of Cases may be computed. */
  @Test
  public void testCasesComputedArguments() {
    check("Cases({d(2), a(1)}, Alternatives @@ (HoldPattern(#(___)) & /@ {a, b}), Infinity)", //
        "{a(1)}");
    check("Cases({d(2), a(1)}, _a, Infinity, FilterRules({}, Options(Cases)))", //
        "{a(1)}");
    check("Cases({d(2), a(1)}, _Symbol, Infinity, {Heads -> True})", //
        "{List,d,a}");
  }

  @Test
  public void testLongestCommonSequence() {
    check("{LongestCommonSequence(\"AAABBBBCCCCC\", \"CCCBBBAAABABA\"), "
        + "LongestCommonSubsequence(\"AAABBBBCCCCC\", \"CCCBBBAAABABA\"), "
        + "LongestCommonSequence({1, 2, 3, 4}, {2, 4, 5})}", //
        "{AAABB,AAAB,{2,4}}");
  }

  @Test
  public void testToFileName() {
    // joined with the separator of the platform: a\b\c.m on Windows
    final String s = java.io.File.separator;
    check("{ToFileName({\"a\", \"b\"}, \"c.m\"), ToFileName(\"a\", \"c.m\")}", //
        "{a" + s + "b" + s + "c.m,a" + s + "c.m}");
  }

  @Test
  public void testInternalSyntacticNegativeQ() {
    check("{Internal`SyntacticNegativeQ(-a), Internal`SyntacticNegativeQ(a), "
        + "Internal`SyntacticNegativeQ(-3*b)}", //
        "{True,False,True}");
  }

  /** Each TimeConstrained has its own deadline; the first one used to switch off all later ones. */
  @Test
  @org.junit.jupiter.api.Timeout(60)
  public void testSecondTimeConstrainedStillStops() {
    check("TimeConstrained(1 + 1, 5)", //
        "2");
    check("TimeConstrained(While(True), 0.05, stopped)", //
        "stopped");
  }

  /**
   * A TimeConstrained inside a rule which runs under another TimeConstrained is evaluated in the
   * same thread; a thread of its own waited for the lock of the rule's symbol.
   */
  @Test
  @org.junit.jupiter.api.Timeout(30)
  public void testNestedTimeConstrainedInARule() {
    check("psftc(0) := 0; psftc(n_) := TimeConstrained(psftc(n - 1), 5, stalled); "
        + "TimeConstrained(psftc(3), 20, outer)", //
        "0");
  }

  /** Pattern(#, _) and Complex(b__) stay expressions until they are evaluated. */
  @Test
  public void testHeldPatternConstructs() {
    check("psftf = Pattern(#, _) &; psftf(zz)", //
        "zz_");
    check("Hold(Complex(b__))", //
        "Hold(Complex(b__))");
  }

  /** Null is the empty box. */
  @Test
  public void testToExpressionNull() {
    check("ToExpression(Null) === Null", //
        "True");
  }

  /** Alternatives @@ list builds the pattern also when the list holds patterns. */
  @Test
  public void testApplyInAPattern() {
    check("psfta(x_, y_) := FreeQ(x, Alternatives @@ y); "
        + "{psfta(k(g(p)), {g(p, ___), g(h(p, ___), ___)}), psfta(k(g(q)), {g(p, ___), g(h(p, ___), ___)})}", //
        "{False,True}");
  }

  @Test
  public void testOuterLevels() {
    check("Outer(f, {{a, b}, {c, d}}, {{x, y}}, 1)", //
        "{{f({a,b},{x,y})},{f({c,d},{x,y})}}");
    check("Outer(f, {{a, b}, {c, d}}, {{x, y}}, 1, 2)", //
        "{{{f({a,b},x),f({a,b},y)}},{{f({c,d},x),f({c,d},y)}}}");
    check("Outer(Union, {{u1, u2}, {u1, u3}}, {{-v}}, 1)", //
        "{{{u1,u2,-v}},{{u1,u3,-v}}}");
  }

  /** m[[i]][[j]] = value is m[[i, j]] = value. */
  @Test
  public void testNestedPartAssignment() {
    check("psftm = {{1, 2}, {3, 4}, {5, 6}}; psftm[[3]][[2]] = psftm[[3]][[2]] + 1; psftm", //
        "{{1,2},{3,4},{5,7}}");
  }

  /** The rules of a function in a pattern run as ordinary code: Options(f) in them evaluates. */
  @Test
  public void testFunctionInAPatternEvaluatesItsBody() {
    check("Options(psftf2) = {psftk -> 1}; psftf2(x_) := x /. Options(psftf2); "
        + "{MatchQ(w(1)*w(z), psftf2(w(psftk))*w(z_)), MatchQ(w(1)*w(z), psftf2(w(psftk))*psftf2(w(z_)))}", //
        "{True,True}");
  }

  /** Expand(expr, h) expands the parts which contain an h(...), as FreeQ sees heads. */
  @Test
  public void testExpandWithAHead() {
    check("Expand((a(ci(1))*b(ci(2)) - c(ci(3)))*d(ci(4)), ci)", //
        "a(ci(1))*b(ci(2))*d(ci(4))-c(ci(3))*d(ci(4))");
  }

  @Test
  public void testTimeConstrainedInfinity() {
    check("TimeConstrained(1 + 1, Infinity, failed)", //
        "2");
  }

  @Test
  public void testDotOfVectorsKeepsProducts() {
    check("{(a + b)*u}.{d}", //
        "(a+b)*d*u");
  }

  /** As for down-values, up-values with the same left-hand side and other conditions coexist. */
  @Test
  public void testConditionalUpValuesWithTheSameLHS() {
    check("psftu /: psftu(x_)^n_Integer?Positive := up1 /; x > 0; "
        + "psftu /: psftu(x_)^n_Integer?Positive := up2 /; x < 0; "
        + "{psftu(1)^2, psftu(-1)^2, Length(UpValues(psftu))}", //
        "{up1,up2,2}");
    check("psftw /: psftw(x_)^n_ := w1; psftw /: psftw(x_)^n_ := w2; "
        + "{psftw(1)^2, Length(UpValues(psftw))}", //
        "{w2,1}");
  }

  /**
   * An up-value on a product of four factors: each binding of its condition is tested once, also
   * when the factors share the symbol and the product is matched part by part.
   */
  @Test
  public void testUpValueConditionOncePerBinding() {
    check("SetAttributes(psftpc, Orderless); psftcnt = 0; "
        + "psftpc /: psftpc(psftli(z_, dim___), h_(x_, dim___))*f_(a__) := "
        + "(f(a) /. psftli(z, ___) -> h(x, dim)) /; (psftcnt++; !FreeQ(f(a), psftli(z, ___))); "
        + "psftpc(psftli(a1, d), psftli(b1, d))*psftpc(psftli(a2, d), psftli(b2, d))*"
        + "psftpc(psftli(a3, d), psftli(b3, d))*psftpc(psftli(a4, d), psftli(b4, d)); psftcnt", //
        // f_(a__) takes one factor, never a group of them
        "24");
    // the rule still applies where its condition holds
    check("psftpc(psftli(a1, d), psftli(b1, d))*psftpc(psftli(b1, d), psftli(c1, d))", //
        "psftpc(psftli(a1,d),psftli(c1,d))");
  }

  /** The size limit of Factor counts the polynomial in large unknowns, not the unknowns. */
  @Test
  public void testFactorWithLargeUnknowns() {
    check("psftd1 = Det(Array(pp(g(a(#1)), g(b(#2))) &, {3, 3})); "
        + "psftd2 = Det(Array(pp(g(c(#1)), g(d(#2))) &, {3, 3})); "
        + "psfte = Expand(psftd1*psftd2); {LeafCount(psfte) > 1000, Head(Factor(psfte)), "
        + "Expand(Factor(psfte) - psfte)}", //
        "{True,Times,0}");
  }

  /**
   * The name in OptionValue(name) is held: an option stored as psftalias -> value is found after
   * psftalias got a value, and a given psftalias -> value (which evaluates to psftlong -> value)
   * still comes before the default.
   */
  @Test
  public void testOptionValueOfAnAlias() {
    check("Options(psftov) = {psftalias -> False}; psftalias = psftlong; "
        + "psftov(opts : OptionsPattern()) := OptionValue(psftalias); "
        + "{psftov(), psftov(psftalias -> True)}", //
        "{False,True}");
  }
}
