package org.matheclipse.core.mathcell;

public class DatasetExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    // return "TreeForm(test)";
    // return "TreeForm(a+b)";
    // return "TreeForm(HornerForm(1 + x + x^2 + x^3, x),2)";
    return "JSForm(SemanticImportString(\"Products,Sales,Market_Share\n"
        + //
        "a,5500,3\n"
        + //
        "b,12200,4\n"
        + //
        "c,60000,33\n"
        + //
        "\"))";
    // return "TreeForm(a+b^3+c^2+d*Pi*e)";
  }

  public static void main(String[] args) {
    DatasetExample p = new DatasetExample();
    p.generateHTML();
  }
}
