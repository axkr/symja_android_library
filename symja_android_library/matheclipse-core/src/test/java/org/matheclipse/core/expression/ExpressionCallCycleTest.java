package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Call cycles between the classes of <code>org.matheclipse.core.expression</code> and its
 * <code>data</code> subpackage, counted the way the SpotBugs plugin fb-contrib counts them for
 * <code>FCCD_FIND_CLASS_CIRCULAR_DEPENDENCY</code>: an edge is a static, virtual or special method
 * call (constructors included) to a concrete class. Interface calls and field reads don't count.
 *
 * <p>
 * {@link F} creates almost every expression and almost every expression calls the factory methods
 * of {@link F}, so most classes of the package stay in one big cycle. This test keeps the cycles
 * which were removed from coming back.
 */
class ExpressionCallCycleTest {

  private static final String PACKAGE = "org.matheclipse.core.expression.";

  /**
   * Calls which were removed to break a cycle, as <code>caller -> callee</code> without the package
   * prefix.
   */
  private static final String[] REMOVED_EDGES = { //
      "AbstractAST -> AbstractAST$NILPointer", // NILPointer.INSTANCE creates NIL
      "Context -> ContextPath", // EvalEngine#setGlobalContext()
      "ExprID -> S", // ExprID#resolve() reads the tables of S
      "UniformFlags -> BuiltInSymbol", // uniformMask(IBuiltInSymbol)
      "DMath -> ApfloatNum", // NumberUtil#apfloatOf()
      "DMath -> ComplexNum", // ComplexNum#besselComplexNum()
      "RealNumberComparison -> ApfloatNum", // INum#precision()
      "WL$ReadObject -> WL", // WXFVarint
      "WL$WriteObject -> WL", //
      "WL$WriteInternalObject -> WL", //
      "data.ByteArrayExpr -> WL", //
      "WMACompress$Reader -> WMACompress", // unescape() is a method of Reader
      "IntegerSym -> F", // numericNumber() and sqrtExpr() in AbstractIntegerSym
      "BigIntegerSym -> F", //
      "FractionSym -> F", // AbstractIntegerSym#valueOf()
      "BigFractionSym -> F", //
  };

  /**
   * Classes which are in no call cycle inside the package. Unlike a count of all classes in cycles,
   * this doesn't depend on the compiler's synthetic calls and doesn't change when an unavoidable
   * cycle is added elsewhere, for example by a new nested class of {@link B2} which {@link F}
   * creates.
   */
  private static final String[] OUTSIDE_CYCLES = { //
      "AbstractAST$NILPointer", //
      "AbstractAST$NILPointer$Holder", //
      "Context", //
      "DMath", //
      "ExprID", //
      "NumberUtil", //
      "RealNumberComparison", //
      "UniformFlags", //
      "WL", //
      "WL$ReadInternalObject", //
      "WL$ReadObject", //
      "WL$WriteInternalObject", //
      "WL$WriteObject", //
      "WMACompress", //
      "WMACompress$Reader", //
      "WXFVarint", //
      "data.ByteArrayExpr", //
  };

  private static Map<String, Set<String>> graph;

  @BeforeAll
  static void readClasses() throws IOException, URISyntaxException {
    URL url = F.class.getResource("F.class");
    // a jar on the class path, as in a build which takes core from the local repository, has no
    // directory to walk
    assumeTrue(url != null && "file".equals(url.getProtocol()),
        "classes of org.matheclipse.core.expression are not in a directory: " + url);
    Path dir = Paths.get(url.toURI()).getParent();
    graph = new TreeMap<>();
    List<Path> files;
    try (Stream<Path> stream = Files.walk(dir)) {
      files = stream.filter(p -> p.toString().endsWith(".class")).sorted()
          .collect(Collectors.toList());
    }
    for (Path file : files) {
      try (InputStream in = Files.newInputStream(file)) {
        addCalls(new ClassReader(in));
      }
    }
  }

  private static void addCalls(ClassReader reader) {
    String caller = reader.getClassName().replace('/', '.');
    if (!caller.startsWith(PACKAGE)) {
      return;
    }
    Set<String> callees = graph.computeIfAbsent(shortName(caller), k -> new TreeSet<>());
    reader.accept(new ClassVisitor(Opcodes.ASM9) {
      @Override
      public MethodVisitor visitMethod(int access, String name, String descriptor,
          String signature, String[] exceptions) {
        return new MethodVisitor(Opcodes.ASM9) {
          @Override
          public void visitMethodInsn(int opcode, String owner, String methodName,
              String methodDescriptor, boolean isInterface) {
            if (opcode == Opcodes.INVOKEINTERFACE) {
              return;
            }
            String callee = owner.replace('/', '.');
            if (callee.equals(caller) || !callee.startsWith(PACKAGE)) {
              return;
            }
            // the exceptions fb-contrib makes for nested classes
            if (caller.startsWith(callee) && callee.indexOf('$') >= 0) {
              return;
            }
            if (callee.startsWith(caller) && caller.indexOf('$') >= 0) {
              return;
            }
            callees.add(shortName(callee));
          }
        };
      }
    }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
  }

  private static String shortName(String className) {
    return className.substring(PACKAGE.length());
  }

  @Test
  void testRemovedCallsStayRemoved() {
    List<String> back = new ArrayList<>();
    for (String edge : REMOVED_EDGES) {
      String[] parts = edge.split(" -> ");
      if (graph.getOrDefault(parts[0], Collections.emptySet()).contains(parts[1])) {
        back.add(edge);
      }
    }
    assertTrue(back.isEmpty(), "calls which close a cycle again: " + back);
  }

  @Test
  void testClassesStayOutsideCycles() {
    Set<String> inCycles = classesInCycles(graph);
    List<String> back = new ArrayList<>();
    for (String className : OUTSIDE_CYCLES) {
      if (inCycles.contains(className)) {
        back.add(className);
      }
    }
    assertTrue(back.isEmpty(), "classes which are part of a call cycle again: " + back);
  }

  /** The classes of the strongly connected components with more than one class (Tarjan). */
  static Set<String> classesInCycles(Map<String, Set<String>> g) {
    Map<String, Integer> index = new HashMap<>();
    Map<String, Integer> low = new HashMap<>();
    Deque<String> stack = new ArrayDeque<>();
    Set<String> onStack = new HashSet<>();
    Set<String> result = new TreeSet<>();
    int[] counter = {0};
    for (String v : g.keySet()) {
      if (!index.containsKey(v)) {
        strongConnect(v, g, index, low, stack, onStack, counter, result);
      }
    }
    return result;
  }

  private static void strongConnect(String v, Map<String, Set<String>> g,
      Map<String, Integer> index, Map<String, Integer> low, Deque<String> stack,
      Set<String> onStack, int[] counter, Set<String> result) {
    index.put(v, counter[0]);
    low.put(v, counter[0]++);
    stack.push(v);
    onStack.add(v);
    for (String w : g.getOrDefault(v, Collections.emptySet())) {
      if (!index.containsKey(w)) {
        strongConnect(w, g, index, low, stack, onStack, counter, result);
        low.put(v, Math.min(low.get(v), low.get(w)));
      } else if (onStack.contains(w)) {
        low.put(v, Math.min(low.get(v), index.get(w)));
      }
    }
    if (low.get(v).equals(index.get(v))) {
      List<String> component = new ArrayList<>();
      String w;
      do {
        w = stack.pop();
        onStack.remove(w);
        component.add(w);
      } while (!w.equals(v));
      if (component.size() > 1) {
        result.addAll(component);
      }
    }
  }
}
