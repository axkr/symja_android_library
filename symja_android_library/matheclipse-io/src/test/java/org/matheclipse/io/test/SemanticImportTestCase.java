package org.matheclipse.io.test;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;

public class SemanticImportTestCase extends AbstractTestCase {

  /**
   * The expected tables below were recorded on Windows and so end their lines with
   * <code>\r\n</code>: <code>tech.tablesaw.io.string.DataFramePrinter</code> writes
   * {@link System#lineSeparator()}. That is the whole reason these tests used to sit behind an
   * <code>os.name</code> check and only ever ran on Windows.
   *
   * <p>
   * Translating the recorded separator to the platform's own keeps one set of expectations exact
   * everywhere, rather than normalizing the actual output - which would also hide a stray line
   * break the printer should not have produced.
   */
  @Override
  public void check(String evalString, String expectedResult) {
    super.check(evalString, expectedResult.replace("\r\n", System.lineSeparator()));
  }

  @Test
  public void testSemanticImport() {

    Config.FILESYSTEM_ENABLED = true;
    // check("ds=SemanticImport(\"./data/color2_data.csv\") //Normal //InputForm", //
    // "");
    // check("ds=SemanticImport(\"./data/color2_data.csv\");ds(All, {\"r\",\"g\",\"b\"})//Normal
    // //Values
    // //InputForm", //
    // "");

    // check(
    // "dset =
    // SemanticImport(\"https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/data/whiskey.csv\")",
    // //
    // "");
    check("dset = Dataset({\n" + //
        "<|\"a\" -> 1, \"b\" -> \"x\", \"c\" -> {1}|>,\n" + //
        "<|\"a\" -> 2, \"b\" -> \"y\", \"c\" -> {2, 3}|>,\n" + //
        "<|\"a\" -> 3, \"b\" -> \"z\", \"c\" -> {3}|>,\n" + //
        "<|\"a\" -> 4, \"b\" -> \"x\", \"c\" -> {4, 5}|>,\n" + //
        "<|\"a\" -> 5, \"b\" -> \"y\", \"c\" -> {5, 6, 7}|>,\n" + //
        "<|\"a\" -> 6, \"b\" -> \"z\", \"c\" -> {}|>})", //
        " a  |  b  |     c     |\r\n" + //
            "-----------------------\r\n" + //
            " 1  |  x  |      {1}  |\r\n" + //
            " 2  |  y  |    {2,3}  |\r\n" + //
            " 3  |  z  |      {3}  |\r\n" + //
            " 4  |  x  |    {4,5}  |\r\n" + //
            " 5  |  y  |  {5,6,7}  |\r\n" + //
            " 6  |  z  |       {}  |");

    check("Normal(dset) //InputForm", //
        "{<|\"a\"->1,\"b\"->\"x\",\"c\"->{1}|>," //
            + "<|\"a\"->2,\"b\"->\"y\",\"c\"->{2,3}|>," //
            + "<|\"a\"->3,\"b\"->\"z\",\"c\"->{3}|>," //
            + "<|\"a\"->4,\"b\"->\"x\",\"c\"->{4,5}|>," //
            + "<|\"a\"->5,\"b\"->\"y\",\"c\"->{5,6,7}|>," //
            + "<|\"a\"->6,\"b\"->\"z\",\"c\"->{}|>}");

    check("dset[2,3]", //
        "    |\r\n" + //
            "-----\r\n" + //
            " 2  |\r\n" + //
            " 3  |");

    // a row reads down the page, its field names in the first column
    check("dset[2]", //
        "    |         |\r\n" + //
            "---------------\r\n" + //
            " a  |      2  |\r\n" + //
            " b  |      y  |\r\n" + //
            " c  |  {2,3}  |");

    check("dset[5, \"c\"]", //
        "    |\r\n" + //
            "-----\r\n" + //
            " 5  |\r\n" + //
            " 6  |\r\n" + //
            " 7  |");

    // a column named on its own is a vector - it has no field name left to show
    check("dset[All, \"a\"]", //
        "    |\r\n" + //
            "-----\r\n" + //
            " 1  |\r\n" + //
            " 2  |\r\n" + //
            " 3  |\r\n" + //
            " 4  |\r\n" + //
            " 5  |\r\n" + //
            " 6  |");

    check("dset[All, \"c\", 1]", //
        "                        |\r\n" + //
            "-------------------------\r\n" + //
            "                     1  |\r\n" + //
            "                     2  |\r\n" + //
            "                     3  |\r\n" + //
            "                     4  |\r\n" + //
            "                     5  |\r\n" + //
            " Missing(PartAbsent,1)  |");

    check("dset[2] // Normal // InputForm", //
        "<|\"a\"->2,\"b\"->\"y\",\"c\"->{2,3}|>");

    check("dset[1;;3]", //
        " a  |  b  |    c    |\r\n" + //
            "---------------------\r\n" + //
            " 1  |  x  |    {1}  |\r\n" + //
            " 2  |  y  |  {2,3}  |\r\n" + //
            " 3  |  z  |    {3}  |");

    check("dset[1;;4,{\"a\",\"b\"}]", //
        " a  |  b  |\r\n" + //
            "-----------\r\n" + //
            " 1  |  x  |\r\n" + //
            " 2  |  y  |\r\n" + //
            " 3  |  z  |\r\n" + //
            " 4  |  x  |");

    // check("Normal(SemanticImport(\"./data/test.csv\"))", //
    // "{<|Products->a,Sales->5500,Market_Share->3|>,<|Products->b,Sales->12200,Market_Share->\n"
    // + //
    // "4|>,<|Products->c,Sales->60000,Market_Share->33|>}");
    //
    // check("ds=SemanticImport(\"./data/test.csv\");", //
    // "");

    // check(
    // "ds=SemanticImport(\"./data/tornadoes_1950-2014.csv\");", //
    // "");
    // check(
    // "ExportString(ds, \"csv\");", //
    // "");
  }

