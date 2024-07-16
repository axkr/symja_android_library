package org.matheclipse.core.decisiontree;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C2Pi;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CN3;
import static org.matheclipse.core.expression.F.CSqrtPi;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.SphericalHarmonicY;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.ZZ;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.t_;
import static org.matheclipse.core.expression.S.Pi;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.t;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pattern;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.GenericPair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherAndEvaluator;

public class DecisionTree {
  Map<Integer, TreeSet<DiscriminationNode>> treeMap = new TreeMap<>();

  public Set<Entry<Integer, TreeSet<DiscriminationNode>>> entrySet() {
    return treeMap.entrySet();
  }

  public TreeSet<DiscriminationNode> get(int key) {
    return treeMap.get(key);
  }

  public DecisionTree() {}

  public Set<DiscriminationNode> put(Integer key, TreeSet<DiscriminationNode> value) {
    return treeMap.put(key, value);
  }

  public int size() {
    return treeMap.size();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();

    for (Map.Entry<Integer, TreeSet<DiscriminationNode>> entry : treeMap.entrySet()) {
      Integer key = entry.getKey();
      Set<DiscriminationNode> set = entry.getValue();
      for (DiscriminationNode val : set) {
        buf.append(key.toString());
        buf.append(" -> [");
        buf.append(val.expr().toString());
        buf.append(", ");
        DecisionTree net = val.decisionTree();
        if (net != null) {
          buf.append("\n");
          buf.append(net.toString());
        }
        buf.append(",");
        List<IPatternMatcher> downRules = val.downRules();
        if (downRules != null) {
          buf.append(downRules.toString());
        }
        buf.append("] | ");
      }

    }
    return buf.toString();
  }

  public final static List<IPatternMatcher> putDownRule(final int setSymbol,
      final IExpr leftHandSide, final IExpr rightHandSide, List<IPatternMatcher> pmList) {
    final PatternMatcherAndEvaluator pmEvaluator =
        new PatternMatcherAndEvaluator(setSymbol, leftHandSide, rightHandSide, false, 0);
    pmList.add(pmEvaluator);
    return pmList;
  }

  private static boolean insertRule(DecisionTree[] dts, IAST lhs, IExpr rhs) {
    if (!lhs.forAll(x -> isCompilableRule(x), 0)) {
      return false;
    }
    if (lhs.forAll(x -> x.isFreeOfPatterns(), 0)) {
      // "equals" rules don't need a decision tree
      return false;
    }
    DecisionTree net = dts[lhs.size()];

    DiscriminationNode node = null;
    for (int i = 0; i < lhs.size(); i++) {
      IExpr arg = lhs.get(i);
      if (arg.isFreeOfPatterns()) {
        if (node != null) {
          DecisionTree subNet = node.decisionTree();
          if (subNet == null) {
            subNet = new DecisionTree();
            node.decisionTree = subNet;
            net = subNet;
          }
        }
        TreeSet<DiscriminationNode> set = net.get(i);
        if (set == null) {
          set = new TreeSet<DiscriminationNode>();
          node = new DiscriminationNode(arg, null, null);
          set.add(node);
          net.put(i, set);
        } else {
          node = new DiscriminationNode(arg, null, null);
          DiscriminationNode floor = set.floor(node);
          if (floor != null && floor.equals(node)) {
            node = floor;
            net = node.decisionTree;
          } else {
            set.add(node);
          }
        }
      }
    }
    for (int i = 0; i < lhs.size(); i++) {
      IExpr arg = lhs.get(i);
      if (arg.isPattern()) {
        Pattern p = (Pattern) arg;
        if (node != null) {
          DecisionTree subNet = node.decisionTree();
          if (subNet == null) {
            subNet = new DecisionTree();
            node.decisionTree = subNet;
            net = subNet;
          }
        }
        TreeSet<DiscriminationNode> set = net.get(i);
        if (set == null) {
          set = new TreeSet<DiscriminationNode>();
          node = new DiscriminationNode(p, null, null);
          set.add(node);
          net.put(i, set);
        } else {
          node = new DiscriminationNode(p, null, null);
          DiscriminationNode floor = set.floor(node);
          if (floor != null && floor.equals(node)) {
            node = floor;
            net = node.decisionTree;
          } else {
            set.add(node);
          }
        }
      } else if (arg.isAST(S.PatternTest, 3)) {
        IAST patternTest = (IAST) arg;
        // Pattern p = (Pattern) patternTest.arg1();
        // IExpr testExpr = patternTest.arg2();
        if (node != null) {
          DecisionTree subNet = node.decisionTree();
          if (subNet == null) {
            subNet = new DecisionTree();
            node.decisionTree = subNet;
            net = subNet;
          }
        }
        TreeSet<DiscriminationNode> set = net.get(i);
        if (set == null) {
          set = new TreeSet<DiscriminationNode>();
          node = new DiscriminationNode(patternTest, null, null);
          set.add(node);
          net.put(i, set);
        } else {
          node = new DiscriminationNode(patternTest, null, null);
          DiscriminationNode floor = set.floor(node);
          if (floor != null && floor.equals(node)) {
            node = floor;
            net = node.decisionTree;
          } else {
            set.add(node);
          }
        }
      }
    }
    if (node != null) {
      List<IPatternMatcher> downRules = node.downRules();
      if (downRules == null) {
        node.patternDownRules = new ArrayList<IPatternMatcher>();
      }
      PatternMatcherAndEvaluator pm = new PatternMatcherAndEvaluator(lhs, rhs);
      node.patternDownRules.add(pm);
    }
    return true;
  }

