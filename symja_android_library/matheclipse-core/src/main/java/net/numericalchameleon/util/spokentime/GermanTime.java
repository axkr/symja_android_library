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

import net.numericalchameleon.util.spokennumbers.GermanNumber;
import net.numericalchameleon.util.spokennumbers.GermanSwitzerlandNumber;

public class GermanTime extends SpokenTime implements NumberToWordsInterface, MilitaryTimeInterface {
    
    public enum Country {
        DE, CH, AT
    }
   
    public enum HourclockType {        
        HOURCLOCK12_VARIANT1_DE(12, Country.DE),
        HOURCLOCK12_VARIANT2_DE(12, Country.DE),
        HOURCLOCK12_VARIANT3_DE(12, Country.DE),
        HOURCLOCK12_VARIANT4_DE(12, Country.DE),
        HOURCLOCK12_VARIANT0_DE(12, Country.DE),
        HOURCLOCK12_VARIANT1_CH(12, Country.CH),
        HOURCLOCK24_DE(24, Country.DE),
        HOURCLOCK24_FORMAL_DE(24, Country.DE),
        HOURCLOCK24_MILITARY_DE(24, Country.DE),
        HOURCLOCK24_CH(24, Country.CH),
        HOURCLOCK24_FORMAL_CH(24, Country.CH),
        HOURCLOCK24_MILITARY_CH(24, Country.CH);

        private final int hours;
        private final Country country;
        
        private HourclockType(int hours, Country country) {
            this.hours = hours;
            this.country = country;
        }
        
        public Country getCountry() {
            return country;
        }
        
        public int getHours() {
            return hours;
        }
        
    }
    private final static String MINUTE = "Minute",
            MINUTES = "Minuten",
            UHR = "Uhr",
            UND = "und",
            BLANK = " ",
            HUNDERT = "Hundert",
            VIERTEL = "Viertel",
            DREIVIERTEL = "Dreiviertel",
            Halb = "Halb",
            halb = "halb",
            NACH = "nach",
            VOR = "vor",
            AB = "ab"; // Switzerland

    private final HourclockType type;

    public GermanTime(HourclockType hourclock) {
        super();
        this.type = hourclock;
    }

    private void hourclock24_normal() {
        // nicht "eins Uhr", sondern "ein Uhr"
        if (hours == 1) {
            syllables.add(GermanNumber.EIN);
        } else {
            hoursInWords(hours);
        }
        syllables.add(BLANK);
        syllables.add(UHR);
        if (minutes == 0) {
            // nicht eins Uhr, sondern ein Uhr
            // timeInWords = String.format("%s %s", prefix, UHR);
            return;
        }
        // timeInWords = String.format("%s Uhr %s", prefix, minutesInWords(minutes));
        syllables.add(BLANK);
        minutesInWords(minutes);
    }

    private void hourclock24_formal() {
        // nicht "eins Uhr", sondern "ein Uhr"
        if (hours == 1) {
            syllables.add(GermanNumber.EIN);
        } else {
            hoursInWords(hours);
        }
        syllables.add(BLANK);
        syllables.add(UHR);
        if (minutes == 0) {
            //timeInWords = String.format("%s Uhr", prefixHour);
            return;
        }

        //timeInWords = String.format("%s Uhr und %s %s", prefixHour, (minutes == 1) ? GermanNumber.EINE: minutesInWords(minutes), wordMinutes);
        syllables.add(BLANK);
        syllables.add(UND);
        syllables.add(BLANK);

        // nicht "eins Minute", sondern "eine Minute"
        if (minutes == 1) {
            syllables.add(GermanNumber.EINE);
        } else {
            minutesInWords(minutes);
        }
        syllables.add(BLANK);
        syllables.add((minutes == 1) ? MINUTE : MINUTES);
    }

    private void hourclock24_military() {
        MilitaryTime mt = new MilitaryTime(this, hours, minutes);
    }