  @Test
  public void testSemanticImportString() {
    check("SemanticImportString(\"Products,Sales,Market_Share\n" + //
        "a,5500,3\n" + //
        "b,12200,4\n" + //
        "c,60000,33\n" + //
        "\")", //
        "                                       \r\n" + //
            " Products  |  Sales  |  Market_Share  |\r\n" + //
            "---------------------------------------\r\n" + //
            "        a  |   5500  |             3  |\r\n" + //
            "        b  |  12200  |             4  |\r\n" + //
            "        c  |  60000  |            33  |");
    check("SemanticImportString(\"Date\\tCity\\tSales\r\n" + //
        " 2014/1/1\\tBoston\\t198\r\n" + //
        " 2014/1/1\\tNew York\\t220\r\n" + //
        " 2014/1/1\\tParis\\t215\r\n" + //
        " 2014/1/1\\tLondon\\t225\r\n" + //
        " 2014/1/1\\tShanghai\\t241\r\n" + //
        " 2014/1/1\\tTokio\\t218\r\n" + //
        " 2014/1/2\\tBoston\\t189\r\n" + //
        " 2014/1/2\\tNew York\\t232\r\n" + //
        " 2014/1/2\\tParis\\t211\r\n" + //
        " 2014/1/2\\tLondon\\t228\r\n" + //
        " 2014/1/2\\tShanghai\\t242\r\n" + //
        " 2014/1/2\\tTokio\\t229\r\n" + //
        " 2014/1/3\\tBoston\\t196\r\n" + //
        " 2014/1/3\\tNew York\\t235\")", //
        "                                   \r\n" + "   Date    |    City    |  Sales  |\r\n"
            + "-----------------------------------\r\n"
            + " 2014/1/1  |    Boston  |    198  |\r\n"
            + " 2014/1/1  |  New York  |    220  |\r\n"
            + " 2014/1/1  |     Paris  |    215  |\r\n"
            + " 2014/1/1  |    London  |    225  |\r\n"
            + " 2014/1/1  |  Shanghai  |    241  |\r\n"
            + " 2014/1/1  |     Tokio  |    218  |\r\n"
            + " 2014/1/2  |    Boston  |    189  |\r\n"
            + " 2014/1/2  |  New York  |    232  |\r\n"
            + " 2014/1/2  |     Paris  |    211  |\r\n"
            + " 2014/1/2  |    London  |    228  |\r\n"
            + " 2014/1/2  |  Shanghai  |    242  |\r\n"
            + " 2014/1/2  |     Tokio  |    229  |\r\n"
            + " 2014/1/3  |    Boston  |    196  |\r\n" + " 2014/1/3  |  New York  |    235  |");

    check("ds=SemanticImportString(\"Products,Sales,Market_Share,Date,Time\n" + //
        "a,12200,4,1950-01-03,11:10:00\n" + //
        "b,5500,3,1970-12-31,23:10:00\n" + //
        "c,60000,33,2020-04-18,11:35:36\n" + //
        "\")", //
        "                                                                   \r\n" + //
            " Products  |  Sales  |  Market_Share  |     Date     |    Time    |\r\n" + //
            "-------------------------------------------------------------------\r\n" + //
            "        a  |  12200  |             4  |  1950-01-03  |  11:10:00  |\r\n" + //
            "        b  |   5500  |             3  |  1970-12-31  |  23:10:00  |\r\n" + //
            "        c  |  60000  |            33  |  2020-04-18  |  11:35:36  |");
    check("st=Structure(ds)", //
        "              Structure of                \r\n" + //
            " Index  |  Column Name   |  Column Type  |\r\n" + //
            "------------------------------------------\r\n" + //
            "     0  |      Products  |       STRING  |\r\n" + //
            "     1  |         Sales  |      INTEGER  |\r\n" + //
            "     2  |  Market_Share  |      INTEGER  |\r\n" + //
            "     3  |          Date  |   LOCAL_DATE  |\r\n" + //
            "     4  |          Time  |   LOCAL_TIME  |");
    check("st(Select(Slot(\"Column Type\") == \"INTEGER\" &))", //
        "              Structure of                \r\n" + //
            " Index  |  Column Name   |  Column Type  |\r\n" + //
            "------------------------------------------\r\n" + //
            "     1  |         Sales  |      INTEGER  |\r\n" + //
            "     2  |  Market_Share  |      INTEGER  |"); //
    check("st(Select(#\"Column Type\" == \"INTEGER\" &))", //
        "              Structure of                \r\n" + //
            " Index  |  Column Name   |  Column Type  |\r\n" + //
            "------------------------------------------\r\n" + //
            "     1  |         Sales  |      INTEGER  |\r\n" + //
            "     2  |  Market_Share  |      INTEGER  |"); //
    check("Summary(ds)", //
        "                                                                                                 \r\n"
            + //
            "  Summary   |  Products  |        Sales         |     Market_Share     |     Date     |  Time   |\r\n"
            + //
            "-------------------------------------------------------------------------------------------------\r\n"
            + //
            "     Count  |         3  |                   3  |                   3  |           3  |      3  |\r\n"
            + //
            "    Unique  |         3  |                      |                      |              |         |\r\n"
            + //
            "       Top  |         a  |                      |                      |              |         |\r\n"
            + //
            " Top Freq.  |         1  |                      |                      |              |         |\r\n"
            + //
            "       sum  |            |               77700  |                  40  |              |         |\r\n"
            + //
            "      Mean  |            |               25900  |  13.333333333333334  |              |         |\r\n"
            + //
            "       Min  |            |                5500  |                   3  |              |         |\r\n"
            + //
            "       Max  |            |               60000  |                  33  |              |         |\r\n"
            + //
            "     Range  |            |               54500  |                  30  |              |         |\r\n"
            + //
            "  Variance  |            |           883330000  |  290.33333333333337  |              |         |\r\n"
            + //
            "  Std. Dev  |            |  29720.868089610034  |  17.039170558842745  |              |         |\r\n"
            + //
            "   Missing  |            |                      |                      |           0  |      0  |\r\n"
            + //
            "  Earliest  |            |                      |                      |  1950-01-03  |  11:10  |\r\n"
            + //
            "    Latest  |            |                      |                      |  2020-04-18  |  23:10  |");
    check("First(ds)", //
        "               |              |\r\n" + //
            "-------------------------------\r\n" + //
            "     Products  |           a  |\r\n" + //
            "        Sales  |       12200  |\r\n" + //
            " Market_Share  |           4  |\r\n" + //
            "         Date  |  1950-01-03  |\r\n" + //
            "         Time  |    11:10:00  |");
    // Keys gives a dataset of the keys of *each row*, not the column names once: the reference
    // answers Keys[dataset] with Dataset[{{"a","b"}, {"a","b"}, …}]
    check("Head(Keys(ds))", //
        "Dataset");
    check("Length(Normal(Keys(ds)))", //
        "3");
    check("Normal(Keys(ds))[[1]] // InputForm", //
        "{\"Products\",\"Sales\",\"Market_Share\",\"Date\",\"Time\"}");
    check("ds[[1,2]]", //
        "12200");
    check("ds(TakeLargest(2), \"Sales\") ", //
        "        |\r\n" + //
            "---------\r\n" + //
            " 60000  |\r\n" + //
            " 12200  |");
    // TODO rewrite GroupBy
    check("ds(GroupBy(\"Sales\"), \"Sales\") ", //
        "        |\r\n" + //
            "---------\r\n" + //
            "  5500  |\r\n" + //
            " 12200  |\r\n" + //
            " 60000  |");
    // TODO rewrite SortBy
    check("ds(SortBy(\"Sales\"), \"Sales\") ", //
        "        |\r\n" + //
            "---------\r\n" + //
            "  5500  |\r\n" + //
            " 12200  |\r\n" + //
            " 60000  |");
    check("ds(Select(#Sales < 13000 &), {\"Products\", \"Market_Share\"})", //
        "                             \r\n" + //
            " Products  |  Market_Share  |\r\n" + //
            "-----------------------------\r\n" + //
            "        a  |             4  |\r\n" + //
            "        b  |             3  |");
    // a select that matches one row leaves a record, which reads down the page
    check("ds(Select(#Products == \"a\" &), {\"Products\", \"Market_Share\"})", //
        "               |     |\r\n" + //
            "----------------------\r\n" + //
            "     Products  |  a  |\r\n" + //
            " Market_Share  |  4  |");
    // print: "Dataset: Column Invalid is not present in table"
    check("ds(Select(#Invalid < 13000 &) ,All)", //
        "                                                                   \r\n" + //
            " Products  |  Sales  |  Market_Share  |     Date     |    Time    |\r\n" + //
            "-------------------------------------------------------------------\r\n" + //
            "        a  |  12200  |             4  |  1950-01-03  |  11:10:00  |\r\n" + //
            "        b  |   5500  |             3  |  1970-12-31  |  23:10:00  |\r\n" + //
            "        c  |  60000  |            33  |  2020-04-18  |  11:35:36  |[Select(Slot(Invalid)<13000&),All]");
    check("ds(All, \"Sales\") // Normal", //
        "{12200,5500,60000}");

    check("ds(Counts, \"Sales\")", //
        "        |     |\r\n" + //
            "---------------\r\n" + //
            " 60000  |  1  |\r\n" + //
            " 12200  |  1  |\r\n" + //
            "  5500  |  1  |");

    check("ds(Total, \"Sales\")", //
        "77700");

    check("ds(Mean, \"Sales\")", //
        "25900");
    check("ds(Median, \"Sales\")", //
        "12200");
    check("ds(StandardDeviation, \"Sales\")", //
        "100*Sqrt(88333)");

    check("ds(StringJoin, \"Products\")", //
        "abc");

    check("ds(3, \"Sales\")", //
        "60000");

    // all rows of column Market_Share
    check("ds(All, \"Market_Share\")", //
        "     |\r\n" + //
            "------\r\n" + //
            "  4  |\r\n" + //
            "  3  |\r\n" + //
            " 33  |");

    // all rows - Column 1 and 2
    check("ds(All,1;;2)", //
        "                      \r\n" + //
            " Products  |  Sales  |\r\n" + //
            "----------------------\r\n" + //
            "        a  |  12200  |\r\n" + //
            "        b  |   5500  |\r\n" + //
            "        c  |  60000  |");

    // rows 2 and 3
    check("ds(2;;3)", //
        "                                                                   \r\n" + //
            " Products  |  Sales  |  Market_Share  |     Date     |    Time    |\r\n" + //
            "-------------------------------------------------------------------\r\n" + //
            "        b  |   5500  |             3  |  1970-12-31  |  23:10:00  |\r\n" + //
            "        c  |  60000  |            33  |  2020-04-18  |  11:35:36  |");

    // row 2
    check("ds(2)", //
        "               |              |\r\n" + //
            "-------------------------------\r\n" + //
            "     Products  |           b  |\r\n" + //
            "        Sales  |        5500  |\r\n" + //
            " Market_Share  |           3  |\r\n" + //
            "         Date  |  1970-12-31  |\r\n" + //
            "         Time  |    23:10:00  |");

    // The date and time columns come out as DateObject and TimeObject, the way every other test
    // of those two records them. The bare "1970-12-31T00:00" this expected is what
    // DateObjectExpr printed years ago; nothing caught the drift because the whole test was
    // pinned to Windows. The \n is the output form's own wrap and is a literal newline on every
    // platform - see OutputFormFactory#newLine.
    check("ds(2) // Normal", //
        "<|Products->b,Sales->5500,Market_Share->3,"
            + "Date->DateObject({1970,12,31,0,0,0},Instant,Gregorian,None),Time->TimeObject({\n"
            + "23,10,0},Instant)|>");

    // row 3 column 2
    check("ds(3, 2)", //
        "60000");

    // all rows column 2
    check("ds(All, 2)", //
        "        |\r\n" + //
            "---------\r\n" + //
            " 12200  |\r\n" + //
            "  5500  |\r\n" + //
            " 60000  |");

    // all rows column 1 and 2
    check("ds(All,{1,2})", //
        "                      \r\n" + //
            " Products  |  Sales  |\r\n" + //
            "----------------------\r\n" + //
            "        a  |  12200  |\r\n" + //
            "        b  |   5500  |\r\n" + //
            "        c  |  60000  |");

    check("ds(All,{\"Products\", \"Market_Share\"})", //
        "                             \r\n" + //
            " Products  |  Market_Share  |\r\n" + //
            "-----------------------------\r\n" + //
            "        a  |             4  |\r\n" + //
            "        b  |             3  |\r\n" + //
            "        c  |            33  |");
    check("ds/.x->3", //
        "                                                                   \r\n" + //
            " Products  |  Sales  |  Market_Share  |     Date     |    Time    |\r\n" + //
            "-------------------------------------------------------------------\r\n" + //
            "        a  |  12200  |             4  |  1950-01-03  |  11:10:00  |\r\n" + //
            "        b  |   5500  |             3  |  1970-12-31  |  23:10:00  |\r\n" + //
            "        c  |  60000  |            33  |  2020-04-18  |  11:35:36  |");
  }

  @Test
  public void testSemanticImportStringToList() {
    check(
        "SemanticImportString(\"1 2, 3 ; 4, 5 6\", \"String\", \"Columns\", Delimiters -> \",\") // InputForm", //
        "{{\"1 2\",\" 3 ; 4\",\" 5 6\"}}");
    check(
        "SemanticImportString(\"1 2, 3 ; 4, 5 6\", \"String\", \"Columns\", Delimiters -> \";\") // InputForm", //
        "{{\"1 2, 3 \",\" 4, 5 6\"}}");

    check(
        "SemanticImportString(\"1 2, 3 ; 4, 5 6\", \"String\", \"List\", Delimiters -> \",\") // InputForm", //
        "{\"1 2\",\" 3 ; 4\",\" 5 6\"}");
    check(
        "SemanticImportString(\"1 2, 3 ; 4, 5 6\", \"String\", \"List\", Delimiters -> \";\") // InputForm", //
        "{\"1 2, 3 \",\" 4, 5 6\"}");
  }

  @Test
  public void testSemanticImportStringWikipedia() {
    // https://en.wikipedia.org/wiki/Comma-separated_values
    check("SemanticImportString(\"Year,Make,Model,Description,Price\n" + //
        "1997,Ford,E350,\\\"ac, abs, moon\\\",3000.00\n" + //
        "1999,Chevy,\\\"Venture \\\"\\\"Extended Edition\\\"\\\"\\\",\\\"\\\",4900.00\n" + //
        "1999,Chevy,\\\"Venture \\\"\\\"Extended Edition, Very Large\\\"\\\"\\\",,5000.00\n" + //
        "1996,Jeep,Grand Cherokee,\\\"MUST SELL!\n" + //
        "air, moon roof, loaded\\\",4799.00\n" + //
        "\")", //
        "                                                                                                             \r\n"
            + " Year  |  Make   |                  Model                   |             Description             |  Price  |\r\n"
            + "-------------------------------------------------------------------------------------------------------------\r\n"
            + " 1997  |   Ford  |                                    E350  |                      ac, abs, moon  |   3000  |\r\n"
            + " 1999  |  Chevy  |              Venture \"Extended Edition\"  |                                     |   4900  |\r\n"
            + " 1999  |  Chevy  |  Venture \"Extended Edition, Very Large\"  |                                     |   5000  |\r\n"
            + " 1996  |   Jeep  |                          Grand Cherokee  |  MUST SELL!\n"
            + "air, moon roof, loaded  |   4799  |");
  }

