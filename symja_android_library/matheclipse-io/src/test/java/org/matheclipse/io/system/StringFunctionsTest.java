package org.matheclipse.io.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for string functions */
public class StringFunctionsTest extends AbstractTestCase {

  public StringFunctionsTest(String name) {
    super(name);
  }

  public void testEditDistance() {
    check("EditDistance(\"kitten\", \"kitchen\")", //
        "2");
    check("EditDistance(\"abc\", \"ac\")", //
        "1");
    check("EditDistance(\"azbc\", \"abxyc\")", //
        "3");
    check("EditDistance(\"time\", \"Thyme\" )", //
        "3");
    check("EditDistance(\"time\", \"Thyme\", IgnoreCase -> True)", //
        "2");
  }

  public void testFileNameJoin() {
    String s = System.getProperty("os.name");
    if (s.contains("Windows")) {
      check("FileNameJoin({\"a\",\"b\",\"c\"})", //
          "a\\b\\c");
    }
  }

  public void testFileNameTake() {
    String s = System.getProperty("os.name");
    if (s.contains("Windows")) {
      check("FileNameTake(\"/a/b/c.txt\")", //
          "c.txt");
    }
  }

  public void testFromLetterNumber() {
    check("FromLetterNumber({1,4,10,17})", //
        "{a,d,j,q}");
    check("FromLetterNumber(26, \"Dutch\")", //
        "ij");
    check("FromLetterNumber(-2, \"Dutch\")", //
        "ij");
    check("FromLetterNumber(-1, \"Greek\")", //
        "ω");
    check("FromLetterNumber(10000)", //
        "Missing(NotApplicable)");
  }

  public void testHammingDistance() {
    check("HammingDistance(\"time\", \"dime\")", //
        "1");
    check("HammingDistance(\"TIME\", \"dime\", IgnoreCase -> True)", //
        "1");
  }

  public void testAlphabet() {
    // check(
    // "Alphabet(\"Hindi\")", //
    // "{अ,आ,इ,ई,उ,ऊ,ऋ,ए,ऐ,ओ,औ,क,ख,ग,घ,ङ,च,छ,ज,झ,ञ,ट,ठ,ड,ढ,ण,त,थ,द,ध,न,प,फ,ब,भ,म,य,र,ल,व,श,ष,स,ह}");
    check("Alphabet(\"Vietnamese\")", //
        "{a,ă,â,b,c,d,đ,e,ê,f,g,h,i,k,l,m,n,o,ô,ơ,p,q,r,s,t,u,ư,v,x,y}");
    // check(
    // "Alphabet(\"Korean\")", //
    // "{ㄱ,ㄲ,ㄴ,ㄷ,ㄸ,ㄹ,ㅁ,ㅂ,ㅃ,ㅅ,ㅆ,ㅇ,ㅈ,ㅉ,ㅊ,ㅋ,ㅌ,ㅍ,ㅎ,ㅏ,ㅐ,ㅑ,ㅒ,ㅓ,ㅔ,ㅕ,ㅖ,ㅗ,ㅘ,ㅙ,ㅚ,ㅛ,ㅜ,ㅝ,ㅞ,ㅟ,ㅠ,ㅡ,ㅢ,ㅣ}");
    // check(
    // "Alphabet(\"Arabic\")", //
    // "{ا,ب,ت,ث,ج,ح,خ,د,ذ,ر,ز,س,ش,ص,ض,ط,ظ,ع,غ,ف,ق,ك,ل,م,ن,ه,و,ي}");
    // check(
    // "Alphabet(\"Belarusian\")", //
    // "{а,б,в,г,д,е,ё,ж,з,і,й,к,л,м,н,о,п,р,с,т,у,ў,ф,х,ц,ч,ш,ы,ь,э,ю,я,'}");
    // check(
    // "Alphabet(\"Bulgarian\")", //
    // "{а,б,в,г,д,е,ж,з,и,й,к,л,м,н,о,п,р,с,т,у,ф,х,ц,ч,ш,щ,ъ,ь,ю,я}");
    check("Alphabet(\"Dutch\")", //
        "{a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,ij,z}");
    check("Alphabet(\"Greek\")", //
        "{α,β,γ,δ,ε,ζ,η,θ,ι,κ,λ,μ,ν,ξ,ο,π,ρ,σ,τ,υ,φ,χ,ψ,ω}");
    // check(
    // "Alphabet(\"Hebrew\")", //
    // "{α,β,γ,δ,ε,ζ,η,θ,ι,κ,λ,μ,ν,ξ,ο,π,ρ,σ,τ,υ,φ,χ,ψ,ω}");
    check("Alphabet(\"German\")", //
        "{a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}");
    check("Alphabet(\"Polish\")", //
        "{a,ą,b,c,ć,d,e,ę,f,g,h,i,j,k,l,ł,m,n,ń,o,ó,p,r,s,ś,t,u,w,y,z,ź,ż}");
    check("Alphabet( )", //
        "{a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}");
  }

