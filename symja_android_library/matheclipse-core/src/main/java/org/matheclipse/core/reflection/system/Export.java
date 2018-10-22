package org.matheclipse.core.reflection.system;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;

import ch.ethz.idsc.tensor.io.Extension;
import ch.ethz.idsc.tensor.io.Filename;
import ch.ethz.idsc.tensor.io.ImageFormat;

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
			Validate.checkRange(ast, 3, 4);

			if (!(ast.arg1() instanceof IStringX)) {
				throw new WrongNumberOfArguments(ast, 1, ast.argSize());
			}
			String format = "Data";
			if (ast.size() == 4) {
				if (!(ast.arg3() instanceof IStringX)) {
					throw new WrongNumberOfArguments(ast, 3, ast.argSize());
				}
				format = ((IStringX) ast.arg3()).toString();
			}
			IStringX arg1 = (IStringX) ast.arg1();
			IExpr arg2 = ast.arg2();
			FileWriter writer = null;
			try {
				writer = new FileWriter(arg1.toString());
				if (format.equals("Table")) {
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
				} else if (format.equals("Data")) {
					File file = new File(arg1.toString());
					com.google.common.io.Files.write(arg2.toString(), file, Charset.defaultCharset());
					return arg1;
				} else if (format.equals("WXF")) {
					File file = new File(arg1.toString());
					byte[] bArray = WL.serialize(arg2);
					com.google.common.io.Files.write(bArray, file);
					return arg1;
				}

			} catch (IOException ioe) {
				engine.printMessage("Export: file " + arg1.toString() + " not found!");
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

	/**
	 * See the documentation of {@link CsvFormat}, {@link ImageFormat}, {@link MatlabExport}, and {@link ObjectFormat}
	 * for information on how tensors are encoded in the respective format.
	 * 
	 * @param file
	 *            destination
	 * @param tensor
	 * @throws IOException
	 */
	public static void of(File file, IAST tensor) throws IOException {
		Filename filename = new Filename(file);
		// if (filename.hasExtension("csv"))
		// Files.write(file.toPath(), (Iterable<String>) CsvFormat.of(tensor)::iterator);
		// else
		Extension extension = filename.extension();
		if (extension.equals(Extension.JPG))
			ImageIO.write(ImageFormat.jpg(tensor), "jpg", file);
		// else if (filename.hasExtension("m"))
		// Files.write(file.toPath(), (Iterable<String>) MatlabExport.of(tensor)::iterator);
		if (extension.equals(Extension.PNG))
			ImageIO.write(ImageFormat.of(tensor), "png", file);
		// else if (filename.hasExtension("tensor"))
		// object(file, tensor);
		else
			throw new RuntimeException(file.toString());
	}
}
