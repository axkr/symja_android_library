/*
 * $Id$
 */

package edu.jas.application;


import java.util.Arrays;


/**
 * Examples for Integer Programming.
 * @author Maximilian Nohr
 */
public class IntegerProgramExamples {


    /**
     * Execute all examples.
     * @param args
     */
    public static void main(String[] args) {
        example1();
        example2();
        example3();
        example4();
        example5();
        example6();
        example7();
        example8();
        // too big: example9();
        // too big: example10();
    }


    /**
     * Example p.360 CLOII
     */
    public static void example1() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //bsp. s.360 CLOII
        long[][] A0 = { { 4, 5, 1, 0 }, { 2, 3, 0, 1 } };
        long[] B0 = { 37, 20 };
        long[] C0 = { -11, -15, 0, 0 };

        long[] sol = IP.solve(A0, B0, C0);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        System.out.println("\n" + IP);
        System.out.println("The solution is: " + Arrays.toString(sol));
        System.out.println("The computation needed " + t + " milliseconds.");


        long[] BW = { 1, 2 }; //,3};

        sol = IP.solve(BW);

        int count = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                B0[0] = i;
                B0[1] = j;

                sol = IP.solve(A0, B0, C0);
                if (IP.getSuccess()) {
                    count++;
                }
            }
        }
        System.out.println(count + " times successful!");
    }


    /**
     * Example p.374 CLOII 10a
     */
    public static void example2() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //bsp. s.374 CLOII 10a
        long[][] A = { { 3, 2, 1, 1 }, { 4, 1, 1, 0 } };
        long[] B = { 10, 5 };
        long[] C = { 2, 3, 1, 5 };

        long[] sol = IP.solve(A, B, C);

        //10b
        long[] Bb = { 20, 14 };
        sol = IP.solve(Bb);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        System.out.println("\n" + IP);
        System.out.println("The solution is: " + Arrays.toString(sol));
        System.out.println("The computation needed " + t + " milliseconds.");
    }


    /**
     * Example p.372 CLOII
     */
    public static void example3() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //bsp. s.372 CLOII
        long[][] A2 = { { 3, -2, 1, 0 }, { 4, 1, -1, -1 } };
        long[] B2 = { -1, 5 };
        long[] C2 = { 1, 1000, 1, 100 };

        long[] sol = IP.solve(A2, B2, C2);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        System.out.println("\n" + IP);
        System.out.println("The solution is: " + Arrays.toString(sol));
        System.out.println("The computation needed " + t + " milliseconds.");
    }


    public static void example4() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //bsp. s.374 10c
        long[][] A3 = { { 3, 2, 1, 1, 0, 0 }, { 1, 2, 3, 0, 1, 0 }, { 2, 1, 1, 0, 0, 1 } };
        long[] B3 = { 45, 21, 18 };
        long[] C3 = { -3, -4, -2, 0, 0, 0 };

        long[] sol = IP.solve(A3, B3, C3);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        System.out.println("\n" + IP);
        System.out.println("The solution is: " + Arrays.toString(sol));
        System.out.println("The computation needed " + t + " milliseconds.");
    }


    /**
     * Example p.138 AAECC-9
     */
    public static void example5() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //bsp. s.138 AAECC-9
        long[][] A4 = { { 32, 45, -41, 22 }, { -82, -13, 33, -33 }, { 23, -21, 12, -12 } };
        long[] B4 = { 214, 712, 331 }; //im Beispiel keine b genannt
        long[] C4 = { 1, 1, 1, 1 };

        long[] sol = IP.solve(A4, B4, C4);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        System.out.println("\n" + IP);
        System.out.println("The solution is: " + Arrays.toString(sol));
        System.out.println("The computation needed " + t + " milliseconds.");
    }


    /**
     * Example from M. Nohr
     */
    public static void example6() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //eigenes beispiel
        //System.out.println("example from mnohr:");
        long[][] A5 = { { 4, 3, 1, 0 }, { 3, 1, 0, 1 } };
        long[] B5 = { 200, 100 };
        long[] C5 = { -5, -4, 0, 0 };

        long[] sol = IP.solve(A5, B5, C5);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        System.out.println("\n" + IP);
        System.out.println("The solution is: " + Arrays.toString(sol));
        System.out.println("The computation needed " + t + " milliseconds.");
    }


    /**
     * Example unsolvable
     */
    public static void example7() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        long[][] A9 = { { 1, 1, 1, 1 }, { -1, -1, -1, -1 } };
        long[] B9 = { 1, 1 };
        long[] C9 = { 1, 1, 0, 0 };

        long[] sol = IP.solve(A9, B9, C9);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        System.out.println("\nunsolvable: " + IP);
        System.out.println("The solution is: " + Arrays.toString(sol));
        System.out.println("The computation needed " + t + " milliseconds.");
    }


    /**
     * Example ?
     */
    public static void example8() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        long[][] A8 = { { 4, 3, 1, 0 }, { 3, 1, 0, 1 } };
        long[] B8 = { 200, 100 };
        long[] C8 = { -5, -4, 0, 0 };

        long[] sol = IP.solve(A8, B8, C8);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        System.out.println("\n" + IP);
        System.out.println("The solution is: " + Arrays.toString(sol));
        System.out.println("The computation needed " + t + " milliseconds.");
    }


    /**
     * Example p.137 AAECC-9, too many vars
     */
    public static void example9() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        // bsp s.137 AAECC-9 auch too many vars
        long[][] A7 = { { 2, 5, -3, 1, -2 }, { 1, 7, 2, 3, 1 }, { 4, -2, -1, -5, 3 } };
        long[] B7 = { 214, 712, 331 };
        long[] C7 = { 1, 1, 1, 1, 1 };

        long[] sol = IP.solve(A7, B7, C7);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        System.out.println("\n" + IP);
        System.out.println("The solution is: " + Arrays.toString(sol));
        System.out.println("The computation needed " + t + " milliseconds.");
    }


    /**
     * Example, too big
     */
    public static void example10() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();
        //too many variables
        long[][] A6 = { { 5, 11, 23, -5, 4, -7, -4 }, { 1, -5, -14, 15, 7, -8, 10 },
                { 3, 21, -12, 7, 9, 11, 8 } };
        long[] B6 = { 23, 19, 31 };
        long[] C6 = { 1, 1, 1, 1, 1, 1, 1 };

        long[] sol = IP.solve(A6, B6, C6);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        System.out.println("\n" + IP);
        System.out.println("The solution is: " + Arrays.toString(sol));
        System.out.println("The computation needed " + t + " milliseconds.");
    }

}