  public void testLetterNumber() {
    // English alphabet has only single character elements
    check("LetterNumber({\"P\", \"Pe\", \"P1\", \"eck\"})", //
        "{16,{16,5},{16,0},{5,3,11}}");
    // Dutch alphabet can have elements with multiple characters
    check("LetterNumber({\"P\", \"Pe\", \"P1\", \"eck\"},  \"Dutch\")", //
        "{16,0,0,0}");

    check("LetterNumber(\"я\",  \"Russian\")", //
        "33");
    check("LetterNumber(Characters(\"Turtle\"))", //
        "{20,21,18,20,12,5}");
    check("LetterNumber(\"\\[Gamma]\", \"Greek\")", //
        "3");
    check("LetterNumber(\"\\[Lambda]\", \"Greek\")", //
        "11");
    // by default "English" alphabet is assumed
    check("LetterNumber(\"\\[Alpha]\")", //
        "0");

    check("LetterNumber(\"a\")", //
        "1");
    check("LetterNumber(\"d\")", //
        "4");
    check("LetterNumber(\"A\")", //
        "1");
    check("LetterNumber(\"D\")", //
        "4");
    check("LetterNumber(\"a\",\"French\")", //
        "1");
    check("LetterNumber(\"ij\",\"Dutch\")", //
        "26");
    check("LetterNumber(\"dzs\",\"Hungarian\")", //
        "8");
  }

  public void testLongest() {
    check("StringCases(\"aabaaab\", Longest(\"a\" ~~ __ ~~ \"b\"))", //
        "{aabaaab}");
    check("StringCases(\"aabaaabaaabaaabaaab\", Longest(RegularExpression(\"a+b\")))", //
        "{aab,aaab,aaab,aaab,aaab}");
  }

  public void testShortest() {
    check("StringCases(\"-(a)--(bb)--(c)-\", \"(\" ~~ __ ~~ \")\")", //
        "{(a)--(bb)--(c)}");
    check("StringCases(\"-(a)--(bb)--(c)-\", Shortest(\"(\" ~~ __ ~~ \")\"))", //
        "{(a),(bb),(c)}");

    check("StringCases(\"aabaaab\", Shortest(\"a\" ~~ __ ~~ \"b\"))", //
        "{aab,aaab}");
    check("StringCases(\"aabaaabaaabaaabaaab\", Shortest(RegularExpression(\"a+b\")))", //
        "{aab,aaab,aaab,aaab,aaab}");
  }

  public void testString() {
    check("Head(\"abc\")", //
        "String");
    check("\"abc\"", //
        "abc");
    check("InputForm(\"abc\")", //
        "\"abc\"");
    check("FullForm(\"abc\" + 2)", //
        "Plus(2, \"abc\")");

    check("\"test\"(x)", //
        "test[x]");
    check("\"Hello world!\\\n" //
        + "next line\"", //
        "Hello world!\n" + "next line");
    check("\"Hello world!\\\n" //
        + "next line\"", //
        "Hello world!\n" + //
            "next line");
  }

  public void testStringExpression() {
    check("\"a\" ~~ \"b\" ~~ \"c\" // FullForm ", //
        "\"abc\"");
  }

  public void testStringFormat() {
    check("StringFormat(\"abcd 1234\")", //
        "Text");
    check("StringFormat(\"1 2 3\\n 5 6 7\\n 8 9 0\\n\")", //
        "Table");
    check("StringFormat(\"1,2\\n 3,4\\n 5,6\\n\")", //
        "CSV");
    check("StringFormat(\"1\\t2\\n 3\\t4\\n 5\\t6\\n\")", //
        "TSV");
  }

  public void testStringFreeQ() {
    check("StringFreeQ(2^x,\"a\")", //
        "StringFreeQ(2^x,a)");
    check("StringFreeQ(\"symja\", \"s\" ~~__ ~~\"a\")", //
        "False");
    check("StringFreeQ(\"symja\", \"y\" ~~__~~\"s\")", //
        "True");
    check("StringFreeQ(\"Symja\", \"SY\" ,IgnoreCase -> True)", //
        "False");
    check("StringFreeQ({\"g\", \"a\", \"laxy\", \"universe\", \"sun\"}, \"u\")", //
        "{True,True,True,False,False}");
    check(
        "StringFreeQ(\"e\" ~~___ ~~\"u\") /@ {\"The Sun\", \"Mercury\", \"Venus\", \"Earth\", \"Mars\", \"Jupiter\", \"Saturn\", \"Uranus\", \"Neptune\"}", //
        "{False,False,False,True,True,True,True,True,False}");
    check(
        "StringFreeQ({\"A\", \"Galaxy\", \"Far\", \"Far\", \"Away\"}, {\"F\" ~~__ ~~\"r\", \"aw\" ~~___}, IgnoreCase ->True)", //
        "{True,True,False,False,False}");
  }