  /**
   * The functions built on the dataset object rather than on its rendering. They assert through
   * <code>Normal</code> wherever they can: the table layout is whitespace to the column, and
   * asserting on it would break on any change to a column width.
   */
  @Test
  public void testDatasetFunctions() {
    Config.FILESYSTEM_ENABLED = true;
    check("l = SemanticImportString(\"id,city,sales\n1,Boston,100\n2,Berlin,200\n3,Boston,50\n\");"
        + "r = SemanticImportString(\"id,owner\nann,1\nbob,2\n\");Head(l)", //
        "Dataset");

    // ToDataset takes a header row plus data rows, which the Dataset(...) head has no form for
    check("FromDataset(ToDataset({{\"a\",\"b\"},{1,2},{3,4}}))", //
        "{<|a->1,b->2|>,<|a->3,b->4|>}");
    check("Head(ToDataset(<|101 -> <|\"t\" -> 42|>|>))", //
        "Dataset");
    // already a dataset: a no-op rather than a failure
    check("Head(ToDataset(ToDataset({{\"a\"},{1}})))", //
        "Dataset");
    check("FromDataset(17)", //
        "FromDataset(17)");

    // an inner join on the column the two have in common
    check("Normal(JoinAcross(l, SemanticImportString(\"id,owner\n1,ann\n2,bob\n\")))", //
        "{<|id->1,city->Boston,sales->100,owner->ann|>,<|id->2,city->Berlin,sales->200,owner->bob|>}");
    // a left join keeps the row that has no partner
    check(
        "Normal(JoinAcross(l, SemanticImportString(\"id,owner\n1,ann\n2,bob\n\"), \"id\", \"Left\")) // Length", //
        "3");
    check("JoinAcross(l, ToDataset({{\"zzz\"},{1}})) // Head", //
        "JoinAcross");

    check("Normal(AggregateBy(l, \"city\", \"sales\", Total))", //
        "{<|city->Boston,Sum [sales]->150.0|>,<|city->Berlin,Sum [sales]->200.0|>}");
    check("Normal(AggregateBy(l, {\"city\"}, \"sales\", Mean))", //
        "{<|city->Boston,Mean [sales]->75.0|>,<|city->Berlin,Mean [sales]->200.0|>}");
    check("AggregateBy(l, \"city\", \"sales\", Sin) // Head", //
        "AggregateBy");

    check("Length(Normal(TableView(l, 2)))", //
        "2");
    check("Head(TableView({{\"a\"},{1},{2}}, 1))", //
        "Dataset");

    // the display options are accepted and ignored; an unknown one is not
    check("Head(Dataset(<|1 -> <|\"t\" -> 42|>|>, MaxItems -> 5, ItemStyle -> Bold))", //
        "Dataset");
    check("Options(Dataset) // Length", //
        "15");

    // a query the dataset cannot answer is a Failure object, not an unevaluated expression
    check("Head(l(1;;99))", //
        "Failure");
    check("l(1;;99)[[1]]", //
        "DatasetQueryFailure");
  }

  /**
   * The display options. Asserted through <code>StringContainsQ</code> of the rendering rather than
   * on the rendering itself: the table pads its columns to their widest value, so a literal
   * expectation would break on any change to the data.
   */
  @Test
  public void testDatasetDisplayOptions() {
    Config.FILESYSTEM_ENABLED = true;
    check("rows = {<|\"city\" -> \"Boston\", \"sales\" -> 100, \"rank\" -> 1|>,"
        + "<|\"city\" -> \"Berlin\", \"sales\" -> 200, \"rank\" -> 2|>,"
        + "<|\"city\" -> \"Oslo\", \"sales\" -> 50, \"rank\" -> 3|>};Head(Dataset(rows))", //
        "Dataset");

    // MaxItems drops rows, and {rows, columns} drops columns too
    check("StringContainsQ(ToString(Dataset(rows, MaxItems -> 2)), \"Oslo\")", //
        "False");
    check("StringContainsQ(ToString(Dataset(rows, MaxItems -> 2)), \"Berlin\")", //
        "True");
    check("StringContainsQ(ToString(Dataset(rows, MaxItems -> {3, 2})), \"rank\")", //
        "False");
    // ... but only the display: the data is all still there
    check("Length(Normal(Dataset(rows, MaxItems -> 2)))", //
        "3");

    check("StringContainsQ(ToString(Dataset(rows, HiddenItems -> \"rank\")), \"rank\")", //
        "False");
    check("StringContainsQ(ToString(Dataset(rows, HiddenItems -> {\"rank\", \"sales\"})), \"sales\")", //
        "False");

    check("StringContainsQ(ToString(Dataset(rows, HeaderDisplayFunction -> ToUpperCase)), \"CITY\")", //
        "True");
    check("StringContainsQ(ToString(Dataset(rows, ItemDisplayFunction -> Function(\"<\" <> # <> \">\"))),"
        + " \"<Boston>\")", //
        "True");

    check("StringContainsQ(ToString(Dataset(rows, DatasetDisplayFormat -> \"Associations\")), \"<|\")", //
        "True");

    // the styling reaches the HTML the servlets send, and nothing else
    check("StringContainsQ(JSForm(Dataset(rows, HeaderBackground -> Red)), \"background:rgb(255,0,0)\")", //
        "True");
    check("StringContainsQ(JSForm(Dataset(rows, Alignment -> Right)), \"text-align:right\")", //
        "True");
    check("StringContainsQ(JSForm(Dataset(rows, ItemStyle -> {Bold, Blue})),"
        + " \"font-weight:bold;color:rgb(0,0,255)\")", //
        "True");
    check("StringContainsQ(JSForm(Dataset(rows, ItemSize -> 12)), \"width:12ch\")", //
        "True");
    check("StringContainsQ(JSForm(Dataset(rows, DatasetTheme -> \"Striped\")), \"background:#f7f7f7\")", //
        "True");
    // an explicit option replaces what the theme set rather than being appended after it
    check("StringContainsQ(JSForm(Dataset(rows, DatasetTheme -> \"Striped\", HeaderBackground -> Red)),"
        + " \"background:#eeeeee\")", //
        "False");
    // with no options the markup still carries the default grid and header shading
    check("StringContainsQ(JSForm(Dataset(rows)), \"border:1px solid darkgray\")", //
        "True");
    check("StringContainsQ(JSForm(Dataset(rows)), \"background:lightgray\")", //
        "True");
    // ... which an option of the same property replaces, leaving the grid alone
    check("StringContainsQ(JSForm(Dataset(rows, HeaderBackground -> Red)), \"background:lightgray\")", //
        "False");
    check("StringContainsQ(JSForm(Dataset(rows, HeaderBackground -> Red)),"
        + " \"border:1px solid darkgray\")", //
        "True");

    // the options survive a selection
    check("StringContainsQ(ToString(Dataset(rows, HeaderDisplayFunction -> ToUpperCase)[All, {\"city\"}]),"
        + " \"CITY\")", //
        "True");
    // ... and a bare column name selects a vector, which has no header to display at all
    check("StringContainsQ(ToString(Dataset(rows, HeaderDisplayFunction -> ToUpperCase)[All, \"city\"]),"
        + " \"CITY\")", //
        "False");

    // AllowedDimensions constrains the data, not its appearance
    check("Head(Dataset(rows, AllowedDimensions -> {3, 3}))", //
        "Dataset");
    check("Head(Dataset(rows, AllowedDimensions -> {9, 9}))", //
        "Dataset");
  }

  /**
   * <code>RandomSample(dataset, n)</code>. Every count case is pinned against the list form rather
   * than against the reference: the two are one function and have to answer alike, whatever they
   * answer.
   */
  /**
   * Rows picked by index come back in the order they were asked for. They used to come back in
   * table order: the selection went through <code>Table.rows(int...)</code>, which is backed by a
   * bitmap and so has no way to remember an order.
   */
  @Test
  public void testRowSelectionKeepsTheOrderAsked() {
    Config.FILESYSTEM_ENABLED = true;
    check("u = SemanticImportString(\"name,n\nAnn,1\nBob,2\nCid,3\nDee,4\n\");Head(u)", //
        "Dataset");

    check("Normal(u[{3,1}, All])[[All, \"name\"]]", //
        "{Cid,Ann}");
    check("Normal(u[{4,2,3}, All])[[All, \"name\"]]", //
        "{Dee,Bob,Cid}");
    // ascending stays ascending, which is the case that passed before too
    check("Normal(u[{1,3}, All])[[All, \"name\"]]", //
        "{Ann,Cid}");
    // a repeated index is a repeated row, which a bitmap cannot express at all
    check("Normal(u[{2,2}, All])[[All, \"name\"]]", //
        "{Bob,Bob}");
    // and the columns are still narrowed alongside
    check("Normal(u[{3,1}, {\"name\"}])", //
        "{Cid,Ann}");
  }

  /**
   * <code>dataset[[{i, j}]]</code> is one dataset of those rows. It used to be
   * <code>Dataset(row, row)</code> - the head applied to one-row datasets - because the generic
   * <code>Part</code> builds <code>head(items...)</code>, which is right for an ordinary
   * expression and not for this.
   */
  @Test
  public void testPartOfADataset() {
    Config.FILESYSTEM_ENABLED = true;
    check("v = SemanticImportString(\"name,n\nAnn,1\nBob,2\nCid,3\n\");"
        + "w = SemanticImportString(\"name,n\nAnn,1\n\");Head(v)", //
        "Dataset");

    check("Head(Part(v, {2,1}))", //
        "Dataset");
    check("Normal(Part(v, {2,1}))[[All, \"name\"]]", //
        "{Bob,Ann}");
    check("Normal(Part(v, {2,2}))[[All, \"name\"]]", //
        "{Bob,Bob}");
    // negative positions count from the end, as they do everywhere else in Part. One row normalizes
    // to one association rather than to a list of one - see ASTDataset#normal
    check("Normal(Part(v, {-1}))", //
        "<|name->Cid,n->3|>");
    // an empty specification is an empty dataset, not a Dataset() complaining about its arguments
    check("Head(Part(v, {}))", //
        "Dataset");
    check("Normal(Part(v, {}))", //
        "{}");
    // out of range is reported and the expression stays put
    check("Head(Part(v, {9}))", //
        "Part");

    // a dataset of one row is indexed by column - Length is its column count - so a selection of
    // several columns is a list of values
    check("Length(w)", //
        "2");
    check("Part(w, {1,2})", //
        "{Ann,1}");

    // and an ordinary expression is untouched by any of this
    check("f(a,b,c)[[{3,1}]]", //
        "f(c,a)");
    check("{10,20,30}[[{-1,1}]]", //
        "{30,10}");
  }

