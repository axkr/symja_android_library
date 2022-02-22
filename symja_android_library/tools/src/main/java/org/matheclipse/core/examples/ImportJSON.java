package org.matheclipse.core.examples;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.io.IOInit;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class ImportJSON {
  public static void main(String[] args) {
    try {
      Config.FILESYSTEM_ENABLED = true;
      IOInit.init();

      ExprEvaluator util = new ExprEvaluator();
      IExpr expr = util.eval("Import(\"https://json-stat.org/samples/oecd.json\",\"JSON\")");

      StringBuilder buf = new StringBuilder();
      OutputFormFactory off = OutputFormFactory.get(false, false);
      off.setIgnoreNewLine(true);
      off.setInputForm(true);
      if (off.convert(buf, expr)) {
        System.out.println(buf);
      }

    } catch (SyntaxError e) {
      // catch Symja parser errors here
      System.out.println(e.getMessage());
    } catch (MathException me) {
      // catch Symja math errors here
      System.out.println(me.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    } catch (final StackOverflowError soe) {
      System.out.println(soe.getMessage());
    } catch (final OutOfMemoryError oome) {
      System.out.println(oome.getMessage());
    }
  }
}
