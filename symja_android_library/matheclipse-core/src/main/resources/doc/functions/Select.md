## Select

```
Select({e1, e2, ...}, head)
```

> returns a list of the elements `ei` for which `head(ei)` returns `True`.

```
Select({e1, e2, ...}, head, n)
```

> returns a list of the first `n` elements `ei` for which `head(ei)` returns `True`.

### Examples

Find numbers greater than zero:

```
>> Select({-3, 0, 1, 3, a}, #>0&)
{1,3}
```

Find the first number that is greater than zero in a list:
 
```
>> Select({-3, 0, 10, 3, a}, #>0&, 1)
{10}
```

`Select` works on an expression with any head:

```
>> Select(f(a, 2, 3), NumberQ)
f(2,3)
```

Nonatomic expression expected.

```
>> Select(a, True) 
Select(a,True)
```

Select all `Listable` system function names.

```
>> Select(Names("System`*"), MemberQ(Attributes(#), Listable) &)

```

### Related terms 
[Cases](Cases.md), [Pick](Pick.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Select](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L6675) 
