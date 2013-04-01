package org.matheclipse.core.test.examples;

import javax.swing.JFrame;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.swing.plot.ParametricPlotter;
import org.matheclipse.swing.plot.PlotWindowAdapter;

public class ParametricPlotExample {
	public static void main(String[] args) {
		try {
			// initialize the engine:
			F.initSymbols(null, null, false);
			Config.SWING_PLOT_FRAME = false;
			Config.SERVER_MODE = true;

			// create an evaluation utility
			EvalUtilities util = new EvalUtilities();
			// evaluate the plot
			String plotterString = "ParametricPlot[{Sin[t Cos[t]], Sin[2t]}, {t, 0, 2Pi}]";
			IExpr result = util.evaluate(plotterString);
			StringBufferWriter buf = new StringBufferWriter();
			OutputFormFactory.get().convert(buf, result);
			// print the result in the console
			System.out.println(buf.toString());

			// b) show the plot directly in a JFrame (Plotter extends JPanel):
			Parser parser = new Parser();
			ASTNode node = parser.parse(plotterString);
			IAST ast = (IAST) AST2Expr.CONST.convert(node);
			ParametricPlotter plotter = ParametricPlotter.getParametricPlotter();
			plotter.plot(ast);
			// To simplify the following you can use a PlotFrame:
			// PlotFrame frame = new PlotFrame(plotter, ast);
			// frame.invokeLater();
			JFrame frame = new JFrame();
			frame.setContentPane(plotter);
			frame.setSize(640, 400);
			frame.setLocationRelativeTo(null);
			frame.addWindowListener(new PlotWindowAdapter(plotter, true));
			frame.setVisible(true);
		} catch (SyntaxError e) {
			// catch parser errors here
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
