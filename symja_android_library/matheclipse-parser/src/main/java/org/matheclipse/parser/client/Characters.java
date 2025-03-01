package org.matheclipse.parser.client;

import java.util.Map;
import org.matheclipse.parser.trie.TrieMatch;

public class Characters {
  public final static Map<String, String> NamedCharactersMap =
      ParserConfig.TRIE_STRING2STRING_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  private final static Map<String, String> ReversedNamedCharactersMap =
      ParserConfig.TRIE_STRING2STRING_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  public final static Map<String, String> CharacterNamesMap =
      ParserConfig.TRIE_STRING2STRING_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  /**
   * See:
   * <a href="https://github.com/mathics/Mathics/blob/master/mathics/core/characters.py">Mathics
   * characters.py</a>
   */
  private static final String[] NamedCharacters = {"AAcute", "\u00E1", "ABar", "\u0101", "ACup",
      "\u0103", "ADoubleDot", "\u00E4", "AE", "\u00E6", "AGrave", "\u00E0", "AHat", "\u00E2",
      "ARing", "\u00E5", "ATilde", "\u00E3", "Akuz", "\uF728", "Aleph", "\u2135", "AliasDelimiter",
      "\uF764", "AliasIndicator", "\uF768", "AlignmentMarker", "\uF760", "Alpha", "\u03B1",
      "AltKey", "\uF7D1", "And", "\u2227", "Andy", "\uF729", "Angle", "\u2220", "Angstrom",
      "\u212B", "AquariusSign", "\u2652", "AriesSign", "\u2648", "AscendingEllipsis", "\u22F0",
      "AutoLeftMatch", "\uF3A8", "AutoOperand", "\uF3AE", "AutoPlaceholder", "\uF3A4",
      "AutoRightMatch", "\uF3A9", "AutoSpace", "\uF3AD", "Backslash", "\u2216", "BeamedEighthNote",
      "\u266B", "BeamedSixteenthNote", "\u266C", "Because", "\u2235", "Bet", "\u2136", "Beta",
      "\u03B2", "BlackBishop", "\u265D", "BlackKing", "\u265A", "BlackKnight", "\u265E",
      "BlackPawn", "\u265F", "BlackQueen", "\u265B", "BlackRook", "\u265C", "Breve", "\u02D8",
      "Bullet", "\u2022", "CAcute", "\u0107", "CCedilla", "\u00E7", "CHacek", "\u010D",
      "COMPATIBILITYKanjiSpace", "\u3000", "COMPATIBILITYNoBreak", "\uF3A2", "CancerSign", "\u264B",
      "Cap", "\u2322", "CapitalAAcute", "\u00C1", "CapitalABar", "\u0100", "CapitalACup", "\u0102",
      "CapitalADoubleDot", "\u00C4", "CapitalAE", "\u00C6", "CapitalAGrave", "\u00C0",
      "CapitalAHat", "\u00C2", "CapitalARing", "\u00C5", "CapitalATilde", "\u00C3", "CapitalAlpha",
      "\u0391", "CapitalBeta", "\u0392", "CapitalCAcute", "\u0106", "CapitalCCedilla", "\u00C7",
      "CapitalCHacek", "\u010C", "CapitalChi", "\u03A7", "CapitalDHacek", "\u010E", "CapitalDelta",
      "\u0394", "CapitalDifferentialD", "\uF74B", "CapitalDigamma", "\u03DC", "CapitalEAcute",
      "\u00C9", "CapitalEBar", "\u0112", "CapitalECup", "\u0114", "CapitalEDoubleDot", "\u00CB",
      "CapitalEGrave", "\u00C8", "CapitalEHacek", "\u011A", "CapitalEHat", "\u00CA",
      "CapitalEpsilon", "\u0395", "CapitalEta", "\u0397", "CapitalEth", "\u00D0", "CapitalGamma",
      "\u0393", "CapitalIAcute", "\u00CD", "CapitalICup", "\u012C", "CapitalIDoubleDot", "\u00CF",
      "CapitalIGrave", "\u00CC", "CapitalIHat", "\u00CE", "CapitalIota", "\u0399", "CapitalKappa",
      "\u039A", "CapitalKoppa", "\u03DE", "CapitalLSlash", "\u0141", "CapitalLambda", "\u039B",
      "CapitalMu", "\u039C", "CapitalNHacek", "\u0147", "CapitalNTilde", "\u00D1", "CapitalNu",
      "\u039D", "CapitalOAcute", "\u00D3", "CapitalODoubleAcute", "\u0150", "CapitalODoubleDot",
      "\u00D6", "CapitalOE", "\u0152", "CapitalOGrave", "\u00D2", "CapitalOHat", "\u00D4",
      "CapitalOSlash", "\u00D8", "CapitalOTilde", "\u00D5", "CapitalOmega", "\u03A9",
      "CapitalOmicron", "\u039F", "CapitalPhi", "\u03A6", "CapitalPi", "\u03A0", "CapitalPsi",
      "\u03A8", "CapitalRHacek", "\u0158", "CapitalRho", "\u03A1", "CapitalSHacek", "\u0160",
      "CapitalSampi", "\u03E0", "CapitalSigma", "\u03A3", "CapitalStigma", "\u03DA",
      "CapitalTHacek", "\u0164", "CapitalTau", "\u03A4", "CapitalTheta", "\u0398", "CapitalThorn",
      "\u00DE", "CapitalUAcute", "\u00DA", "CapitalUDoubleAcute", "\u0170", "CapitalUDoubleDot",
      "\u00DC", "CapitalUGrave", "\u00D9", "CapitalUHat", "\u00DB", "CapitalURing", "\u016E",
      "CapitalUpsilon", "\u03A5", "CapitalXi", "\u039E", "CapitalYAcute", "\u00DD", "CapitalZHacek",
      "\u017D", "CapitalZeta", "\u0396", "CapricornSign", "\u2651", "Cedilla", "\u00B8", "Cent",
      "\u00A2", "CenterDot", "\u00B7", "CenterEllipsis", "\u22EF", "CheckedBox", "\u2612",
      "Checkmark", "\u2713", "CheckedBox", "\u2612", "Chi", "\u03C7", "CircleDot", "\u2299",
      "CircleMinus", "\u2296", "CirclePlus", "\u2295", "CircleTimes", "\u2297",
      "ClockwiseContourIntegral", "\u2232", "CloseCurlyDoubleQuote", "\u201D", "CloseCurlyQuote",
      "\u2019", "CloverLeaf", "\u2318", "ClubSuit", "\u2663", "Colon", "\u2236", "CommandKey",
      "\uF76A", "Conditioned", "\uF3D3", "Congruent", "\u2261", "Conjugate", "\uF3C8",
      "ConjugateTranspose", "\uF3C9", "ConstantC", "\uF7DA", "Continuation", "\uF3B1",
      "ContinuedFractionK", "\uF3D9", "ContourIntegral", "\u222E", "ControlKey", "\uF763",
      "Coproduct", "\u2210", "Copyright", "\u00A9", "CounterClockwiseContourIntegral", "\u2233",
      "Cross", "\uF4A0", "CubeRoot", "\u221B", "CupCap", "\u224D", "Cup", "\u2323", "Curl",
      "\uF3D8", "CurlyCapitalUpsilon", "\u03D2", "CurlyEpsilon", "\u03B5", "CurlyKappa", "\u03F0",
      "CurlyPhi", "\u03C6", "CurlyPi", "\u03D6", "CurlyRho", "\u03F1", "CurlyTheta", "\u03D1",
      "Currency", "\u00A4", "DHacek", "\u010F", "Dagger", "\u2020", "Dalet", "\u2138", "Dash",
      "\u2013", "Degree", "\u00B0", "Del", "\u2207", "DeleteKey", "\uF7D0", "Delta", "\u03B4",
      "DescendingEllipsis", "\u22F1", "Diameter", "\u2300", "Diamond", "\u22C4", "DiamondSuit",
      "\u2662", "DifferenceDelta", "\u2206", "DifferentialD", "\uF74C", "Digamma", "\u03DD",
      "DirectedEdge", "\uF3D5", "DiscreteRatio", "\uF4A4", "DiscreteShift", "\uF4A3",
      "DiscretionaryHyphen", "\u00AD", "DiscretionaryLineSeparator", "\uF76E",
      "DiscretionaryPageBreakAbove", "\uF3BF", "DiscretionaryPageBreakBelow", "\uF3C6",
      "DiscretionaryParagraphSeparator", "\uF76F", "Distributed", "\uF3D2", "Divergence", "\uF3D7",
      "Divide", "\u00F7", "Divides", "\u2223", "DivisionSlash", "\u2215", "DotEqual", "\u2250",
      "DotlessI", "\u0131", "DotlessJ", "\uF700", "DottedSquare", "\uF751", "DoubleContourIntegral",
      "\u222F", "DoubleDagger", "\u2021", "DoubleDot", "\u00A8", "DoubleDownArrow", "\u21D3",
      "DoubleLeftArrow", "\u21D0", "DoubleLeftRightArrow", "\u21D4", "DoubleLeftTee", "\u2AE4",
      "DoubleLongLeftArrow", "\u27F8", "DoubleLongLeftRightArrow", "\u27FA", "DoubleLongRightArrow",
      "\u27F9", "DoublePrime", "\u2033", "DoubleRightArrow", "\u21D2", "DoubleRightTee", "\u22A8",
      "DoubleStruckA", "\uF6E6", "DoubleStruckB", "\uF6E7", "DoubleStruckC", "\uF6E8",
      "DoubleStruckCapitalA", "\uF7A4", "DoubleStruckCapitalB", "\uF7A5", "DoubleStruckCapitalC",
      "\uF7A6", "DoubleStruckCapitalD", "\uF7A7", "DoubleStruckCapitalE", "\uF7A8",
      "DoubleStruckCapitalF", "\uF7A9", "DoubleStruckCapitalG", "\uF7AA", "DoubleStruckCapitalH",
      "\uF7AB", "DoubleStruckCapitalI", "\uF7AC", "DoubleStruckCapitalJ", "\uF7AD",
      "DoubleStruckCapitalK", "\uF7AE", "DoubleStruckCapitalL", "\uF7AF", "DoubleStruckCapitalM",
      "\uF7B0", "DoubleStruckCapitalN", "\uF7B1", "DoubleStruckCapitalO", "\uF7B2",
      "DoubleStruckCapitalP", "\uF7B3", "DoubleStruckCapitalQ", "\uF7B4", "DoubleStruckCapitalR",
      "\uF7B5", "DoubleStruckCapitalS", "\uF7B6", "DoubleStruckCapitalT", "\uF7B7",
      "DoubleStruckCapitalU", "\uF7B8", "DoubleStruckCapitalV", "\uF7B9", "DoubleStruckCapitalW",
      "\uF7BA", "DoubleStruckCapitalX", "\uF7BB", "DoubleStruckCapitalY", "\uF7BC",
      "DoubleStruckCapitalZ", "\uF7BD", "DoubleStruckD", "\uF6E9", "DoubleStruckE", "\uF6EA",
      "DoubleStruckEight", "\uF7E3", "DoubleStruckF", "\uF6EB", "DoubleStruckFive", "\uF7E0",
      "DoubleStruckFour", "\uF7DF", "DoubleStruckG", "\uF6EC", "DoubleStruckH", "\uF6ED",
      "DoubleStruckI", "\uF6EE", "DoubleStruckJ", "\uF6EF", "DoubleStruckK", "\uF6F0",
      "DoubleStruckL", "\uF6F1", "DoubleStruckM", "\uF6F2", "DoubleStruckN", "\uF6F3",
      "DoubleStruckNine", "\uF7E4", "DoubleStruckO", "\uF6F4", "DoubleStruckOne", "\uF7DC",
      "DoubleStruckP", "\uF6F5", "DoubleStruckQ", "\uF6F6", "DoubleStruckR", "\uF6F7",
      "DoubleStruckS", "\uF6F8", "DoubleStruckSeven", "\uF7E2", "DoubleStruckSix", "\uF7E1",
      "DoubleStruckT", "\uF6F9", "DoubleStruckThree", "\uF7DE", "DoubleStruckTwo", "\uF7DD",
      "DoubleStruckU", "\uF6FA", "DoubleStruckV", "\uF6FB", "DoubleStruckW", "\uF6FC",
      "DoubleStruckX", "\uF6FD", "DoubleStruckY", "\uF6FE", "DoubleStruckZ", "\uF6FF",
      "DoubleStruckZero", "\uF7DB", "DoubleUpArrow", "\u21D1", "DoubleUpDownArrow", "\u21D5",
      "DoubleVerticalBar", "\u2225", "DoubledGamma", "\uF74A", "DoubledPi", "\uF749", "DownArrow",
      "\u2193", "DownArrowBar", "\u2913", "DownArrowUpArrow", "\u21F5", "DownBreve", "\uF755",
      "DownExclamation", "\u00A1", "DownLeftRightVector", "\u2950", "DownLeftTeeVector", "\u295E",
      "DownLeftVector", "\u21BD", "DownLeftVectorBar", "\u2956", "DownPointer", "\u25BE",
      "DownQuestion", "\u00BF", "DownRightTeeVector", "\u295F", "DownRightVector", "\u21C1",
      "DownRightVectorBar", "\u2957", "DownTee", "\u22A4", "DownTeeArrow", "\u21A7", "EAcute",
      "\u00E9", "EBar", "\u0113", "ECup", "\u0115", "EDoubleDot", "\u00EB", "EGrave", "\u00E8",
      "EHacek", "\u011B", "EHat", "\u00EA", "Earth", "\u2641", "EighthNote", "\u266A", "Element",
      "\u2208", "Ellipsis", "\u2026", "EmptyCircle", "\u25CB", "EmptyDiamond", "\u25C7",
      "EmptyDownTriangle", "\u25BD", "EmptyRectangle", "\u25AF", "EmptySet", "\u2205",
      "EmptySmallCircle", "\u25E6", "EmptySmallSquare", "\u25FB", "EmptySquare", "\u25A1",
      "EmptyUpTriangle", "\u25B3", "EmptyVerySmallSquare", "\u25AB", "EnterKey", "\uF7D4",
      "EntityEnd", "\uF3B9", "EntityStart", "\uF3B8", "Epsilon", "\u03F5", "Equal", "\uF431",
      "EqualTilde", "\u2242", "Equilibrium", "\u21CC", "Equivalent", "\u29E6", "ErrorIndicator",
      "\uF767", "EscapeKey", "\uF769", "Eta", "\u03B7", "Eth", "\u00F0", "Euro", "\u20AC", "Exists",
      "\u2203", "ExpectationE", "\uF3DD", "ExponentialE", "\uF74D", "FiLigature", "\uFB01",
      "FilledCircle", "\u25CF", "FilledDiamond", "\u25C6", "FilledDownTriangle", "\u25BC",
      "FilledLeftTriangle", "\u25C0", "FilledRectangle", "\u25AE", "FilledRightTriangle", "\u25B6",
      "FilledSmallCircle", "\uF750", "FilledSmallSquare", "\u25FC", "FilledSquare", "\u25A0",
      "FilledUpTriangle", "\u25B2", "FilledVerySmallSquare", "\u25AA", "FinalSigma", "\u03C2",
      "FirstPage", "\uF7FA", "FivePointedStar", "\u2605", "FlLigature", "\uFB02", "Flat", "\u266D",
      "Florin", "\u0192", "ForAll", "\u2200", "FormalA", "\uF800", "FormalAlpha", "\uF854",
      "FormalB", "\uF801", "FormalBeta", "\uF855", "FormalC", "\uF802", "FormalCapitalA", "\uF81A",
      "FormalCapitalAlpha", "\uF834", "FormalCapitalB", "\uF81B", "FormalCapitalBeta", "\uF835",
      "FormalCapitalC", "\uF81C", "FormalCapitalChi", "\uF84A", "FormalCapitalD", "\uF81D",
      "FormalCapitalDelta", "\uF837", "FormalCapitalDigamma", "\uF87F", "FormalCapitalE", "\uF81E",
      "FormalCapitalEpsilon", "\uF838", "FormalCapitalEta", "\uF83A", "FormalCapitalF", "\uF81F",
      "FormalCapitalG", "\uF820", "FormalCapitalGamma", "\uF836", "FormalCapitalH", "\uF821",
      "FormalCapitalI", "\uF822", "FormalCapitalIota", "\uF83C", "FormalCapitalJ", "\uF823",
      "FormalCapitalK", "\uF824", "FormalCapitalKappa", "\uF83D", "FormalCapitalKoppa", "\uF881",
      "FormalCapitalL", "\uF825", "FormalCapitalLambda", "\uF83E", "FormalCapitalM", "\uF826",
      "FormalCapitalMu", "\uF83F", "FormalCapitalN", "\uF827", "FormalCapitalNu", "\uF840",
      "FormalCapitalO", "\uF828", "FormalCapitalOmega", "\uF84C", "FormalCapitalOmicron", "\uF842",
      "FormalCapitalP", "\uF829", "FormalCapitalPhi", "\uF849", "FormalCapitalPi", "\uF843",
      "FormalCapitalPsi", "\uF84B", "FormalCapitalQ", "\uF82A", "FormalCapitalR", "\uF82B",
      "FormalCapitalRho", "\uF844", "FormalCapitalS", "\uF82C", "FormalCapitalSampi", "\uF883",
      "FormalCapitalSigma,", "\uF846", "FormalCapitalStigma", "\uF87D", "FormalCapitalT", "\uF82D",
      "FormalCapitalTau", "\uF847", "FormalCapitalTheta", "\uF83B", "FormalCapitalU", "\uF82E",
      "FormalCapitalUpsilon", "\uF848", "FormalCapitalV", "\uF82F", "FormalCapitalW", "\uF830",
      "FormalCapitalX", "\uF831", "FormalCapitalXi", "\uF841", "FormalCapitalY", "\uF832",
      "FormalCapitalZ", "\uF833", "FormalCapitalZeta", "\uF839", "FormalChi", "\uF86A",
      "FormalCurlyCapitalUpsilon", "\uF875", "FormalCurlyEpsilon", "\uF858", "FormalCurlyKappa",
      "\uF885", "FormalCurlyPhi", "\uF869", "FormalCurlyPi", "\uF879", "FormalCurlyRho", "\uF886",
      "FormalCurlyTheta", "\uF874", "FormalD", "\uF803", "FormalDelta", "\uF857", "FormalDigamma",
      "\uF880", "FormalE", "\uF804", "FormalEpsilon", "\uF88A", "FormalEta", "\uF85A", "FormalF",
      "\uF805", "FormalFinalSigma", "\uF865", "FormalG", "\uF806", "FormalGamma", "\uF856",
      "FormalH", "\uF807", "FormalI", "\uF808", "FormalIota", "\uF85C", "FormalJ", "\uF809",
      "FormalK", "\uF80A", "FormalKappa", "\uF85D", "FormalKoppa", "\uF882", "FormalL", "\uF80B",
      "FormalLambda", "\uF85E", "FormalM", "\uF80C", "FormalMu", "\uF85F", "FormalN", "\uF80D",
      "FormalNu", "\uF860", "FormalO", "\uF80E", "FormalOmega", "\uF86C", "FormalOmicron", "\uF862",
      "FormalP", "\uF80F", "FormalPhi", "\uF878", "FormalPi", "\uF863", "FormalPsi", "\uF86B",
      "FormalQ", "\uF810", "FormalR", "\uF811", "FormalRho", "\uF864", "FormalS", "\uF812",
      "FormalSampi", "\uF884", "FormalSigma", "\uF866", "FormalStigma", "\uF87E", "FormalT",
      "\uF813", "FormalTau", "\uF867", "FormalTheta", "\uF85B", "FormalU", "\uF814",
      "FormalUpsilon", "\uF868", "FormalV", "\uF815", "FormalW", "\uF816", "FormalX", "\uF817",
      "FormalXi", "\uF861", "FormalY", "\uF818", "FormalZ", "\uF819", "FormalZeta", "\uF859",
      "FreakedSmiley", "\uF721", "FreeformPrompt", "\uF351", "Function", "\uF4A1", "Gamma",
      "\u03B3", "GeminiSign", "\u264A", "Gimel", "\u2137", "GothicA", "\uF6CC", "GothicB", "\uF6CD",
      "GothicC", "\uF6CE", "GothicCapitalA", "\uF78A", "GothicCapitalB", "\uF78B", "GothicCapitalC",
      "\u212D", "GothicCapitalD", "\uF78D", "GothicCapitalE", "\uF78E", "GothicCapitalF", "\uF78F",
      "GothicCapitalG", "\uF790", "GothicCapitalH", "\u210C", "GothicCapitalI", "\u2111",
      "GothicCapitalJ", "\uF793", "GothicCapitalK", "\uF794", "GothicCapitalL", "\uF795",
      "GothicCapitalM", "\uF796", "GothicCapitalN", "\uF797", "GothicCapitalO", "\uF798",
      "GothicCapitalP", "\uF799", "GothicCapitalQ", "\uF79A", "GothicCapitalR", "\u211C",
      "GothicCapitalS", "\uF79C", "GothicCapitalT", "\uF79D", "GothicCapitalU", "\uF79E",
      "GothicCapitalV", "\uF79F", "GothicCapitalW", "\uF7A0", "GothicCapitalX", "\uF7A1",
      "GothicCapitalY", "\uF7A2", "GothicCapitalZ", "\u2128", "GothicD", "\uF6CF", "GothicE",
      "\uF6D0", "GothicEight", "\uF7ED", "GothicF", "\uF6D1", "GothicFive", "\uF7EA", "GothicFour",
      "\uF7E9", "GothicG", "\uF6D2", "GothicH", "\uF6D3", "GothicI", "\uF6D4", "GothicJ", "\uF6D5",
      "GothicK", "\uF6D6", "GothicL", "\uF6D7", "GothicM", "\uF6D8", "GothicN", "\uF6D9",
      "GothicNine", "\uF7EF", "GothicO", "\uF6DA", "GothicOne", "\uF7E6", "GothicP", "\uF6DB",
      "GothicQ", "\uF6DC", "GothicR", "\uF6DD", "GothicS", "\uF6DE", "GothicSeven", "\uF7EC",
      "GothicSix", "\uF7EB", "GothicT", "\uF6DF", "GothicThree", "\uF7E8", "GothicTwo", "\uF7E7",
      "GothicU", "\uF6E0", "GothicV", "\uF6E1", "GothicW", "\uF6E2", "GothicX", "\uF6E3", "GothicY",
      "\uF6E4", "GothicZ", "\uF6E5", "GothicZero", "\uF7E5", "Gradient", "\uF3D6", "GrayCircle",
      "\uF753", "GraySquare", "\uF752", "GreaterEqual", "\u2265", "GreaterEqualLess", "\u22DB",
      "GreaterFullEqual", "\u2267", "GreaterGreater", "\u226B", "GreaterLess", "\u2277",
      "GreaterSlantEqual", "\u2A7E", "GreaterTilde", "\u2273", "HBar", "\u210F", "Hacek", "\u02C7",
      "HappySmiley", "\u263A", "HeartSuit", "\u2661", "HermitianConjugate", "\uF3CE",
      "HorizontalLine", "\u2500", "HumpDownHump", "\u224E", "HumpEqual", "\u224F", "Hyphen",
      "\u2010", "IAcute", "\u00ED", "ICup", "\u012D", "IDoubleDot", "\u00EF", "IGrave", "\u00EC",
      "IHat", "\u00EE", "ImaginaryI", "\uF74E", "ImaginaryJ", "\uF74F", "ImplicitPlus", "\uF39E",
      "Implies", "\uF523", "IndentingNewLine", "\uF3A3", "Infinity", "\u221E", "InlinePart",
      "\uF51E", "Integral", "\u222B", "Intersection", "\u22C2", "InvisibleApplication", "\uF76D",
      "InvisibleComma", "\uF765", "InvisiblePostfixScriptBase", "\uF3B4",
      "InvisiblePrefixScriptBase", "\uF3B3", "InvisibleSpace", "\uF360", "InvisibleTimes", "\u2062",
      "Iota", "\u03B9", "Jupiter", "\u2643", "Kappa", "\u03BA", "KernelIcon", "\uF756", "KeyBar",
      "\uF7D3", "Koppa", "\u03DF", "LSlash", "\u0142", "Lambda", "\u03BB", "Laplacian", "\u2206",
      "LastPage", "\uF7FB", "LeftAngleBracket", "\u2329", "LeftArrow", "\u2190", "LeftArrowBar",
      "\u21E4", "LeftArrowRightArrow", "\u21C6", "LeftAssociation", "\uF113", "LeftBracketingBar",
      "\uF603", "LeftCeiling", "\u2308", "LeftDoubleBracket", "\u301A", "LeftDoubleBracketingBar",
      "\uF605", "LeftDownTeeVector", "\u2961", "LeftDownVector", "\u21C3", "LeftDownVectorBar",
      "\u2959", "LeftFloor", "\u230A", "LeftGuillemet", "\u00AB", "LeftModified", "\uF76B",
      "LeftPointer", "\u25C2", "LeftRightArrow", "\u2194", "LeftRightVector", "\u294E",
      "LeftSkeleton", "\uF761", "LeftTee", "\u22A3", "LeftTeeArrow", "\u21A4", "LeftTeeVector",
      "\u295A", "LeftTriangle", "\u22B2", "LeftTriangleBar", "\u29CF", "LeftTriangleEqual",
      "\u22B4", "LeftUpDownVector", "\u2951", "LeftUpTeeVector", "\u2960", "LeftUpVector", "\u21BF",
      "LeftUpVectorBar", "\u2958", "LeftVector", "\u21BC", "LeftVectorBar", "\u2952", "LeoSign",
      "\u264C", "LessEqual", "\u2264", "LessEqualGreater", "\u22DA", "LessFullEqual", "\u2266",
      "LessGreater", "\u2276", "LessLess", "\u226A", "LessSlantEqual", "\u2A7D", "LessTilde",
      "\u2272", "LetterSpace", "\uF754", "LibraSign", "\u264E", "LightBulb", "\uF723", "Limit",
      "\uF438", "LineSeparator", "\u2028", "LongDash", "\u2014", "LongEqual", "\uF7D9",
      "LongLeftArrow", "\u27F5", "LongLeftRightArrow", "\u27F7", "LongRightArrow", "\u27F6",
      "LowerLeftArrow", "\u2199", "LowerRightArrow", "\u2198", "Mars", "\u2642", "MathematicaIcon",
      "\uF757", "MaxLimit", "\uF439", "MeasuredAngle", "\u2221", "MediumSpace", "\u205F", "Mercury",
      "\u263F", "Mho", "\u2127", "Micro", "\u00B5", "MinLimit", "\uF43A", "Minus", "\u2212",
      "MinusPlus", "\u2213", "Mod1Key", "\uF7D6", "Mod2Key", "\uF7D7", "Moon", "\u263E", "Mu",
      "\u03BC", "NHacek", "\u0148", "NTilde", "\u00F1", "Nand", "\u22BC", "Natural", "\u266E",
      "NegativeMediumSpace", "\uF383", "NegativeThickSpace", "\uF384", "NegativeThinSpace",
      "\uF382", "NegativeVeryThinSpace", "\uF380", "Neptune", "\u2646", "NestedGreaterGreater",
      "\u2AA2", "NestedLessLess", "\u2AA1", "NeutralSmiley", "\uF722", "NewLine", "\n", "NoBreak",
      "\u2060", "NonBreakingSpace", "\u00A0", "Nor", "\u22BD", "Not", "\u00AC", "NotCongruent",
      "\u2262", "NotCupCap", "\u226D", "NotDoubleVerticalBar", "\u2226", "NotElement", "\u2209",
      "NotEqual", "\u2260", "NotEqualTilde", "\uF400", "NotExists", "\u2204", "NotGreater",
      "\u226F", "NotGreaterEqual", "\u2271", "NotGreaterFullEqual", "\u2269", "NotGreaterGreater",
      "\uF427", "NotGreaterLess", "\u2279", "NotGreaterSlantEqual", "\uF429", "NotGreaterTilde",
      "\u2275", "NotHumpDownHump", "\uF402", "NotHumpEqual", "\uF401", "NotLeftTriangle", "\u22EA",
      "NotLeftTriangleBar", "\uF412", "NotLeftTriangleEqual", "\u22EC", "NotLess", "\u226E",
      "NotLessEqual", "\u2270", "NotLessFullEqual", "\u2268", "NotLessGreater", "\u2278",
      "NotLessLess", "\uF422", "NotLessSlantEqual", "\uF424", "NotLessTilde", "\u2274",
      "NotNestedGreaterGreater", "\uF428", "NotNestedLessLess", "\uF423", "NotPrecedes", "\u2280",
      "NotPrecedesEqual", "\uF42B", "NotPrecedesSlantEqual", "\u22E0", "NotPrecedesTilde", "\u22E8",
      "NotReverseElement", "\u220C", "NotRightTriangle", "\u22EB", "NotRightTriangleBar", "\uF413",
      "NotRightTriangleEqual", "\u22ED", "NotSquareSubset", "\uF42E", "NotSquareSubsetEqual",
      "\u22E2", "NotSquareSuperset", "\uF42F", "NotSquareSupersetEqual", "\u22E3", "NotSubset",
      "\u2284", "NotSubsetEqual", "\u2288", "NotSucceeds", "\u2281", "NotSucceedsEqual", "\uF42D",
      "NotSucceedsSlantEqual", "\u22E1", "NotSucceedsTilde", "\u22E9", "NotSuperset", "\u2285",
      "NotSupersetEqual", "\u2289", "NotTilde", "\u2241", "NotTildeEqual", "\u2244",
      "NotTildeFullEqual", "\u2247", "NotTildeTilde", "\u2249", "NotVerticalBar", "\u2224", "Nu",
      "\u03BD", "Null", "\uF3A0", "NumberSign", "\uF724", "OAcute", "\u00F3", "ODoubleAcute",
      "\u0151", "ODoubleDot", "\u00F6", "OE", "\u0153", "OGrave", "\u00F2", "OHat", "\u00F4",
      "OSlash", "\u00F8", "OTilde", "\u00F5", "Omega", "\u03C9", "Omicron", "\u03BF",
      "OpenCurlyDoubleQuote", "\u201C", "OpenCurlyQuote", "\u2018", "OptionKey", "\uF7D2", "Or",
      "\u2228", "OverBrace", "\uFE37", "OverBracket", "\u23B4", "OverParenthesis", "\uFE35",
      "PageBreakAbove", "\uF3BD", "PageBreakBelow", "\uF3BE", "Paragraph", "\u00B6",
      "ParagraphSeparator", "\u2029", "PartialD", "\u2202", "PermutationProduct", "\uF3DE",
      "Perpendicular", "\u27C2", "Phi", "\u03D5", "Pi", "\u03C0", "Piecewise", "\uF361",
      "PiscesSign", "\u2653", "Placeholder", "\uF528", "PlusMinus", "\u00B1", "Pluto", "\u2647",
      "Precedes", "\u227A", "PrecedesEqual", "\u2AAF", "PrecedesSlantEqual", "\u227C",
      "PrecedesTilde", "\u227E", "Prime", "\u2032", "ProbabilityPr", "\uF3DC", "Product", "\u220F",
      "Proportion", "\u2237", "Proportional", "\u221D", "Psi", "\u03C8", "QuarterNote", "\u2669",
      "RHacek", "\u0159", "RawAmpersand", "\u0026", "RawAt", "\u0040", "RawBackquote", "\u0060",
      "RawBackslash", "\\", "RawColon", "\u003A", "RawComma", "\u002C", "RawDash", "\u002D",
      "RawDollar", "\u0024", "RawDot", "\u002E", "RawDoubleQuote", "\"", "RawEqual", "\u003D",
      "RawEscape", "\u001B", "RawExclamation", "\u0021", "RawGreater", "\u003E", "RawLeftBrace",
      "\u007B", "RawLeftBracket", "\u005B", "RawLeftParenthesis", "\u0028", "RawLess", "\u003C",
      "RawNumberSign", "\u0023", "RawPercent", "\u0025", "RawPlus", "\u002B", "RawQuestion",
      "\u003F", "RawQuote", "\u0027", "RawReturn", "\r", "RawRightBrace", "\u007D",
      "RawRightBracket", "\u005D", "RawRightParenthesis", "\u0029", "RawSemicolon", "\u003B",
      "RawSlash", "\u002F", "RawSpace", "\u0020", "RawStar", "\u002A", "RawTab", "\u0009",
      "RawTilde", "\u007E", "RawUnderscore", "\u005F", "RawVerticalBar", "\u007C", "RawWedge",
      "\u005E", "RegisteredTrademark", "\u00AE", "ReturnIndicator", "\u21B5", "ReturnKey", "\uF766",
      "ReverseDoublePrime", "\u2036", "ReverseElement", "\u220B", "ReverseEquilibrium", "\u21CB",
      "ReversePrime", "\u2035", "ReverseUpEquilibrium", "\u296F", "Rho", "\u03C1", "RightAngle",
      "\u221F", "RightAngleBracket", "\u232A", "RightArrow", "\u2192", "RightArrowBar", "\u21E5",
      "RightArrowLeftArrow", "\u21C4", "RightBracketingBar", "\uF604", "RightCeiling", "\u2309",
      "RightDoubleBracket", "\u301B", "RightDoubleBracketingBar", "\uF606", "RightDownTeeVector",
      "\u295D", "RightDownVector", "\u21C2", "RightDownVectorBar", "\u2955", "RightFloor", "\u230B",
      "RightGuillemet", "\u00BB", "RightModified", "\uF76C", "RightPointer", "\u25B8",
      "RightSkeleton", "\uF762", "RightTee", "\u22A2", "RightTeeArrow", "\u21A6", "RightTeeVector",
      "\u295B", "RightTriangle", "\u22B3", "RightTriangleBar", "\u29D0", "RightTriangleEqual",
      "\u22B5", "RightUpDownVector", "\u294F", "RightUpTeeVector", "\u295C", "RightUpVector",
      "\u21BE", "RightUpVectorBar", "\u2954", "RightVector", "\u21C0", "RightVectorBar", "\u2953",
      "RoundImplies", "\u2970", "RoundSpaceIndicator", "\uF3B2", "Rule", "\uF522", "RuleDelayed",
      "\uF51F", "SHacek", "\u0161", "SZ", "\u00DF", "SadSmiley", "\u2639", "SagittariusSign",
      "\u2650", "Sampi", "\u03E0", "Saturn", "\u2644", "ScorpioSign", "\u264F", "ScriptA", "\uF6B2",
      "ScriptB", "\uF6B3", "ScriptC", "\uF6B4", "ScriptCapitalA", "\uF770", "ScriptCapitalB",
      "\u212C", "ScriptCapitalC", "\uF772", "ScriptCapitalD", "\uF773", "ScriptCapitalE", "\u2130",
      "ScriptCapitalF", "\u2131", "ScriptCapitalG", "\uF776", "ScriptCapitalH", "\u210B",
      "ScriptCapitalI", "\u2110", "ScriptCapitalJ", "\uF779", "ScriptCapitalK", "\uF77A",
      "ScriptCapitalL", "\u2112", "ScriptCapitalM", "\u2133", "ScriptCapitalN",
      new StringBuilder().appendCodePoint(0x01D4A9).toString(), // "\uF77D",
      "ScriptCapitalO", "\uF77E", "ScriptCapitalP", "\u2118", "ScriptCapitalQ", "\uF780",
      "ScriptCapitalR", "\u211B", "ScriptCapitalS", "\uF782", "ScriptCapitalT", "\uF783",
      "ScriptCapitalU", "\uF784", "ScriptCapitalV", "\uF785", "ScriptCapitalW", "\uF786",
      "ScriptCapitalX", "\uF787", "ScriptCapitalY", "\uF788", "ScriptCapitalZ", "\uF789", "ScriptD",
      "\uF6B5", "ScriptDotlessI", "\uF730", "ScriptDotlessJ", "\uF731", "ScriptE", "\u212F",
      "ScriptEight", "\uF7F8", "ScriptF", "\uF6B7", "ScriptFive", "\uF7F5", "ScriptFour", "\uF7F4",
      "ScriptG", "\u210A", "ScriptH", "\uF6B9", "ScriptI", "\uF6BA", "ScriptJ", "\uF6BB", "ScriptK",
      "\uF6BC", "ScriptL", "\u2113", "ScriptM", "\uF6BE", "ScriptN", "\uF6BF", "ScriptNine",
      "\uF7F9", "ScriptO", "\u2134", "ScriptOne", "\uF7F1", "ScriptP", "\uF6C1", "ScriptQ",
      "\uF6C2", "ScriptR", "\uF6C3", "ScriptS", "\uF6C4", "ScriptSeven", "\uF7F7", "ScriptSix",
      "\uF7F6", "ScriptT", "\uF6C5", "ScriptThree", "\uF7F3", "ScriptTwo", "\uF7F2", "ScriptU",
      "\uF6C6", "ScriptV", "\uF6C7", "ScriptW", "\uF6C8", "ScriptX", "\uF6C9", "ScriptY", "\uF6CA",
      "ScriptZ", "\uF6CB", "ScriptZero", "\uF7F0", "Section", "\u00A7", "SelectionPlaceholder",
      "\uF527", "Shah", "\uF11D", "Sharp", "\u266F", "ShiftKey", "\uF7D5", "ShortDownArrow",
      "\uF52B", "ShortLeftArrow", "\uF526", "ShortRightArrow", "\uF525", "Sigma", "\u03C3",
      "SixPointedStar", "\u2736", "SkeletonIndicator", "\u2043", "SmallCircle", "\u2218",
      "SpaceIndicator", "\u2423", "SpaceKey", "\uF7BF", "SpadeSuit", "\u2660", "SpanFromAbove",
      "\uF3BB", "SpanFromBoth", "\uF3BC", "SpanFromLeft", "\uF3BA", "SphericalAngle", "\u2222",
      "Sqrt", "\u221A", "Square", "\uF520", "SquareIntersection", "\u2293", "SquareSubset",
      "\u228F", "SquareSubsetEqual", "\u2291", "SquareSuperset", "\u2290", "SquareSupersetEqual",
      "\u2292", "SquareUnion", "\u2294", "Star", "\u22C6", "StepperDown", "\uF3CD", "StepperLeft",
      "\uF3CB", "StepperRight", "\uF3CA", "StepperUp", "\uF3CC", "Sterling", "\u00A3", "Stigma",
      "\u03DB", "Subset", "\u2282", "SubsetEqual", "\u2286", "Succeeds", "\u227B", "SucceedsEqual",
      "\u2AB0", "SucceedsSlantEqual", "\u227D", "SucceedsTilde", "\u227F", "SuchThat", "\u220D",
      "Sum", "\u2211", "Sun", "\u2609", "Superset", "\u2283", "SupersetEqual", "\u2287",
      "SystemEnterKey", "\uF75F", "SystemsModelDelay", "\uF3AF", "THacek", "\u0165", "TabKey",
      "\uF7BE", "Tau", "\u03C4", "TaurusSign", "\u2649", "TensorProduct", "\uF3DA", "TensorWedge",
      "\uF3DB", "Therefore", "\u2234", "Theta", "\u03B8", "ThickSpace", "\u2005", "ThinSpace",
      "\u2009", "Thorn", "\u00FE", "Tilde", "\u223C", "TildeEqual", "\u2243", "TildeFullEqual",
      "\u2245", "TildeTilde", "\u2248", "Times", "\u00D7", "Trademark", "\u2122", "Transpose",
      "\uF3C7", "TripleDot", "\uF758", "TwoWayRule", "\uF120", "UAcute", "\u00FA", "UDoubleAcute",
      "\u0171", "UDoubleDot", "\u00FC", "UGrave", "\u00F9", "UHat", "\u00FB", "URing", "\u016F",
      "UnderBrace", "\uFE38", "UnderBracket", "\u23B5", "UnderParenthesis", "\uFE36",
      "UndirectedEdge", "\uF3D4", "Union", "\u22C3", "UnionPlus", "\u228E", "UnknownGlyph",
      "\uFFFD", "UpArrow", "\u2191", "UpArrowBar", "\u2912", "UpArrowDownArrow", "\u21C5",
      "UpDownArrow", "\u2195", "UpEquilibrium", "\u296E", "UpPointer", "\u25B4", "UpTee", "\u22A5",
      "UpTeeArrow", "\u21A5", "UpperLeftArrow", "\u2196", "UpperRightArrow", "\u2197", "Upsilon",
      "\u03C5", "Uranus", "\u2645", "VectorGreaterEqual", "\uF435", "VectorGreater", "\uF434",
      "VectorLessEqual", "\uF437", "VectorLess", "\uF436", "Vee", "\u22C1", "Venus", "\u2640",
      "VerticalBar", "\u2223", "VerticalEllipsis", "\u22EE", "VerticalLine", "\u2502",
      "VerticalSeparator", "\uF432", "VerticalTilde", "\u2240", "VeryThinSpace", "\u200A", "Villa",
      "\uF727", "VirgoSign", "\u264D", "WarningSign", "\uF725", "WatchIcon", "\u231A", "Wedge",
      "\u22C0", "WeierstrassP", "\u2118", "WhiteBishop", "\u2657", "WhiteKing", "\u2654",
      "WhiteKnight", "\u2658", "WhitePawn", "\u2659", "WhiteQueen", "\u2655", "WhiteRook", "\u2656",
      "Wolf", "\uF720", "WolframAlphaPrompt", "\uF352", "WolframLanguageLogoCircle", "\uF11F",
      "WolframLanguageLogo", "\uF11E", "Xi", "\u03BE", "Xnor", "\uF4A2", "Xor", "\u22BB", "YAcute",
      "\u00FD", "YDoubleDot", "\u00FF", "Yen", "\u00A5", "ZHacek", "\u017E", "Zeta", "\u03B6",};

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      for (int i = 0; i < NamedCharacters.length; i += 2) {
        NamedCharactersMap.put(NamedCharacters[i], NamedCharacters[i + 1]);
      }
      CharacterNamesMap.put(NamedCharactersMap.get("Infinity"), "Infinity");
      CharacterNamesMap.put(NamedCharactersMap.get("ImaginaryI"), "I");
      CharacterNamesMap.put(NamedCharactersMap.get("ImaginaryJ"), "I");
      CharacterNamesMap.put(NamedCharactersMap.get("Pi"), "Pi");
      CharacterNamesMap.put(NamedCharactersMap.get("Degree"), "Degree");
      CharacterNamesMap.put(NamedCharactersMap.get("ExponentialE"), "E");
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  static {
    Characters.initialize();
  }

