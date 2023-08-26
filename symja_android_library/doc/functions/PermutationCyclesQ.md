## PermutationCyclesQ

```
PermutationCyclesQ(cycles-expression)
```

> if `cycles-expression` is a valid `Cycles({{...},{...}, ...})` expression return `True`.
 
See 
* [Wikipedia - Permutation](https://en.wikipedia.org/wiki/Permutation)
	 
### Examples

```
>> PermutationCyclesQ(Cycles({{1, 6, 2}, {4, 11, 12, 3}}))
True
```

### Related terms 
[Cycles](Cycles.md), [FindPermutation](FindPermutation.md), [PermutationCycles](PermutationCycles.md), [PermutationList](PermutationList.md), [PermutationListQ](PermutationListQ.md), [PermutationReplace](PermutationReplace.md), [Permutations](Permutations.md), [Permute](Permute.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PermutationCyclesQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L1754) 
