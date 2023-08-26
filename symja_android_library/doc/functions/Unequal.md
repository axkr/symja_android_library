## Unequal

```
Unequal(x, y) 

x != y
```

> yields `False` if `x` and `y` are known to be equal, or `True` if `x` and `y` are known to be unequal.

```
lhs != rhs
```

> represents the inequality  `lhs <> rhs`.
 
See
* [Wikipedia - Computer algebra - Equality](https://en.wikipedia.org/wiki/Computer_algebra#Equality)

### Examples
 
```
>> 1 != 1
False
```

Lists are compared based on their elements:

```
>> {1} != {2}
True
 
>> {1, 2} != {1, 2}
False
 
>> {a} != {a}
False
 
>> "a" != "b"
True
 
>> "a" != "a"
False
 
>> Pi != N(Pi)
False
 
>> a_ != b_
a_ != b_
 
>> a != a != a
False
 
>> "abc" != "def" != "abc"
False

>> a != a != b
False

>> a != b != a
a != b != a

>> {Unequal(), Unequal(x), Unequal(1)}
{True, True, True}
```

### Related terms
[Equal](Equal.md), [SameQ](SameQ.md) , [UnsameQ](UnsameQ.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Unequal](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L4650) 
