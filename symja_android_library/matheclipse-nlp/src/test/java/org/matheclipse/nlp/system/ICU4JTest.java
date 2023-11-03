package org.matheclipse.nlp.system;

/**
 * <a href="https://unicode-org.github.io/icu/userguide/icu4j/">ICU4J</a> related tests.
 *
 */
public class ICU4JTest extends AbstractTestCase {

  public ICU4JTest(String name) {
    super(name);
  }


  public void testIntegerName() {
    check("IntegerName(0,\"Tongan\")", //
        "noll");
    check("IntegerName(123007,\"Tongan\")", //
        "ett­hundra­tjugo­tre­tusen sju");
    check("IntegerName(-123007,\"Tongan\")", //
        "minus ett­hundra­tjugo­tre­tusen sju");

    // check("IntegerName(123007,\"Latin\")", //
    // "ciento veintitrés mil siete");
    // check("IntegerName(-123007,\"Latin\")", //
    // "menos ciento veintitrés mil siete");

    check("IntegerName(0,\"Dutch\")", //
        "nul");
    check("IntegerName(123007,\"Dutch\")", //
        "honderddrie­ën­twintig­duizend­zeven");
    check("IntegerName(-123007,\"Dutch\")", //
        "min honderddrie­ën­twintig­duizend­zeven");

    check("IntegerName(0,\"English\")", //
        "zero");
    check("IntegerName(42,\"English\")", //
        "forty-two");
    check("IntegerName(-42,\"English\")", //
        "minus forty-two");

    check("IntegerName(0,\"Esperanto\")", //
        "nulo");
    check("IntegerName(99,\"Esperanto\")", //
        "naŭdek naŭ");
    check("IntegerName(4711,\"Esperanto\")", //
        "kvar mil sepcent dek unu");
    check("IntegerName(123007,\"Esperanto\")", //
        "cent dudek tri mil sep");
    check("IntegerName(-123007,\"Esperanto\")", //
        "minus cent dudek tri mil sep");

    check("IntegerName(0,\"Finnish\")", //
        "nolla");
    check("IntegerName(123007,\"Finnish\")", //
        "sata­kaksi­kymmentä­kolme­tuhatta­seitsemän");
    check("IntegerName(-123007,\"Finnish\")", //
        "miinus sata­kaksi­kymmentä­kolme­tuhatta­seitsemän");

    check("IntegerName(0,\"French\")", //
        "zéro");
    check("IntegerName(123007,\"French\")", //
        "cent vingt-trois mille sept");
    check("IntegerName(-123007,\"French\")", //
        "moins cent vingt-trois mille sept");

    check("IntegerName(0,\"German\")", //
        "null");
    check("IntegerName(123007,\"German\")", //
        "ein­hundert­drei­und­zwanzig­tausend­sieben");
    check("IntegerName(-123007,\"German\")", //
        "minus ein­hundert­drei­und­zwanzig­tausend­sieben");

    check("IntegerName(0,\"Hungarian\")", //
        "nulla");
    check("IntegerName(123007,\"Hungarian\")", //
        "száz­huszon­három­ezer­hét");
    check("IntegerName(-123007,\"Hungarian\")", //
        "mínusz száz­huszon­három­ezer­hét");

    check("IntegerName(0,\"Italian\")", //
        "zero");
    check("IntegerName(123007,\"Italian\")", //
        "cento­venti­tré­mila­sette");
    check("IntegerName(-123007,\"Italian\")", //
        "meno cento­venti­tré­mila­sette");

    check("IntegerName(0,\"Polish\")", //
        "zero");
    check("IntegerName(123007,\"Polish\")", //
        "sto dwadzieścia trzy tysiące siedem");
    check("IntegerName(-123007,\"Polish\")", //
        "minus sto dwadzieścia trzy tysiące siedem");

    check("IntegerName(1223456789123456789123456789,\"Portuguese\")", //
        "IntegerName(1223456789123456789123456789,Portuguese)");
    check("IntegerName(0,\"Portuguese\")", //
        "zero");
    check("IntegerName(123007,\"Portuguese\")", //
        "cento e vinte e três mil e sete");
    check("IntegerName(-123007,\"Portuguese\")", //
        "menos cento e vinte e três mil e sete");

    check("IntegerName(0,\"Romanian\")", //
        "zero");
    check("IntegerName(123007,\"Romanian\")", //
        "una sută douăzeci şi trei mii şapte");
    check("IntegerName(-123007,\"Romanian\")", //
        "minus una sută douăzeci şi trei mii şapte");

    check("IntegerName(0,\"Russian\")", //
        "ноль");
    check("IntegerName(123007,\"Russian\")", //
        "сто двадцать три тысячи семь");
    check("IntegerName(-123007,\"Russian\")", //
        "минус сто двадцать три тысячи семь");

    check("IntegerName(0,\"Spanish\")", //
        "cero");
    check("IntegerName(123007,\"Spanish\")", //
        "ciento veintitrés mil siete");
    check("IntegerName(-123007,\"Spanish\")", //
        "menos ciento veintitrés mil siete");

    check("IntegerName(0,\"Swedish\")", //
        "noll");
    check("IntegerName(123007,\"Swedish\")", //
        "ett­hundra­tjugo­tre­tusen sju");
    check("IntegerName(-123007,\"Swedish\")", //
        "minus ett­hundra­tjugo­tre­tusen sju");

    check("IntegerName(0,\"Turkish\")", //
        "sıfır");
    check("IntegerName(123007,\"Turkish\")", //
        "yüz yirmi üç bin yedi");
    check("IntegerName(-123007,\"Turkish\")", //
        "eksi yüz yirmi üç bin yedi");

    check("IntegerName(0)", //
        "zero");
    check("IntegerName(42)", //
        "forty-two");
    check("IntegerName(-42)", //
        "minus forty-two");
  }

  public void testRemoveDiacritics() {
    check("RemoveDiacritics(\"\\[CapitalEpsilon]\\[Upsilon]\\[Rho]\\[Omega]\\[Pi]\\[Eta]\")", //
        "Ευρωπη");
    check("RemoveDiacritics(\"éèáàâ\")", //
        "eeaaa");
  }

  public void testTransliterate() {
    check("Transliterate(\"tadaima\", \"Hiragana\")", //
        "ただいま");
    check("Transliterate(\"fish\",\"Bopomofo\")", //
        "ㄈㄧ˙ㄕ");
    check("Transliterate(\"Фёдоров, Николай Алексеевич\",\"Cyrillic\"->\"English\")", //
        "Fëdorov, Nikolaj Alekseevič");
    check("Transliterate(\"Фёдоров, Николай Алексеевич\")", //
        "Fedorov, Nikolaj Alekseevic");
    check("Transliterate(\"Горбачёв, Михаил Сергеевич\")", //
        "Gorbacev, Mihail Sergeevic");
    check(
        "Transliterate(\"\\[CapitalAlpha]\\[Lambda]\\[CurlyPhi]\\[Alpha]\\[Beta]\\[Eta]\\[Tau]\\[Iota]\\[Kappa]\\[Omega]\\[FinalSigma]\")", //
        "Alphabetikos");
  }
}
