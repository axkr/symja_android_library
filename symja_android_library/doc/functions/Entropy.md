## Entropy

```
Entropy(list)
```

> return the base `E` (Shannon) information entropy of the elements in `list`.

```
Entropy(b, list)
```

> return the base `b` (Shannon) information entropy of the elements in `list`.

See:  
* [Wikipedia - Entropy (information theory)](https://en.wikipedia.org/wiki/Entropy_(information_theory))

### Examples

```
>> Entropy({a, b, b}) 
2/3*Log(3/2)+Log(3)/3

>> Entropy({a, b, b,c,c,c,d}) 
3/7*Log(7/3)+2/7*Log(7/2)+2/7*Log(7) 

>> Entropy(b,{a,c,c})
2/3*Log(3/2)/Log(b)+Log(3)/(3*Log(b))
```

### Related terms 
[Commonest](Commonest.md), [Counts](Counts.md), [E](E.md), [Tally](Tally.md)