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
package net.numericalchameleon.util.spokennumbers;

/**
 * SpokenNumber implementation for the Romanian language
 *
 * @author Marius Scurtescu (mariuss@romanians.bc.ca) @created May 8, 2002
 */
public class RomanianNumber extends SpokenNumber {

    private final static int MASCULIN = 0;
    private final static int FEMININ = 1;
    private final static int NEUTRU = 2;
    private final static String UNITATI[] = {"", "unu", "doi", "trei", "patru", "cinci", "\u015fase", "\u015fapte", "opt", "nou\u0103"};
    // {"", "unu", "doi", "trei", "patru", "cinci", "sase",      "sapte",      "opt", "noua"};
    private final static String SPREZECE[] = {"zece", "unsprezece", "doisprezece", "treisprezece", "paisprezece", "cincisprezece", "\u015faisprezece", "\u015faptesprezece", "optsprezece", "nou\u0103sprezece"};
    // {"zece", "unsprezece", "doisprezece", "treisprezece", "paisprezece", "cincisprezece", "saisprezece",      "saptesprezece",      "optsprezece", "nouasprezece"};
    private final static String ZECI[] = {"", "", "dou\u0103zeci", "treizeci", "patruzeci", "cincizeci", "\u015faizeci", "\u015faptezeci", "optzeci", "nou\u0103zeci"};
    // {"", "", "douazeci",      "treizeci", "patruzeci", "cincizeci", "saizeci",      "saptezeci",      "optzeci", "nouazeci"};
    private final static String GRUPA[][] = {
        {"sut\u0103", "sute"}, // {"suta", "sute"},
        {"mie", "mii"},
        {"milion", "milioane"},
        {"miliard", "miliarde"},
        {"biliard", "biliarde"},
        {"triliard", "triliarde"}
    };
    private final static String ZERO = "zero";
    private final static String MINUS = "minus";
    private final static String DE = "de";
    private final static String SI = "\u015fi"; // "si";
    private final static String UNU[] = {"un", "o", "un"};
    private final static String UNA[] = {"unu", "una", "unu"};
    private final static String DOI[] = {"doi", "dou\u0103", "dou\u0103"}; // {"doi", "doua", "doua"};
    private final static int GENGRUPA[] = {FEMININ, FEMININ, NEUTRU, NEUTRU, NEUTRU, NEUTRU};

    /**
     * Constructor for the RomanianNumber object
     */
    public RomanianNumber() {
        super();
    }

    /**
     * Constructor for the RomanianNumber object
     *
     * @param nNumber the number to convert
     */
    public RomanianNumber(long nNumber) throws Exception {
        super(nNumber);
    }

    /**
     * Constructor for the RomanianNumber object
     *
     * @param strNumber the number to convert as a string
     */
    public RomanianNumber(String strNumber) throws Exception {
        super(strNumber);
    }

    @Override
    protected int getSupportedDigits() {
        return 18;
    }

    @Override
    public String getSoundDir() {
        return "romanian";
    }

    /**
     * wird zum Konvertieren der Zahl in Silben benötigt, Ergebnis wird in einen
     * Vektor geschrieben.
     */
    @Override
    protected void convert2Syllables() {

        if (number.charAt(0) == '-') {
            number = number.substring(1);
            syllables.add(MINUS);
        }

        // we just would like the digits
        if (numberType == DIGITS) {
            String[] single = new String[9];
            System.arraycopy(UNITATI, 1, single, 0, 9);
            fillSyllables(ZERO, single);
            return;
        }

        number2digits();
        if (number.equals("0")) {
            syllables.add(ZERO);
        } else {
            int cntGroups = digits.length / 3;

            for (int i = cntGroups; i > 0; i--) {
                int nUnits = digits[(i - 1) * 3 + 0];
                int nTens = digits[(i - 1) * 3 + 1];
                int nHundresds = digits[(i - 1) * 3 + 2];

                if (nUnits + nTens + nHundresds == 0) {
                    continue;
                }

                if (i == 1) {
                    convertThreesome(nUnits, nTens, nHundresds, MASCULIN, true);
                } else {
                    switch (nUnits + nTens * 10 + nHundresds * 100) {
                        case 1:
                            syllables.add(UNU[GENGRUPA[i - 1]]);
                            syllables.add(GRUPA[i - 1][0]);
                            break;

                        case 2:
                            syllables.add(DOI[GENGRUPA[i - 1]]);
                            syllables.add(GRUPA[i - 1][1]);
                            break;

                        default:
                            convertThreesome(nUnits, nTens, nHundresds, GENGRUPA[i - 1], false);

                            if (nUnits + nTens * 10 >= 20 || nUnits + nTens * 10 == 0) {
                                syllables.add(DE);
                            }

                            syllables.add(GRUPA[i - 1][1]);
                            break;
                    }
                }
            }
        }

        addSpaces();
    }

    /**
     * Converts a three digit number
     *
     * @param nUnits the units to convert
     * @param nTens the tens to convert
     * @param nHundreds the hundreds to convert
     * @param nGender the gender of the converted number should be one of
     * (MASCULIN, FEMININ or NEUTRU)
     * @param bFirstGroup should be true if this the first group of three digits
     * of a larger number
     */
    private void convertThreesome(int nUnits, int nTens, int nHundreds, int nGender, boolean bFirstGroup) {
        switch (nHundreds) {
            case 0:
                break;
            case 1:
                syllables.add(UNU[FEMININ]);
                syllables.add(GRUPA[0][0]);
                break;
            case 2:
                syllables.add(DOI[FEMININ]);
                syllables.add(GRUPA[0][1]);
                break;
            default:
                syllables.add(UNITATI[nHundreds]);
                syllables.add(GRUPA[0][1]);
                break;
        }

        if (nTens > 1) {
            syllables.add(ZECI[nTens]);

            if (nUnits > 0) {
                syllables.add(SI);
            }
        } else if (nTens == 1) {
            syllables.add(SPREZECE[nUnits]);
        }

        if (nUnits > 0 && nTens != 1) {
            switch (nUnits) {
                case 1:
                    if (bFirstGroup) {
                        syllables.add(UNITATI[1]);
                    } else {
                        syllables.add(UNA[nGender]);
                    }
                    break;

                case 2:
                    if (bFirstGroup) {
                        syllables.add(UNITATI[2]);
                    } else {
                        syllables.add(DOI[nGender]);
                    }
                    break;

                default:
                    syllables.add(UNITATI[nUnits]);
                    break;
            }
        }
    }

    /**
     * add spaces after each word, except the last one
     */
    protected void addSpaces() {
        for (int i = 0; i < syllables.size() - 1; i++) {
            syllables.set(i, ((String) syllables.get(i)) + " ");
        }
    }

    /**
     * returns number in romanian words
     *
     * @param nNumber Description of class
     * @return Description of class
     * @exception Exception Description of {2}
     */
    public static String toString(long nNumber) throws Exception {
        return (new RomanianNumber(nNumber)).toString();
    }

    /**
     * Description of the Method
     *
     * @param strNumber Description of class
     * @return Description of class
     * @exception Exception Description of {2}
     */
    public static String toString(String strNumber) throws Exception {
        return (new RomanianNumber(strNumber)).toString();
    }
}
