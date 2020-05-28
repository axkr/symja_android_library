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
package net.numericalchameleon.util.spokentime;

import net.numericalchameleon.util.spokennumbers.UKEnglishNumber;
import net.numericalchameleon.util.spokennumbers.USEnglishNumber;

public class EnglishTime extends SpokenTime implements NumberToWordsInterface, MilitaryTimeInterface {

    public enum HourclockType {
        HOURCLOCK12_COLLOQUIAL_US,
        HOURCLOCK24_MILITARY_US,
        HOURCLOCK12_COLLOQUIAL_UK,
        HOURCLOCK24_MILITARY_UK
    }
    
    private HourclockType type = HourclockType.HOURCLOCK12_COLLOQUIAL_US;
    
    private final static String
        BLANK = " ",
        MIDNIGHT = "midnight",
        NOON = "noon",
        OCLOCK = "o'clock",
        A_QUARTER = "a quarter", // UK only
        QUARTER = "quarter",
        HALF = "half",
        AFTER = "after", // US only
        PAST = "past", // UK only
        TO = "to",
        MINUTES = "minutes",
        MINUTE = "minute",
        HUNDERT = "hundred";
    
    public EnglishTime(HourclockType hourclock) {
        super();
        this.type = hourclock;
    }

    @Override
    public void init() {
        syllables.clear();
        try {
            switch (type) {
                case HOURCLOCK12_COLLOQUIAL_US:
                case HOURCLOCK12_COLLOQUIAL_UK:
                    hourclock12_colloquial();
                    break;
                case HOURCLOCK24_MILITARY_US:
                case HOURCLOCK24_MILITARY_UK:
                    hourclock24_military();
                    break;
            }
        } catch (Exception e) {
            System.err.println(e); // should not happen
        }
        
    }
        
    private void hourclock12_colloquial() {
            if (minutes == 0) {
                if (hours == 0) {
                    syllables.add(MIDNIGHT);
                    return;
                }
                if (hours == 12) {
                    syllables.add(NOON);
                    return;
                }
                // timeInWords = String.format("%s o'clock", hoursInWords(hours));
                hoursInWords(hours);
                syllables.add(BLANK);
                syllables.add(OCLOCK);
            } else if (minutes == 15) {
                // timeInWords = String.format("quarter after %s", hoursInWords(hours));
                if (type == HourclockType.HOURCLOCK12_COLLOQUIAL_UK) {
                    syllables.add(A_QUARTER);
                } else {
                    syllables.add(QUARTER);
                }
                syllables.add(BLANK);
                if (type == HourclockType.HOURCLOCK12_COLLOQUIAL_US) {
                    syllables.add(AFTER);
                } else {
                    syllables.add(PAST);
                }                
                syllables.add(BLANK);
                hoursInWords(hours);
            } else if (minutes == 30) {
                if (type == HourclockType.HOURCLOCK12_COLLOQUIAL_UK) {
                    syllables.add(HALF);
                    syllables.add(BLANK);
                    syllables.add(PAST);
                    syllables.add(BLANK);
                    hoursInWords(hours);
                } else {
                    // US
                    // timeInWords = String.format("%s %s", hoursInWords(hours), minutesInWords(minutes));
                    hoursInWords(hours);
                    syllables.add(BLANK);
                    minutesInWords(minutes);
                }
            } else if (minutes == 45) {
                // timeInWords = String.format("quarter to %s", hoursInWords(hours + 1));
                if (type == HourclockType.HOURCLOCK12_COLLOQUIAL_UK) {
                    syllables.add(A_QUARTER);
                } else {
                    syllables.add(QUARTER);
                }
                syllables.add(BLANK);
                syllables.add(TO);
                syllables.add(BLANK);
                hoursInWords(hours + 1);
            } else if (minutes < 30) {
                // timeInWords = String.format("%s after %s", minutesInWords(minutes), hoursInWords(hours));
                minutesInWords(minutes);
                if (minutes < 5) {
                    syllables.add(BLANK);
                    if (minutes == 1) {
                        syllables.add(MINUTE);
                    } else {
                        syllables.add(MINUTES);
                    }
                }                
                syllables.add(BLANK);
                if (type == HourclockType.HOURCLOCK12_COLLOQUIAL_US) {
                    syllables.add(AFTER);
                } else {
                    syllables.add(PAST);
                }   
                syllables.add(BLANK);
                hoursInWords(hours);
            } else if (minutes > 30) {
                // timeInWords = String.format("%s to %s", minutesInWords(60 - minutes), hoursInWords(hours + 1));
                minutesInWords(60 - minutes);
                if (60 - minutes < 5) {
                    syllables.add(BLANK);
                    if (60 - minutes == 1) {
                        syllables.add(MINUTE);
                    } else {
                        syllables.add(MINUTES);
                    }
                }
                syllables.add(BLANK);
                syllables.add(TO);
                syllables.add(BLANK);
                hoursInWords(hours + 1);
            }
    }

    private void hourclock24_military() {
        MilitaryTime mt = new MilitaryTime(this, hours, minutes);
    }
    
    private void hoursInWords(int hours) {
        // normalize from 24h system to 12h system        
        int h = hours;
        if (type == HourclockType.HOURCLOCK12_COLLOQUIAL_US
         || type == HourclockType.HOURCLOCK12_COLLOQUIAL_UK) {
            if (h >= 12) {
                h -= 12;
            }
            if (h == 0) {
                h = 12;
            }
        }
        numberToWords(h);
    }
    
    @Override
    public void numberToWords(int number) {
        try {
            if (type == HourclockType.HOURCLOCK12_COLLOQUIAL_US
             || type == HourclockType.HOURCLOCK24_MILITARY_US) {
               USEnglishNumber englishNumber = new USEnglishNumber(number);
               syllables.addAll(englishNumber.getSyllables());
            } else {
               UKEnglishNumber englishNumber = new UKEnglishNumber(number);
               syllables.addAll(englishNumber.getSyllables());                
            }                
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void minutesInWords(int minutes) {
        numberToWords(minutes);
    }

    @Override
    public String getSoundDir() {
        if (type == HourclockType.HOURCLOCK12_COLLOQUIAL_UK)
            return "uk_english";
        return "us_english";
    }

    @Override
    public String timeInWords() {
        StringBuilder sb = new StringBuilder();
        for (String syllable : syllables) {
            sb.append(syllable);
        }
        return sb.toString();
    }

    @Override
    public void separator() {
        syllables.add(" ");
    }

    @Override
    public void hundredString() {
        syllables.add(HUNDERT);
    }
}
