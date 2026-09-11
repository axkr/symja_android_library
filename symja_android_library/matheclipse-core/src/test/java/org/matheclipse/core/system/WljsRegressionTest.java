package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Wolfram Language behaviour that packages rely on and Symja did not have.
 *
 * <p>
 * Every one of these was found by loading the packages of a real application - the WLJS Notebook -
 * and is here so that it stays fixed. They have nothing else in common, which is why they are
 * together rather than spread through the suites of the functions they belong to.
 */
public class WljsRegressionTest extends ExprEvaluatorTestCase {

  /**
   * The engine these tests run in: Wolfram Language syntax, where <code>f[x]</code> is a call and
   * <code>key</code> is not the built-in <code>Key</code>.
   *
   * <p>
   * The rest of the suite runs in Symja's relaxed syntax, which lowercases identifiers and reads
   * <code>f(x)</code> as a call. The packages these tests come from are Wolfram Language source,
   * and reading them the relaxed way silently changes what they say.
   */
  static {
    // Before anything of Symja is loaded: the built-in symbol table is keyed by name, and with
    // Symja's relaxed syntax those keys are lower-cased ("join" for Join). Parsing Wolfram
    // Language then finds none of them. The flag therefore has to be set before F.await() builds
    // the table, which is why this class runs in a JVM of its own - see the "wolfram-language-
    // syntax" surefire execution in the module's pom.
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
  }

  private final ExprEvaluator wolframLanguage;

  public WljsRegressionTest() {
    EvalEngine engine = new EvalEngine(false);
    EvalEngine.set(engine);
    engine.init();
    engine.setRecursionLimit(512);
    engine.setIterationLimit(500);
    engine.setOutListDisabled(false, (short) 10);
    wolframLanguage = new ExprEvaluator(engine, false, (short) 100);
  }

  /** Evaluate one input in Wolfram Language syntax and compare what it writes. */
  @Override
  public void check(String evalString, String expectedResult) {
    checkWolframLanguage(evalString, expectedResult, "");
  }

  /**
   * As {@link #check(String, String)}; the third argument is the message of the exception the
   * evaluation is expected to throw, as in {@link ExprEvaluatorTestCase}.
   */
  @Override
  public void check(String evalString, String expectedResult, String strException) {
    checkWolframLanguage(evalString, expectedResult, strException);
  }

  private void checkWolframLanguage(String evalString, String expectedResult,
      String strException) {
    EvalEngine previous = EvalEngine.get();
    try {
      EvalEngine.set(wolframLanguage.getEvalEngine());
      IExpr result = wolframLanguage.eval(evalString);
      assertEquals(expectedResult, printWolframLanguage(result));
    } catch (SyntaxError e) {
      assertEquals(expectedResult, e.getMessage());
    } catch (Exception e) {
      assertEquals(strException, e.getMessage());
    } finally {
      EvalEngine.set(previous);
    }
  }

  private static String printWolframLanguage(IExpr result) {
    if (result == S.Null) {
      return "";
    }
    StringWriter buf = new StringWriter();
    int significantFigures = EvalEngine.get().getSignificantFigures();
    OutputFormFactory off =
        OutputFormFactory.get(false, false, significantFigures - 1, significantFigures + 1);
    off.setGraphicsPlaceholder(true);
    return off.convert(buf, result) ? buf.toString() : "ERROR-IN-OUTPUTFORM";
  }

  @Test
  public void testTagIsFoundInsideAPattern() {
    // UObject /: MakeBoxes[object : UObject[…], form : StandardForm | TraditionalForm] := …
    // The tag stands under a Pattern, and the rule used to be refused as "tag not found".
    check("UObject /: MakeBoxes[object : UObject[symbol_Symbol], form : StandardForm | TraditionalForm] := \"boxed\"", //
        "");
    check("MakeBoxes[UObject[x], StandardForm]", //
        "boxed");
    // through a test and a condition as well
    check("Sock /: listen[socket : Sock[id_Integer] /; True, handler_] := \"listening\"", //
        "");
    check("listen[Sock[3], f]", //
        "listening");
    // ...but a tag that is really not there is still an error
    check("q /: r[s[x_]] := 1", //
        "$Failed");
  }

  @Test
  public void testOffAndOnSwitchOneMessage() {
    check("Part[{1, 2}, 5]", //
        "{1,2}[[5]]", //
        "Part: Part 5 of {1,2} does not exist.");
    check("Off[Part::partw]", //
        "");
    // the message is gone, the value is the same
    check("Part[{1, 2}, 5]", //
        "{1,2}[[5]]");
    check("On[Part::partw]", //
        "");
  }

  @Test
  public void testRejoiningAnAbsolutePathKeepsItsRoot() {
    // FileNameSplit["/a/b"] is {"", "a", "b"}: the empty first segment is the root, and dropping
    // it turned every absolute path into a relative one
    check("FileNameSplit[\"/Users/someone/x.wl\"]", //
        "{,Users,someone,x.wl}");
    // FileNameJoin writes the separator of the host it runs on, so what is asserted is that the
    // root is still there and still a separator - naming "/" would only hold away from Windows
    check("StringTake[FileNameJoin[FileNameSplit[\"/Users/someone/x.wl\"]], 1] "
        + "=== $PathnameSeparator", //
        "True");
    check("FileNameJoin[FileNameSplit[\"/Users/someone/x.wl\"]] "
        + "=== StringRiffle[{\"\", \"Users\", \"someone\", \"x.wl\"}, $PathnameSeparator]", //
        "True");
    check("FileNameJoin[{\"\", \"Users\", \"someone\"}] "
        + "=== StringRiffle[{\"\", \"Users\", \"someone\"}, $PathnameSeparator]", //
        "True");
  }

  @Test
  public void testAnEmptyListJoinsWithAnAssociation() {
    // a package joins in what it found, and finding nothing is not an incompatibility
    check("Join[<|\"a\" -> 1|>, {}]", //
        "<|a->1|>");
    check("Join[{}, <|\"a\" -> 1|>]", //
        "<|a->1|>");
    check("Join[<||>, {}]", //
        "<||>");
  }

  @Test
  public void testReturnCanNameTheConstructItLeaves() {
    // Return[Null, Module] is written by packages
    check("f[x_] := Module[{}, Return[x + 1, Module]; 99]", //
        "");
    check("f[1]", //
        "2");
  }

  @Test
  public void testTheDynamicLibraryExtensionIsKnown() {
    // a package that loads a shared library builds the file name from this
    check("MemberQ[{\"so\", \"dylib\", \"dll\"}, Internal`DynamicLibraryExtension[]]", //
        "True");
  }

  @Test
  public void testNeedsOfASystemContextIsSilent() {
    // the kernel provides these, so there is nothing to read and nothing to complain about
    check("Needs[\"Parallel`Developer`\"]", //
        "");
    check("Needs[\"Developer`\"]", //
        "");
  }

