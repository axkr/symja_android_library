/**
 *
 * NumericalChameleon 3.0.0 - more than an unit converter - a NumericalChameleon
 * Copyright (c) 2001-2020 Dipl.-Inf. (FH) Johann Nepomuk Loefflmann, All Rights
 * Reserved, <http://www.numericalchameleon.net>.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *******************************************************************************
 *
 * @author SpanishNumber by Gustavo A. Herrera Fernández, México.
 * @contributor Johann N. Loefflmann, Germany [added addSyllables(String)]
 */
package net.numericalchameleon.util.spokennumbers;

public class SpanishNumber extends SpokenNumber {

    private final static String NULL = "cero",
            MINUS = "menos ";
    private final static String // build static string arrays for performance.
            // Cardinal numbers 1 through 29 are mostly special numbers
            specialNumbers[] = {"uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez",
        "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete", "dieciocho",
        "diecinueve", "veinte", "veintiuno", "veintidós", "veintitrés", "veinticuatro",
        "veinticinco", "veintiséis", "veintisiete", "veintiocho", "veintinueve"},
            // Cardinal numbers 30, 40, ..., 90:
            prefixTenths[] = {"treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"},
            // Cardinal numbers 100, 200, 300,..., 900:
            prefixHundreds[] = {"ciento", "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos",
        "setecientos", "ochocientos", "novecientos"},
            // Cardinal numbers 10^6, 10^12, 10^18 & 10^24.  Plural & singular forms (note accented singulars).
            prefixGillions[] = {" millones ", " billones ", " trillones ", " cuatrillones "},
            UN = "un",
            prefix1Gillion[] = {UN + " millón ", UN + " billón ", UN + " trillón ", UN + " cuatrillón "},
            Y = " y ",
            MIL = "mil",
            ONCE = "once",
            VEINTIUN = "veintiún";

    public SpanishNumber() {
        super();
    }

    public SpanishNumber(long number) throws Exception {
        super(number);
    }

    public SpanishNumber(String number) throws Exception {
        super(number);
    }

    public static String toString(long number) throws Exception {
        return (new SpanishNumber(number)).toString();
    }

    public static String toString(String number) throws Exception {
        return (new SpanishNumber(number)).toString();
    }

    @Override
    protected int getSupportedDigits() {
        return 30;
    }

    @Override
    public String getSoundDir() {
        return "spanish";
    }

    // Method called by the superclass...
    @Override
    protected void convert2Syllables() throws Exception {

        // "number" is a string object initialized by the superclass: SpokenNumber
        StringBuilder workingNumber = new StringBuilder(number);

        // Strip sign
        boolean negative = false;
        if (number.startsWith("-")) {
            negative = true;
            workingNumber.deleteCharAt(0);
        }

        // Bad input
        if (workingNumber.length() == 0 || workingNumber.length() > MAX_DIGITS) {
            return;
        }

        // we just want digits as output rather than the number
        if (numberType == DIGITS) { // digits
            number = workingNumber.toString();
            if (negative) {
                syllables.add(MINUS);
            }
            fillSyllables(NULL, specialNumbers);
            return;
        }

        // Trivial case
        if (number.equals("0")) {
            syllables.add(NULL);
            return;
        }

        // Normalize number to MAX_DIGIT by prepending an appropriate number of zeroes:
        for (int i = 1; workingNumber.length() < MAX_DIGITS; ++i) {
            workingNumber.insert(0, "0");
        }

        // Extract the gillions digits.  Could have done this using arrays in a loop, but for the sake of clarity...
        int mill4 = Integer.parseInt(workingNumber.substring(MAX_DIGITS - 30, MAX_DIGITS - 24));
        int mill3 = Integer.parseInt(workingNumber.substring(MAX_DIGITS - 24, MAX_DIGITS - 18));
        int mill2 = Integer.parseInt(workingNumber.substring(MAX_DIGITS - 18, MAX_DIGITS - 12));
        int mill1 = Integer.parseInt(workingNumber.substring(MAX_DIGITS - 12, MAX_DIGITS - 6));
        // Ditto for thousands & hundreads.
        int thou = Integer.parseInt(workingNumber.substring(MAX_DIGITS - 6, MAX_DIGITS - 3));
        int hund = Integer.parseInt(workingNumber.substring(MAX_DIGITS - 3, MAX_DIGITS));

        //Populate the "syllables" vector object (defined in superclass) with words
        if (negative) {
            syllables.add(MINUS);
        }
        addSyllables(gillions(mill4, 3));
        addSyllables(gillions(mill3, 2));
        addSyllables(gillions(mill2, 1));
        addSyllables(gillions(mill1, 0));
        addSyllables(thousands(thou));
        addSyllables(hundreds(hund));
        addSyllables(tenths(hund % 100));
    }

    private void addSyllables(String string) {
        for (String s : string.split(" ")) {
            syllables.add(s + " ");
        }
    }

    private String tenths(int m) {
        if (m == 0) {
            return "";
        }
        if (m < 30) {
            return specialNumbers[m - 1];
        }
        int mod = m % 10;
        return prefixTenths[(m / 10) - 3] + (mod > 0 ? Y + specialNumbers[(mod) - 1] : "");
    }

    // Cardinal numbers whose tenths of: thousands, millions, ... part end in "1" get special treatment
    private boolean isSpecialTenth(String s) {
        return (s.endsWith("1"));
    }

    private String specialTenths(int m) {
        switch (m) {
            case 11:
                return ONCE;
            case 21:
                return VEINTIUN;
            default: {
                String s = tenths(m);
                return (isSpecialTenth(m + "") ? s.substring(0, s.length() - 1) : s);
            }
        }
    }

    private String hundreds(int m) {
        if (m < 100) {
            return "";
        }
        return (m == 100 ? "cien" : prefixHundreds[(m / 100) - 1] + " ");
    }

    private String thousands(int m) {
        if (m == 0) {
            return "";
        }
        if (m == 1) {
            return MIL + " ";
        }
        return hundreds(m) + specialTenths(m % 100) + " " + MIL + " ";
    }

    private String gillions(int m, int magnitude) {
        if (m == 0) {
            return "";
        }
        if (m == 1) {
            return prefix1Gillion[magnitude];
        }
        int t = m / 1000;
        int h = m - (t * 1000);
        return thousands(t) + hundreds(h) + specialTenths(h % 100) + prefixGillions[magnitude];
    }

}
