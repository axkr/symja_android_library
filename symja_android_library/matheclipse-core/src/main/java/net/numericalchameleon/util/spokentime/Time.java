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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time {

    private int hours;
    private int minutes;

    public Time() {
        this.hours = 0;
        this.minutes = 0;
    }

    public Time(int minutes) {
        this.hours = minutes / 60;
        this.hours = hours % 24;
        
        this.minutes = minutes % 60;    
    }
    
    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    /**
     * @return the hours
     */
    public int getHours() {
        return hours;
    }

    /**
     * @param hours the hours to set
     */
    public void setHours(int hours) {
        this.hours = hours;
    }

    /**
     * @return the minutes
     */
    public int getMinutes() {
        return minutes;
    }

    public void addMinutes(int m) {
        while (m > 0) {
            minutes++;
            if (minutes > 59) {
                minutes = 0;
                hours++;
            }
            if (hours > 23) {
                hours = 0;
            }
            m--;
        }
        while (m < 0) {
            minutes--;
            if (minutes < 0) {
                hours--;
                minutes = 59;
            }
            if (hours < 0) {
                hours = 23;
            }
            m++;
        }
    }

    /**
     * @param minutes the minutes to set
     */
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hours, minutes);
    }
    
    public static final int FORMAT24H = 24, FORMATDEC = 10, FORMAT12H = 12;
    
    public static String format(Time time, int format) {
        switch (format) {
            case FORMAT24H:
                return String.format("%02d:%02d", time.getHours(), time.getMinutes());
            case FORMAT12H:
                int hour = time.getHours();
                boolean am = true;
                if (hour >= 12) {
                    hour -= 12;                    
                    am = false;
                }
                if (hour == 0) {
                    hour = 12;
                }
                String ampm = am ? "am":"pm";
                return String.format("%02d:%02d %s", hour, time.getMinutes(), ampm);
            case FORMATDEC:
                return Integer.toString(time.getHours() * 100 + time.getMinutes());
            default:
                throw new IllegalArgumentException("inernal error");
        }
    }
    
    
    public static Time parse(String input, int format) throws IllegalArgumentException {

        Time time = new Time();
        int minutes = 0;
        int hours = 0;

        switch (format) {
            case FORMAT24H:
                final String REGEX24H = "(\\d{2})[ \\.:](\\d{2})";
                Pattern pattern24h = Pattern.compile(REGEX24H, Pattern.CASE_INSENSITIVE);
                Matcher matcher24h = pattern24h.matcher(input);
                if (matcher24h.find()) {
                    hours = Integer.parseInt(matcher24h.group(1));
                    minutes = Integer.parseInt(matcher24h.group(2));
                    if ((hours > 23) || (minutes > 59)) {
                        throw new IllegalArgumentException("invalid");
                    }
                } else {
                    throw new IllegalArgumentException("invalid");
                }
                time.setHours(hours);
                time.setMinutes(minutes);
                break;

            case FORMAT12H:
                final String REGEX12H = "(\\d{2})[ \\.:](\\d{2})\\s+([ap]m)";
                Pattern pattern12h = Pattern.compile(REGEX12H, Pattern.CASE_INSENSITIVE);
                Matcher matcher12h = pattern12h.matcher(input);
                if (matcher12h.find()) {
                    hours = Integer.parseInt(matcher12h.group(1));
                    minutes = Integer.parseInt(matcher12h.group(2));
                    if (hours == 12) hours = 0;
                    if (matcher12h.group(3).equalsIgnoreCase("pm")) {
                        hours += 12;
                    }
                    if ((hours > 23) || (minutes > 59)) {
                        throw new IllegalArgumentException("invalid");
                    }
                } else {
                    throw new IllegalArgumentException("invalid");
                }
                time.setHours(hours);
                time.setMinutes(minutes);
                break;

            case FORMATDEC:
                try {
                    int value = Integer.parseInt(input);
                    int hour = value / 100;
                    int minute = value % 100;

                    time.setHours(hour);
                    time.setMinutes(minute);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
                break;
        }

        return time;
    }
}
