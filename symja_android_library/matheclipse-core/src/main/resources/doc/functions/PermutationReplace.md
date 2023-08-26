## PermutationReplace

```
PermutationReplace(list-or-integer, Cycles({{...},{...}, ...}))
```

> replace the arguments of the first expression with the corresponding element from the `Cycles({{...},{...}, ...})` expression.
 
See 
* [Wikipedia - Permutation](https://en.wikipedia.org/wiki/Permutation)
	 
### Examples

```
>> PermutationReplace({1, b, 3, 4, 5}, Cycles({{1, 5,8}, {2, 7}}))
{5,b,3,4,8}
```

### Related terms 
[Cycles](Cycles.md), [FindPermutation](FindPermutation.md), [PermutationCycles](PermutationCycles.md), [PermutationCyclesQ](PermutationCyclesQ.md), [PermutationList](PermutationList.md), [PermutationListQ](PermutationListQ.md), [Permutations](Permutations.md), [Permute](Permute.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PermutationReplace](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L1968) 