  /**
   * A <code>Dataset</code> built from associations - the way the reference documents building one -
   * gets columns of the type its values actually have, so it can be grouped, aggregated and
   * summarized. Everything used to go into an <code>ExprColumn</code>, which is neither categorical
   * nor numeric, and those operations failed on it while the same data from a CSV worked.
   */
  @Test
  public void testAssociationsGetTypedColumns() {
    Config.FILESYSTEM_ENABLED = true;
    check("s = Dataset({<|\"g\" -> \"a\", \"v\" -> 1|>, <|\"g\" -> \"a\", \"v\" -> 2|>,"
        + "<|\"g\" -> \"b\", \"v\" -> 3|>});Head(s)", //
        "Dataset");

    check("Normal(AggregateBy(s, \"g\", \"v\", Total))", //
        "{<|g->a,Sum [v]->3.0|>,<|g->b,Sum [v]->3.0|>}");
    // and it agrees with the same data read as a CSV
    check("Normal(AggregateBy(SemanticImportString(\"g,v\na,1\na,2\nb,3\n\"), \"g\", \"v\", Total))", //
        "{<|g->a,Sum [v]->3.0|>,<|g->b,Sum [v]->3.0|>}");
    // Summary now has something to say beyond the row count
    check("MemberQ(Normal(Summary(s))[[All, \"Summary\"]], \"Mean\")", //
        "True");

    // One column normalizes to a bare list of its values - see ASTDataset#normal - so these read
    // the values directly. Integers stay integers: Normal gives back what was put in.
    check("Normal(Dataset({<|\"n\" -> 1|>, <|\"n\" -> 2|>})) // InputForm", //
        "{1,2}");
    // a column no other type can hold without changing it stays symbolic
    check("Normal(Dataset({<|\"e\" -> x^2|>, <|\"e\" -> Sin(y)|>})) // InputForm", //
        "{x^2,Sin(y)}");
    // a fraction is not silently turned into a double
    check("Normal(Dataset({<|\"r\" -> 1/2|>, <|\"r\" -> 3/4|>})) // InputForm", //
        "{1/2,3/4}");
    check("Normal(Dataset({<|\"b\" -> True|>, <|\"b\" -> False|>})) // InputForm", //
        "{True,False}");
    check("Normal(Dataset({<|\"s\" -> \"x\"|>, <|\"s\" -> \"y\"|>})) // InputForm", //
        "{\"x\",\"y\"}");
    // and with two columns the associations are kept, types and all
    check("Normal(Dataset({<|\"n\" -> 1, \"s\" -> \"x\"|>})) // InputForm", //
        "<|\"n\"->1,\"s\"->\"x\"|>");
  }

  /**
   * An expression cell is parsed as an expression, and a comma in it is syntax. The parser used to
   * strip commas first - the thousands-separator trick the numeric parsers use - which turned
   * <code>f(a,b)</code> into <code>f(ab)</code> without saying so.
   */
  @Test
  public void testExprCellsKeepTheirCommas() {
    Config.FILESYSTEM_ENABLED = true;
    // a list-valued cell survives a round trip through the dataset. One column, so Normal is the
    // list of the cells and the cell itself is the single element.
    check("Normal(Dataset({<|\"c\" -> {1, 2, 3}|>})) // InputForm", //
        "{{1,2,3}}");
    check("Normal(Dataset({<|\"c\" -> f(a, b)|>})) // InputForm", //
        "{f(a,b)}");
  }

  /**
   * A built-in that walks a collection must see the rows of a dataset, not the dataset object.
   *
   * <p>
   * <code>Total</code> and <code>Apply</code> used to abort the whole evaluation on one - an
   * <code>AbortException</code> reported as "null" - because the level visitor read the dataset as
   * a structure and then called <code>set</code>, which a dataset cannot answer. <code>Sort</code>
   * and <code>Union</code> threw for the same reason. The first assertions here are that nothing
   * aborts; the rest are that the answer is the one the rows give.
   */
  /**
   * The idioms of the reference's datasets chapter. A chart's result is a <code>Graphics</code>
   * expression, and its head is all that is asserted here - what it draws is the charts' own tests
   * to answer, not these.
   */
  /**
   * The two dataset shapes that are not tables: a bare vector and a bare association. Every
   * expectation here was read off a real Mathematica.
   *
   * <p>
   * The rule they follow is that a dataset wraps a collection and a scalar comes back bare, which is
   * why <code>Total</code> of a vector dataset is a number and not a dataset of one.
   */
  /**
   * A cell holding a <code>Graphics</code> is drawn, not printed: <code>data[All, PieChart]</code>
   * shows charts in the browser rather than the source text of the chart expressions.
   */
  @Test
  public void testGraphicsCellsAreDrawn() {
    check("data = Dataset(<|\"a\" -> <|\"x\"->1,\"y\"->2,\"z\"->3|>,"
        + " \"b\" -> <|\"x\"->5,\"y\"->10,\"z\"->7|>|>);", //
        "");

    // one picture per key ...
    check("StringCount(JSForm(data[All, PieChart]), \"<svg\")", //
        "2");
    // ... and not the expression that draws it
    check("StringContainsQ(JSForm(data[All, PieChart]), \"Disk(\")", //
        "False");
    // the markup is written verbatim, so the SVG keeps the case of its attributes - lower cased,
    // viewBox stops scaling the picture
    check("StringContainsQ(JSForm(data[All, PieChart]), \"viewBox\")", //
        "True");
    // it scales to the cell rather than being drawn at plot size
    check("StringContainsQ(JSForm(data[All, PieChart]), \"max-width\")", //
        "True");

    // a graphic in an ordinary table cell is drawn too
    check("StringCount(JSForm(Dataset({<|\"name\"->\"a\",\"chart\"->PieChart({1,2,3})|>})), \"<svg\")", //
        "1");

    // a Graphics3D is drawn too, as the WebGL canvas and the script that fills it
    check("threeD = Dataset(<|\"g\" -> Graphics3D(Sphere()), \"h\" -> Graphics3D(Cuboid())|>);", //
        "");
    check("StringCount(JSForm(threeD), \"data-type\")", //
        "2");
    check("StringCount(JSForm(threeD), \"renderSymjaWebGL(\")", //
        "2");
    check("StringContainsQ(JSForm(threeD), \"Graphics3D(\")", //
        "False");
    // each canvas needs a container of its own: two cells sharing an id renders both scenes into
    // the first cell and leaves the second empty
    check("Length(DeleteDuplicates(StringCases(JSForm(threeD),"
        + " RegularExpression(\"webgl_[0-9]+_[0-9]+\"))))", //
        "2");
    // a plot and a Legended plot are the same thing underneath, and both draw
    check("StringCount(JSForm(Dataset(<|\"a\" -> Plot3D(Sin(x*y),{x,0,3},{y,0,3})|>)), \"data-type\")", //
        "1");
    check("StringCount(JSForm(Dataset(<|\"l\" -> Legended(Graphics3D(Sphere()), \"s\")|>)), \"data-type\")", //
        "1");
    // 2D and 3D in one table, each drawn its own way
    check("StringCount(JSForm(Dataset(<|\"2d\" -> PieChart({1,2}), \"3d\" -> Graphics3D(Sphere())|>)),"
        + " \"<svg\")", //
        "1");
    check("StringCount(JSForm(Dataset(<|\"2d\" -> PieChart({1,2}), \"3d\" -> Graphics3D(Sphere())|>)),"
        + " \"data-type\")", //
        "1");

    // only markup the column produced itself goes through unescaped - a string that looks like
    // markup is still escaped
    check("StringContainsQ(JSForm(Dataset(<|\"s\" -> \"<b>bold</b>\"|>)), \"&lt;b&gt;bold&lt;/b&gt;\")", //
        "True");
    check("StringContainsQ(JSForm(Dataset(<|\"s\" -> \"<b>bold</b>\"|>)), \"<b>bold</b>\")", //
        "False");
  }

  /**
   * A missing cell is drawn as a grey hyphen and says nothing else. The value behind it is
   * untouched - <code>Normal</code> still gives back the <code>Missing(...)</code> that says why -
   * and nothing is reported about it either: a part that is not there is the question
   * <code>dataset[All, "c", 1]</code> asks, not a fault to warn about.
   */
  @Test
  public void testAMissingCellIsDrawnAsAHyphen() {
    check("dm = Dataset({<|\"a\" -> 1, \"c\" -> {1}|>, <|\"a\" -> 5, \"c\" -> {5, 6, 7}|>,"
        + "<|\"a\" -> 6, \"c\" -> {}|>});", //
        "");

    // one absent part, drawn as a hyphen in the colour of the grid
    check("StringCount(JSForm(dm[All, \"c\", 1]), \"color:darkgray\\\">-<\")", //
        "1");
    // and not spelled out in the cell
    check("StringContainsQ(JSForm(dm[All, \"c\", 1]), \"Missing\")", //
        "False");
    // the value is still there to be read
    check("Normal(dm[All, \"c\", 1]) // InputForm", //
        "{1,5,Missing(\"PartAbsent\",1)}");
    check("Count(Normal(dm[All, \"c\", 1]), _Missing)", //
        "1");

    // that nothing is *reported* about it is pinned by MissingCellTest in matheclipse-dataset:
    // it is about what the engine prints, which this string comparison never sees

    // a row of the source that simply lacked a key is missing too - in every column type, not
    // only the ones holding expressions
    check("gappy = Dataset({<|\"a\" -> 1, \"b\" -> 2|>, <|\"a\" -> 3|>});", //
        "");
    check("Normal(gappy) // InputForm", //
        "{<|\"a\"->1,\"b\"->2|>,<|\"a\"->3,\"b\"->Missing(NotAvailable)|>}");
    check("StringCount(JSForm(gappy), \"color:darkgray\\\">-<\")", //
        "1");
    // ... and walking it does not throw, which is what an unboxed null used to do
    check("Head(gappy)", //
        "Dataset");
    check("Length(Normal(gappy))", //
        "2");
  }

  /**
   * <code>TakeLargest</code> and <code>TakeSmallest</code> used to <b>abort</b> on a dataset: the
   * cleaning they do first reads it as a plain AST and hands <code>N</code> something it cannot
   * make a number of. They ask the rows now, and give a dataset back like every other collection.
   */
  @Test
  public void testTakeLargestOnADataset() {
    check("tl = Dataset({<|\"a\" -> 1|>, <|\"a\" -> 3|>, <|\"a\" -> 2|>});"
        + "col = tl[All, \"a\"];Head(col)", //
        "Dataset");

    check("Head(TakeLargest(col, 2))", //
        "Dataset");
    check("Normal(TakeLargest(col, 2)) // InputForm", //
        "{3,2}");
    check("Head(TakeSmallest(col, 2))", //
        "Dataset");
    check("Normal(TakeSmallest(col, 2)) // InputForm", //
        "{1,2}");
    // the same answer as the rows on their own, which is the point
    check("Normal(TakeLargest(col, 2)) === TakeLargest(Normal(col), 2)", //
        "True");

    // a one column table normalizes to its values, so it answers like the column itself does -
    // the same rule that makes Normal of a one column dataset a bare list
    check("Head(TakeLargest(tl, 2))", //
        "Dataset");
    check("Normal(TakeLargest(tl, 2)) // InputForm", //
        "{3,2}");

    // rows of more than one field cannot be ordered by size, so that is reported - which is the
    // behaviour of the rows on their own, and no longer an abort
    check("wide = Dataset({<|\"a\" -> 1, \"b\" -> \"x\"|>, <|\"a\" -> 3, \"b\" -> \"y\"|>});"
        + "Head(TakeLargest(wide, 1))", //
        "TakeLargest");
    check("Head(TakeLargest(Normal(wide), 1))", //
        "TakeLargest");

    // the operator form through the query path was always fine and stays so
    check("Head(tl[TakeLargest(2), \"a\"])", //
        "Dataset");
  }

