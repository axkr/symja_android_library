## TraceDialog

```
TraceDialog(expr)
```

> evaluate `expr` one step at a time, stopping at each step so that it can be looked at before the evaluation goes on.

```
TraceDialog(expr, maxDepth, level)
```

> as for [TraceForm](TraceForm.md): how deep the steps may nest, and how fine grained they are.

Where the evaluation is being shown - the browser notebook - it stops at each step and waits. The reader is told which rule was applied and what it rewrote, and can

* take the next step,
* let the evaluation run to the end, which still collects every remaining step,
* stop, which keeps everything worked out up to that point and answers `$Aborted`,
* evaluate an expression of their own, answered where the evaluation stands and without becoming part of the derivation.

Evaluated anywhere else - a script, a test, a `TraceDialog` inside another expression - there is nobody to stop for, so it runs straight through and returns the same derivation as [TraceForm](TraceForm.md).

The result is the same display wrapper `TraceForm(HoldForm(result), {step, ...})`, so it prints and can be taken apart the same way.

### Examples

```
>> TraceDialog(D(Sin(x^2),x))
```

With nobody to stop for, this is `TraceForm(D(Sin(x^2),x))`:

```
>> TraceDialog(D(x^2,x))[[1]]
2*x
```

Step through an integration, showing every level:

```
>> TraceDialog(Integrate(Sin(x)^3,x), Infinity)
```

### Notes

* The evaluation gets an engine of its own rather than the notebook's. A dialog stands still for as long as the reader takes, and the notebook's engine is held by one evaluation at a time - borrowing it would stop every other cell for minutes. Definitions the reader has made are still in force: those belong to the symbols, which one session shares, not to the engine.
* A reader who closes the tab leaves an evaluation waiting. It is not left there: the wait has a deadline, after which the evaluation gives up by itself.
* An expression evaluated from the dialog is evaluated with the step collection switched off, so asking a question never adds to the derivation being read.
* Steps are collected only in a build with `ToggleFeature.SHOW_STEPS` switched on.

### Related terms
[TraceForm](TraceForm.md), [Trace](Trace.md), [Stack](Stack.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TraceDialog](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java)
