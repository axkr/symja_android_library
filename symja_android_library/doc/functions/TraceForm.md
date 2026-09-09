## TraceForm

```
TraceForm(expr)
```

> evaluate `expr` and return the steps which lead to the result, as a hierarchy: a step which was caused by another step is shown below it.

```
TraceForm(expr, maxDepth)
```

> show the steps nested at most `maxDepth` deep, counting the steps a reader is shown rather than the ones which were recorded. The default is `5`; `Infinity` shows every level.

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

An integration reads as a derivation: each integral is rewritten until nothing is left to integrate, and an integral that a step created is shown under it. The sentence says what the rule does in general, taken from the rule set's own description of it:

```
>> TraceForm(Integrate(Sin(x)/x^3,x))
-Cos(x)/(2*x)-Sin(x)/(2*x^2)-SinIntegral(x)/2
Rubi integration rule 3790. If LtQ[m,-1], rewrite Integrate((c+d*x)^m*Sin(e+f*x),x) as ...
Integrate(Sin(x)/x^3,x) -> Integrate(Cos(x)/x^2,x)/2-Sin(x)/(2*x^2)
  Rubi integration rule 3790. If LtQ[m,-1], rewrite ...
  Integrate(Cos(x)/x^2,x) -> -Cos(x)/x+Integrate(-Sin(x)/x,x)
    Move the constant factor -1 out of the integral.
    Integrate(-Sin(x)/x,x) -> -Integrate(Sin(x)/x,x)
      Rubi integration rule 3792. If EqQ[d e-c f,0], rewrite Integrate(Sin(e+f*x)/(c+d*x),x) as SinIntegral(e+f*x)/d.
      Integrate(Sin(x)/x,x) -> SinIntegral(x)
```

Where a rule substitutes, the substituted variable is given a name of its own and the step says what it stands for. The rule set reuses the integration variable as its dummy, which is true only while the replacement is written beside it:

```
>> TraceForm(Integrate(Cos(x)*Sin(x)^2,x))
Sin(x)^3/3
Rubi integration rule 3056. If IntegerQ[(n-1)/(2)]&&..., rewrite Integrate(Cos(e+f*x)^n*(a*Sin(e+f*x))^m,x) as subst(Integrate(x^m*(1-x^2/a^2)^(1/2*(-1+n)),x),x,a*Sin(e+f*x))/(a*f).
Integrate(Cos(x)*Sin(x)^2,x) -> Integrate(u^2,u)
  Substituting u = Sin(x).
  Apply the power rule $\int x^n\,dx = \frac{x^{n+1}}{n+1}$ for $n \neq -1$, with n = 2.
  Integrate(u^2,u) -> u^(1+2)/(1+2)
```

An integral that Symja answers with an algorithm of its own is worked through too:

```
>> TraceForm(Integrate((x^2+x+1)/(x^4+x^3+x+1),x))
-1/(3*(1+x))+4/3*ArcTan((2*(-1/2+x))/Sqrt(3))/Sqrt(3)
Split the denominator into the part with repeated factors, x+1, and the square-free part x^3+1.
x^4+x^3+x+1 -> (x+1)*(x^3+1)
By the Horowitz-Ostrogradsky reduction the rational part of the answer is -1/(3*(x+1)), and what is left to integrate has a square-free denominator.
Integrate((x^2+x+1)/(x^4+x^3+x+1),x) -> -1/(3*(x+1))+Integrate((2/3*x+2/3)/(x^3+1),x)
Factor the square-free denominator over the rationals - x^3+1 becomes (x+1)*(x^2-x+1).
x^3+1 -> (x+1)*(x^2-x+1)
The quadratic factor x^2-x+1 has no real root, so completing the square gives an arc tangent.
Integrate(2/(3*(x^2-x+1)),x) -> 2/3*ArcTan((-1/2+x)/Sqrt(3/4))/Sqrt(3/4)
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
* A step records the right-hand side of the rule that fired, before it is evaluated, so it is written in the rule set's own helpers. Those are rewritten into the mathematics they stand for before a step is shown: `Simp[u,x]` is `u`, `Dist[u,v,x]` is the product `u*v`, `Subst[u,x,v]` is the replacement `u /. x -> v`, and `§sin` is the inert `Sin` the rules match on. The arithmetic of the rewrite is worked out at the same time, while every integral is left standing - which is what makes a step an intermediate rather than the answer.
* A rule which only rewrites an expression into the rule set's own spelling changes nothing once that is undone, so it is not shown. `maxDepth` counts the steps which are left, not the ones which were recorded, so asking for three levels gives three levels to read.
* A rule which substitutes records the new variable under the name of the old one. Since the step says the substitution in words rather than writing the replacement beside the integral, the new variable is renamed - `u`, or the next free letter - and every step below it is written in that name.
* What each integration rule does comes from Rubi's own `ShowSteps` spelling of its rule set, read out by `ConvertRubiShowSteps` in the `tools` module into `rubi/rubi_steps.tsv.gz`. About 7050 of the 7300 rules carry one; the remaining rules are plumbing which Rubi itself does not show as a step, and they are named by their rule number alone. The table is read the first time an integration step is described, so an evaluation which shows none never touches it.
* The general shape a rule matches and rewrites to is written with the rule's own pattern names, not with the expression at hand - the step itself carries that.
* Repeated sub-expressions of one integral are answered from the Rubi result cache and produce no steps of their own the second time.
* `Config.USER_STEPS_PARSER` makes the parser keep `Divide` and `Subtract` the way they were typed, so the steps read like the input rather than like its normal form.

### Related terms
[TraceDialog](TraceDialog.md), [Trace](Trace.md), [Stack](Stack.md), [StackBegin](StackBegin.md), [TeXForm](TeXForm.md), [MathMLForm](MathMLForm.md), [StackBegin](StackBegin.md), [TeXForm](TeXForm.md), [MathMLForm](MathMLForm.md)

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of TraceForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java)