  /**
   * <code>Keys</code> and <code>Values</code> answer for <b>each row</b>, and answer with a
   * dataset - verified against a real Mathematica, which gives
   * <code>Keys[ds]</code> as <code>Dataset[{{"a","b"}, {"a","b"}, {"a","b"}}]</code> and
   * <code>Values[ds]</code> as <code>Dataset[{{1,"x"}, {2,"y"}, {3,"x"}}]</code>.
   */
  @Test
  public void testKeysAndValuesOfADataset() {
    check("kv = Dataset({<|\"a\" -> 1, \"b\" -> \"x\"|>, <|\"a\" -> 2, \"b\" -> \"y\"|>});Head(kv)", //
        "Dataset");

    check("Head(Keys(kv))", //
        "Dataset");
    check("Normal(Keys(kv)) // InputForm", //
        "{{\"a\",\"b\"},{\"a\",\"b\"}}");
    check("Head(Values(kv))", //
        "Dataset");
    check("Normal(Values(kv)) // InputForm", //
        "{{1,\"x\"},{2,\"y\"}}");
    // the same answer as the rows on their own, wrapped
    check("Normal(Keys(kv)) === Keys(Normal(kv))", //
        "True");
    check("Normal(Values(kv)) === Values(Normal(kv))", //
        "True");

    // a dataset whose rows are named answers with those names, and no longer with the column the
    // names are stored in - that used to come out as {"", "a"}, which is the storage showing
    check("named = Dataset(<|\"r1\" -> <|\"a\" -> 1|>, \"r2\" -> <|\"a\" -> 2|>|>);"
        + "Normal(Keys(named)) // InputForm", //
        "{\"r1\",\"r2\"}");
    check("StringContainsQ(ToString(Normal(Keys(named))), \"\\\"\\\"\")", //
        "False");

    // Keys and Values of a plain association are untouched
    check("Keys(<|\"a\" -> 1, \"b\" -> 2|>) // InputForm", //
        "{\"a\",\"b\"}");
    check("Values(<|\"a\" -> 1, \"b\" -> 2|>) // InputForm", //
        "{1,2}");
  }

  /** A dataset lays its rows out, like any other collection. */
  @Test
  public void testMulticolumnOfADataset() {
    check("mc = Dataset({<|\"a\" -> 1, \"b\" -> \"x\"|>, <|\"a\" -> 2, \"b\" -> \"y\"|>,"
        + "<|\"a\" -> 3, \"b\" -> \"z\"|>, <|\"a\" -> 4, \"b\" -> \"w\"|>});"
        + "Head(Multicolumn(mc, 2))", //
        "Grid");
    check("Multicolumn(mc, 2) // InputForm", //
        "Grid({{<|\"a\"->1,\"b\"->\"x\"|>,<|\"a\"->3,\"b\"->\"z\"|>},"
            + "{<|\"a\"->2,\"b\"->\"y\"|>,<|\"a\"->4,\"b\"->\"w\"|>}})");
    // through the query syntax too
    check("Head(mc[Multicolumn])", //
        "Grid");
    // a one column dataset normalizes to its values, so those are what get laid out
    check("Multicolumn(Dataset({<|\"a\" -> 1|>, <|\"a\" -> 2|>}), 1) // InputForm", //
        "Grid({{1},{2}})");
  }

  /**
   * <code>Query</code> on a dataset is the dataset's own query, written the other way round:
   * <code>Query[op1, op2, …][dataset]</code> and <code>dataset[Query[op1, op2, …]]</code> are both
   * <code>dataset[op1, op2, …]</code>. Walking the dataset as a plain expression, which is what
   * used to happen, gave the right values in the wrong shape - a bare association where
   * <code>dataset[Total]</code> gives a dataset - or nothing at all.
   */
  @Test
  public void testQueryOnADataset() {
    check("qd = Dataset({<|\"a\" -> 1, \"b\" -> \"x\"|>, <|\"a\" -> 2, \"b\" -> \"y\"|>,"
        + "<|\"a\" -> 3, \"b\" -> \"x\"|>});Head(qd)", //
        "Dataset");

    // an aggregate: a dataset, and the same one dataset[Total] gives
    check("Head(Query(Total)[qd])", //
        "Dataset");
    check("Normal(Query(Total)[qd]) === Normal(qd[Total])", //
        "True");
    check("Head(qd[Query(Total)])", //
        "Dataset");
    check("Normal(qd[Query(Total)]) === Normal(qd[Total])", //
        "True");

    // a column
    check("Normal(Query(All, \"a\")[qd]) // InputForm", //
        "{1,2,3}");
    check("Normal(qd[Query(All, \"a\")]) // InputForm", //
        "{1,2,3}");
    check("Normal(Query(All, \"a\")[qd]) === Normal(qd[All, \"a\"])", //
        "True");

    // an operator form reaches the same machinery
    check("Normal(Query(Select(Function(#a > 1)))[qd]) === Normal(qd[Select(Function(#a > 1))])", //
        "True");
    // narrowed to a column it is a number, as everywhere else
    check("Query(Total, \"a\")[qd]", //
        "6");
    // no specification at all leaves the dataset alone
    check("Query()[qd] === qd", //
        "True");
  }

  /**
   * The query surface: the two-argument forms, the operator forms, and <code>JoinAcross</code>.
   * Three of the operator forms did not work on plain <b>lists</b> either - the framework wants an
   * operator form declared as the third entry of <code>expectedArgSize</code>, and a two entry
   * specification says the opposite, that there is none.
   */
  @Test
  public void testTheQuerySurfaceTakesADataset() {
    check("qs = Dataset({<|\"a\" -> 1, \"b\" -> \"x\"|>, <|\"a\" -> 2, \"b\" -> \"y\"|>,"
        + "<|\"a\" -> 3, \"b\" -> \"x\"|>});"
        + "qs2 = Dataset({<|\"a\" -> 1, \"c\" -> 10|>, <|\"a\" -> 2, \"c\" -> 20|>});"
        + "L = Normal(qs);Head(qs)", //
        "Dataset");

    // the operator forms, on plain lists first
    check("CountsBy(Function(#b))[L] // InputForm", //
        "<|\"x\"->2,\"y\"->1|>");
    check("TakeLargestBy(Function(#a), 2)[L] // InputForm", //
        "{<|\"a\"->3,\"b\"->\"x\"|>,<|\"a\"->2,\"b\"->\"y\"|>}");
    check("TakeSmallestBy(Function(#a), 2)[L] // InputForm", //
        "{<|\"a\"->1,\"b\"->\"x\"|>,<|\"a\"->2,\"b\"->\"y\"|>}");
    // ... and then on a dataset
    check("Head(qs[CountsBy(Function(#b))])", //
        "Dataset");
    check("Head(qs[TakeLargestBy(Function(#a), 2)])", //
        "Dataset");
    check("Head(qs[TakeSmallestBy(Function(#a), 2)])", //
        "Dataset");
    // a count is a number, so it comes back bare - and it used to be computed and then dropped
    check("qs[Count(_)]", //
        "3");
    check("Count(qs, _)", //
        "3");

    // the two-argument forms
    check("Normal(GroupBy(qs, Function(#b))) // InputForm", //
        "<|\"x\"->{<|\"a\"->1,\"b\"->\"x\"|>,<|\"a\"->3,\"b\"->\"x\"|>},"
            + "\"y\"->{<|\"a\"->2,\"b\"->\"y\"|>}|>");
    check("Normal(CountsBy(qs, Function(#b))) // InputForm", //
        "<|\"x\"->2,\"y\"->1|>");
    check("Normal(Lookup(qs, \"a\")) // InputForm", //
        "{1,2,3}");
    check("Normal(Merge(qs, Total)) // InputForm", //
        "<|\"a\"->6,\"b\"->2*\"x\" + \"y\"|>");
    check("Normal(PositionIndex(qs[All, \"b\"])) // InputForm", //
        "<|\"x\"->{1,3},\"y\"->{2}|>");
    check("Normal(DeleteMissing(Dataset({<|\"a\" -> 1|>, <|\"a\" -> Missing()|>, <|\"a\" -> 3|>})))"
        + " // InputForm", //
        "{1,3}");

    // grouping on a *column name* is the dataset's own operation and must not be handed the rows,
    // which would read the name as a function of a row
    check("Head(qs[GroupBy(\"b\")])", //
        "Dataset");

    // JoinAcross: a list in gives a list out, a dataset in gives a dataset out. Only the dataset
    // form worked before - the opposite way round from everything else here
    check("Head(JoinAcross(L, Normal(qs2), \"a\"))", //
        "List");
    check("JoinAcross(L, Normal(qs2), \"a\") // InputForm", //
        "{<|\"a\"->1,\"b\"->\"x\",\"c\"->10|>,<|\"a\"->2,\"b\"->\"y\",\"c\"->20|>}");
    check("Head(JoinAcross(qs, qs2, \"a\"))", //
        "Dataset");
    check("Normal(JoinAcross(qs, qs2, \"a\")) === JoinAcross(L, Normal(qs2), \"a\")", //
        "True");
  }

  /**
   * The collection rule across the structural built-ins: each of these already walked a dataset's
   * rows and simply handed them back bare, where the reference keeps a dataset.
   * <code>Take</code>, <code>Drop</code>, <code>Reverse</code>, <code>Rest</code> and
   * <code>First</code> were already right and are checked here beside them.
   */
  @Test
  public void testStructuralBuiltInsKeepTheDataset() {
    check("st = Dataset({<|\"a\" -> 3, \"b\" -> \"x\"|>, <|\"a\" -> 1, \"b\" -> \"y\"|>,"
        + "<|\"a\" -> 2, \"b\" -> \"x\"|>});"
        + "one = Dataset({<|\"a\" -> 9, \"b\" -> \"z\"|>});"
        + "r7 = <|\"a\" -> 7, \"b\" -> \"w\"|>;Head(st)", //
        "Dataset");

    check("Head(Sort(st))", //
        "Dataset");
    // ... and it really is sorted now, which needed the association ordering fixed first
    check("Normal(Sort(st))[[All, \"a\"]] // InputForm", //
        "{1,2,3}");

    check("Head(SortBy(st, Function(#a)))", //
        "Dataset");
    check("Head(DeleteDuplicates(st))", //
        "Dataset");
    check("Head(Union(st))", //
        "Dataset");
    check("Head(Insert(st, r7, 1))", //
        "Dataset");
    check("Head(Delete(st, 1))", //
        "Dataset");
    check("Head(Append(st, r7))", //
        "Dataset");

    // the contents are what they should be, not merely the head
    check("Normal(Insert(st, r7, 1))[[1, \"a\"]]", //
        "7");
    check("Normal(Append(st, r7))[[-1, \"a\"]]", //
        "7");
    check("Length(Normal(Delete(st, 1)))", //
        "2");
    check("Length(Normal(Union(st)))", //
        "3");

    // two datasets combine. A dataset of one row normalizes to that row rather than to a list of
    // one, so a second dataset argument is wrapped before it is handed over - without that the
    // heads did not match and the union was left unevaluated
    check("Head(Union(st, one))", //
        "Dataset");
    check("Length(Normal(Union(st, one)))", //
        "4");

    // the five that were already right
    check("Head(Take(st, 2))", //
        "Dataset");
    check("Head(Drop(st, 1))", //
        "Dataset");
    check("Head(Reverse(st))", //
        "Dataset");

    // and the same functions on plain lists are untouched
    check("Sort({3,1,2})", //
        "{1,2,3}");
    check("Union({1,2}, {2,3})", //
        "{1,2,3}");
    check("Append({1,2}, 3)", //
        "{1,2,3}");
    check("Insert({1,2}, 9, 1)", //
        "{9,1,2}");
    check("Delete({1,2,3}, 2)", //
        "{1,3}");
    check("DeleteDuplicates({1,1,2})", //
        "{1,2}");
    check("Union({1,2,3}, SameTest -> Equal)", //
        "{1,2,3}");
  }

