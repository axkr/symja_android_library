package org.matheclipse.core.reflection.system;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.CSVExporter;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;
import org.jgrapht.io.GraphMLExporter;
import org.jgrapht.io.IntegerComponentNameProvider;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.ASTDataset;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;

/**
 * Export some data into a string representation
 *
 */
public class ExportString extends AbstractEvaluator {

	public ExportString() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (Config.isFileSystemEnabled(engine)) {
			IExpr arg1 = ast.arg1();
			if (!(ast.arg2() instanceof IStringX)) {
				return F.NIL;
			}
			Extension format = Extension.exportExtension(((IStringX) ast.arg2()).toString());
			StringWriter writer = null;
			try {
				writer = new StringWriter();
				if (arg1 instanceof GraphExpr) {
					graphExport(((GraphExpr<DefaultEdge>) arg1).toData(), writer, format);
					return F.stringx(writer.toString());
				}

				if (format.equals(Extension.CSV)) {
					if (arg1 instanceof ASTDataset) {
						((ASTDataset) arg1).csv(writer);
						return F.stringx(writer.toString());
					}
				} else if (format.equals(Extension.TABLE)) {
					int[] dims = arg1.isMatrix();
					if (dims != null) {
						for (int j = 0; j < dims[0]; j++) {
							IAST rowList = (IAST) arg1.getAt(j + 1);
							for (int i = 1; i <= dims[1]; i++) {
								if (rowList.get(i).isReal()) {
									writer.append(rowList.get(i).toString());
								} else {
									writer.append("\"");
									writer.append(rowList.get(i).toString());
									writer.append("\"");
								}
								if (i < dims[1]) {
									writer.append(" ");
								}
							}
							writer.append("\n");
						}
						return F.stringx(writer.toString());
					} else {
						if (arg1.isList()) {

						}
					}
					// } else if (format.equals(Extension.DAT)) {
					// File file = new File(arg1.toString());
					// com.google.common.io.Files.write(arg2.toString(), file, Charset.defaultCharset());
					// return arg1;
					// } else if (format.equals(Extension.WXF)) {
					// File file = new File(arg1.toString());
					// byte[] bArray = WL.serialize(arg2);
					// com.google.common.io.Files.write(bArray, file);
					// return arg1;
				}

				// } catch (IOException ioe) {
				// return engine.printMessage("ExportString: " + arg1.toString() + " not found!");
			} catch (Exception ex) {
				return engine.printMessage("ExportString: format: " + arg1.toString() + " - " + ex.getMessage());
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return F.NIL;
	}

	private static final ComponentNameProvider<IExpr> nameProvider = v -> String.valueOf(v);

	void graphExport(Graph<IExpr, DefaultEdge> g, Writer writer, Extension format)
			throws ExportException, UnsupportedEncodingException {
		switch (format) {
		case DOT:
			DOTExporter<IExpr, DefaultEdge> dotExporter = new DOTExporter<>(new IntegerComponentNameProvider<>(), null,
					null, null, null);
			dotExporter.putGraphAttribute("overlap", "false");
			dotExporter.putGraphAttribute("splines", "true");

			dotExporter.exportGraph(g, writer);
			return;
		case GRAPHML:
			GraphExporter<IExpr, DefaultEdge> graphMLExporter = new GraphMLExporter<>();
			graphMLExporter.exportGraph(g, writer);
			return;
		default:
		}

		// DEFAULT: return CSV file
		CSVExporter<IExpr, DefaultEdge> exporter = new CSVExporter<IExpr, DefaultEdge>(nameProvider,
				org.jgrapht.io.CSVFormat.EDGE_LIST, ';');
		exporter.exportGraph(g, writer);
	}

	public int[] expectedArgSize() {
		return IOFunctions.ARGS_2_2;
	}

}