  public void testStringCount() {
    check(
        "StringCount(\"https://github.com/axkr/symja_android_library\", #) & /@ CharacterRange(\"a\", \"z\")", //
        "{4,2,1,2,0,0,1,2,3,1,1,1,2,1,2,1,0,4,2,3,1,0,0,1,2,0}");
    check("StringCount(\"a#ä_123\", WordCharacter)", //
        "5");
    check("StringCount(\"a#ä_123\", LetterCharacter)", //
        "2");
    check(
        "StringCount(\"the cat in the hat\", RegularExpression[\"(?<=the )\"] ~~ WordCharacter ..)", //
        "2");
    check("StringCount({\"ability\", \"argument\", \"listable\"}, \"a\" ~~ ___ ~~ \"l\")", //
        "{1,0,1}");
    check("StringCount(\"abAB\", \"a\")", //
        "1");
    check("StringCount(\"abAB\", \"a\", IgnoreCase -> True)", //
        "2");
    check("StringCount(\"abaababba\", \"a\" ~~ ___ ~~ \"b\")", //
        "1");
  }

  public void testStringCases() {
    check("StringCases(\"AaBBccDDeefG\", CharacterRange(\"A\", \"Z\") ..)", //
        "{A,BB,DD,G}");
    check("StringCases(\"a#ä_123\", WordCharacter)", //
        "{a,ä,1,2,3}");
    check("StringCases(\"a#ä_123\", LetterCharacter)", //
        "{a,ä}");

    check("StringCases(\"\",  {})", //
        "{}");
    check("StringCases(\"abcdabcdcd\", \"abc\")", //
        "{abc,abc}");
    check("StringCases(\"abcdabcdcd\", \"abc\" | \"cd\")", //
        "{abc,abc,cd}");
    check("StringCases(\"12341235678\", \"123\" | \"78\")", //
        "{123,123,78}");

    // TODO
    // check("StringCases(\"abcadcacb\", \"a\" ~~ x_ ~~ \"c\" -> x)", //
    // "{b,d}");
    check("StringCases(\"a\" ~~ _ ~~ \"c\")[\"abcadcacb\"]", //
        "{abc,adc}");
    check("StringCases(\"ab bac adaf\", \"a\" ~~ LetterCharacter ..)", //
        "{ab,ac,adaf}");
  }

  public void testStringContainsQ() {
    check("StringContainsQ( \"BC\" , IgnoreCase -> True)[\"abcd\"]", //
        "True");
    check("StringContainsQ({\"the quick brown fox\", \"jumps\", \"over the lazy dog\"}, \"the\")", //
        "{True,False,True}");
    check("StringContainsQ({\"a\", \"b\", \"ab\", \"abcd\", \"bcde\"}, \"a\")", //
        "{True,False,True,True,False}");
    check(
        "StringContainsQ(\"C\" ~~ _ ~~ \"C\")/@ {\"CAC1\", \"CTG1\", \"ACT1\", \"CGA1\", \"CTC1\"}", //
        "{True,False,False,False,True}");

    check("StringContainsQ(\"abcd\", \"BC\" , IgnoreCase -> False)", //
        "False");
    check("StringContainsQ(\"abcd\", \"BC\" , IgnoreCase -> True)", //
        "True");

    // StringContainsQ: StringExpression currently only partial supported in StringContainsQ.
    check("StringContainsQ(\"bcde\", \"c\" ~~ __ ~~ \"t\")", //
        "False");
    check("StringContainsQ(\"bcde\", \"c\" ~~ __ ~~ \"e\")", //
        "True");

    check("StringContainsQ(\"bcde\", \"b\" ~~ _ ~~ \"e\")", //
        "False");
    check("StringContainsQ(\"bcde\", \"b\" ~~ __ ~~ \"e\")", //
        "True");
    check("StringContainsQ(\"be\", \"b\" ~~ __ ~~ \"e\")", //
        "False");
    check("StringContainsQ(\"be\", \"b\" ~~ ___ ~~ \"e\")", //
        "True");
  }

  public void testStringDrop() {
    check("StringDrop(\"abcdefghijklm\", 4)", //
        "efghijklm");
    check("StringDrop(\"abcdefghijklm\", -4)", //
        "abcdefghi");
    check("StringDrop(\"\",-1)", //
        "StringDrop(,-1)");
  }

