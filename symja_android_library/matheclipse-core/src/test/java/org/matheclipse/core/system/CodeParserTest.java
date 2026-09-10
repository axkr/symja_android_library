package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>CodeParser`</code>: reading Wolfram Language source as source.
 *
 * <p>
 * The names print in lower case here because this suite runs in relaxed syntax, where every symbol
 * name is folded; under the Wolfram Language syntax the console uses they read
 * <code>LeafNode[Token`Symbol, …]</code>.
 *
 * <p>
 * The use these have to serve is cutting a file into the pieces between its top-level expressions,
 * which is how a template engine reads a <code>.wlx</code> file. That needs three things to be
 * right: the tokens tile the source exactly, a bracket gathers what is inside it into a group, and
 * a newline written inside a bracket, a string or a comment is therefore not a top-level one.
 */
public class CodeParserTest extends ExprEvaluatorTestCase {

  /** WLX's own SplitExpression, which is the thing these functions exist to serve. */
  private void defineSplitExpression() {
    check("Needs(\"CodeParser`\")", //
        "");
    check("SplitExpression(astr_) := With({str = astr},"
        + " Select(Select((StringTake(str, Partition(Join({1}, #, {StringLength(str)}), 2)) &)@"
        + " Flatten({#1 - 1, #2 + 1} & @@@ Sort@Cases("
        + "   CodeParser`CodeConcreteParse(str, CodeParser`SourceConvention -> \"SourceCharacterIndex\")[[2]],"
        + "   LeafNode(Token`Newline, _, a_) :> Lookup(a, Source, Nothing))),"
        + " StringQ), (StringLength(#) > 0) &))", //
        "");
  }

  @Test
  public void testTokensTileTheSource() {
    check("Needs(\"CodeParser`\")", //
        "");
    // every character belongs to exactly one token, so the texts join back into the source
    check("StringJoin(CodeTokenize(\"a = 1\")[[All, 2]])", //
        "a = 1");
    check("CodeTokenize(\"a = 1\")[[All, 1]]", //
        "{Token`symbol,Token`whitespace,Token`operator,Token`whitespace,Token`integer}");
    // and each one says where it is
    check("CodeTokenize(\"ab\", CodeParser`SourceConvention -> \"SourceCharacterIndex\")[[1, 3, Key(Source)]]", //
        "{1,2}");
  }

  @Test
  public void testBracketsGatherWhatIsInsideThem() {
    check("Needs(\"CodeParser`\")", //
        "");
    check("Head(CodeParser`CodeConcreteParse(\"f[1]\")[[2, 2]])", //
        "groupnode");
    check("CodeParser`CodeConcreteParse(\"f[1]\")[[2, 2, 1]]", //
        "groupsquare");
    // the group covers the whole bracketed run
    check("CodeParser`CodeConcreteParse(\"f[1]\", CodeParser`SourceConvention -> \"SourceCharacterIndex\")[[2, 2, 3, Key(Source)]]", //
        "{2,4}");
  }

  @Test
  public void testOnlyTopLevelNewlinesAreChildrenOfTheContainer() {
    check("Needs(\"CodeParser`\")", //
        "");
    // the newline inside f[x,\ny] is inside the group, so it is not one of these
    check("Cases(CodeParser`CodeConcreteParse(\"a = 1\\nf[x,\\ny]\\nb = 2\","
        + " CodeParser`SourceConvention -> \"SourceCharacterIndex\")[[2]],"
        + " LeafNode(Token`Newline, _, a_) :> Lookup(a, Source, Nothing))", //
        "{{6,6},{14,14}}");
  }

  @Test
  public void testSplittingSourceAtItsTopLevelNewlines() {
    defineSplitExpression();
    check("SplitExpression(\"a = 1\\nb = 2\\nc = 3\")", //
        "{a = 1,b = 2,c = 3}");
    // a newline inside a bracket, a comment or a string does not split anything
    check("SplitExpression(\"f[x,\\ny]\\ng = 1\") // Length", //
        "2");
    check("SplitExpression(\"(* over\\ntwo lines *)\\nx = 1\") // Length", //
        "2");
    check("SplitExpression(\"s = \\\"text\\nin a string\\\"\\ny = 2\") // Length", //
        "2");
  }

  @Test
  public void testALineBreakInsideAnExpressionDoesNotSplitIt() {
    defineSplitExpression();
    // Components/FakeMenuBrowser.wlx is written
    //   Component[OptionsPattern[]] :=
    //   With[{...}, ...]
    // and splitting it there left a definition of nothing followed by a loose body, which then
    // ran with no options around it - the whole menu came back as OptionValue["Plugins"].
    check("SplitExpression(\"f(x_) := \\nWith({y = 1}, y)\\ng(z_) := z\")", //
        "{f(x_) := \nWith({y = 1}, y),g(z_) := z}");
    // an operator waiting for its right-hand side keeps the expression open across blank lines
    check("SplitExpression(\"h(x_) :=\\n\\n  x + 1\\nk = 2\") // Length", //
        "2");
    check("SplitExpression(\"s = \\\"one\\\" <>\\n\\\"two\\\"\\nt = 1\") // Length", //
        "2");
    // ...but an expression which is finished still ends at the line break
    check("SplitExpression(\"a = 1\\nb = 2\") // Length", //
        "2");
    check("SplitExpression(\"a = 1;\\nb = 2\") // Length", //
        "2");
    check("SplitExpression(\"f(1) &\\ng = 2\") // Length", //
        "2");
    // the line break which does not end anything is not a Token`Newline at the top level
    check("Cases(CodeParser`CodeConcreteParse(\"a :=\\n1\\nb = 2\","
        + " CodeParser`SourceConvention -> \"SourceCharacterIndex\")[[2]],"
        + " LeafNode(Token`Newline, _, x_) :> Lookup(x, Source, Nothing))", //
        "{{7,7}}");
  }

  @Test
  public void testStringTakeWithSeveralSpans() {
    // what SplitExpression cuts the file up with
    check("StringTake(\"abcdefgh\", {{1, 3}, {5, 6}})", //
        "{abc,ef}");
    check("StringTake(\"abcdefgh\", {2, 4})", //
        "bcd");
  }

  @Test
  public void testCodeParseSaysWhatTheSourceMeans() {
    check("Needs(\"CodeParser`\")", //
        "");
    check("CodeParse(\"f[1]\")[[2, 1, 1]]", //
        "leafnode(Token`symbol,f,<||>)");
    check("Head(CodeParse(\"f[1]\")[[2, 1]])", //
        "callnode");
    // source that will not read says so rather than taking the caller down
    check("Head(CodeParse(\"f[1\")[[2, 1]])", //
        "errornode");
  }
}
