package org.matheclipse.core.mathcell;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

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
