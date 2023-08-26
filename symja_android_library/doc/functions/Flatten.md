## Flatten

```
Flatten(expr)
```

> flattens out nested lists in `expr`.
	
```
Flatten(expr, n)
```

> stops flattening at level `n`.
	
```
Flatten(expr, n, h)
```

> flattens expressions with head `h` instead of 'List'.

### Examples

```
>> Flatten({{a, b}, {c, {d}, e}, {f, {g, h}}})
{a, b, c, d, e, f, g, h}
>> Flatten({{a, b}, {c, {e}, e}, {f, {g, h}}}, 1)
{a, b, c, {e}, e, f, {g, h}}
>> Flatten(f(a, f(b, f(c, d)), e), Infinity, f)
f(a, b, c, d, e)
>> Flatten({{a, b}, {c, d}}, {{2}, {1}})
{{a, c}, {b, d}}
>> Flatten({{a, b}, {c, d}}, {{1, 2}})
{a, b, c, d}
```

Flatten also works in irregularly shaped arrays

```
>> Flatten({{1, 2, 3}, {4}, {6, 7}, {8, 9, 10}}, {{2}, {1}})
{{1, 4, 6, 8}, {2, 7, 9}, {3, 10}}

>> Flatten({{{111, 112, 113}, {121, 122}}, {{211, 212}, {221, 222, 223}}}, {{3}, {1}, {2}})
{{{111, 121}, {211, 221}}, {{112, 122}, {212, 222}}, {{113}, {223}}}

>> Flatten({{{1, 2, 3}, {4, 5}}, {{6, 7}, {8, 9,  10}}}, {{3}, {1}, {2}})
{{{1, 4}, {6, 8}}, {{2, 5}, {7, 9}}, {{3}, {10}}}

>> Flatten({{{1, 2, 3}, {4, 5}}, {{6, 7}, {8, 9, 10}}}, {{2}, {1, 3}})
{{1, 2, 3, 6, 7}, {4, 5, 8, 9, 10}}

>> Flatten({{1, 2}, {3,4}}, {1, 2})
{1, 2, 3, 4}
```

Levels to be flattened together in {{-1, 2}} should be lists of positive integers.

```
>> Flatten({{1, 2}, {3, 4}}, {{-1, 2}})
Flatten({{1, 2}, {3, 4}}, {{-1, 2}}, List)
```

Level 2 specified in {{1}, {2}} exceeds the levels, 1, which can be flattened together in {a, b}.

```
>> Flatten({a, b}, {{1}, {2}})
Flatten({a, b}, {{1}, {2}}, List)
```

Check `n` completion

```
>> m = {{{1, 2}, {3}}, {{4}, {5, 6}}}
>> Flatten(m, {2})
{{{1, 2}, {4}}, {{3}, {5, 6}}}
>> Flatten(m, {{2}})
{{{1, 2}, {4}}, {{3}, {5, 6}}}
>> Flatten(m, {{2}, {1}})
{{{1, 2}, {4}}, {{3}, {5, 6}}}
>> Flatten(m, {{2}, {1}, {3}})
{{{1, 2}, {4}}, {{3}, {5, 6}}}
```

Level 4 specified in {{2}, {1}, {3}, {4}} exceeds the levels, 3, which can be flattened together in {{{1, 2}, {3}}, {{4}, {5, 6}}}.

```
>> Flatten(m, {{2}, {1}, {3}, {4}})
Flatten({{{1, 2}, {3}}, {{4}, {5, 6}}}, {{2}, {1}, {3}, {4}}, List)
 
>> m{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};

>> Flatten(m, {1})
{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}

>> Flatten(m, {2})
{{1, 4, 7}, {2, 5, 8}, {3, 6, 9}}
```

 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Flatten](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StructureFunctions.java#L485) 