  /**
   * Return the name for a given unicode character.
   *
   * @param unicode a character
   * @return <code>null</code> if no corresponding name was found
   */
  public static String unicodeName(char unicode) {
    String str = String.valueOf(unicode);
    if (ReversedNamedCharactersMap.size() == 0) {
      // create unicode to name map
      for (int i = 0; i < NamedCharacters.length; i += 2) {
        ReversedNamedCharactersMap.put(NamedCharacters[i + 1], NamedCharacters[i]);
      }
    }
    return ReversedNamedCharactersMap.get(str);
  }

  public static String unicodePoint(int codePoint) {
    String str = new String(Character.toChars(codePoint));
    if (ReversedNamedCharactersMap.size() == 0) {
      // create unicode to name map
      for (int i = 0; i < NamedCharacters.length; i += 2) {
        ReversedNamedCharactersMap.put(NamedCharacters[i + 1], NamedCharacters[i]);
      }
    }
    return ReversedNamedCharactersMap.get(str);
  }

  /**
   * Substitute all named (unicode-) characters in a string with their unicode value For example
   * <code>\[Alpha], \[Phi], \[Pi]</code> will be replaced with unicode characters:
   *
   * <pre>
   * f(\[Alpha])+\[Phi]*\[Pi]
   * </pre>
   *
   * @param str
   * @return
   */
  public static String substituteCharacters(String str) {
    StringBuilder buf = null;
    char currentChar;
    int currentPosition = str.indexOf('\\');
    if (currentPosition < 0) {
      return str;
    }
    final int strLength = str.length();
    while (currentPosition < strLength) {
      currentChar = str.charAt(currentPosition++);
      if (currentChar == '\\') {
        if (currentPosition < strLength) {
          if (str.charAt(currentPosition) == '[') {
            final int startPosition = currentPosition++ - 1;
            if (currentPosition < strLength) {
              currentChar = str.charAt(currentPosition++);
            } else {
              break;
            }
            while (Character.isLetterOrDigit(currentChar)) {
              if (currentPosition < strLength) {
                currentChar = str.charAt(currentPosition++);
              } else {
                break;
              }
            }
            int endPosition = currentPosition;
            if (currentChar == ']') {
              String subString = str.substring(startPosition + 2, endPosition - 1);
              String namedCh = Characters.NamedCharactersMap.get(subString);
              if (namedCh != null) {
                buf = new StringBuilder(str.length());
                buf.append(str.substring(0, startPosition));
                currentChar = namedCh.charAt(0);
                buf.append(currentChar);
                break; // while (currentPosition < strLength)
              }
            }
          } else if (str.charAt(currentPosition) == '.') {
            try {
              final int numberOfUnicodeDigits = 2;
              buf = new StringBuilder(str.length());
              buf.append(str.substring(0, ++currentPosition - 2));
              currentPosition = codePointToUTF16(str, currentPosition, numberOfUnicodeDigits, buf);
            } catch (final NumberFormatException e) {
            }
            break;
          } else if (str.charAt(currentPosition) == ':') {
            try {
              final int numberOfUnicodeDigits = 4;
              buf = new StringBuilder(str.length());
              buf.append(str.substring(0, ++currentPosition - 2));
              currentPosition = codePointToUTF16(str, currentPosition, numberOfUnicodeDigits, buf);

            } catch (final NumberFormatException e) {
            }
            break;
          } else if (str.charAt(currentPosition) == '|') {
            try {
              final int numberOfUnicodeDigits = 6;
              buf = new StringBuilder(str.length());
              buf.append(str.substring(0, ++currentPosition - 2));
              currentPosition = codePointToUTF16(str, currentPosition, numberOfUnicodeDigits, buf);
              break;
            } catch (final NumberFormatException e) {
            }
          } else {
            // escape next character
            currentPosition++;
          }
        }
      }
    }
    if (buf == null) {
      // no special unicode character found, return original string
      return str;
    }

    while (currentPosition < strLength) {
      currentChar = str.charAt(currentPosition++);
      if (currentChar == '\\') {
        if (currentPosition < strLength) {
          if (str.charAt(currentPosition) == '[') {
            final int startPosition = currentPosition++ - 1;
            if (currentPosition < strLength) {
              currentChar = str.charAt(currentPosition++);
            } else {
              break;
            }
            while (Character.isLetterOrDigit(currentChar)) {
              if (currentPosition < strLength) {
                currentChar = str.charAt(currentPosition++);
              } else {
                break;
              }
            }
            int endPosition = currentPosition;
            if (currentChar == ']') {
              String subString = str.substring(startPosition + 2, endPosition - 1);
              String namedCh = Characters.NamedCharactersMap.get(subString);
              if (namedCh != null) {
                // currentChar = namedCh.charAt(0);
                buf.append(namedCh);
                continue; // while (currentPosition < strLength)
              }
              buf.append("\\[");
              buf.append(subString);

            } else {
              String subString = str.substring(startPosition, endPosition - 1);
              buf.append(subString);
            }
          } else if (str.charAt(currentPosition) == '.') {
            try {
              final int numberOfUnicodeDigits = 2;
              currentPosition =
                  codePointToUTF16(str, ++currentPosition, numberOfUnicodeDigits, buf);
              continue;
            } catch (final NumberFormatException e) {
            }
          } else if (str.charAt(currentPosition) == ':') {
            try {
              final int numberOfUnicodeDigits = 4;
              currentPosition =
                  codePointToUTF16(str, ++currentPosition, numberOfUnicodeDigits, buf);
              continue;
            } catch (final NumberFormatException e) {
            }
          } else if (str.charAt(currentPosition) == '|') {
            try {
              final int numberOfUnicodeDigits = 6;
              currentPosition =
                  codePointToUTF16(str, ++currentPosition, numberOfUnicodeDigits, buf);
              continue;
            } catch (final NumberFormatException e) {
            }
          } else {
            // escape character
            buf.append(currentChar);
          }
        } else {
          buf.append(currentChar);
        }
      } else {
        buf.append(currentChar);
      }
    }
    return buf.toString();
  }

