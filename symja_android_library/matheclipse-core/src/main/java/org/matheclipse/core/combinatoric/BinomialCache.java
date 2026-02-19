package org.matheclipse.core.combinatoric;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IInteger;

/**
 * A hybrid cache that combines on-the-fly Binomial calculation with cached storage.
 * <ul>
 * <li>For small 'm' (below a threshold), it calculates the value on demand.</li>
 * <li>For large 'm', it retrieves the value from a pre-computed, symmetric, and interned
 * cache.</li>
 * </ul>
 */
public final class BinomialCache {

  public static final int MAX_N = 250;

  /**
   * Coefficients with 'm' below this value will be calculated on-the-fly.
   */
  private static final int CALCULATION_THRESHOLD = 1;

  private static IInteger[][] COEFFICIENTS;

  static {
    // if (true) {
    // readBinomialCoefficients();
    // } else {
    writeBinomialCoefficients();
    // }
  }

  /** Load the pre-computed binomial coefficients from a file. */
  private static void readBinomialCoefficients() {
    F.initSymja();
    String filename = "binomials.dat";
    IInteger[][] loadedCoefficients = null;

    // Use try-with-resources for safe file handling
    try (InputStream is = BinomialCache.class.getResourceAsStream("/bin/" + filename);
        ObjectInputStream ois = new ObjectInputStream(is)) {

      // Read the object and cast it back to the correct type
      loadedCoefficients = (IInteger[][]) ois.readObject();

    } catch (IOException | ClassNotFoundException e) {
      System.err.println("FATAL: Could not load binomial coefficients file: " + filename);
      e.printStackTrace();
    }

    COEFFICIENTS = loadedCoefficients;
  }

  /** Pre-compute and write the binomial coefficients to a file. */
  private static void writeBinomialCoefficients() {
    Map<IInteger, IInteger> INTERN_POOL = new HashMap<>();
    COEFFICIENTS = new IInteger[MAX_N + 1][];
    // Pre-intern the number 1
    INTERN_POOL.putIfAbsent(F.C1, F.C1);


    for (int n = 0; n <= MAX_N; n++) {
      int startM = CALCULATION_THRESHOLD;
      int endM = n / 2;

      if (startM > endM) {
        // This row is too short to have any cached values.
        COEFFICIENTS[n] = new IInteger[0];
        continue;
      }

      int rowLength = endM - startM + 1;
      COEFFICIENTS[n] = new IInteger[rowLength];

      for (int m = startM; m <= endM; m++) {
        // To calculate C(n, m), we need C(n-1, m-1) and C(n-1, m).
        // Each of these might be cached or need on-the-fly calculation.
        IInteger term1 = getValueFromPreviousRow(n - 1, m - 1);
        IInteger term2 = getValueFromPreviousRow(n - 1, m);

        IInteger result = term1.add(term2);

        // The array index is offset by the threshold.
        int storageIndex = m - startM;

        IInteger bigInteger = INTERN_POOL.get(result);
        if (bigInteger == null) {
          INTERN_POOL.put(result, result);
          COEFFICIENTS[n][storageIndex] = result;
        } else {
          COEFFICIENTS[n][storageIndex] = bigInteger;
        }
      }
    }
    // createCoefficientsFile();
  }

  /**
   * The internal on-the-fly calculator for small 'm'.
   */
  private static IInteger calculateInternal(int n, int m) {
    if (m < 0 || m > n) {
      return F.C0;
    }
    if (m == 0 || m == n) {
      return F.C1;
    }
    if (m > n / 2) {
      m = n - m;
    }
    IInteger result = F.C1;
    for (int i = 1; i <= m; i++) {
      result = result.multiply(F.ZZ(n - i + 1)).div(F.ZZ(i));
    }
    return result;
  }

  /** Writes the COEFFICIENTS array to a file in the root of this module for future use. */
  private static void createCoefficientsFile() {
    // The output file
    String filename = "binomials.dat";

    try (FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {

      // This single line serializes and writes the entire array object
      oos.writeObject(COEFFICIENTS);

      System.out.println("Successfully wrote coefficients to " + filename);

    } catch (IOException e) {
      System.err.println("Error writing to file: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Retrieves the binomial coefficient, either from the cache or by calculating.
   */
  public static IInteger binomial(int n, int m) {
    if (n < 0 || m < 0 || n > MAX_N) {
      throw new IllegalArgumentException("Invalid input for n or m.");
    }
    if (m > n) {
      return F.C0;
    }
    if (m == 0 || m == n) {
      return F.C1;
    }

    int effectiveM = (m > n / 2) ? n - m : m;

    if (effectiveM < CALCULATION_THRESHOLD) {
      // --- On-the-Fly Calculation ---
      return calculateInternal(n, effectiveM);
    } else {
      // --- Cached Lookup ---
      int storageIndex = effectiveM - CALCULATION_THRESHOLD;
      return COEFFICIENTS[n][storageIndex];
    }
  }

  private static IInteger getValueFromPreviousRow(int n, int m) {
    int effectiveM = (m > n / 2) ? n - m : m;

    if (effectiveM < CALCULATION_THRESHOLD) {
      // This value is not stored, so calculate it.
      return calculateInternal(n, effectiveM);
    } else {
      // This value is in our cache. Find it at the offset index.
      int storageIndex = effectiveM - CALCULATION_THRESHOLD;
      return COEFFICIENTS[n][storageIndex];
    }
  }

  public static void main(String[] args) {
    System.out.println("--- Hybrid Cache Demonstration ---");
    int threshold = CALCULATION_THRESHOLD;

    System.out.println("\nRequesting C(30, 3) where m < threshold (" + threshold + ")");
    IInteger val1 = binomial(30, 1);
    System.out.println("Result: " + val1 + " (Calculated on-the-fly)");

    System.out.println("\nRequesting C(30, 10) where m >= threshold (" + threshold + ")");
    IInteger val2 = binomial(30, 10);
    System.out.println("Result: " + val2 + " (Retrieved from cache)");
  }

  private BinomialCache() {}
}