  /**
   * A dataset wraps a collection and gives a scalar back bare. Two places did not: a function
   * applied to a whole dataset handed back the bare association it computed, and a cell holding a
   * list handed back the bare list.
   */
  @Test
  public void testACollectionComesBackAsADataset() {
    check("ds = Dataset(Table(<|\"a\" -> 3 i, \"b\" -> 3 i + 2, \"c\" -> 3 i + 5|>, {i, 3}));", //
        "");

    // an aggregate over the rows is one entry per column, and that is a dataset
    check("Head(ds[Total])", //
        "Dataset");
    check("Normal(ds[Total]) // InputForm", //
        "<|\"a\"->18,\"b\"->24,\"c\"->33|>");
    check("Head(ds[Mean])", //
        "Dataset");
    // ... narrowed to one column it is a number, and a number comes back bare
    check("ds[Total, \"a\"]", //
        "18");
    check("Head(ds[Total, \"a\"])", //
        "Integer");

    check("dsl = Dataset({<|\"a\" -> 1, \"b\" -> \"x\", \"c\" -> {1}|>,"
        + "<|\"a\" -> 5, \"b\" -> \"y\", \"c\" -> {5, 6, 7}|>});", //
        "");

    // a cell holding a list is a dataset of that list
    check("Head(dsl[2, \"c\"])", //
        "Dataset");
    check("Normal(dsl[2, \"c\"]) // InputForm", //
        "{5,6,7}");
    check("dsl[2, \"c\"][[1]]", //
        "5");
    check("Total(dsl[2, \"c\"])", //
        "18");
    // by column number too
    check("Head(dsl[2, 3])", //
        "Dataset");
    // ... while a cell holding a scalar is still the scalar
    check("Head(dsl[2, \"b\"])", //
        "String");
    check("Head(dsl[2, \"a\"])", //
        "Integer");

    // a column of lists run together is a collection, so it is a dataset as well
    check("Head(dsl[Catenate, \"c\"])", //
        "Dataset");
    // the second slot is a column wherever it appears, so a number past the last column names
    // nothing and is reported rather than left unevaluated
    check("Head(dsl[RandomSample, 9])", //
        "Failure");
    check("Head(dsl[Total, 9])", //
        "Failure");
    check("dsl[Total, 1]", //
        "6");
    check("Head(dsl[RandomSample, 1])", //
        "Dataset");
    check("Normal(dsl[Catenate, \"c\"]) // InputForm", //
        "{1,5,6,7}");
    check("dsl[[1, 1]]", //
        "1");

    // an empty collection is a collection: the cell holding {} is an empty dataset, not a bare
    // empty list, and so is one built directly
    check("dse = Dataset({<|\"a\" -> 6, \"c\" -> {}|>});", //
        "");
    check("Head(dse[1, \"c\"])", //
        "Dataset");
    check("Normal(dse[1, \"c\"]) // InputForm", //
        "{}");
    check("Length(dse[1, \"c\"])", //
        "0");
    check("Head(Dataset({}))", //
        "Dataset");
    check("Normal(Dataset({})) // InputForm", //
        "{}");
    check("Head(Dataset(<||>))", //
        "Dataset");
    check("Normal(Dataset(<||>)) // InputForm", //
        "<||>");

    // the operator forms answer with a dataset too - a collection is a collection however it was
    // arrived at, and the servlet has to be able to draw it
    check("Head(ds[RandomSample])", //
        "Dataset");
    check("Length(ds[RandomSample])", //
        "3");
    check("Head(ds[SortBy(Function(#a))])", //
        "Dataset");
    check("Head(ds[Sort, \"a\"])", //
        "Dataset");
  }

  /**
   * A lone record reads down the page in a console too, so the text and the HTML rendering lay a
   * dataset out the same way. Only the shading and the grid are browser-only: a console has
   * nowhere to put CSS.
   */
  @Test
  public void testConsoleAndBrowserAgreeOnLayout() {
    check("ds = Dataset(Table(<|\"a\" -> 3 i, \"b\" -> 3 i + 2|>, {i, 3}));", //
        "");

    // several rows: field names across the top
    check("ds", //
        " a  |  b   |\r\n" + //
            "------------\r\n" + //
            " 3  |   5  |\r\n" + //
            " 6  |   8  |\r\n" + //
            " 9  |  11  |");

    // one row: field names down the side
    check("ds[2]", //
        "    |     |\r\n" + //
            "-----------\r\n" + //
            " a  |  6  |\r\n" + //
            " b  |  8  |");

    // an association is the same layout, and still never shows the columns it is kept in
    check("Dataset(<|\"a\" -> 3, \"b\" -> 7|>)", //
        "    |     |\r\n" + //
            "-----------\r\n" + //
            " a  |  3  |\r\n" + //
            " b  |  7  |");
    check("StringContainsQ(ToString(Dataset(<|\"a\" -> 3|>)), \"key\")", //
        "False");

    // a vector has no names at all
    check("Dataset({3, 7, 11})", //
        "     |\r\n" + //
            "------\r\n" + //
            "  3  |\r\n" + //
            "  7  |\r\n" + //
            " 11  |");

    // a table whose rows are named keeps both, even at one row - the row name is not a field
    check("Dataset(<|\"r1\" -> <|\"x\" -> 1, \"y\" -> 2|>|>)", //
        "     |  x  |  y  |\r\n" + //
            "------------------\r\n" + //
            " r1  |  1  |  2  |");

    // DatasetDisplayFormat -> \"Associations\" still asks for the association itself, laid out
    // by nothing
    check("Dataset(Table(<|\"a\" -> i, \"b\" -> 2 i|>, {i, 2}),"
        + " DatasetDisplayFormat -> \"Associations\")", //
        "{<|a->1,b->2|>,<|a->2,b->4|>}");
  }

  /**
   * How a dataset is drawn in the browser with no options given: the field names shaded, a grid
   * between every pair of cells, and a lone record read down the page rather than across.
   */
  @Test
  public void testDefaultGridAndHeaders() {
    check("ds = Dataset(Table(<|\"a\" -> 3 i, \"b\" -> 3 i + 2, \"c\" -> 3 i + 5|>, {i, 3}));", //
        "");

    // a table: the field names are a header row, shaded
    check("StringContainsQ(JSForm(ds), \"<thead>\")", //
        "True");
    check("StringCount(JSForm(ds), \"background:lightgray\")", //
        "3");
    check("StringContainsQ(JSForm(ds), \">a</th>\")", //
        "True");
    // one grid line between cells, not two abutting ones
    check("StringContainsQ(JSForm(ds), \"border-collapse:collapse\")", //
        "True");
    check("StringCount(JSForm(ds), \"border:1px solid darkgray\")", //
        "12");

    // one row reads down the page: the field names are the header column and the values the only
    // other one. Every other part of the class already indexes a lone row by field name
    check("StringCount(JSForm(ds[2]), \"<thead>\")", //
        "0");
    check("StringCount(JSForm(ds[2]), \"<th \")", //
        "3");
    check("StringContainsQ(JSForm(ds[2]), \">b</th>\")", //
        "True");
    check("StringContainsQ(JSForm(ds[2]), \">8</td>\")", //
        "True");
    check("StringCount(JSForm(ds[2]), \"background:lightgray\")", //
        "3");
    // three field names and three values, and nothing else
    check("StringCount(JSForm(ds[2]), \"<td \")", //
        "3");
  }

  /**
   * The browser form of each shape. A vector and an association have no field names to show, so
   * their headers are blank - and an association has two blank ones, which the table rejected as
   * duplicates: every association threw {@code IllegalArgumentException} out of the servlet, since
   * {@code toString} answers from the association itself and never built this table.
   */
  @Test
  public void testEachShapeRendersInTheBrowser() {
    // an association reads down the page: the keys name the rows, so they are the header column
    // and there is no header row above them
    check("StringCount(JSForm(Dataset(<|\"a\"->3,\"b\"->7|>)), \"<thead>\")", //
        "0");
    check("StringCount(JSForm(Dataset(<|\"a\"->3,\"b\"->7|>)), \"<th \")", //
        "2");
    check("StringContainsQ(JSForm(Dataset(<|\"a\"->3,\"b\"->7|>)), \">a</th>\")", //
        "True");
    check("StringContainsQ(JSForm(Dataset(<|\"a\"->3,\"b\"->7|>)), \">7</td>\")", //
        "True");

    // a vector has neither: nothing names its rows and nothing names its column
    check("StringCount(JSForm(Dataset({3,7,11})), \"<thead>\")", //
        "0");
    check("StringCount(JSForm(Dataset({3,7,11})), \"<th \")", //
        "0");
    check("StringContainsQ(JSForm(Dataset({3,7,11})), \">11</td>\")", //
        "True");
    // ... including one that came out of a selection
    check("StringCount(JSForm(Dataset({<|\"x\"->1,\"y\"->2|>,<|\"x\"->3,\"y\"->4|>})[All, \"x\"]),"
        + " \"<th \")", //
        "0");

    // a table of several rows shows its field names across the top
    check("StringContainsQ(JSForm(Dataset({<|\"x\"->1|>,<|\"x\"->3|>})), \"<thead>\")", //
        "True");
    check("StringContainsQ(JSForm(Dataset({<|\"x\"->1|>,<|\"x\"->3|>})), \">x</th>\")", //
        "True");

    // an association of associations names its rows as well as its columns, so both are headers
    check("keyed = Dataset(<|\"r1\" -> <|\"x\"->1,\"y\"->2|>, \"r2\" -> <|\"x\"->5,\"y\"->6|>|>);", //
        "");
    check("StringContainsQ(JSForm(keyed), \"<thead>\")", //
        "True");
    check("StringContainsQ(JSForm(keyed), \">r1</th>\")", //
        "True");

    // an association of associations reduced by a function is an association again - this is the
    // shape the servlet crashed on
    check("nested = Dataset(<|\"a\" -> <|\"x\"->1,\"y\"->2|>, \"b\" -> <|\"x\"->5,\"y\"->10|>|>);", //
        "");
    check("StringCount(JSForm(nested[All, Total]), \"<th \")", //
        "2");
    check("StringContainsQ(JSForm(nested[All, Total]), \">15</td>\")", //
        "True");
  }