  @Test
  public void testLocalisingVariablesAroundAnAssociation() {
    // the module-variable visitor reads an element and writes it back, and an association answers
    // an element with its value - which it then refused to take
    check("Module[{a = <|\"x\" -> 1|>}, <|\"k\" -> a[\"x\"], \"e\" -> True|>]", //
        "<|k->1,e->True|>");
    check("f[assoc_] := Module[{b = assoc}, {b[\"n\"] -> Join[<|\"key\" -> b[\"n\"]|>, b]}]", //
        "");
    check("f[<|\"n\" -> \"v\"|>]", //
        "{v-><|key->v,n->v|>}");
  }
  @Test
  public void testStringCasesCanSayWhatToMakeOfEachMatch() {
    // A rule answers with what it builds from each match rather than with the matched text, and a
    // regular expression's groups are written "$1", "$2", ... anywhere inside it. This is how a
    // template engine reads the attributes out of a tag.
    check("StringCases[\"<Tag attr=1>\", RegularExpression[\"\\\\<\\\\/?([^\\\\<|\\\\>|\\\\/|\\\\s]*)[^\\\\<|\\\\>]*\\\\>\"] -> \"$1\"]", //
        "{Tag}");
    check("StringCases[\"x={a} y={b}\", RegularExpression[\"(\\\\w*)=\\\\{(\\\\w*)\\\\}\"] -> (\"$1\" -> \"$2\")]", //
        "{x->a,y->b}");
    check("StringCases[\"class=\\\"p{q}r\\\"\", RegularExpression[\"([\\\\w|\\\\-]*)=\\\"([^\\\"|=|{|}]*)\\\\{([^{}]*)\\\\}([^\\\"|=|{|}]*)\\\"\"] -> (\"$1\" -> {\"$2\", \"$3\", \"$4\"})]", //
        "{class->{p,q,r}}");
    // $0 is the whole match and $$ a literal dollar
    check("StringCases[\"ab\", RegularExpression[\"(a)(b)\"] -> \"$0|$$|$2\"]", //
        "{ab|$|b}");
    // the delayed form evaluates the right hand side once per match
    check("StringCases[\"a1b2\", RegularExpression[\"([a-z])(\\\\d)\"] :> StringJoin[\"$2\", \"$1\"]]", //
        "{1a,2b}");
    // a pattern written in the language names its parts with symbols instead
    check("StringCases[\"the cat\", \"c\" ~~ x__ -> x]", //
        "{at}");
    // no match, no results
    check("StringCases[\"nothing here\", RegularExpression[\"(z)(q)\"] -> \"$1\"]", //
        "{}");
    // and the pattern itself is evaluated, so a regular expression may be built
    check("innerPart = \"[a-z]+\"; StringCases[\"k={vv}\", RegularExpression[\"(\\\\w*)=\\\\{(\" <> innerPart <> \")\\\\}\"] -> (\"$1\" -> \"$2\")]", //
        "{k->vv}");
  }
  @Test
  public void testAPatternNameStandingForSeveralArgumentsIsSpreadIn() {
    // x__ holds its arguments as a Sequence, and putting one where a single argument was leaves
    // f[Sequence[a, b]] where f[a, b] was meant. Evaluation would flatten that, but a substitution
    // into a held expression is never evaluated - and {v} in Module[{v}, …] has to be the list of
    // names by the time Module sees it.
    check("f[{a, b, c}] /. _[{v__}] :> Hold[Module[{v}, 1]]", //
        "Hold[Module[{a,b,c},1]]");
    check("f[{a, b}] /. _[{v__}] :> Hold[g[v, 1]]", //
        "Hold[g[a,b,1]]");
    check("{{a, b}} /. {{v__}} :> Hold[{v, x}]", //
        "Hold[{a,b,x}]");
  }

