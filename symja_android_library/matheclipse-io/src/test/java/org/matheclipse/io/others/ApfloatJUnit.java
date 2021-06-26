package org.matheclipse.io.others;

import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.io.system.AbstractTestCase;
import org.matheclipse.parser.client.FEConfig;

public class ApfloatJUnit extends AbstractTestCase {

  public ApfloatJUnit() {
    super("ApfloatTest");
  }

  public void testDegree() {
    // 0.01745329251994329576923690768488612713442871888541725456097191440171009114603449443682241569634509482
    checkNumeric(
        "N(Degree, 100)", //
        "0.01745329251994329576923690768488612713442871888541725456097191440171009114603449443682241569634509482");
  }

  public void testE() {
    // 2.718281828459045235360287471352662497757247093699959574966967627724076630353547594571382178525166427
    checkNumeric(
        "N(E, 100)", //
        "2.718281828459045235360287471352662497757247093699959574966967627724076630353547594571382178525166427");

    checkNumeric(
        "N(E, 16)", //
        "2.718281828459045");
    checkNumeric(
        "N(E, 17)", //
        "2.7182818284590452");
  }

  public void testGoldenAngle() {
    // 2.399963229728653322231555506633613853124999011058115042935112750731307338239438790779962060660583964
    checkNumeric(
        "N(GoldenAngle, 100)", //
        "2.399963229728653322231555506633613853124999011058115042935112750731307338239438790779962060660583963");
  }

  public void testGoldenRatio() {
    // 1.618033988749894848204586834365638117720309179805762862135448622705260462818902449707207204189391137
    checkNumeric(
        "N(GoldenRatio, 100)", //
        "1.618033988749894848204586834365638117720309179805762862135448622705260462818902449707207204189391137");
  }

  public void testPi() {
    // 3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117068
    checkNumeric(
        "N(Pi, 100)", //
        "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067");
  }

  public void testApfloatDivide() {
    EvalEngine.setApfloat(new FixedPrecisionApfloatHelper(30));
    IComplexNum cnum1 = F.complexNum(new Apfloat(Long.MIN_VALUE), new Apfloat(Long.MAX_VALUE));
    INum cnum2 = F.num(new Apfloat(Long.MIN_VALUE));
    IExpr value = S.Divide.of(cnum2, cnum1);
    assertEquals(
        value.toString(), //
        "(5.00000000000000000054210108624*10^-1, 4.99999999999999999999999999999*10^-1)");
  }
}
