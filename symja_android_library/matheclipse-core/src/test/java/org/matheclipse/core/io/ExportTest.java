package org.matheclipse.core.io;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.Export;
import org.matheclipse.core.reflection.system.Import;

import junit.framework.TestCase;

public class ExportTest extends TestCase {
	// private static void static_check(File file) throws ClassNotFoundException, DataFormatException, IOException {
	// assertFalse(file.isFile());
	// IAST tensor = Tensors.fromString("{{2,3.123+3*I[V]},{34.1231`32,556,3/456,-323/2}}");
	// Export.of(file, tensor);
	// assertEquals(tensor, Import.of(file));
	// file.delete();
	// }

	// public void testCsvAndTensor() throws IOException, ClassNotFoundException, DataFormatException {
	// static_check(UserHome.file("tensorLib_ExportTest.csv"));
	// static_check(UserHome.file("tensorLib_ExportTest.tensor"));
	// }

	public void testPngColor() throws ClassNotFoundException, DataFormatException, IOException {
		File file = UserHome.file("tensorLib_ExportTest.png");
		// assertFalse(file.isFile());
		EvalEngine engine = EvalEngine.get();
		IAST image = (IAST) engine.evaluate(
				F.RandomVariate(F.DiscreteUniformDistribution(F.List(F.C0, F.ZZ(256))), F.List(F.C7, F.ZZ(11), F.C4)));
		Export.of(file, image);
		assertEquals(image, Import.of(file, engine));
		file.delete();
	}

	public void testPngGray() throws ClassNotFoundException, DataFormatException, IOException {
		File file = UserHome.file("tensorLib_ExportTest.png");
		// assertFalse(file.isFile());
		EvalEngine engine = EvalEngine.get();
		IAST image = (IAST) engine.evaluate(
				F.RandomVariate(F.DiscreteUniformDistribution(F.List(F.C0, F.ZZ(256))), F.List(F.C7, F.ZZ(11))));
		Export.of(file, image);
		System.out.println(image.toString());
		IExpr result = Import.of(file, engine);
		System.out.println(result.toString());
		System.out.println(image.equals(result));
		assertEquals(image, result);
		file.delete();
	}

	// public void testJpgColor() throws ClassNotFoundException, DataFormatException, IOException {
	// File file = UserHome.file("tensorLib_ExportTest.jpg");
	// assertFalse(file.isFile());
	// IAST image = MeanFilter.of(RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11, 4), 2);
	// image.set(Array.of(f -> RealScalar.of(255), 7, 11), Tensor.ALL, Tensor.ALL, 3);
	// Export.of(file, image);
	// EvalEngine engine = EvalEngine.get();
	// IExpr diff = image.subtract(Import.of(file, engine));
	// IExpr total = diff.map(Abs.FUNCTION).flatten(-1).reduce(Tensor::add).get().Get();
	// IExpr pixel = total.divide(RealScalar.of(4 * 77.0));
	// assertTrue(engine.evalTrue(F.Less(pixel, F.C6)));
	// file.delete();
	// }

	// public void testJpgGray() throws ClassNotFoundException, DataFormatException, IOException {
	// File file = UserHome.file("tensorLib_ExportTest.jpg");
	// assertFalse(file.isFile());
	// IAST image = MeanFilter.of(RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11), 4);
	// Export.of(file, image);
	// EvalEngine engine = EvalEngine.get();
	// IExpr diff = image.subtract(Import.of(file, engine));
	// IExpr total = diff.map(Abs.FUNCTION).flatten(-1).reduce(Tensor::add).get().Get();
	// IExpr pixel = total.divide(RealScalar.of(77.0));
	// assertTrue(engine.evalTrue(F.Less(pixel, F.C5)));
	// file.delete();
	// }

	// public void testMatlabM() throws ClassNotFoundException, DataFormatException, IOException {
	// File file = UserHome.file("tensorLib_ExportTest.m");
	// assertFalse(file.isFile());
	// IAST tensor = Tensors.fromString("{{2,3.123+3*I,34.1231},{556,3/456,-323/2}}");
	// Export.of(file, tensor);
	// file.delete();
	// }

	// public void testFail() {
	// try {
	// Export.of(new File("ajshgd.ueyghasfd"), Tensors.empty());
	// assertTrue(false);
	// } catch (Exception exception) {
	// // ---
	// }
	// }
}
