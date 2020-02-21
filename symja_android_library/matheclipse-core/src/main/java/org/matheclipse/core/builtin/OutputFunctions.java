
package org.matheclipse.core.builtin;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.ASTDataset;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.form.output.DoubleFormFactory;
import org.matheclipse.core.form.output.JavaDoubleFormFactory;
import org.matheclipse.core.form.output.JavaScriptFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.HornerScheme;

public final class OutputFunctions {

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.BaseForm.setEvaluator(new BaseForm());
			F.CForm.setEvaluator(new CForm());
			F.FullForm.setEvaluator(new FullForm());
			F.HoldForm.setEvaluator(new HoldForm());
			F.HornerForm.setEvaluator(new HornerForm());
			F.InputForm.setEvaluator(new InputForm());
			F.JavaForm.setEvaluator(new JavaForm());
			F.JSForm.setEvaluator(new JSForm());
			F.MathMLForm.setEvaluator(new MathMLForm());
			F.TableForm.setEvaluator(new TableForm());
			F.TeXForm.setEvaluator(new TeXForm());
			F.TreeForm.setEvaluator(new TreeForm());
		}
	}

	private static class BaseForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = engine.evaluate(ast.arg1());
			IExpr arg2 = engine.evaluate(ast.arg2());
			if (arg1.isInteger() && arg2.isInteger()) {
				int base = arg2.toIntDefault();
				if (base > 0 && base <= 36) {
					BigInteger big = ((IInteger) arg1).toBigNumerator();
					String str = big.toString(base);
					return F.Subscript(F.$str(str), arg2);
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
		}
	}

	private static class CForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// CFormUtilities texUtil = new CFormUtilities(engine, engine.isRelaxedSyntax());
			// IExpr arg1 = engine.evaluate(ast.arg1());
			// StringWriter stw = new StringWriter();
			// texUtil.toCForm(arg1, stw);
			// return F.$str(stw.toString());
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * FullForm(expression)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * shows the internal representation of the given <code>expression</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * FullForm shows the difference in the internal expression representation:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; FullForm(x(x+1))
	 * "x(Plus(1, x))"
	 * 
	 * &gt;&gt;&gt; FullForm(x*(x+1))
	 * "Times(x, Plus(1, x))"
	 * </pre>
	 */
	private static class FullForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return F.stringx(engine.evaluate(ast.arg1()).fullFormString(), IStringX.APPLICATION_SYMJA);
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
		}
	}

	private static class TableForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				int[] dim = arg1.isMatrix();
				if (dim != null) {
					IAST matrix = (IAST) arg1;
					StringBuilder[] sb = new StringBuilder[dim[0]];
					for (int j = 0; j < dim[0]; j++) {
						sb[j] = new StringBuilder();
					}
					int rowLength = 0;
					for (int i = 0; i < dim[1]; i++) {
						int columnLength = 0;
						for (int j = 0; j < dim[0]; j++) {
							String str = matrix.getPart(j + 1, i + 1).toString();
							if (str.length() > columnLength) {
								columnLength = str.length();
							}
							sb[j].append(str);
						}
						if (i < dim[1] - 1) {
							rowLength += columnLength + 1;
						} else {
							rowLength += columnLength;
						}
						for (int j = 0; j < dim[0]; j++) {
							int rest = rowLength - sb[j].length();
							for (int k = 0; k < rest; k++) {
								sb[j].append(' ');
							}
						}
					}
					StringBuilder result = new StringBuilder();
					for (int i = 0; i < dim[0]; i++) {
						result.append(sb[i]);
						if (i < dim[0] - 1) {
							result.append("\n");
						}
					}
					return F.stringx(result.toString(), IStringX.TEXT_PLAIN);
				}
				if (arg1.isList()) {
					IAST list = (IAST) arg1;
					StringBuilder sb = new StringBuilder();
					for (int i = 1; i < list.size(); i++) {
						sb.append(list.get(i).toString());
						sb.append("\n");
					}
					return F.stringx(sb.toString(), IStringX.TEXT_PLAIN);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	/**
	 * <pre>
	 * HoldForm(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * <code>HoldForm</code> doesn't evaluate <code>expr</code> and didn't appear in the output
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; HoldForm(3*2)
	 * 3*2
	 * </pre>
	 */
	private static class HoldForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * HornerForm(polynomial)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Generate the horner scheme for a univariate <code>polynomial</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * HornerForm(polynomial, x)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * Generate the horner scheme for a univariate <code>polynomial</code> in <code>x</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See:
	 * </p>
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Horner_scheme">Wikipedia - Horner scheme</a></li>
	 * <li><a href="https://rosettacode.org/wiki/">Rosetta Code - Horner's rule for polynomial evaluation</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; HornerForm(3+4*x+5*x^2+33*x^6+x^8)
	 * 3+x*(4+x*(5+(33+x^2)*x^4))
	 * 
	 * &gt;&gt; HornerForm(a+b*x+c*x^2,x)
	 * a+x*(b+c*x)
	 * </pre>
	 */
	private static class HornerForm extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isAST()) {

				IAST poly = (IAST) arg1;
				VariablesSet eVar;
				IAST variables;
				if (ast.isAST2()) {
					variables = Validate.checkIsVariableOrVariableList(ast, 2, engine);
				} else {
					eVar = new VariablesSet(ast.arg1());
					variables = eVar.getVarList();
				}
				if (variables.isPresent()) {
					if (variables.size() >= 2) {
						ISymbol sym = (ISymbol) variables.arg1();
						if (poly.isPlus()) {
							HornerScheme scheme = new HornerScheme();
							return scheme.generate(engine.isNumericMode(), poly, sym);
						}
					}
				}
			}
			return arg1;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private static class InputForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
					return F.stringx(StringFunctions.inputForm(arg1, true), IStringX.APPLICATION_SYMJA);
				}
				return F.stringx(StringFunctions.inputForm(arg1, false), IStringX.APPLICATION_SYMJA);
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
		}
	}

	/**
	 * <pre>
	 * JavaForm(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the Symja Java form of the <code>expr</code>. In Java you can use the created Symja expressions.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * JavaForm can add the <code>F.</code> prefix for class <code>org.matheclipse.core.expression.F</code> if you set
	 * <code>prefix-&gt;True</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; JavaForm(D(sin(x)*cos(x),x), prefix-&gt;True)
	 * "F.Plus(F.Sqr(F.Cos(F.x)),F.Negate(F.Sqr(F.Sin(F.x))))"
	 * 
	 * &gt;&gt; JavaForm(I/2*E^((-I)*x)-I/2*E^(I*x))
	 * "Plus(Times(CC(0L,1L,1L,2L),Power(E,Times(CNI,x))),Times(CC(0L,1L,-1L,2L),Power(E,Times(CI,x))))"
	 * </pre>
	 * <p>
	 * JavaForm evaluates its argument before creating the Java form:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; JavaForm(D(sin(x)*cos(x),x))
	 * "Plus(Sqr(Cos(x)),Negate(Sqr(Sin(x))))"
	 * </pre>
	 * <p>
	 * You can use <code>Hold</code> to suppress the evaluation:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; JavaForm(Hold(D(sin(x)*cos(x),x)))
	 * "D(Times(Sin(x),Cos(x)),x)"
	 * 
	 * &gt;&gt; JavaForm(Hold(D(sin(x)*cos(x),x)), prefix-&gt;True)
	 * "F.D(F.Times(F.Sin(F.x),F.Cos(F.x)),F.x)"
	 * </pre>
	 */
	private static class JavaForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				IExpr arg1 = engine.evaluate(ast.arg1());
				boolean floatJava = false;
				boolean strictJava = false;
				boolean usePrefix = false;
				if (ast.isAST2()) {
					IExpr arg2 = engine.evaluate(ast.arg2());
					if (arg2 == F.Float) {
						floatJava = true;
					} else if (arg2 == F.Strict) {
						strictJava = true;
					} else if (arg2 == F.Prefix) {
						usePrefix = true;
					} else {
						final OptionArgs options = new OptionArgs(ast.topHead(), arg2, engine);
						floatJava = options.isTrue(F.Float);
						strictJava = options.isTrue(F.Strict);
						usePrefix = options.isTrue(F.Prefix);
					}
				}
				if (floatJava) {
					return F.$str(toJavaDouble(arg1), IStringX.APPLICATION_JAVA);
				}
				String resultStr = javaForm(arg1, strictJava, usePrefix);
				return F.$str(resultStr, IStringX.APPLICATION_JAVA);
			} catch (Exception rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
				return engine.printMessage("JavaForm: " + rex.getMessage());
			}
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

		public static String javaForm(IExpr arg1, boolean strictJava, boolean usePrefix) {
			return arg1.internalJavaString(strictJava, 0, false, usePrefix, false);
		}

	}

	private static class JSForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1.isAST(F.JSFormData, 3)) {
					String manipulateStr = ((IAST) arg1).arg1().toString();
					return F.$str(manipulateStr, IStringX.APPLICATION_JAVASCRIPT);
				}
				if (arg1 instanceof ASTDataset) {
  					return F.$str(ASTDataset.datasetToJSForm((ASTDataset) arg1), IStringX.TEXT_HTML);
				}
				if (arg1 instanceof GraphExpr) {
					return F.$str(GraphFunctions.graphToJSForm((GraphExpr) arg1), IStringX.APPLICATION_JAVASCRIPT);
				}
				return F.$str(toJavaScript(arg1), IStringX.APPLICATION_JAVASCRIPT);
			} catch (Exception rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
				return engine.printMessage("JSForm: " + rex.getMessage());
			}
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

	}

	/**
	 * <pre>
	 * MathMLForm(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the MathMLForm form of the evaluated <code>expr</code>.
	 * </p>
	 * </blockquote>
	 */
	private static class MathMLForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			MathMLUtilities mathMLUtil = new MathMLUtilities(engine, false, engine.isRelaxedSyntax());
			IExpr arg1 = ast.arg1();
			StringWriter stw = new StringWriter();
			mathMLUtil.toMathML(arg1, stw);
			return F.stringx(stw.toString(), IStringX.TEXT_MATHML);
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * TeXForm(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the TeX form of the evaluated <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt;&gt; TeXForm(D(sin(x)*cos(x),x))
	 * "{\cos(x)}^{2}-{\sin(x)}^{2}"
	 * </pre>
	 */
	private static class TeXForm extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			TeXUtilities texUtil = new TeXUtilities(engine, engine.isRelaxedSyntax());
			IExpr arg1 = engine.evaluate(ast.arg1());
			StringWriter stw = new StringWriter();
			texUtil.toTeX(arg1, stw);
			return F.$str(stw.toString(), IStringX.TEXT_LATEX);
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private static class TreeForm extends AbstractCoreFunctionEvaluator {
		private static void vertexToVisjs(StringBuilder buf, List<SimpleImmutableEntry<String, Integer>> vertexSet) {
			buf.append("var nodes = new vis.DataSet([\n");
			boolean first = true;
			int counter = 1;
			for (SimpleImmutableEntry<String, Integer> expr : vertexSet) {
				// {id: 1, label: 'Node 1'},
				if (first) {
					buf.append("  {id: ");
				} else {
					buf.append(", {id: ");
				}
				buf.append(counter++);
				buf.append(", label: '");
				buf.append(expr.getKey().toString());
				buf.append("', level: ");
				buf.append(expr.getValue().toString());
				buf.append("}\n");
				first = false;
			}
			buf.append("]);\n");
		}

		private static void edgesToVisjs(StringBuilder buf, List<SimpleImmutableEntry<Integer, Integer>> edgeSet) {
			boolean first = true;

			buf.append("var edges = new vis.DataSet([\n");
			for (SimpleImmutableEntry<Integer, Integer> edge : edgeSet) {
				// {from: 1, to: 3},
				if (first) {
					buf.append("  {from: ");
				} else {
					buf.append(", {from: ");
				}
				buf.append(edge.getKey());
				buf.append(", to: ");
				buf.append(edge.getValue());
				// , arrows: { to: { enabled: true, type: 'arrow'}}
				buf.append(" , arrows: { to: { enabled: true, type: 'arrow'}}");
				buf.append("}\n");
				first = false;
			}
			buf.append("]);\n");

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			try {
				int maxLevel = Integer.MAX_VALUE;
				if (ast.isAST2()) {
					maxLevel = ast.arg2().toIntDefault();
					if (maxLevel < 0) {
						return F.NIL;
					}
				}
				IExpr arg1 = engine.evaluate(ast.arg1());
				List<SimpleImmutableEntry<String, Integer>> vertexList = new ArrayList<SimpleImmutableEntry<String, Integer>>();
				List<SimpleImmutableEntry<Integer, Integer>> edgeList = new ArrayList<SimpleImmutableEntry<Integer, Integer>>();
				StringBuilder jsControl = new StringBuilder();
				if (maxLevel > 0 && arg1.isAST()) {
					IAST tree = (IAST) arg1;
					int[] currentCount = new int[] { 1 };
					treeToGraph(tree, 0, maxLevel, currentCount, vertexList, edgeList);
					vertexToVisjs(jsControl, vertexList);
					edgesToVisjs(jsControl, edgeList);
					return F.JSFormData(jsControl.toString(), "treeform");
				} else {
					vertexList.add(new SimpleImmutableEntry<String, Integer>(arg1.toString(), Integer.valueOf(0)));
					vertexToVisjs(jsControl, vertexList);
					edgesToVisjs(jsControl, edgeList);
					return F.JSFormData(jsControl.toString(), "treeform");
				}

			} catch (Exception rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
				return engine.printMessage("TreeForm: " + rex.getMessage());
			}
		}

		private static void treeToGraph(IAST tree, final int level, final int maxLevel, int[] currentCount,
				List<SimpleImmutableEntry<String, Integer>> vertexList,
				List<SimpleImmutableEntry<Integer, Integer>> edgeList) {
			vertexList.add(new SimpleImmutableEntry<String, Integer>(tree.head().toString(), Integer.valueOf(level)));
			int currentNode = vertexList.size();
			final int nextLevel = level + 1;
			for (int i = 1; i < tree.size(); i++) {
				currentCount[0]++;
				edgeList.add(new SimpleImmutableEntry<Integer, Integer>(currentNode, currentCount[0]));
				IExpr arg = tree.get(i);
				if (nextLevel >= maxLevel || !arg.isAST()) {
					vertexList.add(new SimpleImmutableEntry<String, Integer>(arg.toString(), nextLevel));
				} else {
					treeToGraph((IAST) arg, nextLevel, maxLevel, currentCount, vertexList, edgeList);
				}
			}
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_2;
		}

	}

	public static String toJavaDouble(final IExpr arg1) throws IOException {
		DoubleFormFactory factory = JavaDoubleFormFactory.get(true, false);
		StringBuilder buf = new StringBuilder();
		factory.convert(buf, arg1);
		return buf.toString();
	}

	public static String toJavaScript(final IExpr arg1) {
		DoubleFormFactory factory = new JavaScriptFormFactory(true, false, -1, -1, JavaScriptFormFactory.USE_PURE_JS);
		StringBuilder buf = new StringBuilder();
		factory.convert(buf, arg1);
		return buf.toString();
	}

	public static void initialize() {
		Initializer.init();
	}

	private OutputFunctions() {

	}

}
