package org.matheclipse.core.polynomials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.polynomials.longexponent.ExpVectorLong;
import org.matheclipse.core.polynomials.symbolicexponent.ExpVectorSymbolic;

/** Hash codes of exponent vectors: equal vectors hash equal and different vectors spread. */
public class ExpVectorHashCodeTest {

  /** All exponent vectors of length 3 with entries 0..4. */
  private static long[][] vectors() {
    long[][] result = new long[125][];
    int k = 0;
    for (long a = 0; a < 5; a++) {
      for (long b = 0; b < 5; b++) {
        for (long c = 0; c < 5; c++) {
          result[k++] = new long[] {a, b, c};
        }
      }
    }
    return result;
  }

  @Test
  public void testExpVectorLong() {
    Set<Integer> hashes = new HashSet<>();
    for (long[] v : vectors()) {
      ExpVectorLong e1 = new ExpVectorLong(v);
      ExpVectorLong e2 = new ExpVectorLong(v.clone());
      assertEquals(e1, e2);
      assertEquals(e1.hashCode(), e2.hashCode());
      assertEquals(e1.hashCode(), e1.hashCode());
      hashes.add(e1.hashCode());
    }
    assertTrue(hashes.size() > 100, "distinct hash codes: " + hashes.size());
  }

  @Test
  public void testExpVectorSymbolic() {
    Set<Integer> hashes = new HashSet<>();
    for (long[] v : vectors()) {
      IExpr[] u = new IExpr[v.length];
      IExpr[] w = new IExpr[v.length];
      for (int i = 0; i < v.length; i++) {
        u[i] = F.ZZ(v[i]);
        w[i] = F.ZZ(v[i]);
      }
      ExpVectorSymbolic e1 = new ExpVectorSymbolic(u);
      ExpVectorSymbolic e2 = new ExpVectorSymbolic(w);
      assertEquals(e1, e2);
      assertEquals(e1.hashCode(), e2.hashCode());
      hashes.add(e1.hashCode());
    }
    assertTrue(hashes.size() > 100, "distinct hash codes: " + hashes.size());

    ExpVectorSymbolic s1 = new ExpVectorSymbolic(new IExpr[] {F.Times(F.C2, F.n), F.C1});
    ExpVectorSymbolic s2 = new ExpVectorSymbolic(new IExpr[] {F.Times(F.C2, F.n), F.C1});
    assertEquals(s1.hashCode(), s2.hashCode());
  }

  /** A vector read back from a stream hashes like an equal vector built in memory. */
  @Test
  public void testExpVectorLongSerialized() throws IOException, ClassNotFoundException {
    ExpVectorLong e1 = new ExpVectorLong(new long[] {3, 1, 4});
    int hash = e1.hashCode();
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
      out.writeObject(e1);
    }
    try (ObjectInputStream in =
        new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      ExpVectorLong e2 = (ExpVectorLong) in.readObject();
      assertEquals(e1, e2);
      assertEquals(hash, e2.hashCode());
    }
  }
}