    private void hourclock12() {
        if (minutes == 0) {
            // timeInWords = hoursInWords(hours);
            hoursInWords(hours);
        } else if (minutes == 15) {
            if (type == HourclockType.HOURCLOCK12_VARIANT1_DE
                    || type == HourclockType.HOURCLOCK12_VARIANT3_DE) { // DE West
                //timeInWords = String.format("Viertel nach %s", hoursInWords(hours));
                syllables.add(VIERTEL);
                syllables.add(BLANK);
                syllables.add(NACH);
                syllables.add(BLANK);
                hoursInWords(hours);
            } else if (type == HourclockType.HOURCLOCK12_VARIANT2_DE
                    || type == HourclockType.HOURCLOCK12_VARIANT4_DE) { // DE Ost
                // timeInWords = String.format("Viertel %s", hoursInWords(hours + 1));
                syllables.add(VIERTEL);
                syllables.add(BLANK);
                hoursInWords(hours + 1);
            } else if (type == HourclockType.HOURCLOCK12_VARIANT1_CH) { // Switzerland "Viertel ab"
                syllables.add(VIERTEL);
                syllables.add(BLANK);
                syllables.add(AB);
                syllables.add(BLANK);
                hoursInWords(hours);
            }
        } else if (minutes == 30) {
            // timeInWords = String.format("Halb %s", hoursInWords(hours + 1));
            syllables.add(Halb);
            syllables.add(BLANK);
            hoursInWords(hours + 1);
        } else if (minutes == 45) {
            if (type == HourclockType.HOURCLOCK12_VARIANT1_DE || // DE West
                    type == HourclockType.HOURCLOCK12_VARIANT3_DE || // DE West
                    type == HourclockType.HOURCLOCK12_VARIANT1_CH) { // Switzerland
                // token = "Viertel vor";
                syllables.add(VIERTEL);
                syllables.add(BLANK);
                syllables.add(VOR);
            } else if (type == HourclockType.HOURCLOCK12_VARIANT2_DE || // DE Ost
                    type == HourclockType.HOURCLOCK12_VARIANT4_DE) { // DE Ost
                syllables.add(DREIVIERTEL);
            }
            // timeInWords = String.format("%s %s", token, hoursInWords(hours + 1));
            syllables.add(BLANK);
            hoursInWords(hours + 1);

        } else if ((minutes >= 25) && (minutes < 30)) {
            // timeInWords = String.format("%s vor halb %s", minutesInWords(30 - minutes), hoursInWords(hours + 1));
            minutesInWords(30 - minutes);
            syllables.add(BLANK);
            syllables.add(VOR);
            syllables.add(BLANK);
            syllables.add(halb);
            syllables.add(BLANK);
            hoursInWords(hours + 1);
        } else if ((minutes >= 20) && (minutes < 25)) {
            if (type == HourclockType.HOURCLOCK12_VARIANT1_DE
                    || type == HourclockType.HOURCLOCK12_VARIANT4_DE) { // DE West
                // timeInWords = String.format("%s nach %s", minutesInWords(minutes), hoursInWords(hours));
                minutesInWords(minutes);
                syllables.add(BLANK);
                syllables.add(NACH);
                syllables.add(BLANK);
                hoursInWords(hours);
            } else if (type == HourclockType.HOURCLOCK12_VARIANT2_DE
                    || type == HourclockType.HOURCLOCK12_VARIANT3_DE) { // DE Ost
                //timeInWords = String.format("%s vor halb %s", minutesInWords(30 - minutes), hoursInWords(hours + 1));
                minutesInWords(30 - minutes);
                syllables.add(BLANK);
                syllables.add(VOR);
                syllables.add(BLANK);
                syllables.add(halb);
                syllables.add(BLANK);
                hoursInWords(hours + 1);
            } else if (type == HourclockType.HOURCLOCK12_VARIANT1_CH) {
                minutesInWords(minutes);
                syllables.add(BLANK);
                syllables.add(AB);
                syllables.add(BLANK);
                hoursInWords(hours);
            }

        } else if ((minutes > 30) && (minutes <= 35)) {
            // timeInWords = String.format("%s nach halb %s", minutesInWords(minutes - 30), hoursInWords(hours + 1));
            minutesInWords(minutes - 30);
            syllables.add(BLANK);
            syllables.add(NACH);
            syllables.add(BLANK);
            syllables.add(halb);
            syllables.add(BLANK);
            hoursInWords(hours + 1);
        } else if ((minutes > 35) && (minutes <= 40)) {
            if (type == HourclockType.HOURCLOCK12_VARIANT1_DE
                    || type == HourclockType.HOURCLOCK12_VARIANT4_DE || // DE West
                    type == HourclockType.HOURCLOCK12_VARIANT1_CH) {
                // timeInWords = String.format("%s vor %s", minutesInWords(60 - minutes), hoursInWords(hours + 1));
                minutesInWords(60 - minutes);
                syllables.add(BLANK);
                syllables.add(VOR);
                syllables.add(BLANK);
                hoursInWords(hours + 1);
            } else if (type == HourclockType.HOURCLOCK12_VARIANT2_DE
                    || type == HourclockType.HOURCLOCK12_VARIANT3_DE) { // Ost
                // timeInWords = String.format("%s nach halb %s", minutesInWords(minutes - 30), hoursInWords(hours + 1));
                minutesInWords(minutes - 30);
                syllables.add(BLANK);
                syllables.add(NACH);
                syllables.add(BLANK);
                syllables.add(halb);
                syllables.add(BLANK);
                hoursInWords(hours + 1);
            }

        } else if (minutes > 40) {
            // timeInWords = String.format("%s vor %s", minutesInWords(60 - minutes), hoursInWords(hours + 1));
            minutesInWords(60 - minutes);
            syllables.add(BLANK);
            syllables.add(VOR);
            syllables.add(BLANK);
            hoursInWords(hours + 1);
        } else if (minutes < 20) {
            if (type == HourclockType.HOURCLOCK12_VARIANT1_CH) {
                minutesInWords(minutes);
                syllables.add(BLANK);
                syllables.add(AB);
                syllables.add(BLANK);
                hoursInWords(hours);
            } else { // Germany
                // timeInWords = String.format("%s nach %s", minutesInWords(minutes), hoursInWords(hours));
                minutesInWords(minutes);
                syllables.add(BLANK);
                syllables.add(NACH);
                syllables.add(BLANK);
                hoursInWords(hours);
            }
        }
    }

