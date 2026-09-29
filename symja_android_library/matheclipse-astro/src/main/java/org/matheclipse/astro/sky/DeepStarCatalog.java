/*
 * DeepStarCatalog.java  -  Star database
 * Copyright (C) 2011-2026 Brian Simpson
 * This file is part of Night Vision.
 *
 * Night Vision is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Night Vision is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Night Vision.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Modified 2026 for Symja: the star.db reader of com.nvastro.nvj.StarDB (internalDB, Night Vision
 * 5.5) rewritten for package org.matheclipse.astro.sky. It reads only the magnitude prefix a query
 * needs, keeps float unit vectors instead of double coordinates, and publishes immutable snapshots
 * instead of filling static arrays; the drawing code was not ported.
 */

package org.matheclipse.astro.sky;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import org.matheclipse.astro.meeus.SpectralColor;

/**
 * The ASCC-2.5 star catalogue of Night Vision, about 1.06 million stars down to magnitude 11.1.
 *
 * <p>
 * The file (see <code>star-data/README.md</code>) is sorted by magnitude, brightest first. That
 * makes a magnitude limit a prefix of the file, so only as much of it is read as the faintest query
 * so far has needed: a chart to magnitude 9 reads 135,565 stars, 3 MB of the 25 MB. A classpath
 * resource inside a jar cannot be memory mapped, so a deeper query re-opens the stream and skips
 * the part already loaded.
 *
 * <p>
 * The loaded stars are published as an immutable snapshot, so queries never lock and any number of
 * sessions can share one catalogue.
 *
 * @author Brian Simpson
 */
public final class DeepStarCatalog {

  /** The classpath resource. */
  public static final String RESOURCE = "/star-data/star.db";

  /** Number of bytes in DB per star */
  public static final int STAR_BYTES = 24;

  /** One more than the faintest magnitude in the file, times 100: "load everything". */
  private static final int ALL = Short.MAX_VALUE;

  private static final DeepStarCatalog INSTANCE = new DeepStarCatalog();

  /** The loaded prefix of the file. */
  private static final class Snapshot {
    final int count;
    /** Unit vectors in the ICRS (J2000). */
    final float[] x, y, z;
    final short[] mag100;
    /** Two bytes of spectral type per star, NUL when unknown. */
    final byte[] spect;
    /** Every star with magnitude * 100 up to this is loaded. */
    final int limit100;
    /** Whether the whole file is loaded. */
    final boolean complete;

    Snapshot(int count, float[] x, float[] y, float[] z, short[] mag100, byte[] spect,
        int limit100, boolean complete) {
      this.count = count;
      this.x = x;
      this.y = y;
      this.z = z;
      this.mag100 = mag100;
      this.spect = spect;
      this.limit100 = limit100;
      this.complete = complete;
    }
  }

  private volatile Snapshot snapshot =
      new Snapshot(0, new float[0], new float[0], new float[0], new short[0], new byte[0],
          Integer.MIN_VALUE, false);

  private DeepStarCatalog() {}

  public static DeepStarCatalog get() {
    return INSTANCE;
  }

  /** Receives the stars of a query. */
  @FunctionalInterface
  public interface Visitor {
    /**
     * @param index the position of the star in the catalogue, for {@link #spectralType(int)}
     * @param rightAscension J2000 right ascension in degrees, 0..360
     * @param declination J2000 declination in degrees
     * @param magnitude visual magnitude
     */
    void accept(int index, double rightAscension, double declination, double magnitude);
  }

  /** Whether the resource is on the classpath. */
  public static boolean isAvailable() {
    return DeepStarCatalog.class.getResource(RESOURCE) != null;
  }

  /**
   * The number of stars in the file down to {@code limitMagnitude}.
   */
  public int count(double limitMagnitude) {
    Snapshot s = ensureLoaded(limitMagnitude);
    return prefixLength(s, limit100(limitMagnitude));
  }

  /** The number of stars in the whole file. */
  public int size() {
    return ensureLoaded(ALL).count;
  }

  /**
   * Visit the stars within {@code radius} degrees of a direction, brightest first.
   *
   * @param maxCount stop after this many stars
   * @return the effective limiting magnitude: {@code limitMagnitude}, or the magnitude of the last
   *         star visited when {@code maxCount} cut the query short
   */
  public double forEachInCap(double rightAscension, double declination, double radius,
      double limitMagnitude, int maxCount, Visitor visitor) {
    Snapshot s = ensureLoaded(limitMagnitude);
    int end = prefixLength(s, limit100(limitMagnitude));
    double ra0 = Math.toRadians(rightAscension);
    double dec0 = Math.toRadians(declination);
    double cx = Math.cos(dec0) * Math.cos(ra0);
    double cy = Math.cos(dec0) * Math.sin(ra0);
    double cz = Math.sin(dec0);
    double minDot = radius >= 180.0 ? -2.0 : Math.cos(Math.toRadians(radius));
    int visited = 0;
    for (int i = 0; i < end; i++) {
      if (cx * s.x[i] + cy * s.y[i] + cz * s.z[i] < minDot) {
        continue;
      }
      if (visited == maxCount) {
        // the next star would be one too many: everything brighter than it is shown
        return s.mag100[i] / 100.0;
      }
      visited++;
      double ra = Math.toDegrees(Math.atan2(s.y[i], s.x[i]));
      visitor.accept(i, ra < 0.0 ? ra + 360.0 : ra, Math.toDegrees(Math.asin(s.z[i])),
          s.mag100[i] / 100.0);
    }
    return limitMagnitude;
  }

