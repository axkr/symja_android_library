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
 */
package net.numericalchameleon.util.romannumerals;

import java.util.Properties;

public class RomanNumeral {

    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 49999;
    private String sourceString = null;
    private long sourceLong = 0;

    // http://www.unicode.org/charts/PDF/U2150.pdf
    private final static char
            _5000 = 'ↁ',
            _10000 = 'ↂ';

    public static enum Errors {
        INVALIDCHAR,
        ISEMPTY,
        CHARONLYONCE,
        CHAR3TIMES,
        CHAR4TIMES,
        OUTOFRANGE;
    }
    private static Properties props;

    static {
        props = new Properties();
        props.put(Errors.ISEMPTY.name(),
                "The roman value may not be empty");
        props.put(Errors.INVALIDCHAR.name(),
                "The roman value is invalid (only ↂ, ↁ, M, D, C, L, X, V and I are allowed).");
        props.put(Errors.CHARONLYONCE.name(),
                "The character {0} is allowed only once in a roman number");
        props.put(Errors.CHAR3TIMES.name(),
                "It is prohibited to use the character {0} more than 3 times in a sequence");
        props.put(Errors.CHAR4TIMES.name(),
                "It is prohibited to use the character {0} more than 4 times in a sequence");
        props.put(Errors.OUTOFRANGE.name(),
                "The decimal value must be between 1 and 49999 for roman calculations");
    }

    public static void setL10NProperties(Properties props) {
        RomanNumeral.props = props;
    }

//    public RomanNumeral(String roman) throws RomanNumeralException {
//        setString(roman);
//    }

    public RomanNumeral(long myLong) throws RomanNumeralException {
        setLong(myLong);
    }

    private int wandle(char c) throws RomanNumeralException {
        switch (c) {
            case _10000:
                return 10000;
            case _5000:
                return 5000;
            case 'M':
                return 1000;
            case 'D':
                return 500;
            case 'C':
                return 100;
            case 'L':
                return 50;
            case 'X':
                return 10;
            case 'V':
                return 5;
            case 'I':
                return 1;
            default:
                throw new RomanNumeralException(props.getProperty(Errors.INVALIDCHAR.name()));
        }
    }

//    public void setString(String myString) throws RomanNumeralException {
//        sourceString = myString.toUpperCase();
//        try {
//            sourceLong = convert2Long();
//        } catch (RomanNumeralException e) {
//            sourceLong = 0;
//            throw new RomanNumeralException(e.getMessage());
//        }
//    }

    public void setLong(long myLong) throws RomanNumeralException {
        if (myLong < MIN_VALUE || myLong > MAX_VALUE) {
            throw new RomanNumeralException(props.getProperty(Errors.OUTOFRANGE.name()));
        }
        sourceLong = myLong;
        sourceString = convert2Roman();
    }

    public long toLong() {
        return sourceLong;
    }

    public String toRoman() {
        return convert2Roman();
    }

//    private long convert2Long() throws RomanNumeralException {
//        if (sourceString.length() == 0) { // equals("")
//            throw new RomanNumeralException(props.getProperty(Errors.ISEMPTY.name()));
//        }
//        if (GeneralString.countChar(sourceString, 'V') > 1) {
//            throw new RomanNumeralException(GeneralString.message(
//                    props.getProperty(Errors.CHARONLYONCE.name()), 'V'));
//        }
//        if (GeneralString.countChar(sourceString, 'L') > 1) {
//            throw new RomanNumeralException(GeneralString.message(
//                    props.getProperty(Errors.CHARONLYONCE.name()), 'L'));
//        }
//        if (GeneralString.countChar(sourceString, 'D') > 1) {
//            throw new RomanNumeralException(GeneralString.message(
//                    props.getProperty(Errors.CHARONLYONCE.name()), 'D'));
//        }
//
//        if (sourceString.indexOf("IIIII") >= 0) {
//            throw new RomanNumeralException(GeneralString.message(
//                    props.getProperty(Errors.CHAR4TIMES.name()), 'I'));
//        }
//        if (sourceString.indexOf("XXXXX") >= 0) {
//            throw new RomanNumeralException(GeneralString.message(
//                    props.getProperty(Errors.CHAR4TIMES.name()), 'X'));
//        }
//        if (sourceString.indexOf("CCCCC") >= 0) {
//            throw new RomanNumeralException(GeneralString.message(
//                    props.getProperty(Errors.CHAR4TIMES.name()), 'C'));
//        }
//
//        int i, len = sourceString.length();
//        long sum;
//
//        // letztes Zeichen umwandeln
//        try {
//            sum = (long) wandle(sourceString.charAt(len - 1));
//
//            for (i = (len - 1); i >= 1; i--) {
//                long temp = wandle(sourceString.charAt(i - 1));
//                if (wandle(sourceString.charAt(i)) <= temp) {
//                    sum += temp;
//                } else {
//                    sum -= temp;
//                }
//            }
//        } catch (RomanNumeralException e) {
//            throw new RomanNumeralException(e.getMessage());
//        }
//        if (sum < MIN_VALUE || sum > MAX_VALUE) {
//            throw new RomanNumeralException(props.getProperty(Errors.OUTOFRANGE.name()));
//        }
//        return sum;
//    }

    private String convert2Roman() {
        StringBuilder buf = new StringBuilder();
        long value = sourceLong;

        // I vor V oder X: IV (4), IX (9)
        // X vor L oder C: XL (40), XC (90)
        // C vor D oder M: CD (400), CM (900)
        // M vor ↁ oder ↂ: Mↁ (4000), Mↂ (9000)

        while (value >= 10000) {
            buf.append(_10000);
            value -= 10000;
        }
        if (value >= 9000) {
            buf.append('M');
            buf.append(_10000);
            value -= 9000;
        }
        while (value >= 5000) {
            buf.append(_5000);
            value -= 5000;
        }
        if (value >= 4000) {
            buf.append('M');
            buf.append(_5000);
            value -= 4000;
        }
        // C vor M: CM (900)
        while (value >= 1000) {
            buf.append('M');
            value -= 1000;
        }
        if (value >= 900) {
            buf.append("CM");
            value -= 900;
        }
        if (value >= 500) {
            buf.append('D');
            value -= 500;
        }
        // C vor D: CD (400)
        if (value >= 400) {
            buf.append("CD");
            value -= 400;
        }
        while (value >= 100) {
            buf.append('C');
            value -= 100;
        }
        if (value >= 90) {
            buf.append("XC");
            value -= 90;
        }
        if (value >= 50) {
            buf.append('L');
            value -= 50;
        }
        if (value >= 40) {
            buf.append("XL");
            value -= 40;
        }
        while (value >= 10) {
            buf.append('X');
            value -= 10;
        }
        if (value >= 9) {
            buf.append("IX");
            value -= 9;
        }
        if (value >= 5) {
            buf.append('V');
            value -= 5;
        }
        if (value >= 4) {
            buf.append("IV");
            value -= 4;
        }
        while (value > 0) {
            buf.append('I');
            value -= 1;
        }
        return buf.toString();
    }
}
