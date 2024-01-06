/*
 * $Id$
 */

package edu.jas.application;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Arrays;

import edu.jas.gb.SolvableGroebnerBase;
import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseParallel;
import edu.jas.gb.SolvableGroebnerBaseSeq;
import edu.jas.gb.SolvableGroebnerBaseSeqPairParallel;
import edu.jas.gb.SolvableReduction;
import edu.jas.gb.SolvableReductionPar;
import edu.jas.gb.SolvableReductionSeq;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.util.CatReader;


/**
 * Simple setup to run a solvable GB example. <br> Usage: RunSGB
 * [seq|par|par+] [irr|left|right|two] &lt;file&gt; #procs
 * @author Heinz Kredel
 */
public class RunSGB {

    /**
     * Check result GB if it is a GB.
     */
    static boolean doCheck = false;


    /**
     * main method to be called from commandline <br> Usage: RunSGB
     * [seq|seq+|par|par+] [irr|left|right|two] &lt;file&gt; #procs
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        String[] allkinds = new String[] { "seq", "seq+", 
                                           "par", "par+", 
                                           //"dist", "dist+", ,
                                           //"disthyb", "disthyb+", 
                                           //"cli" 
                                         }; // must be last
        String[] allmeth = new String[] { "irr", "left", "right", "two" };

        String usage = "Usage: RunSGB [ "
                        + join(allkinds, " | ") 
                        //+ "[port] ] " 
                        + " ] ["
                        + join(allmeth, " | ") 
                        + "] <file> " 
                        + "#threads " 
                        //+ "#procs/#threadsPerNode " 
                        //+ "[machinefile] ";
                        + "[check] ";

        if (args.length < 3) {
            System.out.println("args: " + Arrays.toString(args));
            System.out.println(usage);
            return;
        }

        boolean plusextra = false;
        String kind = args[0];
        boolean sup = false;
        int k = -1;
        for (int i = 0; i < args.length; i++) {
            int j = indexOf(allkinds, args[i]);
            if (j < 0) {
                continue;
            }
            sup = true;
            k = i;
            kind = args[k];
            break;
        }
        if (!sup) {
            System.out.println("args(sup): " + Arrays.toString(args));
            System.out.println(usage);
            return;
        }
        if (kind.indexOf("+") >= 0) {
            plusextra = true;
        }
        System.out.println("kind: " + kind + ", k = " + k);

        String action = args[k + 1];
        sup = false;
        int j = indexOf(allmeth, action);
        if (j < 0) {
            System.out.println(usage);
            return;
        }

        String filename = args[k + 2];

        int threads = 0;
        if (kind.startsWith("par")) {
            if (args.length < 4) {
                System.out.println("args(par): " + Arrays.toString(args));
                System.out.println(usage);
                return;
            }
            String tup = args[k + 3];
            String t = tup;
            try {
                threads = Integer.parseInt(t);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("args(threads): " + Arrays.toString(args));
                System.out.println(usage);
                return;
            }
            if (threads < 1) {
                threads = 1;
            }
        }
        j = indexOf(args, "check");
        if (j >= 0) {
            doCheck = true;
        }

        Reader problem = RunGB.getReader(filename);
        if (problem == null) {
            System.out.println("args(file): " + filename);
            System.out.println("args(file): examples.jar(" + filename + ")");
            System.out.println("args(file): " + Arrays.toString(args));
            System.out.println(usage);
            return;
        }
        RingFactoryTokenizer rftok = new RingFactoryTokenizer(problem);
        GenSolvablePolynomialRing spfac = null;
        try {
            spfac = rftok.nextSolvablePolynomialRing();
            rftok = null;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Reader polyreader = new CatReader(new StringReader("("),problem); // ( has gone
        //Reader polyreader = problem; 
        GenPolynomialTokenizer tok = new GenPolynomialTokenizer(spfac,polyreader);
        PolynomialList S = null;
        try {
            S = new PolynomialList(spfac,tok.nextSolvablePolynomialList());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("S =\n" + S);

        if (kind.startsWith("seq")) {
            runSequential(S, action, plusextra);
        } else if (kind.startsWith("par")) {
            runParallel(S, threads, action, plusextra);
        }
        ComputerThreads.terminate();
        try {
            problem.close();
        } catch (IOException ignored) {
        }
    }


    /**
     * run Sequential.
     * @param S polynomial list.
     * @param action what to to.
     */
    @SuppressWarnings("unchecked")
    static void runSequential(PolynomialList S, String action, boolean plusextra) {
        List<GenSolvablePolynomial> L = S.list;
        List<GenSolvablePolynomial> G = null;
        long t;
        SolvableReduction sred = new SolvableReductionSeq();
        SolvableGroebnerBase sbb = null;
        if (plusextra) {
            //sbb = new SolvableGroebnerBaseSeqPlusextra();
            //System.out.println("SolvableGroebnerBaseSeqPlusextra not implemented using SolvableGroebnerBaseSeq");
            sbb = new SolvableGroebnerBaseSeq(sred);
        } else {
            sbb = new SolvableGroebnerBaseSeq();
        }
        t = System.currentTimeMillis();
        System.out.println("\nSolvable GB [" + action + "] sequential ...");
        if (action.equals("irr")) {
            G = sred.leftIrreducibleSet(L);
        }
        if (action.equals("left")) {
            G = sbb.leftGB(L);
        }
        if (action.equals("right")) {
            G = sbb.rightGB(L);
        }
        if (action.equals("two")) {
            G = sbb.twosidedGB(L);
        }
        if (G == null) {
            System.out.println("unknown action = " + action + "\n");
            return;
        }
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (plusextra) {
            System.out.print("seq+, ");
        } else {
            System.out.print("seq, ");
        }
        System.out.println("time = " + t + " milliseconds");
        checkGB(S);
        System.out.println("");
    }