  public static boolean isCompilableRule(IExpr x) {
    if (x.isFreeOfPatterns()) {
      return true;
    }
    if (x.isPattern() && !x.isPatternDefault()) {
      return true;
    }
    if (x.isAST(S.PatternTest, 3) && x.first().isPattern()) {
      IPattern pattern = (IPattern) x.first();
      IExpr patternTest = x.second();
      if (patternTest.isFreeOfPatterns() && !pattern.isPatternDefault()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Experimental. Don't use it.
   * 
   * @param dn
   * @param evalLHS
   * @return
   * @deprecated
   */
  @Deprecated
  private static IExpr matchLHSRecursive(DecisionTree dn, IAST evalLHS) {
    DecisionTree net = dn;
    DiscriminationNode node = null;
    for (Map.Entry<Integer, TreeSet<DiscriminationNode>> entry : net.entrySet()) {
      Integer key = entry.getKey();
      TreeSet<DiscriminationNode> set = entry.getValue();
      IExpr arg = evalLHS.get(key);
      node = new DiscriminationNode(arg, null, null);
      DiscriminationNode floor = set.floor(node);
      if (node.equals(floor)) {
        List<IPatternMatcher> downRules = floor.downRules();
        if (downRules != null) {
          // match downRules
          for (int i = 0; i < downRules.size(); i++) {
            IPatternMatcher patternMatcher = downRules.get(i);
            IExpr result = patternMatcher.eval(evalLHS, EvalEngine.get());
            if (result.isPresent()) {
              return result;
            }
          }
        }
        DecisionTree subNet = floor.decisionTree();
        if (subNet != null) {
          IExpr temp = matchLHSRecursive(subNet, evalLHS);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
    }
    return F.NIL;
  }

  private static CharSequence toJava(IExpr expr) {
    return expr.internalJavaString(SourceCodeProperties.JAVA_FORM_PROPERTIES_NO_SYMBOL_PREFIX, 0,
        z -> null);
  }

  private static IExpr toJavaMethodRecursive(DecisionTree dn, StringBuilder buf,
      List<GenericPair<ISymbol, String>> patternIndexMap) {
    DecisionTree net = dn;
    boolean patternEval = false;
    for (Map.Entry<Integer, TreeSet<DiscriminationNode>> entry : net.entrySet()) {
      int index = entry.getKey();
      String arg = EvalEngine.uniqueName("a");
      buf.append("IExpr " + arg + " = evalLHS.get(" + index + ");\n");
      TreeSet<DiscriminationNode> set = entry.getValue();
      for (DiscriminationNode node : set) {

        String x = EvalEngine.uniqueName("x");
        IExpr expr = node.expr();
        CharSequence patternValueVar = null;
        ISymbol patternSymbol = null;
        IExpr exprTest = null;
        IPattern pattern = null;
        if (expr.isPattern()) {
          patternEval = true;
          pattern = (IPattern) expr;
          patternSymbol = pattern.getSymbol();
          for (int i = 0; i < patternIndexMap.size(); i++) {
            GenericPair<ISymbol, String> pair = patternIndexMap.get(i);
            if (pair.getFirst().equals(patternSymbol)) {
              patternValueVar = pair.getSecond();
              break;
            }
          }
        } else if (expr.isAST(S.PatternTest, 3)) {
          IAST patternTest = (IAST) expr;
          patternEval = true;
          pattern = (IPattern) patternTest.arg1();
          exprTest = patternTest.arg2();
          patternSymbol = pattern.getSymbol();
          for (int i = 0; i < patternIndexMap.size(); i++) {
            GenericPair<ISymbol, String> pair = patternIndexMap.get(i);
            if (pair.getFirst().equals(patternSymbol)) {
              patternValueVar = pair.getSecond();
              break;
            }
          }
        } else {
          patternValueVar = toJava(expr);
        }
        try {
          if (patternValueVar == null) {
            buf.append("IPattern " + x + " = (IPattern)" + toJava(pattern) + ";\n");
            if (exprTest == null) {
              buf.append("if (" + x + ".isConditionMatched(" //
                  + arg + ",null)) {\n");
            } else {
              String t = EvalEngine.uniqueName("t");
              buf.append("IExpr " + t + " = " + toJava(exprTest) + ";\n");
              buf.append("if (engine.evalTrue(" + t + "," + arg + ") &&" //
                  + x + ".isConditionMatched(" //
                  + arg + ",null)) {\n");
            }
            buf.append("patternIndexMap.push(new GenericPair<IExpr, ISymbol>(" + arg + ", " + x
                + ".getSymbol()));\n");
            buf.append("try {\n");
            patternIndexMap.add(new GenericPair<ISymbol, String>(patternSymbol, arg));
          } else {
            buf.append("IExpr " + x + " = " + patternValueVar + ";\n");
            buf.append("if (" + x + ".equals(" + arg + ")) {\n");
          }
          List<IPatternMatcher> downRules = node.downRules();
          if (downRules != null) {
            // match downRules
            for (int i = 0; i < downRules.size(); i++) {
              IPatternMatcher pm = downRules.get(i);
              if (patternEval) {
                buf.append("    // "
                    + pm.getLHS().internalJavaString(RulesToDecisionTree.SCP, 1, fn -> null)
                    + " :=\n");
                buf.append("        // "
                    + pm.getRHS().internalJavaString(RulesToDecisionTree.SCP, 1, fn -> null)
                    + "\n");
                buf.append("result = PatternMatcherAndEvaluator.evalInternal(evalLHS,"
                    + toJava(pm.getRHS()) + ", patternIndexMap );\n");

              } else {

                buf.append("pm = new PatternMatcherAndEvaluator(" + toJava(pm.getLHS()) + ","
                    + toJava(pm.getRHS()) + ");\n");
                buf.append("result = pm.eval(evalLHS, engine);\n");

              }

              buf.append("if (result.isPresent()) { return result; }\n");
            }
          }
          DecisionTree subTree = node.decisionTree();
          if (subTree != null) {
            IExpr temp = toJavaMethodRecursive(subTree, buf, patternIndexMap);
            if (temp.isPresent()) {
              return temp;
            }
          }
          if (patternValueVar == null) {
            buf.append("} finally { patternIndexMap.pop(); }\n");
          }
        } finally {
          if (patternValueVar == null) {
            patternIndexMap.remove(patternIndexMap.size() - 1);
          }
        }
        buf.append("\n}\n");
      }

    }

    return F.NIL;

  }


  public static void main(String[] args) {
    F.initSymbols();
    TreeMap<String, String> ts;

    List<IPatternMatcher> pmList = new ArrayList<IPatternMatcher>();
    putDownRule(IPatternMatcher.SET_DELAYED, F.Beta(F.C0, F.C1, F.a_, F.b_), F.List(F.a, F.b),
        pmList);

    // SphericalHarmonicY(0, 0, t_, p_) = 1/(2*Sqrt(Pi)),
    IAST lhs1 = SphericalHarmonicY(C0, C0, t_, p_);
    IExpr rhs1 = Times(C1D2, Power(Pi, CN1D2));
    // SphericalHarmonicY(1, -1, t_, p_) := ((1/2)*Sqrt(3/(2*Pi))*Sin(t))/E^(I*p),
    IAST lhs2 = SphericalHarmonicY(C1, CN1, t_, p_);
    IExpr rhs2 =
        Times(C1D2, Power(Exp(Times(CI, p)), CN1), Sqrt(Times(C3, Power(C2Pi, CN1))), Sin(t));
    // SphericalHarmonicY(1, 1, t_, p_) := (-1/2)*E^(I*p)*Sqrt(3/(2*Pi))*Sin(t),
    IAST lhs3 = SphericalHarmonicY(C1, C1, t_, F.PatternTest(p_, S.IntegerQ));
    IExpr rhs3 = Times(CN1D2, Exp(Times(CI, p)), Sqrt(Times(C3, Power(C2Pi, CN1))), Sin(t));
    IAST lhs3a = SphericalHarmonicY(C1, C3, t_, t_);
    IExpr rhs3a = Times(CN1D2, Exp(Times(CI, t)), Sqrt(Times(C3, Power(C2Pi, CN1))), Sin(t));
    // SphericalHarmonicY(n_, 0, 0, p_) := Sqrt(1 + 2*n)/(2*Sqrt(Pi)),
    IAST lhs4 = SphericalHarmonicY(n_, C0, C0, p_);
    IExpr rhs4 = Times(Sqrt(Plus(C1, Times(C2, n))), Power(Times(C2, CSqrtPi), CN1));
    // SphericalHarmonicY(2, -2, t_, p_) := ((1/4)*Sqrt(15/(2*Pi))*Sin(t)^2)/E^(2*I*p),
    IAST lhs5 = SphericalHarmonicY(C2, CN2, t_, p_);
    IExpr rhs5 = Times(C1D4, Power(Exp(Times(C2, CI, p)), CN1),
        Sqrt(Times(ZZ(15L), Power(C2Pi, CN1))), Sqr(Sin(t)));
    // SphericalHarmonicY(2, -1, t_, p_) := ((1/2)*Sqrt(15/(2*Pi))*Cos(t)*Sin(t))/E^(I*p);
    IAST lhs6 = SphericalHarmonicY(C3, CN3, t_, p_);
    IExpr rhs6 = Times(QQ(1L, 8L), Power(Exp(Times(C3, CI, p)), CN1),
        Sqrt(Times(ZZ(35L), Power(Pi, CN1))), Power(Sin(t), C3));
    // SphericalHarmonicY(2, 0, t_, p_) := (1/4)*Sqrt(5/Pi)*(-1 + 3*Cos(t)^2),
    IAST lhs7 = SphericalHarmonicY(C3, CN2, t_, p_);
    IExpr rhs7 = Times(C1D4, Power(Exp(Times(C2, CI, p)), CN1),
        Sqrt(Times(ZZ(105L), Power(C2Pi, CN1))), Cos(t), Sqr(Sin(t)));
    // SphericalHarmonicY(3, -3, t_, p_) := ((1/8)*Sqrt(35/Pi)*Sin(t)^3)/E^(3*I*p),
    IAST lhs8 = SphericalHarmonicY(C3, CN1, t_, p_);
    IExpr rhs8 = Times(QQ(1L, 8L), Power(Exp(Times(CI, p)), CN1),
        Sqrt(Times(ZZ(21L), Power(Pi, CN1))), Plus(CN1, Times(C5, Sqr(Cos(t)))), Sin(t));
    // SphericalHarmonicY(3, -2, t_, p_) := ((1/4)*Sqrt(105/(2*Pi))*Cos(t)*Sin(t)^2)/E^(2*I*p),
    // SphericalHarmonicY(3, -1, t_, p_) := ((1/8)*Sqrt(21/Pi)*(-1 + 5*Cos(t)^2)*Sin(t))/E^(I*p),
    // SphericalHarmonicY(3, 0, t_, p_) := (1/4)*Sqrt(7/Pi)*(-3*Cos(t) + 5*Cos(t)^3),
    // SphericalHarmonicY(3, 1, t_, p_) := (-(1/8))*E^(I*p)*Sqrt(21/Pi)*(-1 + 5*Cos(t)^2)*Sin(t),
    // SphericalHarmonicY(3, 2, t_, p_) := (1/4)*E^(2*I*p)*Sqrt(105/(2*Pi))*Cos(t)*Sin(t)^2,
    // SphericalHarmonicY(3, 3, t_, p_)


    // DiscriminationNet netLevel2 = new DiscriminationNet();
    // netLevel1.put(2, new DiscriminationRecord(F.C0, netLevel2, null));
    // netLevel1.put(3, new DiscriminationRecord(F.C1, null, pmList));
    // netLevel2.put(3, new DiscriminationRecord(F.C1, null, pmList));

    DecisionTree[] dts = new DecisionTree[10];
    for (int i = 0; i < dts.length; i++) {
      dts[i] = new DecisionTree();
    }
    insertRule(dts, lhs1, rhs1);
    insertRule(dts, lhs2, rhs2);
    insertRule(dts, lhs3, rhs3);
    insertRule(dts, lhs3a, rhs3a);
    for (int i = 0; i < dts.length; i++) {
      if (dts[i].size() > 0) {
        System.out.println(dts[i].toString());
      }
    }

    IAST evalAST = SphericalHarmonicY(C1, C1, F.C10, F.ZZ(12));
    IExpr result = matchLHSRecursive(dts[evalAST.size()], evalAST);
    System.out.println("\nResult: " + result.toString());

    evalAST = SphericalHarmonicY(C1, CN1, F.C10, F.ZZ(12));
    // result = matchLHSRecursive(rootNet, evalAST);
    result = match5(evalAST, EvalEngine.get());
    System.out.println("\nResult: " + result.toString());

    StringBuilder buf = toJavaMethods(dts);

    System.out.println(buf.toString());
  }

  public static String insertRules(IASTAppendable rules) {
    DecisionTree[] dts = new DecisionTree[10];
    for (int i = 0; i < dts.length; i++) {
      dts[i] = new DecisionTree();
    }
    int i = 1;
    while (i < rules.size()) {
      IExpr setDelayed = rules.get(i);
      if (setDelayed.isAST2()) {
        IExpr lhs = ((IAST) setDelayed).arg1();
        IExpr rhs = ((IAST) setDelayed).arg2();
        if (lhs.isAST()) {
          if (insertRule(dts, (IAST) lhs, rhs)) {
            rules.remove(i);
            continue;
          }
        }
      }
      i++;
    }
    // for (int i = 0; i < dts.length; i++) {
    // if (dts[i].size() > 0) {
    // // System.out.println(dts[i].toString());
    // }
    // }
    StringBuilder buf = toJavaMethods(dts);
    System.out.println(buf.toString());
    return buf.toString();
  }

  private static StringBuilder toJavaMethods(DecisionTree[] dts) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < dts.length; i++) {
      DecisionTree decisionTree = dts[i];
      if (decisionTree.size() > 0) {
        toSingleJavaMethod(decisionTree, buf, i);
      }
    }
    return buf;
  }

  private static void toSingleJavaMethod(DecisionTree decisionTree, StringBuilder buf,
      int methodNumber) {
    buf.append(
        "\n\npublic static IExpr match" + methodNumber + "(IAST evalLHS, EvalEngine engine) {\n");
    buf.append(
        "Stack<GenericPair<IExpr, ISymbol>> patternIndexMap = new Stack<GenericPair<IExpr, ISymbol>>();\n");
    // buf.append("PatternMatcherAndEvaluator pm = null;\n");
    buf.append("IExpr result = F.NIL;\n");

    List<GenericPair<ISymbol, String>> patternIndexMap =
        new ArrayList<GenericPair<ISymbol, String>>();
    toJavaMethodRecursive(decisionTree, buf, patternIndexMap);
    buf.append("return F.NIL;\n");
    buf.append("\n}\n\n");
  }

  public static IExpr match5(IAST evalLHS, EvalEngine engine) {
    Stack<GenericPair<IExpr, ISymbol>> patternIndexMap = new Stack<GenericPair<IExpr, ISymbol>>();
    IExpr result = F.NIL;
    IExpr a1 = evalLHS.get(0);
    IExpr x2 = F.SphericalHarmonicY;
    if (x2.equals(a1)) {
      IExpr a3 = evalLHS.get(1);
      IExpr x4 = F.C0;
      if (x4.equals(a3)) {
        IExpr a5 = evalLHS.get(2);
        IExpr x6 = F.C0;
        if (x6.equals(a5)) {
          IExpr a7 = evalLHS.get(3);
          IPattern x8 = F.t_;
          if (x8.isConditionMatched(a7, null)) {
            patternIndexMap.push(new GenericPair<IExpr, ISymbol>(a7, x8.getSymbol()));
            try {
              IExpr a9 = evalLHS.get(4);
              IPattern x10 = F.p_;
              if (x10.isConditionMatched(a9, null)) {
                patternIndexMap.push(new GenericPair<IExpr, ISymbol>(a9, x10.getSymbol()));
                try {
                  result = PatternMatcherAndEvaluator.evalInternal(evalLHS,
                      F.Times(F.C1D2, F.Power(F.Pi, F.CN1D2)), patternIndexMap);
                  if (result.isPresent()) {
                    return result;
                  }
                } finally {
                  patternIndexMap.pop();
                }

              }
            } finally {
              patternIndexMap.pop();
            }

          }

        }

      }
      IExpr x11 = F.C1;
      if (x11.equals(a3)) {
        IExpr a12 = evalLHS.get(2);
        IExpr x13 = F.CN1;
        if (x13.equals(a12)) {
          IExpr a14 = evalLHS.get(3);
          IPattern x15 = F.t_;
          if (x15.isConditionMatched(a14, null)) {
            patternIndexMap.push(new GenericPair<IExpr, ISymbol>(a14, x15.getSymbol()));
            try {
              IExpr a16 = evalLHS.get(4);
              IPattern x17 = F.p_;
              if (x17.isConditionMatched(a16, null)) {
                patternIndexMap.push(new GenericPair<IExpr, ISymbol>(a16, x17.getSymbol()));
                try {
                  result = PatternMatcherAndEvaluator.evalInternal(evalLHS,
                      F.Times(F.C1D2, F.Power(F.Exp(F.Times(F.CI, p)), F.CN1),
                          F.Sqrt(F.Times(F.C3, F.Power(F.C2Pi, F.CN1))), F.Sin(t)),
                      patternIndexMap);
                  if (result.isPresent()) {
                    return result;
                  }
                } finally {
                  patternIndexMap.pop();
                }

              }
            } finally {
              patternIndexMap.pop();
            }

          }

        }
        IExpr x18 = F.C1;
        if (x18.equals(a12)) {
          IExpr a19 = evalLHS.get(3);
          IPattern x20 = F.t_;
          if (x20.isConditionMatched(a19, null)) {
            patternIndexMap.push(new GenericPair<IExpr, ISymbol>(a19, x20.getSymbol()));
            try {
              IExpr a21 = evalLHS.get(4);
              IPattern x22 = F.p_;
              IExpr t23 = F.IntegerQ;
              if (engine.evalTrue(t23, a21) && x22.isConditionMatched(a21, null)) {
                patternIndexMap.push(new GenericPair<IExpr, ISymbol>(a21, x22.getSymbol()));
                try {
                  result = PatternMatcherAndEvaluator.evalInternal(evalLHS,
                      F.Times(F.CN1D2, F.Exp(F.Times(F.CI, p)),
                          F.Sqrt(F.Times(F.C3, F.Power(F.C2Pi, F.CN1))), F.Sin(t)),
                      patternIndexMap);
                  if (result.isPresent()) {
                    return result;
                  }
                } finally {
                  patternIndexMap.pop();
                }

              }
            } finally {
              patternIndexMap.pop();
            }

          }

        }
        IExpr x24 = F.C3;
        if (x24.equals(a12)) {
          IExpr a25 = evalLHS.get(3);
          IPattern x26 = F.t_;
          if (x26.isConditionMatched(a25, null)) {
            patternIndexMap.push(new GenericPair<IExpr, ISymbol>(a25, x26.getSymbol()));
            try {
              IExpr a27 = evalLHS.get(4);
              IExpr x28 = a25;
              if (x28.equals(a27)) {
                result =
                    PatternMatcherAndEvaluator.evalInternal(evalLHS,
                        F.Times(F.CN1D2, F.Exp(F.Times(F.CI, t)),
                            F.Sqrt(F.Times(F.C3, F.Power(F.C2Pi, F.CN1))), F.Sin(t)),
                        patternIndexMap);
                if (result.isPresent()) {
                  return result;
                }

              }
            } finally {
              patternIndexMap.pop();
            }

          }

        }

      }

    }
    return F.NIL;

  }
}
