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
    check("FileNameJoin[FileNameSplit[\"/Users/someone/x.wl\"]]", //
        "/Users/someone/x.wl");
    check("FileNameJoin[{\"\", \"Users\", \"someone\"}]", //
        "/Users/someone");
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
}
