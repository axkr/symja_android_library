## SlotSequence

```
##
```

> is the sequence of arguments supplied to a pure function.

```
##n
```

> starts with the `n`-th argument. 
 
 
### Examples

``` 
>> Plus(##)& [1, 2, 3]
5

>> Plus(##2)& [1, 2, 3]
5

>> ## // InputForm
##1
```

`##n` names the arguments from the `n`-th onwards. It is valid up to one past the last argument,
where it stands for no arguments at all:

```
>> ff(##3) &[a, b]
ff()

>> ff(x, ##3, y) &[a, b]
ff(x,y)
```

Beyond that it cannot be filled, and is left in place:

```
>> ff(##4) &[a, b]
ff(##4)
```

### Related terms 
[Function](Function.md), [Slot](Slot.md)