  public void testStringInsert() {
    check("StringInsert({\"\", \"Symja\"}, \"\", {1, 1, -1})", //
        "{,Symja}");
    check("StringInsert({\"\", \"Symja\"}, \"X\", {1, 1, -1})", //
        "{XXX,XXSymjaX}");
    check("StringInsert({\"abcdefghijklm\", \"Symja\"}, \"X\", {0, 2})", //
        "{StringInsert(abcdefghijklm,X,{0,2}),StringInsert(Symja,X,{0,2})}");
    check("StringInsert(\"abcdefghijklm\", \"X\", {1, -1, 14, -14})", //
        "XXabcdefghijklmXX");
    check("StringInsert(\"adac\", \"he\", {1, 5})", //
        "headache");
    check("StringInsert(\"noting\", \"h\", 4)", //
        "nothing");
    check("StringInsert(\"abcdefghijklm\", \"XYZ\", -4)", //
        "abcdefghijXYZklm");
    check("StringInsert(\"abcdefghijklm\", \"XYZ\", {2, 3, 7})", //
        "aXYZbXYZcdefXYZghijklm");

    // StringInsert: Cannot insert at position 15 in abcdefghijklm.
    check("StringInsert(\"abcdefghijklm\", \"X\", 15)", //
        "StringInsert(abcdefghijklm,X,15)");
    check("StringInsert(\"1234567890123456\", \".\", Range(-16, -4, 3))", //
        "1.234.567.890.123.456");
  }

  public void testStringJoin() {
    check("StringJoin({\"a\", \"b\"})// InputForm", //
        "\"ab\"");
    check("StringJoin(\"test\")", //
        "test");
    check("\"Hello\" <> \" \" <> \"world!\"", //
        "Hello world!");

    // message String expected at position `1` in `2`.
    check("\"Debian\" <> 6", //
        "Debian<>6");
    check("\"Debian\" <> ToString(6)", //
        "Debian6");
    check("\"Java\" <> ToString(8)", //
        "Java8");
    check("StringJoin(\"Java\", ToString(8))", //
        "Java8");
  }

  public void testStringLength() {
    check("StringLength(\"symja\")", //
        "5");
  }

  public void testStringMatchQ() {
    check("StringMatchQ(#, StartOfString ~~\"a\" ~~__) &/@ {\"apple\", \"banana\", \"artichoke\"}", //
        "{True,False,True}");
    check("StringMatchQ(\"15a94xcZ6\", (DigitCharacter | LetterCharacter)..)", //
        "True");
    check("StringMatchQ(\"apppbb\", \"a\" ~~ ___ ~~ \"b\")", //
        "True");
    check("StringMatchQ(\"apppbb\", \"a*b\")", //
        "True");
    check("StringMatchQ(\"a*b\")[\"apppbb\"]", //
        "True");
    check("StringMatchQ(\"acggtaagc\", Characters(\"acgt\") ..)", //
        "True");

    check("StringMatchQ(\"tester\", \"t\" ~~ __ ~~ \"t\")", //
        "False");
    check("StringMatchQ(\"abc 123 a\", RegularExpression(\"a.*\") ~~ DigitCharacter ..)", //
        "False");
    check("StringMatchQ(\"abc 123\", RegularExpression(\"a.*\") ~~ DigitCharacter ..)", //
        "True");

    check("StringMatchQ(\"acggtATTCaagc\", __ ~~ \"aT\" ~~ __, IgnoreCase -> True)", //
        "True");
    check("StringMatchQ(\"acggtATTCaagc\", __ ~~ \"aT\" ~~ __, IgnoreCase -> False)", //
        "False");

    check("StringMatchQ(\"acggtaagc\", RegularExpression(\"[acgt]+\"))", //
        "True");
    check("StringMatchQ({\"ExpandAll\", \"listable\", \"test\"}, RegularExpression(\"li(.+?)le\"))", //
        "{False,True,False}");
    check("StringMatchQ(\"abaababbat\", ___ ~~ \"t\" ~~ EndOfString)", //
        "True");
  }

  public void testStringPart() {
    check("StringPart(\"abcdefghijlkm\", 14)", //
        "StringPart(abcdefghijlkm,14)");
    check("StringPart(\"abcdefghijlkm\", 13)", //
        "m");
    check("StringPart(\"abcdefghijlkm\", 12)", //
        "k");
    check("StringPart(\"abcdefghijlkm\", 6)", //
        "f");
    check("StringPart(\"abcdefghijlkm\", {1, 3, 5})", //
        "{a,c,e}");
    check("StringPart(\"1234567890\",  {1, 3, 10})", //
        "{1,3,0}");
  }

