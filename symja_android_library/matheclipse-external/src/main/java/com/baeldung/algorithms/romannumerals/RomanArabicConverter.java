package com.baeldung.algorithms.romannumerals;

import java.util.List;
import java.util.Locale;

/**
 * @see <a href="https://www.baeldung.com/java-convert-roman-arabic">Baeldung: Converting Between
 *     Roman and Arabic Numerals in Java</a>
 */
public class RomanArabicConverter {
  public static final int MIN_VALUE = 0;
  public static final int MAX_VALUE = 4000;

  public static int romanToArabic(String input) {
    String romanNumeral = input.toUpperCase(Locale.US);
    int result = 0;

    List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

    int i = 0;

    while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
      RomanNumeral symbol = romanNumerals.get(i);
      if (romanNumeral.startsWith(symbol.name())) {
        result += symbol.getValue();
        romanNumeral = romanNumeral.substring(symbol.name().length());
      } else {
        i++;
      }
    }
    if (romanNumeral.length() > 0) {
      throw new IllegalArgumentException(input + " cannot be converted to a Roman Numeral");
    }

    return result;
  }

  public static String arabicToRoman(int number) {
    if ((number < 0) || (number > 4000)) {
      throw new IllegalArgumentException(number + " is not in range (0,4000]");
    }
    if (number == 0) {
      return "N";
    }

    List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

    int i = 0;
    StringBuilder sb = new StringBuilder();

    while (number > 0 && i < romanNumerals.size()) {
      RomanNumeral currentSymbol = romanNumerals.get(i);
      if (currentSymbol.getValue() <= number) {
        sb.append(currentSymbol.name());
        number -= currentSymbol.getValue();
      } else {
        i++;
      }
    }
    return sb.toString();
  }
}
