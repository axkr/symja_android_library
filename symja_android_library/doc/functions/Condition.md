## Condition

```
Condition(pattern, expr)
```

or

```
pattern /; expr
```

> places an additional constraint on `pattern` that only allows it to match if `expr` evaluates to `True`.
   
### Examples

The controlling expression of a `Condition` can use variables from the pattern:

```
>> f(3) /. f(x_) /; x>0 -> t
t
	 
>> f(-3) /. f(x_) /; x>0 -> t
f(-3)
```

`Condition` can be used in an assignment:

```
>> f(x_) := p(x) /; x>0
>> f(3)
p(3)

>> f(-3)
f(-3)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Condition](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Programming.java#L532) 