  /**
   * Append the specified character (Unicode code point with the string length of <code>
   * numberOfUnicodeDigits</code> starting at <code>startPosition</code>) to its UTF-16
   * representation stored in a char array and append this array to the <code>buffer</code>. If the
   * specified code point is a BMP (Basic Multilingual Plane or Plane 0) value, the resulting char
   * array has the same value as codePoint. If the specified codepoint is a supplementary code
   * point, the resulting char array has the corresponding surrogate pair.
   *
   * @param str the input string
   * @param startPosition
   * @param numberOfUnicodeDigits
   * @param buffer the output string buffer
   * @return
   * @throws NumberFormatException
   */
  private static int codePointToUTF16(String str, int startPosition,
      final int numberOfUnicodeDigits, StringBuilder buffer) throws NumberFormatException {
    if (startPosition + numberOfUnicodeDigits >= str.length()) {
      throw new NumberFormatException("Number length must be exactly: " + numberOfUnicodeDigits);
    }
    final String number = str.substring(startPosition, startPosition + numberOfUnicodeDigits);
    // parseInt() may also throw NumberFormatException
    int codePoint = Integer.parseInt(number, 16);
    buffer.append(Character.toChars(codePoint));
    return startPosition + numberOfUnicodeDigits;
  }

  /**
   * Determines if the specified character is permissible as the first character in a Symja
   * identifier.
   *
   * <p>
   * A character may start a Symja identifier if and only if one of the following conditions is
   * true: • isLetter(ch) returns true • getType(ch) returns LETTER_NUMBER • ch is a currency symbol
   * (such as '$').
   *
   * @param ch
   * @return <code>true</code> if the character may start a Symja identifier; false otherwise.
   */
  public static boolean isSymjaIdentifierStart(char ch) {
    return (Character.isJavaIdentifierStart(ch) && (ch != '_'))
        || (ch >= '\uF800' && ch <= '\uF819'); // FormalA <= ch <= FormalZ
  }

