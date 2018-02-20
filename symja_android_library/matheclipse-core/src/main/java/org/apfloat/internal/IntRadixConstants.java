package org.apfloat.internal;

/**
 * Constants related to different radixes for the <code>int</code> data type.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public interface IntRadixConstants
{
    /**
     * Bases for radixes 2, ..., 36. The base is the radix to the maximum power
     * so that the base is less than all moduli used.
     */

    public static final int BASE[] = { (int) -1L, (int) -1L, (int) 1073741824L, (int) 1162261467L, (int) 1073741824L, (int) 1220703125L, (int) 362797056L, (int) 282475249L, (int) 1073741824L, (int) 387420489L, (int) 1000000000L, (int) 214358881L, (int) 429981696L, (int) 815730721L, (int) 1475789056L, (int) 170859375L, (int) 268435456L, (int) 410338673L, (int) 612220032L, (int) 893871739L, (int) 1280000000L, (int) 1801088541L, (int) 113379904L, (int) 148035889L, (int) 191102976L, (int) 244140625L, (int) 308915776L, (int) 387420489L, (int) 481890304L, (int) 594823321L, (int) 729000000L, (int) 887503681L, (int) 1073741824L, (int) 1291467969L, (int) 1544804416L, (int) 52521875L, (int) 60466176L };

    /**
     * The power of the radix in each base.
     */

    public static final int BASE_DIGITS[] = { -1, -1, 30, 19, 15, 13, 11, 10, 10, 9, 9, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5, 5 };

    /**
     * The minimum number in each radix to have the specified amount of digits.
     */

    public static final int MINIMUM_FOR_DIGITS[][] = { null, null, { (int) 1L, (int) 2L, (int) 4L, (int) 8L, (int) 16L, (int) 32L, (int) 64L, (int) 128L, (int) 256L, (int) 512L, (int) 1024L, (int) 2048L, (int) 4096L, (int) 8192L, (int) 16384L, (int) 32768L, (int) 65536L, (int) 131072L, (int) 262144L, (int) 524288L, (int) 1048576L, (int) 2097152L, (int) 4194304L, (int) 8388608L, (int) 16777216L, (int) 33554432L, (int) 67108864L, (int) 134217728L, (int) 268435456L, (int) 536870912L }, { (int) 1L, (int) 3L, (int) 9L, (int) 27L, (int) 81L, (int) 243L, (int) 729L, (int) 2187L, (int) 6561L, (int) 19683L, (int) 59049L, (int) 177147L, (int) 531441L, (int) 1594323L, (int) 4782969L, (int) 14348907L, (int) 43046721L, (int) 129140163L, (int) 387420489L }, { (int) 1L, (int) 4L, (int) 16L, (int) 64L, (int) 256L, (int) 1024L, (int) 4096L, (int) 16384L, (int) 65536L, (int) 262144L, (int) 1048576L, (int) 4194304L, (int) 16777216L, (int) 67108864L, (int) 268435456L }, { (int) 1L, (int) 5L, (int) 25L, (int) 125L, (int) 625L, (int) 3125L, (int) 15625L, (int) 78125L, (int) 390625L, (int) 1953125L, (int) 9765625L, (int) 48828125L, (int) 244140625L }, { (int) 1L, (int) 6L, (int) 36L, (int) 216L, (int) 1296L, (int) 7776L, (int) 46656L, (int) 279936L, (int) 1679616L, (int) 10077696L, (int) 60466176L }, { (int) 1L, (int) 7L, (int) 49L, (int) 343L, (int) 2401L, (int) 16807L, (int) 117649L, (int) 823543L, (int) 5764801L, (int) 40353607L }, { (int) 1L, (int) 8L, (int) 64L, (int) 512L, (int) 4096L, (int) 32768L, (int) 262144L, (int) 2097152L, (int) 16777216L, (int) 134217728L }, { (int) 1L, (int) 9L, (int) 81L, (int) 729L, (int) 6561L, (int) 59049L, (int) 531441L, (int) 4782969L, (int) 43046721L }, { (int) 1L, (int) 10L, (int) 100L, (int) 1000L, (int) 10000L, (int) 100000L, (int) 1000000L, (int) 10000000L, (int) 100000000L }, { (int) 1L, (int) 11L, (int) 121L, (int) 1331L, (int) 14641L, (int) 161051L, (int) 1771561L, (int) 19487171L }, { (int) 1L, (int) 12L, (int) 144L, (int) 1728L, (int) 20736L, (int) 248832L, (int) 2985984L, (int) 35831808L }, { (int) 1L, (int) 13L, (int) 169L, (int) 2197L, (int) 28561L, (int) 371293L, (int) 4826809L, (int) 62748517L }, { (int) 1L, (int) 14L, (int) 196L, (int) 2744L, (int) 38416L, (int) 537824L, (int) 7529536L, (int) 105413504L }, { (int) 1L, (int) 15L, (int) 225L, (int) 3375L, (int) 50625L, (int) 759375L, (int) 11390625L }, { (int) 1L, (int) 16L, (int) 256L, (int) 4096L, (int) 65536L, (int) 1048576L, (int) 16777216L }, { (int) 1L, (int) 17L, (int) 289L, (int) 4913L, (int) 83521L, (int) 1419857L, (int) 24137569L }, { (int) 1L, (int) 18L, (int) 324L, (int) 5832L, (int) 104976L, (int) 1889568L, (int) 34012224L }, { (int) 1L, (int) 19L, (int) 361L, (int) 6859L, (int) 130321L, (int) 2476099L, (int) 47045881L }, { (int) 1L, (int) 20L, (int) 400L, (int) 8000L, (int) 160000L, (int) 3200000L, (int) 64000000L }, { (int) 1L, (int) 21L, (int) 441L, (int) 9261L, (int) 194481L, (int) 4084101L, (int) 85766121L }, { (int) 1L, (int) 22L, (int) 484L, (int) 10648L, (int) 234256L, (int) 5153632L }, { (int) 1L, (int) 23L, (int) 529L, (int) 12167L, (int) 279841L, (int) 6436343L }, { (int) 1L, (int) 24L, (int) 576L, (int) 13824L, (int) 331776L, (int) 7962624L }, { (int) 1L, (int) 25L, (int) 625L, (int) 15625L, (int) 390625L, (int) 9765625L }, { (int) 1L, (int) 26L, (int) 676L, (int) 17576L, (int) 456976L, (int) 11881376L }, { (int) 1L, (int) 27L, (int) 729L, (int) 19683L, (int) 531441L, (int) 14348907L }, { (int) 1L, (int) 28L, (int) 784L, (int) 21952L, (int) 614656L, (int) 17210368L }, { (int) 1L, (int) 29L, (int) 841L, (int) 24389L, (int) 707281L, (int) 20511149L }, { (int) 1L, (int) 30L, (int) 900L, (int) 27000L, (int) 810000L, (int) 24300000L }, { (int) 1L, (int) 31L, (int) 961L, (int) 29791L, (int) 923521L, (int) 28629151L }, { (int) 1L, (int) 32L, (int) 1024L, (int) 32768L, (int) 1048576L, (int) 33554432L }, { (int) 1L, (int) 33L, (int) 1089L, (int) 35937L, (int) 1185921L, (int) 39135393L }, { (int) 1L, (int) 34L, (int) 1156L, (int) 39304L, (int) 1336336L, (int) 45435424L }, { (int) 1L, (int) 35L, (int) 1225L, (int) 42875L, (int) 1500625L }, { (int) 1L, (int) 36L, (int) 1296L, (int) 46656L, (int) 1679616L } };

    /**
     * Maximum allowed exponent for each radix.
     */

    public static final long MAX_EXPONENT[] = { -1L, -1L, 307445734561825854L, 485440633518672404L, 614891469123651714L, 709490156681136594L, 838488366986797794L, 922337203685477574L, 922337203685477574L, 1024819115206086194L, 1024819115206086194L, 1152921504606846969L, 1152921504606846969L, 1152921504606846969L, 1152921504606846969L, 1317624576693539395L, 1317624576693539395L, 1317624576693539395L, 1317624576693539395L, 1317624576693539395L, 1317624576693539395L, 1317624576693539395L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1844674407370955155L, 1844674407370955155L };
}