  public void testStringPosition() {
    check("StringPosition(1317624576693539401)[2^10007,{x__},1/0]", //
        "StringPosition(1317624576693539401)[2553680789607370732651189968234988907294075944814326342210571455811768170268409\\\n"
            + "1688857550955566329085311143845661707679067592308927249639830520045993083294666\\\n"
            + "0553344598960569942595155856205566662663041112552874387744110188295911739974516\\\n"
            + "1582859471985675582706539265184504529057004879560743612659527284785672348812370\\\n"
            + "4159284539852635682541380125271728846968724192805940176431207373737252524917301\\\n"
            + "0895154641682674809137912458764294864228109627154269867443667527240313991720226\\\n"
            + "3070076083242999592531386790114979797219277590090500020223257366989744415447521\\\n"
            + "5718447747693423682433068114664261952714149889810987419668140272818249082086104\\\n"
            + "4726755850585059016632884775193840100304966434922544502665092784676689830802280\\\n"
            + "1054899407897045976393479861715755817553766559853586855066551295009230309920016\\\n"
            + "7670861109386676233735593369806423670129275207476880440398767025857903506248083\\\n"
            + "3421948742581802416898597525004257234748876933194826772045722175524697811392792\\\n"
            + "5840768535862051316309875273549621485689844666375048736160051968962539809522951\\\n"
            + "5823511109689328733320185910461874204137865477725647842253883664683236743730948\\\n"
            + "3688923308718779170567208575199430505494796451980586128824365720032317036926173\\\n"
            + "1448054583452364723184241492583471211243267538340195222051028169311332366373681\\\n"
            + "8859277902857501764213727760793852933348200916092769149650600010795694028624363\\\n"
            + "6865640773200428501108335238669409035297482546713417857566810167431463371123703\\\n"
            + "2909389235836564042650710029556902515680361397189087410482907552954277185307175\\\n"
            + "8012571661764447776948192350702525822077876168880583227697435999448959077141212\\\n"
            + "2927377464744138460343190929897254193226845880835468341540492864933475179066247\\\n"
            + "4065216140909182439985852662873541226796308327301626767230184338659734674383321\\\n"
            + "4372008268139242498664287977179978254578011172161159458326318362339169179414596\\\n"
            + "7560802887870778661183903501166465043655466344065305442250959799597298629237447\\\n"
            + "9795308145603348818376787050690491356208293534732090399606176556851286787491051\\\n"
            + "2326881800610430254388527989006037399753024971645568315891046628188065220328041\\\n"
            + "5001011562275320428146624187861355388134360143892944811217841963180234258017750\\\n"
            + "2960347015089180894655886103867932121162451390096703260463353307489672851899931\\\n"
            + "8634081536464354889886440147624462331918439414616936815617662263885783746099021\\\n"
            + "4842909412755936229726531153528920615525046604137177703054340126278352488563657\\\n"
            + "4607535147314379744821597932865404194766581176267646924563795765419047892087811\\\n"
            + "9836479920601599010280617418269583184185056382893770464010495794222444307519847\\\n"
            + "7938690972727835824276711505369797996595950365821348529607980627922438873121561\\\n"
            + "8427337091687982280641605547302818563724501587272775316160706141122419096848603\\\n"
            + "0512243391892712083419576329892672733682633537178022637190204305687443721374788\\\n"
            + "2483692314675666198575410067848189299209508527004112395564518074026501995120587\\\n"
            + "1178270457883984527370335080902568697364175929382210363073242225667491164361560\\\n"
            + "0808441280524457107303037683783428373538495727540884560909678581134952671110134\\\n"
            + "52378800128,{x__},ComplexInfinity]");
    check("StringPosition(\"123ABCxyABCzzzABCABC\", \"ABC\")", //
        "{{4,6},{9,11},{15,17},{18,20}}");
    check("StringPosition(\"123ABCxyABCzzzABCABC\", \"ABC\", 2)", //
        "{{4,6},{9,11}}");
  }

  public void testStringTrim() {
    check("StringJoin(\"a\", StringTrim(\" \\tb\\n \"), \"c\")", //
        "abc");
    check("StringTrim(\"ababaxababyaabab\", RegularExpression(\"(ab)+\"))", //
        "axababya");

    check("StringTrim(\"   aaa bbb ccc   \") // FullForm", //
        "\"aaa bbb ccc\"");

    check("StringTrim(\"   aaa bbb ccc   \", RegularExpression(\"^ *\")) // FullForm", //
        "\"aaa bbb ccc   \"");
  }

  public void testStringQ() {
    check("StringQ(a)", //
        "False");
    check("StringQ(\"a\")", //
        "True");
  }

