package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** <code>Enclose</code>, the <code>Confirm</code> functions and <code>Failure</code> properties. */
public class ConfirmFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testConfirmPassesValues() {
    check("Enclose(Confirm(7)+1)", //
        "8");
    check("Enclose(ConfirmBy(12, EvenQ)/4)", //
        "3");
    check("Enclose(ConfirmMatch({3,u}, {_Integer, _Symbol}))", //
        "{3,u}");
    check("Enclose(ConfirmAssert(5>2); ok)", //
        "ok");
    check("Enclose(ConfirmQuiet(2+3))", //
        "5");
  }

  @Test
  public void testConfirmFailures() {
    check("Enclose(Confirm($Failed); never)", //
        "Failure(ConfirmationFailed,<|MessageTemplate->`` encountered.,MessageParameters:>{$Failed},ConfirmationType->Confirm,Expression:>$Failed,Information->Null|>)");
    check("Enclose(ConfirmBy(9, EvenQ))", //
        "Failure(ConfirmationFailed,<|MessageTemplate->``[``] did not return True.,MessageParameters:>{EvenQ,\n9},ConfirmationType->ConfirmBy,Expression:>9,Information->Null,Function->EvenQ|>)");
    check("Enclose(ConfirmMatch(v, _Integer))", //
        "Failure(ConfirmationFailed,<|MessageTemplate->`` does not match ``.,MessageParameters:>{v,_Integer},ConfirmationType->ConfirmMatch,Expression:>v,Information->Null,Pattern->_Integer|>)");
    check("Enclose(ConfirmAssert(1>3))", //
        "Failure(ConfirmationFailed,<|MessageTemplate->Assertion `` failed.,MessageParameters:>{\n1>3},ConfirmationType->ConfirmAssert,Expression:>False,Information->Null,Test:>False,HeldTest->Hold(\n1>3)|>)");
    check("Enclose(ConfirmQuiet(1/0); never)", //
        "Failure(ConfirmationFailed,<|MessageTemplate->`` produced a message on evaluation.,MessageParameters:>{1/\n0},ConfirmationType->ConfirmQuiet,Expression:>Hold(1/0),Information->Null,HeldExpression->Hold(1/\n0),HeldMessageName->Hold(Power::infy)|>)");
    check("Enclose(Confirm(Missing(\"Gone\"), \"lookup failed\"))", //
        "Failure(ConfirmationFailed,<|MessageTemplate->lookup failed,MessageParameters->{},ConfirmationType->Confirm,Expression:>Missing(Gone),Information->lookup failed|>)");
  }

  @Test
  public void testFailureProperties() {
    check("Enclose(ConfirmBy(9, EvenQ), \"Message\")", //
        "EvenQ[9] did not return True.");
    check("Enclose(ConfirmMatch(v, _Integer), \"Pattern\")", //
        "_Integer");
    check("Enclose(ConfirmAssert(1>3), #(\"Tag\")&)", //
        "ConfirmationFailed");
    check("f=Enclose(Confirm($Failed)); {f(\"ConfirmationType\"), f(\"Expression\"), f(\"unknownKey\")}", //
        "{Confirm,$Failed,Missing(NotAvailable,unknownKey)}");
    check("Enclose(Confirm($Aborted), \"Properties\")", //
        "Failure(ConfirmationFailed,<|MessageTemplate->`` encountered.,MessageParameters:>{$Aborted},ConfirmationType->Confirm,Expression:>$Aborted,Information->Null|>)");
  }

  @Test
  public void testTags() {
    check("Enclose(Enclose(Confirm($Failed, \"i\", t1), \"Tag\"), \"Expression\", t1)", //
        "$Failed");
    check("Enclose(Confirm(Failure(\"x\", <||>), \"j\", t2), \"Information\", t2)", //
        "j");
    // Catch does not intercept a failed confirmation
    check("Enclose(Catch(Confirm($Failed)), \"ConfirmationType\")", //
        "Confirm");
  }

  @Test
  public void testWithoutEnclose() {
    check("Confirm($Failed)", //
        "Failure(confirmnotag,<|MessageTemplate:>Confirm::confirmnotag,MessageParameters:>{Confirm},HeldInput->Hold(Confirm($Failed))|>)");
    check("Confirm(5)", //
        "Failure(confirmnotag,<|MessageTemplate:>Confirm::confirmnotag,MessageParameters:>{Confirm},HeldInput->Hold(Confirm(\n5))|>)");
  }
}
