package org.matheclipse.core.config;

import junit.framework.TestCase;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatMath;
import org.apfloat.spi.FilenameGenerator;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

import java.util.Locale;

public class SystemConfigurationTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        F.initSymbols();
    }

    public void testApfloatStorage() {
        ApfloatContext.getContext().setFilenameGenerator(new FilenameGenerator("default", "0", "default") {
            @Override
            public synchronized String generateFilename() {
                throw new UnsupportedOperationException();
            }
        });

        try {
            Apfloat apfloat = new Apfloat("9".repeat(1_000_000) + "." + "9".repeat(1_000_000));
            apfloat = ApfloatMath.pow(apfloat, new Apfloat("91212312" + ".1231236"));
            ApfloatNum apfloatNum = ApfloatNum.valueOf(apfloat);
            IInteger integerPart = apfloatNum.integerPart();
            System.out.println(integerPart.bitLength());
            fail("Should be fail");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    public void testOverflowError() {
        String expr = "N(1.7*10^1,100)/N(2.5*10^1,100)*N(0,100)";
        ExprEvaluator evaluator = new ExprEvaluator();
        IExpr result = evaluator.eval(expr);
        assertEquals(result.toString(), "0");
    }

    public void testOverflowError02() {
        String expr = "ArcSin(N(1.7*10^1,100)/N(2.5*10^1,100)*N(0,100))";
        ExprEvaluator evaluator = new ExprEvaluator();
        IExpr result = evaluator.eval(expr);
        assertEquals(result.toString(), "0");
    }


    public void testLocale() {
        Locale.setDefault(Locale.forLanguageTag("tr"));
        ExprEvaluator evaluator = new ExprEvaluator();
        String input =  "TestIChar[x_] := Sin[x]; Definition[TestIChar]";
        IExpr result = evaluator.eval(input);
        String str = OutputFormFactory.get(true).toString(result);
        assertEquals(str, "1");
    }
}
