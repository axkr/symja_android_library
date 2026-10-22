package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Experimental`ValueFunction(s) = f</code>: <code>f(s, value)</code> is called for every new
 * value of <code>s</code> and <code>f(s)</code> when the value is cleared (issue #1566).
 */
public class ValueFunctionTest extends ExprEvaluatorTestCase {

  /** Track <code>sym</code>: every call of its value function is appended to <code>lg</code>. */
  private void track(String sym) {
    check("ClearAll(" + sym + "); lg = {}; Experimental`ValueFunction(Unevaluated(" + sym
        + ")) = Function(Null, AppendTo(lg, HoldForm({##})), HoldAll);", //
        "");
  }

  @Test
  public void testAssignments() {
    track("v");
    check("v = 4; v := 1 + 2; lg", //
        "{{v,4},{v,1+2}}");
    check("lg = {}; v = 3; v++; ++v; v--; v += 10; v -= 2; v *= 3; v /= 4; ApplyTo(v, g); lg", //
        "{{v,3},{v,4},{v,5},{v,4},{v,14},{v,12},{v,36},{v,9},{v,g(9)}}");
    check("lg = {}; v = {5, 6}; v[[2]] = 8; v[[1]] -= 1; v[[2]] *= 2; AppendTo(v, 0); PrependTo(v, -1); lg", //
        "{{v,{5,6}},{v,{5,8}},{v,{4,8}},{v,{4,16}},{v,{4,16,0}},{v,{-1,4,16,0}}}");
    check("lg = {}; v = <|k -> 1|>; AssociateTo(v, m -> 2); v[\"n\"] = 3; KeyDropFrom(v, k); lg", //
        "{{v,<|k->1|>},{v,<|k->1,m->2|>},{v,<|k->1,m->2,n->3|>},{v,<|m->2,n->3|>}}");
  }

  @Test
  public void testClear() {
    track("v");
    check("v = 2; v =.; Clear(v); lg", //
        "{{v,2},{v}}");
    // ClearAll removes the value, but not the function which is told about it
    check("lg = {}; v = 7; ClearAll(v); v = 8; lg", //
        "{{v,7},{v},{v,8}}");
  }

  @Test
  public void testScopes() {
    track("v");
    check("v = 1; lg = {}; Block({v}, v = 5); lg", //
        "{{v},{v,5},{v,1}}");
    check("lg = {}; Block({v = 2}, v); lg", //
        "{{v,2},{v,1}}");
    check("lg = {}; Do(Null, {v, 3}); lg", //
        "{{v,1},{v,2},{v,3},{v,1}}");
    check("lg = {}; Table(v, {v, {a, b}}); lg", //
        "{{v,a},{v,b},{v,1}}");
    check("lg = {}; Sum(v, {v, 0.5, 1.0, 0.5}); lg", //
        "{{v,0.5},{v,1.0},{v,1}}");
    // an iteration which never starts changes nothing
    check("lg = {}; Table(v, {v, 0}); Do(Null, {v, 4, 3}); lg", //
        "{}");
    // the local of a Module is another symbol
    check("lg = {}; Module({v}, v = 9); lg", //
        "{}");
  }

  @Test
  public void testFailingValueFunction() {
    check("ClearAll(r, s); r = 1; s = 2; "
        + "Experimental`ValueFunction(Unevaluated(r)) = Function({y, x}, Throw(x), HoldFirst);", //
        "");
    // the exception of the Block body comes first, and every variable is restored
    check("Catch(Block({r = 3, s = 4}, r + s))", //
        "3");
    check("{r, s}", //
        "{1,2}");
    check("Catch(Do(Null, {r, 5, 6})); r", //
        "1");
    check("Catch(Table(r, {r, 5, 6})); r", //
        "1");
    // the value is stored before the function is called
    check("{Catch(r = 6), r}", //
        "{6,6}");
  }

  @Test
  public void testRecursion() {
    // a function which corrects the value is not told about its own correction
    check("ClearAll(c); Experimental`ValueFunction(Unevaluated(c)) = "
        + "Function({y, x}, If(x > 10, c = 10), HoldFirst); c = 4; c = 25; c", //
        "10");
    // two symbols which mirror each other
    check("ClearAll(p, q); "
        + "Experimental`ValueFunction(Unevaluated(p)) = Function({y, x}, q = x, HoldFirst); "
        + "Experimental`ValueFunction(Unevaluated(q)) = Function({y, x}, p = x, HoldFirst); "
        + "p = 3; {p, q}", //
        "{3,3}");
    check("q = 4; {p, q}", //
        "{4,4}");
    // a function which changes every value it is told about runs into the iteration limit
    check("ClearAll(inc); Experimental`ValueFunction(Unevaluated(inc)) = "
        + "Function({y, x}, inc = x + 1, HoldFirst);", //
        "");
    check("inc = 1", //
        "Hold(inc=1)");
    check("inc > 100", //
        "True");
  }

  @Test
  public void testGetAndUnset() {
    check("ClearAll(w); Experimental`ValueFunction(Unevaluated(w)) = h", //
        "h");
    check("Experimental`ValueFunction(Unevaluated(w))", //
        "h");
    // the argument is held, so a symbol with a value needs no Unevaluated
    check("w = 3; Experimental`ValueFunction(w)", //
        "h");
    // the symbol of an assignment is not evaluated
    check("Experimental`ValueFunction(w) := h2; Experimental`ValueFunction(Unevaluated(w))", //
        "h2");
    check("Experimental`ValueFunction(Unevaluated(w)) =.", //
        "");
    check("Experimental`ValueFunction(Unevaluated(w))", //
        "ValueFunction(Unevaluated(w))");
    check("Experimental`ValueFunction(Unevaluated(w)) =.", //
        "$Failed");
    check("Attributes(Experimental`ValueFunction)", //
        "{HoldAll,Protected}");
  }

  @Test
  public void testInvalidSymbols() {
    check("Experimental`ValueFunction(2 + k) = h", //
        "$Failed");
    check("Experimental`ValueFunction(k, k2) = h", //
        "$Failed");
    check("Experimental`ValueFunction(k, k2) := h", //
        "$Failed");
    check("Experimental`ValueFunction() =.", //
        "$Failed");
    check("Experimental`ValueFunction(Cos) = h", //
        "$Failed");
    check("ClearAll(pr); SetAttributes(pr, Protected); Experimental`ValueFunction(pr) = h", //
        "$Failed");
    check("Unprotect(pr); ClearAll(pr)", //
        "");
  }

  @Test
  public void testTrackingCallback() {
    // the shape a notebook front end uses: a held symbol, a callback which may fail, and a
    // function which removes itself when the callback failed
    check("ClearAll(t, seen); seen = {}; "
        + "addTracking = Function({symbol, callback}, "
        + "Experimental`ValueFunction(Unevaluated(symbol)) = Function({y, x}, "
        + "If(FailureQ(callback(x)), Experimental`ValueFunction(Unevaluated(symbol)) // Unset)), "
        + "HoldFirst);", //
        "");
    check("t = 1; addTracking(t, If(# === 6, $Failed, AppendTo(seen, #)) &); "
        + "t = 2; t = {3, 4}; t = 6; t = 7; seen", //
        "{2,{3,4}}");
    check("Experimental`ValueFunction(Unevaluated(t))", //
        "ValueFunction(Unevaluated(t))");
    // a cleared value calls the function with one argument
    check("seen = {}; addTracking(t, AppendTo(seen, #) &); Off(Function::fpct); t =.; "
        + "On(Function::fpct); t = 8; seen", //
        "{8}");
    // a proxy symbol local to a Module
    check("Module({sym = {0.5, {0, 0}}, got = {}}, "
        + "Experimental`ValueFunction(Unevaluated(sym)) = Function({y, x}, AppendTo(got, x)); "
        + "sym = {1.5, {1, 2}}; sym[[1]] = 2.5; got)", //
        "{{1.5,{1,2}},{2.5,{1,2}}}");
  }

  @Test
  public void testAssociationChangedInPlace() {
    // the function keeps the associations it is told about; changing the variable in place again
    // must not change them
    check("ClearAll(as, kept); kept = {}; as = <|p -> 1|>; "
        + "Experimental`ValueFunction(Unevaluated(as)) = Function({y, x}, AppendTo(kept, x), HoldFirst); "
        + "AssociateTo(as, q -> 2); AssociateTo(as, r -> 3); kept", //
        "{<|p->1,q->2|>,<|p->1,q->2,r->3|>}");
  }

  @Test
  public void testClearWithFailingFunction() {
    // the function is called when the symbol is cleared completely
    check("ClearAll(u); u(1) = 7; u = 1; "
        + "Experimental`ValueFunction(Unevaluated(u)) = Function(Null, Throw(0)); "
        + "{Catch(Clear(u)), DownValues(u)}", //
        "{0,{}}");
    check("Experimental`ValueFunction(Unevaluated(u)) =.; u(2) = 8; u = 2; "
        + "SetAttributes(u, Listable); "
        + "Experimental`ValueFunction(Unevaluated(u)) = Function(Null, Throw(0)); "
        + "{Catch(ClearAll(u)), DownValues(u), Attributes(u)}", //
        "{0,{},{}}");
  }

  @Test
  public void testCorrectionIsReported() {
    check("ClearAll(cr, told); told = {}; Experimental`ValueFunction(Unevaluated(cr)) = "
        + "Function({y, x}, AppendTo(told, x); If(x > 10, cr = 10), HoldFirst); cr = 25; "
        + "{cr, told}", //
        "{10,{25,10}}");
  }

  @Test
  public void testValueAsStored() {
    // a pure function passes an argument in Unevaluated on as it is
    check("pf = Function({y, x}, Hold(x)); pf(a, Unevaluated(2 + 3))", //
        "Hold(2+3)");
    check("(Hold(#2) &)[a, Unevaluated(2 + 3)]", //
        "Hold(2+3)");
    // a function which does not hold its arguments gets the value unevaluated
    check("ClearAll(dv, told); told = {}; "
        + "Experimental`ValueFunction(Unevaluated(dv)) = Function({y, x}, AppendTo(told, Hold(x))); "
        + "dv := 2 + 3; dv = 4; told", //
        "{Hold(2+3),Hold(4)}");
  }

  @Test
  public void testBlockVariablesTogether() {
    // the function of p sees every variable of the Block local at its start and restored at its
    // end
    check("ClearAll(bp, bq, told); told = {}; bp = 1; bq = 2; "
        + "Experimental`ValueFunction(Unevaluated(bp)) = "
        + "Function(Null, AppendTo(told, {Length(Unevaluated({##})), bq}), HoldAll); "
        + "Block({bp, bq = 5}, Null); told", //
        "{{1,5},{2,2}}");
  }
}
