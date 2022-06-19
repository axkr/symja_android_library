package org.matheclipse.core.system;

public class BoxesTest extends ExprEvaluatorTestCase {
  public BoxesTest(String name) {
    super(name);
  }

  public void testMakeBoxes001() {
    check("MakeBoxes[Beta[a, b], StandardForm] // InputForm", //
        "RowBox({\"Beta\",\"[\",RowBox({\"a\",\",\",\"b\"}),\"]\"})");
  }

  public void testMakeBoxesList() {
    check("MakeBoxes[{a,b}]// InputForm", //
        "RowBox({\"{\",RowBox({\"a\",\",\",\"b\"}),\"}\"})");
  }

  public void testMakeBoxesAssociation() {
    check("MakeBoxes[<|a->b, c:>d|>] // InputForm", //
        "RowBox({\"Association\",\"[\",RowBox({RowBox({\"a\",\"\\[Rule]\",\"b\"}),\",\",RowBox({\"c\",\"\\[RuleDelayed]\",\"d\"})}),\"]\"})");
  }

  public void testToBoxes001() {
    check("Map[ToBoxes, {123, 1/23, 1.23, 1 + 23 I}] // InputForm", //
        "{\"123\",FractionBox(\"1\",\"23\"),\"1.23\",RowBox({\"1\",\"+\",RowBox({\"23\",\" \",\"\\[ImaginaryI]\"})})}");
  }
}
