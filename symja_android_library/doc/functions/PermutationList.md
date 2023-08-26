## PermutationList

```
PermutationList(Cycles({{...},{...}, ...}))
```

> get the permutation list representation from the `Cycles({{...},{...}, ...})` expression.
 
See 
* [Wikipedia - Permutation](https://en.wikipedia.org/wiki/Permutation)
	 
### Examples

```
>> PermutationList(Cycles({{3, 2}, { 6, 7},{11,17}})) 
{1,3,2,4,5,7,6,8,9,10,17,12,13,14,15,16,11}
```

### Related terms 
[Cycles](Cycles.md), [FindPermutation](FindPermutation.md), [PermutationCycles](PermutationCycles.md), [PermutationCyclesQ](PermutationCyclesQ.md), [PermutationListQ](PermutationListQ.md), [PermutationReplace](PermutationReplace.md), [Permutations](Permutations.md), [Permute](Permute.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PermutationList](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L1809) 
