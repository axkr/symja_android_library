## Cycles

```
Cycles(a, b)
```

> expression for defining canonical cycles of a permutation.

See:
* [Wikipedia: Cyclic permutation](https://en.wikipedia.org/wiki/Cyclic_permutation)

### Examples

The singletons `{2}` and `{5}` are deleted:

```
>> PermutationCycles({4,2,7,6,5,8,1,3}) 
Cycles({{1,4,6,8,3,7}})
```

### Related terms 
[FindPermutation](FindPermutation.md), [PermutationCycles](PermutationCycles.md), [PermutationCyclesQ](PermutationCyclesQ.md), [PermutationList](PermutationList.md), [PermutationListQ](PermutationListQ.md), [PermutationReplace](PermutationReplace.md), [Permutations](Permutations.md), [Permute](Permute.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Cycles](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L293) 
