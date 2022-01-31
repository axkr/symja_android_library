package org.matheclipse.io.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.matheclipse.core.expression.F;
import org.matheclipse.io.expression.ASTDataset;
import junit.framework.TestCase;
import tech.tablesaw.api.Table;

public class SerializableDataSetTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    // wait for initializing of Integrate() rules:
    F.await();
  }

  public void testDataset() {
    Table table = Table.read().csv("Products,Sales,Market_Share\n" + //
        "a,5500,3\n" + //
        "b,12200,4\n" + //
        "c,60000,33\n", "");

    ASTDataset ds = ASTDataset.newTablesawTable(table);
    equalsStringCopy(ds);
  }

  private void equalsCopy(Object original) {
    try {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(original);
      byte[] bArray = baos.toByteArray();
      baos.close();
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(bArray);
      ObjectInputStream ois = new ObjectInputStream(bais);
      Object copy = ois.readObject();
      bais.close();
      ois.close();
      assertEquals(original, copy);

      // if (!original.toString().equals(copy.toString())) {
      // System.out.println(copy.toString());
      // }
      // assertEquals(original.toString(), copy.toString());
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
      assertEquals("", cnfe.toString());
    } catch (IOException ioe) {
      ioe.printStackTrace();
      assertEquals("", ioe.toString());
    }
  }

  private void equalsStringCopy(Object original) {
    try {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);

      long start0 = System.currentTimeMillis();
      oos.writeObject(original);
      byte[] bArray = baos.toByteArray();
      baos.close();
      oos.close();

      long start1 = System.currentTimeMillis();
      ByteArrayInputStream bais = new ByteArrayInputStream(bArray);
      ObjectInputStream ois = new ObjectInputStream(bais);
      Object copy = ois.readObject();
      bais.close();
      ois.close();
      long end = System.currentTimeMillis();
      long temp = start1 - start0;
      System.out.println(Long.valueOf(temp).toString());
      temp = end - start1;
      System.out.println(Long.valueOf(temp).toString());
      assertEquals(original.toString(), copy.toString());

    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
      assertEquals("", cnfe.toString());
    } catch (IOException ioe) {
      ioe.printStackTrace();
      assertEquals("", ioe.toString());
    }
  }
}