  public void testStringSplit() {
    check("StringSplit(\"a\\nb\\r\\nc\\rd\", WhitespaceCharacter)//InputForm", //
        "{\"a\",\"b\",\"\",\"c\",\"d\"}");
    check("StringSplit(\"x\", \"x\")", //
        "{}");
    check("StringSplit({\"test,fdfd\", \"fhjagsjdg\"},\",\") // InputForm", //
        "{{\"test\",\"fdfd\"},{\"fhjagsjdg\"}}");
    check("StringSplit(test, \",\")", //
        "StringSplit(test,,)");
    check("StringSplit(\"a, ,c,d,\", \",\") // InputForm", //
        "{\"a\",\" \",\"c\",\"d\"}");
    check("StringSplit(\"abc\\ndef\\nhij\",StartOfLine)", //
        "{abc,def,hij}");
    check("StringSplit(\"13  a22    bbb\", WhitespaceCharacter) // InputForm", //
        "{\"13\",\"\",\"a22\",\"\",\"\",\"\",\"bbb\"}");
    check("StringSplit(\"13 a22 bbb\", WhitespaceCharacter) // InputForm", //
        "{\"13\",\"a22\",\"bbb\"}");
    check("StringSplit(\"13  a22    bbb\", Whitespace) // InputForm", //
        "{\"13\",\"a22\",\"bbb\"}");

    check("StringSplit(\"\")", //
        "{}");
    check("StringSplit(\"test\")", //
        "{test}");
    check("StringSplit(\"a bbb  cccc aa   d\")", //
        "{a,bbb,cccc,aa,d}");
    check(
        "StringSplit(\"A tree, an apple, four pears. And more: two sacks\", RegularExpression(\"\\\\W+\"))", //
        "{A,tree,an,apple,four,pears,And,more,two,sacks}");
    check("StringSplit(\"a--bbb---ccc--dddd\", \"--\")", //
        "{a,bbb,-ccc,dddd}");
    check("StringSplit(\"128.0.0.1\", \".\")", //
        "{128,0,0,1}");
    check("StringSplit(\"128.0.0.1\", RegularExpression(\"\\\\W+\"))", //
        "{128,0,0,1}");
    check("StringSplit(\"a1b2.2c0.333d4444.0efghijlkm\", NumberString)", //
        "{a,b,c,d,efghijlkm}");
  }

  public void testStringExpresion() {
    check("\"ab\" ~~ _", //
        "ab~~_");
  }

  public void testStringReplace() {
    check(
        "StringReplace(\"The number 17 is a prime number, 42 is not\", WordBoundary ~~ x:DigitCharacter.. ~~ WordBoundary /; PrimeQ(ToExpression(x)) :> \"*\" <> x <> \"*\")", //
        "The number *17* is a prime number, 42 is not");
    check("StringReplace(\"this is a text\", WordBoundary ~~ x_ :> ToUpperCase(x))", //
        "This Is A Text");

    check("StringReplace(\"this is text\", CharacterRange(\"a\", \"z\") -> \"X\")", //
        "XXXX XX XXXX");
    check("StringReplace(\"01101100010\", \"01\" .. -> \"x\")", //
        "x1x100x0");
    check("StringReplace(\"ab\" .. -> \"X\")[\"ababbabbaaababa\"]", //
        "XbXbaaXa");
    check("StringReplace(\"abc abcb abdc\", \"ab\" ~~ _ -> \"X\")", //
        "X Xb Xc");
    check("StringReplace(\"abc abcd abcd\", WordBoundary ~~ \"abc\" ~~ WordBoundary -> \"XX\")", //
        "XX abcd abcd");
    check("StringReplace(\"abcd acbd\", RegularExpression(\"[ab]\") -> \"XX\")", //
        "XXXXcd XXcXXd");
    check("StringReplace(\"abcdabcdaabcabcd\", {\"abc\" -> \"Y\", \"d\" -> \"XXX\"})", //
        "YXXXYXXXaYYXXX");
    check("StringReplace(\"product: A \\[CirclePlus] B\" , \"\\[CirclePlus]\" -> \"x\")", //
        "product: A x B");
    check("StringReplace({\"aaabbbbaaaa\", \"bbbaaaab\", \"aaabab\"}, \"ab\" -> \"X\")", //
        "{aaXbbbaaaa,bbbaaaX,aaXX}");

    check("StringReplace(\"The cat in the hat.\", \"the\" -> \"a\", IgnoreCase -> True)", //
        "a cat in a hat.");

    check("StringReplace(\"the quick brown fox jumps over the lazy dog\", \"the\" -> \"a\")", //
        "a quick brown fox jumps over a lazy dog");
    // operator form:
    check("StringReplace(\"ab\" -> \"X\")[\"abbaabbaa\"]", //
        "XbaXbaa");

    check("StringReplace(\"abbaabbaa\", \"ab\" -> \"X\")", //
        "XbaXbaa");
    check("StringReplace(\"ababbabbaaaCb\", LetterCharacter ~~ EndOfString -> \"t\")", //
        "ababbabbaaaCt");
    check(
        "StringReplace(\"  see you later alligator.  \", (Whitespace ~~ EndOfString) ->  \"\")// FullForm", //
        "\"  see you later alligator.\"");
  }

  public void testStringReverse() {
    // https://en.wikipedia.org/wiki/Palindrome
    check("StringReverse(\"Never odd or even\")", //
        "neve ro ddo reveN");
  }

