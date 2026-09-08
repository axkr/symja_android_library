## TraceForm

```
TraceForm(expr)
```

> evaluate `expr` and return the steps which lead to the result, as a hierarchy: a step which was caused by another step is shown below it.

```
TraceForm(expr, maxDepth)
```

> show the steps nested at most `maxDepth` deep. The default is `3`; `Infinity` shows every level.

```
TraceForm(expr, maxDepth, level)
```

> show steps down to the given level of detail.

The level says how fine grained the steps are:

* `"Rule"` (the default) - the rule which was applied, such as the chain rule of `D` or a rule of the Rubi integration rule set.
* `"Algebra"` - additionally the algebraic reshaping below the rules, such as cancelling a common factor.
* `"Arithmetic"` - additionally the single arithmetic operations, such as squaring a coefficient while the quadratic formula is worked through.

A level may also be written as an integer `1`, `2` or `3`, and `"None"` records nothing.

`TraceForm` is a display wrapper like `TableForm`: it evaluates to

```
TraceForm(HoldForm(result), {step, ...})
```

which stays as it is in the expression tree, so it can be printed by `TeXForm`, `MathMLForm` or `OutputForm`, and taken apart with `Part` or `Cases`. One step is

```
{HoldForm(input), HoldForm(result), {headSymbol, "RuleKey", hintArg...}, {subStep...}}
```

where `headSymbol::RuleKey` names the sentence which explains the step. Both the input and the result of a step are wrapped in `HoldForm`, so taking the tree apart never evaluates them again.

Unlike [Trace](Trace.md), which reports every turn of the evaluation loop, `TraceForm` reports only the steps a built-in function or the pattern matcher announced as meaningful, and nests them the way the derivation runs.

### Examples

```
>> TraceForm(D(Sin(x^2),x))
2*x*Cos(x^2)
If $h(x) = f(g(x))$ apply the chain rule $h'(x) = f'(g(x)) \cdot g'(x)$.
D(Sin(x^2),x) -> D(x^2,x)*Sin'(x^2)
  The derivative of $a \cdot x^n$ is $n \cdot a \cdot x^{(n-1)}$.
  D(x^2,x) -> 2*x^1*D(x,x)
    Apply the identity rule $\frac{d}{dx} x = 1$ - expression D(x,x) is rewritten as 1.
    D(x,x) -> 1
```

Only the first level of steps:

```
>> TraceForm(D(Sin(x^2),x), 1)[[2,1,3,2]]
ChainRule
```

The rule which was applied, and the number it has in the Rubi rule set:

```
>> TraceForm(Integrate(Sin(x)^3,x))[[2,1,3,2]]
RubiRule
```

An integration step says what the rule it applied does in general, taken from the rule set's own description of it:

```
>> TraceForm(Integrate(Sin(x)^3,x), 2)
-Cos(x)+Cos(x)^3/3
Apply integration rule 3054 of the Rubi rule set - Integrate(Sin(x)^3,x) becomes Integrate(Rubi`deactivatetrig(Sin(x)^3,x),x).
Integrate(Sin(x)^3,x) -> Integrate(Rubi`deactivatetrig(Sin(x)^3,x),x)
Rubi integration rule 3125. If IGtQ[(n-1)/(2),0], rewrite Integrate(Sin(c+d*x)^n,x) as -1/d*subst(Integrate(Expand((1-x^2)^(1/2*(-1+n)),x),x),x,Cos(c+d*x)).
Integrate(§sin(x)^3,x) -> ...
```

Every arithmetic operation of the quadratic formula:

```
>> Length(TraceForm(QuarticSolve(1,-4,-3), Infinity, "Arithmetic")[[2]])
10
```

As TeX, one row per step, indented by how deep the step is nested:

```
>> TeXForm(TraceForm(D(x^2,x)))
```

### Notes

* Steps are collected only in a build with `ToggleFeature.SHOW_STEPS` switched on. With it off `TraceForm(expr)` evaluates `expr`, reports that steps are switched off and returns `TraceForm(HoldForm(result), {})`. The switch is `final`, so nothing of the machinery costs anything at run time in a build which does not want it.
* The helper functions the Rubi integration rules are built from are implementation detail and are not shown as steps; the integration rules themselves are.
* What each integration rule does comes from Rubi's own `ShowSteps` spelling of its rule set, read out by `ConvertRubiShowSteps` in the `tools` module into `rubi/rubi_steps.tsv.gz`. About 7050 of the 7300 rules carry one; the remaining rules are plumbing which Rubi itself does not show as a step, and they are named by their rule number alone. The table is read the first time an integration step is described, so an evaluation which shows none never touches it.
* The general shape a rule matches and rewrites to is written with the rule's own pattern names, not with the expression at hand - the step itself carries that.
* Repeated sub-expressions of one integral are answered from the Rubi result cache and produce no steps of their own the second time.
* `Config.USER_STEPS_PARSER` makes the parser keep `Divide` and `Subtract` the way they were typed, so the steps read like the input rather than like its normal form.

### Related terms
[Trace](Trace.md), [Stack](Stack.md), [StackBegin](StackBegin.md), [TeXForm](TeXForm.md), [MathMLForm](MathMLForm.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TraceForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java)
