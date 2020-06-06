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

import java.math.BigInteger;
import java.util.ArrayList;

import org.matheclipse.core.eval.exception.ArgumentTypeException;

abstract public class SpokenNumber {

    public static final int NUMBER = 0, DIGITS = 1, YEAR = 2;
    protected int MAX_DIGITS = 0;
    protected BigInteger MAX_VALUE = BigInteger.ZERO, // muß überschrieben werden
            MIN_VALUE = BigInteger.ZERO;   // muß überschrieben werden
    protected String SOUND_DIR = null;   // muß überschrieben werden
    public static String
            ERROR_TOOBIG = "Number is too big [maximum is (10^{0})-1]",
            ERROR_TOOSMALL = "Number is too small [minimum is -(10^{0})-1]";
    protected String number;
    protected ArrayList<String> syllables = new ArrayList<String>(); // Silbenvektor
    protected int digits[];
    protected AudioThread audioThread = null;
    protected int waitAfterASyllable = 0;
    protected int numberType = 0;  // 0=number, 1=digits, 2=year
    protected boolean appendBlank = true;

    /**
     * Default-Konstruktor.
     */
    public SpokenNumber() {
        init();
        try {
            setNumber(0);
        } catch (Exception e) {
        }
    }

    /**
     * Creates a new SpokenNumber
     * @param number die zu konvertierende Zahl
     * @exception Exception wenn number nicht zwischen MIN_VALUE und MAX_VALUE liegt
     */
    public SpokenNumber(long number) throws Exception {
        init();
        setNumber(number);
    }

    public SpokenNumber(String number) throws Exception {
        init();
        setNumber(number);
    }

    public void setNumber(long number) throws Exception {
        syllables.clear();
        checkNumber(Long.toString(number));
        this.number = Long.toString(number);
        //try {
            convert2Syllables();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }
    
    public ArrayList<String> getSyllables() {
        return syllables;
    }

    public void setNumber(String number) throws Exception {
        syllables.clear();
        checkNumber(number);
        this.number = number;
        //try {
            convert2Syllables();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }

    public String getNumber() {
        return number;
    }

    public void setNumberType(int numberType) {
        this.numberType = numberType;
    }

    public int getNumberType() {
        return numberType;
    }

    protected void checkNumber(String number) throws Exception {
        BigInteger big = new BigInteger(number);
        if (numberType != NUMBER) {
            return;
        }
        if (big.compareTo(MAX_VALUE) > 0) {
            throw new ArgumentTypeException("Number to big");//GeneralString.message(ERROR_TOOBIG, MAX_DIGITS));
        } else if (big.compareTo(MIN_VALUE) < 0) {
            throw new ArgumentTypeException("Number to small");//GeneralString.message(ERROR_TOOSMALL, MAX_DIGITS));
        }
    }

    /**
     * return the number as words
     * @return the number as words
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < syllables.size(); i++) {
            sb.append(syllables.get(i).toString());
            if (numberType == DIGITS && appendBlank) {
                sb.append(" ");
            }
            //if (appendBlank) sb.append(" ");
        }
        return sb.toString().trim();
    }

    abstract protected void convert2Syllables() throws Exception; // muß überschrieben werden

    protected void init() {
        MAX_DIGITS = getSupportedDigits();
        digits = new int[MAX_DIGITS];
        MAX_VALUE = new BigInteger(getMaximum(MAX_DIGITS));
        MIN_VALUE = new BigInteger("-" + MAX_VALUE);
    }

    // How many digits can we handle?
    abstract protected int getSupportedDigits();

    // How is the sound dir called?
    abstract public String getSoundDir();

    //public int getDigits() { return MAX_DIGITS; }
    public void play() {
        String dir = getSoundDir();
        if (dir != null) {
            if (audioThread != null) { // stop any old threads
                audioThread.interrupt();
                while (audioThread.isAlive()) {
                    // wait for a while
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                }
            }
            audioThread = new AudioThread(syllables, dir);
            audioThread.setWaitAfterASyllable(waitAfterASyllable);
            audioThread.start();
        }
    }

    public void stopPlaying() {
        if (audioThread != null) {
            audioThread.interrupt();
        }
    }

    public void fillSyllables(String Null, String[] _1to9) {
        for (int i = 0; i < number.length(); i++) {
            int digit = 0;
            try {
                digit = Integer.parseInt(number.substring(i, i + 1));
            } catch (Exception e) {
            }
            if (digit == 0) {
                syllables.add(Null);
            } else {
                syllables.add(_1to9[digit - 1]);
            }
        }
    }

    /**
     * fill the syllables vector
     * @param _0to9 String values from 0 to 9
     */
    public void fillSyllables(String[] _0to9) {
        for (int i = 0; i < number.length(); i++) {
            int digit = 0;
            try {
                digit = Integer.parseInt(number.substring(i, i + 1));
            } catch (Exception e) {
            }
            syllables.add(_0to9[digit]);
        }
    }

    protected void number2digits() {
        // absolut value verwenden!
        int offset;
        if (number.charAt(0) == '-') {
            offset = 1;
        } else {
            offset = 0;
        }
        for (int i = offset; i < number.length(); i++) {
            int digit = 0;
            try {
                int start = (number.length() - i) - 1;
                start += offset;
                digit = Integer.parseInt(number.substring(start, start + 1));
            } catch (Exception e) {
            }
            digits[i - offset] = digit;
        }
        for (int i = number.length(); i < MAX_DIGITS; i++) {
            digits[i - offset] = 0;
        }
    }

    // returns the maximum of digits, e.g getMaximum(3) returns 999
    public static String getMaximum(int digits) {
        StringBuilder sb = new StringBuilder(digits);
        sb.setLength(digits);
        for (int i = 0; i < digits; i++) {
            sb.setCharAt(i, '9');
        }
        return sb.toString();
    }
}
