package org.matheclipse.core.mathcell;

public class ListLinePlotExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    return "ListLinePlot({1, 1, 2, 3, 5, 8})";
    // return "ListLinePlot(Table(ElementData(z, \"MeltingPoint\"), {z, 118}))";
    // return "Manipulate(ListLinePlot(Table({Sin(t), Cos(t*a)}, {t, 50})), {a,1,4,1})";
  }

  public static void main(String[] args) {
    ListLinePlotExample p = new ListLinePlotExample();
    p.generateHTML();
  }
}