    @Override
    public void init() {
        syllables.clear();
        try {
            switch (type) {
                case HOURCLOCK12_VARIANT1_DE:
                case HOURCLOCK12_VARIANT2_DE:
                case HOURCLOCK12_VARIANT3_DE:
                case HOURCLOCK12_VARIANT4_DE:
                case HOURCLOCK12_VARIANT1_CH:
                    hourclock12();
                    break;
                case HOURCLOCK12_VARIANT0_DE:
                case HOURCLOCK24_DE:
                case HOURCLOCK24_CH:
                    hourclock24_normal();
                    break;
                case HOURCLOCK24_FORMAL_DE:
                case HOURCLOCK24_FORMAL_CH:
                    hourclock24_formal();
                    break;
                case HOURCLOCK24_MILITARY_DE:
                case HOURCLOCK24_MILITARY_CH:
                    hourclock24_military();
                    break;
            }
        } catch (Exception e) {
            System.err.println(e); // should not happen
        }
    }

    private void hoursInWords(int hours) {
        int h = hours;
        // adjustment for 12 hour clock
        if (type.getHours() == 12) {
            if (h >= 12) {
                h -= 12;
            }
            if (h == 0) {
                h = 12;
            }
        }
        numberToWords(h);
    }

    public void minutesInWords(int minutes) {
        numberToWords(minutes);
    }

    @Override
    public void numberToWords(int number) {
        try {
            if (type.getCountry() == Country.DE || type.getCountry() == Country.AT) {
                GermanNumber germanNumber = new GermanNumber(number);
                syllables.addAll(germanNumber.getSyllables());
                return;
            }
            if (type.getCountry() == Country.CH) {
                GermanSwitzerlandNumber germanSwitzerlandNumber = new GermanSwitzerlandNumber(number);
                syllables.addAll(germanSwitzerlandNumber.getSyllables());
            } else {
                throw new IllegalArgumentException(type + " is not supported");
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    public String getSoundDir() {
        return "german";
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
    public void hundredString() {
        syllables.add(HUNDERT);
    }

    @Override
    public void separator() {
        syllables.add(" ");
    }
}
