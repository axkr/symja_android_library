package org.matheclipse.io.others;

import java.math.RoundingMode;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

public class ApfloatOutput {
  public static void main(String[] args) {
    Apfloat value = new Apfloat("6.7", 25);
    value = ApfloatMath.pow(value, -4L);
    System.out.println(value.toString());
    // Apfloat newValue = ApfloatMath.round(value, 20, RoundingMode.HALF_EVEN);
    // System.out.println(newValue.toString());

    Apfloat value2 = new Apfloat("6.7", 24);
    value2 = ApfloatMath.pow(value2, -4L);
    System.out.println(value2.toString());
    // Apfloat newValue2 = ApfloatMath.round(value2, 20, RoundingMode.HALF_EVEN);
    // System.out.println(newValue2.toString());

    Apfloat valueD = new Apfloat(Math.pow(6.7, -4));
    Apfloat newValueD = ApfloatMath.round(valueD, 6, RoundingMode.HALF_EVEN);
    System.out.println(newValueD.toString());

    Apfloat valueD2 = new Apfloat(Math.pow(6.7, -4));
    Apfloat newValueD2 = ApfloatMath.round(valueD2, 7, RoundingMode.HALF_EVEN);
    System.out.println(newValueD2.toString());
  }
}