  /**
   * A column named on its own reduces to a vector, as in the reference: <code>ds[All, "x"]</code>
   * is <code>Dataset[{1, 3, 5}]</code>, so its first part is the bare value. Naming the same
   * column in a list, <code>ds[All, {"x"}]</code>, keeps the field and stays a table.
   */
  @Test
  public void testColumnSelectionReducesToAVector() {
    check("ds = Dataset({<|\"x\"->1,\"y\"->2|>,<|\"x\"->3,\"y\"->4|>,<|\"x\"->5,\"y\"->6|>});", //
        "");

    check("Head(ds[All, \"x\"])", //
        "Dataset");
    check("Normal(ds[All, \"x\"]) // InputForm", //
        "{1,3,5}");
    // the point of the shape: indexing gives the value, not a one-row dataset
    check("ds[All, \"x\"][[1]]", //
        "1");
    check("Head(ds[All, \"x\"][[1]])", //
        "Integer");
    check("Total(ds[All, \"x\"])", //
        "9");
    // a column selected by number is a name given on its own too
    check("ds[All, 1][[2]]", //
        "3");
    // ... and it prints with no field name
    check("ds[All, \"x\"]", //
        "    |\r\n" + //
            "-----\r\n" + //
            " 1  |\r\n" + //
            " 3  |\r\n" + //
            " 5  |");

    // a list of one column is still a struct of one field
    check("ds[All, {\"x\"}]", //
        " x  |\r\n" + //
            "-----\r\n" + //
            " 1  |\r\n" + //
            " 3  |\r\n" + //
            " 5  |");
    // and two columns are unchanged
    check("Normal(ds[All, {\"x\", \"y\"}]) // InputForm", //
        "{<|\"x\"->1,\"y\"->2|>,<|\"x\"->3,\"y\"->4|>,<|\"x\"->5,\"y\"->6|>}");

    // a single cell is a value, not a vector of one
    check("ds[2, \"x\"]", //
        "3");
    // a row is still a row
    check("Normal(ds[2]) // InputForm", //
        "<|\"x\"->3,\"y\"->4|>");

    // the vector round trips through the places a vector is accepted
    check("Head(BarChart(ds[All, \"x\"]))", //
        "Graphics");
    check("Normal(Reverse(ds[All, \"x\"])) // InputForm", //
        "{5,3,1}");
  }

  @Test
  public void testBareVectorAndAssociationDatasets() {
    Config.FILESYSTEM_ENABLED = true;
    check("v = Dataset({3, 7, 11});av = Dataset(<|\"a\" -> 3, \"b\" -> 7|>);"
        + "rows = Dataset({<|\"x\" -> 1, \"y\" -> 2|>, <|\"x\" -> 3, \"y\" -> 4|>,"
        + "<|\"x\" -> 5, \"y\" -> 6|>});Head(v)", //
        "Dataset");

    // a bare vector
    check("Head(v)", //
        "Dataset");
    check("Normal(v)", //
        "{3,7,11}");
    // indexing down to one value gives the value, not a dataset of one
    check("v[[1]]", //
        "3");
    check("v[1]", //
        "3");
    check("Head(v[[1]])", //
        "Integer");
    // and an aggregation that reduces to an atom gives the atom
    check("Total(v)", //
        "21");
    check("Head(Total(v))", //
        "Integer");

    // a bare association
    check("Head(av)", //
        "Dataset");
    check("Normal(av)", //
        "<|a->3,b->7|>");
    check("av[\"a\"]", //
        "3");
    check("Head(av[\"a\"])", //
        "Integer");
    // it prints as the association it is, not as the key/value columns it is stored in
    check("StringContainsQ(ToString(av), \"key\")", //
        "False");

    // an operation whose result is still a collection stays wrapped
    check("Head(rows[All, Total])", //
        "Dataset");
    check("Normal(rows[All, Total])", //
        "{3,7,11}");
    check("Head(rows[All, \"x\"])", //
        "Dataset");

    // an empty collection is no dataset, and the table forms are untouched
    check("Head(Dataset({}))", //
        "Dataset");
    check("Head(Dataset({<|\"x\" -> 1|>}))", //
        "Dataset");
  }

  @Test
  public void testDatasetsChapterIdioms() {
    Config.FILESYSTEM_ENABLED = true;
    check("chap = Dataset(<|\"a\" -> <|\"x\" -> 1, \"y\" -> 2, \"z\" -> 3|>,"
        + "\"b\" -> <|\"x\" -> 4, \"y\" -> 5, \"z\" -> 6|>|>);"
        + "planets = Dataset({<|\"name\" -> \"Earth\", \"radius\" -> 6378|>,"
        + "<|\"name\" -> \"Mars\", \"radius\" -> 3396|>});Head(chap)", //
        "Dataset");

    // dataset[All, f] applies f to each row. The outer keys name the rows, so they key the result
    // and are not counted into it.
    check("Normal(chap[All, Total])", //
        "<|a->6,b->15|>");
    check("Head(chap[All, Total])", //
        "Dataset");
    // a dataset back, keyed by the row keys - Mathematica gives Dataset[<|"a" -> ..., ...|>]
    check("Head(chap[All, PieChart])", //
        "Dataset");
    check("Head(chap[All, PieChart][[1]])", //
        "Graphics");

    // dataset[f, "column"] applies f to that column, whatever f is. A built-in symbol always
    // worked; a function of one's own was not applied at all and the query stayed unevaluated.
    check("chap[Total, \"z\"]", //
        "9");
    check("chap[Mean, \"z\"]", //
        "9/2");
    check("chap[undefinedF, \"z\"]", //
        "undefinedf(<|a->3,b->6|>)");
    check("sq[u_] := u^2; chap[sq, \"z\"]", //
        "    |      |\r\n" + //
            "------------\r\n" + //
            " a  |   9  |\r\n" + //
            " b  |  36  |");

    // The rows of this dataset have names, so one of its columns is those values against those
    // names - Mathematica gives Association["a" -> 3, "b" -> 6] for this, not {3, 6}. A dataset
    // built from a list of rows has no names, and its column stays a plain list; `planets` below
    // is checked for that.
    check("Normal(chap[All, \"z\"])", //
        "<|a->3,b->6|>");
    // ... and the dataset as a whole is an association of its rows, not a list of rows each
    // carrying its own name under an empty key
    check("Normal(chap)", //
        "<|a-><|x->1,y->2,z->3|>,b-><|x->4,y->5,z->6|>|>");
    check("chap[f, All]", //
        "f(<|a-><|x->1,y->2,z->3|>,b-><|x->4,y->5,z->6|>|>)");
    // a row of a dataset is a dataset, as the reference gives it - `Dataset[<|"x" -> 1, …|>]` and
    // not the bare association
    check("Head(chap[\"a\"])", //
        "Dataset");
    check("Normal(chap[\"a\"])", //
        "<|x->1,y->2,z->3|>");
    check("chap[\"a\"][\"x\"]", //
        "1");
    // a scalar still comes back bare: a dataset wraps a collection
    check("Head(Dataset(<|\"a\" -> 1, \"b\" -> 2|>)[\"a\"])", //
        "Integer");

    // A bare string is a key, not a column. On a dataset of rows there are no keys, and the
    // reference answers Failure rather than guessing a column; a column is asked for with All.
    check("Head(planets[\"radius\"])", //
        "Failure");
    check("Head(planets[All, \"radius\"])", //
        "Dataset");
    check("Normal(planets[All, \"radius\"])", //
        "{6378,3396}");
    check("Head(planets[All, \"radius\"][Histogram])", //
        "Graphics");
    check("Head(planets[All, \"radius\"][BarChart])", //
        "Graphics");
    // on a dataset built from an association of associations the key names its row
    check("Normal(Dataset(<|\"a\" -> <|\"x\" -> 1|>, \"b\" -> <|\"x\" -> 2|>|>)[\"a\"]) // InputForm", //
        "<|\"x\"->1|>");
    check("Dataset(<|\"a\" -> <|\"x\" -> 1|>, \"b\" -> <|\"x\" -> 2|>|>)[\"a\"]", //
        "    |     |\r\n" + //
            "-----------\r\n" + //
            " x  |  1  |");

    // the charts take a dataset and an association directly
    check("Head(BarChart(planets[All, \"radius\"]))", //
        "Graphics");
    check("Head(Histogram(planets[All, \"radius\"]))", //
        "Graphics");
    check("Head(PieChart(<|\"a\" -> 1, \"b\" -> 2|>))", //
        "Graphics");
    check("Head(WordCloud(<|\"cat\" -> 10, \"dog\" -> 5|>))", //
        "Graphics");
    // the whole plot family, not only the charts the chapter names
    check("Map(Function(Head(#(planets[All, \"radius\"]))), {ListPlot, ListLinePlot, ListLogPlot,"
        + " ListLogLogPlot, ListLogLinearPlot, ListStepPlot, ListPolarPlot, BarChart, Histogram,"
        + " PieChart, BoxWhiskerChart, WordCloud}) // DeleteDuplicates", //
        "{Graphics}");
    // an association's keys label the chart unless ChartLabels says otherwise
    check("FreeQ(PieChart(<|\"a\" -> 1, \"b\" -> 2|>), \"a\")", //
        "False");

    check("Head(chap[Select(Function(#z > 5))])", //
        "Dataset");
    check("Catenate(planets)", //
        "{Earth,6378,Mars,3396}");
    check("Total(planets[All, \"radius\"])", //
        "9774");
  }

