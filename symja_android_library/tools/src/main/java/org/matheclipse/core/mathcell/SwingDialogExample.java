package org.matheclipse.core.mathcell;

public class SwingDialogExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    // return "DialogInput(\n" +
    // " DialogNotebook({Row({DefaultButton(DialogReturn(True)),\n" +
    // " CancelButton(DialogReturn(False))})}))";
    return "DialogInput(DialogNotebook({Column({\"column\", InputField(Dynamic(x), String), Button(\"Test\", DialogReturn(\"Test button\")), CancelButton( ), DefaultButton(x)})}))";
    // return "DialogInput(DialogNotebook(Row({TextCell(\"Click the DoIt button:\"),
    // Button(\"DoIt\", DialogReturn(\"Hello dialog world\"))})))";
  }

  public static void main(String[] args) {
    SwingDialogExample p = new SwingDialogExample();
    p.generateHTML();
  }
}