  /**
   * Determines if the specified character may be part of a Symja identifier as other than the first
   * character.
   *
   * <p>
   * A character may be part of a Symja identifier if any of the followingare true: • it is a letter
   * • it is a currency symbol (such as '$') • it is a digit • it is a numeric letter (such as a
   * Roman numeral character) • it is a combining mark • it is a non-spacing mark • <code>
   * Character#isIdentifierIgnorable</code> returns true for the character
   *
   * @param ch the character to be tested.
   * @return <code>true</code> if the character may be part of a Symja identifier; false otherwise.
   */
  public static boolean isSymjaIdentifierPart(char ch) {
    return (Character.isJavaIdentifierPart(ch) && (ch != '_')) || (ch == '`') // context name
                                                                              // separator character
        || (ch >= '\uF800' && ch <= '\uF819'); // FormalA <= ch <= FormalZ
  }

  /**
   * Map the WL-unicode string to Unicode-equivalent string for inserting into an output form.
   *
   * <p>
   * Some data from Mathics: <a href=
   * "https://github.com/Mathics3/mathics-scanner/blob/master/mathics_scanner/data/named-characters.yml">named-characters.yml</a>
   *
   * @param operatorString
   * @return
   */
  public static String mapWLUnicodeToEquivalent(String operatorString) {
    StringBuilder sb = null;
    char newCh;
    for (int i = 0; i < operatorString.length(); i++) {
      char ch = operatorString.charAt(i);
      newCh = Character.MIN_VALUE;
      if ('\u0fff' < ch) {
        if ('\uF74B' == ch) {
          // CapitalDifferentialD
          newCh = '\u2145';
        } else if ('\uF523' == ch) {
          // Implies
          newCh = '\u21D2';
        } else if ('\u29E6' == ch) {
          // Equivalent
          newCh = '\u21D4'; // DoubleLeftRightArrow
        }

        if (Character.MIN_VALUE != newCh) {
          if (sb == null) {
            sb = new StringBuilder(operatorString.length());
            if (i > 0) {
              sb.append(operatorString.substring(0, i - 1));
            }
          }
        }
      }
      if (sb != null) {
        sb.append('\u0000' != newCh ? newCh : ch);
      }
    }
    return sb != null ? sb.toString() : operatorString;
  }
}
