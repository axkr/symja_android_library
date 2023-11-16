package org.matheclipse.core.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.matheclipse.core.expression.F.C10;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 *  
 */
public class Java8TestCase extends ExprEvaluatorTestCase {

  @Test
  public void testForeach() {
    IAST ast = List(C10, a, b, c, d, e);
    IASTAppendable result = F.ListAlloc(ast.argSize());
    ast.forEach(x -> result.append(x));
    assertEquals("{10,a,b,c,d,e}", result.toString());
  }

  @Test
  public void testStream001() {
    IAST ast = List(C10, a, b, c, d, e);
    IASTAppendable result = F.ListAlloc(ast.argSize() + 7);
    // Consumer<IExpr> action = (IExpr x) -> System.out.println(x);
    ast.stream().forEach(x -> result.append(x));
    ast.stream(0, 7).forEach(x -> result.append(x));
    assertEquals("{10,a,b,c,d,e,List,10,a,b,c,d,e}", result.toString());
  }

  @Test
  public void testNumber001() {
    IExpr result = F.Factorial.of(30);
    assertEquals("265252859812191058636308480000000", result.toString());
  }

  @Test
  public void testBoolean001() {
    IExpr result = F.Boole.of(false);
    assertEquals("0", result.toString());
    result = F.Boole.of(true);
    assertEquals("1", result.toString());
  }

  @Test
  public void testTransform() {
    IAST ast = List(C10, F.CND1, F.C1D3, F.CI, a, F.Pi, F.C2Pi, F.CSqrt2);
    IASTAppendable result = ast.stream() //
        .map(IExpr::head) //
        .collect(IASTAppendable.toAST(F.List, ast.argSize()));
    assertEquals("{Integer,Real,Rational,Complex,Symbol,Symbol,Times,Power}", result.toString());

    result = ast.stream0() //
        .map(IExpr::head) //
        .collect(IASTAppendable.toAST(F.ListAlloc(11)));
    assertEquals("{Symbol,Integer,Real,Rational,Complex,Symbol,Symbol,Times,Power}",
        result.toString());
  }

  @Test
  public void testFiltering() {
    IAST ast = (IAST) F.List.of(10, 11, 12, 13, 14, 15, 16, 17, 18, 19);

    // filtering using Predicate
    IASTAppendable resultList = ast.stream() //
        .filter(x -> F.PrimeQ.ofQ(x)) //
        .collect(IASTAppendable.toAST(F.ListAlloc(11)));
    assertEquals("{11,13,17,19}", resultList.toString());

    // count based filtering
    resultList = ast.stream() //
        .skip(2).limit(2) //
        .collect(IASTAppendable.toAST(F.ListAlloc(11)));
    assertEquals("{12,13}", resultList.toString());
  }

  @Test
  public void testSearching() {
    IAST ast = (IAST) F.List.of(10, 11, 12, 13, 14, 15, 16, 17, 18, 19);

    // searching for a element
    java.util.Optional<IExpr> any = ast.stream() //
        .filter(x -> F.PrimeQ.ofQ(x)) //
        .findAny();
    assertEquals("Optional[11]", any.toString());
    // searching for existence
    boolean anyMatch = ast.stream() //
        .anyMatch(x -> F.PrimeQ.ofQ(x));
    assertEquals(true, anyMatch);
  }

  @Test
  public void testReordering() {
    IAST ast = (IAST) F.List.of(10, 11, 12, 13, 14, 15, 16, 17, 18, 19);

    IASTAppendable reversedList = ast.stream() //
        .sorted(Comparator.comparing(IExpr::evalNumber) //
            .reversed()) //
        .collect(IASTAppendable.toAST(F.ListAlloc(11)));
    assertEquals("{19,18,17,16,15,14,13,12,11,10}", reversedList.toString());
  }

  @Test
  public void testStatistics() {
    IAST ast = (IAST) F.List.of(10, 11, 12, 13, 14, 15, 16, 17, 18, 19);

    // calculating sum using reduce terminal operator
    int value = ast.stream() //
        .mapToInt(IExpr::toIntDefault) //
        .reduce(0, (total, currentValue) -> total + currentValue);
    assertEquals(145, value);

    value = ast.stream() //
        .mapToInt(IExpr::toIntDefault) //
        .sum();
    assertEquals(145, value);

    long longValue = ast.stream() //
        .mapToInt(IExpr::toIntDefault) //
        .count();
    assertEquals(10, longValue);

    IntSummaryStatistics ageStatistics = ast.stream() //
        .mapToInt(IExpr::toIntDefault) //
        .summaryStatistics();

    assertEquals(14.5, ageStatistics.getAverage(), Config.MACHINE_EPSILON);
    assertEquals(10, ageStatistics.getCount(), Config.MACHINE_EPSILON);
    assertEquals(19, ageStatistics.getMax(), Config.MACHINE_EPSILON);
    assertEquals(10, ageStatistics.getMin(), Config.MACHINE_EPSILON);
    assertEquals(145, ageStatistics.getSum(), Config.MACHINE_EPSILON);
  }

  @Test
  public void testGrouping() {
    IAST ast = (IAST) F.List.of(10, 11, 12, 13, 14, 15, 16, 17, 18, 19);

    IASTAppendable prototype = F.ListAlloc(10);
    prototype.append(F.C0);
    prototype.append(F.C0);
    prototype.append(F.C0);

    // append numbers grouped by prime to the prototype
    java.util.Map<Boolean, IASTAppendable> exprByPrime = ast.stream().collect(Collectors.groupingBy( //
        F.PrimeQ::ofQ, //
        IASTAppendable.toAST(prototype)));
    assertEquals("{false={0,0,0,10,12,14,15,16,18}, true={0,0,0,11,13,17,19}}",
        exprByPrime.toString());

    exprByPrime = ast.stream() //
        .collect(Collectors.groupingBy( //
            F.PrimeQ::ofQ, //
            Collectors.mapping(x -> x.greater(F.ZZ(15)), //
                IASTAppendable.toAST(F.List, 10))));
    assertEquals("{false={False,False,False,False,True,True}, true={False,False,True,True}}",
        exprByPrime.toString());

    java.util.Map<Boolean, Double> averageInt = ast.stream() //
        .collect(Collectors.groupingBy( //
            F.PrimeQ::ofQ, //
            Collectors.averagingInt(IExpr::toIntDefault) //
        ));
    assertEquals("{false=14.166666666666666, true=15.0}", averageInt.toString());
  }

  public static void testParallel() {
    try {
      // simple example - real word example needs more preparing
      IAST range = (IAST) F.Range.of(1000);

      System.out.println("Normal...");
      long start = System.currentTimeMillis();
      range.stream().forEach(x -> System.out.print(x.toString() + ","));
      long end = System.currentTimeMillis();
      System.out.println();
      System.out.println("Time: " + (end - start));

      System.out.println("Parallel...");
      IAST range2 = (IAST) F.Range.of(1000);
      long start2 = System.currentTimeMillis();
      range2.stream().parallel().forEach(x -> System.out.print(x.toString() + ","));
      long end2 = System.currentTimeMillis();
      System.out.println();
      System.out.println("Time: " + (end2 - start2));
    } catch (RuntimeException rex) {
      fail();
    }
  }
}
