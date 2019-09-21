## FindEulerianCycle

```
 FindEulerianCycle(graph)
```

> find an eulerian cycle in the `graph`.
 
### Examples

```
>> FindEulerianCycle(Graph({1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1}))
{4->1,1->2,2->3,3->4}
```

```
>> FindEulerianCycle(Graph({1 -> 2, 2 -> 3, 3 -> 4, 3 -> 1}))
{}
```