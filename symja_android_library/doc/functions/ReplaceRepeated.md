## ReplaceRepeated

```
ReplaceRepeated(expr, lhs -> rhs)

expr //. lhs -> rhs
```

or

```
ReplaceRepeated(expr, lhs :> rhs)

expr //. lhs :> rhs
```

> repeatedly applies the rule `lhs -> rhs` to `expr` until  the result no longer changes. 
 
### Examples

```
>> a+b+c //. c->d
a+b+d
```

Simplification of logarithms:

```
>> logrules = {Log(x_ * y_) :> Log(x) + Log(y), Log(x_^y_) :> y * Log(x)};

>> Log(a * (b * c) ^ d ^ e * f) //. logrules
Log(a)+d^e*(Log(b)+Log(c))+Log(f) 
```

`ReplaceAll` just performs a single replacement:

```
>> Log(a * (b * c) ^ d ^ e * f) /. logrules
Log(a)+Log((b*c)^d^e*f) 
```

### Related terms 
[Dispatch](Dispatch.md), [Replace](Replace.md), [ReplaceAll](ReplaceAll.md), [ReplaceList](ReplaceList.md), [ReplacePart](ReplacePart.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of ReplaceRepeated](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L6138) 
