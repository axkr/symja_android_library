package org.matheclipse.io.test;

import org.junit.Test;
import org.matheclipse.core.basic.Config;

public class SemanticImportTestCase extends AbstractTestCase {

  @Test
  public void testSemanticImport() {
    String s = System.getProperty("os.name");

    Config.FILESYSTEM_ENABLED = true;
    // check("ds=SemanticImport(\"./data/color2_data.csv\") //Normal //InputForm", //
    // "");
    // check("ds=SemanticImport(\"./data/color2_data.csv\");ds(All, {\"r\",\"g\",\"b\"})//Normal
    // //Values
    // //InputForm", //
    // "");

    if (s.contains("Windows")) {
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
          "{2,3}");

      check("dset[2]", //
          " a  |  b  |    c    |\r\n" + //
              "---------------------\r\n" + //
              " 2  |  y  |  {2,3}  |");

      check("dset[5, \"c\"]", //
          "{5,6,7}");

      check("dset[All, \"a\"]", //
          " a  |\r\n" + //
              "-----\r\n" + //
              " 1  |\r\n" + //
              " 2  |\r\n" + //
              " 3  |\r\n" + //
              " 4  |\r\n" + //
              " 5  |\r\n" + //
              " 6  |");

      check("dset[All, \"c\", 1]", //
          "           c            |\r\n" + //
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
  }

  @Test
  public void testSemanticImportString() {
    String s = System.getProperty("os.name");
    if (s.contains("Windows")) {
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
          "                                                                   \r\n" + //
              " Products  |  Sales  |  Market_Share  |     Date     |    Time    |\r\n" + //
              "-------------------------------------------------------------------\r\n" + //
              "        a  |  12200  |             4  |  1950-01-03  |  11:10:00  |");
      check("Keys(ds)", //
          "{Products,Sales,Market_Share,Date,Time}");
      check("ds[[1,2]]", //
          "12200");
      check("ds(TakeLargest(2), \"Sales\") ", //
          "{60000,12200}");
      // TODO rewrite GroupBy
      check("ds(GroupBy(\"Sales\"), \"Sales\") ", //
          "         \r\n" + //
              " Sales  |\r\n" + //
              "---------\r\n" + //
              "  5500  |\r\n" + //
              " 12200  |\r\n" + //
              " 60000  |");
      // TODO rewrite SortBy
      check("ds(SortBy(\"Sales\"), \"Sales\") ", //
          "         \r\n" + //
              " Sales  |\r\n" + //
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
      check("ds(Select(#Products == \"a\" &), {\"Products\", \"Market_Share\"})", //
          "                             \r\n" + //
              " Products  |  Market_Share  |\r\n" + //
              "-----------------------------\r\n" + //
              "        a  |             4  |");
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
          "<|60000->1,12200->1,5500->1|>");

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
          "                \r\n" + //
              " Market_Share  |\r\n" + //
              "----------------\r\n" + //
              "            4  |\r\n" + //
              "            3  |\r\n" + //
              "           33  |");

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
          "                                                                   \r\n" + //
              " Products  |  Sales  |  Market_Share  |     Date     |    Time    |\r\n" + //
              "-------------------------------------------------------------------\r\n" + //
              "        b  |   5500  |             3  |  1970-12-31  |  23:10:00  |");

      check("ds(2) // Normal", //
          "<|Products->b,Sales->5500,Market_Share->3,Date->1970-12-31T00:00,Time->23:10:00|>");

      // row 3 column 2
      check("ds(3, 2)", //
          "60000");

      // all rows column 2
      check("ds(All, 2)", //
          "         \r\n" + //
              " Sales  |\r\n" + //
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
  }

  @Test
  public void testSemanticImportStringToList() {
    String s = System.getProperty("os.name");
    if (s.contains("Windows")) {
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
  }

  @Test
  public void testSemanticImportStringWikipedia() {
    // https://en.wikipedia.org/wiki/Comma-separated_values
    String s = System.getProperty("os.name");
    if (s.contains("Windows")) {
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
  }

  @Test
  public void testDataset() {
    String s = System.getProperty("os.name");

    Config.FILESYSTEM_ENABLED = true;
    // check("ds=SemanticImport(\"./data/color2_data.csv\") //Normal //InputForm", //
    // "");
    // check("ds=SemanticImport(\"./data/color2_data.csv\");ds(All, {\"r\",\"g\",\"b\"})//Normal
    // //Values
    // //InputForm", //
    // "");

    if (s.contains("Windows")) {
      // check(
      // "dset =
      // SemanticImport(\"https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/data/whiskey.csv\")",
      // //
      // "");
      check(
          "dset=Dataset@<|101 -> <|\"t\" -> 42, \"r\" -> 7.5`|>, 102 -> <|\"t\" -> 42, \"r\" -> 7.5`|>, 103 -> <|\"t\" -> 42, \"r\" -> 7.5`|>|>", //
          "      |  t   |   r   |\r\n" + "----------------------\r\n" + " 101  |  42  |  7.5  |\r\n"
              + " 102  |  42  |  7.5  |\r\n" + " 103  |  42  |  7.5  |");

      check("Normal(dset) //InputForm", //
          "{<|\"\"->101,\"t\"->42,\"r\"->7.5`|>," + "<|\"\"->102,\"t\"->42,\"r\"->7.5`|>,"
              + "<|\"\"->103,\"t\"->42,\"r\"->7.5`|>}");
      check("dset[1,1]", //
          "101");
      check("dset[2,3]", //
          "7.5");
    }
  }
}