  /**
   * The index of the brightest star within {@code radius} degrees of a direction whose magnitude is
   * within {@code magnitudeTolerance} of {@code magnitude}, or <code>-1</code>.
   */
  public int match(double rightAscension, double declination, double radius, double magnitude,
      double magnitudeTolerance) {
    int[] found = {-1};
    double[] best = {Double.MAX_VALUE};
    forEachInCap(rightAscension, declination, radius, magnitude + magnitudeTolerance,
        Integer.MAX_VALUE, (index, ra, dec, mag) -> {
          double difference = Math.abs(mag - magnitude);
          if (difference <= magnitudeTolerance && difference < best[0]) {
            best[0] = difference;
            found[0] = index;
          }
        });
    return found[0];
  }

  /**
   * The spectral type of the star at {@code index}, such as <code>"A1"</code>, or the empty string
   * when the catalogue has none.
   */
  public String spectralType(int index) {
    Snapshot s = snapshot;
    StringBuilder buf = new StringBuilder(2);
    for (int k = 0; k < 2; k++) {
      byte b = s.spect[2 * index + k];
      if (b > ' ') {
        buf.append((char) b);
      }
    }
    return buf.toString();
  }

  /** The color index of {@link SpectralColor} for the star at {@code index}. */
  public int colorIndex(int index) {
    byte b = snapshot.spect[2 * index];
    return b > ' ' ? SpectralColor.index((char) b) : SpectralColor.UNSPECIFIED;
  }

  private static int limit100(double magnitude) {
    if (magnitude * 100.0 >= ALL) {
      return ALL;
    }
    return (int) Math.floor(magnitude * 100.0 + 1.0e-9);
  }

  /** The number of loaded stars with magnitude * 100 at most {@code limit100}. */
  private static int prefixLength(Snapshot s, int limit100) {
    if (limit100 >= s.limit100) {
      return s.count;
    }
    // the magnitudes are sorted, so the prefix ends at the first star fainter than the limit
    int low = 0;
    int high = s.count;
    while (low < high) {
      int mid = (low + high) >>> 1;
      if (s.mag100[mid] <= limit100) {
        low = mid + 1;
      } else {
        high = mid;
      }
    }
    return low;
  }

  private Snapshot ensureLoaded(double limitMagnitude) {
    int limit = limit100(limitMagnitude);
    Snapshot s = snapshot;
    if (s.complete || s.limit100 >= limit) {
      return s;
    }
    synchronized (this) {
      s = snapshot;
      if (s.complete || s.limit100 >= limit) {
        return s;
      }
      snapshot = s = extend(s, limit);
      return s;
    }
  }

  /** Read the stars after the loaded prefix down to {@code limit100}. */
  /**
   * The stream the snapshots are read from, left open between extensions so that a deeper limit
   * reads on from where the last one stopped - re-opening a resource inside a jar and skipping the
   * prefix would inflate it all again. Guarded by the lock on this catalogue.
   */
  private DataInputStream stream;

  /** The record read past the last limit, which the next extension starts with. */
  private byte[] pending;

  private Snapshot extend(Snapshot s, int limit100) {
    int count = s.count;
    int capacity = Math.max(1024, s.x.length);
    float[] x = Arrays.copyOf(s.x, capacity);
    float[] y = Arrays.copyOf(s.y, capacity);
    float[] z = Arrays.copyOf(s.z, capacity);
    short[] mag100 = Arrays.copyOf(s.mag100, capacity);
    byte[] spect = Arrays.copyOf(s.spect, 2 * capacity);
    boolean complete = false;
    int loadedLimit = limit100;
    try {
      if (stream == null) {
        InputStream resource = DeepStarCatalog.class.getResourceAsStream(RESOURCE);
        if (resource == null) {
          throw new IllegalStateException("resource not found: " + RESOURCE);
        }
        stream = new DataInputStream(new BufferedInputStream(resource, 1 << 16));
        // only after a read error: the stars already loaded are passed over once
        stream.skipNBytes((long) count * STAR_BYTES);
      }
      while (true) {
        byte[] record = pending;
        pending = null;
        if (record == null) {
          record = new byte[STAR_BYTES];
          try {
            stream.readFully(record);
          } catch (EOFException eof) {
            complete = true;
            stream.close();
            stream = null;
            break;
          }
        }
        // The order is important (must match DB)
        long raBits = 0;
        long decBits = 0;
        for (int k = 0; k < 8; k++) {
          raBits = (raBits << 8) | (record[k] & 0xff);
          decBits = (decBits << 8) | (record[8 + k] & 0xff);
        }
        short mag = (short) (((record[16] & 0xff) << 8) | (record[17] & 0xff));
        if (mag > limit100) {
          // the first star which is too faint; the next extension starts with it
          pending = record;
          break;
        }
        if (count == x.length) {
          int grown = x.length * 2;
          x = Arrays.copyOf(x, grown);
          y = Arrays.copyOf(y, grown);
          z = Arrays.copyOf(z, grown);
          mag100 = Arrays.copyOf(mag100, grown);
          spect = Arrays.copyOf(spect, 2 * grown);
        }
        double ra = Double.longBitsToDouble(raBits);
        double dec = Double.longBitsToDouble(decBits);
        double cosde = Math.cos(dec);
        x[count] = (float) (cosde * Math.cos(ra));
        y[count] = (float) (cosde * Math.sin(ra));
        z[count] = (float) Math.sin(dec);
        mag100[count] = mag;
        spect[2 * count] = record[18];
        spect[2 * count + 1] = record[19];
        count++;
      }
    } catch (IOException ioe) {
      stream = null;
      pending = null;
      throw new UncheckedIOException(ioe);
    }
    if (complete) {
      loadedLimit = ALL;
    }
    return new Snapshot(count, x, y, z, mag100, spect, loadedLimit, complete);
  }
}
