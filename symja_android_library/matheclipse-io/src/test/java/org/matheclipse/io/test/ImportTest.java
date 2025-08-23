// code by jph
package org.matheclipse.io.test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.io.Extension;
import org.matheclipse.io.builtin.Import;
import org.matheclipse.io.tensor.io.ImageFormat;
import it.unimi.dsi.fastutil.ints.IntArrayList;

@RunWith(JUnit4.class)
public class ImportTest {
  // @Test
  // public void testCsv() throws Exception {
  // File file = new File(ImportTest.class.getResource("/io/libreoffice_calc.csv").getFile());
  // IAST table = Import.of(file);
  // assertEquals(Dimensions.of(table), Arrays.asList(4, 2));
  // }

  @Test
  public void testPng() throws Exception {
    File file = new File(ImportTest.class.getResource("/io/rgba15x33.png").getFile());
    // IAST tensor = Import.of(file);
    IAST tensor = ImageFormat.from(ImageIO.read(file));
    System.out.println(tensor.toString());
    assertEquals(LinearAlgebraUtil.dimensions(tensor), Arrays.asList(33, 15, 4));
  }

  @Test
  public void testJpg() throws Exception {
    File file = new File(ImportTest.class.getResource("/io/rgb15x33.jpg").getFile());
    // IAST tensor = Import.of(file);
    IAST tensor = ImageFormat.from(ImageIO.read(file));
    assertEquals(LinearAlgebraUtil.dimensions(tensor), Arrays.asList(33, 15, 4));
    IExpr part = tensor.getPart(22, 4);
    // verified with gimp
    System.out.println(part.toString());
    assertEquals(F.List(F.ZZ(180), F.ZZ(46), F.ZZ(47), F.ZZ(255)), part);
  }

  @Test
  public void testRawJSON() throws Exception {
    File file = new File(ImportTest.class.getResource("/io/java_wikipedia.json").getFile());

    IExpr importResult =
        Import.importFromPath(F.stringx("dummy"), Extension.RAWJSON, file, EvalEngine.get());
    assertEquals(importResult.toString(), //
        "<|continue-><|plcontinue->15881|0|Android_application,continue->|||>,query-><|pages-><|15881-><|pageid->\n" //
            + "15881,ns->0,title->Java (programming language),links->{<|ns->0,title->\"Hello, world!\" program|>,<|ns->\n" //
            + "0,title->ALGOL|>,<|ns->0,title->APL (programming language)|>,<|ns->0,title->ARM architecture family|>,<|ns->\n" //
            + "0,title->Abstract Window Toolkit|>,<|ns->0,title->Acquisition of Sun Microsystems by Oracle Corporation|>,<|ns->\n" //
            + "0,title->Ada (programming language)|>,<|ns->0,title->Android (operating system)|>,<|ns->\n" //
            + "0,title->Android Runtime|>,<|ns->0,title->Android SDK|>}|>|>|>|>");
  }

  // @Test
  // public void testObject() throws ClassNotFoundException, DataFormatException, IOException {
  // // Export.object(UserHome.file("string.object"), "tensorlib.importtest");
  // File file = new File(ImportTest.class.getResource("/io/string.object").getFile());
  // String string = Import.object(file);
  // assertEquals(string, "tensorlib.importtest");
  // }

  // @Test
  // public void testUnknownFail() {
  // File file = new File(ImportTest.class.getResource("/io/extension.unknown").getFile());
  // try {
  // Import.of(file);
  // assertTrue(false);
  // } catch (Exception exception) {
  // // ---
  // }
  // }

  @Test
  public void testDenseRowColSparseMat() throws Exception {
    File file = new File(ImportTest.class.getResource("/io/mat/denseRowColSparse.mat").getFile());
    IExpr importResult =
        Import.importFromPath(F.stringx("dummy"), Extension.MAT, file, EvalEngine.get());
    if (importResult instanceof IAST && importResult.isPresent()) {
      System.out.println(importResult);
      IntArrayList dimensions = LinearAlgebraUtil.dimensions(((IAST) importResult));
      assertEquals(dimensions.toString(), "[8, 9]");
      return;
    }

    fail();
  }

  @Test
  public void testMultiDimMatrixMat() throws Exception {
    File file = new File(ImportTest.class.getResource("/io/mat/multiDimMatrix.mat").getFile());
    IExpr importResult =
        Import.importFromPath(F.stringx("dummy"), Extension.MAT, file, EvalEngine.get());
    if (importResult instanceof IAST && importResult.isPresent()) {
      System.out.println(importResult);
      IntArrayList dimensions = LinearAlgebraUtil.dimensions(((IAST) importResult));
      assertEquals(dimensions.toString(), "[2, 3, 4, 5, 6]");
      return;
    }

    fail();
  }

  @Test
  public void testLogicalMat() throws Exception {
    File file = new File(ImportTest.class.getResource("/io/mat/logical.mat").getFile());
    IExpr importResult =
        Import.importFromPath(F.stringx("dummy"), Extension.MAT, file, EvalEngine.get());
    if (importResult instanceof IAST && importResult.isPresent()) {
      System.out.println(importResult);
      IntArrayList dimensions = LinearAlgebraUtil.dimensions(((IAST) importResult));
      assertEquals(dimensions.toString(), "[1, 2]");
      return;
    }

    fail();
  }
}