  public void testStringRiffle() {
    check("StringRiffle({\"a\", \"b\", \"c\", \"d\", \"e\"})", //
        "a b c d e");

    check("StringRiffle({\"a\", \"b\", \"c\", \"d\", \"e\"}, \", \")", //
        "a, b, c, d, e");

    check("StringRiffle({\"a\", \"b\", \"c\", \"d\", \"e\"}, {\"(\", \" \", \")\"})", //
        "(a b c d e)");

    check("StringRiffle({{\"a\", \"b\", \"c\"}, {\"d\", \"e\", \"f\"}}, \"test\",0)", //
        "StringRiffle({{a,b,c},{d,e,f}},test,0)");
    check("StringRiffle({\"a\", \"b\", \"c\", \"d\", \"e\"})", //
        "a b c d e");
    check("StringRiffle({\"a\", \"b\", \"c\", \"d\", \"e\"}, {\"(\", \" \", \")\"})", //
        "(a b c d e)");
    check("StringRiffle({{\"a\", \"b\", \"c\"}, {\"d\", \"e\", \"f\"}})", //
        "a b c\n" + "d e f");
    check("StringRiffle({{\"a\", \"b\", \"c\"}, {\"d\", \"e\", \"f\"}}, \"\\n\", \"\\t\")", //
        "a	b	c\n" + "d	e	f");
    check("StringRiffle(Range(10), \", \")", //
        "1, 2, 3, 4, 5, 6, 7, 8, 9, 10");
    check(
        "StringRiffle({{\"a\", \"b\", \"c\"}, {\"d\", \"e\", \"f\"}}, {\"(\", \"\\n\", \")\"},\"-\")", //
        "(a-b-c\n" + "d-e-f)");
  }

  public void testStringTake() {
    // TODO
    // check(
    // "StringTake( \"abc\",{{1,1},{1,3},{0,0},{1,2},{-1},{4}}) // InputForm", //
    // " ");

    check("StringTake({\"abcdef\", \"stuv\", \"xyzw\"}, -2) ", //
        "{ef,uv,zw}");

    check("StringTake(\"abc\",{1,3,0,2,-1,4}) // InputForm", //
        "{\"a\",\"abc\",\"\",\"ab\",\"c\",StringTake(\"abc\",4)}");
    check("StringTake(\"abcde\",{2,-3,0})", //
        "{ab,cde,}");
    check("StringTake(\"abcde\", 2)", //
        "ab");
    check("StringTake(\"abcde\", 0)", //
        "");
    check("StringTake(\"abcde\", -2)", //
        "de");
    check("StringTake(\"abcde\", {2})", //
        "b");
    check("StringTake(\"abcd\", {2,3})", //
        "bc");
    check("StringTake(\"abcdefgh\", {1, 5, 2})", //
        "ace");
    check("StringTake(\"abcdef\", All)", //
        "abcdef");

    check("StringTake(\"abcdefghijklm\", {1, -1, 2})", //
        "acegikm");
    check("StringTake(\"abcdefghijklm\", {1, -1, 3})", //
        "adgjm");
    check("StringTake(\"abcdefghijklm\", {1, 7, 2})", //
        "aceg");
    check("StringTake(\"abcdefghijklm\", {-6, 7, 2})", //
        "");
    check("StringTake(\"abcdefghijklm\", {-7, 7, 2})", //
        "g");
    check("StringTake(\"abcdefghijklm\", {-100, 7, 2})", //
        "StringTake(abcdefghijklm,{-100,7,2})");

    check("StringTake(\"abcdefghijklm\", {5})", //
        "e");
    check("StringTake(\"abcdefghijklm\", {-4})", //
        "j");
    // StringTake: Cannot take positions 0 through 0 in abcdefghijklm.
    check("StringTake(\"abcdefghijklm\", {0})", //
        "StringTake(abcdefghijklm,{0})");

    check("StringTake(\"abcdefghijklm\", {5, 10})", //
        "efghij");
    check("StringTake(\"abcdefghijklm\", {5, -4})", //
        "efghij");
    check("StringTake(\"abcdefghijklm\", {-6, -4})", //
        "hij");
    check("StringTake(\"abcdefghijklm\", {5, 100})", //
        "StringTake(abcdefghijklm,{5,100})");

    check("StringTake(\"abcdefghijklm\", UpTo(-100))", //
        "StringTake(abcdefghijklm,UpTo(-100))");
    check("StringTake(\"abcdefghijklm\", UpTo(Infinity))", //
        "abcdefghijklm");
    check("StringTake(\"abcdefghijklm\", UpTo(20))", //
        "abcdefghijklm");
    check("StringTake(\"abcdefghijklm\", UpTo(11))", //
        "abcdefghijk");
    check("StringTake(\"abcdefghijklm\", UpTo(12))", //
        "abcdefghijkl");
    check("StringTake(\"abcdefghijklm\", UpTo(13))", //
        "abcdefghijklm");
    check("StringTake(\"abcdefghijklm\", UpTo(14))", //
        "abcdefghijklm");
    check("StringTake(\"abcdefghijklm\", UpTo(0))", //
        "");

    // StringTake: Cannot take positions 1 through 1 in .
    check("StringTake(\"\", 1)", //
        "StringTake(,1)");
    // StringTake: Cannot take positions 1 through 6 in abc.
    check("StringTake(\"abc\", 6)", //
        "StringTake(abc,6)");

    check("StringTake(\"abcdefghijklm\", 6)", //
        "abcdef");
    check("StringTake(\"abcdefghijklm\", -4)", //
        "jklm");
  }

