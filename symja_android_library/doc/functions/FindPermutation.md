## FindPermutation

```
FindPermutation(list1, list2)
```

> create a `Cycles({{...},{...}, ...})` permutation expression, for two lists whose arguments are the same but may be differently arranged.
 
See 
* [Wikipedia - Permutation](https://en.wikipedia.org/wiki/Permutation)
	 
### Examples

```
>> FindPermutation(CharacterRange("a","d"),{"a","d","c","b"})
Cycles({{2,4}})
```

### Related terms 
[Cycles](Cycles.md), [FindPermutation](FindPermutation.md), [PermutationCycles](PermutationCycles.md), [PermutationCyclesQ](PermutationCyclesQ.md), [PermutationList](PermutationList.md), [PermutationListQ](PermutationListQ.md), [PermutationReplace](PermutationReplace.md), [Permutations](Permutations.md), [Permute](Permute.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FindPermutation](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L535) 
