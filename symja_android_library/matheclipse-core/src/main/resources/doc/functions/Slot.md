## Slot 

```
#
```

> is a short-hand for `#1`.

```
#n
```

> represents the `n`-th argument of a pure function.
 
```
#0
```

> represents the pure function itself.
        
See:  
* [Wikipedia - Pure function](https://en.wikipedia.org/wiki/Pure_function)
  
### Examples

Unused arguments are simply ignored:

``` 
>> {#1, #2, #3}&[1, 2, 3, 4, 5]
{1,2,3}
```

Recursive pure functions can be written using `#0`:

``` 
>> If(#1<=1, 1, #1 #0(#1-1))& [10]
3628800

>> # // InputForm
#1
     
>> #0 // InputForm
#0
     
>> f := # ^ 2 &
>> f(3)
9

>> #^3& /@ {1, 2, 3}
{1,8,27}

>> #1+#2&[4, 5]
9
```

### Related terms 
[Function](Function.md), [SlotSequence](SlotSequence.md) 