  public void testStringTemplate() {
    // operator form
    check(
        "StringTemplate(\"The quick brown `a` jumps over the lazy `b`.\")[<|\"a\" -> \"fox\", \"b\" -> \"dog\"|>]", //
        "The quick brown fox jumps over the lazy dog.");
  }

  public void testTemplateApply() {
    check(
        "TemplateApply(\"The quick brown `a` jumps over the lazy `b`.\",<|\"a\" -> \"fox\", \"b\" -> \"dog\"|>)", //
        "The quick brown fox jumps over the lazy dog.");
    check(
        "TemplateApply(StringTemplate(\"The quick brown `a` jumps over the lazy `b`.\"),<|\"a\" -> \"fox\", \"b\" -> \"dog\"|>)", //
        "The quick brown fox jumps over the lazy dog.");
    check("t = TemplateSlot(\"dd\") -> TemplateIf(\n" //
        + "    MatchQ(TemplateSlot(\"dd\"), _Integer),\n" + "    Fibonacci(TemplateSlot(\"dd\")),\n"
        + "    None);\n" + "TemplateApply(t, <|\"dd\" -> 10|>)", //
        "10->55");
    check("t = TemplateSlot(1);TemplateApply({\"Variable t is:\", t})", //
        "{Variable t is:,Missing(SlotAbsent,1)}");
    check(
        "t = TemplateSlot(1, DefaultValue->\"No data found\");TemplateApply({\"Variable t is:\", t})", //
        "{Variable t is:,No data found}");
    check(
        "t = TemplateSlot(\"data\", InsertionFunction -> Total);TemplateApply(t, <|\"data\" -> Range(10)|>)", //
        "55");
    check(
        "TemplateApply(\"The orbit of `planet` has period `period`.\", <|\"planet\" -> \"Jupiter\", \"period\" -> Quantity(11.8707, \"Years\")|>)", //
        "The orbit of Jupiter has period 11.8707[Years].");
    check("TemplateApply({1, 2, 3, TemplateSlot(\"element\"), 5}, <|\"element\" -> 4|>)", //
        "{1,2,3,4,5}");
    // check(
    // "TemplateApply( \"We use the `` template ``.\" , {\"Pebble\", \"engine\"})", //
    // "We use the Pebble template engine.");
    // check(
    // "TemplateApply(StringTemplate(\"We use the `` template ``.\"), {\"Pebble\",
    // \"engine\"})", //
    // "We use the Pebble template engine.");
    check(
        "TemplateApply(StringTemplate(\"We use the `2` template `1`.\"), {\"engine\", \"Pebble\"})", //
        "We use the Pebble template engine.");
    check(
        "TemplateApply(StringTemplate(\"We use the `name` template `motor`.\"), <|\"name\"->\"Pebble\", \"motor\"->\"engine\"|>)", //
        "We use the Pebble template engine.");
  }

  public void testTemplateIf() {
    check(
        "t=TemplateIf( TemplateSlot(\"summer\"), \"in summer ice cream is delicious\", \"ice cream is boring in winter\"); TemplateApply(t, <|\"summer\" -> True|>)", //
        "in summer ice cream is delicious");
    check(
        "t = TemplateIf( TemplateSlot(\"tt\"), True, False); TemplateApply(t, <|\"tt\" -> True === True|>)", //
        "True");
  }

  public void testToLowerCase() {
    check("ToLowerCase(\"This is a Test\")", //
        "this is a test");
  }

  public void testToUpperCase() {
    check("ToUpperCase(\"This is a Test\")", //
        "THIS IS A TEST");
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

  public void testWhitespace() {
    check("StringMatchQ(\"\\r \\n\", Whitespace)", //
        "True");

    check("StringSplit(\"a \\n b \\r\\n c d\", Whitespace)", //
        "{a,b,c,d}");
  }

  public void testWhitespaceCharacter() {
    check("StringMatchQ(\"\\n\", WhitespaceCharacter)", //
        "True");
    check("StringSplit(\"a\\nb\\nc d\", WhitespaceCharacter)", //
        "{a,b,c,d}");
  }

  public void testWordBoundary() {
    check("StringReplace(\"apple banana orange artichoke\", \"e\" ~~ WordBoundary -> \"E\")", //
        "applE banana orangE artichokE");
  }

  public void testWordCharacter() {
    check("StringMatchQ(#, WordCharacter) &/@ {\"1\", \"a\", \"A\", \",\", \" \"}", //
        "{True,True,True,False,False}");
    check("StringMatchQ(\"abc123DEF\", WordCharacter..)", //
        "True");
    check("StringMatchQ(\"$b;123\",WordCharacter..)", //
        "False");
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
