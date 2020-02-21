package org.matheclipse.core.reflection.system;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;

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
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.io.Extension;

import com.univocity.parsers.csv.CsvFormat;

import ch.ethz.idsc.tensor.io.ImageFormat;
import tech.tablesaw.api.Table;

/**
 * Export some data from file system.
 *
 */
public class Export extends AbstractEvaluator {

	public Export() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (Config.isFileSystemEnabled(engine)) {
			if (!(ast.arg1() instanceof IStringX)) {
				return F.NIL;
			}
			IStringX arg1 = (IStringX) ast.arg1();
			Extension format = Extension.exportFilename(arg1.toString());
			if (ast.size() == 4) {
				if (!(ast.arg3() instanceof IStringX)) {
					return F.NIL;
				}
				// format = ((IStringX) ast.arg3()).toString();
				format = Extension.exportExtension(((IStringX) ast.arg3()).toString());
			}

			IExpr arg2 = ast.arg2();
			FileWriter writer = null;
			try {
				writer = new FileWriter(arg1.toString());
				if (arg2 instanceof GraphExpr) {
					graphExport(((GraphExpr<DefaultEdge>) arg2).toData(), writer, format);
					return arg1;
				}

				if (format.equals(Extension.CSV)) {
					if (arg2 instanceof ASTDataset) {
						((ASTDataset) arg2).csv(writer);
						return arg1;
					}
				} else if (format.equals(Extension.TABLE)) {
					int[] dims = arg2.isMatrix();
					if (dims != null) {
						for (int j = 0; j < dims[0]; j++) {
							IAST rowList = (IAST) arg2.getAt(j + 1);
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
						return arg1;
					} else {
						if (arg2.isList()) {

						}
					}
				} else if (format.equals(Extension.DAT)) {
					File file = new File(arg1.toString());
					com.google.common.io.Files.write(arg2.toString(), file, Charset.defaultCharset());
					return arg1;
				} else if (format.equals(Extension.WXF)) {
					File file = new File(arg1.toString());
					byte[] bArray = WL.serialize(arg2);
					com.google.common.io.Files.write(bArray, file);
					return arg1;
				}

			} catch (IOException ioe) {
				return engine.printMessage("Export: file " + arg1.toString() + " not found!");
			} catch (Exception ex) {
				return engine.printMessage("Export: file " + arg1.toString() + " - " + ex.getMessage());
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
		return IOFunctions.ARGS_2_3;
	}

	/**
	 * See the documentation of {@link CsvFormat}, {@link ImageFormat}, {@link MatlabExport}, and {@link ObjectFormat}
	 * for information on how tensors are encoded in the respective format.
	 * 
	 * @param file
	 *            destination
	 * @param tensor
	 * @throws IOException
	 */
	// public static void of(File file, IAST tensor) throws IOException {
	// String filename = file.getName();
	// Extension extension = Extension.exportFilename(filename);
	// if (extension.equals(Extension.JPG))
	// ImageIO.write(ImageFormat.jpg(tensor), "jpg", file);
	// // else if (filename.hasExtension("m"))
	// // Files.write(file.toPath(), (Iterable<String>) MatlabExport.of(tensor)::iterator);
	// if (extension.equals(Extension.PNG))
	// ImageIO.write(ImageFormat.of(tensor), "png", file);
	// // else if (filename.hasExtension("tensor"))
	// // object(file, tensor);
	// else
	// throw new RuntimeException(file.toString());
	// }
}