  @Test
  public void testAContextMeansTheSameInsideAPackageAsOutside(@TempDir Path directory)
      throws IOException {
    // A package used to begin with no knowledge of the contexts that existed before it, so a
    // context it mentioned was created empty a second time - and when the package ended, that
    // empty one replaced the one holding the values. Everything assigned to it beforehand was
    // then unreachable by name.
    Path file = directory.resolve("Inner.wl");
    Files.write(file, ("BeginPackage[\"Inner`\"]\n" //
        + "Begin[\"`Private`\"]\n" //
        + "seen := Other`shared\n" //
        + "End[]\n" //
        + "EndPackage[]\n").getBytes(StandardCharsets.UTF_8));
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("Other`shared = {1, 2}", //
          "{1,2}");
      check("Get[\"" + file.toString().replace("\\", "\\\\") + "\"]", //
          "");
      // the value is still there, and the package sees the same symbol
      check("Other`shared", //
          "{1,2}");
      check("Inner`Private`seen", //
          "{1,2}");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testTheHeadOfAnAtomIsPartZero() {
    // Hold[Alert][[1, 0]] asks whether the name a template collected stands for a symbol or for a
    // call. Part refused it because part 1 is an atom, so every WLX page localised its variables
    // as Extract[…] expressions instead of as symbols and no page rendered.
    check("Hold[Alert][[1, 0]]", //
        "Symbol");
    check("Extract[Hold[Alert], {1, 0}, Hold]", //
        "Hold[Symbol]");
    check("Extract[Hold[f[x]], {1, 0}, Hold]", //
        "Hold[f]");
    check("Hold[\"text\"][[1, 0]]", //
        "String");
    // and a position that is not the head is still an error
    check("Hold[Alert][[1, 2]]", //
        "Hold[Alert][[1,2]]", //
        "Part: Part specification alert is longer than depth of object.");
  }

  @Test
  public void testANewlineAfterAnAssociationEndsTheStatement(@TempDir Path directory)
      throws IOException {
    // In a script a newline separates two statements, but the token after |> was read while the
    // parser still counted itself as inside the association, so the newline was swallowed and the
    // next definition in the file was multiplied onto the association.
    Path file = directory.resolve("Assoc.wl");
    Files.write(file, ("packet[x_] :=\n" //
        + "  <|\n" //
        + "    \"a\" -> x\n" //
        + "  |>\n" //
        + "\n" //
        + "packet[x_, y_] := {x, y}\n").getBytes(StandardCharsets.UTF_8));
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("Get[\"" + file.toString().replace("\\", "\\\\") + "\"]", //
          "");
      check("Length[DownValues[packet]]", //
          "2");
      check("packet[1]", //
          "<|a->1|>");
      check("packet[1, 2]", //
          "{1,2}");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testAnArgumentSkipsAnOptionalItDoesNotFit() {
    // CreateUType is declared as
    //   CreateUType[type_Symbol, parent:_Symbol?UTypeQ:UObject, init:_Symbol|_Function:Automatic,
    //               fields_List:{}]
    // and called with two arguments as often as with four. Which optional a supplied argument
    // belongs to is not settled by counting: an argument skips over an optional whose pattern it
    // does not fit.
    check("utq[x_] := x === UObj", //
        "");
    check("cut[t_Symbol, p:_Symbol?utq:UObj, i:_Symbol|_Function:Auto, f_List:{}] := {t,p,i,f}", //
        "");
    check("cut[T, {1}]", //
        "{T,UObj,Auto,{1}}");
    check("cut[T, UObj, {1}]", //
        "{T,UObj,Auto,{1}}");
    check("cut[T, ini, {1}]", //
        "{T,UObj,ini,{1}}");
    check("cut[T, UObj, ini, {1}]", //
        "{T,UObj,ini,{1}}");
  }

  @Test
  public void testASuppliedArgumentStillFillsTheEarliestSlot() {
    // the search above must not disturb the ordinary reading: one argument for two optionals
    // belongs to the first of them
    check("g[a_:1, b_:2] := {a, b}", //
        "");
    check("{g[9], g[], g[8, 7]}", //
        "{{9,2},{1,2},{8,7}}");
  }

  @Test
  public void testTheWebSocketAcceptKey() {
    // Packages/WebSocketHandler/Kernel/WebSocketHandler.wl answers a handshake with
    //   BaseEncode[Hash[key <> $guid, "SHA1", "ByteArray"], "Base64"]
    // The key and its answer here are the worked example in RFC 6455 section 1.3.
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    try {
      Config.FILESYSTEM_ENABLED = true;
      check(
          "BaseEncode[Hash[\"dGhlIHNhbXBsZSBub25jZQ==\" <> \"258EAFA5-E914-47DA-95CA-C5AB0DC85B11\", \"SHA1\", \"ByteArray\"], \"Base64\"]", //
          "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testHashOutputFormats() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    try {
      Config.FILESYSTEM_ENABLED = true;
      check("Hash[\"abc\", \"SHA256\", \"HexString\"]", //
          "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
      // a digest which begins with a zero byte keeps its width as a string, and its bytes
      check("Hash[\"abc\", \"SHA1\", \"HexString\"]", //
          "a9993e364706816aba3e25717850c26c9cd0d89d");
      check("StringLength[Hash[\"abc\", \"MD5\", \"DecimalString\"]]", //
          "39");
      check("Normal[Hash[\"abc\", \"SHA1\", \"ByteArray\"]] // Length", //
          "20");
      check("Hash[\"abc\", \"SHA1\"] === Hash[\"abc\", \"SHA\"]", //
          "True");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testBaseEncodeNamesItsEncoding() {
    check("BaseEncode[ByteArray[{1, 2, 3}], \"Base16\"]", //
        "010203");
    check("Normal[BaseDecode[\"AQID\", \"Base64\"]]", //
        "{1,2,3}");
    check("Normal[BaseDecode[BaseEncode[ByteArray[{0, 1, 255}]]]]", //
        "{0,1,255}");
  }

  @Test
  public void testADataStructureIsChangedInPlace() {
    // WLJS buffers a half-arrived request in a DynamicArray and remembers its clients in a
    // HashSet; both are filled by one evaluation and read by the next, so the container the
    // second one holds has to be the very container the first one changed.
    check("d = CreateDataStructure[\"DynamicArray\", {1, 2}]", //
        "DataStructure[DynamicArray, <2>]");
    check("DataStructureQ[d]", //
        "True");
    check("d[\"Append\", 3]; d[\"Elements\"]", //
        "{1,2,3}");
    check("d[\"Part\", -1]", //
        "3");
    check("e = d; e[\"Append\", 4]; d[\"Length\"]", //
        "4");
    check("d[\"DropAll\"]; d[\"Length\"]", //
        "0");
    // a pattern reaches it by head, the way saveFrameToBuffer[buffer_DataStructure, …] does
    check("f[b_DataStructure] := b[\"Length\"]", //
        "");
    check("f[d]", //
        "0");
  }

  @Test
  public void testHashSetAndHashTableMethods() {
    check("s = CreateDataStructure[\"HashSet\"]", //
        "DataStructure[HashSet, <0>]");
    check("{s[\"Insert\", x], s[\"MemberQ\", x], s[\"MemberQ\", y]}", //
        "{True,True,False}");
    check("{s[\"Remove\", x], s[\"MemberQ\", x], s[\"Elements\"]}", //
        "{True,False,{}}");
    check("h = CreateDataStructure[\"HashTable\"]", //
        "DataStructure[HashTable, <0>]");
    check("h[\"Insert\", 1 -> a]; {h[\"KeyExistsQ\", 1], h[\"Lookup\", 1], h[\"KeyExistsQ\", 2]}", //
        "{True,a,False}");
    check("CreateDataStructure[\"Nope\"]", //
        "CreateDataStructure[Nope]");
  }

  @Test
  public void testAnUnloadableLibraryFallsBackToWolframLanguage() {
    // Packages/Internal/Kernel/byteMask.wl is
    //   If[FailureQ[f = LibraryFunctionLoad[…]], f = Compile[…], f]
    // and unmasks every WebSocket frame the browser sends. Symja has no LibraryLink, so the
    // fallback is the only branch there is - which needs LibraryFunctionLoad to fail, FailureQ
    // to see that it did, and Compile to hold its body until it has arguments.
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    try {
      Config.FILESYSTEM_ENABLED = true;
      check("FailureQ[LibraryFunctionLoad[File[\"/no/such/library\"], \"f\", {}, \"ByteArray\"]]", //
          "True");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
    check("{FailureQ[$Failed], FailureQ[$Aborted], FailureQ[Failure[\"x\", <|\"a\" -> 1|>]]}", //
        "{True,True,True}");
    // a Missing is not a failure
    check("{FailureQ[Missing[\"KeyAbsent\", k]], FailureQ[3], FailureQ[f[x]]}", //
        "{False,False,False}");
  }

  @Test
  public void testAnUncompiledCompileHoldsItsBodyAndStillApplies() {
    // without matheclipse-compile on the class path Compile does not compile - but evaluating
    // the body before the parameters have values turns it into nonsense which is then kept:
    // Table[…, {i, 1, Length[payload]}] with a symbolic payload is {}
    check("c = Compile[{{key, _Integer, 1}, {payload, _Integer, 1}}, "
        + "Table[BitXor[payload[[i]], key[[Mod[i - 1, 4] + 1]]], {i, 1, Length[payload]}]]; Head[c]", //
        "Compile");
    check("c[{1, 2, 3, 4}, {10, 20, 30, 40, 50}]", //
        "{11,22,29,44,51}");
    check("d = Compile[{x}, x^2 + 1]; d[3]", //
        "10");
  }

  @Test
  public void testTakeAndDropOfAByteArray() {
    // a frame is read by dropping its header and taking its payload
    check("Normal[Drop[ByteArray[{1, 2, 3, 4, 5}], 2]]", //
        "{3,4,5}");
    check("Normal[Take[ByteArray[{1, 2, 3, 4, 5}], 2]]", //
        "{1,2}");
    check("Head[Drop[ByteArray[{1, 2, 3, 4, 5}], 2]]", //
        "ByteArray");
  }

  @Test
  public void testTheWolframLanguageWireFormatIsRead() {
    // These three strings were written by Mathematica: they are taken from the WLJS Notebook's
    // own test suite and its demo notebooks, where the browser reads them with mma.js. A cell
    // carries its content compressed, so a notebook is unreadable until these are.
    check("Uncompress[\"1:eJxTTMoPSmNkYGAo5gESAZmpyanlmcWpTvkVmUxAAQBzVQdd\"]", //
        "PiecewiseBox[2]");
    check(
        "Uncompress[\"1:eJxTTMoPSmNiYGAo5gUSYZmp5S6pyflFiSX5RcEsQBGXxJLUYCkgQ8k3P0/B0FzBN7FIwcjAyFTB0NLK1NjKyFgJACEfD5c=\"]", //
        "ViewDecorator[Date,\"Mon 17 Mar 2025 19:53:23\"]");
    check(
        "Uncompress[\"1:eJxTTMoPSmNkYGAoZgESHvk5KWnMIB4vkAjLTC13SU3OL0osyS8KZgOKOBbll+alBLMCmSbmBnomYJahqZmeBQA57g9C\"]", //
        "Hold[ViewDecorator[Around,470.4,156.8]]");
    // and a string in no format at all is not an expression
    check("Uncompress[\"garbage\"]", //
        "Uncompress[garbage]");
  }

  @Test
  public void testTheWolframLanguageWireFormatIsWritten() {
    // Compress writes what Mathematica writes, so the browser can read it
    check("StringTake[Compress[Hold[1 + 1]], 2]", //
        "1:");
    check("Uncompress[Compress[Hold[1 + 1]]]", //
        "Hold[1+1]");
    check("Uncompress[Compress[Expand[(a + b)^3]]]", //
        "a^3+3*a^2*b+3*a*b^2+b^3");
    // every kind of atom the format has a token for
    check("Uncompress[Compress[{1.5, 2/3, 3 + 4*I, \"text\", Sin, 12345678901234567890, "
        + "myContext`name}]]", //
        "{1.5,2/3,3+I*4,text,Sin,12345678901234567890,myContext`name}");
    check("Uncompress[Compress[N[Pi, 100]]] == N[Pi, 100]", //
        "True");
    // a string stays a string and a symbol stays a symbol, which OutputForm alone does not show
    check("Map[Head, Uncompress[Compress[{\"text\", Sin, 3 + 4*I, 2/3}]]]", //
        "{String,Symbol,Complex,Rational}");
    // a character outside ASCII is written \\:XXXX, and comes back as itself
    check("Uncompress[Compress[\"\\[Alpha]\\[Beta]\"]]", //
        "\u03b1\u03b2");
    // the legacy Symja format is still read
    check("Uncompress[\"H4sIAAAAAAAA/0uMM1bQVjDWSowz0kqCsLSS4oyArKQ4YwDZmlsNHQAAAA==\"]", //
        "a^3+3*a^2*b+3*a*b^2+b^3");
  }

  @Test
  public void testWritingToTheProcessOutput() {
    // Kernel/Utils.wl says
    //   WriteString[$StandardOutputStream, "<<<IPC>>>" <> ExportString[msg, "RawJSON"] <> "\n"]
    // which is how the notebook tells the application around it which port it came up on. The
    // stream has to exist, and WriteString has to take one instead of a file name.
    check("Head[$StandardOutputStream]", //
        "OutputStream");
    PrintStream out = System.out;
    ByteArrayOutputStream written = new ByteArrayOutputStream();
    try {
      System.setOut(new PrintStream(written, true, StandardCharsets.UTF_8));
      check("WriteString[$StandardOutputStream, \"<<<IPC>>>[1]\\n\"]", //
          "");
      check("WriteLine[$StandardOutputStream, \"a line\"]", //
          "");
    } finally {
      System.setOut(out);
    }
    assertEquals("<<<IPC>>>[1]\na line\n",
        new String(written.toByteArray(), StandardCharsets.UTF_8));
  }

  @Test
  public void testWriteStringStillRefusesWhatIsNotAString() {
    // it stays as it is, and says why - the stream's own name carries a serial number, so the
    // head is what this asserts
    check("Head[WriteString[$StandardOutputStream, 42]]", //
        "WriteString");
  }

  @Test
  public void testOnceEvaluatesItsExpressionOnlyOnce() {
    // Components/Notifications/Notifications.wlx reads a component with
    //   MessageList = Once[ImportComponent["Components/MessagesList.wlx"]]
    // and several modules attach their listeners with Once[attachListeners[#]] &, where a second
    // evaluation would attach them twice. Without an evaluator the call stayed as it was and the
    // unevaluated Once[...] was printed into the page.
    check("ctr = 0", //
        "0");
    check("f[] := (ctr = ctr + 1; \"result\")", //
        "");
    check("{Once[f[]], Once[f[]], ctr}", //
        "{result,result,1}");
    // the same expression anywhere is the same one
    check("g[x_] := (ctr = ctr + 10; x)", //
        "");
    check("{Table[Once[g[7]], {3}], ctr}", //
        "{{7,7,7},11}");
    // a different expression is worked out on its own
    check("{Once[g[8]], ctr}", //
        "{8,21}");
    // it holds what it is given, and takes a persistence location without complaining
    check("Attributes[Once]", //
        "{HoldFirst,Protected}");
    check("Once[f[], \"KernelSession\"]", //
        "result");
  }

  @Test
  public void testAComposedHeadIsBuiltBeforeItsArgumentIsEvaluated() {
    // WLX interpolates the text of an attribute with
    //   ToExpression[text, InputForm, FakeHold @* ToString]
    // and FakeHold being HoldAll is what keeps that text unevaluated until the page is rendered.
    // Evaluating the argument first ran the interpolation while the component's own variables
    // were still unassigned - a menu with class="{StringRiffle[ulStyles]}" complained that
    // StringRiffle wanted a string.
    check("SetAttributes[FakeHold, HoldAll]", //
        "");
    check("ToExpression[\"StringRiffle[styles]\", InputForm, FakeHold @* ToString]", //
        "FakeHold[ToString[StringRiffle[styles]]]");
    check("(FakeHold @* ToString)[1 + 1]", //
        "FakeHold[ToString[1+1]]");
    // ...and a composition of ordinary functions is what it always was
    check("{(f @* g)[x], Composition[f, g, h][x], RightComposition[f, g, h][x]}", //
        "{f[g[x]],f[g[h[x]]],h[g[f[x]]]}");
    check("{Composition[][x], Composition[f][x], Composition[Identity, f][x]}", //
        "{x,f[x],f[x]}");
    check("{(Sqrt @* Abs)[-4], RightComposition[Sqrt, Abs][-4], Composition[f, g][a, b]}", //
        "{2,2,f[g[a,b]]}");
    check("{Nest[Composition[f, g], x, 2], Map[Composition[Sin, Cos], {0, 1}]}", //
        "{f[g[f[g[x]]]],{Sin[1],Sin[Cos[1]]}}");
    check("Attributes[Composition]", //
        "{Flat,OneIdentity,Protected}");
  }

  @Test
  public void testAnAbsoluteContextNameIsNotHungUnderTheCurrentOne() {
    // Frontend/Views.wl begins Begin["CoffeeLiqueur`Notebook`Views`"], defines its router, and
    // ends by handing back CoffeeLiqueur`Notebook`Views`View written out in full. Hanging that
    // context under whichever one the file was read from put the definition in one symbol and
    // the name at the end on another, so the page carried the text of the call instead of what
    // the router built.
    check("Begin[\"Outer`Private`\"]", //
        "Outer`Private`");
    check("Begin[\"A`B`\"]", //
        "A`B`");
    check("$Context", //
        "A`B`");
    check("g[x__] := {x}", //
        "");
    // the short name inside the context and the name written out in full are the same symbol
    check("{Context[g], SymbolName[g], g === A`B`g}", //
        "{A`B`,A`B`g,True}");
    check("End[]", //
        "A`B`");
    check("{Length[DownValues[A`B`g]], A`B`g[1, 2]}", //
        "{1,{1,2}}");
    // ...while a leading backtick still names a context under the one open now
    check("Begin[\"`Inner`\"]", //
        "Outer`Private`Inner`");
    check("End[]; End[]; $Context", //
        "Global`");
  }

  @Test
  public void testReturnLeavesTheLoopAndTheFunctionAroundIt() {
    // Return exits the control structures of a definition and gives its value for the whole
    // definition. While and For were catching it and answering it as the loop's own value, so the
    // statement after the loop ran anyway and its value was the one that came back.
    check("f1[] := Module[{n = 0}, While[n < 5, n = n + 1; If[n === 2, Return[n]]]; $Failed]", //
        "");
    check("f1[]", //
        "2");
    check("f2[] := Module[{}, For[i = 0, i < 5, i++, If[i === 2, Return[i]]]; $Failed]", //
        "");
    check("f2[]", //
        "2");
    // ...with no scoping construct in between either
    check("f3[] := (While[True, Return[5]]; $Failed)", //
        "");
    check("f3[]", //
        "5");
    check("f4[] := (For[i = 0, True, i++, Return[6]]; $Failed)", //
        "");
    check("f4[]", //
        "6");
    // Do keeps its own semantics: the Return ends the iteration and becomes its value
    check("f5[] := Module[{}, Do[If[i === 2, Return[i]], {i, 5}]; $Failed]", //
        "");
    check("f5[]", //
        "$Failed");
    // Break and Continue still belong to the loop
    check("g[] := Module[{n = 0, s = 0}, While[n < 5, n = n + 1; If[n === 3, Continue[]]; "
        + "s = s + n]; s]", //
        "");
    check("{g[], Module[{n = 0}, While[True, n = n + 1; If[n > 3, Break[]]]; n]}", //
        "{12,4}");
    // a definition with no arguments is a definition too
    check("f6[] := (While[True, Return[7]]; $Failed)", //
        "");
    check("f6[]", //
        "7");
    // ...and so is one made on a symbol
    check("f7 := (Return[8]; $Failed)", //
        "");
    check("f7", //
        "8");
    // but a Return which reaches the top level is the Return itself
    check("i = 1; While[True, If[i^2 > 100, Return[i + 1], i++]]", //
        "Return[12]");
    check("i", //
        "11");
  }

  /**
   * The attributes of a symbol say how the arguments of <code>f[...]</code> are evaluated, not those
   * of <code>f[a][b]</code> - in the second the head is <code>f[a]</code>, which is no symbol at
   * all. WLJS's object system gives every type HoldFirst (so that <code>obj = UObject[sym]</code>
   * can rewrite the assignment), and every property access is written <code>obj[key]</code>. With
   * the attribute leaking through, a held argument still standing as a local symbol was matched
   * against the "look up a key" rule instead of the rule meant for its value.
   */
  @Test
  public void testAttributesBelongToTheHeadNotToTheTopHead() {
    check("SetAttributes[U, HoldFirst]", //
        "");
    check("U[symbol_Symbol][key_String] := symbol[key]", //
        "");
    check("U[symbol_Symbol][key_Symbol] := symbol[SymbolName[key]]", //
        "");
    check("srv_U[packet_Association] := \"entry\"", //
        "");
    check("fields = <|\"x\" -> 1|>", //
        "<|x->1|>");
    check("object = U[fields]", //
        "U[fields]");
    // a key still reads as a key
    check("object[\"x\"]", //
        "1");
    // an association reaches the rule written for an association, whether it is handed over
    // literally or through a local symbol which still has to be evaluated
    check("object[<|\"n\" -> 1|>]", //
        "entry");
    check("caller[o_U] := Module[{payload}, payload = <|\"n\" -> 1|>; o[payload]]", //
        "");
    check("caller[object]", //
        "entry");
    // Function carries its own attributes into the application, which still has to work
    check("h := Function[{x}, Hold[1 + x], HoldAll]", //
        "");
    check("h[1 + 1]", //
        "Hold[1+1+1]");
  }

  /**
   * A trailing <code>OptionsPattern[]</code> says nothing about how specific the rest of a rule is,
   * so it must not push the rule behind every rule that takes a run of arguments. WLJS's WLX
   * skeletons are written as a pair - one rule folds several children into one string and hands it
   * back to the other - and with the order reversed the folding rule matched its own result
   * forever.
   */
  @Test
  public void testAnOptionsPatternKeepsTheRulePriority() {
    check("frame[children__, opts : OptionsPattern[]] := frame[StringRiffle[{children}], opts]", //
        "");
    check("frame[child_, OptionsPattern[]] := \"<\" <> OptionValue[\"Id\"] <> \">\" <> child", //
        "");
    check("Options[frame] = {\"Id\" -> \"none\"}", //
        "{Id->none}");
    check("frame[\"a\", \"Id\" -> \"one\"]", //
        "<one>a");
    check("frame[\"a\", \"b\", \"Id\" -> \"two\"]", //
        "<two>a b");
  }

  /**
   * A context begun with a relative name knows itself only as <code>`Internal`</code>; which
   * <code>Internal`</code> it is comes from its parent. Written to WXF under that short name, every
   * package's private context collapses into whichever one was created first - which is how the
   * evaluation kernel was handed a symbol from a neighbouring package instead of the evaluator it
   * had been told to run.
   */
  @Test
  public void testAPrivateContextSurvivesTheWire() {
    check("BeginPackage[\"Co`One`\"]", //
        "");
    check("Begin[\"`Internal`\"]", //
        "Co`One`Internal`");
    check("End[]", //
        "Co`One`Internal`");
    check("EndPackage[]", //
        "");
    check("BeginPackage[\"Co`Two`\"]", //
        "");
    check("Begin[\"`Internal`\"]", //
        "Co`Two`Internal`");
    check("End[]", //
        "Co`Two`Internal`");
    check("EndPackage[]", //
        "");
    check("ToString[BinaryDeserialize[BinarySerialize[Co`Two`Internal`marker]]]", //
        "Co`Two`Internal`marker");
    check("ToString[BinaryDeserialize[BinarySerialize[Co`One`Internal`marker]]]", //
        "Co`One`Internal`marker");
  }

  /**
   * A stream whose behaviour is written in the Wolfram Language: <code>DefineOutputStreamMethod</code>
   * names the functions, <code>OpenWrite[Method -> name]</code> opens one, and
   * <code>$Output</code> is the list of streams a print goes to.
   *
   * <p>
   * That is how a front end puts what a kernel prints where it belongs. The WLJS notebook opens
   * such a stream and sends every printed line to the cell that is printing, so without the three
   * of them a Print in a cell went to a console nobody was looking at.
   */
  @Test
  public void testAPrintGoesWhereOutputSaysItGoes() {
    check("collected = {}", //
        "{}");
    check("DefineOutputStreamMethod[\"CollectForTest\", {"
        + "\"ConstructorFunction\" -> Function[{name, isAppend, caller, opts}, {True, <|\"n\" -> 0|>}], "
        + "\"WriteFunction\" -> Function[{state, bytes}, "
        + "collected = Append[collected, FromCharacterCode[bytes]]; {Length[bytes], state}], "
        + "\"CloseFunction\" -> Function[state, closedWith = state]}]", //
        "CollectForTest");
    check("stream = OpenWrite[Method -> \"CollectForTest\"]; Head[stream]", //
        "OutputStream");
    // with $Output naming it, a print is handed to the method rather than to the console
    check("$Output = {stream}; Print[\"hi\"]; Print[2^10]; $Output = {}; collected", //
        "{hi\n,1024\n}");
    // and the close is given the state the constructor made
    check("Close[stream]; closedWith", //
        "<|n->0|>");
    // an output which names no stream leaves printing as it was
    check("$Output = {}; Print[\"plain\"]", //
        "", //
        "plain");
  }

  /**
   * A <code>$...</code> variable comes back out of a <code>Block</code> with the value it went in
   * with.
   *
   * <p>
   * Such a symbol keeps its value in the engine rather than in the symbol, and the restore put it
   * back into the symbol - so the block's value stood afterwards. WLJS wraps its printing in
   * <code>Block[{$Output = {}}, …]</code> to keep it from printing into itself, so the first line
   * a cell printed switched the redirection off and every later one went to a console nobody was
   * reading.
   */
  @Test
  public void testABlockGivesBackADollarVariable() {
    check("$Assumptions = a; Block[{$Assumptions = b}, $Assumptions]", //
        "b");
    check("$Assumptions", //
        "a");
    // one which was never assigned goes back to having no value of its own
    check("Block[{$Output = {1}}, Length[$Output]]", //
        "1");
    check("$Output", //
        "{}");
    // an ordinary symbol was always restored and still is
    check("ordinary = 1; Block[{ordinary = 2}, ordinary]; ordinary", //
        "1");
  }

  /**
   * <code>ToCharacterCode[string, encoding]</code> names the encoding the codes are read in. Symja
   * holds a string as Java does, so the codes are the same whichever name is given - but refusing
   * the second argument made the call an error, and the notebook's own way of turning a printed
   * line back into characters got none.
   */
  @Test
  public void testToCharacterCodeAcceptsAnEncoding() {
    check("ToCharacterCode[\"ab\"]", //
        "{97,98}");
    check("ToCharacterCode[\"ab\", \"UTF8\"]", //
        "{97,98}");
  }

  /**
   * <code>EchoLabel(label)[expr]</code> says where a line of output came from, and is
   * <code>Echo(expr, label)</code>. The notebook's master kernel labels what its evaluation kernel
   * prints with it, so left unevaluated it took the message with it and the kernel's output was
   * lost on the way to the console.
   */
  @Test
  public void testEchoLabelSaysWhereOutputCameFrom() {
    check("EchoLabel[\"KernelPrint\"][\"hello\"]", //
        "hello", //
        "KernelPrinthello");
    // the plain head is left alone, as an operator form waiting for its argument
    check("Head[EchoLabel[\"KernelPrint\"]]", //
        "EchoLabel");
  }

  /**
   * A name which stood for one argument keeps standing for one, even when the argument it was
   * matched with is itself a <code>Sequence</code>. Only <code>x__</code> and <code>x___</code> are
   * spread into the expression they are substituted into.
   *
   * <p>
   * WLJS's object constructor turns each option into <code>SetDelayed[symbol[k], v]</code> through
   * such a rule, and a cell is created with <code>"After" -> Sequence[cell, ___?OutputCellQ]</code>
   * - which is how the notebook says where an output cell belongs. With the Sequence spread out,
   * the assignment became a three-argument SetDelayed, the field was never stored, and the output
   * cell was built but never placed.
   */
  @Test
  public void testASingleValuePatternIsNotSpreadOut() {
    check("outputQ[x_] := True", //
        "");
    check("fields = <||>", //
        "<||>");
    check("((\"After\" -> Sequence[cell, ___?outputQ]) /. "
        + "{_[k_String, v_] :> SetDelayed[fields[k], v]}); Keys[fields]", //
        "{After}");
    // the field holds the whole Sequence, which spreads out only when it is evaluated in a
    // position that allows it
    check("Head[fields[\"After\"]]", //
        "Sequence");
    check("Length[{fields[\"After\"]}]", //
        "2");
    // a name which stood for a run of arguments still is spread out
    check("{a, b} /. {x__} :> g[x]", //
        "g[a,b]");
    check("Hold[{a, b}] /. Hold[{w__}] :> Hold[Module[{w}, 1]]", //
        "Hold[Module[{a,b},1]]");
    // and an argument a rule left unassigned still disappears
    check("optional[x_, y_ : 0] := {x, y}", //
        "");
    check("{optional[1], optional[1, 2]}", //
        "{{1,0},{1,2}}");
  }

  /**
   * <code>Information</code> takes a name as well as a symbol, and answers a single property
   * rather than printing everything.
   *
   * <p>
   * WLJS's autocompletion asks <code>ToString@Information[#, "Usage"] &amp;/@ Names[#&lt;&gt;"*"]</code>,
   * and <code>Names</code> answers with strings - so every one of the ~4000 names produced an
   * <code>Information::sym</code> message at startup.
   */
  @Test
  public void testInformationTakesANameAndAnswersOneProperty() {
    check("Information[\"Sin\", \"Usage\"]", //
        "Sin - sine function");
    check("Information[Sin, \"Usage\"] === Information[\"Sin\", \"Usage\"]", //
        "True");
    check("Information[Sin, \"Attributes\"]", //
        "{Listable,NumericFunction,Protected}");
    // a symbol with no usage message is not an error
    check("Information[\"Global`noSuchThing\", \"Usage\"]", //
        "Missing[NotAvailable]");
    check("Information[Sin, \"Nonsense\"]", //
        "Missing[UnknownProperty,Nonsense]");
  }

  /**
   * <code>Function[Null, body]</code> binds no parameter: <code>Null</code> is the parameter
   * specification which says "none", and the body reads its arguments as slots.
   *
   * <p>
   * WLJS's kernel abort is such a function - <code>Module[{token}, token = Function[Null, ...
   * token = Null;]; ...]</code> in <code>Kernel/Evaluator.wl</code>. Taking Null for a formal
   * parameter renamed it to <code>Null$nnn</code> throughout the body and then refused to apply
   * the function at all, so pressing "Abort evaluation" never reached the kernel.
   */
  @Test
  public void testFunctionWithoutParametersBindsNothing() {
    check("Function[Null, 42][]", //
        "42");
    check("Function[Null, #^2][7]", //
        "49");
    check("Module[{tok}, tok = Function[Null, tok = 1]; tok[]; tok]", //
        "1");
    // and Null inside a Module body is left alone
    check("Module[{a}, StringContainsQ[ToString[Hold[Function[Null, a = Null]], InputForm], "
        + "\"Null$\"]]", //
        "False");
  }


  /**
   * A local variable written as the head a pattern must have is an occurrence of the variable, and
   * <code>With</code> substitutes it there too - in <code>a_s</code>, <code>_s</code> and
   * <code>b__s</code> alike.
   *
   * <p>
   * WLJS's <code>BoxesWorkarounds.wl</code> defines the boxes of each legend head with
   * <code>With[{sym = #}, sym /: MakeBoxes[a_sym, StandardForm] := ...] &amp; /@ {LineLegend, ...}</code>.
   * With the head test left as <code>sym</code>, <code>TagSetDelayed::tagnf</code> was raised, the
   * <code>Get</code> of the file failed half way, and the notebook showed a
   * <code>Get::error</code> warning for every new kernel.
   */
  @Test
  public void testWithSubstitutesTheHeadOfAPattern() {
    check("With[{s = f}, Hold[a_s, _s, b__s, c___s, s]]", //
        "Hold[a_f,_f,b__f,c___f,f]");
    check("Scan[With[{sym = #}, sym /: boxes[a_sym] := {\"boxed\", a}] &, {legendA, legendB}]", //
        "");
    check("{boxes[legendA[1]], boxes[legendB[2]]}", //
        "{{boxed,legendA[1]},{boxed,legendB[2]}}");
  }


  /**
   * The <code>Graphics</code> a plot answers with carries the options of a <code>Graphics</code>,
   * not the plot function's own options left at a value which says nothing.
   *
   * <p>
   * The WLJS notebook reads the options of the <code>Graphics</code> it is handed and was given
   * <code>JSForm</code>, <code>PlotLegends</code>, <code>Filling</code> and seven more it does not
   * know. A value which does say something stays, because Symja's own SVG renderer reads it.
   */
  @Test
  public void testAPlotHandsOnOnlyWhatSaysSomething() {
    check("Intersection[First /@ Rest[List @@ Plot[Sin[x], {x, 0, 1}]], {JSForm, PlotLegends, "
        + "Filling, Joined, PlotStyle, PlotLabels, FillingStyle, DataRange, ChartLegends, "
        + "$Scaling}]", //
        "{}");
    // the scaling, which is also what tells the SVG renderer the picture came from a plot, travels
    // under Method, written with strings only so that a front end needs no definition for it
    check("Method /. Rest[List @@ LogPlot[Exp[x], {x, 0, 1}]]", //
        "{Scaling->{None,Log}}");
    // Filling is drawn by the plot itself; nothing reads it off the Graphics
    check("MemberQ[First /@ Rest[List @@ Plot[Sin[x], {x, 0, 1}, Filling -> Axis]], Filling]", //
        "False");
  }

  /**
   * <code>$Path</code> can be assigned, and the directories it is set to are searched.
   *
   * <p>
   * The WLJS kernel runs <code>AppendTo[$Path, shared]</code> at every launch
   * (<code>Kernel/LocalKernel.wl</code>), and each one drew <code>AppendTo::rvalue</code>.
   */
  @Test
  public void testPathCanBeExtended() {
    try {
      check("AppendTo[$Path, \"/symja-test/packages\"]; Last[$Path]", //
          "/symja-test/packages");
      check("MemberQ[$Path, \"/symja-test/packages\"]", //
          "True");
    } finally {
      EvalEngine.get().removeDollarValue(S.$Path);
    }
  }

  /**
   * <code>Developer`RawCompress</code> compresses bytes with zlib and
   * <code>Developer`RawUncompress</code> undoes it.
   *
   * <p>
   * The WLJS notebook sends every object larger than 2 KB - every plot - to the browser through
   * exactly the chain checked last, and the browser inflates the result as zlib. Without the two
   * functions each plot drew a <code>ByteArray::lend</code> warning.
   */
  @Test
  public void testRawCompressIsZlib() {
    check("Developer`RawUncompress[Developer`RawCompress[{1, 2, 3, 250}]]", //
        "{1,2,3,250}");
    // a zlib stream starts with the byte 120 (0x78)
    check("First[Developer`RawCompress[{1, 2, 3}]]", //
        "120");
    check("Head[Developer`RawCompress[ByteArray[{1, 2, 3}]]]", //
        "ByteArray");
    check("ImportByteArray[ByteArray[Developer`RawUncompress[Normal[BaseDecode[BaseEncode["
        + "ByteArray[Developer`RawCompress[Normal[ExportByteArray[{1, 2.5, \"a\"}, "
        + "\"ExpressionJSON\"]]]]]]]]], \"ExpressionJSON\"]", //
        "{1,2.5,a}");
  }

  /**
   * <code>ExpressionJSON</code> writes exact numbers as numbers: an integer as a JSON number, a
   * rational and a complex number as a structure of numbers, as the Wolfram Language does.
   *
   * <p>
   * It is the format the WLJS notebook sends every result to the browser in. Integers used to be
   * written as strings - <code>["List","1","2"]</code> - which a reader takes for symbols named
   * <code>1</code> and <code>2</code>; <code>GrayLevel[1]</code> came back holding the symbol.
   */
  @Test
  public void testExpressionJSONWritesNumbersAsNumbers() {
    check("ExportString[{1, -3, 2^70, 1/2, 1.5, \"s\", x, True, I, 1 + 2 I}, \"ExpressionJSON\"]", //
        "[\"List\",1,-3,1180591620717411303424,[\"Rational\",1,2],1.5,\"'s'\",\"x\",true,"
            + "[\"Complex\",0,1],[\"Complex\",1,2]]");
    check("ExportString[5, \"ExpressionJSON\"]", //
        "5");
    check("ExportString[1/2, \"ExpressionJSON\"]", //
        "[\"Rational\",1,2]");
    check("ExportString[\"a\", \"ExpressionJSON\"]", //
        "\"'a'\"");
    check("e = {1, 2^70, -1/2, 1 + 2 I, 1.5, \"s\", GrayLevel[1], f[x]}; "
        + "ImportString[ExportString[e, \"ExpressionJSON\"], \"ExpressionJSON\"] === e", //
        "True");
    // what an older export wrote still reads back as the numbers it meant
    check("Head /@ ImportString[\"[\\\"List\\\",\\\"1\\\",\\\"-2\\\"]\", \"ExpressionJSON\"]", //
        "{Integer,Integer}");
  }

  /**
   * <code>a[key] =.</code> removes the key from the association <code>a</code> holds.
   *
   * <p>
   * The WLJS notebook drops cells, notifications and event handlers that way after nearly every
   * evaluation. Every such Unset said <code>Unset::norep</code> and kept the key.
   */
  @Test
  public void testUnsetRemovesAnAssociationKey() {
    check("a = <|\"k1\" -> 1, \"a76bf884-fac0-4ded-916d-439bbfd509af\" -> 2, \"k3\" -> 3|>; "
        + "a[\"k1\"] =.; a", //
        "<|a76bf884-fac0-4ded-916d-439bbfd509af->2,k3->3|>");
    check("k = \"a76bf884-fac0-4ded-916d-439bbfd509af\"; a[k] =.; a", //
        "<|k3->3|>");
    check("Module[{e = <|\"q\" -> 1, \"r\" -> 2|>}, e[\"q\"] =.; e]", //
        "<|r->2|>");
    // a key the association does not have is still reported, and nothing changes
    check("a[\"missing\"] =.; a", //
        "<|k3->3|>");
  }

  /**
   * <code>Import["!command", "Text"]</code> reads what a shell command prints, where the session
   * may run programs. The WLJS notebook asks the shell for the user's <code>PATH</code> that way
   * at startup and got <code>Import::noopen</code>.
   */
  @Test
  public void testImportReadsWhatACommandPrints() {
    boolean fileSystem = org.matheclipse.core.basic.Config.FILESYSTEM_ENABLED;
    boolean osAccess = org.matheclipse.core.basic.Config.OS_ACCESS_ENABLED;
    try {
      org.matheclipse.core.basic.Config.FILESYSTEM_ENABLED = true;
      org.matheclipse.core.basic.Config.OS_ACCESS_ENABLED = true;
      check("Import[\"!echo hello\", \"Text\"]", //
          "hello");
    } finally {
      org.matheclipse.core.basic.Config.FILESYSTEM_ENABLED = fileSystem;
      org.matheclipse.core.basic.Config.OS_ACCESS_ENABLED = osAccess;
    }
  }

  /**
   * <code>VectorPlot</code> and <code>VectorPlot3D</code> draw the field as arrows on a grid,
   * centred on the grid points; a zero vector gets no arrow. Both used to come back unevaluated,
   * so the notebook drew nothing.
   */
  @Test
  public void testVectorPlotDrawsArrows() {
    check("Head[VectorPlot[{x + y, y - x}, {x, -3, 3}, {y, -3, 3}]]", //
        "Graphics");
    check("Count[VectorPlot[{x + y, y - x}, {x, -3, 3}, {y, -3, 3}, VectorPoints -> 5], "
        + "_Arrow, Infinity]", //
        "24");
    check("Head[VectorPlot3D[{x, y, z}, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}]]", //
        "Graphics3D");
    check("Count[VectorPlot3D[{x, y, z}, {x, -1, 1}, {y, -1, 1}, {z, -1, 1}, VectorPoints -> 3], "
        + "_Arrow, Infinity]", //
        "26");
    check("Chop[Mean[First[Cases[VectorPlot[{1, 0}, {x, 0, 1}, {y, 0, 1}, VectorPoints -> 2], "
        + "Arrow[p_] :> p, Infinity]]]] == {0, 0}", //
        "True");
    check("Count[VectorPlot[{1, 0}, {x, 0, 1}, {y, 0, 1}, VectorColorFunction -> None], "
        + "_RGBColor, Infinity]", //
        "1");
    // a field held in a function is evaluated at each point too
    check("fld[a_, b_] := {-b, a}; Count[VectorPlot[fld[x, y], {x, -1, 1}, {y, -1, 1}, "
        + "VectorPoints -> 3], _Arrow, Infinity]", //
        "8");
  }

  /**
   * A subscript with several indices is a variable like one with a single index:
   * <code>Subscript[Y, 4, 0]</code> as well as <code>Subscript[Y, 4]</code>. The notebook writes
   * a typeset subscript that way.
   */
  @Test
  public void testASubscriptWithSeveralIndicesIsAVariable() {
    check("D[Subscript[x, 1, 2]^2, Subscript[x, 1, 2]] === 2*Subscript[x, 1, 2]", //
        "True");
    // one index still is
    check("D[Subscript[x, 1]^2, Subscript[x, 1]] === 2*Subscript[x, 1]", //
        "True");
  }

  /**
   * <code>Table</code>, <code>Sum</code>, <code>Product</code> and <code>Do</code> take a subscript
   * as the iterator variable, with one index or several. They answered "Raw object ... cannot be
   * used as an iterator".
   */
  @Test
  public void testASubscriptCanBeAnIteratorVariable() {
    check("Table[Subscript[a, 1]^2, {Subscript[a, 1], 3}]", //
        "{1,4,9}");
    check("Table[Subscript[a, 1, 2]^2, {Subscript[a, 1, 2], 3}]", //
        "{1,4,9}");
    check("Sum[Subscript[k, 1], {Subscript[k, 1], 1, 10}]", //
        "55");
    check("Product[Subscript[k, 1], {Subscript[k, 1], 1, 5}]", //
        "120");
    check("s = 0; Do[s += Subscript[i, 1], {Subscript[i, 1], 4}]; s", //
        "10");
    // a later iterator's bound may use an earlier subscript
    check("Table[Subscript[a, 1] + Subscript[a, 2], {Subscript[a, 1], 2}, "
        + "{Subscript[a, 2], Subscript[a, 1]}]", //
        "{{2},{3,4}}");
    // what stays symbolic still reads in terms of the subscript
    check("Sum[f[Subscript[k, 1]], {Subscript[k, 1], 1, n}]", //
        "Sum[f[Subscript[k,1]],{Subscript[k,1],1,n}]");
  }

  /**
   * A compiled function's parameters are variables holding the arguments, not values pasted into
   * the body. Pasted in, a Table over a list argument walked the whole list at every step: the
   * WLJS notebook unmasks each WebSocket frame that way, and a large frame hung the server.
   */
  @Test
  public void testCompiledTableOverAListArgumentIsLinear() {
    check("cc = Compile[{{p, _Integer, 1}}, Table[BitXor[p[[i]], 1], {i, 1, Length[p]}]]; "
        + "cc[{1, 2, 3}]", //
        "{0,3,2}");
    // quadratic in the length before: 10^8 steps (the test engine caps a list at 20000 elements)
    check("Length[cc[Range[10000]]]", //
        "10000");
    // the parameter is a local variable, whatever a global of the same name holds
    check("p = 99; cc[{1}]", //
        "{0}");
  }

  /**
   * <code>OptionValue[f, opts, name, h]</code> wraps the value in <code>h</code> before it is
   * evaluated, and the operator form <code>Apply[f]</code> is accepted quietly. WLJS's
   * <code>LeakyModule</code> uses the first, and both drew messages in the notebook server's log.
   */
  @Test
  public void testOptionValueWrapperAndApplyOperatorForm() {
    check("Options[og] = {\"Garbage\" :> ogv}; ogv = {1}; OptionValue[og, {}, \"Garbage\", Hold]", //
        "Hold[ogv]");
    check("oh[opts : OptionsPattern[{\"k\" :> ohv}]] := "
        + "OptionValue[Automatic, Automatic, \"k\", Hold]; oh[]", //
        "Hold[ohv]");
    check("Apply[f][{1, 2}]", //
        "f[1,2]");
  }

  /**
   * Whatever options a plot is given, its <code>Graphics</code> carries only options a front end
   * knows as options of a <code>Graphics</code>. The legend and the plot style, which Symja's own
   * renderer still reads, travel under <code>Method</code> as strings - and the SVG still draws the
   * legend.
   *
   * <p>
   * The WLJS notebook packs the options of a <code>Graphics</code> for the browser, which reported
   * "symbol PlotLegends is not defined" (and <code>PlotStyle</code>) under such plots.
   */
  @Test
  public void testAPlotCarriesOnlyGraphicsOptions() {
    check("Complement[Union @@ (First /@ Rest[List @@ If[Head[#] === Legended, First[#], #]] & /@ {"
        + "Plot[{Sin[x], Cos[x]}, {x, 0, 3}, PlotLegends -> Automatic], "
        + "Plot[Sin[x], {x, 0, 3}, PlotStyle -> Red], "
        + "ContourPlot[x y, {x, 0, 1}, {y, 0, 1}], DiscretePlot[n^2, {n, 1, 5}], "
        + "ComplexPlot[z, {z, -1 - I, 1 + I}, PlotLegends -> Automatic]}), "
        + "{Axes, AxesLabel, PlotLabel, AspectRatio, PlotRange, GridLines, Frame, FrameTicks, "
        + "Background, Epilog, ImageSize, Ticks, AxesOrigin, AxesStyle, FrameStyle, FrameLabel, "
        + "GridLinesStyle, ImagePadding, PlotRangePadding, Prolog, PlotRangeClipping, Method}]", //
        "{}");
    // Symja's own renderer still draws the legend: the label can only reach the SVG from the
    // PlotLegends it decoded out of Method, since that is now the only place the legend is kept
    check("lg = Plot[{Sin[x], Cos[x]}, {x, 0, 3}, PlotLegends -> {\"sine\", \"cosine\"}]; "
        + "{Head[lg], Head[lg[[2]]], lg[[2, 2]], "
        + "FreeQ[lg, PlotLegends | PlotStyle | Joined], "
        + "StringContainsQ[ExportString[lg, \"SVG\"], \"cosine\"]}", //
        "{Legended,LineLegend,{sine,cosine},True,True}");
    // a colour scale is written the way the Wolfram Language writes one
    check("MatchQ[ComplexPlot[z, {z, -1 - I, 1 + I}, PlotLegends -> Automatic], "
        + "Legended[_Graphics, BarLegend[{_, {_, _}}]]]", //
        "True");
  }

  /**
   * A plot's raster is numbers, as in the Wolfram Language: <code>{r, g, b}</code> cells, not
   * <code>RGBColor</code> objects, and the smoothing hint of a domain colouring under
   * <code>Method</code>. The WLJS notebook could not draw a <code>ComplexPlot</code> - ten thousand
   * colour objects - and reported <code>InterpolationOrder</code> as an undefined symbol.
   */
  @Test
  public void testAPlotRasterIsNumbers() {
    check("cp = ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2 I, 2 + 2 I}]; "
        + "rs = Cases[cp, _Raster, Infinity]; "
        + "{Length[rs], FreeQ[rs, InterpolationOrder | _RGBColor], "
        + "MatchQ[rs[[1, 1, 1, 1]], {_Real, _Real, _Real} | {_Real, _Real, _Real, _Real}], "
        + "Cases[rs, (Method -> m_) :> m, Infinity], "
        + "StringLength[ExportString[cp, \"SVG\"]] > 1000}", //
        "{1,True,True,{{InterpolationOrder->1}},True}");
    check("FreeQ[Cases[ArrayPlot[{{1, 0}, {0, 1}}], _Raster, Infinity], _RGBColor]", //
        "True");
  }

  /**
   * The option names a Wolfram Language front end knows are built-in System symbols, so a package
   * that writes one refers to that symbol and not to a new one in its own context.
   *
   * <p>
   * The WLJS notebook draws a colour-scale legend with <code>TickLabels -> {...}</code>, written in
   * a package. Without a built-in <code>TickLabels</code> the package made its own, and the browser
   * reported <code>CoffeeLiqueur`Extensions`Boxes`Workarounds`TickLabels</code> as undefined.
   */
  @Test
  public void testFrontEndOptionNamesAreSystemSymbols() {
    check("Map[Context, {ColorOutput, ControllerMethod, CurrentValue, "
        + "ImageSizeAction, Selectable, TickLabels, TransitionDuration}]", //
        "{System`,System`,System`,System`,System`,System`,System`}");
    // AutomaticImageSize is carried by Graphics3D in Mathematica but is no System symbol there:
    // Context[AutomaticImageSize] answers Global` in Mathematica too
    check("Context[AutomaticImageSize]", //
        "Global`");
    // written inside a package's private context, the name still means the built-in symbol. One
    // statement at a time, as a package file is read: a compound input is parsed before Begin runs
    check("BeginPackage[\"Wljs`Probe`\"]", //
        "");
    check("Begin[\"`Private`\"]", //
        "Wljs`Probe`Private`");
    check("Context[TickLabels]", //
        "System`");
    check("End[]", //
        "Wljs`Probe`Private`");
    check("EndPackage[]", //
        "");
  }

  /**
   * A parametric region is drawn translucent, with mesh lines of constant u and constant v over
   * it: <code>Mesh -> Automatic</code> (the default for a region) draws about fifteen each way,
   * <code>Mesh -> n</code> n, and <code>Mesh -> None</code> none. The region used to be opaque and
   * without a mesh, hiding the axes beneath it.
   */
  @Test
  public void testAParametricRegionHasAMesh() {
    check("pp = ParametricPlot[With[{z = u + I v}, {Re[z + 1/z], Im[z + 1/z]}], "
        + "{u, -1/2, 1/2}, {v, -1/2, 1/2}, PlotRange -> 5, Mesh -> Automatic]; "
        + "{Count[pp, _Line, Infinity] >= 20, MemberQ[pp, _Opacity, Infinity], "
        + "Count[ParametricPlot[{u, v}, {u, 0, 1}, {v, 0, 1}, Mesh -> None], _Line, Infinity], "
        + "Count[ParametricPlot[{u, v}, {u, 0, 1}, {v, 0, 1}, Mesh -> 3], _Line, Infinity], "
        + "Count[ParametricPlot[{u, v}, {u, 0, 1}, {v, 0, 1}], _Line, Infinity] > 0}", //
        "{True,True,0,6,True}");
  }

  /**
   * A plot writes <code>Ticks -> {Automatic, Automatic}</code>, one setting for each axis, as the
   * Wolfram Language does; the WLJS notebook reads only that shape and left the vertical axis of
   * every Symja plot unlabelled. A parametric region is written as plain polygons, which a front
   * end draws in the same layer as the mesh over it.
   */
  @Test
  public void testAPlotLabelsBothAxesAndDrawsItsRegionWithItsMesh() {
    check("Ticks /. Rest[List @@ Plot[Sin[x], {x, 0, 1}]]", //
        "{Automatic,Automatic}");
    check("pr = ParametricPlot[{u, v}, {u, 0, 1}, {v, 0, 1}]; "
        + "{FreeQ[pr, _GraphicsComplex], Count[pr, _Polygon, Infinity] > 0}", //
        "{True,True}");
  }

  /**
   * <code>ToBoxes</code> consults a <code>MakeBoxes</code> up-value for an object held as an atom
   * too - a <code>Graph</code> or a <code>ByteArray</code> - not only for a compound expression.
   * The WLJS notebook draws a graph through <code>Graph /: MakeBoxes[b_Graph, StandardForm]</code>,
   * and Symja's graph is such an atom, so it was printed as text.
   */
  @Test
  public void testToBoxesUsesAnUpValueForAnAtomicObject() {
    check("AtomQ[ByteArray[{1, 2}]]", //
        "True");
    // as the notebook does for Graph: Unprotect[Graph]; Graph /: MakeBoxes[...] := ...
    check("Unprotect[ByteArray]; ByteArray /: MakeBoxes[b_ByteArray, StandardForm] := \"bytes\"", //
        "");
    check("{ToBoxes[ByteArray[{1, 2}], StandardForm], ToBoxes[{ByteArray[{1, 2}]}, StandardForm]}", //
        "{bytes,RowBox[{{,RowBox[{bytes}],}}]}");
    check("ByteArray /: MakeBoxes[b_ByteArray, StandardForm] =.; "
        + "ToBoxes[ByteArray[{1, 2}], StandardForm] === \"bytes\"", //
        "False");
  }

  /**
   * <code>ListVectorPlot</code> draws a field given as data: an array of vectors, with
   * <code>array[[i, j]]</code> at <code>{j, i}</code>, or a list of <code>{point, vector}</code>
   * pairs. It had no evaluator, and the notebook reported <code>Take::seqs</code> for the
   * unevaluated call.
   */
  @Test
  public void testListVectorPlotDrawsDataAsArrows() {
    check("p = ListVectorPlot[Table[{y, -x}, {x, -3, 3}, {y, -3, 3}]]; "
        + "{Head[p], Count[p, _Arrow, Infinity]}", //
        "{Graphics,48}");
    check("Cases[ListVectorPlot[{{{1, 0}, {1, 0}, {1, 0}}}], Arrow[{a_, b_}] :> Round[(a + b)/2], Infinity]", //
        "{{1,1},{2,1},{3,1}}");
    check("Cases[ListVectorPlot[{{{0, 0}, {1, 0}}, {{2, 2}, {0, 1}}, {{4, 0}, {1, 1}}}], "
        + "Arrow[{a_, b_}] :> Round[(a + b)/2], Infinity]", //
        "{{0,0},{2,2},{4,0}}");
  }

  /**
   * <code>ListVectorPlot3D</code>: an array of 3-vectors puts <code>array[[i, j, k]]</code> at
   * <code>{k, j, i}</code>; a list of <code>{point, vector}</code> pairs puts each at its point.
   */
  @Test
  public void testListVectorPlot3DDrawsDataAsArrows() {
    check("p = ListVectorPlot3D[Table[{y, -x, z}, {z, -1, 1}, {y, -1, 1}, {x, -1, 1}]]; "
        + "{Head[p], Count[p, _Arrow, Infinity], Axes /. Rest[List @@ p]}", //
        "{Graphics3D,26,True}");
    check("Cases[ListVectorPlot3D[{{{{1, 0, 0}, {1, 0, 0}}}}], Arrow[{a_, b_}] :> Round[(a + b)/2], Infinity]", //
        "{{1,1,1},{2,1,1}}");
    check("Cases[ListVectorPlot3D[{{{0, 0, 0}, {1, 0, 0}}, {{2, 2, 2}, {0, 0, 1}}}], "
        + "Arrow[{a_, b_}] :> Round[(a + b)/2], Infinity]", //
        "{{0,0,0},{2,2,2}}");
  }

  /**
   * <code>Transpose[{}]</code> is <code>{}</code>. It stayed unevaluated, printed like
   * <code>{}</code> but had length 1, so the notebook's
   * <code>Select[Transpose[{keys, values}], ...] // Transpose</code> never looked empty and built
   * <code>Rule[{}]</code> on every notebook it opened.
   */
  @Test
  public void testTransposeOfTheEmptyListIsTheEmptyList() {
    check("{Transpose[{}], Length[Transpose[{}]], Transpose[Transpose[{{}, {}}]] === {}}", //
        "{{},0,True}");
    check("Length[Transpose[Select[{{\"a\", $Failed}}, !FailureQ[#[[2]]] &]]]", //
        "0");
  }

  /**
   * <code>StreamPlot</code> draws the field as streamlines, each an <code>Arrow</code> through the
   * points of a curve that follows the field, over the plot range the iterators give, as the
   * Wolfram Language draws it.
   */
  @Test
  public void testStreamPlotDrawsStreamlines() {
    check("p = StreamPlot[{-1 - x^2 + y, 1 + x - y^2}, {x, -3, 3}, {y, -3, 3}, StreamScale -> Large]; "
        + "{Head[p], Count[p, _Arrow, Infinity] > 20, Min[Cases[p, Arrow[l_] :> Length[l], Infinity]] >= 2, "
        + "PlotRange /. Rest[List @@ p]}", //
        "{Graphics,True,True,{{-3.0,3.0},{-3.0,3.0}}}");
  }

  /**
   * <code>ListVectorPlot</code> thins a dense array to at most 15 entries along each axis and plots
   * the extent of the data, as the Wolfram Language does: 31 x 31 vectors are 15 x 15 arrows over
   * <code>{{1, 31}, {1, 31}}</code>.
   */
  @Test
  public void testListVectorPlotThinsADenseArray() {
    check("p = ListVectorPlot[Table[{y, -x}, {x, -3, 3, 0.2}, {y, -3, 3, 0.2}]]; "
        + "{Count[p, _Arrow, Infinity], PlotRange /. Rest[List @@ p]}", //
        "{225,{{1.0,31.0},{1.0,31.0}}}");
  }

}
