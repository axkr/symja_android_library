// code by jph
package org.matheclipse.io.test;

import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.io.tensor.io.ImageFormat;
import junit.framework.TestCase;

public class ImportTest extends TestCase {
  // public void testCsv() throws Exception {
  // File file = new File(getClass().getResource("/io/libreoffice_calc.csv").getFile());
  // IAST table = Import.of(file);
  // assertEquals(Dimensions.of(table), Arrays.asList(4, 2));
  // }

  public void testPng() throws Exception {
    File file = new File(getClass().getResource("/io/rgba15x33.png").getFile());
    // IAST tensor = Import.of(file);
    IAST tensor = ImageFormat.from(ImageIO.read(file));
    System.out.println(tensor.toString());
    assertEquals(LinearAlgebra.dimensions(tensor), Arrays.asList(33, 15, 4));
  }

  public void testJpg() throws Exception {
    File file = new File(getClass().getResource("/io/rgb15x33.jpg").getFile());
    // IAST tensor = Import.of(file);
    IAST tensor = ImageFormat.from(ImageIO.read(file));
    assertEquals(LinearAlgebra.dimensions(tensor), Arrays.asList(33, 15, 4));
    IExpr part = tensor.getPart(22, 4);
    // verified with gimp
    System.out.println(part.toString());
    assertEquals(F.List(F.ZZ(180), F.ZZ(46), F.ZZ(47), F.ZZ(255)), part);
  }

  // public void testObject() throws ClassNotFoundException, DataFormatException, IOException {
  // // Export.object(UserHome.file("string.object"), "tensorlib.importtest");
  // File file = new File(getClass().getResource("/io/string.object").getFile());
  // String string = Import.object(file);
  // assertEquals(string, "tensorlib.importtest");
  // }

  // public void testUnknownFail() {
  // File file = new File(getClass().getResource("/io/extension.unknown").getFile());
  // try {
  // Import.of(file);
  // assertTrue(false);
  // } catch (Exception exception) {
  // // ---
  // }
  // }
}