  /**
   * <code>SortBy</code> on a dataset, in both the shapes it comes in. Column names sort the dataset
   * and it stays one; a function sorts the rows by what the function says of each.
   */
  /**
   * The structural built-ins give the rows, not a <code>Dataset</code> wrapped round one-row
   * datasets.
   *
   * <p>
   * <code>Take</code> and friends built their result with the head of what they were given, which
   * for a dataset produced <code>Dataset(row, row)</code> - the same defect <code>Part</code> had.
   * <code>Rest</code> did not get that far and threw. They all walk the rows now, which is what
   * <code>Drop</code> already did and what leaves the family agreeing with itself.
   */
  @Test
  public void testStructuralBuiltInsGiveRows() {
    Config.FILESYSTEM_ENABLED = true;
    check("td = Dataset({<|\"x\" -> 1, \"y\" -> 2|>, <|\"x\" -> 3, \"y\" -> 4|>,"
        + "<|\"x\" -> 5, \"y\" -> 6|>});Head(td)", //
        "Dataset");

    // the one that threw
    check("Normal(Rest(td))", //
        "{<|x->3,y->4|>,<|x->5,y->6|>}");

    check("Normal(Take(td, 2))", //
        "{<|x->1,y->2|>,<|x->3,y->4|>}");
    check("Normal(Drop(td, 1))", //
        "{<|x->3,y->4|>,<|x->5,y->6|>}");
    check("Normal(Most(td))", //
        "{<|x->1,y->2|>,<|x->3,y->4|>}");
    check("Normal(Reverse(td))", //
        "{<|x->5,y->6|>,<|x->3,y->4|>,<|x->1,y->2|>}");
    check("Length(Join(td, td))", //
        "6");
    check("Normal(Cases(td, _))", //
        "{<|x->1,y->2|>,<|x->3,y->4|>,<|x->5,y->6|>}");
    // a dataset is already flat, and the reference gives it straight back
    check("Normal(Flatten(td))", //
        "{<|x->1,y->2|>,<|x->3,y->4|>,<|x->5,y->6|>}");

    // each of them stays a Dataset, which is what Mathematica gives - none is the Dataset head
    // wrapped round one-row datasets any more, and none is a bare list
    check("Map(Head, {Rest(td), Take(td, 2), Drop(td, 1), Most(td), Reverse(td), Join(td, td),"
        + " Cases(td, _), Flatten(td), First(td), Last(td)}) // DeleteDuplicates", //
        "{Dataset}");

    // and each agrees with the same question asked of the rows
    check("Normal(Take(td, 2)) === Take(Normal(td), 2)", //
        "True");
    check("Normal(Reverse(td)) === Reverse(Normal(td))", //
        "True");
  }

  @Test
  public void testSortByOnADataset() {
    Config.FILESYSTEM_ENABLED = true;
    check("sd = Dataset({<|\"x\" -> 3, \"y\" -> 1|>, <|\"x\" -> 1, \"y\" -> 2|>,"
        + "<|\"x\" -> 2, \"y\" -> 9|>});Head(sd)", //
        "Dataset");

    // column names: still a dataset, sorted on that column
    check("Head(SortBy(sd, {\"x\"}))", //
        "Dataset");

    // a function: the rows in the order the function puts them, and a dataset like the column
    // name form above - a collection comes back as a dataset however it was arrived at
    check("Head(SortBy(sd, Function(#x)))", //
        "Dataset");
    check("Normal(SortBy(sd, Function(#x))) // InputForm", //
        "{<|\"x\"->1,\"y\"->2|>,<|\"x\"->2,\"y\"->9|>,<|\"x\"->3,\"y\"->1|>}");
    // and the operator form the chapter writes
    check("sd[SortBy(Function(#x))]", //
        " x  |  y  |\r\n" + //
            "-----------\r\n" + //
            " 1  |  2  |\r\n" + //
            " 2  |  9  |\r\n" + //
            " 3  |  1  |");
    // the rows are the same ones, wrapped: a dataset is not === the list it holds
    check("Normal(sd[SortBy(Function(#x))]) === SortBy(Normal(sd), Function(#x))", //
        "True");
    // a two-column key, as the chapter's SortBy(#x - #y &)
    check("sd[SortBy(Function(#x - #y))]", //
        " x  |  y  |\r\n" + //
            "-----------\r\n" + //
            " 2  |  9  |\r\n" + //
            " 1  |  2  |\r\n" + //
            " 3  |  1  |");

    // Select's operator form keeps working and keeps being a dataset
    check("Head(sd[Select(Function(#y > 1))])", //
        "Dataset");
  }

  @Test
  public void testBuiltInsWalkTheRowsOfADataset() {
    Config.FILESYSTEM_ENABLED = true;
    check("q = Dataset({<|\"n\" -> \"Earth\", \"r\" -> 6378|>, <|\"n\" -> \"Mars\", \"r\" -> 3396|>});"
        + "col = q[All, \"r\"];Head(col)", //
        "Dataset");

    // the four that aborted
    check("Total(col)", //
        "9774");
    check("Apply(Plus, col)", //
        "9774");
    // Sort and Union give a dataset back - a collection stays one - while Total gives the number
    check("Head(Sort(col))", //
        "Dataset");
    check("Normal(Sort(col)) // InputForm", //
        "{3396,6378}");
    check("Head(Union(col))", //
        "Dataset");
    check("Normal(Union(col)) // InputForm", //
        "{3396,6378}");
    // and each agrees with the same question asked of the rows
    check("Total(col) === Total(Normal(col))", //
        "True");
    check("Normal(Sort(col)) === Sort(Normal(col))", //
        "True");
    check("Normal(Union(col)) === Union(Normal(col))", //
        "True");

    // Total of a whole dataset totals each column, as it does for a list of associations
    check("Total(q)", //
        "<|n->Earth+Mars,r->9774|>");

    check("Mean(col)", //
        "4887");
    check("Median(col)", //
        "4887");
    check("Catenate(q)", //
        "{Earth,6378,Mars,3396}");
    check("Tally(col)", //
        "{{6378,1},{3396,1}}");
    check("Counts(col)", //
        "<|3396->1,6378->1|>");
    check("Head(DeleteDuplicates(col))", //
        "Dataset");
    check("Normal(DeleteDuplicates(col)) // InputForm", //
        "{6378,3396}");
    // Values gives a dataset of the values of each row, as the reference does
    check("Head(Values(q))", //
        "Dataset");
    check("Normal(Values(q)) // InputForm", //
        "{{\"Earth\",6378},{\"Mars\",3396}}");

    // Map sees each row as the association it is, not as a one-row dataset
    check("Map(f, q)", //
        "{f(<|n->Earth,r->6378|>),f(<|n->Mars,r->3396|>)}");
    check("Map(f, col)", //
        "{f(6378),f(3396)}");

    // what already worked, so that it keeps working
    check("Length(q)", //
        "2");
    check("Dimensions(q)", //
        "{2,2}");
    // one entry per row - see testKeysAndValuesOfADataset
    check("Normal(Keys(q))[[1]] // InputForm", //
        "{\"n\",\"r\"}");
    check("Length(Normal(Keys(q)))", //
        "2");
  }

  @Test
  public void testRandomSampleOfADataset() {
    Config.FILESYSTEM_ENABLED = true;
    check("t = SemanticImportString(\"name,age\nAnn,29\nBob,1\nCid,2\nDee,25\nEve,48\n\");Head(t)", //
        "Dataset");

    check("Head(RandomSample(t, 2))", //
        "Dataset");
    check("Length(Normal(RandomSample(t, 2)))", //
        "2");
    // no count: every row, in a random order
    check("Length(Normal(RandomSample(t)))", //
        "5");
    check("Length(Normal(RandomSample(t, 5)))", //
        "5");
    // more than there are is reported and left alone, for a dataset as for a list: "RandomSample
    // is also known as sampling without replacement", so there is no honest answer to give
    check("RandomSample(Range(5), 99) // Head", //
        "RandomSample");
    check("RandomSample(t, 99) // Head", //
        "RandomSample");
    // UpTo is the form that asks for as many as are available
    check("Length(RandomSample(Range(5), UpTo(99)))", //
        "5");
    check("Length(Normal(RandomSample(t, UpTo(99))))", //
        "5");
    check("Length(Normal(RandomSample(t, UpTo(2))))", //
        "2");
    // none, and fewer than none
    check("Normal(RandomSample(t, 0))", //
        "{}");
    check("RandomSample(Range(5), -1) // Head", //
        "RandomSample");
    check("RandomSample(t, -1) // Head", //
        "RandomSample");

    // the sample is drawn from the whole dataset and nothing is invented
    check("Complement(Normal(RandomSample(t, 5))[[All, \"name\"]], Normal(t)[[All, \"name\"]])", //
        "{}");

    // SeedRandom governs it, and governs it the same way for a dataset and for the rows as a list.
    // RandomSample used to shuffle through hipparchus's own generator, which SeedRandom does not
    // reach, so neither of these held.
    check("SeedRandom(7); a = Normal(RandomSample(t, 3))[[All, \"name\"]];"
        + "SeedRandom(7); b = Normal(RandomSample(t, 3))[[All, \"name\"]]; a === b", //
        "True");
    check("SeedRandom(7); a = Normal(RandomSample(t, 3))[[All, \"name\"]];"
        + "SeedRandom(7); b = RandomSample(Normal(t), 3)[[All, \"name\"]]; a === b", //
        "True");

    // the display options come along
    check("StringContainsQ(ToString(RandomSample(Dataset(Normal(t), HiddenItems -> \"age\"), 2)),"
        + " \"age\")", //
        "False");

    // the operator form gives a dataset as well, so the servlet can draw it
    check("Head(t(RandomSample))", //
        "Dataset");
    check("Length(Normal(t(RandomSample)))", //
        "5");
  }

  @Test
  public void testSemanticImportOptions() {
    Config.FILESYSTEM_ENABLED = true;
    check("Options(SemanticImport)", //
        "{Delimiters->Automatic,ExcludedLines->None,HeaderLines->1,IncludeMetaInformation->False,"
            + "MissingValuePattern->Automatic}");
    // HeaderLines -> 0 reads the first line as data, so the columns get generated names
    check("Normal(SemanticImportString(\"a,b\n1,2\n\", HeaderLines -> 0))", //
        "{<|C0->a,C1->b|>,<|C0->1,C1->2|>}");
    check("Normal(SemanticImportString(\"x;y\n1;2\n\", Delimiters -> \";\"))", //
        "<|x->1,y->2|>");
    // the positional arguments still work with options after them
    check(
        "SemanticImportString(\"1 2, 3 ; 4, 5 6\", \"String\", \"List\", Delimiters -> \",\") // InputForm", //
        "{\"1 2\",\" 3 ; 4\",\" 5 6\"}");
  }

  @Test
  public void testDataset() {

    Config.FILESYSTEM_ENABLED = true;
    // check("ds=SemanticImport(\"./data/color2_data.csv\") //Normal //InputForm", //
    // "");
    // check("ds=SemanticImport(\"./data/color2_data.csv\");ds(All, {\"r\",\"g\",\"b\"})//Normal
    // //Values
    // //InputForm", //
    // "");

    // check(
    // "dset =
    // SemanticImport(\"https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/data/whiskey.csv\")",
    // //
    // "");
    check(
        "dset=Dataset@<|101 -> <|\"t\" -> 42, \"r\" -> 7.5`|>, 102 -> <|\"t\" -> 42, \"r\" -> 7.5`|>, 103 -> <|\"t\" -> 42, \"r\" -> 7.5`|>|>", //
        "      |  t   |   r   |\r\n" + "----------------------\r\n" + " 101  |  42  |  7.5  |\r\n"
            + " 102  |  42  |  7.5  |\r\n" + " 103  |  42  |  7.5  |");

    // the rows of this dataset have names - 101, 102, 103 - so they key the result rather than
    // riding along inside each row under an empty key
    check("Normal(dset) //InputForm", //
        "<|101-><|\"t\"->42,\"r\"->7.5`|>," + "102-><|\"t\"->42,\"r\"->7.5`|>,"
            + "103-><|\"t\"->42,\"r\"->7.5`|>|>");
    check("dset[1,1]", //
        "101");
    check("dset[2,3]", //
        "7.5");
  }
}