    /**
     * run Parallel.
     * @param S polynomial list.
     * @param action what to to.
     */
    @SuppressWarnings("unchecked")
    static void runParallel(PolynomialList S, int threads, String action, boolean plusextra) {
        List<GenSolvablePolynomial> L = S.list;
        List<GenSolvablePolynomial> G = null;
        long t;
        SolvableReduction sred = new SolvableReductionPar();
        SolvableGroebnerBaseParallel sbb = null;
        SolvableGroebnerBaseSeqPairParallel sbbs = null;
        if (plusextra) {
            sbbs = new SolvableGroebnerBaseSeqPairParallel(threads);
        } else {
            sbb = new SolvableGroebnerBaseParallel(threads);
        }

        t = System.currentTimeMillis();
        System.out.println("\nSolvable GB [" + action + "] parallel " + threads + " threads ...");
        if (action.equals("irr")) {
            G = sred.leftIrreducibleSet(L);
        }
        if (action.equals("left")) {
            if (plusextra) {
                G = sbbs.leftGB(L);
            } else {
                G = sbb.leftGB(L);
            }
        }
        if (action.equals("right")) {
            if (plusextra) {
                G = sbbs.rightGB(L);
            } else {
                G = sbb.rightGB(L);
            }
        }
        if (action.equals("two")) {
            if (plusextra) {
                G = sbbs.twosidedGB(L);
            } else {
                G = sbb.twosidedGB(L);
            }
        }
        if (G == null) {
            System.out.println("unknown action = " + action + "\n");
            return;
        }
        if (G.size() > 0) {
            S = new PolynomialList(G.get(0).ring, G);
        } else {
            S = new PolynomialList(S.ring, G);
        }
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (plusextra) {
            System.out.print("p+ ");
        } else {
            System.out.print("p ");
        }
        System.out.println("= " + threads + ", time = " + t + " milliseconds");
        checkGB(S);
        System.out.println("");
        if (plusextra) {
            sbbs.terminate();
        } else {
            sbb.terminate();
        }
    }


    @SuppressWarnings("unchecked")
    static void checkGB(PolynomialList S) {
        if (!doCheck) {
            return;
        }
        SolvableGroebnerBaseAbstract sbb = new SolvableGroebnerBaseSeq();
        long t = System.currentTimeMillis();
        boolean chk = sbb.isLeftGB(S.list,false);
        t = System.currentTimeMillis() - t;
        System.out.println("check isGB = " + chk + " in " + t + " milliseconds");
    }


    static int indexOf(String[] args, String s) {
        for (int i = 0; i < args.length; i++) {
            if (s.equals(args[i])) {
                return i;
            }
        }
        return -1;
    }


    static String join(String[] args, String d) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(d);
            }
            sb.append(args[i]);
        }
        return sb.toString();
    }

}
